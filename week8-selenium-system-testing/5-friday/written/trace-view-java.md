# Playwright Trace Viewer in Java

## Learning Objectives
- Record execution traces during test runs
- Open and navigate Trace Viewer interface
- Analyze test failures using trace data
- Configure trace recording options
- Interpret network, DOM, and action traces
- Integrate tracing with CI/CD pipelines

## Why This Matters

Trace Viewer provides complete test execution visibility:
- Debug flaky tests
- Understand test failures post-mortem
- Analyze timing and performance
- Review network activity
- Inspect DOM state at any point

## The Concept

### What is a Trace?

```
┌─────────────────────────────────────────────────────────────────┐
│                    Trace Contents                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ┌──────────────────────────────────────────────┐              │
│   │  Screenshots at each action                  │              │
│   │  DOM snapshots                               │              │
│   │  Network requests/responses                  │              │
│   │  Console logs                                │              │
│   │  Action timeline                             │              │
│   │  Source code locations                       │              │
│   └──────────────────────────────────────────────┘              │
│                                                                  │
│   All packaged into a single .zip file                          │
│   Viewable in browser-based Trace Viewer                        │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Recording Traces

```java
import com.microsoft.playwright.*;
import java.nio.file.Paths;

public class TraceRecording {
    
    @Test
    void recordTrace() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();
            
            // Start tracing
            context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)    // Capture screenshots
                .setSnapshots(true)      // Capture DOM snapshots
                .setSources(true)        // Include source code
            );
            
            Page page = context.newPage();
            page.navigate("https://example.com");
            page.locator("#search").fill("test query");
            page.locator("#search-btn").click();
            
            // Stop and save trace
            context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace.zip"))
            );
            
            context.close();
            browser.close();
        }
    }
}
```

### Opening Trace Viewer

```bash
# Open trace file
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace trace.zip"

# Or use npx (if Node.js installed)
npx playwright show-trace trace.zip

# Opens in browser at: http://localhost:9323
```

### Trace Viewer Interface

```
┌─────────────────────────────────────────────────────────────────┐
│  Trace Viewer                                            [─][□][×]│
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────┐  ┌─────────────────────────────┐   │
│  │  Actions Timeline       │  │  Screenshot Panel           │   │
│  │  ─────────────────      │  │  ─────────────────          │   │
│  │  ▶ navigate            │  │  [Current page screenshot]  │   │
│  │  ▶ click #search       │  │                             │   │
│  │  ▶ fill "test query"   │  │  Before / After toggle      │   │
│  │  ▶ click #search-btn   │  │                             │   │
│  │  ▶ wait for selector   │  └─────────────────────────────┘   │
│  │                         │                                    │
│  └─────────────────────────┘  ┌─────────────────────────────┐   │
│                               │  Tabs:                       │   │
│  ┌─────────────────────────┐  │  [Call] [Console] [Network] │   │
│  │  Source Code            │  │  [Source]                   │   │
│  │  ─────────────────      │  │                             │   │
│  │  page.locator("#search")│  │  Request details...         │   │
│  │  > .fill("test query");│  │  Response body...           │   │
│  │                         │  │                             │   │
│  └─────────────────────────┘  └─────────────────────────────┘   │
│                                                                  │
│  Timeline: [========●=============]  00:05.234                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Trace Recording Options

```java
public class TraceOptions {
    
    @Test
    void fullTraceRecording() {
        BrowserContext context = browser.newContext();
        
        context.tracing().start(new Tracing.StartOptions()
            // Capture screenshots at each action
            .setScreenshots(true)
            
            // Capture DOM snapshots
            .setSnapshots(true)
            
            // Include source code in trace
            .setSources(true)
            
            // Optional: trace title
            .setTitle("Login Flow Test")
        );
        
        // Run test...
        
        context.tracing().stop(new Tracing.StopOptions()
            .setPath(Paths.get("traces/login-test.zip"))
        );
    }
    
    @Test
    void minimalTrace() {
        BrowserContext context = browser.newContext();
        
        // Screenshots only (smaller file)
        context.tracing().start(new Tracing.StartOptions()
            .setScreenshots(true)
            .setSnapshots(false)
            .setSources(false)
        );
        
        // ...
    }
}
```

### Tracing in Test Lifecycle

```java
public class TracedTest {
    
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
    void startTracing(TestInfo testInfo) {
        context = browser.newContext();
        
        // Start trace for each test
        context.tracing().start(new Tracing.StartOptions()
            .setScreenshots(true)
            .setSnapshots(true)
            .setSources(true)
            .setTitle(testInfo.getDisplayName())
        );
        
        page = context.newPage();
    }
    
    @AfterEach
    void stopTracing(TestInfo testInfo) {
        // Save trace with test name
        String traceName = testInfo.getDisplayName()
            .replaceAll("[^a-zA-Z0-9]", "_") + ".zip";
        
        context.tracing().stop(new Tracing.StopOptions()
            .setPath(Paths.get("traces/" + traceName))
        );
        
        context.close();
    }
    
    @Test
    @DisplayName("User can search products")
    void searchProductsTest() {
        page.navigate("https://example.com");
        page.locator("#search").fill("laptop");
        page.locator("#search-btn").click();
        
        assertThat(page.locator(".results")).isVisible();
    }
    
    @AfterAll
    static void teardown() {
        browser.close();
        playwright.close();
    }
}
```

### Trace on Failure Only

```java
public class TraceOnFailure implements TestWatcher {
    
    private BrowserContext context;
    private boolean traceStarted = false;
    
    @BeforeEach
    void setupContext() {
        context = browser.newContext();
        
        // Always start tracing
        context.tracing().start(new Tracing.StartOptions()
            .setScreenshots(true)
            .setSnapshots(true)
        );
        traceStarted = true;
    }
    
    @Override
    public void testFailed(ExtensionContext ec, Throwable cause) {
        if (traceStarted) {
            // Save trace only on failure
            String traceName = "failure_" + ec.getDisplayName() + ".zip";
            context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("traces/" + traceName))
            );
            System.out.println("Trace saved: traces/" + traceName);
        }
    }
    
    @Override
    public void testSuccessful(ExtensionContext ec) {
        if (traceStarted) {
            // Discard trace on success (no path = not saved)
            context.tracing().stop();
        }
    }
}
```

### Analyzing Traces

**Actions Panel:**
- Step through each action
- See timing for each step
- Identify slow operations

**Screenshot Panel:**
- Before/after screenshots for each action
- Visual state verification
- Spot UI issues

**Network Tab:**
- All HTTP requests/responses
- Request timing
- Response bodies
- Failed requests

**Console Tab:**
- JavaScript console output
- Errors and warnings
- Debug logs

**Source Tab:**
- Test code that caused action
- Line numbers
- Call stack

### CI/CD Integration

```yaml
# GitHub Actions
name: Tests with Traces

on: [push]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          
      - name: Install Playwright
        run: mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
        
      - name: Run tests
        run: mvn test
        
      - name: Upload traces
        if: failure()
        uses: actions/upload-artifact@v3
        with:
          name: playwright-traces
          path: traces/
          retention-days: 7
```

### Viewing Traces from CI

```bash
# Download trace artifact from CI

# View locally
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace downloaded-trace.zip"

# Or view online at trace.playwright.dev
# Upload trace.zip to the website
```

### Trace Chunks for Long Tests

```java
public class TraceChunks {
    
    @Test
    void longTestWithChunks() {
        context.tracing().start(new Tracing.StartOptions()
            .setScreenshots(true)
            .setSnapshots(true)
        );
        
        // Phase 1: Login
        page.navigate("https://example.com/login");
        page.locator("#email").fill("user@example.com");
        page.locator("#password").fill("password");
        page.locator("#submit").click();
        
        // Save chunk 1
        context.tracing().stop(new Tracing.StopOptions()
            .setPath(Paths.get("traces/phase1-login.zip"))
        );
        
        // Start new trace for phase 2
        context.tracing().start(new Tracing.StartOptions()
            .setScreenshots(true)
            .setSnapshots(true)
        );
        
        // Phase 2: Shopping
        page.navigate("https://example.com/products");
        page.locator(".product").first().click();
        page.locator("#add-to-cart").click();
        
        // Save chunk 2
        context.tracing().stop(new Tracing.StopOptions()
            .setPath(Paths.get("traces/phase2-shopping.zip"))
        );
    }
}
```

## Key Takeaways

1. **Traces** capture complete execution history
2. **`context.tracing().start()`** begins recording
3. **`context.tracing().stop()`** saves trace to file
4. **Trace Viewer** provides visual debugging interface
5. **CI/CD integration** preserves traces as artifacts
6. **Trace on failure** saves resources and storage

## Additional Resources

- [Playwright Trace Viewer](https://playwright.dev/java/docs/trace-viewer) - Official documentation
- [Trace API](https://playwright.dev/java/docs/api/class-tracing) - API reference
- [Debugging](https://playwright.dev/java/docs/debug) - Debug strategies
- [Online Viewer](https://trace.playwright.dev) - View traces online

