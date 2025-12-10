# Capstone Project: Full Test Suite with Unit Tests, Mocks, and Allure Reporting

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Advanced |
| **Time Estimate** | 2-3 hours |
| **Mode** | Individual Capstone |
| **Prerequisites** | All Week 6 content |

## Learning Objectives
By completing this capstone, you will demonstrate mastery of:
- JUnit5 and Pytest fundamentals
- Mockito and pytest-mock for test isolation
- Parameterized and data-driven testing
- Test lifecycle management
- Allure reporting integration
- Professional test documentation

## The Scenario

You're the QA Engineer for **ShopEasy**, an e-commerce platform. The development team has delivered a new `CheckoutService` that needs comprehensive test coverage before release. Your task is to create a complete test suite that:

1. Tests all functionality in both Java and Python
2. Achieves high code coverage through mocking
3. Is well-documented with Allure reporting
4. Follows professional testing practices

## The System Under Test

### Java Implementation

```java
// CheckoutService.java
public class CheckoutService {
    private final InventoryService inventoryService;
    private final PaymentGateway paymentGateway;
    private final ShippingCalculator shippingCalculator;
    private final EmailService emailService;
    private final OrderRepository orderRepository;
    
    public CheckoutService(
        InventoryService inventoryService,
        PaymentGateway paymentGateway,
        ShippingCalculator shippingCalculator,
        EmailService emailService,
        OrderRepository orderRepository
    ) {
        this.inventoryService = inventoryService;
        this.paymentGateway = paymentGateway;
        this.shippingCalculator = shippingCalculator;
        this.emailService = emailService;
        this.orderRepository = orderRepository;
    }
    
    public Order checkout(Cart cart, PaymentInfo payment, ShippingAddress address) {
        // 1. Validate cart
        if (cart.isEmpty()) {
            throw new EmptyCartException("Cannot checkout empty cart");
        }
        
        // 2. Check inventory
        for (CartItem item : cart.getItems()) {
            if (!inventoryService.isAvailable(item.getSku(), item.getQuantity())) {
                throw new InsufficientInventoryException(
                    "Item " + item.getSku() + " not available in requested quantity");
            }
        }
        
        // 3. Calculate totals
        BigDecimal subtotal = cart.getSubtotal();
        BigDecimal shipping = shippingCalculator.calculate(address, cart.getTotalWeight());
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.08")); // 8% tax
        BigDecimal total = subtotal.add(shipping).add(tax);
        
        // 4. Process payment
        PaymentResult paymentResult = paymentGateway.charge(payment, total);
        if (!paymentResult.isSuccessful()) {
            throw new PaymentFailedException(paymentResult.getErrorMessage());
        }
        
        // 5. Reserve inventory
        for (CartItem item : cart.getItems()) {
            inventoryService.reserve(item.getSku(), item.getQuantity());
        }
        
        // 6. Create order
        Order order = new Order();
        order.setItems(cart.getItems());
        order.setSubtotal(subtotal);
        order.setShipping(shipping);
        order.setTax(tax);
        order.setTotal(total);
        order.setTransactionId(paymentResult.getTransactionId());
        order.setStatus(OrderStatus.CONFIRMED);
        
        Order savedOrder = orderRepository.save(order);
        
        // 7. Send confirmation email
        emailService.sendOrderConfirmation(savedOrder, address.getEmail());
        
        return savedOrder;
    }
    
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        
        if (order.getStatus() == OrderStatus.SHIPPED) {
            throw new InvalidOperationException("Cannot cancel shipped order");
        }
        
        // Refund payment
        paymentGateway.refund(order.getTransactionId(), order.getTotal());
        
        // Release inventory
        for (CartItem item : order.getItems()) {
            inventoryService.release(item.getSku(), item.getQuantity());
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}
```

### Python Implementation

```python
# checkout_service.py
from dataclasses import dataclass
from decimal import Decimal
from typing import List, Optional
from enum import Enum


class OrderStatus(Enum):
    PENDING = "pending"
    CONFIRMED = "confirmed"
    SHIPPED = "shipped"
    CANCELLED = "cancelled"


@dataclass
class CartItem:
    sku: str
    name: str
    quantity: int
    price: Decimal
    weight: float


@dataclass
class Cart:
    items: List[CartItem]
    
    def is_empty(self) -> bool:
        return len(self.items) == 0
    
    def get_subtotal(self) -> Decimal:
        return sum(item.price * item.quantity for item in self.items)
    
    def get_total_weight(self) -> float:
        return sum(item.weight * item.quantity for item in self.items)


@dataclass
class Order:
    id: Optional[int] = None
    items: List[CartItem] = None
    subtotal: Decimal = Decimal("0")
    shipping: Decimal = Decimal("0")
    tax: Decimal = Decimal("0")
    total: Decimal = Decimal("0")
    transaction_id: Optional[str] = None
    status: OrderStatus = OrderStatus.PENDING


class CheckoutService:
    TAX_RATE = Decimal("0.08")  # 8% tax
    
    def __init__(
        self,
        inventory_service,
        payment_gateway,
        shipping_calculator,
        email_service,
        order_repository
    ):
        self.inventory_service = inventory_service
        self.payment_gateway = payment_gateway
        self.shipping_calculator = shipping_calculator
        self.email_service = email_service
        self.order_repository = order_repository
    
    def checkout(self, cart: Cart, payment_info, shipping_address) -> Order:
        # 1. Validate cart
        if cart.is_empty():
            raise EmptyCartError("Cannot checkout empty cart")
        
        # 2. Check inventory
        for item in cart.items:
            if not self.inventory_service.is_available(item.sku, item.quantity):
                raise InsufficientInventoryError(
                    f"Item {item.sku} not available in requested quantity"
                )
        
        # 3. Calculate totals
        subtotal = cart.get_subtotal()
        shipping = self.shipping_calculator.calculate(
            shipping_address, cart.get_total_weight()
        )
        tax = subtotal * self.TAX_RATE
        total = subtotal + shipping + tax
        
        # 4. Process payment
        payment_result = self.payment_gateway.charge(payment_info, total)
        if not payment_result.successful:
            raise PaymentFailedError(payment_result.error_message)
        
        # 5. Reserve inventory
        for item in cart.items:
            self.inventory_service.reserve(item.sku, item.quantity)
        
        # 6. Create order
        order = Order(
            items=cart.items,
            subtotal=subtotal,
            shipping=shipping,
            tax=tax,
            total=total,
            transaction_id=payment_result.transaction_id,
            status=OrderStatus.CONFIRMED
        )
        
        saved_order = self.order_repository.save(order)
        
        # 7. Send confirmation
        self.email_service.send_order_confirmation(
            saved_order, shipping_address.email
        )
        
        return saved_order
    
    def cancel_order(self, order_id: int) -> Order:
        order = self.order_repository.find_by_id(order_id)
        if order is None:
            raise OrderNotFoundError(f"Order not found: {order_id}")
        
        if order.status == OrderStatus.SHIPPED:
            raise InvalidOperationError("Cannot cancel shipped order")
        
        # Refund and release
        self.payment_gateway.refund(order.transaction_id, order.total)
        for item in order.items:
            self.inventory_service.release(item.sku, item.quantity)
        
        order.status = OrderStatus.CANCELLED
        return self.order_repository.save(order)


# Custom Exceptions
class EmptyCartError(Exception): pass
class InsufficientInventoryError(Exception): pass
class PaymentFailedError(Exception): pass
class OrderNotFoundError(Exception): pass
class InvalidOperationError(Exception): pass
```

## Capstone Requirements

### Part 1: Java Test Suite (40%)

Create `CheckoutServiceTest.java` with:

1. **Mockito Setup**
   - Mock all dependencies
   - Use `@ExtendWith(MockitoExtension.class)`

2. **Test Coverage** (minimum)
   - Happy path checkout
   - Empty cart rejection
   - Inventory check failure
   - Payment failure handling
   - Order cancellation success
   - Shipped order cancellation rejection

3. **Advanced Features**
   - Use `@ParameterizedTest` for multiple scenarios
   - Use `ArgumentCaptor` to verify email content
   - Use `InOrder` to verify operation sequence

4. **Allure Annotations**
   - Organize with `@Epic`, `@Feature`, `@Story`
   - Add `@Description` and `@Severity`
   - Include `@Step` methods

### Part 2: Python Test Suite (40%)

Create `test_checkout_service.py` with:

1. **pytest-mock Setup**
   - Create fixtures for all mocks
   - Use `conftest.py` for shared fixtures

2. **Test Coverage** (minimum)
   - Same scenarios as Java
   - Use `pytest.raises` for exceptions
   - Use `pytest.mark.parametrize` for data-driven tests

3. **Advanced Features**
   - Use `side_effect` for error simulation
   - Use `mocker.spy` to verify real method calls
   - Test calculation accuracy with `pytest.approx`

4. **Allure Decorators**
   - Organize with `@allure.epic`, `@allure.feature`, `@allure.story`
   - Add `allure.step` for complex flows
   - Include attachments for debugging

### Part 3: Allure Report (20%)

1. **Generate Combined Report**
   - Run both test suites
   - Generate comprehensive report

2. **Documentation**
   - All tests have clear descriptions
   - Steps show business flow
   - Attachments include test data

3. **Report Quality**
   - Tests are organized logically
   - Severity levels are appropriate
   - Links to requirements (simulated)

## Definition of Done

- [ ] Java test class with all required tests
- [ ] Python test class with all required tests
- [ ] All mocks properly configured
- [ ] Parameterized tests for multiple scenarios
- [ ] Exception handling thoroughly tested
- [ ] Verification of method call order
- [ ] Allure annotations/decorators complete
- [ ] Report generated successfully
- [ ] Pass rate: 100% (all tests pass)
- [ ] Code follows best practices

## Evaluation Rubric

| Criteria | Points | Requirements |
|----------|--------|--------------|
| Java Tests | 25 | All scenarios covered, good assertions |
| Python Tests | 25 | All scenarios covered, good assertions |
| Mocking Quality | 15 | Proper isolation, no real dependencies |
| Parameterization | 10 | Data-driven tests for variations |
| Allure Integration | 15 | Well-documented, organized reports |
| Code Quality | 10 | Clean, readable, follows conventions |
| **Total** | **100** | |

## Submission

Create a branch and submit:
```bash
git checkout -b week6-capstone-{your-name}
git add .
git commit -m "feat(week6): Complete unit testing capstone project"
git push origin week6-capstone-{your-name}
```

Include in your commit:
- `src/test/java/CheckoutServiceTest.java`
- `tests/test_checkout_service.py`
- `tests/conftest.py`
- Screenshot of Allure report overview

---

**Good luck! This capstone demonstrates your mastery of Week 6 material.**

