# Exercise: AI Model Selection for QA Tasks

## Overview

| Attribute | Value |
|-----------|-------|
| **Duration** | 45 minutes |
| **Difficulty** | Intermediate |
| **Mode** | Individual (Mode B: Conceptual Analysis) |
| **Prerequisites** | Read model-considerations.md |

## Learning Objectives

By completing this exercise, you will be able to:
- Evaluate multiple AI models for specific QA tasks
- Compare models based on cost, quality, speed, and context limits
- Create a decision framework for model selection
- Recommend optimal models for different QA scenarios

## Background

Your organization is evaluating AI models for integration into QA workflows. You need to assess different options and provide recommendations.

### Available Models (Simulated Specifications)

| Model | Provider | Context Window | Cost (per 1K tokens) | Speed | Strengths |
|-------|----------|----------------|---------------------|-------|-----------|
| GPT-4o | OpenAI | 128K | $0.005 input / $0.015 output | Fast | Reasoning, code |
| GPT-4o-mini | OpenAI | 128K | $0.00015 / $0.0006 | Very Fast | Cost-effective |
| Claude 3.5 Sonnet | Anthropic | 200K | $0.003 / $0.015 | Fast | Long context, analysis |
| Claude 3.5 Haiku | Anthropic | 200K | $0.0008 / $0.004 | Very Fast | Speed, cost |
| Gemini 1.5 Pro | Google | 1M | $0.00125 / $0.005 | Medium | Massive context |
| Llama 3.1 70B | Meta (Local) | 128K | $0 (compute cost) | Varies | Privacy, no API |
| Mistral Large | Mistral | 128K | $0.002 / $0.006 | Fast | European, multilingual |

---

## Part 1: Task Analysis - 15 minutes

Analyze each QA task below and identify the key requirements for model selection.

### Task A: Test Case Generation from Requirements

**Scenario:** Generate test cases from a 50-page requirements document.

**Analysis:**

| Consideration | Requirement | Priority (H/M/L) |
|--------------|-------------|------------------|
| Context window | Must handle ~50 pages | |
| Output quality | Test cases must be comprehensive | |
| Speed | Batch processing overnight OK | |
| Cost | Running 100+ documents/month | |
| Privacy | Requirements may be confidential | |
| Format consistency | Need structured output | |

**Key constraints for this task:**
```
1. [Your analysis]
2. [Your analysis]
3. [Your analysis]
```

---

### Task B: Real-Time Bug Triage Assistant

**Scenario:** Developers paste bug reports into a chatbot for instant severity classification and suggested assignee.

**Analysis:**

| Consideration | Requirement | Priority (H/M/L) |
|--------------|-------------|------------------|
| Context window | Individual bug reports (~500 tokens) | |
| Output quality | Accurate classification | |
| Speed | Must respond in < 2 seconds | |
| Cost | 500+ requests/day | |
| Privacy | Internal bug details | |
| Availability | Business hours critical | |

**Key constraints for this task:**
```
1. [Your analysis]
2. [Your analysis]
3. [Your analysis]
```

---

### Task C: Code Review for Security Vulnerabilities

**Scenario:** Analyze pull requests for security issues before merge.

**Analysis:**

| Consideration | Requirement | Priority (H/M/L) |
|--------------|-------------|------------------|
| Context window | Large PRs may be 10K+ lines | |
| Output quality | Must catch real vulnerabilities | |
| Speed | Within CI/CD pipeline timing | |
| Cost | ~50 PRs/day | |
| Privacy | Proprietary source code | |
| Accuracy | False positives waste developer time | |

**Key constraints for this task:**
```
1. [Your analysis]
2. [Your analysis]
3. [Your analysis]
```

---

### Task D: Test Documentation Generation

**Scenario:** Generate user-friendly test documentation from technical test scripts.

**Analysis:**

| Consideration | Requirement | Priority (H/M/L) |
|--------------|-------------|------------------|
| Context window | Test suites vary (1-100 pages) | |
| Output quality | Clear, readable prose | |
| Speed | Weekly batch processing | |
| Cost | Quarterly documentation refresh | |
| Privacy | Internal processes | |
| Consistency | Match company style guide | |

**Key constraints for this task:**
```
1. [Your analysis]
2. [Your analysis]
3. [Your analysis]
```

---

## Part 2: Model Evaluation Matrix - 15 minutes

For each task, score the models on relevant criteria (1-5 scale).

### Task A: Test Case Generation

| Model | Context | Quality | Cost | Speed | Privacy | Total |
|-------|---------|---------|------|-------|---------|-------|
| GPT-4o | | | | | | |
| GPT-4o-mini | | | | | | |
| Claude 3.5 Sonnet | | | | | | |
| Claude 3.5 Haiku | | | | | | |
| Gemini 1.5 Pro | | | | | | |
| Llama 3.1 70B | | | | | | |
| Mistral Large | | | | | | |

**Recommended Model:** _____________

**Justification:**
```
[Why this model is best for this task]
```

---

### Task B: Bug Triage Assistant

| Model | Context | Quality | Cost | Speed | Privacy | Total |
|-------|---------|---------|------|-------|---------|-------|
| GPT-4o | | | | | | |
| GPT-4o-mini | | | | | | |
| Claude 3.5 Sonnet | | | | | | |
| Claude 3.5 Haiku | | | | | | |
| Gemini 1.5 Pro | | | | | | |
| Llama 3.1 70B | | | | | | |
| Mistral Large | | | | | | |

**Recommended Model:** _____________

**Justification:**
```
[Why this model is best for this task]
```

---

### Task C: Security Code Review

| Model | Context | Quality | Cost | Speed | Privacy | Total |
|-------|---------|---------|------|-------|---------|-------|
| GPT-4o | | | | | | |
| GPT-4o-mini | | | | | | |
| Claude 3.5 Sonnet | | | | | | |
| Claude 3.5 Haiku | | | | | | |
| Gemini 1.5 Pro | | | | | | |
| Llama 3.1 70B | | | | | | |
| Mistral Large | | | | | | |

**Recommended Model:** _____________

**Justification:**
```
[Why this model is best for this task]
```

---

### Task D: Documentation Generation

| Model | Context | Quality | Cost | Speed | Privacy | Total |
|-------|---------|---------|------|-------|---------|-------|
| GPT-4o | | | | | | |
| GPT-4o-mini | | | | | | |
| Claude 3.5 Sonnet | | | | | | |
| Claude 3.5 Haiku | | | | | | |
| Gemini 1.5 Pro | | | | | | |
| Llama 3.1 70B | | | | | | |
| Mistral Large | | | | | | |

**Recommended Model:** _____________

**Justification:**
```
[Why this model is best for this task]
```

---

## Part 3: Cost Analysis - 10 minutes

Calculate estimated monthly costs for each task.

### Assumptions

```
Task A: 100 documents/month, avg 20K input tokens, 5K output tokens each
Task B: 500 requests/day × 22 days, avg 500 input tokens, 200 output each
Task C: 50 PRs/day × 22 days, avg 8K input tokens, 1K output each
Task D: 50 test suites/quarter, avg 15K input tokens, 10K output each
```

### Cost Calculations

**Task A Monthly Cost:**
| Model | Input Cost | Output Cost | Total Monthly |
|-------|------------|-------------|---------------|
| GPT-4o | | | |
| Claude 3.5 Sonnet | | | |
| [Your top pick] | | | |

Show your work:
```
[Calculation for your recommended model]
```

**Task B Monthly Cost:**
| Model | Input Cost | Output Cost | Total Monthly |
|-------|------------|-------------|---------------|
| GPT-4o-mini | | | |
| Claude 3.5 Haiku | | | |
| [Your top pick] | | | |

Show your work:
```
[Calculation for your recommended model]
```

**Combined Monthly Budget:**
```
Task A: $____
Task B: $____
Task C: $____
Task D: $____ (amortized monthly)
────────────────
Total: $____
```

---

## Part 4: Decision Framework - 5 minutes

Create a reusable decision framework for your organization.

### Model Selection Decision Tree

```
┌─────────────────────────────────────────────────────┐
│           QA TASK MODEL SELECTION                   │
├─────────────────────────────────────────────────────┤
│                                                     │
│  Is source code or PHI involved?                    │
│  ├── YES → Consider: [your recommendations]        │
│  └── NO → Proceed to next question                 │
│                                                     │
│  Is real-time response required (< 2s)?            │
│  ├── YES → Consider: [your recommendations]        │
│  └── NO → Proceed to next question                 │
│                                                     │
│  Is context > 100K tokens needed?                  │
│  ├── YES → Consider: [your recommendations]        │
│  └── NO → Proceed to next question                 │
│                                                     │
│  Is cost the primary concern?                      │
│  ├── YES → Consider: [your recommendations]        │
│  └── NO → Consider: [your recommendations]        │
│                                                     │
└─────────────────────────────────────────────────────┘
```

### Quick Reference Table

| If You Need... | Best Choice | Runner-Up |
|----------------|-------------|-----------|
| Maximum context | | |
| Lowest cost | | |
| Fastest response | | |
| Best code analysis | | |
| Privacy compliance | | |
| Balanced performance | | |

---

## Deliverables

1. **Task analysis** for all 4 scenarios
2. **Evaluation matrices** with scores and recommendations
3. **Cost calculations** for key models
4. **Decision framework** for model selection

## Definition of Done

- [ ] Analyzed requirements for all 4 QA tasks
- [ ] Scored at least 5 models per task
- [ ] Provided justified recommendations for each task
- [ ] Calculated costs for top model choices
- [ ] Created reusable decision framework
- [ ] Identified privacy-sensitive scenarios

## Evaluation Rubric

| Criteria | Excellent (4) | Good (3) | Satisfactory (2) | Needs Work (1) |
|----------|---------------|----------|------------------|----------------|
| Task Analysis | Deep understanding of requirements | Good analysis | Basic analysis | Superficial |
| Model Scoring | Evidence-based, consistent scoring | Reasonable scores | Some inconsistency | Random/unjustified |
| Cost Analysis | Accurate calculations with assumptions | Minor errors | Incomplete | Missing/wrong |
| Framework | Actionable, covers edge cases | Useful | Basic | Incomplete |

## Reflection Questions

1. How would your recommendations change if privacy wasn't a concern?

2. What's the trade-off between using one model for everything vs. specialized models per task?

3. How often should you re-evaluate model choices as new models are released?

4. What metrics would you track to validate your model selection over time?

## Real-World Considerations

When implementing in production, also consider:

- **Rate limits:** How many requests per minute does each API allow?
- **Reliability:** What's the uptime SLA? Do you need fallback models?
- **Versioning:** How do you handle model updates/deprecations?
- **Compliance:** Does your industry have regulations about AI usage?
- **Vendor lock-in:** How hard is it to switch models later?


