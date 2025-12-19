# Exercise 2: WebDriver Manager Setup

## Objective

Configure automated driver management using webdriver-manager and test across multiple browsers to understand the advantages over manual driver setup.

## Learning Goals

- Implement webdriver-manager for Chrome, Firefox, and Edge
- Compare automated vs manual driver management
- Create a multi-browser test configuration
- Handle browser-specific options

## Time Estimate

30 minutes

## Prerequisites

- Completed Exercise 1
- Read `automated-driver-setup-python.md` and `manual-driver-setup-python.md`

---

## Core Tasks

### Task 1: Multi-Browser Driver Factory (15 minutes)

Create `utils/multi_browser_factory.py`:

```python
"""
Multi-browser driver factory with webdriver-manager.

Supports:
- Chrome
- Firefox
- Edge
"""

from contextlib import contextmanager
from selenium import webdriver
from selenium.webdriver.chrome.service import Service as ChromeService
from selenium.webdriver.firefox.service import Service as FirefoxService
from selenium.webdriver.edge.service import Service as EdgeService
from webdriver_manager.chrome import ChromeDriverManager
from webdriver_manager.firefox import GeckoDriverManager
from webdriver_manager.microsoft import EdgeChromiumDriverManager


@contextmanager
def create_driver(browser: str = "chrome", headless: bool = False):
    """
    Create a WebDriver instance for the specified browser.
    
    Args:
        browser: Browser name ("chrome", "firefox", "edge")
        headless: Run in headless mode if True
    
    Yields:
        WebDriver instance
    
    Example:
        with create_driver("firefox", headless=True) as driver:
            driver.get("https://example.com")
    """
    driver = None
    
    try:
        if browser.lower() == "chrome":
            # TODO: Implement Chrome driver setup
            # 1. Create ChromeOptions
            # 2. Add headless argument if needed
            # 3. Use ChromeDriverManager for automatic driver download
            pass
            
        elif browser.lower() == "firefox":
            # TODO: Implement Firefox driver setup
            # 1. Create FirefoxOptions
            # 2. Add headless argument if needed (note: Firefox uses -headless)
            # 3. Use GeckoDriverManager
            pass
            
        elif browser.lower() == "edge":
            # TODO: Implement Edge driver setup
            # 1. Create EdgeOptions
            # 2. Add headless argument if needed
            # 3. Use EdgeChromiumDriverManager
            pass
            
        else:
            raise ValueError(f"Unsupported browser: {browser}")
        
        driver.implicitly_wait(10)
        yield driver
        
    finally:
        if driver:
            driver.quit()


def get_browser_version(browser: str) -> str:
    """
    Get the installed browser version.
    
    TODO: Implement version detection for each browser
    """
    # YOUR CODE HERE
    pass
```

### Task 2: Cross-Browser Test Suite (10 minutes)

Create `tests/test_cross_browser.py`:

```python
"""
Cross-browser compatibility tests.

Run the same tests across Chrome, Firefox, and Edge.
"""

import pytest
from selenium.webdriver.common.by import By
import sys
sys.path.insert(0, '..')
from utils.multi_browser_factory import create_driver

# Parameterize tests to run on multiple browsers
BROWSERS = ["chrome", "firefox"]  # Add "edge" if installed


@pytest.mark.parametrize("browser", BROWSERS)
def test_page_loads_correctly(browser):
    """
    Verify the page loads correctly in each browser.
    
    Steps:
    1. Navigate to the-internet homepage
    2. Verify page title
    3. Verify heading text
    """
    with create_driver(browser, headless=True) as driver:
        driver.get("https://the-internet.herokuapp.com/")
        
        assert "The Internet" in driver.title
        
        heading = driver.find_element(By.TAG_NAME, "h1")
        assert "Welcome to the-internet" in heading.text


@pytest.mark.parametrize("browser", BROWSERS)
def test_form_interaction(browser):
    """
    Verify form interaction works in each browser.
    
    Steps:
    1. Navigate to login page
    2. Enter credentials
    3. Submit form
    4. Verify result
    """
    # TODO: Implement cross-browser form test
    pass


@pytest.mark.parametrize("browser", BROWSERS)
def test_screenshot_capture(browser):
    """
    Verify screenshot capture works in each browser.
    
    Steps:
    1. Navigate to a page
    2. Take screenshot
    3. Verify file was created
    """
    # TODO: Implement screenshot test
    # Save to screenshots/{browser}_screenshot.png
    pass
```

### Task 3: Browser Capability Report (5 minutes)

Create a simple script `browser_report.py` that prints information about available browsers:

```python
"""
Browser capability reporter.

Displays information about installed browsers and their capabilities.
"""

from utils.multi_browser_factory import create_driver


def generate_browser_report():
    """Generate a report of browser capabilities."""
    browsers = ["chrome", "firefox", "edge"]
    
    print("=" * 60)
    print("BROWSER CAPABILITY REPORT")
    print("=" * 60)
    
    for browser in browsers:
        print(f"\n{browser.upper()}")
        print("-" * 40)
        
        try:
            with create_driver(browser, headless=True) as driver:
                # TODO: Print browser capabilities
                # - driver.capabilities.get('browserName')
                # - driver.capabilities.get('browserVersion')
                # - driver.capabilities.get('platformName')
                pass
                
        except Exception as e:
            print(f"  ❌ Not available: {e}")


if __name__ == "__main__":
    generate_browser_report()
```

---

## Expected Output

### pytest output:
```
$ pytest tests/test_cross_browser.py -v

tests/test_cross_browser.py::test_page_loads_correctly[chrome] PASSED
tests/test_cross_browser.py::test_page_loads_correctly[firefox] PASSED
tests/test_cross_browser.py::test_form_interaction[chrome] PASSED
tests/test_cross_browser.py::test_form_interaction[firefox] PASSED
tests/test_cross_browser.py::test_screenshot_capture[chrome] PASSED
tests/test_cross_browser.py::test_screenshot_capture[firefox] PASSED

========================= 6 passed in 24.56s =========================
```

### Browser report output:
```
============================================================
BROWSER CAPABILITY REPORT
============================================================

CHROME
----------------------------------------
  Browser Name: chrome
  Version: 120.0.6099.71
  Platform: Windows

FIREFOX
----------------------------------------
  Browser Name: firefox
  Version: 120.0.1
  Platform: Windows

EDGE
----------------------------------------
  Browser Name: MicrosoftEdge
  Version: 120.0.2210.77
  Platform: Windows
```

---

## Definition of Done

- [ ] Multi-browser factory supports Chrome, Firefox, and Edge
- [ ] All browsers use webdriver-manager for driver management
- [ ] Headless mode works for all browsers
- [ ] Parameterized tests run across multiple browsers
- [ ] Browser report displays version information
- [ ] Screenshots saved with browser-specific names

---

## Hints

<details>
<summary>Hint 1: Firefox Headless Argument</summary>

Firefox uses a different headless argument:
```python
options = webdriver.FirefoxOptions()
if headless:
    options.add_argument("-headless")  # Note the dash, not double-dash
```
</details>

<details>
<summary>Hint 2: Browser Capabilities</summary>

```python
# Access browser info from capabilities
browser_name = driver.capabilities.get('browserName')
browser_version = driver.capabilities.get('browserVersion')
platform = driver.capabilities.get('platformName')
```
</details>

<details>
<summary>Hint 3: Handling Missing Browsers</summary>

```python
# Use pytest.skip for browsers not installed
try:
    with create_driver(browser) as driver:
        # Test code
        pass
except WebDriverException:
    pytest.skip(f"{browser} not available on this system")
```
</details>

