# Exercise 4: Form Automation with Python

## Objective

Automate a complete form submission workflow including validation, dropdowns, checkboxes, radio buttons, and file uploads using Python Selenium.

## Learning Goals

- Interact with various form elements (text, checkbox, radio, dropdown)
- Handle form validation messages
- Implement file uploads
- Work with the Select class for dropdowns
- Chain form interactions for end-to-end workflows

## Time Estimate

60 minutes

## Prerequisites

- Completed Exercises 1-3
- Read `interact-methods-python.md` content

---

## Core Tasks

### Task 1: Basic Form Interactions (20 minutes)

Create `tests/test_form_basics.py`:

```python
"""
Basic form interaction tests.

Target: https://the-internet.herokuapp.com/login
"""

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import pytest


@pytest.fixture
def driver():
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    driver.implicitly_wait(10)
    yield driver
    driver.quit()


class TestLoginForm:
    """Test the login form functionality."""
    
    def test_successful_login(self, driver):
        """
        Test successful login with valid credentials.
        
        Valid credentials for this test site:
        - Username: tomsmith
        - Password: SuperSecretPassword!
        """
        driver.get("https://the-internet.herokuapp.com/login")
        
        # TODO: Implement login flow
        # 1. Find username field and enter "tomsmith"
        # 2. Find password field and enter "SuperSecretPassword!"
        # 3. Click the login button
        # 4. Verify success message appears
        # 5. Verify URL contains "/secure"
        
        # YOUR CODE HERE
        pass
    
    def test_failed_login_wrong_password(self, driver):
        """
        Test failed login with wrong password.
        
        Should display error message.
        """
        driver.get("https://the-internet.herokuapp.com/login")
        
        # TODO: Implement failed login
        # 1. Enter valid username, wrong password
        # 2. Click login
        # 3. Verify error message is displayed
        # 4. Verify error contains "Your password is invalid!"
        
        # YOUR CODE HERE
        pass
    
    def test_clear_and_retype(self, driver):
        """
        Test clearing field and retyping.
        """
        driver.get("https://the-internet.herokuapp.com/login")
        
        username = driver.find_element(By.ID, "username")
        
        # Type, clear, retype
        username.send_keys("wrong_user")
        username.clear()
        username.send_keys("tomsmith")
        
        assert username.get_attribute("value") == "tomsmith"
    
    def test_logout_after_login(self, driver):
        """
        Test complete login and logout flow.
        """
        driver.get("https://the-internet.herokuapp.com/login")
        
        # TODO: Implement full login/logout cycle
        # 1. Login successfully
        # 2. Find and click logout button
        # 3. Verify redirect to login page
        # 4. Verify logout success message
        
        # YOUR CODE HERE
        pass
```

### Task 2: Dropdown Selection (15 minutes)

Create `tests/test_dropdowns.py`:

```python
"""
Dropdown interaction tests.

Target: https://the-internet.herokuapp.com/dropdown
"""

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import pytest


@pytest.fixture
def driver():
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    driver.implicitly_wait(10)
    driver.get("https://the-internet.herokuapp.com/dropdown")
    yield driver
    driver.quit()


class TestDropdowns:
    """Test dropdown interactions using Select class."""
    
    def test_select_by_visible_text(self, driver):
        """
        Select option by its visible text.
        """
        dropdown = driver.find_element(By.ID, "dropdown")
        select = Select(dropdown)
        
        # Select "Option 1" by visible text
        select.select_by_visible_text("Option 1")
        
        # Verify selection
        selected = select.first_selected_option
        assert selected.text == "Option 1"
    
    def test_select_by_value(self, driver):
        """
        Select option by its value attribute.
        """
        # TODO: Use select_by_value("2") to select Option 2
        # Verify the selection
        
        # YOUR CODE HERE
        pass
    
    def test_select_by_index(self, driver):
        """
        Select option by its index (0-based).
        """
        # TODO: Use select_by_index(1) to select Option 1
        # Note: index 0 is usually the "Please select" option
        
        # YOUR CODE HERE
        pass
    
    def test_get_all_options(self, driver):
        """
        Get all available options from dropdown.
        """
        dropdown = driver.find_element(By.ID, "dropdown")
        select = Select(dropdown)
        
        # Get all options
        all_options = select.options
        option_texts = [opt.text for opt in all_options]
        
        # TODO: Verify expected options are present
        # Should include "Please select an option", "Option 1", "Option 2"
        
        # YOUR CODE HERE
        pass
    
    def test_dropdown_without_select_class(self, driver):
        """
        Some dropdowns aren't <select> elements.
        Practice finding options through regular element interaction.
        """
        # Navigate to dropdown example with custom dropdown
        driver.get("https://the-internet.herokuapp.com/")
        
        # TODO: Find a different type of dropdown
        # and interact with it without Select class
        
        # YOUR CODE HERE
        pass
```

### Task 3: Checkboxes and Radio Buttons (10 minutes)

Create `tests/test_checkboxes.py`:

```python
"""
Checkbox interaction tests.

Target: https://the-internet.herokuapp.com/checkboxes
"""

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import pytest


@pytest.fixture
def driver():
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    driver.implicitly_wait(10)
    driver.get("https://the-internet.herokuapp.com/checkboxes")
    yield driver
    driver.quit()


class TestCheckboxes:
    """Test checkbox interactions."""
    
    def test_check_unchecked_box(self, driver):
        """
        Check a checkbox that starts unchecked.
        """
        checkboxes = driver.find_elements(By.CSS_SELECTOR, "input[type='checkbox']")
        checkbox1 = checkboxes[0]
        
        # Check if already checked
        if not checkbox1.is_selected():
            checkbox1.click()
        
        assert checkbox1.is_selected()
    
    def test_uncheck_checked_box(self, driver):
        """
        Uncheck a checkbox that starts checked.
        """
        checkboxes = driver.find_elements(By.CSS_SELECTOR, "input[type='checkbox']")
        checkbox2 = checkboxes[1]  # This one is checked by default
        
        # TODO: Uncheck the checkbox
        # Verify it's unchecked
        
        # YOUR CODE HERE
        pass
    
    def test_toggle_checkbox(self, driver):
        """
        Toggle checkbox state regardless of initial state.
        """
        # TODO: Implement toggle functionality
        # 1. Get initial state
        # 2. Click to toggle
        # 3. Verify state changed
        
        # YOUR CODE HERE
        pass
    
    def test_select_all_checkboxes(self, driver):
        """
        Ensure all checkboxes are checked.
        """
        checkboxes = driver.find_elements(By.CSS_SELECTOR, "input[type='checkbox']")
        
        for checkbox in checkboxes:
            if not checkbox.is_selected():
                checkbox.click()
        
        # Verify all are checked
        for checkbox in checkboxes:
            assert checkbox.is_selected(), "Not all checkboxes are checked"
```

### Task 4: File Upload (15 minutes)

Create `tests/test_file_upload.py`:

```python
"""
File upload tests.

Target: https://the-internet.herokuapp.com/upload
"""

import os
from pathlib import Path
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import pytest


@pytest.fixture
def driver():
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)
    driver.implicitly_wait(10)
    yield driver
    driver.quit()


@pytest.fixture
def test_file(tmp_path):
    """Create a temporary test file for upload."""
    test_file = tmp_path / "test_upload.txt"
    test_file.write_text("This is a test file for upload testing.")
    return str(test_file)


class TestFileUpload:
    """Test file upload functionality."""
    
    def test_upload_file(self, driver, test_file):
        """
        Upload a file and verify success.
        """
        driver.get("https://the-internet.herokuapp.com/upload")
        
        # Find the file input element
        file_input = driver.find_element(By.ID, "file-upload")
        
        # Send the file path to the input
        # Note: We don't click the input, just send_keys the path
        file_input.send_keys(test_file)
        
        # Click upload button
        upload_button = driver.find_element(By.ID, "file-submit")
        upload_button.click()
        
        # Verify upload success
        # TODO: Verify the uploaded file name appears on the confirmation page
        
        # YOUR CODE HERE
        pass
    
    def test_upload_without_file(self, driver):
        """
        Attempt to upload without selecting a file.
        """
        driver.get("https://the-internet.herokuapp.com/upload")
        
        # TODO: Click upload without selecting file
        # Verify appropriate error handling
        
        # YOUR CODE HERE
        pass
    
    def test_drag_and_drop_upload(self, driver, test_file):
        """
        Test drag and drop file upload if available.
        
        Note: Drag and drop for file upload requires JavaScript execution
        and is more complex. This is an optional advanced exercise.
        """
        driver.get("https://the-internet.herokuapp.com/upload")
        
        # For drag-drop, we need to use JavaScript
        # This is an advanced technique - try if you have time
        
        # YOUR CODE HERE (OPTIONAL)
        pass
```

---

## Expected Output

```
$ pytest tests/test_form_*.py tests/test_dropdown*.py tests/test_checkbox*.py tests/test_file*.py -v

tests/test_form_basics.py::TestLoginForm::test_successful_login PASSED
tests/test_form_basics.py::TestLoginForm::test_failed_login_wrong_password PASSED
tests/test_form_basics.py::TestLoginForm::test_clear_and_retype PASSED
tests/test_form_basics.py::TestLoginForm::test_logout_after_login PASSED
tests/test_dropdowns.py::TestDropdowns::test_select_by_visible_text PASSED
tests/test_dropdowns.py::TestDropdowns::test_select_by_value PASSED
tests/test_dropdowns.py::TestDropdowns::test_select_by_index PASSED
tests/test_dropdowns.py::TestDropdowns::test_get_all_options PASSED
tests/test_checkboxes.py::TestCheckboxes::test_check_unchecked_box PASSED
tests/test_checkboxes.py::TestCheckboxes::test_uncheck_checked_box PASSED
tests/test_checkboxes.py::TestCheckboxes::test_toggle_checkbox PASSED
tests/test_checkboxes.py::TestCheckboxes::test_select_all_checkboxes PASSED
tests/test_file_upload.py::TestFileUpload::test_upload_file PASSED
tests/test_file_upload.py::TestFileUpload::test_upload_without_file PASSED

========================= 14 passed in 35.67s =========================
```

---

## Definition of Done

- [ ] Login form tests pass with valid and invalid credentials
- [ ] Dropdown selection works with all three methods (text, value, index)
- [ ] Checkbox tests verify check, uncheck, and toggle operations
- [ ] File upload successfully sends a file
- [ ] All 14+ tests pass
- [ ] Code uses explicit waits for dynamic content

---

## Hints

<details>
<summary>Hint 1: Waiting for Success Message</summary>

```python
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Wait for success message
success = WebDriverWait(driver, 10).until(
    EC.visibility_of_element_located((By.CSS_SELECTOR, ".flash.success"))
)
assert "logged in" in success.text.lower()
```
</details>

<details>
<summary>Hint 2: Select Class Import</summary>

```python
from selenium.webdriver.support.ui import Select

dropdown = driver.find_element(By.ID, "dropdown")
select = Select(dropdown)

# Three ways to select
select.select_by_visible_text("Option 1")
select.select_by_value("1")
select.select_by_index(1)
```
</details>

<details>
<summary>Hint 3: File Upload Path</summary>

```python
import os

# Get absolute path to ensure it works
file_path = os.path.abspath("test_file.txt")
file_input.send_keys(file_path)
```
</details>

