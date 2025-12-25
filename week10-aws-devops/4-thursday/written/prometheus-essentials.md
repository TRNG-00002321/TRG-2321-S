# Prometheus Essentials

## Learning Objectives

- Explain what Prometheus is and its role in monitoring
- Understand the time-series database concept and data model
- Describe Prometheus architecture and components
- Understand pull-based monitoring versus push-based alternatives
- Learn PromQL basics for querying metrics
- Configure and deploy Prometheus server

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Prometheus is the industry standard for metrics-based monitoring in cloud-native environments. It collects, stores, and enables querying of time-series data about your systems and applications. When deployments occur, Prometheus tracks the impact. When systems degrade, Prometheus alerts you. When troubleshooting, Prometheus provides the data.

As a quality engineer, monitoring data helps you understand application behavior, validate that deployments meet performance requirements, and investigate issues. Prometheus metrics power the dashboards (Grafana, covered next) that visualize system health.

## The Concept

### What is Prometheus?

**Prometheus** is an open-source systems monitoring and alerting toolkit originally built at SoundCloud. It's now a graduated project of the Cloud Native Computing Foundation (CNCF), alongside Kubernetes.

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Prometheus Overview                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   KEY FEATURES                                                       │
│   ────────────                                                       │
│   • Multi-dimensional data model (metrics with labels)              │
│   • Flexible query language (PromQL)                                │
│   • Pull-based collection (scrapes targets)                         │
│   • Time-series database (built-in storage)                         │
│   • Alerting rules and notifications                                │
│   • Service discovery                                               │
│   • No external dependencies                                        │
│                                                                      │
│   USE CASES                                                          │
│   ─────────                                                          │
│   • Application performance monitoring                              │
│   • Infrastructure metrics (CPU, memory, disk)                      │
│   • Business metrics (orders, users, transactions)                 │
│   • SLA/SLO monitoring                                              │
│   • Alerting on anomalies                                           │
│                                                                      │
│   NOT FOR                                                            │
│   ───────                                                            │
│   • Log aggregation (use ELK, Loki)                                │
│   • Distributed tracing (use Jaeger, Zipkin)                       │
│   • Long-term storage (use Thanos, Cortex)                         │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Time-Series Database Concept

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Time-Series Data Model                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   What is a Time Series?                                            │
│   ──────────────────────                                            │
│   A sequence of data points indexed by time                         │
│                                                                      │
│   Time:   │ 10:00 │ 10:15 │ 10:30 │ 10:45 │ 11:00 │                │
│   ────────┼───────┼───────┼───────┼───────┼───────┼                │
│   CPU %   │  45   │  52   │  48   │  67   │  55   │                │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   Prometheus Data Model                                             │
│   ─────────────────────                                             │
│                                                                      │
│   METRIC NAME + LABELS = UNIQUE TIME SERIES                        │
│                                                                      │
│   http_requests_total{method="GET", status="200", path="/api"}     │
│   │                    │           │              │                  │
│   └─ Metric name       └─ Label    └─ Label      └─ Label           │
│                                                                      │
│   Example time series:                                              │
│   http_requests_total{method="GET",  status="200"} = 1000          │
│   http_requests_total{method="GET",  status="404"} = 50            │
│   http_requests_total{method="POST", status="200"} = 500           │
│   http_requests_total{method="POST", status="500"} = 10            │
│                                                                      │
│   Each unique combination of metric + labels is a separate series  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Metric Types

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Prometheus Metric Types                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   COUNTER                                                            │
│   ───────                                                            │
│   Cumulative metric that only increases (or resets to zero)        │
│                                                                      │
│   Use for: Total requests, errors, bytes sent                      │
│   Example: http_requests_total = 10542                              │
│                                                                      │
│   Value: 100 → 150 → 200 → 0 (reset) → 50 → 100                   │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   GAUGE                                                              │
│   ─────                                                              │
│   Current value that can go up or down                              │
│                                                                      │
│   Use for: Temperature, memory usage, queue size                   │
│   Example: node_memory_usage_bytes = 1073741824                    │
│                                                                      │
│   Value: 45% → 62% → 58% → 70% → 55%                              │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   HISTOGRAM                                                          │
│   ─────────                                                          │
│   Samples observations into configurable buckets                    │
│                                                                      │
│   Use for: Request durations, response sizes                       │
│   Example: http_request_duration_seconds_bucket{le="0.5"} = 95    │
│            (95% of requests completed in <= 0.5 seconds)           │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   SUMMARY                                                            │
│   ───────                                                            │
│   Similar to histogram, provides quantiles directly                 │
│                                                                      │
│   Use for: Pre-calculated percentiles                              │
│   Example: http_request_duration_seconds{quantile="0.95"} = 0.45  │
│            (95th percentile response time is 0.45 seconds)         │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Prometheus Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Prometheus Architecture                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   TARGETS (Applications, Exporters)                                 │
│   ┌────────────┐ ┌────────────┐ ┌────────────┐                     │
│   │ App Server │ │ Node       │ │ Database   │                     │
│   │ /metrics   │ │ Exporter   │ │ Exporter   │                     │
│   └─────┬──────┘ └─────┬──────┘ └─────┬──────┘                     │
│         │              │              │                             │
│         │   HTTP scrape (pull)        │                             │
│         └──────────────┼──────────────┘                             │
│                        │                                            │
│                        ▼                                            │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                   PROMETHEUS SERVER                          │   │
│   │                                                              │   │
│   │  ┌────────────────┐  ┌────────────────┐  ┌───────────────┐ │   │
│   │  │   Retrieval    │  │   TSDB         │  │   HTTP        │ │   │
│   │  │   (Scraper)    │──│   (Storage)    │──│   Server      │ │   │
│   │  └────────────────┘  └────────────────┘  └───────────────┘ │   │
│   │          │                                      │          │   │
│   │          │           ┌────────────────┐         │          │   │
│   │          └──────────▶│   Alert        │         │          │   │
│   │                      │   Manager      │◀────────┘          │   │
│   │                      └───────┬────────┘                    │   │
│   └──────────────────────────────┼──────────────────────────────┘   │
│                                  │                                  │
│                                  ▼                                  │
│   ┌────────────────────────────────────────────────────────────┐   │
│   │  NOTIFICATIONS: Email, Slack, PagerDuty, Webhook           │   │
│   └────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Also: Grafana connects to Prometheus HTTP API for visualization  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Pull-Based vs Push-Based Monitoring

```
┌─────────────────────────────────────────────────────────────────────┐
│              Pull-Based vs Push-Based Monitoring                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   PULL-BASED (Prometheus)                                           │
│   ───────────────────────                                           │
│                                                                      │
│   Prometheus ──────────────────▶ Target                             │
│              HTTP GET /metrics                                       │
│                                                                      │
│   Pros:                                                              │
│   ✓ Easy to detect if target is down                               │
│   ✓ Can manually inspect metrics endpoints                         │
│   ✓ Easier scaling (add targets, not receiver capacity)           │
│   ✓ Service discovery integration                                  │
│                                                                      │
│   Cons:                                                              │
│   ✗ Requires network access to targets                             │
│   ✗ Short-lived jobs need Pushgateway                             │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   PUSH-BASED (e.g., Graphite, InfluxDB)                             │
│   ─────────────────────────────────────                             │
│                                                                      │
│   Application ──────────────────▶ Monitoring Server                 │
│                  Send metrics                                        │
│                                                                      │
│   Pros:                                                              │
│   ✓ Works through firewalls                                        │
│   ✓ Good for short-lived jobs                                      │
│   ✓ Application controls when to send                              │
│                                                                      │
│   Cons:                                                              │
│   ✗ Harder to detect silent failures                               │
│   ✗ Can overwhelm receiver under load                              │
│                                                                      │
│   Prometheus uses Pushgateway for short-lived jobs                  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Prometheus Configuration

```yaml
# prometheus.yml - Main configuration file
global:
  scrape_interval: 15s      # Default scrape interval
  evaluation_interval: 15s  # Rule evaluation interval
  
  external_labels:
    environment: production
    region: us-east-1

# Alertmanager configuration
alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093

# Rule files
rule_files:
  - /etc/prometheus/rules/*.yml

# Scrape configurations
scrape_configs:
  # Prometheus itself
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']
  
  # Application servers
  - job_name: 'web-servers'
    static_configs:
      - targets:
        - 'web1:8080'
        - 'web2:8080'
        - 'web3:8080'
    metrics_path: /metrics
    scrape_interval: 10s
  
  # Node exporters
  - job_name: 'node-exporter'
    static_configs:
      - targets:
        - 'node1:9100'
        - 'node2:9100'
```

### Run Prometheus with Docker

```bash
# Create configuration directory
mkdir -p prometheus-config

# Create prometheus.yml
cat > prometheus-config/prometheus.yml << 'EOF'
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']
EOF

# Run Prometheus container
docker run -d \
  --name prometheus \
  -p 9090:9090 \
  -v $(pwd)/prometheus-config:/etc/prometheus \
  prom/prometheus

# Access Prometheus UI
echo "Prometheus available at http://localhost:9090"
```

### Basic PromQL Queries

```promql
# Instant vector - current values
http_requests_total

# Filter by label
http_requests_total{method="GET"}
http_requests_total{status=~"5.."}  # Regex match

# Range vector - values over time period
http_requests_total[5m]  # Last 5 minutes

# Rate - per-second rate of increase (for counters)
rate(http_requests_total[5m])

# Aggregation
sum(http_requests_total)
sum by (method) (http_requests_total)
avg(node_cpu_seconds_total)

# Arithmetic
http_requests_total / http_requests_total{status="200"}

# Comparison
http_requests_total > 1000

# Top K
topk(10, http_requests_total)
```

### Application Metrics Endpoint (Python)

```python
# app.py - Flask app with Prometheus metrics
from flask import Flask
from prometheus_client import Counter, Histogram, generate_latest, CONTENT_TYPE_LATEST

app = Flask(__name__)

# Define metrics
REQUEST_COUNT = Counter(
    'http_requests_total',
    'Total HTTP requests',
    ['method', 'endpoint', 'status']
)

REQUEST_LATENCY = Histogram(
    'http_request_duration_seconds',
    'HTTP request latency',
    ['method', 'endpoint'],
    buckets=[0.01, 0.05, 0.1, 0.5, 1.0, 5.0]
)

@app.route('/api/users')
def get_users():
    with REQUEST_LATENCY.labels(method='GET', endpoint='/api/users').time():
        # Your logic here
        users = [{"id": 1, "name": "Alice"}]
        REQUEST_COUNT.labels(method='GET', endpoint='/api/users', status='200').inc()
        return {"users": users}

@app.route('/metrics')
def metrics():
    return generate_latest(), 200, {'Content-Type': CONTENT_TYPE_LATEST}

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)
```

### Application Metrics (Node.js)

```javascript
// server.js - Express app with Prometheus metrics
const express = require('express');
const promClient = require('prom-client');

const app = express();

// Create metrics
const httpRequestsTotal = new promClient.Counter({
  name: 'http_requests_total',
  help: 'Total HTTP requests',
  labelNames: ['method', 'route', 'status']
});

const httpRequestDuration = new promClient.Histogram({
  name: 'http_request_duration_seconds',
  help: 'HTTP request duration in seconds',
  labelNames: ['method', 'route'],
  buckets: [0.01, 0.05, 0.1, 0.5, 1, 5]
});

// Middleware to track metrics
app.use((req, res, next) => {
  const start = Date.now();
  res.on('finish', () => {
    const duration = (Date.now() - start) / 1000;
    httpRequestsTotal.labels(req.method, req.route?.path || req.path, res.statusCode).inc();
    httpRequestDuration.labels(req.method, req.route?.path || req.path).observe(duration);
  });
  next();
});

// Your routes
app.get('/api/users', (req, res) => {
  res.json({ users: [] });
});

// Metrics endpoint
app.get('/metrics', async (req, res) => {
  res.set('Content-Type', promClient.register.contentType);
  res.end(await promClient.register.metrics());
});

app.listen(8080, () => console.log('Server running on port 8080'));
```

### Service Discovery with Docker

```yaml
# prometheus.yml with Docker service discovery
scrape_configs:
  - job_name: 'docker-containers'
    docker_sd_configs:
      - host: unix:///var/run/docker.sock
        refresh_interval: 30s
    relabel_configs:
      # Only scrape containers with prometheus label
      - source_labels: [__meta_docker_container_label_prometheus_scrape]
        regex: 'true'
        action: keep
      # Use container name as instance label
      - source_labels: [__meta_docker_container_name]
        target_label: instance
        regex: '/(.*)'
        replacement: '${1}'
```

## Summary

- **Prometheus** is the standard monitoring solution for cloud-native environments
- **Time-series data** stores metrics indexed by time with labels for dimensions
- **Metric types**: Counter (only increases), Gauge (up/down), Histogram (buckets), Summary (quantiles)
- **Pull-based** architecture scrapes targets for metrics
- **PromQL** queries metrics data flexibly with aggregations, filters, and functions
- Applications expose metrics on `/metrics` endpoint in Prometheus format

## Additional Resources

- [Prometheus Documentation](https://prometheus.io/docs/) - Official comprehensive documentation
- [PromQL Examples](https://prometheus.io/docs/prometheus/latest/querying/examples/) - Query examples
- [Prometheus Exporters](https://prometheus.io/docs/instrumenting/exporters/) - Pre-built metric exporters

