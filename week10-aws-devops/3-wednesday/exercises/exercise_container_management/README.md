# Exercise 2: Container Management

## Objective

Master the Docker container lifecycle: running, stopping, inspecting, and managing containers with various configurations.

---

## Learning Outcomes

By completing this exercise, you will:
- Manage container lifecycle (create, start, stop, restart, remove)
- Run containers in foreground and detached modes
- Configure port mappings and environment variables
- Execute commands inside running containers
- View and manage container logs
- Understand resource limits

---

## Prerequisites

- Completed Exercise 1 (Docker Installation)
- Docker running and verified
- Terminal access

---

## Time Estimate

45 minutes

---

## Tasks

### Task 1: Container Lifecycle (15 minutes)

1. **Run Container in Foreground**
   ```bash
   # Run alpine and execute a command
   docker run alpine echo "Hello from Alpine!"
   
   # Run interactive shell
   docker run -it alpine sh
   ```
   
   Inside the Alpine container:
   ```bash
   # Check OS
   cat /etc/os-release
   
   # List files
   ls -la
   
   # Exit the container
   exit
   ```

2. **Run Container in Background (Detached)**
   ```bash
   # Run nginx in background
   docker run -d --name web nginx
   
   # Verify it's running
   docker ps
   ```

3. **Container State Transitions**
   ```bash
   # Stop the container
   docker stop web
   docker ps        # Not shown
   docker ps -a     # Shows in "Exited" state
   
   # Start it again
   docker start web
   docker ps        # Running again
   
   # Restart (stop + start)
   docker restart web
   
   # Pause and unpause
   docker pause web
   docker ps        # Shows "Paused"
   docker unpause web
   ```

4. **Remove Containers**
   ```bash
   # Must stop first (or use -f)
   docker stop web
   docker rm web
   
   # Force remove running container
   docker run -d --name web2 nginx
   docker rm -f web2
   
   # Remove all stopped containers
   docker container prune
   ```

**Checkpoint:** Container lifecycle understood ✓

---

### Task 2: Port Mapping (10 minutes)

1. **Basic Port Mapping**
   ```bash
   # Map host port 8080 to container port 80
   docker run -d --name web -p 8080:80 nginx
   
   # Test
   curl http://localhost:8080
   # Or open in browser: http://localhost:8080
   ```

2. **Multiple Port Mappings**
   ```bash
   docker rm -f web
   
   # Map multiple ports
   docker run -d --name web \
     -p 8080:80 \
     -p 8443:443 \
     nginx
   
   # Check port mappings
   docker port web
   ```

3. **Random Port Assignment**
   ```bash
   docker rm -f web
   
   # Let Docker assign random host port
   docker run -d --name web -P nginx
   
   # Find the assigned port
   docker port web
   # Example output: 80/tcp -> 0.0.0.0:32768
   ```

4. **Bind to Specific Interface**
   ```bash
   docker rm -f web
   
   # Only accept connections from localhost
   docker run -d --name web -p 127.0.0.1:8080:80 nginx
   ```

**Checkpoint:** Port mapping mastered ✓

---

### Task 3: Environment Variables (10 minutes)

1. **Pass Environment Variables**
   ```bash
   docker rm -f web 2>/dev/null
   
   # Run with environment variables
   docker run -d --name mydb \
     -e MYSQL_ROOT_PASSWORD=secret \
     -e MYSQL_DATABASE=testdb \
     -e MYSQL_USER=testuser \
     -e MYSQL_PASSWORD=testpass \
     mysql:8
   
   # Wait for MySQL to start
   sleep 15
   ```

2. **Verify Environment Variables**
   ```bash
   # Check env vars inside container
   docker exec mydb env | grep MYSQL
   
   # Or use inspect
   docker inspect mydb --format '{{.Config.Env}}'
   ```

3. **Connect to MySQL**
   ```bash
   # Execute MySQL command
   docker exec -it mydb mysql -u testuser -p
   # Enter password: testpass
   
   # Inside MySQL:
   SHOW DATABASES;
   USE testdb;
   EXIT;
   ```

4. **Environment File**
   ```bash
   # Create env file
   cat > mysql.env << 'EOF'
   MYSQL_ROOT_PASSWORD=rootsecret
   MYSQL_DATABASE=appdb
   MYSQL_USER=appuser
   MYSQL_PASSWORD=appsecret
   EOF
   
   # Run with env file
   docker rm -f mydb
   docker run -d --name mydb --env-file mysql.env mysql:8
   
   # Clean up env file (contains secrets!)
   rm mysql.env
   ```

**Checkpoint:** Environment variables configured ✓

---

### Task 4: Executing Commands in Containers (5 minutes)

1. **Execute Single Command**
   ```bash
   # List files in nginx container
   docker run -d --name web nginx
   docker exec web ls -la /usr/share/nginx/html
   
   # Check nginx config
   docker exec web cat /etc/nginx/nginx.conf
   ```

2. **Interactive Shell**
   ```bash
   # Open bash shell (nginx uses Debian)
   docker exec -it web bash
   
   # Inside container:
   whoami
   pwd
   apt-get update
   apt-get install -y curl
   curl localhost
   exit
   ```

3. **Run as Different User**
   ```bash
   # Run as root
   docker exec -u root web whoami
   
   # Run as specific user (if exists)
   docker exec -u nginx web whoami
   ```

**Checkpoint:** Can execute commands in containers ✓

---

### Task 5: Container Logs and Inspection (5 minutes)

1. **View Logs**
   ```bash
   # Generate some logs
   curl http://localhost:8080
   curl http://localhost:8080/nonexistent
   
   # View logs
   docker logs web
   
   # Follow logs (like tail -f)
   docker logs -f web
   # Press Ctrl+C to stop
   
   # Last N lines
   docker logs --tail 5 web
   
   # Logs with timestamps
   docker logs -t web
   ```

2. **Inspect Container**
   ```bash
   # Full inspection (JSON)
   docker inspect web
   
   # Specific fields
   docker inspect web --format '{{.NetworkSettings.IPAddress}}'
   docker inspect web --format '{{.State.Status}}'
   docker inspect web --format '{{json .Config.Env}}' | jq
   ```

3. **Container Stats**
   ```bash
   # Live resource usage
   docker stats
   # Press Ctrl+C to stop
   
   # One-time snapshot
   docker stats --no-stream
   ```

4. **View Running Processes**
   ```bash
   docker top web
   ```

---

### Task 6: Resource Limits (Bonus)

1. **Memory Limits**
   ```bash
   # Run with 256MB memory limit
   docker run -d --name limited \
     --memory="256m" \
     --memory-swap="256m" \
     nginx
   
   # Check the limit
   docker inspect limited --format '{{.HostConfig.Memory}}'
   ```

2. **CPU Limits**
   ```bash
   docker rm -f limited
   
   # Limit to 0.5 CPUs
   docker run -d --name limited \
     --cpus="0.5" \
     nginx
   
   # Check stats
   docker stats limited --no-stream
   ```

---

## Verification Checklist

- [ ] Can run containers in foreground and detached mode
- [ ] Can stop, start, restart, and remove containers
- [ ] Configured port mappings successfully
- [ ] Passed environment variables to container
- [ ] Executed commands inside running container
- [ ] Viewed container logs
- [ ] Used `docker inspect` to view container details

---

## Deliverables

1. Output of `docker ps` showing running containers
2. Screenshot of MySQL connection via `docker exec`
3. Output of `docker logs` for your web container
4. Output of `docker stats --no-stream`

---

## Quick Reference

```bash
# Run options
docker run -d                    # Detached mode
docker run -it                   # Interactive with TTY
docker run --name NAME           # Assign name
docker run -p 8080:80           # Port mapping
docker run -e VAR=value         # Environment variable
docker run --env-file file      # Env from file
docker run --rm                  # Remove when exits
docker run --restart always     # Auto-restart policy

# Lifecycle
docker start CONTAINER          # Start stopped
docker stop CONTAINER           # Stop running (graceful)
docker kill CONTAINER           # Stop immediately
docker restart CONTAINER        # Restart
docker pause CONTAINER          # Pause
docker unpause CONTAINER        # Resume

# Execute
docker exec CONTAINER CMD       # Run command
docker exec -it CONTAINER bash  # Interactive shell
docker exec -u root CMD         # As different user

# Information
docker logs CONTAINER           # View logs
docker logs -f CONTAINER        # Follow logs
docker inspect CONTAINER        # Detailed info
docker stats                    # Resource usage
docker top CONTAINER            # Running processes
docker port CONTAINER           # Port mappings

# Cleanup
docker rm CONTAINER             # Remove stopped
docker rm -f CONTAINER          # Force remove
docker container prune          # Remove all stopped
```

---

## Clean-Up

```bash
# Stop and remove all exercise containers
docker rm -f web mydb limited 2>/dev/null

# Remove unused resources
docker system prune -f
```

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| Port already in use | Conflict | Use different port `-p 8081:80` |
| Container exits immediately | Command exits | Use `-it` for interactive |
| Exec fails "not running" | Container stopped | Start container first |
| MySQL connection refused | Not ready yet | Wait longer, check logs |

---

## Additional Resources

- [Docker Run Reference](https://docs.docker.com/engine/reference/run/)
- [Docker Exec Reference](https://docs.docker.com/engine/reference/commandline/exec/)
- [Container Networking](https://docs.docker.com/network/)

