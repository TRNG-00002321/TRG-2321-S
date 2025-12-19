# System Testing

## Learning Objectives
- Define system testing and understand its purpose in software quality assurance
- Position system testing within the V-model and software testing lifecycle
- Differentiate system testing from other testing levels (unit, integration)
- Identify types of system testing (functional and non-functional)
- Design effective system test cases with proper entry and exit criteria
- Plan system test environments for realistic testing scenarios

## Why This Matters

As you progress through your journey to becoming a complete test automation engineer, understanding **where** your tests fit in the testing hierarchy is as important as knowing **how** to write them. System testing represents the point where all components come together for validation as a complete, integrated system.

In Week 6 and 7, you learned unit testing and integration testing. Now we examine system testing—the level where you verify that the entire application meets its specified requirements. This knowledge ensures you design tests at the appropriate level, avoiding over-testing at one level while under-testing at another.

## The Concept

### What is System Testing?

**System testing** is a level of software testing where a complete, integrated system is tested to evaluate compliance with specified requirements. It examines the system's behavior as a whole, from the end-user's perspective.

```
┌─────────────────────────────────────────────────────────────────┐
│                    Software Testing Levels                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Unit Testing        → Test individual components in isolation │
│        ↓                                                         │
│   Integration Testing → Test component interactions              │
│        ↓                                                         │
│   System Testing      → Test complete integrated system          │
│        ↓                                                         │
│   Acceptance Testing  → Test against business requirements       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### System Testing in the V-Model

The V-Model shows the relationship between development phases and testing phases:

```
Requirements Analysis  ←─────────────────→  Acceptance Testing
        ↓                                          ↑
   System Design      ←─────────────────→  System Testing
        ↓                                          ↑
  Architecture Design ←─────────────────→  Integration Testing
        ↓                                          ↑
    Module Design     ←─────────────────→  Unit Testing
        ↓                                          ↑
      Coding ─────────────────────────────────────┘
```

**Key Points:**
- System testing corresponds to **System Design** specifications
- Tests are derived from system-level requirements and design documents
- Verifies the system meets its intended design

### System Testing vs Other Testing Levels

| Aspect | Unit Testing | Integration Testing | System Testing |
|--------|--------------|---------------------|----------------|
| **Scope** | Single function/method | Multiple components | Entire system |
| **Tester** | Developer | Developer/Tester | Tester/QA Team |
| **Environment** | Development | Test environment | System test environment |
| **Dependencies** | Mocked/Stubbed | Some real, some mocked | All real |
| **Test Basis** | Code specifications | Interface specifications | System requirements |
| **Objective** | Code correctness | Component interaction | System behavior |

### Purpose of System Testing

System testing serves several critical purposes:

1. **Requirement Verification** - Confirms the system meets functional requirements
2. **Behavior Validation** - Validates system behavior in realistic scenarios
3. **End-to-End Workflows** - Tests complete business processes
4. **External Interface Testing** - Verifies integration with external systems
5. **Non-functional Testing** - Evaluates performance, security, usability
6. **Risk Mitigation** - Identifies defects before production deployment

### Types of System Testing

#### Functional System Testing

Tests the system's functionality against requirements:

```
┌─────────────────────────────────────────────────────────────────┐
│                  Functional System Testing Types                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  • Feature Testing      - Individual features work correctly     │
│  • Workflow Testing     - End-to-end business processes          │
│  • Data Flow Testing    - Data moves correctly through system    │
│  • User Interface Testing - UI elements function properly        │
│  • Error Handling Testing - System handles errors gracefully     │
│  • Boundary Testing     - System behavior at limits              │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Example Functional Tests:**

```
Test Case: User Registration Flow
----------------------------------
Preconditions: User is not registered

Steps:
1. Navigate to registration page
2. Enter valid user details (name, email, password)
3. Accept terms and conditions
4. Click Register button
5. Verify confirmation email sent
6. Click email verification link
7. Verify account is activated

Expected Result: User can log in with registered credentials
```

#### Non-Functional System Testing

Tests quality attributes beyond functionality:

| Type | Purpose | Example |
|------|---------|---------|
| **Performance Testing** | Response time, throughput | Page loads in < 3 seconds |
| **Load Testing** | Behavior under expected load | 1000 concurrent users |
| **Stress Testing** | Behavior beyond capacity | 10x normal load |
| **Security Testing** | Vulnerability identification | SQL injection attempts |
| **Usability Testing** | User experience quality | Task completion time |
| **Compatibility Testing** | Cross-browser/device support | Chrome, Firefox, Safari |
| **Recovery Testing** | System recovery from failures | Database restore |
| **Installation Testing** | Install/uninstall process | Clean installation |

### System Test Environment Setup

A proper system test environment mirrors production:

```
┌─────────────────────────────────────────────────────────────────┐
│                  System Test Environment                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │  Web     │  │  App     │  │  API     │  │ Database │        │
│  │  Server  │──│  Server  │──│  Server  │──│  Server  │        │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │
│       │             │             │             │                │
│       └─────────────┴─────────────┴─────────────┘                │
│                          │                                       │
│              ┌───────────┴───────────┐                          │
│              │   External Services   │                          │
│              │  (Payment, Email, etc)│                          │
│              └───────────────────────┘                          │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Environment Requirements:**

1. **Hardware** - Similar specs to production
2. **Software** - Same versions of OS, middleware, databases
3. **Network** - Realistic network topology and latency
4. **Data** - Representative test data (sanitized from production)
5. **External Systems** - Stubs or sandbox versions of third-party services
6. **Configuration** - Production-like configuration settings

### System Testing Entry and Exit Criteria

#### Entry Criteria (When to Start)

| Criterion | Description |
|-----------|-------------|
| Build stability | Integration tests passing |
| Test environment | Environment ready and verified |
| Test data | Test data prepared and loaded |
| Test cases | Test cases reviewed and approved |
| Dependencies | All dependencies available |
| Documentation | Requirements and design documents available |

#### Exit Criteria (When to Stop)

| Criterion | Description |
|-----------|-------------|
| Test coverage | All planned test cases executed |
| Defect resolution | Critical/High defects resolved |
| Pass rate | Minimum 95% tests passing |
| Requirements coverage | All requirements tested |
| Sign-off | Stakeholder approval received |

### System Test Case Design

#### Test Case Structure

```
Test Case ID: SYS-TC-001
Test Case Name: Complete Purchase Flow
Priority: High
Module: E-Commerce

Preconditions:
- User is logged in
- Shopping cart has at least one item
- Payment service is available

Test Steps:
┌────┬────────────────────────────────┬─────────────────────────────┐
│ #  │ Action                         │ Expected Result              │
├────┼────────────────────────────────┼─────────────────────────────┤
│ 1  │ Navigate to checkout page      │ Checkout page displays       │
│ 2  │ Enter shipping address         │ Address accepted             │
│ 3  │ Select shipping method         │ Shipping cost calculated     │
│ 4  │ Enter payment details          │ Payment form validated       │
│ 5  │ Click "Place Order"            │ Order confirmation displayed │
│ 6  │ Check email                    │ Confirmation email received  │
│ 7  │ View order history             │ Order appears in history     │
└────┴────────────────────────────────┴─────────────────────────────┘

Postconditions:
- Order recorded in database
- Inventory updated
- Payment processed

Test Data:
- Product: Widget A (SKU: WGT-001)
- Shipping: Standard (5-7 days)
- Payment: Test credit card 4111-1111-1111-1111
```

#### Test Design Techniques for System Testing

**1. Equivalence Partitioning:**
```
Input: User Age for Registration
Valid partition: 18-120 (legal adults)
Invalid partitions: <18, >120, non-numeric

Test cases:
- Age = 25 (valid)
- Age = 15 (invalid - too young)
- Age = 150 (invalid - unrealistic)
- Age = "abc" (invalid - non-numeric)
```

**2. Boundary Value Analysis:**
```
Password length requirement: 8-20 characters

Boundaries: 7, 8, 20, 21

Test cases:
- 7 chars → Invalid (below minimum)
- 8 chars → Valid (at minimum)
- 20 chars → Valid (at maximum)
- 21 chars → Invalid (above maximum)
```

**3. State Transition Testing:**
```
Order States: Pending → Processing → Shipped → Delivered

Test state transitions:
- Pending → Processing (valid)
- Processing → Shipped (valid)
- Shipped → Pending (invalid - cannot go back)
- Delivered → Shipped (invalid - cannot go back)
```

**4. Use Case Testing:**
```
Use Case: Password Reset

Main Flow:
1. User clicks "Forgot Password"
2. User enters email
3. System sends reset link
4. User clicks link
5. User enters new password
6. System confirms password change

Alternate Flows:
- 2a. Invalid email format → Error message
- 2b. Email not registered → "If registered, check email"
- 4a. Link expired → "Request new link"
- 5a. Weak password → Password requirements message
```

### System Testing Best Practices

1. **Test from User Perspective**
   - Focus on realistic user scenarios
   - Test complete workflows, not isolated features

2. **Prioritize Based on Risk**
   - Critical business functions first
   - High-impact failure scenarios

3. **Use Production-Like Environment**
   - Match production configuration
   - Use realistic data volumes

4. **Document Everything**
   - Detailed test cases
   - Clear pass/fail criteria
   - Defect reports with reproduction steps

5. **Automate Regression Tests**
   - Automate repetitive system tests
   - This is where Selenium/Playwright automation shines

6. **Coordinate with Other Testing**
   - Don't duplicate integration tests
   - Focus on system-level concerns

### Connecting to Your Automation Skills

The Selenium and Playwright skills you're learning this week are primarily used for **automated system testing**:

```python
"""
Example: Automated System Test with Selenium
Tests the complete user registration workflow
"""
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

def test_user_registration_workflow(driver):
    """
    System test: Complete user registration
    Tests the end-to-end registration process
    """
    wait = WebDriverWait(driver, 10)
    
    # Step 1: Navigate to registration
    driver.get("https://app.example.com/register")
    
    # Step 2: Fill registration form
    driver.find_element(By.ID, "name").send_keys("Test User")
    driver.find_element(By.ID, "email").send_keys("test@example.com")
    driver.find_element(By.ID, "password").send_keys("SecurePass123!")
    driver.find_element(By.ID, "confirm-password").send_keys("SecurePass123!")
    
    # Step 3: Accept terms
    driver.find_element(By.ID, "terms-checkbox").click()
    
    # Step 4: Submit registration
    driver.find_element(By.ID, "register-btn").click()
    
    # Step 5: Verify success
    success_message = wait.until(
        EC.visibility_of_element_located((By.CLASS_NAME, "success-message"))
    )
    assert "Registration successful" in success_message.text
    
    # Step 6: Verify redirect to login
    wait.until(EC.url_contains("/login"))
    assert "/login" in driver.current_url
```

## Key Takeaways

1. **System testing** validates the complete integrated system against requirements
2. **Position in V-Model** - corresponds to system design specifications
3. **Includes both functional and non-functional testing** types
4. **Requires production-like environment** for realistic results
5. **Entry/exit criteria** ensure proper test execution timing
6. **Automation with Selenium/Playwright** is ideal for system test regression

## Additional Resources

- [ISTQB Foundation Level Syllabus](https://www.istqb.org/certifications/certified-tester-foundation-level) - Chapter on Test Levels
- [Software Testing Fundamentals - System Testing](http://softwaretestingfundamentals.com/system-testing/) - Comprehensive overview
- [Guru99 System Testing Guide](https://www.guru99.com/system-testing.html) - Practical examples and tutorials

