# Introduction to Playwright

## Learning Objectives
- Understand what Playwright is and its unique capabilities
- Compare Playwright with Selenium for browser automation
- Explore Playwright's architecture and design principles
- Learn about browser contexts and their benefits
- Identify Playwright advantages: auto-wait, web-first assertions, parallel execution

## Why This Matters

This week's epic emphasizes becoming a polyglot test automation engineer. After mastering Selenium (Java and Python), learning Playwright completes your browser automation toolkit. Playwright represents Microsoft's modern approach to browser automation, offering:

- **Cutting-edge features** not available in Selenium
- **Built-in reliability** with auto-waiting
- **Modern architecture** designed for today's web apps
- **Parallel execution** out of the box

## The Concept

### What is Playwright?

**Playwright** is a modern browser automation library developed by Microsoft. It enables reliable end-to-end testing for web applications across all modern browsers (Chromium, Firefox, WebKit) with a single API.

```
┌─────────────────────────────────────────────────────────────────┐
│                    Playwright Architecture                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Test Code                                                      │
│       │                                                          │
│       ▼                                                          │
│   Playwright API                                                 │
│       │                                                          │
│       ▼                                                          │
│   Browser Connection (WebSocket)                                 │
│       │                                                          │
│       ├──────────────┬──────────────┬──────────────┐            │
│       ▼              ▼              ▼              │            │
│   Chromium       Firefox        WebKit            │            │
│   (Chrome/Edge)  (Firefox)      (Safari)          │            │
│                                                    │            │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │  Browser Context 1    │    Browser Context 2    │        │   │
│   │  ├── Page 1          │    ├── Page 1          │        │   │
│   │  └── Page 2          │    └── Page 2          │        │   │
│   └─────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Playwright vs Selenium

| Feature | Playwright | Selenium |
|---------|------------|----------|
| **Auto-waiting** | Built-in, intelligent | Manual waits needed |
| **Browser support** | Chromium, Firefox, WebKit | Chrome, Firefox, Safari, Edge, IE |
| **Architecture** | Single process, WebSocket | WebDriver protocol, separate process |
| **Parallel execution** | Native browser contexts | Requires Grid/external tools |
| **Network interception** | Built-in | Limited, extension needed |
| **Mobile emulation** | Built-in | Limited |
| **Video recording** | Built-in | External tools |
| **Tracing** | Built-in Trace Viewer | Third-party tools |
| **Language support** | JS, Python, Java, C# | Many languages |
| **Maturity** | Newer (2020) | Mature (2004) |

### Playwright Architecture Advantages

**1. Direct Browser Communication**
```
Selenium:
Test → WebDriver → Browser Driver → Browser
(Multiple hops, potential latency)

Playwright:
Test → Playwright → Browser (WebSocket)
(Direct connection, faster)
```

**2. Browser Contexts**

Browser contexts are isolated browser sessions:

```java
// Create isolated contexts within same browser
Browser browser = playwright.chromium().launch();

// Context 1: User A's session
BrowserContext context1 = browser.newContext();
Page page1 = context1.newPage();
page1.navigate("https://example.com");
// User A is logged in here

// Context 2: User B's session (isolated!)
BrowserContext context2 = browser.newContext();
Page page2 = context2.newPage();
page2.navigate("https://example.com");
// User B is logged in here - completely separate from User A

// No cookie/storage sharing between contexts!
```

**Benefits of Browser Contexts:**
- Parallel test execution without conflicts
- Isolated authentication states
- Clean state without browser restart
- Faster than launching new browsers

### Key Playwright Advantages

#### 1. Auto-Wait

Playwright automatically waits for elements:

```java
// Selenium: Manual waits required
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement button = wait.until(
    ExpectedConditions.elementToBeClickable(By.id("submit"))
);
button.click();

// Playwright: Auto-waits built in
page.locator("#submit").click(); // Automatically waits until clickable
```

**What Playwright waits for automatically:**
- Element to be attached to DOM
- Element to be visible
- Element to be stable (not animating)
- Element to be enabled
- Element to receive events

#### 2. Web-First Assertions

Assertions that automatically retry:

```java
// Selenium: Assert might fail if element not ready
String text = driver.findElement(By.id("status")).getText();
assertEquals("Success", text); // Might fail due to timing

// Playwright: Auto-retrying assertions
assertThat(page.locator("#status")).hasText("Success");
// Retries until timeout or success
```

#### 3. Parallel Execution

```java
// Run tests in parallel with isolated contexts
@Test
void testUser1() {
    BrowserContext context = browser.newContext();
    Page page = context.newPage();
    // Test user 1
}

@Test
void testUser2() {
    BrowserContext context = browser.newContext();
    Page page = context.newPage();
    // Test user 2 - runs in parallel, completely isolated
}
```

#### 4. Network Interception

```java
// Mock API responses
page.route("**/api/users", route -> {
    route.fulfill(new Route.FulfillOptions()
        .setContentType("application/json")
        .setBody("[{\"name\": \"Mock User\"}]")
    );
});

page.navigate("https://example.com");
// App will receive mocked data
```

#### 5. Built-in Features

```java
// Video recording
BrowserContext context = browser.newContext(new Browser.NewContextOptions()
    .setRecordVideoDir(Paths.get("videos/"))
);

// Screenshot
page.screenshot(new Page.ScreenshotOptions()
    .setPath(Paths.get("screenshot.png"))
    .setFullPage(true)
);

// Tracing
context.tracing().start(new Tracing.StartOptions()
    .setScreenshots(true)
    .setSnapshots(true)
);
// ... run test ...
context.tracing().stop(new Tracing.StopOptions()
    .setPath(Paths.get("trace.zip"))
);
```

### Playwright Object Model

```
┌─────────────────────────────────────────────────────────────────┐
│                    Playwright Object Hierarchy                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Playwright                                                     │
│       │                                                          │
│       ├── chromium() ──► Browser                                │
│       ├── firefox()  ──► Browser                                │
│       └── webkit()   ──► Browser                                │
│                              │                                   │
│                              ├── newContext() ──► BrowserContext│
│                              │                         │         │
│                              │                         ├── newPage() ──► Page │
│                              │                         │              │     │
│                              │                         │              │     ├── locator() │
│                              │                         │              │     ├── navigate() │
│                              │                         │              │     └── ... │
│                              │                         │                          │
│                              │                         └── close()                 │
│                              │                                                     │
│                              └── close()                                          │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### When to Choose Playwright

**Choose Playwright when:**
- Building new test automation framework
- Need built-in video/tracing
- Want simplified waiting strategies
- Need parallel execution without Grid
- Testing modern single-page applications
- Need network mocking capabilities

**Choose Selenium when:**
- Existing Selenium infrastructure
- Need specific browser support (IE, legacy)
- Team familiar with Selenium
- Using Selenium-based tools (Appium for mobile)
- Need broader language support

### Quick Start Preview

```java
import com.microsoft.playwright.*;

public class FirstPlaywrightTest {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            
            page.navigate("https://playwright.dev/");
            System.out.println("Title: " + page.title());
            
            // Auto-waiting locator
            page.locator("text=Get started").click();
            
            // Auto-retrying assertion
            assertThat(page).hasURL(Pattern.compile(".*intro"));
        }
    }
}
```

## Key Takeaways

1. **Playwright** is Microsoft's modern browser automation library
2. **Architecture** uses direct WebSocket connection to browsers
3. **Browser Contexts** enable isolated, parallel sessions
4. **Auto-wait** eliminates most explicit waits
5. **Web-first assertions** retry automatically
6. **Built-in features**: video, tracing, network interception

## Additional Resources

- [Playwright Official Documentation](https://playwright.dev/java/) - Java documentation
- [Playwright vs Selenium](https://playwright.dev/docs/selenium-grid) - Official comparison
- [Playwright GitHub](https://github.com/microsoft/playwright-java) - Source and examples

