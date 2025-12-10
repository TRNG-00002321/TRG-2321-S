# Lab: Test Lifecycle - Database Mock Scenario

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | setup-teardown.md, demo_lifecycle_methods.java |

## Learning Objectives
By completing this exercise, you will:
- Use `@BeforeEach` and `@AfterEach` for per-test setup/cleanup
- Use `@BeforeAll` and `@AfterAll` for class-level setup/cleanup
- Understand when to use each lifecycle method
- Manage shared resources properly
- Ensure test isolation

## The Scenario

You're testing a `UserRepository` that interacts with a database. For testing purposes, you'll use an in-memory "mock database" (`MockDatabase`). Your challenge is to:
1. Set up the database connection once before all tests
2. Clear/reset data before each test
3. Clean up properly after tests complete

## Core Tasks

### Task 1: Implement @BeforeAll and @AfterAll (15 minutes)

The database connection is expensive to create. Set it up once for all tests:

```java
class UserRepositoryTest {
    
    private static MockDatabase database;
    private UserRepository repository;
    
    @BeforeAll
    static void setUpDatabase() {
        // TODO: Initialize the database connection
        // This runs ONCE before all tests
        System.out.println("Connecting to database...");
        database = new MockDatabase();
        database.connect();
    }
    
    @AfterAll
    static void tearDownDatabase() {
        // TODO: Close the database connection
        // This runs ONCE after all tests
        System.out.println("Disconnecting from database...");
        database.disconnect();
    }
}
```

### Task 2: Implement @BeforeEach and @AfterEach (15 minutes)

Each test needs fresh data. Reset the database before each test:

```java
@BeforeEach
void setUpTest() {
    // TODO: Clear all data from database
    // TODO: Create a new repository instance
    // TODO: Insert any test fixtures needed
    database.clearAll();
    repository = new UserRepository(database);
    
    // Optional: Insert baseline test data
    database.insert(new User(1, "Admin", "admin@test.com"));
}

@AfterEach
void tearDownTest() {
    // TODO: Any per-test cleanup
    // Note: The database is cleared in setUpTest anyway
    System.out.println("Test completed, data will be reset");
}
```

### Task 3: Write Tests That Verify Isolation (15 minutes)

Write tests that prove your lifecycle methods work correctly:

```java
@Test
@DisplayName("Test 1: Add user and verify")
void test1_addUser() {
    // Add a user
    repository.save(new User(2, "John", "john@test.com"));
    
    // Verify it exists
    assertEquals(2, repository.count());  // Admin + John
}

@Test
@DisplayName("Test 2: Should have fresh state")
void test2_freshState() {
    // This test should ONLY see the Admin user
    // NOT the John user from test1
    assertEquals(1, repository.count());  // Only Admin
}

@Test
@DisplayName("Test 3: Database operations work independently")
void test3_independentOperations() {
    repository.save(new User(3, "Jane", "jane@test.com"));
    repository.save(new User(4, "Bob", "bob@test.com"));
    
    // Should have Admin + 2 new users
    assertEquals(3, repository.count());
}
```

### Task 4: Add Execution Order Logging (10 minutes)

Add print statements to verify execution order:

```java
@BeforeAll
static void setUpDatabase() {
    System.out.println("1. @BeforeAll: Setting up database");
}

@BeforeEach
void setUpTest() {
    System.out.println("  2. @BeforeEach: Preparing test");
}

@Test
void someTest() {
    System.out.println("    3. @Test: Running test");
}

@AfterEach
void tearDownTest() {
    System.out.println("  4. @AfterEach: Cleaning up test");
}

@AfterAll
static void tearDownDatabase() {
    System.out.println("5. @AfterAll: Closing database");
}
```

**Expected output:**
```
1. @BeforeAll: Setting up database
  2. @BeforeEach: Preparing test
    3. @Test: Running test1
  4. @AfterEach: Cleaning up test
  2. @BeforeEach: Preparing test
    3. @Test: Running test2
  4. @AfterEach: Cleaning up test
5. @AfterAll: Closing database
```

## Starter Code

```java
// MockDatabase.java
public class MockDatabase {
    private Map<Integer, User> users = new HashMap<>();
    private boolean connected = false;
    
    public void connect() {
        connected = true;
        System.out.println("[DB] Connected");
    }
    
    public void disconnect() {
        connected = false;
        users.clear();
        System.out.println("[DB] Disconnected");
    }
    
    public void insert(User user) {
        if (!connected) throw new IllegalStateException("Not connected");
        users.put(user.getId(), user);
    }
    
    public User findById(int id) {
        return users.get(id);
    }
    
    public void clearAll() {
        users.clear();
        System.out.println("[DB] All data cleared");
    }
    
    public int count() {
        return users.size();
    }
}

// UserRepository.java
public class UserRepository {
    private final MockDatabase database;
    
    public UserRepository(MockDatabase database) {
        this.database = database;
    }
    
    public void save(User user) {
        database.insert(user);
    }
    
    public User findById(int id) {
        return database.findById(id);
    }
    
    public int count() {
        return database.count();
    }
}

// User.java
public class User {
    private int id;
    private String name;
    private String email;
    
    // Constructor, getters, setters
}
```

## Definition of Done

- [ ] `@BeforeAll` connects to database once
- [ ] `@AfterAll` disconnects from database once
- [ ] `@BeforeEach` clears data and creates repository
- [ ] `@AfterEach` performs per-test cleanup
- [ ] At least 3 tests that prove isolation works
- [ ] Logging shows correct execution order
- [ ] All tests pass regardless of execution order
- [ ] No test depends on another test's data

## Decision Guide: When to Use Each

| Lifecycle Method | Use When |
|------------------|----------|
| `@BeforeAll` | Expensive setup (DB connections, servers) |
| `@AfterAll` | Releasing expensive resources |
| `@BeforeEach` | Fresh state per test (objects, data) |
| `@AfterEach` | Per-test cleanup (files, transactions) |

## Common Mistakes

1. **Forgetting `static`** on `@BeforeAll`/`@AfterAll` methods
2. **Shared mutable state** between tests without reset
3. **Test order dependency** - tests should work in any order
4. **Not cleaning up** resources (memory leaks)

## Submission

Commit with message:
```
feat(week6): Complete test lifecycle exercise
```

