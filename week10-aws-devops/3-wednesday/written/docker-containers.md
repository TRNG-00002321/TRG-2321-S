# Docker Containers

## Learning Objectives

- Master container lifecycle commands: run, start, stop, and rm
- Understand interactive vs detached container modes
- Configure port mapping to expose container services
- Pass environment variables to containers
- Apply container naming conventions and resource limits
- Execute commands inside running containers using exec

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Containers are the runtime manifestation of Docker images—they're where your applications actually execute. Mastering container management is essential for development, testing, and debugging. When a test environment needs a database, you spin up a container. When debugging an issue, you exec into a running container. When cleaning up resources, you remove containers properly.

As a quality engineer, you'll manage containers constantly: running test dependencies, inspecting application behavior, and ensuring clean test environments. These skills directly translate to CI/CD pipelines where containers are created, tested, and destroyed automatically.

## The Concept

### Container Lifecycle Commands

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Container Lifecycle                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   IMAGE                                                              │
│     │                                                                │
│     │ docker create / docker run                                    │
│     ▼                                                                │
│   ┌─────────────┐                                                   │
│   │   CREATED   │──────── docker start ────────▶┌─────────────┐    │
│   └─────────────┘                               │   RUNNING   │    │
│                                                  └──────┬──────┘    │
│                                                         │           │
│                              ┌───────────────────┬──────┴──────┐   │
│                              │                   │              │   │
│                        docker pause        docker stop    process  │
│                              │                   │          exits  │
│                              ▼                   ▼              │   │
│                        ┌──────────┐       ┌──────────┐         │   │
│                        │  PAUSED  │       │  EXITED  │◀────────┘   │
│                        └────┬─────┘       └────┬─────┘             │
│                             │                  │                    │
│                     docker unpause       docker start              │
│                             │                  │                    │
│                             └────────┬─────────┘                   │
│                                      ▼                              │
│                               ┌─────────────┐                       │
│                               │   RUNNING   │                       │
│                               └─────────────┘                       │
│                                                                      │
│   From any state:                                                   │
│   docker rm ───────────────────▶ REMOVED                           │
│   docker rm -f (force removes running container)                   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Interactive vs Detached Mode

```
┌─────────────────────────────────────────────────────────────────────┐
│              Interactive vs Detached Mode                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   INTERACTIVE MODE (-it)                                            │
│   ──────────────────────                                            │
│   docker run -it ubuntu bash                                        │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Terminal ◄───────────────────────▶ Container               │   │
│   │                                                              │   │
│   │  • STDIN connected                                          │   │
│   │  • TTY allocated                                            │   │
│   │  • Foreground process                                       │   │
│   │  • Container stops when you exit                            │   │
│   │                                                              │   │
│   │  Use for: Debugging, exploring, manual tasks                │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   DETACHED MODE (-d)                                                │
│   ──────────────────                                                │
│   docker run -d nginx                                               │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Terminal                         Container                  │   │
│   │                                   (background)               │   │
│   │  • Returns immediately                                      │   │
│   │  • Container runs in background                             │   │
│   │  • View output with docker logs                             │   │
│   │  • Container continues after terminal closes                │   │
│   │                                                              │   │
│   │  Use for: Servers, databases, long-running services         │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Port Mapping

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Port Mapping                                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   docker run -p HOST_PORT:CONTAINER_PORT image                      │
│                                                                      │
│   Example: docker run -p 8080:80 nginx                              │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                                                              │   │
│   │   Host Machine                    Container                  │   │
│   │   ─────────────                   ─────────                  │   │
│   │                                                              │   │
│   │   localhost:8080  ─────────────▶  :80                       │   │
│   │                    Port mapping     nginx                   │   │
│   │                                                              │   │
│   │   Browser request to              Received by nginx          │   │
│   │   http://localhost:8080           on port 80                │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Port Mapping Options:                                             │
│   ─────────────────────                                             │
│   -p 8080:80          Map host 8080 to container 80                │
│   -p 127.0.0.1:8080:80  Map only localhost                         │
│   -p 8080-8090:80-90  Map port ranges                              │
│   -P                  Map all exposed ports to random host ports   │
│                                                                      │
│   Multiple Ports:                                                   │
│   docker run -p 80:80 -p 443:443 nginx                             │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Environment Variables

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Environment Variables                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Pass configuration to containers without modifying images         │
│                                                                      │
│   Single Variable:                                                   │
│   docker run -e MY_VAR=value image                                  │
│                                                                      │
│   Multiple Variables:                                                │
│   docker run -e VAR1=value1 -e VAR2=value2 image                   │
│                                                                      │
│   From File (.env):                                                  │
│   docker run --env-file .env image                                  │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Example: Database Container                                 │   │
│   │                                                              │   │
│   │  docker run -d \                                            │   │
│   │    -e POSTGRES_USER=myuser \                                │   │
│   │    -e POSTGRES_PASSWORD=secret \                            │   │
│   │    -e POSTGRES_DB=myapp \                                   │   │
│   │    postgres:15                                               │   │
│   │                                                              │   │
│   │  Container reads these variables to configure itself        │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Common Uses:                                                       │
│   • Database credentials                                            │
│   • API keys and tokens                                             │
│   • Application modes (development, production)                     │
│   • Feature flags                                                    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Container Naming

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Container Naming                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Without --name: Docker generates random name                      │
│   docker run nginx                                                   │
│   # Creates: happy_fermi, zealous_tesla, etc.                      │
│                                                                      │
│   With --name: You control the name                                 │
│   docker run --name my-nginx nginx                                  │
│                                                                      │
│   Benefits of Named Containers:                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  • Reference by name instead of ID                          │   │
│   │    docker stop my-nginx                                     │   │
│   │    docker logs my-nginx                                     │   │
│   │                                                              │   │
│   │  • Container networking by name                              │   │
│   │    docker run --link my-nginx:nginx my-app                  │   │
│   │                                                              │   │
│   │  • Scripts and automation                                   │   │
│   │    More readable and maintainable                           │   │
│   │                                                              │   │
│   │  • Docker Compose uses service names                        │   │
│   │    Service discovery by container name                      │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Naming Conventions:                                                │
│   • Lowercase letters, digits, underscores, hyphens                 │
│   • Start with letter or digit                                      │
│   • Descriptive: web-server, api-gateway, db-postgres              │
│   • Include environment: my-app-dev, my-app-prod                   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Resource Limits

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Resource Limits                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   MEMORY LIMITS                                                      │
│   ─────────────                                                      │
│   -m, --memory      Hard memory limit                               │
│   --memory-swap     Memory + swap limit                             │
│   --memory-reservation  Soft limit                                  │
│                                                                      │
│   docker run -m 512m nginx        # Max 512MB                       │
│   docker run -m 1g nginx          # Max 1GB                         │
│                                                                      │
│   CPU LIMITS                                                         │
│   ──────────                                                         │
│   --cpus            Number of CPUs                                  │
│   --cpu-shares      Relative weight (default 1024)                  │
│   --cpuset-cpus     Specific CPUs to use                           │
│                                                                      │
│   docker run --cpus 0.5 nginx     # Max 50% of one CPU             │
│   docker run --cpus 2 nginx       # Max 2 CPUs                      │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Example: Production-like Resource Limits                    │   │
│   │                                                              │   │
│   │  docker run -d \                                            │   │
│   │    --name my-app \                                          │   │
│   │    --memory 512m \                                          │   │
│   │    --cpus 1 \                                               │   │
│   │    --restart unless-stopped \                               │   │
│   │    my-app:latest                                            │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Why Use Resource Limits?                                          │
│   • Prevent runaway containers from consuming all resources         │
│   • Simulate production constraints in testing                      │
│   • Fair resource sharing between containers                        │
│   • Required for orchestrators (Kubernetes)                         │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Docker Exec

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Docker Exec                                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Execute commands inside running containers                        │
│                                                                      │
│   docker exec [OPTIONS] CONTAINER COMMAND                           │
│                                                                      │
│   Common Patterns:                                                   │
│   ─────────────────                                                  │
│                                                                      │
│   # Interactive shell                                               │
│   docker exec -it my-container bash                                 │
│   docker exec -it my-container sh  # For alpine                    │
│                                                                      │
│   # Run single command                                              │
│   docker exec my-container ls /app                                  │
│   docker exec my-container cat /etc/hosts                          │
│                                                                      │
│   # Run as different user                                           │
│   docker exec -u root my-container whoami                          │
│                                                                      │
│   # Set environment variable for command                            │
│   docker exec -e DEBUG=true my-container ./script.sh               │
│                                                                      │
│   # Work in specific directory                                      │
│   docker exec -w /app my-container npm test                        │
│                                                                      │
│   Use Cases:                                                         │
│   • Debug running applications                                      │
│   • Inspect filesystem                                               │
│   • Run database commands                                           │
│   • View logs not sent to stdout                                    │
│   • Install debugging tools temporarily                             │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Container Lifecycle Management

```bash
# Run container in detached mode
docker run -d --name web nginx

# Check running containers
docker ps

# Check all containers (including stopped)
docker ps -a

# View container logs
docker logs web
docker logs -f web    # Follow logs
docker logs --tail 100 web  # Last 100 lines

# Stop container gracefully (SIGTERM, then SIGKILL after 10s)
docker stop web

# Start stopped container
docker start web

# Restart container
docker restart web

# Pause container (freeze processes)
docker pause web
docker unpause web

# Stop immediately (SIGKILL)
docker kill web

# Remove stopped container
docker rm web

# Force remove running container
docker rm -f web

# Remove container when it exits
docker run --rm alpine echo "I will be removed"
```

### Interactive Container Sessions

```bash
# Start interactive bash session
docker run -it ubuntu bash

# Inside container:
apt update
apt install -y curl
curl https://example.com
exit

# Start shell in running container
docker run -d --name my-ubuntu ubuntu sleep 3600
docker exec -it my-ubuntu bash
# Do work, then exit
# Container keeps running

# Run command and get output
docker exec my-ubuntu cat /etc/os-release

# Interactive with pseudo-TTY (needed for some commands)
docker exec -it my-ubuntu top

# Cleanup
docker rm -f my-ubuntu
```

### Port Mapping Examples

```bash
# Map single port
docker run -d -p 8080:80 --name web nginx
curl http://localhost:8080

# Map multiple ports
docker run -d \
  -p 80:80 \
  -p 443:443 \
  --name secure-web \
  nginx

# Map to specific interface
docker run -d -p 127.0.0.1:3000:3000 --name local-only node-app

# Random host port
docker run -d -P --name random-ports nginx
docker port random-ports  # See assigned ports

# Map port range
docker run -d -p 8000-8010:8000-8010 --name range my-app

# Check which port is mapped
docker port web
# 80/tcp -> 0.0.0.0:8080

# Cleanup
docker rm -f web secure-web local-only random-ports
```

### Environment Variables

```bash
# Single variable
docker run -e MY_VAR=hello alpine env | grep MY_VAR

# Multiple variables
docker run \
  -e DATABASE_HOST=localhost \
  -e DATABASE_PORT=5432 \
  -e DATABASE_NAME=myapp \
  alpine env

# From host environment
export API_KEY=secret123
docker run -e API_KEY alpine env | grep API_KEY

# From file
cat > .env << 'EOF'
DATABASE_URL=postgres://localhost/myapp
REDIS_URL=redis://localhost:6379
DEBUG=true
EOF

docker run --env-file .env alpine env

# Database example with environment variables
docker run -d \
  --name postgres \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=secretpassword \
  -e POSTGRES_DB=testdb \
  -p 5432:5432 \
  postgres:15

# Connect to the database
docker exec -it postgres psql -U admin -d testdb

# Cleanup
docker rm -f postgres
rm .env
```

### Resource Limits

```bash
# Memory limit
docker run -d --name limited-mem -m 256m nginx
docker stats limited-mem --no-stream

# CPU limit
docker run -d --name limited-cpu --cpus 0.5 nginx
docker stats limited-cpu --no-stream

# Combined limits
docker run -d \
  --name production-like \
  --memory 512m \
  --cpus 1 \
  nginx

# Memory stress test (container will be OOM killed)
docker run --rm -m 64m alpine sh -c \
  'dd if=/dev/zero of=/dev/null bs=100M count=1' 2>&1 || echo "OOM killed as expected"

# View container resource usage
docker stats --no-stream

# Inspect resource limits
docker inspect limited-mem --format '{{.HostConfig.Memory}}'

# Cleanup
docker rm -f limited-mem limited-cpu production-like
```

### Container Inspection and Debugging

```bash
# Full container details
docker inspect my-container

# Specific information
docker inspect my-container --format '{{.State.Status}}'
docker inspect my-container --format '{{.NetworkSettings.IPAddress}}'
docker inspect my-container --format '{{json .Config.Env}}'

# View real-time resource usage
docker stats

# View container processes
docker top my-container

# View port mappings
docker port my-container

# Copy files from container
docker cp my-container:/var/log/app.log ./app.log

# Copy files to container
docker cp ./config.json my-container:/app/config.json

# View container filesystem changes
docker diff my-container
# A = Added, C = Changed, D = Deleted

# Export container filesystem
docker export my-container > container-backup.tar

# Create image from container (with changes)
docker commit my-container my-new-image:v1
```

### Common Container Patterns

```bash
# One-off command (run and remove)
docker run --rm alpine echo "Task completed"

# Run database for development
docker run -d \
  --name dev-postgres \
  -e POSTGRES_PASSWORD=devpass \
  -p 5432:5432 \
  -v postgres-data:/var/lib/postgresql/data \
  postgres:15

# Run Redis cache
docker run -d \
  --name dev-redis \
  -p 6379:6379 \
  redis:7

# Run tests in container
docker run --rm \
  -v $(pwd):/app \
  -w /app \
  python:3.11 \
  python -m pytest tests/

# Run linter
docker run --rm \
  -v $(pwd):/app \
  -w /app \
  node:18 \
  npx eslint src/

# Debug a build
docker run -it --rm \
  -v $(pwd):/app \
  -w /app \
  python:3.11 \
  bash

# Cleanup development containers
docker rm -f dev-postgres dev-redis
docker volume rm postgres-data
```

## Summary

- **Lifecycle commands**: `run`, `start`, `stop`, `rm` manage container states
- **Interactive mode** (`-it`) connects terminal to container; **detached** (`-d`) runs in background
- **Port mapping** (`-p HOST:CONTAINER`) exposes container services to the host
- **Environment variables** (`-e`) configure containers without image changes
- **Container naming** (`--name`) improves manageability and enables networking
- **Resource limits** (`-m`, `--cpus`) prevent resource exhaustion and simulate production
- **Docker exec** runs commands inside running containers for debugging

## Additional Resources

- [Docker run reference](https://docs.docker.com/engine/reference/run/) - Complete command documentation
- [Container networking](https://docs.docker.com/network/) - How containers communicate
- [Docker exec reference](https://docs.docker.com/engine/reference/commandline/exec/) - Exec command options

