# JMeter CLI Mode (Non-GUI)

## Learning Objectives
- Run JMeter tests from the command line effectively
- Configure command-line options for various test scenarios
- Generate HTML reports from CLI execution
- Integrate JMeter into CI/CD pipelines
- Understand JMeter command options and their uses

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, the ability to run performance tests from the command line is essential for modern DevOps practices. While GUI mode is invaluable for test development, production load tests and CI/CD integration require CLI execution.

CLI mode uses significantly fewer resources than GUI mode, produces more accurate results, and enables automated testing as part of your deployment pipeline. Every serious performance testing effort relies on CLI execution.

## Why CLI Mode?

### GUI vs CLI Comparison

```
GUI Mode:                          CLI Mode:
├── Visual interface               ├── Text-based execution
├── Good for development           ├── Good for production tests
├── Resource-intensive             ├── Lightweight
├── Limited scalability            ├── Highly scalable
├── Interactive debugging          ├── Automated execution
└── Not CI/CD friendly             └── CI/CD native

Resource Comparison (100 users test):
┌────────────────────┬──────────────┬──────────────┐
│ Metric             │ GUI Mode     │ CLI Mode     │
├────────────────────┼──────────────┼──────────────┤
│ Memory Usage       │ ~500MB       │ ~200MB       │
│ CPU Overhead       │ 15-20%       │ 2-5%         │
│ Result Accuracy    │ Lower        │ Higher       │
│ Max Threads        │ ~200         │ 1000+        │
└────────────────────┴──────────────┴──────────────┘
```

### When to Use CLI Mode

```
Use CLI Mode For:
├── Production load tests
├── Tests with >100 users
├── CI/CD pipeline integration
├── Distributed testing
├── Scheduled test runs
├── Resource-constrained environments
└── Accurate performance metrics

Use GUI Mode For:
├── Test plan development
├── Debugging test scripts
├── Viewing individual responses
├── Learning JMeter
└── Small-scale tests (<50 users)
```

## Basic CLI Execution

### Running a Test Plan

```bash
# Basic execution
jmeter -n -t test_plan.jmx -l results.jtl

# Explanation:
# -n : Non-GUI mode
# -t : Test plan file (.jmx)
# -l : Results log file (.jtl)
```

### Complete Example

```bash
# Navigate to JMeter bin directory
cd apache-jmeter-5.6.2/bin

# Run test
./jmeter -n -t /path/to/my_test.jmx -l /path/to/results.jtl

# Windows
jmeter.bat -n -t C:\tests\my_test.jmx -l C:\results\results.jtl
```

### With HTML Report Generation

```bash
# Generate HTML report after test
jmeter -n -t test_plan.jmx -l results.jtl -e -o /path/to/html_report

# Explanation:
# -e : Generate dashboard report after test
# -o : Output folder for HTML report (must be empty or non-existent)
```

## JMeter Command Options

### Essential Options

| Option | Long Form | Description |
|--------|-----------|-------------|
| `-n` | `--nongui` | Run in non-GUI mode |
| `-t` | `--testfile` | Test plan file (.jmx) |
| `-l` | `--logfile` | Results file (.jtl) |
| `-e` | `--reportatendofloadtests` | Generate HTML report |
| `-o` | `--reportoutputfolder` | HTML report output folder |
| `-j` | `--jmeterlogfile` | JMeter log file |

### Property Options

| Option | Description | Example |
|--------|-------------|---------|
| `-J` | Set JMeter property | `-JthreadCount=100` |
| `-G` | Set global property | `-Gserver.rmi.ssl.disable=true` |
| `-D` | Set system property | `-Duser.language=en` |

### Control Options

| Option | Description |
|--------|-------------|
| `-r` | Run remote servers (distributed testing) |
| `-R` | Run specific remote servers |
| `-X` | Exit remote servers after test |
| `-H` | Proxy host |
| `-P` | Proxy port |

## Passing Properties via Command Line

### Using Properties in Test Plans

First, define variables in your test plan that reference properties:
```
${__P(threadCount,10)}    # Default: 10
${__P(rampUp,60)}         # Default: 60
${__P(duration,300)}      # Default: 300
${__P(baseUrl,https://api.example.com)}
```

### Passing Properties

```bash
# Override thread count and ramp-up
jmeter -n -t test.jmx -l results.jtl \
  -JthreadCount=100 \
  -JrampUp=120 \
  -Jduration=600 \
  -JbaseUrl=https://staging.api.example.com
```

### Property File

```bash
# Create properties file: test.properties
threadCount=100
rampUp=120
duration=600
baseUrl=https://staging.api.example.com

# Use property file
jmeter -n -t test.jmx -l results.jtl -q test.properties
```

### Environment-Specific Configurations

```bash
# Development
jmeter -n -t test.jmx -l results.jtl \
  -JbaseUrl=https://dev.api.example.com \
  -JthreadCount=10

# Staging
jmeter -n -t test.jmx -l results.jtl \
  -JbaseUrl=https://staging.api.example.com \
  -JthreadCount=50

# Production (careful!)
jmeter -n -t test.jmx -l results.jtl \
  -JbaseUrl=https://api.example.com \
  -JthreadCount=100
```

## Generating Reports

### HTML Dashboard Report

```bash
# Generate report with test execution
jmeter -n -t test.jmx -l results.jtl -e -o ./report

# Generate report from existing results file
jmeter -g results.jtl -o ./report_from_existing

# Options:
# -e : Generate report at end of test
# -o : Output folder for report
# -g : Generate report from existing JTL file only
```

### Report Output Structure

```
report/
├── index.html              # Main dashboard
├── content/
│   ├── css/               # Stylesheets
│   └── js/                # JavaScript
├── sbadmin2-1.0.7/        # Dashboard framework
└── statistics.json        # Raw statistics data

Dashboard Sections:
├── APDEX (Application Performance Index)
├── Requests Summary
├── Statistics Table
├── Errors
├── Response Times Over Time
├── Active Threads Over Time
├── Response Times Distribution
├── Response Times Percentiles Over Time
└── Throughput Over Time
```

### Custom Report Configuration

```properties
# Custom report settings in user.properties or via -J
jmeter.reportgenerator.apdex_satisfied_threshold=500
jmeter.reportgenerator.apdex_tolerated_threshold=1500
jmeter.reportgenerator.sample_filter=
jmeter.reportgenerator.overall_granularity=60000
```

## CI/CD Integration

### Jenkins Pipeline Integration

```groovy
// Jenkinsfile
pipeline {
    agent any
    
    environment {
        JMETER_HOME = '/opt/apache-jmeter-5.6.2'
    }
    
    stages {
        stage('Performance Test') {
            steps {
                sh '''
                    ${JMETER_HOME}/bin/jmeter -n \
                        -t tests/performance/api_load_test.jmx \
                        -l results/results_${BUILD_NUMBER}.jtl \
                        -e -o results/report_${BUILD_NUMBER} \
                        -JthreadCount=${THREAD_COUNT:-50} \
                        -Jduration=${DURATION:-300}
                '''
            }
            post {
                always {
                    // Archive results
                    archiveArtifacts artifacts: 'results/**/*', fingerprint: true
                    
                    // Publish HTML report
                    publishHTML target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: "results/report_${BUILD_NUMBER}",
                        reportFiles: 'index.html',
                        reportName: 'JMeter Report'
                    ]
                }
            }
        }
        
        stage('Analyze Results') {
            steps {
                script {
                    // Check for performance thresholds
                    def results = readFile("results/results_${BUILD_NUMBER}.jtl")
                    // Parse and validate against thresholds
                }
            }
        }
    }
}
```

### GitHub Actions Integration

```yaml
# .github/workflows/performance-test.yml
name: Performance Test

on:
  schedule:
    - cron: '0 2 * * *'  # Daily at 2 AM
  workflow_dispatch:
    inputs:
      threads:
        description: 'Number of threads'
        default: '50'
      duration:
        description: 'Test duration (seconds)'
        default: '300'

jobs:
  performance-test:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Setup JMeter
        run: |
          wget https://dlcdn.apache.org/jmeter/binaries/apache-jmeter-5.6.2.tgz
          tar -xzf apache-jmeter-5.6.2.tgz
          
      - name: Run Performance Test
        run: |
          apache-jmeter-5.6.2/bin/jmeter -n \
            -t tests/api_load_test.jmx \
            -l results/results.jtl \
            -e -o results/report \
            -JthreadCount=${{ github.event.inputs.threads || '50' }} \
            -Jduration=${{ github.event.inputs.duration || '300' }}
            
      - name: Upload Results
        uses: actions/upload-artifact@v3
        with:
          name: jmeter-results
          path: results/
          
      - name: Check Thresholds
        run: |
          # Simple threshold check script
          python scripts/check_performance.py results/results.jtl
```

### GitLab CI Integration

```yaml
# .gitlab-ci.yml
performance_test:
  stage: test
  image: justb4/jmeter:latest
  
  variables:
    THREADS: "50"
    DURATION: "300"
  
  script:
    - jmeter -n 
        -t tests/load_test.jmx 
        -l results/results.jtl 
        -e -o results/report
        -JthreadCount=$THREADS
        -Jduration=$DURATION
  
  artifacts:
    paths:
      - results/
    expire_in: 1 week
    
  rules:
    - if: $CI_PIPELINE_SOURCE == "schedule"
    - if: $CI_PIPELINE_SOURCE == "web"
```

### Azure DevOps Integration

```yaml
# azure-pipelines.yml
trigger:
  - main

pool:
  vmImage: 'ubuntu-latest'

stages:
  - stage: PerformanceTest
    jobs:
      - job: RunJMeter
        steps:
          - task: Bash@3
            displayName: 'Install JMeter'
            inputs:
              targetType: 'inline'
              script: |
                wget https://dlcdn.apache.org/jmeter/binaries/apache-jmeter-5.6.2.tgz
                tar -xzf apache-jmeter-5.6.2.tgz
                
          - task: Bash@3
            displayName: 'Run Performance Test'
            inputs:
              targetType: 'inline'
              script: |
                apache-jmeter-5.6.2/bin/jmeter -n \
                  -t $(Build.SourcesDirectory)/tests/load_test.jmx \
                  -l $(Build.ArtifactStagingDirectory)/results.jtl \
                  -e -o $(Build.ArtifactStagingDirectory)/report
                  
          - task: PublishBuildArtifacts@1
            inputs:
              PathtoPublish: '$(Build.ArtifactStagingDirectory)'
              ArtifactName: 'jmeter-results'
```

## Batch and Shell Scripts

### Linux/macOS Shell Script

```bash
#!/bin/bash
# run_performance_test.sh

# Configuration
JMETER_HOME="/opt/apache-jmeter-5.6.2"
TEST_PLAN="./tests/api_load_test.jmx"
RESULTS_DIR="./results"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Default parameters (can be overridden)
THREADS=${1:-50}
RAMPUP=${2:-60}
DURATION=${3:-300}
BASE_URL=${4:-"https://api.example.com"}

# Create results directory
mkdir -p "${RESULTS_DIR}"

echo "Starting performance test..."
echo "Threads: ${THREADS}"
echo "Ramp-up: ${RAMPUP}s"
echo "Duration: ${DURATION}s"
echo "Target: ${BASE_URL}"

# Run JMeter
${JMETER_HOME}/bin/jmeter -n \
    -t "${TEST_PLAN}" \
    -l "${RESULTS_DIR}/results_${TIMESTAMP}.jtl" \
    -e -o "${RESULTS_DIR}/report_${TIMESTAMP}" \
    -j "${RESULTS_DIR}/jmeter_${TIMESTAMP}.log" \
    -JthreadCount=${THREADS} \
    -JrampUp=${RAMPUP} \
    -Jduration=${DURATION} \
    -JbaseUrl=${BASE_URL}

# Check exit code
if [ $? -eq 0 ]; then
    echo "Test completed successfully!"
    echo "Results: ${RESULTS_DIR}/results_${TIMESTAMP}.jtl"
    echo "Report: ${RESULTS_DIR}/report_${TIMESTAMP}/index.html"
else
    echo "Test failed!"
    exit 1
fi

# Usage: ./run_performance_test.sh 100 120 600 https://staging.api.example.com
```

### Windows Batch Script

```batch
@echo off
REM run_performance_test.bat

SET JMETER_HOME=C:\apache-jmeter-5.6.2
SET TEST_PLAN=.\tests\api_load_test.jmx
SET RESULTS_DIR=.\results
SET TIMESTAMP=%date:~-4,4%%date:~-10,2%%date:~-7,2%_%time:~0,2%%time:~3,2%%time:~6,2%

REM Default parameters
SET THREADS=%1
IF "%THREADS%"=="" SET THREADS=50
SET DURATION=%2
IF "%DURATION%"=="" SET DURATION=300

REM Create results directory
IF NOT EXIST "%RESULTS_DIR%" mkdir "%RESULTS_DIR%"

echo Starting performance test...
echo Threads: %THREADS%
echo Duration: %DURATION%s

REM Run JMeter
%JMETER_HOME%\bin\jmeter.bat -n ^
    -t "%TEST_PLAN%" ^
    -l "%RESULTS_DIR%\results_%TIMESTAMP%.jtl" ^
    -e -o "%RESULTS_DIR%\report_%TIMESTAMP%" ^
    -JthreadCount=%THREADS% ^
    -Jduration=%DURATION%

IF %ERRORLEVEL% EQU 0 (
    echo Test completed successfully!
) ELSE (
    echo Test failed!
    exit /b 1
)
```

## Advanced CLI Features

### Heap Size Configuration

```bash
# Set heap size for large tests
export HEAP="-Xms2g -Xmx4g"

# Or modify jmeter script directly
# In jmeter.sh, find and modify:
# HEAP="-Xms1g -Xmx1g -XX:MaxMetaspaceSize=256m"
```

### Remote/Distributed Testing

```bash
# Start JMeter server on remote machines
./jmeter-server

# Run distributed test
jmeter -n -t test.jmx -l results.jtl \
    -R server1.example.com,server2.example.com,server3.example.com
```

### Plugin Manager CLI

```bash
# Install plugins via command line
java -jar ${JMETER_HOME}/lib/cmdrunner-2.2.jar \
    --tool org.jmeterplugins.repository.PluginManagerCMD \
    install jpgc-graphs-basic,jpgc-casutg

# List available plugins
java -jar ${JMETER_HOME}/lib/cmdrunner-2.2.jar \
    --tool org.jmeterplugins.repository.PluginManagerCMD \
    available
```

## Summary

- **CLI mode** is essential for production load tests and CI/CD integration
- **Basic command**: `jmeter -n -t test.jmx -l results.jtl -e -o report`
- **Properties** (`-J`) allow runtime configuration of test parameters
- **HTML reports** (`-e -o`) provide comprehensive visual results
- **CI/CD integration** works with Jenkins, GitHub Actions, GitLab CI, and Azure DevOps
- **Shell scripts** enable repeatable, parameterized test execution

In the next lesson, you'll learn to read and interpret JMeter results to identify performance issues and bottlenecks.

## Additional Resources

- [JMeter CLI Options Reference](https://jmeter.apache.org/usermanual/get-started.html#non_gui) - Official documentation
- [JMeter Dashboard Report](https://jmeter.apache.org/usermanual/generating-dashboard.html) - Report generation guide
- [JMeter Properties Reference](https://jmeter.apache.org/usermanual/properties_reference.html) - All configurable properties

