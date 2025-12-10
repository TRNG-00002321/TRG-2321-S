# Re-Testing: Confirming Bug Fixes

## Learning Objectives
- Define re-testing and distinguish it from regression testing
- Understand the re-testing workflow and confirmation testing process
- Apply re-testing practices when verifying bug fixes
- Know when to combine re-testing with regression testing

## Why This Matters

When a developer says "bug fixed," how do you know it's really fixed? Re-testing provides the verification—running the exact test that originally found the defect to confirm it no longer fails. This confirmation step is crucial for quality assurance and prevents the embarrassment of "fixed" bugs that reappear.

## The Concept

### What is Re-Testing?

**Re-testing** (also called **confirmation testing**) is executing the specific test case that originally found a defect to verify the defect has been fixed.

```
┌─────────────────────────────────────────────────────────────┐
│                    RE-TESTING WORKFLOW                       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  1. Tester finds bug using Test Case X                       │
│  2. Developer fixes the bug                                  │
│  3. Tester runs Test Case X again  ← RE-TESTING              │
│  4. If pass → Bug confirmed fixed                            │
│     If fail → Bug NOT fixed, back to developer               │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Re-Testing vs. Regression Testing

| Aspect | Re-Testing | Regression Testing |
|--------|------------|-------------------|
| **Purpose** | Verify specific bug is fixed | Verify fix didn't break other things |
| **Scope** | Narrow: specific test case | Broad: many test cases |
| **Trigger** | Bug fix delivered | Any code change |
| **Test Cases** | Original failing test | Suite of passing tests |
| **Question** | "Is THIS bug fixed?" | "Is EVERYTHING ELSE still working?" |

**They complement each other:**
```
Bug Fix Delivered
       │
       ├─► Re-Testing: Run the original failing test
       │                 └─► Confirms the fix works
       │
       └─► Regression Testing: Run related test suite
                         └─► Confirms fix didn't break other things
```

### The Re-Testing Process

1. **Identify the test case** that found the original defect
2. **Ensure same environment** as when bug was found
3. **Execute the exact steps** from the original test
4. **Verify expected behavior** now occurs
5. **Document the result** (pass/fail)
6. **Close or reopen** the defect accordingly

### When Re-Testing Fails

If re-testing fails, the bug is not fixed:

```
Re-Test Failed
      │
      ├─► Reopen the defect ticket
      ├─► Provide clear reproduction steps
      ├─► Include actual vs expected results
      └─► Return to developer
```

### Re-Testing Best Practices

```java
/**
 * RE-TESTING CHECKLIST
 * 
 * Before re-testing:
 * □ Bug fix is deployed to test environment
 * □ Test environment matches original conditions
 * □ Original test case/steps are documented
 * 
 * During re-testing:
 * □ Follow exact steps from original test
 * □ Don't deviate from original scenario
 * □ Use same test data if applicable
 * 
 * After re-testing:
 * □ Document pass/fail result
 * □ If failed, provide evidence
 * □ Update defect status appropriately
 */
```

## Code Example

### Automated Re-Testing

```java
@Tag("bug-fix")
@Tag("JIRA-1234")  // Link to defect ticket
class BugFix1234ReTest {

    @Test
    @DisplayName("JIRA-1234: Division by zero should throw exception, not return infinity")
    void divide_byZero_throwsException() {
        Calculator calc = new Calculator();
        
        // Original bug: returned Infinity instead of throwing
        assertThrows(ArithmeticException.class, () -> {
            calc.divide(10, 0);
        });
        
        // Bug is fixed when this test passes
    }
}

// Run re-tests: mvn test -Dgroups="bug-fix"
```

### Combining Re-Test with Regression

```java
@Test
@Tag("retest")      // Re-test for the fix
@Tag("regression")  // Add to regression suite permanently
void originalBugScenario() {
    // This test:
    // 1. Confirms the bug is fixed (re-test)
    // 2. Prevents bug from returning (regression)
}
```

## Summary

- **Re-testing** confirms a specific bug fix works
- It uses the **original failing test** that found the bug
- Different from **regression testing**, which checks for unintended impacts
- **Both are needed**: re-test to confirm fix, regression to check side effects
- Failed re-tests mean **bug is not fixed**—return to developer
- Add important bug fix tests to the **regression suite** to prevent recurrence

## Additional Resources

- [ISTQB: Confirmation Testing](https://glossary.istqb.org/en/term/confirmation-testing) - Standard definition
- [Re-Testing vs Regression Testing](https://www.guru99.com/re-testing-vs-regression-testing.html) - Detailed comparison
- [Defect Lifecycle](https://www.softwaretestinghelp.com/defect-life-cycle/) - Bug tracking workflow

