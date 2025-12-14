# API Testing in Python

## Learning Objectives
- Understand the Python ecosystem for API testing
- Compare Python and Java approaches to API testing
- Identify when to choose Python for API testing projects
- Recognize the key libraries and tools available in Python

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, versatility is a superpower. While Java with REST Assured provides enterprise-grade API testing, Python offers a different set of strengths: rapid prototyping, readable syntax, and seamless integration with data science and automation tools.

Many organizations use Python for API testing due to its gentler learning curve and powerful libraries. Your Python fundamentals from Week 1, combined with pytest knowledge from Week 6, prepare you perfectly for Python-based API testing. By the end of today, you'll have two complete toolkits for API automation.

## The Python API Testing Ecosystem

### Overview

Python's API testing ecosystem is built around several key components:

```
Python API Testing Stack:
┌─────────────────────────────────────────────────────────────┐
│                    Test Frameworks                           │
│              pytest  |  unittest  |  nose2                   │
├─────────────────────────────────────────────────────────────┤
│                    HTTP Libraries                            │
│        requests  |  httpx  |  aiohttp  |  urllib3           │
├─────────────────────────────────────────────────────────────┤
│                   Assertion Libraries                        │
│         pytest assertions  |  assertpy  |  hamcrest         │
├─────────────────────────────────────────────────────────────┤
│                   Data Handling                              │
│            json  |  jsonschema  |  pydantic                 │
├─────────────────────────────────────────────────────────────┤
│                   Reporting                                  │
│            pytest-html  |  allure-pytest  |  pytest-cov     │
└─────────────────────────────────────────────────────────────┘
```

### Core Libraries

| Library | Purpose | Key Features |
|---------|---------|--------------|
| **requests** | HTTP client | Simple API, session management, auth |
| **pytest** | Test framework | Fixtures, parametrization, plugins |
| **jsonschema** | Schema validation | JSON Schema draft support |
| **pydantic** | Data validation | Type hints, serialization |
| **httpx** | Async HTTP | Modern, async/await support |
| **responses** | Mocking | Mock HTTP responses |

### Why Python for API Testing?

**Strengths:**

```
✓ Readable Syntax
  Python code reads almost like English, making tests
  easy to write and review.

✓ Rapid Development
  Less boilerplate means faster test creation.
  
✓ Rich Ecosystem
  Thousands of packages for any testing need.

✓ Cross-Platform
  Same code runs on Windows, Mac, Linux.

✓ Integration Friendly
  Easy to integrate with CI/CD, data tools, scripts.

✓ Lower Barrier
  Gentle learning curve for QA engineers.
```

**Best Use Cases:**

- Rapid prototyping and exploration
- Data-driven testing with CSV/JSON files
- Integration with data science tools
- Quick automation scripts
- Cross-functional teams with varied backgrounds
- Microservices testing
- DevOps and infrastructure testing

## Comparing Python vs Java for API Testing

### Code Comparison

**Same Test in Both Languages:**

**Python with requests:**
```python
import requests
import pytest

def test_get_user():
    """Test retrieving a user by ID."""
    response = requests.get(
        "https://api.example.com/users/123",
        headers={"Authorization": "Bearer token123"}
    )
    
    assert response.status_code == 200
    data = response.json()
    assert data["id"] == 123
    assert data["name"] is not None
```

**Java with REST Assured:**
```java
@Test
void testGetUser() {
    given()
        .baseUri("https://api.example.com")
        .header("Authorization", "Bearer token123")
    .when()
        .get("/users/123")
    .then()
        .statusCode(200)
        .body("id", equalTo(123))
        .body("name", notNullValue());
}
```

### Feature Comparison

| Feature | Python + requests | Java + REST Assured |
|---------|-------------------|---------------------|
| **Lines of Code** | Fewer | More |
| **Type Safety** | Dynamic (runtime) | Static (compile-time) |
| **IDE Support** | Good | Excellent |
| **Refactoring** | Manual | Automated |
| **Build System** | pip/poetry | Maven/Gradle |
| **Dependency Mgmt** | requirements.txt | pom.xml |
| **Learning Curve** | Lower | Higher |
| **Enterprise Adoption** | Growing | Established |
| **CI/CD Integration** | Easy | Easy |
| **Performance** | Good | Better |
| **Async Support** | Native | Complex |

### Syntax Philosophy

**Python Philosophy:**
```python
# Explicit is better than implicit
# Simple is better than complex
# Readability counts

response = requests.get(url)
data = response.json()
assert data["status"] == "success"
```

**Java (REST Assured) Philosophy:**
```java
// Fluent, chainable API
// BDD-style Given-When-Then
// Strong typing throughout

given()
    .spec(requestSpec)
.when()
    .get(url)
.then()
    .body("status", equalTo("success"));
```

## When to Use Python for API Tests

### Choose Python When:

```
┌─────────────────────────────────────────────────────────────┐
│ ✓ Quick Prototyping                                          │
│   Need to validate an API quickly before formal automation   │
├─────────────────────────────────────────────────────────────┤
│ ✓ Data-Heavy Testing                                         │
│   Processing CSV, Excel, JSON data files for tests           │
├─────────────────────────────────────────────────────────────┤
│ ✓ Cross-Functional Teams                                     │
│   Team includes data scientists, DevOps, non-Java developers │
├─────────────────────────────────────────────────────────────┤
│ ✓ Infrastructure Testing                                     │
│   AWS, Docker, Kubernetes API interactions                   │
├─────────────────────────────────────────────────────────────┤
│ ✓ Existing Python Codebase                                   │
│   Application under test is Python-based                     │
├─────────────────────────────────────────────────────────────┤
│ ✓ Scripting & Automation                                     │
│   Beyond just testing - data extraction, setup, cleanup      │
└─────────────────────────────────────────────────────────────┘
```

### Choose Java When:

```
┌─────────────────────────────────────────────────────────────┐
│ ✓ Enterprise Environment                                     │
│   Organization standardized on Java ecosystem                │
├─────────────────────────────────────────────────────────────┤
│ ✓ Type Safety Critical                                       │
│   Complex test data requiring compile-time checks            │
├─────────────────────────────────────────────────────────────┤
│ ✓ IDE Refactoring                                            │
│   Large test suite requiring automated refactoring           │
├─────────────────────────────────────────────────────────────┤
│ ✓ Same Language as App                                       │
│   Java application - easier for devs to contribute           │
├─────────────────────────────────────────────────────────────┤
│ ✓ Mature CI/CD                                               │
│   Existing Maven/Gradle pipelines                            │
└─────────────────────────────────────────────────────────────┘
```

## Python Testing Stack Setup

### Project Structure

```
api-tests/
├── requirements.txt
├── pytest.ini
├── conftest.py
├── tests/
│   ├── __init__.py
│   ├── test_users.py
│   ├── test_products.py
│   └── test_orders.py
├── utils/
│   ├── __init__.py
│   ├── api_client.py
│   └── data_helpers.py
├── testdata/
│   ├── users.json
│   └── products.csv
└── schemas/
    ├── user_schema.json
    └── product_schema.json
```

### requirements.txt

```
# HTTP Client
requests>=2.31.0

# Testing Framework
pytest>=7.4.0
pytest-html>=4.0.0
pytest-xdist>=3.3.0  # Parallel execution

# Data Validation
jsonschema>=4.19.0
pydantic>=2.4.0

# Reporting
allure-pytest>=2.13.0

# Utilities
python-dotenv>=1.0.0
faker>=19.0.0
```

### Installation

```bash
# Create virtual environment
python -m venv venv

# Activate (Windows)
venv\Scripts\activate

# Activate (Mac/Linux)
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt
```

### pytest.ini Configuration

```ini
[pytest]
testpaths = tests
python_files = test_*.py
python_classes = Test*
python_functions = test_*
addopts = -v --tb=short
markers =
    smoke: Quick validation tests
    regression: Full regression tests
    api: API tests
```

### conftest.py (Shared Fixtures)

```python
import pytest
import requests
import os

@pytest.fixture(scope="session")
def base_url():
    """Return the base URL for API tests."""
    return os.getenv("API_BASE_URL", "https://api.example.com")

@pytest.fixture(scope="session")
def api_session(base_url):
    """Create a requests session with default configuration."""
    session = requests.Session()
    session.headers.update({
        "Content-Type": "application/json",
        "Accept": "application/json",
    })
    return session

@pytest.fixture(scope="session")
def auth_token():
    """Get authentication token."""
    return os.getenv("API_TOKEN", "test-token")

@pytest.fixture
def auth_headers(auth_token):
    """Return authorization headers."""
    return {"Authorization": f"Bearer {auth_token}"}
```

## Basic Test Example

```python
# tests/test_users.py
import pytest
import requests

class TestUserAPI:
    """Tests for User API endpoints."""
    
    @pytest.fixture(autouse=True)
    def setup(self, base_url, auth_headers):
        """Set up test fixtures."""
        self.base_url = base_url
        self.headers = auth_headers
    
    def test_get_users_returns_list(self):
        """GET /users should return a list of users."""
        response = requests.get(
            f"{self.base_url}/users",
            headers=self.headers
        )
        
        assert response.status_code == 200
        data = response.json()
        assert isinstance(data["users"], list)
        assert len(data["users"]) > 0
    
    def test_get_user_by_id(self):
        """GET /users/{id} should return user details."""
        user_id = 123
        response = requests.get(
            f"{self.base_url}/users/{user_id}",
            headers=self.headers
        )
        
        assert response.status_code == 200
        user = response.json()
        assert user["id"] == user_id
        assert "name" in user
        assert "email" in user
    
    def test_create_user(self):
        """POST /users should create a new user."""
        user_data = {
            "name": "Test User",
            "email": "test@example.com"
        }
        
        response = requests.post(
            f"{self.base_url}/users",
            json=user_data,
            headers=self.headers
        )
        
        assert response.status_code == 201
        created_user = response.json()
        assert created_user["id"] is not None
        assert created_user["name"] == user_data["name"]
```

## Running Tests

```bash
# Run all tests
pytest

# Run with verbose output
pytest -v

# Run specific file
pytest tests/test_users.py

# Run specific test
pytest tests/test_users.py::TestUserAPI::test_get_users_returns_list

# Run by marker
pytest -m smoke

# Run in parallel
pytest -n 4

# Generate HTML report
pytest --html=report.html

# Generate Allure report
pytest --alluredir=allure-results
allure serve allure-results
```

## Integration with pytest Fixtures

```python
# Advanced fixture usage
import pytest
import requests

@pytest.fixture
def created_user(base_url, auth_headers):
    """Create a user for testing and clean up after."""
    # Setup - create user
    user_data = {
        "name": "Fixture User",
        "email": f"fixture_{int(time.time())}@example.com"
    }
    
    response = requests.post(
        f"{base_url}/users",
        json=user_data,
        headers=auth_headers
    )
    user = response.json()
    
    yield user  # Provide user to test
    
    # Teardown - delete user
    requests.delete(
        f"{base_url}/users/{user['id']}",
        headers=auth_headers
    )

def test_update_created_user(base_url, auth_headers, created_user):
    """Test updating the fixture-created user."""
    response = requests.patch(
        f"{base_url}/users/{created_user['id']}",
        json={"name": "Updated Name"},
        headers=auth_headers
    )
    
    assert response.status_code == 200
    assert response.json()["name"] == "Updated Name"
```

## Summary

- **Python's API testing ecosystem** offers simplicity, readability, and powerful libraries
- **Key libraries** include requests (HTTP), pytest (testing), and jsonschema (validation)
- **Python excels** at rapid prototyping, data-driven testing, and cross-functional teams
- **Java excels** at enterprise environments, type safety, and established CI/CD pipelines
- **Both languages** are valuable—choose based on project context and team skills
- Your **Week 1 Python skills** and **Week 6 pytest knowledge** provide a strong foundation

In the next lesson, you'll dive deep into the requests module—Python's most popular HTTP library.

## Additional Resources

- [Python requests Documentation](https://docs.python-requests.org/) - Official requests library docs
- [pytest Documentation](https://docs.pytest.org/) - pytest framework reference
- [Real Python: API Integration in Python](https://realpython.com/api-integration-in-python/) - Comprehensive tutorial

