# Executing Cucumber Tests

## Learning Objectives
- Run Cucumber tests from the IDE (IntelliJ IDEA, Eclipse)
- Execute tests via Maven and Gradle build tools
- Use command line execution for CI/CD integration
- Apply test filtering with tags and name patterns
- Configure parallel test execution for faster feedback
- Generate and interpret test reports

## Why This Matters

Knowing multiple ways to execute Cucumber tests enables you to:
- Run quick feedback cycles during development (IDE)
- Integrate tests into build pipelines (Maven/Gradle)
- Execute selective tests for focused debugging
- Optimize test execution with parallelization
- Generate reports for stakeholder communication

## The Concept

### Execution Methods Overview

```
┌─────────────────────────────────────────────────────────────────┐
│               Cucumber Test Execution Methods                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐       │
│  │     IDE       │  │  Build Tool   │  │ Command Line  │       │
│  │               │  │               │  │               │       │
│  │ • IntelliJ    │  │ • Maven       │  │ • java -jar   │       │
│  │ • Eclipse     │  │ • Gradle      │  │ • mvn exec    │       │
│  │ • VS Code     │  │               │  │ • gradle      │       │
│  └───────────────┘  └───────────────┘  └───────────────┘       │
│         │                  │                  │                 │
│         └──────────────────┴──────────────────┘                 │
│                           │                                     │
│                    All use same:                                │
│                    • Feature files                              │
│                    • Step definitions                           │
│                    • Configuration                              │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### IDE Integration

#### IntelliJ IDEA

**Prerequisites:**
1. Install "Cucumber for Java" plugin
2. Install "Gherkin" plugin

**Running Tests:**

```
┌─────────────────────────────────────────────────────────────────┐
│  IntelliJ IDEA - Running Cucumber Tests                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Method 1: Right-click Feature File                             │
│  ─────────────────────────────────                              │
│  1. Navigate to .feature file                                   │
│  2. Right-click → Run 'Feature: FeatureName'                    │
│                                                                  │
│  Method 2: Run Individual Scenario                              │
│  ─────────────────────────────────                              │
│  1. Place cursor on specific Scenario                           │
│  2. Click green arrow (gutter icon)                             │
│  3. Or use Ctrl+Shift+F10 (Windows/Linux)                       │
│                                                                  │
│  Method 3: Run from Runner Class                                │
│  ─────────────────────────────────                              │
│  1. Open TestRunner.java                                        │
│  2. Right-click → Run 'TestRunner'                              │
│                                                                  │
│  Method 4: Run via Maven/Gradle Tool Window                     │
│  ─────────────────────────────────                              │
│  1. Open Maven/Gradle tool window                               │
│  2. Navigate to Lifecycle → test                                │
│  3. Double-click to execute                                     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**IntelliJ Run Configuration:**

```
Edit Configurations → Add New → Cucumber Java

Main class: io.cucumber.core.cli.Main
Glue: com.example.stepdefinitions com.example.hooks
Feature or folder path: src/test/resources/features
Program arguments: --tags @smoke
```

#### Eclipse

**Prerequisites:**
1. Install "Cucumber Eclipse Plugin" from Marketplace

**Running Tests:**

```
┌─────────────────────────────────────────────────────────────────┐
│  Eclipse - Running Cucumber Tests                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Method 1: Run Feature File                                     │
│  ─────────────────────────────                                  │
│  1. Right-click .feature file                                   │
│  2. Run As → Cucumber Feature                                   │
│                                                                  │
│  Method 2: Run Runner Class                                     │
│  ─────────────────────────────                                  │
│  1. Right-click TestRunner.java                                 │
│  2. Run As → JUnit Test                                         │
│                                                                  │
│  Method 3: Run via Maven                                        │
│  ─────────────────────────────                                  │
│  1. Right-click project                                         │
│  2. Run As → Maven test                                         │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Maven Execution

**Basic Execution:**

```bash
# Run all Cucumber tests
mvn test

# Run with specific profile
mvn test -Pintegration

# Run and generate reports
mvn test verify

# Skip compilation (if already built)
mvn test -DskipTests=false -Dmaven.test.skip=false
```

**Filtering Tests with Tags:**

```bash
# Run only smoke tests
mvn test -Dcucumber.filter.tags="@smoke"

# Run smoke tests excluding WIP
mvn test -Dcucumber.filter.tags="@smoke and not @wip"

# Run multiple tag groups
mvn test -Dcucumber.filter.tags="@smoke or @regression"

# Run specific scenario by name
mvn test -Dcucumber.filter.name="Successful login"
```

**Specifying Features:**

```bash
# Run specific feature file
mvn test -Dcucumber.features="src/test/resources/features/login.feature"

# Run multiple feature files
mvn test -Dcucumber.features="src/test/resources/features/login.feature,src/test/resources/features/checkout.feature"

# Run all features in a directory
mvn test -Dcucumber.features="src/test/resources/features/"
```

**Maven Surefire Configuration:**

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.1</version>
    <configuration>
        <properties>
            <configurationParameters>
                cucumber.junit-platform.naming-strategy=long
                cucumber.plugin=pretty,html:target/cucumber-reports/report.html
                cucumber.publish.quiet=true
            </configurationParameters>
        </properties>
        <includes>
            <include>**/TestRunner.java</include>
        </includes>
    </configuration>
</plugin>
```

### Gradle Execution

**Basic Execution:**

```bash
# Run all tests
gradle test

# Run with info logging
gradle test --info

# Run and continue on failure
gradle test --continue

# Clean and run
gradle clean test
```

**Filtering Tests:**

```bash
# Run with tags
gradle test -Dcucumber.filter.tags="@smoke"

# Run specific feature
gradle test -Dcucumber.features="src/test/resources/features/login.feature"
```

**build.gradle Configuration:**

```groovy
test {
    useJUnitPlatform()
    
    systemProperty "cucumber.junit-platform.naming-strategy", "long"
    systemProperty "cucumber.plugin", "pretty,html:build/cucumber-reports/report.html"
    
    // Pass system properties from command line
    systemProperties System.getProperties()
    
    testLogging {
        events "passed", "skipped", "failed"
        showStandardStreams = true
    }
}

// Custom task for specific tag
task smokeTest(type: Test) {
    useJUnitPlatform()
    systemProperty "cucumber.filter.tags", "@smoke"
}

task regressionTest(type: Test) {
    useJUnitPlatform()
    systemProperty "cucumber.filter.tags", "@regression"
}
```

### Command Line Execution

**Direct CLI Execution:**

```bash
# Using Cucumber CLI
java -cp "target/test-classes:target/classes:lib/*" \
     io.cucumber.core.cli.Main \
     --glue com.example.stepdefinitions \
     --plugin pretty \
     --plugin html:target/cucumber-report.html \
     src/test/resources/features
```

**With Classpath from Maven:**

```bash
# Generate classpath
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt

# Run Cucumber
java -cp "$(cat cp.txt):target/test-classes" \
     io.cucumber.core.cli.Main \
     --glue com.example.stepdefinitions \
     src/test/resources/features
```

### Test Filtering

#### Tag-Based Filtering

```bash
# Single tag
-Dcucumber.filter.tags="@smoke"

# AND condition
-Dcucumber.filter.tags="@smoke and @login"

# OR condition
-Dcucumber.filter.tags="@smoke or @regression"

# NOT condition
-Dcucumber.filter.tags="not @wip"

# Complex expressions
-Dcucumber.filter.tags="(@smoke or @regression) and not @slow"
```

#### Name-Based Filtering

```bash
# Filter by scenario name (regex)
-Dcucumber.filter.name=".*login.*"

# Exact match
-Dcucumber.filter.name="Successful login with valid credentials"
```

### Parallel Execution

**Maven Surefire Parallel Configuration:**

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.1</version>
    <configuration>
        <parallel>methods</parallel>
        <threadCount>4</threadCount>
        <perCoreThreadCount>true</perCoreThreadCount>
        <properties>
            <configurationParameters>
                cucumber.execution.parallel.enabled=true
                cucumber.execution.parallel.config.strategy=fixed
                cucumber.execution.parallel.config.fixed.parallelism=4
            </configurationParameters>
        </properties>
    </configuration>
</plugin>
```

**Cucumber Properties for Parallel:**

```properties
# cucumber.properties
cucumber.execution.parallel.enabled=true
cucumber.execution.parallel.config.strategy=dynamic
cucumber.execution.parallel.config.dynamic.factor=0.5
```

**Thread-Safe Test Design:**

```java
// Use ThreadLocal for driver in parallel execution
public class DriverManager {
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    
    public static WebDriver getDriver() {
        return driver.get();
    }
    
    public static void setDriver(WebDriver webDriver) {
        driver.set(webDriver);
    }
    
    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
```

### Test Reports

#### Built-in Reporters

```bash
# Pretty - Console output
-Dcucumber.plugin="pretty"

# HTML Report
-Dcucumber.plugin="html:target/cucumber-reports/report.html"

# JSON Report (for tools)
-Dcucumber.plugin="json:target/cucumber-reports/report.json"

# JUnit XML (for CI systems)
-Dcucumber.plugin="junit:target/cucumber-reports/report.xml"

# Multiple reporters
-Dcucumber.plugin="pretty,html:target/report.html,json:target/report.json"
```

#### Runner Configuration for Reports:

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, 
    value = "pretty, " +
            "html:target/cucumber-reports/cucumber.html, " +
            "json:target/cucumber-reports/cucumber.json, " +
            "junit:target/cucumber-reports/cucumber.xml, " +
            "timeline:target/cucumber-reports/timeline")
public class TestRunner {
}
```

#### Generating Rich Reports:

**Cucumber Reports (Publishing):**
```properties
# cucumber.properties
cucumber.publish.enabled=true
cucumber.publish.token=your-token-here
```

**Allure Integration:**
```xml
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-cucumber7-jvm</artifactId>
    <version>2.24.0</version>
</dependency>
```

```java
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, 
    value = "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm")
```

### Dry Run Mode

Test that step definitions exist without executing:

```bash
# Maven
mvn test -Dcucumber.execution.dry-run=true

# Or in runner
@ConfigurationParameter(key = "cucumber.execution.dry-run", value = "true")
```

### Complete CI/CD Example

**GitHub Actions Workflow:**

```yaml
name: Cucumber Tests

on:
  push:
    branches: [ main, develop ]
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
        cache: maven
    
    - name: Run Smoke Tests
      run: mvn test -Dcucumber.filter.tags="@smoke"
    
    - name: Run Full Regression
      if: github.event_name == 'push' && github.ref == 'refs/heads/main'
      run: mvn test -Dcucumber.filter.tags="@regression"
    
    - name: Upload Reports
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: cucumber-reports
        path: target/cucumber-reports/
```

## Key Takeaways

1. **IDE execution** provides quick feedback during development
2. **Maven/Gradle** integration enables CI/CD pipeline execution
3. **Tag filtering** allows selective test execution (`@smoke`, `@regression`)
4. **Parallel execution** reduces test suite runtime
5. **Multiple report formats** support different stakeholder needs
6. **Dry run** validates step definitions without execution

## Additional Resources

- [Cucumber CLI Reference](https://cucumber.io/docs/cucumber/api/#running-cucumber) - Command line options
- [Cucumber JUnit Platform Engine](https://github.com/cucumber/cucumber-jvm/tree/main/cucumber-junit-platform-engine) - JUnit 5 configuration
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/) - Test execution configuration

