# Weekly Knowledge Check: Week 7 - Integration Testing, Selenium

**"From API to UI: Mastering Full-Stack Test Automation"**

Test your understanding of API testing with Postman, REST Assured, Python requests, JMeter, and Selenium WebDriver with these practice questions.

---

## Part 1: API Testing Fundamentals & Postman (Monday)

### 1. Which HTTP method should be used to partially update a resource?

- [ ] A) PUT
- [ ] B) POST
- [ ] C) PATCH
- [ ] D) GET

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) PATCH

**Explanation:** PATCH is used for partial modifications to a resource, sending only the fields that need to be changed. PUT replaces the entire resource and requires all fields.

- **Why others are wrong:**
  - A) PUT replaces the entire resource, requiring all fields to be sent
  - B) POST is used to create new resources, not update existing ones
  - D) GET is used to retrieve data without modifications
</details>

---

### 2. In Postman, what is the purpose of a Pre-request Script?

- [ ] A) To validate the response after it is received
- [ ] B) To execute code before a request is sent
- [ ] C) To format the response body
- [ ] D) To save the collection

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) To execute code before a request is sent

**Explanation:** Pre-request scripts are JavaScript code blocks that execute before Postman sends your request. They're used to set variables dynamically, generate test data, or configure request settings.

- **Why others are wrong:**
  - A) Validating responses is done in the Tests tab (Post-response scripts)
  - C) Response formatting is handled by the response viewer, not scripts
  - D) Saving collections is a manual action, not a script function
</details>

---

### 3. What does the HTTP status code 201 indicate?

- [ ] A) Success, no content returned
- [ ] B) Resource not found
- [ ] C) Resource created successfully
- [ ] D) Server error

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Resource created successfully

**Explanation:** HTTP 201 (Created) indicates that the request has been fulfilled and a new resource has been created. It's typically returned after successful POST requests.

- **Why others are wrong:**
  - A) 204 No Content indicates success with no response body
  - B) 404 Not Found indicates the resource doesn't exist
  - D) 5xx codes (500, 502, etc.) indicate server errors
</details>

---

### 4. In Postman, which assertion checks if a response contains specific text?

- [ ] A) `pm.response.to.have.status(200)`
- [ ] B) `pm.expect(pm.response.json().name).to.include("text")`
- [ ] C) `pm.test("check", function(){})`
- [ ] D) `pm.environment.set("key", "value")`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `pm.expect(pm.response.json().name).to.include("text")`

**Explanation:** The `pm.expect().to.include()` assertion checks if a value contains a specified substring. This uses Chai's BDD assertion syntax available in Postman.

- **Why others are wrong:**
  - A) This checks HTTP status code, not content
  - C) This is a test function wrapper, not an assertion itself
  - D) This sets an environment variable, not an assertion
</details>

---

### 5. What is the difference between Initial Value and Current Value in Postman environments?

- [ ] A) There is no difference
- [ ] B) Initial Value is shared with team, Current Value is private to your machine
- [ ] C) Current Value is shared, Initial Value is private
- [ ] D) Initial Value is for production, Current Value is for development

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Initial Value is shared with team, Current Value is private to your machine

**Explanation:** Initial Value is synced and shared when you export or collaborate, making it visible to team members. Current Value stays local on your machine, making it ideal for sensitive data like API keys.

- **Why others are wrong:**
  - A) They have different scopes and visibility
  - C) This is reversed; Initial is shared, Current is private
  - D) Both values can be used in any environment type
</details>

---

### 6. Which Postman dynamic variable generates a unique identifier?

- [ ] A) `{{$timestamp}}`
- [ ] B) `{{$randomInt}}`
- [ ] C) `{{$guid}}`
- [ ] D) `{{$randomEmail}}`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) `{{$guid}}`

**Explanation:** `{{$guid}}` generates a universally unique identifier (UUID) in the format "550e8400-e29b-41d4-a716-446655440000". It's ideal for creating unique IDs in tests.

- **Why others are wrong:**
  - A) `{{$timestamp}}` generates a Unix timestamp (e.g., 1562757107)
  - B) `{{$randomInt}}` generates a random integer between 0-1000
  - D) `{{$randomEmail}}` generates a random email address
</details>

---

### 7. True or False: REST APIs must use XML as the data format.

- [ ] A) True
- [ ] B) False

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) False

**Explanation:** REST APIs support multiple data formats including JSON, XML, HTML, and plain text. JSON is the most common format for REST APIs due to its lightweight nature and JavaScript compatibility. SOAP, not REST, is restricted to XML only.
</details>

---

### 8. What does the `pm.sendRequest()` function do in a pre-request script?

- [ ] A) Sends the main request
- [ ] B) Sends an additional HTTP request before the main request
- [ ] C) Cancels the current request
- [ ] D) Validates the request format

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Sends an additional HTTP request before the main request

**Explanation:** `pm.sendRequest()` allows you to make auxiliary HTTP calls within scripts. This is commonly used to fetch authentication tokens or set up test data before the main request executes.

- **Why others are wrong:**
  - A) The main request is sent automatically after scripts run
  - C) There's no built-in function to cancel requests in scripts
  - D) Request validation is not the purpose of this function
</details>

---

## Part 2: REST Assured, Python Requests & JMeter (Tuesday)

### 9. What is the correct REST Assured syntax to validate a JSON response field?

- [ ] A) `given().body("name", equals("John"))`
- [ ] B) `then().body("name", equalTo("John"))`
- [ ] C) `when().assertThat("name", "John")`
- [ ] D) `expect().json("name", "John")`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `then().body("name", equalTo("John"))`

**Explanation:** REST Assured uses the Given-When-Then BDD syntax. Response validation happens in the `then()` section using `.body()` with JSONPath and Hamcrest matchers like `equalTo()`.

- **Why others are wrong:**
  - A) `given()` is for request setup, not validation
  - C) `when()` is for defining the HTTP action, not assertions
  - D) This is not valid REST Assured syntax
</details>

---

### 10. In Python requests, which method sends a request with automatic JSON serialization?

- [ ] A) `requests.post(url, data={"key": "value"})`
- [ ] B) `requests.post(url, json={"key": "value"})`
- [ ] C) `requests.post(url, body={"key": "value"})`
- [ ] D) `requests.post(url, payload={"key": "value"})`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `requests.post(url, json={"key": "value"})`

**Explanation:** Using `json=` parameter automatically serializes the dictionary to JSON and sets the Content-Type header to `application/json`. The `data=` parameter sends form-encoded data instead.

- **Why others are wrong:**
  - A) `data=` sends form-encoded data (application/x-www-form-urlencoded)
  - C) `body=` is not a valid parameter name
  - D) `payload=` is not a valid parameter name
</details>

---

### 11. What is a Thread Group in JMeter?

- [ ] A) A collection of test results
- [ ] B) A configuration for simulating virtual users
- [ ] C) A type of HTTP sampler
- [ ] D) A listener for displaying graphs

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) A configuration for simulating virtual users

**Explanation:** A Thread Group in JMeter represents virtual users (threads) that execute test plan elements. It configures how many users to simulate, ramp-up time, and iteration count.

- **Why others are wrong:**
  - A) Test results are collected by Listeners
  - C) HTTP Sampler is a separate element for making requests
  - D) Listeners are separate elements for displaying results
</details>

---

### 12. Fill in the blank: In REST Assured, the method to extract a value from the response is _____.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** `.extract().path("fieldName")` or `.extract().as(ClassName.class)`

**Explanation:** The `.extract()` method in REST Assured allows you to retrieve values from responses. Use `.path("fieldName")` for specific values or `.as(Class.class)` to deserialize the entire response to a POJO.

```java
String name = given()
    .when().get("/users/1")
    .then().extract().path("name");
```
</details>

---

### 13. What is the ramp-up period in JMeter?

- [ ] A) The time it takes to complete all requests
- [ ] B) The time to start all threads (virtual users)
- [ ] C) The delay between each request
- [ ] D) The time to generate the report

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) The time to start all threads (virtual users)

**Explanation:** Ramp-up period defines how long JMeter takes to launch all threads. For example, 100 threads with a 100-second ramp-up means JMeter starts 1 new user per second.

- **Why others are wrong:**
  - A) Test duration is controlled by loop count or duration settings
  - C) Delays between requests are controlled by Timers
  - D) Report generation happens after test completion
</details>

---

### 14. In Python requests, how do you check if a response was successful?

- [ ] A) `response.ok`
- [ ] B) `response.success`
- [ ] C) `response.passed`
- [ ] D) `response.valid`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** A) `response.ok`

**Explanation:** The `response.ok` property returns `True` for any 2xx status code, indicating the request was successful. You can also use `response.raise_for_status()` to raise an exception for 4xx/5xx errors.

- **Why others are wrong:**
  - B) `success` is not a valid property
  - C) `passed` is not a valid property
  - D) `valid` is not a valid property
</details>

---

### 15. What does JMeter's Aggregate Report listener show that Summary Report doesn't?

- [ ] A) Error percentage
- [ ] B) Throughput
- [ ] C) Percentile response times (90th, 95th, 99th)
- [ ] D) Average response time

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Percentile response times (90th, 95th, 99th)

**Explanation:** Aggregate Report includes response time percentiles (90th, 95th, 99th), which help identify outliers and worst-case performance. Summary Report shows only average and standard deviation.

- **Why others are wrong:**
  - A) Both reports show error percentage
  - B) Both reports show throughput
  - D) Both reports show average response time
</details>

---

## Part 3: Selenium WebDriver Fundamentals (Wednesday)

### 16. What is the correct order of Selenium WebDriver components when a test runs?

- [ ] A) Browser → Browser Driver → Test Code
- [ ] B) Test Code → Browser Driver → Browser
- [ ] C) Browser Driver → Test Code → Browser
- [ ] D) Test Code → Browser → Browser Driver

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Test Code → Browser Driver → Browser

**Explanation:** Test code sends commands to the browser driver (ChromeDriver, GeckoDriver, etc.), which translates them to browser-specific protocols and communicates with the actual browser.

- **Why others are wrong:**
  - A) Browser doesn't initiate communication
  - C) Browser Driver receives commands, doesn't initiate
  - D) The browser driver must mediate between test code and browser
</details>

---

### 17. Which XPath function matches elements where an attribute contains a substring?

- [ ] A) `starts-with()`
- [ ] B) `contains()`
- [ ] C) `text()`
- [ ] D) `normalize-space()`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `contains()`

**Explanation:** The `contains()` function checks if an attribute or text contains a specified substring. Example: `//button[contains(@class, 'btn')]` matches elements with 'btn' anywhere in their class attribute.

- **Why others are wrong:**
  - A) `starts-with()` only matches if the attribute begins with the value
  - C) `text()` returns the text content of an element, not a substring check
  - D) `normalize-space()` removes whitespace, not a substring check
</details>

---

### 18. What is the difference between implicit wait and explicit wait in Selenium?

- [ ] A) Implicit wait is faster than explicit wait
- [ ] B) Implicit wait applies globally; explicit wait is for specific conditions
- [ ] C) Explicit wait applies globally; implicit wait is for specific conditions
- [ ] D) There is no difference

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Implicit wait applies globally; explicit wait is for specific conditions

**Explanation:** Implicit wait sets a global timeout for all `findElement` calls to wait for element existence. Explicit wait (WebDriverWait) waits for specific conditions like visibility, clickability, or text presence on specific elements.

- **Why others are wrong:**
  - A) Explicit wait can be more efficient as it returns immediately when condition is met
  - C) This is reversed
  - D) They have fundamentally different scopes and purposes
</details>

---

### 19. Which code correctly waits for an element to be clickable?

- [ ] A) `driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))`
- [ ] B) `Thread.sleep(10000)`
- [ ] C) `new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.id("btn")))`
- [ ] D) `driver.findElement(By.id("btn")).wait(10)`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) `new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.id("btn")))`

**Explanation:** `WebDriverWait` with `ExpectedConditions.elementToBeClickable()` waits until the element is visible and enabled, making it safe to click. This is the recommended approach for dynamic elements.

- **Why others are wrong:**
  - A) Implicit wait only checks element existence, not clickability
  - B) `Thread.sleep()` is a static wait that always waits the full duration—never use in tests
  - D) `.wait()` is not a valid WebElement method
</details>

---

### 20. What does the following XPath select? `//label[text()='Email']/following-sibling::input`

- [ ] A) All input elements on the page
- [ ] B) The input element that comes after a label with text "Email"
- [ ] C) The label element containing "Email"
- [ ] D) The parent of the label with text "Email"

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) The input element that comes after a label with text "Email"

**Explanation:** This XPath uses the `following-sibling` axis to find the input element that is a sibling after the label containing "Email". This is a common pattern for locating form fields by their label text.

- **Why others are wrong:**
  - A) It specifically targets inputs following a specific label
  - C) The `/following-sibling::input` continues past the label
  - D) `parent::` would select the parent, not `following-sibling::`
</details>

---

### 21. Which WebElement method retrieves the visible text of an element?

- [ ] A) `element.getValue()`
- [ ] B) `element.getText()`
- [ ] C) `element.getAttribute("text")`
- [ ] D) `element.getInnerText()`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `element.getText()`

**Explanation:** The `getText()` method returns the visible (rendered) text of a web element and its sub-elements, excluding any hidden text.

- **Why others are wrong:**
  - A) `getValue()` is not a WebElement method
  - C) `getAttribute("text")` would look for a "text" attribute, not visible text
  - D) `getInnerText()` is not a WebElement method (it's a JavaScript property)
</details>

---

### 22. True or False: Absolute XPath (starting with `/html/body/...`) is preferred over relative XPath.

- [ ] A) True
- [ ] B) False

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) False

**Explanation:** Relative XPath (starting with `//`) is strongly preferred because it's more resilient to page structure changes. Absolute XPath breaks easily when any element in the path changes, making tests brittle and hard to maintain.
</details>

---

### 23. What does `ExpectedConditions.invisibilityOfElementLocated()` wait for?

- [ ] A) The element to become visible
- [ ] B) The element to disappear or become hidden
- [ ] C) The element to be clickable
- [ ] D) The element text to be empty

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) The element to disappear or become hidden

**Explanation:** `invisibilityOfElementLocated()` waits until an element is either removed from the DOM or hidden. This is commonly used to wait for loading spinners or modal overlays to disappear.

- **Why others are wrong:**
  - A) Use `visibilityOfElementLocated()` for that
  - C) Use `elementToBeClickable()` for that
  - D) There's no built-in condition specifically for empty text
</details>

---

## Part 4: Selenium Advanced Patterns (Thursday)

### 24. What is the main purpose of the Page Object Model (POM)?

- [ ] A) To make tests run faster
- [ ] B) To separate test logic from page-specific code and locators
- [ ] C) To automatically generate test reports
- [ ] D) To run tests in parallel

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) To separate test logic from page-specific code and locators

**Explanation:** POM creates an abstraction layer where page classes encapsulate UI locators and interactions, while test classes contain only test logic and assertions. This improves maintainability—when a locator changes, you update it in one place.

- **Why others are wrong:**
  - A) POM is about maintainability, not performance
  - C) Reporting is handled by test frameworks, not POM
  - D) Parallel execution is a test runner feature, not related to POM
</details>

---

### 25. In Page Factory, what does `PageFactory.initElements(driver, this)` do?

- [ ] A) Navigates to the page URL
- [ ] B) Initializes WebElements annotated with @FindBy
- [ ] C) Runs all test methods
- [ ] D) Closes the browser

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Initializes WebElements annotated with @FindBy

**Explanation:** `PageFactory.initElements()` scans the class for `@FindBy` annotations and creates proxy objects for the WebElements. Elements are lazily initialized—found when first accessed, not at initialization.

- **Why others are wrong:**
  - A) Navigation is done separately with `driver.get()`
  - C) Test execution is handled by test frameworks
  - D) `driver.quit()` closes the browser
</details>

---

### 26. Which annotation is correct for locating an element by CSS selector in Page Factory?

- [ ] A) `@FindBy(selector = "div.container")`
- [ ] B) `@FindBy(css = "div.container")`
- [ ] C) `@FindBy(cssSelector = "div.container")`
- [ ] D) `@FindBy(style = "div.container")`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `@FindBy(css = "div.container")`

**Explanation:** The `@FindBy` annotation uses `css` as the attribute name for CSS selectors. Other common attributes include `id`, `name`, `xpath`, `className`, `linkText`, and `tagName`.

- **Why others are wrong:**
  - A) `selector` is not a valid attribute
  - C) `cssSelector` is not a valid attribute (it's just `css`)
  - D) `style` is not a valid attribute
</details>

---

### 27. What does WebDriverManager do?

- [ ] A) Manages multiple browser windows
- [ ] B) Automatically downloads and configures browser drivers
- [ ] C) Runs tests in parallel
- [ ] D) Generates test reports

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Automatically downloads and configures browser drivers

**Explanation:** WebDriverManager (by Boni García) automatically detects your browser version, downloads the matching driver, and sets it up. This eliminates manual driver management and version compatibility issues.

- **Why others are wrong:**
  - A) Window management is done with `driver.switchTo().window()`
  - C) Parallel execution is a test framework feature
  - D) Reporting is handled by test frameworks
</details>

---

### 28. In a Page Object, what should methods return when navigation occurs?

- [ ] A) void
- [ ] B) String
- [ ] C) The new Page Object representing the destination page
- [ ] D) Boolean indicating success

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) The new Page Object representing the destination page

**Explanation:** When an action navigates to a different page, the method should return a new instance of that page's Page Object. For same-page actions, return `this` to enable method chaining.

```java
public DashboardPage clickLogin() {
    loginButton.click();
    return new DashboardPage(driver);  // Navigation occurred
}

public LoginPage enterUsername(String user) {
    usernameField.sendKeys(user);
    return this;  // Same page, enable chaining
}
```
</details>

---

### 29. What is the purpose of `@CacheLookup` in Page Factory?

- [ ] A) To cache the browser session
- [ ] B) To cache element references after first lookup
- [ ] C) To cache test results
- [ ] D) To cache page URLs

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) To cache element references after first lookup

**Explanation:** `@CacheLookup` stores the WebElement reference after the first lookup, avoiding repeated DOM searches. Only use for static elements that don't change—dynamic elements should NOT be cached.

- **Why others are wrong:**
  - A) Session management is separate from element caching
  - C) Test results are handled by test frameworks
  - D) Page URLs are not cached by this annotation
</details>

---

### 30. How do you switch to a new window/tab in Selenium 4?

- [ ] A) `driver.switchTo().alert()`
- [ ] B) `driver.switchTo().newWindow(WindowType.TAB)`
- [ ] C) `driver.get("new tab")`
- [ ] D) `driver.navigate().newTab()`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `driver.switchTo().newWindow(WindowType.TAB)`

**Explanation:** Selenium 4 introduced `switchTo().newWindow()` with `WindowType.TAB` or `WindowType.WINDOW` to create and switch to new tabs/windows. This is cleaner than the Selenium 3 approach of using JavaScript.

- **Why others are wrong:**
  - A) `switchTo().alert()` is for handling JavaScript alerts
  - C) `driver.get()` navigates within the current tab
  - D) This syntax doesn't exist
</details>

---

### 31. True or False: Assertions should be placed inside Page Objects.

- [ ] A) True
- [ ] B) False

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) False

**Explanation:** Page Objects should encapsulate UI interactions and return data (like `getText()`, `isDisplayed()`). Assertions belong in test classes. This separation keeps Page Objects reusable—the same method can be used in tests expecting success or failure.
</details>

---

## Part 5: Selenium Additional Features & Integration (Friday)

### 32. Which locator strategy is generally considered most reliable?

- [ ] A) XPath with index positions like `//div[1]/span[2]`
- [ ] B) Element ID when available
- [ ] C) Class name only
- [ ] D) Absolute XPath

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Element ID when available

**Explanation:** IDs are designed to be unique within a page, making them the most reliable locator. When IDs aren't available, use meaningful attributes like `name`, `data-testid`, or stable CSS selectors.

- **Why others are wrong:**
  - A) Index-based XPath is fragile and breaks when elements are added/removed
  - C) Class names are often shared across many elements
  - D) Absolute XPath breaks when any ancestor element changes
</details>

---

### 33. What does the following code do?

```java
((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
```

- [ ] A) Saves a screenshot to database
- [ ] B) Captures a screenshot and returns it as a File object
- [ ] C) Displays the screenshot on screen
- [ ] D) Compares screenshots

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Captures a screenshot and returns it as a File object

**Explanation:** The `TakesScreenshot` interface provides `getScreenshotAs()` method that captures the current browser state. `OutputType.FILE` returns a temporary file that you can copy to a permanent location.

- **Why others are wrong:**
  - A) Database storage requires additional code
  - C) Displaying requires separate viewer code
  - D) Comparison requires additional libraries
</details>

---

### 34. When running Selenium tests from Maven command line, which command executes only tests in a specific class?

- [ ] A) `mvn test`
- [ ] B) `mvn test -Dtest=LoginTest`
- [ ] C) `mvn run LoginTest`
- [ ] D) `mvn execute -class=LoginTest`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `mvn test -Dtest=LoginTest`

**Explanation:** Maven's Surefire plugin uses `-Dtest=` to filter which tests to run. You can specify a class name, method name (`LoginTest#testValidLogin`), or use wildcards (`Login*`).

- **Why others are wrong:**
  - A) `mvn test` runs all tests without filtering
  - C) `run` is not a Maven phase/goal
  - D) `-class=` is not valid Maven syntax
</details>

---

### 35. What is the purpose of combining API setup with UI validation in integration tests?

- [ ] A) To make tests slower
- [ ] B) To use API calls for fast test data setup, then verify UI displays correctly
- [ ] C) To replace all UI tests
- [ ] D) To avoid using Selenium

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) To use API calls for fast test data setup, then verify UI displays correctly

**Explanation:** API-UI integration tests use API calls (REST Assured/requests) to quickly create test data, then use Selenium to verify the UI displays that data correctly. This is faster than creating data through the UI and tests the full stack.

- **Why others are wrong:**
  - A) This approach actually speeds up tests
  - C) UI tests are still necessary for visual validation
  - D) Selenium is used for the UI validation portion
</details>

---

## Part 6: Code Prediction

### 36. What is the output of this REST Assured test if the API returns `{"users": [{"name": "John"}, {"name": "Jane"}]}`?

```java
given()
    .when().get("/api/users")
    .then()
    .body("users.size()", equalTo(2))
    .body("users[0].name", equalTo("John"));
```

- [ ] A) Test passes
- [ ] B) Test fails on first assertion
- [ ] C) Test fails on second assertion
- [ ] D) Compilation error

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** A) Test passes

**Explanation:** Both assertions are satisfied:
- `users.size()` equals 2 (array has 2 elements)
- `users[0].name` equals "John" (first user's name is John)

JSONPath expressions `users.size()` and `users[0].name` correctly navigate the JSON structure.
</details>

---

### 37. What happens when this Python code runs?

```python
response = requests.get("https://api.example.com/users", timeout=5)
response.raise_for_status()
```

If the server returns HTTP 404?

- [ ] A) `response.json()` returns empty
- [ ] B) An `HTTPError` exception is raised
- [ ] C) The code silently continues
- [ ] D) A `TimeoutError` is raised

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) An `HTTPError` exception is raised

**Explanation:** `raise_for_status()` raises an `HTTPError` exception for any 4xx or 5xx status codes. A 404 response triggers this exception, allowing you to handle errors explicitly.

- **Why others are wrong:**
  - A) `.json()` isn't called, and 404 would raise an error first
  - C) The whole point of `raise_for_status()` is to NOT silently continue
  - D) Timeout would only occur if the server didn't respond within 5 seconds
</details>

---

### 38. What element does this XPath locate?

```xpath
//table[@id='users']//tr[td[contains(text(), 'Admin')]]/td[3]
```

- [ ] A) All cells in the table
- [ ] B) The third cell in any row containing "Admin" text
- [ ] C) The "Admin" cell itself
- [ ] D) All rows in the users table

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) The third cell in any row containing "Admin" text

**Explanation:** This XPath:
1. Finds table with id='users'
2. Finds any `tr` that has a `td` containing "Admin"
3. Returns the 3rd `td` cell of that row

This pattern is useful for finding data in a specific column of a row identified by another column's content.
</details>

---

### 39. What does this Selenium wait code do?

```java
wait.until(ExpectedConditions.or(
    ExpectedConditions.visibilityOfElementLocated(By.id("success")),
    ExpectedConditions.visibilityOfElementLocated(By.id("error"))
));
```

- [ ] A) Waits for both success AND error elements to appear
- [ ] B) Waits until EITHER success OR error element appears
- [ ] C) Waits for success element only
- [ ] D) Throws an exception immediately

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Waits until EITHER success OR error element appears

**Explanation:** `ExpectedConditions.or()` returns true when ANY of the provided conditions is met. This is useful when waiting for a page to show either a success or error state after an action.

- **Why others are wrong:**
  - A) That would be `ExpectedConditions.and()`
  - C) Both conditions are evaluated
  - D) The wait continues polling until timeout or condition met
</details>

---

## Part 7: Fill in the Blank

### 40. Fill in the blank: In Postman's variable scope hierarchy, the order from highest to lowest priority is: Local → _____ → Environment → Collection → Global.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** Data

**Explanation:** The complete variable resolution order in Postman is:
1. Local (request scope) - Highest priority
2. Data (from CSV/JSON file during Collection Runner)
3. Environment
4. Collection
5. Global - Lowest priority

When Postman encounters `{{variableName}}`, it searches these scopes in order, using the first match found.
</details>

---

### 41. Fill in the blank: The REST Assured syntax follows the _____ pattern from Behavior-Driven Development.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** Given-When-Then

**Explanation:** REST Assured uses BDD syntax:
- `given()` - Set up preconditions (headers, auth, body)
- `when()` - Perform the action (HTTP method and endpoint)
- `then()` - Verify the outcome (status, body, headers)

This makes tests readable and self-documenting.
</details>

---

### 42. Fill in the blank: To create a reusable API client in Python requests that maintains cookies across requests, use a _____ object.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** Session

**Explanation:** `requests.Session()` persists settings (headers, authentication, cookies) across multiple requests. This is ideal for API testing where you need to maintain login state or reuse configuration.

```python
session = requests.Session()
session.headers["Authorization"] = "Bearer token"
response = session.get("/api/users")  # Uses session settings
```
</details>

---

## Part 8: True/False Rapid Fire

### 43. True or False: JMeter can only test HTTP/REST APIs.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** JMeter supports multiple protocols including HTTP/HTTPS, JDBC (databases), FTP, LDAP, JMS (message queues), SMTP (email), TCP/UDP, and more. It's a general-purpose load testing tool.
</details>

---

### 44. True or False: Selenium WebDriver can test mobile native applications.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** Selenium WebDriver is specifically for web browser automation. For mobile native apps, use Appium (which uses the WebDriver protocol). Selenium can test mobile web browsers but not native mobile apps.
</details>

---

### 45. True or False: In Page Factory, elements annotated with @FindBy are found immediately when the page object is instantiated.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** Page Factory uses lazy initialization—elements are proxied and only actually located when first accessed (e.g., when you call `.click()` or `.getText()`). This is different from calling `driver.findElement()` directly.
</details>

---

### 46. True or False: The HTTP DELETE method is idempotent.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** True

**Explanation:** DELETE is idempotent—deleting the same resource multiple times produces the same result (the resource is gone). The first DELETE removes it; subsequent DELETEs may return 404, but the end state is the same.
</details>

---

### 47. True or False: WebDriverWait and implicit wait should be used together for best results.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** Mixing implicit and explicit waits leads to unpredictable behavior and can cause unexpected long wait times. Best practice is to use explicit waits (WebDriverWait) only and avoid implicit waits entirely.
</details>

---

## Part 9: Scenario-Based Questions

### 48. You need to test an API that requires authentication. The token expires after 1 hour. What's the best approach in Postman?

- [ ] A) Manually update the token in every request
- [ ] B) Use a pre-request script to check token expiry and refresh if needed
- [ ] C) Create a new collection for each test session
- [ ] D) Disable authentication

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Use a pre-request script to check token expiry and refresh if needed

**Explanation:** A pre-request script can:
1. Check if token exists and isn't expired
2. Use `pm.sendRequest()` to call the auth endpoint if needed
3. Store the new token and expiry time in environment variables

This enables fully automated test execution without manual intervention.
</details>

---

### 49. Your Selenium test intermittently fails with `StaleElementReferenceException`. What's the most likely cause and solution?

- [ ] A) Wrong locator - change to ID
- [ ] B) Element was found, then DOM updated, making the reference stale - re-find the element or use explicit wait
- [ ] C) Browser closed - add longer timeout
- [ ] D) Network latency - use Thread.sleep()

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Element was found, then DOM updated, making the reference stale - re-find the element or use explicit wait

**Explanation:** `StaleElementReferenceException` occurs when a WebElement reference points to an element that no longer exists in the current DOM (page refreshed, AJAX update, SPA navigation). Solutions:
1. Re-find the element before interaction
2. Use explicit waits before interactions
3. In FluentWait, ignore `StaleElementReferenceException`

- **Why others are wrong:**
  - A) The locator worked initially; it's a timing issue
  - C) Browser closing causes `SessionNotFoundException`
  - D) Never use Thread.sleep()—use proper waits
</details>

---

### 50. You're building a Page Object for a form with 20 fields. What's the best approach?

- [ ] A) Create 20 individual setter methods and one submit method
- [ ] B) Create a single method that accepts a Map of field names to values
- [ ] C) Create a convenience method accepting a data object, with individual methods for specific fields
- [ ] D) Put all code in the test class

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Create a convenience method accepting a data object, with individual methods for specific fields

**Explanation:** Best practice combines both approaches:
- Individual methods for specific field interactions when needed
- A convenience method (`fillForm(UserData data)`) for common scenarios
- A data object/class to represent form data

This provides flexibility while keeping tests clean:
```java
// Simple case: use convenience method
loginPage.fillForm(testData);

// Specific case: use individual methods  
loginPage.enterEmail("test@example.com")
         .checkTerms()
         .submit();
```
</details>

---

## Congratulations! 🎉

You've completed the Week 7 Practice Quiz covering Integration Testing and Selenium. Review any questions you found challenging by revisiting the corresponding written content in `weeklytechrepo/week7-integration-testing-selenium/`.

**Key Topics Covered:**
- API Testing Fundamentals & HTTP Methods
- Postman: Requests, Scripts, Environments
- REST Assured (Java) & Python Requests
- Apache JMeter Performance Testing
- Selenium WebDriver Architecture
- XPath & Locator Strategies
- Waiting Strategies
- Page Object Model & Page Factory
- Integration Testing Approaches

