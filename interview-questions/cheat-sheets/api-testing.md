# API Testing Cheat Sheet

## HTTP Methods

| Method | Purpose | Has Body | Idempotent |
|--------|---------|----------|------------|
| GET | Retrieve resource | No | Yes |
| POST | Create resource | Yes | No |
| PUT | Update/replace resource | Yes | Yes |
| PATCH | Partial update | Yes | No |
| DELETE | Remove resource | Optional | Yes |
| HEAD | Get headers only | No | Yes |
| OPTIONS | Get allowed methods | No | Yes |

## HTTP Status Codes

### 2xx Success
| Code | Meaning |
|------|---------|
| 200 | OK |
| 201 | Created |
| 204 | No Content |

### 3xx Redirection
| Code | Meaning |
|------|---------|
| 301 | Moved Permanently |
| 302 | Found (Temporary Redirect) |
| 304 | Not Modified |

### 4xx Client Errors
| Code | Meaning |
|------|---------|
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 405 | Method Not Allowed |
| 409 | Conflict |
| 422 | Unprocessable Entity |
| 429 | Too Many Requests |

### 5xx Server Errors
| Code | Meaning |
|------|---------|
| 500 | Internal Server Error |
| 502 | Bad Gateway |
| 503 | Service Unavailable |
| 504 | Gateway Timeout |

## Postman

### Request Components
```
Method: GET/POST/PUT/DELETE
URL: https://api.example.com/users/123
Headers:
  Content-Type: application/json
  Authorization: Bearer <token>
Body (POST/PUT):
  {
    "name": "Alice",
    "email": "alice@example.com"
  }
```

### Pre-request Script
```javascript
// Set timestamp
pm.environment.set("timestamp", Date.now());

// Generate random data
pm.environment.set("randomEmail", 
    `user${Math.random().toString(36).substring(7)}@test.com`);

// Get environment variable
const baseUrl = pm.environment.get("baseUrl");
```

### Tests (Post-response)
```javascript
// Status code
pm.test("Status is 200", function() {
    pm.response.to.have.status(200);
});

// Response time
pm.test("Response time < 500ms", function() {
    pm.expect(pm.response.responseTime).to.be.below(500);
});

// JSON body
pm.test("Has correct user", function() {
    const json = pm.response.json();
    pm.expect(json.name).to.eql("Alice");
    pm.expect(json.email).to.include("@");
});

// Headers
pm.test("Content-Type is JSON", function() {
    pm.response.to.have.header("Content-Type");
    pm.expect(pm.response.headers.get("Content-Type"))
        .to.include("application/json");
});

// Save for next request
pm.environment.set("userId", pm.response.json().id);
```

### Variables
```
{{baseUrl}}/users/{{userId}}

Scopes (priority order):
1. Local (set in script)
2. Data (from runner)
3. Environment
4. Collection
5. Global
```

## REST Assured (Java)

### Basic Request
```java
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

// GET request
given()
    .baseUri("https://api.example.com")
    .header("Authorization", "Bearer " + token)
.when()
    .get("/users/123")
.then()
    .statusCode(200)
    .body("name", equalTo("Alice"))
    .body("email", containsString("@"));
```

### POST with Body
```java
String requestBody = """
    {
        "name": "Alice",
        "email": "alice@example.com"
    }
    """;

given()
    .contentType(ContentType.JSON)
    .body(requestBody)
.when()
    .post("/users")
.then()
    .statusCode(201)
    .body("id", notNullValue());
```

### Extract Response
```java
Response response = 
    given()
        .get("/users/123")
    .then()
        .extract().response();

String name = response.jsonPath().getString("name");
int id = response.jsonPath().getInt("id");
List<String> emails = response.jsonPath().getList("users.email");
```

### Path and Query Parameters
```java
given()
    .pathParam("userId", 123)
    .queryParam("include", "orders")
    .queryParam("limit", 10)
.when()
    .get("/users/{userId}")
```

## Python Requests

### Basic Requests
```python
import requests

# GET
response = requests.get("https://api.example.com/users")
print(response.status_code)
print(response.json())

# POST
data = {"name": "Alice", "email": "alice@example.com"}
response = requests.post(
    "https://api.example.com/users",
    json=data,
    headers={"Authorization": "Bearer token"}
)

# PUT
response = requests.put(
    "https://api.example.com/users/123",
    json={"name": "Alice Updated"}
)

# DELETE
response = requests.delete("https://api.example.com/users/123")
```

### Response Handling
```python
response = requests.get(url)

response.status_code      # 200
response.text             # Raw text
response.json()           # Parse JSON
response.headers          # Response headers
response.elapsed          # Time taken
response.url              # Final URL
response.cookies          # Cookies
```

### With pytest
```python
import pytest
import requests

BASE_URL = "https://api.example.com"

class TestUserAPI:
    
    def test_get_user_returns_200(self):
        response = requests.get(f"{BASE_URL}/users/1")
        assert response.status_code == 200
    
    def test_create_user(self):
        user_data = {"name": "Test", "email": "test@test.com"}
        response = requests.post(f"{BASE_URL}/users", json=user_data)
        
        assert response.status_code == 201
        assert response.json()["name"] == "Test"
    
    def test_invalid_user_returns_404(self):
        response = requests.get(f"{BASE_URL}/users/99999")
        assert response.status_code == 404
```

## API Test Checklist

### Functional
- [ ] Valid inputs return correct data
- [ ] Invalid inputs return appropriate errors
- [ ] All HTTP methods work correctly
- [ ] Pagination works properly
- [ ] Filtering/sorting works
- [ ] Required fields validated

### Security
- [ ] Authentication required
- [ ] Authorization enforced
- [ ] Rate limiting works
- [ ] SQL injection prevented
- [ ] Sensitive data not exposed
- [ ] HTTPS enforced

### Performance
- [ ] Response time acceptable
- [ ] Handles concurrent requests
- [ ] Large payloads handled
- [ ] Timeouts configured

### Error Handling
- [ ] Meaningful error messages
- [ ] Correct status codes
- [ ] Graceful failure
- [ ] Proper error format

## Common Headers

| Header | Purpose | Example |
|--------|---------|---------|
| Content-Type | Request body format | `application/json` |
| Accept | Expected response format | `application/json` |
| Authorization | Authentication | `Bearer <token>` |
| X-API-Key | API key auth | `abc123` |
| Cache-Control | Caching behavior | `no-cache` |
