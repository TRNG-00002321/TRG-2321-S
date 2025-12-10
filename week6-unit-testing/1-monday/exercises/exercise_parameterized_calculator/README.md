# Lab: Parameterized Tests - Data-Driven Calculator Testing

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | parameterizing-tests.md, demo_parameterized_tests.java |

## Learning Objectives
By completing this exercise, you will:
- Convert repetitive tests to parameterized tests
- Use `@ValueSource` for simple values
- Use `@CsvSource` for multiple parameters
- Use `@MethodSource` for complex test data
- Use `@EnumSource` for enum testing
- Create custom display names

## The Scenario

Your team has written hundreds of repetitive test methods for the Calculator. Code review feedback says: "This violates DRY principle - use parameterized tests!" Your task is to refactor the tests to use JUnit5's parameterization features.

## Core Tasks

### Task 1: @ValueSource - Simple Values (10 minutes)

Convert these repetitive tests:
```java
// BEFORE: Repetitive!
@Test void isEven_2_returnsTrue() { assertTrue(calc.isEven(2)); }
@Test void isEven_4_returnsTrue() { assertTrue(calc.isEven(4)); }
@Test void isEven_100_returnsTrue() { assertTrue(calc.isEven(100)); }
```

To parameterized:
```java
// AFTER: Clean!
@ParameterizedTest
@ValueSource(ints = {2, 4, 6, 100, 0, -2})
void isEven_evenNumbers_returnsTrue(int number) {
    assertTrue(calculator.isEven(number));
}
```

**Your Task:** Create parameterized tests for:
- Even numbers (should return true)
- Odd numbers (should return false)
- Positive numbers (for `isPositive()`)

### Task 2: @CsvSource - Multiple Parameters (15 minutes)

Use `@CsvSource` to test addition with multiple inputs and expected outputs:

```java
@ParameterizedTest
@CsvSource({
    "1, 2, 3",
    "0, 0, 0",
    "-1, 1, 0",
    "100, 200, 300"
})
void add_variousInputs_returnsCorrectSum(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}
```

**Your Task:** Create `@CsvSource` parameterized tests for:
- Addition (at least 6 test cases)
- Subtraction (at least 4 test cases)
- Multiplication (at least 4 test cases)
- Division (at least 4 test cases)

### Task 3: @MethodSource - Complex Data (15 minutes)

For complex test scenarios, use `@MethodSource`:

```java
@ParameterizedTest
@MethodSource("provideDivisionTestCases")
void divide_variousCases_returnsCorrectQuotient(int a, int b, int expected) {
    assertEquals(expected, calculator.divide(a, b));
}

static Stream<Arguments> provideDivisionTestCases() {
    return Stream.of(
        Arguments.of(10, 2, 5),
        Arguments.of(9, 3, 3),
        Arguments.of(-10, 2, -5),
        Arguments.of(7, 2, 3)  // Integer division
    );
}
```

**Your Task:** Create a `@MethodSource` test for `power()` method that includes:
- Normal cases (2^3 = 8)
- Edge cases (anything^0 = 1)
- Zero cases (0^n = 0)

### Task 4: Custom Display Names (10 minutes)

Make test output readable:
```java
@ParameterizedTest(name = "{0} + {1} = {2}")
@CsvSource({"1, 2, 3", "4, 5, 9"})
void add_customDisplayName(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}
// Output: "1 + 2 = 3", "4 + 5 = 9"
```

**Your Task:** Add custom display names to all your parameterized tests.

### Task 5: @NullAndEmptySource (Optional) (10 minutes)

For string testing, combine sources:
```java
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {"  ", "\t", "\n"})
void isBlank_blankInputs_returnsTrue(String input) {
    assertTrue(StringUtils.isBlank(input));
}
```

## Required Dependencies

Add to your `pom.xml`:
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-params</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

## Definition of Done

- [ ] At least 2 tests using `@ValueSource`
- [ ] At least 4 tests using `@CsvSource` 
- [ ] At least 1 test using `@MethodSource`
- [ ] All parameterized tests have custom display names
- [ ] Total of at least 30 test cases across all parameterized tests
- [ ] Tests cover edge cases (zero, negative, boundary values)
- [ ] All tests pass

## Common Display Name Placeholders

| Placeholder | Description |
|-------------|-------------|
| `{index}` | Invocation index (1, 2, 3...) |
| `{arguments}` | All arguments as string |
| `{0}`, `{1}` | Individual arguments |
| `{displayName}` | The test's @DisplayName |

## Hints

<details>
<summary>Hint: MethodSource with Arguments</summary>

```java
import org.junit.jupiter.params.provider.Arguments;
import java.util.stream.Stream;

static Stream<Arguments> provideTestCases() {
    return Stream.of(
        Arguments.of(input1, input2, expected),
        Arguments.of(input1, input2, expected)
    );
}
```
</details>

<details>
<summary>Hint: CsvSource with Strings</summary>

```java
@CsvSource({
    "'hello', 5",       // Use quotes for strings
    "'', 0",            // Empty string
    "'hello world', 11" // String with space
})
void stringLength(String input, int expectedLength) {
    assertEquals(expectedLength, input.length());
}
```
</details>

## Submission

Commit with message:
```
feat(week6): Complete parameterized tests exercise
```

