# LLM Prompt Engineering Guide

## 1. Be Specific and Clear

Vague prompts get vague results. Define exactly what you want: format, length, audience, tone, and scope.

**Bad**: "Write about climate change"
**Good**: "Write a 300-word explanation of carbon capture technology for high school students. Use simple analogies and avoid jargon."

## 2. Provide Context

Give the model relevant background information. Explain the purpose, constraints, and any specific requirements.

```
Context: I'm preparing a presentation for executives.
Task: Summarize these quarterly sales figures.
Format: 3 bullet points, focus on trends not details.
```

## 3. Use Examples (Few-Shot Learning)

Show 2-3 examples of what you want. The model will match the pattern.

```
Convert these to formal tone:
"hey can u send that?" → "Could you please send that document?"
"thx for the help" → "Thank you for your assistance."
"lemme know asap" → [model completes the pattern]
```

## 4. Break Down Complex Tasks

Don't ask for everything at once. Use step-by-step instructions or chain multiple prompts.

```
Step 1: List the main arguments in this article.
Step 2: Identify weaknesses in each argument.
Step 3: Suggest counterarguments.
```

## 5. Set Constraints

Define boundaries clearly: what to include, what to avoid, word limits, sources to use.

```
Requirements:
- Use only information from the provided documents
- Maximum 200 words
- Cite specific page numbers
- Do not speculate beyond the given data
```

## 6. Use Structured Formats

Delimiters and formatting help the model parse your request correctly.

```
###Instructions###
Analyze the following text for sentiment.

###Text###
[paste text here]

###Output Format###
Sentiment: [positive/negative/neutral]
Confidence: [0-100%]
Key phrases: [list]
```

## 7. Request Reasoning

Ask the model to show its work. This often improves output quality.

```
Before answering, think through this step-by-step:
1. What is being asked?
2. What information is relevant?
3. What is the logical approach?
Then provide your answer.
```

## 8. Iterate and Refine

Your first prompt rarely works perfectly. Review outputs, identify issues, and adjust your prompt accordingly.

## Common Mistakes to Avoid

- Assuming the model "knows" what you mean
- Asking leading questions that bias the output
- Trusting outputs without verification
- Mixing multiple unrelated tasks in one prompt
- Forgetting that the model has no real-world knowledge or verification ability

## Remember

The model is a pattern-matching system. It generates plausible text based on training data, not truth or reasoning. Your prompt quality directly determines output quality.