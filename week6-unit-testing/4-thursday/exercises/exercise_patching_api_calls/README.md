# Lab: Patching API Calls - WeatherService

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | patching.md, demo_patching_techniques.py |

## Learning Objectives
By completing this exercise, you will:
- Use `@patch` decorator for function patching
- Use `patch()` context manager for targeted patching
- Understand "where to patch" rule
- Use `patch.object()` for method patching
- Use `patch.dict()` for environment variables
- Test code that makes external API calls

## The Scenario

You're testing a `WeatherService` that fetches weather data from an external API. You need to patch the HTTP client to avoid making real network requests and to test various API response scenarios.

## Core Tasks

### Task 1: Patch with Decorator (15 minutes)

```python
import pytest
from unittest.mock import patch, MagicMock
from weather_service import WeatherService


class TestWeatherServiceWithDecorator:
    
    @patch('weather_service.requests.get')
    def test_get_temperature_returns_value(self, mock_get):
        """Test successful temperature fetch."""
        # Configure mock response
        mock_response = MagicMock()
        mock_response.status_code = 200
        mock_response.json.return_value = {
            "main": {"temp": 72.5},
            "name": "New York"
        }
        mock_get.return_value = mock_response
        
        service = WeatherService(api_key="fake_key")
        temp = service.get_temperature("New York")
        
        assert temp == 72.5
        mock_get.assert_called_once()
    
    @patch('weather_service.requests.get')
    def test_get_temperature_handles_api_error(self, mock_get):
        """Test handling of API error response."""
        mock_response = MagicMock()
        mock_response.status_code = 401
        mock_response.json.return_value = {"error": "Invalid API key"}
        mock_get.return_value = mock_response
        
        service = WeatherService(api_key="invalid_key")
        
        with pytest.raises(WeatherAPIError, match="API key"):
            service.get_temperature("New York")
```

### Task 2: Patch with Context Manager (10 minutes)

```python
def test_get_forecast_with_context_manager(self):
    """Use context manager for more control."""
    with patch('weather_service.requests.get') as mock_get:
        mock_response = MagicMock()
        mock_response.status_code = 200
        mock_response.json.return_value = {
            "list": [
                {"dt_txt": "2024-01-01 12:00:00", "main": {"temp": 65}},
                {"dt_txt": "2024-01-02 12:00:00", "main": {"temp": 68}},
            ]
        }
        mock_get.return_value = mock_response
        
        service = WeatherService(api_key="fake_key")
        forecast = service.get_forecast("Chicago", days=2)
        
        assert len(forecast) == 2
        assert forecast[0]["temp"] == 65


def test_partial_patching_with_context_manager(self):
    """Patch only specific part of test."""
    service = WeatherService(api_key="fake_key")
    
    # First call - not patched (would fail in real scenario)
    # service.get_temperature("NYC")  # Real call!
    
    # Patched section
    with patch('weather_service.requests.get') as mock_get:
        mock_get.return_value = MagicMock(
            status_code=200,
            json=lambda: {"main": {"temp": 70}}
        )
        temp = service.get_temperature("NYC")
        assert temp == 70
    
    # After context - no longer patched
```

### Task 3: "Where to Patch" Rule (15 minutes)

```python
# weather_service.py imports requests like this:
# from requests import get

# You must patch WHERE IT'S USED, not where it's defined!

@patch('weather_service.get')  # CORRECT - patch in weather_service
def test_correct_patch_location(self, mock_get):
    """Patch where the function is looked up."""
    mock_get.return_value = MagicMock(
        status_code=200,
        json=lambda: {"main": {"temp": 75}}
    )
    
    service = WeatherService(api_key="key")
    temp = service.get_temperature("City")
    assert temp == 75


# DON'T DO THIS:
# @patch('requests.get')  # WRONG - patches in requests module, not where used!


def test_patch_multiple_imports(self):
    """When module has multiple imports."""
    with patch('weather_service.requests.get') as mock_get, \
         patch('weather_service.requests.post') as mock_post:
        
        mock_get.return_value = MagicMock(status_code=200, json=lambda: {"temp": 70})
        mock_post.return_value = MagicMock(status_code=201)
        
        service = WeatherService(api_key="key")
        # Test code that uses both get and post
```

### Task 4: patch.object for Methods (10 minutes)

```python
def test_patch_object_method(self):
    """Patch a specific method on an object."""
    service = WeatherService(api_key="key")
    
    with patch.object(service, '_make_request') as mock_request:
        mock_request.return_value = {"main": {"temp": 80}}
        
        temp = service.get_temperature("Miami")
        
        assert temp == 80
        mock_request.assert_called_once()


def test_patch_class_method(self):
    """Patch a method on the class itself."""
    with patch.object(WeatherService, 'validate_city', return_value=True):
        service = WeatherService(api_key="key")
        
        # validate_city will return True regardless of input
        assert service.validate_city("InvalidCity123") is True
```

### Task 5: patch.dict for Environment Variables (10 minutes)

```python
import os
from unittest.mock import patch


def test_service_uses_env_api_key(self):
    """Test service reads API key from environment."""
    with patch.dict(os.environ, {'WEATHER_API_KEY': 'env_key'}):
        service = WeatherService()  # Should read from env
        assert service.api_key == 'env_key'


def test_service_with_custom_env(self):
    """Test with complete custom environment."""
    custom_env = {
        'WEATHER_API_KEY': 'test_key',
        'WEATHER_BASE_URL': 'http://test.api.com',
        'WEATHER_TIMEOUT': '30'
    }
    
    with patch.dict(os.environ, custom_env, clear=True):
        service = WeatherService()
        assert service.api_key == 'test_key'
        assert service.base_url == 'http://test.api.com'


def test_service_missing_env_raises_error(self):
    """Test error when required env var missing."""
    with patch.dict(os.environ, {}, clear=True):
        with pytest.raises(EnvironmentError, match="API key"):
            WeatherService()
```

## Starter Code

```python
# weather_service.py
import os
import requests


class WeatherAPIError(Exception):
    """Raised when weather API returns an error."""
    pass


class WeatherService:
    """Service for fetching weather data from external API."""
    
    def __init__(self, api_key: str = None):
        self.api_key = api_key or os.environ.get('WEATHER_API_KEY')
        if not self.api_key:
            raise EnvironmentError("WEATHER_API_KEY not set")
        
        self.base_url = os.environ.get(
            'WEATHER_BASE_URL', 
            'https://api.openweathermap.org/data/2.5'
        )
    
    def get_temperature(self, city: str) -> float:
        """Get current temperature for a city."""
        response = requests.get(
            f"{self.base_url}/weather",
            params={"q": city, "appid": self.api_key, "units": "imperial"}
        )
        
        if response.status_code == 401:
            raise WeatherAPIError("Invalid API key")
        if response.status_code != 200:
            raise WeatherAPIError(f"API error: {response.status_code}")
        
        data = response.json()
        return data["main"]["temp"]
    
    def get_forecast(self, city: str, days: int = 5) -> list:
        """Get weather forecast for a city."""
        response = requests.get(
            f"{self.base_url}/forecast",
            params={"q": city, "appid": self.api_key, "cnt": days * 8}
        )
        
        if response.status_code != 200:
            raise WeatherAPIError(f"API error: {response.status_code}")
        
        data = response.json()
        return [{"date": item["dt_txt"], "temp": item["main"]["temp"]} 
                for item in data["list"]]
```

## Definition of Done

- [ ] At least 2 tests using `@patch` decorator
- [ ] At least 2 tests using `patch()` context manager
- [ ] At least 1 test demonstrating "where to patch" rule
- [ ] At least 1 test using `patch.object()`
- [ ] At least 2 tests using `patch.dict()`
- [ ] Tests cover success and error scenarios
- [ ] All tests pass

## Submission

Commit with message:
```
feat(week6): Complete patching API calls exercise
```

