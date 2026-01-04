# Lab: Multi-Container Application with Docker Compose

## Overview

In this exercise, you will create a complete multi-container application stack using Docker Compose. You'll configure a Python Flask web application with a PostgreSQL database, demonstrating service orchestration, networking, volumes, and health checks.

**Estimated Time:** 3-4 hours  
**Mode:** Individual Code Lab  
**Prerequisites:** Completed Docker containers, images, volumes, and Dockerfile exercises

---

## Learning Objectives

By completing this lab, you will be able to:
- Write a `docker-compose.yml` file from scratch
- Configure multi-service applications with dependencies
- Implement health checks for service readiness
- Use volumes for database persistence
- Manage environment variables securely
- Execute common Docker Compose commands

---

## The Scenario

Your development team needs a consistent local environment for the **Task Tracker API**—a simple Flask application that stores tasks in a PostgreSQL database. Currently, developers run the database and application separately, leading to "works on my machine" issues.

Your job: Create a Docker Compose configuration that launches the entire stack with a single command.

---

## Core Tasks

### Task 1: Project Setup (15 min)

1. Navigate to the `starter_code/` directory
2. Review the provided files:
   - `app/app.py` - Flask application with task CRUD endpoints
   - `app/requirements.txt` - Python dependencies
   - `app/Dockerfile` - Application Dockerfile (provided)
3. Verify you understand the application structure before proceeding

### Task 2: Create the Docker Compose File (45 min)

Create a `docker-compose.yml` file in the `starter_code/` directory with the following requirements:

**Service 1: `db` (PostgreSQL)**
- Use image: `postgres:15-alpine`
- Set environment variables:
  - `POSTGRES_USER`: taskuser
  - `POSTGRES_PASSWORD`: taskpass
  - `POSTGRES_DB`: taskdb
- Create a named volume `postgres-data` mounted to `/var/lib/postgresql/data`
- Add a health check using `pg_isready`

**Service 2: `web` (Flask Application)**
- Build from the current directory (use the provided Dockerfile)
- Map port `5000` to host port `5000`
- Set environment variable:
  - `DATABASE_URL`: `postgresql://taskuser:taskpass@db:5432/taskdb`
- Mount the `./app` directory to `/app` for live code reloading
- Depend on `db` with condition `service_healthy`
- Override command to: `flask run --host=0.0.0.0 --reload`

**Don't forget:**
- Define the `postgres-data` volume at the bottom of the file

#### Reference Solution: docker-compose.yml

Create a file named `docker-compose.yml` in the `starter_code/` directory with the following content:

```yaml
version: '3.8'

services:
  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_USER: taskuser
      POSTGRES_PASSWORD: taskpass
      POSTGRES_DB: taskdb
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U taskuser -d taskdb"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s

  web:
    build: ./app
    ports:
      - "5000:5000"
    environment:
      DATABASE_URL: postgresql://taskuser:taskpass@db:5432/taskdb
      FLASK_ENV: development
    volumes:
      - ./app:/app
    depends_on:
      db:
        condition: service_healthy
    command: flask run --host=0.0.0.0 --reload

volumes:
  postgres-data:
```

> **Note:** This `docker-compose.yml` file defines two services (`db` and `web`), configures health checks for proper startup order, and creates a named volume for database persistence.

### Task 3: Launch and Test (30 min)

1. **Start the stack:**
   ```bash
   docker compose up -d
   ```

2. **Verify services are running:**
   ```bash
   docker compose ps
   ```

3. **Check logs for any errors:**
   ```bash
   docker compose logs -f
   ```

4. **Test the API endpoints:**
   ```bash
   # Health check
   curl http://localhost:5000/health
   
   # Create a task
   curl -X POST http://localhost:5000/tasks \
     -H "Content-Type: application/json" \
     -d '{"title": "Learn Docker Compose", "description": "Complete the lab exercise"}'
   
   # List all tasks
   curl http://localhost:5000/tasks
   
   # Get a specific task
   curl http://localhost:5000/tasks/1
   ```

### Task 4: Verify Persistence (20 min)

1. **Create several tasks** using the API
2. **Stop and remove containers:**
   ```bash
   docker compose down
   ```
3. **Restart the stack:**
   ```bash
   docker compose up -d
   ```
4. **Verify your tasks still exist:**
   ```bash
   curl http://localhost:5000/tasks
   ```

**Question:** What would happen if you used `docker compose down -v` instead? Try it and explain.

### Task 5: Explore Container Interaction (20 min)

1. **Execute into the database container:**
   ```bash
   docker compose exec db psql -U taskuser -d taskdb
   ```

2. **Run SQL queries to inspect data:**
   ```sql
   SELECT * FROM tasks;
   \dt
   \q
   ```

3. **Execute into the web container:**
   ```bash
   docker compose exec web sh
   ```

4. **Verify the database connection from the web container:**
   ```bash
   # Inside the container
   env | grep DATABASE
   exit
   ```

---

## Stretch Goals (Optional)

### Stretch 1: Add a Redis Cache
Add a third service `redis` using the `redis:alpine` image. Update the Flask app to use Redis for caching (bonus: modify app.py).

### Stretch 2: Add Adminer
Add an `adminer` service (web-based database management) on port 8080. Connect to your PostgreSQL database through the UI.

### Stretch 3: Create Override File
Create a `docker-compose.override.yml` for development that:
- Enables Flask debug mode
- Adds verbose logging

---

## Definition of Done

- [ ] `docker-compose.yml` file created with correct syntax
- [ ] Both services start successfully with `docker compose up`
- [ ] API endpoints respond correctly
- [ ] Database data persists across container restarts
- [ ] Can execute commands inside both containers
- [ ] All provided test commands execute successfully

---

## Troubleshooting Guide

| Issue | Solution |
|-------|----------|
| Port 5000 already in use | Stop other services or change the host port mapping |
| Database connection refused | Ensure `depends_on` with `service_healthy` is configured |
| Permission denied on volumes | Check Docker Desktop file sharing settings |
| Container exits immediately | Check logs with `docker compose logs [service]` |
| Changes not reflecting | Rebuild with `docker compose up --build` |

---

## Submission

1. Commit your completed `docker-compose.yml` to your repository
2. Take a screenshot of `docker compose ps` showing running services
3. Take a screenshot of successful API responses
4. Answer the persistence question from Task 4 in a `NOTES.md` file
