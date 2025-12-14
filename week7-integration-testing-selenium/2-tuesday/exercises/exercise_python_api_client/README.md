# Lab: Python API Client with Requests

## Overview

**Duration:** 60-75 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll create a Python API client using the `requests` library. You'll implement CRUD operations, handle responses, and build a reusable client class.

---

## Learning Objectives

By completing this lab, you will:
- Use Python's requests library for API calls
- Handle JSON responses and error conditions
- Build a reusable API client class
- Implement proper error handling
- Create session-based requests

---

## Prerequisites

- Python 3.8+ installed
- Understanding of Python basics (from Week 1)
- Basic API testing concepts

---

## The Scenario

The BookHaven team wants Python-based API utilities for their test automation suite. Python's readability and speed make it ideal for quick API validations and data setup scripts. Your task is to build a robust API client.

---

## Core Tasks

### Task 1: Project Setup (10 minutes)

**Create project structure:**

```
python-api-lab/
├── requirements.txt
├── api_client.py
├── test_api.py
└── config.py
```

**Create `requirements.txt`:**
```
requests==2.31.0
pytest==7.4.0
python-dotenv==1.0.0
```

**Install dependencies:**
```bash
pip install -r requirements.txt
```

**Create `config.py`:**
```python
"""Configuration for API testing."""

BASE_URL = "https://jsonplaceholder.typicode.com"
DEFAULT_TIMEOUT = 30
DEFAULT_HEADERS = {
    "Content-Type": "application/json",
    "Accept": "application/json"
}
```

### Task 2: Basic Requests (20 minutes)

**Create `api_basics.py`:**

```python
"""Basic API operations using requests library."""

import requests
import json

BASE_URL = "https://jsonplaceholder.typicode.com"


def get_all_posts():
    """GET all posts."""
    response = requests.get(f"{BASE_URL}/posts")
    
    # Print response details
    print(f"Status Code: {response.status_code}")
    print(f"Response Time: {response.elapsed.total_seconds()}s")
    print(f"Content-Type: {response.headers.get('Content-Type')}")
    print(f"Number of posts: {len(response.json())}")
    
    return response


def get_post_by_id(post_id):
    """GET single post by ID."""
    response = requests.get(f"{BASE_URL}/posts/{post_id}")
    
    if response.status_code == 200:
        post = response.json()
        print(f"Post {post_id}: {post['title'][:50]}...")
    else:
        print(f"Error: {response.status_code}")
    
    return response


def get_posts_by_user(user_id):
    """GET posts filtered by user ID using query params."""
    params = {"userId": user_id}
    response = requests.get(f"{BASE_URL}/posts", params=params)
    
    posts = response.json()
    print(f"User {user_id} has {len(posts)} posts")
    
    return response


def create_post(title, body, user_id):
    """POST create new post."""
    payload = {
        "title": title,
        "body": body,
        "userId": user_id
    }
    
    headers = {"Content-Type": "application/json"}
    
    response = requests.post(
        f"{BASE_URL}/posts",
        json=payload,  # Automatically serializes to JSON
        headers=headers
    )
    
    if response.status_code == 201:
        created = response.json()
        print(f"Created post with ID: {created['id']}")
    
    return response


def update_post(post_id, title, body, user_id):
    """PUT update entire post."""
    payload = {
        "id": post_id,
        "title": title,
        "body": body,
        "userId": user_id
    }
    
    response = requests.put(
        f"{BASE_URL}/posts/{post_id}",
        json=payload
    )
    
    return response


def delete_post(post_id):
    """DELETE remove post."""
    response = requests.delete(f"{BASE_URL}/posts/{post_id}")
    
    print(f"Delete status: {response.status_code}")
    return response


# Run basic tests
if __name__ == "__main__":
    print("=== GET All Posts ===")
    get_all_posts()
    
    print("\n=== GET Post by ID ===")
    get_post_by_id(1)
    
    print("\n=== GET Posts by User ===")
    get_posts_by_user(1)
    
    print("\n=== CREATE Post ===")
    create_post("Test Title", "Test body content", 1)
    
    print("\n=== UPDATE Post ===")
    update_post(1, "Updated Title", "Updated body", 1)
    
    print("\n=== DELETE Post ===")
    delete_post(1)
```

**Your Tasks:**
1. Run the script: `python api_basics.py`
2. Add a PATCH request function for partial updates
3. Add timeout handling to all requests

### Task 3: Build Reusable API Client Class (25 minutes)

**Create `api_client.py`:**

```python
"""Reusable API Client for BookHaven."""

import requests
from typing import Optional, Dict, Any, List
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class APIClient:
    """A reusable API client with session management."""

    def __init__(self, base_url: str, timeout: int = 30):
        """Initialize the API client.
        
        Args:
            base_url: Base URL for all API requests
            timeout: Default timeout in seconds
        """
        self.base_url = base_url.rstrip('/')
        self.timeout = timeout
        self.session = requests.Session()
        
        # Set default headers
        self.session.headers.update({
            "Content-Type": "application/json",
            "Accept": "application/json"
        })

    def _build_url(self, endpoint: str) -> str:
        """Build full URL from endpoint."""
        endpoint = endpoint.lstrip('/')
        return f"{self.base_url}/{endpoint}"

    def _handle_response(self, response: requests.Response) -> Dict[str, Any]:
        """Handle response and return parsed JSON or raise exception."""
        try:
            response.raise_for_status()
            return response.json() if response.text else {}
        except requests.exceptions.HTTPError as e:
            logger.error(f"HTTP Error: {e}")
            logger.error(f"Response: {response.text}")
            raise
        except requests.exceptions.JSONDecodeError:
            logger.warning("Response is not JSON")
            return {"raw": response.text}

    def get(
        self,
        endpoint: str,
        params: Optional[Dict[str, Any]] = None
    ) -> requests.Response:
        """Send GET request.
        
        Args:
            endpoint: API endpoint
            params: Query parameters
            
        Returns:
            Response object
        """
        url = self._build_url(endpoint)
        logger.info(f"GET {url}")
        
        response = self.session.get(
            url,
            params=params,
            timeout=self.timeout
        )
        
        logger.info(f"Response: {response.status_code}")
        return response

    def post(
        self,
        endpoint: str,
        data: Optional[Dict[str, Any]] = None
    ) -> requests.Response:
        """Send POST request.
        
        Args:
            endpoint: API endpoint
            data: Request body data
            
        Returns:
            Response object
        """
        url = self._build_url(endpoint)
        logger.info(f"POST {url}")
        
        response = self.session.post(
            url,
            json=data,
            timeout=self.timeout
        )
        
        logger.info(f"Response: {response.status_code}")
        return response

    def put(
        self,
        endpoint: str,
        data: Optional[Dict[str, Any]] = None
    ) -> requests.Response:
        """Send PUT request."""
        url = self._build_url(endpoint)
        logger.info(f"PUT {url}")
        
        response = self.session.put(
            url,
            json=data,
            timeout=self.timeout
        )
        
        logger.info(f"Response: {response.status_code}")
        return response

    def patch(
        self,
        endpoint: str,
        data: Optional[Dict[str, Any]] = None
    ) -> requests.Response:
        """Send PATCH request."""
        url = self._build_url(endpoint)
        logger.info(f"PATCH {url}")
        
        response = self.session.patch(
            url,
            json=data,
            timeout=self.timeout
        )
        
        logger.info(f"Response: {response.status_code}")
        return response

    def delete(self, endpoint: str) -> requests.Response:
        """Send DELETE request."""
        url = self._build_url(endpoint)
        logger.info(f"DELETE {url}")
        
        response = self.session.delete(url, timeout=self.timeout)
        
        logger.info(f"Response: {response.status_code}")
        return response

    def set_auth_token(self, token: str) -> None:
        """Set authorization header."""
        self.session.headers["Authorization"] = f"Bearer {token}"

    def close(self) -> None:
        """Close the session."""
        self.session.close()


class PostsAPI:
    """Posts-specific API operations."""

    def __init__(self, client: APIClient):
        self.client = client
        self.endpoint = "/posts"

    def get_all(self) -> List[Dict[str, Any]]:
        """Get all posts."""
        response = self.client.get(self.endpoint)
        return response.json()

    def get_by_id(self, post_id: int) -> Dict[str, Any]:
        """Get post by ID."""
        response = self.client.get(f"{self.endpoint}/{post_id}")
        return response.json()

    def get_by_user(self, user_id: int) -> List[Dict[str, Any]]:
        """Get posts by user ID."""
        response = self.client.get(self.endpoint, params={"userId": user_id})
        return response.json()

    def create(
        self,
        title: str,
        body: str,
        user_id: int
    ) -> Dict[str, Any]:
        """Create new post."""
        data = {"title": title, "body": body, "userId": user_id}
        response = self.client.post(self.endpoint, data)
        return response.json()

    def update(
        self,
        post_id: int,
        title: str,
        body: str,
        user_id: int
    ) -> Dict[str, Any]:
        """Update post."""
        data = {"id": post_id, "title": title, "body": body, "userId": user_id}
        response = self.client.put(f"{self.endpoint}/{post_id}", data)
        return response.json()

    def delete(self, post_id: int) -> bool:
        """Delete post."""
        response = self.client.delete(f"{self.endpoint}/{post_id}")
        return response.status_code == 200


# Example usage
if __name__ == "__main__":
    # Create client
    client = APIClient("https://jsonplaceholder.typicode.com")
    posts = PostsAPI(client)

    # Use high-level API
    print("=== Get All Posts ===")
    all_posts = posts.get_all()
    print(f"Total posts: {len(all_posts)}")

    print("\n=== Get Post by ID ===")
    post = posts.get_by_id(1)
    print(f"Post: {post['title']}")

    print("\n=== Create Post ===")
    new_post = posts.create(
        title="New Post via API Client",
        body="This is created using our reusable client",
        user_id=1
    )
    print(f"Created: {new_post}")

    # Cleanup
    client.close()
```

**Your Tasks:**
1. Run and verify the client works
2. Add a `UsersAPI` class similar to `PostsAPI`
3. Add retry logic for failed requests

### Task 4: Error Handling (15 minutes)

**Add to `api_client.py`:**

```python
class APIError(Exception):
    """Custom API Error."""
    def __init__(self, status_code: int, message: str, response: Optional[requests.Response] = None):
        self.status_code = status_code
        self.message = message
        self.response = response
        super().__init__(f"{status_code}: {message}")


def make_request_with_retry(
    func,
    *args,
    max_retries: int = 3,
    backoff_factor: float = 0.5,
    **kwargs
) -> requests.Response:
    """Make request with retry logic.
    
    Args:
        func: Request function to call
        max_retries: Maximum number of retries
        backoff_factor: Delay multiplier between retries
    """
    import time
    
    last_exception = None
    
    for attempt in range(max_retries):
        try:
            response = func(*args, **kwargs)
            
            # Retry on server errors
            if response.status_code >= 500:
                raise APIError(
                    response.status_code,
                    "Server error",
                    response
                )
            
            return response
            
        except (requests.exceptions.Timeout, 
                requests.exceptions.ConnectionError,
                APIError) as e:
            last_exception = e
            wait_time = backoff_factor * (2 ** attempt)
            logger.warning(f"Attempt {attempt + 1} failed: {e}")
            logger.info(f"Retrying in {wait_time}s...")
            time.sleep(wait_time)
    
    raise last_exception


# Error handling example
def safe_get_post(post_id: int) -> Optional[Dict[str, Any]]:
    """Safely get post with error handling."""
    client = APIClient("https://jsonplaceholder.typicode.com")
    
    try:
        response = client.get(f"/posts/{post_id}")
        
        if response.status_code == 404:
            logger.warning(f"Post {post_id} not found")
            return None
        
        response.raise_for_status()
        return response.json()
        
    except requests.exceptions.Timeout:
        logger.error("Request timed out")
        return None
        
    except requests.exceptions.ConnectionError:
        logger.error("Connection failed")
        return None
        
    except requests.exceptions.HTTPError as e:
        logger.error(f"HTTP error: {e}")
        return None
        
    finally:
        client.close()
```

**Your Tasks:**
1. Test the error handling with invalid URLs
2. Add validation for response data
3. Create a context manager version of the client

---

## Definition of Done

Your lab is complete when you have:

- [ ] Basic requests working (GET, POST, PUT, DELETE)
- [ ] Reusable APIClient class implemented
- [ ] PostsAPI high-level class working
- [ ] Error handling with retry logic
- [ ] All scripts run without errors
- [ ] Code follows Python best practices

---

## Starter Code

Find complete starter code in the `starter_code/` directory.

---

## Challenge Tasks (Optional)

### 1. Context Manager Support
```python
class APIClient:
    def __enter__(self):
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()
        return False

# Usage
with APIClient("https://api.example.com") as client:
    response = client.get("/posts")
```

### 2. Async Support
```python
import aiohttp
import asyncio

async def get_posts_async():
    async with aiohttp.ClientSession() as session:
        async with session.get("https://jsonplaceholder.typicode.com/posts") as resp:
            return await resp.json()
```

---

## Submission Checklist

| Item | Completed |
|------|-----------|
| Basic requests (all methods) | ☐ |
| APIClient class | ☐ |
| PostsAPI class | ☐ |
| UsersAPI class | ☐ |
| Error handling | ☐ |
| Retry logic | ☐ |
| Code runs without errors | ☐ |

---

## Additional Resources

- Written Content: `python-api-testing.md`, `requests-module.md`, `consuming-endpoints.md`
- [Requests Documentation](https://docs.python-requests.org/)
- [Python Type Hints](https://docs.python.org/3/library/typing.html)

