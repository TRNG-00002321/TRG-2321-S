# Typical Unit Test Defects: What Unit Tests Catch

## Learning Objectives
- Identify common defect categories that unit tests excel at finding
- Recognize off-by-one errors and boundary violations in code
- Understand null pointer issues and how to test for them
- Detect logic errors through systematic unit testing

## Why This Matters

Unit tests aren't just about proving code works—they're about finding bugs before your users do. Research shows that **85% of defects** are introduced during coding, and unit tests catch the majority of these. Understanding the types of defects unit tests excel at finding helps you write more targeted, effective tests.

Think of unit tests as quality detectives. The better you understand what crimes (defects) they're good at solving, the more effective your testing strategy becomes.

## The Concept

### Categories of Defects Found Through Unit Testing

Unit tests are particularly effective at catching:

```
┌─────────────────────────────────────────────────────────────┐
│              DEFECTS UNIT TESTS CATCH BEST                  │
├─────────────────────────────────────────────────────────────┤
│  1. Off-by-One Errors (OBOE)                                │
│  2. Boundary Violations                                      │
│  3. Null Pointer Issues                                      │
│  4. Logic Errors                                             │
│  5. Incorrect Calculations                                   │
│  6. Invalid State Transitions                                │
│  7. Edge Case Handling                                       │
│  8. Type Conversion Errors                                   │
└─────────────────────────────────────────────────────────────┘
```

### 1. Off-by-One Errors (OBOE)

Off-by-one errors are among the most common programming mistakes. They occur when a loop iterates one time too many or too few, or when an index is off by one.

**The Bug:**
```java
public class ArrayProcessor {
    // BUG: Iterates one too many times
    public int sumArray(int[] numbers) {
        int sum = 0;
        for (int i = 0; i <= numbers.length; i++) {  // <= should be <
            sum += numbers[i];  // ArrayIndexOutOfBoundsException!
        }
        return sum;
    }
}
```

**The Test That Catches It:**
```java
@Test
void sumArray_withValidArray_returnsSumWithoutException() {
    ArrayProcessor processor = new ArrayProcessor();
    int[] numbers = {1, 2, 3, 4, 5};
    
    // This test exposes the off-by-one error
    int result = processor.sumArray(numbers);
    
    assertEquals(15, result);
}
```

**Common Off-by-One Scenarios:**

```java
// Scenario 1: Loop boundaries
for (int i = 0; i <= length; i++)    // Wrong: includes length
for (int i = 0; i < length; i++)     // Correct: stops before length

// Scenario 2: Array/List access
list.get(list.size())                // Wrong: index out of bounds
list.get(list.size() - 1)            // Correct: last element

// Scenario 3: Substring operations
str.substring(0, str.length() + 1)   // Wrong: end index too high
str.substring(0, str.length())       // Correct: full string

// Scenario 4: Range checks
if (index > array.length)            // Wrong: allows exact length
if (index >= array.length)           // Correct: catches boundary
```

### 2. Boundary Violations

Boundary errors occur at the edges of valid input ranges. They're closely related to off-by-one errors but encompass a broader category.

**The Bug:**
```java
public class AgeValidator {
    // BUG: Doesn't handle boundary values correctly
    public boolean isValidAge(int age) {
        // Should accept ages 0-120, but...
        if (age > 0 && age < 120) {  // Excludes 0 and 120!
            return true;
        }
        return false;
    }
}
```

**Tests That Catch Boundary Issues:**
```java
@Test
void isValidAge_atLowerBoundary_returnsTrue() {
    AgeValidator validator = new AgeValidator();
    assertTrue(validator.isValidAge(0), "Age 0 should be valid");
}

@Test
void isValidAge_atUpperBoundary_returnsTrue() {
    AgeValidator validator = new AgeValidator();
    assertTrue(validator.isValidAge(120), "Age 120 should be valid");
}

@Test
void isValidAge_justBelowLowerBoundary_returnsFalse() {
    AgeValidator validator = new AgeValidator();
    assertFalse(validator.isValidAge(-1), "Age -1 should be invalid");
}

@Test
void isValidAge_justAboveUpperBoundary_returnsFalse() {
    AgeValidator validator = new AgeValidator();
    assertFalse(validator.isValidAge(121), "Age 121 should be invalid");
}
```

**Boundary Testing Strategy (BVA - Boundary Value Analysis):**

```
For a valid range [MIN, MAX], test:
┌───────────────────────────────────────────────────┐
│  MIN-1  │  MIN  │  MIN+1  │...│  MAX-1  │  MAX  │  MAX+1  │
│ Invalid │ Valid │  Valid  │   │  Valid  │ Valid │ Invalid │
└───────────────────────────────────────────────────┘
```

### 3. Null Pointer Issues

NullPointerException is the "billion-dollar mistake" of programming. Unit tests help catch null handling issues before they crash production systems.

**The Bug:**
```java
public class UserService {
    // BUG: Doesn't handle null user
    public String getDisplayName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }
}
```

**Tests That Catch Null Issues:**
```java
@Test
void getDisplayName_withNullUser_throwsNullPointerException() {
    UserService service = new UserService();
    
    assertThrows(NullPointerException.class, () -> {
        service.getDisplayName(null);
    });
}

@Test
void getDisplayName_withNullFirstName_handlesGracefully() {
    UserService service = new UserService();
    User user = new User(null, "Doe");
    
    // Depending on requirements, test expected behavior
    String result = service.getDisplayName(user);
    // Either expect "null Doe" or a proper handling
    assertNotNull(result);
}
```

**Common Null Pointer Scenarios:**

```java
// Scenario 1: Dereferencing without null check
user.getName().toUpperCase();  // NPE if user or name is null

// Scenario 2: Collection operations
list.get(0).process();  // NPE if list is empty or element is null

// Scenario 3: Method chaining
order.getCustomer().getAddress().getCity();  // NPE anywhere in chain

// Scenario 4: Map access
map.get("key").toString();  // NPE if key doesn't exist
```

**Defensive Testing Approach:**
```java
@Test
void processOrder_withNullCustomer_returnsErrorResult() {
    OrderProcessor processor = new OrderProcessor();
    Order order = new Order();
    order.setCustomer(null);  // Intentionally null
    
    Result result = processor.process(order);
    
    assertFalse(result.isSuccess());
    assertEquals("Customer is required", result.getErrorMessage());
}
```

### 4. Logic Errors

Logic errors occur when code doesn't correctly implement the intended algorithm or business rules.

**The Bug:**
```java
public class DiscountCalculator {
    // BUG: Wrong operator - should be >= not >
    public double calculateDiscount(double amount) {
        if (amount > 100) {  // Customer spending exactly $100 gets no discount!
            return amount * 0.10;  // 10% discount
        }
        return 0;
    }
}
```

**Tests That Catch Logic Errors:**
```java
@Test
void calculateDiscount_atExactThreshold_appliesDiscount() {
    DiscountCalculator calc = new DiscountCalculator();
    
    double discount = calc.calculateDiscount(100.00);
    
    assertEquals(10.00, discount, 0.01, 
        "Spending exactly $100 should qualify for discount");
}

@Test
void calculateDiscount_belowThreshold_noDiscount() {
    DiscountCalculator calc = new DiscountCalculator();
    
    double discount = calc.calculateDiscount(99.99);
    
    assertEquals(0, discount, 0.01);
}
```

**Common Logic Error Patterns:**

```java
// Pattern 1: Wrong operator
if (a > b) vs if (a >= b)
if (a == b) vs if (a != b)
if (a && b) vs if (a || b)

// Pattern 2: Inverted conditions
if (!isValid) { proceed(); }     // Wrong: proceeds when invalid
if (isValid) { proceed(); }      // Correct

// Pattern 3: Missing conditions
if (status == ACTIVE) {          // Missing: what about PENDING?
    process();
}

// Pattern 4: Incorrect order of operations
total = price + tax * quantity;   // Wrong: tax applied only to quantity
total = (price + tax) * quantity; // Correct: tax included per item
```

### 5. Incorrect Calculations

Mathematical and computational errors are easy to make and hard to spot in code review.

**The Bug:**
```java
public class InterestCalculator {
    // BUG: Integer division loses precision
    public double calculateInterestRate(int earned, int principal) {
        return earned / principal;  // Integer division!
    }
}
```

**The Test:**
```java
@Test
void calculateInterestRate_withValidInputs_returnsCorrectRate() {
    InterestCalculator calc = new InterestCalculator();
    
    // $50 earned on $1000 principal = 5% = 0.05
    double rate = calc.calculateInterestRate(50, 1000);
    
    assertEquals(0.05, rate, 0.001, "Interest rate should be 0.05 (5%)");
    // This test fails because 50/1000 = 0 (integer division)
}
```

### 6. Invalid State Transitions

State machines can transition to invalid states if transitions aren't properly validated.

**The Bug:**
```java
public class OrderStateMachine {
    // BUG: Allows invalid transition from DELIVERED back to PROCESSING
    public void transition(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);  // No validation!
    }
}
```

**Tests for State Transitions:**
```java
@Test
void transition_fromDeliveredToProcessing_throwsException() {
    OrderStateMachine machine = new OrderStateMachine();
    Order order = new Order();
    order.setStatus(OrderStatus.DELIVERED);
    
    assertThrows(IllegalStateException.class, () -> {
        machine.transition(order, OrderStatus.PROCESSING);
    }, "Cannot transition from DELIVERED back to PROCESSING");
}

@Test
void transition_fromProcessingToShipped_succeeds() {
    OrderStateMachine machine = new OrderStateMachine();
    Order order = new Order();
    order.setStatus(OrderStatus.PROCESSING);
    
    machine.transition(order, OrderStatus.SHIPPED);
    
    assertEquals(OrderStatus.SHIPPED, order.getStatus());
}
```

## Code Example

### Comprehensive Defect-Catching Test Suite

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class StringUtilsDefectTests {

    // Testing for Off-by-One Errors
    @Test
    @DisplayName("Substring extraction should handle exact length")
    void getFirstNChars_exactLength_returnsFullString() {
        String result = StringUtils.getFirstNChars("Hello", 5);
        assertEquals("Hello", result);
    }
    
    @Test
    @DisplayName("Substring extraction should handle length exceeding string")
    void getFirstNChars_lengthExceedsString_returnsFullString() {
        String result = StringUtils.getFirstNChars("Hi", 10);
        assertEquals("Hi", result);  // Should not throw exception
    }
    
    // Testing for Null Pointer Issues
    @Test
    @DisplayName("Null input should be handled gracefully")
    void reverse_nullInput_returnsNull() {
        String result = StringUtils.reverse(null);
        assertNull(result);
    }
    
    @Test
    @DisplayName("Empty string reversal should return empty string")
    void reverse_emptyString_returnsEmpty() {
        String result = StringUtils.reverse("");
        assertEquals("", result);
    }
    
    // Testing for Boundary Violations
    @ParameterizedTest
    @DisplayName("Index at boundary should work correctly")
    @ValueSource(ints = {0, 4})  // First and last valid indices for "Hello"
    void charAt_boundaryIndices_returnsCorrectChar(int index) {
        char result = StringUtils.safeCharAt("Hello", index);
        assertNotEquals('\0', result);  // Should return valid char
    }
    
    @ParameterizedTest
    @DisplayName("Index outside boundaries should return default")
    @ValueSource(ints = {-1, 5, 100})  // Invalid indices
    void charAt_invalidIndices_returnsDefault(int index) {
        char result = StringUtils.safeCharAt("Hello", index);
        assertEquals('\0', result);  // Should return null char
    }
    
    // Testing for Logic Errors
    @Test
    @DisplayName("Palindrome check should be case-insensitive")
    void isPalindrome_mixedCase_returnsTrue() {
        assertTrue(StringUtils.isPalindrome("RaceCar"));
    }
    
    @Test
    @DisplayName("Palindrome check should ignore spaces")
    void isPalindrome_withSpaces_returnsTrue() {
        assertTrue(StringUtils.isPalindrome("A man a plan a canal Panama"));
    }
}
```

### Testing Checklist for Common Defects

```java
/*
 * DEFECT TESTING CHECKLIST
 * ========================
 * 
 * For each method, consider testing:
 * 
 * □ Null inputs
 * □ Empty inputs (empty string, empty array, empty list)
 * □ Single element inputs
 * □ Boundary values (min, max, min-1, max+1)
 * □ Zero values (0, 0.0)
 * □ Negative values (when applicable)
 * □ Maximum values (Integer.MAX_VALUE, etc.)
 * □ Special characters in strings
 * □ Unicode/internationalization
 * □ Concurrent access (if applicable)
 * □ Order dependencies
 * □ State before and after
 */
```

## Summary

- **Off-by-One Errors**: Test loop boundaries, array indices, and range endpoints
- **Boundary Violations**: Apply Boundary Value Analysis—test at, above, and below limits
- **Null Pointer Issues**: Always test null inputs and empty collections
- **Logic Errors**: Test all branches, operators, and business rule conditions
- **Incorrect Calculations**: Verify mathematical operations with known results
- **Invalid State Transitions**: Test both valid and invalid state changes
- Unit tests excel at catching defects **early**, when they're cheapest to fix

## Additional Resources

- [Common Programming Mistakes](https://owasp.org/www-community/vulnerabilities/) - OWASP vulnerability patterns
- [Boundary Value Analysis](https://www.guru99.com/equivalence-partitioning-boundary-value-analysis.html) - Testing technique tutorial
- [Defensive Programming](https://enterprisecraftsmanship.com/posts/defensive-programming/) - Writing robust code that's easier to test

