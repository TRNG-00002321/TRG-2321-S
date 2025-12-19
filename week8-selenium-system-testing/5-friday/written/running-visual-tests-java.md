# Running Visual Tests in Playwright Java

## Learning Objectives
- Execute visual tests locally and in CI/CD
- Update baseline images when designs change
- Interpret visual test results and differences
- Handle visual test failures appropriately
- Integrate visual testing into test workflows

## Why This Matters

Running visual tests effectively:
- Catches visual regressions early
- Maintains UI quality standards
- Documents expected appearance
- Streamlines design review process
- Prevents production visual bugs

## The Concept

### Running Visual Tests Locally

```bash
# Run all visual tests
mvn test -Dtest=VisualTests

# Run specific visual test
mvn test -Dtest=VisualTests#homepageVisualTest

# Run with visible browser for debugging
mvn test -Dtest=VisualTests -Dplaywright.headless=false
```

### First Run: Creating Baselines

```java
public class VisualTestFirstRun {
    
    @Test
    void createBaseline() {
        page.navigate("https://example.com");
        
        // First run: Creates baseline automatically
        // File saved to: test-snapshots/<TestClass>/<testName>-<browser>-<platform>.png
        assertThat(page).hasScreenshot("homepage.png");
        
        // Output: Screenshot saved: homepage-chromium-win32.png
    }
}
```

```
First run output:
────────────────────────────────────────
PASSED: createBaseline
  Screenshot saved as baseline:
  test-snapshots/VisualTestFirstRun/homepage-chromium-win32.png
────────────────────────────────────────
```

### Subsequent Runs: Comparing Against Baseline

```java
@Test
void compareAgainstBaseline() {
    page.navigate("https://example.com");
    
    // Compares against existing baseline
    assertThat(page).hasScreenshot("homepage.png");
    
    // If match: Test passes
    // If mismatch: Test fails with diff image
}
```

### Updating Baselines

When design intentionally changes:

```bash
# Update all baselines
mvn test -Dplaywright.updateSnapshots=all

# Update specific test baselines
mvn test -Dtest=VisualTests#homepageVisualTest -Dplaywright.updateSnapshots=all

# Update only missing baselines
mvn test -Dplaywright.updateSnapshots=missing
```

**Programmatic update:**

```java
public class BaselineUpdate {
    
    @Test
    void updateBaseline() {
        page.navigate("https://example.com");
        
        // Force baseline update in code
        // Usually controlled via environment variable instead
        if (Boolean.getBoolean("updateBaselines")) {
            Path baseline = Paths.get("test-snapshots/homepage.png");
            byte[] screenshot = page.screenshot();
            Files.write(baseline, screenshot);
        } else {
            assertThat(page).hasScreenshot("homepage.png");
        }
    }
}
```

### Interpreting Test Results

**Successful comparison:**

```
✓ homepageVisualTest (1.2s)
  Screenshots match baseline
```

**Failed comparison:**

```
✗ homepageVisualTest (1.5s)
  AssertionError: Screenshot comparison failed
  
  Expected: test-snapshots/VisualTests/homepage-chromium-win32.png
  Actual:   test-results/VisualTests/homepage-actual.png
  Diff:     test-results/VisualTests/homepage-diff.png
  
  Pixels differing: 1,234 (0.06%)
  
  To update baseline, run with: -Dplaywright.updateSnapshots=all
```

### Viewing Diff Images

```
test-results/
└── VisualTests/
    ├── homepage-actual.png     # Current screenshot
    ├── homepage-expected.png   # Baseline copy
    └── homepage-diff.png       # Highlighted differences
```

**Diff image interpretation:**
- Pink/Red areas: Changed pixels
- Unchanged areas: Original image (dimmed)

### Handling Visual Test Failures

```java
public class VisualTestFailureHandling {
    
    @Test
    void handleVisualFailure() {
        page.navigate("https://example.com");
        
        try {
            assertThat(page).hasScreenshot("homepage.png");
        } catch (AssertionError e) {
            // Capture additional context
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get("debug-screenshots/failure-full-page.png"))
                .setFullPage(true)
            );
            
            // Log page state
            System.out.println("Page URL: " + page.url());
            System.out.println("Page title: " + page.title());
            
            // Re-throw to fail the test
            throw e;
        }
    }
}
```

### CI/CD Integration

**GitHub Actions example:**

```yaml
name: Visual Tests

on: [push, pull_request]

jobs:
  visual-tests:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          
      - name: Install Playwright
        run: mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
        
      - name: Run Visual Tests
        run: mvn test -Dtest=VisualTests
        
      - name: Upload diff artifacts
        if: failure()
        uses: actions/upload-artifact@v3
        with:
          name: visual-test-diffs
          path: test-results/
          
      - name: Upload baseline snapshots
        uses: actions/upload-artifact@v3
        with:
          name: baseline-snapshots
          path: test-snapshots/
```

**Jenkins Pipeline:**

```groovy
pipeline {
    agent any
    
    stages {
        stage('Visual Tests') {
            steps {
                sh 'mvn test -Dtest=VisualTests'
            }
            post {
                failure {
                    archiveArtifacts artifacts: 'test-results/**/*.png'
                }
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        reportDir: 'test-results',
                        reportFiles: 'index.html',
                        reportName: 'Visual Test Report'
                    ])
                }
            }
        }
    }
}
```

### Running Across Browsers

```java
@ParameterizedTest
@ValueSource(strings = {"chromium", "firefox", "webkit"})
void crossBrowserVisualTest(String browserType) {
    BrowserType type;
    switch (browserType) {
        case "chromium": type = playwright.chromium(); break;
        case "firefox": type = playwright.firefox(); break;
        case "webkit": type = playwright.webkit(); break;
        default: throw new IllegalArgumentException();
    }
    
    try (Browser browser = type.launch()) {
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1920, 1080)
        );
        Page page = context.newPage();
        page.navigate("https://example.com");
        
        // Browser-specific baseline: homepage-<browser>-<platform>.png
        assertThat(page).hasScreenshot("homepage.png");
    }
}
```

### Visual Test Workflow

```
┌─────────────────────────────────────────────────────────────────┐
│              Visual Testing Workflow                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Development:                                                    │
│  1. Create feature branch                                       │
│  2. Implement UI changes                                        │
│  3. Run visual tests locally                                    │
│  4. Update baselines if intentional: mvn test -DupdateSnapshots │
│  5. Commit updated baselines with code                          │
│                                                                  │
│  PR Review:                                                      │
│  1. CI runs visual tests                                        │
│  2. Review baseline changes in PR diff                          │
│  3. Approve visual changes                                      │
│                                                                  │
│  Failure Investigation:                                          │
│  1. Download diff artifacts from CI                             │
│  2. Compare expected vs actual                                  │
│  3. Determine if regression or intentional change               │
│  4. Fix bug OR update baseline                                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Best Practices for Running Visual Tests

```java
public class VisualTestBestPractices {
    
    // 1. Consistent viewport
    @BeforeEach
    void setup() {
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1920, 1080)
            .setDeviceScaleFactor(1)
        );
    }
    
    // 2. Wait for stable state
    @Test
    void waitBeforeCapture() {
        page.navigate("https://example.com");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Wait for animations
        page.waitForTimeout(500);
        
        assertThat(page).hasScreenshot("stable.png",
            new PageAssertions.HasScreenshotOptions()
                .setAnimations(ScreenshotAnimations.DISABLED)
        );
    }
    
    // 3. Isolate tests
    @Test
    void isolatedVisualTest() {
        // Each test gets fresh context
        // No shared state between visual tests
    }
    
    // 4. Meaningful names
    @Test
    void loginPage_darkMode_mobile() {
        // Name describes: page, state, device
        assertThat(page).hasScreenshot("login-dark-mobile.png");
    }
    
    // 5. Version control baselines
    // Commit test-snapshots/ to git
    // Review baseline changes in PRs
}
```

## Key Takeaways

1. **First run** creates baseline automatically
2. **`-DupdateSnapshots=all`** updates baselines
3. **Diff images** show exact pixel differences
4. **CI/CD integration** catches regressions automatically
5. **Commit baselines** to version control
6. **Review baseline changes** in pull requests

## Additional Resources

- [Playwright Visual Comparisons](https://playwright.dev/java/docs/test-snapshots) - Documentation
- [CI/CD Integration](https://playwright.dev/java/docs/ci) - Pipeline setup
- [Test Configuration](https://playwright.dev/java/docs/test-configuration) - Options reference

