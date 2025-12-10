/**
 * Calculator class - Your system under test for this exercise.
 * 
 * Your task is to write comprehensive JUnit5 tests for all methods
 * in this class.
 */
public class Calculator {

    /**
     * Adds two integers.
     * @param a First operand
     * @param b Second operand
     * @return Sum of a and b
     */
    public int add(int a, int b) {
        return a + b;
    }

    /**
     * Subtracts the second integer from the first.
     * @param a First operand
     * @param b Second operand
     * @return Difference (a - b)
     */
    public int subtract(int a, int b) {
        return a - b;
    }

    /**
     * Multiplies two integers.
     * @param a First operand
     * @param b Second operand
     * @return Product of a and b
     */
    public int multiply(int a, int b) {
        return a * b;
    }

    /**
     * Divides the first integer by the second.
     * @param a Dividend
     * @param b Divisor
     * @return Quotient (a / b)
     * @throws ArithmeticException if b is zero
     */
    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return a / b;
    }

    /**
     * Checks if a number is even.
     * @param number The number to check
     * @return true if even, false if odd
     */
    public boolean isEven(int number) {
        return number % 2 == 0;
    }

    /**
     * Checks if a number is positive (greater than zero).
     * @param number The number to check
     * @return true if positive, false otherwise
     */
    public boolean isPositive(int number) {
        return number > 0;
    }
}

