# Lab: Postman Test Assertions

## Overview

**Duration:** 60-75 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll write comprehensive test assertions for API responses. You'll learn to validate status codes, response bodies, headers, and response times using Postman's built-in testing capabilities.

---

## Learning Objectives

By completing this lab, you will:
- Write test assertions using `pm.test()` and `pm.expect()`
- Validate HTTP status codes and response times
- Assert JSON response body structure and values
- Verify response headers
- Create reusable test patterns

---

## Prerequisites

- Completed "First API Tests" exercise
- Understanding of Postman's Tests tab
- Familiarity with JSON structure

---

## The Scenario

The BookHaven QA lead has asked you to add automated assertions to your API tests. Every test should verify that the API behaves correctly—not just return responses, but validate them automatically. Your assertions will be used in the CI/CD pipeline.

---

## Core Tasks

### Task 1: Status Code Assertions (15 minutes)

Open or create a GET request to `https://jsonplaceholder.typicode.com/posts/1`

**Add these test scripts in the Tests tab:**

```javascript
// Test 1: Status code is 200
pm.test("Status code is 200", function() {
    pm.response.to.have.status(200);
});

// Test 2: Status code is in success range
pm.test("Status code is success (2xx)", function() {
    pm.expect(pm.response.code).to.be.within(200, 299);
});

// Test 3: Status text
pm.test("Status message is OK", function() {
    pm.response.to.have.status("OK");
});
```

**Your Tasks:**
1. Run the request and verify all tests pass
2. Add a test that fails if status is 404
3. Create a similar request to `/posts/9999` and add tests expecting 404

### Task 2: Response Body Assertions (20 minutes)

Create or use an existing GET request to `https://jsonplaceholder.typicode.com/posts/1`

**Add these assertions:**

```javascript
// Parse response
const jsonData = pm.response.json();

// Test: Response has required fields
pm.test("Response has required fields", function() {
    pm.expect(jsonData).to.have.property("id");
    pm.expect(jsonData).to.have.property("title");
    pm.expect(jsonData).to.have.property("body");
    pm.expect(jsonData).to.have.property("userId");
});

// Test: Data types are correct
pm.test("Field types are correct", function() {
    pm.expect(jsonData.id).to.be.a("number");
    pm.expect(jsonData.title).to.be.a("string");
    pm.expect(jsonData.body).to.be.a("string");
    pm.expect(jsonData.userId).to.be.a("number");
});

// Test: Specific values
pm.test("Post ID is 1", function() {
    pm.expect(jsonData.id).to.equal(1);
});

// Test: String contains expected content
pm.test("Title is not empty", function() {
    pm.expect(jsonData.title).to.not.be.empty;
});
```

**Your Tasks:**
1. Add a test verifying `userId` is within range 1-10
2. Add a test checking that `title` has minimum length of 5 characters
3. Add a test using `to.include()` to check if body contains a specific word

### Task 3: Array Response Assertions (15 minutes)

Create a GET request to `https://jsonplaceholder.typicode.com/posts`

**Add these assertions:**

```javascript
const jsonData = pm.response.json();

// Test: Response is an array
pm.test("Response is an array", function() {
    pm.expect(jsonData).to.be.an("array");
});

// Test: Array has items
pm.test("Array is not empty", function() {
    pm.expect(jsonData).to.have.length.above(0);
});

// Test: Array has expected count
pm.test("Array has 100 posts", function() {
    pm.expect(jsonData).to.have.lengthOf(100);
});

// Test: First item has required structure
pm.test("First item has required fields", function() {
    const firstPost = jsonData[0];
    pm.expect(firstPost).to.have.all.keys("userId", "id", "title", "body");
});
```

**Your Tasks:**
1. Add a test verifying every item in the array has an `id` property
2. Add a test checking that all `id` values are unique
3. Create an assertion that all `userId` values are between 1 and 10

**Hint for iterating:**
```javascript
pm.test("All items have id", function() {
    jsonData.forEach((item, index) => {
        pm.expect(item, `Item at index ${index}`).to.have.property("id");
    });
});
```

### Task 4: Response Time Assertions (10 minutes)

**Add to any request:**

```javascript
// Test: Response time is acceptable
pm.test("Response time is under 500ms", function() {
    pm.expect(pm.response.responseTime).to.be.below(500);
});

// Test: Response time range
pm.test("Response time is within SLA", function() {
    pm.expect(pm.response.responseTime).to.be.within(0, 2000);
});
```

**Your Tasks:**
1. Add response time tests to all your requests
2. Define different thresholds:
   - GET single resource: < 300ms
   - GET list: < 1000ms
   - POST create: < 500ms

### Task 5: Header Assertions (10 minutes)

**Add header validations:**

```javascript
// Test: Content-Type header
pm.test("Content-Type is JSON", function() {
    pm.expect(pm.response.headers.get("Content-Type"))
        .to.include("application/json");
});

// Test: Header exists
pm.test("Has required headers", function() {
    pm.response.to.have.header("Content-Type");
    pm.response.to.have.header("Date");
});
```

**Your Tasks:**
1. Check for `Cache-Control` header
2. Verify `Content-Length` header exists and is a number
3. Test that no sensitive headers are exposed

### Task 6: Negative Testing (15 minutes)

Create requests for error scenarios:

**Request 1: Invalid Resource**
- GET `https://jsonplaceholder.typicode.com/posts/9999`

```javascript
pm.test("Status code is 404", function() {
    pm.response.to.have.status(404);
});

pm.test("Response is empty object", function() {
    const jsonData = pm.response.json();
    pm.expect(Object.keys(jsonData)).to.have.lengthOf(0);
});
```

**Request 2: Invalid Endpoint**
- GET `https://jsonplaceholder.typicode.com/invalid`

```javascript
pm.test("Status code is 404", function() {
    pm.response.to.have.status(404);
});
```

**Request 3: Missing Required Data (POST)**
- POST to `/posts` with empty body

**Your Tasks:**
1. Create all three negative test scenarios
2. Add appropriate assertions for each
3. Document expected vs actual behavior

---

## Definition of Done

Your lab is complete when you have:

- [ ] Status code assertions for success (200, 201) and error (404) cases
- [ ] Response body validations for structure and values
- [ ] Array response assertions (length, item structure)
- [ ] Response time assertions with appropriate thresholds
- [ ] Header validations
- [ ] At least 3 negative test scenarios
- [ ] All tests passing (green) when run

---

## Challenge Tasks (Optional)

1. **JSON Schema Validation:**
```javascript
const schema = {
    "type": "object",
    "required": ["id", "title", "body", "userId"],
    "properties": {
        "id": { "type": "number" },
        "title": { "type": "string" },
        "body": { "type": "string" },
        "userId": { "type": "number" }
    }
};

pm.test("Schema is valid", function() {
    pm.response.to.have.jsonSchema(schema);
});
```

2. **Conditional Tests:**
```javascript
pm.test("Conditional test based on status", function() {
    if (pm.response.code === 200) {
        const jsonData = pm.response.json();
        pm.expect(jsonData).to.have.property("id");
    } else if (pm.response.code === 404) {
        pm.expect(pm.response.json()).to.be.empty;
    }
});
```

---

## Submission Checklist

| Assertion Type | Implemented | Tests Passing |
|----------------|-------------|---------------|
| Status code (200) | ☐ | ☐ |
| Status code (201 for POST) | ☐ | ☐ |
| Status code (404) | ☐ | ☐ |
| Response body - properties exist | ☐ | ☐ |
| Response body - data types | ☐ | ☐ |
| Response body - specific values | ☐ | ☐ |
| Array length | ☐ | ☐ |
| Array item structure | ☐ | ☐ |
| Response time | ☐ | ☐ |
| Headers | ☐ | ☐ |
| Negative test cases | ☐ | ☐ |

---

## Common Mistakes

1. **Not parsing JSON:** Remember to use `pm.response.json()` before accessing properties
2. **Wrong assertion syntax:** `pm.expect(value).to.be.equal(x)` should be `pm.expect(value).to.equal(x)`
3. **Comparing types incorrectly:** String "1" is not equal to number 1
4. **Forgetting test callback:** `pm.test("name", function() { ... })` requires the function

---

## Additional Resources

- Written Content: `post-test-scripts.md`
- [Chai Assertion Library](https://www.chaijs.com/api/bdd/)
- [Postman Test Examples](https://learning.postman.com/docs/writing-scripts/test-scripts/)

