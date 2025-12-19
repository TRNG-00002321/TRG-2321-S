# User Stories and Acceptance Criteria

## Learning Objectives
- Understand user story fundamentals and format (As a/I want/So that)
- Write effective user stories following the INVEST criteria
- Define clear acceptance criteria using the Given-When-Then format
- Map user stories to testable scenarios
- Bridge the gap between requirements and automated tests

## Why This Matters

User stories are the foundation of agile development and BDD. They capture **what** users need without prescribing **how** to build it. Acceptance criteria define **when** a story is complete. Together, they form the basis for:

- Development prioritization
- Sprint planning
- Test case design
- **Cucumber scenarios**

As a test automation engineer, you'll often translate user stories into automated tests. Understanding this translation is essential for writing meaningful, maintainable test suites.

## The Concept

### User Story Fundamentals

A **user story** is a short, simple description of a feature told from the perspective of the person who wants the capability—usually a user or customer.

```
┌─────────────────────────────────────────────────────────────────┐
│                    User Story Format                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   As a [type of user]                                           │
│   I want [some goal]                                            │
│   So that [some reason/benefit]                                 │
│                                                                  │
│   ─────────────────────────────────────────────────────────     │
│                                                                  │
│   Role:    WHO wants this functionality?                        │
│   Action:  WHAT do they want to do?                             │
│   Benefit: WHY do they want it?                                 │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### User Story Format Breakdown

#### The Role (As a...)

Identifies who wants the feature:

```
As a registered customer...
As a site administrator...
As a first-time visitor...
As a premium subscriber...
As a warehouse manager...
```

**Why it matters:** Different users have different needs and permissions.

#### The Goal (I want...)

Describes what the user wants to accomplish:

```
...I want to filter products by category...
...I want to export reports to PDF...
...I want to reset my password via email...
...I want to schedule appointments...
```

**Focus on goals, not solutions:**
- ✗ "I want a dropdown menu with categories"
- ✓ "I want to filter products by category"

#### The Benefit (So that...)

Explains why this matters:

```
...so that I can find products relevant to my interests.
...so that I can share data with stakeholders offline.
...so that I can regain access to my account.
...so that I can manage my time effectively.
```

**Why it matters:** Helps prioritize and may suggest better solutions.

### User Story Examples

**E-Commerce Example:**
```
As an online shopper
I want to add items to a wishlist
So that I can save products for later purchase without committing to buy now
```

**Banking Example:**
```
As a bank customer
I want to transfer money between my accounts
So that I can manage my finances without visiting a branch
```

**Healthcare Example:**
```
As a patient
I want to view my upcoming appointments
So that I can plan my schedule and avoid missing appointments
```

### The INVEST Criteria

Good user stories follow the **INVEST** criteria:

```
┌─────────────────────────────────────────────────────────────────┐
│                    INVEST Criteria                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   I - Independent                                                │
│       Stories should be self-contained, not dependent on others │
│                                                                  │
│   N - Negotiable                                                 │
│       Details can be negotiated between team and stakeholders   │
│                                                                  │
│   V - Valuable                                                   │
│       Must deliver value to users or stakeholders               │
│                                                                  │
│   E - Estimable                                                  │
│       Team should be able to estimate the effort required       │
│                                                                  │
│   S - Small                                                      │
│       Should be completable within one sprint/iteration         │
│                                                                  │
│   T - Testable                                                   │
│       Clear criteria for testing and verification               │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### Applying INVEST

**Poor Story (Violates INVEST):**
```
As a user
I want to use the system
So that I can do things
```
- ✗ Not Independent (too vague)
- ✗ Not Valuable (no clear value)
- ✗ Not Estimable (scope undefined)
- ✗ Not Testable (no criteria)

**Good Story (Follows INVEST):**
```
As a registered customer
I want to track my order status in real-time
So that I know when to expect my delivery

Acceptance Criteria:
- Show current order status (Processing, Shipped, Out for Delivery, Delivered)
- Display estimated delivery date
- Show tracking number linked to carrier website
```
- ✓ Independent (doesn't depend on other stories)
- ✓ Negotiable (details can be discussed)
- ✓ Valuable (clear user benefit)
- ✓ Estimable (scope is clear)
- ✓ Small (achievable in a sprint)
- ✓ Testable (has acceptance criteria)

### Acceptance Criteria Definition

**Acceptance criteria** are the conditions that must be met for a user story to be considered complete. They define the boundaries of the story and guide testing.

#### Purpose of Acceptance Criteria

1. **Define scope** - What's included and what's not
2. **Guide development** - Clear requirements to implement
3. **Enable testing** - Specific conditions to verify
4. **Confirm completion** - When is "done" done?

#### Acceptance Criteria Formats

**Format 1: Checklist Style**
```
User Story: Password Reset

Acceptance Criteria:
□ User can request reset via email
□ Reset link expires after 24 hours
□ Password must meet complexity requirements
□ User receives confirmation after successful reset
□ Old password no longer works after reset
```

**Format 2: Given-When-Then (BDD Style)**
```
User Story: Password Reset

Acceptance Criteria:

Given a registered user has forgotten their password
When they request a password reset
Then they should receive a reset link via email within 2 minutes

Given a user has a valid reset link
When they enter a new password meeting complexity requirements
Then their password should be updated
And they should receive a confirmation email

Given a reset link older than 24 hours
When a user tries to use it
Then they should see an expiration message
And be prompted to request a new link
```

### Given-When-Then Format

The **Given-When-Then** format structures acceptance criteria as testable scenarios:

```
┌─────────────────────────────────────────────────────────────────┐
│                 Given-When-Then Structure                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   GIVEN [preconditions/context]                                 │
│   │                                                              │
│   │   The starting state before the action                      │
│   │   Sets up the scenario context                              │
│   │   Can have multiple Given clauses (use And)                 │
│   │                                                              │
│   WHEN [action/event]                                           │
│   │                                                              │
│   │   The specific action being tested                          │
│   │   Usually a single action                                   │
│   │   Triggers the behavior under test                          │
│   │                                                              │
│   THEN [expected outcome]                                        │
│                                                                  │
│       The expected result after the action                      │
│       Verifiable assertions                                     │
│       Can have multiple Then clauses (use And)                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### Given-When-Then Examples

**Login Scenario:**
```gherkin
Given a registered user with email "john@example.com"
And the user's password is "SecurePass123"
When the user logs in with correct credentials
Then the user should be authenticated
And redirected to their dashboard
```

**Shopping Cart Scenario:**
```gherkin
Given a customer has items in their cart
And the cart total is $150
When the customer applies coupon code "SAVE20"
Then the discount of $30 should be applied
And the new cart total should be $120
```

**Error Handling Scenario:**
```gherkin
Given a user is on the registration page
When they submit the form with an already registered email
Then they should see an error message "Email already exists"
And the form should not be submitted
```

### Mapping Stories to Tests

User stories translate directly into Cucumber feature files:

**User Story:**
```
As a library member
I want to search for books by title
So that I can find books I want to borrow
```

**Acceptance Criteria:**
```
1. Search returns books with matching titles
2. Search is case-insensitive
3. Partial matches are included in results
4. No results shows appropriate message
```

**Cucumber Feature File:**
```gherkin
Feature: Book Search
  As a library member
  I want to search for books by title
  So that I can find books I want to borrow

  Background:
    Given the library has the following books:
      | Title                    | Author           |
      | The Great Gatsby         | F. Scott Fitzgerald |
      | Great Expectations       | Charles Dickens  |
      | To Kill a Mockingbird    | Harper Lee       |

  Scenario: Search finds exact title match
    When I search for "The Great Gatsby"
    Then I should see "The Great Gatsby" in the results

  Scenario: Search is case-insensitive
    When I search for "great expectations"
    Then I should see "Great Expectations" in the results

  Scenario: Search finds partial matches
    When I search for "Great"
    Then I should see "The Great Gatsby" in the results
    And I should see "Great Expectations" in the results
    But I should not see "To Kill a Mockingbird" in the results

  Scenario: No results found
    When I search for "Nonexistent Book"
    Then I should see the message "No books found matching your search"
```

### Writing Good User Stories

#### Tips for Effective Stories

1. **Start with the user**
   ```
   ✗ "The system should validate email format"
   ✓ "As a user, I want to receive immediate feedback on invalid email entry"
   ```

2. **Focus on outcomes, not outputs**
   ```
   ✗ "I want a search box"
   ✓ "I want to quickly find products I'm looking for"
   ```

3. **Keep it conversational**
   ```
   ✗ "User account status modification functionality"
   ✓ "As an admin, I want to suspend user accounts that violate terms"
   ```

4. **Include context when needed**
   ```
   "As a mobile user on slow connection,
    I want to see cached product information
    So that I can browse even with poor connectivity"
   ```

#### Story Splitting Techniques

Large stories can be split into smaller ones:

**Original (Too Large):**
```
As a user, I want to manage my profile
```

**Split by Action:**
```
As a user, I want to view my profile
As a user, I want to edit my profile photo
As a user, I want to update my email address
As a user, I want to change my password
```

**Split by Data:**
```
As a user, I want to update my contact information
As a user, I want to update my notification preferences
As a user, I want to update my privacy settings
```

### Acceptance Criteria Best Practices

1. **Be specific but not technical**
   ```
   ✗ "The API should return JSON with status 200"
   ✓ "User receives confirmation of successful update"
   ```

2. **Cover happy path and edge cases**
   ```
   Happy: User logs in with valid credentials
   Edge: User exceeds maximum login attempts
   Edge: User account is locked
   ```

3. **Make criteria testable**
   ```
   ✗ "System should be fast"
   ✓ "Search results appear within 2 seconds"
   ```

4. **Avoid implementation details**
   ```
   ✗ "Click the blue button in the top-right corner"
   ✓ "User can submit the form"
   ```

### Connecting to Automation

**From Acceptance Criteria to Step Definitions:**

Acceptance Criteria:
```gherkin
Given a registered user with valid credentials
When the user logs in
Then they should see their personalized dashboard
```

Step Definition (Java):
```java
@Given("a registered user with valid credentials")
public void a_registered_user_with_valid_credentials() {
    testUser = UserFactory.createRegisteredUser();
}

@When("the user logs in")
public void the_user_logs_in() {
    loginPage.navigateTo();
    loginPage.login(testUser.getEmail(), testUser.getPassword());
}

@Then("they should see their personalized dashboard")
public void they_should_see_their_personalized_dashboard() {
    assertTrue(dashboardPage.isDisplayed());
    assertEquals(testUser.getName(), dashboardPage.getWelcomeMessage());
}
```

## Key Takeaways

1. **User stories** follow "As a [role], I want [goal], So that [benefit]"
2. **INVEST criteria** ensure stories are well-formed (Independent, Negotiable, Valuable, Estimable, Small, Testable)
3. **Acceptance criteria** define when a story is complete
4. **Given-When-Then** format makes criteria directly translatable to Cucumber scenarios
5. **Focus on behavior and outcomes**, not implementation details
6. **User stories are the starting point** for BDD test automation

## Additional Resources

- [User Stories Applied](https://www.mountaingoatsoftware.com/books/user-stories-applied) - Mike Cohn's definitive guide
- [INVEST in Good Stories](https://xp123.com/articles/invest-in-good-stories-and-smart-tasks/) - Original INVEST article
- [Writing Great Specifications](https://www.manning.com/books/writing-great-specifications) - BDD and acceptance criteria guide

