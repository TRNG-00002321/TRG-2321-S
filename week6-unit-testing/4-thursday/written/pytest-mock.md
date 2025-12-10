# Pytest-Mock: Seamless Mocking with Pytest

## Learning Objectives
- Install and use the pytest-mock plugin
- Apply the `mocker` fixture for clean mocking
- Understand advantages over manual patching
- Integrate mocking smoothly into Pytest workflow

## Why This Matters

While `unittest.mock` is powerful, using it directly with Pytest can feel clunky. The `pytest-mock` plugin provides a `mocker` fixture that integrates mocking seamlessly into Pytest's fixture system—making your tests cleaner and more Pythonic.

## The Concept

### Installation

```bash
pip install pytest-mock
```

### The mocker Fixture

pytest-mock provides a `mocker` fixture that wraps `unittest.mock`:

```python
def test_with_mocker(mocker):
    # Create a mock
    mock_function = mocker.Mock(return_value=42)
    
    # Patch a module attribute
    mocker.patch('module.function', return_value="mocked")
    
    # Patch an object method
    mocker.patch.object(obj, 'method', return_value="mocked")
```

### Advantages Over Manual Patching

**Without pytest-mock:**
```python
from unittest.mock import patch

def test_manual_patching():
    with patch('mymodule.api_call') as mock_api:
        mock_api.return_value = {"status": "ok"}
        result = my_service.do_something()
    # Patch automatically removed after context
```

**With pytest-mock:**
```python
def test_with_mocker(mocker):
    mock_api = mocker.patch('mymodule.api_call', return_value={"status": "ok"})
    result = my_service.do_something()
    # Patch automatically removed after test
```

**Benefits:**
- No context managers or decorators needed
- Automatic cleanup after each test
- Direct fixture injection
- Cleaner, more readable tests

### Common mocker Methods

```python
def test_mocker_methods(mocker):
    # Create mocks
    mock_obj = mocker.Mock()
    magic_mock = mocker.MagicMock()
    
    # Patch module attributes
    mocker.patch('os.path.exists', return_value=True)
    
    # Patch object methods
    mocker.patch.object(my_object, 'method')
    
    # Patch dictionaries
    mocker.patch.dict('os.environ', {'KEY': 'value'})
    
    # Create spy (wraps real function)
    spy = mocker.spy(my_module, 'function')
    
    # Create stub
    stub = mocker.stub(name='my_stub')
```

## Code Example

### Complete pytest-mock Example

```python
import pytest

class TestEmailService:
    
    def test_send_email_success(self, mocker):
        # Mock the SMTP client
        mock_smtp = mocker.patch('email_service.smtplib.SMTP')
        mock_instance = mock_smtp.return_value.__enter__.return_value
        
        service = EmailService()
        result = service.send("to@example.com", "Hello!")
        
        assert result is True
        mock_instance.sendmail.assert_called_once()
    
    def test_send_email_failure(self, mocker):
        mock_smtp = mocker.patch('email_service.smtplib.SMTP')
        mock_instance = mock_smtp.return_value.__enter__.return_value
        mock_instance.sendmail.side_effect = Exception("Connection failed")
        
        service = EmailService()
        result = service.send("to@example.com", "Hello!")
        
        assert result is False

def test_api_call_with_spy(mocker):
    """Use spy to track calls to real function."""
    spy = mocker.spy(requests, 'get')
    
    # This calls the real function
    response = my_service.fetch_data()
    
    # But we can still verify it was called
    spy.assert_called_once()
```

## Summary

- **pytest-mock** provides the `mocker` fixture for clean mocking
- **Automatic cleanup** after each test—no context managers needed
- Methods: `mocker.patch()`, `mocker.Mock()`, `mocker.spy()`
- **Integrates naturally** with Pytest fixture system
- Wraps `unittest.mock` with Pytest-friendly interface

## Additional Resources

- [pytest-mock Documentation](https://pytest-mock.readthedocs.io/) - Official docs
- [PyPI: pytest-mock](https://pypi.org/project/pytest-mock/) - Package page
- [pytest-mock vs unittest.mock](https://medium.com/@yeraydiazdiaz/what-the-mock-cheatsheet-mocking-in-python-6a71db997832) - Comparison

