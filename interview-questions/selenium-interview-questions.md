# Selenium & Automation Interview Questions & Answers

## Beginner Level

**Q1: What is Selenium and what are its components?**

> **Answer:** Selenium is an open-source web browser automation framework for testing web applications across browsers and platforms.
>
> **Components:**
>
> | Component | Purpose |
> |-----------|---------|
> | **WebDriver** | Browser automation API |
> | **IDE** | Record/playback tool (browser extension) |
> | **Grid** | Distributed testing across machines |
>
> **Architecture:**
> ```
> Test Script → WebDriver API → Browser Driver → Browser
>      ↓              ↓              ↓            ↓
>   Java/Python   Commands    chromedriver    Chrome
> ```
>
> **Languages supported:** Java, Python, C#, JavaScript, Ruby, Kotlin

---

**Q2: What are the different locator strategies in Selenium?**

> **Answer:**
>
> | Locator | Example | Reliability |
> |---------|---------|-------------|
> | **ID** | `By.id("username")` | ⭐⭐⭐⭐⭐ Best |
> | **Name** | `By.name("email")` | ⭐⭐⭐⭐ |
> | **CSS Selector** | `By.cssSelector(".btn")` | ⭐⭐⭐⭐ |
> | **XPath** | `By.xpath("//input")` | ⭐⭐⭐ |
> | **Class Name** | `By.className("submit")` | ⭐⭐⭐ |
> | **Tag Name** | `By.tagName("button")` | ⭐⭐ |
> | **Link Text** | `By.linkText("Click")` | ⭐⭐ |
>
> **Best practice order:** ID > Name > CSS > XPath
>
> **Examples:**
> ```java
> // ID - most reliable
> driver.findElement(By.id("login-button"));
> 
> // CSS Selector - flexible
> driver.findElement(By.cssSelector("input[type='email']"));
> 
> // XPath - powerful but slower
> driver.findElement(By.xpath("//button[text()='Submit']"));
> ```

---

**Q3: What is the difference between findElement and findElements?**

> **Answer:**
>
> | Aspect | findElement | findElements |
> |--------|-------------|--------------|
> | Returns | Single WebElement | List<WebElement> |
> | Not found | NoSuchElementException | Empty list |
> | Use case | Unique element | Multiple elements |
>
> ```java
> // findElement - single element
> WebElement button = driver.findElement(By.id("submit"));
> 
> // findElements - multiple elements
> List<WebElement> links = driver.findElements(By.tagName("a"));
> System.out.println("Found " + links.size() + " links");
> 
> // Check if element exists without exception
> List<WebElement> elements = driver.findElements(By.id("optional"));
> if (!elements.isEmpty()) {
>     elements.get(0).click();
> }
> ```

---

**Q4: Explain implicit wait vs explicit wait.**

> **Answer:**
>
> **Implicit Wait:**
> - Global setting for all elements
> - Polls DOM until element found or timeout
> - Set once, applies everywhere
>
> ```java
> driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
> // All findElement calls will wait up to 10 seconds
> ```
>
> **Explicit Wait:**
> - Waits for specific condition
> - Applied to individual elements
> - More flexible and precise
>
> ```java
> WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
> 
> // Wait until element is visible
> WebElement element = wait.until(
>     ExpectedConditions.visibilityOfElementLocated(By.id("result"))
> );
> 
> // Wait until element is clickable
> wait.until(ExpectedConditions.elementToBeClickable(By.id("button")));
> ```
>
> **Common ExpectedConditions:**
> - `presenceOfElementLocated` - In DOM
> - `visibilityOfElementLocated` - Visible on page
> - `elementToBeClickable` - Visible and enabled
> - `textToBePresentInElement` - Contains text
> - `urlContains` - URL check
>
> **Best practice:** Use explicit waits; avoid implicit waits or mixing both.

---

**Q5: How do you handle dropdowns in Selenium?**

> **Answer:**
>
> ```java
> import org.openqa.selenium.support.ui.Select;
> 
> // Find dropdown element
> WebElement dropdown = driver.findElement(By.id("country"));
> Select select = new Select(dropdown);
> 
> // Select by visible text
> select.selectByVisibleText("United States");
> 
> // Select by value attribute
> select.selectByValue("us");
> 
> // Select by index (0-based)
> select.selectByIndex(0);
> 
> // Get selected option
> WebElement selected = select.getFirstSelectedOption();
> System.out.println(selected.getText());
> 
> // Get all options
> List<WebElement> options = select.getOptions();
> 
> // For multi-select dropdowns
> if (select.isMultiple()) {
>     select.selectByValue("option1");
>     select.selectByValue("option2");
>     select.deselectAll();
> }
> ```

---

## Intermediate Level

**Q6: What is the Page Object Model (POM) and why is it important?**

> **Answer:** POM is a design pattern that creates a class for each web page, separating page structure from test logic.
>
> **Benefits:**
> - **Maintainability:** Locator changes in one place
> - **Reusability:** Page methods used across tests
> - **Readability:** Tests read like user actions
> - **Reduced duplication:** Common actions defined once
>
> **Implementation:**
> ```java
> // Page Object
> public class LoginPage {
>     private WebDriver driver;
>     
>     // Locators
>     private By usernameField = By.id("username");
>     private By passwordField = By.id("password");
>     private By loginButton = By.id("login");
>     
>     public LoginPage(WebDriver driver) {
>         this.driver = driver;
>     }
>     
>     // Actions
>     public void enterUsername(String username) {
>         driver.findElement(usernameField).sendKeys(username);
>     }
>     
>     public void enterPassword(String password) {
>         driver.findElement(passwordField).sendKeys(password);
>     }
>     
>     public DashboardPage clickLogin() {
>         driver.findElement(loginButton).click();
>         return new DashboardPage(driver);
>     }
>     
>     public DashboardPage login(String user, String pass) {
>         enterUsername(user);
>         enterPassword(pass);
>         return clickLogin();
>     }
> }
> 
> // Test Class
> @Test
> void testLogin() {
>     LoginPage loginPage = new LoginPage(driver);
>     DashboardPage dashboard = loginPage.login("user", "pass");
>     assertTrue(dashboard.isWelcomeDisplayed());
> }
> ```

---

**Q7: How do you handle alerts and pop-ups?**

> **Answer:**
>
> ```java
> // Switch to alert
> Alert alert = driver.switchTo().alert();
> 
> // Get alert text
> String text = alert.getText();
> 
> // Accept (OK button)
> alert.accept();
> 
> // Dismiss (Cancel button)
> alert.dismiss();
> 
> // Enter text (prompt)
> alert.sendKeys("input text");
> alert.accept();
> 
> // Wait for alert
> WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
> wait.until(ExpectedConditions.alertIsPresent());
> 
> // Handle unexpected alert
> try {
>     Alert unexpectedAlert = driver.switchTo().alert();
>     unexpectedAlert.accept();
> } catch (NoAlertPresentException e) {
>     // No alert present
> }
> ```
>
> **Alert types:**
> - **Simple alert:** OK button only
> - **Confirm alert:** OK and Cancel buttons
> - **Prompt alert:** Text input with OK/Cancel

---

**Q8: How do you handle multiple windows or tabs?**

> **Answer:**
>
> ```java
> // Store original window
> String originalWindow = driver.getWindowHandle();
> 
> // Click link that opens new window
> driver.findElement(By.id("new-window-link")).click();
> 
> // Wait for new window
> WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
> wait.until(ExpectedConditions.numberOfWindowsToBe(2));
> 
> // Get all window handles
> Set<String> allWindows = driver.getWindowHandles();
> 
> // Switch to new window
> for (String window : allWindows) {
>     if (!window.equals(originalWindow)) {
>         driver.switchTo().window(window);
>         break;
>     }
> }
> 
> // Perform actions in new window
> System.out.println(driver.getTitle());
> 
> // Close new window and switch back
> driver.close();
> driver.switchTo().window(originalWindow);
> ```

---

**Q9: How do you handle iframes?**

> **Answer:**
>
> ```java
> // Switch to iframe by ID or name
> driver.switchTo().frame("iframe-id");
> 
> // Switch by index
> driver.switchTo().frame(0);  // First iframe
> 
> // Switch by WebElement
> WebElement iframeElement = driver.findElement(By.cssSelector("iframe.content"));
> driver.switchTo().frame(iframeElement);
> 
> // Interact with elements inside iframe
> driver.findElement(By.id("button-inside")).click();
> 
> // Switch back to main content
> driver.switchTo().defaultContent();
> 
> // Switch to parent frame (from nested iframe)
> driver.switchTo().parentFrame();
> 
> // Handling nested iframes
> driver.switchTo().frame("outer-frame");
> driver.switchTo().frame("inner-frame");
> // ... interact with elements ...
> driver.switchTo().defaultContent();  // Back to main
> ```

---

**Q10: Explain the Actions class and its uses.**

> **Answer:** Actions class handles complex user interactions like mouse movements, drag-drop, and keyboard combinations.
>
> ```java
> Actions actions = new Actions(driver);
> 
> // Hover over element
> actions.moveToElement(element).perform();
> 
> // Double click
> actions.doubleClick(element).perform();
> 
> // Right click (context menu)
> actions.contextClick(element).perform();
> 
> // Click and hold
> actions.clickAndHold(element).perform();
> actions.release().perform();
> 
> // Drag and drop
> actions.dragAndDrop(source, target).perform();
> 
> // Drag by offset
> actions.dragAndDropBy(element, 100, 50).perform();
> 
> // Keyboard actions
> actions.sendKeys(Keys.ENTER).perform();
> actions.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform();
> 
> // Chain multiple actions
> actions.moveToElement(menu)
>        .pause(Duration.ofMillis(500))
>        .moveToElement(submenu)
>        .click()
>        .perform();
> ```

---

## Advanced Level

**Q11: How do you implement data-driven testing?**

> **Answer:** Data-driven testing runs same test with multiple data sets.
>
> **JUnit 5 with @ParameterizedTest:**
> ```java
> @ParameterizedTest
> @CsvSource({
>     "user1, pass1, Welcome user1",
>     "user2, pass2, Welcome user2",
>     "invalid, wrong, Invalid credentials"
> })
> void testLogin(String username, String password, String expected) {
>     loginPage.enterUsername(username);
>     loginPage.enterPassword(password);
>     loginPage.clickLogin();
>     assertEquals(expected, getResultMessage());
> }
> 
> // From CSV file
> @ParameterizedTest
> @CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 1)
> void testFromFile(String input, String expected) {
>     // Test logic
> }
> 
> // From method
> @ParameterizedTest
> @MethodSource("provideTestData")
> void testFromMethod(String user, String pass, boolean shouldPass) {
>     // Test logic
> }
> 
> static Stream<Arguments> provideTestData() {
>     return Stream.of(
>         Arguments.of("admin", "admin123", true),
>         Arguments.of("user", "wrongpass", false)
>     );
> }
> ```
>
> **Python pytest:**
> ```python
> @pytest.mark.parametrize("username,password,expected", [
>     ("user1", "pass1", "Welcome"),
>     ("invalid", "wrong", "Error"),
> ])
> def test_login(username, password, expected):
>     login_page.login(username, password)
>     assert expected in dashboard.get_message()
> ```

---

**Q12: How do you handle flaky tests?**

> **Answer:** Flaky tests pass/fail intermittently without code changes.
>
> **Common causes and solutions:**
>
> **1. Timing issues:**
> ```java
> // Bad - fixed sleep
> Thread.sleep(3000);
> 
> // Good - explicit wait
> WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
> wait.until(ExpectedConditions.elementToBeClickable(button));
> ```
>
> **2. Dynamic elements:**
> ```java
> // Bad - absolute XPath
> By.xpath("/html/body/div[1]/div[2]/button")
> 
> // Good - stable locator
> By.cssSelector("[data-testid='submit-button']")
> ```
>
> **3. Test order dependency:**
> ```java
> // Each test should set up its own state
> @BeforeEach
> void setUp() {
>     database.reset();
>     createTestUser();
> }
> ```
>
> **4. Environment issues:**
> - Use consistent test environments
> - Clear browser state between tests
> - Handle stale element exceptions
>
> **5. Retry mechanism:**
> ```java
> @RetryingTest(3)  // JUnit Pioneer
> void flakyTest() {
>     // Test that might need retry
> }
> ```
>
> **Best practices:**
> - Investigate root cause, don't just add retries
> - Run flaky tests multiple times to confirm
> - Track flakiness metrics
> - Quarantine consistently failing tests
