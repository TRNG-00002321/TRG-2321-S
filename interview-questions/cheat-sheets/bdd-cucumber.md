# BDD & Cucumber Cheat Sheet

## What is BDD?

**Behavior-Driven Development** bridges the gap between business and technical teams using natural language specifications.

```
User Story → Acceptance Criteria → Scenarios → Automated Tests
```

---

## Gherkin Syntax

### Feature File Structure

```gherkin
@tag1 @tag2
Feature: Feature Name
  As a [role]
  I want [feature]
  So that [benefit]

  Background:
    Given some common precondition

  @smoke
  Scenario: Scenario Name
    Given some initial context
    When an action is performed
    Then expected outcome occurs

  Scenario Outline: Parameterized Scenario
    Given I have <count> items
    When I add <more> items
    Then I should have <total> items

    Examples:
      | count | more | total |
      | 5     | 3    | 8     |
      | 0     | 1    | 1     |
```

### Keywords

| Keyword | Purpose |
|---------|---------|
| `Feature` | Describes the feature being tested |
| `Background` | Common steps for all scenarios |
| `Scenario` | Single test case |
| `Scenario Outline` | Template for multiple data sets |
| `Examples` | Data table for outline |
| `Given` | Initial context/precondition |
| `When` | Action being performed |
| `Then` | Expected outcome |
| `And` | Additional step (same type as previous) |
| `But` | Alternative additional step |
| `@tag` | Tags for filtering/grouping |

### Data Tables

```gherkin
Scenario: Create multiple users
  Given the following users exist:
    | name    | email           | role  |
    | Alice   | alice@test.com  | admin |
    | Bob     | bob@test.com    | user  |
  When I view the user list
  Then I should see 2 users
```

### Doc Strings

```gherkin
Scenario: Submit JSON payload
  When I send the following request:
    """json
    {
      "name": "Alice",
      "email": "alice@test.com"
    }
    """
  Then the response should be successful
```

---

## Cucumber (Java)

### Project Setup (Maven)

```xml
<dependencies>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-java</artifactId>
        <version>7.14.0</version>
    </dependency>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-junit-platform-engine</artifactId>
        <version>7.14.0</version>
    </dependency>
</dependencies>
```

### Step Definitions

```java
import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;

public class LoginSteps {
    
    private LoginPage loginPage;
    private DashboardPage dashboardPage;
    
    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        loginPage = new LoginPage(driver);
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
        dashboardPage = loginPage.clickLogin();
    }
    
    @Then("I should see the dashboard")
    public void iShouldSeeTheDashboard() {
        assertTrue(dashboardPage.isDisplayed());
    }
    
    @Then("I should see {string} message")
    public void iShouldSeeMessage(String message) {
        assertTrue(dashboardPage.getWelcomeMessage().contains(message));
    }
}
```

### Parameter Types

```java
// String
@Given("I search for {string}")
public void search(String query) { }

// Integer
@Given("I have {int} items")
public void haveItems(int count) { }

// Float
@Given("the price is {float}")
public void setPrice(float price) { }

// Word (no quotes)
@Given("I am a {word} user")
public void userType(String type) { }

// Custom regex
@Given("I wait for {duration} seconds")
public void waitFor(int duration) { }
```

### Data Tables

```java
@Given("the following users exist:")
public void usersExist(DataTable dataTable) {
    // As list of maps
    List<Map<String, String>> users = dataTable.asMaps();
    for (Map<String, String> user : users) {
        String name = user.get("name");
        String email = user.get("email");
        // Create user
    }
}

// Or convert to objects
@Given("the following users exist:")
public void usersExist(List<User> users) {
    for (User user : users) {
        userService.create(user);
    }
}
```

### Hooks

```java
import io.cucumber.java.*;

public class Hooks {
    
    @Before
    public void setUp() {
        // Runs before each scenario
        driver = new ChromeDriver();
    }
    
    @After
    public void tearDown(Scenario scenario) {
        // Runs after each scenario
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "screenshot");
        }
        driver.quit();
    }
    
    @Before("@database")
    public void setUpDatabase() {
        // Only for scenarios tagged @database
    }
    
    @BeforeStep
    public void beforeStep() {
        // Before each step
    }
    
    @AfterStep
    public void afterStep() {
        // After each step
    }
}
```

### Test Runner

```java
import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.steps")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, 
    value = "pretty, html:target/cucumber-reports.html")
public class CucumberTestRunner {
}
```

---

## Behave (Python)

### Project Structure

```
project/
├── features/
│   ├── login.feature
│   ├── environment.py     # Hooks
│   └── steps/
│       └── login_steps.py
```

### Step Definitions

```python
# features/steps/login_steps.py
from behave import given, when, then
from pages.login_page import LoginPage

@given('I am on the login page')
def step_on_login_page(context):
    context.login_page = LoginPage(context.driver)
    context.login_page.navigate()

@when('I enter "{username}" as username')
def step_enter_username(context, username):
    context.login_page.enter_username(username)

@when('I enter "{password}" as password')
def step_enter_password(context, password):
    context.login_page.enter_password(password)

@when('I click the login button')
def step_click_login(context):
    context.dashboard = context.login_page.click_login()

@then('I should see the dashboard')
def step_see_dashboard(context):
    assert context.dashboard.is_displayed()

@then('the welcome message should contain "{text}"')
def step_welcome_contains(context, text):
    message = context.dashboard.get_welcome_message()
    assert text in message, f"Expected '{text}' in '{message}'"
```

### Data Tables

```python
@given('the following users exist')
def step_users_exist(context):
    for row in context.table:
        user = {
            'name': row['name'],
            'email': row['email'],
            'role': row['role']
        }
        context.user_service.create(user)
```

### Hooks (environment.py)

```python
# features/environment.py
from selenium import webdriver

def before_all(context):
    """Runs once before all tests"""
    context.config.setup_logging()

def before_scenario(context, scenario):
    """Runs before each scenario"""
    context.driver = webdriver.Chrome()
    context.driver.maximize_window()

def after_scenario(context, scenario):
    """Runs after each scenario"""
    if scenario.status == "failed":
        context.driver.save_screenshot(f"screenshots/{scenario.name}.png")
    context.driver.quit()

def before_tag(context, tag):
    """Runs before scenarios with specific tag"""
    if tag == "database":
        context.db = setup_test_database()

def after_tag(context, tag):
    if tag == "database":
        context.db.rollback()
```

### Running Behave

```bash
# Run all tests
behave

# Run specific feature
behave features/login.feature

# Run with tags
behave --tags=@smoke
behave --tags="@login and not @slow"

# Generate reports
behave --format=allure_behave.formatter:AllureFormatter -o reports/
behave --format=html -o reports/report.html
```

---

## Best Practices

### Writing Good Scenarios

```gherkin
# BAD - Too technical
Scenario: Login
  Given I open browser to http://example.com/login
  When I type "alice" into input#username
  And I type "pass123" into input#password
  And I click button.submit

# GOOD - Business language
Scenario: Successful login with valid credentials
  Given I am on the login page
  When I enter valid credentials
  Then I should be redirected to my dashboard
```

### Scenario Independence

```gherkin
# Each scenario should be independent
# Use Background for common setup

Background:
  Given I am logged in as a standard user
  And I have an empty shopping cart

Scenario: Add item to cart
  When I add "Laptop" to my cart
  Then my cart should contain 1 item

Scenario: Remove item from cart
  Given I have "Laptop" in my cart
  When I remove "Laptop" from my cart
  Then my cart should be empty
```

### Tags for Organization

```gherkin
@smoke @critical
Feature: User Authentication

  @login @positive
  Scenario: Valid login

  @login @negative
  Scenario: Invalid login

  @slow @integration
  Scenario: Password reset email
```

### Running by Tags

```bash
# Cucumber (Java)
mvn test -Dcucumber.filter.tags="@smoke"
mvn test -Dcucumber.filter.tags="@login and not @slow"

# Behave (Python)
behave --tags=@smoke
behave --tags="@login and not @slow"
```

---

## Common Patterns

### Page Object Integration

```java
@Given("I am on the login page")
public void onLoginPage() {
    loginPage = new LoginPage(driver);
    loginPage.navigate();
}

@When("I login with valid credentials")
public void loginValid() {
    dashboardPage = loginPage.login("user", "pass");
}
```

### Sharing State (Context)

```java
// Java - Use dependency injection or shared state object
public class SharedContext {
    public WebDriver driver;
    public User currentUser;
    public Response lastResponse;
}
```

```python
# Python - Use context object
context.driver = webdriver.Chrome()
context.current_user = user
context.last_response = response
```
