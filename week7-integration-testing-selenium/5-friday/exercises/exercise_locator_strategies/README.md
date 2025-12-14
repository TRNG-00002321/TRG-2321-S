# Lab: Locator Strategy Selection Guide

## Overview

**Duration:** 45-60 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll practice selecting the best locator strategy for various element scenarios. You'll build a decision-making framework for choosing locators.

---

## Learning Objectives

By completing this lab, you will:
- Compare all Selenium locator strategies
- Choose optimal locators for different scenarios
- Build maintainable, resilient locators
- Understand performance implications
- Create a personal locator selection guide

---

## Prerequisites

- Completed XPath and form interaction exercises
- Understanding of all By class strategies
- WebDriver project set up

---

## Core Tasks

### Task 1: Locator Strategy Comparison (20 minutes)

**Create `LocatorStrategyTest.java`:**

```java
package com.bookhaven.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocatorStrategyTest extends BaseTest {

    @Test
    @DisplayName("Find same element with all strategies")
    void testAllStrategiesForSameElement() {
        driver.get("https://the-internet.herokuapp.com/login");
        
        // Target: Username input field
        // All these find the SAME element
        
        WebElement byId = driver.findElement(By.id("username"));
        WebElement byName = driver.findElement(By.name("username"));
        WebElement byCss = driver.findElement(By.cssSelector("#username"));
        WebElement byXpath = driver.findElement(By.xpath("//input[@id='username']"));
        WebElement byTagAndAttr = driver.findElement(By.cssSelector("input[type='text']"));
        
        // Verify all found the same element
        assertEquals(byId.getAttribute("id"), byName.getAttribute("id"));
        assertEquals(byName.getAttribute("id"), byCss.getAttribute("id"));
        assertEquals(byCss.getAttribute("id"), byXpath.getAttribute("id"));
        
        // Preference order for this element:
        // 1. By.id - fastest, unique
        // 2. By.name - fast, semantic
        // 3. By.cssSelector - flexible
        // 4. By.xpath - powerful but slower
    }

    @Test
    @DisplayName("By.id - fastest and most reliable")
    void testByIdStrategy() {
        driver.get("https://the-internet.herokuapp.com/login");
        
        // BEST for: Elements with unique IDs
        WebElement username = driver.findElement(By.id("username"));
        WebElement password = driver.findElement(By.id("password"));
        
        assertNotNull(username);
        assertNotNull(password);
        
        // Why preferred:
        // - Fastest lookup (browser optimization)
        // - IDs should be unique per HTML spec
        // - Clear intent in code
    }

    @Test
    @DisplayName("By.name - good for form fields")
    void testByNameStrategy() {
        driver.get("https://the-internet.herokuapp.com/login");
        
        // BEST for: Form inputs that share name (e.g., radio buttons)
        WebElement username = driver.findElement(By.name("username"));
        
        assertNotNull(username);
        
        // Caution: Names aren't always unique
        // Good for form elements where name is required
    }

    @Test
    @DisplayName("By.className - use carefully")
    void testByClassNameStrategy() {
        driver.get("https://the-internet.herokuapp.com/login");
        
        // By.className only accepts ONE class
        WebElement button = driver.findElement(By.className("radius"));
        
        assertNotNull(button);
        
        // WRONG: Multiple classes will fail
        // driver.findElement(By.className("btn primary")); // Error!
        
        // Use CSS for multiple classes:
        WebElement multiClass = driver.findElement(By.cssSelector(".fa.fa-2x"));
    }

    @Test
    @DisplayName("By.linkText and By.partialLinkText")
    void testLinkTextStrategies() {
        driver.get("https://the-internet.herokuapp.com/");
        
        // Exact match
        WebElement exactLink = driver.findElement(By.linkText("Form Authentication"));
        assertNotNull(exactLink);
        
        // Partial match
        WebElement partialLink = driver.findElement(By.partialLinkText("Form"));
        assertNotNull(partialLink);
        
        // Same element
        assertEquals(exactLink, partialLink);
        
        // BEST for: Navigation links with meaningful text
        // Caution: Case-sensitive, only for <a> tags
    }

    @Test
    @DisplayName("By.tagName - for collections")
    void testTagNameStrategy() {
        driver.get("https://the-internet.herokuapp.com/");
        
        // Get all links on page
        List<WebElement> allLinks = driver.findElements(By.tagName("a"));
        assertTrue(allLinks.size() > 10);
        
        // Get all list items
        List<WebElement> listItems = driver.findElements(By.tagName("li"));
        
        // BEST for: Counting elements, iterating collections
        // Rarely useful for finding single specific element
    }

    @Test
    @DisplayName("By.cssSelector - versatile choice")
    void testCssSelectorStrategy() {
        driver.get("https://the-internet.herokuapp.com/login");
        
        // Various CSS selector patterns
        WebElement byId = driver.findElement(By.cssSelector("#username"));
        WebElement byClass = driver.findElement(By.cssSelector(".radius"));
        WebElement byAttr = driver.findElement(By.cssSelector("[type='submit']"));
        WebElement byMultiClass = driver.findElement(By.cssSelector("button.radius"));
        WebElement byChild = driver.findElement(By.cssSelector("form > div"));
        WebElement byNth = driver.findElement(By.cssSelector("form div:nth-child(1)"));
        
        // BEST for: Complex element selection when ID unavailable
        // Faster than XPath, highly flexible
    }

    @Test
    @DisplayName("By.xpath - most powerful, use wisely")
    void testXPathStrategy() {
        driver.get("https://the-internet.herokuapp.com/login");
        
        // XPath unique capabilities
        
        // 1. Find by text content
        WebElement byText = driver.findElement(By.xpath("//button[text()=' Login']"));
        
        // 2. Navigate to parent
        WebElement parent = driver.findElement(By.xpath("//input[@id='username']/.."));
        
        // 3. Find by sibling relationship
        WebElement sibling = driver.findElement(By.xpath(
            "//label[@for='username']/following-sibling::input"
        ));
        
        // 4. Complex conditions
        WebElement complex = driver.findElement(By.xpath(
            "//input[@type='text' and @id='username']"
        ));
        
        // BEST for: Complex scenarios CSS can't handle
        // Use relative XPath, avoid absolute paths
    }
}
```

### Task 2: Locator Decision Framework (15 minutes)

**Add decision tests:**

```java
@Test
@DisplayName("Decision: Element has unique ID")
void testDecisionUniqueId() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    // DECISION: Always use By.id when available
    WebElement element = driver.findElement(By.id("username"));
    
    // Why: Fastest, most reliable
}

@Test
@DisplayName("Decision: Element is a link")
void testDecisionLink() {
    driver.get("https://the-internet.herokuapp.com/");
    
    // DECISION: Use linkText for navigation links
    WebElement link = driver.findElement(By.linkText("Checkboxes"));
    
    // Why: Readable, semantic, verifies link text
}

@Test
@DisplayName("Decision: Need to find by text content")
void testDecisionTextContent() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    // DECISION: Use XPath for text-based location
    WebElement byText = driver.findElement(By.xpath("//*[contains(text(), 'Login')]"));
    
    // CSS can't match text content
}

@Test
@DisplayName("Decision: Element has no unique attributes")
void testDecisionNoUniqueAttrs() {
    driver.get("https://the-internet.herokuapp.com/challenging_dom");
    
    // DECISION: Use structural CSS or XPath
    // Option 1: CSS with position
    WebElement byCss = driver.findElement(By.cssSelector("table tbody tr:first-child td:first-child"));
    
    // Option 2: XPath with relationship
    WebElement byXpath = driver.findElement(By.xpath("//table//tr[1]/td[1]"));
    
    // Why: When no ID/class, rely on DOM structure
}

@Test
@DisplayName("Decision: Multiple classes on element")
void testDecisionMultipleClasses() {
    driver.get("https://the-internet.herokuapp.com/challenging_dom");
    
    // DECISION: Use CSS with multiple class selectors
    List<WebElement> elements = driver.findElements(By.cssSelector(".button.alert"));
    
    // NOT: By.className("button alert") - this fails
}

@Test  
@DisplayName("Decision: Dynamic ID pattern")
void testDecisionDynamicId() {
    driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");
    
    // DECISION: Use contains() or starts-with() for partial match
    // CSS: [id*='partial'], [id^='start']
    // XPath: contains(@id, 'partial'), starts-with(@id, 'start')
    
    WebElement element = driver.findElement(By.cssSelector("[id^='start']"));
    assertNotNull(element);
}
```

### Task 3: Performance Testing (10 minutes)

**Add performance comparison:**

```java
@Test
@DisplayName("Compare locator performance")
void testLocatorPerformance() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    int iterations = 50;
    
    // By.id
    long idStart = System.currentTimeMillis();
    for (int i = 0; i < iterations; i++) {
        driver.findElement(By.id("username"));
    }
    long idTime = System.currentTimeMillis() - idStart;
    
    // By.name  
    long nameStart = System.currentTimeMillis();
    for (int i = 0; i < iterations; i++) {
        driver.findElement(By.name("username"));
    }
    long nameTime = System.currentTimeMillis() - nameStart;
    
    // By.cssSelector
    long cssStart = System.currentTimeMillis();
    for (int i = 0; i < iterations; i++) {
        driver.findElement(By.cssSelector("#username"));
    }
    long cssTime = System.currentTimeMillis() - cssStart;
    
    // By.xpath (relative)
    long xpathStart = System.currentTimeMillis();
    for (int i = 0; i < iterations; i++) {
        driver.findElement(By.xpath("//input[@id='username']"));
    }
    long xpathTime = System.currentTimeMillis() - xpathStart;
    
    System.out.println("Performance for " + iterations + " iterations:");
    System.out.println("By.id: " + idTime + "ms");
    System.out.println("By.name: " + nameTime + "ms");
    System.out.println("By.cssSelector: " + cssTime + "ms");
    System.out.println("By.xpath: " + xpathTime + "ms");
    
    // ID should generally be fastest
    // All should complete in reasonable time
}
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Tested all locator strategies
- [ ] Documented decision criteria
- [ ] Completed performance comparison
- [ ] Created personal decision guide
- [ ] All tests passing

---

## Locator Strategy Decision Guide

```
┌────────────────────────────────────────────────────────────────┐
│                 LOCATOR STRATEGY DECISION TREE                  │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Does element have unique ID?                                  │
│  ├── YES → Use By.id()                                         │
│  └── NO  ↓                                                     │
│                                                                 │
│  Is it a link (<a>) with known text?                          │
│  ├── YES → Use By.linkText() or By.partialLinkText()          │
│  └── NO  ↓                                                     │
│                                                                 │
│  Does it have unique name attribute?                          │
│  ├── YES → Use By.name()                                       │
│  └── NO  ↓                                                     │
│                                                                 │
│  Can you locate with CSS (class, attribute, structure)?       │
│  ├── YES → Use By.cssSelector()                                │
│  └── NO  ↓                                                     │
│                                                                 │
│  Need to match text content or navigate to parent?            │
│  ├── YES → Use By.xpath()                                      │
│  └── NO  ↓                                                     │
│                                                                 │
│  Counting/collecting elements of same type?                   │
│  └── YES → Use By.tagName() with findElements()               │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
```

---

## Submission Checklist

| Strategy | Tested | Use Case Documented |
|----------|--------|---------------------|
| By.id | ☐ | ☐ |
| By.name | ☐ | ☐ |
| By.className | ☐ | ☐ |
| By.linkText | ☐ | ☐ |
| By.partialLinkText | ☐ | ☐ |
| By.tagName | ☐ | ☐ |
| By.cssSelector | ☐ | ☐ |
| By.xpath | ☐ | ☐ |

---

## Additional Resources

- Written Content: `find-methods-java.md`
- [Selenium Locators Guide](https://www.selenium.dev/documentation/webdriver/elements/locators/)

