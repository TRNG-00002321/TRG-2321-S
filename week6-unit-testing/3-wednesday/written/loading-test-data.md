# Loading Test Data: Data-Driven Testing in Python

## Learning Objectives
- Implement strategies for managing test data
- Load test data from JSON and CSV files
- Use fixtures for test data management
- Apply parameterization with external data files

## Why This Matters

Real-world tests often need substantial test data—user records, product catalogs, transaction histories. Hardcoding this data makes tests brittle and hard to maintain. Learning to externalize test data keeps tests clean, makes data reusable, and enables non-developers to contribute test cases.

## The Concept

### Test Data Strategies

| Strategy | Best For | Example |
|----------|----------|---------|
| Inline | Simple, few values | `assert add(2, 3) == 5` |
| Fixtures | Shared, structured data | User objects |
| External files | Large datasets, shared across tests | CSV, JSON |
| Factories | Dynamic, realistic data | Faker library |

### Loading JSON Test Data

```python
import json
import pytest
from pathlib import Path

@pytest.fixture
def test_users():
    """Load users from JSON file."""
    data_file = Path(__file__).parent / "data" / "users.json"
    with open(data_file) as f:
        return json.load(f)

def test_all_users_have_email(test_users):
    for user in test_users:
        assert "email" in user
        assert "@" in user["email"]
```

**`tests/data/users.json`:**
```json
[
    {"name": "John", "email": "john@example.com", "age": 30},
    {"name": "Jane", "email": "jane@example.com", "age": 25},
    {"name": "Bob", "email": "bob@example.com", "age": 40}
]
```

### Loading CSV Test Data

```python
import csv
import pytest
from pathlib import Path

def load_csv_data(filename):
    """Load test data from CSV file."""
    data_file = Path(__file__).parent / "data" / filename
    with open(data_file, newline='') as f:
        reader = csv.DictReader(f)
        return list(reader)

@pytest.fixture
def calculator_test_cases():
    return load_csv_data("calculator_tests.csv")

def test_addition_from_csv(calculator_test_cases):
    for case in calculator_test_cases:
        a = int(case["a"])
        b = int(case["b"])
        expected = int(case["expected"])
        assert a + b == expected
```

**`tests/data/calculator_tests.csv`:**
```csv
a,b,expected
1,2,3
10,20,30
-5,5,0
100,200,300
```

### Parameterization with External Data

```python
import pytest
import json

def load_test_cases():
    with open("tests/data/validation_cases.json") as f:
        return json.load(f)

@pytest.mark.parametrize(
    "input_value,expected_valid",
    [(case["input"], case["valid"]) for case in load_test_cases()]
)
def test_validation(input_value, expected_valid):
    result = validator.is_valid(input_value)
    assert result == expected_valid
```

### Fixture Factory Pattern

For dynamic test data:

```python
import pytest
from faker import Faker

fake = Faker()

@pytest.fixture
def user_factory():
    """Factory for creating test users."""
    def _create_user(name=None, email=None, age=None):
        return {
            "name": name or fake.name(),
            "email": email or fake.email(),
            "age": age or fake.random_int(18, 80)
        }
    return _create_user

def test_create_multiple_users(user_factory):
    users = [user_factory() for _ in range(10)]
    assert len(users) == 10
    assert all("@" in u["email"] for u in users)
```

## Code Example

### Complete Data Loading Strategy

```python
# tests/conftest.py
import pytest
import json
from pathlib import Path

DATA_DIR = Path(__file__).parent / "data"

@pytest.fixture(scope="session")
def all_test_products():
    """Load product catalog once per session."""
    with open(DATA_DIR / "products.json") as f:
        return json.load(f)

@pytest.fixture
def single_product(all_test_products):
    """Get first product for single-item tests."""
    return all_test_products[0]

# tests/test_inventory.py
def test_product_has_required_fields(single_product):
    required = ["id", "name", "price", "stock"]
    for field in required:
        assert field in single_product

def test_all_products_have_positive_price(all_test_products):
    for product in all_test_products:
        assert product["price"] > 0
```

## Summary

- **Externalize test data** for maintainability and reuse
- Use **JSON** for structured hierarchical data
- Use **CSV** for tabular data (spreadsheet-editable)
- **Fixtures** manage data loading and scope
- **Parameterization** runs tests with multiple data sets
- **Factory pattern** creates dynamic test data

## Additional Resources

- [pytest-datafiles](https://pypi.org/project/pytest-datafiles/) - File fixtures plugin
- [Faker Library](https://faker.readthedocs.io/) - Generate realistic test data
- [Data-Driven Testing](https://www.guru99.com/data-driven-testing.html) - Methodology overview

