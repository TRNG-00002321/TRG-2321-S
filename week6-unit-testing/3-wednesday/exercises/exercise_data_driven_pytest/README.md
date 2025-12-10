# Lab: Data-Driven Pytest - Loading Tests from JSON

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | loading-test-data.md, demo_test_data_loading.py |

## Learning Objectives
By completing this exercise, you will:
- Load test data from JSON files
- Use `@pytest.mark.parametrize` with external data
- Create fixture factories for test data
- Generate tests dynamically from data files
- Organize test data effectively

## The Scenario

Your QA team maintains test cases in JSON files (easier for non-developers to edit). Your task is to load these test cases and run them as parameterized pytest tests.

## Core Tasks

### Task 1: Create Test Data Files (10 minutes)

Create `test_data/calculator_tests.json`:

```json
{
    "addition_tests": [
        {"a": 1, "b": 2, "expected": 3, "description": "simple addition"},
        {"a": -1, "b": 1, "expected": 0, "description": "negative plus positive"},
        {"a": 0, "b": 0, "expected": 0, "description": "zeros"},
        {"a": 100, "b": 200, "expected": 300, "description": "larger numbers"}
    ],
    "division_tests": [
        {"a": 10, "b": 2, "expected": 5.0, "description": "even division"},
        {"a": 7, "b": 2, "expected": 3.5, "description": "decimal result"},
        {"a": -10, "b": 2, "expected": -5.0, "description": "negative dividend"}
    ],
    "division_by_zero_tests": [
        {"a": 10, "b": 0, "description": "divide by zero"},
        {"a": 0, "b": 0, "description": "zero divided by zero"}
    ]
}
```

### Task 2: Load Data with Fixture (10 minutes)

Create a fixture to load test data:

```python
# conftest.py
import json
import pytest
from pathlib import Path


@pytest.fixture(scope="session")
def test_data():
    """Load all test data from JSON file."""
    data_path = Path(__file__).parent / "test_data" / "calculator_tests.json"
    with open(data_path) as f:
        return json.load(f)


def load_test_cases(filename, key):
    """Helper function to load specific test cases."""
    data_path = Path(__file__).parent / "test_data" / filename
    with open(data_path) as f:
        data = json.load(f)
    return data.get(key, [])
```

### Task 3: Parameterize with JSON Data (15 minutes)

Use the loaded data in parameterized tests:

```python
# test_calculator_data_driven.py
import pytest
from calculator import Calculator
from conftest import load_test_cases


# Load test cases at module level
addition_cases = load_test_cases("calculator_tests.json", "addition_tests")
division_cases = load_test_cases("calculator_tests.json", "division_tests")
division_by_zero_cases = load_test_cases("calculator_tests.json", "division_by_zero_tests")


class TestCalculatorDataDriven:
    
    @pytest.fixture(autouse=True)
    def setup(self):
        self.calc = Calculator()
    
    @pytest.mark.parametrize(
        "a, b, expected, description",
        [(c["a"], c["b"], c["expected"], c["description"]) for c in addition_cases],
        ids=[c["description"] for c in addition_cases]
    )
    def test_addition(self, a, b, expected, description):
        """Test addition with data from JSON."""
        result = self.calc.add(a, b)
        assert result == expected, f"Failed: {description}"
    
    @pytest.mark.parametrize(
        "a, b, expected, description",
        [(c["a"], c["b"], c["expected"], c["description"]) for c in division_cases],
        ids=[c["description"] for c in division_cases]
    )
    def test_division(self, a, b, expected, description):
        """Test division with data from JSON."""
        result = self.calc.divide(a, b)
        assert result == pytest.approx(expected), f"Failed: {description}"
    
    @pytest.mark.parametrize(
        "a, b, description",
        [(c["a"], c["b"], c["description"]) for c in division_by_zero_cases],
        ids=[c["description"] for c in division_by_zero_cases]
    )
    def test_division_by_zero(self, a, b, description):
        """Test that division by zero raises exception."""
        with pytest.raises(ZeroDivisionError):
            self.calc.divide(a, b)
```

### Task 4: Create Test Data for User Validation (10 minutes)

Create `test_data/user_validation_tests.json`:

```json
{
    "valid_emails": [
        {"email": "user@example.com", "description": "standard email"},
        {"email": "user.name@domain.co.uk", "description": "email with subdomain"},
        {"email": "user+tag@example.com", "description": "email with plus sign"}
    ],
    "invalid_emails": [
        {"email": "", "error": "empty", "description": "empty string"},
        {"email": "noatsign", "error": "missing @", "description": "no at sign"},
        {"email": "@nodomain", "error": "invalid format", "description": "no local part"},
        {"email": "user@", "error": "invalid format", "description": "no domain"}
    ],
    "valid_passwords": [
        {"password": "SecurePass123!", "description": "strong password"},
        {"password": "MyP@ssw0rd", "description": "mixed characters"}
    ],
    "invalid_passwords": [
        {"password": "short", "error": "too short", "description": "under 8 chars"},
        {"password": "nouppercase123", "error": "no uppercase", "description": "missing uppercase"},
        {"password": "NOLOWERCASE123", "error": "no lowercase", "description": "missing lowercase"}
    ]
}
```

Write parameterized tests for this data.

### Task 5: Advanced - Pytest Hook for Data Loading (10 minutes)

Use pytest hooks for more dynamic test generation:

```python
# conftest.py
def pytest_generate_tests(metafunc):
    """Generate tests dynamically based on fixture names."""
    
    if "email_test_case" in metafunc.fixturenames:
        cases = load_test_cases("user_validation_tests.json", "valid_emails")
        cases += load_test_cases("user_validation_tests.json", "invalid_emails")
        metafunc.parametrize(
            "email_test_case",
            cases,
            ids=[c["description"] for c in cases]
        )
```

## Directory Structure

```
tests/
├── conftest.py
├── test_data/
│   ├── calculator_tests.json
│   └── user_validation_tests.json
├── test_calculator_data_driven.py
└── test_user_validation_data_driven.py
```

## Definition of Done

- [ ] At least 2 JSON test data files created
- [ ] Fixture for loading test data
- [ ] At least 10 test cases loaded from JSON
- [ ] Parameterized tests using the JSON data
- [ ] Tests have descriptive IDs from the "description" field
- [ ] Both success and failure cases covered
- [ ] Tests use `pytest.approx()` for float comparisons
- [ ] All tests pass

## Tips for Data-Driven Testing

```python
# Use pytest.approx for floating point
assert result == pytest.approx(3.14159, rel=1e-5)

# Use descriptive IDs
@pytest.mark.parametrize(
    "input,expected",
    [("a", 1), ("b", 2)],
    ids=["test_a_returns_1", "test_b_returns_2"]
)

# Load from CSV with pandas
import pandas as pd
test_cases = pd.read_csv("tests.csv").to_dict('records')

# Use indirect fixtures for complex setup
@pytest.mark.parametrize("user_type", ["admin", "user"], indirect=True)
def test_permissions(user_type):
    ...
```

## Submission

Commit with message:
```
feat(week6): Complete data-driven pytest exercise
```

