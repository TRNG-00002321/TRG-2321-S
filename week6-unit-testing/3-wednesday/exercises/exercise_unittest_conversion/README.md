# Lab: unittest Conversion - MathOperations

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Beginner-Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | python-unittest.md, demo_unittest_basics.py |

## Learning Objectives
By completing this exercise, you will:
- Write tests using Python's built-in `unittest` module
- Understand `TestCase` class structure
- Use `setUp()` and `tearDown()` methods
- Apply unittest assertion methods
- Convert conceptual JUnit-style tests to Python unittest

## The Scenario

Your team has tests written in "pseudo-JUnit" format from a Java developer. Your task is to convert these test specifications to proper Python `unittest` tests for the `MathOperations` class.

## Core Tasks

### Task 1: Set Up unittest Test Class (10 minutes)

Create `test_math_operations.py`:

```python
import unittest
from math_operations import MathOperations


class TestMathOperations(unittest.TestCase):
    """Tests for the MathOperations class."""
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        self.math = MathOperations()
    
    def tearDown(self):
        """Clean up after each test method."""
        pass  # No cleanup needed for this simple class


if __name__ == '__main__':
    unittest.main()
```

### Task 2: Convert Basic Tests (15 minutes)

Convert these JUnit-style specifications to unittest:

**JUnit Spec:**
```java
@Test
void factorial_zero_returnsOne() {
    assertEquals(1, math.factorial(0));
}

@Test
void factorial_five_returns120() {
    assertEquals(120, math.factorial(5));
}
```

**Your Python unittest:**
```python
def test_factorial_zero_returns_one(self):
    self.assertEqual(self.math.factorial(0), 1)

def test_factorial_five_returns_120(self):
    self.assertEqual(self.math.factorial(5), 120)
```

**Convert these additional specs:**
- `fibonacci_zero_returnsZero`
- `fibonacci_ten_returns55`
- `isPrime_2_returnsTrue`
- `isPrime_4_returnsFalse`
- `gcd_48and18_returns6`

### Task 3: Convert Exception Tests (10 minutes)

**JUnit Spec:**
```java
@Test
void factorial_negative_throwsValueError() {
    assertThrows(ValueError.class, () -> math.factorial(-1));
}
```

**Your Python unittest:**
```python
def test_factorial_negative_raises_value_error(self):
    with self.assertRaises(ValueError):
        self.math.factorial(-1)

def test_factorial_negative_has_correct_message(self):
    with self.assertRaises(ValueError) as context:
        self.math.factorial(-1)
    self.assertIn("negative", str(context.exception).lower())
```

### Task 4: Use setUp for Shared Data (10 minutes)

Add a `setUpClass` for expensive one-time setup:

```python
class TestMathOperations(unittest.TestCase):
    
    @classmethod
    def setUpClass(cls):
        """Run once before all tests in this class."""
        cls.prime_cache = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29}
        print("Prime cache initialized")
    
    @classmethod
    def tearDownClass(cls):
        """Run once after all tests in this class."""
        print("Cleaning up class resources")
    
    def setUp(self):
        """Run before each test method."""
        self.math = MathOperations()
```

### Task 5: Use Various Assertions (10 minutes)

Write tests using different unittest assertions:

```python
def test_is_prime_returns_boolean(self):
    result = self.math.is_prime(7)
    self.assertIsInstance(result, bool)

def test_prime_list_contains_expected(self):
    primes = self.math.get_primes_up_to(10)
    self.assertIn(7, primes)
    self.assertNotIn(4, primes)

def test_factorial_large_number(self):
    result = self.math.factorial(10)
    self.assertGreater(result, 1000000)  # 10! = 3,628,800

def test_gcd_returns_positive(self):
    result = self.math.gcd(-12, 18)
    self.assertGreaterEqual(result, 0)
```

## Starter Code

```python
# math_operations.py
class MathOperations:
    """Mathematical operations for testing practice."""
    
    def factorial(self, n: int) -> int:
        """Calculate factorial of n."""
        if n < 0:
            raise ValueError("Factorial not defined for negative numbers")
        if n <= 1:
            return 1
        return n * self.factorial(n - 1)
    
    def fibonacci(self, n: int) -> int:
        """Return the nth Fibonacci number (0-indexed)."""
        if n < 0:
            raise ValueError("Fibonacci not defined for negative indices")
        if n <= 1:
            return n
        a, b = 0, 1
        for _ in range(2, n + 1):
            a, b = b, a + b
        return b
    
    def is_prime(self, n: int) -> bool:
        """Check if n is a prime number."""
        if n < 2:
            return False
        if n == 2:
            return True
        if n % 2 == 0:
            return False
        for i in range(3, int(n ** 0.5) + 1, 2):
            if n % i == 0:
                return False
        return True
    
    def get_primes_up_to(self, n: int) -> list:
        """Return list of primes up to n."""
        return [x for x in range(2, n + 1) if self.is_prime(x)]
    
    def gcd(self, a: int, b: int) -> int:
        """Calculate greatest common divisor."""
        a, b = abs(a), abs(b)
        while b:
            a, b = b, a % b
        return a
```

## unittest Assertion Methods

| Method | Description |
|--------|-------------|
| `assertEqual(a, b)` | a == b |
| `assertNotEqual(a, b)` | a != b |
| `assertTrue(x)` | bool(x) is True |
| `assertFalse(x)` | bool(x) is False |
| `assertIs(a, b)` | a is b |
| `assertIsNone(x)` | x is None |
| `assertIsNotNone(x)` | x is not None |
| `assertIn(a, b)` | a in b |
| `assertNotIn(a, b)` | a not in b |
| `assertIsInstance(a, b)` | isinstance(a, b) |
| `assertRaises(exc)` | Exception raised |
| `assertGreater(a, b)` | a > b |
| `assertLess(a, b)` | a < b |
| `assertAlmostEqual(a, b)` | round(a-b, 7) == 0 |

## Running unittest Tests

```bash
# Run all tests in file
python -m unittest test_math_operations

# Run with verbose output
python -m unittest test_math_operations -v

# Run specific test class
python -m unittest test_math_operations.TestMathOperations

# Run specific test method
python -m unittest test_math_operations.TestMathOperations.test_factorial_zero_returns_one

# Run via pytest (also works!)
pytest test_math_operations.py -v
```

## Definition of Done

- [ ] Test class extends `unittest.TestCase`
- [ ] `setUp()` method creates `MathOperations` instance
- [ ] At least 3 tests for `factorial()`
- [ ] At least 2 tests for `fibonacci()`
- [ ] At least 3 tests for `is_prime()`
- [ ] At least 2 tests for `gcd()`
- [ ] At least 2 exception tests with `assertRaises`
- [ ] Uses at least 5 different assertion methods
- [ ] All tests pass

## Submission

Commit with message:
```
feat(week6): Complete unittest conversion exercise
```

