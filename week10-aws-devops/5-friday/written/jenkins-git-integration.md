# Jenkins Git Integration

## Learning Objectives

- Configure Git repositories in Jenkins jobs
- Set up multibranch pipelines for automatic branch discovery
- Implement branch-based build strategies
- Configure webhooks for push and PR events
- Work with Git credentials and authentication
- Apply Git-specific pipeline patterns

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Git is the backbone of modern development. Jenkins-Git integration enables automatic builds on every push, PR validation before merge, and branch-specific pipelines. Effective Git integration is what makes CI/CD truly continuous.

As a quality engineer, Git integration affects when tests run. You'll configure Jenkins to run fast tests on every commit, full regression on PRs, and deployment validations on merges to main.

## The Concept

### Git Integration Options

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Git Integration Options                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   SINGLE BRANCH PIPELINE                                            │
│   ──────────────────────                                            │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Job: my-app                                                 │   │
│   │  Branch: main                                                │   │
│   │  Trigger: Webhook on push to main                           │   │
│   └─────────────────────────────────────────────────────────────┘   │
│   Simple, one job per branch                                        │
│   Use when: Single branch CI, simple projects                      │
│                                                                      │
│   MULTIBRANCH PIPELINE                                              │
│   ────────────────────                                              │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Pipeline: my-app                                            │   │
│   │  ├── main      (builds on push)                             │   │
│   │  ├── develop   (builds on push)                             │   │
│   │  ├── feature/login (auto-discovered)                        │   │
│   │  ├── PR-123    (pull request build)                         │   │
│   │  └── ...                                                     │   │
│   └─────────────────────────────────────────────────────────────┘   │
│   Automatic branch/PR discovery                                     │
│   Use when: Feature branches, PR validation, GitFlow              │
│                                                                      │
│   ORGANIZATION FOLDER                                               │
│   ───────────────────                                               │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Organization: my-github-org                                 │   │
│   │  ├── repo-1                                                  │   │
│   │  │   ├── main                                               │   │
│   │  │   └── develop                                            │   │
│   │  ├── repo-2                                                  │   │
│   │  │   └── main                                               │   │
│   │  └── repo-3                                                  │   │
│   │      └── main                                               │   │
│   └─────────────────────────────────────────────────────────────┘   │
│   Auto-discover all repos in GitHub/GitLab org                     │
│   Use when: Many repos, consistent pipeline across projects        │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Multibranch Pipeline

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Multibranch Pipeline                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   HOW IT WORKS                                                       │
│   ────────────                                                       │
│   1. Jenkins scans repository for branches with Jenkinsfile        │
│   2. Creates a pipeline job for each branch automatically          │
│   3. Removes jobs when branches are deleted                        │
│   4. Handles PRs as special branches                               │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Repository                     Jenkins                      │   │
│   │  ──────────                     ───────                      │   │
│   │  main (Jenkinsfile)    ────▶    main pipeline               │   │
│   │  develop (Jenkinsfile) ────▶    develop pipeline            │   │
│   │  feature/x (Jenkinsfile)────▶   feature/x pipeline          │   │
│   │  hotfix/y (no Jenkinsfile) ──▶  (not discovered)            │   │
│   │  PR #42                ────▶    PR-42 pipeline              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   BRANCH DISCOVERY                                                   │
│   ────────────────                                                   │
│   • Discover all branches                                          │
│   • Only branches with Jenkinsfile                                 │
│   • Filter by name pattern (main, develop, feature/*)              │
│   • Exclude branches (test/*, experimental/*)                      │
│                                                                      │
│   SCAN TRIGGERS                                                      │
│   ─────────────                                                      │
│   • Periodic scan (every X minutes)                                │
│   • Webhook (GitHub, GitLab, Bitbucket)                           │
│   • Manual scan                                                     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Branch-Based Workflows

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Branch-Based Build Strategies                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   FEATURE BRANCH WORKFLOW                                           │
│   ───────────────────────                                           │
│                                                                      │
│   feature/* branches:                                               │
│   • Build and unit test                                            │
│   • Code analysis                                                   │
│   • No deployment                                                   │
│                                                                      │
│   develop branch:                                                    │
│   • Build and all tests                                            │
│   • Deploy to dev environment                                      │
│   • Integration tests                                              │
│                                                                      │
│   main branch:                                                       │
│   • Full build and test                                            │
│   • Deploy to staging                                              │
│   • E2E tests                                                      │
│   • Manual approval for production                                 │
│                                                                      │
│   PULL REQUEST WORKFLOW                                             │
│   ─────────────────────                                             │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  PR #42: feature/login → develop                             │   │
│   │  ──────────────────────────────────                          │   │
│   │                                                              │   │
│   │  1. Build                     ✓ Passed                      │   │
│   │  2. Unit Tests                ✓ 150 passed                  │   │
│   │  3. Integration Tests         ✓ 45 passed                   │   │
│   │  4. Code Coverage             ✓ 85% (threshold: 80%)        │   │
│   │  5. SonarQube                 ✓ No new issues               │   │
│   │                                                              │   │
│   │  ✓ All checks passed - Ready to merge                       │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Basic Git Checkout in Pipeline

```groovy
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                // Simple checkout
                git branch: 'main',
                    url: 'https://github.com/org/repo.git',
                    credentialsId: 'github-token'
                
                // Or use checkout scm for pipeline from SCM
                checkout scm
            }
        }
        
        stage('Git Info') {
            steps {
                script {
                    // Get commit information
                    env.GIT_COMMIT_SHORT = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                    
                    env.GIT_AUTHOR = sh(
                        script: 'git log -1 --format="%an"',
                        returnStdout: true
                    ).trim()
                    
                    env.GIT_MESSAGE = sh(
                        script: 'git log -1 --format="%s"',
                        returnStdout: true
                    ).trim()
                }
                
                echo "Commit: ${env.GIT_COMMIT_SHORT}"
                echo "Author: ${env.GIT_AUTHOR}"
                echo "Message: ${env.GIT_MESSAGE}"
            }
        }
    }
}
```

### Multibranch Pipeline Jenkinsfile

```groovy
pipeline {
    agent any
    
    environment {
        // Available in multibranch pipelines
        BRANCH = "${env.BRANCH_NAME}"
        // For PRs
        PR_NUMBER = "${env.CHANGE_ID ?: ''}"
        PR_TARGET = "${env.CHANGE_TARGET ?: ''}"
    }
    
    stages {
        stage('Build') {
            steps {
                echo "Building branch: ${BRANCH}"
                sh 'mvn clean compile'
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }
        
        stage('Integration Tests') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                    changeRequest()
                }
            }
            steps {
                sh 'mvn verify -Pintegration-tests'
            }
        }
        
        stage('Deploy to Dev') {
            when {
                branch 'develop'
            }
            steps {
                sh './deploy.sh dev'
            }
        }
        
        stage('Deploy to Staging') {
            when {
                branch 'main'
            }
            steps {
                sh './deploy.sh staging'
            }
        }
        
        stage('Deploy to Production') {
            when {
                allOf {
                    branch 'main'
                    not { changeRequest() }
                }
            }
            input {
                message 'Deploy to production?'
                ok 'Deploy'
            }
            steps {
                sh './deploy.sh production'
            }
        }
    }
    
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
        success {
            script {
                if (env.CHANGE_ID) {
                    // Comment on PR
                    echo "PR #${env.CHANGE_ID} build succeeded"
                }
            }
        }
    }
}
```

### PR Validation Pipeline

```groovy
pipeline {
    agent any
    
    stages {
        stage('Validate PR') {
            when {
                changeRequest()
            }
            steps {
                script {
                    echo "PR #${env.CHANGE_ID}: ${env.CHANGE_TITLE}"
                    echo "Source: ${env.CHANGE_BRANCH}"
                    echo "Target: ${env.CHANGE_TARGET}"
                    echo "Author: ${env.CHANGE_AUTHOR}"
                }
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Tests') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        sh 'mvn test'
                    }
                }
                stage('Lint') {
                    steps {
                        sh 'mvn checkstyle:check'
                    }
                }
            }
        }
        
        stage('Coverage Check') {
            when {
                changeRequest()
            }
            steps {
                sh 'mvn jacoco:report'
                script {
                    def coverage = readFile('target/site/jacoco/index.html')
                    // Parse coverage and fail if below threshold
                }
            }
        }
        
        stage('Update PR Status') {
            when {
                changeRequest()
            }
            steps {
                // GitHub status update
                script {
                    if (currentBuild.result == 'SUCCESS') {
                        githubNotify status: 'SUCCESS',
                                    description: 'Build passed',
                                    context: 'jenkins/build'
                    }
                }
            }
        }
    }
}
```

### Branch Discovery Configuration (JCasC)

```yaml
# jenkins.yaml
jobs:
  - script: >
      multibranchPipelineJob('my-app') {
        displayName('My Application')
        description('Multibranch pipeline for my-app')
        
        branchSources {
          github {
            id('my-app-github')
            repoOwner('my-org')
            repository('my-app')
            credentialsId('github-token')
            
            buildOriginBranch(true)
            buildOriginBranchWithPR(true)
            buildOriginPRHead(true)
            buildForkPRHead(false)
          }
        }
        
        factory {
          workflowBranchProjectFactory {
            scriptPath('Jenkinsfile')
          }
        }
        
        orphanedItemStrategy {
          discardOldItems {
            numToKeep(10)
            daysToKeep(30)
          }
        }
        
        triggers {
          periodic(1)  // Scan every minute
        }
      }
```

### GitHub Webhook Configuration

```groovy
// Jenkinsfile with webhook triggers
pipeline {
    agent any
    
    triggers {
        // GitHub push trigger
        githubPush()
    }
    
    options {
        // Don't trigger on initial checkout
        skipDefaultCheckout(true)
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    extensions: [
                        [$class: 'CloneOption', depth: 1, shallow: true],
                        [$class: 'CleanCheckout']
                    ],
                    userRemoteConfigs: [[
                        url: 'https://github.com/org/repo.git',
                        credentialsId: 'github-token'
                    ]]
                ])
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
    }
}
```

### Checkout Specific Branch/Tag/Commit

```groovy
pipeline {
    agent any
    
    parameters {
        string(name: 'GIT_REF', defaultValue: 'main', description: 'Branch, tag, or commit')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "${params.GIT_REF}"]],
                    extensions: [
                        [$class: 'CloneOption', noTags: false, shallow: false],
                        [$class: 'LocalBranch', localBranch: "**"]
                    ],
                    userRemoteConfigs: [[
                        url: 'https://github.com/org/repo.git',
                        credentialsId: 'github-token'
                    ]]
                ])
            }
        }
        
        stage('Checkout Tag') {
            steps {
                // Checkout specific tag
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: 'refs/tags/v1.0.0']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/org/repo.git',
                        credentialsId: 'github-token'
                    ]]
                ])
            }
        }
    }
}
```

### Git Operations in Pipeline

```groovy
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Modify and Commit') {
            steps {
                script {
                    // Configure git
                    sh '''
                        git config user.email "jenkins@example.com"
                        git config user.name "Jenkins"
                    '''
                    
                    // Make changes
                    sh 'echo "${BUILD_NUMBER}" > build-number.txt'
                    
                    // Commit
                    sh '''
                        git add build-number.txt
                        git commit -m "Update build number to ${BUILD_NUMBER}"
                    '''
                }
            }
        }
        
        stage('Tag Release') {
            when {
                branch 'main'
            }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'github-token',
                    usernameVariable: 'GIT_USER',
                    passwordVariable: 'GIT_TOKEN'
                )]) {
                    sh '''
                        git tag -a "v${BUILD_NUMBER}" -m "Release ${BUILD_NUMBER}"
                        git push https://${GIT_USER}:${GIT_TOKEN}@github.com/org/repo.git --tags
                    '''
                }
            }
        }
    }
}
```

### Branch-Specific Behavior

```groovy
pipeline {
    agent any
    
    stages {
        stage('Determine Environment') {
            steps {
                script {
                    switch(env.BRANCH_NAME) {
                        case 'main':
                            env.DEPLOY_ENV = 'production'
                            env.RUN_E2E = 'true'
                            break
                        case 'develop':
                            env.DEPLOY_ENV = 'staging'
                            env.RUN_E2E = 'true'
                            break
                        case ~/feature\/.*/:
                            env.DEPLOY_ENV = 'dev'
                            env.RUN_E2E = 'false'
                            break
                        default:
                            env.DEPLOY_ENV = 'none'
                            env.RUN_E2E = 'false'
                    }
                    
                    echo "Branch: ${env.BRANCH_NAME}"
                    echo "Deploy to: ${env.DEPLOY_ENV}"
                }
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('E2E Tests') {
            when {
                expression { env.RUN_E2E == 'true' }
            }
            steps {
                sh 'npm run e2e'
            }
        }
        
        stage('Deploy') {
            when {
                expression { env.DEPLOY_ENV != 'none' }
            }
            steps {
                sh "./deploy.sh ${env.DEPLOY_ENV}"
            }
        }
    }
}
```

## Summary

- **Git integration** enables CI/CD triggered by code changes
- **Multibranch pipelines** automatically discover and build branches with Jenkinsfile
- **Branch-based strategies** apply different build/deploy logic per branch type
- **PR validation** runs builds on pull requests before merge
- **Webhooks** provide instant build triggers (prefer over polling)
- **Available environment variables**: `BRANCH_NAME`, `CHANGE_ID`, `CHANGE_TARGET`, `GIT_COMMIT`

## Additional Resources

- [Jenkins Git Plugin](https://plugins.jenkins.io/git/) - Git plugin documentation
- [Multibranch Pipelines](https://www.jenkins.io/doc/book/pipeline/multibranch/) - Official guide
- [GitHub Branch Source](https://plugins.jenkins.io/github-branch-source/) - GitHub integration

