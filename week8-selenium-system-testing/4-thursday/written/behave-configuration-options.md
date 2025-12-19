# Behave Configuration Options

## Learning Objectives
- Configure Behave using the `behave.ini` configuration file
- Understand command line options for test execution
- Apply configuration precedence rules
- Set default behaviors and formatter options
- Configure output and reporting settings

## Why This Matters

Proper configuration enables:
- Consistent test execution across team members
- CI/CD pipeline integration
- Selective test running for different purposes
- Customized output formats for reporting

## The Concept

### Configuration Methods

Behave can be configured through multiple methods:

```
┌─────────────────────────────────────────────────────────────────┐
│              Behave Configuration Methods                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Priority (highest to lowest):                                   │
│                                                                  │
│  1. Command line arguments     behave --tags=@smoke             │
│                                        │                         │
│  2. behave.ini / .behaverc            ▼                         │
│     [behave]                    Overrides lower                 │
│     tags = @smoke               priority settings               │
│                                        │                         │
│  3. setup.cfg                         ▼                         │
│     [behave]                                                    │
│     tags = @smoke                                               │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### behave.ini Configuration File

Create `behave.ini` in your project root:

```ini
[behave]
# ==================== PATHS ====================
# Path to feature files (default: features)
paths = features

# ==================== FORMATTING ====================
# Output format: pretty, plain, progress, json, etc.
format = pretty

# Show source code location
show_source = true

# Show timings for each step
show_timings = true

# Colored output
color = true

# ==================== FILTERING ====================
# Default tags to run
tags = not @wip and not @skip

# Name pattern to filter scenarios
# name = .*login.*

# ==================== EXECUTION ====================
# Stop on first failure
stop = false

# Dry run (check steps without executing)
dry_run = false

# ==================== OUTPUT ====================
# Quiet mode (minimal output)
quiet = false

# Log capture level
log_capture = true
logging_level = INFO

# Screenshot directory
# screenshot_directory = screenshots

# ==================== JUNIT ====================
# JUnit XML output directory
junit = false
junit_directory = reports/junit

# ==================== ADVANCED ====================
# Default step definitions location
default_tags = 

# User data (accessible via context.config.userdata)
# D = environment:staging
```

### Common Configuration Options

| Option | Default | Description |
|--------|---------|-------------|
| `paths` | `features` | Feature file directory |
| `format` | `pretty` | Output formatter |
| `tags` | (none) | Default tag filter |
| `stop` | `false` | Stop on first failure |
| `dry_run` | `false` | Check without executing |
| `quiet` | `false` | Minimal output |
| `color` | `true` | Colored output |
| `show_source` | `true` | Show step locations |
| `show_timings` | `true` | Show step durations |
| `log_capture` | `true` | Capture log output |
| `junit` | `false` | Generate JUnit XML |
| `junit_directory` | `reports` | JUnit output path |

### Command Line Options

**Basic Options:**
```bash
# Run all tests
behave

# Run specific feature file
behave features/login.feature

# Run specific scenario by name
behave --name "Successful login"

# Run with tags
behave --tags=@smoke
behave --tags="@smoke and not @wip"
behave --tags="@regression or @smoke"

# Dry run (verify steps without execution)
behave --dry-run

# Stop on first failure
behave --stop

# Quiet mode
behave --quiet
```

**Output Options:**
```bash
# Different formatters
behave --format=pretty        # Human-readable
behave --format=plain         # Minimal output
behave --format=progress      # Dots for progress
behave --format=json          # JSON output
behave --format=json.pretty   # Pretty JSON

# Write output to file
behave --format=json --outfile=report.json

# Multiple formatters
behave --format=pretty --format=json --outfile=report.json

# JUnit XML output
behave --junit --junit-directory=reports/

# No capture (show print statements)
behave --no-capture

# Show source locations
behave --show-source
```

**Filtering Options:**
```bash
# By tag
behave --tags=@smoke

# Tag expressions
behave --tags="@smoke and @login"
behave --tags="@smoke or @regression"
behave --tags="not @wip"
behave --tags="(@smoke or @regression) and not @slow"

# By scenario name (regex)
behave --name=".*login.*"

# Include/exclude features
behave --include="login"
behave --exclude="wip"
```

### Configuration Precedence

```python
# Configuration priority demonstration

# 1. Command line (highest priority)
# behave --tags=@smoke

# 2. behave.ini
# [behave]
# tags = @regression

# 3. setup.cfg
# [behave]
# tags = @all

# Result: @smoke is used (command line wins)
```

### Default Settings Configuration

**behave.ini for Development:**
```ini
[behave]
# Development-friendly defaults
format = pretty
color = true
show_source = true
show_timings = true
stop = true
log_capture = true
logging_level = DEBUG
```

**behave.ini for CI/CD:**
```ini
[behave]
# CI-friendly defaults
format = plain
color = false
show_source = false
junit = true
junit_directory = reports/junit
tags = @smoke or @regression
```

### Formatter Configuration

**Available Formatters:**

| Formatter | Description | Use Case |
|-----------|-------------|----------|
| `pretty` | Colored, readable output | Development |
| `plain` | No colors, simple text | CI logs |
| `progress` | Dot notation (. F S) | Quick status |
| `json` | JSON output | Reporting tools |
| `json.pretty` | Formatted JSON | Debugging |
| `null` | No output | Performance testing |
| `rerun` | Failed scenarios list | Re-running failures |

**Multiple Formatters:**
```bash
# Console output + JSON report
behave --format=pretty --format=json --outfile=report.json

# Progress + JUnit for CI
behave --format=progress --junit
```

### Output Configuration

```ini
[behave]
# Capture stdout/stderr
stdout_capture = true
stderr_capture = true
log_capture = true

# Logging configuration
logging_level = INFO
logging_format = %(levelname)s:%(name)s:%(message)s

# Summary configuration
show_skipped = true
show_timings = true
```

### User Data Configuration

Pass custom data to tests via configuration:

**behave.ini:**
```ini
[behave.userdata]
environment = staging
browser = chrome
base_url = https://staging.example.com
timeout = 30
```

**Access in environment.py:**
```python
def before_all(context):
    # Access userdata
    userdata = context.config.userdata
    
    context.environment = userdata.get('environment', 'development')
    context.browser_name = userdata.get('browser', 'chrome')
    context.base_url = userdata.get('base_url', 'http://localhost')
    context.timeout = int(userdata.get('timeout', 10))
    
    print(f"Running tests on {context.environment} environment")
```

**Override from command line:**
```bash
behave -D environment=production -D browser=firefox
```

### Environment-Specific Configuration

Create multiple configuration files:

**behave-staging.ini:**
```ini
[behave]
tags = @smoke
format = pretty

[behave.userdata]
environment = staging
base_url = https://staging.example.com
```

**behave-production.ini:**
```ini
[behave]
tags = @smoke and @safe
format = json
junit = true

[behave.userdata]
environment = production
base_url = https://www.example.com
```

**Run with specific config:**
```bash
# Set environment variable
export BEHAVE_CONFIG=behave-staging.ini
behave

# Or use command line
behave --config=behave-staging.ini
```

### Complete Configuration Example

**behave.ini:**
```ini
[behave]
# ========================================
# Behave Configuration
# ========================================

# Paths
paths = features

# Output format
format = pretty
show_source = true
show_timings = true
color = true

# Default filters (can be overridden via CLI)
tags = not @wip and not @manual

# Execution behavior
stop = false
dry_run = false

# Logging
log_capture = true
logging_level = INFO
logging_format = %(asctime)s - %(levelname)s - %(name)s - %(message)s

# JUnit reporting
junit = false
junit_directory = reports/junit

# Screenshot settings (custom)
# Accessed via context.config.userdata

[behave.userdata]
# Environment settings
environment = development
base_url = http://localhost:8080

# Browser settings
browser = chrome
headless = false
window_size = 1920x1080

# Timeouts
implicit_wait = 10
explicit_wait = 30
page_load_timeout = 60

# Paths
screenshot_dir = screenshots
download_dir = downloads

# Feature flags
debug_mode = false
```

### Accessing Configuration in Code

```python
# features/environment.py
def before_all(context):
    """Access all configuration options"""
    
    # Built-in config
    config = context.config
    
    # Paths
    print(f"Feature paths: {config.paths}")
    
    # Formatters
    print(f"Formatters: {config.format}")
    
    # Tags
    print(f"Tags: {config.tags}")
    
    # User data
    userdata = config.userdata
    print(f"Environment: {userdata.get('environment')}")
    print(f"Base URL: {userdata.get('base_url')}")
    
    # Setup based on config
    if userdata.get('headless', 'false').lower() == 'true':
        setup_headless_browser(context)
    else:
        setup_browser(context)
```

## Key Takeaways

1. **behave.ini** provides default configuration
2. **Command line** overrides configuration file settings
3. **Multiple formatters** can run simultaneously
4. **User data** enables custom configuration values
5. **Environment-specific** configs support different test environments
6. **Tag expressions** filter which tests run

## Additional Resources

- [Behave Configuration](https://behave.readthedocs.io/en/stable/behave.html#configuration-files) - Official documentation
- [Command Line Options](https://behave.readthedocs.io/en/stable/behave.html#command-line-arguments) - Complete CLI reference
- [Behave Formatters](https://behave.readthedocs.io/en/stable/formatters.html) - Available output formats

