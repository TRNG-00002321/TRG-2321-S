# Lab: XPath Mastery - Advanced Locators

## Overview

**Duration:** 45-60 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Advanced

In this lab, you'll master advanced XPath expressions, comparing absolute vs relative paths, and building robust locators for challenging element scenarios.

---

## Learning Objectives

By completing this lab, you will:
- Compare absolute and relative XPath approaches
- Build XPath for complex table scenarios
- Handle dynamic IDs and classes
- Create locators for elements without unique attributes
- Optimize XPath for performance

---

## Prerequisites

- Completed Wednesday XPath practice
- Understanding of XPath functions and axes
- WebDriver project set up

---

## Core Tasks

### Task 1: Absolute vs Relative XPath (15 minutes)

**Create `XPathMasteryTest.java`:**

```java
package com.bookhaven.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XPathMasteryTest extends BaseTest {

    @Test
    @DisplayName("Absolute XPath - fragile but precise")
    void testAbsoluteXPath() {
        driver.get("https://the-internet.herokuapp.com/tables");
        
        // Absolute XPath - full path from root
        // FRAGILE: Any DOM change breaks this
        WebElement cell = driver.findElement(By.xpath(
            "/html/body/div[2]/div/div/table[1]/tbody/tr[1]/td[1]"
        ));
        
        assertEquals("Smith", cell.getText());
        
        // Problem: If dev adds a div wrapper, this breaks!
    }

    @Test
    @DisplayName("Relative XPath - flexible and maintainable")
    void testRelativeXPath() {
        driver.get("https://the-internet.herokuapp.com/tables");
        
        // Relative XPath - starts from anywhere with //
        // ROBUST: Works regardless of parent structure
        WebElement cell = driver.findElement(By.xpath(
            "//table[@id='table1']//tbody/tr[1]/td[1]"
        ));
        
        assertEquals("Smith", cell.getText());
    }

    @Test
    @DisplayName("Compare approaches for same element")
    void testCompareApproaches() {
        driver.get("https://the-internet.herokuapp.com/login");
        
        // Multiple ways to find the same element
        WebElement byId = driver.findElement(By.xpath("//*[@id='username']"));
        WebElement byName = driver.findElement(By.xpath("//input[@name='username']"));
        WebElement byType = driver.findElement(By.xpath("//input[@type='text']"));
        WebElement byLabelFor = driver.findElement(By.xpath(
            "//label[@for='username']/following-sibling::input"
        ));
        
        // All should find the same element
        assertEquals(byId, byName);
        assertEquals(byName, byType);
        assertEquals(byType, byLabelFor);
    }
}
```

### Task 2: Table Navigation (20 minutes)

**Add table tests:**

```java
@Test
@DisplayName("Find cell by row content")
void testFindCellByRowContent() {
    driver.get("https://the-internet.herokuapp.com/tables");
    
    // Find email for person named "Smith"
    WebElement email = driver.findElement(By.xpath(
        "//table[@id='table1']//tr[td[text()='Smith']]/td[3]"
    ));
    
    assertEquals("jsmith@gmail.com", email.getText());
}

@Test
@DisplayName("Find cell by column header")
void testFindCellByColumnHeader() {
    driver.get("https://the-internet.herokuapp.com/tables");
    
    // Find the column index for "Email"
    // Then get the value from first data row
    
    // First, find which column "Email" is in
    List<WebElement> headers = driver.findElements(By.xpath(
        "//table[@id='table1']//thead/tr/th"
    ));
    
    int emailColumnIndex = -1;
    for (int i = 0; i < headers.size(); i++) {
        if (headers.get(i).getText().equals("Email")) {
            emailColumnIndex = i + 1; // XPath is 1-indexed
            break;
        }
    }
    
    // Now get email from first row using that index
    WebElement email = driver.findElement(By.xpath(
        String.format("//table[@id='table1']//tbody/tr[1]/td[%d]", emailColumnIndex)
    ));
    
    assertTrue(email.getText().contains("@"));
}

@Test
@DisplayName("Navigate table with multiple conditions")
void testTableMultipleConditions() {
    driver.get("https://the-internet.herokuapp.com/tables");
    
    // Find action link for user with specific email
    WebElement editLink = driver.findElement(By.xpath(
        "//table[@id='table1']//tr[td[text()='jsmith@gmail.com']]//a[text()='edit']"
    ));
    
    assertTrue(editLink.isDisplayed());
    
    // Find all delete links in table
    List<WebElement> deleteLinks = driver.findElements(By.xpath(
        "//table[@id='table1']//a[text()='delete']"
    ));
    
    assertEquals(4, deleteLinks.size());
}

@Test
@DisplayName("Get all data from a column")
void testGetColumnData() {
    driver.get("https://the-internet.herokuapp.com/tables");
    
    // Get all last names (first column)
    List<WebElement> lastNames = driver.findElements(By.xpath(
        "//table[@id='table1']//tbody/tr/td[1]"
    ));
    
    List<String> names = lastNames.stream()
        .map(WebElement::getText)
        .toList();
    
    assertTrue(names.contains("Smith"));
    assertTrue(names.contains("Doe"));
}
```

### Task 3: Dynamic Elements (15 minutes)

**Add dynamic element tests:**

```java
@Test
@DisplayName("Handle dynamic IDs with contains")
void testDynamicIds() {
    driver.get("https://the-internet.herokuapp.com/challenging_dom");
    
    // IDs might be like "button_12345" where numbers change
    // Use contains or starts-with
    
    List<WebElement> buttons = driver.findElements(By.xpath(
        "//a[contains(@class, 'button')]"
    ));
    
    assertTrue(buttons.size() >= 3);
}

@Test
@DisplayName("Handle elements with multiple classes")
void testMultipleClasses() {
    driver.get("https://the-internet.herokuapp.com/challenging_dom");
    
    // Element has class="button alert"
    // className() only works with single class
    // Use contains for partial class matching
    
    WebElement alertButton = driver.findElement(By.xpath(
        "//a[contains(@class, 'button') and contains(@class, 'alert')]"
    ));
    
    assertTrue(alertButton.isDisplayed());
}

@Test
@DisplayName("Find element without unique attributes")
void testNoUniqueAttributes() {
    driver.get("https://the-internet.herokuapp.com/");
    
    // Find link by visible text when no ID or unique class
    WebElement link = driver.findElement(By.xpath(
        "//a[text()='Checkboxes']"
    ));
    
    assertTrue(link.isDisplayed());
    
    // Find by partial text
    WebElement partialLink = driver.findElement(By.xpath(
        "//a[contains(text(), 'Check')]"
    ));
    
    assertEquals(link, partialLink);
}

@Test
@DisplayName("Find element by neighboring text")
void testFindByNearbyText() {
    driver.get("https://the-internet.herokuapp.com/forgot_password");
    
    // Find input after specific label text
    WebElement emailInput = driver.findElement(By.xpath(
        "//label[contains(text(), 'E-mail')]/following::input[1]"
    ));
    
    assertNotNull(emailInput);
}
```

### Task 4: XPath Optimization (10 minutes)

**Add performance comparison:**

```java
@Test
@DisplayName("XPath performance comparison")
void testXPathPerformance() {
    driver.get("https://the-internet.herokuapp.com/tables");
    
    int iterations = 100;
    
    // Test 1: CSS Selector (usually fastest)
    long cssStart = System.currentTimeMillis();
    for (int i = 0; i < iterations; i++) {
        driver.findElement(By.cssSelector("#table1 tbody tr:first-child td:first-child"));
    }
    long cssTime = System.currentTimeMillis() - cssStart;
    
    // Test 2: Relative XPath
    long relativeStart = System.currentTimeMillis();
    for (int i = 0; i < iterations; i++) {
        driver.findElement(By.xpath("//table[@id='table1']//tbody/tr[1]/td[1]"));
    }
    long relativeTime = System.currentTimeMillis() - relativeStart;
    
    // Test 3: Absolute XPath
    long absoluteStart = System.currentTimeMillis();
    for (int i = 0; i < iterations; i++) {
        driver.findElement(By.xpath("/html/body/div[2]/div/div/table[1]/tbody/tr[1]/td[1]"));
    }
    long absoluteTime = System.currentTimeMillis() - absoluteStart;
    
    System.out.println("CSS Selector: " + cssTime + "ms");
    System.out.println("Relative XPath: " + relativeTime + "ms");
    System.out.println("Absolute XPath: " + absoluteTime + "ms");
    
    // CSS is typically faster than XPath
    // Both should complete in reasonable time
    assertTrue(cssTime < 5000);
    assertTrue(relativeTime < 5000);
}
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Absolute vs relative XPath comparison
- [ ] Table navigation tests
- [ ] Dynamic element handling
- [ ] Element location without unique attributes
- [ ] Performance comparison test
- [ ] All tests passing

---

## XPath Best Practices

```
✓ DO:
  - Use relative XPath (start with //)
  - Use ID or unique attributes when available
  - Use contains() for dynamic/partial values
  - Use descriptive parent/child relationships
  
✗ DON'T:
  - Use absolute XPath in production tests
  - Rely on position alone (//div[3])
  - Use overly complex expressions
  - Ignore CSS selectors (often faster)
```

---

## Submission Checklist

| Item | Completed |
|------|-----------|
| Absolute vs relative comparison | ☐ |
| Table cell by row content | ☐ |
| Table column data extraction | ☐ |
| Dynamic ID handling | ☐ |
| Multiple class handling | ☐ |
| No unique attributes handling | ☐ |
| Performance test | ☐ |

---

## Additional Resources

- Written Content: `absolute-relative-xpath-java.md`
- [XPath Specification](https://www.w3.org/TR/xpath/)

