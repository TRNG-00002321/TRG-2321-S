# Capturing Screenshots in Selenium WebDriver

## Learning Objectives
- Capture full-page screenshots using TakesScreenshot interface
- Save screenshots to files with meaningful names
- Capture element-specific screenshots
- Implement automatic screenshot capture on test failure
- Integrate screenshots into test reports

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, screenshots are essential for debugging and documentation. When a test fails, a screenshot shows exactly what the user would see at the moment of failure, making it invaluable for diagnosing issues.

Screenshots also serve as visual evidence for test reports, helping stakeholders understand test coverage and results.

## The TakesScreenshot Interface

### Basic Screenshot Capture

```java
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;

// Cast driver to TakesScreenshot
TakesScreenshot screenshotDriver = (TakesScreenshot) driver;

// Capture screenshot as file
File screenshot = screenshotDriver.getScreenshotAs(OutputType.FILE);
```

### OutputType Options

```
OutputType Options:
┌─────────────────────────────────────────────────────────────────────┐
│ OutputType.FILE   │ Returns File object (temporary file)           │
│ OutputType.BYTES  │ Returns byte[] array                           │
│ OutputType.BASE64 │ Returns Base64 encoded string                  │
└───────────────────┴─────────────────────────────────────────────────┘
```

## Saving Screenshots to Files

### Basic File Save

```java
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.io.IOException;

public void captureScreenshot(String filename) {
    TakesScreenshot ts = (TakesScreenshot) driver;
    File source = ts.getScreenshotAs(OutputType.FILE);
    
    try {
        File destination = new File("screenshots/" + filename + ".png");
        FileUtils.copyFile(source, destination);
        System.out.println("Screenshot saved: " + destination.getAbsolutePath());
    } catch (IOException e) {
        System.err.println("Failed to save screenshot: " + e.getMessage());
    }
}
```

### With Timestamp

```java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public void captureScreenshot(String testName) {
    TakesScreenshot ts = (TakesScreenshot) driver;
    File source = ts.getScreenshotAs(OutputType.FILE);
    
    // Create timestamp
    String timestamp = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    
    String filename = testName + "_" + timestamp + ".png";
    
    try {
        File screenshotDir = new File("screenshots");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }
        
        File destination = new File(screenshotDir, filename);
        FileUtils.copyFile(source, destination);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

### Using Java NIO (Without Apache Commons)

```java
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public void captureScreenshotNIO(String filename) {
    TakesScreenshot ts = (TakesScreenshot) driver;
    File source = ts.getScreenshotAs(OutputType.FILE);
    
    try {
        Path screenshotDir = Paths.get("screenshots");
        if (!Files.exists(screenshotDir)) {
            Files.createDirectories(screenshotDir);
        }
        
        Path destination = screenshotDir.resolve(filename + ".png");
        Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        
        System.out.println("Screenshot saved: " + destination.toAbsolutePath());
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

## Element Screenshots

### Capturing Specific Element (Selenium 4)

```java
import org.openqa.selenium.WebElement;

// Find the element
WebElement element = driver.findElement(By.id("login-form"));

// Capture screenshot of element only
File elementScreenshot = element.getScreenshotAs(OutputType.FILE);

// Save it
try {
    FileUtils.copyFile(elementScreenshot, new File("screenshots/login-form.png"));
} catch (IOException e) {
    e.printStackTrace();
}
```

### Element Screenshot Utility

```java
public void captureElementScreenshot(WebElement element, String filename) {
    try {
        File source = element.getScreenshotAs(OutputType.FILE);
        
        Path screenshotDir = Paths.get("screenshots", "elements");
        if (!Files.exists(screenshotDir)) {
            Files.createDirectories(screenshotDir);
        }
        
        Path destination = screenshotDir.resolve(filename + ".png");
        Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// Usage
WebElement errorMessage = driver.findElement(By.className("error"));
captureElementScreenshot(errorMessage, "validation-error");
```

## Automatic Screenshot on Failure

### JUnit 5 Extension

```java
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotOnFailureExtension implements TestWatcher {
    
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        // Get WebDriver from test instance
        Object testInstance = context.getRequiredTestInstance();
        
        try {
            var field = testInstance.getClass().getDeclaredField("driver");
            field.setAccessible(true);
            WebDriver driver = (WebDriver) field.get(testInstance);
            
            if (driver != null) {
                captureScreenshot(driver, context.getDisplayName());
            }
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }
    
    private void captureScreenshot(WebDriver driver, String testName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        
        // Clean test name for filename
        String cleanName = testName.replaceAll("[^a-zA-Z0-9]", "_");
        String filename = "FAILED_" + cleanName + "_" + timestamp + ".png";
        
        try {
            Path screenshotDir = Paths.get("screenshots", "failures");
            Files.createDirectories(screenshotDir);
            
            Path destination = screenshotDir.resolve(filename);
            Files.copy(source.toPath(), destination);
            
            System.out.println("Failure screenshot: " + destination.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Usage in test class
@ExtendWith(ScreenshotOnFailureExtension.class)
class LoginTests {
    WebDriver driver;  // Field accessed by extension
    
    @Test
    void testLogin() {
        // Test code...
        // If test fails, screenshot is automatically captured
    }
}
```

### TestNG Listener

```java
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScreenshotListener implements ITestListener {
    
    @Override
    public void onTestFailure(ITestResult result) {
        Object testInstance = result.getInstance();
        
        try {
            var field = testInstance.getClass().getDeclaredField("driver");
            field.setAccessible(true);
            WebDriver driver = (WebDriver) field.get(testInstance);
            
            if (driver != null) {
                captureScreenshot(driver, result.getName());
            }
        } catch (Exception e) {
            System.err.println("Screenshot capture failed: " + e.getMessage());
        }
    }
    
    private void captureScreenshot(WebDriver driver, String testName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        
        try {
            Path destination = Paths.get("screenshots", "failures", 
                "FAILED_" + testName + ".png");
            Files.createDirectories(destination.getParent());
            Files.copy(source.toPath(), destination);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Screenshot Utility Class

```java
package com.example.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class ScreenshotUtils {
    
    private static final String SCREENSHOT_DIR = "screenshots";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");
    
    /**
     * Capture full page screenshot and save to file
     */
    public static Path captureScreenshot(WebDriver driver, String name) {
        return captureScreenshot(driver, name, "general");
    }
    
    /**
     * Capture screenshot with category subdirectory
     */
    public static Path captureScreenshot(WebDriver driver, String name, String category) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String cleanName = sanitizeFilename(name);
        String filename = cleanName + "_" + timestamp + ".png";
        
        try {
            Path dir = Paths.get(SCREENSHOT_DIR, category);
            Files.createDirectories(dir);
            
            Path destination = dir.resolve(filename);
            Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            
            return destination;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save screenshot", e);
        }
    }
    
    /**
     * Capture element screenshot
     */
    public static Path captureElementScreenshot(WebElement element, String name) {
        File source = element.getScreenshotAs(OutputType.FILE);
        
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String filename = sanitizeFilename(name) + "_" + timestamp + ".png";
        
        try {
            Path dir = Paths.get(SCREENSHOT_DIR, "elements");
            Files.createDirectories(dir);
            
            Path destination = dir.resolve(filename);
            Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            
            return destination;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save element screenshot", e);
        }
    }
    
    /**
     * Get screenshot as Base64 string (for embedding in reports)
     */
    public static String getScreenshotAsBase64(WebDriver driver) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        return ts.getScreenshotAs(OutputType.BASE64);
    }
    
    /**
     * Get screenshot as byte array
     */
    public static byte[] getScreenshotAsBytes(WebDriver driver) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        return ts.getScreenshotAs(OutputType.BYTES);
    }
    
    /**
     * Capture failure screenshot with error details
     */
    public static Path captureFailureScreenshot(WebDriver driver, String testName, 
                                                 Throwable error) {
        String errorType = error.getClass().getSimpleName();
        String name = "FAILED_" + testName + "_" + errorType;
        return captureScreenshot(driver, name, "failures");
    }
    
    /**
     * Sanitize filename by removing invalid characters
     */
    private static String sanitizeFilename(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
```

## Integration with Test Reports

### Allure Integration

```java
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public void attachScreenshotToAllure(WebDriver driver, String name) {
    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    Allure.getLifecycle().addAttachment(
        name, 
        "image/png", 
        ".png", 
        screenshot
    );
}

// Or using annotation
@Attachment(value = "Page Screenshot", type = "image/png")
public byte[] saveScreenshot(WebDriver driver) {
    return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
}
```

### HTML Report Embedding

```java
// Capture as Base64 for HTML embedding
String base64Screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);

// Embed in HTML report
String htmlImg = "<img src='data:image/png;base64," + base64Screenshot + "'/>";
```

## Complete Test Example

```java
package com.example.tests;

import com.example.utils.ScreenshotUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.nio.file.Path;

@ExtendWith(ScreenshotOnFailureExtension.class)
class ScreenshotDemoTest {
    
    WebDriver driver;
    
    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    @DisplayName("Capture screenshot after login")
    void testLoginWithScreenshot() {
        driver.get("https://example.com/login");
        
        // Perform login
        driver.findElement(By.id("username")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.id("login")).click();
        
        // Capture success screenshot
        Path screenshot = ScreenshotUtils.captureScreenshot(driver, "login_success");
        System.out.println("Screenshot saved: " + screenshot);
        
        // Continue with assertions
        assertTrue(driver.getCurrentUrl().contains("dashboard"));
    }
    
    @Test
    @DisplayName("Capture element screenshot")
    void testElementScreenshot() {
        driver.get("https://example.com/dashboard");
        
        // Capture specific element
        var widget = driver.findElement(By.id("stats-widget"));
        Path elementShot = ScreenshotUtils.captureElementScreenshot(widget, "stats_widget");
        
        System.out.println("Element screenshot: " + elementShot);
    }
    
    @Test
    @DisplayName("This test will fail and capture screenshot")
    void testFailureScreenshot() {
        driver.get("https://example.com");
        
        // This will fail - extension captures screenshot automatically
        assertEquals("Wrong Title", driver.getTitle());
    }
}
```

## Summary

- **TakesScreenshot** interface provides screenshot capture capability
- **OutputType** options: FILE (temp file), BYTES (byte array), BASE64 (string)
- **Element screenshots** (Selenium 4+) capture specific elements only
- **Automatic capture on failure** via JUnit extensions or TestNG listeners
- Use **timestamps** in filenames to avoid overwrites
- **Organize screenshots** in folders by category (failures, elements, general)
- **Base64 encoding** enables embedding in HTML reports
- **Allure integration** attaches screenshots directly to test reports

In the next lesson, you'll learn about running Selenium tests from the command line.

## Additional Resources

- [Selenium Screenshots](https://www.selenium.dev/documentation/webdriver/interactions/screenshots/) - Official docs
- [Apache Commons IO](https://commons.apache.org/proper/commons-io/) - File utilities

