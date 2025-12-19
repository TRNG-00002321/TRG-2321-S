# Interview Questions: Week 8 - Selenium, System Testing

## Beginner (Foundational)

### Q1: What is the difference between `find_element` and `find_elements` in Python Selenium?
**Keywords:** Single, List, NoSuchElementException, Empty List
<details>
<summary>Click to Reveal Answer</summary>

`find_element` returns a single WebElement and raises a `NoSuchElementException` if no matching element is found. `find_elements` returns a list of all matching WebElements and returns an empty list (rather than raising an exception) if no elements match the locator.
</details>

---

### Q2: What is the purpose of the `By` class in Python Selenium?
**Keywords:** Locator Strategies, Element Location, ID, CSS Selector, XPath
<details>
<summary>Click to Reveal Answer</summary>

The `By` class provides constants for specifying locator strategies when finding elements. It includes options like `By.ID`, `By.NAME`, `By.CLASS_NAME`, `By.TAG_NAME`, `By.LINK_TEXT`, `By.PARTIAL_LINK_TEXT`, `By.CSS_SELECTOR`, and `By.XPATH`. Using the `By` class makes the code more readable and allows you to specify how elements should be located.
</details>

---

### Q3: What is the difference between implicit waits and explicit waits in Selenium?
**Keywords:** Global Timeout, Specific Condition, WebDriverWait, expected_conditions
<details>
<summary>Click to Reveal Answer</summary>

Implicit waits set a global timeout that applies to all `find_element` calls - Selenium will poll the DOM until the element is found or the timeout expires. Explicit waits (using `WebDriverWait` with `expected_conditions`) wait for a specific condition to be true for a specific element, providing more fine-grained control. Explicit waits are preferred because they can wait for conditions like visibility, clickability, or text presence.
</details>

---

### Q4: What is System Testing and how does it differ from Integration Testing?
**Keywords:** Complete System, Requirements, Component Interactions, End-to-End
<details>
<summary>Click to Reveal Answer</summary>

System Testing validates the complete, integrated system against specified requirements from an end-user perspective. It tests the system as a whole. Integration Testing focuses on testing the interactions and interfaces between individual components or modules. System testing occurs after integration testing and tests broader end-to-end workflows, while integration testing verifies that components work correctly when combined.
</details>

---

### Q5: What is Behavior-Driven Development (BDD) and what problem does it solve?
**Keywords:** Collaboration, Business Language, Given-When-Then, Three Amigos
<details>
<summary>Click to Reveal Answer</summary>

BDD is a software development approach that bridges the gap between technical and business stakeholders by writing tests in natural language that everyone can understand. It solves the problem of miscommunication between business requirements and implementation by using executable specifications written in Given-When-Then format. The Three Amigos practice (Business, Developer, QA) ensures shared understanding before development begins.
</details>

---

### Q6: What is Gherkin and what are its main keywords?
**Keywords:** Feature, Scenario, Given, When, Then
<details>
<summary>Click to Reveal Answer</summary>

Gherkin is a business-readable, domain-specific language used to describe software behavior in BDD frameworks like Cucumber and Behave. Its main keywords are: `Feature` (describes the feature being tested), `Scenario` (a specific test case), `Given` (preconditions/setup), `When` (actions being performed), `Then` (expected outcomes), and `And`/`But` (additional steps that take the meaning of the previous keyword).
</details>

---

### Q7: What is a stub and what is a driver in Integration Testing?
**Keywords:** Test Double, Top-Down, Bottom-Up, Simulate
<details>
<summary>Click to Reveal Answer</summary>

A stub is a dummy implementation of a lower-level module used in top-down integration testing. It simulates the behavior of modules that haven't been developed or integrated yet. A driver is a dummy implementation that calls the module under test, used in bottom-up integration testing. It simulates higher-level modules that would normally call the component being tested.
</details>

---

### Q8: What is the difference between Cucumber (Java) and Behave (Python)?
**Keywords:** Gherkin, Step Definitions, Context Object, Hooks
<details>
<summary>Click to Reveal Answer</summary>

Both Cucumber and Behave use Gherkin syntax for feature files, making them interchangeable at the specification level. The main differences are in implementation: Cucumber uses Java annotations (`@Given`, `@When`, `@Then`) and dependency injection, while Behave uses Python decorators (`@given`, `@when`, `@then`) and a `context` object for sharing state. Behave uses `environment.py` for hooks, while Cucumber uses `@Before` and `@After` annotations in Java classes.
</details>

---

### Q9: What is Playwright and how does it differ from Selenium?
**Keywords:** Microsoft, Auto-Wait, Browser Contexts, WebSocket
<details>
<summary>Click to Reveal Answer</summary>

Playwright is a modern browser automation library developed by Microsoft that offers built-in auto-waiting, browser contexts for parallel isolated sessions, and direct WebSocket communication with browsers. Unlike Selenium's WebDriver protocol, Playwright connects directly to browsers, resulting in faster execution. Playwright includes built-in features for video recording, tracing, and network interception that Selenium requires external tools for.
</details>

---

### Q10: What are Browser Contexts in Playwright and why are they useful?
**Keywords:** Isolation, Parallel Execution, Session, Cookies
<details>
<summary>Click to Reveal Answer</summary>

Browser Contexts are isolated browser sessions within a single browser instance. Each context has its own cookies, local storage, and session data - they don't share state. This enables parallel test execution without conflicts (different users can be logged in simultaneously), faster test execution (no need to restart browsers between tests), and clean state management. Contexts are much faster to create than launching new browser instances.
</details>

---

## Intermediate (Application)

### Q11: You have a web application where content loads dynamically via AJAX. How would you handle waiting for this content in Python Selenium?
**Hint:** Think about `WebDriverWait` and `expected_conditions`.
<details>
<summary>Click to Reveal Answer</summary>

You should use explicit waits with `WebDriverWait` and appropriate expected conditions. For example:

```python
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

wait = WebDriverWait(driver, 10)
element = wait.until(EC.visibility_of_element_located((By.ID, "ajax-content")))
```

You might also wait for a loading spinner to disappear using `EC.invisibility_of_element_located()`, or wait for specific text using `EC.text_to_be_present_in_element()`. Avoid using `time.sleep()` as it wastes time and creates brittle tests.
</details>

---

### Q12: A product owner asks you to write a BDD scenario for a shopping cart feature. They say "users should be able to add items to their cart." What questions would you ask in a Three Amigos session, and write an example scenario.
**Hint:** Think about edge cases, validation, and acceptance criteria.
<details>
<summary>Click to Reveal Answer</summary>

Questions to ask:
- What happens if the item is out of stock?
- Is there a maximum quantity limit?
- Should logged-out users be able to add items?
- What confirmation should the user see?
- Can the same item be added multiple times?

Example scenario:
```gherkin
Feature: Shopping Cart
  As a customer
  I want to add items to my cart
  So that I can purchase them later

  Scenario: Add available item to cart
    Given the product "Blue Widget" is in stock
    And I am viewing the product page for "Blue Widget"
    When I click the "Add to Cart" button
    Then my cart should contain 1 item
    And I should see a confirmation message "Blue Widget added to cart"

  Scenario: Cannot add out-of-stock item
    Given the product "Red Widget" is out of stock
    When I try to add "Red Widget" to my cart
    Then I should see an error message "This item is currently unavailable"
```
</details>

---

### Q13: Your team needs to run the same test scenario across multiple browsers (Chrome, Firefox, Safari). How would you approach this in Playwright vs Selenium?
**Hint:** Consider browser launching and parallel execution.
<details>
<summary>Click to Reveal Answer</summary>

**Playwright approach:**
Playwright has built-in support for all three browser engines. You can parameterize tests:
```java
// Playwright supports chromium, firefox, webkit natively
Browser chromeBrowser = playwright.chromium().launch();
Browser firefoxBrowser = playwright.firefox().launch();
Browser safariBrowser = playwright.webkit().launch();
```
Browser contexts enable easy parallel execution within the same test run.

**Selenium approach:**
You need separate WebDriver instances for each browser:
```python
from selenium import webdriver
chrome_driver = webdriver.Chrome()
firefox_driver = webdriver.Firefox()
# Safari requires SafariDriver setup
```
For parallel execution, you'd need Selenium Grid or a third-party tool like pytest-xdist.

Playwright's advantage is native parallel execution and consistent API across browsers; Selenium's advantage is broader browser support including legacy browsers.
</details>

---

### Q14: You're writing integration tests for a service that depends on a payment gateway that isn't available in your test environment. How would you approach this?
**Hint:** Consider stubs and what behavior to simulate.
<details>
<summary>Click to Reveal Answer</summary>

Create a stub that simulates the payment gateway's behavior:

```python
class PaymentGatewayStub:
    def process_payment(self, amount, card_details):
        """Stub that simulates payment processing"""
        # Simulate successful payment
        if card_details.get('number', '').startswith('4111'):
            return {'success': True, 'transaction_id': 'STUB-TXN-123'}
        # Simulate declined card
        if card_details.get('number', '').startswith('4000'):
            return {'success': False, 'error': 'Card declined'}
        return {'success': False, 'error': 'Invalid card'}
```

The stub should:
1. Handle the main success path
2. Simulate common failure scenarios (declined card, timeout)
3. Return realistic response formats
4. Allow testing edge cases without real transactions

This enables integration testing of your service layer without depending on external systems.
</details>

---

## Advanced (Deep Dive)

### Q15: Explain how Playwright's auto-wait mechanism works under the hood and why it's considered more reliable than Selenium's approach. What conditions does Playwright check before performing actions?
<details>
<summary>Click to Reveal Answer</summary>

Playwright's auto-wait mechanism performs actionability checks before every action. When you call `page.locator("#button").click()`, Playwright automatically:

1. **Attached** - Waits for element to be attached to the DOM
2. **Visible** - Waits for element to have non-empty bounding box and no `visibility:hidden`
3. **Stable** - Waits for element to stop moving (animations complete)
4. **Receives Events** - Checks element is not obscured by other elements
5. **Enabled** - Ensures element is not disabled

This is more reliable than Selenium because:
- **Single protocol connection**: Playwright uses WebSocket for direct browser communication, avoiding the latency of Selenium's WebDriver protocol
- **Automatic retrying**: Actions are automatically retried until timeout, not just element finding
- **Actionability checks**: Selenium's `element_to_be_clickable` only checks visibility and enabled state, not stability or event reception

Web-first assertions in Playwright also auto-retry, comparing to Selenium where assertions fail immediately if the condition isn't met at that exact moment. This architecture eliminates most flaky tests caused by timing issues in modern single-page applications where elements frequently animate and load asynchronously.
</details>

---

## Question Distribution Summary

| Difficulty | Count | Percentage |
|------------|-------|------------|
| Beginner | 10 | 67% |
| Intermediate | 4 | 27% |
| Advanced | 1 | 6% |
| **Total** | **15** | **100%** |

## Topics Covered

- **Monday**: Python Selenium (locators, waits, `By` class, `find_element`/`find_elements`)
- **Tuesday**: System Testing vs Integration Testing (stubs, drivers, test levels)
- **Wednesday**: Cucumber & BDD (Gherkin, Three Amigos, Given-When-Then)
- **Thursday**: Behave Framework (Python BDD, context object, comparison with Cucumber)
- **Friday**: Playwright (auto-wait, browser contexts, architecture comparison with Selenium)

