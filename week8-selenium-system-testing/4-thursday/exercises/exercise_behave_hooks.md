# Exercise 3: Behave Hooks Implementation

## Objective

Implement comprehensive environment hooks for test setup, teardown, database fixtures, and screenshot capture on failure.

## Learning Goals

- Implement all Behave hook types
- Manage WebDriver lifecycle properly
- Share fixtures between scenarios
- Capture screenshots on test failure
- Use hook scope appropriately

## Time Estimate

30 minutes

---

## Core Tasks

### Task 1: Complete Environment Hooks (20 minutes)

Create `environment.py`:

```python
"""
Complete Behave environment hooks implementation.

Hook execution order:
- before_all
  - before_tag (for each tag)
    - before_feature
      - before_scenario
        - before_step
          [step execution]
        - after_step
      - after_scenario
    - after_feature
  - after_tag
- after_all
"""

from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
import os
from datetime import datetime


# ============================================================================
# GLOBAL HOOKS
# ============================================================================

def before_all(context):
    """
    Execute once before all tests.
    Use for global setup like configuration loading.
    """
    print("\n" + "=" * 60)
    print("BEHAVE TEST SUITE STARTING")
    print("=" * 60)
    
    # Store configuration in context
    context.config.userdata.setdefault('browser', 'chrome')
    context.config.userdata.setdefault('headless', 'false')
    
    # Create screenshots directory
    os.makedirs('screenshots', exist_ok=True)
    
    # Store test start time
    context.test_start_time = datetime.now()


def after_all(context):
    """
    Execute once after all tests.
    Use for final cleanup and reporting.
    """
    duration = datetime.now() - context.test_start_time
    
    print("\n" + "=" * 60)
    print("BEHAVE TEST SUITE COMPLETED")
    print(f"Total Duration: {duration}")
    print("=" * 60)


# ============================================================================
# FEATURE HOOKS
# ============================================================================

def before_feature(context, feature):
    """
    Execute before each feature file.
    """
    print(f"\n📁 Feature: {feature.name}")
    context.feature_start_time = datetime.now()


def after_feature(context, feature):
    """
    Execute after each feature file.
    """
    duration = datetime.now() - context.feature_start_time
    print(f"   Feature completed in {duration.total_seconds():.2f}s")


# ============================================================================
# SCENARIO HOOKS
# ============================================================================

def before_scenario(context, scenario):
    """
    Execute before each scenario.
    Initialize WebDriver and page objects.
    """
    print(f"\n  🧪 Scenario: {scenario.name}")
    
    # Determine if headless mode
    headless = context.config.userdata.get('headless', 'false').lower() == 'true'
    
    # Check for @headless tag
    if 'headless' in scenario.effective_tags:
        headless = True
    
    # Initialize WebDriver
    options = Options()
    if headless:
        options.add_argument('--headless')
        options.add_argument('--no-sandbox')
        options.add_argument('--disable-dev-shm-usage')
    
    options.add_argument('--window-size=1920,1080')
    
    service = Service(ChromeDriverManager().install())
    context.driver = webdriver.Chrome(service=service, options=options)
    context.driver.implicitly_wait(10)
    
    # Store scenario start time
    context.scenario_start_time = datetime.now()


def after_scenario(context, scenario):
    """
    Execute after each scenario.
    Handle cleanup and failure screenshots.
    """
    duration = datetime.now() - context.scenario_start_time
    
    # Take screenshot on failure
    if scenario.status == 'failed':
        _capture_screenshot(context, scenario)
        print(f"     ❌ FAILED ({duration.total_seconds():.2f}s)")
    else:
        print(f"     ✅ PASSED ({duration.total_seconds():.2f}s)")
    
    # Cleanup WebDriver
    if hasattr(context, 'driver') and context.driver:
        context.driver.quit()


def _capture_screenshot(context, scenario):
    """
    Capture screenshot and attach to report.
    """
    try:
        # Generate filename
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        scenario_name = scenario.name.replace(' ', '_')[:50]
        filename = f"screenshots/{timestamp}_{scenario_name}.png"
        
        # Save screenshot
        context.driver.save_screenshot(filename)
        print(f"     📸 Screenshot saved: {filename}")
        
        # Attach to scenario for reporting
        with open(filename, 'rb') as f:
            screenshot_data = f.read()
        
        # Attach for Allure or other reporters
        if hasattr(context, '_runner'):
            context._runner.current_match.attach(
                screenshot_data, 
                mime_type='image/png',
                name=f'{scenario_name}_failure'
            )
    
    except Exception as e:
        print(f"     ⚠️  Failed to capture screenshot: {e}")


# ============================================================================
# STEP HOOKS
# ============================================================================

def before_step(context, step):
    """
    Execute before each step.
    Useful for debugging or step-level logging.
    """
    # Uncomment for verbose step logging
    # print(f"       → {step.keyword} {step.name}")
    pass


def after_step(context, step):
    """
    Execute after each step.
    Useful for capturing step-level screenshots.
    """
    if step.status == 'failed':
        # Step failure is also captured at scenario level
        pass


# ============================================================================
# TAG HOOKS
# ============================================================================

def before_tag(context, tag):
    """
    Execute before scenarios with specific tags.
    """
    if tag == 'database':
        _setup_database(context)
    elif tag == 'slow':
        # Increase timeouts for slow tests
        if hasattr(context, 'driver'):
            context.driver.implicitly_wait(30)


def after_tag(context, tag):
    """
    Execute after scenarios with specific tags.
    """
    if tag == 'database':
        _teardown_database(context)


def _setup_database(context):
    """Setup test database fixtures."""
    print("     🗄️  Setting up database fixtures...")
    # TODO: Implement database setup
    # context.db_connection = create_connection()
    # load_fixtures(context.db_connection)


def _teardown_database(context):
    """Cleanup test database."""
    print("     🗄️  Cleaning up database...")
    # TODO: Implement database cleanup
    # cleanup_fixtures(context.db_connection)
    # context.db_connection.close()
```

### Task 2: Test Your Hooks (10 minutes)

Create a simple feature to test hooks work correctly:

`features/hooks_test.feature`:
```gherkin
@hooks-test
Feature: Hook Testing
  Test that hooks execute correctly

  @smoke
  Scenario: Test basic hooks
    Given I verify hooks are working
    When I perform an action
    Then I should see the result

  @database
  Scenario: Test database hooks
    Given the database is initialized
    When I query for data
    Then I should get results

  @slow @headless
  Scenario: Test tagged hooks
    Given I am in headless mode
    When I wait for slow operation
    Then I should complete successfully
```

`features/steps/hooks_test_steps.py`:
```python
"""Steps to verify hooks are working."""
from behave import given, when, then

@given('I verify hooks are working')
def step_verify_hooks(context):
    assert hasattr(context, 'driver'), "Driver should be initialized"
    print("       Hooks working: driver initialized")

@when('I perform an action')
def step_perform_action(context):
    context.driver.get("https://the-internet.herokuapp.com/")

@then('I should see the result')
def step_see_result(context):
    assert "The Internet" in context.driver.title

@given('the database is initialized')
def step_db_init(context):
    # Database hook should have run
    pass

@when('I query for data')
def step_query(context):
    pass

@then('I should get results')
def step_results(context):
    pass

@given('I am in headless mode')
def step_headless(context):
    # Verify headless by checking window handle count
    pass

@when('I wait for slow operation')
def step_slow(context):
    import time
    time.sleep(1)  # Simulate slow operation

@then('I should complete successfully')
def step_complete(context):
    assert True
```

---

## Definition of Done

- [ ] All hook types implemented
- [ ] WebDriver initialized in before_scenario
- [ ] Screenshots captured on failure
- [ ] Tag-specific hooks working
- [ ] Test feature passes all scenarios
- [ ] Screenshots saved to correct directory

