# Selenium Cheat Sheet

## Setup

### Java
```java
// Maven dependency
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.15.0</version>
</dependency>

// WebDriverManager (auto driver setup)
WebDriverManager.chromedriver().setup();
WebDriver driver = new ChromeDriver();
```

### Python
```python
# Installation
pip install selenium webdriver-manager

# Import
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.chrome.service import Service

# Setup
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)
```

## Locator Strategies

| Strategy | Java | Python |
|----------|------|--------|
| ID | `By.id("id")` | `By.ID, "id"` |
| Name | `By.name("name")` | `By.NAME, "name"` |
| Class | `By.className("class")` | `By.CLASS_NAME, "class"` |
| Tag | `By.tagName("div")` | `By.TAG_NAME, "div"` |
| Link Text | `By.linkText("Click")` | `By.LINK_TEXT, "Click"` |
| Partial Link | `By.partialLinkText("Cli")` | `By.PARTIAL_LINK_TEXT, "Cli"` |
| CSS | `By.cssSelector("#id")` | `By.CSS_SELECTOR, "#id"` |
| XPath | `By.xpath("//div")` | `By.XPATH, "//div"` |

## Common XPath Patterns

```xpath
//tag[@attribute='value']     # By attribute
//input[@id='username']       # By ID
//button[@class='submit']     # By class
//div[contains(@class,'btn')] # Contains
//span[text()='Click Me']     # By text
//a[contains(text(),'Link')]  # Contains text
//input[@type='submit']       # By type
(//div[@class='item'])[1]     # First match
//div[@class='parent']/child  # Parent/child
//child/..                    # Parent of child
//preceding-sibling::div      # Previous sibling
//following-sibling::span     # Next sibling
```

## Common CSS Selectors

```css
#id                    /* By ID */
.class                 /* By class */
tag                    /* By tag */
tag#id                 /* Tag with ID */
tag.class              /* Tag with class */
[attribute='value']    /* By attribute */
input[type='text']     /* Attribute equals */
[class*='partial']     /* Contains */
[class^='start']       /* Starts with */
[class$='end']         /* Ends with */
parent > child         /* Direct child */
ancestor descendant    /* Any descendant */
element + sibling      /* Adjacent sibling */
element ~ sibling      /* General sibling */
:first-child           /* First child */
:last-child            /* Last child */
:nth-child(2)          /* Second child */
```

## Element Interactions

### Java
```java
// Find elements
WebElement element = driver.findElement(By.id("id"));
List<WebElement> elements = driver.findElements(By.className("class"));

// Actions
element.click();
element.sendKeys("text");
element.clear();
element.submit();

// Get info
element.getText();
element.getAttribute("href");
element.getCssValue("color");
element.isDisplayed();
element.isEnabled();
element.isSelected();
```

### Python
```python
# Find elements
element = driver.find_element(By.ID, "id")
elements = driver.find_elements(By.CLASS_NAME, "class")

# Actions
element.click()
element.send_keys("text")
element.clear()
element.submit()

# Get info
element.text
element.get_attribute("href")
element.value_of_css_property("color")
element.is_displayed()
element.is_enabled()
element.is_selected()
```

## Waits

### Java
```java
// Implicit wait (global)
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

// Explicit wait
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement elem = wait.until(
    ExpectedConditions.visibilityOfElementLocated(By.id("id"))
);

// Common ExpectedConditions
ExpectedConditions.visibilityOfElementLocated(locator)
ExpectedConditions.elementToBeClickable(locator)
ExpectedConditions.presenceOfElementLocated(locator)
ExpectedConditions.textToBePresentInElement(element, "text")
ExpectedConditions.urlContains("dashboard")
ExpectedConditions.alertIsPresent()
```

### Python
```python
# Implicit wait (global)
driver.implicitly_wait(10)

# Explicit wait
wait = WebDriverWait(driver, 10)
element = wait.until(
    EC.visibility_of_element_located((By.ID, "id"))
)

# Common ExpectedConditions
EC.visibility_of_element_located((By.ID, "id"))
EC.element_to_be_clickable((By.ID, "id"))
EC.presence_of_element_located((By.ID, "id"))
EC.text_to_be_present_in_element((By.ID, "id"), "text")
EC.url_contains("dashboard")
EC.alert_is_present()
```

## Browser Actions

### Java
```java
// Navigation
driver.get("https://example.com");
driver.navigate().to("https://example.com");
driver.navigate().back();
driver.navigate().forward();
driver.navigate().refresh();

// Window
driver.manage().window().maximize();
driver.manage().window().setSize(new Dimension(1920, 1080));
driver.getTitle();
driver.getCurrentUrl();

// Close
driver.close();   // Current window
driver.quit();    // All windows
```

### Python
```python
# Navigation
driver.get("https://example.com")
driver.back()
driver.forward()
driver.refresh()

# Window
driver.maximize_window()
driver.set_window_size(1920, 1080)
driver.title
driver.current_url

# Close
driver.close()   # Current window
driver.quit()    # All windows
```

## Special Interactions

### Dropdowns (Select)
```java
// Java
Select dropdown = new Select(driver.findElement(By.id("dropdown")));
dropdown.selectByVisibleText("Option 1");
dropdown.selectByValue("value1");
dropdown.selectByIndex(0);
```

```python
# Python
from selenium.webdriver.support.ui import Select
dropdown = Select(driver.find_element(By.ID, "dropdown"))
dropdown.select_by_visible_text("Option 1")
dropdown.select_by_value("value1")
dropdown.select_by_index(0)
```

### Alerts
```java
// Java
Alert alert = driver.switchTo().alert();
alert.getText();
alert.accept();
alert.dismiss();
alert.sendKeys("text");
```

```python
# Python
alert = driver.switch_to.alert
alert.text
alert.accept()
alert.dismiss()
alert.send_keys("text")
```

### Frames
```java
// Java
driver.switchTo().frame("frameName");
driver.switchTo().frame(0);  // By index
driver.switchTo().frame(element);
driver.switchTo().defaultContent();  // Exit frame
```

```python
# Python
driver.switch_to.frame("frameName")
driver.switch_to.frame(0)  # By index
driver.switch_to.frame(element)
driver.switch_to.default_content()  # Exit frame
```

### Windows/Tabs
```java
// Java
String mainWindow = driver.getWindowHandle();
Set<String> handles = driver.getWindowHandles();
for (String handle : handles) {
    driver.switchTo().window(handle);
}
driver.switchTo().window(mainWindow);
```

```python
# Python
main_window = driver.current_window_handle
handles = driver.window_handles
for handle in handles:
    driver.switch_to.window(handle)
driver.switch_to.window(main_window)
```

### Screenshots
```java
// Java
File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
FileUtils.copyFile(screenshot, new File("screenshot.png"));
```

```python
# Python
driver.save_screenshot("screenshot.png")
element.screenshot("element.png")
```

### Actions API
```java
// Java
Actions actions = new Actions(driver);
actions.moveToElement(element).click().perform();
actions.doubleClick(element).perform();
actions.contextClick(element).perform();  // Right-click
actions.dragAndDrop(source, target).perform();
actions.sendKeys(Keys.ENTER).perform();
```

```python
# Python
from selenium.webdriver import ActionChains, Keys
actions = ActionChains(driver)
actions.move_to_element(element).click().perform()
actions.double_click(element).perform()
actions.context_click(element).perform()  # Right-click
actions.drag_and_drop(source, target).perform()
actions.send_keys(Keys.ENTER).perform()
```

## Browser Options

### Java
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless");
options.addArguments("--disable-gpu");
options.addArguments("--window-size=1920,1080");
options.addArguments("--incognito");
WebDriver driver = new ChromeDriver(options);
```

### Python
```python
from selenium.webdriver.chrome.options import Options
options = Options()
options.add_argument("--headless")
options.add_argument("--disable-gpu")
options.add_argument("--window-size=1920,1080")
options.add_argument("--incognito")
driver = webdriver.Chrome(options=options)
```
