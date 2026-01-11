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

You've learned to test AI systems for fairness, robustness, and accuracy. You've set up automated pipelines to run these tests. But if no one sees the results, what's the point?

Data without visibility is just noise. You can implement the most sophisticated AI testing—fairness checks, robustness testing, bias monitoring—but if stakeholders can't see and understand the results, the value is lost. Dashboards transform raw metrics into actionable insights.

**Dashboards transform data into decisions.** They answer questions like:
- Is our model healthy right now?
- Is performance trending up or down?
- Are there emerging bias issues?
- Should we retrain soon?

Good dashboards make AI quality visible to the entire team—from engineers to executives. They're how you communicate that your testing efforts are working.

## The Concept

### What Makes AI Dashboards Different

Traditional application dashboards track uptime and response times. AI dashboards must also track model-specific concerns:

| Traditional Metrics | AI-Specific Metrics |
|--------------------|---------------------|
| Uptime | Model accuracy |
| Latency | Inference latency |
| Error rate | Prediction errors |
| Throughput | Fairness metrics |
| - | Data drift indicators |
| - | Prediction distributions |
| - | Model confidence |
| - | Bias monitoring |

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

### Dashboard Design Principles

#### 1. Information Hierarchy

Put the most important information at the top:

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

#### 2. Choose the Right Visualization

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


#### 3. Use Color Meaningfully

```
GREEN: Good / Within threshold
YELLOW: Warning / Approaching threshold
RED: Alert / Exceeded threshold
```

Don't use color just for decoration. Color should convey meaning.

### Building the Dashboard

#### Collecting Metrics

First, instrument your application to collect metrics:

```python
"""
Metrics Collection for AI Dashboard
"""

from datetime import datetime

class MetricsCollector:
    """Collect AI model metrics for dashboard."""
    
    def __init__(self, model_name):
        self.model_name = model_name
        self.predictions = []
        self.latencies = []
    
    def record_prediction(self, prediction, confidence, latency, protected_values=None):
        """
        Record a single prediction for monitoring.
        """
        record = {
            "timestamp": datetime.now(),
            "prediction": prediction,
            "confidence": confidence,
            "latency_ms": latency * 1000,
            "protected": protected_values or {}
        }
        self.predictions.append(record)
        self.latencies.append(latency * 1000)
    
    def get_summary(self, window_minutes=60):
        """
        Get summary metrics for recent predictions.
        """
        # Filter to recent window
        cutoff = datetime.now() - timedelta(minutes=window_minutes)
        recent = [p for p in self.predictions if p["timestamp"] > cutoff]
        
        if not recent:
            return None
        
        latencies = [p["latency_ms"] for p in recent]
        confidences = [p["confidence"] for p in recent]
        
        return {
            "total_predictions": len(recent),
            "avg_confidence": sum(confidences) / len(confidences),
            "latency_p50": sorted(latencies)[len(latencies)//2],
            "latency_p99": sorted(latencies)[int(len(latencies)*0.99)],
        }
    
    def get_fairness_metrics(self, protected_attr, window_minutes=60):
        """
        Get fairness metrics for a protected attribute.
        """
        cutoff = datetime.now() - timedelta(minutes=window_minutes)
        recent = [p for p in self.predictions if p["timestamp"] > cutoff]
        
        groups = {}
        for p in recent:
            group = p["protected"].get(protected_attr)
            if group:
                if group not in groups:
                    groups[group] = []
                groups[group].append(p["prediction"])
        
        rates = {}
        for group, preds in groups.items():
            rates[group] = sum(preds) / len(preds) if preds else 0
        
        if len(rates) >= 2:
            diff = max(rates.values()) - min(rates.values())
        else:
            diff = 0
        
        return {
            "rates_by_group": rates,
            "demographic_parity_difference": diff
        }
```

#### Dashboard Display

Simple text-based dashboard example:

```python
"""
Simple AI Dashboard Display
"""

def display_dashboard(metrics):
    """
    Display dashboard in terminal/logs.
    """
    print("=" * 60)
    print("AI MODEL HEALTH DASHBOARD")
    print(f"Time: {datetime.now().strftime('%Y-%m-%d %H:%M')}")
    print("=" * 60)
    
    # Overall health
    health = "🟢 HEALTHY" if metrics["healthy"] else "🔴 DEGRADED"
    print(f"\nOVERALL: {health}")
    
    # Performance
    print("\nPERFORMANCE:")
    print(f"  Accuracy:    {metrics['accuracy']:.1%}")
    print(f"  Latency P50: {metrics['latency_p50']:.0f}ms")
    print(f"  Latency P99: {metrics['latency_p99']:.0f}ms")
    
    # Fairness
    print("\nFAIRNESS:")
    print(f"  Parity Diff: {metrics['fairness_diff']:.1%}")
    status = "✓ OK" if metrics['fairness_diff'] < 0.05 else "⚠ WARNING"
    print(f"  Status:      {status}")
    
    # Predictions today
    print(f"\nVOLUME:")
    print(f"  Predictions today: {metrics['predictions_today']:,}")
    
    print("=" * 60)
```

### Trend Analysis

Track how metrics change over time:

```python
def analyze_trend(metric_history, lookback_days=7):
    """
    Analyze trend in metric over recent days.
    """
    if len(metric_history) < 2:
        return {"trend": "insufficient_data"}
    
    # Simple linear regression
    x = list(range(len(metric_history)))
    y = metric_history
    
    n = len(x)
    sum_x = sum(x)
    sum_y = sum(y)
    sum_xy = sum(x[i] * y[i] for i in range(n))
    sum_x2 = sum(xi ** 2 for xi in x)
    
    slope = (n * sum_xy - sum_x * sum_y) / (n * sum_x2 - sum_x ** 2)
    
    # Classify trend
    if slope > 0.01:
        direction = "improving"
    elif slope < -0.01:
        direction = "declining"
    else:
        direction = "stable"
    
    return {
        "trend": direction,
        "slope": slope,
        "current": y[-1],
        "start": y[0]
    }
```

### Anomaly Detection

Detect unusual metric values:

```python
def detect_anomaly(current_value, historical_values, threshold=3.0):
    """
    Detect if current value is anomalous.
    Uses simple z-score method.
    """
    if len(historical_values) < 10:
        return {"anomaly": False, "reason": "insufficient_history"}
    
    mean = sum(historical_values) / len(historical_values)
    variance = sum((v - mean) ** 2 for v in historical_values) / len(historical_values)
    std = variance ** 0.5
    
    if std == 0:
        return {"anomaly": False, "reason": "no_variance"}
    
    z_score = abs(current_value - mean) / std
    
    return {
        "anomaly": z_score > threshold,
        "z_score": z_score,
        "current": current_value,
        "mean": mean,
        "std": std
    }
```

### Communicating to Stakeholders

Different audiences need different views:

**For Executives:**
- High-level health status (green/yellow/red)
- Business impact metrics (predictions served, decisions made)
- Compliance status (fairness requirements met?)
- One-sentence summary

**For Data Scientists:**
- Detailed accuracy metrics
- Feature importance changes
- Drift scores by feature
- Model comparison

**For Engineers:**
- Latency percentiles
- Error rates and types
- Resource utilization
- System health

### Dashboard Tools

| Tool | Best For | Complexity |
|------|----------|------------|
| **Grafana** | Time-series, alerting | Medium |
| **Evidently** | ML-specific monitoring | Low-Medium |
| **MLflow** | Experiment tracking | Medium |
| **Streamlit/Dash** | Custom dashboards | Low |
| **Prometheus + Grafana** | Full monitoring stack | High |

## Code Example

```python
"""
Simple AI Quality Dashboard
"""

from datetime import datetime, timedelta

class AIDashboard:
    """Simple AI model quality dashboard."""
    
    def __init__(self):
        self.metrics_history = []
    
    def record_hourly_metrics(self, metrics):
        """Record hourly summary."""
        self.metrics_history.append({
            "timestamp": datetime.now(),
            **metrics
        })
    
    def get_current_status(self):
        """Get current dashboard status."""
        if not self.metrics_history:
            return {"status": "no_data"}
        
        current = self.metrics_history[-1]
        
        # Determine health
        healthy = (
            current.get("accuracy", 0) >= 0.90 and
            current.get("fairness_diff", 1) <= 0.05 and
            current.get("latency_p99", 1000) <= 200
        )
        
        return {
            "healthy": healthy,
            "accuracy": current.get("accuracy", 0),
            "fairness_diff": current.get("fairness_diff", 0),
            "latency_p99": current.get("latency_p99", 0),
            "predictions_today": current.get("predictions", 0),
            "last_updated": current["timestamp"]
        }
    
    def get_trends(self):
        """Get metric trends."""
        if len(self.metrics_history) < 24:  # Need 24 hours
            return {"status": "building_history"}
        
        recent = self.metrics_history[-24:]
        accuracies = [m.get("accuracy", 0) for m in recent]
        
        return {
            "accuracy_trend": "stable" if max(accuracies) - min(accuracies) < 0.02 else "variable",
            "accuracy_min": min(accuracies),
            "accuracy_max": max(accuracies)
        }
```

## Summary

- **AI dashboards** display model quality metrics for visibility and decision-making
- **Key metrics:** Accuracy, latency, fairness measures, drift indicators
- **Design principles:** Information hierarchy, appropriate visualizations, meaningful color
- **Trend analysis** shows how metrics change over time
- **Anomaly detection** catches unusual values automatically
- **Different audiences** need different dashboard views
- **Tools:** Grafana, Evidently, MLflow, or custom with Streamlit/Dash

## Additional Resources

- [Grafana Documentation](https://grafana.com/docs/) - Dashboard and alerting platform
- [Evidently AI](https://www.evidentlyai.com/) - ML monitoring with built-in dashboards
- [Streamlit](https://streamlit.io/) - Quick Python dashboards
- [Google's ML Monitoring Best Practices](https://cloud.google.com/architecture/mlops-continuous-delivery-and-automation-pipelines-in-machine-learning) - MLOps guidance
