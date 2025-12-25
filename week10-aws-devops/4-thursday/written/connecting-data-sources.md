# Connecting Data Sources

## Learning Objectives

- Add Prometheus as a data source in Grafana
- Configure connection settings for various data sources
- Test data source connectivity and troubleshoot issues
- Work with multiple data sources in a single Grafana instance
- Use data source variables for dynamic dashboard switching
- Understand authentication options for data sources

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Before you can visualize metrics, Grafana needs to know where to find them. Data source configuration is the bridge between your monitoring systems and Grafana's visualization capabilities. Misconfigured data sources mean empty dashboards and missing alerts.

As a quality engineer, understanding data source setup helps you configure monitoring for test environments, connect to multiple metrics sources (perhaps separate dev/staging/prod), and troubleshoot "no data" issues that often trace back to connection problems.

## The Concept

### Data Source Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Data Source Connection Flow                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   User queries panel     Grafana queries        Data source returns │
│          │               data source            results              │
│          ▼                    │                      │               │
│   ┌──────────────┐           │                      │               │
│   │    Panel     │           │                      │               │
│   │  "CPU Usage" │           │                      │               │
│   │  PromQL:     │           │                      │               │
│   │  rate(...)   │──────────▶│──────────▶│──────────▶│             │
│   └──────────────┘           │           │          │               │
│                              │           │          │               │
│                         ┌────┴────┐  ┌───┴───┐  ┌──┴───┐           │
│                         │ Grafana │  │ HTTP  │  │Prom- │           │
│                         │ Backend │  │Request│  │etheus│           │
│                         └─────────┘  └───────┘  └──────┘           │
│                                                                      │
│   Data Source Configuration determines:                             │
│   • URL: Where to send queries                                     │
│   • Auth: How to authenticate                                      │
│   • Options: Timeouts, TLS, etc.                                   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Adding Prometheus Data Source

```
┌─────────────────────────────────────────────────────────────────────┐
│              Adding Prometheus Data Source                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   STEP 1: Navigate to Data Sources                                  │
│   Configuration (gear icon) → Data Sources → Add data source        │
│                                                                      │
│   STEP 2: Select Prometheus                                         │
│   Search for "Prometheus" and click                                 │
│                                                                      │
│   STEP 3: Configure Connection                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Name:  [Prometheus                               ]         │   │
│   │  Default: [✓]                                               │   │
│   │                                                              │   │
│   │  HTTP                                                        │   │
│   │  ────                                                        │   │
│   │  URL:  [http://prometheus:9090                    ]         │   │
│   │                                                              │   │
│   │  Access: [Server (default)  ▼]                              │   │
│   │    Server: Grafana backend queries data source               │   │
│   │    Browser: Browser directly queries (CORS needed)           │   │
│   │                                                              │   │
│   │  Auth (if needed)                                            │   │
│   │  ────                                                        │   │
│   │  [ ] Basic auth                                              │   │
│   │  [ ] With credentials                                        │   │
│   │  [ ] TLS Client Auth                                        │   │
│   │  [ ] Skip TLS Verify                                        │   │
│   │                                                              │   │
│   │  Prometheus Specific                                         │   │
│   │  ──────────────────                                         │   │
│   │  Scrape interval: [15s            ]                         │   │
│   │  Query timeout:   [60s            ]                         │   │
│   │  HTTP Method:     [POST  ▼]                                 │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   STEP 4: Save & Test                                               │
│   Click "Save & Test" to verify connection                          │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Connection Options

```
┌─────────────────────────────────────────────────────────────────────┐
│              Data Source Connection Options                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ACCESS MODES                                                       │
│   ────────────                                                       │
│                                                                      │
│   Server (Proxy) - RECOMMENDED                                      │
│   ┌─────────────┐      ┌─────────────┐      ┌─────────────┐        │
│   │   Browser   │─────▶│   Grafana   │─────▶│ Data Source │        │
│   └─────────────┘      │   Server    │      │ (Prometheus)│        │
│                        └─────────────┘      └─────────────┘        │
│   • Data source URL can be internal (not exposed)                  │
│   • Grafana handles auth                                           │
│   • No CORS issues                                                 │
│                                                                      │
│   Browser (Direct)                                                   │
│   ┌─────────────┐                          ┌─────────────┐         │
│   │   Browser   │─────────────────────────▶│ Data Source │         │
│   └─────────────┘                          └─────────────┘         │
│   • Browser queries directly                                       │
│   • Data source must be accessible from browser                    │
│   • Requires CORS configuration                                    │
│   • Use for: Data sources on same domain                          │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   AUTHENTICATION OPTIONS                                            │
│   ──────────────────────                                            │
│                                                                      │
│   No Auth          Data source has no authentication               │
│   Basic Auth       Username/password                                │
│   API Key          Header-based authentication                      │
│   OAuth            Token-based (for cloud services)                │
│   TLS Client       Certificate-based authentication                │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Multiple Data Sources

```
┌─────────────────────────────────────────────────────────────────────┐
│              Working with Multiple Data Sources                      │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Common scenarios for multiple data sources:                       │
│                                                                      │
│   BY ENVIRONMENT                                                     │
│   ──────────────                                                     │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Prometheus-Dev      →  Development metrics                  │   │
│   │  Prometheus-Staging  →  Staging metrics                      │   │
│   │  Prometheus-Prod     →  Production metrics                   │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   BY TYPE                                                            │
│   ───────                                                            │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Prometheus          →  Application metrics                  │   │
│   │  CloudWatch          →  AWS infrastructure                   │   │
│   │  Loki                →  Application logs                     │   │
│   │  Elasticsearch       →  Search/analytics                     │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   BY TEAM                                                            │
│   ───────                                                            │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Prometheus-Platform →  Infrastructure team                  │   │
│   │  Prometheus-App      →  Application team                     │   │
│   │  Prometheus-Security →  Security team                        │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   DASHBOARD VARIABLES                                               │
│   ───────────────────                                               │
│   Use variables to switch between data sources dynamically:        │
│                                                                      │
│   Variable: $datasource                                             │
│   Type: Data source                                                 │
│   Filter: Prometheus                                                │
│                                                                      │
│   Then panels use: ${datasource} instead of hardcoded name         │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Provisioning Prometheus Data Source

```yaml
# /etc/grafana/provisioning/datasources/prometheus.yml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
    editable: true
    jsonData:
      timeInterval: "15s"
      queryTimeout: "60s"
      httpMethod: POST
```

### Multiple Environment Data Sources

```yaml
# /etc/grafana/provisioning/datasources/all-environments.yml
apiVersion: 1

datasources:
  - name: Prometheus-Dev
    type: prometheus
    access: proxy
    url: http://prometheus-dev:9090
    isDefault: false
    jsonData:
      timeInterval: "15s"
    editable: true
  
  - name: Prometheus-Staging
    type: prometheus
    access: proxy
    url: http://prometheus-staging:9090
    isDefault: false
    jsonData:
      timeInterval: "15s"
    editable: true
  
  - name: Prometheus-Production
    type: prometheus
    access: proxy
    url: http://prometheus-prod:9090
    isDefault: true
    jsonData:
      timeInterval: "15s"
    editable: true
```

### Prometheus with Basic Auth

```yaml
# prometheus.yml with authentication
apiVersion: 1

datasources:
  - name: Prometheus-Secured
    type: prometheus
    access: proxy
    url: https://prometheus.example.com
    basicAuth: true
    basicAuthUser: prometheus_user
    secureJsonData:
      basicAuthPassword: ${PROMETHEUS_PASSWORD}
    jsonData:
      tlsSkipVerify: false
```

### CloudWatch Data Source

```yaml
# cloudwatch.yml
apiVersion: 1

datasources:
  - name: CloudWatch
    type: cloudwatch
    access: proxy
    jsonData:
      authType: keys
      defaultRegion: us-east-1
      customMetricsNamespaces: "MyApp,MyOtherApp"
    secureJsonData:
      accessKey: ${AWS_ACCESS_KEY_ID}
      secretKey: ${AWS_SECRET_ACCESS_KEY}
```

### Loki Data Source (for Logs)

```yaml
# loki.yml
apiVersion: 1

datasources:
  - name: Loki
    type: loki
    access: proxy
    url: http://loki:3100
    jsonData:
      maxLines: 1000
      derivedFields:
        - datasourceUid: tempo
          matcherRegex: '"traceId":"(\w+)"'
          name: TraceID
          url: '$${__value.raw}'
```

### Test Data Source via API

```bash
# Test Prometheus connection
curl -X POST http://localhost:3000/api/datasources \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <api-key>" \
  -d '{
    "name": "Prometheus-Test",
    "type": "prometheus",
    "url": "http://prometheus:9090",
    "access": "proxy",
    "isDefault": false
  }'

# Get data source by name
curl -H "Authorization: Bearer <api-key>" \
  http://localhost:3000/api/datasources/name/Prometheus

# Test data source health
curl -H "Authorization: Bearer <api-key>" \
  http://localhost:3000/api/datasources/1/health

# Query through data source
curl -X POST http://localhost:3000/api/ds/query \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <api-key>" \
  -d '{
    "queries": [
      {
        "refId": "A",
        "datasource": {"type": "prometheus", "uid": "prometheus"},
        "expr": "up",
        "instant": true
      }
    ],
    "from": "now-1h",
    "to": "now"
  }'
```

### Data Source Variable in Dashboard

```json
{
  "templating": {
    "list": [
      {
        "name": "datasource",
        "type": "datasource",
        "query": "prometheus",
        "current": {
          "text": "Prometheus-Production",
          "value": "Prometheus-Production"
        },
        "hide": 0,
        "includeAll": false,
        "multi": false,
        "options": [],
        "refresh": 1,
        "regex": ""
      }
    ]
  },
  "panels": [
    {
      "title": "Requests per Second",
      "datasource": "${datasource}",
      "targets": [
        {
          "expr": "rate(http_requests_total[5m])"
        }
      ]
    }
  ]
}
```

### Troubleshooting Connection Issues

```bash
# Check if Grafana can reach data source
docker exec grafana curl -v http://prometheus:9090/-/healthy

# Check Grafana logs for data source errors
docker logs grafana 2>&1 | grep -i "datasource\|prometheus"

# Test from Grafana container
docker exec grafana wget -qO- http://prometheus:9090/api/v1/query?query=up

# Verify Prometheus is responding
curl http://prometheus:9090/api/v1/query?query=up

# Check network connectivity (Docker)
docker network inspect <network_name>

# Common issues:
# 1. Wrong URL (use container name in Docker network)
# 2. Port not exposed
# 3. Authentication mismatch
# 4. TLS certificate issues
# 5. Firewall/security group blocking
```

### Docker Compose with Data Source Provisioning

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
    networks:
      - monitoring

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - ./grafana/provisioning:/etc/grafana/provisioning
    networks:
      - monitoring
    depends_on:
      - prometheus

networks:
  monitoring:
    driver: bridge
```

```yaml
# ./grafana/provisioning/datasources/default.yml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
```

## Summary

- **Data sources** connect Grafana to external metrics/data systems
- **Server (proxy)** access mode is recommended—Grafana backend queries the data source
- **Prometheus connection** requires URL, auth settings, and optional scrape interval configuration
- **Multiple data sources** support different environments, types, or teams
- **Data source variables** enable dynamic switching between data sources in dashboards
- **Provisioning** via YAML files enables automated, version-controlled data source configuration

## Additional Resources

- [Grafana Data Sources](https://grafana.com/docs/grafana/latest/datasources/) - Complete data source documentation
- [Prometheus Data Source](https://grafana.com/docs/grafana/latest/datasources/prometheus/) - Prometheus-specific configuration
- [Provisioning Grafana](https://grafana.com/docs/grafana/latest/administration/provisioning/) - Automated configuration

