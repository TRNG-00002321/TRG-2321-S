# Browser Options in Python Selenium

## Learning Objectives
- Configure browser behavior using Options classes (ChromeOptions, FirefoxOptions, EdgeOptions)
- Run browsers in headless mode for CI/CD environments
- Set browser arguments and preferences programmatically
- Configure experimental options and capabilities
- Implement production-ready browser configurations

## Why This Matters

Browser options control how Selenium launches and interacts with browsers. In real-world testing scenarios, you'll need to:

- Run tests **headlessly** in CI/CD pipelines (no GUI)
- Disable browser features that interfere with automation
- Configure **proxy settings** for corporate networks
- Set **download directories** for file download tests
- Emulate **mobile devices** for responsive testing
- Improve **test stability** by disabling problematic features

Mastering browser options transforms your tests from brittle desktop scripts to robust, production-ready automation that runs anywhere.

## The Concept

### Browser Options Classes

Each browser has its own Options class:

```python
from selenium.webdriver.chrome.options import Options as ChromeOptions
from selenium.webdriver.firefox.options import Options as FirefoxOptions
from selenium.webdriver.edge.options import Options as EdgeOptions
```

### ChromeOptions Configuration

**Basic Usage:**

```python
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

# Create options object
options = Options()

# Add arguments
options.add_argument("--start-maximized")

# Create driver with options
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service, options=options)

driver.get("https://example.com")
driver.quit()
```

### Headless Mode Configuration

Headless mode runs the browser without a visible GUI—essential for CI/CD:

**Chrome Headless:**

```python
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

options = Options()

# New headless mode (Chrome 109+)
options.add_argument("--headless=new")

# Required for stability in headless mode
options.add_argument("--no-sandbox")
options.add_argument("--disable-dev-shm-usage")

# Set window size (important for headless - no auto-maximize)
options.add_argument("--window-size=1920,1080")

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service, options=options)

driver.get("https://example.com")
print(f"Running headless: {driver.title}")
driver.quit()
```

**Firefox Headless:**

```python
from selenium import webdriver
from selenium.webdriver.firefox.options import Options
from selenium.webdriver.firefox.service import Service
from webdriver_manager.firefox import GeckoDriverManager

options = Options()
options.add_argument("--headless")
options.add_argument("--width=1920")
options.add_argument("--height=1080")

service = Service(GeckoDriverManager().install())
driver = webdriver.Firefox(service=service, options=options)

driver.get("https://example.com")
print(f"Firefox headless: {driver.title}")
driver.quit()
```

### Common Browser Arguments

**Chrome Arguments:**

```python
options = Options()

# Window control
options.add_argument("--start-maximized")       # Start maximized
options.add_argument("--window-size=1920,1080") # Set specific size
options.add_argument("--start-fullscreen")      # Fullscreen mode

# Stability arguments (recommended for automation)
options.add_argument("--no-sandbox")            # Required for Docker/CI
options.add_argument("--disable-dev-shm-usage") # Overcome limited resources
options.add_argument("--disable-gpu")           # Disable GPU hardware acceleration
options.add_argument("--disable-extensions")    # Disable extensions

# Security/Privacy
options.add_argument("--incognito")             # Incognito mode
options.add_argument("--disable-popup-blocking")# Allow popups

# Performance
options.add_argument("--disable-images")        # Don't load images
options.add_argument("--blink-settings=imagesEnabled=false")

# Network
options.add_argument("--proxy-server=http://proxy:8080")

# User agent
options.add_argument("--user-agent=Custom User Agent String")
```

**Firefox Arguments:**

```python
from selenium.webdriver.firefox.options import Options

options = Options()

options.add_argument("-headless")               # Headless mode
options.add_argument("-width=1920")             # Window width
options.add_argument("-height=1080")            # Window height
options.add_argument("-private")                # Private browsing
```

### Browser Preferences

Preferences control browser behavior settings:

**Chrome Preferences:**

```python
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

options = Options()

# Set preferences using a dictionary
prefs = {
    # Download settings
    "download.default_directory": "C:/Downloads/selenium",
    "download.prompt_for_download": False,
    "download.directory_upgrade": True,
    
    # Disable PDF viewer (download PDFs instead)
    "plugins.always_open_pdf_externally": True,
    
    # Disable password manager
    "credentials_enable_service": False,
    "profile.password_manager_enabled": False,
    
    # Disable notifications
    "profile.default_content_setting_values.notifications": 2,
    
    # Disable geolocation prompt
    "profile.default_content_setting_values.geolocation": 2,
}

options.add_experimental_option("prefs", prefs)

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service, options=options)
```

**Firefox Preferences:**

```python
from selenium import webdriver
from selenium.webdriver.firefox.options import Options
from selenium.webdriver.firefox.service import Service
from webdriver_manager.firefox import GeckoDriverManager

options = Options()

# Set Firefox preferences
options.set_preference("browser.download.folderList", 2)
options.set_preference("browser.download.dir", "C:/Downloads/selenium")
options.set_preference("browser.download.useDownloadDir", True)
options.set_preference("browser.helperApps.neverAsk.saveToDisk", 
                       "application/pdf,application/octet-stream")

# Disable notifications
options.set_preference("dom.webnotifications.enabled", False)

# Disable geolocation
options.set_preference("geo.enabled", False)

service = Service(GeckoDriverManager().install())
driver = webdriver.Firefox(service=service, options=options)
```

### Experimental Options (Chrome)

Chrome supports additional experimental options:

```python
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

options = Options()

# Exclude automation switches (reduce detection)
options.add_experimental_option("excludeSwitches", ["enable-automation"])
options.add_experimental_option("useAutomationExtension", False)

# Disable "Chrome is being controlled by automated software" infobar
options.add_experimental_option("excludeSwitches", ["enable-automation"])

# Set custom capabilities
options.add_experimental_option("w3c", True)

# Detach browser (keep open after script ends) - useful for debugging
options.add_experimental_option("detach", True)

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service, options=options)
```

### Mobile Emulation

Test responsive designs by emulating mobile devices:

```python
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

options = Options()

# Option 1: Use predefined device
mobile_emulation = {"deviceName": "iPhone 12 Pro"}
options.add_experimental_option("mobileEmulation", mobile_emulation)

# Option 2: Custom device settings
mobile_emulation = {
    "deviceMetrics": {"width": 375, "height": 812, "pixelRatio": 3.0},
    "userAgent": "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15"
}
options.add_experimental_option("mobileEmulation", mobile_emulation)

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service, options=options)

driver.get("https://www.google.com")
print(f"Testing mobile view: {driver.get_window_size()}")
driver.quit()
```

### Capabilities Setup

Capabilities define browser features and behaviors:

```python
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

options = Options()

# Page load strategy
options.page_load_strategy = 'normal'   # Wait for full page load (default)
# options.page_load_strategy = 'eager'  # Wait for DOMContentLoaded
# options.page_load_strategy = 'none'   # Return immediately

# Accept insecure certificates
options.accept_insecure_certs = True

# Strict file interactability
options.strict_file_interactability = True

# Timeouts (in milliseconds)
options.timeouts = {
    'implicit': 10000,
    'pageLoad': 30000,
    'script': 30000
}

service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service, options=options)
```

### Production-Ready Configuration

Here's a complete configuration for CI/CD environments:

```python
"""
production_config.py
Production-ready browser configuration
"""
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import os

def create_production_driver(headless=True, download_dir=None):
    """
    Creates a Chrome driver configured for production/CI environments
    
    Args:
        headless: Run without GUI (default True for CI)
        download_dir: Custom download directory
    
    Returns:
        Configured WebDriver instance
    """
    options = Options()
    
    # Headless configuration
    if headless:
        options.add_argument("--headless=new")
        options.add_argument("--window-size=1920,1080")
    else:
        options.add_argument("--start-maximized")
    
    # Stability arguments
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--disable-gpu")
    options.add_argument("--disable-extensions")
    options.add_argument("--disable-popup-blocking")
    
    # Reduce detection
    options.add_experimental_option("excludeSwitches", ["enable-automation"])
    options.add_experimental_option("useAutomationExtension", False)
    
    # Preferences
    prefs = {
        "credentials_enable_service": False,
        "profile.password_manager_enabled": False,
        "profile.default_content_setting_values.notifications": 2,
    }
    
    # Download directory
    if download_dir:
        prefs["download.default_directory"] = download_dir
        prefs["download.prompt_for_download"] = False
        prefs["download.directory_upgrade"] = True
    
    options.add_experimental_option("prefs", prefs)
    
    # Page load strategy
    options.page_load_strategy = 'normal'
    
    # Accept insecure certs (for test environments)
    options.accept_insecure_certs = True
    
    # Create driver
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    
    # Set timeouts
    driver.implicitly_wait(10)
    driver.set_page_load_timeout(30)
    driver.set_script_timeout(30)
    
    return driver


# Usage examples
if __name__ == "__main__":
    # Headless for CI
    driver = create_production_driver(headless=True)
    driver.get("https://example.com")
    print(f"Headless test: {driver.title}")
    driver.quit()
    
    # With GUI for local development
    driver = create_production_driver(headless=False)
    driver.get("https://example.com")
    print(f"GUI test: {driver.title}")
    driver.quit()
```

### EdgeOptions Configuration

Microsoft Edge uses similar patterns:

```python
from selenium import webdriver
from selenium.webdriver.edge.options import Options
from selenium.webdriver.edge.service import Service
from webdriver_manager.microsoft import EdgeChromiumDriverManager

options = Options()

# Headless
options.add_argument("--headless=new")
options.add_argument("--window-size=1920,1080")

# Stability
options.add_argument("--no-sandbox")
options.add_argument("--disable-dev-shm-usage")

# InPrivate mode
options.add_argument("--inprivate")

# Preferences (same as Chrome - Edge is Chromium-based)
prefs = {
    "download.default_directory": "C:/Downloads",
    "download.prompt_for_download": False,
}
options.add_experimental_option("prefs", prefs)

service = Service(EdgeChromiumDriverManager().install())
driver = webdriver.Edge(service=service, options=options)
```

## Key Takeaways

1. **Options classes** control browser launch configuration and behavior
2. **Headless mode** is essential for CI/CD—always include stability arguments
3. **Preferences** control browser settings like downloads and notifications
4. **Experimental options** provide Chrome-specific advanced configurations
5. **Mobile emulation** enables responsive testing without physical devices
6. **Production configurations** should disable automation detection and ensure stability

## Additional Resources

- [Chrome Command Line Switches](https://peter.sh/experiments/chromium-command-line-switches/) - Complete list of Chrome arguments
- [ChromeDriver Capabilities](https://chromedriver.chromium.org/capabilities) - Official capabilities documentation
- [Firefox Preferences](https://searchfox.org/mozilla-release/source/modules/libpref/init/all.js) - All Firefox preferences

