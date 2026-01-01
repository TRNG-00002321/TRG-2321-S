# Robustness Testing

## Learning Objectives

- Understand what AI robustness means and why it matters
- Recognize adversarial examples and common attack types
- Apply perturbation testing to evaluate model stability
- Implement noise injection techniques for robustness evaluation
- Test input validation and edge case handling
- Use robustness benchmarks to measure model resilience
- Apply defensive strategies to improve robustness

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

A self-driving car AI misclassifies a stop sign because someone put a few stickers on it. A content moderation system fails because an image was slightly rotated. A medical diagnosis AI gives different results when a scan has minimal noise added. These aren't hypothetical scenarios—they're documented failures of AI robustness.

**Robustness** is the ability of an AI system to maintain correct behavior when inputs are slightly perturbed, corrupted, or deliberately manipulated. Without robustness, AI systems become unreliable in the real world where inputs are rarely perfect and adversaries may actively try to fool them.

For quality engineers, robustness testing is essential because traditional accuracy metrics don't capture vulnerability to small perturbations. A model might achieve 99% accuracy on clean test data but fail catastrophically when real-world noise or adversarial inputs are encountered.

## The Concept

### What is AI Robustness?

**Robustness** refers to the stability of a model's predictions when inputs are slightly changed. A robust model produces consistent outputs despite small variations in input.

```
Robustness Illustrated:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  ROBUST MODEL:                                                          │
│  ─────────────                                                          │
│                                                                          │
│  Original Image → "Cat" (98%)                                           │
│  Image + tiny noise → "Cat" (96%)                                       │
│  Image slightly darker → "Cat" (95%)                                    │
│  Image rotated 2° → "Cat" (97%)                                         │
│                                                                          │
│  ✓ Consistent predictions despite variations                            │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  NON-ROBUST MODEL:                                                      │
│  ─────────────────                                                      │
│                                                                          │
│  Original Image → "Cat" (98%)                                           │
│  Image + tiny noise → "Airplane" (75%)    ← Completely wrong!           │
│  Image slightly darker → "Dog" (60%)      ← Changed prediction          │
│  Image rotated 2° → "Cat" (45%)           ← Low confidence             │
│                                                                          │
│  ✗ Predictions flip with minimal changes                                │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Adversarial Examples Explained

**Adversarial examples** are inputs specifically crafted to fool a machine learning model. They often look identical to human eyes but cause dramatic misclassifications.

```
Adversarial Attack Types:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  WHITE-BOX ATTACKS (attacker knows the model)                           │
│  ─────────────────────────────────────────────                          │
│  • FGSM (Fast Gradient Sign Method)                                     │
│  • PGD (Projected Gradient Descent)                                     │
│  • C&W (Carlini & Wagner)                                               │
│  • DeepFool                                                             │
│                                                                          │
│  BLACK-BOX ATTACKS (attacker only sees outputs)                         │
│  ──────────────────────────────────────────────                         │
│  • Transfer attacks (use different model)                               │
│  • Query-based attacks                                                  │
│  • Boundary attacks                                                     │
│                                                                          │
│  PHYSICAL ATTACKS (real-world perturbations)                            │
│  ────────────────────────────────────────────                           │
│  • Adversarial patches                                                  │
│  • 3D-printed objects                                                   │
│  • Lighting manipulation                                                │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```python
"""
Simple adversarial example generation using FGSM
"""
import numpy as np

def fgsm_attack(model, image, label, epsilon=0.01):
    """
    Fast Gradient Sign Method attack.
    
    Args:
        model: Model with gradient computation
        image: Original input image
        label: True label
        epsilon: Perturbation magnitude
    
    Returns:
        Adversarial image
    """
    # Compute gradient of loss with respect to input
    gradient = compute_gradient(model, image, label)
    
    # Create perturbation in direction of gradient sign
    perturbation = epsilon * np.sign(gradient)
    
    # Add perturbation to original image
    adversarial_image = image + perturbation
    
    # Clip to valid range [0, 1]
    adversarial_image = np.clip(adversarial_image, 0, 1)
    
    return adversarial_image
```

### Perturbation Testing

Perturbation testing systematically evaluates how model predictions change under various input modifications.

#### Types of Perturbations

```
Common Perturbation Types:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  FOR IMAGES:                                                            │
│  ───────────                                                            │
│  • Gaussian noise                                                       │
│  • Salt and pepper noise                                                │
│  • Blur (Gaussian, motion)                                              │
│  • Brightness/contrast changes                                          │
│  • Rotation, scaling, translation                                       │
│  • Compression artifacts (JPEG)                                         │
│  • Occlusion (blocking parts)                                           │
│                                                                          │
│  FOR TEXT:                                                              │
│  ──────────                                                             │
│  • Typos (character swaps, deletions)                                   │
│  • Synonym replacement                                                  │
│  • Word insertion/deletion                                              │
│  • Case changes                                                         │
│  • Whitespace manipulation                                              │
│                                                                          │
│  FOR TABULAR DATA:                                                      │
│  ─────────────────                                                      │
│  • Feature noise (±small values)                                        │
│  • Missing values                                                       │
│  • Out-of-range values                                                  │
│  • Type mismatches                                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```python
"""
Perturbation Testing Framework
"""
import numpy as np
from typing import Callable, Dict, List

class PerturbationTester:
    """
    Test model robustness against various perturbations.
    """
    
    def __init__(self, model):
        self.model = model
        self.perturbations = {}
        self.results = []
    
    def register_perturbation(self, name: str, fn: Callable):
        """Register a perturbation function."""
        self.perturbations[name] = fn
    
    def test_sample(self, original: np.ndarray, label: int) -> Dict:
        """Test a single sample against all perturbations."""
        original_pred = self.model.predict([original])[0]
        original_conf = self._get_confidence(original)
        
        results = {
            'original_prediction': original_pred,
            'original_confidence': original_conf,
            'original_correct': original_pred == label,
            'perturbation_results': {}
        }
        
        for name, perturb_fn in self.perturbations.items():
            perturbed = perturb_fn(original)
            perturbed_pred = self.model.predict([perturbed])[0]
            perturbed_conf = self._get_confidence(perturbed)
            
            results['perturbation_results'][name] = {
                'prediction': perturbed_pred,
                'confidence': perturbed_conf,
                'prediction_changed': perturbed_pred != original_pred,
                'still_correct': perturbed_pred == label,
                'confidence_drop': original_conf - perturbed_conf
            }
        
        return results
    
    def _get_confidence(self, sample: np.ndarray) -> float:
        """Get model confidence for prediction."""
        if hasattr(self.model, 'predict_proba'):
            probs = self.model.predict_proba([sample])[0]
            return max(probs)
        return 1.0  # Binary prediction, confidence unknown
    
    def run_test_suite(self, test_data: List, labels: List) -> Dict:
        """Run full perturbation test suite."""
        all_results = []
        
        for sample, label in zip(test_data, labels):
            result = self.test_sample(sample, label)
            all_results.append(result)
        
        # Aggregate results
        summary = {
            'total_samples': len(all_results),
            'original_accuracy': np.mean([r['original_correct'] for r in all_results]),
            'by_perturbation': {}
        }
        
        for perturb_name in self.perturbations.keys():
            perturb_results = [
                r['perturbation_results'][perturb_name] 
                for r in all_results
            ]
            
            summary['by_perturbation'][perturb_name] = {
                'accuracy': np.mean([r['still_correct'] for r in perturb_results]),
                'prediction_flip_rate': np.mean([r['prediction_changed'] for r in perturb_results]),
                'avg_confidence_drop': np.mean([r['confidence_drop'] for r in perturb_results])
            }
        
        return summary


# Common perturbation functions for images
def gaussian_noise(image: np.ndarray, sigma: float = 0.1) -> np.ndarray:
    """Add Gaussian noise to image."""
    noise = np.random.normal(0, sigma, image.shape)
    return np.clip(image + noise, 0, 1)

def salt_pepper_noise(image: np.ndarray, prob: float = 0.01) -> np.ndarray:
    """Add salt and pepper noise."""
    noisy = image.copy()
    salt = np.random.random(image.shape) < prob / 2
    pepper = np.random.random(image.shape) < prob / 2
    noisy[salt] = 1
    noisy[pepper] = 0
    return noisy

def brightness_shift(image: np.ndarray, delta: float = 0.1) -> np.ndarray:
    """Shift brightness."""
    return np.clip(image + delta, 0, 1)

def gaussian_blur(image: np.ndarray, sigma: float = 1.0) -> np.ndarray:
    """Apply Gaussian blur."""
    from scipy.ndimage import gaussian_filter
    return gaussian_filter(image, sigma=sigma)


# Common perturbation functions for text
def typo_injection(text: str, prob: float = 0.05) -> str:
    """Inject random typos."""
    chars = list(text)
    for i in range(len(chars)):
        if np.random.random() < prob and chars[i].isalpha():
            # Random character swap, deletion, or insertion
            operation = np.random.choice(['swap', 'delete', 'insert'])
            if operation == 'swap' and i < len(chars) - 1:
                chars[i], chars[i+1] = chars[i+1], chars[i]
            elif operation == 'delete':
                chars[i] = ''
            else:
                chars.insert(i, np.random.choice('abcdefghijklmnopqrstuvwxyz'))
    return ''.join(chars)

def case_perturbation(text: str) -> str:
    """Randomly change case."""
    return ''.join(
        c.swapcase() if np.random.random() < 0.1 else c 
        for c in text
    )
```

### Model Stability Testing

Test whether small input changes cause disproportionate output changes:

```python
def test_model_stability(model, samples: List, perturbation_fn: Callable,
                          n_perturbations: int = 10,
                          epsilon: float = 0.01) -> Dict:
    """
    Test model stability under multiple small perturbations.
    
    Args:
        model: Model to test
        samples: Test samples
        perturbation_fn: Function to perturb samples
        n_perturbations: Number of perturbations per sample
        epsilon: Perturbation magnitude
    
    Returns:
        Stability metrics
    """
    results = {
        'samples': [],
        'overall_stability': 0
    }
    
    for sample in samples:
        original_pred = model.predict([sample])[0]
        original_proba = model.predict_proba([sample])[0] if hasattr(model, 'predict_proba') else None
        
        perturbation_preds = []
        perturbation_probas = []
        
        for _ in range(n_perturbations):
            perturbed = perturbation_fn(sample, epsilon)
            pred = model.predict([perturbed])[0]
            perturbation_preds.append(pred)
            
            if original_proba is not None:
                proba = model.predict_proba([perturbed])[0]
                perturbation_probas.append(proba)
        
        # Calculate stability metrics
        prediction_stability = np.mean([p == original_pred for p in perturbation_preds])
        
        if perturbation_probas:
            proba_variance = np.mean([np.max(np.abs(p - original_proba)) for p in perturbation_probas])
        else:
            proba_variance = None
        
        results['samples'].append({
            'original_prediction': original_pred,
            'prediction_stability': prediction_stability,
            'probability_variance': proba_variance,
            'unique_predictions': len(set(perturbation_preds))
        })
    
    results['overall_stability'] = np.mean([s['prediction_stability'] for s in results['samples']])
    
    return results
```

### Input Validation Robustness

Test how models handle invalid or unexpected inputs:

```python
"""
Input Validation Robustness Tests
"""

class InputValidationTester:
    """
    Test model robustness to invalid inputs.
    """
    
    def __init__(self, model, expected_input_shape, expected_dtype):
        self.model = model
        self.expected_shape = expected_input_shape
        self.expected_dtype = expected_dtype
    
    def test_null_input(self):
        """Test with null/None input."""
        try:
            self.model.predict(None)
            return {'passed': False, 'error': 'Model accepted None input'}
        except (TypeError, ValueError) as e:
            return {'passed': True, 'error': str(e)}
    
    def test_empty_input(self):
        """Test with empty input."""
        try:
            self.model.predict([])
            return {'passed': False, 'error': 'Model accepted empty input'}
        except (IndexError, ValueError) as e:
            return {'passed': True, 'error': str(e)}
    
    def test_wrong_shape(self):
        """Test with incorrectly shaped input."""
        wrong_shape = tuple(dim + 1 for dim in self.expected_shape)
        wrong_input = np.zeros(wrong_shape)
        
        try:
            self.model.predict([wrong_input])
            return {'passed': False, 'error': 'Model accepted wrong shape'}
        except (ValueError, RuntimeError) as e:
            return {'passed': True, 'error': str(e)}
    
    def test_extreme_values(self):
        """Test with extreme values."""
        results = {}
        
        # Very large values
        large_input = np.full(self.expected_shape, 1e10)
        try:
            pred = self.model.predict([large_input])
            results['large_values'] = {
                'accepted': True,
                'prediction': str(pred),
                'reasonable': not np.isnan(pred).any()
            }
        except Exception as e:
            results['large_values'] = {'accepted': False, 'error': str(e)}
        
        # Very small values
        small_input = np.full(self.expected_shape, -1e10)
        try:
            pred = self.model.predict([small_input])
            results['small_values'] = {
                'accepted': True,
                'prediction': str(pred),
                'reasonable': not np.isnan(pred).any()
            }
        except Exception as e:
            results['small_values'] = {'accepted': False, 'error': str(e)}
        
        # NaN values
        nan_input = np.full(self.expected_shape, np.nan)
        try:
            pred = self.model.predict([nan_input])
            results['nan_values'] = {
                'accepted': True,
                'prediction': str(pred),
                'reasonable': False  # Shouldn't accept NaN
            }
        except Exception as e:
            results['nan_values'] = {'accepted': False, 'error': str(e)}
        
        return results
    
    def test_wrong_dtype(self):
        """Test with wrong data type."""
        results = {}
        
        # String input when numeric expected
        if self.expected_dtype in [np.float32, np.float64, np.int32]:
            string_input = np.array(['text'] * np.prod(self.expected_shape)).reshape(self.expected_shape)
            try:
                self.model.predict([string_input])
                results['string_dtype'] = {'accepted': True, 'expected': False}
            except (TypeError, ValueError) as e:
                results['string_dtype'] = {'accepted': False, 'error': str(e)}
        
        return results
    
    def run_all_tests(self) -> Dict:
        """Run all input validation tests."""
        return {
            'null_input': self.test_null_input(),
            'empty_input': self.test_empty_input(),
            'wrong_shape': self.test_wrong_shape(),
            'extreme_values': self.test_extreme_values(),
            'wrong_dtype': self.test_wrong_dtype()
        }
```

### Robustness Benchmarks

Standard benchmarks for measuring robustness:

```
Common Robustness Benchmarks:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  IMAGE CLASSIFICATION:                                                  │
│  ─────────────────────                                                  │
│  • ImageNet-C: 15 corruption types at 5 severity levels                 │
│  • ImageNet-P: Perturbation sequences                                   │
│  • ImageNet-A: Natural adversarial examples                             │
│  • CIFAR-10-C/CIFAR-100-C: Corrupted CIFAR variants                     │
│                                                                          │
│  NLP:                                                                   │
│  ────                                                                   │
│  • TextFlint: Comprehensive text transformation benchmark               │
│  • CheckList: Behavioral testing for NLP                                │
│  • AdvGLUE: Adversarial GLUE benchmark                                  │
│                                                                          │
│  METRICS:                                                               │
│  ────────                                                               │
│  • Clean Accuracy: Accuracy on unperturbed data                         │
│  • Robust Accuracy: Accuracy under adversarial attack                   │
│  • mCE (mean Corruption Error): Average error across corruptions        │
│  • Flip Rate: How often predictions change under perturbation           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```python
def calculate_robustness_metrics(clean_results: Dict, 
                                  perturbed_results: Dict) -> Dict:
    """
    Calculate standard robustness metrics.
    """
    metrics = {
        'clean_accuracy': clean_results['accuracy'],
        'robust_accuracy': perturbed_results['accuracy'],
        'accuracy_drop': clean_results['accuracy'] - perturbed_results['accuracy'],
        'flip_rate': perturbed_results.get('prediction_flip_rate', 0),
        'robustness_gap': clean_results['accuracy'] - perturbed_results['accuracy'],
    }
    
    # Relative robustness
    if clean_results['accuracy'] > 0:
        metrics['relative_robustness'] = perturbed_results['accuracy'] / clean_results['accuracy']
    else:
        metrics['relative_robustness'] = 0
    
    return metrics
```

### Defensive Strategies

```
Improving Model Robustness:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  TRAINING-TIME DEFENSES:                                                │
│  ───────────────────────                                                │
│  • Adversarial training: Include adversarial examples in training       │
│  • Data augmentation: Train with various perturbations                  │
│  • Noise injection: Add noise during training                           │
│  • Certified defenses: Provable robustness bounds                       │
│                                                                          │
│  INFERENCE-TIME DEFENSES:                                               │
│  ────────────────────────                                               │
│  • Input preprocessing: Denoise, normalize inputs                       │
│  • Ensemble methods: Use multiple models                                │
│  • Gradient masking: Make gradients uninformative                       │
│  • Input validation: Reject suspicious inputs                           │
│                                                                          │
│  ARCHITECTURAL CHOICES:                                                 │
│  ──────────────────────                                                 │
│  • Smaller models (often more robust)                                   │
│  • Skip connections                                                     │
│  • Attention mechanisms                                                 │
│  • Lipschitz constraints                                                │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

## Code Example

Complete robustness testing workflow:

```python
"""
Complete Robustness Testing Framework
"""

import numpy as np
from typing import Dict, List, Callable
from dataclasses import dataclass

@dataclass
class RobustnessReport:
    clean_accuracy: float
    robust_accuracy: float
    accuracy_drop: float
    flip_rate: float
    by_perturbation: Dict
    worst_case_perturbation: str
    overall_score: float

class RobustnessTester:
    """
    Comprehensive robustness testing for ML models.
    """
    
    def __init__(self, model):
        self.model = model
        self.perturbations = {}
    
    def add_perturbation(self, name: str, fn: Callable, severity: str = 'medium'):
        """Add a perturbation type to test."""
        self.perturbations[name] = {'fn': fn, 'severity': severity}
    
    def run_robustness_audit(self, X_test, y_test) -> RobustnessReport:
        """
        Run complete robustness audit.
        """
        # Clean accuracy
        clean_preds = self.model.predict(X_test)
        clean_accuracy = np.mean(clean_preds == y_test)
        
        # Test each perturbation
        perturb_results = {}
        all_robust_accuracies = []
        all_flip_rates = []
        
        for name, config in self.perturbations.items():
            perturb_fn = config['fn']
            
            # Apply perturbation to all test samples
            X_perturbed = np.array([perturb_fn(x) for x in X_test])
            perturbed_preds = self.model.predict(X_perturbed)
            
            # Calculate metrics
            robust_acc = np.mean(perturbed_preds == y_test)
            flip_rate = np.mean(perturbed_preds != clean_preds)
            
            perturb_results[name] = {
                'robust_accuracy': robust_acc,
                'accuracy_drop': clean_accuracy - robust_acc,
                'flip_rate': flip_rate,
                'severity': config['severity']
            }
            
            all_robust_accuracies.append(robust_acc)
            all_flip_rates.append(flip_rate)
        
        # Find worst case
        worst_case = min(perturb_results.items(), 
                        key=lambda x: x[1]['robust_accuracy'])
        
        # Overall robustness score (0-1, higher is better)
        overall_score = np.mean(all_robust_accuracies) / clean_accuracy if clean_accuracy > 0 else 0
        
        return RobustnessReport(
            clean_accuracy=clean_accuracy,
            robust_accuracy=np.mean(all_robust_accuracies),
            accuracy_drop=clean_accuracy - np.mean(all_robust_accuracies),
            flip_rate=np.mean(all_flip_rates),
            by_perturbation=perturb_results,
            worst_case_perturbation=worst_case[0],
            overall_score=overall_score
        )
    
    def generate_report(self, report: RobustnessReport) -> str:
        """Generate human-readable report."""
        output = f"""
╔══════════════════════════════════════════════════════════════════════════╗
║                      ROBUSTNESS TESTING REPORT                            ║
╠══════════════════════════════════════════════════════════════════════════╣

SUMMARY
───────
• Clean Accuracy: {report.clean_accuracy:.1%}
• Robust Accuracy (avg): {report.robust_accuracy:.1%}
• Accuracy Drop: {report.accuracy_drop:.1%}
• Flip Rate: {report.flip_rate:.1%}
• Overall Robustness Score: {report.overall_score:.2f}/1.00

WORST CASE
──────────
• Perturbation: {report.worst_case_perturbation}
• Accuracy: {report.by_perturbation[report.worst_case_perturbation]['robust_accuracy']:.1%}

RESULTS BY PERTURBATION
───────────────────────
"""
        for name, results in sorted(report.by_perturbation.items(),
                                   key=lambda x: x[1]['robust_accuracy']):
            status = "✓" if results['accuracy_drop'] < 0.1 else "⚠" if results['accuracy_drop'] < 0.2 else "✗"
            output += f"""
  {status} {name} [{results['severity']}]
    Accuracy: {results['robust_accuracy']:.1%} (drop: {results['accuracy_drop']:+.1%})
    Flip Rate: {results['flip_rate']:.1%}
"""
        
        # Recommendations
        output += """
RECOMMENDATIONS
───────────────
"""
        if report.overall_score < 0.8:
            output += "• Model shows significant robustness issues\n"
            output += f"• Prioritize defense against: {report.worst_case_perturbation}\n"
            output += "• Consider adversarial training or data augmentation\n"
        else:
            output += "• Model demonstrates reasonable robustness\n"
            output += "• Continue monitoring under new perturbation types\n"
        
        return output


# Example usage
if __name__ == "__main__":
    from sklearn.ensemble import RandomForestClassifier
    from sklearn.datasets import make_classification
    
    # Create sample data and model
    X, y = make_classification(n_samples=1000, n_features=20, random_state=42)
    X_train, X_test = X[:800], X[800:]
    y_train, y_test = y[:800], y[800:]
    
    model = RandomForestClassifier(random_state=42)
    model.fit(X_train, y_train)
    
    # Set up robustness tester
    tester = RobustnessTester(model)
    
    # Add perturbation types
    tester.add_perturbation(
        'gaussian_noise_small',
        lambda x: x + np.random.normal(0, 0.1, x.shape),
        severity='low'
    )
    tester.add_perturbation(
        'gaussian_noise_medium',
        lambda x: x + np.random.normal(0, 0.3, x.shape),
        severity='medium'
    )
    tester.add_perturbation(
        'gaussian_noise_large',
        lambda x: x + np.random.normal(0, 0.5, x.shape),
        severity='high'
    )
    tester.add_perturbation(
        'feature_dropout',
        lambda x: x * (np.random.random(x.shape) > 0.1),
        severity='medium'
    )
    
    # Run audit
    report = tester.run_robustness_audit(X_test, y_test)
    print(tester.generate_report(report))
```

## Summary

- **Robustness** measures model stability under input perturbations
- **Adversarial examples** are crafted inputs that fool models despite looking normal
- **Perturbation testing** systematically evaluates response to common corruptions
- **Key metrics:** Robust accuracy, flip rate, accuracy drop, relative robustness
- **Input validation** ensures models handle edge cases gracefully
- **Standard benchmarks** (ImageNet-C, TextFlint) provide comparable metrics
- **Defensive strategies** include adversarial training, data augmentation, and input preprocessing

## Additional Resources

- [Adversarial Robustness Toolbox (IBM)](https://github.com/Trusted-AI/adversarial-robustness-toolbox)
- [CleverHans - Adversarial Examples Library](https://github.com/cleverhans-lab/cleverhans)
- [RobustBench - Adversarial Robustness Benchmark](https://robustbench.github.io/)

