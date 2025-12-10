# Generating Test Reports: From Results to Stakeholder-Ready Documentation

## Learning Objectives
- Generate Allure reports from test results
- Use `allure serve` for local review
- Use `allure generate` for shareable reports
- Integrate Allure reporting into CI/CD pipelines

## Why This Matters

Running tests produces results; generating reports transforms those results into actionable insights. Whether reviewing locally, sharing with your team, or publishing from CI/CD, understanding report generation is the final step in creating professional test documentation.

## The Concept

### The Report Generation Flow

```
┌───────────┐      ┌────────────────┐      ┌──────────────┐
│ Run Tests │ ───► │ Allure Results │ ───► │ Allure Report│
│           │      │ (JSON files)   │      │ (HTML)       │
└───────────┘      └────────────────┘      └──────────────┘
                          │                       │
                     allure-results/         allure-report/
```

### Generating Results

**Pytest:**
```bash
pytest --alluredir=allure-results tests/
```

**JUnit5 (Maven):**
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.1.2</version>
            <configuration>
                <argLine>
                    -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/1.9.19/aspectjweaver-1.9.19.jar"
                </argLine>
                <systemPropertyVariables>
                    <allure.results.directory>${project.build.directory}/allure-results</allure.results.directory>
                </systemPropertyVariables>
            </configuration>
        </plugin>
    </plugins>
</build>
```

```bash
mvn clean test
# Results in target/allure-results/
```

### allure serve: Quick Local Review

Opens report in browser, auto-refreshes:

```bash
# Serve from results directory
allure serve allure-results

# Opens browser automatically at http://localhost:port
```

**Use for:**
- Local development
- Quick review of test run
- Debugging failures

### allure generate: Shareable Reports

Creates static HTML files:

```bash
# Generate report
allure generate allure-results -o allure-report --clean

# Open generated report
allure open allure-report
```

**Use for:**
- Archiving reports
- Sharing with team
- Publishing to web server
- CI/CD artifacts

### CI/CD Integration

**GitHub Actions:**
```yaml
name: Tests with Allure

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.11'
      
      - name: Install dependencies
        run: pip install pytest allure-pytest
      
      - name: Run tests
        run: pytest --alluredir=allure-results
      
      - name: Generate Allure Report
        uses: simple-elf/allure-report-action@master
        if: always()
        with:
          allure_results: allure-results
      
      - name: Upload Report
        uses: actions/upload-pages-artifact@v2
        with:
          path: allure-report
```

**Jenkins:**
```groovy
pipeline {
    agent any
    stages {
        stage('Test') {
            steps {
                sh 'pytest --alluredir=allure-results'
            }
        }
    }
    post {
        always {
            allure([
                results: [[path: 'allure-results']]
            ])
        }
    }
}
```

### Report Customization

**Environment info (`allure-results/environment.properties`):**
```properties
Browser=Chrome 120
OS=Windows 11
Environment=Staging
App.Version=2.1.0
```

**Categories (`allure-results/categories.json`):**
```json
[
  {
    "name": "Product Defects",
    "matchedStatuses": ["failed"],
    "messageRegex": ".*AssertionError.*"
  },
  {
    "name": "Test Infrastructure",
    "matchedStatuses": ["broken"],
    "messageRegex": ".*ConnectionError.*"
  }
]
```

### History and Trends

Preserve history across runs:

```bash
# Before running tests, copy previous history
cp -r allure-report/history allure-results/

# Run tests
pytest --alluredir=allure-results

# Generate with history
allure generate allure-results -o allure-report --clean
```

## Code Example

### Complete CI/CD Script

```bash
#!/bin/bash
# run-tests-with-allure.sh

# Clean previous results
rm -rf allure-results allure-report

# Preserve history if exists
if [ -d "allure-report/history" ]; then
    mkdir -p allure-results
    cp -r allure-report/history allure-results/
fi

# Run tests
pytest --alluredir=allure-results tests/

# Generate report
allure generate allure-results -o allure-report --clean

# Optionally serve
# allure open allure-report

echo "Report generated at allure-report/index.html"
```

## Summary

- **`allure serve`**: Quick local review, auto-opens browser
- **`allure generate`**: Static HTML for sharing/archiving
- **Results → Report**: JSON files transformed to interactive HTML
- **CI/CD integration**: Jenkins plugin, GitHub Actions
- **Customization**: Environment info, categories, history
- **History preservation**: Copy `history/` folder between runs for trends

## Additional Resources

- [Allure CLI Documentation](https://docs.qameta.io/allure/#_commandline) - Command reference
- [GitHub Actions Integration](https://github.com/simple-elf/allure-report-action) - GitHub Action
- [Jenkins Allure Plugin](https://plugins.jenkins.io/allure-jenkins-plugin/) - Jenkins integration

