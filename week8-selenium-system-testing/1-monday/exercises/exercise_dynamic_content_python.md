# Exercise 5: Dynamic Content Handling with Python

## Objective

Implement proper waiting strategies for pages with dynamic loading, modal dialogs, AJAX content, and multiple windows/tabs.

## Learning Goals

- Implement implicit and explicit waits appropriately
- Use WebDriverWait with expected_conditions
- Create custom wait conditions
- Handle modal dialogs and alerts
- Manage multiple windows and tabs
- Work with frames and iframes

## Time Estimate

60 minutes

## Prerequisites

- Completed Exercises 1-4
- Read `waiting-window-handling-python.md` and `actions-api-python.md`

---

## Core Tasks

### Task 1: Waiting Strategies (20 minutes)

Create `tests/test_waiting.py`:

```python
"""
Waiting strategy tests for dynamic content.

Targets:
- https://the-internet.herokuapp.com/dynamic_loading/1
- https://the-internet.herokuapp.com/dynamic_loading/2
- https://the-internet.herokuapp.com/dynamic_controls
"""

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import pytest


@pytest.fixture
def driver():
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    # Note: We're NOT setting implicit wait here to practice explicit waits
    yield driver
    driver.quit()


class TestExplicitWaits:
    """Test explicit waiting strategies."""
    
    def test_wait_for_element_visibility(self, driver):
        """
        Wait for hidden element to become visible.
        
        Page: dynamic_loading/1 - Element is hidden initially
        """
        driver.get("https://the-internet.herokuapp.com/dynamic_loading/1")
        
        # Click Start button
        start_button = driver.find_element(By.CSS_SELECTOR, "#start button")
        start_button.click()
        
        # Wait for hidden element to become visible
        wait = WebDriverWait(driver, 10)
        hello_text = wait.until(
            EC.visibility_of_element_located((By.CSS_SELECTOR, "#finish h4"))
        )
        
        assert hello_text.text == "Hello World!"
    
    def test_wait_for_element_presence(self, driver):
        """
        Wait for element to appear in DOM.
        
        Page: dynamic_loading/2 - Element is added to DOM after loading
        """
        driver.get("https://the-internet.herokuapp.com/dynamic_loading/2")
        
        # Click Start button
        start_button = driver.find_element(By.CSS_SELECTOR, "#start button")
        start_button.click()
        
        # TODO: Wait for element to be present in DOM
        # Then wait for it to be visible
        
        # YOUR CODE HERE
        pass
    
    def test_wait_for_element_clickable(self, driver):
        """
        Wait for element to become clickable.
        
        Page: dynamic_controls - Enable/disable input
        """
        driver.get("https://the-internet.herokuapp.com/dynamic_controls")
        
        # The input is initially disabled
        text_input = driver.find_element(By.CSS_SELECTOR, "#input-example input")
        assert not text_input.is_enabled()
        
        # Click Enable button
        enable_btn = driver.find_element(By.CSS_SELECTOR, "#input-example button")
        enable_btn.click()
        
        # TODO: Wait for input to become enabled/clickable
        # Then type into it
        
        # YOUR CODE HERE
        pass
    
    def test_wait_for_text_in_element(self, driver):
        """
        Wait for specific text to appear in an element.
        """
        driver.get("https://the-internet.herokuapp.com/dynamic_controls")
        
        # Click Remove button for checkbox
        remove_btn = driver.find_element(By.CSS_SELECTOR, "#checkbox-example button")
        remove_btn.click()
        
        # TODO: Wait for message "It's gone!" to appear
        
        # YOUR CODE HERE
        pass
    
    def test_wait_for_staleness(self, driver):
        """
        Wait for element to become stale (removed from DOM).
        """
        driver.get("https://the-internet.herokuapp.com/dynamic_controls")
        
        checkbox = driver.find_element(By.CSS_SELECTOR, "#checkbox")
        
        # Click Remove
        remove_btn = driver.find_element(By.CSS_SELECTOR, "#checkbox-example button")
        remove_btn.click()
        
        # TODO: Wait for checkbox to become stale
        # Use EC.staleness_of(checkbox)
        
        # YOUR CODE HERE
        pass


class TestCustomWaits:
    """Create custom wait conditions."""
    
    def test_custom_wait_condition(self, driver):
        """
        Implement a custom wait condition.
        """
        driver.get("https://the-internet.herokuapp.com/dynamic_loading/1")
        
        # Custom condition: Wait for loading spinner to disappear
        def loading_complete(driver):
            try:
                loading = driver.find_element(By.ID, "loading")
                return not loading.is_displayed()
            except:
                return True
        
        start_button = driver.find_element(By.CSS_SELECTOR, "#start button")
        start_button.click()
        
        wait = WebDriverWait(driver, 10)
        wait.until(loading_complete)
        
        # Now check the result
        result = driver.find_element(By.CSS_SELECTOR, "#finish h4")
        assert result.is_displayed()
    
    def test_custom_wait_with_lambda(self, driver):
        """
        Use lambda for simple custom conditions.
        """
        driver.get("https://the-internet.herokuapp.com/dynamic_loading/1")
        
        start_button = driver.find_element(By.CSS_SELECTOR, "#start button")
        start_button.click()
        
        # TODO: Use lambda for custom condition
        # Example: wait.until(lambda d: "Hello" in d.page_source)
        
        # YOUR CODE HERE
        pass
```

### Task 2: Modal and Alert Handling (15 minutes)

Create `tests/test_modals_alerts.py`:

```python
"""
Modal dialog and JavaScript alert handling.

Targets:
- https://the-internet.herokuapp.com/javascript_alerts
- https://the-internet.herokuapp.com/entry_ad
"""

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import pytest


@pytest.fixture
def driver():
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    driver.implicitly_wait(10)
    yield driver
    driver.quit()


class TestJavaScriptAlerts:
    """Handle JavaScript alerts, confirms, and prompts."""
    
    def test_handle_alert(self, driver):
        """
        Accept a simple JavaScript alert.
        """
        driver.get("https://the-internet.herokuapp.com/javascript_alerts")
        
        # Click button to trigger alert
        driver.find_element(By.CSS_SELECTOR, "button[onclick='jsAlert()']").click()
        
        # Wait for and switch to alert
        wait = WebDriverWait(driver, 10)
        alert = wait.until(EC.alert_is_present())
        
        # Get alert text
        alert_text = alert.text
        assert "I am a JS Alert" in alert_text
        
        # Accept (click OK)
        alert.accept()
        
        # Verify result
        result = driver.find_element(By.ID, "result")
        assert "successfully clicked" in result.text
    
    def test_handle_confirm_accept(self, driver):
        """
        Accept a JavaScript confirm dialog.
        """
        driver.get("https://the-internet.herokuapp.com/javascript_alerts")
        
        # TODO: Click confirm button
        # Switch to alert
        # Accept the confirm
        # Verify "Ok" in result
        
        # YOUR CODE HERE
        pass
    
    def test_handle_confirm_dismiss(self, driver):
        """
        Dismiss (cancel) a JavaScript confirm dialog.
        """
        driver.get("https://the-internet.herokuapp.com/javascript_alerts")
        
        # TODO: Click confirm button
        # Switch to alert
        # Dismiss (cancel) the confirm
        # Verify "Cancel" in result
        
        # YOUR CODE HERE
        pass
    
    def test_handle_prompt(self, driver):
        """
        Enter text into a JavaScript prompt.
        """
        driver.get("https://the-internet.herokuapp.com/javascript_alerts")
        
        # Click prompt button
        driver.find_element(By.CSS_SELECTOR, "button[onclick='jsPrompt()']").click()
        
        # Switch to alert
        wait = WebDriverWait(driver, 10)
        alert = wait.until(EC.alert_is_present())
        
        # TODO: Send text to prompt
        # Accept the prompt
        # Verify text appears in result
        
        # YOUR CODE HERE
        pass


class TestModalDialogs:
    """Handle modal dialogs (not JavaScript alerts)."""
    
    def test_close_modal_dialog(self, driver):
        """
        Close a modal that appears on page load.
        """
        driver.get("https://the-internet.herokuapp.com/entry_ad")
        
        # Wait for modal to appear
        wait = WebDriverWait(driver, 10)
        
        # TODO: Wait for modal to be visible
        # Find and click the close button
        # Verify modal is no longer visible
        
        # YOUR CODE HERE
        pass
```

### Task 3: Window and Tab Handling (15 minutes)

Create `tests/test_windows.py`:

```python
"""
Multiple window and tab handling.

Target: https://the-internet.herokuapp.com/windows
"""

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import pytest


@pytest.fixture
def driver():
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    driver.implicitly_wait(10)
    yield driver
    driver.quit()


class TestWindowHandling:
    """Handle multiple browser windows and tabs."""
    
    def test_switch_to_new_window(self, driver):
        """
        Switch to a new window that opens from a link click.
        """
        driver.get("https://the-internet.herokuapp.com/windows")
        
        # Store original window handle
        original_window = driver.current_window_handle
        
        # Click link that opens new window
        driver.find_element(By.LINK_TEXT, "Click Here").click()
        
        # Wait for new window
        wait = WebDriverWait(driver, 10)
        wait.until(EC.number_of_windows_to_be(2))
        
        # Switch to new window
        for handle in driver.window_handles:
            if handle != original_window:
                driver.switch_to.window(handle)
                break
        
        # Verify new window content
        heading = driver.find_element(By.TAG_NAME, "h3")
        assert heading.text == "New Window"
        
        # Close new window and return to original
        driver.close()
        driver.switch_to.window(original_window)
        
        # Verify back on original page
        assert "windows" in driver.current_url
    
    def test_open_multiple_windows(self, driver):
        """
        Open and manage multiple windows.
        """
        driver.get("https://the-internet.herokuapp.com/windows")
        original_window = driver.current_window_handle
        
        # TODO: Open new window 3 times
        # Store handles in a list
        # Switch between them
        # Verify content in each
        # Close all except original
        
        # YOUR CODE HERE
        pass
    
    def test_open_new_tab(self, driver):
        """
        Open a new tab programmatically.
        """
        driver.get("https://the-internet.herokuapp.com/")
        
        # Open new tab using JavaScript
        driver.execute_script("window.open('', '_blank');")
        
        # TODO: Switch to new tab
        # Navigate to a different page
        # Verify content
        # Switch back to original tab
        
        # YOUR CODE HERE
        pass


class TestFrameHandling:
    """Handle iframes and nested frames."""
    
    def test_switch_to_iframe(self, driver):
        """
        Switch to an iframe and interact with its content.
        """
        driver.get("https://the-internet.herokuapp.com/iframe")
        
        # Switch to iframe by ID
        driver.switch_to.frame("mce_0_ifr")
        
        # TODO: Find the editor content
        # Clear existing text
        # Type new text
        # Switch back to main content
        
        # YOUR CODE HERE
        pass
    
    def test_nested_frames(self, driver):
        """
        Handle nested iframes.
        """
        driver.get("https://the-internet.herokuapp.com/nested_frames")
        
        # TODO: Navigate through nested frames
        # Switch to top frame
        # Switch to left frame inside top
        # Get frame content
        # Switch back to main document
        
        # YOUR CODE HERE
        pass
```

### Task 4: ActionChains for Complex Interactions (10 minutes)

Create `tests/test_actions.py`:

```python
"""
Complex interactions using ActionChains.

Targets:
- https://the-internet.herokuapp.com/hovers
- https://the-internet.herokuapp.com/drag_and_drop
- https://the-internet.herokuapp.com/context_menu
"""

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import pytest


@pytest.fixture
def driver():
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    driver.implicitly_wait(10)
    yield driver
    driver.quit()


class TestActionChains:
    """Test complex mouse and keyboard interactions."""
    
    def test_hover_to_reveal_content(self, driver):
        """
        Hover over element to reveal hidden content.
        """
        driver.get("https://the-internet.herokuapp.com/hovers")
        
        # Find first figure (user image)
        figures = driver.find_elements(By.CSS_SELECTOR, ".figure")
        first_figure = figures[0]
        
        # Hover over it
        actions = ActionChains(driver)
        actions.move_to_element(first_figure).perform()
        
        # Verify caption is now visible
        caption = first_figure.find_element(By.CSS_SELECTOR, ".figcaption")
        assert caption.is_displayed()
        assert "user1" in caption.text
    
    def test_drag_and_drop(self, driver):
        """
        Drag element and drop it on another element.
        """
        driver.get("https://the-internet.herokuapp.com/drag_and_drop")
        
        source = driver.find_element(By.ID, "column-a")
        target = driver.find_element(By.ID, "column-b")
        
        # TODO: Perform drag and drop
        # Note: This page may require JavaScript workaround
        # due to HTML5 drag-drop compatibility issues
        
        # YOUR CODE HERE
        pass
    
    def test_right_click_context_menu(self, driver):
        """
        Right-click to trigger context menu.
        """
        driver.get("https://the-internet.herokuapp.com/context_menu")
        
        hot_spot = driver.find_element(By.ID, "hot-spot")
        
        # TODO: Right-click on the element
        # Handle the JavaScript alert that appears
        
        # YOUR CODE HERE
        pass
    
    def test_double_click(self, driver):
        """
        Double-click on an element.
        """
        # Note: the-internet doesn't have a double-click example
        # We'll use a generic approach
        
        driver.get("https://the-internet.herokuapp.com/")
        
        heading = driver.find_element(By.TAG_NAME, "h1")
        
        actions = ActionChains(driver)
        actions.double_click(heading).perform()
        
        # This just demonstrates the action - no visible change expected
        assert True
```

---

## Definition of Done

- [ ] All explicit wait tests pass with proper timeout handling
- [ ] Custom wait conditions implemented and working
- [ ] JavaScript alerts handled (accept, dismiss, send text)
- [ ] Modal dialogs closed properly
- [ ] Multiple windows opened, switched, and closed correctly
- [ ] Iframe content accessed successfully
- [ ] ActionChains hover reveals hidden content
- [ ] All 20+ tests pass

---

## Hints

<details>
<summary>Hint 1: Common Expected Conditions</summary>

```python
from selenium.webdriver.support import expected_conditions as EC

# Element conditions
EC.presence_of_element_located((By.ID, "element"))
EC.visibility_of_element_located((By.ID, "element"))
EC.element_to_be_clickable((By.ID, "element"))
EC.invisibility_of_element_located((By.ID, "element"))
EC.staleness_of(element)

# Window conditions
EC.number_of_windows_to_be(2)
EC.new_window_is_opened(current_handles)

# Alert condition
EC.alert_is_present()

# Text conditions
EC.text_to_be_present_in_element((By.ID, "element"), "text")
```
</details>

<details>
<summary>Hint 2: Alert Handling Methods</summary>

```python
alert = driver.switch_to.alert

# Get alert text
text = alert.text

# Accept (click OK)
alert.accept()

# Dismiss (click Cancel)
alert.dismiss()

# Send text to prompt
alert.send_keys("your text")
alert.accept()
```
</details>

<details>
<summary>Hint 3: Frame Switching</summary>

```python
# Switch by ID or name
driver.switch_to.frame("frame_id")

# Switch by index (0-based)
driver.switch_to.frame(0)

# Switch by WebElement
frame = driver.find_element(By.TAG_NAME, "iframe")
driver.switch_to.frame(frame)

# Return to main document
driver.switch_to.default_content()

# Return to parent frame (for nested frames)
driver.switch_to.parent_frame()
```
</details>

