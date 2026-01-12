# Week 8: BDD & Playwright - Coding Assessment

This assessment covers **Selenium (Python)**, **Gherkin/BDD**, **Cucumber (Java)**, **Behave (Python)**, and **Playwright (Java)**. Complete all 5 exercises.

---

## Exercise 1: Selenium Python - WebDriver Basics (Beginner)

Write Selenium Python tests for a simple login flow with explicit waits.

**Target URL:** `https://the-internet.herokuapp.com/login`

**Requirements:**
- Set up WebDriver with Chrome
- Use explicit waits (`WebDriverWait`) instead of implicit waits or `time.sleep()`
- Navigate to the login page
- Enter valid credentials and submit
- Verify successful login

**Starter Code:**
```python
import pytest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

class TestLoginPage:
    
    @pytest.fixture(autouse=True)
    def setup(self):
        # TODO: Initialize Chrome WebDriver
        # TODO: Set implicit wait (optional) or use explicit waits
        self.driver = None
        yield
        # TODO: Close browser in teardown
    
    def test_successful_login(self):
        self.driver.get("https://the-internet.herokuapp.com/login")
        
        # TODO: Wait for username field to be visible
        wait = WebDriverWait(self.driver, 10)
        username_field = wait.until(
            EC.visibility_of_element_located((By.ID, "username"))
        )
        
        # TODO: Enter username "tomsmith"
        # TODO: Enter password "SuperSecretPassword!"
        # TODO: Click login button
        # TODO: Wait for and verify success message contains "You logged into"
    
    def test_invalid_login_shows_error(self):
        # TODO: Navigate to login page
        # TODO: Enter invalid credentials
        # TODO: Click login
        # TODO: Verify error message is displayed
        pass
    
    def test_page_title(self):
        # TODO: Navigate to login page
        # TODO: Assert page title is "The Internet"
        pass
```

---

## Exercise 2: Gherkin Feature File Writing (Beginner)

Write a complete Gherkin feature file for an e-commerce shopping cart functionality.

**Requirements:**
Write scenarios for the following:
1. Adding a single item to an empty cart
2. Adding multiple items to the cart
3. Removing an item from the cart
4. Updating item quantity
5. Viewing cart total

Use the following Gherkin keywords appropriately:
- `Feature`, `Scenario`, `Scenario Outline`, `Examples`
- `Given`, `When`, `Then`, `And`, `But`
- `Background` (for common setup)

**Starter Template:**
```gherkin
Feature: Shopping Cart Management
  As a customer
  I want to manage items in my shopping cart
  So that I can purchase the products I need

  Background:
    # TODO: Add common setup steps that apply to all scenarios
    # Example: Given the user is logged in
    # And the product catalog is available

  Scenario: Add single item to empty cart
    # TODO: Write Given/When/Then steps
    # Hint: Given the cart is empty
    #       When the user adds "Laptop" to the cart
    #       Then the cart should contain 1 item

  Scenario: Add multiple different items to cart
    # TODO: Write steps for adding multiple items
    # Include And keywords to chain steps

  Scenario: Remove item from cart
    # TODO: Write steps for removing an item
    # Include verification that cart is updated

  Scenario: Update item quantity
    # TODO: Write steps for changing quantity
    # Verify the total is recalculated

  Scenario Outline: Apply discount codes
    # TODO: Use Scenario Outline with Examples table
    # Test different discount codes and their effects
    Given the cart total is $<original_total>
    When the user applies discount code "<code>"
    Then the cart total should be $<final_total>

    Examples:
      | original_total | code      | final_total |
      # TODO: Add at least 3 examples including invalid code
```

---

## Exercise 3: Cucumber Step Definitions (Java) (Intermediate)

Implement step definitions for product search functionality in Java using Cucumber.

**Feature File (provided):**
```gherkin
Feature: Product Search
  
  Scenario: Search for existing product
    Given the user is on the home page
    When the user searches for "laptop"
    Then search results should be displayed
    And the results should contain "laptop" in the title

  Scenario: Search with no results
    Given the user is on the home page
    When the user searches for "xyznonexistent123"
    Then a "no results" message should be displayed

  Scenario Outline: Search with filters
    Given the user is on the home page
    When the user searches for "<product>"
    And applies the "<filter>" filter
    Then results should be filtered by "<filter>"
    
    Examples:
      | product  | filter     |
      | laptop   | price_low  |
      | phone    | rating     |
      | tablet   | newest     |
```

**Starter Code for Step Definitions:**
```java
import io.cucumber.java.en.*;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import static org.junit.jupiter.api.Assertions.*;

public class ProductSearchSteps {
    
    private WebDriver driver;
    private SearchResultsPage searchResultsPage;
    
    @Before
    public void setup() {
        // TODO: Initialize WebDriver
    }
    
    @After
    public void tearDown() {
        // TODO: Close WebDriver
    }
    
    @Given("the user is on the home page")
    public void userIsOnHomePage() {
        // TODO: Navigate to home page
        // Example: driver.get("https://example-store.com");
    }
    
    @When("the user searches for {string}")
    public void userSearchesFor(String searchTerm) {
        // TODO: Find search input
        // TODO: Enter search term
        // TODO: Submit search
    }
    
    @When("applies the {string} filter")
    public void appliesFilter(String filterType) {
        // TODO: Apply the specified filter
        // Consider using a switch statement for different filter types
    }
    
    @Then("search results should be displayed")
    public void searchResultsDisplayed() {
        // TODO: Assert that results container is visible
        // TODO: Assert that at least one result is shown
    }
    
    @Then("the results should contain {string} in the title")
    public void resultsContainInTitle(String expectedText) {
        // TODO: Get all result titles
        // TODO: Assert each title contains the expected text (case-insensitive)
    }
    
    @Then("a {string} message should be displayed")
    public void messageIsDisplayed(String messageType) {
        // TODO: Verify the appropriate message is shown
    }
    
    @Then("results should be filtered by {string}")
    public void resultsFilteredBy(String filterType) {
        // TODO: Verify filter is applied correctly
        // This might involve checking sort order or filter badges
    }
}
```

---

## Exercise 4: Behave BDD (Python) (Intermediate)

Implement a complete Behave test suite for user registration functionality.

**Requirements:**
1. Create a feature file with at least 3 scenarios
2. Implement step definitions
3. Set up fixtures in `environment.py`

**Feature File (registration.feature):**
```gherkin
Feature: User Registration
  As a new user
  I want to create an account
  So that I can access the application features

  Background:
    Given the registration page is open

  Scenario: Successful registration with valid data
    # TODO: Write steps for successful registration
    # Include: entering valid email, password, confirm password
    # Verify: success message and redirect to dashboard

  Scenario: Registration fails with mismatched passwords
    # TODO: Write steps for password mismatch scenario
    # Verify: error message is displayed

  Scenario: Registration fails with existing email
    # TODO: Write steps for duplicate email scenario

  Scenario Outline: Password validation
    # TODO: Test various password strengths
    When the user enters password "<password>"
    Then the password strength should be "<strength>"

    Examples:
      | password          | strength |
      | 123               | weak     |
      | password123       | medium   |
      | P@ssw0rd!Strong   | strong   |
```

**Step Definitions (steps/registration_steps.py):**
```python
from behave import given, when, then
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

@given('the registration page is open')
def step_registration_page_open(context):
    # TODO: Navigate to registration page
    # The driver should be available as context.driver (set up in environment.py)
    pass

@when('the user enters email "{email}"')
def step_enter_email(context, email):
    # TODO: Find email field and enter the email
    pass

@when('the user enters password "{password}"')
def step_enter_password(context, password):
    # TODO: Find password field and enter the password
    pass

@when('the user enters confirm password "{password}"')
def step_enter_confirm_password(context, password):
    # TODO: Find confirm password field and enter the password
    pass

@when('the user clicks the register button')
def step_click_register(context):
    # TODO: Find and click the register button
    pass

@then('a success message should be displayed')
def step_verify_success_message(context):
    # TODO: Verify success message is visible
    # Use assertions to validate
    pass

@then('an error message "{message}" should be displayed')
def step_verify_error_message(context, message):
    # TODO: Verify the specific error message is shown
    pass

@then('the password strength should be "{strength}"')
def step_verify_password_strength(context, strength):
    # TODO: Check the password strength indicator
    pass
```

**Environment Setup (features/environment.py):**
```python
from selenium import webdriver

def before_scenario(context, scenario):
    # TODO: Initialize WebDriver
    # Store it in context.driver so steps can access it
    pass

def after_scenario(context, scenario):
    # TODO: Close WebDriver
    pass

def before_all(context):
    # TODO: Any global setup (optional)
    pass

def after_all(context):
    # TODO: Any global teardown (optional)
    pass
```

---

## Exercise 5: Playwright Java - Browser Automation (Intermediate)

Write Playwright tests in Java for a todo application.

**Target URL:** `https://demo.playwright.dev/todomvc`

**Requirements:**
- Create a new todo item
- Mark a todo as complete
- Delete a todo item
- Filter todos (All, Active, Completed)
- Verify todo count

**Starter Code:**
```java
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class TodoMvcTest {
    
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    
    @BeforeAll
    static void setupAll() {
        // TODO: Initialize Playwright and launch browser
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(false));
    }
    
    @AfterAll
    static void tearDownAll() {
        // TODO: Close browser and Playwright
    }
    
    @BeforeEach
    void setup() {
        // TODO: Create new context and page
        // TODO: Navigate to the todo app URL
        context = browser.newContext();
        page = context.newPage();
        page.navigate("https://demo.playwright.dev/todomvc");
    }
    
    @AfterEach
    void tearDown() {
        // TODO: Close context
    }
    
    @Test
    void testCreateTodo() {
        // TODO: Find the input field with placeholder "What needs to be done?"
        // TODO: Type "Buy groceries" and press Enter
        // TODO: Verify the todo item appears in the list
        
        page.locator(".new-todo").fill("Buy groceries");
        page.locator(".new-todo").press("Enter");
        
        // TODO: Assert that todo list contains "Buy groceries"
    }
    
    @Test
    void testMarkTodoComplete() {
        // First create a todo
        page.locator(".new-todo").fill("Complete assignment");
        page.locator(".new-todo").press("Enter");
        
        // TODO: Click the checkbox to mark as complete
        // TODO: Verify the todo has the "completed" class
    }
    
    @Test
    void testDeleteTodo() {
        // Create a todo first
        page.locator(".new-todo").fill("Delete me");
        page.locator(".new-todo").press("Enter");
        
        // TODO: Hover over the todo item to reveal the delete button
        // TODO: Click the delete button (X)
        // TODO: Verify the todo is no longer in the list
    }
    
    @Test
    void testFilterTodos() {
        // Create multiple todos
        page.locator(".new-todo").fill("Task 1");
        page.locator(".new-todo").press("Enter");
        page.locator(".new-todo").fill("Task 2");
        page.locator(".new-todo").press("Enter");
        
        // Mark first todo as complete
        page.locator(".todo-list li").first().locator(".toggle").click();
        
        // TODO: Click "Active" filter
        // TODO: Verify only "Task 2" is visible
        
        // TODO: Click "Completed" filter
        // TODO: Verify only "Task 1" is visible
        
        // TODO: Click "All" filter
        // TODO: Verify both tasks are visible
    }
    
    @Test
    void testTodoCount() {
        // TODO: Add 3 todo items
        // TODO: Verify the count shows "3 items left"
        
        // TODO: Complete one item
        // TODO: Verify the count shows "2 items left"
    }
}
```

---

## Submission Guidelines

1. Complete all exercises with working, runnable code
2. Feature files should follow Gherkin syntax correctly
3. Step definitions should be reusable where possible
4. Playwright tests should use proper locator strategies
5. Include appropriate waits and error handling
6. Add comments explaining complex logic
