# Consuming REST Endpoints in Python

## Learning Objectives
- Parse and validate JSON responses effectively
- Implement comprehensive error handling for API responses
- Work with response objects and extract relevant data
- Handle various status codes appropriately
- Access and validate response headers

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, consuming API endpoints correctly is the foundation of reliable API testing. While sending requests is straightforward, properly handling responses—parsing data, validating structures, handling errors—separates robust tests from fragile ones.

This lesson bridges the gap between making requests and writing meaningful assertions. You'll learn to extract exactly the data you need, handle edge cases gracefully, and build tests that provide clear feedback when APIs misbehave.

## Parsing JSON Responses

### Basic JSON Parsing

```python
import requests

response = requests.get("https://api.example.com/users/123")

# Parse JSON response
data = response.json()

# Access data
print(data["id"])           # 123
print(data["name"])         # "John Doe"
print(data["email"])        # "john@example.com"

# Nested data
print(data["address"]["city"])      # "New York"
print(data["orders"][0]["total"])   # 150.00
```

### Safe JSON Parsing

```python
import requests

response = requests.get("https://api.example.com/resource")

# Check content type before parsing
if "application/json" in response.headers.get("Content-Type", ""):
    data = response.json()
else:
    print(f"Unexpected content type: {response.headers.get('Content-Type')}")
    data = None

# Handle parsing errors
try:
    data = response.json()
except requests.exceptions.JSONDecodeError as e:
    print(f"Failed to parse JSON: {e}")
    print(f"Response body: {response.text}")
    data = None
```

### Navigating Complex JSON

```python
# Given response:
# {
#     "data": {
#         "users": [
#             {"id": 1, "name": "John", "tags": ["admin", "active"]},
#             {"id": 2, "name": "Jane", "tags": ["user", "active"]}
#         ],
#         "pagination": {
#             "page": 1,
#             "total_pages": 10
#         }
#     },
#     "metadata": {
#         "request_id": "abc123"
#     }
# }

response = requests.get("https://api.example.com/users")
data = response.json()

# Navigate nested structure
users = data["data"]["users"]
first_user = users[0]
first_user_name = first_user["name"]
first_user_tags = first_user["tags"]

# Pagination info
current_page = data["data"]["pagination"]["page"]
total_pages = data["data"]["pagination"]["total_pages"]

# Metadata
request_id = data["metadata"]["request_id"]

# Safe navigation with .get()
description = data.get("description", "No description available")
category = data.get("data", {}).get("category", "unknown")
```

### Working with Lists

```python
response = requests.get("https://api.example.com/products")
products = response.json()["products"]

# Iterate through items
for product in products:
    print(f"{product['name']}: ${product['price']}")

# List comprehension for filtering
active_products = [p for p in products if p["status"] == "active"]
expensive_items = [p for p in products if p["price"] > 100]

# Extract specific fields
product_names = [p["name"] for p in products]
product_ids = [p["id"] for p in products]

# Find specific item
target_product = next(
    (p for p in products if p["id"] == 123),
    None  # Default if not found
)
```

## Error Handling

### Status Code-Based Handling

```python
def handle_api_response(response):
    """Handle API response based on status code."""
    if response.status_code == 200:
        return {"success": True, "data": response.json()}
    
    elif response.status_code == 201:
        return {"success": True, "data": response.json(), "created": True}
    
    elif response.status_code == 204:
        return {"success": True, "data": None}  # No content
    
    elif response.status_code == 400:
        error_data = response.json()
        return {
            "success": False,
            "error": "Bad Request",
            "details": error_data.get("errors", [])
        }
    
    elif response.status_code == 401:
        return {"success": False, "error": "Unauthorized"}
    
    elif response.status_code == 403:
        return {"success": False, "error": "Forbidden"}
    
    elif response.status_code == 404:
        return {"success": False, "error": "Not Found"}
    
    elif response.status_code == 422:
        error_data = response.json()
        return {
            "success": False,
            "error": "Validation Error",
            "details": error_data.get("errors", [])
        }
    
    elif response.status_code >= 500:
        return {
            "success": False,
            "error": "Server Error",
            "status_code": response.status_code
        }
    
    else:
        return {
            "success": False,
            "error": "Unexpected Status",
            "status_code": response.status_code
        }
```

### Comprehensive Error Handling

```python
import requests
from requests.exceptions import (
    RequestException, ConnectionError, Timeout, HTTPError
)

def safe_api_call(method, url, **kwargs):
    """Make an API call with comprehensive error handling."""
    try:
        response = requests.request(method, url, timeout=30, **kwargs)
        response.raise_for_status()
        
        # Success - return parsed JSON
        if response.content:
            return {
                "success": True,
                "status_code": response.status_code,
                "data": response.json()
            }
        else:
            return {
                "success": True,
                "status_code": response.status_code,
                "data": None
            }
    
    except ConnectionError as e:
        return {
            "success": False,
            "error_type": "connection",
            "message": f"Failed to connect: {e}"
        }
    
    except Timeout as e:
        return {
            "success": False,
            "error_type": "timeout",
            "message": f"Request timed out: {e}"
        }
    
    except HTTPError as e:
        # HTTP error (4xx, 5xx)
        error_response = e.response
        try:
            error_body = error_response.json()
        except:
            error_body = error_response.text
        
        return {
            "success": False,
            "error_type": "http",
            "status_code": error_response.status_code,
            "message": str(e),
            "details": error_body
        }
    
    except requests.exceptions.JSONDecodeError as e:
        return {
            "success": False,
            "error_type": "parse",
            "message": f"Failed to parse JSON: {e}"
        }
    
    except RequestException as e:
        return {
            "success": False,
            "error_type": "request",
            "message": f"Request failed: {e}"
        }


# Usage
result = safe_api_call("GET", "https://api.example.com/users/123")

if result["success"]:
    user = result["data"]
    print(f"Found user: {user['name']}")
else:
    print(f"Error: {result['message']}")
```

## Working with Response Objects

### Response Object Properties

```python
response = requests.get("https://api.example.com/users")

# Status information
response.status_code      # 200
response.reason           # "OK"
response.ok               # True (for 2xx status codes)

# Response body
response.text             # Raw text response
response.content          # Bytes content
response.json()           # Parsed JSON

# Request information
response.url              # Final URL (after redirects)
response.request          # PreparedRequest object
response.request.method   # "GET"
response.request.headers  # Request headers

# Timing
response.elapsed          # Time taken (timedelta)
response.elapsed.total_seconds()  # Time in seconds

# History (for redirects)
response.history          # List of Response objects
response.is_redirect      # True if redirect
response.is_permanent_redirect  # True if permanent redirect

# Encoding
response.encoding         # Character encoding
response.apparent_encoding  # Detected encoding
```

### Extracting Data Patterns

```python
class APIResponse:
    """Wrapper for API response with convenient accessors."""
    
    def __init__(self, response):
        self.raw = response
        self.status_code = response.status_code
        self.headers = response.headers
        self._json = None
    
    @property
    def success(self):
        return self.raw.ok
    
    @property
    def json(self):
        if self._json is None:
            try:
                self._json = self.raw.json()
            except:
                self._json = {}
        return self._json
    
    def get(self, path, default=None):
        """Get nested value using dot notation."""
        keys = path.split(".")
        value = self.json
        for key in keys:
            if isinstance(value, dict):
                value = value.get(key)
            elif isinstance(value, list) and key.isdigit():
                index = int(key)
                value = value[index] if index < len(value) else None
            else:
                return default
            if value is None:
                return default
        return value
    
    @property
    def response_time_ms(self):
        return self.raw.elapsed.total_seconds() * 1000


# Usage
response = APIResponse(requests.get("https://api.example.com/users/123"))

if response.success:
    name = response.get("name")
    city = response.get("address.city", "Unknown")
    first_order_total = response.get("orders.0.total", 0)
    print(f"Response time: {response.response_time_ms:.2f}ms")
```

## Handling Status Codes

### Status Code Assertions in Tests

```python
import pytest
import requests

class TestUserAPI:
    """Test class for User API endpoints."""
    
    BASE_URL = "https://api.example.com"
    
    def test_get_user_success(self):
        """GET /users/{id} returns 200 for existing user."""
        response = requests.get(f"{self.BASE_URL}/users/123")
        assert response.status_code == 200
        assert response.json()["id"] == 123
    
    def test_get_user_not_found(self):
        """GET /users/{id} returns 404 for non-existent user."""
        response = requests.get(f"{self.BASE_URL}/users/999999")
        assert response.status_code == 404
        error = response.json()
        assert "error" in error
        assert error["error"]["code"] == "NOT_FOUND"
    
    def test_create_user_success(self):
        """POST /users returns 201 with valid data."""
        user_data = {
            "name": "Test User",
            "email": f"test_{int(time.time())}@example.com"
        }
        response = requests.post(
            f"{self.BASE_URL}/users",
            json=user_data
        )
        assert response.status_code == 201
        created = response.json()
        assert created["id"] is not None
        assert created["name"] == user_data["name"]
    
    def test_create_user_validation_error(self):
        """POST /users returns 400 with invalid data."""
        invalid_data = {
            "name": "",  # Empty name
            "email": "invalid-email"  # Invalid format
        }
        response = requests.post(
            f"{self.BASE_URL}/users",
            json=invalid_data
        )
        assert response.status_code == 400
        error = response.json()
        assert "errors" in error or "error" in error
    
    def test_delete_user_success(self):
        """DELETE /users/{id} returns 204."""
        # First create a user
        create_resp = requests.post(
            f"{self.BASE_URL}/users",
            json={"name": "To Delete", "email": "delete@test.com"}
        )
        user_id = create_resp.json()["id"]
        
        # Then delete
        response = requests.delete(f"{self.BASE_URL}/users/{user_id}")
        assert response.status_code in [200, 204]
    
    def test_unauthorized_access(self):
        """Protected endpoint returns 401 without auth."""
        response = requests.get(f"{self.BASE_URL}/admin/users")
        assert response.status_code == 401
```

### Status Code Ranges

```python
def assert_success(response):
    """Assert response is successful (2xx)."""
    assert 200 <= response.status_code < 300, \
        f"Expected 2xx, got {response.status_code}: {response.text}"

def assert_client_error(response):
    """Assert response is client error (4xx)."""
    assert 400 <= response.status_code < 500, \
        f"Expected 4xx, got {response.status_code}"

def assert_server_error(response):
    """Assert response is server error (5xx)."""
    assert 500 <= response.status_code < 600, \
        f"Expected 5xx, got {response.status_code}"
```

## Response Headers

### Accessing Headers

```python
response = requests.get("https://api.example.com/users")

# All headers (case-insensitive dict)
print(response.headers)

# Specific headers
content_type = response.headers["Content-Type"]
content_length = response.headers.get("Content-Length")
cache_control = response.headers.get("Cache-Control", "not set")

# Common headers to check
rate_limit = response.headers.get("X-RateLimit-Limit")
rate_remaining = response.headers.get("X-RateLimit-Remaining")
request_id = response.headers.get("X-Request-ID")
```

### Header Validation

```python
def test_response_headers():
    """Validate important response headers."""
    response = requests.get("https://api.example.com/users")
    
    # Content type
    assert "application/json" in response.headers["Content-Type"]
    
    # Security headers
    assert response.headers.get("X-Content-Type-Options") == "nosniff"
    
    # CORS headers
    assert "Access-Control-Allow-Origin" in response.headers
    
    # Rate limiting
    assert "X-RateLimit-Limit" in response.headers
    assert "X-RateLimit-Remaining" in response.headers
    
    # Caching
    if response.headers.get("Cache-Control"):
        print(f"Cache-Control: {response.headers['Cache-Control']}")
```

### Extracting Pagination from Headers

```python
def get_paginated_results(url, params=None):
    """Fetch all pages of results using Link headers."""
    all_results = []
    
    while url:
        response = requests.get(url, params=params)
        response.raise_for_status()
        
        all_results.extend(response.json()["data"])
        
        # Check for next page in Link header
        link_header = response.headers.get("Link", "")
        url = None
        
        for link in link_header.split(","):
            if 'rel="next"' in link:
                url = link.split(";")[0].strip(" <>")
                params = None  # URL includes params
                break
    
    return all_results
```

## Complete Test Example

```python
"""
Complete example: Consuming and validating API endpoints.
"""
import pytest
import requests
from dataclasses import dataclass
from typing import Optional, List, Dict, Any

@dataclass
class User:
    """User data model."""
    id: int
    name: str
    email: str
    role: str = "user"
    active: bool = True

class UserAPIClient:
    """Client for User API operations."""
    
    def __init__(self, base_url: str, token: str):
        self.base_url = base_url
        self.session = requests.Session()
        self.session.headers.update({
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json",
            "Accept": "application/json"
        })
    
    def get_user(self, user_id: int) -> Optional[User]:
        """Get user by ID."""
        response = self.session.get(f"{self.base_url}/users/{user_id}")
        
        if response.status_code == 404:
            return None
        
        response.raise_for_status()
        data = response.json()
        
        return User(
            id=data["id"],
            name=data["name"],
            email=data["email"],
            role=data.get("role", "user"),
            active=data.get("active", True)
        )
    
    def get_users(self, filters: Dict[str, Any] = None) -> List[User]:
        """Get list of users with optional filters."""
        response = self.session.get(
            f"{self.base_url}/users",
            params=filters
        )
        response.raise_for_status()
        
        return [
            User(
                id=u["id"],
                name=u["name"],
                email=u["email"],
                role=u.get("role", "user"),
                active=u.get("active", True)
            )
            for u in response.json()["users"]
        ]
    
    def create_user(self, name: str, email: str, role: str = "user") -> User:
        """Create a new user."""
        response = self.session.post(
            f"{self.base_url}/users",
            json={"name": name, "email": email, "role": role}
        )
        response.raise_for_status()
        data = response.json()
        
        return User(
            id=data["id"],
            name=data["name"],
            email=data["email"],
            role=data.get("role", "user")
        )
    
    def delete_user(self, user_id: int) -> bool:
        """Delete a user."""
        response = self.session.delete(f"{self.base_url}/users/{user_id}")
        return response.status_code in [200, 204]


class TestUserAPI:
    """Test suite for User API."""
    
    @pytest.fixture
    def client(self):
        return UserAPIClient(
            base_url="https://api.example.com",
            token="test-token"
        )
    
    @pytest.fixture
    def created_user(self, client):
        """Create a test user and clean up after test."""
        user = client.create_user(
            name="Test User",
            email=f"test_{int(time.time())}@example.com"
        )
        yield user
        client.delete_user(user.id)
    
    def test_get_user_returns_user_object(self, client, created_user):
        """Should return User object for existing user."""
        user = client.get_user(created_user.id)
        
        assert user is not None
        assert user.id == created_user.id
        assert user.name == created_user.name
        assert user.email == created_user.email
    
    def test_get_user_returns_none_for_missing(self, client):
        """Should return None for non-existent user."""
        user = client.get_user(999999)
        assert user is None
    
    def test_get_users_returns_list(self, client):
        """Should return list of users."""
        users = client.get_users()
        
        assert isinstance(users, list)
        if users:
            assert all(isinstance(u, User) for u in users)
    
    def test_get_users_with_filter(self, client):
        """Should filter users by role."""
        admin_users = client.get_users({"role": "admin"})
        
        assert all(u.role == "admin" for u in admin_users)
    
    def test_create_user_returns_new_user(self, client):
        """Should create and return new user."""
        user = client.create_user(
            name="New User",
            email=f"new_{int(time.time())}@example.com",
            role="admin"
        )
        
        assert user.id is not None
        assert user.name == "New User"
        assert user.role == "admin"
        
        # Cleanup
        client.delete_user(user.id)
```

## Summary

- **JSON parsing** with `.json()` handles most API responses; use `.get()` for safe access
- **Error handling** should cover HTTP errors, connection issues, and parsing failures
- **Response objects** provide status, headers, timing, and body access
- **Status codes** indicate success, client errors, and server errors—test each scenario
- **Headers** carry important metadata like rate limits, caching, and pagination
- **Type-safe clients** using dataclasses improve code reliability and IDE support

With endpoint consumption mastered, you're ready to explore JMeter for performance testing in the next lesson.

## Additional Resources

- [HTTP Status Codes](https://httpstatuses.com/) - Complete status code reference
- [JSON Path in Python](https://pypi.org/project/jsonpath-ng/) - Advanced JSON navigation
- [Pydantic](https://docs.pydantic.dev/) - Data validation and serialization

