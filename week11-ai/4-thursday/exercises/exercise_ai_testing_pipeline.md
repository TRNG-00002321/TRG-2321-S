# Exercise: Build an AI Testing Pipeline

## Overview

| Attribute | Value |
|-----------|-------|
| **Duration** | 90 minutes |
| **Difficulty** | Advanced |
| **Mode** | Pair Programming (Driver/Navigator) |
| **Prerequisites** | Week 10 CI/CD content, Complete Monday and Tuesday exercises |

## Pair Programming Guidelines

```
╔═══════════════════════════════════════════════════════════════════════════╗
║                         PAIR PROGRAMMING ROLES                             ║
╠═══════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  DRIVER (Person at keyboard)                                              ║
║  • Writes the code                                                        ║
║  • Focuses on immediate implementation                                    ║
║  • Explains decisions out loud                                            ║
║                                                                            ║
║  NAVIGATOR (Person reviewing)                                             ║
║  • Reviews code as it's written                                           ║
║  • Thinks about the bigger picture                                        ║
║  • Catches errors and suggests improvements                               ║
║  • Researches solutions when stuck                                        ║
║                                                                            ║
║  SWITCH ROLES EVERY 20-30 MINUTES                                         ║
║                                                                            ║
╚═══════════════════════════════════════════════════════════════════════════╝
```

## Learning Objectives

By completing this exercise, your pair will be able to:
- Design and implement automated AI testing pipelines
- Integrate fairness checks into CI/CD workflows
- Configure robustness testing automation
- Set up performance monitoring for AI models
- Create actionable test reports

---

## The Scenario

**DataDriven Corp** is deploying a machine learning model that predicts customer churn. Before production deployment, they need an automated testing pipeline that runs on every model update.

### Requirements

The pipeline must automatically:
1. Run bias/fairness tests on model predictions
2. Execute robustness tests with perturbations
3. Measure inference performance (latency, throughput)
4. Generate a pass/fail report with metrics
5. Block deployment if quality gates fail

### Quality Gates

| Test Category | Metric | Pass Threshold |
|---------------|--------|----------------|
| Fairness | Disparate Impact Ratio | >= 0.80 |
| Fairness | Demographic Parity Diff | <= 0.10 |
| Robustness | Accuracy under perturbation | >= 90% of baseline |
| Performance | P95 Latency | < 200ms |
| Performance | Throughput | > 100 req/sec |

---

## Part 1: Pipeline Architecture Design - 20 minutes

**Roles:** Both partners design together, then Navigator documents

### Task 1.1: Design Pipeline Stages

Design the testing pipeline stages:

```markdown
## AI Testing Pipeline Design

### Stage 1: Setup
- Purpose: [What this stage does]
- Inputs: [What's needed]
- Outputs: [What's produced]
- Duration estimate: [Time]

### Stage 2: Data Validation
- Purpose: [What this stage does]
- Inputs: [What's needed]
- Outputs: [What's produced]
- Duration estimate: [Time]

### Stage 3: Fairness Testing
- Purpose: [What this stage does]
- Tests to run:
  - [ ] Disparate impact calculation
  - [ ] Demographic parity check
  - [ ] [Other tests]
- Inputs: [What's needed]
- Outputs: [What's produced]
- Duration estimate: [Time]

### Stage 4: Robustness Testing
- Purpose: [What this stage does]
- Tests to run:
  - [ ] Perturbation tests
  - [ ] Edge case tests
  - [ ] [Other tests]
- Inputs: [What's needed]
- Outputs: [What's produced]
- Duration estimate: [Time]

### Stage 5: Performance Testing
- Purpose: [What this stage does]
- Tests to run:
  - [ ] Latency measurement
  - [ ] Throughput testing
  - [ ] [Other tests]
- Inputs: [What's needed]
- Outputs: [What's produced]
- Duration estimate: [Time]

### Stage 6: Report Generation
- Purpose: [What this stage does]
- Outputs:
  - [ ] JSON summary
  - [ ] HTML report
  - [ ] Pass/fail status
- Duration estimate: [Time]
```

### Task 1.2: Create Pipeline Diagram

Draw the pipeline flow:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        AI TESTING PIPELINE                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   [Your diagram here - show stages, inputs, outputs, and decision       │
│    points where pipeline can fail]                                      │
│                                                                          │
│   Example structure:                                                     │
│                                                                          │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                         │
│   │  Setup   │───▶│  Stage   │───▶│  Stage   │───▶ ...                 │
│   └──────────┘    └────┬─────┘    └──────────┘                         │
│                        │                                                 │
│                        ▼ (fail)                                         │
│                   ┌──────────┐                                          │
│                   │  ABORT   │                                          │
│                   └──────────┘                                          │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Part 2: Core Implementation - 40 minutes

**Roles:** Rotate Driver/Navigator every 15 minutes

### Task 2.1: Create Pipeline Framework

**Driver 1 starts:**

Create the main pipeline structure:

```python
"""
AI Testing Pipeline
Automated testing framework for ML model validation
"""

import json
import time
from dataclasses import dataclass, asdict
from typing import Dict, List, Optional
from datetime import datetime
from enum import Enum
import numpy as np
import pandas as pd


class TestStatus(Enum):
    PASSED = "passed"
    FAILED = "failed"
    SKIPPED = "skipped"
    ERROR = "error"


@dataclass
class TestResult:
    """Individual test result."""
    name: str
    status: TestStatus
    metric_value: Optional[float] = None
    threshold: Optional[float] = None
    message: str = ""
    duration_ms: float = 0


@dataclass
class StageResult:
    """Results from a pipeline stage."""
    stage_name: str
    status: TestStatus
    tests: List[TestResult]
    duration_ms: float
    
    @property
    def passed(self) -> bool:
        return self.status == TestStatus.PASSED


@dataclass
class PipelineResult:
    """Overall pipeline results."""
    pipeline_name: str
    model_version: str
    timestamp: str
    overall_status: TestStatus
    stages: List[StageResult]
    total_duration_ms: float
    
    def to_dict(self) -> dict:
        return asdict(self)
    
    def to_json(self) -> str:
        # Custom serialization for Enum
        def serialize(obj):
            if isinstance(obj, TestStatus):
                return obj.value
            return obj
        return json.dumps(asdict(self), default=serialize, indent=2)


class AITestingPipeline:
    """Main pipeline orchestrator."""
    
    def __init__(self, model, test_data: pd.DataFrame, config: Dict):
        """
        Initialize pipeline.
        
        Args:
            model: The ML model to test (must have predict method)
            test_data: DataFrame with test data and labels
            config: Pipeline configuration
        """
        self.model = model
        self.test_data = test_data
        self.config = config
        self.results: List[StageResult] = []
        
    def run(self) -> PipelineResult:
        """Execute full pipeline."""
        start_time = time.time()
        
        stages = [
            ("Data Validation", self._run_data_validation),
            ("Fairness Testing", self._run_fairness_tests),
            ("Robustness Testing", self._run_robustness_tests),
            ("Performance Testing", self._run_performance_tests),
        ]
        
        overall_status = TestStatus.PASSED
        
        for stage_name, stage_func in stages:
            print(f"\n{'='*60}")
            print(f"Running: {stage_name}")
            print('='*60)
            
            stage_result = stage_func()
            self.results.append(stage_result)
            
            if not stage_result.passed:
                overall_status = TestStatus.FAILED
                if self.config.get('fail_fast', False):
                    print(f"❌ {stage_name} failed. Stopping pipeline.")
                    break
            else:
                print(f"✅ {stage_name} passed")
        
        total_duration = (time.time() - start_time) * 1000
        
        return PipelineResult(
            pipeline_name="AI Model Testing Pipeline",
            model_version=self.config.get('model_version', 'unknown'),
            timestamp=datetime.now().isoformat(),
            overall_status=overall_status,
            stages=self.results,
            total_duration_ms=total_duration
        )
    
    def _run_data_validation(self) -> StageResult:
        """Stage 1: Validate test data quality."""
        # YOUR CODE HERE
        # Check: required columns exist, no nulls in key fields, 
        # data types correct, protected attributes present
        pass
    
    def _run_fairness_tests(self) -> StageResult:
        """Stage 2: Run fairness/bias tests."""
        # YOUR CODE HERE
        # Run disparate impact, demographic parity tests
        # for each protected attribute in config
        pass
    
    def _run_robustness_tests(self) -> StageResult:
        """Stage 3: Run robustness tests."""
        # YOUR CODE HERE
        # Apply perturbations and measure accuracy degradation
        pass
    
    def _run_performance_tests(self) -> StageResult:
        """Stage 4: Run performance tests."""
        # YOUR CODE HERE
        # Measure latency and throughput
        pass
```

### Task 2.2: Implement Fairness Stage

**Switch roles. Driver 2:**

Complete the fairness testing stage:

```python
def _run_fairness_tests(self) -> StageResult:
    """Stage 2: Run fairness/bias tests."""
    start_time = time.time()
    tests = []
    
    # Get predictions
    predictions = self.model.predict(self.test_data)
    self.test_data['prediction'] = predictions
    
    protected_attributes = self.config.get('protected_attributes', [])
    thresholds = self.config.get('fairness_thresholds', {
        'disparate_impact': 0.80,
        'demographic_parity_diff': 0.10
    })
    
    for attr in protected_attributes:
        if attr not in self.test_data.columns:
            tests.append(TestResult(
                name=f"Fairness ({attr})",
                status=TestStatus.SKIPPED,
                message=f"Protected attribute '{attr}' not in data"
            ))
            continue
        
        # Calculate disparate impact
        # YOUR CODE HERE: Implement disparate impact calculation
        di_ratio = self._calculate_disparate_impact(attr)
        di_passed = di_ratio >= thresholds['disparate_impact']
        
        tests.append(TestResult(
            name=f"Disparate Impact ({attr})",
            status=TestStatus.PASSED if di_passed else TestStatus.FAILED,
            metric_value=di_ratio,
            threshold=thresholds['disparate_impact'],
            message=f"{'✅' if di_passed else '❌'} DI ratio: {di_ratio:.3f}"
        ))
        
        # Calculate demographic parity difference
        # YOUR CODE HERE: Implement demographic parity calculation
        dp_diff = self._calculate_demographic_parity_diff(attr)
        dp_passed = dp_diff <= thresholds['demographic_parity_diff']
        
        tests.append(TestResult(
            name=f"Demographic Parity ({attr})",
            status=TestStatus.PASSED if dp_passed else TestStatus.FAILED,
            metric_value=dp_diff,
            threshold=thresholds['demographic_parity_diff'],
            message=f"{'✅' if dp_passed else '❌'} DP diff: {dp_diff:.3f}"
        ))
    
    # Determine stage status
    all_passed = all(t.status == TestStatus.PASSED for t in tests 
                     if t.status != TestStatus.SKIPPED)
    
    duration = (time.time() - start_time) * 1000
    
    return StageResult(
        stage_name="Fairness Testing",
        status=TestStatus.PASSED if all_passed else TestStatus.FAILED,
        tests=tests,
        duration_ms=duration
    )

def _calculate_disparate_impact(self, attr: str) -> float:
    """Calculate disparate impact ratio for attribute."""
    # YOUR CODE HERE
    pass

def _calculate_demographic_parity_diff(self, attr: str) -> float:
    """Calculate demographic parity difference for attribute."""
    # YOUR CODE HERE
    pass
```

### Task 2.3: Implement Robustness Stage

**Switch roles. Driver 1:**

```python
def _run_robustness_tests(self) -> StageResult:
    """Stage 3: Run robustness tests with perturbations."""
    start_time = time.time()
    tests = []
    
    # Get baseline accuracy
    baseline_predictions = self.model.predict(self.test_data)
    baseline_accuracy = (baseline_predictions == 
                        self.test_data['label']).mean()
    
    perturbation_threshold = self.config.get(
        'robustness_threshold', 0.90)  # 90% of baseline
    
    # Test with noise perturbation
    # YOUR CODE HERE: Add noise to numerical features and re-test
    
    # Test with missing values
    # YOUR CODE HERE: Introduce missing values and re-test
    
    # Calculate robustness score
    # YOUR CODE HERE: Compare perturbed accuracy to baseline
    
    duration = (time.time() - start_time) * 1000
    
    return StageResult(
        stage_name="Robustness Testing",
        status=TestStatus.PASSED,  # Update based on tests
        tests=tests,
        duration_ms=duration
    )
```

### Task 2.4: Implement Performance Stage

**Switch roles. Driver 2:**

```python
def _run_performance_tests(self) -> StageResult:
    """Stage 4: Run performance tests."""
    start_time = time.time()
    tests = []
    
    thresholds = self.config.get('performance_thresholds', {
        'p95_latency_ms': 200,
        'throughput_per_sec': 100
    })
    
    # Measure latency
    latencies = []
    sample_size = min(100, len(self.test_data))
    
    for i in range(sample_size):
        sample = self.test_data.iloc[[i]]
        t0 = time.time()
        _ = self.model.predict(sample)
        latencies.append((time.time() - t0) * 1000)
    
    p95_latency = np.percentile(latencies, 95)
    p95_passed = p95_latency < thresholds['p95_latency_ms']
    
    tests.append(TestResult(
        name="P95 Latency",
        status=TestStatus.PASSED if p95_passed else TestStatus.FAILED,
        metric_value=p95_latency,
        threshold=thresholds['p95_latency_ms'],
        message=f"P95: {p95_latency:.2f}ms"
    ))
    
    # Measure throughput
    # YOUR CODE HERE: Measure requests per second
    
    duration = (time.time() - start_time) * 1000
    
    all_passed = all(t.status == TestStatus.PASSED for t in tests)
    
    return StageResult(
        stage_name="Performance Testing",
        status=TestStatus.PASSED if all_passed else TestStatus.FAILED,
        tests=tests,
        duration_ms=duration
    )
```

---

## Part 3: Report Generation - 15 minutes

**Roles:** Work together

### Task 3.1: Create Report Generator

```python
class PipelineReportGenerator:
    """Generate human-readable reports from pipeline results."""
    
    def __init__(self, result: PipelineResult):
        self.result = result
    
    def generate_summary(self) -> str:
        """Generate text summary."""
        status_emoji = "✅" if self.result.overall_status == TestStatus.PASSED else "❌"
        
        summary = f"""
{'='*70}
AI TESTING PIPELINE REPORT
{'='*70}

Pipeline: {self.result.pipeline_name}
Model Version: {self.result.model_version}
Timestamp: {self.result.timestamp}
Total Duration: {self.result.total_duration_ms:.0f}ms

OVERALL STATUS: {status_emoji} {self.result.overall_status.value.upper()}

{'='*70}
STAGE RESULTS
{'='*70}
"""
        for stage in self.result.stages:
            status_icon = "✅" if stage.passed else "❌"
            summary += f"\n{status_icon} {stage.stage_name} ({stage.duration_ms:.0f}ms)\n"
            
            for test in stage.tests:
                test_icon = "  ✓" if test.status == TestStatus.PASSED else "  ✗"
                metric_str = f" [{test.metric_value:.3f}]" if test.metric_value else ""
                summary += f"{test_icon} {test.name}{metric_str}\n"
        
        return summary
    
    def generate_html(self) -> str:
        """Generate HTML report."""
        # YOUR CODE HERE: Create professional HTML report
        pass
    
    def generate_json(self) -> str:
        """Generate JSON report for CI/CD integration."""
        return self.result.to_json()
```

---

## Part 4: CI/CD Integration - 15 minutes

**Roles:** Navigate together, one types

### Task 4.1: Create GitHub Actions Workflow

```yaml
# .github/workflows/ai-testing-pipeline.yml
name: AI Model Testing Pipeline

on:
  push:
    paths:
      - 'models/**'
      - 'data/**'
  pull_request:
    branches: [main]
  workflow_dispatch:  # Manual trigger

jobs:
  ai-testing:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
      
      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.10'
      
      - name: Install dependencies
        run: |
          pip install -r requirements.txt
          pip install fairlearn pandas numpy scikit-learn
      
      - name: Run AI Testing Pipeline
        run: |
          python -m pytest tests/ai_pipeline/ -v --junitxml=test-results.xml
        env:
          MODEL_VERSION: ${{ github.sha }}
      
      - name: Upload Test Results
        uses: actions/upload-artifact@v3
        with:
          name: test-results
          path: |
            test-results.xml
            reports/
      
      - name: Check Quality Gates
        run: |
          python scripts/check_quality_gates.py reports/pipeline_result.json
      
      - name: Comment on PR
        if: github.event_name == 'pull_request'
        uses: actions/github-script@v6
        with:
          script: |
            // YOUR CODE: Read report and post summary to PR
```

### Task 4.2: Create Quality Gate Checker

```python
# scripts/check_quality_gates.py
"""
Quality gate checker for CI/CD integration.
Returns exit code 0 for pass, 1 for fail.
"""

import json
import sys


def check_quality_gates(report_path: str) -> bool:
    """Check if pipeline results pass quality gates."""
    
    with open(report_path) as f:
        result = json.load(f)
    
    # Check overall status
    if result['overall_status'] != 'passed':
        print(f"❌ Pipeline failed: {result['overall_status']}")
        
        # Print failing tests
        for stage in result['stages']:
            for test in stage['tests']:
                if test['status'] == 'failed':
                    print(f"  - {test['name']}: {test['message']}")
        
        return False
    
    print("✅ All quality gates passed")
    return True


if __name__ == "__main__":
    report_path = sys.argv[1] if len(sys.argv) > 1 else "reports/pipeline_result.json"
    success = check_quality_gates(report_path)
    sys.exit(0 if success else 1)
```

---

## Deliverables

1. **Pipeline architecture design** document
2. **Complete pipeline implementation** (all 4 stages)
3. **Report generator** (text and JSON at minimum)
4. **CI/CD workflow configuration** (GitHub Actions or equivalent)
5. **Quality gate checker script**

## Definition of Done

- [ ] Both partners contributed to design AND implementation
- [ ] Pipeline runs all 4 stages successfully
- [ ] Fairness tests calculate disparate impact and demographic parity
- [ ] Robustness tests apply perturbations and measure accuracy
- [ ] Performance tests measure latency and throughput
- [ ] Report generator produces readable output
- [ ] CI/CD workflow configured with quality gates
- [ ] All code runs without errors
- [ ] Switched roles at least 3 times

## Evaluation Rubric

| Criteria | Excellent (4) | Good (3) | Satisfactory (2) | Needs Work (1) |
|----------|---------------|----------|------------------|----------------|
| Collaboration | Equal contribution, clear role switching | Good collaboration | Uneven but both contributed | One person dominated |
| Architecture | Clean, extensible design | Good design | Basic structure | Disorganized |
| Implementation | All stages complete and working | Most stages working | Some stages working | Minimal implementation |
| CI/CD Integration | Production-ready workflow | Functional workflow | Basic workflow | Incomplete |

## Reflection (Both Partners)

1. How did pair programming help with this complex task?

2. What was the most challenging part to implement together?

3. How would you extend this pipeline for production use?


