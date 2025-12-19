# Exercise 5: Week 8 Capstone Project

## Objective

Create a comprehensive test automation project that synthesizes the entire week's learning: Selenium Python, BDD with Cucumber/Behave, and Playwright visual testing with tracing.

## Learning Goals

- Integrate multiple testing frameworks in one project
- Apply BDD approach to test organization
- Implement visual regression testing
- Use tracing for debugging and analysis
- Create a production-ready test architecture

## Time Estimate

90 minutes

---

## The Capstone Challenge

Build a test automation suite for **The Internet** application that demonstrates mastery of:

1. **BDD Test Design** - Gherkin feature files
2. **Playwright Automation** - Modern browser testing
3. **Visual Testing** - Screenshot comparisons
4. **Tracing** - Debugging capabilities
5. **Reporting** - Comprehensive test reports

---

## Project Structure

```
week8-capstone/
├── src/
│   └── test/
│       ├── java/
│       │   └── com/training/capstone/
│       │       ├── config/
│       │       │   └── TestConfig.java
│       │       ├── pages/
│       │       │   ├── BasePage.java
│       │       │   ├── LoginPage.java
│       │       │   ├── SecurePage.java
│       │       │   ├── DynamicLoadingPage.java
│       │       │   └── CheckboxPage.java
│       │       ├── stepdefs/
│       │       │   ├── Hooks.java
│       │       │   ├── LoginSteps.java
│       │       │   └── VisualSteps.java
│       │       ├── runners/
│       │       │   └── TestRunner.java
│       │       └── utils/
│       │           ├── PlaywrightManager.java
│       │           └── VisualTestUtils.java
│       └── resources/
│           └── features/
│               ├── login.feature
│               ├── visual_regression.feature
│               └── dynamic_content.feature
├── visual-baselines/
├── traces/
├── reports/
├── pom.xml
└── README.md
```

---

## Core Tasks

### Task 1: Project Setup (15 minutes)

**pom.xml:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.training</groupId>
    <artifactId>week8-capstone</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <playwright.version>1.40.0</playwright.version>
        <cucumber.version>7.14.0</cucumber.version>
        <junit.version>5.10.0</junit.version>
    </properties>
    
    <dependencies>
        <!-- Playwright -->
        <dependency>
            <groupId>com.microsoft.playwright</groupId>
            <artifactId>playwright</artifactId>
            <version>${playwright.version}</version>
        </dependency>
        
        <!-- Cucumber -->
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-java</artifactId>
            <version>${cucumber.version}</version>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-junit-platform-engine</artifactId>
            <version>${cucumber.version}</version>
        </dependency>
        
        <!-- JUnit -->
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-suite</artifactId>
            <version>1.10.0</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
        </dependency>
    </dependencies>
</project>
```

### Task 2: Playwright Manager with Tracing (15 minutes)

Create `PlaywrightManager.java`:

```java
package com.training.capstone.utils;

import com.microsoft.playwright.*;

import java.nio.file.*;

/**
 * Manages Playwright browser lifecycle with tracing support.
 */
public class PlaywrightManager {
    
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;
    private static boolean tracingEnabled = true;
    
    public static void initialize() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(true));
    }
    
    public static Page createPage(String scenarioName) throws Exception {
        // Create directories
        Files.createDirectories(Paths.get("traces"));
        Files.createDirectories(Paths.get("visual-baselines"));
        
        // Create context with video recording
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1920, 1080)
            .setRecordVideoDir(Paths.get("videos/")));
        
        // Start tracing
        if (tracingEnabled) {
            context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        }
        
        page = context.newPage();
        return page;
    }
    
    public static Page getPage() {
        return page;
    }
    
    public static void closePage(String scenarioName, boolean failed) throws Exception {
        // Stop tracing and save on failure
        if (tracingEnabled) {
            String traceFile = "traces/" + scenarioName.replace(" ", "_") + ".zip";
            context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get(traceFile)));
            
            if (failed) {
                System.out.println("Trace saved: " + traceFile);
                System.out.println("View with: npx playwright show-trace " + traceFile);
            }
        }
        
        context.close();
    }
    
    public static void shutdown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
    
    public static void setTracingEnabled(boolean enabled) {
        tracingEnabled = enabled;
    }
}
```

### Task 3: Feature Files (15 minutes)

**features/login.feature:**
```gherkin
@login @smoke
Feature: User Login
  As a registered user
  I want to log in to the application
  So that I can access protected features

  @positive
  Scenario: Successful login with valid credentials
    Given I am on the login page
    When I enter username "tomsmith"
    And I enter password "SuperSecretPassword!"
    And I click the login button
    Then I should be on the secure area
    And I should see success message "You logged into a secure area!"
    And the page should visually match "secure-page" baseline

  @negative
  Scenario Outline: Failed login with invalid credentials
    Given I am on the login page
    When I enter username "<username>"
    And I enter password "<password>"
    And I click the login button
    Then I should see error message "<error>"

    Examples:
      | username    | password    | error                      |
      | tomsmith    | wrong       | Your password is invalid!  |
      | invalid     | password    | Your username is invalid!  |

  @logout
  Scenario: Complete login and logout flow
    Given I am on the login page
    When I login as "tomsmith" with password "SuperSecretPassword!"
    And I click logout
    Then I should be back on the login page
    And I should see message "You logged out"
```

**features/visual_regression.feature:**
```gherkin
@visual
Feature: Visual Regression Testing
  As a QA engineer
  I want to verify pages haven't changed visually
  So that I can catch unintended UI changes

  @baseline
  Scenario: Homepage visual baseline
    Given I navigate to the homepage
    When the page has fully loaded
    Then the page should visually match "homepage" baseline

  @baseline
  Scenario: Login page visual baseline
    Given I am on the login page
    When the page has fully loaded
    Then the page should visually match "login-page" baseline

  @element
  Scenario: Login form element baseline
    Given I am on the login page
    When the page has fully loaded
    Then the login form should visually match "login-form" baseline
```

**features/dynamic_content.feature:**
```gherkin
@dynamic
Feature: Dynamic Content Handling
  As a tester
  I want to verify dynamic content loads correctly
  So that I can ensure page functionality

  Scenario: Dynamic loading with hidden element
    Given I am on dynamic loading page 1
    When I click the start button
    Then I should see "Hello World!" after loading

  Scenario: Dynamic loading with added element
    Given I am on dynamic loading page 2
    When I click the start button
    Then I should see "Hello World!" after loading
    And a trace should be captured for this scenario
```

### Task 4: Cucumber Hooks with Playwright (15 minutes)

Create `Hooks.java`:

```java
package com.training.capstone.stepdefs;

import com.training.capstone.utils.PlaywrightManager;
import io.cucumber.java.*;

/**
 * Cucumber hooks for Playwright lifecycle management.
 */
public class Hooks {
    
    @BeforeAll
    public static void beforeAll() {
        PlaywrightManager.initialize();
    }
    
    @AfterAll
    public static void afterAll() {
        PlaywrightManager.shutdown();
    }
    
    @Before
    public void before(Scenario scenario) throws Exception {
        PlaywrightManager.createPage(scenario.getName());
    }
    
    @After
    public void after(Scenario scenario) throws Exception {
        boolean failed = scenario.isFailed();
        
        if (failed) {
            // Capture screenshot on failure
            byte[] screenshot = PlaywrightManager.getPage().screenshot();
            scenario.attach(screenshot, "image/png", "Failure Screenshot");
        }
        
        PlaywrightManager.closePage(scenario.getName(), failed);
    }
}
```

### Task 5: Step Definitions (15 minutes)

Create `LoginSteps.java`:

```java
package com.training.capstone.stepdefs;

import com.microsoft.playwright.*;
import com.training.capstone.pages.LoginPage;
import com.training.capstone.utils.PlaywrightManager;
import io.cucumber.java.en.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginSteps {
    
    private LoginPage loginPage;
    
    private Page getPage() {
        return PlaywrightManager.getPage();
    }
    
    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        loginPage = new LoginPage(getPage());
        loginPage.navigate();
    }
    
    @When("I enter username {string}")
    public void iEnterUsername(String username) {
        loginPage.enterUsername(username);
    }
    
    @When("I enter password {string}")
    public void iEnterPassword(String password) {
        loginPage.enterPassword(password);
    }
    
    @When("I click the login button")
    public void iClickTheLoginButton() {
        loginPage.clickLogin();
    }
    
    @When("I login as {string} with password {string}")
    public void iLoginAsWithPassword(String username, String password) {
        loginPage.login(username, password);
    }
    
    @When("I click logout")
    public void iClickLogout() {
        getPage().click("a.button");
    }
    
    @Then("I should be on the secure area")
    public void iShouldBeOnTheSecureArea() {
        assertThat(getPage()).hasURL(java.util.regex.Pattern.compile(".*/secure"));
    }
    
    @Then("I should see success message {string}")
    public void iShouldSeeSuccessMessage(String message) {
        assertThat(getPage().locator("#flash")).containsText(message);
    }
    
    @Then("I should see error message {string}")
    public void iShouldSeeErrorMessage(String message) {
        assertThat(getPage().locator("#flash")).containsText(message);
    }
    
    @Then("I should be back on the login page")
    public void iShouldBeBackOnTheLoginPage() {
        assertThat(getPage()).hasURL(java.util.regex.Pattern.compile(".*/login"));
    }
    
    @Then("I should see message {string}")
    public void iShouldSeeMessage(String message) {
        assertThat(getPage().locator("#flash")).containsText(message);
    }
}
```

Create `VisualSteps.java`:

```java
package com.training.capstone.stepdefs;

import com.microsoft.playwright.*;
import com.training.capstone.utils.PlaywrightManager;
import com.training.capstone.utils.VisualTestUtils;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

public class VisualSteps {
    
    private Page getPage() {
        return PlaywrightManager.getPage();
    }
    
    @Given("I navigate to the homepage")
    public void iNavigateToTheHomepage() {
        getPage().navigate("https://the-internet.herokuapp.com/");
    }
    
    @When("the page has fully loaded")
    public void thePageHasFullyLoaded() {
        getPage().waitForLoadState();
    }
    
    @Then("the page should visually match {string} baseline")
    public void thePageShouldVisuallyMatchBaseline(String baselineName) throws Exception {
        boolean matches = VisualTestUtils.compareWithBaseline(
            getPage(), 
            baselineName,
            new Page.ScreenshotOptions().setFullPage(true)
        );
        assertTrue(matches, "Page should match baseline: " + baselineName);
    }
    
    @Then("the login form should visually match {string} baseline")
    public void theLoginFormShouldVisuallyMatchBaseline(String baselineName) throws Exception {
        Locator form = getPage().locator("#login");
        byte[] screenshot = form.screenshot();
        
        // Use element-level comparison
        boolean matches = VisualTestUtils.compareElementWithBaseline(
            screenshot, baselineName
        );
        assertTrue(matches, "Element should match baseline: " + baselineName);
    }
}
```

### Task 6: Test Runner and Execution (15 minutes)

Create `TestRunner.java`:

```java
package com.training.capstone.runners;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.training.capstone.stepdefs")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, 
    value = "pretty, html:reports/cucumber.html, json:reports/cucumber.json")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @wip")
public class TestRunner {
}
```

---

## Running the Capstone

```bash
# Install Playwright browsers
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"

# Run all tests
mvn test

# Run specific tags
mvn test -Dcucumber.filter.tags="@smoke"

# Run with visual tests
mvn test -Dcucumber.filter.tags="@visual"

# View trace file (after failure)
npx playwright show-trace traces/scenario_name.zip
```

---

## Definition of Done

- [ ] Project structure complete
- [ ] All feature files created with meaningful scenarios
- [ ] Playwright Manager handles browser lifecycle
- [ ] Tracing enabled and captures on failure
- [ ] Visual testing implemented with baselines
- [ ] All Cucumber hooks working
- [ ] Page Objects implemented
- [ ] All tests pass
- [ ] HTML report generated
- [ ] README documents how to run

---

## Bonus Challenges

1. **Add Video Recording** - Capture video of test execution
2. **Network Mocking** - Mock API responses for specific tests
3. **Parallel Execution** - Configure parallel test execution
4. **CI/CD Integration** - Add GitHub Actions workflow
5. **Allure Reporting** - Integrate Allure for rich reports

---

## Grading Rubric

| Criteria | Points |
|----------|--------|
| Project Structure | 10 |
| Feature Files (3 complete) | 15 |
| Playwright Integration | 20 |
| Visual Testing | 15 |
| Tracing Implementation | 10 |
| Page Objects | 10 |
| Step Definitions | 10 |
| All Tests Pass | 10 |
| **Total** | **100** |

