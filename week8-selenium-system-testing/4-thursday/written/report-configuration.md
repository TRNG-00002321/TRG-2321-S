# Behave Reporting Configuration

## Learning Objectives
- Configure built-in Behave formatters (pretty, progress, json, junit)
- Create custom formatters for specialized output
- Integrate Allure reporting with Behave
- Generate HTML reports for stakeholders
- Configure reporting for CI/CD integration

## Why This Matters

Effective reporting enables:
- Stakeholder visibility into test results
- CI/CD pipeline integration with JUnit XML
- Rich visual reports with Allure
- Historical test analysis and trends
- Failure debugging with detailed output

## The Concept

### Built-in Formatters

```
┌─────────────────────────────────────────────────────────────────┐
│                    Behave Built-in Formatters                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Formatter    │  Output           │  Use Case                   │
│  ────────────────────────────────────────────────────────────   │
│  pretty       │  Colored console  │  Development                │
│  plain        │  Plain text       │  CI logs                    │
│  progress     │  Dots (.F)        │  Quick status               │
│  json         │  JSON file        │  Report tools               │
│  json.pretty  │  Formatted JSON   │  Debugging                  │
│  junit        │  JUnit XML        │  CI/CD (Jenkins, etc.)      │
│  rerun        │  Failed list      │  Re-running failures        │
│  null         │  No output        │  Performance tests          │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Pretty Formatter (Default)

Human-readable colored output:

```bash
behave --format=pretty
```

**Output:**
```
Feature: User Login # features/login.feature:1
  As a registered user
  I want to log in
  So that I can access my account

  Scenario: Successful login                    # features/login.feature:6
    Given I am on the login page               # features/steps/login.py:5
    When I enter valid credentials             # features/steps/login.py:10
    Then I should see the dashboard            # features/steps/login.py:15

1 feature passed, 0 failed, 0 skipped
1 scenario passed, 0 failed, 0 skipped
3 steps passed, 0 failed, 0 skipped
```

### JSON Formatter

Machine-readable output for reporting tools:

```bash
# JSON to stdout
behave --format=json

# JSON to file
behave --format=json --outfile=report.json

# Pretty JSON (formatted)
behave --format=json.pretty --outfile=report.json
```

**Sample JSON Output:**
```json
[
  {
    "keyword": "Feature",
    "name": "User Login",
    "location": "features/login.feature:1",
    "elements": [
      {
        "keyword": "Scenario",
        "name": "Successful login",
        "steps": [
          {
            "keyword": "Given",
            "name": "I am on the login page",
            "result": {
              "status": "passed",
              "duration": 0.123
            }
          }
        ]
      }
    ]
  }
]
```

### JUnit XML Formatter

CI/CD integration format:

```bash
# Generate JUnit XML
behave --junit

# Specify output directory
behave --junit --junit-directory=reports/junit

# Combined with other formatters
behave --format=progress --junit --junit-directory=reports/
```

**JUnit XML Structure:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<testsuite name="login" tests="3" errors="0" failures="0" skipped="0">
  <testcase classname="login.Successful login" 
            name="Given I am on the login page" 
            time="0.123"/>
  <testcase classname="login.Successful login" 
            name="When I enter valid credentials" 
            time="0.456"/>
  <testcase classname="login.Successful login" 
            name="Then I should see the dashboard" 
            time="0.789"/>
</testsuite>
```

### Multiple Formatters

Use multiple formatters simultaneously:

```bash
# Console + JSON + JUnit
behave \
  --format=pretty \
  --format=json --outfile=reports/report.json \
  --junit --junit-directory=reports/junit/
```

### Allure Integration

**Installation:**
```bash
pip install allure-behave
```

**Usage:**
```bash
# Run with Allure formatter
behave -f allure_behave.formatter:AllureFormatter -o reports/allure

# Generate HTML report
allure serve reports/allure

# Or generate static report
allure generate reports/allure -o reports/allure-html --clean
```

**behave.ini Configuration:**
```ini
[behave]
format = allure_behave.formatter:AllureFormatter
outfiles = reports/allure
```

**Allure Annotations in Steps:**
```python
import allure
from behave import given, when, then

@given('I am on the login page')
@allure.step('Navigate to login page')
def step_on_login_page(context):
    context.browser.get(context.base_url + '/login')

@when('I enter username "{username}"')
@allure.step('Enter username: {username}')
def step_enter_username(context, username):
    context.login_page.enter_username(username)

@then('I should see error "{message}"')
@allure.step('Verify error message: {message}')
def step_see_error(context, message):
    with allure.step('Get error text'):
        actual = context.login_page.get_error()
    with allure.step('Compare with expected'):
        assert message in actual
```

**Attaching Screenshots:**
```python
import allure

def after_scenario(context, scenario):
    if scenario.status == 'failed' and hasattr(context, 'browser'):
        # Attach screenshot to Allure report
        screenshot = context.browser.get_screenshot_as_png()
        allure.attach(
            screenshot,
            name='failure_screenshot',
            attachment_type=allure.attachment_type.PNG
        )
```

### HTML Report Generation

**Using behave-html-formatter:**

```bash
# Install
pip install behave-html-formatter

# Run with HTML formatter
behave -f behave_html_formatter:HTMLFormatter -o reports/report.html

# Or via behave.ini
```

**Custom HTML Report from JSON:**
```python
"""
generate_report.py
Generate HTML report from Behave JSON output
"""
import json
from datetime import datetime

def generate_html_report(json_file, output_file):
    with open(json_file) as f:
        results = json.load(f)
    
    html = f"""
    <!DOCTYPE html>
    <html>
    <head>
        <title>Behave Test Report</title>
        <style>
            body {{ font-family: Arial, sans-serif; margin: 20px; }}
            .passed {{ color: green; }}
            .failed {{ color: red; }}
            .skipped {{ color: orange; }}
            .feature {{ margin: 20px 0; padding: 15px; border: 1px solid #ddd; }}
            .scenario {{ margin: 10px 0; padding: 10px; background: #f5f5f5; }}
            .step {{ margin: 5px 0 5px 20px; }}
        </style>
    </head>
    <body>
        <h1>Behave Test Report</h1>
        <p>Generated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
    """
    
    for feature in results:
        html += f'<div class="feature"><h2>{feature["name"]}</h2>'
        
        for element in feature.get('elements', []):
            if element['keyword'] == 'Scenario':
                status = 'passed'
                for step in element.get('steps', []):
                    if step.get('result', {}).get('status') == 'failed':
                        status = 'failed'
                        break
                
                html += f'<div class="scenario {status}">'
                html += f'<h3>{element["name"]}</h3>'
                
                for step in element.get('steps', []):
                    step_status = step.get('result', {}).get('status', 'skipped')
                    html += f'<div class="step {step_status}">'
                    html += f'{step["keyword"]} {step["name"]} - {step_status}'
                    html += '</div>'
                
                html += '</div>'
        
        html += '</div>'
    
    html += '</body></html>'
    
    with open(output_file, 'w') as f:
        f.write(html)

if __name__ == '__main__':
    generate_html_report('reports/report.json', 'reports/report.html')
```

### CI/CD Report Integration

**GitHub Actions:**
```yaml
name: Behave Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Setup Python
      uses: actions/setup-python@v4
      with:
        python-version: '3.10'
    
    - name: Install dependencies
      run: |
        pip install -r requirements.txt
        pip install allure-behave
    
    - name: Run tests
      run: |
        behave --junit --junit-directory=reports/junit \
               -f allure_behave.formatter:AllureFormatter \
               -o reports/allure
    
    - name: Publish Test Results
      uses: EnricoMi/publish-unit-test-result-action@v2
      if: always()
      with:
        files: reports/junit/*.xml
    
    - name: Generate Allure Report
      uses: simple-elf/allure-report-action@master
      if: always()
      with:
        allure_results: reports/allure
    
    - name: Upload Allure Report
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: allure-report
        path: allure-report/
```

**Jenkins Pipeline:**
```groovy
pipeline {
    agent any
    
    stages {
        stage('Test') {
            steps {
                sh '''
                    pip install -r requirements.txt
                    behave --junit --junit-directory=reports/
                '''
            }
            post {
                always {
                    junit 'reports/*.xml'
                }
            }
        }
    }
}
```

### Configuration Best Practices

```ini
# behave.ini
[behave]
# Development
format = pretty
show_source = true
show_timings = true

# Always generate JSON for tooling
# Use --format=json --outfile=report.json on CI

# JUnit for CI
junit = false
junit_directory = reports/junit

# Allure (when using)
# format = allure_behave.formatter:AllureFormatter
# outfiles = reports/allure
```

### Complete Reporting Setup

```python
"""
features/environment.py
With comprehensive reporting support
"""
import os
import json
from datetime import datetime

def before_all(context):
    # Create reports directory
    context.reports_dir = 'reports'
    os.makedirs(context.reports_dir, exist_ok=True)
    
    # Initialize results tracking
    context.test_results = {
        'start_time': datetime.now().isoformat(),
        'features': [],
        'summary': {
            'passed': 0,
            'failed': 0,
            'skipped': 0
        }
    }

def after_scenario(context, scenario):
    # Track results
    if scenario.status == 'passed':
        context.test_results['summary']['passed'] += 1
    elif scenario.status == 'failed':
        context.test_results['summary']['failed'] += 1
    else:
        context.test_results['summary']['skipped'] += 1

def after_all(context):
    # Finalize results
    context.test_results['end_time'] = datetime.now().isoformat()
    
    # Write custom summary
    summary_file = os.path.join(context.reports_dir, 'summary.json')
    with open(summary_file, 'w') as f:
        json.dump(context.test_results, f, indent=2)
    
    print(f"\n{'='*50}")
    print("TEST SUMMARY")
    print(f"{'='*50}")
    print(f"Passed:  {context.test_results['summary']['passed']}")
    print(f"Failed:  {context.test_results['summary']['failed']}")
    print(f"Skipped: {context.test_results['summary']['skipped']}")
    print(f"{'='*50}")
```

## Key Takeaways

1. **Built-in formatters** cover most common needs
2. **JUnit XML** integrates with CI/CD systems
3. **Allure** provides rich visual reports
4. **Multiple formatters** can run simultaneously
5. **JSON output** enables custom report generation
6. **CI/CD integration** uses JUnit + artifact uploads

## Additional Resources

- [Behave Formatters](https://behave.readthedocs.io/en/stable/formatters.html) - Official formatter documentation
- [Allure Behave](https://docs.qameta.io/allure/#_behave) - Allure integration guide
- [behave-html-formatter](https://pypi.org/project/behave-html-formatter/) - HTML formatter package

