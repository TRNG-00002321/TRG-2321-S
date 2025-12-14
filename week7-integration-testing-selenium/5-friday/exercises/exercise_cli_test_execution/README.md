# Lab: Command Line Test Execution

## Overview

**Duration:** 45-60 minutes  
**Mode:** Individual Implementation (Code Lab)  
**Difficulty:** Intermediate

In this lab, you'll learn to run Selenium tests from the command line using Maven. This is essential for CI/CD pipeline integration.

---

## Learning Objectives

By completing this lab, you will:
- Run tests via Maven command line
- Filter tests by name, tags, or class
- Pass parameters to tests via CLI
- Generate test reports
- Prepare tests for CI/CD execution

---

## Prerequisites

- Maven installed and in PATH
- Working Selenium test project
- Understanding of Maven lifecycle

---

## Core Tasks

### Task 1: Basic Maven Test Execution (10 minutes)

**Navigate to project directory and run:**

```bash
# Run all tests
mvn test

# Run with clean (recommended)
mvn clean test

# Run with verbose output
mvn test -X

# Skip tests
mvn package -DskipTests

# Run specific test class
mvn test -Dtest=LoginTests

# Run specific test method
mvn test -Dtest=LoginTests#testValidLogin

# Run multiple specific tests
mvn test -Dtest=LoginTests,CheckboxTests
```

### Task 2: Configure Surefire Plugin (15 minutes)

**Update `pom.xml`:**

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.1.2</version>
            <configuration>
                <!-- Include test patterns -->
                <includes>
                    <include>**/*Test.java</include>
                    <include>**/*Tests.java</include>
                </includes>
                
                <!-- Parallel execution -->
                <parallel>methods</parallel>
                <threadCount>4</threadCount>
                
                <!-- System properties -->
                <systemPropertyVariables>
                    <browser>${browser}</browser>
                    <headless>${headless}</headless>
                    <baseUrl>${baseUrl}</baseUrl>
                </systemPropertyVariables>
                
                <!-- Fail build on test failure -->
                <testFailureIgnore>false</testFailureIgnore>
                
                <!-- Generate reports -->
                <reportsDirectory>${project.build.directory}/surefire-reports</reportsDirectory>
            </configuration>
        </plugin>
    </plugins>
</build>

<properties>
    <!-- Default values -->
    <browser>chrome</browser>
    <headless>false</headless>
    <baseUrl>https://the-internet.herokuapp.com</baseUrl>
</properties>
```

**Run with custom properties:**

```bash
# Chrome headless
mvn test -Dbrowser=chrome -Dheadless=true

# Firefox
mvn test -Dbrowser=firefox

# Custom base URL
mvn test -DbaseUrl=https://staging.example.com
```

### Task 3: Test Filtering with Groups (15 minutes)

**Add JUnit 5 tags to tests:**

```java
package com.bookhaven.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("smoke")
class SmokeTests extends BaseTest {
    
    @Test
    @Tag("login")
    void testLogin() {
        // Quick validation test
    }
}

@Tag("regression")
class RegressionTests extends BaseTest {
    
    @Test
    @Tag("slow")
    void testComprehensiveFlow() {
        // Full regression test
    }
}
```

**Configure Surefire for tags:**

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.1.2</version>
    <configuration>
        <!-- Run by tag expression -->
        <groups>${testGroups}</groups>
        <excludedGroups>${excludedGroups}</excludedGroups>
    </configuration>
</plugin>
```

**Run by tags:**

```bash
# Run only smoke tests
mvn test -DtestGroups=smoke

# Run regression but exclude slow tests
mvn test -DtestGroups=regression -DexcludedGroups=slow

# Run smoke OR regression
mvn test -DtestGroups="smoke | regression"

# Run smoke AND login
mvn test -DtestGroups="smoke & login"
```

### Task 4: Generate Reports (15 minutes)

**Add Surefire Report Plugin:**

```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-report-plugin</artifactId>
            <version>3.1.2</version>
        </plugin>
    </plugins>
</reporting>
```

**Generate HTML report:**

```bash
# Run tests and generate report
mvn clean test surefire-report:report

# Report location: target/site/surefire-report.html
```

**View reports:**
- `target/surefire-reports/` - XML and TXT reports
- `target/site/surefire-report.html` - HTML report

### Task 5: CI/CD Ready Configuration (10 minutes)

**Create Maven profile for CI:**

```xml
<profiles>
    <profile>
        <id>ci</id>
        <properties>
            <browser>chrome</browser>
            <headless>true</headless>
        </properties>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <systemPropertyVariables>
                            <browser>chrome</browser>
                            <headless>true</headless>
                        </systemPropertyVariables>
                        <parallel>classes</parallel>
                        <threadCount>2</threadCount>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
    
    <profile>
        <id>smoke</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <groups>smoke</groups>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

**Run with profile:**

```bash
# CI profile (headless Chrome)
mvn test -Pci

# Smoke tests only
mvn test -Psmoke

# Combine profiles
mvn test -Pci,smoke
```

### Task 6: Create Test Runner Script (10 minutes)

**Create `run-tests.sh` (Linux/Mac):**

```bash
#!/bin/bash

# Default values
BROWSER=${1:-chrome}
HEADLESS=${2:-false}
TAGS=${3:-}

echo "Running tests with:"
echo "  Browser: $BROWSER"
echo "  Headless: $HEADLESS"
echo "  Tags: $TAGS"

# Build Maven command
MVN_CMD="mvn clean test -Dbrowser=$BROWSER -Dheadless=$HEADLESS"

if [ -n "$TAGS" ]; then
    MVN_CMD="$MVN_CMD -DtestGroups=$TAGS"
fi

echo "Executing: $MVN_CMD"
$MVN_CMD

# Check result
if [ $? -eq 0 ]; then
    echo "✅ Tests passed!"
else
    echo "❌ Tests failed!"
    exit 1
fi
```

**Create `run-tests.bat` (Windows):**

```batch
@echo off
setlocal

set BROWSER=%1
if "%BROWSER%"=="" set BROWSER=chrome

set HEADLESS=%2
if "%HEADLESS%"=="" set HEADLESS=false

set TAGS=%3

echo Running tests with:
echo   Browser: %BROWSER%
echo   Headless: %HEADLESS%
echo   Tags: %TAGS%

set MVN_CMD=mvn clean test -Dbrowser=%BROWSER% -Dheadless=%HEADLESS%

if not "%TAGS%"=="" set MVN_CMD=%MVN_CMD% -DtestGroups=%TAGS%

echo Executing: %MVN_CMD%
%MVN_CMD%

if %ERRORLEVEL% EQU 0 (
    echo Tests passed!
) else (
    echo Tests failed!
    exit /b 1
)
```

**Usage:**

```bash
# Linux/Mac
./run-tests.sh chrome true smoke

# Windows
run-tests.bat chrome true smoke
```

---

## Definition of Done

Your lab is complete when you have:

- [ ] Run tests via `mvn test`
- [ ] Configured Surefire plugin
- [ ] Run tests with custom properties
- [ ] Filter tests by tags
- [ ] Generated HTML test report
- [ ] Created CI profile
- [ ] Created test runner script
- [ ] Documented all commands

---

## Maven Command Reference

```bash
# Basic execution
mvn test                          # Run all tests
mvn clean test                    # Clean and run
mvn test -Dtest=ClassName         # Run specific class
mvn test -Dtest=Class#method      # Run specific method

# Properties
mvn test -Dbrowser=firefox        # Set browser
mvn test -Dheadless=true          # Enable headless

# Tags/Groups
mvn test -DtestGroups=smoke       # Run tagged tests
mvn test -DexcludedGroups=slow    # Exclude tags

# Profiles
mvn test -Pci                     # Use CI profile
mvn test -Psmoke                  # Use smoke profile

# Reports
mvn surefire-report:report        # Generate HTML report
mvn site                          # Generate full site

# Parallel
mvn test -DforkCount=2            # Parallel JVMs
```

---

## Submission Checklist

| Item | Completed |
|------|-----------|
| Basic mvn test | ☐ |
| Run specific test | ☐ |
| Custom properties | ☐ |
| Tag filtering | ☐ |
| Surefire configuration | ☐ |
| HTML report generated | ☐ |
| CI profile created | ☐ |
| Test runner script | ☐ |

---

## Additional Resources

- Written Content: `cli-mode-java.md`
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [JUnit 5 Tags](https://junit.org/junit5/docs/current/user-guide/#writing-tests-tagging-and-filtering)

