# Exercise 1: Test Level Classification

## Objective

Given a set of test scenarios, classify each as unit, integration, or system test with justification. Develop critical thinking about where tests belong in the testing hierarchy.

## Learning Goals

- Distinguish between unit, integration, and system testing
- Recognize test characteristics that indicate the appropriate level
- Justify test level decisions with clear reasoning
- Understand the implications of testing at different levels

## Time Estimate

30 minutes

---

## The Scenario

You're a QA engineer reviewing test scenarios for an e-commerce platform. The platform includes:
- User authentication service
- Product catalog service
- Shopping cart functionality
- Payment processing integration
- Order management system
- Email notification service

## Core Tasks

### Task 1: Classify the Test Scenarios (20 minutes)

For each test scenario below, determine the appropriate test level and provide justification.

**Complete the classification table:**

| # | Test Scenario | Test Level | Justification |
|---|--------------|------------|---------------|
| 1 | Verify the `calculateTax()` function returns correct tax amount for a given product price | | |
| 2 | Verify user can browse products, add to cart, and complete checkout with credit card payment | | |
| 3 | Verify the shopping cart service correctly retrieves product prices from the product catalog service | | |
| 4 | Verify the `validateEmail()` function correctly identifies valid and invalid email formats | | |
| 5 | Verify order confirmation emails are sent when payment is successfully processed | | |
| 6 | Verify the user authentication service correctly generates JWT tokens after validating credentials against the database | | |
| 7 | Verify the `formatCurrency()` utility function displays prices in correct format | | |
| 8 | Verify a new user can register, receive verification email, confirm account, and log in | | |
| 9 | Verify the payment processing module correctly handles Stripe API responses (success, failure, timeout) | | |
| 10 | Verify the `sortProducts()` function correctly sorts an array of products by price | | |
| 11 | Verify the order service updates inventory counts after payment confirmation from the payment service | | |
| 12 | Verify the system handles 1000 concurrent users browsing and adding items to cart | | |

### Task 2: Analyze Your Classifications (5 minutes)

Answer the following questions:

**Q1: What characteristics made you classify a test as a unit test?**
```
Your answer:



```

**Q2: What characteristics indicated integration testing?**
```
Your answer:



```

**Q3: What characteristics indicated system testing?**
```
Your answer:



```

**Q4: Were any scenarios ambiguous? Which ones and why?**
```
Your answer:



```

### Task 3: Create Additional Scenarios (5 minutes)

Write one original test scenario for each level:

**Unit Test Scenario:**
```
Your scenario:


```

**Integration Test Scenario:**
```
Your scenario:


```

**System Test Scenario:**
```
Your scenario:


```

---

## Classification Guide

Use this reference to help with your classifications:

### Unit Test Indicators
- Tests a single function or method in isolation
- Uses mocks/stubs for dependencies
- Fast execution (milliseconds)
- No external systems (database, network, files)
- Tests code correctness, not business workflow

### Integration Test Indicators
- Tests interaction between 2+ components
- Real connections between services
- May use test database
- Verifies data flow between modules
- Tests interface contracts

### System Test Indicators
- Tests complete end-to-end workflows
- All components integrated
- Tests from user perspective
- Production-like environment
- Verifies business requirements

---

## Definition of Done

- [ ] All 12 test scenarios classified
- [ ] Each classification includes a 1-2 sentence justification
- [ ] Analysis questions answered thoughtfully
- [ ] Three original scenarios created (one per level)

---

## Sample Answer (Scenario 1 only)

| # | Test Scenario | Test Level | Justification |
|---|--------------|------------|---------------|
| 1 | Verify the `calculateTax()` function returns correct tax amount for a given product price | **Unit Test** | This tests a single function in isolation with no external dependencies. It verifies the algorithm correctness with input/output validation. |

*Complete the remaining 11 scenarios yourself.*

