# Mockito: The Mocking Framework for Java

## Learning Objectives
- Understand why mocking is essential for unit testing
- Set up Mockito in your project with proper dependencies
- Recognize when to use mocks vs real objects
- Compare Mockito with other mocking frameworks

## Why This Matters

As we continue **"Building Confidence Through Comprehensive Test Coverage,"** we encounter a challenge: real-world code has dependencies. A `UserService` talks to a `UserRepository`. An `OrderProcessor` calls a `PaymentGateway`. Testing these classes in isolation requires controlling their dependencies—and that's where Mockito comes in.

Mockito lets you create fake objects (mocks) that simulate real dependencies, giving you complete control over their behavior during tests.

## The Concept

### Why Mocking Matters

Consider testing a service that sends emails:

```java
public class NotificationService {
    private final EmailClient emailClient;
    
    public void notifyUser(User user, String message) {
        emailClient.send(user.getEmail(), message);
    }
}
```

**Problems without mocking:**
- You'd send real emails during tests
- Tests depend on email server availability
- Slow: network calls take time
- Side effects: real notifications sent

**With mocking:**
- No real emails sent
- No external dependencies
- Fast: no network calls
- Verifiable: confirm email would be sent

### What is Mockito?

Mockito is Java's most popular mocking framework. It lets you:
1. **Create mock objects** that simulate real dependencies
2. **Define behavior** (stubbing): what methods return
3. **Verify interactions**: confirm methods were called

### Setting Up Mockito

**Maven:**
```xml
<dependencies>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.8.0</version>
        <scope>test</scope>
    </dependency>
    <!-- For JUnit5 integration -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>5.8.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Gradle:**
```groovy
dependencies {
    testImplementation 'org.mockito:mockito-core:5.8.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.8.0'
}
```

### Basic Mockito Usage

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;
    
    @Test
    void getUser_existingUser_returnsUser() {
        // Arrange: Define mock behavior
        User mockUser = new User("john@example.com");
        when(repository.findById(1L)).thenReturn(mockUser);
        
        // Create service with mock
        UserService service = new UserService(repository);
        
        // Act
        User result = service.getUser(1L);
        
        // Assert
        assertEquals("john@example.com", result.getEmail());
        
        // Verify: Confirm mock was used correctly
        verify(repository).findById(1L);
    }
}
```

### Dependency Injection for Testability

Mockito works best with dependency injection:

```java
// ❌ Hard to test - creates its own dependency
public class OrderService {
    private PaymentGateway gateway = new PaymentGateway();
}

// ✅ Testable - dependency is injected
public class OrderService {
    private final PaymentGateway gateway;
    
    public OrderService(PaymentGateway gateway) {
        this.gateway = gateway;
    }
}
```

### Mock vs Spy vs Real Object

| Type | Description | Use When |
|------|-------------|----------|
| **Mock** | Completely fake object | Testing in isolation |
| **Spy** | Real object with selective stubbing | Need mostly real behavior |
| **Real** | Actual implementation | Integration testing |

```java
// Mock: All methods return default values unless stubbed
List<String> mockList = mock(List.class);
mockList.add("item");  // Does nothing, returns false

// Spy: Real object, but can stub specific methods
List<String> spyList = spy(new ArrayList<>());
spyList.add("item");  // Actually adds item
when(spyList.size()).thenReturn(100);  // Override size()

// Real: Use actual implementation
List<String> realList = new ArrayList<>();
```

## Code Example

### Complete Mockito Test Class

```java
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductRepository productRepo;
    
    @Mock
    private PaymentGateway paymentGateway;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    @DisplayName("Process order with valid payment")
    void processOrder_validPayment_completesSuccessfully() {
        // Arrange
        Product product = new Product("Laptop", 999.99);
        when(productRepo.findById(1L)).thenReturn(product);
        when(paymentGateway.charge(anyDouble())).thenReturn(true);
        
        // Act
        OrderResult result = orderService.processOrder(1L, 1);
        
        // Assert
        assertTrue(result.isSuccess());
        verify(paymentGateway).charge(999.99);
    }
    
    @Test
    @DisplayName("Process order with failed payment")
    void processOrder_failedPayment_returnsFailure() {
        // Arrange
        Product product = new Product("Laptop", 999.99);
        when(productRepo.findById(1L)).thenReturn(product);
        when(paymentGateway.charge(anyDouble())).thenReturn(false);
        
        // Act
        OrderResult result = orderService.processOrder(1L, 1);
        
        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Payment failed", result.getMessage());
    }
}
```

## Summary

- **Mockito** is Java's leading mocking framework for unit testing
- Mocking enables **test isolation** by simulating dependencies
- Use **@ExtendWith(MockitoExtension.class)** for JUnit5 integration
- **@Mock** creates mock objects; **@InjectMocks** injects them
- Design for testability with **dependency injection**
- Choose **mock vs spy vs real** based on isolation needs

## Additional Resources

- [Mockito Official Documentation](https://site.mockito.org/) - Comprehensive guide
- [Mockito GitHub](https://github.com/mockito/mockito) - Source and examples
- [Baeldung: Mockito Tutorial](https://www.baeldung.com/mockito-series) - Practical tutorials

