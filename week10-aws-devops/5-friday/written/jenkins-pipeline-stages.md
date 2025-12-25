# Jenkins Pipeline Stages

## Learning Objectives

- Define and structure pipeline stages effectively
- Implement parallel execution for faster builds
- Apply conditional stages using `when` directive
- Handle stage failures and implement error recovery
- Use input steps for manual approvals
- Create reusable stage templates

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Stages organize your pipeline into logical phases—build, test, deploy. Well-structured stages provide visibility into pipeline progress, enable parallel execution for speed, and allow conditional flows for different branches or environments.

As a quality engineer, stages often represent quality gates: unit tests, integration tests, security scans, performance tests. Understanding stage configuration helps you insert appropriate testing at each phase.

## The Concept

### Stage Structure

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Pipeline Stage Structure                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   pipeline {                                                        │
│     stages {                         ◄── Container for all stages  │
│                                                                      │
│       stage('Build') {               ◄── Named stage               │
│         steps {                      ◄── What to execute           │
│           sh 'mvn compile'                                          │
│         }                                                           │
│       }                                                             │
│                                                                      │
│       stage('Test') {                                               │
│         when { ... }                 ◄── Conditional execution     │
│         steps {                                                     │
│           sh 'mvn test'                                             │
│         }                                                           │
│         post { ... }                 ◄── Stage-level post actions  │
│       }                                                             │
│                                                                      │
│       stage('Deploy') {                                             │
│         input { ... }                ◄── Manual approval           │
│         steps {                                                     │
│           sh './deploy.sh'                                          │
│         }                                                           │
│       }                                                             │
│                                                                      │
│     }                                                               │
│   }                                                                 │
│                                                                      │
│   BLUE OCEAN VISUALIZATION                                          │
│   ────────────────────────                                          │
│   ┌────────┐    ┌────────┐    ┌────────┐                          │
│   │ Build  │───▶│  Test  │───▶│ Deploy │                          │
│   │   ✓    │    │   ✓    │    │   ◐    │                          │
│   │  45s   │    │  2m    │    │ waiting│                          │
│   └────────┘    └────────┘    └────────┘                          │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Parallel Stages

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Parallel Stage Execution                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Run multiple stages simultaneously to reduce build time           │
│                                                                      │
│   SEQUENTIAL (Slow)                  PARALLEL (Fast)                │
│   ─────────────────                  ──────────────                 │
│   ┌─────┐                           ┌─────┐                        │
│   │Build│ 2min                      │Build│ 2min                   │
│   └──┬──┘                           └──┬──┘                        │
│      ▼                                 │                            │
│   ┌──────┐                     ┌──────┴──────┐                     │
│   │ Unit │ 3min                │             │                     │
│   │ Test │                     ▼             ▼                     │
│   └──┬───┘               ┌──────┐      ┌──────┐                   │
│      ▼                   │ Unit │      │ E2E  │  3min             │
│   ┌──────┐               │ Test │      │ Test │  (parallel)       │
│   │ E2E  │ 5min          └──┬───┘      └──┬───┘                   │
│   │ Test │                  │             │                        │
│   └──┬───┘                  └──────┬──────┘                        │
│      ▼                             ▼                                │
│   ┌──────┐                    ┌──────┐                             │
│   │Deploy│ 1min               │Deploy│ 1min                        │
│   └──────┘                    └──────┘                             │
│                                                                      │
│   Total: 11 min               Total: 6 min                         │
│                                                                      │
│   BEST FOR PARALLELIZATION                                          │
│   ────────────────────────                                          │
│   • Independent test suites (unit, integration, E2E)               │
│   • Multi-platform builds (Linux, Windows, Mac)                    │
│   • Multi-environment deployments                                   │
│   • Static analysis + tests                                        │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Conditional Stages (when)

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Conditional Stage Execution                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   WHEN CONDITIONS                                                    │
│   ───────────────                                                    │
│                                                                      │
│   branch          when { branch 'main' }                           │
│   branch pattern  when { branch pattern: 'release/*' }             │
│   environment     when { environment name: 'ENV', value: 'prod' }  │
│   expression      when { expression { return params.RUN } }        │
│   not             when { not { branch 'develop' } }                │
│   allOf           when { allOf { branch 'main'; env... } }         │
│   anyOf           when { anyOf { branch 'main'; branch 'dev' } }   │
│   triggeredBy     when { triggeredBy 'TimerTrigger' }              │
│   changelog       when { changelog '.*fix.*' }                     │
│   changeset       when { changeset '**/src/**' }                   │
│                                                                      │
│   EXAMPLE FLOW                                                       │
│   ────────────                                                       │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                        All Branches                          │   │
│   │   ┌────────┐    ┌────────┐    ┌────────────┐                │   │
│   │   │ Build  │───▶│  Test  │───▶│ Code Scan  │                │   │
│   │   └────────┘    └────────┘    └─────┬──────┘                │   │
│   │                                     │                        │   │
│   │               ┌─────────────────────┴────────────────────┐  │   │
│   │               │                                          │  │   │
│   │               ▼                                          ▼  │   │
│   │   ┌──────────────────┐                    ┌────────────────┐│   │
│   │   │ Deploy Staging   │                    │ Deploy Prod    ││   │
│   │   │ when: develop    │                    │ when: main     ││   │
│   │   │        or PR     │                    │       + input  ││   │
│   │   └──────────────────┘                    └────────────────┘│   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Input and Approval

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Manual Input and Approval                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   INPUT STEP                                                         │
│   ──────────                                                         │
│   Pauses pipeline and waits for human interaction                   │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Pipeline: my-app #45                                        │   │
│   │  ─────────────────────                                       │   │
│   │                                                              │   │
│   │  Stage: Deploy to Production                                 │   │
│   │  ──────────────────────────                                  │   │
│   │                                                              │   │
│   │  ┌────────────────────────────────────────────────────────┐ │   │
│   │  │  WAITING FOR INPUT                                      │ │   │
│   │  │                                                          │ │   │
│   │  │  Deploy to production?                                  │ │   │
│   │  │                                                          │ │   │
│   │  │  Version: 1.2.3                                         │ │   │
│   │  │  Environment: production                                │ │   │
│   │  │                                                          │ │   │
│   │  │  [Proceed]  [Abort]                                     │ │   │
│   │  └────────────────────────────────────────────────────────┘ │   │
│   │                                                              │   │
│   │  Waiting for: admin, release-managers                       │   │
│   │  Timeout: 24 hours                                          │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   USE CASES                                                          │
│   ─────────                                                          │
│   • Production deployments                                          │
│   • Release approvals                                               │
│   • Manual verification gates                                       │
│   • Parameter collection mid-pipeline                              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Basic Sequential Stages

```groovy
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar'
            }
        }
        
        stage('Deploy') {
            steps {
                sh './deploy.sh staging'
            }
        }
    }
}
```

### Parallel Stages

```groovy
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        sh 'mvn test -Dgroups=unit'
                    }
                    post {
                        always {
                            junit 'target/surefire-reports/unit/*.xml'
                        }
                    }
                }
                
                stage('Integration Tests') {
                    steps {
                        sh 'mvn test -Dgroups=integration'
                    }
                    post {
                        always {
                            junit 'target/surefire-reports/integration/*.xml'
                        }
                    }
                }
                
                stage('Static Analysis') {
                    steps {
                        sh 'mvn checkstyle:check pmd:check'
                    }
                }
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
    }
}
```

### Parallel with Different Agents

```groovy
pipeline {
    agent none
    
    stages {
        stage('Build') {
            parallel {
                stage('Linux Build') {
                    agent { label 'linux' }
                    steps {
                        sh 'make build-linux'
                        stash name: 'linux-build', includes: 'dist/linux/*'
                    }
                }
                
                stage('Windows Build') {
                    agent { label 'windows' }
                    steps {
                        bat 'make build-windows'
                        stash name: 'windows-build', includes: 'dist/windows/*'
                    }
                }
                
                stage('Mac Build') {
                    agent { label 'mac' }
                    steps {
                        sh 'make build-mac'
                        stash name: 'mac-build', includes: 'dist/mac/*'
                    }
                }
            }
        }
        
        stage('Archive All') {
            agent any
            steps {
                unstash 'linux-build'
                unstash 'windows-build'
                unstash 'mac-build'
                archiveArtifacts artifacts: 'dist/**/*'
            }
        }
    }
}
```

### Conditional Stages with When

```groovy
pipeline {
    agent any
    
    parameters {
        booleanParam(name: 'RUN_TESTS', defaultValue: true)
        booleanParam(name: 'DEPLOY', defaultValue: false)
    }
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn compile'
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
        
        stage('Deploy to Staging') {
            when {
                anyOf {
                    branch 'develop'
                    changeRequest target: 'develop'
                }
            }
            steps {
                sh './deploy.sh staging'
            }
        }
        
        stage('Deploy to Production') {
            when {
                allOf {
                    branch 'main'
                    expression { params.DEPLOY == true }
                }
            }
            steps {
                sh './deploy.sh production'
            }
        }
        
        stage('Cleanup PR Environment') {
            when {
                changeRequest()
            }
            steps {
                sh './cleanup-pr-env.sh ${CHANGE_ID}'
            }
        }
    }
}
```

### Input for Manual Approval

```groovy
pipeline {
    agent any
    
    stages {
        stage('Build and Test') {
            steps {
                sh 'mvn clean verify'
            }
        }
        
        stage('Deploy to Staging') {
            steps {
                sh './deploy.sh staging'
            }
        }
        
        stage('Approval') {
            when {
                branch 'main'
            }
            steps {
                script {
                    def approver = input(
                        message: 'Deploy to production?',
                        ok: 'Deploy',
                        submitter: 'admin,release-team',
                        submitterParameter: 'APPROVED_BY',
                        parameters: [
                            string(
                                name: 'RELEASE_NOTES',
                                defaultValue: '',
                                description: 'Release notes'
                            ),
                            booleanParam(
                                name: 'NOTIFY_USERS',
                                defaultValue: true,
                                description: 'Send notification to users'
                            )
                        ]
                    )
                    echo "Approved by: ${approver.APPROVED_BY}"
                    env.RELEASE_NOTES = approver.RELEASE_NOTES
                }
            }
        }
        
        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                sh './deploy.sh production'
            }
        }
    }
}
```

### Input with Timeout

```groovy
pipeline {
    agent any
    
    stages {
        stage('Deploy') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    input message: 'Deploy to production?', ok: 'Deploy'
                }
                sh './deploy.sh production'
            }
        }
    }
}
```

### Stage Post Actions

```groovy
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn compile'
            }
            post {
                success {
                    echo 'Build succeeded!'
                }
                failure {
                    echo 'Build failed!'
                    mail to: 'team@example.com',
                         subject: "Build Failed: ${currentBuild.fullDisplayName}",
                         body: "Check console output at ${BUILD_URL}"
                }
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
                unstable {
                    echo 'Tests failed!'
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            slackSend color: 'good', message: "Build succeeded: ${BUILD_URL}"
        }
        failure {
            slackSend color: 'danger', message: "Build failed: ${BUILD_URL}"
        }
    }
}
```

### Error Handling in Stages

```groovy
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn compile'
            }
        }
        
        stage('Test') {
            steps {
                script {
                    try {
                        sh 'mvn test'
                    } catch (Exception e) {
                        echo "Tests failed: ${e.message}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }
        
        stage('Deploy') {
            when {
                expression { currentBuild.result != 'FAILURE' }
            }
            steps {
                retry(3) {
                    sh './deploy.sh'
                }
            }
        }
        
        stage('Smoke Test') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    sh './smoke-test.sh'
                }
            }
        }
    }
    
    options {
        // Mark unstable if tests fail but continue
        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE')
    }
}
```

### Matrix Stages (Jenkins 2.x)

```groovy
pipeline {
    agent none
    
    stages {
        stage('Build and Test') {
            matrix {
                agent {
                    label "${PLATFORM}"
                }
                axes {
                    axis {
                        name 'PLATFORM'
                        values 'linux', 'windows', 'mac'
                    }
                    axis {
                        name 'JAVA_VERSION'
                        values '11', '17', '21'
                    }
                }
                excludes {
                    exclude {
                        axis {
                            name 'PLATFORM'
                            values 'mac'
                        }
                        axis {
                            name 'JAVA_VERSION'
                            values '11'
                        }
                    }
                }
                stages {
                    stage('Build') {
                        steps {
                            echo "Building on ${PLATFORM} with Java ${JAVA_VERSION}"
                            sh "java -version"
                            sh "mvn compile"
                        }
                    }
                    stage('Test') {
                        steps {
                            sh "mvn test"
                        }
                    }
                }
            }
        }
    }
}
```

## Summary

- **Stages** organize pipelines into logical phases visible in Jenkins UI
- **parallel** runs multiple stages simultaneously for faster builds
- **when** directive enables conditional stage execution (branch, expression, etc.)
- **input** pauses for manual approval with optional parameters and timeout
- **post** blocks at stage level handle stage-specific cleanup and notifications
- **matrix** builds test across multiple axes (platform, version combinations)

## Additional Resources

- [Pipeline Syntax: Stages](https://www.jenkins.io/doc/book/pipeline/syntax/#stages) - Official stage documentation
- [Parallel Execution](https://www.jenkins.io/doc/book/pipeline/syntax/#parallel) - Parallel stages guide
- [Conditional Execution](https://www.jenkins.io/doc/book/pipeline/syntax/#when) - When directive reference

