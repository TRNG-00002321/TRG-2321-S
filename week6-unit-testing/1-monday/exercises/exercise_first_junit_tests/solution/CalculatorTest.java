import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * SOLUTION: Comprehensive tests for the Calculator class.
 * 
 * This solution demonstrates:
 * - Proper test structure with AAA pattern
 * - Meaningful test names
 * - @DisplayName for readable output
 * - @Nested classes for organization
 * - Assertion messages
 */
@DisplayName("Calculator Tests")
class CalculatorTest {

    private final Calculator calculator = new Calculator();

    // ==========================================================
    // Task 2: Addition Tests
    // ==========================================================

    @Nested
    @DisplayName("Addition Tests")
    class AdditionTests {

        @Test
        @DisplayName("Adding two positive numbers returns correct sum")
        void add_twoPositiveNumbers_returnsSum() {
            // Arrange
            int a = 5;
            int b = 3;

            // Act
            int result = calculator.add(a, b);

            // Assert
            assertEquals(8, result, "5 + 3 should equal 8");
        }

        @Test
        @DisplayName("Adding positive and negative number returns correct result")
        void add_positiveAndNegative_returnsCorrectResult() {
            // Arrange
            int a = 10;
            int b = -3;

            // Act
            int result = calculator.add(a, b);

            // Assert
            assertEquals(7, result, "10 + (-3) should equal 7");
        }

        @Test
        @DisplayName("Adding two negative numbers returns negative sum")
        void add_twoNegativeNumbers_returnsNegativeSum() {
            // Arrange
            int a = -5;
            int b = -3;

            // Act
            int result = calculator.add(a, b);

            // Assert
            assertEquals(-8, result, "-5 + (-3) should equal -8");
        }

        @Test
        @DisplayName("Adding zero returns the original number")
        void add_withZero_returnsOriginalNumber() {
            // Arrange
            int a = 42;
            int b = 0;

            // Act
            int result = calculator.add(a, b);

            // Assert
            assertEquals(42, result, "42 + 0 should equal 42");
        }
    }

    // ==========================================================
    // Task 3: Subtraction Tests
    // ==========================================================

    @Nested
    @DisplayName("Subtraction Tests")
    class SubtractionTests {

        @Test
        @DisplayName("Basic subtraction returns correct difference")
        void subtract_basicSubtraction_returnsCorrectDifference() {
            // Arrange
            int a = 10;
            int b = 3;

            // Act
            int result = calculator.subtract(a, b);

            // Assert
            assertEquals(7, result, "10 - 3 should equal 7");
        }

        @Test
        @DisplayName("Subtracting larger number returns negative result")
        void subtract_largerFromSmaller_returnsNegative() {
            // Arrange
            int a = 5;
            int b = 10;

            // Act
            int result = calculator.subtract(a, b);

            // Assert
            assertEquals(-5, result, "5 - 10 should equal -5");
        }

        @Test
        @DisplayName("Subtracting zero returns original number")
        void subtract_zero_returnsOriginalNumber() {
            // Arrange
            int a = 42;
            int b = 0;

            // Act
            int result = calculator.subtract(a, b);

            // Assert
            assertEquals(42, result, "42 - 0 should equal 42");
        }
    }

    // ==========================================================
    // Task 4: Boolean Method Tests
    // ==========================================================

    @Nested
    @DisplayName("isEven Tests")
    class IsEvenTests {

        @Test
        @DisplayName("Even positive numbers return true")
        void isEven_evenPositiveNumbers_returnsTrue() {
            assertTrue(calculator.isEven(2), "2 should be even");
            assertTrue(calculator.isEven(4), "4 should be even");
            assertTrue(calculator.isEven(100), "100 should be even");
        }

        @Test
        @DisplayName("Odd numbers return false")
        void isEven_oddNumbers_returnsFalse() {
            assertFalse(calculator.isEven(1), "1 should be odd");
            assertFalse(calculator.isEven(3), "3 should be odd");
            assertFalse(calculator.isEven(99), "99 should be odd");
        }

        @Test
        @DisplayName("Zero is even")
        void isEven_zero_returnsTrue() {
            assertTrue(calculator.isEven(0), "0 should be even");
        }

        @Test
        @DisplayName("Negative even numbers return true")
        void isEven_negativeEvenNumbers_returnsTrue() {
            assertTrue(calculator.isEven(-2), "-2 should be even");
            assertTrue(calculator.isEven(-100), "-100 should be even");
        }

        @Test
        @DisplayName("Negative odd numbers return false")
        void isEven_negativeOddNumbers_returnsFalse() {
            assertFalse(calculator.isEven(-1), "-1 should be odd");
            assertFalse(calculator.isEven(-99), "-99 should be odd");
        }
    }

    @Nested
    @DisplayName("isPositive Tests")
    class IsPositiveTests {

        @Test
        @DisplayName("Positive numbers return true")
        void isPositive_positiveNumbers_returnsTrue() {
            assertTrue(calculator.isPositive(1), "1 should be positive");
            assertTrue(calculator.isPositive(100), "100 should be positive");
            assertTrue(calculator.isPositive(Integer.MAX_VALUE), "MAX_VALUE should be positive");
        }

        @Test
        @DisplayName("Negative numbers return false")
        void isPositive_negativeNumbers_returnsFalse() {
            assertFalse(calculator.isPositive(-1), "-1 should not be positive");
            assertFalse(calculator.isPositive(-100), "-100 should not be positive");
        }

        @Test
        @DisplayName("Zero is not positive")
        void isPositive_zero_returnsFalse() {
            assertFalse(calculator.isPositive(0), "0 should not be positive");
        }
    }

    // ==========================================================
    // Stretch Goal: Multiplication Tests
    // ==========================================================

    @Nested
    @DisplayName("Multiplication Tests (Stretch Goal)")
    class MultiplicationTests {

        @Test
        @DisplayName("Multiplying two positive numbers returns correct product")
        void multiply_twoPositiveNumbers_returnsProduct() {
            assertEquals(15, calculator.multiply(3, 5), "3 * 5 should equal 15");
        }

        @Test
        @DisplayName("Multiplying by zero returns zero")
        void multiply_byZero_returnsZero() {
            assertEquals(0, calculator.multiply(100, 0), "100 * 0 should equal 0");
            assertEquals(0, calculator.multiply(0, 100), "0 * 100 should equal 0");
        }

        @Test
        @DisplayName("Multiplying negative numbers follows sign rules")
        void multiply_negativeNumbers_followsSignRules() {
            assertEquals(-15, calculator.multiply(3, -5), "3 * -5 should equal -15");
            assertEquals(-15, calculator.multiply(-3, 5), "-3 * 5 should equal -15");
            assertEquals(15, calculator.multiply(-3, -5), "-3 * -5 should equal 15");
        }
    }
}

