# Jenkins Plugins

## Learning Objectives

- Understand the Jenkins plugin architecture
- Install and manage plugins via UI and CLI
- Identify essential plugins for common CI/CD tasks
- Configure plugins for specific workflows
- Troubleshoot plugin compatibility issues
- Use Blue Ocean for modern pipeline visualization

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Jenkins' power comes from its plugin ecosystem. With over 1,800 plugins, Jenkins can integrate with virtually any tool in your DevOps stack. However, plugins also introduce complexity—version compatibility, security updates, and configuration management.

As a quality engineer, plugins enable test framework integration (JUnit, TestNG), reporting (Allure, HTML Publisher), and tool connectivity (Selenium, SonarQube). Understanding plugin management helps you extend Jenkins for your testing needs.

## The Concept

### Plugin Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Jenkins Plugin Architecture                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                     Jenkins Core                             │   │
│   │    (scheduling, security, web UI, CLI, base features)        │   │
│   └─────────────────────────┬───────────────────────────────────┘   │
│                             │                                        │
│             ┌───────────────┼───────────────────────┐               │
│             │               │                       │               │
│             ▼               ▼                       ▼               │
│   ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐      │
│   │ Pipeline Plugin │ │  Git Plugin     │ │ Docker Plugin   │      │
│   │                 │ │                 │ │                 │      │
│   │ Jenkinsfile     │ │ SCM checkout    │ │ Docker agents   │      │
│   │ stages, steps   │ │ polling         │ │ image builds    │      │
│   └─────────────────┘ └─────────────────┘ └─────────────────┘      │
│             │                                                        │
│             ▼                                                        │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                   Plugin Dependencies                        │   │
│   │                                                              │   │
│   │   Pipeline Plugin depends on:                               │   │
│   │   • workflow-api                                            │   │
│   │   • workflow-step-api                                       │   │
│   │   • script-security                                         │   │
│   │   • ...many more                                            │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   PLUGIN STORAGE                                                    │
│   ──────────────                                                    │
│   $JENKINS_HOME/plugins/                                           │
│   ├── git.jpi                (plugin archive)                      │
│   ├── git/                   (unpacked plugin)                     │
│   ├── pipeline.jpi                                                 │
│   └── ...                                                          │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Essential Plugin Categories

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Essential Plugin Categories                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   PIPELINE & WORKFLOW                                               │
│   ───────────────────                                               │
│   Pipeline (workflow-aggregator)    Core pipeline functionality    │
│   Pipeline: Stage View              Visual stage progress          │
│   Blue Ocean                        Modern UI for pipelines        │
│                                                                      │
│   SOURCE CONTROL                                                     │
│   ──────────────                                                     │
│   Git                               Git integration                 │
│   GitHub                            GitHub integration              │
│   GitLab                            GitLab integration              │
│   Bitbucket                         Bitbucket integration          │
│                                                                      │
│   BUILD TOOLS                                                        │
│   ───────────                                                        │
│   Maven Integration                 Maven builds                   │
│   Gradle                            Gradle builds                  │
│   NodeJS                            Node.js environment            │
│   Docker Pipeline                   Docker in pipelines            │
│                                                                      │
│   TESTING & QUALITY                                                  │
│   ─────────────────                                                  │
│   JUnit                             Test result publishing         │
│   Allure                            Allure report integration      │
│   Code Coverage API                 Coverage visualization         │
│   SonarQube Scanner                 Code quality analysis          │
│                                                                      │
│   NOTIFICATIONS                                                      │
│   ─────────────                                                      │
│   Slack Notification                Slack messages                 │
│   Email Extension                   Rich email notifications       │
│   Microsoft Teams                   Teams webhooks                 │
│                                                                      │
│   CREDENTIALS & SECURITY                                            │
│   ──────────────────────                                            │
│   Credentials                       Credential storage             │
│   Credentials Binding               Use creds in builds            │
│   Role-based Authorization          RBAC for Jenkins               │
│                                                                      │
│   INFRASTRUCTURE                                                     │
│   ──────────────                                                     │
│   Amazon EC2                        EC2 cloud agents               │
│   Kubernetes                        K8s pod agents                 │
│   Docker                            Docker agent support           │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Plugin Management

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Plugin Management                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   VIA WEB UI                                                        │
│   ──────────                                                        │
│   Manage Jenkins → Manage Plugins                                   │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │ TABS                                                         │   │
│   │ ────                                                         │   │
│   │ Updates     Plugins with available updates                  │   │
│   │ Available   Plugins not yet installed                       │   │
│   │ Installed   Currently installed plugins                     │   │
│   │ Advanced    Manual upload, update site settings             │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   INSTALL PROCESS                                                    │
│   ───────────────                                                    │
│   1. Check "Install" next to plugin                                │
│   2. Click "Install without restart" or "Download and install"     │
│   3. Some plugins require Jenkins restart                          │
│   4. Dependencies are automatically included                       │
│                                                                      │
│   UPDATE PROCESS                                                     │
│   ──────────────                                                     │
│   1. Review available updates                                      │
│   2. Check compatibility with Jenkins version                      │
│   3. Update in maintenance window (some require restart)           │
│   4. Test after updates                                            │
│                                                                      │
│   BEST PRACTICES                                                     │
│   ──────────────                                                     │
│   • Keep plugins updated for security                              │
│   • Test updates in staging first                                  │
│   • Document installed plugins and versions                        │
│   • Avoid unnecessary plugins (attack surface)                     │
│   • Use plugin version pinning in production                       │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Blue Ocean

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Blue Ocean UI                                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Modern, visual UI for Jenkins pipelines                           │
│                                                                      │
│   FEATURES                                                           │
│   ────────                                                           │
│   • Visual pipeline editor                                         │
│   • Modern, intuitive design                                       │
│   • Better pipeline visualization                                  │
│   • GitHub/Bitbucket integration wizard                            │
│   • Branch and PR filtering                                        │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Blue Ocean Pipeline View                                    │   │
│   │  ─────────────────────────                                   │   │
│   │                                                              │   │
│   │  my-app #45  ✓ SUCCESS  2m 34s                              │   │
│   │                                                              │   │
│   │  ┌────────┐   ┌────────┐   ┌────────┐   ┌────────┐         │   │
│   │  │Checkout│──▶│ Build  │──▶│  Test  │──▶│ Deploy │         │   │
│   │  │   ✓    │   │   ✓    │   │   ✓    │   │   ✓    │         │   │
│   │  │  10s   │   │  45s   │   │  1m    │   │  40s   │         │   │
│   │  └────────┘   └────────┘   └────────┘   └────────┘         │   │
│   │                                                              │   │
│   │  Click any stage to see logs                                │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   ACCESSING BLUE OCEAN                                              │
│   ────────────────────                                              │
│   Classic UI: http://jenkins:8080/                                 │
│   Blue Ocean: http://jenkins:8080/blue/                            │
│                                                                      │
│   NOTE: Blue Ocean is complementary, not a replacement             │
│   Some admin tasks still require classic UI                        │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Install Plugins via CLI

```bash
# Using jenkins-cli.jar
java -jar jenkins-cli.jar -s http://localhost:8080 \
  -auth admin:password install-plugin git pipeline-stage-view blue-ocean

# Install and restart
java -jar jenkins-cli.jar -s http://localhost:8080 \
  -auth admin:password install-plugin docker-workflow -restart

# List installed plugins
java -jar jenkins-cli.jar -s http://localhost:8080 \
  -auth admin:password list-plugins
```

### Install Plugins via Docker

```dockerfile
# Dockerfile
FROM jenkins/jenkins:lts

# Install plugins from file
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt
```

```text
# plugins.txt
git:5.2.0
workflow-aggregator:596.v8c21c963d92d
blueocean:1.27.5
docker-workflow:563.vd5d2e5c4007f
credentials-binding:642.v737c34dea_6c2
junit:1240.vf9529b_881428
slack:684.v833089650554
```

### Install Plugins Programmatically

```groovy
// init.groovy.d/install-plugins.groovy
import jenkins.model.*
import hudson.model.*

def instance = Jenkins.getInstance()
def pm = instance.getPluginManager()
def uc = instance.getUpdateCenter()

// Update available plugins list
uc.updateAllSites()

def plugins = [
    'git',
    'workflow-aggregator',
    'blueocean',
    'docker-workflow',
    'credentials-binding'
]

plugins.each { pluginName ->
    if (!pm.getPlugin(pluginName)) {
        def plugin = uc.getPlugin(pluginName)
        if (plugin) {
            plugin.deploy()
            println "Installed: ${pluginName}"
        }
    }
}

instance.save()
```

### Plugin Configuration via JCasC

```yaml
# jenkins.yaml
jenkins:
  systemMessage: "Jenkins configured with JCasC"

unclassified:
  # Git plugin configuration
  gitSCM:
    globalConfigName: "jenkins"
    globalConfigEmail: "jenkins@example.com"
  
  # Slack plugin configuration
  slackNotifier:
    teamDomain: "myteam"
    tokenCredentialId: "slack-token"
    room: "#builds"
  
  # SonarQube configuration
  sonarGlobalConfiguration:
    installations:
      - name: "SonarQube"
        serverUrl: "https://sonar.example.com"
        credentialsId: "sonar-token"

tool:
  # Maven tool configuration
  maven:
    installations:
      - name: "Maven 3.8"
        properties:
          - installSource:
              installers:
                - maven:
                    id: "3.8.6"
  
  # JDK configuration
  jdk:
    installations:
      - name: "JDK 17"
        properties:
          - installSource:
              installers:
                - jdkInstaller:
                    id: "jdk-17"
  
  # NodeJS configuration
  nodejs:
    installations:
      - name: "Node 18"
        properties:
          - installSource:
              installers:
                - nodeJSInstaller:
                    id: "18.17.0"
```

### Using Plugin Steps in Pipeline

```groovy
pipeline {
    agent any
    
    tools {
        maven 'Maven 3.8'
        jdk 'JDK 17'
        nodejs 'Node 18'
    }
    
    stages {
        stage('Checkout') {
            steps {
                // Git plugin
                git branch: 'main', 
                    url: 'https://github.com/org/repo.git',
                    credentialsId: 'github-token'
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    // JUnit plugin
                    junit 'target/surefire-reports/*.xml'
                    
                    // Allure plugin
                    allure includeProperties: false,
                           results: [[path: 'target/allure-results']]
                }
            }
        }
        
        stage('Code Quality') {
            steps {
                // SonarQube plugin
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                // Docker plugin
                script {
                    docker.build("myapp:${BUILD_NUMBER}")
                }
            }
        }
        
        stage('Notify') {
            steps {
                // Slack plugin
                slackSend channel: '#builds',
                          color: 'good',
                          message: "Build ${BUILD_NUMBER} succeeded"
            }
        }
    }
}
```

### Check Plugin Versions

```bash
# List plugins with versions via API
curl -s http://localhost:8080/pluginManager/api/json?depth=1 \
  --user admin:password \
  | jq -r '.plugins[] | "\(.shortName):\(.version)"' \
  | sort

# Export to plugins.txt format
curl -s http://localhost:8080/pluginManager/api/json?depth=1 \
  --user admin:password \
  | jq -r '.plugins[] | "\(.shortName):\(.version)"' \
  > installed-plugins.txt
```

### Plugin Compatibility Script

```groovy
// Groovy script to check plugin compatibility
import jenkins.model.*
import hudson.PluginWrapper

def jenkins = Jenkins.getInstance()
def plugins = jenkins.getPluginManager().getPlugins()

println "Jenkins Version: ${Jenkins.VERSION}"
println "Plugins requiring update:"
println "========================="

plugins.each { plugin ->
    def wrapper = plugin
    if (wrapper.hasUpdate()) {
        println "${wrapper.getShortName()}: ${wrapper.getVersion()} -> ${wrapper.getUpdateInfo()?.version}"
    }
}

println "\nPlugins with issues:"
println "===================="

plugins.each { plugin ->
    def wrapper = plugin
    if (!wrapper.isActive()) {
        println "${wrapper.getShortName()}: INACTIVE - ${wrapper.getDisableReason()}"
    }
}
```

### Custom Plugin Configuration

```groovy
// Configure Email Extension plugin
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
    }
    
    post {
        failure {
            emailext(
                subject: "FAILED: ${currentBuild.fullDisplayName}",
                body: '''
                    <h2>Build Failed</h2>
                    <p>Job: ${JOB_NAME}</p>
                    <p>Build: ${BUILD_NUMBER}</p>
                    <p>Status: ${BUILD_STATUS}</p>
                    <p>Console: <a href="${BUILD_URL}console">${BUILD_URL}console</a></p>
                    <h3>Changes:</h3>
                    ${CHANGES}
                    <h3>Failed Tests:</h3>
                    ${FAILED_TESTS}
                ''',
                to: 'team@example.com',
                recipientProviders: [developers(), culprits()],
                attachLog: true
            )
        }
    }
}
```

## Summary

- **Jenkins plugins** extend functionality for SCM, builds, testing, notifications, and more
- **Essential plugins**: Pipeline, Git, Docker, JUnit, Credentials, Blue Ocean
- **Install plugins** via UI, CLI, Docker, or JCasC
- **Blue Ocean** provides modern pipeline visualization
- **Best practices**: Keep updated, test before production, document versions
- Over 1,800 plugins available at [plugins.jenkins.io](https://plugins.jenkins.io)

## Additional Resources

- [Jenkins Plugin Index](https://plugins.jenkins.io/) - Searchable plugin directory
- [Plugin Installation Manager](https://github.com/jenkinsci/plugin-installation-manager-tool) - CLI tool
- [Blue Ocean Documentation](https://www.jenkins.io/doc/book/blueocean/) - Blue Ocean guide

