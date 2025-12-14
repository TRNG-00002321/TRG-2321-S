# Interview Questions: Week 7 - Integration Testing, Selenium

**"From API to UI: Mastering Full-Stack Test Automation"**

This question bank prepares you for technical interviews covering API testing with Postman, REST Assured, Python requests, JMeter performance testing, and Selenium WebDriver automation.

---

## Beginner (Foundational)

### Q1: What is an API, and why is API testing important in the test pyramid?

**Keywords:** Interface, Contract, Middle Layer, Speed, Stability

<details>
<summary>Click to Reveal Answer</summary>

An **API (Application Programming Interface)** is a contract that defines how software components interact with each other. It allows systems to communicate without knowing internal implementation details.

API testing is important because it occupies the **middle layer** of the test pyramid, providing:
- **More coverage than unit tests** by testing integrated components
- **More stability than UI tests** by avoiding browser complexity
- **Faster execution** than UI tests (milliseconds vs seconds)
- **Better cost-effectiveness** by balancing coverage and maintenance cost

The recommended distribution is 60-70% unit tests, 20-30% API tests, and 5-10% UI tests.
</details>

---

### Q2: Explain the difference between REST and SOAP APIs.

**Keywords:** Architectural Style, Protocol, JSON, XML, Lightweight

<details>
<summary>Click to Reveal Answer</summary>

**REST (Representational State Transfer):**
- An **architectural style**, not a strict protocol
- Supports multiple data formats (JSON, XML, HTML, plain text)
- **Lightweight and faster** performance
- Uses standard HTTP methods (GET, POST, PUT, DELETE)
- Stateless communication

**SOAP (Simple Object Access Protocol):**
- A **strict protocol** with defined standards
- **XML-only** message format
- Built-in security (WS-Security) and transaction support
- Uses WSDL for contract definition
- More rigid but offers enterprise features

REST has become the industry standard due to its simplicity, while SOAP remains in legacy enterprise systems requiring advanced security features.
</details>

---

### Q3: What is the purpose of environments in Postman?

**Keywords:** Variables, Context Switching, Dev/Staging/Production, Reusability

<details>
<summary>Click to Reveal Answer</summary>

Postman environments allow you to store **environment-specific variables** that change between contexts (development, staging, production). This enables:

- **Context switching** without modifying requests—just select a different environment
- **Variable reusability** across multiple requests using `{{variableName}}` syntax
- **Sensitive data separation**—Current Value stays local while Initial Value is shared
- **Team collaboration**—share environment configurations without exposing secrets

Example: Define `{{baseUrl}}` as `https://dev.api.example.com` in dev environment and `https://api.example.com` in production, then use the same requests for both.
</details>

---

### Q4: What does REST Assured's Given-When-Then syntax represent?

**Keywords:** BDD, Preconditions, Action, Verification, Fluent API

<details>
<summary>Click to Reveal Answer</summary>

REST Assured uses **Behavior-Driven Development (BDD)** syntax:

- **`given()`** - Set up preconditions: headers, authentication, request body, parameters
- **`when()`** - Define the action: HTTP method and endpoint
- **`then()`** - Verify the outcome: status codes, response body, headers

This **fluent API** design makes tests readable and self-documenting:

```java
given()
    .header("Authorization", "Bearer token")
    .body(requestBody)
.when()
    .post("/users")
.then()
    .statusCode(201)
    .body("name", equalTo("John"));
```

The syntax mirrors natural language, making tests understandable to non-technical stakeholders.
</details>

---

### Q5: What is a Thread Group in JMeter, and what are its key configuration settings?

**Keywords:** Virtual Users, Ramp-up, Loop Count, Duration, Concurrent

<details>
<summary>Click to Reveal Answer</summary>

A **Thread Group** represents virtual users (threads) executing your test plan. Each thread simulates an independent user making requests concurrently.

**Key configuration settings:**
- **Number of Threads (users)**: How many virtual users to simulate
- **Ramp-up Period**: Time to start all threads (e.g., 100 users over 60 seconds = 1 user every 0.6 seconds)
- **Loop Count**: How many times each thread executes the test
- **Duration**: Total test duration in seconds

Example: 50 users, 50-second ramp-up, 5-minute duration simulates gradual traffic buildup for a realistic load test.
</details>

---

### Q6: Explain the architecture of Selenium WebDriver.

**Keywords:** Browser Driver, Direct Communication, HTTP Protocol, ChromeDriver/GeckoDriver

<details>
<summary>Click to Reveal Answer</summary>

Selenium WebDriver uses a **three-tier architecture**:

1. **Test Code** → Sends commands via HTTP using the WebDriver protocol
2. **Browser Driver** (ChromeDriver, GeckoDriver, EdgeDriver) → Receives commands, translates them to browser-specific protocols
3. **Browser** → Executes actions and returns results

The communication flow:
```
Test Code → JSON over HTTP → Browser Driver → DevTools Protocol → Browser
```

Each browser requires its own driver because browsers have different internal architectures. The driver acts as a **translator** between the standardized WebDriver API and browser-specific commands.
</details>

---

### Q7: What is the difference between implicit wait and explicit wait in Selenium?

**Keywords:** Global, Specific Condition, WebDriverWait, ExpectedConditions

<details>
<summary>Click to Reveal Answer</summary>

**Implicit Wait:**
- Applied **globally** to all `findElement` calls
- Waits only for **element presence** in DOM
- Set once for the WebDriver instance
- Less precise, can mask real performance issues

**Explicit Wait (WebDriverWait):**
- Applied to **specific elements** with specific conditions
- Waits for conditions like visibility, clickability, text presence
- Uses `ExpectedConditions` class for precise control
- Returns immediately when condition is met

```java
// Implicit (global, less precise)
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

// Explicit (specific, recommended)
new WebDriverWait(driver, Duration.ofSeconds(10))
    .until(ExpectedConditions.elementToBeClickable(By.id("submit")));
```

**Best Practice:** Use explicit waits; avoid mixing implicit and explicit waits.
</details>

---

### Q8: What is the Page Object Model (POM) design pattern, and why is it used?

**Keywords:** Encapsulation, Maintainability, Separation of Concerns, Reusability

<details>
<summary>Click to Reveal Answer</summary>

**Page Object Model (POM)** is a design pattern that creates a class for each web page, encapsulating UI elements and interactions.

**Structure:**
- **Page Objects** contain locators and methods to interact with elements
- **Test Classes** contain test logic and assertions only

**Benefits:**
- **Maintainability**: Change a locator once in the page class, not in every test
- **Readability**: Tests read like user stories: `loginPage.loginAs("user", "pass")`
- **Reusability**: Same page methods used across multiple tests
- **Separation of Concerns**: UI details hidden from test logic

**Key Rule:** Page objects should **not contain assertions**—they provide data/actions; tests make assertions.
</details>

---

### Q9: What XPath function would you use to find an element whose attribute contains a partial value?

**Keywords:** contains(), starts-with(), XPath Functions, Partial Match

**Hint:** Think about dynamic IDs or class names that include a consistent substring.

<details>
<summary>Click to Reveal Answer</summary>

Use the **`contains()`** function to match elements with partial attribute values:

```xpath
//button[contains(@class, 'btn-primary')]
//input[contains(@id, 'user')]
//div[contains(text(), 'Welcome')]
```

Other useful XPath functions:
- **`starts-with()`**: Matches if attribute begins with value
- **`text()`**: Selects by visible text content
- **`normalize-space()`**: Trims whitespace before comparison

`contains()` is especially useful for:
- Dynamic IDs with static portions (`id="user_12345"` → `contains(@id, 'user')`)
- Multiple CSS classes on elements
- Partial text matching
</details>

---

### Q10: What does WebDriverManager do, and why is it preferred over manual driver setup?

**Keywords:** Automatic Download, Version Detection, No Manual Configuration

<details>
<summary>Click to Reveal Answer</summary>

**WebDriverManager** is a library that automatically downloads and configures browser drivers.

**Automatic process:**
1. Detects your installed browser version
2. Downloads the matching driver version
3. Sets up system properties automatically
4. Caches drivers for reuse

**Manual setup (old way):**
```java
System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
WebDriver driver = new ChromeDriver();
```

**WebDriverManager (modern way):**
```java
WebDriverManager.chromedriver().setup();
WebDriver driver = new ChromeDriver();
```

**Benefits:**
- **No version mismatch errors** when browser updates
- **No manual downloads** or path configuration
- **Cross-platform compatibility** out of the box
- **Cleaner CI/CD integration**
</details>

---

## Intermediate (Application)

### Q11: You need to test an API that requires authentication, and the token expires after 1 hour. How would you handle this in Postman for automated test runs?

**Keywords:** Pre-request Script, Token Refresh, pm.sendRequest(), Environment Variables

**Hint:** Consider what runs before each request.

<details>
<summary>Click to Reveal Answer</summary>

Use a **pre-request script** to check token validity and refresh automatically:

```javascript
// In Pre-request Script tab
const tokenExpiry = pm.environment.get("tokenExpiry");
const currentTime = Date.now();

// Check if token is expired or missing
if (!tokenExpiry || currentTime > tokenExpiry) {
    // Make authentication request
    pm.sendRequest({
        url: pm.environment.get("authUrl"),
        method: 'POST',
        header: { 'Content-Type': 'application/json' },
        body: {
            mode: 'raw',
            raw: JSON.stringify({
                username: pm.environment.get("username"),
                password: pm.environment.get("password")
            })
        }
    }, function(err, response) {
        const jsonResponse = response.json();
        pm.environment.set("authToken", jsonResponse.token);
        // Set expiry 5 minutes before actual expiry for safety
        pm.environment.set("tokenExpiry", currentTime + (55 * 60 * 1000));
    });
}
```

This approach enables **fully automated** test execution without manual token updates.
</details>

---

### Q12: Your Selenium test intermittently fails with `StaleElementReferenceException`. What causes this and how do you fix it?

**Keywords:** DOM Update, Re-find Element, Dynamic Content, AJAX

**Hint:** Think about what happens between finding an element and using it.

<details>
<summary>Click to Reveal Answer</summary>

**Cause:** `StaleElementReferenceException` occurs when a WebElement reference points to an element that no longer exists in the current DOM. This happens when:
- Page refreshes after element was found
- AJAX call updates the DOM
- SPA navigation re-renders components
- JavaScript dynamically modifies elements

**Solutions:**

1. **Re-find the element before interaction:**
```java
// Instead of storing element reference
WebElement element = driver.findElement(By.id("btn"));
// Re-find when needed
driver.findElement(By.id("btn")).click();
```

2. **Use explicit waits with retry logic:**
```java
new WebDriverWait(driver, Duration.ofSeconds(10))
    .ignoring(StaleElementReferenceException.class)
    .until(ExpectedConditions.elementToBeClickable(By.id("btn")))
    .click();
```

3. **Wait for DOM stability before interaction:**
```java
wait.until(ExpectedConditions.refreshed(
    ExpectedConditions.elementToBeClickable(By.id("btn"))
));
```

**Never use** `Thread.sleep()` as a fix—use proper wait strategies.
</details>

---

### Q13: How would you combine API testing with UI testing in an integration test? Give a practical example.

**Keywords:** API Setup, UI Validation, Test Data Creation, Full-Stack Testing

<details>
<summary>Click to Reveal Answer</summary>

Combining API and UI testing creates efficient **integration tests** that leverage the speed of APIs for setup while validating the UI displays correctly.

**Pattern: API Setup → UI Validation**

```java
@Test
void testUserCreationAppearsInDashboard() {
    // 1. Create user via API (fast, reliable)
    String userId = given()
        .contentType(ContentType.JSON)
        .body(new User("John Doe", "john@example.com"))
    .when()
        .post("/api/users")
    .then()
        .statusCode(201)
        .extract().path("id");
    
    // 2. Validate user appears in UI
    DashboardPage dashboard = new DashboardPage(driver);
    dashboard.navigateTo();
    dashboard.searchUser("John Doe");
    
    assertTrue(dashboard.isUserDisplayed(userId));
    assertEquals("john@example.com", dashboard.getUserEmail(userId));
}
```

**Benefits:**
- **Faster execution** than creating data through UI
- **Reliable test data** setup (APIs are more stable than UI)
- **Tests the full stack** from backend to frontend
- **Cleaner cleanup** via API calls after tests
</details>

---

### Q14: In Page Object Model, what should methods return when navigation occurs versus when staying on the same page?

**Keywords:** Method Chaining, New Page Object, Return This, Fluent Interface

<details>
<summary>Click to Reveal Answer</summary>

**Navigation to new page → Return new Page Object:**
```java
public DashboardPage clickLogin() {
    loginButton.click();
    wait.until(ExpectedConditions.urlContains("dashboard"));
    return new DashboardPage(driver);  // New page
}

public ForgotPasswordPage clickForgotPassword() {
    forgotPasswordLink.click();
    return new ForgotPasswordPage(driver);  // Different page
}
```

**Same page actions → Return `this` for method chaining:**
```java
public LoginPage enterUsername(String username) {
    usernameField.sendKeys(username);
    return this;  // Stay on same page
}

public LoginPage enterPassword(String password) {
    passwordField.sendKeys(password);
    return this;  // Enable chaining
}
```

**Usage with method chaining:**
```java
DashboardPage dashboard = new LoginPage(driver)
    .enterUsername("user")      // returns this
    .enterPassword("pass")      // returns this
    .clickLogin();              // returns DashboardPage
```

This pattern makes the **navigation flow explicit** and enables **fluent, readable tests**.
</details>

---

## Advanced (Deep Dive)

### Q15: Explain how Selenium 4's W3C WebDriver protocol differs from the older JSON Wire Protocol, and what practical impact this has on test stability.

**Keywords:** W3C Standard, Browser Native Implementation, Protocol Translation, Consistency

<details>
<summary>Click to Reveal Answer</summary>

**JSON Wire Protocol (Selenium 3):**
- Selenium-specific protocol created by the Selenium project
- Each browser vendor implemented it **differently**
- Required **protocol translation** between Selenium and browser drivers
- Led to **inconsistent behavior** across browsers
- Browser updates could break compatibility

**W3C WebDriver Protocol (Selenium 4):**
- **Industry standard** ratified by W3C
- Browser vendors (Google, Mozilla, Microsoft) implement it **natively**
- **Direct communication** without translation layers
- **Consistent behavior** across all browsers
- Part of the browser, not an external adapter

**Practical Impact:**

1. **Improved Stability:**
   - Fewer "works in Chrome, fails in Firefox" issues
   - Browser updates are less likely to break tests
   - More predictable element interactions

2. **Better Error Messages:**
   - Standardized error responses
   - Clearer debugging information

3. **New Capabilities:**
   - Relative locators (`above()`, `below()`, `near()`)
   - Chrome DevTools Protocol integration
   - Better window/tab management: `switchTo().newWindow(WindowType.TAB)`

4. **Future-Proof:**
   - As a W3C standard, long-term browser support is guaranteed
   - New browsers adopt the standard automatically

**Code difference is minimal**—existing tests work, but behavior is more reliable.
</details>

---

## Congratulations! 🎉

You've completed the Interview Question Bank for Week 7. Practice answering these questions out loud to prepare for technical interviews. Focus on:

1. **Clear definitions** for beginner questions
2. **Real-world scenarios** and trade-offs for intermediate questions
3. **Deep technical understanding** for advanced questions

**Topics Covered:**
- API Testing Fundamentals (REST vs SOAP)
- Postman (Environments, Scripts, Collections)
- REST Assured (Given-When-Then syntax, Java integration)
- Python Requests (Session management, error handling)
- Apache JMeter (Thread Groups, Samplers, Listeners)
- Selenium WebDriver Architecture
- XPath and Locator Strategies
- Wait Strategies (Implicit vs Explicit)
- Page Object Model (POM) and Page Factory
- API-UI Integration Testing

Good luck with your interviews!

