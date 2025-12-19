# Scenario Outline and Examples

## Learning Objectives
- Use Scenario Outline for data-driven BDD testing
- Create Examples tables with parameterized test data
- Understand placeholder syntax for variable substitution
- Configure multiple Examples tables for different test data sets
- Handle data type conversion in step definitions
- Know when to use Scenario Outline vs regular Scenarios

## Why This Matters

Data-driven testing is essential when:
- Testing the same behavior with different inputs
- Validating boundary conditions
- Reducing test duplication
- Maintaining test readability

Scenario Outline transforms repetitive scenarios into maintainable, data-driven tests that are easy to extend and modify.

## The Concept

### Scenario Outline Overview

**Scenario Outline** allows you to run the same scenario multiple times with different data:

```
┌─────────────────────────────────────────────────────────────────┐
│              Scenario Outline Structure                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Scenario Outline: [Description with placeholders]             │
│     Given [step with <placeholder>]                             │
│     When [step with <placeholder>]                              │
│     Then [step with <placeholder>]                              │
│                                                                  │
│     Examples:                                                    │
│       | placeholder1 | placeholder2 | placeholder3 |            │
│       | value1a      | value2a      | value3a      |  ← Test 1 │
│       | value1b      | value2b      | value3b      |  ← Test 2 │
│       | value1c      | value2c      | value3c      |  ← Test 3 │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Basic Example

**Without Scenario Outline (Repetitive):**

```gherkin
Feature: Login Validation

  Scenario: Login with valid user john
    Given I am on the login page
    When I enter username "john" and password "pass123"
    Then I should see "Welcome, john"

  Scenario: Login with valid user jane
    Given I am on the login page
    When I enter username "jane" and password "secret456"
    Then I should see "Welcome, jane"

  Scenario: Login with valid user admin
    Given I am on the login page
    When I enter username "admin" and password "admin789"
    Then I should see "Welcome, admin"
```

**With Scenario Outline (Clean):**

```gherkin
Feature: Login Validation

  Scenario Outline: Login with valid credentials
    Given I am on the login page
    When I enter username "<username>" and password "<password>"
    Then I should see "Welcome, <username>"

    Examples:
      | username | password   |
      | john     | pass123    |
      | jane     | secret456  |
      | admin    | admin789   |
```

### Placeholder Syntax

Placeholders are denoted with angle brackets `<placeholder>`:

```gherkin
Scenario Outline: Product search
  Given the product catalog contains "<product>"
  When I search for "<search_term>"
  Then I should see <result_count> results
  And the first result should be "<expected_product>"

  Examples:
    | product           | search_term | result_count | expected_product  |
    | iPhone 15 Pro     | iPhone      | 5            | iPhone 15 Pro     |
    | Samsung Galaxy    | Samsung     | 3            | Samsung Galaxy    |
    | Google Pixel      | Pixel       | 2            | Google Pixel      |
```

**Placeholder Rules:**
- Use meaningful names: `<username>` not `<x>`
- Placeholders must match column headers exactly
- Case-sensitive matching
- Can appear anywhere in the step text

### Examples Tables

The **Examples** table provides data for each test iteration:

```gherkin
Scenario Outline: Price calculation with discount
  Given the product price is <original_price>
  When I apply a <discount_percent>% discount
  Then the final price should be <final_price>

  Examples: Standard discounts
    | original_price | discount_percent | final_price |
    | 100.00         | 10               | 90.00       |
    | 100.00         | 20               | 80.00       |
    | 100.00         | 50               | 50.00       |
```

**Table Format Rules:**
- First row is header (column names)
- Each subsequent row is a test iteration
- Columns separated by `|`
- Whitespace is trimmed
- Values are strings (converted by step definitions)

### Multiple Examples Tables

Use multiple Examples tables for categorized test data:

```gherkin
Scenario Outline: User registration validation
  Given I am on the registration page
  When I enter email "<email>"
  And I enter password "<password>"
  And I submit the form
  Then I should see "<message>"

  Examples: Valid registrations
    | email              | password      | message              |
    | john@example.com   | SecurePass1!  | Registration success |
    | jane@company.org   | MyP@ssw0rd    | Registration success |

  Examples: Invalid emails
    | email              | password      | message              |
    | invalid-email      | SecurePass1!  | Invalid email format |
    | @nodomain.com      | SecurePass1!  | Invalid email format |
    | missing@           | SecurePass1!  | Invalid email format |

  Examples: Invalid passwords
    | email              | password      | message                |
    | test@example.com   | short         | Password too short     |
    | test@example.com   | nospecialchar | Password needs special |
    | test@example.com   | NOLOWERCASE1! | Password needs lowercase|
```

**Benefits of Multiple Examples:**
- Organized by test category
- Clear documentation of test intent
- Easier to maintain
- Better reporting (shows which category failed)

### Named Examples Tables

Add descriptions to Examples tables:

```gherkin
Scenario Outline: Shopping cart calculation
  Given I have <quantity> items at $<price> each
  When I view my cart
  Then the subtotal should be $<subtotal>

  Examples: Single items
    | quantity | price  | subtotal |
    | 1        | 10.00  | 10.00    |
    | 1        | 25.50  | 25.50    |

  Examples: Multiple items
    | quantity | price  | subtotal |
    | 2        | 10.00  | 20.00    |
    | 5        | 10.00  | 50.00    |
    | 3        | 25.50  | 76.50    |

  Examples: Boundary values
    | quantity | price    | subtotal   |
    | 99       | 10.00    | 990.00     |
    | 1        | 9999.99  | 9999.99    |
```

### Data Type Conversion

Cucumber converts string values to appropriate types:

**Step Definition:**

```java
@Given("I have {int} items at ${double} each")
public void itemsAtPrice(int quantity, double price) {
    cart.addItems(quantity, price);
}

@Then("the subtotal should be ${double}")
public void subtotalShouldBe(double expected) {
    assertEquals(expected, cart.getSubtotal(), 0.01);
}
```

**Supported Type Conversions:**

| Cucumber Expression | Java Type | Example |
|---------------------|-----------|---------|
| `{int}` | int/Integer | 42 |
| `{long}` | long/Long | 42 |
| `{float}` | float/Float | 3.14 |
| `{double}` | double/Double | 3.14159 |
| `{word}` | String (no spaces) | hello |
| `{string}` | String (quoted) | "hello world" |
| `{}` | String | anything |
| `{bigdecimal}` | BigDecimal | 123.45 |
| `{biginteger}` | BigInteger | 123456789 |

### Complex Data in Examples

Use multiple columns for complex test cases:

```gherkin
Scenario Outline: Order processing with shipping
  Given a customer in "<country>"
  And an order total of $<order_total>
  When I calculate shipping for "<shipping_method>"
  Then the shipping cost should be $<shipping_cost>
  And the delivery time should be "<delivery_time>"
  And tax should be $<tax>

  Examples: US Shipping
    | country | order_total | shipping_method | shipping_cost | delivery_time | tax   |
    | USA     | 50.00       | Standard        | 5.99          | 5-7 days      | 4.50  |
    | USA     | 50.00       | Express         | 12.99         | 2-3 days      | 4.50  |
    | USA     | 100.00      | Standard        | 0.00          | 5-7 days      | 9.00  |

  Examples: International Shipping
    | country | order_total | shipping_method | shipping_cost | delivery_time  | tax    |
    | Canada  | 50.00       | Standard        | 15.99         | 7-14 days      | 6.50   |
    | UK      | 50.00       | Express         | 25.99         | 3-5 days       | 10.00  |
```

### When to Use Scenario Outline

**Use Scenario Outline When:**

```gherkin
# ✓ Same behavior, different data
Scenario Outline: Login attempts
  When I login with "<username>" and "<password>"
  Then I should see "<result>"
  
  Examples:
    | username | password | result  |
    | valid    | valid    | success |
    | valid    | invalid  | error   |
    | invalid  | valid    | error   |
```

**Use Regular Scenarios When:**

```gherkin
# ✓ Different behaviors requiring different steps
Scenario: Admin user sees admin panel
  Given I am logged in as an admin
  When I view the dashboard
  Then I should see the admin panel
  And I should see user management options

Scenario: Regular user sees limited dashboard
  Given I am logged in as a regular user
  When I view the dashboard
  Then I should not see the admin panel
  And I should see my profile options only
```

### Step Definition for Scenario Outline

```java
public class LoginSteps {
    
    private LoginPage loginPage;
    private DashboardPage dashboardPage;
    private String result;
    
    @When("I enter username {string} and password {string}")
    public void enterCredentials(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
    }
    
    @Then("I should see {string}")
    public void shouldSeeMessage(String expectedMessage) {
        if (expectedMessage.startsWith("Welcome")) {
            assertTrue(dashboardPage.isDisplayed());
            assertEquals(expectedMessage, dashboardPage.getWelcomeMessage());
        } else {
            assertEquals(expectedMessage, loginPage.getErrorMessage());
        }
    }
}
```

### Handling Special Characters

Escape special characters in Examples:

```gherkin
Scenario Outline: Search with special characters
  When I search for "<query>"
  Then I should see results for "<query>"

  Examples:
    | query           |
    | C++             |
    | C#              |
    | Rock & Roll     |
    | 50% off         |
    | $100 deals      |
    | "exact phrase"  |
```

### Best Practices

```
┌─────────────────────────────────────────────────────────────────┐
│         Scenario Outline Best Practices                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  DO:                                                             │
│  ✓ Use meaningful placeholder names                             │
│  ✓ Group related examples in named tables                       │
│  ✓ Include boundary values and edge cases                       │
│  ✓ Keep examples focused on one variation type                  │
│  ✓ Document what each Examples table is testing                 │
│                                                                  │
│  DON'T:                                                          │
│  ✗ Create too many columns (hard to read)                       │
│  ✗ Mix unrelated test cases in one outline                      │
│  ✗ Use Scenario Outline for 1-2 examples (use Scenario)         │
│  ✗ Duplicate data that could be in Background                   │
│  ✗ Include data that doesn't affect the test                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Example: Complete Data-Driven Feature

```gherkin
Feature: E-commerce checkout validation
  As a customer
  I want the checkout to validate my inputs
  So that I can complete purchases correctly

  Background:
    Given I am logged in as a customer
    And I have items in my cart

  Scenario Outline: Credit card validation
    Given I am on the payment page
    When I enter card number "<card_number>"
    And I enter expiry "<expiry>"
    And I enter CVV "<cvv>"
    Then I should see validation message "<message>"

    Examples: Valid cards
      | card_number         | expiry | cvv | message |
      | 4111111111111111    | 12/25  | 123 | Valid   |
      | 5500000000000004    | 06/26  | 456 | Valid   |

    Examples: Invalid card numbers
      | card_number         | expiry | cvv | message              |
      | 1234567890123456    | 12/25  | 123 | Invalid card number  |
      | 411111111111        | 12/25  | 123 | Card number too short|
      |                     | 12/25  | 123 | Card number required |

    Examples: Invalid expiry dates
      | card_number         | expiry | cvv | message        |
      | 4111111111111111    | 01/20  | 123 | Card expired   |
      | 4111111111111111    | 13/25  | 123 | Invalid month  |
      | 4111111111111111    |        | 123 | Expiry required|

    Examples: Invalid CVV
      | card_number         | expiry | cvv  | message       |
      | 4111111111111111    | 12/25  | 12   | CVV too short |
      | 4111111111111111    | 12/25  | 1234 | CVV too long  |
      | 4111111111111111    | 12/25  |      | CVV required  |
```

## Key Takeaways

1. **Scenario Outline** runs the same scenario with different data sets
2. **Placeholders** (`<name>`) are replaced with values from Examples table
3. **Examples tables** provide test data with header row and data rows
4. **Multiple Examples** tables organize test data by category
5. **Data type conversion** happens automatically in step definitions
6. **Use Scenario Outline** when testing same behavior with different inputs

## Additional Resources

- [Cucumber Scenario Outline](https://cucumber.io/docs/gherkin/reference/#scenario-outline) - Official documentation
- [Data Tables Reference](https://cucumber.io/docs/gherkin/reference/#data-tables) - Complex data handling
- [Cucumber Expressions](https://cucumber.io/docs/cucumber/cucumber-expressions/) - Parameter type conversion

