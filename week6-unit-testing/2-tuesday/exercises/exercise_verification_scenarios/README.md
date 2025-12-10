# Lab: Verification Scenarios - Prove Your Code Calls Dependencies

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | verifying-method-calls.md, demo_verification_techniques.java |

## Learning Objectives
By completing this exercise, you will:
- Use `verify()` to ensure methods were called
- Apply verification modes: `times()`, `never()`, `atLeast()`, `atMost()`
- Use `ArgumentCaptor` to inspect passed arguments
- Verify call order with `InOrder`
- Use `verifyNoMoreInteractions()` for strictness

## The Scenario

You're testing a `NotificationService` that sends various types of notifications. Just stubbing isn't enough—you need to **verify** that the right methods are called with the right arguments in the right order.

## Core Tasks

### Task 1: Basic Verification (10 minutes)

Verify that a method was called:

```java
@Test
void sendWelcomeEmail_newUser_sendsExactlyOneEmail() {
    // Arrange
    User user = new User("John", "john@test.com");
    
    // Act
    notificationService.sendWelcome(user);
    
    // Assert: Verify the email was sent
    verify(emailClient).send(eq("john@test.com"), anyString(), anyString());
}
```

**Your Task:** Write verification tests for:
- `sendPasswordReset()` calls email client once
- `deleteUser()` calls logger with user details
- `createOrder()` calls inventory service

### Task 2: Verification Modes (15 minutes)

Test how many times methods are called:

```java
@Test
void bulkNotification_multipleUsers_sendsEmailToEach() {
    // Arrange
    List<User> users = List.of(
        new User("Alice", "alice@test.com"),
        new User("Bob", "bob@test.com"),
        new User("Charlie", "charlie@test.com")
    );
    
    // Act
    notificationService.sendBulkNotification(users, "News Update");
    
    // Assert: Verify exact count
    verify(emailClient, times(3)).send(anyString(), anyString(), anyString());
}

@Test
void sendNotification_invalidEmail_neverSendsEmail() {
    // Arrange
    User user = new User("John", "invalid-email");  // No @ sign
    
    // Act
    notificationService.sendWelcome(user);
    
    // Assert: Should never send to invalid email
    verify(emailClient, never()).send(anyString(), anyString(), anyString());
}
```

**Your Task:** Write tests using:
- `times(n)` - Exact count
- `never()` - Should not call
- `atLeast(n)` - Minimum calls
- `atMost(n)` - Maximum calls
- `atLeastOnce()` - At least one call

### Task 3: Argument Captors (15 minutes)

Capture and inspect arguments passed to mocked methods:

```java
@Test
void sendWelcome_newUser_sendsPersonalizedMessage() {
    // Arrange
    ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
    
    User user = new User("John", "john@test.com");
    
    // Act
    notificationService.sendWelcome(user);
    
    // Capture the arguments
    verify(emailClient).send(
        eq("john@test.com"),
        subjectCaptor.capture(),
        bodyCaptor.capture()
    );
    
    // Assert on captured values
    assertTrue(subjectCaptor.getValue().contains("Welcome"));
    assertTrue(bodyCaptor.getValue().contains("John"));
}

@Test
void createUser_capturesUserWithCorrectProperties() {
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    
    // Act
    userService.createUser("Jane", "jane@test.com");
    
    // Capture
    verify(repository).save(userCaptor.capture());
    
    // Assert on captured object
    User captured = userCaptor.getValue();
    assertEquals("Jane", captured.getName());
    assertEquals("jane@test.com", captured.getEmail());
    assertTrue(captured.isActive());
}
```

**Your Task:** Write tests that capture and verify:
- Order details passed to payment gateway
- Log message format
- Multiple captured values from bulk operations

### Task 4: InOrder Verification (10 minutes)

Verify methods are called in the correct order:

```java
@Test
void processOrder_validOrder_callsServicesInCorrectOrder() {
    // Arrange
    InOrder inOrder = inOrder(inventoryService, paymentGateway, notificationService);
    
    Order order = createTestOrder();
    
    // Act
    orderService.processOrder(order);
    
    // Assert: Verify order of operations
    inOrder.verify(inventoryService).reserveStock(anyString(), anyInt());
    inOrder.verify(paymentGateway).charge(any(), any());
    inOrder.verify(notificationService).sendOrderConfirmation(any());
}

@Test
void cancelOrder_callsRefundBeforeReleasingStock() {
    InOrder inOrder = inOrder(paymentGateway, inventoryService);
    
    Order order = createPaidOrder();
    
    // Act
    orderService.cancelOrder(order);
    
    // Assert: Refund should happen before stock release
    inOrder.verify(paymentGateway).refund(anyString(), any());
    inOrder.verify(inventoryService).releaseStock(anyString(), anyInt());
}
```

**Your Task:** Write tests that verify order of:
- User registration flow (validate → save → notify)
- Payment flow (authorize → capture → receipt)

### Task 5: Strict Verification (5 minutes)

Ensure no unexpected interactions:

```java
@Test
void getUser_existingUser_onlyCallsRepository() {
    // Arrange
    when(repository.findById(1L)).thenReturn(Optional.of(testUser));
    
    // Act
    userService.getUser(1L);
    
    // Assert: Only repository was called, nothing else
    verify(repository).findById(1L);
    verifyNoMoreInteractions(repository);
    verifyNoInteractions(emailClient);  // Email should not be involved
}
```

## Verification Cheat Sheet

| Verification | Purpose |
|--------------|---------|
| `verify(mock).method()` | Called exactly once |
| `verify(mock, times(n)).method()` | Called exactly n times |
| `verify(mock, never()).method()` | Never called |
| `verify(mock, atLeast(n)).method()` | Called at least n times |
| `verify(mock, atMost(n)).method()` | Called at most n times |
| `verifyNoInteractions(mock)` | No methods called |
| `verifyNoMoreInteractions(mock)` | No other methods called |
| `inOrder(m1, m2).verify(...)` | Verify call order |

## Definition of Done

- [ ] At least 2 tests with basic `verify()`
- [ ] Tests using each verification mode: `times`, `never`, `atLeast`, `atMost`
- [ ] At least 2 tests using `ArgumentCaptor`
- [ ] At least 1 test using `InOrder`
- [ ] At least 1 test with `verifyNoMoreInteractions`
- [ ] Tests cover real business scenarios
- [ ] All tests pass

## Submission

Commit with message:
```
feat(week6): Complete verification scenarios exercise
```

