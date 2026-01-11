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

In 2018, Amazon discovered that their AI recruiting tool was systematically discriminating against women. The system, trained on 10 years of historical hiring decisions, learned that the company had predominantly hired men—and concluded that being male was a positive signal. Resumes containing the word "women's" (as in "women's chess club captain") were downgraded. The system was scrapped.

This isn't an isolated incident. Facial recognition systems have been shown to fail disproportionately on darker-skinned faces. Healthcare algorithms have allocated fewer resources to Black patients than equally sick white patients. Recidivism prediction tools used in criminal sentencing have exhibited racial bias.

**Why should quality engineers care?** Because these aren't hypothetical concerns—they're real harms affecting real people. AI systems are increasingly making or influencing decisions about who gets hired, who gets loans, who gets healthcare, and who goes to prison. Testing for bias and fairness isn't just good engineering practice—it's an ethical responsibility.

This module equips you to recognize, measure, and test for bias in AI systems, ensuring the technology we build serves all users fairly.

## The Concept

### What is AI Bias?
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
**AI bias** refers to systematic errors in AI systems that create unfair outcomes, typically disadvantaging certain groups of people. The key word is *systematic*—these aren't random errors. Bias is consistent and directional: it reliably produces worse outcomes for specific populations.

**Important distinction:** Not all model errors are bias. If a model incorrectly classifies an image, that's a mistake. If the model consistently misclassifies images of one demographic group more often than another, that's bias.

**Think of it this way:** Imagine a loan approval system that approves 70% of male applicants and 50% of female applicants with identical qualifications. The 30% and 50% rejection rates might include random errors, but the 20-point gap between groups represents systematic bias.

### Where Does Bias Come From?

Bias can enter AI systems at every stage of development—from initial data collection through deployment and beyond. Understanding these entry points is essential for testing.

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

#### 1. Data Collection Bias

**The problem:** Training data doesn't represent the full population the model will serve.

**Example:** A voice assistant trained primarily on native English speakers performs poorly for users with accents. A dermatology AI trained mostly on images of lighter skin struggles to diagnose conditions on darker skin.

**Testing implication:** Check whether your test data represents all user groups, not just the majority.

#### 2. Data Labeling Bias

**The problem:** Human labelers inject their own biases into training labels.

**Example:** When human annotators rate resume quality, they might unconsciously rate identical resumes differently based on whether the name sounds male or female, or based on ethnic associations with names.

**Testing implication:** Audit label distributions across demographic groups. If possible, use multiple labelers and measure inter-annotator agreement.

#### 3. Feature Selection Bias

**The problem:** The attributes chosen as model inputs encode biased information, even if protected attributes aren't explicitly included.

**Example:** Using zip code as a feature for loan approval seems neutral, but it can serve as a proxy for race due to historical residential segregation. Using "attended a historically Black college" correlates directly with race.

**Testing implication:** Review features for potential proxy effects. Test whether removing proxies changes model behavior across groups.

#### 4. Algorithm Bias

**The problem:** The optimization objective prioritizes overall accuracy, which may sacrifice performance for minority groups.

**Example:** A model optimized for overall accuracy might achieve 95% accuracy by getting 98% accuracy on the majority group (90% of data) and 70% accuracy on the minority group (10% of data). The overall metric looks good, but the minority group experience is poor.

**Testing implication:** Always evaluate metrics separately by demographic group, not just overall.

#### 5. Deployment Context Bias

**The problem:** A model designed for one context is deployed in a different context where its assumptions don't hold.

**Example:** A facial recognition system trained and tested on US demographics is deployed in a country with different demographic distributions, where it performs worse.

**Testing implication:** Test with data representative of actual deployment context, not just training context.

#### 6. Feedback Loop Bias

**The problem:** Model predictions influence future data collection, amplifying existing biases.

**Example:** Predictive policing sends more officers to areas the model flags as high-crime. More officers make more arrests, generating more crime data from those areas. This data reinforces the model's belief those areas are high-crime—regardless of actual crime rates elsewhere.

**Testing implication:** Monitor for amplification effects over time. Track whether disparities are increasing.

### Types of Bias

#### Selection Bias
The training data systematically excludes or underrepresents certain populations.

**Real-world example:** Medical AI trained primarily on data from academic medical centers may perform poorly in rural or community health settings with different patient populations.

**How to test:** Compare demographic distribution in training data to target population distribution.

#### Confirmation Bias
Looking for patterns that confirm existing beliefs or practices.

**Real-world example:** If fraud investigators have historically focused on certain countries, more labeled fraud examples exist from those countries. A model trained on this data will flag transactions from those countries more often—not because fraud is actually higher, but because that's where investigators looked.

**How to test:** Audit label distributions by demographic. Question whether patterns in data reflect reality or historical practices.

#### Measurement Bias
Different accuracy in measuring attributes across groups.

**Real-world example:** Pulse oximeters are less accurate on darker skin tones. AI health monitors trained on this data inherit inaccurate readings for Black patients, potentially underestimating how sick they are.

**How to test:** Validate that measurement accuracy is consistent across groups for input features.

#### Aggregation Bias
A single model applied to diverse subgroups that shouldn't be treated uniformly.

**Real-world example:** A diabetes risk model that works well on average but fails for specific ethnic groups whose risk factors differ from the majority population.

**How to test:** Evaluate model performance separately on each subgroup rather than only in aggregate.

#### Historical Bias
Data reflects past discrimination that we shouldn't perpetuate.

**Real-world example:** Hiring AI trained on historical hiring decisions learns to prefer candidates similar to those historically hired—even if those patterns reflected past discrimination rather than actual job qualifications.

**How to test:** Question whether historical patterns should be replicated. Ask: "Is this pattern what *should* happen, or just what *did* happen?"

### Fairness Concepts

**Fairness** means different things to different stakeholders. Several mathematical definitions exist, and importantly, they're often mutually exclusive—you can't satisfy all of them simultaneously.

#### Protected Attributes

**Protected attributes** are characteristics for which discrimination is legally prohibited or ethically concerning. These vary by jurisdiction but commonly include:

| Category | Examples |
|----------|----------|
| **Legally Protected (varies by jurisdiction)** | Race/Ethnicity, Gender/Sex, Age, Religion, National Origin, Disability Status, Genetic Information |
| **Often Ethically Concerning** | Socioeconomic Status, Sexual Orientation, Geographic Location, Education Level |

**Critical concept: Proxy variables.** Even if you remove protected attributes from model inputs, biased outcomes can persist through *proxy variables*—features that correlate with protected attributes:

| Proxy Variable | May Encode |
|----------------|------------|
| Zip code | Race, Income |
| First name | Gender, Ethnicity |
| College attended | Socioeconomic Status |
| Hobbies/Club memberships | Gender, Age |

A model that never sees "gender" but uses first name to make predictions can still exhibit gender bias.

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

**Disparate impact** occurs when a seemingly neutral practice disproportionately affects a protected group, regardless of intent. This is a legal concept with practical testing implications.

**The Four-Fifths (80%) Rule:** A commonly used threshold for identifying potential disparate impact in employment contexts. If the selection rate for a protected group is less than 80% (four-fifths) of the rate for the most-favored group, disparate impact may exist.

**Example calculation:**

```
Loan Approval Rates:
- Group A: 800 approved out of 1000 = 80%
- Group B: 500 approved out of 1000 = 50%

Disparate Impact Ratio = Rate_B / Rate_A
                       = 50% / 80%
                       = 0.625 (62.5%)

Is 62.5% < 80%? YES

Conclusion: Potential disparate impact against Group B
            (their approval rate is only 62.5% of Group A's)
```

**Simple code to calculate:**

```python
def check_disparate_impact(approved_a, total_a, approved_b, total_b):
    """
    Check for disparate impact using the 4/5 rule.
    Returns the ratio and whether it indicates potential bias.
    """
    rate_a = approved_a / total_a
    rate_b = approved_b / total_b
    
    # Calculate ratio (smaller / larger)
    if rate_a > rate_b:
        ratio = rate_b / rate_a
        disadvantaged = "Group B"
    else:
        ratio = rate_a / rate_b
        disadvantaged = "Group A"
    
    potential_bias = ratio < 0.8
    
    return {
        "ratio": ratio,
        "potential_bias": potential_bias,
        "disadvantaged_group": disadvantaged
    }
```

### Mathematical Definitions of Fairness

Different stakeholders have different ideas about what "fairness" means. Several mathematical definitions exist, and importantly, they're often mutually exclusive.

#### Demographic Parity
**Equal selection rates across groups**, regardless of qualifications.

"The percentage of applicants approved should be the same for all demographic groups."

**When to use:** When historical qualifications may themselves reflect past discrimination.

**Limitation:** Ignores actual qualifications. May approve unqualified people from one group while rejecting qualified people from another.

#### Equalized Odds
**Equal true positive rates AND equal false positive rates** across groups.

"The model should be equally accurate (same error rates) for all groups."

**When to use:** When you have reliable ground truth labels and want the model to treat similar individuals similarly.

**Limitation:** Requires accurate ground truth labels, which may themselves be biased.

#### Predictive Parity
**Equal precision across groups.**

"When the model says 'yes,' it should be equally likely to be correct for all groups."

**When to use:** When the consequences of positive predictions need to be justified equally across groups.

**Limitation:** Can coexist with very different treatment of the groups.

### The Impossibility Theorem

**Critical insight:** Except in very specific cases, it's mathematically impossible to satisfy demographic parity, equalized odds, and predictive parity simultaneously.

**Why this matters:** Teams must choose which definition of fairness matters most for their context. There is no "perfectly fair" system that satisfies all definitions at once.

| Application Domain | Often Prioritize | Reasoning |
|--------------------|------------------|-----------|
| Criminal justice | Equalized Odds | Equal accuracy across groups is paramount |
| Hiring | Demographic Parity | Counteract historical underrepresentation |
| Healthcare | Equal Opportunity | Ensure sick people get equal treatment |
| Lending | Predictive Parity | Actuarial accuracy required by business model |

### Ethical Implications

Bias in AI isn't just a technical problem—it has profound ethical dimensions. When testing for bias, consider these questions:

**Harm assessment:**
- Who is harmed by the model's errors?
- What is the severity of harm? (Minor inconvenience vs. denied opportunities vs. physical harm)
- Are harms distributed equitably across groups?
- Can individuals challenge or appeal decisions?

**Power dynamics:**
- Who benefits from the system?
- Who bears the costs of errors?
- Do affected people have a voice in system design?

**Alternatives:**
- Is AI the right solution for this problem?
- What happens without AI involvement? (Is the status quo actually better?)
- Are there lower-stakes alternatives?

### Regulatory Requirements

The regulatory landscape for AI fairness is evolving rapidly:

**EU AI Act (2024)**
- High-risk AI systems must undergo conformity assessments
- Mandatory bias testing for systems affecting fundamental rights
- Human oversight requirements
- Documentation and logging obligations

**US Regulations (varies by sector)**
- Equal Credit Opportunity Act (credit decisions)
- Fair Housing Act (housing)
- Title VII of Civil Rights Act (employment)
- Americans with Disabilities Act
- NYC Local Law 144 (automated employment decision tools)

**Industry standards:**
- IEEE 7010 (Wellbeing Impact Assessment)
- ISO/IEC TR 24028 (AI Trustworthiness)
- NIST AI Risk Management Framework

### Bias in Testing Tools

Even your testing infrastructure can contain bias:

**Test data bias:** Benchmark datasets may not represent all populations. "Standard" test scenarios often default to majority group characteristics.

**Tooling bias:** Synthetic data generators embed assumptions. Performance testing optimizes for average-case, missing worst-case experiences for minorities.

**Process bias:** QA teams lacking diversity may miss issues. Bug triage may deprioritize issues affecting smaller user groups.

**Mitigation:**
- Include demographic analysis in test plans
- Use diverse test personas
- Explicitly test for differential performance across groups
- Include fairness in your definition of "done"

## Code Example

```python
"""
Simple Bias Detection Example
"""

def analyze_bias(decisions, protected_attribute):
    """
    Analyze a set of decisions for potential bias.
    
    Args:
        decisions: List of dicts with 'approved' (bool) and protected attribute
        protected_attribute: Name of the attribute to analyze (e.g., 'gender')
    
    Returns:
        Analysis report
    """
    # Group by protected attribute
    groups = {}
    for d in decisions:
        group = d[protected_attribute]
        if group not in groups:
            groups[group] = {"approved": 0, "total": 0}
        groups[group]["total"] += 1
        if d["approved"]:
            groups[group]["approved"] += 1
    
    # Calculate rates
    rates = {}
    for group, counts in groups.items():
        rates[group] = counts["approved"] / counts["total"]
    
    # Find disparate impact
    max_rate = max(rates.values())
    min_rate = min(rates.values())
    
    if max_rate > 0:
        di_ratio = min_rate / max_rate
    else:
        di_ratio = 1.0
    
    return {
        "rates_by_group": rates,
        "disparate_impact_ratio": di_ratio,
        "potential_bias": di_ratio < 0.8
    }
```

## Summary

- **AI bias** creates systematic unfair outcomes, often disadvantaging marginalized groups
- **Sources of bias** include data collection, labeling, features, algorithms, deployment context, and feedback loops
- **Types of bias:** Selection, confirmation, measurement, aggregation, and historical
- **Protected attributes** include race, gender, age—plus proxy variables that encode them indirectly
- **Disparate impact** measures differential outcomes; the four-fifths rule is a common threshold
- **Fairness definitions** are mathematically incompatible—teams must choose priorities based on context
- **Regulatory requirements** are expanding rapidly (EU AI Act, NYC LL 144, and more)
- **Testing tools themselves** can contain bias—explicitly include fairness in QA processes

## Additional Resources

- [AI Fairness 360 (IBM)](https://aif360.mybluemix.net/) - Comprehensive bias detection toolkit
- [Fairlearn (Microsoft)](https://fairlearn.org/) - Fairness assessment and mitigation library
- [EU AI Act Text](https://artificialintelligenceact.eu/) - Full regulatory text
- [Algorithmic Justice League](https://www.ajl.org/) - Advocacy organization for equitable AI
