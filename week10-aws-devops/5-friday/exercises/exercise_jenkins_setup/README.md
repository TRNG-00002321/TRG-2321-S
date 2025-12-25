# Exercise 1: Jenkins Setup

## Objective

Install Jenkins using Docker, complete the initial setup wizard, and configure essential plugins to prepare for CI/CD pipeline development.

---

## Learning Outcomes

By completing this exercise, you will:
- Deploy Jenkins using Docker Compose
- Complete the initial setup wizard
- Install essential plugins
- Create an admin user
- Navigate the Jenkins interface
- Understand Jenkins architecture basics

---

## Prerequisites

- Docker and Docker Compose installed
- Ports 8080 and 50000 available
- Web browser

---

## Time Estimate

30 minutes

---

## Tasks

### Task 1: Create Jenkins Docker Setup (10 minutes)

1. **Create Working Directory**
   ```bash
   mkdir -p jenkins-lab
   cd jenkins-lab
   ```

2. **Create `docker-compose.yml`**
   ```bash
   cat > docker-compose.yml << 'EOF'
   version: '3.8'
   
   services:
     jenkins:
       image: jenkins/jenkins:lts
       container_name: jenkins
       ports:
         - "8080:8080"      # Web UI
         - "50000:50000"    # Agent communication
       volumes:
         - jenkins_home:/var/jenkins_home
         - /var/run/docker.sock:/var/run/docker.sock
       environment:
         - JAVA_OPTS=-Djenkins.install.runSetupWizard=true
       restart: unless-stopped
       user: root
   
   volumes:
     jenkins_home:
   EOF
   ```

3. **Start Jenkins**
   ```bash
   docker compose up -d
   ```

4. **Wait for Startup**
   ```bash
   # Wait for Jenkins to fully start
   sleep 30
   
   # Check logs
   docker compose logs jenkins | tail -20
   ```

5. **Get Initial Admin Password**
   ```bash
   docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```
   
   **Save this password!** You'll need it in the next step.

**Checkpoint:** Jenkins container running ✓

---

### Task 2: Complete Setup Wizard (10 minutes)

1. **Open Jenkins UI**
   
   Open browser: http://localhost:8080

2. **Unlock Jenkins**
   - Paste the initial admin password
   - Click "Continue"

3. **Install Plugins**
   - Select "Install suggested plugins"
   - Wait for installation (5-10 minutes)
   
   **Key plugins being installed:**
   - Git
   - Pipeline
   - Credentials
   - Blue Ocean
   - Docker Pipeline

4. **Create Admin User**
   ```
   Username: admin
   Password: admin123
   Confirm password: admin123
   Full name: Jenkins Admin
   Email: admin@example.com
   ```
   
   Click "Save and Continue"

5. **Instance Configuration**
   - Jenkins URL: `http://localhost:8080/`
   - Click "Save and Finish"

6. **Start Using Jenkins**
   - Click "Start using Jenkins"
   - You're now on the Jenkins Dashboard!

**Checkpoint:** Jenkins setup complete ✓

---

### Task 3: Install Additional Plugins (5 minutes)

1. **Navigate to Plugin Manager**
   - `Manage Jenkins → Plugins → Available plugins`

2. **Search and Install These Plugins**
   - `Docker` (if not installed)
   - `Docker Pipeline`
   - `Pipeline Stage View`
   - `Timestamper`
   - `Workspace Cleanup`

3. **Install Without Restart**
   - Check desired plugins
   - Click "Install without restart"

4. **Verify Installation**
   - Go to "Installed plugins"
   - Confirm plugins are listed

**Checkpoint:** Plugins installed ✓

---

### Task 4: Explore Jenkins Interface (5 minutes)

1. **Dashboard**
   - Shows all jobs
   - Build queue on left
   - Build executor status

2. **Manage Jenkins**
   - `System`: Global configuration
   - `Security`: Users, authentication
   - `Plugins`: Add/remove plugins
   - `Credentials`: Stored secrets

3. **New Item**
   - Types of jobs:
     - Freestyle project
     - Pipeline
     - Multibranch Pipeline
     - Folder

4. **Build History**
   - Shows recent builds across all jobs

5. **Blue Ocean** (if installed)
   - Click "Open Blue Ocean" on left menu
   - Modern, visual pipeline interface

---

## Verification Checklist

- [ ] Jenkins container running
- [ ] Successfully logged into Jenkins
- [ ] Admin user created
- [ ] Suggested plugins installed
- [ ] Can navigate Jenkins interface
- [ ] Understand main sections

---

## Deliverables

1. Screenshot of Jenkins Dashboard (logged in)
2. Screenshot of Installed Plugins page showing key plugins
3. Output of `docker compose ps`

---

## Quick Reference

### URLs
```
Jenkins UI:  http://localhost:8080
Blue Ocean:  http://localhost:8080/blue
```

### Credentials
```
Username: admin
Password: admin123
```

### Docker Commands
```bash
# Start Jenkins
docker compose up -d

# Stop Jenkins
docker compose stop

# View logs
docker compose logs -f jenkins

# Get admin password (first time)
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# Enter Jenkins container
docker exec -it jenkins bash
```

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| Can't access 8080 | Port conflict | Change port in docker-compose.yml |
| Plugin install hangs | Network issues | Retry or check connectivity |
| Container exits | Volume permissions | Run with `user: root` |
| Forgot password | Locked out | Reset via config.xml |

### Reset Admin Password

```bash
# Enter container
docker exec -it jenkins bash

# Edit config
sed -i 's/<useSecurity>true/<useSecurity>false/' /var/jenkins_home/config.xml

# Restart Jenkins
exit
docker compose restart jenkins

# Access without auth, then reconfigure security
```

---

## Keep Jenkins Running

Don't stop Jenkins! You need it for the remaining exercises.

```bash
# Verify it's running
docker compose ps
```

---

## Additional Resources

- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Jenkins Docker Hub](https://hub.docker.com/r/jenkins/jenkins)
- [Plugin Manager Guide](https://www.jenkins.io/doc/book/managing/plugins/)

