# Weekly Knowledge Check: Week 8 - Selenium, System Testing

Test your understanding of Selenium Python, System Testing, Integration Testing, BDD with Cucumber and Behave, and Playwright Java.

---

## Part 1: Multiple Choice - Selenium Python

### 1. Which locator strategy is generally considered the fastest and most reliable when available in Python Selenium?

- [ ] A) `By.CLASS_NAME`
- [ ] B) `By.XPATH`
- [ ] C) `By.ID`
- [ ] D) `By.CSS_SELECTOR`

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** C) `By.ID`

**Explanation:** ID is the fastest locator strategy because IDs are designed to be unique within a page, allowing the browser to use highly optimized lookup methods. The `By` class documentation and best practices prioritize ID when available.
- **Why others are wrong:**
  - A) `By.CLASS_NAME` has low reliability because classes are often shared among multiple elements
  - B) `By.XPATH` is powerful but slower due to the complexity of XPath parsing
  - D) `By.CSS_SELECTOR` is fast and flexible but still not as optimized as ID lookup
</details>

---

### 2. What does the following Python Selenium code return?

```python
driver.find_elements(By.TAG_NAME, "a")
```

- [ ] A) The first anchor element found
- [ ] B) A list of all anchor elements
- [ ] C) NoSuchElementException if no elements found
- [ ] D) None if no elements found

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) A list of all anchor elements

**Explanation:** The `find_elements` (plural) method returns a list of all matching WebElements. If no elements match, it returns an empty list, not an exception.
- **Why others are wrong:**
  - A) `find_element` (singular) returns the first element; `find_elements` returns all matching elements
  - C) `find_elements` returns an empty list when no matches found; `find_element` raises NoSuchElementException
  - D) It returns an empty list `[]`, not `None`
</details>

---

### 3. Which XPath expression correctly finds a button with the exact text "Login"?

- [ ] A) `//button[@text='Login']`
- [ ] B) `//button[text()='Login']`
- [ ] C) `//button[contains='Login']`
- [ ] D) `//button{text='Login'}`

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) `//button[text()='Login']`

**Explanation:** The `text()` function in XPath returns the text content of an element. To match exact text, use `text()='value'` inside square brackets.
- **Why others are wrong:**
  - A) `@text` would look for an attribute named "text", not the element's text content
  - C) `contains` is a function that requires parentheses: `contains(text(), 'Login')`
  - D) XPath uses square brackets `[]` for predicates, not curly braces `{}`
</details>

---

### 4. What is the purpose of `WebDriverWait` combined with `expected_conditions` in Python Selenium?

- [ ] A) To pause execution for a fixed time period
- [ ] B) To wait for specific conditions before proceeding
- [ ] C) To set a global timeout for all operations
- [ ] D) To retry failed element interactions

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) To wait for specific conditions before proceeding

**Explanation:** `WebDriverWait` with `expected_conditions` provides explicit waiting that polls for a specific condition (like element visibility or clickability) until it's met or timeout occurs. This is more reliable than fixed sleeps.
- **Why others are wrong:**
  - A) That would be `time.sleep()`, which is not recommended
  - C) `driver.implicitly_wait()` sets a global timeout; `WebDriverWait` is for specific conditions
  - D) Retrying is a side effect of polling, but the primary purpose is waiting for conditions
</details>

---

### 5. Which method is used to switch back to the main document after interacting with an iframe?

- [ ] A) `driver.switch_to.main_document()`
- [ ] B) `driver.switch_to.default_content()`
- [ ] C) `driver.switch_to.parent()`
- [ ] D) `driver.exit_frame()`

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) `driver.switch_to.default_content()`

**Explanation:** The `switch_to.default_content()` method returns focus to the main document (top-level frame). This is essential after working with iframes.
- **Why others are wrong:**
  - A) There is no `main_document()` method in Selenium
  - C) `switch_to.parent_frame()` goes up one level (for nested frames), not necessarily to the main document
  - D) `exit_frame()` is not a valid Selenium method
</details>

---

### 6. What is the recommended approach for handling multiple browser windows in Python Selenium?

- [ ] A) Use `driver.switch_to.alert()` for each window
- [ ] B) Store `current_window_handle` before opening new window, then use `window_handles` to switch
- [ ] C) Create a new WebDriver instance for each window
- [ ] D) Use `driver.get()` to navigate between windows

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) Store `current_window_handle` before opening new window, then use `window_handles` to switch

**Explanation:** The correct pattern is: (1) store the main window handle, (2) perform action that opens new window, (3) iterate through `window_handles` to find and switch to the new window, (4) switch back to main window when done.
- **Why others are wrong:**
  - A) `switch_to.alert()` is for JavaScript alerts, not browser windows
  - C) Creating new WebDriver instances is expensive and doesn't manage existing windows
  - D) `driver.get()` navigates within the current window, doesn't switch between windows
</details>

---

## Part 2: Multiple Choice - System & Integration Testing

### 7. In the software testing hierarchy, what is the primary focus of system testing?

- [ ] A) Testing individual functions in isolation
- [ ] B) Testing interfaces between components
- [ ] C) Testing the complete integrated system against requirements
- [ ] D) Testing code coverage metrics

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** C) Testing the complete integrated system against requirements

**Explanation:** System testing validates the fully integrated system to verify it meets specified requirements. It tests the system as a whole from an end-user perspective.
- **Why others are wrong:**
  - A) Testing individual functions in isolation is unit testing
  - B) Testing interfaces between components is integration testing
  - D) Code coverage is a metric, not a testing level
</details>

---

### 8. Which integration testing approach starts from the top-level modules and progressively integrates lower-level modules?

- [ ] A) Bottom-Up Integration
- [ ] B) Big Bang Integration
- [ ] C) Top-Down Integration
- [ ] D) Sandwich Integration

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** C) Top-Down Integration

**Explanation:** Top-Down Integration starts with high-level modules and integrates lower modules progressively. Lower modules that aren't ready yet are replaced with stubs.
- **Why others are wrong:**
  - A) Bottom-Up starts with low-level modules and integrates upward using drivers
  - B) Big Bang integrates all components simultaneously
  - D) Sandwich (Hybrid) combines both top-down and bottom-up approaches
</details>

---

### 9. What is a "stub" in the context of integration testing?

- [ ] A) A real database used for testing
- [ ] B) A dummy implementation of a lower-level module
- [ ] C) A test that runs automatically on commit
- [ ] D) A dummy implementation that calls the module under test

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) A dummy implementation of a lower-level module

**Explanation:** Stubs are used in top-down integration testing to simulate lower-level modules that aren't yet integrated. They provide simplified implementations that allow higher-level code to be tested.
- **Why others are wrong:**
  - A) Real databases are used in actual tests, not simulations
  - C) That describes a CI/CD trigger, not a stub
  - D) That describes a driver, which simulates higher-level modules calling the module under test
</details>

---

### 10. Which statement best describes the difference between system testing and integration testing?

- [ ] A) System testing uses mocks; integration testing uses real components
- [ ] B) System testing validates requirements; integration testing validates interfaces
- [ ] C) System testing is done by developers; integration testing is done by QA
- [ ] D) System testing is automated; integration testing is manual

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) System testing validates requirements; integration testing validates interfaces

**Explanation:** Integration testing focuses on verifying that components communicate correctly through their interfaces. System testing validates the complete system against functional and non-functional requirements.
- **Why others are wrong:**
  - A) Both can use real components; integration testing often uses some real dependencies
  - C) Either level can be performed by developers or QA
  - D) Both levels can and should be automated
</details>

---

## Part 3: Multiple Choice - BDD & Cucumber

### 11. What does BDD stand for, and what is its primary focus?

- [ ] A) Bug Detection Development - finding defects early
- [ ] B) Behavior-Driven Development - describing system behavior in business terms
- [ ] C) Build-Deploy-Debug - continuous integration practices
- [ ] D) Branch-Develop-Deploy - version control workflow

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) Behavior-Driven Development - describing system behavior in business terms

**Explanation:** BDD is a software development approach that describes system behavior using natural language that business stakeholders can understand. It focuses on collaboration between business, development, and QA.
- **Why others are wrong:**
  - A) BDD is broader than defect detection
  - C) This is not a real acronym in the testing domain
  - D) This describes Git workflows, not testing practices
</details>

---

### 12. What is the "Three Amigos" practice in BDD?

- [ ] A) A testing framework for three different languages
- [ ] B) A collaboration session between Business, Developer, and Tester
- [ ] C) Running tests on three different browsers
- [ ] D) Creating three types of test cases per feature

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) A collaboration session between Business, Developer, and Tester

**Explanation:** The Three Amigos is a BDD practice where Product Owner (Business), Developer, and Tester collaborate before development to discuss requirements, clarify acceptance criteria, and create concrete examples (scenarios).
- **Why others are wrong:**
  - A) Three Amigos refers to people, not programming languages
  - C) Cross-browser testing is a separate practice
  - D) The number of test cases varies; "Three" refers to the three roles
</details>

---

### 13. Which Gherkin keyword is used to define the initial state or preconditions in a scenario?

- [ ] A) `When`
- [ ] B) `Then`
- [ ] C) `Given`
- [ ] D) `And`

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** C) `Given`

**Explanation:** `Given` defines preconditions or the initial context of the scenario. It sets up the state before the action being tested.
- **Why others are wrong:**
  - A) `When` describes the action being performed
  - B) `Then` describes the expected outcome
  - D) `And` continues the previous step type (Given/When/Then)
</details>

---

### 14. What is the purpose of Scenario Outline with Examples table in Cucumber?

- [ ] A) To document test requirements
- [ ] B) To run the same scenario with different data sets
- [ ] C) To define background steps shared across scenarios
- [ ] D) To skip certain tests based on tags

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) To run the same scenario with different data sets

**Explanation:** Scenario Outline combined with Examples tables allows data-driven testing. The scenario is executed once for each row in the Examples table, with placeholders replaced by actual values.
- **Why others are wrong:**
  - A) Documentation is a benefit, but data-driven testing is the primary purpose
  - C) `Background` keyword defines shared setup steps
  - D) Tags and tag expressions control test filtering
</details>

---

### 15. In Cucumber Java, what annotation is used to define hooks that run before each scenario?

- [ ] A) `@BeforeScenario`
- [ ] B) `@Before`
- [ ] C) `@Setup`
- [ ] D) `@Precondition`

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) `@Before`

**Explanation:** The `@Before` annotation in Cucumber marks a method to run before each scenario. `@After` runs after each scenario. These come from `io.cucumber.java.Before`.
- **Why others are wrong:**
  - A) `@BeforeScenario` is not a valid Cucumber annotation
  - C) `@Setup` is not used in Cucumber (JUnit uses `@BeforeEach`)
  - D) `@Precondition` is not a Cucumber annotation
</details>

---

## Part 4: Multiple Choice - Behave (Python BDD)

### 16. How does Behave share state between step definitions?

- [ ] A) Using global variables
- [ ] B) Using the `context` object passed to each step
- [ ] C) Using class instance variables
- [ ] D) Using environment variables

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) Using the `context` object passed to each step

**Explanation:** Behave passes a `context` object to every step function. This object is used to share state (like browser instances, test data) between steps within a scenario.
- **Why others are wrong:**
  - A) Global variables work but are not the recommended Behave pattern
  - C) Steps are functions, not class methods in standard Behave
  - D) Environment variables are for configuration, not runtime state sharing
</details>

---

### 17. Where are Behave hooks (like `before_scenario` and `after_scenario`) typically defined?

- [ ] A) In each step definition file
- [ ] B) In `environment.py` in the features directory
- [ ] C) In a `hooks.py` file
- [ ] D) In `behave.ini`

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) In `environment.py` in the features directory

**Explanation:** Behave looks for hooks in `features/environment.py`. This file contains setup/teardown functions like `before_scenario()`, `after_scenario()`, `before_feature()`, etc.
- **Why others are wrong:**
  - A) Step definition files contain step implementations, not hooks
  - C) `hooks.py` is not a recognized Behave convention
  - D) `behave.ini` is for configuration options, not hook code
</details>

---

### 18. What is the correct decorator syntax to define a Given step in Behave?

- [ ] A) `@given("I am on the login page")`
- [ ] B) `@Given("I am on the login page")`
- [ ] C) `@step("Given I am on the login page")`
- [ ] D) `@Given(step="I am on the login page")`

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** A) `@given("I am on the login page")`

**Explanation:** Behave uses lowercase decorators: `@given`, `@when`, `@then`. The step text is passed as a string argument to the decorator.
- **Why others are wrong:**
  - B) `@Given` with capital G is Cucumber Java syntax, not Behave Python
  - C) There's no `@step` decorator; specific keywords are used
  - D) Behave uses positional string argument, not `step=` keyword
</details>

---

## Part 5: Multiple Choice - Playwright Java

### 19. What is a key advantage of Playwright's browser contexts over traditional Selenium sessions?

- [ ] A) Browser contexts require less memory
- [ ] B) Browser contexts provide isolated sessions within the same browser instance
- [ ] C) Browser contexts automatically fix flaky tests
- [ ] D) Browser contexts are only available in headless mode

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) Browser contexts provide isolated sessions within the same browser instance

**Explanation:** Browser contexts are like incognito profiles - they have separate cookies, storage, and session state while sharing the same browser process. This enables parallel testing without interference.
- **Why others are wrong:**
  - A) Memory usage is similar; the advantage is isolation
  - C) Auto-wait helps with flakiness, not contexts specifically
  - D) Contexts work in both headed and headless modes
</details>

---

### 20. Which Playwright feature automatically waits for elements to be ready before interacting?

- [ ] A) Explicit waits
- [ ] B) Implicit waits
- [ ] C) Auto-wait
- [ ] D) Sleep timers

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** C) Auto-wait

**Explanation:** Playwright's auto-wait is a built-in feature that automatically waits for elements to be attached, visible, stable, enabled, and ready to receive events before performing actions like `click()`.
- **Why others are wrong:**
  - A) Explicit waits require manual code; auto-wait is automatic
  - B) Implicit waits are a Selenium concept; Playwright uses auto-wait
  - D) Sleep timers are arbitrary delays, not intelligent waiting
</details>

---

### 21. What does the Playwright Trace Viewer capture during test execution?

- [ ] A) Only screenshots
- [ ] B) Only network requests
- [ ] C) Screenshots, DOM snapshots, network activity, console logs, and action timeline
- [ ] D) Only test assertions

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** C) Screenshots, DOM snapshots, network activity, console logs, and action timeline

**Explanation:** Playwright traces capture comprehensive execution data including screenshots at each action, DOM snapshots, network requests/responses, console output, and a timeline of all actions - all packaged in a `.zip` file viewable in Trace Viewer.
- **Why others are wrong:**
  - A) Screenshots are captured, but traces include much more
  - B) Network activity is one component, not the only thing captured
  - D) Traces capture execution flow, not just assertion results
</details>

---

### 22. In Playwright visual testing, what is the purpose of the `mask` option?

- [ ] A) To encrypt screenshot data
- [ ] B) To hide dynamic content that changes between runs
- [ ] C) To compress the screenshot file
- [ ] D) To add a watermark to screenshots

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) To hide dynamic content that changes between runs

**Explanation:** The `mask` option in `hasScreenshot()` allows you to specify locators for elements that should be masked (hidden) during comparison. This prevents false positives from timestamps, ads, or other dynamic content.
- **Why others are wrong:**
  - A) Screenshots are not encrypted; mask hides dynamic elements
  - C) Compression is handled separately, not related to masking
  - D) Masking hides content, doesn't add anything to the image
</details>

---

## Part 6: True/False

### 23. True or False: In Python Selenium, `By.CLASS_NAME` can match elements with multiple CSS classes by passing the full class string.

- [ ] True
- [ ] False

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** `By.CLASS_NAME` only matches a single class. Attempting to use multiple classes like `"btn btn-primary"` will fail. For multiple classes, use `By.CSS_SELECTOR` with `.btn.btn-primary`.
</details>

---

### 24. True or False: XPath 1.0 (used by browsers) includes an `ends-with()` function.

- [ ] True
- [ ] False

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** XPath 1.0, which is what browsers implement, does not include `ends-with()`. This function was added in XPath 2.0. As a workaround, you can use `contains()` or `substring()` to match endings.
</details>

---

### 25. True or False: Integration testing focuses on testing the entire system against business requirements.

- [ ] True
- [ ] False

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** Integration testing focuses on testing interfaces and interactions between components. System testing tests the complete integrated system against requirements. Integration testing validates that components communicate correctly together.
</details>

---

### 26. True or False: In BDD, scenarios should be written in technical language that describes implementation details.

- [ ] True
- [ ] False

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** BDD scenarios should use business language that non-technical stakeholders can understand. Technical details like CSS selectors, URLs, and database operations should be hidden in step definitions, not exposed in feature files.
</details>

---

### 27. True or False: Behave uses the same Gherkin syntax as Cucumber for feature files.

- [ ] True
- [ ] False

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** True

**Explanation:** Behave uses Gherkin syntax identical to Cucumber. Feature files with Given/When/Then steps work the same way in both frameworks. The main differences are in step definition syntax (Python decorators vs Java annotations).
</details>

---

### 28. True or False: Playwright's browser contexts share cookies and local storage between each other.

- [ ] True
- [ ] False

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** Browser contexts are completely isolated from each other. Each context has its own cookies, local storage, and session storage. This isolation enables parallel testing without data conflicts between tests.
</details>

---

## Part 7: Code Prediction

### 29. What will this Python Selenium code output?

```python
from selenium import webdriver
from selenium.webdriver.common.by import By

driver = webdriver.Chrome()
driver.implicitly_wait(10)
driver.get("https://example.com")

elements = driver.find_elements(By.CLASS_NAME, "nonexistent-class")
print(len(elements))

driver.quit()
```

- [ ] A) NoSuchElementException
- [ ] B) 0
- [ ] C) None
- [ ] D) TimeoutException

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) 0

**Explanation:** `find_elements` (plural) returns an empty list when no elements match the locator. Unlike `find_element` (singular), it does not raise an exception. The `len()` of an empty list is 0.
</details>

---

### 30. What does this XPath expression match?

```xpath
//div[contains(@class, 'error') and contains(text(), 'failed')]
```

- [ ] A) Any div with class exactly equal to "error"
- [ ] B) Any div where class contains "error" AND text contains "failed"
- [ ] C) Any div where class contains "error" OR text contains "failed"
- [ ] D) Any element with class containing "error"

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) Any div where class contains "error" AND text contains "failed"

**Explanation:** The XPath uses `and` to combine two conditions: `contains(@class, 'error')` checks if the class attribute contains "error", and `contains(text(), 'failed')` checks if the element's text contains "failed". Both must be true.
</details>

---

### 31. What will this Gherkin step match?

```gherkin
When I add "Laptop" to my cart
```

Which step definition will match (in Behave)?

- [ ] A) `@when('I add {product} to my cart')`
- [ ] B) `@when('I add "{product}" to my cart')`
- [ ] C) `@when('I add <product> to my cart')`
- [ ] D) `@when('I add $product to my cart')`

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) `@when('I add "{product}" to my cart')`

**Explanation:** In Behave, quoted strings in Gherkin steps are matched using `"{parameter}"` syntax with the quotes included in the pattern. The curly braces capture the parameter value.
- **Why others are wrong:**
  - A) Without quotes, this wouldn't match the quoted string in the step
  - C) Angle brackets `<>` are used in Scenario Outline placeholders, not step definitions
  - D) `$` is not used in Behave parameter syntax
</details>

---

## Part 8: Fill-in-the-Blank

### 32. In Python Selenium, the method to switch to a new browser tab opened via Selenium 4 is `driver.switch_to.new_window('_____')`.

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** tab

**Explanation:** Selenium 4 introduced `switch_to.new_window('tab')` to open and switch to a new tab. Using `'window'` opens a new browser window instead.
</details>

---

### 33. In Cucumber Gherkin, the keyword used to define data-driven scenarios with multiple examples is _____ Outline.

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** Scenario

**Explanation:** `Scenario Outline` (or `Scenario Template`) combined with `Examples:` tables enables data-driven testing. The scenario runs once for each row in the Examples table.
</details>

---

### 34. The integration testing approach that combines top-down and bottom-up, meeting in the middle, is called _____ integration.

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** Sandwich (or Hybrid)

**Explanation:** Sandwich integration (also called Hybrid) uses both top-down and bottom-up approaches simultaneously, with integration meeting at a target layer in the middle.
</details>

---

### 35. In Playwright Java, traces are started using `context.tracing()._____(options)`.

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** start

**Explanation:** Trace recording is initiated with `context.tracing().start(new Tracing.StartOptions()...)` and stopped with `context.tracing().stop(...)`.
</details>

---

### 36. The Python BDD framework that uses `environment.py` for hooks is called _____.

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** Behave

**Explanation:** Behave is Python's BDD framework that uses `features/environment.py` for defining hooks like `before_scenario()`, `after_scenario()`, etc.
</details>

---

## Part 9: Advanced Multiple Choice

### 37. Which expected condition should you use when you need to click a button in Python Selenium?

- [ ] A) `EC.presence_of_element_located()`
- [ ] B) `EC.visibility_of_element_located()`
- [ ] C) `EC.element_to_be_clickable()`
- [ ] D) `EC.text_to_be_present_in_element()`

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** C) `EC.element_to_be_clickable()`

**Explanation:** `element_to_be_clickable()` waits until the element is both visible AND enabled. This is the recommended condition before clicking, as it ensures the element can actually receive the click event.
- **Why others are wrong:**
  - A) `presence_of_element_located` only checks DOM presence, not visibility or enabled state
  - B) `visibility_of_element_located` checks visibility but not if the element is enabled
  - D) `text_to_be_present_in_element` checks text content, not clickability
</details>

---

### 38. Which anti-pattern should be avoided when writing BDD scenarios?

- [ ] A) Using Given-When-Then structure
- [ ] B) Including CSS selectors and XPath in scenario steps
- [ ] C) Collaborating with stakeholders
- [ ] D) Using Examples tables for data variation

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) Including CSS selectors and XPath in scenario steps

**Explanation:** Technical implementation details like CSS selectors, XPath, database queries, and API endpoints should be hidden in step definitions. Scenarios should use business language that stakeholders can understand.
- **Why others are wrong:**
  - A) Given-When-Then is the correct BDD structure
  - C) Stakeholder collaboration is a core BDD principle
  - D) Examples tables are the proper way to do data-driven BDD
</details>

---

### 39. What is the main difference between Playwright's `assertThat(page).hasScreenshot()` and manually taking screenshots?

- [ ] A) Manual screenshots are higher quality
- [ ] B) `hasScreenshot()` automatically compares against baseline and fails on difference
- [ ] C) Manual screenshots support more image formats
- [ ] D) `hasScreenshot()` requires an external comparison tool

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) `hasScreenshot()` automatically compares against baseline and fails on difference

**Explanation:** `hasScreenshot()` performs visual testing: on first run it creates a baseline, on subsequent runs it compares against the baseline and fails the test if differences exceed the threshold.
- **Why others are wrong:**
  - A) Quality is comparable between methods
  - C) Both support PNG format
  - D) Playwright has built-in comparison; no external tool needed
</details>

---

### 40. In the V-Model, which testing level corresponds to System Design?

- [ ] A) Unit Testing
- [ ] B) Integration Testing
- [ ] C) System Testing
- [ ] D) Acceptance Testing

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** C) System Testing

**Explanation:** In the V-Model, each development phase has a corresponding testing phase:
- Module Design ↔ Unit Testing
- Architecture Design ↔ Integration Testing  
- System Design ↔ System Testing
- Requirements Analysis ↔ Acceptance Testing
</details>

---

## Part 10: Scenario-Based Questions

### 41. You need to test a web application that opens a popup window for payment. What is the correct sequence of Selenium Python operations?

- [ ] A) Click payment button → Get new window handle → Switch to popup → Complete payment → Close popup → Switch back
- [ ] B) Store main window handle → Click payment button → Wait for new window → Switch to popup → Complete payment → Close popup → Switch to main window
- [ ] C) Click payment button → Wait for popup → driver.switch_to.alert() → Complete payment
- [ ] D) Open new browser instance → Navigate to payment URL → Complete payment

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) Store main window handle → Click payment button → Wait for new window → Switch to popup → Complete payment → Close popup → Switch to main window

**Explanation:** The correct pattern requires storing the main window handle first (so you can return to it), waiting for the new window to appear, switching to it, performing actions, closing it, and switching back to the original window.
- **Why others are wrong:**
  - A) Missing the crucial first step of storing the main window handle
  - C) `switch_to.alert()` is for JavaScript alerts, not browser windows
  - D) Creating new browser instances doesn't manage popup windows
</details>

---

### 42. A team is adopting BDD but writing scenarios after the code is complete. What is this anti-pattern called and why is it problematic?

- [ ] A) Test-First - it's actually the correct approach
- [ ] B) Specification by Example - scenarios should be written before development to drive shared understanding
- [ ] C) Technical Debt - it creates maintenance issues
- [ ] D) Feature Bloat - it adds unnecessary tests

<details>
<summary><b>Click for Solution</b></summary>

**Correct Answer:** B) Specification by Example - scenarios should be written before development to drive shared understanding

**Explanation:** Writing scenarios after code defeats BDD's purpose. BDD is about discovering requirements through collaboration BEFORE development. When written afterward, scenarios become just documentation, missing the opportunity to clarify requirements and prevent misunderstandings.
- **Why others are wrong:**
  - A) Test-First is a TDD concept; this is the opposite of test-first
  - C) Technical debt is accumulated shortcuts, not a BDD-specific anti-pattern
  - D) Feature bloat refers to excessive features, not test timing
</details>

---

I have generated the Practice Quiz with detailed explanations for Week 8: Selenium, System Testing. Please review the quiz at `weeklytechrepo/week8-selenium-system-testing/quiz-questions.md`.

**Summary:**
- 42 total questions covering all 5 days of content
- Multiple formats: MCQ, True/False, Code Prediction, Fill-in-the-Blank, Scenario-Based
- Every question includes detailed explanations and distractor analysis
- Topics covered:
  - Monday: Selenium Python (locators, XPath, waits, window/frame handling)
  - Tuesday: System Testing & Integration Testing concepts
  - Wednesday: BDD & Cucumber (Gherkin, Three Amigos, anti-patterns)
  - Thursday: Behave Framework (context object, environment.py, decorators)
  - Friday: Playwright Java (auto-wait, browser contexts, visual testing, tracing)

