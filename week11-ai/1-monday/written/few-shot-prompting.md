# Few-Shot Prompting

## Learning Objectives

- Understand what few-shot prompting is and how it leverages in-context learning
- Select effective examples for few-shot prompts
- Apply few-shot prompting for test data generation and defect pattern recognition
- Compare few-shot performance to zero-shot approaches
- Optimize example ordering and formatting for best results
- Create few-shot prompts for common QA tasks

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

While zero-shot prompting gets you quick results, few-shot prompting dramatically improves output quality by teaching the AI through examples. This technique bridges the gap between generic AI responses and outputs that match your team's specific formats, conventions, and standards.

For quality engineers, few-shot prompting is particularly powerful for tasks requiring consistency—generating test cases that follow your template, classifying bugs according to your severity definitions, or creating test data that matches production patterns. The small upfront investment of curating examples pays dividends in output quality and reduced editing time.

## The Concept

### What is Few-Shot Prompting?

**Few-shot prompting** provides the AI with a small number of examples (typically 2-5) demonstrating the task before asking it to perform similar work. The model learns from these examples "in context"—during the prompt itself—and applies that pattern to new inputs.

```
Few-Shot Prompt Structure:
┌─────────────────────────────────────────────────────────────────┐
│  CONTEXT/INSTRUCTION                                            │
│  "Classify bug reports by severity. Use these examples:"        │
├─────────────────────────────────────────────────────────────────┤
│  EXAMPLE 1:                                                     │
│  Bug: "Login page crashes when..."  → Severity: CRITICAL        │
├─────────────────────────────────────────────────────────────────┤
│  EXAMPLE 2:                                                     │
│  Bug: "Button color slightly off..." → Severity: LOW            │
├─────────────────────────────────────────────────────────────────┤
│  EXAMPLE 3:                                                     │
│  Bug: "Search returns wrong results..." → Severity: HIGH        │
├─────────────────────────────────────────────────────────────────┤
│  NEW TASK:                                                      │
│  Bug: "Payment fails for international cards..."  → ???         │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
          Model applies learned pattern to new input
```

### In-Context Learning Explained

In-context learning is the phenomenon where LLMs can learn new tasks from examples provided in the prompt, without any actual training or fine-tuning. The model's weights don't change—it's recognizing and extending patterns from the examples you've given.

```
Traditional ML Training:              In-Context Learning:
┌──────────────────────┐             ┌──────────────────────┐
│   Many examples      │             │   Few examples       │
│   (thousands+)       │             │   (2-5 typically)    │
│         │            │             │         │            │
│         ▼            │             │         ▼            │
│   Update weights     │             │   No weight updates  │
│   (slow, expensive)  │             │   (instant)          │
│         │            │             │         │            │
│         ▼            │             │         ▼            │
│   Trained model      │             │   Same model applies │
│   (specialized)      │             │   pattern from prompt│
└──────────────────────┘             └──────────────────────┘
```

**Why this matters:** You can customize AI behavior for your specific needs without any machine learning expertise—just by providing good examples.

### Selecting Effective Examples

The quality of your examples directly determines output quality. Here's how to select them:

#### Principle 1: Diversity

Cover different scenarios, not variations of the same thing:

```markdown
❌ POOR DIVERSITY (all similar):
Example 1: Test login with valid email and password
Example 2: Test login with correct credentials
Example 3: Test successful authentication

✅ GOOD DIVERSITY (covers range):
Example 1: Test login with valid credentials (positive case)
Example 2: Test login with wrong password (negative case)
Example 3: Test login with SQL injection attempt (security case)
```

#### Principle 2: Representativeness

Examples should represent typical cases, not extreme outliers:

```markdown
❌ OUTLIER EXAMPLES:
Example: A 500-character bug title with every edge case combined

✅ REPRESENTATIVE EXAMPLES:
Example: "[Module] - Clear description of the issue and impact"
```

#### Principle 3: Clarity

Each example should clearly demonstrate the pattern:

```markdown
❌ AMBIGUOUS EXAMPLE:
Input: "error" → Output: "medium"
(Why medium? What's the reasoning?)

✅ CLEAR EXAMPLE:
Input: "Users see error message when submitting form with special characters"
Output: "MEDIUM - Feature impaired for specific input patterns, workaround exists"
```

#### Principle 4: Consistency

All examples should follow the same format and logic:

```markdown
❌ INCONSISTENT FORMAT:
Example 1: TC-001: Valid login test
Example 2: Login Test Case - number 2
Example 3: test_invalid_password()

✅ CONSISTENT FORMAT:
Example 1: TC-001 | Valid Login | email="user@test.com", pass="Valid123!" | Success
Example 2: TC-002 | Invalid Password | email="user@test.com", pass="wrong" | Error 401
Example 3: TC-003 | Empty Email | email="", pass="Valid123!" | Validation Error
```

### Example Ordering and Formatting

Research shows that example order affects output quality:

```
Ordering Strategies:
┌─────────────────────────────────────────────────────────────────┐
│                                                                  │
│  COMPLEXITY ORDER (Simple → Complex):                           │
│  Good for: Learning progression, building understanding          │
│  Example: Basic test → Edge case → Complex scenario             │
│                                                                  │
│  SIMILARITY ORDER (Most similar to task last):                  │
│  Good for: Classification, pattern matching                      │
│  Example: If classifying a security bug, put security example    │
│           closer to the new task                                 │
│                                                                  │
│  CATEGORY COVERAGE ORDER:                                        │
│  Good for: Ensuring all categories are represented              │
│  Example: One positive, one negative, one boundary              │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Formatting Best Practices:**

```markdown
# Clear delimiter between examples
---

# Explicit labels
**Input:** [the input]
**Output:** [the output]

# Consistent structure
Example 1:
  Input: ...
  Output: ...

Example 2:
  Input: ...
  Output: ...

# Now the actual task
New Task:
  Input: ...
  Output: [AI generates this]
```

### Few-Shot for Test Data Generation

Few-shot excels at generating test data that matches specific patterns:

**Example: Generating Realistic User Test Data**

```markdown
PROMPT:
Generate test user data matching our production patterns.

Examples of valid test users:
---
Example 1:
{
  "user_id": "USR-2024-00142",
  "email": "john.smith@acme-corp.com",
  "role": "analyst",
  "department": "Finance",
  "created_date": "2024-01-15",
  "last_login": "2024-03-20",
  "status": "active"
}
---
Example 2:
{
  "user_id": "USR-2024-00287",
  "email": "maria.garcia@techstart.io",
  "role": "admin",
  "department": "IT",
  "created_date": "2024-02-28",
  "last_login": "2024-03-22",
  "status": "active"
}
---
Example 3:
{
  "user_id": "USR-2023-00891",
  "email": "inactive.user@oldcompany.com",
  "role": "viewer",
  "department": "Marketing",
  "created_date": "2023-06-10",
  "last_login": "2023-09-15",
  "status": "inactive"
}
---

Now generate 5 new test users following these patterns. Include:
- 3 active users with different roles
- 1 inactive user
- 1 user with admin role
```

**Why this works better than zero-shot:**
- Model learns your ID format (`USR-YYYY-NNNNN`)
- Model learns email domain patterns
- Model learns the exact field structure
- Model understands your status values
- Model generates consistent department names

### Few-Shot for Defect Pattern Recognition

Few-shot is powerful for teaching AI your team's classification system:

**Example: Bug Category Classification**

```markdown
PROMPT:
Classify bugs into categories based on these examples from our system.

Example 1:
Bug: "Application throws NullPointerException when user profile image is not set"
Category: DATA_HANDLING
Reason: Missing null check for optional data
---

Example 2:
Bug: "Login session doesn't expire after 30 minutes of inactivity as specified"
Category: SECURITY
Reason: Session management not following security requirements
---

Example 3:
Bug: "Dashboard takes 15 seconds to load when user has more than 1000 transactions"
Category: PERFORMANCE
Reason: Scalability issue with data volume
---

Example 4:
Bug: "Submit button text is cut off on German language setting"
Category: LOCALIZATION
Reason: UI not accommodating translated text length
---

Example 5:
Bug: "User receives success message but data is not saved to database"
Category: DATA_INTEGRITY
Reason: Mismatch between UI feedback and actual data persistence
---

Now classify these bugs:

Bug A: "Report export fails silently when date range exceeds 1 year"
Bug B: "Password reset email is sent to old email address after email change"
Bug C: "Price displays as $0.00 when product currency is not USD"
```

### Comparing Few-Shot to Zero-Shot Performance

```
Task                        Zero-Shot        Few-Shot       Improvement
─────────────────────────────────────────────────────────────────────────
Standard test cases         Good             Good           Minimal
Custom format test cases    Poor             Excellent      Significant
Bug classification          Moderate         Excellent      High
Test data generation        Generic          Realistic      Significant
Pattern recognition         Variable         Consistent     High
Domain-specific tasks       Poor             Good           Major
```

**When to upgrade from zero-shot to few-shot:**

| Signal | Action |
|--------|--------|
| Output format inconsistent | Add 2-3 format examples |
| Missing domain-specific cases | Add domain examples |
| Classification not matching your categories | Provide category examples |
| Generic test data | Provide realistic data samples |
| Need higher accuracy | Add more diverse examples |

### Advanced Few-Shot Techniques

#### Technique 1: Negative Examples

Show what NOT to do alongside what TO do:

```markdown
Examples:

✅ CORRECT:
Input: "Button color doesn't match design spec"
Output: LOW - Cosmetic issue, no functional impact

❌ INCORRECT (do not classify this way):
Input: "Button color doesn't match design spec"  
Output: CRITICAL - This overestimates severity for cosmetic issues

✅ CORRECT:
Input: "Users cannot complete checkout process"
Output: CRITICAL - Core business function blocked

Now classify: "Footer links point to wrong pages"
```

#### Technique 2: Chain Examples

Show reasoning progression:

```markdown
Example 1:
Bug: "Memory usage grows unbounded during batch processing"
Analysis: 
  - Symptom: Memory leak
  - Impact: Server crash after extended operation
  - Affected users: All users of batch feature
  - Workaround: Restart service (disruptive)
Classification: HIGH

Example 2:
Bug: "Tooltip appears in wrong position on high-DPI displays"
Analysis:
  - Symptom: UI misalignment
  - Impact: Visual annoyance only
  - Affected users: Users with specific display settings
  - Workaround: None needed, functional unaffected
Classification: LOW

Now analyze and classify: "Search results pagination breaks after page 100"
```

#### Technique 3: Structured Input/Output Examples

Use consistent structure for complex tasks:

```markdown
### Test Case Examples

EXAMPLE 1:
**Feature:** User Registration
**Requirement:** Email must be unique
**Test Case:**
```json
{
  "id": "TC-REG-001",
  "title": "Reject duplicate email registration",
  "preconditions": ["User with email test@example.com exists"],
  "steps": [
    "Navigate to registration page",
    "Enter email: test@example.com",
    "Enter valid password",
    "Click Register"
  ],
  "expected": "Display error: 'Email already registered'",
  "type": "Negative",
  "priority": "High"
}
```

EXAMPLE 2:
**Feature:** User Registration  
**Requirement:** Password minimum 8 characters
**Test Case:**
```json
{
  "id": "TC-REG-002",
  "title": "Accept password with exactly 8 characters",
  "preconditions": [],
  "steps": [
    "Navigate to registration page",
    "Enter unique email",
    "Enter password: Abcd123!",
    "Click Register"
  ],
  "expected": "Registration successful",
  "type": "Boundary",
  "priority": "High"
}
```

Now generate a test case for:
**Feature:** User Registration
**Requirement:** Name field required, max 100 characters
```

## Code Example

Here's a practical implementation of few-shot prompting for QA tasks:

```python
"""
Few-Shot Prompting Implementation for QA Tasks
"""

class FewShotPromptBuilder:
    """
    Builds few-shot prompts with consistent formatting.
    """
    
    def __init__(self, task_description):
        self.task_description = task_description
        self.examples = []
        
    def add_example(self, input_text, output_text, explanation=None):
        """Add an example to the few-shot prompt."""
        example = {
            'input': input_text,
            'output': output_text,
            'explanation': explanation
        }
        self.examples.append(example)
        return self  # Allow chaining
    
    def build(self, new_input):
        """Build the complete few-shot prompt."""
        prompt_parts = [
            f"Task: {self.task_description}\n",
            "Learn from these examples:\n",
            "=" * 50 + "\n"
        ]
        
        for i, example in enumerate(self.examples, 1):
            prompt_parts.append(f"Example {i}:")
            prompt_parts.append(f"Input: {example['input']}")
            if example['explanation']:
                prompt_parts.append(f"Reasoning: {example['explanation']}")
            prompt_parts.append(f"Output: {example['output']}")
            prompt_parts.append("-" * 30)
        
        prompt_parts.extend([
            "\n" + "=" * 50,
            "\nNow apply the same pattern:",
            f"Input: {new_input}",
            "Output:"
        ])
        
        return "\n".join(prompt_parts)


# Example 1: Bug Severity Classification
severity_classifier = FewShotPromptBuilder(
    "Classify bug severity as CRITICAL, HIGH, MEDIUM, or LOW"
)

severity_classifier.add_example(
    input_text="Database connection pool exhausted during peak hours",
    output_text="CRITICAL",
    explanation="System-wide impact, service degradation for all users"
)

severity_classifier.add_example(
    input_text="User avatar image doesn't resize correctly on mobile",
    output_text="LOW",
    explanation="Cosmetic issue, no functional impact"
)

severity_classifier.add_example(
    input_text="Password reset email takes 10 minutes to arrive instead of 2",
    output_text="MEDIUM",
    explanation="Feature degraded but functional, workaround is to wait"
)

# Generate classification for new bug
new_bug = "Checkout process fails when cart contains more than 50 items"
classification_prompt = severity_classifier.build(new_bug)
print(classification_prompt)


# Example 2: Test Case Generation with Format
test_case_generator = FewShotPromptBuilder(
    "Generate a test case in our standard format for the given requirement"
)

test_case_generator.add_example(
    input_text="REQ: Login should lock account after 5 failed attempts",
    output_text="""
TC-001: Account Lockout After Failed Attempts
Preconditions: Valid user account exists
Steps:
  1. Navigate to login page
  2. Enter valid username
  3. Enter incorrect password
  4. Repeat steps 2-3 five times
Expected: Account locked, "Account locked" message displayed
Priority: High | Type: Security | Automation: Candidate
""".strip()
)

test_case_generator.add_example(
    input_text="REQ: Search should return results within 2 seconds",
    output_text="""
TC-002: Search Response Time Validation
Preconditions: Database contains test products
Steps:
  1. Navigate to search page
  2. Enter search term "laptop"
  3. Click search button
  4. Measure time until results display
Expected: Results displayed within 2 seconds
Priority: Medium | Type: Performance | Automation: Required
""".strip()
)

# Generate test case for new requirement
new_req = "REQ: User profile photo upload must accept JPEG and PNG only"
test_case_prompt = test_case_generator.build(new_req)
print("\n" + "=" * 60 + "\n")
print(test_case_prompt)


# Example 3: Test Data Pattern Generator
def create_test_data_prompt(examples, field_definitions, count=5):
    """
    Create a few-shot prompt for generating test data.
    
    Args:
        examples: List of example data dictionaries
        field_definitions: Description of required fields
        count: Number of records to generate
    """
    import json
    
    prompt = f"""
Generate test data following the patterns shown in these examples.

Field Definitions:
{field_definitions}

Examples from our production patterns:
"""
    
    for i, example in enumerate(examples, 1):
        prompt += f"\nExample {i}:\n"
        prompt += json.dumps(example, indent=2)
        prompt += "\n---"
    
    prompt += f"""

Now generate {count} new test records following these exact patterns.
Ensure variety in values while maintaining format consistency.
Output as a JSON array.
"""
    return prompt


# Usage
test_data_examples = [
    {
        "order_id": "ORD-2024-00142",
        "customer_id": "CUST-10042",
        "product_code": "PROD-LAPTOP-001",
        "quantity": 1,
        "unit_price": 999.99,
        "order_date": "2024-03-15",
        "status": "completed"
    },
    {
        "order_id": "ORD-2024-00143",
        "customer_id": "CUST-10099",
        "product_code": "PROD-MOUSE-003",
        "quantity": 5,
        "unit_price": 29.99,
        "order_date": "2024-03-16",
        "status": "pending"
    }
]

field_defs = """
- order_id: Format ORD-YYYY-NNNNN
- customer_id: Format CUST-NNNNN  
- product_code: Format PROD-CATEGORY-NNN
- quantity: Integer 1-100
- unit_price: Decimal with 2 places
- order_date: ISO format date in 2024
- status: One of [pending, processing, completed, cancelled]
"""

data_prompt = create_test_data_prompt(test_data_examples, field_defs, count=5)
print("\n" + "=" * 60 + "\n")
print(data_prompt)
```

## Summary

- **Few-shot prompting** provides examples to guide AI output quality and format
- **In-context learning** allows models to learn patterns without actual training
- **Example selection** should prioritize diversity, representativeness, clarity, and consistency
- **Order matters:** Consider complexity progression and similarity to the task
- **Outperforms zero-shot** for custom formats, domain-specific tasks, and classification
- **Advanced techniques** include negative examples, chain examples, and structured I/O

## Additional Resources

- [Few-Shot Learning in NLP - Papers With Code](https://paperswithcode.com/task/few-shot-learning)
- [Prompting Guide - Few-Shot Prompting](https://www.promptingguide.ai/techniques/fewshot)
- [Google Research Blog - Few-Shot Learning](https://ai.googleblog.com/2022/04/pathways-language-model-palm-scaling-to.html)

