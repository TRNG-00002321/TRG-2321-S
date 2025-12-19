# Exercise 1: First Python Selenium Project

## Objective

Set up a Python Selenium project from scratch and write your first automated tests to navigate, interact with forms, and validate page content.

## Learning Goals

- Create a Python Selenium project structure
- Configure Selenium WebDriver with webdriver-manager
- Write basic navigation and assertion tests
- Apply Pythonic patterns (context managers)
- Compare with Java Selenium syntax from Week 7

## Time Estimate

45 minutes

## Prerequisites

- Python 3.8+ installed
- Completed `selenium-webdriver-python.md` content
- Watched `demo_selenium_python_setup.py` demonstration

---

## Core Tasks

### Task 1: Project Setup (10 minutes)

Create the following project structure:

```
first_selenium_project/
├── tests/
│   ├── __init__.py
│   ├── test_navigation.py
│   └── test_page_validation.py
├── utils/
│   ├── __init__.py
│   └── driver_factory.py
├── requirements.txt
└── README.md
```

**requirements.txt:**
```
selenium>=4.15.0
webdriver-manager>=4.0.0
pytest>=7.4.0
```

### Task 2: Driver Factory (10 minutes)

Create `utils/driver_factory.py` with a context manager for safe browser handling:

```python
"""
TODO: Implement a driver factory that:
1. Uses webdriver-manager for automatic driver management
2. Provides a context manager for safe browser cleanup
3. Supports headless mode via parameter
"""

from contextlib import contextmanager
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

@contextmanager
def create_chrome_driver(headless: bool = False):
    # YOUR CODE HERE
    # 1. Create ChromeOptions and configure headless if needed
    # 2. Set up Service with ChromeDriverManager
    # 3. Create driver, yield it, and ensure quit() in finally block
    pass
```

### Task 3: Navigation Tests (15 minutes)

Create `tests/test_navigation.py`:

```python
"""
Test navigation functionality using Python Selenium.

Implement tests that:
1. Navigate to https://the-internet.herokuapp.com/
2. Click on "Form Authentication" link
3. Verify URL changed to /login
4. Use back/forward navigation
5. Capture screenshots at key points
"""

from selenium.webdriver.common.by import By
import sys
sys.path.insert(0, '..')
from utils.driver_factory import create_chrome_driver

def test_navigate_to_login_page():
    """
    Test: Navigate from home to login page
    
    Steps:
    1. Go to the-internet homepage
    2. Find and click "Form Authentication" link
    3. Assert URL contains "/login"
    4. Assert page contains "Login Page" heading
    """
    # YOUR CODE HERE
    pass

def test_back_forward_navigation():
    """
    Test: Browser navigation (back/forward)
    
    Steps:
    1. Navigate to homepage
    2. Click a link to go to another page
    3. Use driver.back() to return
    4. Assert you're on homepage
    5. Use driver.forward() to go forward
    6. Assert you're on the second page again
    """
    # YOUR CODE HERE
    pass

def test_capture_screenshot():
    """
    Test: Screenshot capture
    
    Steps:
    1. Navigate to any page
    2. Take a full page screenshot
    3. Save it to screenshots/homepage.png
    """
    # YOUR CODE HERE
    pass
```

### Task 4: Page Validation Tests (10 minutes)

Create `tests/test_page_validation.py`:

```python
"""
Test page content validation using Python Selenium.

Implement tests that:
1. Validate page title
2. Check for specific text content
3. Verify element presence
4. Check element attributes
"""

from selenium.webdriver.common.by import By
import sys
sys.path.insert(0, '..')
from utils.driver_factory import create_chrome_driver

def test_page_title():
    """Verify the page title matches expected value."""
    # YOUR CODE HERE
    pass

def test_heading_text():
    """Verify the main heading contains expected text."""
    # YOUR CODE HERE
    pass

def test_links_present():
    """Verify that all example links are present on the page."""
    # YOUR CODE HERE
    # Use find_elements to get all links
    # Use list comprehension to extract link texts
    pass

def test_link_attributes():
    """Verify that links have correct href attributes."""
    # YOUR CODE HERE
    pass
```

---

## Expected Output

When running your tests with pytest:

```
$ pytest tests/ -v

tests/test_navigation.py::test_navigate_to_login_page PASSED
tests/test_navigation.py::test_back_forward_navigation PASSED
tests/test_navigation.py::test_capture_screenshot PASSED
tests/test_page_validation.py::test_page_title PASSED
tests/test_page_validation.py::test_heading_text PASSED
tests/test_page_validation.py::test_links_present PASSED
tests/test_page_validation.py::test_link_attributes PASSED

========================= 7 passed in 12.34s =========================
```

---

## Definition of Done

- [ ] Project structure created with all required files
- [ ] `requirements.txt` includes all dependencies
- [ ] Driver factory implements context manager pattern
- [ ] All 7 test functions pass
- [ ] Screenshots directory contains captured images
- [ ] Code uses Python conventions (snake_case, docstrings)

---

## Java Comparison Reference

| Java Selenium | Python Selenium |
|--------------|-----------------|
| `driver.get(url);` | `driver.get(url)` |
| `driver.findElement(By.id("x"))` | `driver.find_element(By.ID, "x")` |
| `element.getText()` | `element.text` |
| `element.getAttribute("href")` | `element.get_attribute("href")` |
| `driver.getTitle()` | `driver.title` |
| `driver.getCurrentUrl()` | `driver.current_url` |

---

## Hints

<details>
<summary>Hint 1: Context Manager Structure</summary>

```python
@contextmanager
def create_chrome_driver(headless=False):
    options = webdriver.ChromeOptions()
    if headless:
        options.add_argument("--headless")
    
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    
    try:
        yield driver
    finally:
        driver.quit()
```
</details>

<details>
<summary>Hint 2: Using the Context Manager</summary>

```python
def test_example():
    with create_chrome_driver() as driver:
        driver.get("https://example.com")
        assert "Example" in driver.title
    # Browser closes automatically after this block
```
</details>

<details>
<summary>Hint 3: Finding Multiple Elements</summary>

```python
# Get all links and extract their text
links = driver.find_elements(By.TAG_NAME, "a")
link_texts = [link.text for link in links if link.text]
```
</details>

