# Exercise 5: Docker Volumes with Database

## Objective

Implement persistent data storage using Docker volumes with PostgreSQL, ensuring data survives container restarts and recreation.

---

## Learning Outcomes

By completing this exercise, you will:
- Understand the need for persistent storage in containers
- Create and manage Docker named volumes
- Run PostgreSQL with persistent data
- Verify data persistence across container lifecycle
- Understand the difference between named volumes and bind mounts

---

## Prerequisites

- Completed previous Docker exercises
- Docker running
- Basic SQL knowledge

---

## Time Estimate

30 minutes

---

## The Problem

By default, container data is ephemeral:

```
┌───────────────────────────────────────────────────────────────┐
│                    WITHOUT VOLUMES                             │
├───────────────────────────────────────────────────────────────┤
│                                                                │
│  Container Running:        Container Stopped/Removed:         │
│  ┌─────────────────┐       ┌─────────────────┐               │
│  │  PostgreSQL     │  ──▶  │                 │               │
│  │  ┌───────────┐  │       │   DATA LOST!    │               │
│  │  │  Data     │  │       │                 │               │
│  │  └───────────┘  │       └─────────────────┘               │
│  └─────────────────┘                                          │
│                                                                │
│                    WITH VOLUMES                                │
│                                                                │
│  Container Running:        Container Stopped/Removed:         │
│  ┌─────────────────┐       ┌─────────────────┐               │
│  │  PostgreSQL     │  ──▶  │  New Container  │               │
│  │  ┌───────────┐  │       │  ┌───────────┐  │               │
│  │  │  Mount    │  │       │  │  Mount    │  │               │
│  │  └─────┬─────┘  │       │  └─────┬─────┘  │               │
│  └────────┼────────┘       └────────┼────────┘               │
│           │                         │                         │
│           ▼                         ▼                         │
│    ┌─────────────────────────────────────┐                   │
│    │         Docker Volume               │                   │
│    │         (persistent)                │                   │
│    │         DATA PRESERVED!             │                   │
│    └─────────────────────────────────────┘                   │
│                                                                │
└───────────────────────────────────────────────────────────────┘
```

---

## Tasks

### Task 1: Demonstrate Data Loss (5 minutes)

First, see what happens without volumes:

1. **Run PostgreSQL Without Volume**
   ```bash
   docker run -d \
     --name db-no-volume \
     -e POSTGRES_USER=testuser \
     -e POSTGRES_PASSWORD=testpass \
     -e POSTGRES_DB=testdb \
     postgres:15
   
   # Wait for startup
   sleep 5
   ```

2. **Create Some Data**
   ```bash
   docker exec -it db-no-volume psql -U testuser -d testdb -c "
   CREATE TABLE messages (id SERIAL PRIMARY KEY, text VARCHAR(100));
   INSERT INTO messages (text) VALUES ('Hello, World!');
   SELECT * FROM messages;
   "
   ```

3. **Remove Container**
   ```bash
   docker rm -f db-no-volume
   ```

4. **Start New Container (Same Settings)**
   ```bash
   docker run -d \
     --name db-no-volume \
     -e POSTGRES_USER=testuser \
     -e POSTGRES_PASSWORD=testpass \
     -e POSTGRES_DB=testdb \
     postgres:15
   
   sleep 5
   
   # Check for data
   docker exec -it db-no-volume psql -U testuser -d testdb -c "SELECT * FROM messages;"
   ```
   
   **Result:** Error! Table doesn't exist - data was lost!

5. **Clean Up**
   ```bash
   docker rm -f db-no-volume
   ```

**Checkpoint:** Demonstrated data loss without volumes ✓

---

### Task 2: Create Named Volume (10 minutes)

1. **Create a Named Volume**
   ```bash
   docker volume create postgres-data
   ```

2. **List Volumes**
   ```bash
   docker volume ls
   ```

3. **Inspect Volume**
   ```bash
   docker volume inspect postgres-data
   ```
   
   Note the `Mountpoint` - this is where Docker stores the data.

4. **Run PostgreSQL with Volume**
   ```bash
   docker run -d \
     --name postgres-db \
     -e POSTGRES_USER=admin \
     -e POSTGRES_PASSWORD=secretpassword \
     -e POSTGRES_DB=appdb \
     -v postgres-data:/var/lib/postgresql/data \
     -p 5432:5432 \
     postgres:15
   
   # Wait for startup
   sleep 10
   ```
   
   The `-v postgres-data:/var/lib/postgresql/data`:
   - `postgres-data` = named volume
   - `/var/lib/postgresql/data` = container path where PostgreSQL stores data

5. **Create Database Schema and Data**
   ```bash
   docker exec -it postgres-db psql -U admin -d appdb -c "
   CREATE TABLE users (
       id SERIAL PRIMARY KEY,
       username VARCHAR(50) NOT NULL UNIQUE,
       email VARCHAR(100) NOT NULL,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   
   CREATE TABLE products (
       id SERIAL PRIMARY KEY,
       name VARCHAR(100) NOT NULL,
       price DECIMAL(10, 2) NOT NULL,
       stock INT DEFAULT 0
   );
   
   INSERT INTO users (username, email) VALUES 
       ('alice', 'alice@example.com'),
       ('bob', 'bob@example.com'),
       ('charlie', 'charlie@example.com');
   
   INSERT INTO products (name, price, stock) VALUES
       ('Widget', 9.99, 100),
       ('Gadget', 24.99, 50),
       ('Gizmo', 14.99, 75);
   "
   ```

6. **Verify Data**
   ```bash
   docker exec -it postgres-db psql -U admin -d appdb -c "SELECT * FROM users;"
   docker exec -it postgres-db psql -U admin -d appdb -c "SELECT * FROM products;"
   ```

**Checkpoint:** Data created with named volume ✓

---

### Task 3: Verify Persistence (10 minutes)

1. **Stop the Container**
   ```bash
   docker stop postgres-db
   docker ps -a | grep postgres
   # Shows: Exited status
   ```

2. **Start It Again**
   ```bash
   docker start postgres-db
   sleep 5
   
   # Check data still exists
   docker exec -it postgres-db psql -U admin -d appdb -c "SELECT * FROM users;"
   ```
   
   **Data preserved!**

3. **Remove Container Completely**
   ```bash
   docker rm -f postgres-db
   docker ps -a | grep postgres
   # No container
   ```

4. **Create New Container with Same Volume**
   ```bash
   docker run -d \
     --name postgres-db-new \
     -e POSTGRES_USER=admin \
     -e POSTGRES_PASSWORD=secretpassword \
     -e POSTGRES_DB=appdb \
     -v postgres-data:/var/lib/postgresql/data \
     -p 5432:5432 \
     postgres:15
   
   sleep 10
   ```

5. **Verify Data Survived**
   ```bash
   docker exec -it postgres-db-new psql -U admin -d appdb -c "SELECT * FROM users;"
   docker exec -it postgres-db-new psql -U admin -d appdb -c "SELECT * FROM products;"
   ```
   
   **All data preserved even after container was deleted!**

**Checkpoint:** Data persists across container recreation ✓

---

### Task 4: Add More Data (5 minutes)

1. **Insert More Records**
   ```bash
   docker exec -it postgres-db-new psql -U admin -d appdb -c "
   INSERT INTO users (username, email) VALUES 
       ('david', 'david@example.com'),
       ('eve', 'eve@example.com');
   
   INSERT INTO products (name, price, stock) VALUES
       ('Thingamajig', 39.99, 25);
   "
   ```

2. **Verify**
   ```bash
   docker exec -it postgres-db-new psql -U admin -d appdb -c "SELECT COUNT(*) FROM users;"
   # Should show: 5
   
   docker exec -it postgres-db-new psql -U admin -d appdb -c "SELECT COUNT(*) FROM products;"
   # Should show: 4
   ```

3. **Check Volume Size**
   ```bash
   docker system df -v | grep postgres
   ```

---

### Task 5: Volume Management (5 minutes)

1. **List All Volumes**
   ```bash
   docker volume ls
   ```

2. **Inspect Volume Details**
   ```bash
   docker volume inspect postgres-data
   ```

3. **Check What's Using the Volume**
   ```bash
   docker ps -a --filter volume=postgres-data
   ```

4. **Backup Data (Conceptual)**
   ```bash
   # Create a dump using pg_dump
   docker exec -t postgres-db-new pg_dump -U admin appdb > backup.sql
   
   # View backup
   head -50 backup.sql
   ```

5. **Remove Volume (After Container)**
   ```bash
   # Note: Can only remove volumes not in use
   # This would fail:
   # docker volume rm postgres-data
   
   # Must remove container first
   docker rm -f postgres-db-new
   
   # Now can remove volume
   # WARNING: This deletes all data!
   # docker volume rm postgres-data
   ```

---

## Verification Checklist

- [ ] Demonstrated data loss without volumes
- [ ] Created a named Docker volume
- [ ] Ran PostgreSQL with persistent volume
- [ ] Created tables and inserted data
- [ ] Verified data survives container stop/start
- [ ] Verified data survives container deletion and recreation
- [ ] Understand volume lifecycle

---

## Deliverables

1. Output of `docker volume ls` showing your volume
2. Output of `docker volume inspect postgres-data`
3. Query results showing persisted data after container recreation
4. Brief explanation: How do volumes differ from bind mounts?

---

## Volume Types Comparison

| Type | Syntax | Use Case |
|------|--------|----------|
| Named Volume | `-v name:/path` | Production databases, application data |
| Bind Mount | `-v /host/path:/container/path` | Development, config files |
| Anonymous Volume | `-v /path` | Temporary data |
| tmpfs Mount | `--tmpfs /path` | Sensitive data, memory-only |

---

## Clean-Up

```bash
# Remove container
docker rm -f postgres-db-new

# Remove volume (WARNING: deletes data!)
docker volume rm postgres-data

# Remove backup file
rm -f backup.sql

# Clean all unused volumes
docker volume prune
```

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| Permission denied | Volume permissions | Check user/group settings |
| Volume not found | Typo in name | Verify with `docker volume ls` |
| Data still there | Volume not removed | Remove volume explicitly |
| Can't remove volume | Container using it | Remove container first |

---

## Bind Mount Example (Bonus)

For development, bind mounts link host directories:

```bash
# Create local directory
mkdir -p ~/docker-data/postgres

# Run with bind mount
docker run -d \
  --name postgres-bind \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=secret \
  -v ~/docker-data/postgres:/var/lib/postgresql/data \
  -p 5433:5432 \
  postgres:15

# Data visible on host
ls ~/docker-data/postgres/
```

---

## Additional Resources

- [Docker Volumes Documentation](https://docs.docker.com/storage/volumes/)
- [Manage Data in Docker](https://docs.docker.com/storage/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)

