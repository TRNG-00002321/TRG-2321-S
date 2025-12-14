# Lab: First Selenium WebDriver Test

## Overview

**Duration:** 60-75 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Beginner to Intermediate

In this lab, you'll write your first Selenium WebDriver tests in Java. You'll set up a project, configure browser drivers manually, navigate to web pages, and perform basic interactions.

---

## Learning Objectives

By completing this lab, you will:
- Set up a Selenium WebDriver Maven project
- Configure browser drivers manually
- Navigate to URLs and verify page content
- Find elements and interact with them
- Write basic Selenium test assertions

---

## Prerequisites

- Java JDK 11+ installed
- Maven installed and configured
- Chrome/Firefox browser installed
- IDE (IntelliJ IDEA or Eclipse)
- Understanding of Selenium concepts (from `selenium-webdriver-java.md`)

---

## The Scenario

BookHaven has launched a new web interface. Before API tests, you need to verify the UI works correctly. Your first task is to create a Selenium test suite that validates basic navigation and form functionality.

---

## Core Tasks

### Task 1: Project Setup (15 minutes)

**Create project structure:**

```
selenium-lab/
├── pom.xml
├── drivers/
│   └── chromedriver.exe
└── src/
    └── test/
        └── java/
            └── com/
                └── bookhaven/
                    └── ui/
                        ├── FirstSeleniumTest.java
                        └── BaseTest.java
```

**Create `pom.xml`:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.bookhaven</groupId>
    <artifactId>selenium-lab</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <selenium.version>4.15.0</selenium.version>
        <junit.version>5.10.0</junit.version>
    </properties>

    <dependencies>
        <!-- Selenium -->
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
            <version>${selenium.version}</version>
        </dependency>

        <!-- JUnit 5 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
            </plugin>
        </plugins>
    </build>
</project>
```

**Download ChromeDriver:**
1. Check your Chrome version: `chrome://version`
2. Download matching ChromeDriver from [chromedriver.chromium.org](https://chromedriver.chromium.org/downloads)
3. Extract to `drivers/` folder

### Task 2: Create Base Test Class (10 minutes)

**Create `BaseTest.java`:**

```java
package com.bookhaven.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    void setUp() {
        // Set driver path (adjust to your path)
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
        
        // Initialize driver
        driver = new ChromeDriver();
        
        // Maximize window
        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

### Task 3: First Navigation Test (15 minutes)

**Create `FirstSeleniumTest.java`:**

```java
package com.bookhaven.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

class FirstSeleniumTest extends BaseTest {

    @Test
    @DisplayName("Navigate to Google and verify title")
    void testNavigateToGoogle() {
        // Navigate to URL
        driver.get("https://www.google.com");
        
        // Get page title
        String title = driver.getTitle();
        
        // Verify title
        assertTrue(title.contains("Google"), "Page title should contain 'Google'");
    }

    @Test
    @DisplayName("Navigate to Example.com and verify content")
    void testNavigateToExample() {
        // Navigate
        driver.get("https://example.com");
        
        // Get title and URL
        String title = driver.getTitle();
        String currentUrl = driver.getCurrentUrl();
        
        // Assertions
        assertEquals("Example Domain", title);
        assertTrue(currentUrl.contains("example.com"));
        
        // Find element and verify text
        WebElement heading = driver.findElement(By.tagName("h1"));
        assertEquals("Example Domain", heading.getText());
    }

    @Test
    @DisplayName("Navigate to practice site and find elements")
    void testFindElements() {
        // Navigate to a practice site
        driver.get("https://the-internet.herokuapp.com/");
        
        // Find heading
        WebElement heading = driver.findElement(By.tagName("h1"));
        assertEquals("Welcome to the-internet", heading.getText());
        
        // Find link by link text
        WebElement formAuthLink = driver.findElement(By.linkText("Form Authentication"));
        assertTrue(formAuthLink.isDisplayed());
        
        // Get page source
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("Available Examples"));
    }
}
```

**Run the tests:**
```bash
mvn test
```

**Your Tasks:**
1. Run the tests and verify they pass
2. Add a test that navigates to your favorite website
3. Add a test that verifies the current URL after navigation

### Task 4: Form Interaction Tests (20 minutes)

**Add form tests:**

```java
@Test
@DisplayName("Fill and submit login form")
void testLoginForm() {
    // Navigate to login page
    driver.get("https://the-internet.herokuapp.com/login");
    
    // Find username field and enter text
    WebElement usernameField = driver.findElement(By.id("username"));
    usernameField.sendKeys("tomsmith");
    
    // Find password field and enter text
    WebElement passwordField = driver.findElement(By.id("password"));
    passwordField.sendKeys("SuperSecretPassword!");
    
    // Find and click submit button
    WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
    loginButton.click();
    
    // Verify success message
    WebElement flashMessage = driver.findElement(By.id("flash"));
    assertTrue(flashMessage.getText().contains("You logged into a secure area!"));
}

@Test
@DisplayName("Test invalid login")
void testInvalidLogin() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    // Enter invalid credentials
    driver.findElement(By.id("username")).sendKeys("invalid");
    driver.findElement(By.id("password")).sendKeys("invalid");
    driver.findElement(By.cssSelector("button[type='submit']")).click();
    
    // Verify error message
    WebElement flashMessage = driver.findElement(By.id("flash"));
    assertTrue(flashMessage.getText().contains("Your username is invalid!"));
}

@Test
@DisplayName("Test form clearing")
void testFormClearing() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    WebElement usernameField = driver.findElement(By.id("username"));
    
    // Enter text
    usernameField.sendKeys("some text");
    assertEquals("some text", usernameField.getAttribute("value"));
    
    // Clear field
    usernameField.clear();
    assertEquals("", usernameField.getAttribute("value"));
    
    // Enter new text
    usernameField.sendKeys("new text");
    assertEquals("new text", usernameField.getAttribute("value"));
}
```

**Your Tasks:**
1. Add a test for logout functionality
2. Add a test that verifies the placeholder text in input fields
3. Create a test that uses the Enter key instead of clicking the button

### Task 5: Click and Navigation Tests (15 minutes)

**Add interaction tests:**

```java
@Test
@DisplayName("Test link clicking and navigation")
void testLinkClicking() {
    driver.get("https://the-internet.herokuapp.com/");
    
    // Click a link
    driver.findElement(By.linkText("Checkboxes")).click();
    
    // Verify navigation
    assertTrue(driver.getCurrentUrl().contains("checkboxes"));
    
    // Go back
    driver.navigate().back();
    
    // Verify we're back
    assertTrue(driver.getCurrentUrl().equals("https://the-internet.herokuapp.com/"));
}

@Test
@DisplayName("Test checkbox interactions")
void testCheckboxes() {
    driver.get("https://the-internet.herokuapp.com/checkboxes");
    
    // Find checkboxes
    java.util.List<WebElement> checkboxes = driver.findElements(By.cssSelector("input[type='checkbox']"));
    
    assertEquals(2, checkboxes.size(), "Should find 2 checkboxes");
    
    // Check states
    WebElement checkbox1 = checkboxes.get(0);
    WebElement checkbox2 = checkboxes.get(1);
    
    // First checkbox - initially unchecked
    assertFalse(checkbox1.isSelected());
    
    // Click to check
    checkbox1.click();
    assertTrue(checkbox1.isSelected());
    
    // Click to uncheck
    checkbox1.click();
    assertFalse(checkbox1.isSelected());
}

@Test
@DisplayName("Test getting element attributes")
void testGetAttributes() {
    driver.get("https://the-internet.herokuapp.com/login");
    
    WebElement usernameField = driver.findElement(By.id("username"));
    
    // Get various attributes
    String id = usernameField.getAttribute("id");
    String type = usernameField.getAttribute("type");
    String name = usernameField.getAttribute("name");
    
    assertEquals("username", id);
    assertEquals("text", type);
    assertEquals("username", name);
    
    // Check if element is enabled and displayed
    assertTrue(usernameField.isEnabled());
    assertTrue(usernameField.isDisplayed());
}
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Maven project compiles without errors
- [ ] ChromeDriver configured and working
- [ ] Navigation tests passing
- [ ] Form interaction tests passing
- [ ] Click tests passing
- [ ] Checkbox tests passing
- [ ] All tests pass with `mvn test`
- [ ] Browser opens and closes cleanly

---

## Starter Code

Find complete starter code in the `starter_code/` directory.

---

## Challenge Tasks (Optional)

### 1. Test Firefox Browser
```java
System.setProperty("webdriver.gecko.driver", "drivers/geckodriver.exe");
WebDriver firefoxDriver = new FirefoxDriver();
```

### 2. Test Multiple Browsers
```java
@ParameterizedTest
@ValueSource(strings = {"chrome", "firefox"})
void testMultipleBrowsers(String browser) {
    // Create driver based on browser parameter
}
```

### 3. Take Screenshots
```java
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
```

---

## Submission Checklist

| Test | Implemented | Passing |
|------|-------------|---------|
| Navigate and verify title | ☐ | ☐ |
| Navigate and verify content | ☐ | ☐ |
| Find elements | ☐ | ☐ |
| Login form | ☐ | ☐ |
| Invalid login | ☐ | ☐ |
| Form clearing | ☐ | ☐ |
| Link clicking | ☐ | ☐ |
| Checkboxes | ☐ | ☐ |
| Get attributes | ☐ | ☐ |

---

## Common Issues

1. **Driver not found:** Verify path in `System.setProperty()`
2. **Version mismatch:** Ensure ChromeDriver matches Chrome version
3. **Element not found:** Check locator or wait for element
4. **Browser doesn't close:** Ensure `driver.quit()` is called

---

## Additional Resources

- Written Content: `selenium-webdriver-java.md`, `introduction-to-selenium-webdriver-java.md`, `manual-driver-setup-java.md`
- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [The Internet - Practice Site](https://the-internet.herokuapp.com/)

