# Exercise 5: Week 10 Capstone - Complete CI/CD Pipeline

## Objective

Build a comprehensive CI/CD pipeline that integrates all Week 10 concepts: Docker containerization, automated testing, deployment, and monitoring integration.

---

## Learning Outcomes

By completing this exercise, you will:
- Integrate Docker builds into Jenkins pipelines
- Implement multi-stage pipelines with testing
- Deploy containers and verify health
- Connect CI/CD with monitoring
- Apply DevOps best practices in a real scenario

---

## Prerequisites

- Completed all previous Friday exercises
- Jenkins running at http://localhost:8080
- Thursday's monitoring stack running (Prometheus + Grafana)
- Docker functioning

---

## Time Estimate

60 minutes

---

## The Scenario

You're building a CI/CD pipeline for a Python Flask application that:
1. **Builds** a Docker image
2. **Tests** the container
3. **Deploys** to a staging environment
4. **Monitors** the deployment

This brings together everything from Week 10!

---

## Architecture

```
┌────────────────────────────────────────────────────────────────────────┐
│                         Week 10 Capstone Pipeline                       │
├────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐        │
│   │  Build   │───▶│   Test   │───▶│  Deploy  │───▶│  Monitor │        │
│   │  Stage   │    │  Stage   │    │  Stage   │    │  Stage   │        │
│   └────┬─────┘    └────┬─────┘    └────┬─────┘    └────┬─────┘        │
│        │               │               │               │               │
│        ▼               ▼               ▼               ▼               │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐        │
│   │  Docker  │    │  Unit &  │    │ Container│    │Prometheus│        │
│   │  Build   │    │Integration│   │   Run    │    │ + Grafana│        │
│   └──────────┘    └──────────┘    └──────────┘    └──────────┘        │
│                                                                         │
│   Technologies: Jenkins + Docker + Prometheus + Grafana                │
│                                                                         │
└────────────────────────────────────────────────────────────────────────┘
```

---

## Tasks

### Task 1: Create Application Files (15 minutes)

1. **Create Project Directory on Jenkins Host**
   ```bash
   # Create directory structure
   mkdir -p capstone-app
   cd capstone-app
   ```

2. **Create `app.py`**
   ```python
   from flask import Flask, jsonify, Response
   import os
   import socket
   from datetime import datetime
   from prometheus_client import Counter, Histogram, generate_latest, CONTENT_TYPE_LATEST
   
   app = Flask(__name__)
   
   # Prometheus metrics
   REQUEST_COUNT = Counter('app_requests_total', 'Total requests', ['endpoint', 'method', 'status'])
   REQUEST_LATENCY = Histogram('app_request_latency_seconds', 'Request latency', ['endpoint'])
   
   @app.route('/')
   def home():
       REQUEST_COUNT.labels(endpoint='/', method='GET', status='200').inc()
       return jsonify({
           'message': 'Week 10 Capstone Application',
           'version': os.environ.get('APP_VERSION', '1.0.0'),
           'hostname': socket.gethostname(),
           'timestamp': datetime.now().isoformat()
       })
   
   @app.route('/health')
   def health():
       REQUEST_COUNT.labels(endpoint='/health', method='GET', status='200').inc()
       return jsonify({
           'status': 'healthy',
           'service': 'capstone-app',
           'version': os.environ.get('APP_VERSION', '1.0.0')
       })
   
   @app.route('/ready')
   def ready():
       REQUEST_COUNT.labels(endpoint='/ready', method='GET', status='200').inc()
       return jsonify({'ready': True})
   
   @app.route('/metrics')
   def metrics():
       return Response(generate_latest(), mimetype=CONTENT_TYPE_LATEST)
   
   @app.route('/api/data')
   def api_data():
       REQUEST_COUNT.labels(endpoint='/api/data', method='GET', status='200').inc()
       return jsonify({
           'data': [
               {'id': 1, 'name': 'Item 1'},
               {'id': 2, 'name': 'Item 2'},
               {'id': 3, 'name': 'Item 3'}
           ]
       })
   
   if __name__ == '__main__':
       port = int(os.environ.get('PORT', 8080))
       app.run(host='0.0.0.0', port=port)
   ```

3. **Create `requirements.txt`**
   ```
   flask==3.0.0
   prometheus-client==0.19.0
   pytest==7.4.0
   requests==2.31.0
   ```

4. **Create `test_app.py`**
   ```python
   import pytest
   import json
   
   # Simple unit tests
   def test_home_response():
       """Test that home returns expected structure"""
       expected_keys = ['message', 'version', 'hostname', 'timestamp']
       # Mock test - in real scenario would import app
       assert len(expected_keys) == 4
   
   def test_health_status():
       """Test health check returns healthy"""
       # Mock test
       health_response = {'status': 'healthy'}
       assert health_response['status'] == 'healthy'
   
   def test_api_data_format():
       """Test API returns list of data"""
       # Mock test
       api_response = {'data': [{'id': 1}]}
       assert 'data' in api_response
       assert isinstance(api_response['data'], list)
   
   if __name__ == '__main__':
       pytest.main([__file__, '-v'])
   ```

5. **Create `Dockerfile`**
   ```dockerfile
   # Multi-stage build (Week 10 Wednesday concept!)
   
   # Stage 1: Dependencies
   FROM python:3.11-slim AS builder
   
   WORKDIR /app
   
   COPY requirements.txt .
   RUN pip install --no-cache-dir -r requirements.txt
   
   # Stage 2: Runtime
   FROM python:3.11-slim
   
   WORKDIR /app
   
   # Copy from builder
   COPY --from=builder /usr/local/lib/python3.11/site-packages /usr/local/lib/python3.11/site-packages
   COPY --from=builder /usr/local/bin /usr/local/bin
   
   # Create non-root user (security!)
   RUN useradd --create-home appuser
   USER appuser
   
   # Copy application
   COPY --chown=appuser:appuser app.py .
   COPY --chown=appuser:appuser test_app.py .
   
   # Environment
   ENV APP_VERSION=1.0.0
   ENV PORT=8080
   
   EXPOSE 8080
   
   # Health check
   HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
       CMD python -c "import urllib.request; urllib.request.urlopen('http://localhost:8080/health')"
   
   CMD ["python", "app.py"]
   ```

6. **Create `docker-compose.yml` for the app**
   ```yaml
   version: '3.8'
   
   services:
     capstone-app:
       build: .
       container_name: capstone-app
       ports:
         - "8081:8080"
       environment:
         - APP_VERSION=${BUILD_NUMBER:-1.0.0}
       networks:
         - monitoring_monitoring
       restart: unless-stopped
   
   networks:
     monitoring_monitoring:
       external: true
   ```

**Checkpoint:** Application files created ✓

---

### Task 2: Create Jenkins Pipeline (20 minutes)

1. **Create Pipeline Job**
   - New Item → Name: `week10-capstone`
   - Type: Pipeline → OK

2. **Complete Pipeline Script**
   ```groovy
   pipeline {
       agent any
       
       environment {
           APP_NAME = 'capstone-app'
           DOCKER_IMAGE = 'week10-capstone'
           DOCKER_TAG = "${BUILD_NUMBER}"
           DEPLOY_PORT = '8081'
       }
       
       stages {
           stage('Checkout') {
               steps {
                   echo '📥 Stage 1: Checkout'
                   echo 'Preparing application source...'
                   
                   // Create application files in workspace
                   sh '''
                       mkdir -p app
                       
                       # Create app.py
                       cat > app/app.py << 'PYEOF'
   from flask import Flask, jsonify, Response
   import os
   import socket
   from datetime import datetime
   from prometheus_client import Counter, Histogram, generate_latest, CONTENT_TYPE_LATEST
   
   app = Flask(__name__)
   
   REQUEST_COUNT = Counter('app_requests_total', 'Total requests', ['endpoint', 'method', 'status'])
   
   @app.route('/')
   def home():
       REQUEST_COUNT.labels(endpoint='/', method='GET', status='200').inc()
       return jsonify({
           'message': 'Week 10 Capstone Application',
           'version': os.environ.get('APP_VERSION', '1.0.0'),
           'hostname': socket.gethostname(),
           'timestamp': datetime.now().isoformat()
       })
   
   @app.route('/health')
   def health():
       REQUEST_COUNT.labels(endpoint='/health', method='GET', status='200').inc()
       return jsonify({'status': 'healthy', 'version': os.environ.get('APP_VERSION', '1.0.0')})
   
   @app.route('/metrics')
   def metrics():
       return Response(generate_latest(), mimetype=CONTENT_TYPE_LATEST)
   
   if __name__ == '__main__':
       app.run(host='0.0.0.0', port=int(os.environ.get('PORT', 8080)))
   PYEOF
                       
                       # Create requirements.txt
                       cat > app/requirements.txt << 'REQEOF'
   flask==3.0.0
   prometheus-client==0.19.0
   REQEOF
                       
                       # Create Dockerfile
                       cat > app/Dockerfile << 'DOCKEOF'
   FROM python:3.11-slim
   WORKDIR /app
   COPY requirements.txt .
   RUN pip install --no-cache-dir -r requirements.txt
   COPY app.py .
   ENV APP_VERSION=1.0.0
   ENV PORT=8080
   EXPOSE 8080
   HEALTHCHECK --interval=30s --timeout=3s CMD python -c "import urllib.request; urllib.request.urlopen('http://localhost:8080/health')"
   CMD ["python", "app.py"]
   DOCKEOF
                       
                       echo "Application files created!"
                       ls -la app/
                   '''
               }
           }
           
           stage('Build') {
               steps {
                   echo '🔨 Stage 2: Build Docker Image'
                   
                   sh '''
                       cd app
                       docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                       docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                       
                       echo "Built images:"
                       docker images | grep ${DOCKER_IMAGE}
                   '''
               }
           }
           
           stage('Test') {
               steps {
                   echo '🧪 Stage 3: Test Container'
                   
                   sh '''
                       # Run test container
                       docker run -d --name test-${BUILD_NUMBER} -p 8082:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}
                       
                       # Wait for startup
                       sleep 5
                       
                       echo "Running tests..."
                       
                       # Test home endpoint
                       echo "Test 1: Home endpoint"
                       curl -sf http://localhost:8082/ | grep -q "Capstone" && echo "✓ PASS" || exit 1
                       
                       # Test health endpoint
                       echo "Test 2: Health endpoint"
                       curl -sf http://localhost:8082/health | grep -q "healthy" && echo "✓ PASS" || exit 1
                       
                       # Test metrics endpoint
                       echo "Test 3: Metrics endpoint"
                       curl -sf http://localhost:8082/metrics | grep -q "app_requests_total" && echo "✓ PASS" || exit 1
                       
                       echo "All tests passed!"
                       
                       # Cleanup test container
                       docker stop test-${BUILD_NUMBER} || true
                       docker rm test-${BUILD_NUMBER} || true
                   '''
               }
           }
           
           stage('Deploy') {
               steps {
                   echo '🚀 Stage 4: Deploy to Staging'
                   
                   sh '''
                       # Stop existing deployment
                       docker stop ${APP_NAME} 2>/dev/null || true
                       docker rm ${APP_NAME} 2>/dev/null || true
                       
                       # Deploy new version
                       docker run -d \
                           --name ${APP_NAME} \
                           -p ${DEPLOY_PORT}:8080 \
                           -e APP_VERSION=${DOCKER_TAG} \
                           --restart unless-stopped \
                           ${DOCKER_IMAGE}:${DOCKER_TAG}
                       
                       # Wait for deployment
                       sleep 5
                       
                       # Verify deployment
                       echo "Verifying deployment..."
                       curl -sf http://localhost:${DEPLOY_PORT}/health && echo "Deployment successful!" || exit 1
                       
                       echo "Application deployed at http://localhost:${DEPLOY_PORT}"
                   '''
               }
           }
           
           stage('Verify') {
               steps {
                   echo '✅ Stage 5: Verify Deployment'
                   
                   sh '''
                       echo "Running deployment verification..."
                       
                       # Check container is running
                       docker ps | grep ${APP_NAME}
                       
                       # Get deployment info
                       echo ""
                       echo "Deployment Information:"
                       echo "======================"
                       curl -s http://localhost:${DEPLOY_PORT}/ | python3 -m json.tool
                       
                       echo ""
                       echo "Health Check:"
                       echo "============="
                       curl -s http://localhost:${DEPLOY_PORT}/health | python3 -m json.tool
                       
                       echo ""
                       echo "Metrics Available:"
                       echo "=================="
                       curl -s http://localhost:${DEPLOY_PORT}/metrics | head -20
                   '''
               }
           }
       }
       
       post {
           success {
               echo '''
   ╔══════════════════════════════════════════════════════════╗
   ║           🎉 PIPELINE COMPLETED SUCCESSFULLY! 🎉          ║
   ╠══════════════════════════════════════════════════════════╣
   ║  Application: ${APP_NAME}                                 
   ║  Version: ${DOCKER_TAG}                                   
   ║  URL: http://localhost:${DEPLOY_PORT}                     
   ║                                                           
   ║  Week 10 Integration:                                     
   ║  ✓ Docker (Wednesday)                                    
   ║  ✓ CI/CD Pipeline (Friday)                               
   ║  ✓ Monitoring Ready (Thursday)                           
   ╚══════════════════════════════════════════════════════════╝
               '''
           }
           failure {
               echo '❌ Pipeline failed!'
               sh '''
                   # Cleanup on failure
                   docker stop test-${BUILD_NUMBER} 2>/dev/null || true
                   docker rm test-${BUILD_NUMBER} 2>/dev/null || true
               '''
           }
           cleanup {
               echo 'Cleaning up workspace...'
               sh 'rm -rf app'
           }
       }
   }
   ```

3. **Save and Build**
   - Save the pipeline
   - Click "Build Now"
   - Watch the pipeline execute

**Checkpoint:** Pipeline runs successfully ✓

---

### Task 3: Add to Prometheus Monitoring (15 minutes)

1. **Update Prometheus Configuration**
   
   Add the capstone app to your Thursday monitoring setup:
   
   Edit `monitoring-lab/prometheus/prometheus.yml`:
   ```yaml
   # Add to scrape_configs:
     - job_name: 'capstone-app'
       static_configs:
         - targets: ['host.docker.internal:8081']
       metrics_path: /metrics
       scrape_interval: 10s
   ```
   
   **Note:** Use `host.docker.internal` or your host IP if on Linux.

2. **Restart Prometheus**
   ```bash
   cd monitoring-lab
   docker compose restart prometheus
   ```

3. **Verify in Prometheus**
   - Open http://localhost:9090
   - Go to Status → Targets
   - Check `capstone-app` shows UP

4. **Create Grafana Dashboard Panel**
   - Open Grafana (http://localhost:3000)
   - Edit your Week 10 dashboard
   - Add panel with query:
     ```promql
     rate(app_requests_total[5m])
     ```

**Checkpoint:** Monitoring integrated ✓

---

### Task 4: Test Complete Pipeline (10 minutes)

1. **Generate Load**
   ```bash
   # Generate some traffic
   for i in {1..50}; do
       curl -s http://localhost:8081/ > /dev/null
       curl -s http://localhost:8081/health > /dev/null
       sleep 0.5
   done
   ```

2. **Check Metrics in Prometheus**
   ```promql
   # Total requests
   app_requests_total
   
   # Request rate
   rate(app_requests_total[5m])
   ```

3. **View in Grafana**
   - Check your dashboard shows the traffic

4. **Rebuild Pipeline**
   - Make a minor change or trigger new build
   - Watch version number update
   - Verify deployment updated

---

## Verification Checklist

- [ ] Pipeline completes all 5 stages
- [ ] Docker image built and tagged
- [ ] Tests pass in Test stage
- [ ] Application deployed and accessible at http://localhost:8081
- [ ] Health check returns healthy
- [ ] Metrics endpoint exposes Prometheus metrics
- [ ] Prometheus can scrape the application
- [ ] Pipeline handles failures gracefully

---

## Deliverables

1. Screenshot of successful pipeline (all stages green)
2. Screenshot of application response (curl or browser)
3. Screenshot of Prometheus targets showing capstone-app UP
4. Screenshot of Grafana with app metrics
5. Your complete Jenkinsfile

---

## Week 10 Integration Summary

This capstone demonstrates:

| Day | Concept | Used In Capstone |
|-----|---------|------------------|
| **Tuesday** | AWS | Deployment target concept (staging) |
| **Wednesday** | Docker | Image build, multi-stage, containers |
| **Thursday** | DevOps | Pipeline design, monitoring integration |
| **Thursday** | Prometheus | Metrics collection, /metrics endpoint |
| **Thursday** | Grafana | Visualization, dashboards |
| **Friday** | Jenkins | Pipeline orchestration |

---

## Clean-Up

After capstone completion:

```bash
# Stop capstone app
docker stop capstone-app
docker rm capstone-app

# Remove images
docker rmi week10-capstone:latest

# Or keep running to showcase!
```

---

## Challenge Extensions

1. **Add Integration with Thursday's Stack**
   - Connect app to monitoring network
   - Add app to Grafana dashboard

2. **Blue-Green Deployment**
   - Run two versions simultaneously
   - Switch traffic between them

3. **Notification**
   - Add Slack/Email notification on success/failure

4. **Production Approval**
   - Add `input` step before production deploy

---

## 🎉 Congratulations!

You've completed the Week 10 Capstone! You now understand:
- How to build CI/CD pipelines with Jenkins
- Docker containerization in pipelines
- Automated testing integration
- Monitoring and observability
- The complete DevOps lifecycle

**Next Week:** AI-enhanced development and testing!

