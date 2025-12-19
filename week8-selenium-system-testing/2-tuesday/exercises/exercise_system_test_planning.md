# Exercise 2: System Test Planning

## Objective

Create a comprehensive system test plan for a provided application specification, including test cases, environment requirements, and entry/exit criteria.

## Learning Goals

- Design system test cases from requirements
- Define appropriate test environment specifications
- Establish meaningful entry and exit criteria
- Apply test design techniques (equivalence partitioning, boundary analysis)
- Prioritize tests based on risk

## Time Estimate

60 minutes

---

## The Application: BookShelf Online

### Application Overview

**BookShelf Online** is a web-based bookstore application with the following features:

1. **User Management**
   - User registration with email verification
   - Login/logout functionality
   - Password reset via email
   - User profile management

2. **Product Catalog**
   - Browse books by category
   - Search books by title, author, ISBN
   - View book details (price, description, reviews)
   - Filter and sort results

3. **Shopping Cart**
   - Add/remove books from cart
   - Update quantities
   - Apply discount codes
   - View cart total

4. **Checkout Process**
   - Shipping address entry
   - Payment processing (credit card, PayPal)
   - Order confirmation
   - Email receipt

5. **Order Management**
   - View order history
   - Track order status
   - Cancel pending orders
   - Request returns

### Technical Stack
- Frontend: React web application
- Backend: REST API (Node.js)
- Database: PostgreSQL
- Payment: Stripe integration
- Email: SendGrid
- Hosting: AWS

---

## Core Tasks

### Task 1: Define Test Environment Requirements (15 minutes)

Complete the environment specification:

```markdown
# System Test Environment Specification

## Hardware Requirements
| Component | Specification | Purpose |
|-----------|--------------|---------|
| Web Server | | |
| Database Server | | |
| Application Server | | |

## Software Requirements
| Software | Version | Configuration |
|----------|---------|---------------|
| Browser(s) | | |
| Operating System | | |
| Database | | |

## External Services
| Service | Test Environment Setup | Notes |
|---------|----------------------|-------|
| Payment Gateway | | |
| Email Service | | |

## Test Data Requirements
| Data Type | Volume | Source |
|-----------|--------|--------|
| User accounts | | |
| Product catalog | | |
| Order history | | |
```

### Task 2: Establish Entry and Exit Criteria (10 minutes)

Define when system testing should start and end:

```markdown
# Entry Criteria

## Prerequisites for Starting System Testing:
1. 
2. 
3. 
4. 
5. 

## Required Documentation:
1. 
2. 
3. 

---

# Exit Criteria

## Conditions for Completing System Testing:
1. 
2. 
3. 
4. 
5. 

## Quality Gates:
| Metric | Target | Actual |
|--------|--------|--------|
| Test Execution Rate | % | |
| Pass Rate | % | |
| Critical Defects Open | | |
| High Defects Open | | |
```

### Task 3: Design System Test Cases (25 minutes)

Create detailed test cases for the **Checkout Process** feature. Complete 5 test cases using the template:

```markdown
# System Test Cases: Checkout Process

---

## Test Case: SYS-CHECKOUT-001

**Name:** Successful checkout with credit card

**Priority:** High

**Test Type:** Functional

**Preconditions:**
- User is logged in
- Shopping cart contains at least 1 item
- 

**Test Steps:**
| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Navigate to checkout page | |
| 2 | | |
| 3 | | |
| 4 | | |
| 5 | | |

**Postconditions:**
- 
- 

**Test Data:**
- 
- 

---

## Test Case: SYS-CHECKOUT-002

**Name:** <!-- Your test case name -->

**Priority:** 

**Test Type:** 

**Preconditions:**
- 

**Test Steps:**
| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | | |
| 2 | | |
| 3 | | |

**Postconditions:**
- 

**Test Data:**
- 

---

<!-- Create 3 more test cases (SYS-CHECKOUT-003, 004, 005) -->
<!-- Include at least one negative test case and one boundary case -->
```

### Task 4: Create Test Summary Matrix (10 minutes)

Summarize all planned system tests:

```markdown
# System Test Summary Matrix

## Test Coverage by Feature

| Feature | Total Tests | High Priority | Medium | Low | Automated |
|---------|-------------|---------------|--------|-----|-----------|
| User Management | | | | | |
| Product Catalog | | | | | |
| Shopping Cart | | | | | |
| Checkout Process | | | | | |
| Order Management | | | | | |
| **TOTAL** | | | | | |

## Test Coverage by Type

| Test Type | Count | Examples |
|-----------|-------|----------|
| Functional | | |
| Security | | |
| Performance | | |
| Usability | | |
| Compatibility | | |

## Risk-Based Priority

| Risk Area | Risk Level | Mitigation (Tests) |
|-----------|------------|-------------------|
| Payment Processing | High | |
| User Data Security | High | |
| | | |
| | | |
```

---

## Templates

Use the template file: `templates/system_test_plan_template.md`

## Definition of Done

- [ ] Environment specification completed with all sections
- [ ] Entry criteria includes at least 5 prerequisites
- [ ] Exit criteria includes quality gate metrics
- [ ] 5 detailed test cases for Checkout Process created
- [ ] At least one negative test case included
- [ ] At least one boundary test case included
- [ ] Test summary matrix shows coverage by feature and type
- [ ] Risk-based priorities documented

---

## Hints

<details>
<summary>Hint: Test Environment External Services</summary>

For the payment gateway, consider:
- Stripe test mode API keys
- Sandbox environment URLs
- Test credit card numbers (4242 4242 4242 4242)

For email service:
- SendGrid sandbox mode
- Email catching service (Mailtrap, Mailhog)
</details>

<details>
<summary>Hint: Entry Criteria Examples</summary>

- Integration testing complete with >95% pass rate
- Test environment deployed and smoke tested
- Test data prepared and loaded
- All P1/P2 integration bugs resolved
- System design documentation approved
</details>

<details>
<summary>Hint: Checkout Test Ideas</summary>

Consider tests for:
- Successful payment with different cards
- Payment declined scenarios
- Session timeout during checkout
- Empty cart checkout attempt
- Invalid discount code
- Network failure during payment
- Concurrent checkout (race condition)
</details>

