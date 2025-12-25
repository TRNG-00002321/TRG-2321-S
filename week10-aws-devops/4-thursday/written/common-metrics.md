# Common Metrics

## Learning Objectives

- Understand the Four Golden Signals of monitoring (Latency, Traffic, Errors, Saturation)
- Apply the RED method for microservices (Rate, Errors, Duration)
- Apply the USE method for infrastructure (Utilization, Saturation, Errors)
- Distinguish between application metrics and infrastructure metrics
- Follow metric naming conventions and best practices
- Use labels and dimensions effectively

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Not all metrics are equally useful. Collecting the right metrics—and knowing what they mean—is the difference between proactive incident prevention and reactive firefighting. Google's Site Reliability Engineering team developed the Four Golden Signals; these metrics tell you if a service is healthy.

As a quality engineer, understanding metrics helps you define performance acceptance criteria, validate deployments meet SLAs, and collaborate with operations teams on monitoring. When tests pass but users complain, these metrics reveal what's actually happening.

## The Concept

### The Four Golden Signals

```
┌─────────────────────────────────────────────────────────────────────┐
│                  The Four Golden Signals                             │
│                  (Google SRE Methodology)                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   "If you can only measure four metrics of your user-facing         │
│    system, focus on these four."                                    │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                                                              │   │
│   │   LATENCY              │   TRAFFIC                          │   │
│   │   ───────              │   ───────                          │   │
│   │   How long requests    │   How much demand                  │   │
│   │   take to complete     │   the system handles               │   │
│   │                        │                                    │   │
│   │   • Response time      │   • Requests per second           │   │
│   │   • P50, P95, P99      │   • Transactions                  │   │
│   │   • Separate success   │   • Sessions                      │   │
│   │     from error latency │   • Page views                    │   │
│   │                        │                                    │   │
│   ├────────────────────────┼────────────────────────────────────┤   │
│   │                        │                                    │   │
│   │   ERRORS               │   SATURATION                       │   │
│   │   ──────               │   ──────────                       │   │
│   │   Rate of requests     │   How "full" the                  │   │
│   │   that fail            │   service is                      │   │
│   │                        │                                    │   │
│   │   • HTTP 5xx           │   • CPU utilization               │   │
│   │   • Exception rate     │   • Memory usage                  │   │
│   │   • Timeout rate       │   • Queue depth                   │   │
│   │   • Failed operations  │   • Thread pool usage             │   │
│   │                        │                                    │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### RED Method (For Services)

```
┌─────────────────────────────────────────────────────────────────────┐
│                  RED Method for Services                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Simplified version of Golden Signals for request-driven services  │
│                                                                      │
│   R - RATE                                                           │
│   ────────────                                                       │
│   Number of requests per second                                     │
│                                                                      │
│   Metric: http_requests_total                                       │
│   Query:  rate(http_requests_total[5m])                            │
│   Alert:  When rate drops significantly (service may be down)       │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   E - ERRORS                                                         │
│   ──────────                                                         │
│   Number of failed requests per second                              │
│                                                                      │
│   Metric: http_requests_total{status=~"5.."}                       │
│   Query:  rate(http_requests_total{status=~"5.."}[5m])             │
│   Alert:  When error rate exceeds threshold (e.g., > 1%)           │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   D - DURATION                                                       │
│   ────────────                                                       │
│   Time taken per request (latency distribution)                     │
│                                                                      │
│   Metric: http_request_duration_seconds                             │
│   Query:  histogram_quantile(0.99, rate(...[5m]))                  │
│   Alert:  When P99 latency exceeds SLA (e.g., > 500ms)             │
│                                                                      │
│   Example Dashboard Panel:                                          │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Service: api-gateway                                        │   │
│   │  ┌───────────┐  ┌───────────┐  ┌───────────┐               │   │
│   │  │ Rate      │  │ Errors    │  │ Duration  │               │   │
│   │  │ 1.2k/s    │  │ 0.5%      │  │ P99: 120ms│               │   │
│   │  └───────────┘  └───────────┘  └───────────┘               │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### USE Method (For Resources)

```
┌─────────────────────────────────────────────────────────────────────┐
│                  USE Method for Resources                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   For every resource (CPU, memory, disk, network):                  │
│                                                                      │
│   U - UTILIZATION                                                    │
│   ───────────────                                                    │
│   Average time resource was busy                                    │
│                                                                      │
│   Examples:                                                          │
│   • CPU: node_cpu_seconds_total (calculate percentage)             │
│   • Memory: node_memory_MemAvailable_bytes / total                 │
│   • Disk: node_filesystem_avail_bytes / total                      │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   S - SATURATION                                                     │
│   ─────────────                                                      │
│   Degree to which resource has extra work it can't service          │
│   (usually queued work)                                             │
│                                                                      │
│   Examples:                                                          │
│   • CPU: Run queue length                                           │
│   • Memory: Swap usage, OOM kills                                   │
│   • Disk: I/O wait time                                             │
│   • Network: Dropped packets                                        │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   E - ERRORS                                                         │
│   ──────────                                                         │
│   Count of error events                                             │
│                                                                      │
│   Examples:                                                          │
│   • Disk: Read/write errors                                         │
│   • Network: Interface errors, CRC errors                          │
│   • Memory: ECC errors                                              │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Resource    Utilization     Saturation      Errors         │   │
│   │  ────────    ───────────     ──────────      ──────         │   │
│   │  CPU         85%             Queue: 5        0              │   │
│   │  Memory      72%             Swap: 200MB     0 OOM          │   │
│   │  Disk        45%             I/O Wait: 2%    0              │   │
│   │  Network     12%             Drops: 0        0              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Application vs Infrastructure Metrics

```
┌─────────────────────────────────────────────────────────────────────┐
│            Application vs Infrastructure Metrics                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   INFRASTRUCTURE METRICS                                            │
│   ──────────────────────                                            │
│   Collected by: Node Exporter, cAdvisor, CloudWatch                │
│                                                                      │
│   System Resources:                                                  │
│   • CPU usage (user, system, idle)                                  │
│   • Memory (used, available, cached)                                │
│   • Disk (space, I/O operations)                                    │
│   • Network (bytes in/out, packets, errors)                         │
│                                                                      │
│   Container Metrics:                                                 │
│   • Container CPU/memory usage                                      │
│   • Container restarts                                              │
│   • Container network traffic                                       │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   APPLICATION METRICS                                                │
│   ───────────────────                                                │
│   Collected by: Application instrumentation                         │
│                                                                      │
│   Request Metrics:                                                   │
│   • Request rate                                                    │
│   • Error rate                                                       │
│   • Response time (latency)                                         │
│   • Request size                                                    │
│                                                                      │
│   Business Metrics:                                                  │
│   • Orders placed                                                    │
│   • User registrations                                              │
│   • Revenue                                                          │
│   • Active users                                                    │
│                                                                      │
│   Application State:                                                 │
│   • Database connection pool size                                   │
│   • Cache hit/miss ratio                                            │
│   • Queue depth                                                      │
│   • Active sessions                                                  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Metric Naming Conventions

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Metric Naming Conventions                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   PROMETHEUS NAMING BEST PRACTICES                                  │
│   ────────────────────────────────                                  │
│                                                                      │
│   Format: namespace_subsystem_name_unit                             │
│                                                                      │
│   Examples:                                                          │
│   ─────────                                                          │
│   http_requests_total                    # Counter                  │
│   http_request_duration_seconds          # Histogram                │
│   node_memory_MemAvailable_bytes         # Gauge                    │
│   process_cpu_seconds_total              # Counter                  │
│                                                                      │
│   RULES                                                              │
│   ─────                                                              │
│   ✓ Use snake_case                                                  │
│   ✓ Include unit as suffix (_bytes, _seconds, _total)              │
│   ✓ Use _total suffix for counters                                 │
│   ✓ Use base units (seconds not milliseconds, bytes not KB)        │
│   ✓ Make names descriptive                                         │
│                                                                      │
│   ✗ Don't use camelCase                                            │
│   ✗ Don't include labels in metric name                            │
│   ✗ Don't use abbreviations unless well-known                      │
│                                                                      │
│   BAD:                                                               │
│   httpRequests_GET_success              # Labels in name            │
│   request_time_ms                        # Not base unit            │
│   req_cnt                               # Abbreviation              │
│                                                                      │
│   GOOD:                                                              │
│   http_requests_total{method="GET", status="200"}                  │
│   http_request_duration_seconds                                     │
│   http_requests_total                                               │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Labels and Dimensions

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Labels and Dimensions                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Labels add dimensions to metrics for filtering and grouping       │
│                                                                      │
│   EXAMPLE                                                            │
│   ───────                                                            │
│   http_requests_total{method="GET", path="/api/users", status="200"}│
│                       │              │               │               │
│                       └─── Label ────┴─── Label ─────┘               │
│                                                                      │
│   This creates one time series. Different label values create       │
│   different time series:                                            │
│                                                                      │
│   http_requests_total{method="GET",  path="/api/users",  status="200"} = 1500│
│   http_requests_total{method="GET",  path="/api/users",  status="404"} = 50 │
│   http_requests_total{method="POST", path="/api/users",  status="201"} = 300│
│   http_requests_total{method="GET",  path="/api/orders", status="200"} = 800│
│                                                                      │
│   GOOD LABELS                                                        │
│   ───────────                                                        │
│   • method (GET, POST, PUT, DELETE)                                 │
│   • status (200, 404, 500)                                          │
│   • endpoint (/api/users, /api/orders)                              │
│   • instance (server hostname)                                      │
│   • environment (prod, staging, dev)                                │
│                                                                      │
│   BAD LABELS (High Cardinality)                                     │
│   ─────────────────────────────                                     │
│   • user_id (millions of unique values)                            │
│   • request_id (unique per request)                                 │
│   • timestamp (changes constantly)                                  │
│   • email (PII, unique per user)                                   │
│                                                                      │
│   High cardinality = Too many time series = Performance problems    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Four Golden Signals Queries

```promql
# LATENCY - P99 response time
histogram_quantile(0.99, 
  rate(http_request_duration_seconds_bucket[5m])
)

# Separate successful vs error latency
histogram_quantile(0.99, 
  rate(http_request_duration_seconds_bucket{status!~"5.."}[5m])
)

# TRAFFIC - Requests per second
sum(rate(http_requests_total[5m]))

# Traffic by endpoint
sum by (path) (rate(http_requests_total[5m]))

# ERRORS - Error rate percentage
sum(rate(http_requests_total{status=~"5.."}[5m])) 
/ 
sum(rate(http_requests_total[5m])) * 100

# SATURATION - CPU utilization
100 - (avg(rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)

# Memory saturation (available memory percentage)
(node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes) * 100
```

### RED Method Queries

```promql
# Rate - Requests per second per service
sum by (service) (rate(http_requests_total[5m]))

# Errors - Error rate as percentage
(
  sum by (service) (rate(http_requests_total{status=~"5.."}[5m]))
  /
  sum by (service) (rate(http_requests_total[5m]))
) * 100

# Duration - P50, P95, P99 latency
# P50 (median)
histogram_quantile(0.50, 
  sum by (service, le) (rate(http_request_duration_seconds_bucket[5m]))
)

# P95
histogram_quantile(0.95, 
  sum by (service, le) (rate(http_request_duration_seconds_bucket[5m]))
)

# P99
histogram_quantile(0.99, 
  sum by (service, le) (rate(http_request_duration_seconds_bucket[5m]))
)
```

### USE Method Queries

```promql
# CPU Utilization
100 - (avg by (instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)

# CPU Saturation (load average)
node_load1 / count by (instance) (node_cpu_seconds_total{mode="idle"})

# Memory Utilization
(1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100

# Memory Saturation (swap usage)
node_memory_SwapTotal_bytes - node_memory_SwapFree_bytes

# Disk Utilization
(1 - (node_filesystem_avail_bytes / node_filesystem_size_bytes)) * 100

# Disk Saturation (I/O wait)
rate(node_cpu_seconds_total{mode="iowait"}[5m]) * 100

# Network Utilization (bytes per second)
rate(node_network_receive_bytes_total[5m]) + rate(node_network_transmit_bytes_total[5m])

# Network Errors
rate(node_network_receive_errs_total[5m]) + rate(node_network_transmit_errs_total[5m])
```

### Application Metric Instrumentation

```python
# Python application with RED metrics
from prometheus_client import Counter, Histogram, Gauge
import time

# Rate metric (Counter)
REQUEST_COUNT = Counter(
    'app_requests_total',
    'Total requests',
    ['method', 'endpoint', 'status']
)

# Duration metric (Histogram)
REQUEST_LATENCY = Histogram(
    'app_request_duration_seconds',
    'Request latency in seconds',
    ['method', 'endpoint'],
    buckets=[0.005, 0.01, 0.025, 0.05, 0.1, 0.25, 0.5, 1.0, 2.5, 5.0]
)

# Saturation metric (Gauge)
ACTIVE_REQUESTS = Gauge(
    'app_active_requests',
    'Number of active requests'
)

DB_CONNECTIONS = Gauge(
    'app_db_connections_active',
    'Active database connections'
)

# Usage in request handler
def handle_request(method, endpoint):
    ACTIVE_REQUESTS.inc()
    start_time = time.time()
    
    try:
        # Process request
        result = process(endpoint)
        status = "200"
    except NotFoundException:
        status = "404"
    except Exception:
        status = "500"
    finally:
        duration = time.time() - start_time
        REQUEST_COUNT.labels(method=method, endpoint=endpoint, status=status).inc()
        REQUEST_LATENCY.labels(method=method, endpoint=endpoint).observe(duration)
        ACTIVE_REQUESTS.dec()
    
    return result
```

### Alert Rules Based on Metrics

```yaml
# prometheus-rules.yml
groups:
  - name: golden_signals
    rules:
      # High error rate
      - alert: HighErrorRate
        expr: |
          (sum(rate(http_requests_total{status=~"5.."}[5m])) 
           / sum(rate(http_requests_total[5m]))) > 0.05
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "Error rate above 5%"
          description: "Error rate is {{ $value | humanizePercentage }}"
      
      # High latency
      - alert: HighLatency
        expr: |
          histogram_quantile(0.99, 
            rate(http_request_duration_seconds_bucket[5m])) > 1
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "P99 latency above 1 second"
      
      # Low traffic (service might be down)
      - alert: LowTraffic
        expr: sum(rate(http_requests_total[5m])) < 10
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "Unusually low traffic"
```

## Summary

- **Four Golden Signals**: Latency, Traffic, Errors, Saturation - core health indicators
- **RED Method**: Rate, Errors, Duration - simplified for request-driven services
- **USE Method**: Utilization, Saturation, Errors - for infrastructure resources
- **Application metrics** measure request behavior and business logic
- **Infrastructure metrics** measure system resources (CPU, memory, disk, network)
- **Naming conventions**: snake_case, include units, use _total for counters
- **Labels** add dimensions; avoid high cardinality (user_id, request_id)

## Additional Resources

- [Google SRE Book - Monitoring](https://sre.google/sre-book/monitoring-distributed-systems/) - Four Golden Signals explained
- [RED Method](https://www.weave.works/blog/the-red-method-key-metrics-for-microservices-architecture/) - Weaveworks explanation
- [USE Method](https://www.brendangregg.com/usemethod.html) - Brendan Gregg's original article

