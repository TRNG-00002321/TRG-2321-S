# QA Engineer Training - Scenario-Based Interview Questions

This document contains practical scenario-based questions that test problem-solving skills and real-world application of concepts. These questions are organized by week and assess how candidates approach actual challenges.

---

## **Week 1: Git & Python Fundamentals**

### Scenario 1: Git Merge Conflict Resolution
> **Situation:** You're working on a feature branch and try to merge it into the main branch. Git reports conflicts in 3 files. Your teammate made changes to the same functions you modified.
>
> **Question:** Walk me through how you would resolve these conflicts and ensure both changes are properly integrated.

**Expected Answer Points:**
- Pull the latest main branch changes
- Identify conflict markers in each file (`<<<<<<<`, `=======`, `>>>>>>>`)
- Communicate with teammate to understand their changes
- Carefully merge both sets of changes, preserving functionality
- Test the merged code before committing
- Use `git add` for resolved files and complete the merge commit
- Consider using visual merge tools like VS Code or GitKraken

---

### Scenario 2: Python Debugging
> **Situation:** A Python script that processes customer data is throwing a `TypeError: cannot unpack non-iterable NoneType object` on line 45. The function works for most customers but fails for some.
>
> **Question:** How would you debug this issue?

**Expected Answer Points:**
- Add print statements or use debugger to inspect data at line 45
- Check what function returns None vs expected tuple/list
- Look for edge cases (missing data, null values in source)
- Add defensive checks: `if result is not None`
- Trace back to find source of None value
- Add input validation or handle None case explicitly
- Consider using try-except with specific error handling

---

### Scenario 3: Virtual Environment Issue
> **Situation:** A colleague cloned your Python project but gets `ModuleNotFoundError` for several packages, even though they ran `pip install -r requirements.txt`.
>
> **Question:** What could be causing this and how would you help them resolve it?

**Expected Answer Points:**
- Check if virtual environment is activated
- Verify they created a new virtual environment first
- Ensure correct Python version is being used
- Check if pip is installing to the right environment (`which pip`)
- Verify requirements.txt has all dependencies listed
- Consider transitive dependencies that might be missing
- Suggest using `pip freeze` to compare environments

---

## **Week 2: Python Advanced & Java Introduction**

### Scenario 4: Flask API Error
> **Situation:** Your Flask API endpoint returns `500 Internal Server Error` intermittently. The logs show the error happens during database queries, but not consistently.
>
> **Question:** How would you investigate and fix this issue?

**Expected Answer Points:**
- Review application logs for specific error messages
- Check database connection pool settings (connections exhausted?)
- Look for race conditions or connection timeouts
- Add proper exception handling around database operations
- Implement connection retry logic
- Consider database query optimization
- Add health checks and monitoring

---

### Scenario 5: Java Application Won't Compile
> **Situation:** A Java project that worked yesterday now fails to compile with "cannot find symbol" errors for several classes.
>
> **Question:** What steps would you take to diagnose and fix this?

**Expected Answer Points:**
- Check if pom.xml dependencies were modified
- Run `mvn clean` to clear compiled classes
- Verify Java version compatibility (JDK version)
- Check for missing imports in affected files
- Look for recently deleted or renamed classes
- Ensure Maven dependencies are downloaded (`mvn dependency:resolve`)
- Check for IDE cache issues (invalidate caches)

---

### Scenario 6: Maven Dependency Conflict
> **Situation:** After adding a new library to your Maven project, existing tests start failing with `NoSuchMethodError` at runtime.
>
> **Question:** What is likely happening and how would you resolve it?

**Expected Answer Points:**
- This is likely a dependency version conflict
- Use `mvn dependency:tree` to view all dependencies
- Look for different versions of the same library
- Use `<exclusions>` to remove conflicting transitive dependencies
- Or use `<dependencyManagement>` to enforce specific versions
- Test thoroughly after resolving conflicts
- Consider using Maven Enforcer Plugin to prevent future conflicts

---

## **Week 3: Java Advanced Concepts**

### Scenario 7: NullPointerException in Production
> **Situation:** A Java application crashes in production with `NullPointerException` in a service method. The same code works in the test environment.
>
> **Question:** How would you approach debugging this production-specific issue?

**Expected Answer Points:**
- Review stack trace to identify exact location
- Compare production vs test environment configurations
- Check for differences in data (production may have edge cases)
- Look for environment-specific configurations or properties
- Add null checks and defensive programming
- Consider using Optional for potentially null values
- Add logging to capture state before the crash
- Review recent deployments for changes

---

### Scenario 8: Collection Performance Issue
> **Situation:** A method that searches through a list of 100,000 users by email is taking 5+ seconds. The application is receiving complaints about slow response times.
>
> **Question:** How would you optimize this?

**Expected Answer Points:**
- Current linear search is O(n) - too slow for large datasets
- Use HashMap with email as key for O(1) lookups
- Or use HashSet if only checking existence
- Consider caching frequently accessed data
- If data is from database, move search to database query
- Add index on email column in database
- Profile the code to confirm the bottleneck

---

### Scenario 9: Thread Safety Bug
> **Situation:** A multi-user web application occasionally shows User A's data to User B. The bug is rare and hard to reproduce.
>
> **Question:** What might be causing this and how would you fix it?

**Expected Answer Points:**
- This is likely a thread safety/concurrency issue
- Check for shared mutable state (class-level variables)
- Look for static variables holding user data
- Review singleton beans in Spring (are services stateless?)
- User data should be in request scope, not shared
- Add synchronized blocks or use thread-safe collections if needed
- Consider ThreadLocal for request-specific data
- Add logging with thread IDs to track the issue

---

## **Week 4: SQL & Database Concepts**

### Scenario 10: Slow Database Query
> **Situation:** A report query that used to run in 2 seconds now takes 2 minutes. The table has grown from 50,000 to 5 million rows.
>
> **Question:** How would you diagnose and improve the query performance?

**Expected Answer Points:**
- Use EXPLAIN/EXPLAIN ANALYZE to see query execution plan
- Check if indexes exist on columns in WHERE clause
- Look for full table scans
- Add appropriate indexes (but not too many)
- Review query for optimization opportunities (avoid SELECT *)
- Consider query restructuring (remove unnecessary JOINs)
- Look at database statistics and vacuum/analyze tables
- Consider partitioning for very large tables

---

### Scenario 11: Data Integrity Issue
> **Situation:** Users report that some orders in the system have no associated customer records. This shouldn't be possible based on business rules.
>
> **Question:** What went wrong and how would you prevent this in the future?

**Expected Answer Points:**
- Foreign key constraint may be missing or disabled
- Check if constraint was bypassed during data import
- Look for application code that doesn't enforce referential integrity
- Add/enable foreign key constraint on orders table
- Use ON DELETE CASCADE or ON DELETE RESTRICT as appropriate
- Review data import and migration scripts
- Add database-level checks in addition to application validation

---

### Scenario 12: SQL Injection Vulnerability
> **Situation:** During a security review, you find this code: `query = "SELECT * FROM users WHERE username = '" + input + "'"`. How would you explain the risk and fix it?
>
> **Question:** Demonstrate what could go wrong and provide the correct solution.

**Expected Answer Points:**
- Attacker could input: `' OR '1'='1` to bypass authentication
- Or use: `'; DROP TABLE users;--` for destructive attacks
- Fix: Use PreparedStatement with parameterized queries:
```java
PreparedStatement stmt = conn.prepareStatement(
    "SELECT * FROM users WHERE username = ?");
stmt.setString(1, input);
```
- Never concatenate user input into SQL strings
- Also apply input validation as defense in depth

---

## **Week 5: Agile & Testing Fundamentals**

### Scenario 13: Sprint Planning Disagreement
> **Situation:** During sprint planning, the development team estimates a user story at 13 points, but the product owner insists it should only be 5 points to fit in the sprint.
>
> **Question:** How should this situation be handled?

**Expected Answer Points:**
- Story points are the team's estimate, not negotiable by PO
- PO cannot reduce points; can only negotiate scope
- Options: break story into smaller pieces, defer to next sprint
- Scrum Master should facilitate discussion
- Focus on what can realistically be delivered
- Consider if story can be simplified while meeting core requirements
- Document assumptions and risks

---

### Scenario 14: Incomplete Test Coverage
> **Situation:** You join a project with 10% test coverage. Management wants 80% coverage in one sprint.
>
> **Question:** How would you approach this situation?

**Expected Answer Points:**
- 80% in one sprint is likely unrealistic; set expectations
- Prioritize critical paths and business logic first
- Focus on quality of tests, not just coverage numbers
- Start with new code (prevent further debt)
- Identify high-risk areas using bug history
- Create a phased plan over multiple sprints
- Balance feature development with testing debt
- Use risk-based testing approach

---

### Scenario 15: Requirements Ambiguity
> **Situation:** A user story says "Users should be able to search products quickly." What's wrong with this requirement and how would you address it?
>
> **Question:** Improve this requirement and explain your reasoning.

**Expected Answer Points:**
- "Quickly" is ambiguous - not measurable
- Missing acceptance criteria
- Improved version:
  - "Search results return within 2 seconds for up to 1M products"
  - "Search supports product name, SKU, and description"
  - "Results are paginated (20 per page)"
  - "Partial matches are supported"
- Work with PO to clarify requirements upfront
- Use INVEST criteria for good user stories

---

## **Week 6: Unit Testing**

### Scenario 16: Testing Legacy Code
> **Situation:** You need to add unit tests to a 500-line method that creates database connections, calls external APIs, and sends emails.
>
> **Question:** How would you approach testing this method?

**Expected Answer Points:**
- First, identify what to test (business logic vs dependencies)
- Extract dependencies into separate classes/interfaces
- Use mocking to isolate external dependencies
- Break the method into smaller, testable units
- Use dependency injection to provide mock implementations
- Start with characterization tests to capture current behavior
- Gradually refactor while tests prevent regressions
- Consider Seams (points where you can alter behavior)

---

### Scenario 17: Flaky Test Investigation
> **Situation:** A unit test passes 90% of the time but fails randomly. It tests an order processing function.
>
> **Question:** What could cause this flakiness and how would you fix it?

**Expected Answer Points:**
- Possible causes:
  - Race conditions or timing issues
  - Shared mutable state between tests
  - Dependency on external systems or time
  - Random test data that occasionally hits edge cases
  - Test order dependency
- Investigation steps:
  - Run test in isolation
  - Check for static variables
  - Look for date/time dependencies
  - Review setup/teardown methods
  - Add logging to capture failure conditions
- Fix by isolating the test properly

---

### Scenario 18: Mock vs Real Database
> **Situation:** A junior developer asks why they should mock the database in unit tests when "testing with the real database would be more realistic."
>
> **Question:** Explain the trade-offs and when each approach is appropriate.

**Expected Answer Points:**
- Unit tests should be fast, isolated, and repeatable
- Mocking benefits:
  - Tests run in milliseconds, not seconds
  - No database setup required
  - Tests don't fail due to database issues
  - Can test error conditions easily
- Real database appropriate for:
  - Integration tests
  - Testing actual SQL queries
  - Repository layer tests
- Use both in a proper test pyramid
- Mocks for unit tests; real DB for integration tests

---

## **Week 7: Integration Testing & Selenium**

### Scenario 19: API Test Failure
> **Situation:** Your API tests pass locally but fail in CI/CD with timeout errors. The same tests worked last week.
>
> **Question:** How would you troubleshoot this?

**Expected Answer Points:**
- Check CI/CD environment resources (memory, CPU)
- Compare local vs CI environment configurations
- Look for recent infrastructure changes
- Check network connectivity in CI environment
- Review if dependent services are available in CI
- Increase timeout temporarily to gather more info
- Add retries with exponential backoff
- Check for resource contention with parallel tests
- Review CI logs for additional context

---

### Scenario 20: Selenium Element Not Found
> **Situation:** A Selenium test fails with `NoSuchElementException` for a login button. The button exists on the page when you check manually.
>
> **Question:** What could be causing this and how would you fix it?

**Expected Answer Points:**
- Page may not be fully loaded when test runs
- Element may be dynamically loaded via JavaScript
- Element may be in an iframe
- Locator strategy may be incorrect or fragile
- Solutions:
  - Add explicit wait for element to be clickable
  - Check for iframes and switch if needed
  - Use more robust locators (id, data-testid vs xpath)
  - Add page load wait before interaction
  - Check for overlaying elements

---

### Scenario 21: Test Data Management
> **Situation:** Your E2E tests create test users but don't clean them up. After running tests for months, the test database has 50,000 test users affecting performance.
>
> **Question:** How would you redesign the test data strategy?

**Expected Answer Points:**
- Implement proper cleanup in teardown/after hooks
- Use unique identifiers for test data (timestamp, UUID prefix)
- Create batch cleanup scripts to run periodically
- Consider test data factories that track created data
- Use database transactions that rollback after tests
- For E2E: Use dedicated test environments that reset
- Implement data retention policies
- Consider using fixtures or seeded data that resets

---

## **Week 8: Selenium & System Testing**

### Scenario 22: Cucumber Step Definition Reuse
> **Situation:** You have 50 feature files and notice many duplicate step definitions. "When I click the submit button" appears in 30 different step definition files.
>
> **Question:** How would you refactor this for better maintainability?

**Expected Answer Points:**
- Create shared step definition classes
- Organize steps by domain (login steps, navigation steps)
- Use parameterized steps for variations
- Implement Page Object Model for UI interactions
- Keep steps at business level, not technical level
- Create common steps library
- Use dependency injection for shared state
- Document step patterns for team to follow

---

### Scenario 23: BDD Scenario Design
> **Situation:** A business analyst writes this scenario:
> ```gherkin
> Scenario: Login
>   Given I open Chrome browser
>   And I navigate to www.example.com
>   And I click on login link
>   And I enter username in username field
>   And I click submit
>   Then I should see dashboard
> ```
> **Question:** What's wrong with this scenario and how would you improve it?

**Expected Answer Points:**
- Too implementation-focused (browser, click details)
- Doesn't describe business behavior
- Improved version:
```gherkin
Scenario: Successful login with valid credentials
  Given a registered user exists
  When the user logs in with valid credentials
  Then the user should see their dashboard
```
- Scenarios should be readable by non-technical stakeholders
- Hide technical details in step definitions
- Focus on WHAT, not HOW

---

### Scenario 24: System Test Environment
> **Situation:** System tests that pass in staging consistently fail in the pre-production environment, even though both should be "identical."
>
> **Question:** What differences should you investigate?

**Expected Answer Points:**
- Configuration differences (connection strings, API keys)
- Data differences (pre-prod may have more realistic data)
- Infrastructure differences (CPU, memory, network latency)
- Integration points (different external service endpoints)
- Security settings (certificates, firewall rules)
- Compare environment variables
- Check database schema versions
- Review deployed application versions
- Document and version control all environment configs

---

## **Week 9: LoadRunner**

### Scenario 25: Performance Baseline Establishment
> **Situation:** You're asked to performance test a new application, but there are no existing performance requirements or benchmarks.
>
> **Question:** How would you establish performance baselines and requirements?

**Expected Answer Points:**
- Gather business context (expected users, peak times)
- Research industry standards for similar applications
- Run baseline tests with current system
- Define SLAs with stakeholders:
  - Response time thresholds (e.g., 95th percentile < 2s)
  - Throughput requirements
  - Error rate thresholds
- Consider user experience expectations
- Document and get stakeholder sign-off
- Plan for growth (test at 2x expected load)

---

### Scenario 26: Performance Degradation Investigation
> **Situation:** Load testing shows that response times are acceptable at 100 users but degrade exponentially at 200 users. CPU is only at 40%.
>
> **Question:** What could be causing this bottleneck?

**Expected Answer Points:**
- Not CPU-bound, so look elsewhere
- Common non-CPU bottlenecks:
  - Database connection pool exhausted
  - Thread pool size limits
  - Network bandwidth saturation
  - Memory pressure causing garbage collection
  - Lock contention in code
  - External service rate limiting
- Investigation steps:
  - Monitor database connections and query times
  - Check application thread dumps
  - Review network metrics
  - Analyze memory usage patterns
  - Profile application under load

---

### Scenario 27: Realistic Load Script
> **Situation:** A developer creates a load test that hammers the same endpoint repeatedly with no think time or variation.
>
> **Question:** What's wrong with this approach and how should it be improved?

**Expected Answer Points:**
- Not realistic user behavior simulation
- Problems:
  - No think time between actions
  - No variety in user paths
  - May trigger rate limiting or caching skew
  - Doesn't represent real user distribution
- Improvements:
  - Add think times matching real user behavior
  - Create multiple scenarios (browse, search, purchase)
  - Parameterize data to avoid cache hits
  - Distribute load across different endpoints
  - Include realistic ramp-up period
  - Mix of user types and actions

---

## **Week 10: AWS & DevOps**

### Scenario 28: EC2 Instance Unreachable
> **Situation:** You launched an EC2 instance and installed a web application, but you can't access it from your browser.
>
> **Question:** What would you check to troubleshoot this?

**Expected Answer Points:**
- Security Group rules - is port 80/443 open for inbound?
- Is the application running? (SSH in and check)
- Is the correct IP being used? (public IP, not private)
- Network ACL rules at subnet level
- Route table configuration
- Is the instance in a public subnet?
- Does the instance have an Internet Gateway path?
- Check application logs for binding errors

---

### Scenario 29: Docker Container Crash
> **Situation:** A Docker container starts and immediately exits. `docker ps` shows nothing running.
>
> **Question:** How would you debug this?

**Expected Answer Points:**
- Check container logs: `docker logs <container_id>`
- Use `docker ps -a` to see all containers including stopped
- Check exit code: `docker inspect <container_id>`
- Verify the CMD/ENTRYPOINT in Dockerfile
- Run interactively to debug: `docker run -it <image> /bin/sh`
- Check if required environment variables are set
- Verify dependencies/files are present in image
- Check for port conflicts or resource limits

---

### Scenario 30: Jenkins Pipeline Failure
> **Situation:** A Jenkins pipeline that was working yesterday now fails at the "Build" stage with an obscure error message.
>
> **Question:** How would you approach troubleshooting?

**Expected Answer Points:**
- Review the full Jenkins console output
- Check for recent changes to Jenkinsfile or application code
- Verify build agent has required tools/versions
- Check for environment changes (credentials expired?)
- Try running build commands locally
- Review Jenkins system logs for infrastructure issues
- Check disk space on Jenkins agent
- Verify network access to repositories/dependencies
- Look for recent Jenkins/plugin updates

---

### Scenario 31: CI/CD Security Breach Concern
> **Situation:** A developer accidentally commits API keys to a public repository. The keys were used in a Jenkins pipeline.
>
> **Question:** What immediate actions should be taken?

**Expected Answer Points:**
- Immediately rotate/revoke the exposed credentials
- Remove credentials from repository history (git filter-branch)
- Check audit logs for unauthorized access
- Review Jenkins credential management
- Use Jenkins Credentials plugin for secrets
- Implement pre-commit hooks to detect secrets
- Add tools like git-secrets or gitleaks to CI
- Conduct team training on secrets management
- Review all credentials that might be compromised

---

## **Week 11: AI Testing & Prompt Engineering**

### Scenario 32: AI Model Bias Detection
> **Situation:** An AI hiring assistance tool is flagged for potentially discriminatory behavior. You're asked to test for bias.
>
> **Question:** What testing approach would you use?

**Expected Answer Points:**
- Counterfactual testing - change protected attributes only
- Create test datasets with balanced demographics
- Compare acceptance rates across groups
- Test with historically biased language patterns
- Evaluate fairness metrics:
  - Demographic parity
  - Equal opportunity
  - Calibration across groups
- Document and report findings
- Include diverse perspectives in test design
- Regular monitoring in production

---

### Scenario 33: Prompt Engineering Optimization
> **Situation:** An AI assistant is giving inconsistent answers to the same question. Sometimes it's accurate, sometimes completely wrong.
>
> **Question:** How would you improve the prompt to get consistent results?

**Expected Answer Points:**
- Make the prompt more specific and structured
- Use few-shot examples showing desired format
- Add context constraints
- Implement chain-of-thought prompting for complex tasks
- Temperature settings may need adjustment
- Add output format requirements
- Test with multiple phrasings
- Consider breaking complex prompts into steps
- Add validation of outputs

---

### Scenario 34: AI System Test Coverage
> **Situation:** You're asked to create a test plan for an AI chatbot that handles customer support queries.
>
> **Question:** What types of testing would you include?

**Expected Answer Points:**
- Functional testing:
  - Happy path conversations
  - Edge cases and unusual inputs
  - Multi-turn conversation handling
- Robustness testing:
  - Typos and misspellings
  - Different languages/accents
  - Adversarial inputs
- Bias and fairness testing
- Performance under load
- Fallback behavior (when AI can't help)
- Human handoff testing
- Integration with backend systems
- User experience testing
- Compliance and safety testing

---

## **Cross-Cutting Scenarios**

### Scenario 35: Production Incident Response
> **Situation:** It's Friday at 5 PM. Users report the application is extremely slow. You're the only QA person available.
>
> **Question:** What's your incident response approach?

**Expected Answer Points:**
- Acknowledge the incident, notify stakeholders
- Check monitoring dashboards for obvious issues
- Verify the problem (reproduce if possible)
- Check recent deployments (rollback candidate?)
- Gather data: error rates, response times, logs
- Escalate to appropriate team members
- Communicate updates regularly
- Document timeline of actions
- After resolution: schedule post-mortem
- QA role: verify fix, update test cases

---

### Scenario 36: Test Automation ROI
> **Situation:** Management questions the value of test automation, noting that automated tests take longer to write than manual tests.
>
> **Question:** How would you justify the investment in test automation?

**Expected Answer Points:**
- One-time creation cost vs repeated execution savings
- Calculate: (manual test time × number of executions) vs automation cost
- Faster feedback in CI/CD pipelines
- Overnight/weekend execution capability
- Consistent execution (no human error)
- Enables frequent releases
- Frees QA for exploratory testing
- Regression safety net for refactoring
- Documentation of expected behavior
- Show metrics: test execution time, defects caught

---

### Scenario 37: Quality Metrics Dashboard
> **Situation:** Your team has no visibility into quality metrics. You're asked to propose key metrics to track.
>
> **Question:** What metrics would you recommend and why?

**Expected Answer Points:**
- Defect metrics:
  - Defect density (defects per KLOC)
  - Defect escape rate (prod vs pre-prod)
  - Time to detect/fix defects
- Test metrics:
  - Test coverage (code, requirements)
  - Test pass/fail rates
  - Test execution time
- Process metrics:
  - Cycle time
  - Deployment frequency
  - Change failure rate
- Balance leading vs lagging indicators
- Avoid vanity metrics
- Align with business goals

---

### Scenario 38: Technical Debt Prioritization
> **Situation:** The test suite has 500 failing tests that have been ignored for months. Some are actual bugs, some are outdated tests.
>
> **Question:** How would you approach this technical debt?

**Expected Answer Points:**
- Triage tests into categories:
  - Real failures (product bugs)
  - Outdated tests (deprecated features)
  - Flaky tests
  - Environment issues
- Stop the bleeding (prevent new ignored failures)
- Prioritize by:
  - Business criticality
  - Frequency of feature usage
  - Risk assessment
- Create a burn-down plan
- Track progress weekly
- Delete tests that provide no value
- Fix or quarantine flaky tests
- Get team commitment to address

---

## Tips for Answering Scenario Questions

1. **Take your time** - It's okay to pause and think through the problem
2. **Ask clarifying questions** - Show you understand context matters
3. **Think out loud** - Explain your reasoning process
4. **Consider trade-offs** - Show you understand there's rarely a perfect solution
5. **Draw from experience** - Relate to similar situations you've faced
6. **Prioritize** - Start with the most critical actions
7. **Follow up** - Mention what you'd do to prevent the issue in the future
