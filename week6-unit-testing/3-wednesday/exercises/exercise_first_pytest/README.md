# Lab: Your First Pytest Tests - StringCalculator

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Beginner |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | pytest.md, demo_pytest_basics.py |

## Learning Objectives
By completing this exercise, you will:
- Write your first Pytest tests
- Use Pytest's simple `assert` syntax
- Understand test discovery conventions
- Run tests with various options
- Test edge cases and error conditions

## The Scenario

You've been assigned to test a `StringCalculator` class that parses and calculates numbers from strings. The class has some tricky edge cases that need comprehensive testing.

## Core Tasks

### Task 1: Set Up Your Test File (5 minutes)

Create `test_string_calculator.py` in the `starter_code/` directory:

```python
import pytest
from string_calculator import StringCalculator


class TestStringCalculator:
    """Tests for the StringCalculator class."""
    
    def setup_method(self):
        """Create a fresh calculator for each test."""
        self.calc = StringCalculator()
```

### Task 2: Test Basic Addition (15 minutes)

Write tests for the `add()` method with simple inputs:

| Input | Expected Output |
|-------|-----------------|
| "" | 0 |
| "1" | 1 |
| "1,2" | 3 |
| "1,2,3,4,5" | 15 |

```python
def test_add_empty_string_returns_zero(self):
    assert self.calc.add("") == 0

def test_add_single_number_returns_that_number(self):
    assert self.calc.add("1") == 1
    assert self.calc.add("42") == 42
```

### Task 3: Test Edge Cases (15 minutes)

Write tests for edge cases:

**Newline delimiter:**
```python
def test_add_newline_delimiter(self):
    # "1\n2,3" should equal 6
    assert self.calc.add("1\n2,3") == 6
```

**Custom delimiter:**
```python
def test_add_custom_delimiter(self):
    # "//;\n1;2" uses ; as delimiter
    assert self.calc.add("//;\n1;2") == 3
```

**Large numbers ignored:**
```python
def test_add_ignores_numbers_over_1000(self):
    # Numbers > 1000 should be ignored
    assert self.calc.add("2,1001") == 2
    assert self.calc.add("1000,1001,2") == 1002  # 1000 + 0 + 2
```

### Task 4: Test Exception Handling (10 minutes)

Use `pytest.raises()` to test exceptions:

```python
def test_add_negative_number_raises_exception(self):
    with pytest.raises(ValueError) as exc_info:
        self.calc.add("-1,2")
    
    assert "negatives not allowed" in str(exc_info.value)
    assert "-1" in str(exc_info.value)

def test_add_multiple_negatives_shows_all_in_message(self):
    with pytest.raises(ValueError) as exc_info:
        self.calc.add("-1,-2,3,-4")
    
    error_message = str(exc_info.value)
    assert "-1" in error_message
    assert "-2" in error_message
    assert "-4" in error_message
```

### Task 5: Run Your Tests (5 minutes)

Try different ways to run tests:

```bash
# Run all tests
pytest

# Run with verbose output
pytest -v

# Run specific test file
pytest test_string_calculator.py

# Run specific test class
pytest test_string_calculator.py::TestStringCalculator

# Run specific test method
pytest test_string_calculator.py::TestStringCalculator::test_add_empty_string_returns_zero

# Run tests matching a pattern
pytest -k "negative"

# Show print statements
pytest -s
```

## Starter Code

```python
# string_calculator.py
class StringCalculator:
    """Calculator that adds numbers from a string."""
    
    def add(self, numbers: str) -> int:
        """
        Add numbers from a delimited string.
        
        Rules:
        - Empty string returns 0
        - Single number returns that number
        - Numbers can be delimited by comma or newline
        - Custom delimiter: "//[delimiter]\n[numbers]"
        - Numbers > 1000 are ignored
        - Negative numbers raise ValueError
        """
        if not numbers:
            return 0
        
        delimiter = ","
        
        # Check for custom delimiter
        if numbers.startswith("//"):
            delimiter_end = numbers.index("\n")
            delimiter = numbers[2:delimiter_end]
            numbers = numbers[delimiter_end + 1:]
        
        # Replace newlines with delimiter
        numbers = numbers.replace("\n", delimiter)
        
        # Parse numbers
        num_list = [int(n) for n in numbers.split(delimiter) if n]
        
        # Check for negatives
        negatives = [n for n in num_list if n < 0]
        if negatives:
            raise ValueError(f"negatives not allowed: {negatives}")
        
        # Filter and sum (ignore > 1000)
        return sum(n for n in num_list if n <= 1000)
```

## Pytest Assertions Cheat Sheet

```python
# Basic assertions
assert result == expected
assert result != unexpected
assert result is None
assert result is not None

# Boolean
assert condition
assert not condition

# Collections
assert item in collection
assert item not in collection
assert len(collection) == expected_length

# Exceptions
with pytest.raises(ValueError):
    function_that_raises()

# Approximate (for floats)
assert result == pytest.approx(expected, rel=1e-3)
```

## Definition of Done

- [ ] Test file follows naming convention (`test_*.py`)
- [ ] At least 4 tests for basic `add()` functionality
- [ ] At least 3 tests for edge cases (newlines, custom delimiter, large numbers)
- [ ] At least 2 tests for exception handling
- [ ] Tests use descriptive method names
- [ ] All tests pass when run with `pytest -v`

## Common Pytest Commands

| Command | Description |
|---------|-------------|
| `pytest` | Run all tests |
| `pytest -v` | Verbose output |
| `pytest -vv` | Extra verbose |
| `pytest -k "pattern"` | Run matching tests |
| `pytest --tb=short` | Shorter tracebacks |
| `pytest --tb=no` | No tracebacks |
| `pytest -x` | Stop on first failure |
| `pytest --lf` | Run last failed tests |

## Hints

<details>
<summary>Hint: Multiple Assertions</summary>

You can have multiple assertions in one test, but if the first fails, the rest won't run. Consider using separate tests for truly independent checks.
</details>

<details>
<summary>Hint: Testing Exception Message</summary>

```python
with pytest.raises(ValueError) as exc_info:
    self.calc.add("-1")

# exc_info.value is the exception instance
assert "negatives not allowed" in str(exc_info.value)
```
</details>

## Submission

Commit with message:
```
feat(week6): Complete first pytest exercise
```

