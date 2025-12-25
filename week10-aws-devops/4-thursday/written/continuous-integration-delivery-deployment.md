# Continuous Integration, Delivery, and Deployment

## Learning Objectives

- Define Continuous Integration (CI) and its core principles
- Distinguish between Continuous Delivery and Continuous Deployment
- Describe typical CI/CD pipeline stages and their purposes
- Explain the role of automated testing in CI/CD pipelines
- Compare deployment strategies: blue-green, canary, and rolling
- Recognize how CI/CD transforms software delivery

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

CI/CD is the automation backbone of DevOps. It transforms manual, error-prone deployments into reliable, repeatable processes. When developers commit code, automated pipelines build, test, and deploy it—sometimes dozens of times per day. This week's finale on Friday covers Jenkins, the industry-standard CI/CD tool that brings these concepts to life.

As a quality engineer, CI/CD directly involves you. Your automated tests run in pipelines. You validate that builds pass quality gates. You ensure deployments meet acceptance criteria. Understanding CI/CD helps you design test strategies that work within pipeline constraints and timelines.

## The Concept

### The Three Continuous Practices

```
┌─────────────────────────────────────────────────────────────────────┐
│              CI/CD: The Three Continuous Practices                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   CONTINUOUS INTEGRATION (CI)                                       │
│   ───────────────────────────                                       │
│   Developers integrate code frequently into shared repository       │
│   Each integration is verified by automated build and tests         │
│                                                                      │
│   ┌────────┐ ┌────────┐ ┌────────┐                                 │
│   │ Dev 1  │ │ Dev 2  │ │ Dev 3  │                                 │
│   └───┬────┘ └───┬────┘ └───┬────┘                                 │
│       │          │          │                                       │
│       └──────────┼──────────┘                                       │
│                  │                                                   │
│                  ▼                                                   │
│          ┌─────────────┐                                            │
│          │ Main Branch │                                            │
│          └──────┬──────┘                                            │
│                 │                                                    │
│                 ▼                                                    │
│          ┌─────────────┐                                            │
│          │   BUILD +   │ ◀── Every commit triggers                 │
│          │    TEST     │                                            │
│          └─────────────┘                                            │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   CONTINUOUS DELIVERY (CD)                                          │
│   ────────────────────────                                          │
│   Code is always in a deployable state                              │
│   One-click deployment to production (manual trigger)               │
│                                                                      │
│          CI Pipeline                      │  Manual   │ Production │
│   ┌─────────────────────┐                │  Trigger  │            │
│   │ Build → Test → Stage│ ──────────────▶│   🔘      │──▶ PROD   │
│   └─────────────────────┘                │           │            │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   CONTINUOUS DEPLOYMENT (CD)                                        │
│   ──────────────────────────                                        │
│   Every change that passes tests deploys automatically              │
│   No manual intervention required                                   │
│                                                                      │
│          CI Pipeline                     Automatic    Production   │
│   ┌─────────────────────┐                                          │
│   │ Build → Test → Stage│ ──────────────────────────▶ PROD        │
│   └─────────────────────┘   (if all tests pass)                    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Continuous Integration Principles

```
┌─────────────────────────────────────────────────────────────────────┐
│                  CI Core Principles                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   1. MAINTAIN A SINGLE SOURCE REPOSITORY                            │
│      All code in version control (Git)                              │
│      Main branch represents latest stable code                      │
│                                                                      │
│   2. AUTOMATE THE BUILD                                             │
│      One command builds everything                                  │
│      No manual steps required                                       │
│                                                                      │
│   3. MAKE THE BUILD SELF-TESTING                                    │
│      Automated tests run with every build                           │
│      Build fails if tests fail                                      │
│                                                                      │
│   4. EVERYONE COMMITS TO MAINLINE EVERY DAY                         │
│      Frequent integration (at least daily)                          │
│      Small changes easier to debug                                  │
│                                                                      │
│   5. EVERY COMMIT TRIGGERS A BUILD                                  │
│      Immediate feedback on changes                                  │
│      Problems found quickly                                         │
│                                                                      │
│   6. KEEP THE BUILD FAST                                            │
│      Target: < 10 minutes                                           │
│      Fast feedback enables rapid iteration                          │
│                                                                      │
│   7. FIX BROKEN BUILDS IMMEDIATELY                                  │
│      Broken build = top priority                                    │
│      Don't commit on broken build                                   │
│                                                                      │
│   8. EVERYONE CAN SEE BUILD RESULTS                                 │
│      Visible dashboards                                             │
│      Transparent status                                             │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### CI/CD Pipeline Stages

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Typical CI/CD Pipeline Stages                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌───────────┐   ┌───────────┐   ┌───────────┐   ┌───────────┐    │
│   │   CODE    │──▶│   BUILD   │──▶│   TEST    │──▶│  PACKAGE  │    │
│   └───────────┘   └───────────┘   └───────────┘   └───────────┘    │
│        │               │               │               │            │
│        │               │               │               │            │
│   ┌────▼────┐    ┌─────▼─────┐   ┌─────▼─────┐  ┌─────▼─────┐     │
│   │Checkout │    │ Compile   │   │Unit Tests │  │Docker     │     │
│   │from Git │    │Dependencies│   │Integration│  │Image      │     │
│   │         │    │Lint/Format │   │Security   │  │Artifact   │     │
│   └─────────┘    └───────────┘   │Code Quality│  │Store      │     │
│                                   └───────────┘  └───────────┘     │
│                                                                      │
│   ┌───────────┐   ┌───────────┐   ┌───────────┐   ┌───────────┐    │
│   │  DEPLOY   │──▶│  VERIFY   │──▶│  RELEASE  │──▶│  MONITOR  │    │
│   │  STAGING  │   │           │   │ (PROD)    │   │           │    │
│   └───────────┘   └───────────┘   └───────────┘   └───────────┘    │
│        │               │               │               │            │
│        │               │               │               │            │
│   ┌────▼────┐    ┌─────▼─────┐   ┌─────▼─────┐  ┌─────▼─────┐     │
│   │Deploy to│    │Smoke Tests│   │Blue-Green │  │Metrics    │     │
│   │Test Env │    │E2E Tests  │   │Canary     │  │Alerts     │     │
│   │         │    │Performance│   │Rolling    │  │Logs       │     │
│   └─────────┘    └───────────┘   └───────────┘  └───────────┘     │
│                                                                      │
│   If any stage fails → Pipeline stops → Team notified              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Testing in CI/CD Pipelines

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Testing Pyramid in CI/CD                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│                          ┌───────┐                                  │
│                         ╱   E2E   ╲       Few, Slow, Expensive      │
│                        ╱───────────╲      Run in staging            │
│                       ╱  UI Tests   ╲                               │
│                      ╱───────────────╲                              │
│                     ╱  Integration    ╲   More tests                │
│                    ╱───────────────────╲  Run after unit tests      │
│                   ╱     API Tests       ╲                           │
│                  ╱───────────────────────╲                          │
│                 ╱        Unit Tests        ╲ Many, Fast, Cheap      │
│                ╱─────────────────────────────╲ Run first            │
│               ╱______________________________╲                      │
│                                                                      │
│   Pipeline Test Strategy:                                           │
│   ─────────────────────────                                         │
│                                                                      │
│   BUILD STAGE:                                                       │
│   • Unit tests (fast, comprehensive)                                │
│   • Static code analysis                                            │
│   • Security vulnerability scan                                     │
│   Target: < 5 minutes                                               │
│                                                                      │
│   TEST STAGE:                                                        │
│   • Integration tests                                               │
│   • API tests                                                        │
│   • Contract tests                                                   │
│   Target: < 15 minutes                                              │
│                                                                      │
│   STAGING STAGE:                                                     │
│   • End-to-end tests                                                │
│   • Performance tests (basic)                                       │
│   • Smoke tests                                                      │
│   Target: < 30 minutes                                              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Deployment Strategies

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Deployment Strategies                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   BLUE-GREEN DEPLOYMENT                                             │
│   ─────────────────────                                             │
│   Two identical production environments                             │
│                                                                      │
│   Before:                        After:                             │
│   ┌──────────┐ ◀─Traffic        ┌──────────┐                       │
│   │  BLUE    │                  │  BLUE    │ (idle)                 │
│   │  v1.0    │                  │  v1.0    │                        │
│   └──────────┘                  └──────────┘                        │
│   ┌──────────┐                  ┌──────────┐ ◀─Traffic              │
│   │  GREEN   │ (idle)           │  GREEN   │                        │
│   │          │ deploy v2.0──▶   │  v2.0    │                        │
│   └──────────┘                  └──────────┘                        │
│                                                                      │
│   Pros: Instant rollback, zero downtime                            │
│   Cons: Requires double infrastructure                             │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   CANARY DEPLOYMENT                                                  │
│   ─────────────────                                                  │
│   Gradually shift traffic to new version                            │
│                                                                      │
│   Phase 1:     Phase 2:     Phase 3:     Phase 4:                  │
│   ┌──────┐     ┌──────┐     ┌──────┐     ┌──────┐                  │
│   │v1.0  │100% │v1.0  │90%  │v1.0  │50%  │v2.0  │100%              │
│   │      │     │      │     │      │     │      │                  │
│   │v2.0  │0%   │v2.0  │10%  │v2.0  │50%  │      │                  │
│   └──────┘     └──────┘     └──────┘     └──────┘                  │
│                                                                      │
│   Pros: Gradual rollout, real-world testing                        │
│   Cons: Complex routing, longer rollout                            │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   ROLLING DEPLOYMENT                                                 │
│   ──────────────────                                                 │
│   Update instances one-by-one                                       │
│                                                                      │
│   Start:       Step 1:      Step 2:      Done:                     │
│   [v1][v1][v1] [v2][v1][v1] [v2][v2][v1] [v2][v2][v2]              │
│                                                                      │
│   Pros: No extra infrastructure, gradual                           │
│   Cons: Mixed versions during rollout, slow rollback               │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Continuous Delivery vs Continuous Deployment

| Aspect | Continuous Delivery | Continuous Deployment |
|--------|--------------------|-----------------------|
| **Production deployment** | Manual trigger | Automatic |
| **Human approval** | Required | Not required |
| **Risk tolerance** | Lower | Higher |
| **Deployment frequency** | On-demand | Every passing build |
| **Best for** | Most organizations | High-maturity teams |
| **Test requirements** | Comprehensive | Extremely comprehensive |

## Code Examples

### Conceptual Pipeline Definition

```yaml
# Pipeline concept (implemented in Jenkins/GitHub Actions)
pipeline:
  name: my-application-pipeline
  
  triggers:
    - push to main branch
    - pull request to main
  
  stages:
    - stage: build
      steps:
        - checkout: self
        - run: npm install
        - run: npm run lint
        - run: npm run build
        - publish: build artifacts
      
    - stage: test
      depends_on: build
      steps:
        - run: npm run test:unit
        - run: npm run test:integration
        - publish: test reports
        - publish: coverage reports
      
    - stage: security-scan
      depends_on: build
      steps:
        - run: npm audit
        - run: docker scan $IMAGE
        - run: sonar-scanner
      
    - stage: deploy-staging
      depends_on: [test, security-scan]
      environment: staging
      steps:
        - download: build artifacts
        - run: deploy to staging
        - run: smoke tests
      
    - stage: deploy-production
      depends_on: deploy-staging
      environment: production
      approval: manual  # Continuous Delivery
      # approval: auto  # Continuous Deployment
      steps:
        - run: deploy to production
        - run: health check
```

### Quality Gates

```yaml
# Quality gates that must pass before deployment
quality_gates:
  
  code_coverage:
    minimum: 80%
    action_on_failure: fail_build
  
  unit_tests:
    minimum_pass_rate: 100%
    action_on_failure: fail_build
  
  security_vulnerabilities:
    critical: 0
    high: 0
    medium: allowed  # logged but not blocking
    action_on_failure: fail_build
  
  code_smells:
    maximum_new: 5
    action_on_failure: warn
  
  performance:
    response_time_p95: 200ms
    error_rate: 1%
    action_on_failure: fail_build

# Pipeline stage example
- stage: quality-gate
  steps:
    - name: Check code coverage
      run: |
        COVERAGE=$(cat coverage/coverage-summary.json | jq '.total.lines.pct')
        if [ "$COVERAGE" -lt 80 ]; then
          echo "Coverage $COVERAGE% is below 80% threshold"
          exit 1
        fi
    
    - name: Check for security vulnerabilities
      run: |
        CRITICAL=$(cat security-report.json | jq '.vulnerabilities.critical')
        if [ "$CRITICAL" -gt 0 ]; then
          echo "$CRITICAL critical vulnerabilities found"
          exit 1
        fi
```

### Deployment Strategy Configuration

```yaml
# Blue-Green Deployment
blue_green:
  environments:
    blue:
      url: blue.example.com
      currently_active: true
    green:
      url: green.example.com
      currently_active: false
  
  steps:
    - deploy to inactive environment (green)
    - run smoke tests on green
    - switch load balancer to green
    - monitor for 10 minutes
    - if errors: switch back to blue
    - mark blue as inactive

# Canary Deployment
canary:
  stages:
    - percentage: 5
      duration: 5m
      success_criteria:
        error_rate: < 1%
        latency_p99: < 500ms
    
    - percentage: 25
      duration: 10m
      success_criteria:
        error_rate: < 1%
        latency_p99: < 500ms
    
    - percentage: 50
      duration: 15m
      success_criteria:
        error_rate: < 1%
        latency_p99: < 500ms
    
    - percentage: 100
      # Full rollout
  
  rollback:
    automatic: true
    on: success_criteria_not_met
```

### CI/CD Metrics Dashboard

```yaml
# Key metrics to track for CI/CD effectiveness
metrics:
  deployment_frequency:
    description: How often deployments occur
    target: multiple per day
    current: 3 per day
  
  lead_time:
    description: Time from commit to production
    target: < 1 hour
    current: 45 minutes
  
  change_failure_rate:
    description: Percentage of failed deployments
    target: < 15%
    current: 8%
  
  mean_time_to_recovery:
    description: Time to recover from failures
    target: < 1 hour
    current: 25 minutes
  
  build_duration:
    description: Time to complete CI pipeline
    target: < 10 minutes
    current: 7 minutes
  
  test_pass_rate:
    description: Percentage of passing tests
    target: > 99%
    current: 99.5%
```

## Summary

- **Continuous Integration**: Frequently integrate code, automated builds and tests on every commit
- **Continuous Delivery**: Code always deployable, manual trigger to production
- **Continuous Deployment**: Every passing build automatically deploys to production
- **Pipeline stages**: Code → Build → Test → Package → Deploy Staging → Verify → Release → Monitor
- **Testing strategy**: Fast unit tests first, integration tests next, E2E tests in staging
- **Deployment strategies**: Blue-green (instant switch), Canary (gradual %), Rolling (one-by-one)
- **Quality gates**: Enforce standards (coverage, security, performance) before deployment

## Additional Resources

- [Continuous Delivery (Martin Fowler)](https://martinfowler.com/bliki/ContinuousDelivery.html) - Foundational article
- [Continuous Deployment (Martin Fowler)](https://martinfowler.com/bliki/ContinuousDeployment.html) - Distinction explained
- [Jenkins Documentation](https://www.jenkins.io/doc/) - CI/CD tool covered Friday

