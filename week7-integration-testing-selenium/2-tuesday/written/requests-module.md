# Python Requests Module

## Learning Objectives
- Install and configure the Python requests library
- Perform GET, POST, PUT, and DELETE requests
- Handle request headers and authentication
- Manage sessions for efficient API testing
- Configure timeouts and handle connection issues

## Why This Matters

The requests library is the cornerstone of API testing in Python. In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, mastering requests gives you a powerful, Pythonic way to interact with REST APIs. Its motto—"HTTP for Humans"—reflects its design philosophy of making HTTP communication simple and intuitive.

While you've used similar concepts in Postman and REST Assured, the requests library offers unique advantages: seamless integration with Python's data structures, easy scripting, and compatibility with pytest's fixtures. These skills will accelerate your API testing capabilities.

## Installation and Setup

### Installing requests

```bash
# Using pip
pip install requests

# Using pip with specific version
pip install requests==2.31.0

# Add to requirements.txt
echo "requests>=2.31.0" >> requirements.txt
pip install -r requirements.txt
```

### Basic Import

```python
import requests

# Or import specific components
from requests import Session
from requests.auth import HTTPBasicAuth
from requests.exceptions import RequestException
```

### Verifying Installation

```python
import requests
print(requests.__version__)  # Should print version like '2.31.0'
```

## GET Requests

### Simple GET

```python
import requests

# Basic GET request
response = requests.get("https://api.example.com/users")

# Check status
print(response.status_code)  # 200
print(response.ok)           # True (for 2xx status codes)

# Access response body
print(response.text)         # Raw text response
print(response.json())       # Parsed JSON (if response is JSON)
```

### GET with Query Parameters

```python
# Method 1: Parameters in URL
response = requests.get("https://api.example.com/users?page=1&limit=10")

# Method 2: Using params dict (recommended)
params = {
    "page": 1,
    "limit": 10,
    "status": "active",
    "sort": "name"
}
response = requests.get("https://api.example.com/users", params=params)

# The URL becomes: https://api.example.com/users?page=1&limit=10&status=active&sort=name

# Multiple values for same parameter
params = {
    "tags": ["python", "testing", "api"]  # Becomes tags=python&tags=testing&tags=api
}
response = requests.get("https://api.example.com/posts", params=params)
```

### GET with Headers

```python
headers = {
    "Accept": "application/json",
    "Authorization": "Bearer eyJhbGciOiJIUzI1NiIs...",
    "X-API-Key": "abc123",
    "User-Agent": "API-Test/1.0"
}

response = requests.get(
    "https://api.example.com/users",
    headers=headers
)

# Access response headers
print(response.headers)                      # All headers
print(response.headers["Content-Type"])      # Specific header
print(response.headers.get("X-Custom"))      # Safe access (returns None if missing)
```

## POST Requests

### POST with JSON Body

```python
# Sending JSON data
user_data = {
    "name": "John Doe",
    "email": "john@example.com",
    "role": "user"
}

response = requests.post(
    "https://api.example.com/users",
    json=user_data  # Automatically sets Content-Type: application/json
)

print(response.status_code)  # 201
print(response.json())       # Created user data
```

### POST with Form Data

```python
# URL-encoded form data
form_data = {
    "username": "john",
    "password": "secret123"
}

response = requests.post(
    "https://api.example.com/login",
    data=form_data  # Sends as application/x-www-form-urlencoded
)
```

### POST with File Upload

```python
# Single file upload
with open("document.pdf", "rb") as f:
    files = {"file": f}
    response = requests.post(
        "https://api.example.com/upload",
        files=files
    )

# File with custom filename and content type
files = {
    "file": ("report.pdf", open("document.pdf", "rb"), "application/pdf")
}
response = requests.post("https://api.example.com/upload", files=files)

# Multiple files
files = [
    ("files", ("file1.txt", open("file1.txt", "rb"), "text/plain")),
    ("files", ("file2.txt", open("file2.txt", "rb"), "text/plain"))
]
response = requests.post("https://api.example.com/upload/batch", files=files)

# File with additional form data
files = {"file": open("image.jpg", "rb")}
data = {"description": "Profile photo", "category": "avatar"}
response = requests.post(
    "https://api.example.com/upload",
    files=files,
    data=data
)
```

## PUT and PATCH Requests

### PUT (Full Replacement)

```python
# PUT replaces the entire resource
updated_user = {
    "name": "John Smith",
    "email": "john.smith@example.com",
    "role": "admin",
    "active": True
}

response = requests.put(
    "https://api.example.com/users/123",
    json=updated_user
)

print(response.status_code)  # 200
```

### PATCH (Partial Update)

```python
# PATCH updates only specified fields
partial_update = {
    "role": "admin"  # Only update role
}

response = requests.patch(
    "https://api.example.com/users/123",
    json=partial_update
)
```

## DELETE Requests

```python
# Simple DELETE
response = requests.delete("https://api.example.com/users/123")
print(response.status_code)  # 204 (No Content) or 200

# DELETE with headers
response = requests.delete(
    "https://api.example.com/users/123",
    headers={"Authorization": "Bearer token123"}
)

# DELETE with confirmation body (some APIs require this)
response = requests.delete(
    "https://api.example.com/users/123",
    json={"confirm": True}
)
```

## Authentication

### Basic Authentication

```python
from requests.auth import HTTPBasicAuth

# Method 1: Using HTTPBasicAuth
response = requests.get(
    "https://api.example.com/secure/data",
    auth=HTTPBasicAuth("username", "password")
)

# Method 2: Tuple shorthand
response = requests.get(
    "https://api.example.com/secure/data",
    auth=("username", "password")
)
```

### Bearer Token (JWT)

```python
token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

headers = {
    "Authorization": f"Bearer {token}"
}

response = requests.get(
    "https://api.example.com/users",
    headers=headers
)
```

### API Key Authentication

```python
# API key in header
response = requests.get(
    "https://api.example.com/data",
    headers={"X-API-Key": "your-api-key"}
)

# API key in query parameter
response = requests.get(
    "https://api.example.com/data",
    params={"api_key": "your-api-key"}
)
```

### Digest Authentication

```python
from requests.auth import HTTPDigestAuth

response = requests.get(
    "https://api.example.com/secure",
    auth=HTTPDigestAuth("username", "password")
)
```

## Session Management

Sessions persist parameters across requests, making them ideal for API testing.

### Creating and Using Sessions

```python
# Create a session
session = requests.Session()

# Set default headers for all requests
session.headers.update({
    "Content-Type": "application/json",
    "Accept": "application/json",
    "User-Agent": "APITest/1.0"
})

# Set authentication
session.auth = ("username", "password")

# All subsequent requests use these defaults
response1 = session.get("https://api.example.com/users")
response2 = session.get("https://api.example.com/products")
response3 = session.post("https://api.example.com/orders", json={"item": "book"})
```

### Session with Authentication Token

```python
class APIClient:
    """Reusable API client using requests session."""
    
    def __init__(self, base_url, token=None):
        self.base_url = base_url
        self.session = requests.Session()
        self.session.headers.update({
            "Content-Type": "application/json",
            "Accept": "application/json"
        })
        if token:
            self.session.headers["Authorization"] = f"Bearer {token}"
    
    def get(self, endpoint, **kwargs):
        return self.session.get(f"{self.base_url}{endpoint}", **kwargs)
    
    def post(self, endpoint, data=None, **kwargs):
        return self.session.post(f"{self.base_url}{endpoint}", json=data, **kwargs)
    
    def put(self, endpoint, data=None, **kwargs):
        return self.session.put(f"{self.base_url}{endpoint}", json=data, **kwargs)
    
    def delete(self, endpoint, **kwargs):
        return self.session.delete(f"{self.base_url}{endpoint}", **kwargs)
    
    def close(self):
        self.session.close()

# Usage
client = APIClient("https://api.example.com", token="your-token")
users = client.get("/users").json()
new_user = client.post("/users", {"name": "John"}).json()
client.close()
```

### Session as Context Manager

```python
with requests.Session() as session:
    session.headers["Authorization"] = "Bearer token123"
    
    # Make multiple requests
    response1 = session.get("https://api.example.com/users")
    response2 = session.get("https://api.example.com/products")
    
# Session automatically closed after with block
```

### Cookie Handling

```python
session = requests.Session()

# Login (cookies automatically stored)
session.post(
    "https://api.example.com/login",
    json={"username": "user", "password": "pass"}
)

# Subsequent requests include cookies
response = session.get("https://api.example.com/profile")

# Access cookies
print(session.cookies.get_dict())
```

## Timeout Configuration

### Setting Timeouts

```python
# Single timeout (applies to both connect and read)
response = requests.get(
    "https://api.example.com/users",
    timeout=10  # 10 seconds
)

# Separate connect and read timeouts
response = requests.get(
    "https://api.example.com/users",
    timeout=(3, 10)  # Connect: 3 seconds, Read: 10 seconds
)

# No timeout (not recommended)
response = requests.get(
    "https://api.example.com/users",
    timeout=None
)
```

### Session-Level Timeout

```python
from requests.adapters import HTTPAdapter

class TimeoutHTTPAdapter(HTTPAdapter):
    def __init__(self, timeout, *args, **kwargs):
        self.timeout = timeout
        super().__init__(*args, **kwargs)
    
    def send(self, request, **kwargs):
        kwargs["timeout"] = kwargs.get("timeout") or self.timeout
        return super().send(request, **kwargs)

# Apply to session
session = requests.Session()
adapter = TimeoutHTTPAdapter(timeout=10)
session.mount("http://", adapter)
session.mount("https://", adapter)
```

## Error Handling

### Exception Types

```python
from requests.exceptions import (
    RequestException,      # Base exception
    ConnectionError,       # Network problem
    HTTPError,            # HTTP error status
    URLRequired,          # Invalid URL
    TooManyRedirects,     # Exceeded redirects
    ConnectTimeout,       # Connection timeout
    ReadTimeout,          # Read timeout
    Timeout              # Any timeout
)
```

### Handling Exceptions

```python
import requests
from requests.exceptions import RequestException, Timeout, ConnectionError

def make_api_request(url):
    """Make API request with comprehensive error handling."""
    try:
        response = requests.get(url, timeout=10)
        response.raise_for_status()  # Raises HTTPError for 4xx/5xx
        return response.json()
        
    except ConnectionError:
        print(f"Failed to connect to {url}")
        raise
    except Timeout:
        print(f"Request to {url} timed out")
        raise
    except requests.exceptions.HTTPError as e:
        print(f"HTTP error: {e.response.status_code}")
        raise
    except RequestException as e:
        print(f"Request failed: {e}")
        raise

# Using raise_for_status()
response = requests.get("https://api.example.com/users")
response.raise_for_status()  # Raises HTTPError if status >= 400
```

### Response Status Checking

```python
response = requests.get("https://api.example.com/users")

# Check success
if response.ok:  # True for 2xx status codes
    data = response.json()
else:
    print(f"Error: {response.status_code}")

# Check specific status
if response.status_code == 200:
    # Success
    pass
elif response.status_code == 404:
    # Not found
    pass
elif response.status_code >= 500:
    # Server error
    pass
```

## Advanced Features

### Retry Configuration

```python
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

# Configure retry strategy
retry_strategy = Retry(
    total=3,                    # Total retries
    backoff_factor=1,           # Wait 1, 2, 4 seconds between retries
    status_forcelist=[500, 502, 503, 504],  # Retry on these status codes
    allowed_methods=["GET", "POST"]  # Methods to retry
)

adapter = HTTPAdapter(max_retries=retry_strategy)

session = requests.Session()
session.mount("https://", adapter)
session.mount("http://", adapter)

# Requests will now automatically retry on failure
response = session.get("https://api.example.com/flaky-endpoint")
```

### SSL Certificate Handling

```python
# Verify SSL (default)
response = requests.get("https://api.example.com", verify=True)

# Skip SSL verification (not recommended for production)
response = requests.get("https://api.example.com", verify=False)

# Custom CA bundle
response = requests.get("https://api.example.com", verify="/path/to/ca-bundle.crt")

# Client certificate
response = requests.get(
    "https://api.example.com",
    cert=("/path/to/client.cert", "/path/to/client.key")
)
```

### Redirects

```python
# Follow redirects (default)
response = requests.get("https://api.example.com/redirect", allow_redirects=True)

# Don't follow redirects
response = requests.get("https://api.example.com/redirect", allow_redirects=False)

# Check redirect history
print(response.history)  # List of Response objects from redirects
print(response.url)      # Final URL after redirects
```

### Proxies

```python
proxies = {
    "http": "http://proxy.example.com:8080",
    "https": "http://proxy.example.com:8080"
}

response = requests.get("https://api.example.com", proxies=proxies)
```

## Complete Example

```python
"""
Complete API testing example using requests.
"""
import requests
from requests.exceptions import RequestException
import os

class APITestClient:
    """A comprehensive API test client."""
    
    def __init__(self, base_url=None, token=None):
        self.base_url = base_url or os.getenv("API_BASE_URL", "https://api.example.com")
        self.session = requests.Session()
        
        # Default headers
        self.session.headers.update({
            "Content-Type": "application/json",
            "Accept": "application/json",
            "User-Agent": "APITestClient/1.0"
        })
        
        # Authentication
        if token:
            self.session.headers["Authorization"] = f"Bearer {token}"
    
    def request(self, method, endpoint, **kwargs):
        """Make a request with error handling."""
        url = f"{self.base_url}{endpoint}"
        
        try:
            response = self.session.request(method, url, timeout=30, **kwargs)
            response.raise_for_status()
            return response
        except RequestException as e:
            print(f"Request failed: {method} {url}")
            print(f"Error: {e}")
            raise
    
    def get(self, endpoint, params=None):
        return self.request("GET", endpoint, params=params)
    
    def post(self, endpoint, data=None):
        return self.request("POST", endpoint, json=data)
    
    def put(self, endpoint, data=None):
        return self.request("PUT", endpoint, json=data)
    
    def patch(self, endpoint, data=None):
        return self.request("PATCH", endpoint, json=data)
    
    def delete(self, endpoint):
        return self.request("DELETE", endpoint)


# Usage in tests
def test_user_crud():
    """Test complete user CRUD operations."""
    client = APITestClient(token="test-token")
    
    # Create
    create_response = client.post("/users", {
        "name": "Test User",
        "email": f"test_{int(time.time())}@example.com"
    })
    assert create_response.status_code == 201
    user_id = create_response.json()["id"]
    
    # Read
    get_response = client.get(f"/users/{user_id}")
    assert get_response.status_code == 200
    assert get_response.json()["id"] == user_id
    
    # Update
    update_response = client.patch(f"/users/{user_id}", {"name": "Updated"})
    assert update_response.status_code == 200
    assert update_response.json()["name"] == "Updated"
    
    # Delete
    delete_response = client.delete(f"/users/{user_id}")
    assert delete_response.status_code in [200, 204]


if __name__ == "__main__":
    test_user_crud()
    print("All tests passed!")
```

## Summary

- **requests** is Python's most popular HTTP library, designed for simplicity
- **HTTP methods** (GET, POST, PUT, PATCH, DELETE) map directly to function names
- **Sessions** persist configuration and cookies across multiple requests
- **Authentication** supports Basic, Bearer, API Key, and custom schemes
- **Timeout configuration** prevents hanging requests
- **Exception handling** catches network and HTTP errors gracefully
- **Advanced features** include retries, SSL handling, and proxy support

In the next lesson, you'll learn to consume API endpoints effectively and handle response data for validation in your tests.

## Additional Resources

- [Requests Documentation](https://docs.python-requests.org/en/latest/) - Official documentation
- [Requests Quickstart](https://docs.python-requests.org/en/latest/user/quickstart/) - Getting started guide
- [Advanced Usage](https://docs.python-requests.org/en/latest/user/advanced/) - Advanced features

