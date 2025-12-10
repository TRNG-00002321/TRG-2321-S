# Lab: Allure Attachments - Screenshots, Logs, and Data

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | integrating-allure-into-tests.md |

## Learning Objectives
By completing this exercise, you will:
- Add text attachments to test reports
- Attach JSON data for debugging
- Attach screenshots (simulated)
- Attach log files
- Use attachments effectively in both Java and Python

## The Scenario

Test failures are hard to debug with just pass/fail status. You'll add attachments to your tests so that when failures occur, all relevant data is captured in the Allure report.

## Core Tasks

### Task 1: Java - Text and JSON Attachments (15 minutes)

```java
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import com.fasterxml.jackson.databind.ObjectMapper;

class PaymentServiceTest {
    
    private ObjectMapper mapper = new ObjectMapper();
    
    @Test
    @Description("Test payment processing with detailed logging")
    void processPayment_logsAllDetails() throws Exception {
        PaymentRequest request = new PaymentRequest("4111111111111111", 100.00);
        
        // Attach the request as JSON
        attachJson("Payment Request", mapper.writeValueAsString(request));
        
        PaymentResult result = paymentService.process(request);
        
        // Attach the response
        attachJson("Payment Response", mapper.writeValueAsString(result));
        
        // Attach any logs
        attachText("Transaction Log", getTransactionLog());
        
        assertEquals("SUCCESS", result.getStatus());
    }
    
    @Attachment(value = "{name}", type = "application/json")
    public String attachJson(String name, String json) {
        return json;
    }
    
    @Attachment(value = "{name}", type = "text/plain")
    public String attachText(String name, String text) {
        return text;
    }
    
    private String getTransactionLog() {
        return """
            [2024-01-15 10:30:00] INFO: Payment initiated
            [2024-01-15 10:30:01] INFO: Card validated
            [2024-01-15 10:30:02] INFO: Amount authorized
            [2024-01-15 10:30:03] INFO: Payment completed
            """;
    }
}
```

### Task 2: Java - Screenshot Attachments (10 minutes)

```java
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

class UITestWithScreenshots {
    
    @Test
    @Description("Test with screenshot on failure")
    void loginTest_capturesScreenshotOnFailure() {
        try {
            // Simulate UI test
            boolean loginSuccess = performLogin("user", "wrong_password");
            
            if (!loginSuccess) {
                // Capture screenshot on failure
                attachScreenshot("Login Failure Screenshot");
            }
            
            assertTrue(loginSuccess, "Login should succeed");
            
        } catch (AssertionError e) {
            attachScreenshot("Assertion Failure Screenshot");
            throw e;
        }
    }
    
    @Attachment(value = "{name}", type = "image/png")
    public byte[] attachScreenshot(String name) {
        // In real UI tests, this would capture actual screenshot
        // For demo, we'll load a placeholder image
        try {
            return Files.readAllBytes(Path.of("src/test/resources/placeholder.png"));
        } catch (Exception e) {
            return createPlaceholderImage();
        }
    }
    
    private byte[] createPlaceholderImage() {
        // Return a minimal PNG (1x1 pixel)
        return new byte[] {
            (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            // ... PNG data
        };
    }
}
```

### Task 3: Python - Various Attachment Types (15 minutes)

```python
import allure
import json
import pytest


class TestPaymentWithAttachments:
    
    def test_process_payment_with_logging(self):
        """Test payment with detailed attachments."""
        
        # Attach request data
        request_data = {
            "card_number": "4111111111111111",
            "amount": 100.00,
            "currency": "USD"
        }
        allure.attach(
            json.dumps(request_data, indent=2),
            name="Payment Request",
            attachment_type=allure.attachment_type.JSON
        )
        
        # Simulate processing
        result = self._process_payment(request_data)
        
        # Attach response
        allure.attach(
            json.dumps(result, indent=2),
            name="Payment Response",
            attachment_type=allure.attachment_type.JSON
        )
        
        # Attach logs
        logs = self._get_logs()
        allure.attach(
            logs,
            name="Transaction Logs",
            attachment_type=allure.attachment_type.TEXT
        )
        
        assert result["status"] == "SUCCESS"
    
    def test_api_response_with_html_attachment(self):
        """Attach formatted HTML for rich display."""
        
        html_content = """
        <html>
        <body>
            <h2>API Response Summary</h2>
            <table border="1">
                <tr><th>Field</th><th>Value</th></tr>
                <tr><td>Status</td><td style="color:green">SUCCESS</td></tr>
                <tr><td>Transaction ID</td><td>TXN-123456</td></tr>
                <tr><td>Amount</td><td>$100.00</td></tr>
            </table>
        </body>
        </html>
        """
        
        allure.attach(
            html_content,
            name="Response Summary",
            attachment_type=allure.attachment_type.HTML
        )
        
        assert True
    
    def test_with_csv_data(self):
        """Attach CSV data for tabular information."""
        
        csv_data = """
order_id,product,quantity,price
1001,Widget A,5,10.00
1002,Widget B,3,15.00
1003,Widget C,2,25.00
        """.strip()
        
        allure.attach(
            csv_data,
            name="Order Data",
            attachment_type=allure.attachment_type.CSV
        )
        
        # Parse and verify
        lines = csv_data.split('\n')
        assert len(lines) == 4  # Header + 3 rows
    
    def _process_payment(self, request):
        return {"status": "SUCCESS", "transaction_id": "TXN-123"}
    
    def _get_logs(self):
        return """
[INFO] 2024-01-15 10:30:00 - Payment initiated
[INFO] 2024-01-15 10:30:01 - Card validation passed
[INFO] 2024-01-15 10:30:02 - Authorization successful
[INFO] 2024-01-15 10:30:03 - Payment completed
        """.strip()
```

### Task 4: Python - Screenshot on Failure (10 minutes)

```python
import allure
import pytest
from pathlib import Path


@pytest.fixture
def attach_screenshot_on_failure(request):
    """Fixture to attach screenshot on test failure."""
    yield
    
    if request.node.rep_call.failed:
        # In real scenario, capture actual screenshot
        screenshot_path = Path("tests/resources/failure_screenshot.png")
        
        if screenshot_path.exists():
            allure.attach.file(
                str(screenshot_path),
                name="Failure Screenshot",
                attachment_type=allure.attachment_type.PNG
            )
        else:
            # Attach text placeholder
            allure.attach(
                "Screenshot capture failed - file not found",
                name="Screenshot Error",
                attachment_type=allure.attachment_type.TEXT
            )


@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """Hook to capture test result for fixture."""
    outcome = yield
    rep = outcome.get_result()
    setattr(item, f"rep_{rep.when}", rep)


class TestUIWithScreenshots:
    
    @pytest.mark.usefixtures("attach_screenshot_on_failure")
    def test_login_captures_screenshot_on_failure(self):
        """This test will attach screenshot if it fails."""
        
        # Simulate a failing login test
        login_result = self._perform_login("user", "password")
        
        # This assertion might fail
        assert login_result is True, "Login failed"
    
    def _perform_login(self, username, password):
        # Simulate login - return False to trigger failure
        return True  # Change to False to see screenshot attachment
```

### Task 5: Conditional Attachments (10 minutes)

```python
import allure
import os


class TestConditionalAttachments:
    
    def test_with_environment_info(self):
        """Attach environment information for debugging."""
        
        env_info = {
            "python_version": os.sys.version,
            "platform": os.sys.platform,
            "cwd": os.getcwd(),
        }
        
        allure.attach(
            json.dumps(env_info, indent=2),
            name="Test Environment",
            attachment_type=allure.attachment_type.JSON
        )
        
        assert True
    
    def test_attach_on_specific_condition(self):
        """Only attach data when relevant."""
        
        result = self._complex_calculation()
        
        # Only attach if result is unexpected
        if result > 100:
            allure.attach(
                f"Unexpected result: {result}",
                name="Warning: High Value",
                attachment_type=allure.attachment_type.TEXT
            )
        
        # Always attach summary
        with allure.step("Attach calculation summary"):
            allure.attach(
                f"Final result: {result}",
                name="Calculation Result",
                attachment_type=allure.attachment_type.TEXT
            )
        
        assert result < 1000
    
    def _complex_calculation(self):
        return 42
```

## Allure Attachment Types

| Type | Python | Java |
|------|--------|------|
| Text | `allure.attachment_type.TEXT` | `"text/plain"` |
| JSON | `allure.attachment_type.JSON` | `"application/json"` |
| HTML | `allure.attachment_type.HTML` | `"text/html"` |
| CSV | `allure.attachment_type.CSV` | `"text/csv"` |
| PNG | `allure.attachment_type.PNG` | `"image/png"` |
| XML | `allure.attachment_type.XML` | `"application/xml"` |

## Definition of Done

- [ ] Java: At least 2 text/JSON attachments
- [ ] Java: At least 1 screenshot attachment example
- [ ] Python: At least 3 different attachment types used
- [ ] Python: Screenshot on failure fixture implemented
- [ ] Conditional attachment example working
- [ ] Report shows all attachments correctly
- [ ] All tests pass

## Submission

Commit with message:
```
feat(week6): Complete Allure attachments exercise
```

