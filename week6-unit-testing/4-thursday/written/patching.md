# Patching: Replacing Objects During Tests

## Learning Objectives
- Use the `@patch` decorator for test-level patching
- Apply `patch()` as a context manager
- Understand where to patch (import location matters!)
- Use `patch.object` and `patch.dict` for specific scenarios

## Why This Matters

When your code imports and uses external modules, you need to replace those modules during testing. Patching temporarily substitutes real objects with mocks. Understanding where to patch—at the import location, not the definition—is crucial for effective Python mocking.

## The Concept

### The @patch Decorator

```python
from unittest.mock import patch

@patch('mymodule.external_api.call')
def test_with_patch(mock_call):
    mock_call.return_value = {"status": "ok"}
    
    result = my_function()  # Uses mocked external_api.call
    
    assert result == "ok"
    mock_call.assert_called_once()
```

### Context Manager Approach

```python
from unittest.mock import patch

def test_with_context_manager():
    with patch('mymodule.database.connect') as mock_connect:
        mock_connect.return_value.query.return_value = [1, 2, 3]
        
        result = fetch_data()
        
        assert result == [1, 2, 3]
    # Patch removed here
```

### WHERE to Patch: The Golden Rule

**Patch where the object is USED, not where it's DEFINED.**

```python
# mymodule.py
from external_lib import api_client

def my_function():
    return api_client.call()  # Uses api_client

# test_mymodule.py
# ❌ WRONG - patching where it's defined
@patch('external_lib.api_client')
def test_wrong(mock):
    pass

# ✅ CORRECT - patching where it's used
@patch('mymodule.api_client')
def test_correct(mock):
    pass
```

### patch.object: Patching Methods

```python
from unittest.mock import patch

class DatabaseConnection:
    def execute(self, query):
        # Real implementation
        pass

# Patch a specific method
@patch.object(DatabaseConnection, 'execute')
def test_database(mock_execute):
    mock_execute.return_value = [{"id": 1}]
    
    conn = DatabaseConnection()
    result = conn.execute("SELECT * FROM users")
    
    assert result == [{"id": 1}]
```

### patch.dict: Patching Dictionaries

```python
from unittest.mock import patch
import os

@patch.dict(os.environ, {"API_KEY": "test-key", "DEBUG": "true"})
def test_with_env_vars():
    assert os.environ["API_KEY"] == "test-key"
    assert os.environ["DEBUG"] == "true"
# Original environ restored after test
```

### Multiple Patches

```python
# Decorator stacking (bottom-up argument order!)
@patch('mymodule.service_b')
@patch('mymodule.service_a')
def test_multiple(mock_a, mock_b):  # Note: reverse order
    mock_a.return_value = "A"
    mock_b.return_value = "B"

# Or nested context managers
def test_multiple_context():
    with patch('mymodule.service_a') as mock_a:
        with patch('mymodule.service_b') as mock_b:
            mock_a.return_value = "A"
            mock_b.return_value = "B"
```

### start() and stop() for Flexible Control

```python
from unittest.mock import patch

class TestAdvanced:
    def setup_method(self):
        self.patcher = patch('mymodule.external_service')
        self.mock_service = self.patcher.start()
    
    def teardown_method(self):
        self.patcher.stop()
    
    def test_something(self):
        self.mock_service.return_value = "mocked"
        # Test code
```

## Code Example

### Complete Patching Patterns

```python
import pytest
from unittest.mock import patch, MagicMock

# Module under test
# weather_service.py
# from external_api import weather_api
# 
# def get_temperature(city):
#     response = weather_api.get(city)
#     return response['temp']

class TestWeatherService:
    
    @patch('weather_service.weather_api')
    def test_get_temperature_success(self, mock_api):
        mock_api.get.return_value = {'temp': 72, 'unit': 'F'}
        
        from weather_service import get_temperature
        result = get_temperature("New York")
        
        assert result == 72
        mock_api.get.assert_called_once_with("New York")
    
    def test_get_temperature_with_context(self):
        with patch('weather_service.weather_api') as mock_api:
            mock_api.get.return_value = {'temp': 20, 'unit': 'C'}
            
            from weather_service import get_temperature
            result = get_temperature("London")
            
            assert result == 20
    
    @patch.dict('os.environ', {'WEATHER_API_KEY': 'test-key'})
    @patch('weather_service.weather_api')
    def test_with_env_and_api(self, mock_api):
        import os
        assert os.environ['WEATHER_API_KEY'] == 'test-key'
        mock_api.get.return_value = {'temp': 25}
        # Test continues...
```

## Summary

- **`@patch`**: Decorator for test-level patching
- **`with patch()`**: Context manager for scoped patches
- **Patch at import location**, not definition location
- **`patch.object`**: Patch specific methods on objects
- **`patch.dict`**: Patch dictionary contents (like `os.environ`)
- **Multiple patches**: Stack decorators (bottom-up order) or nest contexts

## Additional Resources

- [patch Documentation](https://docs.python.org/3/library/unittest.mock.html#patch) - Official reference
- [Where to Patch](https://docs.python.org/3/library/unittest.mock.html#where-to-patch) - Critical concept
- [Patching Patterns](https://realpython.com/python-mock-library/#patching) - Practical examples

