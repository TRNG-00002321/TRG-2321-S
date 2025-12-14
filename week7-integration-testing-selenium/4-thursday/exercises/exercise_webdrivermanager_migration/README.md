# Lab: WebDriverManager Migration

## Overview

**Duration:** 30-45 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Beginner to Intermediate

In this lab, you'll migrate your existing tests from manual driver setup to using WebDriverManager for automatic browser driver management.

---

## Learning Objectives

By completing this lab, you will:
- Understand the benefits of WebDriverManager
- Configure WebDriverManager in Maven
- Replace manual driver setup
- Test across multiple browsers easily
- Handle driver version management automatically

---

## Prerequisites

- Existing Selenium tests with manual driver setup
- Understanding of Maven dependencies
- Working test suite

---

## The Problem with Manual Driver Setup

**Current approach (manual):**
```java
// Must download chromedriver manually
// Must update when Chrome updates
// Must set system property
System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
WebDriver driver = new ChromeDriver();
```

**Problems:**
1. Manual download required
2. Version mismatch errors
3. Different paths on different machines
4. CI/CD configuration complexity

---

## Core Tasks

### Task 1: Add WebDriverManager Dependency (5 minutes)

**Update `pom.xml`:**

```xml
<dependencies>
    <!-- Existing dependencies... -->
    
    <!-- WebDriverManager -->
    <dependency>
        <groupId>io.github.bonigarcia</groupId>
        <artifactId>webdrivermanager</artifactId>
        <version>5.6.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Run `mvn clean install` to download the dependency.

### Task 2: Update BaseTest Class (15 minutes)

**Before (manual setup):**
```java
@BeforeEach
void setUp() {
    System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
    driver = new ChromeDriver();
}
```

**After (WebDriverManager):**
```java
package com.bookhaven.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class BaseTest {
    
    protected WebDriver driver;
    
    @BeforeAll
    static void setupClass() {
        // Setup driver manager once for all tests
        WebDriverManager.chromedriver().setup();
    }
    
    @BeforeEach
    void setUp() {
        // No system property needed!
        driver = new ChromeDriver();
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

### Task 3: Multi-Browser Support (15 minutes)

**Create browser-agnostic test setup:**

```java
package com.bookhaven.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class MultiBrowserTest {
    
    protected WebDriver driver;
    
    void setupBrowser(String browserName) {
        switch (browserName.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }
        driver.manage().window().maximize();
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"chrome", "firefox", "edge"})
    @DisplayName("Test works across browsers")
    void testAcrossBrowsers(String browser) {
        setupBrowser(browser);
        
        driver.get("https://example.com");
        
        assertTrue(driver.getTitle().contains("Example"));
    }
}
```

### Task 4: Environment Variable Configuration (10 minutes)

**Create browser selection from environment:**

```java
public class ConfigurableBaseTest {
    
    protected WebDriver driver;
    
    @BeforeEach
    void setUp() {
        String browser = System.getProperty("browser", "chrome");
        
        driver = createDriver(browser);
        driver.manage().window().maximize();
    }
    
    private WebDriver createDriver(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver();
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver();
            case "edge":
                WebDriverManager.edgedriver().setup();
                return new EdgeDriver();
            default:
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver();
        }
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

**Run with different browsers:**
```bash
# Chrome (default)
mvn test

# Firefox
mvn test -Dbrowser=firefox

# Edge
mvn test -Dbrowser=edge
```

### Task 5: WebDriverManager Features (10 minutes)

**Explore additional features:**

```java
@Test
void testWebDriverManagerFeatures() {
    // Get specific version
    WebDriverManager.chromedriver().driverVersion("119.0.6045.105").setup();
    
    // Cache configuration
    WebDriverManager.chromedriver()
        .cachePath("/custom/path")
        .setup();
    
    // Browser version detection
    WebDriverManager.chromedriver()
        .browserVersion("120")
        .setup();
    
    // Proxy configuration
    WebDriverManager.chromedriver()
        .proxy("proxy.example.com:8080")
        .setup();
    
    // Get driver path (for debugging)
    String driverPath = WebDriverManager.chromedriver().getDownloadedDriverPath();
    System.out.println("Driver path: " + driverPath);
}
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] WebDriverManager dependency added
- [ ] BaseTest updated to use WebDriverManager
- [ ] Tests run without manual driver download
- [ ] Multi-browser support implemented
- [ ] Environment-based browser selection working
- [ ] All existing tests still pass

---

## Migration Checklist

| Step | Before | After | Done |
|------|--------|-------|------|
| Dependency | None | WebDriverManager in pom.xml | ☐ |
| Driver download | Manual | Automatic | ☐ |
| System property | Required | Not needed | ☐ |
| Version matching | Manual | Automatic | ☐ |
| CI/CD setup | Complex | Simple | ☐ |

---

## Common Issues

1. **First run slow:** WebDriverManager downloads driver on first use
2. **Cache location:** Default is `~/.cache/selenium`
3. **Proxy issues:** Configure if behind corporate proxy
4. **Browser not installed:** WebDriverManager manages driver, not browser

---

## Additional Resources

- Written Content: `automated-driver-setup-java.md`
- [WebDriverManager GitHub](https://github.com/bonigarcia/webdrivermanager)
- [WebDriverManager Documentation](https://bonigarcia.dev/webdrivermanager/)

