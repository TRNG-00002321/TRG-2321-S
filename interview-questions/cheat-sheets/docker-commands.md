# Docker Commands Cheat Sheet

## Images

| Command | Description |
|---------|-------------|
| `docker images` | List all images |
| `docker pull <image>` | Download image |
| `docker build -t name:tag .` | Build from Dockerfile |
| `docker rmi <image>` | Remove image |
| `docker image prune` | Remove unused images |
| `docker tag source:tag target:tag` | Tag an image |
| `docker push <image>` | Push to registry |

## Containers

| Command | Description |
|---------|-------------|
| `docker ps` | List running containers |
| `docker ps -a` | List all containers |
| `docker run <image>` | Create and start container |
| `docker run -d <image>` | Run in background (detached) |
| `docker run -it <image> bash` | Interactive with terminal |
| `docker run --name myapp <image>` | Run with custom name |
| `docker run -p 8080:80 <image>` | Map port host:container |
| `docker run -v /host:/container <image>` | Mount volume |
| `docker run -e VAR=value <image>` | Set environment variable |
| `docker start <container>` | Start stopped container |
| `docker stop <container>` | Stop running container |
| `docker restart <container>` | Restart container |
| `docker rm <container>` | Remove container |
| `docker rm -f <container>` | Force remove running |
| `docker container prune` | Remove stopped containers |

## Container Inspection

| Command | Description |
|---------|-------------|
| `docker logs <container>` | View container logs |
| `docker logs -f <container>` | Follow logs |
| `docker logs --tail 100 <container>` | Last 100 lines |
| `docker inspect <container>` | Detailed info (JSON) |
| `docker exec -it <container> bash` | Open shell in container |
| `docker exec <container> command` | Run command in container |
| `docker top <container>` | Running processes |
| `docker stats` | Resource usage |
| `docker cp file.txt container:/path` | Copy file to container |
| `docker cp container:/path file.txt` | Copy from container |

## Dockerfile

```dockerfile
# Base image
FROM python:3.9-slim

# Set working directory
WORKDIR /app

# Copy dependency file first (layer caching)
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY . .

# Set environment variables
ENV APP_ENV=production

# Expose port
EXPOSE 8080

# Default command
CMD ["python", "app.py"]

# Or use ENTRYPOINT for fixed command
ENTRYPOINT ["python"]
CMD ["app.py"]
```

### Multi-Stage Build

```dockerfile
# Build stage
FROM maven:3.8-openjdk-11 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

## Docker Compose

### docker-compose.yml
```yaml
version: '3.8'

services:
  web:
    build: ./app
    ports:
      - "8080:80"
    environment:
      - DATABASE_URL=postgres://db:5432/mydb
    depends_on:
      - db
    volumes:
      - ./app:/app
    restart: always

  db:
    image: postgres:13
    environment:
      - POSTGRES_DB=mydb
      - POSTGRES_USER=user
      - POSTGRES_PASSWORD=password
    volumes:
      - db_data:/var/lib/postgresql/data
    
  redis:
    image: redis:6
    ports:
      - "6379:6379"

volumes:
  db_data:

networks:
  default:
    driver: bridge
```

### Docker Compose Commands

| Command | Description |
|---------|-------------|
| `docker-compose up` | Start all services |
| `docker-compose up -d` | Start in background |
| `docker-compose up --build` | Rebuild and start |
| `docker-compose down` | Stop and remove |
| `docker-compose down -v` | Also remove volumes |
| `docker-compose ps` | List services |
| `docker-compose logs` | View logs |
| `docker-compose logs -f web` | Follow specific service |
| `docker-compose exec web bash` | Shell into service |
| `docker-compose run web pytest` | Run one-off command |
| `docker-compose build` | Build images |
| `docker-compose pull` | Pull images |
| `docker-compose restart` | Restart services |
| `docker-compose stop` | Stop without removing |

## Volumes

| Command | Description |
|---------|-------------|
| `docker volume ls` | List volumes |
| `docker volume create myvolume` | Create volume |
| `docker volume inspect myvolume` | Volume details |
| `docker volume rm myvolume` | Remove volume |
| `docker volume prune` | Remove unused |

## Networks

| Command | Description |
|---------|-------------|
| `docker network ls` | List networks |
| `docker network create mynetwork` | Create network |
| `docker network inspect mynetwork` | Network details |
| `docker network connect mynetwork container` | Connect container |
| `docker network disconnect mynetwork container` | Disconnect |
| `docker network rm mynetwork` | Remove network |

## Cleanup

```bash
# Remove all stopped containers
docker container prune

# Remove unused images
docker image prune

# Remove unused volumes
docker volume prune

# Remove all unused (containers, images, networks)
docker system prune

# Remove everything including volumes
docker system prune -a --volumes

# Disk usage
docker system df
```

## Common Patterns

### Run database for testing
```bash
docker run -d \
  --name test-db \
  -e POSTGRES_PASSWORD=test \
  -e POSTGRES_DB=testdb \
  -p 5432:5432 \
  postgres:13
```

### Run Selenium for testing
```bash
docker run -d \
  --name selenium \
  -p 4444:4444 \
  -p 7900:7900 \
  --shm-size="2g" \
  selenium/standalone-chrome:latest
```

### Build and push image
```bash
docker build -t myapp:1.0 .
docker tag myapp:1.0 registry.example.com/myapp:1.0
docker push registry.example.com/myapp:1.0
```
