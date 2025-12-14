# Lab: Collection Runner & Test Automation

## Overview

**Duration:** 45-60 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll use Postman's Collection Runner to execute your entire test suite as a batch. You'll learn to organize collections for automated execution, analyze results, and prepare for CI/CD integration.

---

## Learning Objectives

By completing this lab, you will:
- Execute collections using Collection Runner
- Organize tests for logical execution order
- Analyze batch test results
- Use data files for data-driven testing
- Export results and understand CI/CD readiness

---

## Prerequisites

- Completed all previous Monday exercises
- Collection with multiple requests and assertions
- At least one environment configured

---

## The Scenario

Your BookHaven test suite is ready for integration into the CI/CD pipeline. Before that happens, you need to validate that all tests can run as a batch, in the correct order, and produce actionable results. Today you'll configure and execute your first automated test run.

---

## Core Tasks

### Task 1: Organize Your Collection (15 minutes)

Structure your collection for automated execution:

**Create this folder structure:**

```
📁 BookHaven API Tests
├── 📁 1. Setup
│   └── Health Check
├── 📁 2. User Operations
│   ├── GET All Users
│   ├── GET User by ID
│   ├── CREATE User
│   └── UPDATE User
├── 📁 3. Post Operations
│   ├── GET All Posts
│   ├── GET Post by ID
│   ├── GET Posts by User
│   ├── CREATE Post
│   ├── UPDATE Post
│   └── DELETE Post
└── 📁 4. Cleanup
    └── Verify Deletion
```

**To create folders:**
1. Right-click collection → "Add folder"
2. Drag and drop requests into folders
3. Reorder folders by dragging

**Naming convention:**
- Prefix folders with numbers for execution order
- Use action-first naming (GET, CREATE, UPDATE, DELETE)

### Task 2: Add Health Check Request (10 minutes)

Create a request to verify API availability:

**Request: Health Check**
- Method: GET
- URL: `{{baseUrl}}/posts/1`
- Folder: 1. Setup

**Tests:**
```javascript
pm.test("API is available", function() {
    pm.response.to.have.status(200);
});

pm.test("Response time is acceptable", function() {
    pm.expect(pm.response.responseTime).to.be.below(5000);
});

pm.test("Response is valid JSON", function() {
    pm.response.to.be.json;
});

// Set flag for subsequent tests
pm.collectionVariables.set("apiAvailable", "true");
console.log("✅ API Health Check Passed");
```

### Task 3: Add Dependency Checks (10 minutes)

Add pre-request scripts to verify dependencies:

**In each request that depends on previous data:**

```javascript
// Check if API is available
const apiAvailable = pm.collectionVariables.get("apiAvailable");
if (apiAvailable !== "true") {
    console.warn("⚠️ API health check not passed - this test may fail");
}

// Check for required variables (for requests needing created IDs)
const userId = pm.environment.get("createdUserId");
if (!userId) {
    console.warn("⚠️ No user ID found - Create User should run first");
}
```

### Task 4: Run Collection with Collection Runner (15 minutes)

1. Click on your collection name
2. Click **"Run"** button (or right-click → "Run collection")
3. **Configure the run:**

| Setting | Value |
|---------|-------|
| Environment | BookHaven - Development |
| Iterations | 1 |
| Delay | 100 ms |
| Save responses | ✓ (check) |
| Keep variable values | ✓ (check) |

4. Click **"Run BookHaven API Tests"**

**Observe the execution:**
- Watch requests execute in order
- Note pass/fail status for each test
- Check console for any warnings

### Task 5: Analyze Results (10 minutes)

After the run completes, examine:

**Summary View:**
- Total tests passed/failed
- Total requests executed
- Average response time
- Any skipped tests

**Individual Request Results:**
- Click on any request to see detailed results
- View response body, headers, test results
- Check console output

**Document your findings:**

| Metric | Value |
|--------|-------|
| Total Requests | ___ |
| Total Tests | ___ |
| Passed | ___ |
| Failed | ___ |
| Average Response Time | ___ ms |
| Slowest Request | ___ |

### Task 6: Data-Driven Testing (15 minutes)

Create a data file for multiple test iterations:

**Create file: `test-data.json`**
```json
[
    {
        "userId": 1,
        "expectedPostCount": 10,
        "description": "User 1 posts"
    },
    {
        "userId": 2,
        "expectedPostCount": 10,
        "description": "User 2 posts"
    },
    {
        "userId": 3,
        "expectedPostCount": 10,
        "description": "User 3 posts"
    }
]
```

**Create a data-driven request:**
- Method: GET
- URL: `{{baseUrl}}/posts?userId={{userId}}`
- Name: GET Posts by User (Data-Driven)

**Tests:**
```javascript
pm.test("Test: " + pm.iterationData.get("description"), function() {
    const posts = pm.response.json();
    const expectedCount = pm.iterationData.get("expectedPostCount");
    
    pm.expect(posts).to.be.an("array");
    pm.expect(posts.length).to.equal(expectedCount);
});
```

**Run with data file:**
1. Open Collection Runner
2. Click "Select File" next to "Data"
3. Choose your `test-data.json`
4. Set Iterations to match data rows (3)
5. Run

### Task 7: Export Run Results (5 minutes)

After a successful run:

1. Click **"Export Results"** button
2. Save as JSON file
3. Review the structure:

```json
{
    "run": {
        "stats": {
            "iterations": { "total": 1, "pending": 0, "failed": 0 },
            "requests": { "total": 10, "pending": 0, "failed": 0 },
            "assertions": { "total": 25, "pending": 0, "failed": 0 }
        },
        "executions": [
            {
                "item": { "name": "GET All Posts" },
                "response": { "code": 200 },
                "assertions": [
                    { "assertion": "Status is 200", "skipped": false }
                ]
            }
        ]
    }
}
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Collection organized with numbered folders
- [ ] Health check request at the beginning
- [ ] Dependency checks in relevant requests
- [ ] Successfully run entire collection
- [ ] Documented run results (pass/fail counts)
- [ ] Created and used a data file for data-driven tests
- [ ] Exported run results to JSON

---

## Challenge Tasks (Optional)

### 1. Create Run Summary Script

Add to the last request in your collection:

```javascript
// Collect run statistics
const stats = {
    totalRequests: pm.info.iteration + 1,
    environment: pm.environment.name,
    timestamp: new Date().toISOString(),
    createdResources: {
        userId: pm.environment.get("createdUserId"),
        postId: pm.environment.get("createdPostId")
    }
};

console.log("========== RUN SUMMARY ==========");
console.log(JSON.stringify(stats, null, 2));
console.log("=================================");
```

### 2. Conditional Execution

```javascript
// In pre-request script - skip if previous test failed
const shouldSkip = pm.collectionVariables.get("criticalFailure") === "true";
if (shouldSkip) {
    console.log("⏭️ Skipping due to critical failure");
    // Note: Postman doesn't truly skip, but you can handle in tests
}

// In tests - mark critical failure
pm.test("Critical test", function() {
    try {
        pm.response.to.have.status(201);
    } catch(e) {
        pm.collectionVariables.set("criticalFailure", "true");
        throw e;
    }
});
```

### 3. Newman CLI Preview

Export your collection and environment:
1. Right-click collection → Export (Collection v2.1)
2. Right-click environment → Export

**Newman command (preview for Week 8):**
```bash
newman run collection.json -e environment.json --reporters cli,json
```

---

## Submission Checklist

| Item | Completed |
|------|-----------|
| Collection folders created (4 minimum) | ☐ |
| Health check request working | ☐ |
| Requests ordered correctly | ☐ |
| Collection Runner executed successfully | ☐ |
| All tests passing (or documented failures) | ☐ |
| Results documented | ☐ |
| Data-driven test implemented | ☐ |
| Results exported to JSON | ☐ |

---

## Collection Runner Best Practices

```
✓ DO:
  - Start with health/setup requests
  - Order requests by dependency
  - Use delays between requests (100-500ms)
  - Save responses for debugging
  - Export results for documentation

✗ DON'T:
  - Run without selecting an environment
  - Skip the delay (can cause rate limiting)
  - Ignore failed tests
  - Run against production without caution
```

---

## Results Documentation Template

```markdown
## Test Run Report
**Date:** _______________
**Environment:** _______________
**Collection:** BookHaven API Tests

### Summary
- Total Requests: ___
- Total Assertions: ___
- Passed: ___
- Failed: ___
- Duration: ___ seconds

### Failures (if any)
| Request | Test Name | Error |
|---------|-----------|-------|
| | | |

### Notes
- 
```

---

## Common Mistakes

1. **No environment selected:** Always verify environment before running
2. **Wrong execution order:** Use numbered folders for control
3. **Missing dependencies:** Tests fail because data from previous requests is missing
4. **Too fast execution:** Add delay to avoid rate limiting
5. **Not saving responses:** Makes debugging failed tests harder

---

## Additional Resources

- [Collection Runner Documentation](https://learning.postman.com/docs/running-collections/intro-to-collection-runs/)
- [Data Files in Postman](https://learning.postman.com/docs/running-collections/working-with-data-files/)
- [Newman CLI](https://learning.postman.com/docs/running-collections/using-newman-cli/command-line-integration-with-newman/)

