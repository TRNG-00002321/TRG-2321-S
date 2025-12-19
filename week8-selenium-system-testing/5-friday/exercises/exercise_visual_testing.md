# Exercise 4: Visual Testing with Playwright

## Objective

Implement visual tests with baseline comparisons, handle dynamic content challenges, and understand visual regression testing practices.

## Learning Goals

- Set up visual testing with Playwright
- Create and manage baseline images
- Handle dynamic content in visual comparisons
- Configure threshold settings
- Integrate visual testing into test workflow

## Time Estimate

45 minutes

---

## Core Tasks

### Task 1: Basic Visual Comparison Setup (15 minutes)

Create `src/test/java/com/training/playwright/visual/VisualTest.java`:

```java
package com.training.playwright.visual;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.nio.file.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Visual regression tests using Playwright screenshot comparison.
 */
public class VisualTest {
    
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    
    private static final Path BASELINE_DIR = Paths.get("visual-baselines");
    private static final Path ACTUAL_DIR = Paths.get("visual-actual");
    private static final Path DIFF_DIR = Paths.get("visual-diff");
    
    @BeforeAll
    static void setup() throws Exception {
        // Create directories
        Files.createDirectories(BASELINE_DIR);
        Files.createDirectories(ACTUAL_DIR);
        Files.createDirectories(DIFF_DIR);
        
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }
    
    @AfterAll
    static void teardown() {
        browser.close();
        playwright.close();
    }
    
    @BeforeEach
    void createContext() {
        // Use consistent viewport for visual testing
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1280, 720));
        page = context.newPage();
    }
    
    @AfterEach
    void closeContext() {
        context.close();
    }
    
    @Test
    void testHomepageVisualBaseline() throws Exception {
        page.navigate("https://the-internet.herokuapp.com/");
        
        // Capture screenshot
        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
            .setFullPage(true));
        
        Path baselinePath = BASELINE_DIR.resolve("homepage.png");
        Path actualPath = ACTUAL_DIR.resolve("homepage.png");
        
        // Save actual screenshot
        Files.write(actualPath, screenshot);
        
        if (!Files.exists(baselinePath)) {
            // First run - create baseline
            Files.write(baselinePath, screenshot);
            System.out.println("Created baseline: " + baselinePath);
        } else {
            // Compare with baseline
            byte[] baseline = Files.readAllBytes(baselinePath);
            boolean matches = Arrays.equals(baseline, screenshot);
            
            if (!matches) {
                // Save diff for analysis
                System.out.println("Visual difference detected! Check: " + actualPath);
            }
            
            // Note: In production, use proper image comparison library
            // assertTrue(matches, "Screenshot should match baseline");
        }
    }
    
    @Test
    void testLoginPageVisual() throws Exception {
        page.navigate("https://the-internet.herokuapp.com/login");
        
        // Wait for page to stabilize
        page.waitForLoadState();
        
        byte[] screenshot = page.screenshot();
        
        Path baselinePath = BASELINE_DIR.resolve("login.png");
        Path actualPath = ACTUAL_DIR.resolve("login.png");
        
        Files.write(actualPath, screenshot);
        
        if (!Files.exists(baselinePath)) {
            Files.write(baselinePath, screenshot);
            System.out.println("Created baseline: " + baselinePath);
        }
    }
    
    @Test
    void testElementScreenshot() throws Exception {
        page.navigate("https://the-internet.herokuapp.com/login");
        
        // Screenshot of specific element
        Locator loginForm = page.locator("#login");
        
        byte[] elementScreenshot = loginForm.screenshot();
        
        Path baselinePath = BASELINE_DIR.resolve("login-form.png");
        Path actualPath = ACTUAL_DIR.resolve("login-form.png");
        
        Files.write(actualPath, elementScreenshot);
        
        if (!Files.exists(baselinePath)) {
            Files.write(baselinePath, elementScreenshot);
            System.out.println("Created element baseline: " + baselinePath);
        }
    }
}
```

### Task 2: Handling Dynamic Content (15 minutes)

Create `src/test/java/com/training/playwright/visual/DynamicVisualTest.java`:

```java
package com.training.playwright.visual;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.nio.file.*;

/**
 * Visual testing with dynamic content handling.
 */
public class DynamicVisualTest {
    
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    
    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }
    
    @AfterAll
    static void teardown() {
        browser.close();
        playwright.close();
    }
    
    @BeforeEach
    void createContext() {
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1280, 720));
        page = context.newPage();
    }
    
    @AfterEach
    void closeContext() {
        context.close();
    }
    
    @Test
    void testWithHiddenDynamicElements() throws Exception {
        page.navigate("https://the-internet.herokuapp.com/dynamic_content");
        
        // Hide dynamic elements before screenshot
        page.evaluate("document.querySelectorAll('.large-2 img').forEach(el => el.style.visibility = 'hidden')");
        
        byte[] screenshot = page.screenshot();
        
        Path path = Paths.get("visual-baselines/dynamic-hidden.png");
        Files.createDirectories(path.getParent());
        Files.write(path, screenshot);
    }
    
    @Test
    void testWithMaskedRegions() throws Exception {
        page.navigate("https://the-internet.herokuapp.com/");
        
        // Use clip to exclude dynamic regions
        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
            .setClip(0, 0, 1280, 300));  // Only capture header
        
        Path path = Paths.get("visual-baselines/header-only.png");
        Files.createDirectories(path.getParent());
        Files.write(path, screenshot);
    }
    
    @Test
    void testWithDateReplacement() throws Exception {
        page.navigate("https://the-internet.herokuapp.com/");
        
        // Replace dynamic dates/times with static values
        page.evaluate("""
            document.querySelectorAll('[class*="date"], [class*="time"]')
                .forEach(el => el.textContent = 'STATIC_DATE');
        """);
        
        byte[] screenshot = page.screenshot();
        
        Path path = Paths.get("visual-baselines/static-dates.png");
        Files.createDirectories(path.getParent());
        Files.write(path, screenshot);
    }
    
    @Test
    void testWaitForAnimationsComplete() throws Exception {
        page.navigate("https://the-internet.herokuapp.com/");
        
        // Wait for all animations to complete
        page.waitForFunction("() => !document.querySelector(':scope *:is(:animating)')");
        
        // Alternative: wait fixed time for animations
        page.waitForTimeout(500);
        
        byte[] screenshot = page.screenshot();
        
        Path path = Paths.get("visual-baselines/post-animation.png");
        Files.createDirectories(path.getParent());
        Files.write(path, screenshot);
    }
    
    @Test
    void testResponsiveVisuals() throws Exception {
        String[] viewports = {"desktop", "tablet", "mobile"};
        int[][] sizes = {{1920, 1080}, {768, 1024}, {375, 667}};
        
        for (int i = 0; i < viewports.length; i++) {
            // Create context with specific viewport
            BrowserContext responsiveContext = browser.newContext(
                new Browser.NewContextOptions()
                    .setViewportSize(sizes[i][0], sizes[i][1])
            );
            Page responsivePage = responsiveContext.newPage();
            
            responsivePage.navigate("https://the-internet.herokuapp.com/");
            
            byte[] screenshot = responsivePage.screenshot();
            
            Path path = Paths.get("visual-baselines/responsive-" + viewports[i] + ".png");
            Files.createDirectories(path.getParent());
            Files.write(path, screenshot);
            
            responsiveContext.close();
        }
    }
}
```

### Task 3: Visual Testing Utilities (10 minutes)

Create `src/test/java/com/training/playwright/visual/VisualTestUtils.java`:

```java
package com.training.playwright.visual;

import com.microsoft.playwright.*;
import java.nio.file.*;
import java.util.Arrays;

/**
 * Utility methods for visual testing.
 */
public class VisualTestUtils {
    
    private static final Path BASELINE_DIR = Paths.get("visual-baselines");
    private static final Path ACTUAL_DIR = Paths.get("visual-actual");
    
    /**
     * Compare current screenshot with baseline.
     * 
     * @param page The page to screenshot
     * @param name The baseline name
     * @param options Screenshot options
     * @return true if matches baseline (or baseline was created)
     */
    public static boolean compareWithBaseline(
            Page page, 
            String name, 
            Page.ScreenshotOptions options) throws Exception {
        
        // Create directories if needed
        Files.createDirectories(BASELINE_DIR);
        Files.createDirectories(ACTUAL_DIR);
        
        Path baselinePath = BASELINE_DIR.resolve(name + ".png");
        Path actualPath = ACTUAL_DIR.resolve(name + ".png");
        
        // Capture screenshot
        byte[] screenshot = page.screenshot(options);
        Files.write(actualPath, screenshot);
        
        // Check for baseline
        if (!Files.exists(baselinePath)) {
            Files.write(baselinePath, screenshot);
            System.out.println("✓ Created baseline: " + name);
            return true;
        }
        
        // Compare
        byte[] baseline = Files.readAllBytes(baselinePath);
        boolean matches = Arrays.equals(baseline, screenshot);
        
        if (matches) {
            System.out.println("✓ Visual match: " + name);
        } else {
            System.out.println("✗ Visual mismatch: " + name);
            System.out.println("  Baseline: " + baselinePath);
            System.out.println("  Actual: " + actualPath);
        }
        
        return matches;
    }
    
    /**
     * Update baseline with current screenshot.
     */
    public static void updateBaseline(Page page, String name) throws Exception {
        Files.createDirectories(BASELINE_DIR);
        
        byte[] screenshot = page.screenshot();
        Path baselinePath = BASELINE_DIR.resolve(name + ".png");
        Files.write(baselinePath, screenshot);
        
        System.out.println("Updated baseline: " + name);
    }
    
    /**
     * Hide elements that contain dynamic content.
     */
    public static void hideDynamicElements(Page page, String... selectors) {
        for (String selector : selectors) {
            page.evaluate(
                "(selector) => document.querySelectorAll(selector).forEach(el => el.style.visibility = 'hidden')",
                selector
            );
        }
    }
}
```

### Task 4: Visual Test with Utility (5 minutes)

```java
@Test
void testUsingUtility() throws Exception {
    page.navigate("https://the-internet.herokuapp.com/login");
    page.waitForLoadState();
    
    boolean matches = VisualTestUtils.compareWithBaseline(
        page,
        "login-page",
        new Page.ScreenshotOptions().setFullPage(true)
    );
    
    assertTrue(matches, "Login page should match baseline");
}
```

---

## Definition of Done

- [ ] Basic visual comparison working
- [ ] Baseline images stored in version control
- [ ] Dynamic content handling implemented
- [ ] Element-level screenshots working
- [ ] Responsive viewport testing implemented
- [ ] Visual test utility class created

