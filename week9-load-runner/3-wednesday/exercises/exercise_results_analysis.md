# Lab: Analyzing Load Test Results

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Lab |
| **Prerequisites** | Completed load test from Tuesday, result-analysis.md, demo_analysis_tool_basics.md |

## Learning Objectives
By completing this exercise, you will:
- Open and navigate load test results in LoadRunner Analysis
- Create and customize key performance graphs
- Apply time filtering to focus on steady-state data
- Interpret transaction response time statistics
- Identify performance patterns from visual data
- Save analysis sessions for future reference

## The Scenario

Your holiday sale load test completed successfully yesterday. Now it's time to analyze the results and determine if the application meets performance requirements. Your manager wants to know: "Did the application handle 50 users within acceptable response times?"

## Prerequisites

Before starting this exercise, ensure you have:
- [ ] Results folder from Tuesday's load test execution
- [ ] Results contain at least 10 minutes of steady-state data
- [ ] LoadRunner Analysis tool is installed and accessible

## Core Tasks

### Task 1: Open Results in Analysis Tool (10 minutes)

1. Launch **LoadRunner Analysis**:
   ```
   Start Menu → LoadRunner → Analysis
   ```

2. Open your results:
   - Click **File → Open**
   - Navigate to your results folder from Tuesday
   - Select the `.lrr` file (LoadRunner Results)
   - Click **Open**

3. Wait for results to load (may take a minute for large files)

**Verify:**
- [ ] Analysis tool opened successfully
- [ ] Results loaded without errors
- [ ] Session Explorer shows your test data

**Document:**
- Results file name: ________________
- Results file size: ________________
- Test duration shown: ________________

### Task 2: Explore the Analysis Interface (10 minutes)

Familiarize yourself with the Analysis interface:

```
Analysis Interface Overview:
┌─────────────────────────────────────────────────────────────────┐
│ File  Edit  View  Graph  Reports  Tools  Window  Help           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│ ┌─────────────────┐  ┌───────────────────────────────────────┐ │
│ │ Session Explorer│  │            Graph View                 │ │
│ │                 │  │                                       │ │
│ │ 📁 Results      │  │    [Your graphs will appear here]     │ │
│ │  └─ Your_Test   │  │                                       │ │
│ │                 │  │                                       │ │
│ │ 📊 Graphs       │  │                                       │ │
│ │  ├─ Running...  │  │                                       │ │
│ │  ├─ Trans/Sec   │  │                                       │ │
│ │  └─ Response... │  │                                       │ │
│ │                 │  │                                       │ │
│ └─────────────────┘  └───────────────────────────────────────┘ │
│                                                                 │
│ ┌─────────────────────────────────────────────────────────────┐│
│ │                   Summary Table                              ││
│ └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

**Navigate and identify:**
- Where is the Session Explorer? ________________
- Where do graphs display? ________________
- Where is the Summary table? ________________
- How do you access the Graph menu? ________________

### Task 3: Create Essential Performance Graphs (15 minutes)

Create and examine the key graphs for performance analysis:

#### Graph 1: Running VUsers
1. Go to **Graph → Add New Graph**
2. Category: **VUsers**
3. Select: **Running Vusers**
4. Click **Open Graph**

**Observations:**
- Maximum VUsers reached: ________________
- Did VUsers ramp up smoothly? Yes / No
- Any unexpected VUser drops? Yes / No

#### Graph 2: Transaction Response Time
1. Go to **Graph → Add New Graph**
2. Category: **Transactions**
3. Select: **Average Transaction Response Time**
4. Click **Open Graph**

**Observations:**
- Which transaction has the highest response time? ________________
- What is the average response time? ________________
- Did response times increase under load? Yes / No

#### Graph 3: Transactions per Second
1. Go to **Graph → Add New Graph**
2. Category: **Transactions**
3. Select: **Transactions per Second**
4. Click **Open Graph**

**Observations:**
- Peak TPS achieved: ________________
- Was TPS stable during steady state? Yes / No

#### Graph 4: Throughput
1. Go to **Graph → Add New Graph**
2. Category: **Web Resources**
3. Select: **Throughput**
4. Click **Open Graph**

**Observations:**
- Peak throughput: ________________ KB/sec
- Did throughput plateau? Yes / No

### Task 4: Apply Time Filtering (10 minutes)

Focus analysis on the steady-state period (exclude ramp-up and ramp-down):

1. In any graph, right-click and select **Set Filter/Group By**
2. Or go to **View → Set Global Filter**
3. Configure time filter:

**Identify Your Steady State Period:**
```
Full Test Timeline:
│◀──────────────── Total Duration ────────────────▶│
│                                                  │
│ Ramp-up │     Steady State      │  Ramp-down    │
│← 5 min ─▶│◀──── 10 min ────────▶│◀── 3 min ───▶│

Your Steady State:
- Start time: ________________ (after ramp-up completes)
- End time: ________________ (before ramp-down begins)
```

**Apply the Filter:**
1. Set **Start Time** to exclude ramp-up (e.g., 00:05:00)
2. Set **End Time** to exclude ramp-down (e.g., 00:15:00)
3. Click **Apply**

**Verify filter applied:**
- [ ] Graphs now show only steady-state data
- [ ] Running VUsers graph shows constant 50 users
- [ ] Response time data is more representative

### Task 5: Analyze Transaction Statistics (10 minutes)

Review detailed transaction statistics:

1. Go to **View → Transaction Summary**
2. Or double-click the **Transaction Response Time** graph

**Complete the Transaction Summary Table:**

| Transaction Name | Min (sec) | Avg (sec) | Max (sec) | Std Dev | Pass | Fail |
|------------------|-----------|-----------|-----------|---------|------|------|
| T01_Navigate_Home | _______ | _______ | _______ | _______ | _______ | _______ |
| T02_Browse_Category | _______ | _______ | _______ | _______ | _______ | _______ |
| T03_View_Product | _______ | _______ | _______ | _______ | _______ | _______ |
| T04_Add_To_Cart | _______ | _______ | _______ | _______ | _______ | _______ |
| T05_View_Cart | _______ | _______ | _______ | _______ | _______ | _______ |

**Calculate Key Metrics:**
- Total transactions passed: ________________
- Total transactions failed: ________________
- Overall pass rate: ________________%
- Slowest transaction: ________________

### Task 6: Save Your Analysis Session (5 minutes)

Save your work for future reference:

1. Go to **File → Save Session**
2. Save as: `HolidaySale_Analysis_[YourName].lra`
3. Verify the session file is created

**Session includes:**
- [ ] All opened graphs
- [ ] Applied filters
- [ ] Custom views
- [ ] Your configurations

## Definition of Done

Your analysis is complete when:
- [ ] Results successfully opened in Analysis tool
- [ ] At least 4 graphs created (Running VUsers, Response Time, TPS, Throughput)
- [ ] Time filter applied to focus on steady state
- [ ] Transaction statistics table completed
- [ ] Pass/fail rates calculated
- [ ] Analysis session saved (.lra file)
- [ ] Key findings documented

## Analysis Summary Template

Complete this summary:

```
Load Test Analysis Summary
==========================

Test Information:
- Test Name: HolidaySale_LoadTest
- Analysis Date: ________________
- Results File: ________________

Test Parameters:
- Total Duration: ________________
- Peak VUsers: ________________
- Steady State Period: ________________ to ________________

Performance Summary:
┌──────────────────────────────────────────────────────────────┐
│ Metric                   │ Value          │ Status           │
├──────────────────────────┼────────────────┼──────────────────┤
│ Overall Pass Rate        │ ____________%  │ PASS/FAIL (>99%) │
│ Avg Response Time        │ ____________s  │ PASS/FAIL (<3s)  │
│ Max Response Time        │ ____________s  │ PASS/FAIL (<10s) │
│ Peak TPS                 │ ____________   │                  │
│ Error Rate               │ ____________%  │ PASS/FAIL (<1%)  │
└──────────────────────────┴────────────────┴──────────────────┘

Transaction Performance:
- Fastest Transaction: ________________ (_______ sec avg)
- Slowest Transaction: ________________ (_______ sec avg)

Key Findings:
1. ________________________________________________
2. ________________________________________________
3. ________________________________________________

Recommendations:
1. ________________________________________________
2. ________________________________________________

Overall Assessment: PASSED / NEEDS IMPROVEMENT / FAILED
```

## Troubleshooting Guide

| Issue | Possible Cause | Solution |
|-------|---------------|----------|
| Cannot open results | File corrupted | Use backup or re-run test |
| No graphs available | Results incomplete | Check if test completed properly |
| Graphs are empty | Filter too restrictive | Reset filter, verify time range |
| Missing transactions | Transaction markers missing | Check VuGen script |
| Very high response times | Application issue | Note for investigation |

## Stretch Goals (Optional)

If you finish early:
1. Create a **Response Time Percentile** graph (90th, 95th, 99th percentiles)
2. View the **Transaction Response Time Distribution** graph
3. Create a **Hits per Second** graph and correlate with response time
4. Export graph images for documentation
5. Explore the **Web Page Diagnostics** feature

## Understanding Response Time Analysis

```
Response Time Interpretation:
─────────────────────────────

Percentile Analysis:
                    Response Time
                    ─────────────
Average (Mean):     What you typically see
                    - Affected by outliers
                    - Not always representative

Median (50th %ile): Half of requests faster, half slower
                    - Better typical experience

90th Percentile:    90% of requests faster than this
                    - Good SLA metric

95th Percentile:    Common SLA threshold
                    - Industry standard

99th Percentile:    Worst 1% experience
                    - Edge cases, outliers

Standard Deviation: How much variation exists
                    - High = inconsistent performance
                    - Low = predictable performance
```

## Common Mistakes to Avoid

1. **Analyzing ramp-up data** - Use filters to exclude non-steady-state periods
2. **Ignoring failed transactions** - Even small failure rates indicate issues
3. **Only looking at averages** - Percentiles tell the real story
4. **Not saving sessions** - You'll lose your analysis work
5. **Missing the big picture** - Correlate multiple graphs for full understanding

## Submission

1. Save your analysis session (.lra file)
2. Complete the Analysis Summary Template
3. Take screenshots of your 4 main graphs

Commit message format:
```
feat(week9): Complete results analysis exercise
```

