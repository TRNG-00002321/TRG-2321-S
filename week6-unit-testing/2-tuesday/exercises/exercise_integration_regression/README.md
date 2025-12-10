# Challenge: Regression Test Strategy

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Design/Analysis (Mode B) |
| **Prerequisites** | regression-testing.md, re-testing.md |

## Learning Objectives
By completing this exercise, you will:
- Identify code changes that require regression testing
- Distinguish between re-testing and regression testing
- Select appropriate regression test candidates
- Document a regression test strategy
- Apply risk-based test prioritization

## The Scenario

Your team is preparing to release version 2.5 of an e-commerce application. The release includes several changes, and you've been asked to develop a regression testing strategy. You must identify:
1. Which existing tests to run (regression testing)
2. What new tests to add (re-testing the changes)
3. How to prioritize the test execution

## The Codebase Context

Review the provided codebase summary and recent changes:

### Application Structure
```
ecommerce-app/
├── UserModule/
│   ├── UserService.java       # User registration, login, profile
│   ├── AuthService.java       # Authentication, tokens
│   └── UserRepository.java    # Database operations
├── ProductModule/
│   ├── ProductService.java    # Product CRUD, search
│   ├── InventoryService.java  # Stock management
│   └── PricingService.java    # Prices, discounts
├── OrderModule/
│   ├── OrderService.java      # Order processing
│   ├── CartService.java       # Shopping cart
│   └── PaymentService.java    # Payment integration
└── NotificationModule/
    ├── EmailService.java      # Email notifications
    └── SmsService.java        # SMS notifications
```

### Recent Changes for v2.5

| Change ID | Description | Modified Files |
|-----------|-------------|----------------|
| CH-001 | Fixed discount calculation bug | `PricingService.java` |
| CH-002 | Added new payment method (Apple Pay) | `PaymentService.java` |
| CH-003 | Improved email template for orders | `EmailService.java` |
| CH-004 | Refactored UserService for performance | `UserService.java`, `AuthService.java` |
| CH-005 | Added bulk product import feature | `ProductService.java`, `InventoryService.java` |

## Core Tasks

### Task 1: Impact Analysis (15 minutes)

For each change, identify:
1. **Direct Impact**: What functionality changed?
2. **Indirect Impact**: What other modules might be affected?
3. **Risk Level**: High / Medium / Low

Complete the analysis table in `templates/impact_analysis.md`:

```markdown
| Change ID | Direct Impact | Indirect Impact | Risk Level |
|-----------|---------------|-----------------|------------|
| CH-001 | Discount calculations | Cart totals, Order totals | HIGH |
| CH-002 | ? | ? | ? |
| ... | ... | ... | ... |
```

### Task 2: Re-Testing Plan (10 minutes)

For each change, list the NEW tests needed to verify the fix/feature:

```markdown
## CH-001: Discount Calculation Bug Fix
### New Tests Required
1. `testPercentageDiscount_roundingCorrect()`
2. `testCombinedDiscounts_appliedInOrder()`
3. `testMaxDiscountCap_enforced()`
```

### Task 3: Regression Test Selection (15 minutes)

Identify EXISTING tests that should be run to ensure changes didn't break anything:

```markdown
## Regression Tests for CH-001: Pricing Changes
### Directly Related Tests
- [ ] PricingServiceTest (all tests)
- [ ] CartServiceTest.testCalculateTotal_*

### Potentially Affected Tests
- [ ] OrderServiceTest.testOrderTotal_*
- [ ] CheckoutFlowTest.testCompleteCheckout_*

### Unaffected Tests (Skip)
- [ ] UserServiceTest (no pricing involvement)
- [ ] SmsServiceTest (no pricing involvement)
```

### Task 4: Test Prioritization (10 minutes)

Create a prioritized execution order based on:
- **Business criticality** (payment > notifications)
- **Failure likelihood** (direct changes > indirect)
- **Execution time** (fast tests first)

```markdown
## Prioritized Test Execution Order

### Priority 1: Smoke Tests (5 min)
Run immediately after build:
- User login
- Product search
- Add to cart
- Checkout (minimal)

### Priority 2: Change-Specific Tests (15 min)
New tests for v2.5 changes

### Priority 3: High-Risk Regression (30 min)
- All PaymentService tests
- All PricingService tests

### Priority 4: Full Regression (2 hours)
Complete test suite
```

### Task 5: Documentation (10 minutes)

Create a one-page regression test strategy document using `templates/strategy.md`:

```markdown
# Regression Test Strategy: Release v2.5

## Scope
- Changes included: CH-001 through CH-005
- Test coverage requirement: 100% of modified code
- Regression scope: Related modules + smoke tests

## Entry Criteria
- All code changes merged to release branch
- Build successful
- Unit tests passing

## Exit Criteria
- All Priority 1-3 tests passing
- No Critical or High severity bugs
- Test report approved by QA Lead

## Risk Mitigation
- CH-004 (UserService refactor) is highest risk
- Recommendation: Dedicated exploratory testing session
```

## Deliverables

Complete the following files in `templates/`:

1. **impact_analysis.md** - Impact analysis table
2. **retest_plan.md** - New tests for each change
3. **regression_selection.md** - Selected regression tests
4. **execution_priority.md** - Prioritized test order
5. **strategy.md** - Overall strategy document

## Definition of Done

- [ ] Impact analysis completed for all 5 changes
- [ ] Re-testing plan identifies at least 3 new tests per change
- [ ] Regression selection covers direct and indirect impacts
- [ ] Prioritization includes at least 4 priority levels
- [ ] Strategy document has entry/exit criteria
- [ ] All analysis is justified with clear reasoning

## Key Concepts

### Re-Testing vs Regression Testing

| Re-Testing | Regression Testing |
|------------|-------------------|
| Tests the FIX directly | Tests EXISTING functionality |
| Confirms defect is fixed | Confirms nothing broke |
| Focuses on changed code | Focuses on unchanged code |
| Always required | Based on risk assessment |

### Risk-Based Test Selection

```
High Risk (Always Test):
- Payment processing
- Security/Authentication
- Data integrity

Medium Risk (Test if related):
- Core business logic
- Integrations

Low Risk (Test in full regression):
- UI cosmetic changes
- Non-critical features
```

## Submission

Commit with message:
```
feat(week6): Complete regression test strategy exercise
```

