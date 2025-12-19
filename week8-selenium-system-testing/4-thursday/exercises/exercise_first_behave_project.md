# Exercise 2: First Behave Project

## Objective

Set up a Behave project independently, create features and step definitions for a user registration flow.

## Learning Goals

- Set up Behave project structure
- Write Gherkin features in Behave format
- Implement step definitions with Python decorators
- Use the context object for sharing state
- Run Behave tests from command line

## Time Estimate

30 minutes

---

## Core Tasks

### Task 1: Project Setup (5 minutes)

Create project structure:
```
first_behave_project/
├── features/
│   ├── registration.feature
│   └── steps/
│       └── registration_steps.py
├── environment.py
├── behave.ini
└── requirements.txt
```

**requirements.txt:**
```
behave>=1.2.6
```

**behave.ini:**
```ini
[behave]
format = pretty
show_timings = True
```

### Task 2: Create Registration Feature (10 minutes)

Create `features/registration.feature`:

```gherkin
@registration
Feature: User Registration
  As a new user
  I want to register for an account
  So that I can access the application features

  @smoke @positive
  Scenario: Successful registration with valid details
    Given I am on the registration page
    When I enter my name "John Doe"
    And I enter my email "john.doe@example.com"
    And I enter my password "SecurePass123!"
    And I confirm my password "SecurePass123!"
    And I click the register button
    Then I should see a success message
    And I should receive a verification email

  @negative
  Scenario: Registration fails with mismatched passwords
    Given I am on the registration page
    When I enter my name "Jane Doe"
    And I enter my email "jane.doe@example.com"
    And I enter my password "SecurePass123!"
    And I confirm my password "DifferentPass456!"
    And I click the register button
    Then I should see an error "Passwords do not match"

  @negative
  Scenario: Registration fails with invalid email
    Given I am on the registration page
    When I enter my name "Bob Smith"
    And I enter my email "invalid-email"
    And I enter my password "SecurePass123!"
    And I confirm my password "SecurePass123!"
    And I click the register button
    Then I should see an error "Invalid email format"

  @data-driven
  Scenario Outline: Password validation rules
    Given I am on the registration page
    When I enter my password "<password>"
    And I click the register button
    Then I should see validation message "<message>"

    Examples:
      | password    | message                        |
      | short       | Password must be 8+ characters |
      | nodigits    | Password must contain a number |
      | NOLOWERCASE | Password must contain lowercase|
```

### Task 3: Implement Step Definitions (10 minutes)

Create `features/steps/registration_steps.py`:

```python
"""
Registration Step Definitions

Note: This exercise uses mock data since the-internet doesn't have
a registration form. Focus on learning the Behave patterns.
"""
from behave import given, when, then


# Mock registration state (in real tests, use actual page interactions)
class MockRegistration:
    def __init__(self):
        self.name = None
        self.email = None
        self.password = None
        self.confirm_password = None
        self.submitted = False
        self.error_message = None
        self.success = False
    
    def validate(self):
        """Simple validation logic for learning purposes."""
        if self.password != self.confirm_password:
            self.error_message = "Passwords do not match"
            return False
        if '@' not in self.email:
            self.error_message = "Invalid email format"
            return False
        if len(self.password) < 8:
            self.error_message = "Password must be 8+ characters"
            return False
        if not any(c.isdigit() for c in self.password):
            self.error_message = "Password must contain a number"
            return False
        if not any(c.islower() for c in self.password):
            self.error_message = "Password must contain lowercase"
            return False
        
        self.success = True
        return True


@given('I am on the registration page')
def step_on_registration_page(context):
    """Initialize registration context."""
    context.registration = MockRegistration()
    # In real tests: context.driver.get(registration_url)


@when('I enter my name "{name}"')
def step_enter_name(context, name):
    """Enter name in registration form."""
    context.registration.name = name
    # In real tests: find_element and send_keys


@when('I enter my email "{email}"')
def step_enter_email(context, email):
    """Enter email in registration form."""
    context.registration.email = email


@when('I enter my password "{password}"')
def step_enter_password(context, password):
    """Enter password in registration form."""
    context.registration.password = password


@when('I confirm my password "{password}"')
def step_confirm_password(context, password):
    """Enter password confirmation."""
    context.registration.confirm_password = password


@when('I click the register button')
def step_click_register(context):
    """Submit registration form."""
    context.registration.submitted = True
    context.registration.validate()


@then('I should see a success message')
def step_see_success(context):
    """Verify registration success."""
    assert context.registration.success, "Registration should be successful"


@then('I should receive a verification email')
def step_receive_email(context):
    """Verify email sent (mock check)."""
    # In real tests: check email service or verify sent status
    assert context.registration.success, "Email only sent on success"


@then('I should see an error "{error_message}"')
def step_see_error(context, error_message):
    """Verify error message displayed."""
    assert context.registration.error_message == error_message, \
        f"Expected '{error_message}' but got '{context.registration.error_message}'"


@then('I should see validation message "{message}"')
def step_see_validation(context, message):
    """Verify validation message."""
    assert context.registration.error_message == message, \
        f"Expected '{message}' but got '{context.registration.error_message}'"
```

### Task 4: Create Environment File (5 minutes)

Create `environment.py`:

```python
"""
Behave environment hooks for registration tests.
"""

def before_all(context):
    """Setup before all tests."""
    print("Starting registration test suite...")


def before_scenario(context, scenario):
    """Setup before each scenario."""
    print(f"Running: {scenario.name}")
    # In real tests: initialize WebDriver here


def after_scenario(context, scenario):
    """Cleanup after each scenario."""
    if scenario.status == 'failed':
        print(f"FAILED: {scenario.name}")
    # In real tests: quit WebDriver here


def after_all(context):
    """Cleanup after all tests."""
    print("Registration test suite completed.")
```

---

## Running Your Tests

```bash
# Install dependencies
pip install -r requirements.txt

# Run all tests
behave

# Run smoke tests only
behave --tags=@smoke

# Run with more detail
behave --format pretty --no-capture

# Dry run (show steps without executing)
behave --dry-run
```

---

## Expected Output

```
Feature: User Registration

  @smoke @positive
  Scenario: Successful registration with valid details
    Given I am on the registration page ... passed
    When I enter my name "John Doe" ... passed
    And I enter my email "john.doe@example.com" ... passed
    And I enter my password "SecurePass123!" ... passed
    And I confirm my password "SecurePass123!" ... passed
    And I click the register button ... passed
    Then I should see a success message ... passed
    And I should receive a verification email ... passed

  @negative
  Scenario: Registration fails with mismatched passwords
    ...

4 scenarios (4 passed)
28 steps (28 passed)
```

---

## Definition of Done

- [ ] Project structure created
- [ ] Feature file with 4+ scenarios
- [ ] All step definitions implemented
- [ ] Environment hooks in place
- [ ] All tests pass when running `behave`
- [ ] Tags work for filtering (`@smoke`, `@negative`)

