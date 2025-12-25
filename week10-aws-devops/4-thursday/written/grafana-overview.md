# Grafana Overview

## Learning Objectives

- Explain what Grafana is and its role in the monitoring stack
- Understand Grafana's visualization capabilities and panel types
- Describe the data source concept and supported integrations
- Differentiate between dashboards and panels
- Explore the Grafana ecosystem and community resources
- Compare Grafana Cloud versus self-hosted deployments

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Prometheus collects metrics, but humans need visualization. Grafana transforms time-series data into meaningful dashboards that reveal system behavior at a glance. When deployments happen, dashboards show the impact. When incidents occur, dashboards help identify the cause. When stakeholders ask "how is the system performing?", dashboards provide the answer.

As a quality engineer, Grafana dashboards help you monitor test environments, track application performance during load tests, and validate that deployments meet performance criteria. Dashboards become a shared language between QA, development, and operations teams.

## The Concept

### What is Grafana?

**Grafana** is an open-source platform for monitoring and observability visualization. It provides a powerful interface for querying, visualizing, and alerting on metrics from various data sources.

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Grafana Overview                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   KEY FEATURES                                                       │
│   ────────────                                                       │
│   • Rich visualization (graphs, gauges, tables, heatmaps)          │
│   • Multiple data source support                                    │
│   • Dashboard templating and variables                              │
│   • Alerting and notifications                                      │
│   • User management and teams                                       │
│   • Annotations and event overlays                                  │
│   • Dashboard sharing and embedding                                 │
│                                                                      │
│   TYPICAL MONITORING STACK                                          │
│   ────────────────────────                                          │
│                                                                      │
│   ┌─────────────┐     ┌─────────────┐     ┌─────────────┐          │
│   │ Applications│────▶│ Prometheus  │────▶│   Grafana   │          │
│   │ (Metrics)   │     │ (Storage)   │     │(Visualization)│        │
│   └─────────────┘     └─────────────┘     └─────────────┘          │
│                                                   │                  │
│                                                   ▼                  │
│                                           ┌─────────────┐           │
│                                           │  Dashboards │           │
│                                           │  + Alerts   │           │
│                                           └─────────────┘           │
│                                                                      │
│   Grafana is the "window" into your monitoring data                │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Visualization Capabilities

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Grafana Panel Types                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   TIME SERIES (Graph)                                               │
│   ───────────────────                                               │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │     ╱╲    ╱╲╲                                               │   │
│   │    ╱  ╲  ╱   ╲    ╱╲                                       │   │
│   │   ╱    ╲╱     ╲  ╱  ╲                                      │   │
│   │──╱──────────────╲╱────╲───────────────────────────────────│   │
│   └─────────────────────────────────────────────────────────────┘   │
│   Use for: Metrics over time, trends, comparisons                  │
│                                                                      │
│   STAT                    GAUGE                    BAR GAUGE        │
│   ────                    ─────                    ─────────        │
│   ┌─────────┐            ┌─────────┐              ┌─────────┐      │
│   │  98.5%  │            │  ◠◡◠   │              │ ████░░░ │      │
│   │ Uptime  │            │   72%   │              │   72%   │      │
│   └─────────┘            └─────────┘              └─────────┘      │
│   Single value           Radial gauge             Horizontal bar   │
│                                                                      │
│   TABLE                   HEATMAP                  PIE CHART        │
│   ─────                   ───────                  ─────────        │
│   ┌──────────────┐       ┌─────────────┐         ┌─────────┐      │
│   │ Name  │ CPU  │       │▓▓░▓▓▒▒▓▓▓▓│         │   ╱╲    │      │
│   │──────────────│       │░▒▒▓▓▓▒▒░░░│         │  ╱55╲   │      │
│   │ node1 │ 45%  │       │▓▓▓▒▒░░▓▓▒▒│         │ ╱────╲  │      │
│   │ node2 │ 62%  │       │▒░░▓▓▓▓▒▒▓▓│         │╲ 45% ╱  │      │
│   └──────────────┘       └─────────────┘         └─────────┘      │
│   Tabular data           Distribution over time   Proportions      │
│                                                                      │
│   LOGS                    STATE TIMELINE           GEOMAP           │
│   ────                    ──────────────           ──────           │
│   Error logs panel        Status history           Geographic data  │
│   (Loki integration)      (up/down states)         visualization   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Data Source Concept

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Data Source Architecture                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Grafana doesn't store data - it queries external data sources    │
│                                                                      │
│                        ┌─────────────────┐                          │
│                        │     GRAFANA     │                          │
│                        │                 │                          │
│                        └────────┬────────┘                          │
│                                 │                                   │
│           ┌─────────────────────┼─────────────────────┐            │
│           │                     │                     │            │
│           ▼                     ▼                     ▼            │
│   ┌───────────────┐    ┌───────────────┐    ┌───────────────┐     │
│   │  Prometheus   │    │   InfluxDB    │    │    MySQL      │     │
│   │   (Metrics)   │    │   (Metrics)   │    │   (Custom)    │     │
│   └───────────────┘    └───────────────┘    └───────────────┘     │
│           │                     │                     │            │
│           ▼                     ▼                     ▼            │
│   ┌───────────────┐    ┌───────────────┐    ┌───────────────┐     │
│   │ Elasticsearch │    │     Loki      │    │  CloudWatch   │     │
│   │    (Logs)     │    │    (Logs)     │    │    (AWS)      │     │
│   └───────────────┘    └───────────────┘    └───────────────┘     │
│                                                                      │
│   SUPPORTED DATA SOURCES                                            │
│   ──────────────────────                                            │
│   Metrics:     Prometheus, InfluxDB, Graphite, OpenTSDB            │
│   Logs:        Loki, Elasticsearch, CloudWatch Logs                │
│   Databases:   MySQL, PostgreSQL, MongoDB, SQL Server              │
│   Cloud:       AWS CloudWatch, Azure Monitor, Google Cloud         │
│   Tracing:     Jaeger, Zipkin, Tempo                               │
│   Other:       JSON API, CSV, Google Sheets                        │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Dashboards vs Panels

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Dashboards and Panels                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   DASHBOARD = Collection of panels for a specific purpose           │
│   PANEL = Single visualization widget                               │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Dashboard: "Application Overview"                           │   │
│   │  ─────────────────────────────────                           │   │
│   │                                                              │   │
│   │  ┌───────────────────────┐  ┌───────────────────────┐       │   │
│   │  │   Panel: Requests/s   │  │   Panel: Error Rate   │       │   │
│   │  │   [Time series graph] │  │   [Stat panel]        │       │   │
│   │  └───────────────────────┘  └───────────────────────┘       │   │
│   │                                                              │   │
│   │  ┌───────────────────────┐  ┌───────────────────────┐       │   │
│   │  │ Panel: Response Time  │  │  Panel: Server CPU    │       │   │
│   │  │  [P50, P95, P99]      │  │  [Gauge panel]        │       │   │
│   │  └───────────────────────┘  └───────────────────────┘       │   │
│   │                                                              │   │
│   │  ┌─────────────────────────────────────────────────────┐   │   │
│   │  │          Panel: Active Hosts Table                   │   │   │
│   │  └─────────────────────────────────────────────────────┘   │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Dashboard Organization:                                           │
│   • Rows group related panels                                       │
│   • Variables enable dynamic filtering                              │
│   • Links navigate between dashboards                               │
│   • Annotations mark events                                         │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Grafana Ecosystem

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Grafana Ecosystem                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   GRAFANA LABS PRODUCTS                                             │
│   ─────────────────────                                             │
│   Grafana         Core visualization platform (this reading)       │
│   Loki            Log aggregation (like Prometheus for logs)       │
│   Tempo           Distributed tracing backend                      │
│   Mimir           Long-term Prometheus storage                     │
│   OnCall          Incident management                              │
│   k6              Load testing                                      │
│                                                                      │
│   COMMUNITY RESOURCES                                               │
│   ───────────────────                                               │
│   grafana.com/dashboards      Pre-built dashboard library          │
│   grafana.com/plugins         Extensions and data sources          │
│   github.com/grafana          Open source repositories             │
│                                                                      │
│   DEPLOYMENT OPTIONS                                                │
│   ──────────────────                                                │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Self-Hosted              │  Grafana Cloud                   │   │
│   ├───────────────────────────┼──────────────────────────────────│   │
│   │  • Full control           │  • Managed service               │   │
│   │  • Your infrastructure    │  • No maintenance                │   │
│   │  • Free (OSS)             │  • Free tier available           │   │
│   │  • Scale yourself         │  • Auto-scaling                  │   │
│   │  • Custom plugins         │  • Integrated Loki/Tempo         │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Most organizations: Start with self-hosted, evaluate Cloud       │
│   for production or when scaling becomes complex                    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### User Management

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Grafana User Management                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ROLES                                                              │
│   ─────                                                              │
│   Admin      Full access, manage users, data sources, settings     │
│   Editor     Create/edit dashboards, cannot manage users           │
│   Viewer     View dashboards only, no editing                      │
│                                                                      │
│   ORGANIZATIONS                                                      │
│   ─────────────                                                      │
│   Isolate dashboards and data sources between teams                │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Org: Platform Team        │  Org: Application Team         │   │
│   │  ─────────────────────     │  ─────────────────────         │   │
│   │  • Infra dashboards        │  • App dashboards              │   │
│   │  • Prometheus DS           │  • Prometheus DS               │   │
│   │  • Node metrics            │  • App metrics                 │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   AUTHENTICATION                                                     │
│   ──────────────                                                     │
│   • Built-in (username/password)                                   │
│   • LDAP / Active Directory                                        │
│   • OAuth (Google, GitHub, Okta, etc.)                            │
│   • SAML                                                            │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Running Grafana with Docker

```bash
# Simple Grafana container
docker run -d \
  --name grafana \
  -p 3000:3000 \
  grafana/grafana

# Access at http://localhost:3000
# Default credentials: admin / admin

# With persistent storage
docker run -d \
  --name grafana \
  -p 3000:3000 \
  -v grafana-data:/var/lib/grafana \
  grafana/grafana

# With environment configuration
docker run -d \
  --name grafana \
  -p 3000:3000 \
  -e GF_SECURITY_ADMIN_PASSWORD=secret \
  -e GF_USERS_ALLOW_SIGN_UP=false \
  grafana/grafana
```

### Docker Compose: Full Monitoring Stack

```yaml
# docker-compose.yml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus-data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
  
  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    volumes:
      - grafana-data:/var/lib/grafana
      - ./grafana/provisioning:/etc/grafana/provisioning
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
      - GF_USERS_ALLOW_SIGN_UP=false
    depends_on:
      - prometheus
  
  node-exporter:
    image: prom/node-exporter:latest
    ports:
      - "9100:9100"

volumes:
  prometheus-data:
  grafana-data:
```

### Grafana Configuration File

```ini
# /etc/grafana/grafana.ini

[server]
http_port = 3000
root_url = https://grafana.example.com

[security]
admin_user = admin
admin_password = secure_password
secret_key = SW2YcwTIb9zpOOhoPsMm

[users]
allow_sign_up = false
allow_org_create = false

[auth.ldap]
enabled = true
config_file = /etc/grafana/ldap.toml

[auth.anonymous]
enabled = false

[dashboards]
default_home_dashboard_path = /var/lib/grafana/dashboards/home.json

[alerting]
enabled = true
execute_alerts = true
```

### Provisioning Data Sources

```yaml
# /etc/grafana/provisioning/datasources/datasources.yml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
    editable: false
  
  - name: Loki
    type: loki
    access: proxy
    url: http://loki:3100
    editable: false
  
  - name: CloudWatch
    type: cloudwatch
    access: proxy
    jsonData:
      authType: keys
      defaultRegion: us-east-1
    secureJsonData:
      accessKey: ${AWS_ACCESS_KEY}
      secretKey: ${AWS_SECRET_KEY}
```

### Provisioning Dashboards

```yaml
# /etc/grafana/provisioning/dashboards/dashboards.yml
apiVersion: 1

providers:
  - name: 'default'
    orgId: 1
    folder: 'Provisioned'
    folderUid: ''
    type: file
    disableDeletion: false
    updateIntervalSeconds: 30
    allowUiUpdates: true
    options:
      path: /var/lib/grafana/dashboards
```

### Grafana API Examples

```bash
# Get all dashboards
curl -H "Authorization: Bearer <api-key>" \
  http://localhost:3000/api/search?type=dash-db

# Create a folder
curl -X POST -H "Authorization: Bearer <api-key>" \
  -H "Content-Type: application/json" \
  -d '{"title": "My Folder"}' \
  http://localhost:3000/api/folders

# Import a dashboard
curl -X POST -H "Authorization: Bearer <api-key>" \
  -H "Content-Type: application/json" \
  -d @dashboard.json \
  http://localhost:3000/api/dashboards/db

# Create an API key
curl -X POST -H "Content-Type: application/json" \
  -u admin:admin \
  -d '{"name":"automation-key","role":"Admin"}' \
  http://localhost:3000/api/auth/keys
```

### Simple Dashboard JSON

```json
{
  "dashboard": {
    "title": "My Application",
    "panels": [
      {
        "title": "Request Rate",
        "type": "graph",
        "gridPos": { "x": 0, "y": 0, "w": 12, "h": 8 },
        "targets": [
          {
            "expr": "rate(http_requests_total[5m])",
            "legendFormat": "{{method}}"
          }
        ]
      },
      {
        "title": "Error Rate",
        "type": "stat",
        "gridPos": { "x": 12, "y": 0, "w": 6, "h": 4 },
        "targets": [
          {
            "expr": "sum(rate(http_requests_total{status=~\"5..\"}[5m])) / sum(rate(http_requests_total[5m])) * 100"
          }
        ],
        "fieldConfig": {
          "defaults": {
            "unit": "percent",
            "thresholds": {
              "steps": [
                { "value": 0, "color": "green" },
                { "value": 1, "color": "yellow" },
                { "value": 5, "color": "red" }
              ]
            }
          }
        }
      }
    ],
    "time": { "from": "now-1h", "to": "now" },
    "refresh": "30s"
  }
}
```

## Summary

- **Grafana** is the visualization layer of the monitoring stack, querying external data sources
- **Panel types** include time series graphs, gauges, stats, tables, heatmaps, and more
- **Data sources** connect Grafana to metrics (Prometheus), logs (Loki), databases, and cloud providers
- **Dashboards** contain multiple panels organized for specific monitoring purposes
- **Ecosystem** includes Loki (logs), Tempo (traces), and thousands of community dashboards/plugins
- **Deployment**: Self-hosted is free and flexible; Grafana Cloud is managed and scales easily

## Additional Resources

- [Grafana Documentation](https://grafana.com/docs/grafana/latest/) - Official comprehensive documentation
- [Dashboard Library](https://grafana.com/grafana/dashboards/) - Thousands of pre-built dashboards
- [Grafana Tutorials](https://grafana.com/tutorials/) - Step-by-step learning resources

