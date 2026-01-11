# AI Testing Integration

## Learning Objectives

- Integrate AI testing into existing CI/CD pipelines
- Implement automated fairness checks as part of the build process
- Set up continuous bias monitoring for production models
- Design model performance regression testing strategies
- Automate testing workflows for AI systems
- Configure alerting for model drift and quality degradation
- Streamline documentation and reporting automation

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

Throughout this week, you've learned powerful AI testing techniques—fairness metrics, robustness testing, bias detection, red teaming. But knowledge isn't value until it's applied consistently.

**The challenge:** AI models get updated, retrained, and redeployed regularly. Manual testing can't keep pace. Without automation:
- Biased models ship to production
- Regression goes unnoticed
- Data drift causes silent failures
- Documentation becomes stale

**The solution:** Integrate AI testing into your CI/CD pipeline, where tests run automatically on every model change. Building on Week 10's DevOps practices, this module shows you how to make AI testing sustainable and systematic.

## The Concept

### Traditional CI/CD vs. AI CI/CD

Traditional CI/CD focuses on code. AI CI/CD must also handle data and models:

| Traditional CI/CD | AI-Extended CI/CD |
|-------------------|-------------------|
| Code unit tests | Code unit tests + Model unit tests |
| Integration tests | Integration tests + Model integration tests |
| Security scans | Security scans + Fairness checks |
| Performance tests | Performance tests + Inference latency tests |
| - | Data validation |
| - | Model accuracy/regression |
| - | Bias monitoring |
| - | Drift detection |


```
Extended CI/CD Pipeline for AI:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  CODE CHANGES          DATA CHANGES         MODEL CHANGES               │
│       │                     │                    │                      │
│       └─────────────────────┼────────────────────┘                      │
│                             │                                           │
│                             ▼                                           │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    CONTINUOUS INTEGRATION                        │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │   │
│  │  │ Code Tests  │  │ Data Tests  │  │ Model Tests │              │   │
│  │  │ • Unit      │  │ • Schema    │  │ • Accuracy  │              │   │
│  │  │ • Lint      │  │ • Quality   │  │ • Fairness  │              │   │
│  │  │ • Security  │  │ • Drift     │  │ • Robustness│              │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘              │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                             │                                           │
│                             ▼                                           │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                   CONTINUOUS DEPLOYMENT                          │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │   │
│  │  │ Shadow Mode │──▶│ Canary     │──▶│ Full Deploy │              │   │
│  │  │ Testing     │  │ Release    │  │             │              │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘              │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                             │                                           │
│                             ▼                                           │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                  CONTINUOUS MONITORING                           │   │
│  │  • Performance metrics    • Fairness tracking                    │   │
│  │  • Data drift detection   • Anomaly alerting                     │   │
│  │  • Error analysis         • Usage patterns                       │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Pipeline Architecture

A typical AI testing pipeline:

```
Code/Model/Data Change
        ↓
┌─────────────────────────┐
│   DATA VALIDATION       │
│   - Schema checks       │
│   - Quality checks      │
│   - Drift detection     │
└───────────┬─────────────┘
            ↓
┌─────────────────────────┐
│   MODEL TESTING         │
│   - Accuracy tests      │
│   - Fairness checks     │
│   - Robustness tests    │
└───────────┬─────────────┘
            ↓
┌─────────────────────────┐
│   QUALITY GATES         │
│   - Accuracy threshold  │
│   - Fairness threshold  │
│   - Performance SLAs    │
└───────────┬─────────────┘
            ↓
     Pass?
     ↓ Yes          ↓ No
┌─────────────┐  ┌─────────────┐
│   DEPLOY    │  │  ALERT &    │
│   Staging → │  │  BLOCK      │
│   Production│  └─────────────┘
└─────────────┘
```

### Automated Fairness Checks

Build fairness checking into your pipeline:

```python
"""
CI/CD Fairness Check Script
Run as: python fairness_check.py --model model.pkl --data test.csv
"""

import argparse
import json
import sys

def load_model_and_data(model_path, data_path):
    """Load model and test data."""
    # Implementation depends on your model format
    import pickle
    import pandas as pd
    
    with open(model_path, 'rb') as f:
        model = pickle.load(f)
    data = pd.read_csv(data_path)
    return model, data


def check_demographic_parity(predictions, protected, threshold=0.05):
    """
    Check if selection rates are similar across groups.
    
    Returns: (passed, details)
    """
    groups = set(protected)
    rates = {}
    
    for group in groups:
        group_preds = [p for p, g in zip(predictions, protected) if g == group]
        rates[group] = sum(group_preds) / len(group_preds)
    
    difference = max(rates.values()) - min(rates.values())
    passed = difference <= threshold
    
    return passed, {"rates": rates, "difference": difference}


def run_fairness_checks(model, data, protected_column, target_column):
    """
    Run all fairness checks.
    """
    predictions = model.predict(data.drop(columns=[target_column]))
    protected = data[protected_column].tolist()
    
    results = {"checks": [], "passed": True}
    
    # Demographic parity check
    dp_passed, dp_details = check_demographic_parity(predictions, protected)
    results["checks"].append({
        "name": "demographic_parity",
        "passed": dp_passed,
        "details": dp_details
    })
    if not dp_passed:
        results["passed"] = False
    
    return results


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--model", required=True)
    parser.add_argument("--data", required=True)
    parser.add_argument("--protected", default="gender")
    parser.add_argument("--target", default="approved")
    parser.add_argument("--output", default="fairness_results.json")
    args = parser.parse_args()
    
    model, data = load_model_and_data(args.model, args.data)
    results = run_fairness_checks(model, data, args.protected, args.target)
    
    # Save results
    with open(args.output, 'w') as f:
        json.dump(results, f, indent=2)
    
    # Print summary
    print("=" * 50)
    print("FAIRNESS CHECK RESULTS")
    print("=" * 50)
    for check in results["checks"]:
        status = "✓ PASS" if check["passed"] else "✗ FAIL"
        print(f"{status}: {check['name']}")
    print(f"\nOverall: {'PASSED' if results['passed'] else 'FAILED'}")
    
    # Exit with appropriate code for CI/CD
    sys.exit(0 if results["passed"] else 1)


if __name__ == "__main__":
    main()
```

### GitHub Actions Example

```yaml
# .github/workflows/ai-testing.yml
name: AI Model Testing

on:
  push:
    branches: [main]
    paths:
      - 'models/**'
      - 'data/**'
  pull_request:
    branches: [main]

jobs:
  test-model:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.10'
      
      - name: Install dependencies
        run: pip install -r requirements.txt
      
      - name: Run accuracy tests
        run: python tests/test_accuracy.py
      
      - name: Run fairness checks
        run: |
          python scripts/fairness_check.py \
            --model models/latest.pkl \
            --data data/test.csv
      
      - name: Upload results
        uses: actions/upload-artifact@v3
        with:
          name: test-results
          path: results/
```

### Jenkins Pipeline Example

```groovy
// Jenkinsfile
pipeline {
    agent any
    
    stages {
        stage('Data Validation') {
            steps {
                sh 'python scripts/validate_data.py'
            }
        }
        
        stage('Model Tests') {
            parallel {
                stage('Accuracy') {
                    steps {
                        sh 'python tests/test_accuracy.py'
                    }
                }
                stage('Fairness') {
                    steps {
                        sh 'python tests/test_fairness.py'
                    }
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                script {
                    def results = readJSON file: 'results/fairness.json'
                    if (!results.passed) {
                        error 'Fairness requirements not met!'
                    }
                }
            }
        }
        
        stage('Deploy') {
            when { branch 'main' }
            steps {
                sh 'scripts/deploy.sh'
            }
        }
    }
    
    post {
        failure {
            // Send alert on failure
            echo 'Pipeline failed!'
        }
    }
}
```

### Continuous Bias Monitoring

Monitor for bias in production, not just at deployment:

```python
"""
Continuous Bias Monitor
Runs periodically to check production predictions.
"""

class BiasMonitor:
    """Monitor production predictions for bias."""
    
    def __init__(self, thresholds):
        self.thresholds = thresholds
        self.alerts = []
    
    def check_recent_predictions(self, predictions_log):
        """
        Analyze recent predictions for bias.
        
        predictions_log: List of dicts with 'prediction', 'protected_attr' keys
        """
        # Group by protected attribute
        by_group = {}
        for pred in predictions_log:
            group = pred['protected_attr']
            if group not in by_group:
                by_group[group] = []
            by_group[group].append(pred['prediction'])
        
        # Calculate rates
        rates = {}
        for group, preds in by_group.items():
            rates[group] = sum(preds) / len(preds)
        
        # Check threshold
        if len(rates) >= 2:
            difference = max(rates.values()) - min(rates.values())
            if difference > self.thresholds['demographic_parity']:
                self.alerts.append({
                    'type': 'bias_detected',
                    'metric': 'demographic_parity',
                    'value': difference,
                    'threshold': self.thresholds['demographic_parity'],
                    'rates': rates
                })
        
        return self.alerts
```

### Model Performance Regression Testing

Check that new models don't perform worse than current production:

```python
def regression_test(new_model, baseline_metrics, test_data):
    """
    Test new model against baseline performance.
    
    Returns: (passed, details)
    """
    # Evaluate new model
    predictions = new_model.predict(test_data['features'])
    new_accuracy = calculate_accuracy(predictions, test_data['labels'])
    
    # Compare to baseline
    allowed_drop = 0.01  # Allow 1% drop
    
    passed = new_accuracy >= (baseline_metrics['accuracy'] - allowed_drop)
    
    return passed, {
        'baseline_accuracy': baseline_metrics['accuracy'],
        'new_accuracy': new_accuracy,
        'difference': new_accuracy - baseline_metrics['accuracy']
    }
```

### Alerting on Drift and Degradation

Set up alerts when things go wrong:

```python
def check_and_alert(metrics, thresholds, alert_function):
    """
    Check metrics against thresholds and alert if violated.
    """
    alerts = []
    
    if metrics['accuracy'] < thresholds['min_accuracy']:
        alerts.append({
            'severity': 'critical',
            'message': f"Accuracy dropped to {metrics['accuracy']:.2%}"
        })
    
    if metrics['fairness_diff'] > thresholds['max_fairness_diff']:
        alerts.append({
            'severity': 'high',
            'message': f"Fairness violation: {metrics['fairness_diff']:.2%} difference"
        })
    
    if metrics['latency_p99'] > thresholds['max_latency']:
        alerts.append({
            'severity': 'medium',
            'message': f"Latency spike: {metrics['latency_p99']}ms at P99"
        })
    
    # Send alerts
    for alert in alerts:
        alert_function(alert)
    
    return alerts
```

## Code Example

Complete CI/CD integration script:

```python
"""
AI Model CI/CD Test Runner
"""

import json
import sys

def run_all_tests(model_path, data_path):
    """
    Run complete AI testing suite.
    """
    results = {
        "accuracy": run_accuracy_tests(model_path, data_path),
        "fairness": run_fairness_tests(model_path, data_path),
        "regression": run_regression_tests(model_path, data_path)
    }
    
    # Overall pass/fail
    all_passed = all(r["passed"] for r in results.values())
    results["overall_passed"] = all_passed
    
    return results


def generate_ci_report(results):
    """
    Generate CI-friendly output.
    """
    print("=" * 60)
    print("AI MODEL TEST RESULTS")
    print("=" * 60)
    
    for test_name, test_results in results.items():
        if test_name == "overall_passed":
            continue
        status = "✓" if test_results["passed"] else "✗"
        print(f"{status} {test_name}")
    
    print("-" * 60)
    print(f"OVERALL: {'PASSED' if results['overall_passed'] else 'FAILED'}")
    
    return 0 if results['overall_passed'] else 1


if __name__ == "__main__":
    results = run_all_tests("models/latest.pkl", "data/test.csv")
    
    with open("results.json", "w") as f:
        json.dump(results, f, indent=2)
    
    exit_code = generate_ci_report(results)
    sys.exit(exit_code)
```

## Summary

- **Integrate AI testing into CI/CD** to catch issues automatically on every change
- **Pipeline stages:** Data validation → Model tests → Quality gates → Deploy
- **Automated fairness checks** run as part of the build process
- **Continuous monitoring** catches bias and drift in production
- **Regression testing** ensures new models don't perform worse
- **Alerting** notifies teams when metrics degrade
- Build on **Week 10 DevOps** practices for infrastructure

## Additional Resources

- [MLflow](https://mlflow.org/) - ML lifecycle management
- [Great Expectations](https://greatexpectations.io/) - Data validation
- [Evidently AI](https://www.evidentlyai.com/) - ML monitoring
- [GitHub Actions for ML](https://github.com/features/actions) - CI/CD for ML projects
