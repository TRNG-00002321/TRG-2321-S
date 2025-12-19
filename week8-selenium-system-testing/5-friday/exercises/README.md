# Friday Exercises: Playwright with Java

## Overview

These exercises introduce Playwright, Microsoft's modern browser automation framework. You'll experience cutting-edge testing capabilities including auto-wait, visual testing, video capture, and trace analysis.

**Exercise Mode:** Code Lab (Mode A)

**Time Estimate:** 4-5 hours total

## Exercise List

| # | Exercise | Type | Difficulty | Time |
|---|----------|------|------------|------|
| 1 | First Playwright Test | Code Lab | ⭐⭐ | 45 min |
| 2 | Playwright vs Selenium | Comparative | ⭐⭐ | 45 min |
| 3 | Recording with Codegen | Code Lab | ⭐⭐ | 30 min |
| 4 | Visual Testing | Code Lab | ⭐⭐⭐ | 45 min |
| 5 | Week 8 Capstone | Capstone | ⭐⭐⭐⭐ | 90 min |

## Prerequisites

- Java 11+ installed
- Maven installed
- Completed Week 7 Selenium Java content
- Read all Friday written content (Playwright, visual testing, tracing)

## Project Setup

**pom.xml dependencies:**
```xml
<dependencies>
    <!-- Playwright -->
    <dependency>
        <groupId>com.microsoft.playwright</groupId>
        <artifactId>playwright</artifactId>
        <version>1.40.0</version>
    </dependency>
    
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Install browsers:**
```bash
# After adding dependencies, install Playwright browsers
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

## Target Application

All exercises use: **https://the-internet.herokuapp.com/**

