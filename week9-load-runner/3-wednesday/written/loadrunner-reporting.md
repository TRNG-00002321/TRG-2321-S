# LoadRunner Reporting

## Learning Objectives
- Generate professional performance reports in LoadRunner
- Understand and customize report templates
- Configure report content for different audiences
- Export reports to HTML, Word, and PDF formats
- Create effective executive summaries
- Set up automated report generation

## Why This Matters

A comprehensive performance test is only valuable if its findings reach the right people in the right format. Technical teams need detailed data; executives need summaries. LoadRunner's reporting capabilities help you communicate results effectively to all stakeholders.

As you complete **Mastering Enterprise Performance Testing with LoadRunner**, reporting skills enable you to translate technical findings into business decisions and drive organizational action on performance issues.

## Generating Performance Reports

LoadRunner Analysis provides built-in report generation capabilities.

### Accessing Report Generation

```
Generate Report:
────────────────
Reports → HTML Report (or Word Report)

Quick Generate:
- File → Generate Report
- Keyboard: Ctrl+Shift+R
```

### Report Generation Workflow

```
┌─────────────────────────────────────────────────────────────────┐
│                   Report Generation Process                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   1. Select Template      2. Configure Content                  │
│   ┌─────────────────┐     ┌─────────────────┐                  │
│   │ ○ Executive     │     │ [✓] Summary     │                  │
│   │ ● Standard      │────▶│ [✓] Graphs      │                  │
│   │ ○ Detailed      │     │ [✓] Statistics  │                  │
│   │ ○ Custom        │     │ [ ] Raw Data    │                  │
│   └─────────────────┘     └─────────────────┘                  │
│                                  │                              │
│                                  ▼                              │
│   3. Choose Format         4. Generate                         │
│   ┌─────────────────┐     ┌─────────────────┐                  │
│   │ ● HTML          │     │ ▶ Generate      │                  │
│   │ ○ Word (.doc)   │────▶│                 │                  │
│   │ ○ PDF           │     │ Progress: ████░ │                  │
│   │ ○ All formats   │     │                 │                  │
│   └─────────────────┘     └─────────────────┘                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Report Templates

Templates define the structure and content of reports.

### Built-in Templates

| Template | Audience | Content Focus |
|----------|----------|---------------|
| **Executive** | Management | Summary, key metrics, pass/fail |
| **Standard** | Technical leads | Balanced detail and overview |
| **Detailed** | Engineers | All metrics, raw data, graphs |
| **SLA** | Stakeholders | SLA compliance focus |
| **Comparison** | All | Before/after analysis |

### Template Structure

```
Standard Report Template Structure:
───────────────────────────────────

1. Executive Summary
   ├── Test Overview
   ├── Key Findings
   └── Recommendations

2. Test Configuration
   ├── Scenario Details
   ├── VUser Distribution
   └── Duration & Schedule

3. Performance Summary
   ├── Transaction Statistics
   ├── Response Time Summary
   └── Throughput Summary

4. Detailed Results
   ├── Transaction Response Time Graphs
   ├── Running VUsers Graph
   ├── Throughput Over Time
   └── Error Analysis

5. Appendix
   ├── Test Environment
   ├── Script Information
   └── Raw Statistics
```

## Customizing Report Content

Tailor reports to your specific needs.

### Content Selection

```
Report Content Customization:
─────────────────────────────

Available Sections:                    Include in Report:
┌────────────────────────────┐        ┌────────────────────────────┐
│ Summary                    │   ──▶  │ [✓] Summary                │
│ ├── Executive Overview     │        │     [✓] Executive Overview │
│ ├── Test Statistics        │        │     [✓] Test Statistics    │
│ └── SLA Status             │        │     [ ] SLA Status         │
│                            │        │                            │
│ Graphs                     │   ──▶  │ [✓] Graphs                 │
│ ├── Running VUsers         │        │     [✓] Running VUsers     │
│ ├── Transaction Response   │        │     [✓] Trans Response     │
│ ├── Throughput             │        │     [✓] Throughput         │
│ ├── Hits per Second        │        │     [ ] Hits per Second    │
│ ├── Errors per Second      │        │     [✓] Errors per Second  │
│ └── Custom Graphs          │        │     [✓] Custom Graphs      │
│                            │        │                            │
│ Statistics Tables          │   ──▶  │ [✓] Statistics Tables      │
│ ├── Transaction Summary    │        │     [✓] Trans Summary      │
│ ├── Percentile Data        │        │     [✓] Percentile Data    │
│ └── Error Details          │        │     [✓] Error Details      │
└────────────────────────────┘        └────────────────────────────┘
```

### Graph Customization

```
Graph Options for Reports:
──────────────────────────

For each graph:
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│ Graph: Transaction Response Time                                │
│                                                                 │
│ Include: [✓]                                                    │
│                                                                 │
│ Size:    ○ Small (400x300)                                      │
│          ● Medium (600x400)                                     │
│          ○ Large (800x600)                                      │
│                                                                 │
│ Options: [✓] Include legend                                     │
│          [✓] Include data table                                 │
│          [✓] Show trend line                                    │
│          [ ] Include annotations                                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Exporting to Different Formats

### HTML Reports

Best for: Web sharing, interactive viewing

```
HTML Report Features:
─────────────────────
✓ Clickable navigation
✓ Expandable sections
✓ Interactive graphs (zoom, hover)
✓ Hyperlinked table of contents
✓ Embedded images
✓ CSS styling

Output: Folder containing:
├── index.html
├── css/
│   └── report.css
├── images/
│   ├── graph1.png
│   ├── graph2.png
│   └── ...
└── js/
    └── report.js
```

### Word Reports

Best for: Editing, formal documentation

```
Word Report Features:
─────────────────────
✓ Editable content
✓ Corporate template integration
✓ Embedded graphs as images
✓ Formatted tables
✓ Page numbers and headers
✓ Table of contents

Output: Single .doc or .docx file

Tip: Use Word for reports requiring 
     management sign-off or edits
```

### PDF Reports

Best for: Distribution, archiving

```
PDF Report Features:
────────────────────
✓ Fixed formatting
✓ Print-ready
✓ Smaller file size
✓ Universal viewing
✓ Cannot be edited (document control)
✓ Embedded fonts

Output: Single .pdf file

Best For:
- Final deliverables
- Archive copies
- Email distribution
```

## Creating Executive Summaries

Executive summaries provide quick insights for decision-makers.

### Executive Summary Structure

```
Executive Summary Template:
───────────────────────────

┌─────────────────────────────────────────────────────────────────┐
│                    PERFORMANCE TEST SUMMARY                     │
│                    ─────────────────────────                    │
│                                                                 │
│ Test Name:     Black Friday Load Test                           │
│ Date:          December 10, 2024                                │
│ Duration:      2 hours                                          │
│ Peak Load:     1,000 concurrent users                           │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                         KEY METRICS                             │
│                                                                 │
│ ┌─────────────────┬────────────┬────────────┬─────────┐        │
│ │ Metric          │ Target     │ Actual     │ Status  │        │
│ ├─────────────────┼────────────┼────────────┼─────────┤        │
│ │ Avg Response    │ < 3 sec    │ 2.1 sec    │ ✓ PASS  │        │
│ │ 95th Percentile │ < 5 sec    │ 3.8 sec    │ ✓ PASS  │        │
│ │ Error Rate      │ < 1%       │ 0.3%       │ ✓ PASS  │        │
│ │ Throughput      │ > 500 TPS  │ 650 TPS    │ ✓ PASS  │        │
│ └─────────────────┴────────────┴────────────┴─────────┘        │
│                                                                 │
│ Overall Status: ✓ PASSED                                        │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                       KEY FINDINGS                              │
│                                                                 │
│ ✓ System handled 1,000 users within SLA requirements            │
│ ⚠ Checkout transaction showed 20% degradation at peak load      │
│ ✓ No errors during normal load (< 500 users)                    │
│ ⚠ Database connection pool reached 90% at peak                  │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                     RECOMMENDATIONS                             │
│                                                                 │
│ 1. Increase database connection pool from 50 to 75              │
│ 2. Optimize checkout query (currently taking 1.8s)              │
│ 3. Re-test after optimizations before Black Friday              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Writing Effective Summaries

| Element | Tips |
|---------|------|
| **Metrics** | Show target vs. actual with clear pass/fail |
| **Findings** | Lead with the most important discovery |
| **Status** | Use visual indicators (colors, icons) |
| **Recommendations** | Be specific and actionable |
| **Language** | Avoid jargon, focus on business impact |

## Automated Report Generation

Automate reports for consistent, repeatable documentation.

### Command-Line Report Generation

```
Automated Report via Command Line:
──────────────────────────────────

AnalysisUI.exe -ResultsFile "C:\Results\test.lrr" 
               -Template "Standard" 
               -Output "C:\Reports\report.html"
               -Format HTML

Parameters:
-ResultsFile : Path to .lrr file
-Template    : Template name or path
-Output      : Output file path
-Format      : HTML, Word, PDF, or All

Batch Script Example:
─────────────────────
@echo off
set RESULTS=C:\LoadTests\Results\%DATE%
set OUTPUT=C:\Reports\%DATE%_report.html

"C:\Program Files\LoadRunner\Analysis\bin\AnalysisUI.exe" ^
    -ResultsFile "%RESULTS%\test.lrr" ^
    -Template "Standard" ^
    -Output "%OUTPUT%" ^
    -Format HTML

echo Report generated: %OUTPUT%
```

### Integration with CI/CD

```
CI/CD Report Integration:
─────────────────────────

Pipeline Step:
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│  1. Run Load Test                                               │
│     ├── Execute LoadRunner scenario                             │
│     └── Collect results                                         │
│                                                                 │
│  2. Generate Report                                             │
│     ├── Call AnalysisUI with results                            │
│     └── Output HTML report                                      │
│                                                                 │
│  3. Publish Report                                              │
│     ├── Upload to artifact storage                              │
│     └── Link in build results                                   │
│                                                                 │
│  4. Evaluate Results                                            │
│     ├── Parse summary for pass/fail                             │
│     └── Fail build if SLA not met                               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Report Best Practices

### Audience-Appropriate Content

```
Content by Audience:
────────────────────

Executive Leadership:
├── 1-2 page summary
├── Pass/fail status prominent
├── Business impact focus
├── Recommendations with cost/effort
└── No technical jargon

Technical Management:
├── 3-5 page summary
├── Key metrics with trends
├── High-level architecture context
├── Technical recommendations
└── Resource requirements

Development Teams:
├── Full detailed report
├── All graphs and statistics
├── Transaction-level breakdown
├── Error details and logs
└── Root cause analysis
```

### Visual Best Practices

```
Effective Report Visuals:
─────────────────────────

DO:
✓ Use consistent color coding (green=good, red=bad)
✓ Include legends on all graphs
✓ Show targets/thresholds on graphs
✓ Use appropriate scale for axes
✓ Include time context

DON'T:
✗ Overcrowd with too many metrics
✗ Use 3D graphs (distorts data)
✗ Truncate axes misleadingly
✗ Include raw data dumps
✗ Use inconsistent formatting
```

## Summary

- **Report generation** transforms raw results into shareable documents
- **Templates** provide consistent structure; customize for specific needs
- **Export formats** serve different purposes: HTML for viewing, Word for editing, PDF for distribution
- **Executive summaries** focus on pass/fail status, key findings, and recommendations
- **Automated generation** integrates reporting into CI/CD pipelines
- **Audience awareness** ensures reports communicate effectively to each stakeholder group

## Additional Resources

- [Analysis Reporting Guide - Micro Focus](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Analysis/c_reports.htm)
- [Creating Custom Templates](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Analysis/c_custom_templates.htm)
- [Command-Line Analysis Reference](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Analysis/c_command_line.htm)

