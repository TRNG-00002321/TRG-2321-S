# Waiting Strategies and Window Handling in Python Selenium

## Learning Objectives
- Implement implicit waits for global timeout configuration
- Master explicit waits with `WebDriverWait` and `expected_conditions`
- Create custom wait conditions for specialized scenarios
- Handle multiple browser windows and tabs with `window_handles` and `switch_to.window()`
- Navigate frames and iframes with proper switching techniques

## Why This Matters

Modern web applications are dynamic—elements load asynchronously, content appears after AJAX calls, and animations create timing dependencies. Without proper waiting strategies, tests become flaky and unreliable.

Similarly, many applications open new windows, popups, or use iframes for embedded content. Understanding window and frame handling ensures your tests can navigate these complexities reliably.

These skills transform brittle automation scripts into robust, production-ready test suites that handle real-world application behavior.

## The Concept

### Waiting Strategies Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    Selenium Waiting Strategies                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Implicit Wait         Global timeout for ALL find_element calls│
│       ↓                                                          │
│  driver.implicitly_wait(10)                                     │
│                                                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Explicit Wait         Wait for SPECIFIC condition               │
│       ↓                                                          │
│  WebDriverWait(driver, 10).until(condition)                     │
│                                                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Fluent Wait          Explicit wait with polling configuration  │
│       ↓                                                          │
│  WebDriverWait(driver, 10, poll_frequency=0.5)                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Implicit Waits

Set a global timeout for all `find_element` calls:

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

# Set implicit wait - applies to ALL find_element calls
driver.implicitly_wait(10)  # Wait up to 10 seconds

driver.get("https://example.com")

# This will wait up to 10 seconds for element to appear
element = driver.find_element(By.ID, "dynamic-content")

driver.quit()
```

**Implicit Wait Characteristics:**
- Applied **globally** to all `find_element` calls
- Set **once** after driver creation
- Polls the DOM until element found or timeout
- Default polling interval is 500ms

**Limitations of Implicit Waits:**
- Cannot wait for specific conditions (visibility, clickability)
- Applies same timeout to all elements
- Can slow down tests when elements don't exist
- Can cause unexpected delays when combined with explicit waits

### Explicit Waits with WebDriverWait

Wait for specific conditions with fine-grained control:

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com")

# Create WebDriverWait instance
wait = WebDriverWait(driver, 10)  # 10 second timeout

# Wait for element to be clickable
button = wait.until(EC.element_to_be_clickable((By.ID, "submit-btn")))
button.click()

# Wait for element to be visible
message = wait.until(EC.visibility_of_element_located((By.CLASS_NAME, "success-message")))
print(message.text)

driver.quit()
```

### Expected Conditions (EC)

The `expected_conditions` module provides common wait conditions:

**Presence and Visibility:**

```python
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

# Element exists in DOM (may be hidden)
EC.presence_of_element_located((By.ID, "element-id"))

# Element exists and is visible
EC.visibility_of_element_located((By.ID, "element-id"))

# Element is visible (pass element object, not locator)
EC.visibility_of(element)

# Multiple elements present
EC.presence_of_all_elements_located((By.CLASS_NAME, "item"))

# Multiple elements visible
EC.visibility_of_all_elements_located((By.CLASS_NAME, "item"))
```

**Clickability and Interactability:**

```python
# Element is visible and enabled
EC.element_to_be_clickable((By.ID, "button"))

# Element is selected (checkbox/radio)
EC.element_to_be_selected(element)

# Element selection state
EC.element_selection_state_to_be(element, True)  # Should be selected
EC.element_selection_state_to_be(element, False)  # Should not be selected
```

**Text and Attribute Conditions:**

```python
# Element contains specific text
EC.text_to_be_present_in_element((By.ID, "message"), "Success")

# Element value contains text
EC.text_to_be_present_in_element_value((By.ID, "input"), "expected")

# Element attribute has value
EC.element_attribute_to_include((By.ID, "element"), "class")
```

**Invisibility and Staleness:**

```python
# Element becomes invisible or removed
EC.invisibility_of_element_located((By.ID, "loading-spinner"))

# Element becomes stale (removed from DOM)
EC.staleness_of(old_element)
```

**Frame and Window Conditions:**

```python
# Frame is available and switch to it
EC.frame_to_be_available_and_switch_to_it((By.ID, "iframe"))
EC.frame_to_be_available_and_switch_to_it("frame-name")

# New window opens
EC.number_of_windows_to_be(2)
EC.new_window_is_opened(current_handles)
```

**URL and Title Conditions:**

```python
# Page title conditions
EC.title_is("Expected Title")
EC.title_contains("Partial Title")

# URL conditions
EC.url_to_be("https://example.com/page")
EC.url_contains("/page")
EC.url_matches(r".*example\.com.*")
```

**Alert Conditions:**

```python
# Alert is present
EC.alert_is_present()
```

### Practical Explicit Wait Examples

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
wait = WebDriverWait(driver, 10)

# Example 1: Wait for page load after click
driver.get("https://example.com")
driver.find_element(By.LINK_TEXT, "Products").click()
wait.until(EC.title_contains("Products"))

# Example 2: Wait for loading spinner to disappear
wait.until(EC.invisibility_of_element_located((By.CLASS_NAME, "spinner")))

# Example 3: Wait for dynamic content
content = wait.until(EC.visibility_of_element_located((By.ID, "ajax-content")))
print(f"Content loaded: {content.text}")

# Example 4: Handle timeout gracefully
try:
    element = wait.until(EC.presence_of_element_located((By.ID, "optional-element")))
except TimeoutException:
    print("Optional element did not appear")

driver.quit()
```

### Custom Wait Conditions

Create your own wait conditions:

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

# Custom condition as a function
def element_has_text(locator, text):
    """Wait until element contains specific text"""
    def _predicate(driver):
        try:
            element = driver.find_element(*locator)
            return text in element.text
        except:
            return False
    return _predicate

# Usage
wait = WebDriverWait(driver, 10)
driver.get("https://example.com")
wait.until(element_has_text((By.ID, "status"), "Complete"))


# Custom condition as a class
class ElementHasAttribute:
    """Wait until element has specific attribute value"""
    
    def __init__(self, locator, attribute, value):
        self.locator = locator
        self.attribute = attribute
        self.value = value
    
    def __call__(self, driver):
        try:
            element = driver.find_element(*self.locator)
            attr_value = element.get_attribute(self.attribute)
            return attr_value == self.value
        except:
            return False

# Usage
wait.until(ElementHasAttribute((By.ID, "progress"), "data-status", "complete"))


# Custom condition for element count
def element_count_is(locator, count):
    """Wait until exactly N elements are present"""
    def _predicate(driver):
        elements = driver.find_elements(*locator)
        return len(elements) == count
    return _predicate

wait.until(element_count_is((By.CLASS_NAME, "search-result"), 10))

driver.quit()
```

### Fluent Wait Configuration

Configure polling frequency and ignored exceptions:

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import NoSuchElementException, StaleElementReferenceException
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

# Fluent wait with custom polling and ignored exceptions
wait = WebDriverWait(
    driver,
    timeout=30,                    # Maximum wait time
    poll_frequency=0.5,            # Check every 0.5 seconds
    ignored_exceptions=[           # Ignore these during polling
        NoSuchElementException,
        StaleElementReferenceException
    ]
)

driver.get("https://example.com")
element = wait.until(EC.element_to_be_clickable((By.ID, "dynamic-button")))
element.click()

driver.quit()
```

### Window Handling

#### Getting Window Handles

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com")

# Get current window handle
main_window = driver.current_window_handle
print(f"Main window: {main_window}")

# Get all window handles
all_windows = driver.window_handles
print(f"All windows: {all_windows}")

driver.quit()
```

#### Switching Between Windows

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
wait = WebDriverWait(driver, 10)

driver.get("https://example.com")

# Store main window handle
main_window = driver.current_window_handle

# Click link that opens new window
driver.find_element(By.LINK_TEXT, "Open New Window").click()

# Wait for new window
wait.until(EC.number_of_windows_to_be(2))

# Switch to new window
for handle in driver.window_handles:
    if handle != main_window:
        driver.switch_to.window(handle)
        break

# Interact with new window
print(f"New window title: {driver.title}")
driver.find_element(By.ID, "some-element").click()

# Close new window and switch back
driver.close()
driver.switch_to.window(main_window)

print(f"Back to main window: {driver.title}")

driver.quit()
```

#### Managing Multiple Tabs

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
wait = WebDriverWait(driver, 10)

driver.get("https://example.com")
main_window = driver.current_window_handle

# Open new tab using JavaScript
driver.execute_script("window.open('https://google.com', '_blank');")

# Or use Selenium 4's switch_to.new_window
driver.switch_to.new_window('tab')
driver.get("https://google.com")

# Wait for and switch between tabs
wait.until(EC.number_of_windows_to_be(2))

for handle in driver.window_handles:
    driver.switch_to.window(handle)
    print(f"Tab: {driver.title} - {driver.current_url}")

# Switch back to main tab
driver.switch_to.window(main_window)

driver.quit()
```

#### Complete Window Handling Pattern

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from contextlib import contextmanager

@contextmanager
def switch_to_new_window(driver, timeout=10):
    """
    Context manager for handling new windows/tabs
    Automatically switches back to original window on exit
    """
    wait = WebDriverWait(driver, timeout)
    original_window = driver.current_window_handle
    original_windows = set(driver.window_handles)
    
    yield  # Caller triggers the new window
    
    # Wait for new window
    wait.until(lambda d: len(d.window_handles) > len(original_windows))
    
    # Find and switch to new window
    new_windows = set(driver.window_handles) - original_windows
    new_window = new_windows.pop()
    driver.switch_to.window(new_window)
    
    try:
        yield driver
    finally:
        # Close new window and switch back
        driver.close()
        driver.switch_to.window(original_window)

# Usage (note: simplified version for demonstration)
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
wait = WebDriverWait(driver, 10)

driver.get("https://example.com")
main_window = driver.current_window_handle

# Click to open popup
driver.find_element(By.ID, "open-popup").click()

# Wait and switch
wait.until(EC.number_of_windows_to_be(2))
for handle in driver.window_handles:
    if handle != main_window:
        driver.switch_to.window(handle)
        break

# Work in popup
print(f"Popup: {driver.title}")

# Close and return
driver.close()
driver.switch_to.window(main_window)

driver.quit()
```

### Frame Handling

#### Switching to Frames

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
wait = WebDriverWait(driver, 10)

driver.get("https://example.com/page-with-frames")

# Method 1: Switch by frame index (0-based)
driver.switch_to.frame(0)

# Method 2: Switch by frame name or ID
driver.switch_to.frame("frame-name")
driver.switch_to.frame("frame-id")

# Method 3: Switch by WebElement
frame_element = driver.find_element(By.CSS_SELECTOR, "iframe.content-frame")
driver.switch_to.frame(frame_element)

# Method 4: Wait for frame and switch (recommended)
wait.until(EC.frame_to_be_available_and_switch_to_it((By.ID, "my-iframe")))

# Interact with elements inside frame
driver.find_element(By.ID, "element-in-frame").click()

# Switch back to main document
driver.switch_to.default_content()

driver.quit()
```

#### Nested Frames

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com/nested-frames")

# Navigate into nested frames
driver.switch_to.frame("outer-frame")
driver.switch_to.frame("inner-frame")

# Interact with deeply nested element
element = driver.find_element(By.ID, "nested-element")
print(element.text)

# Go up one level
driver.switch_to.parent_frame()

# Or go all the way out
driver.switch_to.default_content()

driver.quit()
```

#### Complete Frame Handling Example

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from contextlib import contextmanager

@contextmanager
def switch_to_frame(driver, frame_reference, timeout=10):
    """
    Context manager for frame switching
    Automatically switches back to parent on exit
    """
    wait = WebDriverWait(driver, timeout)
    
    if isinstance(frame_reference, tuple):
        # frame_reference is a locator tuple
        wait.until(EC.frame_to_be_available_and_switch_to_it(frame_reference))
    else:
        # frame_reference is name, id, index, or element
        driver.switch_to.frame(frame_reference)
    
    try:
        yield driver
    finally:
        driver.switch_to.parent_frame()

# Usage
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

driver.get("https://example.com/page-with-iframe")

# Use context manager
with switch_to_frame(driver, (By.ID, "content-iframe")):
    # Automatically inside frame
    title = driver.find_element(By.TAG_NAME, "h1").text
    print(f"Frame title: {title}")
    
# Automatically back to main content

driver.quit()
```

### Best Practices Summary

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

# 1. Prefer explicit waits over implicit waits
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
wait = WebDriverWait(driver, 10)

# 2. Wait for specific conditions, not arbitrary time.sleep()
# BAD: time.sleep(5)
# GOOD:
element = wait.until(EC.element_to_be_clickable((By.ID, "button")))

# 3. Use appropriate conditions
# - visibility_of_element_located: When you need to read or verify
# - element_to_be_clickable: Before clicking
# - presence_of_element_located: Just need to know it exists

# 4. Handle timeouts gracefully
from selenium.common.exceptions import TimeoutException
try:
    element = wait.until(EC.presence_of_element_located((By.ID, "optional")))
except TimeoutException:
    print("Element not found - continuing without it")

# 5. Store window handles before actions that open new windows
main_window = driver.current_window_handle
driver.find_element(By.ID, "open-popup").click()
wait.until(EC.number_of_windows_to_be(2))

# 6. Always switch back from frames and windows
driver.switch_to.default_content()  # From frame
driver.switch_to.window(main_window)  # From popup

driver.quit()
```

## Key Takeaways

1. **Implicit waits** apply globally; **explicit waits** target specific conditions
2. **WebDriverWait + expected_conditions** is the recommended waiting strategy
3. **Custom conditions** can be functions or classes with `__call__`
4. **window_handles** returns all open windows/tabs as a list
5. **switch_to.window()** changes focus between windows
6. **switch_to.frame()** and **switch_to.default_content()** handle iframe navigation
7. **Always switch back** to main window/frame after interacting with popups or iframes

## Additional Resources

- [Selenium Waits Documentation](https://www.selenium.dev/documentation/webdriver/waits/) - Official waits guide
- [Expected Conditions Reference](https://selenium-python.readthedocs.io/api.html#module-selenium.webdriver.support.expected_conditions) - All built-in conditions
- [Selenium Window Handling](https://www.selenium.dev/documentation/webdriver/interactions/windows/) - Official windows guide

