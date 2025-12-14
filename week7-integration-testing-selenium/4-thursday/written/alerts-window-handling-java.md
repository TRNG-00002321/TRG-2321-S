# Handling Alerts, Windows, and Frames in Selenium

## Learning Objectives
- Handle JavaScript alerts using accept(), dismiss(), getText(), and sendKeys()
- Switch between multiple windows and tabs using window handles
- Manage browser tabs and multiple windows effectively
- Work with frames and iframes using switchTo()

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, web applications frequently use alerts, multiple windows, and frames. Without proper handling, tests fail because Selenium can't interact with content in a different context.

Mastering these techniques enables you to automate complex applications that open popups, display confirmations, or embed content in frames.

## Handling JavaScript Alerts

### Alert Types

```
JavaScript Alert Types:
┌─────────────────────────────────────────────────────────────────────┐
│ alert("Message")                                                     │
│ └── Simple message with OK button                                   │
│                                                                      │
│ confirm("Question?")                                                 │
│ └── Message with OK and Cancel buttons                              │
│     Returns true (OK) or false (Cancel)                             │
│                                                                      │
│ prompt("Enter value:", "default")                                    │
│ └── Message with text input, OK and Cancel                          │
│     Returns entered value or null                                   │
└─────────────────────────────────────────────────────────────────────┘
```

### Switching to Alert

```java
import org.openqa.selenium.Alert;

// Switch to alert
Alert alert = driver.switchTo().alert();

// Methods available:
alert.accept();      // Click OK
alert.dismiss();     // Click Cancel
alert.getText();     // Get alert message
alert.sendKeys("text"); // Type into prompt
```

### Handling Simple Alert

```java
@Test
void testSimpleAlert() {
    driver.get("https://example.com/alert-page");
    
    // Trigger alert
    driver.findElement(By.id("show-alert")).click();
    
    // Switch to alert
    Alert alert = driver.switchTo().alert();
    
    // Get message
    String message = alert.getText();
    assertEquals("This is an alert!", message);
    
    // Accept (click OK)
    alert.accept();
    
    // Alert is now closed, back to main page
    assertTrue(driver.findElement(By.id("result")).getText().contains("Accepted"));
}
```

### Handling Confirm Dialog

```java
@Test
void testConfirmAccept() {
    driver.get("https://example.com/confirm-page");
    
    driver.findElement(By.id("show-confirm")).click();
    
    Alert confirm = driver.switchTo().alert();
    assertEquals("Are you sure?", confirm.getText());
    
    // Accept (click OK)
    confirm.accept();
    
    assertTrue(driver.findElement(By.id("result")).getText().contains("Confirmed"));
}

@Test
void testConfirmDismiss() {
    driver.get("https://example.com/confirm-page");
    
    driver.findElement(By.id("show-confirm")).click();
    
    Alert confirm = driver.switchTo().alert();
    
    // Dismiss (click Cancel)
    confirm.dismiss();
    
    assertTrue(driver.findElement(By.id("result")).getText().contains("Cancelled"));
}
```

### Handling Prompt Dialog

```java
@Test
void testPromptDialog() {
    driver.get("https://example.com/prompt-page");
    
    driver.findElement(By.id("show-prompt")).click();
    
    Alert prompt = driver.switchTo().alert();
    assertEquals("Please enter your name:", prompt.getText());
    
    // Type response
    prompt.sendKeys("John Doe");
    
    // Accept
    prompt.accept();
    
    assertTrue(driver.findElement(By.id("result")).getText().contains("John Doe"));
}
```

### Waiting for Alert

```java
// Wait for alert to appear
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

// Trigger action that shows alert
driver.findElement(By.id("show-alert")).click();

// Wait for alert
Alert alert = wait.until(ExpectedConditions.alertIsPresent());

// Handle alert
alert.accept();
```

## Window and Tab Management

### Understanding Window Handles

```
Window Handles:
┌─────────────────────────────────────────────────────────────────────┐
│ Every browser window/tab has a unique handle (String identifier)    │
│                                                                      │
│ getWindowHandle()   → Returns current window's handle               │
│ getWindowHandles()  → Returns Set of all window handles             │
│ switchTo().window() → Switch focus to specific window               │
└─────────────────────────────────────────────────────────────────────┘
```

### Switching Between Windows

```java
@Test
void testMultipleWindows() {
    driver.get("https://example.com");
    
    // Store original window handle
    String originalWindow = driver.getWindowHandle();
    
    // Click link that opens new window
    driver.findElement(By.id("open-new-window")).click();
    
    // Wait for new window
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.numberOfWindowsToBe(2));
    
    // Get all window handles
    Set<String> windowHandles = driver.getWindowHandles();
    
    // Switch to new window
    for (String handle : windowHandles) {
        if (!handle.equals(originalWindow)) {
            driver.switchTo().window(handle);
            break;
        }
    }
    
    // Now in new window - perform actions
    assertTrue(driver.getCurrentUrl().contains("new-page"));
    
    // Close new window
    driver.close();
    
    // Switch back to original
    driver.switchTo().window(originalWindow);
    
    // Verify we're back
    assertTrue(driver.getCurrentUrl().contains("example.com"));
}
```

### Opening New Window (Selenium 4)

```java
// Selenium 4: Create new window programmatically
driver.switchTo().newWindow(WindowType.WINDOW);

// Create new tab
driver.switchTo().newWindow(WindowType.TAB);

// Navigate in new window/tab
driver.get("https://example.com/other-page");
```

### Window Management Helper

```java
public class WindowHelper {
    private WebDriver driver;
    private WebDriverWait wait;
    
    public WindowHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    public String getMainWindowHandle() {
        return driver.getWindowHandle();
    }
    
    public void switchToNewWindow() {
        String original = driver.getWindowHandle();
        
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(original)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }
    
    public void switchToWindow(String handle) {
        driver.switchTo().window(handle);
    }
    
    public void closeCurrentAndSwitchTo(String handle) {
        driver.close();
        driver.switchTo().window(handle);
    }
    
    public void closeAllExceptMain(String mainHandle) {
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(mainHandle)) {
                driver.switchTo().window(handle);
                driver.close();
            }
        }
        driver.switchTo().window(mainHandle);
    }
    
    public int getWindowCount() {
        return driver.getWindowHandles().size();
    }
}
```

### Handling Multiple Tabs

```java
@Test
void testMultipleTabs() {
    driver.get("https://example.com");
    String mainTab = driver.getWindowHandle();
    
    // Open new tab (Selenium 4)
    driver.switchTo().newWindow(WindowType.TAB);
    driver.get("https://example.com/page2");
    
    // Open another tab
    driver.switchTo().newWindow(WindowType.TAB);
    driver.get("https://example.com/page3");
    
    // Now have 3 tabs
    assertEquals(3, driver.getWindowHandles().size());
    
    // Switch back to main tab
    driver.switchTo().window(mainTab);
    
    assertTrue(driver.getCurrentUrl().equals("https://example.com/"));
}
```

## Frame Handling

### What Are Frames?

```html
<!-- HTML Frames -->
<iframe id="content-frame" name="contentFrame" src="content.html">
    <!-- Content inside frame -->
</iframe>

<frame name="navigationFrame" src="nav.html">
```

### Switching to Frame

```java
// By ID or name
driver.switchTo().frame("content-frame");
driver.switchTo().frame("contentFrame");

// By index (0-based)
driver.switchTo().frame(0);

// By WebElement
WebElement frameElement = driver.findElement(By.id("content-frame"));
driver.switchTo().frame(frameElement);
```

### Switching Back from Frame

```java
// Switch to parent frame (one level up)
driver.switchTo().parentFrame();

// Switch to default content (main page)
driver.switchTo().defaultContent();
```

### Frame Handling Example

```java
@Test
void testFrameInteraction() {
    driver.get("https://example.com/page-with-frames");
    
    // Switch to frame
    driver.switchTo().frame("content-frame");
    
    // Now can interact with elements inside frame
    driver.findElement(By.id("frame-button")).click();
    String frameText = driver.findElement(By.id("frame-message")).getText();
    assertEquals("Clicked!", frameText);
    
    // Switch back to main page
    driver.switchTo().defaultContent();
    
    // Now can interact with main page elements
    driver.findElement(By.id("main-button")).click();
}
```

### Nested Frames

```java
@Test
void testNestedFrames() {
    driver.get("https://example.com/nested-frames");
    
    // Switch to outer frame
    driver.switchTo().frame("outer-frame");
    
    // Switch to inner frame
    driver.switchTo().frame("inner-frame");
    
    // Interact with innermost content
    driver.findElement(By.id("inner-button")).click();
    
    // Go back one level
    driver.switchTo().parentFrame();  // Now in outer-frame
    
    // Interact with outer frame content
    driver.findElement(By.id("outer-button")).click();
    
    // Go back to main
    driver.switchTo().defaultContent();
}
```

### Waiting for Frame

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

// Wait for frame and switch to it
wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("frame-id"));

// Or with By locator
wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(
    By.cssSelector("iframe.content-frame")
));

// Now in frame - can interact
driver.findElement(By.id("frame-element")).click();
```

## Complete Example

```java
package com.example.tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.Set;

class AlertsWindowsFramesTest {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    @DisplayName("Handle alert, confirm, and prompt")
    void testAlertHandling() {
        driver.get("https://the-internet.herokuapp.com/javascript_alerts");
        
        // Simple alert
        driver.findElement(By.xpath("//button[text()='Click for JS Alert']")).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("I am a JS Alert", alert.getText());
        alert.accept();
        
        // Confirm - accept
        driver.findElement(By.xpath("//button[text()='Click for JS Confirm']")).click();
        Alert confirm = driver.switchTo().alert();
        confirm.accept();
        assertTrue(driver.findElement(By.id("result")).getText().contains("Ok"));
        
        // Confirm - dismiss
        driver.findElement(By.xpath("//button[text()='Click for JS Confirm']")).click();
        confirm = driver.switchTo().alert();
        confirm.dismiss();
        assertTrue(driver.findElement(By.id("result")).getText().contains("Cancel"));
        
        // Prompt
        driver.findElement(By.xpath("//button[text()='Click for JS Prompt']")).click();
        Alert prompt = driver.switchTo().alert();
        prompt.sendKeys("Test Input");
        prompt.accept();
        assertTrue(driver.findElement(By.id("result")).getText().contains("Test Input"));
    }
    
    @Test
    @DisplayName("Handle multiple windows")
    void testWindowHandling() {
        driver.get("https://the-internet.herokuapp.com/windows");
        
        String mainWindow = driver.getWindowHandle();
        
        // Click link to open new window
        driver.findElement(By.linkText("Click Here")).click();
        
        // Wait for new window
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        
        // Switch to new window
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(mainWindow)) {
                driver.switchTo().window(handle);
            }
        }
        
        // Verify new window content
        assertTrue(driver.findElement(By.tagName("h3")).getText().contains("New Window"));
        
        // Close new window and switch back
        driver.close();
        driver.switchTo().window(mainWindow);
        
        // Verify back on main window
        assertTrue(driver.getCurrentUrl().contains("windows"));
    }
    
    @Test
    @DisplayName("Handle frames")
    void testFrameHandling() {
        driver.get("https://the-internet.herokuapp.com/nested_frames");
        
        // Switch to top frame
        driver.switchTo().frame("frame-top");
        
        // Switch to middle frame within top
        driver.switchTo().frame("frame-middle");
        
        // Get content
        String content = driver.findElement(By.id("content")).getText();
        assertEquals("MIDDLE", content);
        
        // Go back to main content
        driver.switchTo().defaultContent();
        
        // Switch to bottom frame
        driver.switchTo().frame("frame-bottom");
        String bottomContent = driver.findElement(By.tagName("body")).getText();
        assertEquals("BOTTOM", bottomContent);
    }
}
```

## Summary

- **Alerts**: Use `switchTo().alert()` to access; `accept()`, `dismiss()`, `getText()`, `sendKeys()` to interact
- **Windows**: Use `getWindowHandle()` and `getWindowHandles()` to manage; `switchTo().window()` to switch
- **Selenium 4**: `newWindow(WindowType.TAB/WINDOW)` creates new tabs/windows directly
- **Frames**: Use `switchTo().frame()` to enter; `defaultContent()` or `parentFrame()` to exit
- Always **switch context** before interacting with alerts, windows, or frame content
- Use **explicit waits** for alerts (`alertIsPresent`) and frames (`frameToBeAvailableAndSwitchToIt`)

In the next lesson, you'll learn the Page Object Model design pattern for organizing your test code.

## Additional Resources

- [Selenium Alert Handling](https://www.selenium.dev/documentation/webdriver/interactions/alerts/) - Official docs
- [Window Handling](https://www.selenium.dev/documentation/webdriver/interactions/windows/) - Official docs
- [Frame Handling](https://www.selenium.dev/documentation/webdriver/interactions/frames/) - Official docs

