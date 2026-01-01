# AI Testing Fundamentals

## Learning Objectives

- Understand the unique challenges of testing AI and machine learning systems
- Recognize the differences between AI testing and traditional software testing
- Apply testing strategies appropriate for non-deterministic behavior
- Design test oracles for AI systems
- Implement continuous testing practices for AI/ML models

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

AI systems are fundamentally different from traditional software. When you test a calculator, 2+2 should always equal 4. When you test an AI model, the same input might produce slightly different outputs, "correct" can be subjective, and a model that worked perfectly yesterday might fail today due to data drift.

As AI permeates every industry—healthcare diagnostics, financial decisions, autonomous vehicles, hiring systems—the stakes of getting AI testing wrong have never been higher. A bug in a calculator is an inconvenience. A bug in a medical diagnostic AI could cost lives. Quality engineers who understand AI testing are positioned at the forefront of a critical emerging discipline.

This module establishes the foundation for testing AI systems, equipping you with mental models, vocabulary, and strategies to ensure AI systems meet the rigorous quality standards they require.

## The Concept

### What Makes AI Systems Different?

Traditional software follows deterministic rules: given input X, always produce output Y. AI systems are fundamentally different:

```
Traditional Software:                AI/ML Systems:
┌────────────────────────┐          ┌────────────────────────┐
│ Input: 2 + 2           │          │ Input: Image           │
│         │              │          │         │              │
│         ▼              │          │         ▼              │
│ ┌──────────────────┐   │          │ ┌──────────────────┐   │
│ │ if (a + b)       │   │          │ │ Neural Network   │   │
│ │   return sum     │   │          │ │ (Billions of     │   │
│ └──────────────────┘   │          │ │  parameters)     │   │
│         │              │          │ └──────────────────┘   │
│         ▼              │          │         │              │
│ Output: 4 (always)     │          │         ▼              │
│                        │          │ Output: "Cat" (95.2%)  │
│ • Deterministic        │          │                        │
│ • Predictable          │          │ • Probabilistic        │
│ • Rule-based           │          │ • Confidence levels    │
└────────────────────────┘          │ • Learned patterns     │
                                    └────────────────────────┘
```

### Key Differences in AI Testing

| Aspect | Traditional Testing | AI/ML Testing |
|--------|---------------------|---------------|
| **Expected Output** | Exact match required | Range or distribution acceptable |
| **Test Oracle** | Specification defines correctness | Often no single "right" answer |
| **Reproducibility** | Identical runs produce identical results | May vary due to randomness |
| **Coverage** | Code paths, branches | Data distributions, edge cases |
| **Root Cause** | Trace through logic | Model behavior may be unexplainable |
| **Regression** | Output changed = bug | Performance degradation over time |
| **Test Data** | Can be synthetic | Must represent real distribution |

### Unique Challenges of Testing AI Systems

#### Challenge 1: Non-Deterministic Behavior

```python
# Traditional function - always same output
def add(a, b):
    return a + b

assert add(2, 2) == 4  # Always passes

# AI model - may vary slightly
def predict_sentiment(text, model):
    return model.predict(text)  # Returns probability

# This assertion is problematic:
# assert predict_sentiment("Great product!") == 0.95
# Might return 0.947, 0.952, 0.949 across runs

# Better approach - test within tolerance:
result = predict_sentiment("Great product!", model)
assert 0.90 <= result <= 1.0  # Positive sentiment expected
assert result > predict_sentiment("Terrible product!", model)  # Relative test
```

**Testing Strategies for Non-Determinism:**
- Use tolerance ranges instead of exact values
- Test relative behaviors (A should score higher than B)
- Set random seeds for reproducibility in testing
- Run multiple times and check statistical properties

#### Challenge 2: The Test Oracle Problem

In traditional testing, the specification tells you what's correct. In AI, defining "correct" is often challenging:

```
Oracle Problem Examples:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  TASK                   ORACLE CHALLENGE                                │
│  ────────────────       ──────────────────────────────────────────     │
│  Image captioning       Many valid captions exist for one image         │
│  Translation            Multiple correct translations possible          │
│  Art generation         What makes "good" art?                          │
│  Recommendation         User preferences are subjective                 │
│  Medical diagnosis      Even experts disagree on edge cases             │
│  Content moderation     Cultural context affects judgment               │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

**Approaches to the Oracle Problem:**
- Use human evaluation (with multiple evaluators)
- Compare to baseline model performance
- Test against known ground-truth datasets
- Measure properties instead of exact outputs (fluency, coherence)

#### Challenge 3: Data Dependency

AI models are only as good as their training data:

```
Data-Related Testing Concerns:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  1. TRAINING DATA QUALITY                                               │
│     └─ Bad data → Bad model → Bad predictions                           │
│                                                                          │
│  2. TRAIN-TEST DISTRIBUTION MISMATCH                                    │
│     └─ Trained on dataset A, deployed on different distribution B       │
│                                                                          │
│  3. DATA DRIFT                                                          │
│     └─ Production data changes over time; model becomes stale           │
│                                                                          │
│  4. LABEL QUALITY                                                       │
│     └─ Incorrect labels in training data propagate to model             │
│                                                                          │
│  5. DATA LEAKAGE                                                        │
│     └─ Test data accidentally included in training → false accuracy     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Challenge 4: Emergent Behavior

AI systems can exhibit unexpected behaviors not explicitly programmed:

```markdown
Example: A model trained to play a video game discovers an exploit the 
developers didn't anticipate. It "wins" by finding a bug in the game 
rather than playing skillfully.

Testing Implication: You can't just test for what you expect. You must 
also probe for unexpected behaviors through adversarial testing.
```

### AI Testing Lifecycle

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        AI TESTING LIFECYCLE                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  1. DATA TESTING                                                        │
│     ├─ Data quality validation                                          │
│     ├─ Distribution analysis                                            │
│     ├─ Bias detection in training data                                  │
│     └─ Data pipeline testing                                            │
│              │                                                           │
│              ▼                                                           │
│  2. MODEL TESTING                                                       │
│     ├─ Unit tests for model components                                  │
│     ├─ Accuracy/performance metrics                                     │
│     ├─ Robustness testing                                               │
│     └─ Fairness evaluation                                              │
│              │                                                           │
│              ▼                                                           │
│  3. INTEGRATION TESTING                                                 │
│     ├─ Model + application integration                                  │
│     ├─ API contract testing                                             │
│     ├─ End-to-end flows                                                 │
│     └─ Performance under load                                           │
│              │                                                           │
│              ▼                                                           │
│  4. PRODUCTION TESTING                                                  │
│     ├─ Shadow mode/canary deployment                                    │
│     ├─ A/B testing                                                      │
│     ├─ Monitoring and alerting                                          │
│     └─ Continuous evaluation                                            │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Testing Strategies for ML Models

#### Strategy 1: Behavioral Testing (CheckList Approach)

Test model behaviors across categories:

```python
"""
Behavioral Testing Framework for NLP Models
Based on the CheckList methodology
"""

class BehavioralTests:
    """
    Test categories for NLP model behavior.
    """
    
    def test_minimum_functionality(self, model):
        """MFT: Test basic capabilities."""
        test_cases = [
            # Simple cases the model MUST get right
            ("I love this product!", "positive"),
            ("This is terrible.", "negative"),
            ("The weather is okay.", "neutral"),
        ]
        
        for text, expected in test_cases:
            prediction = model.predict(text)
            assert prediction == expected, f"MFT failed: {text}"
    
    def test_invariance(self, model):
        """INV: Output shouldn't change for these modifications."""
        base_text = "The movie was fantastic!"
        base_pred = model.predict(base_text)
        
        # Adding typos shouldn't flip sentiment
        variants = [
            "The moive was fantastic!",  # typo
            "The movie was fantastic!!",  # extra punctuation
            "THE MOVIE WAS FANTASTIC!",  # caps
        ]
        
        for variant in variants:
            assert model.predict(variant) == base_pred, \
                f"Invariance failed: {variant}"
    
    def test_directional_expectation(self, model):
        """DIR: Certain changes should affect output predictably."""
        # Adding "not" should flip sentiment
        positive = "I liked the movie."
        negative = "I did not like the movie."
        
        pos_score = model.predict_proba(positive)[1]  # positive probability
        neg_score = model.predict_proba(negative)[1]
        
        assert pos_score > neg_score, \
            "Adding 'not' should decrease positive sentiment"
```

#### Strategy 2: Metamorphic Testing

When you can't verify the exact output, verify relationships between inputs and outputs:

```python
"""
Metamorphic Testing for AI Models
Test relationships rather than exact outputs
"""

class MetamorphicTests:
    
    def test_symmetry(self, translation_model):
        """
        If we translate A→B→A, result should be similar to original.
        """
        original = "Hello, how are you?"
        
        translated = translation_model.translate(original, "en", "es")
        back_translated = translation_model.translate(translated, "es", "en")
        
        similarity = compute_similarity(original, back_translated)
        assert similarity > 0.8, "Round-trip translation diverged too much"
    
    def test_additive_perturbation(self, image_classifier):
        """
        Small changes to input should cause small changes to output.
        """
        image = load_test_image()
        base_confidence = image_classifier.predict(image)["cat"]
        
        # Add small noise
        noisy_image = add_gaussian_noise(image, sigma=0.01)
        noisy_confidence = image_classifier.predict(noisy_image)["cat"]
        
        confidence_change = abs(base_confidence - noisy_confidence)
        assert confidence_change < 0.05, \
            "Model too sensitive to small perturbations"
    
    def test_monotonicity(self, risk_model):
        """
        Higher risk factors should increase risk score.
        """
        base_input = {"age": 40, "income": 50000, "debt": 10000}
        base_score = risk_model.predict(base_input)
        
        # Increasing debt should increase risk
        high_debt_input = {**base_input, "debt": 50000}
        high_debt_score = risk_model.predict(high_debt_input)
        
        assert high_debt_score > base_score, \
            "Higher debt should mean higher risk"
```

#### Strategy 3: Property-Based Testing

Test that outputs always satisfy certain properties:

```python
"""
Property-Based Testing for ML Models
Verify properties hold across random inputs
"""
from hypothesis import given, strategies as st

class PropertyTests:
    
    @given(st.floats(min_value=0, max_value=1))
    def test_probability_bounds(self, input_value, model):
        """All probability outputs must be in [0, 1]."""
        result = model.predict_proba(input_value)
        for prob in result:
            assert 0.0 <= prob <= 1.0, "Probability out of bounds"
    
    @given(st.lists(st.floats(), min_size=1))
    def test_probability_sum(self, inputs, classifier):
        """Probabilities for all classes must sum to 1."""
        probabilities = classifier.predict_proba(inputs)
        total = sum(probabilities)
        assert abs(total - 1.0) < 1e-6, "Probabilities don't sum to 1"
    
    @given(st.text(min_size=1, max_size=1000))
    def test_no_crashes(self, text, nlp_model):
        """Model should handle any text without crashing."""
        try:
            result = nlp_model.predict(text)
            assert result is not None
        except Exception as e:
            pytest.fail(f"Model crashed on input: {e}")
```

### Test Oracles for AI Systems

When there's no single right answer, use these oracle strategies:

```
Oracle Strategies:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  1. REFERENCE ORACLE                                                    │
│     Compare to a trusted baseline model or human-labeled dataset        │
│     Example: New model accuracy should be >= existing model             │
│                                                                          │
│  2. STATISTICAL ORACLE                                                  │
│     Check aggregate properties over many predictions                     │
│     Example: Accuracy on test set should be >= 95%                      │
│                                                                          │
│  3. RELATIVE ORACLE                                                     │
│     Compare outputs for related inputs                                   │
│     Example: "Excellent" should be more positive than "Good"            │
│                                                                          │
│  4. PROPERTY ORACLE                                                     │
│     Verify outputs satisfy invariants                                    │
│     Example: Probabilities must sum to 1                                │
│                                                                          │
│  5. HUMAN ORACLE                                                        │
│     Use human judgment for sample evaluation                             │
│     Example: Random sample reviewed by domain experts                   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Continuous Testing for AI

AI models require ongoing testing, not just at deployment:

```python
"""
Continuous AI Testing Pipeline
"""

class ContinuousAITestingPipeline:
    """
    Implements continuous testing for ML models in production.
    """
    
    def __init__(self, model, baseline_metrics):
        self.model = model
        self.baseline = baseline_metrics
        self.alerts = []
    
    def daily_validation(self, sample_data):
        """Run daily validation checks."""
        results = {
            'timestamp': datetime.now(),
            'checks': []
        }
        
        # 1. Accuracy check on recent data
        accuracy = self.evaluate_accuracy(sample_data)
        results['checks'].append({
            'name': 'accuracy',
            'value': accuracy,
            'baseline': self.baseline['accuracy'],
            'pass': accuracy >= self.baseline['accuracy'] * 0.95  # 5% tolerance
        })
        
        # 2. Latency check
        latency_p99 = self.measure_latency(sample_data)
        results['checks'].append({
            'name': 'latency_p99',
            'value': latency_p99,
            'threshold': 100,  # ms
            'pass': latency_p99 <= 100
        })
        
        # 3. Data drift detection
        drift_score = self.detect_drift(sample_data)
        results['checks'].append({
            'name': 'data_drift',
            'value': drift_score,
            'threshold': 0.1,
            'pass': drift_score <= 0.1
        })
        
        # 4. Prediction distribution check
        distribution_shift = self.check_prediction_distribution(sample_data)
        results['checks'].append({
            'name': 'prediction_distribution',
            'value': distribution_shift,
            'threshold': 0.05,
            'pass': distribution_shift <= 0.05
        })
        
        # Generate alerts for failures
        for check in results['checks']:
            if not check['pass']:
                self.alert(f"FAILED: {check['name']} = {check['value']}")
        
        return results
    
    def detect_drift(self, recent_data):
        """Detect if input data distribution has shifted."""
        from scipy import stats
        
        # Compare feature distributions
        reference_distribution = self.baseline['feature_distribution']
        current_distribution = compute_distribution(recent_data)
        
        # Use KS test to detect drift
        statistic, p_value = stats.ks_2samp(
            reference_distribution, 
            current_distribution
        )
        
        return statistic  # Higher = more drift
    
    def alert(self, message):
        """Send alert for test failure."""
        self.alerts.append({
            'timestamp': datetime.now(),
            'message': message
        })
        # Integrate with PagerDuty, Slack, etc.
        send_notification(message)
```

## Code Example

```python
"""
Comprehensive AI Testing Example
Testing a Sentiment Analysis Model
"""

import pytest
import numpy as np
from sklearn.metrics import accuracy_score, f1_score

class SentimentModelTester:
    """
    Complete testing suite for a sentiment analysis model.
    """
    
    def __init__(self, model, test_dataset):
        self.model = model
        self.test_data = test_dataset
        
    # === UNIT TESTS ===
    
    def test_model_loads(self):
        """Model should load without errors."""
        assert self.model is not None
        assert hasattr(self.model, 'predict')
    
    def test_output_format(self):
        """Predictions should be in expected format."""
        prediction = self.model.predict(["Test sentence"])
        
        assert isinstance(prediction, list)
        assert len(prediction) == 1
        assert prediction[0] in ['positive', 'negative', 'neutral']
    
    def test_batch_prediction(self):
        """Model should handle batch inputs."""
        inputs = ["Good", "Bad", "Okay"] * 100  # 300 items
        predictions = self.model.predict(inputs)
        
        assert len(predictions) == 300
    
    # === ACCURACY TESTS ===
    
    def test_overall_accuracy(self, threshold=0.85):
        """Model should meet accuracy threshold on test set."""
        predictions = self.model.predict(self.test_data['texts'])
        accuracy = accuracy_score(self.test_data['labels'], predictions)
        
        assert accuracy >= threshold, \
            f"Accuracy {accuracy:.2%} below threshold {threshold:.2%}"
    
    def test_class_balance(self):
        """Model shouldn't be biased toward one class."""
        predictions = self.model.predict(self.test_data['texts'])
        
        from collections import Counter
        distribution = Counter(predictions)
        
        # No class should be > 60% of predictions (arbitrary threshold)
        for cls, count in distribution.items():
            ratio = count / len(predictions)
            assert ratio < 0.6, f"Class {cls} over-represented: {ratio:.2%}"
    
    # === ROBUSTNESS TESTS ===
    
    def test_handles_empty_input(self):
        """Model should handle empty strings gracefully."""
        result = self.model.predict([""])
        assert result is not None
    
    def test_handles_long_input(self):
        """Model should handle very long text."""
        long_text = "word " * 10000
        result = self.model.predict([long_text])
        assert result is not None
    
    def test_handles_special_characters(self):
        """Model should handle special characters."""
        special_texts = [
            "Great product! 😊👍",
            "¿Cómo estás?",
            "Test <script>alert('xss')</script>",
            "Price: $99.99 (50% off!!!)",
        ]
        
        for text in special_texts:
            result = self.model.predict([text])
            assert result is not None, f"Failed on: {text}"
    
    # === INVARIANCE TESTS ===
    
    def test_case_invariance(self):
        """Capitalization shouldn't change sentiment."""
        test_cases = [
            ("I love this product!", "I LOVE THIS PRODUCT!"),
            ("Terrible experience", "TERRIBLE EXPERIENCE"),
        ]
        
        for lower, upper in test_cases:
            pred_lower = self.model.predict([lower])[0]
            pred_upper = self.model.predict([upper])[0]
            assert pred_lower == pred_upper, \
                f"Case sensitivity: {lower} vs {upper}"
    
    def test_whitespace_invariance(self):
        """Extra whitespace shouldn't change prediction."""
        original = "Great movie!"
        variations = [
            "  Great movie!  ",
            "Great  movie!",
            "Great movie! ",
        ]
        
        base_pred = self.model.predict([original])[0]
        for variant in variations:
            assert self.model.predict([variant])[0] == base_pred
    
    # === DIRECTIONAL TESTS ===
    
    def test_negation_effect(self):
        """Negation should flip or reduce positive sentiment."""
        pairs = [
            ("I like this", "I don't like this"),
            ("Good quality", "Not good quality"),
            ("Would recommend", "Would not recommend"),
        ]
        
        for positive, negated in pairs:
            pos_score = self.model.predict_proba([positive])[0]['positive']
            neg_score = self.model.predict_proba([negated])[0]['positive']
            
            assert pos_score > neg_score, \
                f"Negation didn't reduce positive score: {positive} -> {negated}"
    
    # === PERFORMANCE TESTS ===
    
    def test_latency(self, max_ms=100):
        """Single prediction should be fast."""
        import time
        
        text = "This is a test sentence for latency measurement."
        
        start = time.time()
        self.model.predict([text])
        elapsed_ms = (time.time() - start) * 1000
        
        assert elapsed_ms < max_ms, \
            f"Latency {elapsed_ms:.1f}ms exceeds {max_ms}ms threshold"
    
    def test_throughput(self, min_per_second=100):
        """Model should handle minimum throughput."""
        import time
        
        texts = ["Test sentence"] * 1000
        
        start = time.time()
        self.model.predict(texts)
        elapsed = time.time() - start
        
        throughput = 1000 / elapsed
        assert throughput >= min_per_second, \
            f"Throughput {throughput:.0f}/s below {min_per_second}/s"
    
    def run_all_tests(self):
        """Execute all tests and return report."""
        test_methods = [m for m in dir(self) if m.startswith('test_')]
        
        results = {'passed': [], 'failed': []}
        
        for method_name in test_methods:
            method = getattr(self, method_name)
            try:
                method()
                results['passed'].append(method_name)
            except AssertionError as e:
                results['failed'].append((method_name, str(e)))
            except Exception as e:
                results['failed'].append((method_name, f"Error: {e}"))
        
        return results


# Usage
if __name__ == "__main__":
    # Assuming model and test_data are loaded
    # tester = SentimentModelTester(model, test_data)
    # results = tester.run_all_tests()
    # print(f"Passed: {len(results['passed'])}, Failed: {len(results['failed'])}")
    pass
```

## Summary

- **AI systems differ fundamentally** from traditional software—probabilistic, data-dependent, potentially unexplainable
- **Key challenges:** Non-determinism, oracle problem, data dependency, emergent behavior
- **AI testing lifecycle** spans data testing, model testing, integration testing, and production monitoring
- **Testing strategies:** Behavioral testing, metamorphic testing, property-based testing
- **Oracles for AI:** Reference models, statistical properties, relative comparisons, human evaluation
- **Continuous testing** is essential—models degrade over time due to data drift

## Additional Resources

- [Checklist: Beyond Accuracy (Microsoft Research)](https://github.com/marcotcr/checklist)
- [ML Test Score: A Rubric for ML Production Readiness](https://research.google/pubs/pub46555/)
- [Testing Machine Learning Systems (Google)](https://developers.google.com/machine-learning/testing-debugging)

