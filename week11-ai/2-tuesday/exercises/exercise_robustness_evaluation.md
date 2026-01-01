# Exercise: AI Robustness Evaluation

## Overview

| Attribute | Value |
|-----------|-------|
| **Duration** | 45 minutes |
| **Difficulty** | Intermediate |
| **Mode** | Individual (Mode A: Implementation) |
| **Prerequisites** | Read robustness-testing.md |

## Learning Objectives

By completing this exercise, you will be able to:
- Apply perturbation techniques to test AI model robustness
- Identify failure modes and threshold boundaries
- Document robustness test results systematically
- Recommend improvements based on failure analysis

## The Concept

**Robustness** measures how well an AI system handles variations in input. A robust model maintains performance when inputs are:
- Slightly modified (perturbations)
- At the edge of expected ranges (boundaries)
- Noisy or corrupted (real-world conditions)
- Adversarially crafted (attacks)

```
Robust Model:                    Fragile Model:
┌─────────────────────┐          ┌─────────────────────┐
│ Input: "laptop"     │          │ Input: "laptop"     │
│ → "electronics" ✓   │          │ → "electronics" ✓   │
│                     │          │                     │
│ Input: "laptoop"    │          │ Input: "laptoop"    │
│ → "electronics" ✓   │          │ → "furniture" ✗     │
│                     │          │                     │
│ Input: "LAPTOP"     │          │ Input: "LAPTOP"     │
│ → "electronics" ✓   │          │ → "unknown" ✗       │
└─────────────────────┘          └─────────────────────┘
```

---

## Part 1: Setting Up Robustness Tests - 10 minutes

### The System Under Test

You're testing a **sentiment analysis model** that classifies customer reviews as Positive, Negative, or Neutral.

### Baseline Test Cases

```python
# Baseline reviews with expected classifications
test_cases = [
    {
        "id": "TC-001",
        "text": "This product is absolutely amazing! Best purchase I've ever made.",
        "expected": "positive"
    },
    {
        "id": "TC-002", 
        "text": "Terrible quality. Broke after one day. Complete waste of money.",
        "expected": "negative"
    },
    {
        "id": "TC-003",
        "text": "The product arrived on time. It works as described.",
        "expected": "neutral"
    },
    {
        "id": "TC-004",
        "text": "I love everything about this! Exceeded all expectations!",
        "expected": "positive"
    },
    {
        "id": "TC-005",
        "text": "Do not buy this. Worst experience ever. Asked for refund.",
        "expected": "negative"
    }
]
```

### Simulated Sentiment Model

```python
import re
from typing import Dict, Tuple

def sentiment_model(text: str) -> Tuple[str, float]:
    """
    Simulated sentiment model with realistic vulnerabilities.
    Returns: (sentiment, confidence)
    """
    text_lower = text.lower()
    
    # Simple keyword-based model (vulnerable to perturbations)
    positive_words = ['amazing', 'love', 'best', 'excellent', 'great', 
                      'exceeded', 'fantastic', 'wonderful', 'perfect']
    negative_words = ['terrible', 'worst', 'broke', 'waste', 'refund',
                      'awful', 'horrible', 'disappointed', 'bad']
    
    pos_count = sum(1 for w in positive_words if w in text_lower)
    neg_count = sum(1 for w in negative_words if w in text_lower)
    
    # Vulnerability: Case sensitivity
    if text.isupper():
        # Model struggles with ALL CAPS
        pos_count *= 0.5
        neg_count *= 0.5
    
    # Vulnerability: Typos break matching
    # (words with typos won't match the lists)
    
    # Vulnerability: Punctuation affects tokenization
    if '!!!' in text or '???' in text:
        # Extreme punctuation confuses the model
        pos_count *= 0.7
        neg_count *= 0.7
    
    # Calculate sentiment
    total = pos_count + neg_count
    if total == 0:
        return ("neutral", 0.5)
    
    if pos_count > neg_count:
        confidence = min(0.95, 0.6 + (pos_count - neg_count) * 0.1)
        return ("positive", confidence)
    elif neg_count > pos_count:
        confidence = min(0.95, 0.6 + (neg_count - pos_count) * 0.1)
        return ("negative", confidence)
    else:
        return ("neutral", 0.5)
```

### Task 1.1: Verify Baseline Accuracy

Run baseline tests and record results:

```python
def run_baseline_tests(test_cases):
    results = []
    for tc in test_cases:
        sentiment, confidence = sentiment_model(tc['text'])
        results.append({
            'id': tc['id'],
            'expected': tc['expected'],
            'actual': sentiment,
            'confidence': confidence,
            'passed': sentiment == tc['expected']
        })
    return results

# Run and print results
baseline_results = run_baseline_tests(test_cases)
```

**Record Baseline Results:**

| TC ID | Expected | Actual | Confidence | Pass? |
|-------|----------|--------|------------|-------|
| TC-001 | | | | |
| TC-002 | | | | |
| TC-003 | | | | |
| TC-004 | | | | |
| TC-005 | | | | |

**Baseline Accuracy:** ____%

---

## Part 2: Implementing Perturbation Functions - 15 minutes

### Task 2.1: Create Perturbation Functions

Implement these perturbation techniques:

```python
import random
import string

class TextPerturbator:
    """Generate perturbed versions of text for robustness testing."""
    
    @staticmethod
    def typo_injection(text: str, num_typos: int = 1) -> str:
        """
        Inject random typos by swapping adjacent characters.
        """
        # YOUR CODE HERE
        # 1. Convert to list of characters
        # 2. Randomly select positions
        # 3. Swap adjacent characters
        # 4. Return modified text
        pass
    
    @staticmethod
    def case_variation(text: str, mode: str = 'upper') -> str:
        """
        Change text case.
        Modes: 'upper', 'lower', 'random', 'alternating'
        """
        # YOUR CODE HERE
        pass
    
    @staticmethod
    def punctuation_noise(text: str) -> str:
        """
        Add or remove punctuation randomly.
        """
        # YOUR CODE HERE
        # Options: Add extra punctuation, remove punctuation, 
        # replace punctuation with similar characters
        pass
    
    @staticmethod
    def whitespace_variation(text: str) -> str:
        """
        Vary whitespace: extra spaces, tabs, multiple newlines.
        """
        # YOUR CODE HERE
        pass
    
    @staticmethod
    def character_substitution(text: str) -> str:
        """
        Replace characters with visually similar ones.
        e.g., 'a' → 'α', 'o' → '0', 'e' → 'є'
        """
        substitutions = {
            'a': 'α', 'e': 'є', 'o': '0', 'i': '1',
            'l': '|', 's': '$', 't': '+', 'b': '6'
        }
        # YOUR CODE HERE
        pass
    
    @staticmethod
    def word_insertion(text: str) -> str:
        """
        Insert neutral filler words that shouldn't change sentiment.
        e.g., "very", "really", "quite"
        """
        # YOUR CODE HERE
        pass
```

### Task 2.2: Generate Perturbed Test Cases

For each baseline test case, generate perturbed versions:

```python
def generate_perturbations(test_case: Dict) -> List[Dict]:
    """Generate all perturbation variants for a test case."""
    original = test_case['text']
    perturbator = TextPerturbator()
    
    perturbations = [
        {
            'id': f"{test_case['id']}-typo",
            'type': 'typo_injection',
            'original': original,
            'perturbed': perturbator.typo_injection(original),
            'expected': test_case['expected']  # Should stay same!
        },
        {
            'id': f"{test_case['id']}-upper",
            'type': 'case_upper',
            'original': original,
            'perturbed': perturbator.case_variation(original, 'upper'),
            'expected': test_case['expected']
        },
        # Add more perturbation types...
    ]
    
    return perturbations
```

**Record perturbed test cases for TC-001:**

| Perturbation Type | Original Text | Perturbed Text |
|-------------------|---------------|----------------|
| Typo | "This product is absolutely amazing!" | |
| Uppercase | "This product is absolutely amazing!" | |
| Extra punctuation | "This product is absolutely amazing!" | |
| Whitespace | "This product is absolutely amazing!" | |
| Char substitution | "This product is absolutely amazing!" | |

---

## Part 3: Executing Robustness Tests - 10 minutes

### Task 3.1: Run Perturbation Tests

```python
def run_robustness_tests(test_cases, perturbations_per_case=5):
    """
    Run robustness tests with all perturbation types.
    """
    all_results = []
    
    for tc in test_cases:
        # Test original
        orig_sentiment, orig_conf = sentiment_model(tc['text'])
        
        # Generate and test perturbations
        perturbations = generate_perturbations(tc)
        
        for p in perturbations:
            pert_sentiment, pert_conf = sentiment_model(p['perturbed'])
            
            all_results.append({
                'original_id': tc['id'],
                'perturbation_type': p['type'],
                'original_text': tc['text'][:50] + '...',
                'perturbed_text': p['perturbed'][:50] + '...',
                'expected': tc['expected'],
                'original_result': orig_sentiment,
                'perturbed_result': pert_sentiment,
                'robust': orig_sentiment == pert_sentiment,
                'confidence_change': abs(orig_conf - pert_conf)
            })
    
    return all_results
```

### Task 3.2: Record Results

**Robustness Test Results:**

| TC ID | Perturbation | Original Result | Perturbed Result | Robust? | Confidence Δ |
|-------|--------------|-----------------|------------------|---------|--------------|
| TC-001 | typo | | | | |
| TC-001 | uppercase | | | | |
| TC-001 | punctuation | | | | |
| TC-002 | typo | | | | |
| TC-002 | uppercase | | | | |
| ... | ... | ... | ... | ... | ... |

### Task 3.3: Calculate Robustness Metrics

```python
def calculate_robustness_metrics(results):
    """Calculate robustness metrics from test results."""
    
    total_tests = len(results)
    robust_count = sum(1 for r in results if r['robust'])
    
    # By perturbation type
    by_type = {}
    for r in results:
        ptype = r['perturbation_type']
        if ptype not in by_type:
            by_type[ptype] = {'total': 0, 'robust': 0}
        by_type[ptype]['total'] += 1
        if r['robust']:
            by_type[ptype]['robust'] += 1
    
    return {
        'overall_robustness': robust_count / total_tests,
        'by_perturbation_type': {
            k: v['robust'] / v['total'] 
            for k, v in by_type.items()
        }
    }
```

**Complete Metrics Table:**

| Perturbation Type | Tests Run | Tests Passed | Robustness Rate |
|-------------------|-----------|--------------|-----------------|
| Typo injection | | | |
| Uppercase | | | |
| Punctuation noise | | | |
| Whitespace variation | | | |
| Character substitution | | | |
| **Overall** | | | |

---

## Part 4: Failure Analysis and Recommendations - 10 minutes

### Task 4.1: Identify Failure Patterns

Analyze the failures and identify patterns:

```markdown
## Failure Pattern Analysis

### Pattern 1: [Name the pattern]
- **Perturbation type:** [Which type failed most]
- **Failure rate:** [X%]
- **Example failures:**
  - Original: "[text]" → [sentiment]
  - Perturbed: "[text]" → [different sentiment]
- **Root cause hypothesis:** [Why does this happen?]

### Pattern 2: [Name the pattern]
[Same structure]

### Pattern 3: [Name the pattern]  
[Same structure]
```

### Task 4.2: Threshold Analysis

Determine how much perturbation the model can handle:

| Perturbation Type | Threshold Before Failure | Notes |
|-------------------|-------------------------|-------|
| Typos | ___ typos per sentence | |
| Case changes | ___ % characters changed | |
| Punctuation | ___ extra punctuation marks | |

### Task 4.3: Robustness Report

```markdown
# Robustness Evaluation Report
## Sentiment Analysis Model v1.0

### Executive Summary

**Overall Robustness Score:** [X]%
**Rating:** [Excellent/Good/Fair/Poor]

**Critical Vulnerabilities:**
1. [Vulnerability 1]
2. [Vulnerability 2]

---

### Test Methodology

- **Baseline tests:** [N] test cases
- **Perturbation types:** [List]
- **Total robustness tests:** [N]
- **Success criteria:** Model output unchanged after perturbation

---

### Results by Perturbation Type

| Type | Robustness | Severity if Exploited |
|------|------------|----------------------|
| Typo injection | [X%] | [High/Med/Low] |
| Case variation | [X%] | [High/Med/Low] |
| Punctuation noise | [X%] | [High/Med/Low] |
| Whitespace | [X%] | [High/Med/Low] |
| Char substitution | [X%] | [High/Med/Low] |

---

### Critical Failures

#### Failure 1: [Title]

**Severity:** [Critical/High/Medium/Low]

**Test case:**
- Original: "[text]"
- Perturbed: "[text]"  
- Original output: [sentiment]
- Perturbed output: [sentiment]

**Impact:** [What could happen in production]

**Recommendation:** [How to fix]

---

### Recommendations

#### Immediate Actions

1. **[Action 1]**
   - Problem: [What's broken]
   - Solution: [How to fix]
   - Priority: [Critical/High/Medium]

2. **[Action 2]**
   - Problem: [What's broken]
   - Solution: [How to fix]
   - Priority: [Critical/High/Medium]

#### Model Improvements

1. [Improvement 1]
2. [Improvement 2]
3. [Improvement 3]

#### Testing Improvements

1. [Improvement 1]
2. [Improvement 2]

---

### Appendix: Full Test Results

[Include detailed results table]
```

---

## Deliverables

1. **Perturbation functions** (all 6 implemented)
2. **Test execution results** (at least 25 total tests)
3. **Robustness metrics** calculated and documented
4. **Failure pattern analysis**
5. **Complete robustness report**

## Definition of Done

- [ ] Implemented all perturbation functions
- [ ] Generated perturbed versions of all baseline tests
- [ ] Executed robustness tests and recorded results
- [ ] Calculated overall and per-type robustness metrics
- [ ] Identified at least 2 failure patterns
- [ ] Completed robustness report with recommendations
- [ ] Code runs without errors

## Evaluation Rubric

| Criteria | Excellent (4) | Good (3) | Satisfactory (2) | Needs Work (1) |
|----------|---------------|----------|------------------|----------------|
| Perturbation Functions | All 6 working correctly | 4-5 working | 2-3 working | < 2 working |
| Test Coverage | All baseline cases + multiple perturbations | Most cases covered | Some coverage | Minimal |
| Analysis | Deep pattern analysis | Good analysis | Basic analysis | No analysis |
| Report Quality | Professional, actionable | Complete | Basic | Incomplete |

## Reflection Questions

1. Which perturbation type revealed the most serious vulnerability?

2. How would you prioritize fixing these robustness issues?

3. What real-world scenarios could exploit these vulnerabilities?

4. How would you automate this testing in a CI/CD pipeline?

## Bonus Challenge

1. **Adversarial examples:** Craft inputs specifically designed to cause misclassification
2. **Confidence calibration:** Test if confidence drops appropriately for perturbed inputs
3. **Combined perturbations:** Test multiple perturbations applied simultaneously


