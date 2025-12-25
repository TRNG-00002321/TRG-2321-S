# Installing and Configuring Docker

## Learning Objectives

- Install Docker on Windows, macOS, and Linux systems
- Configure Docker Desktop for development workflows
- Complete post-installation steps for optimal setup
- Modify Docker daemon configuration for specific needs
- Verify Docker installation and troubleshoot common issues
- Understand Docker system requirements and resource allocation

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Before you can containerize applications and run tests in Docker, you need a properly configured Docker environment. A misconfigured Docker installation leads to frustrating issues: containers that won't start, resource exhaustion, or networking problems. Getting the setup right from the start saves hours of troubleshooting later.

As a quality engineer, you'll need Docker on your development machine for local testing, and you'll understand Docker installation for CI/CD runners (covered Friday with Jenkins). This knowledge helps you troubleshoot environment issues that affect test execution.

## The Concept

### Docker Installation Options

Different installation approaches for different use cases:

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Docker Installation Options                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   DOCKER DESKTOP                                                     │
│   ──────────────                                                     │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Best for: Windows, macOS (development)                      │   │
│   │                                                              │   │
│   │  Includes:                                                   │   │
│   │  • Docker Engine                                             │   │
│   │  • Docker CLI                                                │   │
│   │  • Docker Compose                                            │   │
│   │  • Kubernetes (optional)                                     │   │
│   │  • GUI for management                                        │   │
│   │  • Automatic updates                                         │   │
│   │                                                              │   │
│   │  Requires:                                                   │   │
│   │  • Windows 10/11 Pro/Enterprise (WSL 2 or Hyper-V)          │   │
│   │  • macOS 10.15+ (Intel or Apple Silicon)                    │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   DOCKER ENGINE                                                      │
│   ─────────────                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Best for: Linux (development and production)                │   │
│   │                                                              │   │
│   │  Includes:                                                   │   │
│   │  • Docker Engine (daemon)                                    │   │
│   │  • Docker CLI                                                │   │
│   │  • containerd                                                │   │
│   │                                                              │   │
│   │  Separate installation:                                      │   │
│   │  • Docker Compose                                            │   │
│   │                                                              │   │
│   │  Supports:                                                   │   │
│   │  • Ubuntu, Debian, CentOS, RHEL, Fedora, SLES               │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### System Requirements

```
┌─────────────────────────────────────────────────────────────────────┐
│                     System Requirements                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   WINDOWS (Docker Desktop)                                          │
│   ────────────────────────                                          │
│   • Windows 10 64-bit: Pro, Enterprise, or Education                │
│     (Build 19041 or higher)                                         │
│   • Windows 11 64-bit: Home, Pro, Enterprise, or Education         │
│   • WSL 2 feature enabled (recommended)                             │
│   • Hardware: 4GB RAM minimum, 8GB+ recommended                     │
│   • Virtualization enabled in BIOS                                  │
│                                                                      │
│   macOS (Docker Desktop)                                            │
│   ──────────────────────                                            │
│   • macOS 10.15 (Catalina) or newer                                │
│   • At least 4GB RAM (8GB+ recommended)                            │
│   • Native support for Apple Silicon (M1/M2/M3)                    │
│                                                                      │
│   LINUX (Docker Engine)                                             │
│   ─────────────────────                                             │
│   • 64-bit kernel and CPU support for virtualization               │
│   • 4GB RAM minimum (depends on workload)                          │
│   • Supported distributions:                                        │
│     Ubuntu 20.04+, Debian 10+, CentOS 7+, RHEL 7+                  │
│   • iptables 1.4+, git 1.7+                                        │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Docker Desktop Architecture

Understanding how Docker Desktop works on Windows/macOS:

```
┌─────────────────────────────────────────────────────────────────────┐
│              Docker Desktop Architecture (Windows/macOS)             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   YOUR APPLICATIONS                                                  │
│   ────────────────                                                   │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Terminal / IDE                                              │   │
│   │  └── docker CLI commands                                    │   │
│   └────────────────────────────┬────────────────────────────────┘   │
│                                │                                    │
│                                ▼                                    │
│   DOCKER DESKTOP                                                     │
│   ──────────────                                                     │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Docker Desktop Application                                  │   │
│   │  ├── GUI Dashboard                                           │   │
│   │  ├── Settings management                                    │   │
│   │  └── Extension marketplace                                  │   │
│   └────────────────────────────┬────────────────────────────────┘   │
│                                │                                    │
│                                ▼                                    │
│   LINUX VM (Transparent to user)                                    │
│   ──────────────────────────────                                    │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  WSL 2 (Windows) / HyperKit/Virtualization.framework (Mac)  │   │
│   │  ┌─────────────────────────────────────────────────────┐   │   │
│   │  │  Linux Kernel                                        │   │   │
│   │  │  └── Docker Engine (dockerd)                        │   │   │
│   │  │      └── Containers run here                        │   │   │
│   │  └─────────────────────────────────────────────────────┘   │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   HOST OS                                                            │
│   ───────                                                            │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Windows / macOS                                             │   │
│   │  └── File system mounts available to containers             │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Docker Daemon Configuration

The Docker daemon (`dockerd`) is configured via `daemon.json`:

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Docker Daemon Configuration                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Configuration File Locations:                                      │
│   • Linux: /etc/docker/daemon.json                                  │
│   • Windows: C:\ProgramData\docker\config\daemon.json               │
│   • Docker Desktop: Settings > Docker Engine (GUI)                  │
│                                                                      │
│   Common Configuration Options:                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  {                                                          │   │
│   │    "data-root": "/var/lib/docker",                         │   │
│   │    "storage-driver": "overlay2",                           │   │
│   │    "log-driver": "json-file",                              │   │
│   │    "log-opts": {                                           │   │
│   │      "max-size": "10m",                                    │   │
│   │      "max-file": "3"                                       │   │
│   │    },                                                       │   │
│   │    "default-address-pools": [                              │   │
│   │      {"base": "172.17.0.0/16", "size": 24}                │   │
│   │    ],                                                       │   │
│   │    "insecure-registries": [],                              │   │
│   │    "registry-mirrors": []                                  │   │
│   │  }                                                          │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Common Settings Explained:                                        │
│   • data-root: Where Docker stores images, containers, volumes     │
│   • storage-driver: Filesystem driver (overlay2 recommended)       │
│   • log-driver: Container log handling                              │
│   • log-opts: Prevent logs from filling disk                       │
│   • insecure-registries: Allow HTTP registries (not recommended)   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Windows Installation (Docker Desktop)

```powershell
# Option 1: Download from Docker website
# https://www.docker.com/products/docker-desktop/

# Option 2: Install via winget (Windows Package Manager)
winget install Docker.DockerDesktop

# After installation, ensure WSL 2 is enabled
wsl --install

# Verify WSL 2 is default
wsl --set-default-version 2

# Restart computer if prompted

# After restart, Docker Desktop starts automatically
# Verify installation
docker --version
docker run hello-world
```

### macOS Installation (Docker Desktop)

```bash
# Option 1: Download from Docker website
# https://www.docker.com/products/docker-desktop/

# Option 2: Install via Homebrew
brew install --cask docker

# Start Docker Desktop from Applications
# or via command line:
open /Applications/Docker.app

# Wait for Docker to start (whale icon in menu bar)

# Verify installation
docker --version
docker run hello-world
```

### Linux Installation (Ubuntu/Debian)

```bash
# Update package index
sudo apt-get update

# Install prerequisites
sudo apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release

# Add Docker's official GPG key
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
    sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Set up repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker Engine
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Verify installation
sudo docker --version
sudo docker run hello-world
```

### Linux Installation (CentOS/RHEL)

```bash
# Remove old versions
sudo yum remove docker docker-client docker-client-latest \
    docker-common docker-latest docker-latest-logrotate \
    docker-logrotate docker-engine

# Install prerequisites
sudo yum install -y yum-utils

# Add Docker repository
sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo

# Install Docker Engine
sudo yum install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Start Docker
sudo systemctl start docker
sudo systemctl enable docker

# Verify installation
sudo docker --version
sudo docker run hello-world
```

### Post-Installation Steps (Linux)

```bash
# Create docker group (usually created by installer)
sudo groupadd docker

# Add your user to docker group (avoids using sudo)
sudo usermod -aG docker $USER

# Apply group changes (or log out and back in)
newgrp docker

# Verify you can run docker without sudo
docker run hello-world

# Configure Docker to start on boot
sudo systemctl enable docker.service
sudo systemctl enable containerd.service

# (Optional) Configure Docker to use a different data directory
# Edit /etc/docker/daemon.json
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json << 'EOF'
{
  "data-root": "/mnt/docker-data"
}
EOF

# Restart Docker to apply changes
sudo systemctl restart docker
```

### Configure Docker Desktop Resources

```bash
# Docker Desktop resources are configured via GUI:
# Settings > Resources > Advanced

# Recommended settings for development:
# - CPUs: 2-4 (leave some for host)
# - Memory: 4-8 GB (depending on total RAM)
# - Swap: 1-2 GB
# - Disk image size: 64-128 GB

# For CLI configuration on Docker Desktop, 
# access Settings > Docker Engine and edit JSON:
{
  "builder": { "gc": { "enabled": true } },
  "features": { "buildkit": true },
  "experimental": false
}
```

### Configure Log Rotation

```bash
# Prevent Docker logs from filling disk
# Edit /etc/docker/daemon.json (Linux) or Docker Desktop settings

sudo tee /etc/docker/daemon.json << 'EOF'
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}
EOF

# Restart Docker
sudo systemctl restart docker

# Verify log configuration
docker info --format '{{.LoggingDriver}}'
```

### Verify Docker Installation

```bash
# Check Docker version
docker --version
# Docker version 24.0.7, build afdd53b

# Detailed version info
docker version
# Shows client and server versions

# System-wide information
docker info
# Shows containers, images, storage driver, etc.

# Run test container
docker run hello-world

# Run interactive container
docker run -it ubuntu bash
# exit

# Run nginx and verify networking
docker run -d -p 8080:80 --name test-nginx nginx
curl http://localhost:8080
docker rm -f test-nginx

# Check Docker Compose
docker compose version
# Docker Compose version v2.21.0
```

### Troubleshooting Common Issues

```bash
# Issue: Permission denied
# Solution: Add user to docker group
sudo usermod -aG docker $USER
# Log out and back in

# Issue: Docker daemon not running
# Check status
sudo systemctl status docker
# Start if not running
sudo systemctl start docker

# Issue: Cannot connect to Docker daemon
# Check Docker socket
ls -la /var/run/docker.sock
# Should show: srw-rw---- 1 root docker

# Issue: Disk space full
# Check Docker disk usage
docker system df
# Clean up unused resources
docker system prune -a

# Issue: Container networking not working
# Check Docker networks
docker network ls
# Inspect network
docker network inspect bridge

# Issue: Slow file system performance (macOS/Windows)
# Use Docker volumes instead of bind mounts for databases
docker volume create my-data
docker run -v my-data:/data myimage

# Issue: WSL 2 not starting (Windows)
# Update WSL
wsl --update
# Restart WSL
wsl --shutdown
# Check Docker Desktop settings use WSL 2 backend
```

### Uninstall Docker

```bash
# Ubuntu/Debian - Uninstall Docker Engine
sudo apt-get purge docker-ce docker-ce-cli containerd.io docker-compose-plugin
sudo rm -rf /var/lib/docker
sudo rm -rf /var/lib/containerd
sudo rm -rf /etc/docker

# CentOS/RHEL - Uninstall Docker Engine
sudo yum remove docker-ce docker-ce-cli containerd.io docker-compose-plugin
sudo rm -rf /var/lib/docker
sudo rm -rf /var/lib/containerd

# Docker Desktop - Windows
# Use Settings > Apps > Docker Desktop > Uninstall

# Docker Desktop - macOS
# Drag Docker.app to Trash
# Clean up data:
rm -rf ~/Library/Group\ Containers/group.com.docker
rm -rf ~/Library/Containers/com.docker.docker
rm -rf ~/.docker
```

## Summary

- **Docker Desktop** is recommended for Windows and macOS development (includes GUI and Kubernetes)
- **Docker Engine** is installed directly on Linux (command-line only, production-ready)
- **System requirements**: 64-bit OS, 4GB+ RAM, virtualization support
- **Post-installation** on Linux: add user to docker group, enable auto-start
- **Daemon configuration** via `daemon.json` controls storage, logging, and networking
- **Log rotation** prevents container logs from filling disk
- **Verify installation** with `docker run hello-world` and `docker info`

## Additional Resources

- [Docker Desktop Installation](https://docs.docker.com/desktop/) - Official installation guides
- [Docker Engine Installation](https://docs.docker.com/engine/install/) - Linux installation documentation
- [Docker Daemon Configuration](https://docs.docker.com/engine/reference/commandline/dockerd/) - Complete daemon options

