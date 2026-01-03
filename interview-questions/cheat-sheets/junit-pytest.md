# JUnit & Pytest Cheat Sheet

## JUnit 5 (Java)

### Annotations

| Annotation | Description |
|------------|-------------|
| `@Test` | Marks method as test |
| `@BeforeEach` | Run before each test |
| `@AfterEach` | Run after each test |
| `@BeforeAll` | Run once before all tests (static) |
| `@AfterAll` | Run once after all tests (static) |
| `@Disabled` | Skip test |
| `@DisplayName("text")` | Custom test name |
| `@Nested` | Nested test class |
| `@Tag("smoke")` | Tag for filtering |
| `@Timeout(5)` | Fail if exceeds 5 seconds |

### Assertions

```java
import static org.junit.jupiter.api.Assertions.*;

// Basic assertions
assertEquals(expected, actual);
assertEquals(expected, actual, "message");
assertNotEquals(unexpected, actual);

// Boolean
assertTrue(condition);
assertFalse(condition);

// Null
assertNull(object);
assertNotNull(object);

// Same reference
assertSame(expected, actual);
assertNotSame(unexpected, actual);

// Array
assertArrayEquals(expectedArray, actualArray);

// Exception
assertThrows(IllegalArgumentException.class, () -> {
    methodThatThrows();
});

// Timeout
assertTimeout(Duration.ofSeconds(5), () -> {
    slowMethod();
});

// Multiple assertions
assertAll(
    () -> assertEquals("a", result.getA()),
    () -> assertEquals("b", result.getB()),
    () -> assertNotNull(result.getC())
);
```

### Test Structure

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @Test
    @DisplayName("Should add two positive numbers")
    void addTwoPositiveNumbers() {
        // Arrange
        int a = 5, b = 3;
        
        // Act
        int result = calculator.add(a, b);
        
        // Assert
        assertEquals(8, result);
    }
    
    @Test
    void divideByZero_shouldThrowException() {
        assertThrows(ArithmeticException.class, () -> {
            calculator.divide(10, 0);
        });
    }
    
    @AfterEach
    void tearDown() {
        calculator = null;
    }
}
```

### Parameterized Tests

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

@ParameterizedTest
@ValueSource(strings = {"racecar", "radar", "level"})
void isPalindrome_shouldReturnTrue(String word) {
    assertTrue(StringUtils.isPalindrome(word));
}

@ParameterizedTest
@CsvSource({
    "1, 2, 3",
    "5, 5, 10",
    "-1, 1, 0"
})
void add_shouldReturnSum(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}

@ParameterizedTest
@CsvFileSource(resources = "/test-data.csv")
void fromCsvFile(String input, String expected) {
    assertEquals(expected, process(input));
}

@ParameterizedTest
@MethodSource("provideTestData")
void fromMethod(String input, int expected) {
    assertEquals(expected, input.length());
}

static Stream<Arguments> provideTestData() {
    return Stream.of(
        Arguments.of("hello", 5),
        Arguments.of("world", 5)
    );
}
```

### Nested Tests

```java
@DisplayName("Calculator Tests")
class CalculatorTest {
    
    @Nested
    @DisplayName("Addition Tests")
    class AdditionTests {
        @Test
        void addPositiveNumbers() { }
        
        @Test
        void addNegativeNumbers() { }
    }
    
    @Nested
    @DisplayName("Division Tests")
    class DivisionTests {
        @Test
        void divideValidNumbers() { }
        
        @Test
        void divideByZero() { }
    }
}
```

---

## Pytest (Python)

### Basic Tests

```python
# test_calculator.py

def test_addition():
    assert 2 + 2 == 4

def test_string():
    assert "hello".upper() == "HELLO"

def test_list():
    items = [1, 2, 3]
    assert len(items) == 3
    assert 2 in items
```

### Assertions

```python
# Basic assertions
assert result == expected
assert result != unexpected
assert result is None
assert result is not None
assert result  # Truthy
assert not result  # Falsy

# Collections
assert item in collection
assert item not in collection
assert len(collection) == 3

# Approximately equal (floats)
assert result == pytest.approx(3.14, rel=0.01)

# Exception
with pytest.raises(ValueError):
    function_that_raises()

with pytest.raises(ValueError, match="invalid"):
    function_that_raises()
```

### Fixtures

```python
import pytest

@pytest.fixture
def sample_user():
    """Create a sample user for testing"""
    return {"name": "Alice", "age": 30}

def test_user_name(sample_user):
    assert sample_user["name"] == "Alice"

# Fixture with setup and teardown
@pytest.fixture
def database():
    # Setup
    db = connect_to_database()
    db.begin_transaction()
    
    yield db  # Test runs here
    
    # Teardown
    db.rollback()
    db.close()

# Fixture scopes
@pytest.fixture(scope="function")  # Default - each test
@pytest.fixture(scope="class")     # Once per test class
@pytest.fixture(scope="module")    # Once per file
@pytest.fixture(scope="session")   # Once per test run
```

### Parametrization

```python
import pytest

@pytest.mark.parametrize("input,expected", [
    ("hello", 5),
    ("world", 5),
    ("", 0),
])
def test_string_length(input, expected):
    assert len(input) == expected

@pytest.mark.parametrize("a,b,expected", [
    (1, 2, 3),
    (-1, 1, 0),
    (0, 0, 0),
])
def test_addition(a, b, expected):
    assert add(a, b) == expected
```

### Markers

```python
import pytest

@pytest.mark.slow
def test_slow_operation():
    # Long-running test
    pass

@pytest.mark.skip(reason="Not implemented yet")
def test_future_feature():
    pass

@pytest.mark.skipif(sys.version < "3.9", reason="Requires Python 3.9+")
def test_new_feature():
    pass

@pytest.mark.xfail(reason="Known bug")
def test_known_issue():
    pass

# Run with markers
# pytest -m "slow"
# pytest -m "not slow"
# pytest -m "smoke and not slow"
```

### conftest.py

```python
# conftest.py - Shared fixtures

import pytest

@pytest.fixture(scope="session")
def browser():
    driver = webdriver.Chrome()
    yield driver
    driver.quit()

@pytest.fixture
def api_client():
    return APIClient(base_url="https://api.example.com")
```

### Running Tests

```bash
# Run all tests
pytest

# Run specific file
pytest test_file.py

# Run specific test
pytest test_file.py::test_function

# Run with pattern
pytest -k "login"

# Run with marker
pytest -m "smoke"

# Verbose output
pytest -v

# Show print statements
pytest -s

# Stop on first failure
pytest -x

# Run last failed
pytest --lf

# Parallel execution
pytest -n auto
```

---

## Mocking Comparison

### Mockito (Java)

```java
import static org.mockito.Mockito.*;

// Create mock
UserRepository mockRepo = mock(UserRepository.class);

// Stub method
when(mockRepo.findById(1)).thenReturn(new User("Alice"));
when(mockRepo.save(any(User.class))).thenThrow(new RuntimeException());

// Verify interactions
verify(mockRepo).findById(1);
verify(mockRepo, times(2)).save(any());
verify(mockRepo, never()).delete(any());

// Capture arguments
ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
verify(mockRepo).save(captor.capture());
User savedUser = captor.getValue();
```

### pytest-mock (Python)

```python
def test_with_mock(mocker):
    # Patch function
    mock_api = mocker.patch('module.api_call')
    mock_api.return_value = {"status": "ok"}
    
    result = service.process()
    
    mock_api.assert_called_once()
    assert result == "ok"

def test_with_side_effect(mocker):
    mock_api = mocker.patch('module.api_call')
    mock_api.side_effect = [
        ConnectionError("Failed"),
        {"status": "ok"}
    ]
    
    # First call raises, second succeeds
```
