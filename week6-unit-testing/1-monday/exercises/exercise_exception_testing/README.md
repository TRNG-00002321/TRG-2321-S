# Lab: Exception Testing - UserValidation

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | testing-exceptions.md, demo_exception_testing.java |

## Learning Objectives
By completing this exercise, you will:
- Use `assertThrows()` to verify exceptions are thrown
- Capture and inspect exception messages
- Test multiple exception scenarios
- Use `assertDoesNotThrow()` for valid inputs
- Avoid exception testing anti-patterns

## The Scenario

You're working on a user registration system. The `UserValidation` class validates user input before creating accounts. Invalid input should throw specific exceptions with helpful messages. Your job is to write tests that verify:
1. The correct exception type is thrown
2. The exception message is helpful
3. Valid inputs don't throw exceptions

## Core Tasks

### Task 1: Test Email Validation (15 minutes)

The `validateEmail()` method should throw `IllegalArgumentException` for:
| Invalid Email | Expected Message Contains |
|---------------|---------------------------|
| null | "cannot be null" |
| "" | "cannot be empty" |
| "noatsign" | "must contain @" |
| "@nodomain" | "invalid format" |

Write tests that:
1. Verify the exception type
2. Capture and verify the message

### Task 2: Test Password Validation (15 minutes)

The `validatePassword()` method should throw `ValidationException` for:
| Invalid Password | Reason |
|------------------|--------|
| null | Null not allowed |
| "short" | Less than 8 characters |
| "nouppercase" | No uppercase letter |
| "NOLOWERCASE" | No lowercase letter |

Write tests that verify both exception type AND message.

### Task 3: Test Age Validation (10 minutes)

The `validateAge()` method should:
- Throw `IllegalArgumentException` for negative ages
- Throw `IllegalArgumentException` for ages over 150
- NOT throw for valid ages (0-150)

Use `assertDoesNotThrow()` to verify valid cases!

### Task 4: Combine with assertAll (10 minutes)

Write a single test that validates multiple invalid inputs:
```java
@Test
void validateEmail_multipleInvalidInputs_allThrowExceptions() {
    assertAll("Email validation exceptions",
        () -> assertThrows(IllegalArgumentException.class, 
            () -> validator.validateEmail(null)),
        () -> assertThrows(IllegalArgumentException.class, 
            () -> validator.validateEmail("")),
        () -> assertThrows(IllegalArgumentException.class, 
            () -> validator.validateEmail("invalid"))
    );
}
```

## Starter Code

```java
public class UserValidation {
    
    public void validateEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email must contain @");
        }
        if (email.startsWith("@") || email.endsWith("@")) {
            throw new IllegalArgumentException("Email has invalid format");
        }
    }
    
    public void validatePassword(String password) {
        if (password == null) {
            throw new ValidationException("Password cannot be null");
        }
        if (password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain an uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Password must contain a lowercase letter");
        }
    }
    
    public void validateAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        if (age > 150) {
            throw new IllegalArgumentException("Age cannot exceed 150");
        }
    }
}

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
```

## Exception Testing Patterns

### Pattern 1: Basic assertThrows
```java
@Test
void method_invalidInput_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> {
        validator.validateEmail(null);
    });
}
```

### Pattern 2: Capture and Verify Message
```java
@Test
void method_invalidInput_hasCorrectMessage() {
    IllegalArgumentException ex = assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateEmail(null)
    );
    assertTrue(ex.getMessage().contains("cannot be null"));
}
```

### Pattern 3: assertDoesNotThrow
```java
@Test
void method_validInput_noException() {
    assertDoesNotThrow(() -> {
        validator.validateEmail("valid@email.com");
    });
}
```

## Definition of Done

- [ ] At least 4 tests for `validateEmail()` covering all invalid cases
- [ ] At least 4 tests for `validatePassword()` with message verification
- [ ] At least 3 tests for `validateAge()` including valid cases
- [ ] One test using `assertAll()` for multiple exception scenarios
- [ ] At least 2 tests using `assertDoesNotThrow()`
- [ ] Exception messages are verified (not just exception type)
- [ ] All tests pass

## Anti-Patterns to Avoid

```java
// DON'T: Try-catch with fail()
@Test
void badPattern() {
    try {
        validator.validateEmail(null);
        fail("Should have thrown");  // Easy to forget!
    } catch (IllegalArgumentException e) {
        // passes
    }
}

// DON'T: Catch too-broad exception
@Test
void tooBroad() {
    assertThrows(Exception.class, () -> ...);  // Too broad!
}

// DON'T: Ignore exception message
@Test
void noMessageCheck() {
    assertThrows(IllegalArgumentException.class, () -> ...);
    // But WHICH validation failed? We don't know!
}
```

## Submission

Commit with message:
```
feat(week6): Complete exception testing exercise
```

