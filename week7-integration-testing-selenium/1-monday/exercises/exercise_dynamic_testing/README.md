# Lab: Dynamic Testing with Pre-Request Scripts

## Overview

**Duration:** 60-75 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll use Postman's pre-request scripts to generate dynamic test data, implement conditional logic, and chain requests together. You'll transform static tests into intelligent, adaptive test automation.

---

## Learning Objectives

By completing this lab, you will:
- Write pre-request scripts to generate dynamic data
- Use Postman's built-in dynamic variables
- Create custom data generation functions
- Implement conditional request logic
- Chain multiple requests using variables

---

## Prerequisites

- Completed "Postman Assertions" exercise
- Basic JavaScript knowledge
- Understanding of Postman variables

---

## The Scenario

The BookHaven system now requires unique data for each test run. User registrations need unique email addresses, orders need timestamps, and test IDs must be traceable. Your task is to make the test suite fully dynamic.

---

## Core Tasks

### Task 1: Built-in Dynamic Variables (10 minutes)

Create a POST request to `https://jsonplaceholder.typicode.com/posts`

**In the Body tab, use Postman's dynamic variables:**

```json
{
    "title": "Test Post {{$randomLoremWords}}",
    "body": "{{$randomLoremParagraph}}",
    "userId": {{$randomInt}}
}
```

**Explore these built-in variables:**
- `{{$guid}}` - Random UUID
- `{{$timestamp}}` - Current Unix timestamp
- `{{$isoTimestamp}}` - ISO format timestamp
- `{{$randomInt}}` - Random integer (0-1000)
- `{{$randomEmail}}` - Random email address
- `{{$randomFirstName}}` - Random first name
- `{{$randomLastName}}` - Random last name
- `{{$randomFullName}}` - Random full name
- `{{$randomLoremWords}}` - Random lorem ipsum words

**Your Tasks:**
1. Run the request 3 times and observe different values
2. Create a user registration request using random email and name
3. Document which variables you found most useful

### Task 2: Custom Pre-Request Scripts (20 minutes)

Create a new POST request and add pre-request scripts:

**Script 1: Unique Email Generator**
```javascript
// Generate unique email with timestamp
const timestamp = Date.now();
const randomNum = Math.floor(Math.random() * 10000);
const uniqueEmail = `testuser_${timestamp}_${randomNum}@bookhaven.test`;

pm.environment.set("uniqueEmail", uniqueEmail);
console.log("Generated email:", uniqueEmail);
```

**Use in request body:**
```json
{
    "email": "{{uniqueEmail}}",
    "name": "Test User"
}
```

**Script 2: Random Number in Range**
```javascript
// Function to generate random number in range
function randomBetween(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

// Generate test data
pm.environment.set("randomAge", randomBetween(18, 65));
pm.environment.set("randomQuantity", randomBetween(1, 10));
pm.environment.set("randomPrice", (randomBetween(999, 9999) / 100).toFixed(2));

console.log("Age:", pm.environment.get("randomAge"));
console.log("Quantity:", pm.environment.get("randomQuantity"));
console.log("Price:", pm.environment.get("randomPrice"));
```

**Script 3: Random Selection from Array**
```javascript
// Select random item from predefined list
const categories = ["fiction", "non-fiction", "science", "history", "biography"];
const statuses = ["pending", "processing", "shipped", "delivered"];

const randomCategory = categories[Math.floor(Math.random() * categories.length)];
const randomStatus = statuses[Math.floor(Math.random() * statuses.length)];

pm.environment.set("bookCategory", randomCategory);
pm.environment.set("orderStatus", randomStatus);
```

**Your Tasks:**
1. Create a pre-request script that generates a unique order ID (format: `ORD-YYYYMMDD-XXXX`)
2. Generate a random ISBN number (13 digits starting with 978)
3. Create a function that generates a random date within the last 30 days

### Task 3: Date and Time Manipulation (15 minutes)

**Add these date scripts:**

```javascript
// Current date in various formats
const now = new Date();

// ISO format (API standard)
pm.environment.set("currentDateTime", now.toISOString());

// Date only (YYYY-MM-DD)
pm.environment.set("currentDate", now.toISOString().split('T')[0]);

// Future date (7 days from now)
const futureDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);
pm.environment.set("deliveryDate", futureDate.toISOString().split('T')[0]);

// Past date (30 days ago)
const pastDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
pm.environment.set("orderDate", pastDate.toISOString().split('T')[0]);

// Unix timestamp
pm.environment.set("unixTimestamp", Math.floor(Date.now() / 1000));

console.log("Current:", pm.environment.get("currentDate"));
console.log("Future:", pm.environment.get("deliveryDate"));
console.log("Past:", pm.environment.get("orderDate"));
```

**Your Tasks:**
1. Generate a random date within the current year
2. Create an expiration date that is always 1 year in the future
3. Generate a timestamp for "next Monday"

### Task 4: Conditional Logic (15 minutes)

**Implement environment-based behavior:**

```javascript
// Get environment indicator
const env = pm.environment.get("testEnvironment") || "dev";

// Set configuration based on environment
const configs = {
    "dev": {
        baseUrl: "https://dev-api.bookhaven.test",
        timeout: 60000,
        debugMode: true
    },
    "staging": {
        baseUrl: "https://staging-api.bookhaven.test",
        timeout: 30000,
        debugMode: true
    },
    "prod": {
        baseUrl: "https://api.bookhaven.test",
        timeout: 10000,
        debugMode: false
    }
};

const config = configs[env] || configs["dev"];

pm.environment.set("baseUrl", config.baseUrl);
pm.environment.set("requestTimeout", config.timeout);
pm.environment.set("debugMode", config.debugMode);

if (config.debugMode) {
    console.log(`Running in ${env} environment`);
    console.log("Config:", JSON.stringify(config, null, 2));
}
```

**Test type conditional:**
```javascript
const testType = pm.environment.get("testType") || "positive";

if (testType === "positive") {
    pm.environment.set("testEmail", "valid@example.com");
    pm.environment.set("testQuantity", "5");
} else if (testType === "negative") {
    pm.environment.set("testEmail", "invalid-email");
    pm.environment.set("testQuantity", "-1");
} else if (testType === "boundary") {
    pm.environment.set("testEmail", "a@b.co");
    pm.environment.set("testQuantity", "0");
}
```

**Your Tasks:**
1. Add conditional logic for "load test" that sets higher quantities
2. Create logic that adds debug headers when debugMode is true
3. Implement a feature flag system for new vs legacy endpoints

### Task 5: Request Chaining (20 minutes)

Create a multi-request flow where each request uses data from the previous one:

**Request 1: Create User (POST)**

Pre-request script:
```javascript
// Generate unique user data
pm.environment.set("newUserEmail", `user_${Date.now()}@test.com`);
pm.environment.set("newUserName", "Test User " + Math.floor(Math.random() * 1000));
```

Post-test script:
```javascript
pm.test("User created successfully", function() {
    pm.response.to.have.status(201);
});

// Store the new user ID for next request
const response = pm.response.json();
pm.environment.set("createdUserId", response.id);
console.log("Created user with ID:", response.id);
```

Body:
```json
{
    "email": "{{newUserEmail}}",
    "name": "{{newUserName}}"
}
```

**Request 2: Get Created User (GET)**

URL: `https://jsonplaceholder.typicode.com/users/{{createdUserId}}`

Pre-request script:
```javascript
const userId = pm.environment.get("createdUserId");
if (!userId) {
    console.warn("No user ID found - run Create User first!");
}
```

Post-test script:
```javascript
pm.test("Retrieved created user", function() {
    pm.response.to.have.status(200);
});
```

**Request 3: Create Post for User (POST)**

URL: `https://jsonplaceholder.typicode.com/posts`

Body:
```json
{
    "title": "Post by {{newUserName}}",
    "body": "This is a test post created by user {{createdUserId}}",
    "userId": {{createdUserId}}
}
```

**Your Tasks:**
1. Complete the chain with a DELETE request
2. Add error handling if previous request data is missing
3. Create a "cleanup" variable that tracks all created resources

---

## Definition of Done

Your lab is complete when you have:

- [ ] Used at least 5 different built-in dynamic variables
- [ ] Created 3 custom data generation scripts
- [ ] Implemented date manipulation for past/present/future dates
- [ ] Added conditional logic based on environment or test type
- [ ] Built a 3-request chain passing data between requests
- [ ] All scripts execute without errors
- [ ] Console output shows generated values

---

## Challenge Tasks (Optional)

1. **Request Signing:**
```javascript
const CryptoJS = require('crypto-js');
const secretKey = pm.environment.get("apiSecret") || "test-secret";
const timestamp = Date.now().toString();
const message = pm.request.method + "|" + pm.request.url.getPath() + "|" + timestamp;
const signature = CryptoJS.HmacSHA256(message, secretKey).toString();
pm.environment.set("requestSignature", signature);
pm.environment.set("requestTimestamp", timestamp);
```

2. **Sequential Test IDs:**
```javascript
let counter = parseInt(pm.collectionVariables.get("testCounter") || "0");
counter++;
pm.collectionVariables.set("testCounter", counter.toString());
pm.environment.set("testId", `TC-${counter.toString().padStart(5, '0')}`);
```

3. **Token Refresh Logic:**
```javascript
const token = pm.environment.get("authToken");
const tokenExpiry = parseInt(pm.environment.get("tokenExpiry") || "0");

if (!token || Date.now() > tokenExpiry) {
    console.log("Token expired, need to refresh");
    // Set flag for manual refresh or implement pm.sendRequest
    pm.environment.set("needsTokenRefresh", "true");
}
```

---

## Submission Checklist

| Task | Completed |
|------|-----------|
| Built-in dynamic variables used | ☐ |
| Unique email generator | ☐ |
| Random number in range | ☐ |
| Random selection from array | ☐ |
| Custom order ID generator | ☐ |
| Date manipulation scripts | ☐ |
| Environment-based conditionals | ☐ |
| Test type conditionals | ☐ |
| Request chain (3+ requests) | ☐ |
| Console logging for debugging | ☐ |

---

## Common Mistakes

1. **Variable scope confusion:** Environment vs Collection vs Global
2. **Not logging output:** Use `console.log()` to debug
3. **Type issues:** Variables are always stored as strings
4. **Timing issues:** Pre-request runs before request, Tests run after response
5. **Missing error handling:** Check if variables exist before using

---

## Additional Resources

- Written Content: `pre-test-script.md`
- [Postman Dynamic Variables List](https://learning.postman.com/docs/writing-scripts/script-references/variables-list/)
- [Postman Sandbox API](https://learning.postman.com/docs/writing-scripts/script-references/postman-sandbox-api-reference/)

