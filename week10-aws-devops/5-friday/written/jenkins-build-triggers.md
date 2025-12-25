# Jenkins Build Triggers

## Learning Objectives

- Configure SCM polling to detect code changes
- Set up webhooks for instant build triggers
- Create scheduled builds using cron syntax
- Implement manual triggers with parameters
- Use upstream/downstream job triggers
- Apply best practices for trigger configuration

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Build triggers determine when automation runs. The right trigger strategy ensures builds happen promptly when code changes (continuous integration), on schedule for nightly tests, or on-demand for releases. Wrong triggers mean either missed changes or wasted resources.

As a quality engineer, you'll configure triggers for different test scenarios: unit tests on every commit, integration tests on PR merges, performance tests overnight, and release builds on demand.

## The Concept

### Trigger Types Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Build Trigger Types                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   SCM POLLING                                                        │
│   ───────────                                                        │
│   Jenkins checks repository at intervals                            │
│   ┌─────────────────┐       ┌─────────────────┐                    │
│   │     Jenkins     │──────▶│   Git Server    │                    │
│   │  (polls every   │       │  (checks for    │                    │
│   │   5 minutes)    │◀──────│   changes)      │                    │
│   └─────────────────┘       └─────────────────┘                    │
│   + Simple setup                                                    │
│   - Delay between commit and build                                 │
│   - Wasted requests when no changes                                │
│                                                                      │
│   WEBHOOKS                                                           │
│   ────────                                                           │
│   Git server notifies Jenkins immediately                          │
│   ┌─────────────────┐       ┌─────────────────┐                    │
│   │   Git Server    │──────▶│     Jenkins     │                    │
│   │ (pushes event)  │       │ (instant build) │                    │
│   └─────────────────┘       └─────────────────┘                    │
│   + Instant builds                                                  │
│   + No polling overhead                                             │
│   - Requires network access to Jenkins                             │
│   - More setup required                                            │
│                                                                      │
│   SCHEDULED (Cron)                                                   │
│   ────────────────                                                   │
│   Builds at specific times                                          │
│   ┌─────────────────┐                                              │
│   │     Jenkins     │  "Every night at 2 AM"                       │
│   │   (internal     │  "Every Monday at 9 AM"                      │
│   │    scheduler)   │                                               │
│   └─────────────────┘                                              │
│   + Regular intervals                                               │
│   + Independent of code changes                                    │
│   Good for: Nightly builds, reports, cleanup                       │
│                                                                      │
│   MANUAL                                                             │
│   ──────                                                             │
│   Triggered by user action                                          │
│   + Full control                                                    │
│   + Parameters for customization                                    │
│   Good for: Releases, deployments, on-demand tests                 │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### SCM Polling

```
┌─────────────────────────────────────────────────────────────────────┐
│                    SCM Polling Configuration                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   JENKINS CRON SYNTAX                                               │
│   ───────────────────                                               │
│                                                                      │
│   MINUTE  HOUR  DAY   MONTH  DAY_OF_WEEK                           │
│     │      │     │      │        │                                  │
│     │      │     │      │        └── 0-7 (0,7=Sunday)              │
│     │      │     │      └─────────── 1-12                          │
│     │      │     └────────────────── 1-31                          │
│     │      └──────────────────────── 0-23                          │
│     └─────────────────────────────── 0-59                          │
│                                                                      │
│   SPECIAL CHARACTERS                                                │
│   ──────────────────                                                │
│   *     Every value                                                 │
│   H     Hash (spread load)    ◄── Jenkins-specific                 │
│   */N   Every N intervals                                          │
│   N-M   Range                                                       │
│   N,M   List                                                        │
│                                                                      │
│   EXAMPLES                                                           │
│   ────────                                                           │
│   H/5 * * * *      Every 5 minutes (hashed)                        │
│   H * * * *        Every hour (hashed minute)                      │
│   H/15 * * * *     Every 15 minutes                                │
│   0 * * * *        Every hour at :00                               │
│   */10 * * * *     Every 10 minutes (exactly)                      │
│                                                                      │
│   WHY USE 'H' (Hash)?                                               │
│   ───────────────────                                               │
│   H distributes load across time to avoid all jobs                 │
│   polling at the same moment.                                       │
│                                                                      │
│   H/5 * * * *  →  Job A polls at :02, :07, :12...                 │
│   H/5 * * * *  →  Job B polls at :04, :09, :14...                 │
│                                                                      │
│   (Instead of all at :00, :05, :10...)                            │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Webhook Configuration

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Webhook Setup                                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   GITHUB WEBHOOK                                                     │
│   ──────────────                                                     │
│                                                                      │
│   1. In Jenkins:                                                    │
│      - Enable "GitHub hook trigger for GITScm polling"             │
│      - Or use Generic Webhook Trigger plugin                       │
│                                                                      │
│   2. In GitHub Repository Settings → Webhooks → Add:               │
│      ┌─────────────────────────────────────────────────────────┐   │
│      │ Payload URL: https://jenkins.example.com/github-webhook/ │   │
│      │ Content type: application/json                           │   │
│      │ Secret: <webhook-secret>                                  │   │
│      │ Events: [✓] Just the push event                          │   │
│      │         [✓] Pull requests                                 │   │
│      └─────────────────────────────────────────────────────────┘   │
│                                                                      │
│   GITLAB WEBHOOK                                                     │
│   ──────────────                                                     │
│   Payload URL: https://jenkins.example.com/project/job-name        │
│   Trigger: Push events, Merge request events                       │
│                                                                      │
│   BITBUCKET WEBHOOK                                                  │
│   ─────────────────                                                  │
│   URL: https://jenkins.example.com/bitbucket-hook/                 │
│   Events: Repository push                                           │
│                                                                      │
│   REQUIREMENTS                                                       │
│   ────────────                                                       │
│   • Jenkins must be accessible from internet (or VPN)              │
│   • Firewall rules allowing incoming webhook requests              │
│   • SSL certificate recommended for security                       │
│   • Webhook secret for authentication                              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Scheduled Builds

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Scheduled Build Examples                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   NIGHTLY BUILD                                                      │
│   ─────────────                                                      │
│   H 2 * * *              Every day around 2 AM                     │
│   0 2 * * *              Every day at exactly 2:00 AM              │
│                                                                      │
│   WEEKLY BUILD                                                       │
│   ────────────                                                       │
│   H 9 * * 1              Every Monday around 9 AM                  │
│   0 9 * * 1-5            Weekdays at 9:00 AM                       │
│                                                                      │
│   MULTIPLE TIMES                                                     │
│   ──────────────                                                     │
│   H 9,12,17 * * 1-5      Weekdays at 9, 12, and 5 PM              │
│                                                                      │
│   USE CASES                                                          │
│   ─────────                                                          │
│   ┌──────────────────────────────────────────────────────────────┐ │
│   │ Schedule         │ Use Case                                   │ │
│   │──────────────────────────────────────────────────────────────│ │
│   │ H 2 * * *        │ Nightly integration tests                 │ │
│   │ H 3 * * 0        │ Weekly performance tests (Sunday)         │ │
│   │ H 6 1 * *        │ Monthly security scan                     │ │
│   │ H 0 * * *        │ Hourly smoke tests                        │ │
│   │ H 23 * * 1-5     │ End-of-day reports (weekdays)            │ │
│   │ H 8 * * 1        │ Monday morning standup metrics           │ │
│   └──────────────────────────────────────────────────────────────┘ │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Upstream/Downstream Triggers

```
┌─────────────────────────────────────────────────────────────────────┐
│               Upstream/Downstream Job Triggers                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   BUILD CHAIN                                                        │
│   ───────────                                                        │
│                                                                      │
│   ┌────────────┐     ┌────────────┐     ┌────────────┐             │
│   │  build-job │────▶│  test-job  │────▶│ deploy-job │             │
│   │ (upstream) │     │            │     │(downstream)│             │
│   └────────────┘     └────────────┘     └────────────┘             │
│         │                                      ▲                    │
│         └──────────────────────────────────────┘                    │
│           "Trigger deploy-job after build-job succeeds"            │
│                                                                      │
│   CONFIGURATION OPTIONS                                             │
│   ─────────────────────                                             │
│                                                                      │
│   In upstream job (post-build action):                             │
│   "Build other projects": test-job, deploy-job                     │
│   Trigger when: Stable, Unstable, Always                           │
│                                                                      │
│   In downstream job (build trigger):                               │
│   "Build after other projects are built": build-job                │
│   Trigger when: Stable, Unstable, Always, Failed                   │
│                                                                      │
│   PASSING PARAMETERS                                                │
│   ──────────────────                                                │
│   Use "Trigger parameterized build" plugin                         │
│   Pass: Current build parameters, predefined values, file          │
│                                                                      │
│   Example:                                                          │
│   build-job passes VERSION=${BUILD_NUMBER} to deploy-job          │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Pipeline with Multiple Triggers

```groovy
pipeline {
    agent any
    
    triggers {
        // Poll SCM every 5 minutes
        pollSCM('H/5 * * * *')
        
        // Also trigger on schedule (nightly)
        cron('H 2 * * *')
        
        // Trigger when upstream job completes
        upstream(
            upstreamProjects: 'build-job',
            threshold: hudson.model.Result.SUCCESS
        )
    }
    
    stages {
        stage('Build') {
            steps {
                echo 'Building...'
                sh 'mvn clean package'
            }
        }
    }
}
```

### Webhook Trigger Pipeline

```groovy
pipeline {
    agent any
    
    triggers {
        // GitHub webhook trigger
        githubPush()
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    echo "Triggered by commit: ${env.GIT_COMMIT}"
                    echo "Branch: ${env.GIT_BRANCH}"
                }
            }
        }
        
        stage('Build') {
            steps {
                sh 'npm install'
                sh 'npm run build'
            }
        }
    }
}
```

### Generic Webhook Trigger

```groovy
pipeline {
    agent any
    
    triggers {
        GenericTrigger(
            genericVariables: [
                [key: 'BRANCH', value: '$.ref'],
                [key: 'COMMIT', value: '$.after'],
                [key: 'REPO', value: '$.repository.name']
            ],
            causeString: 'Triggered by $REPO commit $COMMIT',
            token: 'my-webhook-token',
            printContributedVariables: true,
            printPostContent: true,
            silentResponse: false,
            regexpFilterText: '$BRANCH',
            regexpFilterExpression: 'refs/heads/(main|develop)'
        )
    }
    
    stages {
        stage('Info') {
            steps {
                echo "Branch: ${BRANCH}"
                echo "Commit: ${COMMIT}"
                echo "Repository: ${REPO}"
            }
        }
    }
}
```

### Parameterized Manual Trigger

```groovy
pipeline {
    agent any
    
    parameters {
        string(
            name: 'VERSION',
            defaultValue: '1.0.0',
            description: 'Version to deploy'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['staging', 'production'],
            description: 'Target environment'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Skip integration tests'
        )
        password(
            name: 'DEPLOY_KEY',
            defaultValue: '',
            description: 'Deployment secret key'
        )
    }
    
    stages {
        stage('Validate') {
            steps {
                script {
                    if (params.ENVIRONMENT == 'production' && params.SKIP_TESTS) {
                        error 'Cannot skip tests for production deployment'
                    }
                }
            }
        }
        
        stage('Deploy') {
            steps {
                echo "Deploying version ${params.VERSION} to ${params.ENVIRONMENT}"
            }
        }
    }
}
```

### Trigger Downstream Jobs

```groovy
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Trigger Downstream') {
            steps {
                // Trigger with current parameters
                build job: 'integration-tests', parameters: [
                    string(name: 'BUILD_VERSION', value: "${BUILD_NUMBER}"),
                    string(name: 'GIT_COMMIT', value: "${GIT_COMMIT}")
                ]
                
                // Trigger without waiting
                build job: 'notification-job', wait: false
                
                // Trigger and propagate failure
                build job: 'quality-gate', propagate: true
            }
        }
    }
}
```

### Conditional Triggers

```groovy
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Deploy to Staging') {
            when {
                branch 'develop'
            }
            steps {
                build job: 'deploy-staging'
            }
        }
        
        stage('Deploy to Production') {
            when {
                allOf {
                    branch 'main'
                    triggeredBy 'TimerTrigger'  // Only from scheduled builds
                }
            }
            steps {
                build job: 'deploy-production'
            }
        }
    }
}
```

### Scheduled Jobs with Different Schedules per Branch

```groovy
pipeline {
    agent any
    
    triggers {
        // Different schedules based on branch
        cron(env.BRANCH_NAME == 'main' ? 'H 2 * * *' : 'H 4 * * 1')
    }
    
    stages {
        stage('Run Tests') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'main') {
                        echo 'Running full test suite (nightly)'
                        sh 'mvn verify'
                    } else {
                        echo 'Running weekly regression tests'
                        sh 'mvn test -Dgroups=regression'
                    }
                }
            }
        }
    }
}
```

### Webhook Setup Script

```bash
#!/bin/bash
# Script to configure GitHub webhook

GITHUB_TOKEN="${GITHUB_TOKEN}"
REPO="org/repo"
JENKINS_URL="https://jenkins.example.com"
WEBHOOK_SECRET="your-secret"

curl -X POST \
  -H "Authorization: token ${GITHUB_TOKEN}" \
  -H "Content-Type: application/json" \
  "https://api.github.com/repos/${REPO}/hooks" \
  -d '{
    "name": "web",
    "active": true,
    "events": ["push", "pull_request"],
    "config": {
      "url": "'"${JENKINS_URL}"'/github-webhook/",
      "content_type": "json",
      "secret": "'"${WEBHOOK_SECRET}"'",
      "insecure_ssl": "0"
    }
  }'
```

## Summary

- **SCM Polling** checks repository at intervals using cron syntax
- **Webhooks** provide instant triggers when code is pushed (recommended)
- **Scheduled builds** run at specific times for nightly tests, reports, etc.
- **Manual triggers** allow parameterized on-demand execution
- **Upstream/Downstream** chains jobs together in build pipelines
- Use `H` in cron expressions to distribute load across Jenkins

## Additional Resources

- [Pipeline Triggers](https://www.jenkins.io/doc/book/pipeline/syntax/#triggers) - Official trigger documentation
- [Generic Webhook Trigger Plugin](https://plugins.jenkins.io/generic-webhook-trigger/) - Flexible webhook handling
- [GitHub Integration](https://www.jenkins.io/solutions/github/) - GitHub webhook setup guide

