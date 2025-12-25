# Multi-Stage Dockerfiles

## Learning Objectives

- Understand the multi-stage build concept and its benefits
- Implement multi-stage builds to reduce image size dramatically
- Separate build-time and runtime dependencies effectively
- Apply the builder pattern for compiled languages
- Create practical multi-stage Dockerfiles for Java and Python applications
- Optimize final images for production deployment

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Traditional Docker builds often produce bloated images containing compilers, build tools, and development dependencies that aren't needed at runtime. Multi-stage builds solve this by using separate stages—one for building, one for running—producing final images that are smaller, faster to deploy, and more secure.

As a quality engineer, understanding multi-stage builds helps you optimize CI/CD pipeline performance (smaller images = faster pushes and pulls), reduce attack surface in production, and troubleshoot build issues when stages fail. When Jenkins builds containers, multi-stage Dockerfiles are the standard approach.

## The Concept

### The Problem with Single-Stage Builds

```
┌─────────────────────────────────────────────────────────────────────┐
│                 Single-Stage Build Problem                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   SINGLE-STAGE BUILD                                                │
│   ──────────────────                                                │
│                                                                      │
│   FROM maven:3.9-eclipse-temurin-17                                 │
│   WORKDIR /app                                                       │
│   COPY . .                                                           │
│   RUN mvn package                                                   │
│   CMD ["java", "-jar", "target/app.jar"]                           │
│                                                                      │
│                    ▼                                                 │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Final Image: 800+ MB                                        │   │
│   │                                                              │   │
│   │  Contains:                                                   │   │
│   │  ├── Maven (not needed at runtime)         ~300 MB          │   │
│   │  ├── JDK (JRE would suffice)               ~200 MB          │   │
│   │  ├── Source code (not needed at runtime)   ~10 MB           │   │
│   │  ├── Downloaded dependencies               ~200 MB          │   │
│   │  └── Final JAR (what we actually need)     ~20 MB           │   │
│   │                                                              │   │
│   │  ❌ Too large for production                                │   │
│   │  ❌ Includes build tools (security risk)                    │   │
│   │  ❌ Slow to push/pull                                       │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Multi-Stage Build Solution

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Multi-Stage Build Solution                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   # Stage 1: BUILD                                                  │
│   FROM maven:3.9-eclipse-temurin-17 AS build                        │
│   WORKDIR /app                                                       │
│   COPY . .                                                           │
│   RUN mvn package -DskipTests                                       │
│                                                                      │
│   # Stage 2: RUNTIME                                                │
│   FROM eclipse-temurin:17-jre-alpine                                │
│   COPY --from=build /app/target/app.jar /app.jar                    │
│   CMD ["java", "-jar", "/app.jar"]                                  │
│                                                                      │
│                    ▼                                                 │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Build Stage (discarded)    │  Final Image: ~150 MB        │   │
│   │  ─────────────────────────  │  ──────────────────────      │   │
│   │  • Maven                    │  • JRE Alpine only            │   │
│   │  • JDK                      │  • Final JAR                  │   │
│   │  • Source code              │                               │   │
│   │  • Dependencies             │  ✅ 5x smaller                │   │
│   │                             │  ✅ No build tools            │   │
│   │                             │  ✅ Minimal attack surface    │   │
│   │         ❌ DISCARDED        │  ✅ Fast deployment           │   │
│   └─────────────────────────────┴───────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### How Multi-Stage Builds Work

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Multi-Stage Build Process                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Dockerfile                                                         │
│   ──────────                                                         │
│                                                                      │
│   FROM node:18 AS build          ◄─── Stage 1: "build"              │
│   WORKDIR /app                                                       │
│   COPY package*.json ./                                             │
│   RUN npm ci                                                        │
│   COPY . .                                                           │
│   RUN npm run build                                                 │
│                                                                      │
│   FROM node:18-alpine AS test    ◄─── Stage 2: "test"               │
│   WORKDIR /app                                                       │
│   COPY --from=build /app .                                          │
│   RUN npm test                                                      │
│                                                                      │
│   FROM nginx:alpine              ◄─── Stage 3: Final (no name)      │
│   COPY --from=build /app/dist /usr/share/nginx/html                │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   Key Concepts:                                                      │
│                                                                      │
│   • AS name       Names a stage for reference                       │
│   • COPY --from   Copies from named stage                           │
│   • Only final    Final FROM creates the output image               │
│     stage kept    All others are build-time only                    │
│   • Each FROM     Starts with fresh filesystem                      │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### The Builder Pattern

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Builder Pattern                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Compiled Languages (Go, Java, Rust, C++)                          │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Build Stage                  Runtime Stage                  │   │
│   │  ───────────                  ─────────────                  │   │
│   │                                                              │   │
│   │  • Full SDK/compiler          • Minimal runtime             │   │
│   │  • Source code                • Binary only                 │   │
│   │  • Build tools                • No source                   │   │
│   │  • Dependencies               • No compiler                 │   │
│   │                                                              │   │
│   │  ┌──────────────┐            ┌──────────────┐              │   │
│   │  │   Source     │            │   Binary     │              │   │
│   │  │   Code       │──compile──▶│   Only       │              │   │
│   │  │   + SDK      │            │              │              │   │
│   │  │   800 MB     │            │   50 MB      │              │   │
│   │  └──────────────┘            └──────────────┘              │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Interpreted Languages (Python, Node.js, Ruby)                     │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Build Stage                  Runtime Stage                  │   │
│   │  ───────────                  ─────────────                  │   │
│   │                                                              │   │
│   │  • Build tools (gcc, make)    • Runtime only               │   │
│   │  • Dev dependencies           • Production deps             │   │
│   │  • Test files                 • App code only               │   │
│   │                                                              │   │
│   │  npm ci (all deps)            npm ci --production           │   │
│   │  pip install (dev)            pip install (prod)            │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Java Multi-Stage Build

```dockerfile
# Stage 1: Build with Maven
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime with minimal JRE
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S java && \
    adduser -S java -u 1001

# Copy only the JAR from build stage
COPY --from=build /app/target/*.jar app.jar

USER java

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and compare:
```bash
# Build multi-stage
docker build -t my-java-app:multi .

# Compare sizes
docker images | grep my-java-app
# my-java-app multi  ~150 MB (vs ~800 MB single-stage)
```

### Go Multi-Stage Build

```dockerfile
# Stage 1: Build
FROM golang:1.21-alpine AS build

WORKDIR /app

# Copy go mod files for dependency caching
COPY go.mod go.sum ./
RUN go mod download

# Copy source code
COPY . .

# Build static binary
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o main .

# Stage 2: Minimal runtime
FROM scratch

# Copy binary from build stage
COPY --from=build /app/main /main

# Copy CA certificates for HTTPS
COPY --from=build /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/

EXPOSE 8080
ENTRYPOINT ["/main"]
```

Result:
```bash
docker images | grep my-go-app
# my-go-app latest  ~10 MB (vs ~300 MB with Go SDK)
```

### Python Multi-Stage Build

```dockerfile
# Stage 1: Build dependencies
FROM python:3.11-slim AS build

WORKDIR /app

# Install build dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# Create virtual environment
RUN python -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Stage 2: Runtime
FROM python:3.11-slim

WORKDIR /app

# Copy virtual environment from build stage
COPY --from=build /opt/venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# Create non-root user
RUN useradd --create-home appuser
USER appuser

# Copy application code
COPY --chown=appuser:appuser . .

EXPOSE 8000
CMD ["python", "app.py"]
```

### Node.js Multi-Stage Build

```dockerfile
# Stage 1: Build
FROM node:18 AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build
RUN npm run test

# Stage 2: Production dependencies only
FROM node:18-alpine AS production-deps

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

# Stage 3: Runtime
FROM node:18-alpine

WORKDIR /app

# Copy production dependencies
COPY --from=production-deps /app/node_modules ./node_modules

# Copy built application
COPY --from=build /app/dist ./dist
COPY package*.json ./

RUN addgroup -g 1001 -S nodejs && \
    adduser -S nodejs -u 1001

USER nodejs

EXPOSE 3000
CMD ["node", "dist/server.js"]
```

### React Frontend Multi-Stage Build

```dockerfile
# Stage 1: Build React application
FROM node:18-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# Stage 2: Serve with nginx
FROM nginx:alpine

# Copy built assets from build stage
COPY --from=build /app/build /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

Result:
```bash
docker images | grep react-app
# react-app latest  ~25 MB (vs ~1 GB with node_modules)
```

### Build Specific Stage

```dockerfile
# Named stages for different purposes
FROM node:18 AS base
WORKDIR /app
COPY package*.json ./

FROM base AS development
RUN npm install
COPY . .
CMD ["npm", "run", "dev"]

FROM base AS test
RUN npm ci
COPY . .
CMD ["npm", "test"]

FROM base AS build
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine AS production
COPY --from=build /app/dist /usr/share/nginx/html
```

Build specific stages:
```bash
# Build development image
docker build --target development -t myapp:dev .

# Build test image
docker build --target test -t myapp:test .

# Build production image (default, last stage)
docker build -t myapp:prod .
```

### Copying from External Images

```dockerfile
# Copy binaries from other images
FROM alpine

# Copy from official images
COPY --from=docker:latest /usr/local/bin/docker /usr/local/bin/
COPY --from=docker/compose:latest /usr/local/bin/docker-compose /usr/local/bin/

# Copy from any image
COPY --from=hashicorp/terraform:latest /bin/terraform /usr/local/bin/

CMD ["/bin/sh"]
```

### Multi-Stage with Build Arguments

```dockerfile
# Base stage with version argument
ARG NODE_VERSION=18
FROM node:${NODE_VERSION} AS build

WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .

ARG BUILD_ENV=production
ENV NODE_ENV=${BUILD_ENV}
RUN npm run build

# Runtime stage
FROM node:${NODE_VERSION}-alpine

WORKDIR /app
COPY --from=build /app/dist ./dist
COPY --from=build /app/node_modules ./node_modules

CMD ["node", "dist/index.js"]
```

Build with arguments:
```bash
docker build \
  --build-arg NODE_VERSION=20 \
  --build-arg BUILD_ENV=staging \
  -t myapp:staging .
```

### Comparing Image Sizes

```bash
# Build both versions
docker build -f Dockerfile.single -t myapp:single .
docker build -f Dockerfile.multi -t myapp:multi .

# Compare sizes
docker images myapp --format "table {{.Tag}}\t{{.Size}}"

# Example output:
# TAG      SIZE
# single   845MB
# multi    142MB

# Analyze layers
docker history myapp:multi
docker history myapp:single

# Use dive for detailed analysis
# dive myapp:multi
```

## Summary

- **Multi-stage builds** use multiple FROM instructions to separate build and runtime environments
- **Benefits**: Smaller images, no build tools in production, better security, faster deployments
- **AS keyword** names stages; **COPY --from** copies files between stages
- Only the **final stage** becomes the output image; intermediate stages are discarded
- **Builder pattern** compiles in one stage, runs in minimal stage (Go: scratch, Java: JRE-only)
- Build **specific stages** with `--target` for development, testing, or production
- Common results: 5-10x smaller images compared to single-stage builds

## Additional Resources

- [Multi-stage builds documentation](https://docs.docker.com/build/building/multi-stage/) - Official Docker guide
- [Dive](https://github.com/wagoodman/dive) - Tool for exploring Docker image layers
- [Distroless images](https://github.com/GoogleContainerTools/distroless) - Minimal base images from Google

