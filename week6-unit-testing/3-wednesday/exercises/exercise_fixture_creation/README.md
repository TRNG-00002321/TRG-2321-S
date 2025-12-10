# Lab: Fixture Creation - Database and User Objects

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | test-fixtures.md, demo_fixtures_intro.py |

## Learning Objectives
By completing this exercise, you will:
- Create pytest fixtures with `@pytest.fixture`
- Understand fixture scopes (function, class, module, session)
- Use fixture dependencies (fixtures using other fixtures)
- Implement setup/teardown with `yield`
- Share fixtures across files with `conftest.py`

## The Scenario

You're building a test suite for a user management application. You need fixtures for:
1. Database connections (expensive, share across tests)
2. User objects (fresh per test)
3. Admin users (special test cases)
4. Test data (loaded once)

## Core Tasks

### Task 1: Create Basic Fixtures (10 minutes)

Create `conftest.py` with basic fixtures:

```python
import pytest
from models import User, DatabaseConnection


@pytest.fixture
def user():
    """Create a standard test user."""
    return User(
        id=1,
        username="testuser",
        email="test@example.com",
        role="user"
    )


@pytest.fixture
def admin_user():
    """Create an admin test user."""
    return User(
        id=99,
        username="admin",
        email="admin@example.com",
        role="admin"
    )
```

### Task 2: Create Scoped Fixtures (15 minutes)

Create fixtures with different scopes:

```python
@pytest.fixture(scope="session")
def database():
    """
    Database connection shared across all tests in the session.
    Expensive to create, so reuse it.
    """
    print("\n[SETUP] Creating database connection")
    db = DatabaseConnection(host="localhost", port=5432)
    db.connect()
    
    yield db  # Provide the fixture
    
    # Teardown: runs after all tests
    print("\n[TEARDOWN] Closing database connection")
    db.disconnect()


@pytest.fixture(scope="module")
def test_data(database):
    """
    Test data loaded once per module.
    Depends on database fixture.
    """
    print("\n[SETUP] Loading test data")
    data = database.load_test_data("test_data.json")
    
    yield data
    
    print("\n[TEARDOWN] Clearing test data")
    database.clear_test_data()


@pytest.fixture(scope="function")  # Default scope
def fresh_user(database):
    """
    Fresh user created for each test function.
    Cleaned up after each test.
    """
    user = User(username="fresh_user", email="fresh@test.com")
    database.save(user)
    
    yield user
    
    database.delete(user)
```

### Task 3: Fixture Dependencies (10 minutes)

Create fixtures that depend on other fixtures:

```python
@pytest.fixture
def authenticated_user(user, database):
    """User with an active session."""
    session = database.create_session(user)
    user.session = session
    
    yield user
    
    database.end_session(session)


@pytest.fixture
def user_with_orders(authenticated_user, database):
    """User with some test orders."""
    orders = [
        {"id": 1, "product": "Laptop", "amount": 999.99},
        {"id": 2, "product": "Mouse", "amount": 29.99}
    ]
    for order in orders:
        database.create_order(authenticated_user.id, order)
    
    yield authenticated_user
    
    database.delete_orders_for_user(authenticated_user.id)
```

### Task 4: Parameterized Fixtures (10 minutes)

Create fixtures that provide multiple values:

```python
@pytest.fixture(params=["user", "admin", "guest"])
def user_role(request):
    """Parameterized fixture for testing different roles."""
    return request.param


@pytest.fixture(params=[
    pytest.param(User("alice", "alice@test.com"), id="alice"),
    pytest.param(User("bob", "bob@test.com"), id="bob"),
    pytest.param(User("charlie", "charlie@test.com"), id="charlie"),
])
def sample_user(request):
    """Multiple sample users."""
    return request.param
```

### Task 5: Use Fixtures in Tests (10 minutes)

Write tests that use your fixtures:

```python
# test_users.py
import pytest


def test_user_has_correct_email(user):
    """Test using the basic user fixture."""
    assert user.email == "test@example.com"


def test_admin_has_admin_role(admin_user):
    """Test using admin_user fixture."""
    assert admin_user.role == "admin"


def test_database_connection_works(database):
    """Test using session-scoped database fixture."""
    assert database.is_connected()


def test_user_can_place_order(authenticated_user, database):
    """Test using multiple fixtures."""
    order_id = database.create_order(
        authenticated_user.id, 
        {"product": "Test Item", "amount": 10.00}
    )
    assert order_id is not None
    
    # Cleanup handled by fixture teardown


def test_user_orders_loaded(user_with_orders):
    """Test using a fixture chain."""
    orders = user_with_orders.get_orders()
    assert len(orders) == 2


class TestUserRoles:
    """Tests that run for each user role."""
    
    def test_role_is_valid(self, user_role):
        """Runs 3 times: once for each role."""
        assert user_role in ["user", "admin", "guest"]
```

## Fixture Scope Reference

| Scope | Description | Use Case |
|-------|-------------|----------|
| `function` | New for each test (default) | Fresh objects per test |
| `class` | Shared within test class | Related tests sharing state |
| `module` | Shared within .py file | Module-level setup |
| `session` | Shared across all tests | Database connections |

## Directory Structure

```
tests/
├── conftest.py          # Shared fixtures
├── test_users.py        # User-related tests
├── test_orders.py       # Order-related tests
└── data/
    └── test_data.json   # Test data file
```

## Definition of Done

- [ ] `conftest.py` with at least 5 fixtures
- [ ] At least one fixture per scope (function, class, module, session)
- [ ] At least 2 fixtures with dependencies
- [ ] At least 1 parameterized fixture
- [ ] All fixtures use `yield` for proper teardown
- [ ] At least 5 tests using the fixtures
- [ ] Tests demonstrate fixture reuse
- [ ] All tests pass

## Fixture Best Practices

```python
# DO: Use descriptive names
@pytest.fixture
def authenticated_admin_with_permissions():
    ...

# DON'T: Use vague names
@pytest.fixture
def user2():
    ...

# DO: Clean up in teardown
@pytest.fixture
def temp_file():
    f = open("test.txt", "w")
    yield f
    f.close()
    os.remove("test.txt")

# DO: Use autouse sparingly
@pytest.fixture(autouse=True)
def setup_logging():
    """Runs for every test automatically."""
    logging.basicConfig(level=logging.DEBUG)
```

## Submission

Commit with message:
```
feat(week6): Complete fixture creation exercise
```

