# Exercise 5: PromQL Practice (Pair Programming)

## Objective

Master PromQL (Prometheus Query Language) by writing queries for common monitoring scenarios and creating basic alerting rules.

---

## Learning Outcomes

By completing this exercise, you will:
- Write PromQL queries using selectors and matchers
- Apply functions like `rate()`, `sum()`, `avg()`
- Use aggregation operators
- Create alerting rules
- Understand query optimization

---

## Prerequisites

- Completed Exercises 2-4 (Prometheus and Grafana running)
- Access to Prometheus UI at http://localhost:9090
- Partner for pair programming

---

## Time Estimate

30 minutes (Pair Programming)

---

## Pair Programming Roles

- **Driver:** Types queries, tests in Prometheus
- **Navigator:** Reviews query logic, suggests improvements

**Switch roles every 10 minutes!**

---

## PromQL Fundamentals

### Data Types
- **Instant Vector:** Single value per time series at a point in time
- **Range Vector:** Multiple values per time series over time range
- **Scalar:** Simple numeric value
- **String:** Simple string (rarely used)

### Basic Syntax
```promql
metric_name{label="value"}[time_range]
```

---

## Tasks

### Task 1: Basic Selectors (5 minutes)

Execute these queries in Prometheus UI (http://localhost:9090):

1. **Simple Metric**
   ```promql
   up
   ```
   Returns all `up` metrics.

2. **Label Selector**
   ```promql
   up{job="prometheus"}
   ```
   Filter by job label.

3. **Regex Matching**
   ```promql
   up{job=~".*exporter"}
   ```
   Match jobs ending in "exporter".

4. **Negative Matching**
   ```promql
   up{job!="prometheus"}
   ```
   Exclude prometheus job.

5. **Multiple Labels**
   ```promql
   prometheus_http_requests_total{handler="/metrics", code="200"}
   ```

**Checkpoint:** Basic selectors understood ✓

---

### Task 2: Range Vectors and Rate (10 minutes)

**Switch roles!**

1. **Range Vector**
   ```promql
   prometheus_http_requests_total[5m]
   ```
   Returns values over last 5 minutes. (Can only view in Graph)

2. **Rate Function (Most Important!)**
   ```promql
   rate(prometheus_http_requests_total[5m])
   ```
   Per-second rate of increase over 5 minutes.

3. **Summing Rates**
   ```promql
   sum(rate(prometheus_http_requests_total[5m]))
   ```
   Total request rate across all handlers.

4. **Group By Label**
   ```promql
   sum by (handler) (rate(prometheus_http_requests_total[5m]))
   ```
   Request rate grouped by handler.

5. **Group By Multiple Labels**
   ```promql
   sum by (handler, code) (rate(prometheus_http_requests_total[5m]))
   ```

6. **Compare Queries**
   
   Discuss with partner:
   - When would you use `rate()` vs `increase()`?
   - Why is 5m a common range?

**Checkpoint:** Rate and aggregation understood ✓

---

### Task 3: Aggregation Functions (5 minutes)

**Switch roles!**

1. **Average**
   ```promql
   avg(prometheus_target_interval_length_seconds)
   ```

2. **Max/Min**
   ```promql
   max(prometheus_target_interval_length_seconds)
   min(prometheus_target_interval_length_seconds)
   ```

3. **Count**
   ```promql
   count(up)
   ```
   Count of time series.

4. **Count by Label**
   ```promql
   count by (job) (up)
   ```

5. **Top K**
   ```promql
   topk(5, prometheus_http_requests_total)
   ```
   Top 5 by value.

6. **Bottom K**
   ```promql
   bottomk(3, prometheus_target_interval_length_seconds)
   ```

---

### Task 4: Practical Monitoring Queries (10 minutes)

Write queries for real-world scenarios:

1. **Query: Error Rate Percentage**
   
   Calculate percentage of requests returning 5xx:
   ```promql
   sum(rate(prometheus_http_requests_total{code=~"5.."}[5m])) 
   / 
   sum(rate(prometheus_http_requests_total[5m])) 
   * 100
   ```
   
   **Discuss:** What threshold would trigger an alert?

2. **Query: Request Latency (if histogram available)**
   ```promql
   histogram_quantile(0.95, 
     sum by (le) (rate(prometheus_http_request_duration_seconds_bucket[5m]))
   )
   ```
   95th percentile latency.

3. **Query: Service Availability**
   ```promql
   avg_over_time(up{job="prometheus"}[1h]) * 100
   ```
   Availability percentage over last hour.

4. **Query: Memory Usage Percentage** (with node-exporter)
   ```promql
   100 - ((node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes) * 100)
   ```

5. **Query: Targets Not Responding**
   ```promql
   up == 0
   ```
   Returns only targets that are down.

6. **Query: Recent Changes**
   ```promql
   changes(prometheus_build_info[1h])
   ```
   Number of times Prometheus restarted.

---

### Task 5: Create Alert Rules (5 minutes)

Create a file `prometheus/alert_rules.yml`:

```yaml
groups:
  - name: week10_alerts
    rules:
      # Alert: Target is down
      - alert: TargetDown
        expr: up == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Target {{ $labels.instance }} is down"
          description: "{{ $labels.job }} target {{ $labels.instance }} has been down for more than 1 minute."

      # Alert: High HTTP Error Rate
      - alert: HighErrorRate
        expr: |
          sum(rate(prometheus_http_requests_total{code=~"5.."}[5m])) 
          / 
          sum(rate(prometheus_http_requests_total[5m])) 
          > 0.05
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High HTTP error rate detected"
          description: "Error rate is {{ $value | humanizePercentage }} over the last 5 minutes"

      # Alert: Slow Scrape
      - alert: SlowScrape  
        expr: prometheus_target_interval_length_seconds{quantile="0.99"} > 30
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Slow scrape on {{ $labels.job }}"
          description: "Scrape is taking {{ $value }}s (99th percentile)"

      # Alert: Prometheus Restarted
      - alert: PrometheusRestarted
        expr: changes(prometheus_build_info[10m]) > 0
        labels:
          severity: info
        annotations:
          summary: "Prometheus has restarted"
```

**Update prometheus.yml to include rules:**

Add to the `rule_files` section:
```yaml
rule_files:
  - "alert_rules.yml"
```

**Mount the rules file** (update docker-compose.yml):
```yaml
volumes:
  - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml:ro
  - ./prometheus/alert_rules.yml:/etc/prometheus/alert_rules.yml:ro
```

**Restart Prometheus:**
```bash
docker compose restart prometheus
```

**View Alerts:**
- Open: http://localhost:9090/alerts

---

## Verification Checklist

- [ ] Executed basic selector queries
- [ ] Used rate() function correctly
- [ ] Applied aggregation functions
- [ ] Wrote practical monitoring queries
- [ ] Created alert rules file
- [ ] Alerts visible in Prometheus UI

---

## Deliverables

1. Document with 5 useful queries and their explanations
2. Your `alert_rules.yml` file
3. Screenshot of Prometheus Alerts page

---

## PromQL Cheat Sheet

### Selectors
```promql
metric                     # All time series
metric{label="value"}      # Exact match
metric{label=~"regex"}     # Regex match
metric{label!="value"}     # Not equal
metric{label!~"regex"}     # Regex not match
```

### Functions
```promql
rate(metric[5m])           # Per-second rate
increase(metric[1h])       # Total increase
avg_over_time(metric[1h])  # Average over time
sum(metric)                # Sum all
avg(metric)                # Average all
max(metric)                # Maximum
min(metric)                # Minimum
count(metric)              # Count series
```

### Aggregation
```promql
sum by (label) (metric)    # Group by label
avg without (label) (m)    # Average excluding label
topk(5, metric)            # Top 5
bottomk(3, metric)         # Bottom 3
```

### Operators
```promql
metric > 100               # Greater than
metric < 50                # Less than
metric == 0                # Equal to
metric1 / metric2          # Division
metric1 + metric2          # Addition
```

---

## Common Patterns

### Request Rate
```promql
sum(rate(http_requests_total[5m])) by (endpoint)
```

### Error Rate
```promql
sum(rate(http_requests_total{status=~"5.."}[5m])) 
/ 
sum(rate(http_requests_total[5m]))
```

### Latency Percentiles
```promql
histogram_quantile(0.99, sum(rate(http_request_duration_bucket[5m])) by (le))
```

### Availability
```promql
avg_over_time(up[24h]) * 100
```

---

## Clean-Up

Keep services running for Friday's Jenkins exercises!

Final stack status:
```bash
docker compose ps
```

All three services should be running.

---

## Additional Resources

- [PromQL Documentation](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [PromQL Cheat Sheet](https://promlabs.com/promql-cheat-sheet/)
- [Alerting Rules](https://prometheus.io/docs/prometheus/latest/configuration/alerting_rules/)

