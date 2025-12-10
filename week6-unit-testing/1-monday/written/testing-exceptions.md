# Testing Exceptions: Validating Error Handling in JUnit5

## Learning Objectives
- Use `assertThrows` to verify that methods throw expected exceptions
- Validate exception messages and types
- Understand `assertDoesNotThrow` and when to use it
- Apply exception testing patterns for robust error handling verification

## Why This Matters

In production systems, how code handles errors is just as important as how it handles success cases. When a user enters invalid data, when a network call fails, or when a resource is unavailable, your code must respond appropriately. Exception testing ensures your error handling works correctly—before your users discover it doesn't.

Think of exception tests as fire drills: you hope you never need them, but when emergencies happen, you need confidence that your safety systems work.

## The Concept

### Why Test Exceptions?

Untested exception handling leads to:
- Silent failures where errors are swallowed
- Wrong exception types that confuse callers
- Missing validation that lets bad data through
- Cryptic error messages that frustrate users and developers

### The assertThrows Method

JUnit5's `assertThrows` is your primary tool for exception testing:

```java
<T extends Throwable> T assertThrows(
    Class<T> expectedType,
    Executable executable
)
```

It:
1. Executes the provided code
2. Expects an exception of the specified type (or subtype)
3. Returns the caught exception for further inspection
4. Fails if no exception is thrown or if a different type is thrown

### Basic Exception Testing

```java
@Test
void divide_byZero_throwsArithmeticException() {
    Calculator calc = new Calculator();
    
    assertThrows(ArithmeticException.class, () -> {
        calc.divide(10, 0);
    });
}
```

**What happens:**
- If `divide(10, 0)` throws `ArithmeticException` → Test passes
- If it throws a different exception → Test fails
- If it throws no exception → Test fails

### Capturing and Inspecting Exceptions

Often you need to verify not just the exception type, but also its message or cause:

```java
@Test
void createUser_withNullEmail_throwsIllegalArgumentException() {
    UserService service = new UserService();
    
    // Capture the exception
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> service.createUser("John", null)
    );
    
    // Verify the message
    assertEquals("Email cannot be null", exception.getMessage());
}
```

### Verifying Exception Messages

Several approaches for message verification:

```java
@Test
void validateAge_negative_throwsWithDescriptiveMessage() {
    Validator validator = new Validator();
    
    IllegalArgumentException ex = assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateAge(-5)
    );
    
    // Exact match
    assertEquals("Age must be non-negative", ex.getMessage());
    
    // Contains check (more flexible)
    assertTrue(ex.getMessage().contains("non-negative"));
    
    // Starts with
    assertTrue(ex.getMessage().startsWith("Age"));
}
```

### Testing Exception Types in Hierarchy

`assertThrows` matches the exact type or any subtype:

```java
@Test
void fileOperation_missingFile_throwsIOException() {
    FileProcessor processor = new FileProcessor();
    
    // This catches IOException or any subclass (FileNotFoundException, etc.)
    assertThrows(IOException.class, () -> {
        processor.readFile("nonexistent.txt");
    });
}

@Test
void fileOperation_missingFile_throwsSpecificException() {
    FileProcessor processor = new FileProcessor();
    
    // More specific - only FileNotFoundException
    assertThrows(FileNotFoundException.class, () -> {
        processor.readFile("nonexistent.txt");
    });
}
```

### Testing Exception Cause (Chained Exceptions)

```java
@Test
void processData_databaseError_wrapsOriginalException() {
    DataProcessor processor = new DataProcessor();
    
    ServiceException ex = assertThrows(
        ServiceException.class,
        () -> processor.process("invalid-data")
    );
    
    // Verify the wrapped cause
    assertNotNull(ex.getCause());
    assertInstanceOf(SQLException.class, ex.getCause());
}
```

### Using assertDoesNotThrow

Verify that code executes without throwing any exception:

```java
@Test
void processValidData_noException() {
    DataProcessor processor = new DataProcessor();
    
    // Explicit assertion that no exception occurs
    assertDoesNotThrow(() -> {
        processor.process("valid-data");
    });
}

// With return value
@Test
void calculateTotal_validItems_returnsResultWithoutException() {
    Calculator calc = new Calculator();
    
    double result = assertDoesNotThrow(() -> {
        return calc.calculateTotal(items);
    });
    
    assertEquals(100.0, result, 0.01);
}
```

**When to use assertDoesNotThrow:**
- When the method previously threw exceptions and you fixed it
- When edge cases that might throw are being tested
- When making the "no exception" contract explicit

### Testing Multiple Exception Scenarios

Use parameterized tests for comprehensive exception coverage:

```java
@ParameterizedTest
@ValueSource(strings = {"", " ", "   ", "\t", "\n"})
void validateUsername_blankValues_throwsException(String username) {
    Validator validator = new Validator();
    
    assertThrows(IllegalArgumentException.class, () -> {
        validator.validateUsername(username);
    });
}

@ParameterizedTest
@NullSource
@ValueSource(strings = {""})
void validateEmail_nullOrEmpty_throwsException(String email) {
    Validator validator = new Validator();
    
    assertThrows(IllegalArgumentException.class, () -> {
        validator.validateEmail(email);
    });
}
```

### Testing That Correct Exception Is Thrown (Not Just Any Exception)

```java
@Test
void withdraw_insufficientFunds_throwsInsufficientFundsException() {
    BankAccount account = new BankAccount(100.0);
    
    // Verify specific exception type, not just any RuntimeException
    InsufficientFundsException ex = assertThrows(
        InsufficientFundsException.class,  // Specific type
        () -> account.withdraw(500.0)
    );
    
    // Verify exception state
    assertEquals(100.0, ex.getAvailableBalance(), 0.01);
    assertEquals(500.0, ex.getRequestedAmount(), 0.01);
}
```

### Common Exception Testing Patterns

#### Pattern 1: Validation Exceptions

```java
@Test
void constructor_invalidParameters_throwsWithClearMessage() {
    assertAll(
        () -> {
            var ex = assertThrows(IllegalArgumentException.class,
                () -> new Person(null, "Doe"));
            assertTrue(ex.getMessage().contains("first name"));
        },
        () -> {
            var ex = assertThrows(IllegalArgumentException.class,
                () -> new Person("", "Doe"));
            assertTrue(ex.getMessage().contains("first name"));
        },
        () -> {
            var ex = assertThrows(IllegalArgumentException.class,
                () -> new Person("John", null));
            assertTrue(ex.getMessage().contains("last name"));
        }
    );
}
```

#### Pattern 2: State-Based Exceptions

```java
@Test
void start_alreadyRunning_throwsIllegalStateException() {
    Engine engine = new Engine();
    engine.start();  // First start is fine
    
    IllegalStateException ex = assertThrows(
        IllegalStateException.class,
        () -> engine.start()  // Second start should fail
    );
    
    assertEquals("Engine is already running", ex.getMessage());
}
```

#### Pattern 3: Resource Exceptions

```java
@Test
void connect_timeout_throwsConnectionException() {
    DatabaseClient client = new DatabaseClient("invalid-host", 5432);
    
    ConnectionException ex = assertThrows(
        ConnectionException.class,
        () -> client.connect()
    );
    
    assertNotNull(ex.getCause());
    assertTrue(ex.getMessage().contains("Failed to connect"));
}
```

## Code Example

### Comprehensive Exception Testing

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationExceptionTest {

    private final UserRegistrationService service = new UserRegistrationService();

    @Nested
    @DisplayName("Email validation exceptions")
    class EmailValidation {
        
        @Test
        @DisplayName("Null email throws IllegalArgumentException")
        void register_nullEmail_throwsException() {
            IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.register("John", null, "password123")
            );
            assertEquals("Email is required", ex.getMessage());
        }
        
        @ParameterizedTest
        @DisplayName("Invalid email formats throw ValidationException")
        @ValueSource(strings = {"notanemail", "missing@domain", "@nodomain.com", "spaces in@email.com"})
        void register_invalidEmailFormat_throwsValidationException(String email) {
            ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.register("John", email, "password123")
            );
            assertTrue(ex.getMessage().contains("Invalid email format"));
        }
        
        @Test
        @DisplayName("Duplicate email throws DuplicateUserException")
        void register_duplicateEmail_throwsDuplicateException() {
            // First registration succeeds
            assertDoesNotThrow(() -> 
                service.register("John", "john@example.com", "password123")
            );
            
            // Second registration with same email fails
            DuplicateUserException ex = assertThrows(
                DuplicateUserException.class,
                () -> service.register("Jane", "john@example.com", "password456")
            );
            assertEquals("john@example.com", ex.getEmail());
        }
    }
    
    @Nested
    @DisplayName("Password validation exceptions")
    class PasswordValidation {
        
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "short", "1234567"})
        @DisplayName("Weak passwords throw ValidationException")
        void register_weakPassword_throwsException(String password) {
            assertThrows(
                ValidationException.class,
                () -> service.register("John", "john@example.com", password)
            );
        }
        
        @Test
        @DisplayName("Password without special char throws with helpful message")
        void register_noSpecialChar_throwsWithMessage() {
            ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.register("John", "john@example.com", "password123")
            );
            assertTrue(ex.getMessage().contains("special character"),
                "Error message should mention special character requirement");
        }
    }
    
    @Nested
    @DisplayName("Happy path - no exceptions")
    class SuccessfulRegistration {
        
        @Test
        @DisplayName("Valid registration completes without exception")
        void register_validInput_noException() {
            User user = assertDoesNotThrow(() -> 
                service.register("John", "valid@email.com", "SecureP@ss123")
            );
            
            assertNotNull(user);
            assertEquals("John", user.getName());
        }
    }
}
```

### Exception Testing Anti-Patterns

```java
// ❌ ANTI-PATTERN 1: Using try-catch instead of assertThrows
@Test
void badPattern_tryCatch() {
    try {
        service.riskyOperation();
        fail("Should have thrown exception");  // Easy to forget!
    } catch (SomeException e) {
        // Test passes
    }
}

// ✅ CORRECT: Use assertThrows
@Test
void goodPattern_assertThrows() {
    assertThrows(SomeException.class, () -> {
        service.riskyOperation();
    });
}

// ❌ ANTI-PATTERN 2: Catching Exception (too broad)
@Test
void badPattern_tooBroad() {
    assertThrows(Exception.class, () -> {  // Too broad!
        service.doSomething();
    });
}

// ✅ CORRECT: Catch specific exception type
@Test
void goodPattern_specific() {
    assertThrows(IllegalArgumentException.class, () -> {
        service.doSomething();
    });
}

// ❌ ANTI-PATTERN 3: Not verifying exception message
@Test
void badPattern_noMessageCheck() {
    assertThrows(ValidationException.class, () -> {
        service.validate(data);
    });
    // But which validation failed? We don't know!
}

// ✅ CORRECT: Verify the message
@Test
void goodPattern_withMessageCheck() {
    ValidationException ex = assertThrows(
        ValidationException.class,
        () -> service.validate(data)
    );
    assertTrue(ex.getMessage().contains("email"));  // Now we know!
}
```

### Exception Testing Decision Guide

```
┌─────────────────────────────────────────────────────────────────┐
│              EXCEPTION TESTING DECISION GUIDE                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. What exception type should be thrown?                       │
│     └─► Use assertThrows with specific exception class          │
│                                                                 │
│  2. What message should the exception have?                     │
│     └─► Capture exception, assert on getMessage()               │
│                                                                 │
│  3. What caused the exception (wrapped exception)?              │
│     └─► Capture exception, assert on getCause()                 │
│                                                                 │
│  4. What state does the exception carry?                        │
│     └─► Capture custom exception, assert on custom getters      │
│                                                                 │
│  5. Should NO exception be thrown?                              │
│     └─► Use assertDoesNotThrow                                  │
│                                                                 │
│  6. Multiple exception scenarios?                               │
│     └─► Use @ParameterizedTest with @ValueSource                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Summary

- **assertThrows**: Primary tool for verifying exceptions are thrown
- Capture the exception to verify **messages**, **causes**, and **custom state**
- Use **specific exception types**, not broad ones like `Exception`
- **assertDoesNotThrow**: Explicitly verifies code runs without exceptions
- Use **parameterized tests** for testing multiple invalid inputs
- Always verify exception **messages** contain useful information
- Test exceptions as thoroughly as you test success cases

## Additional Resources

- [JUnit5 User Guide - Exception Testing](https://junit.org/junit5/docs/current/user-guide/#writing-tests-assertions-exceptions) - Official documentation
- [Effective Java: Exceptions](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/) - Best practices for exception design
- [Testing Exceptions in JUnit5](https://www.baeldung.com/junit-assert-exception) - Practical tutorial with examples

