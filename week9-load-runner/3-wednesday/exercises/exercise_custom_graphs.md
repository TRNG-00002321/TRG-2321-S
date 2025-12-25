# Lab: Creating Custom Correlation Graphs

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Lab |
| **Prerequisites** | exercise_results_analysis.md, demo_correlation_graphs.md, performance-metrics-interpretation.md |

## Learning Objectives
By completing this exercise, you will:
- Create custom graphs that combine multiple metrics
- Configure graph appearance for clear communication
- Build correlation views that tell a performance story
- Use annotations to highlight key findings
- Export graphs for inclusion in reports
- Apply analysis techniques to uncover hidden patterns

## The Scenario

Your investigation has identified performance patterns in the holiday sale load test. Now you need to create compelling visualizations that clearly communicate these findings to both technical and non-technical stakeholders. Your graphs should tell the story of what happened during the test.

## Core Tasks

### Task 1: Plan Your Performance Story (10 minutes)

Before creating graphs, plan what story you want to tell:

**Story Framework:**

```
The Performance Story:
══════════════════════════════════════════════════════════════

Beginning (Ramp-Up):
- "As users arrived, the system..."
- Key metric to show: ________________________________

Middle (Steady State):  
- "At peak load, the system..."
- Key finding to highlight: ________________________________

Climax (Issue Point):
- "The critical moment occurred when..."
- Evidence needed: ________________________________

Resolution (Ramp-Down):
- "As load decreased, the system..."
- Recovery pattern: ________________________________

Key Message for Stakeholders:
"________________________________________________
________________________________________________"
```

### Task 2: Create the Response Time Trend Graph (15 minutes)

Create a graph showing how response times evolved during the test:

#### Step-by-Step:

1. Open your Analysis session
2. Go to **Graph → Add New Graph**
3. Select **Transaction Response Time** (Average or Percentile)
4. Click **Open Graph**

#### Customize the Graph:

1. **Right-click graph → Graph Configuration**
2. Configure appearance:
   - Title: "Response Time Under Load - Holiday Sale Test"
   - Y-Axis Label: "Response Time (seconds)"
   - X-Axis: Time

3. **Add Reference Lines:**
   - Right-click → **Add Horizontal Line**
   - Add line at your SLA threshold (e.g., 3 seconds)
   - Label it "SLA Target (3s)"

4. **Configure Legend:**
   - Show transaction names clearly
   - Position legend where it doesn't obstruct data

**Screenshot your graph and document:**
```
Graph 1: Response Time Trend
────────────────────────────
Purpose: Show response time behavior under load

Key Observations:
1. ________________________________________________
2. ________________________________________________
3. ________________________________________________

SLA Compliance:
- Transactions within SLA: ___/___
- Transactions exceeding SLA: ___/___
```

### Task 3: Create a Combined VUsers + Response Time Graph (15 minutes)

Overlay VUsers and Response Time to show correlation:

#### Step-by-Step:

1. Open **Running VUsers** graph
2. Open **Transaction Response Time** graph
3. Use **Graph → Merge Graphs** 
   - Select both graphs
   - Choose **Overlay** option

#### Alternative Method (Side-by-Side):

1. Open both graphs
2. **Window → Tile Horizontally**
3. Align time scales

#### Customize the Combined View:

1. **Dual Y-Axis:**
   - Left axis: Response Time (seconds)
   - Right axis: Running VUsers

2. **Color Coding:**
   - VUsers line: Blue
   - Response Time line: Red (indicates attention needed)

3. **Add Annotation:**
   - Right-click at the point where response time spikes
   - Add text: "Bottleneck threshold reached"

**Document your correlation graph:**
```
Graph 2: VUsers vs. Response Time Correlation
─────────────────────────────────────────────
Purpose: Demonstrate relationship between load and performance

Correlation Observed:
[ ] Strong positive correlation (response time increases with VUsers)
[ ] Threshold pattern (stable until X VUsers, then degrades)
[ ] No correlation (response time independent of load)
[ ] Other: ________________________________

Key Inflection Points:
- At ___ VUsers: ________________________________
- At ___ VUsers: ________________________________

Story this tells: ________________________________
```

### Task 4: Create a Throughput Capacity Graph (10 minutes)

Show system capacity through throughput analysis:

#### Step-by-Step:

1. Open **Throughput** graph
2. Overlay with **Running VUsers** graph

#### Add Analysis Elements:

1. **Identify Plateau Point:**
   - Where did throughput stop increasing?
   - Add annotation at this point

2. **Add Trend Line:**
   - Right-click → **Add Trend Line** (if available)
   - Shows ideal linear scaling

3. **Highlight Capacity:**
   - Mark the maximum sustained throughput
   - Note: "System capacity: ___ KB/sec"

**Document your throughput graph:**
```
Graph 3: Throughput Capacity Analysis
─────────────────────────────────────
Purpose: Show system throughput under increasing load

Measurements:
- Initial throughput (10 VUsers): _______ KB/sec
- Peak throughput: _______ KB/sec
- Throughput at 50 VUsers: _______ KB/sec

Capacity Assessment:
- Linear scaling observed up to: _______ VUsers
- Throughput plateau at: _______ VUsers
- Maximum sustainable throughput: _______ KB/sec

Story this tells: ________________________________
```

### Task 5: Create a Transaction Comparison Graph (10 minutes)

Compare performance across different transactions:

#### Step-by-Step:

1. Open **Transaction Response Time** graph
2. Ensure all transactions are displayed
3. Configure for comparison view

#### Customize for Comparison:

1. **Bar Chart View:**
   - Right-click → **Display as Bar Chart**
   - Easier to compare transaction performance

2. **Group by Transaction:**
   - Shows Min, Avg, Max for each
   - Quickly identify problematic transactions

3. **Add SLA Lines:**
   - Horizontal line at target response time
   - Visual pass/fail for each transaction

**Document your comparison graph:**
```
Graph 4: Transaction Performance Comparison
───────────────────────────────────────────
Purpose: Compare performance across business operations

Transaction Ranking (fastest to slowest):
1. _______________________ : _______ sec (avg)
2. _______________________ : _______ sec (avg)
3. _______________________ : _______ sec (avg)
4. _______________________ : _______ sec (avg)
5. _______________________ : _______ sec (avg)

Observations:
- Best performing: ________________________________
- Needs improvement: ________________________________
- SLA failures: ________________________________
```

### Task 6: Export and Organize Your Graphs (5 minutes)

Prepare graphs for inclusion in reports:

#### Export Each Graph:

1. Select the graph
2. **File → Export Graph** or right-click → **Export**
3. Choose format:
   - **PNG**: Best for presentations
   - **JPG**: Smaller file size
   - **EMF**: Best quality for Word documents

4. Name files descriptively:
   - `01_ResponseTime_Trend.png`
   - `02_VUsers_ResponseTime_Correlation.png`
   - `03_Throughput_Capacity.png`
   - `04_Transaction_Comparison.png`

**Create a Graph Index:**
```
Custom Graphs Created:
══════════════════════════════════════════════════════════════

│ # │ File Name                         │ Purpose              │
├───┼───────────────────────────────────┼──────────────────────┤
│ 1 │ 01_ResponseTime_Trend.png         │ Show RT over time    │
│ 2 │ 02_VUsers_RT_Correlation.png      │ Load vs performance  │
│ 3 │ 03_Throughput_Capacity.png        │ Capacity limits      │
│ 4 │ 04_Transaction_Comparison.png     │ Compare operations   │
└───┴───────────────────────────────────┴──────────────────────┘

Files exported to: ________________________________
```

## Definition of Done

Your custom graphs are complete when:
- [ ] At least 4 custom graphs created
- [ ] Response time trend graph with SLA reference line
- [ ] VUsers + Response Time correlation graph
- [ ] Throughput capacity analysis graph
- [ ] Transaction comparison graph
- [ ] Annotations added to highlight key findings
- [ ] All graphs exported as image files
- [ ] Graph index created

## Graph Quality Checklist

```
Graph Quality Review:
═══════════════════════════════════════════════════════════════

For each graph, verify:

Graph 1: Response Time Trend
[  ] Clear title
[  ] Axis labels present
[  ] SLA reference line added
[  ] Legend is readable
[  ] Key points annotated

Graph 2: VUsers vs Response Time
[  ] Both metrics clearly visible
[  ] Dual axis properly labeled
[  ] Correlation point marked
[  ] Color coding logical

Graph 3: Throughput Capacity
[  ] Capacity limit identified
[  ] Plateau point annotated
[  ] Scale appropriate

Graph 4: Transaction Comparison
[  ] All transactions visible
[  ] Ranking clear
[  ] SLA threshold shown
[  ] Winners/losers identifiable

Overall Quality: PRESENTATION READY / NEEDS WORK
```

## Visualization Best Practices

```
Effective Graph Design:
═══════════════════════════════════════════════════════════════

DO:
✓ Use consistent color scheme across all graphs
✓ Include clear, descriptive titles
✓ Label all axes with units
✓ Add annotations at key points
✓ Use appropriate scale (don't truncate Y-axis misleadingly)
✓ Include legend when multiple data series
✓ Keep graphs simple and focused

DON'T:
✗ Overcrowd with too many metrics
✗ Use 3D effects (distorts data)
✗ Start Y-axis at arbitrary point
✗ Use red and green together (color blindness)
✗ Include unnecessary grid lines
✗ Make text too small to read
✗ Leave graphs without context
```

## Stretch Goals (Optional)

If you finish early:
1. Create a **Percentile Distribution** graph showing 50th, 90th, 95th, 99th percentiles
2. Build an **Error Timeline** graph correlating errors with load
3. Create a **Before/After** comparison view (if you have multiple test results)
4. Design a **Dashboard View** combining 4 key graphs in one view
5. Experiment with different visualization types (area charts, stacked bars)

## Common Mistakes to Avoid

1. **Too many lines on one graph** - Keep it simple, split if needed
2. **Misleading scales** - Y-axis should start at zero for fair comparison
3. **Missing context** - Always include what load level the data represents
4. **Poor annotation placement** - Don't cover the data
5. **Inconsistent colors** - Same metric should use same color across graphs

## Troubleshooting Guide

| Issue | Possible Cause | Solution |
|-------|---------------|----------|
| Cannot merge graphs | Different time scales | Verify results are from same test |
| Graph too crowded | Too many metrics | Split into separate graphs |
| Export fails | File path issue | Check permissions, use simple path |
| Colors don't show in export | Format issue | Use PNG instead of EMF |
| Annotations disappear | Not saved with session | Save session after adding |

## Submission

1. Export all 4 graphs as PNG files
2. Create a folder with organized graph files
3. Complete the graph index documentation

Commit message format:
```
feat(week9): Complete custom correlation graphs exercise
```

