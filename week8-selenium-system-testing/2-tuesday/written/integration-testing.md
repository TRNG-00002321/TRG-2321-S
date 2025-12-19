# Integration Testing

## Learning Objectives
- Define integration testing and its objectives in software quality assurance
- Compare integration testing approaches: Big Bang, Incremental, Top-Down, Bottom-Up, Sandwich
- Differentiate integration testing from unit testing and system testing
- Design effective integration tests for component interactions
- Create and use stubs and drivers for isolated integration testing
- Apply integration testing best practices in continuous integration environments

## Why This Matters

In Week 6, you mastered unit testing with JUnit5, Mockito, pytest, and pytest-mock—testing components in isolation. But isolated components that work perfectly individually can fail spectacularly when combined. **Integration testing** bridges this gap.

Understanding integration testing helps you:
- Catch interface defects early
- Verify components communicate correctly
- Identify data flow issues between modules
- Build confidence before system testing

This knowledge complements your automation skills and helps you design test suites that cover the right concerns at the right level.

## The Concept

### What is Integration Testing?

**Integration testing** is a level of software testing where individual software modules are combined and tested as a group. The purpose is to expose defects in the interfaces and interactions between integrated components.

```
┌─────────────────────────────────────────────────────────────────┐
│                    Integration Testing Focus                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│    Component A          Interface          Component B          │
│   ┌──────────┐         ┌───────┐          ┌──────────┐         │
│   │          │◄───────►│       │◄────────►│          │         │
│   │  Module  │   API   │ Data  │  Message │  Module  │         │
│   │          │  Calls  │ Flow  │  Passing │          │         │
│   └──────────┘         └───────┘          └──────────┘         │
│                                                                  │
│   Integration testing verifies these connections work correctly  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Integration Testing Objectives

1. **Interface Verification** - Components communicate correctly
2. **Data Integrity** - Data transfers accurately between modules
3. **API Contract Validation** - Components honor their contracts
4. **Error Handling** - Errors propagate correctly across boundaries
5. **Configuration Validation** - Components configure correctly together
6. **External Service Integration** - Third-party integrations work

### Integration Testing Approaches

#### 1. Big Bang Integration

All components are integrated simultaneously and tested as a whole.

```
                    ┌─────────────────────────────┐
                    │        Big Bang              │
                    │     Integration              │
                    └─────────────────────────────┘
                               ▲
               ┌───────────────┼───────────────┐
               │               │               │
          ┌────┴────┐     ┌────┴────┐     ┌────┴────┐
          │Module A │     │Module B │     │Module C │
          └─────────┘     └─────────┘     └─────────┘
          
          All modules integrated at once
```

| Advantages | Disadvantages |
|------------|---------------|
| Simple approach | Difficult to isolate defects |
| Good for small systems | High risk if failures occur |
| Less planning needed | Late defect detection |
| | Debugging is challenging |

**When to Use:** Small projects, tight deadlines, simple integrations

#### 2. Incremental Integration

Components are integrated and tested one at a time or in small groups.

```
Step 1:  [A] + [B] → Test
Step 2:  [A+B] + [C] → Test  
Step 3:  [A+B+C] + [D] → Test
```

| Advantages | Disadvantages |
|------------|---------------|
| Easier defect isolation | Requires more planning |
| Earlier defect detection | Takes more time |
| Systematic approach | Needs stubs/drivers |

#### 3. Top-Down Integration

Start from the top-level modules and progressively integrate lower-level modules.

```
         ┌──────────┐
         │   Main   │  ← Start here
         │  Module  │
         └────┬─────┘
              │
    ┌─────────┴─────────┐
    │                   │
┌───┴───┐           ┌───┴───┐
│Module │           │Module │  ← Then here
│  A    │           │  B    │
└───┬───┘           └───┬───┘
    │                   │
┌───┴───┐           ┌───┴───┐
│Module │           │Module │  ← Finally here
│  A1   │           │  B1   │
└───────┘           └───────┘

Lower modules replaced with STUBS until ready
```

| Advantages | Disadvantages |
|------------|---------------|
| Early prototype available | Stubs needed for lower modules |
| Critical modules tested first | Lower modules tested late |
| Design defects found early | Stub creation overhead |
| Major functions verified early | |

**Stubs Required:** Stubs simulate lower-level modules

#### 4. Bottom-Up Integration

Start from the lowest-level modules and progressively integrate higher-level modules.

```
┌───────┐           ┌───────┐
│Module │           │Module │  ← Start here
│  A1   │           │  B1   │
└───┬───┘           └───┬───┘
    │                   │
┌───┴───┐           ┌───┴───┐
│Module │           │Module │  ← Then here
│  A    │           │  B    │
└───┬───┘           └───┬───┘
    │                   │
    └─────────┬─────────┘
              │
         ┌────┴─────┐
         │   Main   │  ← Finally here
         │  Module  │
         └──────────┘

Higher modules replaced with DRIVERS until ready
```

| Advantages | Disadvantages |
|------------|---------------|
| No stubs needed | Drivers needed for higher modules |
| Lower modules tested thoroughly | No working prototype until late |
| Easier to observe test results | Top-level design issues found late |
| Good for utility/infrastructure code | |

**Drivers Required:** Drivers simulate higher-level modules

#### 5. Sandwich (Hybrid) Integration

Combines top-down and bottom-up approaches, meeting in the middle.

```
        Top-Down                    Bottom-Up
            ↓                           ↑
    ┌──────────────┐            ┌──────────────┐
    │  High Level  │            │  Low Level   │
    │   Modules    │            │   Modules    │
    └──────────────┘            └──────────────┘
            ↓                           ↑
    ┌──────────────────────────────────────────┐
    │           Middle Layer                    │
    │    (Integration point - Target Layer)     │
    └──────────────────────────────────────────┘
```

| Advantages | Disadvantages |
|------------|---------------|
| Parallel development possible | Complex to plan |
| Best of both approaches | May miss middle-layer defects |
| Faster overall | Requires coordination |

### Integration Testing vs Unit Testing

| Aspect | Unit Testing | Integration Testing |
|--------|--------------|---------------------|
| **Scope** | Single class/function | Multiple components |
| **Dependencies** | Mocked/Stubbed | Real (mostly) |
| **Speed** | Very fast | Slower |
| **Isolation** | Complete | Partial |
| **Database** | Mocked | Real test database |
| **Network** | Mocked | Real or sandboxed |
| **Purpose** | Code correctness | Interface correctness |

### Integration Testing vs System Testing

| Aspect | Integration Testing | System Testing |
|--------|---------------------|----------------|
| **Scope** | Component groups | Entire system |
| **Focus** | Interfaces | Requirements |
| **Environment** | Test environment | System test environment |
| **External Systems** | Mocked/Sandboxed | Real/Production-like |
| **User Perspective** | Technical | End-user |
| **Test Basis** | Design specifications | System requirements |

### Stubs and Drivers

**Stubs** and **Drivers** are test doubles used to simulate components during integration testing.

#### Stubs (For Top-Down Testing)

A **stub** is a dummy implementation of a lower-level module:

```python
# Real module (not ready yet)
class PaymentGateway:
    def process_payment(self, amount, card_details):
        # Complex payment processing logic
        pass

# Stub for integration testing
class PaymentGatewayStub:
    def process_payment(self, amount, card_details):
        """Stub that simulates payment processing"""
        if amount > 0 and card_details.get('number'):
            return {
                'success': True,
                'transaction_id': 'STUB-TXN-12345',
                'amount': amount
            }
        return {
            'success': False,
            'error': 'Invalid payment details'
        }

# Integration test using stub
def test_checkout_with_payment_stub():
    checkout_service = CheckoutService(
        payment_gateway=PaymentGatewayStub()
    )
    result = checkout_service.complete_purchase(
        cart_id='CART-001',
        payment_details={'number': '4111111111111111'}
    )
    assert result['status'] == 'completed'
```

#### Drivers (For Bottom-Up Testing)

A **driver** is a dummy implementation that calls the module under test:

```python
# Module under test
class OrderProcessor:
    def process_order(self, order_data):
        # Validate order
        if not order_data.get('items'):
            raise ValueError("Order must have items")
        
        # Calculate total
        total = sum(item['price'] * item['quantity'] 
                   for item in order_data['items'])
        
        return {
            'order_id': f"ORD-{order_data['customer_id']}",
            'total': total,
            'status': 'processed'
        }

# Driver to test OrderProcessor
class OrderProcessorDriver:
    """Driver that simulates the calling module"""
    
    def __init__(self, order_processor):
        self.processor = order_processor
    
    def run_test_scenarios(self):
        """Execute integration test scenarios"""
        
        # Scenario 1: Valid order
        valid_order = {
            'customer_id': 'CUST-001',
            'items': [
                {'product': 'Widget', 'price': 10.00, 'quantity': 2},
                {'product': 'Gadget', 'price': 25.00, 'quantity': 1}
            ]
        }
        result = self.processor.process_order(valid_order)
        assert result['total'] == 45.00
        print("✓ Valid order test passed")
        
        # Scenario 2: Empty order
        try:
            self.processor.process_order({'customer_id': 'CUST-002', 'items': []})
            print("✗ Should have raised error for empty order")
        except ValueError:
            print("✓ Empty order rejection test passed")

# Run integration test
if __name__ == '__main__':
    processor = OrderProcessor()
    driver = OrderProcessorDriver(processor)
    driver.run_test_scenarios()
```

### Integration Test Design

#### Identifying Integration Points

```
┌─────────────────────────────────────────────────────────────────┐
│              E-Commerce System Integration Points                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  User Interface ←───────→ API Layer (REST endpoints)            │
│       │                        │                                 │
│       │                        ↓                                 │
│       │               Service Layer                              │
│       │         ┌──────────────┼──────────────┐                 │
│       │         ↓              ↓              ↓                 │
│       │    OrderService  PaymentService  InventoryService       │
│       │         │              │              │                 │
│       │         └──────────────┼──────────────┘                 │
│       │                        ↓                                 │
│       │               Database Layer                             │
│       │                        │                                 │
│       └────────────────────────┘                                 │
│                                                                  │
│  Integration Points to Test:                                     │
│  1. UI → API (HTTP requests/responses)                          │
│  2. API → Services (Method calls, data transformation)          │
│  3. Services → Database (CRUD operations)                       │
│  4. Service → Service (OrderService → InventoryService)         │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### Integration Test Examples

**Python Integration Test with pytest:**

```python
"""
test_order_inventory_integration.py
Integration test between OrderService and InventoryService
"""
import pytest
from services.order_service import OrderService
from services.inventory_service import InventoryService
from repositories.order_repository import OrderRepository
from repositories.inventory_repository import InventoryRepository

class TestOrderInventoryIntegration:
    
    @pytest.fixture
    def services(self, test_database):
        """Set up real services with test database"""
        inventory_repo = InventoryRepository(test_database)
        order_repo = OrderRepository(test_database)
        
        inventory_service = InventoryService(inventory_repo)
        order_service = OrderService(order_repo, inventory_service)
        
        return order_service, inventory_service
    
    def test_order_reduces_inventory(self, services):
        """Test that placing an order reduces inventory"""
        order_service, inventory_service = services
        
        # Arrange: Set initial inventory
        inventory_service.set_stock('PROD-001', 100)
        
        # Act: Place order
        order = order_service.create_order({
            'customer_id': 'CUST-001',
            'items': [{'product_id': 'PROD-001', 'quantity': 5}]
        })
        
        # Assert: Inventory reduced
        remaining = inventory_service.get_stock('PROD-001')
        assert remaining == 95
        assert order['status'] == 'confirmed'
    
    def test_order_fails_on_insufficient_inventory(self, services):
        """Test that order fails when inventory insufficient"""
        order_service, inventory_service = services
        
        # Arrange: Limited inventory
        inventory_service.set_stock('PROD-002', 2)
        
        # Act & Assert: Order should fail
        with pytest.raises(InsufficientInventoryError):
            order_service.create_order({
                'customer_id': 'CUST-001',
                'items': [{'product_id': 'PROD-002', 'quantity': 10}]
            })
        
        # Inventory should be unchanged
        assert inventory_service.get_stock('PROD-002') == 2
```

**Java Integration Test with JUnit5:**

```java
/**
 * Integration test for Order and Payment services
 */
@SpringBootTest
@Transactional
class OrderPaymentIntegrationTest {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Test
    void orderCompletesWhenPaymentSucceeds() {
        // Arrange
        Order order = new Order("CUST-001", List.of(
            new OrderItem("PROD-001", 2, 25.00)
        ));
        
        PaymentDetails payment = new PaymentDetails(
            "4111111111111111", "12/25", "123"
        );
        
        // Act
        OrderResult result = orderService.processOrder(order, payment);
        
        // Assert
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(result.getPaymentConfirmation()).isNotNull();
        
        Order savedOrder = orderRepository.findById(result.getOrderId()).orElseThrow();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }
    
    @Test
    void orderRollsBackWhenPaymentFails() {
        // Arrange
        Order order = new Order("CUST-001", List.of(
            new OrderItem("PROD-001", 1, 50.00)
        ));
        
        // Invalid card
        PaymentDetails payment = new PaymentDetails(
            "0000000000000000", "01/20", "000"
        );
        
        // Act & Assert
        assertThrows(PaymentFailedException.class, () -> {
            orderService.processOrder(order, payment);
        });
        
        // Verify order was not saved
        List<Order> orders = orderRepository.findByCustomerId("CUST-001");
        assertThat(orders).isEmpty();
    }
}
```

### Integration Testing Best Practices

1. **Test Integration Points, Not Implementation**
   ```python
   # BAD: Testing internal implementation
   def test_internal_method():
       assert service._internal_process() == expected
   
   # GOOD: Testing public interface
   def test_public_behavior():
       result = service.process_request(input_data)
       assert result['status'] == 'success'
   ```

2. **Use Realistic Test Data**
   ```python
   # Use data that represents real scenarios
   test_order = {
       'customer_id': 'CUST-12345',  # Realistic format
       'items': [
           {'product_id': 'SKU-001', 'quantity': 2, 'price': 29.99},
           {'product_id': 'SKU-002', 'quantity': 1, 'price': 49.99}
       ],
       'shipping_address': {
           'street': '123 Main St',
           'city': 'Springfield',
           'state': 'IL',
           'zip': '62701'
       }
   }
   ```

3. **Isolate Test Database**
   ```python
   @pytest.fixture(scope='function')
   def test_database():
       """Create isolated test database for each test"""
       db = create_test_database()
       yield db
       db.cleanup()
   ```

4. **Test Error Paths**
   ```python
   def test_handles_service_timeout():
       """Test behavior when downstream service times out"""
       with mock_service_timeout():
           result = service.process_with_retry(request)
           assert result['status'] == 'retry_later'
   ```

5. **Keep Integration Tests Focused**
   ```python
   # Test ONE integration point per test
   def test_order_service_calls_inventory_service():
       # Only verify Order→Inventory integration
       pass
   
   def test_order_service_calls_payment_service():
       # Only verify Order→Payment integration
       pass
   ```

### Continuous Integration Testing

Integration tests in CI/CD pipelines:

```yaml
# .github/workflows/integration-tests.yml
name: Integration Tests

on: [push, pull_request]

jobs:
  integration-tests:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:13
        env:
          POSTGRES_PASSWORD: test
          POSTGRES_DB: testdb
        ports:
          - 5432:5432
    
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up Python
        uses: actions/setup-python@v2
        with:
          python-version: '3.9'
      
      - name: Install dependencies
        run: pip install -r requirements.txt
      
      - name: Run integration tests
        run: pytest tests/integration/ -v --tb=short
        env:
          DATABASE_URL: postgresql://postgres:test@localhost:5432/testdb
```

## Key Takeaways

1. **Integration testing** verifies that components work correctly together
2. **Approaches**: Big Bang, Incremental (Top-Down, Bottom-Up), Sandwich
3. **Stubs** simulate lower modules; **Drivers** simulate higher modules
4. **Focus on interfaces** and data flow between components
5. **Different from unit testing** (tests isolation) and system testing (tests entire system)
6. **Essential in CI/CD** for catching integration issues early

## Additional Resources

- [ISTQB Integration Testing](https://www.istqb.org/) - Foundation Level Syllabus coverage
- [Martin Fowler - Integration Testing](https://martinfowler.com/bliki/IntegrationTest.html) - Patterns and practices
- [Testing Strategies in Microservices](https://martinfowler.com/articles/microservice-testing/) - Modern integration testing approaches

