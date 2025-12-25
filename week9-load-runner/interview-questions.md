# Interview Questions: Week 9 - Load Runner

## Beginner (Foundational)

### Q1: What is LoadRunner and what is its purpose?
**Keywords:** Performance Testing, Virtual Users, Enterprise, Micro Focus, Load Simulation
<details>
<summary>Click to Reveal Answer</summary>

LoadRunner is an enterprise-grade performance testing platform developed by Micro Focus used to simulate thousands of users interacting with an application simultaneously. It measures how systems behave under various load conditions, helping teams identify bottlenecks before they impact real users. LoadRunner creates virtual users (VUsers) that mimic real user behavior.
</details>

---

### Q2: What are the four main components of LoadRunner?
**Keywords:** VuGen, Controller, Load Generator, Analysis
<details>
<summary>Click to Reveal Answer</summary>

The four main components of LoadRunner are:
1. **Virtual User Generator (VuGen)**: Creates and edits test scripts by recording user interactions
2. **Controller**: The command center for designing and managing load test scenarios
3. **Load Generators**: Execute the actual virtual users and generate load on the system
4. **Analysis**: Processes raw test results into meaningful graphs, statistics, and reports
</details>

---

### Q3: What is a Virtual User (VUser) in LoadRunner?
**Keywords:** Simulation, User Behavior, Concurrent, Session, Script
<details>
<summary>Click to Reveal Answer</summary>

A Virtual User (VUser) simulates the actions of a real user interacting with an application. VUsers execute scripts recorded in VuGen, generating load on the system. Each VUser operates independently, maintaining its own session and data, allowing testers to simulate hundreds or thousands of concurrent users without needing real people.
</details>

---

### Q4: What is the purpose of the Virtual User Generator (VuGen)?
**Keywords:** Recording, Scripting, IDE, Parameterization, Debugging
<details>
<summary>Click to Reveal Answer</summary>

VuGen is LoadRunner's integrated development environment for creating, editing, and debugging performance test scripts. It records user interactions with applications and generates scripts that simulate those actions. VuGen also provides capabilities for parameterization, correlation, validation, and script debugging.
</details>

---

### Q5: Explain the three-section structure of a VuGen script.
**Keywords:** vuser_init, Action, vuser_end, Iteration, Lifecycle
<details>
<summary>Click to Reveal Answer</summary>

Every VuGen script follows a three-section structure:
1. **vuser_init**: Executes once at the start of virtual user lifecycle (initialization, login, establish connections)
2. **Action**: Executes for each iteration; contains the main business workflow that simulates typical user actions
3. **vuser_end**: Executes once at the end of virtual user lifecycle (logout, cleanup, close connections)
</details>

---

### Q6: What is parameterization in LoadRunner and why is it important?
**Keywords:** Dynamic Data, CSV, Realistic, Unique Values, Static Replacement
<details>
<summary>Click to Reveal Answer</summary>

Parameterization replaces static (hardcoded) values with dynamic data, making scripts more realistic. Without parameterization, all virtual users would use identical data (same username, same search terms), creating unrealistic tests. With parameterization, each VUser can use unique credentials, search different products, and maintain separate sessions.
</details>

---

### Q7: What is correlation in LoadRunner?
**Keywords:** Dynamic Values, Session ID, Token, Capture, web_reg_save_param
<details>
<summary>Click to Reveal Answer</summary>

Correlation captures dynamic server-generated values (like session IDs, tokens, and timestamps) and replaces them with parameterized values for subsequent requests. It's necessary because web applications generate unique values per session, and replaying recorded static values would cause script failures. Correlation ensures scripts remain valid across multiple iterations.
</details>

---

### Q8: What is the difference between manual and goal-oriented scenarios in LoadRunner Controller?
**Keywords:** VUser Count, Target, TPS, Automatic Adjustment, Configuration
<details>
<summary>Click to Reveal Answer</summary>

- **Manual Scenario**: Tester explicitly configures the number of VUsers, ramp-up time, duration, and ramp-down pattern. Provides full control over the load profile.
- **Goal-Oriented Scenario**: Tester defines a performance goal (e.g., 500 transactions/second), and LoadRunner automatically determines and adjusts the number of VUsers needed to achieve that goal.
</details>

---

### Q9: What is ramp-up in load testing?
**Keywords:** Gradual Increase, Virtual Users, Breaking Point, Schedule, Realistic
<details>
<summary>Click to Reveal Answer</summary>

Ramp-up is the gradual increase in the number of virtual users over time. Instead of starting all users simultaneously (which can crash the system), ramp-up adds users incrementally. For example, adding 10 users every 30 seconds until reaching 100 users. This approach simulates realistic user growth, helps identify breaking points, and allows the system to scale resources appropriately.
</details>

---

### Q10: What is think time in LoadRunner?
**Keywords:** Pause, User Delay, Realistic Simulation, Reading Time, Pacing
<details>
<summary>Click to Reveal Answer</summary>

Think time simulates the pause between user actions, representing the time a real user spends reading content, filling forms, or making decisions. It makes load simulation more realistic and prevents unrealistic stress on the server. Think time affects the calculation of concurrent user capacity and helps identify issues that only appear under realistic load patterns.
</details>

---

### Q11: What key metrics would you analyze in a LoadRunner performance report?
**Keywords:** Response Time, Throughput, Error Rate, Percentile, TPS
<details>
<summary>Click to Reveal Answer</summary>

Key metrics include:
- **Response Time**: Average, 90th percentile, standard deviation
- **Throughput**: Transactions per second (TPS), data throughput (MB/sec)
- **Error Rate**: Percentage of failed transactions
- **Hits per Second**: Individual HTTP requests to the server
- **Running VUsers**: Concurrent users over time
</details>

---

## Intermediate (Application)

### Q12: Why is the 90th percentile more meaningful than average response time for SLAs?
**Keywords:** Distribution, Outliers, User Experience, Skewed, Benchmark
**Hint:** Think about what happens when one very slow response affects the average.
<details>
<summary>Click to Reveal Answer</summary>

The 90th percentile means 90% of users experienced response times at or below this value. It's more meaningful than average because:
- Averages can be skewed by outliers (one 10-second response among 99 1-second responses)
- Shows the experience of the majority of users
- Better represents actual user experience
- Commonly used in SLA definitions
- Industry standard for performance benchmarking
</details>

---

### Q13: How do you identify a performance bottleneck using LoadRunner Analysis?
**Keywords:** Correlation, Graphs, Resource Metrics, Response Time, Throughput Plateau
**Hint:** Look for relationships between different metrics at the same point in time.
<details>
<summary>Click to Reveal Answer</summary>

1. Correlate response time graphs with throughput - if response time increases while throughput flattens, there's a bottleneck
2. Compare transaction response times to identify which transactions degrade under load
3. Overlay system resource graphs (CPU, memory, I/O) with response times
4. Analyze hit distribution to identify slow server components
5. Use web page diagnostics to break down response time by component
6. Look for patterns: connection pool exhaustion, database locks, GC pauses
</details>

---

### Q14: A script runs successfully with one user but fails with multiple concurrent users. What would you investigate?
**Keywords:** Correlation, Parameterization, Unique Data, Session Conflict, Resource Cleanup
**Hint:** Consider what each virtual user needs to be unique.
<details>
<summary>Click to Reveal Answer</summary>

1. **Correlation**: Missing dynamic value correlation (session IDs, tokens)
2. **Parameterization**: Hard-coded data causing conflicts (same username for all users)
3. **Data uniqueness**: Each user needs unique test data
4. **Resource cleanup**: Scripts not properly releasing resources
5. **Think time**: Too aggressive pacing overwhelming the server
6. **Connection limits**: Server connection limits being reached
</details>

---

### Q15: Explain the difference between load testing, stress testing, and endurance testing.
**Keywords:** Expected Load, Breaking Point, Sustained Duration, Capacity, Memory Leak
**Hint:** Consider the goals and duration of each test type.
<details>
<summary>Click to Reveal Answer</summary>

- **Load Testing**: Tests system under expected normal and peak loads to verify it meets performance requirements
- **Stress Testing**: Pushes system beyond normal capacity to find breaking points and recovery behavior
- **Endurance Testing**: Runs sustained load over extended periods (hours/days) to identify memory leaks, resource depletion, and stability issues

LoadRunner supports all three through Controller scenario configuration.
</details>

---

## Advanced (Deep Dive)

### Q16: You notice response times increasing linearly with user count while throughput plateaus. What does this pattern indicate and how would you investigate?
**Keywords:** Bottleneck, Resource Contention, Sequential Processing, Connection Pool, Database Locks
**Hint:** This pattern typically indicates a specific type of resource constraint.
<details>
<summary>Click to Reveal Answer</summary>

This pattern typically indicates:
- Resource contention (CPU, memory, or I/O bound)
- Sequential processing bottleneck (single-threaded component)
- Database lock contention
- Connection pool exhaustion
- Insufficient application server threads

Investigation steps:
1. Correlate with server metrics (CPU, memory, connections)
2. Identify the specific component using transaction breakdown
3. Analyze database queries for locks and wait times
4. Check connection pool utilization
5. Review application logs for timeout or queuing messages
</details>

---
