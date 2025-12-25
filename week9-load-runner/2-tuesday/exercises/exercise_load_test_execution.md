# Lab: Executing Your First Load Test

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Lab |
| **Prerequisites** | exercise_scenario_creation.md, load-generation.md, demo_load_execution.md |

## Learning Objectives
By completing this exercise, you will:
- Execute a complete load test using LoadRunner Controller
- Monitor real-time performance metrics during test execution
- Understand the test execution lifecycle (init, ramp-up, steady state, ramp-down)
- Observe key performance indicators (response time, throughput, errors)
- Collect results for analysis
- Handle common execution issues

## The Scenario

Your holiday sale load test scenario is ready. Now it's time to execute the test, monitor the results in real-time, and collect data for analysis. You'll run a complete test cycle and document your observations at each phase.

## Important Notes Before Starting

1. **Test Duration**: The full test will take approximately 18-20 minutes
2. **Resource Impact**: Your machine will be under load during the test
3. **Close Other Apps**: Close unnecessary applications to free up resources
4. **Results Storage**: Ensure you have at least 1GB free space for results

## Core Tasks

### Task 1: Pre-Execution Checklist (5 minutes)

Before starting the test, verify everything is ready:

**Script Verification:**
- [ ] All scripts in scenario have been replayed successfully in VuGen
- [ ] Parameters are configured correctly
- [ ] No syntax errors or warnings

**Scenario Verification:**
- [ ] All virtual user groups are configured
- [ ] Total VUsers = 50 (or less)
- [ ] Schedule is configured with ramp-up
- [ ] Scenario is saved

**Load Generator Verification:**
- [ ] localhost Load Generator shows **Connected**
- [ ] Status shows **Ready**

**System Verification:**
- [ ] Close unnecessary applications
- [ ] Check available disk space (1GB+ free)
- [ ] Note current time for test documentation

### Task 2: Configure Results Settings (5 minutes)

Set up where results will be stored:

1. Go to **Results → Results Settings**
2. Configure:
   - Results Directory: `C:\LoadRunner\Results\HolidaySale_Test1`
   - [ ] Automatically create results for each run
3. Click **OK**

**Also configure:**
- Go to **Scenario → Runtime Settings**
- Under **Miscellaneous**:
   - [ ] Enable **Automatic transactions** (if not using manual transactions)

### Task 3: Start the Load Test (5 minutes)

1. Verify you're in the **Run** view (click Run tab if needed)
2. Click **Start Scenario** button (▶) or press **Ctrl+G**
3. Observe the **Scenario Status** panel

**Initial Observations:**
```
Test Start Time: ________________

Scenario Status After Start:
- Status: ________________ (Initializing/Running)
- VUsers Initializing: ________________
```

### Task 4: Monitor the Ramp-Up Phase (5 minutes)

Watch the test during the first 5 minutes as VUsers ramp up:

**Real-Time Monitoring Panels:**

```
Controller Run View:
┌─────────────────────────────────────────────────────────────────┐
│                     Running VUsers                               │
│   50 │                                                          │
│      │                              ╱────────────                │
│   40 │                            ╱╱                             │
│      │                          ╱╱                               │
│   30 │                        ╱╱                                 │
│      │                      ╱╱                                   │
│   20 │                    ╱╱                                     │
│      │                  ╱╱                                       │
│   10 │                ╱╱                                         │
│      │              ╱╱                                           │
│    0 └────────────────────────────────────────────────────▶     │
│        0    1    2    3    4    5 min                           │
├─────────────────────────────────────────────────────────────────┤
│  Passed: ____    Failed: ____    Errors: ____                   │
└─────────────────────────────────────────────────────────────────┘
```

**Document Ramp-Up Observations:**

| Time | Running VUsers | Transactions/Sec | Errors |
|------|----------------|------------------|--------|
| 1 min | _______ | _______ | _______ |
| 2 min | _______ | _______ | _______ |
| 3 min | _______ | _______ | _______ |
| 4 min | _______ | _______ | _______ |
| 5 min | _______ | _______ | _______ |

**Questions to Answer:**
1. At what point did all VUsers become "Running"? ________________
2. Did response times increase during ramp-up? ________________
3. Were there any errors during initialization? ________________

### Task 5: Monitor Steady State (10 minutes)

During the steady state phase (minutes 5-15), all 50 VUsers should be running:

**Key Metrics to Watch:**

1. **Running VUsers Graph**
   - Should stay constant at 50

2. **Transaction Response Time Graph**
   - Watch for response time trends
   - Note any spikes

3. **Transactions per Second**
   - Should stabilize during steady state

4. **Errors per Second**
   - Should be near zero
   - Investigate if errors appear

**Document Steady State Observations:**

```
Steady State Metrics (at 10 minutes):
=====================================

Running VUsers: ______ / 50

Transaction Statistics:
- Transactions Passed: ________________
- Transactions Failed: ________________
- Pass Rate: ________________%

Response Times:
- Average Response Time: ________________ sec
- Maximum Response Time: ________________ sec

Throughput:
- Transactions/Second: ________________
- Hits/Second: ________________

Errors:
- Total Errors: ________________
- Error Rate: ________________%
```

### Task 6: Observe Online Graphs (During Test)

While the test runs, explore different monitoring views:

1. **Open Online Graphs:**
   - Click **View → Online Graphs** (or use the Graphs panel)

2. **View Available Graphs:**
   - Running VUsers
   - Transaction Response Time
   - Hits per Second
   - Throughput
   - Transactions per Second

3. **Document What You See:**

| Graph | Observation |
|-------|-------------|
| Running VUsers | ________________ |
| Response Time | ________________ |
| Throughput | ________________ |
| Errors | ________________ |

### Task 7: Monitor Ramp-Down and Test Completion (5 minutes)

Watch the test conclude:

1. Observe VUsers decreasing during ramp-down
2. Wait for all VUsers to stop
3. Note the test end time and total duration

**End of Test Documentation:**
```
Test End Time: ________________
Total Duration: ________________

Final Statistics:
- Total Transactions Passed: ________________
- Total Transactions Failed: ________________
- Overall Pass Rate: ________________%
- Peak Running VUsers: ________________
- Peak Transactions/Second: ________________
```

### Task 8: Collect and Save Results (5 minutes)

After the test completes:

1. The **Results** dialog will appear automatically
2. Review the summary information
3. Click **OK** to save results

4. Verify results are saved:
   - Navigate to your results directory
   - Confirm .lrr file exists
   - Note the results folder size: ________________

**Results Location:**
```
Results saved to: ________________________________

Files in results folder:
[  ] .lrr file (Analysis results)
[  ] .mdb file (Database)
[  ] Log folders
[  ] Other supporting files
```

## Definition of Done

Your load test execution is complete when:
- [ ] Pre-execution checklist completed
- [ ] Test executed from start to finish without manual intervention
- [ ] Ramp-up phase documented with VUser counts
- [ ] Steady state metrics recorded
- [ ] No critical errors preventing test completion
- [ ] Test completed naturally (ramp-down finished)
- [ ] Results collected and saved
- [ ] Results folder contains .lrr file

## Execution Summary Template

Complete this summary for your records:

```
Load Test Execution Summary
===========================

Test Information:
- Scenario Name: HolidaySale_LoadTest
- Execution Date: ________________
- Start Time: ________________
- End Time: ________________
- Total Duration: ________________

Configuration:
- Total VUsers: 50
- Ramp-up Time: 5 minutes
- Steady State: 10 minutes
- Ramp-down Time: 3 minutes

Results Summary:
- Total Transactions: ________________
- Passed: ________________
- Failed: ________________
- Pass Rate: ________________%

Performance Metrics:
- Avg Response Time: ________________ sec
- Max Response Time: ________________ sec
- Avg Transactions/Sec: ________________
- Peak Throughput: ________________

Issues Encountered:
- ________________
- ________________

Results Location: ________________

Status: SUCCESS / PARTIAL / FAILED
```

## Troubleshooting Guide

| Issue | Possible Cause | Solution |
|-------|---------------|----------|
| Test won't start | Load Generator not connected | Reconnect Load Generator |
| VUsers fail to initialize | Script error | Fix script in VuGen, re-run |
| High error rate | Application issue | Check target application status |
| Test runs but no data | Results path issue | Check Results Settings |
| Controller freezes | Resource exhaustion | Close other apps, reduce VUsers |
| All transactions fail | Correlation issue | Fix script correlation |

## Stretch Goals (Optional)

If you finish early:
1. Run the test again with different VUser counts (25, 40, 50)
2. Experiment with adjusting load during test (add/remove VUsers manually)
3. Try stopping and restarting individual groups
4. Export the online graphs to files during the test
5. Compare results from multiple runs

## Understanding the Execution Lifecycle

```
Load Test Execution Phases:
───────────────────────────

Phase 1: INITIALIZATION
┌─────────────────────────────────────────────┐
│ - Scripts loaded into memory                │
│ - VUsers created but not running            │
│ - Connections established                   │
│ - Parameter files loaded                    │
└─────────────────────────────────────────────┘
                    ▼
Phase 2: RAMP-UP  
┌─────────────────────────────────────────────┐
│ - VUsers start according to schedule        │
│ - Load gradually increases                  │
│ - Watch for initial errors                  │
│ - Response times may fluctuate              │
└─────────────────────────────────────────────┘
                    ▼
Phase 3: STEADY STATE
┌─────────────────────────────────────────────┐
│ - All VUsers running                        │
│ - Primary measurement period                │
│ - Stable metrics expected                   │
│ - Identify performance patterns             │
└─────────────────────────────────────────────┘
                    ▼
Phase 4: RAMP-DOWN
┌─────────────────────────────────────────────┐
│ - VUsers stop according to schedule         │
│ - Load gradually decreases                  │
│ - Transactions complete gracefully          │
│ - Results collected                         │
└─────────────────────────────────────────────┘
```

## Common Mistakes to Avoid

1. **Not monitoring during test** - Important to catch issues early
2. **Stopping test prematurely** - Let it complete naturally for valid results
3. **Ignoring errors** - Even small error rates indicate problems
4. **Not saving results** - Results may be lost if not properly saved
5. **Running on a busy machine** - Other processes affect results

## Submission

1. Complete the Execution Summary Template
2. Screenshot the final Results Summary dialog
3. Verify your results folder contains all necessary files

Commit message format:
```
feat(week9): Complete load test execution exercise
```

## Next Steps

After completing this exercise, you'll have results ready for analysis in Wednesday's session. Keep your results folder safe - you'll use it for:
- Result Analysis exercise
- Bottleneck Investigation exercise
- Custom Graphs exercise
- Performance Report exercise

