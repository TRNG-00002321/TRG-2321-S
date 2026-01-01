# Exercise: AI Workflow Integration

## Overview

| Attribute | Value |
|-----------|-------|
| **Duration** | 45 minutes |
| **Difficulty** | Intermediate |
| **Mode** | Individual (Mode A: Implementation) |
| **Prerequisites** | Completed written content on AI-enhanced workflows |

## Learning Objectives

By completing this exercise, you will be able to:
- Integrate an AI assistant into a testing workflow
- Generate test cases from user stories using AI prompts
- Document productivity improvements from AI-assisted testing
- Identify appropriate use cases for AI in QA workflows

## The Scenario

You are a QA Engineer at **TechMart**, an e-commerce company launching a new "Wishlist" feature. The development team has provided user stories, and your task is to integrate AI assistance into your test case generation workflow.

## User Stories

```
US-101: As a logged-in user, I want to add products to my wishlist 
        so that I can save items for later purchase.

Acceptance Criteria:
- User can add any product from product detail page
- User can add products from search results
- Maximum 100 items per wishlist
- Duplicate items show count instead of multiple entries
- Guest users prompted to log in

US-102: As a user, I want to share my wishlist via email or social media
        so that friends and family can see what I want.

Acceptance Criteria:
- Generate shareable link with privacy options (public/private)
- Share to Facebook, Twitter, email
- Shared list is read-only for recipients
- Owner can revoke sharing at any time
```

## Tasks

### Task 1: Baseline (Without AI) - 10 minutes

Before using AI, manually write test cases for **US-101** using your traditional approach.

**Instructions:**
1. Set a timer for 10 minutes
2. Write as many test cases as you can
3. Use the format below

**Test Case Format:**
```markdown
| TC-ID | Title | Steps | Expected Result | Priority |
|-------|-------|-------|-----------------|----------|
| TC-001 | ... | 1. ... 2. ... | ... | High/Med/Low |
```

**Record your results:**
- Number of test cases written: ____
- Categories covered: [ ] Positive [ ] Negative [ ] Boundary [ ] Security
- Time spent: 10 minutes

### Task 2: AI-Assisted Test Generation - 15 minutes

Now use an AI assistant (Claude, ChatGPT, or local model) to generate test cases for **US-102**.

**Instructions:**

1. **Craft your initial prompt** following the prompt engineering principles from today's content
   
2. **Include these elements:**
   - Context (what the feature does)
   - Specific instruction (generate test cases)
   - Constraints (focus areas, quantity)
   - Format specification (how to structure output)

3. **Iterate on your prompt** at least twice to improve results

4. **Document your prompt evolution:**

```markdown
## Prompt Iteration Log

### Attempt 1
**Prompt:**
[Your first prompt]

**Result Quality:** (1-5 stars)
**Issues Identified:**

---

### Attempt 2
**Prompt:**
[Refined prompt]

**Result Quality:** (1-5 stars)
**Improvements Made:**

---

### Final Prompt
**Prompt:**
[Your best prompt]

**Result Quality:** (1-5 stars)
**Why this worked:**
```

### Task 3: Quality Comparison - 10 minutes

Compare your manual test cases (Task 1) with AI-generated test cases (Task 2).

**Complete this comparison table:**

| Metric | Manual | AI-Assisted |
|--------|--------|-------------|
| Total test cases | | |
| Positive scenarios | | |
| Negative scenarios | | |
| Boundary conditions | | |
| Security considerations | | |
| Edge cases found | | |
| Time to generate | 10 min | |

**Analysis Questions:**

1. What types of test cases did the AI generate that you missed manually?

```
[Your answer]
```

2. What did the AI miss that you included manually?

```
[Your answer]
```

3. How would you rate the overall quality difference?

```
[Your answer]
```

### Task 4: Workflow Documentation - 10 minutes

Create a brief workflow document describing how you would integrate AI assistance into your daily testing activities.

**Template:**

```markdown
# AI-Assisted Testing Workflow

## When to Use AI Assistance

| Task | AI Helpful? | Reason |
|------|-------------|--------|
| Test case generation | | |
| Bug report writing | | |
| Test data creation | | |
| Exploratory testing | | |
| Regression planning | | |

## My AI Prompt Templates

### Test Case Generation Template
```
[Your reusable template]
```

### Bug Analysis Template  
```
[Your reusable template]
```

## Productivity Gains

- Estimated time saved per sprint: ____ hours
- Quality improvements observed: 
- Limitations encountered:

## Best Practices I Discovered

1. 
2. 
3. 
```

## Deliverables

Submit the following:

1. **Manual test cases** from Task 1 (screenshot or document)
2. **Prompt iteration log** from Task 2
3. **Comparison table** from Task 3
4. **Workflow document** from Task 4

## Definition of Done

- [ ] Completed at least 5 manual test cases for US-101
- [ ] Iterated on AI prompts at least 2 times with documented improvements
- [ ] Generated at least 10 AI-assisted test cases for US-102
- [ ] Completed comparison table with analysis
- [ ] Created workflow document with at least 2 reusable prompt templates
- [ ] Documented specific productivity improvements observed

## Evaluation Rubric

| Criteria | Excellent (4) | Good (3) | Satisfactory (2) | Needs Work (1) |
|----------|---------------|----------|------------------|----------------|
| Prompt Quality | Clear context, constraints, format; shows iteration | Good structure, minor gaps | Basic structure | Vague or incomplete |
| Test Coverage | Comprehensive scenarios including edge cases | Good coverage, few gaps | Basic positive/negative | Minimal coverage |
| Analysis Depth | Insightful comparison with specific examples | Good comparison | Basic comparison | Superficial |
| Documentation | Complete, reusable templates | Mostly complete | Partial | Incomplete |

## Hints

- Start with a simple prompt, then add specificity based on gaps
- Don't accept the first AI response - iterate!
- Consider asking the AI to critique its own output
- Save your best prompts as templates for future use

## Extension Challenge

If you finish early, try these advanced tasks:

1. **Cross-prompt comparison:** Use the same prompt on two different AI models and compare outputs
2. **Negative prompting:** Experiment with telling the AI what NOT to include
3. **Format exploration:** Try generating test cases in different formats (Gherkin, table, JSON)


