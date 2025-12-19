# Getting Started with Playwright in Java

## Learning Objectives
- Set up a Playwright project with Maven
- Understand Playwright dependencies and configuration
- Write your first Playwright test
- Navigate the Playwright object model (Browser, BrowserContext, Page)
- Understand the Playwright lifecycle

## Why This Matters

Getting the foundation right ensures:
- Clean project structure from the start
- Proper dependency management
- Understanding of core concepts
- Effective test development workflow

## The Concept

### Project Setup with Maven

**pom.xml:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>playwright-tests</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <playwright.version>1.40.0</playwright.version>
        <junit.version>5.10.0</junit.version>
    </properties>

    <dependencies>
        <!-- Playwright -->
        <dependency>
            <groupId>com.microsoft.playwright</groupId>
            <artifactId>playwright</artifactId>
            <version>${playwright.version}</version>
        </dependency>

        <!-- JUnit 5 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.1</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### Installing Browser Binaries

After adding dependencies, install browsers:

```bash
# Using Maven exec plugin
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"

# Or install specific browsers
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install firefox"
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install webkit"
```

### Project Structure

```
playwright-project/
├── pom.xml
├── src/
│   ├── main/java/
│   │   └── com/example/
│   │       └── pages/           # Page objects
│   │           ├── LoginPage.java
│   │           └── DashboardPage.java
│   └── test/java/
│       └── com/example/
│           ├── tests/           # Test classes
│           │   ├── LoginTests.java
│           │   └── DashboardTests.java
│           └── base/            # Base classes
│               └── BaseTest.java
└── playwright.config            # Optional config
```

### Understanding the Object Model

```
┌─────────────────────────────────────────────────────────────────┐
│                    Playwright Object Model                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Playwright                                                     │
│   │   Entry point to all Playwright functionality               │
│   │                                                              │
│   └── Browser                                                    │
│       │   A single browser instance (Chromium, Firefox, WebKit) │
│       │                                                          │
│       └── BrowserContext                                         │
│           │   Isolated browser session (cookies, storage)       │
│           │                                                      │
│           └── Page                                               │
│               │   A single tab/window                           │
│               │                                                  │
│               ├── Locator                                        │
│               │   Element finder with auto-wait                 │
│               │                                                  │
│               └── Frame                                          │
│                   Iframe or frame within page                   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### First Playwright Test

```java
package com.example.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FirstPlaywrightTest {
    
    // Shared across all tests in class
    static Playwright playwright;
    static Browser browser;
    
    // Fresh for each test
    BrowserContext context;
    Page page;
    
    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(false)  // Set true for CI
            .setSlowMo(100)      // Slow down for visibility
        );
    }
    
    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }
    
    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }
    
    @AfterEach
    void closeContext() {
        context.close();
    }
    
    @Test
    void shouldNavigateToPlaywrightDocs() {
        // Navigate
        page.navigate("https://playwright.dev/java/");
        
        // Verify title
        assertThat(page).hasTitle(Pattern.compile("Playwright"));
        
        // Click a link
        page.locator("text=Get started").click();
        
        // Verify URL changed
        assertThat(page).hasURL(Pattern.compile(".*intro"));
    }
    
    @Test
    void shouldSearchDocs() {
        page.navigate("https://playwright.dev/java/");
        
        // Click search button
        page.locator("[aria-label='Search']").click();
        
        // Type in search
        page.locator("[placeholder='Search docs']").fill("locator");
        
        // Verify results appear
        assertThat(page.locator(".DocSearch-Hits")).isVisible();
    }
}
```

### Playwright Lifecycle

```java
// 1. CREATE PLAYWRIGHT INSTANCE
Playwright playwright = Playwright.create();

// 2. LAUNCH BROWSER
Browser browser = playwright.chromium().launch();
// Options: chromium(), firefox(), webkit()

// 3. CREATE BROWSER CONTEXT
BrowserContext context = browser.newContext();
// Isolated session with own cookies, storage

// 4. CREATE PAGE
Page page = context.newPage();
// A single tab in the browser

// 5. INTERACT WITH PAGE
page.navigate("https://example.com");
page.locator("#username").fill("user");
page.locator("#submit").click();

// 6. CLEANUP (in reverse order)
page.close();      // Close tab (optional if closing context)
context.close();   // Close context (cleans up all pages)
browser.close();   // Close browser
playwright.close(); // Release all resources
```

### Try-with-Resources Pattern

```java
// Playwright implements AutoCloseable
public class SafePlaywrightTest {
    
    @Test
    void testWithAutoClose() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            
            page.navigate("https://example.com");
            assertThat(page).hasTitle("Example Domain");
            
        } // All resources automatically closed
    }
}
```

### Base Test Class

```java
package com.example.base;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

public abstract class BaseTest {
    
    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;
    
    @BeforeAll
    static void globalSetup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(System.getenv("CI") != null)
        );
    }
    
    @AfterAll
    static void globalTeardown() {
        browser.close();
        playwright.close();
    }
    
    @BeforeEach
    void setup() {
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1920, 1080)
        );
        page = context.newPage();
    }
    
    @AfterEach
    void teardown(TestInfo testInfo) {
        // Screenshot on failure
        if (testInfo.getTags().contains("screenshot-on-failure")) {
            // Capture logic here
        }
        context.close();
    }
    
    protected void navigateTo(String path) {
        String baseUrl = System.getenv().getOrDefault("BASE_URL", "http://localhost:8080");
        page.navigate(baseUrl + path);
    }
}
```

### Using the Base Test

```java
package com.example.tests;

import com.example.base.BaseTest;
import org.junit.jupiter.api.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginTests extends BaseTest {
    
    @Test
    void shouldLoginSuccessfully() {
        navigateTo("/login");
        
        page.locator("#username").fill("testuser");
        page.locator("#password").fill("password123");
        page.locator("#login-btn").click();
        
        assertThat(page).hasURL(Pattern.compile(".*dashboard"));
        assertThat(page.locator(".welcome")).containsText("Welcome");
    }
    
    @Test
    void shouldShowErrorForInvalidCredentials() {
        navigateTo("/login");
        
        page.locator("#username").fill("invalid");
        page.locator("#password").fill("wrong");
        page.locator("#login-btn").click();
        
        assertThat(page.locator(".error")).isVisible();
        assertThat(page.locator(".error")).containsText("Invalid credentials");
    }
}
```

## Key Takeaways

1. **Maven dependency**: `com.microsoft.playwright:playwright`
2. **Install browsers**: `mvn exec:java -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"`
3. **Object hierarchy**: Playwright → Browser → BrowserContext → Page
4. **Lifecycle**: Create → Use → Close (in reverse order)
5. **Try-with-resources** ensures proper cleanup
6. **Base test class** promotes code reuse

## Additional Resources

- [Playwright Java Installation](https://playwright.dev/java/docs/intro) - Official setup guide
- [Playwright Java API](https://playwright.dev/java/docs/api/class-playwright) - Complete API reference
- [Playwright Examples](https://github.com/microsoft/playwright-java/tree/main/examples) - Official examples

