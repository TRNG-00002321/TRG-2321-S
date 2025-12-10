# Validating Functions: Assertion Methods in JUnit5

## Learning Objectives
- Master all assertion methods available in JUnit5
- Use `assertEquals`, `assertTrue`, `assertNull`, and other common assertions
- Group assertions with `assertAll` for comprehensive validation
- Write meaningful assertion messages that aid debugging

## Why This Matters

Assertions are the vocabulary of testing. Just as a detective needs the right tools to solve a case, you need the right assertions to verify your code's behavior. JUnit5 provides a rich set of assertion methods that go far beyond simple equality checks—mastering them allows you to write expressive tests that clearly communicate intent and provide helpful failure messages.

A well-chosen assertion can be the difference between "Test failed" and "Expected user age to be 25 but was 24"—the latter tells you exactly what went wrong.

## The Concept

### The Anatomy of an Assertion

Every assertion in JUnit5 follows this pattern:

```java
assertXxx(expected, actual, optionalMessage)
```

- **expected**: What the value should be
- **actual**: What the value actually is
- **optionalMessage**: Description shown on failure (can be a String or Supplier<String>)

### Core Assertion Methods

JUnit5's assertions are in `org.junit.jupiter.api.Assertions`:

```java
import static org.junit.jupiter.api.Assertions.*;
```

### 1. assertEquals / assertNotEquals

Verifies that two values are equal (or not equal):

```java
@Test
void assertEquals_examples() {
    // Primitive comparison
    assertEquals(4, 2 + 2);
    
    // Object comparison (uses .equals())
    assertEquals("hello", "hello");
    
    // With message
    assertEquals(100, calculateTotal(), "Total should be 100");
    
    // Floating-point comparison with delta
    assertEquals(3.14159, Math.PI, 0.001, "PI should be approximately 3.14159");
    
    // Not equals
    assertNotEquals("hello", "world");
}
```

**Important**: For floating-point numbers, always use the delta (tolerance) parameter:
```java
// BAD: Floating point comparison without delta
assertEquals(0.3, 0.1 + 0.2);  // May fail due to precision!

// GOOD: With delta
assertEquals(0.3, 0.1 + 0.2, 0.0001);  // Passes
```

### 2. assertTrue / assertFalse

Verifies boolean conditions:

```java
@Test
void assertTrue_examples() {
    // Simple boolean check
    assertTrue(5 > 3);
    
    // With message
    assertTrue(user.isActive(), "User should be active after registration");
    
    // Method returning boolean
    assertTrue(list.isEmpty(), "List should be empty after clearing");
    
    // assertFalse for negative conditions
    assertFalse(account.isLocked(), "Account should not be locked");
}
```

**Pro tip**: Prefer specific assertions over assertTrue when possible:
```java
// Less informative failure message
assertTrue(result == 5);  // "expected: <true> but was: <false>"

// More informative failure message
assertEquals(5, result);  // "expected: <5> but was: <7>"
```

### 3. assertNull / assertNotNull

Verifies null or non-null values:

```java
@Test
void assertNull_examples() {
    // Verify null
    assertNull(repository.findById(-1), "Non-existent ID should return null");
    
    // Verify not null
    assertNotNull(factory.create(), "Factory should never return null");
    
    // After deletion
    service.delete(userId);
    assertNull(service.findById(userId), "Deleted user should not be found");
}
```

### 4. assertSame / assertNotSame

Verifies object identity (same reference):

```java
@Test
void assertSame_examples() {
    // Same reference
    String str1 = "hello";
    String str2 = str1;
    assertSame(str1, str2, "Should be the same object reference");
    
    // Different references (even if equal)
    String str3 = new String("hello");
    String str4 = new String("hello");
    assertEquals(str3, str4);      // Passes - same content
    assertNotSame(str3, str4);     // Passes - different objects
}
```

**Use case**: Testing singleton patterns or caching:
```java
@Test
void singleton_returnsSameInstance() {
    Database db1 = Database.getInstance();
    Database db2 = Database.getInstance();
    assertSame(db1, db2, "Singleton should return same instance");
}
```

### 5. assertArrayEquals

Verifies array equality (element by element):

```java
@Test
void assertArrayEquals_examples() {
    // Integer arrays
    int[] expected = {1, 2, 3};
    int[] actual = {1, 2, 3};
    assertArrayEquals(expected, actual);
    
    // String arrays
    String[] names = {"Alice", "Bob"};
    assertArrayEquals(new String[]{"Alice", "Bob"}, names);
    
    // Floating-point arrays with delta
    double[] expected = {1.0, 2.0, 3.0};
    double[] actual = {1.001, 1.999, 3.002};
    assertArrayEquals(expected, actual, 0.01);  // Each element within 0.01
}
```

### 6. assertIterableEquals

Verifies equality of iterables (Lists, Sets, etc.):

```java
@Test
void assertIterableEquals_examples() {
    List<String> expected = Arrays.asList("a", "b", "c");
    List<String> actual = new ArrayList<>();
    actual.add("a");
    actual.add("b");
    actual.add("c");
    
    assertIterableEquals(expected, actual);
}
```

### 7. assertAll - Grouped Assertions

**This is one of JUnit5's most powerful features!**

`assertAll` runs all assertions and reports all failures, rather than stopping at the first failure:

```java
@Test
void assertAll_examples() {
    User user = userService.createUser("John", "Doe", 30);
    
    // Without assertAll - stops at first failure
    // assertEquals("John", user.getFirstName());
    // assertEquals("Doe", user.getLastName());    // Never runs if above fails
    // assertEquals(30, user.getAge());            // Never runs if above fails
    
    // With assertAll - runs all, reports all failures
    assertAll("User properties",
        () -> assertEquals("John", user.getFirstName()),
        () -> assertEquals("Doe", user.getLastName()),
        () -> assertEquals(30, user.getAge()),
        () -> assertNotNull(user.getId()),
        () -> assertTrue(user.isActive())
    );
}
```

**Output when multiple assertions fail:**
```
org.opentest4j.MultipleFailuresError: User properties (2 failures)
    expected: <Doe> but was: <Smith>
    expected: <30> but was: <25>
```

**Use cases for assertAll:**
- Validating object state with multiple properties
- Checking multiple return values
- Verifying side effects

### 8. assertThrows / assertDoesNotThrow

Verifies exception behavior:

```java
@Test
void assertThrows_examples() {
    // Verify exception is thrown
    assertThrows(IllegalArgumentException.class, () -> {
        new User(null, "Doe");  // null first name
    });
    
    // Capture and verify exception details
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> calculator.divide(10, 0)
    );
    assertEquals("Division by zero", exception.getMessage());
    
    // Verify NO exception is thrown
    assertDoesNotThrow(() -> {
        calculator.add(1, 2);
    });
}
```

### 9. assertTimeout / assertTimeoutPreemptively

Verifies operations complete within time limit:

```java
@Test
void assertTimeout_examples() {
    // Waits for operation to complete, fails if over time
    assertTimeout(Duration.ofSeconds(2), () -> {
        // Operation that should complete in under 2 seconds
        Thread.sleep(100);
        return "result";
    });
    
    // Aborts immediately if time exceeded (preemptively)
    assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
        // Will be interrupted if it takes too long
        return quickOperation();
    });
}
```

**Note**: `assertTimeoutPreemptively` runs in a different thread and will interrupt execution, while `assertTimeout` waits for completion then fails.

### Custom Assertion Messages

Always write helpful messages that explain **what** should be true, not just **that** something failed:

```java
// BAD: Redundant message
assertEquals(5, count, "assertEquals failed");

// BAD: States the obvious
assertEquals(5, count, "count should equal 5");

// GOOD: Explains the business meaning
assertEquals(5, count, "Shopping cart should contain 5 items after adding");

// GOOD: Includes context
assertEquals(expected, actual, 
    String.format("User %s should have role %s", userId, expectedRole));

// GOOD: Lazy message (computed only on failure)
assertEquals(expected, actual, 
    () -> "Computed value for input " + input + " was incorrect");
```

## Code Example

### Complete Assertion Demonstration

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssertionShowcaseTest {

    @Test
    @DisplayName("Demonstrate equality assertions")
    void equalityAssertions() {
        // Primitives
        assertEquals(42, calculateAnswer());
        assertNotEquals(0, calculateAnswer());
        
        // Objects
        String expected = "Hello, World!";
        assertEquals(expected, greet("World"));
        
        // Arrays
        int[] expectedArray = {1, 2, 3};
        assertArrayEquals(expectedArray, getNumbers());
        
        // Collections
        List<String> expectedList = Arrays.asList("a", "b", "c");
        assertIterableEquals(expectedList, getLetters());
    }
    
    @Test
    @DisplayName("Demonstrate boolean assertions")
    void booleanAssertions() {
        assertTrue(isPositive(5), "5 should be positive");
        assertFalse(isPositive(-3), "-3 should not be positive");
    }
    
    @Test
    @DisplayName("Demonstrate null assertions")
    void nullAssertions() {
        assertNull(findUser("nonexistent"));
        assertNotNull(findUser("admin"));
    }
    
    @Test
    @DisplayName("Demonstrate reference assertions")
    void referenceAssertions() {
        Object singleton1 = Singleton.getInstance();
        Object singleton2 = Singleton.getInstance();
        
        assertSame(singleton1, singleton2, "Singleton instances should be identical");
    }
    
    @Test
    @DisplayName("Demonstrate grouped assertions with assertAll")
    void groupedAssertions() {
        Person person = new Person("Jane", "Doe", 28);
        
        assertAll("Person validation",
            () -> assertEquals("Jane", person.getFirstName()),
            () -> assertEquals("Doe", person.getLastName()),
            () -> assertEquals(28, person.getAge()),
            () -> assertTrue(person.getAge() >= 0, "Age must be non-negative"),
            () -> assertNotNull(person.getFullName())
        );
    }
    
    @Test
    @DisplayName("Demonstrate exception assertions")
    void exceptionAssertions() {
        // Verify exception is thrown
        Exception exception = assertThrows(
            ArithmeticException.class,
            () -> divide(10, 0)
        );
        
        // Verify exception message
        assertTrue(exception.getMessage().contains("zero"));
        
        // Verify no exception for valid input
        assertDoesNotThrow(() -> divide(10, 2));
    }
    
    @Test
    @DisplayName("Demonstrate timeout assertions")
    void timeoutAssertions() {
        // Should complete within 1 second
        String result = assertTimeout(Duration.ofSeconds(1), () -> {
            // Simulate quick operation
            return "completed";
        });
        assertEquals("completed", result);
    }
    
    @Test
    @DisplayName("Demonstrate floating-point assertions")
    void floatingPointAssertions() {
        double calculated = 0.1 + 0.2;
        
        // Without delta - might fail due to floating point precision
        // assertEquals(0.3, calculated);  // Risky!
        
        // With delta - accounts for floating point imprecision
        assertEquals(0.3, calculated, 0.0001, "Sum should be approximately 0.3");
    }
    
    // Helper methods for demonstration
    private int calculateAnswer() { return 42; }
    private String greet(String name) { return "Hello, " + name + "!"; }
    private int[] getNumbers() { return new int[]{1, 2, 3}; }
    private List<String> getLetters() { return Arrays.asList("a", "b", "c"); }
    private boolean isPositive(int n) { return n > 0; }
    private Object findUser(String name) { return "admin".equals(name) ? new Object() : null; }
    private int divide(int a, int b) { return a / b; }
}
```

### Assertion Selection Guide

```
┌─────────────────────────────────────────────────────────────────┐
│                 WHICH ASSERTION SHOULD I USE?                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Comparing values?                                              │
│  ├── Primitives/Objects ──► assertEquals / assertNotEquals     │
│  ├── Floating point ────────► assertEquals with delta          │
│  ├── Arrays ───────────────► assertArrayEquals                 │
│  └── Collections ──────────► assertIterableEquals              │
│                                                                 │
│  Checking boolean condition? ──► assertTrue / assertFalse      │
│                                                                 │
│  Checking for null? ───────────► assertNull / assertNotNull    │
│                                                                 │
│  Checking same object? ────────► assertSame / assertNotSame    │
│                                                                 │
│  Testing exceptions? ──────────► assertThrows / assertDoesNotThrow │
│                                                                 │
│  Testing performance? ─────────► assertTimeout                 │
│                                                                 │
│  Multiple related checks? ─────► assertAll                     │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Summary

- **assertEquals**: Compare values with `.equals()`; use delta for floating-point
- **assertTrue/assertFalse**: Verify boolean conditions
- **assertNull/assertNotNull**: Check for null or non-null values
- **assertSame/assertNotSame**: Compare object references (identity)
- **assertArrayEquals/assertIterableEquals**: Compare arrays and collections
- **assertAll**: Group multiple assertions to see all failures at once
- **assertThrows/assertDoesNotThrow**: Verify exception behavior
- **assertTimeout**: Ensure operations complete within time limits
- Always include **meaningful messages** that explain the business context

## Additional Resources

- [JUnit5 Assertions JavaDoc](https://junit.org/junit5/docs/current/api/org.junit.jupiter.api/org/junit/jupiter/api/Assertions.html) - Complete API reference
- [AssertJ - Fluent Assertions](https://assertj.github.io/doc/) - Alternative assertion library with readable syntax
- [Hamcrest Matchers](http://hamcrest.org/JavaHamcrest/) - Matcher framework for more expressive assertions

