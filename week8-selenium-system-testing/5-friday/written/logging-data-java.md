# Logging and Data Capture in Playwright Java

## Learning Objectives
- Capture browser console logs in tests
- Log and intercept network requests/responses
- Implement request/response interception
- Debug tests with Playwright's logging capabilities
- Use Playwright debug mode for troubleshooting

## Why This Matters

Effective logging enables:
- Faster debugging of test failures
- Understanding application behavior
- Capturing API responses for validation
- Network traffic analysis
- Production issue investigation

## The Concept

### Console Log Capture

```java
import com.microsoft.playwright.*;

public class ConsoleLogCapture {
    
    @Test
    void captureConsoleLogs() {
        page.onConsoleMessage(msg -> {
            System.out.println("Console [" + msg.type() + "]: " + msg.text());
            
            // Access log arguments
            for (JSHandle arg : msg.args()) {
                System.out.println("  Arg: " + arg.jsonValue());
            }
        });
        
        page.navigate("https://example.com");
        
        // Trigger console messages via JavaScript
        page.evaluate("console.log('Test message')");
        page.evaluate("console.warn('Warning message')");
        page.evaluate("console.error('Error message')");
    }
    
    @Test
    void filterConsoleLogs() {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        page.onConsoleMessage(msg -> {
            switch (msg.type()) {
                case "error":
                    errors.add(msg.text());
                    break;
                case "warning":
                    warnings.add(msg.text());
                    break;
            }
        });
        
        page.navigate("https://example.com");
        
        // Assert no console errors
        assertTrue(errors.isEmpty(), "Expected no console errors: " + errors);
    }
}
```

### Network Request Logging

```java
public class NetworkLogging {
    
    @Test
    void logAllRequests() {
        page.onRequest(request -> {
            System.out.println(">> Request: " + request.method() + " " + request.url());
            System.out.println("   Headers: " + request.headers());
        });
        
        page.onResponse(response -> {
            System.out.println("<< Response: " + response.status() + " " + response.url());
            System.out.println("   Headers: " + response.headers());
        });
        
        page.navigate("https://example.com");
    }
    
    @Test
    void logApiCalls() {
        List<String> apiCalls = new ArrayList<>();
        
        page.onRequest(request -> {
            if (request.url().contains("/api/")) {
                apiCalls.add(request.method() + " " + request.url());
            }
        });
        
        page.navigate("https://example.com");
        // Interact with page that makes API calls
        
        System.out.println("API calls made: " + apiCalls);
    }
    
    @Test
    void captureResponseBody() {
        page.onResponse(response -> {
            if (response.url().contains("/api/users")) {
                try {
                    String body = response.text();
                    System.out.println("Users API response: " + body);
                } catch (Exception e) {
                    // Binary response
                }
            }
        });
        
        page.navigate("https://example.com/users");
    }
}
```

### Request Interception

```java
public class RequestInterception {
    
    @Test
    void mockApiResponse() {
        // Intercept and mock API calls
        page.route("**/api/users", route -> {
            route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("[{\"id\": 1, \"name\": \"Mock User\"}]")
            );
        });
        
        page.navigate("https://example.com/users");
        // App will receive mocked data
    }
    
    @Test
    void modifyRequest() {
        page.route("**/api/**", route -> {
            // Modify request headers
            Map<String, String> headers = new HashMap<>(route.request().headers());
            headers.put("X-Test-Header", "test-value");
            
            route.resume(new Route.ResumeOptions()
                .setHeaders(headers)
            );
        });
        
        page.navigate("https://example.com");
    }
    
    @Test
    void blockRequests() {
        // Block images for faster tests
        page.route("**/*.{png,jpg,jpeg,gif}", Route::abort);
        
        // Block analytics
        page.route("**/analytics/**", Route::abort);
        page.route("**/tracking/**", Route::abort);
        
        page.navigate("https://example.com");
    }
    
    @Test
    void simulateNetworkError() {
        page.route("**/api/data", route -> {
            route.abort("connectionfailed");
        });
        
        page.navigate("https://example.com");
        // Test error handling
    }
    
    @Test
    void delayResponse() {
        page.route("**/api/slow", route -> {
            // Simulate slow network
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // Handle
            }
            route.resume();
        });
        
        page.navigate("https://example.com/slow-page");
    }
}
```

### Wait for Network Responses

```java
public class WaitForNetwork {
    
    @Test
    void waitForSpecificResponse() {
        // Wait for API response
        Response response = page.waitForResponse(
            "**/api/users",
            () -> page.navigate("https://example.com/users")
        );
        
        System.out.println("Response status: " + response.status());
        System.out.println("Response body: " + response.text());
    }
    
    @Test
    void waitForNetworkIdle() {
        page.navigate("https://example.com", new Page.NavigateOptions()
            .setWaitUntil(WaitUntilState.NETWORKIDLE)
        );
    }
    
    @Test
    void captureXhrResponse() {
        // Wait for XHR response after action
        Response response = page.waitForResponse(
            resp -> resp.url().contains("/api/search") && resp.status() == 200,
            () -> {
                page.locator("#search").fill("query");
                page.locator("#search-btn").click();
            }
        );
        
        String searchResults = response.text();
        System.out.println("Search results: " + searchResults);
    }
}
```

### Debug Mode

```java
public class DebugMode {
    
    @Test
    void runWithDebugger() {
        // Enable Playwright Inspector
        // Set environment variable: PWDEBUG=1
        // Or use: page.pause()
        
        page.navigate("https://example.com");
        
        // Pause execution - opens Playwright Inspector
        page.pause();
        
        // Continue manually in Inspector
        page.locator("#button").click();
    }
    
    public static void main(String[] args) {
        // Run with debug mode
        // Set PWDEBUG=1 before running
        // Or: System.setProperty("playwright.debug", "true");
        
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(1000)  // Slow down for observation
            );
            
            Page page = browser.newPage();
            page.navigate("https://example.com");
            
            // Debug point
            page.pause();
            
            browser.close();
        }
    }
}
```

### Comprehensive Logging Setup

```java
public class ComprehensiveLogging {
    
    private final List<String> consoleMessages = new ArrayList<>();
    private final List<String> networkRequests = new ArrayList<>();
    private final List<String> networkResponses = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();
    
    @BeforeEach
    void setupLogging() {
        // Console logging
        page.onConsoleMessage(msg -> {
            String logEntry = "[" + msg.type() + "] " + msg.text();
            consoleMessages.add(logEntry);
            
            if (msg.type().equals("error")) {
                errors.add(msg.text());
            }
        });
        
        // Page errors
        page.onPageError(error -> {
            errors.add("Page error: " + error);
        });
        
        // Network logging
        page.onRequest(request -> {
            networkRequests.add(request.method() + " " + request.url());
        });
        
        page.onResponse(response -> {
            networkResponses.add(response.status() + " " + response.url());
        });
        
        // Request failures
        page.onRequestFailed(request -> {
            errors.add("Request failed: " + request.url() + 
                      " - " + request.failure());
        });
    }
    
    @AfterEach
    void printLogs(TestInfo testInfo) {
        System.out.println("\n=== Test: " + testInfo.getDisplayName() + " ===");
        
        System.out.println("\nConsole messages:");
        consoleMessages.forEach(System.out::println);
        
        System.out.println("\nNetwork requests:");
        networkRequests.forEach(System.out::println);
        
        System.out.println("\nNetwork responses:");
        networkResponses.forEach(System.out::println);
        
        if (!errors.isEmpty()) {
            System.err.println("\nErrors:");
            errors.forEach(System.err::println);
        }
    }
    
    @Test
    void testWithFullLogging() {
        page.navigate("https://example.com");
        page.locator("#search").fill("test");
        page.locator("#search-btn").click();
        
        // All console, network activity is logged
    }
}
```

### HAR File Recording

```java
public class HarRecording {
    
    @Test
    void recordHarFile() {
        // Record network to HAR file
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
            .setRecordHarPath(Paths.get("network.har"))
            .setRecordHarContent(HarContentPolicy.ATTACH)  // Include response bodies
        );
        
        Page page = context.newPage();
        page.navigate("https://example.com");
        // Perform actions...
        
        // HAR saved when context closes
        context.close();
        // network.har can be opened in browser DevTools
    }
    
    @Test
    void recordHarWithOptions() {
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
            .setRecordHarPath(Paths.get("filtered.har"))
            .setRecordHarUrlFilter(Pattern.compile(".*api.*"))  // Only API calls
            .setRecordHarContent(HarContentPolicy.EMBED)
        );
        
        // ...
    }
}
```

## Key Takeaways

1. **Console capture** via `page.onConsoleMessage()`
2. **Network logging** via `page.onRequest()` and `page.onResponse()`
3. **Request interception** via `page.route()` for mocking
4. **Debug mode** with `page.pause()` or `PWDEBUG=1`
5. **HAR recording** captures complete network activity
6. **Wait for network** to synchronize with API calls

## Additional Resources

- [Playwright Network](https://playwright.dev/java/docs/network) - Network interception
- [Playwright Console](https://playwright.dev/java/docs/api/class-consolemessage) - Console API
- [Playwright Debugging](https://playwright.dev/java/docs/debug) - Debug guide

