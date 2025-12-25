# Jenkins Best Practices

## Learning Objectives

- Apply pipeline design best practices for maintainability
- Implement security hardening for Jenkins installations
- Optimize build performance and resource usage
- Establish backup and disaster recovery procedures
- Follow organizational standards for pipeline development
- Create shared libraries for code reuse

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

A poorly maintained Jenkins installation becomes a liability—slow builds, security vulnerabilities, and unreliable deployments. Best practices ensure Jenkins remains a reliable backbone for your CI/CD process, not a bottleneck.

As a quality engineer, these practices affect test reliability. Flaky builds often trace back to poor pipeline design, insufficient resources, or environment inconsistencies that best practices help prevent.

## The Concept

### Pipeline Design Best Practices

```
┌─────────────────────────────────────────────────────────────────────┐
│                Pipeline Design Best Practices                        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   DO                                   DON'T                        │
│   ──                                   ─────                        │
│                                                                      │
│   ✓ Use declarative syntax            ✗ Overuse scripted syntax    │
│   ✓ Keep Jenkinsfile in repo          ✗ Configure in UI            │
│   ✓ Use shared libraries              ✗ Copy-paste pipelines       │
│   ✓ Fail fast (critical first)        ✗ Run long tests first       │
│   ✓ Parallelize where possible        ✗ Sequential when parallel ok │
│   ✓ Clean workspace between builds    ✗ Rely on leftover files     │
│   ✓ Pin tool versions                 ✗ Use "latest" versions      │
│   ✓ Use meaningful stage names        ✗ Generic names like "Step1" │
│                                                                      │
│   PIPELINE STRUCTURE                                                │
│   ──────────────────                                                │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  1. Checkout         (fast, get code)                        │   │
│   │  2. Build            (compile, package)                      │   │
│   │  3. Unit Tests       (fast feedback)                        │   │
│   │  4. Static Analysis  (parallel with tests)                  │   │
│   │  5. Integration Tests (may need deployment)                 │   │
│   │  6. Security Scan    (parallel with tests)                  │   │
│   │  7. Build Artifacts  (Docker image, package)                │   │
│   │  8. Deploy (staged)  (dev → staging → prod)                │   │
│   │  9. Smoke Tests      (verify deployment)                    │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Security Best Practices

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Security Best Practices                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ACCESS CONTROL                                                     │
│   ──────────────                                                     │
│   ✓ Use role-based access (RBAC plugin)                            │
│   ✓ Integrate with SSO (LDAP, SAML, OAuth)                        │
│   ✓ Principle of least privilege                                   │
│   ✓ Audit user actions                                             │
│   ✗ Share admin credentials                                        │
│   ✗ Use "Anyone can do anything" security                         │
│                                                                      │
│   CREDENTIALS                                                        │
│   ───────────                                                        │
│   ✓ Use Jenkins credentials store                                  │
│   ✓ Limit credential scope (folder-level)                         │
│   ✓ Rotate credentials regularly                                   │
│   ✓ Use credential binding in pipelines                           │
│   ✗ Hardcode passwords in Jenkinsfile                             │
│   ✗ Echo credentials in logs                                       │
│                                                                      │
│   NETWORK                                                            │
│   ───────                                                            │
│   ✓ Use HTTPS for Jenkins UI                                       │
│   ✓ Put Jenkins behind reverse proxy                               │
│   ✓ Restrict agent communication ports                             │
│   ✓ Use VPN for remote agents                                      │
│   ✗ Expose Jenkins to public internet                              │
│                                                                      │
│   PLUGINS                                                            │
│   ───────                                                            │
│   ✓ Keep plugins updated                                           │
│   ✓ Remove unused plugins                                          │
│   ✓ Review plugin security advisories                              │
│   ✓ Minimize installed plugins                                     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Performance Optimization

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Performance Optimization                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   BUILD SPEED                                                        │
│   ───────────                                                        │
│   • Parallelize independent stages                                 │
│   • Use incremental builds where possible                          │
│   • Cache dependencies (Maven .m2, npm node_modules)               │
│   • Shallow git clone (depth=1)                                    │
│   • Use fast agents for time-critical builds                       │
│                                                                      │
│   RESOURCE MANAGEMENT                                               │
│   ───────────────────                                               │
│   • Set master executors to 0                                      │
│   • Right-size agent resources                                     │
│   • Use cloud agents for elastic scaling                           │
│   • Limit concurrent builds per project                            │
│                                                                      │
│   STORAGE                                                            │
│   ───────                                                            │
│   • Configure build rotation (discard old)                         │
│   • Archive only necessary artifacts                               │
│   • Use external artifact storage (Nexus, S3)                      │
│   • Periodically clean workspace                                   │
│                                                                      │
│   OPTIMIZATION EXAMPLE                                              │
│   ────────────────────                                              │
│   BEFORE                         AFTER                              │
│   ──────                         ─────                              │
│   Full clone (5GB repo)          Shallow clone (100MB)             │
│   Sequential tests: 20min        Parallel tests: 8min              │
│   No caching                     Cached dependencies               │
│   Large artifacts stored         External S3 storage               │
│                                                                      │
│   Result: 20min → 10min build time                                │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Well-Structured Pipeline

```groovy
pipeline {
    agent none
    
    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timestamps()
    }
    
    environment {
        MAVEN_OPTS = '-Xmx1024m'
    }
    
    stages {
        stage('Build') {
            agent { label 'linux && maven' }
            steps {
                checkout([
                    $class: 'GitSCM',
                    extensions: [[$class: 'CloneOption', depth: 1, shallow: true]]
                ])
                sh 'mvn clean compile -DskipTests'
                stash name: 'compiled', includes: 'target/**'
            }
        }
        
        stage('Quality Checks') {
            parallel {
                stage('Unit Tests') {
                    agent { label 'linux' }
                    steps {
                        unstash 'compiled'
                        sh 'mvn test'
                    }
                    post {
                        always { junit '**/surefire-reports/*.xml' }
                    }
                }
                stage('Static Analysis') {
                    agent { label 'linux' }
                    steps {
                        unstash 'compiled'
                        sh 'mvn checkstyle:check pmd:check'
                    }
                }
            }
        }
        
        stage('Package') {
            agent { label 'linux && docker' }
            steps {
                unstash 'compiled'
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        
        stage('Deploy') {
            when { branch 'main' }
            agent { label 'deploy' }
            steps {
                unstash 'compiled'
                sh './deploy.sh'
            }
        }
    }
    
    post {
        always { cleanWs() }
        failure {
            slackSend color: 'danger', message: "Build failed: ${BUILD_URL}"
        }
    }
}
```

### Shared Library Structure

```
vars/
├── buildMaven.groovy
├── deployToEnv.groovy
└── notifySlack.groovy
src/
└── org/example/
    └── PipelineUtils.groovy
resources/
└── templates/
```

```groovy
// vars/buildMaven.groovy
def call(Map config = [:]) {
    def mavenVersion = config.mavenVersion ?: 'Maven 3.8'
    def goals = config.goals ?: 'clean package'
    
    withMaven(maven: mavenVersion) {
        sh "mvn ${goals}"
    }
}

// vars/deployToEnv.groovy  
def call(String environment) {
    def envConfig = [
        'dev': [url: 'dev.example.com', approve: false],
        'staging': [url: 'staging.example.com', approve: false],
        'production': [url: 'prod.example.com', approve: true]
    ]
    
    def config = envConfig[environment]
    if (!config) {
        error "Unknown environment: ${environment}"
    }
    
    if (config.approve) {
        input message: "Deploy to ${environment}?", ok: 'Deploy'
    }
    
    sh "./deploy.sh ${config.url}"
}
```

### Using Shared Library

```groovy
@Library('my-shared-library') _

pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                buildMaven(goals: 'clean package')
            }
        }
        
        stage('Deploy') {
            steps {
                deployToEnv('staging')
            }
        }
    }
    
    post {
        always {
            notifySlack(currentBuild.result)
        }
    }
}
```

### Backup Configuration

```bash
#!/bin/bash
# backup-jenkins.sh

JENKINS_HOME="/var/jenkins_home"
BACKUP_DIR="/backups/jenkins"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/jenkins_backup_${DATE}.tar.gz"

# Important directories
tar -czf ${BACKUP_FILE} \
    ${JENKINS_HOME}/config.xml \
    ${JENKINS_HOME}/credentials.xml \
    ${JENKINS_HOME}/secrets/ \
    ${JENKINS_HOME}/users/ \
    ${JENKINS_HOME}/jobs/*/config.xml \
    ${JENKINS_HOME}/nodes/ \
    ${JENKINS_HOME}/plugins/*.jpi

# Retain last 30 days
find ${BACKUP_DIR} -name "jenkins_backup_*.tar.gz" -mtime +30 -delete

echo "Backup completed: ${BACKUP_FILE}"
```

### JCasC Complete Example

```yaml
# jenkins.yaml
jenkins:
  systemMessage: "Production Jenkins - Managed by JCasC"
  numExecutors: 0
  mode: EXCLUSIVE
  
  securityRealm:
    ldap:
      configurations:
        - server: "ldap.example.com"
          rootDN: "dc=example,dc=com"
  
  authorizationStrategy:
    roleBased:
      roles:
        global:
          - name: "admin"
            permissions: ["Overall/Administer"]
            entries:
              - group: "jenkins-admins"
          - name: "developer"
            permissions: ["Job/Build", "Job/Read"]
            entries:
              - group: "developers"

unclassified:
  location:
    url: "https://jenkins.example.com/"

tool:
  maven:
    installations:
      - name: "Maven 3.8"
        properties:
          - installSource:
              installers:
                - maven:
                    id: "3.8.6"
```

## Summary

- **Pipeline design**: Use declarative, keep in repo, fail fast, parallelize
- **Security**: RBAC, credential scopes, HTTPS, update plugins
- **Performance**: Parallel stages, shallow clones, cache dependencies, cloud agents
- **Shared libraries** enable code reuse across pipelines
- **JCasC** enables version-controlled Jenkins configuration
- **Regular backups** of config, credentials, and job definitions are essential

## Additional Resources

- [Jenkins Best Practices](https://www.jenkins.io/doc/book/pipeline/pipeline-best-practices/) - Official guide
- [Shared Libraries](https://www.jenkins.io/doc/book/pipeline/shared-libraries/) - Library documentation
- [Configuration as Code](https://plugins.jenkins.io/configuration-as-code/) - JCasC plugin

