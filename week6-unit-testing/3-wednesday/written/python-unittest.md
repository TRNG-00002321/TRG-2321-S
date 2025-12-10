# Python unittest: The Built-in Testing Module

## Learning Objectives
- Understand Python's built-in unittest module
- Write tests using the TestCase class
- Use unittest assertion methods
- Run tests with the unittest runner

## Why This Matters

While Pytest is the modern choice, understanding unittest is essential. It's built into Python (no installation needed), many legacy projects use it, and Pytest can run unittest-style tests. Knowing both gives you flexibility when joining existing projects.

## The Concept

### unittest Basics

unittest is inspired by Java's JUnit (hence similar patterns):

```python
import unittest

class TestCalculator(unittest.TestCase):
    
    def test_addition(self):
        self.assertEqual(2 + 2, 4)
    
    def test_subtraction(self):
        self.assertEqual(10 - 5, 5)

if __name__ == '__main__':
    unittest.main()
```

### Key Components

| Component | Description |
|-----------|-------------|
| `TestCase` | Base class for test classes |
| `setUp()` | Run before each test method |
| `tearDown()` | Run after each test method |
| `setUpClass()` | Run once before all tests in class |
| `tearDownClass()` | Run once after all tests in class |

### setUp and tearDown

```python
import unittest

class TestDatabase(unittest.TestCase):
    
    @classmethod
    def setUpClass(cls):
        """Run once before all tests."""
        cls.db = Database.connect()
    
    def setUp(self):
        """Run before each test."""
        self.db.begin_transaction()
    
    def test_insert(self):
        self.db.insert({'name': 'John'})
        self.assertEqual(self.db.count(), 1)
    
    def test_delete(self):
        self.db.insert({'name': 'Jane'})
        self.db.delete(1)
        self.assertEqual(self.db.count(), 0)
    
    def tearDown(self):
        """Run after each test."""
        self.db.rollback()
    
    @classmethod
    def tearDownClass(cls):
        """Run once after all tests."""
        cls.db.close()
```

### unittest Assertions

```python
class TestAssertions(unittest.TestCase):
    
    def test_equality(self):
        self.assertEqual(1 + 1, 2)
        self.assertNotEqual(1 + 1, 3)
    
    def test_boolean(self):
        self.assertTrue(5 > 3)
        self.assertFalse(5 < 3)
    
    def test_none(self):
        self.assertIsNone(None)
        self.assertIsNotNone("value")
    
    def test_identity(self):
        a = [1, 2, 3]
        b = a
        self.assertIs(a, b)
    
    def test_membership(self):
        self.assertIn(3, [1, 2, 3])
        self.assertNotIn(4, [1, 2, 3])
    
    def test_exceptions(self):
        with self.assertRaises(ZeroDivisionError):
            1 / 0
    
    def test_almost_equal(self):
        self.assertAlmostEqual(0.1 + 0.2, 0.3, places=5)
```

### Running Tests

```bash
# Run all tests in a module
python -m unittest test_module

# Run specific test class
python -m unittest test_module.TestClass

# Run specific test method
python -m unittest test_module.TestClass.test_method

# Verbose output
python -m unittest -v test_module

# Discover and run all tests
python -m unittest discover
```

## Code Example

### Complete unittest Test Class

```python
import unittest
from datetime import datetime
from src.user import User, InvalidEmailError

class TestUser(unittest.TestCase):
    """Test cases for User class."""
    
    def setUp(self):
        """Create a fresh user for each test."""
        self.user = User(
            name="John Doe",
            email="john@example.com",
            age=30
        )
    
    def test_user_creation(self):
        """Test user is created with correct attributes."""
        self.assertEqual(self.user.name, "John Doe")
        self.assertEqual(self.user.email, "john@example.com")
        self.assertEqual(self.user.age, 30)
    
    def test_user_has_created_timestamp(self):
        """Test user gets a creation timestamp."""
        self.assertIsNotNone(self.user.created_at)
        self.assertIsInstance(self.user.created_at, datetime)
    
    def test_invalid_email_raises_error(self):
        """Test that invalid email raises InvalidEmailError."""
        with self.assertRaises(InvalidEmailError) as context:
            User("Jane", "not-an-email", 25)
        
        self.assertIn("Invalid email", str(context.exception))
    
    def test_user_string_representation(self):
        """Test __str__ returns meaningful string."""
        self.assertIn("John Doe", str(self.user))
        self.assertIn("john@example.com", str(self.user))

if __name__ == '__main__':
    unittest.main()
```

## Summary

- **unittest** is Python's built-in testing framework
- Tests inherit from **`unittest.TestCase`**
- Use **`setUp()`/`tearDown()`** for per-test setup/cleanup
- Use **`setUpClass()`/`tearDownClass()`** for one-time setup
- Rich set of **assertion methods** (`assertEqual`, `assertTrue`, etc.)
- Run with **`python -m unittest`**

## Additional Resources

- [unittest Documentation](https://docs.python.org/3/library/unittest.html) - Official docs
- [Python Testing with unittest](https://realpython.com/python-testing/) - Real Python tutorial
- [unittest vs pytest](https://www.guru99.com/pytest-tutorial.html) - Comparison guide

