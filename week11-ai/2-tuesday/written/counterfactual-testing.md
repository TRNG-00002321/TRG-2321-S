# Counterfactual Testing

## Learning Objectives

- Understand what counterfactual testing is and its role in AI fairness
- Apply counterfactual fairness concepts to identify discrimination
- Generate effective counterfactual examples for testing
- Implement testing strategies using minimal changes
- Identify discriminatory patterns in model behavior
- Use counterfactual explanation tools for analysis

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

Consider this scenario: A loan application is rejected. The applicant asks, "Would I have been approved if I were a different gender?" This is a counterfactual question—asking what would happen in an alternative reality where only one factor changed.

Counterfactual testing allows us to answer such questions systematically. By changing protected attributes (like gender, race, or age) while keeping everything else constant, we can detect whether a model's decisions are influenced by these sensitive characteristics. This technique is one of the most direct ways to test for discrimination in AI systems.

As AI systems increasingly make consequential decisions—who gets hired, who receives loans, who gets medical treatment—the ability to test counterfactual fairness becomes essential for building trustworthy AI.

## The Concept

### What is Counterfactual Testing?

**Counterfactual testing** examines how model predictions change when specific input attributes are modified while everything else remains constant. If changing a protected attribute (like gender) changes the prediction, the model may be exhibiting unfair behavior.

```
Counterfactual Testing Illustrated:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  ORIGINAL INPUT:                     COUNTERFACTUAL INPUT:              │
│  ───────────────                     ────────────────────               │
│  Name: John Smith                    Name: Jane Smith                   │
│  Age: 35                             Age: 35          ← Same            │
│  Income: $75,000                     Income: $75,000  ← Same            │
│  Credit Score: 720                   Credit Score: 720 ← Same           │
│  Gender: Male        ← Changed →     Gender: Female                     │
│                                                                          │
│           │                                    │                        │
│           ▼                                    ▼                        │
│      ┌─────────┐                         ┌─────────┐                    │
│      │  MODEL  │                         │  MODEL  │                    │
│      └─────────┘                         └─────────┘                    │
│           │                                    │                        │
│           ▼                                    ▼                        │
│    APPROVED (92%)                      REJECTED (45%)                   │
│                                                                          │
│  IF PREDICTIONS DIFFER → POTENTIAL DISCRIMINATION                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Counterfactual Fairness Explained

**Counterfactual fairness** is a causal definition of fairness: A decision is counterfactually fair for an individual if it would have been the same in a "counterfactual world" where the individual's protected attributes were different.

Formally:
```
A model M is counterfactually fair if:
P(Ŷ = 1 | A = a, X = x) = P(Ŷ = 1 | A = a', X = x)

Where:
- Ŷ = Model prediction
- A = Protected attribute (e.g., gender)
- a, a' = Different values of the protected attribute
- X = All other attributes
```

In plain language: For any individual, the model's prediction should remain the same regardless of their protected attribute value.

### Why Counterfactual Testing Matters

```
Traditional Fairness Metrics:            Counterfactual Fairness:
─────────────────────────────            ─────────────────────────
                                         
• Compare GROUP statistics               • Test INDIVIDUAL decisions
• "Are approval rates equal?"            • "Would THIS person's outcome
• Can mask individual bias                 change if their gender changed?"
• Aggregate view                         • Granular view
                                         
Example Problem:                         Counterfactual Catches It:
A model might have equal                 Testing individual cases reveals
approval rates across genders            the model penalizes women with
overall, but still discriminate          high incomes while favoring
against women in specific                men with the same characteristics
subgroups (intersectionality)            
```

### Generating Counterfactual Examples

#### Simple Attribute Swap

The most basic approach—directly change the protected attribute:

```python
def generate_simple_counterfactual(original: dict, 
                                    attribute: str, 
                                    new_value) -> dict:
    """
    Generate counterfactual by swapping attribute value.
    """
    counterfactual = original.copy()
    counterfactual[attribute] = new_value
    return counterfactual

# Example
original_applicant = {
    'name': 'John Smith',
    'gender': 'male',
    'age': 35,
    'income': 75000,
    'credit_score': 720
}

counterfactual_applicant = generate_simple_counterfactual(
    original_applicant, 
    'gender', 
    'female'
)
# Result: same person but gender changed to female
```

#### Handling Correlated Attributes

Protected attributes often correlate with other features. Simply swapping may create unrealistic examples:

```
Problem with Simple Swaps:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  Original: Male, Name="Michael", Height=180cm                           │
│  Simple swap: Female, Name="Michael", Height=180cm                      │
│                                                                          │
│  Issues:                                                                 │
│  • "Michael" is typically a male name                                   │
│  • 180cm is above average for women                                     │
│  • Creates unrealistic/impossible combination                           │
│                                                                          │
│  Better Approach: Adjust correlated attributes                          │
│  Counterfactual: Female, Name="Michelle", Height=165cm                  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```python
def generate_realistic_counterfactual(original: dict,
                                       protected_attr: str,
                                       new_value,
                                       correlation_adjustments: dict) -> dict:
    """
    Generate counterfactual with adjustments for correlated attributes.
    
    Args:
        original: Original data point
        protected_attr: Attribute to change
        new_value: New value for protected attribute
        correlation_adjustments: Dict mapping attributes to adjustment functions
    """
    counterfactual = original.copy()
    counterfactual[protected_attr] = new_value
    
    # Apply adjustments for correlated attributes
    for attr, adjustment_fn in correlation_adjustments.items():
        if attr in counterfactual:
            counterfactual[attr] = adjustment_fn(
                original[attr], 
                original[protected_attr], 
                new_value
            )
    
    return counterfactual

# Example: Gender swap with correlated attribute adjustments
def adjust_name(original_name, original_gender, new_gender):
    """Map name to gender-appropriate equivalent."""
    name_mappings = {
        ('male', 'female'): {
            'Michael': 'Michelle',
            'John': 'Joan',
            'Robert': 'Roberta',
        },
        ('female', 'male'): {
            'Michelle': 'Michael',
            'Joan': 'John',
            'Roberta': 'Robert',
        }
    }
    mapping = name_mappings.get((original_gender, new_gender), {})
    return mapping.get(original_name, original_name)
```

### Testing with Minimal Changes

The principle of minimal change ensures counterfactuals are as similar as possible to originals:

```
Minimal Change Principle:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  GOAL: Change ONLY what's necessary to test the hypothesis              │
│                                                                          │
│  BAD: Change multiple attributes                                        │
│  ─────────────────────────────                                          │
│  Original:  Age=35, Gender=M, Job=Engineer, City=NYC                    │
│  Counterfactual: Age=45, Gender=F, Job=Teacher, City=LA                 │
│  Problem: If prediction changes, which attribute caused it?             │
│                                                                          │
│  GOOD: Change only the target attribute                                 │
│  ────────────────────────────────                                       │
│  Original:     Age=35, Gender=M, Job=Engineer, City=NYC                 │
│  Counterfactual: Age=35, Gender=F, Job=Engineer, City=NYC               │
│  Benefit: Any prediction change is attributable to gender               │
│                                                                          │
│  BETTER: Change target + necessary correlates only                      │
│  ────────────────────────────────────────────────                       │
│  Original:     Name=John, Gender=M, Job=Engineer, City=NYC              │
│  Counterfactual: Name=Jane, Gender=F, Job=Engineer, City=NYC            │
│  Benefit: Realistic example, clear attribution                          │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Identifying Discriminatory Patterns

Systematic counterfactual testing reveals discrimination patterns:

```python
def counterfactual_audit(model, dataset: list, 
                          protected_attr: str,
                          attr_values: list) -> dict:
    """
    Audit model for discriminatory patterns using counterfactuals.
    
    Args:
        model: Model with predict() method
        dataset: List of data points to test
        protected_attr: Attribute to test for discrimination
        attr_values: Possible values of protected attribute
    
    Returns:
        Audit results with discrimination metrics
    """
    results = {
        'total_tested': 0,
        'prediction_changes': 0,
        'advantage_by_group': {val: 0 for val in attr_values},
        'examples': []
    }
    
    for original in dataset:
        original_pred = model.predict(original)
        original_value = original[protected_attr]
        
        for new_value in attr_values:
            if new_value == original_value:
                continue
            
            counterfactual = generate_counterfactual(
                original, protected_attr, new_value
            )
            counterfactual_pred = model.predict(counterfactual)
            
            results['total_tested'] += 1
            
            if original_pred != counterfactual_pred:
                results['prediction_changes'] += 1
                
                # Track which group is advantaged
                if original_pred > counterfactual_pred:
                    results['advantage_by_group'][original_value] += 1
                else:
                    results['advantage_by_group'][new_value] += 1
                
                # Store example for analysis
                if len(results['examples']) < 100:
                    results['examples'].append({
                        'original': original,
                        'original_pred': original_pred,
                        'counterfactual': counterfactual,
                        'counterfactual_pred': counterfactual_pred
                    })
    
    # Calculate discrimination rate
    results['discrimination_rate'] = (
        results['prediction_changes'] / results['total_tested'] * 100
        if results['total_tested'] > 0 else 0
    )
    
    return results
```

### Counterfactual Analysis Patterns

Different patterns of counterfactual behavior indicate different problems:

```
Pattern Analysis:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  PATTERN 1: Consistent Bias                                             │
│  ─────────────────────────────                                          │
│  Changing A → B always hurts prediction                                 │
│  Example: Male → Female always reduces approval probability             │
│  Diagnosis: Direct discrimination against group B                       │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  PATTERN 2: Conditional Bias                                            │
│  ──────────────────────────────                                         │
│  Bias only appears in certain contexts                                  │
│  Example: Female penalty only for high-income applicants                │
│  Diagnosis: Intersectional discrimination                               │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  PATTERN 3: Symmetric Impact                                            │
│  ─────────────────────────────                                          │
│  Changes in both directions affect predictions equally                  │
│  Example: M→F and F→M both cause changes, roughly balanced              │
│  Diagnosis: Model is sensitive to attribute but not systematically      │
│             biased (may still be problematic)                           │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  PATTERN 4: No Impact                                                   │
│  ─────────────────                                                      │
│  Changing protected attribute doesn't change predictions                │
│  Diagnosis: Model may be counterfactually fair for this attribute       │
│  Caveat: Check for proxy variables                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Counterfactual Explanation Tools

Several tools exist to generate and analyze counterfactuals:

```python
"""
Using DiCE (Diverse Counterfactual Explanations)
"""

# DiCE is a popular library for counterfactual explanations
# pip install dice-ml

def generate_dice_counterfactuals(model, data, instance):
    """
    Generate diverse counterfactual explanations using DiCE.
    """
    import dice_ml
    
    # Create DiCE data object
    d = dice_ml.Data(
        dataframe=data,
        continuous_features=['age', 'income'],
        outcome_name='approved'
    )
    
    # Create DiCE model object
    m = dice_ml.Model(model=model, backend='sklearn')
    
    # Create explainer
    exp = dice_ml.Dice(d, m)
    
    # Generate counterfactuals
    counterfactuals = exp.generate_counterfactuals(
        instance,
        total_CFs=5,  # Generate 5 counterfactuals
        desired_class='opposite',  # Flip the prediction
        features_to_vary=['income', 'credit_score'],  # Only change these
        permitted_range={'income': [0, 200000]}  # Constraints
    )
    
    return counterfactuals


"""
Simple Counterfactual Generator (without external libraries)
"""

def generate_minimal_counterfactual(model, instance: dict, 
                                     target_prediction,
                                     mutable_features: list,
                                     step_sizes: dict,
                                     max_iterations: int = 100):
    """
    Find minimal counterfactual through iterative feature changes.
    """
    current = instance.copy()
    current_pred = model.predict([current])[0]
    
    for _ in range(max_iterations):
        if current_pred == target_prediction:
            return current
        
        # Try changing each mutable feature
        for feature in mutable_features:
            step = step_sizes.get(feature, 1)
            
            # Try increasing
            candidate = current.copy()
            candidate[feature] = current[feature] + step
            if model.predict([candidate])[0] == target_prediction:
                return candidate
            
            # Try decreasing
            candidate = current.copy()
            candidate[feature] = current[feature] - step
            if model.predict([candidate])[0] == target_prediction:
                return candidate
        
        # Reduce step size if no progress
        step_sizes = {k: v * 0.9 for k, v in step_sizes.items()}
    
    return None  # Could not find counterfactual
```

### Practical Implementation Strategy

```
Counterfactual Testing Workflow:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  STEP 1: IDENTIFY PROTECTED ATTRIBUTES                                  │
│  ─────────────────────────────────────                                  │
│  • List legally protected characteristics                               │
│  • Include proxy variables                                              │
│  • Consider domain-specific sensitive attributes                        │
│                                                                          │
│  STEP 2: DEFINE COUNTERFACTUAL GENERATION STRATEGY                      │
│  ────────────────────────────────────────────────                       │
│  • Simple swap vs. realistic adjustment                                 │
│  • Handle correlated attributes                                         │
│  • Set constraints for realistic ranges                                 │
│                                                                          │
│  STEP 3: SELECT TEST CASES                                              │
│  ─────────────────────────                                              │
│  • Sample across demographic groups                                     │
│  • Include edge cases                                                   │
│  • Cover different feature ranges                                       │
│                                                                          │
│  STEP 4: RUN COUNTERFACTUAL TESTS                                       │
│  ──────────────────────────────                                         │
│  • Generate counterfactuals for each test case                          │
│  • Record original and counterfactual predictions                       │
│  • Calculate prediction differences                                      │
│                                                                          │
│  STEP 5: ANALYZE PATTERNS                                               │
│  ────────────────────────                                               │
│  • Aggregate discrimination metrics                                     │
│  • Identify systematic patterns                                         │
│  • Examine intersectional effects                                       │
│                                                                          │
│  STEP 6: DOCUMENT AND REPORT                                            │
│  ───────────────────────────                                            │
│  • Record findings with examples                                        │
│  • Recommend remediation actions                                        │
│  • Track over time                                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

## Code Example

```python
"""
Complete Counterfactual Testing Framework
"""

import pandas as pd
import numpy as np
from typing import Dict, List, Callable, Any
from dataclasses import dataclass

@dataclass
class CounterfactualResult:
    """Result of a single counterfactual test."""
    original: Dict[str, Any]
    original_prediction: float
    counterfactual: Dict[str, Any]
    counterfactual_prediction: float
    changed_attribute: str
    original_value: Any
    new_value: Any
    prediction_changed: bool
    
    @property
    def prediction_difference(self) -> float:
        return self.counterfactual_prediction - self.original_prediction


class CounterfactualTester:
    """
    Comprehensive counterfactual testing for ML models.
    """
    
    def __init__(self, model, protected_attributes: List[str],
                 attribute_values: Dict[str, List],
                 correlated_adjustments: Dict[str, Callable] = None):
        """
        Args:
            model: Trained model with predict/predict_proba method
            protected_attributes: List of protected attributes to test
            attribute_values: Dict mapping attributes to possible values
            correlated_adjustments: Dict mapping attributes to adjustment functions
        """
        self.model = model
        self.protected_attributes = protected_attributes
        self.attribute_values = attribute_values
        self.correlated_adjustments = correlated_adjustments or {}
        self.results: List[CounterfactualResult] = []
    
    def generate_counterfactual(self, original: Dict, 
                                 attribute: str, 
                                 new_value: Any) -> Dict:
        """Generate a single counterfactual example."""
        cf = original.copy()
        cf[attribute] = new_value
        
        # Apply correlated adjustments
        for attr, adjust_fn in self.correlated_adjustments.items():
            if attr in cf and attr != attribute:
                cf[attr] = adjust_fn(original, attribute, new_value)
        
        return cf
    
    def test_instance(self, instance: Dict) -> List[CounterfactualResult]:
        """Test a single instance against all counterfactuals."""
        results = []
        
        # Get original prediction
        original_pred = self._get_prediction(instance)
        
        for attr in self.protected_attributes:
            if attr not in instance:
                continue
            
            original_value = instance[attr]
            
            for new_value in self.attribute_values.get(attr, []):
                if new_value == original_value:
                    continue
                
                # Generate counterfactual
                cf = self.generate_counterfactual(instance, attr, new_value)
                cf_pred = self._get_prediction(cf)
                
                result = CounterfactualResult(
                    original=instance,
                    original_prediction=original_pred,
                    counterfactual=cf,
                    counterfactual_prediction=cf_pred,
                    changed_attribute=attr,
                    original_value=original_value,
                    new_value=new_value,
                    prediction_changed=abs(original_pred - cf_pred) > 0.01
                )
                results.append(result)
        
        return results
    
    def _get_prediction(self, instance: Dict) -> float:
        """Get model prediction for an instance."""
        # Convert to format expected by model
        features = pd.DataFrame([instance])
        
        # Try to get probability, fall back to binary prediction
        if hasattr(self.model, 'predict_proba'):
            return self.model.predict_proba(features)[0][1]
        else:
            return float(self.model.predict(features)[0])
    
    def run_audit(self, test_data: List[Dict]) -> Dict:
        """
        Run full counterfactual audit on test data.
        """
        all_results = []
        
        for instance in test_data:
            instance_results = self.test_instance(instance)
            all_results.extend(instance_results)
        
        self.results = all_results
        return self.analyze_results()
    
    def analyze_results(self) -> Dict:
        """Analyze counterfactual test results."""
        if not self.results:
            return {'error': 'No results to analyze'}
        
        analysis = {
            'total_tests': len(self.results),
            'predictions_changed': sum(r.prediction_changed for r in self.results),
            'by_attribute': {},
        }
        
        # Analyze by attribute
        for attr in self.protected_attributes:
            attr_results = [r for r in self.results if r.changed_attribute == attr]
            if not attr_results:
                continue
            
            changes = [r for r in attr_results if r.prediction_changed]
            
            # Calculate average prediction shift by transition
            transitions = {}
            for r in attr_results:
                key = (r.original_value, r.new_value)
                if key not in transitions:
                    transitions[key] = []
                transitions[key].append(r.prediction_difference)
            
            analysis['by_attribute'][attr] = {
                'total_tests': len(attr_results),
                'prediction_changes': len(changes),
                'change_rate': len(changes) / len(attr_results) * 100,
                'transitions': {
                    f"{k[0]}->{k[1]}": {
                        'count': len(v),
                        'avg_change': np.mean(v),
                        'std_change': np.std(v),
                        'bias_direction': 'favors_original' if np.mean(v) < 0 else 'favors_counterfactual'
                    }
                    for k, v in transitions.items()
                }
            }
        
        # Overall discrimination score
        analysis['discrimination_rate'] = (
            analysis['predictions_changed'] / analysis['total_tests'] * 100
        )
        
        # Severity assessment
        if analysis['discrimination_rate'] > 20:
            analysis['severity'] = 'HIGH'
        elif analysis['discrimination_rate'] > 5:
            analysis['severity'] = 'MEDIUM'
        else:
            analysis['severity'] = 'LOW'
        
        return analysis
    
    def get_worst_examples(self, n: int = 10) -> List[CounterfactualResult]:
        """Get examples with largest prediction changes."""
        changed = [r for r in self.results if r.prediction_changed]
        sorted_results = sorted(
            changed, 
            key=lambda r: abs(r.prediction_difference), 
            reverse=True
        )
        return sorted_results[:n]
    
    def generate_report(self) -> str:
        """Generate human-readable audit report."""
        analysis = self.analyze_results()
        
        report = f"""
╔══════════════════════════════════════════════════════════════════════════╗
║                COUNTERFACTUAL FAIRNESS AUDIT REPORT                       ║
╠══════════════════════════════════════════════════════════════════════════╣

SUMMARY
───────
• Total Counterfactual Tests: {analysis['total_tests']:,}
• Predictions Changed: {analysis['predictions_changed']:,}
• Overall Discrimination Rate: {analysis['discrimination_rate']:.1f}%
• Severity: {analysis['severity']}

ANALYSIS BY PROTECTED ATTRIBUTE
───────────────────────────────
"""
        for attr, data in analysis['by_attribute'].items():
            report += f"\n{attr.upper()}:\n"
            report += f"  • Tests: {data['total_tests']}, Changes: {data['prediction_changes']}\n"
            report += f"  • Change Rate: {data['change_rate']:.1f}%\n"
            report += "  • Transitions:\n"
            
            for transition, stats in data['transitions'].items():
                direction = "↓" if stats['avg_change'] < 0 else "↑"
                report += f"    {transition}: {direction} {abs(stats['avg_change']):.3f} avg change\n"
        
        # Worst examples
        report += "\nWORST DISCRIMINATION EXAMPLES\n"
        report += "─────────────────────────────\n"
        
        worst = self.get_worst_examples(5)
        for i, example in enumerate(worst, 1):
            report += f"""
Example {i}:
  Changed: {example.changed_attribute} ({example.original_value} → {example.new_value})
  Prediction: {example.original_prediction:.3f} → {example.counterfactual_prediction:.3f}
  Difference: {example.prediction_difference:+.3f}
"""
        
        return report


# Usage Example
if __name__ == "__main__":
    from sklearn.ensemble import RandomForestClassifier
    from sklearn.datasets import make_classification
    
    # Create sample model and data
    X, y = make_classification(n_samples=1000, n_features=5, random_state=42)
    
    # Add protected attribute
    data = pd.DataFrame(X, columns=['income', 'credit_score', 'age', 'debt_ratio', 'employment_years'])
    data['gender'] = np.random.choice(['male', 'female'], 1000)
    data['approved'] = y
    
    # Train biased model (for demonstration)
    model = RandomForestClassifier(random_state=42)
    model.fit(data.drop('approved', axis=1), data['approved'])
    
    # Run counterfactual audit
    tester = CounterfactualTester(
        model=model,
        protected_attributes=['gender'],
        attribute_values={'gender': ['male', 'female']}
    )
    
    test_instances = data.drop('approved', axis=1).head(100).to_dict('records')
    analysis = tester.run_audit(test_instances)
    
    print(tester.generate_report())
```

## Summary

- **Counterfactual testing** examines how predictions change when protected attributes are modified
- **Counterfactual fairness** means decisions should be the same in alternative realities
- **Minimal change principle:** Only modify necessary attributes to maintain causal attribution
- **Handle correlations:** Some attributes must be adjusted together for realistic counterfactuals
- **Pattern analysis:** Consistent, conditional, symmetric, and no-impact patterns have different implications
- **Tools available:** DiCE and custom implementations can generate diverse counterfactuals
- **Systematic auditing** reveals discrimination patterns that aggregate metrics might miss

## Additional Resources

- [DiCE: Diverse Counterfactual Explanations](https://github.com/interpretml/DiCE)
- [Counterfactual Fairness (Original Paper)](https://arxiv.org/abs/1703.06856)
- [Alibi Explain - Counterfactuals](https://docs.seldon.io/projects/alibi/en/latest/methods/CF.html)

