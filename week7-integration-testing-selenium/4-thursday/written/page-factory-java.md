# Page Factory in Selenium WebDriver

## Learning Objectives
- Understand Page Factory and its relationship to Page Object Model
- Use @FindBy annotations to declare element locators
- Initialize page elements with PageFactory.initElements()
- Apply Page Factory patterns for cleaner page objects
- Understand when to use Page Factory vs standard POM

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, Page Factory provides a streamlined way to implement the Page Object Model. Using annotations instead of explicit By locators makes page objects more readable and reduces boilerplate code.

Understanding Page Factory helps you write cleaner, more maintainable page classes while leveraging Selenium's built-in support for element initialization.

## What is Page Factory?

### Core Concept

```
Page Factory:
┌─────────────────────────────────────────────────────────────────────┐
│ Standard POM                    │ Page Factory                     │
├─────────────────────────────────┼──────────────────────────────────┤
│ By locator = By.id("elem");     │ @FindBy(id = "elem")            │
│ WebElement el = driver.find... │ private WebElement elem;         │
├─────────────────────────────────┼──────────────────────────────────┤
│ Manual element lookup           │ Automatic initialization         │
│ Explicit driver.findElement()   │ Lazy loading                     │
└─────────────────────────────────┴──────────────────────────────────┘
```

### Benefits

```
Page Factory Benefits:
┌─────────────────────────────────────────────────────────────────────┐
│ Cleaner Syntax     │ Annotations replace By declarations           │
├────────────────────┼────────────────────────────────────────────────┤
│ Lazy Loading       │ Elements found when accessed, not at init     │
├────────────────────┼────────────────────────────────────────────────┤
│ Less Boilerplate   │ No explicit findElement() calls needed        │
├────────────────────┼────────────────────────────────────────────────┤
│ Readable           │ Element declarations at top of class          │
└────────────────────┴────────────────────────────────────────────────┘
```

## Basic Page Factory Usage

### Importing Required Classes

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
```

### Simple Page Factory Example

```java
package com.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
    
    private WebDriver driver;
    
    // Element declarations using @FindBy
    @FindBy(id = "username")
    private WebElement usernameField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(id = "login-btn")
    private WebElement loginButton;
    
    @FindBy(className = "error-message")
    private WebElement errorMessage;
    
    // Constructor with PageFactory initialization
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    
    // Methods using annotated elements
    public void enterUsername(String username) {
        usernameField.clear();
        usernameField.sendKeys(username);
    }
    
    public void enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
    }
    
    public void clickLogin() {
        loginButton.click();
    }
    
    public String getErrorMessage() {
        return errorMessage.getText();
    }
}
```

## @FindBy Annotation Options

### Locator Strategies

```java
// By ID
@FindBy(id = "username")
private WebElement usernameById;

// By name
@FindBy(name = "email")
private WebElement emailByName;

// By class name
@FindBy(className = "submit-btn")
private WebElement buttonByClass;

// By CSS selector
@FindBy(css = "#login-form input[type='submit']")
private WebElement submitByCss;

// By XPath
@FindBy(xpath = "//button[text()='Login']")
private WebElement buttonByXpath;

// By link text
@FindBy(linkText = "Forgot Password?")
private WebElement forgotPasswordLink;

// By partial link text
@FindBy(partialLinkText = "Forgot")
private WebElement forgotPartialLink;

// By tag name
@FindBy(tagName = "h1")
private WebElement headerTag;
```

### Using 'how' and 'using' Syntax

```java
import org.openqa.selenium.support.How;

// Alternative syntax
@FindBy(how = How.ID, using = "username")
private WebElement usernameAlt;

@FindBy(how = How.CSS, using = ".login-form input")
private WebElement inputAlt;

@FindBy(how = How.XPATH, using = "//div[@class='container']")
private WebElement divAlt;
```

## Finding Multiple Elements

### @FindBy with List

```java
import java.util.List;

// Find all matching elements
@FindBy(css = ".product-item")
private List<WebElement> productItems;

@FindBy(tagName = "li")
private List<WebElement> listItems;

// Usage
public int getProductCount() {
    return productItems.size();
}

public List<String> getListItemTexts() {
    return listItems.stream()
        .map(WebElement::getText)
        .collect(Collectors.toList());
}
```

## Advanced Annotations

### @FindBys - AND Logic (All Conditions Must Match)

```java
import org.openqa.selenium.support.FindBys;

// Element must match ALL criteria (chained)
// Finds: div.container -> form.login-form -> button
@FindBys({
    @FindBy(css = "div.container"),
    @FindBy(css = "form.login-form"),
    @FindBy(css = "button")
})
private WebElement loginButtonChained;
```

### @FindAll - OR Logic (Any Condition Matches)

```java
import org.openqa.selenium.support.FindAll;

// Element matches ANY of the criteria
@FindAll({
    @FindBy(id = "submit"),
    @FindBy(css = ".submit-btn"),
    @FindBy(xpath = "//button[@type='submit']")
})
private WebElement submitButton;
```

### @CacheLookup - Cache Element

```java
import org.openqa.selenium.support.CacheLookup;

// Cache element after first lookup (for static elements)
@FindBy(id = "header")
@CacheLookup
private WebElement header;

// WARNING: Don't cache dynamic elements that change
// Only use for stable elements that don't change during test
```

## Complete Page Factory Example

### Login Page

```java
package com.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Static elements - can be cached
    @FindBy(tagName = "h1")
    @CacheLookup
    private WebElement pageHeader;
    
    // Form elements
    @FindBy(id = "username")
    private WebElement usernameInput;
    
    @FindBy(id = "password")
    private WebElement passwordInput;
    
    @FindBy(id = "login-btn")
    private WebElement loginButton;
    
    @FindBy(id = "remember-me")
    private WebElement rememberMeCheckbox;
    
    // Dynamic elements - don't cache
    @FindBy(css = ".alert.error")
    private WebElement errorAlert;
    
    @FindBy(css = ".alert.success")
    private WebElement successAlert;
    
    // Links
    @FindBy(linkText = "Forgot Password?")
    private WebElement forgotPasswordLink;
    
    @FindBy(linkText = "Create Account")
    private WebElement createAccountLink;
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }
    
    public void navigateTo(String url) {
        driver.get(url);
    }
    
    public String getPageHeader() {
        return pageHeader.getText();
    }
    
    public LoginPage enterUsername(String username) {
        usernameInput.clear();
        usernameInput.sendKeys(username);
        return this;
    }
    
    public LoginPage enterPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
        return this;
    }
    
    public LoginPage checkRememberMe() {
        if (!rememberMeCheckbox.isSelected()) {
            rememberMeCheckbox.click();
        }
        return this;
    }
    
    public DashboardPage clickLogin() {
        loginButton.click();
        wait.until(ExpectedConditions.urlContains("dashboard"));
        return new DashboardPage(driver);
    }
    
    public LoginPage clickLoginExpectingError() {
        loginButton.click();
        wait.until(ExpectedConditions.visibilityOf(errorAlert));
        return this;
    }
    
    public ForgotPasswordPage clickForgotPassword() {
        forgotPasswordLink.click();
        return new ForgotPasswordPage(driver);
    }
    
    public RegistrationPage clickCreateAccount() {
        createAccountLink.click();
        return new RegistrationPage(driver);
    }
    
    // Convenience method
    public DashboardPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }
    
    // State checks
    public String getErrorMessage() {
        return errorAlert.getText();
    }
    
    public boolean isErrorDisplayed() {
        try {
            return errorAlert.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
```

### Dashboard Page

```java
package com.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class DashboardPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    @FindBy(css = ".welcome-message")
    @CacheLookup
    private WebElement welcomeMessage;
    
    @FindBy(id = "user-menu-toggle")
    private WebElement userMenuToggle;
    
    @FindBy(css = "#user-menu .logout")
    private WebElement logoutLink;
    
    @FindBy(css = "#user-menu .profile")
    private WebElement profileLink;
    
    @FindBy(css = ".notification-badge")
    private WebElement notificationBadge;
    
    @FindBy(css = ".dashboard-widget")
    private List<WebElement> widgets;
    
    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }
    
    public String getWelcomeMessage() {
        wait.until(ExpectedConditions.visibilityOf(welcomeMessage));
        return welcomeMessage.getText();
    }
    
    public void openUserMenu() {
        userMenuToggle.click();
        wait.until(ExpectedConditions.visibilityOf(logoutLink));
    }
    
    public LoginPage logout() {
        openUserMenu();
        logoutLink.click();
        return new LoginPage(driver);
    }
    
    public ProfilePage goToProfile() {
        openUserMenu();
        profileLink.click();
        return new ProfilePage(driver);
    }
    
    public int getNotificationCount() {
        try {
            return Integer.parseInt(notificationBadge.getText());
        } catch (Exception e) {
            return 0;
        }
    }
    
    public int getWidgetCount() {
        return widgets.size();
    }
    
    public boolean isLoggedIn() {
        try {
            return welcomeMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
```

## Base Page with Page Factory

```java
package com.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }
    
    protected void waitForVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    protected void waitForClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    protected void click(WebElement element) {
        waitForClickable(element);
        element.click();
    }
    
    protected void type(WebElement element, String text) {
        waitForVisible(element);
        element.clear();
        element.sendKeys(text);
    }
    
    protected String getText(WebElement element) {
        waitForVisible(element);
        return element.getText();
    }
    
    public String getTitle() {
        return driver.getTitle();
    }
    
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}

// Child page using base
public class LoginPage extends BasePage {
    
    @FindBy(id = "username")
    private WebElement usernameField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(id = "submit")
    private WebElement submitButton;
    
    public LoginPage(WebDriver driver) {
        super(driver);  // Calls PageFactory.initElements
    }
    
    public void login(String user, String pass) {
        type(usernameField, user);     // Uses base method
        type(passwordField, pass);
        click(submitButton);
    }
}
```

## Page Factory vs Standard POM

### When to Use Page Factory

```
Use Page Factory When:
┌─────────────────────────────────────────────────────────────────────┐
│ + Clean, readable page objects with annotation-based locators      │
│ + Simple element declarations with minimal boilerplate             │
│ + Lazy loading is acceptable (element found when accessed)         │
│ + Elements are relatively stable                                    │
└─────────────────────────────────────────────────────────────────────┘
```

### When to Use Standard POM

```
Use Standard POM When:
┌─────────────────────────────────────────────────────────────────────┐
│ + Need dynamic locators (built at runtime)                          │
│ + Complex wait strategies before element access                     │
│ + Working with elements that change frequently                      │
│ + Need more control over element lookup timing                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Comparison Example

```java
// Page Factory approach
public class PageFactoryStyle {
    @FindBy(id = "item-1")
    private WebElement item1;
    
    @FindBy(id = "item-2")
    private WebElement item2;
    
    // What if you need item-3, item-4, ... item-N?
}

// Standard POM approach (more flexible for dynamic scenarios)
public class StandardPOMStyle {
    
    public WebElement getItem(int index) {
        return driver.findElement(By.id("item-" + index));
    }
    
    public List<WebElement> getAllItems() {
        return driver.findElements(By.cssSelector("[id^='item-']"));
    }
}
```

## Test Using Page Factory Pages

```java
package com.example.tests;

import com.example.pages.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

class PageFactoryTest {
    
    private WebDriver driver;
    
    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    @DisplayName("Login with Page Factory pages")
    void testLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo("https://example.com/login");
        
        DashboardPage dashboard = loginPage
            .enterUsername("testuser")
            .enterPassword("password123")
            .clickLogin();
        
        assertTrue(dashboard.isLoggedIn());
        assertTrue(dashboard.getWelcomeMessage().contains("Welcome"));
    }
    
    @Test
    @DisplayName("Invalid login shows error")
    void testInvalidLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo("https://example.com/login");
        
        loginPage.enterUsername("invalid")
                 .enterPassword("wrong")
                 .clickLoginExpectingError();
        
        assertTrue(loginPage.isErrorDisplayed());
    }
}
```

## Summary

- **Page Factory** is Selenium's built-in support for Page Object Model
- **@FindBy** annotations declare element locators cleanly at class level
- **PageFactory.initElements()** initializes annotated elements (call in constructor)
- Elements are **lazily loaded** - found when first accessed, not at initialization
- **@FindBys** (AND logic) and **@FindAll** (OR logic) provide advanced locator combinations
- **@CacheLookup** caches static elements (use cautiously)
- Page Factory is great for **clean, readable** pages; standard POM offers **more flexibility**
- Both approaches work with **base page classes** for code reuse

In the next lesson, you'll learn about element location strategies and the findElements method.

## Additional Resources

- [Page Factory Documentation](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/support/PageFactory.html) - JavaDoc
- [Selenium Page Factory Guide](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/) - Official docs

