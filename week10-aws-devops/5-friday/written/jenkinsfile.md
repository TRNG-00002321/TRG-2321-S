# Jenkinsfile and Pipeline Syntax

## Learning Objectives

- Create a Jenkinsfile for Pipeline as Code
- Distinguish between Declarative and Scripted pipeline syntax
- Structure pipelines with stages and steps
- Use the `agent` directive for execution environment
- Apply `environment` and `parameters` in pipelines
- Handle errors and control flow in pipelines

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Pipeline as Code revolutionized CI/CD by treating build configurations as source code. The Jenkinsfile lives with your application, is version controlled, reviewed in PRs, and evolves with the project. No more clicking through UIs to recreate lost configurations.

As a quality engineer, Jenkinsfiles define when and how your tests run. Understanding pipeline syntax lets you add test stages, configure test environments, and integrate quality gates into the deployment workflow.

## The Concept

### Pipeline as Code

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Pipeline as Code                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   TRADITIONAL (Freestyle)          PIPELINE AS CODE                 │
│   ───────────────────────          ────────────────                 │
│                                                                      │
│   ┌─────────────────────┐         ┌─────────────────────┐          │
│   │  Jenkins Web UI     │         │  Jenkinsfile        │          │
│   │  ───────────────────│         │  ───────────────────│          │
│   │  Configure manually │         │  pipeline {         │          │
│   │  Click, click, click│         │    stages {         │          │
│   │  No version control │         │      stage('Build') │          │
│   │  Hard to replicate  │         │    }                │          │
│   └─────────────────────┘         │  }                  │          │
│                                   └─────────────────────┘          │
│                                             │                       │
│                                             ▼                       │
│                                   ┌─────────────────────┐          │
│                                   │  Git Repository     │          │
│                                   │  ───────────────────│          │
│                                   │  ✓ Version control  │          │
│                                   │  ✓ Code review      │          │
│                                   │  ✓ History/blame    │          │
│                                   │  ✓ Reproducible     │          │
│                                   └─────────────────────┘          │
│                                                                      │
│   Benefits of Jenkinsfile:                                          │
│   • Stored in SCM with application code                            │
│   • Reviewed in pull requests                                       │
│   • Branch-specific pipelines                                       │
│   • Easy to replicate and audit                                    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Declarative vs Scripted Syntax

```
┌─────────────────────────────────────────────────────────────────────┐
│              Declarative vs Scripted Pipeline                        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   DECLARATIVE (Recommended)                                          │
│   ─────────────────────────                                          │
│   pipeline {                                                        │
│     agent any                                                       │
│     stages {                                                        │
│       stage('Build') {                                              │
│         steps {                                                     │
│           sh 'mvn compile'                                          │
│         }                                                           │
│       }                                                             │
│     }                                                               │
│   }                                                                 │
│                                                                      │
│   ✓ Simpler, structured syntax                                     │
│   ✓ Better error reporting                                         │
│   ✓ Blue Ocean compatible                                          │
│   ✓ Recommended for most cases                                     │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   SCRIPTED (Advanced)                                               │
│   ───────────────────                                               │
│   node {                                                            │
│     stage('Build') {                                                │
│       sh 'mvn compile'                                              │
│     }                                                               │
│   }                                                                 │
│                                                                      │
│   ✓ Full Groovy power                                              │
│   ✓ More flexible                                                  │
│   ✗ Harder to read                                                 │
│   ✗ Less validation                                                │
│                                                                      │
│   Use scripted when: Complex logic, loops, conditionals            │
│   Use declarative when: Standard CI/CD workflows                   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Pipeline Structure

```
┌─────────────────────────────────────────────────────────────────────┐
│                 Declarative Pipeline Structure                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   pipeline {                 ◄── Pipeline block (required)          │
│                                                                      │
│     agent any                ◄── Where to run (required)            │
│                                                                      │
│     environment {            ◄── Environment variables              │
│       VERSION = '1.0'                                               │
│     }                                                               │
│                                                                      │
│     options {                ◄── Pipeline options                   │
│       timeout(time: 1, unit: 'HOURS')                               │
│     }                                                               │
│                                                                      │
│     parameters {             ◄── Build parameters                   │
│       string(name: 'BRANCH')                                        │
│     }                                                               │
│                                                                      │
│     stages {                 ◄── Stages block (required)            │
│                                                                      │
│       stage('Build') {       ◄── Stage block                        │
│         steps {              ◄── Steps block (required in stage)    │
│           sh 'mvn compile'   ◄── Step (command)                     │
│         }                                                           │
│       }                                                             │
│                                                                      │
│       stage('Test') {                                               │
│         steps {                                                     │
│           sh 'mvn test'                                             │
│         }                                                           │
│       }                                                             │
│                                                                      │
│     }                                                               │
│                                                                      │
│     post {                   ◄── Post-build actions                 │
│       always {                                                      │
│         cleanWs()                                                   │
│       }                                                             │
│       success {                                                     │
│         echo 'Build succeeded!'                                     │
│       }                                                             │
│       failure {                                                     │
│         echo 'Build failed!'                                        │
│       }                                                             │
│     }                                                               │
│                                                                      │
│   }                                                                 │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Agent Directive

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Agent Directive Options                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   agent any                                                          │
│   ─────────                                                          │
│   Run on any available agent                                        │
│                                                                      │
│   agent none                                                         │
│   ──────────                                                         │
│   No global agent; each stage specifies its own                     │
│                                                                      │
│   agent { label 'linux' }                                           │
│   ───────────────────────                                           │
│   Run on agent with specific label                                  │
│                                                                      │
│   agent {                                                            │
│     docker {                                                        │
│       image 'maven:3.8-jdk-11'                                      │
│       args '-v /root/.m2:/root/.m2'                                 │
│     }                                                               │
│   }                                                                 │
│   Run inside Docker container                                       │
│                                                                      │
│   agent {                                                            │
│     dockerfile {                                                    │
│       filename 'Dockerfile.build'                                   │
│       dir 'ci'                                                      │
│     }                                                               │
│   }                                                                 │
│   Build and use Dockerfile                                          │
│                                                                      │
│   agent {                                                            │
│     kubernetes {                                                    │
│       yaml '''                                                      │
│         apiVersion: v1                                              │
│         kind: Pod                                                   │
│         spec:                                                       │
│           containers:                                               │
│           - name: maven                                             │
│             image: maven:3.8                                        │
│       '''                                                           │
│     }                                                               │
│   }                                                                 │
│   Run in Kubernetes pod                                             │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Basic Declarative Pipeline

```groovy
// Jenkinsfile
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                echo 'Building...'
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            steps {
                echo 'Testing...'
                sh 'mvn test'
            }
        }
        
        stage('Package') {
            steps {
                echo 'Packaging...'
                sh 'mvn package -DskipTests'
            }
        }
    }
}
```

### Pipeline with Environment and Parameters

```groovy
pipeline {
    agent any
    
    environment {
        // Static environment variables
        MAVEN_HOME = '/usr/local/maven'
        APP_NAME = 'my-application'
        // From credentials
        DOCKER_CREDS = credentials('docker-hub')
    }
    
    parameters {
        string(name: 'BRANCH', defaultValue: 'main', description: 'Branch to build')
        choice(name: 'ENVIRONMENT', choices: ['dev', 'staging', 'prod'], description: 'Target environment')
        booleanParam(name: 'RUN_TESTS', defaultValue: true, description: 'Run test suite')
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: "${params.BRANCH}", url: 'https://github.com/org/repo.git'
            }
        }
        
        stage('Build') {
            steps {
                echo "Building ${env.APP_NAME} for ${params.ENVIRONMENT}"
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            when {
                expression { params.RUN_TESTS == true }
            }
            steps {
                sh 'mvn test'
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
            args '-v $HOME/.m2:/root/.m2'
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

### Pipeline with Different Agents per Stage

```groovy
pipeline {
    agent none  // No global agent
    
    stages {
        stage('Build') {
            agent {
                docker { image 'maven:3.8' }
            }
            steps {
                sh 'mvn clean package'
                stash name: 'app', includes: 'target/*.jar'
            }
        }
        
        stage('Test') {
            agent {
                docker { image 'openjdk:17' }
            }
            steps {
                unstash 'app'
                sh 'java -jar target/*.jar --test'
            }
        }
        
        stage('Deploy') {
            agent { label 'deploy-server' }
            steps {
                unstash 'app'
                sh './deploy.sh'
            }
        }
    }
}
```

### Complete CI/CD Pipeline

```groovy
pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'docker.io/myorg'
        IMAGE_NAME = 'my-app'
        VERSION = "${BUILD_NUMBER}"
    }
    
    options {
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                }
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Code Quality') {
            steps {
                sh 'mvn sonar:sonar'
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh """
                    docker build -t ${DOCKER_REGISTRY}/${IMAGE_NAME}:${VERSION} .
                    docker tag ${DOCKER_REGISTRY}/${IMAGE_NAME}:${VERSION} ${DOCKER_REGISTRY}/${IMAGE_NAME}:latest
                """
            }
        }
        
        stage('Push to Registry') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                        echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
                        docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:${VERSION}
                        docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:latest
                    """
                }
            }
        }
        
        stage('Deploy to Staging') {
            steps {
                sh './deploy.sh staging ${VERSION}'
            }
        }
        
        stage('Integration Tests') {
            steps {
                sh './run-integration-tests.sh staging'
            }
        }
        
        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            input {
                message 'Deploy to production?'
                ok 'Deploy'
            }
            steps {
                sh './deploy.sh production ${VERSION}'
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            slackSend channel: '#builds', color: 'good',
                message: "Build ${BUILD_NUMBER} succeeded"
        }
        failure {
            slackSend channel: '#builds', color: 'danger',
                message: "Build ${BUILD_NUMBER} failed"
        }
    }
}
```

### Scripted Pipeline Example

```groovy
// Scripted syntax for complex logic
node('linux') {
    def servers = ['server1', 'server2', 'server3']
    
    stage('Checkout') {
        checkout scm
    }
    
    stage('Build') {
        sh 'mvn clean package'
    }
    
    stage('Deploy') {
        for (server in servers) {
            try {
                sh "scp target/*.jar ${server}:/app/"
                sh "ssh ${server} 'systemctl restart myapp'"
                echo "Deployed to ${server}"
            } catch (Exception e) {
                echo "Failed to deploy to ${server}: ${e.message}"
            }
        }
    }
}
```

### Error Handling

```groovy
pipeline {
    agent any
    
    stages {
        stage('Risky Operation') {
            steps {
                script {
                    try {
                        sh 'might-fail-command'
                    } catch (Exception e) {
                        echo "Command failed: ${e.message}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }
        
        stage('With Retry') {
            steps {
                retry(3) {
                    sh 'flaky-command'
                }
            }
        }
        
        stage('With Timeout') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    sh 'long-running-command'
                }
            }
        }
    }
}
```

## Summary

- **Jenkinsfile** stores pipeline configuration as code in the repository
- **Declarative syntax** is structured and recommended for most pipelines
- **Scripted syntax** offers full Groovy flexibility for complex logic
- **Pipeline structure**: `pipeline` → `agent` → `stages` → `stage` → `steps`
- **Agent directive** specifies where to run (any, label, docker, kubernetes)
- **Post section** defines actions for success, failure, always conditions

## Additional Resources

- [Pipeline Syntax](https://www.jenkins.io/doc/book/pipeline/syntax/) - Complete syntax reference
- [Pipeline Steps Reference](https://www.jenkins.io/doc/pipeline/steps/) - All available steps
- [Declarative Directive Generator](https://www.jenkins.io/doc/book/pipeline/getting-started/#directive-generator) - UI tool for generating syntax

