# Validating Functions in Python: Testing Outputs and Side Effects

## Learning Objectives
- Test function return values with Pytest assertions
- Verify function side effects
- Create custom assertions for complex validation
- Apply assertion patterns for different data types

## Why This Matters

Python functions return values, raise exceptions, modify state, and produce side effects. Thoroughly validating all these behaviors ensures your functions work correctly in all scenarios. Python's dynamic typing makes assertion patterns especially important for catching type-related issues.

## The Concept

### Basic Return Value Testing

```python
def test_return_value():
    result = calculate_total(100, 0.1)
    assert result == 110.0

def test_return_type():
    result = get_user_ids()
    assert isinstance(result, list)
    assert all(isinstance(id, int) for id in result)
```

### Floating-Point Assertions

```python
import pytest

def test_floating_point():
    result = 0.1 + 0.2
    
    # Don't do this - floating point precision issues
    # assert result == 0.3  # May fail!
    
    # Use pytest.approx for float comparison
    assert result == pytest.approx(0.3)
    assert result == pytest.approx(0.3, rel=1e-9)  # Relative tolerance
    assert result == pytest.approx(0.3, abs=1e-10)  # Absolute tolerance
```

### Collection Assertions

```python
def test_list_contents():
    result = get_items()
    
    # Length
    assert len(result) == 3
    
    # Contains
    assert "apple" in result
    
    # Order matters
    assert result == ["apple", "banana", "cherry"]
    
    # Order doesn't matter
    assert set(result) == {"apple", "banana", "cherry"}

def test_dict_contents():
    user = get_user()
    
    # Key exists
    assert "email" in user
    
    # Value check
    assert user["name"] == "John"
    
    # Subset check
    assert user.items() >= {"name": "John", "age": 30}.items()
```

### Testing Side Effects

```python
def test_file_creation():
    create_log_file("test.log")
    
    assert Path("test.log").exists()
    content = Path("test.log").read_text()
    assert "Log initialized" in content

def test_state_modification():
    cart = ShoppingCart()
    cart.add_item("apple", 2)
    
    assert cart.item_count == 1
    assert cart.total_quantity == 2
```

### Custom Assertion Helpers

```python
def assert_valid_email(email):
    """Custom assertion for email validation."""
    assert email is not None, "Email cannot be None"
    assert "@" in email, f"Email missing @: {email}"
    assert "." in email.split("@")[1], f"Invalid domain: {email}"

def test_user_email():
    user = create_user("John", "john@example.com")
    assert_valid_email(user.email)

# Using pytest's assertion introspection
def test_with_message():
    result = process_data(data)
    assert result.status == "success", f"Processing failed: {result.error}"
```

## Code Example

### Comprehensive Function Validation

```python
import pytest
from datetime import datetime

class TestUserService:
    
    def test_create_user_returns_user_object(self):
        result = user_service.create("John", "john@example.com")
        
        assert result is not None
        assert hasattr(result, 'id')
        assert hasattr(result, 'name')
        assert hasattr(result, 'email')
    
    def test_create_user_sets_correct_values(self):
        result = user_service.create("Jane", "jane@example.com")
        
        assert result.name == "Jane"
        assert result.email == "jane@example.com"
        assert isinstance(result.id, int)
        assert result.id > 0
    
    def test_create_user_sets_timestamp(self):
        before = datetime.now()
        result = user_service.create("Bob", "bob@example.com")
        after = datetime.now()
        
        assert before <= result.created_at <= after
    
    def test_get_users_returns_list(self):
        result = user_service.get_all()
        
        assert isinstance(result, list)
        assert all(hasattr(u, 'id') for u in result)
    
    def test_calculate_age_with_approximation(self):
        result = calculate_average_age(users)
        assert result == pytest.approx(32.5, abs=0.1)
```

## Summary

- Use **`assert`** statements for simple validations
- Use **`pytest.approx()`** for floating-point comparisons
- Check **types** with `isinstance()` in dynamic Python
- Test **side effects** (files, state changes) explicitly
- Create **custom assertion helpers** for complex validations
- Include **descriptive messages** for debugging

## Additional Resources

- [Pytest Assertions](https://docs.pytest.org/en/stable/how-to/assert.html) - Official guide
- [pytest.approx Documentation](https://docs.pytest.org/en/stable/reference/reference.html#pytest-approx) - Float comparison
- [Writing Effective Assertions](https://realpython.com/pytest-python-testing/) - Best practices

