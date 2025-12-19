# Background, Rule, and Example Keywords

## Learning Objectives
- Use the Background keyword to share common setup steps
- Understand the difference between Background and hooks
- Apply the Rule keyword (Cucumber 6+) to organize related scenarios
- Use the Example keyword as an alias for Scenario
- Design well-organized feature files with proper grouping

## Why This Matters

As feature files grow, they often contain repeated setup steps and related scenarios that belong together logically. The Background, Rule, and Example keywords help you:

- **Reduce duplication** with shared setup steps
- **Organize scenarios** by business rules
- **Improve readability** for stakeholders
- **Maintain clean** feature files

## The Concept

### The Background Keyword

**Background** defines steps that run before each scenario in a feature:

```gherkin
Feature: Shopping Cart Management

  Background:
    Given the user is logged in
    And the user has an empty cart

  Scenario: Add single item to cart
    When the user adds "Widget" to the cart
    Then the cart should contain 1 item

  Scenario: Add multiple items to cart
    When the user adds "Widget" to the cart
    And the user adds "Gadget" to the cart
    Then the cart should contain 2 items
```

**Execution Flow:**

```
┌─────────────────────────────────────────────────────────────────┐
│                    Background Execution                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Scenario 1: Add single item                                    │
│  ────────────────────────────────────                           │
│  1. Background: Given the user is logged in                     │
│  2. Background: And the user has an empty cart                  │
│  3. Scenario: When the user adds "Widget" to the cart           │
│  4. Scenario: Then the cart should contain 1 item               │
│                                                                  │
│  Scenario 2: Add multiple items                                 │
│  ────────────────────────────────────                           │
│  1. Background: Given the user is logged in       ← Runs again  │
│  2. Background: And the user has an empty cart    ← Runs again  │
│  3. Scenario: When the user adds "Widget" to the cart           │
│  4. Scenario: And the user adds "Gadget" to the cart            │
│  5. Scenario: Then the cart should contain 2 items              │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Background Best Practices

**Keep Background Short:**

```gherkin
# GOOD: Short, essential setup
Background:
  Given the user is logged in

# BAD: Too many steps in Background
Background:
  Given the browser is open
  And I navigate to the homepage
  And I click the login link
  And I enter username "test@example.com"
  And I enter password "password123"
  And I click the login button
  And I wait for the dashboard to load
```

**Use Background for Common Setup Only:**

```gherkin
# GOOD: Steps common to ALL scenarios
Feature: User Profile
  
  Background:
    Given I am logged in as "john@example.com"

  Scenario: View profile
    When I visit my profile page
    Then I should see my email "john@example.com"

  Scenario: Edit profile
    When I update my display name to "John Doe"
    Then my profile should show "John Doe"
```

```gherkin
# BAD: Background contains steps not needed by all scenarios
Feature: User Management
  
  Background:
    Given I am logged in as admin
    And I have created 5 test users    # Not needed for all scenarios!

  Scenario: Admin views dashboard
    When I visit the admin dashboard
    Then I should see admin options

  Scenario: Admin creates new user
    When I create a new user           # This scenario creates its own users!
    Then the user should exist
```

### Background vs Hooks

| Aspect | Background | Hooks (@Before) |
|--------|------------|-----------------|
| **Visibility** | Visible in feature file | Hidden in Java code |
| **Scope** | Per feature file | Can be global or tagged |
| **Purpose** | Business setup steps | Technical setup |
| **Audience** | Stakeholders can read | Developers only |
| **Example** | "Given user is logged in" | Browser initialization |

**When to Use Each:**

```gherkin
Feature: User Dashboard

  # Background: Business logic visible to stakeholders
  Background:
    Given the user "john@example.com" is logged in
    And the user has a subscription plan
```

```java
// Hooks: Technical setup hidden from feature files
public class Hooks {
    @Before
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }
    
    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

### The Rule Keyword (Cucumber 6+)

**Rule** groups related scenarios under a business rule:

```gherkin
Feature: Account Balance

  Rule: Users cannot withdraw more than their balance
    
    Example: Withdrawal within balance
      Given my account balance is $100
      When I withdraw $50
      Then my balance should be $50

    Example: Withdrawal exceeds balance
      Given my account balance is $100
      When I try to withdraw $150
      Then I should see error "Insufficient funds"
      And my balance should remain $100

  Rule: Users receive notifications for low balance
    
    Background:
      Given notifications are enabled

    Example: Balance drops below threshold
      Given my account balance is $100
      And the low balance threshold is $25
      When I withdraw $80
      Then I should receive a low balance notification

    Example: Balance stays above threshold
      Given my account balance is $100
      And the low balance threshold is $25
      When I withdraw $50
      Then I should not receive any notification
```

**Rule Structure:**

```
┌─────────────────────────────────────────────────────────────────┐
│                    Rule Keyword Structure                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Feature: [Feature Name]                                        │
│  │                                                               │
│  ├── Rule: [Business Rule 1]                                    │
│  │   ├── Background (optional, scoped to this Rule)             │
│  │   ├── Example/Scenario 1                                     │
│  │   └── Example/Scenario 2                                     │
│  │                                                               │
│  └── Rule: [Business Rule 2]                                    │
│      ├── Background (optional, scoped to this Rule)             │
│      ├── Example/Scenario 3                                     │
│      └── Example/Scenario 4                                     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Benefits of Rule:**
- Groups scenarios by business rule
- Documents business logic
- Scoped Background per Rule
- Improves feature file organization

### The Example Keyword

**Example** is an alias for **Scenario**:

```gherkin
# These are identical:

Scenario: User logs in successfully
  Given I am on the login page
  When I enter valid credentials
  Then I should see my dashboard

Example: User logs in successfully
  Given I am on the login page
  When I enter valid credentials
  Then I should see my dashboard
```

**When to Use Example vs Scenario:**

```gherkin
# Use "Example" with Rule (reads more naturally)
Rule: Users must be authenticated to access dashboard

  Example: Authenticated user sees dashboard
    Given I am logged in
    When I visit the dashboard
    Then I should see my dashboard

  Example: Unauthenticated user is redirected
    Given I am not logged in
    When I visit the dashboard
    Then I should be redirected to login

# Use "Scenario" for standalone scenarios (traditional)
Feature: User Login

  Scenario: Successful login
    Given I am on the login page
    When I enter valid credentials
    Then I should see my dashboard
```

### Complete Example with All Keywords

```gherkin
Feature: E-commerce Discount Rules
  As a customer
  I want discounts applied correctly
  So that I pay the right amount

  Rule: First-time customers receive 10% discount

    Background:
      Given I am a new customer

    Example: First purchase gets discount
      Given my cart total is $100
      When I proceed to checkout
      Then the discount should be $10
      And my final total should be $90

    Example: Discount applies to full cart
      Given I have items totaling $250
      When I proceed to checkout
      Then the discount should be $25

  Rule: Loyalty members receive free shipping on orders over $50

    Background:
      Given I am a loyalty member

    Example: Free shipping on qualifying order
      Given my cart total is $75
      When I select standard shipping
      Then the shipping cost should be $0

    Example: Regular shipping on small orders
      Given my cart total is $30
      When I select standard shipping
      Then the shipping cost should be $5.99

  Rule: Discounts cannot be combined

    Example: Only highest discount applies
      Given I am a new customer
      And I am a loyalty member
      And I have a 15% coupon
      When my cart total is $100
      Then only the 15% coupon discount should apply
      And my final total should be $85
```

### Organizing Features with Rules

**Without Rules (Less Organized):**

```gherkin
Feature: Order Processing

  Scenario: New customer places order
    # ...
  Scenario: Returning customer places order
    # ...
  Scenario: Order cancellation within 24 hours
    # ...
  Scenario: Order cancellation after 24 hours
    # ...
  Scenario: Order with express shipping
    # ...
```

**With Rules (Well Organized):**

```gherkin
Feature: Order Processing

  Rule: Customer type determines discount

    Example: New customer receives welcome discount
      # ...
    Example: Returning customer receives loyalty points
      # ...

  Rule: Orders can only be cancelled within 24 hours

    Example: Cancellation within window succeeds
      # ...
    Example: Cancellation after window fails
      # ...

  Rule: Express shipping adds delivery guarantee

    Example: Express order arrives within 2 days
      # ...
```

### Background Scope with Rules

Background can be defined at feature level or rule level:

```gherkin
Feature: User Permissions

  # Feature-level Background: Applies to ALL scenarios
  Background:
    Given the system is running

  Rule: Admins have full access

    # Rule-level Background: Only applies to scenarios in this Rule
    Background:
      Given I am logged in as admin

    Example: Admin can view all users
      When I request the user list
      Then I should see all users

    Example: Admin can delete users
      When I delete user "john@example.com"
      Then the user should be removed

  Rule: Regular users have limited access

    Background:
      Given I am logged in as regular user

    Example: User can view own profile
      When I request my profile
      Then I should see my information

    Example: User cannot view other profiles
      When I request another user's profile
      Then I should see "Access denied"
```

### Best Practices Summary

```
┌─────────────────────────────────────────────────────────────────┐
│        Background, Rule, Example Best Practices                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Background:                                                     │
│  ✓ Keep it short (1-3 steps)                                    │
│  ✓ Only include steps needed by ALL scenarios                   │
│  ✓ Use for business setup, not technical setup                  │
│  ✗ Don't duplicate steps that vary per scenario                 │
│                                                                  │
│  Rule:                                                           │
│  ✓ Use to group related scenarios                               │
│  ✓ Name rules after business rules                              │
│  ✓ Use scoped Background within Rules                           │
│  ✗ Don't use Rules for trivial grouping                         │
│                                                                  │
│  Example:                                                        │
│  ✓ Use with Rule keyword (reads naturally)                      │
│  ✓ Can be used interchangeably with Scenario                    │
│  ✓ Consider team preferences for consistency                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Key Takeaways

1. **Background** runs before each scenario in the feature/rule
2. **Background** is for business setup; **hooks** are for technical setup
3. **Rule** groups scenarios under a business rule (Cucumber 6+)
4. **Example** is an alias for Scenario (preferred within Rules)
5. **Background scope** can be feature-level or rule-level
6. **Keep Background short** - only essential shared setup

## Additional Resources

- [Gherkin Reference - Background](https://cucumber.io/docs/gherkin/reference/#background) - Official documentation
- [Gherkin Reference - Rule](https://cucumber.io/docs/gherkin/reference/#rule) - Rule keyword guide
- [Feature File Organization](https://cucumber.io/docs/bdd/better-gherkin/) - Best practices

