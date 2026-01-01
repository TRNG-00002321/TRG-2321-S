# Exercise: Counterfactual Test Case Generation

## Overview

| Attribute | Value |
|-----------|-------|
| **Duration** | 45 minutes |
| **Difficulty** | Intermediate |
| **Mode** | Individual (Mode A: Implementation) |
| **Prerequisites** | Read counterfactual-testing.md, bias-and-fairness.md |

## Learning Objectives

By completing this exercise, you will be able to:
- Generate counterfactual test cases by varying protected attributes
- Test AI systems for discriminatory behavior
- Document and analyze counterfactual testing results
- Identify when AI decisions are influenced by protected characteristics

## The Concept

**Counterfactual testing** asks: "Would the outcome change if only the protected attribute were different?"

If a loan application is denied, and the ONLY thing we change is the applicant's gender, would it still be denied? If the outcome changes, the model may be discriminating based on that attribute.

```
Original Input:           Counterfactual Input:
┌─────────────────────┐   ┌─────────────────────┐
│ Name: John Smith    │   │ Name: Jane Smith    │  ← Changed
│ Age: 35             │   │ Age: 35             │
│ Income: $75,000     │   │ Income: $75,000     │
│ Credit: 720         │   │ Credit: 720         │
└─────────────────────┘   └─────────────────────┘
         │                          │
         ▼                          ▼
┌─────────────────────┐   ┌─────────────────────┐
│ Result: APPROVED    │   │ Result: DENIED      │  ← Different!
└─────────────────────┘   └─────────────────────┘

⚠️ This indicates potential gender discrimination
```

---

## Part 1: Understanding Counterfactuals - 10 minutes

### Task 1.1: Identify Counterfactual Pairs

For each scenario, identify what should change to create a valid counterfactual:

**Scenario A: Resume Screening**

Original:
```
Name: Michael Johnson
University: MIT
GPA: 3.8
Experience: 5 years at Google
Skills: Python, ML, AWS
```

What would you change to test for gender bias?
```
Change: [Your answer]
Keep same: [Everything else]
```

What would you change to test for racial bias?
```
Change: [Your answer]
Keep same: [Everything else]
```

---

**Scenario B: Insurance Quote**

Original:
```
Driver: David Chen, Age 28
Vehicle: 2020 Honda Civic
Location: 90210 (Beverly Hills)
Driving record: Clean
Coverage: Full
```

What would you change to test for age bias?
```
Change: [Your answer]
Keep same: [Everything else]
```

What would you change to test for location/socioeconomic bias?
```
Change: [Your answer]
Keep same: [Everything else]
```

---

### Task 1.2: Valid vs Invalid Counterfactuals

Mark each as VALID or INVALID counterfactual:

| Original → Counterfactual | Valid? | Why? |
|---------------------------|--------|------|
| Change name from "Jose" to "Joseph" | | |
| Change age from 25 to 55 AND income from $50K to $150K | | |
| Change gender AND update all pronouns in cover letter | | |
| Change ethnicity but keep culturally-specific name | | |
| Change zip code from low-income to high-income area | | |

---

## Part 2: Generating Counterfactual Test Cases - 20 minutes

### The System Under Test

You're testing a **hiring recommendation AI** that scores candidates 0-100 based on their profile.

### Task 2.1: Create Counterfactual Generator

Implement a counterfactual test case generator:

```python
import copy
from typing import Dict, List, Tuple

class CounterfactualGenerator:
    """Generate counterfactual test cases for bias testing."""
    
    # Name pairs that signal different demographics
    NAME_PAIRS = {
        'gender': [
            ('James', 'Jessica'),
            ('Michael', 'Michelle'),
            ('Robert', 'Rebecca'),
            ('William', 'Willow'),
            ('David', 'Diana'),
        ],
        'ethnicity': [
            ('John Smith', 'Jamal Washington'),
            ('Emily Johnson', 'Lakisha Jefferson'),
            ('Michael Williams', 'Jose Rodriguez'),
            ('Sarah Davis', 'Wei Chen'),
        ]
    }
    
    def __init__(self, base_profile: Dict):
        """Initialize with a base candidate profile."""
        self.base_profile = base_profile
    
    def generate_gender_counterfactual(self) -> Tuple[Dict, Dict]:
        """
        Generate a pair of profiles differing only in gender-signaling name.
        Returns: (male_profile, female_profile)
        """
        # YOUR CODE HERE
        # 1. Create deep copies of base profile
        # 2. Assign male name to one, female name to other
        # 3. Return both profiles
        pass
    
    def generate_ethnicity_counterfactual(self) -> List[Dict]:
        """
        Generate profiles with names signaling different ethnicities.
        Returns: List of profiles with different ethnic-signaling names
        """
        # YOUR CODE HERE
        pass
    
    def generate_age_counterfactual(self, ages: List[int]) -> List[Dict]:
        """
        Generate profiles with different ages.
        Adjusts graduation year to maintain consistency.
        Returns: List of profiles with different ages
        """
        # YOUR CODE HERE
        # Remember: If age changes, graduation_year should change too
        pass


# Base profile template
base_candidate = {
    'name': 'PLACEHOLDER',
    'age': 30,
    'education': "Bachelor's in Computer Science",
    'university': 'State University',
    'gpa': 3.5,
    'years_experience': 5,
    'skills': ['Python', 'SQL', 'Machine Learning'],
    'previous_company': 'Tech Corp',
    'graduation_year': 2017
}
```

### Task 2.2: Generate Test Cases

Using your generator, create counterfactual pairs for testing:

**Gender Counterfactuals:**

| Profile | Name | All Other Fields | Expected: Same Score? |
|---------|------|------------------|----------------------|
| A (Male) | | [Same] | Baseline |
| B (Female) | | [Same] | Yes |

**Ethnicity Counterfactuals:**

| Profile | Name | All Other Fields | Expected: Same Score? |
|---------|------|------------------|----------------------|
| A | | [Same] | Baseline |
| B | | [Same] | Yes |
| C | | [Same] | Yes |
| D | | [Same] | Yes |

**Age Counterfactuals:**

| Profile | Age | Grad Year | All Other Fields | Expected: Same Score? |
|---------|-----|-----------|------------------|----------------------|
| A | 25 | 2022 | [Same] | Baseline |
| B | 35 | 2012 | [Same] | Yes |
| C | 45 | 2002 | [Same] | Yes |

---

## Part 3: Executing Counterfactual Tests - 10 minutes

### Task 3.1: Simulate Model Responses

For this exercise, use this simulated "biased" model:

```python
def biased_hiring_model(profile: Dict) -> int:
    """
    Simulated hiring model with built-in biases.
    Returns score 0-100.
    """
    import hashlib
    
    # Base score from qualifications
    score = 50
    score += profile['gpa'] * 5  # 0-20 points
    score += min(profile['years_experience'] * 2, 20)  # 0-20 points
    score += len(profile['skills']) * 2  # variable
    
    # BIAS: Names starting with certain letters get penalty
    name = profile['name'].lower()
    if name.startswith(('j', 'l', 'm')) and len(name.split()[0]) > 5:
        # Proxy for certain demographics
        score -= 8
    
    # BIAS: Age penalty for older candidates
    if profile['age'] > 40:
        score -= 10
    elif profile['age'] > 50:
        score -= 15
    
    # Add some noise to seem random
    hash_val = int(hashlib.md5(profile['name'].encode()).hexdigest()[:8], 16)
    noise = (hash_val % 10) - 5
    
    return max(0, min(100, int(score + noise)))


# Run counterfactual tests
def run_counterfactual_test(profiles: List[Dict]) -> Dict:
    """
    Run counterfactual test and return results.
    """
    results = []
    for p in profiles:
        score = biased_hiring_model(p)
        results.append({
            'name': p['name'],
            'age': p.get('age'),
            'score': score
        })
    
    # Calculate score differences
    baseline = results[0]['score']
    for r in results:
        r['diff_from_baseline'] = r['score'] - baseline
    
    return results
```

### Task 3.2: Document Test Results

Run your counterfactual tests and record results:

**Gender Test Results:**

| Profile | Name | Score | Difference | Discriminatory? |
|---------|------|-------|------------|-----------------|
| Baseline | | | 0 | N/A |
| Counterfactual | | | | |

**Threshold for discrimination:** Score difference > 5 points

**Analysis:**
```
Did the model show gender bias? [Yes/No]
Evidence: [Describe the score differences]
```

---

**Ethnicity Test Results:**

| Profile | Name | Score | Difference | Discriminatory? |
|---------|------|-------|------------|-----------------|
| A | | | 0 | N/A |
| B | | | | |
| C | | | | |
| D | | | | |

**Analysis:**
```
Did the model show ethnicity bias? [Yes/No]
Evidence: [Describe the patterns]
```

---

**Age Test Results:**

| Profile | Age | Score | Difference | Discriminatory? |
|---------|-----|-------|------------|-----------------|
| 25 | | | 0 | N/A |
| 35 | | | | |
| 45 | | | | |

**Analysis:**
```
Did the model show age bias? [Yes/No]
Evidence: [Describe the patterns]
```

---

## Part 4: Counterfactual Test Report - 5 minutes

### Complete Test Report

```markdown
# Counterfactual Fairness Test Report
## Hiring Recommendation AI - v1.0

### Test Summary

| Protected Attribute | Tested? | Bias Detected? | Severity |
|---------------------|---------|----------------|----------|
| Gender | | | |
| Ethnicity | | | |
| Age | | | |

### Methodology

- **Counterfactual approach:** Changed only protected attribute while keeping all qualifications identical
- **Discrimination threshold:** Score difference > 5 points
- **Sample size:** [N] counterfactual pairs per attribute

### Findings

#### Finding 1: [Attribute] Bias

**Test Case:**
- Original: [Profile details]
- Counterfactual: [Profile details]

**Results:**
- Original score: [X]
- Counterfactual score: [Y]
- Difference: [Z] points

**Conclusion:** [Discriminatory / Not discriminatory]

---

[Repeat for other findings]

---

### Recommendations

1. **[Recommendation 1]**
   - Issue: [What was found]
   - Fix: [What to do]
   - Priority: [High/Medium/Low]

2. **[Recommendation 2]**
   - Issue: [What was found]
   - Fix: [What to do]  
   - Priority: [High/Medium/Low]

### Test Limitations

- [Limitation 1: e.g., Names may not perfectly signal demographics]
- [Limitation 2: e.g., Real bias may be more subtle]
- [Limitation 3: e.g., Limited counterfactual variations tested]

### Next Steps

1. [ ] Investigate model features for proxy variables
2. [ ] Expand counterfactual test set
3. [ ] Implement bias mitigation
4. [ ] Re-test after fixes
```

---

## Deliverables

1. **Counterfactual generator code** with all methods implemented
2. **Generated test cases** (at least 3 pairs per protected attribute)
3. **Test execution results** with scores recorded
4. **Completed test report** with findings and recommendations

## Definition of Done

- [ ] Implemented CounterfactualGenerator class
- [ ] Generated valid counterfactuals for gender, ethnicity, and age
- [ ] Executed tests and recorded scores
- [ ] Identified at least 2 discriminatory patterns
- [ ] Completed formal test report
- [ ] Code runs without errors

## Evaluation Rubric

| Criteria | Excellent (4) | Good (3) | Satisfactory (2) | Needs Work (1) |
|----------|---------------|----------|------------------|----------------|
| Counterfactual Quality | Valid, minimal changes | Mostly valid | Some invalid CFs | Invalid approach |
| Code Implementation | Complete, clean code | Working code | Partial implementation | Doesn't work |
| Analysis | Clear bias identification | Good analysis | Basic analysis | No analysis |
| Documentation | Professional report | Complete report | Basic report | Incomplete |

## Reflection Questions

1. What are the limitations of using names as proxies for protected attributes?

2. How would you handle intersectional testing (e.g., Black women vs. White men)?

3. If a model shows counterfactual unfairness, what are possible technical fixes?

4. How many counterfactual pairs would you need for statistically significant results?

## Bonus Challenge

1. **Automation:** Create a pytest-based test suite that automatically runs counterfactual tests
2. **Reporting:** Generate a visual report showing score distributions by demographic
3. **Intersectionality:** Test combinations of protected attributes


