# Querying Metrics with PromQL

## Learning Objectives

- Master PromQL query language fundamentals
- Use selectors and matchers to filter metrics
- Apply functions like rate(), sum(), and avg() effectively
- Work with aggregation operators for data summarization
- Follow PromQL query best practices
- Create basic alerting rules using PromQL

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

PromQL (Prometheus Query Language) is the key to unlocking value from your metrics. It's not enough to collect data—you need to ask the right questions. PromQL lets you calculate rates, percentages, percentiles, and aggregations that reveal system behavior and anomalies.

As a quality engineer, PromQL helps you define precise performance criteria ("P99 latency must be under 500ms"), investigate issues ("show me error rate by endpoint"), and create alerting rules. Every Grafana panel querying Prometheus uses PromQL.

## The Concept

### PromQL Data Types

```
┌─────────────────────────────────────────────────────────────────────┐
│                    PromQL Data Types                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   INSTANT VECTOR                                                     │
│   ──────────────                                                     │
│   Current value of metrics at a point in time                       │
│                                                                      │
│   Query: http_requests_total                                        │
│   Result:                                                            │
│   http_requests_total{method="GET",  status="200"} 1500             │
│   http_requests_total{method="POST", status="200"} 800              │
│   http_requests_total{method="GET",  status="404"} 50               │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   RANGE VECTOR                                                       │
│   ────────────                                                       │
│   Set of time series with values over a time range                  │
│                                                                      │
│   Query: http_requests_total[5m]                                    │
│   Result:                                                            │
│   http_requests_total{method="GET"} 1500 @1.5m ago                  │
│                                      1520 @1.0m ago                  │
│                                      1540 @0.5m ago                  │
│                                      1560 @now                       │
│                                                                      │
│   Used with functions like rate(), avg_over_time()                 │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   SCALAR                                                             │
│   ──────                                                             │
│   Single numeric value (no labels)                                  │
│                                                                      │
│   Query: 42  or  scalar(http_requests_total{status="200"})         │
│   Result: 42                                                         │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   STRING                                                             │
│   ──────                                                             │
│   Rarely used (for special functions)                               │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Selectors and Matchers

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Selectors and Label Matchers                        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   METRIC SELECTOR                                                    │
│   ───────────────                                                    │
│   http_requests_total                    All series for this metric │
│   http_requests_total{method="GET"}      With exact label match    │
│                                                                      │
│   LABEL MATCHERS                                                     │
│   ──────────────                                                     │
│   =     Exact match      {status="200"}                             │
│   !=    Not equal        {status!="500"}                            │
│   =~    Regex match      {status=~"2.."}                            │
│   !~    Regex not match  {path!~"/admin.*"}                         │
│                                                                      │
│   EXAMPLES                                                           │
│   ────────                                                           │
│   # Exact match                                                      │
│   http_requests_total{method="GET", status="200"}                   │
│                                                                      │
│   # Multiple status codes using regex                               │
│   http_requests_total{status=~"5.."}      # All 5xx errors         │
│   http_requests_total{status=~"2..|3.."}  # Success or redirect    │
│                                                                      │
│   # Exclude certain values                                          │
│   http_requests_total{job!="test"}        # Exclude test job       │
│                                                                      │
│   # Combine matchers (AND logic)                                    │
│   http_requests_total{method="GET", status=~"5..", path!="/health"}│
│                                                                      │
│   # Match all labels (empty selector)                              │
│   {__name__=~"http_.*"}                   # All http_* metrics     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Essential Functions

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Essential PromQL Functions                        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   RATE / IRATE                                                       │
│   ────────────                                                       │
│   Per-second rate of increase for counters                          │
│                                                                      │
│   rate(http_requests_total[5m])    # Smoothed rate over 5 min      │
│   irate(http_requests_total[5m])   # Instantaneous rate            │
│                                                                      │
│   rate() is preferred for alerting (more stable)                   │
│   irate() is better for graphs (shows spikes)                      │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   HISTOGRAM_QUANTILE                                                 │
│   ──────────────────                                                 │
│   Calculate percentiles from histogram buckets                      │
│                                                                      │
│   histogram_quantile(0.95, rate(http_duration_seconds_bucket[5m])) │
│                       │                                              │
│                       └── 0.95 = 95th percentile (P95)              │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   INCREASE                                                           │
│   ────────                                                           │
│   Total increase over a time range                                  │
│                                                                      │
│   increase(http_requests_total[1h])   # Requests in last hour      │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   TIME FUNCTIONS                                                     │
│   ──────────────                                                     │
│   avg_over_time(metric[5m])     # Average over time                │
│   max_over_time(metric[5m])     # Maximum over time                │
│   min_over_time(metric[5m])     # Minimum over time                │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   MATH FUNCTIONS                                                     │
│   ──────────────                                                     │
│   abs(metric)         # Absolute value                             │
│   ceil(metric)        # Round up                                   │
│   floor(metric)       # Round down                                 │
│   round(metric, 0.1)  # Round to nearest 0.1                       │
│   clamp_max(metric,100) # Cap at 100                               │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Aggregation Operators

```
┌─────────────────────────────────────────────────────────────────────┐
│                   Aggregation Operators                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Aggregate across label dimensions                                 │
│                                                                      │
│   sum()      Total                                                  │
│   avg()      Average                                                │
│   min()      Minimum                                                │
│   max()      Maximum                                                │
│   count()    Number of series                                       │
│   stddev()   Standard deviation                                     │
│   topk()     Top K series                                          │
│   bottomk()  Bottom K series                                       │
│                                                                      │
│   SYNTAX                                                             │
│   ──────                                                             │
│   <aggr>(<vector>)                    # Aggregate all               │
│   <aggr> by (<label>) (<vector>)      # Group by label             │
│   <aggr> without (<label>) (<vector>) # Exclude label              │
│                                                                      │
│   EXAMPLES                                                           │
│   ────────                                                           │
│                                                                      │
│   # Total requests across all series                               │
│   sum(rate(http_requests_total[5m]))                               │
│                                                                      │
│   # Total requests per method                                       │
│   sum by (method) (rate(http_requests_total[5m]))                  │
│                                                                      │
│   # Total requests per instance (excluding method)                  │
│   sum without (method, status) (rate(http_requests_total[5m]))     │
│                                                                      │
│   # Average CPU per node                                            │
│   avg by (instance) (rate(node_cpu_seconds_total[5m]))             │
│                                                                      │
│   # Top 5 endpoints by request rate                                │
│   topk(5, sum by (path) (rate(http_requests_total[5m])))          │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Binary Operators

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Binary Operators                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ARITHMETIC                                                         │
│   ──────────                                                         │
│   +   Addition           metric_a + metric_b                        │
│   -   Subtraction        metric_a - metric_b                        │
│   *   Multiplication     metric_a * 100                             │
│   /   Division           metric_a / metric_b                        │
│   %   Modulo             metric_a % 10                              │
│   ^   Power              metric_a ^ 2                               │
│                                                                      │
│   COMPARISON                                                         │
│   ──────────                                                         │
│   ==  Equal              metric > 100                               │
│   !=  Not equal          metric != 0                                │
│   >   Greater than       metric > threshold                         │
│   <   Less than          metric < threshold                         │
│   >=  Greater or equal   metric >= 95                               │
│   <=  Less or equal      metric <= 5                                │
│                                                                      │
│   LOGICAL                                                            │
│   ───────                                                            │
│   and     Intersection   metric_a and metric_b                      │
│   or      Union          metric_a or metric_b                       │
│   unless  Complement     metric_a unless metric_b                   │
│                                                                      │
│   VECTOR MATCHING                                                    │
│   ───────────────                                                    │
│   on()      Match specific labels                                   │
│   ignoring() Ignore specific labels                                 │
│                                                                      │
│   # Example: Error rate percentage                                  │
│   sum(rate(http_requests_total{status=~"5.."}[5m]))                │
│   /                                                                  │
│   sum(rate(http_requests_total[5m]))                               │
│   * 100                                                              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Basic Queries

```promql
# Current value of a counter
http_requests_total

# Filter by labels
http_requests_total{method="GET", status="200"}

# Use regex for multiple values
http_requests_total{status=~"2.."}  # All 2xx status codes
http_requests_total{path=~"/api/.*"}  # All /api paths

# Exclude values
http_requests_total{job!="test"}
```

### Rate and Increase

```promql
# Per-second rate over 5 minutes (use for counters)
rate(http_requests_total[5m])

# Total increase over 1 hour
increase(http_requests_total[1h])

# Requests per second by method
sum by (method) (rate(http_requests_total[5m]))

# Compare rate at different percentiles
rate(http_requests_total{status="200"}[5m])
rate(http_requests_total{status="500"}[5m])
```

### Error Rate Calculations

```promql
# Error rate as percentage
sum(rate(http_requests_total{status=~"5.."}[5m])) 
/ 
sum(rate(http_requests_total[5m])) 
* 100

# Error rate by endpoint
sum by (path) (rate(http_requests_total{status=~"5.."}[5m]))
/
sum by (path) (rate(http_requests_total[5m]))
* 100

# Services with error rate > 1%
(
  sum by (service) (rate(http_requests_total{status=~"5.."}[5m]))
  /
  sum by (service) (rate(http_requests_total[5m]))
) > 0.01
```

### Latency Percentiles

```promql
# P50 (median) response time
histogram_quantile(0.50, 
  sum by (le) (rate(http_request_duration_seconds_bucket[5m]))
)

# P95 response time
histogram_quantile(0.95, 
  sum by (le) (rate(http_request_duration_seconds_bucket[5m]))
)

# P99 response time
histogram_quantile(0.99, 
  sum by (le) (rate(http_request_duration_seconds_bucket[5m]))
)

# P95 by endpoint
histogram_quantile(0.95,
  sum by (le, path) (rate(http_request_duration_seconds_bucket[5m]))
)
```

### Resource Utilization

```promql
# CPU utilization percentage
100 - (avg(rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)

# CPU by instance
100 - (avg by (instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)

# Memory utilization
(1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100

# Disk utilization
(1 - (node_filesystem_avail_bytes{mountpoint="/"} / node_filesystem_size_bytes{mountpoint="/"})) * 100
```

### Aggregation Examples

```promql
# Total requests across all instances
sum(rate(http_requests_total[5m]))

# Average CPU per node
avg by (instance) (rate(node_cpu_seconds_total{mode!="idle"}[5m]))

# Top 10 endpoints by traffic
topk(10, sum by (path) (rate(http_requests_total[5m])))

# Count of instances per job
count by (job) (up)

# Percentage of instances that are up
avg(up) * 100
```

### Alert Rule Examples

```yaml
# prometheus-rules.yml
groups:
  - name: application_alerts
    rules:
      - alert: HighErrorRate
        expr: |
          (
            sum(rate(http_requests_total{status=~"5.."}[5m]))
            /
            sum(rate(http_requests_total[5m]))
          ) > 0.05
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
          description: "Error rate is {{ $value | humanizePercentage }}"
      
      - alert: HighLatency
        expr: |
          histogram_quantile(0.99, 
            sum by (le) (rate(http_request_duration_seconds_bucket[5m]))
          ) > 1
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High P99 latency"
          description: "P99 latency is {{ $value | humanizeDuration }}"
      
      - alert: InstanceDown
        expr: up == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Instance {{ $labels.instance }} is down"
      
      - alert: HighCPU
        expr: |
          100 - (avg by (instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100) > 90
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "High CPU on {{ $labels.instance }}"
          description: "CPU usage is {{ $value }}%"
```

### Advanced Queries

```promql
# Rate of change (derivative) - is metric increasing?
deriv(http_requests_total[5m])

# Predict value in 1 hour
predict_linear(node_filesystem_avail_bytes[1h], 3600)

# Standard deviation (detect anomalies)
stddev_over_time(http_request_duration_seconds[1h])

# Absent metric (for alerting on missing data)
absent(up{job="myapp"})

# Group left/right for joining metrics
cpu_usage * on(instance) group_left(app_name) app_metadata
```

## Summary

- **PromQL** is essential for querying Prometheus metrics and creating visualizations/alerts
- **Instant vectors** return current values; **range vectors** (with `[5m]`) are used with functions
- **Selectors** filter metrics: exact match (`=`), regex (`=~`), negation (`!=`, `!~`)
- **rate()** calculates per-second rates for counters; **histogram_quantile()** calculates percentiles
- **Aggregations** (`sum`, `avg`, `max`, `topk`) group and summarize data
- **Binary operators** enable calculations (error rate = errors / total * 100)
- Alert rules use PromQL with `for` duration and severity labels

## Additional Resources

- [PromQL Documentation](https://prometheus.io/docs/prometheus/latest/querying/basics/) - Official query language guide
- [PromQL Cheat Sheet](https://promlabs.com/promql-cheat-sheet/) - Quick reference
- [PromLens](https://promlens.com/) - Interactive PromQL query builder and explainer

