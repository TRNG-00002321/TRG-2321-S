# Parameterizing Tests: Data-Driven Testing in JUnit5

## Learning Objectives
- Use `@ParameterizedTest` to run tests with multiple inputs
- Apply different argument sources: `@ValueSource`, `@CsvSource`, `@MethodSource`, `@EnumSource`
- Create custom argument providers for complex scenarios
- Customize test display names for parameterized tests

## Why This Matters

When testing a method that accepts various inputs, writing separate tests for each case leads to code duplication and maintenance headaches. Parameterized tests let you write the test logic once and run it with many different inputs—making your test suite more comprehensive while keeping it DRY (Don't Repeat Yourself).

## The Concept

### The Problem: Repetitive Tests

```java
// Without parameterization - repetitive!
@Test void isEven_with2_returnsTrue() { assertTrue(isEven(2)); }
@Test void isEven_with4_returnsTrue() { assertTrue(isEven(4)); }
@Test void isEven_with100_returnsTrue() { assertTrue(isEven(100)); }
@Test void isEven_with1_returnsFalse() { assertFalse(isEven(1)); }
@Test void isEven_with3_returnsFalse() { assertFalse(isEven(3)); }
// ... and on and on
```

### The Solution: Parameterized Tests

```java
// With parameterization - clean and comprehensive!
@ParameterizedTest
@ValueSource(ints = {2, 4, 6, 100, 0, -2})
void isEven_evenNumbers_returnsTrue(int number) {
    assertTrue(isEven(number));
}

@ParameterizedTest
@ValueSource(ints = {1, 3, 5, 99, -1})
void isEven_oddNumbers_returnsFalse(int number) {
    assertFalse(isEven(number));
}
```

### Dependencies Required

Add to your `pom.xml`:
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-params</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

### @ValueSource: Simple Single Values

For testing with primitive types and strings:

```java
@ParameterizedTest
@ValueSource(ints = {1, 2, 3, 4, 5})
void squareRoot_positiveNumbers_returnsCorrectResult(int number) {
    assertTrue(Math.sqrt(number) > 0);
}

@ParameterizedTest
@ValueSource(strings = {"hello", "world", "JUnit5"})
void toUpperCase_convertsCorrectly(String input) {
    assertEquals(input.toUpperCase(), input.toUpperCase(Locale.ROOT));
}

@ParameterizedTest
@ValueSource(doubles = {1.0, 2.5, 3.14, 100.0})
void absoluteValue_positiveDoubles_returnsSameValue(double value) {
    assertEquals(value, Math.abs(value));
}
```

**Supported types:**
- `shorts`, `bytes`, `ints`, `longs`, `floats`, `doubles`
- `chars`, `booleans`, `strings`, `classes`

### @NullSource and @EmptySource

Test null and empty values:

```java
@ParameterizedTest
@NullSource
void validate_nullInput_throwsException(String input) {
    assertThrows(IllegalArgumentException.class, 
        () -> validator.validate(input));
}

@ParameterizedTest
@EmptySource
void validate_emptyInput_throwsException(String input) {
    assertThrows(IllegalArgumentException.class, 
        () -> validator.validate(input));
}

// Combine them!
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {"  ", "\t", "\n"})
void validate_blankInputs_throwsException(String input) {
    assertThrows(IllegalArgumentException.class, 
        () -> validator.validate(input));
}
```

### @CsvSource: Multiple Parameters

When your test needs multiple inputs:

```java
@ParameterizedTest
@CsvSource({
    "1, 2, 3",      // 1 + 2 = 3
    "0, 0, 0",      // 0 + 0 = 0
    "-1, 1, 0",     // -1 + 1 = 0
    "100, 200, 300" // 100 + 200 = 300
})
void add_variousInputs_returnsCorrectSum(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}

@ParameterizedTest
@CsvSource({
    "john@example.com, true",
    "invalid-email, false",
    "missing@domain, false",
    "'', false"  // Use quotes for empty string
})
void validateEmail_variousInputs_returnsExpected(String email, boolean expected) {
    assertEquals(expected, validator.isValidEmail(email));
}
```

**CSV formatting tips:**
- Use single quotes for strings with commas: `"'Hello, World', greeting"`
- Use `''` for empty strings
- Customize delimiter: `@CsvSource(value = {...}, delimiter = '|')`

### @CsvFileSource: External Test Data

Load test data from CSV files:

```java
@ParameterizedTest
@CsvFileSource(resources = "/test-data/users.csv", numLinesToSkip = 1)
void createUser_fromCsvFile_succeeds(String name, String email, int age) {
    User user = new User(name, email, age);
    assertNotNull(user.getId());
}
```

File `src/test/resources/test-data/users.csv`:
```csv
name,email,age
John,john@example.com,30
Jane,jane@example.com,25
Bob,bob@example.com,40
```

### @MethodSource: Complex Arguments

For complex objects or computed values:

```java
@ParameterizedTest
@MethodSource("provideStringsForIsBlank")
void isBlank_variousStrings_returnsExpected(String input, boolean expected) {
    assertEquals(expected, StringUtils.isBlank(input));
}

// Method must be static and return Stream, Iterable, or array
static Stream<Arguments> provideStringsForIsBlank() {
    return Stream.of(
        Arguments.of(null, true),
        Arguments.of("", true),
        Arguments.of("  ", true),
        Arguments.of("hello", false),
        Arguments.of(" hello ", false)
    );
}

// For complex objects
@ParameterizedTest
@MethodSource("provideUsersForValidation")
void validateUser_variousUsers_validatesCorrectly(User user, boolean expected) {
    assertEquals(expected, userValidator.isValid(user));
}

static Stream<Arguments> provideUsersForValidation() {
    return Stream.of(
        Arguments.of(new User("John", "john@email.com", 25), true),
        Arguments.of(new User(null, "john@email.com", 25), false),
        Arguments.of(new User("John", null, 25), false),
        Arguments.of(new User("John", "john@email.com", -1), false)
    );
}
```

### @EnumSource: Testing with Enums

```java
@ParameterizedTest
@EnumSource(Month.class)
void getMonthLength_allMonths_returnsPositive(Month month) {
    int length = month.length(false);
    assertTrue(length > 0 && length <= 31);
}

// Filter specific enum values
@ParameterizedTest
@EnumSource(value = Month.class, names = {"APRIL", "JUNE", "SEPTEMBER", "NOVEMBER"})
void thirtyDayMonths_haveThirtyDays(Month month) {
    assertEquals(30, month.length(false));
}

// Exclude values
@ParameterizedTest
@EnumSource(value = Month.class, names = {"FEBRUARY"}, mode = EnumSource.Mode.EXCLUDE)
void nonFebruaryMonths_haveAtLeast30Days(Month month) {
    assertTrue(month.length(false) >= 30);
}
```

### Custom Display Names

Make test results readable:

```java
@ParameterizedTest(name = "{0} + {1} = {2}")
@CsvSource({"1, 2, 3", "10, 20, 30", "-1, 1, 0"})
void add_displayName(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}
// Results: "1 + 2 = 3", "10 + 20 = 30", "-1 + 1 = 0"

@ParameterizedTest(name = "Email ''{0}'' should be {1}")
@CsvSource({"valid@email.com, valid", "invalid, invalid"})
void validateEmail_withCustomName(String email, String expectation) {
    // ...
}
// Results: "Email 'valid@email.com' should be valid"
```

**Placeholders:**
- `{index}` - invocation index (1, 2, 3...)
- `{arguments}` - all arguments comma-separated
- `{0}`, `{1}`, ... - individual arguments

## Code Example

### Comprehensive Parameterized Testing

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class StringCalculatorParameterizedTest {

    private final StringCalculator calc = new StringCalculator();

    // Simple single values
    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "42", "100"})
    void add_singleNumber_returnsThatNumber(String input) {
        assertEquals(Integer.parseInt(input), calc.add(input));
    }

    // Null and empty handling
    @ParameterizedTest
    @NullAndEmptySource
    void add_nullOrEmpty_returnsZero(String input) {
        assertEquals(0, calc.add(input));
    }

    // Multiple parameters from CSV
    @ParameterizedTest(name = "add(\"{0}\") = {1}")
    @CsvSource({
        "'1,2', 3",
        "'1,2,3', 6",
        "'10,20,30', 60",
        "'1,2,3,4,5', 15"
    })
    void add_multipleNumbers_returnsSum(String input, int expected) {
        assertEquals(expected, calc.add(input));
    }

    // Complex scenarios from method
    @ParameterizedTest(name = "#{index}: {0}")
    @MethodSource("provideEdgeCases")
    void add_edgeCases_handledCorrectly(String description, String input, int expected) {
        assertEquals(expected, calc.add(input), description);
    }

    static Stream<Arguments> provideEdgeCases() {
        return Stream.of(
            Arguments.of("Negative numbers", "-1,2", 1),
            Arguments.of("Large numbers", "1000,2000", 3000),
            Arguments.of("Decimal truncation", "1.5,2.5", 4),
            Arguments.of("Whitespace handling", " 1 , 2 ", 3)
        );
    }

    // Enum-based testing
    @ParameterizedTest
    @EnumSource(Delimiter.class)
    void add_withDifferentDelimiters_works(Delimiter delimiter) {
        String input = "1" + delimiter.getChar() + "2";
        assertEquals(3, calc.addWithDelimiter(input, delimiter));
    }
}
```

## Summary

- **@ParameterizedTest** runs the same test with different data sets
- **@ValueSource**: Simple primitive/string values
- **@NullSource/@EmptySource**: Test null/empty edge cases
- **@CsvSource**: Multiple inline parameters
- **@CsvFileSource**: Load data from CSV files
- **@MethodSource**: Complex objects and computed values
- **@EnumSource**: Test with enum values
- Customize display names with `(name = "...")`

## Additional Resources

- [JUnit5 Parameterized Tests Guide](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests) - Official documentation
- [Baeldung: Parameterized Tests](https://www.baeldung.com/parameterized-tests-junit-5) - Comprehensive tutorial
- [Data-Driven Testing Patterns](https://www.softwaretestinghelp.com/data-driven-framework-selenium-tutorial/) - Testing strategies

