# Lab: JMeter Load Testing

## Overview

**Duration:** 60-75 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll create a JMeter test plan to perform load testing on an API. You'll configure thread groups, add samplers, analyze results, and understand performance metrics.

---

## Learning Objectives

By completing this lab, you will:
- Create and configure JMeter test plans
- Set up thread groups for load simulation
- Add HTTP samplers for API requests
- Configure listeners for results analysis
- Interpret load test results and metrics

---

## Prerequisites

- Apache JMeter installed (5.x)
- Understanding of performance testing concepts
- Familiarity with HTTP requests

---

## The Scenario

BookHaven is preparing for a major sale event. The team needs to verify the API can handle 50 concurrent users making requests. Your task is to create a load test that simulates this traffic and identifies any performance bottlenecks.

---

## Core Tasks

### Task 1: JMeter Setup and Interface (10 minutes)

**Launch JMeter:**
1. Navigate to JMeter's `/bin` directory
2. Run `jmeter.bat` (Windows) or `jmeter.sh` (Linux/Mac)
3. JMeter GUI opens

**Familiarize with interface:**
```
┌─────────────────────────────────────────────────────────────────┐
│ File  Edit  Run  Options  Help                                   │
├─────────────────────────────────────────────────────────────────┤
│ Test Plan                    │                                   │
│ ├── Thread Group            │  [Configuration Panel]            │
│ │   ├── Sampler             │                                   │
│ │   ├── Listener            │                                   │
│ │   └── Assertions          │                                   │
│ └── Config Elements         │                                   │
└─────────────────────────────────────────────────────────────────┘
```

### Task 2: Create Test Plan Structure (15 minutes)

**Step 1: Configure Test Plan**
1. Click on "Test Plan" in the tree
2. Name: `BookHaven API Load Test`
3. Add comment: "Performance test for Posts API"

**Step 2: Add Thread Group**
1. Right-click Test Plan → Add → Threads → Thread Group
2. Configure:
   - Name: `API Users`
   - Number of Threads (users): `10`
   - Ramp-up period: `5` seconds
   - Loop Count: `10`

**Thread Group Settings Explained:**
```
Number of Threads = 10
├── Simulates 10 concurrent users

Ramp-up Period = 5 seconds
├── Users added gradually over 5 seconds
├── 10 users / 5 seconds = 2 users per second

Loop Count = 10
├── Each user makes 10 iterations
├── Total requests = 10 users × 10 loops = 100 requests
```

**Step 3: Add HTTP Request Defaults**
1. Right-click Thread Group → Add → Config Element → HTTP Request Defaults
2. Configure:
   - Server Name: `jsonplaceholder.typicode.com`
   - Protocol: `https`
   - Port: `443`

### Task 3: Add HTTP Samplers (15 minutes)

**Sampler 1: GET All Posts**
1. Right-click Thread Group → Add → Sampler → HTTP Request
2. Configure:
   - Name: `GET All Posts`
   - Method: `GET`
   - Path: `/posts`

**Sampler 2: GET Single Post**
1. Add another HTTP Request sampler
2. Configure:
   - Name: `GET Post by ID`
   - Method: `GET`
   - Path: `/posts/1`

**Sampler 3: POST Create Post**
1. Add HTTP Request sampler
2. Configure:
   - Name: `CREATE Post`
   - Method: `POST`
   - Path: `/posts`
   - Body Data:
```json
{
    "title": "JMeter Test Post",
    "body": "Created during load testing",
    "userId": 1
}
```
3. Add HTTP Header Manager:
   - Right-click sampler → Add → Config Element → HTTP Header Manager
   - Add: `Content-Type` = `application/json`

**Sampler 4: DELETE Post**
1. Add HTTP Request sampler
2. Configure:
   - Name: `DELETE Post`
   - Method: `DELETE`
   - Path: `/posts/1`

### Task 4: Add Listeners (10 minutes)

**Add these listeners under Thread Group:**

**1. View Results Tree**
- Right-click → Add → Listener → View Results Tree
- Shows individual request/response details
- Good for debugging

**2. Summary Report**
- Right-click → Add → Listener → Summary Report
- Shows aggregate statistics
- Key metrics: Average, Min, Max, Error %, Throughput

**3. Aggregate Report**
- Right-click → Add → Listener → Aggregate Report
- Similar to Summary with more detail
- Includes percentiles (90%, 95%, 99%)

**4. Response Time Graph**
- Right-click → Add → Listener → Response Time Graph
- Visual representation of response times

**5. Transactions per Second**
- Right-click → Add → Listener → Transactions per Second
- Shows throughput over time

### Task 5: Add Assertions (10 minutes)

**Response Assertion for GET requests:**
1. Right-click on "GET All Posts" → Add → Assertions → Response Assertion
2. Configure:
   - Apply to: Main sample only
   - Response Field: Response Code
   - Pattern Matching: Equals
   - Patterns to Test: `200`

**Duration Assertion:**
1. Right-click Thread Group → Add → Assertions → Duration Assertion
2. Configure:
   - Duration in milliseconds: `5000`
   - Marks request as failed if > 5 seconds

### Task 6: Run the Test (15 minutes)

**Before running:**
1. Save the test plan: File → Save Test Plan As → `bookhaven_load_test.jmx`
2. Clear previous results: Run → Clear All

**Run the test:**
1. Click the green "Start" button (or Run → Start)
2. Watch the listeners update in real-time
3. Wait for completion

**Analyze Results:**

**Summary Report Interpretation:**
| Metric | What it Means |
|--------|---------------|
| Samples | Total number of requests |
| Average | Mean response time (ms) |
| Min | Fastest response |
| Max | Slowest response |
| Std. Dev. | Response time variation |
| Error % | Percentage of failures |
| Throughput | Requests per second |
| KB/sec | Data transfer rate |

**Record your results:**

| Sampler | Samples | Average (ms) | Error % | Throughput |
|---------|---------|--------------|---------|------------|
| GET All Posts | | | | |
| GET Post by ID | | | | |
| CREATE Post | | | | |
| DELETE Post | | | | |

### Task 7: Increase Load (10 minutes)

**Test with increased load:**

**Scenario 1: 25 Users**
- Threads: `25`
- Ramp-up: `10`
- Loops: `10`

**Scenario 2: 50 Users**
- Threads: `50`
- Ramp-up: `20`
- Loops: `10`

**Record and compare results:**

| Scenario | Total Requests | Avg Response | Error % | Throughput |
|----------|----------------|--------------|---------|------------|
| 10 Users | | | | |
| 25 Users | | | | |
| 50 Users | | | | |

**Look for:**
- Response time increase as load increases
- Error rate changes
- Throughput plateau

---

## Definition of Done

Your lab is complete when you have:

- [ ] Test plan created with thread group
- [ ] 4 HTTP samplers configured
- [ ] Listeners added for analysis
- [ ] Assertions configured
- [ ] Baseline test (10 users) executed
- [ ] Increased load test (50 users) executed
- [ ] Results documented
- [ ] Test plan saved as .jmx file

---

## Starter Files

Find `bookhaven_load_test.jmx` template in `starter_code/`.

---

## Challenge Tasks (Optional)

### 1. Run from Command Line
```bash
jmeter -n -t bookhaven_load_test.jmx -l results.jtl -e -o ./report
```
- `-n`: Non-GUI mode
- `-t`: Test file
- `-l`: Results file
- `-e`: Generate report
- `-o`: Output folder

### 2. Add CSV Data Set
Use different post IDs from a CSV file:
1. Create `post_ids.csv`:
```csv
postId
1
2
3
4
5
```
2. Add CSV Data Set Config
3. Use `${postId}` in request

### 3. Add Timers
Add realistic delays between requests:
- Constant Timer: Fixed delay
- Uniform Random Timer: Random delay within range
- Gaussian Random Timer: Normal distribution

---

## Results Analysis Template

```markdown
## Load Test Report

**Test Date:** _______________
**Test Duration:** _______________
**Target API:** JSONPlaceholder

### Test Scenarios

#### Scenario 1: Baseline (10 Users)
- Threads: 10
- Ramp-up: 5s
- Loops: 10
- Total Requests: 400

**Results:**
| Metric | Value |
|--------|-------|
| Average Response Time | ___ ms |
| 90th Percentile | ___ ms |
| Error Rate | ___ % |
| Throughput | ___ req/s |

#### Scenario 2: Peak Load (50 Users)
- Threads: 50
- Ramp-up: 20s
- Loops: 10
- Total Requests: 2000

**Results:**
| Metric | Value |
|--------|-------|
| Average Response Time | ___ ms |
| 90th Percentile | ___ ms |
| Error Rate | ___ % |
| Throughput | ___ req/s |

### Observations
- 
- 

### Recommendations
- 
- 
```

---

## Common Issues

1. **No results showing:** Ensure samplers are under Thread Group
2. **Connection errors:** Check server name and protocol
3. **Timeout errors:** Increase timeout in HTTP Request Defaults
4. **Out of memory:** Increase JMeter heap size in jmeter.bat

---

## Submission Checklist

| Item | Completed |
|------|-----------|
| Test plan saved (.jmx) | ☐ |
| Thread group configured | ☐ |
| 4 HTTP samplers added | ☐ |
| Listeners configured | ☐ |
| Assertions added | ☐ |
| 10-user test completed | ☐ |
| 50-user test completed | ☐ |
| Results documented | ☐ |

---

## Additional Resources

- Written Content: `jmeter-overview.md`, `performance-testing.md`, `jmeter-gui-mode.md`
- [JMeter User Manual](https://jmeter.apache.org/usermanual/)
- [JMeter Best Practices](https://jmeter.apache.org/usermanual/best-practices.html)

