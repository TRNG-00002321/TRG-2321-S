# Out-of-Distribution Testing

## Learning Objectives

- Understand distribution shift and its impact on AI systems
- Apply OOD detection techniques to identify unusual inputs
- Recognize differences between training and production data
- Handle unknown inputs gracefully
- Monitor for distribution shifts in production
- Design practical OOD testing strategies

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

Your AI model was trained on data from 2020-2022. It's now 2024. Customer behavior has changed, new products exist, and the world looks different. The patterns your model learned may no longer apply.

This is the **distribution shift** problem: when production data differs from training data, model performance degrades—sometimes silently. The model confidently makes predictions, but those predictions are increasingly wrong.

**Real-world examples:**

1. **COVID pandemic:** Models trained on pre-pandemic data suddenly faced radically different customer behavior
2. **Seasonal drift:** A fraud detector trained in winter may struggle with summer shopping patterns
3. **Demographic shift:** A model trained in one country deployed in another faces different user populations

As quality engineers, you need to detect when inputs fall outside what the model was trained on, and handle those cases appropriately.

## The Concept

### What is Distribution Shift?

**Distribution** refers to the statistical properties of your data—what values are common, what patterns exist, how variables relate.

**Distribution shift** occurs when production data has different statistical properties than training data.

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

**Visual analogy:**

Imagine training a model to identify dog breeds using photos from professional photographers. All photos have:
- Good lighting
- Dogs centered in frame
- Clear background

Now deploy it on user-uploaded phone photos:
- Variable lighting (often poor)
- Dogs partially visible, off-center
- Cluttered backgrounds

The model may struggle—not because it doesn't know dogs, but because the photos look different from what it learned on.

### Types of Distribution Shift

**1. Covariate Shift**

The input distribution changes, but the relationship between inputs and outputs stays the same.

Example: Your loan model was trained when most applicants were ages 30-50. Now more applicants are 20-30. The relationship between features and default risk hasn't changed, but you're seeing inputs in regions the model didn't learn well.

**2. Prior Shift (Label Shift)**

The proportion of classes changes, but how each class looks stays the same.

Example: Fraud detector trained when fraud was 1% of transactions. Now fraud is 5%. Same fraud patterns, but the model's threshold may be wrong.

**3. Concept Shift**

The relationship between inputs and outputs changes.

Example: What constitutes "good" customer service may have shifted as expectations evolved. Same inputs, different correct labels.

**4. Domain Shift**

Data comes from an entirely different domain than training.

Example: Medical model trained on data from US hospitals, deployed in rural clinics in India with different equipment and patient populations.

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

### Why OOD Detection Matters

When a model encounters OOD data, several things can happen:

1. **Silent failure:** Model makes confident but wrong predictions
2. **Calibration failure:** Confidence scores become meaningless
3. **Degraded accuracy:** Performance drops compared to test set results

Detection allows you to:
- Flag uncertain predictions for human review
- Trigger alerts that data drift has occurred
- Decide when to retrain models

### OOD Detection Techniques

#### Technique 1: Confidence-Based Detection

**Idea:** OOD samples often have lower prediction confidence. If the model is unsure, the input might be unusual.

```python
def confidence_based_ood(model, input_data, threshold=0.5):
    """
    Detect OOD based on prediction confidence.
    
    Low confidence → possibly OOD
    """
    probabilities = model.predict_proba(input_data)
    max_confidence = max(probabilities)
    
    is_ood = max_confidence < threshold
    
    return {
        "is_ood": is_ood,
        "confidence": max_confidence
    }
```

**Limitation:** Some OOD samples can produce high-confidence wrong predictions. Confidence alone isn't foolproof.

#### Technique 2: Distance-Based Detection

**Idea:** OOD samples are far from training data in feature space.

```python
def distance_based_ood(training_data, new_input, threshold=2.0):
    """
    Detect OOD based on distance from training data.
    
    Far from training data → possibly OOD
    """
    # Calculate mean and std of training data
    train_mean = np.mean(training_data, axis=0)
    train_std = np.std(training_data, axis=0)
    
    # How many standard deviations away is the new input?
    z_scores = np.abs((new_input - train_mean) / train_std)
    max_z = np.max(z_scores)
    
    is_ood = max_z > threshold
    
    return {
        "is_ood": is_ood,
        "max_z_score": max_z
    }
```

**Limitation:** Only works well for simple feature spaces. High-dimensional data may need more sophisticated approaches.

#### Technique 3: Ensemble Disagreement

**Idea:** Train multiple models. If they disagree on a prediction, the input might be OOD.

```python
def ensemble_ood(models, input_data, threshold=0.3):
    """
    Detect OOD based on ensemble disagreement.
    
    High disagreement → possibly OOD
    """
    predictions = [model.predict(input_data) for model in models]
    
    # How much do models disagree?
    unique_preds = set(predictions)
    disagreement = (len(unique_preds) - 1) / (len(models) - 1)
    
    is_ood = disagreement > threshold
    
    return {
        "is_ood": is_ood,
        "disagreement": disagreement,
        "predictions": predictions
    }
```

**Limitation:** Requires maintaining multiple models.

### Comparing Training and Production Data

Monitor for distribution drift by comparing statistics:

```python
def compare_distributions(training_data, production_data):
    """
    Compare training and production data distributions.
    """
    comparison = {}
    
    for column in training_data.columns:
        train_col = training_data[column]
        prod_col = production_data[column]
        
        if train_col.dtype in ['int64', 'float64']:
            # Numeric: compare mean, std
            comparison[column] = {
                "train_mean": train_col.mean(),
                "prod_mean": prod_col.mean(),
                "train_std": train_col.std(),
                "prod_std": prod_col.std(),
                "mean_shift": prod_col.mean() - train_col.mean(),
            }
        else:
            # Categorical: compare value distributions
            train_dist = train_col.value_counts(normalize=True).to_dict()
            prod_dist = prod_col.value_counts(normalize=True).to_dict()
            comparison[column] = {
                "train_distribution": train_dist,
                "prod_distribution": prod_dist
            }
    
    return comparison
```

### Handling OOD Inputs Gracefully

When you detect OOD inputs, options include:

1. **Flag for human review:** Don't make automated decision; route to expert
2. **Return with low confidence:** Make prediction but indicate uncertainty
3. **Reject the input:** Refuse to process and explain why
4. **Fall back to simpler model:** Use more robust (if less accurate) backup

```python
def handle_ood_gracefully(model, ood_detector, input_data):
    """
    Process input with OOD-aware handling.
    """
    ood_result = ood_detector.check(input_data)
    
    if ood_result["is_ood"]:
        return {
            "prediction": None,
            "status": "FLAGGED_FOR_REVIEW",
            "reason": "Input appears unusual",
            "ood_score": ood_result["score"]
        }
    else:
        prediction = model.predict(input_data)
        return {
            "prediction": prediction,
            "status": "AUTOMATED",
            "ood_score": ood_result["score"]
        }
```

### Testing for OOD Robustness

**Test 1: Known OOD data**

Create or collect examples that are clearly different from training:
- Different domain (e.g., if trained on product reviews, test on movie reviews)
- Different time period
- Different demographic

**Test 2: Edge cases**

Test inputs at the boundaries of training distribution:
- Minimum/maximum values for numeric features
- Rare category combinations
- Unusual text patterns

**Test 3: Synthetic OOD**

Artificially create OOD by modifying training samples:
- Add noise beyond training noise
- Combine features in unusual ways
- Extrapolate beyond training ranges

## Code Example

```python
"""
Simple OOD Detection Framework
"""

class OODDetector:
    """Detect out-of-distribution inputs."""
    
    def __init__(self, threshold=0.5):
        self.threshold = threshold
        self.training_stats = None
    
    def fit(self, training_data):
        """Learn distribution from training data."""
        self.training_stats = {
            "means": np.mean(training_data, axis=0),
            "stds": np.std(training_data, axis=0)
        }
    
    def score(self, input_data):
        """Calculate OOD score (higher = more OOD)."""
        z_scores = np.abs(
            (input_data - self.training_stats["means"]) / 
            self.training_stats["stds"]
        )
        return np.max(z_scores)
    
    def is_ood(self, input_data):
        """Check if input is out-of-distribution."""
        score = self.score(input_data)
        return score > self.threshold
    
    def check(self, input_data):
        """Full OOD check with details."""
        score = self.score(input_data)
        return {
            "is_ood": score > self.threshold,
            "score": score,
            "threshold": self.threshold
        }
```

## Summary

- **Distribution shift** occurs when production data differs from training data
- **Types:** Covariate shift, prior shift, concept shift, domain shift
- **OOD detection** identifies inputs that fall outside training distribution
- **Techniques:** Confidence-based, distance-based, ensemble disagreement
- **Monitoring:** Regularly compare training and production data statistics
- **Handling OOD:** Flag for review, reduce confidence, reject, or use fallback

## Additional Resources

- [Failing Loudly: An Empirical Study of Methods for Detecting Dataset Shift](https://arxiv.org/abs/1810.11953) - Research on detecting shift
- [Evidently AI](https://www.evidentlyai.com/) - ML monitoring including drift detection
- [Alibi Detect](https://github.com/SeldonIO/alibi-detect) - Library for detecting outliers and drift
