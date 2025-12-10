# Pytest: Python's Modern Testing Framework

## Learning Objectives
- Understand Pytest's advantages over unittest
- Install and configure Pytest with `pytest.ini` or `pyproject.toml`
- Write and run your first Pytest tests
- Master test discovery conventions

## Why This Matters

As we transition from Java to Python testing, Pytest emerges as the clear choice for modern Python projects. Its simplicity, powerful fixtures, and extensive plugin ecosystem make it the preferred testing framework for everything from small scripts to large enterprise applications. Over 75% of Python projects on GitHub use Pytest.

## The Concept

### Why Pytest?

| Feature | Pytest | unittest |
|---------|--------|----------|
| Assertions | Simple `assert` | `self.assertEqual`, `self.assertTrue`, etc. |
| Setup/Teardown | Fixtures (flexible) | setUp/tearDown (rigid) |
| Test Discovery | Automatic | Requires boilerplate |
| Plugins | Rich ecosystem | Limited |
| Output | Detailed diffs | Basic |

### Installation

```bash
pip install pytest
```

Or in `requirements.txt`:
```
pytest>=7.4.0
```

### Your First Pytest Test

```python
# test_calculator.py

def test_addition():
    assert 2 + 2 == 4

def test_subtraction():
    assert 10 - 5 == 5
```

Run with:
```bash
pytest                    # Run all tests
pytest test_calculator.py  # Run specific file
pytest -v                 # Verbose output
```

### Test Discovery Rules

Pytest automatically discovers tests following conventions:

```
project/
├── src/
│   └── calculator.py
├── tests/
│   ├── __init__.py
│   ├── test_calculator.py    ✓ Starts with 'test_'
│   └── calculator_test.py    ✓ Ends with '_test'
└── pytest.ini
```

**Conventions:**
- Files: `test_*.py` or `*_test.py`
- Functions: `test_*`
- Classes: `Test*` (no `__init__`)
- Methods: `test_*`

### Configuration: pytest.ini

```ini
[pytest]
testpaths = tests
python_files = test_*.py
python_functions = test_*
python_classes = Test*
addopts = -v --tb=short
```

### Configuration: pyproject.toml

```toml
[tool.pytest.ini_options]
testpaths = ["tests"]
python_files = ["test_*.py"]
addopts = "-v --tb=short"
```

### Assertions with Helpful Output

```python
def test_string_comparison():
    expected = "Hello, World!"
    actual = "Hello, world!"
    assert expected == actual
    # Pytest shows:
    # E       AssertionError: assert 'Hello, World!' == 'Hello, world!'
    # E         - Hello, World!
    # E         + Hello, world!
```

### Running Specific Tests

```bash
pytest test_file.py::test_function       # Specific test
pytest test_file.py::TestClass           # All tests in class
pytest test_file.py::TestClass::test_method  # Specific method
pytest -k "add or subtract"              # Tests matching pattern
pytest -m "slow"                         # Tests with marker
```

## Code Example

### Complete Pytest Test Module

```python
# tests/test_string_utils.py
import pytest
from src.string_utils import StringUtils

class TestStringUtils:
    """Test cases for StringUtils class."""
    
    def test_reverse_simple_string(self):
        result = StringUtils.reverse("hello")
        assert result == "olleh"
    
    def test_reverse_empty_string(self):
        result = StringUtils.reverse("")
        assert result == ""
    
    def test_reverse_none_raises_error(self):
        with pytest.raises(TypeError):
            StringUtils.reverse(None)
    
    def test_capitalize_words(self):
        result = StringUtils.capitalize_words("hello world")
        assert result == "Hello World"

# Standalone test functions work too
def test_is_palindrome_true():
    assert StringUtils.is_palindrome("racecar") is True

def test_is_palindrome_false():
    assert StringUtils.is_palindrome("hello") is False
```

## Summary

- **Pytest** is Python's modern testing framework—simple, powerful, extensible
- Uses **plain assert** statements with detailed failure output
- **Automatic test discovery** based on naming conventions
- Configure with **pytest.ini** or **pyproject.toml**
- Run tests with **`pytest`** command with flexible selection options
- Rich **plugin ecosystem** for extended functionality

## Additional Resources

- [Pytest Official Documentation](https://docs.pytest.org/) - Comprehensive guide
- [Pytest GitHub](https://github.com/pytest-dev/pytest) - Source and issues
- [Full Stack Python: Pytest](https://www.fullstackpython.com/pytest.html) - Tutorials

