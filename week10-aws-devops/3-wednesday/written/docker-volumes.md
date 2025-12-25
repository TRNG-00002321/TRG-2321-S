# Docker Volumes

## Learning Objectives

- Explain why data persistence is important in containerized environments
- Distinguish between volume types: named volumes, bind mounts, and tmpfs
- Create and manage Docker volumes using CLI commands
- Understand volume drivers and their use cases
- Share data between multiple containers
- Implement backup strategies for volume data

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Containers are ephemeral by design—when a container is removed, its filesystem is deleted. But real applications need persistent data: databases store records, applications save user uploads, and logs must survive container restarts. Docker volumes solve this by providing persistent storage that exists outside the container lifecycle.

As a quality engineer, understanding volumes is essential for managing test data, setting up databases for integration tests, and ensuring data persists across container restarts in test environments. When CI/CD pipelines run database tests, volumes ensure test fixtures are available.

## The Concept

### The Data Persistence Problem

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Container Data Problem                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   WITHOUT VOLUMES                                                    │
│   ───────────────                                                    │
│                                                                      │
│   Day 1: Create container, data is written                          │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Container: postgres                                         │   │
│   │  ┌─────────────────────────────────────────────────────┐   │   │
│   │  │  Container Filesystem                                │   │   │
│   │  │  /var/lib/postgresql/data                           │   │   │
│   │  │  └── Database files: 500MB                          │   │   │
│   │  └─────────────────────────────────────────────────────┘   │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Day 2: Container removed                                          │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                                                              │   │
│   │             ❌ ALL DATA LOST ❌                              │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   WITH VOLUMES                                                       │
│   ────────────                                                       │
│                                                                      │
│   Day 1: Container with volume mount                                │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Container: postgres          Volume: postgres-data         │   │
│   │  ┌───────────────────────┐   ┌───────────────────────┐     │   │
│   │  │  /var/lib/postgresql  │───│  Persistent storage   │     │   │
│   │  │  /data (mount point)  │   │  on host              │     │   │
│   │  └───────────────────────┘   └───────────────────────┘     │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Day 2: Container removed, new container created                   │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  New Container             Same Volume: postgres-data       │   │
│   │  ┌───────────────────────┐   ┌───────────────────────┐     │   │
│   │  │  /var/lib/postgresql  │───│  ✅ Data preserved    │     │   │
│   │  │  /data                │   │  All 500MB intact     │     │   │
│   │  └───────────────────────┘   └───────────────────────┘     │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Types of Docker Storage

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Docker Storage Types                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   1. VOLUMES (Recommended for persistence)                          │
│   ─────────────────────────────────────────                         │
│   docker run -v my-volume:/data image                               │
│                                                                      │
│   ┌──────────────────┐                                              │
│   │    Container     │                                              │
│   │    /data         │─────▶ Volume: my-volume                     │
│   └──────────────────┘       /var/lib/docker/volumes/my-volume     │
│                                                                      │
│   • Managed by Docker                                               │
│   • Portable across hosts (with volume drivers)                     │
│   • Can be shared between containers                                │
│   • Best for database data, app data                                │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   2. BIND MOUNTS (Host filesystem access)                           │
│   ───────────────────────────────────────                           │
│   docker run -v /host/path:/container/path image                   │
│                                                                      │
│   ┌──────────────────┐                                              │
│   │    Container     │                                              │
│   │    /app          │─────▶ /home/user/myproject                  │
│   └──────────────────┘       (host filesystem)                      │
│                                                                      │
│   • Direct host filesystem access                                   │
│   • Changes reflect immediately                                     │
│   • Path must exist on host                                         │
│   • Best for development (live code reload)                        │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   3. TMPFS MOUNTS (Memory only)                                     │
│   ─────────────────────────────                                     │
│   docker run --tmpfs /tmp image                                     │
│                                                                      │
│   ┌──────────────────┐                                              │
│   │    Container     │                                              │
│   │    /tmp          │─────▶ Host memory (RAM)                     │
│   └──────────────────┘       Not persisted                          │
│                                                                      │
│   • Stored in memory only                                           │
│   • Very fast                                                       │
│   • Data lost when container stops                                  │
│   • Best for sensitive data, temp files                            │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Volume vs Bind Mount Comparison

| Feature | Volumes | Bind Mounts |
|---------|---------|-------------|
| **Location** | Docker-managed (/var/lib/docker/volumes) | Anywhere on host |
| **Creation** | Docker creates automatically | Must exist on host |
| **Management** | docker volume commands | Manual (OS commands) |
| **Portability** | Can use volume drivers | Host-specific |
| **Permissions** | Docker handles | Host permissions apply |
| **Performance** | Optimized for Docker | OS-dependent |
| **Best for** | Production data | Development, config |

### Volume Drivers

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Volume Drivers                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Volume drivers extend Docker storage to external systems          │
│                                                                      │
│   DRIVER              STORAGE                  USE CASE             │
│   ──────              ───────                  ────────             │
│   local (default)     Host filesystem          Single host          │
│   nfs                 NFS server               Shared storage       │
│   amazon-ebs          AWS EBS volumes          AWS workloads        │
│   azure-file          Azure File storage       Azure workloads      │
│   flocker             Multi-host storage       Kubernetes alt       │
│   rexray              Dell EMC storage         Enterprise           │
│                                                                      │
│   Example: Create volume with specific driver                       │
│   docker volume create --driver nfs \                               │
│     --opt type=nfs \                                                │
│     --opt o=addr=192.168.1.100,rw \                                │
│     --opt device=:/exports/data \                                  │
│     shared-data                                                     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Creating and Using Volumes

```bash
# Create a named volume
docker volume create my-data

# List volumes
docker volume ls

# Inspect volume
docker volume inspect my-data

# Run container with volume
docker run -d \
  --name postgres-db \
  -v my-data:/var/lib/postgresql/data \
  -e POSTGRES_PASSWORD=secret \
  postgres:15

# Volume persists after container removal
docker rm -f postgres-db

# New container can use same volume
docker run -d \
  --name postgres-db-new \
  -v my-data:/var/lib/postgresql/data \
  -e POSTGRES_PASSWORD=secret \
  postgres:15
```

### Bind Mounts for Development

```bash
# Mount current directory for live development
docker run -d \
  --name dev-app \
  -v $(pwd):/app \
  -w /app \
  -p 3000:3000 \
  node:18 \
  npm run dev

# Changes to local files immediately available in container

# Mount specific config file
docker run -d \
  --name nginx \
  -v $(pwd)/nginx.conf:/etc/nginx/nginx.conf:ro \
  -p 80:80 \
  nginx

# :ro = read-only mount
```

### Volume Mount Syntax Options

```bash
# Named volume (Docker manages location)
docker run -v my-volume:/data image

# Bind mount with absolute path
docker run -v /host/path:/container/path image

# Bind mount with relative path (current directory)
docker run -v $(pwd)/data:/app/data image

# Read-only mount
docker run -v my-volume:/data:ro image

# Using --mount (more explicit, recommended)
docker run --mount type=volume,source=my-volume,target=/data image

docker run --mount type=bind,source=/host/path,target=/container/path image

docker run --mount type=bind,source="$(pwd)"/app,target=/app,readonly image
```

### Sharing Volumes Between Containers

```bash
# Create shared volume
docker volume create shared-data

# Container 1: Writer
docker run -d \
  --name writer \
  -v shared-data:/data \
  alpine \
  sh -c "while true; do date >> /data/log.txt; sleep 5; done"

# Container 2: Reader
docker run -it --rm \
  -v shared-data:/data:ro \
  alpine \
  tail -f /data/log.txt

# Both containers access the same data
```

### Database with Persistent Volume

```bash
# PostgreSQL with persistent data
docker volume create postgres-data

docker run -d \
  --name postgres \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=secretpassword \
  -e POSTGRES_DB=myapp \
  -v postgres-data:/var/lib/postgresql/data \
  -p 5432:5432 \
  postgres:15

# MySQL with persistent data
docker volume create mysql-data

docker run -d \
  --name mysql \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=myapp \
  -e MYSQL_USER=appuser \
  -e MYSQL_PASSWORD=apppassword \
  -v mysql-data:/var/lib/mysql \
  -p 3306:3306 \
  mysql:8

# MongoDB with persistent data
docker volume create mongo-data

docker run -d \
  --name mongo \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=secretpassword \
  -v mongo-data:/data/db \
  -p 27017:27017 \
  mongo:6
```

### tmpfs for Temporary Data

```bash
# Use tmpfs for sensitive temporary data
docker run -d \
  --name secure-app \
  --tmpfs /tmp:rw,noexec,nosuid,size=100m \
  my-app

# tmpfs options:
# rw       - read-write
# noexec   - can't execute binaries
# nosuid   - no setuid
# size     - limit size

# Useful for:
# - Temporary files
# - Sensitive data that shouldn't persist
# - Session data
```

### Volume Backup and Restore

```bash
# Backup volume to tar file
docker run --rm \
  -v postgres-data:/data \
  -v $(pwd):/backup \
  alpine \
  tar czf /backup/postgres-backup.tar.gz -C /data .

# Restore volume from tar file
docker volume create postgres-data-restored

docker run --rm \
  -v postgres-data-restored:/data \
  -v $(pwd):/backup \
  alpine \
  sh -c "cd /data && tar xzf /backup/postgres-backup.tar.gz"

# Copy volume to another volume
docker run --rm \
  -v source-volume:/source:ro \
  -v dest-volume:/dest \
  alpine \
  cp -a /source/. /dest/
```

### Volume Cleanup

```bash
# Remove specific volume (must not be in use)
docker volume rm my-data

# Remove all unused volumes
docker volume prune

# Force remove volume (even if container exists but stopped)
docker volume rm -f my-data

# Remove volume with container
docker rm -v my-container  # Also removes anonymous volumes

# List dangling (unused) volumes
docker volume ls -f dangling=true

# Remove dangling volumes
docker volume prune
```

### Anonymous vs Named Volumes

```bash
# Anonymous volume (random name, hard to manage)
docker run -v /data image
# Creates volume with random ID like:
# abc123def456789...

# Named volume (explicit name, easy to manage)
docker run -v my-app-data:/data image

# Dockerfile VOLUME creates anonymous volumes
# Better to use named volumes with docker run -v

# List all volumes with names
docker volume ls --format "table {{.Name}}\t{{.Driver}}"
```

### Volumes with Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    image: my-app
    volumes:
      # Named volume
      - app-data:/data
      # Bind mount for development
      - ./src:/app/src
      # Read-only config
      - ./config/app.conf:/etc/app/app.conf:ro

  db:
    image: postgres:15
    volumes:
      - db-data:/var/lib/postgresql/data
    environment:
      POSTGRES_PASSWORD: secret

# Declare named volumes
volumes:
  app-data:
  db-data:
    driver: local
    driver_opts:
      type: none
      o: bind
      device: /mnt/db-storage
```

```bash
# Manage compose volumes
docker compose up -d
docker compose down           # Keeps volumes
docker compose down -v        # Removes volumes
docker compose down --volumes # Same as -v
```

## Summary

- **Volumes** provide persistent storage that survives container lifecycle
- **Three types**: Volumes (Docker-managed), Bind mounts (host paths), tmpfs (memory)
- **Named volumes** are preferred for production data; bind mounts for development
- **Volume drivers** extend storage to NFS, cloud storage, and other systems
- **Sharing volumes** enables data exchange between containers
- **Backup volumes** by mounting to temporary containers with tar commands
- Use `docker volume prune` to clean up unused volumes

## Additional Resources

- [Manage data in Docker](https://docs.docker.com/storage/) - Official storage documentation
- [Use volumes](https://docs.docker.com/storage/volumes/) - Volume-specific guide
- [Volume drivers](https://docs.docker.com/engine/extend/plugins_volume/) - Third-party storage plugins

