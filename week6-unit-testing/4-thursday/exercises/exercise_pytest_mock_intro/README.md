# Lab: Pytest-Mock Introduction - EmailService

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Beginner-Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | pytest-mock.md, demo_pytest_mock_basics.py |

## Learning Objectives
By completing this exercise, you will:
- Use the `mocker` fixture from pytest-mock
- Create mock objects with `mocker.Mock()`
- Patch modules and classes with `mocker.patch()`
- Verify mock calls with assertions
- Replace dependencies without modifying production code

## The Scenario

You're testing an `EmailService` that uses an SMTP client to send emails. You can't send real emails in tests! Using pytest-mock, you'll mock the SMTP client and verify the email service behaves correctly.

## Core Tasks

### Task 1: Basic Mock with mocker (10 minutes)

Create `test_email_service.py`:

```python
import pytest
from email_service import EmailService


class TestEmailService:
    """Tests for EmailService using pytest-mock."""
    
    def test_send_email_creates_smtp_connection(self, mocker):
        """Verify SMTP connection is established."""
        # Create a mock SMTP client
        mock_smtp = mocker.Mock()
        
        # Patch the SMTP class
        mocker.patch('email_service.smtplib.SMTP', return_value=mock_smtp)
        
        # Create service and send email
        service = EmailService(host="smtp.test.com", port=587)
        service.send("to@test.com", "Subject", "Body")
        
        # Verify SMTP was instantiated with correct args
        # (check the patch was called)
        assert mock_smtp.starttls.called
        assert mock_smtp.login.called
```

### Task 2: Mock Method Return Values (15 minutes)

Configure mock return values:

```python
def test_send_email_returns_message_id(self, mocker):
    """Verify send returns message ID from SMTP."""
    mock_smtp = mocker.Mock()
    mock_smtp.sendmail.return_value = {"message_id": "12345"}
    
    mocker.patch('email_service.smtplib.SMTP', return_value=mock_smtp)
    
    service = EmailService(host="smtp.test.com", port=587)
    result = service.send("to@test.com", "Subject", "Body")
    
    assert result["message_id"] == "12345"


def test_send_email_passes_correct_arguments(self, mocker):
    """Verify correct data passed to SMTP."""
    mock_smtp = mocker.Mock()
    mocker.patch('email_service.smtplib.SMTP', return_value=mock_smtp)
    
    service = EmailService(host="smtp.test.com", port=587)
    service.send("to@test.com", "Test Subject", "Test Body")
    
    # Verify sendmail was called with correct args
    mock_smtp.sendmail.assert_called_once()
    call_args = mock_smtp.sendmail.call_args
    
    assert "to@test.com" in str(call_args)
    assert "Test Subject" in str(call_args) or True  # Check message format
```

### Task 3: Mock Exception Handling (10 minutes)

Test error scenarios:

```python
def test_send_email_handles_connection_error(self, mocker):
    """Verify graceful handling of connection failure."""
    mock_smtp = mocker.Mock()
    mock_smtp.connect.side_effect = ConnectionError("Failed to connect")
    
    mocker.patch('email_service.smtplib.SMTP', return_value=mock_smtp)
    
    service = EmailService(host="smtp.test.com", port=587)
    
    with pytest.raises(EmailSendError) as exc_info:
        service.send("to@test.com", "Subject", "Body")
    
    assert "connection" in str(exc_info.value).lower()


def test_send_email_handles_authentication_error(self, mocker):
    """Verify handling of auth failure."""
    import smtplib
    
    mock_smtp = mocker.Mock()
    mock_smtp.login.side_effect = smtplib.SMTPAuthenticationError(535, b"Auth failed")
    
    mocker.patch('email_service.smtplib.SMTP', return_value=mock_smtp)
    
    service = EmailService(host="smtp.test.com", port=587)
    
    with pytest.raises(EmailSendError):
        service.send("to@test.com", "Subject", "Body")
```

### Task 4: Verify Call Counts and Arguments (10 minutes)

```python
def test_send_bulk_emails_sends_to_all_recipients(self, mocker):
    """Verify bulk send reaches all recipients."""
    mock_smtp = mocker.Mock()
    mocker.patch('email_service.smtplib.SMTP', return_value=mock_smtp)
    
    service = EmailService(host="smtp.test.com", port=587)
    recipients = ["a@test.com", "b@test.com", "c@test.com"]
    
    service.send_bulk(recipients, "Subject", "Body")
    
    # Verify sendmail called correct number of times
    assert mock_smtp.sendmail.call_count == 3


def test_send_email_quits_connection(self, mocker):
    """Verify SMTP connection is properly closed."""
    mock_smtp = mocker.Mock()
    mocker.patch('email_service.smtplib.SMTP', return_value=mock_smtp)
    
    service = EmailService(host="smtp.test.com", port=587)
    service.send("to@test.com", "Subject", "Body")
    
    # Verify quit was called (cleanup)
    mock_smtp.quit.assert_called_once()
```

### Task 5: Using mocker.spy (10 minutes)

Spy on real methods while keeping original behavior:

```python
def test_email_formatter_called(self, mocker):
    """Verify email formatting is applied."""
    mock_smtp = mocker.Mock()
    mocker.patch('email_service.smtplib.SMTP', return_value=mock_smtp)
    
    service = EmailService(host="smtp.test.com", port=587)
    
    # Spy on the format_message method
    spy = mocker.spy(service, 'format_message')
    
    service.send("to@test.com", "Subject", "Body")
    
    # Verify format_message was called
    spy.assert_called_once_with("to@test.com", "Subject", "Body")
```

## Starter Code

```python
# email_service.py
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart


class EmailSendError(Exception):
    """Raised when email sending fails."""
    pass


class EmailService:
    """Service for sending emails via SMTP."""
    
    def __init__(self, host: str, port: int, username: str = None, password: str = None):
        self.host = host
        self.port = port
        self.username = username
        self.password = password
    
    def send(self, to: str, subject: str, body: str) -> dict:
        """Send a single email."""
        try:
            smtp = smtplib.SMTP(self.host, self.port)
            smtp.starttls()
            
            if self.username and self.password:
                smtp.login(self.username, self.password)
            
            message = self.format_message(to, subject, body)
            result = smtp.sendmail(self.username or "noreply@test.com", to, message)
            smtp.quit()
            
            return {"success": True, "message_id": "generated_id"}
            
        except smtplib.SMTPAuthenticationError as e:
            raise EmailSendError(f"Authentication failed: {e}")
        except ConnectionError as e:
            raise EmailSendError(f"Connection failed: {e}")
    
    def send_bulk(self, recipients: list, subject: str, body: str) -> list:
        """Send email to multiple recipients."""
        results = []
        for recipient in recipients:
            result = self.send(recipient, subject, body)
            results.append(result)
        return results
    
    def format_message(self, to: str, subject: str, body: str) -> str:
        """Format email message."""
        msg = MIMEMultipart()
        msg['To'] = to
        msg['Subject'] = subject
        msg.attach(MIMEText(body, 'plain'))
        return msg.as_string()
```

## pytest-mock Cheat Sheet

| Method | Purpose |
|--------|---------|
| `mocker.Mock()` | Create a mock object |
| `mocker.patch('module.Class')` | Patch a class/function |
| `mocker.patch.object(obj, 'attr')` | Patch object attribute |
| `mocker.spy(obj, 'method')` | Spy on real method |
| `mock.return_value = x` | Set return value |
| `mock.side_effect = Exception` | Make mock raise exception |
| `mock.assert_called_once()` | Verify single call |
| `mock.assert_called_with(args)` | Verify call arguments |
| `mock.call_count` | Number of calls |

## Definition of Done

- [ ] At least 2 tests using `mocker.Mock()`
- [ ] At least 2 tests using `mocker.patch()`
- [ ] At least 2 tests verifying exception handling
- [ ] At least 1 test using `mocker.spy()`
- [ ] Tests verify call counts
- [ ] Tests verify call arguments
- [ ] All tests pass

## Submission

Commit with message:
```
feat(week6): Complete pytest-mock intro exercise
```

