# Serializing Response Data in REST Assured

## Learning Objectives
- Handle and validate API responses effectively in REST Assured
- Extract response data using JSONPath queries
- Deserialize JSON responses to Java POJOs
- Implement response specification for consistent validation
- Master JSON schema validation for contract testing

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, validating API responses is where testing truly happens. Sending requests is straightforward—the challenge lies in verifying that the response data is correct, complete, and follows the expected structure.

Understanding response handling transforms your tests from simple "did it return 200?" checks into comprehensive validations that catch data inconsistencies, schema violations, and business logic errors. These skills directly translate to finding real defects before they reach users.

## Response Validation Basics

### The Response Object

When REST Assured executes a request, it returns a `Response` object containing all response data:

```java
Response response = given()
    .baseUri("https://api.example.com")
.when()
    .get("/users/123")
.then()
    .extract()
    .response();

// Access response properties
int statusCode = response.getStatusCode();
String statusLine = response.getStatusLine();
String body = response.getBody().asString();
Headers headers = response.getHeaders();
long time = response.getTime();
String contentType = response.getContentType();
```

### Direct Validation in then()

```java
given()
    .baseUri("https://api.example.com")
.when()
    .get("/users/123")
.then()
    .statusCode(200)
    .statusLine(containsString("OK"))
    .contentType(ContentType.JSON)
    .time(lessThan(2000L))
    .body("name", equalTo("John"));
```

### Status Code Validation

```java
// Exact status code
.then().statusCode(200)

// Status code ranges
.then().statusCode(anyOf(equalTo(200), equalTo(201)))
.then().statusCode(greaterThanOrEqualTo(200))
.then().statusCode(lessThan(300))

// Status line
.then().statusLine("HTTP/1.1 200 OK")
.then().statusLine(containsString("OK"))
```

## Extracting Response Data

### Using extract()

```java
// Extract entire response
Response response = given()
    .when()
    .get("/users/123")
    .then()
    .extract()
    .response();

// Extract body as string
String bodyString = given()
    .when()
    .get("/users/123")
    .then()
    .extract()
    .body()
    .asString();

// Extract specific value
String userName = given()
    .when()
    .get("/users/123")
    .then()
    .extract()
    .path("name");

// Extract multiple values
Response response = given()
    .when()
    .get("/users/123")
    .then()
    .extract()
    .response();

String name = response.path("name");
String email = response.path("email");
int age = response.path("age");
```

### Extracting After Validation

```java
// Validate then extract
String userId = given()
    .contentType(ContentType.JSON)
    .body(newUserData)
.when()
    .post("/users")
.then()
    .statusCode(201)  // Validate first
    .body("name", notNullValue())  // More validation
    .extract()
    .path("id");  // Then extract

System.out.println("Created user ID: " + userId);
```

## JSONPath Queries

JSONPath provides powerful syntax for navigating and extracting data from JSON responses.

### Basic JSONPath Expressions

```java
// Given this JSON response:
// {
//     "id": 123,
//     "name": "John Doe",
//     "email": "john@example.com",
//     "address": {
//         "city": "New York",
//         "zip": "10001"
//     },
//     "phones": ["555-1234", "555-5678"],
//     "orders": [
//         {"id": 1, "total": 100.00},
//         {"id": 2, "total": 250.50}
//     ]
// }

// Root level properties
.body("id", equalTo(123))
.body("name", equalTo("John Doe"))

// Nested properties
.body("address.city", equalTo("New York"))
.body("address.zip", equalTo("10001"))

// Array access by index
.body("phones[0]", equalTo("555-1234"))
.body("phones[1]", equalTo("555-5678"))

// Array properties
.body("orders[0].id", equalTo(1))
.body("orders[1].total", equalTo(250.50f))
```

### Advanced JSONPath

```java
// Array size
.body("phones.size()", equalTo(2))
.body("orders.size()", greaterThan(0))

// Collect all values of a property
.body("orders.id", hasItems(1, 2))
.body("orders.total", everyItem(greaterThan(0f)))

// Find in array
.body("orders.find { it.id == 1 }.total", equalTo(100.00f))
.body("orders.findAll { it.total > 100 }.size()", equalTo(1))

// Sum, min, max
.body("orders.total.sum()", equalTo(350.50f))
.body("orders.total.min()", equalTo(100.00f))
.body("orders.total.max()", equalTo(250.50f))

// Collect specific properties
.body("orders.collect { it.id }", hasItems(1, 2))
```

### Extract with JSONPath

```java
// Extract single value
int userId = response.path("id");
String city = response.path("address.city");

// Extract list
List<String> phones = response.path("phones");
List<Integer> orderIds = response.path("orders.id");

// Extract with type
Integer count = response.path("orders.size()");
Float total = response.path("orders.total.sum()");

// Using JsonPath directly
JsonPath jsonPath = response.jsonPath();
String name = jsonPath.getString("name");
int orderCount = jsonPath.getInt("orders.size()");
List<Map<String, Object>> orders = jsonPath.getList("orders");
```

## Deserializing to POJOs

### Basic Deserialization

```java
// POJO class
public class User {
    private int id;
    private String name;
    private String email;
    private Address address;
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}

public class Address {
    private String city;
    private String zip;
    
    // Getters and setters
}

// Deserialize response to POJO
User user = given()
    .when()
    .get("/users/123")
    .then()
    .statusCode(200)
    .extract()
    .as(User.class);

// Now use strongly-typed object
assertEquals(123, user.getId());
assertEquals("John Doe", user.getName());
assertEquals("New York", user.getAddress().getCity());
```

### Deserializing Lists

```java
// For array responses
// [{"id": 1, "name": "John"}, {"id": 2, "name": "Jane"}]

List<User> users = given()
    .when()
    .get("/users")
    .then()
    .statusCode(200)
    .extract()
    .body()
    .jsonPath()
    .getList(".", User.class);

assertEquals(2, users.size());
assertEquals("John", users.get(0).getName());

// For nested arrays
// {"users": [{"id": 1, "name": "John"}, ...], "total": 2}

List<User> users = given()
    .when()
    .get("/users")
    .then()
    .extract()
    .body()
    .jsonPath()
    .getList("users", User.class);
```

### Deserializing with Jackson Annotations

```java
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private int id;
    
    @JsonProperty("full_name")  // Map JSON property to Java field
    private String name;
    
    @JsonProperty("email_address")
    private String email;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    // Getters and setters
}

// JSON: {"id": 1, "full_name": "John", "email_address": "john@example.com"}
// Automatically maps to User object
```

### Custom ObjectMapper Configuration

```java
@BeforeAll
static void configureObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    mapper.registerModule(new JavaTimeModule());
    
    RestAssured.config = RestAssuredConfig.config()
        .objectMapperConfig(
            ObjectMapperConfig.objectMapperConfig()
                .jackson2ObjectMapperFactory((cls, charset) -> mapper)
        );
}
```

## Response Specification

### Creating Reusable Response Specs

```java
public class ApiTestBase {
    
    protected static ResponseSpecification successResponseSpec;
    protected static ResponseSpecification errorResponseSpec;
    
    @BeforeAll
    static void setupResponseSpecs() {
        // Success response specification
        successResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType(ContentType.JSON)
            .expectResponseTime(lessThan(2000L))
            .build();
        
        // Error response specification
        errorResponseSpec = new ResponseSpecBuilder()
            .expectContentType(ContentType.JSON)
            .expectBody("error", notNullValue())
            .expectBody("error.code", notNullValue())
            .expectBody("error.message", notNullValue())
            .build();
    }
}
```

### Using Response Specs

```java
@Test
void testGetUserSuccess() {
    given()
        .spec(requestSpec)
        .pathParam("userId", 123)
    .when()
        .get("/users/{userId}")
    .then()
        .spec(successResponseSpec)  // Apply success validation
        .body("id", equalTo(123));
}

@Test
void testGetUserNotFound() {
    given()
        .spec(requestSpec)
        .pathParam("userId", 999999)
    .when()
        .get("/users/{userId}")
    .then()
        .spec(errorResponseSpec)  // Apply error validation
        .statusCode(404)
        .body("error.code", equalTo("NOT_FOUND"));
}
```

### Dynamic Response Specs

```java
public ResponseSpecification createListResponseSpec(int expectedSize) {
    return new ResponseSpecBuilder()
        .expectStatusCode(200)
        .expectContentType(ContentType.JSON)
        .expectBody("data.size()", equalTo(expectedSize))
        .build();
}

@Test
void testGetUsersWithPagination() {
    int pageSize = 10;
    
    given()
        .spec(requestSpec)
        .queryParam("pageSize", pageSize)
    .when()
        .get("/users")
    .then()
        .spec(createListResponseSpec(pageSize));
}
```

## JSON Schema Validation

### What is JSON Schema?

JSON Schema defines the structure and validation rules for JSON data. It's ideal for contract testing—ensuring APIs return data in the expected format.

### Adding Schema Validation Dependency

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>5.4.0</version>
    <scope>test</scope>
</dependency>
```

### Creating a JSON Schema

```json
// src/test/resources/schemas/user-schema.json
{
    "$schema": "http://json-schema.org/draft-07/schema#",
    "type": "object",
    "required": ["id", "name", "email"],
    "properties": {
        "id": {
            "type": "integer",
            "minimum": 1
        },
        "name": {
            "type": "string",
            "minLength": 1,
            "maxLength": 100
        },
        "email": {
            "type": "string",
            "format": "email"
        },
        "age": {
            "type": "integer",
            "minimum": 0,
            "maximum": 150
        },
        "address": {
            "type": "object",
            "properties": {
                "city": { "type": "string" },
                "zip": { "type": "string", "pattern": "^[0-9]{5}$" }
            }
        },
        "roles": {
            "type": "array",
            "items": {
                "type": "string",
                "enum": ["admin", "user", "guest"]
            }
        }
    },
    "additionalProperties": false
}
```

### Validating Against Schema

```java
import static io.restassured.module.jsv.JsonSchemaValidator.*;

@Test
void testUserResponseMatchesSchema() {
    given()
        .spec(requestSpec)
        .pathParam("userId", 123)
    .when()
        .get("/users/{userId}")
    .then()
        .statusCode(200)
        .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
}

// From file
@Test
void testSchemaFromFile() {
    File schemaFile = new File("src/test/resources/schemas/user-schema.json");
    
    given()
        .when()
        .get("/users/123")
    .then()
        .body(matchesJsonSchema(schemaFile));
}

// Inline schema
@Test
void testInlineSchema() {
    String schema = """
        {
            "type": "object",
            "required": ["id", "name"],
            "properties": {
                "id": {"type": "integer"},
                "name": {"type": "string"}
            }
        }
        """;
    
    given()
        .when()
        .get("/users/123")
    .then()
        .body(matchesJsonSchema(schema));
}
```

### Schema for List Responses

```json
// schemas/users-list-schema.json
{
    "$schema": "http://json-schema.org/draft-07/schema#",
    "type": "object",
    "required": ["users", "pagination"],
    "properties": {
        "users": {
            "type": "array",
            "items": {
                "$ref": "#/definitions/user"
            }
        },
        "pagination": {
            "type": "object",
            "required": ["page", "pageSize", "total"],
            "properties": {
                "page": { "type": "integer", "minimum": 1 },
                "pageSize": { "type": "integer", "minimum": 1, "maximum": 100 },
                "total": { "type": "integer", "minimum": 0 }
            }
        }
    },
    "definitions": {
        "user": {
            "type": "object",
            "required": ["id", "name"],
            "properties": {
                "id": { "type": "integer" },
                "name": { "type": "string" }
            }
        }
    }
}
```

## Header Validation

```java
// Check header existence
.then()
    .header("Content-Type", notNullValue())
    .header("X-Request-ID", notNullValue())

// Check header values
.then()
    .header("Content-Type", "application/json; charset=utf-8")
    .header("Content-Type", containsString("application/json"))
    .header("Cache-Control", equalTo("no-cache"))

// Multiple headers
.then()
    .headers(
        "Content-Type", containsString("json"),
        "X-RateLimit-Limit", notNullValue(),
        "X-RateLimit-Remaining", notNullValue()
    )

// Extract headers
Headers headers = response.getHeaders();
String contentType = response.getHeader("Content-Type");
String rateLimit = response.getHeader("X-RateLimit-Remaining");
```

## Complete Response Handling Example

```java
public class UserApiResponseTest {
    
    private static RequestSpecification requestSpec;
    private static ResponseSpecification userResponseSpec;
    
    @BeforeAll
    static void setup() {
        requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://api.example.com")
            .setBasePath("/api/v1")
            .setContentType(ContentType.JSON)
            .addHeader("Authorization", "Bearer test-token")
            .build();
        
        userResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType(ContentType.JSON)
            .expectBody("id", notNullValue())
            .expectBody("name", notNullValue())
            .expectBody("email", notNullValue())
            .build();
    }
    
    @Test
    @DisplayName("Get user - validate full response")
    void testGetUserFullValidation() {
        Response response = given()
            .spec(requestSpec)
            .pathParam("userId", 123)
        .when()
            .get("/users/{userId}")
        .then()
            .spec(userResponseSpec)
            .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"))
            .header("X-Request-ID", notNullValue())
            .time(lessThan(2000L))
            .extract()
            .response();
        
        // Further processing with extracted response
        User user = response.as(User.class);
        assertNotNull(user.getCreatedAt(), "createdAt should not be null");
        assertTrue(user.getId() > 0, "ID should be positive");
    }
    
    @Test
    @DisplayName("Get users list - validate pagination")
    void testGetUsersListValidation() {
        int requestedPageSize = 10;
        
        Response response = given()
            .spec(requestSpec)
            .queryParam("page", 1)
            .queryParam("pageSize", requestedPageSize)
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .body("users", not(empty()))
            .body("users.size()", lessThanOrEqualTo(requestedPageSize))
            .body("pagination.page", equalTo(1))
            .body("pagination.pageSize", equalTo(requestedPageSize))
            .body("users.id", everyItem(greaterThan(0)))
            .body("users.name", everyItem(not(emptyString())))
            .extract()
            .response();
        
        // Extract and validate using POJOs
        List<User> users = response.jsonPath().getList("users", User.class);
        
        users.forEach(user -> {
            assertNotNull(user.getName(), "Each user should have a name");
            assertNotNull(user.getEmail(), "Each user should have an email");
        });
    }
    
    @Test
    @DisplayName("Create user - validate response and extract ID")
    void testCreateUserAndExtractData() {
        Map<String, Object> newUser = Map.of(
            "name", "Test User",
            "email", "test@example.com"
        );
        
        // Create user and extract response
        Response createResponse = given()
            .spec(requestSpec)
            .body(newUser)
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo(newUser.get("name")))
            .body("email", equalTo(newUser.get("email")))
            .extract()
            .response();
        
        // Extract created user ID
        int createdUserId = createResponse.path("id");
        
        // Verify user exists
        given()
            .spec(requestSpec)
            .pathParam("userId", createdUserId)
        .when()
            .get("/users/{userId}")
        .then()
            .statusCode(200)
            .body("id", equalTo(createdUserId))
            .body("name", equalTo(newUser.get("name")));
        
        // Cleanup
        given()
            .spec(requestSpec)
            .pathParam("userId", createdUserId)
        .when()
            .delete("/users/{userId}")
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(204)));
    }
}
```

## Summary

- **Response extraction** with `extract()` allows accessing status, body, and headers
- **JSONPath** provides powerful syntax for navigating nested JSON structures
- **POJO deserialization** with `as()` creates strongly-typed objects from responses
- **Response specifications** enable reusable validation patterns
- **JSON Schema validation** ensures API responses conform to contracts
- **Header validation** verifies metadata like rate limits and content types

With response handling mastered, you can now build comprehensive API tests that validate every aspect of your API's behavior. The next lesson covers integrating REST Assured with JUnit 5 and other test frameworks for complete test automation.

## Additional Resources

- [JSONPath Syntax Guide](https://goessner.net/articles/JsonPath/) - Complete JSONPath reference
- [JSON Schema](https://json-schema.org/) - Official JSON Schema documentation
- [Jackson Annotations](https://github.com/FasterXML/jackson-annotations) - Jackson serialization reference

