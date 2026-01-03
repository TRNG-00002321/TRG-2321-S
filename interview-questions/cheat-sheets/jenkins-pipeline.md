# Jenkins Cheat Sheet

## Key Concepts

| Term | Description |
|------|-------------|
| Job/Project | A runnable task |
| Build | Single execution of a job |
| Pipeline | Job defined as code |
| Agent/Node | Machine that runs jobs |
| Executor | Slot for running builds |
| Workspace | Directory for job files |
| Artifact | Files saved from build |

---

## Pipeline Syntax (Jenkinsfile)

### Declarative Pipeline (Recommended)

```groovy
pipeline {
    agent any  // Run on any available agent
    
    environment {
        APP_NAME = 'my-app'
        VERSION = '1.0.0'
    }
    
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
        
        stage('Deploy') {
            steps {
                echo 'Deploying...'
                sh './deploy.sh'
            }
        }
    }
    
    post {
        always {
            echo 'Cleanup...'
            cleanWs()
        }
        success {
            echo 'Build succeeded!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
```

### Agent Options

```groovy
// Any available agent
agent any

// No agent (for pipeline-level only)
agent none

// Specific label
agent { label 'linux' }

// Docker container
agent {
    docker {
        image 'maven:3.8-openjdk-11'
        args '-v /root/.m2:/root/.m2'
    }
}

// Dockerfile in repo
agent {
    dockerfile {
        filename 'Dockerfile.build'
        dir 'docker'
    }
}
```

### Environment Variables

```groovy
pipeline {
    environment {
        // Define variables
        MY_VAR = 'value'
        CREDS = credentials('my-credentials-id')
    }
    
    stages {
        stage('Example') {
            environment {
                // Stage-specific variable
                STAGE_VAR = 'stage-value'
            }
            steps {
                // Use variables
                echo "My var: ${MY_VAR}"
                echo "Stage var: ${env.STAGE_VAR}"
                
                // Built-in variables
                echo "Build number: ${BUILD_NUMBER}"
                echo "Job name: ${JOB_NAME}"
                echo "Workspace: ${WORKSPACE}"
            }
        }
    }
}
```

### Built-in Environment Variables

| Variable | Description |
|----------|-------------|
| `BUILD_NUMBER` | Current build number |
| `BUILD_ID` | Build identifier |
| `JOB_NAME` | Name of the job |
| `WORKSPACE` | Workspace directory |
| `JENKINS_URL` | Jenkins server URL |
| `GIT_BRANCH` | Git branch name |
| `GIT_COMMIT` | Git commit hash |

---

## Common Steps

### Shell Commands
```groovy
// Linux/Mac
sh 'echo "Hello"'
sh '''
    echo "Multi-line"
    echo "Script"
'''

// Windows
bat 'echo Hello'
powershell 'Get-Process'
```

### File Operations
```groovy
// Write file
writeFile file: 'output.txt', text: 'Hello World'

// Read file
def content = readFile 'input.txt'

// Check file exists
fileExists 'path/to/file'

// Delete files
deleteDir()  // Delete workspace
sh 'rm -rf build/'
```

### Archiving & Artifacts
```groovy
// Archive artifacts
archiveArtifacts artifacts: 'target/*.jar', fingerprint: true

// Publish test results
junit 'target/surefire-reports/*.xml'

// Publish HTML reports
publishHTML(target: [
    reportName: 'Test Report',
    reportDir: 'reports',
    reportFiles: 'index.html'
])

// Allure report
allure includeProperties: false,
       results: [[path: 'allure-results']]
```

### Git Operations
```groovy
// Checkout SCM (from pipeline config)
checkout scm

// Checkout specific repo
git branch: 'main',
    url: 'https://github.com/user/repo.git',
    credentialsId: 'github-creds'
```

---

## Conditions & Control

### When Conditions
```groovy
stage('Deploy to Prod') {
    when {
        branch 'main'
    }
    steps {
        sh './deploy-prod.sh'
    }
}

stage('Only on PR') {
    when {
        changeRequest()
    }
    steps { ... }
}

stage('With Expression') {
    when {
        expression { return env.DEPLOY == 'true' }
    }
    steps { ... }
}

stage('All of') {
    when {
        allOf {
            branch 'main'
            environment name: 'DEPLOY', value: 'true'
        }
    }
    steps { ... }
}
```

### Input (Manual Approval)
```groovy
stage('Deploy to Production') {
    input {
        message "Deploy to production?"
        ok "Deploy"
        submitter "admin,deployer"
        parameters {
            choice(name: 'ENV', choices: ['staging', 'production'])
        }
    }
    steps {
        sh "./deploy.sh ${ENV}"
    }
}
```

### Parallel Stages
```groovy
stage('Tests') {
    parallel {
        stage('Unit Tests') {
            steps {
                sh 'mvn test -Punit'
            }
        }
        stage('Integration Tests') {
            steps {
                sh 'mvn test -Pintegration'
            }
        }
        stage('E2E Tests') {
            steps {
                sh 'mvn test -Pe2e'
            }
        }
    }
}
```

---

## Triggers

```groovy
pipeline {
    triggers {
        // Poll SCM every 5 minutes
        pollSCM('H/5 * * * *')
        
        // Cron schedule
        cron('0 8 * * 1-5')  // Weekdays at 8am
        
        // GitHub webhook
        githubPush()
        
        // Upstream job
        upstream(upstreamProjects: 'job-name', threshold: hudson.model.Result.SUCCESS)
    }
    ...
}
```

### Cron Syntax
```
MINUTE HOUR DOM MONTH DOW
  │     │    │    │    └── Day of week (0-7, 0=Sunday)
  │     │    │    └─────── Month (1-12)
  │     │    └──────────── Day of month (1-31)
  │     └───────────────── Hour (0-23)
  └─────────────────────── Minute (0-59)

H = Hash for load balancing

Examples:
H/15 * * * *     # Every 15 minutes
H 8 * * 1-5      # Weekdays at ~8am
0 0 1 * *        # First of month at midnight
```

---

## Credentials

```groovy
// Username/Password
withCredentials([usernamePassword(
    credentialsId: 'my-creds',
    usernameVariable: 'USER',
    passwordVariable: 'PASS'
)]) {
    sh 'curl -u $USER:$PASS https://api.example.com'
}

// Secret text
withCredentials([string(credentialsId: 'api-key', variable: 'API_KEY')]) {
    sh 'curl -H "Authorization: $API_KEY" https://api.example.com'
}

// SSH key
withCredentials([sshUserPrivateKey(
    credentialsId: 'ssh-key',
    keyFileVariable: 'SSH_KEY'
)]) {
    sh 'ssh -i $SSH_KEY user@server'
}

// In environment
environment {
    CREDS = credentials('my-credentials')
    // Creates: CREDS, CREDS_USR, CREDS_PSW
}
```

---

## Post Actions

```groovy
post {
    always {
        // Always runs
        junit '**/test-results/*.xml'
        cleanWs()
    }
    success {
        // Only on success
        slackSend channel: '#builds', message: 'Build succeeded!'
    }
    failure {
        // Only on failure
        emailext to: 'team@example.com',
                 subject: 'Build Failed',
                 body: 'Check ${BUILD_URL}'
    }
    unstable {
        // Tests failed but build succeeded
    }
    changed {
        // Status changed from previous build
    }
    aborted {
        // Build was aborted
    }
}
```

---

## Notifications

### Slack
```groovy
slackSend channel: '#builds',
          color: 'good',
          message: "Build ${BUILD_NUMBER} succeeded!"
```

### Email
```groovy
emailext to: 'user@example.com',
         subject: "Build ${BUILD_NUMBER}",
         body: """
         Build: ${BUILD_URL}
         Status: ${currentBuild.result}
         """
```

---

## Useful Plugins

| Plugin | Purpose |
|--------|---------|
| Pipeline | Pipeline-as-code support |
| Git | Git SCM integration |
| GitHub | GitHub integration |
| Docker | Docker support |
| JUnit | Test result publishing |
| Allure | Rich test reports |
| Slack | Slack notifications |
| Email Extension | Advanced email |
| Credentials | Credentials management |
| Blue Ocean | Modern UI |
