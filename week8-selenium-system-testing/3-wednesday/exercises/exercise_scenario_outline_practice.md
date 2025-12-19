# Exercise 3: Scenario Outline & Examples

## Objective

Convert existing scenarios to Scenario Outline with Examples tables to create data-driven BDD tests. Practice parameterized testing with multiple data sets.

## Learning Goals

- Convert repetitive scenarios to Scenario Outline
- Design effective Examples tables
- Use multiple Examples tables for grouping
- Handle different data types in Examples
- Understand when Scenario Outline is appropriate

## Time Estimate

45 minutes

---

## Core Tasks

### Task 1: Login Data-Driven Tests (15 minutes)

Convert individual login scenarios to a Scenario Outline.

**Before (Multiple similar scenarios):**
```gherkin
Scenario: Login with valid user tomsmith
  Given the user is on the login page
  When the user enters username "tomsmith"
  And the user enters password "SuperSecretPassword!"
  And the user clicks login
  Then the login should be successful

Scenario: Login with valid user janesmith
  Given the user is on the login page
  When the user enters username "janesmith"
  And the user enters password "SecretPass456!"
  And the user clicks login
  Then the login should be successful
```

**Task: Create `features/login_data_driven.feature`:**
```gherkin
@login @data-driven
Feature: Data-Driven Login Testing
  As a QA engineer
  I want to test login with multiple credentials
  So that I can verify the authentication system works for all users

  Background:
    Given the user is on the login page

  @positive
  Scenario Outline: Successful login with valid credentials
    When the user enters username "<username>"
    And the user enters password "<password>"
    And the user clicks the login button
    Then the login should be "<result>"
    And the user should see message "<message>"

    # TODO: Create Examples table with at least 3 valid users
    Examples: Valid Users
      | username | password             | result  | message                          |
      | tomsmith | SuperSecretPassword! | success | You logged into a secure area!   |
      |          |                      |         |                                  |
      |          |                      |         |                                  |

  @negative
  Scenario Outline: Failed login with invalid credentials
    When the user enters username "<username>"
    And the user enters password "<password>"
    And the user clicks the login button
    Then the login should be "<result>"
    And the user should see message "<message>"

    # TODO: Create Examples table with various failure cases
    Examples: Invalid Credentials
      | username    | password    | result | message                    |
      | tomsmith    | wrongpass   | failed | Your password is invalid!  |
      |             |             |        |                            |
      |             |             |        |                            |
      |             |             |        |                            |

    Examples: Empty Credentials
      | username | password | result | message                    |
      |          |          | failed | Your username is invalid!  |
      |          |          |        |                            |
```

### Task 2: Form Validation Tests (15 minutes)

Create data-driven validation tests.

**Create `features/form_validation.feature`:**
```gherkin
@validation
Feature: Form Field Validation
  As a user
  I want to receive clear validation messages
  So that I can correctly fill out forms

  @email-validation
  Scenario Outline: Email field validation
    Given the user is on the registration page
    When the user enters email "<email>"
    And the user submits the form
    Then the email validation result should be "<valid>"
    And the validation message should be "<message>"

    # TODO: Create comprehensive Examples for email validation
    Examples: Valid Emails
      | email                    | valid | message |
      | user@example.com         | yes   | Valid   |
      | user.name@example.co.uk  |       |         |
      | user+tag@example.com     |       |         |

    Examples: Invalid Emails - Format Issues
      | email              | valid | message                    |
      | userexample.com    | no    | Email must contain @       |
      | user@              |       |                            |
      | @example.com       |       |                            |
      | user @example.com  |       |                            |

    Examples: Invalid Emails - Empty
      | email | valid | message                  |
      |       | no    | Email is required        |

  @password-validation
  Scenario Outline: Password strength validation
    Given the user is on the registration page
    When the user enters password "<password>"
    And the user submits the form
    Then the password strength should be "<strength>"
    And the validation message should be "<message>"

    # TODO: Create Examples for password strength
    # Consider: length, uppercase, lowercase, numbers, special chars
    Examples: Password Strength
      | password        | strength | message                              |
      | abc             | weak     | Password must be at least 8 chars    |
      | abcdefgh        |          |                                      |
      | Abcdefgh        |          |                                      |
      | Abcdefg1        |          |                                      |
      | Abcdef1!        |          |                                      |

  @age-validation
  Scenario Outline: Age boundary validation
    Given the user is on the registration page
    When the user enters age "<age>"
    And the user submits the form
    Then the age validation result should be "<valid>"

    # TODO: Create Examples using boundary value analysis
    # Valid age range: 18-120
    Examples: Boundary Values
      | age | valid | description          |
      | 17  | no    | Below minimum        |
      | 18  |       | At minimum boundary  |
      | 19  |       | Just above minimum   |
      | 119 |       | Just below maximum   |
      | 120 |       | At maximum boundary  |
      | 121 |       | Above maximum        |
```

### Task 3: Multi-Browser Testing (10 minutes)

Create Scenario Outline for cross-browser testing.

**Create `features/cross_browser.feature`:**
```gherkin
@cross-browser
Feature: Cross-Browser Compatibility
  As a QA engineer
  I want to verify the application works across browsers
  So that all users have a consistent experience

  @smoke
  Scenario Outline: Homepage loads correctly in different browsers
    Given the user opens "<browser>" browser
    When the user navigates to the homepage
    Then the page title should be "The Internet"
    And the main heading should be visible
    And the navigation links should be functional

    Examples: Desktop Browsers
      | browser  |
      | chrome   |
      | firefox  |
      | edge     |

    Examples: Mobile Emulation
      | browser         |
      | chrome-mobile   |
      | safari-mobile   |

  Scenario Outline: Login works across browsers and screen sizes
    Given the user opens "<browser>" browser
    And the viewport is set to "<width>" x "<height>"
    When the user logs in with valid credentials
    Then the login should be successful

    # TODO: Create Examples with browser/viewport combinations
    Examples: Desktop Viewports
      | browser | width | height |
      | chrome  | 1920  | 1080   |
      |         |       |        |
      |         |       |        |

    Examples: Tablet Viewports
      | browser | width | height |
      |         |       |        |
      |         |       |        |

    Examples: Mobile Viewports
      | browser | width | height |
      |         |       |        |
      |         |       |        |
```

### Task 4: Implement Step Definitions (5 minutes)

Create step definitions that handle parameterized scenarios:

```java
package com.training.cucumber.stepdefinitions;

import io.cucumber.java.en.*;

public class DataDrivenSteps {

    @When("the user enters email {string}")
    public void enterEmail(String email) {
        // TODO: Implement
        // Handle empty string case
        if (email != null && !email.isEmpty()) {
            driver.findElement(By.id("email")).sendKeys(email);
        }
    }

    @Then("the email validation result should be {string}")
    public void validateEmailResult(String expectedValid) {
        // TODO: Implement
        boolean isValid = expectedValid.equalsIgnoreCase("yes");
        // Assert based on expected validation
    }

    @Given("the user opens {string} browser")
    public void openBrowser(String browserName) {
        // TODO: Implement browser factory
        switch (browserName.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            // Add more browsers
        }
    }

    @Given("the viewport is set to {string} x {string}")
    public void setViewport(String width, String height) {
        // TODO: Implement viewport setting
        driver.manage().window().setSize(
            new Dimension(Integer.parseInt(width), Integer.parseInt(height))
        );
    }
}
```

---

## When to Use Scenario Outline

### ✅ Use Scenario Outline When:
- Testing the same flow with different data
- Validating boundary values
- Cross-browser testing
- Testing multiple user roles
- Negative testing with various invalid inputs

### ❌ Avoid Scenario Outline When:
- Only 1-2 data variations exist
- Steps differ significantly between cases
- Each case needs unique assertions
- Readability would suffer

---

## Definition of Done

- [ ] Login data-driven feature has 2 Scenario Outlines with Examples
- [ ] Form validation has email, password, and age validation Examples
- [ ] Boundary value analysis applied to age validation
- [ ] Cross-browser feature uses multiple Examples groups
- [ ] At least 20 total example rows across all features
- [ ] Step definitions handle parameterized data correctly

---

## Hints

<details>
<summary>Hint: Handling Empty Strings in Examples</summary>

In Examples tables, empty cells become empty strings:
```gherkin
Examples:
  | email    |
  |          |  <- This becomes ""
```

Handle in step definition:
```java
@When("the user enters email {string}")
public void enterEmail(String email) {
    if (!email.isEmpty()) {
        emailField.sendKeys(email);
    }
    // Leave field empty if email is empty string
}
```
</details>

<details>
<summary>Hint: Multiple Examples Tables</summary>

Use descriptive names for Examples groups:
```gherkin
Examples: Happy Path
  | ... |

Examples: Edge Cases
  | ... |

Examples: Error Conditions
  | ... |
```

This makes reports more readable and allows tag filtering.
</details>

