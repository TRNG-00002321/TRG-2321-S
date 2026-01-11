# Testing Dataset Audits

## Learning Objectives

- Understand the importance of dataset quality for AI testing
- Apply data profiling techniques to assess dataset characteristics
- Identify data imbalances and representation issues
- Assess label quality and consistency
- Detect data drift over time
- Implement dataset versioning and lineage tracking
- Evaluate synthetic data appropriateness
- Document and report audit findings

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

"Garbage in, garbage out" is the oldest principle in data processing—and it applies doubly to AI. Your model is only as good as the data it learned from. Test your model on biased test data, and you'll get biased test results that don't reflect real performance.

**Dataset auditing matters because:**

1. **Training data bias → Model bias:** If training data underrepresents certain groups, the model will perform worse for them
2. **Test data bias → False confidence:** If test data has the same biases as training data, metrics will look good even when real-world performance is poor
3. **Label errors → Wrong learning:** Mislabeled examples teach the model wrong patterns
4. **Data leakage → Inflated metrics:** If test data accidentally overlaps with training data, your accuracy is fake

As a quality engineer, auditing datasets is as important as testing the model itself.

## The Concept
```
Data Quality Impact Chain:
┌──────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  POOR DATA                                                               │
│     │                                                                    │
│     ├──▶ Biased training ──▶ Biased model ──▶ Biased decisions         │
│     │                                                                    │
│     ├──▶ Missing groups ──▶ Poor generalization ──▶ Failures in prod    │
│     │                                                                    │
│     ├──▶ Wrong labels ──▶ Learning wrong patterns ──▶ Wrong predictions│
│     │                                                                    │
│     └──▶ Stale data ──▶ Outdated patterns ──▶ Model degradation        │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────   │
│                                                                          │
│  QUALITY DATA                                                            │
│     │                                                                    │
│     ├──▶ Representative samples ──▶ Fair model ──▶ Equitable outcomes  │
│     │                                                                    │
│     ├──▶ Accurate labels ──▶ Correct learning ──▶ Reliable predictions │
│     │                                                                    │
│     └──▶ Fresh data ──▶ Current patterns ──▶ Relevant decisions        │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```
### What is a Dataset Audit?

A **dataset audit** systematically examines a dataset to identify quality issues, biases, and risks before that data is used to train or evaluate a model.

**Key questions a dataset audit answers:**

- Does the data represent the real-world population the model will serve?
- Are there biased patterns that the model might learn?
- Are the labels accurate and consistent?
- Has the data changed since the model was trained?
- Is the data properly versioned and documented?

### Data Profiling Basics

**Data profiling** is the systematic analysis of data to understand its structure, content, and quality.
**Data profiling** creates a statistical summary of your dataset.

**For numeric columns:**
- Count, missing values
- Min, max, mean, median
- Standard deviation, percentiles
- Distribution shape (normal? skewed?)

**For categorical columns:**
- Unique values count
- Value frequencies
- Missing values
- Rare categories

**Simple profiling code:**

```python
def profile_dataset(df):
    """Create basic profile of a dataset."""
    profile = {}
    
    for column in df.columns:
        col_data = df[column]
        col_profile = {
            "count": len(col_data),
            "missing": col_data.isna().sum(),
            "missing_pct": col_data.isna().mean() * 100
        }
        
        if col_data.dtype in ['int64', 'float64']:
            col_profile["type"] = "numeric"
            col_profile["min"] = col_data.min()
            col_profile["max"] = col_data.max()
            col_profile["mean"] = col_data.mean()
            col_profile["std"] = col_data.std()
        else:
            col_profile["type"] = "categorical"
            col_profile["unique"] = col_data.nunique()
            col_profile["top_values"] = col_data.value_counts().head(5).to_dict()
        
        profile[column] = col_profile
    
    return profile
```

### Identifying Data Imbalances

**Class imbalance:** When some outcome classes are much more common than others.
```
Class Imbalance Illustration:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  BALANCED DATASET:                    IMBALANCED DATASET:               │
│  ─────────────────                    ───────────────────               │
│                                                                          │
│  Fraud: ████████ (50%)               Fraud: █ (1%)                      │
│  Normal: ████████ (50%)              Normal: ███████████████████ (99%)  │
│                                                                          │
│  Model sees equal examples           Model sees mostly "normal"         │
│  of each class                       May learn to always predict normal │
│                                       to achieve 99% "accuracy"          │
│                                                                          │
│  PROBLEM: A model predicting "normal" for everything is 99% accurate   │
│           but catches 0% of fraud!                                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

Example: Fraud detection with 99% legitimate transactions and 1% fraud.

**Why it matters:** Models can achieve 99% accuracy by always predicting "legitimate"—but that's useless for catching fraud.

**Demographic imbalance:** When some demographic groups are underrepresented.

Example: Training data has 80% male, 20% female applicants, but real-world has 50/50.

**Why it matters:** Model will be better calibrated for the majority group.

**How to check:**

```python
def check_class_balance(df, target_column):
    """Check class distribution."""
    counts = df[target_column].value_counts()
    percentages = df[target_column].value_counts(normalize=True) * 100
    
    return {
        "counts": counts.to_dict(),
        "percentages": percentages.to_dict(),
        "imbalance_ratio": counts.max() / counts.min()
    }

def check_demographic_representation(df, protected_columns):
    """Check representation across demographic groups."""
    for col in protected_columns:
        print(f"\n{col} distribution:")
        print(df[col].value_counts(normalize=True) * 100)
```

### Label Quality Assessment

Labels in training data tell the model what's "correct." Wrong labels teach wrong patterns.
```
Label Quality Issues:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  ISSUE                    EXAMPLE                    DETECTION          │
│  ─────────────────────    ─────────────────────      ────────────────   │
│                                                                          │
│  Mislabeled data          Cat labeled as dog         Human review,      │
│                                                       model confidence   │
│                                                                          │
│  Inconsistent labeling    Same image labeled both    Inter-rater        │
│                           "happy" and "neutral"      agreement check    │
│                                                                          │
│  Missing labels           Unlabeled samples in       Null check,        │
│                           training set               coverage analysis  │
│                                                                          │
│  Label noise              Rushed annotation with     Statistical        │
│                           random errors              outlier detection  │
│                                                                          │
│  Systematic labeler bias  Labelers consistently      Labeler agreement  │
│                           label one way              analysis           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```
**Sources of label errors:**

1. **Human annotator mistakes:** People make errors, especially when tired or confused
2. **Ambiguous cases:** Some examples genuinely have no clear correct label
3. **Annotator disagreement:** Different people interpret labeling guidelines differently
4. **Stale labels:** What was correct historically may not be correct now

**How to assess label quality:**

```python
def assess_label_consistency(annotations):
    """
    Check consistency when same item has multiple annotations.
    
    Args:
        annotations: List of dicts like {"item_id": 1, "annotator": "A", "label": "positive"}
    """
    from collections import defaultdict
    
    # Group annotations by item
    by_item = defaultdict(list)
    for ann in annotations:
        by_item[ann["item_id"]].append(ann["label"])
    
    # Check agreement
    consistent = 0
    inconsistent = 0
    
    for item_id, labels in by_item.items():
        if len(set(labels)) == 1:
            consistent += 1
        else:
            inconsistent += 1
    
    total = consistent + inconsistent
    return {
        "consistent": consistent,
        "inconsistent": inconsistent,
        "agreement_rate": consistent / total if total > 0 else 0
    }
```

### Data Drift Detection

**Data drift** occurs when production data differs from training data.

**Types of drift:**
- **Feature drift:** Input distributions change
- **Label drift:** Outcome distributions change
- **Concept drift:** Relationship between inputs and outputs changes

```
Types of Data Drift:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  COVARIATE DRIFT (Feature Distribution Change)                          │
│  ─────────────────────────────────────────────                          │
│  Training:  User age distribution peaked at 25-35                       │
│  Production: User age distribution peaked at 45-55                      │
│  Impact: Model may perform poorly on older demographics                 │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  CONCEPT DRIFT (Relationship Change)                                    │
│  ─────────────────────────────────────                                  │
│  Training: "Corona" associated with beer                                │
│  Production (2020+): "Corona" associated with virus                     │
│  Impact: Model predictions become incorrect                             │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  LABEL DRIFT (Target Distribution Change)                               │
│  ─────────────────────────────────────────                              │
│  Training: 5% fraud rate                                                │
│  Production: 15% fraud rate (new fraud pattern emerged)                 │
│  Impact: Model thresholds become inappropriate                          │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

**Simple drift check:**

```python
def check_drift(reference_data, current_data, columns):
    """
    Compare reference (training) and current (production) data.
    
    Returns alert if significant drift detected.
    """
    alerts = []
    
    for col in columns:
        ref_mean = reference_data[col].mean()
        cur_mean = current_data[col].mean()
        
        # Calculate percent change
        pct_change = abs(cur_mean - ref_mean) / ref_mean * 100
        
        if pct_change > 10:  # 10% threshold
            alerts.append({
                "column": col,
                "reference_mean": ref_mean,
                "current_mean": cur_mean,
                "percent_change": pct_change
            })
    
    return alerts
```

### Dataset Versioning

Just like code, datasets should be versioned:

**Why version datasets:**
- **Reproducibility:** Know exactly what data trained/tested a model
- **Debugging:** Trace issues to specific data versions
- **Auditing:** Demonstrate what data was used when

Track what data was used when:

```
Dataset Versioning Best Practices:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  VERSION TRACKING                                                       │
│  ────────────────                                                       │
│  • Unique identifier for each dataset version                           │
│  • Timestamp of creation                                                │
│  • Hash/checksum of contents                                            │
│  • Size and row count                                                   │
│                                                                          │
│  LINEAGE TRACKING                                                       │
│  ────────────────                                                       │
│  • Source systems (where did data come from?)                           │
│  • Transformations applied                                              │
│  • Processing scripts used                                              │
│  • Who created it and when                                              │
│                                                                          │
│  EXAMPLE VERSION RECORD:                                                │
│  ────────────────────────                                               │
│  {                                                                       │
│    "version": "train_v2.3.0",                                           │
│    "created": "2024-03-15T10:30:00Z",                                   │
│    "created_by": "data-pipeline-job-1234",                              │
│    "sha256": "a3f2b1c...",                                              │
│    "row_count": 1500000,                                                │
│    "sources": ["crm_export_20240315", "web_events_202403"],             │
│    "transforms": ["remove_pii", "normalize_amounts", "encode_cats"],    │
│    "parent_version": "train_v2.2.0"                                     │
│  }                                                                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

**Simple versioning approach:**

```python
import hashlib
import json
from datetime import datetime

def version_dataset(df, name):
    """Create version metadata for a dataset."""
    # Calculate checksum of data
    data_str = df.to_json()
    checksum = hashlib.md5(data_str.encode()).hexdigest()
    
    version = {
        "name": name,
        "version_id": checksum[:8],
        "created_at": datetime.now().isoformat(),
        "rows": len(df),
        "columns": list(df.columns),
        "checksum": checksum
    }
    
    return version
```

### Synthetic Data Considerations

Sometimes you can't use real data (privacy, scarcity). Synthetic data is generated to mimic real data.
```
Synthetic Data Audit Checklist:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  FIDELITY                                                               │
│  ────────                                                               │
│  □ Does synthetic data have similar statistical properties?             │
│  □ Are correlations between features preserved?                         │
│  □ Are edge cases and rare events represented?                          │
│                                                                          │
│  PRIVACY                                                                │
│  ───────                                                                │
│  □ Can any real individuals be identified?                              │
│  □ Are sensitive combinations possible?                                 │
│  □ Has privacy audit been conducted?                                    │
│                                                                          │
│  COVERAGE                                                               │
│  ────────                                                               │
│  □ Are all demographic groups represented?                              │
│  □ Are all feature value ranges covered?                                │
│  □ Is the temporal range appropriate?                                   │
│                                                                          │
│  VALIDITY                                                               │
│  ────────                                                               │
│  □ Are impossible combinations avoided?                                 │
│  □ Are business rules respected?                                        │
│  □ Are referential relationships maintained?                            │
│                                                                          │
│  DOCUMENTATION                                                          │
│  ─────────────                                                          │
│  □ Is generation method documented?                                     │
│  □ Are known limitations documented?                                    │
│  □ Is appropriate use guidance provided?                                │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

**Risks with synthetic test data:**
- May not capture rare edge cases
- May not represent real-world correlations
- May hide biases that exist in real data
- May give false confidence in model performance

**Best practices:**
- Validate synthetic data distribution against real samples
- Use synthetic for development, real for final evaluation
- Document when and why synthetic data was used

### Audit Documentation

An audit should produce clear documentation:

```python
def generate_audit_report(dataset, audit_results):
    """Generate audit summary report."""
    report = f"""
DATASET AUDIT REPORT
====================
Dataset: {dataset.name}
Date: {datetime.now().strftime('%Y-%m-%d')}
Rows: {len(dataset)}

DATA QUALITY
------------
Missing values: {audit_results['missing_pct']:.1f}%
Duplicate rows: {audit_results['duplicate_count']}

CLASS BALANCE
-------------
{format_class_distribution(audit_results['class_distribution'])}

DEMOGRAPHIC REPRESENTATION
--------------------------
{format_demographic_data(audit_results['demographics'])}

DRIFT ALERTS
------------
{format_drift_alerts(audit_results['drift_alerts'])}

RECOMMENDATIONS
---------------
{format_recommendations(audit_results)}
"""
    return report
```

## Code Example

```python
"""
Simple Dataset Audit Framework
"""

class DatasetAuditor:
    """Audit a dataset for quality issues."""
    
    def __init__(self, df):
        self.df = df
        self.issues = []
    
    def check_missing_values(self, threshold=5.0):
        """Flag columns with too many missing values."""
        for col in self.df.columns:
            missing_pct = self.df[col].isna().mean() * 100
            if missing_pct > threshold:
                self.issues.append({
                    "type": "missing_values",
                    "column": col,
                    "severity": "high" if missing_pct > 20 else "medium",
                    "detail": f"{missing_pct:.1f}% missing"
                })
    
    def check_class_balance(self, target_column, threshold=10):
        """Flag class imbalance."""
        counts = self.df[target_column].value_counts()
        ratio = counts.max() / counts.min()
        
        if ratio > threshold:
            self.issues.append({
                "type": "class_imbalance",
                "column": target_column,
                "severity": "high",
                "detail": f"Imbalance ratio {ratio:.1f}:1"
            })
    
    def check_duplicates(self):
        """Check for duplicate rows."""
        dup_count = self.df.duplicated().sum()
        if dup_count > 0:
            self.issues.append({
                "type": "duplicates",
                "severity": "medium",
                "detail": f"{dup_count} duplicate rows"
            })
    
    def run_full_audit(self, target_column=None):
        """Run all audit checks."""
        self.check_missing_values()
        self.check_duplicates()
        if target_column:
            self.check_class_balance(target_column)
        
        return {
            "total_issues": len(self.issues),
            "high_severity": sum(1 for i in self.issues if i["severity"] == "high"),
            "issues": self.issues
        }
```

## Summary

- **Dataset audits** identify quality issues before they affect model performance
- **Data profiling** creates statistical summaries of dataset characteristics
- **Class/demographic imbalance** can cause biased or poorly-calibrated models
- **Label quality** directly affects what the model learns
- **Data drift** detection catches when production differs from training
- **Versioning** enables reproducibility and debugging
- **Synthetic data** has risks—validate against real data
- **Documentation** makes audit findings actionable

## Additional Resources

- [Data Quality for Machine Learning (O'Reilly)](https://www.oreilly.com/library/view/data-quality-fundamentals/9781098112035/) - Book on data quality
- [Great Expectations](https://greatexpectations.io/) - Data validation framework
- [Pandas Profiling](https://github.com/ydataai/ydata-profiling) - Automated data profiling
