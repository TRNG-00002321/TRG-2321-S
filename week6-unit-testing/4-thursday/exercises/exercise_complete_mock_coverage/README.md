# Collaborative Project: Complete Mock Coverage - DataPipeline

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Advanced |
| **Time Estimate** | 90-120 minutes |
| **Mode** | **Collaborative - Pair Programming** |
| **Prerequisites** | All Thursday content and demos |

## Learning Objectives
By completing this exercise, you will:
- Achieve 100% test coverage using mocking techniques
- Practice pair programming (Driver/Navigator roles)
- Apply all Python mocking techniques learned this week
- Work collaboratively on complex testing scenarios
- Review and improve code as a team

## The Collaboration Protocol

### Pair Programming Roles

**Driver:**
- Writes the code
- Focuses on implementation details
- Thinks out loud about what they're typing

**Navigator:**
- Reviews code as it's written
- Thinks about the bigger picture
- Suggests improvements and catches errors
- Looks up documentation when needed

**Rotation:** Switch roles every 20-25 minutes!

### Session Structure

| Time | Activity | Driver |
|------|----------|--------|
| 0-25 min | Tasks 1-2: Basic mocks | Person A |
| 25-50 min | Tasks 3-4: Advanced mocking | Person B |
| 50-75 min | Task 5: Complete coverage | Person A |
| 75-90 min | Review & Refactor | Person B |

## The System Under Test

```python
# data_pipeline.py
import os
import json
import logging
from datetime import datetime
from typing import List, Dict, Any, Optional

logger = logging.getLogger(__name__)


class DataPipeline:
    """
    A data pipeline that:
    1. Extracts data from external API
    2. Transforms the data
    3. Validates the data
    4. Loads data to database
    5. Sends notifications
    """
    
    def __init__(
        self,
        api_client,
        database,
        validator,
        notifier,
        config: Dict[str, Any] = None
    ):
        self.api_client = api_client
        self.database = database
        self.validator = validator
        self.notifier = notifier
        self.config = config or {}
    
    def run(self, source_id: str) -> Dict[str, Any]:
        """Execute the complete pipeline."""
        logger.info(f"Starting pipeline for source: {source_id}")
        
        try:
            # Extract
            raw_data = self.extract(source_id)
            
            # Transform
            transformed_data = self.transform(raw_data)
            
            # Validate
            validation_result = self.validate(transformed_data)
            if not validation_result["valid"]:
                raise ValidationError(validation_result["errors"])
            
            # Load
            load_result = self.load(transformed_data)
            
            # Notify success
            self.notifier.send_success(
                f"Pipeline completed: {len(transformed_data)} records processed"
            )
            
            return {
                "status": "success",
                "records_processed": len(transformed_data),
                "load_result": load_result
            }
            
        except Exception as e:
            logger.error(f"Pipeline failed: {e}")
            self.notifier.send_failure(str(e))
            raise
    
    def extract(self, source_id: str) -> List[Dict]:
        """Extract data from the API."""
        logger.info(f"Extracting data from source: {source_id}")
        
        data = self.api_client.fetch(source_id)
        
        if not data:
            raise ExtractionError(f"No data found for source: {source_id}")
        
        return data
    
    def transform(self, data: List[Dict]) -> List[Dict]:
        """Transform the raw data."""
        transformed = []
        
        for record in data:
            transformed_record = {
                "id": record.get("id"),
                "name": record.get("name", "").upper(),
                "value": float(record.get("value", 0)),
                "timestamp": datetime.now().isoformat(),
                "source": record.get("source", "unknown")
            }
            
            # Apply custom transformations from config
            if "multiplier" in self.config:
                transformed_record["value"] *= self.config["multiplier"]
            
            transformed.append(transformed_record)
        
        return transformed
    
    def validate(self, data: List[Dict]) -> Dict[str, Any]:
        """Validate the transformed data."""
        errors = []
        
        for i, record in enumerate(data):
            result = self.validator.validate_record(record)
            if not result["valid"]:
                errors.append({"index": i, "errors": result["errors"]})
        
        return {
            "valid": len(errors) == 0,
            "errors": errors
        }
    
    def load(self, data: List[Dict]) -> Dict[str, Any]:
        """Load data to database."""
        if self.config.get("dry_run", False):
            logger.info("Dry run mode - skipping database load")
            return {"loaded": 0, "dry_run": True}
        
        loaded = 0
        for record in data:
            try:
                self.database.insert(record)
                loaded += 1
            except Exception as e:
                logger.warning(f"Failed to insert record: {e}")
                if self.config.get("fail_fast", False):
                    raise
        
        return {"loaded": loaded, "total": len(data)}


class ValidationError(Exception):
    """Raised when validation fails."""
    pass


class ExtractionError(Exception):
    """Raised when extraction fails."""
    pass
```

## Core Tasks (Collaborative)

### Task 1: Set Up Test Class (Driver: Person A)

Create comprehensive test fixtures:

```python
# test_data_pipeline.py
import pytest
from unittest.mock import MagicMock, patch
from data_pipeline import DataPipeline, ValidationError, ExtractionError


@pytest.fixture
def mock_api_client():
    return MagicMock()


@pytest.fixture
def mock_database():
    return MagicMock()


@pytest.fixture
def mock_validator():
    validator = MagicMock()
    validator.validate_record.return_value = {"valid": True, "errors": []}
    return validator


@pytest.fixture
def mock_notifier():
    return MagicMock()


@pytest.fixture
def pipeline(mock_api_client, mock_database, mock_validator, mock_notifier):
    return DataPipeline(
        api_client=mock_api_client,
        database=mock_database,
        validator=mock_validator,
        notifier=mock_notifier
    )


@pytest.fixture
def sample_data():
    return [
        {"id": 1, "name": "item1", "value": 10, "source": "api"},
        {"id": 2, "name": "item2", "value": 20, "source": "api"},
    ]
```

### Task 2: Test Extraction (Driver: Person A, Navigator: Person B)

Write tests for the extract phase:

```python
class TestExtraction:
    
    def test_extract_returns_api_data(self, pipeline, mock_api_client, sample_data):
        mock_api_client.fetch.return_value = sample_data
        
        result = pipeline.extract("source_123")
        
        assert result == sample_data
        mock_api_client.fetch.assert_called_once_with("source_123")
    
    def test_extract_empty_data_raises_error(self, pipeline, mock_api_client):
        mock_api_client.fetch.return_value = []
        
        with pytest.raises(ExtractionError, match="No data found"):
            pipeline.extract("empty_source")
    
    def test_extract_api_failure_propagates(self, pipeline, mock_api_client):
        mock_api_client.fetch.side_effect = ConnectionError("API down")
        
        with pytest.raises(ConnectionError):
            pipeline.extract("any_source")
```

### Task 3: Test Transformation (Driver: Person B, Navigator: Person A)

Write comprehensive transform tests:

```python
class TestTransformation:
    
    def test_transform_uppercase_names(self, pipeline, sample_data):
        result = pipeline.transform(sample_data)
        
        assert result[0]["name"] == "ITEM1"
        assert result[1]["name"] == "ITEM2"
    
    def test_transform_applies_multiplier(self, mock_api_client, mock_database, 
                                          mock_validator, mock_notifier, sample_data):
        pipeline = DataPipeline(
            api_client=mock_api_client,
            database=mock_database,
            validator=mock_validator,
            notifier=mock_notifier,
            config={"multiplier": 2}
        )
        
        result = pipeline.transform(sample_data)
        
        assert result[0]["value"] == 20.0  # 10 * 2
        assert result[1]["value"] == 40.0  # 20 * 2
    
    # Add more transform tests...
```

### Task 4: Test Validation and Loading (Driver: Person B)

Test validation logic and database loading:

```python
class TestValidation:
    
    def test_validate_all_valid_returns_success(self, pipeline, mock_validator):
        mock_validator.validate_record.return_value = {"valid": True, "errors": []}
        
        result = pipeline.validate([{"id": 1}, {"id": 2}])
        
        assert result["valid"] is True
        assert len(result["errors"]) == 0
    
    def test_validate_invalid_records_returns_errors(self, pipeline, mock_validator):
        mock_validator.validate_record.side_effect = [
            {"valid": True, "errors": []},
            {"valid": False, "errors": ["missing name"]}
        ]
        
        result = pipeline.validate([{"id": 1}, {"id": 2}])
        
        assert result["valid"] is False
        assert len(result["errors"]) == 1


class TestLoading:
    
    def test_load_inserts_all_records(self, pipeline, mock_database):
        data = [{"id": 1}, {"id": 2}]
        
        result = pipeline.load(data)
        
        assert result["loaded"] == 2
        assert mock_database.insert.call_count == 2
    
    def test_load_dry_run_skips_database(self, mock_api_client, mock_database,
                                         mock_validator, mock_notifier):
        pipeline = DataPipeline(
            api_client=mock_api_client,
            database=mock_database,
            validator=mock_validator,
            notifier=mock_notifier,
            config={"dry_run": True}
        )
        
        result = pipeline.load([{"id": 1}])
        
        assert result["dry_run"] is True
        mock_database.insert.assert_not_called()
```

### Task 5: Test Complete Pipeline Run (Driver: Person A)

Test the full pipeline with all mocks:

```python
class TestFullPipeline:
    
    def test_run_successful_pipeline(self, pipeline, mock_api_client,
                                     mock_database, mock_notifier, sample_data):
        mock_api_client.fetch.return_value = sample_data
        
        result = pipeline.run("source_123")
        
        assert result["status"] == "success"
        assert result["records_processed"] == 2
        mock_notifier.send_success.assert_called_once()
    
    def test_run_validation_failure_notifies(self, pipeline, mock_api_client,
                                             mock_validator, mock_notifier):
        mock_api_client.fetch.return_value = [{"id": 1}]
        mock_validator.validate_record.return_value = {
            "valid": False, 
            "errors": ["invalid"]
        }
        
        with pytest.raises(ValidationError):
            pipeline.run("source_123")
        
        mock_notifier.send_failure.assert_called_once()
    
    def test_run_extraction_failure_notifies(self, pipeline, mock_api_client,
                                             mock_notifier):
        mock_api_client.fetch.side_effect = ExtractionError("API failed")
        
        with pytest.raises(ExtractionError):
            pipeline.run("source_123")
        
        mock_notifier.send_failure.assert_called_once()
```

## Definition of Done

- [ ] All test fixtures created
- [ ] At least 3 tests for `extract()`
- [ ] At least 4 tests for `transform()`
- [ ] At least 3 tests for `validate()`
- [ ] At least 3 tests for `load()`
- [ ] At least 3 tests for `run()` (full pipeline)
- [ ] Tests cover success and failure scenarios
- [ ] Config options tested (multiplier, dry_run, fail_fast)
- [ ] Both team members contributed code
- [ ] Code review completed together
- [ ] All tests pass

## Collaboration Deliverables

1. **Test file** with both developers' contributions
2. **Brief notes** on:
   - What you learned from your partner
   - One improvement suggestion from the review
   - Challenges faced and how you solved them

## Submission

Create a branch named `week6-collab-{name1}-{name2}`:
```bash
git checkout -b week6-collab-alice-bob
git add .
git commit -m "feat(week6): Complete collaborative DataPipeline exercise - Alice & Bob"
git push origin week6-collab-alice-bob
```

Then create a pull request for peer review.

