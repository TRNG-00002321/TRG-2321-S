# Exercise 2: Playwright vs Selenium Comparison

## Objective

Implement the same test scenario in both Playwright and Selenium to compare the experience, code structure, and capabilities of each framework.

## Learning Goals

- Directly compare Playwright and Selenium syntax
- Experience Playwright's auto-wait vs Selenium's explicit waits
- Understand when to choose each framework
- Document the differences in approach

## Time Estimate

45 minutes

---

## Core Tasks

### Task 1: Define the Test Scenario (5 minutes)

Both implementations will test the same scenario:

**Scenario: Complete Login and Secure Page Verification**
1. Navigate to login page
2. Enter valid credentials
3. Click login button
4. Wait for navigation to secure area
5. Verify welcome message displayed
6. Click logout button
7. Verify returned to login page
8. Verify logout message displayed

### Task 2: Selenium Implementation (15 minutes)

Create `src/test/java/com/training/comparison/SeleniumLoginTest.java`:

```java
package com.training.comparison;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Login test implemented with Selenium.
 */
public class SeleniumLoginTest {
    
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "https://the-internet.herokuapp.com";
    
    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    void testCompleteLoginLogoutFlow() {
        // 1. Navigate to login page
        driver.get(BASE_URL + "/login");
        
        // 2. Enter credentials
        // Must find elements first
        WebElement usernameField = driver.findElement(By.id("username"));
        usernameField.sendKeys("tomsmith");
        
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("SuperSecretPassword!");
        
        // 3. Click login
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();
        
        // 4. Wait for navigation - EXPLICIT WAIT REQUIRED
        wait.until(ExpectedConditions.urlContains("/secure"));
        
        // 5. Verify welcome message - EXPLICIT WAIT REQUIRED
        WebElement flashMessage = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("flash"))
        );
        assertTrue(flashMessage.getText().contains("You logged into a secure area!"),
            "Should see success message");
        
        // 6. Click logout
        WebElement logoutButton = driver.findElement(By.cssSelector("a.button"));
        logoutButton.click();
        
        // 7. Wait and verify returned to login
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"),
            "Should be back on login page");
        
        // 8. Verify logout message - EXPLICIT WAIT REQUIRED
        WebElement logoutMessage = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("flash"))
        );
        assertTrue(logoutMessage.getText().contains("You logged out of the secure area!"),
            "Should see logout message");
    }
    
    @Test
    void testDynamicContentWithWaits() {
        driver.get(BASE_URL + "/dynamic_loading/1");
        
        // Click start
        driver.findElement(By.cssSelector("#start button")).click();
        
        // MUST wait explicitly for hidden element to become visible
        WebElement result = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#finish h4"))
        );
        
        assertEquals("Hello World!", result.getText());
    }
}
```

### Task 3: Playwright Implementation (15 minutes)

Create `src/test/java/com/training/comparison/PlaywrightLoginTest.java`:

```java
package com.training.comparison;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Login test implemented with Playwright.
 */
public class PlaywrightLoginTest {
    
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private static final String BASE_URL = "https://the-internet.herokuapp.com";
    
    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            // .setHeadless(true)
        );
    }
    
    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }
    
    @BeforeEach
    void createContext() {
        context = browser.newContext();
        page = context.newPage();
    }
    
    @AfterEach
    void closeContext() {
        context.close();
    }
    
    @Test
    void testCompleteLoginLogoutFlow() {
        // 1. Navigate to login page
        page.navigate(BASE_URL + "/login");
        
        // 2. Enter credentials - NO element finding needed!
        page.fill("#username", "tomsmith");
        page.fill("#password", "SuperSecretPassword!");
        
        // 3. Click login
        page.click("button[type='submit']");
        
        // 4. Verify navigation - NO explicit wait needed!
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*/secure"));
        
        // 5. Verify welcome message - AUTO-WAITS until visible!
        assertThat(page.locator("#flash")).containsText("You logged into a secure area!");
        
        // 6. Click logout
        page.click("a.button");
        
        // 7. Verify returned to login - AUTO-WAITS!
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*/login"));
        
        // 8. Verify logout message - AUTO-WAITS!
        assertThat(page.locator("#flash")).containsText("You logged out of the secure area!");
    }
    
    @Test
    void testDynamicContentWithAutoWait() {
        page.navigate(BASE_URL + "/dynamic_loading/1");
        
        // Click start
        page.click("#start button");
        
        // NO explicit wait needed - auto-waits for visibility!
        assertThat(page.locator("#finish h4")).hasText("Hello World!");
    }
}
```

### Task 4: Create Comparison Document (10 minutes)

Create `comparison_analysis.md`:

```markdown
# Playwright vs Selenium Comparison Analysis

## Test: Complete Login/Logout Flow

### Line Count Comparison
| Metric | Selenium | Playwright |
|--------|----------|------------|
| Total lines of code | | |
| Setup/teardown lines | | |
| Test logic lines | | |
| Wait-related lines | | |

### Code Comparison

#### Finding Elements
**Selenium:**
```java
WebElement usernameField = driver.findElement(By.id("username"));
usernameField.sendKeys("tomsmith");
```

**Playwright:**
```java
page.fill("#username", "tomsmith");
```

**Analysis:** <!-- YOUR ANALYSIS HERE -->

#### Waiting for Elements
**Selenium:**
```java
WebElement flashMessage = wait.until(
    ExpectedConditions.visibilityOfElementLocated(By.id("flash"))
);
```

**Playwright:**
```java
assertThat(page.locator("#flash")).containsText("message");
```

**Analysis:** <!-- YOUR ANALYSIS HERE -->

### Feature Comparison

| Feature | Selenium | Playwright |
|---------|----------|------------|
| Auto-waiting | ❌ Manual | ✅ Built-in |
| Web-first assertions | ❌ No | ✅ Yes |
| Network interception | ❌ Limited | ✅ Built-in |
| Video recording | ❌ External | ✅ Built-in |
| Tracing | ❌ External | ✅ Built-in |
| Browser installation | Manual | Automated |
| Multiple browsers | ✅ Many | ✅ 3 engines |
| Community size | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |
| Language support | Many | 4 languages |

### When to Choose Each

#### Choose Selenium When:
1. 
2. 
3. 

#### Choose Playwright When:
1. 
2. 
3. 

### Personal Experience Notes

**What surprised me about Playwright:**


**What I missed from Selenium:**


**Overall preference for this scenario:**

```

---

## Definition of Done

- [ ] Selenium test implemented and passing
- [ ] Playwright test implemented and passing
- [ ] Both tests verify the same behavior
- [ ] Comparison document completed
- [ ] Line counts analyzed
- [ ] Feature comparison table completed
- [ ] "When to choose" recommendations written

