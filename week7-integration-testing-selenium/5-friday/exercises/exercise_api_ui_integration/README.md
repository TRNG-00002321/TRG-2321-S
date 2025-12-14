# Lab: API and UI Integration Testing

## Overview

**Duration:** 60-90 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Advanced

In this lab, you'll combine API testing (REST Assured) with UI testing (Selenium) to create comprehensive integration tests. This represents true full-stack test automation.

---

## Learning Objectives

By completing this lab, you will:
- Use API calls to set up test data for UI tests
- Validate UI changes via API verification
- Create efficient test flows combining both layers
- Understand when to use API vs UI testing
- Build a hybrid test framework

---

## Prerequisites

- REST Assured exercises completed
- Selenium WebDriver exercises completed
- Understanding of both test approaches

---

## The Scenario

BookHaven's QA strategy requires:
1. Use APIs for fast test data setup
2. Use UI to verify user-facing functionality  
3. Use APIs to verify backend state after UI actions

This approach gives you speed (API) AND confidence (UI).

---

## Core Tasks

### Task 1: Project Setup (10 minutes)

**Create combined project with both dependencies:**

```xml
<dependencies>
    <!-- Selenium -->
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.15.0</version>
    </dependency>
    <dependency>
        <groupId>io.github.bonigarcia</groupId>
        <artifactId>webdrivermanager</artifactId>
        <version>5.6.2</version>
    </dependency>
    
    <!-- REST Assured -->
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <version>5.4.0</version>
        <scope>test</scope>
    </dependency>
    
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Task 2: API Client for Test Data (20 minutes)

**Create `ApiTestDataHelper.java`:**

```java
package com.bookhaven.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

public class ApiTestDataHelper {
    
    private static final String API_BASE_URL = "https://jsonplaceholder.typicode.com";
    
    static {
        RestAssured.baseURI = API_BASE_URL;
    }
    
    /**
     * Create a user via API (simulated)
     */
    public static Map<String, Object> createTestUser(String name, String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("username", email.split("@")[0]);
        
        Response response = given()
            .contentType("application/json")
            .body(userData)
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .extract()
            .response();
        
        Map<String, Object> createdUser = response.jsonPath().getMap("$");
        System.out.println("Created test user via API: " + createdUser.get("id"));
        
        return createdUser;
    }
    
    /**
     * Create a post via API
     */
    public static int createTestPost(String title, String body, int userId) {
        Map<String, Object> postData = new HashMap<>();
        postData.put("title", title);
        postData.put("body", body);
        postData.put("userId", userId);
        
        int postId = given()
            .contentType("application/json")
            .body(postData)
        .when()
            .post("/posts")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .extract()
            .path("id");
        
        System.out.println("Created test post via API with ID: " + postId);
        return postId;
    }
    
    /**
     * Verify post exists via API
     */
    public static void verifyPostExists(int postId) {
        given()
        .when()
            .get("/posts/" + postId)
        .then()
            .statusCode(200)
            .body("id", equalTo(postId));
        
        System.out.println("Verified post " + postId + " exists via API");
    }
    
    /**
     * Delete test data via API
     */
    public static void deletePost(int postId) {
        given()
        .when()
            .delete("/posts/" + postId)
        .then()
            .statusCode(200);
        
        System.out.println("Deleted post " + postId + " via API");
    }
    
    /**
     * Get all posts for a user
     */
    public static int getPostCountForUser(int userId) {
        return given()
            .queryParam("userId", userId)
        .when()
            .get("/posts")
        .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$")
            .size();
    }
}
```

### Task 3: Integrated Test Class (30 minutes)

**Create `ApiUiIntegrationTest.java`:**

```java
package com.bookhaven.tests;

import com.bookhaven.utils.ApiTestDataHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ApiUiIntegrationTest {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }
    
    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    @DisplayName("API setup → UI action → API verification")
    void testApiSetupUiActionApiVerify() {
        // STEP 1: Create test data via API (FAST)
        int postId = ApiTestDataHelper.createTestPost(
            "Integration Test Post",
            "Created via API for UI testing",
            1
        );
        
        // STEP 2: Navigate to UI and interact
        driver.get("https://jsonplaceholder.typicode.com/");
        
        // Verify page loaded
        WebElement heading = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.tagName("h1"))
        );
        assertTrue(heading.getText().contains("JSONPlaceholder"));
        
        // STEP 3: Verify via API that data still exists
        ApiTestDataHelper.verifyPostExists(postId);
        
        // STEP 4: Cleanup via API (FAST)
        ApiTestDataHelper.deletePost(postId);
    }
    
    @Test
    @DisplayName("Verify API and UI show consistent data")
    void testDataConsistency() {
        // Get data from API
        int apiPostCount = given()
            .when()
            .get("https://jsonplaceholder.typicode.com/posts")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$")
            .size();
        
        System.out.println("API reports " + apiPostCount + " posts");
        
        // Navigate to UI
        driver.get("https://jsonplaceholder.typicode.com/");
        
        // The UI should reference the same data source
        // (In a real app, you'd verify counts match)
        WebElement guideLink = driver.findElement(By.linkText("Guide"));
        assertTrue(guideLink.isDisplayed());
        
        // Both API and UI are using same backend
        assertEquals(100, apiPostCount, "API should return 100 posts");
    }
    
    @Test
    @DisplayName("UI form submission → API verification")
    void testUiSubmitApiVerify() {
        // NOTE: JSONPlaceholder is read-only for UI
        // This demonstrates the PATTERN for a real app
        
        // 1. User fills form in UI
        driver.get("https://the-internet.herokuapp.com/login");
        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        // 2. Verify UI shows success
        WebElement flash = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("flash"))
        );
        assertTrue(flash.getText().contains("You logged into"));
        
        // 3. In a real app, you'd verify via API:
        // - User session created
        // - Login event logged
        // - User preferences loaded
        
        // Example API verification pattern:
        // Response sessionResponse = given()
        //     .cookie("session_id", driver.manage().getCookieNamed("session").getValue())
        //     .when()
        //     .get("/api/session")
        //     .then()
        //     .statusCode(200)
        //     .body("user", equalTo("tomsmith"))
        //     .extract().response();
    }
    
    @Test
    @DisplayName("Bulk API setup for UI testing")
    void testBulkApiSetup() {
        // Create multiple test records via API (FAST)
        int userId = 99;
        
        // This would be much slower via UI!
        for (int i = 1; i <= 5; i++) {
            ApiTestDataHelper.createTestPost(
                "Test Post " + i,
                "Bulk created for testing",
                userId
            );
        }
        
        // Verify count via API
        int postCount = ApiTestDataHelper.getPostCountForUser(userId);
        // Note: JSONPlaceholder doesn't persist, so this won't reflect our creates
        // In a real app, you'd verify: assertEquals(5, postCount);
        
        // Now UI tests can assume data exists
        driver.get("https://jsonplaceholder.typicode.com/");
        
        // UI tests proceed with pre-populated data...
    }
}
```

### Task 4: Test Strategy Pattern (20 minutes)

**Create `HybridTestStrategy.java`:**

```java
package com.bookhaven.strategy;

/**
 * Hybrid Test Strategy: Combining API and UI Testing
 * 
 * USE API FOR:
 * - Test data setup/teardown
 * - Verification of backend state
 * - Performance-sensitive operations
 * - Testing business logic
 * - Negative testing edge cases
 * 
 * USE UI FOR:
 * - User workflow validation
 * - Visual verification
 * - JavaScript-dependent features
 * - Cross-browser testing
 * - Accessibility testing
 */
public class HybridTestStrategy {
    
    /**
     * Example: E-commerce purchase flow
     * 
     * TRADITIONAL UI-ONLY APPROACH (SLOW):
     * 1. UI: Navigate to site
     * 2. UI: Create account
     * 3. UI: Browse products
     * 4. UI: Add to cart
     * 5. UI: Checkout
     * 6. UI: Verify order confirmation
     * 
     * HYBRID APPROACH (FAST + THOROUGH):
     * 1. API: Create user account
     * 2. API: Add products to cart
     * 3. UI: Navigate directly to cart (with auth cookie)
     * 4. UI: Complete checkout flow
     * 5. API: Verify order in database
     * 6. API: Cleanup test data
     */
    
    /**
     * Pattern: API Setup → UI Action → API Verify
     */
    public void examplePurchaseFlow() {
        // API: Create authenticated session
        // String authToken = ApiHelper.authenticateUser("test@example.com", "password");
        
        // API: Pre-populate cart
        // ApiHelper.addToCart(authToken, "PRODUCT-001", 2);
        // ApiHelper.addToCart(authToken, "PRODUCT-002", 1);
        
        // UI: Test checkout experience
        // driver.manage().addCookie(new Cookie("auth", authToken));
        // driver.get(baseUrl + "/cart");
        // cartPage.proceedToCheckout();
        // checkoutPage.fillShippingDetails(...);
        // checkoutPage.completeOrder();
        
        // API: Verify order created correctly
        // Order order = ApiHelper.getLatestOrder(authToken);
        // assertEquals(3, order.getItemCount());
        // assertEquals("COMPLETED", order.getStatus());
        
        // API: Cleanup
        // ApiHelper.deleteOrder(order.getId());
        // ApiHelper.deleteUser(authToken);
    }
}
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Combined project with Selenium + REST Assured
- [ ] ApiTestDataHelper class implemented
- [ ] API setup → UI action → API verify test
- [ ] Data consistency test
- [ ] Bulk API setup test
- [ ] Hybrid strategy documented
- [ ] All tests passing

---

## When to Use Each Approach

| Scenario | Use API | Use UI | Use Both |
|----------|---------|--------|----------|
| Create test data | ✅ | ❌ | |
| Test user workflow | ❌ | ✅ | |
| Verify database state | ✅ | ❌ | |
| Test visual elements | ❌ | ✅ | |
| Login for other tests | ✅ | | |
| Test login form itself | | ✅ | |
| E2E purchase flow | | | ✅ |
| Performance testing | ✅ | ❌ | |

---

## Submission Checklist

| Item | Completed |
|------|-----------|
| Project with both dependencies | ☐ |
| ApiTestDataHelper class | ☐ |
| API setup test | ☐ |
| UI action test | ☐ |
| API verification test | ☐ |
| Hybrid pattern documented | ☐ |
| All tests passing | ☐ |

---

## Additional Resources

- Week 7: REST Assured content (Tuesday)
- Week 7: Selenium content (Wednesday-Friday)
- [Test Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html)

