# Containerization

## Learning Objectives

- Explain containerization concepts and underlying technology
- Describe how containers achieve isolation using namespaces and cgroups
- Understand the container lifecycle from creation to removal
- Differentiate between stateless and stateful containers
- Recognize how containerization supports microservices architecture
- Compare container workloads with traditional deployment approaches

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Containerization is the foundation of modern application deployment. Understanding how containers work—not just how to use them—helps you troubleshoot issues, optimize performance, and make informed architecture decisions. When tests fail in containerized environments, knowing the underlying technology helps you identify whether the issue is application code, container configuration, or resource constraints.

As a quality engineer working with CI/CD pipelines, you'll encounter containers at every stage: building applications, running tests, and deploying to production. This foundational knowledge transforms containers from "magic boxes" into understandable, predictable technology.

## The Concept

### What is Containerization?

**Containerization** is a lightweight form of virtualization that packages an application and its dependencies together in an isolated environment called a container. Unlike virtual machines, containers share the host operating system's kernel while maintaining isolation through Linux kernel features.

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Containerization Concept                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Traditional Deployment          Containerized Deployment          │
│   ──────────────────────          ────────────────────────          │
│                                                                      │
│   ┌──────────────────┐           ┌──────────────────┐               │
│   │  Application     │           │  ┌────────────┐  │               │
│   │  depends on:     │           │  │ Container  │  │               │
│   │  - Python 3.8    │           │  │ ┌────────┐ │  │               │
│   │  - NumPy 1.19    │           │  │ │  App   │ │  │               │
│   │  - /etc/myconfig │           │  │ │Python  │ │  │               │
│   │                  │           │  │ │NumPy   │ │  │               │
│   │  "Works on my    │           │  │ │Config  │ │  │               │
│   │   machine..."    │           │  │ └────────┘ │  │               │
│   └──────────────────┘           │  └────────────┘  │               │
│           │                      │                  │               │
│           ▼                      │  Everything      │               │
│   Different machine has:         │  packaged        │               │
│   - Python 3.10 (wrong!)        │  together        │               │
│   - NumPy 1.24 (wrong!)         │                  │               │
│   - Missing config              │  Works anywhere  │               │
│                                  │  Docker runs     │               │
│   ❌ Broken deployment          └──────────────────┘               │
│                                                                      │
│                                  ✅ Consistent deployment           │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### How Containers Achieve Isolation

Containers use Linux kernel features to create isolated environments:

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Container Isolation Technology                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   NAMESPACES (What a container can see)                             │
│   ─────────────────────────────────────                             │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Namespace     │  Isolates                                  │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  PID           │  Process IDs (container sees only its      │   │
│   │                │  processes, PID 1 is container's init)     │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  NET           │  Network interfaces, IP addresses, ports   │   │
│   │                │  (container has its own network stack)      │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  MNT           │  Mount points (filesystem view)            │   │
│   │                │  (container has its own root filesystem)    │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  UTS           │  Hostname and domain name                  │   │
│   │                │  (container has its own hostname)           │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  IPC           │  Inter-process communication               │   │
│   │                │  (message queues, semaphores)               │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  USER          │  User and group IDs                        │   │
│   │                │  (root in container ≠ root on host)         │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   CGROUPS (What a container can use)                                │
│   ──────────────────────────────────                                │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  cgroup        │  Controls                                  │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  CPU           │  CPU time allocation                       │   │
│   │                │  (e.g., limit to 50% of one core)          │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  Memory        │  RAM usage limits                          │   │
│   │                │  (e.g., max 512MB, OOM kill if exceeded)   │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  I/O           │  Disk read/write bandwidth                 │   │
│   │                │  (e.g., max 100MB/s)                        │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │  Network       │  Network bandwidth                         │   │
│   │                │  (e.g., max 10Mbps)                         │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Container Lifecycle

Understanding the container lifecycle helps you manage containers effectively:

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Container Lifecycle                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                                                              │   │
│   │                     ┌───────────┐                           │   │
│   │    docker create    │  CREATED  │                           │   │
│   │    ───────────────▶ │           │                           │   │
│   │                     └─────┬─────┘                           │   │
│   │                           │                                  │   │
│   │                    docker start                             │   │
│   │                           │                                  │   │
│   │                           ▼                                  │   │
│   │   ┌─────────┐      ┌───────────┐      ┌──────────┐         │   │
│   │   │ PAUSED  │◀────▶│  RUNNING  │─────▶│  EXITED  │         │   │
│   │   └─────────┘      └───────────┘      └────┬─────┘         │   │
│   │    docker pause     docker stop/          │                │   │
│   │    docker unpause   process exit          │                │   │
│   │                                     docker start           │   │
│   │                                           │                │   │
│   │                                           ▼                │   │
│   │                                    ┌───────────┐           │   │
│   │                                    │  RUNNING  │           │   │
│   │                                    └───────────┘           │   │
│   │                                                              │   │
│   │   From any state: docker rm ───────▶ REMOVED                │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Common Commands:                                                   │
│   docker run = docker create + docker start                         │
│   docker run -d = run in background (detached)                      │
│   docker run --rm = remove container when it exits                  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Stateless vs Stateful Containers

Understanding state is crucial for container design:

```
┌─────────────────────────────────────────────────────────────────────┐
│              Stateless vs Stateful Containers                        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   STATELESS CONTAINERS (Recommended default)                        │
│   ─────────────────────────────────────────                         │
│                                                                      │
│   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐            │
│   │ Container 1 │    │ Container 2 │    │ Container 3 │            │
│   │             │    │             │    │             │            │
│   │  Same app   │    │  Same app   │    │  Same app   │            │
│   │  No local   │    │  No local   │    │  No local   │            │
│   │  state      │    │  state      │    │  state      │            │
│   └──────┬──────┘    └──────┬──────┘    └──────┬──────┘            │
│          │                  │                  │                    │
│          └──────────────────┼──────────────────┘                    │
│                             │                                       │
│                             ▼                                       │
│                    ┌─────────────────┐                              │
│                    │ External State  │                              │
│                    │ (Database, S3)  │                              │
│                    └─────────────────┘                              │
│                                                                      │
│   Benefits:                                                          │
│   ✓ Any container can handle any request                           │
│   ✓ Easy to scale up/down                                          │
│   ✓ Easy to replace failed containers                               │
│   ✓ Predictable behavior                                            │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   STATEFUL CONTAINERS (Use with caution)                            │
│   ──────────────────────────────────────                            │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │              ┌─────────────┐                                 │   │
│   │              │  Container  │                                 │   │
│   │              │             │                                 │   │
│   │              │  Database   │                                 │   │
│   │              │  Server     │                                 │   │
│   │              └──────┬──────┘                                 │   │
│   │                     │                                        │   │
│   │                     ▼                                        │   │
│   │              ┌─────────────┐                                 │   │
│   │              │   Volume    │  Persistent storage             │   │
│   │              │   (data)    │  survives container restarts   │   │
│   │              └─────────────┘                                 │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Considerations:                                                    │
│   • Requires volume management                                      │
│   • More complex backup/recovery                                    │
│   • Can't easily scale horizontally                                 │
│   • Container identity matters                                      │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Microservices Architecture Fit

Containers are ideal for microservices architecture:

```
┌─────────────────────────────────────────────────────────────────────┐
│              Containers and Microservices                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   MONOLITHIC APPLICATION                                            │
│   ──────────────────────                                            │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    Single Deployment Unit                    │   │
│   │                                                              │   │
│   │   ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────────────┐   │   │
│   │   │  User   │ │ Product │ │  Order  │ │    Payment      │   │   │
│   │   │ Module  │ │ Module  │ │ Module  │ │    Module       │   │   │
│   │   └─────────┘ └─────────┘ └─────────┘ └─────────────────┘   │   │
│   │                                                              │   │
│   │   All modules deployed together, scale together, fail       │   │
│   │   together                                                   │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   MICROSERVICES WITH CONTAINERS                                     │
│   ─────────────────────────────                                     │
│                                                                      │
│   ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────────────┐       │
│   │  User   │  │ Product │  │  Order  │  │    Payment      │       │
│   │ Service │  │ Service │  │ Service │  │    Service      │       │
│   │         │  │         │  │         │  │                 │       │
│   │Container│  │Container│  │Container│  │   Container     │       │
│   └────┬────┘  └────┬────┘  └────┬────┘  └───────┬─────────┘       │
│        │            │            │               │                  │
│        └────────────┴──────┬─────┴───────────────┘                  │
│                            │                                        │
│                    API Gateway / Service Mesh                       │
│                                                                      │
│   Benefits:                                                          │
│   ✓ Independent deployment (deploy Order without User)              │
│   ✓ Independent scaling (scale Payment during checkout)            │
│   ✓ Technology diversity (User in Python, Order in Java)           │
│   ✓ Fault isolation (Order crash doesn't affect User)              │
│   ✓ Team autonomy (different teams own different services)         │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Container vs Traditional Deployment

```
┌─────────────────────────────────────────────────────────────────────┐
│           Traditional vs Container Deployment Comparison             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Aspect              Traditional          Container                │
│   ──────              ───────────          ─────────                │
│                                                                      │
│   Deployment          Manual installs,     docker pull/run          │
│                       scripts, config                               │
│                                                                      │
│   Environment         Configuration        Defined in Dockerfile,   │
│   Definition          documentation        version controlled       │
│                                                                      │
│   Dependencies        System-wide,         Per-container,           │
│                       conflicts possible   isolated                 │
│                                                                      │
│   Scaling             Manual provisioning  Orchestrator handles     │
│                       and configuration    (Kubernetes, ECS)        │
│                                                                      │
│   Rollback            Restore from backup, docker run old-image     │
│                       reconfigure                                   │
│                                                                      │
│   Resource Usage      Full OS per VM       Shared kernel,           │
│                       or dedicated server  lightweight              │
│                                                                      │
│   Startup Time        Minutes              Seconds                  │
│                                                                      │
│   Testing             "Works on my         Same image everywhere    │
│   Consistency         machine" syndrome                             │
│                                                                      │
│   Security Updates    Manual patching      Rebuild image,           │
│                       per server           redeploy                 │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Container Security Considerations

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Container Security Overview                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ISOLATION IS NOT COMPLETE                                         │
│   ─────────────────────────                                         │
│   • Containers share host kernel                                    │
│   • Kernel vulnerabilities affect all containers                    │
│   • Not as isolated as VMs                                          │
│                                                                      │
│   BEST PRACTICES                                                     │
│   ──────────────                                                     │
│   ✓ Don't run as root inside containers                            │
│   ✓ Use minimal base images (alpine, distroless)                   │
│   ✓ Scan images for vulnerabilities                                │
│   ✓ Keep base images updated                                       │
│   ✓ Don't store secrets in images                                  │
│   ✓ Use read-only filesystems when possible                        │
│   ✓ Limit container capabilities                                   │
│   ✓ Set resource limits (prevent DoS)                              │
│                                                                      │
│   RUNTIME SECURITY                                                   │
│   ────────────────                                                   │
│   • AppArmor / SELinux profiles                                     │
│   • Seccomp (limit system calls)                                    │
│   • User namespaces (rootless containers)                           │
│   • Network policies                                                 │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Explore Container Isolation

```bash
# Compare process views: host vs container

# On host, list all processes
ps aux | wc -l
# Output: hundreds of processes

# Inside a container, list processes
docker run --rm alpine ps aux
# Output: only 2 processes (sh and ps)

# Container has its own PID namespace
# PID 1 in container is not PID 1 on host
```

### Observe Namespace Isolation

```bash
# See container's isolated hostname
docker run --rm alpine hostname
# Output: random container ID

# Set custom hostname
docker run --rm --hostname mycontainer alpine hostname
# Output: mycontainer

# Container has isolated network
docker run --rm alpine ip addr
# Shows container's own network interfaces

# Compare with host
ip addr
# Shows different interfaces
```

### Resource Limits with cgroups

```bash
# Run container with memory limit
docker run --rm -m 256m alpine free -m
# Container limited to 256MB

# Run container with CPU limit
docker run --rm --cpus 0.5 alpine cat /proc/cpuinfo
# Container limited to 50% of one CPU

# Observe memory limit enforcement
docker run --rm -m 64m alpine sh -c "
    # Try to allocate 100MB - will be killed (OOM)
    dd if=/dev/zero of=/dev/null bs=100M count=1
"
```

### Container Lifecycle Demo

```bash
# Create container without starting
docker create --name lifecycle-demo alpine echo "Hello"
docker ps -a | grep lifecycle-demo
# Status: Created

# Start the container
docker start lifecycle-demo
docker ps -a | grep lifecycle-demo
# Status: Exited (ran and completed)

# View output
docker logs lifecycle-demo
# Output: Hello

# Remove container
docker rm lifecycle-demo
```

### Stateless Container Example

```bash
# Stateless web server - no local state
docker run -d --name web1 -p 8081:80 nginx
docker run -d --name web2 -p 8082:80 nginx
docker run -d --name web3 -p 8083:80 nginx

# All three are identical, interchangeable
curl http://localhost:8081
curl http://localhost:8082
curl http://localhost:8083

# Can replace any without data loss
docker rm -f web2
docker run -d --name web2 -p 8082:80 nginx

# Cleanup
docker rm -f web1 web2 web3
```

### Stateful Container Example

```bash
# Stateful database - requires volume for persistence
docker run -d \
    --name postgres-db \
    -e POSTGRES_PASSWORD=secret \
    -v postgres-data:/var/lib/postgresql/data \
    postgres:15

# Data persists even if container is removed
docker rm -f postgres-db

# Recreate with same volume - data still there
docker run -d \
    --name postgres-db \
    -e POSTGRES_PASSWORD=secret \
    -v postgres-data:/var/lib/postgresql/data \
    postgres:15

# Cleanup
docker rm -f postgres-db
docker volume rm postgres-data
```

### Compare Container and VM Resource Usage

```bash
# Container stats
docker run -d --name small-container alpine sleep 3600
docker stats small-container --no-stream
# Memory: ~1-2 MB

# Compare to VM memory usage
# (A typical VM would use 500MB-2GB for the OS alone)

# Container startup time
time docker run --rm alpine echo "Started"
# Typically < 1 second

# Cleanup
docker rm -f small-container
```

## Summary

- **Containerization** packages applications with dependencies in isolated environments sharing the host kernel
- **Namespaces** provide isolation of what containers can see (processes, network, filesystem, etc.)
- **Cgroups** control resource limits (CPU, memory, I/O) for containers
- **Container lifecycle** moves through created, running, paused, exited, and removed states
- **Stateless containers** (no local state) are easier to scale and replace; use external storage for data
- **Microservices** architecture benefits from containers: independent deployment, scaling, and technology diversity
- Containers are lighter than VMs but have less isolation; follow security best practices

## Additional Resources

- [Docker Overview: The Underlying Technology](https://docs.docker.com/get-started/overview/#the-underlying-technology) - Official explanation of container technology
- [Linux Namespaces](https://man7.org/linux/man-pages/man7/namespaces.7.html) - Linux manual page on namespaces
- [Understanding cgroups](https://www.kernel.org/doc/html/latest/admin-guide/cgroup-v2.html) - Linux kernel documentation on cgroups

