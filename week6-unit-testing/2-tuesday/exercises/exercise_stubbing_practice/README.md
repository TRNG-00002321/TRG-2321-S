# Lab: Stubbing Practice - OrderService

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | stubbing.md, demo_stubbing_patterns.java |

## Learning Objectives
By completing this exercise, you will:
- Master `when().thenReturn()` for various return types
- Use `thenThrow()` to simulate errors
- Use `thenAnswer()` for dynamic responses
- Stub consecutive calls with different results
- Stub void methods with `doNothing()` and `doThrow()`

## The Scenario

You're testing an `OrderService` that processes customer orders. The service depends on:
- `ProductRepository` - Fetches product details
- `InventoryService` - Checks and updates stock
- `PaymentGateway` - Processes payments
- `NotificationService` - Sends order confirmations (void method)

Your task is to stub all these dependencies to test various scenarios.

## Core Tasks

### Task 1: Basic Stubbing with thenReturn (10 minutes)

Stub the `ProductRepository` to return specific products:

```java
@Test
void calculateOrderTotal_multipleProducts_returnsCorrectSum() {
    // Arrange: Stub product lookups
    Product laptop = new Product("LAPTOP", "MacBook Pro", new BigDecimal("1999.99"));
    Product mouse = new Product("MOUSE", "Magic Mouse", new BigDecimal("79.99"));
    
    when(productRepository.findById("LAPTOP")).thenReturn(Optional.of(laptop));
    when(productRepository.findById("MOUSE")).thenReturn(Optional.of(mouse));
    
    // Act
    BigDecimal total = orderService.calculateTotal(List.of("LAPTOP", "MOUSE"));
    
    // Assert
    assertEquals(new BigDecimal("2079.98"), total);
}
```

**Your Task:** Write similar tests for:
- Order with single product
- Order with non-existent product (return `Optional.empty()`)
- Order with products having discounts

### Task 2: Stubbing Exceptions with thenThrow (10 minutes)

Test error handling when external services fail:

```java
@Test
void processPayment_gatewayTimeout_throwsPaymentException() {
    // Arrange: Payment gateway times out
    when(paymentGateway.charge(any(), any()))
        .thenThrow(new PaymentTimeoutException("Gateway timeout"));
    
    // Act & Assert
    assertThrows(PaymentException.class, () -> {
        orderService.processPayment(order, paymentDetails);
    });
}
```

**Your Task:** Write tests for:
- Inventory service throws `OutOfStockException`
- Product repository throws `DatabaseException`
- Payment gateway throws `InsufficientFundsException`

### Task 3: Dynamic Stubbing with thenAnswer (15 minutes)

When you need dynamic behavior based on input:

```java
@Test
void createOrder_generatesUniqueOrderId() {
    // Arrange: Generate sequential IDs
    AtomicLong idGenerator = new AtomicLong(1000);
    
    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
        Order order = invocation.getArgument(0);
        order.setId(idGenerator.incrementAndGet());
        return order;
    });
    
    // Act
    Order order1 = orderService.createOrder(items1);
    Order order2 = orderService.createOrder(items2);
    
    // Assert
    assertEquals(1001L, order1.getId());
    assertEquals(1002L, order2.getId());
}
```

**Your Task:** Write tests using `thenAnswer` for:
- Apply tax based on order total
- Calculate shipping based on destination
- Apply dynamic discounts based on customer tier

### Task 4: Consecutive Call Stubbing (10 minutes)

Test retry logic with different responses per call:

```java
@Test
void processPayment_retriesOnFirstFailure_succeedsOnSecond() {
    // Arrange: First call fails, second succeeds
    when(paymentGateway.charge(any(), any()))
        .thenThrow(new PaymentTimeoutException("Timeout"))
        .thenReturn(new PaymentResult("SUCCESS", "TXN123"));
    
    // Act: OrderService has retry logic
    PaymentResult result = orderService.processPaymentWithRetry(order, payment);
    
    // Assert
    assertEquals("SUCCESS", result.getStatus());
}
```

**Your Task:** Write tests for:
- Inventory check fails twice, succeeds third time
- Product not found, then found (cache refresh scenario)
- Multiple failed attempts leading to final exception

### Task 5: Void Method Stubbing (10 minutes)

Stub void methods (they can't use `when().thenReturn()`):

```java
@Test
void completeOrder_sendsNotification_doesNotThrow() {
    // Arrange: Notification service is a void method
    doNothing().when(notificationService).sendOrderConfirmation(any());
    
    // Act & Assert
    assertDoesNotThrow(() -> orderService.completeOrder(order));
}

@Test
void completeOrder_notificationFails_orderStillCompletes() {
    // Arrange: Notification throws but shouldn't break the flow
    doThrow(new NotificationException("Email failed"))
        .when(notificationService).sendOrderConfirmation(any());
    
    // Act: Order should still complete
    Order completed = orderService.completeOrder(order);
    
    // Assert
    assertEquals(OrderStatus.COMPLETED, completed.getStatus());
}
```

**Your Task:** Write tests for:
- `doReturn().when()` alternative syntax
- Void method that throws on specific input

## Starter Code

```java
// Interfaces to mock
public interface ProductRepository {
    Optional<Product> findById(String sku);
    List<Product> findByCategory(String category);
}

public interface InventoryService {
    boolean checkStock(String sku, int quantity);
    void reserveStock(String sku, int quantity);  // void
    void releaseStock(String sku, int quantity);  // void
}

public interface PaymentGateway {
    PaymentResult charge(BigDecimal amount, PaymentDetails details);
    void refund(String transactionId, BigDecimal amount);  // void
}

public interface NotificationService {
    void sendOrderConfirmation(Order order);  // void
    void sendShippingUpdate(Order order, String status);  // void
}
```

## Stubbing Cheat Sheet

| Scenario | Syntax |
|----------|--------|
| Return value | `when(mock.method()).thenReturn(value)` |
| Throw exception | `when(mock.method()).thenThrow(exception)` |
| Dynamic return | `when(mock.method()).thenAnswer(inv -> ...)` |
| Consecutive calls | `when(mock.method()).thenReturn(a).thenReturn(b)` |
| Void - do nothing | `doNothing().when(mock).voidMethod()` |
| Void - throw | `doThrow(ex).when(mock).voidMethod()` |
| Alternative syntax | `doReturn(value).when(mock).method()` |

## Definition of Done

- [ ] At least 3 tests using `thenReturn()` with different types
- [ ] At least 2 tests using `thenThrow()`
- [ ] At least 2 tests using `thenAnswer()`
- [ ] At least 1 test with consecutive call stubbing
- [ ] At least 2 tests stubbing void methods
- [ ] Tests cover success and failure scenarios
- [ ] All tests pass

## Submission

Commit with message:
```
feat(week6): Complete stubbing practice exercise
```

