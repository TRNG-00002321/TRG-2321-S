# Weekly Knowledge Check: Week 10 - AWS, DevOps

This quiz covers AWS fundamentals, Docker containerization, DevOps philosophy & monitoring, and Jenkins CI/CD.

---

## Part 1: Multiple Choice - AWS Fundamentals

### 1. Which cloud service model requires you to manage the operating system, middleware, and runtime, while the provider manages servers, storage, and networking?
- [ ] A) SaaS (Software as a Service)
- [ ] B) PaaS (Platform as a Service)
- [ ] C) IaaS (Infrastructure as a Service)
- [ ] D) FaaS (Function as a Service)

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) IaaS (Infrastructure as a Service)

**Explanation:** In IaaS, you rent raw computing resources and manage everything from the OS up. AWS EC2 is a prime example—you get virtual servers but must configure the operating system, middleware, runtime, and applications.
- **Why others are wrong:**
  - A) SaaS: The provider manages everything; you just use the software (e.g., Gmail, Salesforce).
  - B) PaaS: The provider manages OS, middleware, and runtime; you only manage applications and data (e.g., AWS Elastic Beanstalk).
  - D) FaaS: A serverless model where you only provide function code (e.g., AWS Lambda).
</details>

---

### 2. What is the default behavior of AWS Security Groups for inbound traffic?
- [ ] A) Allow all traffic
- [ ] B) Allow only HTTP and HTTPS
- [ ] C) Deny all traffic unless explicitly allowed
- [ ] D) Allow traffic from the same VPC only

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Deny all traffic unless explicitly allowed

**Explanation:** Security groups follow a "default deny" principle for inbound traffic. All inbound traffic is blocked unless you create explicit allow rules. This is a fundamental security best practice.
- **Why others are wrong:**
  - A) Allowing all traffic by default would be a security vulnerability.
  - B) No protocols are allowed by default; you must configure each port.
  - D) VPC membership doesn't automatically grant access through security groups.
</details>

---

### 3. Which AWS feature provides high availability by maintaining a synchronous standby replica in a different Availability Zone?
- [ ] A) Read Replicas
- [ ] B) Multi-AZ Deployment
- [ ] C) Cross-Region Replication
- [ ] D) Auto Scaling

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Multi-AZ Deployment

**Explanation:** Multi-AZ deployments maintain a synchronous standby replica in a different Availability Zone. On primary failure, RDS automatically fails over to the standby (typically 60-120 seconds). The same endpoint works before and after failover.
- **Why others are wrong:**
  - A) Read Replicas use asynchronous replication for scaling reads, not high availability.
  - C) Cross-Region Replication is for distributing data across regions, not for automatic failover.
  - D) Auto Scaling adjusts the number of EC2 instances based on demand, not database availability.
</details>

---

### 4. In CIDR notation, what does `/32` represent?
- [ ] A) All IP addresses (0.0.0.0/0)
- [ ] B) A /24 subnet (256 IPs)
- [ ] C) A single IP address
- [ ] D) A /16 network (65,536 IPs)

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) A single IP address

**Explanation:** In CIDR notation, `/32` means all 32 bits are fixed, leaving no variable bits. This represents exactly one IP address. It's commonly used to restrict access to a specific workstation (e.g., `203.0.113.50/32` allows only that exact IP).
- **Why others are wrong:**
  - A) All IP addresses is represented by `/0` (0.0.0.0/0).
  - B) A /24 subnet has 256 IPs (last 8 bits variable).
  - D) A /16 network has 65,536 IPs (last 16 bits variable).
</details>

---

### 5. Which S3 storage class is best for data accessed very rarely with retrieval times of 12+ hours?
- [ ] A) S3 Standard
- [ ] B) S3 Standard-IA
- [ ] C) S3 Glacier Flexible Retrieval
- [ ] D) S3 Glacier Deep Archive

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** D) S3 Glacier Deep Archive

**Explanation:** S3 Glacier Deep Archive is the lowest-cost storage class designed for long-term archive with retrieval times of 12+ hours. It's ideal for compliance data or archives rarely (if ever) accessed.
- **Why others are wrong:**
  - A) S3 Standard is for frequently accessed data with immediate access.
  - B) S3 Standard-IA is for infrequently accessed data with immediate retrieval.
  - C) S3 Glacier Flexible Retrieval has retrieval times of minutes to hours, not 12+ hours.
</details>

---

### 6. What is the key characteristic that makes Security Groups "stateful"?
- [ ] A) They can create both allow and deny rules
- [ ] B) Return traffic is automatically allowed
- [ ] C) Rules are processed in numeric order
- [ ] D) They operate at the subnet level

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Return traffic is automatically allowed

**Explanation:** Security groups are stateful, meaning if you allow inbound traffic on a port, the response traffic is automatically allowed without needing an explicit outbound rule. This simplifies rule configuration significantly.
- **Why others are wrong:**
  - A) Security groups only have allow rules (not deny rules). NACLs have both.
  - C) All security group rules are evaluated together, not in order. This is a NACL characteristic.
  - D) Security groups operate at the instance level, not subnet level. NACLs operate at the subnet level.
</details>

---

### 7. Which EC2 Auto Scaling policy type is considered the simplest and most commonly used?
- [ ] A) Step Scaling
- [ ] B) Scheduled Scaling
- [ ] C) Target Tracking Scaling
- [ ] D) Predictive Scaling

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Target Tracking Scaling

**Explanation:** Target Tracking is the simplest approach—you specify a target metric value (e.g., keep CPU at 50%), and Auto Scaling automatically adjusts capacity to maintain that target. It's self-adjusting and recommended for most use cases.
- **Why others are wrong:**
  - A) Step Scaling requires defining specific thresholds and corresponding actions, making it more complex.
  - B) Scheduled Scaling is for predictable patterns but requires manual schedule configuration.
  - D) Predictive Scaling uses machine learning and is more advanced.
</details>

---

## Part 2: Multiple Choice - Docker & Containerization

### 8. What is the primary difference between Docker containers and virtual machines?
- [ ] A) Containers are always faster than VMs
- [ ] B) Containers share the host OS kernel; VMs have full guest operating systems
- [ ] C) Containers provide better security isolation than VMs
- [ ] D) Containers can only run Linux applications

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Containers share the host OS kernel; VMs have full guest operating systems

**Explanation:** Docker containers share the host operating system's kernel, making them lightweight (MBs vs GBs for VMs) and fast to start (seconds vs minutes). VMs run complete guest operating systems on a hypervisor, providing hardware-level isolation but with more overhead.
- **Why others are wrong:**
  - A) While containers are typically faster to start, "always" is too absolute.
  - C) VMs actually provide stronger isolation (hardware-level vs process-level).
  - D) Docker supports both Linux and Windows containers.
</details>

---

### 9. In a Dockerfile, what is the difference between CMD and ENTRYPOINT?
- [ ] A) CMD is required; ENTRYPOINT is optional
- [ ] B) CMD provides default arguments that can be overridden; ENTRYPOINT defines the fixed executable
- [ ] C) ENTRYPOINT runs during build; CMD runs at container start
- [ ] D) They are interchangeable with identical behavior

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) CMD provides default arguments that can be overridden; ENTRYPOINT defines the fixed executable

**Explanation:** ENTRYPOINT configures the container's executable and cannot be easily overridden. CMD provides default arguments that are appended to ENTRYPOINT and can be replaced at runtime. Together they allow flexible container behavior.
- **Why others are wrong:**
  - A) Neither is strictly required; both are optional.
  - C) Both CMD and ENTRYPOINT run at container start, not during build. RUN executes during build.
  - D) They have distinct behaviors—ENTRYPOINT is harder to override while CMD is easily replaced.
</details>

---

### 10. Which Docker storage type is recommended for persisting database data in production?
- [ ] A) Bind mounts
- [ ] B) Named volumes
- [ ] C) tmpfs mounts
- [ ] D) Anonymous volumes

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Named volumes

**Explanation:** Named volumes are Docker-managed, optimized for Docker operations, and can use volume drivers for external storage systems. They're recommended for production data because they're portable, easy to backup, and managed independently of the container lifecycle.
- **Why others are wrong:**
  - A) Bind mounts are better for development (live code reload) but are host-specific.
  - C) tmpfs mounts store data in memory only—data is lost when container stops.
  - D) Anonymous volumes have random names, making them difficult to manage and reference.
</details>

---

### 11. What Dockerfile instruction should you use to copy files while preserving the ability to extract tar archives?
- [ ] A) COPY
- [ ] B) ADD
- [ ] C) RUN cp
- [ ] D) IMPORT

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) ADD

**Explanation:** ADD has special features beyond COPY: it can automatically extract tar archives and download files from URLs. However, best practice is to use COPY unless you specifically need ADD's special features, as COPY's behavior is more predictable.
- **Why others are wrong:**
  - A) COPY only copies files; it does not extract archives.
  - C) RUN cp would work but creates an extra layer and is less efficient.
  - D) IMPORT is not a valid Dockerfile instruction.
</details>

---

### 12. What is the purpose of a .dockerignore file?
- [ ] A) To ignore Docker daemon errors
- [ ] B) To exclude files from the build context sent to the daemon
- [ ] C) To prevent certain containers from running
- [ ] D) To hide sensitive environment variables

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) To exclude files from the build context sent to the daemon

**Explanation:** The .dockerignore file specifies patterns for files to exclude from the build context. This reduces context size (faster builds), prevents accidentally including sensitive files, and excludes unnecessary files like node_modules, .git, and IDE configurations.
- **Why others are wrong:**
  - A) Error handling is not the purpose of .dockerignore.
  - C) Container execution is controlled by docker run, not .dockerignore.
  - D) Environment variables are handled separately through ENV or -e flags.
</details>

---

### 13. What is the recommended form for CMD and ENTRYPOINT in Dockerfiles?
- [ ] A) Shell form: `CMD python app.py`
- [ ] B) Exec form: `CMD ["python", "app.py"]`
- [ ] C) Both forms are equally recommended
- [ ] D) String form: `CMD "python app.py"`

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Exec form: `CMD ["python", "app.py"]`

**Explanation:** Exec form (JSON array) is recommended because it runs the command directly as PID 1, allowing proper signal handling (SIGTERM, etc.). Shell form runs via `/bin/sh -c`, which can prevent graceful container shutdown.
- **Why others are wrong:**
  - A) Shell form doesn't receive signals directly and is not recommended for CMD/ENTRYPOINT.
  - C) They are not equally recommended—exec form has clear advantages.
  - D) String form is not a valid Dockerfile syntax.
</details>

---

## Part 3: Multiple Choice - DevOps & Monitoring

### 14. What does the "CALMS" acronym represent in DevOps?
- [ ] A) Continuous, Agile, Lean, Managed, Secure
- [ ] B) Culture, Automation, Lean, Measurement, Sharing
- [ ] C) Code, Application, Logging, Monitoring, Security
- [ ] D) Collaboration, Architecture, Lifecycle, Methods, Standards

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Culture, Automation, Lean, Measurement, Sharing

**Explanation:** CALMS is a framework for DevOps transformation: Culture (shared responsibility, trust), Automation (automate everything possible), Lean (eliminate waste, optimize flow), Measurement (data-driven decisions), and Sharing (knowledge, tools, responsibilities).
- **Why others are wrong:**
  - A, C, D) These are not the correct DevOps CALMS acronym components.
</details>

---

### 15. In Prometheus, what type of metric should be used to track the total number of HTTP requests?
- [ ] A) Gauge
- [ ] B) Counter
- [ ] C) Histogram
- [ ] D) Summary

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Counter

**Explanation:** A Counter is a cumulative metric that only increases (or resets to zero). It's perfect for tracking totals like HTTP requests, errors, or bytes sent. The value goes: 100 → 150 → 200, never decreasing.
- **Why others are wrong:**
  - A) Gauge is for values that can go up or down (temperature, memory usage, queue size).
  - C) Histogram samples observations into buckets, used for request durations or response sizes.
  - D) Summary provides pre-calculated quantiles, similar to histogram but computed client-side.
</details>

---

### 16. What is the primary advantage of Prometheus's pull-based monitoring approach?
- [ ] A) It works better through firewalls
- [ ] B) It's easier to detect when a target is down
- [ ] C) It requires less network bandwidth
- [ ] D) It supports real-time streaming

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) It's easier to detect when a target is down

**Explanation:** With pull-based monitoring, if Prometheus fails to scrape a target, it immediately knows that target is down. Push-based systems have "silent failures"—if an application stops pushing, it's harder to distinguish between "no data to send" and "application is dead."
- **Why others are wrong:**
  - A) Push-based actually works better through firewalls since the target initiates the connection.
  - C) Network bandwidth is similar; both transfer metric data.
  - D) Real-time streaming isn't a characteristic of pull-based monitoring.
</details>

---

### 17. Which of the following is NOT one of the DORA (DevOps Research and Assessment) metrics?
- [ ] A) Deployment Frequency
- [ ] B) Lead Time for Changes
- [ ] C) Code Coverage Percentage
- [ ] D) Mean Time to Recovery (MTTR)

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Code Coverage Percentage

**Explanation:** The four DORA metrics are: Deployment Frequency, Lead Time for Changes, Change Failure Rate, and Mean Time to Recovery (MTTR). These measure software delivery performance. Code coverage is a quality metric but not part of DORA.
- **Why others are wrong:**
  - A) Deployment Frequency measures how often code is deployed to production.
  - B) Lead Time measures time from commit to production.
  - D) MTTR measures how quickly you recover from failures.
</details>

---

### 18. What is the main philosophy behind DevOps' approach to failures?
- [ ] A) Assign blame to identify responsible parties
- [ ] B) Conduct blameless postmortems to improve systems
- [ ] C) Prevent all failures through extensive manual testing
- [ ] D) Roll back all changes that cause failures

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Conduct blameless postmortems to improve systems

**Explanation:** DevOps culture emphasizes blameless postmortems—focusing on improving systems rather than blaming individuals. The goal is psychological safety where teams can discuss failures openly, leading to better solutions and preventing future incidents.
- **Why others are wrong:**
  - A) Blame culture discourages reporting and hides problems.
  - C) Manual testing alone cannot prevent all failures; automation and resilience matter.
  - D) Rollback is a valid response, but the focus should be on learning and improving, not just reverting.
</details>

---

## Part 4: Multiple Choice - Jenkins CI/CD

### 19. What is the primary benefit of using a Jenkinsfile over configuring jobs through the Jenkins UI?
- [ ] A) Faster build execution
- [ ] B) Version control, code review, and reproducibility
- [ ] C) Better security features
- [ ] D) Automatic plugin management

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Version control, code review, and reproducibility

**Explanation:** A Jenkinsfile stores pipeline configuration as code in your repository. This enables version control (history, blame), code review in PRs, branch-specific pipelines, and easy replication. Configuration changes are auditable and reproducible.
- **Why others are wrong:**
  - A) Build execution speed is not affected by where configuration is stored.
  - C) Security features are separate from the configuration method.
  - D) Plugin management is handled separately through Jenkins administration.
</details>

---

### 20. In Jenkins Declarative Pipeline syntax, what directive specifies where the pipeline or stage will execute?
- [ ] A) node
- [ ] B) executor
- [ ] C) agent
- [ ] D) runner

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) agent

**Explanation:** The `agent` directive in Declarative Pipeline specifies where to run: `agent any` (any available), `agent { label 'linux' }` (specific label), `agent { docker { image 'maven:3.8' } }` (Docker container), or `agent none` (per-stage agents).
- **Why others are wrong:**
  - A) `node` is used in Scripted Pipeline syntax, not Declarative.
  - B) `executor` is not a pipeline directive.
  - D) `runner` is used in other CI systems (GitHub Actions) but not Jenkins.
</details>

---

### 21. What is the purpose of the `post` section in a Jenkins Declarative Pipeline?
- [ ] A) To define pipeline parameters
- [ ] B) To specify build steps
- [ ] C) To define actions that run after stages complete based on build status
- [ ] D) To configure pipeline triggers

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) To define actions that run after stages complete based on build status

**Explanation:** The `post` section defines cleanup and notification actions based on build outcome: `always` (runs regardless), `success` (on successful build), `failure` (on failed build), `unstable`, `changed`, etc. Common uses include cleanup, notifications, and artifact archiving.
- **Why others are wrong:**
  - A) Pipeline parameters are defined in the `parameters` section.
  - B) Build steps are defined within `stage { steps { } }` blocks.
  - D) Triggers are configured in `triggers` directive or job settings.
</details>

---

### 22. Which Jenkins Pipeline syntax is recommended for most CI/CD workflows?
- [ ] A) Scripted Pipeline
- [ ] B) Declarative Pipeline
- [ ] C) Freestyle Jobs
- [ ] D) Matrix Pipeline

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Declarative Pipeline

**Explanation:** Declarative Pipeline is recommended for most use cases because it has simpler, structured syntax; better error reporting; Blue Ocean compatibility; and is easier to read and maintain. Use Scripted only when you need complex Groovy logic.
- **Why others are wrong:**
  - A) Scripted Pipeline offers more flexibility but is harder to read and maintain.
  - C) Freestyle Jobs are UI-configured, lacking version control benefits.
  - D) Matrix Pipeline is a specific feature, not a general syntax recommendation.
</details>

---

## Part 5: True/False

### 23. True or False: In AWS, security groups can have both allow and deny rules.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** Security groups only support allow rules—you cannot create explicit deny rules. If traffic isn't explicitly allowed, it's denied by default. If you need deny rules, use Network ACLs (NACLs) which support both allow and deny rules.
</details>

---

### 24. True or False: Docker containers include a full operating system, which makes them more portable than virtual machines.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** Docker containers do NOT include a full operating system—they share the host OS kernel. This makes them lightweight (MBs instead of GBs) and fast to start (seconds instead of minutes). VMs include full guest operating systems, which adds overhead.
</details>

---

### 25. True or False: RDS Read Replicas use synchronous replication for zero data loss.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** RDS Read Replicas use asynchronous replication, meaning there can be slight lag between primary and replica. Multi-AZ deployments use synchronous replication for zero data loss. Read Replicas are for scaling read workloads, not high availability.
</details>

---

### 26. True or False: In Prometheus, a Gauge metric can only increase, never decrease.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** A Gauge is specifically for values that can go up AND down (temperature, memory usage, queue size, active connections). Counters are the metric type that only increases (or resets to zero on restart).
</details>

---

### 27. True or False: S3 bucket names must be globally unique across all AWS accounts.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** True

**Explanation:** S3 bucket names must be globally unique across ALL AWS accounts worldwide. This is because bucket names become part of the S3 URL (bucket-name.s3.amazonaws.com). You cannot create a bucket if the name is already taken by any AWS customer.
</details>

---

### 28. True or False: Jenkins can only execute builds on the master (controller) node.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** False

**Explanation:** Jenkins uses a master-agent architecture. While the master can execute builds (especially for small setups), production environments use dedicated agents to execute builds. Agents can be Linux, Windows, Docker containers, or Kubernetes pods.
</details>

---

## Part 6: Code Prediction

### 29. What does this Dockerfile command do?

```dockerfile
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY . .
```

- [ ] A) Copies all files, then installs dependencies
- [ ] B) Installs dependencies first, leveraging Docker's layer caching for faster rebuilds
- [ ] C) Creates a syntax error because COPY appears twice
- [ ] D) Overwrites requirements.txt with the entire directory

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Installs dependencies first, leveraging Docker's layer caching for faster rebuilds

**Explanation:** This pattern copies `requirements.txt` separately, installs dependencies, then copies application code. Since requirements change less frequently than code, Docker can cache the dependency installation layer. When only code changes, the pip install layer is reused, significantly speeding up builds.
</details>

---

### 30. What will this Jenkins Pipeline output when the ENVIRONMENT parameter is "prod"?

```groovy
pipeline {
    agent any
    parameters {
        choice(name: 'ENVIRONMENT', choices: ['dev', 'staging', 'prod'])
    }
    stages {
        stage('Deploy') {
            when {
                expression { params.ENVIRONMENT == 'prod' }
            }
            steps {
                echo 'Deploying to production!'
            }
        }
    }
}
```

- [ ] A) The stage is skipped
- [ ] B) "Deploying to production!" is printed
- [ ] C) A syntax error occurs
- [ ] D) The build fails

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) "Deploying to production!" is printed

**Explanation:** The `when` directive conditionally executes the stage. Since `params.ENVIRONMENT` equals 'prod' and the expression evaluates to true, the Deploy stage runs and prints "Deploying to production!". If ENVIRONMENT were 'dev' or 'staging', the stage would be skipped.
</details>

---

### 31. What is the result of this PromQL query?

```promql
rate(http_requests_total[5m])
```

- [ ] A) The total number of HTTP requests ever made
- [ ] B) The per-second rate of HTTP requests over the last 5 minutes
- [ ] C) The HTTP requests made exactly 5 minutes ago
- [ ] D) The average response time of HTTP requests

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) The per-second rate of HTTP requests over the last 5 minutes

**Explanation:** The `rate()` function calculates the per-second average rate of increase for a counter over the specified time range. `http_requests_total[5m]` is a range vector containing values from the last 5 minutes. This is how you get meaningful request rates from cumulative counters.
</details>

---

### 32. What does this Docker command do?

```bash
docker run -d -v postgres-data:/var/lib/postgresql/data -p 5432:5432 postgres:15
```

- [ ] A) Runs PostgreSQL with ephemeral storage that's lost when container stops
- [ ] B) Runs PostgreSQL with a named volume for persistent data and exposed on port 5432
- [ ] C) Creates a bind mount to the current directory
- [ ] D) Fails because volume name cannot contain hyphens

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Runs PostgreSQL with a named volume for persistent data and exposed on port 5432

**Explanation:** `-d` runs detached. `-v postgres-data:/var/lib/postgresql/data` creates/uses a named volume for data persistence. `-p 5432:5432` maps the container's PostgreSQL port to the host. Data survives container removal because it's stored in the named volume.
</details>

---

## Part 7: Fill-in-the-Blank

### 33. The AWS CLI command to list all S3 buckets is `aws s3 _____`.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** `ls`

**Explanation:** `aws s3 ls` lists all S3 buckets in your account. You can also use `aws s3 ls s3://bucket-name/` to list objects within a specific bucket, or add `--recursive` for nested objects.
</details>

---

### 34. In Docker, the command to build an image with the tag "my-app:1.0" from the current directory is `docker build -t my-app:1.0 _____`.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** `.` (dot/period)

**Explanation:** The `.` specifies the build context—the current directory containing the Dockerfile and files to copy. The `-t` flag tags the resulting image as "my-app:1.0". The build context is sent to the Docker daemon for building.
</details>

---

### 35. In a Jenkinsfile, the `_____` directive is required and contains all the stages of the pipeline.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** `stages`

**Explanation:** In Declarative Pipeline syntax, the `stages` block is required and contains one or more `stage` blocks. Each stage represents a logical step in the pipeline (Build, Test, Deploy). Structure: `pipeline { agent any stages { stage('Build') { steps { ... } } } }`.
</details>

---

### 36. The Prometheus metric type that samples observations into configurable buckets (useful for request durations) is called a _____.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** `Histogram`

**Explanation:** Histogram samples observations and counts them in configurable buckets. It's ideal for measuring request durations or response sizes. You can query buckets like `http_request_duration_seconds_bucket{le="0.5"}` to find what percentage of requests completed in ≤0.5 seconds.
</details>

---

### 37. The DevOps practice that ensures developers integrate code frequently with automated builds and tests is called Continuous _____.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** `Integration` (CI)

**Explanation:** Continuous Integration (CI) is the practice where developers integrate code frequently (daily or more). Each integration triggers automated builds and tests to find and fix issues quickly. Tools include Jenkins, GitHub Actions, and GitLab CI.
</details>

---

### 38. In AWS RDS, the _____ feature allows you to restore your database to any point in time within the backup retention period.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** `Point-in-Time Recovery` (or automated backups)

**Explanation:** RDS automated backups enable Point-in-Time Recovery by taking daily full backups and continuous transaction log backups (every 5 minutes). You can restore to any second within the retention period (up to 35 days).
</details>

---

## Part 8: Advanced Questions

### 39. A company needs to deploy a web application with the following requirements: automatic scaling based on demand, high availability across multiple availability zones, and automatic replacement of unhealthy instances. Which combination of AWS services would best meet these needs?

- [ ] A) EC2 with Elastic IP and S3
- [ ] B) Auto Scaling Group with Application Load Balancer and Multi-AZ
- [ ] C) Single large EC2 instance with RDS
- [ ] D) Lambda functions with API Gateway

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Auto Scaling Group with Application Load Balancer and Multi-AZ

**Explanation:** This combination provides: Auto Scaling Group (scales instances based on demand, replaces unhealthy instances), Application Load Balancer (distributes traffic, provides health checks), Multi-AZ deployment (high availability across availability zones). ELB health checks enable accurate unhealthy instance detection.
- **Why others are wrong:**
  - A) Elastic IP and S3 don't provide scaling or HA for compute.
  - C) Single instance is a single point of failure.
  - D) Lambda is serverless—valid but not traditional web server architecture.
</details>

---

### 40. You're debugging why your Jenkins pipeline can't connect to your RDS database in AWS. What is the MOST likely cause?

- [ ] A) The Jenkinsfile has a syntax error
- [ ] B) The RDS security group doesn't allow inbound traffic from Jenkins
- [ ] C) Jenkins doesn't support database connections
- [ ] D) RDS can only be accessed from EC2 instances

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) The RDS security group doesn't allow inbound traffic from Jenkins

**Explanation:** RDS databases are protected by security groups. If the security group doesn't have an inbound rule allowing traffic on the database port (e.g., 3306 for MySQL, 5432 for PostgreSQL) from Jenkins' IP or security group, connections will be refused. This is the most common connectivity issue.
- **Why others are wrong:**
  - A) Syntax errors would cause pipeline failures earlier, not connection issues.
  - C) Jenkins can run any command including database connections.
  - D) RDS can be accessed from anywhere with proper security group configuration.
</details>

---

### 41. In a multi-stage Docker build, what is the primary benefit of using a separate build stage from the runtime stage?

- [ ] A) Faster build times
- [ ] B) Better security through code obfuscation
- [ ] C) Smaller final images by excluding build tools and dependencies
- [ ] D) Support for multiple programming languages

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Smaller final images by excluding build tools and dependencies

**Explanation:** Multi-stage builds allow you to use a full SDK/build image for compilation, then copy only the compiled artifacts to a minimal runtime image. For example, a Java build might use a 1GB JDK image for building but a 200MB JRE image for running. Build tools, source code, and intermediate files aren't included in the final image.
- **Why others are wrong:**
  - A) Multi-stage can actually be slightly slower due to multiple image pulls.
  - B) Code obfuscation is a separate concern.
  - D) Multiple languages aren't the primary benefit (though possible).
</details>

---

### 42. What is the correct order of the DevOps lifecycle phases?

- [ ] A) Plan → Build → Test → Deploy → Operate → Monitor → Release
- [ ] B) Build → Test → Release → Deploy → Operate → Monitor → Plan
- [ ] C) Plan → Build → Test → Release → Deploy → Operate → Monitor
- [ ] D) Monitor → Plan → Build → Test → Release → Deploy → Operate

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Plan → Build → Test → Release → Deploy → Operate → Monitor

**Explanation:** The DevOps lifecycle is a continuous loop: Plan (requirements, backlog) → Build (code, compile) → Test (automated testing) → Release (prepare deployment) → Deploy (to environment) → Operate (run in production) → Monitor (collect metrics, logs) → back to Plan (feedback loop).
- **Why others are wrong:**
  - A) Release comes before Deploy, not after Operate.
  - B) Plan should come first, not last.
  - D) Monitor completes the cycle, feeding back to Plan.
</details>

---

## Summary

You've completed the Week 10 Knowledge Check covering:
- ☁️ **AWS Fundamentals**: IaaS/PaaS/SaaS, EC2, Security Groups, RDS, S3, Auto Scaling
- 🐳 **Docker**: Containers vs VMs, Dockerfiles, Volumes, Images
- 🔄 **DevOps**: CALMS framework, CI/CD, DORA metrics, blameless culture
- 📊 **Monitoring**: Prometheus metrics, PromQL, pull-based architecture
- 🔧 **Jenkins**: Pipelines, Jenkinsfile, Declarative syntax, agents

**Next Steps:** Review any questions you missed by returning to the written content in the corresponding day's folder.

