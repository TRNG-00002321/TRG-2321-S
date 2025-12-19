# Cucumber Hooks and Tags

## Learning Objectives
- Implement Cucumber hooks: @Before, @After, @BeforeStep, @AfterStep
- Control hook execution order with priority
- Create conditional hooks using tag expressions
- Apply tags for test organization and filtering
- Use tag expressions for complex test selection
- Understand tag inheritance in feature files

## Why This Matters

Hooks and tags are essential for:
- **Test lifecycle management** - Setup and teardown
- **Test organization** - Categorize tests by type, priority, feature
- **Selective execution** - Run subsets of tests
- **Failure handling** - Screenshots, logging on failure
- **Environment management** - Browser setup, database state

## The Concept

### Cucumber Hooks Overview

Hooks are methods that run at specific points in the test lifecycle:

```
┌─────────────────────────────────────────────────────────────────┐
│                    Cucumber Hook Execution                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  @BeforeAll                                                      │
│       │                                                          │
│       ▼                                                          │
│  ┌── Feature Start ──┐                                          │
│  │                   │                                          │
│  │  @Before          │  ◄── Runs before EACH scenario           │
│  │       │           │                                          │
│  │  ┌────┴────┐      │                                          │
│  │  │Scenario │      │                                          │
│  │  │ @BeforeStep    │  ◄── Runs before EACH step               │
│  │  │ Step 1        │                                          │
│  │  │ @AfterStep    │  ◄── Runs after EACH step                │
│  │  │ @BeforeStep   │                                          │
│  │  │ Step 2        │                                          │
│  │  │ @AfterStep    │                                          │
│  │  └─────────┘     │                                          │
│  │       │          │                                          │
│  │  @After          │  ◄── Runs after EACH scenario            │
│  │                   │                                          │
│  └───────────────────┘                                          │
│       │                                                          │
│       ▼                                                          │
│  @AfterAll                                                       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### @Before Hook

Runs before each scenario:

```java
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {
    
    private WebDriver driver;
    
    @Before
    public void setup(Scenario scenario) {
        System.out.println("Starting scenario: " + scenario.getName());
        
        // Initialize WebDriver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }
}
```

### @After Hook

Runs after each scenario:

```java
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {
    
    private WebDriver driver;
    
    @After
    public void teardown(Scenario scenario) {
        // Take screenshot on failure
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "failure-screenshot");
        }
        
        // Clean up
        if (driver != null) {
            driver.quit();
        }
        
        System.out.println("Finished scenario: " + scenario.getName() + 
                          " - Status: " + scenario.getStatus());
    }
}
```

### @BeforeStep and @AfterStep Hooks

Run before/after each step:

```java
import io.cucumber.java.BeforeStep;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario;

public class Hooks {
    
    @BeforeStep
    public void beforeStep(Scenario scenario) {
        // Log step start
        System.out.println("Executing step in: " + scenario.getName());
    }
    
    @AfterStep
    public void afterStep(Scenario scenario) {
        // Take screenshot after each step (for debugging)
        if (Boolean.getBoolean("screenshot.each.step")) {
            byte[] screenshot = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "step-screenshot");
        }
    }
}
```

### @BeforeAll and @AfterAll Hooks

Run once before/after all scenarios:

```java
import io.cucumber.java.BeforeAll;
import io.cucumber.java.AfterAll;

public class Hooks {
    
    @BeforeAll
    public static void globalSetup() {
        System.out.println("========================================");
        System.out.println("Starting test execution...");
        System.out.println("========================================");
        
        // One-time setup
        // - Database seeding
        // - Test environment verification
        // - Report initialization
    }
    
    @AfterAll
    public static void globalTeardown() {
        System.out.println("========================================");
        System.out.println("Test execution complete.");
        System.out.println("========================================");
        
        // Final cleanup
        // - Report finalization
        // - Test data cleanup
    }
}
```

### Hook Ordering

Control hook execution order with `order` parameter:

```java
public class Hooks {
    
    // Lower order runs first for @Before
    @Before(order = 1)
    public void initializeDriver() {
        System.out.println("1. Initialize WebDriver");
    }
    
    @Before(order = 2)
    public void navigateToApp() {
        System.out.println("2. Navigate to application");
    }
    
    @Before(order = 3)
    public void loginIfNeeded() {
        System.out.println("3. Login if required");
    }
    
    // Higher order runs first for @After (reverse)
    @After(order = 3)
    public void logoutIfNeeded() {
        System.out.println("1. Logout if logged in");
    }
    
    @After(order = 2)
    public void captureEvidence() {
        System.out.println("2. Capture screenshots/logs");
    }
    
    @After(order = 1)
    public void closeDriver() {
        System.out.println("3. Close WebDriver");
    }
}
```

**Order Execution:**
- `@Before`: Lower order runs **first** (1, 2, 3)
- `@After`: Higher order runs **first** (3, 2, 1)

### Tags in Feature Files

Tags categorize and filter scenarios:

```gherkin
@feature-login
Feature: User Login

  @smoke @critical
  Scenario: Successful login
    Given I am on the login page
    When I enter valid credentials
    Then I should see the dashboard

  @regression @security
  Scenario: Failed login locks account
    Given I am on the login page
    When I enter wrong password 5 times
    Then my account should be locked

  @wip
  Scenario: Password reset flow
    Given I request password reset
    # Work in progress - incomplete
```

**Tag Placement:**

```gherkin
@feature-tag          # Applies to all scenarios in feature
Feature: Shopping Cart

  @all-scenarios      # Applies to all scenarios below
  Background:
    Given user is logged in

  @scenario-tag       # Applies to this scenario only
  Scenario: Add item
    ...

  @outline-tag        # Applies to all examples
  Scenario Outline: Remove items
    ...
    
    @example-tag      # Applies to this example set
    Examples:
      | item   |
      | Widget |
```

### Conditional Hooks

Run hooks only for scenarios with specific tags:

```java
public class Hooks {
    
    // Only run for @ui tagged scenarios
    @Before("@ui")
    public void setupBrowser() {
        driver = new ChromeDriver();
    }
    
    // Only run for @api tagged scenarios
    @Before("@api")
    public void setupApiClient() {
        apiClient = new ApiClient();
    }
    
    // Run for @database scenarios
    @Before("@database")
    public void seedDatabase() {
        DatabaseSeeder.seed();
    }
    
    // Cleanup only for @database scenarios
    @After("@database")
    public void cleanDatabase() {
        DatabaseSeeder.cleanup();
    }
    
    // Complex tag expressions
    @Before("@ui and @smoke")
    public void setupUiSmoke() {
        // Only for UI smoke tests
    }
    
    @Before("@regression and not @slow")
    public void setupFastRegression() {
        // For regression tests that aren't slow
    }
}
```

### Tag Expressions

Filter scenarios using boolean logic:

```java
// Single tag
@Before("@smoke")

// AND - both tags required
@Before("@smoke and @login")

// OR - either tag
@Before("@smoke or @regression")

// NOT - exclude tag
@Before("not @wip")

// Complex expressions
@Before("(@smoke or @regression) and not @slow")
@Before("@feature-login and (@positive or @negative)")
```

**Tag Expression Operators:**

| Operator | Description | Example |
|----------|-------------|---------|
| `and` | Both conditions | `@smoke and @login` |
| `or` | Either condition | `@smoke or @regression` |
| `not` | Negate condition | `not @wip` |
| `()` | Group conditions | `(@smoke or @regression) and not @slow` |

### Running Tests by Tags

**Maven:**
```bash
mvn test -Dcucumber.filter.tags="@smoke"
mvn test -Dcucumber.filter.tags="@smoke and not @wip"
mvn test -Dcucumber.filter.tags="@regression or @smoke"
```

**Runner Configuration:**
```java
@CucumberOptions(
    tags = "@smoke and not @wip"
)
```

### Tag Inheritance

Tags cascade from feature to scenarios:

```gherkin
@feature-level           # Applied to ALL scenarios
Feature: User Management

  @scenario-level        # This scenario has: @feature-level, @scenario-level
  Scenario: Create user
    ...

  Scenario: Delete user  # This scenario has: @feature-level
    ...
```

### Complete Hooks Example

```java
package com.example.hooks;

import io.cucumber.java.*;
import org.openqa.selenium.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

public class Hooks {
    
    private static final ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();
    
    // ==================== GLOBAL HOOKS ====================
    
    @BeforeAll
    public static void beforeAllScenarios() {
        System.out.println("=== Test Suite Starting ===");
        // Global initialization
    }
    
    @AfterAll
    public static void afterAllScenarios() {
        System.out.println("=== Test Suite Complete ===");
    }
    
    // ==================== SCENARIO HOOKS ====================
    
    @Before(order = 0)
    public void logScenarioStart(Scenario scenario) {
        System.out.println("Starting: " + scenario.getName());
        System.out.println("Tags: " + scenario.getSourceTagNames());
    }
    
    @Before(value = "@ui", order = 1)
    public void setupWebDriver() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driverThread.set(driver);
    }
    
    @Before(value = "@ui and @login-required", order = 2)
    public void performLogin() {
        getDriver().get("https://example.com/login");
        // Perform login steps
    }
    
    @After(value = "@ui", order = 1)
    public void captureScreenshotOnFailure(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) getDriver())
                .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "failure-screenshot");
        }
    }
    
    @After(value = "@ui", order = 0)
    public void quitWebDriver() {
        WebDriver driver = driverThread.get();
        if (driver != null) {
            driver.quit();
            driverThread.remove();
        }
    }
    
    @After(order = 100)  // Runs last
    public void logScenarioEnd(Scenario scenario) {
        System.out.println("Finished: " + scenario.getName());
        System.out.println("Status: " + scenario.getStatus());
    }
    
    // ==================== STEP HOOKS ====================
    
    @BeforeStep("@debug")
    public void beforeEachStep(Scenario scenario) {
        System.out.println("  [DEBUG] Executing step...");
    }
    
    @AfterStep("@debug")
    public void afterEachStep(Scenario scenario) {
        byte[] screenshot = ((TakesScreenshot) getDriver())
            .getScreenshotAs(OutputType.BYTES);
        scenario.attach(screenshot, "image/png", "step-screenshot");
    }
    
    // ==================== HELPER METHODS ====================
    
    public static WebDriver getDriver() {
        return driverThread.get();
    }
}
```

### Common Tag Patterns

```gherkin
# Test types
@smoke          # Quick sanity tests
@regression     # Full regression suite
@integration    # Integration tests
@e2e            # End-to-end tests

# Priority
@critical       # Must-pass tests
@high           # High priority
@medium         # Medium priority
@low            # Low priority

# Status
@wip            # Work in progress
@skip           # Temporarily skip
@flaky          # Known flaky tests

# Feature areas
@login          # Login functionality
@checkout       # Checkout functionality
@search         # Search functionality

# Environment
@prod-ready     # Safe for production
@staging-only   # Only run on staging

# Browser/Platform
@chrome-only    # Chrome-specific tests
@mobile         # Mobile tests
```

### Best Practices

```
┌─────────────────────────────────────────────────────────────────┐
│              Hooks and Tags Best Practices                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Hooks:                                                          │
│  ✓ Use @Before for setup, @After for cleanup                    │
│  ✓ Take screenshots on failure in @After                        │
│  ✓ Use order parameter for hook dependencies                    │
│  ✓ Keep hooks focused on single responsibility                  │
│  ✗ Don't put business logic in hooks                            │
│                                                                  │
│  Tags:                                                           │
│  ✓ Use consistent naming conventions                            │
│  ✓ Keep tag names descriptive                                   │
│  ✓ Apply tags at appropriate level (feature/scenario)           │
│  ✓ Use conditional hooks for tag-specific setup                 │
│  ✗ Don't over-tag (2-4 tags per scenario is enough)             │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Key Takeaways

1. **@Before/@After** run before/after each scenario
2. **@BeforeStep/@AfterStep** run before/after each step
3. **@BeforeAll/@AfterAll** run once per test run
4. **Hook order** controls execution sequence
5. **Tags** organize and filter tests
6. **Tag expressions** enable complex filtering logic

## Additional Resources

- [Cucumber Hooks Reference](https://cucumber.io/docs/cucumber/api/#hooks) - Official documentation
- [Cucumber Tags Reference](https://cucumber.io/docs/cucumber/api/#tags) - Tag syntax and usage
- [Tag Expressions](https://cucumber.io/docs/cucumber/api/#tag-expressions) - Boolean expressions

