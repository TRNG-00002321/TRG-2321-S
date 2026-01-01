# Testing Dataset Audits

## Learning Objectives

- Understand the critical importance of dataset quality in AI systems
- Apply data profiling techniques to identify issues
- Detect data imbalances and their implications
- Assess label quality in training datasets
- Detect data drift between training and production
- Implement dataset versioning and lineage tracking
- Evaluate synthetic data considerations
- Document and report audit findings effectively

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

"Garbage in, garbage out" has never been more relevant than in the age of AI. Your model can only be as good as the data it learns from. A flawlessly architected neural network trained on biased, incomplete, or mislabeled data will produce biased, incomplete, or incorrect predictions—consistently and at scale.

Dataset auditing is the foundation of trustworthy AI. Before you test the model, you must test the data. This module teaches you to examine datasets with the rigor of a forensic investigator: profiling distributions, hunting for imbalances, validating labels, tracking lineage, and detecting drift. These skills transform you from someone who tests AI outputs to someone who ensures AI inputs are worthy of trust.

## The Concept

### The Critical Role of Dataset Quality

```
Data Quality Impact Chain:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  POOR DATA                                                              │
│     │                                                                   │
│     ├──▶ Biased training ──▶ Biased model ──▶ Biased decisions         │
│     │                                                                   │
│     ├──▶ Missing groups ──▶ Poor generalization ──▶ Failures in prod   │
│     │                                                                   │
│     ├──▶ Wrong labels ──▶ Learning wrong patterns ──▶ Wrong predictions│
│     │                                                                   │
│     └──▶ Stale data ──▶ Outdated patterns ──▶ Model degradation        │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│  QUALITY DATA                                                           │
│     │                                                                   │
│     ├──▶ Representative samples ──▶ Fair model ──▶ Equitable outcomes  │
│     │                                                                   │
│     ├──▶ Accurate labels ──▶ Correct learning ──▶ Reliable predictions │
│     │                                                                   │
│     └──▶ Fresh data ──▶ Current patterns ──▶ Relevant decisions        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Data Profiling Techniques

Data profiling is the systematic analysis of data to understand its structure, content, and quality.

#### Basic Statistical Profiling

```python
"""
Basic Data Profiling for ML Datasets
"""

import pandas as pd
import numpy as np
from typing import Dict, Any

class DataProfiler:
    """
    Comprehensive data profiling for ML dataset auditing.
    """
    
    def __init__(self, df: pd.DataFrame):
        self.df = df
        self.profile = {}
        
    def basic_stats(self) -> Dict[str, Any]:
        """Generate basic statistics for the dataset."""
        stats = {
            'n_rows': len(self.df),
            'n_columns': len(self.df.columns),
            'memory_usage_mb': self.df.memory_usage(deep=True).sum() / 1e6,
            'duplicate_rows': self.df.duplicated().sum(),
            'duplicate_row_pct': self.df.duplicated().mean() * 100,
        }
        return stats
    
    def column_profiles(self) -> Dict[str, Dict]:
        """Profile each column individually."""
        profiles = {}
        
        for col in self.df.columns:
            col_data = self.df[col]
            profile = {
                'dtype': str(col_data.dtype),
                'n_unique': col_data.nunique(),
                'n_missing': col_data.isna().sum(),
                'missing_pct': col_data.isna().mean() * 100,
            }
            
            # Numeric columns
            if np.issubdtype(col_data.dtype, np.number):
                profile.update({
                    'mean': col_data.mean(),
                    'std': col_data.std(),
                    'min': col_data.min(),
                    'max': col_data.max(),
                    'median': col_data.median(),
                    'q25': col_data.quantile(0.25),
                    'q75': col_data.quantile(0.75),
                    'n_zeros': (col_data == 0).sum(),
                    'n_negative': (col_data < 0).sum(),
                })
            
            # Categorical columns
            else:
                top_values = col_data.value_counts().head(5)
                profile.update({
                    'top_values': top_values.to_dict(),
                    'is_unique': col_data.nunique() == len(col_data),
                })
            
            profiles[col] = profile
            
        return profiles
    
    def detect_anomalies(self) -> Dict[str, list]:
        """Detect potential data anomalies."""
        anomalies = {
            'high_cardinality': [],
            'low_variance': [],
            'high_missing': [],
            'potential_id_columns': [],
            'suspicious_distributions': [],
        }
        
        for col in self.df.columns:
            col_data = self.df[col]
            
            # High cardinality categorical
            if col_data.dtype == 'object':
                if col_data.nunique() > 100:
                    anomalies['high_cardinality'].append(col)
            
            # Low variance numeric
            if np.issubdtype(col_data.dtype, np.number):
                if col_data.std() < 0.001:
                    anomalies['low_variance'].append(col)
            
            # High missing rate
            if col_data.isna().mean() > 0.1:
                anomalies['high_missing'].append(col)
            
            # Potential ID column
            if col_data.nunique() == len(col_data):
                anomalies['potential_id_columns'].append(col)
        
        return anomalies
    
    def generate_report(self) -> str:
        """Generate comprehensive profiling report."""
        basic = self.basic_stats()
        columns = self.column_profiles()
        anomalies = self.detect_anomalies()
        
        report = f"""
════════════════════════════════════════════════════════════════════════
                        DATA PROFILING REPORT
════════════════════════════════════════════════════════════════════════

BASIC STATISTICS
────────────────
• Rows: {basic['n_rows']:,}
• Columns: {basic['n_columns']}
• Memory Usage: {basic['memory_usage_mb']:.2f} MB
• Duplicate Rows: {basic['duplicate_rows']:,} ({basic['duplicate_row_pct']:.1f}%)

ANOMALIES DETECTED
──────────────────
• High cardinality columns: {anomalies['high_cardinality']}
• Low variance columns: {anomalies['low_variance']}
• High missing rate (>10%): {anomalies['high_missing']}
• Potential ID columns: {anomalies['potential_id_columns']}

COLUMN DETAILS
──────────────
"""
        for col, profile in columns.items():
            report += f"\n{col} ({profile['dtype']}):\n"
            report += f"  - Unique: {profile['n_unique']}, Missing: {profile['missing_pct']:.1f}%\n"
            
            if 'mean' in profile:
                report += f"  - Mean: {profile['mean']:.2f}, Std: {profile['std']:.2f}\n"
                report += f"  - Range: [{profile['min']:.2f}, {profile['max']:.2f}]\n"
            
            if 'top_values' in profile:
                top = list(profile['top_values'].items())[:3]
                report += f"  - Top values: {top}\n"
        
        return report
```

### Identifying Data Imbalances

Class imbalance is a common issue that can severely affect model performance:

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

```python
def analyze_class_balance(df: pd.DataFrame, target_col: str) -> Dict:
    """
    Analyze class distribution in target variable.
    """
    distribution = df[target_col].value_counts()
    percentages = df[target_col].value_counts(normalize=True) * 100
    
    # Calculate imbalance ratio
    min_class = distribution.min()
    max_class = distribution.max()
    imbalance_ratio = max_class / min_class
    
    analysis = {
        'distribution': distribution.to_dict(),
        'percentages': percentages.to_dict(),
        'imbalance_ratio': imbalance_ratio,
        'majority_class': distribution.idxmax(),
        'minority_class': distribution.idxmin(),
        'is_severely_imbalanced': imbalance_ratio > 10,
    }
    
    # Recommendations
    if imbalance_ratio > 100:
        analysis['recommendation'] = "SEVERE imbalance. Consider SMOTE, undersampling, or class weights."
    elif imbalance_ratio > 10:
        analysis['recommendation'] = "Significant imbalance. Use stratified sampling and appropriate metrics."
    elif imbalance_ratio > 3:
        analysis['recommendation'] = "Moderate imbalance. Ensure stratified cross-validation."
    else:
        analysis['recommendation'] = "Balanced. Standard approaches should work."
    
    return analysis
```

### Demographic Imbalance Analysis

Beyond target classes, analyze representation across demographic groups:

```python
def analyze_demographic_balance(df: pd.DataFrame, 
                                 demographic_cols: list,
                                 target_col: str) -> Dict:
    """
    Analyze demographic representation and target distribution by group.
    """
    report = {}
    
    for demo_col in demographic_cols:
        group_analysis = {
            'representation': {},
            'target_distribution': {},
            'underrepresented': [],
        }
        
        # Overall representation
        representation = df[demo_col].value_counts(normalize=True) * 100
        group_analysis['representation'] = representation.to_dict()
        
        # Target distribution by group
        for group in df[demo_col].unique():
            group_data = df[df[demo_col] == group]
            target_dist = group_data[target_col].value_counts(normalize=True)
            group_analysis['target_distribution'][group] = target_dist.to_dict()
            
            # Flag underrepresented groups
            if representation[group] < 10:  # Less than 10% threshold
                group_analysis['underrepresented'].append(group)
        
        report[demo_col] = group_analysis
    
    return report
```

### Label Quality Assessment

Labels (ground truth) must be accurate for the model to learn correctly:

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

```python
def assess_label_quality(labels_df: pd.DataFrame,
                          primary_label_col: str,
                          labeler_col: str = None) -> Dict:
    """
    Assess quality of labels in a dataset.
    """
    assessment = {}
    
    # Missing labels
    missing = labels_df[primary_label_col].isna().sum()
    assessment['missing_labels'] = {
        'count': missing,
        'percentage': missing / len(labels_df) * 100
    }
    
    # Label distribution (look for suspicious patterns)
    distribution = labels_df[primary_label_col].value_counts()
    assessment['label_distribution'] = distribution.to_dict()
    
    # If multiple labelers, calculate agreement
    if labeler_col and labeler_col in labels_df.columns:
        # Group by sample and check if labelers agree
        # This assumes multiple labels per sample exist
        pass  # Implementation depends on data structure
    
    return assessment


def calculate_inter_rater_reliability(labels_by_rater: pd.DataFrame) -> float:
    """
    Calculate Cohen's Kappa for inter-rater reliability.
    """
    from sklearn.metrics import cohen_kappa_score
    
    # Assuming two columns for two raters
    rater1 = labels_by_rater.iloc[:, 0]
    rater2 = labels_by_rater.iloc[:, 1]
    
    kappa = cohen_kappa_score(rater1, rater2)
    
    # Interpretation
    if kappa < 0:
        interpretation = "No agreement"
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
```

### Data Drift Detection

Data drift occurs when production data differs from training data:

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

```python
from scipy import stats
import numpy as np

def detect_covariate_drift(reference_data: pd.DataFrame,
                            current_data: pd.DataFrame,
                            numerical_cols: list,
                            threshold: float = 0.05) -> Dict:
    """
    Detect drift in feature distributions using statistical tests.
    """
    drift_report = {}
    
    for col in numerical_cols:
        ref = reference_data[col].dropna()
        cur = current_data[col].dropna()
        
        # Kolmogorov-Smirnov test
        ks_stat, p_value = stats.ks_2samp(ref, cur)
        
        drift_report[col] = {
            'ks_statistic': ks_stat,
            'p_value': p_value,
            'drift_detected': p_value < threshold,
            'reference_mean': ref.mean(),
            'current_mean': cur.mean(),
            'mean_shift': cur.mean() - ref.mean(),
            'reference_std': ref.std(),
            'current_std': cur.std(),
        }
    
    return drift_report


def population_stability_index(reference: pd.Series, 
                                current: pd.Series,
                                bins: int = 10) -> float:
    """
    Calculate Population Stability Index (PSI).
    
    PSI < 0.1: No significant change
    0.1 <= PSI < 0.2: Moderate change, monitor
    PSI >= 0.2: Significant change, investigate
    """
    # Create bins from reference distribution
    _, bin_edges = np.histogram(reference, bins=bins)
    
    # Calculate proportions in each bin
    ref_counts, _ = np.histogram(reference, bins=bin_edges)
    cur_counts, _ = np.histogram(current, bins=bin_edges)
    
    ref_pct = ref_counts / len(reference)
    cur_pct = cur_counts / len(current)
    
    # Avoid division by zero
    ref_pct = np.where(ref_pct == 0, 0.0001, ref_pct)
    cur_pct = np.where(cur_pct == 0, 0.0001, cur_pct)
    
    # Calculate PSI
    psi = np.sum((cur_pct - ref_pct) * np.log(cur_pct / ref_pct))
    
    return psi
```

### Dataset Versioning and Lineage

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

### Synthetic Data Considerations

Synthetic data is artificially generated data that mimics real data:

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

### Audit Documentation and Reporting

```python
class DatasetAuditReport:
    """
    Generate comprehensive dataset audit report.
    """
    
    def __init__(self, dataset_name: str, dataset_version: str):
        self.dataset_name = dataset_name
        self.version = dataset_version
        self.findings = []
        self.recommendations = []
        
    def add_finding(self, category: str, severity: str, 
                    description: str, evidence: str):
        """Add an audit finding."""
        self.findings.append({
            'category': category,
            'severity': severity,  # CRITICAL, HIGH, MEDIUM, LOW
            'description': description,
            'evidence': evidence,
            'timestamp': datetime.now().isoformat()
        })
    
    def add_recommendation(self, finding_id: int, action: str, 
                           priority: str):
        """Add recommendation for a finding."""
        self.recommendations.append({
            'finding_id': finding_id,
            'action': action,
            'priority': priority
        })
    
    def generate_report(self) -> str:
        """Generate formatted audit report."""
        report = f"""
╔══════════════════════════════════════════════════════════════════════════╗
║                       DATASET AUDIT REPORT                                ║
╠══════════════════════════════════════════════════════════════════════════╣
║  Dataset: {self.dataset_name:<60}  ║
║  Version: {self.version:<60}  ║
║  Audit Date: {datetime.now().strftime('%Y-%m-%d %H:%M'):<56}  ║
╠══════════════════════════════════════════════════════════════════════════╣

EXECUTIVE SUMMARY
─────────────────
• Total Findings: {len(self.findings)}
• Critical: {sum(1 for f in self.findings if f['severity'] == 'CRITICAL')}
• High: {sum(1 for f in self.findings if f['severity'] == 'HIGH')}
• Medium: {sum(1 for f in self.findings if f['severity'] == 'MEDIUM')}
• Low: {sum(1 for f in self.findings if f['severity'] == 'LOW')}

DETAILED FINDINGS
─────────────────
"""
        for i, finding in enumerate(self.findings, 1):
            report += f"""
Finding #{i} [{finding['severity']}]
Category: {finding['category']}
Description: {finding['description']}
Evidence: {finding['evidence']}
"""
        
        report += """
RECOMMENDATIONS
───────────────
"""
        for rec in self.recommendations:
            report += f"• [{rec['priority']}] {rec['action']}\n"
        
        return report
```

## Code Example

Complete dataset audit implementation:

```python
"""
Comprehensive Dataset Audit Tool
"""

import pandas as pd
import numpy as np
from datetime import datetime
from typing import Dict, List, Any

class DatasetAuditor:
    """
    Complete dataset auditing tool for ML datasets.
    """
    
    def __init__(self, df: pd.DataFrame, name: str, version: str):
        self.df = df
        self.name = name
        self.version = version
        self.audit_results = {}
    
    def run_full_audit(self, 
                       target_col: str = None,
                       protected_attrs: List[str] = None,
                       reference_data: pd.DataFrame = None) -> Dict:
        """
        Run comprehensive dataset audit.
        """
        results = {
            'metadata': self._audit_metadata(),
            'quality': self._audit_quality(),
            'completeness': self._audit_completeness(),
            'distribution': self._audit_distributions(),
        }
        
        if target_col:
            results['target_balance'] = self._audit_target_balance(target_col)
        
        if protected_attrs:
            results['demographic_balance'] = self._audit_demographics(
                protected_attrs, target_col
            )
        
        if reference_data is not None:
            results['drift'] = self._audit_drift(reference_data)
        
        self.audit_results = results
        return results
    
    def _audit_metadata(self) -> Dict:
        """Audit basic dataset metadata."""
        return {
            'n_rows': len(self.df),
            'n_columns': len(self.df.columns),
            'columns': list(self.df.columns),
            'dtypes': {col: str(dtype) for col, dtype in self.df.dtypes.items()},
            'memory_mb': self.df.memory_usage(deep=True).sum() / 1e6,
            'audit_timestamp': datetime.now().isoformat(),
        }
    
    def _audit_quality(self) -> Dict:
        """Audit data quality issues."""
        return {
            'duplicate_rows': {
                'count': self.df.duplicated().sum(),
                'percentage': self.df.duplicated().mean() * 100,
                'status': 'PASS' if self.df.duplicated().mean() < 0.01 else 'WARN'
            },
            'constant_columns': [
                col for col in self.df.columns 
                if self.df[col].nunique() <= 1
            ],
            'high_cardinality': [
                col for col in self.df.select_dtypes(include=['object']).columns
                if self.df[col].nunique() > len(self.df) * 0.5
            ],
        }
    
    def _audit_completeness(self) -> Dict:
        """Audit data completeness."""
        completeness = {}
        
        for col in self.df.columns:
            missing = self.df[col].isna().sum()
            pct = missing / len(self.df) * 100
            
            if pct > 50:
                status = 'CRITICAL'
            elif pct > 20:
                status = 'HIGH'
            elif pct > 5:
                status = 'MEDIUM'
            elif pct > 0:
                status = 'LOW'
            else:
                status = 'PASS'
            
            completeness[col] = {
                'missing_count': missing,
                'missing_pct': pct,
                'status': status
            }
        
        return completeness
    
    def _audit_distributions(self) -> Dict:
        """Audit feature distributions."""
        distributions = {}
        
        for col in self.df.select_dtypes(include=[np.number]).columns:
            data = self.df[col].dropna()
            distributions[col] = {
                'mean': data.mean(),
                'std': data.std(),
                'min': data.min(),
                'max': data.max(),
                'skewness': data.skew(),
                'kurtosis': data.kurtosis(),
                'has_outliers': self._detect_outliers(data),
            }
        
        return distributions
    
    def _detect_outliers(self, series: pd.Series) -> bool:
        """Detect outliers using IQR method."""
        Q1 = series.quantile(0.25)
        Q3 = series.quantile(0.75)
        IQR = Q3 - Q1
        outliers = ((series < Q1 - 1.5 * IQR) | (series > Q3 + 1.5 * IQR)).sum()
        return outliers > len(series) * 0.01  # More than 1% outliers
    
    def _audit_target_balance(self, target_col: str) -> Dict:
        """Audit target variable balance."""
        distribution = self.df[target_col].value_counts()
        percentages = self.df[target_col].value_counts(normalize=True) * 100
        
        imbalance_ratio = distribution.max() / distribution.min()
        
        return {
            'distribution': distribution.to_dict(),
            'percentages': percentages.to_dict(),
            'imbalance_ratio': imbalance_ratio,
            'status': 'CRITICAL' if imbalance_ratio > 100 else 
                      'HIGH' if imbalance_ratio > 10 else
                      'MEDIUM' if imbalance_ratio > 3 else 'PASS'
        }
    
    def _audit_demographics(self, protected_attrs: List[str], 
                            target_col: str) -> Dict:
        """Audit demographic representation and fairness."""
        results = {}
        
        for attr in protected_attrs:
            if attr not in self.df.columns:
                continue
                
            representation = self.df[attr].value_counts(normalize=True) * 100
            
            # Check for underrepresentation
            underrepresented = [
                group for group, pct in representation.items() 
                if pct < 10
            ]
            
            results[attr] = {
                'representation': representation.to_dict(),
                'underrepresented_groups': underrepresented,
                'status': 'WARN' if underrepresented else 'PASS'
            }
            
            # If target column exists, check target rate by group
            if target_col and target_col in self.df.columns:
                target_rates = {}
                for group in self.df[attr].unique():
                    group_data = self.df[self.df[attr] == group]
                    rate = group_data[target_col].mean()
                    target_rates[group] = rate
                
                results[attr]['target_rates'] = target_rates
        
        return results
    
    def _audit_drift(self, reference_data: pd.DataFrame) -> Dict:
        """Audit for data drift against reference."""
        from scipy.stats import ks_2samp
        
        drift_results = {}
        
        numeric_cols = self.df.select_dtypes(include=[np.number]).columns
        
        for col in numeric_cols:
            if col not in reference_data.columns:
                continue
            
            ref = reference_data[col].dropna()
            cur = self.df[col].dropna()
            
            stat, p_value = ks_2samp(ref, cur)
            
            drift_results[col] = {
                'ks_statistic': stat,
                'p_value': p_value,
                'drift_detected': p_value < 0.05,
                'ref_mean': ref.mean(),
                'cur_mean': cur.mean(),
            }
        
        return drift_results
    
    def get_summary(self) -> str:
        """Generate human-readable audit summary."""
        if not self.audit_results:
            return "No audit results. Run run_full_audit() first."
        
        summary = f"""
Dataset Audit Summary: {self.name} v{self.version}
{'=' * 60}

📊 Size: {self.audit_results['metadata']['n_rows']:,} rows × {self.audit_results['metadata']['n_columns']} columns

🔍 Quality Issues:
   - Duplicate rows: {self.audit_results['quality']['duplicate_rows']['percentage']:.1f}%
   - Constant columns: {len(self.audit_results['quality']['constant_columns'])}

📝 Completeness Issues:
"""
        critical_missing = [
            col for col, info in self.audit_results['completeness'].items()
            if info['status'] in ['CRITICAL', 'HIGH']
        ]
        summary += f"   - Columns with >20% missing: {len(critical_missing)}\n"
        
        if 'target_balance' in self.audit_results:
            tb = self.audit_results['target_balance']
            summary += f"""
⚖️ Target Balance:
   - Imbalance ratio: {tb['imbalance_ratio']:.1f}x
   - Status: {tb['status']}
"""
        
        if 'drift' in self.audit_results:
            drifted = [
                col for col, info in self.audit_results['drift'].items()
                if info['drift_detected']
            ]
            summary += f"""
📈 Data Drift:
   - Columns with detected drift: {len(drifted)}
"""
        
        return summary


# Example usage
if __name__ == "__main__":
    # Create sample dataset
    np.random.seed(42)
    sample_data = pd.DataFrame({
        'age': np.random.normal(35, 10, 1000),
        'income': np.random.exponential(50000, 1000),
        'gender': np.random.choice(['M', 'F'], 1000, p=[0.7, 0.3]),
        'approved': np.random.choice([0, 1], 1000, p=[0.8, 0.2]),
    })
    
    # Run audit
    auditor = DatasetAuditor(sample_data, "loan_applications", "1.0.0")
    results = auditor.run_full_audit(
        target_col='approved',
        protected_attrs=['gender']
    )
    
    print(auditor.get_summary())
```

## Summary

- **Dataset quality directly impacts model quality**—audit before you train
- **Data profiling** reveals structure, statistics, and potential issues
- **Class imbalance** can cause models to ignore minority classes
- **Label quality** affects what the model learns—verify accuracy and consistency
- **Data drift** degrades model performance—monitor continuously
- **Version and lineage tracking** ensures reproducibility and auditability
- **Synthetic data** requires its own validation for fidelity and privacy
- **Document everything**—clear audit reports enable informed decisions

## Additional Resources

- [Great Expectations - Data Quality](https://greatexpectations.io/)
- [Evidently AI - ML Monitoring](https://www.evidentlyai.com/)
- [Data Version Control (DVC)](https://dvc.org/)

