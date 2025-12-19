# Exercise 3: Integration Test Design

## Objective

Design integration tests for a multi-component system, identify integration points, and create test cases that verify component interactions.

## Learning Goals

- Identify integration points between components
- Design integration tests for specific interfaces
- Choose appropriate integration testing approaches
- Create stubs and drivers conceptually
- Document component dependencies

## Time Estimate

45 minutes

---

## The System: Order Processing Pipeline

### System Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        Order Processing System                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                 │
│  │   Web UI    │───►│   Order     │───►│  Inventory  │                 │
│  │  (React)    │    │   Service   │    │   Service   │                 │
│  └─────────────┘    └──────┬──────┘    └──────┬──────┘                 │
│                            │                   │                         │
│                            ▼                   ▼                         │
│                     ┌─────────────┐    ┌─────────────┐                 │
│                     │   Payment   │    │  Warehouse  │                 │
│                     │   Service   │    │   Service   │                 │
│                     └──────┬──────┘    └─────────────┘                 │
│                            │                                             │
│                            ▼                                             │
│                     ┌─────────────┐    ┌─────────────┐                 │
│                     │   Stripe    │    │Notification │                 │
│                     │   (External)│    │   Service   │                 │
│                     └─────────────┘    └─────────────┘                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Component Descriptions

| Component | Responsibility | Dependencies |
|-----------|---------------|--------------|
| **Web UI** | User interface for placing orders | Order Service |
| **Order Service** | Process orders, coordinate workflow | Inventory, Payment, Notification |
| **Inventory Service** | Manage stock levels, reserve items | Warehouse Service, Database |
| **Payment Service** | Handle payment processing | Stripe API |
| **Warehouse Service** | Manage physical inventory, shipping | Database |
| **Notification Service** | Send emails, SMS, push notifications | SendGrid, Twilio |

---

## Core Tasks

### Task 1: Identify Integration Points (15 minutes)

Complete the integration point matrix:

```markdown
# Integration Points Analysis

## Component Integration Matrix

| Source Component | Target Component | Interface Type | Data Exchanged | Protocol |
|-----------------|------------------|----------------|----------------|----------|
| Web UI | Order Service | REST API | Order details (items, quantity, user) | HTTPS |
| Order Service | Inventory Service | | | |
| Order Service | Payment Service | | | |
| Inventory Service | Warehouse Service | | | |
| Payment Service | Stripe | | | |
| Order Service | Notification Service | | | |

## Critical Integration Points (Ranked by Risk)

| Rank | Integration Point | Risk Level | Failure Impact |
|------|-------------------|------------|----------------|
| 1 | Order → Payment | High | Customer charged but order fails |
| 2 | | | |
| 3 | | | |
| 4 | | | |
| 5 | | | |
```

### Task 2: Select Integration Testing Approach (10 minutes)

Analyze which integration approach to use:

```markdown
# Integration Testing Approach Selection

## Available Approaches

1. **Big Bang:** Test all components together at once
2. **Top-Down:** Start from UI, stub lower components
3. **Bottom-Up:** Start from database, use drivers for upper components
4. **Sandwich:** Combine top-down and bottom-up

## Approach Analysis for Order Processing System

### Recommended Approach: _______________

**Justification:**




### Integration Order:

| Phase | Components to Integrate | Stubs Needed | Drivers Needed |
|-------|------------------------|--------------|----------------|
| 1 | | | |
| 2 | | | |
| 3 | | | |
| 4 | | | |
```

### Task 3: Design Integration Test Cases (15 minutes)

Create 5 integration test cases for the **Order Service ↔ Payment Service** integration:

```markdown
# Integration Test Cases: Order-Payment Integration

---

## INT-OP-001: Successful Payment Processing

**Components Under Test:** Order Service, Payment Service

**Integration Point:** Order Service calls Payment Service to process payment

**Preconditions:**
- Order Service is running and accessible
- Payment Service is running with Stripe test mode
- Valid test order created with total $50.00

**Test Steps:**

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Order Service sends payment request with order ID, amount $50.00, card token | Payment Service receives request |
| 2 | Payment Service calls Stripe API with card token | Stripe returns success response |
| 3 | Payment Service returns success status to Order Service | Order Service receives payment confirmation |
| 4 | Order Service updates order status to "PAID" | Database shows order status = PAID |

**Verification Points:**
- Payment Service correctly formats Stripe API request
- Error responses are properly propagated
- Transaction ID is returned and stored
- Both services log the transaction

**Test Data:**
- Order ID: TEST-001
- Amount: $50.00
- Card Token: tok_visa (Stripe test token)

---

## INT-OP-002: Payment Failure - Card Declined

**Components Under Test:** Order Service, Payment Service

**Integration Point:** 

**Preconditions:**
- 

**Test Steps:**

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | | |
| 2 | | |
| 3 | | |

**Verification Points:**
- 
- 

**Test Data:**
- Card Token: tok_chargeDeclined (Stripe test token for decline)

---

## INT-OP-003: Payment Service Timeout

<!-- Design a test for timeout scenario -->

---

## INT-OP-004: Partial Amount Payment

<!-- Design a test for split/partial payment -->

---

## INT-OP-005: Refund Processing

<!-- Design a test for refund flow -->
```

### Task 4: Document Stubs and Drivers Needed (5 minutes)

Define the stubs and drivers required for isolated testing:

```markdown
# Stubs and Drivers Specification

## Stubs (Replace Lower-Level Components)

### Stripe API Stub
**Purpose:** Simulate Stripe responses without actual API calls
**Responses to Simulate:**
- [ ] Successful charge
- [ ] Card declined
- [ ] Insufficient funds
- [ ] Network timeout
- [ ] Invalid card token

**Implementation Notes:**




### Warehouse Service Stub
**Purpose:** 
**Responses to Simulate:**
- [ ] 
- [ ] 
- [ ] 

---

## Drivers (Simulate Higher-Level Callers)

### Order Service Driver
**Purpose:** Simulate Order Service calls to test Payment Service in isolation
**Scenarios to Trigger:**
- [ ] Valid payment request
- [ ] Missing required fields
- [ ] Invalid amount (negative, zero)
- [ ] Duplicate payment attempt

**Implementation Notes:**




### Web UI Driver
**Purpose:** 
**Scenarios to Trigger:**
- [ ] 
- [ ] 
```

---

## Integration Approach Reference

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    Integration Testing Approaches                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  BIG BANG:    [A] + [B] + [C] + [D] → Test all at once                 │
│               Pros: Simple setup                                         │
│               Cons: Hard to isolate failures                            │
│                                                                          │
│  TOP-DOWN:    [A] → [B-stub] → [C-stub] → [D-stub]                     │
│               Replace stubs gradually with real components               │
│               Pros: UI works early, find high-level issues              │
│               Cons: Low-level bugs found late                           │
│                                                                          │
│  BOTTOM-UP:   [D-driver] → [C-driver] → [B-driver] → [A]               │
│               Pros: Core functionality tested first                      │
│               Cons: UI tested last                                       │
│                                                                          │
│  SANDWICH:    Top: [A] → [B-stub]                                       │
│               Bottom: [D] → [C-driver]                                   │
│               Meet in middle                                             │
│               Pros: Parallel development                                 │
│               Cons: Complex coordination                                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Definition of Done

- [ ] All integration points identified in matrix
- [ ] Critical integration points ranked by risk
- [ ] Integration approach selected with justification
- [ ] Integration order defined with stub/driver needs
- [ ] 5 integration test cases completed
- [ ] Stubs and drivers documented for at least 2 components
- [ ] Each test case includes verification points

---

## Hints

<details>
<summary>Hint: Identifying Integration Points</summary>

Look for:
- API calls between services
- Database connections
- Message queue interactions
- External API integrations
- Event subscriptions/publications
</details>

<details>
<summary>Hint: Stub Response Design</summary>

A good stub should simulate:
- Happy path responses
- Error responses (4xx, 5xx)
- Edge cases (empty data, large payloads)
- Timeout scenarios
- Rate limiting responses
</details>

