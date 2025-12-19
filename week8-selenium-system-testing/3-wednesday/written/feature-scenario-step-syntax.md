# Feature, Scenario, and Step Syntax

## Learning Objectives
- Master the Gherkin syntax fundamentals for BDD
- Understand the Feature keyword and its purpose
- Write effective Scenarios with clear naming conventions
- Use Step keywords correctly: Given, When, Then, And, But
- Create reusable steps for maintainable test suites
- Apply best practices for writing business-readable scenarios

## Why This Matters

Gherkin syntax is the language of BDD. Well-written feature files serve as:
- **Living documentation** that stays current with the system
- **Communication tool** between technical and non-technical team members
- **Test specifications** that drive automation
- **Acceptance criteria** that define "done"

Mastering Gherkin ensures your tests are readable, maintainable, and valuable to all stakeholders.

## The Concept

### Gherkin Syntax Overview

Gherkin is a business-readable, domain-specific language that uses a structured format:

```gherkin
Feature: Feature Name
  Feature description paragraph
  explaining the business value

  Scenario: Scenario Name
    Given some precondition
    When some action is taken
    Then some outcome is expected
```

### The Feature Keyword

The **Feature** keyword starts a feature file and provides context:

```gherkin
Feature: User Account Management
  As a registered user
  I want to manage my account settings
  So that I can keep my information up to date

  # Scenarios follow...
```

**Feature Components:**

```
┌─────────────────────────────────────────────────────────────────┐
│                    Feature Structure                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Feature: [Title]                                               │
│  │                                                               │
│  │  [Free-form description]                                     │
│  │  Can span multiple lines                                     │
│  │  Often uses user story format:                               │
│  │    As a [role]                                               │
│  │    I want [goal]                                             │
│  │    So that [benefit]                                         │
│  │                                                               │
│  └── Scenarios                                                   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Feature Naming Best Practices:**

```gherkin
# GOOD: Clear, business-focused names
Feature: Shopping Cart Management
Feature: User Authentication
Feature: Payment Processing
Feature: Order Tracking

# BAD: Technical or vague names
Feature: Test Cart API
Feature: Various User Tests
Feature: Module 5 Tests
```

### The Scenario Keyword

A **Scenario** is a concrete example of system behavior:

```gherkin
Feature: User Authentication

  Scenario: Successful login with valid credentials
    Given the user has a registered account
    When the user logs in with correct username and password
    Then the user should see their dashboard

  Scenario: Failed login with invalid password
    Given the user has a registered account
    When the user logs in with incorrect password
    Then the user should see an error message
```

**Scenario Characteristics:**
- Represents a single test case
- Should be independent (can run in any order)
- Tests one specific behavior
- Has clear expected outcome

**Scenario Naming Conventions:**

```gherkin
# GOOD: Descriptive, outcome-focused names
Scenario: Customer receives confirmation email after order placement
Scenario: Admin can deactivate user accounts
Scenario: Expired password requires reset before login

# BAD: Vague or technical names
Scenario: Test login
Scenario: Scenario 1
Scenario: Happy path
Scenario: Edge case test
```

### Step Keywords

Steps define the scenario flow using Given, When, Then:

```
┌─────────────────────────────────────────────────────────────────┐
│                    Step Keywords                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   GIVEN  │  Precondition / Context                              │
│          │  "The state of the world before the action"          │
│          │  Setup steps, initial conditions                     │
│          │                                                       │
│   WHEN   │  Action / Event                                      │
│          │  "What the user does"                                │
│          │  The action being tested                             │
│          │                                                       │
│   THEN   │  Outcome / Expected Result                           │
│          │  "What should happen"                                │
│          │  Assertions and verifications                        │
│          │                                                       │
│   AND    │  Additional step of same type                        │
│          │  Continues previous Given/When/Then                  │
│          │                                                       │
│   BUT    │  Negative continuation                               │
│          │  Same as And, for readability                        │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### Given - Preconditions

Sets up the initial context:

```gherkin
# Setup user state
Given a registered user with email "john@example.com"
Given the user is logged in
Given the user has items in their cart

# Setup system state
Given the inventory has 100 units of "Widget"
Given the payment service is available
Given it is December 25th

# Setup data
Given the following products exist:
  | name    | price | stock |
  | Widget  | 9.99  | 100   |
  | Gadget  | 19.99 | 50    |
```

#### When - Actions

Describes the action being tested:

```gherkin
# User actions
When the user clicks the "Add to Cart" button
When the user enters "search term" in the search box
When the user submits the registration form

# System events
When the payment is processed
When the scheduled job runs
When the API receives a request

# Time-based
When 24 hours have passed
When the session expires
```

#### Then - Outcomes

Verifies the expected result:

```gherkin
# UI verification
Then the user should see "Welcome, John!"
Then the cart should display 3 items
Then the error message "Invalid email" should appear

# State verification
Then the order status should be "Confirmed"
Then the user's account should be active
Then the inventory should be reduced by 1

# Navigation verification
Then the user should be on the dashboard page
Then the URL should contain "/success"
```

#### And / But - Continuations

Chain multiple steps of the same type:

```gherkin
Scenario: Complete checkout with multiple items
  Given the user is logged in
  And the user has items in their cart      # Additional Given
  And the shipping address is valid         # Additional Given
  
  When the user clicks "Proceed to Checkout"
  And the user confirms the shipping address  # Additional When
  And the user enters payment details         # Additional When
  And the user clicks "Place Order"           # Additional When
  
  Then the order confirmation should appear
  And an email should be sent to the user     # Additional Then
  But the items should not be in the cart     # Negative Then
```

### Writing Effective Scenarios

#### Focus on Behavior, Not Implementation

```gherkin
# BAD: Implementation-focused
Scenario: Test login API
  Given I send POST to /api/login with JSON body
  When the server returns status 200
  Then the response contains JWT token

# GOOD: Behavior-focused
Scenario: User successfully logs in
  Given a registered user exists
  When the user logs in with valid credentials
  Then the user should be authenticated
```

#### Keep Scenarios Short

```gherkin
# BAD: Too many steps
Scenario: User journey
  Given the user opens the browser
  And the user navigates to the homepage
  And the user clicks the login link
  And the user enters their username
  And the user enters their password
  And the user clicks submit
  # ... 20 more steps

# GOOD: Focused scenario
Scenario: User logs in successfully
  Given the user is on the login page
  When the user logs in with valid credentials
  Then the user should see their dashboard
```

#### Use Declarative Over Imperative Style

```gherkin
# BAD: Imperative (how)
Scenario: Add item to cart
  Given I am on the homepage
  When I click the "Products" link
  And I click on "Widget"
  And I select size "Large"
  And I click "Add to Cart"
  And I click the cart icon
  Then I should see "Widget" in the cart

# GOOD: Declarative (what)
Scenario: Add item to cart
  Given I am viewing the "Widget" product
  When I add the product to my cart
  Then my cart should contain the "Widget"
```

### Step Reusability

Write steps that can be reused across scenarios:

```gherkin
# Reusable steps with parameters
Given the user {string} is logged in
When the user adds {string} to their cart
Then the cart should contain {int} items

# Used in multiple scenarios:
Scenario: Add first item
  Given the user "john@example.com" is logged in
  When the user adds "Widget" to their cart
  Then the cart should contain 1 items

Scenario: Add multiple items
  Given the user "jane@example.com" is logged in
  When the user adds "Widget" to their cart
  And the user adds "Gadget" to their cart
  Then the cart should contain 2 items
```

### Step Definition Mapping

```java
// Steps map to Java methods via annotations

@Given("the user {string} is logged in")
public void userIsLoggedIn(String email) {
    loginPage.loginAs(email);
}

@When("the user adds {string} to their cart")
public void userAddsToCart(String productName) {
    productPage.addToCart(productName);
}

@Then("the cart should contain {int} items")
public void cartContainsItems(int count) {
    assertEquals(count, cartPage.getItemCount());
}
```

### Parameter Types

Cucumber supports various parameter types:

```gherkin
# String parameters
When the user searches for "selenium testing"

# Integer parameters
Then the cart should have 3 items

# Float parameters
Then the total should be 29.99

# Word parameters (no spaces)
Given the user is a admin
Given the user is a customer

# Custom parameters
When the user logs in at 2:30 PM
Then the date should be December 25, 2024
```

**Step Definition with Parameters:**

```java
@When("the user searches for {string}")
public void searchFor(String query) {
    searchPage.search(query);
}

@Then("the cart should have {int} items")
public void cartHasItems(int count) {
    assertEquals(count, cart.getCount());
}

@Then("the total should be {double}")
public void totalIs(double amount) {
    assertEquals(amount, cart.getTotal(), 0.01);
}
```

### Doc Strings for Long Text

Use doc strings for multi-line text:

```gherkin
Scenario: Submit feedback with detailed message
  Given I am on the feedback page
  When I submit the following feedback:
    """
    Your product is excellent!
    I especially love the new dashboard feature.
    The reporting tools have saved me hours of work.
    Keep up the great work!
    """
  Then I should see "Thank you for your feedback"
```

```java
@When("I submit the following feedback:")
public void submitFeedback(String feedback) {
    feedbackPage.enterFeedback(feedback);
    feedbackPage.submit();
}
```

### Data Tables

Use tables for structured data:

```gherkin
Scenario: Register multiple users
  Given the following users are registered:
    | username | email              | role    |
    | john     | john@example.com   | admin   |
    | jane     | jane@example.com   | user    |
    | bob      | bob@example.com    | user    |
```

```java
@Given("the following users are registered:")
public void usersRegistered(DataTable dataTable) {
    List<Map<String, String>> users = dataTable.asMaps();
    for (Map<String, String> user : users) {
        userService.register(
            user.get("username"),
            user.get("email"),
            user.get("role")
        );
    }
}
```

### Best Practices Summary

```
┌─────────────────────────────────────────────────────────────────┐
│              Gherkin Best Practices                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  DO:                                                             │
│  ✓ Write from user's perspective                                │
│  ✓ Use business language, not technical jargon                  │
│  ✓ Keep scenarios focused and independent                       │
│  ✓ Make steps reusable across scenarios                         │
│  ✓ Use declarative style (what, not how)                        │
│  ✓ Name scenarios by their outcome                              │
│                                                                  │
│  DON'T:                                                          │
│  ✗ Include UI implementation details                            │
│  ✗ Write overly long scenarios                                  │
│  ✗ Use technical terms business wouldn't understand             │
│  ✗ Duplicate steps unnecessarily                                │
│  ✗ Make scenarios dependent on each other                       │
│  ✗ Include test data that obscures intent                       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Key Takeaways

1. **Feature** describes the functionality being tested
2. **Scenario** represents a single test case with clear outcome
3. **Given** sets up preconditions, **When** performs actions, **Then** verifies outcomes
4. **And/But** chain multiple steps of the same type
5. **Declarative style** focuses on what, not how
6. **Reusable steps** improve maintainability

## Additional Resources

- [Gherkin Reference](https://cucumber.io/docs/gherkin/reference/) - Official Gherkin syntax reference
- [Writing Better Gherkin](https://cucumber.io/docs/bdd/better-gherkin/) - Best practices guide
- [Example Mapping](https://cucumber.io/blog/bdd/example-mapping-introduction/) - Technique for discovering scenarios

