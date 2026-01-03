# Testing Fundamentals Interview Questions & Answers

## Beginner Level

**Q1: What is software testing and why is it important?**

> **Answer:** Software testing is the process of evaluating a software application to find defects and verify it meets requirements.
>
> **Importance:**
> - **Quality:** Ensures software works correctly
> - **Reliability:** Builds confidence in the system
> - **Cost savings:** Finding bugs early is cheaper than fixing in production
> - **User satisfaction:** Prevents poor user experience
> - **Security:** Identifies vulnerabilities before release
>
> **Cost of bugs by phase:**
> - Requirements: $1
> - Development: $10
> - Testing: $100
> - Production: $1,000+

---

**Q2: Explain the difference between QA and QC.**

> **Answer:**
>
> | Aspect | QA (Quality Assurance) | QC (Quality Control) |
> |--------|----------------------|---------------------|
> | Focus | Process-oriented | Product-oriented |
> | Goal | Prevent defects | Detect defects |
> | Timing | Throughout SDLC | After development |
> | Activities | Process audits, standards | Testing, inspection |
> | Question | "Are we building it right?" | "Does it meet specs?" |
>
> **Examples:**
> - **QA:** Implementing code review process, establishing coding standards
> - **QC:** Executing test cases, finding bugs in the application

---

**Q3: What are the different levels of testing?**

> **Answer:**
>
> **Testing Pyramid:**
> ```
>        /\         E2E Tests (few)
>       /  \        
>      /----\       Integration Tests (some)
>     /      \      
>    /--------\     Unit Tests (many)
> ```
>
> | Level | Scope | Who | Speed |
> |-------|-------|-----|-------|
> | **Unit** | Functions, methods | Developers | Very fast |
> | **Integration** | Component interactions | Dev/QA | Medium |
> | **System** | Complete application | QA | Slower |
> | **Acceptance** | Business requirements | Users/QA | Slowest |
>
> **Example:**
> ```
> Unit: Test calculateTotal() returns correct sum
> Integration: Test OrderService creates order in database
> System: Test complete checkout flow
> Acceptance: Verify user can purchase items as specified
> ```

---

**Q4: Explain black-box vs white-box testing.**

> **Answer:**
>
> **Black-Box Testing:**
> - Test without knowing internal code
> - Based on requirements/specifications
> - Focus on inputs and outputs
> - Done by QA testers
>
> **White-Box Testing:**
> - Test with knowledge of internal code
> - Based on code structure
> - Focus on code paths and logic
> - Done by developers
>
> | Aspect | Black-Box | White-Box |
> |--------|-----------|-----------|
> | Knowledge | External behavior | Internal structure |
> | Techniques | EP, BVA, decision tables | Statement, branch coverage |
> | Finds | Functional defects | Logic errors, dead code |
> | Done by | Testers | Developers |
>
> **Gray-Box:** Combination of both (some internal knowledge).

---

**Q5: What are the 7 principles of software testing (ISTQB)?**

> **Answer:**
>
> 1. **Testing shows presence of defects**
>    - Can prove bugs exist, not prove absence
>
> 2. **Exhaustive testing is impossible**
>    - Can't test every combination; use risk-based approach
>
> 3. **Early testing saves time and money**
>    - Shift left; find bugs in requirements, not production
>
> 4. **Defects cluster together**
>    - 80% of bugs in 20% of modules; focus efforts
>
> 5. **Pesticide paradox**
>    - Same tests find fewer bugs over time; refresh tests
>
> 6. **Testing is context dependent**
>    - Approach varies (medical software vs. game)
>
> 7. **Absence of errors fallacy**
>    - Bug-free ≠ meets user needs; validation matters

---

## Intermediate Level

**Q6: Explain Equivalence Partitioning and Boundary Value Analysis.**

> **Answer:** Test design techniques to reduce test cases while maximizing coverage.
>
> **Equivalence Partitioning (EP):**
> - Divide inputs into groups with same expected behavior
> - Test one value per partition
>
> **Boundary Value Analysis (BVA):**
> - Test values at partition boundaries
> - Bugs often occur at edges
>
> **Example: Age field (valid: 18-65)**
>
> **EP Partitions:**
> | Partition | Range | Test Value |
> |-----------|-------|------------|
> | Invalid low | < 18 | 10 |
> | Valid | 18-65 | 40 |
> | Invalid high | > 65 | 80 |
>
> **BVA Values:**
> - 17, 18, 19 (lower boundary)
> - 64, 65, 66 (upper boundary)
>
> **Combined test cases:** 17, 18, 40, 65, 66 (covers both techniques)

---

**Q7: Describe the defect lifecycle.**

> **Answer:**
>
> ```
> NEW → ASSIGNED → OPEN → FIXED → RETEST → VERIFIED → CLOSED
>                   ↓                 ↓
>               REJECTED          REOPENED
> ```
>
> | Status | Owner | Description |
> |--------|-------|-------------|
> | New | Tester | Just reported |
> | Assigned | Lead | Given to developer |
> | Open | Developer | Under investigation |
> | Fixed | Developer | Code change complete |
> | Retest | Tester | Verifying fix |
> | Verified | Tester | Fix confirmed working |
> | Closed | Lead | Issue resolved |
> | Rejected | Developer | Not a bug / as designed |
> | Reopened | Tester | Fix didn't work |
>
> **Good bug report includes:**
> - Clear title
> - Steps to reproduce
> - Expected vs actual result
> - Environment details
> - Screenshots/logs
> - Severity and priority

---

**Q8: What is the difference between severity and priority?**

> **Answer:**
>
> **Severity:** Technical impact on the system (set by tester)
> **Priority:** Business urgency to fix (set by stakeholder)
>
> | | High Severity | Low Severity |
> |-|---------------|--------------|
> | **High Priority** | Critical bug blocking users | Typo on homepage (visible) |
> | **Low Priority** | Crash in rarely used feature | Minor UI issue in settings |
>
> **Examples:**
> - **High Severity, Low Priority:** App crashes when clicking obsolete button nobody uses
> - **Low Severity, High Priority:** Company name misspelled on login page
>
> **Severity levels:**
> 1. Critical - System crash, data loss
> 2. Major - Feature unusable
> 3. Minor - Feature works with issues
> 4. Trivial - Cosmetic issues

---

**Q9: Explain regression testing and when it's needed.**

> **Answer:** Regression testing ensures that new changes haven't broken existing functionality.
>
> **When needed:**
> - After bug fixes
> - After new feature development
> - After code refactoring
> - After environment changes
> - Before releases
>
> **Types:**
> - **Corrective:** Re-run unchanged tests
> - **Progressive:** Update tests for new requirements
> - **Selective:** Run subset of tests (risk-based)
> - **Complete:** Run entire test suite
>
> **Best practices:**
> - Automate regression tests
> - Prioritize critical paths
> - Run in CI/CD pipeline
> - Keep tests maintainable
>
> **Example:**
> ```
> Sprint 1: Login, Dashboard (tests created)
> Sprint 2: Add Profile feature
>   → Regression: Run Login + Dashboard tests
>   → New tests: Profile tests
> Sprint 3: Bug fix in Login
>   → Regression: All tests
> ```

---

**Q10: What is a test plan and what does it contain?**

> **Answer:** A test plan is a document describing the testing approach, scope, resources, and schedule.
>
> **Key sections:**
>
> 1. **Introduction**
>    - Purpose, scope, objectives
>
> 2. **Test Items**
>    - Features to be tested
>    - Features not being tested (and why)
>
> 3. **Testing Approach**
>    - Types of testing
>    - Test levels
>    - Tools to be used
>
> 4. **Entry/Exit Criteria**
>    - Conditions to start testing
>    - Conditions to complete testing
>
> 5. **Test Environment**
>    - Hardware, software, network
>    - Test data requirements
>
> 6. **Resources**
>    - Team roles
>    - Training needs
>
> 7. **Schedule**
>    - Milestones
>    - Dependencies
>
> 8. **Risks and Mitigation**
>    - Potential issues
>    - Contingency plans
>
> 9. **Deliverables**
>    - Test cases, reports, documentation

---

## Advanced Level

**Q11: Explain different testing methodologies (TDD, BDD, ATDD).**

> **Answer:**
>
> **TDD (Test-Driven Development):**
> - Write test first, then code to pass it
> - Red → Green → Refactor cycle
> - Focus: Technical correctness
>
> ```java
> // 1. Write failing test (Red)
> @Test
> void shouldAddTwoNumbers() {
>     assertEquals(5, calculator.add(2, 3));
> }
> 
> // 2. Write minimal code to pass (Green)
> public int add(int a, int b) {
>     return a + b;
> }
> 
> // 3. Refactor if needed
> ```
>
> **BDD (Behavior-Driven Development):**
> - Describe behavior in natural language
> - Given-When-Then format
> - Focus: Business behavior
>
> ```gherkin
> Scenario: Successful login
>   Given I am on the login page
>   When I enter valid credentials
>   Then I should see the dashboard
> ```
>
> **ATDD (Acceptance Test-Driven Development):**
> - Define acceptance criteria first
> - Team collaborates on tests
> - Focus: User requirements
>
> | Aspect | TDD | BDD | ATDD |
> |--------|-----|-----|------|
> | Focus | Unit code | Behavior | Acceptance |
> | Audience | Developers | All | Business + Dev |
> | Language | Code | Natural | Natural |

---

**Q12: How do you create an effective test strategy?**

> **Answer:**
>
> **1. Understand Context:**
> - Project type, risks, constraints
> - Regulatory requirements
> - Team capabilities
>
> **2. Define Test Levels:**
> ```
> Unit (70%) → Integration (20%) → E2E (10%)
> ```
>
> **3. Select Test Types:**
> - Functional: Feature verification
> - Performance: Load, stress testing
> - Security: Vulnerability testing
> - Usability: UX verification
>
> **4. Prioritize with Risk Analysis:**
> ```
> Risk = Probability × Impact
> 
> High Risk → More testing
> Low Risk → Less testing
> ```
>
> **5. Choose Tools:**
> | Purpose | Tools |
> |---------|-------|
> | Unit | JUnit, pytest |
> | API | Postman, REST Assured |
> | UI | Selenium, Playwright |
> | Performance | JMeter, LoadRunner |
>
> **6. Define Metrics:**
> - Test coverage
> - Defect density
> - Pass rate
> - Defect leakage rate
>
> **7. Ensure Sustainability:**
> - Automate regression
> - Integrate with CI/CD
> - Maintain test documentation
