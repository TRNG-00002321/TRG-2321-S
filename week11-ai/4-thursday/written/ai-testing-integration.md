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

Throughout this week, you've learned powerful techniques for testing AI systems—fairness metrics, robustness testing, bias detection, and more. But knowledge without implementation is just theory. In the real world, AI models are deployed, updated, retrained, and evolved continuously. Manual testing can't keep pace.

This module connects everything you've learned to the CI/CD practices you mastered in Week 10 (AWS, DevOps). You'll learn to automate AI-specific tests, integrate them into deployment pipelines, and establish continuous monitoring that catches problems before they reach users. This is where AI testing becomes sustainable, scalable, and systematic.

## The Concept

### AI Testing in the CI/CD Pipeline

Traditional CI/CD pipelines focus on code quality. AI pipelines must also validate data quality, model quality, and fairness.

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

### Automated Fairness Checks

Integrate fairness testing as a required step in your pipeline:

```python
"""
CI/CD Fairness Check Module
"""

import json
import sys
from typing import Dict, List
from dataclasses import dataclass

@dataclass
class FairnessCheckResult:
    """Result of a fairness check."""
    passed: bool
    metric_name: str
    actual_value: float
    threshold: float
    details: Dict

class CIFairnessChecker:
    """
    Fairness checker for CI/CD integration.
    """
    
    def __init__(self, config_path: str = "fairness_config.json"):
        self.config = self._load_config(config_path)
        self.results = []
        
    def _load_config(self, path: str) -> Dict:
        """Load fairness configuration."""
        default_config = {
            'thresholds': {
                'demographic_parity_difference': 0.05,
                'equalized_odds_difference': 0.05,
                'disparate_impact_ratio': 0.8,
                'accuracy_difference': 0.03
            },
            'protected_attributes': ['gender', 'race', 'age_group'],
            'required_checks': [
                'demographic_parity',
                'equalized_odds',
                'disparate_impact'
            ],
            'blocking': True  # Fail pipeline on fairness violations
        }
        
        try:
            with open(path, 'r') as f:
                return {**default_config, **json.load(f)}
        except FileNotFoundError:
            return default_config
    
    def check_demographic_parity(self, predictions, protected, 
                                   threshold: float = None) -> FairnessCheckResult:
        """Check demographic parity."""
        threshold = threshold or self.config['thresholds']['demographic_parity_difference']
        
        # Calculate rates by group
        groups = set(protected)
        rates = {}
        for group in groups:
            mask = [p == group for p in protected]
            group_preds = [pred for pred, m in zip(predictions, mask) if m]
            rates[str(group)] = sum(group_preds) / len(group_preds) if group_preds else 0
        
        difference = max(rates.values()) - min(rates.values())
        
        return FairnessCheckResult(
            passed=difference <= threshold,
            metric_name='demographic_parity_difference',
            actual_value=difference,
            threshold=threshold,
            details={'rates_by_group': rates}
        )
    
    def check_disparate_impact(self, predictions, protected,
                                threshold: float = None) -> FairnessCheckResult:
        """Check disparate impact ratio."""
        threshold = threshold or self.config['thresholds']['disparate_impact_ratio']
        
        groups = set(protected)
        rates = {}
        for group in groups:
            mask = [p == group for p in protected]
            group_preds = [pred for pred, m in zip(predictions, mask) if m]
            rates[str(group)] = sum(group_preds) / len(group_preds) if group_preds else 0
        
        if max(rates.values()) == 0:
            ratio = 1.0
        else:
            ratio = min(rates.values()) / max(rates.values())
        
        return FairnessCheckResult(
            passed=ratio >= threshold,
            metric_name='disparate_impact_ratio',
            actual_value=ratio,
            threshold=threshold,
            details={'rates_by_group': rates}
        )
    
    def run_all_checks(self, predictions, actuals, protected) -> Dict:
        """Run all configured fairness checks."""
        results = {
            'checks': [],
            'overall_passed': True,
            'summary': {}
        }
        
        # Run each required check
        for check_name in self.config['required_checks']:
            if check_name == 'demographic_parity':
                result = self.check_demographic_parity(predictions, protected)
            elif check_name == 'disparate_impact':
                result = self.check_disparate_impact(predictions, protected)
            else:
                continue
            
            results['checks'].append({
                'name': result.metric_name,
                'passed': result.passed,
                'value': result.actual_value,
                'threshold': result.threshold,
                'details': result.details
            })
            
            if not result.passed:
                results['overall_passed'] = False
        
        # Summary
        results['summary'] = {
            'total_checks': len(results['checks']),
            'passed': sum(1 for c in results['checks'] if c['passed']),
            'failed': sum(1 for c in results['checks'] if not c['passed']),
        }
        
        return results
    
    def generate_ci_output(self, results: Dict) -> str:
        """Generate CI-friendly output."""
        output = []
        output.append("=" * 60)
        output.append("FAIRNESS CHECK RESULTS")
        output.append("=" * 60)
        
        for check in results['checks']:
            status = "✓ PASS" if check['passed'] else "✗ FAIL"
            output.append(f"\n{status}: {check['name']}")
            output.append(f"  Value: {check['value']:.4f}")
            output.append(f"  Threshold: {check['threshold']:.4f}")
        
        output.append("\n" + "-" * 60)
        output.append(f"Overall: {'PASSED' if results['overall_passed'] else 'FAILED'}")
        output.append(f"Checks: {results['summary']['passed']}/{results['summary']['total_checks']} passed")
        
        return "\n".join(output)


def main():
    """Main entry point for CI integration."""
    import argparse
    
    parser = argparse.ArgumentParser(description='Run fairness checks')
    parser.add_argument('--model-path', required=True, help='Path to model')
    parser.add_argument('--data-path', required=True, help='Path to test data')
    parser.add_argument('--config', default='fairness_config.json', help='Config file')
    parser.add_argument('--output', default='fairness_results.json', help='Output file')
    
    args = parser.parse_args()
    
    # Load model and data (implementation depends on format)
    # predictions, actuals, protected = load_and_predict(args.model_path, args.data_path)
    
    checker = CIFairnessChecker(args.config)
    # results = checker.run_all_checks(predictions, actuals, protected)
    
    # print(checker.generate_ci_output(results))
    
    # Exit with appropriate code
    # sys.exit(0 if results['overall_passed'] else 1)


if __name__ == '__main__':
    main()
```

### Pipeline Configuration Examples

#### GitHub Actions Workflow

```yaml
# .github/workflows/ai-testing.yml
name: AI Model Testing Pipeline

on:
  push:
    branches: [main, develop]
    paths:
      - 'models/**'
      - 'data/**'
      - 'src/**'
  pull_request:
    branches: [main]

jobs:
  data-validation:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.10'
      
      - name: Install dependencies
        run: |
          pip install great-expectations pandas
      
      - name: Run data validation
        run: |
          python scripts/validate_data.py --config data/expectations.json
  
  model-testing:
    needs: data-validation
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
          pip install fairlearn pytest
      
      - name: Run accuracy tests
        run: |
          pytest tests/test_model_accuracy.py -v
      
      - name: Run fairness checks
        run: |
          python scripts/fairness_check.py \
            --model-path models/latest.pkl \
            --data-path data/test.csv \
            --output results/fairness.json
      
      - name: Run robustness tests
        run: |
          python scripts/robustness_check.py \
            --model-path models/latest.pkl \
            --perturbations noise,rotation,blur
      
      - name: Upload test results
        uses: actions/upload-artifact@v3
        with:
          name: test-results
          path: results/
  
  fairness-gate:
    needs: model-testing
    runs-on: ubuntu-latest
    steps:
      - uses: actions/download-artifact@v3
        with:
          name: test-results
      
      - name: Check fairness gate
        run: |
          python -c "
          import json
          with open('results/fairness.json') as f:
              results = json.load(f)
          if not results['overall_passed']:
              print('FAIRNESS GATE FAILED')
              exit(1)
          print('FAIRNESS GATE PASSED')
          "
  
  deploy-staging:
    needs: fairness-gate
    if: github.ref == 'refs/heads/develop'
    runs-on: ubuntu-latest
    steps:
      - name: Deploy to staging
        run: |
          echo "Deploying to staging environment..."
          # Add deployment steps
```

#### Jenkins Pipeline

```groovy
// Jenkinsfile for AI Model Testing
pipeline {
    agent any
    
    environment {
        MODEL_PATH = 'models/latest.pkl'
        DATA_PATH = 'data/test.csv'
        FAIRNESS_THRESHOLD = '0.05'
    }
    
    stages {
        stage('Setup') {
            steps {
                sh 'pip install -r requirements.txt'
                sh 'pip install fairlearn pytest-html'
            }
        }
        
        stage('Data Validation') {
            steps {
                sh '''
                    python scripts/validate_data.py \
                        --data ${DATA_PATH} \
                        --schema data/schema.json
                '''
            }
            post {
                failure {
                    error 'Data validation failed!'
                }
            }
        }
        
        stage('Model Tests') {
            parallel {
                stage('Accuracy') {
                    steps {
                        sh 'pytest tests/test_accuracy.py --html=reports/accuracy.html'
                    }
                }
                stage('Fairness') {
                    steps {
                        sh '''
                            python scripts/fairness_check.py \
                                --model ${MODEL_PATH} \
                                --data ${DATA_PATH} \
                                --threshold ${FAIRNESS_THRESHOLD} \
                                --output reports/fairness.json
                        '''
                    }
                }
                stage('Robustness') {
                    steps {
                        sh '''
                            python scripts/robustness_check.py \
                                --model ${MODEL_PATH} \
                                --output reports/robustness.json
                        '''
                    }
                }
            }
        }
        
        stage('Fairness Gate') {
            steps {
                script {
                    def fairness = readJSON file: 'reports/fairness.json'
                    if (!fairness.overall_passed) {
                        currentBuild.result = 'FAILURE'
                        error 'Fairness requirements not met!'
                    }
                }
            }
        }
        
        stage('Deploy to Staging') {
            when {
                branch 'develop'
            }
            steps {
                sh 'scripts/deploy_staging.sh'
            }
        }
    }
    
    post {
        always {
            archiveArtifacts artifacts: 'reports/**', fingerprint: true
            publishHTML([
                reportDir: 'reports',
                reportFiles: 'accuracy.html',
                reportName: 'Test Results'
            ])
        }
        failure {
            slackSend channel: '#ml-alerts',
                      message: "AI Pipeline Failed: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
        }
    }
}
```

### Continuous Bias Monitoring

```python
"""
Continuous Bias Monitoring System
"""

import time
from datetime import datetime, timedelta
from collections import deque
import threading
import logging

class ContinuousBiasMonitor:
    """
    Monitor model predictions for bias in real-time.
    """
    
    def __init__(self, model_name: str, 
                 protected_attributes: list,
                 window_size: int = 10000,
                 check_interval: int = 300):  # 5 minutes
        self.model_name = model_name
        self.protected_attrs = protected_attributes
        self.window_size = window_size
        self.check_interval = check_interval
        
        # Sliding windows for each protected attribute
        self.prediction_windows = {
            attr: {} for attr in protected_attributes
        }
        
        self.alerts = []
        self.metrics_history = []
        self._lock = threading.Lock()
        self._running = False
        
        # Thresholds
        self.thresholds = {
            'demographic_parity_diff': 0.05,
            'alert_consecutive_failures': 3
        }
        
        self.consecutive_failures = {attr: 0 for attr in protected_attributes}
        
        logging.basicConfig(level=logging.INFO)
        self.logger = logging.getLogger(f'BiasMonitor-{model_name}')
    
    def record_prediction(self, prediction: int, protected_values: dict):
        """
        Record a prediction for monitoring.
        
        Args:
            prediction: The model's prediction (0 or 1)
            protected_values: Dict of {attribute: value}
        """
        with self._lock:
            for attr, value in protected_values.items():
                if attr not in self.prediction_windows:
                    continue
                
                if value not in self.prediction_windows[attr]:
                    self.prediction_windows[attr][value] = deque(maxlen=self.window_size)
                
                self.prediction_windows[attr][value].append({
                    'prediction': prediction,
                    'timestamp': datetime.now()
                })
    
    def calculate_current_metrics(self) -> dict:
        """Calculate current fairness metrics."""
        metrics = {
            'timestamp': datetime.now().isoformat(),
            'by_attribute': {}
        }
        
        with self._lock:
            for attr, groups in self.prediction_windows.items():
                if not groups:
                    continue
                
                rates = {}
                for group, predictions in groups.items():
                    if len(predictions) > 0:
                        rate = sum(p['prediction'] for p in predictions) / len(predictions)
                        rates[str(group)] = rate
                
                if rates:
                    diff = max(rates.values()) - min(rates.values()) if len(rates) > 1 else 0
                    metrics['by_attribute'][attr] = {
                        'rates': rates,
                        'difference': diff,
                        'samples_per_group': {g: len(self.prediction_windows[attr][g]) 
                                              for g in groups}
                    }
        
        return metrics
    
    def check_for_violations(self) -> list:
        """Check current metrics against thresholds."""
        metrics = self.calculate_current_metrics()
        violations = []
        
        for attr, data in metrics['by_attribute'].items():
            if data['difference'] > self.thresholds['demographic_parity_diff']:
                self.consecutive_failures[attr] += 1
                
                if self.consecutive_failures[attr] >= self.thresholds['alert_consecutive_failures']:
                    violations.append({
                        'attribute': attr,
                        'metric': 'demographic_parity_difference',
                        'value': data['difference'],
                        'threshold': self.thresholds['demographic_parity_diff'],
                        'consecutive_failures': self.consecutive_failures[attr],
                        'details': data
                    })
            else:
                self.consecutive_failures[attr] = 0
        
        return violations
    
    def start_monitoring(self):
        """Start background monitoring thread."""
        self._running = True
        self._monitor_thread = threading.Thread(target=self._monitoring_loop)
        self._monitor_thread.daemon = True
        self._monitor_thread.start()
        self.logger.info(f"Started bias monitoring for {self.model_name}")
    
    def stop_monitoring(self):
        """Stop monitoring."""
        self._running = False
        if self._monitor_thread:
            self._monitor_thread.join()
        self.logger.info(f"Stopped bias monitoring for {self.model_name}")
    
    def _monitoring_loop(self):
        """Background monitoring loop."""
        while self._running:
            try:
                metrics = self.calculate_current_metrics()
                self.metrics_history.append(metrics)
                
                violations = self.check_for_violations()
                if violations:
                    for v in violations:
                        self._handle_violation(v)
                
                time.sleep(self.check_interval)
                
            except Exception as e:
                self.logger.error(f"Monitoring error: {e}")
    
    def _handle_violation(self, violation: dict):
        """Handle a detected violation."""
        self.alerts.append({
            **violation,
            'timestamp': datetime.now().isoformat(),
            'model': self.model_name
        })
        
        self.logger.warning(
            f"BIAS ALERT: {violation['attribute']} - "
            f"{violation['metric']}={violation['value']:.4f} "
            f"(threshold: {violation['threshold']})"
        )
        
        # Send alert (implement notification here)
        self._send_alert(violation)
    
    def _send_alert(self, violation: dict):
        """Send alert notification."""
        # Implement: Slack, PagerDuty, email, etc.
        pass
    
    def get_status_report(self) -> str:
        """Generate current status report."""
        metrics = self.calculate_current_metrics()
        
        report = f"""
BIAS MONITORING STATUS REPORT
Model: {self.model_name}
Time: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}
{'='*50}

"""
        for attr, data in metrics['by_attribute'].items():
            status = "✓ OK" if data['difference'] <= self.thresholds['demographic_parity_diff'] else "⚠ ELEVATED"
            report += f"""
{attr.upper()}:
  Status: {status}
  Difference: {data['difference']:.4f} (threshold: {self.thresholds['demographic_parity_diff']})
  Rates by group:
"""
            for group, rate in data['rates'].items():
                samples = data['samples_per_group'].get(group, 0)
                report += f"    {group}: {rate:.4f} ({samples} samples)\n"
        
        if self.alerts:
            report += f"\nRecent Alerts: {len(self.alerts)}\n"
            for alert in self.alerts[-5:]:
                report += f"  - {alert['timestamp']}: {alert['attribute']} violation\n"
        
        return report
```

### Model Performance Regression Testing

```python
"""
Model Performance Regression Testing
"""

class ModelRegressionTester:
    """
    Compare new model performance against baseline.
    """
    
    def __init__(self, baseline_metrics_path: str):
        self.baseline = self._load_baseline(baseline_metrics_path)
        self.tolerance = {
            'accuracy': 0.01,  # Allow 1% drop
            'precision': 0.02,
            'recall': 0.02,
            'f1': 0.02,
            'latency_p99_ms': 50,  # Allow 50ms increase
            'fairness_diff': 0.01  # Allow 1% increase in disparity
        }
    
    def _load_baseline(self, path: str) -> dict:
        """Load baseline metrics."""
        import json
        with open(path, 'r') as f:
            return json.load(f)
    
    def compare(self, current_metrics: dict) -> dict:
        """Compare current metrics against baseline."""
        comparison = {
            'passed': True,
            'metrics': {},
            'regressions': [],
            'improvements': []
        }
        
        for metric, baseline_value in self.baseline.items():
            if metric not in current_metrics:
                continue
            
            current_value = current_metrics[metric]
            tolerance = self.tolerance.get(metric, 0.01)
            
            # Determine if regression or improvement
            diff = current_value - baseline_value
            
            # For latency and fairness_diff, lower is better
            if metric in ['latency_p99_ms', 'fairness_diff']:
                is_regression = diff > tolerance
                is_improvement = diff < -tolerance
            else:
                # For accuracy metrics, higher is better
                is_regression = diff < -tolerance
                is_improvement = diff > tolerance
            
            comparison['metrics'][metric] = {
                'baseline': baseline_value,
                'current': current_value,
                'difference': diff,
                'tolerance': tolerance,
                'status': 'regression' if is_regression else 'improvement' if is_improvement else 'stable'
            }
            
            if is_regression:
                comparison['passed'] = False
                comparison['regressions'].append(metric)
            elif is_improvement:
                comparison['improvements'].append(metric)
        
        return comparison
    
    def generate_report(self, comparison: dict) -> str:
        """Generate regression test report."""
        status = "✓ PASSED" if comparison['passed'] else "✗ FAILED"
        
        report = f"""
MODEL REGRESSION TEST REPORT
{'='*50}
Overall Status: {status}

METRIC COMPARISON
─────────────────
"""
        for metric, data in comparison['metrics'].items():
            icon = "↓" if data['status'] == 'regression' else "↑" if data['status'] == 'improvement' else "="
            report += f"""
{metric}:
  Baseline: {data['baseline']:.4f}
  Current:  {data['current']:.4f}
  Change:   {data['difference']:+.4f} {icon}
  Status:   {data['status'].upper()}
"""
        
        if comparison['regressions']:
            report += f"""
⚠ REGRESSIONS DETECTED:
  {', '.join(comparison['regressions'])}
"""
        
        if comparison['improvements']:
            report += f"""
✓ IMPROVEMENTS:
  {', '.join(comparison['improvements'])}
"""
        
        return report
```

### Alerting Configuration

```python
"""
AI Model Alerting System
"""

class AlertingSystem:
    """
    Multi-channel alerting for AI model issues.
    """
    
    def __init__(self, config: dict):
        self.config = config
        self.alert_channels = []
        
        # Initialize channels
        if 'slack' in config:
            self.alert_channels.append(self._create_slack_channel(config['slack']))
        if 'pagerduty' in config:
            self.alert_channels.append(self._create_pagerduty_channel(config['pagerduty']))
        if 'email' in config:
            self.alert_channels.append(self._create_email_channel(config['email']))
    
    def _create_slack_channel(self, config):
        """Create Slack alerting channel."""
        return {
            'type': 'slack',
            'webhook': config['webhook_url'],
            'channel': config.get('channel', '#ml-alerts')
        }
    
    def _create_pagerduty_channel(self, config):
        """Create PagerDuty channel."""
        return {
            'type': 'pagerduty',
            'routing_key': config['routing_key'],
            'severity_map': config.get('severity_map', {
                'critical': 'critical',
                'high': 'error',
                'medium': 'warning',
                'low': 'info'
            })
        }
    
    def _create_email_channel(self, config):
        """Create email channel."""
        return {
            'type': 'email',
            'recipients': config['recipients'],
            'smtp_config': config['smtp']
        }
    
    def send_alert(self, alert: dict):
        """Send alert through all configured channels."""
        for channel in self.alert_channels:
            try:
                if channel['type'] == 'slack':
                    self._send_slack_alert(channel, alert)
                elif channel['type'] == 'pagerduty':
                    self._send_pagerduty_alert(channel, alert)
                elif channel['type'] == 'email':
                    self._send_email_alert(channel, alert)
            except Exception as e:
                logging.error(f"Failed to send alert via {channel['type']}: {e}")
    
    def _send_slack_alert(self, channel: dict, alert: dict):
        """Send Slack alert."""
        import requests
        
        color = {
            'critical': '#FF0000',
            'high': '#FFA500',
            'medium': '#FFFF00',
            'low': '#00FF00'
        }.get(alert.get('severity', 'medium'), '#808080')
        
        payload = {
            'channel': channel['channel'],
            'attachments': [{
                'color': color,
                'title': f"AI Model Alert: {alert['title']}",
                'text': alert['message'],
                'fields': [
                    {'title': 'Model', 'value': alert.get('model', 'Unknown'), 'short': True},
                    {'title': 'Severity', 'value': alert.get('severity', 'Unknown'), 'short': True},
                ],
                'ts': datetime.now().timestamp()
            }]
        }
        
        requests.post(channel['webhook'], json=payload)
    
    def _send_pagerduty_alert(self, channel: dict, alert: dict):
        """Send PagerDuty alert."""
        import requests
        
        severity = channel['severity_map'].get(alert.get('severity', 'medium'), 'warning')
        
        payload = {
            'routing_key': channel['routing_key'],
            'event_action': 'trigger',
            'payload': {
                'summary': f"AI Model Alert: {alert['title']}",
                'severity': severity,
                'source': alert.get('model', 'ai-model'),
                'custom_details': alert
            }
        }
        
        requests.post('https://events.pagerduty.com/v2/enqueue', json=payload)
    
    def _send_email_alert(self, channel: dict, alert: dict):
        """Send email alert."""
        import smtplib
        from email.mime.text import MIMEText
        
        msg = MIMEText(f"""
AI Model Alert

Title: {alert['title']}
Severity: {alert.get('severity', 'Unknown')}
Model: {alert.get('model', 'Unknown')}
Time: {datetime.now().isoformat()}

Message:
{alert['message']}
        """)
        
        msg['Subject'] = f"[{alert.get('severity', 'ALERT').upper()}] AI Model: {alert['title']}"
        msg['From'] = channel['smtp_config']['from']
        msg['To'] = ', '.join(channel['recipients'])
        
        with smtplib.SMTP(channel['smtp_config']['host'], channel['smtp_config']['port']) as server:
            server.starttls()
            server.login(channel['smtp_config']['user'], channel['smtp_config']['password'])
            server.send_message(msg)
```

## Summary

- **CI/CD for AI** extends traditional pipelines with data and model quality checks
- **Automated fairness checks** become pipeline gates that block unfair models
- **Continuous bias monitoring** tracks fairness in production with alerting
- **Regression testing** ensures new models don't degrade from baseline
- **Multi-channel alerting** enables rapid response to model issues
- **Pipeline configurations** in GitHub Actions, Jenkins, etc. encode AI testing standards

## Additional Resources

- [MLOps: Continuous Delivery for ML (Google)](https://cloud.google.com/architecture/mlops-continuous-delivery-and-automation-pipelines-in-machine-learning)
- [Evidently AI - ML Monitoring](https://www.evidentlyai.com/)
- [Great Expectations - Data Validation](https://greatexpectations.io/)

