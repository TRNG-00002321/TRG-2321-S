# Exercise 4: Multi-Stage Build Optimization

## Objective

Convert single-stage Dockerfiles to multi-stage builds, significantly reducing image size and improving security by separating build and runtime environments.

---

## Learning Outcomes

By completing this exercise, you will:
- Understand the multi-stage build concept
- Convert single-stage Dockerfiles to multi-stage
- Measure and compare image sizes
- Apply security best practices (non-root users, minimal runtime)
- Understand when and why to use multi-stage builds

---

## Prerequisites

- Completed Exercise 3 (Dockerfile Python)
- Docker running
- Understanding of basic Dockerfile concepts

---

## Time Estimate

45 minutes

---

## The Problem

Single-stage images include everything used during build:
- Build tools (compilers, make, etc.)
- Development dependencies
- Package managers and caches
- Source code and intermediate files

This leads to:
- **Large images** (100s of MB to GBs)
- **Security risks** (more software = more vulnerabilities)
- **Slow deployments** (larger downloads)

---

## Tasks

### Task 1: Understand the Problem (10 minutes)

1. **Create Project Directory**
   ```bash
   mkdir -p multistage-demo
   cd multistage-demo
   ```

2. **Create a Simple Python Application**
   ```bash
   cat > app.py << 'EOF'
   from flask import Flask, jsonify
   import os
   
   app = Flask(__name__)
   
   @app.route('/')
   def hello():
       return jsonify({
           'message': 'Optimized Multi-Stage Build!',
           'version': os.environ.get('APP_VERSION', '1.0.0')
       })
   
   @app.route('/health')
   def health():
       return jsonify({'status': 'healthy'})
   
   if __name__ == '__main__':
       app.run(host='0.0.0.0', port=5000)
   EOF
   ```

3. **Create Requirements with Native Dependencies**
   ```bash
   cat > requirements.txt << 'EOF'
   flask==3.0.0
   gunicorn==21.2.0
   numpy==1.26.0
   EOF
   ```
   
   Note: `numpy` requires compilation, demonstrating build-time dependencies.

4. **Create Single-Stage Dockerfile**
   ```bash
   cat > Dockerfile.single << 'EOF'
   # Single-stage: includes all build tools in final image
   FROM python:3.11
   
   WORKDIR /app
   
   # Install build dependencies
   RUN apt-get update && apt-get install -y \
       build-essential \
       gcc \
       python3-dev \
       && rm -rf /var/lib/apt/lists/*
   
   COPY requirements.txt .
   RUN pip install -r requirements.txt
   
   COPY app.py .
   
   ENV APP_VERSION=1.0.0
   EXPOSE 5000
   
   CMD ["python", "app.py"]
   EOF
   ```

5. **Build and Check Size**
   ```bash
   docker build -f Dockerfile.single -t demo-single .
   
   # Check the size
   docker images demo-single
   ```
   
   Expected: **~1GB or more!**

**Checkpoint:** Single-stage image is large ✓

---

### Task 2: Create Multi-Stage Dockerfile (15 minutes)

1. **Create Multi-Stage Dockerfile**
   ```bash
   cat > Dockerfile.multi << 'EOF'
   # ============================================
   # Stage 1: Build Stage
   # ============================================
   FROM python:3.11 AS build
   
   WORKDIR /app
   
   # Install build dependencies (only in build stage)
   RUN apt-get update && apt-get install -y \
       build-essential \
       gcc \
       python3-dev \
       && rm -rf /var/lib/apt/lists/*
   
   # Create virtual environment
   RUN python -m venv /opt/venv
   
   # Activate venv by adding to PATH
   ENV PATH="/opt/venv/bin:$PATH"
   
   # Install Python dependencies
   COPY requirements.txt .
   RUN pip install --no-cache-dir -r requirements.txt
   
   # ============================================
   # Stage 2: Runtime Stage
   # ============================================
   FROM python:3.11-slim
   
   WORKDIR /app
   
   # Copy virtual environment from build stage
   COPY --from=build /opt/venv /opt/venv
   
   # Activate venv
   ENV PATH="/opt/venv/bin:$PATH"
   
   # Create non-root user for security
   RUN useradd --create-home --shell /bin/bash appuser
   
   # Copy application code
   COPY --chown=appuser:appuser app.py .
   
   # Switch to non-root user
   USER appuser
   
   # Environment variables
   ENV APP_VERSION=2.0.0
   ENV PYTHONDONTWRITEBYTECODE=1
   ENV PYTHONUNBUFFERED=1
   
   EXPOSE 5000
   
   # Use gunicorn for production
   CMD ["gunicorn", "--bind", "0.0.0.0:5000", "--workers", "2", "app:app"]
   EOF
   ```

2. **Build Multi-Stage Image**
   ```bash
   docker build -f Dockerfile.multi -t demo-multi .
   ```

3. **Compare Sizes**
   ```bash
   docker images | grep demo
   ```
   
   Expected output (approximate):
   ```
   demo-single    latest    1.2GB
   demo-multi     latest    200MB
   ```
   
   **~6x smaller!**

**Checkpoint:** Multi-stage image is much smaller ✓

---

### Task 3: Test Both Images (10 minutes)

1. **Test Single-Stage**
   ```bash
   docker run -d --name single-test -p 5001:5000 demo-single
   curl http://localhost:5001
   curl http://localhost:5001/health
   ```

2. **Test Multi-Stage**
   ```bash
   docker run -d --name multi-test -p 5002:5000 demo-multi
   curl http://localhost:5002
   curl http://localhost:5002/health
   ```

3. **Compare Running Containers**
   ```bash
   docker stats --no-stream
   ```

4. **Verify Non-Root User in Multi-Stage**
   ```bash
   # Single-stage runs as root
   docker exec single-test whoami
   # Output: root
   
   # Multi-stage runs as appuser
   docker exec multi-test whoami
   # Output: appuser
   ```

**Checkpoint:** Both images work, but multi-stage is more secure ✓

---

### Task 4: Analyze the Images (5 minutes)

1. **View Layer History**
   ```bash
   echo "=== Single Stage ===" 
   docker history demo-single --format "{{.Size}}\t{{.CreatedBy}}" | head -10
   
   echo ""
   echo "=== Multi Stage ==="
   docker history demo-multi --format "{{.Size}}\t{{.CreatedBy}}" | head -10
   ```

2. **Check What's Inside**
   ```bash
   # Single-stage has gcc, build tools
   docker run --rm demo-single which gcc
   # Output: /usr/bin/gcc
   
   # Multi-stage does NOT have gcc
   docker run --rm demo-multi which gcc
   # Output: (nothing - gcc not installed)
   ```

3. **Security Comparison**
   - Single-stage: gcc, make, development headers = attack surface
   - Multi-stage: minimal runtime only = smaller attack surface

---

### Task 5: Advanced Multi-Stage Patterns (Bonus)

1. **Build Specific Stage**
   ```bash
   # Build only the build stage (for debugging)
   docker build -f Dockerfile.multi --target build -t demo-build-stage .
   
   # Check it has build tools
   docker run --rm demo-build-stage which gcc
   ```

2. **Multiple Build Stages**
   ```bash
   cat > Dockerfile.advanced << 'EOF'
   # Stage 1: Dependencies
   FROM python:3.11 AS deps
   WORKDIR /app
   RUN python -m venv /opt/venv
   ENV PATH="/opt/venv/bin:$PATH"
   COPY requirements.txt .
   RUN pip install --no-cache-dir -r requirements.txt
   
   # Stage 2: Test (optional)
   FROM deps AS test
   COPY . .
   # RUN pytest tests/  # Would run tests here
   
   # Stage 3: Production
   FROM python:3.11-slim AS production
   COPY --from=deps /opt/venv /opt/venv
   ENV PATH="/opt/venv/bin:$PATH"
   WORKDIR /app
   COPY app.py .
   USER nobody
   CMD ["gunicorn", "--bind", "0.0.0.0:5000", "app:app"]
   EOF
   
   # Build production stage (default)
   docker build -f Dockerfile.advanced -t demo-advanced .
   
   # Build test stage specifically
   docker build -f Dockerfile.advanced --target test -t demo-test .
   ```

---

## Size Comparison Summary

| Image | Size | Notes |
|-------|------|-------|
| `python:3.11` | ~1 GB | Full Python with build tools |
| `python:3.11-slim` | ~150 MB | Minimal Python runtime |
| `python:3.11-alpine` | ~50 MB | Alpine-based, smallest |
| `demo-single` | ~1.2 GB | With build deps included |
| `demo-multi` | ~200 MB | Runtime only |

---

## Verification Checklist

- [ ] Created single-stage Dockerfile
- [ ] Built and measured single-stage image size
- [ ] Created multi-stage Dockerfile
- [ ] Achieved significant size reduction
- [ ] Both images run correctly
- [ ] Multi-stage runs as non-root user
- [ ] Understand the multi-stage concept

---

## Deliverables

1. Both Dockerfiles (`Dockerfile.single` and `Dockerfile.multi`)
2. Screenshot of `docker images | grep demo` showing size comparison
3. Output of `whoami` from both containers
4. Brief explanation (2-3 sentences): Why is the multi-stage image smaller?

---

## When to Use Multi-Stage Builds

**Use when:**
- Application requires compilation (Go, Java, Rust, C/C++)
- Native dependencies need build tools (Python with C extensions)
- You want to minimize production image size
- Security is important (reduce attack surface)

**May not need when:**
- Simple scripts with no compilation
- Image size is not a concern
- Development/debugging environments

---

## Clean-Up

```bash
# Stop containers
docker rm -f single-test multi-test

# Remove images
docker rmi demo-single demo-multi demo-build-stage demo-advanced demo-test 2>/dev/null

# Clean up directory
cd ..
rm -rf multistage-demo
```

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| COPY --from fails | Wrong stage name | Check AS name matches |
| Module not found | venv not copied | Verify COPY --from path |
| Permission denied | Non-root user | Ensure files are owned by user |

---

## Additional Resources

- [Multi-Stage Builds Documentation](https://docs.docker.com/build/building/multi-stage/)
- [Docker Best Practices](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/)
- [Python Docker Best Practices](https://snyk.io/blog/best-practices-containerizing-python-docker/)

