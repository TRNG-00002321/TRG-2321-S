# Visual Testing Setup in Playwright Java

## Learning Objectives
- Understand visual testing concepts and benefits
- Set up visual testing infrastructure in Playwright Java
- Configure baseline image storage and comparison
- Handle platform-specific visual differences
- Integrate visual testing with CI/CD pipelines

## Why This Matters

Visual testing catches issues that functional tests miss:
- CSS regressions
- Layout shifts
- Font rendering issues
- Responsive design problems
- Unintended visual changes

## The Concept

### Visual Testing Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                  Visual Testing Workflow                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   First Run:                                                     │
│   ┌──────────┐                                                  │
│   │ Capture  │ ───► Save as baseline image                      │
│   │Screenshot│                                                  │
│   └──────────┘                                                  │
│                                                                  │
│   Subsequent Runs:                                               │
│   ┌──────────┐     ┌──────────┐     ┌──────────┐               │
│   │ Capture  │ ──► │ Compare  │ ──► │ Report   │               │
│   │Screenshot│     │ Baseline │     │ Result   │               │
│   └──────────┘     └──────────┘     └──────────┘               │
│                          │                                       │
│                    Match/Mismatch                                │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Built-in Playwright Visual Comparison

Playwright provides built-in screenshot comparison:

```java
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class VisualTestSetup {
    
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    
    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }
    
    @BeforeEach
    void createContext() {
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1920, 1080)
        );
        page = context.newPage();
    }
    
    @Test
    void visualComparisonTest() {
        page.navigate("https://example.com");
        
        // Compare screenshot against baseline
        assertThat(page).hasScreenshot("homepage.png");
        
        // First run: Creates baseline in test snapshots folder
        // Subsequent runs: Compares against baseline
    }
}
```

### Project Structure for Visual Testing

```
project/
├── src/
│   ├── main/java/
│   └── test/java/
│       └── com/example/tests/
│           └── VisualTests.java
├── test-snapshots/               # Baseline images
│   ├── com.example.tests.VisualTests/
│   │   ├── homepage-chromium-linux.png
│   │   ├── homepage-chromium-win32.png
│   │   └── homepage-chromium-darwin.png
├── test-results/                 # Test artifacts
│   └── com.example.tests.VisualTests/
│       └── homepage-actual.png   # Actual screenshot
│       └── homepage-diff.png     # Difference image
└── pom.xml
```

### Configuring Screenshot Comparison

```java
public class VisualTestConfig {
    
    @Test
    void screenshotWithOptions() {
        page.navigate("https://example.com");
        
        // Compare with tolerance options
        assertThat(page).hasScreenshot("page.png", 
            new PageAssertions.HasScreenshotOptions()
                // Maximum allowed pixel difference ratio (0-1)
                .setMaxDiffPixelRatio(0.01)  // 1% tolerance
                
                // Maximum absolute pixel difference
                // .setMaxDiffPixels(100)
                
                // Threshold for color comparison (0-1)
                .setThreshold(0.2)
                
                // Animation handling
                .setAnimations(ScreenshotAnimations.DISABLED)
                
                // Specific comparison mask
                .setMask(Arrays.asList(
                    page.locator(".dynamic-content"),
                    page.locator(".timestamp")
                ))
        );
    }
    
    @Test
    void elementScreenshotComparison() {
        page.navigate("https://example.com");
        
        // Compare specific element
        Locator header = page.locator("#header");
        assertThat(header).hasScreenshot("header.png");
        
        // With options
        assertThat(header).hasScreenshot("header-styled.png",
            new LocatorAssertions.HasScreenshotOptions()
                .setOmitBackground(true)
        );
    }
}
```

### Handling Dynamic Content

```java
public class DynamicContentHandling {
    
    @Test
    void maskDynamicElements() {
        page.navigate("https://example.com");
        
        // Mask elements that change between runs
        assertThat(page).hasScreenshot("page.png",
            new PageAssertions.HasScreenshotOptions()
                .setMask(Arrays.asList(
                    page.locator(".date-time"),
                    page.locator(".random-ad"),
                    page.locator(".user-avatar"),
                    page.locator("[data-testid='dynamic-content']")
                ))
        );
    }
    
    @Test
    void hideAnimations() {
        page.navigate("https://example.com");
        
        // Disable CSS animations
        assertThat(page).hasScreenshot("static.png",
            new PageAssertions.HasScreenshotOptions()
                .setAnimations(ScreenshotAnimations.DISABLED)
        );
    }
    
    @Test
    void waitForStableContent() {
        page.navigate("https://example.com");
        
        // Wait for dynamic content to stabilize
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator(".loading-spinner").waitFor(
            new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN)
        );
        
        // Now take screenshot
        assertThat(page).hasScreenshot("stable.png");
    }
    
    @Test
    void freezeTimeForConsistency() {
        // Mock date for consistent timestamps
        page.addInitScript("" +
            "Date.prototype.getTime = () => 1609459200000;" +
            "Date.now = () => 1609459200000;"
        );
        
        page.navigate("https://example.com");
        assertThat(page).hasScreenshot("frozen-time.png");
    }
}
```

### Cross-Platform Visual Testing

```java
public class CrossPlatformVisual {
    
    // Playwright stores platform-specific baselines automatically
    // homepage-chromium-linux.png
    // homepage-chromium-win32.png
    // homepage-chromium-darwin.png
    
    @Test
    void crossPlatformTest() {
        page.navigate("https://example.com");
        
        // Playwright uses platform-specific baseline automatically
        assertThat(page).hasScreenshot("homepage.png");
    }
    
    // For CI/CD consistency, use Docker or specific platform
    // docker run --rm -v $(pwd):/work mcr.microsoft.com/playwright/java:latest
}
```

### Custom Baseline Storage

```java
public class CustomBaselineStorage {
    
    private static final Path BASELINES_DIR = Paths.get("visual-baselines");
    private static final Path RESULTS_DIR = Paths.get("visual-results");
    
    @BeforeAll
    static void setupDirectories() throws IOException {
        Files.createDirectories(BASELINES_DIR);
        Files.createDirectories(RESULTS_DIR);
    }
    
    @Test
    void manualBaselineComparison() throws IOException {
        page.navigate("https://example.com");
        
        String testName = "homepage";
        Path baselinePath = BASELINES_DIR.resolve(testName + ".png");
        Path actualPath = RESULTS_DIR.resolve(testName + "-actual.png");
        
        // Capture current screenshot
        byte[] actual = page.screenshot(new Page.ScreenshotOptions()
            .setFullPage(true)
        );
        Files.write(actualPath, actual);
        
        if (Files.exists(baselinePath)) {
            // Compare with baseline
            byte[] baseline = Files.readAllBytes(baselinePath);
            
            // Use image comparison library
            // (BufferedImage comparison, or external library)
            boolean match = compareImages(baseline, actual);
            
            assertTrue(match, "Visual regression detected: " + testName);
        } else {
            // Create baseline
            Files.write(baselinePath, actual);
            System.out.println("Created baseline: " + baselinePath);
        }
    }
    
    private boolean compareImages(byte[] baseline, byte[] actual) {
        // Implement pixel-by-pixel comparison
        // Or use library like java-image-comparison
        return Arrays.equals(baseline, actual);
    }
}
```

### Environment Configuration

```java
public class VisualTestEnvironment {
    
    @BeforeEach
    void setupConsistentEnvironment() {
        // Fixed viewport for consistent screenshots
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1920, 1080)
            .setDeviceScaleFactor(1.0)
            .setColorScheme(ColorScheme.LIGHT)
            .setLocale("en-US")
            .setTimezoneId("UTC")
        );
        page = context.newPage();
        
        // Disable font anti-aliasing differences
        page.addStyleTag(new Page.AddStyleTagOptions()
            .setContent("* { -webkit-font-smoothing: antialiased !important; }"));
    }
    
    @Test
    void consistentVisualTest() {
        page.navigate("https://example.com");
        assertThat(page).hasScreenshot("consistent.png");
    }
}
```

### Maven Configuration for Visual Testing

```xml
<!-- pom.xml -->
<project>
    <dependencies>
        <dependency>
            <groupId>com.microsoft.playwright</groupId>
            <artifactId>playwright</artifactId>
            <version>1.40.0</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
                <configuration>
                    <systemPropertyVariables>
                        <!-- Update baselines -->
                        <playwright.update-snapshots>
                            ${updateSnapshots}
                        </playwright.update-snapshots>
                    </systemPropertyVariables>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

<!-- Run with: mvn test -DupdateSnapshots=true -->
```

## Key Takeaways

1. **`assertThat(page).hasScreenshot()`** provides built-in comparison
2. **Baselines** stored per platform automatically
3. **Mask dynamic content** to prevent false positives
4. **Consistent environment** (viewport, locale) essential
5. **Tolerance options** handle minor rendering differences
6. **CI/CD integration** requires platform consistency

## Additional Resources

- [Playwright Visual Comparisons](https://playwright.dev/java/docs/test-snapshots) - Official guide
- [Screenshot Options](https://playwright.dev/java/docs/api/class-page#page-screenshot) - Configuration
- [Best Practices](https://playwright.dev/java/docs/best-practices) - Visual testing tips

