# Capstone Project: Comprehensive Selenium Test Suite

## Overview

**Duration:** 3-4 hours  
**Mode:** Individual Project  
**Difficulty:** Advanced

This capstone project synthesizes everything you've learned in Week 7. You'll create a complete, production-ready Selenium test automation framework with Page Object Model, proper waits, screenshot capture, and command-line execution.

---

## Learning Objectives

By completing this project, you will:
- Demonstrate mastery of Selenium WebDriver
- Implement a complete Page Object Model framework
- Create reliable, maintainable test code
- Apply professional testing patterns
- Build CI/CD-ready automation

---

## Prerequisites

- All Week 7 exercises completed
- Understanding of POM, waits, assertions
- Maven project setup experience

---

## The Scenario

**BookHaven E-Commerce** has hired you to build their UI test automation framework. The application allows users to:
- Login/Logout
- Browse products
- Add items to cart
- Complete checkout

You'll test a practice e-commerce site that simulates these features.

**Target Application:** [SauceDemo](https://www.saucedemo.com/)
- Username: `standard_user`
- Password: `secret_sauce`

---

## Project Requirements

### 1. Project Structure

```
bookhaven-selenium-capstone/
├── pom.xml
├── README.md
├── run-tests.bat
├── run-tests.sh
└── src/
    ├── main/java/com/bookhaven/
    │   ├── config/
    │   │   └── DriverFactory.java
    │   ├── pages/
    │   │   ├── BasePage.java
    │   │   ├── LoginPage.java
    │   │   ├── InventoryPage.java
    │   │   ├── CartPage.java
    │   │   ├── CheckoutPage.java
    │   │   └── CheckoutCompletePage.java
    │   └── utils/
    │       ├── ScreenshotHelper.java
    │       └── WaitHelper.java
    └── test/java/com/bookhaven/
        ├── BaseTest.java
        ├── LoginTests.java
        ├── InventoryTests.java
        ├── CartTests.java
        ├── CheckoutTests.java
        └── EndToEndTests.java
```

### 2. Core Functionality

#### A. Driver Factory (Browser Abstraction)
- Support Chrome, Firefox, Edge
- Headless mode option
- WebDriverManager integration
- Configurable via system properties

#### B. Base Page Class
- Common wait methods
- Click, type, getText helpers
- Screenshot capability
- Page load verification

#### C. Page Objects Required

**LoginPage:**
- `navigateTo()`
- `enterUsername(String)`
- `enterPassword(String)`
- `clickLogin()` → InventoryPage
- `getErrorMessage()`
- `loginAs(String user, String pass)`

**InventoryPage:**
- `getProductCount()`
- `addProductToCart(String productName)`
- `removeProductFromCart(String productName)`
- `openCart()` → CartPage
- `sortProducts(String sortOption)`
- `getProductPrice(String productName)`
- `logout()` → LoginPage

**CartPage:**
- `getCartItemCount()`
- `getCartItems()` → List of product names
- `removeItem(String productName)`
- `proceedToCheckout()` → CheckoutPage
- `continueShopping()` → InventoryPage

**CheckoutPage:**
- `enterFirstName(String)`
- `enterLastName(String)`
- `enterZipCode(String)`
- `clickContinue()` → CheckoutOverviewPage
- `clickCancel()` → CartPage

**CheckoutCompletePage:**
- `getConfirmationMessage()`
- `clickBackHome()` → InventoryPage

### 3. Test Scenarios Required

#### Login Tests (5 tests)
- [ ] Valid login with standard_user
- [ ] Invalid username shows error
- [ ] Invalid password shows error
- [ ] Locked out user shows error
- [ ] Logout returns to login page

#### Inventory Tests (5 tests)
- [ ] Products displayed after login
- [ ] Add single item to cart
- [ ] Remove item from cart
- [ ] Sort products by price low to high
- [ ] Sort products by name Z to A

#### Cart Tests (4 tests)
- [ ] Cart shows added items
- [ ] Cart item count is correct
- [ ] Remove item from cart
- [ ] Continue shopping returns to inventory

#### Checkout Tests (4 tests)
- [ ] Complete checkout flow
- [ ] Missing information shows error
- [ ] Cancel returns to cart
- [ ] Order confirmation displayed

#### End-to-End Test (2 tests)
- [ ] Complete purchase flow (login → add items → checkout)
- [ ] Full user journey with multiple products

---

## Grading Rubric

| Category | Points | Criteria |
|----------|--------|----------|
| **Project Structure** | 15 | Correct folder structure, Maven config |
| **Page Objects** | 25 | All pages implemented, encapsulation correct |
| **Base Classes** | 15 | Proper abstraction, reusable methods |
| **Test Coverage** | 25 | All required tests, assertions meaningful |
| **Error Handling** | 10 | Proper waits, no flaky tests |
| **Documentation** | 10 | README, code comments, clear naming |
| **Total** | **100** | |

---

## Starter Code

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.bookhaven</groupId>
    <artifactId>selenium-capstone</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <selenium.version>4.15.0</selenium.version>
        <webdrivermanager.version>5.6.2</webdrivermanager.version>
        <junit.version>5.10.0</junit.version>
        
        <!-- Test configuration defaults -->
        <browser>chrome</browser>
        <headless>false</headless>
        <baseUrl>https://www.saucedemo.com</baseUrl>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
            <version>${selenium.version}</version>
        </dependency>
        <dependency>
            <groupId>io.github.bonigarcia</groupId>
            <artifactId>webdrivermanager</artifactId>
            <version>${webdrivermanager.version}</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.15.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
                <configuration>
                    <systemPropertyVariables>
                        <browser>${browser}</browser>
                        <headless>${headless}</headless>
                        <baseUrl>${baseUrl}</baseUrl>
                    </systemPropertyVariables>
                </configuration>
            </plugin>
        </plugins>
    </build>

    <profiles>
        <profile>
            <id>ci</id>
            <properties>
                <headless>true</headless>
            </properties>
        </profile>
    </profiles>
</project>
```

### DriverFactory.java (Starter)

```java
package com.bookhaven.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverFactory {
    
    public static WebDriver createDriver() {
        String browser = System.getProperty("browser", "chrome");
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        
        // TODO: Implement browser creation based on parameters
        // Support: chrome, firefox, edge
        // Support: headless mode
        
        return null; // Replace with implementation
    }
}
```

### BasePage.java (Starter)

```java
package com.bookhaven.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

public abstract class BasePage {
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    
    // TODO: Implement common page methods:
    // - waitForElement(By)
    // - click(By)
    // - type(By, String)
    // - getText(By)
    // - isDisplayed(By)
    // - takeScreenshot(String)
}
```

---

## Submission Requirements

1. **Complete Project in Git Repository**
   - All source files
   - pom.xml configured
   - README with instructions

2. **Documentation**
   - How to run tests
   - Test coverage summary
   - Known issues (if any)

3. **Test Execution Evidence**
   - Screenshot of passed tests
   - Test report (Surefire HTML)

4. **Commands to Run**
   ```bash
   # All tests
   mvn clean test
   
   # Smoke tests only
   mvn test -DtestGroups=smoke
   
   # Headless CI mode
   mvn test -Pci
   ```

---

## Definition of Done

Your capstone is complete when:

- [ ] All page objects implemented
- [ ] All 20 test scenarios passing
- [ ] Tests run in headless mode
- [ ] Screenshots captured on failure
- [ ] Maven CLI execution works
- [ ] README documentation complete
- [ ] Code follows Java conventions
- [ ] No hardcoded waits (Thread.sleep)

---

## Tips for Success

1. **Start with login** - Get authentication working first
2. **Test manually first** - Understand the app before automating
3. **Use explicit waits** - Avoid flaky tests
4. **Small commits** - Commit working code frequently
5. **Test one thing** - Each test should verify one behavior
6. **Meaningful assertions** - Assert what matters
7. **Clean up** - Logout/reset state between tests

---

## Common Mistakes to Avoid

- ❌ Hardcoded Thread.sleep()
- ❌ Tests dependent on each other
- ❌ Assertions in page objects
- ❌ Absolute XPath locators
- ❌ Ignored exceptions
- ❌ Duplicate code across tests

---

## Additional Resources

- [SauceDemo App](https://www.saucedemo.com/)
- Week 7 Written Content and Demos
- [Selenium Best Practices](https://www.selenium.dev/documentation/test_practices/)

