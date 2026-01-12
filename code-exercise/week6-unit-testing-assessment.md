# Week 6: Unit Testing - Coding Assessment

This assessment covers **JUnit5**, **Mockito**, **pytest**, **mocking**, and **test fixtures**. Complete all 5 exercises.

---

## Exercise 1: JUnit5 Basic Assertions (Beginner)

Write a JUnit5 test class for a `StringUtils` class that has the following methods:
- `reverse(String str)` - reverses a string
- `isPalindrome(String str)` - returns true if the string is a palindrome
- `countVowels(String str)` - returns the count of vowels

**Requirements:**
- Create at least 2 test methods per utility method
- Use appropriate assertions (`assertEquals`, `assertTrue`, `assertFalse`)
- Include a test for null/empty input handling

**Starter Code:**
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    // TODO: Create an instance of StringUtils
    
    @Test
    void testReverse_normalString() {
        // TODO: Test that "hello" reversed is "olleh"
    }
    
    @Test
    void testReverse_emptyString() {
        // TODO: Test that empty string reversed is empty string
    }
    
    // TODO: Add tests for isPalindrome and countVowels
}
```

---

## Exercise 2: Test Lifecycle with Setup/Teardown (Beginner)

Create a test class for a `BankAccount` class that uses lifecycle annotations properly.

**Requirements:**
- Use `@BeforeAll` to print "Starting BankAccount tests"
- Use `@BeforeEach` to create a fresh `BankAccount` with initial balance of 1000
- Use `@AfterEach` to reset the account
- Use `@AfterAll` to print "Completed BankAccount tests"
- Write tests for `deposit()`, `withdraw()`, and `getBalance()`

**Starter Code:**
```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    private BankAccount account;
    
    @BeforeAll
    static void setupAll() {
        // TODO: Print starting message
    }
    
    @BeforeEach
    void setup() {
        // TODO: Initialize account with balance 1000
    }
    
    @AfterEach
    void tearDown() {
        // TODO: Reset account to null
    }
    
    @AfterAll
    static void tearDownAll() {
        // TODO: Print completion message
    }
    
    @Test
    void testDeposit_positiveAmount() {
        // TODO: Deposit 500 and verify balance is 1500
    }
    
    @Test
    void testWithdraw_sufficientFunds() {
        // TODO: Withdraw 300 and verify balance is 700
    }
    
    @Test
    void testWithdraw_insufficientFunds_throwsException() {
        // TODO: Verify that withdrawing 2000 throws InsufficientFundsException
    }
}
```

---

## Exercise 3: Parameterized Testing (Intermediate)

Create parameterized tests for a `GradeCalculator` class that converts percentage scores to letter grades.

**Grade Scale:**
- 90-100: A
- 80-89: B
- 70-79: C
- 60-69: D
- Below 60: F

**Requirements:**
- Use `@ParameterizedTest` with `@CsvSource` to test multiple score-to-grade conversions
- Use `@ValueSource` to test that invalid scores (negative, >100) throw `IllegalArgumentException`
- Include edge cases (59, 60, 89, 90, 100)

**Starter Code:**
```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class GradeCalculatorTest {

    private GradeCalculator calculator = new GradeCalculator();
    
    @ParameterizedTest
    @CsvSource({
        // TODO: Add test cases as "score, expectedGrade" pairs
        // Example: "95, A"
    })
    void testCalculateGrade(int score, String expectedGrade) {
        assertEquals(expectedGrade, calculator.calculateGrade(score));
    }
    
    @ParameterizedTest
    @ValueSource(ints = { /* TODO: Add invalid scores */ })
    void testCalculateGrade_invalidScore_throwsException(int invalidScore) {
        // TODO: Assert that IllegalArgumentException is thrown
    }
}
```

---

## Exercise 4: Mockito - Mocking Dependencies (Intermediate)

You have a `UserService` class that depends on `UserRepository` and `EmailService`. Write unit tests using Mockito to mock these dependencies.

**Class Under Test:**
```java
public class UserService {
    private UserRepository userRepository;
    private EmailService emailService;
    
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    
    public boolean registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return false;
        }
        userRepository.save(user);
        emailService.sendWelcomeEmail(user.getEmail());
        return true;
    }
}
```

**Requirements:**
- Mock `UserRepository` and `EmailService` using `@Mock` annotation
- Use `@InjectMocks` for `UserService`
- Test successful registration (user doesn't exist)
- Test failed registration (user already exists)
- Verify that `sendWelcomeEmail` is called only on successful registration

**Starter Code:**
```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void testRegisterUser_success() {
        User newUser = new User("test@example.com", "John Doe");
        
        // TODO: Stub userRepository.existsByEmail() to return false
        // TODO: Call registerUser and assert it returns true
        // TODO: Verify that save() was called once
        // TODO: Verify that sendWelcomeEmail() was called once
    }
    
    @Test
    void testRegisterUser_userExists() {
        User existingUser = new User("existing@example.com", "Jane Doe");
        
        // TODO: Stub userRepository.existsByEmail() to return true
        // TODO: Call registerUser and assert it returns false
        // TODO: Verify that save() was never called
        // TODO: Verify that sendWelcomeEmail() was never called
    }
}
```

---

## Exercise 5: pytest with Fixtures (Intermediate)

Write pytest tests for a Python `ShoppingCart` class using fixtures and parametrization.

**Class Under Test:**
```python
class ShoppingCart:
    def __init__(self):
        self.items = []
    
    def add_item(self, name: str, price: float, quantity: int = 1):
        self.items.append({"name": name, "price": price, "quantity": quantity})
    
    def get_total(self) -> float:
        return sum(item["price"] * item["quantity"] for item in self.items)
    
    def get_item_count(self) -> int:
        return sum(item["quantity"] for item in self.items)
    
    def apply_discount(self, percentage: float) -> float:
        if percentage < 0 or percentage > 100:
            raise ValueError("Discount must be between 0 and 100")
        return self.get_total() * (1 - percentage / 100)
```

**Requirements:**
- Create a `@pytest.fixture` that returns a cart with 2 sample items
- Test `add_item()`, `get_total()`, and `get_item_count()`
- Use `@pytest.mark.parametrize` to test `apply_discount()` with various percentages
- Test that invalid discount percentages raise `ValueError`

**Starter Code:**
```python
import pytest
from shopping_cart import ShoppingCart

@pytest.fixture
def cart_with_items():
    # TODO: Create a ShoppingCart and add two items:
    # - "Book" at $15.99, quantity 2
    # - "Pen" at $1.50, quantity 5
    pass

def test_add_item():
    # TODO: Test adding an item to an empty cart
    pass

def test_get_total(cart_with_items):
    # TODO: Use the fixture and verify total is calculated correctly
    pass

def test_get_item_count(cart_with_items):
    # TODO: Verify item count is 7 (2 + 5)
    pass

@pytest.mark.parametrize("discount,expected", [
    # TODO: Add test cases for 0%, 10%, 50%, 100% discounts
])
def test_apply_discount(cart_with_items, discount, expected):
    # TODO: Test discount calculation
    pass

def test_apply_discount_invalid_percentage(cart_with_items):
    # TODO: Test that -10 and 150 raise ValueError
    pass
```

---

## Submission Guidelines

1. Complete all exercises in separate test files
2. Ensure all tests pass when run
3. Include meaningful test method names that describe the scenario
4. Add comments explaining your test logic where appropriate
