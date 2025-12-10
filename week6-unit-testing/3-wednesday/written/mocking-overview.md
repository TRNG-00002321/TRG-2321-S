# Mocking Overview: Test Doubles in Python

## Learning Objectives
- Understand why mocking is essential in Python testing
- Explore the `unittest.mock` module
- Differentiate between Mock and MagicMock objects
- Apply the test doubles concept in Python

## Why This Matters

Just as we used Mockito in Java, Python needs mocking for isolating units under test. The `unittest.mock` module (standard since Python 3.3) provides powerful tools for creating test doubles—fake objects that replace real dependencies during testing.

## The Concept

### Why Mock in Python?

```python
# Without mocking - calls real API!
def test_get_weather():
    service = WeatherService()
    result = service.get_weather("London")  # Real HTTP call!
    assert result is not None
```

**Problems:**
- Slow (network calls)
- Unreliable (API might be down)
- Expensive (API rate limits)
- Non-deterministic (weather changes!)

### The unittest.mock Module

```python
from unittest.mock import Mock, MagicMock, patch
```

**Key components:**
- **Mock**: Basic mock object
- **MagicMock**: Mock with magic methods pre-configured
- **patch**: Replace objects during tests
- **PropertyMock**: Mock properties

### Creating a Basic Mock

```python
from unittest.mock import Mock

# Create a mock
mock_repository = Mock()

# Configure return value
mock_repository.find_by_id.return_value = {"id": 1, "name": "John"}

# Use the mock
result = mock_repository.find_by_id(1)
print(result)  # {'id': 1, 'name': 'John'}

# Verify it was called
mock_repository.find_by_id.assert_called_once_with(1)
```

### Mock vs MagicMock

```python
from unittest.mock import Mock, MagicMock

# Mock - basic mock
mock = Mock()
len(mock)  # TypeError: object has no len()

# MagicMock - includes magic methods
magic_mock = MagicMock()
len(magic_mock)  # Returns another MagicMock (0 by default for len)
magic_mock[0]    # Works - __getitem__ is pre-configured
str(magic_mock)  # Works - __str__ is pre-configured
```

**Use MagicMock when:**
- Object uses magic methods (`__len__`, `__getitem__`, etc.)
- You're not sure what methods will be called
- Default choice for most scenarios

### Test Doubles Concept

| Type | Description | Python Implementation |
|------|-------------|----------------------|
| **Dummy** | Passed but never used | `Mock()` |
| **Stub** | Returns predetermined values | `Mock(return_value=...)` |
| **Spy** | Records calls for verification | `Mock()` with assertions |
| **Mock** | Verifies expected interactions | `Mock()` with assertions |
| **Fake** | Working implementation (simplified) | Custom class |

## Code Example

### Complete Mock Usage

```python
from unittest.mock import Mock, MagicMock
import pytest

class TestUserService:
    
    def test_get_user_returns_user(self):
        # Arrange
        mock_repo = Mock()
        mock_repo.find_by_id.return_value = {
            "id": 1,
            "name": "John",
            "email": "john@example.com"
        }
        
        service = UserService(mock_repo)
        
        # Act
        user = service.get_user(1)
        
        # Assert
        assert user["name"] == "John"
        mock_repo.find_by_id.assert_called_once_with(1)
    
    def test_create_user_saves_to_repository(self):
        # Arrange
        mock_repo = Mock()
        mock_repo.save.return_value = True
        service = UserService(mock_repo)
        
        # Act
        result = service.create_user("Jane", "jane@example.com")
        
        # Assert
        assert result is True
        mock_repo.save.assert_called_once()
        
        # Inspect what was passed
        call_args = mock_repo.save.call_args[0][0]
        assert call_args["name"] == "Jane"
        assert call_args["email"] == "jane@example.com"
```

## Summary

- **unittest.mock** is Python's built-in mocking library
- **Mock** provides basic mock functionality
- **MagicMock** adds pre-configured magic methods
- Use mocks to **isolate** code from external dependencies
- Mocks can **verify** interactions (calls, arguments)
- Different test double types serve different purposes

## Additional Resources

- [unittest.mock Documentation](https://docs.python.org/3/library/unittest.mock.html) - Official docs
- [Real Python: Mock Guide](https://realpython.com/python-mock-library/) - Comprehensive tutorial
- [Martin Fowler: Test Doubles](https://martinfowler.com/bliki/TestDouble.html) - Concept overview

