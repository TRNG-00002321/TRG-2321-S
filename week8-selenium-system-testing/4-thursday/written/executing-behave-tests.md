# Executing Behave Tests

## Learning Objectives
- Run Behave tests from the command line
- Execute specific features, scenarios, and tags
- Use verbose output and dry run modes
- Integrate Behave with IDEs (PyCharm, VS Code)
- Configure parallel test execution

## Why This Matters

Knowing multiple ways to execute Behave tests enables efficient workflows:
- Quick feedback during development
- Selective testing for debugging
- CI/CD pipeline integration
- Parallel execution for faster test suites

## The Concept

### Basic Execution

**Run all tests:**
```bash
# Run all feature files in features/ directory
behave

# Specify features directory
behave features/

# Run from project root
python -m behave
```

### Running Specific Features

```bash
# Single feature file
behave features/login.feature

# Multiple feature files
behave features/login.feature features/checkout.feature

# Features in subdirectory
behave features/authentication/
```

### Running by Tags

```bash
# Single tag
behave --tags=@smoke

# AND condition - both tags required
behave --tags="@smoke and @login"

# OR condition - either tag
behave --tags="@smoke or @regression"

# NOT condition - exclude tag
behave --tags="not @wip"

# Complex expressions
behave --tags="(@smoke or @regression) and not @slow"
behave --tags="@feature-login and (@positive or @negative)"
```

### Running by Scenario Name

```bash
# Exact name match
behave --name="Successful login"

# Pattern match (regex)
behave --name=".*login.*"

# Multiple patterns
behave --name=".*login.*" --name=".*logout.*"
```

### Verbose Output

```bash
# Show more details
behave --verbose

# Show step source locations
behave --show-source

# Show timings
behave --show-timings

# All verbose options
behave --verbose --show-source --show-timings
```

### Dry Run Mode

Validate step definitions without executing:

```bash
# Check all steps have definitions
behave --dry-run

# Useful output for undefined steps
behave --dry-run --format=plain
```

**Dry Run Output:**
```
Feature: Login
  Scenario: Successful login
    Given I am on the login page ... passed
    When I enter valid credentials ... passed
    Then I should see the dashboard ... undefined

You can implement step definitions for undefined steps with these snippets:

@then(u'I should see the dashboard')
def step_impl(context):
    raise NotImplementedError(u'STEP: Then I should see the dashboard')
```

### IDE Integration

#### PyCharm

**Setup:**
1. Install "Gherkin" plugin (bundled)
2. Configure Behave as test runner:
   - File → Settings → Tools → Python Integrated Tools
   - Default test runner: Behave

**Running Tests:**
- Right-click `.feature` file → Run
- Click green arrow in gutter next to scenario
- Use Run Configuration for custom options

**Run Configuration:**
```
Name: Behave Smoke Tests
Script: behave
Parameters: --tags=@smoke --format=pretty
Working directory: $PROJECT_DIR$
```

#### VS Code

**Setup:**
1. Install "Cucumber (Gherkin) Full Support" extension
2. Install "Python" extension

**Running Tests:**
```json
// .vscode/launch.json
{
    "version": "0.2.0",
    "configurations": [
        {
            "name": "Behave: All Tests",
            "type": "python",
            "request": "launch",
            "module": "behave",
            "console": "integratedTerminal"
        },
        {
            "name": "Behave: Current Feature",
            "type": "python",
            "request": "launch",
            "module": "behave",
            "args": ["${file}"],
            "console": "integratedTerminal"
        },
        {
            "name": "Behave: Smoke Tests",
            "type": "python",
            "request": "launch",
            "module": "behave",
            "args": ["--tags=@smoke"],
            "console": "integratedTerminal"
        }
    ]
}
```

### Output Formats

```bash
# Pretty format (default, colored)
behave --format=pretty

# Plain format (no colors)
behave --format=plain

# Progress format (dots)
behave --format=progress

# JSON format
behave --format=json

# JSON to file
behave --format=json --outfile=report.json

# Multiple formats simultaneously
behave --format=pretty --format=json --outfile=report.json
```

### JUnit XML Output

For CI/CD integration:

```bash
# Generate JUnit XML
behave --junit

# Specify output directory
behave --junit --junit-directory=reports/

# Combined with other formats
behave --format=progress --junit --junit-directory=reports/
```

### Parallel Execution

**Using behave-parallel:**

```bash
# Install
pip install behave-parallel

# Run with parallel processes
behave-parallel --processes=4

# Or using command line
behave --processes 4 --parallel-element scenario
```

**Using Python's concurrent features:**

```python
# parallel_runner.py
import subprocess
import concurrent.futures
import glob

def run_feature(feature_file):
    """Run a single feature file"""
    result = subprocess.run(
        ['behave', feature_file, '--format=json', f'--outfile={feature_file}.json'],
        capture_output=True,
        text=True
    )
    return feature_file, result.returncode

def run_parallel():
    """Run all features in parallel"""
    feature_files = glob.glob('features/*.feature')
    
    with concurrent.futures.ThreadPoolExecutor(max_workers=4) as executor:
        futures = {executor.submit(run_feature, f): f for f in feature_files}
        
        for future in concurrent.futures.as_completed(futures):
            feature, status = future.result()
            print(f"{feature}: {'PASSED' if status == 0 else 'FAILED'}")

if __name__ == '__main__':
    run_parallel()
```

### Controlling Test Execution

```bash
# Stop on first failure
behave --stop

# Continue on failure (default)
behave --no-stop

# Rerun failed scenarios
behave --format=rerun --outfile=rerun.txt
behave @rerun.txt  # Run failed scenarios

# Exclude certain features
behave --exclude="wip"

# Include specific pattern
behave --include="login"
```

### Environment Variables

```bash
# Set environment for tests
ENVIRONMENT=staging behave

# Multiple variables
BROWSER=firefox BASE_URL=https://staging.example.com behave

# Access in environment.py
# context.config.userdata.get('ENVIRONMENT')
```

### Complete CI/CD Example

**GitHub Actions workflow:**

```yaml
name: Behave Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up Python
      uses: actions/setup-python@v4
      with:
        python-version: '3.10'
    
    - name: Install dependencies
      run: |
        pip install -r requirements.txt
        pip install behave allure-behave
    
    - name: Run smoke tests
      run: behave --tags=@smoke --format=pretty --junit
    
    - name: Run regression tests
      if: github.ref == 'refs/heads/main'
      run: behave --tags=@regression --junit
    
    - name: Upload test results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-results
        path: reports/
```

### Execution Best Practices

```
┌─────────────────────────────────────────────────────────────────┐
│              Behave Execution Best Practices                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Development:                                                    │
│  ✓ Use --stop for quick feedback                                │
│  ✓ Use --dry-run to verify step mappings                        │
│  ✓ Run single features during development                       │
│                                                                  │
│  CI/CD:                                                          │
│  ✓ Use --junit for XML reports                                  │
│  ✓ Use plain format to avoid ANSI codes in logs                 │
│  ✓ Tag-based execution for different pipeline stages            │
│                                                                  │
│  Performance:                                                    │
│  ✓ Use parallel execution for large test suites                 │
│  ✓ Use rerun for failed test recovery                           │
│  ✓ Organize tests with tags for selective runs                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Key Takeaways

1. `behave` runs all features by default
2. **Tags** enable flexible test filtering
3. **Dry run** validates step definitions
4. **JUnit output** integrates with CI/CD
5. **Parallel execution** speeds up large test suites
6. **IDE integration** provides development convenience

## Additional Resources

- [Behave Command Line Reference](https://behave.readthedocs.io/en/stable/behave.html) - Complete CLI options
- [Behave Parallel](https://pypi.org/project/behave-parallel/) - Parallel execution package
- [PyCharm Behave Support](https://www.jetbrains.com/help/pycharm/bdd-frameworks.html) - IDE configuration

