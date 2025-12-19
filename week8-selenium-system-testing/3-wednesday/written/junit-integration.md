# Cucumber JUnit Integration

## Learning Objectives
- Integrate Cucumber with JUnit using @RunWith(Cucumber.class)
- Configure JUnit 5 integration with Cucumber
- Use assertions in step definitions effectively
- Combine Cucumber with JUnit assertions
- Manage the test lifecycle with JUnit and Cucumber

## Why This Matters

JUnit is the standard testing framework in the Java ecosystem. Integrating Cucumber with JUnit enables you to:
- Run Cucumber tests with familiar JUnit tooling
- Integrate with CI/CD systems that support JUnit
- Use JUnit assertions in step definitions
- Generate JUnit-compatible test reports
- Leverage IDE support for test execution

## The Concept

### JUnit 4 Integration (Classic)

The traditional approach using `@RunWith`:

```java
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.stepdefinitions",
    plugin = {"pretty", "html:target/cucumber-reports/report.html"}
)
public class TestRunner {
    // Empty class - configuration via annotations
}
```

**Required Dependencies (Maven):**
```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>
```

### JUnit 5 Integration (Modern)

The modern approach using JUnit Platform:

```java
import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, 
    value = "com.example.stepdefinitions")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, 
    value = "pretty, html:target/cucumber-reports/report.html")
public class TestRunner {
}
```

**Required Dependencies (Maven):**
```xml
<!-- Cucumber Core -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>

<!-- Cucumber JUnit Platform Engine -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>

<!-- JUnit Platform Suite -->
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-suite</artifactId>
    <version>1.10.0</version>
    <scope>test</scope>
</dependency>

<!-- JUnit Jupiter (for assertions) -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

### JUnit 4 vs JUnit 5 Comparison

| Aspect | JUnit 4 | JUnit 5 |
|--------|---------|---------|
| **Annotation** | `@RunWith(Cucumber.class)` | `@Suite` + `@IncludeEngines` |
| **Configuration** | `@CucumberOptions` | `@ConfigurationParameter` |
| **Dependency** | `cucumber-junit` | `cucumber-junit-platform-engine` |
| **Assertions** | `org.junit.Assert` | `org.junit.jupiter.api.Assertions` |
| **Support** | Legacy | Current/Recommended |

### Assertions in Step Definitions

#### Using JUnit 4 Assertions

```java
import static org.junit.Assert.*;
import io.cucumber.java.en.*;

public class LoginSteps {
    
    private LoginPage loginPage;
    private String actualMessage;
    
    @Then("I should see the welcome message {string}")
    public void shouldSeeWelcomeMessage(String expectedMessage) {
        assertEquals(expectedMessage, dashboardPage.getWelcomeMessage());
    }
    
    @Then("the login should fail")
    public void loginShouldFail() {
        assertTrue("Expected to remain on login page", 
                   loginPage.isDisplayed());
    }
    
    @Then("I should not see the admin panel")
    public void shouldNotSeeAdminPanel() {
        assertFalse("Admin panel should be hidden", 
                    dashboardPage.isAdminPanelVisible());
    }
    
    @Then("the error message should be {string}")
    public void errorMessageShouldBe(String expected) {
        assertNotNull("Error message should exist", 
                      loginPage.getErrorMessage());
        assertEquals(expected, loginPage.getErrorMessage());
    }
}
```

#### Using JUnit 5 Assertions

```java
import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.en.*;

public class LoginSteps {
    
    @Then("I should see the welcome message {string}")
    public void shouldSeeWelcomeMessage(String expectedMessage) {
        assertEquals(expectedMessage, dashboardPage.getWelcomeMessage(),
                    "Welcome message should match");
    }
    
    @Then("the login should fail")
    public void loginShouldFail() {
        assertTrue(loginPage.isDisplayed(),
                  "Expected to remain on login page");
    }
    
    @Then("the user count should be {int}")
    public void userCountShouldBe(int expected) {
        assertEquals(expected, userService.getCount(),
                    () -> "Expected " + expected + " users");
    }
    
    @Then("the operation should throw an exception")
    public void shouldThrowException() {
        assertThrows(IllegalStateException.class,
                    () -> orderService.processInvalidOrder());
    }
    
    @Then("all values should match")
    public void allValuesShouldMatch() {
        assertAll("User details",
            () -> assertEquals("John", user.getFirstName()),
            () -> assertEquals("Doe", user.getLastName()),
            () -> assertEquals("john@example.com", user.getEmail())
        );
    }
}
```

### Common Assertions Reference

**JUnit 5 Assertions:**

```java
import static org.junit.jupiter.api.Assertions.*;

// Equality
assertEquals(expected, actual);
assertEquals(expected, actual, "message");
assertNotEquals(unexpected, actual);

// Boolean
assertTrue(condition);
assertTrue(condition, "message");
assertFalse(condition);

// Null checks
assertNull(object);
assertNotNull(object);

// Same reference
assertSame(expected, actual);
assertNotSame(unexpected, actual);

// Arrays
assertArrayEquals(expectedArray, actualArray);

// Exceptions
assertThrows(ExpectedException.class, () -> code());
assertDoesNotThrow(() -> code());

// Grouped assertions
assertAll("group name",
    () -> assertEquals(1, value1),
    () -> assertEquals(2, value2),
    () -> assertEquals(3, value3)
);

// Timeout
assertTimeout(Duration.ofSeconds(5), () -> slowOperation());

// Fail explicitly
fail("This should not happen");
```

### Combining Cucumber with JUnit Assertions

**Complete Step Definition Example:**

```java
package com.example.stepdefinitions;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;

public class UserManagementSteps {
    
    private UserService userService;
    private User currentUser;
    private Exception lastException;
    
    public UserManagementSteps(TestContext context) {
        this.userService = context.getUserService();
    }
    
    @Given("the following users exist:")
    public void usersExist(DataTable dataTable) {
        List<Map<String, String>> users = dataTable.asMaps();
        for (Map<String, String> userData : users) {
            User user = new User(
                userData.get("username"),
                userData.get("email"),
                userData.get("role")
            );
            userService.create(user);
        }
        
        assertEquals(users.size(), userService.count(),
                    "All users should be created");
    }
    
    @When("I create a user with username {string}")
    public void createUser(String username) {
        try {
            currentUser = userService.create(new User(username));
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    @Then("the user should be created successfully")
    public void userCreatedSuccessfully() {
        assertNotNull(currentUser, "User should be created");
        assertNotNull(currentUser.getId(), "User should have an ID");
        assertNull(lastException, "No exception should occur");
    }
    
    @Then("the creation should fail with error {string}")
    public void creationShouldFail(String expectedError) {
        assertNotNull(lastException, "Exception should be thrown");
        assertTrue(lastException.getMessage().contains(expectedError),
                  "Error message should contain: " + expectedError);
    }
    
    @Then("the user details should be:")
    public void userDetailsShouldBe(DataTable dataTable) {
        Map<String, String> expected = dataTable.asMaps().get(0);
        
        assertAll("User details",
            () -> assertEquals(expected.get("username"), 
                              currentUser.getUsername()),
            () -> assertEquals(expected.get("email"), 
                              currentUser.getEmail()),
            () -> assertEquals(expected.get("role"), 
                              currentUser.getRole())
        );
    }
}
```

### Test Lifecycle Management

**Integration with Hooks:**

```java
package com.example.hooks;

import io.cucumber.java.*;
import org.junit.jupiter.api.Assumptions;
import static org.junit.jupiter.api.Assertions.*;

public class Hooks {
    
    private TestContext context;
    
    public Hooks(TestContext context) {
        this.context = context;
    }
    
    @Before
    public void setup(Scenario scenario) {
        // JUnit-style assumptions
        Assumptions.assumeTrue(
            System.getenv("TEST_ENV") != null,
            "TEST_ENV must be set"
        );
        
        System.out.println("Starting: " + scenario.getName());
    }
    
    @After
    public void teardown(Scenario scenario) {
        if (scenario.isFailed()) {
            // Assert that we can capture failure state
            assertNotNull(context.getDriver(),
                         "Driver should exist for failure capture");
            
            // Capture screenshot
            captureScreenshot(scenario);
        }
        
        // Cleanup with assertion
        context.cleanup();
        assertNull(context.getDriver(), 
                  "Driver should be null after cleanup");
    }
    
    private void captureScreenshot(Scenario scenario) {
        byte[] screenshot = context.captureScreenshot();
        scenario.attach(screenshot, "image/png", "failure");
    }
}
```

### Running Cucumber Tests

**Via IDE:**
- Right-click TestRunner → Run as JUnit Test

**Via Maven:**
```bash
mvn test
mvn test -Dtest=TestRunner
mvn test -Dcucumber.filter.tags="@smoke"
```

**Via Gradle:**
```bash
gradle test
gradle test --tests "TestRunner"
```

### JUnit Test Reports

Configure JUnit XML output for CI/CD:

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, 
    value = "pretty, " +
            "junit:target/cucumber-reports/junit.xml, " +
            "html:target/cucumber-reports/report.html")
public class TestRunner {
}
```

**Maven Surefire Configuration:**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.1</version>
    <configuration>
        <includes>
            <include>**/*Runner.java</include>
        </includes>
        <testFailureIgnore>false</testFailureIgnore>
    </configuration>
</plugin>
```

### Best Practices

```
┌─────────────────────────────────────────────────────────────────┐
│          Cucumber JUnit Integration Best Practices               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Assertions:                                                     │
│  ✓ Use descriptive assertion messages                           │
│  ✓ Prefer JUnit 5 assertions for new projects                   │
│  ✓ Use assertAll() for related checks                           │
│  ✓ One logical assertion per Then step                          │
│                                                                  │
│  Configuration:                                                  │
│  ✓ Use JUnit Platform Engine for JUnit 5 projects               │
│  ✓ Generate JUnit XML for CI integration                        │
│  ✓ Configure meaningful runner class names                      │
│                                                                  │
│  Organization:                                                   │
│  ✓ Separate runner classes for different test suites            │
│  ✓ Use cucumber.properties for defaults                         │
│  ✓ Override with command line for CI/CD                         │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Multiple Test Runners

```java
// Smoke Test Runner
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@smoke")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, 
    value = "html:target/smoke-report.html")
public class SmokeTestRunner {
}

// Regression Test Runner
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@regression")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, 
    value = "html:target/regression-report.html")
public class RegressionTestRunner {
}
```

## Key Takeaways

1. **JUnit 4** uses `@RunWith(Cucumber.class)` and `@CucumberOptions`
2. **JUnit 5** uses `@Suite`, `@IncludeEngines`, and `@ConfigurationParameter`
3. **Use JUnit 5** for new projects (modern, more features)
4. **Assertions** from JUnit work seamlessly in step definitions
5. **assertAll()** groups related assertions together
6. **JUnit XML** output enables CI/CD integration

## Additional Resources

- [Cucumber JUnit Documentation](https://cucumber.io/docs/cucumber/api/#junit) - Official JUnit integration guide
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) - Complete JUnit 5 documentation
- [JUnit 5 Assertions](https://junit.org/junit5/docs/current/api/org.junit.jupiter.api/org/junit/jupiter/api/Assertions.html) - Assertion methods reference

