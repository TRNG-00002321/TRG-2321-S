# Lab: Allure Python Integration - Enhancing Pytest Tests

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Beginner-Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | allure.md, demo_allure_pytest_setup.py |

## Learning Objectives
By completing this exercise, you will:
- Install and configure allure-pytest
- Use Allure decorators to enhance pytest tests
- Organize tests with `@allure.epic`, `@allure.feature`, `@allure.story`
- Add descriptions, severity, and links
- Generate and view Allure reports

## The Scenario

Your Python test suite for an `OrderService` needs professional reporting for stakeholder reviews. You'll add Allure decorators to transform basic test output into interactive, documented reports.

## Core Tasks

### Task 1: Install allure-pytest (5 minutes)

```bash
# Install allure-pytest
pip install allure-pytest

# Verify installation
pip show allure-pytest
```

Update `pytest.ini` or `pyproject.toml`:

```ini
# pytest.ini
[pytest]
addopts = --alluredir=allure-results
```

Or in `pyproject.toml`:
```toml
[tool.pytest.ini_options]
addopts = "--alluredir=allure-results"
```

### Task 2: Add Epic/Feature/Story Decorators (15 minutes)

Organize tests hierarchically:

```python
import allure
import pytest
from order_service import OrderService


@allure.epic("E-Commerce Platform")
@allure.feature("Order Management")
class TestOrderService:
    
    @allure.story("Create new order")
    @allure.title("Successfully create order with valid items")
    @allure.description("""
    Tests the order creation flow:
    1. Add items to order
    2. Calculate total
    3. Create order record
    4. Return order ID
    """)
    @allure.severity(allure.severity_level.CRITICAL)
    def test_create_order_success(self):
        service = OrderService()
        order_id = service.create_order(
            items=[{"sku": "ITEM1", "qty": 2}],
            customer_id=123
        )
        assert order_id is not None
    
    @allure.story("Order validation")
    @allure.title("Reject order with empty items")
    @allure.severity(allure.severity_level.NORMAL)
    def test_create_order_empty_items_fails(self):
        service = OrderService()
        with pytest.raises(ValueError, match="Items required"):
            service.create_order(items=[], customer_id=123)
```

### Task 3: Add Steps with allure.step (15 minutes)

Break down complex tests:

```python
@allure.story("Complete order flow")
@allure.title("Process order from creation to fulfillment")
def test_complete_order_flow(self):
    order_id = self._create_order()
    self._add_payment(order_id)
    self._fulfill_order(order_id)
    self._verify_completion(order_id)

@allure.step("Create order with items")
def _create_order(self):
    with allure.step("Initialize order service"):
        service = OrderService()
    
    with allure.step("Add items to order"):
        items = [{"sku": "SKU1", "qty": 1}]
    
    with allure.step("Submit order"):
        order_id = service.create_order(items=items, customer_id=1)
    
    allure.attach(
        str(order_id),
        name="Created Order ID",
        attachment_type=allure.attachment_type.TEXT
    )
    
    return order_id

@allure.step("Add payment for order: {order_id}")
def _add_payment(self, order_id):
    service = OrderService()
    service.add_payment(order_id, amount=100.00)

@allure.step("Fulfill order: {order_id}")
def _fulfill_order(self, order_id):
    service = OrderService()
    service.fulfill(order_id)

@allure.step("Verify order completion")
def _verify_completion(self, order_id):
    service = OrderService()
    order = service.get_order(order_id)
    assert order.status == "COMPLETED"
```

### Task 4: Add Dynamic Titles and Links (10 minutes)

```python
@allure.story("Order cancellation")
@allure.link("https://jira.company.com/browse/ORD-123", name="User Story")
@allure.issue("BUG-456", "Fix cancel race condition")
@allure.testcase("TC-789", "Order Cancel Test Case")
def test_cancel_order(self):
    # Dynamic title based on test data
    allure.dynamic.title(f"Cancel order in PENDING status")
    allure.dynamic.description("Tests that pending orders can be cancelled")
    
    service = OrderService()
    order_id = service.create_order(items=[{"sku": "X", "qty": 1}], customer_id=1)
    
    result = service.cancel_order(order_id)
    
    assert result is True


@allure.story("Order queries")
@pytest.mark.parametrize("status,expected_count", [
    ("PENDING", 5),
    ("COMPLETED", 10),
    ("CANCELLED", 2),
])
def test_get_orders_by_status(self, status, expected_count):
    allure.dynamic.title(f"Get orders with status: {status}")
    
    service = OrderService()
    orders = service.get_orders_by_status(status)
    
    # Note: This would fail without proper setup
    # Just demonstrating dynamic titles
    assert isinstance(orders, list)
```

### Task 5: Generate and View Report (10 minutes)

Run tests and generate report:

```bash
# Run tests with allure
pytest --alluredir=allure-results

# View report (requires allure command-line tool)
allure serve allure-results

# Generate static report
allure generate allure-results -o allure-report --clean
```

## Provided Test Class to Enhance

```python
# test_order_service.py - ADD ALLURE DECORATORS
import pytest
from order_service import OrderService, OrderNotFoundError


class TestOrderService:
    
    @pytest.fixture(autouse=True)
    def setup(self):
        self.service = OrderService()
    
    def test_create_order_returns_id(self):
        order_id = self.service.create_order(
            items=[{"sku": "SKU1", "qty": 1}],
            customer_id=100
        )
        assert order_id is not None
        assert isinstance(order_id, int)
    
    def test_create_order_empty_items_raises(self):
        with pytest.raises(ValueError):
            self.service.create_order(items=[], customer_id=100)
    
    def test_get_order_existing_returns_order(self):
        order_id = self.service.create_order(
            items=[{"sku": "SKU1", "qty": 1}],
            customer_id=100
        )
        order = self.service.get_order(order_id)
        assert order is not None
        assert order.id == order_id
    
    def test_get_order_nonexistent_raises(self):
        with pytest.raises(OrderNotFoundError):
            self.service.get_order(99999)
    
    def test_cancel_order_pending_succeeds(self):
        order_id = self.service.create_order(
            items=[{"sku": "SKU1", "qty": 1}],
            customer_id=100
        )
        result = self.service.cancel_order(order_id)
        assert result is True
    
    def test_cancel_order_completed_fails(self):
        order_id = self.service.create_order(
            items=[{"sku": "SKU1", "qty": 1}],
            customer_id=100
        )
        self.service.complete_order(order_id)
        
        with pytest.raises(ValueError, match="Cannot cancel"):
            self.service.cancel_order(order_id)
    
    def test_calculate_total_multiple_items(self):
        order_id = self.service.create_order(
            items=[
                {"sku": "SKU1", "qty": 2, "price": 10.00},
                {"sku": "SKU2", "qty": 1, "price": 25.00}
            ],
            customer_id=100
        )
        order = self.service.get_order(order_id)
        assert order.total == 45.00  # (2*10) + (1*25)
```

## Allure Python Decorators Reference

| Decorator | Purpose | Example |
|-----------|---------|---------|
| `@allure.epic()` | Top-level grouping | `@allure.epic("Platform")` |
| `@allure.feature()` | Feature grouping | `@allure.feature("Orders")` |
| `@allure.story()` | User story | `@allure.story("Create order")` |
| `@allure.title()` | Test title | `@allure.title("My test")` |
| `@allure.description()` | Description | `@allure.description("...")` |
| `@allure.severity()` | Importance | `@allure.severity(CRITICAL)` |
| `@allure.step()` | Test step | `@allure.step("Click")` |
| `@allure.link()` | External link | `@allure.link(url)` |
| `@allure.issue()` | Bug reference | `@allure.issue("BUG-1")` |

## Definition of Done

- [ ] `allure-pytest` installed and configured
- [ ] All test methods have `@allure.story` and `@allure.title`
- [ ] Tests organized with `@allure.epic` and `@allure.feature`
- [ ] Severity levels set for all tests
- [ ] At least 1 test with `allure.step` context managers
- [ ] At least 1 test with dynamic title
- [ ] Report generated and viewable
- [ ] All tests pass

## Submission

Commit with message:
```
feat(week6): Complete Allure Python integration exercise
```

