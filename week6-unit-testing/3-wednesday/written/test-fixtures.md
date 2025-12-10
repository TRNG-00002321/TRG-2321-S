# Test Fixtures: Pytest's Powerful Setup Mechanism

## Learning Objectives
- Create fixtures using the `@pytest.fixture` decorator
- Understand fixture scopes (function, class, module, session)
- Manage fixture dependencies
- Organize shared fixtures in `conftest.py`

## Why This Matters

Fixtures are Pytest's answer to test setup and teardown—but they're far more powerful than traditional setUp/tearDown methods. They enable dependency injection, automatic cleanup, and sophisticated resource sharing. Mastering fixtures is key to writing maintainable Pytest test suites.

## The Concept

### Basic Fixture

```python
import pytest

@pytest.fixture
def sample_user():
    """Provide a sample user for tests."""
    return {"name": "John", "email": "john@example.com", "age": 30}

def test_user_has_email(sample_user):
    assert "email" in sample_user
    assert "@" in sample_user["email"]

def test_user_is_adult(sample_user):
    assert sample_user["age"] >= 18
```

### Fixture Scopes

| Scope | Created | Use Case |
|-------|---------|----------|
| `function` (default) | Each test | Fresh state per test |
| `class` | Once per test class | Shared within class |
| `module` | Once per module | Shared within file |
| `session` | Once per test run | Expensive resources |

```python
@pytest.fixture(scope="function")
def fresh_database():
    """New database for each test."""
    db = Database()
    yield db
    db.clear()

@pytest.fixture(scope="module")
def database_connection():
    """Shared connection for all tests in module."""
    conn = connect_database()
    yield conn
    conn.close()

@pytest.fixture(scope="session")
def expensive_resource():
    """Created once for entire test session."""
    resource = load_expensive_resource()
    yield resource
    resource.cleanup()
```

### Setup and Teardown with yield

```python
@pytest.fixture
def temp_file():
    """Create temp file, yield it, then clean up."""
    # Setup
    file_path = Path("temp_test_file.txt")
    file_path.write_text("test content")
    
    # Provide to test
    yield file_path
    
    # Teardown (runs even if test fails)
    file_path.unlink()
```

### Fixture Dependencies

Fixtures can depend on other fixtures:

```python
@pytest.fixture
def database():
    return Database()

@pytest.fixture
def user_repository(database):
    return UserRepository(database)

@pytest.fixture
def user_service(user_repository):
    return UserService(user_repository)

def test_create_user(user_service):
    # user_service has all dependencies injected
    result = user_service.create("John")
    assert result.id is not None
```

### conftest.py: Shared Fixtures

Place fixtures in `conftest.py` for automatic sharing:

```
tests/
├── conftest.py           # Fixtures available to all tests
├── test_users.py
├── test_orders.py
└── integration/
    ├── conftest.py       # Additional fixtures for integration tests
    └── test_api.py
```

```python
# tests/conftest.py
import pytest

@pytest.fixture
def api_client():
    """Available to all tests."""
    return APIClient()

@pytest.fixture
def authenticated_user():
    """Available to all tests."""
    return User(authenticated=True)
```

### Using Fixtures Automatically

```python
@pytest.fixture(autouse=True)
def reset_database():
    """Automatically runs for every test."""
    Database.reset()
    yield
    Database.cleanup()
```

## Code Example

### Complete Fixture Example

```python
# conftest.py
import pytest
from pathlib import Path
import tempfile

@pytest.fixture(scope="session")
def temp_directory():
    """Session-scoped temporary directory."""
    with tempfile.TemporaryDirectory() as tmpdir:
        yield Path(tmpdir)

@pytest.fixture
def config(temp_directory):
    """Test configuration with temp paths."""
    return {
        "data_dir": temp_directory / "data",
        "log_dir": temp_directory / "logs",
        "debug": True
    }

@pytest.fixture
def application(config):
    """Fully configured application."""
    app = Application(config)
    app.initialize()
    yield app
    app.shutdown()

# test_application.py
def test_app_starts(application):
    assert application.is_running

def test_app_creates_directories(application, config):
    assert config["data_dir"].exists()
    assert config["log_dir"].exists()
```

## Summary

- **Fixtures** provide test setup via dependency injection
- **Scopes** control fixture lifetime (function, class, module, session)
- Use **`yield`** for setup-teardown pattern with automatic cleanup
- Fixtures can **depend on other fixtures**
- **`conftest.py`** shares fixtures across test files
- **`autouse=True`** applies fixtures automatically

## Additional Resources

- [Pytest Fixtures Documentation](https://docs.pytest.org/en/stable/explanation/fixtures.html) - Official guide
- [Fixture Scope Tutorial](https://www.tutorialspoint.com/pytest/pytest_fixtures.htm) - Scope examples
- [conftest.py Guide](https://docs.pytest.org/en/stable/reference/fixtures.html#conftest-py-sharing-fixtures-across-multiple-files) - Sharing fixtures

