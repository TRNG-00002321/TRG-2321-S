# Recording User Actions with Playwright Codegen

## Learning Objectives
- Use Playwright codegen to record user interactions
- Generate test code from browser actions
- Configure codegen options for different scenarios
- Select target browser and device emulation during recording
- Refine and improve generated code

## Why This Matters

Playwright codegen accelerates test development:
- Quick test scaffolding
- Accurate locator generation
- Learning new applications
- Prototyping test scenarios
- Reducing manual coding effort

## The Concept

### Playwright Codegen Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    Codegen Workflow                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   1. Launch codegen with target URL                             │
│   2. Browser opens with recording toolbar                       │
│   3. Perform actions in browser                                 │
│   4. Code is generated in real-time                             │
│   5. Copy code to your test file                                │
│   6. Refine and enhance as needed                               │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Basic Codegen Usage

```bash
# Launch codegen with Maven
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="codegen"

# With target URL
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="codegen https://example.com"

# Save to file
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="codegen --output=tests/RecordedTest.java https://example.com"
```

### Codegen Options

```bash
# Browser selection
codegen --browser=chromium https://example.com
codegen --browser=firefox https://example.com
codegen --browser=webkit https://example.com

# Device emulation
codegen --device="iPhone 12" https://example.com
codegen --device="Pixel 5" https://example.com
codegen --device="iPad Pro" https://example.com

# Viewport size
codegen --viewport-size=1920,1080 https://example.com

# Color scheme
codegen --color-scheme=dark https://example.com

# Geolocation
codegen --geolocation="40.7128,-74.0060" https://example.com

# Language/locale
codegen --lang=en-US https://example.com

# Timezone
codegen --timezone="America/New_York" https://example.com

# Save authentication state
codegen --save-storage=auth.json https://example.com

# Load authentication state
codegen --load-storage=auth.json https://example.com
```

### Codegen Interface

When codegen launches:

```
┌─────────────────────────────────────────────────────────────────┐
│  Browser Window                         │  Code Panel           │
│  ───────────────                        │  ──────────           │
│                                         │                       │
│  ┌─────────────────────────────────┐   │  import ...           │
│  │                                 │   │                       │
│  │    Your application under test  │   │  @Test                │
│  │                                 │   │  void test() {        │
│  │    Click, type, navigate       │   │    page.goto(...);    │
│  │    Watch code appear ──────────────►│    page.click(...);   │
│  │                                 │   │    page.fill(...);    │
│  │                                 │   │  }                    │
│  └─────────────────────────────────┘   │                       │
│                                         │                       │
│  [Record] [Pause] [Assert visibility]   │  [Copy] [Clear]       │
│  [Assert text] [Pick locator]           │  [Java ▼]             │
│                                         │                       │
└─────────────────────────────────────────────────────────────────┘
```

### Recording Actions

Codegen captures these actions automatically:

```java
// Navigation
page.navigate("https://example.com/login");

// Clicking
page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
page.locator("#submit").click();

// Typing
page.getByLabel("Email").fill("user@example.com");
page.getByPlaceholder("Search").fill("search term");

// Selection
page.getByLabel("Country").selectOption("USA");

// Checkbox/Radio
page.getByLabel("Remember me").check();
page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Option A")).click();

// File upload
page.getByLabel("Upload file").setInputFiles(Paths.get("file.pdf"));
```

### Adding Assertions During Recording

Use the assertion toolbar buttons:

```java
// Assert visibility (green button)
assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Welcome"))).isVisible();

// Assert text (T button)
assertThat(page.getByTestId("status")).hasText("Success");

// Pick locator (crosshair button)
// Hover over element to see locator suggestions
```

### Generated Code Example

**Recording session:**
1. Navigate to login page
2. Enter credentials
3. Click login
4. Verify dashboard

**Generated code:**

```java
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import java.util.*;

public class RecordedTest {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            
            page.navigate("https://example.com/login");
            page.getByLabel("Email").click();
            page.getByLabel("Email").fill("user@example.com");
            page.getByLabel("Password").click();
            page.getByLabel("Password").fill("password123");
            page.getByRole(AriaRole.BUTTON, 
                new Page.GetByRoleOptions().setName("Sign In")).click();
            
            assertThat(page.getByRole(AriaRole.HEADING, 
                new Page.GetByRoleOptions().setName("Dashboard"))).isVisible();
            
            context.close();
            browser.close();
        }
    }
}
```

### Saving Authentication State

```bash
# Record and save cookies/storage
codegen --save-storage=auth.json https://example.com

# Perform login during recording
# Close codegen
# auth.json now contains session data

# Use saved state in future recordings
codegen --load-storage=auth.json https://example.com
# Already logged in!
```

### Using Saved State in Tests

```java
// Save state after login
BrowserContext context = browser.newContext();
Page page = context.newPage();
page.navigate("https://example.com/login");
page.getByLabel("Email").fill("user@example.com");
page.getByLabel("Password").fill("password");
page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();

// Save storage state
context.storageState(new BrowserContext.StorageStateOptions()
    .setPath(Paths.get("auth.json")));

// Later: Load state to skip login
BrowserContext authenticatedContext = browser.newContext(new Browser.NewContextOptions()
    .setStorageStatePath(Paths.get("auth.json"))
);
Page authenticatedPage = authenticatedContext.newPage();
authenticatedPage.navigate("https://example.com/dashboard");
// Already logged in!
```

### Codegen Best Practices

```
┌─────────────────────────────────────────────────────────────────┐
│              Codegen Best Practices                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  During Recording:                                               │
│  ✓ Use the assertion buttons for verification points            │
│  ✓ Click precisely on elements (not whitespace)                 │
│  ✓ Wait for pages to load before interacting                    │
│  ✓ Record complete user flows                                   │
│                                                                  │
│  After Recording:                                                │
│  ✓ Review and improve locators                                  │
│  ✓ Add more assertions                                          │
│  ✓ Extract to Page Objects                                      │
│  ✓ Parameterize test data                                       │
│  ✓ Add proper test structure (@Test, @BeforeEach)               │
│                                                                  │
│  Avoid:                                                          │
│  ✗ Using codegen output without review                          │
│  ✗ Keeping unnecessary clicks (e.g., clicking empty space)      │
│  ✗ Hardcoded sensitive data                                     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Programmatic Recording

```java
public class ProgrammaticRecording {
    
    public static void main(String[] args) throws Exception {
        // Launch browser with recording
        ProcessBuilder pb = new ProcessBuilder(
            "mvn", "exec:java",
            "-e",
            "-Dexec.mainClass=com.microsoft.playwright.CLI",
            "-Dexec.args=codegen https://example.com"
        );
        pb.inheritIO();
        Process process = pb.start();
        process.waitFor();
    }
}
```

### Multi-Browser Recording

```bash
# Record same session, generate for different browsers
codegen --browser=chromium --output=ChromeTest.java https://example.com
codegen --browser=firefox --output=FirefoxTest.java https://example.com
codegen --browser=webkit --output=SafariTest.java https://example.com
```

## Key Takeaways

1. **`codegen`** launches interactive recording session
2. **Assertion buttons** add verification during recording
3. **Device emulation** available via `--device` flag
4. **Auth state** can be saved/loaded for efficiency
5. **Generated code** should be refined, not used as-is
6. **Locators** generated use best practices (role, testid)

## Additional Resources

- [Playwright Codegen](https://playwright.dev/java/docs/codegen) - Official guide
- [Test Generator](https://playwright.dev/java/docs/codegen-intro) - Introduction
- [Authentication](https://playwright.dev/java/docs/auth) - Auth state management

