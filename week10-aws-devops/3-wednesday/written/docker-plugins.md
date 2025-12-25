# Docker Plugins

## Learning Objectives

- Understand Docker's plugin architecture and extensibility model
- Identify common plugin categories: storage, network, authorization
- Install and manage Docker plugins using CLI commands
- Configure and use volume plugins for extended storage options
- Recognize common use cases for Docker plugins
- Evaluate when to use plugins versus built-in functionality

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Docker's core functionality covers most use cases, but enterprise environments often require extended capabilities: connecting to network-attached storage, integrating with corporate authorization systems, or adding specialized networking features. Docker plugins provide this extensibility without modifying Docker itself.

As a quality engineer, understanding plugins helps you work with enterprise Docker deployments, troubleshoot plugin-related issues, and understand how containerized applications integrate with organizational infrastructure. When CI/CD pipelines use shared storage or corporate authentication, plugins often provide that integration.

## The Concept

### Docker Plugin Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Docker Plugin Architecture                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    Docker Engine                             │   │
│   │                                                              │   │
│   │   ┌───────────────────────────────────────────────────────┐ │   │
│   │   │                Core Functionality                      │ │   │
│   │   │  • Container runtime                                   │ │   │
│   │   │  • Image management                                    │ │   │
│   │   │  • Default networking                                  │ │   │
│   │   │  • Local storage                                       │ │   │
│   │   └───────────────────────────────────────────────────────┘ │   │
│   │                           │                                  │   │
│   │                    Plugin API                               │   │
│   │                           │                                  │   │
│   │   ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │   │
│   │   │   Volume    │ │   Network   │ │   Authorization     │  │   │
│   │   │   Plugins   │ │   Plugins   │ │   Plugins           │  │   │
│   │   │             │ │             │ │                     │  │   │
│   │   │ • NFS       │ │ • Weave     │ │ • LDAP integration │  │   │
│   │   │ • AWS EBS   │ │ • Calico    │ │ • Policy engine    │  │   │
│   │   │ • Azure     │ │ • Overlay   │ │ • Audit logging    │  │   │
│   │   │ • GlusterFS │ │ • Macvlan   │ │                     │  │   │
│   │   └─────────────┘ └─────────────┘ └─────────────────────┘  │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Plugins extend Docker without modifying the engine itself         │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Plugin Categories

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Plugin Categories                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   VOLUME PLUGINS                                                     │
│   ──────────────                                                     │
│   Extend storage to external systems                                │
│                                                                      │
│   Plugin              Storage System         Use Case                │
│   ──────              ──────────────         ────────                │
│   local (built-in)    Host filesystem        Single host            │
│   vieux/sshfs         SSHFS mounts          Remote servers          │
│   rexray/*            Dell EMC, AWS EBS     Enterprise/Cloud        │
│   convoy              NFS, EBS, VFS         Multi-backend           │
│   azure-file          Azure Files           Azure workloads         │
│   netapp              NetApp storage        Enterprise              │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   NETWORK PLUGINS                                                    │
│   ───────────────                                                    │
│   Provide networking capabilities beyond Docker's defaults          │
│                                                                      │
│   Plugin              Feature                Use Case                │
│   ──────              ───────                ────────                │
│   bridge (built-in)   NAT networking        Single host             │
│   overlay (built-in)  Multi-host            Swarm clusters          │
│   weave               Mesh networking       Multi-host + DNS        │
│   calico              Policy-based          Kubernetes              │
│   macvlan             Physical network      Legacy integration      │
│   flannel             Overlay network       Kubernetes              │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   AUTHORIZATION PLUGINS                                              │
│   ─────────────────────                                              │
│   Control who can do what with Docker                               │
│                                                                      │
│   Plugin              Feature                Use Case                │
│   ──────              ───────                ────────                │
│   twistlock           Security platform     Enterprise security     │
│   open-policy-agent   Policy decisions      Fine-grained control    │
│   custom authz        LDAP/AD integration   Corporate auth          │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Plugin Lifecycle

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Plugin Lifecycle                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   1. INSTALL                                                        │
│      docker plugin install vieux/sshfs                              │
│      └── Downloads plugin from registry                             │
│      └── Plugin stored in /var/lib/docker/plugins/                 │
│                                                                      │
│   2. ENABLE (happens automatically or manually)                     │
│      docker plugin enable vieux/sshfs                               │
│      └── Plugin is now available for use                            │
│                                                                      │
│   3. USE                                                             │
│      docker volume create -d vieux/sshfs myvolume                  │
│      docker run -v myvolume:/data myimage                          │
│      └── Docker delegates to plugin for volume operations          │
│                                                                      │
│   4. DISABLE (when needed)                                          │
│      docker plugin disable vieux/sshfs                              │
│      └── Plugin no longer available (existing volumes still work)  │
│                                                                      │
│   5. REMOVE                                                          │
│      docker plugin rm vieux/sshfs                                   │
│      └── Uninstalls plugin completely                               │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Managing Plugins

```bash
# List installed plugins
docker plugin ls

# Search for plugins (Docker Hub)
# Visit https://hub.docker.com/search?type=plugin

# Install a plugin
docker plugin install vieux/sshfs

# Install with specific permissions granted automatically
docker plugin install --grant-all-permissions vieux/sshfs

# View plugin details
docker plugin inspect vieux/sshfs

# Enable a disabled plugin
docker plugin enable vieux/sshfs

# Disable a plugin
docker plugin disable vieux/sshfs

# Remove a plugin
docker plugin rm vieux/sshfs
```

### Volume Plugin: SSHFS Example

```bash
# Install SSHFS volume plugin
docker plugin install vieux/sshfs

# Create volume using SSHFS
docker volume create \
  --driver vieux/sshfs \
  -o sshcmd=user@remote.example.com:/data \
  -o password=mypassword \
  remote-data

# Or using SSH key
docker volume create \
  --driver vieux/sshfs \
  -o sshcmd=user@remote.example.com:/data \
  -o IdentityFile=/path/to/id_rsa \
  remote-data-key

# Use the volume
docker run -d \
  --name app \
  -v remote-data:/app/data \
  my-app

# Data is stored on remote server via SSH
```

### Volume Plugin: Local with NFS

```bash
# Use the local driver with NFS options
docker volume create \
  --driver local \
  --opt type=nfs \
  --opt o=addr=192.168.1.100,rw \
  --opt device=:/exports/data \
  nfs-data

# Use the NFS volume
docker run -d \
  --name app \
  -v nfs-data:/data \
  my-app

# All containers on any host can access the same NFS share
```

### Setting Plugin Configuration

```bash
# Some plugins have configurable settings
docker plugin install myvendor/myplugin

# View plugin settings
docker plugin inspect myvendor/myplugin --format '{{.Settings}}'

# Set plugin configuration (must disable first)
docker plugin disable myvendor/myplugin
docker plugin set myvendor/myplugin LOG_LEVEL=debug
docker plugin enable myvendor/myplugin
```

### Creating a Volume with Plugin Options

```bash
# Azure File Storage plugin example
docker plugin install --grant-all-permissions docker4x/cloudstor:azure

docker volume create \
  --driver docker4x/cloudstor:azure \
  -o share=myshare \
  azure-data

# AWS EBS plugin example
docker plugin install --grant-all-permissions rexray/ebs

docker volume create \
  --driver rexray/ebs \
  -o size=100 \
  -o type=gp2 \
  ebs-data
```

### Plugin Logs and Troubleshooting

```bash
# View plugin logs (requires access to daemon logs)
journalctl -u docker | grep plugin

# Check plugin status
docker plugin inspect myplugin --format '{{.Enabled}}'

# View plugin capabilities
docker plugin inspect myplugin --format '{{.Config.Interface}}'

# Check for plugin errors
docker plugin inspect myplugin --format '{{.Config.Entrypoint}}'

# Debug plugin issues
docker info | grep -A5 Plugins
```

### Authorization Plugin Example

```bash
# Authorization plugins control Docker API access
# Example: Open Policy Agent (OPA) plugin

# Install OPA plugin (hypothetical)
docker plugin install openpolicyagent/docker-authz-plugin

# Configure Docker daemon to use the plugin
# Add to /etc/docker/daemon.json:
{
  "authorization-plugins": ["openpolicyagent/docker-authz-plugin"]
}

# Restart Docker
sudo systemctl restart docker

# Now all Docker commands go through OPA for authorization
# Define policies in OPA to control access
```

### Plugin with Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    image: my-app
    volumes:
      - remote-data:/app/data

volumes:
  remote-data:
    driver: vieux/sshfs
    driver_opts:
      sshcmd: "user@remote.example.com:/data"
      password: "${SSH_PASSWORD}"  # Use environment variable
```

### Built-in vs Plugin Comparison

```bash
# Built-in local driver (default)
docker volume create local-data
docker run -v local-data:/data alpine

# Local driver with bind mount option
docker volume create \
  --driver local \
  --opt type=none \
  --opt o=bind \
  --opt device=/mnt/external \
  bind-data

# External plugin for cloud storage
docker plugin install rexray/s3fs
docker volume create \
  --driver rexray/s3fs \
  -o bucket=my-bucket \
  s3-data

# When to use each:
# Built-in local:  Single host, simple storage
# Local with opts: NFS, specific mount options
# External plugin: Cloud storage, enterprise systems
```

### Checking Plugin Requirements

```bash
# Before installing, check plugin requirements

# View plugin settings and capabilities
docker plugin inspect --format '{{.Config.Args}}' myplugin

# Check required privileges
docker plugin inspect --format '{{.Config.Linux}}' myplugin

# View network requirements
docker plugin inspect --format '{{.Config.Network}}' myplugin

# Check mount requirements
docker plugin inspect --format '{{.Config.Mounts}}' myplugin
```

## When to Use Plugins

```
┌─────────────────────────────────────────────────────────────────────┐
│                  When to Use Docker Plugins                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   USE PLUGINS WHEN:                                                  │
│   ─────────────────                                                  │
│   ✓ Need to connect to external storage systems (NFS, SAN, cloud)  │
│   ✓ Require enterprise networking (SDN, policy-based)              │
│   ✓ Must integrate with corporate authentication (LDAP, AD)        │
│   ✓ Need centralized logging or monitoring integration             │
│   ✓ Running multi-host clusters needing shared storage             │
│   ✓ Compliance requires audit logging of Docker operations         │
│                                                                      │
│   USE BUILT-IN FEATURES WHEN:                                       │
│   ────────────────────────────                                       │
│   ✓ Single host development/testing                                │
│   ✓ Standard bridge/overlay networking suffices                    │
│   ✓ Local volumes or bind mounts meet needs                        │
│   ✓ Simplicity is prioritized over features                        │
│   ✓ No enterprise integration requirements                         │
│                                                                      │
│   CONSIDERATIONS:                                                    │
│   ───────────────                                                    │
│   • Plugins add complexity and potential failure points            │
│   • Not all plugins are well-maintained                             │
│   • Kubernetes often has better solutions for enterprise needs     │
│   • Test plugins thoroughly before production use                   │
│   • Check plugin compatibility with your Docker version            │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Summary

- **Docker plugins** extend core functionality without modifying Docker Engine
- **Plugin types**: Volume (storage), Network (connectivity), Authorization (access control)
- **Volume plugins** connect Docker to NFS, cloud storage, SANs, and other systems
- **Network plugins** provide advanced networking (mesh, policy-based, SDN)
- **Authorization plugins** integrate with corporate authentication and audit systems
- **Management**: install, enable, disable, set, inspect, rm
- **Use plugins** when built-in features don't meet enterprise or specialized requirements

## Additional Resources

- [Docker Plugin Documentation](https://docs.docker.com/engine/extend/) - Official plugin documentation
- [Docker Hub Plugins](https://hub.docker.com/search?type=plugin) - Available plugins
- [Volume Plugins](https://docs.docker.com/engine/extend/plugins_volume/) - Volume plugin development

