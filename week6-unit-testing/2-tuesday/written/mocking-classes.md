# Mocking Classes: Creating Test Doubles with Mockito

## Learning Objectives
- Create mocks using `@Mock` annotation and `Mockito.mock()` method
- Understand mock behavior and default return values
- Distinguish between mock and spy approaches
- Apply best practices for when to use mocks vs real objects

## Why This Matters

When unit testing a class that depends on other classes, you need to control those dependencies. Mocking lets you create fake versions of dependencies that behave exactly as you specify—no database calls, no network requests, no file system access. Just pure, isolated unit tests.

## The Concept

### Creating Mocks

**Method 1: @Mock Annotation (Recommended)**
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository repository;  // Mock created automatically
    
    @Test
    void test() {
        // repository is ready to use
    }
}
```

**Method 2: Mockito.mock() Method**
```java
class UserServiceTest {
    
    @Test
    void test() {
        UserRepository repository = Mockito.mock(UserRepository.class);
        // repository is ready to use
    }
}
```

### Default Mock Behavior

Unstubbed mock methods return default values:

```java
@Mock
private UserRepository repository;

@Test
void defaultMockBehavior() {
    // Numeric types return 0
    assertEquals(0, repository.count());
    
    // Objects return null
    assertNull(repository.findById(1L));
    
    // Collections return empty (not null)
    assertTrue(repository.findAll().isEmpty());
    
    // Booleans return false
    assertFalse(repository.exists(1L));
    
    // void methods do nothing
    repository.delete(1L);  // No effect, no exception
}
```

### @InjectMocks: Automatic Dependency Injection

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductRepository productRepo;
    
    @Mock
    private PaymentGateway paymentGateway;
    
    @InjectMocks
    private OrderService orderService;  // Mocks injected automatically
    
    @Test
    void processOrder() {
        // orderService has productRepo and paymentGateway injected
    }
}
```

### Mock vs Spy

**Mock**: Complete fake, no real behavior
```java
@Mock
private ArrayList<String> mockList;

@Test
void mockBehavior() {
    mockList.add("item");           // Does nothing
    assertEquals(0, mockList.size()); // Still 0
}
```

**Spy**: Real object with selective overrides
```java
@Spy
private ArrayList<String> spyList = new ArrayList<>();

@Test
void spyBehavior() {
    spyList.add("item");            // Actually adds
    assertEquals(1, spyList.size()); // Returns 1
    
    // Can still stub specific methods
    when(spyList.size()).thenReturn(100);
    assertEquals(100, spyList.size());
}
```

### When to Use What

| Scenario | Use |
|----------|-----|
| External service (API, database) | Mock |
| Complete isolation needed | Mock |
| Testing the class itself | Real object |
| Need mostly real behavior | Spy |
| Legacy code you can't change | Spy |

## Code Example

### Comprehensive Mock Usage

```java
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private EmailClient emailClient;
    
    @Mock
    private TemplateEngine templateEngine;
    
    @InjectMocks
    private EmailService emailService;
    
    @Test
    void sendWelcomeEmail_success() {
        // Arrange
        User user = new User("john@example.com", "John");
        when(templateEngine.render("welcome", user))
            .thenReturn("Welcome, John!");
        when(emailClient.send(anyString(), anyString()))
            .thenReturn(true);
        
        // Act
        boolean result = emailService.sendWelcome(user);
        
        // Assert
        assertTrue(result);
        verify(emailClient).send("john@example.com", "Welcome, John!");
    }
    
    @Test
    void sendWelcomeEmail_emailClientFails() {
        // Arrange
        User user = new User("john@example.com", "John");
        when(templateEngine.render(anyString(), any()))
            .thenReturn("Welcome!");
        when(emailClient.send(anyString(), anyString()))
            .thenReturn(false);  // Email fails
        
        // Act
        boolean result = emailService.sendWelcome(user);
        
        // Assert
        assertFalse(result);
    }
}
```

## Summary

- Create mocks with **@Mock annotation** or **Mockito.mock()**
- Use **@ExtendWith(MockitoExtension.class)** for JUnit5
- **@InjectMocks** automatically injects mocks into the test subject
- Unstubbed mocks return **default values** (null, 0, false, empty)
- **Mocks** = complete fakes; **Spies** = real objects with overrides
- Use mocks to **isolate** the unit under test from dependencies

## Additional Resources

- [Mockito @Mock Annotation](https://www.baeldung.com/mockito-annotations) - Annotation guide
- [Mock vs Spy](https://www.baeldung.com/mockito-spy) - When to use each
- [Mockito Best Practices](https://github.com/mockito/mockito/wiki) - Official wiki

