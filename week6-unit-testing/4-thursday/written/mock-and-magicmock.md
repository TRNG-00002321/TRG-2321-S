# Mock and MagicMock: Deep Dive into Python Mocking

## Learning Objectives
- Understand the Mock class in depth
- Master MagicMock and magic method support
- Use auto-speccing for safer mocks
- Configure mock return values and attributes

## Why This Matters

Mock and MagicMock are the workhorses of Python testing. Understanding their capabilities—auto-creation of attributes, magic method handling, and specification enforcement—lets you create sophisticated test doubles that accurately simulate real objects while remaining completely under your control.

## The Concept

### The Mock Class

Mock creates objects that accept any attribute access or method call:

```python
from unittest.mock import Mock

mock = Mock()

# Any attribute access creates a new Mock
mock.any_attribute        # Returns a Mock
mock.nested.deep.call()   # Returns a Mock

# Any method call works
mock.unknown_method(1, 2, 3)  # Returns a Mock

# Return value
mock.return_value = 42
result = mock()  # Returns 42

# Configure methods
mock.method.return_value = "hello"
mock.method()  # Returns "hello"
```

### MagicMock: Magic Methods Included

MagicMock is Mock with pre-configured magic methods:

```python
from unittest.mock import Mock, MagicMock

# Mock - no magic methods
regular_mock = Mock()
# len(regular_mock)  # TypeError!

# MagicMock - magic methods work
magic_mock = MagicMock()
len(magic_mock)        # Returns 0
str(magic_mock)        # Returns string representation
magic_mock[0]          # Works (__getitem__)
magic_mock['key']      # Works
iter(magic_mock)       # Works (__iter__)
```

### Configuring Return Values

```python
mock = MagicMock()

# Simple return
mock.get_user.return_value = {"name": "John"}

# Callable mock itself
mock.return_value = 42
mock()  # Returns 42

# Nested return values
mock.client.connect.return_value.query.return_value = [1, 2, 3]
result = mock.client.connect().query()  # Returns [1, 2, 3]

# Configure via constructor
mock = MagicMock(return_value="constructed")
```

### Auto-Speccing: Safer Mocks

Auto-spec creates mocks that mirror the real object's interface:

```python
from unittest.mock import create_autospec

class UserRepository:
    def find_by_id(self, user_id: int):
        pass
    
    def save(self, user):
        pass

# Regular mock - accepts any call
regular_mock = Mock()
regular_mock.nonexistent_method()  # Works (dangerous!)

# Auto-spec mock - validates interface
spec_mock = create_autospec(UserRepository)
# spec_mock.nonexistent_method()  # AttributeError!
spec_mock.find_by_id(1)  # Works
# spec_mock.find_by_id()  # TypeError - missing argument!
```

### Mock Configuration Options

```python
# name - for debugging
mock = Mock(name="database_mock")

# spec - restrict attributes
mock = Mock(spec=SomeClass)

# return_value - what mock() returns
mock = Mock(return_value=42)

# side_effect - exception or callable
mock = Mock(side_effect=Exception("Error!"))

# wraps - partial mock
mock = Mock(wraps=real_object)
```

## Code Example

### Practical Mock and MagicMock Usage

```python
from unittest.mock import Mock, MagicMock, create_autospec

class TestDatabaseService:
    
    def test_with_basic_mock(self):
        mock_conn = Mock()
        mock_conn.execute.return_value.fetchall.return_value = [
            (1, "John"), (2, "Jane")
        ]
        
        service = DatabaseService(mock_conn)
        users = service.get_all_users()
        
        assert len(users) == 2
        mock_conn.execute.assert_called_once()
    
    def test_with_magic_mock(self):
        mock_cursor = MagicMock()
        mock_cursor.__iter__.return_value = iter([
            {"id": 1}, {"id": 2}
        ])
        
        for row in mock_cursor:
            assert "id" in row
    
    def test_with_autospec(self):
        mock_repo = create_autospec(UserRepository)
        mock_repo.find_by_id.return_value = User(id=1, name="John")
        
        service = UserService(mock_repo)
        user = service.get_user(1)
        
        assert user.name == "John"
        mock_repo.find_by_id.assert_called_with(1)
```

## Summary

- **Mock**: Flexible fake object accepting any call
- **MagicMock**: Mock + pre-configured magic methods
- **return_value**: Configure what mock returns when called
- **Auto-spec**: Create mocks that validate against real interface
- Use MagicMock by default, Mock for simple cases
- Prefer **auto-spec** to catch API misuse

## Additional Resources

- [Mock Documentation](https://docs.python.org/3/library/unittest.mock.html#unittest.mock.Mock) - Official Mock reference
- [MagicMock Documentation](https://docs.python.org/3/library/unittest.mock.html#unittest.mock.MagicMock) - Magic method details
- [Auto-speccing Guide](https://docs.python.org/3/library/unittest.mock.html#autospeccing) - Safer mocking

