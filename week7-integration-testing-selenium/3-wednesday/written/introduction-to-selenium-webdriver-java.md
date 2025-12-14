# Getting Started with Selenium WebDriver in Java

## Learning Objectives
- Set up a Selenium WebDriver project using Maven
- Add required Selenium dependencies to your project
- Write and run your first Selenium test
- Understand the WebDriver interface and its key methods
- Navigate the Selenium API structure

## Why This Matters

Building on your understanding of Selenium WebDriver concepts, this lesson puts theory into practice. In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, you'll now create actual browser automation tests using Java.

Setting up your first Selenium project correctly establishes patterns you'll use throughout your career. A well-structured project with proper dependency management makes tests maintainable, shareable, and CI/CD-ready. Let's build that foundation.

## Project Setup with Maven

### Creating the Project Structure

```
selenium-tests/
├── pom.xml
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/example/
│   │           └── pages/          (Page Objects - future)
│   └── test/
│       ├── java/
│       │   └── com/example/
│       │       └── tests/          (Test classes)
│       └── resources/
│           └── drivers/            (Browser drivers)
└── target/                         (Build output)
```

### Maven Project Creation

**Using Maven Archetype:**
```bash
mvn archetype:generate \
    -DgroupId=com.example \
    -DartifactId=selenium-tests \
    -DarchetypeArtifactId=maven-archetype-quickstart \
    -DarchetypeVersion=1.4 \
    -DinteractiveMode=false
```

**Or Create Manually:**
Create the directory structure and add `pom.xml`.

## Adding Selenium Dependencies

### Complete pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>selenium-tests</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <selenium.version>4.15.0</selenium.version>
        <junit.version>5.10.0</junit.version>
    </properties>

    <dependencies>
        <!-- Selenium WebDriver -->
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

        <!-- AssertJ for fluent assertions (optional but recommended) -->
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <version>3.24.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Compiler plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>

            <!-- Surefire plugin for running tests -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.2</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### Dependency Explanation

```
Selenium Dependencies:
┌─────────────────────────────────────────────────────────────────────┐
│ selenium-java                                                        │
│   └── Includes all Selenium components:                             │
│       ├── selenium-api (WebDriver interface)                        │
│       ├── selenium-remote-driver (Remote execution)                 │
│       ├── selenium-chrome-driver (Chrome support)                   │
│       ├── selenium-firefox-driver (Firefox support)                 │
│       ├── selenium-edge-driver (Edge support)                       │
│       ├── selenium-safari-driver (Safari support)                   │
│       └── selenium-support (Wait utilities, etc.)                   │
└─────────────────────────────────────────────────────────────────────┘
```

### Installing Dependencies

```bash
# Navigate to project directory
cd selenium-tests

# Download dependencies
mvn dependency:resolve

# Verify installation
mvn compile
```

## First Selenium Test

### Basic Test Class

```java
package com.example.tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("First Selenium Tests")
class FirstSeleniumTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        // Set path to ChromeDriver (manual setup for now)
        System.setProperty("webdriver.chrome.driver", 
            "src/test/resources/drivers/chromedriver.exe");
        
        // Create new Chrome browser instance
        driver = new ChromeDriver();
    }

    @AfterEach
    void tearDown() {
        // Close browser after each test
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Should navigate to a webpage and verify title")
    void testNavigateAndVerifyTitle() {
        // Navigate to webpage
        driver.get("https://www.selenium.dev/");
        
        // Get page title
        String title = driver.getTitle();
        
        // Verify title contains expected text
        assertTrue(title.contains("Selenium"), 
            "Page title should contain 'Selenium'");
    }

    @Test
    @DisplayName("Should get current URL")
    void testGetCurrentUrl() {
        // Navigate to webpage
        driver.get("https://www.selenium.dev/documentation/");
        
        // Get current URL
        String currentUrl = driver.getCurrentUrl();
        
        // Verify URL
        assertTrue(currentUrl.contains("documentation"),
            "URL should contain 'documentation'");
    }

    @Test
    @DisplayName("Should get page source")
    void testGetPageSource() {
        // Navigate to webpage
        driver.get("https://www.selenium.dev/");
        
        // Get page source (HTML)
        String pageSource = driver.getPageSource();
        
        // Verify page source contains expected content
        assertTrue(pageSource.contains("Selenium"),
            "Page source should contain 'Selenium'");
    }
}
```

### Running the Test

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=FirstSeleniumTest

# Run specific test method
mvn test -Dtest=FirstSeleniumTest#testNavigateAndVerifyTitle

# Run with console output
mvn test -Dtest=FirstSeleniumTest -Dsurefire.useFile=false
```

## Understanding the WebDriver Interface

### WebDriver Interface Hierarchy

```
WebDriver Interface Hierarchy:
┌─────────────────────────────────────────────────────────────────────┐
│                          WebDriver                                   │
│                       (Main Interface)                               │
│   ├── get(String url)                                               │
│   ├── getCurrentUrl()                                               │
│   ├── getTitle()                                                    │
│   ├── findElement(By by)                                            │
│   ├── findElements(By by)                                           │
│   ├── getPageSource()                                               │
│   ├── close()                                                       │
│   ├── quit()                                                        │
│   ├── getWindowHandles()                                            │
│   ├── getWindowHandle()                                             │
│   ├── switchTo()                                                    │
│   ├── navigate()                                                    │
│   └── manage()                                                      │
├─────────────────────────────────────────────────────────────────────┤
│ Implemented By:                                                      │
│   ├── ChromeDriver                                                  │
│   ├── FirefoxDriver                                                 │
│   ├── EdgeDriver                                                    │
│   ├── SafariDriver                                                  │
│   └── RemoteWebDriver                                               │
└─────────────────────────────────────────────────────────────────────┘
```

### Key WebDriver Methods

**Navigation Methods:**
```java
// Navigate to URL
driver.get("https://example.com");

// Get current URL
String url = driver.getCurrentUrl();

// Get page title
String title = driver.getTitle();

// Get page source HTML
String source = driver.getPageSource();
```

**Element Methods:**
```java
// Find single element
WebElement element = driver.findElement(By.id("username"));

// Find multiple elements
List<WebElement> elements = driver.findElements(By.className("item"));
```

**Window Management:**
```java
// Close current window/tab
driver.close();

// Quit browser (closes all windows)
driver.quit();

// Get window handle
String handle = driver.getWindowHandle();

// Get all window handles
Set<String> handles = driver.getWindowHandles();
```

**Navigation Interface:**
```java
// Access navigation methods
driver.navigate().to("https://example.com");
driver.navigate().back();
driver.navigate().forward();
driver.navigate().refresh();
```

**Options Interface (manage()):**
```java
// Window management
driver.manage().window().maximize();
driver.manage().window().minimize();
driver.manage().window().fullscreen();
driver.manage().window().setSize(new Dimension(1024, 768));

// Timeouts
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

// Cookies
driver.manage().getCookies();
driver.manage().addCookie(new Cookie("name", "value"));
driver.manage().deleteAllCookies();
```

**Switch To Interface:**
```java
// Switch to frame
driver.switchTo().frame("frameName");
driver.switchTo().frame(0);  // By index
driver.switchTo().frame(element);  // By WebElement

// Switch back to main content
driver.switchTo().defaultContent();
driver.switchTo().parentFrame();

// Switch to window
driver.switchTo().window(windowHandle);

// Switch to alert
Alert alert = driver.switchTo().alert();
```

## Complete Example Test Suite

```java
package com.example.tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Selenium WebDriver Basics")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeleniumBasicsTest {

    private static WebDriver driver;

    @BeforeAll
    static void setUpOnce() {
        // Configure Chrome driver path
        System.setProperty("webdriver.chrome.driver", 
            "src/test/resources/drivers/chromedriver.exe");
        
        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        // options.addArguments("--headless");  // Run without GUI
        
        // Initialize driver once for all tests
        driver = new ChromeDriver(options);
        
        // Set implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterAll
    static void tearDownOnce() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Navigate to Selenium website")
    void testNavigation() {
        driver.get("https://www.selenium.dev/");
        
        String title = driver.getTitle();
        String url = driver.getCurrentUrl();
        
        assertAll("Page properties",
            () -> assertTrue(title.contains("Selenium"), "Title should contain 'Selenium'"),
            () -> assertTrue(url.contains("selenium.dev"), "URL should contain 'selenium.dev'")
        );
    }

    @Test
    @Order(2)
    @DisplayName("Find element by ID and verify text")
    void testFindElementById() {
        driver.get("https://www.selenium.dev/");
        
        // Find element by tag name (as example)
        WebElement heading = driver.findElement(By.tagName("h1"));
        
        String headingText = heading.getText();
        assertFalse(headingText.isEmpty(), "Heading should not be empty");
    }

    @Test
    @Order(3)
    @DisplayName("Find multiple elements")
    void testFindMultipleElements() {
        driver.get("https://www.selenium.dev/");
        
        // Find all links on the page
        List<WebElement> links = driver.findElements(By.tagName("a"));
        
        assertTrue(links.size() > 0, "Page should have links");
        System.out.println("Found " + links.size() + " links on the page");
    }

    @Test
    @Order(4)
    @DisplayName("Navigate using navigation interface")
    void testNavigationInterface() {
        // Navigate to first page
        driver.get("https://www.selenium.dev/");
        String firstUrl = driver.getCurrentUrl();
        
        // Navigate to documentation
        driver.navigate().to("https://www.selenium.dev/documentation/");
        String secondUrl = driver.getCurrentUrl();
        
        // Go back
        driver.navigate().back();
        assertEquals(firstUrl, driver.getCurrentUrl(), 
            "Should be back at first URL");
        
        // Go forward
        driver.navigate().forward();
        assertEquals(secondUrl, driver.getCurrentUrl(), 
            "Should be at second URL");
    }

    @Test
    @Order(5)
    @DisplayName("Manage window size")
    void testWindowManagement() {
        driver.get("https://www.selenium.dev/");
        
        // Maximize window
        driver.manage().window().maximize();
        
        // Get window size
        var size = driver.manage().window().getSize();
        
        assertTrue(size.getWidth() > 0, "Width should be positive");
        assertTrue(size.getHeight() > 0, "Height should be positive");
        
        System.out.println("Window size: " + size.getWidth() + "x" + size.getHeight());
    }

    @Test
    @Order(6)
    @DisplayName("Get page source")
    void testPageSource() {
        driver.get("https://www.selenium.dev/");
        
        String pageSource = driver.getPageSource();
        
        assertAll("Page source validation",
            () -> assertNotNull(pageSource, "Page source should not be null"),
            () -> assertTrue(pageSource.contains("<html"), "Should contain HTML"),
            () -> assertTrue(pageSource.contains("</html>"), "Should have closing HTML tag")
        );
    }
}
```

## WebDriver API Structure

### Package Organization

```java
// Core WebDriver classes
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

// Browser-specific drivers
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

// Support classes
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

// Interactions
import org.openqa.selenium.interactions.Actions;

// Keys
import org.openqa.selenium.Keys;

// Exceptions
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.StaleElementReferenceException;
```

### Class Relationships

```
WebDriver API Structure:
┌─────────────────────────────────────────────────────────────────────┐
│ org.openqa.selenium                                                  │
│   ├── WebDriver (interface)                                         │
│   │     └── Methods: get, findElement, quit, etc.                   │
│   ├── WebElement (interface)                                        │
│   │     └── Methods: click, sendKeys, getText, etc.                 │
│   ├── By (class)                                                    │
│   │     └── Static methods: id, name, xpath, cssSelector, etc.      │
│   ├── Keys (enum)                                                   │
│   │     └── ENTER, TAB, ESCAPE, CONTROL, etc.                       │
│   └── Alert (interface)                                             │
│         └── Methods: accept, dismiss, getText, sendKeys             │
├─────────────────────────────────────────────────────────────────────┤
│ org.openqa.selenium.chrome                                          │
│   ├── ChromeDriver (class)                                          │
│   └── ChromeOptions (class)                                         │
├─────────────────────────────────────────────────────────────────────┤
│ org.openqa.selenium.support.ui                                      │
│   ├── WebDriverWait (class)                                         │
│   ├── ExpectedConditions (class)                                    │
│   └── Select (class)                                                │
├─────────────────────────────────────────────────────────────────────┤
│ org.openqa.selenium.interactions                                    │
│   └── Actions (class)                                               │
└─────────────────────────────────────────────────────────────────────┘
```

## Common Patterns

### Test Base Class

```java
package com.example.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public abstract class BaseTest {

    protected WebDriver driver;
    protected static final String BASE_URL = "https://example.com";

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver", 
            "src/test/resources/drivers/chromedriver.exe");
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void navigateTo(String path) {
        driver.get(BASE_URL + path);
    }
}
```

### Using the Base Class

```java
package com.example.tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class LoginTest extends BaseTest {

    @Test
    @DisplayName("User can log in with valid credentials")
    void testSuccessfulLogin() {
        navigateTo("/login");
        
        driver.findElement(By.id("username")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.id("login-button")).click();
        
        // Verify successful login
        String welcomeText = driver.findElement(By.id("welcome")).getText();
        assertTrue(welcomeText.contains("Welcome"), 
            "Should display welcome message");
    }
}
```

## Summary

- **Maven project setup** with `selenium-java` dependency provides all necessary components
- The **WebDriver interface** defines browser automation capabilities
- **Browser-specific drivers** (ChromeDriver, FirefoxDriver) implement WebDriver
- Key methods include `get()`, `findElement()`, `quit()`, `navigate()`, and `manage()`
- **Test base classes** encapsulate common setup/teardown logic
- The API is organized into packages for core, browser-specific, support, and interaction classes

In the next lesson, you'll learn about Selenium IDE for recording tests and Selenium Grid for distributed execution.

## Additional Resources

- [Selenium Java Documentation](https://www.selenium.dev/documentation/webdriver/) - Official guide
- [Maven Central - Selenium](https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java) - Latest versions
- [WebDriver JavaDoc](https://www.selenium.dev/selenium/docs/api/java/) - API reference

