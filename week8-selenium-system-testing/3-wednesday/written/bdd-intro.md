# Behavior-Driven Development (BDD) Introduction

## Learning Objectives
- Understand BDD philosophy and core principles
- Learn the Three Amigos collaboration practice
- Compare BDD with TDD and understand their relationship
- Identify the benefits of adopting BDD
- Recognize common BDD anti-patterns to avoid

## Why This Matters

Throughout your training, you've learned to write tests that verify code works correctly. But a fundamental question remains: **Are we building the right thing?** Test-Driven Development (TDD) helps ensure code quality, but Behavior-Driven Development (BDD) ensures we build what stakeholders actually need.

BDD shifts the focus from testing implementation to specifying behavior. When you write tests in business language that stakeholders can read and validate, you eliminate the gap between what was requested and what was built.

## The Concept

### What is Behavior-Driven Development?

**Behavior-Driven Development (BDD)** is a software development approach that emerged from Test-Driven Development (TDD). It combines the general techniques and principles of TDD with ideas from domain-driven design and object-oriented analysis to provide software development and management teams with shared tools and a shared process to collaborate on software development.

```
┌─────────────────────────────────────────────────────────────────┐
│                    BDD Core Idea                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Traditional Testing:                                           │
│   "Test that the code works correctly"                          │
│                                                                  │
│   TDD Testing:                                                   │
│   "Write tests first, then make them pass"                      │
│                                                                  │
│   BDD Testing:                                                   │
│   "Describe behavior in business terms,                         │
│    then make the system behave that way"                        │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### BDD Philosophy and Principles

#### Core Philosophy

BDD is built on three pillars:

```
         ┌───────────────────────────────────────┐
         │          BDD Pillars                  │
         └───────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┐
        ▼                ▼                ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│  Discovery   │ │  Formulation │ │  Automation  │
│              │ │              │ │              │
│ Explore and  │ │ Document     │ │ Turn specs   │
│ understand   │ │ behaviors in │ │ into         │
│ requirements │ │ examples     │ │ automated    │
│              │ │              │ │ tests        │
└──────────────┘ └──────────────┘ └──────────────┘
```

#### Key Principles

1. **Enough is Enough**
   - Don't specify more than necessary
   - Focus on the next increment of value

2. **Deliver Stakeholder Value**
   - Every feature should deliver measurable value
   - Tests should describe business outcomes

3. **It's All Behavior**
   - Everything is behavior at some level
   - Describe what, not how

4. **Ubiquitous Language**
   - Use business terms in code and tests
   - Everyone speaks the same language

5. **Examples Guide Development**
   - Concrete examples clarify requirements
   - Examples become tests

### The Three Amigos

The **Three Amigos** is a collaborative practice central to BDD:

```
┌─────────────────────────────────────────────────────────────────┐
│                    Three Amigos Meeting                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│      ┌──────────────┐                                           │
│      │   Business   │  "What business problem are we solving?"  │
│      │   (Product   │  "What value does this deliver?"          │
│      │    Owner)    │  "What are the acceptance criteria?"      │
│      └──────────────┘                                           │
│            │                                                     │
│            │                                                     │
│   ┌────────┴────────┐                                           │
│   │                 │                                           │
│   ▼                 ▼                                           │
│ ┌──────────────┐  ┌──────────────┐                              │
│ │  Developer   │  │    Tester    │                              │
│ │              │  │    (QA)      │                              │
│ │ "How will we │  │ "What could  │                              │
│ │  build it?"  │  │  go wrong?"  │                              │
│ │ "Technical   │  │ "Edge cases?"│                              │
│ │  constraints"│  │ "How to test"│                              │
│ └──────────────┘  └──────────────┘                              │
│                                                                  │
│  Output: Shared understanding + Concrete examples                │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Three Amigos Process:**

1. **Before Development:**
   - Product Owner presents the user story
   - Developer asks clarifying technical questions
   - Tester identifies edge cases and scenarios

2. **Outcome:**
   - Agreed acceptance criteria
   - Concrete examples (scenarios)
   - Shared understanding of "done"

3. **Example Discussion:**

```
User Story: As a customer, I want to apply discount codes at checkout

Product Owner: "Customers can enter a code for a percentage discount"

Developer: "What if they enter multiple codes?"
PO: "Only one code per order"

Tester: "What if the code is expired?"
PO: "Show an error message"

Tester: "What about case sensitivity?"
Developer: "We should make it case-insensitive"
PO: "Agreed"

→ Scenarios emerge from this conversation
```

### BDD vs TDD

| Aspect | TDD | BDD |
|--------|-----|-----|
| **Focus** | Code design | System behavior |
| **Language** | Programming language | Natural language |
| **Audience** | Developers | Everyone |
| **Test Level** | Unit tests | Acceptance tests |
| **Starting Point** | Method behavior | User story |
| **Documentation** | Code coverage | Living documentation |
| **Collaboration** | Developer practice | Team practice |

**Relationship Between TDD and BDD:**

```
                    BDD
             (Outside-In)
                  │
                  ▼
┌─────────────────────────────────────┐
│         Acceptance Test             │  ← Describes business behavior
│   (Feature file with scenarios)     │    Written with stakeholders
└─────────────────────────────────────┘
                  │
                  │ Drives implementation
                  ▼
┌─────────────────────────────────────┐
│          Integration Test           │  ← Tests component interactions
│                                     │
└─────────────────────────────────────┘
                  │
                  │ Drives implementation
                  ▼
┌─────────────────────────────────────┐
│            Unit Test                │  ← TDD practice
│      (Inside-Out development)       │    Developer-focused
└─────────────────────────────────────┘
```

**BDD extends TDD:**
- TDD: Red → Green → Refactor (code level)
- BDD: Discover → Formulate → Automate (behavior level)

### Benefits of BDD

#### 1. Shared Understanding

```
Before BDD:
┌──────────┐     Different      ┌──────────┐
│ Business │ ─────────────────► │ Developer│
│ says X   │   interpretations  │ builds Y │
└──────────┘                    └──────────┘

With BDD:
┌──────────┐     Examples &     ┌──────────┐
│ Business │ ◄────────────────► │ Developer│
│          │    collaboration   │          │
└──────────┘         │          └──────────┘
                     ▼
              ┌──────────────┐
              │ Both agree   │
              │ on X         │
              └──────────────┘
```

#### 2. Living Documentation

```
Traditional Documentation:
Day 1: Document written ────────►  Day 30: Document outdated
                                   (code has changed)

BDD Documentation:
Day 1: Scenarios written ────────►  Day 30: Scenarios still valid
       (feature files)                      (tests still pass)
```

#### 3. Early Bug Detection

```
Traditional:
Requirements → Development → Testing → Bugs found late
                                      (expensive to fix)

BDD:
Requirements → Examples → Ambiguities found → Development
              (Three Amigos)  (cheap to fix)
```

#### 4. Regression Safety

```gherkin
# Scenarios serve as regression tests
# If behavior changes, tests fail immediately

Feature: Shopping Cart
  Scenario: Apply discount code
    Given item "Widget" costs $100
    When I apply discount code "SAVE10"
    Then the total should be $90
    
# If someone changes discount logic, this test catches it
```

#### 5. Business Engagement

```
Without BDD:
Business: "Did you build what I asked?"
Dev: "I think so... want to see the code?"
Business: "I can't read code"

With BDD:
Business: "Did you build what I asked?"
Dev: "Here are the scenarios we agreed on - they all pass"
Business: "I can read these! Yes, that's exactly what I wanted"
```

### BDD Anti-Patterns to Avoid

#### 1. Writing Scenarios After Code (Specification by Example)

**Anti-pattern:**
```
Code first → Then write scenarios to match
```

**Correct:**
```
Three Amigos → Write scenarios → Then write code
```

#### 2. Technical Language in Scenarios

**Anti-pattern:**
```gherkin
Scenario: Login test
  Given I navigate to URL "/login"
  When I enter text "john" in input field with ID "username"
  And I click button with CSS selector ".submit-btn"
  Then the element with class "dashboard" should be visible
```

**Correct:**
```gherkin
Scenario: User logs in successfully
  Given I am on the login page
  When I enter my valid credentials
  And I submit the login form
  Then I should see my dashboard
```

#### 3. Too Many Scenarios (Scenario Explosion)

**Anti-pattern:**
```gherkin
Feature: Login
  Scenario: Login with correct username and password
  Scenario: Login with correct username and wrong password
  Scenario: Login with wrong username and correct password
  Scenario: Login with wrong username and wrong password
  Scenario: Login with empty username
  Scenario: Login with empty password
  # 50 more scenarios...
```

**Correct:**
```gherkin
Feature: Login
  Scenario: Successful login
    # Happy path
    
  Scenario Outline: Failed login attempts
    # Edge cases via data table
    
  # Delegate technical validation to unit tests
```

#### 4. Implementation Details in Scenarios

**Anti-pattern:**
```gherkin
Scenario: Add item to cart
  Given the database has product ID 12345
  When I POST to /api/cart with JSON payload
  And the server returns status 200
  Then the cart_items table has 1 row
```

**Correct:**
```gherkin
Scenario: Add item to cart
  Given the product "Blue Widget" is available
  When I add "Blue Widget" to my cart
  Then my cart should contain 1 item
```

#### 5. No Business Involvement

**Anti-pattern:**
```
Developers write all scenarios alone
QA reviews scenarios after coding is done
Business never sees feature files
```

**Correct:**
```
Three Amigos session BEFORE development
Business validates scenarios
Feature files are shared documentation
```

#### 6. Incidental Details

**Anti-pattern:**
```gherkin
Scenario: User registration
  Given it is Monday, January 15th, 2024
  And the user is using Chrome browser version 120
  And the user's IP address is 192.168.1.100
  When the user named "John Smith" with email "john@example.com" registers
  Then the registration should succeed
```

**Correct:**
```gherkin
Scenario: User registration
  When a new user completes the registration form
  Then the registration should succeed
```

### BDD Workflow

```
┌─────────────────────────────────────────────────────────────────┐
│                      BDD Workflow                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. DISCOVERY (Three Amigos)                                     │
│     ├── Review user story                                        │
│     ├── Ask questions                                            │
│     ├── Identify examples                                        │
│     └── Agree on acceptance criteria                             │
│                           │                                      │
│                           ▼                                      │
│  2. FORMULATION (Write Scenarios)                                │
│     ├── Convert examples to Gherkin                              │
│     ├── Review with stakeholders                                 │
│     └── Refine language                                          │
│                           │                                      │
│                           ▼                                      │
│  3. AUTOMATION (Implement Steps)                                 │
│     ├── Write step definitions                                   │
│     ├── Implement application code                               │
│     ├── Run tests                                                │
│     └── Refactor                                                 │
│                           │                                      │
│                           ▼                                      │
│  4. DELIVER                                                      │
│     ├── Demo passing scenarios                                   │
│     ├── Update documentation                                     │
│     └── Iterate on feedback                                      │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Example: From Story to Scenarios

**User Story:**
```
As an online shopper
I want to filter products by price range
So that I can find products within my budget
```

**Three Amigos Discussion:**
```
Business: "Users should set min and max price"
Dev: "What happens if min > max?"
Business: "Show an error"
QA: "What about negative prices?"
Business: "Don't allow negative values"
QA: "Default filter values?"
Business: "Show all products by default"
```

**Resulting Scenarios:**
```gherkin
Feature: Product Price Filter
  As an online shopper
  I want to filter products by price range
  So that I can find products within my budget

  Scenario: Filter products within price range
    Given there are products priced at $10, $25, $50, and $100
    When I set the price filter from $20 to $60
    Then I should see products priced at $25 and $50
    And I should not see products priced at $10 and $100

  Scenario: Show all products by default
    Given there are products at various prices
    When I view the product list without filters
    Then I should see all products

  Scenario: Invalid price range
    When I set minimum price higher than maximum price
    Then I should see an error message
    And the filter should not be applied

  Scenario: Negative price not allowed
    When I try to enter a negative price
    Then the input should not accept the value
```

## Key Takeaways

1. **BDD** focuses on behavior, not implementation
2. **Three Amigos** ensures shared understanding before coding
3. **BDD extends TDD** to include business stakeholders
4. **Benefits**: living documentation, early bug detection, collaboration
5. **Avoid anti-patterns**: technical language, scenario explosion, no business involvement
6. **Workflow**: Discovery → Formulation → Automation

## Additional Resources

- [BDD in Action](https://www.manning.com/books/bdd-in-action) - Comprehensive BDD book by John Ferguson Smart
- [Cucumber BDD Guide](https://cucumber.io/docs/bdd/) - Official BDD introduction
- [Dan North's Introducing BDD](https://dannorth.net/introducing-bdd/) - Original BDD article by the creator

