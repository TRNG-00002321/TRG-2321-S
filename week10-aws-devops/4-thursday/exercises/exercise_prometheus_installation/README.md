# Exercise 2: Prometheus Installation (Pair Programming)

## Objective

Install and configure Prometheus using Docker Compose, set up scrape targets including Node Exporter, and verify metrics collection.

---

## Learning Outcomes

By completing this exercise, you will:
- Deploy Prometheus using Docker Compose
- Configure scrape targets in prometheus.yml
- Set up Node Exporter for system metrics
- Navigate the Prometheus web interface
- Verify targets are being scraped successfully

---

## Prerequisites

- Docker and Docker Compose installed
- Completed Wednesday's Docker exercises
- Partner for pair programming

---

## Time Estimate

45 minutes (Pair Programming)

---

## Pair Programming Roles

- **Driver:** Types commands, creates files
- **Navigator:** Reviews configuration, checks documentation

**Switch roles at the 20-minute mark!**

---

## Tasks

### Task 1: Set Up Project Structure (5 minutes)

1. **Create Working Directory**
   ```bash
   mkdir -p monitoring-lab
   cd monitoring-lab
   ```

2. **Create Directory Structure**
   ```bash
   mkdir -p prometheus grafana
   ```

**Checkpoint:** Directory structure ready ✓

---

### Task 2: Create Prometheus Configuration (15 minutes)

1. **Create `prometheus/prometheus.yml`**
   ```bash
   cat > prometheus/prometheus.yml << 'EOF'
   # Prometheus Configuration File
   
   global:
     # How often to scrape targets
     scrape_interval: 15s
     
     # How often to evaluate alerting rules
     evaluation_interval: 15s
     
     # Labels added to any time series or alert
     external_labels:
       monitor: 'week10-monitoring'
   
   # Alerting configuration (we'll add Alertmanager later)
   alerting:
     alertmanagers:
       - static_configs:
           - targets: []
   
   # Rule files (alert rules)
   rule_files:
     # - "alert_rules.yml"
   
   # Scrape configurations
   scrape_configs:
     # Prometheus monitors itself
     - job_name: 'prometheus'
       static_configs:
         - targets: ['localhost:9090']
       metrics_path: /metrics
       scheme: http
   
     # Node Exporter for system metrics
     - job_name: 'node-exporter'
       static_configs:
         - targets: ['node-exporter:9100']
       scrape_interval: 10s
   
     # Demo application (we'll add this later)
     # - job_name: 'demo-app'
     #   static_configs:
     #     - targets: ['demo-app:8080']
     #   metrics_path: /metrics
   EOF
   ```

2. **Understand the Configuration**

   Discuss with your partner:
   - What is `scrape_interval`?
   - What does `job_name` represent?
   - Why do we scrape Prometheus itself?

**Checkpoint:** Prometheus config created ✓

---

### Task 3: Create Docker Compose File (10 minutes)

**Switch roles if you haven't!**

1. **Create `docker-compose.yml`**
   ```bash
   cat > docker-compose.yml << 'EOF'
   version: '3.8'
   
   services:
     # Prometheus - Metrics collection and storage
     prometheus:
       image: prom/prometheus:latest
       container_name: prometheus
       ports:
         - "9090:9090"
       volumes:
         - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml:ro
         - prometheus-data:/prometheus
       command:
         - '--config.file=/etc/prometheus/prometheus.yml'
         - '--storage.tsdb.path=/prometheus'
         - '--web.enable-lifecycle'
         - '--web.enable-admin-api'
       restart: unless-stopped
       networks:
         - monitoring
   
     # Node Exporter - System metrics
     node-exporter:
       image: prom/node-exporter:latest
       container_name: node-exporter
       ports:
         - "9100:9100"
       volumes:
         - /proc:/host/proc:ro
         - /sys:/host/sys:ro
         - /:/rootfs:ro
       command:
         - '--path.procfs=/host/proc'
         - '--path.sysfs=/host/sys'
         - '--path.rootfs=/rootfs'
         - '--collector.filesystem.mount-points-exclude=^/(sys|proc|dev|host|etc)($$|/)'
       restart: unless-stopped
       networks:
         - monitoring
   
   networks:
     monitoring:
       driver: bridge
   
   volumes:
     prometheus-data:
   EOF
   ```

   **Note for Windows users:** Node Exporter may have limited functionality. You can comment out the volumes section or skip Node Exporter.

**Checkpoint:** Docker Compose file created ✓

---

### Task 4: Start the Stack (5 minutes)

1. **Launch Services**
   ```bash
   docker compose up -d
   ```

2. **Check Status**
   ```bash
   docker compose ps
   ```
   
   Both services should show "running".

3. **View Logs**
   ```bash
   # Prometheus logs
   docker compose logs prometheus
   
   # Node Exporter logs
   docker compose logs node-exporter
   ```

4. **Wait for Startup**
   ```bash
   sleep 10
   ```

**Checkpoint:** Services running ✓

---

### Task 5: Explore Prometheus UI (10 minutes)

1. **Open Prometheus Web UI**
   
   Open in browser: http://localhost:9090

2. **Check Targets Status**
   - Navigate to: `Status → Targets`
   - Verify both targets show "UP":
     - `prometheus` (1/1 up)
     - `node-exporter` (1/1 up)
   
   **Discuss:** What does it mean if a target is "DOWN"?

3. **Explore Configuration**
   - Navigate to: `Status → Configuration`
   - See your prometheus.yml loaded

4. **Basic Query**
   - Go to: `Graph` tab
   - Enter query: `up`
   - Click "Execute"
   - View in both Table and Graph views
   
   **Discussion:** What does the `up` metric tell us?

5. **Explore Metrics**
   - Try queries:
     ```promql
     # All metrics from Prometheus
     prometheus_http_requests_total
     
     # Node Exporter CPU (if available)
     node_cpu_seconds_total
     
     # Memory info
     node_memory_MemTotal_bytes
     ```

**Checkpoint:** Prometheus UI accessible and targets UP ✓

---

### Task 6: Verify Metrics Collection (5 minutes)

1. **View Raw Metrics**
   ```bash
   # Prometheus metrics
   curl http://localhost:9090/metrics | head -50
   
   # Node Exporter metrics
   curl http://localhost:9100/metrics | head -50
   ```

2. **Count Metrics**
   ```bash
   # How many metrics does Node Exporter expose?
   curl -s http://localhost:9100/metrics | grep "^node_" | wc -l
   ```

3. **Test in Prometheus**
   
   In Prometheus UI, query:
   ```promql
   # Count of scrapes
   prometheus_target_scrape_pool_targets
   
   # Scrape duration
   prometheus_target_scrape_pool_sync_total
   ```

---

## Verification Checklist

- [ ] `prometheus/prometheus.yml` created with correct syntax
- [ ] `docker-compose.yml` created
- [ ] Prometheus container running
- [ ] Node Exporter container running
- [ ] Prometheus UI accessible at http://localhost:9090
- [ ] Both targets showing "UP" status
- [ ] Basic PromQL query executed successfully

---

## Deliverables

1. Screenshot of Prometheus Targets page showing all targets UP
2. Your `prometheus.yml` configuration file
3. Output of a successful PromQL query

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| Target DOWN | Service not running | Check `docker compose logs <service>` |
| Connection refused | Wrong port/host | Use container name in config, not localhost |
| Config not loading | YAML syntax error | Validate YAML, check indentation |
| Node Exporter no data (Windows) | Incompatible | Skip Node Exporter or use alternative |

### Common PromQL Errors

```promql
# Wrong: Metric doesn't exist
no_such_metric

# Right: Check metric exists first
prometheus_build_info
```

---

## Configuration Reference

### Prometheus.yml Structure

```yaml
global:
  scrape_interval: <duration>     # Default: 1m
  evaluation_interval: <duration> # Default: 1m

scrape_configs:
  - job_name: <string>            # Required
    static_configs:
      - targets: ['host:port']
    metrics_path: <path>          # Default: /metrics
    scheme: <http|https>          # Default: http
    scrape_interval: <duration>   # Override global
```

---

## Keep Services Running

**Don't stop the services!** You'll need them for the next exercises.

If you need to restart later:
```bash
cd monitoring-lab
docker compose up -d
```

---

## Additional Resources

- [Prometheus Getting Started](https://prometheus.io/docs/prometheus/latest/getting_started/)
- [Configuration Documentation](https://prometheus.io/docs/prometheus/latest/configuration/configuration/)
- [Node Exporter](https://github.com/prometheus/node_exporter)

