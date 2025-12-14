# Lab: Selenium Wait Strategies

## Overview

**Duration:** 60-75 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll implement various waiting strategies to handle dynamic web page elements. You'll learn when to use implicit waits, explicit waits, and fluent waits.

---

## Learning Objectives

By completing this lab, you will:
- Understand the problems waits solve
- Implement implicit waits
- Master explicit waits with ExpectedConditions
- Create fluent waits with custom conditions
- Choose the right wait strategy for different scenarios

---

## Prerequisites

- Completed previous Selenium exercises
- Understanding of dynamic web content
- WebDriver project set up

---

## The Scenario

BookHaven's web pages load data dynamically via AJAX. Elements appear after delays, spinners indicate loading, and content changes based on user actions. Without proper waits, your tests will fail with "element not found" errors. Master waiting strategies to create stable, reliable tests.

---

## Core Tasks

### Task 1: Understanding the Problem (10 minutes)

**Create `WaitStrategiesTest.java`:**

```java
package com.bookhaven.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class WaitStrategiesTest extends BaseTest {

    @Test
    @DisplayName("Problem: Element not immediately available")
    void testWithoutWait_WillFail() {
        driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");
        
        // Click start button
        driver.findElement(By.cssSelector("#start button")).click();
        
        // This will FAIL - element is hidden during loading
        try {
            WebElement result = driver.findElement(By.id("finish"));
            // Element exists but might not be visible yet
            String text = result.getText();
            System.out.println("Text (might be empty): '" + text + "'");
        } catch (NoSuchElementException e) {
            System.out.println("Element not found - expected for this demo");
        }
    }
}
```

### Task 2: Implicit Waits (15 minutes)

**Add implicit wait tests:**

```java
@Test
@DisplayName("Implicit wait - global timeout")
void testImplicitWait() {
    // Set implicit wait for all findElement calls
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    
    driver.get("https://the-internet.herokuapp.com/dynamic_loading/2");
    
    // Click start button
    driver.findElement(By.cssSelector("#start button")).click();
    
    // This findElement will wait up to 10 seconds for element to exist
    WebElement result = driver.findElement(By.cssSelector("#finish h4"));
    
    // Note: Implicit wait only waits for presence, not visibility
    // The element might be present but hidden
    assertNotNull(result);
    
    // Reset implicit wait (best practice)
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
}

@Test
@DisplayName("Implicit wait - affects all findElement calls")
void testImplicitWaitScope() {
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    
    driver.get("https://the-internet.herokuapp.com/");
    
    long startTime = System.currentTimeMillis();
    
    try {
        // This element doesn't exist - will wait full 5 seconds
        driver.findElement(By.id("nonexistent-element"));
        fail("Should have thrown NoSuchElementException");
    } catch (NoSuchElementException e) {
        long elapsed = System.currentTimeMillis() - startTime;
        assertTrue(elapsed >= 5000, "Should have waited at least 5 seconds");
        System.out.println("Waited " + elapsed + "ms before failing");
    }
    
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
}
```

**Implicit Wait Pros and Cons:**
```
Pros:
✓ Simple to set up
✓ Applies globally
✓ Good for consistently slow apps

Cons:
✗ Can't wait for specific conditions
✗ Affects ALL findElement calls (slows down negative tests)
✗ Only checks for presence, not visibility
✗ Can't customize per element
```

### Task 3: Explicit Waits with WebDriverWait (20 minutes)

**Add explicit wait tests:**

```java
@Test
@DisplayName("Explicit wait - visibility of element")
void testExplicitWaitVisibility() {
    driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");
    
    // Click start button
    driver.findElement(By.cssSelector("#start button")).click();
    
    // Wait up to 10 seconds for element to be visible
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement result = wait.until(
        ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#finish h4"))
    );
    
    assertEquals("Hello World!", result.getText());
}

@Test
@DisplayName("Explicit wait - element to be clickable")
void testExplicitWaitClickable() {
    driver.get("https://the-internet.herokuapp.com/dynamic_loading/2");
    
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    // Wait for start button to be clickable
    WebElement startButton = wait.until(
        ExpectedConditions.elementToBeClickable(By.cssSelector("#start button"))
    );
    startButton.click();
    
    // Wait for result
    WebElement result = wait.until(
        ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#finish h4"))
    );
    
    assertEquals("Hello World!", result.getText());
}

@Test
@DisplayName("Explicit wait - presence of element")
void testExplicitWaitPresence() {
    driver.get("https://the-internet.herokuapp.com/dynamic_loading/2");
    
    driver.findElement(By.cssSelector("#start button")).click();
    
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    // Wait for element to exist in DOM (might not be visible)
    WebElement result = wait.until(
        ExpectedConditions.presenceOfElementLocated(By.cssSelector("#finish h4"))
    );
    
    // Then wait for it to be visible
    wait.until(ExpectedConditions.visibilityOf(result));
    
    assertEquals("Hello World!", result.getText());
}

@Test
@DisplayName("Explicit wait - text to be present")
void testExplicitWaitText() {
    driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");
    
    driver.findElement(By.cssSelector("#start button")).click();
    
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    // Wait for specific text
    boolean hasText = wait.until(
        ExpectedConditions.textToBePresentInElementLocated(
            By.id("finish"), "Hello World!"
        )
    );
    
    assertTrue(hasText);
}

@Test
@DisplayName("Explicit wait - element to disappear")
void testExplicitWaitInvisibility() {
    driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");
    
    driver.findElement(By.cssSelector("#start button")).click();
    
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    // Wait for loading indicator to disappear
    wait.until(
        ExpectedConditions.invisibilityOfElementLocated(By.id("loading"))
    );
    
    // Now safe to interact with result
    WebElement result = driver.findElement(By.cssSelector("#finish h4"));
    assertEquals("Hello World!", result.getText());
}

@Test
@DisplayName("Explicit wait - URL contains")
void testExplicitWaitUrl() {
    driver.get("https://the-internet.herokuapp.com/");
    
    driver.findElement(By.linkText("Form Authentication")).click();
    
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    // Wait for URL to contain "login"
    wait.until(ExpectedConditions.urlContains("login"));
    
    assertTrue(driver.getCurrentUrl().contains("login"));
}

@Test
@DisplayName("Explicit wait - title contains")
void testExplicitWaitTitle() {
    driver.get("https://the-internet.herokuapp.com/");
    
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    // Wait for title
    wait.until(ExpectedConditions.titleContains("Internet"));
    
    assertTrue(driver.getTitle().contains("Internet"));
}
```

**Your Tasks:**
1. Create a test waiting for an alert to be present
2. Create a test waiting for a frame to be available
3. Create a test waiting for number of elements to be more than X

### Task 4: Common ExpectedConditions (10 minutes)

**Reference test showing all common conditions:**

```java
@Test
@DisplayName("ExpectedConditions catalog")
void testExpectedConditionsCatalog() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    By locator = By.id("example");
    WebElement element = driver.findElement(By.tagName("body")); // placeholder
    
    // Element presence/visibility
    // wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    // wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    // wait.until(ExpectedConditions.visibilityOf(element));
    // wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    
    // Element state
    // wait.until(ExpectedConditions.elementToBeClickable(locator));
    // wait.until(ExpectedConditions.elementToBeSelected(element));
    // wait.until(ExpectedConditions.elementSelectionStateToBe(element, true));
    
    // Element disappearance
    // wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    // wait.until(ExpectedConditions.stalenessOf(element));
    
    // Text conditions
    // wait.until(ExpectedConditions.textToBePresentInElement(element, "text"));
    // wait.until(ExpectedConditions.textToBePresentInElementValue(element, "value"));
    
    // Page conditions
    // wait.until(ExpectedConditions.titleIs("Exact Title"));
    // wait.until(ExpectedConditions.titleContains("Partial"));
    // wait.until(ExpectedConditions.urlToBe("http://exact.url"));
    // wait.until(ExpectedConditions.urlContains("partial"));
    
    // Frame and alert
    // wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    // wait.until(ExpectedConditions.alertIsPresent());
    
    // Logical combinations
    // wait.until(ExpectedConditions.and(condition1, condition2));
    // wait.until(ExpectedConditions.or(condition1, condition2));
    // wait.until(ExpectedConditions.not(condition));
    
    assertTrue(true); // Placeholder - this is a reference test
}
```

### Task 5: Fluent Wait with Custom Conditions (15 minutes)

**Add fluent wait tests:**

```java
@Test
@DisplayName("Fluent wait with custom polling")
void testFluentWait() {
    driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");
    
    driver.findElement(By.cssSelector("#start button")).click();
    
    // Create fluent wait with custom settings
    FluentWait<org.openqa.selenium.WebDriver> fluentWait = new FluentWait<>(driver)
        .withTimeout(Duration.ofSeconds(30))        // Max wait time
        .pollingEvery(Duration.ofMillis(500))       // Check every 500ms
        .ignoring(NoSuchElementException.class);    // Ignore this exception
    
    WebElement result = fluentWait.until(
        driver -> driver.findElement(By.cssSelector("#finish h4"))
    );
    
    assertEquals("Hello World!", result.getText());
}

@Test
@DisplayName("Fluent wait with custom condition")
void testFluentWaitCustomCondition() {
    driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");
    
    driver.findElement(By.cssSelector("#start button")).click();
    
    FluentWait<org.openqa.selenium.WebDriver> wait = new FluentWait<>(driver)
        .withTimeout(Duration.ofSeconds(30))
        .pollingEvery(Duration.ofSeconds(1))
        .ignoring(NoSuchElementException.class);
    
    // Custom condition: element exists and has specific text
    String text = wait.until(new Function<org.openqa.selenium.WebDriver, String>() {
        public String apply(org.openqa.selenium.WebDriver driver) {
            WebElement element = driver.findElement(By.cssSelector("#finish h4"));
            String text = element.getText();
            if (text != null && !text.isEmpty()) {
                return text;
            }
            return null; // Return null to continue waiting
        }
    });
    
    assertEquals("Hello World!", text);
}

@Test
@DisplayName("Fluent wait with lambda")
void testFluentWaitLambda() {
    driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");
    
    driver.findElement(By.cssSelector("#start button")).click();
    
    FluentWait<org.openqa.selenium.WebDriver> wait = new FluentWait<>(driver)
        .withTimeout(Duration.ofSeconds(30))
        .pollingEvery(Duration.ofMillis(500))
        .ignoring(NoSuchElementException.class);
    
    // Lambda version
    WebElement result = wait.until(d -> {
        WebElement elem = d.findElement(By.cssSelector("#finish h4"));
        return elem.isDisplayed() ? elem : null;
    });
    
    assertEquals("Hello World!", result.getText());
}
```

### Task 6: Wait Strategy Best Practices (10 minutes)

**Create a reusable wait utility:**

```java
// Add to your BaseTest or create WaitHelper class
public class WaitHelper {
    private WebDriver driver;
    private WebDriverWait wait;
    
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
    
    public boolean waitForText(By locator, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }
    
    public void waitForUrlContains(String urlPart) {
        wait.until(ExpectedConditions.urlContains(urlPart));
    }
}

// Usage in test:
@Test
@DisplayName("Using WaitHelper")
void testWithWaitHelper() {
    WaitHelper waitHelper = new WaitHelper(driver, 10);
    
    driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");
    
    waitHelper.waitForClickable(By.cssSelector("#start button")).click();
    waitHelper.waitForInvisible(By.id("loading"));
    
    WebElement result = waitHelper.waitForVisible(By.cssSelector("#finish h4"));
    assertEquals("Hello World!", result.getText());
}
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Demonstrated the problem without waits
- [ ] Implemented implicit wait tests
- [ ] Implemented explicit wait tests (visibility, clickable, text)
- [ ] Implemented wait for element disappearance
- [ ] Implemented fluent wait with custom conditions
- [ ] Created reusable wait helper methods
- [ ] All tests passing

---

## Wait Strategy Decision Guide

```
┌─────────────────────────────────────────────────────────────────┐
│                  Which Wait Should I Use?                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Need global timeout for slow app?                              │
│  └── YES → Implicit Wait                                         │
│                                                                  │
│  Need to wait for specific condition?                           │
│  └── YES → Explicit Wait (WebDriverWait)                        │
│                                                                  │
│  Need custom polling or multiple ignored exceptions?            │
│  └── YES → Fluent Wait                                          │
│                                                                  │
│  Common conditions:                                             │
│  • Element visible → visibilityOfElementLocated                 │
│  • Element clickable → elementToBeClickable                     │
│  • Loading spinner gone → invisibilityOfElementLocated          │
│  • URL changed → urlContains                                    │
│  • Alert appeared → alertIsPresent                              │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Submission Checklist

| Wait Type | Implemented | Passing |
|-----------|-------------|---------|
| Without wait (failing) | ☐ | N/A |
| Implicit wait | ☐ | ☐ |
| Explicit - visibility | ☐ | ☐ |
| Explicit - clickable | ☐ | ☐ |
| Explicit - text | ☐ | ☐ |
| Explicit - invisibility | ☐ | ☐ |
| Fluent wait | ☐ | ☐ |
| Wait helper class | ☐ | ☐ |

---

## Additional Resources

- Written Content: `waiting-strategies-java.md`
- [ExpectedConditions Javadoc](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/support/ui/ExpectedConditions.html)
- [WebDriverWait Javadoc](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/support/ui/WebDriverWait.html)

