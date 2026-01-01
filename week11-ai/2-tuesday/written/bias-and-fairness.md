# Bias and Fairness in AI Systems

## Learning Objectives

- Understand what AI bias is and its sources
- Identify different types of bias in machine learning systems
- Recognize fairness concepts and protected attributes
- Understand disparate impact and its measurement
- Navigate ethical implications and regulatory requirements
- Identify bias in testing tools and processes

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

When Amazon's AI recruiting tool was found to systematically downgrade resumes containing the word "women's," it became a stark reminder that AI systems can perpetuate and amplify societal biases. When facial recognition systems fail disproportionately on darker-skinned faces, real people face wrongful accusations. When healthcare AI allocates fewer resources to Black patients due to biased training data, lives are at stake.

AI bias isn't a theoretical concern—it's a present danger with documented real-world harm. As quality engineers, testing for bias and fairness isn't optional; it's a professional and ethical responsibility. This module equips you to identify, measure, and mitigate bias in AI systems, ensuring the technology we build serves all users equitably.

## The Concept

### What is AI Bias?

**AI bias** refers to systematic errors in AI systems that create unfair outcomes, typically disadvantaging certain groups of people. Unlike random errors, bias is consistent and directional—it reliably produces worse outcomes for specific populations.

```
AI Bias Illustrated:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  UNBIASED MODEL:                    BIASED MODEL:                       │
│  ───────────────                    ─────────────                       │
│                                                                          │
│  Applicant Pool:                    Same Applicant Pool:                │
│  ○ ○ ○ ● ● ●                        ○ ○ ○ ● ● ●                         │
│  (○=Group A, ●=Group B)             (Equal qualifications)              │
│                                                                          │
│         │                                   │                            │
│         ▼                                   ▼                            │
│  Approved: ○ ● ○ ●                  Approved: ○ ○ ○ ●                   │
│  (Proportional selection)           (Group B under-selected)            │
│                                                                          │
│  Approval Rate:                     Approval Rate:                      │
│  Group A: 67%                       Group A: 100%                       │
│  Group B: 67%                       Group B: 33%                        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Sources of Bias

Bias can enter AI systems at every stage of development:

```
Bias Entry Points:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  1. DATA COLLECTION                                                     │
│     └─ Who is included/excluded in training data?                       │
│        Example: Medical AI trained on data primarily from one           │
│        demographic performs poorly on others                            │
│                                                                          │
│  2. DATA LABELING                                                       │
│     └─ How do human labelers' biases affect labels?                     │
│        Example: Labelers rate same resume differently based on          │
│        perceived gender from name                                        │
│                                                                          │
│  3. FEATURE SELECTION                                                   │
│     └─ Which attributes are used for prediction?                        │
│        Example: Using zip code as feature encodes racial segregation    │
│                                                                          │
│  4. ALGORITHM CHOICE                                                    │
│     └─ How does the model optimize, and for whom?                       │
│        Example: Optimizing for overall accuracy may sacrifice           │
│        minority group performance                                        │
│                                                                          │
│  5. DEPLOYMENT CONTEXT                                                  │
│     └─ How is the model used in practice?                               │
│        Example: Model designed for one context applied to another       │
│                                                                          │
│  6. FEEDBACK LOOPS                                                      │
│     └─ How do model predictions affect future data?                     │
│        Example: Predictive policing creates more arrests in             │
│        already-policed areas, reinforcing patterns                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Types of Bias

#### Selection Bias
Data doesn't represent the full population:
```markdown
Example: Training a voice assistant primarily on recordings from 
young adults results in poor performance for elderly users.

Testing Implication: Test with diverse demographic groups
```

#### Confirmation Bias
Looking for patterns that confirm existing beliefs:
```markdown
Example: A fraud detection system flags transactions from certain 
countries more often because investigators expect fraud from there, 
creating more labeled fraud examples from those countries.

Testing Implication: Audit label distribution across groups
```

#### Measurement Bias
Different accuracy in measuring attributes across groups:
```markdown
Example: Pulse oximeters less accurate on darker skin tones, 
leading to AI health monitors trained on this data having 
systematic errors for Black patients.

Testing Implication: Validate measurement accuracy across groups
```

#### Aggregation Bias
Single model applied to diverse subgroups inappropriately:
```markdown
Example: A diabetes risk model works well on average but fails 
for specific ethnic groups with different risk factors.

Testing Implication: Evaluate model performance on subgroups separately
```

#### Historical Bias
Data reflects past inequities:
```markdown
Example: Hiring AI trained on historical hiring decisions learns 
to prefer male candidates because men were historically preferred.

Testing Implication: Question whether historical patterns should be replicated
```

### Fairness in Machine Learning

**Fairness** means different things to different stakeholders. Several mathematical definitions exist, and importantly, they're often mutually exclusive—you can't satisfy all of them simultaneously.

#### Protected Attributes

These are characteristics for which discrimination is legally prohibited or ethically concerning:

```
Common Protected Attributes:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  LEGALLY PROTECTED (varies by jurisdiction):                            │
│  • Race / Ethnicity                                                     │
│  • Gender / Sex                                                         │
│  • Age                                                                  │
│  • Religion                                                             │
│  • National Origin                                                      │
│  • Disability Status                                                    │
│  • Genetic Information                                                  │
│                                                                          │
│  OFTEN ETHICALLY CONCERNING:                                            │
│  • Socioeconomic Status                                                 │
│  • Sexual Orientation                                                   │
│  • Geographic Location (as proxy)                                       │
│  • Education Level (as proxy)                                           │
│                                                                          │
│  PROXY VARIABLES (encode protected attributes indirectly):              │
│  • Zip Code → Race/Income                                               │
│  • Name → Gender/Ethnicity                                              │
│  • College Attended → Socioeconomic Status                              │
│  • Club Memberships → Gender                                            │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Disparate Impact

**Disparate impact** occurs when a seemingly neutral practice disproportionately affects a protected group, regardless of intent.

#### The Four-Fifths Rule

A common threshold: if the selection rate for a protected group is less than 80% (4/5) of the rate for the most-favored group, disparate impact may exist.

```
Disparate Impact Calculation:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  Loan Approval Rates:                                                   │
│  • Group A: 800 approved out of 1000 = 80%                              │
│  • Group B: 500 approved out of 1000 = 50%                              │
│                                                                          │
│  Disparate Impact Ratio = Rate_B / Rate_A                               │
│                        = 50% / 80%                                      │
│                        = 0.625 (62.5%)                                  │
│                                                                          │
│  Is 62.5% < 80%? YES                                                    │
│                                                                          │
│  CONCLUSION: Disparate impact exists against Group B                    │
│              (their approval rate is only 62.5% of Group A's)           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```python
def calculate_disparate_impact(group_a_selected, group_a_total,
                                group_b_selected, group_b_total):
    """
    Calculate disparate impact ratio.
    
    Returns:
        Ratio (< 0.8 may indicate disparate impact)
    """
    rate_a = group_a_selected / group_a_total
    rate_b = group_b_selected / group_b_total
    
    # Always divide smaller by larger to get ratio
    if rate_a > rate_b:
        return rate_b / rate_a, "Group B disadvantaged"
    else:
        return rate_a / rate_b, "Group A disadvantaged"

# Example
ratio, disadvantaged = calculate_disparate_impact(800, 1000, 500, 1000)
print(f"Disparate Impact Ratio: {ratio:.2%}")
print(f"Status: {disadvantaged}")
print(f"Four-Fifths Violation: {ratio < 0.8}")
```

### Fairness Definitions

Different mathematical definitions of fairness:

```
Fairness Definitions Comparison:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  DEMOGRAPHIC PARITY (Independence)                                      │
│  ─────────────────────────────────                                      │
│  P(Ŷ=1|A=0) = P(Ŷ=1|A=1)                                               │
│                                                                          │
│  "Equal proportion of positive predictions across groups"               │
│                                                                          │
│  Example: Same percentage of loan approvals regardless of race          │
│  Limitation: Ignores actual qualifications                              │
│                                                                          │
│  ───────────────────────────────────────────────────────────────────    │
│                                                                          │
│  EQUALIZED ODDS (Separation)                                            │
│  ──────────────────────────                                             │
│  P(Ŷ=1|A=0,Y=y) = P(Ŷ=1|A=1,Y=y) for y ∈ {0,1}                        │
│                                                                          │
│  "Same true positive rate AND false positive rate across groups"        │
│                                                                          │
│  Example: Equal accuracy in detecting fraud for all demographics        │
│  Limitation: Requires access to true labels                             │
│                                                                          │
│  ───────────────────────────────────────────────────────────────────    │
│                                                                          │
│  PREDICTIVE PARITY (Sufficiency)                                        │
│  ───────────────────────────────                                        │
│  P(Y=1|Ŷ=1,A=0) = P(Y=1|Ŷ=1,A=1)                                       │
│                                                                          │
│  "When model predicts positive, same likelihood of being correct"       │
│                                                                          │
│  Example: When flagged as high-risk, same actual default rate           │
│  Limitation: Can coexist with unequal treatment                         │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

**The Impossibility Theorem:** Except in special cases, it's mathematically impossible to satisfy demographic parity, equalized odds, and predictive parity simultaneously. Teams must choose which definition of fairness matters most for their context.

### Ethical Implications

AI bias isn't just a technical problem—it has profound ethical dimensions:

```
Ethical Considerations Framework:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  HARM ASSESSMENT                                                        │
│  ────────────────                                                       │
│  • Who is harmed by errors?                                             │
│  • What is the severity of harm?                                        │
│  • Are harms distributed equitably?                                     │
│  • Can individuals challenge decisions?                                 │
│                                                                          │
│  POWER DYNAMICS                                                         │
│  ──────────────                                                         │
│  • Who benefits from the system?                                        │
│  • Who bears the costs of errors?                                       │
│  • Do affected people have voice in system design?                      │
│                                                                          │
│  ALTERNATIVES                                                           │
│  ────────────                                                           │
│  • Is AI the right solution?                                            │
│  • What happens without AI involvement?                                 │
│  • Are there lower-stakes alternatives?                                 │
│                                                                          │
│  TRANSPARENCY                                                           │
│  ────────────                                                           │
│  • Do affected people know AI is involved?                              │
│  • Can decisions be explained?                                          │
│  • Is the system auditable?                                             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Regulatory Requirements

The regulatory landscape for AI fairness is evolving rapidly:

#### EU AI Act (2024)
```markdown
Key Requirements:
- High-risk AI systems must undergo conformity assessments
- Mandatory bias testing for systems affecting fundamental rights
- Human oversight requirements
- Documentation and logging obligations

High-Risk Categories:
- Employment and worker management
- Access to essential services (credit, insurance)
- Law enforcement
- Migration and border control
- Education and vocational training
```

#### US Regulations (Varies by Sector)
```markdown
Federal:
- Equal Credit Opportunity Act (credit decisions)
- Fair Housing Act (housing)
- Title VII (employment)
- ADA (disability discrimination)

State Level:
- NYC Local Law 144 (automated employment tools)
- Illinois BIPA (biometric data)
- California CPRA (profiling disclosures)
```

#### Industry Standards
```markdown
- IEEE 7010 (Wellbeing Impact Assessment)
- ISO/IEC TR 24028 (AI Trustworthiness)
- NIST AI Risk Management Framework
```

### Bias in Testing Tools

Even testing tools can contain bias:

```
Bias in Testing Infrastructure:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  TEST DATA BIAS                                                         │
│  ──────────────                                                         │
│  • Benchmark datasets may not represent all populations                 │
│  • Standard test suites may miss edge cases for minorities              │
│  • "Standard" users in test scenarios often default to majority         │
│                                                                          │
│  TOOLING BIAS                                                           │
│  ────────────                                                           │
│  • Synthetic data generators may embed assumptions                      │
│  • Test automation may not cover accessibility scenarios                │
│  • Performance testing may optimize for average, not worst-case         │
│                                                                          │
│  PROCESS BIAS                                                           │
│  ────────────                                                           │
│  • QA teams may lack diversity of perspective                           │
│  • Test plans may not explicitly include fairness criteria              │
│  • Bug triage may deprioritize issues affecting minorities              │
│                                                                          │
│  MITIGATION                                                             │
│  ──────────                                                             │
│  ✓ Include demographic analysis in test plans                           │
│  ✓ Use diverse test personas                                            │
│  ✓ Explicitly test for differential performance across groups           │
│  ✓ Include fairness in definition of done                               │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

## Code Example

```python
"""
Bias Detection and Fairness Assessment Tools
"""

import pandas as pd
import numpy as np
from typing import Dict, List, Tuple

class BiasDetector:
    """
    Tools for detecting bias in AI model predictions.
    """
    
    def __init__(self, predictions: pd.DataFrame, 
                 label_col: str,
                 prediction_col: str,
                 protected_attribute: str):
        """
        Args:
            predictions: DataFrame with actual labels and predictions
            label_col: Column name for true labels
            prediction_col: Column name for model predictions
            protected_attribute: Column name for protected attribute
        """
        self.df = predictions
        self.label_col = label_col
        self.pred_col = prediction_col
        self.protected = protected_attribute
        
    def selection_rates(self) -> Dict[str, float]:
        """Calculate positive prediction rates by group."""
        rates = {}
        for group in self.df[self.protected].unique():
            group_data = self.df[self.df[self.protected] == group]
            rate = group_data[self.pred_col].mean()
            rates[group] = rate
        return rates
    
    def disparate_impact_ratio(self) -> Tuple[float, str]:
        """
        Calculate disparate impact ratio.
        Returns (ratio, disadvantaged_group)
        """
        rates = self.selection_rates()
        groups = list(rates.keys())
        
        if len(groups) != 2:
            raise ValueError("Disparate impact requires exactly 2 groups")
        
        rate_0, rate_1 = rates[groups[0]], rates[groups[1]]
        
        if rate_0 > rate_1:
            return rate_1 / rate_0, groups[1]
        else:
            return rate_0 / rate_1, groups[0]
    
    def demographic_parity_difference(self) -> float:
        """
        Calculate demographic parity difference.
        |P(Ŷ=1|A=0) - P(Ŷ=1|A=1)|
        Closer to 0 is fairer.
        """
        rates = self.selection_rates()
        return abs(max(rates.values()) - min(rates.values()))
    
    def equalized_odds_difference(self) -> Dict[str, float]:
        """
        Calculate difference in TPR and FPR across groups.
        """
        results = {'tpr_difference': 0, 'fpr_difference': 0}
        
        tprs = {}
        fprs = {}
        
        for group in self.df[self.protected].unique():
            group_data = self.df[self.df[self.protected] == group]
            
            # True Positive Rate
            positives = group_data[group_data[self.label_col] == 1]
            if len(positives) > 0:
                tpr = positives[self.pred_col].mean()
            else:
                tpr = 0
            tprs[group] = tpr
            
            # False Positive Rate
            negatives = group_data[group_data[self.label_col] == 0]
            if len(negatives) > 0:
                fpr = negatives[self.pred_col].mean()
            else:
                fpr = 0
            fprs[group] = fpr
        
        results['tpr_difference'] = max(tprs.values()) - min(tprs.values())
        results['fpr_difference'] = max(fprs.values()) - min(fprs.values())
        results['tprs'] = tprs
        results['fprs'] = fprs
        
        return results
    
    def generate_fairness_report(self) -> str:
        """Generate comprehensive fairness report."""
        rates = self.selection_rates()
        di_ratio, disadvantaged = self.disparate_impact_ratio()
        dp_diff = self.demographic_parity_difference()
        eo_diff = self.equalized_odds_difference()
        
        report = f"""
╔══════════════════════════════════════════════════════════════════════╗
║                      FAIRNESS ASSESSMENT REPORT                       ║
╠══════════════════════════════════════════════════════════════════════╣
║  Protected Attribute: {self.protected:<42} ║
╠══════════════════════════════════════════════════════════════════════╣
║                                                                       ║
║  SELECTION RATES BY GROUP:                                           ║
"""
        for group, rate in rates.items():
            report += f"║    • {group}: {rate:.1%}{'':>50}║\n"
        
        report += f"""║                                                                       ║
║  DISPARATE IMPACT ANALYSIS:                                          ║
║    • Ratio: {di_ratio:.3f}                                                    ║
║    • Threshold: 0.800 (4/5 rule)                                     ║
║    • Status: {'⚠️  POTENTIAL VIOLATION' if di_ratio < 0.8 else '✓  PASSES'}                                   ║
║    • Disadvantaged Group: {disadvantaged:<40} ║
║                                                                       ║
║  DEMOGRAPHIC PARITY:                                                 ║
║    • Difference: {dp_diff:.3f} (closer to 0 is fairer)                       ║
║                                                                       ║
║  EQUALIZED ODDS:                                                     ║
║    • TPR Difference: {eo_diff['tpr_difference']:.3f}                                           ║
║    • FPR Difference: {eo_diff['fpr_difference']:.3f}                                           ║
"""
        for group, tpr in eo_diff['tprs'].items():
            fpr = eo_diff['fprs'][group]
            report += f"║      {group}: TPR={tpr:.3f}, FPR={fpr:.3f}{'':>30}║\n"
        
        report += """║                                                                       ║
╚══════════════════════════════════════════════════════════════════════╝
"""
        return report


# Example usage
if __name__ == "__main__":
    # Create sample data
    np.random.seed(42)
    
    # Simulated loan approval data with bias
    n = 1000
    data = pd.DataFrame({
        'gender': np.random.choice(['male', 'female'], n),
        'actual_qualified': np.random.choice([0, 1], n, p=[0.3, 0.7]),
    })
    
    # Introduce bias: females approved at lower rate
    data['approved'] = data.apply(
        lambda row: row['actual_qualified'] if row['gender'] == 'male'
        else np.random.choice([0, 1], p=[0.3, 0.7]) if row['actual_qualified'] == 1
        else 0,
        axis=1
    )
    
    # Run bias detection
    detector = BiasDetector(
        predictions=data,
        label_col='actual_qualified',
        prediction_col='approved',
        protected_attribute='gender'
    )
    
    print(detector.generate_fairness_report())
```

## Summary

- **AI bias** creates systematic unfair outcomes, often disadvantaging marginalized groups
- **Sources of bias** include data collection, labeling, features, algorithms, deployment, and feedback loops
- **Types of bias:** Selection, confirmation, measurement, aggregation, historical
- **Protected attributes** include race, gender, age—plus proxy variables that encode them
- **Disparate impact** measures differential outcomes; four-fifths rule is common threshold
- **Fairness definitions** are mathematically incompatible—teams must choose priorities
- **Regulatory requirements** are expanding rapidly (EU AI Act, NYC LL 144)
- **Testing tools themselves** can contain bias—include fairness in QA processes

## Additional Resources

- [AI Fairness 360 (IBM)](https://aif360.mybluemix.net/)
- [Fairlearn (Microsoft)](https://fairlearn.org/)
- [EU AI Act Text](https://artificialintelligenceact.eu/)
- [Algorithmic Justice League](https://www.ajl.org/)

