# Exercise 4: Custom Grafana Dashboard (Pair Programming)

## Objective

Build a custom Grafana dashboard from scratch with multiple panel types, applying visualization best practices and creating a cohesive monitoring view.

---

## Learning Outcomes

By completing this exercise, you will:
- Create Grafana dashboards from scratch
- Configure different panel types (Stat, Gauge, Time Series, Table)
- Write PromQL queries for visualization
- Apply dashboard design best practices
- Use variables for dynamic filtering
- Export dashboards for sharing

---

## Prerequisites

- Completed Exercise 3 (Grafana running with data source)
- Grafana accessible at http://localhost:3000
- Partner for pair programming

---

## Time Estimate

45 minutes (Pair Programming)

---

## Pair Programming Roles

- **Driver:** Creates panels, writes queries
- **Navigator:** Guides layout, suggests improvements, checks results

**Switch roles every 15 minutes!**

---

## Tasks

### Task 1: Create New Dashboard (5 minutes)

1. **Create Dashboard**
   - Click: `☰ Menu → Dashboards`
   - Click: `New → New Dashboard`

2. **Save Dashboard**
   - Click: Floppy disk icon (💾) or `Ctrl+S`
   - Name: `Week 10 Custom Monitoring`
   - Folder: General
   - Click "Save"

3. **Access Dashboard Settings**
   - Click: Gear icon (⚙️) → Settings
   - Add description: "Custom monitoring dashboard for DevOps Week 10"
   - Save dashboard

**Checkpoint:** Empty dashboard created ✓

---

### Task 2: Create "Quick Stats" Row (15 minutes)

Build a row of stat panels showing key health indicators.

1. **Add Panel**
   - Click: `+ Add visualization`
   - Select: `Prometheus` data source

2. **Panel 1: Prometheus Up Status**
   
   Query:
   ```promql
   up{job="prometheus"}
   ```
   
   Panel settings:
   - Title: `Prometheus Status`
   - Visualization: `Stat`
   - Value options → Calculation: `Last`
   - Standard options → Unit: `short`
   - Thresholds:
     - 1 = Green
     - 0 = Red
   - Value mappings:
     - 1 → "UP"
     - 0 → "DOWN"

3. **Panel 2: Node Exporter Status**
   
   Query:
   ```promql
   up{job="node-exporter"}
   ```
   
   Same configuration as Panel 1, Title: `Node Exporter Status`

4. **Panel 3: Prometheus Scrape Duration**
   
   Query:
   ```promql
   avg(prometheus_target_interval_length_seconds{quantile="0.99"})
   ```
   
   Settings:
   - Title: `Scrape Duration (p99)`
   - Visualization: `Stat`
   - Unit: `seconds (s)`
   - Thresholds:
     - 0-15 = Green
     - 15-30 = Yellow
     - 30+ = Red

5. **Panel 4: Total Scrape Targets**
   
   Query:
   ```promql
   count(up)
   ```
   
   Settings:
   - Title: `Total Targets`
   - Visualization: `Stat`
   - Unit: `none`

6. **Arrange Panels**
   - Drag panels to arrange in a single row
   - Resize to equal widths (4 panels across)

**Checkpoint:** Quick stats row complete ✓

---

### Task 3: Create Gauge Panels (10 minutes)

**Switch roles!**

1. **Add Row**
   - Hover at top of dashboard
   - Click: `+ Add row`
   - Name row: "System Metrics" (click row title to edit)

2. **Panel 5: Memory Usage Gauge**
   
   Query (for systems with node-exporter):
   ```promql
   100 - ((node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes) * 100)
   ```
   
   OR if no node-exporter, use:
   ```promql
   # Simulated for demo
   50 + (15 * sin(time() / 300))
   ```
   
   Settings:
   - Title: `Memory Usage`
   - Visualization: `Gauge`
   - Min: 0, Max: 100
   - Unit: `Percent (0-100)`
   - Thresholds:
     - 0-70 = Green
     - 70-90 = Yellow
     - 90-100 = Red

3. **Panel 6: CPU Usage Gauge**
   
   Query:
   ```promql
   100 - (avg(rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)
   ```
   
   OR simulated:
   ```promql
   30 + (20 * sin(time() / 200))
   ```
   
   Same gauge settings, Title: `CPU Usage`

4. **Resize and Position**
   - Make gauges similar size
   - Place side by side

**Checkpoint:** Gauge panels complete ✓

---

### Task 4: Create Time Series Panel (10 minutes)

**Switch roles!**

1. **Panel 7: HTTP Requests Over Time**
   
   Query:
   ```promql
   rate(prometheus_http_requests_total[5m])
   ```
   
   Settings:
   - Title: `Prometheus HTTP Request Rate`
   - Visualization: `Time series`
   - Legend: `{{handler}}` (shows endpoint)
   - Graph styles → Line width: 2
   - Graph styles → Fill opacity: 20

2. **Add Second Query to Same Panel**
   
   Click "Add query":
   ```promql
   sum(rate(prometheus_http_requests_total[5m]))
   ```
   
   Legend: `Total`
   
   This shows both individual endpoints and total.

3. **Panel 8: Scrape Duration Over Time**
   
   Query:
   ```promql
   prometheus_target_interval_length_seconds{quantile="0.99"}
   ```
   
   Settings:
   - Title: `Scrape Interval (p99)`
   - Visualization: `Time series`
   - Unit: `seconds`
   - Legend: `{{job}}`

4. **Make Panels Full Width**
   - Drag to span most of dashboard width

**Checkpoint:** Time series panels complete ✓

---

### Task 5: Create Table Panel (5 minutes)

1. **Panel 9: All Targets Table**
   
   Query:
   ```promql
   up
   ```
   
   Settings:
   - Title: `Scrape Targets Status`
   - Visualization: `Table`
   - Transform data → Add transformation:
     - Labels to fields
   
   Table display:
   - Column: `job` - visible
   - Column: `instance` - visible
   - Column: `Value` - visible, rename to "Status"
   - Value mappings: 1 → "UP", 0 → "DOWN"

**Checkpoint:** Table panel complete ✓

---

### Task 6: Final Touches (5 minutes)

1. **Set Time Range**
   - Click time picker (top right)
   - Set to: `Last 30 minutes`
   - Refresh: `10s`

2. **Add Annotations** (Optional)
   - Settings → Annotations → Add
   - Name: Prometheus Restarts
   - Query: `changes(prometheus_build_info[1h])`

3. **Save Dashboard**
   - Click 💾 or `Ctrl+S`
   - Add description of changes

4. **Export Dashboard**
   - Settings → JSON Model
   - Copy and save for backup

---

## Dashboard Layout Guide

```
┌─────────────────────────────────────────────────────────────────┐
│                       Week 10 Custom Monitoring                  │
├────────────┬────────────┬────────────┬────────────────────────────┤
│ Prometheus │   Node     │  Scrape    │    Total                  │
│   Status   │ Exporter   │ Duration   │   Targets                 │
│    [UP]    │   [UP]     │   [14s]    │     [2]                   │
├────────────┴────────────┴────────────┴────────────────────────────┤
│                      System Metrics Row                          │
├────────────────────────┬─────────────────────────────────────────┤
│   Memory Usage         │        CPU Usage                        │
│     [GAUGE]           │         [GAUGE]                         │
├────────────────────────┴─────────────────────────────────────────┤
│              Prometheus HTTP Request Rate                         │
│              [TIME SERIES GRAPH]                                  │
├──────────────────────────────────────────────────────────────────┤
│              Scrape Interval Over Time                           │
│              [TIME SERIES GRAPH]                                  │
├──────────────────────────────────────────────────────────────────┤
│              Scrape Targets Status                               │
│              [TABLE]                                             │
└──────────────────────────────────────────────────────────────────┘
```

---

## Verification Checklist

- [ ] Dashboard created and saved
- [ ] At least 4 Stat panels showing key metrics
- [ ] 2 Gauge panels for system metrics
- [ ] 2 Time Series graphs
- [ ] 1 Table panel
- [ ] Appropriate thresholds set
- [ ] Dashboard exported as JSON

---

## Deliverables

1. Screenshot of completed dashboard
2. Exported JSON of dashboard
3. Brief explanation: Why did you choose these visualizations?

---

## Panel Type Quick Reference

| Type | Use Case | Key Settings |
|------|----------|--------------|
| Stat | Single value, status | Thresholds, value mappings |
| Gauge | Percentage, limits | Min/Max, thresholds |
| Time Series | Trends over time | Legend, line styles |
| Table | List of values | Transformations |
| Bar Gauge | Comparison | Orientation, thresholds |
| Heatmap | Distribution | Color scheme |

---

## Dashboard Design Tips

1. **Top Row:** Most critical status indicators
2. **Second Row:** System health (CPU, memory, disk)
3. **Middle:** Time series for trends
4. **Bottom:** Detailed tables

**Color Consistency:**
- Green: Good/Healthy
- Yellow: Warning
- Red: Critical/Error

---

## Clean-Up

Keep the dashboard! You'll reference it in the next exercise.

Export your dashboard JSON as a backup:
1. Dashboard Settings → JSON Model
2. Copy all content
3. Save to `week10-dashboard.json`

---

## Additional Resources

- [Grafana Panel Types](https://grafana.com/docs/grafana/latest/panels-visualizations/)
- [Dashboard Best Practices](https://grafana.com/docs/grafana/latest/best-practices/best-practices-for-creating-dashboards/)
- [Transformations](https://grafana.com/docs/grafana/latest/panels-visualizations/query-transform-data/transform-data/)

