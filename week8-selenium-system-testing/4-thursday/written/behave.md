# Introduction to Behave

## Learning Objectives
- Understand what Behave is and its role in Python BDD
- Compare Behave with Cucumber and other BDD frameworks
- Identify when to choose Behave for Python projects
- Explore the Behave community and ecosystem
- Set up a basic Behave environment

## Why This Matters

Yesterday you learned Cucumber for Java BDD. Today you'll learn **Behave**, Python's premier BDD framework. As part of your journey to becoming a polyglot test automation engineer, mastering both frameworks enables you to:

- Work effectively in both Java and Python ecosystems
- Apply BDD principles regardless of technology stack
- Choose the right tool based on project requirements
- Transfer BDD knowledge between languages seamlessly

## The Concept

### What is Behave?

**Behave** is a Python BDD (Behavior-Driven Development) framework that uses Gherkin syntax for writing tests in plain language. It's Python's equivalent to Cucumber, allowing teams to write executable specifications that both technical and non-technical stakeholders can understand.

```
┌─────────────────────────────────────────────────────────────────┐
│                    Behave Architecture                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Feature File (.feature)     Step Definitions     Application  │
│   ┌──────────────────┐       ┌──────────────┐     ┌─────────┐  │
│   │ Given user logs  │──────►│   Python     │────►│  Web    │  │
│   │ When clicks buy  │       │   functions  │     │  App    │  │
│   │ Then order placed│       │  in steps/   │     │         │  │
│   └──────────────────┘       └──────────────┘     └─────────┘  │
│        (Gherkin)              (Step files)          (System)    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Behave as Python's Cucumber

Behave shares many concepts with Cucumber:

| Concept | Cucumber (Java) | Behave (Python) |
|---------|-----------------|-----------------|
| Feature files | `.feature` | `.feature` |
| Gherkin syntax | ✓ | ✓ |
| Step definitions | Java methods | Python functions |
| Hooks | `@Before`, `@After` | `environment.py` |
| Tags | `@tag` | `@tag` |
| Data tables | `DataTable` | `Table` |
| Scenario Outline | `Examples:` | `Examples:` |

### Key Characteristics

1. **Pure Python** - No Java dependencies, native Python experience
2. **Gherkin Syntax** - Same language as Cucumber for feature files
3. **Pythonic Design** - Follows Python conventions and idioms
4. **Context Object** - Shares state via `context` object
5. **Environment Hooks** - Centralized setup/teardown in `environment.py`
6. **Parse/cfparse** - Flexible step parameter matching

### Behave vs pytest-bdd

Python has two main BDD options:

| Aspect | Behave | pytest-bdd |
|--------|--------|------------|
| **Architecture** | Standalone framework | pytest plugin |
| **Syntax** | Full Gherkin support | Full Gherkin support |
| **Fixtures** | `environment.py` | pytest fixtures |
| **Parallel execution** | Limited built-in | pytest-xdist |
| **Integration** | Selenium, requests | Any pytest plugin |
| **Community** | Large, mature | Growing |
| **Learning curve** | Lower (standalone) | Lower if you know pytest |

**When to Choose Behave:**
- Dedicated BDD project structure
- Team familiar with Cucumber conventions
- Need for standalone BDD framework
- Traditional Given-When-Then workflow

**When to Choose pytest-bdd:**
- Existing pytest infrastructure
- Need pytest plugin ecosystem
- Want to mix BDD and traditional tests
- Prefer pytest's fixture system

### Behave Ecosystem

```
┌─────────────────────────────────────────────────────────────────┐
│                    Behave Ecosystem                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Core:                                                           │
│  ├── behave              - Main framework                       │
│  ├── behave-parallel     - Parallel execution                   │
│  └── behave-django       - Django integration                   │
│                                                                  │
│  Integrations:                                                   │
│  ├── selenium            - Browser automation                   │
│  ├── requests            - API testing                          │
│  ├── splinter            - High-level browser automation        │
│  └── appium-python       - Mobile testing                       │
│                                                                  │
│  Reporting:                                                      │
│  ├── allure-behave       - Allure reports                       │
│  ├── behave2cucumber     - Cucumber JSON format                 │
│  └── behave-html-formatter - HTML reports                       │
│                                                                  │
│  IDE Support:                                                    │
│  ├── PyCharm             - Built-in Gherkin support             │
│  ├── VS Code             - Cucumber extension                   │
│  └── Vim/Emacs           - Gherkin syntax plugins               │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Installation

**Install Behave:**
```bash
# Basic installation
pip install behave

# With Selenium support
pip install behave selenium webdriver-manager

# With all common dependencies
pip install behave selenium webdriver-manager allure-behave
```

**Verify Installation:**
```bash
behave --version
# behave 1.2.6
```

### Project Structure

Standard Behave project layout:

```
project/
├── features/
│   ├── login.feature           # Feature files
│   ├── checkout.feature
│   ├── steps/                   # Step definitions
│   │   ├── login_steps.py
│   │   ├── checkout_steps.py
│   │   └── common_steps.py
│   └── environment.py           # Hooks (setup/teardown)
├── pages/                       # Page objects (optional)
│   ├── login_page.py
│   └── checkout_page.py
├── behave.ini                   # Configuration
└── requirements.txt
```

### Quick Start Example

**features/login.feature:**
```gherkin
Feature: User Login
  As a registered user
  I want to log into my account
  So that I can access my personalized content

  Scenario: Successful login with valid credentials
    Given I am on the login page
    When I enter username "john@example.com"
    And I enter password "SecurePass123"
    And I click the login button
    Then I should see the dashboard
    And I should see welcome message "Welcome, John!"
```

**features/steps/login_steps.py:**
```python
from behave import given, when, then

@given('I am on the login page')
def step_on_login_page(context):
    context.browser.get("https://example.com/login")

@when('I enter username "{username}"')
def step_enter_username(context, username):
    context.browser.find_element("id", "username").send_keys(username)

@when('I enter password "{password}"')
def step_enter_password(context, password):
    context.browser.find_element("id", "password").send_keys(password)

@when('I click the login button')
def step_click_login(context):
    context.browser.find_element("id", "login-btn").click()

@then('I should see the dashboard')
def step_see_dashboard(context):
    assert "dashboard" in context.browser.current_url

@then('I should see welcome message "{message}"')
def step_see_welcome(context, message):
    welcome = context.browser.find_element("class name", "welcome")
    assert message in welcome.text
```

**features/environment.py:**
```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

def before_scenario(context, scenario):
    """Setup before each scenario"""
    service = Service(ChromeDriverManager().install())
    context.browser = webdriver.Chrome(service=service)
    context.browser.implicitly_wait(10)

def after_scenario(context, scenario):
    """Cleanup after each scenario"""
    if hasattr(context, 'browser'):
        context.browser.quit()
```

**Run the tests:**
```bash
# Run all tests
behave

# Run specific feature
behave features/login.feature

# Run with tags
behave --tags=@smoke
```

### Behave Community and Resources

**Official Resources:**
- [Behave Documentation](https://behave.readthedocs.io/) - Comprehensive guide
- [Behave GitHub](https://github.com/behave/behave) - Source code and issues

**Community:**
- Active development and maintenance
- Regular releases with bug fixes
- Good documentation coverage
- Stack Overflow support

**Learning Resources:**
- Official tutorial in documentation
- Example projects on GitHub
- Integration guides for Selenium, requests

### Comparison with Cucumber-JVM

**Feature File (Identical):**
```gherkin
# Same Gherkin syntax works in both
Feature: Shopping Cart
  Scenario: Add item to cart
    Given the product catalog contains "Widget"
    When I add "Widget" to my cart
    Then my cart should contain 1 item
```

**Step Definition - Cucumber (Java):**
```java
@Given("the product catalog contains {string}")
public void catalogContains(String product) {
    catalog.add(product);
}
```

**Step Definition - Behave (Python):**
```python
@given('the product catalog contains "{product}"')
def catalog_contains(context, product):
    context.catalog.add(product)
```

**Key Differences:**
- Python uses decorators, Java uses annotations
- Behave uses `context` object, Cucumber uses dependency injection
- Python string formatting vs Java method parameters

## Key Takeaways

1. **Behave** is Python's premier BDD framework using Gherkin syntax
2. **Same Gherkin** - Feature files work identically to Cucumber
3. **Python-native** - Uses decorators, context object, environment hooks
4. **Choose Behave** for dedicated BDD projects in Python
5. **Ecosystem** includes Selenium, Allure, Django integrations
6. **Tomorrow's pair programming** will give hands-on Behave practice

## Additional Resources

- [Behave Documentation](https://behave.readthedocs.io/) - Official documentation
- [Behave Tutorial](https://behave.readthedocs.io/en/stable/tutorial.html) - Getting started guide
- [Behave Examples](https://github.com/behave/behave/tree/main/features) - Official examples

