# Model Considerations

## Learning Objectives

- Understand different AI model types and their capabilities
- Choose the right model for specific QA tasks
- Work effectively within context window and token limitations
- Navigate API costs, rate limiting, and performance trade-offs
- Evaluate local vs cloud-hosted model options
- Apply security and privacy best practices for enterprise AI usage

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

The AI landscape is rich with options—GPT-4, Claude, Gemini, LLaMA, and dozens more. Each has different strengths, costs, speed, and limitations. Making the wrong choice means wasted time, budget, or subpar results. Making the right choice means faster test generation, better analysis, and more productive workflows.

As a quality engineer integrating AI into your work, understanding these models isn't about becoming an ML expert—it's about being an informed consumer. Know what you're paying for, what limits apply, and how to protect sensitive information. This module gives you the practical knowledge to select, use, and manage AI models responsibly in professional settings.

## The Concept

### Overview of AI Model Types

Modern Large Language Models (LLMs) vary significantly in their capabilities, costs, and ideal use cases:

```
Model Landscape (2024):
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  PROPRIETARY (API-based)          OPEN SOURCE (Self-hosted option)      │
│  ─────────────────────────        ─────────────────────────────────     │
│                                                                          │
│  OpenAI GPT Series                Meta LLaMA Series                      │
│  ├─ GPT-4o (latest, fast)         ├─ LLaMA 3 (70B, 8B)                   │
│  ├─ GPT-4 Turbo                   └─ Code LLaMA                          │
│  └─ GPT-3.5 Turbo (budget)                                              │
│                                   Mistral AI                             │
│  Anthropic Claude Series          ├─ Mistral Large                       │
│  ├─ Claude 3.5 Sonnet             ├─ Mixtral 8x7B                        │
│  ├─ Claude 3 Opus                 └─ Mistral 7B                          │
│  └─ Claude 3 Haiku (fast)                                               │
│                                   Google (Open)                          │
│  Google Gemini                    └─ Gemma (7B, 2B)                      │
│  ├─ Gemini 1.5 Pro                                                      │
│  ├─ Gemini 1.5 Flash              Microsoft                              │
│  └─ Gemini 1.0 Pro                └─ Phi-3 (3.8B, small but capable)    │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Model Capabilities Comparison

| Capability | GPT-4 | Claude 3.5 | Gemini Pro | LLaMA 3 70B |
|------------|-------|------------|------------|-------------|
| **Reasoning** | Excellent | Excellent | Very Good | Good |
| **Code Generation** | Excellent | Excellent | Very Good | Very Good |
| **Long Context** | 128K tokens | 200K tokens | 1M tokens | 8K tokens |
| **Speed** | Moderate | Fast | Fast | Varies |
| **Cost** | High | Medium | Medium | Free (self-host) |
| **Privacy** | Cloud-based | Cloud-based | Cloud-based | Self-hostable |
| **API Availability** | Excellent | Good | Good | Via providers |

### Choosing the Right Model for QA Tasks

Different tasks benefit from different model characteristics:

```
Task-to-Model Mapping:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  TASK                          RECOMMENDED APPROACH                      │
│  ─────────────────────────     ─────────────────────────────────────    │
│                                                                          │
│  Quick test case brainstorm    Fast model (GPT-3.5, Claude Haiku)       │
│  Complex code analysis         Best reasoning (GPT-4, Claude 3.5)       │
│  Large codebase review         Long context (Gemini, Claude)            │
│  High volume generation        Cost-efficient (GPT-3.5, local models)   │
│  Sensitive code analysis       Local models (LLaMA, Mistral)            │
│  Detailed documentation        Quality-focused (GPT-4, Claude Opus)     │
│  Real-time IDE assistance      Fast response (Claude Haiku, GPT-3.5)    │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

**Decision Framework:**

```markdown
1. What's the task complexity?
   - Simple (test data, basic generation) → Fast/cheap model
   - Complex (analysis, debugging) → Best reasoning model

2. What's the input size?
   - Short prompts → Any model
   - Large code files → Long context model

3. What's the sensitivity level?
   - Public/sample code → Cloud APIs fine
   - Proprietary/sensitive → Consider local models

4. What's the budget constraint?
   - Experimental/learning → Free tiers, cheap models
   - Production workload → Balance cost vs quality
   
5. What's the latency requirement?
   - Batch processing → Quality over speed
   - Interactive use → Speed matters
```

### Understanding Context Windows and Token Limits

#### What is a Context Window?

The context window is the maximum amount of text (measured in tokens) that a model can process in a single interaction. It includes both your prompt AND the model's response.

```
Context Window Visualization:
┌─────────────────────────────────────────────────────────────────────────┐
│                    CONTEXT WINDOW (e.g., 128K tokens)                   │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                                                                    │ │
│  │   YOUR PROMPT                    │   MODEL RESPONSE               │ │
│  │   ────────────                   │   ──────────────               │ │
│  │   • System instructions          │   • Analysis                   │ │
│  │   • Previous conversation        │   • Generated code             │ │
│  │   • Code to analyze              │   • Test cases                 │ │
│  │   • Requirements                 │   • Documentation              │ │
│  │                                  │                                │ │
│  │   ◄────── Input tokens ──────►  │  ◄── Output tokens ───►        │ │
│  │                                  │                                │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                                                          │
│  Total = Input + Output must fit within context window                  │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Token Counts by Model

| Model | Context Window | Approximate Pages of Text |
|-------|---------------|---------------------------|
| GPT-3.5 Turbo | 16K tokens | ~20 pages |
| GPT-4 Turbo | 128K tokens | ~160 pages |
| Claude 3.5 Sonnet | 200K tokens | ~250 pages |
| Gemini 1.5 Pro | 1M tokens | ~1,250 pages |
| LLaMA 3 8B | 8K tokens | ~10 pages |

#### Working Within Token Limits

```python
# Practical tips for managing context

# BAD: Pasting entire large files
prompt = f"Review this code: {entire_10000_line_file}"  # Might exceed limits

# GOOD: Extract relevant sections
prompt = f"""
Review this authentication module (lines 150-300):
{relevant_section}

Focus on: security vulnerabilities and test suggestions
"""

# BAD: Keeping full conversation history forever
messages = [all_50_previous_messages]  # Context fills up

# GOOD: Summarize and trim history
messages = [
    system_message,
    summary_of_earlier_conversation,
    last_3_relevant_messages,
    current_query
]
```

### API Costs and Rate Limiting

#### Understanding Pricing Models

Most API-based models charge per token:

```
Cost Structure Example (illustrative - check current pricing):
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  Model              Input Cost/1K    Output Cost/1K    Notes            │
│  ─────────────────  ─────────────    ──────────────    ─────────────    │
│  GPT-4 Turbo        $0.01            $0.03             Best quality     │
│  GPT-3.5 Turbo      $0.0005          $0.0015           Budget option    │
│  Claude 3.5 Sonnet  $0.003           $0.015            Good balance     │
│  Claude 3 Haiku     $0.00025         $0.00125          Very cheap       │
│  Gemini 1.5 Pro     $0.00125         $0.005            Long context     │
│                                                                          │
│  Example: Generate 50 test cases (2K tokens output)                     │
│  ─────────────────────────────────────────────────────                  │
│  GPT-4 Turbo: 50 × $0.06 = $3.00                                        │
│  GPT-3.5:     50 × $0.003 = $0.15                                       │
│  Claude Haiku: 50 × $0.0025 = $0.125                                    │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Rate Limiting

APIs impose limits on requests per minute (RPM) and tokens per minute (TPM):

```python
# Rate limit handling example
import time
from functools import wraps

def rate_limited(max_per_minute):
    """Decorator to rate limit function calls."""
    min_interval = 60.0 / max_per_minute
    last_called = [0.0]
    
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            elapsed = time.time() - last_called[0]
            wait_time = min_interval - elapsed
            if wait_time > 0:
                time.sleep(wait_time)
            result = func(*args, **kwargs)
            last_called[0] = time.time()
            return result
        return wrapper
    return decorator

@rate_limited(max_per_minute=60)  # 60 requests per minute
def call_ai_api(prompt):
    # Your API call here
    pass
```

#### Cost Optimization Strategies

```markdown
1. **Use cheaper models for simple tasks**
   - Test data generation → GPT-3.5
   - Complex analysis → GPT-4

2. **Minimize unnecessary tokens**
   - Be concise in prompts
   - Don't repeat context needlessly
   - Request specific output format (avoid verbose explanations)

3. **Cache common responses**
   - Store generated test cases for reuse
   - Cache similar queries

4. **Batch similar requests**
   - Generate multiple test cases in one call vs. individual calls

5. **Set output limits**
   - "Generate exactly 5 test cases" vs. "Generate test cases"
```

### Local vs Cloud-Hosted Models

#### Cloud-Hosted (API-Based)

**Advantages:**
- No infrastructure to manage
- Always up-to-date models
- Scalable on demand
- Easy to start

**Disadvantages:**
- Data leaves your network
- Ongoing costs
- Rate limits
- Vendor dependency

#### Self-Hosted (Local)

**Advantages:**
- Complete data privacy
- No usage costs after setup
- No rate limits
- Works offline

**Disadvantages:**
- Hardware requirements (GPU recommended)
- Setup and maintenance complexity
- Potentially lower quality than top-tier cloud models
- Updates require manual intervention

```
Decision Matrix:
┌────────────────────────────────────────────────────────────────────────┐
│                                                                         │
│  SCENARIO                              RECOMMENDATION                   │
│  ─────────────────────────────         ─────────────────────────────   │
│  Learning/experimentation              Cloud APIs (free tiers)          │
│  Production with sensitive code        Local (LLaMA, Mistral)           │
│  High volume batch processing          Local (cost savings)             │
│  Need best possible quality            Cloud (GPT-4, Claude)            │
│  Offline/air-gapped environment        Local only                       │
│  Startup/small team budget             Mix (local for volume)           │
│  Enterprise with security reqs         Local or enterprise APIs         │
│                                                                         │
└────────────────────────────────────────────────────────────────────────┘
```

### Local Model Options

#### Ollama (Recommended for Beginners)

```bash
# Install Ollama
curl -fsSL https://ollama.com/install.sh | sh

# Pull a model
ollama pull llama3

# Run interactively
ollama run llama3

# Use via API
curl http://localhost:11434/api/generate -d '{
  "model": "llama3",
  "prompt": "Generate test cases for a login form"
}'
```

#### LM Studio (GUI-Based)

- Download from: https://lmstudio.ai
- Browse and download models from GUI
- Run models with visual interface
- OpenAI-compatible API endpoint

#### Hardware Requirements

| Model Size | Minimum RAM | Recommended GPU | Quality |
|------------|-------------|-----------------|---------|
| 7B parameters | 8GB | 8GB VRAM | Good for simple tasks |
| 13B parameters | 16GB | 12GB VRAM | Better reasoning |
| 70B parameters | 64GB+ | 48GB+ VRAM | Near cloud quality |

### Model Versioning and Deprecation

AI models are versioned, and older versions get deprecated:

```markdown
**Version Awareness:**

OpenAI: gpt-4-0125-preview → gpt-4-turbo-2024-04-09 → gpt-4o
Claude: claude-2 → claude-3-sonnet → claude-3.5-sonnet

**Best Practices:**
1. Don't hardcode model names in production
2. Use configuration files for model selection
3. Monitor deprecation announcements
4. Test new model versions before switching
5. Keep fallback to stable model versions
```

```python
# Configuration-based model selection
import os

MODEL_CONFIG = {
    "primary": os.getenv("AI_MODEL", "gpt-4-turbo"),
    "fallback": "gpt-3.5-turbo",
    "code_generation": "gpt-4-turbo",
    "quick_tasks": "gpt-3.5-turbo"
}

def get_model_for_task(task_type):
    return MODEL_CONFIG.get(task_type, MODEL_CONFIG["primary"])
```

### Privacy and Data Security Considerations

#### What Happens to Your Data?

```
Data Flow Awareness:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  YOUR PROMPT ──────────► API PROVIDER ──────────► ???                   │
│                                                                          │
│  Questions to ask:                                                       │
│  ─────────────────                                                       │
│  1. Is my data used for training? (Usually not for API, check ToS)      │
│  2. How long is data retained? (Often 30 days for abuse monitoring)     │
│  3. Is data encrypted in transit? (Should be HTTPS)                      │
│  4. Who can access my data? (Provider employees, subprocessors)         │
│  5. Where is data processed? (Data residency for compliance)            │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Enterprise AI Policies

Most organizations have or should have AI usage policies:

```markdown
**Common Policy Elements:**

1. **Approved Tools**
   - List of sanctioned AI services
   - Unapproved tools prohibited

2. **Data Classification**
   - Public data: Any AI tool OK
   - Internal data: Approved tools only
   - Confidential: Local models or none
   - Regulated (PII, PHI): Special controls

3. **Use Cases**
   - Allowed: Code assistance, documentation, learning
   - Restricted: Customer data analysis
   - Prohibited: Security-sensitive code without review

4. **Review Requirements**
   - AI-generated code must be reviewed
   - AI outputs not trusted without verification
   - Attribution required in some contexts
```

#### Practical Security Practices

```python
# Good: Sanitize before sending to AI
def prepare_code_for_review(code):
    """Remove sensitive information before AI analysis."""
    sanitized = code
    
    # Remove hardcoded credentials
    sanitized = re.sub(r'password\s*=\s*["\'].*?["\']', 
                       'password="<REDACTED>"', sanitized)
    
    # Remove API keys
    sanitized = re.sub(r'api_key\s*=\s*["\'].*?["\']',
                       'api_key="<REDACTED>"', sanitized)
    
    # Remove connection strings
    sanitized = re.sub(r'(mongodb|postgresql|mysql):\/\/.*?@',
                       r'\1://<REDACTED>@', sanitized)
    
    return sanitized

# Good: Use environment variables for API keys
import os
api_key = os.getenv("OPENAI_API_KEY")  # Never hardcode

# Good: Log AI usage for audit
def log_ai_interaction(prompt_hash, model, tokens_used):
    """Log AI usage without storing actual prompts."""
    logger.info(f"AI call: model={model}, tokens={tokens_used}, "
                f"prompt_hash={prompt_hash}")
```

## Code Example

```python
"""
Model Selection and Configuration for QA AI Tools
"""

import os
from dataclasses import dataclass
from enum import Enum
from typing import Optional


class TaskType(Enum):
    TEST_GENERATION = "test_generation"
    CODE_ANALYSIS = "code_analysis"
    DOCUMENTATION = "documentation"
    BUG_TRIAGE = "bug_triage"
    QUICK_QUERY = "quick_query"
    SENSITIVE_ANALYSIS = "sensitive_analysis"


@dataclass
class ModelConfig:
    name: str
    provider: str
    context_window: int
    cost_per_1k_input: float
    cost_per_1k_output: float
    is_local: bool = False
    speed: str = "medium"  # fast, medium, slow


# Model registry
MODELS = {
    "gpt-4-turbo": ModelConfig(
        name="gpt-4-turbo",
        provider="openai",
        context_window=128000,
        cost_per_1k_input=0.01,
        cost_per_1k_output=0.03,
        speed="medium"
    ),
    "gpt-3.5-turbo": ModelConfig(
        name="gpt-3.5-turbo",
        provider="openai",
        context_window=16000,
        cost_per_1k_input=0.0005,
        cost_per_1k_output=0.0015,
        speed="fast"
    ),
    "claude-3.5-sonnet": ModelConfig(
        name="claude-3-5-sonnet-20240620",
        provider="anthropic",
        context_window=200000,
        cost_per_1k_input=0.003,
        cost_per_1k_output=0.015,
        speed="fast"
    ),
    "claude-3-haiku": ModelConfig(
        name="claude-3-haiku-20240307",
        provider="anthropic",
        context_window=200000,
        cost_per_1k_input=0.00025,
        cost_per_1k_output=0.00125,
        speed="fast"
    ),
    "llama3-local": ModelConfig(
        name="llama3",
        provider="ollama",
        context_window=8000,
        cost_per_1k_input=0,
        cost_per_1k_output=0,
        is_local=True,
        speed="medium"
    ),
}

# Task-to-model mapping
TASK_MODEL_PREFERENCES = {
    TaskType.TEST_GENERATION: ["gpt-4-turbo", "claude-3.5-sonnet", "gpt-3.5-turbo"],
    TaskType.CODE_ANALYSIS: ["gpt-4-turbo", "claude-3.5-sonnet"],
    TaskType.DOCUMENTATION: ["claude-3.5-sonnet", "gpt-4-turbo"],
    TaskType.BUG_TRIAGE: ["gpt-3.5-turbo", "claude-3-haiku"],
    TaskType.QUICK_QUERY: ["claude-3-haiku", "gpt-3.5-turbo"],
    TaskType.SENSITIVE_ANALYSIS: ["llama3-local"],
}


class ModelSelector:
    """Intelligent model selection based on task requirements."""
    
    def __init__(self, budget_limit: Optional[float] = None, 
                 require_local: bool = False):
        self.budget_limit = budget_limit
        self.require_local = require_local
    
    def select_model(self, task_type: TaskType, 
                     input_size: int = 1000) -> ModelConfig:
        """
        Select the best model for a task.
        
        Args:
            task_type: Type of task to perform
            input_size: Approximate input token count
        
        Returns:
            Selected model configuration
        """
        preferences = TASK_MODEL_PREFERENCES.get(task_type, ["gpt-3.5-turbo"])
        
        for model_name in preferences:
            model = MODELS.get(model_name)
            if not model:
                continue
            
            # Check local requirement
            if self.require_local and not model.is_local:
                continue
            
            # Check context window
            if input_size > model.context_window * 0.7:  # 70% buffer
                continue
            
            # Check budget
            if self.budget_limit:
                estimated_cost = self._estimate_cost(model, input_size)
                if estimated_cost > self.budget_limit:
                    continue
            
            return model
        
        # Fallback
        return MODELS["gpt-3.5-turbo"]
    
    def _estimate_cost(self, model: ModelConfig, input_tokens: int) -> float:
        """Estimate cost for a request."""
        output_tokens = input_tokens  # Rough estimate
        return (
            (input_tokens / 1000) * model.cost_per_1k_input +
            (output_tokens / 1000) * model.cost_per_1k_output
        )
    
    def get_recommendation_report(self, task_type: TaskType, 
                                   input_size: int) -> str:
        """Generate a report explaining model selection."""
        selected = self.select_model(task_type, input_size)
        estimated_cost = self._estimate_cost(selected, input_size)
        
        report = f"""
Model Selection Report
━━━━━━━━━━━━━━━━━━━━━━━
Task Type: {task_type.value}
Input Size: ~{input_size} tokens

Selected Model: {selected.name}
Provider: {selected.provider}
Context Window: {selected.context_window:,} tokens
Speed: {selected.speed}
Local: {'Yes' if selected.is_local else 'No'}

Estimated Cost: ${estimated_cost:.4f}

Alternatives Considered:
"""
        for model_name in TASK_MODEL_PREFERENCES.get(task_type, []):
            model = MODELS.get(model_name)
            if model and model.name != selected.name:
                cost = self._estimate_cost(model, input_size)
                report += f"  - {model.name}: ${cost:.4f}\n"
        
        return report


# Usage example
if __name__ == "__main__":
    selector = ModelSelector(budget_limit=0.10)
    
    # Select model for code analysis
    task = TaskType.CODE_ANALYSIS
    input_tokens = 5000
    
    model = selector.select_model(task, input_tokens)
    print(f"Selected: {model.name}")
    
    # Get detailed report
    report = selector.get_recommendation_report(task, input_tokens)
    print(report)
    
    # Sensitive data handling - force local
    secure_selector = ModelSelector(require_local=True)
    secure_model = secure_selector.select_model(TaskType.SENSITIVE_ANALYSIS)
    print(f"\nFor sensitive data: {secure_model.name} (local={secure_model.is_local})")
```

## Summary

- **Model types vary** in capabilities, cost, speed, and privacy—choose based on task needs
- **Context windows** define how much text can be processed—manage input size accordingly
- **API costs** can add up—use cheaper models for simple tasks, optimize prompts
- **Rate limits** require handling—implement throttling and fallbacks
- **Local models** provide privacy and cost savings but require infrastructure
- **Security matters**—sanitize inputs, follow enterprise policies, protect credentials
- **Version awareness**—models deprecate; use configuration over hardcoding

## Additional Resources

- [OpenAI Platform Documentation](https://platform.openai.com/docs)
- [Anthropic Claude Documentation](https://docs.anthropic.com)
- [Ollama - Local Model Running](https://ollama.com)
- [LM Studio - Desktop LLM Application](https://lmstudio.ai)

