# Out-of-Distribution Testing

## Learning Objectives

- Understand distribution shift and its impact on AI systems
- Apply OOD detection techniques to identify unusual inputs
- Recognize differences between training and production data
- Implement strategies for handling unknown inputs gracefully
- Set up OOD monitoring for production systems
- Test models for distribution robustness

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

A machine learning model is trained on historical data, but deployed in a world that keeps changing. A model trained on 2020 user behavior encounters 2024 users. A medical imaging model trained on one hospital's equipment encounters scans from different machines. A sentiment analysis model trained on formal text encounters social media slang.

This fundamental mismatch—**distribution shift**—is one of the most common causes of AI failure in production. Models confidently make predictions on data unlike anything they've seen before, often with disastrous results.

Out-of-distribution (OOD) testing ensures models can recognize when they're "out of their depth" and handle such situations appropriately. This is essential for building trustworthy AI systems that know what they don't know.

## The Concept

### What is Distribution Shift?

**Distribution shift** occurs when the data a model encounters in production differs from the data it was trained on. The model's learned patterns may no longer apply.

```
Distribution Shift Illustrated:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  TRAINING DATA                    PRODUCTION DATA                       │
│  ─────────────                    ───────────────                       │
│                                                                          │
│  [Users aged 18-35]               [Users aged 18-65]                    │
│  [English text only]              [Multilingual text]                   │
│  [Product images, studio]         [Product images, user photos]         │
│  [Normal market conditions]       [Market crash conditions]             │
│  [2018-2022 data]                 [2024 data]                           │
│                                                                          │
│           ↓                                ↓                             │
│      Model learns                    Model encounters                    │
│      these patterns                  different patterns                  │
│           ↓                                ↓                             │
│      98% accuracy                    ???                                │
│                                                                          │
│  IF MODEL DOESN'T DETECT SHIFT:                                         │
│  • Confident but wrong predictions                                      │
│  • Silent failures                                                      │
│  • Degrading user experience                                            │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Types of Distribution Shift

```
Distribution Shift Taxonomy:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  1. COVARIATE SHIFT                                                     │
│     ─────────────────                                                   │
│     Input distribution changes: P(X) changes, P(Y|X) stays same         │
│                                                                          │
│     Example: Image classifier trained on daytime photos                 │
│     encounters nighttime photos. Objects are same, lighting different.  │
│                                                                          │
│  ───────────────────────────────────────────────────────────────────    │
│                                                                          │
│  2. LABEL SHIFT (Prior Shift)                                           │
│     ───────────────────────────                                         │
│     Label distribution changes: P(Y) changes, P(X|Y) stays same         │
│                                                                          │
│     Example: Fraud detector trained when fraud was 1%, deployed         │
│     when fraud is 5%. Same fraud patterns, different prevalence.        │
│                                                                          │
│  ───────────────────────────────────────────────────────────────────    │
│                                                                          │
│  3. CONCEPT DRIFT                                                       │
│     ─────────────────                                                   │
│     Relationship changes: P(Y|X) changes                                │
│                                                                          │
│     Example: What users consider "good content" changes over time.      │
│     Same features, different meaning.                                   │
│                                                                          │
│  ───────────────────────────────────────────────────────────────────    │
│                                                                          │
│  4. DOMAIN SHIFT                                                        │
│     ─────────────                                                       │
│     Data comes from different domain entirely                           │
│                                                                          │
│     Example: Model trained on news text applied to social media.        │
│     Entirely different data characteristics.                            │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### OOD Detection Techniques

#### 1. Confidence-Based Detection

Assume OOD samples have lower prediction confidence:

```python
"""
Confidence-Based OOD Detection
"""

import numpy as np

class ConfidenceBasedOODDetector:
    """
    Detect OOD samples based on prediction confidence.
    """
    
    def __init__(self, model, threshold: float = 0.5):
        self.model = model
        self.threshold = threshold
    
    def get_confidence(self, X) -> np.ndarray:
        """Get maximum prediction probability."""
        probas = self.model.predict_proba(X)
        return np.max(probas, axis=1)
    
    def detect_ood(self, X) -> np.ndarray:
        """
        Detect OOD samples.
        
        Returns:
            Boolean array: True for OOD samples
        """
        confidences = self.get_confidence(X)
        return confidences < self.threshold
    
    def calibrate_threshold(self, X_val, percentile: float = 5):
        """
        Set threshold based on validation data.
        
        Args:
            X_val: Validation data (in-distribution)
            percentile: Percentile of confidence to use as threshold
        """
        confidences = self.get_confidence(X_val)
        self.threshold = np.percentile(confidences, percentile)
        return self.threshold


class TemperatureScaledOOD(ConfidenceBasedOODDetector):
    """
    OOD detection with temperature scaling for better calibration.
    """
    
    def __init__(self, model, temperature: float = 1.0, threshold: float = 0.5):
        super().__init__(model, threshold)
        self.temperature = temperature
    
    def get_confidence(self, X) -> np.ndarray:
        """Get temperature-scaled confidence."""
        # Get logits (raw model outputs before softmax)
        if hasattr(self.model, 'predict_logits'):
            logits = self.model.predict_logits(X)
        else:
            # Approximate from probabilities
            probas = self.model.predict_proba(X)
            logits = np.log(probas + 1e-10)
        
        # Apply temperature scaling
        scaled_logits = logits / self.temperature
        scaled_probas = self._softmax(scaled_logits)
        
        return np.max(scaled_probas, axis=1)
    
    def _softmax(self, x):
        exp_x = np.exp(x - np.max(x, axis=1, keepdims=True))
        return exp_x / np.sum(exp_x, axis=1, keepdims=True)
```

#### 2. Distance-Based Detection

Measure distance from training distribution:

```python
"""
Distance-Based OOD Detection
"""

from sklearn.neighbors import NearestNeighbors
from sklearn.covariance import EmpiricalCovariance
import numpy as np

class DistanceBasedOODDetector:
    """
    Detect OOD using distance to training data.
    """
    
    def __init__(self, k_neighbors: int = 5):
        self.k = k_neighbors
        self.nn = None
        self.threshold = None
    
    def fit(self, X_train):
        """Fit on training data."""
        self.nn = NearestNeighbors(n_neighbors=self.k)
        self.nn.fit(X_train)
        
        # Calculate distances for training data
        distances, _ = self.nn.kneighbors(X_train)
        self.train_distances = distances.mean(axis=1)
        
        # Set threshold at 95th percentile of training distances
        self.threshold = np.percentile(self.train_distances, 95)
        
        return self
    
    def get_ood_scores(self, X) -> np.ndarray:
        """Calculate OOD score (higher = more likely OOD)."""
        distances, _ = self.nn.kneighbors(X)
        return distances.mean(axis=1)
    
    def detect_ood(self, X) -> np.ndarray:
        """Detect OOD samples."""
        scores = self.get_ood_scores(X)
        return scores > self.threshold


class MahalanobisOODDetector:
    """
    OOD detection using Mahalanobis distance.
    """
    
    def __init__(self):
        self.covariance = None
        self.mean = None
        self.threshold = None
    
    def fit(self, X_train):
        """Fit covariance on training data."""
        self.mean = np.mean(X_train, axis=0)
        self.covariance = EmpiricalCovariance().fit(X_train)
        
        # Calculate Mahalanobis distances for training data
        train_distances = self.covariance.mahalanobis(X_train)
        self.threshold = np.percentile(train_distances, 95)
        
        return self
    
    def get_ood_scores(self, X) -> np.ndarray:
        """Calculate Mahalanobis distance."""
        return self.covariance.mahalanobis(X)
    
    def detect_ood(self, X) -> np.ndarray:
        """Detect OOD samples."""
        distances = self.get_ood_scores(X)
        return distances > self.threshold
```

#### 3. Ensemble-Based Detection

Use prediction disagreement among models:

```python
"""
Ensemble-Based OOD Detection
"""

class EnsembleOODDetector:
    """
    Detect OOD using ensemble disagreement.
    """
    
    def __init__(self, models: list, threshold: float = 0.3):
        self.models = models
        self.threshold = threshold
    
    def get_predictions(self, X) -> np.ndarray:
        """Get predictions from all models."""
        predictions = []
        for model in self.models:
            pred = model.predict(X)
            predictions.append(pred)
        return np.array(predictions)
    
    def get_disagreement(self, X) -> np.ndarray:
        """
        Calculate prediction disagreement.
        Higher disagreement = more likely OOD.
        """
        predictions = self.get_predictions(X)
        
        # For each sample, calculate how many unique predictions
        disagreement = []
        for i in range(predictions.shape[1]):
            sample_preds = predictions[:, i]
            unique_preds = len(np.unique(sample_preds))
            # Normalize by number of models
            disagreement.append(unique_preds / len(self.models))
        
        return np.array(disagreement)
    
    def detect_ood(self, X) -> np.ndarray:
        """Detect OOD samples based on disagreement."""
        disagreement = self.get_disagreement(X)
        return disagreement > self.threshold
```

### Training vs Production Data Differences

```python
"""
Comparing Training and Production Distributions
"""

from scipy import stats
import numpy as np
import pandas as pd

class DistributionComparator:
    """
    Compare training and production data distributions.
    """
    
    def __init__(self, train_data: pd.DataFrame):
        self.train_data = train_data
        self.train_stats = self._compute_stats(train_data)
    
    def _compute_stats(self, df: pd.DataFrame) -> dict:
        """Compute statistics for each column."""
        stats_dict = {}
        
        for col in df.columns:
            if df[col].dtype in [np.float64, np.int64]:
                stats_dict[col] = {
                    'mean': df[col].mean(),
                    'std': df[col].std(),
                    'min': df[col].min(),
                    'max': df[col].max(),
                    'distribution': df[col].values
                }
            else:
                stats_dict[col] = {
                    'value_counts': df[col].value_counts(normalize=True).to_dict(),
                    'unique': df[col].nunique()
                }
        
        return stats_dict
    
    def compare(self, prod_data: pd.DataFrame) -> dict:
        """Compare production data to training data."""
        prod_stats = self._compute_stats(prod_data)
        comparison = {}
        
        for col in self.train_data.columns:
            if col not in prod_data.columns:
                comparison[col] = {'status': 'MISSING', 'drift': None}
                continue
            
            col_comparison = {}
            
            if 'mean' in self.train_stats[col]:
                # Numerical column
                train_dist = self.train_stats[col]['distribution']
                prod_dist = prod_stats[col]['distribution']
                
                # KS test for distribution difference
                ks_stat, p_value = stats.ks_2samp(train_dist, prod_dist)
                
                col_comparison = {
                    'type': 'numerical',
                    'ks_statistic': ks_stat,
                    'p_value': p_value,
                    'drift_detected': p_value < 0.05,
                    'mean_shift': prod_stats[col]['mean'] - self.train_stats[col]['mean'],
                    'train_mean': self.train_stats[col]['mean'],
                    'prod_mean': prod_stats[col]['mean'],
                }
            else:
                # Categorical column
                train_values = set(self.train_stats[col]['value_counts'].keys())
                prod_values = set(prod_stats[col]['value_counts'].keys())
                
                new_values = prod_values - train_values
                
                col_comparison = {
                    'type': 'categorical',
                    'new_categories': list(new_values),
                    'has_new_categories': len(new_values) > 0,
                    'train_unique': self.train_stats[col]['unique'],
                    'prod_unique': prod_stats[col]['unique'],
                }
            
            comparison[col] = col_comparison
        
        return comparison
    
    def generate_drift_report(self, prod_data: pd.DataFrame) -> str:
        """Generate human-readable drift report."""
        comparison = self.compare(prod_data)
        
        report = """
DISTRIBUTION DRIFT REPORT
═════════════════════════

"""
        
        drifted_cols = []
        new_category_cols = []
        
        for col, stats in comparison.items():
            if stats.get('drift_detected'):
                drifted_cols.append((col, stats))
            if stats.get('has_new_categories'):
                new_category_cols.append((col, stats))
        
        report += f"SUMMARY\n"
        report += f"───────\n"
        report += f"• Columns analyzed: {len(comparison)}\n"
        report += f"• Columns with drift: {len(drifted_cols)}\n"
        report += f"• Columns with new categories: {len(new_category_cols)}\n\n"
        
        if drifted_cols:
            report += "NUMERICAL DRIFT DETECTED\n"
            report += "────────────────────────\n"
            for col, stats in drifted_cols:
                report += f"\n{col}:\n"
                report += f"  KS statistic: {stats['ks_statistic']:.4f}\n"
                report += f"  p-value: {stats['p_value']:.4f}\n"
                report += f"  Mean shift: {stats['mean_shift']:+.2f}\n"
                report += f"  Train mean: {stats['train_mean']:.2f}, Prod mean: {stats['prod_mean']:.2f}\n"
        
        if new_category_cols:
            report += "\nNEW CATEGORIES DETECTED\n"
            report += "───────────────────────\n"
            for col, stats in new_category_cols:
                report += f"\n{col}:\n"
                report += f"  New values: {stats['new_categories']}\n"
        
        return report
```

### Handling Unknown Inputs Gracefully

```python
"""
Strategies for Handling OOD Inputs
"""

class OODHandler:
    """
    Handle out-of-distribution inputs appropriately.
    """
    
    def __init__(self, model, ood_detector, strategy: str = 'reject'):
        """
        Args:
            model: Main prediction model
            ood_detector: OOD detection model
            strategy: How to handle OOD - 'reject', 'fallback', 'uncertain'
        """
        self.model = model
        self.ood_detector = ood_detector
        self.strategy = strategy
        self.fallback_model = None
    
    def set_fallback_model(self, model):
        """Set fallback model for 'fallback' strategy."""
        self.fallback_model = model
    
    def predict(self, X) -> dict:
        """
        Make predictions with OOD handling.
        
        Returns:
            Dict with predictions and metadata
        """
        # Detect OOD samples
        is_ood = self.ood_detector.detect_ood(X)
        ood_scores = self.ood_detector.get_ood_scores(X)
        
        results = {
            'predictions': [],
            'is_ood': is_ood.tolist(),
            'ood_scores': ood_scores.tolist(),
            'handling': []
        }
        
        for i, (sample, ood, score) in enumerate(zip(X, is_ood, ood_scores)):
            sample = sample.reshape(1, -1)
            
            if not ood:
                # Normal prediction
                pred = self.model.predict(sample)[0]
                conf = self.model.predict_proba(sample).max()
                results['predictions'].append(pred)
                results['handling'].append('normal')
            
            elif self.strategy == 'reject':
                # Reject OOD samples
                results['predictions'].append(None)
                results['handling'].append('rejected')
            
            elif self.strategy == 'fallback':
                # Use fallback model
                if self.fallback_model:
                    pred = self.fallback_model.predict(sample)[0]
                    results['predictions'].append(pred)
                    results['handling'].append('fallback')
                else:
                    results['predictions'].append(None)
                    results['handling'].append('no_fallback')
            
            elif self.strategy == 'uncertain':
                # Return prediction with uncertainty flag
                pred = self.model.predict(sample)[0]
                results['predictions'].append({
                    'prediction': pred,
                    'uncertain': True,
                    'confidence': 'low'
                })
                results['handling'].append('uncertain')
        
        return results
    
    def get_handling_summary(self, results: dict) -> dict:
        """Summarize how inputs were handled."""
        handling = results['handling']
        return {
            'total': len(handling),
            'normal': handling.count('normal'),
            'rejected': handling.count('rejected'),
            'fallback': handling.count('fallback'),
            'uncertain': handling.count('uncertain'),
            'ood_rate': (len(handling) - handling.count('normal')) / len(handling)
        }
```

### OOD Monitoring in Production

```python
"""
Production OOD Monitoring
"""

import time
from collections import deque
from datetime import datetime
import threading

class OODMonitor:
    """
    Continuous OOD monitoring for production systems.
    """
    
    def __init__(self, ood_detector, window_size: int = 1000,
                 alert_threshold: float = 0.1):
        """
        Args:
            ood_detector: Fitted OOD detector
            window_size: Number of recent samples to track
            alert_threshold: Fraction of OOD to trigger alert
        """
        self.detector = ood_detector
        self.window_size = window_size
        self.alert_threshold = alert_threshold
        
        self.recent_scores = deque(maxlen=window_size)
        self.recent_ood = deque(maxlen=window_size)
        self.alerts = []
        self._lock = threading.Lock()
    
    def record_prediction(self, X):
        """Record OOD scores for incoming data."""
        scores = self.detector.get_ood_scores(X)
        is_ood = self.detector.detect_ood(X)
        
        with self._lock:
            for score, ood in zip(scores, is_ood):
                self.recent_scores.append(score)
                self.recent_ood.append(ood)
        
        # Check for alerts
        self._check_alerts()
    
    def _check_alerts(self):
        """Check if OOD rate exceeds threshold."""
        if len(self.recent_ood) < self.window_size // 2:
            return  # Not enough data yet
        
        ood_rate = sum(self.recent_ood) / len(self.recent_ood)
        
        if ood_rate > self.alert_threshold:
            alert = {
                'timestamp': datetime.now().isoformat(),
                'ood_rate': ood_rate,
                'window_size': len(self.recent_ood),
                'message': f'OOD rate {ood_rate:.1%} exceeds threshold {self.alert_threshold:.1%}'
            }
            self.alerts.append(alert)
            self._send_alert(alert)
    
    def _send_alert(self, alert: dict):
        """Send alert (implement actual notification here)."""
        print(f"⚠️ OOD ALERT: {alert['message']}")
    
    def get_current_stats(self) -> dict:
        """Get current OOD statistics."""
        with self._lock:
            if not self.recent_ood:
                return {'status': 'no_data'}
            
            return {
                'window_size': len(self.recent_ood),
                'ood_count': sum(self.recent_ood),
                'ood_rate': sum(self.recent_ood) / len(self.recent_ood),
                'avg_ood_score': np.mean(list(self.recent_scores)),
                'max_ood_score': max(self.recent_scores),
                'alerts_triggered': len(self.alerts)
            }
    
    def generate_report(self) -> str:
        """Generate monitoring report."""
        stats = self.get_current_stats()
        
        report = f"""
OOD MONITORING REPORT
═════════════════════

Current Window Statistics
─────────────────────────
• Samples tracked: {stats.get('window_size', 0)}
• OOD samples: {stats.get('ood_count', 0)}
• OOD rate: {stats.get('ood_rate', 0):.1%}
• Average OOD score: {stats.get('avg_ood_score', 0):.3f}
• Max OOD score: {stats.get('max_ood_score', 0):.3f}

Alerts
──────
• Total alerts triggered: {stats.get('alerts_triggered', 0)}
"""
        
        if self.alerts:
            report += "\nRecent Alerts:\n"
            for alert in self.alerts[-5:]:
                report += f"  • {alert['timestamp']}: {alert['message']}\n"
        
        return report
```

## Code Example

Complete OOD testing workflow:

```python
"""
Complete Out-of-Distribution Testing Suite
"""

import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split

class OODTestSuite:
    """
    Comprehensive OOD testing for ML models.
    """
    
    def __init__(self, model, train_data, train_labels):
        self.model = model
        self.train_data = train_data
        self.train_labels = train_labels
        
        # Initialize detectors
        self.confidence_detector = ConfidenceBasedOODDetector(model)
        self.distance_detector = DistanceBasedOODDetector()
        self.distance_detector.fit(train_data)
        
        # Calibrate thresholds
        self.confidence_detector.calibrate_threshold(train_data)
    
    def test_with_synthetic_ood(self, n_samples: int = 200) -> dict:
        """Test OOD detection with synthetic OOD data."""
        results = {}
        
        # Generate different types of OOD data
        ood_types = {
            'random_noise': self._generate_noise_ood(n_samples),
            'shifted_mean': self._generate_shifted_ood(n_samples),
            'scaled': self._generate_scaled_ood(n_samples),
            'mixed': self._generate_mixed_ood(n_samples),
        }
        
        for ood_name, ood_data in ood_types.items():
            # Test each detector
            conf_detected = self.confidence_detector.detect_ood(ood_data)
            dist_detected = self.distance_detector.detect_ood(ood_data)
            
            results[ood_name] = {
                'confidence_detection_rate': conf_detected.mean(),
                'distance_detection_rate': dist_detected.mean(),
                'combined_detection_rate': (conf_detected | dist_detected).mean()
            }
        
        return results
    
    def _generate_noise_ood(self, n: int) -> np.ndarray:
        """Generate random noise OOD samples."""
        return np.random.randn(n, self.train_data.shape[1])
    
    def _generate_shifted_ood(self, n: int) -> np.ndarray:
        """Generate mean-shifted OOD samples."""
        mean_shift = np.std(self.train_data, axis=0) * 3
        samples = self.train_data[np.random.choice(len(self.train_data), n)]
        return samples + mean_shift
    
    def _generate_scaled_ood(self, n: int) -> np.ndarray:
        """Generate scaled OOD samples."""
        samples = self.train_data[np.random.choice(len(self.train_data), n)]
        return samples * 5
    
    def _generate_mixed_ood(self, n: int) -> np.ndarray:
        """Generate mixed OOD samples."""
        noise = self._generate_noise_ood(n // 3)
        shifted = self._generate_shifted_ood(n // 3)
        scaled = self._generate_scaled_ood(n // 3)
        return np.vstack([noise, shifted, scaled])
    
    def test_in_distribution_baseline(self) -> dict:
        """Test false positive rate on in-distribution data."""
        # Use subset of training data
        test_indices = np.random.choice(
            len(self.train_data), 
            size=min(500, len(self.train_data)),
            replace=False
        )
        id_test = self.train_data[test_indices]
        
        conf_false_pos = self.confidence_detector.detect_ood(id_test).mean()
        dist_false_pos = self.distance_detector.detect_ood(id_test).mean()
        
        return {
            'confidence_false_positive_rate': conf_false_pos,
            'distance_false_positive_rate': dist_false_pos,
            'target_false_positive_rate': 0.05  # Should be ~5%
        }
    
    def test_distribution_shift_detection(self, 
                                          shifted_data: pd.DataFrame) -> dict:
        """Test detection of distribution shift."""
        comparator = DistributionComparator(
            pd.DataFrame(self.train_data)
        )
        
        comparison = comparator.compare(shifted_data)
        
        # Count detected shifts
        drift_count = sum(
            1 for v in comparison.values() 
            if v.get('drift_detected', False)
        )
        
        return {
            'columns_with_drift': drift_count,
            'total_columns': len(comparison),
            'drift_rate': drift_count / len(comparison),
            'details': comparison
        }
    
    def generate_report(self) -> str:
        """Generate comprehensive OOD testing report."""
        synthetic_results = self.test_with_synthetic_ood()
        baseline_results = self.test_in_distribution_baseline()
        
        report = """
╔══════════════════════════════════════════════════════════════════════════╗
║              OUT-OF-DISTRIBUTION TESTING REPORT                           ║
╠══════════════════════════════════════════════════════════════════════════╣

BASELINE (In-Distribution False Positive Rate)
──────────────────────────────────────────────
"""
        report += f"• Confidence detector: {baseline_results['confidence_false_positive_rate']:.1%}\n"
        report += f"• Distance detector: {baseline_results['distance_false_positive_rate']:.1%}\n"
        report += f"• Target: <{baseline_results['target_false_positive_rate']:.0%}\n"
        
        status = "✓ PASS" if (
            baseline_results['confidence_false_positive_rate'] < 0.1 and
            baseline_results['distance_false_positive_rate'] < 0.1
        ) else "✗ FAIL"
        report += f"• Status: {status}\n"
        
        report += """
SYNTHETIC OOD DETECTION
───────────────────────
"""
        for ood_type, results in synthetic_results.items():
            report += f"\n{ood_type}:\n"
            report += f"  • Confidence detector: {results['confidence_detection_rate']:.1%}\n"
            report += f"  • Distance detector: {results['distance_detection_rate']:.1%}\n"
            report += f"  • Combined: {results['combined_detection_rate']:.1%}\n"
        
        # Overall assessment
        avg_detection = np.mean([
            r['combined_detection_rate'] 
            for r in synthetic_results.values()
        ])
        
        report += f"""
OVERALL ASSESSMENT
──────────────────
• Average OOD detection rate: {avg_detection:.1%}
• Rating: {'Good' if avg_detection > 0.8 else 'Needs improvement' if avg_detection > 0.5 else 'Poor'}

RECOMMENDATIONS
───────────────
"""
        if avg_detection < 0.5:
            report += "• Consider using more sophisticated OOD detection methods\n"
            report += "• Ensemble multiple detection approaches\n"
            report += "• Review and potentially expand training data\n"
        elif avg_detection < 0.8:
            report += "• Fine-tune detection thresholds\n"
            report += "• Add monitoring for production OOD rates\n"
        else:
            report += "• OOD detection is performing well\n"
            report += "• Continue monitoring in production\n"
        
        return report


# Usage example
if __name__ == "__main__":
    # Generate sample data
    np.random.seed(42)
    X = np.random.randn(1000, 10)
    y = (X[:, 0] + X[:, 1] > 0).astype(int)
    
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)
    
    # Train model
    model = RandomForestClassifier(random_state=42)
    model.fit(X_train, y_train)
    
    # Run OOD test suite
    suite = OODTestSuite(model, X_train, y_train)
    print(suite.generate_report())
```

## Summary

- **Distribution shift** occurs when production data differs from training data
- **Types of shift:** Covariate shift, label shift, concept drift, domain shift
- **OOD detection methods:** Confidence-based, distance-based, ensemble disagreement
- **Handling strategies:** Reject, fallback, return with uncertainty flag
- **Production monitoring** tracks OOD rates and triggers alerts
- **Testing should include** synthetic OOD data, baseline false positive rates, and real distribution comparisons

## Additional Resources

- [Detecting and Correcting for Label Shift (Paper)](https://arxiv.org/abs/1802.03916)
- [OpenOOD Benchmark](https://github.com/Jingkang50/OpenOOD)
- [Uncertainty Estimation in Deep Learning](https://arxiv.org/abs/2107.03342)

