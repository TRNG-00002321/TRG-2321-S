# Exercise 3: Grafana Setup (Pair Programming)

## Objective

Add Grafana to the monitoring stack, configure Prometheus as a data source, and import a community dashboard for system monitoring.

---

## Learning Outcomes

By completing this exercise, you will:
- Deploy Grafana using Docker Compose
- Connect Grafana to Prometheus as a data source
- Navigate the Grafana interface
- Import community dashboards from Grafana.com
- Understand Grafana's role in observability

---

## Prerequisites

- Completed Exercise 2 (Prometheus running)
- Services from Exercise 2 still running
- Partner for pair programming

---

## Time Estimate

30 minutes (Pair Programming)

---

## Pair Programming Roles

- **Driver:** Modifies files, clicks through UI
- **Navigator:** Guides configuration, verifies settings

**Switch roles at 15-minute mark!**

---

## Tasks

### Task 1: Add Grafana to Docker Compose (10 minutes)

1. **Update `docker-compose.yml`**
   
   Add Grafana service:
   ```bash
   cat >> docker-compose.yml << 'EOF'
   
     # Grafana - Visualization and dashboards
     grafana:
       image: grafana/grafana:latest
       container_name: grafana
       ports:
         - "3000:3000"
       volumes:
         - grafana-data:/var/lib/grafana
       environment:
         - GF_SECURITY_ADMIN_USER=admin
         - GF_SECURITY_ADMIN_PASSWORD=admin123
         - GF_USERS_ALLOW_SIGN_UP=false
       depends_on:
         - prometheus
       restart: unless-stopped
       networks:
         - monitoring
   EOF
   ```

2. **Add Grafana Volume**
   
   Update the volumes section at the bottom:
   ```bash
   # Edit docker-compose.yml to add grafana-data volume
   # The volumes section should look like:
   # volumes:
   #   prometheus-data:
   #   grafana-data:
   ```
   
   Or recreate the complete file if needed.

3. **Start Grafana**
   ```bash
   docker compose up -d grafana
   ```

4. **Verify All Services**
   ```bash
   docker compose ps
   ```
   
   Should show 3 services running: prometheus, node-exporter, grafana

5. **Wait for Startup**
   ```bash
   sleep 15
   ```

**Checkpoint:** Grafana container running ✓

---

### Task 2: Access and Configure Grafana (10 minutes)

**Switch roles if you haven't!**

1. **Open Grafana UI**
   
   Open in browser: http://localhost:3000

2. **Login**
   ```
   Username: admin
   Password: admin123
   ```
   
   Skip password change prompt (or set a new one).

3. **Explore the Interface**
   
   Navigate and discuss:
   - **Home Dashboard:** Overview and quick links
   - **Menu (☰):** Dashboards, Explore, Alerting, Connections
   - **Administration:** Settings, plugins, users

4. **Add Prometheus Data Source**
   - Click: `☰ Menu → Connections → Data sources`
   - Click: `Add data source`
   - Select: `Prometheus`

5. **Configure Connection**
   ```
   Name: Prometheus
   
   Connection:
   URL: http://prometheus:9090
   
   (Note: Use container name, not localhost!)
   ```
   
   Leave other settings as default.

6. **Save & Test**
   - Scroll down
   - Click `Save & test`
   - Should show: "Successfully queried the Prometheus API"

**Checkpoint:** Prometheus data source connected ✓

---

### Task 3: Import Node Exporter Dashboard (10 minutes)

1. **Navigate to Import**
   - Click: `☰ Menu → Dashboards`
   - Click: `New → Import`

2. **Import from Grafana.com**
   ```
   Import via grafana.com: 1860
   ```
   
   Click "Load"
   
   This is the popular "Node Exporter Full" dashboard.

3. **Configure Import**
   - Name: Keep default or customize
   - Folder: General
   - Prometheus: Select "Prometheus" (your data source)
   - Click "Import"

4. **Explore the Dashboard**
   
   The dashboard shows:
   - CPU usage by mode
   - Memory utilization
   - Disk I/O
   - Network traffic
   - System load
   - Uptime

5. **Discussion Points**
   - Which panels are most useful for troubleshooting?
   - What patterns do you see in the metrics?
   - How would you customize this for your needs?

6. **Try Another Dashboard (Optional)**
   
   Import ID: `3662` - Prometheus 2.0 Overview
   
   This shows Prometheus's own metrics.

**Checkpoint:** Dashboard imported and displaying data ✓

---

### Task 4: Test Queries in Explore (5 minutes)

1. **Open Explore**
   - Click: `☰ Menu → Explore`

2. **Select Data Source**
   - Ensure "Prometheus" is selected

3. **Run Queries**
   
   Try these queries:
   ```promql
   # Basic metric
   up
   
   # CPU usage (if node-exporter working)
   rate(node_cpu_seconds_total{mode="user"}[5m])
   
   # Prometheus HTTP requests
   rate(prometheus_http_requests_total[5m])
   ```

4. **Switch Visualization**
   - Try Table view vs Graph view
   - Adjust time range (Last 15 minutes, Last 1 hour)

---

## Verification Checklist

- [ ] Grafana container running
- [ ] Can login to Grafana at http://localhost:3000
- [ ] Prometheus data source configured
- [ ] "Save & test" shows successful connection
- [ ] Node Exporter dashboard imported (ID: 1860)
- [ ] Dashboard displays metrics
- [ ] Queries work in Explore view

---

## Deliverables

1. Screenshot of Grafana dashboard showing system metrics
2. Screenshot of data source configuration showing successful test
3. Brief note: Which dashboard panels do you find most useful?

---

## Quick Reference

### Grafana Credentials
```
URL: http://localhost:3000
User: admin
Pass: admin123
```

### Prometheus Data Source URL
```
http://prometheus:9090
```
(Use Docker service name, not localhost)

### Popular Dashboard IDs
| ID | Name | Use |
|----|------|-----|
| 1860 | Node Exporter Full | System metrics |
| 3662 | Prometheus 2.0 Overview | Prometheus metrics |
| 11074 | Node Exporter for Prometheus | Alternative system view |

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| "Data source is not working" | Wrong URL | Use `http://prometheus:9090` |
| No data in dashboard | Time range | Adjust to "Last 15 minutes" |
| Login failed | Wrong password | Use `admin123` or check docker-compose |
| Import fails | No data source | Configure data source first |

### Check Connectivity from Grafana

```bash
# Enter Grafana container
docker exec -it grafana sh

# Test Prometheus connection
wget -qO- http://prometheus:9090/api/v1/status/config | head
exit
```

---

## Keep Services Running

**Don't stop the services!** You need them for the next exercises.

Current stack:
- Prometheus: http://localhost:9090
- Node Exporter: http://localhost:9100/metrics
- Grafana: http://localhost:3000

---

## Additional Resources

- [Grafana Documentation](https://grafana.com/docs/grafana/latest/)
- [Grafana Dashboards](https://grafana.com/grafana/dashboards/)
- [Data Source Configuration](https://grafana.com/docs/grafana/latest/datasources/prometheus/)

