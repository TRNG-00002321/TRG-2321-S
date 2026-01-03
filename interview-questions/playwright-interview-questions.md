# Playwright Interview Questions & Answers

## Beginner Level

**Q1: What is Playwright and how does it differ from Selenium?**

> **Answer:** Playwright is a modern browser automation framework by Microsoft for end-to-end testing.
>
> | Feature | Selenium | Playwright |
> |---------|----------|------------|
> | Auto-wait | Manual waits needed | Built-in auto-wait |
> | Browser support | Separate drivers | Single API for all |
> | Parallel execution | Grid required | Native support |
> | Network interception | Limited | Full control |
> | Debugging | Third-party tools | Built-in trace viewer |
> | Video recording | External tools | Native feature |
>
> **Playwright advantages:** Faster, more reliable, better debugging, modern API.

---

**Q2: How do you set up Playwright in Java?**

> **Answer:**
> ```xml
> <dependency>
>     <groupId>com.microsoft.playwright</groupId>
>     <artifactId>playwright</artifactId>
>     <version>1.40.0</version>
> </dependency>
> ```
>
> ```java
> Playwright playwright = Playwright.create();
> Browser browser = playwright.chromium().launch();
> Page page = browser.newPage();
> page.navigate("https://example.com");
> ```

---

**Q3: What are Playwright's auto-waiting features?**

> **Answer:** Playwright automatically waits for elements before interacting.
>
> **Auto-waits for:**
> - Element attached to DOM
> - Element visible
> - Element stable (not animating)
> - Element enabled
>
> ```java
> // No explicit waits needed
> page.click("#button");  // Waits automatically
> page.fill("#input", "text");  // Waits for enabled
> ```

---

**Q4: What are the different locator strategies in Playwright?**

> **Answer:**
> ```java
> // CSS selector
> page.locator("#username");
> page.locator(".btn-primary");
> 
> // By text
> page.getByText("Submit");
> page.getByText("Submit", new Page.GetByTextOptions().setExact(true));
> 
> // By role (recommended)
> page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login"));
> 
> // By label
> page.getByLabel("Email");
> 
> // By placeholder
> page.getByPlaceholder("Enter email");
> 
> // By test ID (best for testing)
> page.getByTestId("submit-button");
> 
> // XPath
> page.locator("xpath=//button[@id='submit']");
> ```

---

**Q5: How do you interact with forms in Playwright?**

> **Answer:**
> ```java
> // Text input
> page.fill("#email", "user@example.com");
> 
> // Type with delay
> page.locator("#search").type("query", new Locator.TypeOptions().setDelay(100));
> 
> // Checkbox
> page.check("#agree-terms");
> page.uncheck("#newsletter");
> 
> // Radio button
> page.check("input[value='option1']");
> 
> // Select dropdown
> page.selectOption("#country", "US");
> 
> // File upload
> page.setInputFiles("#upload", Paths.get("file.pdf"));
> 
> // Submit
> page.click("button[type='submit']");
> ```

---

## Intermediate Level

**Q6: How do you use assertions in Playwright?**

> **Answer:**
> ```java
> import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
> 
> // Visibility
> assertThat(page.locator("#message")).isVisible();
> assertThat(page.locator("#loading")).isHidden();
> 
> // Text content
> assertThat(page.locator(".title")).hasText("Welcome");
> assertThat(page.locator(".title")).containsText("Welcome");
> 
> // Attributes
> assertThat(page.locator("#link")).hasAttribute("href", "/home");
> assertThat(page.locator("button")).isEnabled();
> assertThat(page.locator("button")).isDisabled();
> 
> // Count
> assertThat(page.locator(".item")).hasCount(5);
> 
> // Page assertions
> assertThat(page).hasURL("https://example.com/dashboard");
> assertThat(page).hasTitle("Dashboard");
> ```

---

**Q7: How do you handle popups, dialogs, and frames?**

> **Answer:**
> ```java
> // Wait for popup
> Page popup = page.waitForPopup(() -> {
>     page.click("#open-popup");
> });
> popup.waitForLoadState();
> 
> // Handle dialog/alert
> page.onDialog(dialog -> {
>     System.out.println("Dialog: " + dialog.message());
>     dialog.accept();  // or dialog.dismiss()
> });
> page.click("#trigger-alert");
> 
> // Frames
> Frame frame = page.frame("frame-name");
> frame.fill("#input", "text");
> 
> // Or use frameLocator
> page.frameLocator("#my-iframe").locator("#button").click();
> ```

---

**Q8: How do you intercept and mock network requests?**

> **Answer:**
> ```java
> // Mock API response
> page.route("**/api/users", route -> {
>     route.fulfill(new Route.FulfillOptions()
>         .setStatus(200)
>         .setContentType("application/json")
>         .setBody("[{\"id\": 1, \"name\": \"Mock User\"}]"));
> });
> 
> // Modify request headers
> page.route("**/api/**", route -> {
>     Map<String, String> headers = new HashMap<>(route.request().headers());
>     headers.put("Authorization", "Bearer token");
>     route.resume(new Route.ResumeOptions().setHeaders(headers));
> });
> 
> // Block requests (images, etc.)
> page.route("**/*.{png,jpg}", Route::abort);
> 
> // Wait for specific response
> Response response = page.waitForResponse("**/api/data", () -> {
>     page.click("#load");
> });
> ```

---

**Q9: How do you take screenshots and record videos?**

> **Answer:**
> ```java
> // Full page screenshot
> page.screenshot(new Page.ScreenshotOptions()
>     .setPath(Paths.get("screenshot.png"))
>     .setFullPage(true));
> 
> // Element screenshot
> page.locator(".chart").screenshot(
>     new Locator.ScreenshotOptions().setPath(Paths.get("chart.png")));
> 
> // Video recording - enable in context
> context = browser.newContext(new Browser.NewContextOptions()
>     .setRecordVideoDir(Paths.get("videos/"))
>     .setRecordVideoSize(1280, 720));
> 
> // Tracing for debugging
> context.tracing().start(new Tracing.StartOptions()
>     .setScreenshots(true).setSnapshots(true));
> // ... run tests ...
> context.tracing().stop(new Tracing.StopOptions()
>     .setPath(Paths.get("trace.zip")));
> ```

---

**Q10: How do you handle keyboard and mouse actions?**

> **Answer:**
> ```java
> // Keyboard
> page.keyboard().press("Enter");
> page.keyboard().press("Control+A");
> page.keyboard().type("Hello World");
> 
> // Mouse
> page.mouse().click(100, 200);
> page.mouse().dblclick(100, 200);
> 
> // Hover
> page.locator("#menu").hover();
> 
> // Drag and drop
> page.locator("#source").dragTo(page.locator("#target"));
> 
> // Right-click
> page.locator("#element").click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
> ```

---

## Advanced Level

**Q11: How do you implement Page Object Model in Playwright?**

> **Answer:**
> ```java
> public class LoginPage {
>     private final Page page;
>     private final Locator usernameInput;
>     private final Locator passwordInput;
>     private final Locator loginButton;
>     
>     public LoginPage(Page page) {
>         this.page = page;
>         this.usernameInput = page.getByLabel("Email");
>         this.passwordInput = page.getByLabel("Password");
>         this.loginButton = page.getByRole(AriaRole.BUTTON, 
>             new Page.GetByRoleOptions().setName("Sign In"));
>     }
>     
>     public void navigate() {
>         page.navigate("https://example.com/login");
>     }
>     
>     public DashboardPage login(String email, String password) {
>         usernameInput.fill(email);
>         passwordInput.fill(password);
>         loginButton.click();
>         return new DashboardPage(page);
>     }
> }
> 
> // Test usage
> @Test
> void testLogin() {
>     LoginPage login = new LoginPage(page);
>     login.navigate();
>     DashboardPage dashboard = login.login("user@test.com", "pass");
>     assertThat(dashboard.getWelcome()).containsText("Welcome");
> }
> ```

---

**Q12: How do you run tests in parallel and across browsers?**

> **Answer:**
> ```java
> // JUnit 5 parallel execution
> // junit-platform.properties
> junit.jupiter.execution.parallel.enabled=true
> junit.jupiter.execution.parallel.mode.default=concurrent
> 
> // Cross-browser testing
> @ParameterizedTest
> @MethodSource("browsers")
> void testAcrossBrowsers(BrowserType browserType) {
>     Browser browser = browserType.launch();
>     Page page = browser.newPage();
>     page.navigate("https://example.com");
>     assertThat(page).hasTitle("Example");
>     browser.close();
> }
> 
> static Stream<BrowserType> browsers() {
>     Playwright pw = Playwright.create();
>     return Stream.of(pw.chromium(), pw.firefox(), pw.webkit());
> }
> ```

---

**Q13: How do you handle authentication and cookies?**

> **Answer:**
> ```java
> // Save authentication state
> context.storageState(new BrowserContext.StorageStateOptions()
>     .setPath(Paths.get("auth.json")));
> 
> // Reuse authentication
> BrowserContext context = browser.newContext(
>     new Browser.NewContextOptions()
>         .setStorageStatePath(Paths.get("auth.json")));
> 
> // Set cookies
> context.addCookies(Arrays.asList(
>     new Cookie("session", "abc123")
>         .setDomain("example.com")
>         .setPath("/")
> ));
> 
> // HTTP credentials
> context = browser.newContext(new Browser.NewContextOptions()
>     .setHttpCredentials("username", "password"));
> ```

---

**Q14: What are best practices for Playwright tests?**

> **Answer:**
> 1. **Use resilient locators:** `getByRole`, `getByTestId` over CSS
> 2. **Avoid hardcoded waits:** Trust auto-waiting
> 3. **Isolate tests:** Each test gets fresh context
> 4. **Use Page Objects:** Maintainable code
> 5. **Enable tracing:** For debugging failures
> 6. **Run in CI:** Headless mode for pipelines
> 7. **Mock external APIs:** Faster, more reliable tests
