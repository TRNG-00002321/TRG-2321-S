# Verifying Method Calls: Confirming Mock Interactions

## Learning Objectives
- Use `verify()` to confirm methods were called
- Apply verification modes: `times()`, `never()`, `atLeast()`, `atMost()`
- Capture arguments with `ArgumentCaptor`
- Verify call order with `InOrder`

## Why This Matters

Stubbing tells mocks what to return, but sometimes you need to verify that methods were actually called—and with the right arguments. Verification is essential when testing side effects: Did the service save the user? Did it send the notification? Did it log the event?

## The Concept

### Basic Verification: verify()

```java
@Mock
private EmailClient emailClient;

@Test
void sendWelcome_callsEmailClient() {
    // Act
    service.sendWelcomeEmail("john@example.com");
    
    // Verify the mock was called
    verify(emailClient).send("john@example.com", anyString());
}
```

### Verification Modes

```java
@Test
void verificationModes() {
    service.processOrder(order);
    
    // Called exactly once (default)
    verify(repository).save(any());
    verify(repository, times(1)).save(any());
    
    // Never called
    verify(emailClient, never()).sendError(any());
    
    // Called at least N times
    verify(logger, atLeast(2)).log(anyString());
    
    // Called at most N times
    verify(cache, atMost(3)).get(anyString());
    
    // Called any number of times (including 0)
    verify(metrics, atLeastOnce()).record(any());
}
```

### Capturing Arguments: ArgumentCaptor

When you need to inspect what was passed to a mock:

```java
@Captor
private ArgumentCaptor<User> userCaptor;

@Test
void createUser_savesCorrectUser() {
    service.createUser("John", "john@example.com");
    
    // Capture what was passed to save()
    verify(repository).save(userCaptor.capture());
    
    // Inspect the captured value
    User savedUser = userCaptor.getValue();
    assertEquals("John", savedUser.getName());
    assertEquals("john@example.com", savedUser.getEmail());
    assertNotNull(savedUser.getCreatedAt());
}
```

For multiple invocations:
```java
@Test
void captureMultipleCalls() {
    service.createUsers(List.of("John", "Jane"));
    
    verify(repository, times(2)).save(userCaptor.capture());
    
    List<User> savedUsers = userCaptor.getAllValues();
    assertEquals(2, savedUsers.size());
    assertEquals("John", savedUsers.get(0).getName());
    assertEquals("Jane", savedUsers.get(1).getName());
}
```

### Verifying Call Order: InOrder

```java
@Test
void processPayment_callsInCorrectOrder() {
    service.processPayment(order);
    
    // Create InOrder verifier
    InOrder inOrder = inOrder(validator, gateway, repository);
    
    // Verify order of calls
    inOrder.verify(validator).validate(order);
    inOrder.verify(gateway).charge(order.getAmount());
    inOrder.verify(repository).save(any());
}
```

### Verification with Timeout

For async operations:
```java
@Test
void asyncOperation_completesWithinTimeout() {
    service.processAsync(data);
    
    // Wait up to 1 second for the call to happen
    verify(handler, timeout(1000)).handleResult(any());
}
```

### No More Interactions

Ensure no unexpected calls were made:
```java
@Test
void strictVerification() {
    service.process(data);
    
    verify(repository).save(any());
    verifyNoMoreInteractions(repository);  // Fails if other methods called
}
```

## Code Example

### Complete Verification Demonstration

```java
@ExtendWith(MockitoExtension.class)
class OrderProcessorTest {

    @Mock private OrderValidator validator;
    @Mock private PaymentGateway gateway;
    @Mock private OrderRepository repository;
    @Mock private NotificationService notifications;
    
    @Captor private ArgumentCaptor<Order> orderCaptor;
    
    @InjectMocks private OrderProcessor processor;
    
    @Test
    void processOrder_validOrder_savesAndNotifies() {
        Order order = new Order("ORD-123", 99.99);
        when(validator.isValid(any())).thenReturn(true);
        when(gateway.charge(anyDouble())).thenReturn(true);
        
        processor.process(order);
        
        // Verify correct order of operations
        InOrder inOrder = inOrder(validator, gateway, repository, notifications);
        inOrder.verify(validator).isValid(order);
        inOrder.verify(gateway).charge(99.99);
        inOrder.verify(repository).save(orderCaptor.capture());
        inOrder.verify(notifications).sendConfirmation(any());
        
        // Verify saved order has correct status
        Order savedOrder = orderCaptor.getValue();
        assertEquals(OrderStatus.COMPLETED, savedOrder.getStatus());
    }
    
    @Test
    void processOrder_invalidOrder_doesNotCharge() {
        Order order = new Order("ORD-456", 50.00);
        when(validator.isValid(any())).thenReturn(false);
        
        processor.process(order);
        
        verify(validator).isValid(order);
        verify(gateway, never()).charge(anyDouble());
        verify(repository, never()).save(any());
    }
}
```

## Summary

- **verify()**: Confirm mock methods were called
- **Verification modes**: `times()`, `never()`, `atLeast()`, `atMost()`
- **ArgumentCaptor**: Capture and inspect method arguments
- **InOrder**: Verify specific call sequence
- **timeout()**: Verify async operations within time limit
- **verifyNoMoreInteractions()**: Ensure no unexpected calls

## Additional Resources

- [Mockito Verification](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#verification) - Official docs
- [ArgumentCaptor Guide](https://www.baeldung.com/mockito-argumentcaptor) - Capture patterns
- [Verification Modes](https://www.baeldung.com/mockito-verify) - All verification options

