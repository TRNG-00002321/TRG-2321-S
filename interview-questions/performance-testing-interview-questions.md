# Performance Testing Interview Questions (JMeter & LoadRunner)

## Beginner Level

**Q1: What is performance testing and what are its types?**

> **Answer:** Performance testing evaluates system behavior under various conditions.
>
> **Types:**
> | Type | Purpose | Example |
> |------|---------|---------|
> | Load Testing | Expected user load | 1000 concurrent users |
> | Stress Testing | Beyond capacity | Find breaking point |
> | Spike Testing | Sudden traffic spike | Flash sale simulation |
> | Endurance/Soak | Sustained load | 24-hour test |
> | Scalability | Growth handling | Double load every hour |

---

**Q2: What are key performance metrics to measure?**

> **Answer:**
> | Metric | Description | Good Target |
> |--------|-------------|-------------|
> | Response Time | Time to complete request | < 3 seconds |
> | Throughput | Requests per second | Per SLA |
> | Error Rate | % of failed requests | < 1% |
> | Concurrent Users | Simultaneous users | Per requirement |
> | CPU Usage | Server processor | < 80% |
> | Memory Usage | RAM consumption | < 80% |
> | 90th Percentile | 90% requests under this time | < 5 seconds |

---

**Q3: What is Apache JMeter and its architecture?**

> **Answer:** JMeter is an open-source performance testing tool.
>
> **Components:**
> - **Test Plan:** Root container for all elements
> - **Thread Group:** Virtual users configuration
> - **Samplers:** Requests (HTTP, JDBC, FTP, etc.)
> - **Logic Controllers:** Control flow
> - **Listeners:** Results and reporting
> - **Assertions:** Response validation
> - **Timers:** Delays between requests
> - **Config Elements:** Default settings

---

**Q4: What is LoadRunner and its components?**

> **Answer:** LoadRunner is an enterprise performance testing tool by Micro Focus.
>
> **Components:**
> | Component | Purpose |
> |-----------|---------|
> | VuGen | Script creation and editing |
> | Controller | Test design and execution |
> | Load Generator | Hosts virtual users |
> | Analysis | Results analysis and reporting |
>
> **Protocols:** Web, SAP, Oracle, Citrix, Mobile, API, etc.

---

**Q5: How do you create a basic JMeter test plan?**

> **Answer:**
> ```
> Test Plan
> └── Thread Group
>     ├── Number of Threads: 50 (users)
>     ├── Ramp-up Period: 60 (seconds)
>     ├── Loop Count: 10
>     │
>     ├── HTTP Request Defaults
>     │   └── Server: api.example.com
>     │
>     ├── HTTP Request (GET /api/users)
>     ├── HTTP Request (POST /api/orders)
>     │
>     ├── Response Assertion
>     │   └── Response Code: 200
>     │
>     ├── Constant Timer (1000 ms)
>     │
>     └── View Results Tree
> ```

---

## Intermediate Level

**Q6: How do you parameterize data in JMeter?**

> **Answer:**
> **CSV Data Set Config:**
> ```
> Filename: testdata.csv
> Variable Names: username,password,email
> Delimiter: ,
> Recycle on EOF: True
> ```
>
> **CSV file:**
> ```
> user1,pass1,user1@test.com
> user2,pass2,user2@test.com
> user3,pass3,user3@test.com
> ```
>
> **Usage in request:**
> ```
> Username: ${username}
> Password: ${password}
> Email: ${email}
> ```
>
> **User Defined Variables:**
> - Use for environment-specific values
> - hostname, port, protocol

---

**Q7: How do you correlate dynamic values?**

> **Answer:** Extract dynamic values from responses for subsequent requests.
>
> **JMeter - Regular Expression Extractor:**
> ```
> Apply to: Main sample only
> Reference Name: sessionId
> Regular Expression: "sessionId":"(.+?)"
> Template: $1$
> Match No: 1
> Default: NOT_FOUND
> ```
>
> **JMeter - JSON Extractor:**
> ```
> Variable names: token
> JSON Path: $.data.accessToken
> ```
>
> **LoadRunner:**
> ```c
> web_reg_save_param("sessionId",
>     "LB=\"sessionId\":\"",
>     "RB=\"",
>     LAST);
> 
> web_submit_data("Login",
>     "Action=https://api.example.com/login",
>     LAST);
> 
> // Use in next request
> lr_output_message("Session: %s", lr_eval_string("{sessionId}"));
> ```

---

**Q8: What are JMeter Listeners and which ones should you use?**

> **Answer:**
> | Listener | Purpose | Memory Impact |
> |----------|---------|---------------|
> | View Results Tree | Debug requests/responses | High |
> | Summary Report | Overall statistics | Low |
> | Aggregate Report | Detailed statistics | Medium |
> | Response Time Graph | Visual trends | Medium |
> | Simple Data Writer | Save raw results | Low |
>
> **Best practices:**
> - Disable View Results Tree for load tests
> - Use Summary Report during execution
> - Save results to JTL file for analysis
> - Generate HTML report after test

---

**Q9: How do you write LoadRunner scripts?**

> **Answer:**
> ```c
> // LoadRunner VuGen Script Structure
> 
> vuser_init() {
>     // Login, setup (runs once per user)
>     web_url("Login Page",
>         "URL=https://example.com/login",
>         LAST);
> }
> 
> Action() {
>     // Main business transaction (runs in loop)
>     
>     lr_start_transaction("Search_Product");
>     
>     web_submit_data("Search",
>         "Action=https://example.com/search",
>         ITEMDATA,
>         "Name=query", "Value={SearchTerm}", ENDITEM,
>         LAST);
>     
>     // Check response
>     web_reg_find("Text=Results",
>         LAST);
>     
>     lr_end_transaction("Search_Product", LR_AUTO);
>     
>     lr_think_time(3);  // User think time
>     
>     return 0;
> }
> 
> vuser_end() {
>     // Logout, cleanup (runs once per user)
>     web_url("Logout",
>         "URL=https://example.com/logout",
>         LAST);
> }
> ```

---

**Q10: How do you handle authentication in performance tests?**

> **Answer:**
> **JMeter - HTTP Authorization Manager:**
> - Add to Thread Group
> - Configure Base URL, Username, Password
> - Supports Basic, Digest, Kerberos
>
> **JMeter - Bearer Token:**
> ```
> HTTP Header Manager:
> - Name: Authorization
> - Value: Bearer ${access_token}
> ```
>
> **Extract token from login:**
> ```
> 1. HTTP Request (POST /login)
> 2. JSON Extractor: $.access_token -> access_token
> 3. HTTP Header Manager: Bearer ${access_token}
> 4. Subsequent requests use the token
> ```

---

## Advanced Level

**Q11: How do you analyze performance test results?**

> **Answer:**
> **Key analysis points:**
> 
> 1. **Response Time Distribution:**
>    - Average vs Median
>    - 90th/95th percentiles
>    - Outliers identification
>
> 2. **Throughput Analysis:**
>    - Requests per second trend
>    - Peak vs sustained
>
> 3. **Error Analysis:**
>    - Error types and patterns
>    - When errors started
>    - Correlation with load
>
> 4. **Resource Correlation:**
>    - Response time vs CPU
>    - Response time vs user count
>    - Identify bottlenecks
>
> **JMeter HTML Report:**
> ```bash
> jmeter -g results.jtl -o report_folder
> ```

---

**Q12: How do you run JMeter in CI/CD pipelines?**

> **Answer:**
> ```bash
> # Command line execution
> jmeter -n -t test.jmx -l results.jtl -e -o report
> 
> # Parameters
> # -n: Non-GUI mode
> # -t: Test file
> # -l: Results file
> # -e: Generate report
> # -o: Output folder
> ```
>
> **Jenkins Pipeline:**
> ```groovy
> pipeline {
>     stages {
>         stage('Performance Test') {
>             steps {
>                 sh '''
>                     jmeter -n \
>                         -t tests/load-test.jmx \
>                         -l results/results.jtl \
>                         -Jusers=100 \
>                         -Jduration=300
>                 '''
>             }
>             post {
>                 always {
>                     perfReport 'results/results.jtl'
>                 }
>             }
>         }
>     }
> }
> ```

---

**Q13: What are common performance bottlenecks?**

> **Answer:**
> | Layer | Bottleneck | Symptoms | Solution |
> |-------|------------|----------|----------|
> | Database | Slow queries | High DB CPU | Index, optimize |
> | Application | Memory leaks | Growing memory | Code profiling |
> | Application | Thread pool | Queuing | Increase pool |
> | Network | Bandwidth | Slow transfers | CDN, compression |
> | Server | CPU bound | 100% CPU | Scale up/out |
> | Server | I/O wait | Disk bottleneck | SSD, caching |

---

**Q14: How do you design a performance test strategy?**

> **Answer:**
> 1. **Define Objectives:**
>    - Target response time (< 3 sec)
>    - Concurrent users (1000)
>    - Throughput (100 req/sec)
>
> 2. **Identify Scenarios:**
>    - Critical user journeys
>    - High-traffic pages
>    - Peak usage patterns
>
> 3. **Prepare Environment:**
>    - Production-like setup
>    - Monitoring tools
>    - Test data
>
> 4. **Execute Tests:**
>    - Baseline (single user)
>    - Load test (expected)
>    - Stress test (breaking point)
>    - Endurance (8+ hours)
>
> 5. **Analyze & Report:**
>    - Compare against SLAs
>    - Identify bottlenecks
>    - Recommendations

---

**Q15: What is the difference between JMeter and LoadRunner?**

> **Answer:**
> | Aspect | JMeter | LoadRunner |
> |--------|--------|------------|
> | Cost | Free, open-source | Licensed, expensive |
> | Protocols | HTTP, JDBC, JMS, etc. | 50+ protocols |
> | Scripting | GUI + Java/Groovy | C-based (VuScript) |
> | Enterprise | Community support | Professional support |
> | Reporting | Basic + plugins | Advanced analytics |
> | Learning | Easier | Steeper curve |
> | Scalability | Good | Excellent |
>
> **When to use JMeter:** Open-source projects, web/API testing, budget constraints
> **When to use LoadRunner:** Enterprise applications, complex protocols, mission-critical systems
