# WebElement Interaction Methods

## Learning Objectives
- Master core interaction methods: click(), sendKeys(), clear(), getText()
- Use element inspection methods: getAttribute(), isDisplayed(), isEnabled(), isSelected()
- Apply interaction best practices for reliable test automation
- Handle common interaction scenarios effectively

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, locating elements is only the first step. Once found, you need to interact with them—clicking buttons, filling forms, reading content. These interaction methods form the vocabulary of UI automation.

Understanding each method's behavior, edge cases, and best practices ensures your tests work reliably across different browsers, page states, and timing conditions.

## Core Interaction Methods

### click()

Simulates a mouse click on an element.

```java
// Basic click
WebElement button = driver.findElement(By.id("submit"));
button.click();

// Chain finding and clicking
driver.findElement(By.id("submit")).click();

// Click on link
driver.findElement(By.linkText("Home")).click();

// Click on checkbox
driver.findElement(By.id("agree")).click();
```

**click() Behavior:**
```
What click() does:
├── Scrolls element into view (if needed)
├── Moves to center of element
├── Performs mouse down and mouse up
└── Triggers associated event handlers

Requirements:
├── Element must be visible
├── Element must be enabled
├── Element must not be obscured by other elements
└── Element must be interactable
```

**Common Click Scenarios:**
```java
// Click button
driver.findElement(By.xpath("//button[@type='submit']")).click();

// Click link
driver.findElement(By.linkText("Learn More")).click();

// Click checkbox (toggles state)
WebElement checkbox = driver.findElement(By.id("terms"));
if (!checkbox.isSelected()) {
    checkbox.click();
}

// Click radio button
driver.findElement(By.xpath("//input[@value='option1']")).click();

// Click element by coordinates (advanced)
// See Actions API for precise control
```

### sendKeys()

Types text into an element or sends keyboard keys.

```java
// Type text
WebElement input = driver.findElement(By.id("username"));
input.sendKeys("testuser");

// Type into password field
driver.findElement(By.id("password")).sendKeys("secret123");

// Append to existing text
input.sendKeys(" additional text");

// Send special keys
import org.openqa.selenium.Keys;

input.sendKeys(Keys.ENTER);  // Press Enter
input.sendKeys(Keys.TAB);    // Press Tab
input.sendKeys(Keys.ESCAPE); // Press Escape

// Key combinations
input.sendKeys(Keys.CONTROL, "a");  // Select all
input.sendKeys(Keys.CONTROL, "c");  // Copy
input.sendKeys(Keys.CONTROL, "v");  // Paste

// Clear and type (common pattern)
input.clear();
input.sendKeys("new value");
```

**Keys Enum:**
```java
// Common Keys values
Keys.ENTER       // Enter/Return key
Keys.TAB         // Tab key
Keys.ESCAPE      // Escape key
Keys.BACK_SPACE  // Backspace key
Keys.DELETE      // Delete key
Keys.SPACE       // Space bar
Keys.ARROW_UP    // Up arrow
Keys.ARROW_DOWN  // Down arrow
Keys.ARROW_LEFT  // Left arrow
Keys.ARROW_RIGHT // Right arrow
Keys.HOME        // Home key
Keys.END         // End key
Keys.PAGE_UP     // Page Up
Keys.PAGE_DOWN   // Page Down
Keys.CONTROL     // Control/Ctrl key
Keys.SHIFT       // Shift key
Keys.ALT         // Alt key
Keys.F1 - Keys.F12  // Function keys
```

### clear()

Clears the contents of an editable element.

```java
// Clear text field
WebElement input = driver.findElement(By.id("search"));
input.clear();

// Clear and type new value
input.clear();
input.sendKeys("new search term");

// Clear textarea
driver.findElement(By.id("comments")).clear();
```

**clear() Behavior:**
```
What clear() does:
├── Removes all text from the element
├── Works on input and textarea elements
├── Does NOT work on contenteditable divs
└── Triggers change events

Limitations:
├── Only works on editable elements
├── Some JavaScript-heavy inputs may not respond
├── May need alternative approach for React/Angular inputs
```

**Alternative Clearing:**
```java
// Using keyboard shortcuts (more reliable for some apps)
WebElement input = driver.findElement(By.id("field"));
input.sendKeys(Keys.CONTROL, "a");  // Select all
input.sendKeys(Keys.DELETE);        // Delete selected

// Using JavaScript (last resort)
((JavascriptExecutor) driver).executeScript(
    "arguments[0].value = ''", input
);
```

### getText()

Gets the visible text content of an element.

```java
// Get text from element
WebElement heading = driver.findElement(By.tagName("h1"));
String text = heading.getText();

// Get button text
String buttonLabel = driver.findElement(By.id("submit")).getText();

// Get text from paragraph
String content = driver.findElement(By.className("description")).getText();

// Get text from list items
List<WebElement> items = driver.findElements(By.xpath("//ul/li"));
for (WebElement item : items) {
    System.out.println(item.getText());
}
```

**getText() Behavior:**
```
What getText() returns:
├── Visible text content
├── Includes text from child elements
├── Excludes hidden text (display: none)
├── Preserves line breaks as \n
└── Trims leading/trailing whitespace

Returns empty string when:
├── Element is empty
├── Element is not displayed
├── Element contains only hidden content
```

### getAttribute()

Gets the value of an element's attribute.

```java
// Get attribute value
WebElement link = driver.findElement(By.id("home-link"));
String href = link.getAttribute("href");

// Get input value
WebElement input = driver.findElement(By.id("username"));
String value = input.getAttribute("value");

// Get class attribute
String classes = driver.findElement(By.id("button")).getAttribute("class");

// Get data attributes
String dataId = driver.findElement(By.id("item")).getAttribute("data-id");

// Get boolean attribute
String disabled = driver.findElement(By.id("submit")).getAttribute("disabled");
// Returns "true" or null (not "false")

// Check if attribute exists
String attr = element.getAttribute("readonly");
if (attr != null) {
    // Attribute exists
}
```

**Common Attributes:**
```
Frequently accessed attributes:
├── href      - Link destination
├── src       - Image/media source
├── value     - Input current value
├── class     - CSS classes
├── type      - Input type
├── name      - Form element name
├── placeholder - Input placeholder text
├── title     - Tooltip text
├── alt       - Image alt text
├── data-*    - Custom data attributes
└── disabled  - Disabled state
```

## Element State Methods

### isDisplayed()

Checks if element is visible on the page.

```java
WebElement element = driver.findElement(By.id("message"));

if (element.isDisplayed()) {
    System.out.println("Element is visible");
    System.out.println("Text: " + element.getText());
}

// Wait for element to be displayed
// (covered in Waiting Strategies)
```

**isDisplayed() Checks:**
```
Returns true when:
├── Element exists in DOM
├── Element has size > 0
├── Element is not styled as display: none
├── Element is not styled as visibility: hidden
├── Element is not off-screen
└── Element opacity > 0 (browser-dependent)
```

### isEnabled()

Checks if element is enabled for interaction.

```java
WebElement submitButton = driver.findElement(By.id("submit"));

if (submitButton.isEnabled()) {
    submitButton.click();
} else {
    System.out.println("Button is disabled");
}

// Common pattern: wait for button to enable
// Fill required fields first
driver.findElement(By.id("email")).sendKeys("test@example.com");
// Then check if submit is now enabled
```

**isEnabled() Checks:**
```
Returns false when:
├── Element has disabled attribute
├── Element has disabled property
├── Element is in disabled fieldset
└── Browser determines element not interactive
```

### isSelected()

Checks if checkbox/radio button/option is selected.

```java
// Check checkbox state
WebElement checkbox = driver.findElement(By.id("agree"));
if (!checkbox.isSelected()) {
    checkbox.click();  // Select it
}

// Check radio button state
WebElement option = driver.findElement(By.id("option1"));
boolean isSelected = option.isSelected();

// Check dropdown option
// (Better to use Select class - covered separately)
```

## Practical Patterns

### Form Filling Pattern

```java
public class FormHelper {
    private WebDriver driver;
    
    public FormHelper(WebDriver driver) {
        this.driver = driver;
    }
    
    public void fillTextField(By locator, String value) {
        WebElement field = driver.findElement(locator);
        field.clear();
        field.sendKeys(value);
    }
    
    public void selectCheckbox(By locator, boolean shouldBeSelected) {
        WebElement checkbox = driver.findElement(locator);
        if (checkbox.isSelected() != shouldBeSelected) {
            checkbox.click();
        }
    }
    
    public void clickIfEnabled(By locator) {
        WebElement element = driver.findElement(locator);
        if (element.isDisplayed() && element.isEnabled()) {
            element.click();
        } else {
            throw new IllegalStateException("Element not clickable");
        }
    }
}
```

### Safe Click Pattern

```java
public void safeClick(WebElement element) {
    // Verify element state before clicking
    if (!element.isDisplayed()) {
        throw new IllegalStateException("Element is not displayed");
    }
    if (!element.isEnabled()) {
        throw new IllegalStateException("Element is not enabled");
    }
    
    try {
        element.click();
    } catch (ElementClickInterceptedException e) {
        // Element obscured, try JavaScript click
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].click()", element
        );
    }
}
```

### Text Extraction Pattern

```java
public class TextExtractor {
    private WebDriver driver;
    
    public String getTextSafely(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            if (element.isDisplayed()) {
                return element.getText().trim();
            }
        } catch (NoSuchElementException e) {
            // Element not found
        }
        return "";
    }
    
    public String getInputValue(By locator) {
        WebElement input = driver.findElement(locator);
        return input.getAttribute("value");
    }
    
    public List<String> getAllText(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return elements.stream()
            .filter(WebElement::isDisplayed)
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }
}
```

## Complete Example

```java
package com.example.tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import static org.junit.jupiter.api.Assertions.*;

class InteractionMethodsTest {
    
    private WebDriver driver;
    
    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver", 
            "src/test/resources/drivers/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    @DisplayName("Complete form interaction example")
    void testFormInteractions() {
        driver.get("https://example.com/form");
        
        // Text input
        WebElement nameField = driver.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("John Doe");
        assertEquals("John Doe", nameField.getAttribute("value"));
        
        // Email input
        WebElement emailField = driver.findElement(By.id("email"));
        emailField.sendKeys("john@example.com");
        assertTrue(emailField.isEnabled());
        
        // Checkbox
        WebElement termsCheckbox = driver.findElement(By.id("terms"));
        assertFalse(termsCheckbox.isSelected());
        termsCheckbox.click();
        assertTrue(termsCheckbox.isSelected());
        
        // Submit button state
        WebElement submitButton = driver.findElement(By.id("submit"));
        assertTrue(submitButton.isDisplayed());
        assertTrue(submitButton.isEnabled());
        assertEquals("Submit", submitButton.getText());
        
        // Submit form
        submitButton.click();
        
        // Verify success message
        WebElement successMessage = driver.findElement(By.className("success"));
        assertTrue(successMessage.isDisplayed());
        assertTrue(successMessage.getText().contains("Thank you"));
    }
    
    @Test
    @DisplayName("Keyboard interaction example")
    void testKeyboardInteractions() {
        driver.get("https://example.com/search");
        
        WebElement searchBox = driver.findElement(By.id("search"));
        
        // Type search term
        searchBox.sendKeys("selenium webdriver");
        
        // Press Enter to search
        searchBox.sendKeys(Keys.ENTER);
        
        // Verify results loaded
        WebElement results = driver.findElement(By.id("results"));
        assertTrue(results.isDisplayed());
    }
    
    @Test
    @DisplayName("Attribute inspection example")
    void testAttributeInspection() {
        driver.get("https://example.com/page");
        
        // Get link attributes
        WebElement link = driver.findElement(By.id("main-link"));
        String href = link.getAttribute("href");
        String target = link.getAttribute("target");
        String linkText = link.getText();
        
        assertNotNull(href);
        assertTrue(href.startsWith("https://"));
        assertEquals("Learn More", linkText);
        
        // Get image attributes
        WebElement image = driver.findElement(By.id("logo"));
        String src = image.getAttribute("src");
        String alt = image.getAttribute("alt");
        
        assertNotNull(src);
        assertNotNull(alt);
    }
}
```

## Interaction Best Practices

```
Best Practices:
┌─────────────────────────────────────────────────────────────────────┐
│ 1. Always verify element state before interaction                   │
│    └── Use isDisplayed(), isEnabled() when appropriate             │
│                                                                      │
│ 2. Clear before typing in text fields                               │
│    └── Prevents unexpected text concatenation                       │
│                                                                      │
│ 3. Use getAttribute("value") for input values, not getText()        │
│    └── getText() returns visible text, not input value             │
│                                                                      │
│ 4. Handle checkbox/radio state explicitly                           │
│    └── Check isSelected() before clicking to ensure desired state  │
│                                                                      │
│ 5. Use explicit waits instead of implicit                           │
│    └── Wait for specific conditions before interaction             │
│                                                                      │
│ 6. Consider using Actions API for complex interactions              │
│    └── Hover, drag-drop, key combinations                          │
│                                                                      │
│ 7. Add assertions after interactions                                │
│    └── Verify the interaction had expected effect                  │
└─────────────────────────────────────────────────────────────────────┘
```

## Summary

- **click()** simulates mouse clicks; requires visible, enabled element
- **sendKeys()** types text and sends special keys (Enter, Tab, etc.)
- **clear()** removes content from editable elements
- **getText()** retrieves visible text; **getAttribute("value")** gets input values
- **State methods** (`isDisplayed()`, `isEnabled()`, `isSelected()`) verify element conditions
- **Best practices** include state verification, clearing before typing, and proper assertions

In the next lesson, you'll learn to work with Select elements (dropdowns) using Selenium's specialized Select class.

## Additional Resources

- [WebElement Interface](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/WebElement.html) - JavaDoc reference
- [Keys Enum](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/Keys.html) - All keyboard keys
- [Selenium Best Practices](https://www.selenium.dev/documentation/test_practices/) - Official guidelines

