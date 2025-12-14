# REST Assured Overview

## Learning Objectives
- Understand what REST Assured is and its purpose in Java API testing
- Recognize the advantages of using REST Assured over alternatives
- Set up REST Assured in a Maven or Gradle project
- Comprehend the basic syntax and fluent API design
- Compare REST Assured with other Java HTTP clients

## Why This Matters

Yesterday you mastered Postman for manual API exploration and scripting. Today, in our **"From API to UI: Mastering Full-Stack Test Automation"** journey, we take the next step: programmatic API testing with Java. While Postman excels at ad-hoc testing and exploration, production-grade test automation requires code that integrates with CI/CD pipelines, version control, and test frameworks.

REST Assured is the industry-standard library for API testing in Java. It transforms the complexity of HTTP communication into readable, expressive code that mirrors the Given-When-Then format you learned in Week 6's unit testing. When combined with JUnit 5 and your existing Java skills from Week 3, REST Assured enables you to build comprehensive, maintainable API test suites.

## What is REST Assured?

**REST Assured** is an open-source Java library that simplifies testing and validation of REST APIs. Created by Johan Haleby in 2010, it has become the de facto standard for API testing in Java ecosystems.

### Core Philosophy

REST Assured is built on several key principles:

```
Traditional HTTP Client Approach:
┌─────────────────────────────────────────────────────────────┐
│ 1. Create HTTP client                                        │
│ 2. Build request object                                      │
│ 3. Set headers manually                                      │
│ 4. Set body manually                                         │
│ 5. Execute request                                           │
│ 6. Extract response                                          │
│ 7. Parse response body                                       │
│ 8. Write assertions                                          │
│ = Many lines of boilerplate code                            │
└─────────────────────────────────────────────────────────────┘

REST Assured Approach:
┌─────────────────────────────────────────────────────────────┐
│ given()                                                      │
│     .header("Authorization", "Bearer token")                 │
│     .body(requestBody)                                       │
│ .when()                                                      │
│     .post("/users")                                          │
│ .then()                                                      │
│     .statusCode(201)                                         │
│     .body("name", equalTo("John"));                          │
│ = Readable, concise, expressive                             │
└─────────────────────────────────────────────────────────────┘
```

### Key Features

| Feature | Description |
|---------|-------------|
| **Fluent API** | Method chaining for readable code |
| **BDD Syntax** | Given-When-Then structure |
| **JSON/XML Support** | Built-in parsing and validation |
| **JSONPath/XPath** | Powerful data extraction |
| **Request Specification** | Reusable request templates |
| **Response Validation** | Rich assertion capabilities |
| **Authentication** | Built-in auth mechanisms |
| **Logging** | Request/response logging |

## Why REST Assured for Java API Testing

### Advantages Over Manual HTTP Handling

**Without REST Assured (using HttpClient):**
```java
// Verbose and complex
HttpClient client = HttpClient.newHttpClient();
String requestBody = "{\"name\":\"John\",\"email\":\"john@example.com\"}";

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users"))
    .header("Content-Type", "application/json")
    .header("Authorization", "Bearer " + token)
    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
    .build();

HttpResponse<String> response = client.send(request, 
    HttpResponse.BodyHandlers.ofString());

// Manual JSON parsing required
ObjectMapper mapper = new ObjectMapper();
User user = mapper.readValue(response.body(), User.class);

// Manual assertions
assertEquals(201, response.statusCode());
assertEquals("John", user.getName());
```

**With REST Assured:**
```java
// Concise and readable
given()
    .contentType(ContentType.JSON)
    .header("Authorization", "Bearer " + token)
    .body(new User("John", "john@example.com"))
.when()
    .post("/users")
.then()
    .statusCode(201)
    .body("name", equalTo("John"));
```

### Industry Adoption

REST Assured is widely adopted because:

1. **Readability**: Code reads like documentation
2. **Productivity**: Less boilerplate, faster development
3. **Maintainability**: Changes are localized and clear
4. **Integration**: Works seamlessly with JUnit, TestNG, Maven, Gradle
5. **Community**: Active development, extensive documentation
6. **Standards**: Follows testing best practices

## Adding REST Assured Dependencies

### Maven Setup

Add the following to your `pom.xml`:

```xml
<dependencies>
    <!-- REST Assured Core -->
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <version>5.4.0</version>
        <scope>test</scope>
    </dependency>

    <!-- JSON Schema Validation (optional but recommended) -->
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>json-schema-validator</artifactId>
        <version>5.4.0</version>
        <scope>test</scope>
    </dependency>

    <!-- JSON Path (included with rest-assured, but useful standalone) -->
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>json-path</artifactId>
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

    <!-- Hamcrest Matchers (for assertions) -->
    <dependency>
        <groupId>org.hamcrest</groupId>
        <artifactId>hamcrest</artifactId>
        <version>2.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Gradle Setup

Add to your `build.gradle`:

```groovy
dependencies {
    // REST Assured
    testImplementation 'io.rest-assured:rest-assured:5.4.0'
    testImplementation 'io.rest-assured:json-schema-validator:5.4.0'
    testImplementation 'io.rest-assured:json-path:5.4.0'
    
    // JUnit 5
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
    
    // Hamcrest
    testImplementation 'org.hamcrest:hamcrest:2.2'
}

test {
    useJUnitPlatform()
}
```

### Required Imports

```java
// REST Assured static imports (recommended)
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

// Standard imports
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.http.ContentType;
```

## Basic Syntax and Fluent API Design

### The Given-When-Then Pattern

REST Assured follows Behavior-Driven Development (BDD) syntax:

```java
given()    // Pre-conditions: headers, auth, body, params
    .spec(requestSpec)
    .header("Authorization", "Bearer token")
    .queryParam("status", "active")
    .body(requestBody)
.when()    // Action: HTTP method and endpoint
    .get("/users")
.then()    // Verification: status, headers, body
    .statusCode(200)
    .contentType(ContentType.JSON)
    .body("users.size()", greaterThan(0));
```

### Fluent API Explained

**Method Chaining:**
```java
// Each method returns an object allowing further chaining
given()                           // Returns RequestSpecification
    .header("Accept", "application/json")  // Returns RequestSpecification
    .queryParam("page", 1)                 // Returns RequestSpecification
.when()                           // Returns RequestSender
    .get("/users")                // Returns Response
.then()                           // Returns ValidatableResponse
    .statusCode(200);             // Returns ValidatableResponse
```

**Breakdown of Components:**

| Method | Returns | Purpose |
|--------|---------|---------|
| `given()` | `RequestSpecification` | Configure request |
| `when()` | `RequestSender` | Prepare to send |
| HTTP methods | `Response` | Execute request |
| `then()` | `ValidatableResponse` | Begin validation |

### Simple Request Examples

**GET Request:**
```java
@Test
void testGetUsers() {
    given()
        .baseUri("https://api.example.com")
    .when()
        .get("/users")
    .then()
        .statusCode(200);
}
```

**POST Request:**
```java
@Test
void testCreateUser() {
    String requestBody = """
        {
            "name": "John Doe",
            "email": "john@example.com"
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
```

**PUT Request:**
```java
@Test
void testUpdateUser() {
    given()
        .baseUri("https://api.example.com")
        .contentType(ContentType.JSON)
        .body("{\"name\": \"Jane Doe\"}")
    .when()
        .put("/users/123")
    .then()
        .statusCode(200)
        .body("name", equalTo("Jane Doe"));
}
```

**DELETE Request:**
```java
@Test
void testDeleteUser() {
    given()
        .baseUri("https://api.example.com")
    .when()
        .delete("/users/123")
    .then()
        .statusCode(204);
}
```

### Extracting Response Data

```java
// Extract entire response
Response response = given()
    .when()
    .get("/users/123")
    .then()
    .extract()
    .response();

// Extract specific value
String userName = given()
    .when()
    .get("/users/123")
    .then()
    .extract()
    .path("name");

// Extract as POJO
User user = given()
    .when()
    .get("/users/123")
    .then()
    .extract()
    .as(User.class);
```

## REST Assured vs HttpClient

### Feature Comparison

| Feature | REST Assured | Java HttpClient |
|---------|--------------|-----------------|
| **Verbosity** | Low | High |
| **Learning Curve** | Easy | Moderate |
| **Built-in Assertions** | Yes | No |
| **JSON/XML Parsing** | Automatic | Manual |
| **BDD Support** | Native | None |
| **Logging** | Built-in | Manual |
| **Request Specs** | Yes | Manual |
| **Test Focus** | API Testing | General HTTP |

### Code Comparison

**Task: Get user, verify response, extract ID**

**Java HttpClient:**
```java
@Test
void testWithHttpClient() throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.example.com/users/123"))
        .header("Accept", "application/json")
        .GET()
        .build();
    
    HttpResponse<String> response = client.send(request, 
        HttpResponse.BodyHandlers.ofString());
    
    assertEquals(200, response.statusCode());
    
    ObjectMapper mapper = new ObjectMapper();
    JsonNode json = mapper.readTree(response.body());
    
    assertEquals("John", json.get("name").asText());
    assertNotNull(json.get("id"));
}
```

**REST Assured:**
```java
@Test
void testWithRestAssured() {
    given()
        .baseUri("https://api.example.com")
        .accept(ContentType.JSON)
    .when()
        .get("/users/123")
    .then()
        .statusCode(200)
        .body("name", equalTo("John"))
        .body("id", notNullValue());
}
```

### When to Choose Each

**Choose REST Assured when:**
- Primary focus is API testing
- Need readable, maintainable tests
- Working with test frameworks (JUnit, TestNG)
- Want built-in validation capabilities
- Team includes QA engineers

**Choose HttpClient when:**
- Building production HTTP clients
- Need maximum control over requests
- Non-testing use cases
- Minimal external dependencies required
- Part of larger application code

## Configuration and Setup

### Global Configuration

```java
@BeforeAll
static void setup() {
    // Set base URI for all requests
    RestAssured.baseURI = "https://api.example.com";
    
    // Set base path (appended to URI)
    RestAssured.basePath = "/api/v1";
    
    // Set default port
    RestAssured.port = 443;
    
    // Enable logging for all requests
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    
    // Set default content type
    RestAssured.requestSpecification = new RequestSpecBuilder()
        .setContentType(ContentType.JSON)
        .setAccept(ContentType.JSON)
        .build();
}

@AfterAll
static void teardown() {
    // Reset to defaults
    RestAssured.reset();
}
```

### Request Specification (Reusable Configuration)

```java
// Create reusable request specification
RequestSpecification requestSpec = new RequestSpecBuilder()
    .setBaseUri("https://api.example.com")
    .setBasePath("/api/v1")
    .setContentType(ContentType.JSON)
    .addHeader("Authorization", "Bearer " + getToken())
    .addFilter(new RequestLoggingFilter())
    .addFilter(new ResponseLoggingFilter())
    .build();

// Use in tests
@Test
void testWithSpec() {
    given()
        .spec(requestSpec)
    .when()
        .get("/users")
    .then()
        .statusCode(200);
}
```

### Response Specification (Reusable Validation)

```java
// Create reusable response specification
ResponseSpecification successResponseSpec = new ResponseSpecBuilder()
    .expectStatusCode(200)
    .expectContentType(ContentType.JSON)
    .expectResponseTime(lessThan(2000L))
    .build();

// Use in tests
@Test
void testWithResponseSpec() {
    given()
        .spec(requestSpec)
    .when()
        .get("/users")
    .then()
        .spec(successResponseSpec)
        .body("users", not(empty()));
}
```

## Project Structure

```
src/
├── main/java/
│   └── com/example/
│       └── model/
│           └── User.java          # POJO for serialization
└── test/
    ├── java/
    │   └── com/example/
    │       └── api/
    │           ├── BaseApiTest.java       # Base class with setup
    │           ├── UserApiTest.java       # User endpoint tests
    │           └── ProductApiTest.java    # Product endpoint tests
    └── resources/
        ├── schemas/
        │   └── user-schema.json   # JSON schemas
        └── testdata/
            └── users.json         # Test data files
```

## Summary

- **REST Assured** is the industry-standard Java library for API testing
- It provides a **fluent API** with **Given-When-Then** BDD syntax
- **Maven/Gradle dependencies** are straightforward to configure
- REST Assured offers significant advantages over raw HttpClient for testing: **readability**, **built-in assertions**, **automatic parsing**
- **Request and Response Specifications** enable reusable, maintainable test configurations
- Integration with **JUnit 5** and **Hamcrest matchers** creates powerful test suites

In the next lesson, you'll dive deeper into REST Assured's request capabilities, learning to construct complex requests with authentication, headers, parameters, and request bodies using the Given-When-Then syntax.

## Additional Resources

- [REST Assured Official Documentation](https://rest-assured.io/) - Complete reference
- [REST Assured GitHub Repository](https://github.com/rest-assured/rest-assured) - Source and examples
- [REST Assured Usage Guide](https://github.com/rest-assured/rest-assured/wiki/Usage) - Detailed usage wiki

