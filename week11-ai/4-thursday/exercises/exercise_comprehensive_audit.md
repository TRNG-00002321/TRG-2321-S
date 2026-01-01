# Exercise: Comprehensive AI System Audit

## Overview

| Attribute | Value |
|-----------|-------|
| **Duration** | 90 minutes |
| **Difficulty** | Advanced |
| **Mode** | Pair Programming (Architect/Critic) |
| **Prerequisites** | Complete all Monday and Tuesday content |

## Pair Programming Guidelines

```
╔═══════════════════════════════════════════════════════════════════════════╗
║                     ARCHITECT/CRITIC COLLABORATION                         ║
╠═══════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  ARCHITECT (Designs and executes)                                         ║
║  • Plans the audit approach                                               ║
║  • Executes tests and collects data                                       ║
║  • Proposes findings and recommendations                                  ║
║                                                                            ║
║  CRITIC (Reviews and challenges)                                          ║
║  • Questions assumptions and methodology                                  ║
║  • Identifies gaps in audit coverage                                      ║
║  • Validates findings and suggests alternatives                           ║
║  • Documents counter-arguments                                            ║
║                                                                            ║
║  SWITCH ROLES AFTER EACH AUDIT SECTION                                    ║
║                                                                            ║
╚═══════════════════════════════════════════════════════════════════════════╝
```

## Learning Objectives

By completing this exercise, your pair will be able to:
- Conduct a comprehensive AI system audit covering multiple dimensions
- Apply systematic audit methodology
- Document findings following industry standards
- Provide actionable remediation recommendations

---

## The System Under Audit

**CreditScore AI** is a machine learning system used by banks to assist in credit decisions. The system:

- Analyzes applicant data (income, employment, credit history, etc.)
- Produces a credit score (300-850)
- Provides a risk category (Low, Medium, High)
- Generates a brief explanation for the decision

### Regulatory Context

The system must comply with:
- **Equal Credit Opportunity Act (ECOA):** Prohibits discrimination
- **Fair Credit Reporting Act (FCRA):** Requires accuracy and dispute rights
- **EU AI Act (if deployed in EU):** High-risk AI system requirements
- **State regulations:** Various state-specific requirements

### Audit Scope

```
╔═══════════════════════════════════════════════════════════════════════════╗
║                           AUDIT SCOPE                                      ║
╠═══════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  IN SCOPE:                              OUT OF SCOPE:                     ║
║  ☑ Bias and fairness testing            ☐ Source code review              ║
║  ☑ Robustness evaluation                ☐ Infrastructure security         ║
║  ☑ Safety and harm potential            ☐ Data privacy (separate audit)   ║
║  ☑ Explainability assessment            ☐ Financial model validation      ║
║  ☑ Documentation review                                                   ║
║  ☑ Human oversight evaluation                                             ║
║                                                                            ║
╚═══════════════════════════════════════════════════════════════════════════╝
```

---

## Part 1: Audit Planning - 20 minutes

**Roles:** Architect plans, Critic challenges

### Task 1.1: Create Audit Checklist

**Architect creates initial checklist:**

```markdown
# CreditScore AI Audit Checklist

## 1. Bias and Fairness Audit
- [ ] Identify protected attributes to test
- [ ] Define fairness metrics to calculate
- [ ] Set pass/fail thresholds
- [ ] Plan data segmentation approach

Protected attributes to test:
1. 
2. 
3. 
4. 

Fairness metrics:
1. Disparate Impact Ratio (threshold: >= 0.80)
2. 
3. 
4. 

## 2. Robustness Audit
- [ ] Define perturbation types
- [ ] Set stability thresholds
- [ ] Plan edge case testing
- [ ] Define adversarial test cases

Perturbation types:
1. 
2. 
3. 

## 3. Safety Audit
- [ ] Identify potential harms
- [ ] Test guardrails
- [ ] Evaluate failure modes
- [ ] Check for harmful outputs

Potential harms:
1. 
2. 
3. 

## 4. Explainability Audit
- [ ] Evaluate explanation quality
- [ ] Test explanation consistency
- [ ] Check for misleading explanations
- [ ] Assess user understanding

## 5. Documentation Review
- [ ] Model card completeness
- [ ] Training data documentation
- [ ] Performance metrics documentation
- [ ] Limitation disclosures

## 6. Human Oversight
- [ ] Review escalation procedures
- [ ] Check override capabilities
- [ ] Evaluate monitoring dashboards
- [ ] Assess appeal process
```

**Critic challenges:**

```markdown
## Audit Plan Critique

### Gaps Identified:
1. [What's missing from the plan?]
2. [What assumptions need validation?]
3. [What additional tests are needed?]

### Methodology Questions:
1. [Challenge the approach]
2. [Question the thresholds]
3. [Suggest alternatives]

### Risk Assessment:
- What could we miss with this plan?
- Are the thresholds appropriate for financial services?
- Do we have enough data for reliable conclusions?
```

### Task 1.2: Define Audit Methodology

**Both partners agree on methodology:**

```markdown
## Audit Methodology

### Data Requirements
- Minimum sample size: [N] records
- Required demographic distribution: [Specify]
- Time period covered: [Specify]

### Statistical Significance
- Confidence level: [95%]
- Minimum subgroup size: [N] for valid analysis

### Evidence Standards
- Finding requires: [What constitutes evidence]
- Severity assignment: [Criteria for Critical/High/Medium/Low]

### Limitations
- [Acknowledge methodology limitations]
```

---

## Part 2: Bias and Fairness Audit - 25 minutes

**Switch roles after this section**

### Task 2.1: Execute Fairness Tests

Use this simulated data and model:

```python
"""
Simulated CreditScore AI for audit exercise
"""

import numpy as np
import pandas as pd

def generate_audit_data(n=5000):
    """Generate synthetic credit application data with realistic bias."""
    np.random.seed(42)
    
    data = {
        'applicant_id': range(1, n+1),
        'age': np.random.normal(40, 12, n).clip(18, 80).astype(int),
        'gender': np.random.choice(['male', 'female'], n, p=[0.55, 0.45]),
        'race': np.random.choice(['white', 'black', 'hispanic', 'asian'], 
                                  n, p=[0.60, 0.15, 0.18, 0.07]),
        'income': np.random.lognormal(10.8, 0.8, n).clip(15000, 500000),
        'employment_years': np.random.exponential(8, n).clip(0, 40),
        'existing_debt': np.random.lognormal(9, 1.2, n).clip(0, 200000),
        'credit_history_years': np.random.exponential(10, n).clip(0, 50),
        'num_credit_accounts': np.random.poisson(5, n).clip(0, 20),
        'late_payments_last_year': np.random.poisson(0.5, n).clip(0, 12),
    }
    
    df = pd.DataFrame(data)
    
    # Calculate debt-to-income ratio
    df['dti_ratio'] = df['existing_debt'] / df['income']
    
    return df

def biased_credit_model(df):
    """
    Simulated credit scoring model with built-in biases.
    Returns credit scores (300-850) and risk categories.
    """
    # Base score from financial factors
    scores = 550 + np.zeros(len(df))
    
    # Income impact
    scores += np.log(df['income'] / 50000) * 50
    
    # Employment stability
    scores += np.minimum(df['employment_years'] * 3, 30)
    
    # Credit history length
    scores += np.minimum(df['credit_history_years'] * 2, 40)
    
    # Debt-to-income penalty
    scores -= df['dti_ratio'] * 100
    
    # Late payment penalty
    scores -= df['late_payments_last_year'] * 20
    
    # BIAS: Age penalty for younger applicants
    scores = np.where(df['age'] < 30, scores - 25, scores)
    
    # BIAS: Subtle race-based penalty through proxy
    # (In real models, this might come from zip codes or other proxies)
    race_adjustments = {'white': 0, 'asian': 5, 'hispanic': -15, 'black': -20}
    for race, adj in race_adjustments.items():
        scores = np.where(df['race'] == race, scores + adj, scores)
    
    # BIAS: Small gender penalty
    scores = np.where(df['gender'] == 'female', scores - 8, scores)
    
    # Clip to valid range
    scores = np.clip(scores, 300, 850)
    
    # Assign risk categories
    risk = np.where(scores >= 700, 'Low',
                    np.where(scores >= 600, 'Medium', 'High'))
    
    return scores.astype(int), risk


# Generate data and predictions
audit_data = generate_audit_data()
audit_data['credit_score'], audit_data['risk_category'] = biased_credit_model(audit_data)
```

**Document findings:**

```markdown
## Fairness Audit Findings

### Protected Attribute: Gender

| Group | Count | Mean Score | Approval Rate (Score >= 650) |
|-------|-------|------------|------------------------------|
| Male | | | |
| Female | | | |

Disparate Impact Ratio: [Calculate]
Status: [PASS/FAIL]

### Protected Attribute: Race

| Group | Count | Mean Score | Approval Rate (Score >= 650) |
|-------|-------|------------|------------------------------|
| White | | | |
| Black | | | |
| Hispanic | | | |
| Asian | | | |

Disparate Impact Ratio (vs highest group): [Calculate for each]
Status: [PASS/FAIL]

### Protected Attribute: Age

| Group | Count | Mean Score | Approval Rate (Score >= 650) |
|-------|-------|------------|------------------------------|
| 18-29 | | | |
| 30-50 | | | |
| 51+ | | | |

Analysis: [Document findings]
```

### Task 2.2: Intersectional Analysis

**Check for compound discrimination:**

```markdown
## Intersectional Analysis

### Race × Gender

| Group | Count | Mean Score | Approval Rate |
|-------|-------|------------|---------------|
| White Male | | | |
| White Female | | | |
| Black Male | | | |
| Black Female | | | |
| Hispanic Male | | | |
| Hispanic Female | | | |

Findings:
- Most advantaged group: [Which?]
- Most disadvantaged group: [Which?]
- Compound discrimination evidence: [Yes/No, explain]
```

---

## Part 3: Robustness Audit - 15 minutes

**Switch roles**

### Task 3.1: Perturbation Testing

```markdown
## Robustness Test Results

### Test 1: Income Perturbation (±5%)

| Perturbation | Original Score | Perturbed Score | Change |
|--------------|----------------|-----------------|--------|
| -5% income | 720 | | |
| +5% income | 720 | | |

Stability assessment: [Stable/Unstable]

### Test 2: Boundary Behavior

Test behavior at decision boundaries (e.g., score = 650):

| Test Case | Expected Behavior | Actual Behavior | Pass? |
|-----------|-------------------|-----------------|-------|
| Score at 649 | High risk | | |
| Score at 650 | Medium risk | | |
| Score at 651 | Medium risk | | |

### Test 3: Missing Data Handling

| Missing Field | Model Behavior | Acceptable? |
|---------------|----------------|-------------|
| Income | | |
| Employment years | | |
| Credit history | | |

### Test 4: Extreme Values

| Test Case | Input | Output | Reasonable? |
|-----------|-------|--------|-------------|
| Max income ($10M) | | | |
| Min income ($0) | | | |
| 100 years old | | | |
| 18 years old, 20 years employment | | | |
```

---

## Part 4: Safety and Explainability Audit - 15 minutes

**Switch roles**

### Task 4.1: Safety Evaluation

```markdown
## Safety Audit

### Potential Harms Assessment

| Harm Type | Severity | Likelihood | Mitigation Present? |
|-----------|----------|------------|---------------------|
| Unfair denial of credit | High | | |
| Discriminatory patterns | High | | |
| Inconsistent decisions | Medium | | |
| Unexplainable rejections | Medium | | |
| Feedback loop amplification | High | | |

### Guardrail Testing

| Guardrail | Test Performed | Working? | Notes |
|-----------|----------------|----------|-------|
| Human review for borderline cases | | | |
| Appeal process available | | | |
| Explanation provided for denials | | | |
| Score range limits enforced | | | |
```

### Task 4.2: Explainability Assessment

```markdown
## Explainability Audit

### Explanation Quality

Sample explanations to evaluate:

**Case 1:** High-income applicant denied
- Score: 580
- Explanation provided: "[What explanation would system give?]"
- Explanation accurate? [Yes/No]
- Explanation complete? [Yes/No]
- User understandable? [Yes/No]

**Case 2:** Low-income applicant approved
- Score: 720
- Explanation provided: "[What explanation would system give?]"
- Evaluation: [Same criteria]

### Explanation Consistency

| Scenario | Expected Explanation Factor | Actual | Consistent? |
|----------|----------------------------|--------|-------------|
| High DTI ratio | "debt-to-income" mentioned | | |
| Many late payments | "payment history" mentioned | | |
| Short credit history | "credit history" mentioned | | |
```

---

## Part 5: Comprehensive Audit Report - 15 minutes

**Both partners collaborate**

### Complete Audit Report

```markdown
# AI System Audit Report
## CreditScore AI - Comprehensive Assessment

---

### Document Control

| Field | Value |
|-------|-------|
| System Name | CreditScore AI |
| Audit Date | [Date] |
| Auditors | [Partner 1], [Partner 2] |
| Report Version | 1.0 |
| Classification | Confidential |

---

### Executive Summary

**Overall Assessment:** [COMPLIANT / NON-COMPLIANT / CONDITIONALLY COMPLIANT]

**Risk Rating:** [Critical / High / Medium / Low]

**Key Findings:**
1. [Most critical finding]
2. [Second most critical]
3. [Third most critical]

**Immediate Actions Required:**
1. [Action 1]
2. [Action 2]

---

### Audit Scope and Methodology

[Summarize what was tested and how]

---

### Detailed Findings

#### FINDING-001: [Title]

| Attribute | Value |
|-----------|-------|
| Category | Bias / Robustness / Safety / Explainability |
| Severity | Critical / High / Medium / Low |
| Compliance Impact | ECOA / FCRA / EU AI Act |
| Status | Open |

**Description:**
[Detailed description of the finding]

**Evidence:**
[Data and test results supporting the finding]

**Impact:**
[What harm could this cause]

**Recommendation:**
[Specific remediation steps]

**Timeline:**
[When should this be fixed]

---

#### FINDING-002: [Title]
[Same format]

---

### Compliance Assessment

| Regulation | Requirement | Status | Finding Reference |
|------------|-------------|--------|-------------------|
| ECOA | No discrimination by protected class | | |
| ECOA | Adverse action notice required | | |
| FCRA | Accuracy of information | | |
| FCRA | Dispute resolution process | | |
| EU AI Act | High-risk system requirements | | |
| EU AI Act | Human oversight | | |

---

### Recommendations Summary

| Priority | Finding | Recommendation | Owner | Deadline |
|----------|---------|----------------|-------|----------|
| Critical | | | | |
| High | | | | |
| Medium | | | | |
| Low | | | | |

---

### Appendices

#### Appendix A: Test Data Summary
[Statistical summary of audit data]

#### Appendix B: Detailed Metrics
[All calculated fairness metrics]

#### Appendix C: Test Cases
[List of specific test cases executed]

---

### Sign-off

| Role | Name | Signature | Date |
|------|------|-----------|------|
| Lead Auditor | | | |
| Peer Reviewer | | | |
| System Owner | | | |
```

---

## Deliverables

1. **Completed audit checklist** with both partners' input
2. **Fairness test results** including intersectional analysis
3. **Robustness test results** with stability assessment
4. **Safety and explainability evaluation**
5. **Comprehensive audit report** following the template
6. **Remediation roadmap** with prioritized actions

## Definition of Done

- [ ] Both partners contributed as Architect and Critic
- [ ] Tested at least 3 protected attributes
- [ ] Calculated disparate impact for all groups
- [ ] Performed intersectional analysis
- [ ] Completed robustness testing
- [ ] Evaluated safety and explainability
- [ ] Produced professional audit report
- [ ] Prioritized remediation recommendations
- [ ] Documented methodology limitations

## Evaluation Rubric

| Criteria | Excellent (4) | Good (3) | Satisfactory (2) | Needs Work (1) |
|----------|---------------|----------|------------------|----------------|
| Collaboration | Effective Architect/Critic dynamic | Good collaboration | Uneven | Poor collaboration |
| Coverage | Comprehensive across all dimensions | Good coverage | Partial coverage | Incomplete |
| Evidence | Strong data-backed findings | Good evidence | Some evidence | Weak evidence |
| Report Quality | Professional, actionable | Complete | Basic | Incomplete |

## Reflection (Both Partners)

1. What did the Architect/Critic model add to the audit quality?

2. Which audit dimension revealed the most significant issues?

3. How would you prioritize the findings for a real production system?

4. What additional audit activities would you recommend?


