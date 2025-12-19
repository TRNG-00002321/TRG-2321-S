# Navigation in Python Selenium

## Learning Objectives
- Navigate to web pages using `driver.get()`
- Use browser history methods: `back()`, `forward()`, `refresh()`
- Access page information via `current_url` and `title` properties
- Implement navigation best practices for reliable test automation
- Handle navigation events and page load states

## Why This Matters

Navigation is the foundation of all web automation. Every test begins with navigating to a page, and many tests require moving between pages, refreshing content, or using browser history. Understanding navigation methods ensures your tests:

- **Start reliably** by properly loading target pages
- **Handle dynamic content** through strategic refreshes
- **Test user workflows** that span multiple pages
- **Validate application state** via URL and title assertions

As part of your journey to becoming a polyglot test automation engineer, you'll find that Python's navigation syntax mirrors what you learned in Java, making the transition seamless.

## The Concept

### The Navigation Methods Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    Browser Navigation Methods                    │
├─────────────────────────────────────────────────────────────────┤
│  driver.get(url)      →  Navigate to URL (waits for page load)  │
│  driver.back()        →  Go back in browser history             │
│  driver.forward()     →  Go forward in browser history          │
│  driver.refresh()     →  Reload the current page                │
├─────────────────────────────────────────────────────────────────┤
│                    Page Information Properties                   │
├─────────────────────────────────────────────────────────────────┤
│  driver.current_url   →  Get the current page URL               │
│  driver.title         →  Get the current page title             │
└─────────────────────────────────────────────────────────────────┘
```

### driver.get() - Navigate to URL

The `get()` method loads a web page and waits for the page load to complete:

```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

# Navigate to a URL
driver.get("https://www.python.org")

# The method blocks until the page finishes loading
print("Page loaded successfully!")

driver.quit()
```

**Key Behaviors:**
- Blocks execution until page load completes (based on page_load_strategy)
- Automatically handles HTTP/HTTPS protocols
- Raises exception for malformed URLs

**URL Formats:**

```python
# Standard HTTPS
driver.get("https://www.example.com")

# HTTP (less common, may show security warnings)
driver.get("http://www.example.com")

# With path and query parameters
driver.get("https://www.example.com/search?q=selenium")

# With port number
driver.get("http://localhost:8080/app")

# File protocol (local HTML files)
driver.get("file:///C:/tests/test-page.html")
```

### driver.back() - Navigate Back

Go back to the previous page in browser history:

```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

# Navigate through multiple pages
driver.get("https://www.python.org")
print(f"Page 1: {driver.current_url}")

driver.get("https://www.google.com")
print(f"Page 2: {driver.current_url}")

# Go back to Python.org
driver.back()
print(f"After back(): {driver.current_url}")  # Should show python.org

driver.quit()
```

### driver.forward() - Navigate Forward

Go forward in browser history (after going back):

```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

# Build navigation history
driver.get("https://www.python.org")
driver.get("https://www.google.com")

# Go back
driver.back()
print(f"After back(): {driver.current_url}")  # python.org

# Go forward again
driver.forward()
print(f"After forward(): {driver.current_url}")  # google.com

driver.quit()
```

### driver.refresh() - Reload Page

Refresh the current page:

```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.by import By
import time

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

driver.get("https://www.timeanddate.com/worldclock/")

# Get initial time
initial_content = driver.find_element(By.TAG_NAME, "body").text[:100]
print(f"Initial: {initial_content}")

# Wait a moment
time.sleep(2)

# Refresh to get updated content
driver.refresh()

# Check updated content
refreshed_content = driver.find_element(By.TAG_NAME, "body").text[:100]
print(f"Refreshed: {refreshed_content}")

driver.quit()
```

**Common Use Cases for refresh():**
- Testing dynamic content updates
- Resetting page state
- Triggering page reload events
- Testing cache behavior

### current_url Property

Get the current page URL:

```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

driver.get("https://www.python.org/downloads/")

# Get current URL
url = driver.current_url
print(f"Current URL: {url}")

# Use in assertions
assert "downloads" in driver.current_url, "Not on downloads page!"

# Check for redirects
driver.get("http://python.org")  # HTTP
print(f"After potential redirect: {driver.current_url}")  # Likely HTTPS

driver.quit()
```

### title Property

Get the current page title:

```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

driver.get("https://www.python.org")

# Get page title
title = driver.title
print(f"Page Title: {title}")

# Use in assertions
assert "Python" in driver.title, "Page title doesn't contain 'Python'!"

driver.quit()
```

### Comparing Python vs Java Navigation

| Operation | Java (Week 7) | Python |
|-----------|---------------|--------|
| Navigate | `driver.get(url)` | `driver.get(url)` |
| Back | `driver.navigate().back()` | `driver.back()` |
| Forward | `driver.navigate().forward()` | `driver.forward()` |
| Refresh | `driver.navigate().refresh()` | `driver.refresh()` |
| Current URL | `driver.getCurrentUrl()` | `driver.current_url` |
| Title | `driver.getTitle()` | `driver.title` |

Notice Python's cleaner syntax—no `navigate()` wrapper and properties instead of getter methods.

### Navigation Best Practices

**1. Always Verify Navigation Completed:**

```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

driver.get("https://www.example.com")

# Wait for a specific element that indicates page loaded
wait = WebDriverWait(driver, 10)
wait.until(EC.title_contains("Example"))

# Or wait for URL to change
wait.until(EC.url_contains("example.com"))

driver.quit()
```

**2. Handle Navigation Timeouts:**

```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.common.exceptions import TimeoutException
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

# Set page load timeout
driver.set_page_load_timeout(30)

try:
    driver.get("https://slow-loading-site.com")
except TimeoutException:
    print("Page load timed out!")
    # Handle timeout - maybe retry or fail gracefully

driver.quit()
```

**3. Use Navigation in Test Setup:**

```python
import pytest
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

class TestHomePage:
    
    @pytest.fixture(autouse=True)
    def setup(self):
        """Setup that runs before each test"""
        service = Service(ChromeDriverManager().install())
        self.driver = webdriver.Chrome(service=service)
        self.driver.get("https://www.example.com")
        
        yield
        
        self.driver.quit()
    
    def test_page_title(self):
        assert "Example" in self.driver.title
    
    def test_page_url(self):
        assert "example.com" in self.driver.current_url
```

**4. Navigate Between Related Pages:**

```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager

def test_navigation_workflow():
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    
    try:
        # Step 1: Navigate to home page
        driver.get("https://www.example.com")
        assert "Example" in driver.title
        
        # Step 2: Click link to navigate
        driver.find_element(By.LINK_TEXT, "More information...").click()
        
        # Step 3: Verify new page
        assert "iana.org" in driver.current_url
        
        # Step 4: Go back
        driver.back()
        assert "example.com" in driver.current_url
        
        print("Navigation workflow test passed!")
        
    finally:
        driver.quit()

test_navigation_workflow()
```

**5. Use Base URL Pattern:**

```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

class BasePage:
    """Base class for all page objects"""
    
    BASE_URL = "https://www.example.com"
    
    def __init__(self, driver):
        self.driver = driver
    
    def navigate_to(self, path=""):
        """Navigate to base URL + path"""
        full_url = f"{self.BASE_URL}{path}"
        self.driver.get(full_url)
        return self
    
    def get_current_path(self):
        """Get current URL path without base"""
        return self.driver.current_url.replace(self.BASE_URL, "")


class HomePage(BasePage):
    def navigate(self):
        return self.navigate_to("/")


class AboutPage(BasePage):
    def navigate(self):
        return self.navigate_to("/about")


# Usage
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

home = HomePage(driver)
home.navigate()
print(f"Home page: {driver.title}")

about = AboutPage(driver)
about.navigate()
print(f"About page: {driver.title}")

driver.quit()
```

### Page Load Strategies

Control when `get()` returns with page_load_strategy:

```python
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

options = Options()

# 'normal' - Wait for full page load (default)
options.page_load_strategy = 'normal'

# 'eager' - Wait for DOMContentLoaded event
# options.page_load_strategy = 'eager'

# 'none' - Return immediately after initial page request
# options.page_load_strategy = 'none'

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service, options=options)

driver.get("https://www.example.com")
print(f"Loaded with strategy: {options.page_load_strategy}")

driver.quit()
```

| Strategy | Returns When | Use Case |
|----------|--------------|----------|
| `normal` | Page fully loaded | Most tests |
| `eager` | DOM ready | Speed optimization |
| `none` | Immediately | Custom wait logic |

## Key Takeaways

1. **`driver.get(url)`** loads a page and waits based on page_load_strategy
2. **`back()`, `forward()`, `refresh()`** control browser history and page state
3. **`current_url` and `title`** are properties (not methods) for page information
4. **Python syntax is cleaner** than Java—no `navigate()` wrapper needed
5. **Always verify navigation** with explicit waits for reliability
6. **Set timeouts** to handle slow-loading pages gracefully

## Additional Resources

- [Selenium Python Navigation Documentation](https://selenium-python.readthedocs.io/navigating.html) - Official navigation guide
- [WebDriver Page Load Strategies](https://www.selenium.dev/documentation/webdriver/drivers/options/#pageloadstrategy) - Detailed strategy explanation
- [Selenium Waits Documentation](https://selenium-python.readthedocs.io/waits.html) - Combining navigation with waits

