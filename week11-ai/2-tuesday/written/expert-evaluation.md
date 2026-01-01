# Expert Evaluation

## Learning Objectives

- Understand the role of human-in-the-loop testing for AI systems
- Design effective domain expert involvement strategies
- Apply qualitative evaluation methods for AI outputs
- Measure inter-rater reliability and manage annotator disagreement
- Create annotation guidelines that produce consistent results
- Balance automation with human judgment appropriately
- Document expert findings and establish escalation procedures

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

Some questions can only be answered by humans. Is this medical diagnosis reasonable? Does this generated text feel natural? Is this recommendation helpful? Would a customer trust this AI's explanation?

While automated metrics tell us a model is 95% accurate, they don't tell us if that 5% error rate is concentrated in high-stakes situations, or if the model's correct answers are correct for the wrong reasons. Expert evaluation fills this gap, providing qualitative insights that no metric can capture.

For quality engineers, knowing when and how to involve human experts is a critical skill. This module teaches you to design expert evaluation workflows, measure agreement between evaluators, create clear guidelines, and integrate human judgment effectively into your AI testing strategy.

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

#### Evaluation Roles

```python
"""
Expert Evaluation Roles and Responsibilities
"""

class EvaluatorRole:
    """Define different evaluator roles for AI testing."""
    
    ROLES = {
        'domain_expert': {
            'description': 'Subject matter expert in the field',
            'focus': [
                'Technical accuracy of outputs',
                'Professional appropriateness',
                'Domain-specific edge cases',
                'Compliance with standards'
            ],
            'when_to_use': [
                'High-stakes decisions (medical, legal, financial)',
                'Complex domain-specific outputs',
                'Regulatory compliance validation'
            ]
        },
        'end_user': {
            'description': 'Representative of target audience',
            'focus': [
                'Usability and clarity',
                'Trust and confidence',
                'Real-world applicability',
                'User experience quality'
            ],
            'when_to_use': [
                'Consumer-facing products',
                'User interface elements',
                'Recommendation quality'
            ]
        },
        'trained_annotator': {
            'description': 'Professional evaluator with task-specific training',
            'focus': [
                'Consistent labeling',
                'High-volume evaluation',
                'Guideline adherence',
                'Comparative assessment'
            ],
            'when_to_use': [
                'Large-scale evaluation',
                'Benchmark creation',
                'Systematic quality assessment'
            ]
        },
        'adversarial_tester': {
            'description': 'Expert in finding system weaknesses',
            'focus': [
                'Edge cases and failure modes',
                'Security vulnerabilities',
                'Manipulation attempts',
                'Boundary testing'
            ],
            'when_to_use': [
                'Safety-critical systems',
                'Security review',
                'Pre-launch testing'
            ]
        }
    }
```

### Qualitative Evaluation Methods

#### 1. Likert Scale Ratings

```python
"""
Likert Scale Evaluation Framework
"""

class LikertScaleEvaluation:
    """
    Structured rating scale for AI output evaluation.
    """
    
    QUALITY_DIMENSIONS = {
        'accuracy': {
            'question': 'How accurate is this output?',
            'scale': {
                1: 'Completely inaccurate',
                2: 'Mostly inaccurate',
                3: 'Partially accurate',
                4: 'Mostly accurate',
                5: 'Completely accurate'
            }
        },
        'helpfulness': {
            'question': 'How helpful is this output for the intended task?',
            'scale': {
                1: 'Not at all helpful',
                2: 'Slightly helpful',
                3: 'Moderately helpful',
                4: 'Very helpful',
                5: 'Extremely helpful'
            }
        },
        'clarity': {
            'question': 'How clear and understandable is this output?',
            'scale': {
                1: 'Very confusing',
                2: 'Somewhat confusing',
                3: 'Neutral',
                4: 'Clear',
                5: 'Very clear'
            }
        },
        'safety': {
            'question': 'How safe/appropriate is this output?',
            'scale': {
                1: 'Harmful/Inappropriate',
                2: 'Potentially problematic',
                3: 'Neutral',
                4: 'Safe',
                5: 'Clearly safe and appropriate'
            }
        },
        'trust': {
            'question': 'How much would you trust this output?',
            'scale': {
                1: 'Would not trust at all',
                2: 'Low trust',
                3: 'Moderate trust',
                4: 'High trust',
                5: 'Complete trust'
            }
        }
    }
    
    def create_evaluation_form(self, dimensions: list = None) -> dict:
        """Create evaluation form for specified dimensions."""
        if dimensions is None:
            dimensions = list(self.QUALITY_DIMENSIONS.keys())
        
        form = {
            'sample_id': None,
            'evaluator_id': None,
            'timestamp': None,
            'ratings': {},
            'comments': ''
        }
        
        for dim in dimensions:
            if dim in self.QUALITY_DIMENSIONS:
                form['ratings'][dim] = {
                    'question': self.QUALITY_DIMENSIONS[dim]['question'],
                    'scale': self.QUALITY_DIMENSIONS[dim]['scale'],
                    'rating': None
                }
        
        return form
```

#### 2. Comparative Evaluation (A/B)

```python
"""
Comparative Evaluation (Side-by-Side)
"""

class ComparativeEvaluation:
    """
    Compare two AI outputs (e.g., different models or versions).
    """
    
    COMPARISON_CRITERIA = [
        'overall_preference',
        'accuracy',
        'relevance',
        'clarity',
        'completeness'
    ]
    
    def create_comparison_task(self, output_a, output_b, 
                                context: str = None) -> dict:
        """Create side-by-side comparison task."""
        return {
            'context': context,
            'output_a': output_a,
            'output_b': output_b,
            'comparisons': {
                criterion: {
                    'options': ['A is better', 'B is better', 'Equal/Tie'],
                    'selection': None
                }
                for criterion in self.COMPARISON_CRITERIA
            },
            'free_text_feedback': '',
            'confidence': None  # 1-5 how confident in assessment
        }
    
    def aggregate_comparisons(self, results: list) -> dict:
        """Aggregate comparison results across evaluators."""
        aggregated = {criterion: {'A': 0, 'B': 0, 'Tie': 0} 
                      for criterion in self.COMPARISON_CRITERIA}
        
        for result in results:
            for criterion, comparison in result['comparisons'].items():
                selection = comparison['selection']
                if 'A' in selection:
                    aggregated[criterion]['A'] += 1
                elif 'B' in selection:
                    aggregated[criterion]['B'] += 1
                else:
                    aggregated[criterion]['Tie'] += 1
        
        # Calculate win rates
        for criterion in aggregated:
            total = sum(aggregated[criterion].values())
            if total > 0:
                aggregated[criterion]['A_win_rate'] = aggregated[criterion]['A'] / total
                aggregated[criterion]['B_win_rate'] = aggregated[criterion]['B'] / total
        
        return aggregated
```

#### 3. Error Taxonomy Evaluation

```python
"""
Error Analysis and Taxonomy
"""

class ErrorTaxonomyEvaluation:
    """
    Structured error categorization for AI outputs.
    """
    
    ERROR_CATEGORIES = {
        'factual_errors': {
            'incorrect_fact': 'Stated fact is wrong',
            'outdated_info': 'Information is no longer accurate',
            'misattribution': 'Incorrect source or credit',
        },
        'reasoning_errors': {
            'logical_fallacy': 'Faulty logical reasoning',
            'incorrect_inference': 'Wrong conclusion from premises',
            'missing_context': 'Failed to consider relevant context',
        },
        'completeness_errors': {
            'incomplete_answer': 'Missing important information',
            'over_generalization': 'Too broad, missing nuance',
            'irrelevant_content': 'Includes unnecessary information',
        },
        'style_errors': {
            'unclear_language': 'Confusing or ambiguous wording',
            'inappropriate_tone': 'Wrong tone for context',
            'formatting_issues': 'Poor structure or organization',
        },
        'safety_errors': {
            'harmful_content': 'Potentially dangerous information',
            'bias_detected': 'Unfair or discriminatory content',
            'privacy_concern': 'May reveal sensitive information',
        }
    }
    
    def annotate_error(self, output: str, error_type: str, 
                       severity: str, description: str) -> dict:
        """Create error annotation."""
        return {
            'output': output,
            'error_type': error_type,
            'severity': severity,  # 'critical', 'major', 'minor'
            'description': description,
            'timestamp': datetime.now().isoformat()
        }
```

### Inter-Rater Reliability

When multiple evaluators assess the same content, we need to measure agreement:

```python
"""
Inter-Rater Reliability Metrics
"""

import numpy as np
from typing import List, Dict

class InterRaterReliability:
    """
    Calculate agreement metrics between multiple raters.
    """
    
    @staticmethod
    def percent_agreement(ratings: List[List]) -> float:
        """
        Simple percent agreement between raters.
        
        Args:
            ratings: List of [rater1_rating, rater2_rating] pairs
        """
        agreements = sum(1 for r1, r2 in ratings if r1 == r2)
        return agreements / len(ratings)
    
    @staticmethod
    def cohens_kappa(ratings_1: List, ratings_2: List) -> dict:
        """
        Cohen's Kappa for two raters.
        Accounts for agreement by chance.
        """
        from sklearn.metrics import cohen_kappa_score
        
        kappa = cohen_kappa_score(ratings_1, ratings_2)
        
        # Interpretation
        if kappa < 0:
            interpretation = "No agreement (worse than chance)"
        elif kappa < 0.20:
            interpretation = "Slight agreement"
        elif kappa < 0.40:
            interpretation = "Fair agreement"
        elif kappa < 0.60:
            interpretation = "Moderate agreement"
        elif kappa < 0.80:
            interpretation = "Substantial agreement"
        else:
            interpretation = "Almost perfect agreement"
        
        return {
            'kappa': kappa,
            'interpretation': interpretation
        }
    
    @staticmethod
    def fleiss_kappa(ratings_matrix: np.ndarray) -> dict:
        """
        Fleiss' Kappa for multiple raters.
        
        Args:
            ratings_matrix: (n_subjects x n_raters) matrix of ratings
        """
        n_subjects, n_raters = ratings_matrix.shape
        categories = np.unique(ratings_matrix)
        n_categories = len(categories)
        
        # Calculate proportion of ratings in each category
        category_counts = np.zeros((n_subjects, n_categories))
        for i, cat in enumerate(categories):
            category_counts[:, i] = np.sum(ratings_matrix == cat, axis=1)
        
        # Calculate P_i (agreement for each subject)
        P_i = (np.sum(category_counts ** 2, axis=1) - n_raters) / (n_raters * (n_raters - 1))
        P_bar = np.mean(P_i)
        
        # Calculate P_e (expected agreement by chance)
        p_j = np.sum(category_counts, axis=0) / (n_subjects * n_raters)
        P_e = np.sum(p_j ** 2)
        
        # Calculate Fleiss' Kappa
        kappa = (P_bar - P_e) / (1 - P_e) if P_e < 1 else 0
        
        return {
            'kappa': kappa,
            'P_bar': P_bar,
            'P_e': P_e,
            'interpretation': InterRaterReliability._interpret_kappa(kappa)
        }
    
    @staticmethod
    def _interpret_kappa(kappa: float) -> str:
        if kappa < 0.20:
            return "Poor agreement"
        elif kappa < 0.40:
            return "Fair agreement"
        elif kappa < 0.60:
            return "Moderate agreement"
        elif kappa < 0.80:
            return "Good agreement"
        else:
            return "Excellent agreement"


class DisagreementAnalysis:
    """
    Analyze and manage disagreements between evaluators.
    """
    
    def __init__(self):
        self.disagreements = []
    
    def identify_disagreements(self, evaluations: List[Dict]) -> List[Dict]:
        """
        Identify cases where evaluators disagree.
        """
        disagreements = []
        
        # Group by sample_id
        by_sample = {}
        for eval in evaluations:
            sample_id = eval['sample_id']
            if sample_id not in by_sample:
                by_sample[sample_id] = []
            by_sample[sample_id].append(eval)
        
        for sample_id, evals in by_sample.items():
            if len(evals) < 2:
                continue
            
            # Check each dimension
            for dimension in evals[0]['ratings'].keys():
                ratings = [e['ratings'][dimension]['rating'] for e in evals]
                
                if max(ratings) - min(ratings) >= 2:  # Significant disagreement
                    disagreements.append({
                        'sample_id': sample_id,
                        'dimension': dimension,
                        'ratings': ratings,
                        'spread': max(ratings) - min(ratings),
                        'evaluators': [e['evaluator_id'] for e in evals]
                    })
        
        return disagreements
    
    def resolution_strategies(self, disagreement: Dict) -> List[str]:
        """
        Suggest strategies for resolving disagreement.
        """
        strategies = []
        spread = disagreement['spread']
        
        if spread >= 3:
            strategies.append("Escalate to senior expert for adjudication")
            strategies.append("Review and clarify evaluation guidelines")
        elif spread == 2:
            strategies.append("Discussion between evaluators")
            strategies.append("Add third evaluator as tiebreaker")
        
        strategies.append("Review specific evaluation criteria for this dimension")
        strategies.append("Check if evaluators need additional training")
        
        return strategies
```

### Annotation Guidelines

```markdown
# Example: AI Output Evaluation Guidelines

## Purpose
These guidelines ensure consistent evaluation of AI-generated content.

## Before You Begin
1. Read through all guidelines completely
2. Complete the practice examples
3. Ask questions if anything is unclear

## Rating Scale (1-5)

### Accuracy
- **5**: All information is factually correct
- **4**: Minor inaccuracies that don't affect usefulness
- **3**: Some inaccuracies that may cause confusion
- **2**: Significant errors that reduce value
- **1**: Mostly or completely incorrect

### Key Principles
1. Rate based on the output alone, not what you know
2. Consider the intended audience
3. When uncertain, use the middle rating (3)
4. Document specific issues in comments

## Common Scenarios

### Scenario A: Partially Correct Answer
The AI provides an answer that is mostly correct but includes one minor error.
**Guidance**: Rate as 4 for accuracy, note the specific error in comments.

### Scenario B: Correct but Incomplete
The answer is accurate but missing important context.
**Guidance**: Rate accuracy as 5, but completeness as 3.

## What NOT to Do
- Do not penalize for style preferences
- Do not assume information not in the output
- Do not rate based on previous samples
```

### Balancing Automation and Human Judgment

```
Automation vs. Human Judgment Matrix:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│                    LOW HUMAN JUDGMENT    HIGH HUMAN JUDGMENT            │
│                    ──────────────────    ────────────────────           │
│                                                                          │
│  HIGH AUTOMATION   Standard QA checks    Automated screening +          │
│                    Format validation     human review of flags          │
│                    Regression testing    Confidence-based sampling      │
│                                                                          │
│  ───────────────────────────────────────────────────────────────────    │
│                                                                          │
│  LOW AUTOMATION    Edge case testing     Creative evaluation            │
│                    Initial exploration   Ethical review                 │
│                    Small sample review   Complex domain assessment      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```python
"""
Hybrid Evaluation Strategy
"""

class HybridEvaluationPipeline:
    """
    Combine automated and human evaluation efficiently.
    """
    
    def __init__(self, model, automated_checks, human_review_budget):
        self.model = model
        self.automated_checks = automated_checks
        self.human_budget = human_review_budget
        self.priority_queue = []
    
    def process_sample(self, sample) -> dict:
        """
        Process sample through hybrid pipeline.
        """
        result = {
            'sample': sample,
            'automated_results': {},
            'requires_human': False,
            'priority': 0,
            'human_result': None
        }
        
        # Run automated checks
        for check_name, check_fn in self.automated_checks.items():
            check_result = check_fn(sample)
            result['automated_results'][check_name] = check_result
            
            # Flag for human review if check fails or is uncertain
            if check_result.get('status') == 'failed':
                result['requires_human'] = True
                result['priority'] += 2
            elif check_result.get('status') == 'uncertain':
                result['requires_human'] = True
                result['priority'] += 1
        
        return result
    
    def allocate_human_review(self, samples: list) -> list:
        """
        Decide which samples get human review within budget.
        """
        processed = [self.process_sample(s) for s in samples]
        
        # Sort by priority
        need_review = [p for p in processed if p['requires_human']]
        need_review.sort(key=lambda x: -x['priority'])
        
        # Allocate within budget
        for_review = need_review[:self.human_budget]
        
        # Also add random sample for quality assurance
        automated_only = [p for p in processed if not p['requires_human']]
        random_sample = np.random.choice(
            len(automated_only),
            size=min(5, len(automated_only)),
            replace=False
        )
        
        for i in random_sample:
            automated_only[i]['requires_human'] = True
            automated_only[i]['priority'] = 0  # Random QA check
            for_review.append(automated_only[i])
        
        return for_review
```

### Documentation and Escalation

```python
"""
Expert Evaluation Documentation
"""

class ExpertEvaluationReport:
    """
    Document and report expert evaluation findings.
    """
    
    def __init__(self):
        self.evaluations = []
        self.findings = []
        self.escalations = []
    
    def add_evaluation(self, evaluation: dict):
        """Record an evaluation."""
        self.evaluations.append(evaluation)
    
    def add_finding(self, finding: dict):
        """Record a significant finding."""
        self.findings.append({
            **finding,
            'timestamp': datetime.now().isoformat(),
            'status': 'new'
        })
    
    def escalate(self, sample_id: str, reason: str, 
                 severity: str, recommended_action: str):
        """Escalate an issue for higher-level review."""
        self.escalations.append({
            'sample_id': sample_id,
            'reason': reason,
            'severity': severity,
            'recommended_action': recommended_action,
            'timestamp': datetime.now().isoformat(),
            'resolved': False
        })
    
    def generate_summary_report(self) -> str:
        """Generate evaluation summary report."""
        # Calculate statistics
        total_evals = len(self.evaluations)
        
        if total_evals == 0:
            return "No evaluations recorded."
        
        avg_ratings = {}
        for eval in self.evaluations:
            for dim, data in eval.get('ratings', {}).items():
                if dim not in avg_ratings:
                    avg_ratings[dim] = []
                if data.get('rating'):
                    avg_ratings[dim].append(data['rating'])
        
        report = f"""
EXPERT EVALUATION SUMMARY
═════════════════════════

Overview
────────
• Total evaluations: {total_evals}
• Evaluators involved: {len(set(e.get('evaluator_id') for e in self.evaluations))}
• Findings recorded: {len(self.findings)}
• Escalations: {len(self.escalations)}

Average Ratings
───────────────
"""
        for dim, ratings in avg_ratings.items():
            if ratings:
                avg = np.mean(ratings)
                report += f"• {dim}: {avg:.2f}/5.0\n"
        
        if self.findings:
            report += "\nKey Findings\n────────────\n"
            for i, finding in enumerate(self.findings[:5], 1):
                report += f"{i}. {finding.get('description', 'No description')}\n"
        
        if self.escalations:
            report += f"\nOpen Escalations ({len([e for e in self.escalations if not e['resolved']])})\n"
            report += "─────────────────\n"
            for esc in self.escalations:
                if not esc['resolved']:
                    report += f"• [{esc['severity']}] {esc['reason']}\n"
        
        return report
```

## Code Example

Complete expert evaluation workflow:

```python
"""
Complete Expert Evaluation System
"""

import numpy as np
from datetime import datetime
from typing import List, Dict
import json

class ExpertEvaluationSystem:
    """
    Comprehensive system for managing expert evaluations.
    """
    
    def __init__(self, evaluation_dimensions: List[str] = None):
        self.dimensions = evaluation_dimensions or [
            'accuracy', 'helpfulness', 'clarity', 'safety'
        ]
        self.evaluations = []
        self.evaluators = {}
        self.guidelines = {}
        self.irr = InterRaterReliability()
    
    def register_evaluator(self, evaluator_id: str, 
                           role: str, expertise: List[str]):
        """Register a new evaluator."""
        self.evaluators[evaluator_id] = {
            'id': evaluator_id,
            'role': role,
            'expertise': expertise,
            'evaluations_completed': 0,
            'registered': datetime.now().isoformat()
        }
    
    def create_evaluation_task(self, sample_id: str, 
                                content: str,
                                context: str = None) -> dict:
        """Create an evaluation task."""
        return {
            'task_id': f"eval_{sample_id}_{datetime.now().strftime('%Y%m%d%H%M%S')}",
            'sample_id': sample_id,
            'content': content,
            'context': context,
            'ratings': {
                dim: {'question': f'Rate the {dim} of this output (1-5)',
                      'rating': None}
                for dim in self.dimensions
            },
            'free_text_feedback': '',
            'errors_identified': [],
            'evaluator_id': None,
            'completed': False,
            'timestamp': None
        }
    
    def submit_evaluation(self, task: dict, evaluator_id: str):
        """Submit a completed evaluation."""
        task['evaluator_id'] = evaluator_id
        task['completed'] = True
        task['timestamp'] = datetime.now().isoformat()
        
        self.evaluations.append(task)
        self.evaluators[evaluator_id]['evaluations_completed'] += 1
        
        return task
    
    def calculate_agreement(self, sample_id: str = None) -> dict:
        """Calculate inter-rater agreement."""
        if sample_id:
            evals = [e for e in self.evaluations if e['sample_id'] == sample_id]
        else:
            evals = self.evaluations
        
        if len(evals) < 2:
            return {'error': 'Not enough evaluations for agreement calculation'}
        
        # Group by sample
        by_sample = {}
        for e in evals:
            sid = e['sample_id']
            if sid not in by_sample:
                by_sample[sid] = []
            by_sample[sid].append(e)
        
        # Calculate agreement for each dimension
        agreement_by_dimension = {}
        
        for dim in self.dimensions:
            pairs = []
            for sid, sample_evals in by_sample.items():
                if len(sample_evals) >= 2:
                    ratings = [e['ratings'][dim]['rating'] for e in sample_evals[:2]]
                    if all(r is not None for r in ratings):
                        pairs.append(ratings)
            
            if pairs:
                ratings_1 = [p[0] for p in pairs]
                ratings_2 = [p[1] for p in pairs]
                kappa = self.irr.cohens_kappa(ratings_1, ratings_2)
                agreement_by_dimension[dim] = kappa
        
        return agreement_by_dimension
    
    def identify_problematic_samples(self, 
                                      min_ratings: int = 2,
                                      threshold: float = 3.0) -> List[dict]:
        """Identify samples with low ratings."""
        problematic = []
        
        by_sample = {}
        for e in self.evaluations:
            sid = e['sample_id']
            if sid not in by_sample:
                by_sample[sid] = []
            by_sample[sid].append(e)
        
        for sid, evals in by_sample.items():
            if len(evals) < min_ratings:
                continue
            
            for dim in self.dimensions:
                ratings = [e['ratings'][dim]['rating'] for e in evals 
                          if e['ratings'][dim]['rating'] is not None]
                
                if ratings and np.mean(ratings) < threshold:
                    problematic.append({
                        'sample_id': sid,
                        'dimension': dim,
                        'mean_rating': np.mean(ratings),
                        'n_ratings': len(ratings),
                        'evaluators': [e['evaluator_id'] for e in evals]
                    })
        
        return sorted(problematic, key=lambda x: x['mean_rating'])
    
    def generate_full_report(self) -> str:
        """Generate comprehensive evaluation report."""
        if not self.evaluations:
            return "No evaluations recorded."
        
        # Aggregate statistics
        total_evals = len(self.evaluations)
        unique_samples = len(set(e['sample_id'] for e in self.evaluations))
        
        # Average ratings by dimension
        avg_by_dim = {}
        for dim in self.dimensions:
            ratings = [e['ratings'][dim]['rating'] for e in self.evaluations
                      if e['ratings'][dim]['rating'] is not None]
            if ratings:
                avg_by_dim[dim] = {
                    'mean': np.mean(ratings),
                    'std': np.std(ratings),
                    'min': min(ratings),
                    'max': max(ratings)
                }
        
        # Inter-rater agreement
        agreement = self.calculate_agreement()
        
        # Problematic samples
        problematic = self.identify_problematic_samples()
        
        report = f"""
╔══════════════════════════════════════════════════════════════════════════╗
║                    EXPERT EVALUATION REPORT                               ║
╠══════════════════════════════════════════════════════════════════════════╣

OVERVIEW
────────
• Total evaluations: {total_evals}
• Unique samples evaluated: {unique_samples}
• Active evaluators: {len(self.evaluators)}
• Evaluation period: {self.evaluations[0]['timestamp'][:10]} to {self.evaluations[-1]['timestamp'][:10]}

RATINGS BY DIMENSION
────────────────────
"""
        for dim, stats in avg_by_dim.items():
            report += f"\n{dim.upper()}:\n"
            report += f"  Mean: {stats['mean']:.2f} (±{stats['std']:.2f})\n"
            report += f"  Range: {stats['min']:.0f} - {stats['max']:.0f}\n"
        
        report += """
INTER-RATER AGREEMENT
─────────────────────
"""
        for dim, kappa_result in agreement.items():
            if isinstance(kappa_result, dict):
                report += f"• {dim}: κ={kappa_result['kappa']:.3f} ({kappa_result['interpretation']})\n"
        
        if problematic:
            report += f"""
SAMPLES REQUIRING ATTENTION ({len(problematic)})
─────────────────────────────────
"""
            for i, prob in enumerate(problematic[:10], 1):
                report += f"{i}. Sample {prob['sample_id']}: {prob['dimension']} = {prob['mean_rating']:.2f}\n"
        
        return report


# Example usage
if __name__ == "__main__":
    # Initialize system
    system = ExpertEvaluationSystem()
    
    # Register evaluators
    system.register_evaluator("exp1", "domain_expert", ["medical", "clinical"])
    system.register_evaluator("exp2", "domain_expert", ["medical", "research"])
    
    # Create and submit evaluations
    for i in range(10):
        task = system.create_evaluation_task(
            sample_id=f"sample_{i}",
            content=f"AI output {i} content here...",
            context="Medical query response"
        )
        
        # Simulate ratings
        for dim in system.dimensions:
            task['ratings'][dim]['rating'] = np.random.randint(1, 6)
        
        system.submit_evaluation(task, f"exp{(i % 2) + 1}")
    
    # Generate report
    print(system.generate_full_report())
```

## Summary

- **Human evaluation** is essential for subjective quality, safety, and domain-specific assessment
- **Expert selection** should match task requirements (domain experts, users, trained annotators)
- **Evaluation methods** include Likert scales, comparative assessment, and error taxonomy
- **Inter-rater reliability** measures agreement; use Cohen's/Fleiss' Kappa
- **Clear guidelines** are critical for consistent evaluation
- **Hybrid approaches** combine automation with targeted human review
- **Document everything:** findings, escalations, and resolution processes

## Additional Resources

- [Annotation Guidelines Best Practices](https://brat.nlplab.org/annotation-guidelines.html)
- [Inter-Rater Reliability Calculator](https://www.graphpad.com/quickcalcs/kappa1/)
- [Human Evaluation of NLG Systems](https://aclanthology.org/2020.inlg-1.23/)

