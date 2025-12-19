# Playwright Browser Management in Java

## Learning Objectives
- Launch browsers programmatically (Chromium, Firefox, WebKit)
- Configure browser launch options for different scenarios
- Manage browser contexts for test isolation
- Use incognito and persistent context modes
- Properly close browsers and release resources

## Why This Matters

Effective browser management enables:
- Test isolation through browser contexts
- Resource efficiency with proper cleanup
- Cross-browser testing capabilities
- Consistent test environments

## The Concept

### Launching Browsers

```java
import com.microsoft.playwright.*;

public class BrowserLaunchExamples {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            
            // Launch Chromium (Chrome/Edge)
            Browser chromium = playwright.chromium().launch();
            System.out.println("Chromium: " + chromium.version());
            chromium.close();
            
            // Launch Firefox
            Browser firefox = playwright.firefox().launch();
            System.out.println("Firefox: " + firefox.version());
            firefox.close();
            
            // Launch WebKit (Safari)
            Browser webkit = playwright.webkit().launch();
            System.out.println("WebKit: " + webkit.version());
            webkit.close();
        }
    }
}
```

### Browser Launch Options

```java
import com.microsoft.playwright.*;
import java.nio.file.Paths;
import java.util.Arrays;

public class BrowserOptions {
    
    public Browser launchWithOptions(Playwright playwright) {
        return playwright.chromium().launch(new BrowserType.LaunchOptions()
            // Visible browser (not headless)
            .setHeadless(false)
            
            // Slow down operations for debugging
            .setSlowMo(100)  // milliseconds
            
            // Browser arguments
            .setArgs(Arrays.asList(
                "--start-maximized",
                "--disable-extensions"
            ))
            
            // Download path for browser
            .setDownloadsPath(Paths.get("downloads"))
            
            // Timeout for browser launch
            .setTimeout(30000)  // milliseconds
            
            // Use specific Chrome channel
            .setChannel("chrome")  // or "msedge", "chrome-beta", etc.
        );
    }
    
    public Browser launchHeadless(Playwright playwright) {
        return playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(true)
            .setArgs(Arrays.asList(
                "--no-sandbox",
                "--disable-dev-shm-usage"
            ))
        );
    }
}
```

### Browser Contexts

Browser contexts provide isolated sessions:

```java
import com.microsoft.playwright.*;

public class BrowserContextExamples {
    
    public void demonstrateContextIsolation(Playwright playwright) {
        Browser browser = playwright.chromium().launch();
        
        // Context 1: First user session
        BrowserContext context1 = browser.newContext();
        Page page1 = context1.newPage();
        page1.navigate("https://example.com/login");
        // Login as User A
        page1.locator("#username").fill("userA");
        page1.locator("#password").fill("passwordA");
        page1.locator("#login").click();
        // User A is now logged in
        
        // Context 2: Second user session (completely isolated!)
        BrowserContext context2 = browser.newContext();
        Page page2 = context2.newPage();
        page2.navigate("https://example.com/login");
        // Login as User B
        page2.locator("#username").fill("userB");
        page2.locator("#password").fill("passwordB");
        page2.locator("#login").click();
        // User B is logged in - separate from User A
        
        // Contexts don't share:
        // - Cookies
        // - Local storage
        // - Session storage
        // - Cache
        
        context1.close();
        context2.close();
        browser.close();
    }
}
```

### Context Options

```java
import com.microsoft.playwright.*;
import java.nio.file.Paths;

public class ContextOptions {
    
    public BrowserContext createConfiguredContext(Browser browser) {
        return browser.newContext(new Browser.NewContextOptions()
            // Viewport size
            .setViewportSize(1920, 1080)
            
            // User agent
            .setUserAgent("Custom User Agent String")
            
            // Locale and timezone
            .setLocale("en-US")
            .setTimezoneId("America/New_York")
            
            // Geolocation
            .setGeolocation(40.7128, -74.0060)  // NYC
            .setPermissions(Arrays.asList("geolocation"))
            
            // Color scheme
            .setColorScheme(ColorScheme.DARK)
            
            // Accept downloads
            .setAcceptDownloads(true)
            
            // HTTP credentials
            .setHttpCredentials("username", "password")
            
            // Ignore HTTPS errors
            .setIgnoreHTTPSErrors(true)
            
            // JavaScript enabled
            .setJavaScriptEnabled(true)
            
            // Record video
            .setRecordVideoDir(Paths.get("videos/"))
            .setRecordVideoSize(1280, 720)
        );
    }
    
    public BrowserContext createMobileContext(Browser browser) {
        // Use device descriptor for mobile emulation
        return browser.newContext(new Browser.NewContextOptions()
            .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0...")
            .setViewportSize(375, 812)
            .setDeviceScaleFactor(3)
            .setIsMobile(true)
            .setHasTouch(true)
        );
    }
}
```

### Device Emulation

```java
import com.microsoft.playwright.*;

public class DeviceEmulation {
    
    public void emulateDevices(Playwright playwright) {
        Browser browser = playwright.chromium().launch();
        
        // iPhone 12
        BrowserContext iPhone = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(390, 844)
            .setDeviceScaleFactor(3)
            .setIsMobile(true)
            .setHasTouch(true)
            .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_4...")
        );
        
        // Pixel 5
        BrowserContext pixel = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(393, 851)
            .setDeviceScaleFactor(2.75)
            .setIsMobile(true)
            .setHasTouch(true)
        );
        
        // iPad
        BrowserContext iPad = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(768, 1024)
            .setDeviceScaleFactor(2)
            .setIsMobile(true)
            .setHasTouch(true)
        );
        
        iPhone.close();
        pixel.close();
        iPad.close();
        browser.close();
    }
}
```

### Incognito Mode

Browser contexts are incognito by default:

```java
// Default context is incognito (isolated)
BrowserContext incognitoContext = browser.newContext();
// No persistent storage, cookies cleared on close

// Each new context is a fresh incognito session
BrowserContext context1 = browser.newContext();
BrowserContext context2 = browser.newContext();
// context1 and context2 share nothing
```

### Persistent Context

Save and restore browser state:

```java
import com.microsoft.playwright.*;
import java.nio.file.Paths;

public class PersistentContext {
    
    public void usePersistentContext(Playwright playwright) {
        // Create persistent context (saves to disk)
        BrowserContext context = playwright.chromium().launchPersistentContext(
            Paths.get("user-data-dir"),
            new BrowserType.LaunchPersistentContextOptions()
                .setHeadless(false)
                .setViewportSize(1920, 1080)
        );
        
        Page page = context.pages().isEmpty() 
            ? context.newPage() 
            : context.pages().get(0);
        
        // Login once
        page.navigate("https://example.com/login");
        page.locator("#username").fill("user");
        page.locator("#password").fill("pass");
        page.locator("#login").click();
        
        // Session is saved to user-data-dir
        context.close();
        
        // Later: Reuse session
        BrowserContext resumedContext = playwright.chromium().launchPersistentContext(
            Paths.get("user-data-dir"),  // Same directory
            new BrowserType.LaunchPersistentContextOptions()
        );
        
        Page resumedPage = resumedContext.newPage();
        resumedPage.navigate("https://example.com/dashboard");
        // Already logged in!
        
        resumedContext.close();
    }
}
```

### Proper Browser Cleanup

```java
import com.microsoft.playwright.*;

public class ProperCleanup {
    
    // Method 1: Try-with-resources (recommended)
    public void testWithAutoCleanup() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            
            page.navigate("https://example.com");
            // Do testing...
            
        } // Playwright.close() called automatically
          // This closes all browsers, contexts, and pages
    }
    
    // Method 2: Explicit cleanup
    public void testWithManualCleanup() {
        Playwright playwright = null;
        Browser browser = null;
        BrowserContext context = null;
        
        try {
            playwright = Playwright.create();
            browser = playwright.chromium().launch();
            context = browser.newContext();
            Page page = context.newPage();
            
            page.navigate("https://example.com");
            // Do testing...
            
        } finally {
            // Close in reverse order
            if (context != null) context.close();
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();
        }
    }
    
    // Method 3: JUnit lifecycle
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    
    @BeforeAll
    static void setupBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }
    
    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }
    
    @BeforeEach
    void setupContext() {
        context = browser.newContext();
        page = context.newPage();
    }
    
    @AfterEach
    void closeContext() {
        context.close();
    }
}
```

### Cross-Browser Testing

```java
import com.microsoft.playwright.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CrossBrowserTest {
    
    static Playwright playwright;
    
    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
    }
    
    @AfterAll
    static void teardown() {
        playwright.close();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"chromium", "firefox", "webkit"})
    void testAcrossBrowsers(String browserType) {
        BrowserType type;
        switch (browserType) {
            case "chromium": type = playwright.chromium(); break;
            case "firefox": type = playwright.firefox(); break;
            case "webkit": type = playwright.webkit(); break;
            default: throw new IllegalArgumentException("Unknown browser");
        }
        
        try (Browser browser = type.launch()) {
            Page page = browser.newPage();
            page.navigate("https://example.com");
            
            assertThat(page).hasTitle("Example Domain");
        }
    }
}
```

## Key Takeaways

1. **Three browsers**: Chromium, Firefox, WebKit
2. **Launch options** control headless, slowMo, args
3. **Browser contexts** provide test isolation
4. **Context options** configure viewport, locale, device emulation
5. **Persistent contexts** save session state
6. **Always cleanup** using try-with-resources or explicit close

## Additional Resources

- [Playwright Browsers](https://playwright.dev/java/docs/browsers) - Browser documentation
- [Browser Contexts](https://playwright.dev/java/docs/browser-contexts) - Context isolation
- [Emulation](https://playwright.dev/java/docs/emulation) - Device and environment emulation

