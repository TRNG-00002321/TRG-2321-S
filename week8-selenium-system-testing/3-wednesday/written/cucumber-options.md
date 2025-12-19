# Cucumber Configuration Options

## Learning Objectives
- Configure Cucumber tests using @CucumberOptions annotation
- Specify feature file paths and glue code packages
- Configure reporting plugins for various output formats
- Filter tests using tag expressions
- Use dryRun, strict mode, and monochrome options
- Apply name filters for selective test execution

## Why This Matters

Proper Cucumber configuration enables:
- Targeted test execution for faster feedback
- Comprehensive reporting for stakeholders
- Flexible test organization and filtering
- CI/CD pipeline integration
- Debugging and maintenance efficiency

## The Concept

### Configuration Methods

Cucumber can be configured through multiple methods:

```
┌─────────────────────────────────────────────────────────────────┐
│              Cucumber Configuration Methods                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. @CucumberOptions Annotation (Runner class)                  │
│  2. cucumber.properties file                                    │
│  3. @ConfigurationParameter (JUnit Platform)                    │
│  4. Command line arguments                                      │
│  5. System properties                                           │
│                                                                  │
│  Priority: Command Line > System Props > Properties File        │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### @CucumberOptions Annotation

The classic way to configure Cucumber tests:

```java
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.example.stepdefinitions", "com.example.hooks"},
    plugin = {"pretty", "html:target/cucumber-reports/report.html"},
    tags = "@smoke and not @wip",
    monochrome = true,
    dryRun = false,
    strict = true
)
public class TestRunner {
}
```

### Features Path Configuration

Specify where Cucumber finds feature files:

```java
@CucumberOptions(
    // Single directory
    features = "src/test/resources/features"
)

@CucumberOptions(
    // Multiple directories
    features = {
        "src/test/resources/features/login",
        "src/test/resources/features/checkout"
    }
)

@CucumberOptions(
    // Specific feature files
    features = {
        "src/test/resources/features/login.feature",
        "src/test/resources/features/checkout.feature"
    }
)

@CucumberOptions(
    // Classpath resource
    features = "classpath:features"
)
```

### Glue Code Configuration

Specify packages containing step definitions and hooks:

```java
@CucumberOptions(
    features = "src/test/resources/features",
    
    // Single package
    glue = "com.example.stepdefinitions"
)

@CucumberOptions(
    features = "src/test/resources/features",
    
    // Multiple packages
    glue = {
        "com.example.stepdefinitions",
        "com.example.hooks",
        "com.example.support"
    }
)
```

**How Glue Works:**

```
┌─────────────────────────────────────────────────────────────────┐
│                    Glue Code Resolution                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Feature File:                                                   │
│  When the user logs in with "john@example.com"                  │
│                       │                                          │
│                       ▼                                          │
│  Cucumber scans glue packages for matching step:                │
│  ─────────────────────────────────────────────                  │
│  com.example.stepdefinitions/                                   │
│    ├── LoginSteps.java      ← @When("the user logs in...")     │
│    ├── CartSteps.java                                           │
│    └── CheckoutSteps.java                                       │
│                                                                  │
│  com.example.hooks/                                              │
│    └── Hooks.java           ← @Before, @After                   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Plugin Configuration

Configure reporting and output formats:

```java
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.stepdefinitions",
    
    plugin = {
        // Console output
        "pretty",
        
        // HTML report
        "html:target/cucumber-reports/cucumber.html",
        
        // JSON report (for tools)
        "json:target/cucumber-reports/cucumber.json",
        
        // JUnit XML (for CI)
        "junit:target/cucumber-reports/cucumber.xml",
        
        // Rerun file (failed scenarios)
        "rerun:target/cucumber-reports/rerun.txt",
        
        // Timeline report
        "timeline:target/cucumber-reports/timeline",
        
        // Usage statistics
        "usage:target/cucumber-reports/usage.json"
    }
)
public class TestRunner {
}
```

**Plugin Descriptions:**

| Plugin | Output | Purpose |
|--------|--------|---------|
| `pretty` | Console | Human-readable colored output |
| `progress` | Console | Dots for progress (. for pass, F for fail) |
| `html:path` | HTML file | Browser-viewable report |
| `json:path` | JSON file | Machine-readable for tools |
| `junit:path` | XML file | CI/CD integration (Jenkins, etc.) |
| `rerun:path` | Text file | List of failed scenarios for re-execution |
| `timeline:path` | Directory | Timeline visualization |
| `usage:path` | JSON file | Step usage statistics |

### Tags Configuration

Filter scenarios by tags:

```java
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.stepdefinitions",
    
    // Single tag
    tags = "@smoke"
)

@CucumberOptions(
    // AND condition
    tags = "@smoke and @login"
)

@CucumberOptions(
    // OR condition
    tags = "@smoke or @regression"
)

@CucumberOptions(
    // NOT condition
    tags = "not @wip"
)

@CucumberOptions(
    // Complex expression
    tags = "(@smoke or @regression) and not @slow"
)
```

**Tag Expression Examples:**

```java
// Run smoke tests only
tags = "@smoke"

// Run smoke OR regression tests
tags = "@smoke or @regression"

// Run smoke tests that are also login tests
tags = "@smoke and @login"

// Run all tests except work-in-progress
tags = "not @wip"

// Run high priority smoke tests, excluding slow ones
tags = "@smoke and @high-priority and not @slow"

// Run either login or checkout smoke tests
tags = "@smoke and (@login or @checkout)"
```

### DryRun Option

Check if step definitions exist without executing:

```java
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.stepdefinitions",
    
    // Check mappings only, don't execute
    dryRun = true
)
```

**DryRun Use Cases:**
- Verify all steps have definitions
- Find undefined steps quickly
- Validate feature file syntax
- CI build step for PR validation

**DryRun Output:**
```
Undefined step: the user clicks the submit button
You can implement it using:
@When("the user clicks the submit button")
public void the_user_clicks_the_submit_button() {
    // Write code here
    throw new io.cucumber.java.PendingException();
}
```

### Strict Mode

Fail the build if there are undefined or pending steps:

```java
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.stepdefinitions",
    
    // Fail on undefined/pending steps
    strict = true
)
```

**Behavior:**
- `strict = true` - Build fails if any step is undefined/pending
- `strict = false` - Undefined steps marked as skipped

### Monochrome Option

Control console output formatting:

```java
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.stepdefinitions",
    
    // Remove ANSI color codes from output
    monochrome = true
)
```

**When to Use:**
- `monochrome = true` - CI/CD logs, plain terminals
- `monochrome = false` - Local development with color support

### Name Filter

Run scenarios matching a regex pattern:

```java
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.stepdefinitions",
    
    // Run scenarios with "login" in the name
    name = ".*login.*"
)

@CucumberOptions(
    // Run specific scenario by exact name
    name = "Successful login with valid credentials"
)

@CucumberOptions(
    // Multiple patterns (OR)
    name = {".*login.*", ".*checkout.*"}
)
```

### JUnit Platform Configuration

Modern approach using JUnit 5:

```java
import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, 
    value = "com.example.stepdefinitions,com.example.hooks")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, 
    value = "pretty,html:target/cucumber-reports/report.html")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, 
    value = "@smoke and not @wip")
@ConfigurationParameter(key = SNIPPET_TYPE_PROPERTY_NAME, 
    value = "camelcase")
public class TestRunner {
}
```

**JUnit Platform Constants:**

| Constant | Description |
|----------|-------------|
| `GLUE_PROPERTY_NAME` | Step definition packages |
| `PLUGIN_PROPERTY_NAME` | Report plugins |
| `FILTER_TAGS_PROPERTY_NAME` | Tag filter expression |
| `FILTER_NAME_PROPERTY_NAME` | Name regex filter |
| `SNIPPET_TYPE_PROPERTY_NAME` | Snippet style (underscore/camelcase) |
| `EXECUTION_DRY_RUN_PROPERTY_NAME` | Dry run mode |
| `ANSI_COLORS_DISABLED_PROPERTY_NAME` | Monochrome mode |

### cucumber.properties File

Configuration via properties file:

**src/test/resources/cucumber.properties:**
```properties
# Features location
cucumber.features=src/test/resources/features

# Glue packages
cucumber.glue=com.example.stepdefinitions,com.example.hooks

# Plugins
cucumber.plugin=pretty,html:target/cucumber-reports/report.html,json:target/cucumber-reports/report.json

# Tag filter
cucumber.filter.tags=@smoke and not @wip

# Name filter
cucumber.filter.name=.*login.*

# Snippet type
cucumber.snippet-type=camelcase

# Publishing
cucumber.publish.quiet=true
cucumber.publish.enabled=false

# Execution options
cucumber.execution.dry-run=false
cucumber.ansi-colors.disabled=false
```

### Command Line Overrides

Override configuration from command line:

```bash
# Maven
mvn test -Dcucumber.filter.tags="@regression"
mvn test -Dcucumber.features="src/test/resources/features/login.feature"
mvn test -Dcucumber.plugin="pretty,html:target/report.html"

# Gradle
gradle test -Dcucumber.filter.tags="@smoke"
```

### Multiple Runner Classes

Create different runners for different purposes:

```java
// Smoke Test Runner
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.stepdefinitions",
    tags = "@smoke",
    plugin = {"pretty", "html:target/smoke-report.html"}
)
public class SmokeTestRunner {
}

// Regression Test Runner
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.stepdefinitions",
    tags = "@regression",
    plugin = {"pretty", "html:target/regression-report.html"}
)
public class RegressionTestRunner {
}

// Failed Test Rerun Runner
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "@target/cucumber-reports/rerun.txt",  // From rerun plugin
    glue = "com.example.stepdefinitions",
    plugin = {"pretty", "html:target/rerun-report.html"}
)
public class RerunFailedTestRunner {
}
```

### Configuration Best Practices

```
┌─────────────────────────────────────────────────────────────────┐
│          Configuration Best Practices                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. Use cucumber.properties for defaults                        │
│     - Keeps runner class clean                                  │
│     - Easy to change without recompilation                      │
│                                                                  │
│  2. Use command line for CI/CD overrides                        │
│     - Different tags per pipeline stage                         │
│     - Environment-specific configuration                        │
│                                                                  │
│  3. Organize glue packages logically                            │
│     - stepdefinitions/ for steps                                │
│     - hooks/ for setup/teardown                                 │
│     - support/ for utilities                                    │
│                                                                  │
│  4. Generate multiple report formats                            │
│     - pretty for console                                        │
│     - html for humans                                           │
│     - json for tools                                            │
│     - junit for CI                                              │
│                                                                  │
│  5. Use rerun plugin for failure recovery                       │
│     - Capture failed scenarios                                  │
│     - Re-execute only failures                                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Key Takeaways

1. **features** specifies where to find .feature files
2. **glue** specifies packages with step definitions and hooks
3. **plugin** configures report output formats
4. **tags** filters scenarios using tag expressions
5. **dryRun** validates step definitions without execution
6. **Multiple configuration methods** provide flexibility

## Additional Resources

- [Cucumber Options Reference](https://cucumber.io/docs/cucumber/api/#options) - Official documentation
- [JUnit Platform Engine](https://github.com/cucumber/cucumber-jvm/tree/main/cucumber-junit-platform-engine) - JUnit 5 configuration
- [Cucumber Reporting](https://cucumber.io/docs/cucumber/reporting/) - Report plugin details

