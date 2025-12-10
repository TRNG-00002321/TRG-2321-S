# Lab: Your First JUnit5 Tests

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Beginner |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | JUnit5 written content, demo_junit5_basics.java |

## Learning Objectives
By completing this exercise, you will:
- Write your first JUnit5 test class from scratch
- Apply the AAA pattern (Arrange, Act, Assert)
- Use basic assertions: `assertEquals`, `assertTrue`, `assertFalse`
- Follow test naming conventions
- Run tests in your IDE

## The Scenario

You've joined a team that has a `Calculator` class but NO tests! Your tech lead has asked you to write comprehensive unit tests to ensure the Calculator works correctly before it's deployed to production.

## Core Tasks

### Task 1: Set Up Your Test Class (10 minutes)

1. Navigate to `starter_code/src/test/java/`
2. Create a new test class called `CalculatorTest.java`
3. Add the necessary JUnit5 imports:
   ```java
   import org.junit.jupiter.api.Test;
   import org.junit.jupiter.api.DisplayName;
   import static org.junit.jupiter.api.Assertions.*;
   ```

### Task 2: Write Tests for Addition (15 minutes)

Write at least **4 test methods** for the `add()` method covering:

| Test Case | Input A | Input B | Expected |
|-----------|---------|---------|----------|
| Two positive numbers | 5 | 3 | 8 |
| Positive and negative | 10 | -3 | 7 |
| Two negative numbers | -5 | -3 | -8 |
| Adding zero | 42 | 0 | 42 |

**Requirements:**
- Follow naming convention: `methodName_scenario_expectedBehavior`
- Use `@DisplayName` for human-readable descriptions
- Include assertion messages

### Task 3: Write Tests for Subtraction (10 minutes)

Write at least **3 test methods** for the `subtract()` method covering:
- Basic subtraction
- Subtracting a larger number (negative result)
- Subtracting zero

### Task 4: Write Tests for Boolean Methods (15 minutes)

Write tests for both `isEven()` and `isPositive()`:

For `isEven()`:
- Test with even positive numbers (2, 4, 100)
- Test with odd numbers (1, 3, 99)
- Test with zero (is 0 even?)
- Test with negative numbers

For `isPositive()`:
- Test with positive numbers
- Test with negative numbers
- Test with zero (is 0 positive?)

### Task 5: Run Your Tests (5 minutes)

1. Run all tests in `CalculatorTest.java`
2. Verify all tests pass (green checkmarks)
3. Intentionally break one test to see what failure looks like
4. Fix the test

## Starter Code

The `Calculator` class is provided in `starter_code/src/main/java/Calculator.java`:

```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    public int subtract(int a, int b) {
        return a - b;
    }
    
    public int multiply(int a, int b) {
        return a * b;
    }
    
    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return a / b;
    }
    
    public boolean isEven(int number) {
        return number % 2 == 0;
    }
    
    public boolean isPositive(int number) {
        return number > 0;
    }
}
```

## Definition of Done

Your submission is complete when:
- [ ] `CalculatorTest.java` exists with proper JUnit5 imports
- [ ] At least 4 tests for `add()` method
- [ ] At least 3 tests for `subtract()` method
- [ ] At least 4 tests for `isEven()` method
- [ ] At least 3 tests for `isPositive()` method
- [ ] All tests have `@DisplayName` annotations
- [ ] All tests follow the AAA pattern
- [ ] All tests pass when run

## Stretch Goals (Optional)

If you finish early:
1. Add tests for the `multiply()` method
2. Group related tests using `@Nested` classes
3. Try running tests from the command line with Maven: `mvn test`

## Hints

<details>
<summary>Hint 1: Test Class Structure</summary>

```java
class CalculatorTest {
    
    private Calculator calculator = new Calculator();
    
    @Test
    @DisplayName("Description here")
    void methodName_scenario_expected() {
        // Arrange
        
        // Act
        
        // Assert
    }
}
```
</details>

<details>
<summary>Hint 2: Boolean Assertions</summary>

```java
// For testing isEven:
assertTrue(calculator.isEven(2), "2 should be even");
assertFalse(calculator.isEven(3), "3 should be odd");
```
</details>

<details>
<summary>Hint 3: Is Zero Even?</summary>

Yes! Zero is mathematically considered even because it's divisible by 2 with no remainder (0 / 2 = 0).
</details>

## Common Mistakes to Avoid

1. **Forgetting `@Test` annotation** - Your method won't run as a test
2. **Using wrong import** - Use `org.junit.jupiter.api.Test`, NOT `org.junit.Test` (JUnit4)
3. **No assertions** - A test without assertions always passes!
4. **Testing multiple behaviors in one test** - Keep tests focused

## Submission

Commit your `CalculatorTest.java` file with the message:
```
feat(week6): Complete first JUnit5 tests exercise
```

