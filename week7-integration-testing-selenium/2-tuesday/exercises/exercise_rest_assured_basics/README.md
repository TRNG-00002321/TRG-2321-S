# Lab: REST Assured Basics

## Overview

**Duration:** 60-75 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll write your first programmatic API tests using REST Assured in Java. You'll set up a Maven project, write tests using the Given-When-Then syntax, and validate API responses.

---

## Learning Objectives

By completing this lab, you will:
- Set up a REST Assured Maven project
- Write API tests using Given-When-Then syntax
- Perform CRUD operations programmatically
- Extract and validate response data
- Integrate REST Assured with JUnit 5

---

## Prerequisites

- Java JDK 11+ installed
- Maven installed and configured
- IDE (IntelliJ IDEA or Eclipse)
- Understanding of REST Assured concepts (from `rest-assured-overview.md`)

---

## The Scenario

After mastering Postman, the BookHaven team wants programmatic API tests that integrate with their CI/CD pipeline. Your task is to convert your Postman collection into Java-based REST Assured tests.

---

## Core Tasks

### Task 1: Project Setup (15 minutes)

**Create a new Maven project:**

1. Create project structure:
```
rest-assured-lab/
├── pom.xml
└── src/
    └── test/
        └── java/
            └── com/
                └── bookhaven/
                    └── api/
                        └── BookApiTest.java
```

2. Create `pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.bookhaven</groupId>
    <artifactId>rest-assured-lab</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <rest-assured.version>5.4.0</rest-assured.version>
        <junit.version>5.10.0</junit.version>
    </properties>

    <dependencies>
        <!-- REST Assured -->
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured</artifactId>
            <version>${rest-assured.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- JSON Schema Validation -->
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>json-schema-validator</artifactId>
            <version>${rest-assured.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- JUnit 5 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- Hamcrest Matchers -->
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
                <version>3.1.2</version>
            </plugin>
        </plugins>
    </build>
</project>
```

3. Run `mvn clean install` to download dependencies

### Task 2: First GET Request (15 minutes)

Create `BookApiTest.java`:

```java
package com.bookhaven.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookApiTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    @Order(1)
    @DisplayName("GET /posts should return 200 and list of posts")
    void testGetAllPosts() {
        given()
            .log().uri()
        .when()
            .get("/posts")
        .then()
            .log().status()
            .statusCode(200)
            .contentType("application/json; charset=utf-8")
            .body("size()", equalTo(100))
            .body("[0].id", equalTo(1))
            .body("[0].userId", equalTo(1));
    }

    @Test
    @Order(2)
    @DisplayName("GET /posts/1 should return specific post")
    void testGetPostById() {
        given()
            .pathParam("id", 1)
        .when()
            .get("/posts/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("title", not(emptyString()))
            .body("body", not(emptyString()))
            .body("userId", equalTo(1));
    }
}
```

**Your Tasks:**
1. Run the tests: `mvn test`
2. Verify both tests pass
3. Add a test for GET `/posts?userId=1`

### Task 3: POST Request - Create Resource (15 minutes)

Add this test to your class:

```java
@Test
@Order(3)
@DisplayName("POST /posts should create new post")
void testCreatePost() {
    String requestBody = """
        {
            "title": "REST Assured Test Post",
            "body": "This post was created using REST Assured",
            "userId": 1
        }
        """;

    given()
        .contentType("application/json")
        .body(requestBody)
        .log().all()
    .when()
        .post("/posts")
    .then()
        .log().all()
        .statusCode(201)
        .body("id", notNullValue())
        .body("title", equalTo("REST Assured Test Post"))
        .body("userId", equalTo(1));
}
```

**Your Tasks:**
1. Run the test and verify it passes
2. Create a similar test using a Java Map:

```java
@Test
@Order(4)
@DisplayName("POST /posts with Map body")
void testCreatePostWithMap() {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("title", "Map-based Post");
    requestBody.put("body", "Created using HashMap");
    requestBody.put("userId", 2);

    given()
        .contentType(ContentType.JSON)
        .body(requestBody)
    .when()
        .post("/posts")
    .then()
        .statusCode(201)
        .body("title", equalTo("Map-based Post"));
}
```

### Task 4: PUT and DELETE Requests (15 minutes)

Add these tests:

```java
@Test
@Order(5)
@DisplayName("PUT /posts/1 should update post")
void testUpdatePost() {
    String requestBody = """
        {
            "id": 1,
            "title": "Updated Title",
            "body": "Updated body content",
            "userId": 1
        }
        """;

    given()
        .contentType(ContentType.JSON)
        .body(requestBody)
    .when()
        .put("/posts/1")
    .then()
        .statusCode(200)
        .body("title", equalTo("Updated Title"));
}

@Test
@Order(6)
@DisplayName("DELETE /posts/1 should remove post")
void testDeletePost() {
    given()
    .when()
        .delete("/posts/1")
    .then()
        .statusCode(200);
}
```

**Your Tasks:**
1. Add a PATCH request test (partial update)
2. Test updating with invalid data (negative test)

### Task 5: Extract Response Data (15 minutes)

Learn to extract data for subsequent tests:

```java
@Test
@Order(7)
@DisplayName("Extract and use response data")
void testExtractResponseData() {
    // Extract entire response
    Response response = given()
        .when()
        .get("/posts/1")
        .then()
        .extract()
        .response();

    // Access response properties
    int statusCode = response.getStatusCode();
    String contentType = response.getContentType();
    long responseTime = response.getTime();

    System.out.println("Status: " + statusCode);
    System.out.println("Content-Type: " + contentType);
    System.out.println("Response Time: " + responseTime + "ms");

    // Extract specific values
    int postId = response.jsonPath().getInt("id");
    String title = response.jsonPath().getString("title");
    int userId = response.jsonPath().getInt("userId");

    System.out.println("Post ID: " + postId);
    System.out.println("Title: " + title);
    System.out.println("User ID: " + userId);

    // Use extracted data in assertions
    Assertions.assertEquals(1, postId);
    Assertions.assertNotNull(title);
}
```

**Your Tasks:**
1. Create a test that extracts a post ID from creation, then uses it to fetch the post
2. Extract all titles from `/posts` and verify none are empty

### Task 6: Query Parameters (10 minutes)

```java
@Test
@Order(8)
@DisplayName("GET with query parameters")
void testQueryParameters() {
    given()
        .queryParam("userId", 1)
        .queryParam("_limit", 5)
        .log().uri()
    .when()
        .get("/posts")
    .then()
        .statusCode(200)
        .body("size()", equalTo(5))
        .body("userId", everyItem(equalTo(1)));
}
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Maven project compiles without errors
- [ ] GET tests (all posts, single post, filtered)
- [ ] POST test creating new resource
- [ ] PUT test updating resource
- [ ] DELETE test removing resource
- [ ] Response extraction working
- [ ] Query parameter tests
- [ ] All tests pass with `mvn test`

---

## Starter Code

Find complete starter code in the `starter_code/` directory.

---

## Challenge Tasks (Optional)

### 1. Response Time Assertion
```java
.time(lessThan(2000L))  // milliseconds
```

### 2. Header Assertions
```java
.header("Content-Type", containsString("json"))
.headers("Cache-Control", notNullValue())
```

### 3. Chained Assertions
```java
.body("$", hasKey("id"))
.body("id", instanceOf(Integer.class))
.body("title", allOf(notNullValue(), not(emptyString())))
```

---

## Submission Checklist

| Test | Implemented | Passing |
|------|-------------|---------|
| GET all posts | ☐ | ☐ |
| GET post by ID | ☐ | ☐ |
| GET posts filtered | ☐ | ☐ |
| POST create post | ☐ | ☐ |
| PUT update post | ☐ | ☐ |
| DELETE remove post | ☐ | ☐ |
| Extract response data | ☐ | ☐ |
| Query parameters | ☐ | ☐ |

---

## Additional Resources

- Written Content: `rest-assured-overview.md`, `rest-assured-test-requests.md`
- [REST Assured Wiki](https://github.com/rest-assured/rest-assured/wiki/Usage)
- [Hamcrest Matchers](http://hamcrest.org/JavaHamcrest/javadoc/2.2/)

