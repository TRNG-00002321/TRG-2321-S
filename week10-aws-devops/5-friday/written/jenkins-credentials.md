# Jenkins Credentials Management

## Learning Objectives

- Understand Jenkins credential types and their use cases
- Create and manage credentials in the Jenkins UI
- Use credentials in Freestyle jobs and Pipelines
- Apply credential scopes appropriately (system, global, folder)
- Secure sensitive information in builds
- Follow best practices for credential management

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

CI/CD pipelines need access to private repositories, cloud providers, databases, and deployment targets. Hardcoding passwords is a security disaster waiting to happen. Jenkins credentials provide secure storage and controlled access to secrets.

As a quality engineer, you'll use credentials to access test environments, external services, and reporting tools. Understanding how to securely reference credentials prevents accidental exposure of sensitive data.

## The Concept

### Credential Types

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Jenkins Credential Types                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   USERNAME WITH PASSWORD                                             │
│   ──────────────────────                                             │
│   • Git repository access                                           │
│   • Docker registry login                                           │
│   • Database connections                                            │
│   • API basic auth                                                  │
│                                                                      │
│   SECRET TEXT                                                        │
│   ───────────                                                        │
│   • API tokens                                                      │
│   • Webhook secrets                                                 │
│   • Single secret values                                            │
│                                                                      │
│   SECRET FILE                                                        │
│   ───────────                                                        │
│   • Configuration files                                             │
│   • Kubernetes configs                                              │
│   • Certificate bundles                                             │
│                                                                      │
│   SSH USERNAME WITH PRIVATE KEY                                      │
│   ─────────────────────────────                                      │
│   • Git SSH access                                                  │
│   • Server SSH access                                               │
│   • Deployment targets                                              │
│                                                                      │
│   CERTIFICATE                                                        │
│   ───────────                                                        │
│   • PKCS#12 certificates                                           │
│   • Client certificates                                             │
│   • Code signing                                                    │
│                                                                      │
│   AWS CREDENTIALS (Plugin)                                          │
│   ────────────────────────                                          │
│   • AWS Access Key ID + Secret                                     │
│   • IAM roles                                                       │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Credential Scopes

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Credential Scopes                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   SYSTEM SCOPE                                                       │
│   ────────────                                                       │
│   Only available to Jenkins itself (system operations)              │
│   • Agent connections                                               │
│   • Plugin authentication                                           │
│   • Jenkins-to-external-system connections                         │
│   NOT available to user jobs                                        │
│                                                                      │
│   GLOBAL SCOPE                                                       │
│   ────────────                                                       │
│   Available to all jobs in Jenkins                                  │
│   • Shared credentials (Git, Docker)                               │
│   • Company-wide service accounts                                   │
│   Use for: Cross-project shared credentials                        │
│                                                                      │
│   FOLDER SCOPE (with Folders plugin)                                │
│   ────────────────────────────────────                              │
│   Available only to jobs within a folder                           │
│   • Team-specific credentials                                       │
│   • Project-specific secrets                                        │
│   Use for: Limiting access to credentials                          │
│                                                                      │
│   HIERARCHY                                                          │
│   ─────────                                                          │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Jenkins (System + Global credentials)                       │   │
│   │    │                                                         │   │
│   │    ├── Folder: Team-A (Folder credentials)                  │   │
│   │    │     ├── Job: app-build                                 │   │
│   │    │     └── Job: app-deploy                                │   │
│   │    │         (can access Team-A + Global creds)             │   │
│   │    │                                                         │   │
│   │    └── Folder: Team-B (Folder credentials)                  │   │
│   │          └── Job: api-build                                 │   │
│   │              (can access Team-B + Global creds)             │   │
│   │              (CANNOT access Team-A creds)                   │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Creating Credentials

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Creating Credentials via UI                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   NAVIGATION                                                         │
│   ──────────                                                         │
│   Dashboard → Manage Jenkins → Manage Credentials                   │
│                                                                      │
│   Or: Dashboard → Credentials → System → Global credentials        │
│                                                                      │
│   ADD NEW CREDENTIAL                                                 │
│   ──────────────────                                                 │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Kind: [Username with password              ▼]              │   │
│   │  Scope: [Global (Jenkins, nodes, items)     ▼]              │   │
│   │                                                              │   │
│   │  Username: [jenkins-bot                          ]          │   │
│   │  Password: [●●●●●●●●●●●●                        ]          │   │
│   │                                                              │   │
│   │  ID: [github-credentials              ]  (required!)        │   │
│   │  Description: [GitHub service account           ]           │   │
│   │                                                              │   │
│   │  [Create]                                                   │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   IMPORTANT: Always set a meaningful ID                            │
│   The ID is used to reference the credential in jobs/pipelines     │
│   If left blank, Jenkins generates a UUID (hard to remember)       │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Using Credentials in Builds

```
┌─────────────────────────────────────────────────────────────────────┐
│                Using Credentials in Jobs                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   FREESTYLE JOB - Source Code Management                            │
│   ──────────────────────────────────────                            │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Git                                                         │   │
│   │  Repository URL: [git@github.com:org/repo.git        ]      │   │
│   │  Credentials: [github-ssh-key                      ▼]      │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   FREESTYLE JOB - Build Environment                                 │
│   ─────────────────────────────────                                 │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  [✓] Use secret text(s) or file(s)                          │   │
│   │                                                              │   │
│   │  Bindings:                                                   │   │
│   │  ┌────────────────────────────────────────────────────────┐ │   │
│   │  │ Variable: [API_TOKEN           ]                       │ │   │
│   │  │ Credential: [api-token-secret  ▼]                     │ │   │
│   │  └────────────────────────────────────────────────────────┘ │   │
│   │                                                              │   │
│   │  Build step can use: ${API_TOKEN}                          │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   SECURITY NOTE                                                      │
│   ─────────────                                                      │
│   • Credentials are masked in console output                       │
│   • Never echo credential values directly                          │
│   • Use credential bindings, not environment variables             │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Using Credentials in Pipeline

```groovy
pipeline {
    agent any
    
    environment {
        // Bind username/password credential
        GIT_CREDS = credentials('github-credentials')
        // Creates: GIT_CREDS_USR and GIT_CREDS_PSW
        
        // Bind secret text
        API_TOKEN = credentials('api-token-secret')
    }
    
    stages {
        stage('Use Credentials') {
            steps {
                // Variables are automatically available
                sh '''
                    echo "Username: $GIT_CREDS_USR"
                    # Password is masked in logs: ****
                    curl -u $GIT_CREDS_USR:$GIT_CREDS_PSW https://api.example.com
                    
                    # Secret text
                    curl -H "Authorization: Bearer $API_TOKEN" https://api.example.com
                '''
            }
        }
    }
}
```

### withCredentials Block

```groovy
pipeline {
    agent any
    
    stages {
        stage('With Username/Password') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'docker-hub',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh '''
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        docker push myimage:latest
                    '''
                }
            }
        }
        
        stage('With Secret Text') {
            steps {
                withCredentials([
                    string(credentialsId: 'slack-token', variable: 'SLACK_TOKEN')
                ]) {
                    sh '''
                        curl -X POST -H "Authorization: Bearer $SLACK_TOKEN" \
                             https://slack.com/api/chat.postMessage
                    '''
                }
            }
        }
        
        stage('With SSH Key') {
            steps {
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'deploy-ssh-key',
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                    )
                ]) {
                    sh '''
                        ssh -i $SSH_KEY $SSH_USER@server.example.com "deploy.sh"
                    '''
                }
            }
        }
        
        stage('With Secret File') {
            steps {
                withCredentials([
                    file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')
                ]) {
                    sh '''
                        kubectl --kubeconfig=$KUBECONFIG get pods
                    '''
                }
            }
        }
    }
}
```

### Multiple Credentials

```groovy
pipeline {
    agent any
    
    stages {
        stage('Deploy') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'docker-hub',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    ),
                    string(
                        credentialsId: 'aws-access-key',
                        variable: 'AWS_ACCESS_KEY_ID'
                    ),
                    string(
                        credentialsId: 'aws-secret-key',
                        variable: 'AWS_SECRET_ACCESS_KEY'
                    ),
                    file(
                        credentialsId: 'deploy-config',
                        variable: 'DEPLOY_CONFIG'
                    )
                ]) {
                    sh '''
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        aws ecs update-service --config-file $DEPLOY_CONFIG
                    '''
                }
            }
        }
    }
}
```

### Git Checkout with Credentials

```groovy
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                // HTTPS with username/password
                git(
                    url: 'https://github.com/org/repo.git',
                    credentialsId: 'github-credentials',
                    branch: 'main'
                )
            }
        }
        
        stage('Checkout SSH') {
            steps {
                // SSH with key
                git(
                    url: 'git@github.com:org/repo.git',
                    credentialsId: 'github-ssh-key',
                    branch: 'main'
                )
            }
        }
        
        stage('Clone Private Repo') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'github-credentials',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_TOKEN'
                    )
                ]) {
                    sh '''
                        git clone https://${GIT_USER}:${GIT_TOKEN}@github.com/org/private-repo.git
                    '''
                }
            }
        }
    }
}
```

### Create Credentials via API

```bash
# Create username/password credential
curl -X POST http://localhost:8080/credentials/store/system/domain/_/createCredentials \
  --user admin:password \
  -H "Content-Type: application/xml" \
  -d '
<com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl>
  <scope>GLOBAL</scope>
  <id>my-credential</id>
  <description>My credential description</description>
  <username>myuser</username>
  <password>mypassword</password>
</com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl>
'

# Create secret text credential
curl -X POST http://localhost:8080/credentials/store/system/domain/_/createCredentials \
  --user admin:password \
  -H "Content-Type: application/xml" \
  -d '
<org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl>
  <scope>GLOBAL</scope>
  <id>api-token</id>
  <description>API Token</description>
  <secret>my-secret-value</secret>
</org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl>
'
```

### Credentials in JCasC (Configuration as Code)

```yaml
# jenkins.yaml
credentials:
  system:
    domainCredentials:
      - credentials:
          - usernamePassword:
              scope: GLOBAL
              id: "github-credentials"
              description: "GitHub service account"
              username: "jenkins-bot"
              password: "${GITHUB_PASSWORD}"
          
          - string:
              scope: GLOBAL
              id: "slack-token"
              description: "Slack API token"
              secret: "${SLACK_TOKEN}"
          
          - basicSSHUserPrivateKey:
              scope: GLOBAL
              id: "deploy-ssh-key"
              description: "Deployment SSH key"
              username: "deploy"
              privateKeySource:
                directEntry:
                  privateKey: "${SSH_PRIVATE_KEY}"
          
          - aws:
              scope: GLOBAL
              id: "aws-credentials"
              description: "AWS credentials"
              accessKey: "${AWS_ACCESS_KEY}"
              secretKey: "${AWS_SECRET_KEY}"
```

### Masking Secrets in Output

```groovy
pipeline {
    agent any
    
    stages {
        stage('Safe Credential Usage') {
            steps {
                withCredentials([string(credentialsId: 'api-key', variable: 'API_KEY')]) {
                    // DO: Use in commands
                    sh 'curl -H "X-API-Key: $API_KEY" https://api.example.com'
                    
                    // DON'T: Echo secrets (they get masked, but avoid it)
                    // sh 'echo $API_KEY'  // Will show: ****
                    
                    // DON'T: Write to file without cleanup
                    // sh 'echo $API_KEY > secret.txt'
                    
                    // DO: Use wrap for additional masking
                    wrap([$class: 'MaskPasswordsBuildWrapper']) {
                        sh 'process-with-secret.sh'
                    }
                }
            }
        }
    }
}
```

## Summary

- **Credential types**: username/password, secret text, SSH key, secret file, certificate
- **Scopes**: System (Jenkins only), Global (all jobs), Folder (limited to folder)
- **Always set meaningful IDs** for easy reference in pipelines
- **withCredentials block** binds credentials to variables in pipeline steps
- **environment directive** with `credentials()` for simple binding
- Credentials are **masked in console output** but avoid echoing them
- Use **JCasC** for version-controlled credential structure (values from environment)

## Additional Resources

- [Using Credentials](https://www.jenkins.io/doc/book/using/using-credentials/) - Official credential guide
- [Credentials Plugin](https://plugins.jenkins.io/credentials/) - Core credentials plugin
- [Credentials Binding Plugin](https://plugins.jenkins.io/credentials-binding/) - Pipeline credential binding

