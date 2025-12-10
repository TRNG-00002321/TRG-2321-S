# Allure Overview: Report Structure and Features

## Learning Objectives
- Navigate Allure report structure and sections
- Understand steps, attachments, and links
- Categorize test failures effectively
- Differentiate between reports and raw results

## Why This Matters

An Allure report contains rich information—but only if you understand how to use it. Knowing the report structure helps you quickly diagnose failures, track trends, and communicate testing status to your team. This knowledge transforms Allure from a pretty report generator into a powerful quality analysis tool.

## The Concept

### Report Structure

```
┌─────────────────────────────────────────────────────────────────┐
│                      ALLURE REPORT LAYOUT                        │
├─────────────────────────────────────────────────────────────────┤
│  OVERVIEW      │  Summary dashboard, pass/fail pie chart        │
│  CATEGORIES    │  Failures grouped by type                      │
│  SUITES        │  Tests organized by test class/module          │
│  GRAPHS        │  Trend charts, duration analysis               │
│  TIMELINE      │  Test execution timeline                       │
│  BEHAVIORS     │  BDD-style feature/story organization          │
│  PACKAGES      │  Tests by package/directory structure          │
└─────────────────────────────────────────────────────────────────┘
```

### Overview Dashboard

The landing page provides at-a-glance status:

- **Pass/Fail pie chart**: Overall test health
- **Severity breakdown**: Critical, high, normal, low
- **Duration**: Total execution time
- **Trend**: Comparison with previous runs
- **Environment**: Test environment details

### Test Details Panel

Each test shows:

```
┌─────────────────────────────────────────────────────────────────┐
│  Test: test_user_creation                                        │
├─────────────────────────────────────────────────────────────────┤
│  Status: PASSED ✓                                                │
│  Duration: 0.234s                                                │
│  Severity: CRITICAL                                              │
│                                                                  │
│  Steps:                                                          │
│    1. Create user with valid data                                │
│    2. Verify user saved to database                              │
│    3. Verify confirmation email sent                             │
│                                                                  │
│  Attachments:                                                    │
│    📷 Screenshot                                                 │
│    📄 Request log                                                │
│    📄 Response body                                              │
│                                                                  │
│  Links:                                                          │
│    🔗 JIRA-1234 (issue)                                          │
│    🔗 TMS-5678 (test case)                                       │
└─────────────────────────────────────────────────────────────────┘
```

### Steps

Steps break down test execution into logical units:

```python
import allure

@allure.step("Create user with name {name}")
def create_user(name, email):
    # Step 1 logic
    pass

@allure.step("Verify user exists in database")
def verify_user_in_db(user_id):
    # Step 2 logic
    pass
```

### Attachments

Add context to test results:

- **Screenshots**: Capture UI state at failure
- **Logs**: Request/response data
- **Files**: Configuration, test data
- **HTML**: Rendered responses

### Links

Connect tests to external systems:

- **Issue links**: Bug tracker references
- **TMS links**: Test management system cases
- **Custom links**: Documentation, requirements

### Categories

Classify failures for analysis:

| Category | Description |
|----------|-------------|
| Product defects | Real bugs in the application |
| Test defects | Issues in test code |
| Infrastructure | Environment/setup issues |
| Flaky tests | Intermittent failures |

### Allure Results vs Reports

```
┌──────────────────────┐         ┌──────────────────────┐
│   ALLURE RESULTS     │   →→→   │    ALLURE REPORT     │
├──────────────────────┤         ├──────────────────────┤
│  JSON files          │         │  HTML files          │
│  allure-results/     │  allure │  allure-report/      │
│  ├── xxx-result.json │ generate│  ├── index.html      │
│  ├── xxx-container   │   →→→   │  ├── data/           │
│  └── attachments/    │         │  └── widgets/        │
└──────────────────────┘         └──────────────────────┘
     (Raw data)                      (Interactive HTML)
```

## Code Example

### Annotated Test with Rich Reporting

```python
import allure

@allure.epic("User Management")
@allure.feature("Registration")
@allure.story("New User Signup")
@allure.severity(allure.severity_level.CRITICAL)
class TestUserRegistration:
    
    @allure.title("Successful user registration with valid data")
    @allure.description("Verify that users can register with valid email and password")
    def test_successful_registration(self):
        with allure.step("Navigate to registration page"):
            # navigation code
            pass
        
        with allure.step("Fill registration form"):
            # form filling code
            pass
        
        with allure.step("Submit and verify success"):
            # assertion code
            allure.attach("user@example.com", name="Registered Email", 
                         attachment_type=allure.attachment_type.TEXT)
```

## Summary

- **Overview dashboard** provides at-a-glance test health
- **Steps** break down test execution into logical phases
- **Attachments** add context (screenshots, logs, files)
- **Links** connect to issue trackers and test management
- **Categories** classify failures for analysis
- **Results → Reports**: Raw JSON transformed to interactive HTML
- Reports enable **stakeholder communication** and **trend analysis**

## Additional Resources

- [Allure Report Features](https://docs.qameta.io/allure-report/) - Feature guide
- [Report Demo](https://demo.qameta.io/allure/) - Interactive example
- [Categories Configuration](https://docs.qameta.io/allure/#_categories) - Custom categories

