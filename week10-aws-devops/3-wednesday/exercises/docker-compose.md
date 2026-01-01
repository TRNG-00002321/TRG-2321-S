# Docker Compose

## Learning Objectives
- Understand what Docker Compose is and when to use it
- Write and configure a `docker-compose.yml` file
- Manage multi-container applications with a single command
- Use Docker Compose for local development environments

## Why This Matters

*Connecting to the Weekly Epic: "From Code to Cloud: Mastering the Modern Deployment Pipeline"*

In the previous topics, you learned how to containerize individual applications using Docker. But real-world applications rarely exist in isolation—a typical web application might need a web server, a database, a cache layer, and perhaps a message queue. Managing each container separately with individual `docker run` commands quickly becomes tedious and error-prone.

**Docker Compose** solves this problem by allowing you to define and run multi-container Docker applications using a single configuration file. Instead of memorizing complex `docker run` commands with port mappings, volumes, and environment variables, you declare everything in a YAML file and launch your entire stack with one command: `docker compose up`.

This is exactly what DevOps teams use to:
- Create consistent local development environments that mirror production
- Simplify onboarding—new developers run one command to start everything
- Define infrastructure as code for reproducible deployments

## The Concept

### What is Docker Compose?

Docker Compose is a tool for defining and running multi-container Docker applications. It uses a YAML file (typically named `docker-compose.yml`) to configure your application's services, networks, and volumes. With a single command, you create and start all the services defined in your configuration.

### Key Components

| Component | Description |
|-----------|-------------|
| **Services** | Containers that make up your application (web, database, cache, etc.) |
| **Networks** | Custom networks for container communication |
| **Volumes** | Persistent storage shared between containers or host |
| **Configs/Secrets** | Configuration files and sensitive data management |

### The docker-compose.yml File Structure

```yaml
version: '3.8'  # Compose file format version

services:
  # Define each container as a service
  web:
    image: nginx:latest
    ports:
      - "8080:80"
    depends_on:
      - api
  
  api:
    build: ./app  # Build from a Dockerfile
    environment:
      - DATABASE_URL=postgres://db:5432/myapp
    volumes:
      - ./app:/code
    depends_on:
      - db
  
  db:
    image: postgres:15
    environment:
      - POSTGRES_PASSWORD=secret
    volumes:
      - db-data:/var/lib/postgresql/data

volumes:
  db-data:  # Named volume for database persistence
```

### Essential Docker Compose Commands

| Command | Description |
|---------|-------------|
| `docker compose up` | Create and start all services |
| `docker compose up -d` | Start in detached (background) mode |
| `docker compose down` | Stop and remove containers, networks |
| `docker compose ps` | List running services |
| `docker compose logs` | View output from services |
| `docker compose logs -f [service]` | Follow logs for a specific service |
| `docker compose build` | Build or rebuild services |
| `docker compose exec [service] [cmd]` | Execute a command in a running container |
| `docker compose stop` | Stop services without removing them |
| `docker compose restart` | Restart services |

### Service Configuration Options

#### Building Images

```yaml
services:
  app:
    build:
      context: ./app           # Build context directory
      dockerfile: Dockerfile   # Dockerfile name (if not default)
      args:
        - BUILD_ENV=production # Build-time arguments
```

#### Port Mapping

```yaml
services:
  web:
    ports:
      - "3000:3000"      # HOST:CONTAINER
      - "9229:9229"      # Debug port
```

#### Environment Variables

```yaml
services:
  api:
    environment:
      - NODE_ENV=development
      - DB_HOST=database
    env_file:
      - .env             # Load from a file
```

#### Volumes and Bind Mounts

```yaml
services:
  app:
    volumes:
      - ./src:/app/src          # Bind mount for development
      - node_modules:/app/node_modules  # Named volume
      
volumes:
  node_modules:
```

#### Dependencies and Startup Order

```yaml
services:
  api:
    depends_on:
      - db
      - redis
    # Note: depends_on only waits for containers to start,
    # not for services inside to be ready
```

#### Health Checks

```yaml
services:
  db:
    image: postgres:15
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5
      
  api:
    depends_on:
      db:
        condition: service_healthy
```

### Networking in Compose

Docker Compose automatically creates a default network for your application. All services can reach each other by their service name.

```yaml
services:
  web:
    # Can reach api at http://api:3000
    
  api:
    # Can reach db at postgres://db:5432
    
  db:
    # Service name becomes the hostname
```

For more complex setups, define custom networks:

```yaml
services:
  frontend:
    networks:
      - frontend-network
      
  api:
    networks:
      - frontend-network
      - backend-network
      
  db:
    networks:
      - backend-network

networks:
  frontend-network:
  backend-network:
```

## Practical Example: Python Flask + PostgreSQL

Here's a complete example for a Python Flask application with a PostgreSQL database:

**docker-compose.yml**
```yaml
version: '3.8'

services:
  web:
    build: .
    ports:
      - "5000:5000"
    environment:
      - FLASK_ENV=development
      - DATABASE_URL=postgresql://postgres:password@db:5432/flaskapp
    volumes:
      - .:/app
    depends_on:
      db:
        condition: service_healthy
    command: flask run --host=0.0.0.0

  db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=password
      - POSTGRES_DB=flaskapp
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 5s
      timeout: 5s
      retries: 5

volumes:
  postgres-data:
```

**Workflow:**
```bash
# Start the entire stack
docker compose up -d

# View logs
docker compose logs -f web

# Access the Flask app
curl http://localhost:5000

# Connect to the database
docker compose exec db psql -U postgres -d flaskapp

# Stop everything
docker compose down

# Stop and remove volumes (clean slate)
docker compose down -v
```

## Development vs Production Considerations

Docker Compose is primarily designed for **local development and testing**. For production deployments, you'll typically use:
- **Docker Swarm** (Docker's native orchestration)
- **Kubernetes** (industry-standard container orchestration)
- **AWS ECS/EKS** (managed container services)

However, Docker Compose files can be a starting point for production configurations, and the skills you learn here translate directly to these production tools.

### Override Files for Different Environments

```bash
# Base configuration
docker-compose.yml

# Development overrides
docker-compose.override.yml    # Applied automatically

# Production overrides  
docker-compose.prod.yml        # Applied with -f flag
```

```bash
# Development (uses override automatically)
docker compose up

# Production
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## Summary

| Key Takeaway | Description |
|--------------|-------------|
| **Single configuration** | Define entire application stack in one YAML file |
| **Simple commands** | `docker compose up` starts everything, `docker compose down` stops everything |
| **Service discovery** | Containers communicate using service names as hostnames |
| **Persistent data** | Named volumes survive container restarts |
| **Development focus** | Ideal for local development; production uses orchestrators |

Docker Compose bridges the gap between single-container Docker usage and full container orchestration, providing a practical tool that you'll use daily in development workflows.

## Additional Resources

- [Docker Compose Official Documentation](https://docs.docker.com/compose/)
- [Compose File Reference](https://docs.docker.com/compose/compose-file/)
- [Awesome Compose - Sample Applications](https://github.com/docker/awesome-compose)
