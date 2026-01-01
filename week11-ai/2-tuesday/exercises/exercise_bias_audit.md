# Exercise: AI Bias Audit

## Overview

| Attribute | Value |
|-----------|-------|
| **Duration** | 60 minutes |
| **Difficulty** | Intermediate to Advanced |
| **Mode** | Individual (Mode A: Implementation) |
| **Prerequisites** | Read bias-and-fairness.md, fairness-metrics.md |

## Learning Objectives

By completing this exercise, you will be able to:
- Conduct a systematic bias audit on an AI/ML dataset
- Calculate and interpret key fairness metrics
- Identify sources of potential bias
- Document findings and recommend remediation strategies

## The Scenario

**LendRight Financial** is a fintech company that uses an ML model to predict loan approval. Before deploying a new version of their model, they need a bias audit to ensure compliance with fair lending regulations.

You've been given prediction data from the model's validation set and must assess whether the model exhibits discriminatory behavior against protected groups.

---

## Part 1: Dataset Exploration - 15 minutes

### The Dataset

Create this dataset in Python for your analysis:

```python
import pandas as pd
import numpy as np

# Set seed for reproducibility
np.random.seed(42)

# Generate synthetic loan data with built-in bias
n = 2000

# Demographic attributes
gender = np.random.choice(['male', 'female'], n, p=[0.55, 0.45])
age_group = np.random.choice(['18-30', '31-50', '51+'], n, p=[0.25, 0.50, 0.25])
ethnicity = np.random.choice(['group_A', 'group_B', 'group_C'], n, p=[0.60, 0.25, 0.15])

# Financial attributes
income = np.random.normal(65000, 25000, n).clip(20000, 200000)
credit_score = np.random.normal(700, 60, n).clip(500, 850)
debt_to_income = np.random.uniform(0.1, 0.6, n)
employment_years = np.random.exponential(6, n).clip(0, 35)

# True qualification (based on financial factors)
qualification_score = (
    (income - 20000) / 180000 * 0.30 +
    (credit_score - 500) / 350 * 0.35 +
    (1 - debt_to_income) * 0.20 +
    np.minimum(employment_years / 10, 1) * 0.15
)
actually_qualified = (qualification_score > 0.5).astype(int)

# Model predictions WITH BIAS built in
# Bias: Reduce approval probability for females and group_B
bias_factor = np.zeros(n)
bias_factor[gender == 'female'] -= 0.10
bias_factor[ethnicity == 'group_B'] -= 0.12
bias_factor[age_group == '18-30'] -= 0.05

approval_prob = qualification_score + bias_factor
model_approved = (np.random.random(n) < approval_prob.clip(0, 1)).astype(int)

# Create DataFrame
df = pd.DataFrame({
    'applicant_id': range(1, n + 1),
    'gender': gender,
    'age_group': age_group,
    'ethnicity': ethnicity,
    'income': income.round(2),
    'credit_score': credit_score.round(0),
    'debt_to_income': debt_to_income.round(3),
    'employment_years': employment_years.round(1),
    'actually_qualified': actually_qualified,
    'model_approved': model_approved
})

print(df.head(10))
print(f"\nDataset shape: {df.shape}")
print(f"\nOverall approval rate: {df['model_approved'].mean():.1%}")
```

### Task 1.1: Data Profiling

Run the code above and complete this profiling:

**Demographics Summary:**

| Attribute | Categories | Distribution |
|-----------|------------|--------------|
| Gender | | |
| Age Group | | |
| Ethnicity | | |

**Overall Statistics:**

| Metric | Value |
|--------|-------|
| Total applicants | |
| Overall approval rate | |
| Qualification rate | |

### Task 1.2: Initial Observations

Before calculating formal metrics, examine approval rates by group:

```python
# Calculate approval rates by protected attribute
print("Approval rates by gender:")
print(df.groupby('gender')['model_approved'].mean())

print("\nApproval rates by age group:")
print(df.groupby('age_group')['model_approved'].mean())

print("\nApproval rates by ethnicity:")
print(df.groupby('ethnicity')['model_approved'].mean())
```

**Record Your Observations:**

| Protected Attribute | Group | Approval Rate | Notable? |
|---------------------|-------|---------------|----------|
| Gender | Male | | |
| Gender | Female | | |
| Age | 18-30 | | |
| Age | 31-50 | | |
| Age | 51+ | | |
| Ethnicity | Group A | | |
| Ethnicity | Group B | | |
| Ethnicity | Group C | | |

**Initial concerns identified:**
```
[What patterns do you see that warrant deeper investigation?]
```

---

## Part 2: Fairness Metrics Calculation - 20 minutes

### Task 2.1: Implement Fairness Metrics

Create functions to calculate the following metrics:

```python
def calculate_selection_rates(df, protected_attr, outcome_col='model_approved'):
    """Calculate positive outcome rate for each group."""
    # YOUR CODE HERE
    pass

def calculate_disparate_impact(df, protected_attr, outcome_col='model_approved'):
    """
    Calculate disparate impact ratio.
    Returns: (ratio, advantaged_group, disadvantaged_group)
    """
    # YOUR CODE HERE
    pass

def calculate_demographic_parity_diff(df, protected_attr, outcome_col='model_approved'):
    """
    Calculate demographic parity difference.
    Returns: max(rate) - min(rate)
    """
    # YOUR CODE HERE
    pass

def calculate_equalized_odds(df, protected_attr, prediction_col='model_approved', 
                              label_col='actually_qualified'):
    """
    Calculate TPR and FPR differences across groups.
    Returns: dict with TPR_diff, FPR_diff, and per-group values
    """
    # YOUR CODE HERE
    pass
```

### Task 2.2: Calculate Metrics for Each Protected Attribute

Complete this metrics table:

**Gender Analysis:**

| Metric | Value | Threshold | Status |
|--------|-------|-----------|--------|
| Disparate Impact Ratio | | >= 0.80 | |
| Demographic Parity Diff | | <= 0.10 | |
| TPR Difference | | <= 0.10 | |
| FPR Difference | | <= 0.10 | |

**Age Group Analysis:**

| Metric | Value | Threshold | Status |
|--------|-------|-----------|--------|
| Disparate Impact Ratio | | >= 0.80 | |
| Demographic Parity Diff | | <= 0.10 | |
| TPR Difference | | <= 0.10 | |
| FPR Difference | | <= 0.10 | |

**Ethnicity Analysis:**

| Metric | Value | Threshold | Status |
|--------|-------|-----------|--------|
| Disparate Impact Ratio | | >= 0.80 | |
| Demographic Parity Diff | | <= 0.10 | |
| TPR Difference | | <= 0.10 | |
| FPR Difference | | <= 0.10 | |

### Task 2.3: Interpretation

For each FAILING metric, explain:

```markdown
## Gender - [Metric Name]
- **Value:** [X]
- **What this means:** [Plain language explanation]
- **Who is harmed:** [Specific group]
- **Magnitude of harm:** [Quantify the impact]

## [Continue for other failing metrics]
```

---

## Part 3: Root Cause Investigation - 15 minutes

### Task 3.1: Correlation Analysis

Investigate whether protected attributes correlate with financial factors:

```python
# Check if financial attributes differ by protected groups
print("Average income by gender:")
print(df.groupby('gender')['income'].mean())

print("\nAverage credit score by ethnicity:")
print(df.groupby('ethnicity')['credit_score'].mean())

# Add more analyses as needed
```

**Findings:**

| Financial Factor | Varies by Protected Attribute? | Correlation |
|------------------|-------------------------------|-------------|
| Income | | |
| Credit Score | | |
| Debt-to-Income | | |
| Employment Years | | |

### Task 3.2: Proxy Variable Analysis

Determine if bias could be entering through proxy variables:

```
1. Could income be a proxy for [protected attribute]?
   Evidence: [Your analysis]

2. Could employment years be a proxy for age?
   Evidence: [Your analysis]

3. Are there other potential proxy effects?
   [Your analysis]
```

### Task 3.3: Qualified vs Approved Analysis

Compare qualification rates to approval rates:

```python
# For qualified applicants, what's the approval rate by group?
qualified = df[df['actually_qualified'] == 1]
print("Among qualified applicants, approval rate by gender:")
print(qualified.groupby('gender')['model_approved'].mean())
```

**Key Finding:**

| Group | Qualification Rate | Approval Rate | Gap |
|-------|-------------------|---------------|-----|
| | | | |
| | | | |

This tells us:
```
[What does the gap indicate about the source of bias?]
```

---

## Part 4: Audit Report - 10 minutes

### Complete Audit Report Template

```markdown
# AI Fairness Audit Report
## LendRight Financial - Loan Approval Model v2.0

### Executive Summary

**Audit Date:** [Today's date]
**Dataset Size:** [N] applicants
**Model Purpose:** Predict loan approval likelihood

**Overall Finding:** [PASS / FAIL / CONDITIONAL PASS]

**Critical Issues Identified:**
1. [Issue 1]
2. [Issue 2]
3. [Issue 3]

---

### Methodology

Protected attributes examined:
- [ ] Gender
- [ ] Age Group  
- [ ] Ethnicity

Fairness metrics calculated:
- [ ] Disparate Impact Ratio (4/5 rule)
- [ ] Demographic Parity Difference
- [ ] Equalized Odds (TPR/FPR parity)

Thresholds applied:
- Disparate Impact: >= 0.80
- Parity Differences: <= 0.10

---

### Detailed Findings

#### Finding 1: [Title]

| Attribute | Metric | Value | Status |
|-----------|--------|-------|--------|
| | | | |

**Impact:** [Who is affected and how]

**Root Cause Hypothesis:** [What's causing this]

**Evidence:** [Data supporting your hypothesis]

---

#### Finding 2: [Title]
[Same structure]

---

### Regulatory Compliance Assessment

| Regulation | Requirement | Status | Notes |
|------------|-------------|--------|-------|
| ECOA | No discrimination by protected class | | |
| Fair Housing | Fair lending standards | | |
| State Laws | [Applicable state regulations] | | |

---

### Recommendations

#### Immediate Actions (Before Deployment)

1. **[Action 1]**
   - Priority: Critical
   - Owner: [Team]
   - Timeline: [When]

2. **[Action 2]**
   - Priority: High
   - Owner: [Team]
   - Timeline: [When]

#### Long-Term Improvements

1. [Improvement 1]
2. [Improvement 2]
3. [Improvement 3]

---

### Appendix: Raw Metrics

[Include all calculated metrics tables]

---

### Sign-Off

Audit conducted by: [Your name]
Review required by: [Role]
Approval status: PENDING
```

---

## Deliverables

1. **Python code** implementing all fairness metrics
2. **Completed metrics tables** for all protected attributes
3. **Root cause analysis** with evidence
4. **Formal audit report** following the template

## Definition of Done

- [ ] Dataset loaded and profiled correctly
- [ ] Implemented all 4 fairness metric calculations
- [ ] Calculated metrics for gender, age, and ethnicity
- [ ] Identified at least 2 failing metrics with explanations
- [ ] Investigated root causes with supporting analysis
- [ ] Completed audit report with recommendations
- [ ] Code runs without errors

## Evaluation Rubric

| Criteria | Excellent (4) | Good (3) | Satisfactory (2) | Needs Work (1) |
|----------|---------------|----------|------------------|----------------|
| Metric Implementation | All metrics correct | Minor errors | Some metrics wrong | Major errors |
| Analysis Depth | Thorough investigation | Good analysis | Surface analysis | Minimal |
| Root Cause | Strong evidence-based hypothesis | Reasonable hypothesis | Basic hypothesis | No investigation |
| Report Quality | Professional, actionable | Complete | Basic | Incomplete |

## Starter Code Solution (Reference)

<details>
<summary>Click to reveal solution code (try first!)</summary>

```python
def calculate_selection_rates(df, protected_attr, outcome_col='model_approved'):
    return df.groupby(protected_attr)[outcome_col].mean().to_dict()

def calculate_disparate_impact(df, protected_attr, outcome_col='model_approved'):
    rates = calculate_selection_rates(df, protected_attr, outcome_col)
    max_rate = max(rates.values())
    min_rate = min(rates.values())
    
    advantaged = [k for k, v in rates.items() if v == max_rate][0]
    disadvantaged = [k for k, v in rates.items() if v == min_rate][0]
    
    ratio = min_rate / max_rate if max_rate > 0 else 0
    return ratio, advantaged, disadvantaged

def calculate_demographic_parity_diff(df, protected_attr, outcome_col='model_approved'):
    rates = calculate_selection_rates(df, protected_attr, outcome_col)
    return max(rates.values()) - min(rates.values())

def calculate_equalized_odds(df, protected_attr, prediction_col='model_approved', 
                              label_col='actually_qualified'):
    results = {'tpr': {}, 'fpr': {}}
    
    for group in df[protected_attr].unique():
        group_data = df[df[protected_attr] == group]
        
        # True Positive Rate
        positives = group_data[group_data[label_col] == 1]
        if len(positives) > 0:
            results['tpr'][group] = positives[prediction_col].mean()
        
        # False Positive Rate
        negatives = group_data[group_data[label_col] == 0]
        if len(negatives) > 0:
            results['fpr'][group] = negatives[prediction_col].mean()
    
    results['tpr_diff'] = max(results['tpr'].values()) - min(results['tpr'].values())
    results['fpr_diff'] = max(results['fpr'].values()) - min(results['fpr'].values())
    
    return results
```

</details>

## Extension Challenge

If you finish early:

1. **Intersectional Analysis:** Calculate metrics for combinations (e.g., female + group_B)
2. **Visualization:** Create charts showing approval rate disparities
3. **Mitigation Simulation:** Implement a threshold adjustment and recalculate metrics


