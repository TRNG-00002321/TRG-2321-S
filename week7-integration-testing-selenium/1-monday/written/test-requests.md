# Creating and Sending Test Requests in Postman

## Learning Objectives
- Create and send requests using all major HTTP methods
- Configure request headers for different scenarios
- Work with query parameters and path parameters
- Construct request bodies in various formats
- Implement different authentication types in Postman

## Why This Matters

Now that you understand Postman's interface, it's time to master the craft of constructing API requests. In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, this skill is foundational—every API test starts with a well-crafted request.

Think of an API request as a carefully written letter: it needs the right address (endpoint), proper formatting (headers), and clear content (body). A malformed request can lead to confusing responses and false test results. By mastering request construction, you ensure that when tests fail, it's because of actual API defects—not your request structure.

## HTTP Methods Deep Dive

HTTP methods (also called "verbs") indicate the intended action for a request. Each method has specific semantics and use cases.

### GET - Retrieve Data

**Purpose:** Fetch data from the server without modifying it.

**Characteristics:**
- Safe (no side effects)
- Idempotent (same result every time)
- Cacheable
- No request body (data in URL)

**Postman Example:**
```
Method: GET
URL: https://api.bookstore.com/books
Parameters: 
  - genre=fiction
  - limit=10
```

**Common Use Cases:**
```
GET /users              → List all users
GET /users/123          → Get specific user
GET /users?active=true  → Filtered list
GET /users/123/orders   → User's orders
```

### POST - Create Data

**Purpose:** Submit data to create a new resource.

**Characteristics:**
- Not safe (causes changes)
- Not idempotent (each call may create new resource)
- Not cacheable
- Request body contains data

**Postman Example:**
```
Method: POST
URL: https://api.bookstore.com/books
Headers:
  Content-Type: application/json
Body:
{
    "title": "The Art of Testing",
    "author": "Jane Doe",
    "isbn": "978-0-123456-78-9",
    "price": 29.99
}
```

**Expected Response:** 201 Created with new resource

### PUT - Replace Data

**Purpose:** Replace an entire resource with new data.

**Characteristics:**
- Not safe (causes changes)
- Idempotent (same result if repeated)
- Request body contains complete resource
- Creates if doesn't exist (in some APIs)

**Postman Example:**
```
Method: PUT
URL: https://api.bookstore.com/books/123
Headers:
  Content-Type: application/json
Body:
{
    "title": "The Art of Testing - 2nd Edition",
    "author": "Jane Doe",
    "isbn": "978-0-123456-78-9",
    "price": 34.99,
    "edition": 2
}
```

**Key Point:** PUT replaces the ENTIRE resource. Missing fields may be removed.

### PATCH - Partial Update

**Purpose:** Apply partial modifications to a resource.

**Characteristics:**
- Not safe (causes changes)
- Not necessarily idempotent
- Request body contains only changed fields
- More efficient for small updates

**Postman Example:**
```
Method: PATCH
URL: https://api.bookstore.com/books/123
Headers:
  Content-Type: application/json
Body:
{
    "price": 24.99
}
```

**PUT vs PATCH:**
```
PUT /books/123          PATCH /books/123
{                       {
  "title": "Book",        "price": 24.99
  "author": "Author",   }
  "price": 24.99,       // Only price updated
  "isbn": "123..."      // Other fields unchanged
}
// All fields required
```

### DELETE - Remove Data

**Purpose:** Remove a resource from the server.

**Characteristics:**
- Not safe (causes changes)
- Idempotent (deleting twice = same result)
- Usually no request body
- May return deleted resource or empty response

**Postman Example:**
```
Method: DELETE
URL: https://api.bookstore.com/books/123
```

**Expected Responses:**
- 204 No Content (successful, no body)
- 200 OK (successful, with deleted resource)
- 404 Not Found (resource didn't exist)

### Method Summary Table

| Method | Purpose | Safe | Idempotent | Body | Success Codes |
|--------|---------|------|------------|------|---------------|
| GET | Retrieve | Yes | Yes | No | 200 |
| POST | Create | No | No | Yes | 201 |
| PUT | Replace | No | Yes | Yes | 200, 204 |
| PATCH | Partial Update | No | No | Yes | 200, 204 |
| DELETE | Remove | No | Yes | No | 200, 204 |
| HEAD | Headers only | Yes | Yes | No | 200 |
| OPTIONS | Get allowed methods | Yes | Yes | No | 200, 204 |

## Request Headers

Headers provide metadata about the request. They're crucial for authentication, content negotiation, and API behavior control.

### Essential Headers

**Content-Type**
Specifies the format of the request body:
```
Content-Type: application/json          → JSON data
Content-Type: application/xml           → XML data
Content-Type: application/x-www-form-urlencoded  → Form data
Content-Type: multipart/form-data       → File uploads
Content-Type: text/plain                → Plain text
```

**Accept**
Specifies desired response format:
```
Accept: application/json                → Want JSON response
Accept: application/xml                 → Want XML response
Accept: */*                             → Any format
Accept: application/json, application/xml  → Preference order
```

**Authorization**
Contains authentication credentials:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=
Authorization: ApiKey abc123def456
```

### Adding Headers in Postman

1. Click the "Headers" tab
2. Enter header name in "Key" column
3. Enter header value in "Value" column
4. Check the checkbox to include/exclude

**Bulk Edit:**
```
Content-Type: application/json
Accept: application/json
X-API-Version: v2
X-Request-ID: {{$guid}}
```

### Common Header Patterns

```
Standard REST API Headers:
├── Content-Type: application/json
├── Accept: application/json
├── Authorization: Bearer <token>
└── X-Request-ID: <correlation-id>

Caching Headers:
├── Cache-Control: no-cache
├── If-None-Match: "etag-value"
└── If-Modified-Since: <date>

Custom Application Headers:
├── X-API-Key: <api-key>
├── X-Tenant-ID: <tenant>
└── X-Client-Version: 2.1.0
```

## Query Parameters

Query parameters filter, sort, or modify the request. They appear after the `?` in the URL.

### Format

```
Base URL: https://api.example.com/products
With Parameters: https://api.example.com/products?category=electronics&sort=price&order=asc
```

### Adding in Postman

**Method 1: Params Tab (Recommended)**
1. Click "Params" tab
2. Enter key-value pairs in the table
3. Parameters automatically added to URL

**Method 2: Direct in URL**
```
https://api.example.com/products?category=electronics&limit=10
```

### Common Parameter Patterns

**Filtering:**
```
GET /products?category=electronics
GET /products?minPrice=100&maxPrice=500
GET /products?status=active
GET /users?role=admin&department=engineering
```

**Pagination:**
```
GET /products?page=2&pageSize=20
GET /products?offset=40&limit=20
GET /products?cursor=abc123
```

**Sorting:**
```
GET /products?sort=price&order=asc
GET /products?sort=-createdAt          (- prefix for descending)
GET /products?sortBy=name,price
```

**Searching:**
```
GET /products?search=laptop
GET /products?q=gaming+laptop
GET /users?email=*@example.com
```

**Field Selection:**
```
GET /users?fields=id,name,email
GET /products?include=reviews,inventory
GET /products?exclude=description
```

## Path Parameters

Path parameters are dynamic parts of the URL that identify specific resources.

### Format

```
Template: /users/{userId}/orders/{orderId}
Actual:   /users/123/orders/456
```

### In Postman

Postman recognizes path parameters when you use colon syntax:

```
URL: https://api.example.com/users/:userId/orders/:orderId
```

When you enter this, Postman creates editable fields for:
- `userId`
- `orderId`

### Path vs Query Parameters

| Aspect | Path Parameters | Query Parameters |
|--------|-----------------|------------------|
| **Purpose** | Identify resource | Filter/modify |
| **Position** | In URL path | After ? |
| **Required** | Usually required | Usually optional |
| **Example** | `/users/123` | `/users?active=true` |

**When to Use Path:**
- Identifying a specific resource
- Part of the resource hierarchy
- Required for the request to make sense

**When to Use Query:**
- Optional filters
- Sorting/pagination
- Search criteria

## Request Body Formats

### JSON (Most Common)

**In Postman:**
1. Select "Body" tab
2. Choose "raw"
3. Select "JSON" from dropdown
4. Enter JSON data

```json
{
    "user": {
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com",
        "preferences": {
            "newsletter": true,
            "notifications": ["email", "sms"]
        }
    }
}
```

### Form Data (x-www-form-urlencoded)

**Use Case:** Traditional form submissions, simple key-value data

**In Postman:**
1. Select "Body" tab
2. Choose "x-www-form-urlencoded"
3. Enter key-value pairs

```
username: johndoe
password: secret123
remember_me: true
```

### Multipart Form Data

**Use Case:** File uploads, mixed data types

**In Postman:**
1. Select "Body" tab
2. Choose "form-data"
3. Enter key-value pairs
4. Select "File" type for file uploads

```
Key         | Type | Value
------------|------|------------------
name        | Text | Product Image
category    | Text | electronics
image       | File | [Select File]
thumbnail   | File | [Select File]
```

### Raw (Various Formats)

**XML:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<user>
    <firstName>John</firstName>
    <lastName>Doe</lastName>
    <email>john.doe@example.com</email>
</user>
```

**Plain Text:**
```
This is plain text content
that will be sent as-is
to the server.
```

### Binary

**Use Case:** Direct file upload without form wrapping

**In Postman:**
1. Select "Body" tab
2. Choose "binary"
3. Click "Select File"

## Authentication Types in Postman

### No Auth

Use when endpoint doesn't require authentication (public APIs):
```
Authorization: (none)
```

### API Key

**Header-based:**
```
Type: API Key
Add to: Header
Key: X-API-Key
Value: your-api-key-here
```

**Query parameter-based:**
```
Type: API Key
Add to: Query Params
Key: api_key
Value: your-api-key-here
```

### Basic Auth

Base64-encoded username:password:
```
Type: Basic Auth
Username: myuser
Password: mypassword

Generates:
Authorization: Basic bXl1c2VyOm15cGFzc3dvcmQ=
```

### Bearer Token

Token-based authentication (JWT, OAuth tokens):
```
Type: Bearer Token
Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Generates:
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### OAuth 2.0

For services requiring OAuth flow:
```
Type: OAuth 2.0
Grant Type: Authorization Code / Client Credentials / Password
Access Token URL: https://auth.example.com/oauth/token
Client ID: your-client-id
Client Secret: your-client-secret
Scope: read write
```

Postman can automatically handle the OAuth flow and token refresh.

### Digest Auth

Challenge-response authentication:
```
Type: Digest Auth
Username: myuser
Password: mypassword
```

### Inherit from Parent

For collection-wide authentication:
```
Type: Inherit auth from parent
```

Set authentication at collection level, all requests inherit it.

## Practical Request Examples

### Example 1: User Registration

```
Method: POST
URL: https://api.example.com/v1/users/register
Headers:
  Content-Type: application/json
  Accept: application/json
Body (raw JSON):
{
    "email": "newuser@example.com",
    "password": "SecurePass123!",
    "firstName": "Jane",
    "lastName": "Smith",
    "acceptTerms": true
}
```

### Example 2: Search with Filters

```
Method: GET
URL: https://api.example.com/v1/products
Params:
  category: electronics
  minPrice: 100
  maxPrice: 1000
  inStock: true
  sort: price
  order: asc
  page: 1
  limit: 20
Headers:
  Authorization: Bearer <token>
  Accept: application/json
```

### Example 3: Update with PATCH

```
Method: PATCH
URL: https://api.example.com/v1/users/:userId/profile
Path Variables:
  userId: 12345
Headers:
  Content-Type: application/json
  Authorization: Bearer <token>
Body (raw JSON):
{
    "bio": "Updated biography",
    "avatar": "https://example.com/new-avatar.jpg"
}
```

### Example 4: File Upload

```
Method: POST
URL: https://api.example.com/v1/documents/upload
Headers:
  Authorization: Bearer <token>
Body (form-data):
  Key: file | Type: File | Value: document.pdf
  Key: category | Type: Text | Value: reports
  Key: description | Type: Text | Value: Q4 Sales Report
```

## Request Building Checklist

Use this checklist when constructing API requests:

```
□ METHOD
  □ Appropriate method for the action (GET/POST/PUT/PATCH/DELETE)
  □ Method matches API documentation

□ URL
  □ Correct base URL for environment
  □ Correct endpoint path
  □ Path parameters properly substituted

□ HEADERS
  □ Content-Type set for POST/PUT/PATCH
  □ Accept header for response format preference
  □ Authorization header if required
  □ Any custom headers required by API

□ PARAMETERS
  □ Required query parameters included
  □ Optional parameters as needed for test case
  □ Parameters properly URL-encoded

□ BODY
  □ Required fields included
  □ Data types correct (strings, numbers, booleans)
  □ Nested objects properly structured
  □ Arrays formatted correctly

□ AUTHENTICATION
  □ Correct auth type selected
  □ Valid credentials/tokens
  □ Token not expired
```

## Summary

- **HTTP methods** each have specific purposes: GET (retrieve), POST (create), PUT (replace), PATCH (update), DELETE (remove)
- **Headers** control request behavior, content type, and authentication
- **Query parameters** filter and modify responses; **path parameters** identify specific resources
- **Request bodies** can be JSON, form data, multipart, or raw content
- **Authentication types** range from simple API keys to complex OAuth 2.0 flows
- **Proper request construction** is essential for meaningful test results

With these request-building skills, you're ready to explore any API. In the next lessons, you'll learn to add intelligence to your requests through pre-request scripts and validate responses with post-test scripts.

## Additional Resources

- [HTTP Methods - MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods) - Complete method reference
- [HTTP Headers - MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers) - Comprehensive header guide
- [JSON Specification](https://www.json.org/json-en.html) - Official JSON format documentation

