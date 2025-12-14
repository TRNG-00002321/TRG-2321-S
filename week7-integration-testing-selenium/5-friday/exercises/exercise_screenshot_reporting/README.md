# Lab: Screenshot Capture and Reporting

## Overview

**Duration:** 45-60 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll implement screenshot capture capabilities for test debugging and reporting. You'll create automatic screenshot-on-failure functionality.

---

## Learning Objectives

By completing this lab, you will:
- Capture full page screenshots
- Capture element screenshots
- Implement screenshot-on-failure
- Organize screenshots with timestamps
- Create a reusable screenshot utility

---

## Prerequisites

- Completed Selenium WebDriver exercises
- Understanding of TakesScreenshot interface
- File I/O basics in Java

---

## Core Tasks

### Task 1: Basic Screenshot Capture (15 minutes)

**Create `ScreenshotTest.java`:**

```java
package com.bookhaven.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class ScreenshotTest extends BaseTest {

    private static final String SCREENSHOT_DIR = "screenshots/";

    @BeforeAll
    static void createScreenshotDirectory() {
        File dir = new File(SCREENSHOT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Test
    @DisplayName("Capture full page screenshot")
    void testFullPageScreenshot() throws IOException {
        driver.get("https://the-internet.herokuapp.com/");
        
        // Cast driver to TakesScreenshot
        TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
        
        // Take screenshot
        File screenshot = screenshotDriver.getScreenshotAs(OutputType.FILE);
        
        // Save to file
        String filename = SCREENSHOT_DIR + "full_page_" + getTimestamp() + ".png";
        FileUtils.copyFile(screenshot, new File(filename));
        
        // Verify file exists
        assertTrue(new File(filename).exists());
        System.out.println("Screenshot saved: " + filename);
    }

    @Test
    @DisplayName("Capture screenshot as bytes")
    void testScreenshotAsBytes() {
        driver.get("https://the-internet.herokuapp.com/");
        
        TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
        
        // Get as byte array (useful for reports)
        byte[] screenshotBytes = screenshotDriver.getScreenshotAs(OutputType.BYTES);
        
        assertTrue(screenshotBytes.length > 0);
        System.out.println("Screenshot size: " + screenshotBytes.length + " bytes");
    }

    @Test
    @DisplayName("Capture screenshot as Base64")
    void testScreenshotAsBase64() {
        driver.get("https://the-internet.herokuapp.com/");
        
        TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
        
        // Get as Base64 string (useful for embedding in HTML)
        String base64Screenshot = screenshotDriver.getScreenshotAs(OutputType.BASE64);
        
        assertNotNull(base64Screenshot);
        assertFalse(base64Screenshot.isEmpty());
        
        // Can be used in HTML: <img src="data:image/png;base64,{base64Screenshot}">
    }

    private String getTimestamp() {
        return LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        );
    }
}
```

### Task 2: Element Screenshot (10 minutes)

**Add element screenshot tests:**

```java
@Test
@DisplayName("Capture element screenshot")
void testElementScreenshot() throws IOException {
    driver.get("https://the-internet.herokuapp.com/login");
    
    // Find specific element
    WebElement loginForm = driver.findElement(By.id("login"));
    
    // Take screenshot of just the element
    File screenshot = loginForm.getScreenshotAs(OutputType.FILE);
    
    // Save
    String filename = SCREENSHOT_DIR + "login_form_" + getTimestamp() + ".png";
    FileUtils.copyFile(screenshot, new File(filename));
    
    assertTrue(new File(filename).exists());
    System.out.println("Element screenshot saved: " + filename);
}

@Test
@DisplayName("Compare element and full page screenshots")
void testCompareScreenshotSizes() throws IOException {
    driver.get("https://the-internet.herokuapp.com/login");
    
    // Full page
    TakesScreenshot pageScreenshot = (TakesScreenshot) driver;
    File fullPage = pageScreenshot.getScreenshotAs(OutputType.FILE);
    
    // Element only
    WebElement loginForm = driver.findElement(By.id("login"));
    File elementOnly = loginForm.getScreenshotAs(OutputType.FILE);
    
    // Element screenshot should be smaller
    assertTrue(elementOnly.length() < fullPage.length());
    
    System.out.println("Full page: " + fullPage.length() + " bytes");
    System.out.println("Element: " + elementOnly.length() + " bytes");
}
```

### Task 3: Screenshot on Failure (20 minutes)

**Create reusable utility:**

```java
package com.bookhaven.utils;

import org.openqa.selenium.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotHelper {
    
    private WebDriver driver;
    private String baseDir;
    
    public ScreenshotHelper(WebDriver driver, String baseDir) {
        this.driver = driver;
        this.baseDir = baseDir;
        createDirectory();
    }
    
    private void createDirectory() {
        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public String takeScreenshot(String name) {
        String filename = generateFilename(name);
        
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            File destination = new File(filename);
            FileUtils.copyFile(source, destination);
            
            return filename;
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
            return null;
        }
    }
    
    public String takeElementScreenshot(WebElement element, String name) {
        String filename = generateFilename(name + "_element");
        
        try {
            File source = element.getScreenshotAs(OutputType.FILE);
            File destination = new File(filename);
            FileUtils.copyFile(source, destination);
            
            return filename;
        } catch (IOException e) {
            System.err.println("Failed to save element screenshot: " + e.getMessage());
            return null;
        }
    }
    
    public byte[] getScreenshotBytes() {
        TakesScreenshot ts = (TakesScreenshot) driver;
        return ts.getScreenshotAs(OutputType.BYTES);
    }
    
    public String getScreenshotBase64() {
        TakesScreenshot ts = (TakesScreenshot) driver;
        return ts.getScreenshotAs(OutputType.BASE64);
    }
    
    private String generateFilename(String name) {
        String timestamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")
        );
        String sanitizedName = name.replaceAll("[^a-zA-Z0-9-_]", "_");
        return baseDir + sanitizedName + "_" + timestamp + ".png";
    }
}
```

**Create test with automatic failure screenshot:**

```java
package com.bookhaven.tests;

import com.bookhaven.utils.ScreenshotHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ScreenshotOnFailureTest implements TestWatcher {
    
    protected WebDriver driver;
    protected ScreenshotHelper screenshotHelper;
    
    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        screenshotHelper = new ScreenshotHelper(driver, "screenshots/failures/");
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String testName = context.getDisplayName();
        String screenshotPath = screenshotHelper.takeScreenshot("FAILED_" + testName);
        System.out.println("Failure screenshot: " + screenshotPath);
    }
    
    @Test
    @DisplayName("Test that will pass")
    void testThatPasses() {
        driver.get("https://example.com");
        // This passes - no screenshot
        assertTrue(driver.getTitle().contains("Example"));
    }
    
    @Test
    @DisplayName("Test that will fail")
    void testThatFails() {
        driver.get("https://example.com");
        // This fails - screenshot taken automatically
        assertEquals("Wrong Title", driver.getTitle());
    }
}
```

### Task 4: JUnit 5 Extension for Screenshots (15 minutes)

**Create extension:**

```java
package com.bookhaven.extensions;

import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotExtension implements TestWatcher, AfterTestExecutionCallback {
    
    private static final String SCREENSHOT_DIR = "screenshots/";
    
    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        // Get test result
        if (context.getExecutionException().isPresent()) {
            takeFailureScreenshot(context);
        }
    }
    
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        takeFailureScreenshot(context);
    }
    
    private void takeFailureScreenshot(ExtensionContext context) {
        // Get WebDriver from test instance
        Object testInstance = context.getRequiredTestInstance();
        
        try {
            java.lang.reflect.Field driverField = 
                testInstance.getClass().getDeclaredField("driver");
            driverField.setAccessible(true);
            WebDriver driver = (WebDriver) driverField.get(testInstance);
            
            if (driver != null) {
                String testName = context.getDisplayName();
                saveScreenshot(driver, "FAILED_" + testName);
            }
        } catch (Exception e) {
            System.err.println("Could not capture failure screenshot: " + e.getMessage());
        }
    }
    
    private void saveScreenshot(WebDriver driver, String name) {
        try {
            // Create directory
            Path dir = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            
            // Generate filename
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
            );
            String filename = SCREENSHOT_DIR + name.replaceAll("[^a-zA-Z0-9-_]", "_") 
                + "_" + timestamp + ".png";
            
            // Take and save screenshot
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            Files.copy(source.toPath(), Paths.get(filename));
            
            System.out.println("📸 Screenshot saved: " + filename);
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }
    }
}

// Usage:
@ExtendWith(ScreenshotExtension.class)
class MyTests extends BaseTest {
    // Screenshots automatically captured on failure
}
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Full page screenshot capture
- [ ] Element screenshot capture
- [ ] Screenshot as bytes/Base64
- [ ] ScreenshotHelper utility class
- [ ] Automatic screenshot on failure
- [ ] JUnit 5 extension for screenshots
- [ ] Screenshots organized with timestamps
- [ ] All tests demonstrate functionality

---

## Screenshot Organization

```
screenshots/
├── passed/
│   ├── login_test_20241209_143022.png
│   └── checkout_test_20241209_143045.png
├── failures/
│   ├── FAILED_invalid_login_20241209_143100.png
│   └── FAILED_payment_error_20241209_143115.png
└── elements/
    ├── login_form_element_20241209_143022.png
    └── error_message_element_20241209_143100.png
```

---

## Submission Checklist

| Item | Completed |
|------|-----------|
| Full page screenshot | ☐ |
| Element screenshot | ☐ |
| Screenshot as bytes | ☐ |
| Screenshot as Base64 | ☐ |
| ScreenshotHelper class | ☐ |
| Screenshot on failure | ☐ |
| JUnit 5 extension | ☐ |
| Timestamp in filenames | ☐ |

---

## Additional Resources

- Written Content: `screenshots-java.md`
- [TakesScreenshot Interface](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/TakesScreenshot.html)

