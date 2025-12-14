# Lab: Alerts, Windows, and Frame Handling

## Overview

**Duration:** 60-75 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll handle JavaScript alerts, manage multiple browser windows/tabs, and navigate between frames. These are common challenges in UI automation.

---

## Learning Objectives

By completing this lab, you will:
- Handle JavaScript alerts (accept, dismiss, input)
- Switch between multiple windows and tabs
- Navigate into and out of frames/iframes
- Create utility methods for window management
- Handle complex multi-window scenarios

---

## Prerequisites

- Completed form interactions and wait exercises
- Understanding of WebDriver switchTo() method
- Selenium project set up

---

## Core Tasks

### Task 1: JavaScript Alerts (20 minutes)

**Create `AlertAndWindowTest.java`:**

```java
package com.bookhaven.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AlertAndWindowTest extends BaseTest {

    @Test
    @DisplayName("Accept simple alert")
    void testAcceptSimpleAlert() {
        driver.get("https://the-internet.herokuapp.com/javascript_alerts");
        
        // Click button to trigger alert
        driver.findElement(By.xpath("//button[text()='Click for JS Alert']")).click();
        
        // Switch to alert
        Alert alert = driver.switchTo().alert();
        
        // Verify alert text
        assertEquals("I am a JS Alert", alert.getText());
        
        // Accept (click OK)
        alert.accept();
        
        // Verify result
        WebElement result = driver.findElement(By.id("result"));
        assertEquals("You successfully clicked an alert", result.getText());
    }

    @Test
    @DisplayName("Dismiss confirm dialog")
    void testDismissConfirm() {
        driver.get("https://the-internet.herokuapp.com/javascript_alerts");
        
        // Click button to trigger confirm
        driver.findElement(By.xpath("//button[text()='Click for JS Confirm']")).click();
        
        // Switch to alert
        Alert alert = driver.switchTo().alert();
        
        // Verify text
        assertEquals("I am a JS Confirm", alert.getText());
        
        // Dismiss (click Cancel)
        alert.dismiss();
        
        // Verify result
        WebElement result = driver.findElement(By.id("result"));
        assertEquals("You clicked: Cancel", result.getText());
    }

    @Test
    @DisplayName("Accept confirm dialog")
    void testAcceptConfirm() {
        driver.get("https://the-internet.herokuapp.com/javascript_alerts");
        
        driver.findElement(By.xpath("//button[text()='Click for JS Confirm']")).click();
        
        Alert alert = driver.switchTo().alert();
        alert.accept();
        
        WebElement result = driver.findElement(By.id("result"));
        assertEquals("You clicked: Ok", result.getText());
    }

    @Test
    @DisplayName("Enter text in prompt")
    void testPromptAlert() {
        driver.get("https://the-internet.herokuapp.com/javascript_alerts");
        
        // Click button to trigger prompt
        driver.findElement(By.xpath("//button[text()='Click for JS Prompt']")).click();
        
        // Switch to alert
        Alert alert = driver.switchTo().alert();
        
        // Enter text
        String inputText = "Hello from Selenium!";
        alert.sendKeys(inputText);
        
        // Accept
        alert.accept();
        
        // Verify result
        WebElement result = driver.findElement(By.id("result"));
        assertEquals("You entered: " + inputText, result.getText());
    }

    @Test
    @DisplayName("Wait for alert to appear")
    void testWaitForAlert() {
        driver.get("https://the-internet.herokuapp.com/javascript_alerts");
        
        driver.findElement(By.xpath("//button[text()='Click for JS Alert']")).click();
        
        // Wait for alert
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        
        alert.accept();
    }
}
```

### Task 2: Multiple Windows/Tabs (25 minutes)

**Add window handling tests:**

```java
@Test
@DisplayName("Handle new window")
void testNewWindow() {
    driver.get("https://the-internet.herokuapp.com/windows");
    
    // Store original window handle
    String originalWindow = driver.getWindowHandle();
    
    // Click link that opens new window
    driver.findElement(By.linkText("Click Here")).click();
    
    // Wait for new window
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.numberOfWindowsToBe(2));
    
    // Get all window handles
    Set<String> allWindows = driver.getWindowHandles();
    assertEquals(2, allWindows.size());
    
    // Switch to new window
    for (String windowHandle : allWindows) {
        if (!windowHandle.equals(originalWindow)) {
            driver.switchTo().window(windowHandle);
            break;
        }
    }
    
    // Verify we're on the new window
    assertTrue(driver.getCurrentUrl().contains("new"));
    assertEquals("New Window", driver.findElement(By.tagName("h3")).getText());
    
    // Close new window
    driver.close();
    
    // Switch back to original
    driver.switchTo().window(originalWindow);
    
    // Verify we're back
    assertTrue(driver.getCurrentUrl().contains("windows"));
}

@Test
@DisplayName("Create new tab programmatically")
void testCreateNewTab() {
    driver.get("https://example.com");
    
    String originalWindow = driver.getWindowHandle();
    
    // Create new tab (Selenium 4)
    driver.switchTo().newWindow(org.openqa.selenium.WindowType.TAB);
    
    // Navigate in new tab
    driver.get("https://the-internet.herokuapp.com");
    
    // Verify we have 2 windows/tabs
    assertEquals(2, driver.getWindowHandles().size());
    
    // Close current tab
    driver.close();
    
    // Switch back
    driver.switchTo().window(originalWindow);
    assertEquals("Example Domain", driver.getTitle());
}

@Test
@DisplayName("Create new window programmatically")
void testCreateNewWindow() {
    driver.get("https://example.com");
    
    String originalWindow = driver.getWindowHandle();
    
    // Create new window (Selenium 4)
    driver.switchTo().newWindow(org.openqa.selenium.WindowType.WINDOW);
    
    // Navigate in new window
    driver.get("https://the-internet.herokuapp.com");
    
    assertEquals(2, driver.getWindowHandles().size());
    
    driver.close();
    driver.switchTo().window(originalWindow);
}

@Test
@DisplayName("Handle multiple windows")
void testMultipleWindows() {
    driver.get("https://the-internet.herokuapp.com/windows");
    
    String mainWindow = driver.getWindowHandle();
    
    // Open multiple windows by clicking link multiple times
    WebElement link = driver.findElement(By.linkText("Click Here"));
    link.click();
    
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.numberOfWindowsToBe(2));
    
    // Iterate through all windows
    for (String handle : driver.getWindowHandles()) {
        driver.switchTo().window(handle);
        System.out.println("Window: " + driver.getTitle() + " - " + driver.getCurrentUrl());
    }
    
    // Close all except main window
    for (String handle : driver.getWindowHandles()) {
        if (!handle.equals(mainWindow)) {
            driver.switchTo().window(handle);
            driver.close();
        }
    }
    
    // Back to main
    driver.switchTo().window(mainWindow);
    assertEquals(1, driver.getWindowHandles().size());
}
```

### Task 3: Frame Handling (20 minutes)

**Add frame tests:**

```java
@Test
@DisplayName("Switch to frame by index")
void testSwitchFrameByIndex() {
    driver.get("https://the-internet.herokuapp.com/nested_frames");
    
    // Switch to frame by index (0-based)
    driver.switchTo().frame(0);  // First frame
    
    // Now we're inside the frame
    // Can interact with frame content
    
    // Switch back to main content
    driver.switchTo().defaultContent();
}

@Test
@DisplayName("Switch to frame by name or ID")
void testSwitchFrameByName() {
    driver.get("https://the-internet.herokuapp.com/frames");
    
    // Click to go to iframe page
    driver.findElement(By.linkText("iFrame")).click();
    
    // Switch to frame by ID
    driver.switchTo().frame("mce_0_ifr");
    
    // Now interact with content inside iframe
    WebElement body = driver.findElement(By.id("tinymce"));
    body.clear();
    body.sendKeys("Text inside iframe");
    
    // Switch back
    driver.switchTo().defaultContent();
}

@Test
@DisplayName("Switch to frame by WebElement")
void testSwitchFrameByElement() {
    driver.get("https://the-internet.herokuapp.com/frames");
    
    driver.findElement(By.linkText("iFrame")).click();
    
    // Find iframe element
    WebElement iframe = driver.findElement(By.id("mce_0_ifr"));
    
    // Switch using element
    driver.switchTo().frame(iframe);
    
    // Interact
    WebElement body = driver.findElement(By.id("tinymce"));
    assertNotNull(body);
    
    driver.switchTo().defaultContent();
}

@Test
@DisplayName("Handle nested frames")
void testNestedFrames() {
    driver.get("https://the-internet.herokuapp.com/nested_frames");
    
    // Switch to top frame first
    driver.switchTo().frame("frame-top");
    
    // Now switch to frame inside top frame
    driver.switchTo().frame("frame-left");
    
    // Get content
    String text = driver.findElement(By.tagName("body")).getText();
    assertEquals("LEFT", text);
    
    // Go up one level (parent frame)
    driver.switchTo().parentFrame();
    
    // Now in frame-top, switch to middle
    driver.switchTo().frame("frame-middle");
    String middleText = driver.findElement(By.id("content")).getText();
    assertEquals("MIDDLE", middleText);
    
    // Go all the way back to main document
    driver.switchTo().defaultContent();
}

@Test
@DisplayName("Wait for frame to be available")
void testWaitForFrame() {
    driver.get("https://the-internet.herokuapp.com/frames");
    
    driver.findElement(By.linkText("iFrame")).click();
    
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    // Wait for frame and switch
    wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("mce_0_ifr"));
    
    // Now we're already in the frame
    WebElement body = driver.findElement(By.id("tinymce"));
    assertNotNull(body);
    
    driver.switchTo().defaultContent();
}
```

### Task 4: Utility Methods (10 minutes)

**Create helper class:**

```java
package com.bookhaven.utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

public class WindowHelper {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    public WindowHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    // Alert helpers
    public void acceptAlert() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
    }
    
    public void dismissAlert() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.dismiss();
    }
    
    public String getAlertText() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        return alert.getText();
    }
    
    public void typeInAlert(String text) {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.sendKeys(text);
        alert.accept();
    }
    
    // Window helpers
    public String getCurrentWindowHandle() {
        return driver.getWindowHandle();
    }
    
    public void switchToNewWindow(String originalHandle) {
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }
    
    public void closeAndSwitchBack(String originalHandle) {
        driver.close();
        driver.switchTo().window(originalHandle);
    }
    
    public void closeAllExcept(String keepHandle) {
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(keepHandle)) {
                driver.switchTo().window(handle);
                driver.close();
            }
        }
        driver.switchTo().window(keepHandle);
    }
    
    // Frame helpers
    public void switchToFrame(String nameOrId) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(nameOrId));
    }
    
    public void switchToMainContent() {
        driver.switchTo().defaultContent();
    }
    
    public void switchToParentFrame() {
        driver.switchTo().parentFrame();
    }
}
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Simple alert acceptance test
- [ ] Confirm dialog accept/dismiss tests
- [ ] Prompt input test
- [ ] New window handling test
- [ ] Multiple windows test
- [ ] Create new tab test (Selenium 4)
- [ ] Frame switching by index/name/element
- [ ] Nested frames navigation
- [ ] WindowHelper utility class
- [ ] All tests passing

---

## Submission Checklist

| Item | Completed |
|------|-----------|
| Accept alert | ☐ |
| Dismiss confirm | ☐ |
| Type in prompt | ☐ |
| Handle new window | ☐ |
| Handle multiple windows | ☐ |
| Switch to frame by name | ☐ |
| Switch to frame by element | ☐ |
| Navigate nested frames | ☐ |
| WindowHelper class | ☐ |

---

## Additional Resources

- Written Content: `alerts-window-handling-java.md`, `navigate-methods-java.md`
- [Selenium Window Handling](https://www.selenium.dev/documentation/webdriver/interactions/windows/)

