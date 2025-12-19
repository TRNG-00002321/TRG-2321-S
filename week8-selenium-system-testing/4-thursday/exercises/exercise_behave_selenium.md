# Exercise 4: Behave-Selenium Integration

## Objective

Integrate Behave with Python Selenium for BDD-style web testing, implementing page objects and proper state management.

## Learning Goals

- Integrate Selenium WebDriver with Behave
- Implement Page Object Model in Python BDD
- Share browser state across step definitions
- Handle web element interactions in BDD context

## Time Estimate

45 minutes

---

## Core Tasks

### Task 1: Page Object Base (10 minutes)

Create `pages/base_page.py`:

```python
"""
Base Page Object for Behave-Selenium integration.
"""
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException


class BasePage:
    """Base class for all page objects."""
    
    BASE_URL = "https://the-internet.herokuapp.com"
    
    def __init__(self, driver):
        self.driver = driver
        self.wait = WebDriverWait(driver, 10)
    
    def navigate_to(self, path):
        """Navigate to a path relative to base URL."""
        url = f"{self.BASE_URL}{path}"
        self.driver.get(url)
        return self
    
    def wait_for_element(self, locator):
        """Wait for element to be visible and return it."""
        return self.wait.until(EC.visibility_of_element_located(locator))
    
    def wait_for_clickable(self, locator):
        """Wait for element to be clickable."""
        return self.wait.until(EC.element_to_be_clickable(locator))
    
    def wait_for_url_contains(self, text):
        """Wait for URL to contain specific text."""
        self.wait.until(EC.url_contains(text))
    
    def click(self, locator):
        """Click element after waiting for it."""
        self.wait_for_clickable(locator).click()
    
    def type_text(self, locator, text):
        """Type text into element after clearing."""
        element = self.wait_for_element(locator)
        element.clear()
        element.send_keys(text)
    
    def get_text(self, locator):
        """Get text from element."""
        return self.wait_for_element(locator).text
    
    def is_displayed(self, locator):
        """Check if element is displayed."""
        try:
            return self.driver.find_element(*locator).is_displayed()
        except NoSuchElementException:
            return False
    
    def get_current_url(self):
        """Get current page URL."""
        return self.driver.current_url
    
    def get_title(self):
        """Get page title."""
        return self.driver.title
```

### Task 2: Login Page Object (10 minutes)

Create `pages/login_page.py`:

```python
"""
Login Page Object.
"""
from selenium.webdriver.common.by import By
from pages.base_page import BasePage


class LoginPage(BasePage):
    """Page object for the login page."""
    
    # Locators
    USERNAME = (By.ID, "username")
    PASSWORD = (By.ID, "password")
    LOGIN_BUTTON = (By.CSS_SELECTOR, "button[type='submit']")
    FLASH_MESSAGE = (By.ID, "flash")
    LOGOUT_BUTTON = (By.CSS_SELECTOR, "a.button")
    
    def navigate_to_login(self):
        """Navigate to login page."""
        return self.navigate_to("/login")
    
    def enter_username(self, username):
        """Enter username."""
        self.type_text(self.USERNAME, username)
        return self
    
    def enter_password(self, password):
        """Enter password."""
        self.type_text(self.PASSWORD, password)
        return self
    
    def click_login(self):
        """Click login button."""
        self.click(self.LOGIN_BUTTON)
        return self
    
    def login(self, username, password):
        """Complete login process."""
        self.enter_username(username)
        self.enter_password(password)
        self.click_login()
        return self
    
    def get_flash_message(self):
        """Get flash message text."""
        return self.get_text(self.FLASH_MESSAGE)
    
    def is_login_successful(self):
        """Check if login was successful."""
        return "/secure" in self.get_current_url()
    
    def click_logout(self):
        """Click logout button."""
        self.click(self.LOGOUT_BUTTON)
        return self
```

### Task 3: Complete Feature and Steps (15 minutes)

Create `features/login_ui.feature`:

```gherkin
@login @ui
Feature: Login UI Tests
  As a user
  I want to log in to the application
  So that I can access secure features

  Background:
    Given the browser is on the login page

  @smoke @positive
  Scenario: Successful login redirects to secure area
    When I enter username "tomsmith"
    And I enter password "SuperSecretPassword!"
    And I click the login button
    Then I should be on the secure area page
    And I should see message containing "You logged into"

  @negative
  Scenario: Invalid password shows error
    When I enter username "tomsmith"
    And I enter password "wrongpassword"
    And I click the login button
    Then I should remain on the login page
    And I should see error containing "Your password is invalid"

  @negative
  Scenario: Invalid username shows error
    When I enter username "invaliduser"
    And I enter password "SuperSecretPassword!"
    And I click the login button
    Then I should remain on the login page
    And I should see error containing "Your username is invalid"

  @logout
  Scenario: Logout returns to login page
    Given I am logged in as "tomsmith"
    When I click logout
    Then I should be on the login page
    And I should see message containing "You logged out"
```

Create `features/steps/login_ui_steps.py`:

```python
"""
Login UI Step Definitions using Page Objects.
"""
from behave import given, when, then
from pages.login_page import LoginPage


def get_login_page(context):
    """Get or create LoginPage instance."""
    if not hasattr(context, 'login_page'):
        context.login_page = LoginPage(context.driver)
    return context.login_page


@given('the browser is on the login page')
def step_on_login_page(context):
    """Navigate to login page."""
    get_login_page(context).navigate_to_login()


@given('I am logged in as "{username}"')
def step_logged_in_as(context, username):
    """Log in with default password."""
    page = get_login_page(context)
    page.navigate_to_login()
    page.login(username, "SuperSecretPassword!")
    assert page.is_login_successful(), "Login should succeed"


@when('I enter username "{username}"')
def step_enter_username(context, username):
    """Enter username in field."""
    get_login_page(context).enter_username(username)


@when('I enter password "{password}"')
def step_enter_password(context, password):
    """Enter password in field."""
    get_login_page(context).enter_password(password)


@when('I click the login button')
def step_click_login(context):
    """Click login button."""
    get_login_page(context).click_login()


@when('I click logout')
def step_click_logout(context):
    """Click logout button."""
    get_login_page(context).click_logout()


@then('I should be on the secure area page')
def step_on_secure_area(context):
    """Verify on secure page."""
    page = get_login_page(context)
    assert page.is_login_successful(), "Should be on secure area"


@then('I should remain on the login page')
def step_remain_on_login(context):
    """Verify still on login page."""
    page = get_login_page(context)
    assert not page.is_login_successful(), "Should remain on login page"


@then('I should be on the login page')
def step_on_login_page_verify(context):
    """Verify on login page."""
    page = get_login_page(context)
    assert "/login" in page.get_current_url(), "Should be on login page"


@then('I should see message containing "{text}"')
def step_see_message(context, text):
    """Verify message displayed."""
    message = get_login_page(context).get_flash_message()
    assert text in message, f"Expected '{text}' in '{message}'"


@then('I should see error containing "{text}"')
def step_see_error(context, text):
    """Verify error displayed."""
    message = get_login_page(context).get_flash_message()
    assert text in message, f"Expected '{text}' in '{message}'"
```

### Task 4: Environment Setup (10 minutes)

Create `environment.py`:

```python
"""
Behave environment for Selenium tests.
"""
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
import os


def before_scenario(context, scenario):
    """Initialize WebDriver before each scenario."""
    options = Options()
    
    # Check for headless tag
    if 'headless' in scenario.effective_tags:
        options.add_argument('--headless')
    
    options.add_argument('--window-size=1920,1080')
    options.add_argument('--no-sandbox')
    
    service = Service(ChromeDriverManager().install())
    context.driver = webdriver.Chrome(service=service, options=options)
    context.driver.implicitly_wait(10)


def after_scenario(context, scenario):
    """Cleanup after each scenario."""
    if scenario.status == 'failed':
        os.makedirs('screenshots', exist_ok=True)
        context.driver.save_screenshot(
            f"screenshots/{scenario.name.replace(' ', '_')}.png"
        )
    
    if hasattr(context, 'driver'):
        context.driver.quit()
```

---

## Definition of Done

- [ ] BasePage with common methods implemented
- [ ] LoginPage extends BasePage correctly
- [ ] Feature file has 4+ scenarios
- [ ] All step definitions use page objects
- [ ] Tests run successfully with `behave`
- [ ] Screenshots captured on failure

