# Side Effects: Dynamic Mock Behavior in Python

## Learning Objectives
- Use `side_effect` to raise exceptions
- Return different values per call
- Create dynamic responses with callables
- Combine side effects for complex scenarios

## Why This Matters

Real methods don't always return the same value. They might raise exceptions, return different results on each call, or compute results based on arguments. The `side_effect` attribute lets you model all these behaviors, making your mocks realistic and your tests comprehensive.

## The Concept

### Raising Exceptions

```python
from unittest.mock import Mock

mock = Mock()
mock.connect.side_effect = ConnectionError("Network unavailable")

# Now calling mock.connect() raises the exception
try:
    mock.connect()
except ConnectionError as e:
    print(f"Caught: {e}")  # Caught: Network unavailable
```

### Different Values Per Call

```python
mock = Mock()
mock.get_next.side_effect = [1, 2, 3]

print(mock.get_next())  # 1
print(mock.get_next())  # 2
print(mock.get_next())  # 3
# mock.get_next()  # StopIteration - list exhausted
```

### Mix Values and Exceptions

```python
mock = Mock()
mock.process.side_effect = [
    "success",           # First call returns "success"
    "success",           # Second call returns "success"
    TimeoutError(),      # Third call raises TimeoutError
    "recovered"          # Fourth call returns "recovered"
]

print(mock.process())    # "success"
print(mock.process())    # "success"
# mock.process()         # Raises TimeoutError
print(mock.process())    # "recovered"
```

### Dynamic Side Effects with Callables

```python
def calculate_result(x, y):
    """Side effect function receives same args as mock call."""
    return x * y

mock = Mock()
mock.multiply.side_effect = calculate_result

print(mock.multiply(3, 4))   # 12
print(mock.multiply(5, 6))   # 30
```

### Conditional Exceptions

```python
def conditional_side_effect(value):
    if value < 0:
        raise ValueError("Negative not allowed")
    return value * 2

mock = Mock()
mock.process.side_effect = conditional_side_effect

print(mock.process(5))    # 10
# mock.process(-1)        # Raises ValueError
```

### Side Effects for Void Methods

```python
mock = Mock()

# Log calls to track invocations
calls = []
def track_call(*args, **kwargs):
    calls.append((args, kwargs))

mock.notify.side_effect = track_call
mock.notify("user1", message="Hello")
mock.notify("user2", message="World")

print(calls)  # [(('user1',), {'message': 'Hello'}), ...]
```

## Code Example

### Practical Side Effect Patterns

```python
import pytest
from unittest.mock import Mock, MagicMock

class TestPaymentService:
    
    def test_retry_on_timeout(self):
        mock_gateway = Mock()
        # First two calls timeout, third succeeds
        mock_gateway.charge.side_effect = [
            TimeoutError("Connection timeout"),
            TimeoutError("Connection timeout"),
            {"status": "success", "transaction_id": "TXN123"}
        ]
        
        service = PaymentService(mock_gateway, max_retries=3)
        result = service.process_payment(99.99)
        
        assert result["status"] == "success"
        assert mock_gateway.charge.call_count == 3
    
    def test_amount_based_response(self):
        def charge_logic(amount):
            if amount > 10000:
                raise ValueError("Amount exceeds limit")
            if amount > 1000:
                return {"status": "pending_review"}
            return {"status": "approved"}
        
        mock_gateway = Mock()
        mock_gateway.charge.side_effect = charge_logic
        
        service = PaymentService(mock_gateway)
        
        assert service.process_payment(500)["status"] == "approved"
        assert service.process_payment(5000)["status"] == "pending_review"
        
        with pytest.raises(ValueError):
            service.process_payment(50000)
```

## Summary

- **`side_effect=Exception()`**: Raise exception when called
- **`side_effect=[a, b, c]`**: Return different values per call
- **`side_effect=callable`**: Dynamic response based on arguments
- Mix **values and exceptions** in sequence
- Use **callables** for complex conditional logic
- Track **void method calls** with side effects

## Additional Resources

- [side_effect Documentation](https://docs.python.org/3/library/unittest.mock.html#unittest.mock.Mock.side_effect) - Official reference
- [Mocking Exceptions](https://realpython.com/python-mock-library/#mocking-exceptions) - Exception patterns
- [Dynamic Return Values](https://www.freblogg.com/pytest-functions-mocking-1) - Advanced patterns

