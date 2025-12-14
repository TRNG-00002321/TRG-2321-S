# Integrating REST Assured with Test Frameworks

## Learning Objectives
- Integrate REST Assured with JUnit 5 for structured API testing
- Understand TestNG integration patterns for REST Assured
- Use Hamcrest matchers effectively for expressive assertions
- Implement parallel test execution for faster feedback
- Configure test reporting for REST Assured tests

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, REST Assured provides the API testing capabilities, but test frameworks provide the structure, organization, and execution model. Without proper framework integration, you'd have a collection of methods rather than a maintainable, scalable test suite.

JUnit 5, which you mastered in Week 6, brings lifecycle management, parameterized testing, and powerful assertions to your API tests. Combining REST Assured's fluent API with JUnit 5's testing capabilities creates a powerful, professional-grade test automation solution.

## REST Assured with JUnit 5

### Project Setup

```xml
<!-- pom.xml dependencies -->
<dependencies>
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
    
    <!-- Hamcrest (included with REST Assured but explicit is better) -->
    <dependency>
        <groupId>org.hamcrest</groupId>
        <artifactId>hamcrest</artifactId>
        <version>2.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.2</version>
        </plugin>
    </plugins>
</build>
```

### Basic Test Structure

```java
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@DisplayName("User API Tests")
class UserApiTest {
    
    private static RequestSpecification requestSpec;
    
    @BeforeAll
    static void setupAll() {
        // One-time setup for all tests
        RestAssured.baseURI = "https://api.example.com";
        RestAssured.basePath = "/api/v1";
        
        requestSpec = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("Authorization", "Bearer " + getAuthToken());
    }
    
    @BeforeEach
    void setupEach(TestInfo testInfo) {
        System.out.println("Running: " + testInfo.getDisplayName());
    }
    
    @AfterEach
    void teardownEach() {
        // Cleanup after each test if needed
    }
    
    @AfterAll
    static void teardownAll() {
        RestAssured.reset();
    }
    
    @Test
    @DisplayName("Should return user when valid ID provided")
    void testGetUserById() {
        given()
            .spec(requestSpec)
            .pathParam("userId", 123)
        .when()
            .get("/users/{userId}")
        .then()
            .statusCode(200)
            .body("id", equalTo(123))
            .body("name", notNullValue());
    }
    
    @Test
    @DisplayName("Should return 404 when user not found")
    void testGetUserNotFound() {
        given()
            .spec(requestSpec)
            .pathParam("userId", 999999)
        .when()
            .get("/users/{userId}")
        .then()
            .statusCode(404)
            .body("error.code", equalTo("NOT_FOUND"));
    }
    
    private static String getAuthToken() {
        return "test-token";
    }
}
```

### JUnit 5 Lifecycle with REST Assured

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiTestWithLifecycle {
    
    private RequestSpecification requestSpec;
    private int testUserId;
    
    @BeforeAll
    void setupOnce() {
        // Create test data once for all tests
        requestSpec = given()
            .baseUri("https://api.example.com")
            .contentType(ContentType.JSON);
        
        // Create a test user to use in all tests
        testUserId = given()
            .spec(requestSpec)
            .body("{\"name\": \"Test User\", \"email\": \"test@example.com\"}")
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .extract()
            .path("id");
    }
    
    @AfterAll
    void cleanupOnce() {
        // Delete test user after all tests
        given()
            .spec(requestSpec)
            .pathParam("userId", testUserId)
        .when()
            .delete("/users/{userId}");
    }
    
    @Test
    void testGetTestUser() {
        given()
            .spec(requestSpec)
            .pathParam("userId", testUserId)
        .when()
            .get("/users/{userId}")
        .then()
            .statusCode(200)
            .body("id", equalTo(testUserId));
    }
    
    @Test
    void testUpdateTestUser() {
        given()
            .spec(requestSpec)
            .pathParam("userId", testUserId)
            .body("{\"name\": \"Updated Name\"}")
        .when()
            .patch("/users/{userId}")
        .then()
            .statusCode(200)
            .body("name", equalTo("Updated Name"));
    }
}
```

### Parameterized API Tests

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class ParameterizedApiTest {
    
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 10, 100})
    @DisplayName("Should return user for valid IDs")
    void testGetUserByVariousIds(int userId) {
        given()
            .baseUri("https://api.example.com")
            .pathParam("userId", userId)
        .when()
            .get("/users/{userId}")
        .then()
            .statusCode(200)
            .body("id", equalTo(userId));
    }
    
    @ParameterizedTest
    @CsvSource({
        "admin, ADMIN",
        "user, USER",
        "guest, GUEST"
    })
    @DisplayName("Should filter users by role")
    void testFilterUsersByRole(String roleParam, String expectedRole) {
        given()
            .baseUri("https://api.example.com")
            .queryParam("role", roleParam)
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .body("users.role", everyItem(equalTo(expectedRole)));
    }
    
    @ParameterizedTest
    @MethodSource("invalidUserDataProvider")
    @DisplayName("Should return 400 for invalid user data")
    void testCreateUserWithInvalidData(String name, String email, String expectedError) {
        String body = String.format("{\"name\": \"%s\", \"email\": \"%s\"}", name, email);
        
        given()
            .baseUri("https://api.example.com")
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post("/users")
        .then()
            .statusCode(400)
            .body("error.message", containsString(expectedError));
    }
    
    static Stream<Arguments> invalidUserDataProvider() {
        return Stream.of(
            Arguments.of("", "valid@email.com", "name"),
            Arguments.of("Valid Name", "invalid-email", "email"),
            Arguments.of("", "", "name"),
            Arguments.of("A", "a@b.c", "name")  // Too short
        );
    }
    
    @ParameterizedTest
    @CsvFileSource(resources = "/testdata/users.csv", numLinesToSkip = 1)
    @DisplayName("Should create users from CSV data")
    void testCreateUsersFromCsv(String name, String email, String role) {
        given()
            .baseUri("https://api.example.com")
            .contentType(ContentType.JSON)
            .body(Map.of("name", name, "email", email, "role", role))
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .body("name", equalTo(name))
            .body("email", equalTo(email));
    }
}
```

### Nested Tests for API Resources

```java
@DisplayName("User API")
class UserApiNestedTest {
    
    private static RequestSpecification requestSpec;
    
    @BeforeAll
    static void setup() {
        requestSpec = given()
            .baseUri("https://api.example.com")
            .contentType(ContentType.JSON);
    }
    
    @Nested
    @DisplayName("GET /users")
    class GetUsers {
        
        @Test
        @DisplayName("returns list of users")
        void returnsUserList() {
            given()
                .spec(requestSpec)
            .when()
                .get("/users")
            .then()
                .statusCode(200)
                .body("users", not(empty()));
        }
        
        @Test
        @DisplayName("supports pagination")
        void supportsPagination() {
            given()
                .spec(requestSpec)
                .queryParam("page", 1)
                .queryParam("pageSize", 5)
            .when()
                .get("/users")
            .then()
                .statusCode(200)
                .body("users.size()", lessThanOrEqualTo(5))
                .body("pagination.page", equalTo(1));
        }
    }
    
    @Nested
    @DisplayName("GET /users/{id}")
    class GetUserById {
        
        @Test
        @DisplayName("returns user when exists")
        void returnsUserWhenExists() {
            given()
                .spec(requestSpec)
                .pathParam("id", 1)
            .when()
                .get("/users/{id}")
            .then()
                .statusCode(200)
                .body("id", equalTo(1));
        }
        
        @Test
        @DisplayName("returns 404 when not exists")
        void returns404WhenNotExists() {
            given()
                .spec(requestSpec)
                .pathParam("id", 999999)
            .when()
                .get("/users/{id}")
            .then()
                .statusCode(404);
        }
    }
    
    @Nested
    @DisplayName("POST /users")
    class CreateUser {
        
        @Test
        @DisplayName("creates user with valid data")
        void createsUserWithValidData() {
            given()
                .spec(requestSpec)
                .body(Map.of("name", "Test", "email", "test@example.com"))
            .when()
                .post("/users")
            .then()
                .statusCode(201)
                .body("id", notNullValue());
        }
        
        @Test
        @DisplayName("returns 400 with invalid data")
        void returns400WithInvalidData() {
            given()
                .spec(requestSpec)
                .body(Map.of("name", "", "email", "invalid"))
            .when()
                .post("/users")
            .then()
                .statusCode(400);
        }
    }
}
```

## TestNG Integration

### TestNG Setup

```xml
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.8.0</version>
    <scope>test</scope>
</dependency>
```

### TestNG Test Structure

```java
import org.testng.annotations.*;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserApiTestNG {
    
    @BeforeSuite
    public void setupSuite() {
        RestAssured.baseURI = "https://api.example.com";
    }
    
    @BeforeClass
    public void setupClass() {
        // Class-level setup
    }
    
    @BeforeMethod
    public void setupMethod() {
        // Method-level setup
    }
    
    @Test(groups = {"smoke"})
    public void testGetUsers() {
        given()
        .when()
            .get("/users")
        .then()
            .statusCode(200);
    }
    
    @Test(groups = {"regression"}, dependsOnMethods = {"testCreateUser"})
    public void testGetCreatedUser() {
        // Depends on testCreateUser
    }
    
    @Test(dataProvider = "userIds")
    public void testGetUserById(int userId) {
        given()
            .pathParam("id", userId)
        .when()
            .get("/users/{id}")
        .then()
            .statusCode(200);
    }
    
    @DataProvider(name = "userIds")
    public Object[][] userIdProvider() {
        return new Object[][] {
            {1}, {2}, {3}
        };
    }
    
    @AfterSuite
    public void teardownSuite() {
        RestAssured.reset();
    }
}
```

## Hamcrest Matchers for REST Assured

### Common Matchers

```java
import static org.hamcrest.Matchers.*;

// Equality
.body("name", equalTo("John"))
.body("id", is(123))

// Null checks
.body("id", notNullValue())
.body("deletedAt", nullValue())

// String matchers
.body("name", containsString("John"))
.body("email", startsWith("john"))
.body("email", endsWith("@example.com"))
.body("description", not(emptyString()))
.body("name", matchesPattern("[A-Z][a-z]+"))

// Numeric matchers
.body("age", greaterThan(18))
.body("age", lessThan(100))
.body("age", greaterThanOrEqualTo(18))
.body("price", closeTo(10.0, 0.1))

// Collection matchers
.body("tags", hasSize(3))
.body("tags", hasItem("java"))
.body("tags", hasItems("java", "testing"))
.body("tags", contains("a", "b", "c"))  // Exact order
.body("tags", containsInAnyOrder("c", "a", "b"))
.body("users", not(empty()))
.body("users", everyItem(hasKey("id")))

// Combining matchers
.body("age", allOf(greaterThan(18), lessThan(100)))
.body("status", anyOf(equalTo("active"), equalTo("pending")))
.body("name", not(equalTo("Admin")))
```

### Custom Matchers

```java
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class IsValidEmail extends TypeSafeMatcher<String> {
    
    private static final String EMAIL_REGEX = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    
    @Override
    protected boolean matchesSafely(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }
    
    @Override
    public void describeTo(Description description) {
        description.appendText("a valid email address");
    }
    
    public static IsValidEmail isValidEmail() {
        return new IsValidEmail();
    }
}

// Usage
import static com.example.matchers.IsValidEmail.isValidEmail;

@Test
void testUserHasValidEmail() {
    given()
    .when()
        .get("/users/123")
    .then()
        .body("email", isValidEmail());
}
```

### Response Time Matchers

```java
// Response time validation
.then()
    .time(lessThan(2000L))  // milliseconds
    .time(lessThanOrEqualTo(1500L))
    .time(greaterThan(100L))  // Not too fast (suspicious)
```

## Parallel Test Execution

### JUnit 5 Parallel Configuration

Create `src/test/resources/junit-platform.properties`:

```properties
# Enable parallel execution
junit.jupiter.execution.parallel.enabled=true

# Default execution mode for classes
junit.jupiter.execution.parallel.mode.default=concurrent

# Default execution mode for methods within a class
junit.jupiter.execution.parallel.mode.classes.default=concurrent

# Maximum number of threads
junit.jupiter.execution.parallel.config.strategy=fixed
junit.jupiter.execution.parallel.config.fixed.parallelism=4
```

### Thread-Safe Test Design

```java
@Execution(ExecutionMode.CONCURRENT)
class ParallelApiTest {
    
    // Each test should be independent
    @Test
    void testA() {
        // Use unique test data
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("email", uniqueEmail))
        .when()
            .post("/users")
        .then()
            .statusCode(201);
    }
    
    @Test
    void testB() {
        // Independent test
        given()
        .when()
            .get("/status")
        .then()
            .statusCode(200);
    }
}
```

### Maven Surefire Parallel Configuration

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.2</version>
    <configuration>
        <parallel>methods</parallel>
        <threadCount>4</threadCount>
        <perCoreThreadCount>true</perCoreThreadCount>
    </configuration>
</plugin>
```

## Reporting with REST Assured

### Maven Surefire Reports

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-report-plugin</artifactId>
    <version>3.2.2</version>
</plugin>
```

Run: `mvn surefire-report:report`

### Allure Reporting (Preview)

> **Note:** We covered Allure in detail during Week 6. Here's a quick integration pattern for REST Assured.

```xml
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-junit5</artifactId>
    <version>2.24.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-rest-assured</artifactId>
    <version>2.24.0</version>
    <scope>test</scope>
</dependency>
```

```java
import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;

@Epic("User Management")
@Feature("User API")
class UserApiWithAllure {
    
    @Test
    @Story("Get User")
    @Description("Verify that user can be retrieved by ID")
    @Severity(SeverityLevel.CRITICAL)
    void testGetUser() {
        given()
            .filter(new AllureRestAssured())  // Attach request/response to report
            .pathParam("id", 123)
        .when()
            .get("/users/{id}")
        .then()
            .statusCode(200);
    }
}
```

### Custom Logging Filter

```java
public class CustomLoggingFilter implements Filter {
    
    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                          FilterableResponseSpecification responseSpec,
                          FilterContext ctx) {
        
        // Log request
        System.out.println("=== REQUEST ===");
        System.out.println("Method: " + requestSpec.getMethod());
        System.out.println("URI: " + requestSpec.getURI());
        System.out.println("Headers: " + requestSpec.getHeaders());
        
        // Execute request
        Response response = ctx.next(requestSpec, responseSpec);
        
        // Log response
        System.out.println("=== RESPONSE ===");
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody().asPrettyString());
        
        return response;
    }
}

// Usage
given()
    .filter(new CustomLoggingFilter())
.when()
    .get("/users")
```

## Complete Integration Example

```java
@DisplayName("User API Integration Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserApiIntegrationTest {
    
    private static final String BASE_URI = "https://api.example.com";
    private RequestSpecification requestSpec;
    private ResponseSpecification successSpec;
    
    @BeforeAll
    void setupOnce() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        
        requestSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setBasePath("/api/v1")
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addHeader("Authorization", "Bearer " + getToken())
            .addFilter(new AllureRestAssured())
            .build();
        
        successSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType(ContentType.JSON)
            .expectResponseTime(lessThan(2000L))
            .build();
    }
    
    @AfterAll
    void teardownOnce() {
        RestAssured.reset();
    }
    
    @Nested
    @DisplayName("User CRUD Operations")
    class UserCrudTests {
        
        private int createdUserId;
        
        @Test
        @Order(1)
        @DisplayName("Create new user")
        void testCreateUser() {
            Map<String, Object> userData = Map.of(
                "name", "Integration Test User",
                "email", "integration" + System.currentTimeMillis() + "@test.com"
            );
            
            createdUserId = given()
                .spec(requestSpec)
                .body(userData)
            .when()
                .post("/users")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo(userData.get("name")))
                .extract()
                .path("id");
        }
        
        @Test
        @Order(2)
        @DisplayName("Read created user")
        void testGetCreatedUser() {
            given()
                .spec(requestSpec)
                .pathParam("userId", createdUserId)
            .when()
                .get("/users/{userId}")
            .then()
                .spec(successSpec)
                .body("id", equalTo(createdUserId));
        }
        
        @Test
        @Order(3)
        @DisplayName("Update user")
        void testUpdateUser() {
            given()
                .spec(requestSpec)
                .pathParam("userId", createdUserId)
                .body(Map.of("name", "Updated Name"))
            .when()
                .patch("/users/{userId}")
            .then()
                .statusCode(200)
                .body("name", equalTo("Updated Name"));
        }
        
        @Test
        @Order(4)
        @DisplayName("Delete user")
        void testDeleteUser() {
            given()
                .spec(requestSpec)
                .pathParam("userId", createdUserId)
            .when()
                .delete("/users/{userId}")
            .then()
                .statusCode(anyOf(equalTo(200), equalTo(204)));
            
            // Verify deletion
            given()
                .spec(requestSpec)
                .pathParam("userId", createdUserId)
            .when()
                .get("/users/{userId}")
            .then()
                .statusCode(404);
        }
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"active", "inactive", "pending"})
    @DisplayName("Filter users by status")
    void testFilterByStatus(String status) {
        given()
            .spec(requestSpec)
            .queryParam("status", status)
        .when()
            .get("/users")
        .then()
            .spec(successSpec)
            .body("users.status", everyItem(equalToIgnoringCase(status)));
    }
    
    private String getToken() {
        return System.getenv().getOrDefault("API_TOKEN", "test-token");
    }
}
```

## Summary

- **JUnit 5 integration** provides lifecycle management, assertions, and test organization
- **Parameterized tests** enable data-driven API testing with multiple inputs
- **Nested tests** organize tests by endpoint or feature for clarity
- **Hamcrest matchers** offer expressive, readable assertions
- **Parallel execution** speeds up test suites with proper configuration
- **Reporting tools** like Allure provide visibility into test results

These integration patterns create maintainable, scalable API test suites that fit naturally into CI/CD pipelines. In the next lesson, you'll explore Python's approach to API testing with the Requests module.

## Additional Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) - Official documentation
- [Hamcrest Tutorial](http://hamcrest.org/JavaHamcrest/tutorial) - Matcher patterns
- [Allure Framework](https://docs.qameta.io/allure/) - Reporting documentation

