# Post-Test Scripts in Postman

## Learning Objectives
- Write effective test assertions using Postman's test scripting
- Master the pm.test() and pm.expect() syntax for validations
- Validate status codes, response bodies, and headers
- Implement response time checks for performance validation
- Chain requests by extracting and storing response data

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, sending requests is only half the story. The real power lies in validating that APIs behave correctly. Post-test scripts transform Postman from an exploration tool into a robust testing platform.

Consider this: you can manually verify that a response looks correct, but what happens when you need to run 100 tests across multiple environments? Post-test scripts automate this validation, ensuring consistent, repeatable, and reliable API testing. They're the foundation for the automated test suites you'll build throughout this course.

## Understanding Post-Test Scripts

### What Are Post-Test Scripts?

Post-test scripts (often called "Tests" in Postman) are JavaScript code blocks that execute **after** the response is received. They allow you to:

- Validate response data
- Check performance metrics
- Extract data for subsequent requests
- Set or update variables
- Log information for debugging

### Execution Timing

```
┌─────────────────────────────────────────────────────────────┐
│                    Request Execution Flow                    │
├─────────────────────────────────────────────────────────────┤
│  1. Pre-request Scripts Execute                              │
│              ↓                                               │
│  2. Request Sent                                             │
│              ↓                                               │
│  3. Response Received                                        │
│              ↓                                               │
│  4. ─────── POST-TEST SCRIPTS EXECUTE ───────               │
│     ├── Request-level tests                                  │
│     ├── Folder-level tests                                   │
│     └── Collection-level tests                               │
│              ↓                                               │
│  5. Results Displayed                                        │
└─────────────────────────────────────────────────────────────┘
```

### Where to Write Tests

```
Request Tab → Scripts → Post-response
       OR
Collection → ... → Edit → Scripts → Post-response
```

## Writing Assertions with pm.test()

### Basic Structure

```javascript
pm.test("Test name describing what is being verified", function() {
    // Assertion code here
});
```

**Key Points:**
- First parameter: descriptive test name (appears in Test Results)
- Second parameter: callback function containing assertions
- Tests are independent—one failure doesn't stop others

### Simple Examples

```javascript
// Test that response status is 200
pm.test("Status code is 200", function() {
    pm.response.to.have.status(200);
});

// Test that response is JSON
pm.test("Response is JSON", function() {
    pm.response.to.be.json;
});

// Test that response time is acceptable
pm.test("Response time is under 500ms", function() {
    pm.expect(pm.response.responseTime).to.be.below(500);
});
```

## The pm.expect() Assertion Library

Postman uses Chai BDD assertion syntax through `pm.expect()`. This provides readable, expressive assertions.

### Equality Assertions

```javascript
// Exact equality
pm.expect(value).to.equal(expected);
pm.expect(value).to.eql(expected);  // Deep equality for objects

// Examples
pm.expect(response.status).to.equal("success");
pm.expect(user.id).to.equal(123);
pm.expect(user).to.eql({ id: 123, name: "John" });  // Object comparison
```

### Type Assertions

```javascript
pm.expect(value).to.be.a("string");
pm.expect(value).to.be.an("array");
pm.expect(value).to.be.an("object");
pm.expect(value).to.be.a("number");
pm.expect(value).to.be.a("boolean");
pm.expect(value).to.be.null;
pm.expect(value).to.be.undefined;
pm.expect(value).to.be.NaN;
```

### Truthiness Assertions

```javascript
pm.expect(value).to.be.true;
pm.expect(value).to.be.false;
pm.expect(value).to.be.ok;      // Truthy
pm.expect(value).to.not.be.ok;  // Falsy
pm.expect(value).to.exist;      // Not null/undefined
```

### Comparison Assertions

```javascript
pm.expect(number).to.be.above(5);      // > 5
pm.expect(number).to.be.below(10);     // < 10
pm.expect(number).to.be.at.least(5);   // >= 5
pm.expect(number).to.be.at.most(10);   // <= 10
pm.expect(number).to.be.within(5, 10); // 5 <= x <= 10
```

### String Assertions

```javascript
pm.expect(string).to.include("substring");
pm.expect(string).to.have.string("substring");
pm.expect(string).to.match(/regex/);
pm.expect(string).to.have.lengthOf(10);
pm.expect(string).to.be.empty;
pm.expect(string).to.not.be.empty;
```

### Array Assertions

```javascript
pm.expect(array).to.include("item");
pm.expect(array).to.include.members(["a", "b"]);
pm.expect(array).to.have.members(["a", "b", "c"]);  // Exact members
pm.expect(array).to.have.lengthOf(5);
pm.expect(array).to.be.empty;
pm.expect(array).to.not.be.empty;
pm.expect(array).to.have.length.above(3);
```

### Object Assertions

```javascript
pm.expect(object).to.have.property("name");
pm.expect(object).to.have.property("name", "John");  // With value
pm.expect(object).to.have.nested.property("user.address.city");
pm.expect(object).to.have.all.keys("id", "name", "email");
pm.expect(object).to.have.any.keys("id", "uuid");
pm.expect(object).to.include({ name: "John" });
pm.expect(object).to.deep.include({ user: { id: 1 } });
```

### Negation

```javascript
pm.expect(value).to.not.equal(unexpected);
pm.expect(array).to.not.include("item");
pm.expect(object).to.not.have.property("deletedAt");
pm.expect(string).to.not.be.empty;
```

## Validating Status Codes

### Common Status Code Tests

```javascript
// Success codes
pm.test("Status is 200 OK", function() {
    pm.response.to.have.status(200);
});

pm.test("Status is 201 Created", function() {
    pm.response.to.have.status(201);
});

pm.test("Status is 204 No Content", function() {
    pm.response.to.have.status(204);
});

// Client error codes
pm.test("Status is 400 Bad Request", function() {
    pm.response.to.have.status(400);
});

pm.test("Status is 401 Unauthorized", function() {
    pm.response.to.have.status(401);
});

pm.test("Status is 404 Not Found", function() {
    pm.response.to.have.status(404);
});

// Server error codes
pm.test("Status is not 500", function() {
    pm.response.to.not.have.status(500);
});
```

### Status Code Ranges

```javascript
// Check success range (2xx)
pm.test("Status code is success", function() {
    pm.expect(pm.response.code).to.be.within(200, 299);
});

// Check client error range (4xx)
pm.test("Status code is client error", function() {
    pm.expect(pm.response.code).to.be.within(400, 499);
});

// Using status text
pm.test("Status is OK", function() {
    pm.response.to.have.status("OK");
});
```

### Status Code with Message

```javascript
pm.test("Successful response", function() {
    pm.expect(pm.response.code, "Expected 200 OK but got " + pm.response.code)
        .to.equal(200);
});
```

## Response Body Assertions

### Accessing Response Body

```javascript
// As JSON object
const jsonData = pm.response.json();

// As text
const textData = pm.response.text();

// As XML (parsed)
const xmlData = xml2Json(pm.response.text());
```

### Validating JSON Structure

```javascript
pm.test("Response has required fields", function() {
    const response = pm.response.json();
    
    pm.expect(response).to.have.property("id");
    pm.expect(response).to.have.property("name");
    pm.expect(response).to.have.property("email");
    pm.expect(response).to.have.property("createdAt");
});

pm.test("Response structure is correct", function() {
    const response = pm.response.json();
    
    pm.expect(response.id).to.be.a("number");
    pm.expect(response.name).to.be.a("string");
    pm.expect(response.active).to.be.a("boolean");
    pm.expect(response.tags).to.be.an("array");
    pm.expect(response.metadata).to.be.an("object");
});
```

### Validating Specific Values

```javascript
pm.test("User data is correct", function() {
    const user = pm.response.json();
    
    pm.expect(user.id).to.equal(123);
    pm.expect(user.name).to.equal("John Doe");
    pm.expect(user.email).to.include("@");
    pm.expect(user.status).to.be.oneOf(["active", "pending", "inactive"]);
});
```

### Validating Arrays

```javascript
pm.test("Products list is valid", function() {
    const response = pm.response.json();
    
    // Check array exists and has items
    pm.expect(response.products).to.be.an("array");
    pm.expect(response.products).to.have.length.above(0);
    
    // Check each item has required fields
    response.products.forEach((product, index) => {
        pm.expect(product, `Product at index ${index}`).to.have.property("id");
        pm.expect(product, `Product at index ${index}`).to.have.property("name");
        pm.expect(product.price, `Price at index ${index}`).to.be.a("number");
    });
});

pm.test("First product has correct structure", function() {
    const firstProduct = pm.response.json().products[0];
    
    pm.expect(firstProduct).to.include.keys("id", "name", "price");
    pm.expect(firstProduct.price).to.be.above(0);
});
```

### Validating Nested Objects

```javascript
pm.test("Nested user address is valid", function() {
    const user = pm.response.json();
    
    pm.expect(user).to.have.nested.property("address.street");
    pm.expect(user).to.have.nested.property("address.city");
    pm.expect(user).to.have.nested.property("address.zipCode");
    
    pm.expect(user.address.country).to.equal("USA");
});
```

### JSON Schema Validation

```javascript
const schema = {
    "type": "object",
    "required": ["id", "name", "email"],
    "properties": {
        "id": { "type": "number" },
        "name": { "type": "string", "minLength": 1 },
        "email": { "type": "string", "format": "email" },
        "age": { "type": "number", "minimum": 0 },
        "roles": {
            "type": "array",
            "items": { "type": "string" }
        }
    }
};

pm.test("Response matches JSON schema", function() {
    pm.response.to.have.jsonSchema(schema);
});
```

## Response Header Validation

### Checking Header Existence

```javascript
pm.test("Content-Type header is present", function() {
    pm.response.to.have.header("Content-Type");
});

pm.test("Required headers are present", function() {
    pm.expect(pm.response.headers.has("X-Request-ID")).to.be.true;
    pm.expect(pm.response.headers.has("X-RateLimit-Remaining")).to.be.true;
});
```

### Checking Header Values

```javascript
pm.test("Content-Type is JSON", function() {
    pm.expect(pm.response.headers.get("Content-Type"))
        .to.include("application/json");
});

pm.test("Cache-Control is set correctly", function() {
    pm.expect(pm.response.headers.get("Cache-Control"))
        .to.equal("no-cache, no-store, must-revalidate");
});

pm.test("CORS headers are present", function() {
    pm.expect(pm.response.headers.get("Access-Control-Allow-Origin"))
        .to.equal("*");
});
```

### Rate Limit Headers

```javascript
pm.test("Rate limit headers are valid", function() {
    const limit = parseInt(pm.response.headers.get("X-RateLimit-Limit"));
    const remaining = parseInt(pm.response.headers.get("X-RateLimit-Remaining"));
    
    pm.expect(limit).to.be.a("number");
    pm.expect(remaining).to.be.a("number");
    pm.expect(remaining).to.be.at.most(limit);
    pm.expect(remaining).to.be.at.least(0);
});
```

## Response Time Checks

### Basic Response Time Tests

```javascript
pm.test("Response time is under 500ms", function() {
    pm.expect(pm.response.responseTime).to.be.below(500);
});

pm.test("Response time is acceptable", function() {
    pm.expect(pm.response.responseTime).to.be.within(0, 2000);
});
```

### SLA-Based Tests

```javascript
// Define SLA thresholds
const SLA = {
    fast: 200,      // Simple lookups
    normal: 500,    // Standard operations
    slow: 2000,     // Complex operations
    timeout: 5000   // Maximum acceptable
};

pm.test("Response meets SLA", function() {
    const endpoint = pm.request.url.getPath();
    
    // Different SLAs for different endpoints
    if (endpoint.includes("/health")) {
        pm.expect(pm.response.responseTime).to.be.below(SLA.fast);
    } else if (endpoint.includes("/search")) {
        pm.expect(pm.response.responseTime).to.be.below(SLA.slow);
    } else {
        pm.expect(pm.response.responseTime).to.be.below(SLA.normal);
    }
});
```

### Performance Logging

```javascript
pm.test("Log performance metrics", function() {
    const metrics = {
        endpoint: pm.request.url.getPath(),
        method: pm.request.method,
        responseTime: pm.response.responseTime,
        responseSize: pm.response.headers.get("Content-Length") || "unknown",
        timestamp: new Date().toISOString()
    };
    
    console.log("Performance:", JSON.stringify(metrics, null, 2));
    
    pm.expect(pm.response.responseTime).to.be.below(2000);
});
```

## Chaining Requests with Variables

### Extracting and Storing Data

```javascript
// Store user ID for subsequent requests
pm.test("Store created user ID", function() {
    const response = pm.response.json();
    pm.environment.set("userId", response.id);
    console.log("Stored userId:", response.id);
});

// Store multiple values
pm.test("Extract and store response data", function() {
    const response = pm.response.json();
    
    pm.environment.set("userId", response.user.id);
    pm.environment.set("userEmail", response.user.email);
    pm.environment.set("accessToken", response.token);
    pm.environment.set("tokenExpiry", response.expiresAt);
});
```

### Conditional Storage

```javascript
pm.test("Store data on success", function() {
    if (pm.response.code === 200) {
        const response = pm.response.json();
        pm.environment.set("lastSuccessfulResponse", JSON.stringify(response));
        pm.environment.set("lastSuccessTime", new Date().toISOString());
    }
});
```

### Building Request Chains

**Request 1: Create User**
```javascript
// Post-test script for Create User
pm.test("User created successfully", function() {
    pm.response.to.have.status(201);
    const user = pm.response.json();
    pm.environment.set("newUserId", user.id);
});
```

**Request 2: Get User (uses newUserId)**
```
URL: {{baseUrl}}/users/{{newUserId}}
```
```javascript
// Post-test script for Get User
pm.test("Retrieved correct user", function() {
    const user = pm.response.json();
    const expectedId = pm.environment.get("newUserId");
    pm.expect(user.id.toString()).to.equal(expectedId);
});
```

**Request 3: Update User**
```
URL: {{baseUrl}}/users/{{newUserId}}
```
```javascript
// Post-test script for Update User
pm.test("User updated successfully", function() {
    pm.response.to.have.status(200);
});
```

**Request 4: Delete User**
```
URL: {{baseUrl}}/users/{{newUserId}}
```
```javascript
// Post-test script for Delete User
pm.test("User deleted successfully", function() {
    pm.expect(pm.response.code).to.be.oneOf([200, 204]);
    // Clean up environment variable
    pm.environment.unset("newUserId");
});
```

## Comprehensive Test Examples

### Example 1: Full User API Test

```javascript
// Test suite for GET /users/{id}
const response = pm.response.json();

pm.test("Status code is 200", function() {
    pm.response.to.have.status(200);
});

pm.test("Response time is acceptable", function() {
    pm.expect(pm.response.responseTime).to.be.below(500);
});

pm.test("Response is JSON", function() {
    pm.response.to.be.json;
});

pm.test("User has all required fields", function() {
    pm.expect(response).to.have.all.keys(
        "id", "email", "firstName", "lastName", "createdAt", "updatedAt"
    );
});

pm.test("User ID matches request", function() {
    const requestedId = pm.environment.get("userId");
    pm.expect(response.id.toString()).to.equal(requestedId);
});

pm.test("Email format is valid", function() {
    pm.expect(response.email).to.match(/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/);
});

pm.test("Timestamps are valid ISO dates", function() {
    pm.expect(new Date(response.createdAt).toString()).to.not.equal("Invalid Date");
    pm.expect(new Date(response.updatedAt).toString()).to.not.equal("Invalid Date");
});
```

### Example 2: Error Response Validation

```javascript
// Test suite for error responses
pm.test("Status code is 400", function() {
    pm.response.to.have.status(400);
});

pm.test("Error response has correct structure", function() {
    const error = pm.response.json();
    
    pm.expect(error).to.have.property("error");
    pm.expect(error.error).to.have.property("code");
    pm.expect(error.error).to.have.property("message");
});

pm.test("Error code is meaningful", function() {
    const error = pm.response.json();
    pm.expect(error.error.code).to.be.oneOf([
        "VALIDATION_ERROR",
        "INVALID_INPUT",
        "MISSING_FIELD"
    ]);
});

pm.test("Error message is helpful", function() {
    const error = pm.response.json();
    pm.expect(error.error.message).to.not.be.empty;
    pm.expect(error.error.message).to.not.equal("Something went wrong");
});

pm.test("Validation details provided", function() {
    const error = pm.response.json();
    if (error.error.code === "VALIDATION_ERROR") {
        pm.expect(error.error).to.have.property("details");
        pm.expect(error.error.details).to.be.an("array");
    }
});
```

### Example 3: List Endpoint Test

```javascript
// Test suite for GET /products
const response = pm.response.json();

pm.test("Status code is 200", function() {
    pm.response.to.have.status(200);
});

pm.test("Response has pagination metadata", function() {
    pm.expect(response).to.have.property("data");
    pm.expect(response).to.have.property("pagination");
    pm.expect(response.pagination).to.include.keys("page", "pageSize", "total");
});

pm.test("Data is an array", function() {
    pm.expect(response.data).to.be.an("array");
});

pm.test("Page size matches request", function() {
    const requestedSize = parseInt(pm.request.url.query.get("pageSize") || "20");
    pm.expect(response.data.length).to.be.at.most(requestedSize);
});

pm.test("All items have required fields", function() {
    response.data.forEach((item, index) => {
        pm.expect(item, `Item ${index}`).to.have.property("id");
        pm.expect(item, `Item ${index}`).to.have.property("name");
        pm.expect(item, `Item ${index}`).to.have.property("price");
    });
});

pm.test("Prices are positive numbers", function() {
    response.data.forEach((item, index) => {
        pm.expect(item.price, `Price of item ${index}`).to.be.a("number");
        pm.expect(item.price, `Price of item ${index}`).to.be.above(0);
    });
});
```

## Test Results Panel

After running tests, the Test Results panel shows:

```
Test Results (6/7 passed)
✓ Status code is 200
✓ Response time is acceptable  
✓ Response is JSON
✓ User has all required fields
✗ Email format is valid
  AssertionError: expected 'invalid-email' to match /^[\w-\.]+@/
✓ User ID matches request
✓ Timestamps are valid
```

## Summary

- **pm.test()** creates named test cases with assertion callbacks
- **pm.expect()** provides expressive Chai-style assertions
- **Status codes** should be validated for both success and error scenarios
- **Response bodies** can be validated for structure, types, and values
- **Headers** carry important metadata like rate limits and content type
- **Response time** tests ensure performance requirements are met
- **Variable extraction** enables request chaining for complex test flows

With pre-request and post-test scripts mastered, you're now equipped to build sophisticated API test suites in Postman. The next lesson covers environments, which allow you to run these same tests across development, staging, and production systems.

## Additional Resources

- [Postman Test Scripts Documentation](https://learning.postman.com/docs/writing-scripts/test-scripts/) - Official guide
- [Chai Assertion Library](https://www.chaijs.com/api/bdd/) - Complete assertion reference
- [JSON Schema](https://json-schema.org/) - Schema validation specification

