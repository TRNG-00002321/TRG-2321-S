# Weekly Knowledge Check: Week 9 - Load Runner

## Part 1: Multiple Choice - LoadRunner Fundamentals

### 1. What are the four main components of the LoadRunner platform?
- [ ] A) Recorder, Executor, Monitor, Reporter
- [ ] B) VuGen, Controller, Load Generator, Analysis
- [ ] C) Script Editor, Test Runner, Results Viewer, Dashboard
- [ ] D) Designer, Player, Collector, Analyzer

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) VuGen, Controller, Load Generator, Analysis

**Explanation:** LoadRunner consists of four integrated components: VuGen (Virtual User Generator) for script creation, Controller for orchestrating scenarios, Load Generators for executing virtual users, and Analysis for interpreting results.

- **Why others are wrong:**
  - A) These generic terms don't represent LoadRunner's actual component names.
  - C) These terms might apply to other testing tools but not LoadRunner's architecture.
  - D) These terms are not part of LoadRunner's official component names.
</details>

---

### 2. Which company currently owns LoadRunner after acquiring HP Software in 2017?
- [ ] A) Mercury Interactive
- [ ] B) Oracle
- [ ] C) Micro Focus
- [ ] D) IBM

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Micro Focus

**Explanation:** In 2017, HP spun off its software division, which merged with Micro Focus. LoadRunner continues as "Micro Focus LoadRunner" with ongoing development.

- **Why others are wrong:**
  - A) Mercury Interactive was the original developer (1989-2006) but was acquired by HP.
  - B) Oracle has its own testing tools but never owned LoadRunner.
  - D) IBM has Rational Performance Tester but never acquired LoadRunner.
</details>

---

### 3. How many virtual users can be generated with LoadRunner Community Edition?
- [ ] A) 10
- [ ] B) 25
- [ ] C) 50
- [ ] D) 100

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) 50

**Explanation:** LoadRunner Community Edition is free and supports up to 50 concurrent virtual users, making it suitable for learning and small-scale testing.

- **Why others are wrong:**
  - A) 10 VUsers is below the actual Community Edition limit.
  - B) 25 VUsers is below the actual Community Edition limit.
  - D) 100 VUsers exceeds the Community Edition limit; would require a paid license.
</details>

---

### 4. What protocol would you select in VuGen to test a RESTful API?
- [ ] A) Web HTTP/HTML
- [ ] B) Web Services
- [ ] C) TruClient
- [ ] D) ODBC

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Web Services

**Explanation:** The Web Services protocol is used for testing REST APIs and SOAP services in LoadRunner, as stated in the Protocol Selection Guide.

- **Why others are wrong:**
  - A) Web HTTP/HTML is for server-rendered web applications, not APIs specifically.
  - C) TruClient is for JavaScript-heavy Single Page Applications requiring browser rendering.
  - D) ODBC is for direct database testing, not API testing.
</details>

---

### 5. Which LoadRunner component is the "command center" for test execution?
- [ ] A) VuGen
- [ ] B) Controller
- [ ] C) Load Generator
- [ ] D) Analysis

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Controller

**Explanation:** The Controller is described as the "command center" for LoadRunner test execution. It coordinates multiple scripts, virtual users, and load generators into unified test scenarios.

- **Why others are wrong:**
  - A) VuGen is the IDE for creating and editing test scripts, not orchestrating tests.
  - C) Load Generators execute virtual users but don't orchestrate the overall test.
  - D) Analysis processes results after tests complete; it doesn't control execution.
</details>

---

## Part 2: Multiple Choice - Scripting and Correlation

### 6. What is the three-section structure of every VuGen script?
- [ ] A) setup, test, teardown
- [ ] B) vuser_init, Action, vuser_end
- [ ] C) begin, main, finish
- [ ] D) init, execute, cleanup

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) vuser_init, Action, vuser_end

**Explanation:** Every VuGen script follows a consistent three-section structure: `vuser_init` (executes once at start for initialization/login), `Action` (executes for each iteration with main business workflow), and `vuser_end` (executes once at end for cleanup/logout).

- **Why others are wrong:**
  - A), C), D) These are generic programming terms but not the actual LoadRunner section names.
</details>

---

### 7. What is correlation in LoadRunner?
- [ ] A) Comparing test results between runs
- [ ] B) Capturing dynamic server values for use in subsequent requests
- [ ] C) Linking multiple scripts together
- [ ] D) Synchronizing virtual users

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Capturing dynamic server values for use in subsequent requests

**Explanation:** Correlation handles dynamic values that change between sessions, such as session IDs, tokens, and CSRF values. It captures these values from server responses and parameterizes them for use in subsequent requests.

- **Why others are wrong:**
  - A) Comparing results is done in the Analysis tool, not correlation.
  - C) Linking scripts is done through scenarios in the Controller.
  - D) Synchronizing users is handled by rendezvous points, not correlation.
</details>

---

### 8. Which function is used in LoadRunner to capture dynamic values from server responses?
- [ ] A) web_find()
- [ ] B) web_reg_save_param()
- [ ] C) lr_save_param()
- [ ] D) web_capture_value()

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) web_reg_save_param()

**Explanation:** `web_reg_save_param()` (and its extended version `web_reg_save_param_ex()`) is the registration function that captures dynamic values from HTTP responses for correlation.

- **Why others are wrong:**
  - A) `web_find()` is for verification/checkpoint, not capturing values.
  - C) `lr_save_param()` doesn't exist; the correct function has `web_reg_` prefix.
  - D) `web_capture_value()` is not a valid LoadRunner function.
</details>

---

### 9. When must `web_reg_save_param()` be placed in the script?
- [ ] A) After the request that contains the dynamic value
- [ ] B) Before the request that generates the response containing the dynamic value
- [ ] C) At the beginning of the script
- [ ] D) Inside a transaction block

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Before the request that generates the response containing the dynamic value

**Explanation:** Registration functions like `web_reg_save_param()` must be placed BEFORE the request that generates the response containing the value to capture. This is because it "registers" to capture the value when the response arrives.

- **Why others are wrong:**
  - A) Placing it after would miss the response entirely.
  - C) It must be immediately before the relevant request, not at the script beginning.
  - D) Transaction blocks are for timing, not correlation placement.
</details>

---

## Part 3: True/False

### 10. True or False: In LoadRunner, "think time" simulates the pause between user actions, representing the time a real user spends reading or thinking.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** TRUE

**Explanation:** Think time simulates the pause between user actions, representing the time a real user spends reading, thinking, or typing. This makes the test more realistic by not flooding the server with instant requests.
</details>

---

### 11. True or False: TruClient protocol is best for traditional server-rendered web applications like PHP or ASP.NET.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** FALSE

**Explanation:** TruClient is best for JavaScript-heavy Single Page Applications (SPAs) like React, Angular, or Vue. For traditional server-rendered web applications (PHP, ASP.NET, JSP), the Web HTTP/HTML protocol is recommended. The decision rule is: "If your application works without JavaScript, use HTTP/HTML. If JavaScript is essential, use TruClient."
</details>

---

### 12. True or False: The 90th percentile response time means that 90% of transactions completed at or below this response time.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** TRUE

**Explanation:** The 90th percentile means 90% of transactions completed at or below this response time, representing the experience of most users. This is commonly used for SLA measurements because it represents typical user experience without being skewed by extreme outliers.
</details>

---

### 13. True or False: In a goal-oriented scenario, you explicitly define the number of virtual users to run.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** FALSE

**Explanation:** In goal-oriented scenarios, you define performance objectives (like transactions per second) and let the Controller calculate the required virtual users automatically. In contrast, manual scenarios require you to explicitly define VUser counts.
</details>

---

### 14. True or False: When throughput plateaus while response time continues to increase, it indicates a performance bottleneck has been reached.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** TRUE

**Explanation:** When throughput plateaus (stops increasing) while response time continues to increase, the system has reached a bottleneck and cannot process requests faster despite a growing queue of waiting requests. This is a key indicator that system capacity has been exceeded.
</details>

---

## Part 4: Multiple Choice - Scenario Design

### 15. What is the difference between a manual scenario and a goal-oriented scenario?
- [ ] A) Manual uses scripts, goal-oriented doesn't
- [ ] B) Manual defines VUser count explicitly, goal-oriented defines performance targets
- [ ] C) Manual is for web testing, goal-oriented is for API testing
- [ ] D) There is no difference

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Manual defines VUser count explicitly, goal-oriented defines performance targets

**Explanation:** In manual scenarios, you explicitly set the number of VUsers and schedule. In goal-oriented scenarios, you define a performance goal (like transactions per second), and LoadRunner determines the required VUsers automatically.

- **Why others are wrong:**
  - A) Both scenario types use VuGen scripts.
  - C) Both scenario types can be used for any protocol/application type.
  - D) They have significant differences in approach and control.
</details>

---

### 16. What does "ramp-up" refer to in load testing?
- [ ] A) The maximum number of users
- [ ] B) The gradual increase of virtual users over time
- [ ] C) The speed of network connections
- [ ] D) The database response time

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) The gradual increase of virtual users over time

**Explanation:** Ramp-up is the gradual addition of virtual users to simulate realistic user growth and prevent system shock from sudden load spikes. Best practice is to use gradual ramp-up because sudden load spikes don't reflect reality.

- **Why others are wrong:**
  - A) Maximum users is part of the steady state, not ramp-up.
  - C) Network speed is a separate configuration, not ramp-up.
  - D) Response time is a metric, not a configuration setting.
</details>

---

### 17. In LoadRunner Controller, what is a "Virtual User Group"?
- [ ] A) A collection of users testing the same feature
- [ ] B) A group of scripts assigned to the same load generator
- [ ] C) Users with the same think time settings
- [ ] D) A set of VUsers running the same script with the same settings

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** D) A set of VUsers running the same script with the same settings

**Explanation:** A Virtual User Group is a set of virtual users that execute the same script with the same runtime settings, representing a specific user type or workflow. Groups help organize tests by user behavior types to match production user mixes.

- **Why others are wrong:**
  - A) Too vague; groups are specifically tied to scripts and settings.
  - B) Scripts and generators are separate configurations; a group uses one script but could span generators.
  - C) Think time is one setting, but groups encompass all runtime settings.
</details>

---

### 18. Why would you use multiple Load Generators in a test?
- [ ] A) To test different applications simultaneously
- [ ] B) To generate more virtual users than a single machine can handle
- [ ] C) To reduce licensing costs
- [ ] D) To speed up script recording

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) To generate more virtual users than a single machine can handle

**Explanation:** Multiple Load Generators distribute the load generation across machines, enabling more virtual users than a single machine can handle and simulating load from different geographic locations.

- **Why others are wrong:**
  - A) Multiple applications would use multiple scenarios, not just multiple generators.
  - C) More generators typically require more licenses, not fewer.
  - D) Recording happens in VuGen, not during load generation.
</details>

---

## Part 5: Fill-in-the-Blank

### 19. The three phases of a typical load test schedule are: ramp-up, _______, and ramp-down.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** steady state

**Explanation:** The three phases are ramp-up (gradual VUser increase), steady state (all VUsers running at full capacity for measurement), and ramp-down (gradual VUser decrease). The steady state phase should be at least 30-60 minutes for meaningful data.
</details>

---

### 20. The function used to mark the beginning of a timed operation in LoadRunner is `lr_start_________("Transaction_Name")`.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** transaction

**Explanation:** `lr_start_transaction("Transaction_Name")` marks the beginning of a timed business operation. Transactions are named markers that measure response time for specific business operations, enabling business-level reporting. The matching end function is `lr_end_transaction("Transaction_Name", LR_AUTO)`.
</details>

---

### 21. The percentage of failed transactions divided by total transactions multiplied by 100 gives the _______ rate.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** error

**Explanation:** Error Rate = (Failed Transactions / Total Transactions) × 100%. Error rates indicate application reliability under load. Best practice is to keep error rates below 1%; rates above 2% may invalidate the test.
</details>

---

## Part 6: Multiple Choice - Result Analysis

### 22. What does throughput measure in LoadRunner Analysis?
- [ ] A) The number of concurrent users
- [ ] B) The amount of data transferred per unit time
- [ ] C) The response time of transactions
- [ ] D) The number of errors

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) The amount of data transferred per unit time

**Explanation:** Throughput measures the amount of data (typically in bytes or transactions) transferred between client and server per unit time. It indicates system capacity and data transfer rates.

- **Why others are wrong:**
  - A) Concurrent users are shown in the Running VUsers graph.
  - C) Response time has its own dedicated graphs.
  - D) Errors are tracked in error graphs and statistics.
</details>

---

### 23. Which graph would you use to identify when response times started degrading?
- [ ] A) Pie chart of errors
- [ ] B) Transaction Response Time graph over time
- [ ] C) Virtual User graph
- [ ] D) Throughput summary

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Transaction Response Time graph over time

**Explanation:** The Transaction Response Time graph plotted against time shows when performance degradation began and can be correlated with user count or other factors to identify root causes.

- **Why others are wrong:**
  - A) Pie charts show proportions, not time-based trends.
  - C) VUser graph shows load, not response times.
  - D) Summary doesn't show the timing of degradation.
</details>

---

### 24. What does standard deviation indicate in response time analysis?
- [ ] A) The average response time
- [ ] B) The fastest response
- [ ] C) The consistency of response times
- [ ] D) The total number of responses

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) The consistency of response times

**Explanation:** Standard deviation measures consistency of response times. Low standard deviation indicates predictable, stable performance (good user experience), while high standard deviation indicates inconsistent, variable performance that may indicate intermittent issues.

- **Why others are wrong:**
  - A) Average is the arithmetic mean, a separate metric.
  - B) Minimum response time shows the fastest response.
  - D) Count/pass rate shows total number of responses.
</details>

---

### 25. What is the purpose of the LoadRunner Analysis "merge results" feature?
- [ ] A) To combine scripts into one
- [ ] B) To compare and combine results from multiple test runs
- [ ] C) To merge multiple scenarios
- [ ] D) To consolidate error messages

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) To compare and combine results from multiple test runs

**Explanation:** Merge results allows combining results from multiple test runs for comparison and trend analysis. Use cases include validating improvements after fixes, combining distributed test data, and A/B testing different configurations.

- **Why others are wrong:**
  - A) Scripts are combined in scenarios, not through merge results.
  - C) Scenarios are designed in Controller, not Analysis.
  - D) Error consolidation is part of error graphs, not merge feature.
</details>

---

## Part 7: Multiple Choice - Troubleshooting

### 26. A script works with one user but fails with 10 users. What is the most likely cause?
- [ ] A) Network bandwidth limitation
- [ ] B) Missing correlation of dynamic values
- [ ] C) Insufficient server memory
- [ ] D) Wrong protocol selection

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Missing correlation of dynamic values

**Explanation:** Multi-user failures often indicate missing correlation. Each virtual user needs unique session values (like session IDs, CSRF tokens) that weren't parameterized. When multiple users try to use the same hardcoded session value, conflicts occur.

- **Why others are wrong:**
  - A) 10 users wouldn't typically exhaust network bandwidth.
  - C) 10 users wouldn't typically cause server memory issues.
  - D) Protocol issues would appear with single user too.
</details>

---

### 27. What should you check first if LoadRunner cannot connect to the Load Generator?
- [ ] A) Script syntax
- [ ] B) Firewall settings and network connectivity
- [ ] C) Think time configuration
- [ ] D) Transaction names

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Firewall settings and network connectivity

**Explanation:** Load Generator connectivity issues are typically caused by firewall blocking the required ports (54345, 443, 50500-50600) or network configuration problems. First verify the agent service is running, then check network connectivity and firewall rules.

- **Why others are wrong:**
  - A) Script syntax doesn't affect generator connectivity.
  - C) Think time is a script setting, not connectivity related.
  - D) Transaction names are script elements, not connectivity related.
</details>

---

### 28. Which type of testing runs sustained load over extended periods to identify memory leaks?
- [ ] A) Load Testing
- [ ] B) Stress Testing
- [ ] C) Spike Testing
- [ ] D) Endurance Testing

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** D) Endurance Testing

**Explanation:** Endurance (or soak) testing runs sustained load over extended periods (hours/days) to identify memory leaks, resource depletion, and stability issues that only appear over time.

- **Why others are wrong:**
  - A) Load testing validates expected production load but typically runs for shorter periods.
  - B) Stress testing pushes systems beyond capacity to find breaking points.
  - C) Spike testing simulates sudden traffic surges.
</details>

---

### 29. What does error "HTTP Status-Code=500" typically indicate?
- [ ] A) Authentication failure
- [ ] B) Resource not found
- [ ] C) Server-side error
- [ ] D) Request timeout

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Server-side error

**Explanation:** HTTP 500 (Internal Server Error) indicates a server-side problem. This could be caused by invalid data submitted, server-side bugs, or load-induced failures. Solution involves verifying request payload, checking server logs, and potentially reducing load to isolate the issue.

- **Why others are wrong:**
  - A) Authentication failure is HTTP 401 (Unauthorized).
  - B) Resource not found is HTTP 404.
  - D) Request timeout is typically HTTP 408 or 504.
</details>

---

## Part 8: Code Prediction

### 30. What will this script section do?

```c
lr_start_transaction("Login");

web_submit_data("LoginRequest",
    "Action=https://app.example.com/api/login",
    "Method=POST",
    ITEMDATA,
    "Name=username", "Value={Username}", ENDITEM,
    "Name=password", "Value={Password}", ENDITEM,
    LAST);

lr_end_transaction("Login", LR_AUTO);
```

- [ ] A) Record a new login script
- [ ] B) Submit login credentials and measure the response time of the operation
- [ ] C) Create a new user account
- [ ] D) Validate login page HTML

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Submit login credentials and measure the response time of the operation

**Explanation:** This code starts a transaction named "Login" to measure timing, submits login credentials via POST to the login API using parameterized values ({Username} and {Password}), then ends the transaction with LR_AUTO which automatically determines pass/fail based on HTTP response.

- **Why others are wrong:**
  - A) Recording is done via VuGen's recording feature, not script code.
  - C) This is a login submission, not account creation.
  - D) There's no validation/checkpoint function in this code.
</details>

---

### 31. What is the purpose of placing `web_reg_save_param()` in this position?

```c
web_reg_save_param("sessionId",
    "LB=sessionId\":\"",
    "RB=\"",
    LAST);

web_submit_data("Login",
    "Action=https://app.example.com/login",
    ...);
```

- [ ] A) To save the session ID before sending the login request
- [ ] B) To capture the session ID from the login response
- [ ] C) To validate the session ID exists
- [ ] D) To delete the old session ID

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) To capture the session ID from the login response

**Explanation:** `web_reg_save_param()` is a registration function that must be placed BEFORE the request. It registers to capture a value (sessionId) from the response that will be returned by the subsequent `web_submit_data` call. The "LB" (left boundary) and "RB" (right boundary) define the pattern to match.

- **Why others are wrong:**
  - A) The session ID comes FROM the response, not before the request.
  - C) Validation uses different functions like `web_reg_find()`.
  - D) This captures a value, not deletes it.
</details>

---

## Part 9: Multiple Choice - Bottleneck Identification

### 32. What is a performance bottleneck?
- [ ] A) A bug in the test script
- [ ] B) A component that limits overall system performance
- [ ] C) A network connection error
- [ ] D) A failed transaction

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) A component that limits overall system performance

**Explanation:** A bottleneck is a component that limits overall system performance. Like a narrow section of pipe that restricts water flow, a bottleneck prevents the system from achieving higher throughput or faster response times.

- **Why others are wrong:**
  - A) Script bugs are errors, not bottlenecks.
  - C) Connection errors are failures, not bottlenecks.
  - D) Failed transactions are symptoms, not the bottleneck itself.
</details>

---

### 33. Which pattern indicates a database connection pool bottleneck?
- [ ] A) Response time gradually increases throughout the test
- [ ] B) Response time suddenly spikes when connections reach maximum and "Cannot acquire connection" errors appear
- [ ] C) All response times are consistently slow
- [ ] D) Response times spike every few minutes in a regular pattern

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Response time suddenly spikes when connections reach maximum and "Cannot acquire connection" errors appear

**Explanation:** Connection pool exhaustion shows a specific pattern: response time suddenly spikes when the pool reaches maximum capacity, and errors like "Cannot acquire connection" appear. The solution involves increasing pool size, reducing connection hold time, or fixing connection leaks.

- **Why others are wrong:**
  - A) Gradual increase suggests memory leak pattern.
  - C) Consistently slow times suggest network latency issues.
  - D) Regular spikes suggest garbage collection pauses.
</details>

---

### 34. What does a "sawtooth" pattern in memory usage graphs typically indicate?
- [ ] A) Normal garbage collection behavior
- [ ] B) Memory leak
- [ ] C) Database bottleneck
- [ ] D) Network latency

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** A) Normal garbage collection behavior

**Explanation:** A sawtooth pattern (memory rises then drops sharply, repeatedly) indicates normal garbage collection. Memory grows as objects are created, then drops when GC runs. This is healthy behavior unless the GC pauses cause significant response time spikes.

- **Why others are wrong:**
  - B) Memory leaks show continuous growth without returning to baseline.
  - C) Database bottlenecks affect query times, not memory patterns.
  - D) Network latency affects response times, not memory usage.
</details>

---

### 35. What does TTFB (Time to First Byte) measure?
- [ ] A) Total page load time
- [ ] B) Time from request sent to first byte of response received
- [ ] C) Time to download all page resources
- [ ] D) Database query execution time

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Time from request sent to first byte of response received

**Explanation:** TTFB (Time to First Byte) measures the time from when a request is sent to when the first byte of the response is received. High TTFB typically indicates server-side issues (slow database queries, application processing, or overloaded server).

- **Why others are wrong:**
  - A) Total page load includes content download after TTFB.
  - C) Resource download time is measured separately.
  - D) Database time is a component of server processing that affects TTFB.
</details>

---

## Part 10: True/False - Advanced Concepts

### 36. True or False: In LoadRunner, parameterization replaces static values with dynamic data to make tests more realistic.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** TRUE

**Explanation:** Parameterization replaces static (hardcoded) values with dynamic data, making scripts more realistic. For example, instead of all 1,000 VUsers searching for "laptop," each can search for different products using parameterized data from a file.
</details>

---

### 37. True or False: The vuser_init section of a VuGen script executes for each iteration of the test.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** FALSE

**Explanation:** The `vuser_init` section executes only ONCE per virtual user at the START of the VUser lifecycle (typically for login/initialization). The `Action` section executes for EACH iteration. The `vuser_end` section executes only ONCE at the END (typically for logout/cleanup).
</details>

---

### 38. True or False: LoadRunner supports over 50 protocols including SAP, Citrix, and legacy systems.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** TRUE

**Explanation:** LoadRunner's extensive protocol support (50+ protocols) is a key differentiator. Protocol categories include Web, Web Services, Mobile, Enterprise (SAP, Oracle, PeopleSoft), Virtualization (Citrix, RDP), Database, Messaging, and Legacy systems.
</details>

---

### 39. True or False: Error rates above 2% typically indicate the test results may be invalid.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** TRUE

**Explanation:** Error rate thresholds indicate: below 0.1% is excellent, 0.1-0.5% is acceptable, 0.5-1% is warning, 1-2% is critical, and above 2% is unacceptable - the test may be invalid and should be stopped to fix issues.
</details>

---

### 40. True or False: LoadRunner Cloud is a SaaS offering that eliminates the need to manage load generator infrastructure.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** TRUE

**Explanation:** LoadRunner Cloud is the SaaS offering that eliminates infrastructure management. Benefits include no infrastructure to manage, global load generation locations, elastic scale to millions of VUsers on demand, pay-per-use pricing, and CI/CD integration APIs.
</details>

---

## Part 11: Scenario-Based Questions

### 41. You're analyzing results and notice that response times for the "Checkout" transaction increased from 2 seconds to 8 seconds when the test reached 300 VUsers. CPU usage stayed at 25%, but database connections reached 100 (maximum pool size). What is the most likely bottleneck?

- [ ] A) CPU bottleneck
- [ ] B) Network latency
- [ ] C) Database connection pool exhaustion
- [ ] D) Memory leak

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** C) Database connection pool exhaustion

**Explanation:** The symptoms match connection pool exhaustion: response time suddenly spiked at a specific VUser count, CPU was not saturated (25%), and database connections reached maximum (100). Solutions include increasing pool size, reducing connection hold time, and fixing potential connection leaks.

- **Why others are wrong:**
  - A) CPU at 25% indicates CPU is not the bottleneck.
  - B) Network latency would show in all requests, not correlate with connection count.
  - D) Memory leaks show gradual degradation over time, not sudden spike at user count.
</details>

---

### 42. During a 4-hour endurance test, you observe that response times gradually increase over time while memory usage continuously grows and never returns to baseline. What type of issue does this pattern indicate?

- [ ] A) Garbage collection problem
- [ ] B) Memory leak
- [ ] C) Database lock contention
- [ ] D) Network congestion

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) Memory leak

**Explanation:** The memory leak pattern shows: performance degrades over time (not with load), memory continuously grows and never returns to baseline, and eventually leads to OutOfMemory errors. Unlike GC (sawtooth pattern), leaked memory isn't reclaimed.

- **Why others are wrong:**
  - A) GC shows sawtooth pattern with memory returning to baseline.
  - C) Database locks cause sporadic spikes, not gradual degradation.
  - D) Network congestion wouldn't affect memory usage patterns.
</details>

---

### 43. Your test script plays back successfully with 1 VUser but fails with "Error -26377: No match found for the requested parameter" when running with 5 VUsers. What is the most likely cause?

- [ ] A) The parameter file doesn't have enough rows
- [ ] B) The correlation boundaries have changed and need updating
- [ ] C) The load generator is overloaded
- [ ] D) Network timeout occurred

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** B) The correlation boundaries have changed and need updating

**Explanation:** Error -26377 indicates a correlation failure where the expected text pattern wasn't found in the response. With multiple VUsers, each gets different dynamic values, and if correlation boundaries are too specific or the application response changed, the capture fails.

- **Why others are wrong:**
  - A) Parameter file issues cause "No more values" errors, not -26377.
  - C) Generator overload would cause different errors (timeouts, resource errors).
  - D) Network timeout has different error codes.
</details>

---

## Part 12: Fill-in-the-Blank (Advanced)

### 44. In LoadRunner Analysis, filtering to focus only on the period when all VUsers are running at full capacity is called filtering to the _______ _______ period.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** steady state

**Explanation:** Steady state is the phase where the full number of virtual users are active, providing consistent load to measure system performance. Filtering to steady state excludes ramp-up (system warming up) and ramp-down periods for more accurate analysis.
</details>

---

### 45. The default port used by LoadRunner Agent for communication between Controller and Load Generator is _______.

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answer:** 54345

**Explanation:** Port 54345 is used for Agent communication between Controller and Load Generators. Additional ports include 443 for secure channel communication and 50500-50600 for data collection.
</details>

---

## Part 13: Matching Concepts

### 46. Match the HTTP error code to its meaning:

| Error Code | Meaning |
|------------|---------|
| 401 | ? |
| 403 | ? |
| 404 | ? |
| 500 | ? |

Options: A) Not Found, B) Forbidden, C) Server Error, D) Unauthorized

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answers:**
- 401 = D) Unauthorized (authentication required, credentials missing/invalid)
- 403 = B) Forbidden (access denied, CSRF/permission issues)
- 404 = A) Not Found (resource/URL doesn't exist, dynamic URL issue)
- 500 = C) Server Error (server-side bug, invalid data, load failure)

**Explanation:** Understanding HTTP status codes is essential for troubleshooting LoadRunner scripts. Each code indicates a different category of problem requiring different solutions.
</details>

---

### 47. Match the LoadRunner component to its primary function:

| Component | Function |
|-----------|----------|
| VuGen | ? |
| Controller | ? |
| Load Generator | ? |
| Analysis | ? |

Options: A) Execute virtual users, B) Process and visualize results, C) Create test scripts, D) Orchestrate test scenarios

<details>
<summary><b>🔎 Click for Solution</b></summary>

**Correct Answers:**
- VuGen = C) Create test scripts (IDE for recording, editing, debugging)
- Controller = D) Orchestrate test scenarios (command center for execution)
- Load Generator = A) Execute virtual users (workhorses running VUsers)
- Analysis = B) Process and visualize results (graphs, reports, insights)

**Explanation:** Understanding each component's role is fundamental to effective LoadRunner usage and troubleshooting.
</details>

---

## Scoring Guide

| Score Range | Performance Level |
|-------------|-------------------|
| 43-47 (91-100%) | Excellent - Ready for advanced LoadRunner work |
| 38-42 (81-90%) | Good - Solid understanding with minor gaps |
| 33-37 (70-80%) | Satisfactory - Review weak areas before proceeding |
| Below 33 (<70%) | Needs Improvement - Re-study written content |

---

*Generated by Practice Quiz Agent for Week 9: Load Runner*
