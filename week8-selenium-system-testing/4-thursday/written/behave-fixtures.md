# Behave Fixtures and Hooks

## Learning Objectives
- Implement environment.py hooks for test lifecycle management
- Use all hook levels: before_all, after_all, before_feature, after_feature, before_scenario, after_scenario, before_step, after_step
- Understand fixture scope and lifecycle
- Share state effectively using the context object
- Create reusable fixtures for common setup patterns

## Why This Matters

Hooks (fixtures) in Behave manage test lifecycle:
- Initialize and clean up resources
- Set up test data before scenarios
- Capture screenshots on failure
- Manage browser instances
- Share configuration across tests

## The Concept

### Hook Lifecycle

```
┌─────────────────────────────────────────────────────────────────┐
│                    Behave Hook Execution Order                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  before_all(context)                                            │
│       │                                                          │
│       ▼                                                          │
│  ┌── Feature 1 ──────────────────────────────────────────────┐  │
│  │  before_feature(context, feature)                          │  │
│  │       │                                                     │  │
│  │  ┌── Scenario 1 ─────────────────────────────────────────┐ │  │
│  │  │  before_scenario(context, scenario)                    │ │  │
│  │  │       │                                                │ │  │
│  │  │  ┌── Step 1 ─────────────────────────────────────────┐│ │  │
│  │  │  │  before_step(context, step)                       ││ │  │
│  │  │  │  [Step Execution]                                 ││ │  │
│  │  │  │  after_step(context, step)                        ││ │  │
│  │  │  └───────────────────────────────────────────────────┘│ │  │
│  │  │       │                                                │ │  │
│  │  │  after_scenario(context, scenario)                     │ │  │
│  │  └────────────────────────────────────────────────────────┘ │  │
│  │       │                                                     │  │
│  │  after_feature(context, feature)                           │  │
│  └─────────────────────────────────────────────────────────────┘  │
│       │                                                          │
│       ▼                                                          │
│  after_all(context)                                             │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### environment.py Hooks

**features/environment.py:**
```python
"""
Behave environment configuration and hooks
"""
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# ==================== GLOBAL HOOKS ====================

def before_all(context):
    """
    Runs once before all features.
    Use for global setup that's needed for entire test run.
    """
    logger.info("=== Starting Test Suite ===")
    
    # Setup global configuration
    context.config.setup_logging()
    
    # Get user data from config
    userdata = context.config.userdata
    context.base_url = userdata.get('base_url', 'http://localhost:8080')
    context.browser_name = userdata.get('browser', 'chrome')
    context.headless = userdata.get('headless', 'false').lower() == 'true'
    
    # Initialize shared resources
    context.api_client = ApiClient(context.base_url)

def after_all(context):
    """
    Runs once after all features.
    Use for global cleanup.
    """
    logger.info("=== Test Suite Complete ===")
    
    # Cleanup shared resources
    if hasattr(context, 'api_client'):
        context.api_client.close()

# ==================== FEATURE HOOKS ====================

def before_feature(context, feature):
    """
    Runs before each feature file.
    """
    logger.info(f"Starting feature: {feature.name}")
    
    # Feature-specific setup
    if 'database' in feature.tags:
        context.db = setup_test_database()

def after_feature(context, feature):
    """
    Runs after each feature file.
    """
    logger.info(f"Completed feature: {feature.name}")
    
    # Feature-specific cleanup
    if hasattr(context, 'db'):
        context.db.cleanup()

# ==================== SCENARIO HOOKS ====================

def before_scenario(context, scenario):
    """
    Runs before each scenario.
    Most common place for setup.
    """
    logger.info(f"Starting scenario: {scenario.name}")
    
    # Initialize browser for UI tests
    if 'ui' in scenario.effective_tags or 'web' in scenario.effective_tags:
        setup_browser(context)
    
    # Initialize test data container
    context.test_data = {}
    
    # Track scenario start time
    import time
    context.scenario_start_time = time.time()

def after_scenario(context, scenario):
    """
    Runs after each scenario.
    Most common place for cleanup.
    """
    import time
    duration = time.time() - context.scenario_start_time
    
    logger.info(f"Completed scenario: {scenario.name}")
    logger.info(f"Status: {scenario.status} | Duration: {duration:.2f}s")
    
    # Screenshot on failure
    if scenario.status == 'failed' and hasattr(context, 'browser'):
        capture_screenshot(context, scenario)
    
    # Cleanup browser
    if hasattr(context, 'browser'):
        context.browser.quit()
        del context.browser

# ==================== STEP HOOKS ====================

def before_step(context, step):
    """
    Runs before each step.
    Useful for debugging and logging.
    """
    logger.debug(f"Executing step: {step.keyword} {step.name}")

def after_step(context, step):
    """
    Runs after each step.
    Useful for screenshots or logging on step failure.
    """
    if step.status == 'failed':
        logger.error(f"Step failed: {step.keyword} {step.name}")
        if hasattr(context, 'browser'):
            capture_screenshot(context, step=step)

# ==================== TAG HOOKS ====================

def before_tag(context, tag):
    """
    Runs before scenarios with specific tag.
    """
    if tag == 'slow':
        context.timeout = 60
    elif tag == 'api':
        context.api = ApiClient(context.base_url)

def after_tag(context, tag):
    """
    Runs after scenarios with specific tag.
    """
    if tag == 'api' and hasattr(context, 'api'):
        context.api.close()

# ==================== HELPER FUNCTIONS ====================

def setup_browser(context):
    """Initialize WebDriver based on configuration"""
    from selenium.webdriver.chrome.options import Options
    
    options = Options()
    if context.headless:
        options.add_argument('--headless')
        options.add_argument('--no-sandbox')
        options.add_argument('--disable-dev-shm-usage')
    
    options.add_argument('--window-size=1920,1080')
    
    service = Service(ChromeDriverManager().install())
    context.browser = webdriver.Chrome(service=service, options=options)
    context.browser.implicitly_wait(10)

def capture_screenshot(context, scenario=None, step=None):
    """Capture and attach screenshot"""
    import os
    from datetime import datetime
    
    # Create screenshots directory
    screenshot_dir = 'screenshots'
    os.makedirs(screenshot_dir, exist_ok=True)
    
    # Generate filename
    timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
    name = scenario.name if scenario else step.name
    safe_name = "".join(c for c in name if c.isalnum() or c in (' ', '-', '_')).rstrip()
    filename = f"{screenshot_dir}/{timestamp}_{safe_name}.png"
    
    # Take screenshot
    context.browser.save_screenshot(filename)
    logger.info(f"Screenshot saved: {filename}")
```

### Hook Scope and Context

**Understanding Context at Different Levels:**

```python
def before_all(context):
    # context._root - root namespace
    # Anything set here persists entire test run
    context.global_config = load_config()

def before_feature(context, feature):
    # Context from before_all is available
    print(context.global_config)  # Works
    
    # Feature-level data
    context.feature_data = {}

def before_scenario(context, scenario):
    # Context from before_all and before_feature available
    # BUT context is reset between scenarios!
    
    # This will be fresh for each scenario:
    context.scenario_data = {}
    
    # To persist across scenarios, use before_all or context._root
```

### Conditional Hooks with Tags

```python
def before_scenario(context, scenario):
    """Setup based on tags"""
    
    tags = scenario.effective_tags  # Includes inherited tags
    
    if 'ui' in tags:
        setup_browser(context)
    
    if 'database' in tags:
        context.db = TestDatabase()
        context.db.seed()
    
    if 'authenticated' in tags:
        context.browser.get(context.base_url + '/login')
        login_as_default_user(context)
    
    if 'admin' in tags:
        context.browser.get(context.base_url + '/login')
        login_as_admin(context)

def after_scenario(context, scenario):
    """Cleanup based on tags"""
    
    tags = scenario.effective_tags
    
    if 'database' in tags and hasattr(context, 'db'):
        context.db.cleanup()
    
    if 'ui' in tags and hasattr(context, 'browser'):
        context.browser.quit()
```

### Sharing State Between Steps

```python
# features/environment.py
def before_scenario(context, scenario):
    # Initialize container for sharing data
    context.test_data = {}
    context.created_objects = []

def after_scenario(context, scenario):
    # Cleanup created objects
    for obj in context.created_objects:
        obj.delete()

# features/steps/user_steps.py
@given('a user "{name}" exists')
def step_user_exists(context, name):
    user = User.create(name=name)
    context.test_data['user'] = user
    context.created_objects.append(user)

@when('the user logs in')
def step_user_logs_in(context):
    user = context.test_data['user']
    context.login_page.login(user.username, user.password)

@then('the user should see their profile')
def step_see_profile(context):
    user = context.test_data['user']
    assert context.profile_page.get_username() == user.username
```

### Complete environment.py Example

```python
"""
features/environment.py
Production-ready Behave environment configuration
"""
import logging
import os
from datetime import datetime
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager

# ========================================
# LOGGING CONFIGURATION
# ========================================

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger('behave')

# ========================================
# GLOBAL HOOKS
# ========================================

def before_all(context):
    """One-time setup for entire test run"""
    logger.info("=" * 60)
    logger.info("STARTING TEST RUN")
    logger.info("=" * 60)
    
    # Load configuration
    userdata = context.config.userdata
    context.base_url = userdata.get('base_url', 'http://localhost:8080')
    context.browser_name = userdata.get('browser', 'chrome')
    context.headless = userdata.get('headless', 'false').lower() == 'true'
    context.screenshot_dir = userdata.get('screenshot_dir', 'screenshots')
    
    # Create directories
    os.makedirs(context.screenshot_dir, exist_ok=True)
    
    # Test counters
    context.passed = 0
    context.failed = 0
    context.skipped = 0

def after_all(context):
    """Final cleanup and reporting"""
    logger.info("=" * 60)
    logger.info("TEST RUN COMPLETE")
    logger.info(f"Passed: {context.passed}")
    logger.info(f"Failed: {context.failed}")
    logger.info(f"Skipped: {context.skipped}")
    logger.info("=" * 60)

# ========================================
# FEATURE HOOKS
# ========================================

def before_feature(context, feature):
    logger.info(f"\n{'='*40}")
    logger.info(f"Feature: {feature.name}")
    logger.info(f"{'='*40}")

def after_feature(context, feature):
    pass

# ========================================
# SCENARIO HOOKS
# ========================================

def before_scenario(context, scenario):
    logger.info(f"\n  Scenario: {scenario.name}")
    logger.info(f"  Tags: {scenario.effective_tags}")
    
    # Initialize test data container
    context.test_data = {}
    
    # Setup browser for UI tests
    if needs_browser(scenario):
        context.browser = create_browser(context)

def after_scenario(context, scenario):
    # Update counters
    if scenario.status == 'passed':
        context.passed += 1
    elif scenario.status == 'failed':
        context.failed += 1
        # Screenshot on failure
        if hasattr(context, 'browser'):
            take_screenshot(context, scenario.name)
    else:
        context.skipped += 1
    
    logger.info(f"  Status: {scenario.status.name}")
    
    # Cleanup browser
    if hasattr(context, 'browser'):
        context.browser.quit()
        del context.browser

# ========================================
# STEP HOOKS
# ========================================

def before_step(context, step):
    pass

def after_step(context, step):
    if step.status == 'failed':
        logger.error(f"    FAILED: {step.keyword} {step.name}")

# ========================================
# HELPER FUNCTIONS
# ========================================

def needs_browser(scenario):
    """Check if scenario needs a browser"""
    ui_tags = {'ui', 'web', 'browser', 'e2e'}
    return bool(ui_tags & set(scenario.effective_tags))

def create_browser(context):
    """Create and configure WebDriver"""
    options = Options()
    
    if context.headless:
        options.add_argument('--headless=new')
        options.add_argument('--no-sandbox')
        options.add_argument('--disable-dev-shm-usage')
    
    options.add_argument('--window-size=1920,1080')
    options.add_argument('--disable-extensions')
    
    service = Service(ChromeDriverManager().install())
    browser = webdriver.Chrome(service=service, options=options)
    browser.implicitly_wait(10)
    
    return browser

def take_screenshot(context, name):
    """Capture screenshot with timestamp"""
    timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
    safe_name = "".join(c if c.isalnum() else '_' for c in name)
    filename = f"{context.screenshot_dir}/{timestamp}_{safe_name}.png"
    
    try:
        context.browser.save_screenshot(filename)
        logger.info(f"    Screenshot: {filename}")
    except Exception as e:
        logger.error(f"    Screenshot failed: {e}")
```

## Key Takeaways

1. **before_all/after_all** run once per test suite
2. **before_scenario/after_scenario** are most commonly used
3. **context** object shares state between hooks and steps
4. **Tags** enable conditional hook execution
5. **Screenshots on failure** belong in after_scenario
6. **Cleanup** is critical in after_* hooks

## Additional Resources

- [Behave Environment](https://behave.readthedocs.io/en/stable/tutorial.html#environmental-controls) - Official documentation
- [Behave Context](https://behave.readthedocs.io/en/stable/context.html) - Context object reference
- [Behave API](https://behave.readthedocs.io/en/stable/api.html) - Complete API documentation

