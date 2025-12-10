# Lab: MagicMock Practice - FileProcessor

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | mock-and-magicmock.md, demo_mock_magicmock.py |

## Learning Objectives
By completing this exercise, you will:
- Understand the difference between `Mock` and `MagicMock`
- Use MagicMock for file operations (context managers)
- Mock magic methods (`__enter__`, `__exit__`, `__len__`, etc.)
- Create auto-specced mocks
- Test code that uses `with` statements

## The Scenario

You're testing a `FileProcessor` class that reads, processes, and writes files. The class uses context managers (`with open(...)`), which require MagicMock's magic method support.

## Core Tasks

### Task 1: Mock File Open with Context Manager (15 minutes)

```python
import pytest
from unittest.mock import MagicMock, mock_open
from file_processor import FileProcessor


class TestFileProcessor:
    
    def test_read_file_returns_content(self, mocker):
        """Test reading file content."""
        # mock_open creates a MagicMock configured for file operations
        mock_file = mock_open(read_data="Hello, World!")
        mocker.patch("builtins.open", mock_file)
        
        processor = FileProcessor()
        content = processor.read_file("test.txt")
        
        assert content == "Hello, World!"
        mock_file.assert_called_once_with("test.txt", "r")
    
    def test_read_file_handles_not_found(self, mocker):
        """Test FileNotFoundError handling."""
        mock_file = mock_open()
        mock_file.side_effect = FileNotFoundError("File not found")
        mocker.patch("builtins.open", mock_file)
        
        processor = FileProcessor()
        
        with pytest.raises(FileNotFoundError):
            processor.read_file("nonexistent.txt")
```

### Task 2: Mock Write Operations (10 minutes)

```python
def test_write_file_writes_content(self, mocker):
    """Test writing to file."""
    mock_file = mock_open()
    mocker.patch("builtins.open", mock_file)
    
    processor = FileProcessor()
    processor.write_file("output.txt", "Test content")
    
    # Verify open was called for writing
    mock_file.assert_called_once_with("output.txt", "w")
    
    # Verify write was called with content
    handle = mock_file()
    handle.write.assert_called_once_with("Test content")


def test_append_file_uses_append_mode(self, mocker):
    """Test appending to file uses 'a' mode."""
    mock_file = mock_open()
    mocker.patch("builtins.open", mock_file)
    
    processor = FileProcessor()
    processor.append_file("log.txt", "New entry")
    
    mock_file.assert_called_once_with("log.txt", "a")
```

### Task 3: MagicMock with Magic Methods (15 minutes)

Create MagicMocks that support iteration and length:

```python
def test_process_csv_iterates_rows(self, mocker):
    """Test CSV processing iterates over rows."""
    # Create mock CSV reader that is iterable
    mock_reader = MagicMock()
    mock_reader.__iter__ = MagicMock(return_value=iter([
        ["name", "age"],
        ["Alice", "30"],
        ["Bob", "25"]
    ]))
    
    mocker.patch("csv.reader", return_value=mock_reader)
    mocker.patch("builtins.open", mock_open())
    
    processor = FileProcessor()
    result = processor.process_csv("data.csv")
    
    assert len(result) == 2  # Excluding header
    assert result[0]["name"] == "Alice"


def test_count_lines_uses_len(self, mocker):
    """Test line counting."""
    mock_file = mock_open(read_data="line1\nline2\nline3\n")
    mocker.patch("builtins.open", mock_file)
    
    processor = FileProcessor()
    count = processor.count_lines("file.txt")
    
    assert count == 3
```

### Task 4: Auto-spec for Type Safety (10 minutes)

```python
def test_autospec_prevents_invalid_calls(self, mocker):
    """Auto-spec ensures mock matches real interface."""
    from file_processor import FileValidator
    
    # Create auto-specced mock
    mock_validator = mocker.create_autospec(FileValidator)
    
    # This works - valid method call
    mock_validator.validate_extension("test.txt")
    
    # This would raise AttributeError if uncommented:
    # mock_validator.nonexistent_method()  # Error!
    
    processor = FileProcessor(validator=mock_validator)
    processor.process_with_validation("data.txt")
    
    mock_validator.validate_extension.assert_called_once()


def test_spec_mock_enforces_signature(self, mocker):
    """Spec mock enforces method signatures."""
    from file_processor import FileHandler
    
    mock_handler = mocker.Mock(spec=FileHandler)
    
    # Valid method
    mock_handler.open("file.txt")
    
    # Would fail: mock_handler.open("file.txt", extra_arg, another)
    # if FileHandler.open only accepts one argument
```

### Task 5: Complex MagicMock Scenarios (10 minutes)

```python
def test_process_directory_handles_nested_structure(self, mocker):
    """Test processing directory with multiple files."""
    # Mock os.walk to return fake directory structure
    mock_walk = [
        ("/root", ["subdir"], ["file1.txt", "file2.txt"]),
        ("/root/subdir", [], ["file3.txt"]),
    ]
    mocker.patch("os.walk", return_value=mock_walk)
    
    # Mock file reads
    file_contents = {
        "/root/file1.txt": "Content 1",
        "/root/file2.txt": "Content 2",
        "/root/subdir/file3.txt": "Content 3",
    }
    
    def read_side_effect(path, mode="r"):
        m = mock_open(read_data=file_contents.get(path, ""))()
        return m
    
    mocker.patch("builtins.open", side_effect=read_side_effect)
    
    processor = FileProcessor()
    results = processor.process_directory("/root")
    
    assert len(results) == 3


def test_context_manager_mock(self, mocker):
    """Test mocking custom context manager."""
    mock_connection = MagicMock()
    mock_connection.__enter__ = MagicMock(return_value=mock_connection)
    mock_connection.__exit__ = MagicMock(return_value=False)
    
    mocker.patch("file_processor.DatabaseConnection", return_value=mock_connection)
    
    processor = FileProcessor()
    processor.save_to_database("data")
    
    mock_connection.__enter__.assert_called_once()
    mock_connection.__exit__.assert_called_once()
```

## Mock vs MagicMock

| Feature | Mock | MagicMock |
|---------|------|-----------|
| Basic attributes | ✓ | ✓ |
| Method calls | ✓ | ✓ |
| Magic methods (`__len__`, etc.) | ✗ | ✓ |
| Context manager (`__enter__/__exit__`) | ✗ | ✓ |
| Iteration (`__iter__`) | ✗ | ✓ |
| Comparison (`__eq__`, `__lt__`) | ✗ | ✓ |

## Definition of Done

- [ ] At least 2 tests using `mock_open()`
- [ ] At least 2 tests with MagicMock magic methods
- [ ] At least 1 test using auto-spec
- [ ] At least 1 test mocking context manager
- [ ] Tests for file read, write, and append operations
- [ ] Tests verify correct file modes used
- [ ] All tests pass

## Submission

Commit with message:
```
feat(week6): Complete MagicMock practice exercise
```

