# Weekly Knowledge Check: Week 6 - Unit Testing

This quiz covers JUnit5, Mockito, Pytest, Python mocking, and Allure reporting. Test your understanding of the concepts from this week's training!

---

## Part 1: Multiple Choice - JUnit5 & Java Unit Testing

### 1. What is the JUnit5 equivalent of JUnit4's `@Before` annotation?
- [ ] A) `@Before`
- [ ] B) `@BeforeTest`
- [ ] C) `@BeforeEach`
- [ ] D) `@Setup`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) `@BeforeEach`

**Explanation:** JUnit5 renamed `@Before` to `@BeforeEach` to more clearly indicate that the annotated method runs before each test method in the class.
- **Why others are wrong:**
  - A) `@Before` is JUnit4 syntax, not JUnit5.
  - B) `@BeforeTest` is not a valid JUnit annotation.
  - D) `@Setup` is not a valid JUnit annotation.
</details>

---

### 2. Which three modules comprise JUnit5's architecture?
- [ ] A) JUnit Core, JUnit Engine, JUnit Runner
- [ ] B) JUnit Platform, JUnit Jupiter, JUnit Vintage
- [ ] C) JUnit API, JUnit Runtime, JUnit Legacy
- [ ] D) JUnit Base, JUnit Modern, JUnit Classic

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) JUnit Platform, JUnit Jupiter, JUnit Vintage

**Explanation:** JUnit5 is modular: **JUnit Platform** is the foundation for launching testing frameworks, **JUnit Jupiter** provides the new programming model and annotations, and **JUnit Vintage** enables running JUnit3/4 tests for backward compatibility.
- **Why others are wrong:**
  - A, C, D) These are not valid JUnit5 module names.
</details>

---

### 3. What is the minimum Java version required for JUnit5?
- [ ] A) Java 5
- [ ] B) Java 7
- [ ] C) Java 8
- [ ] D) Java 11

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Java 8

**Explanation:** JUnit5 requires Java 8 or higher because it leverages modern Java features like lambda expressions and the Stream API. JUnit4 supported Java 5+, but JUnit5 modernized this requirement.
- **Why others are wrong:**
  - A) Java 5 was the minimum for JUnit4.
  - B) Java 7 is not the minimum requirement.
  - D) While Java 11 works, Java 8 is the minimum.
</details>

---

### 4. What does the "FIRST" acronym stand for in unit testing principles?
- [ ] A) Fast, Independent, Repeatable, Self-validating, Timely
- [ ] B) Functional, Isolated, Reliable, Simple, Thorough
- [ ] C) Fast, Isolated, Repeatable, Self-validating, Timely
- [ ] D) Full, Integrated, Robust, Secure, Tested

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Fast, Isolated, Repeatable, Self-validating, Timely

**Explanation:** The FIRST principles capture essential qualities of effective unit tests: **F**ast (milliseconds), **I**solated (independent), **R**epeatable (consistent results), **S**elf-validating (automatic pass/fail), **T**imely (written close to the code).
- **Why others are wrong:**
  - A) Uses "Independent" instead of "Isolated" - the correct term emphasizes test isolation from dependencies.
  - B, D) These are fabricated acronyms not used in testing literature.
</details>

---

### 5. When comparing floating-point numbers in JUnit5, which approach is correct?
- [ ] A) `assertEquals(0.3, 0.1 + 0.2)`
- [ ] B) `assertEquals(0.3, 0.1 + 0.2, 0.0001)`
- [ ] C) `assertTrue(0.3 == 0.1 + 0.2)`
- [ ] D) `assertSame(0.3, 0.1 + 0.2)`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `assertEquals(0.3, 0.1 + 0.2, 0.0001)`

**Explanation:** Floating-point arithmetic can produce imprecise results (0.1 + 0.2 may not exactly equal 0.3). The delta parameter (0.0001) specifies an acceptable tolerance for the comparison.
- **Why others are wrong:**
  - A) Without a delta, this may fail due to floating-point precision issues.
  - C) Direct `==` comparison is unreliable for floating-point numbers.
  - D) `assertSame` checks object identity, not value equality.
</details>

---

### 6. What does `assertAll()` in JUnit5 do differently from sequential assertions?
- [ ] A) It runs faster than individual assertions
- [ ] B) It reports all failures instead of stopping at the first failure
- [ ] C) It automatically generates test data
- [ ] D) It validates object references instead of values

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) It reports all failures instead of stopping at the first failure

**Explanation:** `assertAll()` groups multiple assertions and runs all of them, reporting all failures at once. Without `assertAll`, a test stops at the first failed assertion, hiding subsequent failures.
- **Why others are wrong:**
  - A) Performance is not the purpose of `assertAll`.
  - C) It doesn't generate test data.
  - D) That's what `assertSame` does, not `assertAll`.
</details>

---

### 7. What method should you use to verify that a method throws an expected exception?
- [ ] A) `assertException()`
- [ ] B) `assertThrows()`
- [ ] C) `expectException()`
- [ ] D) `catchException()`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `assertThrows()`

**Explanation:** `assertThrows(ExceptionClass.class, () -> { code })` is JUnit5's method for verifying that code throws an expected exception. It returns the exception for further inspection if needed.
- **Why others are wrong:**
  - A, C, D) These are not valid JUnit5 assertion methods.
</details>

---

## Part 2: Code Prediction - JUnit5

### 8. What is the execution order of the following code?

```java
class LifecycleTest {
    @BeforeAll static void beforeAll() { System.out.print("1 "); }
    @BeforeEach void beforeEach() { System.out.print("2 "); }
    @Test void testA() { System.out.print("A "); }
    @Test void testB() { System.out.print("B "); }
    @AfterEach void afterEach() { System.out.print("3 "); }
    @AfterAll static void afterAll() { System.out.print("4"); }
}
```
- [ ] A) `1 2 A 3 2 B 3 4`
- [ ] B) `1 2 A B 3 4`
- [ ] C) `2 A 3 2 B 3 1 4`
- [ ] D) `1 A B 4`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** A) `1 2 A 3 2 B 3 4`

**Explanation:** `@BeforeAll` runs once before all tests (1), then for each test: `@BeforeEach` (2), test method (A or B), `@AfterEach` (3). Finally, `@AfterAll` runs once (4). Note: test order may vary, but the pattern is consistent.
</details>

---

### 9. What happens when this test runs?

```java
@Test
void testDivision() {
    assertThrows(ArithmeticException.class, () -> {
        int result = 10 / 0;
    });
}
```
- [ ] A) Test fails with ArithmeticException
- [ ] B) Test passes
- [ ] C) Test fails because ArithmeticException was not thrown
- [ ] D) Compilation error

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Test passes

**Explanation:** The code inside the lambda throws `ArithmeticException` (division by zero), and `assertThrows` expects this exception type. Since the expected exception is thrown, the test passes.
</details>

---

## Part 3: Multiple Choice - Mockito

### 10. Which annotation enables Mockito integration with JUnit5?
- [ ] A) `@RunWith(MockitoRunner.class)`
- [ ] B) `@ExtendWith(MockitoExtension.class)`
- [ ] C) `@MockitoSettings`
- [ ] D) `@EnableMockito`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `@ExtendWith(MockitoExtension.class)`

**Explanation:** JUnit5 uses `@ExtendWith` to add extensions. `MockitoExtension` from `mockito-junit-jupiter` enables `@Mock`, `@InjectMocks`, and other Mockito annotations.
- **Why others are wrong:**
  - A) `@RunWith` is JUnit4 syntax.
  - C, D) These are not valid Mockito annotations.
</details>

---

### 11. What does `@InjectMocks` do in a Mockito test?
- [ ] A) Creates a mock of the annotated class
- [ ] B) Automatically injects mocks into the annotated object
- [ ] C) Verifies that mocks were called
- [ ] D) Resets all mocks after each test

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Automatically injects mocks into the annotated object

**Explanation:** `@InjectMocks` creates an instance of the annotated class and injects the `@Mock` fields into it via constructor, setter, or field injection.
- **Why others are wrong:**
  - A) `@Mock` creates mocks, not `@InjectMocks`.
  - C) `verify()` method does this.
  - D) `@InjectMocks` doesn't handle resetting.
</details>

---

### 12. What is the difference between a Mock and a Spy in Mockito?
- [ ] A) Mocks are faster than spies
- [ ] B) Mocks are complete fakes; spies wrap real objects with selective overrides
- [ ] C) Spies can only be used with interfaces
- [ ] D) Mocks verify calls; spies don't

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Mocks are complete fakes; spies wrap real objects with selective overrides

**Explanation:** A **Mock** is a completely fake object where all methods return default values unless stubbed. A **Spy** wraps a real object, allowing real behavior while enabling selective method stubbing.
- **Why others are wrong:**
  - A) Performance is not the distinguishing factor.
  - C) Both can be used with classes and interfaces.
  - D) Both support verification.
</details>

---

### 13. How do you stub a void method to throw an exception in Mockito?
- [ ] A) `when(repository.delete(1L)).thenThrow(new RuntimeException())`
- [ ] B) `doThrow(new RuntimeException()).when(repository).delete(1L)`
- [ ] C) `repository.delete(1L).throws(new RuntimeException())`
- [ ] D) `stubThrow(repository.delete(1L), new RuntimeException())`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `doThrow(new RuntimeException()).when(repository).delete(1L)`

**Explanation:** Void methods cannot use `when().thenThrow()` syntax because there's no return value. Instead, use the `doThrow().when()` pattern for void methods.
- **Why others are wrong:**
  - A) `when().thenThrow()` doesn't work with void methods.
  - C, D) These are not valid Mockito syntax.
</details>

---

### 14. What does `verify(mock, times(2)).method()` check?
- [ ] A) That the method was called at least twice
- [ ] B) That the method was called exactly twice
- [ ] C) That the method was called at most twice
- [ ] D) That the method will be called twice in the future

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) That the method was called exactly twice

**Explanation:** `times(2)` is a verification mode that asserts the method was called exactly 2 times. Use `atLeast(2)` for "at least twice" and `atMost(2)` for "at most twice."
- **Why others are wrong:**
  - A) That would be `atLeast(2)`.
  - C) That would be `atMost(2)`.
  - D) Verification checks past calls, not future.
</details>

---

### 15. What is the purpose of `ArgumentCaptor` in Mockito?
- [ ] A) To create mocks with specific argument types
- [ ] B) To capture and inspect arguments passed to mock methods
- [ ] C) To match any argument type
- [ ] D) To count method invocations

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) To capture and inspect arguments passed to mock methods

**Explanation:** `ArgumentCaptor` captures the actual arguments passed to a mock method so you can inspect them with assertions. Example: `verify(repo).save(userCaptor.capture())` then `userCaptor.getValue()`.
- **Why others are wrong:**
  - A) `@Mock` creates mocks.
  - C) `any()` matches any argument.
  - D) `times()` counts invocations.
</details>

---

## Part 4: Multiple Choice - Pytest & Python unittest

### 16. What is the default assertion syntax in Pytest?
- [ ] A) `self.assertEqual(a, b)`
- [ ] B) `assert a == b`
- [ ] C) `expect(a).toBe(b)`
- [ ] D) `Assert.equals(a, b)`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `assert a == b`

**Explanation:** Pytest uses Python's built-in `assert` statement with detailed failure output. This is simpler than unittest's verbose `self.assertEqual()` syntax.
- **Why others are wrong:**
  - A) This is unittest syntax.
  - C) This is JavaScript/Jest syntax.
  - D) This is not valid Python syntax.
</details>

---

### 17. Which file name patterns does Pytest automatically discover for test files?
- [ ] A) `test_*.py` and `*_test.py`
- [ ] B) Only `test_*.py`
- [ ] C) `Test*.py` only
- [ ] D) Any `.py` file with test functions

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** A) `test_*.py` and `*_test.py`

**Explanation:** Pytest's test discovery looks for files matching `test_*.py` or `*_test.py` patterns by default. This can be customized in pytest.ini or pyproject.toml.
- **Why others are wrong:**
  - B) Both patterns are valid, not just `test_*.py`.
  - C) File names should be lowercase.
  - D) File names must match the patterns.
</details>

---

### 18. What is the equivalent of JUnit5's `@BeforeEach` in Python unittest?
- [ ] A) `@before_each`
- [ ] B) `setUpEach()`
- [ ] C) `setUp()`
- [ ] D) `before_test()`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) `setUp()`

**Explanation:** In Python's unittest module, `setUp()` runs before each test method (like JUnit5's `@BeforeEach`), and `setUpClass()` runs once before all tests (like `@BeforeAll`).
- **Why others are wrong:**
  - A, B, D) These are not valid unittest method names.
</details>

---

### 19. What is the purpose of `conftest.py` in Pytest?
- [ ] A) Configuration for test logging
- [ ] B) Shared fixtures available to all tests in the directory
- [ ] C) Test coverage settings
- [ ] D) Mock configuration

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Shared fixtures available to all tests in the directory

**Explanation:** `conftest.py` is a special Pytest file where you define fixtures that are automatically available to all tests in the same directory and subdirectories without explicit imports.
- **Why others are wrong:**
  - A, C, D) These are not the primary purpose of conftest.py.
</details>

---

### 20. What are the valid fixture scopes in Pytest?
- [ ] A) `test`, `class`, `file`, `all`
- [ ] B) `function`, `class`, `module`, `session`
- [ ] C) `each`, `group`, `suite`, `global`
- [ ] D) `unit`, `integration`, `system`, `acceptance`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `function`, `class`, `module`, `session`

**Explanation:** Pytest fixture scopes control when fixtures are created/destroyed: `function` (default, each test), `class` (per test class), `module` (per file), `session` (once per test run).
- **Why others are wrong:**
  - A, C, D) These are not valid Pytest fixture scope names.
</details>

---

## Part 5: True/False

### 21. In JUnit5, test classes and methods no longer need to be declared as `public`.
- [ ] True
- [ ] False

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** True

**Explanation:** JUnit5 relaxed visibility requirements. Test classes and methods can be package-private (no modifier) instead of public, reducing boilerplate code compared to JUnit4.
</details>

---

### 22. MagicMock in Python includes pre-configured magic methods like `__len__` and `__iter__`.
- [ ] True
- [ ] False

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** True

**Explanation:** MagicMock is a subclass of Mock that includes default implementations of magic methods. `len(magic_mock)` returns 0 and `iter(magic_mock)` works, while regular Mock would raise TypeError.
</details>

---

### 23. When patching in Python, you should patch where the object is defined, not where it's used.
- [ ] True
- [ ] False

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** The golden rule of patching is: **patch where the object is USED, not where it's DEFINED.** If `mymodule.py` imports `from external import api`, you patch `mymodule.api`, not `external.api`.
</details>

---

### 24. `@AfterEach` in JUnit5 runs even if the test method throws an exception.
- [ ] True
- [ ] False

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** True

**Explanation:** `@AfterEach` is guaranteed to run after each test method, regardless of whether the test passed, failed, or threw an exception. This is critical for resource cleanup.
</details>

---

### 25. In unittest, `assertIs(a, b)` and `assertEqual(a, b)` test the same thing.
- [ ] True
- [ ] False

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** `assertEqual(a, b)` tests value equality using `==`, while `assertIs(a, b)` tests object identity (same object reference) using `is`. Two equal strings may not be the same object.
</details>

---

## Part 6: Fill-in-the-Blank

### 26. In JUnit5, the annotation `@_____` is used to skip a test.
- [ ] A) `@Skip`
- [ ] B) `@Ignore`
- [ ] C) `@Disabled`
- [ ] D) `@Exclude`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) `@Disabled`

**Explanation:** JUnit5 uses `@Disabled` to skip tests. JUnit4 used `@Ignore`, but this was renamed in JUnit5 to `@Disabled` for clearer semantics.
</details>

---

### 27. In Mockito, the syntax `when(mock.method()).thenReturn(value)` is called _____.
- [ ] A) Mocking
- [ ] B) Stubbing
- [ ] C) Verifying
- [ ] D) Spying

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Stubbing

**Explanation:** **Stubbing** defines what a mock returns when its methods are called. **Verification** checks that methods were called. Stubbing controls behavior; verification confirms interaction.
</details>

---

### 28. In Pytest, the command to run tests and generate Allure results is `pytest --alluredir=_____`.
- [ ] A) `results`
- [ ] B) `output`
- [ ] C) `allure-results`
- [ ] D) `report`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) `allure-results`

**Explanation:** The standard convention is `pytest --alluredir=allure-results`. This directory stores JSON result files that Allure uses to generate HTML reports with `allure serve allure-results`.
</details>

---

## Part 7: Multiple Choice - Python Mocking

### 29. What is the correct way to patch `os.path.exists` to return `True` in a test?
- [ ] A) `@patch('os.path.exists', True)`
- [ ] B) `@patch('os.path.exists', return_value=True)`
- [ ] C) `@patch.return('os.path.exists', True)`
- [ ] D) `@mock('os.path.exists', True)`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `@patch('os.path.exists', return_value=True)`

**Explanation:** The `@patch` decorator accepts a `return_value` keyword argument to specify what the patched function should return.
- **Why others are wrong:**
  - A) Just passing `True` doesn't set the return value.
  - C, D) These are not valid patch syntax.
</details>

---

### 30. What does `mocker.spy()` do in pytest-mock?
- [ ] A) Creates a completely fake object
- [ ] B) Wraps a real function to track calls while preserving behavior
- [ ] C) Automatically generates test data
- [ ] D) Prevents a function from being called

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Wraps a real function to track calls while preserving behavior

**Explanation:** A spy wraps a real function, allowing it to execute normally while recording all calls for later verification. Unlike a mock, a spy doesn't replace the function's behavior by default.
- **Why others are wrong:**
  - A) That's what `mocker.Mock()` does.
  - C, D) Spies don't do this.
</details>

---

### 31. How do you configure a mock to raise an exception on the first call and return a value on the second call?
- [ ] A) `mock.side_effect = [Exception("Error"), "success"]`
- [ ] B) `mock.return_value = [Exception("Error"), "success"]`
- [ ] C) `mock.throws = Exception("Error"); mock.returns = "success"`
- [ ] D) `mock.first_call.throw(); mock.second_call.return("success")`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** A) `mock.side_effect = [Exception("Error"), "success"]`

**Explanation:** `side_effect` accepts a list of values/exceptions to return on consecutive calls. The first call raises the exception, the second returns "success".
- **Why others are wrong:**
  - B) `return_value` doesn't handle exceptions or consecutive calls this way.
  - C, D) These are not valid Mock syntax.
</details>

---

### 32. What advantage does pytest-mock's `mocker` fixture have over manual `@patch` decorators?
- [ ] A) It's faster
- [ ] B) Automatic cleanup after each test without context managers
- [ ] C) It supports more mock types
- [ ] D) It works with async functions only

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Automatic cleanup after each test without context managers

**Explanation:** The `mocker` fixture automatically cleans up all patches after each test, eliminating the need for `with patch()` context managers or decorator stacking. This makes tests cleaner and more readable.
- **Why others are wrong:**
  - A) Performance is similar.
  - C) Both support the same mock types.
  - D) Both work with sync and async functions.
</details>

---

## Part 8: Multiple Choice - Allure Reporting

### 33. Which Allure annotation in JUnit5 documents what a test does?
- [ ] A) `@Title`
- [ ] B) `@Description`
- [ ] C) `@Documentation`
- [ ] D) `@Summary`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `@Description`

**Explanation:** `@Description("...")` adds a detailed description to a test that appears in the Allure report, explaining what the test verifies.
- **Why others are wrong:**
  - A, C, D) These are not valid Allure annotations.
</details>

---

### 34. In Allure Pytest, how do you create a step in a test?
- [ ] A) `@allure.step("Step name")`
- [ ] B) `with allure.step("Step name"):`
- [ ] C) Both A and B are correct
- [ ] D) `allure.add_step("Step name")`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Both A and B are correct

**Explanation:** In Pytest, you can create steps using either the `@allure.step` decorator on functions or the `with allure.step()` context manager for inline steps within a test.
</details>

---

### 35. What command generates and serves an Allure report locally?
- [ ] A) `allure report`
- [ ] B) `allure serve allure-results`
- [ ] C) `allure open allure-results`
- [ ] D) `allure start --dir allure-results`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `allure serve allure-results`

**Explanation:** `allure serve <results-directory>` generates an HTML report from the results and starts a local web server to display it in your browser.
- **Why others are wrong:**
  - A, C, D) These are not valid Allure CLI commands.
</details>

---

## Part 9: Code Prediction - Python

### 36. What is the output of this Pytest test?

```python
import pytest

@pytest.fixture
def numbers():
    return [1, 2, 3]

def test_sum(numbers):
    assert sum(numbers) == 6
```
- [ ] A) Test passes
- [ ] B) Test fails - numbers is undefined
- [ ] C) Test fails - fixture not injected
- [ ] D) SyntaxError

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** A) Test passes

**Explanation:** Pytest automatically injects fixtures by matching parameter names. The `numbers` fixture is injected into `test_sum`, providing `[1, 2, 3]`, and `sum([1, 2, 3]) == 6` is True.
</details>

---

### 37. What does this Mock assertion verify?

```python
from unittest.mock import Mock

mock = Mock()
mock.process("data", timeout=30)
mock.process.assert_called_once_with("data", timeout=30)
```
- [ ] A) That `process` was called with any arguments
- [ ] B) That `process` was called exactly once with `"data"` and `timeout=30`
- [ ] C) That `process` was called at least once
- [ ] D) That `process` returned `("data", 30)`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) That `process` was called exactly once with `"data"` and `timeout=30`

**Explanation:** `assert_called_once_with(...)` verifies that the method was called exactly one time with the exact arguments specified. It would fail if called more than once or with different arguments.
</details>

---

## Part 10: Scenario-Based Questions

### 38. You need to test a `PaymentService` that depends on an external `PaymentGateway`. The gateway makes real HTTP calls. What's the best approach?
- [ ] A) Use the real PaymentGateway in tests
- [ ] B) Mock the PaymentGateway to control its behavior
- [ ] C) Skip the tests that involve payments
- [ ] D) Test only in production

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Mock the PaymentGateway to control its behavior

**Explanation:** Mocking the external dependency allows isolated unit testing without real HTTP calls. You can simulate success, failure, and edge cases predictably without affecting real systems.
- **Why others are wrong:**
  - A) Real calls make tests slow, flaky, and may cause real charges.
  - C, D) These approaches leave code untested.
</details>

---

### 39. A test fails on your machine but passes on a colleague's machine. This violates which FIRST principle?
- [ ] A) Fast
- [ ] B) Isolated
- [ ] C) Repeatable
- [ ] D) Self-validating

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Repeatable

**Explanation:** **Repeatable** means tests produce the same result every time, regardless of environment. Environment-dependent tests (different OS, time zones, file paths) violate repeatability.
- **Why others are wrong:**
  - A) Fast relates to execution time.
  - B) Isolated means tests don't depend on each other.
  - D) Self-validating means automatic pass/fail.
</details>

---

### 40. When using boundary value analysis to test a function that accepts ages 0-120, which values should you test?
- [ ] A) 0, 50, 120
- [ ] B) -1, 0, 1, 119, 120, 121
- [ ] C) 0, 60, 120
- [ ] D) Any 5 random values between 0 and 120

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) -1, 0, 1, 119, 120, 121

**Explanation:** Boundary Value Analysis tests at the boundaries (MIN, MAX) and just outside them (MIN-1, MAX+1) plus values just inside (MIN+1, MAX-1). This catches off-by-one errors and boundary violations.
- **Why others are wrong:**
  - A, C) Missing boundary-adjacent values (-1, 1, 119, 121).
  - D) Random values don't systematically test boundaries.
</details>

---

## Part 11: Advanced Questions

### 41. What's the difference between `@BeforeAll` and `@BeforeEach` in terms of method requirements?
- [ ] A) `@BeforeAll` must be public, `@BeforeEach` can be private
- [ ] B) `@BeforeAll` must be static, `@BeforeEach` cannot be static
- [ ] C) Both must be static
- [ ] D) Neither has specific requirements

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) `@BeforeAll` must be static, `@BeforeEach` cannot be static

**Explanation:** `@BeforeAll` runs once before any test instance is created, so it must be static. `@BeforeEach` runs on each test instance, so it's an instance method. (Exception: with `@TestInstance(PER_CLASS)`, `@BeforeAll` can be non-static.)
- **Why others are wrong:**
  - A, C, D) These don't correctly describe the static requirements.
</details>

---

### 42. In Mockito, what happens if you call an unstubbed method on a mock?
- [ ] A) It throws an exception
- [ ] B) It returns a default value (null, 0, false, empty collection)
- [ ] C) It returns the last stubbed value
- [ ] D) The test fails automatically

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) It returns a default value (null, 0, false, empty collection)

**Explanation:** Mockito mocks return sensible defaults for unstubbed methods: `null` for objects, `0` for numbers, `false` for booleans, and empty collections for collection types.
- **Why others are wrong:**
  - A) No exception unless configured with strict stubs.
  - C, D) These don't describe Mockito's default behavior.
</details>

---

### 43. What's the purpose of auto-speccing in Python's unittest.mock?
- [ ] A) Automatically generate test cases
- [ ] B) Create mocks that validate method signatures and attributes exist
- [ ] C) Speed up mock creation
- [ ] D) Generate API documentation

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Create mocks that validate method signatures and attributes exist

**Explanation:** `create_autospec(SomeClass)` creates a mock that mirrors the real class's interface. Calling nonexistent methods or passing wrong arguments raises errors, catching API misuse early.
- **Why others are wrong:**
  - A, C, D) Auto-spec doesn't do these things.
</details>

---

### 44. Which JUnit5 annotation allows tests to run only on specific operating systems?
- [ ] A) `@Platform(OS.WINDOWS)`
- [ ] B) `@RunOnOs(OS.WINDOWS)`
- [ ] C) `@EnabledOnOs(OS.WINDOWS)`
- [ ] D) `@OsFilter(OS.WINDOWS)`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) `@EnabledOnOs(OS.WINDOWS)`

**Explanation:** JUnit5 provides `@EnabledOnOs` and `@DisabledOnOs` annotations for platform-specific test execution. Values include `OS.WINDOWS`, `OS.LINUX`, `OS.MAC`.
- **Why others are wrong:**
  - A, B, D) These are not valid JUnit5 annotations.
</details>

---

### 45. In Allure, what's the hierarchy for organizing tests from broadest to most specific?
- [ ] A) Feature → Epic → Story
- [ ] B) Story → Feature → Epic
- [ ] C) Epic → Feature → Story
- [ ] D) Test → Suite → Project

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Epic → Feature → Story

**Explanation:** Allure uses agile terminology: **Epic** (largest scope, e.g., "User Management"), **Feature** (major functionality, e.g., "Authentication"), **Story** (specific behavior, e.g., "Login").
- **Why others are wrong:**
  - A, B) The order is reversed or scrambled.
  - D) These are not Allure's organizational hierarchy.
</details>

---

## Scoring Guide

| Score | Rating |
|-------|--------|
| 40-45 | ⭐⭐⭐⭐⭐ Expert - Outstanding understanding of unit testing! |
| 35-39 | ⭐⭐⭐⭐ Advanced - Strong grasp of concepts |
| 28-34 | ⭐⭐⭐ Proficient - Good foundation, review weak areas |
| 20-27 | ⭐⭐ Developing - Review written content and practice more |
| Below 20 | ⭐ Needs Review - Revisit the week's materials thoroughly |

---

## Topics to Review by Question

- **JUnit5 Basics**: Questions 1-3, 8, 21, 26, 41
- **FIRST Principles & Unit Testing**: Questions 4, 39
- **Assertions**: Questions 5, 6, 7, 9
- **Mockito**: Questions 10-15, 42
- **Pytest**: Questions 16, 17, 36
- **Python unittest**: Questions 18, 25
- **Fixtures**: Questions 19, 20
- **Python Mocking**: Questions 22, 23, 29-32, 37, 43
- **Allure**: Questions 33-35, 45
- **Testing Strategies**: Questions 38, 40
- **Conditional Execution**: Questions 44


