# Wednesday Exercises: Cucumber & BDD (Java)

## Overview

These exercises provide hands-on practice with Cucumber and Behavior-Driven Development in Java. You'll write Gherkin feature files, implement step definitions, and integrate Cucumber with Selenium WebDriver.

**Exercise Mode:** Code Lab (Mode A)

**Time Estimate:** 3-4 hours total

## Exercise List

| # | Exercise | Type | Difficulty | Time |
|---|----------|------|------------|------|
| 1 | First Cucumber Project | Code Lab | ⭐⭐ | 45 min |
| 2 | Gherkin Writing Practice | Code Lab | ⭐⭐ | 30 min |
| 3 | Scenario Outline & Examples | Code Lab | ⭐⭐⭐ | 45 min |
| 4 | Hooks and Tags | Code Lab | ⭐⭐⭐ | 45 min |
| 5 | Cucumber-Selenium Integration | Code Lab | ⭐⭐⭐⭐ | 60 min |

## Prerequisites

- Java 11+ installed
- Maven installed
- Completed Week 7 Selenium Java content
- Read all Wednesday written content (BDD, Cucumber, Gherkin)

## Project Setup

```bash
# Create new Maven project
mvn archetype:generate -DgroupId=com.training.cucumber \
  -DartifactId=cucumber-exercises \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false

cd cucumber-exercises
```

**Required Dependencies (pom.xml):**
```xml
<dependencies>
    <!-- Cucumber -->
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-java</artifactId>
        <version>7.14.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-junit-platform-engine</artifactId>
        <version>7.14.0</version>
        <scope>test</scope>
    </dependency>
    
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.platform</groupId>
        <artifactId>junit-platform-suite</artifactId>
        <version>1.10.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Selenium -->
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.15.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.github.bonigarcia</groupId>
        <artifactId>webdrivermanager</artifactId>
        <version>5.6.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Target Application

All exercises use: **https://the-internet.herokuapp.com/**

