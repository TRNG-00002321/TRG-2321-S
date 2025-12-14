# Typical API Testing Defects

## Learning Objectives
- Identify common defects discovered through API testing
- Understand the root causes and impacts of each defect type
- Recognize patterns that indicate potential API issues
- Learn how to design tests that uncover these defects effectively

## Why This Matters

In our journey **"From API to UI: Mastering Full-Stack Test Automation"**, knowing what to look for is just as important as knowing how to test. APIs are notorious for hiding subtle bugs that can cause cascading failures throughout an application. A skilled QA engineer doesn't just run tests—they anticipate where problems lurk.

Understanding typical API defects transforms you from a test executor into a defect hunter. When you know the common failure patterns, you can design targeted test cases that expose vulnerabilities before they reach production. This proactive approach saves organizations countless hours of debugging and prevents customer-facing incidents.

## Status Code Mismatches

### What It Is

Status code mismatches occur when the API returns an incorrect HTTP status code for a given situation. The response body might be correct, but the status code tells a different story.

### Common Examples

| Scenario | Expected | Actual (Defect) |
|----------|----------|-----------------|
| Resource created successfully | 201 Created | 200 OK |
| Resource not found | 404 Not Found | 200 OK with empty body |
| Unauthorized access | 401 Unauthorized | 403 Forbidden |
| Validation error | 400 Bad Request | 500 Internal Server Error |
| Resource deleted | 204 No Content | 200 OK |

### Real-World Impact

```json
// Client code expecting correct status codes
if (response.statusCode === 201) {
    showSuccessMessage("User created!");
    redirectToUserProfile(response.body.id);
} else if (response.statusCode === 400) {
    showValidationErrors(response.body.errors);
}

// If API returns 200 instead of 201 or 400, client logic breaks
```

### How to Test For It

```
Test Case: Verify correct status code for resource creation
Given a valid user payload
When POST /api/users is called
Then status code should be 201 (not 200)
And response should contain "Location" header

Test Case: Verify 404 for non-existent resource
Given a resource ID that doesn't exist
When GET /api/users/999999 is called
Then status code should be 404
And response body should contain error message
```

## Data Validation Errors

### What It Is

Data validation errors occur when the API accepts invalid data that should be rejected, or rejects valid data that should be accepted.

### Common Categories

**Missing Validation:**
- Accepting empty required fields
- Allowing strings in numeric fields
- No length limits on text fields
- Invalid email formats accepted
- Future dates accepted for birth dates

**Over-Validation:**
- Rejecting valid international phone numbers
- Blocking legitimate special characters in names (O'Brien, García)
- Overly restrictive password rules
- Rejecting valid edge-case inputs

### Example Defects

```json
// Defect: API accepts invalid email
POST /api/users
{
    "name": "John",
    "email": "not-an-email",      // Should be rejected
    "age": -5                      // Should be rejected
}

Response: 201 Created  // BUG! Should be 400 Bad Request

// Defect: API rejects valid input
POST /api/users
{
    "name": "Mary O'Connor",       // Apostrophe is valid
    "email": "mary@example.co.uk"  // .co.uk is valid
}

Response: 400 Bad Request
{
    "error": "Invalid characters in name"  // BUG!
}
```

### Testing Strategy

```
Boundary Testing:
├── Minimum length (0, 1 character)
├── Maximum length (limit, limit+1)
├── Numeric boundaries (MIN_INT, MAX_INT)
└── Date boundaries (past, present, future)

Format Testing:
├── Valid formats (standard cases)
├── Invalid formats (obviously wrong)
├── Edge cases (unusual but valid)
└── Unicode/special characters

Required Field Testing:
├── All required fields present
├── Each required field missing individually
├── All required fields missing
└── Required fields as empty strings vs null
```

## Authentication Failures

### What It Is

Authentication defects involve improper handling of user identity verification—allowing unauthorized access or blocking legitimate users.

### Common Defects

| Defect Type | Description | Risk Level |
|-------------|-------------|------------|
| **Missing Auth Check** | Endpoint accessible without token | Critical |
| **Broken Token Validation** | Expired/invalid tokens accepted | Critical |
| **Token Leakage** | Sensitive tokens in URLs/logs | High |
| **Weak Token Generation** | Predictable or guessable tokens | Critical |
| **Session Fixation** | Token not rotated after login | High |
| **Improper Logout** | Tokens still valid after logout | Medium |

### Example Scenarios

```
# Defect: Missing authentication on sensitive endpoint
GET /api/admin/users
Authorization: (none)

Response: 200 OK
{
    "users": [...all user data...]  // CRITICAL BUG!
}

# Defect: Expired token still works
GET /api/profile
Authorization: Bearer eyJ...expired_token...

Response: 200 OK  // Should be 401!

# Defect: Token from logged-out session still valid
POST /api/logout
Authorization: Bearer eyJ...token...
Response: 200 OK

GET /api/profile
Authorization: Bearer eyJ...same_token...
Response: 200 OK  // Should be 401 after logout!
```

### Testing Checklist

```
Authentication Test Cases:
□ Request without any authentication header
□ Request with malformed token format
□ Request with expired token
□ Request with token for deleted user
□ Request with token signed with wrong key
□ Request after user logout
□ Request with token from different environment
□ Brute force protection validation
```

## Rate Limiting Issues

### What It Is

Rate limiting issues occur when APIs fail to properly restrict the number of requests a client can make, leading to potential abuse or denial of service.

### Common Defects

**Missing Rate Limiting:**
- No limits on authentication attempts (enables brute force)
- Unlimited API calls (enables scraping)
- No throttling on expensive operations

**Improper Implementation:**
- Rate limits applied inconsistently across endpoints
- Rate limiting easily bypassed (by changing headers)
- Incorrect rate limit headers returned
- Limits reset at wrong intervals

### Example Testing

```
# Test: Verify rate limiting exists
for i in {1..150}; do
    curl -X POST /api/login \
         -d '{"user":"test","pass":"wrong"}' \
         -w "Request $i: %{http_code}\n"
done

# Expected after threshold (e.g., 100 requests):
# Response: 429 Too Many Requests
# Headers should include:
#   X-RateLimit-Limit: 100
#   X-RateLimit-Remaining: 0
#   X-RateLimit-Reset: 1673456789
```

### Rate Limit Validation Tests

```
Test Case: Verify rate limit enforcement
Given rate limit is 100 requests per minute
When 101 requests are made within one minute
Then request 101 should return 429 Too Many Requests
And response should include Retry-After header

Test Case: Verify rate limit reset
Given rate limit was reached
When the reset interval passes
Then subsequent requests should succeed
And X-RateLimit-Remaining should be reset
```

## Payload Structure Problems

### What It Is

Payload structure problems involve malformed, inconsistent, or incorrect response data structures that can break client applications.

### Common Defects

**Inconsistent Response Structure:**
```json
// Sometimes returns object
GET /api/user/123
{
    "id": 123,
    "name": "John"
}

// Sometimes returns wrapped object (inconsistent!)
GET /api/user/456
{
    "data": {
        "id": 456,
        "name": "Jane"
    }
}
```

**Missing Fields:**
```json
// Expected (per documentation)
{
    "id": 123,
    "name": "John",
    "email": "john@example.com",
    "createdAt": "2024-01-15T10:00:00Z"
}

// Actual (missing fields)
{
    "id": 123,
    "name": "John"
    // email and createdAt missing!
}
```

**Type Inconsistencies:**
```json
// Record 1: ID as number
{ "id": 123, "price": 29.99 }

// Record 2: ID as string (inconsistent!)
{ "id": "124", "price": "29.99" }
```

**Null vs Missing Fields:**
```json
// Some records have null
{ "id": 1, "middleName": null }

// Some records omit the field entirely
{ "id": 2 }  // middleName not present

// Client code must handle both cases
```

### Testing Strategy

```
Schema Validation Tests:
□ All documented fields present
□ Field types match specification
□ Required vs optional fields correct
□ Null handling consistent
□ Empty arrays vs null for collections
□ Date format consistency
□ Nested object structure correct
□ Array item structure consistent
```

## Timeout Issues

### What It Is

Timeout defects occur when APIs take too long to respond or don't handle slow operations gracefully.

### Common Problems

| Issue | Description | Impact |
|-------|-------------|--------|
| **No Timeout** | API hangs indefinitely | Resource exhaustion |
| **Short Timeout** | Legitimate operations fail | Poor user experience |
| **Silent Timeout** | No indication of timeout | Confusing errors |
| **Cascade Timeout** | Downstream service delays | System-wide slowdown |
| **No Async Option** | Long operations block | Poor scalability |

### Testing Approach

```
Test Case: Verify reasonable response time
Given a standard GET request
When /api/users is called
Then response should arrive within 2 seconds
And if timeout occurs, appropriate error should return

Test Case: Test timeout handling for slow operations
Given a request that triggers slow processing
When the operation exceeds timeout threshold
Then API should return 504 Gateway Timeout
And partial work should be rolled back or tracked

Test Case: Verify timeout on downstream failures
Given a downstream service is unresponsive
When API makes request to that service
Then API should timeout gracefully
And return appropriate error to client
And release resources properly
```

### Monitoring Test Example

```java
// Test that measures and validates response times
@Test
void testResponseTimeWithinSLA() {
    long startTime = System.currentTimeMillis();
    
    Response response = given()
        .when()
        .get("/api/users")
        .then()
        .extract().response();
    
    long duration = System.currentTimeMillis() - startTime;
    
    assertThat(duration).isLessThan(2000); // 2 second SLA
    assertThat(response.statusCode()).isEqualTo(200);
}
```

## Authorization Errors

### What It Is

Authorization defects occur when the API improperly controls access to resources based on user permissions—distinct from authentication (proving identity).

### Common Defects

**Horizontal Privilege Escalation:**
```
# User A's token accessing User B's data
GET /api/users/456/orders
Authorization: Bearer <user_A_token>

Response: 200 OK
{
    "orders": [User B's private orders]  // CRITICAL BUG!
}
```

**Vertical Privilege Escalation:**
```
# Regular user accessing admin endpoints
GET /api/admin/all-users
Authorization: Bearer <regular_user_token>

Response: 200 OK  // Should be 403 Forbidden!
```

**IDOR (Insecure Direct Object Reference):**
```
# Simply changing ID grants access
GET /api/documents/12345  # User's document
Response: 200 OK

GET /api/documents/12346  # Someone else's document
Response: 200 OK  // Should be 403!
```

### Testing Matrix

```
Authorization Test Matrix:
┌─────────────────┬─────────┬───────┬───────┬───────────┐
│ Resource        │ Owner   │ Admin │ User  │ Anonymous │
├─────────────────┼─────────┼───────┼───────┼───────────┤
│ View own data   │ ✓ 200   │ ✓ 200 │ ✗ 403 │ ✗ 401     │
│ Edit own data   │ ✓ 200   │ ✓ 200 │ ✗ 403 │ ✗ 401     │
│ View others     │ ✗ 403   │ ✓ 200 │ ✗ 403 │ ✗ 401     │
│ Admin functions │ ✗ 403   │ ✓ 200 │ ✗ 403 │ ✗ 401     │
│ Delete resource │ ✓ 200   │ ✓ 200 │ ✗ 403 │ ✗ 401     │
└─────────────────┴─────────┴───────┴───────┴───────────┘
```

## Error Message Defects

### What It Is

Error message defects involve unhelpful, inconsistent, or security-revealing error messages.

### Common Problems

**Too Vague:**
```json
{
    "error": "Something went wrong"  // Not helpful!
}
```

**Too Revealing (Security Risk):**
```json
{
    "error": "SQLException: SELECT * FROM users WHERE id = 123",
    "stackTrace": "at com.app.UserDAO.findById(UserDAO.java:45)..."
}
// Reveals database schema and internal code paths!
```

**Inconsistent Format:**
```json
// Endpoint 1
{ "error": "Not found" }

// Endpoint 2
{ "message": "Resource not found", "code": "NOT_FOUND" }

// Endpoint 3
{ "errors": [{ "field": "id", "message": "Not found" }] }
```

### Best Practice Error Format

```json
{
    "error": {
        "code": "VALIDATION_ERROR",
        "message": "Invalid input provided",
        "details": [
            {
                "field": "email",
                "message": "Must be a valid email address"
            },
            {
                "field": "age",
                "message": "Must be a positive number"
            }
        ],
        "requestId": "abc-123-def",
        "timestamp": "2024-01-15T10:30:00Z"
    }
}
```

## Defect Detection Checklist

Use this comprehensive checklist when testing APIs:

```
□ STATUS CODES
  □ Success operations return 2xx
  □ Client errors return 4xx
  □ Server errors return 5xx
  □ Correct specific codes (201 vs 200, 401 vs 403)

□ DATA VALIDATION
  □ Required fields enforced
  □ Data types validated
  □ Boundary conditions handled
  □ Special characters processed correctly

□ AUTHENTICATION
  □ All endpoints require auth (unless intentionally public)
  □ Invalid tokens rejected
  □ Expired tokens rejected
  □ Logout invalidates tokens

□ AUTHORIZATION
  □ Users can only access own resources
  □ Role-based access enforced
  □ IDOR vulnerabilities tested

□ RATE LIMITING
  □ Limits enforced on sensitive endpoints
  □ Correct headers returned
  □ Limits reset properly

□ ERROR HANDLING
  □ Consistent error format
  □ Helpful error messages
  □ No sensitive information leaked
  □ Graceful handling of unexpected inputs

□ PERFORMANCE
  □ Response times within SLA
  □ Timeouts configured properly
  □ Large payloads handled
```

## Summary

- **Status code mismatches** mislead clients about operation outcomes
- **Data validation errors** allow corrupt data into systems or reject legitimate input
- **Authentication failures** can expose systems to unauthorized access
- **Rate limiting issues** enable abuse and denial of service
- **Payload structure problems** break client applications expecting consistent data
- **Timeout issues** cause poor user experience and resource exhaustion
- **Authorization errors** allow users to access resources they shouldn't
- **Error message defects** either confuse users or reveal security-sensitive information

By understanding these common defect patterns, you can design targeted test cases that uncover vulnerabilities before they reach production. As you begin using Postman later today, keep this defect catalog in mind—it will guide your test case design and help you think like both a tester and an attacker.

## Additional Resources

- [OWASP API Security Top 10](https://owasp.org/www-project-api-security/) - Industry standard API security vulnerabilities
- [HTTP Status Codes Reference](https://httpstatuses.com/) - Complete guide to HTTP status codes
- [API Testing Best Practices](https://www.guru99.com/api-testing.html) - Comprehensive testing strategies

