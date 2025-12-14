# Pre-Request Scripts in Postman

## Learning Objectives
- Understand the purpose and timing of pre-request scripts
- Write JavaScript code to execute before API requests
- Set variables dynamically for request customization
- Generate test data programmatically
- Implement conditional request logic for complex scenarios

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, static requests only get you so far. Real-world API testing requires dynamic, intelligent requests that adapt to different scenarios. Pre-request scripts are your power tool for this challenge.

Imagine testing an API that requires unique email addresses for each user registration, or an endpoint that needs a fresh timestamp in every request. Without pre-request scripts, you'd manually edit these values each time. With them, Postman becomes a smart testing engine that generates data, calculates values, and configures requests on the fly.

## Understanding Pre-Request Scripts

### What Are Pre-Request Scripts?

Pre-request scripts are JavaScript code blocks that execute **before** Postman sends your request. They run in a sandboxed environment with access to Postman's API and several built-in libraries.

### Execution Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    Request Execution Flow                    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  1. Collection Pre-request Script (if any)                  │
│              ↓                                               │
│  2. Folder Pre-request Script (if any)                      │
│              ↓                                               │
│  3. Request Pre-request Script                               │
│              ↓                                               │
│  4. ─────── REQUEST SENT ───────                            │
│              ↓                                               │
│  5. Response Received                                        │
│              ↓                                               │
│  6. Test Scripts Execute                                     │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Where to Write Pre-Request Scripts

In Postman, click on the "Scripts" tab, then select "Pre-request":

```
Request Tab → Scripts → Pre-request
       OR
Collection → ... → Edit → Scripts → Pre-request
```

## JavaScript in Postman

### The pm Object

The `pm` object is your gateway to Postman's functionality:

```javascript
// Access request information
pm.request        // Current request object
pm.request.url    // Request URL
pm.request.headers // Request headers

// Work with variables
pm.variables      // Current scope variables
pm.collectionVariables  // Collection-level variables
pm.environment    // Environment variables
pm.globals        // Global variables

// Utilities
pm.sendRequest()  // Send additional requests
```

### Available Libraries

Postman includes several libraries:

```javascript
// Lodash for utility functions
const _ = require('lodash');

// Moment.js for dates (deprecated, but available)
const moment = require('moment');

// CryptoJS for encryption
const CryptoJS = require('crypto-js');

// uuid for unique identifiers
const uuid = require('uuid');

// cheerio for HTML parsing
const cheerio = require('cheerio');

// Built-in libraries
// JSON (native)
// Math (native)
// Date (native)
```

### Console for Debugging

```javascript
console.log("Message for debugging");
console.info("Informational message");
console.warn("Warning message");
console.error("Error message");

// View console: View → Show Postman Console
// Or: Ctrl+Alt+C (Cmd+Alt+C on Mac)
```

## Setting Variables Dynamically

### Variable Scopes

```
Scope Priority (highest to lowest):
1. Local (request scope)
2. Data (from data file during Collection Runner)
3. Environment
4. Collection
5. Global
```

### Setting Variables

```javascript
// Set environment variable
pm.environment.set("userId", "12345");

// Set collection variable
pm.collectionVariables.set("baseUrl", "https://api.staging.example.com");

// Set global variable
pm.globals.set("apiVersion", "v2");

// Set local (temporary) variable
pm.variables.set("tempToken", "abc123");
```

### Getting Variables

```javascript
// Get from specific scope
const userId = pm.environment.get("userId");
const baseUrl = pm.collectionVariables.get("baseUrl");
const version = pm.globals.get("apiVersion");

// Get from any scope (uses priority order)
const value = pm.variables.get("anyVariable");
```

### Using Variables in Requests

Once set, variables can be used anywhere with double curly braces:

```
URL: {{baseUrl}}/api/{{apiVersion}}/users/{{userId}}
Headers: Authorization: Bearer {{accessToken}}
Body: { "id": "{{userId}}", "timestamp": "{{requestTime}}" }
```

### Practical Variable Examples

**Example 1: Dynamic Base URL**
```javascript
// Pre-request script
const environment = pm.environment.get("env") || "dev";

const urls = {
    "dev": "https://dev-api.example.com",
    "staging": "https://staging-api.example.com",
    "prod": "https://api.example.com"
};

pm.collectionVariables.set("baseUrl", urls[environment]);
```

**Example 2: Request Timestamp**
```javascript
// Add timestamp to every request
pm.collectionVariables.set("requestTimestamp", new Date().toISOString());

// For Unix timestamp
pm.collectionVariables.set("unixTimestamp", Math.floor(Date.now() / 1000));
```

## Generating Test Data

### Built-in Dynamic Variables

Postman provides dynamic variables that generate data automatically:

```
{{$guid}}           → "550e8400-e29b-41d4-a716-446655440000"
{{$randomUUID}}     → "6929bb52-3ab2-448a-9796-d6480ecad36b"
{{$timestamp}}      → 1562757107 (Unix timestamp)
{{$isoTimestamp}}   → "2023-06-15T09:30:45.123Z"
{{$randomInt}}      → 752 (0-1000)

{{$randomFirstName}}    → "Ethan"
{{$randomLastName}}     → "Smith"
{{$randomFullName}}     → "Ethan Smith"
{{$randomEmail}}        → "ethan.smith@example.com"
{{$randomUserName}}     → "ethan_smith123"
{{$randomPassword}}     → "password123"

{{$randomPhoneNumber}}     → "555-123-4567"
{{$randomCity}}            → "New York"
{{$randomStreetAddress}}   → "123 Main St"
{{$randomCountry}}         → "United States"

{{$randomCompanyName}}     → "Acme Corp"
{{$randomJobTitle}}        → "Software Engineer"
{{$randomDepartment}}      → "Engineering"

{{$randomLoremWord}}       → "lorem"
{{$randomLoremWords}}      → "lorem ipsum dolor"
{{$randomLoremSentence}}   → "Lorem ipsum dolor sit amet."
{{$randomLoremParagraph}}  → Full paragraph

{{$randomBoolean}}     → true/false
{{$randomColor}}       → "red"
{{$randomHexColor}}    → "#ff5733"
```

### Custom Data Generation

**Unique Email:**
```javascript
const timestamp = Date.now();
const randomNum = Math.floor(Math.random() * 10000);
const email = `testuser_${timestamp}_${randomNum}@testmail.com`;
pm.environment.set("uniqueEmail", email);
```

**Random Number in Range:**
```javascript
function randomBetween(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

pm.environment.set("randomAge", randomBetween(18, 65));
pm.environment.set("randomPrice", (randomBetween(100, 10000) / 100).toFixed(2));
```

**Random Selection from Array:**
```javascript
const categories = ["electronics", "clothing", "books", "home", "sports"];
const randomCategory = categories[Math.floor(Math.random() * categories.length)];
pm.environment.set("productCategory", randomCategory);
```

**UUID Generation:**
```javascript
// Using built-in uuid module
const uuid = require('uuid');
pm.environment.set("uniqueId", uuid.v4());

// Or using pm.variables with built-in
pm.environment.set("requestId", pm.variables.replaceIn("{{$guid}}"));
```

**Date Manipulation:**
```javascript
// Current date in different formats
const now = new Date();

// ISO format
pm.environment.set("isoDate", now.toISOString());

// Custom format (YYYY-MM-DD)
const formatted = now.toISOString().split('T')[0];
pm.environment.set("dateOnly", formatted);

// Future date (7 days from now)
const futureDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);
pm.environment.set("futureDate", futureDate.toISOString());

// Past date (30 days ago)
const pastDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
pm.environment.set("pastDate", pastDate.toISOString());
```

## Conditional Request Logic

### Environment-Based Logic

```javascript
const env = pm.environment.get("env");

if (env === "prod") {
    // Production-specific setup
    pm.environment.set("timeout", "30000");
    pm.environment.set("retries", "3");
} else if (env === "staging") {
    // Staging setup
    pm.environment.set("timeout", "60000");
    pm.environment.set("retries", "5");
} else {
    // Development defaults
    pm.environment.set("timeout", "120000");
    pm.environment.set("retries", "10");
}
```

### Feature Flag Logic

```javascript
const features = {
    newUserFlow: true,
    betaApi: false,
    extendedLogging: pm.environment.get("env") !== "prod"
};

if (features.newUserFlow) {
    pm.environment.set("registrationEndpoint", "/api/v2/users/register");
} else {
    pm.environment.set("registrationEndpoint", "/api/v1/users/register");
}

if (features.extendedLogging) {
    pm.request.headers.add({ key: "X-Debug-Mode", value: "true" });
}
```

### Data-Driven Variations

```javascript
// Vary test data based on test type
const testType = pm.environment.get("testType") || "positive";

if (testType === "positive") {
    pm.environment.set("testEmail", "valid@example.com");
    pm.environment.set("testAge", "25");
} else if (testType === "negative") {
    pm.environment.set("testEmail", "invalid-email");
    pm.environment.set("testAge", "-5");
} else if (testType === "boundary") {
    pm.environment.set("testEmail", "a@b.co");  // Minimum valid
    pm.environment.set("testAge", "0");
}
```

## Common Pre-Request Patterns

### Pattern 1: Authentication Token Management

```javascript
// Check if token exists and is not expired
const token = pm.environment.get("accessToken");
const tokenExpiry = pm.environment.get("tokenExpiry");

if (!token || !tokenExpiry || Date.now() > tokenExpiry) {
    // Token missing or expired - need to refresh
    console.log("Token expired or missing, fetching new token...");
    
    const authRequest = {
        url: pm.environment.get("authUrl") + "/oauth/token",
        method: 'POST',
        header: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: {
            mode: 'urlencoded',
            urlencoded: [
                { key: 'grant_type', value: 'client_credentials' },
                { key: 'client_id', value: pm.environment.get("clientId") },
                { key: 'client_secret', value: pm.environment.get("clientSecret") }
            ]
        }
    };
    
    pm.sendRequest(authRequest, (err, response) => {
        if (err) {
            console.error("Auth request failed:", err);
            return;
        }
        
        const jsonResponse = response.json();
        pm.environment.set("accessToken", jsonResponse.access_token);
        
        // Set expiry (subtract 60 seconds for buffer)
        const expiresIn = jsonResponse.expires_in || 3600;
        pm.environment.set("tokenExpiry", Date.now() + (expiresIn - 60) * 1000);
        
        console.log("New token acquired");
    });
} else {
    console.log("Using existing valid token");
}
```

### Pattern 2: Request Signing

```javascript
// Sign request with HMAC
const CryptoJS = require('crypto-js');

const secretKey = pm.environment.get("apiSecret");
const timestamp = Date.now().toString();
const requestPath = pm.request.url.getPath();
const method = pm.request.method;

// Create signature string
const signatureString = `${method}|${requestPath}|${timestamp}`;

// Generate HMAC signature
const signature = CryptoJS.HmacSHA256(signatureString, secretKey).toString(CryptoJS.enc.Hex);

// Set headers
pm.environment.set("requestSignature", signature);
pm.environment.set("requestTimestamp", timestamp);

console.log("Request signed at:", timestamp);
```

### Pattern 3: Sequential ID Generation

```javascript
// Get current counter or initialize
let counter = parseInt(pm.collectionVariables.get("requestCounter") || "0");

// Increment
counter++;

// Set new value
pm.collectionVariables.set("requestCounter", counter.toString());

// Create sequential ID
const sequentialId = `TEST-${counter.toString().padStart(5, '0')}`;
pm.environment.set("testId", sequentialId);

console.log("Generated test ID:", sequentialId);
```

### Pattern 4: Dependency Resolution

```javascript
// Ensure required data exists before making request
const userId = pm.environment.get("userId");

if (!userId) {
    console.log("No userId found, creating user first...");
    
    const createUserRequest = {
        url: pm.environment.get("baseUrl") + "/api/users",
        method: 'POST',
        header: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + pm.environment.get("accessToken")
        },
        body: {
            mode: 'raw',
            raw: JSON.stringify({
                email: `testuser_${Date.now()}@example.com`,
                name: "Test User"
            })
        }
    };
    
    pm.sendRequest(createUserRequest, (err, response) => {
        if (err) {
            console.error("Failed to create user:", err);
            return;
        }
        
        const newUser = response.json();
        pm.environment.set("userId", newUser.id);
        console.log("Created user with ID:", newUser.id);
    });
}
```

### Pattern 5: Dynamic Headers

```javascript
// Add headers based on conditions
const headers = pm.request.headers;

// Add correlation ID for tracing
headers.add({ key: 'X-Correlation-ID', value: pm.variables.replaceIn('{{$guid}}') });

// Add timestamp
headers.add({ key: 'X-Request-Time', value: new Date().toISOString() });

// Conditional debug header
if (pm.environment.get("debugMode") === "true") {
    headers.add({ key: 'X-Debug', value: 'true' });
}

// Environment indicator
headers.add({ key: 'X-Environment', value: pm.environment.get("env") || "unknown" });
```

## Best Practices

### Do's

```javascript
// ✓ Use meaningful variable names
pm.environment.set("userAuthToken", token);

// ✓ Add console logging for debugging
console.log("Setting up request with userId:", userId);

// ✓ Handle errors gracefully
try {
    const data = JSON.parse(pm.environment.get("jsonData"));
} catch (e) {
    console.error("Failed to parse JSON:", e.message);
}

// ✓ Clean up temporary variables when done
pm.environment.unset("tempVariable");

// ✓ Use constants for repeated values
const API_VERSION = "v2";
const MAX_RETRIES = 3;
```

### Don'ts

```javascript
// ✗ Don't store sensitive data in logs
console.log("Password:", password); // NEVER DO THIS

// ✗ Don't make too many nested requests
pm.sendRequest(req1, () => {
    pm.sendRequest(req2, () => {
        pm.sendRequest(req3, () => {
            // This is a code smell
        });
    });
});

// ✗ Don't block on synchronous operations
// Pre-request scripts have execution time limits

// ✗ Don't hardcode environment-specific values
const url = "https://dev-api.example.com"; // Use variables instead
```

## Summary

- **Pre-request scripts** execute JavaScript before each request, enabling dynamic request configuration
- The **pm object** provides access to variables, request data, and utility functions
- **Dynamic variables** (`{{$guid}}`, `{{$randomEmail}}`, etc.) generate test data automatically
- **Custom scripts** can generate complex data, manage authentication, and implement conditional logic
- **pm.sendRequest()** enables fetching dependencies or tokens before the main request
- Following **best practices** ensures maintainable and debuggable scripts

Pre-request scripts transform Postman from a simple HTTP client into an intelligent test automation platform. Combined with the post-test scripts you'll learn next, you'll have complete control over your API testing workflow.

## Additional Resources

- [Postman Pre-request Scripts Documentation](https://learning.postman.com/docs/writing-scripts/pre-request-scripts/) - Official guide
- [Postman Sandbox API Reference](https://learning.postman.com/docs/writing-scripts/script-references/postman-sandbox-api-reference/) - Complete pm object reference
- [Dynamic Variables](https://learning.postman.com/docs/writing-scripts/script-references/variables-list/) - Full list of built-in dynamic variables

