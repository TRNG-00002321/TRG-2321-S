# Exercise 3: Dockerfile for Python Application

## Objective

Create a Dockerfile to containerize a Python Flask application, understanding best practices for image building and layer optimization.

---

## Learning Outcomes

By completing this exercise, you will:
- Write Dockerfiles from scratch
- Understand Dockerfile instructions (FROM, WORKDIR, COPY, RUN, CMD, etc.)
- Build Docker images with proper tagging
- Optimize builds using layer caching
- Create and use `.dockerignore` files
- Run and test containerized applications

---

## Prerequisites

- Completed Exercise 2 (Container Management)
- Docker running
- Text editor

---

## Time Estimate

45 minutes

---

## Project Structure

```
flask-app/
├── app.py              # Flask application
├── requirements.txt    # Python dependencies
├── Dockerfile          # Build instructions
└── .dockerignore       # Excluded files
```

---

## Tasks

### Task 1: Create the Flask Application (10 minutes)

1. **Create Project Directory**
   ```bash
   mkdir -p flask-app
   cd flask-app
   ```

2. **Create `requirements.txt`**
   ```bash
   cat > requirements.txt << 'EOF'
   flask==3.0.0
   gunicorn==21.2.0
   EOF
   ```

3. **Create `app.py`**
   ```bash
   cat > app.py << 'EOF'
   from flask import Flask, jsonify
   import os
   import socket
   from datetime import datetime
   
   app = Flask(__name__)
   
   @app.route('/')
   def home():
       return jsonify({
           'message': 'Hello from Dockerized Flask!',
           'hostname': socket.gethostname(),
           'timestamp': datetime.now().isoformat(),
           'version': os.environ.get('APP_VERSION', '1.0.0')
       })
   
   @app.route('/health')
   def health():
       return jsonify({
           'status': 'healthy',
           'service': 'flask-demo'
       })
   
   @app.route('/env')
   def environment():
       # Show safe environment info
       return jsonify({
           'python_version': os.popen('python --version').read().strip(),
           'app_version': os.environ.get('APP_VERSION', 'not set'),
           'environment': os.environ.get('FLASK_ENV', 'production')
       })
   
   if __name__ == '__main__':
       port = int(os.environ.get('PORT', 5000))
       app.run(host='0.0.0.0', port=port)
   EOF
   ```

4. **Test Locally (Optional)**
   ```bash
   # If Python is installed locally
   pip install flask
   python app.py
   # Open http://localhost:5000
   # Ctrl+C to stop
   ```

**Checkpoint:** Application files created ✓

---

### Task 2: Write the Dockerfile (15 minutes)

1. **Create Basic Dockerfile**
   ```bash
   cat > Dockerfile << 'EOF'
   # Use official Python runtime as base image
   FROM python:3.11-slim
   
   # Set working directory inside container
   WORKDIR /app
   
   # Copy requirements first (better caching)
   COPY requirements.txt .
   
   # Install dependencies
   RUN pip install --no-cache-dir -r requirements.txt
   
   # Copy application code
   COPY app.py .
   
   # Set environment variables
   ENV APP_VERSION=1.0.0
   ENV FLASK_ENV=production
   
   # Document the port (doesn't actually expose it)
   EXPOSE 5000
   
   # Command to run the application
   CMD ["python", "app.py"]
   EOF
   ```

2. **Understand Each Instruction**

   | Instruction | Purpose |
   |-------------|---------|
   | `FROM` | Base image to build upon |
   | `WORKDIR` | Set working directory |
   | `COPY` | Copy files from host to image |
   | `RUN` | Execute commands during build |
   | `ENV` | Set environment variables |
   | `EXPOSE` | Document intended port |
   | `CMD` | Default command when container starts |

3. **Create `.dockerignore`**
   ```bash
   cat > .dockerignore << 'EOF'
   # Python artifacts
   __pycache__/
   *.py[cod]
   *$py.class
   *.so
   .Python
   venv/
   ENV/
   
   # IDE
   .idea/
   .vscode/
   *.swp
   
   # Git
   .git/
   .gitignore
   
   # Docker
   Dockerfile
   .dockerignore
   docker-compose*.yml
   
   # Documentation
   *.md
   docs/
   
   # Tests
   tests/
   test_*.py
   *_test.py
   
   # Misc
   .env
   .env.*
   *.log
   EOF
   ```

**Checkpoint:** Dockerfile created ✓

---

### Task 3: Build the Image (10 minutes)

1. **Build with Tag**
   ```bash
   docker build -t flask-demo:1.0 .
   ```
   
   Watch the build output:
   - Each instruction creates a layer
   - Layers are cached for subsequent builds

2. **View the Image**
   ```bash
   docker images flask-demo
   ```

3. **Inspect Image Layers**
   ```bash
   docker history flask-demo:1.0
   ```

4. **Test Layer Caching**
   
   Make a small change to `app.py`:
   ```bash
   # Change the message
   sed -i 's/Dockerized Flask/Dockerized Flask v2/' app.py
   
   # Rebuild
   docker build -t flask-demo:1.1 .
   ```
   
   Notice: `requirements.txt` layer shows "Using cache"!

5. **Compare Tags**
   ```bash
   docker images flask-demo
   ```

**Checkpoint:** Image built successfully ✓

---

### Task 4: Run and Test (10 minutes)

1. **Run the Container**
   ```bash
   docker run -d \
     --name flask-app \
     -p 5000:5000 \
     flask-demo:1.1
   ```

2. **Test Endpoints**
   ```bash
   # Home endpoint
   curl http://localhost:5000
   
   # Health endpoint
   curl http://localhost:5000/health
   
   # Environment endpoint
   curl http://localhost:5000/env
   ```

3. **View Logs**
   ```bash
   docker logs flask-app
   ```

4. **Run with Different Environment**
   ```bash
   docker rm -f flask-app
   
   docker run -d \
     --name flask-app \
     -p 5000:5000 \
     -e APP_VERSION=2.0.0 \
     -e FLASK_ENV=development \
     flask-demo:1.1
   
   # Check environment
   curl http://localhost:5000/env
   ```

**Checkpoint:** Application runs in container ✓

---

### Task 5: Production Improvements (Bonus)

1. **Use Gunicorn (Production Server)**
   
   Update Dockerfile:
   ```bash
   cat > Dockerfile.prod << 'EOF'
   FROM python:3.11-slim
   
   WORKDIR /app
   
   # Install system dependencies
   RUN apt-get update && apt-get install -y --no-install-recommends \
       curl \
       && rm -rf /var/lib/apt/lists/*
   
   # Copy and install requirements
   COPY requirements.txt .
   RUN pip install --no-cache-dir -r requirements.txt
   
   # Copy application
   COPY app.py .
   
   # Create non-root user
   RUN useradd --create-home appuser
   USER appuser
   
   # Environment
   ENV APP_VERSION=1.0.0
   ENV FLASK_ENV=production
   
   EXPOSE 5000
   
   # Health check
   HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
       CMD curl -f http://localhost:5000/health || exit 1
   
   # Use gunicorn for production
   CMD ["gunicorn", "--bind", "0.0.0.0:5000", "--workers", "2", "app:app"]
   EOF
   ```

2. **Build and Test Production Image**
   ```bash
   docker build -f Dockerfile.prod -t flask-demo:prod .
   
   docker rm -f flask-app
   docker run -d --name flask-app -p 5000:5000 flask-demo:prod
   
   # Test
   curl http://localhost:5000
   
   # Check health status
   docker inspect flask-app --format '{{json .State.Health}}'
   ```

---

## Verification Checklist

- [ ] Created Flask application with multiple endpoints
- [ ] Wrote Dockerfile with proper instructions
- [ ] Created `.dockerignore` file
- [ ] Built image with tag
- [ ] Verified layer caching on rebuild
- [ ] Container runs and serves endpoints
- [ ] Can override environment variables at runtime

---

## Deliverables

1. Your `Dockerfile` contents
2. Output of `docker images flask-demo`
3. Output of `curl http://localhost:5000`
4. Screenshot of `docker history flask-demo:1.0`

---

## Dockerfile Quick Reference

```dockerfile
# Base image
FROM image:tag

# Set working directory
WORKDIR /path

# Copy files
COPY source dest
COPY --chown=user:group source dest

# Run commands (build time)
RUN command

# Set environment variable
ENV KEY=value

# Document port
EXPOSE port

# Add metadata
LABEL key="value"

# Set default user
USER username

# Define volume mount point
VOLUME /path

# Default command
CMD ["executable", "arg1", "arg2"]

# Entry point (can't be overridden easily)
ENTRYPOINT ["executable"]

# Health check
HEALTHCHECK CMD curl -f http://localhost/ || exit 1

# Build argument
ARG VERSION=default
```

---

## Clean-Up

```bash
# Stop and remove container
docker rm -f flask-app

# Remove images
docker rmi flask-demo:1.0 flask-demo:1.1 flask-demo:prod

# Remove project directory (optional)
cd ..
rm -rf flask-app
```

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| Build fails at pip | No requirements.txt | Verify file exists |
| Module not found | Dependencies missing | Check requirements.txt |
| Connection refused | Wrong port | Verify `-p 5000:5000` |
| Changes not reflected | Cache used | Rebuild with `--no-cache` |

---

## Additional Resources

- [Dockerfile Reference](https://docs.docker.com/engine/reference/builder/)
- [Best Practices for Dockerfiles](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/)
- [Docker Python Guide](https://docs.docker.com/language/python/)

