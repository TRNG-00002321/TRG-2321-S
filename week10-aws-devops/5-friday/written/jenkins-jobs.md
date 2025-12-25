# Jenkins Jobs

## Learning Objectives

- Distinguish between Freestyle and Pipeline jobs
- Create and configure Freestyle jobs
- Understand job configuration options and settings
- Work with build steps and post-build actions
- View and interpret build results
- Manage job history and artifacts

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Jobs are the fundamental unit of work in Jenkins. Every build, test run, and deployment is executed as a job. Understanding job types helps you choose the right approach—Freestyle for simple tasks, Pipeline for complex workflows with version-controlled configuration.

As a quality engineer, you'll configure jobs that run test suites, generate reports, and gate deployments. Knowing how to set up, trigger, and troubleshoot jobs is essential for maintaining reliable test automation.

## The Concept

### Job Types Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Jenkins Job Types                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   FREESTYLE PROJECT                                                  │
│   ─────────────────                                                  │
│   • Traditional job type                                            │
│   • Configuration via web UI                                        │
│   • Simple, linear build steps                                      │
│   • Good for simple tasks                                           │
│   • Limited reusability                                             │
│                                                                      │
│   Use when: Simple builds, quick setup, single-step tasks          │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   PIPELINE                                                           │
│   ────────                                                           │
│   • Pipeline as code (Jenkinsfile)                                 │
│   • Version controlled with source code                            │
│   • Complex workflows with stages                                   │
│   • Parallel execution                                              │
│   • Reusable via shared libraries                                  │
│                                                                      │
│   Use when: Complex workflows, CD pipelines, team collaboration    │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   MULTIBRANCH PIPELINE                                               │
│   ────────────────────                                               │
│   • Automatically discovers branches                               │
│   • Creates pipeline per branch                                    │
│   • Great for feature branch workflows                             │
│                                                                      │
│   Use when: Multiple branches need CI, pull request builds         │
│                                                                      │
│   ───────────────────────────────────────────────────────────────   │
│                                                                      │
│   FOLDER                                                             │
│   ──────                                                             │
│   • Organize jobs into groups                                      │
│   • Shared credentials scope                                       │
│   • Access control per folder                                      │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Freestyle Job Configuration

```
┌─────────────────────────────────────────────────────────────────────┐
│               Freestyle Job Configuration                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Configure Job: my-freestyle-job                             │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │                                                              │   │
│   │  GENERAL                                                     │   │
│   │  ───────                                                     │   │
│   │  Description: [Build and test my application           ]    │   │
│   │  [✓] Discard old builds                                     │   │
│   │      Days to keep: [7  ] Max builds: [10 ]                  │   │
│   │  [✓] GitHub project                                         │   │
│   │      URL: [https://github.com/org/repo             ]        │   │
│   │                                                              │   │
│   │  SOURCE CODE MANAGEMENT                                      │   │
│   │  ───────────────────────                                     │   │
│   │  (○) None  (●) Git  ( ) Subversion                          │   │
│   │  Repository URL: [git@github.com:org/repo.git       ]       │   │
│   │  Credentials: [github-ssh-key           ▼]                  │   │
│   │  Branch: [*/main                        ]                   │   │
│   │                                                              │   │
│   │  BUILD TRIGGERS                                              │   │
│   │  ──────────────                                              │   │
│   │  [✓] Poll SCM: [H/5 * * * *            ]                    │   │
│   │  [ ] Build periodically                                      │   │
│   │  [✓] GitHub hook trigger for GITScm polling                 │   │
│   │                                                              │   │
│   │  BUILD ENVIRONMENT                                           │   │
│   │  ─────────────────                                           │   │
│   │  [✓] Delete workspace before build starts                   │   │
│   │  [✓] Add timestamps to Console Output                       │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Build Steps

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Build Steps Configuration                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   BUILD STEPS                                                        │
│   ───────────                                                        │
│   [Add build step ▼]                                                │
│                                                                      │
│   1. Execute shell                                                  │
│      ┌─────────────────────────────────────────────────────────┐   │
│      │ #!/bin/bash                                              │   │
│      │ echo "Building application..."                           │   │
│      │ mvn clean compile                                        │   │
│      └─────────────────────────────────────────────────────────┘   │
│                                                                      │
│   2. Execute shell                                                  │
│      ┌─────────────────────────────────────────────────────────┐   │
│      │ echo "Running tests..."                                  │   │
│      │ mvn test                                                  │   │
│      └─────────────────────────────────────────────────────────┘   │
│                                                                      │
│   3. Invoke Ant / Maven / Gradle                                    │
│      ┌─────────────────────────────────────────────────────────┐   │
│      │ Maven version: [Maven 3.8    ▼]                         │   │
│      │ Goals: [package -DskipTests                       ]      │   │
│      └─────────────────────────────────────────────────────────┘   │
│                                                                      │
│   COMMON BUILD STEP TYPES                                           │
│   ───────────────────────                                           │
│   Execute shell           Run bash/sh commands (Linux)             │
│   Execute Windows batch   Run cmd commands (Windows)               │
│   Execute PowerShell      Run PowerShell scripts                   │
│   Invoke Ant/Maven/Gradle Call build tools directly                │
│   Invoke npm              Node.js builds                           │
│   Run with timeout        Time-limited execution                   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Post-Build Actions

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Post-Build Actions                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   POST-BUILD ACTIONS                                                 │
│   ──────────────────                                                 │
│   [Add post-build action ▼]                                         │
│                                                                      │
│   1. Archive the artifacts                                          │
│      Files: [target/*.jar, target/*.war                     ]      │
│      [✓] Fingerprint artifacts                                      │
│                                                                      │
│   2. Publish JUnit test results                                     │
│      Test report XMLs: [target/surefire-reports/*.xml       ]      │
│      [✓] Retain long standard output/error                          │
│                                                                      │
│   3. Email notification                                              │
│      Recipients: [team@example.com                          ]       │
│      [✓] Send to committers                                         │
│                                                                      │
│   4. Slack notification                                             │
│      Channel: [#builds                                      ]       │
│      [✓] Notify on success                                          │
│      [✓] Notify on failure                                          │
│                                                                      │
│   COMMON POST-BUILD ACTIONS                                         │
│   ─────────────────────────                                         │
│   Archive artifacts       Save build outputs                       │
│   Publish test results    JUnit, TestNG reports                    │
│   Email/Slack notify      Alert on build status                    │
│   Deploy                  Push to servers/registries               │
│   Trigger other jobs      Chain builds                             │
│   Clean workspace         Remove files after build                 │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Build Results and History

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Build Results Page                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  my-job #45  ✓ SUCCESS                                      │   │
│   │  ───────────────────────────────────────────────────────────│   │
│   │                                                              │   │
│   │  Started:    Dec 10, 2025 10:30 AM                          │   │
│   │  Duration:   2 min 34 sec                                   │   │
│   │  Commit:     abc1234 (Merged PR #123)                       │   │
│   │  Changes:    3 files changed                                │   │
│   │                                                              │   │
│   │  LEFT MENU                       MAIN AREA                   │   │
│   │  ─────────                       ─────────                   │   │
│   │  Status                          Build #45                   │   │
│   │  Changes                         ────────                    │   │
│   │  Console Output    ◄────────────  Console log text          │   │
│   │  Edit Build Info                 [view full output]          │   │
│   │  Delete Build                                                │   │
│   │  Test Result       ◄────────────  Tests: 150 passed, 0 fail │   │
│   │  Artifacts         ◄────────────  app-1.0.jar (2.5MB)       │   │
│   │  Git Polling Log                                             │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   BUILD STATUS ICONS                                                │
│   ──────────────────                                                │
│   ✓  Blue    SUCCESS     Build completed without errors           │
│   ✗  Red     FAILURE     Build failed                              │
│   !  Yellow  UNSTABLE    Build succeeded but tests failed         │
│   ○  Gray    NOT_BUILT   Never built or disabled                  │
│   ⊘  Gray    ABORTED     Build was manually stopped               │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Freestyle Job XML Configuration

```xml
<!-- job-config.xml -->
<?xml version='1.1' encoding='UTF-8'?>
<project>
  <description>Build and test my application</description>
  <keepDependencies>false</keepDependencies>
  <properties>
    <jenkins.model.BuildDiscarderProperty>
      <strategy class="hudson.tasks.LogRotator">
        <daysToKeep>7</daysToKeep>
        <numToKeep>10</numToKeep>
      </strategy>
    </jenkins.model.BuildDiscarderProperty>
  </properties>
  
  <scm class="hudson.plugins.git.GitSCM">
    <configVersion>2</configVersion>
    <userRemoteConfigs>
      <hudson.plugins.git.UserRemoteConfig>
        <url>git@github.com:org/repo.git</url>
        <credentialsId>github-ssh</credentialsId>
      </hudson.plugins.git.UserRemoteConfig>
    </userRemoteConfigs>
    <branches>
      <hudson.plugins.git.BranchSpec>
        <name>*/main</name>
      </hudson.plugins.git.BranchSpec>
    </branches>
  </scm>
  
  <triggers>
    <hudson.triggers.SCMTrigger>
      <spec>H/5 * * * *</spec>
    </hudson.triggers.SCMTrigger>
  </triggers>
  
  <builders>
    <hudson.tasks.Shell>
      <command>#!/bin/bash
mvn clean test package</command>
    </hudson.tasks.Shell>
  </builders>
  
  <publishers>
    <hudson.tasks.junit.JUnitResultArchiver>
      <testResults>target/surefire-reports/*.xml</testResults>
    </hudson.tasks.junit.JUnitResultArchiver>
    <hudson.tasks.ArtifactArchiver>
      <artifacts>target/*.jar</artifacts>
    </hudson.tasks.ArtifactArchiver>
  </publishers>
</project>
```

### Create Job via API

```bash
# Create a new Freestyle job
curl -X POST http://localhost:8080/createItem?name=my-new-job \
  --user admin:password \
  -H "Content-Type: application/xml" \
  -d @job-config.xml

# Copy existing job
curl -X POST "http://localhost:8080/createItem?name=my-job-copy&mode=copy&from=my-job" \
  --user admin:password

# Update job configuration
curl -X POST http://localhost:8080/job/my-job/config.xml \
  --user admin:password \
  -H "Content-Type: application/xml" \
  -d @updated-config.xml

# Delete job
curl -X POST http://localhost:8080/job/my-job/doDelete \
  --user admin:password
```

### Job with Parameters

```xml
<!-- Parameterized job configuration -->
<project>
  <properties>
    <hudson.model.ParametersDefinitionProperty>
      <parameterDefinitions>
        <hudson.model.StringParameterDefinition>
          <name>BRANCH</name>
          <description>Branch to build</description>
          <defaultValue>main</defaultValue>
          <trim>true</trim>
        </hudson.model.StringParameterDefinition>
        <hudson.model.ChoiceParameterDefinition>
          <name>ENVIRONMENT</name>
          <description>Target environment</description>
          <choices>
            <string>dev</string>
            <string>staging</string>
            <string>production</string>
          </choices>
        </hudson.model.ChoiceParameterDefinition>
        <hudson.model.BooleanParameterDefinition>
          <name>RUN_TESTS</name>
          <description>Run test suite</description>
          <defaultValue>true</defaultValue>
        </hudson.model.BooleanParameterDefinition>
      </parameterDefinitions>
    </hudson.model.ParametersDefinitionProperty>
  </properties>
  
  <builders>
    <hudson.tasks.Shell>
      <command>#!/bin/bash
echo "Building branch: ${BRANCH}"
echo "Target environment: ${ENVIRONMENT}"
if [ "${RUN_TESTS}" = "true" ]; then
  mvn test
fi
mvn package</command>
    </hudson.tasks.Shell>
  </builders>
</project>
```

### Trigger Parameterized Build

```bash
# Trigger with parameters
curl -X POST "http://localhost:8080/job/my-job/buildWithParameters" \
  --user admin:password \
  --data "BRANCH=develop&ENVIRONMENT=staging&RUN_TESTS=true"

# Trigger with JSON parameters
curl -X POST "http://localhost:8080/job/my-job/build" \
  --user admin:password \
  -H "Content-Type: application/json" \
  -d '{"parameter": [{"name": "BRANCH", "value": "develop"}]}'
```

### Build Shell Scripts

```bash
# build.sh - Common build script
#!/bin/bash
set -e  # Exit on error

echo "====== Build Information ======"
echo "Build Number: ${BUILD_NUMBER}"
echo "Workspace: ${WORKSPACE}"
echo "Git Branch: ${GIT_BRANCH}"
echo "Git Commit: ${GIT_COMMIT}"
echo "==============================="

# Install dependencies
echo "Installing dependencies..."
npm ci

# Run linting
echo "Running linter..."
npm run lint

# Run tests
echo "Running tests..."
npm test

# Build application
echo "Building application..."
npm run build

# Create artifact
echo "Creating artifact..."
tar -czf app-${BUILD_NUMBER}.tar.gz dist/

echo "Build completed successfully!"
```

### Post-Build Notification Script

```groovy
// Post-build script for email
def build = Thread.currentThread().executable
def result = build.result

if (result.toString() == "FAILURE") {
    def cause = build.getCauses()[0]
    def culprits = build.getCulprits()
    
    mail to: "team@example.com",
         subject: "Build Failed: ${build.fullDisplayName}",
         body: """
Build ${build.fullDisplayName} failed.

Cause: ${cause.shortDescription}
Changes by: ${culprits.join(', ')}

Console Output: ${build.absoluteUrl}console
"""
}
```

### Downstream Job Trigger

```xml
<!-- Trigger another job after successful build -->
<publishers>
  <hudson.plugins.parameterizedtrigger.BuildTrigger>
    <configs>
      <hudson.plugins.parameterizedtrigger.BuildTriggerConfig>
        <projects>deploy-to-staging</projects>
        <condition>SUCCESS</condition>
        <triggerWithNoParameters>false</triggerWithNoParameters>
        <configs>
          <hudson.plugins.parameterizedtrigger.CurrentBuildParameters/>
          <hudson.plugins.parameterizedtrigger.PredefinedBuildParameters>
            <properties>ARTIFACT_VERSION=${BUILD_NUMBER}</properties>
          </hudson.plugins.parameterizedtrigger.PredefinedBuildParameters>
        </configs>
      </hudson.plugins.parameterizedtrigger.BuildTriggerConfig>
    </configs>
  </hudson.plugins.parameterizedtrigger.BuildTrigger>
</publishers>
```

## Summary

- **Freestyle jobs** are configured via UI, good for simple tasks
- **Pipeline jobs** use Jenkinsfile, better for complex workflows and version control
- **Job configuration** includes SCM, triggers, build steps, and post-build actions
- **Build steps** execute commands (shell, batch, Maven, npm, etc.)
- **Post-build actions** archive artifacts, publish test results, send notifications
- **Build results** show status, console output, test reports, and artifacts

## Additional Resources

- [Jenkins Freestyle Projects](https://www.jenkins.io/doc/book/using/using-jobs/) - Official job documentation
- [Build Triggers](https://www.jenkins.io/doc/book/using/build-trigger/) - Trigger configuration guide
- [Post-build Actions](https://plugins.jenkins.io/) - Explore publisher plugins

