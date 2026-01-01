# AI Quality Metrics Dashboard

## Learning Objectives

- Design effective dashboards for monitoring AI model quality
- Identify key metrics to track for AI systems
- Apply visualization best practices for ML metrics
- Communicate AI performance to stakeholders effectively
- Implement trend analysis for model quality
- Set up anomaly detection for metrics
- Choose appropriate dashboard tools and technologies

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

Data without visibility is just noise. You can implement the most sophisticated AI testing—fairness checks, robustness testing, bias monitoring—but if stakeholders can't see and understand the results, the value is lost. Dashboards transform raw metrics into actionable insights.

Building on the monitoring and visualization concepts from Week 10 (Grafana, Prometheus), this module focuses specifically on AI quality dashboards. You'll learn what metrics matter for AI systems, how to visualize them effectively, and how to communicate AI quality to both technical and non-technical audiences.

## The Concept

### What Makes AI Dashboards Different

Traditional application dashboards focus on availability, latency, and error rates. AI dashboards must also track model-specific metrics:

```
Traditional Dashboard:               AI Quality Dashboard:
─────────────────────               ─────────────────────

• Uptime                            • Uptime
• Request latency                   • Inference latency
• Error rate                        • Error rate
• Throughput                        • Throughput
                                    
                                    PLUS:
                                    ─────
                                    • Accuracy metrics
                                    • Fairness metrics
                                    • Data drift indicators
                                    • Prediction distributions
                                    • Feature importance
                                    • Model confidence
                                    • Bias monitoring
                                    • OOD detection rates
```

### Key Metrics to Track

#### Performance Metrics

```
Model Performance Metrics:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  ACCURACY METRICS                                                       │
│  ────────────────                                                       │
│  • Accuracy: Overall correctness                                        │
│  • Precision: True positives / All positive predictions                 │
│  • Recall: True positives / All actual positives                        │
│  • F1 Score: Harmonic mean of precision and recall                      │
│  • AUC-ROC: Model's ability to distinguish classes                      │
│                                                                          │
│  INFERENCE METRICS                                                      │
│  ─────────────────                                                      │
│  • Latency (P50, P95, P99)                                              │
│  • Throughput (predictions/second)                                      │
│  • Error rate                                                           │
│  • Timeout rate                                                         │
│                                                                          │
│  RESOURCE METRICS                                                       │
│  ────────────────                                                       │
│  • GPU utilization                                                      │
│  • Memory usage                                                         │
│  • CPU utilization                                                      │
│  • Queue depth                                                          │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Fairness Metrics

```
Fairness Metrics:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  DEMOGRAPHIC PARITY                                                     │
│  ──────────────────                                                     │
│  Track: Selection rate difference across groups                         │
│  Alert if: > 5% difference                                              │
│  Visualization: Bar chart comparing rates                               │
│                                                                          │
│  EQUALIZED ODDS                                                         │
│  ───────────────                                                        │
│  Track: TPR and FPR by group                                            │
│  Alert if: > 5% difference in either                                    │
│  Visualization: Multi-series line chart                                 │
│                                                                          │
│  DISPARATE IMPACT RATIO                                                 │
│  ───────────────────────                                                │
│  Track: Min rate / Max rate                                             │
│  Alert if: < 0.8 (four-fifths rule)                                     │
│  Visualization: Gauge showing ratio                                     │
│                                                                          │
│  CALIBRATION                                                            │
│  ───────────                                                            │
│  Track: Predicted probability vs actual rate by group                   │
│  Alert if: Calibration error > 0.05                                     │
│  Visualization: Calibration curves by group                             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Data Quality Metrics

```
Data Quality Metrics:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  DRIFT INDICATORS                                                       │
│  ────────────────                                                       │
│  • Feature distribution shift (KS statistic)                            │
│  • Prediction distribution shift                                        │
│  • Label distribution change                                            │
│                                                                          │
│  DATA QUALITY                                                           │
│  ────────────                                                           │
│  • Missing value rate                                                   │
│  • Out-of-range values                                                  │
│  • Cardinality changes                                                  │
│  • Schema violations                                                    │
│                                                                          │
│  OOD DETECTION                                                          │
│  ─────────────                                                          │
│  • OOD sample rate                                                      │
│  • Average OOD score                                                    │
│  • Rejected request rate                                                │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Dashboard Design Best Practices

#### Information Hierarchy

```
Dashboard Layout Principles:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  TOP: EXECUTIVE SUMMARY (Glanceable health indicators)                  │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  🟢 Model Health    🟡 Fairness    🟢 Performance   🟢 Data       │   │
│  │     99.2%              Warning         Normal          Normal     │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  MIDDLE: KEY METRICS (Most important trends)                            │
│  ┌───────────────────────┐  ┌───────────────────────┐                  │
│  │    Accuracy Trend     │  │   Fairness Metrics    │                  │
│  │    [Line Chart]       │  │   [Bar Chart]         │                  │
│  └───────────────────────┘  └───────────────────────┘                  │
│  ┌───────────────────────┐  ┌───────────────────────┐                  │
│  │   Latency Percentiles │  │   Data Drift          │                  │
│  │   [Multi-line]        │  │   [Heatmap]           │                  │
│  └───────────────────────┘  └───────────────────────┘                  │
│                                                                          │
│  BOTTOM: DETAILED BREAKDOWNS (Drill-down information)                   │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  Detailed tables, feature-level metrics, historical data         │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Visualization Selection

```
Metric Type → Best Visualization:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  SINGLE VALUE (current state)                                           │
│  → Gauge, Big Number, Status Indicator                                  │
│                                                                          │
│  TREND OVER TIME                                                        │
│  → Line Chart, Area Chart                                               │
│                                                                          │
│  COMPARISON BETWEEN GROUPS                                              │
│  → Bar Chart, Grouped Bar Chart                                         │
│                                                                          │
│  DISTRIBUTION                                                           │
│  → Histogram, Box Plot                                                  │
│                                                                          │
│  CORRELATION/RELATIONSHIP                                               │
│  → Scatter Plot, Heatmap                                                │
│                                                                          │
│  PROPORTION/COMPOSITION                                                 │
│  → Pie Chart (sparingly), Stacked Bar                                   │
│                                                                          │
│  MULTI-DIMENSIONAL                                                      │
│  → Heatmap, Parallel Coordinates                                        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Dashboard Implementation

#### Metric Collection

```python
"""
AI Metrics Collection Service
"""

from prometheus_client import Counter, Gauge, Histogram, start_http_server
from datetime import datetime
import threading
import time

class AIMetricsCollector:
    """
    Collect and expose AI model metrics for dashboard consumption.
    """
    
    def __init__(self, model_name: str, port: int = 8000):
        self.model_name = model_name
        
        # Performance metrics
        self.predictions_total = Counter(
            'model_predictions_total',
            'Total predictions made',
            ['model', 'outcome']
        )
        
        self.latency_histogram = Histogram(
            'model_inference_latency_seconds',
            'Inference latency in seconds',
            ['model'],
            buckets=[0.01, 0.025, 0.05, 0.1, 0.25, 0.5, 1.0, 2.5, 5.0]
        )
        
        self.accuracy_gauge = Gauge(
            'model_accuracy',
            'Current model accuracy',
            ['model']
        )
        
        # Fairness metrics
        self.selection_rate = Gauge(
            'model_selection_rate',
            'Selection rate by group',
            ['model', 'protected_attribute', 'group']
        )
        
        self.demographic_parity_diff = Gauge(
            'model_demographic_parity_difference',
            'Demographic parity difference',
            ['model', 'protected_attribute']
        )
        
        self.equalized_odds_diff = Gauge(
            'model_equalized_odds_difference',
            'Equalized odds difference (max of TPR/FPR diff)',
            ['model', 'protected_attribute']
        )
        
        # Data quality metrics
        self.data_drift_score = Gauge(
            'model_data_drift_score',
            'Data drift score by feature',
            ['model', 'feature']
        )
        
        self.ood_rate = Gauge(
            'model_ood_rate',
            'Out-of-distribution sample rate',
            ['model']
        )
        
        self.prediction_confidence = Histogram(
            'model_prediction_confidence',
            'Prediction confidence distribution',
            ['model'],
            buckets=[0.5, 0.6, 0.7, 0.8, 0.9, 0.95, 0.99]
        )
        
        # Start metrics server
        start_http_server(port)
    
    def record_prediction(self, prediction: int, confidence: float, 
                          latency: float, protected_values: dict = None):
        """Record a single prediction."""
        # Basic metrics
        self.predictions_total.labels(
            model=self.model_name, 
            outcome=str(prediction)
        ).inc()
        
        self.latency_histogram.labels(model=self.model_name).observe(latency)
        self.prediction_confidence.labels(model=self.model_name).observe(confidence)
    
    def update_accuracy(self, accuracy: float):
        """Update accuracy metric."""
        self.accuracy_gauge.labels(model=self.model_name).set(accuracy)
    
    def update_fairness_metrics(self, attribute: str, metrics: dict):
        """Update fairness metrics for a protected attribute."""
        # Selection rates by group
        for group, rate in metrics.get('selection_rates', {}).items():
            self.selection_rate.labels(
                model=self.model_name,
                protected_attribute=attribute,
                group=str(group)
            ).set(rate)
        
        # Demographic parity difference
        if 'dp_difference' in metrics:
            self.demographic_parity_diff.labels(
                model=self.model_name,
                protected_attribute=attribute
            ).set(metrics['dp_difference'])
        
        # Equalized odds difference
        if 'eo_difference' in metrics:
            self.equalized_odds_diff.labels(
                model=self.model_name,
                protected_attribute=attribute
            ).set(metrics['eo_difference'])
    
    def update_drift_scores(self, drift_scores: dict):
        """Update data drift scores by feature."""
        for feature, score in drift_scores.items():
            self.data_drift_score.labels(
                model=self.model_name,
                feature=feature
            ).set(score)
    
    def update_ood_rate(self, rate: float):
        """Update OOD detection rate."""
        self.ood_rate.labels(model=self.model_name).set(rate)
```

#### Grafana Dashboard Configuration

```json
{
  "dashboard": {
    "title": "AI Model Quality Dashboard",
    "tags": ["ai", "ml", "quality"],
    "refresh": "30s",
    "panels": [
      {
        "title": "Model Health Status",
        "type": "stat",
        "gridPos": {"h": 4, "w": 6, "x": 0, "y": 0},
        "targets": [
          {
            "expr": "model_accuracy{model=\"production\"}"
          }
        ],
        "options": {
          "colorMode": "background",
          "graphMode": "none",
          "textMode": "value_and_name"
        },
        "fieldConfig": {
          "defaults": {
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {"color": "red", "value": 0},
                {"color": "yellow", "value": 0.9},
                {"color": "green", "value": 0.95}
              ]
            },
            "unit": "percentunit"
          }
        }
      },
      {
        "title": "Fairness Status",
        "type": "stat",
        "gridPos": {"h": 4, "w": 6, "x": 6, "y": 0},
        "targets": [
          {
            "expr": "model_demographic_parity_difference{model=\"production\"}"
          }
        ],
        "options": {
          "colorMode": "background"
        },
        "fieldConfig": {
          "defaults": {
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {"color": "green", "value": 0},
                {"color": "yellow", "value": 0.03},
                {"color": "red", "value": 0.05}
              ]
            }
          }
        }
      },
      {
        "title": "Accuracy Trend",
        "type": "timeseries",
        "gridPos": {"h": 8, "w": 12, "x": 0, "y": 4},
        "targets": [
          {
            "expr": "model_accuracy{model=\"production\"}",
            "legendFormat": "Accuracy"
          }
        ],
        "options": {
          "legend": {"displayMode": "list", "placement": "bottom"}
        },
        "fieldConfig": {
          "defaults": {
            "custom": {
              "lineWidth": 2,
              "fillOpacity": 10
            },
            "unit": "percentunit"
          }
        }
      },
      {
        "title": "Selection Rates by Group",
        "type": "barchart",
        "gridPos": {"h": 8, "w": 12, "x": 12, "y": 4},
        "targets": [
          {
            "expr": "model_selection_rate{model=\"production\"}",
            "legendFormat": "{{group}}"
          }
        ]
      },
      {
        "title": "Inference Latency",
        "type": "timeseries",
        "gridPos": {"h": 8, "w": 12, "x": 0, "y": 12},
        "targets": [
          {
            "expr": "histogram_quantile(0.5, rate(model_inference_latency_seconds_bucket[5m]))",
            "legendFormat": "P50"
          },
          {
            "expr": "histogram_quantile(0.95, rate(model_inference_latency_seconds_bucket[5m]))",
            "legendFormat": "P95"
          },
          {
            "expr": "histogram_quantile(0.99, rate(model_inference_latency_seconds_bucket[5m]))",
            "legendFormat": "P99"
          }
        ],
        "fieldConfig": {
          "defaults": {
            "unit": "s"
          }
        }
      },
      {
        "title": "Data Drift by Feature",
        "type": "heatmap",
        "gridPos": {"h": 8, "w": 12, "x": 12, "y": 12},
        "targets": [
          {
            "expr": "model_data_drift_score{model=\"production\"}"
          }
        ]
      }
    ]
  }
}
```

### Stakeholder Communication

#### Executive Dashboard

```python
"""
Executive Summary Dashboard Generator
"""

class ExecutiveDashboard:
    """
    Generate executive-level AI quality summary.
    """
    
    def __init__(self, metrics_source):
        self.metrics = metrics_source
    
    def generate_summary(self) -> dict:
        """Generate executive summary."""
        return {
            'overall_health': self._calculate_health_score(),
            'key_metrics': {
                'accuracy': {
                    'value': self.metrics.get_accuracy(),
                    'trend': self.metrics.get_accuracy_trend(days=7),
                    'status': self._get_status(self.metrics.get_accuracy(), 0.95, 0.90)
                },
                'fairness': {
                    'value': self.metrics.get_fairness_score(),
                    'status': self._get_status(1 - self.metrics.get_dp_diff(), 0.95, 0.90)
                },
                'reliability': {
                    'value': self.metrics.get_uptime(),
                    'status': self._get_status(self.metrics.get_uptime(), 0.999, 0.99)
                },
                'latency': {
                    'value': self.metrics.get_p95_latency(),
                    'status': self._get_latency_status(self.metrics.get_p95_latency())
                }
            },
            'alerts_summary': {
                'critical': self.metrics.get_alert_count('critical'),
                'warning': self.metrics.get_alert_count('warning')
            },
            'business_impact': self._calculate_business_impact()
        }
    
    def _calculate_health_score(self) -> str:
        """Calculate overall health score."""
        accuracy = self.metrics.get_accuracy()
        fairness = 1 - self.metrics.get_dp_diff()
        reliability = self.metrics.get_uptime()
        
        score = (accuracy * 0.4 + fairness * 0.3 + reliability * 0.3)
        
        if score >= 0.95:
            return 'HEALTHY'
        elif score >= 0.85:
            return 'WARNING'
        else:
            return 'CRITICAL'
    
    def _get_status(self, value, good_threshold, warn_threshold):
        """Get status indicator."""
        if value >= good_threshold:
            return '🟢'
        elif value >= warn_threshold:
            return '🟡'
        else:
            return '🔴'
    
    def _get_latency_status(self, latency_ms):
        """Get latency status (lower is better)."""
        if latency_ms <= 100:
            return '🟢'
        elif latency_ms <= 500:
            return '🟡'
        else:
            return '🔴'
    
    def _calculate_business_impact(self) -> dict:
        """Calculate business impact metrics."""
        return {
            'predictions_today': self.metrics.get_prediction_count(hours=24),
            'estimated_decisions_affected': self.metrics.get_prediction_count(hours=24),
            'fairness_compliance': 'COMPLIANT' if self.metrics.get_dp_diff() < 0.05 else 'REVIEW NEEDED'
        }
    
    def generate_report(self) -> str:
        """Generate executive report text."""
        summary = self.generate_summary()
        
        report = f"""
╔══════════════════════════════════════════════════════════════════════════╗
║                    AI MODEL EXECUTIVE SUMMARY                             ║
╠══════════════════════════════════════════════════════════════════════════╣
║  Report Date: {datetime.now().strftime('%Y-%m-%d %H:%M')}                                              ║
║  Overall Status: {summary['overall_health']:<55}  ║
╠══════════════════════════════════════════════════════════════════════════╣

KEY METRICS
───────────
"""
        for metric, data in summary['key_metrics'].items():
            report += f"  {data['status']} {metric.title()}: {data['value']:.2%}\n"
        
        report += f"""
ALERTS
──────
  Critical: {summary['alerts_summary']['critical']}
  Warning: {summary['alerts_summary']['warning']}

BUSINESS IMPACT
───────────────
  Predictions (24h): {summary['business_impact']['predictions_today']:,}
  Fairness Compliance: {summary['business_impact']['fairness_compliance']}
"""
        
        return report
```

### Trend Analysis and Anomaly Detection

```python
"""
Trend Analysis and Anomaly Detection for AI Metrics
"""

import numpy as np
from scipy import stats

class MetricTrendAnalyzer:
    """
    Analyze trends and detect anomalies in AI metrics.
    """
    
    def __init__(self, window_size: int = 24):
        self.window_size = window_size
    
    def calculate_trend(self, values: list) -> dict:
        """
        Calculate trend statistics.
        """
        if len(values) < 2:
            return {'trend': 'insufficient_data'}
        
        x = np.arange(len(values))
        slope, intercept, r_value, p_value, std_err = stats.linregress(x, values)
        
        # Classify trend
        if p_value > 0.05:
            trend_direction = 'stable'
        elif slope > 0:
            trend_direction = 'increasing'
        else:
            trend_direction = 'decreasing'
        
        return {
            'trend': trend_direction,
            'slope': slope,
            'r_squared': r_value ** 2,
            'p_value': p_value,
            'current': values[-1],
            'change_rate': slope / np.mean(values) * 100  # % change per unit
        }
    
    def detect_anomalies(self, values: list, threshold: float = 3.0) -> list:
        """
        Detect anomalies using z-score method.
        """
        if len(values) < self.window_size:
            return []
        
        anomalies = []
        
        for i in range(self.window_size, len(values)):
            window = values[i-self.window_size:i]
            mean = np.mean(window)
            std = np.std(window)
            
            if std == 0:
                continue
            
            z_score = (values[i] - mean) / std
            
            if abs(z_score) > threshold:
                anomalies.append({
                    'index': i,
                    'value': values[i],
                    'z_score': z_score,
                    'expected_range': (mean - threshold*std, mean + threshold*std)
                })
        
        return anomalies
    
    def forecast_simple(self, values: list, periods: int = 7) -> list:
        """
        Simple linear forecast.
        """
        if len(values) < 2:
            return []
        
        trend = self.calculate_trend(values)
        last_value = values[-1]
        
        forecasts = []
        for i in range(1, periods + 1):
            forecasted = last_value + (trend['slope'] * i)
            forecasts.append({
                'period': i,
                'value': forecasted,
                'confidence': max(0, 1 - (i * 0.1))  # Confidence decreases with distance
            })
        
        return forecasts


class MetricAlertEngine:
    """
    Alert engine for metric anomalies.
    """
    
    def __init__(self, rules: dict):
        self.rules = rules
        self.analyzer = MetricTrendAnalyzer()
    
    def check_all_rules(self, metrics: dict) -> list:
        """Check all configured rules."""
        alerts = []
        
        for metric_name, rule in self.rules.items():
            if metric_name not in metrics:
                continue
            
            values = metrics[metric_name]
            
            # Threshold check
            if 'threshold' in rule:
                current = values[-1] if values else None
                if current is not None:
                    if rule.get('above', False) and current > rule['threshold']:
                        alerts.append({
                            'metric': metric_name,
                            'type': 'threshold_exceeded',
                            'value': current,
                            'threshold': rule['threshold'],
                            'severity': rule.get('severity', 'warning')
                        })
                    elif rule.get('below', False) and current < rule['threshold']:
                        alerts.append({
                            'metric': metric_name,
                            'type': 'threshold_below',
                            'value': current,
                            'threshold': rule['threshold'],
                            'severity': rule.get('severity', 'warning')
                        })
            
            # Trend check
            if rule.get('detect_trend'):
                trend = self.analyzer.calculate_trend(values)
                if trend['trend'] == 'decreasing' and rule.get('alert_on_decrease'):
                    alerts.append({
                        'metric': metric_name,
                        'type': 'degrading_trend',
                        'details': trend,
                        'severity': rule.get('severity', 'warning')
                    })
            
            # Anomaly check
            if rule.get('detect_anomalies'):
                anomalies = self.analyzer.detect_anomalies(values)
                if anomalies:
                    alerts.append({
                        'metric': metric_name,
                        'type': 'anomaly_detected',
                        'anomalies': anomalies[-3:],  # Last 3 anomalies
                        'severity': rule.get('severity', 'warning')
                    })
        
        return alerts
```

### Dashboard Tools and Technologies

```
Dashboard Technology Options:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  GRAFANA                                                                │
│  ───────                                                                │
│  Best for: Time-series visualization, alerting                          │
│  Data sources: Prometheus, InfluxDB, SQL databases                      │
│  AI-specific: Good for performance monitoring                           │
│  Pros: Flexible, powerful alerting, widely used                         │
│  Cons: Requires setup, learning curve                                   │
│                                                                          │
│  EVIDENTLY                                                              │
│  ─────────                                                              │
│  Best for: ML-specific monitoring, drift detection                      │
│  Data sources: Python DataFrames                                        │
│  AI-specific: Purpose-built for ML monitoring                           │
│  Pros: Easy setup, pre-built ML reports                                 │
│  Cons: Less flexible than Grafana                                       │
│                                                                          │
│  MLFLOW                                                                 │
│  ──────                                                                 │
│  Best for: Experiment tracking, model registry                          │
│  Data sources: MLflow tracking server                                   │
│  AI-specific: Designed for ML lifecycle                                 │
│  Pros: Full ML lifecycle support                                        │
│  Cons: More experiment-focused than monitoring                          │
│                                                                          │
│  CUSTOM (Streamlit/Dash)                                                │
│  ──────────────────────                                                 │
│  Best for: Custom visualizations, rapid prototyping                     │
│  Data sources: Any Python data source                                   │
│  AI-specific: Fully customizable                                        │
│  Pros: Maximum flexibility                                              │
│  Cons: Requires more development effort                                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

## Code Example

Complete dashboard implementation:

```python
"""
Complete AI Quality Dashboard Application
Using Streamlit for rapid deployment
"""

import streamlit as st
import pandas as pd
import numpy as np
import plotly.express as px
import plotly.graph_objects as go
from datetime import datetime, timedelta

class AIQualityDashboard:
    """
    Interactive AI quality monitoring dashboard.
    """
    
    def __init__(self):
        st.set_page_config(
            page_title="AI Model Quality Dashboard",
            page_icon="🤖",
            layout="wide"
        )
    
    def render(self, metrics_data: dict):
        """Render the complete dashboard."""
        st.title("🤖 AI Model Quality Dashboard")
        
        # Top-level health indicators
        self._render_health_status(metrics_data)
        
        # Main metrics
        col1, col2 = st.columns(2)
        
        with col1:
            self._render_accuracy_panel(metrics_data)
            self._render_latency_panel(metrics_data)
        
        with col2:
            self._render_fairness_panel(metrics_data)
            self._render_drift_panel(metrics_data)
        
        # Detailed tables
        self._render_detailed_metrics(metrics_data)
        
        # Alerts
        self._render_alerts(metrics_data)
    
    def _render_health_status(self, data: dict):
        """Render overall health status."""
        col1, col2, col3, col4 = st.columns(4)
        
        with col1:
            st.metric(
                label="Model Accuracy",
                value=f"{data['accuracy']:.1%}",
                delta=f"{data['accuracy_delta']:+.1%}"
            )
        
        with col2:
            fairness_status = "🟢 Fair" if data['dp_diff'] < 0.05 else "🟡 Review"
            st.metric(
                label="Fairness Status",
                value=fairness_status,
                delta=f"{data['dp_diff']:.3f} diff"
            )
        
        with col3:
            st.metric(
                label="P95 Latency",
                value=f"{data['p95_latency']:.0f}ms",
                delta=f"{data['latency_delta']:+.0f}ms"
            )
        
        with col4:
            st.metric(
                label="Predictions (24h)",
                value=f"{data['predictions_24h']:,}",
                delta=f"{data['predictions_delta']:+.0%}"
            )
    
    def _render_accuracy_panel(self, data: dict):
        """Render accuracy trend panel."""
        st.subheader("📈 Accuracy Trend")
        
        fig = go.Figure()
        fig.add_trace(go.Scatter(
            x=data['timestamps'],
            y=data['accuracy_history'],
            mode='lines+markers',
            name='Accuracy',
            line=dict(color='#2ecc71', width=2)
        ))
        
        # Add threshold line
        fig.add_hline(y=0.95, line_dash="dash", 
                      annotation_text="Target: 95%",
                      line_color="orange")
        
        fig.update_layout(
            yaxis_title="Accuracy",
            yaxis_tickformat='.1%',
            height=300
        )
        
        st.plotly_chart(fig, use_container_width=True)
    
    def _render_fairness_panel(self, data: dict):
        """Render fairness metrics panel."""
        st.subheader("⚖️ Fairness Metrics")
        
        # Bar chart of selection rates by group
        fig = px.bar(
            x=list(data['selection_rates'].keys()),
            y=list(data['selection_rates'].values()),
            labels={'x': 'Group', 'y': 'Selection Rate'},
            color=list(data['selection_rates'].values()),
            color_continuous_scale=['red', 'yellow', 'green']
        )
        
        fig.update_layout(height=300)
        st.plotly_chart(fig, use_container_width=True)
        
        # Fairness metrics
        col1, col2 = st.columns(2)
        with col1:
            st.metric("Demographic Parity Diff", f"{data['dp_diff']:.3f}")
        with col2:
            st.metric("Disparate Impact Ratio", f"{data['di_ratio']:.3f}")
    
    def _render_latency_panel(self, data: dict):
        """Render latency panel."""
        st.subheader("⚡ Inference Latency")
        
        fig = go.Figure()
        
        for percentile, values in [('P50', data['p50_history']), 
                                   ('P95', data['p95_history']),
                                   ('P99', data['p99_history'])]:
            fig.add_trace(go.Scatter(
                x=data['timestamps'],
                y=values,
                mode='lines',
                name=percentile
            ))
        
        fig.update_layout(
            yaxis_title="Latency (ms)",
            height=300,
            legend=dict(orientation="h", yanchor="bottom", y=1.02)
        )
        
        st.plotly_chart(fig, use_container_width=True)
    
    def _render_drift_panel(self, data: dict):
        """Render data drift panel."""
        st.subheader("📊 Data Drift Detection")
        
        # Heatmap of drift scores
        drift_df = pd.DataFrame({
            'Feature': list(data['drift_scores'].keys()),
            'Drift Score': list(data['drift_scores'].values())
        })
        
        fig = px.bar(
            drift_df,
            x='Feature',
            y='Drift Score',
            color='Drift Score',
            color_continuous_scale=['green', 'yellow', 'red']
        )
        
        fig.add_hline(y=0.1, line_dash="dash",
                      annotation_text="Alert Threshold",
                      line_color="red")
        
        fig.update_layout(height=300)
        st.plotly_chart(fig, use_container_width=True)
    
    def _render_detailed_metrics(self, data: dict):
        """Render detailed metrics table."""
        st.subheader("📋 Detailed Metrics")
        
        metrics_df = pd.DataFrame({
            'Metric': ['Accuracy', 'Precision', 'Recall', 'F1 Score', 'AUC-ROC'],
            'Value': [data['accuracy'], data['precision'], 
                     data['recall'], data['f1'], data['auc_roc']],
            'Target': [0.95, 0.90, 0.90, 0.90, 0.95],
            'Status': ['✅' if v >= t else '⚠️' 
                      for v, t in zip(
                          [data['accuracy'], data['precision'], 
                           data['recall'], data['f1'], data['auc_roc']],
                          [0.95, 0.90, 0.90, 0.90, 0.95]
                      )]
        })
        
        st.dataframe(metrics_df, use_container_width=True)
    
    def _render_alerts(self, data: dict):
        """Render alerts section."""
        st.subheader("🚨 Active Alerts")
        
        if data.get('alerts'):
            for alert in data['alerts']:
                if alert['severity'] == 'critical':
                    st.error(f"🔴 {alert['message']}")
                elif alert['severity'] == 'warning':
                    st.warning(f"🟡 {alert['message']}")
                else:
                    st.info(f"🔵 {alert['message']}")
        else:
            st.success("✅ No active alerts")


# Run dashboard
if __name__ == "__main__":
    # Sample data (replace with actual metrics)
    sample_data = {
        'accuracy': 0.956,
        'accuracy_delta': 0.002,
        'accuracy_history': [0.95, 0.952, 0.954, 0.953, 0.956],
        'dp_diff': 0.032,
        'di_ratio': 0.89,
        'p95_latency': 125,
        'latency_delta': -5,
        'p50_history': [80, 82, 81, 79, 80],
        'p95_history': [120, 125, 122, 130, 125],
        'p99_history': [200, 210, 205, 215, 210],
        'predictions_24h': 125000,
        'predictions_delta': 0.05,
        'timestamps': pd.date_range(end=datetime.now(), periods=5, freq='D'),
        'selection_rates': {'Group A': 0.72, 'Group B': 0.68, 'Group C': 0.70},
        'drift_scores': {'feature_1': 0.02, 'feature_2': 0.08, 'feature_3': 0.15, 'feature_4': 0.03},
        'precision': 0.94,
        'recall': 0.92,
        'f1': 0.93,
        'auc_roc': 0.97,
        'alerts': []
    }
    
    dashboard = AIQualityDashboard()
    dashboard.render(sample_data)
```

## Summary

- **AI dashboards** extend traditional monitoring with model-specific metrics
- **Key metrics:** Accuracy, fairness, drift, latency, confidence distributions
- **Visualization principles:** Information hierarchy, appropriate chart types, clear status indicators
- **Stakeholder communication:** Executive summaries for leadership, detailed views for engineers
- **Trend analysis:** Detect degradation before it becomes critical
- **Tool selection:** Grafana for time-series, Evidently for ML-specific, Streamlit for custom

## Additional Resources

- [Evidently AI - ML Monitoring](https://www.evidentlyai.com/)
- [Grafana Machine Learning Plugin](https://grafana.com/grafana/plugins/grafana-ml-app/)
- [Streamlit Documentation](https://docs.streamlit.io/)
- [MLOps Dashboard Best Practices](https://neptune.ai/blog/ml-model-monitoring-best-tools)

