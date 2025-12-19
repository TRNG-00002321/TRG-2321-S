# Installing Playwright for Java

## Learning Objectives
- Configure Playwright with Maven and Gradle
- Install browser binaries for automation
- Understand CI/CD installation requirements
- Handle Docker containerization considerations
- Troubleshoot common installation issues

## Why This Matters

Proper installation ensures:
- Reliable test execution across environments
- Consistent browser versions
- Smooth CI/CD integration
- Minimal setup friction for team members

## The Concept

### Maven Installation

**pom.xml:**
```xml
<dependencies>
    <dependency>
        <groupId>com.microsoft.playwright</groupId>
        <artifactId>playwright</artifactId>
        <version>1.40.0</version>
    </dependency>
</dependencies>
```

**Download dependencies:**
```bash
mvn dependency:resolve
```

### Gradle Installation

**build.gradle:**
```groovy
plugins {
    id 'java'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.microsoft.playwright:playwright:1.40.0'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
}

test {
    useJUnitPlatform()
}
```

**build.gradle.kts (Kotlin DSL):**
```kotlin
plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.microsoft.playwright:playwright:1.40.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}
```

### Browser Installation

Playwright requires browser binaries. Install them after adding the dependency:

```bash
# Install all browsers
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"

# Install specific browser
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install firefox"
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install webkit"

# Install with system dependencies (Linux)
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
```

**Browser Installation Locations:**
- Windows: `%USERPROFILE%\AppData\Local\ms-playwright`
- macOS: `~/Library/Caches/ms-playwright`
- Linux: `~/.cache/ms-playwright`

### Programmatic Browser Installation

```java
import com.microsoft.playwright.CLI;

public class InstallBrowsers {
    public static void main(String[] args) {
        // Install all browsers programmatically
        CLI.main(new String[]{"install"});
        
        // Or specific browser
        CLI.main(new String[]{"install", "chromium"});
    }
}
```

### CI/CD Installation

**GitHub Actions:**
```yaml
name: Playwright Tests

on: [push, pull_request]

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
    
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Cache Playwright browsers
      uses: actions/cache@v3
      with:
        path: ~/.cache/ms-playwright
        key: ${{ runner.os }}-playwright-${{ hashFiles('**/pom.xml') }}
    
    - name: Install Playwright browsers
      run: mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
    
    - name: Run tests
      run: mvn test
```

**Jenkins Pipeline:**
```groovy
pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    stages {
        stage('Install') {
            steps {
                sh 'mvn dependency:resolve'
                sh 'mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
    }
    
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
```

### Docker Considerations

**Dockerfile:**
```dockerfile
FROM maven:3.9-eclipse-temurin-17

# Install Playwright system dependencies
RUN apt-get update && apt-get install -y \
    libglib2.0-0 \
    libnss3 \
    libnspr4 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libcups2 \
    libdrm2 \
    libdbus-1-3 \
    libxcb1 \
    libxkbcommon0 \
    libx11-6 \
    libxcomposite1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxrandr2 \
    libgbm1 \
    libpango-1.0-0 \
    libcairo2 \
    libasound2 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy pom and install dependencies
COPY pom.xml .
RUN mvn dependency:resolve

# Install Playwright browsers
RUN mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"

# Copy source
COPY src ./src

# Run tests
CMD ["mvn", "test"]
```

**Using Official Playwright Docker Image:**
```dockerfile
FROM mcr.microsoft.com/playwright/java:v1.40.0-jammy

WORKDIR /app
COPY . .

RUN mvn dependency:resolve
CMD ["mvn", "test"]
```

### Environment Variables

```bash
# Custom browser installation path
export PLAYWRIGHT_BROWSERS_PATH=/custom/path/browsers

# Skip browser download
export PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1

# Use system browsers
export PLAYWRIGHT_CHROMIUM_PATH=/usr/bin/chromium
```

### Troubleshooting

**Issue: Browsers not found**
```bash
# Reinstall browsers
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --force"
```

**Issue: Missing system dependencies (Linux)**
```bash
# Install all system dependencies
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install-deps"

# Or for specific browser
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install-deps chromium"
```

**Issue: Permission denied**
```bash
# Check browser location permissions
ls -la ~/.cache/ms-playwright/

# Fix permissions
chmod -R 755 ~/.cache/ms-playwright/
```

**Issue: Version mismatch**
```xml
<!-- Ensure consistent versions -->
<dependency>
    <groupId>com.microsoft.playwright</groupId>
    <artifactId>playwright</artifactId>
    <version>1.40.0</version>  <!-- Pin specific version -->
</dependency>
```

### Version Management

```java
// Check Playwright version programmatically
public class VersionCheck {
    public static void main(String[] args) {
        System.out.println("Playwright version check");
        
        // The version is determined by the Maven dependency
        // Browser versions are tied to Playwright version
        
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            System.out.println("Browser version: " + browser.version());
            browser.close();
        }
    }
}
```

### Complete Setup Script

```bash
#!/bin/bash
# setup-playwright.sh

echo "Setting up Playwright for Java..."

# Check Java version
java -version

# Check Maven
mvn -version

# Install dependencies
echo "Installing Maven dependencies..."
mvn dependency:resolve

# Install browsers with dependencies
echo "Installing Playwright browsers..."
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"

# Verify installation
echo "Verifying installation..."
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="--version"

echo "Setup complete!"
```

## Key Takeaways

1. **Maven/Gradle** dependency: `com.microsoft.playwright:playwright`
2. **Browser installation** required after adding dependency
3. **CI/CD** needs `--with-deps` flag for system dependencies
4. **Docker** requires system libraries or official images
5. **Cache browsers** in CI for faster builds
6. **Version pinning** ensures consistency

## Additional Resources

- [Playwright Java Installation](https://playwright.dev/java/docs/intro) - Official guide
- [Playwright Docker](https://playwright.dev/java/docs/docker) - Docker configuration
- [Playwright CI/CD](https://playwright.dev/java/docs/ci) - CI integration guide

