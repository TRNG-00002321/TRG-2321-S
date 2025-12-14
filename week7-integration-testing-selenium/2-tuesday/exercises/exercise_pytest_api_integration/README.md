# Lab: Pytest API Integration

## Overview

**Duration:** 60-75 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll integrate your Python API client with pytest to create a comprehensive test suite. You'll use fixtures, parametrization, and markers to build maintainable API tests.

---

## Learning Objectives

By completing this lab, you will:
- Create pytest fixtures for API testing
- Write parameterized API tests
- Use pytest markers for test organization
- Generate test reports
- Implement test data management

---

## Prerequisites

- Completed "Python API Client" exercise
- Understanding of pytest (from Week 6)
- API client code ready

---

## The Scenario

Now that you have a working Python API client, the BookHaven team wants a full pytest test suite. The tests should be organized, reusable, and produce clear reports for the CI/CD pipeline.

---

## Core Tasks

### Task 1: Project Structure Setup (10 minutes)

**Create project structure:**

```
pytest-api-lab/
├── requirements.txt
├── pytest.ini
├── conftest.py
├── api/
│   ├── __init__.py
│   └── client.py
├── tests/
│   ├── __init__.py
│   ├── test_posts.py
│   ├── test_users.py
│   └── test_comments.py
└── test_data/
    └── posts.json
```

**Create `pytest.ini`:**
```ini
[pytest]
testpaths = tests
python_files = test_*.py
python_classes = Test*
python_functions = test_*
addopts = -v --tb=short
markers =
    smoke: Quick validation tests
    regression: Full regression suite
    posts: Post endpoint tests
    users: User endpoint tests
    slow: Tests that take longer to run
```

**Create `requirements.txt`:**
```
requests==2.31.0
pytest==7.4.0
pytest-html==4.1.0
pytest-xdist==3.3.0
python-dotenv==1.0.0
```

### Task 2: Create Fixtures in conftest.py (15 minutes)

**Create `conftest.py`:**

```python
"""Pytest fixtures for API testing."""

import pytest
import requests
from typing import Generator, Dict, Any

BASE_URL = "https://jsonplaceholder.typicode.com"


class APIClient:
    """Simple API client for tests."""
    
    def __init__(self, base_url: str):
        self.base_url = base_url
        self.session = requests.Session()
        self.session.headers.update({
            "Content-Type": "application/json",
            "Accept": "application/json"
        })
    
    def get(self, endpoint: str, **kwargs):
        return self.session.get(f"{self.base_url}{endpoint}", **kwargs)
    
    def post(self, endpoint: str, json=None, **kwargs):
        return self.session.post(f"{self.base_url}{endpoint}", json=json, **kwargs)
    
    def put(self, endpoint: str, json=None, **kwargs):
        return self.session.put(f"{self.base_url}{endpoint}", json=json, **kwargs)
    
    def delete(self, endpoint: str, **kwargs):
        return self.session.delete(f"{self.base_url}{endpoint}", **kwargs)
    
    def close(self):
        self.session.close()


@pytest.fixture(scope="session")
def api_client() -> Generator[APIClient, None, None]:
    """Create API client for entire test session."""
    client = APIClient(BASE_URL)
    yield client
    client.close()


@pytest.fixture(scope="function")
def new_post_data() -> Dict[str, Any]:
    """Generate unique test post data."""
    import time
    return {
        "title": f"Test Post {time.time()}",
        "body": "This is a test post body",
        "userId": 1
    }


@pytest.fixture(scope="module")
def created_post(api_client) -> Dict[str, Any]:
    """Create a post and return its data."""
    post_data = {
        "title": "Fixture Created Post",
        "body": "Created by pytest fixture",
        "userId": 1
    }
    response = api_client.post("/posts", json=post_data)
    return response.json()


@pytest.fixture(scope="function")
def cleanup_posts(api_client):
    """Track and cleanup created posts."""
    created_ids = []
    
    yield created_ids
    
    # Cleanup after test
    for post_id in created_ids:
        api_client.delete(f"/posts/{post_id}")


@pytest.fixture
def valid_user_ids():
    """Return valid user IDs for testing."""
    return list(range(1, 11))


@pytest.fixture
def sample_post():
    """Return sample post for validation."""
    return {
        "userId": 1,
        "id": 1,
        "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
        "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
    }
```

### Task 3: Write Post Tests (20 minutes)

**Create `tests/test_posts.py`:**

```python
"""Tests for Posts API endpoints."""

import pytest


@pytest.mark.smoke
@pytest.mark.posts
class TestGetPosts:
    """Tests for GET /posts endpoints."""

    def test_get_all_posts_returns_200(self, api_client):
        """GET /posts should return 200 OK."""
        response = api_client.get("/posts")
        assert response.status_code == 200

    def test_get_all_posts_returns_100_items(self, api_client):
        """GET /posts should return 100 posts."""
        response = api_client.get("/posts")
        posts = response.json()
        assert len(posts) == 100

    def test_get_all_posts_returns_json(self, api_client):
        """GET /posts should return JSON content type."""
        response = api_client.get("/posts")
        assert "application/json" in response.headers["Content-Type"]

    def test_get_post_by_id(self, api_client, sample_post):
        """GET /posts/1 should return specific post."""
        response = api_client.get("/posts/1")
        post = response.json()
        
        assert response.status_code == 200
        assert post["id"] == 1
        assert post["userId"] == sample_post["userId"]

    @pytest.mark.parametrize("post_id", [1, 2, 3, 50, 100])
    def test_get_various_posts_by_id(self, api_client, post_id):
        """GET /posts/{id} should work for various IDs."""
        response = api_client.get(f"/posts/{post_id}")
        
        assert response.status_code == 200
        assert response.json()["id"] == post_id

    def test_get_nonexistent_post_returns_404(self, api_client):
        """GET /posts/999 should return 404."""
        response = api_client.get("/posts/999")
        assert response.status_code == 404

    @pytest.mark.parametrize("user_id,expected_count", [
        (1, 10),
        (2, 10),
        (3, 10),
    ])
    def test_get_posts_by_user(self, api_client, user_id, expected_count):
        """GET /posts?userId={id} should filter correctly."""
        response = api_client.get("/posts", params={"userId": user_id})
        posts = response.json()
        
        assert response.status_code == 200
        assert len(posts) == expected_count
        assert all(p["userId"] == user_id for p in posts)


@pytest.mark.posts
class TestCreatePosts:
    """Tests for POST /posts endpoint."""

    def test_create_post_returns_201(self, api_client, new_post_data):
        """POST /posts should return 201 Created."""
        response = api_client.post("/posts", json=new_post_data)
        assert response.status_code == 201

    def test_create_post_returns_id(self, api_client, new_post_data):
        """POST /posts should return post with ID."""
        response = api_client.post("/posts", json=new_post_data)
        created = response.json()
        
        assert "id" in created
        assert created["id"] is not None

    def test_create_post_echoes_data(self, api_client, new_post_data):
        """POST /posts should echo submitted data."""
        response = api_client.post("/posts", json=new_post_data)
        created = response.json()
        
        assert created["title"] == new_post_data["title"]
        assert created["body"] == new_post_data["body"]
        assert created["userId"] == new_post_data["userId"]


@pytest.mark.posts
class TestUpdatePosts:
    """Tests for PUT/PATCH /posts endpoints."""

    def test_update_post_returns_200(self, api_client):
        """PUT /posts/1 should return 200."""
        update_data = {
            "id": 1,
            "title": "Updated Title",
            "body": "Updated body",
            "userId": 1
        }
        response = api_client.put("/posts/1", json=update_data)
        assert response.status_code == 200

    def test_update_post_reflects_changes(self, api_client):
        """PUT /posts/1 should return updated data."""
        update_data = {
            "id": 1,
            "title": "New Title",
            "body": "New body",
            "userId": 1
        }
        response = api_client.put("/posts/1", json=update_data)
        updated = response.json()
        
        assert updated["title"] == "New Title"
        assert updated["body"] == "New body"


@pytest.mark.posts
class TestDeletePosts:
    """Tests for DELETE /posts endpoint."""

    def test_delete_post_returns_200(self, api_client):
        """DELETE /posts/1 should return 200."""
        response = api_client.delete("/posts/1")
        assert response.status_code == 200


@pytest.mark.regression
@pytest.mark.posts
class TestPostsValidation:
    """Validation tests for post data."""

    def test_all_posts_have_required_fields(self, api_client):
        """All posts should have id, userId, title, body."""
        response = api_client.get("/posts")
        posts = response.json()
        
        required_fields = {"id", "userId", "title", "body"}
        
        for post in posts:
            assert required_fields.issubset(post.keys()), \
                f"Post {post.get('id')} missing required fields"

    def test_all_post_ids_are_unique(self, api_client):
        """All post IDs should be unique."""
        response = api_client.get("/posts")
        posts = response.json()
        
        ids = [p["id"] for p in posts]
        assert len(ids) == len(set(ids)), "Duplicate IDs found"

    def test_all_user_ids_in_valid_range(self, api_client, valid_user_ids):
        """All userIds should be in valid range."""
        response = api_client.get("/posts")
        posts = response.json()
        
        for post in posts:
            assert post["userId"] in valid_user_ids, \
                f"Invalid userId: {post['userId']}"
```

### Task 4: Create User Tests (10 minutes)

**Create `tests/test_users.py`:**

```python
"""Tests for Users API endpoints."""

import pytest


@pytest.mark.smoke
@pytest.mark.users
class TestGetUsers:
    """Tests for GET /users endpoints."""

    def test_get_all_users_returns_200(self, api_client):
        """GET /users should return 200 OK."""
        response = api_client.get("/users")
        assert response.status_code == 200

    def test_get_all_users_returns_10_users(self, api_client):
        """GET /users should return 10 users."""
        response = api_client.get("/users")
        users = response.json()
        assert len(users) == 10

    @pytest.mark.parametrize("user_id", range(1, 11))
    def test_get_user_by_id(self, api_client, user_id):
        """GET /users/{id} should return user."""
        response = api_client.get(f"/users/{user_id}")
        
        assert response.status_code == 200
        assert response.json()["id"] == user_id

    def test_user_has_nested_address(self, api_client):
        """User should have nested address object."""
        response = api_client.get("/users/1")
        user = response.json()
        
        assert "address" in user
        assert "street" in user["address"]
        assert "city" in user["address"]
        assert "geo" in user["address"]

    def test_user_has_company(self, api_client):
        """User should have company object."""
        response = api_client.get("/users/1")
        user = response.json()
        
        assert "company" in user
        assert "name" in user["company"]


@pytest.mark.regression
@pytest.mark.users
class TestUsersValidation:
    """Validation tests for user data."""

    def test_all_users_have_email(self, api_client):
        """All users should have email addresses."""
        response = api_client.get("/users")
        users = response.json()
        
        for user in users:
            assert "email" in user
            assert "@" in user["email"]

    def test_all_users_have_unique_username(self, api_client):
        """All usernames should be unique."""
        response = api_client.get("/users")
        users = response.json()
        
        usernames = [u["username"] for u in users]
        assert len(usernames) == len(set(usernames))
```

### Task 5: Run Tests and Generate Reports (15 minutes)

**Run different test subsets:**

```bash
# Run all tests
pytest

# Run only smoke tests
pytest -m smoke

# Run only post tests
pytest -m posts

# Run regression tests
pytest -m regression

# Run with HTML report
pytest --html=report.html --self-contained-html

# Run parallel
pytest -n auto

# Run with verbose output
pytest -v --tb=long
```

**Your Tasks:**
1. Run the test suite and fix any failures
2. Generate an HTML report
3. Run tests in parallel using pytest-xdist
4. Add more parameterized tests

---

## Definition of Done

Your lab is complete when you have:

- [ ] Fixtures created in conftest.py
- [ ] Post tests covering CRUD operations
- [ ] User tests covering GET operations
- [ ] Parameterized tests for multiple inputs
- [ ] Markers configured and working
- [ ] HTML report generated
- [ ] All tests passing

---

## Challenge Tasks (Optional)

### 1. Test Data from Files
```python
@pytest.fixture
def test_posts():
    import json
    with open("test_data/posts.json") as f:
        return json.load(f)
```

### 2. Custom Assertions
```python
def assert_valid_post(post):
    assert "id" in post
    assert "title" in post
    assert isinstance(post["id"], int)
```

### 3. Allure Integration
```python
import allure

@allure.feature("Posts")
@allure.story("Create Post")
def test_create_post():
    pass
```

---

## Submission Checklist

| Item | Completed |
|------|-----------|
| Project structure created | ☐ |
| conftest.py with fixtures | ☐ |
| test_posts.py implemented | ☐ |
| test_users.py implemented | ☐ |
| Markers configured | ☐ |
| Parameterized tests | ☐ |
| HTML report generated | ☐ |
| All tests passing | ☐ |

---

## Additional Resources

- Written Content: `python-api-testing.md`
- [Pytest Documentation](https://docs.pytest.org/)
- [Pytest-HTML](https://pytest-html.readthedocs.io/)

