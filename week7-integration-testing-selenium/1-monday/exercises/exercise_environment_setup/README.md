# Lab: Environment Configuration in Postman

## Overview

**Duration:** 45-60 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll configure Postman environments to run the same tests against different API instances. You'll learn to manage environment variables, switch between contexts, and handle sensitive data securely.

---

## Learning Objectives

By completing this lab, you will:
- Create and configure multiple Postman environments
- Understand variable scopes and priority
- Switch between environments seamlessly
- Manage sensitive data with current values
- Export and import environments for team sharing

---

## Prerequisites

- Completed previous Postman exercises
- Understanding of Postman variables
- API testing collection ready

---

## The Scenario

BookHaven has multiple environments: Development, Staging, and Production. Each has different URLs, API keys, and configurations. You need to configure your test collection to run against any environment by simply switching a dropdown.

---

## Core Tasks

### Task 1: Create Development Environment (10 minutes)

1. Click **Environments** in the left sidebar
2. Click **"+"** to create a new environment
3. Name it: **"BookHaven - Development"**

**Add these variables:**

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `baseUrl` | `https://jsonplaceholder.typicode.com` | `https://jsonplaceholder.typicode.com` |
| `apiVersion` | `v1` | `v1` |
| `timeout` | `60000` | `60000` |
| `debugMode` | `true` | `true` |
| `apiKey` | _(leave empty)_ | `dev-api-key-12345` |
| `testUserEmail` | `devtest@bookhaven.test` | `devtest@bookhaven.test` |

**Important:** Notice the `apiKey` has empty Initial Value but has Current Value. This keeps secrets out of exports!

### Task 2: Create Staging Environment (10 minutes)

Create another environment: **"BookHaven - Staging"**

**Add these variables:**

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `baseUrl` | `https://jsonplaceholder.typicode.com` | `https://jsonplaceholder.typicode.com` |
| `apiVersion` | `v2` | `v2` |
| `timeout` | `30000` | `30000` |
| `debugMode` | `true` | `true` |
| `apiKey` | _(leave empty)_ | `stg-api-key-67890` |
| `testUserEmail` | `stagingtest@bookhaven.test` | `stagingtest@bookhaven.test` |

### Task 3: Create Production Environment (10 minutes)

Create: **"BookHaven - Production"**

**Add these variables:**

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `baseUrl` | `https://jsonplaceholder.typicode.com` | `https://jsonplaceholder.typicode.com` |
| `apiVersion` | `v1` | `v1` |
| `timeout` | `10000` | `10000` |
| `debugMode` | `false` | `false` |
| `apiKey` | _(leave empty)_ | `prod-api-key-XXXXX` |
| `testUserEmail` | _(leave empty)_ | _(leave empty - no test data in prod!)_ |

### Task 4: Update Collection to Use Variables (15 minutes)

Modify your existing requests to use environment variables:

**Update Request URLs:**
```
Before: https://jsonplaceholder.typicode.com/posts
After:  {{baseUrl}}/posts
```

**Add Dynamic Headers:**
In the Headers tab of your requests:
| Key | Value |
|-----|-------|
| `X-API-Key` | `{{apiKey}}` |
| `X-Request-Timeout` | `{{timeout}}` |
| `X-Debug-Mode` | `{{debugMode}}` |

**Update Pre-request Scripts:**
```javascript
// Log current environment configuration
console.log("=== Environment Configuration ===");
console.log("Base URL:", pm.environment.get("baseUrl"));
console.log("API Version:", pm.environment.get("apiVersion"));
console.log("Debug Mode:", pm.environment.get("debugMode"));
console.log("================================");
```

**Update Test Scripts:**
```javascript
// Conditional assertions based on environment
const debugMode = pm.environment.get("debugMode") === "true";

pm.test("Response time within SLA", function() {
    const timeout = parseInt(pm.environment.get("timeout"));
    pm.expect(pm.response.responseTime).to.be.below(timeout);
});

if (debugMode) {
    console.log("Full response:", pm.response.json());
}
```

### Task 5: Test Environment Switching (10 minutes)

1. Select **"BookHaven - Development"** from the environment dropdown (top-right)
2. Run a request and verify:
   - Console shows "Development" configuration
   - Timeout check uses 60000ms threshold
   - Debug logging is enabled

3. Switch to **"BookHaven - Staging"**
4. Run the same request and verify:
   - API version shows "v2"
   - Timeout is 30000ms
   - Different API key is used

5. Switch to **"BookHaven - Production"**
6. Run the request and verify:
   - Debug mode is false (no extra logging)
   - Timeout is 10000ms
   - No test user email available

### Task 6: Environment-Aware Tests (10 minutes)

Add conditional test logic:

```javascript
// Get environment name from URL
const baseUrl = pm.environment.get("baseUrl");
const isProd = baseUrl && baseUrl.includes("prod");

pm.test("Environment-appropriate response", function() {
    if (isProd) {
        // Stricter checks for production
        pm.expect(pm.response.responseTime).to.be.below(5000);
    } else {
        // More lenient for dev/staging
        pm.expect(pm.response.responseTime).to.be.below(30000);
    }
});

// Skip destructive tests in production
if (!isProd) {
    pm.test("Can run write operations", function() {
        // This test only runs in non-production
        pm.expect(true).to.be.true;
    });
}
```

### Task 7: Create Environment Validation Script (10 minutes)

Add to collection-level pre-request script:

```javascript
// Validate required environment variables
const requiredVars = ["baseUrl", "apiVersion", "apiKey"];
const missingVars = [];

requiredVars.forEach(varName => {
    const value = pm.environment.get(varName);
    if (!value) {
        missingVars.push(varName);
    }
});

if (missingVars.length > 0) {
    console.error("Missing required environment variables:", missingVars.join(", "));
    console.error("Please select an environment and configure: " + missingVars.join(", "));
}

// Log active environment indicator
const envName = pm.environment.name || "No Environment Selected";
console.log(`\n🌍 Active Environment: ${envName}\n`);
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Created 3 environments (Dev, Staging, Prod)
- [ ] All requests use `{{baseUrl}}` instead of hardcoded URLs
- [ ] API keys stored in Current Value only (not Initial)
- [ ] Successfully run tests in all 3 environments
- [ ] Conditional logic adjusts behavior per environment
- [ ] Environment validation script warns about missing variables
- [ ] Quick Look (eye icon) shows correct values for each environment

---

## Challenge Tasks (Optional)

### 1. Export and Re-import Environment

1. Export "Development" environment to JSON
2. Review the exported file - notice what's included/excluded
3. Delete the environment
4. Re-import from the JSON file
5. Verify the Current Values need to be re-entered (security feature!)

### 2. Global Variables for Cross-Environment Data

Create Global variables for constants:
```javascript
// In any pre-request script
pm.globals.set("appName", "BookHaven");
pm.globals.set("maxPageSize", "100");
pm.globals.set("supportedFormats", "json,xml");
```

### 3. Collection Variables for Test Suite Data

```javascript
// In collection pre-request script
pm.collectionVariables.set("testRunId", Date.now().toString());
pm.collectionVariables.set("testSuiteName", "API Regression");
```

---

## Variable Scope Quick Reference

| Scope | Persist? | Access | Use Case |
|-------|----------|--------|----------|
| **Global** | Yes | All collections | Universal constants |
| **Collection** | Yes | Single collection | Test suite config |
| **Environment** | Yes | When selected | Environment-specific |
| **Local** | No | Single request | Temporary data |

**Priority (highest to lowest):** Local → Data → Environment → Collection → Global

---

## Submission Checklist

| Item | Completed |
|------|-----------|
| Development environment created | ☐ |
| Staging environment created | ☐ |
| Production environment created | ☐ |
| API keys in Current Value only | ☐ |
| Requests use `{{baseUrl}}` | ☐ |
| Environment switch test passed | ☐ |
| Conditional tests implemented | ☐ |
| Validation script added | ☐ |
| Quick Look shows correct values | ☐ |

---

## Common Mistakes

1. **Storing secrets in Initial Value:** Secrets should only be in Current Value
2. **Hardcoded URLs remaining:** Search and replace all hardcoded URLs
3. **Variable name typos:** `{{baseUrl}}` vs `{{baseURL}}` - case matters!
4. **Forgetting to select environment:** Many issues caused by "No Environment"
5. **Assuming variables exist:** Always check with `pm.environment.get()` before using

---

## Security Best Practices

```
✓ DO:
  - Use Current Value for secrets
  - Create template environments with placeholders
  - Validate required variables exist
  - Use environment.get() with fallback values

✗ DON'T:
  - Store passwords/keys in Initial Value
  - Commit exported environments with secrets
  - Log sensitive values to console
  - Share environments without reviewing content
```

---

## Additional Resources

- Written Content: `environments.md`
- [Managing Environments](https://learning.postman.com/docs/sending-requests/managing-environments/)
- [Variable Scopes](https://learning.postman.com/docs/sending-requests/variables/)

