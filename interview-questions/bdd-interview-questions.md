# BDD Interview Questions (Cucumber & Behave)

## Beginner Level

**Q1: What is BDD and how does it differ from TDD?**

> **Answer:**
>
> **BDD (Behavior-Driven Development):**
> - Describes system behavior in natural language
> - Focuses on user/business requirements
> - Uses Given-When-Then format
> - Readable by non-technical stakeholders
>
> **TDD (Test-Driven Development):**
> - Write failing test first, then code
> - Focuses on technical implementation
> - Tests written in programming language
> - Primarily for developers
>
> | Aspect | TDD | BDD |
> |--------|-----|-----|
> | Focus | Code correctness | Behavior |
> | Language | Programming language | Natural language |
> | Audience | Developers | Everyone |
> | Starting point | Unit test | User story |
>
> **BDD Example:**
> ```gherkin
> Scenario: Successful login
>   Given I am on the login page
>   When I enter valid credentials
>   Then I should see the dashboard
> ```

---

**Q2: Explain Gherkin syntax and its keywords.**

> **Answer:** Gherkin is the language used to write BDD scenarios.
>
> **Keywords:**
>
> | Keyword | Purpose |
> |---------|---------|
> | `Feature` | High-level description |
> | `Scenario` | Single test case |
> | `Scenario Outline` | Template for data-driven tests |
> | `Background` | Common steps for all scenarios |
> | `Given` | Initial context/precondition |
> | `When` | Action being performed |
> | `Then` | Expected outcome |
> | `And` / `But` | Additional steps |
> | `Examples` | Data table for outline |
> | `@tag` | Tags for organization |
>
> **Example feature file:**
> ```gherkin
> @login @critical
> Feature: User Authentication
>   As a registered user
>   I want to log into my account
>   So that I can access my personalized content
> 
>   Background:
>     Given the login page is displayed
> 
>   @smoke
>   Scenario: Successful login
>     When I enter username "alice@test.com"
>     And I enter password "password123"
>     And I click the login button
>     Then I should be redirected to the dashboard
>     And I should see "Welcome, Alice"
> 
>   @negative
>   Scenario: Invalid credentials
>     When I enter username "alice@test.com"
>     And I enter password "wrongpassword"
>     And I click the login button
>     Then I should see error "Invalid credentials"
>     And I should remain on the login page
> ```

---

**Q3: What is a Scenario Outline and when do you use it?**

> **Answer:** Scenario Outline is a template for running the same scenario with different data sets.
>
> ```gherkin
> Scenario Outline: Login validation
>   Given I am on the login page
>   When I enter username "<username>"
>   And I enter password "<password>"
>   And I click login
>   Then I should see "<result>"
> 
>   Examples:
>     | username          | password  | result                |
>     | admin@test.com    | admin123  | Welcome, Admin        |
>     | user@test.com     | user123   | Welcome, User         |
>     | invalid@test.com  | wrong     | Invalid credentials   |
>     |                   | password  | Username required     |
>     | user@test.com     |           | Password required     |
> ```
>
> **Benefits:**
> - Reduces duplication
> - Tests multiple data combinations
> - Easier to maintain
> - Clear data visibility
>
> **When to use:**
> - Testing with multiple input combinations
> - Validation rules with different values
> - Edge cases for same flow

---

**Q4: How do you write step definitions in Cucumber (Java)?**

> **Answer:**
>
> ```java
> import io.cucumber.java.en.*;
> import static org.junit.jupiter.api.Assertions.*;
> 
> public class LoginSteps {
>     private LoginPage loginPage;
>     private DashboardPage dashboardPage;
>     
>     @Given("I am on the login page")
>     public void iAmOnTheLoginPage() {
>         loginPage = new LoginPage(driver);
>         loginPage.navigate();
>         assertTrue(loginPage.isDisplayed());
>     }
>     
>     @When("I enter username {string}")
>     public void iEnterUsername(String username) {
>         loginPage.enterUsername(username);
>     }
>     
>     @When("I enter password {string}")
>     public void iEnterPassword(String password) {
>         loginPage.enterPassword(password);
>     }
>     
>     @When("I click the login button")
>     public void iClickLoginButton() {
>         dashboardPage = loginPage.clickLogin();
>     }
>     
>     @Then("I should be redirected to the dashboard")
>     public void iShouldBeRedirectedToDashboard() {
>         assertTrue(dashboardPage.isDisplayed());
>     }
>     
>     @Then("I should see {string}")
>     public void iShouldSee(String expectedText) {
>         assertTrue(dashboardPage.containsText(expectedText));
>     }
> }
> ```

---

**Q5: How do you write step definitions in Behave (Python)?**

> **Answer:**
>
> ```python
> # features/steps/login_steps.py
> from behave import given, when, then
> from pages.login_page import LoginPage
> from pages.dashboard_page import DashboardPage
> 
> @given('I am on the login page')
> def step_on_login_page(context):
>     context.login_page = LoginPage(context.driver)
>     context.login_page.navigate()
>     assert context.login_page.is_displayed()
> 
> @when('I enter username "{username}"')
> def step_enter_username(context, username):
>     context.login_page.enter_username(username)
> 
> @when('I enter password "{password}"')
> def step_enter_password(context, password):
>     context.login_page.enter_password(password)
> 
> @when('I click the login button')
> def step_click_login(context):
>     context.dashboard_page = context.login_page.click_login()
> 
> @then('I should be redirected to the dashboard')
> def step_on_dashboard(context):
>     assert context.dashboard_page.is_displayed()
> 
> @then('I should see "{text}"')
> def step_should_see(context, text):
>     assert text in context.dashboard_page.get_page_text()
> ```

---

## Intermediate Level

**Q6: How do you use hooks in Cucumber and Behave?**

> **Answer:** Hooks run setup/teardown code before and after scenarios.
>
> **Cucumber (Java):**
> ```java
> import io.cucumber.java.*;
> 
> public class Hooks {
>     
>     @Before
>     public void setUp(Scenario scenario) {
>         driver = new ChromeDriver();
>         System.out.println("Starting: " + scenario.getName());
>     }
>     
>     @After
>     public void tearDown(Scenario scenario) {
>         if (scenario.isFailed()) {
>             byte[] screenshot = ((TakesScreenshot) driver)
>                 .getScreenshotAs(OutputType.BYTES);
>             scenario.attach(screenshot, "image/png", "failure");
>         }
>         driver.quit();
>     }
>     
>     @Before("@database")
>     public void setUpDatabase() {
>         // Only for @database tagged scenarios
>         database.reset();
>     }
>     
>     @BeforeStep
>     public void beforeStep() {
>         // Before each step
>     }
>     
>     @AfterStep
>     public void afterStep(Scenario scenario) {
>         // After each step
>     }
> }
> ```
>
> **Behave (Python):**
> ```python
> # features/environment.py
> from selenium import webdriver
> 
> def before_all(context):
>     """Runs once before all tests"""
>     context.config.setup_logging()
> 
> def before_scenario(context, scenario):
>     """Runs before each scenario"""
>     context.driver = webdriver.Chrome()
>     context.driver.maximize_window()
> 
> def after_scenario(context, scenario):
>     """Runs after each scenario"""
>     if scenario.status == "failed":
>         context.driver.save_screenshot(f"screenshots/{scenario.name}.png")
>     context.driver.quit()
> 
> def before_tag(context, tag):
>     """Runs before scenarios with specific tag"""
>     if tag == "database":
>         context.db = setup_database()
> 
> def after_tag(context, tag):
>     if tag == "database":
>         context.db.rollback()
> ```

---

**Q7: How do you use data tables in BDD?**

> **Answer:**
>
> **Gherkin:**
> ```gherkin
> Scenario: Create multiple users
>   Given the following users exist:
>     | name    | email           | role   |
>     | Alice   | alice@test.com  | admin  |
>     | Bob     | bob@test.com    | user   |
>     | Charlie | charlie@test.com| user   |
>   When I view the user list
>   Then I should see 3 users
> ```
>
> **Cucumber (Java):**
> ```java
> @Given("the following users exist:")
> public void usersExist(DataTable dataTable) {
>     List<Map<String, String>> users = dataTable.asMaps();
>     for (Map<String, String> user : users) {
>         userService.create(
>             user.get("name"),
>             user.get("email"),
>             user.get("role")
>         );
>     }
> }
> 
> // Or with POJO
> @Given("the following users exist:")
> public void usersExist(List<User> users) {
>     users.forEach(userService::create);
> }
> ```
>
> **Behave (Python):**
> ```python
> @given('the following users exist')
> def step_users_exist(context):
>     for row in context.table:
>         user = {
>             'name': row['name'],
>             'email': row['email'],
>             'role': row['role']
>         }
>         context.user_service.create(user)
> ```

---

**Q8: How do you run specific scenarios using tags?**

> **Answer:**
>
> **Tagging scenarios:**
> ```gherkin
> @smoke @critical
> Feature: Login
> 
>   @positive
>   Scenario: Valid login
>     ...
>   
>   @negative @slow
>   Scenario: Invalid login
>     ...
> ```
>
> **Running by tag:**
>
> **Cucumber (Java/Maven):**
> ```bash
> # Run smoke tests
> mvn test -Dcucumber.filter.tags="@smoke"
> 
> # Run positive but not slow
> mvn test -Dcucumber.filter.tags="@positive and not @slow"
> 
> # Run smoke or critical
> mvn test -Dcucumber.filter.tags="@smoke or @critical"
> ```
>
> **Behave (Python):**
> ```bash
> # Run smoke tests
> behave --tags=@smoke
> 
> # Run positive but not slow
> behave --tags="@positive" --tags="~@slow"
> 
> # Run smoke or critical
> behave --tags="@smoke,@critical"
> ```

---

## Advanced Level

**Q9: How do you integrate BDD with Page Object Model?**

> **Answer:**
>
> ```java
> // Page Object
> public class LoginPage {
>     private WebDriver driver;
>     private By username = By.id("username");
>     private By password = By.id("password");
>     private By loginBtn = By.id("login-btn");
>     
>     public LoginPage(WebDriver driver) {
>         this.driver = driver;
>     }
>     
>     public void enterCredentials(String user, String pass) {
>         driver.findElement(username).sendKeys(user);
>         driver.findElement(password).sendKeys(pass);
>     }
>     
>     public DashboardPage clickLogin() {
>         driver.findElement(loginBtn).click();
>         return new DashboardPage(driver);
>     }
> }
> 
> // Step Definitions use Page Objects
> public class LoginSteps {
>     private final WebDriver driver;
>     private LoginPage loginPage;
>     private DashboardPage dashboardPage;
>     
>     public LoginSteps() {
>         this.driver = DriverManager.getDriver();
>     }
>     
>     @When("I login with {string} and {string}")
>     public void iLoginWith(String user, String pass) {
>         loginPage = new LoginPage(driver);
>         loginPage.enterCredentials(user, pass);
>         dashboardPage = loginPage.clickLogin();
>     }
>     
>     @Then("I should see the dashboard")
>     public void iShouldSeeTheDashboard() {
>         assertTrue(dashboardPage.isDisplayed());
>     }
> }
> ```
>
> **Benefits:**
> - Step definitions stay slim
> - Reusable page actions
> - Easy to maintain locators
> - Clear separation of concerns

---

**Q10: How do you handle state sharing between steps?**

> **Answer:**
>
> **Cucumber with Dependency Injection (PicoContainer):**
> ```java
> // Shared context class
> public class TestContext {
>     private WebDriver driver;
>     private User currentUser;
>     private Order lastOrder;
>     
>     // Getters and setters
> }
> 
> // Steps inject context
> public class OrderSteps {
>     private TestContext context;
>     
>     public OrderSteps(TestContext context) {
>         this.context = context;  // Injected automatically
>     }
>     
>     @When("I create an order")
>     public void createOrder() {
>         Order order = new Order(context.getCurrentUser());
>         context.setLastOrder(order);
>     }
>     
>     @Then("the order should be saved")
>     public void orderSaved() {
>         assertNotNull(context.getLastOrder().getId());
>     }
> }
> ```
>
> **Behave (Python) - Using context:**
> ```python
> # Context object is passed to all steps
> 
> @given('I am logged in as "{username}"')
> def step_logged_in(context, username):
>     context.current_user = login(username)
> 
> @when('I create an order for "{product}"')
> def step_create_order(context, product):
>     context.last_order = create_order(
>         context.current_user, 
>         product
>     )
> 
> @then('the order should be confirmed')
> def step_order_confirmed(context):
>     assert context.last_order.status == "confirmed"
> ```
