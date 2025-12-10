# Skipping Tests: Conditional Execution in Pytest

## Learning Objectives
- Use `@pytest.mark.skip` to skip tests
- Apply `@pytest.mark.skipif` for conditional skipping
- Understand `xfail` for expected failures
- Know when to skip vs when to use xfail

## Why This Matters

Not every test should run in every environment. Some tests require specific OS features, Python versions, or external services. Pytest provides flexible mechanisms to skip tests conditionally and to handle known failures gracefully—keeping your test suite clean and informative.

## The Concept

### Basic Skip

```python
import pytest

@pytest.mark.skip(reason="Feature not yet implemented")
def test_upcoming_feature():
    assert new_feature() == "works"
```

### Conditional Skip with skipif

```python
import sys
import pytest

@pytest.mark.skipif(
    sys.version_info < (3, 10),
    reason="Requires Python 3.10+ match statement"
)
def test_pattern_matching():
    # Uses match-case syntax
    pass

@pytest.mark.skipif(
    sys.platform == "win32",
    reason="Unix-only test"
)
def test_unix_permissions():
    pass
```

### Skipping at Runtime

```python
def test_database_connection():
    if not database_available():
        pytest.skip("Database not available")
    
    # Test code here
    result = db.query("SELECT 1")
    assert result == 1
```

### Expected Failures with xfail

Use `xfail` when a test is **expected** to fail (known bug, not yet implemented):

```python
@pytest.mark.xfail(reason="Bug #1234 - division by zero not handled")
def test_divide_by_zero_handling():
    result = calculator.divide(10, 0)
    assert result == float('inf')
```

**xfail outcomes:**
- Test fails → `XFAIL` (expected)
- Test passes → `XPASS` (unexpected - bug might be fixed!)

### Strict xfail

Fail the test suite if an xfail unexpectedly passes:

```python
@pytest.mark.xfail(strict=True, reason="Known bug")
def test_known_bug():
    # If this passes, we need to remove xfail marker
    pass
```

### Skip vs Xfail

| Situation | Use |
|-----------|-----|
| Test can't run (missing dependencies) | `skip` |
| Test not applicable (wrong OS) | `skipif` |
| Known bug, test should fail | `xfail` |
| Feature not implemented yet | `skip` or `xfail` |
| Flaky test under investigation | `xfail` (temporary) |

### Platform and Version Conditions

```python
import pytest

# Skip on specific platform
@pytest.mark.skipif(
    sys.platform != "linux",
    reason="Linux-specific functionality"
)
def test_linux_feature():
    pass

# Skip below Python version
@pytest.mark.skipif(
    sys.version_info < (3, 9),
    reason="Requires Python 3.9+ features"
)
def test_modern_python():
    pass

# Skip if module not installed
pytest.importorskip("pandas")

def test_with_pandas():
    import pandas as pd
    # pandas is guaranteed to be available
```

## Code Example

### Comprehensive Skip/Xfail Usage

```python
import sys
import os
import pytest

class TestPlatformSpecific:
    
    @pytest.mark.skipif(
        sys.platform == "win32",
        reason="Uses Unix signals"
    )
    def test_signal_handling(self):
        import signal
        # Unix-specific signal handling
        pass
    
    @pytest.mark.skipif(
        "CI" not in os.environ,
        reason="Only runs in CI environment"
    )
    def test_ci_only(self):
        pass
    
    @pytest.mark.xfail(
        sys.platform == "darwin",
        reason="Known issue on macOS - investigating"
    )
    def test_file_locking(self):
        pass

class TestFeatureFlags:
    
    @pytest.mark.skip(reason="Feature disabled in production")
    def test_experimental_feature(self):
        pass
    
    @pytest.mark.xfail(reason="Ticket #5678 - awaiting fix")
    def test_known_regression(self):
        pass

def test_requires_external_service():
    """Skip if external service not available."""
    if not os.getenv("EXTERNAL_SERVICE_URL"):
        pytest.skip("External service URL not configured")
    
    # Test implementation
    pass
```

## Summary

- **`@pytest.mark.skip`**: Always skip with reason
- **`@pytest.mark.skipif`**: Conditional skip based on expression
- **`pytest.skip()`**: Runtime skip within test
- **`@pytest.mark.xfail`**: Expected to fail (known issue)
- **`pytest.importorskip()`**: Skip if module unavailable
- **Always document** why tests are skipped
- Use **xfail for bugs**, **skip for environment issues**

## Additional Resources

- [Pytest Skip and Xfail](https://docs.pytest.org/en/stable/how-to/skipping.html) - Official guide
- [Conditional Test Execution](https://pytest.org/en/stable/example/markers.html) - Marker examples
- [Managing Flaky Tests](https://pytest-rerunfailures.readthedocs.io/) - Rerun plugin

