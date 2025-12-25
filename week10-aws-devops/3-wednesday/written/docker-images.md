# Docker Images

## Learning Objectives

- Understand what Docker images are and how they're structured in layers
- Pull images from Docker registries (Docker Hub and others)
- Apply image naming and tagging conventions correctly
- List, inspect, and remove images effectively
- Navigate Docker Hub and find official images
- Examine image contents and layer history

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Docker images are the blueprints from which containers run. Understanding images—how they're built, tagged, stored, and distributed—is fundamental to working with Docker. When debugging container issues, you need to know what's in the image. When optimizing CI/CD pipelines, you need to understand image layers and caching. When securing deployments, you need to verify image sources.

As a quality engineer, you'll pull images to create test environments, inspect images to verify correct versions, and understand image tagging to ensure consistent deployments across environments.

## The Concept

### What is a Docker Image?

A **Docker Image** is a read-only template containing everything needed to run an application: code, runtime, libraries, environment variables, and configuration files. When you run an image, Docker creates a container—a running instance of that image.

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Docker Image Concept                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   IMAGE = Template (read-only)                                      │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Docker Image: python:3.11                                   │   │
│   │                                                              │   │
│   │  Contains:                                                   │   │
│   │  ├── Base operating system (Debian slim)                    │   │
│   │  ├── Python 3.11 interpreter                                │   │
│   │  ├── pip package manager                                    │   │
│   │  ├── Standard library                                       │   │
│   │  └── Environment configuration                              │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                    │                                                 │
│                    │ docker run                                     │
│                    ▼                                                 │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Container 1        Container 2        Container 3          │   │
│   │  (running)          (running)          (running)            │   │
│   │                                                              │   │
│   │  Each container is an isolated instance                     │   │
│   │  All from the same image template                           │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Image Layers

Images are composed of **layers**—each layer represents a filesystem change. Layers are:
- **Read-only**: Once created, never modified
- **Shared**: Multiple images can share common layers
- **Cached**: Unchanged layers don't need rebuilding

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Image Layer Structure                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   FROM python:3.11-slim                                             │
│   COPY requirements.txt .                                           │
│   RUN pip install -r requirements.txt                               │
│   COPY . /app                                                        │
│   CMD ["python", "/app/main.py"]                                    │
│                                                                      │
│                    ▼                                                 │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Layer 5: CMD instruction            (metadata only, 0 KB)  │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  Layer 4: COPY . /app                (your application)     │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  Layer 3: pip install                (installed packages)   │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  Layer 2: COPY requirements.txt      (single file)          │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  Layer 1: python:3.11-slim           (base image layers)    │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Benefits of layers:                                               │
│   ✓ Efficient storage (shared between images)                      │
│   ✓ Fast builds (cached layers not rebuilt)                        │
│   ✓ Efficient distribution (only changed layers transferred)       │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Image Naming and Tagging

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Image Naming Convention                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Full image reference:                                             │
│   [REGISTRY/][NAMESPACE/]REPOSITORY[:TAG][@DIGEST]                  │
│                                                                      │
│   Examples:                                                          │
│   ─────────                                                          │
│                                                                      │
│   nginx                                                              │
│   └── library/nginx:latest from docker.io                          │
│                                                                      │
│   nginx:1.25                                                         │
│   └── library/nginx:1.25 from docker.io                            │
│                                                                      │
│   mycompany/myapp:v2.0                                              │
│   └── mycompany namespace, myapp repo, v2.0 tag                    │
│                                                                      │
│   gcr.io/project/image:tag                                          │
│   └── Google Container Registry                                     │
│                                                                      │
│   123456789.dkr.ecr.us-east-1.amazonaws.com/myapp:latest           │
│   └── AWS Elastic Container Registry                                │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   Tag Best Practices:                                               │
│   ─────────────────                                                 │
│   • Avoid :latest in production (not immutable)                    │
│   • Use semantic versioning: v1.2.3                                │
│   • Use git SHA for traceability: abc123                           │
│   • Use date stamps: 2024-01-15                                     │
│   • Combine approaches: v1.2.3-abc123                              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Docker Hub and Registries

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Docker Registries                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   DOCKER HUB (docker.io) - Default public registry                  │
│   ─────────────────────────────────────────────────                 │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Official Images           Verified Publisher                │   │
│   │  ────────────────          ──────────────────                │   │
│   │  • Maintained by Docker    • Company-maintained              │   │
│   │  • "library" namespace     • Verified organization           │   │
│   │  • Examples:               • Examples:                       │   │
│   │    nginx, python,            bitnami/postgresql,             │   │
│   │    postgres, redis           hashicorp/terraform             │   │
│   │                                                              │   │
│   │  Community Images                                            │   │
│   │  ────────────────                                            │   │
│   │  • User-uploaded                                             │   │
│   │  • Varying quality                                           │   │
│   │  • Format: username/imagename                                │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   PRIVATE REGISTRIES                                                │
│   ──────────────────                                                │
│   • AWS ECR (Elastic Container Registry)                           │
│   • Google GCR (Google Container Registry)                         │
│   • Azure ACR (Azure Container Registry)                           │
│   • GitHub Container Registry (ghcr.io)                            │
│   • Self-hosted: Harbor, GitLab Registry                           │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Image Tags

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Understanding Image Tags                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Example: python image tags                                        │
│   ──────────────────────────                                        │
│                                                                      │
│   python:3.11          ─▶ Full Python 3.11 (Debian-based)          │
│   python:3.11-slim     ─▶ Minimal Python 3.11 (~150MB smaller)     │
│   python:3.11-alpine   ─▶ Alpine Linux base (~100MB)               │
│   python:3.11-bullseye ─▶ Debian Bullseye base                     │
│   python:3.11.7        ─▶ Specific patch version                   │
│   python:latest        ─▶ Latest stable (currently 3.12)           │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   Tag Patterns:                                                      │
│   ─────────────                                                      │
│                                                                      │
│   VERSION TAGS                                                       │
│   3.11, 3.11.7, 3.11.7-slim                                        │
│   More specific = more predictable                                  │
│                                                                      │
│   VARIANT TAGS                                                       │
│   -slim     Smaller image (fewer packages)                         │
│   -alpine   Alpine Linux base (smallest, musl libc)                │
│   -bullseye Debian Bullseye                                        │
│   -bookworm Debian Bookworm                                        │
│                                                                      │
│   SPECIAL TAGS                                                       │
│   latest    Default tag (changes over time)                        │
│   stable    Stable release                                          │
│   edge      Latest development                                      │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Pulling Images

```bash
# Pull image from Docker Hub
docker pull nginx

# Pull specific version
docker pull nginx:1.25

# Pull with full reference
docker pull docker.io/library/nginx:1.25

# Pull from different registry
docker pull gcr.io/google-containers/busybox:1.27

# Pull by digest (immutable, exact image)
docker pull nginx@sha256:abc123...

# Pull all tags for an image (rare, large download)
# docker pull --all-tags nginx

# See pull progress
docker pull python:3.11
# Shows each layer being downloaded
```

### Listing and Inspecting Images

```bash
# List all images
docker images
docker image ls

# List with size information
docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"

# List only image IDs
docker images -q

# Filter images
docker images nginx
docker images --filter "dangling=true"    # Untagged images
docker images --filter "before=nginx:1.25"

# Show image history (layers)
docker history nginx:1.25
docker history --no-trunc nginx:1.25  # Full commands

# Detailed inspection
docker inspect nginx:1.25

# Get specific information
docker inspect nginx:1.25 --format '{{.Config.ExposedPorts}}'
docker inspect nginx:1.25 --format '{{.Config.Env}}'
docker inspect nginx:1.25 --format '{{.RepoTags}}'
docker inspect nginx:1.25 --format '{{.Size}}'
```

### Image Tagging

```bash
# Tag an existing image
docker tag nginx:1.25 my-nginx:v1

# Tag for private registry
docker tag nginx:1.25 myregistry.com/nginx:1.25

# Tag for AWS ECR
docker tag my-app:latest 123456789.dkr.ecr.us-east-1.amazonaws.com/my-app:latest

# Multiple tags for same image
docker tag my-app:latest my-app:v1.0.0
docker tag my-app:latest my-app:production

# Verify tags
docker images my-app
# Shows same IMAGE ID with different tags
```

### Removing Images

```bash
# Remove image by name
docker rmi nginx:1.25

# Remove image by ID
docker rmi abc123def456

# Force remove (even if containers exist)
docker rmi -f nginx:1.25

# Remove multiple images
docker rmi nginx:1.25 nginx:1.24 nginx:1.23

# Remove dangling images (untagged)
docker image prune

# Remove all unused images
docker image prune -a

# Remove images matching filter
docker images --filter "dangling=true" -q | xargs docker rmi

# Remove all images (careful!)
docker rmi $(docker images -q)
```

### Docker Hub Operations

```bash
# Search Docker Hub
docker search nginx
docker search --filter "is-official=true" postgres
docker search --limit 5 python

# Login to Docker Hub
docker login
# Enter username and password

# Login to other registry
docker login myregistry.com
docker login 123456789.dkr.ecr.us-east-1.amazonaws.com

# Push image to registry
docker push myusername/my-app:v1.0
docker push myregistry.com/my-app:v1.0

# Logout
docker logout
docker logout myregistry.com
```

### Examining Image Contents

```bash
# View image layers
docker history python:3.11

# See each layer with full commands
docker history --no-trunc python:3.11

# Inspect image configuration
docker inspect python:3.11

# Extract specific info
docker inspect python:3.11 --format '{{.Config.Cmd}}'
docker inspect python:3.11 --format '{{json .Config.Env}}'

# Export image to tar file
docker save nginx:1.25 -o nginx-1.25.tar

# Import image from tar file
docker load -i nginx-1.25.tar

# View image filesystem (create temp container)
docker run --rm -it nginx:1.25 ls -la /
docker run --rm -it python:3.11 pip list

# Extract file from image
docker run --rm nginx:1.25 cat /etc/nginx/nginx.conf > nginx.conf.local
```

### Working with Digests

```bash
# Get image digest
docker images --digests nginx

# Pull by digest (immutable reference)
docker pull nginx@sha256:4c0fdaa8b6341bfdeca5f18f7837462c80cbc15b153e8d5c6c9f08a5b9cfe7a2

# Reference in Dockerfile (for reproducibility)
# FROM nginx@sha256:4c0fdaa8b6341bfdeca5f18f7837462c80cbc15b153e8d5c6c9f08a5b9cfe7a2

# Why use digests?
# - Tags can be overwritten
# - Digest is content-addressable (changes if content changes)
# - Guarantees exact same image every time
```

### Image Comparison and Analysis

```bash
# Compare image sizes
docker images --format "{{.Repository}}:{{.Tag}} {{.Size}}" | sort -k2 -h

# Find large images
docker images --format "{{.Repository}}:{{.Tag}}\t{{.Size}}" | sort -t$'\t' -k2 -h | tail -10

# Compare two images
docker history nginx:1.25 > nginx-1.25-layers.txt
docker history nginx:1.24 > nginx-1.24-layers.txt
diff nginx-1.25-layers.txt nginx-1.24-layers.txt

# Analyze image with dive (third-party tool)
# Install: https://github.com/wagoodman/dive
# dive nginx:1.25
# Shows layer-by-layer filesystem changes

# Clean up
rm nginx-*.txt
```

### Common Image Patterns

```bash
# Use slim images for smaller size
docker pull python:3.11-slim
# vs python:3.11 (much larger)

# Use alpine for smallest size (different libc)
docker pull python:3.11-alpine
# Note: May have compatibility issues

# Pin specific versions
docker pull python:3.11.7-slim-bullseye
# More reproducible than python:3.11-slim

# Multi-architecture images (docker handles automatically)
docker pull --platform linux/amd64 nginx:1.25
docker pull --platform linux/arm64 nginx:1.25

# Check available architectures
docker manifest inspect nginx:1.25
```

## Summary

- **Docker images** are read-only templates containing everything needed to run an application
- **Layers** are stacked filesystem changes; they're shared, cached, and efficient
- **Image naming**: `[registry/][namespace/]repository[:tag][@digest]`
- **Tags** identify versions; avoid `:latest` in production for predictability
- **Docker Hub** hosts official images, verified publishers, and community images
- Use `docker pull` to download, `docker images` to list, `docker rmi` to remove
- **Digests** provide immutable references for reproducible builds

## Additional Resources

- [Docker Hub](https://hub.docker.com/) - Official image registry
- [Docker Image Reference](https://docs.docker.com/engine/reference/commandline/image/) - Command documentation
- [Best practices for writing Dockerfiles](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/) - Official guidelines

