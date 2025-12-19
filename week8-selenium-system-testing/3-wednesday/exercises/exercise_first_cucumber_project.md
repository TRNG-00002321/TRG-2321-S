# Exercise 1: First Cucumber Project

## Objective

Set up a complete Cucumber project with Maven, write your first feature file and step definitions, and execute tests for a login scenario.

## Learning Goals

- Configure Cucumber with Maven and JUnit 5
- Create proper project structure for Cucumber
- Write your first Gherkin feature file
- Implement step definitions in Java
- Run Cucumber tests from command line and IDE

## Time Estimate

45 minutes

---

## Core Tasks

### Task 1: Project Structure (10 minutes)

Create the following project structure:

```
cucumber-exercises/
├── pom.xml
├── src/
│   ├── main/java/
│   │   └── com/training/cucumber/
│   │       └── (empty for now)
│   └── test/
│       ├── java/
│       │   └── com/training/cucumber/
│       │       ├── runners/
│       │       │   └── TestRunner.java
│       │       └── stepdefinitions/
│       │           └── LoginSteps.java
│       └── resources/
│           └── features/
│               └── login.feature
└── README.md
```

### Task 2: Create Feature File (10 minutes)

Create `src/test/resources/features/login.feature`:

```gherkin
@login
Feature: User Authentication
  As a registered user of the application
  I want to be able to log in to my account
  So that I can access my personalized content

  Background:
    Given the user is on the login page

  @smoke @positive
  Scenario: Successful login with valid credentials
    When the user enters username "tomsmith"
    And the user enters password "SuperSecretPassword!"
    And the user clicks the login button
    Then the user should be redirected to the secure area
    And the user should see a success message containing "You logged into a secure area!"

  @negative
  Scenario: Failed login with invalid password
    When the user enters username "tomsmith"
    And the user enters password "wrongpassword"
    And the user clicks the login button
    Then the user should remain on the login page
    And the user should see an error message containing "Your password is invalid!"

  @negative
  Scenario: Failed login with invalid username
    # TODO: Implement this scenario
    # Use username "invaliduser" and valid password format

  @negative
  Scenario: Failed login with empty credentials
    # TODO: Implement this scenario
    # Try to login with empty username and password
```

### Task 3: Implement Step Definitions (15 minutes)

Create `src/test/java/com/training/cucumber/stepdefinitions/LoginSteps.java`:

```java
package com.training.cucumber.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import static org.junit.jupiter.api.Assertions.*;

public class LoginSteps {
    
    private WebDriver driver;
    private static final String BASE_URL = "https://the-internet.herokuapp.com";
    
    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        // TODO: Implement this step
        // 1. Set up WebDriverManager for Chrome
        // 2. Initialize ChromeDriver
        // 3. Navigate to login page
        
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get(BASE_URL + "/login");
    }
    
    @When("the user enters username {string}")
    public void theUserEntersUsername(String username) {
        // TODO: Implement this step
        // Find username field and enter the provided username
        
        WebElement usernameField = driver.findElement(By.id("username"));
        usernameField.sendKeys(username);
    }
    
    @When("the user enters password {string}")
    public void theUserEntersPassword(String password) {
        // TODO: Implement this step
        // Find password field and enter the provided password
        
        // YOUR CODE HERE
    }
    
    @When("the user clicks the login button")
    public void theUserClicksTheLoginButton() {
        // TODO: Implement this step
        // Find and click the login button
        
        // YOUR CODE HERE
    }
    
    @Then("the user should be redirected to the secure area")
    public void theUserShouldBeRedirectedToTheSecureArea() {
        // TODO: Implement this step
        // Verify the URL contains "/secure"
        
        assertTrue(driver.getCurrentUrl().contains("/secure"),
            "User was not redirected to secure area");
    }
    
    @Then("the user should see a success message containing {string}")
    public void theUserShouldSeeSuccessMessageContaining(String expectedMessage) {
        // TODO: Implement this step
        // Find the flash message element and verify it contains expected text
        
        // YOUR CODE HERE
    }
    
    @Then("the user should remain on the login page")
    public void theUserShouldRemainOnTheLoginPage() {
        // TODO: Implement this step
        // Verify URL still contains "/login"
        
        // YOUR CODE HERE
    }
    
    @Then("the user should see an error message containing {string}")
    public void theUserShouldSeeErrorMessageContaining(String expectedMessage) {
        // TODO: Implement this step
        // Find the flash message element and verify it contains expected error
        
        // YOUR CODE HERE
    }
}
```

### Task 4: Create Test Runner (5 minutes)

Create `src/test/java/com/training/cucumber/runners/TestRunner.java`:

```java
package com.training.cucumber.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.training.cucumber.stepdefinitions")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports.html")
public class TestRunner {
    // This class is just a runner - no code needed
}
```

### Task 5: Run and Verify (5 minutes)

Run the tests:

```bash
# Run all tests
mvn test

# Run tests with specific tags
mvn test -Dcucumber.filter.tags="@smoke"

# Run tests with multiple tags
mvn test -Dcucumber.filter.tags="@login and @positive"
```

---

## Expected Output

```
Feature: User Authentication

  Background:                          
    Given the user is on the login page

  @smoke @positive
  Scenario: Successful login with valid credentials
    When the user enters username "tomsmith"
    And the user enters password "SuperSecretPassword!"
    And the user clicks the login button
    Then the user should be redirected to the secure area
    And the user should see a success message containing "You logged into a secure area!"

  @negative
  Scenario: Failed login with invalid password
    When the user enters username "tomsmith"
    And the user enters password "wrongpassword"
    And the user clicks the login button
    Then the user should remain on the login page
    And the user should see an error message containing "Your password is invalid!"

4 Scenarios (4 passed)
20 Steps (20 passed)
```

---

## Definition of Done

- [ ] Project structure created correctly
- [ ] pom.xml includes all required dependencies
- [ ] Feature file contains 4 scenarios (2 provided, 2 you implement)
- [ ] All step definitions implemented and working
- [ ] Tests can be run from command line with `mvn test`
- [ ] Tag-based filtering works (`@smoke`, `@negative`)
- [ ] HTML report generated in target/cucumber-reports.html

---

## Hints

<details>
<summary>Hint: Finding the Flash Message</summary>

```java
// The flash message has ID "flash"
WebElement flashMessage = driver.findElement(By.id("flash"));
String messageText = flashMessage.getText();
assertTrue(messageText.contains(expectedMessage));
```
</details>

<details>
<summary>Hint: Login Button Selector</summary>

```java
// The login button is a button with type="submit"
// You can find it by CSS selector
WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
// Or by class
WebElement loginButton = driver.findElement(By.className("radius"));
```
</details>

<details>
<summary>Hint: Empty Credentials Scenario</summary>

```gherkin
@negative
Scenario: Failed login with empty credentials
  When the user enters username ""
  And the user enters password ""
  And the user clicks the login button
  Then the user should remain on the login page
  And the user should see an error message containing "Your username is invalid!"
```
</details>

