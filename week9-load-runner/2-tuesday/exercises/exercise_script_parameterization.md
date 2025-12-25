# Lab: Script Parameterization and Correlation

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 60-75 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | exercise_first_vugen_script.md, vugen-overview.md, demo_script_enhancement.c |

## Learning Objectives
By completing this exercise, you will:
- Understand why parameterization is essential for realistic load testing
- Create parameter files (CSV) with test data
- Replace hardcoded values with parameters
- Identify values that require correlation
- Implement correlation using `web_reg_save_param`
- Configure parameter update methods (Sequential, Random, Unique)

## The Scenario

Your VuGen script from the previous exercise works for a single user, but in production, thousands of different users will search for different products. Running a load test where all 1,000 virtual users search for "laptop" is unrealistic and won't stress the database properly. Your task is to parameterize the script so each virtual user behaves differently.

## Core Tasks

### Task 1: Identify Values to Parameterize (10 minutes)

Review your script and identify values that should vary between virtual users:

**Common Values to Parameterize:**

| Value Type | Current Value | Why Parameterize? |
|------------|---------------|-------------------|
| Search terms | "laptop" | Different users search different things |
| Product IDs | "prod_12345" | Users view different products |
| Usernames | "testuser1" | Each user needs unique credentials |
| Passwords | "password123" | Goes with username |
| Quantities | "1" | Users buy different quantities |

**In your script, identify and document:**

1. Search term location (line number): ________________
2. Product ID location (line number): ________________
3. Any hardcoded user data locations: ________________

### Task 2: Create Parameter Data Files (15 minutes)

Create CSV files to hold your test data:

#### Create Search Terms File
1. In VuGen, go to **View → Parameter List** (or press Ctrl+L)
2. Click **New Parameter**
3. Name: `SearchTerm`
4. Type: **File**
5. Click **Create Table** and add these values:

**search_terms.csv:**
```csv
SearchTerm
laptop
desktop
monitor
keyboard
mouse
tablet
phone
camera
headphones
printer
```

#### Create Product Data File
Create another parameter file:

**products.csv:**
```csv
ProductID,ProductName,Category
43,MacBook,Laptops
40,iPhone,Phones
42,Apple Cinema 30,Monitors
30,Canon EOS 5D,Cameras
28,HTC Touch HD,Phones
41,iMac,Desktops
```

### Task 3: Apply Parameters to Your Script (15 minutes)

Replace hardcoded values with parameters:

#### Before (Hardcoded):
```c
web_submit_data("Search",
    "Action=https://demo.opencart.com/index.php?route=product/search",
    ITEMDATA,
    "Name=search", "Value=laptop", ENDITEM,  // Hardcoded!
    LAST);
```

#### After (Parameterized):
```c
web_submit_data("Search",
    "Action=https://demo.opencart.com/index.php?route=product/search",
    ITEMDATA,
    "Name=search", "Value={SearchTerm}", ENDITEM,  // Parameterized!
    LAST);
```

**Steps:**
1. Find the hardcoded value in your script
2. Right-click on the value
3. Select **Replace with Parameter**
4. Choose your parameter or create a new one
5. Repeat for all values identified in Task 1

### Task 4: Configure Parameter Settings (10 minutes)

Configure how parameters behave during load testing:

1. Open **Parameter List** (Ctrl+L)
2. For each parameter, configure:

**SearchTerm Parameter:**
| Setting | Value | Why |
|---------|-------|-----|
| Update Value On | Each Iteration | New search each loop |
| When Out of Values | Wrap around | Cycle through all terms |
| Select Next Row | Random | Realistic distribution |

**ProductID Parameter:**
| Setting | Value | Why |
|---------|-------|-----|
| Update Value On | Each Occurrence | Different product each time |
| When Out of Values | Wrap around | Reuse data |
| Select Next Row | Sequential | Predictable for debugging |

### Task 5: Identify and Implement Correlation (20 minutes)

Some values change with each session and must be captured dynamically.

#### Find Values Needing Correlation

1. Run your script once and let it **fail** (or examine the replay log)
2. Look for errors like:
   - "Invalid session"
   - "Token mismatch"
   - "CSRF validation failed"

3. Use VuGen's **Correlation Studio**:
   - Go to **Design → Design Studio**
   - Click **Correlate**
   - Review suggestions

#### Common Values Requiring Correlation

| Value Type | Pattern | Example |
|------------|---------|---------|
| Session ID | `PHPSESSID=xxxxx` | Changes every session |
| CSRF Token | `csrf_token=xxxxx` | Security token |
| Cart ID | `cart_id=12345` | Generated on add to cart |
| Product Token | `product_token=abc` | Dynamic product reference |

#### Implement Correlation

For each dynamic value, add correlation BEFORE the request that receives it:

```c
// Capture the session ID from the server response
web_reg_save_param_ex(
    "ParamName=SessionID",
    "LB=PHPSESSID=",           // Left boundary
    "RB=;",                     // Right boundary
    SEARCH_FILTERS,
    "Scope=ALL",
    LAST);

// The request that returns the session ID
web_url("Homepage",
    "URL=https://demo.opencart.com/",
    LAST);

// Now use the captured value in subsequent requests
web_add_cookie("PHPSESSID={SessionID}; Domain=demo.opencart.com");
```

**Document your correlations:**

| Correlated Value | Left Boundary | Right Boundary |
|------------------|---------------|----------------|
| ________________ | ________________ | ________________ |
| ________________ | ________________ | ________________ |
| ________________ | ________________ | ________________ |

### Task 6: Validate Your Enhanced Script (10 minutes)

1. **Save** your parameterized script
2. **Run** the script multiple times (at least 3 iterations)
3. Verify in the log that:
   - [ ] Different search terms are used each iteration
   - [ ] Different products are accessed
   - [ ] No session/token errors occur
   - [ ] All transactions pass

**Check the Replay Log:**
```
Iteration 1: SearchTerm = "laptop", ProductID = "43"
Iteration 2: SearchTerm = "desktop", ProductID = "40"
Iteration 3: SearchTerm = "monitor", ProductID = "42"
```

## Definition of Done

Your parameterized script is complete when:
- [ ] At least one parameter file (CSV) created with 5+ values
- [ ] Search term is parameterized
- [ ] Parameter update method configured appropriately
- [ ] At least one correlation implemented (if application uses dynamic values)
- [ ] Script runs successfully for 3+ iterations
- [ ] Each iteration uses different parameter values (verify in log)

## Parameterization Quality Checklist

```
Script: [Your Script Name]
==========================

Parameters Created:
[  ] SearchTerm - File parameter with 10+ values
[  ] ProductID - File parameter with 5+ values
[  ] Additional: ________________

Parameter Configuration:
[  ] Update method set (Sequential/Random/Unique)
[  ] Out-of-values behavior configured
[  ] Column selection correct for multi-column files

Correlations Implemented:
[  ] Correlation 1: ________________
[  ] Correlation 2: ________________

Validation:
[  ] Script compiles without errors
[  ] 3+ iterations run successfully
[  ] Parameter values change each iteration (verified in log)
[  ] No hardcoded user-specific data remains

Status: READY FOR LOAD TESTING / NEEDS REVISION
```

## Troubleshooting Guide

| Issue | Possible Cause | Solution |
|-------|---------------|----------|
| Parameter not substituted | Wrong syntax | Use `{ParamName}` not `$ParamName` |
| Same value every iteration | Update method wrong | Change to "Each Iteration" |
| File not found | Path issue | Use relative path, check file location |
| Correlation captures wrong value | Boundaries too broad | Make LB/RB more specific |
| Empty correlation value | Value not in response | Check scope, verify response contains value |

## Stretch Goals (Optional)

If you finish early:
1. Create a **User Credentials** parameter file with username/password pairs
2. Add parameterization for quantities or other numeric values
3. Implement **nested parameterization** (use one parameter to select a row)
4. Create a parameter that uses **Unique** mode for user IDs
5. Add validation checkpoints using `web_reg_find` to verify expected content

## Understanding Parameter Types

```
Parameter Update Methods:
─────────────────────────

SEQUENTIAL:
  VUser1-Iter1: laptop    VUser2-Iter1: laptop
  VUser1-Iter2: desktop   VUser2-Iter2: desktop
  → Good for: Predictable, reproducible tests

RANDOM:
  VUser1-Iter1: tablet    VUser2-Iter1: laptop
  VUser1-Iter2: phone     VUser2-Iter2: camera
  → Good for: Realistic user behavior simulation

UNIQUE:
  VUser1-Iter1: laptop    VUser2-Iter1: desktop
  VUser1-Iter2: monitor   VUser2-Iter2: keyboard
  → Good for: User IDs, order numbers (no duplicates)
```

## Common Mistakes to Avoid

1. **Using wrong parameter syntax** - LoadRunner uses `{ParamName}`, not `$param` or `%param%`
2. **Correlation in wrong location** - Must be BEFORE the request that returns the value
3. **Too generic boundaries** - "=" as left boundary will match many values
4. **Forgetting to save** - Parameter files must be saved before replay
5. **Column name mismatch** - CSV column header must match parameter name exactly

## Submission

1. Verify script runs for 3+ iterations with different values
2. Export your parameter files
3. Document your correlations

Commit message format:
```
feat(week9): Complete script parameterization exercise
```

