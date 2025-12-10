# Ignoring Tests: Conditional Test Execution in JUnit5

## Learning Objectives
- Use `@Disabled` annotation to skip tests with proper documentation
- Apply conditional annotations like `@EnabledIf`, `@EnabledOnOs`, and `@EnabledOnJre`
- Understand when skipping tests is appropriate vs. when it's a code smell
- Document disabled tests with meaningful explanations

## Why This Matters

In real-world development, you'll encounter situations where tests can't or shouldn't run—perhaps a feature is incomplete, a bug is being investigated, or the test only makes sense on certain platforms. JUnit5 provides sophisticated mechanisms for conditional test execution that go far beyond simple "ignore this test" functionality.

However, every disabled test is a potential blind spot in your quality assurance. Learning when and how to properly disable tests—and more importantly, when NOT to—is a crucial skill for maintaining a healthy test suite.

## The Concept

### The @Disabled Annotation

The simplest way to skip a test:

```java
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class FeatureTest {

    @Test
    @Disabled("Feature not yet implemented - JIRA-1234")
    void newFeature_shouldWork() {
        // Test code here
    }
}
```

**Key points:**
- Test is skipped during execution
- Reported as "skipped" in test results (not "passed" or "failed")
- The reason message is displayed in test reports

### Always Document Why

```java
// ❌ BAD: No explanation
@Disabled
void mysteryTest() { }

// ❌ BAD: Useless explanation
@Disabled("Test disabled")
void stillMystery() { }

// ✅ GOOD: Clear reason with ticket reference
@Disabled("Database migration in progress - see JIRA-5678")
void databaseIntegrationTest() { }

// ✅ GOOD: Specific reason with date
@Disabled("Flaky on CI due to timing issues - investigating since 2024-01-15")
void networkTimeoutTest() { }
```

### Disabling Entire Test Classes

```java
@Disabled("Payment gateway sandbox unavailable until contract renewal")
class PaymentGatewayTest {
    
    @Test
    void processPayment_validCard_succeeds() { }
    
    @Test
    void processPayment_expiredCard_fails() { }
    
    // All tests in this class are skipped
}
```

### Conditional Test Execution

JUnit5 provides powerful conditional annotations in `org.junit.jupiter.api.condition`:

#### @EnabledOnOs / @DisabledOnOs

```java
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

@Test
@EnabledOnOs(OS.WINDOWS)
void windowsSpecificTest() {
    // Only runs on Windows
}

@Test
@EnabledOnOs({OS.LINUX, OS.MAC})
void unixLikeSystemTest() {
    // Runs on Linux and macOS
}

@Test
@DisabledOnOs(OS.WINDOWS)
void nonWindowsTest() {
    // Runs everywhere except Windows
}
```

#### @EnabledOnJre / @DisabledOnJre

```java
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.JRE;

@Test
@EnabledOnJre(JRE.JAVA_17)
void java17SpecificFeatureTest() {
    // Only runs on Java 17
}

@Test
@EnabledOnJre({JRE.JAVA_11, JRE.JAVA_17, JRE.JAVA_21})
void ltsVersionsTest() {
    // Runs on LTS versions
}

@Test
@DisabledOnJre(JRE.JAVA_8)
void modernJavaOnlyTest() {
    // Doesn't run on Java 8
}
```

#### @EnabledForJreRange / @DisabledForJreRange

```java
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

@Test
@EnabledForJreRange(min = JRE.JAVA_11, max = JRE.JAVA_17)
void java11To17Test() {
    // Runs on Java 11, 12, 13, 14, 15, 16, 17
}

@Test
@EnabledForJreRange(min = JRE.JAVA_17)
void java17AndAboveTest() {
    // Runs on Java 17 and any newer version
}
```

#### @EnabledIfSystemProperty / @DisabledIfSystemProperty

```java
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

@Test
@EnabledIfSystemProperty(named = "env", matches = "staging")
void stagingEnvironmentTest() {
    // Only runs when -Denv=staging is set
}

@Test
@EnabledIfSystemProperty(named = "ci", matches = "true")
void ciOnlyTest() {
    // Only runs in CI environment (when -Dci=true)
}
```

#### @EnabledIfEnvironmentVariable / @DisabledIfEnvironmentVariable

```java
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@Test
@EnabledIfEnvironmentVariable(named = "TEST_DATABASE_URL", matches = ".+")
void databaseIntegrationTest() {
    // Only runs when TEST_DATABASE_URL environment variable is set
}

@Test
@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
void localOnlyTest() {
    // Doesn't run in CI environment
}
```

#### @EnabledIf / @DisabledIf (Custom Conditions)

```java
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.DisabledIf;

@Test
@EnabledIf("customCondition")
void conditionalTest() {
    // Runs if customCondition() returns true
}

// Condition method must be static or instance method returning boolean
boolean customCondition() {
    return System.getProperty("run.special.tests") != null;
}

// Using a static method
@Test
@EnabledIf("isWorkday")
void workdayOnlyTest() { }

static boolean isWorkday() {
    DayOfWeek day = LocalDate.now().getDayOfWeek();
    return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
}
```

### Assumptions: Runtime Conditional Execution

For conditions that can only be evaluated at runtime, use Assumptions:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assumptions.*;

class AssumptionTest {

    @Test
    void testOnlyOnDeveloperMachine() {
        assumeTrue("DEV".equals(System.getenv("ENVIRONMENT")),
            "Skipping - not a development environment");
        
        // Test only runs if assumption passes
        performDevOnlyTest();
    }
    
    @Test
    void testWithDatabaseConnection() {
        assumeTrue(isDatabaseAvailable(),
            "Skipping - database not available");
        
        // Test only runs if database is reachable
        performDatabaseTest();
    }
    
    @Test
    void testPartiallyConditional() {
        // Always runs this part
        String result = service.processBasicData();
        assertEquals("basic", result);
        
        // Only continues if condition met
        assumeTrue(advancedFeaturesEnabled());
        
        // This part only runs if assumption passes
        String advancedResult = service.processAdvancedData();
        assertEquals("advanced", advancedResult);
    }
    
    private boolean isDatabaseAvailable() {
        // Check database connectivity
        return true;
    }
    
    private boolean advancedFeaturesEnabled() {
        return Boolean.getBoolean("advanced.features");
    }
}
```

**Assumptions vs. Disabled:**
- `@Disabled`: Compile-time, always skipped
- `assumeTrue()`: Runtime, evaluated during execution

### When to Disable Tests (And When Not To)

#### Legitimate Reasons to Disable

```java
// 1. Feature in development
@Disabled("Feature branch FEAT-123 - remove when merged")
void upcomingFeatureTest() { }

// 2. Known bug under investigation
@Disabled("Bug BUG-456 - test exposes timing issue, fix in progress")
void flakyTimingTest() { }

// 3. External dependency unavailable
@Disabled("Third-party API deprecated - migrating to new API")
void legacyApiTest() { }

// 4. Infrastructure not ready
@Disabled("Waiting for test environment setup - OPS-789")
void productionLikeTest() { }
```

#### Red Flags: When Disabling Is a Code Smell

```java
// ❌ RED FLAG: Test has been disabled for months
@Disabled("Fails sometimes")  // Disabled since 2023-01...
void ancientDisabledTest() { }

// ❌ RED FLAG: No explanation or tracking
@Disabled
void mysteriouslyDisabledTest() { }

// ❌ RED FLAG: "Temporary" becomes permanent
@Disabled("Temporary - will fix later")  // Added 2 years ago
void permanentlyTemporaryTest() { }

// ❌ RED FLAG: Disabling instead of fixing
@Disabled("Test broke after refactoring")
void brokenButIgnoredTest() { }
```

### Best Practices for Managing Disabled Tests

```java
/**
 * DISABLED TEST MANAGEMENT CHECKLIST
 * 
 * Before disabling a test:
 * □ Is there a ticket tracking the issue?
 * □ Is there a clear reason documented?
 * □ Is there a plan to re-enable?
 * □ Has the team been notified?
 * 
 * Regularly (e.g., sprint retrospective):
 * □ Review all @Disabled tests
 * □ Remove tests that are no longer relevant
 * □ Re-enable tests where issues are fixed
 * □ Update tickets if timelines changed
 */

// Good practice: Include all context
@Disabled("""
    Feature temporarily removed due to security review.
    Ticket: SEC-2024-001
    Owner: security-team@example.com
    Expected resolution: Q2 2024
    """)
void securitySensitiveTest() { }
```

## Code Example

### Complete Conditional Execution Showcase

```java
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class ConditionalExecutionShowcase {

    // Platform-specific tests
    @Test
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("Windows file path handling")
    void windowsFilePath_handlesBackslashes() {
        String path = FileUtils.normalizePath("C:\\Users\\test");
        assertTrue(path.contains("\\"));
    }
    
    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    @DisplayName("Unix file permissions")
    void unixPermissions_areRespected() {
        // Test Unix-specific file permission handling
    }
    
    // JRE-specific tests
    @Test
    @EnabledForJreRange(min = JRE.JAVA_11)
    @DisplayName("HTTP Client (Java 11+)")
    void httpClient_java11Feature() {
        // Uses java.net.http.HttpClient introduced in Java 11
    }
    
    @Test
    @EnabledOnJre(JRE.JAVA_17)
    @DisplayName("Sealed classes (Java 17)")
    void sealedClasses_java17Feature() {
        // Tests sealed class functionality
    }
    
    // Environment-based tests
    @Test
    @EnabledIfEnvironmentVariable(named = "INTEGRATION_TESTS", matches = "true")
    @DisplayName("Integration test - requires external services")
    void integrationTest_externalService() {
        // Only runs when INTEGRATION_TESTS=true
    }
    
    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = "true")
    @DisplayName("Local development test")
    void localTest_notForCI() {
        // Skipped in CI pipelines
    }
    
    // System property-based tests
    @Test
    @EnabledIfSystemProperty(named = "test.level", matches = "full")
    @DisplayName("Extensive test - full test suite only")
    void extensiveTest_fullSuiteOnly() {
        // Run with: mvn test -Dtest.level=full
    }
    
    // Custom conditions
    @Test
    @EnabledIf("isDatabaseConfigured")
    @DisplayName("Database test - requires DB configuration")
    void databaseTest_requiresConfig() {
        // Only runs if database is configured
    }
    
    static boolean isDatabaseConfigured() {
        return System.getProperty("db.url") != null;
    }
    
    // Using assumptions for runtime conditions
    @Test
    @DisplayName("Test with runtime assumption")
    void runtimeConditionalTest() {
        assumeTrue(
            isExternalServiceAvailable(),
            "External service not available - skipping test"
        );
        
        // Test code only runs if assumption passes
        String result = externalService.call();
        assertNotNull(result);
    }
    
    private boolean isExternalServiceAvailable() {
        // Check if service is reachable
        return true;
    }
    
    // Explicitly disabled tests
    @Test
    @Disabled("JIRA-12345: Payment gateway sandbox expired, renewal pending")
    void paymentTest_sandboxUnavailable() {
        // Clearly documented why and tracked with ticket
    }
    
    @Test
    @Disabled("BUG-67890: Flaky due to race condition, fix in PR #234")
    void concurrencyTest_underInvestigation() {
        // Links to both bug tracker and fix
    }
}
```

### Conditional Annotation Combinations

```java
class CombinedConditionsTest {

    @Test
    @EnabledOnOs(OS.LINUX)
    @EnabledForJreRange(min = JRE.JAVA_17)
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    @DisplayName("Linux + Java 17+ + CI environment")
    void verySpecificConditions() {
        // All conditions must be met
    }
}
```

## Summary

- **@Disabled**: Skip tests with mandatory documentation of why
- **Platform conditions**: `@EnabledOnOs`, `@EnabledOnJre`, `@EnabledForJreRange`
- **Environment conditions**: `@EnabledIfSystemProperty`, `@EnabledIfEnvironmentVariable`
- **Custom conditions**: `@EnabledIf` with custom boolean methods
- **Runtime conditions**: Use `assumeTrue()` for conditions evaluated during execution
- **Always document** the reason for disabling and include ticket references
- **Regularly review** disabled tests—they can become permanent blind spots
- Disabled tests should be **temporary**, not permanent fixtures

## Additional Resources

- [JUnit5 Conditional Test Execution](https://junit.org/junit5/docs/current/user-guide/#writing-tests-conditional-execution) - Official guide
- [JUnit5 Assumptions](https://junit.org/junit5/docs/current/user-guide/#writing-tests-assumptions) - Runtime conditional execution
- [Managing Test Debt](https://martinfowler.com/articles/testing-culture.html) - Strategies for maintaining healthy test suites

