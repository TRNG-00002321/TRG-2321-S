# Lab: Assertion Practice - Testing StringUtils

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Beginner-Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | validating-functions.md, demo_assertions_comprehensive.java |

## Learning Objectives
By completing this exercise, you will:
- Master all JUnit5 assertion methods
- Use `assertAll()` for grouped assertions
- Apply `assertNull()` and `assertNotNull()`
- Use `assertArrayEquals()` and `assertIterableEquals()`
- Write meaningful assertion messages

## The Scenario

Your team has created a `StringUtils` utility class with various string manipulation methods. QA has found several bugs in production, and management wants comprehensive tests for every method. Your task is to write tests using ALL available assertion types.

## Core Tasks

### Task 1: Test `reverse()` Method (10 minutes)

Write tests using `assertEquals` for:
| Input | Expected Output |
|-------|-----------------|
| "hello" | "olleh" |
| "a" | "a" |
| "" | "" |

### Task 2: Test `isEmpty()` Method (10 minutes)

Write tests using `assertTrue` and `assertFalse`:
- Empty string should return true
- String with spaces should return false
- Non-empty string should return false

### Task 3: Test `findFirst()` with Null Handling (10 minutes)

Write tests using `assertNull` and `assertNotNull`:
- When item exists, return non-null
- When item doesn't exist, return null

### Task 4: Test `split()` with Array Assertions (10 minutes)

Write tests using `assertArrayEquals`:
```java
// Example: "a,b,c".split(",") should return ["a", "b", "c"]
```

### Task 5: Master `assertAll()` (15 minutes)

Write a single test that validates a `User` object using `assertAll`:
```java
@Test
void user_allPropertiesValid() {
    User user = StringUtils.parseUser("John,Doe,30,john@test.com");
    
    assertAll("User properties",
        () -> assertEquals("John", user.getFirstName()),
        () -> assertEquals("Doe", user.getLastName()),
        () -> assertEquals(30, user.getAge()),
        () -> assertNotNull(user.getEmail()),
        () -> assertTrue(user.getEmail().contains("@"))
    );
}
```

## Starter Code

The `StringUtils` class is provided in `starter_code/`:

```java
public class StringUtils {
    
    public static String reverse(String input) {
        if (input == null) return null;
        return new StringBuilder(input).reverse().toString();
    }
    
    public static boolean isEmpty(String input) {
        return input == null || input.length() == 0;
    }
    
    public static boolean isBlank(String input) {
        return input == null || input.trim().length() == 0;
    }
    
    public static String findFirst(String[] items, String prefix) {
        if (items == null) return null;
        for (String item : items) {
            if (item != null && item.startsWith(prefix)) {
                return item;
            }
        }
        return null;
    }
    
    public static String[] split(String input, String delimiter) {
        if (input == null) return new String[0];
        return input.split(delimiter);
    }
    
    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
    
    public static User parseUser(String csv) {
        String[] parts = csv.split(",");
        return new User(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]);
    }
}
```

## Assertion Cheat Sheet

| Assertion | Use Case |
|-----------|----------|
| `assertEquals(expected, actual)` | Compare values |
| `assertTrue(condition)` | Verify boolean is true |
| `assertFalse(condition)` | Verify boolean is false |
| `assertNull(value)` | Verify value is null |
| `assertNotNull(value)` | Verify value is not null |
| `assertSame(obj1, obj2)` | Verify same object reference |
| `assertArrayEquals(arr1, arr2)` | Compare arrays element by element |
| `assertIterableEquals(list1, list2)` | Compare collections |
| `assertAll(heading, executables...)` | Group multiple assertions |
| `assertThrows(Exception.class, () -> ...)` | Verify exception thrown |

## Definition of Done

- [ ] Tests for `reverse()` using `assertEquals`
- [ ] Tests for `isEmpty()` using `assertTrue`/`assertFalse`
- [ ] Tests for `findFirst()` using `assertNull`/`assertNotNull`
- [ ] Tests for `split()` using `assertArrayEquals`
- [ ] At least one test using `assertAll()` with 4+ assertions
- [ ] All assertion messages are meaningful
- [ ] All tests pass

## Stretch Goals

1. Use `assertIterableEquals()` to compare Lists
2. Use `assertSame()` to test object identity
3. Use `assertTimeout()` to verify performance

## Hints

<details>
<summary>Hint: assertAll Syntax</summary>

```java
assertAll("Group name",
    () -> assertEquals(expected1, actual1),
    () -> assertTrue(condition),
    () -> assertNotNull(value)
);
```
Each assertion is a lambda!
</details>

<details>
<summary>Hint: Array Assertion</summary>

```java
String[] expected = {"a", "b", "c"};
String[] actual = StringUtils.split("a,b,c", ",");
assertArrayEquals(expected, actual);
```
</details>

## Submission

Commit with message:
```
feat(week6): Complete assertion practice exercise
```

