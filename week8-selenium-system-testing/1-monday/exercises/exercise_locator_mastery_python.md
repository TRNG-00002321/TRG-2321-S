# Exercise 3: Locator Mastery with Python

## Objective

Practice all locator strategies on a provided webpage and document the best approach for each element type. Master XPath and CSS selectors for complex element location.

## Learning Goals

- Apply all 8 Selenium locator strategies
- Build robust XPath expressions
- Write efficient CSS selectors
- Document locator strategy decisions
- Handle elements with dynamic attributes

## Time Estimate

45 minutes

## Prerequisites

- Completed Exercises 1 and 2
- Read `locator-strategies-python.md` and `xpath-python.md`

---

## Core Tasks

### Task 1: Locator Strategy Comparison (20 minutes)

Create `tests/test_locators.py` that demonstrates ALL locator strategies on the following page:
**https://the-internet.herokuapp.com/login**

```python
"""
Locator Strategy Mastery Tests

Demonstrate all 8 locator strategies and compare their usage.
"""

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import pytest


@pytest.fixture
def driver():
    """Create a Chrome driver for testing."""
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    driver.implicitly_wait(10)
    driver.get("https://the-internet.herokuapp.com/login")
    yield driver
    driver.quit()


class TestLocatorStrategies:
    """
    Test all 8 locator strategies on the login page.
    
    Page Elements to locate:
    - Username input: <input type="text" name="username" id="username">
    - Password input: <input type="password" name="password" id="password">
    - Login button: <button type="submit" class="radius">Login</button>
    - Page heading: <h2>Login Page</h2>
    - Subheading link: <a href="...">Elemental Selenium</a>
    """
    
    def test_locate_by_id(self, driver):
        """Locate elements by ID attribute."""
        # TODO: Locate username field by ID
        username = driver.find_element(By.ID, "username")
        assert username.is_displayed()
        
        # TODO: Locate password field by ID
        # YOUR CODE HERE
    
    def test_locate_by_name(self, driver):
        """Locate elements by name attribute."""
        # TODO: Locate username field by name
        # YOUR CODE HERE
        pass
    
    def test_locate_by_class_name(self, driver):
        """Locate elements by class name."""
        # TODO: Locate the login button by class name "radius"
        # YOUR CODE HERE
        pass
    
    def test_locate_by_tag_name(self, driver):
        """Locate elements by tag name."""
        # TODO: Find all input elements on the page
        # YOUR CODE HERE
        # Assert there are at least 2 inputs (username, password)
        pass
    
    def test_locate_by_link_text(self, driver):
        """Locate anchor elements by exact link text."""
        # TODO: Locate the "Elemental Selenium" link by exact text
        # YOUR CODE HERE
        pass
    
    def test_locate_by_partial_link_text(self, driver):
        """Locate anchor elements by partial link text."""
        # TODO: Locate the link using partial text "Elemental"
        # YOUR CODE HERE
        pass
    
    def test_locate_by_css_selector(self, driver):
        """Locate elements by CSS selector."""
        # TODO: Implement CSS selector examples
        
        # Basic: Element by ID
        username_by_id = driver.find_element(By.CSS_SELECTOR, "#username")
        
        # Basic: Element by class
        button_by_class = driver.find_element(By.CSS_SELECTOR, ".radius")
        
        # Compound: Element with multiple attributes
        # YOUR CODE HERE
        
        # Child selector: form > input
        # YOUR CODE HERE
        
        # Attribute selector: input[type='password']
        # YOUR CODE HERE
        pass
    
    def test_locate_by_xpath(self, driver):
        """Locate elements by XPath."""
        # TODO: Implement XPath examples
        
        # Basic: By ID
        username_xpath = driver.find_element(By.XPATH, "//input[@id='username']")
        
        # Text content: Find heading by text
        heading = driver.find_element(By.XPATH, "//h2[text()='Login Page']")
        
        # Contains function: Find element with partial text
        # YOUR CODE HERE
        
        # Following sibling: Find password after username
        # YOUR CODE HERE
        
        # Parent axis: Find form containing username
        # YOUR CODE HERE
        pass


class TestXPathAdvanced:
    """Advanced XPath exercises."""
    
    def test_xpath_contains(self, driver):
        """
        Use contains() for partial attribute matching.
        Find elements where attribute contains specific text.
        """
        # Find button that contains "Login" text
        # YOUR CODE HERE
        pass
    
    def test_xpath_starts_with(self, driver):
        """
        Use starts-with() for prefix matching.
        Useful for dynamic IDs like "user_12345".
        """
        # Example: //input[starts-with(@id, 'user')]
        # YOUR CODE HERE
        pass
    
    def test_xpath_text_functions(self, driver):
        """
        Use text(), normalize-space() for text matching.
        """
        # Find heading with exact text
        heading = driver.find_element(By.XPATH, "//h2[text()='Login Page']")
        
        # Find element with text containing whitespace (use normalize-space)
        # YOUR CODE HERE
        pass
    
    def test_xpath_axes(self, driver):
        """
        Use XPath axes for relative element location.
        """
        # Following: Find element after username
        # YOUR CODE HERE
        
        # Preceding: Find element before password
        # YOUR CODE HERE
        
        # Parent: Find parent of username input
        # YOUR CODE HERE
        
        # Ancestor: Find form ancestor of button
        # YOUR CODE HERE
        pass
```

### Task 2: Locator Strategy Analysis Document (15 minutes)

Create `locator_analysis.md` documenting your findings:

```markdown
# Locator Strategy Analysis

## Page Analyzed
URL: https://the-internet.herokuapp.com/login

## Element Inventory

| Element | ID | Name | Class | Other Attributes |
|---------|-----|------|-------|------------------|
| Username Input | username | username | - | type="text" |
| Password Input | | | | |
| Login Button | | | | |
| Page Heading | | | | |
| Footer Link | | | | |

## Recommended Locators

### 1. Username Input

| Strategy | Locator | Reliability | Reasoning |
|----------|---------|-------------|-----------|
| ID | `By.ID, "username"` | ⭐⭐⭐⭐⭐ | Unique, stable |
| Name | | | |
| CSS | | | |
| XPath | | | |

**Recommendation:** Use ID - it's unique and least likely to change.

### 2. Password Input
<!-- Complete for remaining elements -->

### 3. Login Button
<!-- Button analysis -->

### 4. Page Heading
<!-- Heading analysis -->

## Complex Locator Scenarios

### Scenario 1: Dynamic IDs
If username field had ID like "username_a1b2c3":
- **Bad:** `By.ID, "username_a1b2c3"` (changes on each load)
- **Good:** `By.XPATH, "//input[starts-with(@id, 'username')]"`

### Scenario 2: Multiple Similar Elements
<!-- Document strategies for handling -->

### Scenario 3: Elements Inside Iframes
<!-- Document approach -->

## Key Learnings

1. 
2. 
3. 
```

### Task 3: Dynamic Locator Challenges (10 minutes)

Test your locators on a more complex page:
**https://the-internet.herokuapp.com/challenging_dom**

```python
"""
Test locators on the Challenging DOM page.

This page has:
- Buttons with random IDs that change on each load
- A table with dynamically generated content
- Elements without stable identifiers
"""

class TestChallengingDOM:
    """Handle elements with dynamic/unstable attributes."""
    
    def test_locate_by_relative_position(self, driver):
        """
        Locate buttons without using ID.
        The page has 3 buttons: blue, red, green
        """
        driver.get("https://the-internet.herokuapp.com/challenging_dom")
        
        # Strategy: Use class name which is stable
        # YOUR CODE HERE
        pass
    
    def test_locate_table_cells(self, driver):
        """
        Locate specific cells in the dynamic table.
        """
        driver.get("https://the-internet.herokuapp.com/challenging_dom")
        
        # Find all rows in the table
        rows = driver.find_elements(By.XPATH, "//table/tbody/tr")
        
        # Get text from first cell of each row
        # YOUR CODE HERE
        pass
    
    def test_locate_by_text_content(self, driver):
        """
        Locate elements by their text content when no other identifiers work.
        """
        driver.get("https://the-internet.herokuapp.com/challenging_dom")
        
        # Find the "edit" links in the table
        edit_links = driver.find_elements(By.XPATH, "//a[text()='edit']")
        assert len(edit_links) > 0
        
        # YOUR CODE HERE - find and count "delete" links
        pass
```

---

## Definition of Done

- [ ] All 8 locator strategies demonstrated with working examples
- [ ] XPath advanced functions tested (contains, starts-with, text)
- [ ] XPath axes used (parent, ancestor, following, preceding)
- [ ] CSS selectors include compound, child, and attribute selectors
- [ ] `locator_analysis.md` completed with recommendations
- [ ] Dynamic locator challenges solved without using unstable IDs

---

## Hints

<details>
<summary>Hint 1: CSS Compound Selector</summary>

```python
# Element with both tag and class
driver.find_element(By.CSS_SELECTOR, "button.radius")

# Element with multiple classes
driver.find_element(By.CSS_SELECTOR, ".class1.class2")

# Element with specific attribute value
driver.find_element(By.CSS_SELECTOR, "input[type='password']")
```
</details>

<details>
<summary>Hint 2: XPath Axes</summary>

```python
# Following sibling: next element at same level
password = driver.find_element(By.XPATH, 
    "//input[@id='username']/following::input[@type='password']")

# Parent: immediate parent element
form = driver.find_element(By.XPATH, 
    "//input[@id='username']/parent::div")

# Ancestor: any parent/grandparent up the tree
form = driver.find_element(By.XPATH, 
    "//input[@id='username']/ancestor::form")
```
</details>

<details>
<summary>Hint 3: Table Cell Location</summary>

```python
# Get specific cell: row 2, column 3
cell = driver.find_element(By.XPATH, 
    "//table/tbody/tr[2]/td[3]")

# Get all cells in a column (e.g., column 1)
first_column = driver.find_elements(By.XPATH, 
    "//table/tbody/tr/td[1]")
```
</details>

