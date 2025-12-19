# Exercise 5: Cucumber-Selenium Integration

## Objective

Integrate Cucumber with Selenium WebDriver to create a complete BDD-style UI test suite with Page Object Model, proper test organization, and comprehensive reporting.

## Learning Goals

- Integrate Cucumber with Selenium WebDriver
- Implement Page Object Model in BDD context
- Share WebDriver state across step definitions
- Create reusable step libraries
- Generate rich test reports

## Time Estimate

60 minutes

---

## Core Tasks

### Task 1: Project Structure (10 minutes)

Create a well-organized project structure:

```
cucumber-selenium-project/
├── pom.xml
├── src/
│   └── test/
│       ├── java/
│       │   └── com/training/bdd/
│       │       ├── config/
│       │       │   └── TestConfig.java
│       │       ├── context/
│       │       │   └── TestContext.java
│       │       ├── hooks/
│       │       │   └── Hooks.java
│       │       ├── pages/
│       │       │   ├── BasePage.java
│       │       │   ├── LoginPage.java
│       │       │   ├── SecurePage.java
│       │       │   └── FormPage.java
│       │       ├── runners/
│       │       │   └── TestRunner.java
│       │       └── stepdefinitions/
│       │           ├── CommonSteps.java
│       │           ├── LoginSteps.java
│       │           └── FormSteps.java
│       └── resources/
│           └── features/
│               ├── login.feature
│               └── forms.feature
└── README.md
```

### Task 2: Test Context for State Sharing (10 minutes)

Create `src/test/java/com/training/bdd/context/TestContext.java`:

```java
package com.training.bdd.context;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

import com.training.bdd.pages.*;
import java.time.Duration;

/**
 * Singleton context to share state across step definitions.
 * Manages WebDriver lifecycle and page object instances.
 */
public class TestContext {
    
    private static TestContext instance;
    private WebDriver driver;
    
    // Page Objects
    private LoginPage loginPage;
    private SecurePage securePage;
    private FormPage formPage;
    
    // Scenario data
    private String currentUser;
    private String lastMessage;
    
    private TestContext() {
        // Private constructor for singleton
    }
    
    public static synchronized TestContext getInstance() {
        if (instance == null) {
            instance = new TestContext();
        }
        return instance;
    }
    
    public void initializeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }
    
    public WebDriver getDriver() {
        return driver;
    }
    
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
        // Reset page objects
        loginPage = null;
        securePage = null;
        formPage = null;
    }
    
    // Page Object getters (lazy initialization)
    public LoginPage getLoginPage() {
        if (loginPage == null) {
            loginPage = new LoginPage(driver);
        }
        return loginPage;
    }
    
    public SecurePage getSecurePage() {
        if (securePage == null) {
            securePage = new SecurePage(driver);
        }
        return securePage;
    }
    
    public FormPage getFormPage() {
        if (formPage == null) {
            formPage = new FormPage(driver);
        }
        return formPage;
    }
    
    // Scenario data accessors
    public void setCurrentUser(String user) {
        this.currentUser = user;
    }
    
    public String getCurrentUser() {
        return currentUser;
    }
    
    public void setLastMessage(String message) {
        this.lastMessage = message;
    }
    
    public String getLastMessage() {
        return lastMessage;
    }
    
    public void reset() {
        currentUser = null;
        lastMessage = null;
    }
}
```

### Task 3: Base Page Object (10 minutes)

Create `src/test/java/com/training/bdd/pages/BasePage.java`:

```java
package com.training.bdd.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
 * Base class for all Page Objects.
 * Provides common functionality and wait utilities.
 */
public abstract class BasePage {
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final String BASE_URL = "https://the-internet.herokuapp.com";
    
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }
    
    // Navigation
    public void navigateTo(String path) {
        driver.get(BASE_URL + path);
    }
    
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    
    public String getTitle() {
        return driver.getTitle();
    }
    
    // Wait utilities
    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    protected void waitForUrl(String urlPart) {
        wait.until(ExpectedConditions.urlContains(urlPart));
    }
    
    protected void waitForTextInElement(By locator, String text) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }
    
    // Actions
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
    
    // Screenshot
    public byte[] takeScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
```

### Task 4: Login Page Object (10 minutes)

Create `src/test/java/com/training/bdd/pages/LoginPage.java`:

```java
package com.training.bdd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for the Login page.
 */
public class LoginPage extends BasePage {
    
    // Locators using @FindBy
    @FindBy(id = "username")
    private WebElement usernameField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;
    
    // Locator for flash message
    private By flashMessage = By.id("flash");
    
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    public void navigateToLogin() {
        navigateTo("/login");
    }
    
    public void enterUsername(String username) {
        // TODO: Implement
        usernameField.clear();
        usernameField.sendKeys(username);
    }
    
    public void enterPassword(String password) {
        // TODO: Implement
        passwordField.clear();
        passwordField.sendKeys(password);
    }
    
    public void clickLogin() {
        // TODO: Implement
        loginButton.click();
    }
    
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }
    
    public String getFlashMessage() {
        return getText(flashMessage);
    }
    
    public boolean isFlashMessageDisplayed() {
        return isDisplayed(flashMessage);
    }
    
    public boolean isOnLoginPage() {
        return getCurrentUrl().contains("/login");
    }
}
```

### Task 5: Create Step Definitions Using Page Objects (15 minutes)

Create `src/test/java/com/training/bdd/stepdefinitions/LoginSteps.java`:

```java
package com.training.bdd.stepdefinitions;

import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;

import com.training.bdd.context.TestContext;
import com.training.bdd.pages.LoginPage;
import com.training.bdd.pages.SecurePage;

public class LoginSteps {
    
    private TestContext context;
    private LoginPage loginPage;
    private SecurePage securePage;
    
    public LoginSteps() {
        context = TestContext.getInstance();
    }
    
    private LoginPage getLoginPage() {
        return context.getLoginPage();
    }
    
    private SecurePage getSecurePage() {
        return context.getSecurePage();
    }
    
    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        getLoginPage().navigateToLogin();
    }
    
    @When("the user enters username {string}")
    public void theUserEntersUsername(String username) {
        getLoginPage().enterUsername(username);
        context.setCurrentUser(username);
    }
    
    @When("the user enters password {string}")
    public void theUserEntersPassword(String password) {
        getLoginPage().enterPassword(password);
    }
    
    @When("the user clicks the login button")
    public void theUserClicksTheLoginButton() {
        getLoginPage().clickLogin();
    }
    
    @When("the user logs in with username {string} and password {string}")
    public void theUserLogsIn(String username, String password) {
        getLoginPage().login(username, password);
        context.setCurrentUser(username);
    }
    
    @Then("the user should be redirected to the secure area")
    public void theUserShouldBeRedirectedToSecureArea() {
        assertTrue(context.getDriver().getCurrentUrl().contains("/secure"),
            "User was not redirected to secure area");
    }
    
    @Then("the user should see a success message containing {string}")
    public void theUserShouldSeeSuccessMessage(String expectedMessage) {
        String actualMessage = getLoginPage().getFlashMessage();
        assertTrue(actualMessage.contains(expectedMessage),
            "Expected message containing '" + expectedMessage + "' but got '" + actualMessage + "'");
    }
    
    @Then("the user should remain on the login page")
    public void theUserShouldRemainOnLoginPage() {
        assertTrue(getLoginPage().isOnLoginPage(),
            "User should remain on login page");
    }
    
    @Then("the user should see an error message containing {string}")
    public void theUserShouldSeeErrorMessage(String expectedMessage) {
        String actualMessage = getLoginPage().getFlashMessage();
        assertTrue(actualMessage.contains(expectedMessage),
            "Expected error message containing '" + expectedMessage + "'");
    }
    
    @Then("the login should be {string}")
    public void theLoginShouldBe(String expectedResult) {
        if (expectedResult.equalsIgnoreCase("success")) {
            assertFalse(getLoginPage().isOnLoginPage());
        } else {
            assertTrue(getLoginPage().isOnLoginPage());
        }
    }
}
```

### Task 6: Updated Hooks with Context (5 minutes)

Update `src/test/java/com/training/bdd/hooks/Hooks.java`:

```java
package com.training.bdd.hooks;

import io.cucumber.java.*;
import com.training.bdd.context.TestContext;

public class Hooks {
    
    private TestContext context;
    
    public Hooks() {
        context = TestContext.getInstance();
    }
    
    @Before
    public void setUp(Scenario scenario) {
        System.out.println("Starting: " + scenario.getName());
        
        boolean headless = scenario.getSourceTagNames().contains("@headless");
        context.initializeDriver(headless);
        context.reset();
    }
    
    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = context.getDriver().getScreenshotAs(
                org.openqa.selenium.OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Failure Screenshot");
        }
        
        context.quitDriver();
        System.out.println("Finished: " + scenario.getName() + 
                          " - " + scenario.getStatus());
    }
}
```

---

## Complete Feature File

Create `src/test/resources/features/login.feature`:

```gherkin
@login @ui
Feature: User Login
  As a registered user
  I want to log in to the application
  So that I can access protected features

  Background:
    Given the user is on the login page

  @smoke @positive @critical
  Scenario: Successful login with valid credentials
    When the user logs in with username "tomsmith" and password "SuperSecretPassword!"
    Then the user should be redirected to the secure area
    And the user should see a success message containing "You logged into a secure area!"

  @regression @negative
  Scenario Outline: Failed login with invalid credentials
    When the user enters username "<username>"
    And the user enters password "<password>"
    And the user clicks the login button
    Then the user should remain on the login page
    And the user should see an error message containing "<error_message>"

    Examples: Invalid Credentials
      | username    | password             | error_message               |
      | tomsmith    | wrongpassword        | Your password is invalid!   |
      | invaliduser | SuperSecretPassword! | Your username is invalid!   |
      |             | SuperSecretPassword! | Your username is invalid!   |
      | tomsmith    |                      | Your password is invalid!   |
```

---

## Definition of Done

- [ ] Project structure created with all packages
- [ ] TestContext singleton manages WebDriver and page objects
- [ ] BasePage provides common functionality
- [ ] LoginPage extends BasePage with login-specific methods
- [ ] Step definitions use Page Objects via TestContext
- [ ] Hooks initialize and cleanup TestContext
- [ ] All scenarios in login.feature pass
- [ ] Screenshots captured on failure
- [ ] HTML report generated with screenshots

---

## Hints

<details>
<summary>Hint: SecurePage Implementation</summary>

```java
public class SecurePage extends BasePage {
    
    @FindBy(css = "a.button")
    private WebElement logoutButton;
    
    @FindBy(css = "h2")
    private WebElement heading;
    
    public SecurePage(WebDriver driver) {
        super(driver);
    }
    
    public void clickLogout() {
        logoutButton.click();
    }
    
    public String getHeading() {
        return heading.getText();
    }
    
    public boolean isOnSecurePage() {
        return getCurrentUrl().contains("/secure");
    }
}
```
</details>

<details>
<summary>Hint: Common Steps</summary>

```java
public class CommonSteps {
    
    private TestContext context;
    
    public CommonSteps() {
        context = TestContext.getInstance();
    }
    
    @Given("the user is on the {string} page")
    public void navigateToPage(String pageName) {
        context.getDriver().get(
            "https://the-internet.herokuapp.com/" + pageName);
    }
    
    @Then("the page title should be {string}")
    public void verifyPageTitle(String expectedTitle) {
        assertEquals(expectedTitle, context.getDriver().getTitle());
    }
}
```
</details>

