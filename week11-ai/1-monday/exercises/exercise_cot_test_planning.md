# Exercise: Chain of Thought Test Planning

## Overview

| Attribute | Value |
|-----------|-------|
| **Duration** | 45 minutes |
| **Difficulty** | Intermediate to Advanced |
| **Mode** | Individual (Mode C: Hybrid - Design then Apply) |
| **Prerequisites** | Read chain-of-thought-prompting.md |

## Learning Objectives

By completing this exercise, you will be able to:
- Apply Chain of Thought (CoT) prompting to complex test planning
- Validate and critique AI reasoning steps
- Refine CoT prompts for better analytical outputs
- Create comprehensive test plans through guided reasoning

## The Scenario

**HealthTrack Pro** is a healthcare startup launching a new patient portal feature that allows patients to:
- View and download their medical records
- Schedule appointments with healthcare providers
- Message their care team securely
- Manage prescription refills
- View and pay medical bills

The feature handles **Protected Health Information (PHI)** and must comply with **HIPAA** regulations.

You have **2 QA engineers** and **5 business days** to create a test plan.

---

## Part 1: Direct Prompting (Baseline) - 10 minutes

First, try getting a test plan WITHOUT using Chain of Thought.

### Direct Prompt

```markdown
Create a test plan for a healthcare patient portal with these features:
- Medical records access
- Appointment scheduling  
- Secure messaging
- Prescription management
- Bill payment

We have 2 QA engineers and 5 days. The system handles PHI and must be HIPAA compliant.
```

### Task

1. Submit this prompt (or similar) to an AI assistant
2. Record the response
3. Evaluate the quality

**AI Response:**
```
[Paste the direct response here]
```

**Quality Evaluation:**

| Criteria | Score (1-5) | Notes |
|----------|-------------|-------|
| Comprehensive coverage | | |
| Prioritization logic | | |
| Risk consideration | | |
| HIPAA compliance focus | | |
| Resource allocation | | |
| Actionable detail | | |

**Total Score:** ___/30

---

## Part 2: Chain of Thought Prompting - 20 minutes

Now create a CoT prompt that guides the AI through systematic reasoning.

### Your CoT Prompt

Design a prompt that explicitly guides the AI through these steps:

**Required Steps:**
1. Feature decomposition and risk assessment
2. HIPAA compliance requirements analysis
3. Test type identification for each feature
4. Priority-based test selection
5. Resource allocation across 5 days

**Template to Complete:**

```markdown
Create a comprehensive test plan for a healthcare patient portal.

## System Context
[Describe the features and constraints]

## Think Through This Step by Step

### Step 1: Feature Decomposition
First, break down each feature into its testable components.
For each component, identify:
- Core functionality
- Data flows
- Integration points
- Security touchpoints

### Step 2: Risk Assessment
For each component from Step 1, assess:
- What could go wrong?
- What's the impact if it fails? (Patient safety, data breach, compliance violation)
- Likelihood of issues
- Risk rating (Critical/High/Medium/Low)

### Step 3: HIPAA Compliance Mapping
Identify which HIPAA requirements apply:
- Access controls (who can see what)
- Audit logging (what must be tracked)
- Data encryption (at rest and in transit)
- Minimum necessary (data exposure limits)
Map each requirement to specific tests.

### Step 4: Test Type Selection
For high-risk areas, specify which test types are needed:
- Functional tests
- Security tests
- Integration tests
- Performance tests
- Accessibility tests
Justify why each type is needed for the risk level.

### Step 5: Prioritization and Scheduling
Given 2 QA engineers and 5 days (80 person-hours):
- Categorize tests as P1 (must complete), P2 (should complete), P3 (if time)
- Assign tests to days based on dependencies
- Allocate team members to parallel work streams

Show your reasoning at each step before providing the final test plan.
```

### Task

1. Complete and submit your CoT prompt
2. Record the AI's step-by-step response
3. Validate each reasoning step

**AI Response:**
```
[Paste the full CoT response here]
```

---

## Part 3: Reasoning Validation - 10 minutes

Critically evaluate the AI's reasoning at each step.

### Step-by-Step Validation

**Step 1: Feature Decomposition**

| Check | Pass/Fail | Notes |
|-------|-----------|-------|
| All 5 features addressed? | | |
| Components are testable units? | | |
| Integration points identified? | | |
| Anything missing? | | |

**Your corrections/additions:**
```
[What did the AI miss or get wrong?]
```

---

**Step 2: Risk Assessment**

| Check | Pass/Fail | Notes |
|-------|-----------|-------|
| Risks are realistic? | | |
| Impact assessment makes sense? | | |
| PHI-related risks prioritized? | | |
| Security risks identified? | | |

**Your corrections/additions:**
```
[What did the AI miss or get wrong?]
```

---

**Step 3: HIPAA Compliance**

| HIPAA Requirement | AI Addressed? | Adequate Coverage? |
|-------------------|---------------|-------------------|
| Access controls | | |
| Audit logging | | |
| Encryption | | |
| Minimum necessary | | |
| Breach notification | | |
| User authentication | | |

**Your corrections/additions:**
```
[What compliance aspects were missed?]
```

---

**Step 4: Test Type Selection**

| Feature | AI's Test Types | Missing Types? |
|---------|-----------------|----------------|
| Medical records | | |
| Appointments | | |
| Messaging | | |
| Prescriptions | | |
| Billing | | |

---

**Step 5: Prioritization**

| Check | Pass/Fail | Notes |
|-------|-----------|-------|
| 80 person-hours accounted for? | | |
| Dependencies considered? | | |
| P1 items are truly critical? | | |
| Schedule is realistic? | | |

---

## Part 4: Comparison and Refinement - 5 minutes

### Quality Comparison

| Criteria | Direct (Part 1) | CoT (Part 2) | Improvement |
|----------|-----------------|--------------|-------------|
| Comprehensive coverage | /5 | /5 | +/- |
| Prioritization logic | /5 | /5 | +/- |
| Risk consideration | /5 | /5 | +/- |
| HIPAA compliance | /5 | /5 | +/- |
| Resource allocation | /5 | /5 | +/- |
| Actionable detail | /5 | /5 | +/- |
| **Total** | /30 | /30 | |

### Key Improvements from CoT

```
1. [What specifically improved?]
2. [What specifically improved?]
3. [What specifically improved?]
```

### Remaining Gaps

```
1. [What's still missing even with CoT?]
2. [What would you add manually?]
```

---

## Deliverables

1. **Direct prompt response** with quality scores
2. **Your completed CoT prompt**
3. **CoT response** with step-by-step output
4. **Validation checklist** for each reasoning step
5. **Comparison table** showing improvement

## Definition of Done

- [ ] Completed baseline (direct) prompt experiment
- [ ] Created CoT prompt with all 5 required steps
- [ ] Validated AI reasoning at each step
- [ ] Identified at least 3 corrections/additions per step
- [ ] Quantified improvement between approaches
- [ ] Documented remaining gaps requiring human expertise

## Evaluation Rubric

| Criteria | Excellent (4) | Good (3) | Satisfactory (2) | Needs Work (1) |
|----------|---------------|----------|------------------|----------------|
| CoT Prompt Design | All steps clearly defined with specific sub-questions | Good step structure | Basic steps | Unclear steps |
| Reasoning Validation | Thorough critique with specific corrections | Good validation | Surface validation | Minimal validation |
| HIPAA Coverage | Comprehensive compliance testing identified | Good coverage | Basic coverage | Missing key areas |
| Comparison Analysis | Quantified improvement with specific examples | Good comparison | Basic comparison | Minimal comparison |

## Bonus: Self-Consistency Check

For extra rigor, run your CoT prompt 3 times and compare:

| Step | Run 1 Conclusion | Run 2 Conclusion | Run 3 Conclusion | Consensus? |
|------|------------------|------------------|------------------|------------|
| Risk #1 Priority | | | | |
| Top P1 Test | | | | |
| Day 1 Focus | | | | |

If answers vary significantly, the reasoning may be uncertain—consider adding more constraints to your prompt.

## Reflection Questions

1. At which step did CoT provide the most value? Why?

2. Where did the AI's reasoning break down or need human correction?

3. How would you modify your CoT prompt for a different domain (e.g., fintech, e-commerce)?

4. What's the trade-off between prompt length and output quality?


