# Lab: Side Effects Scenarios - Retry Logic and Error Handling

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | side-effects.md, demo_side_effects.py |

## Learning Objectives
By completing this exercise, you will:
- Use `side_effect` to return different values on consecutive calls
- Use `side_effect` to raise exceptions
- Use `side_effect` as a callable for dynamic behavior
- Test retry logic with controlled failures
- Test error handling and recovery scenarios

## The Scenario

You're testing a `RetryableClient` that implements retry logic for unreliable network calls. You need to simulate various failure/success patterns to thoroughly test the retry behavior.

## Core Tasks

### Task 1: Basic Side Effect - Return Different Values (10 minutes)

```python
import pytest
from retryable_client import RetryableClient


class TestRetryLogic:
    
    def test_client_returns_first_success(self, mocker):
        """Test immediate success - no retry needed."""
        mock_api = mocker.Mock()
        mock_api.fetch.return_value = {"data": "success"}
        
        client = RetryableClient(api=mock_api)
        result = client.get_data()
        
        assert result["data"] == "success"
        assert mock_api.fetch.call_count == 1
    
    def test_client_retries_on_failure(self, mocker):
        """Test retry on first failure, success on second."""
        mock_api = mocker.Mock()
        # First call fails, second succeeds
        mock_api.fetch.side_effect = [
            ConnectionError("Network error"),
            {"data": "success"}
        ]
        
        client = RetryableClient(api=mock_api, max_retries=3)
        result = client.get_data()
        
        assert result["data"] == "success"
        assert mock_api.fetch.call_count == 2
```

### Task 2: Multiple Failures Before Success (15 minutes)

```python
def test_client_succeeds_on_third_attempt(self, mocker):
    """Test success after two failures."""
    mock_api = mocker.Mock()
    mock_api.fetch.side_effect = [
        ConnectionError("Attempt 1 failed"),
        TimeoutError("Attempt 2 failed"),
        {"data": "finally!"}
    ]
    
    client = RetryableClient(api=mock_api, max_retries=5)
    result = client.get_data()
    
    assert result["data"] == "finally!"
    assert mock_api.fetch.call_count == 3


def test_client_exhausts_retries_and_fails(self, mocker):
    """Test failure after max retries exhausted."""
    mock_api = mocker.Mock()
    mock_api.fetch.side_effect = ConnectionError("Always fails")
    
    client = RetryableClient(api=mock_api, max_retries=3)
    
    with pytest.raises(ConnectionError):
        client.get_data()
    
    assert mock_api.fetch.call_count == 3


def test_client_retry_pattern_timeout_connection_success(self, mocker):
    """Test specific failure pattern."""
    mock_api = mocker.Mock()
    mock_api.fetch.side_effect = [
        TimeoutError("Timeout"),
        ConnectionError("Connection reset"),
        TimeoutError("Timeout again"),
        {"status": "ok"}
    ]
    
    client = RetryableClient(api=mock_api, max_retries=5)
    result = client.get_data()
    
    assert result["status"] == "ok"
```

### Task 3: Side Effect as Callable (15 minutes)

```python
def test_side_effect_callable_for_dynamic_response(self, mocker):
    """Use callable to generate dynamic responses."""
    call_count = {"value": 0}
    
    def dynamic_response():
        call_count["value"] += 1
        if call_count["value"] < 3:
            raise ConnectionError(f"Attempt {call_count['value']} failed")
        return {"attempt": call_count["value"], "success": True}
    
    mock_api = mocker.Mock()
    mock_api.fetch.side_effect = dynamic_response
    
    client = RetryableClient(api=mock_api, max_retries=5)
    result = client.get_data()
    
    assert result["success"] is True
    assert result["attempt"] == 3


def test_side_effect_validates_arguments(self, mocker):
    """Use callable to validate call arguments."""
    def validate_and_respond(endpoint, params=None):
        if not endpoint.startswith("/api/"):
            raise ValueError("Invalid endpoint")
        if params and "invalid" in params:
            raise ValueError("Invalid params")
        return {"endpoint": endpoint, "params": params}
    
    mock_api = mocker.Mock()
    mock_api.fetch.side_effect = validate_and_respond
    
    client = RetryableClient(api=mock_api)
    
    # Valid call
    result = client.fetch_endpoint("/api/users", {"id": 1})
    assert result["endpoint"] == "/api/users"
    
    # Invalid endpoint
    with pytest.raises(ValueError, match="Invalid endpoint"):
        client.fetch_endpoint("/invalid/path")
```

### Task 4: Different Exceptions Based on Input (10 minutes)

```python
def test_side_effect_raises_different_exceptions(self, mocker):
    """Different inputs cause different exceptions."""
    def conditional_exception(user_id):
        if user_id == 0:
            raise ValueError("Invalid user ID")
        if user_id < 0:
            raise PermissionError("Access denied")
        if user_id > 1000:
            raise LookupError("User not found")
        return {"id": user_id, "name": f"User {user_id}"}
    
    mock_api = mocker.Mock()
    mock_api.get_user.side_effect = conditional_exception
    
    client = RetryableClient(api=mock_api)
    
    # Test various inputs
    assert client.get_user(1)["name"] == "User 1"
    
    with pytest.raises(ValueError):
        client.get_user(0)
    
    with pytest.raises(PermissionError):
        client.get_user(-1)
    
    with pytest.raises(LookupError):
        client.get_user(9999)
```

### Task 5: Iterator Side Effect (10 minutes)

```python
def test_side_effect_iterator_for_pagination(self, mocker):
    """Use iterator for paginated responses."""
    pages = iter([
        {"page": 1, "items": ["a", "b"], "has_more": True},
        {"page": 2, "items": ["c", "d"], "has_more": True},
        {"page": 3, "items": ["e"], "has_more": False},
    ])
    
    mock_api = mocker.Mock()
    mock_api.fetch_page.side_effect = lambda: next(pages)
    
    client = RetryableClient(api=mock_api)
    all_items = client.fetch_all_pages()
    
    assert all_items == ["a", "b", "c", "d", "e"]
    assert mock_api.fetch_page.call_count == 3


def test_side_effect_stopiteration_handling(self, mocker):
    """Test behavior when iterator is exhausted."""
    mock_api = mocker.Mock()
    mock_api.fetch.side_effect = iter([
        {"success": True},
        {"success": True},
        # No more values - StopIteration raised
    ])
    
    client = RetryableClient(api=mock_api)
    
    client.get_data()  # First call OK
    client.get_data()  # Second call OK
    
    with pytest.raises(StopIteration):
        client.get_data()  # Third call - no more values
```

## Side Effect Patterns Summary

| Pattern | Syntax | Use Case |
|---------|--------|----------|
| Raise exception | `side_effect = Exception("msg")` | Test error handling |
| List of returns | `side_effect = [val1, val2, val3]` | Test retry logic |
| Callable | `side_effect = my_function` | Dynamic responses |
| Iterator | `side_effect = iter([...])` | Pagination, sequences |
| Conditional | `side_effect = lambda x: ...` | Input-based responses |

## Definition of Done

- [ ] At least 2 tests with exception side effects
- [ ] At least 2 tests with list side effects (consecutive returns)
- [ ] At least 2 tests with callable side effects
- [ ] At least 1 test with iterator side effect
- [ ] Tests cover retry success and failure scenarios
- [ ] Tests verify correct call counts
- [ ] All tests pass

## Submission

Commit with message:
```
feat(week6): Complete side effects scenarios exercise
```

