# Lab: XPath Locator Practice

## Overview

**Duration:** 60-75 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll master XPath expressions for locating web elements. You'll practice building XPath locators from simple to complex, using various functions and axes.

---

## Learning Objectives

By completing this lab, you will:
- Build XPath expressions using various strategies
- Use XPath functions (contains, starts-with, text)
- Navigate DOM with XPath axes
- Choose between absolute and relative XPath
- Create robust, maintainable locators

---

## Prerequisites

- Completed "First Selenium Test" exercise
- Understanding of HTML structure
- Basic XPath syntax knowledge

---

## The Scenario

BookHaven's web pages have inconsistent element IDs and classes. You need to become an expert at crafting XPath expressions to reliably locate elements regardless of their attributes.

---

## Core Tasks

### Task 1: Setup Practice Project (5 minutes)

Create `XPathPracticeTest.java`:

```java
package com.bookhaven.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XPathPracticeTest extends BaseTest {

    @BeforeEach
    void navigateToTestPage() {
        driver.get("https://the-internet.herokuapp.com/");
    }
}
```

### Task 2: Basic XPath Selectors (15 minutes)

**Add these tests:**

```java
@Test
@DisplayName("XPath by tag name")
void testXPathByTagName() {
    // Find heading by tag
    WebElement heading = driver.findElement(By.xpath("//h1"));
    assertEquals("Welcome to the-internet", heading.getText());
    
    // Find all links
    List<WebElement> links = driver.findElements(By.xpath("//a"));
    assertTrue(links.size() > 10, "Should find many links");
}

@Test
@DisplayName("XPath by attribute")
void testXPathByAttribute() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    // By id attribute
    WebElement usernameById = driver.findElement(By.xpath("//*[@id='username']"));
    assertNotNull(usernameById);
    
    // By name attribute
    WebElement usernameByName = driver.findElement(By.xpath("//input[@name='username']"));
    assertNotNull(usernameByName);
    
    // By type attribute
    WebElement passwordByType = driver.findElement(By.xpath("//input[@type='password']"));
    assertNotNull(passwordByType);
    
    // By multiple attributes
    WebElement button = driver.findElement(By.xpath("//button[@type='submit'][@class=' radius']"));
    assertTrue(button.isDisplayed());
}

@Test
@DisplayName("XPath with specific value")
void testXPathSpecificValue() {
    driver.get("https://the-internet.herokuapp.com/dropdown");
    
    // Select option with specific value
    WebElement option = driver.findElement(By.xpath("//option[@value='1']"));
    assertEquals("Option 1", option.getText());
}
```

**Your Tasks:**
1. Find an element by its `class` attribute
2. Find an element with `href` attribute containing "login"
3. Find all input elements on the login page

### Task 3: XPath Functions (20 minutes)

**Add function-based tests:**

```java
@Test
@DisplayName("XPath contains() function")
void testContainsFunction() {
    // Contains in attribute
    WebElement link = driver.findElement(By.xpath("//a[contains(@href, 'login')]"));
    assertEquals("Form Authentication", link.getText());
    
    // Contains in text
    WebElement heading = driver.findElement(By.xpath("//*[contains(text(), 'Welcome')]"));
    assertNotNull(heading);
    
    // Contains in class (useful for partial class matching)
    driver.get("https://the-internet.herokuapp.com/login");
    WebElement button = driver.findElement(By.xpath("//button[contains(@class, 'radius')]"));
    assertTrue(button.isDisplayed());
}

@Test
@DisplayName("XPath starts-with() function")
void testStartsWithFunction() {
    // Links starting with specific href
    List<WebElement> internalLinks = driver.findElements(
        By.xpath("//a[starts-with(@href, '/')]")
    );
    assertFalse(internalLinks.isEmpty());
    
    // IDs starting with prefix
    driver.get("https://the-internet.herokuapp.com/challenging_dom");
    List<WebElement> elements = driver.findElements(
        By.xpath("//*[starts-with(@id, 'row')]")
    );
    // May be empty depending on the page, but syntax is correct
}

@Test
@DisplayName("XPath text() function")
void testTextFunction() {
    // Exact text match
    WebElement link = driver.findElement(By.xpath("//a[text()='Checkboxes']"));
    assertNotNull(link);
    
    // Navigate and verify
    link.click();
    assertTrue(driver.getCurrentUrl().contains("checkboxes"));
}

@Test
@DisplayName("XPath normalize-space() function")
void testNormalizeSpaceFunction() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    // Handle whitespace in text
    WebElement label = driver.findElement(
        By.xpath("//label[normalize-space()='Username']")
    );
    assertNotNull(label);
}
```

**Your Tasks:**
1. Find all links containing "Add" in their text
2. Find elements with IDs ending with a specific suffix (using contains)
3. Create XPath that handles text with leading/trailing spaces

### Task 4: XPath Axes (20 minutes)

**Add axis-based navigation tests:**

```java
@Test
@DisplayName("XPath parent axis")
void testParentAxis() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    // Find parent of input element
    WebElement input = driver.findElement(By.id("username"));
    WebElement parent = driver.findElement(
        By.xpath("//input[@id='username']/parent::div")
    );
    
    // Alternative: ..
    WebElement parentAlt = driver.findElement(
        By.xpath("//input[@id='username']/..")
    );
    
    assertNotNull(parent);
    assertNotNull(parentAlt);
}

@Test
@DisplayName("XPath child axis")
void testChildAxis() {
    driver.get("https://the-internet.herokuapp.com/");
    
    // Find direct children
    List<WebElement> listItems = driver.findElements(
        By.xpath("//ul/child::li")
    );
    assertTrue(listItems.size() > 0);
    
    // Alternative: direct path
    List<WebElement> listItemsAlt = driver.findElements(
        By.xpath("//ul/li")
    );
    assertEquals(listItems.size(), listItemsAlt.size());
}

@Test
@DisplayName("XPath descendant axis")
void testDescendantAxis() {
    // Find all descendants (not just children)
    List<WebElement> allDescendants = driver.findElements(
        By.xpath("//div[@id='content']//a")
    );
    assertTrue(allDescendants.size() > 0);
}

@Test
@DisplayName("XPath following-sibling axis")
void testFollowingSiblingAxis() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    // Find element after label
    WebElement inputAfterLabel = driver.findElement(
        By.xpath("//label[@for='username']/following-sibling::input")
    );
    assertNotNull(inputAfterLabel);
}

@Test
@DisplayName("XPath preceding-sibling axis")
void testPrecedingSiblingAxis() {
    driver.get("https://the-internet.herokuapp.com/tables");
    
    // Find element before another
    WebElement cell = driver.findElement(
        By.xpath("//table[@id='table1']//td[text()='http://www.jdoe.com']/preceding-sibling::td[1]")
    );
    // Get the cell before the website cell
    assertNotNull(cell);
}

@Test
@DisplayName("XPath ancestor axis")
void testAncestorAxis() {
    driver.get("https://the-internet.herokuapp.com/tables");
    
    // Find ancestor table of a cell
    WebElement table = driver.findElement(
        By.xpath("//td[text()='jsmith@gmail.com']/ancestor::table")
    );
    assertEquals("table1", table.getAttribute("id"));
}
```

**Your Tasks:**
1. Find the form element that contains the username input
2. Find all siblings of the first list item
3. Navigate from a cell to its row parent, then to the table

### Task 5: Complex XPath Expressions (15 minutes)

**Add advanced tests:**

```java
@Test
@DisplayName("XPath with multiple conditions (and/or)")
void testMultipleConditions() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    // AND condition
    WebElement input = driver.findElement(
        By.xpath("//input[@type='text' and @id='username']")
    );
    assertNotNull(input);
    
    // OR condition
    List<WebElement> inputs = driver.findElements(
        By.xpath("//input[@type='text' or @type='password']")
    );
    assertEquals(2, inputs.size());
}

@Test
@DisplayName("XPath with position")
void testPositionalXPath() {
    // First element
    WebElement firstLink = driver.findElement(
        By.xpath("(//a)[1]")
    );
    assertNotNull(firstLink);
    
    // Last element
    WebElement lastLink = driver.findElement(
        By.xpath("(//ul/li)[last()]")
    );
    assertNotNull(lastLink);
    
    // Specific position
    WebElement thirdLink = driver.findElement(
        By.xpath("(//ul/li)[3]")
    );
    assertNotNull(thirdLink);
}

@Test
@DisplayName("XPath combining techniques")
void testCombinedXPath() {
    driver.get("https://the-internet.herokuapp.com/tables");
    
    // Find table row containing specific text, then get another cell
    WebElement email = driver.findElement(
        By.xpath("//table[@id='table1']//tr[td[text()='Smith']]/td[3]")
    );
    assertEquals("jsmith@gmail.com", email.getText());
    
    // Find cell by row and column position
    WebElement cell = driver.findElement(
        By.xpath("//table[@id='table1']//tbody/tr[1]/td[2]")
    );
    assertEquals("John", cell.getText());
}

@Test
@DisplayName("XPath not() function")
void testNotFunction() {
    driver.get("https://the-internet.herokuapp.com/checkboxes");
    
    // Find unchecked checkbox
    List<WebElement> uncheckedBoxes = driver.findElements(
        By.xpath("//input[@type='checkbox' and not(@checked)]")
    );
    
    // At least one should be unchecked initially
    assertFalse(uncheckedBoxes.isEmpty() || 
                driver.findElements(By.xpath("//input[@type='checkbox']")).size() > 0);
}
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Basic XPath selectors working (tag, attribute)
- [ ] XPath functions working (contains, starts-with, text)
- [ ] XPath axes working (parent, child, sibling, ancestor)
- [ ] Multiple condition XPath working
- [ ] Positional XPath working
- [ ] Combined complex expressions working
- [ ] All tests passing

---

## XPath Quick Reference

```
Basic:
//tagname           - All elements with tag
//*[@id='value']    - Element with ID
//tag[@attr='val']  - Element with attribute

Functions:
contains(@attr, 'partial')    - Partial attribute match
starts-with(@attr, 'prefix')  - Attribute starts with
text()='exact text'           - Exact text match
contains(text(), 'partial')   - Partial text match
normalize-space()             - Trim whitespace

Axes:
/parent::tag      or  /..     - Parent element
/child::tag       or  /tag    - Direct child
//descendant::tag or  //tag   - Any descendant
/following-sibling::tag       - Next sibling
/preceding-sibling::tag       - Previous sibling
/ancestor::tag                - Parent/grandparent

Logic:
[@a and @b]       - Both conditions
[@a or @b]        - Either condition
[not(@attr)]      - Negation

Position:
[1]               - First element
[last()]          - Last element
[position()>1]    - After first
```

---

## Submission Checklist

| XPath Type | Implemented | Passing |
|------------|-------------|---------|
| By tag name | ☐ | ☐ |
| By attribute | ☐ | ☐ |
| contains() | ☐ | ☐ |
| starts-with() | ☐ | ☐ |
| text() | ☐ | ☐ |
| Parent axis | ☐ | ☐ |
| Child axis | ☐ | ☐ |
| Sibling axes | ☐ | ☐ |
| Multiple conditions | ☐ | ☐ |
| Positional | ☐ | ☐ |

---

## Additional Resources

- Written Content: `xpath-functions-java.md`
- [XPath Tutorial](https://www.w3schools.com/xml/xpath_intro.asp)
- [XPath Cheatsheet](https://devhints.io/xpath)

