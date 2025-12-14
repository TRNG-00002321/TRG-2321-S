# Selenium Actions API

## Learning Objectives
- Use the Actions API for complex user interactions
- Perform mouse actions: hover, drag-and-drop, double-click, right-click
- Execute keyboard actions and key combinations
- Build action chains for sequential interactions
- Simulate realistic complex user interactions

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, basic click and sendKeys methods handle most interactions. However, modern web applications often require more sophisticated interactions: hovering over menus, dragging elements, using keyboard shortcuts, or combining multiple actions.

The Actions API provides fine-grained control over mouse and keyboard, enabling you to simulate exactly how real users interact with your application.

## Understanding the Actions Class

### What is the Actions API?

The `Actions` class provides a way to build chains of user interactions and execute them. It's part of `org.openqa.selenium.interactions`.

```java
import org.openqa.selenium.interactions.Actions;

// Create Actions instance
Actions actions = new Actions(driver);

// Build and perform action
actions.moveToElement(element).click().perform();
```

### Key Concepts

```
Actions API Concepts:
┌─────────────────────────────────────────────────────────────────────┐
│ Builder Pattern:                                                     │
│ └── Chain multiple actions together before executing                │
│                                                                      │
│ perform():                                                           │
│ └── Executes the built chain of actions                             │
│                                                                      │
│ build():                                                             │
│ └── Creates Action object without executing (for reuse)             │
│                                                                      │
│ Action Types:                                                        │
│ ├── Mouse: click, doubleClick, contextClick, moveToElement          │
│ ├── Keyboard: keyDown, keyUp, sendKeys                              │
│ └── Combined: clickAndHold, dragAndDrop, release                    │
└─────────────────────────────────────────────────────────────────────┘
```

## Mouse Actions

### moveToElement() - Hover

Moves the mouse to the center of an element (hover effect).

```java
Actions actions = new Actions(driver);

// Hover over element
WebElement menu = driver.findElement(By.id("dropdown-menu"));
actions.moveToElement(menu).perform();

// Hover then click submenu
WebElement mainMenu = driver.findElement(By.id("products"));
WebElement subMenu = driver.findElement(By.id("electronics"));

actions.moveToElement(mainMenu)
       .pause(Duration.ofMillis(500))  // Wait for submenu
       .moveToElement(subMenu)
       .click()
       .perform();
```

**Use Cases:**
```
moveToElement() is useful for:
├── Revealing dropdown menus on hover
├── Triggering tooltips
├── Exposing hidden elements
├── Testing hover states/styles
└── Accessing nested navigation menus
```

### click() Variations

```java
Actions actions = new Actions(driver);
WebElement element = driver.findElement(By.id("button"));

// Click at current mouse position
actions.click().perform();

// Click on specific element
actions.click(element).perform();

// Click at offset from element center
actions.moveToElement(element, 10, 10).click().perform();
```

### doubleClick()

```java
Actions actions = new Actions(driver);

// Double-click to select word
WebElement textElement = driver.findElement(By.id("content"));
actions.doubleClick(textElement).perform();

// Double-click to open item
WebElement fileIcon = driver.findElement(By.className("file-icon"));
actions.doubleClick(fileIcon).perform();
```

### contextClick() - Right-Click

```java
Actions actions = new Actions(driver);

// Right-click to open context menu
WebElement element = driver.findElement(By.id("document"));
actions.contextClick(element).perform();

// Then click on context menu option
WebElement menuItem = driver.findElement(By.xpath("//li[text()='Copy']"));
menuItem.click();
```

### Drag and Drop

```java
Actions actions = new Actions(driver);

// Method 1: dragAndDrop()
WebElement source = driver.findElement(By.id("draggable"));
WebElement target = driver.findElement(By.id("droppable"));
actions.dragAndDrop(source, target).perform();

// Method 2: dragAndDropBy() - move by offset
actions.dragAndDropBy(source, 100, 50).perform();  // Move 100px right, 50px down

// Method 3: Manual drag and drop (more control)
actions.clickAndHold(source)
       .moveToElement(target)
       .release()
       .perform();

// Method 4: With pause for slow animations
actions.clickAndHold(source)
       .pause(Duration.ofMillis(100))
       .moveToElement(target)
       .pause(Duration.ofMillis(100))
       .release()
       .perform();
```

### clickAndHold() and release()

```java
Actions actions = new Actions(driver);

// Click and hold (for drag operations)
WebElement slider = driver.findElement(By.id("slider-handle"));
actions.clickAndHold(slider).perform();

// Move while holding
actions.moveByOffset(50, 0).perform();

// Release
actions.release().perform();

// Combined slider drag
WebElement sliderTrack = driver.findElement(By.id("slider-track"));
actions.clickAndHold(slider)
       .moveToElement(sliderTrack, 200, 0)  // Move to specific position
       .release()
       .perform();
```

### moveByOffset()

```java
Actions actions = new Actions(driver);

// Move mouse by pixel offset from current position
actions.moveByOffset(100, 50).perform();  // 100px right, 50px down
actions.moveByOffset(-50, -25).perform(); // 50px left, 25px up

// Click at specific coordinates on page
actions.moveByOffset(500, 300).click().perform();
```

## Keyboard Actions

### sendKeys() with Actions

```java
Actions actions = new Actions(driver);

// Type text (not to specific element)
actions.sendKeys("Hello World").perform();

// Type into focused element
WebElement input = driver.findElement(By.id("search"));
input.click();  // Focus the element
actions.sendKeys("search term").perform();

// Send keys to specific element
actions.sendKeys(input, "text").perform();
```

### keyDown() and keyUp()

```java
Actions actions = new Actions(driver);

// Hold Shift and type (uppercase)
actions.keyDown(Keys.SHIFT)
       .sendKeys("hello")
       .keyUp(Keys.SHIFT)
       .perform();
// Types: HELLO

// Ctrl+A (Select All)
actions.keyDown(Keys.CONTROL)
       .sendKeys("a")
       .keyUp(Keys.CONTROL)
       .perform();

// Ctrl+C (Copy)
actions.keyDown(Keys.CONTROL)
       .sendKeys("c")
       .keyUp(Keys.CONTROL)
       .perform();

// Ctrl+V (Paste)
actions.keyDown(Keys.CONTROL)
       .sendKeys("v")
       .keyUp(Keys.CONTROL)
       .perform();
```

### Common Keyboard Shortcuts

```java
Actions actions = new Actions(driver);
WebElement input = driver.findElement(By.id("editor"));
input.click();

// Select all text
actions.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform();

// Copy selected
actions.keyDown(Keys.CONTROL).sendKeys("c").keyUp(Keys.CONTROL).perform();

// Paste
actions.keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL).perform();

// Undo
actions.keyDown(Keys.CONTROL).sendKeys("z").keyUp(Keys.CONTROL).perform();

// Redo
actions.keyDown(Keys.CONTROL)
       .keyDown(Keys.SHIFT)
       .sendKeys("z")
       .keyUp(Keys.SHIFT)
       .keyUp(Keys.CONTROL)
       .perform();

// Save
actions.keyDown(Keys.CONTROL).sendKeys("s").keyUp(Keys.CONTROL).perform();
```

## Action Chains

### Building Complex Interactions

```java
Actions actions = new Actions(driver);

// Chain multiple actions
actions.moveToElement(menu)              // Hover over menu
       .pause(Duration.ofMillis(500))    // Wait for animation
       .moveToElement(submenu)           // Hover over submenu
       .click()                          // Click submenu item
       .perform();                       // Execute all
```

### Using build() for Reusable Actions

```java
Actions actions = new Actions(driver);

// Build action without performing
Action hoverAndClick = actions.moveToElement(element)
                              .click()
                              .build();

// Perform later (can be performed multiple times)
hoverAndClick.perform();
hoverAndClick.perform();  // Can repeat
```

### Complex Form Interaction

```java
public void fillFormWithActions(WebDriver driver) {
    Actions actions = new Actions(driver);
    
    // Tab through form and fill
    actions.click(driver.findElement(By.id("firstName")))
           .sendKeys("John")
           .sendKeys(Keys.TAB)
           .sendKeys("Doe")
           .sendKeys(Keys.TAB)
           .sendKeys("john@example.com")
           .sendKeys(Keys.TAB)
           .sendKeys("password123")
           .sendKeys(Keys.ENTER)
           .perform();
}
```

## Practical Examples

### Example 1: Hover Menu Navigation

```java
@Test
void testHoverMenu() {
    driver.get("https://example.com");
    
    Actions actions = new Actions(driver);
    
    // Hover over main menu
    WebElement productsMenu = driver.findElement(By.id("products"));
    actions.moveToElement(productsMenu).perform();
    
    // Wait for submenu to appear
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    WebElement electronicsOption = wait.until(
        ExpectedConditions.visibilityOfElementLocated(By.linkText("Electronics"))
    );
    
    // Click submenu item
    actions.moveToElement(electronicsOption).click().perform();
    
    // Verify navigation
    assertTrue(driver.getCurrentUrl().contains("electronics"));
}
```

### Example 2: Drag and Drop

```java
@Test
void testDragAndDrop() {
    driver.get("https://example.com/sortable");
    
    Actions actions = new Actions(driver);
    
    // Find source and target
    WebElement item1 = driver.findElement(By.id("item-1"));
    WebElement item3 = driver.findElement(By.id("item-3"));
    
    // Drag item1 to item3's position
    actions.dragAndDrop(item1, item3).perform();
    
    // Verify new order
    List<WebElement> items = driver.findElements(By.className("sortable-item"));
    assertEquals("Item 2", items.get(0).getText());
    assertEquals("Item 3", items.get(1).getText());
    assertEquals("Item 1", items.get(2).getText());
}
```

### Example 3: Slider Manipulation

```java
@Test
void testSlider() {
    driver.get("https://example.com/slider");
    
    Actions actions = new Actions(driver);
    
    WebElement slider = driver.findElement(By.id("price-slider"));
    WebElement sliderHandle = driver.findElement(By.className("slider-handle"));
    
    // Get slider dimensions
    int sliderWidth = slider.getSize().getWidth();
    int handleX = sliderHandle.getLocation().getX();
    
    // Move slider to 75% position
    int targetX = (int) (sliderWidth * 0.75);
    int moveBy = targetX - handleX;
    
    actions.clickAndHold(sliderHandle)
           .moveByOffset(moveBy, 0)
           .release()
           .perform();
    
    // Verify slider value
    String value = driver.findElement(By.id("slider-value")).getText();
    assertTrue(Integer.parseInt(value) >= 70);
}
```

### Example 4: Context Menu

```java
@Test
void testContextMenu() {
    driver.get("https://example.com/editor");
    
    Actions actions = new Actions(driver);
    
    // Select some text
    WebElement textArea = driver.findElement(By.id("content"));
    actions.click(textArea)
           .keyDown(Keys.CONTROL)
           .sendKeys("a")
           .keyUp(Keys.CONTROL)
           .perform();
    
    // Right-click to open context menu
    actions.contextClick(textArea).perform();
    
    // Click "Copy" in context menu
    driver.findElement(By.xpath("//li[@data-action='copy']")).click();
    
    // Verify copy action (application-specific)
}
```

### Example 5: Keyboard Shortcuts

```java
@Test
void testKeyboardShortcuts() {
    driver.get("https://example.com/document");
    
    Actions actions = new Actions(driver);
    WebElement editor = driver.findElement(By.id("editor"));
    
    // Type some text
    editor.click();
    actions.sendKeys("Original text").perform();
    
    // Select all (Ctrl+A)
    actions.keyDown(Keys.CONTROL)
           .sendKeys("a")
           .keyUp(Keys.CONTROL)
           .perform();
    
    // Type replacement (overwrites selected)
    actions.sendKeys("New text").perform();
    
    // Verify
    assertEquals("New text", editor.getAttribute("value"));
    
    // Undo (Ctrl+Z)
    actions.keyDown(Keys.CONTROL)
           .sendKeys("z")
           .keyUp(Keys.CONTROL)
           .perform();
    
    // Verify undo
    assertEquals("Original text", editor.getAttribute("value"));
}
```

## Actions Best Practices

```
Best Practices:
┌─────────────────────────────────────────────────────────────────────┐
│ 1. Always call perform()                                            │
│    └── Action chains do nothing until perform() is called          │
│                                                                      │
│ 2. Use pause() for animations                                       │
│    └── Add delays when UI needs time to respond                    │
│                                                                      │
│ 3. Prefer explicit waits over pauses                                │
│    └── Wait for conditions rather than arbitrary time              │
│                                                                      │
│ 4. Build reusable actions with build()                              │
│    └── For frequently repeated interactions                        │
│                                                                      │
│ 5. Use moveToElement before clicking hidden elements                │
│    └── Ensures element is in viewport                              │
│                                                                      │
│ 6. Release after clickAndHold                                       │
│    └── Always release to avoid stuck mouse state                   │
│                                                                      │
│ 7. Test on target browsers                                          │
│    └── Actions behavior can vary between browsers                  │
└─────────────────────────────────────────────────────────────────────┘
```

## Summary

- **Actions API** enables complex mouse and keyboard interactions
- **Mouse actions**: `moveToElement()`, `click()`, `doubleClick()`, `contextClick()`, `dragAndDrop()`
- **Keyboard actions**: `sendKeys()`, `keyDown()`, `keyUp()` for shortcuts and combinations
- **Action chains** combine multiple actions with `perform()` to execute
- Use **pause()** for animations and **build()** for reusable actions
- Actions API simulates realistic user behavior that basic methods cannot

In the next lesson, you'll learn waiting strategies to handle timing and synchronization challenges.

## Additional Resources

- [Actions Class JavaDoc](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/interactions/Actions.html) - API reference
- [Selenium Actions Documentation](https://www.selenium.dev/documentation/webdriver/actions_api/) - Official guide

