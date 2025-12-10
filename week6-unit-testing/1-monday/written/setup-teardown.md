# Setup & Teardown: Test Lifecycle Management in JUnit5

## Learning Objectives
- Use `@BeforeEach`, `@AfterEach`, `@BeforeAll`, and `@AfterAll` effectively
- Understand the JUnit5 test lifecycle and execution order
- Manage shared resources and test isolation through lifecycle methods
- Apply best practices for setup and teardown operations

## Why This Matters

Real-world tests often need preparation before they run and cleanup after they complete. Database connections need opening and closing. Test data needs creating and removing. Mock servers need starting and stopping. JUnit5's lifecycle annotations give you precise control over when these operations happen, ensuring your tests are isolated, repeatable, and efficient.

## The Concept

### The Test Lifecycle

JUnit5 executes tests in a well-defined sequence:

```
┌─────────────────────────────────────────────────────────────┐
│                    TEST CLASS LIFECYCLE                      │
├─────────────────────────────────────────────────────────────┤
│  @BeforeAll (once, before all tests)                        │
│  │                                                          │
│  ├─► @BeforeEach (before each test)                         │
│  │   └─► @Test (testMethodOne)                              │
│  │       └─► @AfterEach (after each test)                   │
│  │                                                          │
│  ├─► @BeforeEach (before each test)                         │
│  │   └─► @Test (testMethodTwo)                              │
│  │       └─► @AfterEach (after each test)                   │
│  │                                                          │
│  @AfterAll (once, after all tests)                          │
└─────────────────────────────────────────────────────────────┘
```

### @BeforeEach

Runs before **each** test method. Use for setting up fresh test state:

```java
class UserServiceTest {
    private UserService service;
    private UserRepository repository;
    
    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        service = new UserService(repository);
    }
    
    @Test
    void createUser_validData_userCreated() {
        // Each test gets fresh service and repository
        User user = service.createUser("John", "john@example.com");
        assertNotNull(user.getId());
    }
    
    @Test
    void findUser_existingUser_returnsUser() {
        // Fresh state - repository is empty
        repository.save(new User("Jane", "jane@example.com"));
        // ...
    }
}
```

### @AfterEach

Runs after **each** test method. Use for cleanup to ensure test isolation:

```java
class FileProcessorTest {
    private Path tempFile;
    
    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "test data");
    }
    
    @AfterEach
    void tearDown() throws IOException {
        // Clean up temp file after each test
        Files.deleteIfExists(tempFile);
    }
    
    @Test
    void readFile_existingFile_returnsContent() throws IOException {
        String content = Files.readString(tempFile);
        assertEquals("test data", content);
    }
}
```

### @BeforeAll

Runs **once** before all tests in the class. Must be `static`:

```java
class DatabaseIntegrationTest {
    private static DatabaseConnection connection;
    
    @BeforeAll
    static void initDatabase() {
        // Expensive operation - do once
        connection = DatabaseConnection.create("test-db");
        connection.migrate();
    }
    
    @Test
    void queryUsers_returnsResults() {
        // Uses shared connection
        List<User> users = connection.query("SELECT * FROM users");
        assertNotNull(users);
    }
    
    @AfterAll
    static void closeDatabase() {
        connection.close();
    }
}
```

### @AfterAll

Runs **once** after all tests complete. Must be `static`:

```java
class ExternalServiceTest {
    private static MockServer mockServer;
    
    @BeforeAll
    static void startMockServer() {
        mockServer = new MockServer(8080);
        mockServer.start();
    }
    
    @AfterAll
    static void stopMockServer() {
        mockServer.stop();
    }
    
    // Tests use mockServer...
}
```

### Per-Class Test Instance Lifecycle

By default, JUnit5 creates a new instance for each test. You can change this:

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PerClassLifecycleTest {
    private int counter = 0;
    
    // Now @BeforeAll and @AfterAll don't need to be static!
    @BeforeAll
    void setUp() {
        counter = 100;  // Can access instance state
    }
    
    @Test
    void test1() {
        counter++;
        assertEquals(101, counter);
    }
    
    @Test  
    void test2() {
        // Caution: test order affects this!
        // counter value depends on which test ran first
    }
}
```

### Execution Order Example

```java
class LifecycleOrderDemo {
    
    @BeforeAll
    static void beforeAll() {
        System.out.println("1. @BeforeAll");
    }
    
    @BeforeEach
    void beforeEach() {
        System.out.println("  2. @BeforeEach");
    }
    
    @Test
    void testA() {
        System.out.println("    3. Test A");
    }
    
    @Test
    void testB() {
        System.out.println("    3. Test B");
    }
    
    @AfterEach
    void afterEach() {
        System.out.println("  4. @AfterEach");
    }
    
    @AfterAll
    static void afterAll() {
        System.out.println("5. @AfterAll");
    }
}
```

**Output:**
```
1. @BeforeAll
  2. @BeforeEach
    3. Test A
  4. @AfterEach
  2. @BeforeEach
    3. Test B
  4. @AfterEach
5. @AfterAll
```

### Resource Management Best Practices

```java
class ResourceManagementTest {
    private Connection dbConnection;
    private FileWriter logWriter;
    
    @BeforeEach
    void setUp() throws Exception {
        dbConnection = DriverManager.getConnection("jdbc:h2:mem:test");
        logWriter = new FileWriter("test.log", true);
    }
    
    @AfterEach
    void tearDown() {
        // Always clean up in reverse order of creation
        // Use try-with-resources pattern in cleanup
        try {
            if (logWriter != null) {
                logWriter.close();
            }
        } catch (IOException e) {
            // Log but don't fail
        }
        
        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
            }
        } catch (SQLException e) {
            // Log but don't fail
        }
    }
    
    @Test
    void testWithResources() throws Exception {
        // Resources are available here
        Statement stmt = dbConnection.createStatement();
        stmt.execute("CREATE TABLE test (id INT)");
    }
}
```

## Code Example

### Complete Lifecycle Demonstration

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartLifecycleTest {
    
    // Shared across all tests (expensive to create)
    private static ProductCatalog catalog;
    
    // Fresh for each test
    private ShoppingCart cart;
    private Customer customer;
    
    @BeforeAll
    static void loadProductCatalog() {
        System.out.println("Loading product catalog (once)...");
        catalog = ProductCatalog.loadFromFile("products.json");
    }
    
    @BeforeEach
    void setUpCart() {
        System.out.println("Creating fresh cart and customer...");
        customer = new Customer("test@example.com");
        cart = new ShoppingCart(customer);
    }
    
    @Test
    @DisplayName("Adding product increases cart total")
    void addProduct_increasesTotal() {
        Product laptop = catalog.findByName("Laptop");
        
        cart.add(laptop, 1);
        
        assertEquals(laptop.getPrice(), cart.getTotal());
        assertEquals(1, cart.getItemCount());
    }
    
    @Test
    @DisplayName("Empty cart has zero total")
    void emptyCart_hasZeroTotal() {
        // Cart is fresh - not affected by previous test
        assertEquals(0, cart.getTotal());
        assertTrue(cart.isEmpty());
    }
    
    @Test
    @DisplayName("Removing last item empties cart")
    void removeLastItem_emptiesCart() {
        Product book = catalog.findByName("Book");
        cart.add(book, 1);
        
        cart.remove(book);
        
        assertTrue(cart.isEmpty());
    }
    
    @AfterEach
    void logCartState() {
        System.out.println("Cart had " + cart.getItemCount() + " items");
        // Any cleanup needed per test
    }
    
    @AfterAll
    static void reportSummary() {
        System.out.println("All cart tests completed.");
        // Release any static resources
        catalog = null;
    }
}
```

### When to Use Each Annotation

```
┌──────────────────────────────────────────────────────────────────┐
│              LIFECYCLE ANNOTATION DECISION GUIDE                 │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Expensive, shareable setup?                                     │
│  (Database connections, servers, large datasets)                 │
│  └─► @BeforeAll + @AfterAll                                      │
│                                                                  │
│  Fresh state needed per test?                                    │
│  (Object instances, test data, mocks)                            │
│  └─► @BeforeEach + @AfterEach                                    │
│                                                                  │
│  Cleanup always required?                                        │
│  (Files, connections, transactions)                              │
│  └─► @AfterEach (runs even if test fails)                        │
│                                                                  │
│  One-time cleanup?                                               │
│  (Servers, shared resources)                                     │
│  └─► @AfterAll                                                   │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

## Summary

- **@BeforeEach**: Runs before each test—use for fresh test state
- **@AfterEach**: Runs after each test—use for cleanup
- **@BeforeAll**: Runs once before all tests—use for expensive shared setup
- **@AfterAll**: Runs once after all tests—use for final cleanup
- @BeforeAll/@AfterAll must be **static** (unless using `@TestInstance(PER_CLASS)`)
- **@AfterEach runs even if tests fail**—critical for resource cleanup
- Keep tests **isolated**: each test should be independent of others

## Additional Resources

- [JUnit5 Test Lifecycle](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle) - Official documentation
- [Test Fixtures Best Practices](https://www.baeldung.com/junit-5-test-order) - Managing test dependencies
- [Resource Management in Tests](https://dzone.com/articles/junit-5-test-lifecycle) - Practical patterns

