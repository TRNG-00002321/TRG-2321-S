# Lab: Service Layer Testing - PaymentService

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Advanced |
| **Time Estimate** | 60-90 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | All Tuesday content and demos |

## Learning Objectives
By completing this exercise, you will:
- Achieve complete unit test coverage for a service class
- Apply all Mockito techniques learned today
- Handle complex dependencies and interactions
- Test edge cases and error scenarios
- Write production-quality unit tests

## The Scenario

The `PaymentService` class handles payment processing for the e-commerce platform. It's critical, complex, and has multiple dependencies. Your task is to achieve **100% line coverage** with meaningful tests.

## The System Under Test

```java
public class PaymentService {
    
    private final PaymentGateway paymentGateway;
    private final OrderRepository orderRepository;
    private final TransactionLogger transactionLogger;
    private final FraudDetectionService fraudService;
    private final RetryConfig retryConfig;
    
    public PaymentService(PaymentGateway gateway, OrderRepository orderRepo,
                          TransactionLogger logger, FraudDetectionService fraudService,
                          RetryConfig retryConfig) {
        this.paymentGateway = gateway;
        this.orderRepository = orderRepo;
        this.transactionLogger = logger;
        this.fraudService = fraudService;
        this.retryConfig = retryConfig;
    }
    
    /**
     * Process payment for an order.
     * 
     * Flow:
     * 1. Validate order exists and is pending
     * 2. Run fraud check
     * 3. Charge payment gateway
     * 4. Update order status
     * 5. Log transaction
     * 6. Return result
     */
    public PaymentResult processPayment(Long orderId, PaymentDetails paymentDetails) {
        // 1. Validate order
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
                "Cannot process payment for order in state: " + order.getStatus());
        }
        
        // 2. Fraud check
        FraudCheckResult fraudResult = fraudService.checkTransaction(
            paymentDetails.getCardNumber(), 
            order.getTotal()
        );
        
        if (fraudResult.isRejected()) {
            order.setStatus(OrderStatus.FRAUD_SUSPECTED);
            orderRepository.save(order);
            transactionLogger.logRejected(orderId, "Fraud detected: " + fraudResult.getReason());
            throw new FraudDetectedException(fraudResult.getReason());
        }
        
        // 3. Process payment with retry
        PaymentResult result = processWithRetry(order.getTotal(), paymentDetails);
        
        // 4. Update order status
        if (result.isSuccessful()) {
            order.setStatus(OrderStatus.PAID);
            order.setTransactionId(result.getTransactionId());
        } else {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
        }
        orderRepository.save(order);
        
        // 5. Log transaction
        transactionLogger.log(orderId, result);
        
        return result;
    }
    
    private PaymentResult processWithRetry(BigDecimal amount, PaymentDetails details) {
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < retryConfig.getMaxAttempts()) {
            try {
                return paymentGateway.charge(amount, details);
            } catch (PaymentTimeoutException e) {
                lastException = e;
                attempts++;
                if (attempts < retryConfig.getMaxAttempts()) {
                    sleep(retryConfig.getRetryDelayMs());
                }
            }
        }
        
        throw new PaymentProcessingException(
            "Payment failed after " + attempts + " attempts", lastException);
    }
    
    /**
     * Refund a previously processed payment.
     */
    public RefundResult refundPayment(Long orderId, BigDecimal amount, String reason) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        
        if (order.getTransactionId() == null) {
            throw new InvalidOperationException("Order has no payment to refund");
        }
        
        if (amount.compareTo(order.getTotal()) > 0) {
            throw new InvalidOperationException("Refund amount exceeds order total");
        }
        
        RefundResult result = paymentGateway.refund(order.getTransactionId(), amount);
        
        if (result.isSuccessful()) {
            if (amount.equals(order.getTotal())) {
                order.setStatus(OrderStatus.REFUNDED);
            } else {
                order.setStatus(OrderStatus.PARTIALLY_REFUNDED);
            }
            orderRepository.save(order);
        }
        
        transactionLogger.logRefund(orderId, amount, reason, result);
        
        return result;
    }
    
    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

## Core Tasks

### Task 1: Set Up Test Class (10 minutes)

Create `PaymentServiceTest.java` with all mocks:

```java
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    
    @Mock private PaymentGateway paymentGateway;
    @Mock private OrderRepository orderRepository;
    @Mock private TransactionLogger transactionLogger;
    @Mock private FraudDetectionService fraudService;
    @Mock private RetryConfig retryConfig;
    
    @InjectMocks private PaymentService paymentService;
    
    private Order testOrder;
    private PaymentDetails testPaymentDetails;
    
    @BeforeEach
    void setUp() {
        testOrder = createTestOrder();
        testPaymentDetails = createTestPaymentDetails();
        
        // Default retry config
        when(retryConfig.getMaxAttempts()).thenReturn(3);
        when(retryConfig.getRetryDelayMs()).thenReturn(0L); // No delay in tests!
    }
}
```

### Task 2: Test Happy Path (15 minutes)

Write tests for successful payment processing:

- [ ] `processPayment_validOrder_chargesGateway`
- [ ] `processPayment_successful_updatesOrderStatus`
- [ ] `processPayment_successful_logsTransaction`
- [ ] `processPayment_successful_returnsTransactionId`

### Task 3: Test Validation Errors (15 minutes)

- [ ] `processPayment_orderNotFound_throwsException`
- [ ] `processPayment_orderAlreadyPaid_throwsException`
- [ ] `processPayment_orderCancelled_throwsException`

### Task 4: Test Fraud Detection (15 minutes)

- [ ] `processPayment_fraudDetected_rejectsPayment`
- [ ] `processPayment_fraudDetected_updatesOrderStatus`
- [ ] `processPayment_fraudDetected_logsRejection`
- [ ] `processPayment_fraudCheckPasses_proceedsToPayment`

### Task 5: Test Retry Logic (20 minutes)

- [ ] `processPayment_firstAttemptSucceeds_noRetry`
- [ ] `processPayment_timeoutThenSuccess_retries`
- [ ] `processPayment_allAttemptsFail_throwsException`
- [ ] `processPayment_maxRetries_attemptsCorrectNumber`

Use consecutive call stubbing:
```java
when(paymentGateway.charge(any(), any()))
    .thenThrow(new PaymentTimeoutException("Timeout"))
    .thenThrow(new PaymentTimeoutException("Timeout"))
    .thenReturn(successResult);
```

### Task 6: Test Refund Processing (15 minutes)

- [ ] `refundPayment_fullRefund_updatesStatusToRefunded`
- [ ] `refundPayment_partialRefund_updatesStatusToPartiallyRefunded`
- [ ] `refundPayment_noTransaction_throwsException`
- [ ] `refundPayment_exceedsTotal_throwsException`
- [ ] `refundPayment_successful_logsRefund`

### Task 7: Verify Interactions (10 minutes)

Use verification to ensure:
- Fraud check happens BEFORE payment
- Order is saved with correct status
- Logger is called with correct arguments

## Definition of Done

- [ ] Test class properly set up with all mocks
- [ ] At least 4 happy path tests
- [ ] At least 3 validation error tests
- [ ] At least 4 fraud detection tests
- [ ] At least 4 retry logic tests
- [ ] At least 5 refund tests
- [ ] At least 2 tests with `ArgumentCaptor`
- [ ] At least 1 test with `InOrder` verification
- [ ] All tests have meaningful names and assertions
- [ ] Total: 20+ tests covering all branches
- [ ] All tests pass

## Helper Methods

Add these to your test class:

```java
private Order createTestOrder() {
    Order order = new Order();
    order.setId(1L);
    order.setTotal(new BigDecimal("99.99"));
    order.setStatus(OrderStatus.PENDING);
    return order;
}

private PaymentDetails createTestPaymentDetails() {
    return new PaymentDetails("4111111111111111", "12/25", "123");
}

private PaymentResult createSuccessResult() {
    return new PaymentResult(true, "TXN123", null);
}

private PaymentResult createFailureResult(String reason) {
    return new PaymentResult(false, null, reason);
}

private FraudCheckResult createCleanResult() {
    return new FraudCheckResult(false, null);
}

private FraudCheckResult createFraudResult(String reason) {
    return new FraudCheckResult(true, reason);
}
```

## Stretch Goals

1. Add `@Nested` classes to group related tests
2. Use `@ParameterizedTest` for multiple order states
3. Calculate and verify code coverage with JaCoCo

## Submission

Commit with message:
```
feat(week6): Complete PaymentService testing exercise
```

