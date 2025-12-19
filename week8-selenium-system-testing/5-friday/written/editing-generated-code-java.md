# Editing and Refining Generated Code

## Learning Objectives
- Refactor generated test code for maintainability
- Improve locators for stability and readability
- Add meaningful assertions beyond recorded ones
- Convert recorded tests to Page Object Model
- Apply best practices for generated code enhancement

## Why This Matters

Codegen provides a starting point, but production-quality tests require refinement:
- Improve maintainability
- Add proper test structure
- Enhance reliability
- Follow team standards
- Enable code reuse

## The Concept

### Raw Generated Code

**Codegen output:**

```java
import com.microsoft.playwright.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class Example {
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
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
            assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Dashboard"))).isVisible();
            context.close();
            browser.close();
        }
    }
}
```

### Step 1: Add Proper Test Structure

```java
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginTest {
    
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    
    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(System.getenv("CI") != null)
        );
    }
    
    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }
    
    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }
    
    @AfterEach
    void closeContext() {
        context.close();
    }
    
    @Test
    @DisplayName("User can login with valid credentials")
    void testSuccessfulLogin() {
        page.navigate("https://example.com/login");
        page.getByLabel("Email").fill("user@example.com");
        page.getByLabel("Password").fill("password123");
        page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Sign In")).click();
        
        assertThat(page.getByRole(AriaRole.HEADING, 
            new Page.GetByRoleOptions().setName("Dashboard"))).isVisible();
    }
}
```

### Step 2: Remove Unnecessary Actions

```java
// BEFORE: Recorded clicks before fill
page.getByLabel("Email").click();
page.getByLabel("Email").fill("user@example.com");
page.getByLabel("Password").click();
page.getByLabel("Password").fill("password123");

// AFTER: fill() auto-clicks, remove redundant clicks
page.getByLabel("Email").fill("user@example.com");
page.getByLabel("Password").fill("password123");
```

### Step 3: Improve Locators

```java
// BEFORE: Generated locators (may be brittle)
page.locator("div:nth-child(3) > button.submit-btn").click();

// AFTER: Use data-testid (most stable)
page.getByTestId("login-submit").click();

// AFTER: Use role-based locators (accessible)
page.getByRole(AriaRole.BUTTON, 
    new Page.GetByRoleOptions().setName("Sign In")).click();

// AFTER: Use label for form elements
page.getByLabel("Email").fill("user@example.com");
```

### Step 4: Add Meaningful Assertions

```java
@Test
void testSuccessfulLogin() {
    page.navigate("https://example.com/login");
    
    // Pre-condition assertions
    assertThat(page).hasTitle("Login - MyApp");
    assertThat(page.getByLabel("Email")).isVisible();
    
    // Perform login
    page.getByLabel("Email").fill("user@example.com");
    page.getByLabel("Password").fill("password123");
    page.getByRole(AriaRole.BUTTON, 
        new Page.GetByRoleOptions().setName("Sign In")).click();
    
    // Multiple post-condition assertions
    assertThat(page).hasURL(Pattern.compile(".*dashboard.*"));
    assertThat(page.getByRole(AriaRole.HEADING)).hasText("Dashboard");
    assertThat(page.getByTestId("user-menu")).isVisible();
    assertThat(page.getByTestId("user-name")).hasText("user@example.com");
}
```

### Step 5: Parameterize Test Data

```java
@Test
void testSuccessfulLogin() {
    // Extract test data
    String email = "user@example.com";
    String password = "password123";
    String expectedName = "Test User";
    
    page.navigate("https://example.com/login");
    page.getByLabel("Email").fill(email);
    page.getByLabel("Password").fill(password);
    page.getByRole(AriaRole.BUTTON, 
        new Page.GetByRoleOptions().setName("Sign In")).click();
    
    assertThat(page.getByTestId("user-name")).hasText(expectedName);
}

// Or use parameterized tests
@ParameterizedTest
@CsvSource({
    "user1@example.com, password1, User One",
    "user2@example.com, password2, User Two"
})
void testLoginWithMultipleUsers(String email, String password, String name) {
    page.navigate("https://example.com/login");
    page.getByLabel("Email").fill(email);
    page.getByLabel("Password").fill(password);
    page.getByRole(AriaRole.BUTTON, 
        new Page.GetByRoleOptions().setName("Sign In")).click();
    
    assertThat(page.getByTestId("user-name")).hasText(name);
}
```

### Step 6: Convert to Page Object Model

**LoginPage.java:**
```java
public class LoginPage {
    private final Page page;
    private final String url;
    
    // Locators
    private Locator emailField() {
        return page.getByLabel("Email");
    }
    
    private Locator passwordField() {
        return page.getByLabel("Password");
    }
    
    private Locator signInButton() {
        return page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Sign In"));
    }
    
    private Locator errorMessage() {
        return page.getByTestId("error-message");
    }
    
    public LoginPage(Page page, String baseUrl) {
        this.page = page;
        this.url = baseUrl + "/login";
    }
    
    public LoginPage navigate() {
        page.navigate(url);
        return this;
    }
    
    public DashboardPage login(String email, String password) {
        emailField().fill(email);
        passwordField().fill(password);
        signInButton().click();
        return new DashboardPage(page);
    }
    
    public LoginPage loginExpectingError(String email, String password) {
        emailField().fill(email);
        passwordField().fill(password);
        signInButton().click();
        return this;
    }
    
    public void assertErrorVisible(String message) {
        assertThat(errorMessage()).isVisible();
        assertThat(errorMessage()).containsText(message);
    }
}
```

**DashboardPage.java:**
```java
public class DashboardPage {
    private final Page page;
    
    private Locator heading() {
        return page.getByRole(AriaRole.HEADING);
    }
    
    private Locator userMenu() {
        return page.getByTestId("user-menu");
    }
    
    private Locator userName() {
        return page.getByTestId("user-name");
    }
    
    public DashboardPage(Page page) {
        this.page = page;
    }
    
    public void assertIsDisplayed() {
        assertThat(heading()).hasText("Dashboard");
        assertThat(userMenu()).isVisible();
    }
    
    public void assertUserName(String name) {
        assertThat(userName()).hasText(name);
    }
}
```

**Refactored Test:**
```java
public class LoginTest extends BaseTest {
    
    private LoginPage loginPage;
    
    @BeforeEach
    void setupPage() {
        loginPage = new LoginPage(page, baseUrl);
    }
    
    @Test
    @DisplayName("User can login with valid credentials")
    void testSuccessfulLogin() {
        DashboardPage dashboard = loginPage
            .navigate()
            .login("user@example.com", "password123");
        
        dashboard.assertIsDisplayed();
        dashboard.assertUserName("Test User");
    }
    
    @Test
    @DisplayName("Invalid credentials show error message")
    void testInvalidCredentials() {
        loginPage
            .navigate()
            .loginExpectingError("invalid@example.com", "wrongpassword")
            .assertErrorVisible("Invalid credentials");
    }
}
```

### Refactoring Checklist

```
┌─────────────────────────────────────────────────────────────────┐
│              Generated Code Refactoring Checklist                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Structure:                                                      │
│  □ Convert to JUnit test class                                  │
│  □ Add @BeforeAll/@AfterAll for browser                         │
│  □ Add @BeforeEach/@AfterEach for context                       │
│  □ Add @DisplayName for test methods                            │
│                                                                  │
│  Cleanup:                                                        │
│  □ Remove unnecessary click() before fill()                     │
│  □ Remove hardcoded waits if any                                │
│  □ Remove duplicate actions                                     │
│                                                                  │
│  Locators:                                                       │
│  □ Prefer getByTestId() for custom elements                     │
│  □ Use getByRole() for interactive elements                     │
│  □ Use getByLabel() for form fields                             │
│  □ Avoid CSS selectors with structure (nth-child)               │
│                                                                  │
│  Assertions:                                                     │
│  □ Add pre-condition assertions                                 │
│  □ Add multiple post-condition assertions                       │
│  □ Use specific assertions (hasText vs isVisible)               │
│                                                                  │
│  Test Data:                                                      │
│  □ Extract to variables or constants                            │
│  □ Consider parameterized tests                                 │
│  □ Remove sensitive data (use env vars)                         │
│                                                                  │
│  Architecture:                                                   │
│  □ Create Page Objects for each page                            │
│  □ Create BaseTest with common setup                            │
│  □ Group related tests in same class                            │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Key Takeaways

1. **Generated code** is a starting point, not final product
2. **Remove redundant** clicks and unnecessary actions
3. **Improve locators** using testid, role, label
4. **Add assertions** for comprehensive validation
5. **Parameterize data** for flexibility
6. **Convert to Page Objects** for maintainability

## Additional Resources

- [Playwright Best Practices](https://playwright.dev/java/docs/best-practices) - Official guidelines
- [Page Object Models](https://playwright.dev/java/docs/pom) - POM pattern guide
- [Locators Guide](https://playwright.dev/java/docs/locators) - Locator strategies

