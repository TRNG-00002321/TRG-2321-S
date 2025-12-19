# Exercise 1: First Playwright Test

## Objective

Set up a Playwright Java project and write tests that navigate, interact, and assert on a web application, experiencing Playwright's auto-wait and web-first assertions.

## Learning Goals

- Set up Playwright with Maven
- Understand Playwright object hierarchy (Playwright → Browser → Context → Page)
- Experience auto-wait behavior
- Use web-first assertions
- Compare with Selenium patterns

## Time Estimate

45 minutes

---

## Core Tasks

### Task 1: Project Setup (10 minutes)

Create project structure:

```
playwright-exercises/
├── pom.xml
├── src/
│   ├── main/java/
│   └── test/java/
│       └── com/training/playwright/
│           ├── tests/
│           │   ├── FirstPlaywrightTest.java
│           │   └── NavigationTest.java
│           └── pages/
│               └── (for later exercises)
└── README.md
```

**pom.xml:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.training</groupId>
    <artifactId>playwright-exercises</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <playwright.version>1.40.0</playwright.version>
        <junit.version>5.10.0</junit.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>com.microsoft.playwright</groupId>
            <artifactId>playwright</artifactId>
            <version>${playwright.version}</version>
        </dependency>
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
                <version>3.1.2</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### Task 2: First Playwright Test (15 minutes)

Create `src/test/java/com/training/playwright/tests/FirstPlaywrightTest.java`:

```java
package com.training.playwright.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * First Playwright test demonstrating basic operations.
 */
public class FirstPlaywrightTest {
    
    // Shared across all tests in the class
    static Playwright playwright;
    static Browser browser;
    
    // Unique per test for isolation
    BrowserContext context;
    Page page;
    
    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(false)  // Set to true for CI
            .setSlowMo(100));    // Slow down for visibility (remove in production)
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
    void testNavigateToHomepage() {
        // Navigate to page
        page.navigate("https://the-internet.herokuapp.com/");
        
        // Playwright's web-first assertions automatically wait!
        assertThat(page).hasTitle("The Internet");
        
        // Find heading and assert
        Locator heading = page.locator("h1");
        assertThat(heading).containsText("Welcome to the-internet");
    }
    
    @Test
    void testClickNavigationLink() {
        page.navigate("https://the-internet.herokuapp.com/");
        
        // Click link - auto-waits for element to be actionable
        page.click("text=Form Authentication");
        
        // Assert URL changed
        assertThat(page).hasURL("https://the-internet.herokuapp.com/login");
        
        // Assert heading on new page
        assertThat(page.locator("h2")).hasText("Login Page");
    }
    
    @Test
    void testFormInteraction() {
        page.navigate("https://the-internet.herokuapp.com/login");
        
        // TODO: Implement form interaction
        // 1. Fill username field
        // 2. Fill password field
        // 3. Click login button
        // 4. Assert success message
        
        // Fill form - auto-waits for fields
        page.fill("#username", "tomsmith");
        page.fill("#password", "SuperSecretPassword!");
        
        // Click login
        page.click("button[type='submit']");
        
        // Assert redirect and success message
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*/secure"));
        assertThat(page.locator("#flash")).containsText("You logged into");
    }
    
    @Test
    void testAutoWaitBehavior() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");
        
        // Click start - element hidden initially
        page.click("#start button");
        
        // This automatically waits for element to be visible!
        // No explicit wait needed unlike Selenium
        Locator result = page.locator("#finish h4");
        
        // Web-first assertion waits until condition is met
        assertThat(result).hasText("Hello World!");
    }
    
    @Test
    void testLocatorStrategies() {
        page.navigate("https://the-internet.herokuapp.com/login");
        
        // TODO: Demonstrate different locator strategies
        
        // By ID
        Locator byId = page.locator("#username");
        assertThat(byId).isVisible();
        
        // By CSS
        Locator byCss = page.locator("input[name='password']");
        assertThat(byCss).isVisible();
        
        // By text
        Locator byText = page.locator("text=Login");
        assertThat(byText).isVisible();
        
        // By role (accessibility)
        Locator byRole = page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Login"));
        assertThat(byRole).isVisible();
        
        // By placeholder
        Locator byPlaceholder = page.getByPlaceholder("username");
        // This might not exist on this page - just showing the API
    }
}
```

### Task 3: Navigation Tests (10 minutes)

Create `src/test/java/com/training/playwright/tests/NavigationTest.java`:

```java
package com.training.playwright.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Tests demonstrating Playwright navigation features.
 */
public class NavigationTest {
    
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    
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
    void createPage() {
        context = browser.newContext();
        page = context.newPage();
    }
    
    @AfterEach
    void closePage() {
        context.close();
    }
    
    @Test
    void testBackForwardNavigation() {
        // Navigate to home
        page.navigate("https://the-internet.herokuapp.com/");
        String homeUrl = page.url();
        
        // Navigate to another page
        page.click("text=Form Authentication");
        assertThat(page).hasURL("https://the-internet.herokuapp.com/login");
        
        // Go back
        page.goBack();
        assertThat(page).hasURL(homeUrl);
        
        // Go forward
        page.goForward();
        assertThat(page).hasURL("https://the-internet.herokuapp.com/login");
    }
    
    @Test
    void testNewTab() {
        page.navigate("https://the-internet.herokuapp.com/windows");
        
        // TODO: Implement new tab handling
        // Use page.waitForPopup() to handle new tab/window
        
        // Wait for popup and click the link
        Page popup = page.waitForPopup(() -> {
            page.click("text=Click Here");
        });
        
        // Assert popup content
        assertThat(popup).hasTitle("New Window");
        assertThat(popup.locator("h3")).hasText("New Window");
        
        // Close popup
        popup.close();
    }
    
    @Test
    void testMultipleTabs() {
        page.navigate("https://the-internet.herokuapp.com/");
        
        // TODO: Open multiple pages in same context
        Page page2 = context.newPage();
        page2.navigate("https://the-internet.herokuapp.com/login");
        
        // Both pages share context (cookies, localStorage)
        assertThat(page).hasTitle("The Internet");
        assertThat(page2).hasURL("https://the-internet.herokuapp.com/login");
        
        page2.close();
    }
    
    @Test
    void testWaitForNavigation() {
        page.navigate("https://the-internet.herokuapp.com/login");
        
        // Wait for navigation when clicking
        page.fill("#username", "tomsmith");
        page.fill("#password", "SuperSecretPassword!");
        
        // waitForURL waits for navigation to complete
        page.click("button[type='submit']");
        page.waitForURL("**/secure");
        
        assertThat(page.locator("#flash")).containsText("secure area");
    }
}
```

### Task 4: Screenshot Test (10 minutes)

Add screenshot functionality:

```java
@Test
void testCaptureScreenshot() {
    page.navigate("https://the-internet.herokuapp.com/");
    
    // Full page screenshot
    page.screenshot(new Page.ScreenshotOptions()
        .setPath(java.nio.file.Paths.get("screenshots/homepage.png"))
        .setFullPage(true));
    
    // Element screenshot
    Locator heading = page.locator("h1");
    heading.screenshot(new Locator.ScreenshotOptions()
        .setPath(java.nio.file.Paths.get("screenshots/heading.png")));
    
    // Verify files exist
    java.io.File fullPage = new java.io.File("screenshots/homepage.png");
    java.io.File element = new java.io.File("screenshots/heading.png");
    
    Assertions.assertTrue(fullPage.exists(), "Full page screenshot should exist");
    Assertions.assertTrue(element.exists(), "Element screenshot should exist");
}
```

---

## Playwright vs Selenium Quick Reference

| Operation | Selenium | Playwright |
|-----------|----------|------------|
| Navigate | `driver.get(url)` | `page.navigate(url)` |
| Find element | `driver.findElement(By.id("x"))` | `page.locator("#x")` |
| Click | `element.click()` | `page.click("#x")` |
| Type | `element.sendKeys("text")` | `page.fill("#x", "text")` |
| Wait for visible | `WebDriverWait + EC` | Automatic! |
| Assert text | `assertEquals(element.getText(), "x")` | `assertThat(locator).hasText("x")` |
| Screenshot | `driver.getScreenshotAs(...)` | `page.screenshot(...)` |

---

## Definition of Done

- [ ] Project compiles with all dependencies
- [ ] Playwright browsers installed (`playwright install`)
- [ ] FirstPlaywrightTest has 5+ passing tests
- [ ] NavigationTest demonstrates back/forward/new tab
- [ ] Screenshots captured successfully
- [ ] No explicit waits needed (using auto-wait)

---

## Hints

<details>
<summary>Hint: Installing Playwright Browsers</summary>

```bash
# Run this after adding Maven dependencies
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"

# Or install specific browsers
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
```
</details>

<details>
<summary>Hint: Headless Mode for CI</summary>

```java
browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
    .setHeadless(true));  // Run without visible browser
```
</details>

