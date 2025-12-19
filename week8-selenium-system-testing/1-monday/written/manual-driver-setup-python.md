# Manual WebDriver Setup in Python

## Learning Objectives
- Download and configure browser drivers manually for Python Selenium
- Set up the executable path for ChromeDriver, GeckoDriver, and EdgeDriver
- Configure PATH environment variables for driver executables
- Implement cross-browser testing setup (Chrome, Firefox, Edge)
- Troubleshoot common driver configuration issues

## Why This Matters

While automated driver management tools exist (covered in the next topic), understanding manual driver setup is essential for several reasons:

1. **Corporate Environments** - Many organizations restrict automatic downloads
2. **Offline Development** - When working without internet access
3. **Specific Version Control** - Pinning exact driver versions for consistency
4. **Debugging** - Troubleshooting driver issues requires understanding the manual process
5. **CI/CD Pipelines** - Some pipelines require explicit driver management

This foundational knowledge ensures you can configure Selenium in any environment, regardless of network restrictions or organizational policies.

## The Concept

### Understanding the WebDriver Architecture

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Python     │────▶│   WebDriver  │────▶│   Browser    │
│   Script     │     │   (Driver)   │     │   Instance   │
└──────────────┘     └──────────────┘     └──────────────┘
                           │
                     ChromeDriver.exe
                     geckodriver.exe
                     msedgedriver.exe
```

Each browser requires its own driver executable that acts as a bridge between your Python code and the browser.

### Browser Driver Download Sources

| Browser | Driver | Download Location |
|---------|--------|-------------------|
| Chrome | ChromeDriver | https://googlechromelabs.github.io/chrome-for-testing/ |
| Firefox | GeckoDriver | https://github.com/mozilla/geckodriver/releases |
| Edge | EdgeDriver | https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/ |
| Safari | SafariDriver | Built into macOS (enable in Safari preferences) |

### Step 1: Check Your Browser Version

Before downloading a driver, identify your browser version:

**Chrome:**
- Navigate to `chrome://version/` in Chrome
- Or: Menu → Help → About Google Chrome

**Firefox:**
- Navigate to `about:support` in Firefox
- Or: Menu → Help → About Firefox

**Edge:**
- Navigate to `edge://version/` in Edge
- Or: Menu → Help → About Microsoft Edge

### Step 2: Download the Matching Driver

**For Chrome (ChromeDriver):**

1. Visit https://googlechromelabs.github.io/chrome-for-testing/
2. Find your Chrome version (e.g., Chrome 120)
3. Download the matching ChromeDriver for your OS
4. Extract the executable

**For Firefox (GeckoDriver):**

1. Visit https://github.com/mozilla/geckodriver/releases
2. Download the latest release for your OS
3. Extract the executable

**For Edge (EdgeDriver):**

1. Visit https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/
2. Select your Edge version
3. Download for your OS
4. Extract the executable

### Step 3: Setting the Executable Path

There are three methods to configure the driver path in Python:

**Method 1: Direct Path in Code (Explicit)**

```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service

# Specify the exact path to the driver
service = Service(executable_path="C:/WebDrivers/chromedriver.exe")
driver = webdriver.Chrome(service=service)

driver.get("https://example.com")
print(driver.title)
driver.quit()
```

**Method 2: Using Service Object (Recommended)**

```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.firefox.service import Service as FirefoxService
from selenium.webdriver.edge.service import Service as EdgeService

# Chrome
chrome_service = Service("./drivers/chromedriver.exe")
chrome_driver = webdriver.Chrome(service=chrome_service)

# Firefox
firefox_service = FirefoxService("./drivers/geckodriver.exe")
firefox_driver = webdriver.Firefox(service=firefox_service)

# Edge
edge_service = EdgeService("./drivers/msedgedriver.exe")
edge_driver = webdriver.Edge(service=edge_service)
```

**Method 3: System PATH (Most Flexible)**

Add the driver directory to your system PATH, then no path specification is needed:

```python
from selenium import webdriver

# When driver is in PATH, no service needed
driver = webdriver.Chrome()
driver.get("https://example.com")
driver.quit()
```

### Configuring PATH Environment Variables

**Windows (Command Prompt - Temporary):**
```cmd
set PATH=%PATH%;C:\WebDrivers
```

**Windows (PowerShell - Temporary):**
```powershell
$env:PATH += ";C:\WebDrivers"
```

**Windows (Permanent via System Settings):**
1. Press `Win + X` → System
2. Click "Advanced system settings"
3. Click "Environment Variables"
4. Under "System variables", find "Path"
5. Click "Edit" → "New"
6. Add your driver folder path (e.g., `C:\WebDrivers`)
7. Click OK to save

**macOS/Linux (Temporary):**
```bash
export PATH=$PATH:/path/to/drivers
```

**macOS/Linux (Permanent - add to ~/.bashrc or ~/.zshrc):**
```bash
echo 'export PATH=$PATH:/path/to/drivers' >> ~/.bashrc
source ~/.bashrc
```

### Cross-Browser Setup Example

Here's a complete example supporting multiple browsers:

```python
"""
cross_browser_setup.py
Manual driver setup for Chrome, Firefox, and Edge
"""
from selenium import webdriver
from selenium.webdriver.chrome.service import Service as ChromeService
from selenium.webdriver.firefox.service import Service as FirefoxService
from selenium.webdriver.edge.service import Service as EdgeService

# Driver paths - adjust for your system
DRIVERS = {
    "chrome": "C:/WebDrivers/chromedriver.exe",
    "firefox": "C:/WebDrivers/geckodriver.exe",
    "edge": "C:/WebDrivers/msedgedriver.exe"
}

def create_driver(browser_name):
    """Factory function to create browser-specific drivers"""
    browser = browser_name.lower()
    
    if browser == "chrome":
        service = ChromeService(DRIVERS["chrome"])
        return webdriver.Chrome(service=service)
    
    elif browser == "firefox":
        service = FirefoxService(DRIVERS["firefox"])
        return webdriver.Firefox(service=service)
    
    elif browser == "edge":
        service = EdgeService(DRIVERS["edge"])
        return webdriver.Edge(service=service)
    
    else:
        raise ValueError(f"Unsupported browser: {browser_name}")

# Test across multiple browsers
def run_cross_browser_test():
    browsers = ["chrome", "firefox", "edge"]
    
    for browser_name in browsers:
        print(f"\nTesting with {browser_name}...")
        driver = create_driver(browser_name)
        
        try:
            driver.get("https://www.python.org")
            print(f"  Title: {driver.title}")
            print(f"  URL: {driver.current_url}")
        finally:
            driver.quit()
            print(f"  {browser_name} test complete!")

if __name__ == "__main__":
    run_cross_browser_test()
```

### Project Structure for Manual Setup

Organize your drivers in a dedicated folder:

```
my_selenium_project/
├── drivers/
│   ├── chromedriver.exe
│   ├── geckodriver.exe
│   └── msedgedriver.exe
├── tests/
│   ├── __init__.py
│   ├── test_login.py
│   └── test_checkout.py
├── config.py
└── requirements.txt
```

**config.py:**
```python
"""
Configuration file for driver paths
"""
import os

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DRIVER_DIR = os.path.join(BASE_DIR, "drivers")

CHROME_DRIVER = os.path.join(DRIVER_DIR, "chromedriver.exe")
FIREFOX_DRIVER = os.path.join(DRIVER_DIR, "geckodriver.exe")
EDGE_DRIVER = os.path.join(DRIVER_DIR, "msedgedriver.exe")
```

### Troubleshooting Common Issues

#### Issue 1: "chromedriver executable needs to be in PATH"

**Solution:**
```python
# Either add to PATH or specify the full path
from selenium.webdriver.chrome.service import Service
service = Service("C:/full/path/to/chromedriver.exe")
driver = webdriver.Chrome(service=service)
```

#### Issue 2: Driver Version Mismatch

**Error:** "This version of ChromeDriver only supports Chrome version X"

**Solution:**
1. Check your browser version
2. Download the matching driver version
3. Replace the old driver executable

```python
# Check Chrome version programmatically
import subprocess
result = subprocess.run(['chrome', '--version'], capture_output=True, text=True)
print(result.stdout)  # Shows installed Chrome version
```

#### Issue 3: Permission Denied (macOS/Linux)

**Solution:**
```bash
# Make the driver executable
chmod +x /path/to/chromedriver
chmod +x /path/to/geckodriver
```

#### Issue 4: "WebDriverException: unknown error: cannot find Chrome binary"

**Solution:**
```python
from selenium.webdriver.chrome.options import Options

options = Options()
# Specify Chrome binary location if not in default path
options.binary_location = "C:/Program Files/Google/Chrome/Application/chrome.exe"

driver = webdriver.Chrome(options=options)
```

#### Issue 5: Driver Blocked by Antivirus

**Solution:**
1. Add driver location to antivirus exclusions
2. Or download driver from official source to verify authenticity

### Version Compatibility Script

Use this script to check driver-browser compatibility:

```python
"""
version_check.py
Verify driver and browser version compatibility
"""
from selenium import webdriver
from selenium.webdriver.chrome.service import Service

def check_chrome_setup(driver_path):
    try:
        service = Service(driver_path)
        driver = webdriver.Chrome(service=service)
        
        # Get browser version via JavaScript
        browser_version = driver.capabilities['browserVersion']
        driver_version = driver.capabilities['chrome']['chromedriverVersion'].split(' ')[0]
        
        print(f"Chrome Browser Version: {browser_version}")
        print(f"ChromeDriver Version: {driver_version}")
        print("✓ Setup successful!")
        
        driver.quit()
        return True
        
    except Exception as e:
        print(f"✗ Setup failed: {e}")
        return False

if __name__ == "__main__":
    check_chrome_setup("./drivers/chromedriver.exe")
```

## Key Takeaways

1. **Manual setup** requires downloading browser-specific driver executables
2. **Version matching** between browser and driver is critical
3. **Three path options**: direct path, Service object, or system PATH
4. **Cross-browser testing** uses the same patterns with different services
5. **Troubleshooting** usually involves version mismatches or path issues
6. **Understand manual setup** before relying on automated tools

## Additional Resources

- [Chrome for Testing Downloads](https://googlechromelabs.github.io/chrome-for-testing/) - Official ChromeDriver source
- [GeckoDriver Releases](https://github.com/mozilla/geckodriver/releases) - Firefox WebDriver releases
- [Selenium Python Installation Guide](https://selenium-python.readthedocs.io/installation.html) - Official installation documentation

