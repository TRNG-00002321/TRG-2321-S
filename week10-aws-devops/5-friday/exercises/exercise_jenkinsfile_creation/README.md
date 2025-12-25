# Exercise 3: Jenkinsfile Creation

## Objective

Create Jenkins Pipeline jobs using declarative Jenkinsfile syntax, implementing multiple stages, environment variables, and post actions.

---

## Learning Outcomes

By completing this exercise, you will:
- Write declarative Jenkinsfile syntax
- Create pipeline jobs with multiple stages
- Use environment variables in pipelines
- Implement conditional execution with `when`
- Handle post-build actions (success/failure)
- Use the Pipeline Syntax generator

---

## Prerequisites

- Completed Exercise 2 (Freestyle jobs)
- Jenkins running at http://localhost:8080
- Understanding of basic build concepts

---

## Time Estimate

45 minutes

---

## Pipeline Fundamentals

### Declarative vs Scripted
- **Declarative:** Structured, easier to read, most common
- **Scripted:** More flexible, full Groovy power

This exercise focuses on **Declarative Pipeline**.

### Basic Structure
```groovy
pipeline {
    agent any
    stages {
        stage('Stage Name') {
            steps {
                // Commands here
            }
        }
    }
}
```

---

## Tasks

### Task 1: Simple Pipeline Job (10 minutes)

1. **Create Pipeline Job**
   - New Item → Name: `first-pipeline`
   - Type: `Pipeline` → OK

2. **Configure Pipeline**
   - Scroll to "Pipeline" section
   - Definition: `Pipeline script`
   - Enter this script:
   
   ```groovy
   pipeline {
       agent any
       
       stages {
           stage('Hello') {
               steps {
                   echo 'Hello from Pipeline!'
                   echo "Build Number: ${BUILD_NUMBER}"
               }
           }
           
           stage('Build') {
               steps {
                   echo 'Building the application...'
                   sh 'echo "Compiling code..."'
                   sh 'sleep 2'
                   sh 'echo "Build complete!"'
               }
           }
           
           stage('Test') {
               steps {
                   echo 'Running tests...'
                   sh '''
                       echo "Test 1: PASS"
                       echo "Test 2: PASS"
                       echo "Test 3: PASS"
                       echo "All tests passed!"
                   '''
               }
           }
           
           stage('Deploy') {
               steps {
                   echo 'Deploying application...'
                   sh 'echo "Deployment complete!"'
               }
           }
       }
   }
   ```

3. **Save and Build**
   - Save
   - Build Now
   - Watch Stage View visualization

4. **Explore Results**
   - Click on build number
   - View "Pipeline Steps" 
   - Click each stage to see logs
   - Try "Blue Ocean" view for better visualization

**Checkpoint:** Basic pipeline works ✓

---

### Task 2: Pipeline with Environment Variables (10 minutes)

1. **Create New Pipeline Job**
   - New Item → Name: `env-pipeline`
   - Type: Pipeline → OK

2. **Pipeline Script**
   ```groovy
   pipeline {
       agent any
       
       environment {
           // Global environment variables
           APP_NAME = 'MyApplication'
           APP_VERSION = '1.0.0'
           DEPLOY_ENV = 'development'
       }
       
       stages {
           stage('Setup') {
               environment {
                   // Stage-specific variable
                   STAGE_VAR = 'setup-specific'
               }
               steps {
                   echo "Application: ${APP_NAME}"
                   echo "Version: ${APP_VERSION}"
                   echo "Environment: ${DEPLOY_ENV}"
                   echo "Stage Variable: ${STAGE_VAR}"
                   
                   sh '''
                       echo "Shell access to env vars:"
                       echo "APP_NAME=$APP_NAME"
                       echo "BUILD_NUMBER=$BUILD_NUMBER"
                   '''
               }
           }
           
           stage('Build Info') {
               steps {
                   echo "Building ${APP_NAME} version ${APP_VERSION}"
                   
                   script {
                       // Dynamic variable assignment
                       def buildTime = new Date().format('yyyy-MM-dd HH:mm:ss')
                       echo "Build Time: ${buildTime}"
                   }
               }
           }
           
           stage('Show All Variables') {
               steps {
                   sh 'env | sort'
               }
           }
       }
       
       post {
           always {
               echo "Pipeline completed for ${APP_NAME}"
           }
       }
   }
   ```

3. **Build and Review**
   - Build Now
   - Check environment variables in output

**Checkpoint:** Environment variables working ✓

---

### Task 3: Pipeline with Conditional Stages (15 minutes)

1. **Create Conditional Pipeline**
   - New Item → Name: `conditional-pipeline`
   - Type: Pipeline → OK

2. **Pipeline Script**
   ```groovy
   pipeline {
       agent any
       
       parameters {
           choice(name: 'ENVIRONMENT', choices: ['dev', 'staging', 'prod'], description: 'Deploy environment')
           booleanParam(name: 'RUN_TESTS', defaultValue: true, description: 'Run test suite')
           booleanParam(name: 'DEPLOY', defaultValue: false, description: 'Deploy after build')
       }
       
       environment {
           APP_NAME = 'ConditionalApp'
       }
       
       stages {
           stage('Build') {
               steps {
                   echo "Building ${APP_NAME}..."
                   sh 'echo "Build successful!"'
               }
           }
           
           stage('Unit Tests') {
               when {
                   expression { params.RUN_TESTS == true }
               }
               steps {
                   echo 'Running unit tests...'
                   sh '''
                       echo "Test Suite: Unit Tests"
                       echo "  ✓ Test 1 passed"
                       echo "  ✓ Test 2 passed"
                       echo "  ✓ Test 3 passed"
                   '''
               }
           }
           
           stage('Integration Tests') {
               when {
                   allOf {
                       expression { params.RUN_TESTS == true }
                       expression { params.ENVIRONMENT != 'dev' }
                   }
               }
               steps {
                   echo 'Running integration tests...'
                   sh 'echo "Integration tests passed!"'
               }
           }
           
           stage('Deploy to Dev') {
               when {
                   expression { params.ENVIRONMENT == 'dev' && params.DEPLOY }
               }
               steps {
                   echo 'Deploying to Development...'
                   sh 'echo "Dev deployment complete!"'
               }
           }
           
           stage('Deploy to Staging') {
               when {
                   expression { params.ENVIRONMENT == 'staging' && params.DEPLOY }
               }
               steps {
                   echo 'Deploying to Staging...'
                   sh 'echo "Staging deployment complete!"'
               }
           }
           
           stage('Deploy to Production') {
               when {
                   allOf {
                       expression { params.ENVIRONMENT == 'prod' }
                       expression { params.DEPLOY == true }
                   }
               }
               steps {
                   echo '⚠️  PRODUCTION DEPLOYMENT'
                   
                   // In real scenario, add approval here
                   // input message: 'Deploy to Production?', ok: 'Deploy'
                   
                   sh '''
                       echo "Deploying to Production..."
                       echo "Production deployment complete!"
                   '''
               }
           }
       }
       
       post {
           success {
               echo "✅ Pipeline succeeded for ${params.ENVIRONMENT}"
           }
           failure {
               echo "❌ Pipeline failed!"
           }
           always {
               echo "Pipeline finished. Environment: ${params.ENVIRONMENT}"
           }
       }
   }
   ```

3. **Test Different Scenarios**
   - Build with Parameters
   - Try different combinations:
     - ENVIRONMENT=dev, RUN_TESTS=true, DEPLOY=false
     - ENVIRONMENT=staging, RUN_TESTS=true, DEPLOY=true
     - ENVIRONMENT=prod, RUN_TESTS=false, DEPLOY=true

4. **Observe Stage Skipping**
   - Note which stages are skipped based on conditions

**Checkpoint:** Conditional execution working ✓

---

### Task 4: Pipeline with Docker (10 minutes)

1. **Create Docker Pipeline**
   - New Item → Name: `docker-pipeline`
   - Type: Pipeline → OK

2. **Pipeline Script**
   ```groovy
   pipeline {
       agent any
       
       environment {
           DOCKER_IMAGE = 'jenkins-demo-app'
           DOCKER_TAG = "${BUILD_NUMBER}"
       }
       
       stages {
           stage('Create App') {
               steps {
                   echo 'Creating application files...'
                   
                   sh '''
                       mkdir -p app
                       
                       cat > app/app.py << 'PYEOF'
   from flask import Flask, jsonify
   import os
   
   app = Flask(__name__)
   
   @app.route('/')
   def hello():
       return jsonify({
           'message': 'Hello from Jenkins Pipeline!',
           'version': os.environ.get('APP_VERSION', 'unknown')
       })
   
   @app.route('/health')
   def health():
       return jsonify({'status': 'healthy'})
   
   if __name__ == '__main__':
       app.run(host='0.0.0.0', port=5000)
   PYEOF
                       
                       cat > app/requirements.txt << 'REQEOF'
   flask==3.0.0
   REQEOF
                       
                       cat > app/Dockerfile << 'DOCKEOF'
   FROM python:3.11-slim
   WORKDIR /app
   COPY requirements.txt .
   RUN pip install --no-cache-dir -r requirements.txt
   COPY app.py .
   ENV APP_VERSION=1.0.0
   EXPOSE 5000
   CMD ["python", "app.py"]
   DOCKEOF
                   '''
               }
           }
           
           stage('Build Docker Image') {
               steps {
                   echo "Building Docker image: ${DOCKER_IMAGE}:${DOCKER_TAG}"
                   
                   sh '''
                       cd app
                       docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                       docker images | grep ${DOCKER_IMAGE}
                   '''
               }
           }
           
           stage('Test Container') {
               steps {
                   echo 'Testing container...'
                   
                   sh '''
                       # Run container
                       docker run -d --name test-container -p 5001:5000 ${DOCKER_IMAGE}:${DOCKER_TAG}
                       
                       # Wait for startup
                       sleep 5
                       
                       # Test endpoints
                       curl -f http://localhost:5001/ || exit 1
                       curl -f http://localhost:5001/health || exit 1
                       
                       echo "Container tests passed!"
                   '''
               }
           }
           
           stage('Cleanup Test') {
               steps {
                   sh '''
                       docker stop test-container || true
                       docker rm test-container || true
                   '''
               }
           }
       }
       
       post {
           success {
               echo "✅ Docker image ${DOCKER_IMAGE}:${DOCKER_TAG} built and tested successfully!"
           }
           failure {
               echo "❌ Pipeline failed!"
               sh '''
                   docker stop test-container || true
                   docker rm test-container || true
               '''
           }
           cleanup {
               echo "Cleaning up workspace..."
               sh 'rm -rf app'
           }
       }
   }
   ```

3. **Build and Monitor**
   - Build Now
   - Watch each stage
   - Verify Docker image created

**Checkpoint:** Docker pipeline working ✓

---

## Verification Checklist

- [ ] Simple pipeline with multiple stages works
- [ ] Environment variables accessible in pipeline
- [ ] Conditional stages skip appropriately
- [ ] Docker build pipeline succeeds
- [ ] Post actions execute correctly
- [ ] Can view pipeline in Blue Ocean

---

## Deliverables

1. Screenshot of Pipeline Stage View
2. Screenshot of Blue Ocean visualization
3. Your docker-pipeline Jenkinsfile
4. Console output showing Docker image build

---

## Jenkinsfile Quick Reference

```groovy
pipeline {
    agent any | none | { docker 'image' } | { label 'label' }
    
    environment {
        VAR = 'value'
    }
    
    parameters {
        string(name: 'NAME', defaultValue: 'value')
        booleanParam(name: 'FLAG', defaultValue: true)
        choice(name: 'OPT', choices: ['a', 'b', 'c'])
    }
    
    stages {
        stage('Name') {
            when {
                expression { condition }
                branch 'main'
                environment name: 'VAR', value: 'value'
            }
            steps {
                echo 'message'
                sh 'command'
                script { groovy code }
            }
        }
    }
    
    post {
        always { }
        success { }
        failure { }
        unstable { }
        cleanup { }
    }
}
```

---

## Clean-Up

Keep pipeline jobs for reference. Docker images created can be removed:
```bash
docker images | grep jenkins-demo
docker rmi jenkins-demo-app:<tag>
```

---

## Additional Resources

- [Pipeline Syntax](https://www.jenkins.io/doc/book/pipeline/syntax/)
- [Pipeline Steps Reference](https://www.jenkins.io/doc/pipeline/steps/)
- [Blue Ocean Documentation](https://www.jenkins.io/doc/book/blueocean/)

