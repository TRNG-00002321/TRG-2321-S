# Lab: REST Assured Advanced Assertions

## Overview

**Duration:** 60-75 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll master REST Assured's assertion capabilities using Hamcrest matchers and JSON path extraction. You'll write comprehensive validations for complex API responses.

---

## Learning Objectives

By completing this lab, you will:
- Use Hamcrest matchers for expressive assertions
- Navigate JSON responses using JSON Path
- Validate nested objects and arrays
- Implement JSON Schema validation
- Create reusable response specifications

---

## Prerequisites

- Completed "REST Assured Basics" exercise
- REST Assured project set up
- Understanding of JSON structure

---

## The Scenario

The BookHaven API returns complex nested responses. You need to validate not just status codes, but the entire response structure, data types, and business rules. Your assertions must be comprehensive enough to catch regressions.

---

## Core Tasks

### Task 1: Hamcrest Matchers Deep Dive (20 minutes)

Create `ResponseValidationTest.java`:

```java
package com.bookhaven.api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

class ResponseValidationTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    @DisplayName("Equality matchers")
    void testEqualityMatchers() {
        given()
        .when()
            .get("/posts/1")
        .then()
            .body("id", equalTo(1))           // Exact equality
            .body("userId", is(1))            // Alias for equalTo
            .body("title", not(equalTo("")))  // Negation
            .body("id", instanceOf(Integer.class)); // Type check
    }

    @Test
    @DisplayName("String matchers")
    void testStringMatchers() {
        given()
        .when()
            .get("/posts/1")
        .then()
            .body("title", not(emptyString()))
            .body("title", not(nullValue()))
            .body("body", containsString("quia"))
            .body("title", startsWith("sunt"))
            .body("body", endsWith("tempora"));
    }

    @Test
    @DisplayName("Number matchers")
    void testNumberMatchers() {
        given()
        .when()
            .get("/posts/1")
        .then()
            .body("id", greaterThan(0))
            .body("id", lessThanOrEqualTo(100))
            .body("userId", greaterThanOrEqualTo(1))
            .body("userId", lessThan(11))
            .body("id", both(greaterThan(0)).and(lessThan(101)));
    }

    @Test
    @DisplayName("Collection matchers on array")
    void testCollectionMatchers() {
        given()
        .when()
            .get("/posts")
        .then()
            .body("$", hasSize(100))
            .body("id", hasItem(1))
            .body("id", hasItems(1, 2, 3))
            .body("id", everyItem(greaterThan(0)))
            .body("userId", everyItem(both(greaterThan(0)).and(lessThan(11))));
    }

    @Test
    @DisplayName("Combining matchers")
    void testCombinedMatchers() {
        given()
        .when()
            .get("/posts/1")
        .then()
            .body("title", allOf(
                not(emptyString()),
                containsString(" "),
                startsWith("sunt")
            ))
            .body("id", anyOf(
                equalTo(1),
                equalTo(2)
            ));
    }
}
```

**Your Tasks:**
1. Run all tests and verify they pass
2. Add tests using these additional matchers:
   - `containsStringIgnoringCase()`
   - `hasToString()`
   - `closeTo()` for floating point
3. Create a test that validates all fields in a single assertion block

### Task 2: JSON Path Expressions (20 minutes)

Add to your test class:

```java
@Test
@DisplayName("JSON Path - basic navigation")
void testBasicJsonPath() {
    given()
    .when()
        .get("/users/1")
    .then()
        .body("name", equalTo("Leanne Graham"))
        .body("username", equalTo("Bret"))
        .body("email", containsString("@"))
        // Nested object access
        .body("address.street", equalTo("Kulas Light"))
        .body("address.city", equalTo("Gwenborough"))
        .body("address.geo.lat", notNullValue())
        .body("address.geo.lng", notNullValue())
        // Deeply nested
        .body("company.name", equalTo("Romaguera-Crona"));
}

@Test
@DisplayName("JSON Path - array operations")
void testArrayJsonPath() {
    given()
    .when()
        .get("/posts")
    .then()
        // First element
        .body("[0].id", equalTo(1))
        // Last element (using size-1)
        .body("[-1].id", equalTo(100))
        // Specific index
        .body("[5].userId", equalTo(1))
        // All IDs from first 3 elements
        .body("[0..2].id", hasItems(1, 2, 3));
}

@Test
@DisplayName("JSON Path - filtering with findAll")
void testJsonPathFiltering() {
    given()
    .when()
        .get("/posts")
    .then()
        // Find all posts by user 1
        .body("findAll { it.userId == 1 }.size()", equalTo(10))
        // Find specific post by title
        .body("find { it.id == 5 }.userId", equalTo(1))
        // Collect all userIds
        .body("collect { it.userId }.unique().size()", lessThanOrEqualTo(10));
}

@Test
@DisplayName("JSON Path - size and existence")
void testJsonPathSize() {
    given()
    .when()
        .get("/users/1")
    .then()
        // Object has keys
        .body("$", hasKey("id"))
        .body("$", hasKey("address"))
        .body("address", hasKey("geo"))
        // Key count
        .body("keySet().size()", greaterThan(5));
}
```

**Your Tasks:**
1. Create tests validating all users have required address fields
2. Test that all post bodies have minimum length of 50 characters
3. Validate that comments contain valid email addresses

### Task 3: JSON Schema Validation (15 minutes)

Create `src/test/resources/schemas/post-schema.json`:

```json
{
    "$schema": "http://json-schema.org/draft-07/schema#",
    "title": "Post",
    "type": "object",
    "required": ["id", "userId", "title", "body"],
    "properties": {
        "id": {
            "type": "integer",
            "minimum": 1
        },
        "userId": {
            "type": "integer",
            "minimum": 1,
            "maximum": 10
        },
        "title": {
            "type": "string",
            "minLength": 1
        },
        "body": {
            "type": "string",
            "minLength": 1
        }
    },
    "additionalProperties": false
}
```

Add schema validation test:

```java
import static io.restassured.module.jsv.JsonSchemaValidator.*;

@Test
@DisplayName("Validate response against JSON Schema")
void testJsonSchemaValidation() {
    given()
    .when()
        .get("/posts/1")
    .then()
        .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));
}
```

**Your Tasks:**
1. Create a schema for the User object (include nested address)
2. Create a schema for the array of posts
3. Test validation against incorrect data (negative test)

### Task 4: Response Specifications (15 minutes)

Create reusable specifications:

```java
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

class ResponseSpecificationTest {

    private static ResponseSpecification successResponseSpec;
    private static ResponseSpecification postResponseSpec;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

        // Reusable success response spec
        successResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType("application/json; charset=utf-8")
            .expectResponseTime(lessThan(5000L))
            .build();

        // Reusable post validation spec
        postResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectBody("id", notNullValue())
            .expectBody("userId", greaterThan(0))
            .expectBody("title", not(emptyString()))
            .expectBody("body", not(emptyString()))
            .build();
    }

    @Test
    @DisplayName("Use success response spec")
    void testWithSuccessSpec() {
        given()
        .when()
            .get("/posts/1")
        .then()
            .spec(successResponseSpec)
            .body("id", equalTo(1));
    }

    @Test
    @DisplayName("Use post response spec")
    void testWithPostSpec() {
        given()
        .when()
            .get("/posts/1")
        .then()
            .spec(postResponseSpec);
    }

    @Test
    @DisplayName("Combine specs with additional assertions")
    void testCombinedSpecs() {
        given()
        .when()
            .get("/posts/1")
        .then()
            .spec(successResponseSpec)
            .spec(postResponseSpec)
            .body("userId", equalTo(1));
    }
}
```

**Your Tasks:**
1. Create a spec for error responses (404, 500)
2. Create a spec for list endpoints (array validation)
3. Create specs for different response times (fast vs normal)

### Task 5: Custom Assertions (10 minutes)

```java
@Test
@DisplayName("Custom assertion logic")
void testCustomAssertions() {
    Response response = given()
        .when()
        .get("/posts")
        .then()
        .extract()
        .response();

    // Custom validation with Java
    List<Map<String, Object>> posts = response.jsonPath().getList("$");

    // All IDs are unique
    List<Integer> ids = posts.stream()
        .map(p -> (Integer) p.get("id"))
        .collect(Collectors.toList());
    Set<Integer> uniqueIds = new HashSet<>(ids);
    Assertions.assertEquals(ids.size(), uniqueIds.size(), "All IDs should be unique");

    // All titles have content
    boolean allHaveTitles = posts.stream()
        .allMatch(p -> p.get("title") != null && !((String) p.get("title")).isEmpty());
    Assertions.assertTrue(allHaveTitles, "All posts should have titles");

    // UserIds are in valid range
    boolean validUserIds = posts.stream()
        .allMatch(p -> {
            int userId = (Integer) p.get("userId");
            return userId >= 1 && userId <= 10;
        });
    Assertions.assertTrue(validUserIds, "All userIds should be 1-10");
}
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Hamcrest matcher tests (equality, string, number, collection)
- [ ] JSON Path navigation tests (nested, arrays, filtering)
- [ ] JSON Schema validation working
- [ ] Response specifications created and used
- [ ] Custom assertion logic implemented
- [ ] All tests passing with `mvn test`

---

## Challenge Tasks (Optional)

### 1. Dynamic Response Validation
```java
@ParameterizedTest
@ValueSource(ints = {1, 2, 3, 4, 5})
void testMultiplePostsParameterized(int postId) {
    given()
        .pathParam("id", postId)
    .when()
        .get("/posts/{id}")
    .then()
        .statusCode(200)
        .body("id", equalTo(postId));
}
```

### 2. Soft Assertions
```java
@Test
void testSoftAssertions() {
    Response response = get("/posts/1").then().extract().response();
    
    SoftAssertions softly = new SoftAssertions();
    softly.assertThat(response.statusCode()).isEqualTo(200);
    softly.assertThat(response.jsonPath().getInt("id")).isEqualTo(1);
    softly.assertThat(response.jsonPath().getString("title")).isNotEmpty();
    softly.assertAll();
}
```

---

## Submission Checklist

| Assertion Type | Implemented | Passing |
|----------------|-------------|---------|
| Equality matchers | ☐ | ☐ |
| String matchers | ☐ | ☐ |
| Number matchers | ☐ | ☐ |
| Collection matchers | ☐ | ☐ |
| Combined matchers | ☐ | ☐ |
| JSON Path navigation | ☐ | ☐ |
| JSON Path filtering | ☐ | ☐ |
| JSON Schema validation | ☐ | ☐ |
| Response specifications | ☐ | ☐ |
| Custom assertions | ☐ | ☐ |

---

## Additional Resources

- Written Content: `serializing-response-data.md`, `rest-assured-framework-integration.md`
- [JSON Path Syntax](https://github.com/json-path/JsonPath)
- [JSON Schema](https://json-schema.org/)

