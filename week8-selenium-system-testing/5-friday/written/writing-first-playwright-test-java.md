# Writing Your First Playwright Test in Java

## Learning Objectives
- Structure Playwright tests with JUnit 5
- Navigate pages and interact with elements
- Use Playwright's auto-waiting locators
- Apply web-first assertions for reliable testing
- Implement proper test lifecycle management
- Integrate Playwright with JUnit @BeforeAll/@AfterAll

## Why This Matters

Writing your first Playwright test establishes patterns you'll use throughout your test automation journey. Understanding proper structure, locators, and assertions ensures reliable, maintainable tests.

## The Concept

### First Playwright Test Structure

```java
package com.example.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FirstPlaywrightTest {
    
    // Shared across all tests - expensive to create
    static Playwright playwright;
    static Browser browser;
    
    // Fresh for each test - isolated
    BrowserContext context;
    Page page;
    
    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(false)
        );
    }
    
    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }
    
    @BeforeEach
    void createContext() {
        context = browser.newContext();
        page = context.newPage();
    }
    
    @AfterEach
    void closeContext() {
        context.close();
    }
    
    @Test
    void shouldNavigateAndVerifyTitle() {
        // Navigate
        page.navigate("https://example.com");
        
        // Assert
        assertThat(page).hasTitle("Example Domain");
    }
}
```

### Page Navigation

```java
@Test
void navigationExamples() {
    // Basic navigation
    page.navigate("https://example.com");
    
    // Wait for specific load state
    page.navigate("https://example.com", new Page.NavigateOptions()
        .setWaitUntil(WaitUntilState.NETWORKIDLE)
    );
    
    // Navigate with timeout
    page.navigate("https://example.com", new Page.NavigateOptions()
        .setTimeout(30000)
    );
    
    // Go back/forward
    page.goBack();
    page.goForward();
    
    // Reload
    page.reload();
    
    // Get current URL
    String url = page.url();
    
    // Get page title
    String title = page.title();
}
```

### Element Interaction

```java
@Test
void elementInteractionExamples() {
    page.navigate("https://example.com/form");
    
    // Click
    page.locator("#submit-btn").click();
    
    // Double click
    page.locator(".editable").dblclick();
    
    // Right click
    page.locator("#context-menu").click(new Locator.ClickOptions()
        .setButton(MouseButton.RIGHT)
    );
    
    // Fill text input
    page.locator("#username").fill("testuser");
    
    // Type with delay (simulates real typing)
    page.locator("#search").type("search query", new Locator.TypeOptions()
        .setDelay(100)
    );
    
    // Clear input
    page.locator("#email").clear();
    
    // Check/uncheck checkbox
    page.locator("#agree").check();
    page.locator("#newsletter").uncheck();
    
    // Select from dropdown
    page.locator("#country").selectOption("USA");
    page.locator("#country").selectOption(new SelectOption().setLabel("United States"));
    
    // Hover
    page.locator(".menu-item").hover();
    
    // Focus
    page.locator("#input").focus();
    
    // Press key
    page.locator("#search").press("Enter");
    
    // Upload file
    page.locator("#file-input").setInputFiles(Paths.get("file.pdf"));
}
```

### Playwright Locators

```java
@Test
void locatorExamples() {
    page.navigate("https://example.com");
    
    // By CSS selector
    Locator byCSS = page.locator(".class-name");
    Locator byId = page.locator("#element-id");
    Locator byAttribute = page.locator("[data-testid='submit']");
    
    // By text content
    Locator byText = page.locator("text=Click me");
    Locator byExactText = page.locator("text='Exact Text'");
    
    // By role (accessibility)
    Locator byRole = page.getByRole(AriaRole.BUTTON, 
        new Page.GetByRoleOptions().setName("Submit"));
    Locator heading = page.getByRole(AriaRole.HEADING, 
        new Page.GetByRoleOptions().setLevel(1));
    
    // By test ID (recommended)
    Locator byTestId = page.getByTestId("submit-button");
    
    // By placeholder
    Locator byPlaceholder = page.getByPlaceholder("Enter your email");
    
    // By label
    Locator byLabel = page.getByLabel("Username");
    
    // By alt text (images)
    Locator byAlt = page.getByAltText("Company Logo");
    
    // Combining locators
    Locator combined = page.locator("form").locator("button");
    Locator filtered = page.locator("button").filter(
        new Locator.FilterOptions().setHasText("Submit")
    );
    
    // First, last, nth
    Locator first = page.locator(".item").first();
    Locator last = page.locator(".item").last();
    Locator third = page.locator(".item").nth(2);  // 0-indexed
}
```

### Web-First Assertions

```java
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Test
void assertionExamples() {
    page.navigate("https://example.com");
    
    // Page assertions
    assertThat(page).hasTitle("Example Domain");
    assertThat(page).hasTitle(Pattern.compile("Example.*"));
    assertThat(page).hasURL("https://example.com/");
    assertThat(page).hasURL(Pattern.compile(".*example.*"));
    
    // Element visibility
    assertThat(page.locator("#header")).isVisible();
    assertThat(page.locator(".popup")).isHidden();
    
    // Element state
    assertThat(page.locator("#submit")).isEnabled();
    assertThat(page.locator("#submit")).isDisabled();
    assertThat(page.locator("#terms")).isChecked();
    assertThat(page.locator("#newsletter")).not().isChecked();
    
    // Text content
    assertThat(page.locator(".message")).hasText("Success!");
    assertThat(page.locator(".message")).containsText("Succ");
    assertThat(page.locator(".error")).hasText(Pattern.compile("Error.*"));
    
    // Attribute
    assertThat(page.locator("input")).hasAttribute("type", "email");
    assertThat(page.locator("a")).hasAttribute("href", "/about");
    
    // CSS class
    assertThat(page.locator("button")).hasClass("btn-primary");
    assertThat(page.locator("button")).hasClass(Pattern.compile("btn.*"));
    
    // Count
    assertThat(page.locator(".item")).hasCount(5);
    
    // Values
    assertThat(page.locator("#email")).hasValue("test@example.com");
    assertThat(page.locator("#email")).isEmpty();
    
    // Negation
    assertThat(page.locator(".error")).not().isVisible();
}
```

### Auto-Waiting Behavior

Playwright automatically waits for:

```java
@Test
void autoWaitingDemo() {
    page.navigate("https://example.com");
    
    // click() waits for:
    // - Element to be attached to DOM
    // - Element to be visible
    // - Element to be stable (not animating)
    // - Element to be enabled
    // - Element to receive events (not obscured)
    page.locator("#submit").click();
    
    // fill() waits for:
    // - Element to be attached
    // - Element to be visible
    // - Element to be editable
    page.locator("#input").fill("text");
    
    // Assertions auto-retry until timeout:
    // Default timeout is 5 seconds
    assertThat(page.locator(".result")).hasText("Success");
    
    // Custom timeout for assertions
    assertThat(page.locator(".slow-element"))
        .hasText("Loaded", new LocatorAssertions.HasTextOptions()
            .setTimeout(10000));
}
```

### Complete Test Example

```java
package com.example.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginTest {
    
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    
    @BeforeAll
    static void globalSetup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(System.getenv("CI") != null)
        );
    }
    
    @AfterAll
    static void globalTeardown() {
        browser.close();
        playwright.close();
    }
    
    @BeforeEach
    void setup() {
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1920, 1080)
        );
        page = context.newPage();
        page.navigate("https://example.com/login");
    }
    
    @AfterEach
    void teardown() {
        context.close();
    }
    
    @Test
    @DisplayName("Successful login with valid credentials")
    void shouldLoginSuccessfully() {
        // Fill login form
        page.getByLabel("Email").fill("user@example.com");
        page.getByLabel("Password").fill("SecurePass123");
        
        // Submit
        page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Sign In")).click();
        
        // Verify login success
        assertThat(page).hasURL(Pattern.compile(".*dashboard.*"));
        assertThat(page.getByRole(AriaRole.HEADING)).hasText("Welcome");
        assertThat(page.getByTestId("user-menu")).isVisible();
    }
    
    @Test
    @DisplayName("Failed login shows error message")
    void shouldShowErrorForInvalidCredentials() {
        // Fill with invalid credentials
        page.getByLabel("Email").fill("invalid@example.com");
        page.getByLabel("Password").fill("wrongpassword");
        
        // Submit
        page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Sign In")).click();
        
        // Verify error
        assertThat(page.locator(".error-message")).isVisible();
        assertThat(page.locator(".error-message"))
            .containsText("Invalid credentials");
        assertThat(page).hasURL(Pattern.compile(".*login.*"));
    }
    
    @Test
    @DisplayName("Password visibility toggle works")
    void shouldTogglePasswordVisibility() {
        // Enter password
        Locator passwordInput = page.getByLabel("Password");
        passwordInput.fill("mypassword");
        
        // Verify hidden by default
        assertThat(passwordInput).hasAttribute("type", "password");
        
        // Click toggle
        page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Show password")).click();
        
        // Verify visible
        assertThat(passwordInput).hasAttribute("type", "text");
    }
}
```

### Test with Page Object Pattern

```java
// pages/LoginPage.java
public class LoginPage {
    private final Page page;
    
    public LoginPage(Page page) {
        this.page = page;
    }
    
    public void navigate() {
        page.navigate("https://example.com/login");
    }
    
    public void login(String email, String password) {
        page.getByLabel("Email").fill(email);
        page.getByLabel("Password").fill(password);
        page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Sign In")).click();
    }
    
    public Locator errorMessage() {
        return page.locator(".error-message");
    }
}

// tests/LoginTest.java
@Test
void shouldLoginWithPageObject() {
    LoginPage loginPage = new LoginPage(page);
    loginPage.navigate();
    loginPage.login("user@example.com", "password123");
    
    assertThat(page).hasURL(Pattern.compile(".*dashboard.*"));
}
```

## Key Takeaways

1. **JUnit 5 integration** uses @BeforeAll/@AfterAll for browser, @BeforeEach/@AfterEach for context
2. **Locators** auto-wait for elements automatically
3. **Web-first assertions** retry until timeout
4. **getByRole, getByTestId** are preferred locator strategies
5. **Page object pattern** improves maintainability
6. **Context per test** ensures isolation

## Additional Resources

- [Playwright Locators](https://playwright.dev/java/docs/locators) - Locator strategies
- [Playwright Assertions](https://playwright.dev/java/docs/test-assertions) - Assertion reference
- [Best Practices](https://playwright.dev/java/docs/best-practices) - Testing patterns

