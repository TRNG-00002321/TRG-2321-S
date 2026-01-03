# Testing Types & Techniques Cheat Sheet

## Testing Levels

| Level | Scope | Who | When |
|-------|-------|-----|------|
| **Unit** | Individual functions/methods | Developers | During development |
| **Integration** | Component interactions | Dev/QA | After unit testing |
| **System** | Complete application | QA | After integration |
| **Acceptance** | Business requirements | Users/QA | Before release |

## Testing Types

### Functional Testing
| Type | Description |
|------|-------------|
| Smoke | Quick check of critical functionality |
| Sanity | Focused testing after minor changes |
| Regression | Re-test after changes to ensure nothing broke |
| Retesting | Verify specific bug fix |

### Non-Functional Testing
| Type | Description |
|------|-------------|
| Performance | Response times, throughput |
| Load | Behavior under expected load |
| Stress | Behavior beyond normal capacity |
| Security | Vulnerabilities, authentication |
| Usability | User experience |
| Compatibility | Different browsers/devices |

## Test Design Techniques

### Black-Box Techniques

#### Equivalence Partitioning
Divide inputs into groups with same expected behavior.

```
Age field (valid: 1-120)
Partitions:
- Invalid: < 1 (test: -5)
- Valid: 1-120 (test: 50)
- Invalid: > 120 (test: 150)
```

#### Boundary Value Analysis
Test at edges of partitions.

```
Age field (valid: 1-120)
Test values: 0, 1, 2, 119, 120, 121
```

#### Decision Table
Map conditions to actions.

| Condition | R1 | R2 | R3 | R4 |
|-----------|----|----|----|----|
| Valid username | T | T | F | F |
| Valid password | T | F | T | F |
| **Result** | Login | Error | Error | Error |

#### State Transition
Test state changes.

```
Order States:
New → Processing → Shipped → Delivered
                 → Cancelled
Test transitions between each state.
```

### White-Box Techniques

#### Statement Coverage
Execute every line of code at least once.

#### Branch Coverage
Execute all branches (if/else) in both directions.

#### Path Coverage
Test all possible execution paths.

## Test Case Template

```markdown
**Test Case ID:** TC_LOGIN_001
**Title:** Verify successful login with valid credentials
**Priority:** High
**Preconditions:** User account exists

**Steps:**
1. Navigate to login page
2. Enter valid username
3. Enter valid password
4. Click Login button

**Expected Result:** User is redirected to dashboard

**Actual Result:** [To be filled during execution]
**Status:** Pass / Fail / Blocked
```

## Defect Report Template

```markdown
**Bug ID:** BUG-123
**Title:** Login fails with special characters in password
**Severity:** High
**Priority:** P1

**Environment:** Chrome 120, Windows 11

**Steps to Reproduce:**
1. Go to login page
2. Enter username: testuser
3. Enter password: Pass@#$123
4. Click Login

**Expected:** Successful login
**Actual:** Error message "Invalid request"

**Attachments:** screenshot.png, console_log.txt
```

## Testing Principles (ISTQB)

1. **Testing shows presence of defects** - Can't prove bug-free
2. **Exhaustive testing is impossible** - Use risk-based approach
3. **Early testing saves money** - Shift left
4. **Defect clustering** - 80% bugs in 20% modules
5. **Pesticide paradox** - Same tests find fewer bugs over time
6. **Testing is context dependent** - Approach varies by project
7. **Absence of errors fallacy** - Bug-free ≠ useful

## Test Metrics

| Metric | Formula |
|--------|---------|
| Test Coverage | (Executed / Total) × 100 |
| Defect Density | Defects / KLOC |
| Defect Leakage | (Prod bugs / Total bugs) × 100 |
| Pass Rate | (Passed / Total) × 100 |

## Defect Lifecycle

```
NEW → ASSIGNED → OPEN → FIXED → RETEST → VERIFIED → CLOSED
                  ↓                ↓
              REJECTED         REOPENED
```

## Priority vs Severity

| | High Severity | Low Severity |
|-|---------------|--------------|
| **High Priority** | Fix immediately | Fix soon (visible issue) |
| **Low Priority** | Schedule for later | Fix if time permits |

**Severity:** Impact on system
**Priority:** Business urgency

## Common Testing Terms

| Term | Definition |
|------|------------|
| Test Plan | Document describing testing approach |
| Test Suite | Collection of test cases |
| Test Harness | Tools and data for testing |
| Test Oracle | Source of expected results |
| Test Stub | Simulates called component |
| Test Driver | Simulates calling component |
| Test Double | Generic term for fake dependencies |
| Code Coverage | % of code executed by tests |
| Entry Criteria | Conditions to start testing |
| Exit Criteria | Conditions to complete testing |
