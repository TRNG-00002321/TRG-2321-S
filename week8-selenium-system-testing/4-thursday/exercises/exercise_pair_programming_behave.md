# Exercise 1: Pair Programming - Complete Behave Suite

## Objective

Work in pairs to implement a complete Behave test suite for a web application, practicing Driver/Navigator roles and collaborative BDD development.

## Learning Goals

- Practice pair programming techniques effectively
- Build a complete Behave test suite as a team
- Apply code review principles during development
- Communicate technical decisions clearly
- Create maintainable, well-structured BDD tests

## Time Estimate

2-3 hours (with role rotations every 15-20 minutes)

---

## The Project: E-Commerce Checkout Flow

You and your partner will build a comprehensive Behave test suite for an e-commerce checkout flow. The suite should include:

1. User authentication
2. Product browsing
3. Cart management
4. Checkout process

**Target Application:** https://the-internet.herokuapp.com/

---

## Project Structure (Create Together)

```
behave-pair-project/
├── features/
│   ├── authentication.feature
│   ├── navigation.feature
│   ├── forms.feature
│   └── steps/
│       ├── common_steps.py
│       ├── auth_steps.py
│       ├── navigation_steps.py
│       └── form_steps.py
├── pages/
│   ├── base_page.py
│   ├── login_page.py
│   ├── home_page.py
│   └── form_page.py
├── environment.py
├── behave.ini
├── requirements.txt
└── README.md
```

---

## Phase 1: Project Setup (30 minutes)

### Driver 1 / Navigator 2

**Task: Create project structure and configuration**

1. Create directory structure
2. Create `requirements.txt`:
```
behave>=1.2.6
selenium>=4.15.0
webdriver-manager>=4.0.0
allure-behave>=2.13.0
```

3. Create `behave.ini`:
```ini
[behave]
format = pretty
show_snippets = True
show_timings = True
log_capture = False
stdout_capture = False
```

4. Create `environment.py` with basic hooks

### Switch Roles

### Driver 2 / Navigator 1

**Task: Create base page object**

Create `pages/base_page.py`:
```python
"""
Base Page Object - implement together
"""
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException

class BasePage:
    BASE_URL = "https://the-internet.herokuapp.com"
    
    def __init__(self, driver):
        self.driver = driver
        self.wait = WebDriverWait(driver, 10)
    
    def navigate_to(self, path):
        """Navigate to a path relative to base URL."""
        # TODO: Implement together
        pass
    
    def wait_for_element(self, locator):
        """Wait for element to be visible."""
        # TODO: Implement together
        pass
    
    def click(self, locator):
        """Click on element after waiting for it."""
        # TODO: Implement together
        pass
    
    def type_text(self, locator, text):
        """Type text into element after clearing it."""
        # TODO: Implement together
        pass
    
    def get_text(self, locator):
        """Get text from element."""
        # TODO: Implement together
        pass
    
    def is_displayed(self, locator):
        """Check if element is displayed."""
        # TODO: Implement together
        pass
```

---

## Phase 2: Authentication Feature (45 minutes)

### Driver 1 / Navigator 2

**Task: Create authentication feature file**

Create `features/authentication.feature`:
```gherkin
@authentication
Feature: User Authentication
  As a registered user
  I want to log in and out of the application
  So that I can access protected content

  Background:
    Given the user is on the login page

  @smoke @positive
  Scenario: Successful login with valid credentials
    When the user enters username "tomsmith"
    And the user enters password "SuperSecretPassword!"
    And the user clicks the login button
    Then the user should be logged in successfully
    And the user should see welcome message "You logged into a secure area!"

  @negative
  Scenario: Failed login with invalid password
    # TODO: Implement this scenario together

  @negative  
  Scenario: Failed login with invalid username
    # TODO: Implement this scenario together

  @logout
  Scenario: Successful logout
    Given the user is logged in as "tomsmith"
    When the user clicks the logout button
    Then the user should be logged out
    And the user should see message "You logged out of the secure area!"

  @data-driven
  Scenario Outline: Login validation with multiple credentials
    When the user enters username "<username>"
    And the user enters password "<password>"
    And the user clicks the login button
    Then the login result should be "<result>"

    Examples:
      | username    | password             | result  |
      | tomsmith    | SuperSecretPassword! | success |
      | invaliduser | SuperSecretPassword! | failed  |
      | tomsmith    | wrongpassword        | failed  |
      |             |                      | failed  |
```

### Switch Roles

### Driver 2 / Navigator 1

**Task: Implement login page object**

Create `pages/login_page.py`:
```python
"""
Login Page Object - implement together
"""
from selenium.webdriver.common.by import By
from pages.base_page import BasePage

class LoginPage(BasePage):
    # Locators
    USERNAME_INPUT = (By.ID, "username")
    PASSWORD_INPUT = (By.ID, "password")
    LOGIN_BUTTON = (By.CSS_SELECTOR, "button[type='submit']")
    FLASH_MESSAGE = (By.ID, "flash")
    LOGOUT_BUTTON = (By.CSS_SELECTOR, "a.button")
    
    def navigate_to_login(self):
        """Navigate to login page."""
        # TODO: Implement
        pass
    
    def enter_username(self, username):
        """Enter username in the field."""
        # TODO: Implement
        pass
    
    def enter_password(self, password):
        """Enter password in the field."""
        # TODO: Implement
        pass
    
    def click_login(self):
        """Click the login button."""
        # TODO: Implement
        pass
    
    def login(self, username, password):
        """Complete login flow."""
        # TODO: Implement - combine above methods
        pass
    
    def get_flash_message(self):
        """Get the flash message text."""
        # TODO: Implement
        pass
    
    def is_login_successful(self):
        """Check if login was successful."""
        # TODO: Implement - check URL or message
        pass
    
    def click_logout(self):
        """Click the logout button."""
        # TODO: Implement
        pass
```

### Switch Roles

### Driver 1 / Navigator 2

**Task: Implement authentication steps**

Create `features/steps/auth_steps.py`:
```python
"""
Authentication Step Definitions - implement together
"""
from behave import given, when, then

@given('the user is on the login page')
def step_on_login_page(context):
    """Navigate to login page."""
    # TODO: Implement using context.login_page
    pass

@given('the user is logged in as "{username}"')
def step_logged_in_as(context, username):
    """Log in as specified user."""
    # TODO: Implement
    # Navigate to login, enter credentials, verify success
    pass

@when('the user enters username "{username}"')
def step_enter_username(context, username):
    """Enter username."""
    # TODO: Implement
    pass

@when('the user enters password "{password}"')
def step_enter_password(context, password):
    """Enter password."""
    # TODO: Implement
    pass

@when('the user clicks the login button')
def step_click_login(context):
    """Click login button."""
    # TODO: Implement
    pass

@when('the user clicks the logout button')
def step_click_logout(context):
    """Click logout button."""
    # TODO: Implement
    pass

@then('the user should be logged in successfully')
def step_logged_in_successfully(context):
    """Verify successful login."""
    # TODO: Implement assertion
    pass

@then('the user should see welcome message "{message}"')
def step_see_welcome_message(context, message):
    """Verify welcome message."""
    # TODO: Implement assertion
    pass

@then('the user should be logged out')
def step_logged_out(context):
    """Verify logout."""
    # TODO: Implement assertion
    pass

@then('the user should see message "{message}"')
def step_see_message(context, message):
    """Verify message displayed."""
    # TODO: Implement assertion
    pass

@then('the login result should be "{result}"')
def step_login_result(context, result):
    """Verify login result."""
    # TODO: Implement based on result value
    pass
```

---

## Phase 3: Forms and Validation (45 minutes)

### Switch Roles

### Driver 2 / Navigator 1

**Task: Create forms feature file**

Create `features/forms.feature`:
```gherkin
@forms
Feature: Form Interactions
  As a user
  I want to interact with various form elements
  So that I can complete different tasks

  @checkboxes
  Scenario: Toggle checkboxes
    Given the user is on the checkboxes page
    When the user checks all checkboxes
    Then all checkboxes should be checked
    When the user unchecks all checkboxes
    Then all checkboxes should be unchecked

  @dropdown
  Scenario Outline: Select dropdown options
    Given the user is on the dropdown page
    When the user selects "<option>" from the dropdown
    Then the dropdown should show "<option>" selected

    Examples:
      | option   |
      | Option 1 |
      | Option 2 |

  @input
  Scenario: Clear and type in input field
    # TODO: Design and implement together
    # Use the inputs page or key presses page

  @upload
  Scenario: Upload a file
    # TODO: Design and implement together
    # Use the file upload page
```

### Switch Roles

### Driver 1 / Navigator 2

**Task: Implement forms step definitions**

Create `features/steps/form_steps.py`:
```python
"""
Form Step Definitions - implement together
"""
from behave import given, when, then
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select

# Checkboxes
@given('the user is on the checkboxes page')
def step_on_checkboxes_page(context):
    # TODO: Implement
    pass

@when('the user checks all checkboxes')
def step_check_all_checkboxes(context):
    # TODO: Implement
    pass

@when('the user unchecks all checkboxes')
def step_uncheck_all_checkboxes(context):
    # TODO: Implement
    pass

@then('all checkboxes should be checked')
def step_all_checked(context):
    # TODO: Implement assertion
    pass

@then('all checkboxes should be unchecked')
def step_all_unchecked(context):
    # TODO: Implement assertion
    pass

# Dropdowns
@given('the user is on the dropdown page')
def step_on_dropdown_page(context):
    # TODO: Implement
    pass

@when('the user selects "{option}" from the dropdown')
def step_select_option(context, option):
    # TODO: Implement using Select class
    pass

@then('the dropdown should show "{option}" selected')
def step_verify_selected(context, option):
    # TODO: Implement assertion
    pass
```

---

## Phase 4: Environment Hooks (30 minutes)

### Final Implementation Together

Update `environment.py`:
```python
"""
Behave Environment Hooks - implement together
"""
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

from pages.login_page import LoginPage
from pages.base_page import BasePage

def before_all(context):
    """
    Setup before all tests.
    Configure browser options, logging, etc.
    """
    # TODO: Implement together
    # Set up any global configuration
    pass

def before_scenario(context, scenario):
    """
    Setup before each scenario.
    Initialize WebDriver and page objects.
    """
    # TODO: Implement together
    # 1. Set up WebDriver
    # 2. Initialize page objects on context
    # 3. Configure implicit wait
    
    service = Service(ChromeDriverManager().install())
    context.driver = webdriver.Chrome(service=service)
    context.driver.implicitly_wait(10)
    context.driver.maximize_window()
    
    # Initialize page objects
    context.login_page = LoginPage(context.driver)
    context.base_page = BasePage(context.driver)

def after_scenario(context, scenario):
    """
    Cleanup after each scenario.
    Take screenshot on failure, quit browser.
    """
    # TODO: Implement together
    # 1. Check if scenario failed
    # 2. If failed, take screenshot
    # 3. Attach to report
    # 4. Quit driver
    
    if scenario.status == 'failed':
        # Take screenshot
        screenshot = context.driver.get_screenshot_as_png()
        scenario.attach(screenshot, mime_type='image/png')
    
    if hasattr(context, 'driver'):
        context.driver.quit()

def after_all(context):
    """
    Cleanup after all tests.
    """
    # TODO: Any final cleanup
    pass
```

---

## Code Review Checklist

Before submitting, review your code together:

### Feature Files
- [ ] Each scenario tests ONE thing
- [ ] Given/When/Then used appropriately
- [ ] Scenarios are business-readable
- [ ] Data tables and Examples used where appropriate
- [ ] Tags applied consistently

### Step Definitions
- [ ] Steps are reusable across features
- [ ] No duplication in step code
- [ ] Assertions have clear error messages
- [ ] Steps use page objects (not direct locators)

### Page Objects
- [ ] Locators are centralized
- [ ] Methods follow single responsibility
- [ ] BasePage contains common functionality
- [ ] No test logic in page objects

### Environment
- [ ] Driver properly initialized
- [ ] Screenshots on failure
- [ ] Clean teardown

---

## Pair Retrospective (15 minutes)

After completing the exercise, discuss with your partner:

1. **What went well?**
   - Which role did you prefer (Driver/Navigator)?
   - What techniques helped communication?

2. **What was challenging?**
   - Where did you get stuck?
   - How did you resolve disagreements?

3. **What would you do differently?**
   - Better ways to organize the work?
   - More effective rotation timing?

---

## Definition of Done

- [ ] All feature files created with complete scenarios
- [ ] All step definitions implemented and working
- [ ] Page objects follow consistent pattern
- [ ] Environment hooks handle setup/teardown
- [ ] Screenshots captured on failure
- [ ] All tests pass when run with `behave`
- [ ] Code reviewed by partner
- [ ] Pair retrospective completed

---

## Running Your Tests

```bash
# Run all tests
behave

# Run with specific tags
behave --tags=@smoke

# Run with verbose output
behave --no-capture --format pretty

# Generate Allure report
behave -f allure_behave.formatter:AllureFormatter -o allure-results
allure serve allure-results
```

