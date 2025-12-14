# Collaborative Project: Page Object Model Implementation

## Overview

**Duration:** 3-4 hours  
**Mode:** PAIR PROGRAMMING (Driver/Navigator)  
**Difficulty:** Intermediate to Advanced

In this collaborative project, you'll work with a partner to implement a complete Page Object Model framework for a multi-page web application. This exercise synthesizes all Selenium concepts learned this week.

---

## Learning Objectives

By completing this project, you will:
- Implement the Page Object Model design pattern
- Practice pair programming techniques
- Create maintainable, reusable test code
- Build a complete test framework
- Collaborate effectively on code design

---

## Prerequisites

- Completed all Wednesday exercises
- Understanding of POM pattern (from `pom-design-pattern-java.md`)
- Partner assigned for pair programming

---

## The Scenario

BookHaven needs a professional test automation framework. Your team has been tasked with creating a Page Object Model implementation that other QA engineers can use as a template. This must be production-quality code.

**Target Application:** [The Internet](https://the-internet.herokuapp.com/)

You'll create page objects for:
1. Login Page
2. Secure Area (Dashboard)
3. Checkboxes Page
4. Dropdown Page
5. Dynamic Loading Page

---

## Pair Programming Guidelines

### Roles

**Driver:**
- Writes the code
- Focuses on implementation details
- Controls keyboard and mouse

**Navigator:**
- Reviews code as it's written
- Thinks about design and architecture
- Catches bugs and typos
- Suggests improvements

### Rotation Schedule

| Time Block | Duration | Driver | Navigator |
|------------|----------|--------|-----------|
| Block 1 | 45 min | Partner A | Partner B |
| Block 2 | 45 min | Partner B | Partner A |
| Block 3 | 45 min | Partner A | Partner B |
| Block 4 | 45 min | Partner B | Partner A |

**Switch roles every 45 minutes!**

---

## Project Structure

Create this folder structure:

```
selenium-pom-project/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── bookhaven/
    │               └── pages/
    │                   ├── BasePage.java
    │                   ├── LoginPage.java
    │                   ├── SecureAreaPage.java
    │                   ├── CheckboxesPage.java
    │                   ├── DropdownPage.java
    │                   └── DynamicLoadingPage.java
    └── test/
        └── java/
            └── com/
                └── bookhaven/
                    └── tests/
                        ├── BaseTest.java
                        ├── LoginTests.java
                        ├── CheckboxTests.java
                        ├── DropdownTests.java
                        └── DynamicLoadingTests.java
```

---

## Core Tasks

### Block 1: Base Classes (45 minutes)

**Driver creates `BasePage.java`:**

```java
package com.bookhaven.pages;

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
    
    // Wait for element to be visible and return it
    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    // Wait for element to be clickable and return it
    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    // Click with wait
    protected void click(By locator) {
        waitForClickable(locator).click();
    }
    
    // Type with wait and clear
    protected void type(By locator, String text) {
        WebElement element = waitForElement(locator);
        element.clear();
        element.sendKeys(text);
    }
    
    // Get text with wait
    protected String getText(By locator) {
        return waitForElement(locator).getText();
    }
    
    // Check if element is displayed
    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    // Wait for element to disappear
    protected void waitForInvisibility(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    
    // Get page title
    public String getPageTitle() {
        return driver.getTitle();
    }
    
    // Get current URL
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
```

**Navigator reviews and suggests:**
- Error handling improvements
- Additional helper methods needed
- Documentation/comments

**Driver creates `BaseTest.java`:**

```java
package com.bookhaven.tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BaseTest {
    
    protected WebDriver driver;
    protected static final String BASE_URL = "https://the-internet.herokuapp.com";
    
    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

### Block 2: Login Page Object (45 minutes)

**Switch roles!**

**Driver creates `LoginPage.java`:**

```java
package com.bookhaven.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {
    
    // URL
    private static final String LOGIN_URL = "https://the-internet.herokuapp.com/login";
    
    // Locators
    private By usernameField = By.id("username");
    private By passwordField = By.id("password");
    private By loginButton = By.cssSelector("button[type='submit']");
    private By flashMessage = By.id("flash");
    
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    // Navigate to login page
    public LoginPage navigateTo() {
        driver.get(LOGIN_URL);
        return this;
    }
    
    // Enter username
    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        return this;
    }
    
    // Enter password
    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }
    
    // Click login button (expecting success)
    public SecureAreaPage clickLogin() {
        click(loginButton);
        return new SecureAreaPage(driver);
    }
    
    // Click login button (expecting error)
    public LoginPage clickLoginExpectingError() {
        click(loginButton);
        return this;
    }
    
    // Convenience method: login with credentials
    public SecureAreaPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }
    
    // Get flash message text
    public String getFlashMessage() {
        return getText(flashMessage);
    }
    
    // Check if flash message is displayed
    public boolean isFlashMessageDisplayed() {
        return isDisplayed(flashMessage);
    }
    
    // Check if we're on login page
    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("/login");
    }
}
```

**Driver creates `SecureAreaPage.java`:**

```java
package com.bookhaven.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SecureAreaPage extends BasePage {
    
    // Locators
    private By flashMessage = By.id("flash");
    private By logoutButton = By.cssSelector("a[href='/logout']");
    private By pageHeading = By.tagName("h2");
    
    public SecureAreaPage(WebDriver driver) {
        super(driver);
    }
    
    // Get flash message
    public String getFlashMessage() {
        return getText(flashMessage);
    }
    
    // Get page heading
    public String getHeading() {
        return getText(pageHeading);
    }
    
    // Click logout
    public LoginPage logout() {
        click(logoutButton);
        return new LoginPage(driver);
    }
    
    // Check if logged in
    public boolean isLoggedIn() {
        return driver.getCurrentUrl().contains("/secure");
    }
}
```

**Navigator writes `LoginTests.java`:**

```java
package com.bookhaven.tests;

import com.bookhaven.pages.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class LoginTests extends BaseTest {
    
    private LoginPage loginPage;
    
    @BeforeEach
    void setupLoginPage() {
        loginPage = new LoginPage(driver);
        loginPage.navigateTo();
    }
    
    @Test
    @DisplayName("Valid login redirects to secure area")
    void testValidLogin() {
        SecureAreaPage securePage = loginPage.loginAs("tomsmith", "SuperSecretPassword!");
        
        assertTrue(securePage.isLoggedIn());
        assertTrue(securePage.getFlashMessage().contains("You logged into"));
    }
    
    @Test
    @DisplayName("Invalid username shows error")
    void testInvalidUsername() {
        loginPage.loginAs("invalid", "SuperSecretPassword!");
        loginPage.clickLoginExpectingError();
        
        assertTrue(loginPage.getFlashMessage().contains("Your username is invalid"));
    }
    
    @Test
    @DisplayName("Invalid password shows error")
    void testInvalidPassword() {
        loginPage.enterUsername("tomsmith");
        loginPage.enterPassword("wrongpassword");
        loginPage.clickLoginExpectingError();
        
        assertTrue(loginPage.getFlashMessage().contains("Your password is invalid"));
    }
    
    @Test
    @DisplayName("Logout returns to login page")
    void testLogout() {
        SecureAreaPage securePage = loginPage.loginAs("tomsmith", "SuperSecretPassword!");
        LoginPage returnedLoginPage = securePage.logout();
        
        assertTrue(returnedLoginPage.isOnLoginPage());
        assertTrue(returnedLoginPage.getFlashMessage().contains("logged out"));
    }
}
```

### Block 3: Additional Page Objects (45 minutes)

**Switch roles!**

**Driver creates `CheckboxesPage.java`:**

```java
package com.bookhaven.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class CheckboxesPage extends BasePage {
    
    private static final String PAGE_URL = "https://the-internet.herokuapp.com/checkboxes";
    
    // Locators
    private By checkboxes = By.cssSelector("input[type='checkbox']");
    
    public CheckboxesPage(WebDriver driver) {
        super(driver);
    }
    
    public CheckboxesPage navigateTo() {
        driver.get(PAGE_URL);
        return this;
    }
    
    public int getCheckboxCount() {
        return driver.findElements(checkboxes).size();
    }
    
    public boolean isCheckboxSelected(int index) {
        List<WebElement> boxes = driver.findElements(checkboxes);
        return boxes.get(index).isSelected();
    }
    
    public CheckboxesPage selectCheckbox(int index) {
        List<WebElement> boxes = driver.findElements(checkboxes);
        WebElement checkbox = boxes.get(index);
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
        return this;
    }
    
    public CheckboxesPage deselectCheckbox(int index) {
        List<WebElement> boxes = driver.findElements(checkboxes);
        WebElement checkbox = boxes.get(index);
        if (checkbox.isSelected()) {
            checkbox.click();
        }
        return this;
    }
    
    public CheckboxesPage toggleCheckbox(int index) {
        List<WebElement> boxes = driver.findElements(checkboxes);
        boxes.get(index).click();
        return this;
    }
}
```

**Driver creates `DropdownPage.java`:**

```java
package com.bookhaven.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import java.util.List;
import java.util.stream.Collectors;

public class DropdownPage extends BasePage {
    
    private static final String PAGE_URL = "https://the-internet.herokuapp.com/dropdown";
    
    private By dropdown = By.id("dropdown");
    
    public DropdownPage(WebDriver driver) {
        super(driver);
    }
    
    public DropdownPage navigateTo() {
        driver.get(PAGE_URL);
        return this;
    }
    
    private Select getSelect() {
        return new Select(driver.findElement(dropdown));
    }
    
    public DropdownPage selectByText(String text) {
        getSelect().selectByVisibleText(text);
        return this;
    }
    
    public DropdownPage selectByValue(String value) {
        getSelect().selectByValue(value);
        return this;
    }
    
    public DropdownPage selectByIndex(int index) {
        getSelect().selectByIndex(index);
        return this;
    }
    
    public String getSelectedOption() {
        return getSelect().getFirstSelectedOption().getText();
    }
    
    public List<String> getAllOptions() {
        return getSelect().getOptions().stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }
    
    public int getOptionsCount() {
        return getSelect().getOptions().size();
    }
}
```

**Navigator writes tests:**

```java
package com.bookhaven.tests;

import com.bookhaven.pages.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CheckboxTests extends BaseTest {
    
    private CheckboxesPage checkboxesPage;
    
    @BeforeEach
    void setupPage() {
        checkboxesPage = new CheckboxesPage(driver);
        checkboxesPage.navigateTo();
    }
    
    @Test
    @DisplayName("Page has two checkboxes")
    void testCheckboxCount() {
        assertEquals(2, checkboxesPage.getCheckboxCount());
    }
    
    @Test
    @DisplayName("Select checkbox changes state")
    void testSelectCheckbox() {
        checkboxesPage.selectCheckbox(0);
        assertTrue(checkboxesPage.isCheckboxSelected(0));
    }
    
    @Test
    @DisplayName("Deselect checkbox changes state")
    void testDeselectCheckbox() {
        checkboxesPage.deselectCheckbox(1);
        assertFalse(checkboxesPage.isCheckboxSelected(1));
    }
}

class DropdownTests extends BaseTest {
    
    private DropdownPage dropdownPage;
    
    @BeforeEach
    void setupPage() {
        dropdownPage = new DropdownPage(driver);
        dropdownPage.navigateTo();
    }
    
    @Test
    @DisplayName("Select by visible text")
    void testSelectByText() {
        dropdownPage.selectByText("Option 1");
        assertEquals("Option 1", dropdownPage.getSelectedOption());
    }
    
    @Test
    @DisplayName("Select by value")
    void testSelectByValue() {
        dropdownPage.selectByValue("2");
        assertEquals("Option 2", dropdownPage.getSelectedOption());
    }
    
    @Test
    @DisplayName("Get all options")
    void testGetAllOptions() {
        List<String> options = dropdownPage.getAllOptions();
        assertEquals(3, options.size());
        assertTrue(options.contains("Option 1"));
        assertTrue(options.contains("Option 2"));
    }
}
```

### Block 4: Dynamic Loading & Integration (45 minutes)

**Switch roles!**

**Complete `DynamicLoadingPage.java` and comprehensive integration tests.**

---

## Definition of Done

Your project is complete when you have:

- [ ] `BasePage` with reusable helper methods
- [ ] `LoginPage` with all necessary methods
- [ ] `SecureAreaPage` with logout functionality
- [ ] `CheckboxesPage` with selection methods
- [ ] `DropdownPage` with Select class integration
- [ ] `DynamicLoadingPage` with wait handling
- [ ] Test classes for each page object
- [ ] All tests passing
- [ ] Code reviewed by partner
- [ ] Clean, consistent code style

---

## Collaboration Rubric

| Criteria | Points |
|----------|--------|
| Both partners contributed equally | 20 |
| Role switching followed | 10 |
| Code reviews conducted | 15 |
| Design discussions documented | 10 |
| Tests comprehensive | 25 |
| Code quality (clean, maintainable) | 20 |
| **Total** | **100** |

---

## Submission Requirements

1. Complete project in Git repository
2. Both partners' names in README.md
3. Commit history showing collaboration
4. All tests passing
5. Brief reflection on pair programming experience

---

## Reflection Questions (Discuss with Partner)

1. What was the biggest benefit of pair programming?
2. What was the most challenging aspect?
3. How did you resolve design disagreements?
4. What would you do differently next time?

---

## Additional Resources

- Written Content: `pom-design-pattern-java.md`, `page-factory-java.md`
- [Page Object Pattern](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/)

