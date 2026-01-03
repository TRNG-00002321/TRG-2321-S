# DevOps & CI/CD Interview Questions & Answers

## Beginner Level

**Q1: What is DevOps and why is it important?**

> **Answer:** DevOps is a culture and practice that combines software development (Dev) and IT operations (Ops) to shorten the development lifecycle while delivering high-quality software.
>
> **Key principles:**
> - **Collaboration:** Break down silos between teams
> - **Automation:** Automate repetitive tasks
> - **Continuous improvement:** Iterate and learn
> - **Shared responsibility:** Everyone owns quality
>
> **Benefits:**
> - Faster time to market
> - Improved deployment frequency
> - Lower failure rate
> - Faster recovery from failures
> - Better collaboration
>
> **DevOps lifecycle:**
> ```
> Plan → Code → Build → Test → Deploy → Operate → Monitor → (feedback to Plan)
> ```

---

**Q2: Explain CI/CD and its stages.**

> **Answer:**
>
> **Continuous Integration (CI):**
> - Developers merge code frequently (daily or more)
> - Automated build and test on each merge
> - Immediate feedback on failures
>
> **Continuous Delivery (CD):**
> - Code always in deployable state
> - Automated deployment to staging
> - Manual approval for production
>
> **Continuous Deployment:**
> - Fully automated deployment to production
> - No manual gates (requires mature testing)
>
> **CI/CD Pipeline:**
> ```
> Commit → Build → Unit Tests → Integration Tests → Deploy Staging → E2E Tests → Deploy Prod
> ```
>
> **Example:**
> ```yaml
> # GitHub Actions example
> on: push
> jobs:
>   build:
>     runs-on: ubuntu-latest
>     steps:
>       - uses: actions/checkout@v3
>       - run: npm install
>       - run: npm test
>       - run: npm run build
>       - run: ./deploy.sh staging
> ```

---

**Q3: What is Docker and why use containers?**

> **Answer:** Docker is a platform for developing, shipping, and running applications in containers.
>
> **Container vs VM:**
>
> | Aspect | Container | Virtual Machine |
> |--------|-----------|-----------------|
> | Size | MBs | GBs |
> | Startup | Seconds | Minutes |
> | Isolation | Process level | Full OS |
> | Resource usage | Light | Heavy |
>
> **Why containers:**
> - **Consistency:** "Works on my machine" → "Works everywhere"
> - **Isolation:** Apps don't conflict
> - **Portability:** Run on any host with Docker
> - **Efficiency:** Share OS kernel, less overhead
> - **Scalability:** Easy to replicate
>
> **Basic concepts:**
> ```bash
> # Image: Blueprint (read-only)
> docker build -t myapp:1.0 .
> 
> # Container: Running instance
> docker run -p 8080:80 myapp:1.0
> 
> # Dockerfile example
> FROM node:18
> WORKDIR /app
> COPY package*.json ./
> RUN npm install
> COPY . .
> CMD ["npm", "start"]
> ```

---

**Q4: What is Jenkins and how does it work?**

> **Answer:** Jenkins is an open-source automation server for building, testing, and deploying software.
>
> **Architecture:**
> ```
> Jenkins Controller (Master)
> ├── Manages configuration
> ├── Schedules jobs
> └── Distributes work
> 
> Jenkins Agents (Nodes)
> ├── Execute build jobs
> └── Can be different environments
> ```
>
> **Key concepts:**
> - **Job/Project:** A runnable task
> - **Build:** Single execution of a job
> - **Pipeline:** Job defined as code (Jenkinsfile)
> - **Plugin:** Extends functionality
>
> **Jenkinsfile example:**
> ```groovy
> pipeline {
>     agent any
>     stages {
>         stage('Build') {
>             steps {
>                 sh 'mvn compile'
>             }
>         }
>         stage('Test') {
>             steps {
>                 sh 'mvn test'
>             }
>         }
>         stage('Deploy') {
>             steps {
>                 sh './deploy.sh'
>             }
>         }
>     }
>     post {
>         failure {
>             mail to: 'team@example.com',
>                  subject: 'Build Failed'
>         }
>     }
> }
> ```

---

**Q5: What are the core AWS services for infrastructure?**

> **Answer:**
>
> | Service | Type | Purpose |
> |---------|------|---------|
> | **EC2** | Compute | Virtual servers |
> | **S3** | Storage | Object storage |
> | **RDS** | Database | Managed relational DB |
> | **VPC** | Network | Virtual network |
> | **IAM** | Security | Identity management |
> | **CloudWatch** | Monitoring | Logs and metrics |
>
> **EC2 example:**
> ```bash
> # Launch instance
> aws ec2 run-instances \
>     --image-id ami-12345678 \
>     --instance-type t2.micro \
>     --key-name my-key
> 
> # Connect
> ssh -i key.pem ec2-user@public-ip
> ```
>
> **S3 example:**
> ```bash
> # Upload file
> aws s3 cp file.txt s3://my-bucket/
> 
> # Sync directory
> aws s3 sync ./build s3://my-bucket/
> ```

---

## Intermediate Level

**Q6: Explain Docker Compose and its use cases.**

> **Answer:** Docker Compose defines and runs multi-container applications.
>
> ```yaml
> # docker-compose.yml
> version: '3.8'
> 
> services:
>   web:
>     build: ./app
>     ports:
>       - "8080:80"
>     environment:
>       - DATABASE_URL=postgres://db:5432/mydb
>     depends_on:
>       - db
> 
>   db:
>     image: postgres:13
>     volumes:
>       - db_data:/var/lib/postgresql/data
>     environment:
>       - POSTGRES_PASSWORD=secret
> 
>   redis:
>     image: redis:6
> 
> volumes:
>   db_data:
> ```
>
> **Commands:**
> ```bash
> docker-compose up -d      # Start in background
> docker-compose down       # Stop and remove
> docker-compose logs -f    # Follow logs
> docker-compose ps         # List services
> docker-compose exec web bash  # Shell into container
> ```
>
> **Use cases:**
> - Local development environments
> - Integration testing
> - Demo environments
> - Single-host deployments

---

**Q7: How do you handle secrets in CI/CD pipelines?**

> **Answer:** Never hardcode secrets in code or configuration files.
>
> **Jenkins Credentials:**
> ```groovy
> pipeline {
>     environment {
>         DB_CREDS = credentials('database-credentials')
>         // Creates: DB_CREDS, DB_CREDS_USR, DB_CREDS_PSW
>     }
>     stages {
>         stage('Deploy') {
>             steps {
>                 sh 'deploy.sh -u $DB_CREDS_USR -p $DB_CREDS_PSW'
>             }
>         }
>     }
> }
> ```
>
> **GitHub Actions Secrets:**
> ```yaml
> steps:
>   - name: Deploy
>     env:
>       API_KEY: ${{ secrets.API_KEY }}
>     run: ./deploy.sh
> ```
>
> **AWS Secrets Manager:**
> ```python
> import boto3
> 
> client = boto3.client('secretsmanager')
> secret = client.get_secret_value(SecretId='my-secret')
> ```
>
> **Best practices:**
> - Use secret management tools (Vault, AWS SM)
> - Rotate secrets regularly
> - Limit secret access (least privilege)
> - Don't log secrets
> - Use environment variables, not files

---

**Q8: Explain Infrastructure as Code (IaC).**

> **Answer:** IaC manages infrastructure through code/configuration files rather than manual processes.
>
> **Benefits:**
> - Version controlled
> - Repeatable and consistent
> - Documentation as code
> - Easy to audit changes
>
> **Tools:**
>
> | Tool | Type | Cloud |
> |------|------|-------|
> | Terraform | Provisioning | Multi-cloud |
> | CloudFormation | Provisioning | AWS only |
> | Ansible | Configuration | Any |
> | Pulumi | Provisioning | Multi-cloud |
>
> **Terraform example:**
> ```hcl
> # main.tf
> provider "aws" {
>   region = "us-east-1"
> }
> 
> resource "aws_instance" "web" {
>   ami           = "ami-12345678"
>   instance_type = "t2.micro"
>   
>   tags = {
>     Name = "web-server"
>   }
> }
> 
> resource "aws_s3_bucket" "logs" {
>   bucket = "my-app-logs"
> }
> ```
>
> **Workflow:**
> ```bash
> terraform init      # Initialize
> terraform plan      # Preview changes
> terraform apply     # Apply changes
> terraform destroy   # Remove resources
> ```

---

**Q9: What is a build artifact and how are they managed?**

> **Answer:** Build artifacts are files produced by the build process (JARs, WARs, Docker images, etc.).
>
> **Artifact repositories:**
>
> | Repository | Type |
> |------------|------|
> | JFrog Artifactory | Universal |
> | Nexus | Universal |
> | Docker Hub | Container images |
> | AWS ECR | Container images |
> | npm registry | JavaScript |
> | PyPI | Python |
>
> **Jenkins artifact archiving:**
> ```groovy
> post {
>     success {
>         archiveArtifacts artifacts: 'target/*.jar'
>     }
> }
> ```
>
> **Docker image workflow:**
> ```bash
> # Build
> docker build -t myapp:${BUILD_NUMBER} .
> 
> # Tag for registry
> docker tag myapp:${BUILD_NUMBER} registry.com/myapp:${BUILD_NUMBER}
> docker tag myapp:${BUILD_NUMBER} registry.com/myapp:latest
> 
> # Push
> docker push registry.com/myapp:${BUILD_NUMBER}
> docker push registry.com/myapp:latest
> ```
>
> **Best practices:**
> - Version all artifacts
> - Clean up old versions
> - Use immutable tags (version, not just latest)
> - Scan for vulnerabilities

---

**Q10: How do you monitor applications in production?**

> **Answer:**
>
> **Three pillars of observability:**
>
> 1. **Metrics:** Quantitative measurements
> ```
> CPU usage, memory, request rate, error rate
> → Tools: Prometheus, CloudWatch, Datadog
> ```
>
> 2. **Logs:** Event records
> ```
> Application events, errors, audit trail
> → Tools: ELK Stack, Splunk, CloudWatch Logs
> ```
>
> 3. **Traces:** Request flow across services
> ```
> End-to-end request path in microservices
> → Tools: Jaeger, Zipkin, X-Ray
> ```
>
> **Key metrics to monitor:**
> - **RED:** Rate, Errors, Duration
> - **USE:** Utilization, Saturation, Errors
>
> **Alerting example:**
> ```yaml
> # Prometheus alert rule
> groups:
>   - name: app-alerts
>     rules:
>       - alert: HighErrorRate
>         expr: rate(http_errors_total[5m]) > 0.1
>         for: 5m
>         labels:
>           severity: critical
>         annotations:
>           summary: High error rate detected
> ```

---

## Advanced Level

**Q11: Explain blue-green and canary deployments.**

> **Answer:** These are deployment strategies to minimize downtime and risk.
>
> **Blue-Green Deployment:**
> - Maintain two identical environments
> - Blue = current production
> - Green = new version
> - Switch traffic when ready
>
> ```
> Before:  Users → Load Balancer → Blue (v1)
>                                   Green (v2) [idle]
> 
> Switch:  Users → Load Balancer → Blue (v1) [idle]
>                                   Green (v2)
> 
> Rollback: Just switch back to Blue
> ```
>
> **Canary Deployment:**
> - Release to small percentage first
> - Gradually increase if successful
> - Monitor for issues
>
> ```
> Phase 1:  Users → LB → 95% v1, 5% v2 (canary)
> Phase 2:  Users → LB → 75% v1, 25% v2
> Phase 3:  Users → LB → 50% v1, 50% v2
> Phase 4:  Users → LB → 0% v1, 100% v2
> 
> If issues: Roll back canary immediately
> ```
>
> **Comparison:**
>
> | Aspect | Blue-Green | Canary |
> |--------|------------|--------|
> | Resource | 2x infrastructure | Minimal extra |
> | Risk | All or nothing switch | Gradual exposure |
> | Rollback | Instant | Instant |
> | Complexity | Simpler | More complex |

---

**Q12: How do you implement a complete CI/CD pipeline?**

> **Answer:**
>
> ```groovy
> // Jenkinsfile - Complete pipeline
> pipeline {
>     agent any
>     
>     environment {
>         DOCKER_REGISTRY = 'registry.example.com'
>         IMAGE_NAME = 'myapp'
>     }
>     
>     stages {
>         stage('Checkout') {
>             steps {
>                 checkout scm
>             }
>         }
>         
>         stage('Build') {
>             steps {
>                 sh 'mvn clean package -DskipTests'
>             }
>         }
>         
>         stage('Unit Tests') {
>             steps {
>                 sh 'mvn test'
>             }
>             post {
>                 always {
>                     junit '**/target/surefire-reports/*.xml'
>                 }
>             }
>         }
>         
>         stage('Code Quality') {
>             steps {
>                 sh 'mvn sonar:sonar'
>             }
>         }
>         
>         stage('Build Image') {
>             steps {
>                 sh "docker build -t ${DOCKER_REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER} ."
>             }
>         }
>         
>         stage('Push Image') {
>             steps {
>                 withCredentials([usernamePassword(credentialsId: 'docker-creds', 
>                     usernameVariable: 'USER', passwordVariable: 'PASS')]) {
>                     sh "docker login -u $USER -p $PASS ${DOCKER_REGISTRY}"
>                     sh "docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}"
>                 }
>             }
>         }
>         
>         stage('Deploy Staging') {
>             steps {
>                 sh "./deploy.sh staging ${BUILD_NUMBER}"
>             }
>         }
>         
>         stage('Integration Tests') {
>             steps {
>                 sh "mvn verify -Pintegration-tests -Denv=staging"
>             }
>         }
>         
>         stage('Deploy Production') {
>             when {
>                 branch 'main'
>             }
>             input {
>                 message "Deploy to production?"
>             }
>             steps {
>                 sh "./deploy.sh production ${BUILD_NUMBER}"
>             }
>         }
>     }
>     
>     post {
>         success {
>             slackSend color: 'good', message: "Build ${BUILD_NUMBER} succeeded"
>         }
>         failure {
>             slackSend color: 'danger', message: "Build ${BUILD_NUMBER} failed"
>         }
>     }
> }
> ```
