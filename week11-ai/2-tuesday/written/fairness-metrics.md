# Fairness Metrics

## Learning Objectives

- Understand different mathematical definitions of fairness
- Calculate and interpret key fairness metrics
- Recognize trade-offs between different fairness criteria
- Choose appropriate metrics for different contexts
- Implement fairness metrics in testing workflows
- Communicate fairness results to stakeholders

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

When you tell a room full of people to build a "fair" AI system, you'll get different interpretations. One person thinks fairness means equal approval rates across groups. Another thinks it means equal accuracy. A third thinks it means treating similar individuals similarly regardless of group membership.

**Here's the problem: These definitions are mathematically incompatible.** You usually can't satisfy all of them at once. Teams must choose which definition matters most for their context—and that choice has real consequences.

Understanding fairness metrics isn't just academic. Regulators are increasingly requiring fairness assessments. Lawsuits are being filed over algorithmic discrimination. As a quality engineer, you need to know what you're measuring, what it means, and why trade-offs exist.

## The Concept

### Why Multiple Fairness Metrics Exist

Different stakeholders care about different aspects of fairness:

**The Applicant's Perspective:**
"I should get the same decision as someone with my qualifications from a different demographic group."
→ Focuses on **individual fairness** and **equalized odds**

**The Lender's Perspective:**
"When I approve a loan, I need it to be equally likely to be repaid regardless of who the borrower is."
→ Focuses on **predictive parity**

**The Regulator's Perspective:**
"Outcomes shouldn't systematically differ by protected class."
→ Focuses on **demographic parity** and **disparate impact**

**The Civil Rights Perspective:**
"Historical discrimination means equal treatment today doesn't produce equal outcomes. We need affirmative action."
→ Focuses on **demographic parity** even if qualifications differ

Each perspective leads to different mathematical definitions of fairness.

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

### The Main Fairness Metrics

Let's walk through each major metric with clear explanations and examples.

#### 1. Demographic Parity (Statistical Parity)

**The idea:** The percentage of positive outcomes should be equal across groups.

**Mathematical definition:** P(Positive Prediction | Group A) = P(Positive Prediction | Group B)

**In plain English:** "50% of men and 50% of women should be approved."

**Example:**
```
Loan approval rates:
- Men: 600 approved out of 1000 = 60%
- Women: 540 approved out of 900 = 60%

Demographic parity satisfied? YES (both 60%)
```

**When it makes sense:** When you believe historical qualifications themselves reflect past discrimination, so you want equal outcomes regardless of current qualifications.

**Limitation:** This metric ignores whether approvals are actually correct. If 80% of men actually repay their loans but only 50% of women do (perhaps due to genuine economic differences), approving equal percentages gives men and women different accuracy—which some would call unfair.

**Simple calculation:**

```python
def demographic_parity(decisions, protected_attr):
    """
    Calculate demographic parity difference.
    
    Returns: Difference in positive rates between groups.
             0 = perfect demographic parity
             Larger values = more disparity
    """
    groups = {}
    
    for d in decisions:
        group = d[protected_attr]
        if group not in groups:
            groups[group] = {"positive": 0, "total": 0}
        groups[group]["total"] += 1
        if d["outcome"] == 1:  # Positive outcome
            groups[group]["positive"] += 1
    
    rates = {g: c["positive"]/c["total"] for g, c in groups.items()}
    difference = max(rates.values()) - min(rates.values())
    
    return {"rates": rates, "difference": difference}
```

#### 2. Equalized Odds

**The idea:** The model should be equally accurate for all groups—same true positive rate (TPR) AND same false positive rate (FPR).

**Mathematical definition:**
- P(Predict Yes | Actually Yes, Group A) = P(Predict Yes | Actually Yes, Group B) [Equal TPR]
- P(Predict Yes | Actually No, Group A) = P(Predict Yes | Actually No, Group B) [Equal FPR]

**In plain English:** "Among people who will repay, we should approve the same percentage from each group. Among people who will default, we should mistakenly approve the same percentage from each group."

**Example:**
```
Among people who actually repaid:
- Men approved: 90%  (TPR for men)
- Women approved: 90%  (TPR for women)
✓ Equal TPR

Among people who actually defaulted:
- Men approved: 10%  (FPR for men)
- Women approved: 10%  (FPR for women)
✓ Equal FPR

Equalized odds satisfied? YES
```

**When it makes sense:** When you have reliable ground truth labels and want the model to be equally accurate for all groups.

**Limitation:** Requires knowing the actual correct answer, which may not be available or may itself be biased.

#### 3. Equal Opportunity

**The idea:** A relaxed version of equalized odds—only the true positive rate needs to be equal.

**Mathematical definition:** P(Predict Yes | Actually Yes, Group A) = P(Predict Yes | Actually Yes, Group B)

**In plain English:** "Among qualified people, we should approve the same percentage from each group."

**Why relax the FPR requirement?** Sometimes the cost of false positives is low (e.g., showing an ad to someone not interested), while the cost of false negatives is high (e.g., denying a loan to someone who would have repaid). In such cases, you might only care about equal opportunity for qualified people.

#### 4. Predictive Parity

**The idea:** When the model says "yes," it should be equally likely to be correct for all groups.

**Mathematical definition:** P(Actually Yes | Predict Yes, Group A) = P(Actually Yes | Predict Yes, Group B)

**In plain English:** "Among people I approve, the same percentage of men and women should actually repay."

**Example:**
```
Among approved applicants:
- Men who repaid: 85%
- Women who repaid: 85%

Predictive parity satisfied? YES
```

**When it makes sense:** When positive predictions lead to consequences that need equal justification (e.g., arresting people for predicted crimes—you want arrests to be equally valid).

**Limitation:** Can coexist with very different treatment of groups. If you approve 80% of men but only 40% of equally qualified women, predictive parity can still hold if both groups have 85% repayment among approvals.

#### 5. Calibration

**The idea:** When the model predicts "70% likely," the actual rate should be 70%—for all groups.

**In plain English:** "My confidence scores should mean the same thing for everyone."

**Example:**
```
Among predictions with 70% confidence:
- Men: 70% actually positive ✓
- Women: 70% actually positive ✓

Among predictions with 90% confidence:
- Men: 90% actually positive ✓
- Women: 90% actually positive ✓

Calibration satisfied? YES
```

**When it makes sense:** When you're using probability scores for downstream decisions and need them to be reliable across groups.

### The Impossibility Theorem

**Critical insight:** Except in special cases (when base rates are identical across groups), it is mathematically impossible to satisfy demographic parity, equalized odds, and predictive parity simultaneously.

**Why?** Consider a scenario:
- Group A: 80% actually qualified (base rate 0.8)
- Group B: 50% actually qualified (base rate 0.5)

If you want equal approval rates (demographic parity), you must approve some unqualified people from Group B or reject some qualified people from Group A. This breaks equal accuracy (equalized odds).

If you want equal accuracy (equalized odds), the different base rates mean you'll approve more from Group A—breaking demographic parity.

**Practical implication:** You must choose which fairness criterion matters most for your context.

### Choosing the Right Metric

| Context | Often Prioritize | Reasoning |
|---------|------------------|-----------|
| Criminal justice | Equalized Odds | Error rates should be equal across demographics |
| Hiring with diversity goals | Demographic Parity | Address historical underrepresentation |
| Healthcare diagnosis | Equal Opportunity | Sick people should have equal chance of diagnosis |
| Lending | Predictive Parity | Business model requires equally valid approvals |
| Advertising | Calibration | Probability scores drive bidding decisions |

### Implementing Fairness Metrics

Here's a practical example of calculating multiple metrics:

```python
"""
Fairness Metrics Calculator
"""

def calculate_all_metrics(predictions, actual, protected):
    """
    Calculate key fairness metrics.
    
    Args:
        predictions: List of model predictions (0 or 1)
        actual: List of actual outcomes (0 or 1)
        protected: List of group labels (e.g., "A" or "B")
    
    Returns:
        Dict with all fairness metrics
    """
    # Group data
    groups = set(protected)
    
    # Calculate rates for each group
    metrics = {}
    for group in groups:
        # Get indices for this group
        idx = [i for i, p in enumerate(protected) if p == group]
        
        group_pred = [predictions[i] for i in idx]
        group_actual = [actual[i] for i in idx]
        
        # Selection rate (for demographic parity)
        selection_rate = sum(group_pred) / len(group_pred)
        
        # True positive rate (TPR)
        positives = [i for i in idx if actual[i] == 1]
        if positives:
            tpr = sum(predictions[i] for i in positives) / len(positives)
        else:
            tpr = 0
        
        # False positive rate (FPR)
        negatives = [i for i in idx if actual[i] == 0]
        if negatives:
            fpr = sum(predictions[i] for i in negatives) / len(negatives)
        else:
            fpr = 0
        
        # Predictive parity (precision)
        approved = [i for i in idx if predictions[i] == 1]
        if approved:
            precision = sum(actual[i] for i in approved) / len(approved)
        else:
            precision = 0
        
        metrics[group] = {
            "selection_rate": selection_rate,
            "tpr": tpr,
            "fpr": fpr,
            "precision": precision
        }
    
    # Calculate differences
    group_list = list(groups)
    return {
        "by_group": metrics,
        "demographic_parity_diff": abs(
            metrics[group_list[0]]["selection_rate"] - 
            metrics[group_list[1]]["selection_rate"]
        ),
        "equalized_odds_tpr_diff": abs(
            metrics[group_list[0]]["tpr"] - 
            metrics[group_list[1]]["tpr"]
        ),
        "equalized_odds_fpr_diff": abs(
            metrics[group_list[0]]["fpr"] - 
            metrics[group_list[1]]["fpr"]
        ),
        "predictive_parity_diff": abs(
            metrics[group_list[0]]["precision"] - 
            metrics[group_list[1]]["precision"]
        )
    }
```

## Summary

- **Demographic Parity:** Equal positive rates across groups (ignores accuracy)
- **Equalized Odds:** Equal TPR and FPR across groups (equal accuracy)
- **Equal Opportunity:** Equal TPR across groups (focus on qualified individuals)
- **Predictive Parity:** Equal precision across groups (equally valid positives)
- **Calibration:** Probability scores mean the same thing for all groups
- **Impossibility Theorem:** You usually can't satisfy all definitions at once
- **Choose based on context:** Different applications prioritize different fairness criteria

## Additional Resources

- [Fairness and Machine Learning (Book)](https://fairmlbook.org/) - Comprehensive textbook on fairness
- [Fairlearn User Guide](https://fairlearn.org/main/user_guide/) - Practical guide to fairness metrics
- [Google's ML Fairness Course](https://developers.google.com/machine-learning/fairness-overview) - Video-based learning
