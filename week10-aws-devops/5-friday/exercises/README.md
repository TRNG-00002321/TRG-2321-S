# Week 10 Friday Exercises: Jenkins CI/CD & Pipeline Automation

## Overview

**Day:** Friday (Week 10)
**Topic:** Jenkins CI/CD
**Mode:** Implementation (Code Lab)
**Duration:** 3-4 hours total

These exercises build your Jenkins pipeline skills from installation through creating a complete CI/CD capstone project that ties together the entire week.

---

## Exercise List

| # | Exercise | Focus Area | Duration |
|---|----------|------------|----------|
| 1 | [Jenkins Setup](./exercise_jenkins_setup/) | Installation, Initial Config | 30 min |
| 2 | [Freestyle Build](./exercise_freestyle_build/) | Jobs, Git, Artifacts | 45 min |
| 3 | [Jenkinsfile Creation](./exercise_jenkinsfile_creation/) | Pipeline as Code | 45 min |
| 4 | [Pipeline Triggers](./exercise_pipeline_triggers/) | Webhooks, Schedules | 30 min |
| 5 | [Week 10 Capstone](./exercise_week10_capstone/) | Complete CI/CD Pipeline | 60 min |

---

## Prerequisites

- Docker and Docker Compose installed
- Git installed and configured
- Completed Wednesday Docker exercises (Docker knowledge)
- Completed Thursday monitoring exercises (Prometheus/Grafana running)
- Web browser

---

## Quick Setup Check

```bash
# Docker ready
docker --version
docker compose version

# Git ready
git --version

# Optional: GitHub account for webhook exercise
```

---

## Skill Mapping

| Exercise | Skills Practiced |
|----------|------------------|
| Jenkins Setup | Installation, plugins, admin configuration |
| Freestyle Build | Job creation, SCM, build steps, artifacts |
| Jenkinsfile Creation | Pipeline syntax, stages, post actions |
| Pipeline Triggers | Webhooks, polling, scheduled builds |
| Week 10 Capstone | Full CI/CD: Docker build, test, deploy, monitor |

---

## Success Criteria

Complete all exercises and verify:
- [ ] Jenkins running at http://localhost:8080
- [ ] Freestyle job successfully builds from Git
- [ ] Pipeline job with multiple stages works
- [ ] Build triggers configured
- [ ] Capstone pipeline builds Docker image and deploys

---

## Week 10 Integration

Friday's exercises bring together everything from the week:

| Day | Topic | Used in Capstone |
|-----|-------|------------------|
| Tuesday | AWS | Deploy target, infrastructure |
| Wednesday | Docker | Build images, run containers |
| Thursday | DevOps/Monitoring | Pipeline design, metrics |
| Friday | Jenkins | Orchestrate everything |

---

## Getting Help

If stuck:
1. Review written content in `../written/`
2. Reference instructor demo in `../demos/INSTRUCTOR_GUIDE.md`
3. Check Jenkins documentation: https://www.jenkins.io/doc/
4. Use Jenkins Blue Ocean for better visualization

---

## Keep Running

Throughout Friday's exercises, keep Jenkins running:
```bash
# Start Jenkins
cd jenkins-lab
docker compose up -d

# Check status
docker compose ps
```

