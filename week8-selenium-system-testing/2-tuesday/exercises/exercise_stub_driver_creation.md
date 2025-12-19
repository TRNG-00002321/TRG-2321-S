# Exercise 4: Stub and Driver Creation

## Objective

Implement stubs and drivers for isolated integration testing of provided components. This is a hybrid exercise combining conceptual design with code implementation.

## Learning Goals

- Create functional stubs that simulate component behavior
- Build drivers that exercise component interfaces
- Understand how isolation enables targeted testing
- Practice both Python and Java stub/driver implementation

## Time Estimate

45 minutes

---

## The Component: Payment Processor

### Interface Definition

We need to test a `PaymentProcessor` component in isolation. Here's its interface:

**Python Interface:**
```python
class PaymentProcessor:
    def process_payment(self, order_id: str, amount: float, card_token: str) -> PaymentResult:
        """
        Process a payment for an order.
        
        Args:
            order_id: Unique order identifier
            amount: Payment amount in dollars
            card_token: Tokenized card from payment provider
            
        Returns:
            PaymentResult with status, transaction_id, and message
        """
        pass
    
    def refund_payment(self, transaction_id: str, amount: float) -> RefundResult:
        """
        Refund a previously processed payment.
        
        Args:
            transaction_id: Original transaction ID
            amount: Refund amount (partial refunds allowed)
            
        Returns:
            RefundResult with status and refund_id
        """
        pass

@dataclass
class PaymentResult:
    status: str  # "success", "declined", "error"
    transaction_id: str
    message: str

@dataclass
class RefundResult:
    status: str  # "success", "error"
    refund_id: str
    message: str
```

**Java Interface:**
```java
public interface PaymentProcessor {
    PaymentResult processPayment(String orderId, double amount, String cardToken);
    RefundResult refundPayment(String transactionId, double amount);
}

public record PaymentResult(String status, String transactionId, String message) {}
public record RefundResult(String status, String refundId, String message) {}
```

---

## Core Tasks

### Task 1: Design Stub Behavior (10 minutes)

Define the behavior matrix for the `StripeGatewayStub`:

```markdown
# Stripe Gateway Stub Behavior Matrix

| Input Card Token | Response Status | Transaction ID | Message |
|-----------------|-----------------|----------------|---------|
| `tok_visa` | success | txn_[random] | Payment approved |
| `tok_chargeDeclined` | declined | null | Card was declined |
| `tok_insufficient` | declined | null | Insufficient funds |
| `tok_expired` | declined | null | Card has expired |
| `tok_error` | error | null | Processing error |
| `tok_timeout` | | | (simulate timeout) |
| Any other | | | |

# Refund Stub Behavior Matrix

| Input Transaction ID | Amount | Response Status | Message |
|---------------------|--------|-----------------|---------|
| `txn_valid_*` | <= original | success | Refund processed |
| `txn_valid_*` | > original | error | Amount exceeds original |
| `txn_invalid_*` | any | error | Transaction not found |
| `txn_already_refunded` | any | | |
```

### Task 2: Implement Python Stubs (15 minutes)

Create `stubs/stripe_gateway_stub.py`:

```python
"""
Stripe Gateway Stub for Integration Testing

This stub simulates Stripe API responses without making actual API calls.
"""

from dataclasses import dataclass
from typing import Dict, Optional
import random
import string
import time


@dataclass
class StripeChargeResponse:
    success: bool
    charge_id: Optional[str]
    error_message: Optional[str]


@dataclass
class StripeRefundResponse:
    success: bool
    refund_id: Optional[str]
    error_message: Optional[str]


class StripeGatewayStub:
    """
    Stub implementation of Stripe payment gateway.
    
    Provides predictable responses based on card tokens for testing.
    """
    
    def __init__(self):
        # Track processed transactions for refund testing
        self._transactions: Dict[str, float] = {}
        self._refunded: set = set()
        
        # Configurable behaviors
        self.simulate_latency = False
        self.latency_ms = 100
    
    def _generate_id(self, prefix: str) -> str:
        """Generate a random ID with prefix."""
        suffix = ''.join(random.choices(string.ascii_lowercase + string.digits, k=12))
        return f"{prefix}_{suffix}"
    
    def charge(self, amount: float, card_token: str, description: str = "") -> StripeChargeResponse:
        """
        Simulate charging a card.
        
        Args:
            amount: Amount to charge in dollars
            card_token: Test card token determining response
            description: Optional charge description
            
        Returns:
            StripeChargeResponse with success status
        """
        # TODO: Implement stub logic based on card_token
        
        # Simulate latency if configured
        if self.simulate_latency:
            time.sleep(self.latency_ms / 1000)
        
        # Handle special test tokens
        if card_token == "tok_visa":
            # TODO: Return successful response
            # Generate transaction ID, store in self._transactions
            pass
        
        elif card_token == "tok_chargeDeclined":
            # TODO: Return declined response
            pass
        
        elif card_token == "tok_insufficient":
            # TODO: Return insufficient funds response
            pass
        
        elif card_token == "tok_expired":
            # TODO: Return expired card response
            pass
        
        elif card_token == "tok_error":
            # TODO: Return processing error response
            pass
        
        elif card_token == "tok_timeout":
            # TODO: Simulate timeout (sleep for 30 seconds or raise exception)
            pass
        
        else:
            # Default: treat as valid card
            pass
        
        # YOUR IMPLEMENTATION HERE
        return StripeChargeResponse(success=False, charge_id=None, error_message="Not implemented")
    
    def refund(self, charge_id: str, amount: float) -> StripeRefundResponse:
        """
        Simulate refunding a charge.
        
        Args:
            charge_id: ID of the original charge
            amount: Amount to refund
            
        Returns:
            StripeRefundResponse with success status
        """
        # TODO: Implement refund stub logic
        
        # Check if transaction exists
        # Check if amount is valid
        # Check if already refunded
        
        # YOUR IMPLEMENTATION HERE
        return StripeRefundResponse(success=False, refund_id=None, error_message="Not implemented")
    
    # Helper methods for testing
    def reset(self):
        """Reset stub state between tests."""
        self._transactions.clear()
        self._refunded.clear()
    
    def get_transaction_count(self) -> int:
        """Get number of processed transactions."""
        return len(self._transactions)


# Example usage and test
if __name__ == "__main__":
    stub = StripeGatewayStub()
    
    # Test successful charge
    result = stub.charge(50.00, "tok_visa", "Order #123")
    print(f"Charge result: {result}")
    
    # Test declined card
    result = stub.charge(50.00, "tok_chargeDeclined", "Order #124")
    print(f"Declined result: {result}")
```

### Task 3: Implement Driver (10 minutes)

Create `drivers/payment_processor_driver.py`:

```python
"""
Payment Processor Driver for Integration Testing

This driver simulates the Order Service calling the Payment Processor.
"""

from dataclasses import dataclass
from typing import List, Callable
import json


@dataclass
class TestCase:
    name: str
    order_id: str
    amount: float
    card_token: str
    expected_status: str


class PaymentProcessorDriver:
    """
    Driver to exercise the Payment Processor component.
    
    Simulates how the Order Service would call the Payment Processor.
    """
    
    def __init__(self, payment_processor):
        """
        Initialize driver with the component under test.
        
        Args:
            payment_processor: PaymentProcessor instance to test
        """
        self.processor = payment_processor
        self.results: List[dict] = []
    
    def run_test_case(self, test_case: TestCase) -> dict:
        """
        Execute a single test case.
        
        Args:
            test_case: TestCase definition
            
        Returns:
            dict with test name, expected, actual, and pass/fail status
        """
        # TODO: Implement test execution
        # 1. Call processor.process_payment with test case data
        # 2. Compare result status with expected_status
        # 3. Record result
        
        # YOUR IMPLEMENTATION HERE
        pass
    
    def run_test_suite(self, test_cases: List[TestCase]) -> dict:
        """
        Run a suite of test cases.
        
        Args:
            test_cases: List of test cases to execute
            
        Returns:
            Summary with total, passed, failed counts
        """
        # TODO: Implement test suite execution
        pass
    
    def generate_report(self) -> str:
        """Generate a formatted test report."""
        # TODO: Implement report generation
        pass


# Standard test suite
STANDARD_TEST_CASES = [
    TestCase(
        name="Successful payment with Visa",
        order_id="ORD-001",
        amount=99.99,
        card_token="tok_visa",
        expected_status="success"
    ),
    TestCase(
        name="Declined card",
        order_id="ORD-002",
        amount=50.00,
        card_token="tok_chargeDeclined",
        expected_status="declined"
    ),
    TestCase(
        name="Insufficient funds",
        order_id="ORD-003",
        amount=1000.00,
        card_token="tok_insufficient",
        expected_status="declined"
    ),
    TestCase(
        name="Zero amount payment",
        order_id="ORD-004",
        amount=0.00,
        card_token="tok_visa",
        expected_status="error"
    ),
    TestCase(
        name="Negative amount payment",
        order_id="ORD-005",
        amount=-10.00,
        card_token="tok_visa",
        expected_status="error"
    ),
]


if __name__ == "__main__":
    # Example: How to use the driver
    # payment_processor = PaymentProcessor(stripe_gateway)
    # driver = PaymentProcessorDriver(payment_processor)
    # results = driver.run_test_suite(STANDARD_TEST_CASES)
    # print(driver.generate_report())
    pass
```

### Task 4: Java Implementation (10 minutes)

Create Java versions of the stub and driver:

**stubs/StripeGatewayStub.java:**
```java
package com.example.testing.stubs;

import java.util.*;

/**
 * Stripe Gateway Stub for Integration Testing
 * 
 * TODO: Implement the stub based on the Python version
 */
public class StripeGatewayStub {
    
    private Map<String, Double> transactions = new HashMap<>();
    private Set<String> refundedTransactions = new HashSet<>();
    
    public record ChargeResponse(boolean success, String chargeId, String errorMessage) {}
    public record RefundResponse(boolean success, String refundId, String errorMessage) {}
    
    public ChargeResponse charge(double amount, String cardToken, String description) {
        // TODO: Implement based on card token
        
        return switch (cardToken) {
            case "tok_visa" -> {
                // Generate ID and record transaction
                String chargeId = "ch_" + UUID.randomUUID().toString().substring(0, 12);
                transactions.put(chargeId, amount);
                yield new ChargeResponse(true, chargeId, null);
            }
            case "tok_chargeDeclined" -> {
                // YOUR CODE HERE
                yield new ChargeResponse(false, null, "Card was declined");
            }
            // Add remaining cases
            default -> new ChargeResponse(true, "ch_default", null);
        };
    }
    
    public RefundResponse refund(String chargeId, double amount) {
        // TODO: Implement refund logic
        // YOUR CODE HERE
        return new RefundResponse(false, null, "Not implemented");
    }
    
    public void reset() {
        transactions.clear();
        refundedTransactions.clear();
    }
}
```

---

## Definition of Done

- [ ] Stub behavior matrix completed for all scenarios
- [ ] Python `StripeGatewayStub` fully implemented
- [ ] Stub handles all card tokens correctly
- [ ] Python driver executes test cases and reports results
- [ ] Java stub skeleton completed
- [ ] Stub tracks transaction state for refund testing
- [ ] Reset functionality works for test isolation

---

## Hints

<details>
<summary>Hint: Generating Random IDs</summary>

```python
import random
import string

def generate_id(prefix: str) -> str:
    suffix = ''.join(random.choices(string.ascii_lowercase + string.digits, k=12))
    return f"{prefix}_{suffix}"

# Usage: generate_id("txn")  -> "txn_a1b2c3d4e5f6"
```
</details>

<details>
<summary>Hint: Simulating Timeout</summary>

```python
import time

def simulate_timeout():
    # Option 1: Sleep (blocks)
    time.sleep(30)
    
    # Option 2: Raise exception
    raise TimeoutError("Connection timed out")
```
</details>

