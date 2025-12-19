# Exercise 4: Hooks and Tags

## Objective

Implement Cucumber hooks for test setup and teardown, and use tags to organize and filter test execution.

## Learning Goals

- Implement @Before and @After hooks
- Use conditional hooks with tags
- Capture screenshots on failure
- Manage WebDriver lifecycle with hooks
- Apply tags for test organization and filtering
- Use tag expressions for complex filtering

## Time Estimate

45 minutes

---

## Core Tasks

### Task 1: Basic Hooks Implementation (15 minutes)

Create `src/test/java/com/training/cucumber/hooks/Hooks.java`:

```java
package com.training.cucumber.hooks;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Hooks {
    
    private static WebDriver driver;
    
    /**
     * Runs before each scenario.
     * Sets up the WebDriver and navigates to base URL if needed.
     */
    @Before(order = 1)
    public void setUp(Scenario scenario) {
        System.out.println("Starting scenario: " + scenario.getName());
        
        // TODO: Implement WebDriver setup
        // 1. Set up WebDriverManager
        // 2. Configure ChromeOptions (implicit wait, window size)
        // 3. Create ChromeDriver instance
        
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // Add your options here
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }
    
    /**
     * Runs before scenarios tagged with @headless.
     * Configures headless browser mode.
     */
    @Before(value = "@headless", order = 0)
    public void setUpHeadless() {
        // TODO: Implement headless setup
        // This should run BEFORE the regular setup
        System.out.println("Configuring headless mode...");
    }
    
    /**
     * Runs before scenarios tagged with @slow.
     * Increases timeout values.
     */
    @Before("@slow")
    public void configureSlowTest() {
        // TODO: Implement slow test configuration
        // Set longer implicit wait, page load timeout
    }
    
    /**
     * Runs after each scenario.
     * Cleans up WebDriver and captures screenshot on failure.
     */
    @After
    public void tearDown(Scenario scenario) {
        // TODO: Implement teardown
        // 1. Check if scenario failed
        // 2. If failed, capture screenshot
        // 3. Attach screenshot to report
        // 4. Quit WebDriver
        
        if (scenario.isFailed()) {
            captureScreenshot(scenario);
        }
        
        if (driver != null) {
            driver.quit();
        }
        
        System.out.println("Finished scenario: " + scenario.getName() + 
                          " - Status: " + scenario.getStatus());
    }
    
    /**
     * Captures screenshot and attaches to Cucumber report.
     */
    private void captureScreenshot(Scenario scenario) {
        // TODO: Implement screenshot capture
        // 1. Take screenshot as byte array
        // 2. Attach to scenario with name
        
        final byte[] screenshot = ((TakesScreenshot) driver)
            .getScreenshotAs(OutputType.BYTES);
        scenario.attach(screenshot, "image/png", scenario.getName());
    }
    
    /**
     * Optional: Runs after each step.
     * Useful for debugging or capturing step-level screenshots.
     */
    @AfterStep
    public void afterStep(Scenario scenario) {
        // TODO: Optional - implement step-level logging
        // Be careful: this can slow down tests significantly
    }
    
    /**
     * Provides access to WebDriver for step definitions.
     */
    public static WebDriver getDriver() {
        return driver;
    }
}
```

### Task 2: Conditional Hooks (10 minutes)

Add these conditional hooks to your `Hooks.java`:

```java
/**
 * Runs only for scenarios tagged with @database.
 * Sets up database connection and test data.
 */
@Before("@database")
public void setUpDatabase(Scenario scenario) {
    System.out.println("Setting up database for: " + scenario.getName());
    // TODO: Implement database setup
    // 1. Connect to test database
    // 2. Clear test data
    // 3. Insert required fixtures
}

/**
 * Runs only for scenarios tagged with @database.
 * Cleans up database after test.
 */
@After("@database")
public void tearDownDatabase(Scenario scenario) {
    System.out.println("Cleaning up database after: " + scenario.getName());
    // TODO: Implement database cleanup
    // 1. Delete test data
    // 2. Close connection
}

/**
 * Runs for scenarios tagged with @api.
 * Sets up API test configuration.
 */
@Before("@api")
public void setUpApi() {
    // TODO: Implement API setup
    // 1. Configure base URL
    // 2. Set up authentication
    // 3. Initialize REST client
}

/**
 * Runs for scenarios tagged with both @login AND @admin.
 * Sets up admin user context.
 */
@Before("@login and @admin")
public void setUpAdminLogin() {
    System.out.println("Setting up admin login context");
    // TODO: Implement admin login setup
}

/**
 * Runs for scenarios tagged with @login but NOT @admin.
 * Sets up regular user context.
 */
@Before("@login and not @admin")
public void setUpRegularLogin() {
    System.out.println("Setting up regular user login context");
    // TODO: Implement regular user login setup
}
```

### Task 3: Tag-Based Feature Organization (10 minutes)

Update your feature files with comprehensive tagging:

**features/tagged_login.feature:**
```gherkin
@login @authentication
Feature: User Login with Tags

  @smoke @critical @p1
  Scenario: Successful login is critical path
    Given the user is on the login page
    When the user logs in with valid credentials
    Then the user should see the dashboard

  @regression @p2
  Scenario: Login with remember me
    Given the user is on the login page
    When the user checks "Remember me"
    And the user logs in with valid credentials
    Then the session should persist

  @security @negative
  Scenario: Account lockout after failed attempts
    Given the user is on the login page
    When the user fails login 5 times
    Then the account should be locked
    And a security email should be sent

  @wip
  Scenario: Biometric login (work in progress)
    # This test is not yet implemented
    Given the user has biometric enabled
    When the user authenticates with fingerprint
    Then the login should succeed

  @slow @integration
  Scenario: Login syncs with external identity provider
    Given the external IDP is available
    When the user logs in via SSO
    Then the user profile should be synchronized
```

### Task 4: Tag Execution Strategies (5 minutes)

Create a test execution script or document different tag combinations:

```bash
# Run only smoke tests
mvn test -Dcucumber.filter.tags="@smoke"

# Run login tests that are NOT work in progress
mvn test -Dcucumber.filter.tags="@login and not @wip"

# Run high priority tests (P1 or P2)
mvn test -Dcucumber.filter.tags="@p1 or @p2"

# Run regression but skip slow tests
mvn test -Dcucumber.filter.tags="@regression and not @slow"

# Run security tests that need database
mvn test -Dcucumber.filter.tags="@security and @database"

# Complex: Run critical smoke OR all regression except slow
mvn test -Dcucumber.filter.tags="(@smoke and @critical) or (@regression and not @slow)"
```

### Task 5: Update Step Definitions to Use Hooks (5 minutes)

Modify step definitions to use the shared WebDriver from Hooks:

```java
package com.training.cucumber.stepdefinitions;

import com.training.cucumber.hooks.Hooks;
import io.cucumber.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginSteps {
    
    // Get driver from Hooks instead of creating new one
    private WebDriver getDriver() {
        return Hooks.getDriver();
    }
    
    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        getDriver().get("https://the-internet.herokuapp.com/login");
    }
    
    @When("the user logs in with valid credentials")
    public void loginWithValidCredentials() {
        WebDriver driver = getDriver();
        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }
    
    // ... rest of steps use getDriver()
}
```

---

## Hook Execution Order

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    Hook Execution Order                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  @Before (order = 0)    ← Lowest number runs first                      │
│  @Before (order = 1)                                                     │
│  @Before (order = 10)   ← Higher numbers run later                      │
│           │                                                              │
│           ▼                                                              │
│     Scenario Steps                                                       │
│           │                                                              │
│           ▼                                                              │
│  @After (order = 10)    ← Higher numbers run first (LIFO)               │
│  @After (order = 1)                                                      │
│  @After (order = 0)     ← Lowest number runs last                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Tag Expression Reference

| Expression | Meaning |
|------------|---------|
| `@smoke` | Has @smoke tag |
| `@smoke and @login` | Has both tags |
| `@smoke or @regression` | Has either tag |
| `not @wip` | Does NOT have @wip |
| `@login and not @slow` | Has @login but not @slow |
| `(@a or @b) and @c` | (a OR b) AND c |

---

## Definition of Done

- [ ] Basic hooks implemented (@Before, @After)
- [ ] Screenshot captured on scenario failure
- [ ] Conditional hooks working for @headless, @slow, @database
- [ ] Tag expressions using AND, OR, NOT
- [ ] Feature files organized with meaningful tags
- [ ] Step definitions use shared WebDriver from Hooks
- [ ] Tests can be filtered by tags from command line

---

## Hints

<details>
<summary>Hint: Attaching Screenshots to Report</summary>

```java
// Capture screenshot as bytes
byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

// Attach to Cucumber report
scenario.attach(screenshot, "image/png", "Screenshot on failure");
```
</details>

<details>
<summary>Hint: Hook Order for Headless</summary>

The headless hook should run BEFORE the driver setup hook:
```java
@Before(value = "@headless", order = 0)  // Runs first
public void configureHeadless() {
    // Set system property or static flag
}

@Before(order = 1)  // Runs second, reads the configuration
public void setUp() {
    // Use headless config if set
}
```
</details>

