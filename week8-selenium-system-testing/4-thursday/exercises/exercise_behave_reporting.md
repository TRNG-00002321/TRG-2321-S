# Exercise 5: Behave Reporting

## Objective

Configure Behave to generate JSON, JUnit XML, and Allure reports for integration with CI/CD pipelines.

## Learning Goals

- Configure multiple report formatters
- Generate JUnit XML for CI/CD integration
- Set up Allure Behave for rich reports
- Understand report formats and their uses

## Time Estimate

30 minutes

---

## Core Tasks

### Task 1: Configure Multiple Formatters (10 minutes)

Update `behave.ini`:

```ini
[behave]
# Output formats - can specify multiple
format = pretty
        json:reports/results.json
        junit:reports/

# Other settings
show_timings = True
show_source = True
color = True

# Tags to skip by default
default_tags = -@wip -@skip

# Logging
log_capture = True
logging_level = INFO
logging_format = %(asctime)s - %(levelname)s - %(message)s
```

### Task 2: Allure Integration (10 minutes)

Install Allure Behave:
```bash
pip install allure-behave
```

Create a runner script `run_tests.sh` (or `.bat` for Windows):

```bash
#!/bin/bash

# Clean previous reports
rm -rf reports allure-results allure-report

# Create report directories
mkdir -p reports allure-results

# Run tests with multiple formatters
behave \
    --format pretty \
    --format json:reports/results.json \
    --format allure_behave.formatter:AllureFormatter -o allure-results \
    $@

# Generate JUnit XML report
behave --format junit --outfile reports/junit.xml $@ 2>/dev/null || true

# Display summary
echo ""
echo "Reports generated:"
echo "  - Pretty output: console"
echo "  - JSON: reports/results.json"
echo "  - JUnit: reports/junit.xml"
echo "  - Allure: allure-results/"
echo ""
echo "To view Allure report:"
echo "  allure serve allure-results"
```

### Task 3: Add Allure Decorators (5 minutes)

Enhance steps with Allure features:

```python
"""
Steps with Allure enhancements.
"""
from behave import given, when, then
import allure


@given('the user is on the login page')
@allure.step('Navigate to login page')
def step_on_login_page(context):
    context.login_page.navigate_to_login()


@when('the user logs in with "{username}" and "{password}"')
@allure.step('Login with credentials')
def step_login(context, username, password):
    with allure.step(f'Enter username: {username}'):
        context.login_page.enter_username(username)
    
    with allure.step('Enter password'):
        context.login_page.enter_password(password)
    
    with allure.step('Click login button'):
        context.login_page.click_login()


@then('the login should be successful')
@allure.step('Verify successful login')
def step_verify_success(context):
    assert context.login_page.is_login_successful()
    
    # Attach screenshot as evidence
    screenshot = context.driver.get_screenshot_as_png()
    allure.attach(screenshot, name='Login Success', 
                  attachment_type=allure.attachment_type.PNG)
```

### Task 4: Enhanced Environment for Reporting (5 minutes)

Update `environment.py` with Allure attachments:

```python
"""
Enhanced environment with Allure reporting.
"""
import allure
from datetime import datetime


def after_scenario(context, scenario):
    """Attach screenshot and logs on failure."""
    if scenario.status == 'failed':
        # Capture screenshot
        if hasattr(context, 'driver'):
            screenshot = context.driver.get_screenshot_as_png()
            allure.attach(
                screenshot,
                name=f'Failure_{scenario.name}',
                attachment_type=allure.attachment_type.PNG
            )
        
        # Attach browser logs
        if hasattr(context, 'driver'):
            try:
                logs = context.driver.get_log('browser')
                if logs:
                    log_text = '\n'.join([str(l) for l in logs])
                    allure.attach(
                        log_text,
                        name='Browser Logs',
                        attachment_type=allure.attachment_type.TEXT
                    )
            except Exception:
                pass
    
    # Cleanup
    if hasattr(context, 'driver'):
        context.driver.quit()


def before_feature(context, feature):
    """Add feature to Allure."""
    # Feature-level setup for reporting
    allure.dynamic.feature(feature.name)


def before_scenario(context, scenario):
    """Add scenario details to Allure."""
    allure.dynamic.story(scenario.name)
    
    # Add tags
    for tag in scenario.effective_tags:
        allure.dynamic.tag(tag)
```

---

## Running and Viewing Reports

```bash
# Run all tests with Allure
behave -f allure_behave.formatter:AllureFormatter -o allure-results

# View Allure report (opens browser)
allure serve allure-results

# Generate static report
allure generate allure-results -o allure-report --clean

# JUnit XML for CI
behave --format junit --outfile reports/junit.xml
```

---

## Report Format Reference

| Format | Use Case | Output |
|--------|----------|--------|
| `pretty` | Console output | Human-readable |
| `json` | Data processing | JSON file |
| `junit` | CI/CD integration | JUnit XML |
| `allure_behave` | Rich reports | Allure directory |

---

## Definition of Done

- [ ] behave.ini configured with multiple formatters
- [ ] JSON report generated
- [ ] JUnit XML report generated
- [ ] Allure results generated
- [ ] Allure report viewable with `allure serve`
- [ ] Screenshots attached in Allure on failure

