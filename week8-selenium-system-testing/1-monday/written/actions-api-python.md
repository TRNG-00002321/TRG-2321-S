# ActionChains in Python Selenium

## Learning Objectives
- Understand the ActionChains API for complex user interactions
- Master mouse actions: `move_to_element()`, `drag_and_drop()`, `double_click()`, `context_click()`
- Implement keyboard actions: `send_keys()`, `key_down()`, `key_up()`
- Chain multiple actions together using the builder pattern
- Execute complex interaction patterns with `perform()`

## Why This Matters

While basic methods like `click()` and `send_keys()` handle most interactions, real-world applications often require more sophisticated user actions:

- **Hover menus** that appear on mouse-over
- **Drag and drop** for sortable lists and file uploads
- **Right-click context menus** for additional options
- **Keyboard shortcuts** combining modifier keys
- **Drawing on canvas** elements
- **Slider controls** that require precise mouse movement

ActionChains enables you to automate these complex interactions that cannot be achieved with simple element methods.

## The Concept

### ActionChains Overview

ActionChains builds a queue of actions that execute together:

```
┌─────────────────────────────────────────────────────────────────┐
│                      ActionChains Workflow                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Create ActionChains  →  Add Actions  →  perform()              │
│         ↓                     ↓              ↓                   │
│   ActionChains(driver)   .move_to()      Execute all             │
│                          .click()        queued actions          │
│                          .send_keys()                            │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Basic ActionChains Usage

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com")

# Create ActionChains instance
actions = ActionChains(driver)

# Add actions to the queue
element = driver.find_element(By.ID, "hoverable")
actions.move_to_element(element)
actions.click()

# Execute all queued actions
actions.perform()

# Reset action queue for next sequence
actions.reset_actions()

driver.quit()
```

### Mouse Actions

#### move_to_element() - Hover

Move mouse to the center of an element:

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com")

actions = ActionChains(driver)

# Hover over menu to reveal dropdown
menu_item = driver.find_element(By.CSS_SELECTOR, ".main-menu")
actions.move_to_element(menu_item).perform()

# Now the dropdown is visible - click submenu item
submenu = driver.find_element(By.CSS_SELECTOR, ".submenu-item")
actions.move_to_element(submenu).click().perform()

driver.quit()
```

#### move_to_element_with_offset() - Precise Positioning

Move to a specific point within an element:

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com/canvas")

actions = ActionChains(driver)

canvas = driver.find_element(By.ID, "drawing-canvas")

# Move to element center, then offset by x=50, y=30 pixels
actions.move_to_element_with_offset(canvas, 50, 30).perform()

# Click at that specific location
actions.click().perform()

driver.quit()
```

#### click() Variations

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com")

actions = ActionChains(driver)

button = driver.find_element(By.ID, "my-button")

# Click at current mouse position
actions.click().perform()

# Click on specific element
actions.click(button).perform()

# Click and hold (for drag operations)
actions.click_and_hold(button).perform()

# Release mouse button
actions.release().perform()
```

#### double_click() - Double Click

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com")

actions = ActionChains(driver)

# Double-click to edit text
editable_text = driver.find_element(By.CSS_SELECTOR, ".editable")
actions.double_click(editable_text).perform()

# Now type replacement text
actions.send_keys("New text").perform()
```

#### context_click() - Right Click

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import time

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com")

actions = ActionChains(driver)

# Right-click to open context menu
element = driver.find_element(By.ID, "context-menu-trigger")
actions.context_click(element).perform()

# Wait for menu and click option
time.sleep(0.5)  # Wait for menu animation
menu_option = driver.find_element(By.CSS_SELECTOR, ".context-menu .delete-option")
menu_option.click()

driver.quit()
```

#### drag_and_drop() - Drag and Drop

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com/sortable-list")

actions = ActionChains(driver)

# Find source and target elements
source = driver.find_element(By.ID, "draggable-item")
target = driver.find_element(By.ID, "drop-zone")

# Simple drag and drop
actions.drag_and_drop(source, target).perform()

driver.quit()
```

#### drag_and_drop_by_offset() - Drag by Pixels

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com/slider")

actions = ActionChains(driver)

# Move slider by pixel offset
slider = driver.find_element(By.CSS_SELECTOR, ".slider-handle")
actions.drag_and_drop_by_offset(slider, 100, 0).perform()  # Move 100px right

# Or use click_and_hold with move_by_offset
actions.click_and_hold(slider)
actions.move_by_offset(50, 0)
actions.release()
actions.perform()

driver.quit()
```

### Keyboard Actions

#### send_keys() - Type Characters

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com")

actions = ActionChains(driver)

# Type without focusing on specific element (types to active element)
actions.send_keys("Hello World").perform()

# Type to specific element
text_field = driver.find_element(By.ID, "search")
actions.send_keys_to_element(text_field, "search query").perform()

driver.quit()
```

#### key_down() and key_up() - Modifier Keys

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com")

actions = ActionChains(driver)

# Ctrl+A (Select All)
actions.key_down(Keys.CONTROL)
actions.send_keys('a')
actions.key_up(Keys.CONTROL)
actions.perform()

# Shift+Click for multi-select
element1 = driver.find_element(By.CSS_SELECTOR, ".item:first-child")
element2 = driver.find_element(By.CSS_SELECTOR, ".item:last-child")

actions.click(element1)
actions.key_down(Keys.SHIFT)
actions.click(element2)
actions.key_up(Keys.SHIFT)
actions.perform()

driver.quit()
```

**Common Keyboard Shortcuts:**

```python
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains

actions = ActionChains(driver)

# Ctrl+C (Copy)
actions.key_down(Keys.CONTROL).send_keys('c').key_up(Keys.CONTROL).perform()

# Ctrl+V (Paste)
actions.key_down(Keys.CONTROL).send_keys('v').key_up(Keys.CONTROL).perform()

# Ctrl+Z (Undo)
actions.key_down(Keys.CONTROL).send_keys('z').key_up(Keys.CONTROL).perform()

# Ctrl+Shift+T (Reopen closed tab in browsers)
actions.key_down(Keys.CONTROL).key_down(Keys.SHIFT).send_keys('t').key_up(Keys.SHIFT).key_up(Keys.CONTROL).perform()

# Alt+Tab (OS-level, may not work in browser)
actions.key_down(Keys.ALT).send_keys(Keys.TAB).key_up(Keys.ALT).perform()
```

### Chaining Actions

The builder pattern allows fluent action chaining:

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com")

menu = driver.find_element(By.CSS_SELECTOR, ".dropdown-menu")
submenu = driver.find_element(By.CSS_SELECTOR, ".submenu-item")
button = driver.find_element(By.ID, "action-button")

# Chain multiple actions fluently
actions = ActionChains(driver)
actions.move_to_element(menu)\
       .pause(0.5)\
       .move_to_element(submenu)\
       .click()\
       .perform()

# Complex interaction chain
actions = ActionChains(driver)
actions.move_to_element(button)\
       .click_and_hold()\
       .move_by_offset(100, 0)\
       .release()\
       .perform()

driver.quit()
```

### pause() - Adding Delays

Insert pauses between actions:

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com")

actions = ActionChains(driver)

menu = driver.find_element(By.ID, "hover-menu")
option = driver.find_element(By.CSS_SELECTOR, ".menu-option")

# Pause to allow animations to complete
actions.move_to_element(menu)\
       .pause(0.5)\
       .move_to_element(option)\
       .pause(0.3)\
       .click()\
       .perform()

driver.quit()
```

### reset_actions() - Clearing the Queue

Clear queued actions without executing:

```python
from selenium import webdriver
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

actions = ActionChains(driver)

# Queue some actions
actions.move_to_element(some_element)
actions.click()

# Decide not to execute - reset
actions.reset_actions()

# Queue different actions
actions.move_to_element(other_element)
actions.double_click()
actions.perform()

driver.quit()
```

### Complex Interaction Patterns

**Hover Menu Navigation:**

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

def navigate_hover_menu(driver, menu_text, submenu_text):
    """Navigate through a hover-activated dropdown menu"""
    
    actions = ActionChains(driver)
    wait = WebDriverWait(driver, 10)
    
    # Find and hover over main menu
    menu = driver.find_element(By.XPATH, f"//nav//a[text()='{menu_text}']")
    actions.move_to_element(menu).perform()
    
    # Wait for submenu to appear
    submenu = wait.until(EC.visibility_of_element_located(
        (By.XPATH, f"//nav//a[text()='{submenu_text}']")
    ))
    
    # Click submenu item
    actions.move_to_element(submenu).click().perform()

# Usage
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com")

navigate_hover_menu(driver, "Products", "Electronics")

driver.quit()
```

**Drag and Drop Sortable List:**

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import time

def reorder_list_item(driver, item_index_from, item_index_to):
    """Reorder items in a sortable list"""
    
    items = driver.find_elements(By.CSS_SELECTOR, ".sortable-list .list-item")
    
    source = items[item_index_from]
    target = items[item_index_to]
    
    actions = ActionChains(driver)
    
    # Method 1: Simple drag and drop
    actions.drag_and_drop(source, target).perform()
    
    # Method 2: Manual drag (for complex cases)
    # actions.click_and_hold(source)
    # actions.pause(0.3)
    # actions.move_to_element(target)
    # actions.pause(0.3)
    # actions.release()
    # actions.perform()

# Usage
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com/sortable")

reorder_list_item(driver, 0, 3)  # Move first item to fourth position

driver.quit()
```

**Drawing on Canvas:**

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

def draw_on_canvas(driver, canvas_element, points):
    """Draw a shape on a canvas element
    
    Args:
        driver: WebDriver instance
        canvas_element: Canvas WebElement
        points: List of (x, y) coordinate tuples
    """
    actions = ActionChains(driver)
    
    # Move to canvas and start at first point
    actions.move_to_element_with_offset(canvas_element, points[0][0], points[0][1])
    actions.click_and_hold()
    
    # Draw through all points
    for x, y in points[1:]:
        actions.move_to_element_with_offset(canvas_element, x, y)
    
    actions.release()
    actions.perform()

# Usage
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com/drawing")

canvas = driver.find_element(By.ID, "drawing-canvas")

# Draw a triangle
triangle_points = [(50, 10), (10, 90), (90, 90), (50, 10)]
draw_on_canvas(driver, canvas, triangle_points)

driver.quit()
```

**Range Slider Control:**

```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

def set_slider_value(driver, slider_element, percentage):
    """Set slider to a percentage of its range
    
    Args:
        driver: WebDriver instance
        slider_element: Slider WebElement
        percentage: Target percentage (0-100)
    """
    # Get slider dimensions
    size = slider_element.size
    width = size['width']
    
    # Calculate target x offset from left edge
    target_x = int((percentage / 100) * width) - (width // 2)
    
    actions = ActionChains(driver)
    actions.move_to_element(slider_element)
    actions.click()
    actions.move_by_offset(target_x, 0)
    actions.click()
    actions.perform()

# Usage
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
driver.get("https://example.com/slider")

slider = driver.find_element(By.CSS_SELECTOR, ".price-slider")
set_slider_value(driver, slider, 75)  # Set to 75%

driver.quit()
```

### ActionChains Best Practices

```python
from selenium import webdriver
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# 1. Create new ActionChains for each independent action sequence
actions1 = ActionChains(driver)
actions1.move_to_element(element1).click().perform()

actions2 = ActionChains(driver)  # Fresh instance
actions2.move_to_element(element2).double_click().perform()

# 2. Use pauses for animations
actions = ActionChains(driver)
actions.move_to_element(menu).pause(0.5).click().perform()

# 3. Combine with explicit waits
wait = WebDriverWait(driver, 10)
element = wait.until(EC.element_to_be_clickable((By.ID, "target")))
ActionChains(driver).move_to_element(element).click().perform()

# 4. Reset if action sequence changes
actions = ActionChains(driver)
actions.click(element)
# Oops, need different action
actions.reset_actions()
actions.double_click(element).perform()
```

## Key Takeaways

1. **ActionChains** queues actions and executes them together with `perform()`
2. **Mouse actions**: `move_to_element()`, `click()`, `double_click()`, `context_click()`, `drag_and_drop()`
3. **Keyboard actions**: `send_keys()`, `key_down()`, `key_up()` for modifier keys
4. **Method chaining** creates fluent, readable action sequences
5. **`pause()`** adds delays between actions for animations
6. **Create fresh ActionChains** instances for independent action sequences

## Additional Resources

- [Selenium ActionChains Documentation](https://www.selenium.dev/documentation/webdriver/actions_api/) - Official Actions API guide
- [Python ActionChains API Reference](https://selenium-python.readthedocs.io/api.html#module-selenium.webdriver.common.action_chains) - Complete method reference
- [W3C WebDriver Actions](https://www.w3.org/TR/webdriver/#actions) - W3C specification for actions

