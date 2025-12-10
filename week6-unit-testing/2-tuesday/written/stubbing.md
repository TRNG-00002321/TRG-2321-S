# Stubbing: Defining Mock Behavior in Mockito

## Learning Objectives
- Use `when().thenReturn()` to stub method return values
- Handle exceptions with `thenThrow()`
- Create dynamic responses with `thenAnswer()`
- Stub void methods and consecutive calls

## Why This Matters

Creating a mock is just the first step—you need to define how it behaves. Stubbing tells your mocks what to return when their methods are called. This control is essential for testing different scenarios: success cases, error cases, edge cases, and everything in between.

## The Concept

### Basic Stubbing: when().thenReturn()

```java
@Mock
private UserRepository repository;

@Test
void basicStubbing() {
    // Define what the mock returns
    User john = new User("john@example.com");
    when(repository.findById(1L)).thenReturn(john);
    
    // Now calling the method returns our stubbed value
    User result = repository.findById(1L);
    assertEquals("john@example.com", result.getEmail());
}
```

### Stubbing with Any Argument

Use argument matchers for flexible stubbing:

```java
@Test
void stubbingWithMatchers() {
    User defaultUser = new User("default@example.com");
    
    // Match any Long argument
    when(repository.findById(anyLong())).thenReturn(defaultUser);
    
    // All these return the stubbed user
    assertEquals(defaultUser, repository.findById(1L));
    assertEquals(defaultUser, repository.findById(999L));
    assertEquals(defaultUser, repository.findById(0L));
}
```

Common matchers: `any()`, `anyString()`, `anyInt()`, `anyList()`, `eq(value)`, `isNull()`

### Stubbing Exceptions: thenThrow()

```java
@Test
void stubbingExceptions() {
    // Throw exception when method is called
    when(repository.findById(-1L))
        .thenThrow(new IllegalArgumentException("Invalid ID"));
    
    assertThrows(IllegalArgumentException.class, () -> {
        repository.findById(-1L);
    });
}
```

### Dynamic Responses: thenAnswer()

For complex logic based on input:

```java
@Test
void dynamicStubbing() {
    when(repository.findById(anyLong())).thenAnswer(invocation -> {
        Long id = invocation.getArgument(0);
        if (id <= 0) return null;
        return new User("user" + id + "@example.com");
    });
    
    assertNull(repository.findById(-1L));
    assertEquals("user5@example.com", repository.findById(5L).getEmail());
}
```

### Consecutive Calls

Return different values on subsequent calls:

```java
@Test
void consecutiveCalls() {
    when(repository.count())
        .thenReturn(0L)   // First call
        .thenReturn(1L)   // Second call
        .thenReturn(5L);  // Third and all subsequent calls
    
    assertEquals(0L, repository.count());  // First
    assertEquals(1L, repository.count());  // Second
    assertEquals(5L, repository.count());  // Third
    assertEquals(5L, repository.count());  // Fourth (still 5)
}
```

### Stubbing Void Methods

Void methods use `doX().when()` syntax:

```java
@Test
void stubbingVoidMethods() {
    // Do nothing (default behavior, but explicit)
    doNothing().when(repository).delete(anyLong());
    
    // Throw exception
    doThrow(new RuntimeException("Cannot delete"))
        .when(repository).delete(-1L);
    
    // Custom action
    doAnswer(invocation -> {
        System.out.println("Deleting: " + invocation.getArgument(0));
        return null;
    }).when(repository).delete(anyLong());
}
```

## Code Example

### Complete Stubbing Demonstration

```java
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentGateway gateway;
    
    @InjectMocks
    private PaymentService service;
    
    @Test
    void charge_successful() {
        when(gateway.processPayment(anyDouble()))
            .thenReturn(new PaymentResult(true, "TXN123"));
        
        PaymentResult result = service.charge(99.99);
        
        assertTrue(result.isSuccess());
    }
    
    @Test
    void charge_networkError() {
        when(gateway.processPayment(anyDouble()))
            .thenThrow(new NetworkException("Connection timeout"));
        
        assertThrows(PaymentException.class, () -> {
            service.charge(99.99);
        });
    }
    
    @Test
    void charge_retryLogic() {
        // First call fails, second succeeds
        when(gateway.processPayment(anyDouble()))
            .thenThrow(new NetworkException("Timeout"))
            .thenReturn(new PaymentResult(true, "TXN456"));
        
        // Service should retry and succeed
        PaymentResult result = service.chargeWithRetry(99.99);
        assertTrue(result.isSuccess());
    }
}
```

## Summary

- **when().thenReturn()**: Define what mocks return
- **Argument matchers**: `any()`, `anyString()`, etc. for flexible matching
- **thenThrow()**: Simulate exceptions
- **thenAnswer()**: Dynamic responses based on input
- **Consecutive returns**: Different values for subsequent calls
- **Void methods**: Use `doX().when()` syntax

## Additional Resources

- [Mockito Stubbing Guide](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#stubbing) - Official docs
- [Argument Matchers](https://www.baeldung.com/mockito-argument-matchers) - Matcher patterns
- [Stubbing Void Methods](https://www.baeldung.com/mockito-void-methods) - doX() patterns

