# Exercise: AI Stress Test Design

## Overview

| Attribute | Value |
|-----------|-------|
| **Duration** | 45 minutes |
| **Difficulty** | Intermediate |
| **Mode** | Individual (Mode C: Hybrid - Design then Implement) |
| **Prerequisites** | Read stress-testing.md, Week 10 performance testing concepts |

## Learning Objectives

By completing this exercise, you will be able to:
- Design comprehensive stress tests for AI inference endpoints
- Measure and analyze performance under load
- Identify performance bottlenecks specific to AI systems
- Recommend infrastructure improvements based on test results

## The Concept

**Stress testing for AI systems** differs from traditional load testing because:

1. **Resource intensity:** ML inference consumes significant CPU/GPU/memory
2. **Variable latency:** Response times vary with input complexity
3. **Batching effects:** Some systems batch requests for efficiency
4. **Model loading:** Cold start vs warm inference differs dramatically
5. **Degradation patterns:** AI systems may degrade gracefully (lower confidence) before failing

```
Traditional API Stress:         AI Inference Stress:
┌────────────────────────┐      ┌────────────────────────┐
│ Requests → Response    │      │ Requests → Inference   │
│                        │      │     ↓                  │
│ Failure: 500 error     │      │ Model Loading          │
│ Success: 200 OK        │      │     ↓                  │
│                        │      │ GPU Memory             │
│ Metrics:               │      │     ↓                  │
│ - Latency              │      │ Batch Processing       │
│ - Throughput           │      │     ↓                  │
│                        │      │ Response               │
│                        │      │                        │
│                        │      │ Failure modes:         │
│                        │      │ - OOM (GPU/CPU)        │
│                        │      │ - Queue overflow       │
│                        │      │ - Timeout              │
│                        │      │ - Degraded accuracy    │
└────────────────────────┘      └────────────────────────┘
```

---

## The Scenario

**SentimentAPI** is an AI-powered service that analyzes customer feedback sentiment in real-time. The service needs to handle traffic from multiple client applications:

- **Web Dashboard:** ~50 requests/minute during business hours
- **Mobile App:** ~200 requests/minute with spikes
- **Batch Processing:** ~1000 requests submitted at once for reports

### Current System Specifications

```
Service: SentimentAPI v2.0
Model: DistilBERT-based sentiment classifier
Hosting: 2x AWS c5.xlarge instances (4 vCPU, 8GB RAM each)
Load Balancer: AWS ALB
Current Limits: 
  - Timeout: 30 seconds
  - Max concurrent: 50 per instance
  - Max input length: 512 tokens
```

### Performance SLAs

| Metric | Target | Critical Threshold |
|--------|--------|-------------------|
| P50 Latency | < 200ms | - |
| P95 Latency | < 500ms | < 1000ms |
| P99 Latency | < 1000ms | < 3000ms |
| Error Rate | < 0.1% | < 1% |
| Throughput | > 500 req/min | > 300 req/min |

---

## Part 1: Test Plan Design - 15 minutes

### Task 1.1: Identify Test Scenarios

Design stress test scenarios for SentimentAPI:

**Scenario 1: Steady State Load**

```markdown
**Name:** Baseline Performance
**Purpose:** Establish performance under expected load
**Load Profile:**
  - Ramp up: [Your design]
  - Steady state: [Your design]
  - Duration: [Your design]
**Expected concurrent users:** [Your calculation]
**Success criteria:** [Based on SLAs]
```

**Scenario 2: Spike Test**

```markdown
**Name:** Traffic Spike Handling
**Purpose:** Test response to sudden traffic increase
**Load Profile:**
  - Initial load: [Your design]
  - Spike magnitude: [Your design]
  - Spike duration: [Your design]
  - Recovery observation: [Your design]
**Expected behavior:** [What should happen]
**Success criteria:** [Define]
```

**Scenario 3: Endurance Test**

```markdown
**Name:** Extended Operation
**Purpose:** Identify memory leaks, resource exhaustion over time
**Load Profile:**
  - Load level: [Your design]
  - Duration: [Your design]
**Monitoring focus:** [What to watch]
**Success criteria:** [Define]
```

**Scenario 4: Batch Submission**

```markdown
**Name:** Bulk Processing
**Purpose:** Test handling of batch report generation
**Load Profile:**
  - Batch size: 1000 requests
  - Submission pattern: [Your design]
**Expected behavior:** [Queue management]
**Success criteria:** [Define]
```

### Task 1.2: Define Test Data Strategy

AI systems respond differently to different inputs. Design your test data:

```markdown
## Test Data Categories

### Short Inputs (< 50 tokens)
- Example: "Great product!"
- Expected inference time: ~50ms
- Percentage in test mix: [Your design]%

### Medium Inputs (50-200 tokens)
- Example: [Create example]
- Expected inference time: ~100ms
- Percentage in test mix: [Your design]%

### Long Inputs (200-512 tokens)
- Example: [Create example]
- Expected inference time: ~200ms
- Percentage in test mix: [Your design]%

### Edge Case Inputs
- Empty string: [How to handle]
- Max length (512 tokens): [How to handle]
- Unicode/special characters: [How to handle]
- Repeated characters: [How to handle]

## Test Data Mix Rationale
[Explain why you chose this distribution]
```

---

## Part 2: Test Implementation - 15 minutes

### Task 2.1: Create Load Test Script

Using Python and a load testing approach, design the test implementation:

```python
"""
Stress Test Implementation for SentimentAPI
"""

import time
import random
import statistics
from concurrent.futures import ThreadPoolExecutor, as_completed
from dataclasses import dataclass
from typing import List, Dict
import threading

@dataclass
class TestResult:
    """Store individual request results."""
    request_id: int
    input_length: int
    latency_ms: float
    status_code: int
    success: bool
    error_message: str = None
    timestamp: float = None

class SentimentAPIStressTester:
    """Stress test harness for SentimentAPI."""
    
    def __init__(self, base_url: str, timeout: int = 30):
        self.base_url = base_url
        self.timeout = timeout
        self.results: List[TestResult] = []
        self.results_lock = threading.Lock()
        
    def generate_test_input(self, length_category: str) -> str:
        """
        Generate test input of specified length category.
        Categories: 'short', 'medium', 'long', 'max'
        """
        # YOUR CODE HERE
        # Generate realistic review text of appropriate length
        pass
    
    def make_request(self, request_id: int, text: str) -> TestResult:
        """
        Make single request to SentimentAPI and record results.
        """
        # YOUR CODE HERE
        # 1. Record start time
        # 2. Make HTTP request to self.base_url/analyze
        # 3. Record response time
        # 4. Return TestResult
        pass
    
    def run_load_test(self, 
                      total_requests: int,
                      concurrent_users: int,
                      ramp_up_seconds: int = 0) -> List[TestResult]:
        """
        Execute load test with specified parameters.
        """
        # YOUR CODE HERE
        # 1. Calculate requests per user
        # 2. Implement ramp-up if specified
        # 3. Use ThreadPoolExecutor for concurrency
        # 4. Collect all results
        pass
    
    def run_spike_test(self,
                       baseline_users: int,
                       spike_users: int,
                       spike_duration_seconds: int) -> Dict:
        """
        Execute spike test: baseline -> spike -> recovery.
        """
        # YOUR CODE HERE
        pass
    
    def calculate_metrics(self, results: List[TestResult]) -> Dict:
        """
        Calculate performance metrics from results.
        """
        latencies = [r.latency_ms for r in results if r.success]
        errors = [r for r in results if not r.success]
        
        return {
            'total_requests': len(results),
            'successful_requests': len(latencies),
            'failed_requests': len(errors),
            'error_rate': len(errors) / len(results) * 100,
            
            # Latency metrics
            'latency_min': min(latencies) if latencies else 0,
            'latency_max': max(latencies) if latencies else 0,
            'latency_avg': statistics.mean(latencies) if latencies else 0,
            'latency_p50': self._percentile(latencies, 50),
            'latency_p95': self._percentile(latencies, 95),
            'latency_p99': self._percentile(latencies, 99),
            
            # Throughput
            'duration_seconds': (max(r.timestamp for r in results) - 
                                min(r.timestamp for r in results)),
            'throughput_per_second': len(results) / max(1, self._get_duration(results)),
        }
    
    def _percentile(self, data: List[float], p: int) -> float:
        """Calculate percentile."""
        if not data:
            return 0
        sorted_data = sorted(data)
        index = int(len(sorted_data) * p / 100)
        return sorted_data[min(index, len(sorted_data) - 1)]
    
    def _get_duration(self, results: List[TestResult]) -> float:
        """Get test duration in seconds."""
        if not results:
            return 0
        timestamps = [r.timestamp for r in results if r.timestamp]
        return max(timestamps) - min(timestamps) if timestamps else 0


# Test Configuration
TEST_CONFIG = {
    'base_url': 'http://localhost:8080',
    'scenarios': {
        'baseline': {
            'total_requests': 1000,
            'concurrent_users': 50,
            'ramp_up_seconds': 30
        },
        'spike': {
            'baseline_users': 50,
            'spike_users': 200,
            'spike_duration_seconds': 60
        },
        'endurance': {
            'total_requests': 10000,
            'concurrent_users': 30,
            'duration_minutes': 30
        }
    }
}
```

### Task 2.2: Complete Implementation

Fill in the missing method implementations. Focus on:

1. `generate_test_input()` - Create realistic test data
2. `make_request()` - Handle HTTP request and timing
3. `run_load_test()` - Orchestrate concurrent execution

---

## Part 3: Results Analysis - 10 minutes

### Task 3.1: Simulated Results Analysis

Assume you ran the tests and got these results:

**Baseline Test Results (1000 requests, 50 concurrent):**

| Metric | Value | SLA Target | Status |
|--------|-------|------------|--------|
| P50 Latency | 180ms | < 200ms | |
| P95 Latency | 620ms | < 500ms | |
| P99 Latency | 1,850ms | < 1000ms | |
| Error Rate | 0.3% | < 0.1% | |
| Throughput | 480 req/min | > 500 req/min | |

**Spike Test Results:**

| Phase | P95 Latency | Error Rate | Throughput |
|-------|-------------|------------|------------|
| Baseline (50 users) | 320ms | 0.1% | 450/min |
| Spike (200 users) | 2,100ms | 4.2% | 380/min |
| Recovery (50 users) | 890ms | 1.1% | 420/min |

**Error Breakdown:**

| Error Type | Count | Percentage |
|------------|-------|------------|
| Timeout (30s) | 28 | 70% |
| HTTP 503 (Service Unavailable) | 8 | 20% |
| HTTP 500 (Internal Error) | 4 | 10% |

### Task 3.2: Root Cause Analysis

Based on the results, complete this analysis:

```markdown
## Performance Issue Analysis

### Issue 1: P95/P99 Latency Exceeds SLA

**Observation:** P95 at 620ms, P99 at 1,850ms
**SLA:** P95 < 500ms, P99 < 1000ms

**Likely causes:**
1. [Your hypothesis]
2. [Your hypothesis]

**Evidence supporting hypothesis:**
- [What in the data supports this?]

**Recommended investigation:**
- [What would you check?]

---

### Issue 2: Elevated Error Rate

**Observation:** 0.3% error rate (mostly timeouts)
**SLA:** < 0.1%

**Likely causes:**
1. [Your hypothesis]
2. [Your hypothesis]

**Evidence supporting hypothesis:**
- [What in the data supports this?]

---

### Issue 3: Poor Spike Recovery

**Observation:** Recovery phase still shows degraded performance
**Expected:** Return to baseline within 1 minute

**Likely causes:**
1. [Your hypothesis]
2. [Your hypothesis]

**Implication:**
- [What does slow recovery mean for production?]
```

---

## Part 4: Recommendations Report - 5 minutes

### Task 4.1: Complete Stress Test Report

```markdown
# AI Inference Stress Test Report
## SentimentAPI v2.0

### Executive Summary

**Test Date:** [Date]
**Test Environment:** AWS c5.xlarge × 2
**Overall Result:** [PASS / FAIL / CONDITIONAL]

**Key Findings:**
1. [Finding 1]
2. [Finding 2]
3. [Finding 3]

---

### Test Execution Summary

| Scenario | Requests | Duration | Status |
|----------|----------|----------|--------|
| Baseline | 1,000 | 5 min | |
| Spike | 2,500 | 10 min | |
| Endurance | 10,000 | 30 min | |

---

### SLA Compliance

| Metric | Target | Actual | Variance | Status |
|--------|--------|--------|----------|--------|
| P50 Latency | < 200ms | | | |
| P95 Latency | < 500ms | | | |
| P99 Latency | < 1000ms | | | |
| Error Rate | < 0.1% | | | |
| Throughput | > 500/min | | | |

---

### Bottleneck Analysis

```
Request Flow with Identified Bottlenecks:

Client → ALB → Instance → Model Inference → Response
          │       │              │
          │       │              └─ BOTTLENECK: Long inference
          │       │                 on complex inputs
          │       │
          │       └─ BOTTLENECK: Connection queue at
          │          high concurrency
          │
          └─ OK: No issues detected
```

---

### Recommendations

#### Critical (Fix Before Production)

1. **[Recommendation 1]**
   - Issue: [What's wrong]
   - Impact: [What happens if not fixed]
   - Solution: [How to fix]
   - Estimated effort: [Time/cost]

2. **[Recommendation 2]**
   - Issue: [What's wrong]
   - Impact: [What happens if not fixed]
   - Solution: [How to fix]
   - Estimated effort: [Time/cost]

#### High Priority

1. **[Recommendation 3]**
   [Same format]

#### Medium Priority

1. **[Recommendation 4]**
   [Same format]

---

### Infrastructure Recommendations

| Current | Recommended | Rationale |
|---------|-------------|-----------|
| 2x c5.xlarge | | |
| 50 max concurrent | | |
| 30s timeout | | |
| No autoscaling | | |

---

### Monitoring Recommendations

Implement these alerts for production:

| Metric | Warning | Critical | Action |
|--------|---------|----------|--------|
| P95 Latency | > 400ms | > 800ms | |
| Error Rate | > 0.5% | > 1% | |
| CPU Usage | > 70% | > 85% | |
| Memory | > 75% | > 90% | |
| Queue Depth | > 100 | > 200 | |

---

### Appendix: Raw Test Data

[Include detailed metrics tables]
```

---

## Deliverables

1. **Test plan** with 4 scenarios fully designed
2. **Test data strategy** document
3. **Implementation code** (at least scaffolding for key methods)
4. **Results analysis** with root cause hypotheses
5. **Complete stress test report** with recommendations

## Definition of Done

- [ ] Designed 4 test scenarios with load profiles
- [ ] Created test data strategy covering all input categories
- [ ] Implemented or scaffolded stress test code
- [ ] Analyzed simulated results and identified root causes
- [ ] Completed stress test report with actionable recommendations
- [ ] Included infrastructure scaling recommendations

## Evaluation Rubric

| Criteria | Excellent (4) | Good (3) | Satisfactory (2) | Needs Work (1) |
|----------|---------------|----------|------------------|----------------|
| Test Design | Comprehensive scenarios | Good coverage | Basic scenarios | Incomplete |
| Analysis | Deep root cause analysis | Good analysis | Surface analysis | No analysis |
| Recommendations | Specific, actionable | Mostly actionable | Generic | Missing |
| Report Quality | Professional | Complete | Basic | Incomplete |

## Reflection Questions

1. How does stress testing AI systems differ from traditional APIs?

2. What additional metrics would you monitor for GPU-based inference?

3. How would you test graceful degradation (returning lower confidence vs errors)?

4. What's the relationship between input complexity and resource consumption?


