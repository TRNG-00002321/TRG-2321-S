# Dockerfiles

## Learning Objectives

- Understand Dockerfile syntax and purpose
- Master essential Dockerfile instructions: FROM, RUN, COPY, ADD, CMD, ENTRYPOINT, EXPOSE, ENV, WORKDIR
- Explain how build context works and optimize it
- Apply best practices for efficient, secure Dockerfiles
- Understand layer optimization and caching strategies
- Use .dockerignore to exclude files from build context

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Dockerfiles are "infrastructure as code" for your application environment. They define exactly how to build a container image—what base system to use, what software to install, what files to include, and how to run the application. A well-written Dockerfile means faster builds, smaller images, and more secure containers.

As a quality engineer, you'll read Dockerfiles to understand test environments, write Dockerfiles for test applications, and review Dockerfiles for CI/CD pipelines. Understanding Dockerfiles helps you troubleshoot build failures and optimize build times in Jenkins pipelines (covered Friday).

## The Concept

### What is a Dockerfile?

A **Dockerfile** is a text file containing instructions for building a Docker image. Each instruction creates a layer in the resulting image.

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Dockerfile to Image                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Dockerfile                        docker build                    │
│   ──────────                        ────────────                    │
│   ┌─────────────────────┐          ┌─────────────────────┐         │
│   │ FROM python:3.11    │          │     Image           │         │
│   │ WORKDIR /app        │   ────▶  │                     │         │
│   │ COPY . .            │          │   ┌─────────────┐   │         │
│   │ RUN pip install     │          │   │  Layer 4    │   │         │
│   │ CMD ["python",      │          │   ├─────────────┤   │         │
│   │      "app.py"]      │          │   │  Layer 3    │   │         │
│   └─────────────────────┘          │   ├─────────────┤   │         │
│                                     │   │  Layer 2    │   │         │
│                                     │   ├─────────────┤   │         │
│                                     │   │  Layer 1    │   │         │
│                                     │   └─────────────┘   │         │
│                                     └─────────────────────┘         │
│                                                                      │
│   Each instruction → One layer                                      │
│   Layers are cached and reusable                                    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Essential Dockerfile Instructions

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Dockerfile Instructions                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   INSTRUCTION    PURPOSE                       EXAMPLE               │
│   ───────────    ───────                       ───────               │
│                                                                      │
│   FROM           Base image                    FROM python:3.11     │
│                  (required, first)             FROM ubuntu:22.04    │
│                                                                      │
│   WORKDIR        Set working directory         WORKDIR /app         │
│                  (creates if not exists)                            │
│                                                                      │
│   COPY           Copy files from build         COPY . /app          │
│                  context into image            COPY app.py .        │
│                                                                      │
│   ADD            Like COPY, plus:              ADD archive.tar /    │
│                  - Extract archives            ADD https://... /    │
│                  - Download URLs                                    │
│                                                                      │
│   RUN            Execute command during        RUN pip install -r \ │
│                  build (creates layer)             requirements.txt │
│                                                                      │
│   ENV            Set environment variable      ENV APP_ENV=prod     │
│                  (available at build           ENV PORT=8080        │
│                  and runtime)                                       │
│                                                                      │
│   EXPOSE         Document port (metadata)      EXPOSE 80            │
│                  (doesn't publish port)        EXPOSE 443           │
│                                                                      │
│   CMD            Default command to run        CMD ["python", "app.py"]│
│                  (can be overridden)           CMD python app.py    │
│                                                                      │
│   ENTRYPOINT     Configure container           ENTRYPOINT ["python"]│
│                  executable                    CMD ["app.py"]       │
│                                                                      │
│   ARG            Build-time variable           ARG VERSION=1.0      │
│                  (not in final image)          RUN echo $VERSION    │
│                                                                      │
│   USER           Set user for RUN/CMD          USER appuser         │
│                  (security best practice)                           │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Build Context

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Build Context                                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   docker build -t my-app .                                          │
│                       ▲                                             │
│                       │                                             │
│                   Build context (current directory)                 │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Your Project Directory (build context)                      │   │
│   │                                                              │   │
│   │  ├── Dockerfile                                              │   │
│   │  ├── app.py                                                  │   │
│   │  ├── requirements.txt                                        │   │
│   │  ├── src/                                                    │   │
│   │  │   └── ...                                                │   │
│   │  ├── tests/                                                  │   │
│   │  │   └── ...                                                │   │
│   │  ├── node_modules/  ❌ Don't include!                       │   │
│   │  ├── .git/          ❌ Don't include!                       │   │
│   │  └── .dockerignore  ✓ Excludes files from context           │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   The build context is sent to Docker daemon                        │
│   Large context = slow builds                                       │
│   Use .dockerignore to exclude unnecessary files                    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### CMD vs ENTRYPOINT

```
┌─────────────────────────────────────────────────────────────────────┐
│                    CMD vs ENTRYPOINT                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   CMD: Default command (can be overridden)                          │
│   ─────────────────────────────────────────                         │
│                                                                      │
│   Dockerfile:                                                        │
│   CMD ["python", "app.py"]                                          │
│                                                                      │
│   docker run my-app                  # Runs: python app.py          │
│   docker run my-app python test.py   # Runs: python test.py         │
│                                      # (CMD replaced)               │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   ENTRYPOINT: Fixed command (arguments appended)                    │
│   ──────────────────────────────────────────────                    │
│                                                                      │
│   Dockerfile:                                                        │
│   ENTRYPOINT ["python"]                                             │
│   CMD ["app.py"]                                                    │
│                                                                      │
│   docker run my-app                  # Runs: python app.py          │
│   docker run my-app test.py          # Runs: python test.py         │
│                                      # (CMD replaced, ENTRYPOINT stays)│
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   Common Patterns:                                                   │
│                                                                      │
│   # Web server (CMD only)                                           │
│   CMD ["nginx", "-g", "daemon off;"]                                │
│                                                                      │
│   # CLI tool (ENTRYPOINT + CMD)                                     │
│   ENTRYPOINT ["aws"]                                                │
│   CMD ["--help"]                                                    │
│                                                                      │
│   # Script wrapper                                                   │
│   ENTRYPOINT ["/entrypoint.sh"]                                     │
│   CMD ["start"]                                                     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Shell Form vs Exec Form

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Shell Form vs Exec Form                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   EXEC FORM (recommended)                                           │
│   ───────────────────────                                           │
│   CMD ["python", "app.py"]                                          │
│   ENTRYPOINT ["python", "app.py"]                                   │
│                                                                      │
│   • Runs command directly (PID 1)                                   │
│   • Receives signals properly (SIGTERM, etc.)                       │
│   • No shell processing                                             │
│   • Arguments as JSON array                                         │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   SHELL FORM                                                         │
│   ──────────                                                         │
│   CMD python app.py                                                 │
│   RUN apt-get update && apt-get install -y curl                    │
│                                                                      │
│   • Runs via /bin/sh -c                                            │
│   • Shell features available (pipes, variables)                     │
│   • Command doesn't receive signals directly                        │
│   • Environment variable expansion                                  │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   When to use each:                                                  │
│   • CMD/ENTRYPOINT: Use exec form (signal handling)                │
│   • RUN: Shell form often cleaner for complex commands             │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Best Practices

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Dockerfile Best Practices                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   1. USE SPECIFIC BASE IMAGES                                       │
│      ❌ FROM python                                                 │
│      ❌ FROM python:latest                                          │
│      ✅ FROM python:3.11-slim-bookworm                              │
│                                                                      │
│   2. ORDER INSTRUCTIONS BY CHANGE FREQUENCY                         │
│      Least changing → Most changing (for cache)                    │
│                                                                      │
│      FROM python:3.11                                               │
│      WORKDIR /app                                                   │
│      COPY requirements.txt .           # Changes less often        │
│      RUN pip install -r requirements.txt                           │
│      COPY . .                          # Changes frequently        │
│                                                                      │
│   3. MINIMIZE LAYERS                                                │
│      ❌ RUN apt-get update                                         │
│         RUN apt-get install -y curl                                │
│         RUN apt-get install -y wget                                │
│                                                                      │
│      ✅ RUN apt-get update && \                                    │
│            apt-get install -y --no-install-recommends \            │
│            curl wget && \                                          │
│            rm -rf /var/lib/apt/lists/*                             │
│                                                                      │
│   4. DON'T RUN AS ROOT                                              │
│      RUN adduser --disabled-password appuser                       │
│      USER appuser                                                   │
│                                                                      │
│   5. USE .dockerignore                                              │
│      Exclude .git, node_modules, __pycache__, etc.                 │
│                                                                      │
│   6. USE COPY INSTEAD OF ADD                                        │
│      (Unless you need ADD's special features)                      │
│                                                                      │
│   7. SET APPROPRIATE DEFAULTS                                       │
│      ENV, WORKDIR, EXPOSE, USER                                    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Basic Python Dockerfile

```dockerfile
# Dockerfile for a Python application
FROM python:3.11-slim

# Set working directory
WORKDIR /app

# Copy requirements first (cache optimization)
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY . .

# Expose port
EXPOSE 8000

# Run the application
CMD ["python", "app.py"]
```

Build and run:
```bash
# Build image
docker build -t my-python-app .

# Run container
docker run -p 8000:8000 my-python-app
```

### Node.js Dockerfile

```dockerfile
# Dockerfile for a Node.js application
FROM node:18-alpine

# Create app directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy app source
COPY . .

# Create non-root user
RUN addgroup -g 1001 -S nodejs && \
    adduser -S nodejs -u 1001

USER nodejs

# Expose port
EXPOSE 3000

# Start the application
CMD ["node", "server.js"]
```

### Java Dockerfile

```dockerfile
# Dockerfile for a Java application
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Non-root user
RUN addgroup -g 1001 -S java && \
    adduser -S java -u 1001
USER java

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Using Build Arguments

```dockerfile
FROM python:3.11-slim

# Build argument with default
ARG APP_VERSION=1.0.0
ARG BUILD_DATE

# Make version available at runtime
ENV APP_VERSION=${APP_VERSION}

# Label for metadata
LABEL version="${APP_VERSION}" \
      build_date="${BUILD_DATE}" \
      maintainer="team@example.com"

WORKDIR /app
COPY . .

CMD ["python", "app.py"]
```

Build with arguments:
```bash
docker build \
  --build-arg APP_VERSION=2.0.0 \
  --build-arg BUILD_DATE=$(date -u +"%Y-%m-%dT%H:%M:%SZ") \
  -t my-app:2.0.0 .
```

### Environment Variables

```dockerfile
FROM node:18-alpine

WORKDIR /app

# Set default environment variables
ENV NODE_ENV=production
ENV PORT=3000
ENV LOG_LEVEL=info

COPY package*.json ./
RUN npm ci --only=production

COPY . .

EXPOSE ${PORT}

CMD ["node", "server.js"]
```

Override at runtime:
```bash
docker run -e NODE_ENV=development -e LOG_LEVEL=debug my-app
```

### Entrypoint Script Pattern

```dockerfile
FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# Copy entrypoint script
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["start"]
```

docker-entrypoint.sh:
```bash
#!/bin/bash
set -e

# Run database migrations
python manage.py migrate

# Execute the command
exec "$@"
```

### Health Check

```dockerfile
FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf
COPY html /usr/share/nginx/html

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://localhost/ || exit 1

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### .dockerignore File

```
# .dockerignore
# Git
.git
.gitignore

# Node
node_modules
npm-debug.log

# Python
__pycache__
*.pyc
*.pyo
.venv
venv

# IDE
.idea
.vscode
*.swp

# Build artifacts
build
dist
*.egg-info

# Docker
Dockerfile*
docker-compose*
.docker

# Documentation
README.md
docs

# Tests (if not needed in image)
tests
*.test.js
```

### Layer Optimization Example

```dockerfile
# ❌ Bad: Many layers, poor caching
FROM ubuntu:22.04
RUN apt-get update
RUN apt-get install -y python3
RUN apt-get install -y python3-pip
RUN apt-get install -y curl
RUN apt-get install -y wget
COPY requirements.txt /app/
COPY app.py /app/
RUN pip3 install -r /app/requirements.txt

# ✅ Good: Fewer layers, better caching
FROM python:3.11-slim

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        curl \
        wget && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .
CMD ["python", "app.py"]
```

### Debugging Dockerfile Builds

```bash
# Build with verbose output
docker build --progress=plain -t my-app .

# Build without cache (test from scratch)
docker build --no-cache -t my-app .

# Build up to specific stage
docker build --target build -t my-app:build .

# List dangling images (failed builds)
docker images -f "dangling=true"

# Inspect intermediate layers
docker history my-app:latest

# Run shell in intermediate image for debugging
docker run -it <intermediate-image-id> /bin/sh
```

## Summary

- **Dockerfiles** are text files defining how to build Docker images
- **Essential instructions**: FROM, WORKDIR, COPY, RUN, ENV, EXPOSE, CMD, ENTRYPOINT
- **Build context** is sent to daemon; use `.dockerignore` to exclude files
- **CMD** provides default command (overridable); **ENTRYPOINT** provides fixed executable
- **Exec form** `["cmd", "arg"]` is preferred for CMD/ENTRYPOINT (proper signal handling)
- **Best practices**: specific base images, order by change frequency, minimize layers, non-root user

## Additional Resources

- [Dockerfile Reference](https://docs.docker.com/engine/reference/builder/) - Complete instruction documentation
- [Best practices for writing Dockerfiles](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/) - Official guidelines
- [Multi-stage builds](https://docs.docker.com/build/building/multi-stage/) - Advanced build patterns (covered next)

