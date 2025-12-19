# Screenshot and Video Capture in Playwright Java

## Learning Objectives
- Capture page screenshots using various methods
- Take element-specific screenshots
- Configure full-page screenshot capture
- Record video of test execution
- Configure video recording options
- Manage screenshot and video artifacts

## Why This Matters

Visual evidence is crucial for:
- Debugging test failures
- Documenting test execution
- Visual regression testing
- Stakeholder reporting
- CI/CD artifact collection

## The Concept

### Page Screenshots

```java
import com.microsoft.playwright.*;
import java.nio.file.Paths;

public class ScreenshotExamples {
    
    @Test
    void basicScreenshot() {
        page.navigate("https://example.com");
        
        // Simple screenshot
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(Paths.get("screenshot.png"))
        );
    }
    
    @Test
    void screenshotWithOptions() {
        page.navigate("https://example.com");
        
        page.screenshot(new Page.ScreenshotOptions()
            // Save location
            .setPath(Paths.get("screenshots/page.png"))
            
            // Full page (scrolls entire page)
            .setFullPage(true)
            
            // Image format
            .setType(ScreenshotType.PNG)  // or JPEG
            
            // JPEG quality (0-100)
            // .setQuality(80)  // Only for JPEG
            
            // Clip to specific area
            // .setClip(100, 100, 500, 300)  // x, y, width, height
            
            // Omit background (transparent)
            .setOmitBackground(false)
            
            // Animation handling
            .setAnimations(ScreenshotAnimations.DISABLED)
            
            // Timeout
            .setTimeout(30000)
        );
    }
    
    @Test
    void screenshotToBytes() {
        page.navigate("https://example.com");
        
        // Get screenshot as byte array
        byte[] buffer = page.screenshot();
        
        // Useful for:
        // - Attaching to reports
        // - Sending to APIs
        // - Base64 encoding
        String base64 = java.util.Base64.getEncoder().encodeToString(buffer);
    }
}
```

### Element Screenshots

```java
@Test
void elementScreenshot() {
    page.navigate("https://example.com");
    
    // Screenshot of specific element
    page.locator("#header").screenshot(new Locator.ScreenshotOptions()
        .setPath(Paths.get("screenshots/header.png"))
    );
    
    // Element with options
    page.locator(".product-card").first().screenshot(new Locator.ScreenshotOptions()
        .setPath(Paths.get("screenshots/product.png"))
        .setOmitBackground(true)  // Transparent background
        .setAnimations(ScreenshotAnimations.DISABLED)
    );
}
```

### Full Page Screenshots

```java
@Test
void fullPageScreenshot() {
    page.navigate("https://example.com/long-page");
    
    // Captures entire scrollable page
    page.screenshot(new Page.ScreenshotOptions()
        .setPath(Paths.get("screenshots/full-page.png"))
        .setFullPage(true)
    );
}
```

### Screenshot on Failure

```java
public class BaseTest {
    
    Page page;
    BrowserContext context;
    
    @AfterEach
    void captureOnFailure(TestInfo testInfo) {
        // Check if test failed
        // Note: JUnit5 doesn't expose failure status directly in @AfterEach
        // Use TestWatcher extension instead
        context.close();
    }
}

// Better approach: TestWatcher extension
public class ScreenshotOnFailureExtension implements TestWatcher {
    
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        // Get page from test instance
        Object testInstance = context.getRequiredTestInstance();
        if (testInstance instanceof BaseTest) {
            Page page = ((BaseTest) testInstance).getPage();
            if (page != null) {
                String fileName = "screenshots/failure_" + 
                    context.getDisplayName() + ".png";
                page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get(fileName))
                    .setFullPage(true)
                );
            }
        }
    }
}

// Usage
@ExtendWith(ScreenshotOnFailureExtension.class)
public class MyTests extends BaseTest {
    // Tests...
}
```

### Video Recording

```java
import com.microsoft.playwright.*;
import java.nio.file.Paths;

public class VideoRecordingExamples {
    
    @Test
    void recordVideo() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            
            // Enable video recording in context
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/"))
            );
            
            Page page = context.newPage();
            page.navigate("https://example.com");
            page.locator("#login").click();
            
            // Video is saved when context closes
            context.close();
            
            browser.close();
        }
    }
    
    @Test
    void recordVideoWithOptions() {
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
            // Video directory
            .setRecordVideoDir(Paths.get("videos/"))
            
            // Video dimensions
            .setRecordVideoSize(1280, 720)
        );
        
        Page page = context.newPage();
        
        // Run test...
        page.navigate("https://example.com");
        
        // Get video path (available after page closes)
        page.close();
        Path videoPath = page.video().path();
        System.out.println("Video saved: " + videoPath);
        
        context.close();
    }
    
    @Test
    void saveVideoWithCustomName() {
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
            .setRecordVideoDir(Paths.get("videos/"))
        );
        
        Page page = context.newPage();
        page.navigate("https://example.com");
        
        // Close page to finalize video
        page.close();
        
        // Save with custom name
        Path originalPath = page.video().path();
        Path customPath = Paths.get("videos/my_test_" + 
            System.currentTimeMillis() + ".webm");
        
        page.video().saveAs(customPath);
        
        // Delete original if needed
        page.video().delete();
        
        context.close();
    }
}
```

### Artifact Management

```java
public class ArtifactManager {
    
    private final Path screenshotDir;
    private final Path videoDir;
    
    public ArtifactManager(String baseDir) {
        this.screenshotDir = Paths.get(baseDir, "screenshots");
        this.videoDir = Paths.get(baseDir, "videos");
        
        // Create directories
        try {
            Files.createDirectories(screenshotDir);
            Files.createDirectories(videoDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create artifact directories", e);
        }
    }
    
    public void captureScreenshot(Page page, String name) {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = name + "_" + timestamp + ".png";
        
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(screenshotDir.resolve(fileName))
            .setFullPage(true)
        );
    }
    
    public BrowserContext createRecordingContext(Browser browser) {
        return browser.newContext(new Browser.NewContextOptions()
            .setRecordVideoDir(videoDir)
            .setRecordVideoSize(1920, 1080)
        );
    }
    
    public void saveVideo(Page page, String testName) {
        if (page.video() != null) {
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path targetPath = videoDir.resolve(testName + "_" + timestamp + ".webm");
            page.video().saveAs(targetPath);
        }
    }
    
    public void cleanupOldArtifacts(int daysToKeep) {
        // Remove artifacts older than specified days
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysToKeep);
        
        try (Stream<Path> files = Files.walk(screenshotDir)) {
            files.filter(Files::isRegularFile)
                 .filter(f -> isOlderThan(f, cutoff))
                 .forEach(this::deleteQuietly);
        } catch (IOException e) {
            // Log error
        }
    }
}
```

### Complete Test with Artifacts

```java
public class VisualTestWithArtifacts {
    
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    
    static final Path ARTIFACTS_DIR = Paths.get("test-artifacts");
    
    @BeforeAll
    static void setup() throws IOException {
        Files.createDirectories(ARTIFACTS_DIR.resolve("screenshots"));
        Files.createDirectories(ARTIFACTS_DIR.resolve("videos"));
        
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }
    
    @BeforeEach
    void createContext(TestInfo testInfo) {
        // Record video for each test
        context = browser.newContext(new Browser.NewContextOptions()
            .setRecordVideoDir(ARTIFACTS_DIR.resolve("videos"))
            .setRecordVideoSize(1280, 720)
        );
        page = context.newPage();
    }
    
    @AfterEach
    void cleanup(TestInfo testInfo) {
        // Screenshot on completion
        String testName = testInfo.getDisplayName();
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(ARTIFACTS_DIR.resolve("screenshots/" + testName + ".png"))
            .setFullPage(true)
        );
        
        // Save video with test name
        page.close();
        if (page.video() != null) {
            page.video().saveAs(ARTIFACTS_DIR.resolve("videos/" + testName + ".webm"));
        }
        
        context.close();
    }
    
    @Test
    void userLoginFlow() {
        page.navigate("https://example.com/login");
        
        // Take screenshot at each step
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(ARTIFACTS_DIR.resolve("screenshots/step1_login_page.png"))
        );
        
        page.locator("#username").fill("testuser");
        page.locator("#password").fill("password123");
        
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(ARTIFACTS_DIR.resolve("screenshots/step2_filled_form.png"))
        );
        
        page.locator("#submit").click();
        
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(ARTIFACTS_DIR.resolve("screenshots/step3_after_login.png"))
        );
    }
    
    @AfterAll
    static void teardown() {
        browser.close();
        playwright.close();
    }
}
```

## Key Takeaways

1. **page.screenshot()** captures page or viewport
2. **locator.screenshot()** captures specific elements
3. **setFullPage(true)** captures entire scrollable content
4. **Video recording** enabled via context options
5. **Video saved** when page/context closes
6. **Artifact management** essential for CI/CD

## Additional Resources

- [Playwright Screenshots](https://playwright.dev/java/docs/screenshots) - Screenshot documentation
- [Playwright Videos](https://playwright.dev/java/docs/videos) - Video recording guide
- [Test Artifacts](https://playwright.dev/java/docs/ci#uploading-playwright-artifacts-on-ci) - CI artifact handling

