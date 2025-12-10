# Lab: Allure Java Integration - Enhancing JUnit5 Tests

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Beginner-Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | allure.md, demo_allure_junit5_setup.java |

## Learning Objectives
By completing this exercise, you will:
- Add Allure dependencies to a Maven project
- Annotate existing JUnit5 tests with Allure annotations
- Use `@Epic`, `@Feature`, `@Story` for test organization
- Add `@Description` and `@Severity` for documentation
- Generate and view Allure reports

## The Scenario

Your team has a working test suite for a `UserService`, but stakeholders complain the test reports are "just green/red checkboxes." You've been tasked with adding Allure reporting to make the test results professional and informative.

## Core Tasks

### Task 1: Add Allure Dependencies (10 minutes)

Update `pom.xml`:

```xml
<properties>
    <allure.version>2.24.0</allure.version>
    <aspectj.version>1.9.20</aspectj.version>
</properties>

<dependencies>
    <!-- Allure JUnit5 Integration -->
    <dependency>
        <groupId>io.qameta.allure</groupId>
        <artifactId>allure-junit5</artifactId>
        <version>${allure.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.1.2</version>
            <configuration>
                <argLine>
                    -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/${aspectj.version}/aspectjweaver-${aspectj.version}.jar"
                </argLine>
                <systemPropertyVariables>
                    <allure.results.directory>${project.build.directory}/allure-results</allure.results.directory>
                </systemPropertyVariables>
            </configuration>
            <dependencies>
                <dependency>
                    <groupId>org.aspectj</groupId>
                    <artifactId>aspectjweaver</artifactId>
                    <version>${aspectj.version}</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

### Task 2: Add Epic/Feature/Story Annotations (15 minutes)

Organize your tests hierarchically:

```java
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@Epic("User Management")
@Feature("User Registration")
class UserRegistrationTest {

    @Test
    @Story("User can register with valid email")
    @Description("Tests that a user with a valid email can successfully register")
    @Severity(SeverityLevel.CRITICAL)
    void registerUser_validEmail_succeeds() {
        // Test implementation
    }
    
    @Test
    @Story("User cannot register with duplicate email")
    @Description("Tests that duplicate email registration is rejected")
    @Severity(SeverityLevel.NORMAL)
    void registerUser_duplicateEmail_fails() {
        // Test implementation
    }
}
```

**Your Task:** Add these annotations to the provided `UserServiceTest.java`:
- Group tests under appropriate Epics and Features
- Add Stories for each test method
- Set appropriate severity levels

### Task 3: Add Descriptions and Links (10 minutes)

Enhance tests with documentation:

```java
@Test
@Description("""
    This test verifies the password reset flow:
    1. User requests password reset
    2. System sends reset email
    3. User clicks reset link
    4. User enters new password
    """)
@Link(name = "User Story", url = "https://jira.company.com/browse/USER-123")
@Issue("BUG-456")
@TmsLink("TC-789")
void passwordReset_completeFlow_succeeds() {
    // Test implementation
}
```

### Task 4: Add Steps with @Step (15 minutes)

Break down complex tests into steps:

```java
@Test
@Description("Complete user registration with email verification")
void registerUser_withEmailVerification_completesSuccessfully() {
    User user = createUser("test@example.com");
    sendVerificationEmail(user);
    verifyEmail(user);
    assertUserIsActive(user);
}

@Step("Create user with email: {email}")
User createUser(String email) {
    User user = new User(email);
    userService.register(user);
    return user;
}

@Step("Send verification email to user")
void sendVerificationEmail(User user) {
    emailService.sendVerification(user.getEmail());
}

@Step("Verify user email")
void verifyEmail(User user) {
    userService.verifyEmail(user.getVerificationToken());
}

@Step("Assert user is now active")
void assertUserIsActive(User user) {
    assertTrue(userService.findById(user.getId()).isActive());
}
```

### Task 5: Generate and View Report (10 minutes)

Run tests and generate report:

```bash
# Run tests
mvn clean test

# Generate report (requires allure command-line)
allure serve target/allure-results

# Or generate static report
allure generate target/allure-results -o target/allure-report --clean
```

## Provided Test Class to Enhance

```java
// UserServiceTest.java - ADD ALLURE ANNOTATIONS
class UserServiceTest {
    
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userService = new UserService();
    }
    
    @Test
    void createUser_validData_returnsUser() {
        User user = userService.createUser("John", "john@test.com");
        assertNotNull(user.getId());
        assertEquals("John", user.getName());
    }
    
    @Test
    void createUser_nullName_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(null, "test@test.com");
        });
    }
    
    @Test
    void createUser_invalidEmail_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("John", "invalid");
        });
    }
    
    @Test
    void getUser_existingUser_returnsUser() {
        User created = userService.createUser("Jane", "jane@test.com");
        User found = userService.getUser(created.getId());
        assertEquals(created, found);
    }
    
    @Test
    void getUser_nonExistingUser_throwsException() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(99999L);
        });
    }
    
    @Test
    void updateUser_validData_updatesUser() {
        User user = userService.createUser("Old", "old@test.com");
        user.setName("New");
        userService.updateUser(user);
        
        User updated = userService.getUser(user.getId());
        assertEquals("New", updated.getName());
    }
    
    @Test
    void deleteUser_existingUser_removesUser() {
        User user = userService.createUser("ToDelete", "delete@test.com");
        userService.deleteUser(user.getId());
        
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(user.getId());
        });
    }
}
```

## Allure Annotations Reference

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@Epic` | Top-level grouping | `@Epic("User Management")` |
| `@Feature` | Feature grouping | `@Feature("Registration")` |
| `@Story` | User story | `@Story("Email validation")` |
| `@Description` | Test description | `@Description("Detailed desc")` |
| `@Severity` | Test importance | `@Severity(SeverityLevel.CRITICAL)` |
| `@Step` | Test step | `@Step("Click login button")` |
| `@Link` | External link | `@Link(url = "...")` |
| `@Issue` | Bug reference | `@Issue("BUG-123")` |
| `@TmsLink` | Test case reference | `@TmsLink("TC-456")` |

## Definition of Done

- [ ] Allure dependencies added to `pom.xml`
- [ ] All test methods have `@Story` and `@Description`
- [ ] At least 2 `@Epic` and 3 `@Feature` annotations used
- [ ] Severity levels set for all tests
- [ ] At least 1 test with `@Step` methods
- [ ] At least 1 test with `@Link` or `@Issue`
- [ ] Report generated successfully
- [ ] All tests pass

## Submission

Commit with message:
```
feat(week6): Complete Allure Java integration exercise
```

