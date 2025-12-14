# Automated Driver Management with WebDriverManager

## Learning Objectives
- Understand the benefits of automated driver management
- Set up and use WebDriverManager library in your projects
- Configure automatic driver downloads for all major browsers
- Handle browser version detection and driver matching
- Configure WebDriverManager for various use cases

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, you learned manual driver setup yesterday. While understanding manual setup is valuable, maintaining driver executables manually is tedious and error-prone—especially when browsers update frequently.

WebDriverManager automates this entirely, downloading the correct driver version for your browser automatically. This eliminates "driver version mismatch" errors and simplifies CI/CD integration dramatically.

## The Driver Management Problem

### Manual Setup Challenges

```
Manual Driver Management Issues:
┌─────────────────────────────────────────────────────────────────────┐
│ 1. Version Matching                                                  │
│    Browser updates automatically → Driver becomes outdated          │
│    Must manually download matching driver version                   │
│                                                                      │
│ 2. Multiple Browsers                                                 │
│    Need to maintain drivers for Chrome, Firefox, Edge, Safari       │
│    Each has different download locations and update schedules       │
│                                                                      │
│ 3. Team Coordination                                                 │
│    Each team member needs correct drivers                           │
│    Different OS versions require different driver builds            │
│                                                                      │
│ 4. CI/CD Complexity                                                  │
│    Build servers need drivers pre-installed                         │
│    Version drift between environments                               │
└─────────────────────────────────────────────────────────────────────┘
```

### WebDriverManager Solution

```
WebDriverManager Benefits:
┌─────────────────────────────────────────────────────────────────────┐
│ ✓ Automatic Downloads                                                │
│   Downloads correct driver automatically                            │
│                                                                      │
│ ✓ Version Detection                                                  │
│   Detects installed browser version                                 │
│   Downloads matching driver                                         │
│                                                                      │
│ ✓ Caching                                                            │
│   Caches drivers locally                                            │
│   Reuses without re-downloading                                     │
│                                                                      │
│ ✓ Cross-Platform                                                     │
│   Works on Windows, macOS, Linux                                    │
│   Handles platform-specific binaries                                │
│                                                                      │
│ ✓ CI/CD Friendly                                                     │
│   No pre-installation required                                      │
│   Same code works everywhere                                        │
└─────────────────────────────────────────────────────────────────────┘
```

## WebDriverManager Setup

### Adding Dependency

**Maven (pom.xml):**
```xml
<dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.6.2</version>
    <scope>test</scope>
</dependency>
```

**Gradle (build.gradle):**
```groovy
testImplementation 'io.github.bonigarcia:webdrivermanager:5.6.2'
```

### Basic Usage

```java
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BasicExample {
    public static void main(String[] args) {
        // Automatic driver setup - one line!
        WebDriverManager.chromedriver().setup();
        
        // Create driver as normal
        WebDriver driver = new ChromeDriver();
        
        try {
            driver.get("https://example.com");
            System.out.println("Title: " + driver.getTitle());
        } finally {
            driver.quit();
        }
    }
}
```

### Comparison: Manual vs WebDriverManager

**Manual Setup:**
```java
// Manual: Download driver, set path, hope versions match
System.setProperty("webdriver.chrome.driver", 
    "C:\\drivers\\chromedriver.exe");
WebDriver driver = new ChromeDriver();
```

**WebDriverManager:**
```java
// Automatic: One line handles everything
WebDriverManager.chromedriver().setup();
WebDriver driver = new ChromeDriver();
```

## Browser-Specific Setup

### Chrome

```java
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

// Setup ChromeDriver
WebDriverManager.chromedriver().setup();

// Optional: Configure options
ChromeOptions options = new ChromeOptions();
options.addArguments("--start-maximized");

// Create driver
WebDriver driver = new ChromeDriver(options);
```

### Firefox

```java
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

// Setup GeckoDriver
WebDriverManager.firefoxdriver().setup();

// Optional: Configure options
FirefoxOptions options = new FirefoxOptions();
options.addArguments("-headless");

// Create driver
WebDriver driver = new FirefoxDriver(options);
```

### Edge

```java
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

// Setup EdgeDriver
WebDriverManager.edgedriver().setup();

// Create driver
WebDriver driver = new EdgeDriver();
```

### Safari

```java
// Safari doesn't need WebDriverManager
// SafariDriver is built into macOS
import org.openqa.selenium.safari.SafariDriver;

// Enable Remote Automation in Safari:
// Safari → Preferences → Advanced → Show Develop menu
// Develop → Allow Remote Automation

WebDriver driver = new SafariDriver();
```

## WebDriverManager Configuration

### Specifying Browser Version

```java
// Use specific driver version
WebDriverManager.chromedriver()
    .driverVersion("119.0.6045.105")
    .setup();

// Use specific browser version (downloads matching driver)
WebDriverManager.chromedriver()
    .browserVersion("119")
    .setup();
```

### Cache Configuration

```java
// Set cache path
WebDriverManager.chromedriver()
    .cachePath("/custom/cache/path")
    .setup();

// Set cache time-to-live (hours)
WebDriverManager.chromedriver()
    .ttl(24)  // Re-check every 24 hours
    .setup();

// Force download (ignore cache)
WebDriverManager.chromedriver()
    .forceDownload()
    .setup();

// Clear cache
WebDriverManager.chromedriver().clearDriverCache();
```

### Proxy Configuration

```java
// HTTP proxy
WebDriverManager.chromedriver()
    .proxy("http://proxy.company.com:8080")
    .setup();

// Proxy with authentication
WebDriverManager.chromedriver()
    .proxy("http://proxy.company.com:8080")
    .proxyUser("username")
    .proxyPass("password")
    .setup();
```

### Architecture Configuration

```java
// Force specific architecture
WebDriverManager.chromedriver()
    .arch64()  // 64-bit
    .setup();

WebDriverManager.chromedriver()
    .arch32()  // 32-bit
    .setup();

// Auto-detect (default)
WebDriverManager.chromedriver()
    .setup();  // Detects automatically
```

### Using Environment Variables

```java
// Can also configure via environment variables
// WDM_CHROMEDRIVER_VERSION=119.0.6045.105
// WDM_CACHEPATH=/custom/cache
// WDM_PROXY=http://proxy:8080

// In code, these are automatically read
WebDriverManager.chromedriver().setup();
```

## Driver Factory with WebDriverManager

### Complete Factory Implementation

```java
package com.example.drivers;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class DriverFactory {
    
    public enum Browser {
        CHROME, FIREFOX, EDGE, CHROME_HEADLESS, FIREFOX_HEADLESS
    }
    
    public static WebDriver createDriver(Browser browser) {
        switch (browser) {
            case CHROME:
                return createChromeDriver(false);
            case CHROME_HEADLESS:
                return createChromeDriver(true);
            case FIREFOX:
                return createFirefoxDriver(false);
            case FIREFOX_HEADLESS:
                return createFirefoxDriver(true);
            case EDGE:
                return createEdgeDriver();
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }
    
    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        
        return new ChromeDriver(options);
    }
    
    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        
        FirefoxOptions options = new FirefoxOptions();
        
        if (headless) {
            options.addArguments("-headless");
        }
        
        return new FirefoxDriver(options);
    }
    
    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        
        return new EdgeDriver(options);
    }
    
    // Get browser from system property or environment variable
    public static WebDriver createDriverFromConfig() {
        String browserName = System.getProperty("browser", 
            System.getenv().getOrDefault("BROWSER", "chrome"));
        
        boolean headless = Boolean.parseBoolean(
            System.getProperty("headless", 
            System.getenv().getOrDefault("HEADLESS", "false")));
        
        Browser browser;
        switch (browserName.toLowerCase()) {
            case "firefox":
                browser = headless ? Browser.FIREFOX_HEADLESS : Browser.FIREFOX;
                break;
            case "edge":
                browser = Browser.EDGE;
                break;
            case "chrome":
            default:
                browser = headless ? Browser.CHROME_HEADLESS : Browser.CHROME;
                break;
        }
        
        return createDriver(browser);
    }
}
```

### Using the Factory

```java
public class TestBase {
    protected WebDriver driver;
    
    @BeforeEach
    void setUp() {
        // Uses system property or env variable, defaults to Chrome
        driver = DriverFactory.createDriverFromConfig();
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

// Run with different browsers:
// mvn test -Dbrowser=firefox
// mvn test -Dbrowser=chrome -Dheadless=true
```

## Advanced Configuration

### Timeout Configuration

```java
WebDriverManager.chromedriver()
    .timeout(60)  // Download timeout in seconds
    .setup();
```

### Using Configuration File

Create `webdrivermanager.properties` in resources:
```properties
wdm.chromeDriverVersion=119.0.6045.105
wdm.cachePath=/custom/cache
wdm.ttl=24
wdm.proxy=http://proxy:8080
wdm.timeout=60
```

```java
// Properties file is automatically read
WebDriverManager.chromedriver().setup();
```

### Logging Configuration

```java
import io.github.bonigarcia.wdm.config.Config;

// Enable verbose logging
WebDriverManager.chromedriver()
    .config()
    .setAvoidOutputTree(false);

// Or via system property
System.setProperty("wdm.avoidOutputTree", "false");
```

## Integration with JUnit 5

### Using @BeforeAll for Performance

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OptimizedTest {
    
    WebDriver driver;
    
    @BeforeAll
    void setupClass() {
        // Setup once per test class
        WebDriverManager.chromedriver().setup();
    }
    
    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    void testSomething() {
        driver.get("https://example.com");
        // ...
    }
}
```

### Cross-Browser Testing

```java
@ParameterizedTest
@EnumSource(DriverFactory.Browser.class)
void testOnAllBrowsers(DriverFactory.Browser browser) {
    // Skip Safari on non-macOS
    Assumptions.assumeTrue(
        browser != DriverFactory.Browser.SAFARI || 
        System.getProperty("os.name").toLowerCase().contains("mac")
    );
    
    WebDriver driver = DriverFactory.createDriver(browser);
    
    try {
        driver.get("https://example.com");
        assertNotNull(driver.getTitle());
    } finally {
        driver.quit();
    }
}
```

## CI/CD Integration

### GitHub Actions

```yaml
# .github/workflows/tests.yml
name: Selenium Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      # No driver installation needed - WebDriverManager handles it
      
      - name: Run Tests
        run: mvn test -Dheadless=true
```

### Jenkins Pipeline

```groovy
pipeline {
    agent any
    
    stages {
        stage('Test') {
            steps {
                // WebDriverManager downloads drivers automatically
                sh 'mvn test -Dheadless=true'
            }
        }
    }
}
```

### Docker

```dockerfile
FROM maven:3.9-eclipse-temurin-17

# Install Chrome
RUN apt-get update && apt-get install -y \
    chromium-browser \
    && rm -rf /var/lib/apt/lists/*

# No driver installation - WebDriverManager handles it
COPY . /app
WORKDIR /app

CMD ["mvn", "test", "-Dheadless=true"]
```

## Troubleshooting

### Common Issues

```
Issue: Driver version mismatch despite WebDriverManager
Solution: Clear cache and force re-download
─────────────────────────────────────────────────────
WebDriverManager.chromedriver()
    .clearDriverCache()
    .forceDownload()
    .setup();

Issue: Network/proxy blocking downloads
Solution: Configure proxy settings
─────────────────────────────────────────────────────
WebDriverManager.chromedriver()
    .proxy("http://proxy:8080")
    .setup();

Issue: Slow first run (downloading)
Solution: Cache drivers in CI/CD
─────────────────────────────────────────────────────
# Cache ~/.cache/selenium directory in your CI config

Issue: Running in Docker without display
Solution: Use headless mode
─────────────────────────────────────────────────────
options.addArguments("--headless=new");
options.addArguments("--no-sandbox");
options.addArguments("--disable-dev-shm-usage");
```

## Summary

- **WebDriverManager** automates browser driver management completely
- **One-line setup**: `WebDriverManager.chromedriver().setup()` replaces manual configuration
- **Automatic version matching** detects browser version and downloads matching driver
- **Caching** prevents repeated downloads, improving performance
- **Configuration options** include proxy, timeout, cache path, and specific versions
- **CI/CD friendly**: Works seamlessly without pre-installed drivers

In the next lesson, you'll learn about browser options classes for configuring browser behavior.

## Additional Resources

- [WebDriverManager Documentation](https://bonigarcia.dev/webdrivermanager/) - Official docs
- [WebDriverManager GitHub](https://github.com/bonigarcia/webdrivermanager) - Source and examples
- [Configuration Options](https://bonigarcia.dev/webdrivermanager/#configuration) - Full configuration reference

