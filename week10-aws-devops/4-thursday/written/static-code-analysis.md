# Static Code Analysis

## Learning Objectives

- Define static code analysis and explain its role in software quality
- Identify benefits of detecting defects early in the development lifecycle
- Describe common static analysis tools for different languages (SonarQube, ESLint, Pylint, Checkstyle)
- Understand how to integrate static analysis into CI/CD pipelines
- Interpret code quality metrics and technical debt indicators
- Apply static analysis findings to improve code quality

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Static code analysis examines source code without executing it, identifying bugs, security vulnerabilities, and code quality issues before they reach production. In CI/CD pipelines, static analysis acts as an automated code reviewer, catching problems that might slip past human review.

As a quality engineer, static analysis is a powerful ally. It finds issues that are difficult to catch with traditional testing: security vulnerabilities, code smells, and maintainability problems. Integrating static analysis into pipelines ensures every commit meets quality standards before deployment.

## The Concept

### What is Static Code Analysis?

**Static Code Analysis** examines source code without running it to find potential issues. It's like having an automated code reviewer that checks every line against established rules and patterns.

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Static vs Dynamic Analysis                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   STATIC ANALYSIS                    DYNAMIC ANALYSIS               │
│   ───────────────                    ────────────────               │
│                                                                      │
│   Analyzes source code               Analyzes running code          │
│   Without execution                  During execution               │
│                                                                      │
│   ┌─────────────────┐               ┌─────────────────┐             │
│   │   Source Code   │               │ Running Program │             │
│   │   ┌─────────┐   │               │   ┌─────────┐   │             │
│   │   │ Scanner │   │               │   │  Tests  │   │             │
│   │   └────┬────┘   │               │   └────┬────┘   │             │
│   │        │        │               │        │        │             │
│   │        ▼        │               │        ▼        │             │
│   │   Issues Found  │               │ Runtime Errors  │             │
│   │   • Bugs        │               │ • Actual bugs   │             │
│   │   • Vulnerabilities │           │ • Performance   │             │
│   │   • Code smells │               │ • Memory leaks  │             │
│   └─────────────────┘               └─────────────────┘             │
│                                                                      │
│   When: Before runtime              When: During runtime            │
│   Speed: Fast                       Speed: Slower                   │
│   Coverage: All code paths          Coverage: Executed paths only   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Benefits of Early Detection

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Cost of Defects by Phase                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Cost to Fix                                                        │
│      │                                             ┌────────────┐   │
│   $$$│                                             │ Production │   │
│      │                                        ╱────┤   $10,000  │   │
│      │                               ╱───────╱     └────────────┘   │
│      │                              │                               │
│      │                      ╱───────╯                               │
│   $$ │              ╱──────╱    ┌────────────┐                      │
│      │             │            │   Testing  │                      │
│      │     ╱──────╱             │   $1,000   │                      │
│      │    │                     └────────────┘                      │
│    $ │────╯                                                         │
│      │    ┌────────────┐  ┌────────────┐                           │
│      │    │   Coding   │  │   Build    │                           │
│      │    │    $100    │  │   $500     │                           │
│      └────┴────────────┴──┴────────────┴──────────────────────────  │
│           Development        Testing         Production             │
│                                                                      │
│   Static analysis catches issues at coding/build phase              │
│   Before expensive testing and production fixes                     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### What Static Analysis Detects

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Types of Issues Detected                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   BUGS                                                               │
│   ────                                                               │
│   • Null pointer dereferences                                       │
│   • Array index out of bounds                                       │
│   • Resource leaks (unclosed files, connections)                    │
│   • Dead code (unreachable)                                         │
│   • Infinite loops                                                   │
│                                                                      │
│   SECURITY VULNERABILITIES                                          │
│   ─────────────────────────                                         │
│   • SQL injection                                                    │
│   • Cross-site scripting (XSS)                                      │
│   • Hardcoded credentials                                           │
│   • Insecure cryptography                                           │
│   • Path traversal                                                   │
│                                                                      │
│   CODE SMELLS                                                        │
│   ───────────                                                        │
│   • Duplicated code                                                  │
│   • Long methods                                                     │
│   • Complex conditionals                                            │
│   • Unused variables                                                 │
│   • Magic numbers                                                    │
│                                                                      │
│   STYLE VIOLATIONS                                                   │
│   ────────────────                                                   │
│   • Inconsistent naming                                             │
│   • Formatting issues                                                │
│   • Missing documentation                                           │
│   • Import organization                                             │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Common Static Analysis Tools

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Static Analysis Tools by Language                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   MULTI-LANGUAGE PLATFORMS                                          │
│   ────────────────────────                                          │
│   SonarQube       Enterprise platform, 25+ languages               │
│   CodeClimate     Quality metrics, many integrations               │
│   Codacy          Automated code review                            │
│                                                                      │
│   JAVA                                                               │
│   ────                                                               │
│   Checkstyle      Style and formatting                              │
│   PMD             Bug detection                                     │
│   SpotBugs        Successor to FindBugs                            │
│   Error Prone     Google's compile-time checker                    │
│                                                                      │
│   JAVASCRIPT/TYPESCRIPT                                              │
│   ──────────────────────                                            │
│   ESLint          Linting and style                                 │
│   JSHint          Error detection                                   │
│   TypeScript      Type checking (built-in)                          │
│   Prettier        Code formatting                                   │
│                                                                      │
│   PYTHON                                                             │
│   ──────                                                             │
│   Pylint          Comprehensive linting                             │
│   Flake8          Style guide enforcement                           │
│   mypy            Static type checking                              │
│   Bandit          Security analysis                                 │
│   Black           Code formatting                                   │
│                                                                      │
│   SECURITY-FOCUSED                                                   │
│   ────────────────                                                   │
│   Snyk            Vulnerability detection                           │
│   Trivy           Container scanning                                │
│   Semgrep         Pattern-based analysis                            │
│   OWASP tools     Security standards                                │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### SonarQube Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                     SonarQube Architecture                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                     CI/CD Pipeline                           │   │
│   │                                                              │   │
│   │   ┌─────────┐    ┌─────────────┐    ┌────────────────┐     │   │
│   │   │  Code   │───▶│  SonarQube  │───▶│  Build        │     │   │
│   │   │ Checkout│    │  Scanner    │    │  Continues/   │     │   │
│   │   │         │    │             │    │  Fails        │     │   │
│   │   └─────────┘    └──────┬──────┘    └────────────────┘     │   │
│   │                         │                                    │   │
│   └─────────────────────────┼────────────────────────────────────┘   │
│                             │                                        │
│                             ▼                                        │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                  SonarQube Server                            │   │
│   │                                                              │   │
│   │   ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐    │   │
│   │   │  Database   │  │   Rules     │  │   Dashboard     │    │   │
│   │   │  (History)  │  │   Engine    │  │   (Web UI)      │    │   │
│   │   └─────────────┘  └─────────────┘  └─────────────────┘    │   │
│   │                                                              │   │
│   │   Metrics Tracked:                                          │   │
│   │   • Bugs, Vulnerabilities, Code Smells                     │   │
│   │   • Code Coverage                                           │   │
│   │   • Duplications                                            │   │
│   │   • Technical Debt                                          │   │
│   │   • Maintainability Rating (A-E)                           │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Code Quality Metrics

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Code Quality Metrics                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   RELIABILITY                                                        │
│   ───────────                                                        │
│   Bugs found in code                                                │
│   Rating: A (0 bugs) to E (critical bugs)                          │
│                                                                      │
│   SECURITY                                                           │
│   ────────                                                           │
│   Vulnerabilities found                                             │
│   Rating: A (0 vulns) to E (critical vulns)                        │
│                                                                      │
│   MAINTAINABILITY                                                    │
│   ───────────────                                                    │
│   Code smells and technical debt                                    │
│   Rating: A (<5% debt ratio) to E (>50% debt ratio)                │
│                                                                      │
│   COVERAGE                                                           │
│   ────────                                                           │
│   Percentage of code covered by tests                               │
│   Target: 80% or higher                                             │
│                                                                      │
│   DUPLICATIONS                                                       │
│   ────────────                                                       │
│   Percentage of duplicated code                                     │
│   Target: < 3%                                                       │
│                                                                      │
│   TECHNICAL DEBT                                                     │
│   ──────────────                                                     │
│   Estimated time to fix all issues                                  │
│   Measured in: hours, days                                          │
│   Debt Ratio: debt time / development time                         │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Integration in CI/CD Pipeline

```
┌─────────────────────────────────────────────────────────────────────┐
│              Static Analysis in CI/CD Pipeline                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐            │
│   │ Build  │──▶│  Lint   │──▶│ Security│──▶│ Quality │──▶ ...     │
│   └────────┘   │  Check  │   │  Scan   │   │  Gate   │            │
│                └────┬────┘   └────┬────┘   └────┬────┘            │
│                     │             │             │                  │
│                     ▼             ▼             ▼                  │
│                ┌─────────┐   ┌─────────┐   ┌─────────┐            │
│                │ ESLint  │   │  Snyk   │   │SonarQube│            │
│                │ Pylint  │   │ Trivy   │   │CodeClimate│          │
│                │Checkstyle│   │ Bandit  │   │         │            │
│                └─────────┘   └─────────┘   └─────────┘            │
│                                                                      │
│   Quality Gate Example:                                             │
│   ─────────────────────                                             │
│   PASS if:                                                          │
│   • No new critical bugs                                            │
│   • No new security vulnerabilities                                │
│   • Code coverage >= 80% on new code                               │
│   • Duplications < 3%                                               │
│                                                                      │
│   FAIL if:                                                          │
│   • Any critical/blocker issue introduced                          │
│   • Coverage drops below threshold                                  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### ESLint Configuration

```javascript
// .eslintrc.js
module.exports = {
  env: {
    browser: true,
    es2021: true,
    node: true,
    jest: true
  },
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:security/recommended'
  ],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module'
  },
  plugins: ['@typescript-eslint', 'security'],
  rules: {
    // Error prevention
    'no-unused-vars': 'error',
    'no-console': 'warn',
    'no-debugger': 'error',
    
    // Best practices
    'eqeqeq': 'error',
    'no-eval': 'error',
    'no-implied-eval': 'error',
    
    // Security
    'security/detect-object-injection': 'warn',
    'security/detect-non-literal-regexp': 'warn',
    
    // Style
    'indent': ['error', 2],
    'quotes': ['error', 'single'],
    'semi': ['error', 'always']
  }
};
```

### Pylint Configuration

```ini
# .pylintrc
[MASTER]
jobs=4
load-plugins=pylint_django

[MESSAGES CONTROL]
disable=
    missing-module-docstring,
    too-few-public-methods

[FORMAT]
max-line-length=100
indent-string='    '

[DESIGN]
max-args=7
max-locals=15
max-statements=50
max-parents=7

[SIMILARITIES]
min-similarity-lines=4

[BASIC]
good-names=i,j,k,ex,_,id,pk
```

### SonarQube Scanner Configuration

```properties
# sonar-project.properties
sonar.projectKey=my-project
sonar.projectName=My Project
sonar.projectVersion=1.0

sonar.sources=src
sonar.tests=tests
sonar.exclusions=**/node_modules/**,**/vendor/**

sonar.javascript.lcov.reportPaths=coverage/lcov.info
sonar.python.coverage.reportPaths=coverage.xml

sonar.qualitygate.wait=true
```

### CI Pipeline with Static Analysis

```yaml
# GitHub Actions example
name: CI with Static Analysis

on: [push, pull_request]

jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '18'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Run ESLint
        run: npm run lint -- --format json --output-file eslint-report.json
        continue-on-error: true
      
      - name: Upload ESLint report
        uses: actions/upload-artifact@v4
        with:
          name: eslint-report
          path: eslint-report.json
  
  security-scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Run Snyk security scan
        uses: snyk/actions/node@master
        env:
          SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
        with:
          args: --severity-threshold=high
  
  sonarqube:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0  # Full history for blame
      
      - name: SonarQube Scan
        uses: SonarSource/sonarqube-scan-action@master
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
      
      - name: Quality Gate Check
        uses: SonarSource/sonarqube-quality-gate-action@master
        timeout-minutes: 5
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

### Python Static Analysis

```bash
# Run multiple Python analyzers

# Pylint - comprehensive linting
pylint src/ --output-format=json > pylint-report.json

# Flake8 - style guide enforcement
flake8 src/ --format=json --output-file=flake8-report.json

# mypy - type checking
mypy src/ --json-report mypy-report

# Bandit - security analysis
bandit -r src/ -f json -o bandit-report.json

# Black - formatting check (dry run)
black src/ --check --diff

# isort - import sorting check
isort src/ --check-only --diff
```

### Quality Gate Script

```bash
#!/bin/bash
# quality-gate.sh - Check if code meets quality standards

set -e

echo "Running Quality Gate..."

# Run linting
echo "1. Linting..."
npm run lint
LINT_EXIT=$?

# Run tests with coverage
echo "2. Running tests..."
npm run test:coverage
TEST_EXIT=$?

# Check coverage threshold
echo "3. Checking coverage..."
COVERAGE=$(cat coverage/coverage-summary.json | jq '.total.lines.pct')
MIN_COVERAGE=80

if (( $(echo "$COVERAGE < $MIN_COVERAGE" | bc -l) )); then
  echo "Coverage $COVERAGE% is below threshold $MIN_COVERAGE%"
  exit 1
fi

# Run security scan
echo "4. Security scan..."
npm audit --audit-level=high
SECURITY_EXIT=$?

# Check for critical issues
echo "5. Checking for critical issues..."
CRITICAL_ISSUES=$(cat sonar-report.json | jq '.issues | map(select(.severity == "CRITICAL")) | length')
if [ "$CRITICAL_ISSUES" -gt 0 ]; then
  echo "Found $CRITICAL_ISSUES critical issues"
  exit 1
fi

echo "Quality Gate PASSED"
exit 0
```

## Summary

- **Static code analysis** examines source code without executing it to find bugs, vulnerabilities, and code smells
- **Early detection** saves money: fixing bugs in development costs 100x less than in production
- **Common tools**: SonarQube (multi-language), ESLint (JavaScript), Pylint (Python), Checkstyle (Java)
- **Quality metrics**: Bugs, vulnerabilities, code smells, coverage, duplications, technical debt
- **CI/CD integration**: Run static analysis on every commit, fail builds that don't meet quality gates
- **Quality gates**: Define thresholds (e.g., 80% coverage, 0 critical bugs) that must pass

## Additional Resources

- [SonarQube Documentation](https://docs.sonarqube.org/) - Enterprise code quality platform
- [ESLint Rules](https://eslint.org/docs/rules/) - JavaScript linting rules reference
- [OWASP Static Analysis Tools](https://owasp.org/www-community/Source_Code_Analysis_Tools) - Security-focused tools

