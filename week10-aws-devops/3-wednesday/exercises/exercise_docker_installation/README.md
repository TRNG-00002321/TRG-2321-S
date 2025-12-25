# Exercise 1: Docker Installation & Verification

## Objective

Install Docker on your local machine, verify the installation, and run your first containers to understand the Docker workflow.

---

## Learning Outcomes

By completing this exercise, you will:
- Install Docker Desktop (Windows/Mac) or Docker Engine (Linux)
- Verify Docker installation and understand version information
- Run your first container (`hello-world`)
- Run a web server container (nginx)
- Understand the docker pull → run workflow

---

## Prerequisites

- Administrative access to your machine
- Internet connection for downloading images
- Meet minimum requirements:
  - Windows 10/11 64-bit with WSL 2 or Hyper-V
  - macOS 11+ (Big Sur or newer)
  - Linux: Ubuntu 20.04+, Debian 10+, or similar

---

## Time Estimate

30 minutes

---

## Tasks

### Task 1: Install Docker (15 minutes)

Choose your operating system:

#### Windows

1. **Download Docker Desktop**
   - Go to: https://www.docker.com/products/docker-desktop/
   - Click "Download for Windows"

2. **Run Installer**
   - Run the downloaded `.exe`
   - Follow the installation wizard
   - Enable WSL 2 when prompted (recommended)

3. **Start Docker Desktop**
   - Docker Desktop starts automatically
   - Look for the whale icon in system tray
   - Wait for "Docker Desktop is running"

4. **Enable WSL 2 Backend** (if not done)
   - Settings → General → ☑ Use the WSL 2 based engine

#### macOS

1. **Download Docker Desktop**
   - Go to: https://www.docker.com/products/docker-desktop/
   - Choose Intel or Apple Silicon version

2. **Install**
   - Open the downloaded `.dmg`
   - Drag Docker to Applications
   - Open Docker from Applications

3. **Authorize**
   - Grant permissions when prompted
   - Wait for Docker to start (whale icon in menu bar)

#### Linux (Ubuntu/Debian)

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
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Set up repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker Engine
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Add your user to docker group (logout required after)
sudo usermod -aG docker $USER

# Start Docker
sudo systemctl start docker
sudo systemctl enable docker
```

**Checkpoint:** Docker is installed ✓

---

### Task 2: Verify Installation (5 minutes)

Open a terminal (PowerShell, Terminal, or shell) and run:

1. **Check Docker Version**
   ```bash
   docker --version
   ```
   
   Expected output (version may vary):
   ```
   Docker version 24.0.x, build xxxxx
   ```

2. **Detailed Version Information**
   ```bash
   docker version
   ```
   
   This shows both Client and Server (daemon) versions.

3. **System Information**
   ```bash
   docker info
   ```
   
   Review the output for:
   - Server Version
   - Storage Driver
   - Total Memory
   - Docker Root Dir

**Checkpoint:** Docker responds to commands ✓

---

### Task 3: Run Hello World (5 minutes)

1. **Run the Official Test Container**
   ```bash
   docker run hello-world
   ```

2. **Understand What Happened**
   
   The output explains the process:
   ```
   1. Docker client contacted Docker daemon
   2. Daemon pulled "hello-world" image from Docker Hub
   3. Daemon created a container from the image
   4. Container ran and produced output
   5. Container exited
   ```

3. **Check What Was Created**
   ```bash
   # List images (hello-world is now cached)
   docker images
   
   # List all containers (including stopped)
   docker ps -a
   ```
   
   You'll see the hello-world container in "Exited" state.

**Checkpoint:** Hello-world runs successfully ✓

---

### Task 4: Run a Web Server (5 minutes)

1. **Run nginx Container**
   ```bash
   docker run -d --name my-nginx -p 8080:80 nginx
   ```
   
   Flags explained:
   - `-d` = detached (runs in background)
   - `--name my-nginx` = friendly name
   - `-p 8080:80` = map port 8080 (host) to 80 (container)

2. **Verify It's Running**
   ```bash
   docker ps
   ```
   
   Should show `my-nginx` in "Up" state.

3. **Test in Browser**
   - Open: http://localhost:8080
   - You should see "Welcome to nginx!"

4. **View Container Logs**
   ```bash
   docker logs my-nginx
   ```

5. **Stop and Remove**
   ```bash
   # Stop the container
   docker stop my-nginx
   
   # Verify it's stopped
   docker ps -a
   
   # Remove the container
   docker rm my-nginx
   
   # Verify removal
   docker ps -a
   ```

**Checkpoint:** nginx runs and serves web page ✓

---

## Verification Checklist

- [ ] Docker is installed
- [ ] `docker --version` shows version information
- [ ] `docker info` shows system details
- [ ] `hello-world` container ran successfully
- [ ] nginx container served web page on localhost:8080
- [ ] Can stop and remove containers

---

## Deliverables

1. Output of `docker version` command
2. Screenshot of nginx welcome page in browser
3. Output of `docker images` showing downloaded images

---

## Quick Reference

```bash
# Version checks
docker --version
docker version
docker info

# Image management
docker images                    # List images
docker pull nginx               # Download image
docker rmi nginx                # Remove image

# Container management  
docker ps                       # List running containers
docker ps -a                    # List all containers
docker run <image>              # Create and start container
docker stop <container>         # Stop container
docker rm <container>           # Remove container

# Logs and inspection
docker logs <container>         # View container logs
docker inspect <container>      # Detailed information

# Cleanup
docker system prune             # Remove unused data
docker container prune          # Remove stopped containers
```

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| "Cannot connect to Docker daemon" | Docker not running | Start Docker Desktop or daemon |
| Permission denied (Linux) | User not in docker group | Run `sudo usermod -aG docker $USER` and re-login |
| Port already in use | Another service on port | Change port: `-p 8081:80` |
| WSL 2 errors (Windows) | WSL not installed | Install WSL 2 from Microsoft Store |
| Slow image download | Network issues | Wait or check connection |

### Linux: Check Docker Service

```bash
# Check status
sudo systemctl status docker

# Start if not running
sudo systemctl start docker

# Enable auto-start
sudo systemctl enable docker
```

### Windows: Check WSL 2

```powershell
# Check WSL status
wsl --status

# Update WSL
wsl --update

# Set default version
wsl --set-default-version 2
```

---

## Clean-Up

Keep Docker installed for remaining exercises. Clean up exercise artifacts:

```bash
# Remove test containers
docker rm -f my-nginx

# Remove hello-world image (optional)
docker rmi hello-world

# General cleanup
docker system prune -f
```

---

## Additional Resources

- [Docker Desktop Documentation](https://docs.docker.com/desktop/)
- [Docker Engine Installation Guide](https://docs.docker.com/engine/install/)
- [Docker Getting Started Tutorial](https://docs.docker.com/get-started/)

