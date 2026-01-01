# Fairness Metrics

## Learning Objectives

- Understand different mathematical definitions of fairness
- Calculate and interpret key fairness metrics
- Recognize trade-offs between different fairness criteria
- Choose appropriate fairness metrics for different contexts
- Implement fairness metrics in testing workflows
- Report fairness results effectively to stakeholders

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

"Is this AI fair?" seems like a simple question, but it opens a Pandora's box of complexity. Fairness isn't a single concept—it's a family of related but distinct mathematical criteria, many of which are mutually incompatible. You literally cannot satisfy all fairness definitions simultaneously.

For quality engineers, understanding these metrics is crucial. Without quantitative fairness measures, claims of fairness are just opinions. With them, you can objectively assess whether a model meets fairness requirements, compare different models, track fairness over time, and demonstrate compliance to regulators. This module equips you with the mathematical and practical tools to measure what matters.

## The Concept

### Why Multiple Fairness Metrics Exist

Different stakeholders care about different aspects of fairness:

```
Stakeholder Perspectives:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  APPLICANT'S PERSPECTIVE:                                               │
│  "I should have the same chance of approval as anyone with my           │
│   qualifications, regardless of my demographic group."                  │
│  → Focuses on INDIVIDUAL fairness                                       │
│                                                                          │
│  ADVOCACY GROUP'S PERSPECTIVE:                                          │
│  "Our group should receive approvals at the same rate as other groups." │
│  → Focuses on DEMOGRAPHIC PARITY                                        │
│                                                                          │
│  LENDER'S PERSPECTIVE:                                                  │
│  "Among those we approve, the same proportion from each group           │
│   should actually repay their loans."                                   │
│  → Focuses on PREDICTIVE PARITY                                         │
│                                                                          │
│  REGULATOR'S PERSPECTIVE:                                               │
│  "Equally qualified people should have equal outcomes, regardless       │
│   of group membership."                                                 │
│  → Focuses on EQUALIZED ODDS                                            │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Core Fairness Metrics

#### 1. Demographic Parity (Statistical Parity)

**Definition:** The probability of a positive prediction should be equal across groups.

```
Mathematical Definition:
P(Ŷ = 1 | A = 0) = P(Ŷ = 1 | A = 1)

Where:
• Ŷ = Predicted outcome (0 or 1)
• A = Protected attribute (group membership)
```

```python
def demographic_parity(predictions, protected_attribute):
    """
    Calculate demographic parity metrics.
    
    Returns:
        Dict with rates and difference
    """
    groups = protected_attribute.unique()
    rates = {}
    
    for group in groups:
        mask = protected_attribute == group
        rate = predictions[mask].mean()
        rates[group] = rate
    
    max_rate = max(rates.values())
    min_rate = min(rates.values())
    
    return {
        'rates': rates,
        'difference': max_rate - min_rate,
        'ratio': min_rate / max_rate if max_rate > 0 else 0,
        'satisfied': (max_rate - min_rate) < 0.05  # 5% threshold
    }
```

**Interpretation:**
- Difference close to 0 = fairer
- Common threshold: difference < 0.05 or ratio > 0.8 (four-fifths rule)

**Limitations:**
- Ignores actual qualifications
- May require lowering standards for one group or raising for another
- Doesn't guarantee individuals are treated fairly

#### 2. Equalized Odds (Separation)

**Definition:** Both true positive rate (TPR) and false positive rate (FPR) should be equal across groups.

```
Mathematical Definition:
P(Ŷ = 1 | A = 0, Y = 1) = P(Ŷ = 1 | A = 1, Y = 1)  [Equal TPR]
P(Ŷ = 1 | A = 0, Y = 0) = P(Ŷ = 1 | A = 1, Y = 0)  [Equal FPR]

Where:
• Y = Actual outcome (ground truth)
```

```python
def equalized_odds(predictions, actual, protected_attribute):
    """
    Calculate equalized odds metrics.
    """
    groups = protected_attribute.unique()
    metrics = {'tpr': {}, 'fpr': {}, 'tnr': {}, 'fnr': {}}
    
    for group in groups:
        mask = protected_attribute == group
        group_preds = predictions[mask]
        group_actual = actual[mask]
        
        # True Positive Rate (Sensitivity/Recall)
        positives = group_actual == 1
        if positives.sum() > 0:
            tpr = group_preds[positives].mean()
        else:
            tpr = 0
        
        # False Positive Rate
        negatives = group_actual == 0
        if negatives.sum() > 0:
            fpr = group_preds[negatives].mean()
        else:
            fpr = 0
        
        metrics['tpr'][group] = tpr
        metrics['fpr'][group] = fpr
        metrics['tnr'][group] = 1 - fpr
        metrics['fnr'][group] = 1 - tpr
    
    # Calculate differences
    metrics['tpr_difference'] = max(metrics['tpr'].values()) - min(metrics['tpr'].values())
    metrics['fpr_difference'] = max(metrics['fpr'].values()) - min(metrics['fpr'].values())
    
    # Equalized odds satisfied if both differences are small
    metrics['satisfied'] = (
        metrics['tpr_difference'] < 0.05 and 
        metrics['fpr_difference'] < 0.05
    )
    
    return metrics
```

**Interpretation:**
- TPR difference: Do qualified people from different groups have equal chances?
- FPR difference: Do unqualified people from different groups face equal risk of false approval?
- Both must be equal for equalized odds

#### 3. Equal Opportunity

**Definition:** A relaxed version of equalized odds—only requires equal true positive rates.

```
Mathematical Definition:
P(Ŷ = 1 | A = 0, Y = 1) = P(Ŷ = 1 | A = 1, Y = 1)
```

```python
def equal_opportunity(predictions, actual, protected_attribute):
    """
    Calculate equal opportunity (TPR parity only).
    """
    groups = protected_attribute.unique()
    tpr_by_group = {}
    
    for group in groups:
        mask = protected_attribute == group
        positives = actual[mask] == 1
        
        if positives.sum() > 0:
            tpr = predictions[mask][positives].mean()
        else:
            tpr = 0
        
        tpr_by_group[group] = tpr
    
    return {
        'tpr_by_group': tpr_by_group,
        'difference': max(tpr_by_group.values()) - min(tpr_by_group.values()),
        'satisfied': (max(tpr_by_group.values()) - min(tpr_by_group.values())) < 0.05
    }
```

**Use case:** When you care most that deserving individuals from all groups have equal chance of positive outcomes.

#### 4. Predictive Parity (Sufficiency)

**Definition:** Precision (positive predictive value) should be equal across groups.

```
Mathematical Definition:
P(Y = 1 | Ŷ = 1, A = 0) = P(Y = 1 | Ŷ = 1, A = 1)
```

```python
def predictive_parity(predictions, actual, protected_attribute):
    """
    Calculate predictive parity metrics.
    """
    groups = protected_attribute.unique()
    ppv_by_group = {}  # Positive Predictive Value (Precision)
    npv_by_group = {}  # Negative Predictive Value
    
    for group in groups:
        mask = protected_attribute == group
        predicted_positive = predictions[mask] == 1
        predicted_negative = predictions[mask] == 0
        
        # PPV: Among predicted positives, how many are actually positive?
        if predicted_positive.sum() > 0:
            ppv = actual[mask][predicted_positive].mean()
        else:
            ppv = 0
        
        # NPV: Among predicted negatives, how many are actually negative?
        if predicted_negative.sum() > 0:
            npv = (actual[mask][predicted_negative] == 0).mean()
        else:
            npv = 0
        
        ppv_by_group[group] = ppv
        npv_by_group[group] = npv
    
    return {
        'ppv_by_group': ppv_by_group,
        'npv_by_group': npv_by_group,
        'ppv_difference': max(ppv_by_group.values()) - min(ppv_by_group.values()),
        'satisfied': (max(ppv_by_group.values()) - min(ppv_by_group.values())) < 0.05
    }
```

**Interpretation:** When the model says "approved," is it equally likely to be correct for all groups?

#### 5. Calibration

**Definition:** Among all instances where the model predicts probability p, the actual proportion should be p, for all groups.

```
Mathematical Definition:
P(Y = 1 | S = s, A = 0) = P(Y = 1 | S = s, A = 1) = s

Where S is the model's score/probability
```

```python
def calibration_by_group(probabilities, actual, protected_attribute, n_bins=10):
    """
    Check calibration by group.
    """
    groups = protected_attribute.unique()
    calibration = {}
    
    for group in groups:
        mask = protected_attribute == group
        group_probs = probabilities[mask]
        group_actual = actual[mask]
        
        # Bin predictions
        bins = np.linspace(0, 1, n_bins + 1)
        bin_indices = np.digitize(group_probs, bins) - 1
        bin_indices = np.clip(bin_indices, 0, n_bins - 1)
        
        bin_stats = []
        for i in range(n_bins):
            bin_mask = bin_indices == i
            if bin_mask.sum() > 0:
                predicted_prob = group_probs[bin_mask].mean()
                actual_rate = group_actual[bin_mask].mean()
                bin_stats.append({
                    'predicted': predicted_prob,
                    'actual': actual_rate,
                    'count': bin_mask.sum(),
                    'error': abs(predicted_prob - actual_rate)
                })
        
        calibration[group] = bin_stats
    
    return calibration
```

### The Impossibility Theorem

**Critical insight:** Except in trivial cases, it is mathematically impossible to satisfy demographic parity, equalized odds, and predictive parity simultaneously.

```
The Fairness Impossibility:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  IF base rates differ between groups (P(Y=1|A=0) ≠ P(Y=1|A=1))          │
│                                                                          │
│  THEN you cannot simultaneously achieve:                                 │
│  • Demographic Parity                                                   │
│  • Equalized Odds                                                       │
│  • Predictive Parity                                                    │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  Example:                                                               │
│  • Group A: 80% actually qualify (base rate = 0.8)                      │
│  • Group B: 50% actually qualify (base rate = 0.5)                      │
│                                                                          │
│  Demographic Parity: Approve same % from each group                     │
│    → But then precision differs (more false positives in Group B)       │
│                                                                          │
│  Predictive Parity: Same precision for approvals in each group          │
│    → But then approval rates must differ                                │
│                                                                          │
│  Equalized Odds: Same TPR and FPR                                       │
│    → But then demographic parity is violated                            │
│                                                                          │
│  YOU MUST CHOOSE which fairness criterion matters most for your context │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Choosing Appropriate Fairness Metrics

| Context | Recommended Metric | Reasoning |
|---------|-------------------|-----------|
| **Hiring** | Demographic Parity + Equal Opportunity | Legal requirements, opportunity equality |
| **Lending** | Equalized Odds | Want accurate predictions regardless of group |
| **Criminal Justice** | Equal Opportunity | False negatives very costly |
| **Medical Diagnosis** | Equalized Odds | Both false positives and negatives matter |
| **Content Moderation** | Calibration | Probability scores should be meaningful |
| **Insurance** | Predictive Parity | Actuarial accuracy required |

### Implementing Fairness Metrics in Testing

```python
"""
Comprehensive Fairness Metrics Calculator
"""

import pandas as pd
import numpy as np
from typing import Dict, List, Optional

class FairnessMetricsCalculator:
    """
    Calculate multiple fairness metrics for ML model evaluation.
    """
    
    def __init__(self, 
                 predictions: np.ndarray,
                 actual: np.ndarray,
                 protected_attribute: np.ndarray,
                 probabilities: Optional[np.ndarray] = None):
        """
        Args:
            predictions: Binary predictions (0/1)
            actual: Ground truth labels (0/1)
            protected_attribute: Group membership
            probabilities: Predicted probabilities (optional)
        """
        self.predictions = np.array(predictions)
        self.actual = np.array(actual)
        self.protected = np.array(protected_attribute)
        self.probabilities = np.array(probabilities) if probabilities is not None else None
        self.groups = np.unique(self.protected)
    
    def all_metrics(self) -> Dict:
        """Calculate all fairness metrics."""
        return {
            'demographic_parity': self.demographic_parity(),
            'equalized_odds': self.equalized_odds(),
            'equal_opportunity': self.equal_opportunity(),
            'predictive_parity': self.predictive_parity(),
            'overall_accuracy': self.accuracy_by_group(),
        }
    
    def demographic_parity(self) -> Dict:
        """Statistical parity metrics."""
        rates = {}
        for group in self.groups:
            mask = self.protected == group
            rates[str(group)] = self.predictions[mask].mean()
        
        values = list(rates.values())
        return {
            'selection_rates': rates,
            'difference': max(values) - min(values),
            'ratio': min(values) / max(values) if max(values) > 0 else 0,
        }
    
    def equalized_odds(self) -> Dict:
        """Separation metrics."""
        tpr, fpr = {}, {}
        
        for group in self.groups:
            mask = self.protected == group
            
            pos_mask = (mask) & (self.actual == 1)
            neg_mask = (mask) & (self.actual == 0)
            
            tpr[str(group)] = self.predictions[pos_mask].mean() if pos_mask.sum() > 0 else 0
            fpr[str(group)] = self.predictions[neg_mask].mean() if neg_mask.sum() > 0 else 0
        
        tpr_vals = list(tpr.values())
        fpr_vals = list(fpr.values())
        
        return {
            'tpr': tpr,
            'fpr': fpr,
            'tpr_difference': max(tpr_vals) - min(tpr_vals),
            'fpr_difference': max(fpr_vals) - min(fpr_vals),
            'equalized_odds_difference': max(
                max(tpr_vals) - min(tpr_vals),
                max(fpr_vals) - min(fpr_vals)
            ),
        }
    
    def equal_opportunity(self) -> Dict:
        """TPR parity only."""
        eo = self.equalized_odds()
        return {
            'tpr': eo['tpr'],
            'difference': eo['tpr_difference'],
        }
    
    def predictive_parity(self) -> Dict:
        """Sufficiency metrics."""
        ppv = {}
        
        for group in self.groups:
            mask = self.protected == group
            pred_pos = (mask) & (self.predictions == 1)
            
            if pred_pos.sum() > 0:
                ppv[str(group)] = self.actual[pred_pos].mean()
            else:
                ppv[str(group)] = 0
        
        values = list(ppv.values())
        return {
            'ppv': ppv,
            'difference': max(values) - min(values),
        }
    
    def accuracy_by_group(self) -> Dict:
        """Accuracy metrics per group."""
        acc = {}
        for group in self.groups:
            mask = self.protected == group
            correct = self.predictions[mask] == self.actual[mask]
            acc[str(group)] = correct.mean()
        
        return {
            'accuracy': acc,
            'difference': max(acc.values()) - min(acc.values()),
        }
    
    def generate_report(self) -> str:
        """Generate comprehensive fairness report."""
        metrics = self.all_metrics()
        
        report = """
╔══════════════════════════════════════════════════════════════════════════╗
║                       FAIRNESS METRICS REPORT                             ║
╠══════════════════════════════════════════════════════════════════════════╣

"""
        # Demographic Parity
        dp = metrics['demographic_parity']
        report += "DEMOGRAPHIC PARITY (Statistical Parity)\n"
        report += "─" * 40 + "\n"
        report += "Definition: Equal positive prediction rates across groups\n\n"
        for group, rate in dp['selection_rates'].items():
            report += f"  {group}: {rate:.1%}\n"
        report += f"\n  Difference: {dp['difference']:.3f}"
        report += f" ({'PASS' if dp['difference'] < 0.05 else 'FAIL'} at 0.05 threshold)\n"
        report += f"  Ratio: {dp['ratio']:.3f}"
        report += f" ({'PASS' if dp['ratio'] >= 0.8 else 'FAIL'} at 0.80 threshold)\n\n"
        
        # Equalized Odds
        eo = metrics['equalized_odds']
        report += "EQUALIZED ODDS (Separation)\n"
        report += "─" * 40 + "\n"
        report += "Definition: Equal TPR and FPR across groups\n\n"
        report += "  True Positive Rates:\n"
        for group, rate in eo['tpr'].items():
            report += f"    {group}: {rate:.1%}\n"
        report += f"  TPR Difference: {eo['tpr_difference']:.3f}\n\n"
        report += "  False Positive Rates:\n"
        for group, rate in eo['fpr'].items():
            report += f"    {group}: {rate:.1%}\n"
        report += f"  FPR Difference: {eo['fpr_difference']:.3f}\n\n"
        
        # Predictive Parity
        pp = metrics['predictive_parity']
        report += "PREDICTIVE PARITY (Sufficiency)\n"
        report += "─" * 40 + "\n"
        report += "Definition: Equal precision across groups\n\n"
        for group, rate in pp['ppv'].items():
            report += f"  {group}: {rate:.1%}\n"
        report += f"\n  Difference: {pp['difference']:.3f}\n\n"
        
        # Overall Assessment
        report += "═" * 40 + "\n"
        report += "OVERALL ASSESSMENT\n"
        report += "═" * 40 + "\n"
        
        issues = []
        if dp['difference'] >= 0.05:
            issues.append("Demographic parity violation")
        if eo['tpr_difference'] >= 0.05:
            issues.append("TPR disparity (equal opportunity violation)")
        if eo['fpr_difference'] >= 0.05:
            issues.append("FPR disparity")
        if pp['difference'] >= 0.05:
            issues.append("Predictive parity violation")
        
        if issues:
            report += "\n⚠️  FAIRNESS CONCERNS:\n"
            for issue in issues:
                report += f"   • {issue}\n"
        else:
            report += "\n✓ No major fairness violations detected\n"
        
        return report


# Utility function for quick checks
def quick_fairness_check(model, X_test, y_test, protected_column):
    """
    Quick fairness assessment for a model.
    """
    predictions = model.predict(X_test)
    protected = X_test[protected_column] if isinstance(X_test, pd.DataFrame) else protected_column
    
    calc = FairnessMetricsCalculator(predictions, y_test, protected)
    return calc.generate_report()
```

### Reporting Fairness Results

```
Effective Fairness Reporting:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  FOR TECHNICAL AUDIENCES:                                               │
│  ─────────────────────────                                              │
│  • Include precise metric values                                        │
│  • Show confidence intervals                                            │
│  • Compare to baseline models                                           │
│  • Document methodology                                                 │
│                                                                          │
│  FOR BUSINESS STAKEHOLDERS:                                             │
│  ──────────────────────────                                             │
│  • Lead with key findings                                               │
│  • Use plain language                                                   │
│  • Visualize disparities                                                │
│  • Focus on business impact                                             │
│                                                                          │
│  FOR COMPLIANCE/LEGAL:                                                  │
│  ─────────────────────                                                  │
│  • Map metrics to regulations                                           │
│  • Document four-fifths rule compliance                                 │
│  • Show audit trail                                                     │
│  • Include remediation plans                                            │
│                                                                          │
│  ALWAYS INCLUDE:                                                        │
│  ──────────────                                                         │
│  • Protected attributes tested                                          │
│  • Sample sizes per group                                               │
│  • Thresholds used                                                      │
│  • Date of assessment                                                   │
│  • Model version                                                        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

## Code Example

Complete fairness testing workflow:

```python
"""
Complete Fairness Testing Workflow
"""

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier

def run_fairness_audit(model, X_test, y_test, protected_cols, 
                       thresholds=None):
    """
    Run complete fairness audit on a model.
    
    Args:
        model: Trained model
        X_test: Test features
        y_test: Test labels
        protected_cols: List of protected attribute columns
        thresholds: Dict of metric thresholds
    
    Returns:
        Audit results dictionary
    """
    if thresholds is None:
        thresholds = {
            'demographic_parity_difference': 0.05,
            'equalized_odds_difference': 0.05,
            'disparate_impact_ratio': 0.8,
        }
    
    predictions = model.predict(X_test)
    
    if hasattr(model, 'predict_proba'):
        probabilities = model.predict_proba(X_test)[:, 1]
    else:
        probabilities = None
    
    audit_results = {
        'thresholds': thresholds,
        'by_attribute': {},
        'overall_pass': True,
        'violations': []
    }
    
    for col in protected_cols:
        protected = X_test[col] if isinstance(X_test, pd.DataFrame) else X_test[:, col]
        
        calc = FairnessMetricsCalculator(
            predictions=predictions,
            actual=y_test,
            protected_attribute=protected,
            probabilities=probabilities
        )
        
        metrics = calc.all_metrics()
        
        # Check violations
        violations = []
        
        dp = metrics['demographic_parity']
        if dp['difference'] >= thresholds['demographic_parity_difference']:
            violations.append(f"Demographic parity: {dp['difference']:.3f}")
            audit_results['overall_pass'] = False
        
        if dp['ratio'] < thresholds['disparate_impact_ratio']:
            violations.append(f"Disparate impact: {dp['ratio']:.3f}")
            audit_results['overall_pass'] = False
        
        eo = metrics['equalized_odds']
        if eo['equalized_odds_difference'] >= thresholds['equalized_odds_difference']:
            violations.append(f"Equalized odds: {eo['equalized_odds_difference']:.3f}")
            audit_results['overall_pass'] = False
        
        audit_results['by_attribute'][col] = {
            'metrics': metrics,
            'violations': violations,
            'pass': len(violations) == 0
        }
        audit_results['violations'].extend(
            [f"{col}: {v}" for v in violations]
        )
    
    return audit_results


def print_audit_summary(audit_results):
    """Print human-readable audit summary."""
    print("\n" + "=" * 60)
    print("FAIRNESS AUDIT SUMMARY")
    print("=" * 60)
    
    print(f"\nOverall Status: {'✓ PASS' if audit_results['overall_pass'] else '✗ FAIL'}")
    
    if audit_results['violations']:
        print(f"\nViolations Found ({len(audit_results['violations'])}):")
        for v in audit_results['violations']:
            print(f"  • {v}")
    
    print("\nBy Protected Attribute:")
    for attr, data in audit_results['by_attribute'].items():
        status = '✓' if data['pass'] else '✗'
        print(f"\n  {attr}: {status}")
        
        dp = data['metrics']['demographic_parity']
        print(f"    Selection rates: {dp['selection_rates']}")
        print(f"    Difference: {dp['difference']:.3f}, Ratio: {dp['ratio']:.3f}")


# Example usage
if __name__ == "__main__":
    # Create sample data
    np.random.seed(42)
    n = 2000
    
    data = pd.DataFrame({
        'income': np.random.normal(50000, 15000, n),
        'credit_score': np.random.normal(700, 50, n),
        'age': np.random.normal(40, 10, n),
        'gender': np.random.choice(['male', 'female'], n, p=[0.6, 0.4]),
    })
    
    # Create biased target (for demonstration)
    data['approved'] = (
        (data['income'] > 45000).astype(int) +
        (data['credit_score'] > 680).astype(int) +
        (data['gender'] == 'male').astype(int) * 0.5  # Bias
    ) > 1.5
    data['approved'] = data['approved'].astype(int)
    
    # Split and train
    X = data.drop('approved', axis=1)
    y = data['approved']
    
    # Encode gender for model
    X_encoded = X.copy()
    X_encoded['gender'] = (X['gender'] == 'male').astype(int)
    
    X_train, X_test, y_train, y_test = train_test_split(
        X_encoded, y, test_size=0.3, random_state=42
    )
    
    model = RandomForestClassifier(random_state=42)
    model.fit(X_train, y_train)
    
    # Run fairness audit
    # Add original gender back for analysis
    X_test_with_gender = X_test.copy()
    X_test_with_gender['gender_original'] = X.loc[X_test.index, 'gender']
    
    audit = run_fairness_audit(
        model=model,
        X_test=X_test_with_gender,
        y_test=y_test,
        protected_cols=['gender_original']
    )
    
    print_audit_summary(audit)
```

## Summary

- **Fairness is multifaceted:** Multiple valid definitions exist (demographic parity, equalized odds, predictive parity)
- **Metrics are context-dependent:** Choose based on domain requirements and stakeholder priorities
- **Impossibility theorem:** You cannot satisfy all fairness criteria simultaneously when base rates differ
- **Key metrics:**
  - Demographic Parity: Equal selection rates
  - Equalized Odds: Equal TPR and FPR
  - Equal Opportunity: Equal TPR only
  - Predictive Parity: Equal precision
- **Implementation:** Systematic calculation and comparison across protected groups
- **Reporting:** Tailor to audience—technical details for engineers, implications for business

## Additional Resources

- [Fairlearn Documentation](https://fairlearn.org/main/user_guide/fairness_in_machine_learning.html)
- [AI Fairness 360 Metrics](https://aif360.readthedocs.io/en/latest/modules/metrics.html)
- [Fairness Definitions Explained (Verma & Rubin)](https://fairware.cs.umass.edu/papers/Verma.pdf)

