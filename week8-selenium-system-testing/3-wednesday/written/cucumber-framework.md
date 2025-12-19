# Cucumber Framework Architecture

## Learning Objectives
- Understand Cucumber framework architecture and project structure
- Configure Cucumber dependencies in Maven and Gradle
- Create and organize feature files effectively
- Write step definitions that map Gherkin to Java code
- Configure the test runner class for Cucumber execution
- Understand glue code and how Cucumber connects features to steps

## Why This Matters

Understanding Cucumber's architecture is essential for building maintainable BDD test suites. A well-structured Cucumber project enables:

- Easy collaboration between technical and non-technical team members
- Reusable step definitions across multiple features
- Scalable test automation that grows with your application
- Integration with your existing Java testing infrastructure

## The Concept

### Cucumber Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                  Cucumber Framework Architecture                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Feature Files (.feature)                                       │
│   ├── Written in Gherkin syntax                                 │
│   ├── Business-readable scenarios                               │
│   └── Stored in src/test/resources/features                     │
│              │                                                   │
│              ▼                                                   │
│   ┌────────────────────┐                                        │
│   │  Gherkin Parser    │  Parses feature files into AST        │
│   └────────────────────┘                                        │
│              │                                                   │
│              ▼                                                   │
│   Step Definitions (Java)                                        │
│   ├── Maps Gherkin steps to Java methods                        │
│   ├── Contains automation code                                  │
│   └── Stored in src/test/java/stepdefinitions                   │
│              │                                                   │
│              ▼                                                   │
│   ┌────────────────────┐                                        │
│   │  Test Runner       │  Configures and executes tests        │
│   │  (JUnit/TestNG)    │                                        │
│   └────────────────────┘                                        │
│              │                                                   │
│              ▼                                                   │
│   ┌────────────────────┐                                        │
│   │  Test Reports      │  HTML, JSON, JUnit XML                │
│   └────────────────────┘                                        │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Project Structure

Standard Cucumber-JVM project structure:

```
cucumber-project/
├── pom.xml (or build.gradle)
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/example/
│   │           ├── pages/              # Page Objects
│   │           │   ├── LoginPage.java
│   │           │   └── DashboardPage.java
│   │           └── utils/              # Utility classes
│   │               └── DriverFactory.java
│   └── test/
│       ├── java/
│       │   └── com/example/
│       │       ├── stepdefinitions/    # Step definitions
│       │       │   ├── LoginSteps.java
│       │       │   └── CommonSteps.java
│       │       ├── runners/            # Test runners
│       │       │   └── TestRunner.java
│       │       └── hooks/              # Setup/teardown
│       │           └── Hooks.java
│       └── resources/
│           ├── features/               # Feature files
│           │   ├── login.feature
│           │   └── checkout.feature
│           └── cucumber.properties     # Cucumber config
└── README.md
```

### Cucumber Dependencies (Maven)

**pom.xml:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>cucumber-bdd-tests</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <cucumber.version>7.14.0</cucumber.version>
        <selenium.version>4.15.0</selenium.version>
        <junit.version>5.10.0</junit.version>
    </properties>

    <dependencies>
        <!-- Cucumber Core -->
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-java</artifactId>
            <version>${cucumber.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- Cucumber JUnit Platform Engine -->
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-junit-platform-engine</artifactId>
            <version>${cucumber.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- JUnit Platform -->
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-suite</artifactId>
            <version>1.10.0</version>
            <scope>test</scope>
        </dependency>

        <!-- JUnit Jupiter -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- Selenium WebDriver -->
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
            <version>${selenium.version}</version>
        </dependency>

        <!-- WebDriver Manager -->
        <dependency>
            <groupId>io.github.bonigarcia</groupId>
            <artifactId>webdrivermanager</artifactId>
            <version>5.6.2</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.1</version>
                <configuration>
                    <properties>
                        <configurationParameters>
                            cucumber.junit-platform.naming-strategy=long
                        </configurationParameters>
                    </properties>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Cucumber Dependencies (Gradle)

**build.gradle:**
```groovy
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0-SNAPSHOT'

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // Cucumber
    testImplementation 'io.cucumber:cucumber-java:7.14.0'
    testImplementation 'io.cucumber:cucumber-junit-platform-engine:7.14.0'
    
    // JUnit Platform
    testImplementation 'org.junit.platform:junit-platform-suite:1.10.0'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
    
    // Selenium
    testImplementation 'org.seleniumhq.selenium:selenium-java:4.15.0'
    testImplementation 'io.github.bonigarcia:webdrivermanager:5.6.2'
}

test {
    useJUnitPlatform()
    systemProperty "cucumber.junit-platform.naming-strategy", "long"
}
```

### Feature Files

Feature files contain Gherkin scenarios:

**src/test/resources/features/login.feature:**
```gherkin
@login @smoke
Feature: User Login
  As a registered user
  I want to log into my account
  So that I can access my personalized dashboard

  Background:
    Given the application is running
    And I am on the login page

  @positive
  Scenario: Successful login with valid credentials
    When I enter username "john.doe@example.com"
    And I enter password "SecurePass123"
    And I click the login button
    Then I should be redirected to the dashboard
    And I should see welcome message "Welcome, John!"

  @negative
  Scenario: Failed login with invalid password
    When I enter username "john.doe@example.com"
    And I enter password "wrongpassword"
    And I click the login button
    Then I should see error message "Invalid credentials"
    And I should remain on the login page

  @negative @security
  Scenario: Account lockout after multiple failed attempts
    When I enter username "john.doe@example.com"
    And I fail to login 5 times
    Then my account should be locked
    And I should see message "Account locked. Please contact support."
```

### Step Definitions

Step definitions connect Gherkin to Java code:

**src/test/java/com/example/stepdefinitions/LoginSteps.java:**
```java
package com.example.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import static org.junit.jupiter.api.Assertions.*;

import com.example.pages.LoginPage;
import com.example.pages.DashboardPage;
import com.example.utils.TestContext;

public class LoginSteps {
    
    private TestContext context;
    private LoginPage loginPage;
    private DashboardPage dashboardPage;
    
    // Constructor injection for shared context
    public LoginSteps(TestContext context) {
        this.context = context;
        this.loginPage = new LoginPage(context.getDriver());
        this.dashboardPage = new DashboardPage(context.getDriver());
    }
    
    @Given("the application is running")
    public void the_application_is_running() {
        // Verify application health
        assertTrue(loginPage.isApplicationAvailable());
    }
    
    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        loginPage.navigateTo();
        assertTrue(loginPage.isDisplayed());
    }
    
    @When("I enter username {string}")
    public void i_enter_username(String username) {
        loginPage.enterUsername(username);
    }
    
    @When("I enter password {string}")
    public void i_enter_password(String password) {
        loginPage.enterPassword(password);
    }
    
    @When("I click the login button")
    public void i_click_the_login_button() {
        loginPage.clickLogin();
    }
    
    @When("I fail to login {int} times")
    public void i_fail_to_login_times(int attempts) {
        for (int i = 0; i < attempts; i++) {
            loginPage.enterUsername("john.doe@example.com");
            loginPage.enterPassword("wrongpassword" + i);
            loginPage.clickLogin();
        }
    }
    
    @Then("I should be redirected to the dashboard")
    public void i_should_be_redirected_to_the_dashboard() {
        assertTrue(dashboardPage.isDisplayed());
    }
    
    @Then("I should see welcome message {string}")
    public void i_should_see_welcome_message(String expectedMessage) {
        assertEquals(expectedMessage, dashboardPage.getWelcomeMessage());
    }
    
    @Then("I should see error message {string}")
    public void i_should_see_error_message(String expectedError) {
        assertEquals(expectedError, loginPage.getErrorMessage());
    }
    
    @Then("I should remain on the login page")
    public void i_should_remain_on_the_login_page() {
        assertTrue(loginPage.isDisplayed());
    }
    
    @Then("my account should be locked")
    public void my_account_should_be_locked() {
        assertTrue(loginPage.isAccountLocked());
    }
    
    @Then("I should see message {string}")
    public void i_should_see_message(String message) {
        assertEquals(message, loginPage.getMessage());
    }
}
```

### Runner Class

The runner class configures and executes Cucumber tests:

**src/test/java/com/example/runners/TestRunner.java:**
```java
package com.example.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.stepdefinitions,com.example.hooks")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/report.html, json:target/cucumber-reports/report.json")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@smoke and not @wip")
public class TestRunner {
    // This class serves as an entry point for running Cucumber tests
    // Configuration is done through annotations
}
```

**Alternative: Classic JUnit 4 Style Runner (still supported):**
```java
package com.example.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.example.stepdefinitions", "com.example.hooks"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/report.html",
        "json:target/cucumber-reports/report.json",
        "junit:target/cucumber-reports/report.xml"
    },
    tags = "@smoke and not @wip",
    monochrome = true,
    dryRun = false
)
public class TestRunner {
    // Empty class - configuration via annotations
}
```

### Glue Code

**Glue** is how Cucumber connects feature files to step definitions:

```
┌─────────────────────────────────────────────────────────────────┐
│                    Glue Code Connection                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Feature File:                                                   │
│  ┌─────────────────────────────────────────┐                    │
│  │ When I enter username "john@example.com" │                    │
│  └──────────────────┬──────────────────────┘                    │
│                     │                                            │
│                     │ Cucumber matches step pattern              │
│                     ▼                                            │
│  Step Definition:                                                │
│  ┌─────────────────────────────────────────┐                    │
│  │ @When("I enter username {string}")      │                    │
│  │ public void enterUsername(String user)  │──► Extracts "john" │
│  │ { loginPage.enterUsername(user); }      │    as parameter    │
│  └─────────────────────────────────────────┘                    │
│                                                                  │
│  Glue packages tell Cucumber where to find step definitions:    │
│  glue = "com.example.stepdefinitions"                           │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Important Glue Concepts:**

1. **Package Scanning** - Cucumber scans specified packages for step definitions
2. **Step Matching** - Gherkin steps match to annotated Java methods
3. **Parameter Extraction** - Values from steps are passed to method parameters
4. **Multiple Glue Packages** - Can specify multiple packages for hooks, steps, etc.

### Test Context (Dependency Injection)

Share state between step definition classes:

**src/test/java/com/example/utils/TestContext.java:**
```java
package com.example.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class TestContext {
    private WebDriver driver;
    private String baseUrl = "https://example.com";
    
    public TestContext() {
        // Initialize WebDriver
        WebDriverManager.chromedriver().setup();
        this.driver = new ChromeDriver();
        driver.manage().window().maximize();
    }
    
    public WebDriver getDriver() {
        return driver;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

### Cucumber with JUnit Integration

**src/test/java/com/example/hooks/Hooks.java:**
```java
package com.example.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Scenario;
import com.example.utils.TestContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {
    
    private TestContext context;
    
    public Hooks(TestContext context) {
        this.context = context;
    }
    
    @BeforeAll
    public static void globalSetup() {
        System.out.println("Starting test execution...");
        // Global setup like database seeding
    }
    
    @Before
    public void beforeScenario(Scenario scenario) {
        System.out.println("Starting scenario: " + scenario.getName());
    }
    
    @After
    public void afterScenario(Scenario scenario) {
        // Take screenshot on failure
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) context.getDriver())
                .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "failure-screenshot");
        }
        
        // Clean up
        context.tearDown();
    }
    
    @AfterAll
    public static void globalTeardown() {
        System.out.println("Test execution complete.");
        // Global cleanup
    }
}
```

### Configuration File

**src/test/resources/cucumber.properties:**
```properties
# Cucumber configuration
cucumber.publish.quiet=true
cucumber.plugin=pretty,html:target/cucumber-reports/report.html
cucumber.glue=com.example.stepdefinitions,com.example.hooks
cucumber.features=src/test/resources/features
cucumber.filter.tags=@smoke and not @wip
```

### Running Tests

**Maven:**
```bash
# Run all tests
mvn test

# Run specific tags
mvn test -Dcucumber.filter.tags="@smoke"

# Run with specific feature
mvn test -Dcucumber.features="src/test/resources/features/login.feature"
```

**Gradle:**
```bash
# Run all tests
gradle test

# Run specific tags
gradle test -Dcucumber.filter.tags="@smoke"
```

## Key Takeaways

1. **Feature files** contain Gherkin scenarios in `src/test/resources/features`
2. **Step definitions** map Gherkin to Java code in `src/test/java/.../stepdefinitions`
3. **Glue code** connects features to steps via package scanning
4. **Runner class** configures features, glue, plugins, and tags
5. **Test context** shares state across step definition classes
6. **Hooks** provide setup and teardown for scenarios

## Additional Resources

- [Cucumber-JVM Documentation](https://cucumber.io/docs/installation/java/) - Official installation guide
- [Cucumber Configuration](https://cucumber.io/docs/cucumber/configuration/) - Configuration reference
- [Cucumber JUnit Platform Engine](https://github.com/cucumber/cucumber-jvm/tree/main/cucumber-junit-platform-engine) - JUnit 5 integration

