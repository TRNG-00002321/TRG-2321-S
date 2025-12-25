# Scraping Metrics

## Learning Objectives

- Configure Prometheus scrape settings for targets
- Understand targets and jobs in Prometheus configuration
- Implement service discovery for dynamic environments
- Set appropriate scrape intervals for different use cases
- Create and configure metric endpoints for applications
- Use exporters for systems without native Prometheus support
- Apply relabeling for metric transformation

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Prometheus doesn't receive metrics—it collects them by "scraping" HTTP endpoints. Understanding how to configure scraping is essential for getting metrics from your applications, infrastructure, and third-party systems. Misconfigured scraping means missing data, alerting failures, or overwhelming your targets.

As a quality engineer, knowing how metrics are collected helps you ensure test environments are properly monitored, validate that applications expose the right metrics, and troubleshoot monitoring gaps.

## The Concept

### How Prometheus Scraping Works

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Prometheus Scraping Process                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Every scrape_interval (e.g., 15 seconds):                         │
│                                                                      │
│   ┌─────────────────┐     HTTP GET /metrics      ┌───────────────┐  │
│   │   Prometheus    │ ─────────────────────────▶ │    Target     │  │
│   │                 │                            │  (Application)│  │
│   │                 │ ◀───────────────────────── │               │  │
│   └─────────────────┘     Metrics in text format └───────────────┘  │
│                                                                      │
│   Response format:                                                   │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │ # HELP http_requests_total Total HTTP requests              │   │
│   │ # TYPE http_requests_total counter                          │   │
│   │ http_requests_total{method="GET",status="200"} 1027         │   │
│   │ http_requests_total{method="GET",status="404"} 3            │   │
│   │ http_requests_total{method="POST",status="200"} 516         │   │
│   │ # HELP http_request_duration_seconds Request latency        │   │
│   │ # TYPE http_request_duration_seconds histogram              │   │
│   │ http_request_duration_seconds_bucket{le="0.1"} 24054       │   │
│   │ http_request_duration_seconds_bucket{le="0.5"} 33444       │   │
│   │ http_request_duration_seconds_bucket{le="+Inf"} 33444      │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Prometheus:                                                        │
│   1. Parses the text format                                         │
│   2. Adds timestamp                                                  │
│   3. Stores in time-series database                                │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Targets and Jobs

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Targets and Jobs                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   JOB: A collection of targets with the same purpose                │
│   TARGET: A single endpoint to scrape                               │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Job: "api-servers"                                          │   │
│   │                                                              │   │
│   │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │   │
│   │  │ api-1:8080  │  │ api-2:8080  │  │ api-3:8080  │         │   │
│   │  │  /metrics   │  │  /metrics   │  │  /metrics   │         │   │
│   │  └─────────────┘  └─────────────┘  └─────────────┘         │   │
│   │     Target          Target          Target                  │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Job: "node-exporter"                                        │   │
│   │                                                              │   │
│   │  ┌─────────────┐  ┌─────────────┐                           │   │
│   │  │ node1:9100  │  │ node2:9100  │                           │   │
│   │  │  /metrics   │  │  /metrics   │                           │   │
│   │  └─────────────┘  └─────────────┘                           │   │
│   │     Target          Target                                   │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Labels added automatically:                                       │
│   • job="api-servers"                                               │
│   • instance="api-1:8080"                                          │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Service Discovery

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Service Discovery Methods                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   STATIC CONFIGURATION                                              │
│   ────────────────────                                              │
│   Manual list of targets (simple but inflexible)                   │
│                                                                      │
│   scrape_configs:                                                    │
│     - job_name: 'web-servers'                                       │
│       static_configs:                                                │
│         - targets: ['web1:8080', 'web2:8080']                       │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   DYNAMIC SERVICE DISCOVERY                                         │
│   ─────────────────────────                                         │
│   Automatically discover targets from various sources              │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Source              │  Use Case                            │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  kubernetes_sd       │  Kubernetes pods and services        │   │
│   │  docker_sd           │  Docker containers                   │   │
│   │  ec2_sd              │  AWS EC2 instances                   │   │
│   │  consul_sd           │  Consul service registry             │   │
│   │  dns_sd              │  DNS SRV records                     │   │
│   │  file_sd             │  JSON/YAML file (auto-reloaded)     │   │
│   │  azure_sd            │  Azure VMs                           │   │
│   │  gce_sd              │  Google Compute Engine               │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Benefits of Service Discovery:                                    │
│   ✓ No manual target updates when scaling                          │
│   ✓ Handles dynamic environments (containers, cloud)               │
│   ✓ Can filter based on metadata (labels, tags)                    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Common Exporters

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Common Prometheus Exporters                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Exporters expose metrics from systems without native support      │
│                                                                      │
│   INFRASTRUCTURE                                                     │
│   ──────────────                                                     │
│   node_exporter        Linux/Unix system metrics                    │
│   windows_exporter     Windows system metrics                       │
│   cAdvisor             Container metrics                            │
│   kube-state-metrics   Kubernetes cluster state                     │
│                                                                      │
│   DATABASES                                                          │
│   ─────────                                                          │
│   postgres_exporter    PostgreSQL                                   │
│   mysqld_exporter      MySQL/MariaDB                                │
│   mongodb_exporter     MongoDB                                      │
│   redis_exporter       Redis                                        │
│   elasticsearch_exporter  Elasticsearch                             │
│                                                                      │
│   WEB/APPLICATIONS                                                   │
│   ────────────────                                                   │
│   nginx_exporter       NGINX metrics                                │
│   apache_exporter      Apache HTTP Server                          │
│   blackbox_exporter    HTTP/TCP/ICMP probes                        │
│   jmx_exporter         Java JMX metrics                            │
│                                                                      │
│   OTHER                                                              │
│   ─────                                                              │
│   snmp_exporter        SNMP devices                                 │
│   cloudwatch_exporter  AWS CloudWatch                               │
│   github_exporter      GitHub metrics                               │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Scrape Intervals

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Choosing Scrape Intervals                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Scrape interval determines how often Prometheus collects metrics  │
│                                                                      │
│   RECOMMENDATIONS                                                    │
│   ───────────────                                                    │
│                                                                      │
│   15 seconds (default)    General purpose, most workloads          │
│   ────────────────────                                              │
│   • Application metrics                                             │
│   • Node exporter                                                    │
│   • Most use cases                                                  │
│                                                                      │
│   5-10 seconds            Fast-changing metrics                     │
│   ────────────                                                       │
│   • High-frequency trading                                         │
│   • Real-time dashboards                                           │
│   • SLA-critical services                                          │
│                                                                      │
│   30-60 seconds           Stable or expensive metrics              │
│   ─────────────                                                      │
│   • Cloud provider APIs (rate limited)                             │
│   • Large metric surfaces                                          │
│   • Cost optimization                                               │
│                                                                      │
│   TRADEOFFS                                                          │
│   ─────────                                                          │
│   Shorter intervals:                                                │
│   ✓ More granular data                                             │
│   ✓ Faster alerting                                                │
│   ✗ More storage                                                    │
│   ✗ Higher load on targets                                         │
│                                                                      │
│   Longer intervals:                                                  │
│   ✓ Less storage                                                    │
│   ✓ Lower load                                                      │
│   ✗ Missed spikes                                                   │
│   ✗ Slower alerting                                                │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Basic Scrape Configuration

```yaml
# prometheus.yml
global:
  scrape_interval: 15s       # Default for all jobs
  evaluation_interval: 15s   # Alert rule evaluation

scrape_configs:
  # Scrape Prometheus itself
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  # Application servers
  - job_name: 'api-servers'
    scrape_interval: 10s      # Override for this job
    metrics_path: /metrics    # Default, can be changed
    scheme: http              # or https
    static_configs:
      - targets:
        - 'api1.example.com:8080'
        - 'api2.example.com:8080'
        - 'api3.example.com:8080'
        labels:
          environment: production
          team: platform

  # Node exporters for infrastructure
  - job_name: 'node-exporter'
    static_configs:
      - targets:
        - 'node1:9100'
        - 'node2:9100'
```

### File-Based Service Discovery

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'file-discovery'
    file_sd_configs:
      - files:
        - '/etc/prometheus/targets/*.json'
        refresh_interval: 30s
```

```json
// /etc/prometheus/targets/web-servers.json
[
  {
    "targets": ["web1:8080", "web2:8080", "web3:8080"],
    "labels": {
      "env": "production",
      "team": "web"
    }
  },
  {
    "targets": ["web-staging:8080"],
    "labels": {
      "env": "staging",
      "team": "web"
    }
  }
]
```

### Kubernetes Service Discovery

```yaml
# prometheus.yml
scrape_configs:
  # Discover all pods with prometheus.io/scrape annotation
  - job_name: 'kubernetes-pods'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      # Only scrape pods with prometheus.io/scrape: "true"
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
      # Use custom port if specified
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_port]
        action: replace
        target_label: __address__
        regex: (.+)
        replacement: ${1}
      # Use custom path if specified
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_path]
        action: replace
        target_label: __metrics_path__
        regex: (.+)
      # Add pod labels
      - action: labelmap
        regex: __meta_kubernetes_pod_label_(.+)
      # Add namespace label
      - source_labels: [__meta_kubernetes_namespace]
        action: replace
        target_label: namespace
      # Add pod name label
      - source_labels: [__meta_kubernetes_pod_name]
        action: replace
        target_label: pod
```

### Docker Service Discovery

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'docker-containers'
    docker_sd_configs:
      - host: unix:///var/run/docker.sock
        refresh_interval: 15s
    relabel_configs:
      # Only scrape containers with label prometheus.scrape=true
      - source_labels: [__meta_docker_container_label_prometheus_scrape]
        regex: 'true'
        action: keep
      # Get port from label
      - source_labels: [__meta_docker_container_label_prometheus_port]
        target_label: __address__
        regex: '(.+)'
        replacement: '${1}'
      # Use container name as instance
      - source_labels: [__meta_docker_container_name]
        target_label: instance
        regex: '/(.*)'
        replacement: '${1}'
```

### EC2 Service Discovery

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'ec2-instances'
    ec2_sd_configs:
      - region: us-east-1
        access_key: YOUR_ACCESS_KEY
        secret_key: YOUR_SECRET_KEY
        port: 9100
        filters:
          - name: tag:Environment
            values: [production]
          - name: instance-state-name
            values: [running]
    relabel_configs:
      # Use instance ID as instance label
      - source_labels: [__meta_ec2_instance_id]
        target_label: instance
      # Use Name tag
      - source_labels: [__meta_ec2_tag_Name]
        target_label: name
      # Use private IP for scraping
      - source_labels: [__meta_ec2_private_ip]
        target_label: __address__
        replacement: '${1}:9100'
```

### Setting Up Node Exporter

```bash
# Install and run node_exporter
docker run -d \
  --name node-exporter \
  --net="host" \
  --pid="host" \
  -v "/:/host:ro,rslave" \
  prom/node-exporter:latest \
  --path.rootfs=/host

# Node exporter exposes metrics at :9100/metrics

# Configure Prometheus to scrape it
# Add to prometheus.yml:
# scrape_configs:
#   - job_name: 'node-exporter'
#     static_configs:
#       - targets: ['localhost:9100']
```

### Relabeling Examples

```yaml
scrape_configs:
  - job_name: 'app-servers'
    static_configs:
      - targets: ['app1:8080', 'app2:8080']
    relabel_configs:
      # Add a static label
      - target_label: environment
        replacement: production
      
      # Drop certain metrics path
      - source_labels: [__metrics_path__]
        regex: /internal/.*
        action: drop
      
      # Rename a label
      - source_labels: [__address__]
        target_label: hostname
        regex: '([^:]+):.*'
        replacement: '${1}'
    
    metric_relabel_configs:
      # Drop high-cardinality metric
      - source_labels: [__name__]
        regex: 'go_gc_.*'
        action: drop
      
      # Rename a metric
      - source_labels: [__name__]
        regex: 'app_http_requests_total'
        target_label: __name__
        replacement: 'http_requests_total'
```

### Blackbox Exporter for Probing

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'blackbox-http'
    metrics_path: /probe
    params:
      module: [http_2xx]
    static_configs:
      - targets:
        - https://example.com
        - https://api.example.com/health
    relabel_configs:
      - source_labels: [__address__]
        target_label: __param_target
      - source_labels: [__param_target]
        target_label: instance
      - target_label: __address__
        replacement: blackbox-exporter:9115
```

### Verify Scrape Targets

```bash
# Check targets in Prometheus UI
# http://localhost:9090/targets

# Or via API
curl http://localhost:9090/api/v1/targets | jq

# Check specific metric
curl http://localhost:9090/api/v1/query?query=up | jq

# Test scraping manually
curl http://target:8080/metrics
```

## Summary

- **Scraping** is how Prometheus collects metrics via HTTP GET to `/metrics` endpoints
- **Jobs** group related targets; **targets** are individual scrape endpoints
- **Service discovery** dynamically finds targets (Kubernetes, Docker, EC2, Consul)
- **Exporters** expose metrics from systems without native Prometheus support
- **Scrape intervals** balance granularity vs. resource usage (default: 15s)
- **Relabeling** transforms labels before storage or during scraping

## Additional Resources

- [Prometheus Configuration](https://prometheus.io/docs/prometheus/latest/configuration/configuration/) - Complete configuration reference
- [Service Discovery](https://prometheus.io/docs/prometheus/latest/configuration/configuration/#scrape_config) - All discovery mechanisms
- [Exporters List](https://prometheus.io/docs/instrumenting/exporters/) - Official and community exporters

