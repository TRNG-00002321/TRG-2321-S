# Lab: Bottleneck Investigation

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Advanced |
| **Time Estimate** | 60-75 minutes |
| **Mode** | Individual Analysis Lab |
| **Prerequisites** | exercise_results_analysis.md, bottleneck-identification.md, demo_correlation_graphs.md |

## Learning Objectives
By completing this exercise, you will:
- Apply systematic bottleneck investigation methodology
- Correlate multiple performance metrics to identify root causes
- Use transaction breakdown to find slow components
- Recognize common bottleneck patterns
- Document findings with supporting evidence
- Propose actionable recommendations

## The Scenario

After reviewing your holiday sale load test results, stakeholders have concerns. They've noticed:
- "Response times increased significantly when we hit 40 users"
- "Some transactions are much slower than others"
- "We saw some errors but don't know why"

Your task is to investigate these observations, identify any bottlenecks, and provide a root cause analysis with recommendations.

## Investigation Approach

You'll follow this systematic approach:

```
Bottleneck Investigation Process:
─────────────────────────────────

Step 1: IDENTIFY SYMPTOMS
         ↓
Step 2: CORRELATE METRICS  
         ↓
Step 3: NARROW DOWN
         ↓
Step 4: ROOT CAUSE ANALYSIS
         ↓
Step 5: DOCUMENT & RECOMMEND
```

## Core Tasks

### Task 1: Identify Performance Symptoms (10 minutes)

Open your analysis session from the previous exercise and identify symptoms:

**Symptom Investigation Checklist:**

1. **Response Time Issues**
   - At what VUser count did response times noticeably increase? ________________
   - Which transactions showed the most degradation? ________________
   - Was the degradation gradual or sudden? ________________

2. **Throughput Issues**
   - Did throughput plateau at any point? Yes / No
   - At what VUser count? ________________
   - Did throughput decrease under load? Yes / No

3. **Error Issues**
   - What was the total error count? ________________
   - When did errors start occurring? ________________
   - Were errors consistent or sporadic? ________________

**Document Primary Symptom:**
```
Primary Symptom: ________________________________________________

Details:
- When it occurs: ________________________________________________
- Severity: ________________________________________________
- Affected transactions: ________________________________________________
```

### Task 2: Create Correlation Graphs (15 minutes)

Correlate metrics to understand relationships:

#### Correlation 1: Response Time vs. Running VUsers
1. Open **Transaction Response Time** graph
2. Open **Running VUsers** graph
3. Use **Graph → Merge Graphs** to overlay them
4. Or view side-by-side

**Document the Correlation:**
```
Response Time vs. VUsers:
                Response Time          Running VUsers
VUsers Count    (avg seconds)          
──────────────────────────────────────────────────────
10 Users        ____________           10
20 Users        ____________           20
30 Users        ____________           30
40 Users        ____________           40
50 Users        ____________           50

Correlation Pattern: 
[ ] Linear increase (proportional to load)
[ ] Exponential increase (rapid degradation)
[ ] Stable (no correlation)
[ ] Threshold pattern (stable then spike)
```

#### Correlation 2: Throughput vs. Running VUsers
1. Open **Throughput** graph alongside **Running VUsers**
2. Look for plateau or decline patterns

**Document the Correlation:**
```
Throughput Analysis:
- Did throughput increase with VUsers? ________________
- At what point did throughput stop increasing? ________________
- Did throughput decrease at high load? ________________

Pattern Identified:
[ ] Throughput scaled linearly (good)
[ ] Throughput plateaued (capacity limit)
[ ] Throughput declined (system stressed)
```

#### Correlation 3: Errors vs. Load
1. Open **Errors per Second** graph (if available) or check error count
2. Correlate error timing with VUser count

**Document the Correlation:**
```
Error Analysis:
- First error occurred at: ________________ VUsers
- Error rate at peak load: ________________%
- Error types observed: ________________
```

### Task 3: Transaction Breakdown Analysis (15 minutes)

Drill into the slowest transaction to find the bottleneck component:

1. In Analysis, find the **Transaction Breakdown** or **Response Time Breakdown**
2. Select your slowest transaction

**If Transaction Breakdown is not available**, analyze these aspects:

#### Manual Breakdown Analysis

```
Slowest Transaction: ________________________________

Response Time Components (estimated):
┌─────────────────────────────────────────────────────────────────┐
│ Component              │ Time        │ % of Total  │ Notes      │
├────────────────────────┼─────────────┼─────────────┼────────────┤
│ Network (DNS, Connect) │ _________ s │ _________ % │            │
│ Server Processing      │ _________ s │ _________ % │            │
│ Content Download       │ _________ s │ _________ % │            │
│ Client Processing      │ _________ s │ _________ % │            │
├────────────────────────┼─────────────┼─────────────┼────────────┤
│ TOTAL                  │ _________ s │ 100%        │            │
└─────────────────────────────────────────────────────────────────┘

Primary Bottleneck Location:
[ ] Network (high latency, slow connections)
[ ] Server (slow processing, database issues)
[ ] Content (large downloads, unoptimized assets)
[ ] Unknown (need more data)
```

### Task 4: Pattern Recognition (10 minutes)

Compare your findings against common bottleneck patterns:

#### Pattern Matching Checklist

**Pattern 1: Connection Pool Exhaustion**
```
Symptoms:
[ ] Response time suddenly spikes at specific VUser count
[ ] Errors include "Cannot acquire connection"
[ ] Low CPU/Memory on server

Does this pattern match? YES / NO
Evidence: ________________________________________________
```

**Pattern 2: CPU Saturation**
```
Symptoms:
[ ] Response time increases linearly with load
[ ] Server CPU at 90-100% during peak
[ ] Throughput plateaus at high load

Does this pattern match? YES / NO
Evidence: ________________________________________________
```

**Pattern 3: Memory Pressure / GC**
```
Symptoms:
[ ] Periodic response time spikes (regular pattern)
[ ] Response times degrade over time
[ ] Possible memory growth pattern

Does this pattern match? YES / NO
Evidence: ________________________________________________
```

**Pattern 4: Database Contention**
```
Symptoms:
[ ] Specific transactions are slow (not all)
[ ] Sporadic slowdowns (lock waits)
[ ] Database-heavy operations affected

Does this pattern match? YES / NO
Evidence: ________________________________________________
```

**Pattern 5: Network Latency**
```
Symptoms:
[ ] High time-to-first-byte (TTFB)
[ ] Consistent slowness across all transactions
[ ] Low server resource utilization

Does this pattern match? YES / NO
Evidence: ________________________________________________
```

### Task 5: Root Cause Analysis (10 minutes)

Based on your investigation, identify the most likely root cause:

**Root Cause Analysis Template:**

```
INVESTIGATION SUMMARY
═══════════════════════════════════════════════════════════════════

Problem Statement:
[Describe the performance issue in one sentence]
───────────────────────────────────────────────────────────────────



Symptoms Observed:
───────────────────────────────────────────────────────────────────
1. 
2. 
3. 

Correlations Found:
───────────────────────────────────────────────────────────────────
- Response Time vs. Load: 
- Throughput vs. Load: 
- Errors vs. Load: 

Pattern Match:
───────────────────────────────────────────────────────────────────
Most likely pattern: 

Evidence supporting this conclusion:
1. 
2. 
3. 

Root Cause:
───────────────────────────────────────────────────────────────────
[Primary root cause in one sentence]



Contributing Factors:
───────────────────────────────────────────────────────────────────
1. 
2. 

Confidence Level: HIGH / MEDIUM / LOW

Reasoning:
───────────────────────────────────────────────────────────────────




═══════════════════════════════════════════════════════════════════
```

### Task 6: Document Recommendations (10 minutes)

Based on your root cause analysis, provide recommendations:

**Recommendations Template:**

```
RECOMMENDATIONS
═══════════════════════════════════════════════════════════════════

Immediate Actions (Before Holiday Sale):
───────────────────────────────────────────────────────────────────
Priority 1 (Critical):
  - Action: 
  - Expected Impact: 
  - Effort: LOW / MEDIUM / HIGH

Priority 2 (Important):
  - Action: 
  - Expected Impact: 
  - Effort: LOW / MEDIUM / HIGH

Long-term Improvements:
───────────────────────────────────────────────────────────────────
1. 
2. 

Further Investigation Needed:
───────────────────────────────────────────────────────────────────
[ ] Server-side profiling to identify slow code paths
[ ] Database query analysis
[ ] Network trace analysis
[ ] Additional load tests with monitoring

Retest Recommendation:
───────────────────────────────────────────────────────────────────
After implementing fixes, retest with:
- Same scenario (50 VUsers)
- Extended duration (30 minutes)
- Server monitoring enabled

═══════════════════════════════════════════════════════════════════
```

## Definition of Done

Your investigation is complete when:
- [ ] Primary performance symptoms identified
- [ ] At least 2 correlation analyses performed
- [ ] Transaction breakdown analyzed for slowest transaction
- [ ] Findings compared against common bottleneck patterns
- [ ] Root cause analysis documented with evidence
- [ ] Recommendations provided with priorities
- [ ] Investigation summary ready for stakeholder presentation

## Investigation Report Template

Complete this comprehensive report:

```
BOTTLENECK INVESTIGATION REPORT
═══════════════════════════════════════════════════════════════════

Project: Holiday Sale Load Test
Investigator: ________________
Date: ________________

EXECUTIVE SUMMARY
───────────────────────────────────────────────────────────────────
[2-3 sentences summarizing the finding]



KEY FINDINGS
───────────────────────────────────────────────────────────────────
1. Performance degradation observed at _______ concurrent users
2. Primary bottleneck identified as: _______________________
3. Impact: [Describe business impact]

EVIDENCE
───────────────────────────────────────────────────────────────────
Graph 1: [Reference correlation graph showing the issue]
Graph 2: [Reference supporting evidence]
Statistics: [Key numbers supporting conclusion]

ROOT CAUSE
───────────────────────────────────────────────────────────────────
[Detailed explanation]

RECOMMENDATIONS
───────────────────────────────────────────────────────────────────
1. [Immediate action with expected impact]
2. [Secondary action]
3. [Long-term improvement]

CONFIDENCE & NEXT STEPS
───────────────────────────────────────────────────────────────────
Confidence in analysis: HIGH / MEDIUM / LOW
Additional data needed: YES / NO
Retest required: YES / NO

═══════════════════════════════════════════════════════════════════
```

## Troubleshooting Guide

| Issue | Possible Cause | Solution |
|-------|---------------|----------|
| No clear pattern | Multiple issues | Focus on one symptom at a time |
| Insufficient data | Test too short | Recommend longer test duration |
| All metrics look normal | Issue may be external | Check application logs |
| Cannot reproduce finding | Intermittent issue | Note conditions when it occurs |

## Stretch Goals (Optional)

If you finish early:
1. Create a visual presentation of your findings (3-5 slides)
2. Compare your results to industry benchmarks
3. Create a test plan for validating your recommendations
4. Research additional monitoring tools that would help diagnose the issue
5. Write a summary suitable for a non-technical stakeholder

## Common Mistakes to Avoid

1. **Jumping to conclusions** - Always correlate multiple data points
2. **Blaming the application without evidence** - Could be test infrastructure
3. **Ignoring context** - Test environment may differ from production
4. **Over-complicating** - Start with simple explanations first
5. **Not prioritizing** - Focus on biggest impact issues first

## Submission

1. Complete the Investigation Report Template
2. Save any correlation graphs as evidence
3. Prepare a 5-minute verbal summary of your findings

Commit message format:
```
feat(week9): Complete bottleneck investigation exercise
```

