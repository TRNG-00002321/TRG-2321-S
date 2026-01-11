# Expert Evaluation

## Learning Objectives

- Understand human-in-the-loop testing approaches
- Design effective domain expert evaluation workflows
- Apply qualitative evaluation methods to AI systems
- Measure inter-rater reliability
- Create clear annotation guidelines
- Balance automation with human judgment
- Document and aggregate expert findings
- Establish escalation procedures

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

Some things machines can't judge well. Is this AI-generated text fluent and natural? Is this medical image annotation accurate? Is this content moderation decision appropriate given cultural context?

For these questions, **human experts remain the gold standard**. No automated metric can fully capture whether a translation sounds natural to a native speaker, or whether a clinical recommendation is sound.

## The Concept

### Human-in-the-Loop Testing

**Human-in-the-loop (HITL)** testing involves human experts evaluating AI outputs, providing feedback, or making decisions that automated systems cannot.

```
When to Use Human Evaluation:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  AUTOMATED TESTING SUFFICIENT:                                          │
│  ─────────────────────────────                                          │
│  • Accuracy against known labels                                        │
│  • Performance metrics (latency, throughput)                            │
│  • Consistency checks                                                   │
│  • Format validation                                                    │
│                                                                          │
│  HUMAN EVALUATION REQUIRED:                                             │
│  ───────────────────────────                                            │
│  • Subjective quality (is this "good"?)                                 │
│  • Real-world applicability (would users trust this?)                   │
│  • Contextual appropriateness                                           │
│  • Creative/generative output assessment                                │
│  • Nuanced error analysis                                               │
│  • Edge case validation                                                 │
│  • Safety and ethics review                                             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```
### Domain Expert Involvement

#### Who Should Evaluate?

```
Expert Selection Criteria:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  DOMAIN EXPERTISE                                                       │
│  ────────────────                                                       │
│  • Subject matter experts (doctors, lawyers, engineers)                 │
│  • Years of experience in relevant field                                │
│  • Professional certifications                                          │
│                                                                          │
│  TARGET USER REPRESENTATION                                             │
│  ──────────────────────────                                             │
│  • Actual end users of the system                                       │
│  • Diverse demographic representation                                   │
│  • Range of technical proficiency                                       │
│                                                                          │
│  QUALITY ASSURANCE PERSPECTIVE                                          │
│  ─────────────────────────────                                          │
│  • QA engineers with domain training                                    │
│  • Professional annotators                                              │
│  • Trained evaluators with guidelines                                   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

**Expert evaluation matters because:**

1. **Ground truth validation:** Experts define what "correct" means in ambiguous domains
2. **Edge case judgment:** Automated tests miss nuanced failure modes
3. **Cultural/contextual factors:** AI lacks human cultural understanding
4. **Regulatory requirements:** Many industries require human oversight of AI decisions

As a quality engineer, you need to know when to involve human experts and how to do it effectively.

## The Concept

### When to Use Expert Evaluation

Expert evaluation is valuable when:

| Situation | Example |
|-----------|---------|
| No clear ground truth | "Is this summary high quality?" |
| Subjective judgment needed | "Is this content offensive?" |
| Domain expertise required | "Is this medical diagnosis reasonable?" |
| Safety-critical decisions | "Is this autonomous vehicle behavior safe?" |
| Cultural context matters | "Is this translation culturally appropriate?" |
| Regulatory compliance | "Does this meet legal requirements?" |

Expert evaluation is less useful when:
- Clear objective metrics exist
- Speed/scale makes human review impractical
- Expert judgment isn't more reliable than algorithms

### Designing Expert Evaluation Workflows

**Step 1: Define what you're evaluating**

Be specific. Not "is this good?" but "is this response factually accurate?" or "does this match the intended tone?"

**Step 2: Create clear guidelines**

Experts need consistent instructions:

```markdown
## Evaluation Guidelines

### Task
Rate the quality of AI-generated product descriptions.

### Scale
1 = Poor (major errors, misleading information)
2 = Below Average (some errors or awkward phrasing)
3 = Average (functional, minor issues)
4 = Good (clear, accurate, few issues)
5 = Excellent (could use as-is, publication quality)

### Criteria
- Accuracy: Does it correctly describe the product?
- Clarity: Is it easy to understand?
- Completeness: Does it cover key features?
- Tone: Is it appropriate for an e-commerce site?

### Examples
[Include 2-3 labeled examples at each rating level]
```

**Step 3: Build evaluation interface**

Make it easy for experts to submit judgments:

```python
def create_evaluation_task(item_id, content, guidelines):
    """Create a task for expert evaluation."""
    return {
        "task_id": item_id,
        "content_to_evaluate": content,
        "instructions": guidelines,
        "questions": [
            {
                "id": "quality",
                "text": "Rate overall quality (1-5)",
                "type": "rating",
                "min": 1,
                "max": 5
            },
            {
                "id": "issues",
                "text": "What issues did you notice?",
                "type": "text",
                "optional": True
            }
        ]
    }
```

**Step 4: Collect multiple opinions**

Have 2-3 experts evaluate each item to catch individual variation.

**Step 5: Measure agreement and aggregate**

### Inter-Rater Reliability

**Inter-rater reliability** measures how consistently different evaluators rate the same items. Low agreement suggests unclear guidelines or genuinely ambiguous cases.

**Common metrics:**

**Cohen's Kappa (for 2 raters):**
- 0.0 = Agreement no better than chance
- 0.4-0.6 = Moderate agreement
- 0.6-0.8 = Substantial agreement
- 0.8-1.0 = Almost perfect agreement

```python
def calculate_agreement(rater1_scores, rater2_scores):
    """
    Calculate simple agreement rate between two raters.
    """
    matching = sum(1 for a, b in zip(rater1_scores, rater2_scores) if a == b)
    total = len(rater1_scores)
    return matching / total
```

**What to do with low agreement:**
- Clarify guidelines with examples
- Discuss disagreements to find patterns
- Accept that some cases are genuinely ambiguous
- Report uncertainty rather than forcing consensus

### Qualitative Evaluation Methods

Beyond numeric ratings, capture qualitative feedback:

**Open-ended questions:**
- "What could be improved?"
- "What did the system get wrong?"
- "Any safety concerns?"

**Structured feedback:**
- Checklist of common issues
- Multi-select categories of errors
- Severity ratings for identified problems

**Example qualitative collection:**

```python
def collect_qualitative_feedback(evaluation):
    """Structure for collecting detailed expert feedback."""
    return {
        "rating": evaluation["quality_score"],
        "issues_found": [
            {
                "type": evaluation["issue_type"],  # e.g., "factual_error"
                "severity": evaluation["severity"],  # 1-3
                "description": evaluation["issue_description"],
                "suggested_fix": evaluation.get("suggestion")
            }
        ],
        "positive_aspects": evaluation.get("positives", []),
        "overall_comments": evaluation.get("comments")
    }
```

### Balancing Automation and Human Judgment

Human evaluation is expensive and slow. Use it strategically:

**Sampling strategies:**
- Random sample (e.g., 1% of predictions)
- Stratified sample (ensure coverage of different categories/demographics)
- Edge case focus (prioritize unusual or borderline predictions)
- Confidence-based (review low-confidence predictions)

**Escalation approach:**
1. Automated checks run first (catch obvious issues)
2. Uncertain cases flagged for expert review
3. Experts handle edge cases and calibrate automation

```python
def route_for_review(prediction, confidence, automated_checks):
    """
    Decide whether prediction needs expert review.
    """
    # High-confidence, passes automated checks → no review needed
    if confidence > 0.9 and all(automated_checks.values()):
        return {"review_needed": False}
    
    # Low confidence → review
    if confidence < 0.6:
        return {
            "review_needed": True,
            "reason": "Low confidence",
            "priority": "high"
        }
    
    # Failed automated check → review
    failed = [c for c, passed in automated_checks.items() if not passed]
    if failed:
        return {
            "review_needed": True,
            "reason": f"Failed checks: {failed}",
            "priority": "medium"
        }
    
    # Medium confidence → sample for review
    if random.random() < 0.1:  # 10% sample
        return {
            "review_needed": True,
            "reason": "Random sample",
            "priority": "low"
        }
    
    return {"review_needed": False}
```

### Documenting Expert Findings

Create structured records:

```python
def document_expert_review(item_id, expert_id, evaluation):
    """
    Document an expert's evaluation for audit trail.
    """
    return {
        "item_id": item_id,
        "expert_id": expert_id,
        "timestamp": datetime.now().isoformat(),
        "expert_credentials": get_expert_credentials(expert_id),
        "evaluation": {
            "scores": evaluation["scores"],
            "issues": evaluation["issues"],
            "comments": evaluation["comments"]
        },
        "time_spent_seconds": evaluation["duration"],
        "guideline_version": CURRENT_GUIDELINE_VERSION
    }
```

### Aggregating Expert Opinions

When multiple experts evaluate the same item:

**For ratings:**
- Mean or median rating
- Majority vote for categorical judgments
- Flag items with high disagreement

**For issues:**
- Union of all issues found
- Weight by severity and agreement

```python
def aggregate_evaluations(evaluations):
    """
    Aggregate multiple expert evaluations.
    """
    ratings = [e["quality_score"] for e in evaluations]
    
    # Collect all issues
    all_issues = []
    for e in evaluations:
        all_issues.extend(e.get("issues", []))
    
    return {
        "mean_rating": sum(ratings) / len(ratings),
        "min_rating": min(ratings),
        "max_rating": max(ratings),
        "rating_variance": variance(ratings),
        "agreement_flag": max(ratings) - min(ratings) <= 1,
        "consolidated_issues": deduplicate_issues(all_issues),
        "evaluator_count": len(evaluations)
    }
```

## Code Example

```python
"""
Simple Expert Evaluation Framework
"""

class ExpertEvaluationSystem:
    """Manage expert evaluations of AI outputs."""
    
    def __init__(self, guidelines):
        self.guidelines = guidelines
        self.evaluations = []  # Store all evaluations
    
    def create_batch(self, items, evaluators_per_item=2):
        """
        Create evaluation batch assigning items to evaluators.
        """
        assignments = []
        for item in items:
            for _ in range(evaluators_per_item):
                assignments.append({
                    "item_id": item["id"],
                    "content": item["content"],
                    "status": "pending"
                })
        return assignments
    
    def submit_evaluation(self, item_id, expert_id, rating, issues=None, comments=None):
        """
        Record an expert's evaluation.
        """
        self.evaluations.append({
            "item_id": item_id,
            "expert_id": expert_id,
            "rating": rating,
            "issues": issues or [],
            "comments": comments,
            "timestamp": datetime.now()
        })
    
    def get_item_results(self, item_id):
        """
        Get aggregated results for an item.
        """
        item_evals = [e for e in self.evaluations if e["item_id"] == item_id]
        
        if not item_evals:
            return None
        
        ratings = [e["rating"] for e in item_evals]
        
        return {
            "item_id": item_id,
            "evaluations": len(item_evals),
            "mean_rating": sum(ratings) / len(ratings),
            "min_rating": min(ratings),
            "max_rating": max(ratings),
            "agreement": max(ratings) - min(ratings) <= 1
        }
```

## Summary

- **Expert evaluation** provides human judgment for ambiguous or subjective assessments
- **Clear guidelines** with examples ensure consistent evaluations
- **Inter-rater reliability** measures evaluator agreement
- **Sampling strategies** make expert review scalable
- **Escalation procedures** route uncertain cases to humans
- **Documentation** creates audit trails and enables improvement
- **Aggregation** combines multiple expert opinions systematically

## Additional Resources

- [Crowdsourcing Best Practices](https://www.mturk.com/resources) - Amazon's guidance on human evaluation
- [Inter-rater Reliability (Wikipedia)](https://en.wikipedia.org/wiki/Inter-rater_reliability) - Overview of agreement metrics
- [Label Studio](https://labelstud.io/) - Open source data labeling tool
