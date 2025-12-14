# Waiting Strategies in Selenium

## Learning Objectives
- Understand why waiting is essential in Selenium automation
- Implement implicit waits for global element detection
- Use explicit waits with WebDriverWait and ExpectedConditions
- Configure fluent waits for advanced scenarios
- Create custom wait conditions for application-specific needs
- Apply wait best practices for reliable tests

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, timing is often the biggest challenge in UI automation. Web pages load asynchronously—elements appear at different times, AJAX calls complete unpredictably, and animations take time.

Without proper waiting strategies, tests become "flaky"—passing sometimes, failing others. Understanding and implementing correct waiting approaches transforms unreliable tests into stable, trustworthy automation.

## The Timing Problem

### Why Waits Are Necessary

```
The Problem:
┌─────────────────────────────────────────────────────────────────────┐
│ Test Code (Fast):          Browser (Slow):                          │
│                                                                      │
│ driver.get(url)            Loading page...                          │
│ findElement(button)        Still loading...                         │
│ → NoSuchElementException!  Element not rendered yet                 │
│                                                                      │
│ The test runs faster than the page loads!                           │
└─────────────────────────────────────────────────────────────────────┘

Common Timing Issues:
├── Page still loading
├── AJAX request in progress
├── Element being animated
├── JavaScript manipulating DOM
├── Content loaded dynamically
└── Modal/overlay appearing
```

### Types of Waits

```
Wait Types:
┌─────────────────────────────────────────────────────────────────────┐
│ Thread.sleep()        │ DON'T USE - Always waits full duration     │
├───────────────────────┼─────────────────────────────────────────────┤
│ Implicit Wait         │ Global - waits for element to exist        │
├───────────────────────┼─────────────────────────────────────────────┤
│ Explicit Wait         │ Specific - waits for condition             │
├───────────────────────┼─────────────────────────────────────────────┤
│ Fluent Wait           │ Advanced - configurable polling/exceptions │
└───────────────────────┴─────────────────────────────────────────────┘
```

## Implicit Waits

### What is Implicit Wait?

Implicit wait tells WebDriver to poll the DOM for a specified time when trying to find elements that are not immediately available.

```java
import java.time.Duration;

// Set implicit wait (applies to all findElement calls)
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
```

### How Implicit Wait Works

```
Implicit Wait Behavior:
┌─────────────────────────────────────────────────────────────────────┐
│ findElement() called                                                 │
│       │                                                              │
│       ▼                                                              │
│ Element found? ──Yes──→ Return element                              │
│       │                                                              │
│      No                                                              │
│       │                                                              │
│       ▼                                                              │
│ Time elapsed? ──Yes──→ Throw NoSuchElementException                 │
│       │                                                              │
│      No                                                              │
│       │                                                              │
│       ▼                                                              │
│ Poll DOM (default ~500ms)                                           │
│       │                                                              │
│       └─────────────────────────────────────┘                       │
└─────────────────────────────────────────────────────────────────────┘
```

### Implicit Wait Example

```java
public class ImplicitWaitExample {
    
    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        
        // Set implicit wait - applies globally
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        try {
            driver.get("https://example.com");
            
            // Will wait up to 10 seconds for element to appear
            WebElement button = driver.findElement(By.id("dynamic-button"));
            button.click();
            
        } finally {
            driver.quit();
        }
    }
}
```

### Implicit Wait Limitations

```
Limitations:
├── Only waits for element existence (in DOM)
├── Doesn't wait for visibility, clickability, etc.
├── Applies globally - can slow down tests with many "not found" checks
├── Can mask performance issues
├── Combines unpredictably with explicit waits
└── Not recommended for modern Selenium

Example Problem:
// Element exists in DOM but is hidden
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
WebElement hidden = driver.findElement(By.id("hidden-element"));
hidden.click();  // Throws ElementNotInteractableException!
// Implicit wait doesn't help - element was found but not clickable
```

## Explicit Waits

### What is Explicit Wait?

Explicit waits wait for a specific condition to be true before proceeding. They're more flexible and targeted than implicit waits.

```java
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

// Create WebDriverWait
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

// Wait for condition
WebElement element = wait.until(
    ExpectedConditions.visibilityOfElementLocated(By.id("element"))
);
```

### WebDriverWait Basics

```java
// Create wait with timeout
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

// Wait for element to be visible
WebElement visible = wait.until(
    ExpectedConditions.visibilityOfElementLocated(By.id("message"))
);

// Wait for element to be clickable
WebElement clickable = wait.until(
    ExpectedConditions.elementToBeClickable(By.id("submit"))
);
clickable.click();

// Wait for text to be present
wait.until(
    ExpectedConditions.textToBePresentInElementLocated(
        By.id("status"), "Complete"
    )
);
```

### Common ExpectedConditions

**Element Presence and Visibility:**
```java
// Element exists in DOM
ExpectedConditions.presenceOfElementLocated(By.id("element"))

// Element is visible on page
ExpectedConditions.visibilityOfElementLocated(By.id("element"))
ExpectedConditions.visibilityOf(webElement)

// All elements visible
ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("item"))

// Element is clickable (visible + enabled)
ExpectedConditions.elementToBeClickable(By.id("button"))
ExpectedConditions.elementToBeClickable(webElement)
```

**Element State:**
```java
// Element is selected (checkbox/radio)
ExpectedConditions.elementToBeSelected(By.id("checkbox"))

// Element has specific attribute value
ExpectedConditions.attributeContains(By.id("el"), "class", "active")
ExpectedConditions.attributeToBe(By.id("el"), "disabled", "true")

// Element is enabled
// (No direct condition - use elementToBeClickable or custom)
```

**Text Conditions:**
```java
// Element contains text
ExpectedConditions.textToBePresentInElementLocated(
    By.id("message"), "Success"
)

// Element text matches exactly
ExpectedConditions.textToBe(By.id("title"), "Welcome")

// Value in input field
ExpectedConditions.textToBePresentInElementValue(
    By.id("input"), "expected value"
)
```

**Absence/Invisibility:**
```java
// Element no longer visible
ExpectedConditions.invisibilityOfElementLocated(By.id("loading"))

// Element not present
ExpectedConditions.stalenessOf(webElement)

// Element disappears with text
ExpectedConditions.invisibilityOfElementWithText(
    By.className("error"), "Error message"
)
```

**Frame and Window:**
```java
// Frame available and switch to it
ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("frame"))
ExpectedConditions.frameToBeAvailableAndSwitchToIt("frameName")
ExpectedConditions.frameToBeAvailableAndSwitchToIt(0)  // index

// Number of windows
ExpectedConditions.numberOfWindowsToBe(2)
```

**Page/URL:**
```java
// URL contains text
ExpectedConditions.urlContains("success")

// URL matches pattern
ExpectedConditions.urlMatches(".*\\/products\\/\\d+")

// Title contains text
ExpectedConditions.titleContains("Dashboard")

// Title matches exactly
ExpectedConditions.titleIs("Home Page")
```

**Alerts:**
```java
// Alert is present
ExpectedConditions.alertIsPresent()
```

### Combining Conditions

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

// AND - both conditions must be true
wait.until(ExpectedConditions.and(
    ExpectedConditions.visibilityOfElementLocated(By.id("element")),
    ExpectedConditions.elementToBeClickable(By.id("element"))
));

// OR - either condition
wait.until(ExpectedConditions.or(
    ExpectedConditions.visibilityOfElementLocated(By.id("success")),
    ExpectedConditions.visibilityOfElementLocated(By.id("error"))
));

// NOT - condition is false
wait.until(ExpectedConditions.not(
    ExpectedConditions.visibilityOfElementLocated(By.id("loading"))
));
```

## Fluent Waits

### What is Fluent Wait?

Fluent wait provides more control over waiting behavior, including custom polling interval and exception handling.

```java
import org.openqa.selenium.support.ui.FluentWait;

Wait<WebDriver> fluentWait = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(30))
    .pollingEvery(Duration.ofMillis(500))
    .ignoring(NoSuchElementException.class)
    .ignoring(StaleElementReferenceException.class);

WebElement element = fluentWait.until(driver -> 
    driver.findElement(By.id("dynamic-element"))
);
```

### Fluent Wait Configuration

```java
Wait<WebDriver> wait = new FluentWait<>(driver)
    // Maximum wait time
    .withTimeout(Duration.ofSeconds(30))
    
    // How often to check condition
    .pollingEvery(Duration.ofMillis(250))
    
    // Exceptions to ignore during polling
    .ignoring(NoSuchElementException.class)
    .ignoring(StaleElementReferenceException.class)
    .ignoring(ElementNotInteractableException.class)
    
    // Custom message on timeout
    .withMessage("Element was not found within 30 seconds");
```

### Fluent Wait with Custom Condition

```java
Wait<WebDriver> wait = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(20))
    .pollingEvery(Duration.ofSeconds(1));

// Wait using lambda
WebElement element = wait.until(driver -> {
    WebElement el = driver.findElement(By.id("dynamic"));
    return el.isDisplayed() ? el : null;
});

// Wait for custom condition
Boolean result = wait.until(driver -> {
    List<WebElement> items = driver.findElements(By.className("item"));
    return items.size() >= 5;
});
```

## Custom Wait Conditions

### Creating Custom ExpectedConditions

```java
public class CustomConditions {
    
    // Wait for element to have specific CSS property
    public static ExpectedCondition<Boolean> cssPropertyToBe(
            By locator, String property, String value) {
        return driver -> {
            try {
                WebElement element = driver.findElement(locator);
                return value.equals(element.getCssValue(property));
            } catch (NoSuchElementException e) {
                return false;
            }
        };
    }
    
    // Wait for element count to be exact
    public static ExpectedCondition<Boolean> elementCountToBe(
            By locator, int count) {
        return driver -> {
            List<WebElement> elements = driver.findElements(locator);
            return elements.size() == count;
        };
    }
    
    // Wait for text to not be empty
    public static ExpectedCondition<WebElement> textToBeNotEmpty(By locator) {
        return driver -> {
            try {
                WebElement element = driver.findElement(locator);
                String text = element.getText();
                return (text != null && !text.trim().isEmpty()) ? element : null;
            } catch (NoSuchElementException e) {
                return null;
            }
        };
    }
    
    // Wait for attribute to contain value
    public static ExpectedCondition<Boolean> attributeContainsValue(
            WebElement element, String attribute, String value) {
        return driver -> {
            try {
                String attrValue = element.getAttribute(attribute);
                return attrValue != null && attrValue.contains(value);
            } catch (StaleElementReferenceException e) {
                return false;
            }
        };
    }
}
```

### Using Custom Conditions

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

// Use custom condition
wait.until(CustomConditions.cssPropertyToBe(
    By.id("panel"), "display", "block"
));

wait.until(CustomConditions.elementCountToBe(
    By.className("search-result"), 10
));

WebElement element = wait.until(CustomConditions.textToBeNotEmpty(
    By.id("result")
));
```

## Wait Best Practices

### Do's and Don'ts

```
DO:
┌─────────────────────────────────────────────────────────────────────┐
│ ✓ Use explicit waits for specific conditions                        │
│ ✓ Wait for the right condition (clickable, not just visible)       │
│ ✓ Use appropriate timeout values (not too long, not too short)     │
│ ✓ Create helper methods for common wait patterns                    │
│ ✓ Handle timeout exceptions appropriately                           │
│ ✓ Wait before interacting, not after                                │
└─────────────────────────────────────────────────────────────────────┘

DON'T:
┌─────────────────────────────────────────────────────────────────────┐
│ ✗ Use Thread.sleep() (always waits full duration)                   │
│ ✗ Mix implicit and explicit waits (unpredictable behavior)         │
│ ✗ Use very long timeouts to "fix" flaky tests                      │
│ ✗ Ignore timeout exceptions (swallowing errors)                    │
│ ✗ Wait for element presence when you need clickability             │
└─────────────────────────────────────────────────────────────────────┘
```

### Wait Helper Class

```java
public class WaitHelper {
    private final WebDriverWait wait;
    private final WebDriver driver;
    
    public WaitHelper(WebDriver driver, int timeoutSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }
    
    public WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    public WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    public void waitForInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    
    public void waitForText(By locator, String text) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }
    
    public void waitForUrlContains(String urlPart) {
        wait.until(ExpectedConditions.urlContains(urlPart));
    }
    
    public void waitForPageLoad() {
        wait.until(driver -> 
            ((JavascriptExecutor) driver)
                .executeScript("return document.readyState")
                .equals("complete")
        );
    }
    
    public void clickWhenReady(By locator) {
        WebElement element = waitForClickable(locator);
        element.click();
    }
    
    public void typeWhenReady(By locator, String text) {
        WebElement element = waitForClickable(locator);
        element.clear();
        element.sendKeys(text);
    }
}
```

### Using Wait Helper

```java
public class LoginTest {
    private WebDriver driver;
    private WaitHelper wait;
    
    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        wait = new WaitHelper(driver, 10);
    }
    
    @Test
    void testLogin() {
        driver.get("https://example.com/login");
        
        // Use helper methods
        wait.typeWhenReady(By.id("username"), "testuser");
        wait.typeWhenReady(By.id("password"), "password123");
        wait.clickWhenReady(By.id("login-button"));
        
        // Wait for redirect
        wait.waitForUrlContains("/dashboard");
        
        // Verify element appears
        WebElement welcome = wait.waitForVisible(By.id("welcome-message"));
        assertTrue(welcome.getText().contains("Welcome"));
    }
}
```

## Complete Example

```java
package com.example.tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

class WaitStrategiesTest {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver", 
            "src/test/resources/drivers/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        
        // Create explicit wait (preferred over implicit)
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    @DisplayName("Wait for element visibility")
    void testWaitForVisibility() {
        driver.get("https://example.com/async-page");
        
        // Click button that loads content
        driver.findElement(By.id("load-button")).click();
        
        // Wait for content to appear
        WebElement content = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("loaded-content"))
        );
        
        assertNotNull(content);
        assertFalse(content.getText().isEmpty());
    }
    
    @Test
    @DisplayName("Wait for loading spinner to disappear")
    void testWaitForLoadingComplete() {
        driver.get("https://example.com/data-page");
        
        // Trigger data load
        driver.findElement(By.id("fetch-data")).click();
        
        // Wait for spinner to disappear
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
            By.className("loading-spinner")
        ));
        
        // Now interact with loaded data
        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        assertTrue(rows.size() > 0);
    }
    
    @Test
    @DisplayName("Wait for element to be clickable")
    void testWaitForClickable() {
        driver.get("https://example.com/form");
        
        // Fill required fields
        driver.findElement(By.id("name")).sendKeys("John");
        driver.findElement(By.id("email")).sendKeys("john@example.com");
        
        // Wait for submit button to become enabled
        WebElement submitButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.id("submit"))
        );
        
        submitButton.click();
        
        // Wait for success message
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.className("success-message")
        ));
    }
    
    @Test
    @DisplayName("Wait with fluent configuration")
    void testFluentWait() {
        driver.get("https://example.com/slow-page");
        
        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
            .withTimeout(Duration.ofSeconds(30))
            .pollingEvery(Duration.ofMillis(500))
            .ignoring(NoSuchElementException.class)
            .withMessage("Element did not appear within timeout");
        
        WebElement element = fluentWait.until(driver -> {
            WebElement el = driver.findElement(By.id("slow-element"));
            return el.isDisplayed() ? el : null;
        });
        
        assertNotNull(element);
    }
    
    @Test
    @DisplayName("Wait for text content")
    void testWaitForText() {
        driver.get("https://example.com/status");
        
        // Click action that updates status
        driver.findElement(By.id("process")).click();
        
        // Wait for status to change
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
            By.id("status"), "Complete"
        ));
        
        String finalStatus = driver.findElement(By.id("status")).getText();
        assertEquals("Complete", finalStatus);
    }
}
```

## Summary

- **Timing issues** are the main cause of flaky UI tests
- **Implicit waits** apply globally but are limited to element existence
- **Explicit waits** (WebDriverWait) wait for specific conditions—preferred approach
- **ExpectedConditions** provides many ready-to-use conditions
- **Fluent waits** offer fine-grained control over polling and exceptions
- **Custom conditions** handle application-specific scenarios
- **Best practice**: Use explicit waits, avoid Thread.sleep(), create helper methods

This completes Wednesday's Selenium fundamentals. Tomorrow, you'll learn advanced patterns including WebDriverManager, Page Object Model, and Page Factory.

## Additional Resources

- [WebDriverWait JavaDoc](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/support/ui/WebDriverWait.html) - API reference
- [ExpectedConditions JavaDoc](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/support/ui/ExpectedConditions.html) - All conditions
- [Selenium Waits Documentation](https://www.selenium.dev/documentation/webdriver/waits/) - Official guide

