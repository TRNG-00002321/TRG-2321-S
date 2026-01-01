# Exercise: Zero-Shot vs Few-Shot Prompting Comparison

## Overview

| Attribute | Value |
|-----------|-------|
| **Duration** | 45 minutes |
| **Difficulty** | Intermediate |
| **Mode** | Individual (Mode A: Implementation) |
| **Prerequisites** | Read zero-shot-prompting.md and few-shot-prompting.md |

## Learning Objectives

By completing this exercise, you will be able to:
- Apply both zero-shot and few-shot prompting techniques
- Evaluate when each approach is most effective
- Measure and compare output quality differences
- Select the appropriate prompting strategy for different QA tasks

## The Experiment

You will complete the same three QA tasks using both zero-shot and few-shot approaches, then compare results.

---

## Task 1: Bug Report Summarization

### Scenario

You need to create concise bug summaries for a status report. The summaries should follow a specific format used by your team.

### Team's Required Format

```
[Component] - [Severity] - One sentence description focusing on user impact
```

### Bug Reports to Summarize

**Bug A:**
```
Title: Payment confirmation email not sent for orders over $500
Description: When a customer completes an order totaling more than $500, 
the payment confirmation email is not triggered. Smaller orders receive 
emails correctly. Customer service has received 23 complaints this week.
Technical Details: The email service times out when processing large order 
data payloads. Error logs show "Request entity too large" in email-service.
Priority: P1
```

**Bug B:**
```
Title: Mobile app crashes when uploading profile photo
Description: Users on Android 12+ experience app crashes when trying to 
update their profile picture. The crash occurs after selecting a photo 
from the gallery. iOS users are not affected. Crash rate is 15% of 
Android users who attempt this action.
Technical Details: Stack trace shows OutOfMemoryError in ImageProcessor.
The app doesn't resize images before processing.
Priority: P2
```

**Bug C:**
```
Title: Search results show incorrect product counts
Description: The search results page shows "Showing 1-20 of 150 results" 
but only 45 products actually match the search. The count appears to 
include out-of-stock and discontinued items that are filtered from display.
Technical Details: Count query doesn't apply the same filters as the 
results query. Different cache TTLs between services.
Priority: P3
```

### Part A: Zero-Shot Approach

Write a zero-shot prompt (no examples) to summarize these bugs:

**Your Zero-Shot Prompt:**
```markdown
[Write your prompt here - NO examples included]
```

**AI Output:**
```
[Paste the AI's summaries here]
```

**Quality Rating (1-5):** ____

### Part B: Few-Shot Approach

Now write a few-shot prompt with 2 examples showing the exact format you want:

**Your Few-Shot Prompt:**
```markdown
[Write your prompt here - INCLUDE 2 example summaries you create]
```

**AI Output:**
```
[Paste the AI's summaries here]
```

**Quality Rating (1-5):** ____

### Part C: Comparison

| Criteria | Zero-Shot | Few-Shot |
|----------|-----------|----------|
| Followed format correctly? | | |
| Appropriate severity label? | | |
| User impact focus? | | |
| Conciseness (one sentence)? | | |
| Overall quality (1-5) | | |

**Winner for this task:** [ ] Zero-Shot [ ] Few-Shot [ ] Tie

**Why?**
```
[Your explanation]
```

---

## Task 2: Test Data Classification

### Scenario

You need to classify test data entries as Valid, Invalid, or Edge Case for a form validation system.

### Test Data to Classify

| Entry | Field | Value |
|-------|-------|-------|
| 1 | Email | user@company.co.uk |
| 2 | Email | user@.com |
| 3 | Email | a@b.co |
| 4 | Age | 18 |
| 5 | Age | 17 |
| 6 | Age | 150 |
| 7 | Phone | +1-555-123-4567 |
| 8 | Phone | 555.123.4567 |
| 9 | Phone | 123 |
| 10 | Username | ab |

### Part A: Zero-Shot Approach

**Your Zero-Shot Prompt:**
```markdown
[Write your prompt here - NO examples]
```

**AI Output:**
```
[Paste results here]
```

### Part B: Few-Shot Approach

Create examples that establish clear classification criteria:

**Your Few-Shot Prompt:**
```markdown
[Write your prompt here with at least 3 classification examples]

Example classifications to include in your prompt:
- One clear valid case
- One clear invalid case  
- One edge case with explanation
```

**AI Output:**
```
[Paste results here]
```

### Part C: Accuracy Check

Score the AI's classifications against your expected answers:

| Entry | Your Expected | Zero-Shot Answer | Few-Shot Answer |
|-------|---------------|------------------|-----------------|
| 1 | | | |
| 2 | | | |
| 3 | | | |
| 4 | | | |
| 5 | | | |
| 6 | | | |
| 7 | | | |
| 8 | | | |
| 9 | | | |
| 10 | | | |

**Accuracy:**
- Zero-Shot: ___/10 correct
- Few-Shot: ___/10 correct

**Winner for this task:** [ ] Zero-Shot [ ] Few-Shot [ ] Tie

---

## Task 3: Test Case Priority Assignment

### Scenario

Your team uses specific criteria for test case priorities. This is a domain-specific classification that requires understanding team conventions.

### Team's Priority Rules

```
CRITICAL: Tests for payment processing, user authentication, data security
HIGH: Tests for core user journeys (checkout, registration, search)
MEDIUM: Tests for secondary features and edge cases
LOW: Tests for cosmetic issues, minor UX improvements
```

### Test Cases to Prioritize

1. Verify user can complete checkout with valid credit card
2. Verify login fails after 5 incorrect password attempts
3. Verify search results highlight matching keywords
4. Verify password reset email contains correct link
5. Verify product image zoom on hover works smoothly
6. Verify credit card number is masked in order history
7. Verify "Add to Cart" button color matches design spec
8. Verify session timeout after 30 minutes of inactivity
9. Verify search suggestions appear within 200ms
10. Verify user data export includes all GDPR-required fields

### Part A: Zero-Shot Approach

**Your Zero-Shot Prompt:**
```markdown
[Include the priority rules in your prompt]
```

**AI Output:**
```
[Paste results]
```

### Part B: Few-Shot Approach

**Your Few-Shot Prompt:**
```markdown
[Include priority rules PLUS 2-3 example classifications]
```

**AI Output:**
```
[Paste results]
```

### Part C: Evaluation

Score against your team's expected priorities:

| Test Case # | Expected Priority | Zero-Shot | Few-Shot |
|-------------|-------------------|-----------|----------|
| 1 | | | |
| 2 | | | |
| 3 | | | |
| 4 | | | |
| 5 | | | |
| 6 | | | |
| 7 | | | |
| 8 | | | |
| 9 | | | |
| 10 | | | |

**Winner for this task:** [ ] Zero-Shot [ ] Few-Shot [ ] Tie

---

## Summary Analysis

### Results Overview

| Task | Zero-Shot Score | Few-Shot Score | Winner |
|------|-----------------|----------------|--------|
| Bug Summarization | | | |
| Data Classification | | | |
| Priority Assignment | | | |

### Key Findings

**1. When did Zero-Shot perform well?**
```
[Your observations]
```

**2. When did Few-Shot perform better?**
```
[Your observations]
```

**3. How much extra effort did Few-Shot require?**
```
[Time/complexity comparison]
```

### Decision Framework

Based on your experiment, complete this decision guide:

```markdown
## When to Use Zero-Shot

Use zero-shot prompting when:
- [ ] Task is [your observation]
- [ ] Output format is [your observation]
- [ ] Domain is [your observation]
- [ ] Time constraints are [your observation]

## When to Use Few-Shot

Use few-shot prompting when:
- [ ] Task requires [your observation]
- [ ] Output must match [your observation]
- [ ] Domain has [your observation]
- [ ] Consistency is [your observation]
```

## Deliverables

1. **All prompts** (6 total: 3 zero-shot, 3 few-shot)
2. **AI outputs** for each prompt
3. **Comparison tables** with scores
4. **Summary analysis** with decision framework

## Definition of Done

- [ ] Completed all 3 tasks with both approaches
- [ ] Documented all prompts and outputs
- [ ] Scored accuracy/quality for each approach
- [ ] Identified winner for each task with justification
- [ ] Created decision framework based on findings

## Evaluation Rubric

| Criteria | Excellent (4) | Good (3) | Satisfactory (2) | Needs Work (1) |
|----------|---------------|----------|------------------|----------------|
| Experiment Rigor | Fair comparison, same criteria | Mostly fair | Some inconsistency | Unfair comparison |
| Few-Shot Examples | High-quality, diverse examples | Good examples | Basic examples | Poor/missing examples |
| Analysis Depth | Insightful patterns identified | Good analysis | Surface analysis | Minimal analysis |
| Decision Framework | Actionable, specific guidance | Useful guidance | Generic guidance | Incomplete |

## Reflection Questions

1. Did the results match your expectations? What surprised you?

2. For your current QA work, which approach would you use more often? Why?

3. How would you explain the trade-off between zero-shot and few-shot to a colleague?

## Bonus Challenge

Try a "graduated" approach:
1. Start with zero-shot
2. If output quality is < 3/5, add one example
3. If still < 4/5, add a second example
4. Document how many examples were needed for each task

This mimics a real-world efficiency strategy: start simple, add complexity only when needed.


