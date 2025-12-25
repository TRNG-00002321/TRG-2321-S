# Introduction to Jenkins

## Learning Objectives

- Explain what Jenkins is and its role in CI/CD
- Understand Jenkins architecture (master and agents)
- Describe the Jenkins ecosystem and community
- Compare Jenkins to other CI/CD tools
- Identify common Jenkins use cases
- Access and navigate the Jenkins web interface

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Jenkins is the most widely adopted CI/CD tool in the industry, automating the path from code commit to production deployment. It orchestrates builds, runs tests, and deploys applications—the backbone of continuous integration. When developers push code, Jenkins determines if it's ready for production.

As a quality engineer, Jenkins is where your automated tests live. Unit tests, integration tests, performance tests—all triggered and reported by Jenkins pipelines. Understanding Jenkins means understanding how quality gates work in modern software delivery.

## The Concept

### What is Jenkins?

```
┌─────────────────────────────────────────────────────────────────────┐
│                      What is Jenkins?                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Jenkins is an open-source automation server that enables          │
│   continuous integration and continuous delivery (CI/CD)            │
│                                                                      │
│   KEY CAPABILITIES                                                   │
│   ────────────────                                                   │
│   • Build automation      Compile, package, create artifacts       │
│   • Test automation       Run unit, integration, E2E tests         │
│   • Deployment            Deploy to any environment                 │
│   • Scheduled tasks       Cron-like job scheduling                  │
│   • Workflow orchestration Pipeline as code                        │
│                                                                      │
│   JENKINS TIMELINE                                                   │
│   ────────────────                                                   │
│   2004    Hudson project started at Sun Microsystems               │
│   2011    Forked to Jenkins after Oracle acquisition               │
│   2016    Pipeline as Code introduced (Jenkinsfile)                │
│   2017    Blue Ocean UI launched                                   │
│   Today   Most popular CI/CD tool, 1500+ plugins                   │
│                                                                      │
│   WHY JENKINS?                                                       │
│   ────────────                                                       │
│   ✓ Free and open source                                           │
│   ✓ Massive plugin ecosystem                                       │
│   ✓ Self-hosted (full control)                                     │
│   ✓ Language/platform agnostic                                     │
│   ✓ Large community and documentation                              │
│   ✓ Pipeline as code (Jenkinsfile)                                 │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Jenkins Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                   Jenkins Master-Agent Architecture                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│                        ┌─────────────────────┐                       │
│                        │   Jenkins Master    │                       │
│                        │   (Controller)      │                       │
│                        │                     │                       │
│                        │  • Schedule jobs    │                       │
│                        │  • Web UI           │                       │
│                        │  • Configuration    │                       │
│                        │  • Plugin mgmt      │                       │
│                        │  • Build dispatch   │                       │
│                        └──────────┬──────────┘                       │
│                                   │                                  │
│              ┌────────────────────┼────────────────────┐            │
│              │                    │                    │            │
│              ▼                    ▼                    ▼            │
│    ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐   │
│    │   Agent 1       │  │   Agent 2       │  │   Agent 3       │   │
│    │   (Linux)       │  │   (Windows)     │  │   (Docker)      │   │
│    │                 │  │                 │  │                 │   │
│    │  • Run builds   │  │  • Run builds   │  │  • Run builds   │   │
│    │  • Execute tests│  │  • Execute tests│  │  • Execute tests│   │
│    │  • Report back  │  │  • Report back  │  │  • Report back  │   │
│    └─────────────────┘  └─────────────────┘  └─────────────────┘   │
│                                                                      │
│   MASTER RESPONSIBILITIES           AGENT RESPONSIBILITIES          │
│   ───────────────────────           ─────────────────────           │
│   • Scheduling builds               • Executing build steps        │
│   • Monitoring agents               • Running tests                │
│   • Recording results               • Deploying applications       │
│   • Serving web interface           • Reporting status             │
│   • Managing configuration          • Providing compute            │
│                                                                      │
│   NOTE: For small setups, master can also execute builds           │
│         For production, use dedicated agents                        │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Jenkins Web Interface

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Jenkins Web Interface                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  JENKINS  [Search bar                  ] 🔔 admin ▼  ?     │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │ ┌──────────────┐                                            │   │
│   │ │ Dashboard    │  BUILD QUEUE                               │   │
│   │ │ New Item     │  └ (empty)                                 │   │
│   │ │ People       │                                            │   │
│   │ │ Build History│  BUILD EXECUTOR STATUS                     │   │
│   │ │ Manage       │  Master                                    │   │
│   │ │ My Views     │  └ Idle                                    │   │
│   │ │ Credentials  │  Agent-Linux                               │   │
│   │ │ New View     │  └ Building my-app #45                     │   │
│   │ └──────────────┘                                            │   │
│   │                                                              │   │
│   │  All Jobs                                                    │   │
│   │  ┌────────────────────────────────────────────────────────┐ │   │
│   │  │ S  │ Name           │ Last Success │ Last Failure    │ │   │
│   │  │────────────────────────────────────────────────────────│ │   │
│   │  │ ✓  │ my-app         │ 2 hr ago     │ 3 days ago      │ │   │
│   │  │ ✓  │ api-service    │ 1 hr ago     │ 1 week ago      │ │   │
│   │  │ ✗  │ frontend       │ 5 hr ago     │ 30 min ago      │ │   │
│   │  │ ◐  │ integration    │ (building)   │ 2 hr ago        │ │   │
│   │  └────────────────────────────────────────────────────────┘ │   │
│   │                                                              │   │
│   │  Legend:  ✓ Success  ✗ Failure  ◐ Building  ○ Not built    │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   KEY SECTIONS                                                       │
│   ────────────                                                       │
│   Dashboard      List of all jobs and their status                 │
│   New Item       Create jobs (Freestyle, Pipeline, etc.)           │
│   Build History  Recent builds across all jobs                     │
│   Manage Jenkins System configuration, plugins, security           │
│   Credentials    Stored secrets (passwords, SSH keys, tokens)      │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Jenkins vs Other CI/CD Tools

```
┌─────────────────────────────────────────────────────────────────────┐
│              Jenkins vs Other CI/CD Tools                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   JENKINS                                                            │
│   ───────                                                            │
│   ✓ Self-hosted, full control                                      │
│   ✓ 1500+ plugins for anything                                     │
│   ✓ Free and open source                                           │
│   ✗ Requires maintenance                                           │
│   ✗ Steeper learning curve                                         │
│                                                                      │
│   GITHUB ACTIONS                                                     │
│   ──────────────                                                     │
│   ✓ Native GitHub integration                                      │
│   ✓ No infrastructure to manage                                    │
│   ✓ YAML-based configuration                                       │
│   ✗ Vendor lock-in                                                 │
│   ✗ Limited for complex workflows                                  │
│                                                                      │
│   GITLAB CI/CD                                                       │
│   ────────────                                                       │
│   ✓ Integrated with GitLab                                         │
│   ✓ Built-in container registry                                    │
│   ✓ Auto DevOps features                                           │
│   ✗ Best with GitLab ecosystem                                     │
│                                                                      │
│   CIRCLECI / TRAVIS CI                                              │
│   ────────────────────                                              │
│   ✓ Cloud-hosted, minimal setup                                    │
│   ✓ Good for open source                                           │
│   ✗ Can be expensive at scale                                      │
│   ✗ Less customizable                                              │
│                                                                      │
│   When to choose Jenkins:                                           │
│   • Complex, enterprise workflows                                   │
│   • Need full control over infrastructure                          │
│   • Require specific plugins/integrations                          │
│   • On-premises requirements                                        │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Jenkins Ecosystem

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Jenkins Ecosystem                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   CORE JENKINS                                                       │
│   ────────────                                                       │
│   jenkins.war          The main application                        │
│   $JENKINS_HOME        Configuration, jobs, plugins storage        │
│                                                                      │
│   PLUGIN CATEGORIES                                                  │
│   ─────────────────                                                  │
│   Source Control       Git, SVN, Mercurial                         │
│   Build Tools          Maven, Gradle, npm, Docker                  │
│   Testing              JUnit, TestNG, Allure                       │
│   Notifications        Slack, Email, Teams                         │
│   Cloud                AWS, Azure, Kubernetes                      │
│   Security             LDAP, SAML, Role-based access              │
│   UI                   Blue Ocean, Dashboard View                  │
│                                                                      │
│   RELATED TOOLS                                                      │
│   ─────────────                                                      │
│   Configuration as Code (JCasC)   Define Jenkins via YAML          │
│   Job DSL                          Generate jobs programmatically  │
│   Pipeline Shared Libraries        Reusable pipeline code          │
│                                                                      │
│   COMMUNITY                                                          │
│   ─────────                                                          │
│   jenkins.io           Official website and documentation          │
│   plugins.jenkins.io   Plugin directory                            │
│   issues.jenkins.io    Bug tracker                                 │
│   community.jenkins.io Forums and discussions                      │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Running Jenkins with Docker

```bash
# Pull Jenkins LTS image
docker pull jenkins/jenkins:lts

# Run Jenkins (basic)
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  jenkins/jenkins:lts

# Run with persistent storage
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  jenkins/jenkins:lts

# Get initial admin password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# Access Jenkins at http://localhost:8080
```

### Docker Compose Setup

```yaml
# docker-compose.yml
version: '3.8'

services:
  jenkins:
    image: jenkins/jenkins:lts
    container_name: jenkins
    ports:
      - "8080:8080"    # Web UI
      - "50000:50000"  # Agent communication
    volumes:
      - jenkins_home:/var/jenkins_home
      - /var/run/docker.sock:/var/run/docker.sock  # Docker in Docker
    environment:
      - JAVA_OPTS=-Djenkins.install.runSetupWizard=false
    restart: unless-stopped

volumes:
  jenkins_home:
```

### Jenkins with Pre-installed Plugins

```dockerfile
# Dockerfile
FROM jenkins/jenkins:lts

# Skip initial setup wizard
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

# Install plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

# Copy configuration
COPY jenkins.yaml /var/jenkins_home/jenkins.yaml
ENV CASC_JENKINS_CONFIG /var/jenkins_home/jenkins.yaml

USER jenkins
```

```text
# plugins.txt
git
workflow-aggregator
docker-workflow
blueocean
credentials-binding
pipeline-stage-view
```

### Jenkins Configuration as Code

```yaml
# jenkins.yaml (JCasC)
jenkins:
  systemMessage: "Jenkins configured via JCasC"
  numExecutors: 2
  mode: NORMAL
  
  securityRealm:
    local:
      allowsSignup: false
      users:
        - id: admin
          password: ${JENKINS_ADMIN_PASSWORD}
  
  authorizationStrategy:
    globalMatrix:
      permissions:
        - "Overall/Administer:admin"
        - "Overall/Read:authenticated"
  
  nodes:
    - permanent:
        name: "linux-agent"
        remoteFS: "/home/jenkins"
        launcher:
          ssh:
            host: "agent-host"
            credentialsId: "ssh-agent-creds"

unclassified:
  location:
    url: http://jenkins.example.com/
```

### Jenkins CLI Usage

```bash
# Download Jenkins CLI
wget http://localhost:8080/jnlpJars/jenkins-cli.jar

# List all jobs
java -jar jenkins-cli.jar -s http://localhost:8080 \
  -auth admin:password list-jobs

# Build a job
java -jar jenkins-cli.jar -s http://localhost:8080 \
  -auth admin:password build my-job

# Get job configuration
java -jar jenkins-cli.jar -s http://localhost:8080 \
  -auth admin:password get-job my-job > job-config.xml

# Create job from XML
java -jar jenkins-cli.jar -s http://localhost:8080 \
  -auth admin:password create-job new-job < job-config.xml

# Install plugin
java -jar jenkins-cli.jar -s http://localhost:8080 \
  -auth admin:password install-plugin git -restart

# Safe restart
java -jar jenkins-cli.jar -s http://localhost:8080 \
  -auth admin:password safe-restart
```

### REST API Examples

```bash
# Get Jenkins version
curl http://localhost:8080/api/json

# Get job status
curl http://localhost:8080/job/my-job/api/json

# Trigger build
curl -X POST http://localhost:8080/job/my-job/build \
  --user admin:password

# Trigger build with parameters
curl -X POST "http://localhost:8080/job/my-job/buildWithParameters?BRANCH=develop" \
  --user admin:password

# Get build console output
curl http://localhost:8080/job/my-job/lastBuild/consoleText \
  --user admin:password

# Get build status
curl http://localhost:8080/job/my-job/lastBuild/api/json?tree=result,building
```

## Summary

- **Jenkins** is the most popular open-source CI/CD automation server
- **Architecture**: Master (controller) schedules and monitors; Agents execute builds
- **Web interface** provides job management, build history, and configuration
- **Plugin ecosystem** (1500+) extends Jenkins for any tool or platform
- **Self-hosted** gives full control but requires maintenance
- Jenkins can run via Docker, native install, or cloud platforms

## Additional Resources

- [Jenkins Documentation](https://www.jenkins.io/doc/) - Official comprehensive documentation
- [Jenkins Tutorials](https://www.jenkins.io/doc/tutorials/) - Step-by-step guides
- [Plugin Index](https://plugins.jenkins.io/) - Searchable plugin directory

