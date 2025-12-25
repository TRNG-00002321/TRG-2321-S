# Capstone: End-to-End LoadRunner Performance Testing Project

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Advanced |
| **Time Estimate** | 3-4 hours |
| **Mode** | Individual or Pair Project |
| **Prerequisites** | All Week 9 exercises completed |

## Learning Objectives
By completing this capstone, you will demonstrate mastery of:
- Complete LoadRunner performance testing lifecycle
- VuGen script development with parameterization and correlation
- Controller scenario design and execution
- Analysis tool proficiency for result interpretation
- Professional performance reporting for stakeholders
- Bottleneck identification and recommendation development

## The Capstone Challenge

You are a Performance Test Engineer at TechMart, an online electronics retailer. The company is launching a new promotional campaign expected to drive 3x normal traffic. Management needs confidence that the platform can handle the increased load before the campaign goes live.

**Your mission:** Conduct a complete performance test of the TechMart application and deliver a professional report with a go/no-go recommendation.

## Project Requirements

### Application Under Test

Use one of these options:

**Option 1: Public Demo Application** (Recommended)
- URL: https://demo.opencart.com/
- Full e-commerce functionality
- No login required for basic testing

**Option 2: Automation Practice Site**
- URL: http://automationpractice.com/
- E-commerce demo site

**Option 3: Organization Test Environment**
- Your company's test/staging environment
- Get approval from your instructor

### Business Requirements

TechMart has defined these performance requirements:

```
Performance SLAs:
═══════════════════════════════════════════════════════════════

Concurrent Users:
- Normal load: 20 concurrent users
- Campaign load (target): 50 concurrent users

Response Time Requirements:
┌────────────────────────────────────────────────────────────┐
│ Transaction           │ Avg Target │ 95th Pctl  │ Max      │
├───────────────────────┼────────────┼────────────┼──────────┤
│ Homepage              │ < 2 sec    │ < 4 sec    │ < 8 sec  │
│ Category Browse       │ < 2 sec    │ < 4 sec    │ < 8 sec  │
│ Product Search        │ < 3 sec    │ < 5 sec    │ < 10 sec │
│ Product View          │ < 2 sec    │ < 4 sec    │ < 8 sec  │
│ Add to Cart           │ < 2 sec    │ < 4 sec    │ < 8 sec  │
│ View Cart             │ < 2 sec    │ < 4 sec    │ < 8 sec  │
│ Checkout (if tested)  │ < 5 sec    │ < 8 sec    │ < 15 sec │
└───────────────────────┴────────────┴────────────┴──────────┘

Other Requirements:
- Error rate: < 1%
- Throughput: Sustain minimum 100 transactions/minute
- System must remain stable for 15+ minutes at peak load
```

## Capstone Deliverables

### Deliverable 1: VuGen Script Package

Create a complete, production-ready VuGen script:

**Script Requirements:**
- [ ] Script name: `TechMart_UserJourney.usr`
- [ ] Protocol: Web HTTP/HTML
- [ ] Minimum 5 user actions recorded:
  1. Navigate to Homepage
  2. Browse a product category
  3. Search for a product
  4. View product details
  5. Add item to cart
- [ ] Transaction markers for each action (T01, T02, etc.)
- [ ] Parameterized search terms (minimum 10 values)
- [ ] Correlation for dynamic values (if present)
- [ ] Think times between actions (realistic 3-8 seconds)
- [ ] Script replays successfully for 3+ iterations

**Submission Files:**
```
Script Package:
├── TechMart_UserJourney.usr
├── Action.c
├── vuser_init.c
├── vuser_end.c
├── parameters/
│   ├── search_terms.csv
│   └── (other parameter files)
└── Script_Documentation.md (brief README)
```

### Deliverable 2: Load Test Scenario

Design and execute a complete load test scenario:

**Scenario Requirements:**
- [ ] Scenario name: `TechMart_CampaignLoad.lrs`
- [ ] Total virtual users: 50 (Community Edition max)
- [ ] User distribution reflecting business mix:
  - Browsers (60%): 30 VUsers
  - Searchers (25%): 12 VUsers
  - Buyers (15%): 8 VUsers
- [ ] Ramp-up: Gradual (5-10 minutes)
- [ ] Steady state duration: 15+ minutes
- [ ] Ramp-down: Gradual (5 minutes)
- [ ] Successful execution to completion

**Scenario Configuration Document:**
```
Scenario Design Document
════════════════════════════════════════════════════════════════

Scenario Name: TechMart_CampaignLoad
Test Date: ______________

Virtual User Groups:
┌─────────────────────────────────────────────────────────────┐
│ Group Name       │ Script      │ VUsers │ % of Load │ LG    │
├──────────────────┼─────────────┼────────┼───────────┼───────┤
│ Browser_Users    │ TechMart... │ 30     │ 60%       │ local │
│ Search_Users     │ TechMart... │ 12     │ 25%       │ local │
│ Cart_Users       │ TechMart... │ 8      │ 15%       │ local │
├──────────────────┼─────────────┼────────┼───────────┼───────┤
│ TOTAL            │             │ 50     │ 100%      │       │
└─────────────────────────────────────────────────────────────┘

Schedule:
- Initialize: All VUsers before start
- Ramp-up: _____ VUsers every _____ seconds for _____ minutes
- Duration: _____ minutes at steady state
- Ramp-down: _____ VUsers every _____ seconds

Expected Test Duration: _____ minutes total
```

### Deliverable 3: Analysis Session

Complete analysis of test results:

**Analysis Requirements:**
- [ ] Analysis session saved: `TechMart_Analysis.lra`
- [ ] Time filter applied (steady state only)
- [ ] Minimum 6 graphs created and analyzed:
  1. Running VUsers
  2. Transaction Response Time
  3. Transaction Response Time (95th Percentile)
  4. Transactions per Second
  5. Throughput
  6. Errors per Second (if errors occurred)
- [ ] Custom correlation graph (VUsers vs Response Time)
- [ ] Transaction statistics documented
- [ ] Bottleneck investigation completed

### Deliverable 4: Professional Performance Report

Comprehensive report suitable for stakeholder review:

**Report Requirements:**
- [ ] Report format: HTML and PDF
- [ ] Executive summary (1 page max)
- [ ] Clear PASS/FAIL determination for each SLA
- [ ] All key graphs included
- [ ] Bottleneck analysis with evidence
- [ ] Prioritized recommendations
- [ ] Go/No-Go recommendation with justification

**Report Structure:**
```
Performance Test Report: TechMart Campaign Load Test
════════════════════════════════════════════════════════════════

1. Executive Summary
   - Overall Result: PASS / FAIL / CONDITIONAL PASS
   - Key Metrics Summary
   - Go/No-Go Recommendation

2. Test Overview
   - Test Objectives
   - Scope
   - Test Environment
   - Test Configuration

3. Results Summary
   - SLA Compliance Table
   - Key Performance Graphs
   - Transaction Performance

4. Detailed Analysis
   - Response Time Analysis
   - Throughput Analysis
   - Error Analysis
   - Bottleneck Identification

5. Findings and Recommendations
   - Critical Findings
   - Recommended Actions (Prioritized)
   - Risk Assessment

6. Conclusion
   - Summary
   - Next Steps

7. Appendix
   - Detailed Statistics
   - Script Documentation
   - Test Log Summary
```

## Capstone Rubric

Your capstone will be evaluated on:

### Script Quality (25 points)

| Criteria | Points | Requirements |
|----------|--------|--------------|
| Recording Quality | 5 | Clean recording, no unnecessary steps |
| Transaction Markers | 5 | All key actions measured |
| Parameterization | 5 | Multiple parameters, appropriate update methods |
| Correlation | 5 | Dynamic values handled correctly |
| Script Stability | 5 | Runs 3+ iterations without errors |

### Scenario Design (25 points)

| Criteria | Points | Requirements |
|----------|--------|--------------|
| User Mix | 5 | Realistic distribution of user types |
| Ramp Pattern | 5 | Gradual ramp-up, appropriate duration |
| Duration | 5 | Sufficient steady state (15+ min) |
| Execution | 5 | Test completed successfully |
| Documentation | 5 | Clear scenario design document |

### Analysis Quality (25 points)

| Criteria | Points | Requirements |
|----------|--------|--------------|
| Graph Creation | 5 | All required graphs present |
| Time Filtering | 5 | Steady state isolated correctly |
| Correlation Analysis | 5 | Meaningful correlations identified |
| Bottleneck ID | 5 | Root cause identified with evidence |
| Accuracy | 5 | Numbers accurate, consistent |

### Report Quality (25 points)

| Criteria | Points | Requirements |
|----------|--------|--------------|
| Executive Summary | 5 | Clear, actionable, 1-page max |
| SLA Assessment | 5 | Clear pass/fail for each requirement |
| Visual Quality | 5 | Professional graphs, good formatting |
| Recommendations | 5 | Specific, prioritized, actionable |
| Overall Professional | 5 | Publication-ready quality |

**Grading Scale:**
- 90-100: Exceptional
- 80-89: Proficient
- 70-79: Competent
- 60-69: Developing
- Below 60: Needs Improvement

## Capstone Timeline

```
Suggested Time Allocation:
════════════════════════════════════════════════════════════════

Phase 1: Script Development (60-90 minutes)
├── Record user journey: 20 min
├── Add transactions: 10 min
├── Parameterization: 15 min
├── Correlation: 15 min
├── Validation (3 iterations): 10 min
└── Documentation: 10 min

Phase 2: Scenario Design & Execution (60 minutes)
├── Scenario creation: 15 min
├── VUser group configuration: 10 min
├── Schedule design: 10 min
├── Pre-execution checklist: 5 min
└── Test execution: 20 min (running time)

Phase 3: Analysis (45-60 minutes)
├── Open results: 5 min
├── Create graphs: 15 min
├── Apply filters: 5 min
├── Analyze bottlenecks: 15 min
└── Document findings: 15 min

Phase 4: Reporting (45-60 minutes)
├── Generate base report: 10 min
├── Write executive summary: 15 min
├── Add custom content: 10 min
├── Quality review: 10 min
└── Export formats: 5 min

TOTAL: 3-4 hours
```

## Submission Package

Organize your submission as follows:

```
TechMart_Capstone_[YourName]/
├── 1_Script/
│   ├── TechMart_UserJourney/
│   │   ├── TechMart_UserJourney.usr
│   │   ├── Action.c
│   │   ├── vuser_init.c
│   │   ├── vuser_end.c
│   │   └── parameters/
│   └── Script_README.md
│
├── 2_Scenario/
│   ├── TechMart_CampaignLoad.lrs
│   └── Scenario_Design_Doc.md
│
├── 3_Results/
│   ├── [Results folder from test]
│   └── TechMart_Analysis.lra
│
├── 4_Report/
│   ├── TechMart_Performance_Report.html
│   ├── TechMart_Performance_Report.pdf
│   └── graphs/
│       ├── 01_VUsers.png
│       ├── 02_ResponseTime.png
│       └── (other graphs)
│
└── Capstone_Checklist.md (completed)
```

## Capstone Completion Checklist

```
Week 9 Capstone Completion Checklist
════════════════════════════════════════════════════════════════

Student Name: ____________________________
Completion Date: __________________________

DELIVERABLE 1: VuGen Script
[  ] Script created with correct protocol
[  ] 5+ user actions recorded
[  ] Transaction markers added (T01-T05+)
[  ] Parameterization implemented
[  ] Correlation implemented (if needed)
[  ] Think times present
[  ] Script replays 3+ iterations successfully
[  ] Documentation complete

DELIVERABLE 2: Load Test Scenario
[  ] Scenario created with 50 VUsers
[  ] User groups configured (3 groups)
[  ] Ramp-up schedule designed
[  ] Duration set to 15+ minutes
[  ] Test executed to completion
[  ] Results collected successfully
[  ] Design document complete

DELIVERABLE 3: Analysis Session
[  ] Results opened in Analysis
[  ] Time filter applied
[  ] 6+ graphs created
[  ] Correlation graph created
[  ] Bottleneck investigation complete
[  ] Session saved (.lra)

DELIVERABLE 4: Performance Report
[  ] Executive summary written
[  ] SLA compliance table complete
[  ] All graphs included
[  ] Bottleneck analysis documented
[  ] Recommendations provided
[  ] Go/No-Go decision stated
[  ] HTML version generated
[  ] PDF version generated

SUBMISSION
[  ] Folder structure correct
[  ] All files present
[  ] Files named correctly
[  ] README files complete

SELF-ASSESSMENT
Script Quality:    ___/25
Scenario Design:   ___/25
Analysis Quality:  ___/25
Report Quality:    ___/25
TOTAL:             ___/100

Ready for Submission: YES / NO
```

## Common Capstone Issues

| Issue | Prevention |
|-------|------------|
| Script won't replay | Test after each major change |
| Test runs too long | Verify schedule before starting |
| No steady state data | Ensure test runs long enough |
| Report looks unprofessional | Use templates, review formatting |
| Missing deliverables | Use the checklist throughout |

## Instructor Presentation

Be prepared to present your capstone in 10 minutes:

1. **Script Demo** (2 min): Show key parameterization and correlation
2. **Scenario Overview** (2 min): Explain your test design decisions
3. **Key Findings** (3 min): Present bottlenecks and evidence
4. **Recommendations** (2 min): Explain your go/no-go decision
5. **Q&A** (1 min): Answer questions

## Congratulations!

Completing this capstone demonstrates your readiness to conduct enterprise performance testing using LoadRunner. You've learned:

- End-to-end performance testing methodology
- Industry-standard LoadRunner tools
- Professional reporting and communication
- Bottleneck identification and troubleshooting

These skills are directly applicable to real-world performance engineering roles!

---

Commit message format:
```
feat(week9): Complete Week 9 LoadRunner Capstone Project
```

