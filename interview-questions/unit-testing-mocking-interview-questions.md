# Unit Testing & Mocking Interview Questions & Answers

## Unit Testing - Beginner Level

**Q1: What is unit testing and why is it important?**

> **Answer:** Unit testing verifies the smallest testable parts of code (functions, methods, classes) work correctly in isolation.
>
> **Why it's important:**
> - **Early bug detection:** Find issues during development
> - **Refactoring safety:** Change code confidently
> - **Documentation:** Tests show how code should work
> - **Design improvement:** Forces modular, testable code
> - **Faster debugging:** Isolated failures are easier to fix
>
> **Characteristics of good unit tests (F.I.R.S.T.):**
> - **Fast:** Run in milliseconds
> - **Independent:** No test depends on another
> - **Repeatable:** Same result every time
> - **Self-validating:** Pass/fail without manual checking
> - **Timely:** Written with or before code

---

**Q2: Explain the structure of a unit test (Arrange-Act-Assert).**

> **Answer:** AAA is the standard pattern for organizing unit tests.
>
> **JUnit 5 (Java):**
> ```java
> @Test
> void calculateTotal_withValidItems_returnsCorrectSum() {
>     // Arrange - Set up test data
>     ShoppingCart cart = new ShoppingCart();
>     cart.addItem(new Item("Laptop", 1000.00));
>     cart.addItem(new Item("Mouse", 50.00));
>     
>     // Act - Execute the method being tested
>     double total = cart.calculateTotal();
>     
>     // Assert - Verify the result
>     assertEquals(1050.00, total, 0.01);
> }
> ```
>
> **pytest (Python):**
> ```python
> def test_calculate_total_with_valid_items():
>     # Arrange
>     cart = ShoppingCart()
>     cart.add_item(Item("Laptop", 1000.00))
>     cart.add_item(Item("Mouse", 50.00))
>     
>     # Act
>     total = cart.calculate_total()
>     
>     # Assert
>     assert total == 1050.00
> ```

---

**Q3: What are common JUnit 5 annotations?**

> **Answer:**
>
> | Annotation | Description |
> |------------|-------------|
> | `@Test` | Marks method as a test |
> | `@BeforeEach` | Run before each test |
> | `@AfterEach` | Run after each test |
> | `@BeforeAll` | Run once before all tests (static) |
> | `@AfterAll` | Run once after all tests (static) |
> | `@Disabled` | Skip this test |
> | `@DisplayName` | Custom test name |
> | `@Nested` | Nested test class |
> | `@Tag` | Tag for filtering |
> | `@ParameterizedTest` | Data-driven test |
>
> **Example:**
> ```java
> class UserServiceTest {
>     private UserService service;
>     
>     @BeforeAll
>     static void setupClass() {
>         // Initialize shared resources
>     }
>     
>     @BeforeEach
>     void setup() {
>         service = new UserService();
>     }
>     
>     @Test
>     @DisplayName("Should create user with valid data")
>     void createUser_validData_success() {
>         User user = service.create("Alice", "alice@test.com");
>         assertNotNull(user.getId());
>     }
>     
>     @Test
>     @Disabled("Pending implementation")
>     void updateUser_notImplemented() { }
>     
>     @AfterEach
>     void cleanup() {
>         service.reset();
>     }
> }
> ```

---

**Q4: What are pytest fixtures and how do you use them?**

> **Answer:** Fixtures provide reusable test setup and teardown.
>
> ```python
> import pytest
> 
> # Simple fixture
> @pytest.fixture
> def sample_user():
>     return {"name": "Alice", "email": "alice@test.com"}
> 
> def test_user_name(sample_user):
>     assert sample_user["name"] == "Alice"
> 
> # Fixture with setup and teardown
> @pytest.fixture
> def database():
>     # Setup
>     db = Database()
>     db.connect()
>     
>     yield db  # Test runs here
>     
>     # Teardown
>     db.disconnect()
> 
> def test_save_user(database):
>     database.save({"name": "Bob"})
>     assert database.count() == 1
> 
> # Fixture scopes
> @pytest.fixture(scope="function")  # Default - each test
> @pytest.fixture(scope="class")     # Once per class
> @pytest.fixture(scope="module")    # Once per file
> @pytest.fixture(scope="session")   # Once per test run
> 
> # Autouse fixture
> @pytest.fixture(autouse=True)
> def reset_state():
>     yield
>     State.reset()
> ```

---

**Q5: How do you write parameterized tests?**

> **Answer:** Parameterized tests run the same test with different inputs.
>
> **JUnit 5:**
> ```java
> @ParameterizedTest
> @ValueSource(strings = {"racecar", "radar", "level"})
> void isPalindrome_shouldReturnTrue(String word) {
>     assertTrue(StringUtils.isPalindrome(word));
> }
> 
> @ParameterizedTest
> @CsvSource({
>     "1, 2, 3",
>     "5, 5, 10",
>     "-1, 1, 0"
> })
> void add_shouldReturnSum(int a, int b, int expected) {
>     assertEquals(expected, calculator.add(a, b));
> }
> 
> @ParameterizedTest
> @MethodSource("provideTestData")
> void fromMethodSource(String input, int expected) {
>     assertEquals(expected, input.length());
> }
> 
> static Stream<Arguments> provideTestData() {
>     return Stream.of(
>         Arguments.of("hello", 5),
>         Arguments.of("world", 5)
>     );
> }
> ```
>
> **pytest:**
> ```python
> @pytest.mark.parametrize("input,expected", [
>     ("hello", 5),
>     ("world", 5),
>     ("", 0),
> ])
> def test_string_length(input, expected):
>     assert len(input) == expected
> 
> @pytest.mark.parametrize("a,b,expected", [
>     (1, 2, 3),
>     (-1, 1, 0),
>     (0, 0, 0),
> ])
> def test_addition(a, b, expected):
>     assert add(a, b) == expected
> ```

---

## Unit Testing - Intermediate Level

**Q6: Explain JUnit 5 assertions and common patterns.**

> **Answer:**
>
> ```java
> import static org.junit.jupiter.api.Assertions.*;
> 
> // Basic assertions
> assertEquals(expected, actual);
> assertEquals(expected, actual, "Custom message");
> assertNotEquals(unexpected, actual);
> 
> // Boolean
> assertTrue(condition);
> assertFalse(condition);
> 
> // Null checks
> assertNull(object);
> assertNotNull(object);
> 
> // Same reference
> assertSame(expected, actual);
> assertNotSame(unexpected, actual);
> 
> // Arrays
> assertArrayEquals(expectedArray, actualArray);
> 
> // Exceptions
> Exception exception = assertThrows(IllegalArgumentException.class, () -> {
>     service.process(null);
> });
> assertEquals("Input cannot be null", exception.getMessage());
> 
> // No exception
> assertDoesNotThrow(() -> service.safeMethod());
> 
> // Timeout
> assertTimeout(Duration.ofSeconds(5), () -> {
>     slowService.process();
> });
> 
> // Multiple assertions (all run even if some fail)
> assertAll(
>     () -> assertEquals("Alice", user.getName()),
>     () -> assertEquals(25, user.getAge()),
>     () -> assertNotNull(user.getId())
> );
> ```

---

**Q7: What is test coverage and how do you measure it?**

> **Answer:** Test coverage measures how much code is executed by tests.
>
> **Coverage types:**
> - **Line coverage:** % of lines executed
> - **Branch coverage:** % of branches (if/else) executed
> - **Method coverage:** % of methods called
> - **Class coverage:** % of classes tested
>
> **Tools:**
> - **Java:** JaCoCo, Cobertura
> - **Python:** coverage.py, pytest-cov
>
> **JaCoCo with Maven:**
> ```xml
> <plugin>
>     <groupId>org.jacoco</groupId>
>     <artifactId>jacoco-maven-plugin</artifactId>
>     <executions>
>         <execution>
>             <goals>
>                 <goal>prepare-agent</goal>
>                 <goal>report</goal>
>             </goals>
>         </execution>
>     </executions>
> </plugin>
> ```
>
> **pytest with coverage:**
> ```bash
> pytest --cov=myapp --cov-report=html
> ```
>
> **Coverage targets:**
> - 80%+ is generally good
> - 100% isn't always necessary or practical
> - Focus on critical paths, not just numbers

---

## Mocking - Beginner Level

**Q8: What is mocking and why is it needed?**

> **Answer:** Mocking creates fake objects that simulate real dependencies, allowing isolated unit testing.
>
> **Why mock:**
> - External services (APIs, databases) are slow/unavailable
> - Control test conditions precisely
> - Test error scenarios easily
> - Verify interactions between components
>
> **Example without mocking:**
> ```java
> // This test hits real database - slow, unreliable
> public void testCreateOrder() {
>     Order order = orderService.create(data);  // Hits DB
>     // Hard to test, slow, might fail due to DB issues
> }
> ```
>
> **With mocking:**
> ```java
> // Fast, reliable, isolated
> @Mock
> private OrderRepository repository;
> 
> @Test
> void createOrder_savesToRepository() {
>     when(repository.save(any())).thenReturn(savedOrder);
>     
>     Order result = orderService.create(data);
>     
>     verify(repository).save(any(Order.class));
> }
> ```

---

**Q9: How do you use Mockito in Java?**

> **Answer:**
>
> ```java
> import static org.mockito.Mockito.*;
> import org.mockito.Mock;
> import org.mockito.InjectMocks;
> 
> @ExtendWith(MockitoExtension.class)
> class OrderServiceTest {
>     
>     @Mock
>     private PaymentGateway paymentGateway;
>     
>     @Mock
>     private EmailService emailService;
>     
>     @InjectMocks
>     private OrderService orderService;
>     
>     @Test
>     void processOrder_chargesAndSendsEmail() {
>         // Arrange - Stub method behavior
>         Order order = new Order(100.00);
>         when(paymentGateway.charge(anyDouble())).thenReturn(true);
>         
>         // Act
>         boolean result = orderService.process(order);
>         
>         // Assert
>         assertTrue(result);
>         
>         // Verify interactions
>         verify(paymentGateway).charge(100.00);
>         verify(emailService).sendConfirmation(any(Order.class));
>     }
>     
>     @Test
>     void processOrder_paymentFails_noEmail() {
>         when(paymentGateway.charge(anyDouble())).thenReturn(false);
>         
>         orderService.process(new Order(100.00));
>         
>         verify(emailService, never()).sendConfirmation(any());
>     }
> }
> ```
>
> **Key Mockito methods:**
> | Method | Purpose |
> |--------|---------|
> | `when().thenReturn()` | Stub return value |
> | `when().thenThrow()` | Stub exception |
> | `verify()` | Check method was called |
> | `verify(never())` | Check NOT called |
> | `verify(times(n))` | Check call count |
> | `any()`, `anyString()` | Argument matchers |

---

**Q10: How do you use pytest-mock in Python?**

> **Answer:**
>
> ```python
> from unittest.mock import Mock, patch, MagicMock
> 
> # Using mocker fixture (pytest-mock)
> def test_order_service(mocker):
>     # Mock the payment gateway
>     mock_payment = mocker.patch('app.services.PaymentGateway')
>     mock_payment.return_value.charge.return_value = True
>     
>     # Mock email service
>     mock_email = mocker.patch('app.services.EmailService')
>     
>     # Test
>     service = OrderService()
>     result = service.process(Order(100.00))
>     
>     assert result is True
>     mock_payment.return_value.charge.assert_called_once_with(100.00)
>     mock_email.return_value.send_confirmation.assert_called_once()
> 
> # Using patch decorator
> @patch('app.services.external_api')
> def test_with_patch(mock_api):
>     mock_api.return_value = {"status": "success"}
>     result = my_service.call_external()
>     assert result["status"] == "success"
> 
> # Side effects - multiple returns or exceptions
> def test_retry_logic(mocker):
>     mock_api = mocker.patch('app.api.call')
>     mock_api.side_effect = [
>         ConnectionError("Failed"),
>         ConnectionError("Failed"),
>         {"status": "ok"}
>     ]
>     
>     result = service.call_with_retry(max_attempts=3)
>     assert result["status"] == "ok"
>     assert mock_api.call_count == 3
> ```

---

## Mocking - Intermediate Level

**Q11: What is the difference between Mock, Stub, Spy, and Fake?**

> **Answer:**
>
> | Type | Purpose | Verification |
> |------|---------|--------------|
> | **Stub** | Provides canned responses | No verification |
> | **Mock** | Verifies interactions | Yes - check calls |
> | **Spy** | Wraps real object | Can verify |
> | **Fake** | Working implementation (simplified) | No verification |
>
> **Mockito examples:**
> ```java
> // Stub - just returns values
> when(repository.findById(1)).thenReturn(user);
> 
> // Mock - verify interactions
> verify(repository).save(any(User.class));
> 
> // Spy - partial mock (calls real methods)
> List<String> realList = new ArrayList<>();
> List<String> spyList = spy(realList);
> spyList.add("one");  // Actually adds
> verify(spyList).add("one");
> 
> // Fake - simple working implementation
> class FakeUserRepository implements UserRepository {
>     private Map<Integer, User> users = new HashMap<>();
>     
>     public User save(User user) {
>         users.put(user.getId(), user);
>         return user;
>     }
> }
> ```

---

**Q12: How do you mock static methods and constructors?**

> **Answer:**
>
> **Mockito (static methods - requires mockito-inline):**
> ```java
> @Test
> void testStaticMethod() {
>     try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {
>         mockedStatic.when(() -> FileUtils.readFile("test.txt"))
>             .thenReturn("mock content");
>         
>         String content = FileUtils.readFile("test.txt");
>         assertEquals("mock content", content);
>     }
> }
> 
> // Mock constructor
> @Test
> void testConstructor() {
>     try (MockedConstruction<Database> mocked = 
>             mockConstruction(Database.class)) {
>         
>         Database db = new Database();  // Returns mock
>         when(db.connect()).thenReturn(true);
>         
>         assertTrue(db.connect());
>     }
> }
> ```
>
> **Python:**
> ```python
> # Mock static/class method
> @patch.object(FileUtils, 'read_file')
> def test_static_method(mock_read):
>     mock_read.return_value = "mock content"
>     
>     result = FileUtils.read_file("test.txt")
>     assert result == "mock content"
> 
> # Mock constructor / new instance
> @patch('app.database.Database')
> def test_database_connection(MockDatabase):
>     mock_instance = MockDatabase.return_value
>     mock_instance.connect.return_value = True
>     
>     service = MyService()  # Creates Database internally
>     assert service.is_connected()
> ```
