# Lab: Recording Your First VuGen Script

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Beginner |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Code Lab |
| **Prerequisites** | LoadRunner installed, vugen-overview.md, demo_vugen_recording.c |

## Learning Objectives
By completing this exercise, you will:
- Create a new VuGen script using the Web HTTP/HTML protocol
- Record a complete user journey on a sample web application
- Understand VuGen's three-section script structure (vuser_init, Action, vuser_end)
- Successfully replay a recorded script
- Add basic transaction markers to measure performance

## The Scenario

Your team needs to performance test an online bookstore application. Before you can run load tests with hundreds of users, you need to create a script that simulates a single user's journey through the application. Your task is to record this journey using VuGen.

## Test Application

For this exercise, use one of these publicly available demo applications:

**Option 1: OpenCart Demo Store** (Recommended)
- URL: https://demo.opencart.com/
- A fully functional e-commerce demo site

**Option 2: Automation Practice Site**
- URL: http://automationpractice.com/
- Simple e-commerce demo

**Option 3: Your Organization's Test Environment**
- Use your organization's test/staging web application if available

## Core Tasks

### Task 1: Create a New VuGen Script (10 minutes)

1. Launch **Virtual User Generator (VuGen)**
2. Click **File → New Script and Solution**
3. In the protocol selection dialog:
   - Category: **Web**
   - Protocol: **Web HTTP/HTML**
   - Click **Create**
4. Save the script:
   - Script name: `Bookstore_Browse`
   - Location: Create a folder for your scripts (e.g., `C:\LoadRunner\Scripts\`)

**Verify:**
- [ ] New script created with three sections: vuser_init, Action, vuser_end
- [ ] Script saved to your chosen location

### Task 2: Configure Recording Options (5 minutes)

Before recording, configure optimal settings:

1. Go to **Record → Recording Options**
2. In **General** tab:
   - Recording Mode: **URL-based script**
3. In **HTTP Properties** tab:
   - [ ] Check "Record think time"
4. Click **OK**

### Task 3: Record the User Journey (20 minutes)

You will record a typical shopping journey:

#### Start Recording
1. Click **Record → Record** (or press F7)
2. In the Record dialog:
   - URL: Enter your test application URL
   - Record into Action: **Action**
   - Browser: **Chrome** or **Firefox** (your preference)
3. Click **Start Recording**

#### Perform These Actions (Record Each Step)

**Step 1: Homepage**
- Wait for the homepage to fully load
- Observe the recording toolbar (VuGen is capturing)

**Step 2: Browse a Category**
- Click on a product category (e.g., "Laptops" or "Desktops")
- Wait for the category page to load

**Step 3: View a Product**
- Click on any product to view its details
- Wait for the product page to load

**Step 4: Add to Cart**
- Click "Add to Cart" button
- Observe any confirmation message

**Step 5: View Cart**
- Navigate to the shopping cart
- Verify the item appears

**Step 6: Stop Recording**
- Click the **Stop Recording** button in the VuGen toolbar
- Wait for VuGen to generate the script

### Task 4: Review the Generated Script (10 minutes)

After recording, examine your script:

1. **vuser_init section** - Should be empty or minimal (we'll add login here later)

2. **Action section** - Should contain your recorded steps:
   ```c
   web_url("Homepage",
       "URL=https://demo.opencart.com/",
       ...);
   
   web_link("Category",
       "Text=Laptops & Notebooks",
       ...);
   
   // Additional recorded actions...
   ```

3. **vuser_end section** - Should be empty or minimal

**Document:**
- Number of `web_url` calls: ________________
- Number of `web_link` calls: ________________
- Number of `web_submit_data` calls: ________________
- Think times recorded? Yes / No

### Task 5: Add Transaction Markers (10 minutes)

Transaction markers measure specific operations. Add them to your Action section:

1. Locate the code that navigates to the homepage
2. **Before** the homepage navigation, add:
   ```c
   lr_start_transaction("T01_Navigate_Home");
   ```
3. **After** the homepage loads (after the web_url call), add:
   ```c
   lr_end_transaction("T01_Navigate_Home", LR_AUTO);
   ```

4. Repeat for other key operations:
   - `T02_Browse_Category`
   - `T03_View_Product`
   - `T04_Add_To_Cart`
   - `T05_View_Cart`

**Example:**
```c
lr_start_transaction("T02_Browse_Category");

web_link("Laptops_Category",
    "Text=Laptops & Notebooks",
    EXTRARES,
    ...
    LAST);

lr_end_transaction("T02_Browse_Category", LR_AUTO);
```

### Task 6: Replay the Script (5 minutes)

Test your script with a single virtual user:

1. Click **Replay → Run** (or press F5)
2. Watch the **Replay Log** panel at the bottom
3. Monitor for:
   - [ ] Each transaction starts and ends
   - [ ] No error messages (red text)
   - [ ] Script completes successfully

**Document:**
- Replay status: PASS / FAIL
- Any errors encountered: ________________
- Total replay time: ________________

## Definition of Done

Your script is complete when:
- [ ] VuGen script created with Web HTTP/HTML protocol
- [ ] User journey recorded (Homepage → Browse → Product → Cart)
- [ ] Script has proper three-section structure
- [ ] At least 5 transaction markers added
- [ ] Script replays successfully without errors
- [ ] Script is saved to your scripts folder

## Script Quality Checklist

Review your script against these quality criteria:

```
Script: Bookstore_Browse
========================

Structure:
[  ] vuser_init section present
[  ] Action section contains recorded steps
[  ] vuser_end section present

Transactions:
[  ] T01_Navigate_Home
[  ] T02_Browse_Category
[  ] T03_View_Product
[  ] T04_Add_To_Cart
[  ] T05_View_Cart

Replay:
[  ] No compilation errors
[  ] No runtime errors
[  ] All transactions pass
[  ] Think times present between actions

Quality: READY FOR PARAMETERIZATION / NEEDS WORK
```

## Troubleshooting Guide

| Issue | Possible Cause | Solution |
|-------|---------------|----------|
| Recording doesn't start | Browser not configured | Use recommended browser, check proxy settings |
| Empty script | Proxy not capturing | Check firewall, use different browser |
| Replay fails 403/404 | Session expired or URL changed | Re-record, check for dynamic values |
| Transaction errors | Syntax error | Check matching start/end calls |
| SSL errors | Certificate issue | Configure SSL settings in Recording Options |

## Stretch Goals (Optional)

If you finish early:
1. Add a comment block at the top of your script explaining its purpose
2. Add `lr_think_time()` calls between transactions if not already present
3. Record a second script that includes login functionality
4. Explore the **Snapshot Viewer** to see captured responses

## Understanding Your Script

After completing this exercise, you should understand:

```
Script Flow:
────────────
┌─────────────────┐
│   vuser_init    │ ← Runs ONCE at start (login, setup)
└────────┬────────┘
         ▼
┌─────────────────┐
│     Action      │ ← Runs for EACH iteration (main workflow)
│                 │   This is where your transactions go
└────────┬────────┘
         ▼
┌─────────────────┐
│   vuser_end     │ ← Runs ONCE at end (logout, cleanup)
└─────────────────┘
```

## Common Mistakes to Avoid

1. **Not waiting for pages to load** - Record too fast and you'll miss requests
2. **Forgetting to stop recording** - Script will include unintended actions
3. **Mismatched transaction markers** - Every `lr_start_transaction` needs a matching `lr_end_transaction`
4. **Not saving before replay** - You might lose changes if something crashes
5. **Ignoring think times** - They make scripts more realistic

## Submission

1. Verify your script replays successfully
2. Take a screenshot of the successful replay log
3. Save your script folder (it contains multiple files)

Commit message format:
```
feat(week9): Complete first VuGen script recording exercise
```

