# Week 10 Wednesday Exercises: Docker & Containerization

## Overview

**Day:** Wednesday (Week 10)
**Topic:** Docker & Containerization
**Mode:** Implementation (Code Lab)
**Duration:** 3-4 hours total

These exercises build your Docker skills from installation through advanced concepts like multi-stage builds and persistent storage.

---

## Exercise List

| # | Exercise | Focus Area | Duration |
|---|----------|------------|----------|
| 1 | [Docker Installation](./exercise_docker_installation/) | Setup, Verification, Hello World | 30 min |
| 2 | [Container Management](./exercise_container_management/) | Lifecycle, Ports, Environment | 45 min |
| 3 | [Dockerfile Python](./exercise_dockerfile_python/) | Build Images, Flask App | 45 min |
| 4 | [Multi-Stage Optimization](./exercise_multistage_optimization/) | Build Optimization, Size Reduction | 45 min |
| 5 | [Volume Database](./exercise_volume_database/) | Persistence, PostgreSQL | 30 min |

---

## Prerequisites

- Docker Desktop installed (Windows/Mac) or Docker Engine (Linux)
- At least 8GB RAM recommended
- ~10GB free disk space
- Terminal/command line access
- Text editor (VS Code recommended)

---

## Docker Installation Quick Check

Before starting, verify Docker is working:

```bash
# Check Docker version
docker --version

# Run hello-world
docker run hello-world

# Check Docker daemon
docker info
```

If these commands fail, complete Exercise 1 first.

---

## Skill Mapping

| Exercise | Skills Practiced |
|----------|------------------|
| Docker Installation | Installation, verification, basic commands |
| Container Management | `run`, `stop`, `rm`, ports, env vars, logs |
| Dockerfile Python | Dockerfile syntax, building images, Flask |
| Multi-Stage Optimization | Build stages, image size reduction, security |
| Volume Database | Data persistence, named volumes, bind mounts |

---

## Success Criteria

Complete all exercises and verify:
- [ ] Docker installed and `hello-world` runs
- [ ] Can manage container lifecycle (run, stop, rm)
- [ ] Built a custom Python Flask image
- [ ] Created multi-stage build with significant size reduction
- [ ] Database data persists across container restarts

---

## Resource Management

Docker uses system resources. Keep your system healthy:

```bash
# View Docker disk usage
docker system df

# Clean up unused resources
docker system prune

# Remove all stopped containers
docker container prune

# Remove unused images
docker image prune -a
```

---

## Getting Help

If stuck:
1. Review the written content in `../written/`
2. Reference the instructor demo guide in `../demos/INSTRUCTOR_GUIDE.md`
3. Check Docker documentation: https://docs.docker.com/
4. Ask your instructor or pair programming partner

