# Postman Environments

## Learning Objectives
- Understand the purpose and power of Postman environments
- Create and manage multiple environments effectively
- Distinguish between environment variables and global variables
- Switch between environments for different testing contexts
- Export and share environments with team members

## Why This Matters

As we progress through our **"From API to UI: Mastering Full-Stack Test Automation"** journey, you'll quickly discover that real-world APIs don't exist in a single instance. Development, staging, production—each environment has different URLs, credentials, and configurations. Without proper environment management, you'd spend countless hours duplicating requests and manually updating values.

Environments are Postman's solution to this challenge. They allow you to write a test once and run it anywhere. A single collection can validate your API across all environments with just a dropdown change. This capability is essential for continuous integration, regression testing, and confident deployments.

## Understanding Environments

### What Are Environments?

An environment is a set of key-value pairs (variables) that represent a specific context for your API requests. Think of it as a configuration profile that can be swapped instantly.

### The Environment Concept

```
┌─────────────────────────────────────────────────────────────┐
│                     Same Request                             │
│         GET {{baseUrl}}/api/{{version}}/users               │
└─────────────────────────────────────────────────────────────┘
                            │
          ┌─────────────────┼─────────────────┐
          ↓                 ↓                 ↓
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│   Development   │ │     Staging     │ │   Production    │
├─────────────────┤ ├─────────────────┤ ├─────────────────┤
│baseUrl: dev-api │ │baseUrl: stg-api │ │baseUrl: api     │
│version: v2      │ │version: v2      │ │version: v1      │
│apiKey: dev-key  │ │apiKey: stg-key  │ │apiKey: prod-key │
│timeout: 60000   │ │timeout: 30000   │ │timeout: 10000   │
└─────────────────┘ └─────────────────┘ └─────────────────┘
```

### Benefits of Using Environments

| Benefit | Description |
|---------|-------------|
| **Reusability** | Write once, run anywhere |
| **Consistency** | Same tests across all environments |
| **Security** | Keep credentials separate from requests |
| **Collaboration** | Share configurations with team |
| **Speed** | Switch contexts in one click |
| **Maintenance** | Update values in one place |

## Creating and Managing Environments

### Creating a New Environment

**Method 1: From Sidebar**
1. Click "Environments" in the left sidebar
2. Click the "+" button or "Create Environment"
3. Name your environment (e.g., "Development")
4. Add variables with initial and current values
5. Click "Save"

**Method 2: Quick Add**
1. Click the environment dropdown (top-right)
2. Select "Create Environment"
3. Configure and save

### Environment Structure

Each environment contains variables with three components:

| Component | Description | Visibility |
|-----------|-------------|------------|
| **Variable** | The key name (e.g., `baseUrl`) | Visible |
| **Initial Value** | Shared value (synced with team) | Shared |
| **Current Value** | Local value (your machine only) | Private |

```
Variable: apiKey
Initial Value: (leave empty for secrets)
Current Value: sk-abc123xyz789
```

### Example Environment Setup

**Development Environment:**
```
Name: Development

| Variable        | Initial Value                    | Current Value              |
|-----------------|----------------------------------|----------------------------|
| baseUrl         | https://dev-api.example.com      | https://dev-api.example.com|
| apiVersion      | v2                               | v2                         |
| apiKey          |                                  | dev-secret-key-123         |
| timeout         | 60000                            | 60000                      |
| testUserEmail   | devtest@example.com              | devtest@example.com        |
| debugMode       | true                             | true                       |
```

**Production Environment:**
```
Name: Production

| Variable        | Initial Value                    | Current Value              |
|-----------------|----------------------------------|----------------------------|
| baseUrl         | https://api.example.com          | https://api.example.com    |
| apiVersion      | v1                               | v1                         |
| apiKey          |                                  | prod-secret-key-456        |
| timeout         | 10000                            | 10000                      |
| testUserEmail   |                                  |                            |
| debugMode       | false                            | false                      |
```

### Best Practices for Variable Naming

```
✓ Good Naming:
  - baseUrl
  - apiKey
  - accessToken
  - userId
  - testUserEmail
  - requestTimeout

✗ Avoid:
  - url (too generic)
  - key (ambiguous)
  - x (meaningless)
  - my_variable (inconsistent style)
```

### Variable Types

**Configuration Variables:**
```
baseUrl: https://api.example.com
apiVersion: v2
timeout: 30000
retryCount: 3
```

**Authentication Variables:**
```
apiKey: (sensitive - use current value only)
accessToken: (sensitive)
refreshToken: (sensitive)
clientId: public-client-id
clientSecret: (sensitive)
```

**Test Data Variables:**
```
testUserId: 12345
testProductId: prod-001
testOrderId: order-789
```

**Dynamic Variables (set by scripts):**
```
lastCreatedId: (set by pre/post scripts)
sessionToken: (set by auth flow)
requestTimestamp: (set per request)
```

## Environment Variables vs Global Variables

### Scope Comparison

```
┌─────────────────────────────────────────────────────────────┐
│                    Variable Scopes                           │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                 GLOBAL VARIABLES                     │    │
│  │  Available everywhere, regardless of environment    │    │
│  │  Example: companyName, standardTimeout             │    │
│  │  ┌─────────────────────────────────────────────┐    │    │
│  │  │          ENVIRONMENT VARIABLES               │    │    │
│  │  │  Specific to selected environment           │    │    │
│  │  │  Example: baseUrl, apiKey                   │    │    │
│  │  │  ┌─────────────────────────────────────┐    │    │    │
│  │  │  │      COLLECTION VARIABLES           │    │    │    │
│  │  │  │  Specific to collection             │    │    │    │
│  │  │  │  ┌─────────────────────────────┐    │    │    │    │
│  │  │  │  │     LOCAL VARIABLES         │    │    │    │    │
│  │  │  │  │  Request/script scope only  │    │    │    │    │
│  │  │  │  └─────────────────────────────┘    │    │    │    │
│  │  │  └─────────────────────────────────────┘    │    │    │
│  │  └─────────────────────────────────────────────┘    │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### When to Use Each

**Global Variables - Use for:**
```javascript
// Constants that never change between environments
pm.globals.set("appName", "BookStore API");
pm.globals.set("apiVersion", "v2");
pm.globals.set("maxPageSize", 100);
pm.globals.set("supportEmail", "support@example.com");
```

**Environment Variables - Use for:**
```javascript
// Values that differ by environment
pm.environment.set("baseUrl", "https://dev-api.example.com");
pm.environment.set("apiKey", "dev-key-123");
pm.environment.set("databaseName", "bookstore_dev");
pm.environment.set("logLevel", "debug");
```

**Collection Variables - Use for:**
```javascript
// Values specific to a test collection
pm.collectionVariables.set("testSuiteId", "regression-v1");
pm.collectionVariables.set("currentTestUser", "user123");
pm.collectionVariables.set("testStartTime", new Date().toISOString());
```

### Variable Resolution Order

When Postman encounters `{{variableName}}`, it searches in this order:

```
1. Local (pm.variables)        ← Highest priority
2. Data (from CSV/JSON file)
3. Environment (pm.environment)
4. Collection (pm.collectionVariables)
5. Global (pm.globals)         ← Lowest priority
```

**Example Resolution:**
```
Request: GET {{baseUrl}}/users

If:
- Global: baseUrl = "https://global.example.com"
- Collection: baseUrl = "https://collection.example.com"
- Environment: baseUrl = "https://env.example.com"

Result: https://env.example.com/users (environment wins)
```

### Comparison Table

| Aspect | Global | Environment | Collection |
|--------|--------|-------------|------------|
| **Scope** | All workspaces | Selected environment | Single collection |
| **Persistence** | Permanent | Per environment | With collection |
| **Sharing** | Workspace-wide | Export/import | With collection |
| **Use Case** | Universal constants | Environment-specific | Test suite data |
| **Security** | Less secure | Moderate | With collection |

## Switching Between Environments

### Using the Environment Dropdown

Located in the top-right corner of Postman:

```
┌─────────────────────────────────────────┐
│ [No Environment     ▼]  [👁]  [⚙]       │
└─────────────────────────────────────────┘

Clicking dropdown shows:
┌─────────────────────────────────────────┐
│ No Environment                           │
│ ─────────────────────────               │
│ ● Development                           │
│   Staging                               │
│   Production                            │
│ ─────────────────────────               │
│ + Create Environment                     │
└─────────────────────────────────────────┘
```

### Quick Look Feature

Click the eye icon (👁) to see current environment values:

```
┌─────────────────────────────────────────┐
│ Environment: Development                 │
├─────────────────────────────────────────┤
│ baseUrl     https://dev-api.example.com │
│ apiKey      ••••••••••                   │
│ apiVersion  v2                           │
│ timeout     60000                        │
└─────────────────────────────────────────┘
```

### Switching in Scripts

```javascript
// You cannot programmatically switch environments
// But you can conditionally set values based on a variable

const env = pm.environment.get("environmentName");

if (env === "production") {
    // Extra caution for production
    pm.environment.set("shouldRunDestructiveTests", "false");
}
```

### Testing Across Environments

**Collection Runner Workflow:**
1. Select your collection
2. Click "Run"
3. Choose first environment (e.g., Development)
4. Run tests
5. Select next environment (e.g., Staging)
6. Run tests again
7. Compare results

## Exporting and Sharing Environments

### Exporting an Environment

1. Click "Environments" in sidebar
2. Click the three dots (...) next to environment name
3. Select "Export"
4. Save the JSON file

### Exported File Structure

```json
{
    "id": "abc123-def456",
    "name": "Development",
    "values": [
        {
            "key": "baseUrl",
            "value": "https://dev-api.example.com",
            "type": "default",
            "enabled": true
        },
        {
            "key": "apiKey",
            "value": "",
            "type": "secret",
            "enabled": true
        },
        {
            "key": "timeout",
            "value": "60000",
            "type": "default",
            "enabled": true
        }
    ],
    "_postman_variable_scope": "environment"
}
```

### Importing an Environment

1. Click "Import" button (top-left)
2. Drag and drop JSON file, or click to browse
3. Review import preview
4. Click "Import"

### Sharing Best Practices

**DO Share:**
```
✓ Base URLs
✓ API version numbers
✓ Timeout configurations
✓ Feature flags
✓ Test data identifiers
```

**DON'T Share (use current value only):**
```
✗ API keys
✗ Passwords
✗ Access tokens
✗ Personal test accounts
✗ Sensitive configuration
```

### Team Environment Template

Create a template with placeholder values:

```json
{
    "name": "Development (Template)",
    "values": [
        {
            "key": "baseUrl",
            "value": "https://dev-api.example.com",
            "enabled": true
        },
        {
            "key": "apiKey",
            "value": "YOUR_API_KEY_HERE",
            "enabled": true
        },
        {
            "key": "testUserEmail",
            "value": "YOUR_EMAIL_HERE",
            "enabled": true
        }
    ]
}
```

Team members import and update current values locally.

## Practical Environment Patterns

### Pattern 1: Multi-Region Setup

```
Environments:
├── US-East-Dev
│   └── baseUrl: https://us-east-dev.api.example.com
├── US-East-Prod
│   └── baseUrl: https://us-east.api.example.com
├── EU-West-Dev
│   └── baseUrl: https://eu-west-dev.api.example.com
└── EU-West-Prod
    └── baseUrl: https://eu-west.api.example.com
```

### Pattern 2: Feature Branch Testing

```
Environments:
├── Main (Staging)
│   └── baseUrl: https://staging.api.example.com
├── Feature-Auth-Refactor
│   └── baseUrl: https://feature-auth.dev.example.com
└── Feature-New-Checkout
    └── baseUrl: https://feature-checkout.dev.example.com
```

### Pattern 3: Client-Specific Testing

```
Environments:
├── Client-A-Sandbox
│   ├── baseUrl: https://sandbox.api.example.com
│   ├── clientId: client-a-sandbox-id
│   └── clientSecret: (current value only)
├── Client-A-Production
│   ├── baseUrl: https://api.example.com
│   ├── clientId: client-a-prod-id
│   └── clientSecret: (current value only)
└── Client-B-Sandbox
    ├── baseUrl: https://sandbox.api.example.com
    ├── clientId: client-b-sandbox-id
    └── clientSecret: (current value only)
```

### Pattern 4: Test Data Isolation

```
Development Environment:
├── Configuration
│   ├── baseUrl: https://dev-api.example.com
│   └── apiKey: dev-key
├── Test Users
│   ├── adminUserId: dev-admin-001
│   ├── regularUserId: dev-user-001
│   └── guestUserId: dev-guest-001
└── Test Products
    ├── validProductId: dev-prod-001
    ├── outOfStockProductId: dev-prod-oos
    └── discontinuedProductId: dev-prod-disc
```

## Environment Variables in Scripts

### Reading Environment Variables

```javascript
// In pre-request or test scripts
const baseUrl = pm.environment.get("baseUrl");
const apiKey = pm.environment.get("apiKey");
const timeout = parseInt(pm.environment.get("timeout") || "30000");

console.log(`Testing against: ${baseUrl}`);
```

### Writing Environment Variables

```javascript
// Store response data for later requests
pm.test("Store token from response", function() {
    const response = pm.response.json();
    
    pm.environment.set("accessToken", response.token);
    pm.environment.set("tokenExpiry", response.expiresAt);
    pm.environment.set("refreshToken", response.refreshToken);
});
```

### Checking Environment

```javascript
// Conditional logic based on environment
const baseUrl = pm.environment.get("baseUrl");

if (baseUrl && baseUrl.includes("prod")) {
    console.warn("Running against PRODUCTION!");
    // Skip destructive tests
    pm.environment.set("skipDestructiveTests", "true");
}

// Check if required variables exist
const requiredVars = ["baseUrl", "apiKey", "apiVersion"];
requiredVars.forEach(varName => {
    if (!pm.environment.get(varName)) {
        console.error(`Missing required variable: ${varName}`);
    }
});
```

### Cleaning Up Variables

```javascript
// Clear sensitive data after test run
pm.test("Cleanup sensitive data", function() {
    pm.environment.unset("tempPassword");
    pm.environment.unset("oneTimeCode");
    
    // Or clear all dynamic variables
    const dynamicVars = ["lastCreatedId", "sessionToken", "tempData"];
    dynamicVars.forEach(v => pm.environment.unset(v));
});
```

## Troubleshooting Environments

### Common Issues

**Variable Not Resolving:**
```
Problem: {{baseUrl}} shows as literal text
Solutions:
1. Check environment is selected (not "No Environment")
2. Verify variable name matches exactly (case-sensitive)
3. Check variable is enabled (checkbox)
4. Look for typos in variable name
```

**Wrong Value Used:**
```
Problem: Getting unexpected value
Solutions:
1. Check current value vs initial value
2. Check variable scope priority
3. Look for same variable in multiple scopes
4. Use Quick Look (eye icon) to verify values
```

**Sensitive Data Exposed:**
```
Problem: API key visible in shared environment
Solutions:
1. Clear initial value, use only current value
2. Mark as "secret" type in newer Postman
3. Use environment templates with placeholders
4. Never commit environment files to version control
```

### Debug Techniques

```javascript
// Log all environment variables
pm.test("Debug: Log environment", function() {
    const allVars = pm.environment.toObject();
    console.log("Environment variables:", JSON.stringify(allVars, null, 2));
});

// Check variable resolution
pm.test("Debug: Check variable", function() {
    console.log("baseUrl from environment:", pm.environment.get("baseUrl"));
    console.log("baseUrl resolved:", pm.variables.get("baseUrl"));
    console.log("Request URL:", pm.request.url.toString());
});
```

## Summary

- **Environments** are configuration profiles containing key-value pairs for different testing contexts
- **Initial vs Current values** allow sharing configurations while keeping secrets private
- **Environment variables** are scoped to the selected environment; **global variables** are available everywhere
- **Variable resolution** follows a priority order: Local > Data > Environment > Collection > Global
- **Switching environments** instantly changes all variable values across your requests
- **Exporting/importing** enables team collaboration while protecting sensitive data
- **Proper environment design** is essential for scalable, maintainable API testing

With environments mastered, you can now run your Postman tests against development, staging, and production APIs with confidence. Tomorrow, you'll learn to take API testing to the next level with REST Assured for programmatic Java testing and Python's Requests module.

## Additional Resources

- [Postman Environments Documentation](https://learning.postman.com/docs/sending-requests/managing-environments/) - Official guide
- [Variable Scopes](https://learning.postman.com/docs/sending-requests/variables/) - Complete variable reference
- [Postman Security Best Practices](https://learning.postman.com/docs/sending-requests/authorization/) - Protecting sensitive data

