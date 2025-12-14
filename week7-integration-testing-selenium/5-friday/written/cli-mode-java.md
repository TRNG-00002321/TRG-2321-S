# Running Selenium Tests from Command Line

## Learning Objectives
- Execute Selenium tests using Maven and Gradle from CLI
- Configure test execution with command-line parameters
- Run specific tests, test classes, or test suites
- Set up headless mode for CI/CD environments
- Integrate tests with continuous integration pipelines

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, tests need to run in various environments—developer machines, CI/CD servers, and automated pipelines. Command-line execution enables this flexibility.

Mastering CLI test execution ensures your tests can be integrated into build pipelines for continuous testing and validation.

## Maven Test Execution

### Basic Test Execution

```bash
# Run all tests
mvn test

# Run tests with clean build
mvn clean test

# Skip compilation, run tests only
mvn test -DskipCompile=true
```

### Running Specific Tests

```bash
# Run single test class
mvn test -Dtest=LoginTest

# Run single test method
mvn test -Dtest=LoginTest#testValidLogin

# Run multiple test classes
mvn test -Dtest=LoginTest,RegistrationTest

# Run tests matching pattern
mvn test -Dtest=*Test

# Run tests in specific package
mvn test -Dtest=com.example.tests.*
```

### Using Maven Surefire Plugin

```xml
<!-- pom.xml -->
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.1.2</version>
            <configuration>
                <includes>
                    <include>**/*Test.java</include>
                    <include>**/*Tests.java</include>
                </includes>
                <excludes>
                    <exclude>**/Abstract*.java</exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Passing System Properties

```bash
# Set browser type
mvn test -Dbrowser=chrome

# Set base URL
mvn test -Dbase.url=https://staging.example.com

# Set headless mode
mvn test -Dheadless=true

# Multiple properties
mvn test -Dbrowser=firefox -Dheadless=true -Dbase.url=https://test.example.com
```

### Reading Properties in Tests

```java
public class TestConfig {
    
    public static String getBrowser() {
        return System.getProperty("browser", "chrome");
    }
    
    public static String getBaseUrl() {
        return System.getProperty("base.url", "https://example.com");
    }
    
    public static boolean isHeadless() {
        return Boolean.parseBoolean(System.getProperty("headless", "false"));
    }
}

// Usage in test setup
@BeforeEach
void setUp() {
    String browser = TestConfig.getBrowser();
    boolean headless = TestConfig.isHeadless();
    
    ChromeOptions options = new ChromeOptions();
    if (headless) {
        options.addArguments("--headless=new");
    }
    
    driver = new ChromeDriver(options);
    driver.get(TestConfig.getBaseUrl());
}
```

## Gradle Test Execution

### Basic Test Execution

```bash
# Run all tests
gradle test

# Clean and test
gradle clean test

# Run with info output
gradle test --info
```

### Running Specific Tests

```bash
# Run single test class
gradle test --tests LoginTest

# Run single test method
gradle test --tests LoginTest.testValidLogin

# Run tests matching pattern
gradle test --tests '*Test'

# Run tests in package
gradle test --tests 'com.example.tests.*'
```

### Gradle Configuration

```groovy
// build.gradle
test {
    useJUnitPlatform()
    
    // Pass system properties
    systemProperty 'browser', System.getProperty('browser', 'chrome')
    systemProperty 'headless', System.getProperty('headless', 'false')
    
    // Configure test logging
    testLogging {
        events "passed", "skipped", "failed"
        showExceptions true
        showCauses true
        showStackTraces true
    }
}
```

### Passing Properties with Gradle

```bash
# Using -D
gradle test -Dbrowser=firefox -Dheadless=true

# Using -P (project properties)
gradle test -Pbrowser=firefox -Pheadless=true
```

## Configuring Headless Mode for CI/CD

### Chrome Headless Configuration

```java
public class DriverFactory {
    
    public static WebDriver createDriver() {
        String browser = System.getProperty("browser", "chrome");
        boolean headless = Boolean.parseBoolean(
            System.getProperty("headless", "false")
        );
        
        switch (browser.toLowerCase()) {
            case "chrome":
                return createChromeDriver(headless);
            case "firefox":
                return createFirefoxDriver(headless);
            case "edge":
                return createEdgeDriver(headless);
            default:
                return createChromeDriver(headless);
        }
    }
    
    private static WebDriver createChromeDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }
        
        return new ChromeDriver(options);
    }
    
    private static WebDriver createFirefoxDriver(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        
        if (headless) {
            options.addArguments("-headless");
            options.addArguments("-width=1920");
            options.addArguments("-height=1080");
        }
        
        return new FirefoxDriver(options);
    }
    
    private static WebDriver createEdgeDriver(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        
        return new EdgeDriver(options);
    }
}
```

## Test Suites and Tags

### JUnit 5 Tags

```java
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("smoke")
class SmokeTests {
    @Test void quickTest() { }
}

@Tag("regression")
class RegressionTests {
    @Test void thoroughTest() { }
}

@Tag("smoke")
@Tag("login")
class LoginSmokeTests {
    @Test void loginTest() { }
}
```

### Running Tagged Tests (Maven)

```xml
<!-- pom.xml - Surefire configuration -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.1.2</version>
    <configuration>
        <groups>${test.groups}</groups>
        <excludedGroups>${test.excludedGroups}</excludedGroups>
    </configuration>
</plugin>
```

```bash
# Run smoke tests only
mvn test -Dtest.groups=smoke

# Run regression tests
mvn test -Dtest.groups=regression

# Exclude slow tests
mvn test -Dtest.excludedGroups=slow

# Combine tags (AND)
mvn test -Dtest.groups="smoke & login"

# Combine tags (OR)
mvn test -Dtest.groups="smoke | sanity"
```

### Running Tagged Tests (Gradle)

```groovy
// build.gradle
test {
    useJUnitPlatform {
        includeTags System.getProperty('includeTags', '')
        excludeTags System.getProperty('excludeTags', '')
    }
}
```

```bash
# Run smoke tests
gradle test -DincludeTags=smoke

# Run multiple tags
gradle test -DincludeTags="smoke | sanity"
```

## CI/CD Integration

### GitHub Actions Example

```yaml
# .github/workflows/selenium-tests.yml
name: Selenium Tests

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Setup Chrome
      uses: browser-actions/setup-chrome@latest
    
    - name: Run Tests
      run: mvn clean test -Dheadless=true -Dbrowser=chrome
    
    - name: Upload Test Results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-results
        path: target/surefire-reports/
    
    - name: Upload Screenshots
      uses: actions/upload-artifact@v3
      if: failure()
      with:
        name: failure-screenshots
        path: screenshots/failures/
```

### Jenkins Pipeline Example

```groovy
// Jenkinsfile
pipeline {
    agent any
    
    tools {
        maven 'Maven 3.9'
        jdk 'JDK 17'
    }
    
    environment {
        HEADLESS = 'true'
        BROWSER = 'chrome'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Smoke Tests') {
            steps {
                sh 'mvn test -Dtest.groups=smoke -Dheadless=${HEADLESS} -Dbrowser=${BROWSER}'
            }
        }
        
        stage('Regression Tests') {
            when {
                branch 'main'
            }
            steps {
                sh 'mvn test -Dtest.groups=regression -Dheadless=${HEADLESS} -Dbrowser=${BROWSER}'
            }
        }
    }
    
    post {
        always {
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'screenshots/**/*.png', allowEmptyArchive: true
        }
        failure {
            archiveArtifacts artifacts: 'screenshots/failures/*.png', allowEmptyArchive: true
        }
    }
}
```

### GitLab CI Example

```yaml
# .gitlab-ci.yml
stages:
  - test

variables:
  HEADLESS: "true"
  BROWSER: "chrome"

selenium-tests:
  stage: test
  image: maven:3.9-eclipse-temurin-17
  
  before_script:
    - apt-get update && apt-get install -y chromium chromium-driver
  
  script:
    - mvn clean test -Dheadless=$HEADLESS -Dbrowser=$BROWSER
  
  artifacts:
    when: always
    reports:
      junit: target/surefire-reports/*.xml
    paths:
      - screenshots/
    expire_in: 7 days
```

## Parallel Test Execution

### Maven Parallel Execution

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.1.2</version>
    <configuration>
        <parallel>methods</parallel>
        <threadCount>4</threadCount>
        <perCoreThreadCount>true</perCoreThreadCount>
    </configuration>
</plugin>
```

```bash
# Override from command line
mvn test -DthreadCount=8 -Dparallel=classes
```

### JUnit 5 Parallel Configuration

```properties
# src/test/resources/junit-platform.properties
junit.jupiter.execution.parallel.enabled = true
junit.jupiter.execution.parallel.mode.default = concurrent
junit.jupiter.execution.parallel.mode.classes.default = concurrent
junit.jupiter.execution.parallel.config.strategy = fixed
junit.jupiter.execution.parallel.config.fixed.parallelism = 4
```

## Common CLI Commands Reference

```
Test Execution Commands:
┌─────────────────────────────────────────────────────────────────────┐
│ Maven                                                               │
├─────────────────────────────────────────────────────────────────────┤
│ mvn test                       │ Run all tests                     │
│ mvn test -Dtest=TestClass      │ Run specific class                │
│ mvn test -Dtest=*Test          │ Run matching pattern              │
│ mvn test -Dtest.groups=smoke   │ Run tagged tests                  │
│ mvn test -Dheadless=true       │ Pass system property              │
│ mvn test -DskipTests=false     │ Ensure tests run                  │
│ mvn verify                     │ Run integration tests             │
├─────────────────────────────────────────────────────────────────────┤
│ Gradle                                                              │
├─────────────────────────────────────────────────────────────────────┤
│ gradle test                    │ Run all tests                     │
│ gradle test --tests TestClass  │ Run specific class                │
│ gradle test --tests '*Test'    │ Run matching pattern              │
│ gradle test -Dheadless=true    │ Pass system property              │
│ gradle test --info             │ Verbose output                    │
│ gradle test --rerun            │ Force rerun all tests             │
└─────────────────────────────────────────────────────────────────────┘
```

## Summary

- **Maven** uses `mvn test` with `-Dtest=` for specific tests and `-D` for properties
- **Gradle** uses `gradle test` with `--tests` for specific tests
- **System properties** configure browser, headless mode, URLs at runtime
- **JUnit tags** enable running smoke, regression, or custom test subsets
- **Headless mode** is essential for CI/CD environments without displays
- **CI/CD integration** automates test execution on code changes
- **Parallel execution** speeds up test runs on multi-core systems
- Always **capture screenshots** on failure in CI environments

Next, review the week's capstone summary to consolidate your learning.

## Additional Resources

- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/) - Configuration reference
- [Gradle Test Task](https://docs.gradle.org/current/userguide/java_testing.html) - Official docs
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) - Tags and parallel execution

