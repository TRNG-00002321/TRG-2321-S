# Jenkins Agents

## Learning Objectives

- Understand the master-agent architecture in Jenkins
- Configure permanent and cloud-based agents
- Use labels to direct jobs to specific agents
- Set up Docker-based agents for isolated builds
- Manage agent resources and executors
- Troubleshoot common agent connection issues

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

The Jenkins master schedules jobs, but agents do the heavy lifting. Agents provide the compute resources, tools, and environments needed to build and test your code. Proper agent configuration enables parallel execution, platform-specific builds, and isolated build environments.

As a quality engineer, agent selection affects where tests run. You might need specific agents for browser testing (with browsers installed), performance testing (with adequate resources), or platform-specific testing (Linux, Windows, Mac).

## The Concept

### Master-Agent Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Jenkins Master-Agent Architecture                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│                    ┌───────────────────────────┐                    │
│                    │     JENKINS MASTER        │                    │
│                    │     (Controller)          │                    │
│                    │                           │                    │
│                    │  • Web UI                 │                    │
│                    │  • Job scheduling         │                    │
│                    │  • Agent management       │                    │
│                    │  • Plugin management      │                    │
│                    │  • Results storage        │                    │
│                    └───────────┬───────────────┘                    │
│                                │                                     │
│           ┌────────────────────┼────────────────────┐               │
│           │                    │                    │               │
│           ▼                    ▼                    ▼               │
│   ┌───────────────┐   ┌───────────────┐   ┌───────────────┐        │
│   │   AGENT 1     │   │   AGENT 2     │   │   AGENT 3     │        │
│   │  (Permanent)  │   │  (Cloud/EC2)  │   │  (Docker)     │        │
│   │               │   │               │   │               │        │
│   │ Label: linux  │   │ Label: large  │   │ Label: node16 │        │
│   │ Executors: 4  │   │ Executors: 8  │   │ Executors: 1  │        │
│   │               │   │               │   │               │        │
│   │ Tools:        │   │ Tools:        │   │ Container:    │        │
│   │ • Java 17     │   │ • Java 17     │   │ • Node 16     │        │
│   │ • Maven 3     │   │ • Docker      │   │ • npm         │        │
│   │ • Git         │   │ • kubectl     │   │               │        │
│   └───────────────┘   └───────────────┘   └───────────────┘        │
│                                                                      │
│   COMMUNICATION METHODS                                             │
│   ─────────────────────                                             │
│   SSH:        Master connects to agent via SSH                     │
│   JNLP:       Agent connects to master (outbound from agent)       │
│   WebSocket:  JNLP over WebSocket (firewall-friendly)              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Agent Types

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Agent Types                                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   PERMANENT AGENTS                                                   │
│   ────────────────                                                   │
│   Always-on servers connected to Jenkins                            │
│   • Physical servers or VMs                                         │
│   • Fixed capacity                                                  │
│   • Persistent tool installations                                   │
│   • Good for: Dedicated build servers, specialized hardware        │
│                                                                      │
│   CLOUD AGENTS                                                       │
│   ────────────                                                       │
│   Dynamically provisioned from cloud providers                      │
│   • AWS EC2, Azure VMs, GCP instances                              │
│   • Scale up/down based on demand                                  │
│   • Cost-efficient (pay only when building)                        │
│   • Good for: Variable workloads, cost optimization                │
│                                                                      │
│   DOCKER AGENTS                                                      │
│   ─────────────                                                      │
│   Run builds inside Docker containers                               │
│   • Isolated, reproducible environments                            │
│   • Any Docker image as build environment                          │
│   • Clean environment for each build                               │
│   • Good for: Dependency isolation, reproducibility                │
│                                                                      │
│   KUBERNETES AGENTS                                                  │
│   ──────────────────                                                 │
│   Run builds as Kubernetes pods                                     │
│   • Define pod template with containers                            │
│   • Autoscaling via Kubernetes                                     │
│   • Good for: Cloud-native, container-based builds                 │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Labels and Node Selection

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Agent Labels                                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Labels categorize agents by capability                            │
│                                                                      │
│   COMMON LABEL PATTERNS                                             │
│   ─────────────────────                                             │
│   Platform:    linux, windows, mac                                 │
│   Size:        small, medium, large                                │
│   Tools:       java11, java17, node16, python3                     │
│   Purpose:     build, test, deploy, performance                    │
│   Region:      us-east, eu-west, ap-south                          │
│                                                                      │
│   LABEL EXPRESSIONS                                                  │
│   ─────────────────                                                  │
│   agent { label 'linux' }           Single label                   │
│   agent { label 'linux && java17' } Both labels (AND)              │
│   agent { label 'linux || windows' } Either label (OR)             │
│   agent { label '!windows' }         Not this label                │
│   agent { label 'linux && !arm' }   Combine AND/NOT                │
│                                                                      │
│   EXAMPLE SETUP                                                      │
│   ─────────────                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Agent: build-linux-01                                       │   │
│   │  Labels: linux java17 maven docker                          │   │
│   │                                                              │   │
│   │  Agent: build-linux-02                                       │   │
│   │  Labels: linux java17 gradle docker large                   │   │
│   │                                                              │   │
│   │  Agent: test-windows-01                                      │   │
│   │  Labels: windows java17 browser-tests                       │   │
│   │                                                              │   │
│   │  Pipeline: agent { label 'linux && docker' }                │   │
│   │  Matches: build-linux-01, build-linux-02                    │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Executors

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Executors                                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Executor = one build slot on an agent                            │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Agent: build-server-01                                      │   │
│   │  Executors: 4                                                │   │
│   │                                                              │   │
│   │  ┌──────────────┐ ┌──────────────┐                          │   │
│   │  │ Executor 1   │ │ Executor 2   │                          │   │
│   │  │ Job: app-1   │ │ Job: app-2   │                          │   │
│   │  │ Status: Busy │ │ Status: Busy │                          │   │
│   │  └──────────────┘ └──────────────┘                          │   │
│   │                                                              │   │
│   │  ┌──────────────┐ ┌──────────────┐                          │   │
│   │  │ Executor 3   │ │ Executor 4   │                          │   │
│   │  │ Status: Idle │ │ Job: api-1   │                          │   │
│   │  │              │ │ Status: Busy │                          │   │
│   │  └──────────────┘ └──────────────┘                          │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   HOW MANY EXECUTORS?                                               │
│   ───────────────────                                               │
│   General rule: # of CPU cores (or slightly higher)                │
│   • CPU-bound builds: = cores                                      │
│   • I/O-bound builds: > cores                                      │
│   • Memory-intensive: < cores                                      │
│                                                                      │
│   MASTER EXECUTORS                                                   │
│   ────────────────                                                   │
│   By default, master can run builds (executors > 0)                │
│   Best practice: Set master executors to 0                         │
│   Run all builds on agents for security and isolation              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Pipeline with Label-based Agent

```groovy
pipeline {
    agent { label 'linux && java17' }
    
    stages {
        stage('Build') {
            steps {
                sh 'java -version'
                sh 'mvn clean compile'
            }
        }
    }
}
```

### Pipeline with Docker Agent

```groovy
pipeline {
    agent {
        docker {
            image 'maven:3.8-openjdk-17'
            args '-v $HOME/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn --version'
                sh 'mvn clean package'
            }
        }
    }
}
```

### Different Agents per Stage

```groovy
pipeline {
    agent none  // No global agent
    
    stages {
        stage('Build') {
            agent { label 'linux' }
            steps {
                sh 'mvn clean package'
                stash name: 'build-output', includes: 'target/*.jar'
            }
        }
        
        stage('Test Linux') {
            agent { label 'linux' }
            steps {
                unstash 'build-output'
                sh 'java -jar target/app.jar --test'
            }
        }
        
        stage('Test Windows') {
            agent { label 'windows' }
            steps {
                unstash 'build-output'
                bat 'java -jar target\\app.jar --test'
            }
        }
        
        stage('Deploy') {
            agent { label 'deploy' }
            steps {
                unstash 'build-output'
                sh './deploy.sh'
            }
        }
    }
}
```

### Docker Agent with Dockerfile

```groovy
pipeline {
    agent {
        dockerfile {
            filename 'Dockerfile.build'
            dir 'ci'
            additionalBuildArgs '--build-arg VERSION=1.0'
            args '-v /tmp:/tmp'
        }
    }
    
    stages {
        stage('Build') {
            steps {
                sh 'make build'
            }
        }
    }
}
```

```dockerfile
# ci/Dockerfile.build
FROM node:18
ARG VERSION
RUN npm install -g yarn
WORKDIR /app
```

### Kubernetes Agent

```groovy
pipeline {
    agent {
        kubernetes {
            yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    image: maven:3.8-openjdk-17
    command:
    - sleep
    args:
    - infinity
  - name: docker
    image: docker:dind
    securityContext:
      privileged: true
'''
            defaultContainer 'maven'
        }
    }
    
    stages {
        stage('Build') {
            steps {
                container('maven') {
                    sh 'mvn clean package'
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                container('docker') {
                    sh 'docker build -t myapp .'
                }
            }
        }
    }
}
```

### Agent Configuration via JCasC

```yaml
# jenkins.yaml
jenkins:
  numExecutors: 0  # Master shouldn't run builds
  
  nodes:
    - permanent:
        name: "linux-build-01"
        remoteFS: "/home/jenkins"
        numExecutors: 4
        labelString: "linux java17 maven docker"
        mode: EXCLUSIVE
        retentionStrategy: "always"
        launcher:
          ssh:
            host: "build-01.example.com"
            port: 22
            credentialsId: "jenkins-ssh-key"
            javaPath: "/usr/bin/java"
            sshHostKeyVerificationStrategy:
              knownHostsFileKeyVerificationStrategy: {}
    
    - permanent:
        name: "windows-test-01"
        remoteFS: "C:\\Jenkins"
        numExecutors: 2
        labelString: "windows browser-test"
        launcher:
          jnlp:
            workDirSettings:
              disabled: false
              internalDir: "remoting"
```

### Cloud Agent (EC2)

```yaml
# jenkins.yaml (with EC2 plugin)
jenkins:
  clouds:
    - amazonEC2:
        name: "aws-ec2"
        region: "us-east-1"
        credentialsId: "aws-credentials"
        templates:
          - description: "Linux Build Agent"
            ami: "ami-0abcdef1234567890"
            instanceType: "t3.medium"
            labels: "linux ec2"
            remoteFS: "/home/ec2-user"
            remoteAdmin: "ec2-user"
            connectionStrategy: PRIVATE_IP
            idleTerminationMinutes: "30"
            numExecutors: 2
```

### JNLP Agent Launch Script

```bash
#!/bin/bash
# agent-launch.sh - Run on agent machine

JENKINS_URL="https://jenkins.example.com"
AGENT_NAME="linux-agent-01"
AGENT_SECRET="abc123def456..."  # From Jenkins agent page
AGENT_WORKDIR="/home/jenkins/agent"

mkdir -p $AGENT_WORKDIR

# Download agent JAR
curl -sO ${JENKINS_URL}/jnlpJars/agent.jar

# Launch agent
java -jar agent.jar \
  -jnlpUrl ${JENKINS_URL}/computer/${AGENT_NAME}/jenkins-agent.jnlp \
  -secret ${AGENT_SECRET} \
  -workDir ${AGENT_WORKDIR}
```

### Agent as Docker Container

```bash
# Run Jenkins agent as Docker container
docker run -d \
  --name jenkins-agent \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v jenkins-agent-workspace:/home/jenkins/agent \
  -e JENKINS_URL=https://jenkins.example.com \
  -e JENKINS_AGENT_NAME=docker-agent-01 \
  -e JENKINS_SECRET=abc123def456 \
  jenkins/inbound-agent
```

```yaml
# docker-compose.yml
version: '3.8'

services:
  jenkins-agent:
    image: jenkins/inbound-agent
    environment:
      JENKINS_URL: https://jenkins.example.com
      JENKINS_AGENT_NAME: docker-agent-01
      JENKINS_SECRET: ${AGENT_SECRET}
    volumes:
      - agent-workspace:/home/jenkins/agent
      - /var/run/docker.sock:/var/run/docker.sock
    restart: unless-stopped

volumes:
  agent-workspace:
```

### Troubleshooting Agent Issues

```groovy
// Pipeline to diagnose agent
pipeline {
    agent { label 'linux' }
    
    stages {
        stage('Agent Info') {
            steps {
                sh '''
                    echo "=== System Info ==="
                    uname -a
                    cat /etc/os-release
                    
                    echo "=== Resources ==="
                    free -h
                    df -h
                    nproc
                    
                    echo "=== Java ==="
                    java -version
                    which java
                    
                    echo "=== Network ==="
                    hostname
                    ip addr
                    
                    echo "=== Environment ==="
                    env | sort
                '''
            }
        }
    }
}
```

## Summary

- **Master** schedules jobs and manages agents; **agents** execute builds
- **Permanent agents** are always-on servers; **cloud agents** scale dynamically
- **Docker agents** provide isolated, reproducible build environments
- **Labels** categorize agents and enable job targeting (`linux && java17`)
- **Executors** define parallel build capacity on each agent
- Set master executors to 0 and run all builds on agents (best practice)

## Additional Resources

- [Jenkins Distributed Builds](https://www.jenkins.io/doc/book/managing/nodes/) - Official agent documentation
- [Docker Pipeline Plugin](https://plugins.jenkins.io/docker-workflow/) - Docker agent configuration
- [Kubernetes Plugin](https://plugins.jenkins.io/kubernetes/) - Kubernetes pod agents

