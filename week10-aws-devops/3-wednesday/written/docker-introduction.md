# Docker Introduction

## Learning Objectives

- Define what Docker is and explain its role in modern software development
- Distinguish between containerization and traditional virtualization
- Describe Docker architecture: daemon, client, and registry
- Identify key benefits of Docker for development, testing, and deployment
- Understand the Docker ecosystem and supporting tools
- Compare Docker Desktop and Docker Engine for different environments

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

"It works on my machine" is the bane of software teams everywhere. Docker eliminates this problem by packaging applications with their dependencies into portable containers. Whether running on a developer's laptop, a test server, or production cloud infrastructure, containers behave identically.

As a quality engineer, Docker transforms your workflow. Test environments become reproducible with a single command. CI/CD pipelines (covered Friday with Jenkins) use Docker to build and test applications in consistent environments. Understanding Docker is essential for modern DevOps practices—it bridges the gap between development and operations.

## The Concept

### What is Docker?

**Docker** is a platform for developing, shipping, and running applications in containers. A container packages an application and all its dependencies into a standardized unit that runs consistently across any environment.

```
┌─────────────────────────────────────────────────────────────────────┐
│                    What Docker Provides                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   PACKAGE                SHIP                   RUN                  │
│   ───────                ────                   ───                  │
│   Bundle application     Distribute via         Execute anywhere    │
│   + dependencies         container registries   Docker is installed │
│   into image                                                         │
│                                                                      │
│   ┌─────────────┐       ┌─────────────┐       ┌─────────────┐       │
│   │  Dockerfile │  ──▶  │  Docker     │  ──▶  │  Running    │       │
│   │  + App Code │  build│  Image      │  push │  Container  │       │
│   │  + Deps     │       │             │  /run │             │       │
│   └─────────────┘       └─────────────┘       └─────────────┘       │
│                                                                      │
│   Developer Laptop ──▶ CI/CD Pipeline ──▶ Production                │
│        Same container runs everywhere                               │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Containerization vs Virtualization

Docker containers are fundamentally different from virtual machines:

```
┌─────────────────────────────────────────────────────────────────────┐
│              Virtual Machines vs Containers                          │
├─────────────────────────────┬───────────────────────────────────────┤
│      VIRTUAL MACHINES       │            CONTAINERS                  │
├─────────────────────────────┼───────────────────────────────────────┤
│                             │                                        │
│   ┌─────┐ ┌─────┐ ┌─────┐  │   ┌─────┐ ┌─────┐ ┌─────┐            │
│   │App 1│ │App 2│ │App 3│  │   │App 1│ │App 2│ │App 3│            │
│   ├─────┤ ├─────┤ ├─────┤  │   ├─────┤ ├─────┤ ├─────┤            │
│   │Bins/│ │Bins/│ │Bins/│  │   │Bins/│ │Bins/│ │Bins/│            │
│   │Libs │ │Libs │ │Libs │  │   │Libs │ │Libs │ │Libs │            │
│   ├─────┤ ├─────┤ ├─────┤  │   └──┬──┘ └──┬──┘ └──┬──┘            │
│   │Guest│ │Guest│ │Guest│  │      │       │       │                │
│   │ OS  │ │ OS  │ │ OS  │  │      └───────┼───────┘                │
│   └──┬──┘ └──┬──┘ └──┬──┘  │              │                        │
│      │       │       │     │      ┌───────┴───────┐                │
│      └───────┼───────┘     │      │  Docker Engine │                │
│              │             │      └───────┬───────┘                │
│      ┌───────┴───────┐     │              │                        │
│      │  Hypervisor   │     │      ┌───────┴───────┐                │
│      └───────┬───────┘     │      │    Host OS    │                │
│              │             │      └───────┬───────┘                │
│      ┌───────┴───────┐     │              │                        │
│      │    Host OS    │     │      ┌───────┴───────┐                │
│      └───────┬───────┘     │      │  Infrastructure│                │
│              │             │      └───────────────┘                │
│      ┌───────┴───────┐     │                                        │
│      │  Infrastructure│     │                                        │
│      └───────────────┘     │                                        │
│                             │                                        │
│   Each VM has full OS      │   Containers share host OS kernel     │
│   (GBs of overhead)        │   (MBs of overhead)                   │
│   Minutes to start         │   Seconds to start                    │
│   Strong isolation         │   Process-level isolation             │
│                             │                                        │
└─────────────────────────────┴───────────────────────────────────────┘
```

**Key Differences:**

| Aspect | Virtual Machines | Containers |
|--------|------------------|------------|
| **Size** | GBs (full OS) | MBs (app + libs only) |
| **Startup** | Minutes | Seconds |
| **Performance** | Near-native with overhead | Near-native |
| **Isolation** | Hardware-level | Process-level |
| **Portability** | Limited | Highly portable |
| **Density** | 10s per host | 100s per host |

### Docker Architecture

Docker uses a client-server architecture:

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Docker Architecture                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────┐                                               │
│   │  Docker Client  │                                               │
│   │  (docker CLI)   │                                               │
│   │                 │                                               │
│   │  docker build   │                                               │
│   │  docker pull    │──────────┐                                    │
│   │  docker run     │          │                                    │
│   └─────────────────┘          │ REST API                           │
│                                │ (Unix socket or TCP)               │
│                                ▼                                    │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    Docker Daemon (dockerd)                   │   │
│   │                                                              │   │
│   │   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │   │
│   │   │   Images    │  │  Containers │  │  Networks   │        │   │
│   │   └─────────────┘  └─────────────┘  └─────────────┘        │   │
│   │                                                              │   │
│   │   ┌─────────────┐  ┌─────────────┐                         │   │
│   │   │   Volumes   │  │  Plugins    │                         │   │
│   │   └─────────────┘  └─────────────┘                         │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                │                                    │
│                                │ Pull images                        │
│                                ▼                                    │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    Docker Registry                           │   │
│   │                   (Docker Hub, ECR, etc.)                    │   │
│   │                                                              │   │
│   │    ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐                  │   │
│   │    │nginx │  │python│  │mysql │  │redis │  ...              │   │
│   │    └──────┘  └──────┘  └──────┘  └──────┘                  │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

**Components:**

| Component | Description |
|-----------|-------------|
| **Docker Client** | CLI tool (docker) that sends commands to daemon |
| **Docker Daemon** | Background service managing containers, images, networks |
| **Docker Registry** | Storage for Docker images (Docker Hub is default public registry) |
| **Docker Objects** | Images, containers, networks, volumes |

### Docker Benefits

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Docker Benefits                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   FOR DEVELOPERS                                                     │
│   ──────────────                                                     │
│   ✓ Consistent environments (dev = test = prod)                     │
│   ✓ Quick onboarding (docker-compose up)                            │
│   ✓ Isolated dependencies (no conflicts between projects)          │
│   ✓ Easy cleanup (docker rm removes everything)                     │
│                                                                      │
│   FOR QUALITY ENGINEERS                                              │
│   ─────────────────────                                              │
│   ✓ Reproducible test environments                                  │
│   ✓ Parallel testing with isolated containers                       │
│   ✓ Easy database/service mocking                                   │
│   ✓ Consistent CI/CD test execution                                 │
│                                                                      │
│   FOR OPERATIONS                                                     │
│   ──────────────                                                     │
│   ✓ Easy deployment and rollback                                    │
│   ✓ Resource efficiency (higher density than VMs)                   │
│   ✓ Scalability (orchestration with Kubernetes)                     │
│   ✓ Microservices architecture support                              │
│                                                                      │
│   FOR EVERYONE                                                       │
│   ────────────                                                       │
│   ✓ Version control for infrastructure                              │
│   ✓ Documentation as code (Dockerfile)                              │
│   ✓ Faster delivery cycles                                          │
│   ✓ Platform independence                                           │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Docker Ecosystem

Docker is part of a larger ecosystem of tools:

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Docker Ecosystem                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   CORE TOOLS                                                         │
│   ──────────                                                         │
│   Docker Engine      Core container runtime                          │
│   Docker CLI         Command-line interface                          │
│   Docker Compose     Multi-container application definition         │
│   Docker Desktop     GUI for Windows/Mac                             │
│                                                                      │
│   REGISTRIES                                                         │
│   ──────────                                                         │
│   Docker Hub         Public registry (official images)               │
│   Amazon ECR         AWS container registry                          │
│   Google GCR         Google container registry                       │
│   Azure ACR          Azure container registry                        │
│   Harbor             Self-hosted registry                            │
│                                                                      │
│   ORCHESTRATION                                                      │
│   ─────────────                                                      │
│   Docker Swarm       Docker's native orchestration                   │
│   Kubernetes         Industry-standard orchestration                 │
│   Amazon ECS         AWS container service                           │
│   Amazon EKS         AWS managed Kubernetes                          │
│                                                                      │
│   RELATED TOOLS                                                      │
│   ─────────────                                                      │
│   Buildah            Build OCI images without daemon                 │
│   Podman             Daemonless container engine                     │
│   containerd         Industry-standard container runtime             │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Docker Desktop vs Docker Engine

Choose the right Docker installation for your environment:

```
┌─────────────────────────────────────────────────────────────────────┐
│              Docker Desktop vs Docker Engine                         │
├─────────────────────────────┬───────────────────────────────────────┤
│      DOCKER DESKTOP         │         DOCKER ENGINE                  │
├─────────────────────────────┼───────────────────────────────────────┤
│                             │                                        │
│   FOR: Windows, macOS       │   FOR: Linux servers                   │
│                             │                                        │
│   • GUI interface           │   • CLI only                           │
│   • Easy installation       │   • Lightweight                        │
│   • Includes Kubernetes     │   • Production-ready                   │
│   • WSL 2 integration       │   • Native performance                 │
│   • Automatic updates       │   • Full control                       │
│                             │                                        │
│   Use for:                  │   Use for:                             │
│   • Local development       │   • Production servers                 │
│   • Learning Docker         │   • CI/CD runners                      │
│   • Testing applications    │   • Cloud deployments                  │
│                             │                                        │
│   License:                  │   License:                             │
│   Free for small business,  │   Open source (Apache 2.0)            │
│   education, personal       │   Free for all uses                    │
│   Paid for large enterprise │                                        │
│                             │                                        │
└─────────────────────────────┴───────────────────────────────────────┘
```

### Core Docker Concepts Preview

Tomorrow we'll dive deeper, but here's a preview of key concepts:

```
┌─────────────────────────────────────────────────────────────────────┐
│                   Core Docker Concepts                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   IMAGE                                                              │
│   ─────                                                              │
│   Read-only template for creating containers                        │
│   Built from a Dockerfile                                            │
│   Composed of layers (each instruction adds a layer)                │
│                                                                      │
│   CONTAINER                                                          │
│   ─────────                                                          │
│   Running instance of an image                                       │
│   Isolated process with its own filesystem, network, etc.           │
│   Can be started, stopped, moved, deleted                           │
│                                                                      │
│   DOCKERFILE                                                         │
│   ──────────                                                         │
│   Text file with instructions to build an image                     │
│   Version-controlled definition of your environment                  │
│   "Infrastructure as Code"                                           │
│                                                                      │
│   REGISTRY                                                           │
│   ────────                                                           │
│   Storage for Docker images                                          │
│   Docker Hub (default), ECR, GCR, private registries               │
│   Push and pull images                                               │
│                                                                      │
│   VOLUME                                                             │
│   ──────                                                             │
│   Persistent data storage                                            │
│   Survives container lifecycle                                       │
│   Can be shared between containers                                   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Verify Docker Installation

```bash
# Check Docker version
docker --version

# More detailed version info
docker version

# System-wide information
docker info

# Run hello-world to verify installation
docker run hello-world
```

### Basic Docker Commands Preview

```bash
# Pull an image from Docker Hub
docker pull nginx

# List downloaded images
docker images

# Run a container
docker run -d --name my-nginx -p 8080:80 nginx

# List running containers
docker ps

# List all containers (including stopped)
docker ps -a

# Stop a container
docker stop my-nginx

# Start a stopped container
docker start my-nginx

# Remove a container
docker rm my-nginx

# Remove an image
docker rmi nginx
```

### Simple Container Example

```bash
# Run a Python script in a container
docker run -it python:3.11 python -c "print('Hello from Docker!')"

# Run an interactive bash shell in Ubuntu
docker run -it ubuntu bash
# Inside the container:
# apt update && apt install -y curl
# curl https://example.com
# exit

# Run a quick test environment
docker run -it --rm alpine sh
# --rm: Remove container when it exits
```

### Container with Port Mapping

```bash
# Run nginx and map port 80 to local port 8080
docker run -d -p 8080:80 --name web nginx

# Access the web server
curl http://localhost:8080

# View container logs
docker logs web

# Clean up
docker stop web
docker rm web
```

### Understand Docker Workflow

```bash
# The typical Docker workflow:

# 1. Write a Dockerfile (we'll cover this tomorrow)
cat > Dockerfile << 'EOF'
FROM python:3.11-slim
WORKDIR /app
COPY app.py .
CMD ["python", "app.py"]
EOF

# 2. Create application file
cat > app.py << 'EOF'
print("Hello from my containerized app!")
EOF

# 3. Build an image
docker build -t my-app:1.0 .

# 4. Run a container from the image
docker run my-app:1.0

# 5. Tag and push to a registry (when ready to share)
# docker tag my-app:1.0 myregistry/my-app:1.0
# docker push myregistry/my-app:1.0
```

### Check System Resources

```bash
# View container resource usage
docker stats

# View disk space used by Docker
docker system df

# Clean up unused resources
docker system prune

# More aggressive cleanup (removes all unused images)
docker system prune -a
```

## Summary

- **Docker** is a platform for packaging and running applications in containers
- **Containers** are lightweight, sharing the host OS kernel (unlike VMs with full guest OS)
- **Docker architecture** consists of client (CLI), daemon (server), and registry (image storage)
- **Key benefits** include consistency across environments, fast startup, and resource efficiency
- **Docker ecosystem** includes Docker Desktop (dev), Docker Engine (prod), registries, and orchestrators
- **Core concepts** to explore: images, containers, Dockerfiles, registries, and volumes
- As a quality engineer, Docker enables reproducible test environments and consistent CI/CD execution

## Additional Resources

- [Docker Documentation](https://docs.docker.com/) - Official comprehensive documentation
- [Docker Hub](https://hub.docker.com/) - Public registry with official images
- [Play with Docker](https://labs.play-with-docker.com/) - Free online Docker playground

