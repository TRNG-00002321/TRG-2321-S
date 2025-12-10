# Integrating Allure into Tests: JUnit5 and Pytest

## Learning Objectives
- Add Allure annotations to JUnit5 tests
- Use Allure decorators with Pytest
- Create meaningful steps and descriptions
- Attach evidence to test results

## Why This Matters

Allure reports are only as good as the information you provide. Learning to annotate your tests with steps, descriptions, and attachments transforms basic pass/fail results into rich, self-documenting test cases that tell a complete story.

## The Concept

### Allure with JUnit5

**Dependencies (Maven):**
```xml
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-junit5</artifactId>
    <version>2.24.0</version>
    <scope>test</scope>
</dependency>
```

**Key Annotations:**

```java
import io.qameta.allure.*;

@Epic("User Management")
@Feature("Authentication")
@Story("Login")
public class LoginTest {

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify user can login with valid credentials")
    @Owner("qa-team")
    @Link(name = "JIRA-1234", type = "issue")
    void testSuccessfulLogin() {
        // Test code
    }
}
```

### JUnit5 Steps

```java
import io.qameta.allure.Step;

public class LoginTest {

    @Test
    void testLogin() {
        navigateToLoginPage();
        enterCredentials("user@example.com", "password123");
        clickLoginButton();
        verifyDashboardDisplayed();
    }
    
    @Step("Navigate to login page")
    void navigateToLoginPage() {
        // Implementation
    }
    
    @Step("Enter credentials: {email}")
    void enterCredentials(String email, String password) {
        // Implementation
    }
    
    @Step("Click login button")
    void clickLoginButton() {
        // Implementation
    }
    
    @Step("Verify dashboard is displayed")
    void verifyDashboardDisplayed() {
        // Implementation
    }
}
```

### JUnit5 Attachments

```java
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;

public class LoginTest {

    @Test
    void testWithAttachments() {
        // Programmatic attachment
        Allure.addAttachment("Request Body", "application/json", 
            "{\"user\": \"test\"}", "json");
        
        // Screenshot attachment
        byte[] screenshot = takeScreenshot();
        Allure.addAttachment("Screenshot", "image/png", 
            new ByteArrayInputStream(screenshot), "png");
    }
    
    // Annotation-based attachment
    @Attachment(value = "Page Source", type = "text/html")
    public String savePageSource() {
        return driver.getPageSource();
    }
}
```

### Allure with Pytest

**Installation:**
```bash
pip install allure-pytest
```

**Key Decorators:**

```python
import allure

@allure.epic("User Management")
@allure.feature("Authentication")
@allure.story("Login")
@allure.severity(allure.severity_level.CRITICAL)
@allure.title("Successful login with valid credentials")
def test_successful_login():
    pass
```

### Pytest Steps

```python
import allure

def test_login():
    with allure.step("Navigate to login page"):
        driver.get("/login")
    
    with allure.step("Enter credentials"):
        driver.find_element("id", "email").send_keys("user@example.com")
        driver.find_element("id", "password").send_keys("password123")
    
    with allure.step("Click login button"):
        driver.find_element("id", "submit").click()
    
    with allure.step("Verify dashboard displayed"):
        assert "Dashboard" in driver.title
```

### Pytest Attachments

```python
import allure

def test_with_attachments():
    # Text attachment
    allure.attach("Test data", name="Input", 
                  attachment_type=allure.attachment_type.TEXT)
    
    # JSON attachment
    allure.attach('{"status": "ok"}', name="Response",
                  attachment_type=allure.attachment_type.JSON)
    
    # Screenshot
    allure.attach(driver.get_screenshot_as_png(), name="Screenshot",
                  attachment_type=allure.attachment_type.PNG)
    
    # File attachment
    allure.attach.file("./logs/test.log", name="Log File",
                       attachment_type=allure.attachment_type.TEXT)
```

## Code Example

### Complete Annotated Test Suite

**JUnit5:**
```java
@Epic("E-Commerce")
@Feature("Shopping Cart")
public class ShoppingCartTest {

    @Test
    @Story("Add to Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify product can be added to cart")
    void testAddToCart() {
        searchProduct("Laptop");
        selectFirstResult();
        addToCart();
        verifyCartUpdated();
    }
    
    @Step("Search for product: {keyword}")
    void searchProduct(String keyword) { }
    
    @Step("Select first search result")
    void selectFirstResult() { }
    
    @Step("Add product to cart")
    void addToCart() { }
    
    @Step("Verify cart item count updated")
    void verifyCartUpdated() { }
}
```

**Pytest:**
```python
@allure.epic("E-Commerce")
@allure.feature("Shopping Cart")
class TestShoppingCart:
    
    @allure.story("Add to Cart")
    @allure.severity(allure.severity_level.CRITICAL)
    @allure.title("Add product to shopping cart")
    def test_add_to_cart(self):
        with allure.step("Search for product"):
            search_product("Laptop")
        
        with allure.step("Select first result"):
            select_first_result()
        
        with allure.step("Add to cart"):
            add_to_cart()
        
        with allure.step("Verify cart updated"):
            assert get_cart_count() == 1
```

## Summary

- **JUnit5**: Use `@Step`, `@Description`, `@Severity`, `@Link` annotations
- **Pytest**: Use `@allure.step`, `@allure.title`, `@allure.severity` decorators
- **Steps** break tests into logical, reportable phases
- **Attachments** add evidence (screenshots, logs, data)
- **Links** connect tests to issue trackers and test management systems
- Annotations are **optional**—tests work without them, but reports are richer with them

## Additional Resources

- [Allure JUnit5 Integration](https://docs.qameta.io/allure/#_junit_5) - Java guide
- [Allure Pytest Integration](https://docs.qameta.io/allure/#_pytest) - Python guide
- [Allure Decorators Reference](https://docs.qameta.io/allure-report/frameworks/python/pytest) - All decorators

