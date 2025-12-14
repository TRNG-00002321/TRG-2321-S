# Page Object Model (POM) Design Pattern

## Learning Objectives
- Understand the Page Object Model design pattern
- Create page classes that encapsulate UI elements and actions
- Separate test logic from page interaction logic
- Implement maintainable and reusable test automation code
- Apply POM best practices for scalable test frameworks

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, maintaining UI tests becomes challenging as applications grow. Without structure, tests become brittle and changes to UI elements require updates across multiple test files.

Page Object Model solves this by creating an abstraction layer between tests and UI elements, making your automation framework maintainable, readable, and scalable.

## What is Page Object Model?

### Core Concept

```
Page Object Model Pattern:
┌─────────────────────────────────────────────────────────────────────┐
│                            Test Class                               │
│     (Contains test logic, assertions, test data)                    │
│                              │                                      │
│                              ▼                                      │
│                         Page Objects                                │
│     (Encapsulate UI elements and interactions)                      │
│                              │                                      │
│                              ▼                                      │
│                      Web Application                                │
│     (The actual web pages being tested)                             │
└─────────────────────────────────────────────────────────────────────┘
```

### Benefits

```
POM Benefits:
┌─────────────────────────────────────────────────────────────────────┐
│ Maintainability  │ Change locator once, not in every test          │
├──────────────────┼──────────────────────────────────────────────────┤
│ Readability      │ Tests read like user stories                    │
├──────────────────┼──────────────────────────────────────────────────┤
│ Reusability      │ Page methods used across multiple tests         │
├──────────────────┼──────────────────────────────────────────────────┤
│ Separation       │ UI details hidden from test logic               │
├──────────────────┼──────────────────────────────────────────────────┤
│ Scalability      │ Easy to add new pages and tests                 │
└──────────────────┴──────────────────────────────────────────────────┘
```

## Basic Page Object Structure

### Without POM (Anti-pattern)

```java
@Test
void testLoginBad() {
    driver.get("https://example.com/login");
    driver.findElement(By.id("username")).sendKeys("user");
    driver.findElement(By.id("password")).sendKeys("pass");
    driver.findElement(By.id("login-btn")).click();
    
    // Wait for redirect
    new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(ExpectedConditions.urlContains("dashboard"));
    
    String welcomeMsg = driver.findElement(By.className("welcome")).getText();
    assertTrue(welcomeMsg.contains("Welcome"));
}

@Test
void testLoginAnotherTest() {
    // Same locators duplicated!
    driver.get("https://example.com/login");
    driver.findElement(By.id("username")).sendKeys("admin");
    driver.findElement(By.id("password")).sendKeys("admin123");
    driver.findElement(By.id("login-btn")).click();
    // ...more duplicated code
}
```

### With POM (Recommended)

```java
// LoginPage.java
public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Locators
    private By usernameField = By.id("username");
    private By passwordField = By.id("password");
    private By loginButton = By.id("login-btn");
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    public void navigateTo() {
        driver.get("https://example.com/login");
    }
    
    public void enterUsername(String username) {
        driver.findElement(usernameField).sendKeys(username);
    }
    
    public void enterPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
    }
    
    public DashboardPage clickLogin() {
        driver.findElement(loginButton).click();
        wait.until(ExpectedConditions.urlContains("dashboard"));
        return new DashboardPage(driver);
    }
    
    // Convenience method combining multiple actions
    public DashboardPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }
}

// Test using POM
@Test
void testLoginGood() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo();
    
    DashboardPage dashboard = loginPage.loginAs("user", "pass");
    
    assertTrue(dashboard.getWelcomeMessage().contains("Welcome"));
}
```

## Creating Page Objects

### Page Object Template

```java
package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ExamplePage {
    
    // WebDriver and wait
    protected WebDriver driver;
    protected WebDriverWait wait;
    
    // Page URL
    private static final String PAGE_URL = "https://example.com/page";
    
    // Locators (keep private)
    private By headerLocator = By.tagName("h1");
    private By submitButton = By.id("submit");
    private By nameField = By.id("name");
    
    // Constructor
    public ExamplePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    // Navigation
    public void navigateTo() {
        driver.get(PAGE_URL);
        waitForPageLoad();
    }
    
    // Wait for page to be ready
    private void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(headerLocator));
    }
    
    // Element interactions (public methods)
    public void enterName(String name) {
        WebElement field = driver.findElement(nameField);
        field.clear();
        field.sendKeys(name);
    }
    
    public void clickSubmit() {
        driver.findElement(submitButton).click();
    }
    
    // State checks
    public String getHeaderText() {
        return driver.findElement(headerLocator).getText();
    }
    
    public boolean isDisplayed() {
        try {
            return driver.findElement(headerLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
```

### Login Page Example

```java
package com.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

public class LoginPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    private static final String LOGIN_URL = "https://example.com/login";
    
    // Locators
    private By usernameInput = By.id("username");
    private By passwordInput = By.id("password");
    private By loginButton = By.id("login-btn");
    private By errorMessage = By.className("error-message");
    private By rememberMeCheckbox = By.id("remember-me");
    private By forgotPasswordLink = By.linkText("Forgot Password?");
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    public void navigateTo() {
        driver.get(LOGIN_URL);
    }
    
    public void enterUsername(String username) {
        WebElement field = wait.until(
            ExpectedConditions.visibilityOfElementLocated(usernameInput)
        );
        field.clear();
        field.sendKeys(username);
    }
    
    public void enterPassword(String password) {
        WebElement field = driver.findElement(passwordInput);
        field.clear();
        field.sendKeys(password);
    }
    
    public void checkRememberMe() {
        WebElement checkbox = driver.findElement(rememberMeCheckbox);
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
    }
    
    public DashboardPage clickLogin() {
        driver.findElement(loginButton).click();
        wait.until(ExpectedConditions.urlContains("dashboard"));
        return new DashboardPage(driver);
    }
    
    public LoginPage clickLoginExpectingError() {
        driver.findElement(loginButton).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
        return this;  // Stay on same page
    }
    
    public ForgotPasswordPage clickForgotPassword() {
        driver.findElement(forgotPasswordLink).click();
        return new ForgotPasswordPage(driver);
    }
    
    // Convenience methods
    public DashboardPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }
    
    public DashboardPage loginWithRememberMe(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        checkRememberMe();
        return clickLogin();
    }
    
    // State checks
    public String getErrorMessage() {
        return driver.findElement(errorMessage).getText();
    }
    
    public boolean isErrorDisplayed() {
        try {
            return driver.findElement(errorMessage).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("login");
    }
}
```

### Dashboard Page Example

```java
package com.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

public class DashboardPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Locators
    private By welcomeMessage = By.className("welcome-message");
    private By userMenu = By.id("user-menu");
    private By logoutLink = By.linkText("Logout");
    private By profileLink = By.linkText("Profile");
    private By notificationBadge = By.className("notification-count");
    
    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    public String getWelcomeMessage() {
        return wait.until(
            ExpectedConditions.visibilityOfElementLocated(welcomeMessage)
        ).getText();
    }
    
    public void openUserMenu() {
        driver.findElement(userMenu).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(logoutLink));
    }
    
    public LoginPage logout() {
        openUserMenu();
        driver.findElement(logoutLink).click();
        wait.until(ExpectedConditions.urlContains("login"));
        return new LoginPage(driver);
    }
    
    public ProfilePage goToProfile() {
        openUserMenu();
        driver.findElement(profileLink).click();
        return new ProfilePage(driver);
    }
    
    public int getNotificationCount() {
        try {
            String count = driver.findElement(notificationBadge).getText();
            return Integer.parseInt(count);
        } catch (NoSuchElementException e) {
            return 0;
        }
    }
    
    public boolean isLoggedIn() {
        try {
            return driver.findElement(welcomeMessage).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
```

## Page Object Patterns

### Method Chaining (Fluent API)

```java
public class FluentLoginPage {
    private WebDriver driver;
    
    // Return 'this' for same-page actions
    public FluentLoginPage enterUsername(String username) {
        driver.findElement(By.id("username")).sendKeys(username);
        return this;
    }
    
    public FluentLoginPage enterPassword(String password) {
        driver.findElement(By.id("password")).sendKeys(password);
        return this;
    }
    
    public FluentLoginPage checkRememberMe() {
        driver.findElement(By.id("remember")).click();
        return this;
    }
    
    // Return new page for navigation
    public DashboardPage submit() {
        driver.findElement(By.id("login")).click();
        return new DashboardPage(driver);
    }
}

// Usage with method chaining
@Test
void testFluentLogin() {
    DashboardPage dashboard = new FluentLoginPage(driver)
        .enterUsername("user")
        .enterPassword("pass")
        .checkRememberMe()
        .submit();
        
    assertTrue(dashboard.isLoggedIn());
}
```

### Base Page Class

```java
package com.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

public abstract class BasePage {
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    
    protected static final int DEFAULT_TIMEOUT = 10;
    
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }
    
    // Common methods available to all pages
    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    protected void click(By locator) {
        waitForClickable(locator).click();
    }
    
    protected void type(By locator, String text) {
        WebElement element = waitForElement(locator);
        element.clear();
        element.sendKeys(text);
    }
    
    protected String getText(By locator) {
        return waitForElement(locator).getText();
    }
    
    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    protected void waitForPageLoad() {
        wait.until(driver -> 
            ((JavascriptExecutor) driver)
                .executeScript("return document.readyState")
                .equals("complete")
        );
    }
    
    public String getPageTitle() {
        return driver.getTitle();
    }
    
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
```

### Page Inheriting from Base

```java
package com.example.pages;

import org.openqa.selenium.*;

public class LoginPage extends BasePage {
    
    private static final String LOGIN_URL = "https://example.com/login";
    
    // Locators
    private By usernameField = By.id("username");
    private By passwordField = By.id("password");
    private By loginButton = By.id("login-btn");
    private By errorMessage = By.className("error");
    
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    public void navigateTo() {
        driver.get(LOGIN_URL);
        waitForPageLoad();
    }
    
    // Uses inherited helper methods
    public void enterUsername(String username) {
        type(usernameField, username);
    }
    
    public void enterPassword(String password) {
        type(passwordField, password);
    }
    
    public DashboardPage clickLogin() {
        click(loginButton);
        return new DashboardPage(driver);
    }
    
    public String getErrorMessage() {
        return getText(errorMessage);
    }
    
    public boolean hasError() {
        return isDisplayed(errorMessage);
    }
}
```

## Tests Using Page Objects

### Complete Test Example

```java
package com.example.tests;

import com.example.pages.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

class LoginTests {
    
    private WebDriver driver;
    private LoginPage loginPage;
    
    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        loginPage = new LoginPage(driver);
        loginPage.navigateTo();
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    @DisplayName("Valid login redirects to dashboard")
    void testValidLogin() {
        DashboardPage dashboard = loginPage.loginAs("validUser", "validPass");
        
        assertTrue(dashboard.isLoggedIn());
        assertTrue(dashboard.getWelcomeMessage().contains("Welcome"));
    }
    
    @Test
    @DisplayName("Invalid credentials show error")
    void testInvalidLogin() {
        loginPage.enterUsername("invalid");
        loginPage.enterPassword("wrong");
        loginPage.clickLoginExpectingError();
        
        assertTrue(loginPage.hasError());
        assertEquals("Invalid credentials", loginPage.getErrorMessage());
    }
    
    @Test
    @DisplayName("User can logout successfully")
    void testLogout() {
        DashboardPage dashboard = loginPage.loginAs("user", "pass");
        LoginPage returnedLoginPage = dashboard.logout();
        
        assertTrue(returnedLoginPage.isOnLoginPage());
    }
    
    @Test
    @DisplayName("Remember me keeps user logged in")
    void testRememberMe() {
        DashboardPage dashboard = loginPage.loginWithRememberMe("user", "pass");
        
        assertTrue(dashboard.isLoggedIn());
        // Additional verification for remember me functionality
    }
}
```

## Best Practices

```
POM Best Practices:
┌─────────────────────────────────────────────────────────────────────┐
│ 1. One class per page (or component)                                │
│    └── Keep pages focused and cohesive                             │
│                                                                      │
│ 2. Keep locators private                                            │
│    └── Only expose methods, not implementation details             │
│                                                                      │
│ 3. Methods return page objects                                      │
│    └── Return new page after navigation, 'this' for same page      │
│                                                                      │
│ 4. No assertions in page objects                                    │
│    └── Assertions belong in tests, pages just provide data         │
│                                                                      │
│ 5. Use meaningful method names                                      │
│    └── login(), getErrorMessage(), isLoggedIn()                    │
│                                                                      │
│ 6. Handle waits in page objects                                     │
│    └── Tests shouldn't need to manage waits                        │
│                                                                      │
│ 7. Create convenience methods                                       │
│    └── Combine common action sequences                             │
└─────────────────────────────────────────────────────────────────────┘
```

## Summary

- **Page Object Model** creates classes representing web pages
- **Page objects** encapsulate locators and interactions, hiding UI details from tests
- Tests become **readable** and **maintainable** - change locator once, not everywhere
- **Methods return page objects** for navigation flow (return new page or `this`)
- **Base page class** provides reusable helper methods
- **No assertions** in page objects - they belong in tests
- POM enables **scalable** test automation frameworks

In the next lesson, you'll learn about Page Factory for simplified element initialization.

## Additional Resources

- [Page Object Model](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/) - Official Selenium guide
- [Martin Fowler on Page Objects](https://martinfowler.com/bliki/PageObject.html) - Original pattern description

