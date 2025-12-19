# Behave Framework Structure

## Learning Objectives
- Understand Behave project organization (features, steps, environment.py)
- Write feature files in Gherkin syntax for Behave
- Implement step definitions using Python decorators
- Use the context object for sharing state between steps
- Apply step parameters with regular expressions and parse format

## Why This Matters

A well-structured Behave project is maintainable, scalable, and easy to understand. Understanding the framework structure enables you to:

- Organize tests logically as they grow
- Share code effectively between step definitions
- Maintain consistent patterns across the team
- Leverage the context object for clean state management

## The Concept

### Behave Project Organization

```
┌─────────────────────────────────────────────────────────────────┐
│                    Behave Project Structure                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  project_root/                                                   │
│  │                                                               │
│  ├── features/                  ← Required: Feature directory   │
│  │   │                                                           │
│  │   ├── *.feature             ← Gherkin feature files          │
│  │   │   ├── login.feature                                      │
│  │   │   ├── checkout.feature                                   │
│  │   │   └── search.feature                                     │
│  │   │                                                           │
│  │   ├── steps/                ← Step definitions directory     │
│  │   │   ├── __init__.py                                        │
│  │   │   ├── login_steps.py                                     │
│  │   │   ├── checkout_steps.py                                  │
│  │   │   └── common_steps.py                                    │
│  │   │                                                           │
│  │   └── environment.py        ← Hooks and fixtures             │
│  │                                                               │
│  ├── pages/                    ← Page objects (optional)        │
│  │   └── *.py                                                   │
│  │                                                               │
│  ├── behave.ini               ← Configuration file              │
│  └── requirements.txt          ← Dependencies                   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Feature Files in Behave

Feature files use standard Gherkin syntax:

**features/user_management.feature:**
```gherkin
@users @management
Feature: User Management
  As an administrator
  I want to manage user accounts
  So that I can control access to the system

  Background:
    Given I am logged in as an administrator

  @smoke @positive
  Scenario: Create new user successfully
    When I navigate to user management
    And I click "Add New User"
    And I fill in user details:
      | field    | value               |
      | username | newuser             |
      | email    | newuser@example.com |
      | role     | editor              |
    And I click "Save"
    Then I should see success message "User created successfully"
    And the user "newuser" should appear in the user list

  @negative
  Scenario: Cannot create user with existing email
    Given a user exists with email "existing@example.com"
    When I try to create a user with email "existing@example.com"
    Then I should see error message "Email already exists"

  @data-driven
  Scenario Outline: User role permissions
    Given I create a user with role "<role>"
    When the user logs in
    Then they should have access to "<features>"
    But they should not have access to "<restricted>"

    Examples:
      | role   | features               | restricted          |
      | admin  | all features           | none                |
      | editor | content management     | user management     |
      | viewer | read-only dashboard    | any write operation |
```

### Step Definitions

Step definitions map Gherkin steps to Python functions:

**features/steps/user_steps.py:**
```python
from behave import given, when, then, step
from pages.user_management_page import UserManagementPage

# ==================== GIVEN STEPS ====================

@given('I am logged in as an administrator')
def step_logged_in_admin(context):
    """Log in as admin user"""
    context.login_page.login_as_admin()
    assert context.dashboard_page.is_displayed()

@given('a user exists with email "{email}"')
def step_user_exists(context, email):
    """Ensure a user with given email exists"""
    context.user_service.create_user(email=email)

# ==================== WHEN STEPS ====================

@when('I navigate to user management')
def step_navigate_to_user_mgmt(context):
    """Navigate to user management section"""
    context.nav.go_to_user_management()
    context.user_page = UserManagementPage(context.browser)

@when('I click "{button_text}"')
def step_click_button(context, button_text):
    """Click a button by its text"""
    context.current_page.click_button(button_text)

@when('I fill in user details')
def step_fill_user_details(context):
    """Fill form using data table"""
    for row in context.table:
        field = row['field']
        value = row['value']
        context.user_page.fill_field(field, value)

@when('I try to create a user with email "{email}"')
def step_try_create_user(context, email):
    """Attempt to create a user (may fail)"""
    context.user_page.create_user(email=email)

# ==================== THEN STEPS ====================

@then('I should see success message "{message}"')
def step_see_success(context, message):
    """Verify success message is displayed"""
    actual = context.current_page.get_success_message()
    assert message in actual, f"Expected '{message}', got '{actual}'"

@then('I should see error message "{message}"')
def step_see_error(context, message):
    """Verify error message is displayed"""
    actual = context.current_page.get_error_message()
    assert message in actual, f"Expected '{message}', got '{actual}'"

@then('the user "{username}" should appear in the user list')
def step_user_in_list(context, username):
    """Verify user appears in the list"""
    users = context.user_page.get_user_list()
    assert username in users, f"User {username} not found in list"

# ==================== STEP ALIASES ====================

# The @step decorator works for Given, When, and Then
@step('I wait for {seconds:d} seconds')
def step_wait(context, seconds):
    """Wait for specified seconds"""
    import time
    time.sleep(seconds)
```

### The Context Object

The `context` object is central to Behave - it shares state between steps:

```python
# features/environment.py
def before_all(context):
    """Runs once before all features"""
    # Add global configuration
    context.config.base_url = "https://example.com"
    context.config.timeout = 10

def before_scenario(context, scenario):
    """Runs before each scenario"""
    # Initialize browser
    from selenium import webdriver
    context.browser = webdriver.Chrome()
    
    # Initialize page objects
    from pages.login_page import LoginPage
    context.login_page = LoginPage(context.browser)
    
    # Initialize test data store
    context.test_data = {}

def after_scenario(context, scenario):
    """Runs after each scenario"""
    if hasattr(context, 'browser'):
        context.browser.quit()
```

**Context Usage in Steps:**

```python
@given('a product "{name}" exists')
def step_product_exists(context, name):
    # Store data in context for later steps
    context.test_data['product'] = name
    context.product_service.create(name)

@when('I add the product to cart')
def step_add_to_cart(context):
    # Access data from previous step
    product_name = context.test_data['product']
    context.cart_page.add_product(product_name)

@then('the cart should contain the product')
def step_cart_contains(context):
    # Access shared data
    product_name = context.test_data['product']
    cart_items = context.cart_page.get_items()
    assert product_name in cart_items
```

### Step Parameters

#### String Parameters

```python
# Double quotes in Gherkin, captured as string
@given('the user "{username}" is logged in')
def step_user_logged_in(context, username):
    context.auth.login(username)

# Feature file:
# Given the user "john.doe" is logged in
```

#### Integer and Float Parameters

```python
# Parse format for type conversion
@given('there are {count:d} items in stock')
def step_items_in_stock(context, count):
    # count is an integer
    context.inventory.set_stock(count)

@then('the price should be ${amount:f}')
def step_price_is(context, amount):
    # amount is a float
    assert context.cart.total == amount
```

#### Regular Expression Parameters

```python
from behave import register_type
import parse

# Custom type for email validation
@parse.with_pattern(r'[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+')
def parse_email(text):
    return text

register_type(email=parse_email)

@given('a user with email {user_email:email}')
def step_user_with_email(context, user_email):
    context.user = User(email=user_email)
```

### Data Tables

Access table data from Gherkin:

```gherkin
Scenario: Register multiple users
  Given the following users should be registered:
    | username | email              | role   |
    | john     | john@example.com   | admin  |
    | jane     | jane@example.com   | editor |
    | bob      | bob@example.com    | viewer |
```

```python
@given('the following users should be registered')
def step_register_users(context):
    # Access as list of dictionaries
    for row in context.table:
        username = row['username']
        email = row['email']
        role = row['role']
        context.user_service.register(username, email, role)
    
    # Or access raw data
    # context.table.headings  → ['username', 'email', 'role']
    # context.table.rows → [['john', 'john@example.com', 'admin'], ...]
```

### Doc Strings

Handle multi-line text:

```gherkin
Scenario: Submit detailed feedback
  When I submit the following feedback:
    """
    This is a great product!
    I especially love:
    - The intuitive interface
    - Fast performance
    - Excellent support
    """
  Then my feedback should be recorded
```

```python
@when('I submit the following feedback')
def step_submit_feedback(context):
    # context.text contains the doc string
    feedback_text = context.text
    context.feedback_page.submit(feedback_text)
```

### Step Organization Best Practices

**Organize by Domain:**
```
features/steps/
├── __init__.py
├── auth_steps.py         # Login, logout, registration
├── product_steps.py      # Product catalog operations
├── cart_steps.py         # Shopping cart operations
├── checkout_steps.py     # Checkout flow
└── common_steps.py       # Shared utility steps
```

**Reusable Steps:**
```python
# features/steps/common_steps.py
from behave import step

@step('I wait for the page to load')
def step_wait_page_load(context):
    """Reusable step for any scenario"""
    context.browser.implicitly_wait(10)

@step('I take a screenshot')
def step_screenshot(context):
    """Take screenshot for debugging"""
    context.browser.save_screenshot(f'screenshot_{context.scenario.name}.png')

@step('I am on the "{page_name}" page')
def step_on_page(context, page_name):
    """Navigate to any named page"""
    pages = {
        'login': '/login',
        'dashboard': '/dashboard',
        'settings': '/settings'
    }
    context.browser.get(context.config.base_url + pages[page_name])
```

### Complete Step Definition Example

```python
"""
features/steps/shopping_steps.py
Complete example with all step types and patterns
"""
from behave import given, when, then, step
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# ============ SETUP STEPS ============

@given('I am on the home page')
def step_on_home(context):
    context.browser.get(context.config.base_url)
    assert "Home" in context.browser.title

@given('I am logged in as "{user_type}"')
def step_logged_in_as(context, user_type):
    credentials = {
        'customer': ('customer@test.com', 'password'),
        'admin': ('admin@test.com', 'adminpass'),
        'guest': (None, None)
    }
    if user_type != 'guest':
        email, password = credentials[user_type]
        context.auth.login(email, password)

# ============ ACTION STEPS ============

@when('I search for "{query}"')
def step_search(context, query):
    search_box = context.browser.find_element(By.ID, "search")
    search_box.clear()
    search_box.send_keys(query)
    search_box.submit()
    context.last_search = query

@when('I add "{product}" to cart')
def step_add_to_cart(context, product):
    context.product_page.add_to_cart(product)
    if 'cart_items' not in context.test_data:
        context.test_data['cart_items'] = []
    context.test_data['cart_items'].append(product)

@when('I proceed to checkout')
def step_proceed_checkout(context):
    context.cart_page.click_checkout()
    context.checkout_page = CheckoutPage(context.browser)

# ============ VERIFICATION STEPS ============

@then('I should see {count:d} search results')
def step_verify_result_count(context, count):
    results = context.browser.find_elements(By.CLASS_NAME, "result")
    assert len(results) == count, f"Expected {count}, found {len(results)}"

@then('the cart total should be ${amount:f}')
def step_verify_total(context, amount):
    actual = context.cart_page.get_total()
    assert abs(actual - amount) < 0.01, f"Expected ${amount}, got ${actual}"

@then('I should see "{text}" on the page')
def step_text_visible(context, text):
    wait = WebDriverWait(context.browser, 10)
    body = wait.until(EC.presence_of_element_located((By.TAG_NAME, "body")))
    assert text in body.text, f"'{text}' not found on page"
```

## Key Takeaways

1. **Project structure** uses `features/`, `steps/`, and `environment.py`
2. **Context object** shares state between steps and hooks
3. **Step decorators** (`@given`, `@when`, `@then`) map Gherkin to Python
4. **Parameters** use parse format or regex for type conversion
5. **Data tables** and **doc strings** handle complex test data
6. **Organize steps** by domain for maintainability

## Additional Resources

- [Behave Step Implementation](https://behave.readthedocs.io/en/stable/tutorial.html#python-step-implementations) - Official tutorial
- [Behave Context](https://behave.readthedocs.io/en/stable/context.html) - Context object documentation
- [Parse Library](https://pypi.org/project/parse/) - Parameter parsing reference

