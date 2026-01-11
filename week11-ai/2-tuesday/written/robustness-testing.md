# Robustness Testing

## Learning Objectives

- Understand what AI robustness means and why it matters
- Recognize adversarial examples and common attack types
- Apply perturbation testing to evaluate model stability
- Design input validation for robustness
- Measure model sensitivity to small changes
- Implement practical robustness testing approaches

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

In 2018, researchers showed that putting a few small stickers on a stop sign could make a self-driving car's AI see it as a speed limit sign. The stickers were imperceptible to humans but catastrophic for the AI.

This is the robustness problem: AI models can fail in unexpected ways when inputs are slightly different from what they saw during training. A human would barely notice the change, but the model's prediction flips entirely.

**Robustness** is the ability of an AI system to maintain correct behavior when inputs are slightly perturbed, corrupted, or deliberately manipulated. Without robustness, AI systems become unreliable in the real world where inputs are rarely perfect and adversaries may actively try to fool them.

**Why does this matter?**

1. **Safety-critical applications:** Medical diagnosis, autonomous vehicles, security systems—where wrong predictions cause real harm
2. **Adversarial environments:** Attackers actively try to fool your models (spam filters, fraud detection, content moderation)
3. **Real-world variability:** Production data naturally varies more than training data

As a quality engineer, you need to test whether your AI handles the messy, unpredictable real world—not just clean lab conditions.

## The Concept

### What is AI Robustness?

**Robustness** measures how stable a model's predictions are under input variations. A robust model gives consistent predictions despite minor changes to inputs that shouldn't affect the outcome.

**Robust vs. Brittle Models:**

A robust image classifier:
- Image → "Cat" (95% confidence)
- Image + tiny noise → "Cat" (93% confidence)
- Image slightly darker → "Cat" (94% confidence)
- Image rotated 2° → "Cat" (95% confidence)

All reasonable, consistent predictions.

A brittle image classifier:
- Image → "Cat" (95% confidence)
- Image + tiny noise → "Dog" (87% confidence)
- Image slightly darker → "Airplane" (72% confidence)
- Image rotated 2° → "Cat" (95% confidence)

Predictions flip wildly with minimal changes—this is dangerous.

### Types of Robustness

#### 1. Natural Robustness
Resilience to variations that occur naturally in production data.

Examples:
- Lighting changes in images
- Background noise in audio
- Typos in text
- Slightly different phrasing of the same question

#### 2. Adversarial Robustness
Resilience to deliberately crafted inputs designed to fool the model.

Examples:
- Carefully calculated perturbations invisible to humans
- Adversarial patches placed on objects
- Carefully crafted prompt injections

#### 3. Distribution Robustness
Resilience to inputs from slightly different distributions than training.

Examples:
- Model trained on professional photos, deployed on phone photos
- Model trained on modern text, seeing archaic spellings
- Model trained on US data, deployed in UK

### Adversarial Examples

**Adversarial examples** are inputs designed to fool AI models. They often look identical to humans but cause dramatically wrong predictions.

**Classic example:** A panda image that the model correctly identifies as "panda" with 57% confidence. Add a small, invisible perturbation—it now says "gibbon" with 99% confidence.

**Why do adversarial examples exist?**

Neural networks find decision boundaries in high-dimensional space. Small movements along those boundaries (which look like noise to us) can cross into different classification regions. The model isn't seeing the same things humans see.

**Common Attack Types:**

| Attack Type | Description | Access Required |
|-------------|-------------|-----------------|
| **FGSM** (Fast Gradient Sign Method) | Uses model gradients to find most damaging perturbation | Full model access (white-box) |
| **PGD** (Projected Gradient Descent) | Iterative version of FGSM, stronger attack | Full model access |
| **C&W** (Carlini & Wagner) | Optimization-based, finds minimal perturbation | Full model access |
| **Boundary Attack** | Starts from wrong class, walks toward target | Only outputs needed (black-box) |
| **Query Attack** | Uses many queries to estimate gradients | Only outputs needed |

### Perturbation Testing

Perturbation testing applies various transformations to inputs and checks if predictions remain stable.

**For Images:**
- Gaussian noise (random static)
- Brightness/contrast changes
- Blur
- Rotation/translation
- Cropping
- Compression artifacts

**For Text:**
- Typos and misspellings
- Case changes (CAPS vs lowercase)
- Extra/missing whitespace
- Synonym substitution
- Character swaps

**For Tabular Data:**
- Small numerical changes (±1%)
- Rounding
- Missing values
- Out-of-range values

**Simple perturbation testing approach:**

```python
def test_perturbation_stability(model, original_input, perturbation_fn, n_tries=10):
    """
    Test if model predictions are stable under perturbations.
    
    Args:
        model: The model to test
        original_input: Clean input
        perturbation_fn: Function that adds noise/changes
        n_tries: Number of perturbed versions to try
    
    Returns:
        Stability analysis
    """
    original_pred = model.predict(original_input)
    perturbed_preds = []
    
    for _ in range(n_tries):
        perturbed = perturbation_fn(original_input)
        perturbed_preds.append(model.predict(perturbed))
    
    # How many times did prediction change?
    changes = sum(1 for p in perturbed_preds if p != original_pred)
    
    return {
        "original_prediction": original_pred,
        "perturbed_predictions": perturbed_preds,
        "change_rate": changes / n_tries,
        "is_stable": changes == 0
    }
```

### Common Perturbation Functions

For images:
```python
import numpy as np

def add_gaussian_noise(image, strength=0.1):
    """Add random noise to image."""
    noise = np.random.normal(0, strength, image.shape)
    noisy = image + noise
    return np.clip(noisy, 0, 1)  # Keep in valid range

def adjust_brightness(image, factor=1.2):
    """Make image brighter or darker."""
    return np.clip(image * factor, 0, 1)
```

For text:
```python
import random

def add_typos(text, prob=0.05):
    """Add random typos to text."""
    chars = list(text)
    for i in range(len(chars)):
        if random.random() < prob and chars[i].isalpha():
            # Swap with neighbor or random char
            chars[i] = random.choice('abcdefghijklmnopqrstuvwxyz')
    return ''.join(chars)

def random_case(text):
    """Randomize capitalization."""
    return ''.join(
        c.upper() if random.random() < 0.3 else c.lower() 
        for c in text
    )
```

### Measuring Robustness

Key metrics for robustness testing:

**1. Flip Rate:** What percentage of predictions change under perturbation?

Lower is better. A flip rate of 0% means perfect stability. A flip rate of 30% means 30% of perturbed inputs get different predictions.

**2. Confidence Drop:** How much does confidence decrease under perturbation?

Even if the prediction doesn't flip, confidence dropping from 99% to 51% suggests fragility.

**3. Perturbation Budget:** How much perturbation is needed to flip predictions?

If predictions flip with tiny changes (ε < 0.01), the model is very fragile. If it takes large changes (ε > 0.1) that are visible to humans, that's more acceptable.

### Input Validation for Robustness

One defense: detect and reject inputs that are likely out-of-distribution or adversarial.

```python
def validate_input(model, input_data, confidence_threshold=0.5):
    """
    Check if input seems valid for model.
    
    Rejects inputs where model seems uncertain or suspicious.
    """
    prediction, confidence = model.predict_with_confidence(input_data)
    
    # Reject low-confidence predictions
    if confidence < confidence_threshold:
        return {
            "valid": False,
            "reason": "Low confidence",
            "confidence": confidence
        }
    
    # Reject if prediction is unstable
    perturbed = add_small_noise(input_data)
    perturbed_pred, _ = model.predict_with_confidence(perturbed)
    
    if perturbed_pred != prediction:
        return {
            "valid": False,
            "reason": "Unstable prediction",
            "original": prediction,
            "perturbed": perturbed_pred
        }
    
    return {"valid": True, "prediction": prediction, "confidence": confidence}
```

### Practical Robustness Testing Strategy

1. **Define realistic perturbations** for your domain
   - What natural variations occur in production?
   - What malicious changes might attackers try?

2. **Create perturbation test suite**
   - Apply each perturbation type to test samples
   - Measure flip rates and confidence changes

3. **Set thresholds**
   - What flip rate is acceptable?
   - What minimum confidence is required?

4. **Report by perturbation type**
   - Some perturbations may be more problematic than others
   - Focus remediation on highest-impact gaps

## Code Example

```python
"""
Simple Robustness Tester
"""

class RobustnessTester:
    """Test model robustness against various perturbations."""
    
    def __init__(self, model):
        self.model = model
        self.perturbations = {}
        
    def add_perturbation(self, name, func):
        """Register a perturbation to test."""
        self.perturbations[name] = func
        
    def test_single(self, original, n_trials=10):
        """Test robustness on single input."""
        original_pred = self.model.predict(original)
        results = {}
        
        for name, perturb in self.perturbations.items():
            flips = 0
            for _ in range(n_trials):
                perturbed = perturb(original)
                if self.model.predict(perturbed) != original_pred:
                    flips += 1
            results[name] = flips / n_trials
            
        return {"original": original_pred, "flip_rates": results}
    
    def test_batch(self, test_data):
        """Test robustness on multiple inputs."""
        all_results = []
        for sample in test_data:
            all_results.append(self.test_single(sample))
        
        # Average flip rates across all samples
        avg_rates = {}
        for name in self.perturbations:
            rates = [r["flip_rates"][name] for r in all_results]
            avg_rates[name] = sum(rates) / len(rates)
        
        return {
            "samples_tested": len(test_data),
            "average_flip_rates": avg_rates
        }
```

## Summary

- **Robustness** measures how stable predictions are under input variations
- **Adversarial examples** are inputs crafted to fool models—often invisible to humans
- **Perturbation testing** applies realistic changes and measures prediction stability
- **Key metrics:** Flip rate, confidence drop, perturbation budget
- **Input validation** can reject suspicious or unstable predictions
- **Testing strategy:** Define realistic perturbations, measure stability, set acceptable thresholds

## Additional Resources

- [CleverHans (Adversarial Examples Library)](https://github.com/cleverhans-lab/cleverhans) - Tools for testing adversarial robustness
- [Adversarial Robustness Toolbox (IBM)](https://github.com/Trusted-AI/adversarial-robustness-toolbox) - Comprehensive attack and defense library
- [Robustness (MIT)](https://github.com/MadryLab/robustness) - Tools from the Madry lab
