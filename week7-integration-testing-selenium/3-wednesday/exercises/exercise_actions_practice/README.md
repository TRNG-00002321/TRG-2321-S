# Lab: Selenium Actions API

## Overview

**Duration:** 60-75 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll master the Selenium Actions API for complex user interactions like hover, drag-and-drop, double-click, and keyboard combinations.

---

## Learning Objectives

By completing this lab, you will:
- Use the Actions class for complex interactions
- Implement mouse hover actions
- Perform drag-and-drop operations
- Execute double-click and right-click
- Handle keyboard modifier keys

---

## Prerequisites

- Completed form interactions exercise
- Understanding of WebDriver basics
- Selenium project set up

---

## The Scenario

BookHaven's new interface includes drag-and-drop shopping carts, hover menus, and context menus. Standard click() won't work for these features. You need to use the Actions API to test these advanced UI interactions.

---

## Core Tasks

### Task 1: Actions Setup and Mouse Hover (15 minutes)

**Create `ActionsApiTest.java`:**

```java
package com.bookhaven.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static org.junit.jupiter.api.Assertions.*;

class ActionsApiTest extends BaseTest {

    private Actions actions;

    @BeforeEach
    void setupActions() {
        actions = new Actions(driver);
    }

    @Test
    @DisplayName("Mouse hover reveals hidden element")
    void testMouseHover() {
        driver.get("https://the-internet.herokuapp.com/hovers");
        
        // Find the first figure (avatar)
        WebElement figure = driver.findElement(By.cssSelector(".figure"));
        
        // Hover over the figure
        actions.moveToElement(figure).perform();
        
        // Now the caption should be visible
        WebElement caption = figure.findElement(By.cssSelector(".figcaption"));
        assertTrue(caption.isDisplayed(), "Caption should be visible on hover");
        
        // Verify caption content
        String captionText = caption.getText();
        assertTrue(captionText.contains("user1"), "Caption should contain user info");
    }

    @Test
    @DisplayName("Hover over multiple elements")
    void testMultipleHovers() {
        driver.get("https://the-internet.herokuapp.com/hovers");
        
        java.util.List<WebElement> figures = driver.findElements(By.cssSelector(".figure"));
        
        for (int i = 0; i < figures.size(); i++) {
            WebElement figure = figures.get(i);
            
            // Hover
            actions.moveToElement(figure).perform();
            
            // Verify caption visible
            WebElement caption = figure.findElement(By.cssSelector(".figcaption h5"));
            assertTrue(caption.isDisplayed(), "Caption " + i + " should be visible");
            
            // Move away
            actions.moveToElement(driver.findElement(By.tagName("h3"))).perform();
        }
    }

    @Test
    @DisplayName("Hover and click link")
    void testHoverAndClick() {
        driver.get("https://the-internet.herokuapp.com/hovers");
        
        WebElement figure = driver.findElement(By.cssSelector(".figure"));
        
        // Hover to reveal link
        actions.moveToElement(figure).perform();
        
        // Click the revealed link
        WebElement profileLink = figure.findElement(By.cssSelector(".figcaption a"));
        profileLink.click();
        
        // Verify navigation
        assertTrue(driver.getCurrentUrl().contains("/users/"));
    }
}
```

**Your Tasks:**
1. Test hover on all three figures and verify different usernames
2. Test that caption hides when mouse moves away
3. Create a method that hovers and clicks in one action chain

### Task 2: Drag and Drop (20 minutes)

**Add drag-and-drop tests:**

```java
@Test
@DisplayName("Basic drag and drop")
void testDragAndDrop() {
    driver.get("https://the-internet.herokuapp.com/drag_and_drop");
    
    WebElement source = driver.findElement(By.id("column-a"));
    WebElement target = driver.findElement(By.id("column-b"));
    
    // Get initial text
    String sourceInitialText = source.getText();
    String targetInitialText = target.getText();
    
    assertEquals("A", sourceInitialText);
    assertEquals("B", targetInitialText);
    
    // Perform drag and drop
    actions.dragAndDrop(source, target).perform();
    
    // Note: This site might have issues with standard drag-drop
    // Alternative using JavaScript might be needed
}

@Test
@DisplayName("Drag and drop with click-hold-move-release")
void testDragAndDropManual() {
    driver.get("https://the-internet.herokuapp.com/drag_and_drop");
    
    WebElement source = driver.findElement(By.id("column-a"));
    WebElement target = driver.findElement(By.id("column-b"));
    
    // Manual drag and drop sequence
    actions.clickAndHold(source)
           .moveToElement(target)
           .release()
           .perform();
}

@Test
@DisplayName("Drag and drop with offset")
void testDragAndDropByOffset() {
    driver.get("https://jqueryui.com/draggable/");
    
    // Switch to iframe containing the draggable
    driver.switchTo().frame(driver.findElement(By.cssSelector(".demo-frame")));
    
    WebElement draggable = driver.findElement(By.id("draggable"));
    
    // Get initial position
    int initialX = draggable.getLocation().getX();
    int initialY = draggable.getLocation().getY();
    
    // Drag by offset (100px right, 50px down)
    actions.dragAndDropBy(draggable, 100, 50).perform();
    
    // Verify position changed
    int newX = draggable.getLocation().getX();
    int newY = draggable.getLocation().getY();
    
    assertTrue(newX > initialX, "Element should have moved right");
    assertTrue(newY > initialY, "Element should have moved down");
    
    // Switch back to main content
    driver.switchTo().defaultContent();
}
```

**Your Tasks:**
1. Implement a JavaScript-based drag-and-drop for sites where Actions fails
2. Test dragging to a specific pixel location
3. Create a test that drags and drops multiple items

### Task 3: Click Actions (15 minutes)

**Add click variation tests:**

```java
@Test
@DisplayName("Double-click action")
void testDoubleClick() {
    driver.get("https://the-internet.herokuapp.com/add_remove_elements/");
    
    WebElement addButton = driver.findElement(By.cssSelector("button[onclick='addElement()']"));
    
    // Double-click to add two elements
    actions.doubleClick(addButton).perform();
    
    // Verify two elements added
    java.util.List<WebElement> deleteButtons = driver.findElements(By.cssSelector(".added-manually"));
    assertEquals(2, deleteButtons.size(), "Double-click should add 2 elements");
}

@Test
@DisplayName("Context menu (right-click)")
void testContextClick() {
    driver.get("https://the-internet.herokuapp.com/context_menu");
    
    WebElement hotSpot = driver.findElement(By.id("hot-spot"));
    
    // Right-click on the element
    actions.contextClick(hotSpot).perform();
    
    // Handle the alert that appears
    String alertText = driver.switchTo().alert().getText();
    assertTrue(alertText.contains("You selected a context menu"));
    
    // Accept the alert
    driver.switchTo().alert().accept();
}

@Test
@DisplayName("Click at specific coordinates")
void testClickAtOffset() {
    driver.get("https://the-internet.herokuapp.com/");
    
    WebElement heading = driver.findElement(By.tagName("h1"));
    
    // Click at offset from element center
    actions.moveToElement(heading, 10, 10)
           .click()
           .perform();
}
```

**Your Tasks:**
1. Test triple-click to select all text in a paragraph
2. Test click-and-hold on an element
3. Create a test that clicks at exact page coordinates

### Task 4: Keyboard Actions (15 minutes)

**Add keyboard tests:**

```java
import org.openqa.selenium.Keys;

@Test
@DisplayName("Keyboard modifier keys")
void testKeyboardModifiers() {
    driver.get("https://the-internet.herokuapp.com/key_presses");
    
    WebElement input = driver.findElement(By.id("target"));
    
    // Press a regular key
    actions.click(input)
           .sendKeys("a")
           .perform();
    
    WebElement result = driver.findElement(By.id("result"));
    assertTrue(result.getText().contains("A"));
    
    // Press with Shift
    actions.keyDown(Keys.SHIFT)
           .sendKeys("a")
           .keyUp(Keys.SHIFT)
           .perform();
    
    // Result should show SHIFT
    assertTrue(result.getText().contains("A"));
}

@Test
@DisplayName("Ctrl+A, Ctrl+C, Ctrl+V sequence")
void testCopyPaste() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    WebElement usernameField = driver.findElement(By.id("username"));
    WebElement passwordField = driver.findElement(By.id("password"));
    
    // Type text in username
    usernameField.sendKeys("testtext");
    
    // Select all in username field
    actions.click(usernameField)
           .keyDown(Keys.CONTROL)
           .sendKeys("a")
           .keyUp(Keys.CONTROL)
           .perform();
    
    // Copy
    actions.keyDown(Keys.CONTROL)
           .sendKeys("c")
           .keyUp(Keys.CONTROL)
           .perform();
    
    // Click password field and paste
    actions.click(passwordField)
           .keyDown(Keys.CONTROL)
           .sendKeys("v")
           .keyUp(Keys.CONTROL)
           .perform();
    
    // Note: Due to security, password fields might not accept paste
    // This test demonstrates the action sequence
}

@Test
@DisplayName("Arrow key navigation")
void testArrowKeys() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    WebElement usernameField = driver.findElement(By.id("username"));
    
    // Type text
    usernameField.sendKeys("hello");
    
    // Move cursor to beginning
    actions.sendKeys(Keys.HOME).perform();
    
    // Type at beginning
    actions.sendKeys("prefix_").perform();
    
    assertEquals("prefix_hello", usernameField.getAttribute("value"));
}

@Test
@DisplayName("Tab through form fields")
void testTabNavigation() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    WebElement usernameField = driver.findElement(By.id("username"));
    
    // Focus on username
    usernameField.click();
    usernameField.sendKeys("user");
    
    // Tab to password
    actions.sendKeys(Keys.TAB).perform();
    
    // Type in password (now focused)
    actions.sendKeys("password").perform();
    
    // Verify password field has the value
    WebElement passwordField = driver.findElement(By.id("password"));
    assertEquals("password", passwordField.getAttribute("value"));
}
```

### Task 5: Action Chains (10 minutes)

**Add chained action tests:**

```java
@Test
@DisplayName("Complex action chain")
void testComplexActionChain() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    WebElement usernameField = driver.findElement(By.id("username"));
    WebElement passwordField = driver.findElement(By.id("password"));
    WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
    
    // Complete form in one action chain
    actions.click(usernameField)
           .sendKeys("tomsmith")
           .sendKeys(Keys.TAB)
           .sendKeys("SuperSecretPassword!")
           .click(loginButton)
           .perform();
    
    // Verify login
    assertTrue(driver.getCurrentUrl().contains("secure"));
}

@Test
@DisplayName("Build and perform actions separately")
void testBuildActions() {
    driver.get("https://the-internet.herokuapp.com/hovers");
    
    WebElement figure = driver.findElement(By.cssSelector(".figure"));
    
    // Build action without performing
    actions.moveToElement(figure);
    
    // Caption not yet visible (action not performed)
    WebElement caption = figure.findElement(By.cssSelector(".figcaption"));
    // Note: visibility might depend on CSS
    
    // Now perform
    actions.perform();
    
    // Now caption should be visible
    assertTrue(caption.isDisplayed());
}
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Mouse hover tests working
- [ ] Drag and drop tests implemented
- [ ] Double-click tests working
- [ ] Context menu (right-click) test working
- [ ] Keyboard modifier tests implemented
- [ ] Action chain tests working
- [ ] All tests passing

---

## Actions API Quick Reference

```java
Actions actions = new Actions(driver);

// Mouse Actions
actions.moveToElement(element).perform();           // Hover
actions.click(element).perform();                   // Click
actions.doubleClick(element).perform();             // Double-click
actions.contextClick(element).perform();            // Right-click
actions.clickAndHold(element).perform();            // Click and hold
actions.release().perform();                        // Release

// Drag and Drop
actions.dragAndDrop(source, target).perform();
actions.dragAndDropBy(element, xOffset, yOffset).perform();

// Keyboard
actions.sendKeys(Keys.ENTER).perform();
actions.keyDown(Keys.SHIFT).sendKeys("text").keyUp(Keys.SHIFT).perform();

// Chaining
actions.moveToElement(e1)
       .click()
       .sendKeys("text")
       .perform();
```

---

## Submission Checklist

| Action | Implemented | Passing |
|--------|-------------|---------|
| Mouse hover | ☐ | ☐ |
| Multiple hovers | ☐ | ☐ |
| Drag and drop | ☐ | ☐ |
| Double-click | ☐ | ☐ |
| Context click | ☐ | ☐ |
| Keyboard modifiers | ☐ | ☐ |
| Action chains | ☐ | ☐ |

---

## Additional Resources

- Written Content: `actions-api-java.md`
- [Actions Class Javadoc](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/interactions/Actions.html)

