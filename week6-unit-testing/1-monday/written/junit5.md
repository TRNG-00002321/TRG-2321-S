# JUnit5: The Modern Java Testing Framework

## Learning Objectives
- Understand JUnit5's architecture and the Jupiter test engine
- Recognize the key differences between JUnit4 and JUnit5
- Set up a JUnit5 project with proper dependencies
- Write and run your first JUnit5 test

## Why This Matters

As we embark on **Week 6: Building Confidence Through Comprehensive Test Coverage**, JUnit5 serves as our foundational tool for Java unit testing. In the software industry, JUnit is the de facto standard—over 70% of Java projects use JUnit for testing. Understanding JUnit5 isn't just about learning a testing framework; it's about adopting a mindset where every piece of code you write can be verified, documented, and trusted.

Think of JUnit5 as your safety net when walking the tightrope of software development. Every test you write catches potential falls before they happen in production, where the consequences are far more severe.

## The Concept

### What is JUnit5?

JUnit5 is a complete rewrite of the JUnit testing framework, designed from the ground up with modern Java features in mind. Unlike its predecessor JUnit4, which was a monolithic framework, JUnit5 embraces modularity through its component-based architecture.

### The JUnit5 Architecture: Platform, Jupiter, and Vintage

JUnit5 is composed of three main modules:

```
JUnit 5 = JUnit Platform + JUnit Jupiter + JUnit Vintage
```

1. **JUnit Platform**: The foundation layer that enables launching testing frameworks on the JVM. It defines the `TestEngine` API for developing testing frameworks and provides a `ConsoleLauncher` for running tests from the command line.

2. **JUnit Jupiter**: The new programming model and extension model for writing tests. This is where you'll spend most of your time—it includes the new annotations like `@Test`, `@BeforeEach`, `@AfterEach`, and more.

3. **JUnit Vintage**: A compatibility layer that allows running JUnit3 and JUnit4 tests on the JUnit5 platform. This is crucial for gradual migration of legacy test suites.

### Why JUnit5 Over JUnit4?

| Feature | JUnit4 | JUnit5 |
|---------|--------|--------|
| Architecture | Monolithic | Modular |
| Minimum Java Version | Java 5 | Java 8+ |
| Lambda Support | No | Yes |
| Nested Tests | No | Yes (@Nested) |
| Parameterized Tests | Limited | Comprehensive |
| Display Names | No | Yes (@DisplayName) |
| Conditional Execution | Limited | Extensive |
| Extension Model | @RunWith, @Rule | @ExtendWith (unified) |

### Key Annotations Comparison

| JUnit4 | JUnit5 | Purpose |
|--------|--------|---------|
| `@Test` | `@Test` | Marks a test method |
| `@Before` | `@BeforeEach` | Runs before each test |
| `@After` | `@AfterEach` | Runs after each test |
| `@BeforeClass` | `@BeforeAll` | Runs once before all tests |
| `@AfterClass` | `@AfterAll` | Runs once after all tests |
| `@Ignore` | `@Disabled` | Skips a test |
| `@Category` | `@Tag` | Categorizes tests |
| `@RunWith` + `@Rule` | `@ExtendWith` | Extends test functionality |

## Code Example

### Setting Up JUnit5 with Maven

Add these dependencies to your `pom.xml`:

```xml
<dependencies>
    <!-- JUnit Jupiter API for writing tests -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    
    <!-- JUnit Jupiter Engine for running tests -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Optional: For parameterized tests -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-params</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.1.2</version>
        </plugin>
    </plugins>
</build>
```

### Setting Up JUnit5 with Gradle

Add to your `build.gradle`:

```groovy
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
}

test {
    useJUnitPlatform()
}
```

### Your First JUnit5 Test

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    @Test
    @DisplayName("Adding two positive numbers should return their sum")
    void additionWithPositiveNumbers() {
        // Arrange
        Calculator calculator = new Calculator();
        
        // Act
        int result = calculator.add(2, 3);
        
        // Assert
        assertEquals(5, result, "2 + 3 should equal 5");
    }
    
    @Test
    @DisplayName("Division by zero should throw ArithmeticException")
    void divisionByZeroThrowsException() {
        Calculator calculator = new Calculator();
        
        assertThrows(ArithmeticException.class, () -> {
            calculator.divide(10, 0);
        });
    }
}
```

### Understanding the Test Class Structure

```java
import org.junit.jupiter.api.*;

class ExampleTestStructure {

    @BeforeAll
    static void initializeOnce() {
        // Runs once before all tests in this class
        // Must be static (unless using @TestInstance(PER_CLASS))
        System.out.println("Setting up test class resources...");
    }

    @BeforeEach
    void setUp() {
        // Runs before each test method
        System.out.println("Preparing for a test...");
    }

    @Test
    void testOne() {
        System.out.println("Running test one");
    }

    @Test
    void testTwo() {
        System.out.println("Running test two");
    }

    @AfterEach
    void tearDown() {
        // Runs after each test method
        System.out.println("Cleaning up after test...");
    }

    @AfterAll
    static void cleanUpOnce() {
        // Runs once after all tests in this class
        System.out.println("Releasing test class resources...");
    }
}
```

**Execution order output:**
```
Setting up test class resources...
Preparing for a test...
Running test one
Cleaning up after test...
Preparing for a test...
Running test two
Cleaning up after test...
Releasing test class resources...
```

### Migrating from JUnit4 to JUnit5

If you're working with legacy code, here's a quick migration checklist:

```java
// JUnit4 Style
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class OldStyleTest {
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }
    
    @Test
    public void testSomething() {
        assertEquals(4, 2 + 2);
    }
}

// JUnit5 Style
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class NewStyleTest {  // No need for 'public'
    @BeforeEach
    void setUp() { }  // No need for 'public'
    
    @AfterEach
    void tearDown() { }
    
    @Test
    void testSomething() {  // Method names don't need 'test' prefix
        assertEquals(4, 2 + 2);
    }
}
```

### Running Tests in Your IDE

**IntelliJ IDEA**: Right-click on the test class or method → Run. The IDE automatically detects JUnit5 tests.

**Eclipse**: Install the JUnit5 support plugin. Right-click → Run As → JUnit Test.

**Command Line (Maven)**:
```bash
mvn test
```

**Command Line (Gradle)**:
```bash
gradle test
```

## Summary

- **JUnit5** is a modular testing framework composed of Platform, Jupiter, and Vintage components
- The **Jupiter** module provides the new programming model with modern annotations
- Key annotation changes from JUnit4: `@Before` → `@BeforeEach`, `@After` → `@AfterEach`, `@Ignore` → `@Disabled`
- JUnit5 requires **Java 8+** and provides superior support for lambdas and modern Java features
- The **extension model** (`@ExtendWith`) unifies the old `@RunWith` and `@Rule` approach
- Test classes and methods no longer need to be `public`

## Additional Resources

- [JUnit5 User Guide (Official)](https://junit.org/junit5/docs/current/user-guide/) - Comprehensive official documentation
- [Baeldung: Guide to JUnit5](https://www.baeldung.com/junit-5) - Excellent tutorial with practical examples
- [JUnit5 Migration Guide](https://junit.org/junit5/docs/current/user-guide/#migrating-from-junit4) - Step-by-step migration from JUnit4

