# Stress Testing AI Systems

## Learning Objectives

- Understand stress testing concepts specific to AI systems
- Apply load testing techniques to ML inference endpoints
- Measure and analyze latency under various load conditions
- Evaluate throughput and resource consumption
- Handle edge cases gracefully
- Identify failure modes and performance bottlenecks
- Design scalability tests for AI applications

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

Your AI model works great in the lab. It classifies images accurately, responds in milliseconds, and handles all your test cases. Then you deploy it—and the system crashes when 1,000 users hit it simultaneously.

**AI stress testing differs from traditional stress testing** because AI models have unique characteristics:

1. **Higher computational requirements:** Neural networks often need GPUs and consume more memory
2. **Variable processing time:** Complex inputs take longer than simple ones
3. **Cold start problems:** First requests after idle are slower
4. **Batching behavior:** Throughput may differ significantly between batch and real-time

A model that takes 50ms for one request might not scale to 50ms × 100 = 5 seconds for 100 concurrent requests. Or it might—stress testing tells you which.

## The Concept

### Traditional vs. AI Stress Testing

| Aspect | Traditional API | AI Inference Endpoint |
|--------|-----------------|----------------------|
| **Typical latency** | 5-50ms | 50-500ms (CPU) or 10-100ms (GPU) |
| **Resource usage** | CPU, memory | CPU, memory, **GPU**, **VRAM** |
| **Input variability** | Structured, predictable | Highly variable (image sizes, text lengths) |
| **Batching** | Usually individual | Often batched for efficiency |
| **Cold start** | Minor | Major (model loading = seconds) |
| **Failure modes** | OOM, timeouts | OOM, CUDA errors, NaN outputs |

### Key Metrics for AI Stress Testing

**Latency Metrics:**
- **P50 (Median):** What most users experience
- **P95:** Catches 95% of requests—outliers starting to matter
- **P99:** The worst 1%—often reveals problems
- **Max latency:** Absolute worst case
- **Cold start latency:** First request after idle period

**Throughput Metrics:**
- **Requests per second (RPS):** How many can you handle?
- **Successful RPS:** RPS that actually complete successfully
- **Saturation point:** RPS where quality degrades

**Resource Metrics:**
- **GPU utilization:** Are you GPU-bound?
- **GPU memory:** Are you running out of VRAM?
- **CPU utilization:** Are you CPU-bound?
- **RAM usage:** Are you running out of memory?

**Reliability Metrics:**
- **Error rate:** Percentage of failed requests
- **Timeout rate:** Requests that exceed time limit
- **Queue depth:** How many requests are waiting?

### Load Testing Patterns

#### Pattern 1: Constant Load

Send the same number of requests per second throughout the test.

Use when: Validating performance at expected production load.

```
Time:    0s   10s   20s   30s   40s
RPS:    [100]-[100]-[100]-[100]-[100]
```

#### Pattern 2: Ramp-Up

Gradually increase load until performance degrades.

Use when: Finding the breaking point.

```
Time:    0s   10s   20s   30s   40s
RPS:    [10]-[50]-[100]-[200]-[400]
                              ^ Where does it break?
```

#### Pattern 3: Spike

Suddenly increase load, then return to normal.

Use when: Testing recovery from traffic spikes.

```
Time:    0s   10s   20s   30s   40s
RPS:    [50]-[50]-[500]-[50]-[50]
                    ^ Spike!
```

#### Pattern 4: Soak Test

Maintain steady load for extended period.

Use when: Finding memory leaks, slow degradation.

```
Time:    0min   1hr   2hr   3hr   4hr
RPS:    [100]-[100]-[100]-[100]-[100]
              ^ Monitor for degradation
```

### Conducting AI Stress Tests

**Step 1: Define realistic inputs**

Don't just test with one input repeated. AI performance often varies by input complexity:

```python
def generate_test_inputs(n):
    """Generate diverse test inputs."""
    inputs = []
    for _ in range(n):
        # Mix of simple and complex inputs
        text_length = random.choice([10, 50, 200, 1000])
        inputs.append({
            "text": generate_random_text(text_length)
        })
    return inputs
```

**Step 2: Start with baseline**

Measure single-request performance first:
- What's the latency for one request?
- What resources does it consume?

**Step 3: Increase concurrency**

Test 2, 5, 10, 20, 50, 100 concurrent users:
- Does latency scale linearly?
- Where does error rate spike?

**Step 4: Monitor resources**

Watch GPU memory, RAM, CPU throughout.

**Step 5: Find the breaking point**

What happens at the limit?
- Graceful degradation (slower but works)?
- Errors?
- Crashes?

### Simple Stress Test Implementation

```python
"""
Simple AI Endpoint Stress Tester
"""

import time
import requests
from concurrent.futures import ThreadPoolExecutor
from statistics import mean, stdev

def stress_test(endpoint_url, test_inputs, concurrency, timeout=30):
    """
    Run stress test against AI endpoint.
    
    Args:
        endpoint_url: URL to test
        test_inputs: List of input payloads
        concurrency: Number of concurrent requests
        timeout: Request timeout in seconds
    
    Returns:
        Test results summary
    """
    latencies = []
    errors = []
    
    def make_request(payload):
        """Make single request and record result."""
        start = time.time()
        try:
            response = requests.post(
                endpoint_url,
                json=payload,
                timeout=timeout
            )
            elapsed = time.time() - start
            
            if response.status_code == 200:
                return {"success": True, "latency": elapsed}
            else:
                return {"success": False, "error": f"Status {response.status_code}"}
        except requests.Timeout:
            return {"success": False, "error": "Timeout"}
        except Exception as e:
            return {"success": False, "error": str(e)}
    
    # Run concurrent requests
    start_time = time.time()
    with ThreadPoolExecutor(max_workers=concurrency) as executor:
        results = list(executor.map(make_request, test_inputs))
    total_time = time.time() - start_time
    
    # Analyze results
    for r in results:
        if r["success"]:
            latencies.append(r["latency"])
        else:
            errors.append(r["error"])
    
    if latencies:
        latencies.sort()
        p50 = latencies[int(len(latencies) * 0.5)]
        p95 = latencies[int(len(latencies) * 0.95)]
        p99 = latencies[int(len(latencies) * 0.99)]
    else:
        p50 = p95 = p99 = 0
    
    return {
        "total_requests": len(test_inputs),
        "successful": len(latencies),
        "failed": len(errors),
        "success_rate": len(latencies) / len(test_inputs),
        "total_time": total_time,
        "rps": len(test_inputs) / total_time,
        "latency_p50": p50,
        "latency_p95": p95,
        "latency_p99": p99,
        "errors": errors[:5]  # First 5 errors
    }


def ramp_up_test(endpoint_url, payload_generator, max_rps=100, step=10):
    """
    Find breaking point by gradually increasing load.
    """
    results = []
    
    for target_rps in range(step, max_rps + 1, step):
        print(f"Testing at {target_rps} RPS...")
        
        # Generate inputs for 10 seconds at target RPS
        inputs = [payload_generator() for _ in range(target_rps * 10)]
        result = stress_test(endpoint_url, inputs, concurrency=target_rps)
        results.append({"target_rps": target_rps, **result})
        
        # Stop if performance degraded significantly
        if result["success_rate"] < 0.9:
            print(f"Breaking point found around {target_rps} RPS")
            break
        if result["latency_p99"] > 5.0:
            print(f"Latency too high at {target_rps} RPS")
            break
    
    return results
```

### Analyzing Results

**What to look for:**

1. **Linear scaling?** Does latency stay constant as load increases?
2. **Knee in the curve?** At what point does latency spike?
3. **Error rate pattern?** Do errors cluster at certain load levels?
4. **Resource saturation?** Which resource maxes out first?

**Example analysis:**

```
Load Test Results:
RPS 10:  P99=50ms,  Success=100%  → Good
RPS 50:  P99=55ms,  Success=100%  → Still good (scaling linearly)
RPS 100: P99=120ms, Success=100%  → Latency rising
RPS 150: P99=800ms, Success=95%   → Degrading
RPS 200: P99=2s,    Success=70%   → Breaking down

Conclusion: System handles ~100 RPS well.
            Performance degrades above 150 RPS.
            Fail-safe needed above 200 RPS.
```

### Edge Case Testing

Beyond load, test edge cases that might cause failures:

**Malformed inputs:**
- Empty input
- Extremely long input
- Invalid characters
- Wrong data types

**Resource exhaustion:**
- Very large batch
- Many concurrent large images
- Input that requires maximum memory

**Timing issues:**
- Request during model loading
- Request during model update
- Rapid repeated requests

## Summary

- **AI stress testing** has unique challenges: GPU resources, variable latency, cold starts
- **Key metrics:** Latency percentiles (P50, P95, P99), throughput, error rate, resource usage
- **Load patterns:** Constant, ramp-up, spike, soak
- **Testing approach:** Start with baseline, increase concurrency, monitor resources, find breaking point
- **Edge cases:** Test malformed inputs, resource exhaustion, timing issues
- **Analysis:** Look for linear scaling, knee in the curve, error patterns

## Additional Resources

- [Locust (Load Testing Tool)](https://locust.io/) - Python-based load testing
- [k6 (Load Testing)](https://k6.io/) - Modern load testing tool
- [Vegeta (HTTP Load Testing)](https://github.com/tsenart/vegeta) - Constant RPS load tester
