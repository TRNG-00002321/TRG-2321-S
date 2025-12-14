# Manual WebDriver Setup in Java

## Learning Objectives
- Download and configure browser drivers manually
- Set system properties for driver executables
- Configure drivers for Chrome, Firefox, and Edge
- Understand cross-browser driver setup patterns
- Troubleshoot common driver configuration issues

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, understanding manual driver setup is foundational. While tools like WebDriverManager (covered tomorrow) automate this process, knowing manual setup helps you:

- Understand what's happening under the hood
- Troubleshoot driver issues effectively
- Work in restricted environments where auto-download isn't possible
- Use specific driver versions when needed
- Debug driver compatibility problems

## Understanding Browser Drivers

### What Are Browser Drivers?

Browser drivers are executables that act as intermediaries between your test code and the browser. Each browser requires its own specific driver.

```
Test Code → Browser Driver → Browser
    │            │              │
    │ HTTP       │ DevTools     │
    │ (WebDriver │ Protocol     │
    │  Protocol) │              │
    └────────────┴──────────────┘
```

### Driver-Browser Matrix

| Browser | Driver | Download Source |
|---------|--------|-----------------|
| Chrome | ChromeDriver | chromedriver.chromium.org |
| Firefox | GeckoDriver | github.com/mozilla/geckodriver |
| Edge | EdgeDriver | developer.microsoft.com/microsoft-edge |
| Safari | SafariDriver | Built into macOS Safari |
| Opera | OperaDriver | github.com/nicenemo/opera-chromium-driver |

### Version Compatibility

```
CRITICAL: Driver version must match browser version!

Chrome Version: 119.0.6045.105
ChromeDriver Version: 119.x.xxxx.xx ✓

Chrome Version: 119.0.6045.105
ChromeDriver Version: 118.x.xxxx.xx ✗ May not work!
```

## ChromeDriver Setup

### Step 1: Check Chrome Version

```
Find Chrome Version:
├── Windows: chrome://version/ or Settings → About Chrome
├── macOS: Chrome → About Google Chrome
└── Linux: google-chrome --version
```

### Step 2: Download ChromeDriver

**From Official Source:**
1. Visit: https://chromedriver.chromium.org/downloads
2. For Chrome 115+: https://googlechromelabs.github.io/chrome-for-testing/
3. Find matching version for your Chrome
4. Download for your OS (win32/win64/mac-arm64/mac-x64/linux64)
5. Extract the executable

**Chrome for Testing Downloads (Chrome 115+):**
```
https://googlechromelabs.github.io/chrome-for-testing/

Downloads available:
├── chromedriver-win32.zip
├── chromedriver-win64.zip
├── chromedriver-mac-arm64.zip
├── chromedriver-mac-x64.zip
└── chromedriver-linux64.zip
```

### Step 3: Configure in Java

**Option 1: System Property**
```java
// Set path to ChromeDriver executable
System.setProperty("webdriver.chrome.driver", 
    "C:\\drivers\\chromedriver.exe");  // Windows

System.setProperty("webdriver.chrome.driver", 
    "/usr/local/bin/chromedriver");    // Linux/Mac

// Create driver instance
WebDriver driver = new ChromeDriver();
```

**Option 2: Environment Variable**
```bash
# Windows (Command Prompt)
set PATH=%PATH%;C:\drivers

# Windows (PowerShell)
$env:PATH += ";C:\drivers"

# Linux/Mac
export PATH=$PATH:/usr/local/bin
```

```java
// If chromedriver is in PATH, no property needed
WebDriver driver = new ChromeDriver();
```

**Option 3: Project Resources**
```
Project structure:
selenium-tests/
├── src/test/resources/
│   └── drivers/
│       ├── chromedriver.exe      (Windows)
│       ├── chromedriver          (Mac/Linux)
│       └── geckodriver.exe
```

```java
// Reference driver from project
System.setProperty("webdriver.chrome.driver", 
    "src/test/resources/drivers/chromedriver.exe");
```

### Complete Chrome Setup Example

```java
package com.example.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeSetup {
    
    public static WebDriver createChromeDriver() {
        // Set driver path
        System.setProperty("webdriver.chrome.driver", 
            "src/test/resources/drivers/chromedriver.exe");
        
        // Configure options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        
        // Create and return driver
        return new ChromeDriver(options);
    }
    
    public static void main(String[] args) {
        WebDriver driver = createChromeDriver();
        
        try {
            driver.get("https://www.google.com");
            System.out.println("Page title: " + driver.getTitle());
        } finally {
            driver.quit();
        }
    }
}
```

## GeckoDriver Setup (Firefox)

### Step 1: Check Firefox Version

```
Find Firefox Version:
├── Windows: Help → About Firefox
├── macOS: Firefox → About Firefox
└── Linux: firefox --version
```

### Step 2: Download GeckoDriver

**From GitHub Releases:**
1. Visit: https://github.com/mozilla/geckodriver/releases
2. Download appropriate version:
   - `geckodriver-vX.XX.X-win64.zip` (Windows 64-bit)
   - `geckodriver-vX.XX.X-win32.zip` (Windows 32-bit)
   - `geckodriver-vX.XX.X-macos.tar.gz` (macOS)
   - `geckodriver-vX.XX.X-linux64.tar.gz` (Linux 64-bit)
3. Extract executable

### Step 3: Configure in Java

```java
// Set GeckoDriver path
System.setProperty("webdriver.gecko.driver", 
    "src/test/resources/drivers/geckodriver.exe");

// Create Firefox driver
WebDriver driver = new FirefoxDriver();
```

### Complete Firefox Setup Example

```java
package com.example.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

public class FirefoxSetup {
    
    public static WebDriver createFirefoxDriver() {
        // Set driver path
        System.setProperty("webdriver.gecko.driver", 
            "src/test/resources/drivers/geckodriver.exe");
        
        // Configure profile (optional)
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", 
            "application/pdf");
        
        // Configure options
        FirefoxOptions options = new FirefoxOptions();
        options.setProfile(profile);
        // options.addArguments("-headless");  // Headless mode
        
        return new FirefoxDriver(options);
    }
    
    public static void main(String[] args) {
        WebDriver driver = createFirefoxDriver();
        
        try {
            driver.get("https://www.mozilla.org");
            System.out.println("Page title: " + driver.getTitle());
        } finally {
            driver.quit();
        }
    }
}
```

## EdgeDriver Setup

### Step 1: Check Edge Version

```
Find Edge Version:
├── Windows: edge://version/ or Settings → About Microsoft Edge
├── macOS: Microsoft Edge → About Microsoft Edge
└── Linux: microsoft-edge --version
```

### Step 2: Download EdgeDriver

**From Microsoft:**
1. Visit: https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/
2. Download version matching your Edge browser
3. Extract `msedgedriver.exe`

### Step 3: Configure in Java

```java
// Set EdgeDriver path
System.setProperty("webdriver.edge.driver", 
    "src/test/resources/drivers/msedgedriver.exe");

// Create Edge driver
WebDriver driver = new EdgeDriver();
```

### Complete Edge Setup Example

```java
package com.example.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class EdgeSetup {
    
    public static WebDriver createEdgeDriver() {
        // Set driver path
        System.setProperty("webdriver.edge.driver", 
            "src/test/resources/drivers/msedgedriver.exe");
        
        // Configure options
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--inprivate");  // Private mode
        
        return new EdgeDriver(options);
    }
    
    public static void main(String[] args) {
        WebDriver driver = createEdgeDriver();
        
        try {
            driver.get("https://www.microsoft.com");
            System.out.println("Page title: " + driver.getTitle());
        } finally {
            driver.quit();
        }
    }
}
```

## Cross-Browser Driver Setup

### Browser Factory Pattern

```java
package com.example.drivers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class DriverFactory {
    
    private static final String DRIVERS_PATH = "src/test/resources/drivers/";
    
    public enum BrowserType {
        CHROME, FIREFOX, EDGE
    }
    
    public static WebDriver createDriver(BrowserType browser) {
        WebDriver driver;
        
        switch (browser) {
            case CHROME:
                driver = setupChrome();
                break;
            case FIREFOX:
                driver = setupFirefox();
                break;
            case EDGE:
                driver = setupEdge();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
        
        return driver;
    }
    
    private static WebDriver setupChrome() {
        System.setProperty("webdriver.chrome.driver", 
            DRIVERS_PATH + "chromedriver.exe");
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        
        return new ChromeDriver(options);
    }
    
    private static WebDriver setupFirefox() {
        System.setProperty("webdriver.gecko.driver", 
            DRIVERS_PATH + "geckodriver.exe");
        
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-width=1920");
        options.addArguments("-height=1080");
        
        return new FirefoxDriver(options);
    }
    
    private static WebDriver setupEdge() {
        System.setProperty("webdriver.edge.driver", 
            DRIVERS_PATH + "msedgedriver.exe");
        
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        
        return new EdgeDriver(options);
    }
}
```

### Using the Factory

```java
package com.example.tests;

import com.example.drivers.DriverFactory;
import com.example.drivers.DriverFactory.BrowserType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openqa.selenium.WebDriver;

class CrossBrowserTest {
    
    private WebDriver driver;
    
    @ParameterizedTest
    @EnumSource(BrowserType.class)
    @DisplayName("Page loads correctly on all browsers")
    void testPageLoadsOnAllBrowsers(BrowserType browserType) {
        driver = DriverFactory.createDriver(browserType);
        
        try {
            driver.get("https://example.com");
            
            String title = driver.getTitle();
            Assertions.assertNotNull(title);
            Assertions.assertFalse(title.isEmpty());
            
            System.out.println(browserType + ": " + title);
        } finally {
            driver.quit();
        }
    }
}
```

### Configuration-Based Browser Selection

```java
package com.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
    
    private static Properties properties;
    
    static {
        properties = new Properties();
        try (InputStream input = TestConfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String getBrowser() {
        return System.getProperty("browser", 
            properties.getProperty("browser", "chrome"));
    }
    
    public static String getBaseUrl() {
        return properties.getProperty("base.url", "https://example.com");
    }
}
```

**config.properties:**
```properties
browser=chrome
base.url=https://example.com
driver.path=src/test/resources/drivers/
implicit.wait=10
```

## Troubleshooting Common Issues

### Issue 1: Driver Not Found

```
Error: The path to the driver executable must be set...

Solutions:
1. Verify driver file exists at specified path
2. Check file permissions (executable on Linux/Mac)
3. Verify correct system property name:
   - webdriver.chrome.driver
   - webdriver.gecko.driver  
   - webdriver.edge.driver
4. Use absolute path for testing
```

```java
// Debug: Print absolute path
File driverFile = new File("src/test/resources/drivers/chromedriver.exe");
System.out.println("Driver path: " + driverFile.getAbsolutePath());
System.out.println("Exists: " + driverFile.exists());
```

### Issue 2: Version Mismatch

```
Error: This version of ChromeDriver only supports Chrome version XX

Solutions:
1. Check Chrome browser version
2. Download matching ChromeDriver version
3. For Chrome 115+, use new download location:
   https://googlechromelabs.github.io/chrome-for-testing/
```

### Issue 3: Permission Denied (Mac/Linux)

```bash
# Make driver executable
chmod +x chromedriver
chmod +x geckodriver

# Verify
ls -la chromedriver
# Should show: -rwxr-xr-x
```

### Issue 4: macOS Security Block

```
Error: "chromedriver" cannot be opened because the developer 
       cannot be verified.

Solutions:
1. System Preferences → Security & Privacy → General
2. Click "Allow Anyway" for chromedriver

Or via terminal:
xattr -d com.apple.quarantine chromedriver
```

### Issue 5: Browser Not Starting

```java
// Add debugging options
ChromeOptions options = new ChromeOptions();
options.addArguments("--verbose");
options.addArguments("--log-path=chromedriver.log");

// Check if port is in use
// Default ChromeDriver port: 9515
```

## Driver Location Strategies

### Strategy Comparison

```
┌─────────────────────────────────────────────────────────────────────┐
│ Strategy           │ Pros                │ Cons                     │
├────────────────────┼─────────────────────┼──────────────────────────┤
│ Project resources  │ Portable, versioned │ Manual updates          │
│ System PATH        │ Simple, shared      │ Environment dependent   │
│ Environment var    │ Flexible            │ Setup on each machine   │
│ WebDriverManager   │ Automatic           │ External dependency     │
└─────────────────────────────────────────────────────────────────────┘
```

### Best Practices

```
Manual Driver Setup Best Practices:
┌─────────────────────────────────────────────────────────────────────┐
│ 1. Use relative paths in project (src/test/resources/drivers/)     │
│ 2. Add drivers to .gitignore (don't commit binaries)               │
│ 3. Document required driver versions in README                      │
│ 4. Create setup scripts for team members                            │
│ 5. Consider CI/CD driver management (cache/download)               │
│ 6. Use factory pattern for cross-browser flexibility               │
│ 7. Externalize driver paths to configuration files                  │
│ 8. Test driver setup as part of build verification                 │
└─────────────────────────────────────────────────────────────────────┘
```

## Summary

- **Browser drivers** are essential executables that bridge test code and browsers
- **Version matching** between driver and browser is critical for compatibility
- **System properties** configure driver locations: `webdriver.chrome.driver`, etc.
- **Cross-browser setup** benefits from a factory pattern for maintainability
- **Common issues** include path errors, version mismatches, and permission problems
- Tomorrow you'll learn **WebDriverManager** which automates this entire process

In the next lesson, you'll learn XPath fundamentals for locating elements on web pages.

## Additional Resources

- [ChromeDriver Downloads](https://googlechromelabs.github.io/chrome-for-testing/) - Official Chrome drivers
- [GeckoDriver Releases](https://github.com/mozilla/geckodriver/releases) - Firefox driver
- [EdgeDriver Downloads](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/) - Edge driver

