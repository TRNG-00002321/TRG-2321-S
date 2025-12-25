# Week 10 Thursday Exercises: DevOps Philosophy & Monitoring Stack

## Overview

**Day:** Thursday (Week 10)
**Topic:** DevOps, Prometheus, Grafana
**Mode:** Collaborative (Pair Programming)
**Duration:** 3-4 hours total

These exercises are designed for **pair programming**. Work with a partner using the Driver/Navigator model to build monitoring infrastructure and observability practices.

---

## 🤝 Pair Programming Guidelines

### Driver/Navigator Roles

| Role | Responsibilities |
|------|------------------|
| **Driver** | Types commands, executes queries, operates keyboard |
| **Navigator** | Guides strategy, checks docs, reviews code, catches errors |

**Switch roles every 15-20 minutes!**

### Collaboration Best Practices

1. **Communicate constantly** - Think aloud
2. **Question decisions** - "Why did you choose that approach?"
3. **Share knowledge** - Teach what you know
4. **Take breaks** - Step away when stuck
5. **Celebrate wins** - Acknowledge progress

---

## Exercise List

| # | Exercise | Focus Area | Duration |
|---|----------|------------|----------|
| 1 | [CI/CD Pipeline Design](./exercise_cicd_pipeline_design/) | DevOps Concepts, Architecture | 45 min |
| 2 | [Prometheus Installation](./exercise_prometheus_installation/) | Metrics Collection, Configuration | 45 min |
| 3 | [Grafana Setup](./exercise_grafana_setup/) | Visualization, Data Sources | 30 min |
| 4 | [Custom Dashboard](./exercise_custom_dashboard/) | Dashboard Design, Panels | 45 min |
| 5 | [PromQL Practice](./exercise_promql_practice/) | Query Language, Alerts | 30 min |

---

## Prerequisites

- Docker and Docker Compose installed
- Completed Wednesday's Docker exercises
- Browser for accessing UIs
- Partner for pair programming

---

## Technical Setup

Before starting exercises 2-5, ensure Docker Compose is available:

```bash
# Check Docker Compose version
docker compose version

# Create working directory
mkdir -p monitoring-lab
cd monitoring-lab
```

---

## Skill Mapping

| Exercise | Skills Practiced |
|----------|------------------|
| CI/CD Pipeline Design | DevOps philosophy, pipeline stages, deployment strategies |
| Prometheus Installation | Time-series databases, scrape configs, exporters |
| Grafana Setup | Data source configuration, dashboard import |
| Custom Dashboard | Panel creation, visualization types, variables |
| PromQL Practice | Query language, functions, alerting rules |

---

## Success Criteria

Complete all exercises and verify:
- [ ] Designed a complete CI/CD pipeline (documented)
- [ ] Prometheus running and scraping metrics
- [ ] Grafana connected to Prometheus
- [ ] Custom dashboard with at least 4 panels
- [ ] Working PromQL queries for key metrics
- [ ] Alert rules defined

---

## Deliverables by End of Day

1. **Pipeline Design Document** (Exercise 1)
2. **Working Prometheus instance** with targets up
3. **Grafana dashboard** (exported JSON)
4. **PromQL query collection** with explanations
5. **Reflection** on pair programming experience

---

## Getting Help

If stuck:
1. Review written content in `../written/`
2. Reference instructor demo in `../demos/INSTRUCTOR_GUIDE.md`
3. Check documentation:
   - Prometheus: https://prometheus.io/docs/
   - Grafana: https://grafana.com/docs/
4. Discuss with your partner first, then ask instructor

