# Lab: Browser Options and Configurations

## Overview

**Duration:** 45-60 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll configure browser options to customize Selenium test execution. You'll run tests in headless mode, set custom arguments, and manage browser capabilities.

---

## Learning Objectives

By completing this lab, you will:
- Configure Chrome, Firefox, and Edge options
- Run browsers in headless mode
- Set custom browser arguments
- Manage browser capabilities
- Configure for different test scenarios

---

## Prerequisites

- WebDriverManager setup complete
- Understanding of browser options
- Selenium project running

---

## Core Tasks

### Task 1: Chrome Options (15 minutes)

**Create `BrowserConfigTest.java`:**

```java
package com.bookhaven.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.*;

class BrowserConfigTest {

    private WebDriver driver;

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Chrome with basic options")
    void testChromeBasicOptions() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        
        // Common useful options
        options.addArguments("--start-maximized");      // Start maximized
        options.addArguments("--disable-extensions");   // Disable extensions
        options.addArguments("--disable-popup-blocking"); // Allow popups
        options.addArguments("--disable-infobars");     // Disable info bars
        
        driver = new ChromeDriver(options);
        driver.get("https://example.com");
        
        assertTrue(driver.getTitle().contains("Example"));
    }

    @Test
    @DisplayName("Chrome headless mode")
    void testChromeHeadless() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");  // New headless mode (Chrome 109+)
        options.addArguments("--window-size=1920,1080");  // Set window size
        options.addArguments("--disable-gpu");    // Recommended for headless
        
        driver = new ChromeDriver(options);
        driver.get("https://example.com");
        
        // Test runs without visible browser!
        assertEquals("Example Domain", driver.getTitle());
        
        // Verify we can still interact
        assertNotNull(driver.getPageSource());
    }

    @Test
    @DisplayName("Chrome with custom user agent")
    void testChromeCustomUserAgent() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
            "--user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)"
        );
        
        driver = new ChromeDriver(options);
        driver.get("https://www.whatismybrowser.com/detect/what-is-my-user-agent");
        
        // Site will detect mobile user agent
    }

    @Test
    @DisplayName("Chrome with download directory")
    void testChromeDownloadDirectory() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        
        java.util.Map<String, Object> prefs = new java.util.HashMap<>();
        prefs.put("download.default_directory", "C:/Downloads/test");
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        
        options.setExperimentalOption("prefs", prefs);
        
        driver = new ChromeDriver(options);
        // Downloads will go to specified directory
    }

    @Test
    @DisplayName("Chrome incognito mode")
    void testChromeIncognito() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        
        driver = new ChromeDriver(options);
        driver.get("https://example.com");
        
        // Browser runs in incognito mode
        assertTrue(driver.getTitle().contains("Example"));
    }
}
```

### Task 2: Firefox Options (15 minutes)

**Add Firefox tests:**

```java
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

@Test
@DisplayName("Firefox with options")
void testFirefoxOptions() {
    WebDriverManager.firefoxdriver().setup();
    
    FirefoxOptions options = new FirefoxOptions();
    
    // Common options
    options.addArguments("-width=1920");
    options.addArguments("-height=1080");
    
    driver = new FirefoxDriver(options);
    driver.get("https://example.com");
    
    assertTrue(driver.getTitle().contains("Example"));
}

@Test
@DisplayName("Firefox headless mode")
void testFirefoxHeadless() {
    WebDriverManager.firefoxdriver().setup();
    
    FirefoxOptions options = new FirefoxOptions();
    options.addArguments("-headless");
    
    driver = new FirefoxDriver(options);
    driver.get("https://example.com");
    
    assertEquals("Example Domain", driver.getTitle());
}

@Test
@DisplayName("Firefox with custom profile")
void testFirefoxProfile() {
    WebDriverManager.firefoxdriver().setup();
    
    FirefoxProfile profile = new FirefoxProfile();
    
    // Disable images for faster loading
    profile.setPreference("permissions.default.image", 2);
    
    // Set download behavior
    profile.setPreference("browser.download.folderList", 2);
    profile.setPreference("browser.download.dir", "C:/Downloads/test");
    profile.setPreference("browser.helperApps.neverAsk.saveToDisk", 
        "application/pdf,application/zip");
    
    FirefoxOptions options = new FirefoxOptions();
    options.setProfile(profile);
    
    driver = new FirefoxDriver(options);
    driver.get("https://example.com");
}

@Test
@DisplayName("Firefox private browsing")
void testFirefoxPrivate() {
    WebDriverManager.firefoxdriver().setup();
    
    FirefoxOptions options = new FirefoxOptions();
    options.addArguments("-private");
    
    driver = new FirefoxDriver(options);
    driver.get("https://example.com");
}
```

### Task 3: Edge Options (10 minutes)

**Add Edge tests:**

```java
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

@Test
@DisplayName("Edge with options")
void testEdgeOptions() {
    WebDriverManager.edgedriver().setup();
    
    EdgeOptions options = new EdgeOptions();
    options.addArguments("--start-maximized");
    options.addArguments("--inprivate");  // InPrivate mode
    
    driver = new EdgeDriver(options);
    driver.get("https://example.com");
    
    assertTrue(driver.getTitle().contains("Example"));
}

@Test
@DisplayName("Edge headless mode")
void testEdgeHeadless() {
    WebDriverManager.edgedriver().setup();
    
    EdgeOptions options = new EdgeOptions();
    options.addArguments("--headless=new");
    options.addArguments("--window-size=1920,1080");
    
    driver = new EdgeDriver(options);
    driver.get("https://example.com");
    
    assertEquals("Example Domain", driver.getTitle());
}
```

### Task 4: Configurable Test Base (15 minutes)

**Create production-ready configuration:**

```java
package com.bookhaven.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class DriverFactory {
    
    private static final boolean HEADLESS = 
        Boolean.parseBoolean(System.getProperty("headless", "false"));
    
    public static WebDriver createDriver(String browserName) {
        WebDriver driver;
        
        switch (browserName.toLowerCase()) {
            case "chrome":
                driver = createChromeDriver();
                break;
            case "firefox":
                driver = createFirefoxDriver();
                break;
            case "edge":
                driver = createEdgeDriver();
                break;
            default:
                driver = createChromeDriver();
        }
        
        return driver;
    }
    
    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        
        if (HEADLESS) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
        }
        
        return new ChromeDriver(options);
    }
    
    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        
        FirefoxOptions options = new FirefoxOptions();
        
        if (HEADLESS) {
            options.addArguments("-headless");
            options.addArguments("-width=1920");
            options.addArguments("-height=1080");
        }
        
        return new FirefoxDriver(options);
    }
    
    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        
        if (HEADLESS) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        
        return new EdgeDriver(options);
    }
}

// Usage in BaseTest:
public class BaseTest {
    protected WebDriver driver;
    
    @BeforeEach
    void setUp() {
        String browser = System.getProperty("browser", "chrome");
        driver = DriverFactory.createDriver(browser);
    }
}
```

**Run tests:**
```bash
# Chrome GUI
mvn test

# Chrome headless
mvn test -Dheadless=true

# Firefox headless
mvn test -Dbrowser=firefox -Dheadless=true
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Chrome options configured
- [ ] Chrome headless working
- [ ] Firefox options configured
- [ ] Firefox headless working
- [ ] Edge options configured
- [ ] Configurable DriverFactory created
- [ ] Command-line browser/headless selection

---

## Common Chrome Options

```java
options.addArguments("--headless=new");        // Headless mode
options.addArguments("--window-size=1920,1080"); // Window size
options.addArguments("--start-maximized");     // Start maximized
options.addArguments("--incognito");           // Incognito mode
options.addArguments("--disable-extensions");  // Disable extensions
options.addArguments("--disable-gpu");         // Disable GPU
options.addArguments("--no-sandbox");          // Required for some CI
options.addArguments("--disable-dev-shm-usage"); // Overcome limited resources
```

---

## Submission Checklist

| Item | Completed |
|------|-----------|
| Chrome options test | ☐ |
| Chrome headless test | ☐ |
| Firefox options test | ☐ |
| Firefox headless test | ☐ |
| Edge options test | ☐ |
| DriverFactory class | ☐ |
| CLI configuration | ☐ |

---

## Additional Resources

- Written Content: `option-classes-java.md`
- [ChromeDriver Capabilities](https://chromedriver.chromium.org/capabilities)
- [Firefox Preferences](https://support.mozilla.org/en-US/kb/about-config-editor-firefox)

