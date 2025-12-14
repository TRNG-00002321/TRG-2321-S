# Creating Requests with REST Assured

## Learning Objectives
- Master the Given-When-Then syntax for request construction
- Implement all HTTP methods (GET, POST, PUT, PATCH, DELETE) in REST Assured
- Configure request specifications for reusable setups
- Work with headers and various authentication methods
- Handle path parameters and query parameters effectively

## Why This Matters

Building on your REST Assured foundation, this lesson focuses on constructing robust, maintainable API requests. In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, the quality of your test requests directly impacts the reliability of your test results.

A poorly constructed request can lead to false positives (tests pass when they shouldn't) or false negatives (tests fail due to request issues, not API defects). Mastering request construction ensures your tests accurately validate API behavior.

## The Given-When-Then Syntax

### Structure Overview

```java
given()     // Setup: Configure the request
    // Headers, auth, params, body, etc.
.when()     // Action: Specify the HTTP method and endpoint
    // .get(), .post(), .put(), .patch(), .delete()
.then()     // Verify: Assert response conditions
    // Status code, body, headers, etc.
```

### Complete Example

```java
@Test
void testCompleteRequestStructure() {
    given()
        // Request configuration
        .baseUri("https://api.example.com")
        .basePath("/api/v1")
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .header("Authorization", "Bearer token123")
        .queryParam("status", "active")
        .body("{\"name\": \"Test\"}")
        .log().all()  // Log request details
    .when()
        // Execute request
        .post("/users")
    .then()
        // Validate response
        .log().all()  // Log response details
        .statusCode(201)
        .contentType(ContentType.JSON)
        .body("name", equalTo("Test"))
        .time(lessThan(2000L));
}
```

## HTTP Methods in REST Assured

### GET Requests

```java
// Simple GET
@Test
void testSimpleGet() {
    given()
        .baseUri("https://api.example.com")
    .when()
        .get("/users")
    .then()
        .statusCode(200);
}

// GET with path parameter
@Test
void testGetWithPathParam() {
    given()
        .baseUri("https://api.example.com")
        .pathParam("userId", 123)
    .when()
        .get("/users/{userId}")
    .then()
        .statusCode(200)
        .body("id", equalTo(123));
}

// GET with query parameters
@Test
void testGetWithQueryParams() {
    given()
        .baseUri("https://api.example.com")
        .queryParam("page", 1)
        .queryParam("pageSize", 20)
        .queryParam("sort", "name")
        .queryParam("order", "asc")
    .when()
        .get("/users")
    .then()
        .statusCode(200)
        .body("page", equalTo(1))
        .body("users.size()", lessThanOrEqualTo(20));
}
```

### POST Requests

```java
// POST with JSON body (String)
@Test
void testPostWithJsonString() {
    String requestBody = """
        {
            "name": "John Doe",
            "email": "john@example.com",
            "role": "user"
        }
        """;
    
    given()
        .baseUri("https://api.example.com")
        .contentType(ContentType.JSON)
        .body(requestBody)
    .when()
        .post("/users")
    .then()
        .statusCode(201)
        .body("id", notNullValue())
        .body("name", equalTo("John Doe"));
}

// POST with POJO
@Test
void testPostWithPojo() {
    User user = new User();
    user.setName("Jane Doe");
    user.setEmail("jane@example.com");
    user.setRole("admin");
    
    given()
        .baseUri("https://api.example.com")
        .contentType(ContentType.JSON)
        .body(user)
    .when()
        .post("/users")
    .then()
        .statusCode(201)
        .body("name", equalTo("Jane Doe"));
}

// POST with Map
@Test
void testPostWithMap() {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("name", "Bob Smith");
    requestBody.put("email", "bob@example.com");
    requestBody.put("age", 30);
    
    given()
        .baseUri("https://api.example.com")
        .contentType(ContentType.JSON)
        .body(requestBody)
    .when()
        .post("/users")
    .then()
        .statusCode(201);
}
```

### PUT Requests

```java
// PUT - Full resource replacement
@Test
void testPutRequest() {
    String updatedUser = """
        {
            "name": "John Smith",
            "email": "john.smith@example.com",
            "role": "admin",
            "active": true
        }
        """;
    
    given()
        .baseUri("https://api.example.com")
        .contentType(ContentType.JSON)
        .pathParam("userId", 123)
        .body(updatedUser)
    .when()
        .put("/users/{userId}")
    .then()
        .statusCode(200)
        .body("name", equalTo("John Smith"))
        .body("role", equalTo("admin"));
}
```

### PATCH Requests

```java
// PATCH - Partial update
@Test
void testPatchRequest() {
    // Only update specific fields
    String partialUpdate = """
        {
            "role": "admin"
        }
        """;
    
    given()
        .baseUri("https://api.example.com")
        .contentType(ContentType.JSON)
        .pathParam("userId", 123)
        .body(partialUpdate)
    .when()
        .patch("/users/{userId}")
    .then()
        .statusCode(200)
        .body("role", equalTo("admin"));
}

// PATCH with JSON Patch format (RFC 6902)
@Test
void testJsonPatchRequest() {
    String jsonPatch = """
        [
            { "op": "replace", "path": "/name", "value": "New Name" },
            { "op": "add", "path": "/tags", "value": ["vip"] }
        ]
        """;
    
    given()
        .baseUri("https://api.example.com")
        .contentType("application/json-patch+json")
        .pathParam("userId", 123)
        .body(jsonPatch)
    .when()
        .patch("/users/{userId}")
    .then()
        .statusCode(200);
}
```

### DELETE Requests

```java
// Simple DELETE
@Test
void testDeleteRequest() {
    given()
        .baseUri("https://api.example.com")
        .pathParam("userId", 123)
    .when()
        .delete("/users/{userId}")
    .then()
        .statusCode(204);
}

// DELETE with verification
@Test
void testDeleteAndVerify() {
    // First, create a user
    int userId = given()
        .baseUri("https://api.example.com")
        .contentType(ContentType.JSON)
        .body("{\"name\": \"Temp User\", \"email\": \"temp@example.com\"}")
    .when()
        .post("/users")
    .then()
        .statusCode(201)
        .extract()
        .path("id");
    
    // Then, delete the user
    given()
        .baseUri("https://api.example.com")
        .pathParam("userId", userId)
    .when()
        .delete("/users/{userId}")
    .then()
        .statusCode(204);
    
    // Finally, verify deletion
    given()
        .baseUri("https://api.example.com")
        .pathParam("userId", userId)
    .when()
        .get("/users/{userId}")
    .then()
        .statusCode(404);
}
```

## Request Specification

### Creating Reusable Specifications

```java
public class ApiTestBase {
    
    protected static RequestSpecification baseRequestSpec;
    protected static RequestSpecification authRequestSpec;
    
    @BeforeAll
    static void setupSpecs() {
        // Base specification for all requests
        baseRequestSpec = new RequestSpecBuilder()
            .setBaseUri("https://api.example.com")
            .setBasePath("/api/v1")
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .build();
        
        // Authenticated specification
        authRequestSpec = new RequestSpecBuilder()
            .addRequestSpecification(baseRequestSpec)  // Inherit from base
            .addHeader("Authorization", "Bearer " + getAuthToken())
            .build();
    }
    
    private static String getAuthToken() {
        // Token retrieval logic
        return "eyJhbGciOiJIUzI1NiIs...";
    }
}
```

### Using Specifications in Tests

```java
public class UserApiTest extends ApiTestBase {
    
    @Test
    void testGetUsers() {
        given()
            .spec(authRequestSpec)
        .when()
            .get("/users")
        .then()
            .statusCode(200);
    }
    
    @Test
    void testCreateUser() {
        given()
            .spec(authRequestSpec)
            .body(createUserPayload())
        .when()
            .post("/users")
        .then()
            .statusCode(201);
    }
}
```

### Dynamic Specification Building

```java
public RequestSpecification buildRequestSpec(String environment) {
    String baseUri = switch (environment) {
        case "dev" -> "https://dev-api.example.com";
        case "staging" -> "https://staging-api.example.com";
        case "prod" -> "https://api.example.com";
        default -> throw new IllegalArgumentException("Unknown environment");
    };
    
    return new RequestSpecBuilder()
        .setBaseUri(baseUri)
        .setBasePath("/api/v1")
        .setContentType(ContentType.JSON)
        .build();
}
```

## Headers and Authentication

### Setting Headers

```java
// Single header
given()
    .header("X-API-Key", "abc123")

// Multiple headers
given()
    .header("X-API-Key", "abc123")
    .header("X-Request-ID", UUID.randomUUID().toString())
    .header("Accept-Language", "en-US")

// Headers from map
Map<String, String> headers = new HashMap<>();
headers.put("X-API-Key", "abc123");
headers.put("X-Client-Version", "2.1.0");

given()
    .headers(headers)

// Multiple values for same header
given()
    .header("Accept", "application/json", "application/xml")
```

### Authentication Methods

**Basic Authentication:**
```java
given()
    .auth().basic("username", "password")
.when()
    .get("/secure/resource")
```

**Preemptive Basic Auth:**
```java
// Send credentials immediately (don't wait for 401 challenge)
given()
    .auth().preemptive().basic("username", "password")
.when()
    .get("/secure/resource")
```

**Bearer Token:**
```java
given()
    .header("Authorization", "Bearer " + accessToken)
.when()
    .get("/secure/resource")

// Or using auth method
given()
    .auth().oauth2(accessToken)
.when()
    .get("/secure/resource")
```

**API Key:**
```java
// In header
given()
    .header("X-API-Key", apiKey)
.when()
    .get("/resource")

// In query parameter
given()
    .queryParam("api_key", apiKey)
.when()
    .get("/resource")
```

**OAuth 2.0:**
```java
given()
    .auth().oauth2(accessToken)
.when()
    .get("/secure/resource")
```

**Digest Authentication:**
```java
given()
    .auth().digest("username", "password")
.when()
    .get("/secure/resource")
```

### Authentication in Request Specification

```java
// Create authenticated spec
RequestSpecification authSpec = new RequestSpecBuilder()
    .setBaseUri("https://api.example.com")
    .addHeader("Authorization", "Bearer " + token)
    .build();

// Or with basic auth
RequestSpecification basicAuthSpec = new RequestSpecBuilder()
    .setBaseUri("https://api.example.com")
    .setAuth(RestAssured.basic("user", "pass"))
    .build();
```

## Path and Query Parameters

### Path Parameters

```java
// Named path parameters
@Test
void testPathParameters() {
    given()
        .baseUri("https://api.example.com")
        .pathParam("userId", 123)
        .pathParam("orderId", 456)
    .when()
        .get("/users/{userId}/orders/{orderId}")
    .then()
        .statusCode(200);
}

// Multiple path params from map
@Test
void testPathParamsFromMap() {
    Map<String, Object> pathParams = new HashMap<>();
    pathParams.put("userId", 123);
    pathParams.put("orderId", 456);
    
    given()
        .baseUri("https://api.example.com")
        .pathParams(pathParams)
    .when()
        .get("/users/{userId}/orders/{orderId}")
    .then()
        .statusCode(200);
}

// Inline path parameters
@Test
void testInlinePathParams() {
    int userId = 123;
    int orderId = 456;
    
    given()
        .baseUri("https://api.example.com")
    .when()
        .get("/users/" + userId + "/orders/" + orderId)
    .then()
        .statusCode(200);
}
```

### Query Parameters

```java
// Single parameter
given()
    .queryParam("status", "active")

// Multiple parameters
given()
    .queryParam("status", "active")
    .queryParam("page", 1)
    .queryParam("pageSize", 20)

// Parameters from map
Map<String, Object> queryParams = new HashMap<>();
queryParams.put("status", "active");
queryParams.put("page", 1);
queryParams.put("sortBy", "name");

given()
    .queryParams(queryParams)

// Multiple values for same parameter
given()
    .queryParam("tags", "java", "testing", "api")
// Results in: ?tags=java&tags=testing&tags=api

// List as parameter value
List<String> statuses = Arrays.asList("active", "pending");
given()
    .queryParam("status", statuses)
```

### Parameter Encoding

```java
// Automatic URL encoding (default)
given()
    .queryParam("search", "hello world")  // Encoded as hello%20world
    .queryParam("filter", "name=John&age>25")  // Special chars encoded

// Disable encoding for pre-encoded values
given()
    .urlEncodingEnabled(false)
    .queryParam("encoded", "already%20encoded")
```

### Form Parameters

```java
// For application/x-www-form-urlencoded
@Test
void testFormParams() {
    given()
        .baseUri("https://api.example.com")
        .contentType(ContentType.URLENC)
        .formParam("username", "john")
        .formParam("password", "secret123")
    .when()
        .post("/login")
    .then()
        .statusCode(200);
}

// Multiple form params from map
Map<String, String> formData = new HashMap<>();
formData.put("grant_type", "password");
formData.put("username", "user");
formData.put("password", "pass");

given()
    .contentType(ContentType.URLENC)
    .formParams(formData)
.when()
    .post("/oauth/token")
```

## Request Body

### JSON Body Options

```java
// String body
String jsonBody = "{\"name\": \"John\", \"email\": \"john@example.com\"}";
given().body(jsonBody)

// Multi-line string (Java 15+)
String jsonBody = """
    {
        "name": "John",
        "email": "john@example.com",
        "address": {
            "city": "New York",
            "zip": "10001"
        }
    }
    """;
given().body(jsonBody)

// Map body (auto-serialized to JSON)
Map<String, Object> body = Map.of(
    "name", "John",
    "email", "john@example.com",
    "age", 30
);
given()
    .contentType(ContentType.JSON)
    .body(body)

// POJO body (auto-serialized)
User user = new User("John", "john@example.com");
given()
    .contentType(ContentType.JSON)
    .body(user)
```

### File Body

```java
// Send file as body
File jsonFile = new File("src/test/resources/testdata/user.json");
given()
    .contentType(ContentType.JSON)
    .body(jsonFile)
.when()
    .post("/users")
```

### Multipart Form Data

```java
// File upload
@Test
void testFileUpload() {
    File file = new File("src/test/resources/testfile.pdf");
    
    given()
        .baseUri("https://api.example.com")
        .multiPart("file", file)
        .multiPart("description", "Test document")
    .when()
        .post("/upload")
    .then()
        .statusCode(200);
}

// Multiple files
@Test
void testMultipleFileUpload() {
    given()
        .multiPart("files", new File("file1.pdf"))
        .multiPart("files", new File("file2.pdf"))
        .multiPart("category", "documents")
    .when()
        .post("/upload/batch")
    .then()
        .statusCode(200);
}

// Custom content type for multipart
given()
    .multiPart("image", new File("photo.jpg"), "image/jpeg")
```

## Request Logging

### Log Options

```java
// Log all request details
given()
    .log().all()

// Log specific parts
given()
    .log().body()      // Only request body
    .log().headers()   // Only headers
    .log().params()    // Only parameters
    .log().method()    // Only HTTP method
    .log().uri()       // Only URI

// Log if validation fails
given()
    .log().ifValidationFails()
```

### Enabling Global Logging

```java
@BeforeAll
static void setupLogging() {
    // Log request and response when validation fails
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    
    // Or always log
    RestAssured.filters(
        new RequestLoggingFilter(),
        new ResponseLoggingFilter()
    );
}
```

## Complete Test Example

```java
public class UserApiTest {
    
    private static RequestSpecification requestSpec;
    private static final String BASE_URI = "https://api.example.com";
    
    @BeforeAll
    static void setup() {
        requestSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setBasePath("/api/v1")
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addHeader("Authorization", "Bearer " + getToken())
            .build();
    }
    
    @Test
    @DisplayName("Create user with valid data")
    void testCreateUserSuccess() {
        Map<String, Object> userData = Map.of(
            "name", "Test User " + System.currentTimeMillis(),
            "email", "test" + System.currentTimeMillis() + "@example.com",
            "role", "user"
        );
        
        int userId = given()
            .spec(requestSpec)
            .body(userData)
            .log().body()
        .when()
            .post("/users")
        .then()
            .log().ifValidationFails()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo(userData.get("name")))
            .body("email", equalTo(userData.get("email")))
            .extract()
            .path("id");
        
        // Cleanup: Delete created user
        deleteUser(userId);
    }
    
    @Test
    @DisplayName("Get user by ID")
    void testGetUserById() {
        // First create a user
        int userId = createTestUser();
        
        given()
            .spec(requestSpec)
            .pathParam("userId", userId)
        .when()
            .get("/users/{userId}")
        .then()
            .statusCode(200)
            .body("id", equalTo(userId))
            .time(lessThan(2000L));
        
        // Cleanup
        deleteUser(userId);
    }
    
    @Test
    @DisplayName("Search users with filters")
    void testSearchUsers() {
        given()
            .spec(requestSpec)
            .queryParam("role", "admin")
            .queryParam("status", "active")
            .queryParam("page", 1)
            .queryParam("pageSize", 10)
        .when()
            .get("/users/search")
        .then()
            .statusCode(200)
            .body("users", not(empty()))
            .body("users.size()", lessThanOrEqualTo(10))
            .body("users.role", everyItem(equalTo("admin")));
    }
    
    private static String getToken() {
        return "test-token-123";
    }
    
    private int createTestUser() {
        return given()
            .spec(requestSpec)
            .body(Map.of("name", "Test", "email", "test@example.com"))
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .extract()
            .path("id");
    }
    
    private void deleteUser(int userId) {
        given()
            .spec(requestSpec)
            .pathParam("userId", userId)
        .when()
            .delete("/users/{userId}")
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(204)));
    }
}
```

## Summary

- **Given-When-Then** syntax provides readable, structured request construction
- **HTTP methods** (GET, POST, PUT, PATCH, DELETE) each have specific use cases and patterns
- **Request Specifications** enable reusable, maintainable request configurations
- **Authentication** options include Basic, Bearer, OAuth2, and API Key
- **Parameters** can be path-based (identifying resources) or query-based (filtering)
- **Request bodies** support JSON strings, Maps, POJOs, and files
- **Logging** capabilities aid debugging and test maintenance

In the next lesson, you'll learn to handle and validate API responses, including JSON path extraction, schema validation, and serializing response data to Java objects.

## Additional Resources

- [REST Assured Request Specification](https://github.com/rest-assured/rest-assured/wiki/Usage#specification-re-use) - Official documentation
- [Hamcrest Matchers Tutorial](http://hamcrest.org/JavaHamcrest/tutorial) - Matcher reference
- [JSON Path Syntax](https://goessner.net/articles/JsonPath/) - JSONPath expressions

