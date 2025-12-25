# Jenkins Artifacts

## Learning Objectives

- Understand what build artifacts are and why they matter
- Archive artifacts from builds using Jenkins
- Use artifact fingerprinting for traceability
- Download and manage artifacts via API
- Pass artifacts between pipeline stages
- Configure artifact retention policies

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Artifacts are the tangible outputs of your build process—JAR files, Docker images, test reports, deployable packages. Without proper artifact management, you can't reliably deploy, debug production issues, or audit what's running where.

As a quality engineer, artifacts include test reports, coverage data, and performance results. Accessing historical artifacts helps compare test results across builds and investigate regressions.

## The Concept

### What are Artifacts?

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Build Artifacts                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Artifacts are files produced by a build that need to be          │
│   preserved for deployment, testing, or archival purposes.          │
│                                                                      │
│   COMMON ARTIFACT TYPES                                             │
│   ─────────────────────                                             │
│   Application:    .jar, .war, .exe, .dll, Docker images            │
│   Test Reports:   JUnit XML, HTML reports, coverage reports        │
│   Documentation:  Javadoc, API docs, release notes                 │
│   Configuration:  Property files, manifests                        │
│   Logs:           Build logs, test logs, debug output              │
│                                                                      │
│   BUILD LIFECYCLE                                                    │
│   ───────────────                                                    │
│                                                                      │
│   ┌────────┐    ┌────────┐    ┌────────┐    ┌────────────────┐     │
│   │ Source │───▶│ Build  │───▶│  Test  │───▶│   Artifacts    │     │
│   │  Code  │    │        │    │        │    │   (Archived)   │     │
│   └────────┘    └────────┘    └────────┘    └────────────────┘     │
│                                                    │                 │
│                                                    ▼                 │
│                                           ┌────────────────┐        │
│                                           │   Deployment   │        │
│                                           │   (uses        │        │
│                                           │   artifacts)   │        │
│                                           └────────────────┘        │
│                                                                      │
│   WITHOUT ARTIFACTS                       WITH ARTIFACTS            │
│   ─────────────────                       ──────────────            │
│   "What version is deployed?"             "Build #45, app-1.0.jar" │
│   "Can't reproduce the issue"             "Downloaded that build"  │
│   "Build from scratch every time"         "Deploy existing build"  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Artifact Archiving in Jenkins

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Archiving Artifacts                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   FREESTYLE JOB CONFIGURATION                                       │
│   ───────────────────────────                                       │
│   Post-build Actions → Archive the artifacts                       │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │ Files to archive: [target/*.jar, target/*.war        ]      │   │
│   │                                                              │   │
│   │ [✓] Do not fail build if archiving returns nothing          │   │
│   │ [✓] Archive artifacts only if build is successful           │   │
│   │ [✓] Fingerprint all archived artifacts                       │   │
│   │                                                              │   │
│   │ Excludes: [target/*-sources.jar, target/*-javadoc.jar]     │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   GLOB PATTERNS                                                      │
│   ─────────────                                                      │
│   target/*.jar          All JAR files in target/                   │
│   **/*.xml              All XML files in any subdirectory          │
│   dist/**/*             Everything in dist/ recursively            │
│   **/test-reports/*.xml Test reports anywhere                      │
│                                                                      │
│   WHERE ARTIFACTS ARE STORED                                        │
│   ──────────────────────────                                        │
│   $JENKINS_HOME/jobs/<job>/builds/<number>/archive/                │
│                                                                      │
│   Example:                                                          │
│   /var/jenkins_home/jobs/my-app/builds/45/archive/target/app.jar  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Artifact Fingerprinting

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Artifact Fingerprinting                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Fingerprinting creates MD5 hash of artifacts to track usage      │
│                                                                      │
│   WHY FINGERPRINT?                                                   │
│   ────────────────                                                   │
│   • Track which build produced an artifact                         │
│   • Know where an artifact was used/deployed                       │
│   • Audit trail for compliance                                     │
│   • Debug: "What version is in production?"                        │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Artifact: app-1.0.jar                                      │   │
│   │  MD5: e4d909c290d0fb1ca068ffaddf22cbd0                      │   │
│   │                                                              │   │
│   │  USED IN BUILDS:                                             │   │
│   │  ─────────────────                                           │   │
│   │  my-app #45           (original)                            │   │
│   │  integration-tests #102                                      │   │
│   │  deploy-staging #78                                         │   │
│   │  deploy-production #34                                      │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   ENABLE FINGERPRINTING                                             │
│   ─────────────────────                                             │
│   1. Archive: Check "Fingerprint all archived artifacts"           │
│   2. Record fingerprints: Post-build action                        │
│   3. Copy artifacts: Enable "Fingerprint artifacts"                │
│                                                                      │
│   VIEW FINGERPRINTS                                                  │
│   ─────────────────                                                  │
│   Jenkins → Build → See Fingerprints                               │
│   Or: /fingerprint/<md5-hash>                                       │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Artifact Retention

```
┌─────────────────────────────────────────────────────────────────────┐
│                  Artifact Retention Policies                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Artifacts consume disk space - manage retention                   │
│                                                                      │
│   BUILD DISCARDER (Job Configuration)                               │
│   ───────────────────────────────────                               │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Strategy: Log Rotation                                      │   │
│   │                                                              │   │
│   │  Days to keep builds: [    ]  (blank = forever)             │   │
│   │  Max # of builds to keep: [10]                              │   │
│   │                                                              │   │
│   │  Days to keep artifacts: [7 ]                               │   │
│   │  Max # of builds with artifacts: [5]                        │   │
│   │                                                              │   │
│   │  Note: Artifacts can be deleted independently of builds     │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   COMMON STRATEGIES                                                  │
│   ─────────────────                                                  │
│   Development:  Keep last 5 builds with artifacts                  │
│   Staging:      Keep 7 days of artifacts                           │
│   Production:   Keep 30 days, or last 10 releases                  │
│   Compliance:   Keep forever (external archive)                    │
│                                                                      │
│   ARTIFACT SIZE MANAGEMENT                                          │
│   ────────────────────────                                          │
│   • Archive only necessary files                                   │
│   • Exclude intermediate files                                     │
│   • Use external artifact storage (Nexus, Artifactory, S3)        │
│   • Compress artifacts before archiving                            │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Pipeline Artifact Archiving

```groovy
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Archive') {
            steps {
                // Archive JAR files
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                
                // Archive with options
                archiveArtifacts(
                    artifacts: 'target/*.jar, target/*.war',
                    excludes: '*-sources.jar, *-javadoc.jar',
                    fingerprint: true,
                    onlyIfSuccessful: true,
                    allowEmptyArchive: false
                )
            }
        }
    }
}
```

### Archive Test Reports

```groovy
pipeline {
    agent any
    
    stages {
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    // Archive JUnit XML results
                    junit 'target/surefire-reports/*.xml'
                    
                    // Archive HTML report
                    archiveArtifacts artifacts: 'target/site/**/*', allowEmptyArchive: true
                    
                    // Archive coverage report
                    archiveArtifacts artifacts: 'target/jacoco/**/*', allowEmptyArchive: true
                }
            }
        }
    }
}
```

### Passing Artifacts Between Stages

```groovy
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
                // Stash artifacts for later stages
                stash name: 'app-artifacts', includes: 'target/*.jar'
            }
        }
        
        stage('Test on Linux') {
            agent { label 'linux' }
            steps {
                // Retrieve stashed artifacts
                unstash 'app-artifacts'
                sh 'java -jar target/app.jar --test'
            }
        }
        
        stage('Test on Windows') {
            agent { label 'windows' }
            steps {
                unstash 'app-artifacts'
                bat 'java -jar target\\app.jar --test'
            }
        }
        
        stage('Archive Final') {
            steps {
                unstash 'app-artifacts'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }
}
```

### Copy Artifacts from Another Job

```groovy
pipeline {
    agent any
    
    stages {
        stage('Get Upstream Artifacts') {
            steps {
                // Copy artifacts from build-job
                copyArtifacts(
                    projectName: 'build-job',
                    selector: lastSuccessful(),
                    filter: '**/*.jar',
                    target: 'upstream-artifacts/',
                    fingerprintArtifacts: true
                )
                
                // Copy specific build
                copyArtifacts(
                    projectName: 'build-job',
                    selector: specific("${params.BUILD_NUMBER}"),
                    filter: '**/*.jar'
                )
            }
        }
        
        stage('Deploy') {
            steps {
                sh 'ls -la upstream-artifacts/'
                sh './deploy.sh upstream-artifacts/*.jar'
            }
        }
    }
}
```

### Artifact Retention in Pipeline

```groovy
pipeline {
    agent any
    
    options {
        // Discard old builds
        buildDiscarder(logRotator(
            numToKeepStr: '10',           // Keep last 10 builds
            artifactNumToKeepStr: '5',    // Keep artifacts for last 5
            daysToKeepStr: '30',          // Keep builds for 30 days
            artifactDaysToKeepStr: '7'    // Keep artifacts for 7 days
        ))
    }
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }
}
```

### Download Artifacts via API

```bash
# Download latest successful build artifacts
curl -O http://jenkins:8080/job/my-job/lastSuccessfulBuild/artifact/target/app.jar \
  --user admin:password

# Download from specific build
curl -O http://jenkins:8080/job/my-job/45/artifact/target/app.jar \
  --user admin:password

# List artifacts
curl http://jenkins:8080/job/my-job/lastSuccessfulBuild/api/json?tree=artifacts[relativePath] \
  --user admin:password

# Download all artifacts as zip
curl -O http://jenkins:8080/job/my-job/lastSuccessfulBuild/artifact/*zip*/archive.zip \
  --user admin:password
```

### External Artifact Storage (S3)

```groovy
pipeline {
    agent any
    
    environment {
        S3_BUCKET = 'my-artifacts-bucket'
        ARTIFACT_NAME = "app-${BUILD_NUMBER}.jar"
    }
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Upload to S3') {
            steps {
                withAWS(credentials: 'aws-credentials', region: 'us-east-1') {
                    s3Upload(
                        file: 'target/app.jar',
                        bucket: env.S3_BUCKET,
                        path: "releases/${ARTIFACT_NAME}"
                    )
                }
            }
        }
        
        stage('Download from S3') {
            steps {
                withAWS(credentials: 'aws-credentials', region: 'us-east-1') {
                    s3Download(
                        file: 'downloaded-app.jar',
                        bucket: env.S3_BUCKET,
                        path: "releases/${ARTIFACT_NAME}"
                    )
                }
            }
        }
    }
}
```

### Publish to Nexus/Artifactory

```groovy
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Publish to Nexus') {
            steps {
                nexusArtifactUploader(
                    nexusVersion: 'nexus3',
                    protocol: 'https',
                    nexusUrl: 'nexus.example.com',
                    groupId: 'com.example',
                    version: "${BUILD_NUMBER}",
                    repository: 'maven-releases',
                    credentialsId: 'nexus-credentials',
                    artifacts: [
                        [
                            artifactId: 'my-app',
                            classifier: '',
                            file: 'target/my-app.jar',
                            type: 'jar'
                        ]
                    ]
                )
            }
        }
    }
}
```

### Docker Image as Artifact

```groovy
pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'docker.io/myorg'
        IMAGE_NAME = 'my-app'
        VERSION = "${BUILD_NUMBER}"
    }
    
    stages {
        stage('Build Image') {
            steps {
                sh "docker build -t ${DOCKER_REGISTRY}/${IMAGE_NAME}:${VERSION} ."
            }
        }
        
        stage('Push Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:${VERSION}
                        docker tag ${DOCKER_REGISTRY}/${IMAGE_NAME}:${VERSION} \
                                   ${DOCKER_REGISTRY}/${IMAGE_NAME}:latest
                        docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:latest
                    '''
                }
            }
        }
        
        stage('Record Image') {
            steps {
                // Save image reference as artifact
                writeFile file: 'image-info.txt', text: """
                    Image: ${DOCKER_REGISTRY}/${IMAGE_NAME}:${VERSION}
                    Build: ${BUILD_NUMBER}
                    Commit: ${GIT_COMMIT}
                """
                archiveArtifacts artifacts: 'image-info.txt', fingerprint: true
            }
        }
    }
}
```

## Summary

- **Artifacts** are build outputs (JARs, reports, images) preserved for deployment and audit
- **Archive artifacts** using `archiveArtifacts` step with glob patterns
- **Fingerprinting** tracks artifact usage across builds for traceability
- **stash/unstash** passes artifacts between pipeline stages
- **copyArtifacts** retrieves artifacts from other jobs
- **Retention policies** manage disk space (days to keep, number to keep)
- **External storage** (S3, Nexus, Artifactory) recommended for production

## Additional Resources

- [Archiving Artifacts](https://www.jenkins.io/doc/pipeline/tour/tests-and-artifacts/) - Official tutorial
- [Copy Artifact Plugin](https://plugins.jenkins.io/copyartifact/) - Cross-job artifact sharing
- [Artifact Manager on S3](https://plugins.jenkins.io/artifact-manager-s3/) - Cloud artifact storage

