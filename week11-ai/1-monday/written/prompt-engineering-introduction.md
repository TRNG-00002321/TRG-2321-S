# Prompt Engineering Introduction

## Learning Objectives

- Understand what prompt engineering is and why it matters for quality engineers
- Identify the core components of an effective prompt
- Apply prompt iteration and refinement techniques
- Avoid common prompting mistakes
- Create prompt templates for common QA tasks
- Understand token considerations and context windows

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

AI language models are remarkably powerful—but they're also remarkably literal. The difference between a mediocre AI response and a brilliant one often comes down to how you ask the question. This skill, called **prompt engineering**, is rapidly becoming one of the most valuable competencies in technology.

For quality engineers, prompt engineering unlocks the full potential of AI assistants. A well-crafted prompt can generate comprehensive test suites in seconds, analyze complex bug patterns, and produce documentation that would take hours to write manually. A poorly-crafted prompt produces generic, unhelpful, or even misleading output that wastes your time.

This module teaches you to communicate effectively with AI—transforming it from a occasionally useful novelty into a reliable daily tool.

## The Concept

### What is Prompt Engineering?

**Prompt engineering** is the practice of designing and refining inputs to AI language models to elicit accurate, relevant, and useful outputs. Think of it as learning to communicate with a highly capable but very literal assistant who takes your instructions exactly as given.

```
Poor Prompt:                          Engineered Prompt:
┌─────────────────────┐               ┌─────────────────────────────────────┐
│ "Write test cases"  │               │ "Generate 5 test cases for a login  │
│                     │               │  function that validates email and  │
│                     │               │  password. Include positive tests,  │
│                     │               │  negative tests for invalid inputs, │
│                     │               │  and boundary conditions."          │
└──────────┬──────────┘               └──────────┬──────────────────────────┘
           │                                     │
           ▼                                     ▼
┌─────────────────────┐               ┌─────────────────────────────────────┐
│ Vague, generic      │               │ Specific, actionable, well-         │
│ response            │               │ structured test cases               │
└─────────────────────┘               └─────────────────────────────────────┘
```

### The Anatomy of an Effective Prompt

Every effective prompt contains some combination of these components:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        EFFECTIVE PROMPT STRUCTURE                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  1. CONTEXT         What background does the AI need to understand?      │
│     ──────────────────────────────────────────────────────────────────   │
│     "You are helping a QA engineer test an e-commerce checkout..."       │
│                                                                          │
│  2. INSTRUCTION     What specific task should it perform?                │
│     ──────────────────────────────────────────────────────────────────   │
│     "Generate test cases for the payment processing module."             │
│                                                                          │
│  3. CONSTRAINTS     What limitations or requirements apply?              │
│     ──────────────────────────────────────────────────────────────────   │
│     "Focus on credit card payments only. Exclude cryptocurrency."        │
│                                                                          │
│  4. FORMAT          How should the output be structured?                 │
│     ──────────────────────────────────────────────────────────────────   │
│     "Present as a numbered list with Test ID, Steps, Expected Result."   │
│                                                                          │
│  5. EXAMPLES        What does good output look like? (Optional)          │
│     ──────────────────────────────────────────────────────────────────   │
│     "Example: TC-001: Valid Visa payment with $100.00 amount..."         │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Component 1: Context

Context provides the AI with background information to frame its response appropriately.

```markdown
**Without Context:**
"List security vulnerabilities"

**With Context:**
"You are reviewing a Python web application that handles user authentication 
and stores sensitive medical records. The application uses Flask and PostgreSQL.
List potential security vulnerabilities specific to this architecture."
```

Context helps the AI:
- Understand the domain (medical, financial, retail)
- Know the technology stack
- Recognize the sensitivity level required
- Tailor terminology appropriately

#### Component 2: Instruction

The instruction is your core ask—what you want the AI to do. Be specific and use action verbs.

```markdown
**Weak Instructions:**
- "Tell me about testing"
- "Help with test cases"
- "Look at this code"

**Strong Instructions:**
- "Generate 10 boundary test cases for this input field"
- "Analyze this function and identify three potential bugs"
- "Create a test data set with 5 valid and 5 invalid email addresses"
```

#### Component 3: Constraints

Constraints narrow the scope and prevent unwanted outputs.

```markdown
**Unconstrained:**
"Generate test data for a user registration form"
(Might generate dozens of fields you don't need)

**Constrained:**
"Generate test data for a user registration form with these fields only:
- Email (required, valid format)
- Password (required, 8-20 characters)  
- Age (optional, must be 18+)

Generate 5 valid combinations and 5 invalid combinations."
```

Common constraints for QA prompts:
- **Scope:** "Focus only on the login module"
- **Quantity:** "Generate exactly 10 test cases"
- **Priority:** "Focus on high-risk scenarios first"
- **Exclusions:** "Do not include performance testing"
- **Technology:** "Use Python and pytest syntax"

#### Component 4: Format

Specify how you want the output structured for immediate usability.

```markdown
**Without Format Specification:**
The AI might return prose, bullet points, or unstructured text.

**With Format Specification:**
"Format your response as a markdown table with columns:
| Test ID | Description | Input | Expected Output | Priority |"
```

Useful format requests:
- Numbered lists
- Markdown tables
- JSON structure
- Code blocks with language specification
- Bullet point hierarchies

#### Component 5: Examples (Few-Shot Prompting Preview)

Providing examples shows the AI exactly what you want. We'll cover this in depth in the few-shot prompting module.

```markdown
"Generate bug report titles in this format:

Example: [Login] - Password reset link expires immediately instead of after 24 hours
Example: [Checkout] - Tax calculation incorrect for international addresses

Now generate 5 bug report titles for issues found in a search functionality."
```

### Prompt Iteration and Refinement

Prompt engineering is rarely one-and-done. Expect to iterate:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      PROMPT REFINEMENT CYCLE                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│    ┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐     │
│    │ Write    │     │ Submit   │     │ Evaluate │     │ Refine   │     │
│    │ Initial  │────▶│ to AI    │────▶│ Output   │────▶│ Prompt   │──┐  │
│    │ Prompt   │     │          │     │          │     │          │  │  │
│    └──────────┘     └──────────┘     └──────────┘     └──────────┘  │  │
│         ▲                                                           │  │
│         └───────────────────────────────────────────────────────────┘  │
│                                                                          │
│    Iteration 1: Too vague → Add specificity                             │
│    Iteration 2: Wrong format → Specify structure                        │
│    Iteration 3: Missing edge cases → Add constraints                    │
│    Iteration 4: Perfect! → Save as template                             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

**Example Iteration Process:**

```markdown
# Iteration 1 - Initial Attempt
PROMPT: "Create test cases for a calculator"
RESULT: Very generic, covers only basic operations

# Iteration 2 - Added Context
PROMPT: "Create test cases for a scientific calculator that handles 
trigonometric functions, logarithms, and supports memory operations"
RESULT: Better coverage but missing edge cases

# Iteration 3 - Added Constraints
PROMPT: "Create test cases for a scientific calculator that handles 
trigonometric functions, logarithms, and supports memory operations.
Include: boundary values, error handling, precision limits.
Focus on scenarios most likely to contain bugs."
RESULT: Good coverage but unstructured output

# Iteration 4 - Added Format
PROMPT: "Create test cases for a scientific calculator...
[previous context and constraints]
Format each test case as:
- TC-ID: Unique identifier
- Category: (Boundary/Error/Functional/Precision)
- Input: Specific values to enter
- Expected: Exact expected output
- Risk: (High/Medium/Low)"
RESULT: Structured, actionable test cases ready for use
```

### Common Prompting Mistakes

#### Mistake 1: Being Too Vague

```markdown
❌ BAD: "Test this"
✅ GOOD: "Generate functional test cases for this user registration 
        endpoint, focusing on input validation for email and password fields"
```

#### Mistake 2: Overloading with Multiple Tasks

```markdown
❌ BAD: "Create test cases, write the automation code, set up the test 
        environment, and document everything"
✅ GOOD: Break into separate prompts:
        1. "Create test cases for..."
        2. "Based on these test cases, write pytest automation..."
        3. "Create setup documentation for..."
```

#### Mistake 3: Assuming Context

```markdown
❌ BAD: "Why is it failing?" 
        (AI has no idea what "it" refers to)
✅ GOOD: "This pytest test is failing with assertion error. The test 
        checks that user.balance returns 100 after deposit(100), but 
        it returns 0. Here's the code: [code]. Why might this fail?"
```

#### Mistake 4: Ignoring the Output Format

```markdown
❌ BAD: "Give me test data"
        (Returns paragraph of text you need to parse)
✅ GOOD: "Generate test data as a JSON array with objects containing 
        'username', 'email', 'age' fields"
```

#### Mistake 5: Not Iterating

```markdown
❌ BAD: Accept mediocre first response
✅ GOOD: "That's helpful, but please also include negative test cases 
        for invalid inputs and add expected error messages"
```

### Prompt Templates for QA Tasks

#### Template: Test Case Generation

```markdown
## Context
I am testing [application/feature name] which [brief description].
The technology stack is [relevant technologies].

## Task  
Generate [number] test cases for [specific functionality].

## Requirements
- Include positive scenarios (valid inputs, happy paths)
- Include negative scenarios (invalid inputs, error conditions)  
- Include boundary conditions (min/max values, empty states)
- Consider security implications if applicable

## Constraints
- Focus only on [specific scope]
- Do not include [exclusions]
- Prioritize [high-risk areas/critical functionality]

## Output Format
For each test case provide:
| Field | Description |
|-------|-------------|
| ID | TC-XXX |
| Title | Brief description |
| Preconditions | Required state before test |
| Steps | Numbered steps to execute |
| Expected Result | Specific expected outcome |
| Priority | High/Medium/Low |
```

#### Template: Bug Report Analysis

```markdown
## Context
I'm investigating the following bug report:
[Paste bug report details]

## Task
Analyze this bug report and provide:
1. Potential root causes (ranked by likelihood)
2. Suggested investigation steps
3. Similar issues to look for
4. Recommended test cases to prevent regression

## Constraints
- Consider [relevant system context]
- The application uses [technology stack]
```

#### Template: Code Review for Testability

```markdown
## Context
Review the following code from a testing perspective:
```
[Paste code]
```

## Task
Identify:
1. Potential bugs or logic errors
2. Missing input validation
3. Edge cases that need testing
4. Suggestions to improve testability

## Output Format
Organize findings by severity (Critical/Major/Minor)
```

### Token Considerations and Context Windows

Understanding tokens and context windows helps you work within AI limitations.

#### What are Tokens?

Tokens are the units AI models use to process text—roughly 3-4 characters or about 0.75 words in English.

```
"Hello, how are you?" 
≈ 5 tokens

"Generate comprehensive test cases for user authentication"
≈ 8 tokens

A typical prompt + response might use 500-2000 tokens
```

#### What is a Context Window?

The context window is the total amount of text (prompt + response) the AI can consider at once.

```
┌─────────────────────────────────────────────────────────────────┐
│                     CONTEXT WINDOW (e.g., 8K tokens)            │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────────────┐  ┌──────────────────────────────┐    │
│  │    YOUR PROMPT       │  │     AI RESPONSE              │    │
│  │    (~2K tokens)      │  │    (~4K tokens available)    │    │
│  │                      │  │                              │    │
│  │  - Context           │  │   (Space for detailed        │    │
│  │  - Instructions      │  │    test cases, analysis,     │    │
│  │  - Code snippets     │  │    documentation)            │    │
│  │  - Examples          │  │                              │    │
│  └──────────────────────┘  └──────────────────────────────┘    │
│                                                                  │
│  Total: Prompt + Response must fit within context window         │
└─────────────────────────────────────────────────────────────────┘
```

#### Practical Implications

1. **Long code files:** Break them into relevant sections rather than pasting entire files
2. **Conversation history:** The AI might "forget" early parts of long conversations
3. **Detailed outputs:** Request chunked responses for lengthy documentation
4. **Cost consideration:** More tokens = higher API costs (when using paid services)

**Tip for large inputs:**
```markdown
"I have a 2000-line Python file. I'll share the authentication module 
(lines 150-300) which contains the functionality I need test cases for:

[paste relevant section]

Please focus your analysis on this section only."
```

## Code Example

Here's a practical Python script demonstrating prompt construction:

```python
def construct_test_generation_prompt(feature_name, description, tech_stack, 
                                      scope, num_cases=10):
    """
    Constructs a well-engineered prompt for test case generation.
    
    Args:
        feature_name: Name of the feature to test
        description: What the feature does
        tech_stack: Technologies used (e.g., "Python Flask, PostgreSQL")
        scope: Specific focus area
        num_cases: Number of test cases to generate
    
    Returns:
        str: A well-structured prompt
    """
    
    prompt = f"""
## Context
I am a QA engineer testing the {feature_name} feature.
This feature {description}.
Technology stack: {tech_stack}

## Task
Generate {num_cases} comprehensive test cases for {scope}.

## Requirements
Include:
- Positive scenarios (happy paths with valid inputs)
- Negative scenarios (invalid inputs, error conditions)
- Boundary conditions (minimum/maximum values, edge cases)
- Security considerations where applicable

## Output Format
For each test case, provide:

### TC-[NUMBER]: [TITLE]
- **Preconditions:** [Required state before test]
- **Steps:**
  1. [Step 1]
  2. [Step 2]
  ...
- **Expected Result:** [Specific expected outcome]
- **Priority:** [High/Medium/Low]
- **Category:** [Functional/Boundary/Negative/Security]

Begin generating test cases:
"""
    
    return prompt.strip()


# Example usage
prompt = construct_test_generation_prompt(
    feature_name="User Registration",
    description="allows new users to create accounts with email and password",
    tech_stack="React frontend, Node.js backend, MongoDB",
    scope="email validation and password strength requirements",
    num_cases=8
)

print(prompt)
```

## Summary

- **Prompt engineering** is the skill of crafting effective AI inputs for useful outputs
- **Five key components:** Context, Instruction, Constraints, Format, and Examples
- **Iteration is essential**—refine prompts based on output quality
- **Avoid common mistakes:** vagueness, overloading, assuming context, ignoring format
- **Use templates** to maintain consistency across similar tasks
- **Understand tokens and context windows** to work within AI limitations

## Additional Resources

- [OpenAI Prompt Engineering Guide](https://platform.openai.com/docs/guides/prompt-engineering)
- [Anthropic's Prompt Engineering Documentation](https://docs.anthropic.com/claude/docs/prompt-engineering)
- [Google's Introduction to Prompt Design](https://cloud.google.com/vertex-ai/docs/generative-ai/learn/introduction-prompt-design)

