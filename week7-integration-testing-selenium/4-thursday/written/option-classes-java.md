# Browser Options Classes in Selenium

## Learning Objectives
- Understand browser options classes and their purpose
- Configure ChromeOptions, FirefoxOptions, and EdgeOptions
- Enable headless mode for CI/CD environments
- Set browser arguments and capabilities
- Configure performance and security options

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, browsers need configuration beyond just launching. Whether you need headless execution for CI/CD, custom download directories, or specific security settings, browser options classes provide this control.

Mastering options classes enables you to run tests in any environment—from developer machines to containerized CI servers—with consistent, predictable browser behavior.

## Understanding Browser Options

### What Are Options Classes?

Options classes configure how browsers launch and behave. Each browser has its own options class:

```java
// Browser-specific options classes
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeOptions;
```

### Common Configuration Categories

```
Options Configuration Categories:
┌─────────────────────────────────────────────────────────────────────┐
│ Arguments        │ Command-line flags (--headless, --incognito)     │
├──────────────────┼──────────────────────────────────────────────────┤
│ Preferences      │ Browser settings (download path, notifications) │
├──────────────────┼──────────────────────────────────────────────────┤
│ Capabilities     │ WebDriver capabilities (platform, version)      │
├──────────────────┼──────────────────────────────────────────────────┤
│ Extensions       │ Load browser extensions                          │
├──────────────────┼──────────────────────────────────────────────────┤
│ Binary           │ Custom browser executable path                  │
└──────────────────┴──────────────────────────────────────────────────┘
```

## ChromeOptions

### Basic Configuration

```java
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;

ChromeOptions options = new ChromeOptions();

// Add arguments (command-line flags)
options.addArguments("--start-maximized");
options.addArguments("--disable-notifications");

// Create driver with options
WebDriver driver = new ChromeDriver(options);
```

### Common Chrome Arguments

```java
ChromeOptions options = new ChromeOptions();

// Window configuration
options.addArguments("--start-maximized");        // Maximize on start
options.addArguments("--start-fullscreen");       // Full screen mode
options.addArguments("--window-size=1920,1080");  // Specific size

// Headless mode
options.addArguments("--headless=new");           // New headless mode (Chrome 109+)

// Security and permissions
options.addArguments("--disable-notifications");  // Block notifications
options.addArguments("--disable-popup-blocking"); // Allow popups
options.addArguments("--disable-infobars");       // Hide info bars
options.addArguments("--incognito");              // Incognito mode

// Performance
options.addArguments("--disable-gpu");            // Disable GPU (for headless)
options.addArguments("--no-sandbox");             // Required in Docker
options.addArguments("--disable-dev-shm-usage");  // Overcome limited resources

// SSL/Security
options.addArguments("--ignore-certificate-errors");  // Ignore SSL errors
options.addArguments("--allow-insecure-localhost");   // Allow insecure localhost
```

### Chrome Preferences

```java
import java.util.HashMap;
import java.util.Map;

ChromeOptions options = new ChromeOptions();

// Set experimental options (preferences)
Map<String, Object> prefs = new HashMap<>();

// Download settings
prefs.put("download.default_directory", "/path/to/downloads");
prefs.put("download.prompt_for_download", false);
prefs.put("download.directory_upgrade", true);

// Disable password manager
prefs.put("credentials_enable_service", false);
prefs.put("profile.password_manager_enabled", false);

// Disable images (faster loading)
prefs.put("profile.managed_default_content_settings.images", 2);

// Set language
prefs.put("intl.accept_languages", "en-US");

options.setExperimentalOption("prefs", prefs);
```

### Chrome Extensions

```java
ChromeOptions options = new ChromeOptions();

// Load unpacked extension
options.addArguments("--load-extension=/path/to/extension");

// Load packed extension (.crx file)
options.addExtensions(new File("/path/to/extension.crx"));
```

### Chrome Binary Path

```java
ChromeOptions options = new ChromeOptions();

// Custom Chrome installation
options.setBinary("/path/to/chrome");  // Linux/Mac
options.setBinary("C:\\Program Files\\Chrome\\chrome.exe");  // Windows
```

## Headless Mode

### Chrome Headless

```java
ChromeOptions options = new ChromeOptions();

// New headless mode (Chrome 109+)
options.addArguments("--headless=new");

// Set window size (important for headless)
options.addArguments("--window-size=1920,1080");

// Additional args often needed for headless
options.addArguments("--disable-gpu");
options.addArguments("--no-sandbox");
options.addArguments("--disable-dev-shm-usage");

WebDriver driver = new ChromeDriver(options);
```

### Firefox Headless

```java
FirefoxOptions options = new FirefoxOptions();

// Enable headless mode
options.addArguments("-headless");

// Set window size
options.addArguments("-width=1920");
options.addArguments("-height=1080");

WebDriver driver = new FirefoxDriver(options);
```

### Edge Headless

```java
EdgeOptions options = new EdgeOptions();

// Enable headless mode
options.addArguments("--headless=new");
options.addArguments("--window-size=1920,1080");

WebDriver driver = new EdgeDriver(options);
```

### Headless Configuration Helper

```java
public class HeadlessConfig {
    
    public static ChromeOptions getChromeHeadless() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return options;
    }
    
    public static FirefoxOptions getFirefoxHeadless() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");
        options.addArguments("-width=1920");
        options.addArguments("-height=1080");
        return options;
    }
    
    public static boolean isHeadlessMode() {
        return Boolean.parseBoolean(
            System.getProperty("headless", 
            System.getenv().getOrDefault("HEADLESS", "false"))
        );
    }
}
```

## FirefoxOptions

### Basic Configuration

```java
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

FirefoxOptions options = new FirefoxOptions();

// Add arguments
options.addArguments("-private");  // Private browsing

// Create driver
WebDriver driver = new FirefoxDriver(options);
```

### Firefox Preferences

```java
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

// Using FirefoxProfile
FirefoxProfile profile = new FirefoxProfile();

// Download settings
profile.setPreference("browser.download.folderList", 2);
profile.setPreference("browser.download.dir", "/path/to/downloads");
profile.setPreference("browser.helperApps.neverAsk.saveToDisk", 
    "application/pdf,application/zip,application/octet-stream");

// Disable notifications
profile.setPreference("dom.webnotifications.enabled", false);

// Disable images
profile.setPreference("permissions.default.image", 2);

// Set language
profile.setPreference("intl.accept_languages", "en-US");

FirefoxOptions options = new FirefoxOptions();
options.setProfile(profile);

WebDriver driver = new FirefoxDriver(options);
```

### Firefox Binary Path

```java
FirefoxOptions options = new FirefoxOptions();

// Custom Firefox installation
options.setBinary("/path/to/firefox");

// Or using GeckoDriver path
System.setProperty("webdriver.gecko.driver", "/path/to/geckodriver");
```

## EdgeOptions

### Basic Configuration

```java
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.edge.EdgeDriver;

EdgeOptions options = new EdgeOptions();

// Add arguments
options.addArguments("--start-maximized");
options.addArguments("--inprivate");  // InPrivate mode

WebDriver driver = new EdgeDriver(options);
```

### Edge Preferences

```java
EdgeOptions options = new EdgeOptions();

Map<String, Object> prefs = new HashMap<>();
prefs.put("download.default_directory", "/path/to/downloads");
prefs.put("download.prompt_for_download", false);

options.setExperimentalOption("prefs", prefs);
```

## Capabilities Configuration

### Setting Capabilities

```java
ChromeOptions options = new ChromeOptions();

// Set capability
options.setCapability("acceptInsecureCerts", true);
options.setCapability("pageLoadStrategy", PageLoadStrategy.NORMAL);

// Page load strategies:
// NORMAL - Wait for full page load
// EAGER - Wait for DOMContentLoaded
// NONE - Return immediately
```

### Platform and Browser Capabilities

```java
ChromeOptions options = new ChromeOptions();

// For Selenium Grid / Remote execution
options.setCapability("platformName", "Windows 10");
options.setCapability("browserVersion", "119");

// Cloud testing platforms
options.setCapability("browserstack:options", Map.of(
    "os", "Windows",
    "osVersion", "11",
    "local", "false"
));
```

## Complete Options Factory

```java
package com.example.config;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.edge.EdgeOptions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BrowserOptionsFactory {
    
    private static final String DOWNLOAD_PATH = System.getProperty("user.home") 
        + File.separator + "Downloads" + File.separator + "selenium";
    
    public static ChromeOptions getChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        
        // Basic arguments
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-infobars");
        
        // Headless configuration
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }
        
        // Preferences
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", DOWNLOAD_PATH);
        prefs.put("download.prompt_for_download", false);
        prefs.put("credentials_enable_service", false);
        options.setExperimentalOption("prefs", prefs);
        
        // Capabilities
        options.setCapability("acceptInsecureCerts", true);
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        
        return options;
    }
    
    public static FirefoxOptions getFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        
        // Headless mode
        if (headless) {
            options.addArguments("-headless");
            options.addArguments("-width=1920");
            options.addArguments("-height=1080");
        }
        
        // Profile preferences
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.dir", DOWNLOAD_PATH);
        profile.setPreference("dom.webnotifications.enabled", false);
        options.setProfile(profile);
        
        // Capabilities
        options.setCapability("acceptInsecureCerts", true);
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        
        return options;
    }
    
    public static EdgeOptions getEdgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        
        options.addArguments("--start-maximized");
        
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", DOWNLOAD_PATH);
        options.setExperimentalOption("prefs", prefs);
        
        return options;
    }
}
```

## Summary

- **Options classes** configure browser behavior before launch
- **ChromeOptions**, **FirefoxOptions**, **EdgeOptions** provide browser-specific settings
- **Headless mode** enables running without visible browser (essential for CI/CD)
- **Arguments** are command-line flags; **preferences** are browser settings
- **Capabilities** configure WebDriver behavior and remote execution
- **Factory pattern** standardizes options across your test framework

In the next lesson, you'll learn about navigation methods for controlling browser history and page loads.

## Additional Resources

- [ChromeDriver Capabilities](https://chromedriver.chromium.org/capabilities) - Chrome-specific options
- [GeckoDriver Capabilities](https://firefox-source-docs.mozilla.org/testing/geckodriver/Capabilities.html) - Firefox options
- [Selenium Options Documentation](https://www.selenium.dev/documentation/webdriver/browsers/) - Official guide

