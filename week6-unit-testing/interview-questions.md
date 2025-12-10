# Interview Questions: Week 6 - Unit Testing

This question bank validates trainee retention of unit testing concepts in both Java (JUnit5/Mockito) and Python (Pytest/Mock) ecosystems, plus Allure reporting. Questions follow the **70-25-5 distribution**: 70% Beginner, 25% Intermediate, 5% Advanced.

---

## Beginner (Foundational)

### Q1: What is a unit test and what makes it different from other types of tests?

**Keywords:** Isolation, Single Method/Function, Fast, Testing Pyramid

<details>
<summary>Click to Reveal Answer</summary>

A **unit test** verifies the behavior of a small, isolated piece of code—typically a single method or function. The "unit" is the smallest testable component of your application. Unit tests differ from integration tests (which test multiple components together) and E2E tests (which test entire user flows) in that they:

1. Test code **in isolation** without external dependencies
2. Are **fast**—executing in milliseconds, not seconds
3. Form the **foundation of the testing pyramid** (many unit tests, fewer integration tests, even fewer E2E tests)

</details>

---

### Q2: What does the FIRST acronym stand for in unit testing, and why is each principle important?

**Keywords:** Fast, Isolated, Repeatable, Self-validating, Timely

<details>
<summary>Click to Reveal Answer</summary>

**FIRST** captures the essential qualities of effective unit tests:

- **F - Fast**: Tests should execute in milliseconds. Slow tests discourage developers from running them frequently.
- **I - Isolated/Independent**: Each test should be self-contained, not depending on other tests or shared state.
- **R - Repeatable**: Tests must produce the same result every time, in any environment.
- **S - Self-validating**: Tests must automatically pass or fail through assertions, with no manual inspection required.
- **T - Timely**: Tests should be written close in time to the code being tested (ideally via TDD).

</details>

---

### Q3: In JUnit5, what is the difference between `@BeforeEach` and `@BeforeAll`?

**Keywords:** Test Lifecycle, Setup, Static, Per-Test vs Once

<details>
<summary>Click to Reveal Answer</summary>

- **`@BeforeEach`**: Runs **before each** test method. Used for setting up fresh test state for every test. Does not need to be static.

- **`@BeforeAll`**: Runs **once before all tests** in the class. Must be static (unless using `@TestInstance(PER_CLASS)`). Used for expensive setup that can be shared, like loading large datasets or starting mock servers.

The execution order is: `@BeforeAll` → (`@BeforeEach` → `@Test` → `@AfterEach`) × N tests → `@AfterAll`

</details>

---

### Q4: What is the purpose of `assertAll()` in JUnit5?

**Keywords:** Grouped Assertions, Multiple Failures, Comprehensive Validation
**Hint:** Think about what happens when testing multiple properties of an object.

<details>
<summary>Click to Reveal Answer</summary>

`assertAll()` runs **all assertions and reports all failures**, rather than stopping at the first failure. This is valuable when validating multiple properties of an object.

```java
assertAll("User properties",
    () -> assertEquals("John", user.getFirstName()),
    () -> assertEquals("Doe", user.getLastName()),
    () -> assertEquals(30, user.getAge())
);
```

Without `assertAll`, if the first assertion fails, you wouldn't know about the second or third failures until you fix the first one. With `assertAll`, you see all failures at once, making debugging more efficient.

</details>

---

### Q5: What is the difference between a Mock and a Spy in Mockito?

**Keywords:** Complete Fake, Partial Mock, Real Behavior, Override
**Hint:** Consider what happens when you call unstubbed methods.

<details>
<summary>Click to Reveal Answer</summary>

- **Mock**: A **complete fake** with no real behavior. All methods return default values (null, 0, false, empty collections) unless stubbed. Use mocks when you need complete isolation.

- **Spy**: A **real object with selective stubbing**. Unstubbed methods execute the real implementation. You can override specific methods while keeping others real. Use spies when you need mostly real behavior with a few controlled methods.

```java
// Mock: Does nothing, returns 0
List<String> mockList = mock(List.class);
mockList.add("item");  // Does nothing

// Spy: Actually adds the item
List<String> spyList = spy(new ArrayList<>());
spyList.add("item");  // Really adds
```

</details>

---

### Q6: What is stubbing in Mockito and what methods are used to stub behavior?

**Keywords:** when().thenReturn(), thenThrow(), Define Mock Behavior

<details>
<summary>Click to Reveal Answer</summary>

**Stubbing** defines how a mock behaves when its methods are called. It tells the mock what to return when specific methods are invoked.

Key stubbing methods:
- **`when(mock.method()).thenReturn(value)`**: Return a specific value
- **`when(mock.method()).thenThrow(exception)`**: Throw an exception
- **`when(mock.method()).thenAnswer(invocation -> ...)`**: Return dynamic values based on input
- **`doNothing().when(mock).voidMethod()`**: For void methods

Example:
```java
when(repository.findById(1L)).thenReturn(new User("John"));
when(repository.findById(-1L)).thenThrow(new IllegalArgumentException());
```

</details>

---

### Q7: What is the difference between regression testing and re-testing?

**Keywords:** Previously Working Features, Bug Fix Verification, Unintended Changes

<details>
<summary>Click to Reveal Answer</summary>

- **Regression testing**: Verifies that **previously working features still work** after code changes. The term "regression" means going backward—ensuring software hasn't regressed to a broken state. It's about catching unintended side effects.

- **Re-testing** (Confirmation testing): Verifies that a **specific bug fix actually works**. After a defect is fixed, re-testing confirms the fix resolved the issue.

Example: A developer fixes a login bug (re-test the login) and runs the entire test suite to ensure the fix didn't break other features (regression testing).

</details>

---

### Q8: How does Pytest differ from Python's built-in unittest module?

**Keywords:** assert Statement, Fixtures, Test Discovery, Boilerplate

<details>
<summary>Click to Reveal Answer</summary>

| Feature | Pytest | unittest |
|---------|--------|----------|
| **Assertions** | Simple `assert` statement | Verbose methods like `self.assertEqual()` |
| **Setup/Teardown** | Flexible fixtures | Rigid setUp/tearDown methods |
| **Test Discovery** | Automatic (finds `test_*.py`) | Requires boilerplate |
| **Test Class** | Optional (functions work) | Required (inherit TestCase) |
| **Output** | Detailed diffs | Basic |
| **Plugins** | Rich ecosystem | Limited |

Pytest is preferred for its simplicity and power—over 75% of Python projects use it.

</details>

---

### Q9: What is a Pytest fixture and what are the different fixture scopes?

**Keywords:** @pytest.fixture, function, class, module, session, Dependency Injection

<details>
<summary>Click to Reveal Answer</summary>

A **fixture** is Pytest's mechanism for test setup, providing test data or resources via dependency injection. Created with the `@pytest.fixture` decorator.

**Fixture scopes** control how often the fixture is created:

| Scope | Created | Use Case |
|-------|---------|----------|
| `function` (default) | Each test | Fresh state per test |
| `class` | Once per test class | Shared within class |
| `module` | Once per module | Shared within file |
| `session` | Once per test run | Expensive resources (DB connections) |

```python
@pytest.fixture(scope="session")
def database_connection():
    conn = connect_database()
    yield conn
    conn.close()
```

</details>

---

### Q10: What is the difference between Python's Mock and MagicMock?

**Keywords:** Magic Methods, __len__, __iter__, Pre-configured
**Hint:** Think about what happens when you call `len()` on a mock.

<details>
<summary>Click to Reveal Answer</summary>

- **Mock**: A flexible fake object that accepts any attribute/method call. Does **not** support magic methods (dunder methods) by default.

- **MagicMock**: Mock with **pre-configured magic methods** like `__len__`, `__iter__`, `__getitem__`, `__str__`, etc.

```python
from unittest.mock import Mock, MagicMock

regular_mock = Mock()
# len(regular_mock)  # TypeError!

magic_mock = MagicMock()
len(magic_mock)        # Returns 0 (works)
magic_mock[0]          # Works (__getitem__)
iter(magic_mock)       # Works (__iter__)
```

**Rule of thumb**: Use MagicMock by default, Mock for simple cases where you don't need magic methods.

</details>

---

## Intermediate (Application/Scenario)

### Q11: When patching in Python, why must you patch "where it's used, not where it's defined"?

**Keywords:** Import Location, Module Namespace, @patch
**Hint:** Consider how Python's import system creates references.

<details>
<summary>Click to Reveal Answer</summary>

When Python imports `from external_lib import api_client`, it creates a **local reference** to `api_client` in the importing module's namespace. The original `external_lib.api_client` and `mymodule.api_client` become separate references.

If you patch `external_lib.api_client`, you change the original—but `mymodule` already has its own reference that won't be affected.

```python
# mymodule.py
from external_lib import api_client  # Creates local reference

def my_function():
    return api_client.call()  # Uses local reference

# test_mymodule.py
# ❌ WRONG - patches original, mymodule still uses old reference
@patch('external_lib.api_client')

# ✅ CORRECT - patches where mymodule looks for it
@patch('mymodule.api_client')
```

**Golden Rule**: Patch at the **import location** (where the object is used), not where it's defined.

</details>

---

### Q12: A developer's test passes locally but fails in CI. The test involves checking today's date. What FIRST principle is violated and how would you fix it?

**Keywords:** Repeatable, Deterministic, Fixed Values, Environment Independence

<details>
<summary>Click to Reveal Answer</summary>

The **Repeatable** principle is violated. Tests must produce the same result every time, in any environment.

**The Problem**: Using `LocalDate.now()` or similar means the test depends on when it runs:
```java
@Test
void isWeekend_today() {
    LocalDate today = LocalDate.now();  // Non-deterministic!
    assertTrue(DateUtils.isWeekend(today));  // Fails on weekdays
}
```

**The Fix**: Use **fixed, deterministic dates**:
```java
@Test
void isWeekend_saturday_returnsTrue() {
    LocalDate saturday = LocalDate.of(2024, 1, 6);  // Fixed date
    assertTrue(DateUtils.isWeekend(saturday));
}
```

Alternatively, inject a clock/time provider that can be controlled in tests.

</details>

---

### Q13: You're testing a PaymentService that depends on a PaymentGateway (external API). How would you structure this test using Mockito to verify the payment was processed?

**Keywords:** @Mock, @InjectMocks, when().thenReturn(), verify()
**Hint:** You need to both stub the response AND verify the call was made.

<details>
<summary>Click to Reveal Answer</summary>

```java
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentGateway paymentGateway;  // Mock external dependency
    
    @InjectMocks
    private PaymentService paymentService;  // Inject mock automatically
    
    @Test
    void processPayment_validCard_succeeds() {
        // Arrange: Stub the gateway response
        when(paymentGateway.charge(anyDouble()))
            .thenReturn(new PaymentResult(true, "TXN123"));
        
        // Act: Call the service
        PaymentResult result = paymentService.processPayment(99.99);
        
        // Assert: Verify the result
        assertTrue(result.isSuccess());
        
        // Verify: Confirm gateway was called correctly
        verify(paymentGateway).charge(99.99);
    }
}
```

Key patterns:
1. **@Mock** creates the fake gateway
2. **@InjectMocks** automatically injects it into the service
3. **when().thenReturn()** stubs the expected behavior
4. **verify()** confirms the interaction happened

</details>

---

### Q14: Your team wants to integrate test reporting into their CI/CD pipeline. Explain the difference between `allure serve` and `allure generate`, and which is appropriate for CI/CD.

**Keywords:** Local Review, Static HTML, CI/CD Artifacts, Shareable

<details>
<summary>Click to Reveal Answer</summary>

- **`allure serve allure-results`**: Opens an interactive report in the browser, runs a local server, auto-refreshes. Best for **local development** and quick reviews. Not suitable for CI/CD because it blocks and requires a browser.

- **`allure generate allure-results -o allure-report --clean`**: Creates **static HTML files** that can be archived, shared, or hosted. Best for **CI/CD** because:
  - Files can be uploaded as artifacts
  - Can be published to a web server or GitHub Pages
  - No running server required
  - Can be accessed later

**CI/CD workflow**:
```bash
pytest --alluredir=allure-results
allure generate allure-results -o allure-report --clean
# Upload allure-report/ as artifact or publish to web server
```

</details>

---

## Advanced (Deep Dive)

### Q15: In a test suite with 1000 unit tests, some tests share expensive setup (database schema creation) while others need completely fresh state. Design a fixture strategy using Pytest scopes that balances efficiency and isolation.

**Keywords:** Session Scope, Function Scope, Module Scope, Fixture Dependencies, yield, Transaction Rollback

<details>
<summary>Click to Reveal Answer</summary>

**Strategy: Layered Fixtures with Appropriate Scopes**

```python
# conftest.py
import pytest

@pytest.fixture(scope="session")
def database_engine():
    """Session scope: Create database schema once for all tests."""
    engine = create_engine("sqlite:///:memory:")
    Base.metadata.create_all(engine)  # Schema creation - expensive
    yield engine
    engine.dispose()

@pytest.fixture(scope="function")
def db_session(database_engine):
    """Function scope: Fresh transaction per test, rolled back after."""
    connection = database_engine.connect()
    transaction = connection.begin()
    session = Session(bind=connection)
    
    yield session
    
    # Teardown: Rollback transaction for isolation
    session.close()
    transaction.rollback()
    connection.close()

@pytest.fixture(scope="function")
def sample_users(db_session):
    """Fresh test data for each test."""
    users = [User(name="John"), User(name="Jane")]
    db_session.add_all(users)
    db_session.flush()
    return users
```

**Design Principles:**

1. **Session-scoped** for expensive, shareable setup (schema creation, test containers)
2. **Function-scoped** for test data that must be fresh
3. **Transaction rollback** pattern gives isolation without recreating database
4. **Fixture dependencies** automatically wire the hierarchy
5. **yield** ensures cleanup runs even if tests fail

This approach runs 1000 tests with one schema creation (seconds) vs. 1000 schema creations (minutes), while maintaining complete test isolation through transaction rollback.

</details>

---

## Quick Reference: Topic Coverage

| Question | Topic | Day |
|----------|-------|-----|
| Q1 | Unit Testing Definition | Monday |
| Q2 | FIRST Principles | Monday |
| Q3 | JUnit5 Lifecycle | Monday |
| Q4 | assertAll() | Monday |
| Q5 | Mock vs Spy | Tuesday |
| Q6 | Stubbing | Tuesday |
| Q7 | Regression vs Re-testing | Tuesday |
| Q8 | Pytest vs unittest | Wednesday |
| Q9 | Fixtures | Wednesday |
| Q10 | Mock vs MagicMock | Thursday |
| Q11 | Patching Location | Thursday |
| Q12 | FIRST - Repeatable | Monday |
| Q13 | Mockito Test Structure | Tuesday |
| Q14 | Allure Commands | Friday |
| Q15 | Advanced Fixture Design | Wednesday/Thursday |

---

*Generated by Quality Assurance Agent | Week 6: Unit Testing*

