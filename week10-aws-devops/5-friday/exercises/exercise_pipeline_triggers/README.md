# Exercise 4: Pipeline Triggers

## Objective

Configure various build triggers including webhooks, scheduled builds, and SCM polling to automate pipeline execution.

---

## Learning Outcomes

By completing this exercise, you will:
- Configure SCM polling for automatic builds
- Set up scheduled (cron) builds
- Understand webhook concepts
- Test remote build triggering
- Configure build trigger tokens

---

## Prerequisites

- Completed Exercise 3 (Jenkinsfile Creation)
- Jenkins running at http://localhost:8080
- Understanding of pipeline jobs

---

## Time Estimate

30 minutes

---

## Trigger Types Overview

| Trigger | Use Case | Configuration |
|---------|----------|---------------|
| Manual | On-demand builds | Default (Build Now) |
| SCM Poll | Check repo for changes | Cron schedule |
| Webhook | Instant on push | External service calls Jenkins |
| Scheduled | Time-based builds | Cron schedule |
| Remote | API/script triggered | Token-based URL |
| Upstream | After another job | Job dependency |

---

## Tasks

### Task 1: SCM Polling (10 minutes)

1. **Create Polling Pipeline**
   - New Item → Name: `scm-polling-demo`
   - Type: Pipeline → OK

2. **Pipeline Script**
   ```groovy
   pipeline {
       agent any
       
       triggers {
           // Poll SCM every 5 minutes
           pollSCM('H/5 * * * *')
       }
       
       stages {
           stage('Check Trigger') {
               steps {
                   echo "Build triggered!"
                   echo "Build Number: ${BUILD_NUMBER}"
                   
                   script {
                       def causes = currentBuild.getBuildCauses()
                       causes.each { cause ->
                           echo "Cause: ${cause.shortDescription}"
                       }
                   }
               }
           }
           
           stage('Simulate Work') {
               steps {
                   echo 'Simulating build work...'
                   sh 'sleep 3'
                   echo 'Work complete!'
               }
           }
       }
       
       post {
           always {
               echo "Build finished at: ${new Date()}"
           }
       }
   }
   ```

3. **Build Once to Register Trigger**
   - Save and Build Now
   - Check "Poll SCM" in job config shows schedule

4. **Understand Cron Syntax**
   ```
   ┌───────────── minute (0 - 59)
   │ ┌───────────── hour (0 - 23)
   │ │ ┌───────────── day of month (1 - 31)
   │ │ │ ┌───────────── month (1 - 12)
   │ │ │ │ ┌───────────── day of week (0 - 6)
   │ │ │ │ │
   │ │ │ │ │
   H/5 * * * *   = Every 5 minutes
   H 8 * * 1-5   = 8am weekdays
   H 0 1 * *     = 1st of month at midnight
   ```

**Checkpoint:** SCM polling configured ✓

---

### Task 2: Scheduled Builds (10 minutes)

1. **Create Scheduled Pipeline**
   - New Item → Name: `scheduled-build-demo`
   - Type: Pipeline → OK

2. **Pipeline Script**
   ```groovy
   pipeline {
       agent any
       
       triggers {
           // Multiple schedules
           cron('''
               # Run every 2 minutes (for demo)
               H/2 * * * *
               
               # Nightly build at 2am (commented for demo)
               # H 2 * * *
               
               # Weekly on Sunday at midnight
               # H 0 * * 0
           ''')
       }
       
       stages {
           stage('Scheduled Task') {
               steps {
                   echo "Scheduled build executed!"
                   echo "Current time: ${new Date()}"
                   
                   script {
                       def causes = currentBuild.getBuildCauses()
                       def isScheduled = causes.any { it.shortDescription.contains('timer') }
                       
                       if (isScheduled) {
                           echo "This was a SCHEDULED build"
                       } else {
                           echo "This was a MANUAL build"
                       }
                   }
               }
           }
           
           stage('Daily Tasks') {
               steps {
                   echo 'Running scheduled maintenance tasks...'
                   sh '''
                       echo "Task 1: Clean old files"
                       echo "Task 2: Generate reports"
                       echo "Task 3: Send notifications"
                   '''
               }
           }
       }
       
       post {
           always {
               echo "Scheduled job completed"
           }
       }
   }
   ```

3. **Build and Wait**
   - Save and Build Now (to register trigger)
   - Wait 2 minutes
   - Check if automatic build triggers

4. **Disable Schedule After Testing**
   - Edit job
   - Comment out or remove cron trigger to stop auto-builds

**Checkpoint:** Scheduled builds working ✓

---

### Task 3: Remote Build Trigger (10 minutes)

1. **Create Remote Trigger Pipeline**
   - New Item → Name: `remote-trigger-demo`
   - Type: Pipeline → OK

2. **Configure Build Trigger**
   - In job configuration
   - ☑ Trigger builds remotely
   - Authentication Token: `my-build-token`

3. **Pipeline Script**
   ```groovy
   pipeline {
       agent any
       
       stages {
           stage('Remote Triggered Build') {
               steps {
                   echo "Build triggered remotely!"
                   
                   script {
                       def causes = currentBuild.getBuildCauses()
                       causes.each { cause ->
                           echo "Trigger cause: ${cause.shortDescription}"
                       }
                   }
               }
           }
           
           stage('Process') {
               steps {
                   echo "Processing remote trigger..."
                   sh '''
                       echo "Received remote build request"
                       echo "Executing automated tasks..."
                       sleep 2
                       echo "Complete!"
                   '''
               }
           }
       }
       
       post {
           success {
               echo "Remote build completed successfully"
           }
       }
   }
   ```

4. **Save Job**

5. **Trigger Remotely via curl**
   
   Open a terminal and run:
   ```bash
   # Without authentication (if anonymous build enabled)
   curl -X POST "http://localhost:8080/job/remote-trigger-demo/build?token=my-build-token"
   
   # With authentication
   curl -X POST "http://admin:admin123@localhost:8080/job/remote-trigger-demo/build?token=my-build-token"
   ```

6. **Check Jenkins**
   - Build should appear in Build History
   - Check Console Output for trigger cause

7. **Trigger with Parameters**
   
   For parameterized jobs:
   ```bash
   curl -X POST "http://admin:admin123@localhost:8080/job/remote-trigger-demo/buildWithParameters?token=my-build-token&PARAM1=value1"
   ```

**Checkpoint:** Remote trigger working ✓

---

### Task 4: Upstream/Downstream Triggers (Bonus)

1. **Create Upstream Job**
   - New Item → Name: `upstream-job`
   - Type: Pipeline
   
   ```groovy
   pipeline {
       agent any
       stages {
           stage('Upstream Work') {
               steps {
                   echo 'Upstream job running...'
                   sh 'sleep 2'
                   echo 'Upstream complete!'
               }
           }
       }
   }
   ```

2. **Create Downstream Job**
   - New Item → Name: `downstream-job`
   - Type: Pipeline
   
   ```groovy
   pipeline {
       agent any
       
       triggers {
           // Trigger after upstream-job completes
           upstream(upstreamProjects: 'upstream-job', threshold: hudson.model.Result.SUCCESS)
       }
       
       stages {
           stage('Downstream Work') {
               steps {
                   echo 'Downstream job triggered by upstream!'
                   echo "Upstream job succeeded, starting downstream tasks..."
               }
           }
       }
   }
   ```

3. **Test Chain**
   - Build `upstream-job`
   - Watch `downstream-job` trigger automatically

---

## Verification Checklist

- [ ] SCM polling configured with cron schedule
- [ ] Scheduled build triggers work
- [ ] Remote trigger via curl successful
- [ ] Understand different trigger types
- [ ] Know when to use each trigger type

---

## Deliverables

1. Screenshot of job configuration showing trigger settings
2. curl command that successfully triggers a build
3. Console output showing "timer" or "remote" trigger cause
4. Brief explanation: When would you use webhooks vs polling?

---

## Cron Reference

### Common Schedules
```
H * * * *       = Every hour
H/15 * * * *    = Every 15 minutes
H 0 * * *       = Daily at midnight
H 2 * * 1-5     = 2am on weekdays
H 0 1 * *       = 1st of each month
H 0 * * 0       = Weekly on Sunday
```

### The 'H' Hash Symbol
- Distributes load across Jenkins
- `H` in minute field: any minute (consistent per job)
- `H/15`: every 15 minutes (offset by hash)

---

## Webhook vs Polling

| Aspect | Webhook | Polling |
|--------|---------|---------|
| Speed | Instant | Delayed (poll interval) |
| Setup | More complex | Simple |
| Network | Requires inbound access | Outbound only |
| Load | Event-driven | Constant checking |
| Use Case | Active development | Legacy/firewalled |

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| Poll not triggering | No SCM configured | Add Git repo to job |
| Remote trigger 403 | Auth required | Add credentials to curl |
| Schedule not running | First build needed | Build once manually |
| Wrong trigger cause | Multiple triggers | Check all trigger configs |

---

## Clean-Up

Disable scheduled triggers to prevent continuous builds:
1. Edit each job
2. Remove or comment cron/pollSCM triggers
3. Save

---

## Additional Resources

- [Pipeline Triggers](https://www.jenkins.io/doc/book/pipeline/syntax/#triggers)
- [Remote Access API](https://www.jenkins.io/doc/book/using/remote-access-api/)
- [Cron Syntax](https://en.wikipedia.org/wiki/Cron#CRON_expression)

