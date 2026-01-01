# Stress Testing AI Systems

## Learning Objectives

- Understand stress testing concepts specific to AI systems
- Apply load testing techniques to ML inference endpoints
- Measure and analyze latency under various load conditions
- Evaluate throughput and identify performance bottlenecks
- Analyze resource consumption patterns for AI workloads
- Test edge case handling under stress
- Conduct failure mode analysis for AI services

## Why This Matters

*"Intelligent Engineering: Harnessing AI for Enhanced Testing and Quality Assurance"*

An AI model that performs flawlessly in development can crumble under production load. Machine learning inference is computationally expensive—a single complex model prediction might consume more resources than hundreds of traditional API calls. When thousands of users hit your AI-powered feature simultaneously, you need to know: Will it scale? How will latency degrade? At what point does it fail?

Building on the performance testing foundations from Week 9 (LoadRunner), this module applies those concepts specifically to AI systems. You'll learn to stress test ML inference endpoints, understand the unique resource demands of AI workloads, and ensure your AI services remain reliable under production pressure.

## The Concept

### AI Stress Testing vs. Traditional Stress Testing

AI systems present unique stress testing challenges:

```
Traditional API Stress Testing:          AI Inference Stress Testing:
──────────────────────────────           ─────────────────────────────

Request: Simple data lookup              Request: Process image/text/audio
Response time: ~5-50ms                   Response time: ~50-500ms+
Resource: CPU, memory, I/O               Resource: GPU, large memory, CPU
Scaling: Horizontal easily               Scaling: GPU-constrained
Variability: Low                         Variability: High (input-dependent)
Predictability: High                     Predictability: Medium
```

### Key Metrics for AI Stress Testing

```
AI Performance Metrics:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                          │
│  LATENCY METRICS                                                        │
│  ───────────────                                                        │
│  • P50 (median): Typical user experience                                │
│  • P95: 95th percentile - catches most users                            │
│  • P99: 99th percentile - worst common case                             │
│  • Max latency: Absolute worst case                                     │
│  • Cold start latency: First request after idle                         │
│                                                                          │
│  THROUGHPUT METRICS                                                     │
│  ─────────────────                                                      │
│  • Requests per second (RPS)                                            │
│  • Inferences per second                                                │
│  • Batch throughput (samples/second)                                    │
│                                                                          │
│  RESOURCE METRICS                                                       │
│  ────────────────                                                       │
│  • GPU utilization %                                                    │
│  • GPU memory usage                                                     │
│  • CPU utilization                                                      │
│  • RAM usage                                                            │
│  • Network I/O                                                          │
│                                                                          │
│  RELIABILITY METRICS                                                    │
│  ──────────────────                                                     │
│  • Error rate under load                                                │
│  • Timeout rate                                                         │
│  • Queue depth                                                          │
│  • Dropped requests                                                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Load Testing ML Inference Endpoints

```python
"""
Load Testing Framework for ML Inference Endpoints
"""

import asyncio
import aiohttp
import time
import statistics
from dataclasses import dataclass
from typing import List, Dict
import numpy as np

@dataclass
class LoadTestResult:
    total_requests: int
    successful_requests: int
    failed_requests: int
    duration_seconds: float
    latencies: List[float]
    errors: List[str]
    
    @property
    def success_rate(self) -> float:
        return self.successful_requests / self.total_requests if self.total_requests > 0 else 0
    
    @property
    def rps(self) -> float:
        return self.total_requests / self.duration_seconds if self.duration_seconds > 0 else 0
    
    @property
    def p50_latency(self) -> float:
        return np.percentile(self.latencies, 50) if self.latencies else 0
    
    @property
    def p95_latency(self) -> float:
        return np.percentile(self.latencies, 95) if self.latencies else 0
    
    @property
    def p99_latency(self) -> float:
        return np.percentile(self.latencies, 99) if self.latencies else 0


class MLInferenceLoadTester:
    """
    Load tester specifically designed for ML inference endpoints.
    """
    
    def __init__(self, endpoint_url: str, headers: Dict = None):
        self.endpoint = endpoint_url
        self.headers = headers or {'Content-Type': 'application/json'}
        self.results = []
    
    async def make_request(self, session: aiohttp.ClientSession, 
                           payload: Dict) -> Dict:
        """Make a single inference request."""
        start_time = time.time()
        try:
            async with session.post(
                self.endpoint, 
                json=payload, 
                headers=self.headers,
                timeout=aiohttp.ClientTimeout(total=30)
            ) as response:
                result = await response.json()
                latency = time.time() - start_time
                return {
                    'success': response.status == 200,
                    'latency': latency,
                    'status': response.status,
                    'result': result
                }
        except asyncio.TimeoutError:
            return {
                'success': False,
                'latency': time.time() - start_time,
                'error': 'Timeout'
            }
        except Exception as e:
            return {
                'success': False,
                'latency': time.time() - start_time,
                'error': str(e)
            }
    
    async def run_concurrent_load(self, payloads: List[Dict], 
                                   concurrency: int) -> LoadTestResult:
        """
        Run load test with specified concurrency.
        
        Args:
            payloads: List of request payloads to send
            concurrency: Number of concurrent requests
        """
        latencies = []
        errors = []
        successful = 0
        failed = 0
        
        start_time = time.time()
        
        async with aiohttp.ClientSession() as session:
            semaphore = asyncio.Semaphore(concurrency)
            
            async def bounded_request(payload):
                async with semaphore:
                    return await self.make_request(session, payload)
            
            tasks = [bounded_request(p) for p in payloads]
            results = await asyncio.gather(*tasks)
            
            for result in results:
                latencies.append(result['latency'])
                if result['success']:
                    successful += 1
                else:
                    failed += 1
                    errors.append(result.get('error', 'Unknown error'))
        
        duration = time.time() - start_time
        
        return LoadTestResult(
            total_requests=len(payloads),
            successful_requests=successful,
            failed_requests=failed,
            duration_seconds=duration,
            latencies=latencies,
            errors=errors
        )
    
    def run_ramp_up_test(self, payload_generator, 
                         start_rps: int, end_rps: int, 
                         step_duration: int = 30) -> List[Dict]:
        """
        Gradually increase load to find breaking point.
        
        Args:
            payload_generator: Function that generates test payloads
            start_rps: Starting requests per second
            end_rps: Maximum requests per second
            step_duration: How long to stay at each level (seconds)
        """
        results = []
        
        for target_rps in range(start_rps, end_rps + 1, 10):
            print(f"Testing at {target_rps} RPS...")
            
            # Generate payloads for this step
            payloads = [payload_generator() for _ in range(target_rps * step_duration)]
            
            # Run test
            result = asyncio.run(
                self.run_concurrent_load(payloads, concurrency=target_rps)
            )
            
            results.append({
                'target_rps': target_rps,
                'actual_rps': result.rps,
                'p50_latency': result.p50_latency,
                'p95_latency': result.p95_latency,
                'p99_latency': result.p99_latency,
                'success_rate': result.success_rate,
                'error_rate': 1 - result.success_rate
            })
            
            # Stop if we're seeing significant degradation
            if result.success_rate < 0.9 or result.p99_latency > 5.0:
                print(f"Performance degraded. Stopping at {target_rps} RPS")
                break
        
        return results


# Test payload generators for different model types
def generate_image_classification_payload():
    """Generate payload for image classification endpoint."""
    # Simulated base64 encoded image
    return {
        'image': 'base64_encoded_image_data_here',
        'model': 'resnet50',
        'top_k': 5
    }

def generate_nlp_payload():
    """Generate payload for NLP endpoint."""
    texts = [
        "This product is amazing and I love it!",
        "The worst experience I've ever had.",
        "It's okay, nothing special.",
    ]
    return {
        'text': np.random.choice(texts),
        'task': 'sentiment_analysis'
    }

def generate_recommendation_payload():
    """Generate payload for recommendation endpoint."""
    return {
        'user_id': f'user_{np.random.randint(1, 10000)}',
        'context': {'time_of_day': 'evening', 'device': 'mobile'},
        'num_recommendations': 10
    }
```

### Latency Analysis Under Load

```python
"""
Latency Analysis for AI Inference
"""

class LatencyAnalyzer:
    """
    Analyze latency patterns in AI inference.
    """
    
    def __init__(self, latencies: List[float]):
        self.latencies = sorted(latencies)
    
    def get_percentiles(self, percentiles: List[int] = None) -> Dict:
        """Calculate latency percentiles."""
        if percentiles is None:
            percentiles = [50, 75, 90, 95, 99, 99.9]
        
        return {
            f'p{p}': np.percentile(self.latencies, p)
            for p in percentiles
        }
    
    def detect_latency_spikes(self, threshold_multiplier: float = 3) -> List[Dict]:
        """
        Detect latency spikes (outliers).
        """
        mean = np.mean(self.latencies)
        std = np.std(self.latencies)
        threshold = mean + threshold_multiplier * std
        
        spikes = [
            {'index': i, 'latency': lat, 'deviation': (lat - mean) / std}
            for i, lat in enumerate(self.latencies)
            if lat > threshold
        ]
        
        return spikes
    
    def analyze_distribution(self) -> Dict:
        """Analyze latency distribution."""
        return {
            'mean': np.mean(self.latencies),
            'median': np.median(self.latencies),
            'std': np.std(self.latencies),
            'min': min(self.latencies),
            'max': max(self.latencies),
            'range': max(self.latencies) - min(self.latencies),
            'coefficient_of_variation': np.std(self.latencies) / np.mean(self.latencies),
            'percentiles': self.get_percentiles()
        }
    
    def calculate_sla_compliance(self, sla_ms: float) -> Dict:
        """
        Calculate SLA compliance.
        
        Args:
            sla_ms: Target latency SLA in milliseconds
        """
        sla_seconds = sla_ms / 1000
        within_sla = sum(1 for lat in self.latencies if lat <= sla_seconds)
        
        return {
            'sla_target_ms': sla_ms,
            'requests_within_sla': within_sla,
            'total_requests': len(self.latencies),
            'compliance_rate': within_sla / len(self.latencies),
            'violations': len(self.latencies) - within_sla
        }
    
    def generate_report(self) -> str:
        """Generate latency analysis report."""
        dist = self.analyze_distribution()
        spikes = self.detect_latency_spikes()
        
        report = f"""
LATENCY ANALYSIS REPORT
═══════════════════════

DISTRIBUTION
────────────
• Mean: {dist['mean']*1000:.1f}ms
• Median: {dist['median']*1000:.1f}ms
• Std Dev: {dist['std']*1000:.1f}ms
• Min: {dist['min']*1000:.1f}ms
• Max: {dist['max']*1000:.1f}ms

PERCENTILES
───────────
"""
        for p, value in dist['percentiles'].items():
            report += f"• {p}: {value*1000:.1f}ms\n"
        
        report += f"""
SPIKES DETECTED
───────────────
• Count: {len(spikes)}
• Percentage: {len(spikes)/len(self.latencies)*100:.2f}%
"""
        
        if spikes:
            report += f"• Worst spike: {max(s['latency'] for s in spikes)*1000:.1f}ms\n"
        
        return report
```

### Resource Consumption Analysis

```python
"""
Resource Monitoring for AI Workloads
"""

import subprocess
import threading
import time
from typing import List, Dict

class ResourceMonitor:
    """
    Monitor resource consumption during AI stress tests.
    """
    
    def __init__(self, sample_interval: float = 1.0):
        self.sample_interval = sample_interval
        self.samples = []
        self._running = False
        self._thread = None
    
    def start(self):
        """Start resource monitoring."""
        self._running = True
        self._thread = threading.Thread(target=self._monitor_loop)
        self._thread.start()
    
    def stop(self):
        """Stop resource monitoring."""
        self._running = False
        if self._thread:
            self._thread.join()
    
    def _monitor_loop(self):
        """Continuous monitoring loop."""
        import psutil
        
        while self._running:
            sample = {
                'timestamp': time.time(),
                'cpu_percent': psutil.cpu_percent(interval=None),
                'memory_percent': psutil.virtual_memory().percent,
                'memory_used_gb': psutil.virtual_memory().used / (1024**3),
            }
            
            # GPU monitoring (if nvidia-smi available)
            gpu_stats = self._get_gpu_stats()
            if gpu_stats:
                sample.update(gpu_stats)
            
            self.samples.append(sample)
            time.sleep(self.sample_interval)
    
    def _get_gpu_stats(self) -> Dict:
        """Get GPU statistics using nvidia-smi."""
        try:
            result = subprocess.run(
                ['nvidia-smi', '--query-gpu=utilization.gpu,memory.used,memory.total',
                 '--format=csv,noheader,nounits'],
                capture_output=True, text=True, timeout=5
            )
            if result.returncode == 0:
                parts = result.stdout.strip().split(',')
                return {
                    'gpu_utilization': float(parts[0]),
                    'gpu_memory_used_mb': float(parts[1]),
                    'gpu_memory_total_mb': float(parts[2]),
                    'gpu_memory_percent': float(parts[1]) / float(parts[2]) * 100
                }
        except (subprocess.TimeoutExpired, FileNotFoundError, IndexError):
            pass
        return {}
    
    def get_summary(self) -> Dict:
        """Get resource usage summary."""
        if not self.samples:
            return {}
        
        summary = {
            'duration_seconds': self.samples[-1]['timestamp'] - self.samples[0]['timestamp'],
            'num_samples': len(self.samples),
            'cpu': {
                'mean': np.mean([s['cpu_percent'] for s in self.samples]),
                'max': max(s['cpu_percent'] for s in self.samples),
                'min': min(s['cpu_percent'] for s in self.samples),
            },
            'memory': {
                'mean_percent': np.mean([s['memory_percent'] for s in self.samples]),
                'max_percent': max(s['memory_percent'] for s in self.samples),
                'max_used_gb': max(s['memory_used_gb'] for s in self.samples),
            }
        }
        
        # Add GPU stats if available
        if 'gpu_utilization' in self.samples[0]:
            summary['gpu'] = {
                'mean_utilization': np.mean([s['gpu_utilization'] for s in self.samples]),
                'max_utilization': max(s['gpu_utilization'] for s in self.samples),
                'max_memory_percent': max(s['gpu_memory_percent'] for s in self.samples),
            }
        
        return summary
```

### Edge Case Handling Under Stress

```python
"""
Edge Case Testing Under Load
"""

def generate_edge_case_payloads() -> List[Dict]:
    """
    Generate edge case payloads to test under load.
    """
    edge_cases = []
    
    # Empty/minimal inputs
    edge_cases.append({'text': '', 'type': 'empty_input'})
    edge_cases.append({'text': 'a', 'type': 'minimal_input'})
    
    # Maximum length inputs
    edge_cases.append({'text': 'word ' * 10000, 'type': 'max_length'})
    
    # Special characters
    edge_cases.append({'text': '!@#$%^&*()' * 100, 'type': 'special_chars'})
    
    # Unicode edge cases
    edge_cases.append({'text': '🔥' * 1000, 'type': 'emoji_heavy'})
    edge_cases.append({'text': '\u0000' * 100, 'type': 'null_chars'})
    
    # Mixed content
    edge_cases.append({
        'text': 'Normal text 日本語 العربية 🎉 <script>alert(1)</script>',
        'type': 'mixed_content'
    })
    
    return edge_cases


class EdgeCaseStressTester:
    """
    Test edge case handling under concurrent load.
    """
    
    def __init__(self, load_tester: MLInferenceLoadTester):
        self.load_tester = load_tester
    
    async def test_edge_cases_under_load(self, 
                                          normal_payloads: List[Dict],
                                          edge_payloads: List[Dict],
                                          concurrency: int) -> Dict:
        """
        Mix edge cases with normal traffic and test.
        """
        # Mix payloads (10% edge cases, 90% normal)
        mixed_payloads = normal_payloads.copy()
        edge_count = len(normal_payloads) // 10
        for i in range(edge_count):
            idx = np.random.randint(0, len(mixed_payloads))
            mixed_payloads.insert(idx, np.random.choice(edge_payloads))
        
        result = await self.load_tester.run_concurrent_load(
            mixed_payloads, concurrency
        )
        
        return {
            'total_requests': result.total_requests,
            'success_rate': result.success_rate,
            'edge_case_count': edge_count,
            'normal_count': len(normal_payloads),
            'errors': result.errors
        }
```

### Failure Mode Analysis

```python
"""
Failure Mode Analysis for AI Services
"""

class FailureModeAnalyzer:
    """
    Analyze failure modes of AI services under stress.
    """
    
    def __init__(self):
        self.failure_patterns = []
    
    def categorize_failures(self, errors: List[str]) -> Dict:
        """Categorize failure types."""
        categories = {
            'timeout': 0,
            'memory': 0,
            'gpu_oom': 0,
            'network': 0,
            'validation': 0,
            'internal': 0,
            'unknown': 0
        }
        
        for error in errors:
            error_lower = error.lower()
            
            if 'timeout' in error_lower:
                categories['timeout'] += 1
            elif 'memory' in error_lower or 'oom' in error_lower:
                if 'gpu' in error_lower or 'cuda' in error_lower:
                    categories['gpu_oom'] += 1
                else:
                    categories['memory'] += 1
            elif 'connection' in error_lower or 'network' in error_lower:
                categories['network'] += 1
            elif 'validation' in error_lower or 'invalid' in error_lower:
                categories['validation'] += 1
            elif '500' in error_lower or 'internal' in error_lower:
                categories['internal'] += 1
            else:
                categories['unknown'] += 1
        
        return categories
    
    def find_breaking_point(self, ramp_results: List[Dict]) -> Dict:
        """
        Analyze ramp-up results to find breaking point.
        """
        breaking_point = None
        degradation_start = None
        
        for i, result in enumerate(ramp_results):
            # Detect degradation start (success rate drops below 99%)
            if degradation_start is None and result['success_rate'] < 0.99:
                degradation_start = result
            
            # Detect breaking point (success rate below 90% or latency > 2s)
            if breaking_point is None:
                if result['success_rate'] < 0.9 or result['p99_latency'] > 2.0:
                    breaking_point = result
        
        return {
            'degradation_start': degradation_start,
            'breaking_point': breaking_point,
            'max_sustainable_rps': degradation_start['target_rps'] if degradation_start else ramp_results[-1]['target_rps'],
            'absolute_max_rps': breaking_point['target_rps'] if breaking_point else ramp_results[-1]['target_rps']
        }
    
    def generate_failure_analysis_report(self, 
                                         errors: List[str],
                                         ramp_results: List[Dict]) -> str:
        """Generate comprehensive failure analysis."""
        categories = self.categorize_failures(errors)
        breaking = self.find_breaking_point(ramp_results)
        
        report = f"""
FAILURE MODE ANALYSIS
═════════════════════

ERROR CATEGORIZATION
────────────────────
Total Errors: {sum(categories.values())}

By Type:
"""
        for category, count in sorted(categories.items(), key=lambda x: -x[1]):
            if count > 0:
                pct = count / sum(categories.values()) * 100
                report += f"  • {category}: {count} ({pct:.1f}%)\n"
        
        report += f"""
CAPACITY ANALYSIS
─────────────────
• Degradation starts at: {breaking['degradation_start']['target_rps'] if breaking['degradation_start'] else 'N/A'} RPS
• Breaking point at: {breaking['breaking_point']['target_rps'] if breaking['breaking_point'] else 'N/A'} RPS
• Max sustainable RPS: {breaking['max_sustainable_rps']}

RECOMMENDATIONS
───────────────
"""
        if categories['timeout'] > categories['memory']:
            report += "• Primary bottleneck: Processing time\n"
            report += "• Consider: Model optimization, batching, caching\n"
        elif categories['memory'] + categories['gpu_oom'] > 0:
            report += "• Primary bottleneck: Memory\n"
            report += "• Consider: Batch size reduction, model quantization, more RAM/VRAM\n"
        
        return report
```

## Code Example

Complete stress testing workflow:

```python
"""
Complete AI Stress Testing Workflow
"""

import asyncio
import json
from datetime import datetime

class AIStressTestSuite:
    """
    Complete stress testing suite for AI services.
    """
    
    def __init__(self, endpoint: str, config: Dict = None):
        self.endpoint = endpoint
        self.config = config or {}
        self.load_tester = MLInferenceLoadTester(endpoint)
        self.resource_monitor = ResourceMonitor()
        self.failure_analyzer = FailureModeAnalyzer()
    
    def run_full_suite(self, payload_generator, 
                       max_rps: int = 100) -> Dict:
        """
        Run complete stress testing suite.
        """
        results = {
            'timestamp': datetime.now().isoformat(),
            'endpoint': self.endpoint,
            'tests': {}
        }
        
        # 1. Baseline test
        print("Running baseline test...")
        baseline = asyncio.run(self._run_baseline_test(payload_generator))
        results['tests']['baseline'] = baseline
        
        # 2. Ramp-up test
        print("Running ramp-up test...")
        self.resource_monitor.start()
        ramp_results = self.load_tester.run_ramp_up_test(
            payload_generator,
            start_rps=10,
            end_rps=max_rps
        )
        self.resource_monitor.stop()
        results['tests']['ramp_up'] = ramp_results
        results['resources'] = self.resource_monitor.get_summary()
        
        # 3. Sustained load test
        print("Running sustained load test...")
        max_sustainable = self._find_sustainable_load(ramp_results)
        sustained = asyncio.run(
            self._run_sustained_test(payload_generator, max_sustainable)
        )
        results['tests']['sustained'] = sustained
        
        # 4. Spike test
        print("Running spike test...")
        spike = asyncio.run(self._run_spike_test(payload_generator, max_sustainable))
        results['tests']['spike'] = spike
        
        # 5. Edge case test
        print("Running edge case test...")
        edge_results = asyncio.run(self._run_edge_case_test())
        results['tests']['edge_cases'] = edge_results
        
        # Analysis
        results['analysis'] = self._analyze_results(results)
        
        return results
    
    async def _run_baseline_test(self, payload_generator) -> Dict:
        """Single request baseline."""
        payloads = [payload_generator() for _ in range(10)]
        result = await self.load_tester.run_concurrent_load(payloads, concurrency=1)
        
        return {
            'latency_p50': result.p50_latency,
            'latency_p95': result.p95_latency,
            'success_rate': result.success_rate
        }
    
    async def _run_sustained_test(self, payload_generator, 
                                   target_rps: int, 
                                   duration: int = 60) -> Dict:
        """Run sustained load for specified duration."""
        payloads = [payload_generator() for _ in range(target_rps * duration)]
        result = await self.load_tester.run_concurrent_load(
            payloads, concurrency=target_rps
        )
        
        analyzer = LatencyAnalyzer(result.latencies)
        
        return {
            'duration': duration,
            'target_rps': target_rps,
            'actual_rps': result.rps,
            'success_rate': result.success_rate,
            'latency_analysis': analyzer.analyze_distribution()
        }
    
    async def _run_spike_test(self, payload_generator, 
                               base_rps: int) -> Dict:
        """Test sudden load spikes."""
        # Normal load
        normal_payloads = [payload_generator() for _ in range(base_rps * 30)]
        
        # Spike (3x normal)
        spike_payloads = [payload_generator() for _ in range(base_rps * 3 * 10)]
        
        # Normal again
        recovery_payloads = [payload_generator() for _ in range(base_rps * 30)]
        
        results = {
            'phases': []
        }
        
        # Run phases
        normal_result = await self.load_tester.run_concurrent_load(
            normal_payloads, concurrency=base_rps
        )
        results['phases'].append({
            'name': 'normal',
            'success_rate': normal_result.success_rate,
            'p95_latency': normal_result.p95_latency
        })
        
        spike_result = await self.load_tester.run_concurrent_load(
            spike_payloads, concurrency=base_rps * 3
        )
        results['phases'].append({
            'name': 'spike',
            'success_rate': spike_result.success_rate,
            'p95_latency': spike_result.p95_latency
        })
        
        recovery_result = await self.load_tester.run_concurrent_load(
            recovery_payloads, concurrency=base_rps
        )
        results['phases'].append({
            'name': 'recovery',
            'success_rate': recovery_result.success_rate,
            'p95_latency': recovery_result.p95_latency
        })
        
        return results
    
    async def _run_edge_case_test(self) -> Dict:
        """Test edge cases under load."""
        edge_payloads = generate_edge_case_payloads()
        result = await self.load_tester.run_concurrent_load(
            edge_payloads, concurrency=5
        )
        
        return {
            'total_edge_cases': len(edge_payloads),
            'success_rate': result.success_rate,
            'errors': result.errors
        }
    
    def _find_sustainable_load(self, ramp_results: List[Dict]) -> int:
        """Find maximum sustainable RPS from ramp results."""
        for result in reversed(ramp_results):
            if result['success_rate'] >= 0.99 and result['p99_latency'] < 1.0:
                return result['target_rps']
        return ramp_results[0]['target_rps']
    
    def _analyze_results(self, results: Dict) -> Dict:
        """Generate analysis from all test results."""
        analysis = {
            'baseline_latency_ms': results['tests']['baseline']['latency_p50'] * 1000,
            'max_sustainable_rps': self._find_sustainable_load(results['tests']['ramp_up']),
            'handles_spikes': all(
                p['success_rate'] > 0.9 
                for p in results['tests']['spike']['phases']
            ),
            'edge_case_robust': results['tests']['edge_cases']['success_rate'] > 0.8,
        }
        
        # Recommendations
        recommendations = []
        
        if analysis['baseline_latency_ms'] > 200:
            recommendations.append("Consider model optimization - baseline latency is high")
        
        if not analysis['handles_spikes']:
            recommendations.append("Implement auto-scaling or request queuing for traffic spikes")
        
        if not analysis['edge_case_robust']:
            recommendations.append("Improve input validation and error handling")
        
        analysis['recommendations'] = recommendations
        
        return analysis
    
    def generate_report(self, results: Dict) -> str:
        """Generate human-readable report."""
        report = f"""
╔══════════════════════════════════════════════════════════════════════════╗
║                    AI STRESS TEST REPORT                                  ║
╠══════════════════════════════════════════════════════════════════════════╣
║  Endpoint: {self.endpoint[:55]:<55} ║
║  Timestamp: {results['timestamp']:<54} ║
╠══════════════════════════════════════════════════════════════════════════╣

BASELINE PERFORMANCE
────────────────────
• P50 Latency: {results['tests']['baseline']['latency_p50']*1000:.1f}ms
• P95 Latency: {results['tests']['baseline']['latency_p95']*1000:.1f}ms

CAPACITY ANALYSIS
─────────────────
• Max Sustainable RPS: {results['analysis']['max_sustainable_rps']}
• Handles Spikes: {'Yes' if results['analysis']['handles_spikes'] else 'No'}
• Edge Case Robust: {'Yes' if results['analysis']['edge_case_robust'] else 'No'}

RESOURCE UTILIZATION (Peak)
───────────────────────────
"""
        if 'resources' in results:
            res = results['resources']
            report += f"• CPU: {res['cpu']['max']:.1f}%\n"
            report += f"• Memory: {res['memory']['max_percent']:.1f}%\n"
            if 'gpu' in res:
                report += f"• GPU: {res['gpu']['max_utilization']:.1f}%\n"
                report += f"• GPU Memory: {res['gpu']['max_memory_percent']:.1f}%\n"
        
        report += """
RECOMMENDATIONS
───────────────
"""
        for rec in results['analysis']['recommendations']:
            report += f"• {rec}\n"
        
        return report


# Usage
if __name__ == "__main__":
    suite = AIStressTestSuite("http://localhost:8000/predict")
    results = suite.run_full_suite(generate_nlp_payload, max_rps=50)
    print(suite.generate_report(results))
```

## Summary

- **AI stress testing** evaluates ML inference performance under production load conditions
- **Key metrics:** Latency percentiles (P50/P95/P99), throughput (RPS), resource utilization
- **Load testing** reveals how latency degrades as concurrent requests increase
- **Resource monitoring** tracks CPU, memory, and GPU utilization during tests
- **Edge case testing** ensures unusual inputs don't cause failures under stress
- **Failure mode analysis** categorizes errors and identifies breaking points
- **Ramp-up testing** finds maximum sustainable capacity

## Additional Resources

- [Locust - Load Testing Tool](https://locust.io/)
- [MLPerf Inference Benchmark](https://mlcommons.org/en/inference-datacenter/)
- [NVIDIA Triton Performance Analyzer](https://github.com/triton-inference-server/server)

