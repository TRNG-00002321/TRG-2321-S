# Result Analysis with LoadRunner Analysis Tool

## Learning Objectives
- Understand the LoadRunner Analysis tool interface and capabilities
- Learn to open and merge results from multiple test runs
- Master analysis session management
- Explore graph types: transaction response time, throughput, hits per second
- Apply time range filtering for focused analysis

## Why This Matters

Running a load test produces raw data. Transforming that data into actionable insights is where real value emerges. The Analysis tool helps you answer critical questions: "Did we meet our SLAs?", "Where are the bottlenecks?", and "Is the application ready for production?"

As you complete your journey in **Mastering Enterprise Performance Testing with LoadRunner**, the Analysis tool becomes your primary means of communicating findings to stakeholders and driving performance improvements.

## LoadRunner Analysis Tool Introduction

The Analysis tool processes raw test results into meaningful graphs, statistics, and reports.

### Opening Analysis

```
Launch Options:
───────────────
1. From Controller: Results → Analyze Results
2. Standalone: Start Menu → LoadRunner → Analysis
3. Double-click .lrr file (LoadRunner Results)
```

### Analysis Interface

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ LoadRunner Analysis                                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│ File  Edit  View  Graph  Reports  Tools  Window  Help                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│ ┌───────────────────┐  ┌─────────────────────────────────────────────────┐ │
│ │ Session Explorer  │  │                Graph View                       │ │
│ │                   │  │                                                 │ │
│ │ 📁 Results        │  │     Response Time (sec)                         │ │
│ │  └─ Test_Run_01   │  │     ▲                                           │ │
│ │                   │  │   3 │    ╱\     /\                              │ │
│ │ 📊 Graphs         │  │     │   /  \   /  \   /\                        │ │
│ │  ├─ Running VUsers│  │   2 │  /    \_/    \_/  \                       │ │
│ │  ├─ Trans/Sec     │  │     │ /                   \                     │ │
│ │  ├─ Response Time │  │   1 │/                     \                    │ │
│ │  └─ Throughput    │  │     │                                           │ │
│ │                   │  │   0 └──────────────────────────────▶ Time       │ │
│ │ 📈 Summary        │  │                                                 │ │
│ │                   │  │                                                 │ │
│ └───────────────────┘  └─────────────────────────────────────────────────┘ │
│                                                                             │
│ ┌─────────────────────────────────────────────────────────────────────────┐│
│ │ Summary Statistics                                                      ││
│ │ Trans Name       | Min    | Avg    | Max    | Std Dev | Pass  | Fail   ││
│ │ ─────────────────┼────────┼────────┼────────┼─────────┼───────┼────────││
│ │ Login            | 0.342  | 0.987  | 3.456  | 0.423   | 4,521 | 12     ││
│ │ Search           | 0.156  | 0.543  | 2.134  | 0.234   | 12,456| 45     ││
│ │ Checkout         | 1.234  | 2.876  | 8.765  | 1.234   | 2,341 | 8      ││
│ └─────────────────────────────────────────────────────────────────────────┘│
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Opening and Merging Results

### Opening a Single Result

```
Opening Results:
────────────────
File → Open → Navigate to results folder

Result File Types:
├── .lrr - LoadRunner Results (Analysis format)
├── .lrs - LoadRunner Scenario (Controller format)
└── Results folder - Raw collected data
```

### Merging Multiple Results

When you need to compare test runs or combine results:

```
Merging Results:
────────────────
File → Add Results...

Merge Scenarios:
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│ Current Session: Test_Run_01                                    │
│                                                                 │
│ Add Result:                                                     │
│ ┌─────────────────────────────────────────────────────────────┐│
│ │ [✓] Test_Run_02 (same configuration, different time)        ││
│ │ [✓] Test_Run_03 (after code fix)                            ││
│ │ [ ] Test_Run_04 (different scenario - don't merge)          ││
│ └─────────────────────────────────────────────────────────────┘│
│                                                                 │
│ Merge Options:                                                  │
│ (•) Add to session (overlay on same timeline)                   │
│ ( ) Align starting points (synchronize beginnings)              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### When to Merge Results

| Scenario | Merge? | Purpose |
|----------|--------|---------|
| Compare before/after fix | Yes | Validate improvement |
| Combine distributed test data | Yes | Complete picture |
| Compare different configurations | Yes | A/B testing |
| Completely different tests | No | Separate analyses |

## Analysis Session Management

Sessions organize your analysis work for later reference.

### Creating Sessions

```
Session Management:
───────────────────
File → New Session

Session includes:
├── Loaded result files
├── Created graphs
├── Applied filters
├── Custom views
└── Saved configurations
```

### Session Structure

```
Analysis Session (.lra file):
┌─────────────────────────────────────────────────────────────────┐
│ Session: Performance_Test_Analysis_Dec2024                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│ Loaded Results:                                                 │
│ ├── Test_Run_12_Baseline.lrr                                    │
│ ├── Test_Run_13_OptimizedDB.lrr                                 │
│ └── Test_Run_14_CacheEnabled.lrr                                │
│                                                                 │
│ Saved Graphs:                                                   │
│ ├── Transaction Response Time Comparison                        │
│ ├── Throughput Over Time                                        │
│ ├── Error Rate by Transaction                                   │
│ └── Running VUsers                                              │
│                                                                 │
│ Applied Filters:                                                │
│ ├── Time range: 10:00 - 45:00 (steady state)                    │
│ └── Exclude: warmup, rampdown                                   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Session Best Practices

1. **Save sessions regularly**: Preserve your analysis work
2. **Name descriptively**: Include date and purpose
3. **Document findings**: Add notes to sessions
4. **Archive with results**: Keep session files with test results

## Graph Types Overview

Analysis provides numerous graph types for different aspects of performance.

### Graph Categories

```
Available Graphs:
─────────────────

📊 VUsers Graphs
├── Running VUsers
├── VUsers with Errors
└── VUsers by State (init, run, end)

📈 Transaction Graphs
├── Transaction Response Time
├── Transaction Response Time (Distribution)
├── Transaction Response Time (Percentile)
├── Transactions per Second
└── Transaction Performance Summary

🌐 Web Resource Graphs
├── Hits per Second
├── Throughput
├── HTTP Responses per Second
├── Pages Downloaded per Second
└── Connections

⚠️ Error Graphs
├── Errors per Second
├── Error Statistics
└── Error Summary

💻 System Resource Graphs
├── Windows Resources
├── Unix Resources
└── Web Server Resources
```

## Transaction Response Time Graphs

The most critical graph for understanding user experience.

### Average Response Time

```
Transaction Response Time:
──────────────────────────

Response Time (seconds)
  ▲
5 │                        ╱╲
  │                      ╱╱  ╲
4 │                    ╱╱     ╲     Under load
  │      ╱╲          ╱╱        ╲    response times
3 │    ╱╱  ╲       ╱╱           ╲   increase
  │  ╱╱     ╲    ╱╱              ╲
2 │╱╱        ╲╱╱╱                 ╲
  │           Baseline             ╲ Recovery
1 │────────────────────────────────────────────────
  │
0 └──────────────────────────────────────────────▶ Time
  │← Ramp-up →│←─── Steady State ───→│← Ramp-down →│

Key Observations:
- Baseline response time: ~1 second
- Peak response time: ~5 seconds (needs investigation)
- Recovery pattern: Returns to baseline after load reduction
```

### Percentile View

```
Response Time Percentiles:
──────────────────────────

Percentile Distribution for "Checkout" Transaction:

         Percentile │ Response Time
         ───────────┼──────────────
              50th  │   1.23 sec
              75th  │   1.89 sec
              90th  │   2.45 sec   ← 90% of users under this
              95th  │   3.12 sec   ← SLA target typically here
              99th  │   5.67 sec   ← Worst 1%
             100th  │  12.34 sec   ← Maximum (outlier)

Interpretation:
- Median (50th): Typical user experience
- 90th/95th: SLA measurement point
- 99th+: Outliers, may indicate issues
```

## Throughput Graphs

Throughput measures data transfer and system capacity.

### Throughput Over Time

```
Throughput (MB/sec):
────────────────────

Throughput
  ▲
120│                 ╭──────────────╮
   │               ╱│              │╲
100│             ╱╱ │              │ ╲╲
   │           ╱╱   │   Plateau    │   ╲╲
 80│         ╱╱     │   (capacity  │     ╲╲
   │       ╱╱       │    limit)    │       ╲
 60│     ╱╱         │              │
   │   ╱╱           │              │
 40│ ╱╱             │              │
   │╱               │              │
 20│                │              │
   │                                
  0└──────────────────────────────────────────▶ Time

Key Indicators:
- Rising throughput: System handling increasing load
- Plateau: System at capacity
- Declining: Bottleneck causing degradation
```

### Transactions per Second (TPS)

```
Transactions per Second:
────────────────────────

TPS
  ▲
2000│            ╭──────────╮
    │          ╱╱          ╲╲
1500│        ╱╱              ╲╲
    │      ╱╱                  ╲╲
1000│    ╱╱                      ╲╲
    │  ╱╱                          ╲
 500│╱╱                             ╲
    │
   0└─────────────────────────────────────▶ Time
    0    10    20    30    40    50   min

Transaction Breakdown:
┌────────────────┬─────────┬─────────┐
│ Transaction    │ Avg TPS │ Peak    │
├────────────────┼─────────┼─────────┤
│ Browse         │ 1,245   │ 1,892   │
│ Search         │ 523     │ 756     │
│ Add_to_Cart    │ 156     │ 234     │
│ Checkout       │ 78      │ 112     │
└────────────────┴─────────┴─────────┘
```

## Hits Per Second

Hits measure individual HTTP requests to the server.

```
Hits per Second:
────────────────

Hits/sec
  ▲
8000│             ╭────────────╮
    │           ╱╱            ╲╲
6000│         ╱╱                ╲╲
    │       ╱╱                    ╲╲
4000│     ╱╱                        ╲
    │   ╱╱
2000│ ╱╱
    │╱
   0└─────────────────────────────────────▶ Time

Correlation with Transactions:
- 1 Transaction = Multiple Hits
- Login transaction = ~15 hits (HTML + images + CSS + JS)
- API call = ~1-3 hits
```

## Time Range Filtering

Filtering focuses analysis on relevant portions of the test.

### Why Filter?

```
Test Timeline:
──────────────

Full Test Duration:
│◀────────────────────── 75 minutes ──────────────────────▶│

│  Ramp-up  │     Steady State      │  Spike Test  │ Down │
│  (ignore) │  (primary analysis)   │  (separate)  │      │
│← 10 min ─▶│◀──── 45 min ────────▶│◀─ 15 min ──▶│◀ 5 ─▶│

Filter to Steady State:
- Exclude ramp-up (system warming up)
- Focus on 45-minute steady state
- Analyze spike separately
```

### Applying Filters

```
Time Filter Dialog:
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   Filter Time Range:                                            │
│                                                                 │
│   Start Time: [00:10:00]  ──▶  End Time: [00:55:00]             │
│                                                                 │
│   ○ All data                                                    │
│   ● Selected range only                                         │
│                                                                 │
│   Quick selections:                                             │
│   [Steady State] [First Hour] [Last 30 min] [Custom]            │
│                                                                 │
│   ┌─────────────────────────────────────────────────────────┐  │
│   │    ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒   │  │
│   │ ← │████████████████████████████████████         │ → │   │  │
│   │    ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒   │  │
│   │  0:00              Selected Range            1:15      │  │
│   └─────────────────────────────────────────────────────────┘  │
│                                                                 │
│   [Apply]  [Reset]  [Cancel]                                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Filter Best Practices

| Filter Type | When to Use |
|-------------|-------------|
| **Steady state only** | Primary performance analysis |
| **Ramp-up period** | Identify warm-up issues |
| **Time of errors** | Focus on problem periods |
| **Peak load period** | Analyze maximum stress |

## Creating Custom Graphs

Build graphs tailored to your analysis needs.

### Graph Creation Workflow

```
Create Custom Graph:
────────────────────

1. Graph → Add New Graph
2. Select graph type
3. Choose measurements
4. Configure display options
5. Save to session

Customization Options:
├── X-axis: Time, VUsers, Transactions
├── Y-axis: Multiple metrics
├── Granularity: Second, minute, hour
├── Line styles: Colors, thickness
└── Annotations: Labels, markers
```

### Overlay Multiple Metrics

```
Combined Graph Example:
───────────────────────

Response Time + Running VUsers:

          Response Time (sec)                Running VUsers
          ▲                                            ▲
        5 │              ╱╲                          │ 500
          │            ╱╱  ╲   Response              │
        4 │          ╱╱     ╲  Time                  │ 400
          │        ╱╱        ╲                       │
        3 │      ╱╱           ╲                      │ 300
          │    ╱╱              ╲                     │
        2 │  ╱╱                 ╲          ╭─────╮   │ 200
          │╱╱                    ╲       ╱╱     ╲╲  │
        1 │═══════════════════════╲════╱╱═══════╲══│ 100
          │                        ╲╱╱           ╲ │
        0 └────────────────────────────────────────▶│ 0
                        Time

Correlation visible: Response time increases with VUser count
```

## Summary

- **Analysis tool** transforms raw results into actionable performance insights
- **Opening results** can be done from Controller or standalone Analysis application
- **Merging results** enables comparison across test runs
- **Sessions** preserve analysis work including graphs, filters, and configurations
- **Transaction graphs** show user experience through response times and percentiles
- **Throughput graphs** reveal system capacity and data transfer rates
- **Hits per second** measures server request load
- **Time filtering** focuses analysis on relevant test periods

## Additional Resources

- [Analysis User Guide - Micro Focus](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Analysis/c_analysis_intro.htm)
- [Graph Types Reference](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Analysis/c_graph_types.htm)
- [Filtering and Sorting Data](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Analysis/c_filter_data.htm)

