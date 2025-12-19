# Exercise 2: Gherkin Writing Practice

## Objective

Write feature files for provided user stories, practicing proper Given-When-Then syntax and Gherkin best practices.

## Learning Goals

- Convert user stories to Gherkin scenarios
- Write clear, maintainable step definitions
- Apply Gherkin best practices
- Use appropriate keywords (Given, When, Then, And, But)
- Write descriptive feature and scenario names

## Time Estimate

30 minutes

---

## The User Stories

Convert the following user stories into Gherkin feature files.

### User Story 1: Shopping Cart

```
As an online shopper
I want to manage items in my shopping cart
So that I can purchase the products I need

Acceptance Criteria:
- User can add items to cart
- User can view cart contents
- User can update item quantities
- User can remove items from cart
- Cart shows total price
- Empty cart shows appropriate message
```

### User Story 2: Password Reset

```
As a registered user
I want to reset my password
So that I can regain access to my account if I forget my password

Acceptance Criteria:
- User can request password reset from login page
- User enters email address
- System sends reset link to valid email
- Invalid email shows appropriate message
- Reset link expires after 24 hours
- User can set new password via reset link
- Old password no longer works after reset
```

### User Story 3: Product Search

```
As a customer
I want to search for products
So that I can quickly find what I'm looking for

Acceptance Criteria:
- User can search by product name
- User can search by category
- Search results show matching products
- Empty search shows all products
- No matches shows "no results" message
- Results can be sorted by price, name, popularity
- Results can be filtered by price range
```

---

## Core Tasks

### Task 1: Shopping Cart Feature (10 minutes)

Create `features/shopping_cart.feature`:

```gherkin
@cart
Feature: Shopping Cart Management
  As an online shopper
  I want to manage items in my shopping cart
  So that I can purchase the products I need

  Background:
    Given the user is logged in
    And the product catalog is available

  @smoke
  Scenario: Add single item to cart
    # TODO: Write the scenario
    # Given: User is on a product page
    # When: User clicks add to cart
    # Then: Item appears in cart, cart count updates

  Scenario: Add multiple quantities of an item
    # TODO: Write the scenario
    # Consider quantity selector interaction

  Scenario: View cart contents
    # TODO: Write the scenario
    # Include verification of item details shown

  Scenario: Update item quantity in cart
    # TODO: Write the scenario
    # Include before/after quantity and price verification

  Scenario: Remove item from cart
    # TODO: Write the scenario
    # Verify item no longer appears and price updates

  Scenario: Empty cart displays message
    # TODO: Write the scenario
    # Verify appropriate message when cart is empty

  Scenario: Cart total calculates correctly
    Given the user has the following items in cart:
      | Product     | Price  | Quantity |
      | Widget A    | 10.00  | 2        |
      | Widget B    | 25.00  | 1        |
    Then the cart subtotal should be "$45.00"
```

### Task 2: Password Reset Feature (10 minutes)

Create `features/password_reset.feature`:

```gherkin
@password @security
Feature: Password Reset
  As a registered user
  I want to reset my password
  So that I can regain access to my account if I forget my password

  @smoke
  Scenario: Request password reset with valid email
    # TODO: Write the scenario

  Scenario: Request password reset with invalid email format
    # TODO: Write the scenario

  Scenario: Request password reset with unregistered email
    # TODO: Write the scenario
    # Note: For security, message should not reveal if email exists

  Scenario: Reset link expires after 24 hours
    # TODO: Write the scenario
    # Use time-based Given clause

  Scenario: Successfully reset password
    # TODO: Write the scenario
    # Include setting new password and verification

  Scenario: Old password fails after reset
    Given the user "john@example.com" has reset their password to "NewPass123!"
    When the user attempts to login with email "john@example.com" and password "OldPass456!"
    Then the login should fail
    And an error message should indicate "Invalid credentials"

  Scenario: Password must meet complexity requirements
    # TODO: Write the scenario
    # Include scenarios for passwords that don't meet requirements
```

### Task 3: Product Search Feature (10 minutes)

Create `features/product_search.feature`:

```gherkin
@search
Feature: Product Search
  As a customer
  I want to search for products
  So that I can quickly find what I'm looking for

  Background:
    Given the product catalog contains:
      | Name           | Category    | Price  |
      | Laptop Pro     | Electronics | 999.00 |
      | Laptop Basic   | Electronics | 599.00 |
      | Wireless Mouse | Electronics | 29.00  |
      | Desk Chair     | Furniture   | 199.00 |
      | Standing Desk  | Furniture   | 449.00 |

  @smoke
  Scenario: Search by exact product name
    # TODO: Write the scenario

  Scenario: Search by partial product name
    When the user searches for "Laptop"
    Then the search results should contain 2 products
    And the results should include "Laptop Pro"
    And the results should include "Laptop Basic"

  Scenario: Search by category
    # TODO: Write the scenario

  Scenario: Empty search returns all products
    # TODO: Write the scenario

  Scenario: No matching results shows message
    # TODO: Write the scenario

  Scenario: Sort results by price ascending
    Given the user has searched for "Electronics"
    When the user sorts by "Price: Low to High"
    Then the first result should be "Wireless Mouse"
    And the last result should be "Laptop Pro"

  Scenario: Filter results by price range
    # TODO: Write the scenario
    # Filter to products between $100 and $500
```

---

## Gherkin Best Practices Checklist

Use this checklist to validate your scenarios:

### ✅ Feature File
- [ ] Feature name is descriptive
- [ ] Feature includes As a/I want/So that
- [ ] Tags are used appropriately

### ✅ Scenarios
- [ ] Each scenario tests ONE thing
- [ ] Scenario names are descriptive
- [ ] Given sets up preconditions
- [ ] When describes the action
- [ ] Then describes expected outcome
- [ ] And/But used appropriately
- [ ] No implementation details in steps

### ✅ Steps
- [ ] Steps are written in third person ("the user") or first person ("I")
- [ ] Steps are reusable across scenarios
- [ ] Steps use parameters for variable data
- [ ] Steps avoid technical jargon

---

## Anti-Patterns to Avoid

❌ **Bad: Too many steps**
```gherkin
Scenario: Login
  Given I open browser
  And I navigate to homepage
  And I click login link
  And I wait for page to load
  And I find username field
  And I type username
  ...
```

✅ **Good: Abstracted steps**
```gherkin
Scenario: Login
  Given I am on the login page
  When I login with valid credentials
  Then I should see the dashboard
```

❌ **Bad: UI-specific language**
```gherkin
When I click the button with id "submit-btn"
And I wait for div.loading to disappear
```

✅ **Good: Business language**
```gherkin
When I submit my order
```

---

## Definition of Done

- [ ] Three feature files created
- [ ] Each feature has at least 6 scenarios
- [ ] All scenarios follow Given-When-Then structure
- [ ] Data tables used where appropriate
- [ ] Tags applied consistently
- [ ] Background used to avoid repetition
- [ ] Steps are reusable and business-focused

---

## Hints

<details>
<summary>Hint: Data Tables in Gherkin</summary>

```gherkin
# Tables for test data
Given the following users exist:
  | Username | Email           | Role  |
  | john     | john@test.com   | admin |
  | jane     | jane@test.com   | user  |

# Tables for expected results
Then the search results should show:
  | Product    | Price  |
  | Widget A   | 10.00  |
  | Widget B   | 25.00  |
```
</details>

<details>
<summary>Hint: Using But</summary>

```gherkin
# 'But' is like 'And' but implies negative/contrast
Scenario: Password reset with expired link
  Given I have a password reset link
  But the link was created 25 hours ago
  When I click the reset link
  Then I should see "This link has expired"
```
</details>

