# Exercise: Prompt Engineering Basics

## Overview

| Attribute | Value |
|-----------|-------|
| **Duration** | 45 minutes |
| **Difficulty** | Beginner to Intermediate |
| **Mode** | Individual (Mode A: Implementation) |
| **Prerequisites** | Read prompt-engineering-introduction.md |

## Learning Objectives

By completing this exercise, you will be able to:
- Craft effective prompts using all five components (Context, Instruction, Constraints, Format, Examples)
- Iterate on prompts to improve output quality
- Avoid common prompting mistakes
- Create reusable prompt templates for QA tasks

## The Challenge

You will craft prompts for five different QA scenarios, starting simple and progressively adding complexity. Each scenario tests different prompt engineering skills.

## Scenario 1: Bug Classification (10 minutes)

### The Task

You need to classify bug reports by severity. Create a prompt that consistently produces useful severity classifications.

### Bug Report to Classify

```
Bug ID: BUG-4521
Title: Dashboard charts don't load after browser refresh
Description: After refreshing the analytics dashboard page (F5), all chart 
components show "Loading..." indefinitely. The only way to see charts again 
is to log out and log back in. Console shows "Failed to fetch" errors.
Affected Users: All users accessing the analytics dashboard
Reported: 3 times this week
```

### Your Task

**Step 1:** Write a POOR prompt (intentionally vague):

```
[Write a bad prompt that would give unhelpful results]
```

**Step 2:** Identify what's wrong with your poor prompt:
- [ ] Missing context
- [ ] Vague instruction
- [ ] No constraints
- [ ] No format specified
- [ ] Other: ____________

**Step 3:** Write an IMPROVED prompt using proper structure:

```markdown
## Context
[Provide relevant background]

## Instruction
[Clear, specific task]

## Constraints  
[Limitations and requirements]

## Format
[How to structure the output]
```

**Step 4:** Test your prompt and record the result:

```
[Paste the AI response here]
```

## Scenario 2: Test Data Generation (10 minutes)

### The Task

Generate test data for a user registration form with specific validation rules.

### Requirements

```
Field Requirements:
- Email: Valid format, unique, max 100 characters
- Password: 8-20 characters, must include uppercase, lowercase, number
- Username: 3-20 characters, alphanumeric only, unique
- Age: Must be 18 or older (integer)
- Country: Select from approved list (US, UK, CA, AU)
```

### Your Task

Craft a prompt that generates test data covering:
- 5 valid combinations
- 5 invalid combinations (different validation failures)
- 3 boundary cases

**Your Prompt:**

```markdown
[Write your complete prompt here]
```

**Quality Check - Does your prompt include:**
- [ ] Clear context about the form
- [ ] Specific quantities (5 valid, 5 invalid, 3 boundary)
- [ ] All validation rules from requirements
- [ ] Output format specification (table/JSON/list)
- [ ] Examples of what you want (optional but helpful)

**Record the Result Quality (1-5):** ____

## Scenario 3: Code Review for Testability (10 minutes)

### The Task

Review code and identify testing concerns. This requires a multi-part output.

### Code to Review

```python
def process_order(order_id, user_email, items, payment_info):
    # Get order from database
    order = db.query(f"SELECT * FROM orders WHERE id = {order_id}")
    
    if not order:
        send_email(user_email, "Order not found")
        return None
    
    total = 0
    for item in items:
        product = get_product(item['id'])
        total += product.price * item['quantity']
    
    # Apply discount if applicable
    if total > 100:
        total = total * 0.9
    
    # Process payment
    result = PaymentGateway.charge(payment_info, total)
    
    if result.success:
        db.execute(f"UPDATE orders SET status='paid' WHERE id = {order_id}")
        send_email(user_email, f"Order confirmed! Total: ${total}")
        return {"status": "success", "total": total}
    else:
        return {"status": "failed", "error": result.error}
```

### Your Task

Create a prompt that asks for:
1. Security vulnerabilities
2. Testing challenges
3. Suggested refactoring for testability
4. Recommended test cases

**Your Prompt:**

```markdown
[Write your complete prompt here]
```

**Format Specification Test:** Did you specify how each section should be organized?
- [ ] Yes - clear format for each deliverable
- [ ] Partially - some format guidance
- [ ] No - left it open

## Scenario 4: Multi-Step Analysis (10 minutes)

### The Task

Create a prompt for a complex analysis that requires the AI to work through multiple steps.

### Scenario

You've received this performance test result and need analysis:

```
Load Test Results - Checkout API
================================
Duration: 1 hour
Virtual Users: Ramped from 10 to 500 over 30 minutes, held for 30 minutes

Results:
- Requests: 45,000 total
- Success Rate: 94.2%
- Average Response Time: 850ms
- 95th Percentile: 2,100ms  
- 99th Percentile: 8,500ms
- Errors: 
  - Timeout (30s): 1,200
  - HTTP 500: 890
  - HTTP 429: 520
- Database Connections: Peaked at 95/100
- Memory: Grew from 2GB to 7.8GB (8GB limit)
```

### Your Task

Write a prompt that guides the AI through:
1. Identifying the most critical issues
2. Analyzing root causes
3. Prioritizing fixes
4. Recommending immediate actions

**Your Prompt (include explicit step guidance):**

```markdown
[Write your complete prompt here]
```

**Technique Check:** Which techniques did you use?
- [ ] Explicit step numbering
- [ ] Role specification ("As a performance engineer...")
- [ ] Output structure per step
- [ ] Priority/severity guidance

## Scenario 5: Template Creation (5 minutes)

### The Task

Create a reusable prompt template for a common QA task of your choice.

### Your Template

Choose ONE of these tasks:
- Regression test selection
- Sprint test planning
- Bug triage
- Test environment setup checklist

```markdown
# QA Prompt Template: [Task Name]

## Template

```
## Context
I am a QA engineer working on [PLACEHOLDER: project/feature description].
The technology stack is [PLACEHOLDER: tech stack].

## Task
[PLACEHOLDER: specific task description]

## Requirements
- [PLACEHOLDER: list requirements]

## Constraints
- [PLACEHOLDER: list constraints]

## Output Format
[PLACEHOLDER: specify format]
```

## How to Use
1. [Instructions for filling placeholders]
2. [Tips for customization]

## Example Usage
[A completed example of the template in use]
```

## Deliverables

Submit a document containing:

1. **Scenario 1:** Poor prompt, improved prompt, and AI response
2. **Scenario 2:** Complete test data prompt and quality rating
3. **Scenario 3:** Code review prompt with format specifications
4. **Scenario 4:** Multi-step analysis prompt
5. **Scenario 5:** Reusable template with example

## Definition of Done

- [ ] Completed all 5 scenarios
- [ ] Each prompt includes at least 3 of 5 components (Context, Instruction, Constraints, Format, Examples)
- [ ] Demonstrated prompt iteration in at least one scenario
- [ ] Created one reusable template
- [ ] Self-evaluated using the quality checklist for each scenario

## Evaluation Rubric

| Criteria | Excellent (4) | Good (3) | Satisfactory (2) | Needs Work (1) |
|----------|---------------|----------|------------------|----------------|
| Component Usage | All 5 components used effectively | 4 components consistently | 3 components | Missing key components |
| Specificity | Highly specific, actionable prompts | Good specificity | Some vagueness | Too vague |
| Format Specification | Clear, consistent formatting requests | Mostly clear | Partial | No format guidance |
| Iteration | Shows clear improvement through iteration | Some improvement | Minimal iteration | No iteration shown |
| Template Quality | Complete, reusable, well-documented | Mostly complete | Basic template | Incomplete |

## Common Mistakes to Avoid

Based on the written content, watch out for:

1. **Being too vague** - "Test this" instead of "Generate 5 boundary test cases for the email validation function"

2. **Overloading** - Asking for test cases, automation code, and documentation in one prompt

3. **Assuming context** - "Why is it failing?" without providing the failure details

4. **Ignoring format** - Not specifying how you want the output structured

5. **Not iterating** - Accepting mediocre first results

## Reflection Questions

After completing the exercise, answer:

1. Which component (Context, Instruction, Constraints, Format, Examples) had the biggest impact on output quality?

2. What was your biggest "aha moment" about prompt engineering?

3. How will you apply these techniques in your actual QA work?


