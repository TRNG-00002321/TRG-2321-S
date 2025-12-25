# Grafana Dashboards

## Learning Objectives

- Create dashboards with multiple panel types
- Apply panel configuration options for effective visualization
- Implement dashboard layout and organization best practices
- Use variables and templating for dynamic dashboards
- Share and export dashboards for team collaboration
- Manage dashboard versioning and history

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Dashboards are where monitoring data becomes actionable insight. A well-designed dashboard answers questions at a glance: "Is the system healthy?", "What changed after deployment?", "Where is the bottleneck?". Poor dashboards overwhelm with data; great dashboards guide decisions.

As a quality engineer, you'll create dashboards to monitor test environments, track performance during load tests, and validate deployments meet requirements. You'll also read dashboards during incident investigations, making it critical to understand what you're seeing.

## The Concept

### Dashboard Anatomy

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Dashboard Anatomy                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  ☰ My Application Dashboard  ⭐  🔗  ⚙️  ⏰ Last 1h  🔄 30s│   │
│   │  ─────────────────────────────────────────────────────────  │   │
│   │  Filter: [$environment ▼] [$service ▼] [$instance ▼]       │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │                                                              │   │
│   │  ROW 1: Overview                                             │   │
│   │  ┌───────────────────────┐  ┌───────────────────────┐       │   │
│   │  │   Requests/sec        │  │   Error Rate          │       │   │
│   │  │   ┌───────────────┐   │  │   ┌───────────────┐   │       │   │
│   │  │   │   1,234       │   │  │   │    0.5%       │   │       │   │
│   │  │   └───────────────┘   │  │   └───────────────┘   │       │   │
│   │  └───────────────────────┘  └───────────────────────┘       │   │
│   │                                                              │   │
│   │  ROW 2: Latency                                              │   │
│   │  ┌─────────────────────────────────────────────────────┐   │   │
│   │  │   Response Time (P50, P95, P99)                      │   │   │
│   │  │   ╱╲    ╱╲                                           │   │   │
│   │  │  ╱  ╲  ╱  ╲   P99: 450ms                            │   │   │
│   │  │ ╱    ╲╱    ╲  P95: 200ms                            │   │   │
│   │  │╱            ╲ P50: 50ms                              │   │   │
│   │  └─────────────────────────────────────────────────────┘   │   │
│   │                                                              │   │
│   │  ROW 3: Infrastructure                                       │   │
│   │  ┌─────────────────┐ ┌─────────────────┐ ┌───────────────┐ │   │
│   │  │ CPU Usage       │ │ Memory Usage    │ │ Disk I/O      │ │   │
│   │  │  ◠◡◠  72%      │ │  ◠◡◠  58%      │ │  ▓▓▓▓░░       │ │   │
│   │  └─────────────────┘ └─────────────────┘ └───────────────┘ │   │
│   │                                                              │   │
│   │  ── Deployment Marker: v2.3.1 deployed ──────────────────   │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Components:                                                        │
│   • Title bar: Dashboard name, time picker, refresh interval       │
│   • Variables: Dynamic filters (dropdowns)                         │
│   • Rows: Group related panels                                      │
│   • Panels: Individual visualizations                              │
│   • Annotations: Mark events (deployments, incidents)              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Panel Types and Use Cases

```
┌─────────────────────────────────────────────────────────────────────┐
│                 Panel Types by Use Case                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   SHOW CURRENT VALUE                                                │
│   ──────────────────                                                │
│   Stat        Single big number with optional sparkline            │
│   Gauge       Radial or bar showing value vs thresholds            │
│   Bar Gauge   Horizontal/vertical bar progress                      │
│                                                                      │
│   Use for: Error rate, current users, CPU%, availability           │
│                                                                      │
│   SHOW TRENDS OVER TIME                                             │
│   ─────────────────────                                             │
│   Time series Graph with multiple series, legends, thresholds      │
│   Heatmap      Distribution over time (latency buckets)            │
│   State timeline Status changes over time (up/down)                │
│                                                                      │
│   Use for: Request rate, latency percentiles, resource usage       │
│                                                                      │
│   SHOW COMPARISONS                                                   │
│   ────────────────                                                   │
│   Bar chart   Compare values across categories                     │
│   Pie chart   Show proportions (use sparingly)                     │
│   Table       Detailed breakdown with sorting                      │
│                                                                      │
│   Use for: Traffic by endpoint, errors by service, top N           │
│                                                                      │
│   SHOW TEXT/LOGS                                                     │
│   ──────────────                                                     │
│   Text        Markdown documentation                               │
│   Logs        Log lines from Loki/Elasticsearch                    │
│   News        RSS feeds                                             │
│                                                                      │
│   Use for: Runbooks, context, correlated logs                      │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Variables and Templating

```
┌─────────────────────────────────────────────────────────────────────┐
│                 Dashboard Variables                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Variables make dashboards dynamic and reusable                    │
│                                                                      │
│   VARIABLE TYPES                                                     │
│   ──────────────                                                     │
│                                                                      │
│   Query          Values from data source query                      │
│   ┌───────────────────────────────────────────────────────────┐    │
│   │ Name: instance                                             │    │
│   │ Query: label_values(up, instance)                          │    │
│   │ Result: [server1:9090, server2:9090, server3:9090]        │    │
│   └───────────────────────────────────────────────────────────┘    │
│                                                                      │
│   Custom         Static list of values                              │
│   ┌───────────────────────────────────────────────────────────┐    │
│   │ Name: environment                                          │    │
│   │ Values: production,staging,development                     │    │
│   └───────────────────────────────────────────────────────────┘    │
│                                                                      │
│   Data source    Switch between data sources                       │
│   ┌───────────────────────────────────────────────────────────┐    │
│   │ Name: datasource                                           │    │
│   │ Type: Data source                                          │    │
│   │ Query: prometheus                                          │    │
│   └───────────────────────────────────────────────────────────┘    │
│                                                                      │
│   Interval       Time interval options                             │
│   ┌───────────────────────────────────────────────────────────┐    │
│   │ Name: interval                                             │    │
│   │ Values: 1m,5m,10m,30m,1h                                   │    │
│   └───────────────────────────────────────────────────────────┘    │
│                                                                      │
│   USING VARIABLES IN QUERIES                                        │
│   ──────────────────────────                                        │
│   PromQL: rate(http_requests_total{instance="$instance"}[5m])      │
│                                                                      │
│   Multi-value: rate(http_requests_total{instance=~"$instance"}[5m])│
│   (Use =~ for regex match when "Multi" or "All" enabled)           │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Dashboard Best Practices

```
┌─────────────────────────────────────────────────────────────────────┐
│               Dashboard Design Best Practices                        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   LAYOUT                                                             │
│   ──────                                                             │
│   ✓ Most important metrics at top-left (read first)                │
│   ✓ Group related metrics in rows                                  │
│   ✓ Keep dashboards focused (one purpose per dashboard)            │
│   ✓ Use consistent panel sizes                                     │
│   ✗ Don't overcrowd (whitespace is good)                          │
│                                                                      │
│   VISUALIZATION                                                      │
│   ─────────────                                                      │
│   ✓ Use appropriate panel type for data                            │
│   ✓ Set meaningful thresholds with colors                          │
│   ✓ Include units (%, ms, bytes)                                   │
│   ✓ Add descriptions to panels                                     │
│   ✗ Don't use pie charts for more than 5 categories               │
│                                                                      │
│   VARIABLES                                                          │
│   ─────────                                                          │
│   ✓ Use variables for environment/service selection                │
│   ✓ Set sensible defaults                                          │
│   ✓ Chain variables (service → instance)                          │
│   ✗ Don't create too many variables (overwhelming)                │
│                                                                      │
│   PERFORMANCE                                                        │
│   ───────────                                                        │
│   ✓ Limit number of panels (< 20 per dashboard)                    │
│   ✓ Use appropriate time ranges                                    │
│   ✓ Avoid expensive queries                                        │
│   ✗ Don't set too-short refresh intervals                         │
│                                                                      │
│   MAINTENANCE                                                        │
│   ───────────                                                        │
│   ✓ Add documentation panel explaining the dashboard               │
│   ✓ Version control dashboard JSON                                 │
│   ✓ Use consistent naming conventions                              │
│   ✓ Review and clean up unused dashboards                         │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Create Dashboard via API

```bash
# Create a simple dashboard
curl -X POST http://localhost:3000/api/dashboards/db \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <api-key>" \
  -d '{
    "dashboard": {
      "title": "My Application",
      "tags": ["application", "production"],
      "timezone": "browser",
      "panels": [],
      "schemaVersion": 30
    },
    "folderUid": "",
    "overwrite": false
  }'
```

### Dashboard JSON Structure

```json
{
  "dashboard": {
    "id": null,
    "uid": "my-app-dashboard",
    "title": "Application Overview",
    "tags": ["application", "production"],
    "timezone": "browser",
    "schemaVersion": 30,
    "version": 1,
    "refresh": "30s",
    "time": {
      "from": "now-1h",
      "to": "now"
    },
    "templating": {
      "list": [
        {
          "name": "service",
          "type": "query",
          "datasource": "Prometheus",
          "query": "label_values(up, service)",
          "refresh": 1,
          "current": {}
        }
      ]
    },
    "annotations": {
      "list": [
        {
          "datasource": "Prometheus",
          "enable": true,
          "expr": "changes(deployment_timestamp[1m]) > 0",
          "name": "Deployments",
          "iconColor": "blue"
        }
      ]
    },
    "panels": []
  }
}
```

### Time Series Panel

```json
{
  "title": "Request Rate",
  "type": "timeseries",
  "gridPos": { "x": 0, "y": 0, "w": 12, "h": 8 },
  "datasource": "Prometheus",
  "targets": [
    {
      "expr": "sum(rate(http_requests_total{service=\"$service\"}[5m])) by (method)",
      "legendFormat": "{{method}}",
      "refId": "A"
    }
  ],
  "fieldConfig": {
    "defaults": {
      "unit": "reqps",
      "custom": {
        "lineWidth": 2,
        "fillOpacity": 10,
        "pointSize": 5,
        "showPoints": "never"
      }
    }
  },
  "options": {
    "legend": {
      "displayMode": "table",
      "placement": "bottom",
      "calcs": ["mean", "max", "last"]
    },
    "tooltip": {
      "mode": "multi"
    }
  }
}
```

### Stat Panel

```json
{
  "title": "Error Rate",
  "type": "stat",
  "gridPos": { "x": 12, "y": 0, "w": 6, "h": 4 },
  "datasource": "Prometheus",
  "targets": [
    {
      "expr": "sum(rate(http_requests_total{status=~\"5..\"}[5m])) / sum(rate(http_requests_total[5m])) * 100",
      "refId": "A"
    }
  ],
  "fieldConfig": {
    "defaults": {
      "unit": "percent",
      "thresholds": {
        "mode": "absolute",
        "steps": [
          { "value": null, "color": "green" },
          { "value": 1, "color": "yellow" },
          { "value": 5, "color": "red" }
        ]
      },
      "mappings": []
    }
  },
  "options": {
    "colorMode": "background",
    "graphMode": "area",
    "justifyMode": "auto",
    "textMode": "auto",
    "reduceOptions": {
      "calcs": ["lastNotNull"],
      "fields": "",
      "values": false
    }
  }
}
```

### Gauge Panel

```json
{
  "title": "CPU Usage",
  "type": "gauge",
  "gridPos": { "x": 0, "y": 8, "w": 6, "h": 6 },
  "datasource": "Prometheus",
  "targets": [
    {
      "expr": "100 - (avg(rate(node_cpu_seconds_total{mode=\"idle\"}[5m])) * 100)",
      "refId": "A"
    }
  ],
  "fieldConfig": {
    "defaults": {
      "unit": "percent",
      "min": 0,
      "max": 100,
      "thresholds": {
        "mode": "absolute",
        "steps": [
          { "value": null, "color": "green" },
          { "value": 70, "color": "yellow" },
          { "value": 90, "color": "red" }
        ]
      }
    }
  },
  "options": {
    "showThresholdLabels": false,
    "showThresholdMarkers": true
  }
}
```

### Table Panel

```json
{
  "title": "Top Endpoints by Requests",
  "type": "table",
  "gridPos": { "x": 0, "y": 14, "w": 24, "h": 8 },
  "datasource": "Prometheus",
  "targets": [
    {
      "expr": "topk(10, sum by (path) (rate(http_requests_total[5m])))",
      "format": "table",
      "instant": true,
      "refId": "A"
    }
  ],
  "transformations": [
    {
      "id": "organize",
      "options": {
        "excludeByName": { "Time": true },
        "renameByName": {
          "path": "Endpoint",
          "Value": "Requests/sec"
        }
      }
    }
  ],
  "fieldConfig": {
    "defaults": {
      "custom": {
        "align": "left"
      }
    },
    "overrides": [
      {
        "matcher": { "id": "byName", "options": "Requests/sec" },
        "properties": [
          { "id": "unit", "value": "reqps" },
          { "id": "decimals", "value": 2 }
        ]
      }
    ]
  }
}
```

### Dashboard with Row Layout

```json
{
  "panels": [
    {
      "type": "row",
      "title": "Overview",
      "gridPos": { "x": 0, "y": 0, "w": 24, "h": 1 },
      "collapsed": false
    },
    {
      "title": "Request Rate",
      "gridPos": { "x": 0, "y": 1, "w": 12, "h": 8 }
    },
    {
      "title": "Error Rate",
      "gridPos": { "x": 12, "y": 1, "w": 12, "h": 8 }
    },
    {
      "type": "row",
      "title": "Infrastructure",
      "gridPos": { "x": 0, "y": 9, "w": 24, "h": 1 },
      "collapsed": true,
      "panels": [
        {
          "title": "CPU Usage",
          "gridPos": { "x": 0, "y": 10, "w": 8, "h": 6 }
        },
        {
          "title": "Memory Usage",
          "gridPos": { "x": 8, "y": 10, "w": 8, "h": 6 }
        }
      ]
    }
  ]
}
```

### Export and Share Dashboard

```bash
# Export dashboard JSON
curl -H "Authorization: Bearer <api-key>" \
  http://localhost:3000/api/dashboards/uid/my-app-dashboard \
  | jq '.dashboard' > dashboard.json

# Import dashboard
curl -X POST http://localhost:3000/api/dashboards/db \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <api-key>" \
  -d @dashboard.json

# Create shareable link (snapshot)
curl -X POST http://localhost:3000/api/snapshots \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <api-key>" \
  -d '{
    "dashboard": {...},
    "expires": 3600
  }'
```

## Summary

- **Dashboards** contain panels organized in rows for specific monitoring purposes
- **Panel types**: Time series for trends, Stat/Gauge for current values, Tables for details
- **Variables** make dashboards dynamic and reusable across environments/services
- **Best practices**: Focus dashboards, use appropriate visualizations, set thresholds, add documentation
- **Sharing**: Export JSON, use snapshots, or embed panels
- Dashboard JSON can be version controlled for reproducibility

## Additional Resources

- [Grafana Dashboard Best Practices](https://grafana.com/docs/grafana/latest/dashboards/build-dashboards/best-practices/) - Official guidelines
- [Dashboard JSON Model](https://grafana.com/docs/grafana/latest/dashboards/json-model/) - Complete specification
- [Community Dashboards](https://grafana.com/grafana/dashboards/) - Pre-built dashboards to import

