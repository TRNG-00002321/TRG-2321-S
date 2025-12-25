# Lab: Creating a Load Test Scenario in Controller

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Intermediate |
| **Time Estimate** | 45-60 minutes |
| **Mode** | Individual Lab |
| **Prerequisites** | Parameterized VuGen script, scenario-design-with-controller.md, demo_controller_scenario.md |

## Learning Objectives
By completing this exercise, you will:
- Create a new scenario in LoadRunner Controller
- Add multiple VuGen scripts to a scenario
- Configure virtual user groups with appropriate VUser counts
- Design a ramp-up schedule for realistic load simulation
- Connect and verify Load Generator status
- Save and manage scenario configurations

## The Scenario

Your e-commerce application is preparing for a holiday sale. Management expects up to 50 concurrent users (the maximum for Community Edition). You need to design a load test scenario that simulates realistic user behavior patterns, including:
- Users who only browse products (60%)
- Users who search for specific items (25%)
- Users who add items to cart (15%)

## Core Tasks

### Task 1: Launch Controller and Create New Scenario (10 minutes)

1. Launch **LoadRunner Controller**
   ```
   Start Menu → LoadRunner → Controller
   ```

2. Create a new scenario:
   - Select **File → New** (or Ctrl+N)
   - Choose **Manual Scenario** (not Goal-Oriented)
   - Click **OK**

3. Save the scenario immediately:
   - File name: `HolidaySale_LoadTest`
   - Location: Your scripts folder

**Verify:**
- [ ] Controller is open
- [ ] New scenario created (Manual type)
- [ ] Scenario saved

### Task 2: Add Scripts to Your Scenario (10 minutes)

Add your VuGen scripts to the scenario:

1. In the **Design** tab, click **Add Group**
2. Browse to your script folder
3. Select your parameterized script (e.g., `Bookstore_Browse.usr`)
4. Click **Add** then **OK**

If you only have one script, create the scenario with a single group. If you have multiple scripts from practice, add them:

| Group Name | Script | Description |
|------------|--------|-------------|
| Browse_Users | Bookstore_Browse.usr | Users browsing products |
| Search_Users | (same or different script) | Users searching |
| Checkout_Users | (same or different script) | Users purchasing |

**For this exercise with one script:**

Create **three groups** using the same script but with different names to simulate different user types:

```
Scenario Groups:
┌──────────────────────────────────────────────────────────────┐
│ Group Name        │ Script             │ Planned VUsers      │
├───────────────────┼────────────────────┼─────────────────────┤
│ Browse_Users      │ Bookstore_Browse   │ 30 (60%)            │
│ Search_Users      │ Bookstore_Browse   │ 12 (25%)            │
│ Checkout_Users    │ Bookstore_Browse   │ 8 (15%)             │
├───────────────────┼────────────────────┼─────────────────────┤
│ TOTAL             │                    │ 50 VUsers           │
└──────────────────────────────────────────────────────────────┘
```

### Task 3: Configure Virtual User Groups (15 minutes)

For each group, configure the VUser settings:

#### Browse_Users Group (30 VUsers)
1. Select the **Browse_Users** group
2. Set **Quantity**: 30
3. Verify **Load Generator**: localhost

#### Search_Users Group (12 VUsers)
1. Select the **Search_Users** group
2. Set **Quantity**: 12
3. Verify **Load Generator**: localhost

#### Checkout_Users Group (8 VUsers)
1. Select the **Checkout_Users** group
2. Set **Quantity**: 8
3. Verify **Load Generator**: localhost

**Configuration View:**
```
Controller Design View:
┌─────────────────────────────────────────────────────────────────┐
│                     Scenario Groups                              │
├─────────────────────────────────────────────────────────────────┤
│ ✓ Browse_Users    │ Bookstore_Browse │ 30 │ localhost │ Ready  │
│ ✓ Search_Users    │ Bookstore_Browse │ 12 │ localhost │ Ready  │
│ ✓ Checkout_Users  │ Bookstore_Browse │ 8  │ localhost │ Ready  │
├─────────────────────────────────────────────────────────────────┤
│                              Total: 50 VUsers                    │
└─────────────────────────────────────────────────────────────────┘
```

### Task 4: Design the Ramp-Up Schedule (15 minutes)

A good load test doesn't start all users simultaneously. Design a gradual ramp-up:

1. Click **Scenario → Scenario Schedule** (or use the Schedule tab)
2. For **Global Schedule**, configure:

#### Ramp-Up Configuration

```
Schedule Timeline:
──────────────────────────────────────────────────────────────────
                           
VUsers
  ▲
50 │                    ┌─────────────────────────────┐
   │                   /│                             │\
40 │                  / │                             │ \
   │                 /  │                             │  \
30 │                /   │                             │   \
   │               /    │       STEADY STATE         │    \
20 │              /     │       (10 minutes)         │     \
   │             /      │                             │      \
10 │            /       │                             │       \
   │           /        │                             │        \
 0 └──────────/─────────┴─────────────────────────────┴─────────▶
   │  Init   │ Ramp-up │                             │ Ramp-   │
   │ (2 min) │ (5 min) │                             │ down    │
   │         │         │                             │ (3 min) │
```

**Schedule Settings:**
| Phase | Setting | Value |
|-------|---------|-------|
| Initialize | All VUsers | Before ramp-up |
| Ramp-Up | Duration | 5 minutes |
| Ramp-Up | Start | 10 VUsers every 1 minute |
| Duration | Run for | 10 minutes |
| Ramp-Down | Stop | 20 VUsers every 1 minute |

#### Configure Each Phase:

1. **Initialization:**
   - Action: **Initialize all Vusers**
   - When: Before running scenario

2. **Start VUsers (Ramp-Up):**
   - Action: **Start Vusers**
   - Select: **Gradually**
   - Start: **10** VUsers every **60** seconds

3. **Duration:**
   - Action: **Duration**
   - Run for: **10** minutes

4. **Stop VUsers (Ramp-Down):**
   - Action: **Stop Vusers**
   - Stop: **20** VUsers every **60** seconds

### Task 5: Verify Load Generator Connection (5 minutes)

Ensure the Load Generator is ready:

1. Go to **Scenario → Load Generators**
2. Select **localhost**
3. Click **Connect**
4. Verify status shows: **● Connected**

**If not connected:**
- Check the LoadRunner Agent service is running
- Verify firewall allows port 54345
- Try restarting the Agent service

### Task 6: Save and Review Your Scenario (5 minutes)

1. **Save** the scenario (Ctrl+S)
2. Review the complete configuration:

**Scenario Summary:**
```
Scenario: HolidaySale_LoadTest
==============================

Type: Manual Scenario

Groups:
┌─────────────────────────────────────────────────────┐
│ Browse_Users    : 30 VUsers (60%) - localhost       │
│ Search_Users    : 12 VUsers (25%) - localhost       │
│ Checkout_Users  : 8 VUsers (15%)  - localhost       │
└─────────────────────────────────────────────────────┘

Schedule:
- Initialize: All VUsers before start
- Ramp-up: 5 minutes (10 VUsers/minute)
- Steady State: 10 minutes at 50 VUsers
- Ramp-down: 3 minutes (20 VUsers/minute)

Total Test Duration: ~18 minutes
Maximum Concurrent VUsers: 50

Load Generator: localhost - Connected ●
```

## Definition of Done

Your scenario is complete when:
- [ ] Manual scenario created and saved
- [ ] At least 2 virtual user groups defined (3 recommended)
- [ ] Total VUsers = 50 (Community Edition maximum)
- [ ] Ramp-up schedule configured (not all users starting at once)
- [ ] Duration set to at least 10 minutes
- [ ] Ramp-down configured
- [ ] Load Generator connected and showing Ready status
- [ ] Scenario saved to file

## Scenario Quality Checklist

```
Scenario: HolidaySale_LoadTest
==============================

Structure:
[  ] Scenario type: Manual
[  ] Scenario saved to file

Groups:
[  ] Group 1: ________ - __ VUsers
[  ] Group 2: ________ - __ VUsers  
[  ] Group 3: ________ - __ VUsers
[  ] Total: 50 VUsers

Schedule:
[  ] Ramp-up configured (not instant)
[  ] Steady state duration: __ minutes
[  ] Ramp-down configured

Load Generator:
[  ] localhost connected
[  ] Status: Ready

Ready for Execution: YES / NO
```

## Troubleshooting Guide

| Issue | Possible Cause | Solution |
|-------|---------------|----------|
| Cannot add script | Script path issue | Use Browse, check .usr file exists |
| VUser count exceeds 50 | Community Edition limit | Reduce total to 50 |
| Load Generator not connecting | Agent service | Restart LoadRunner Agent Service |
| Schedule not saving | Validation error | Check all required fields filled |
| Script shows errors | Script issue | Fix in VuGen first |

## Stretch Goals (Optional)

If you finish early:
1. Create a second scenario with **Goal-Oriented** type targeting 100 TPS
2. Experiment with different ramp-up patterns (stepped vs. gradual)
3. Add **IP Spoofing** configuration to simulate different client IPs
4. Create a **Run-time settings** profile for each group
5. Explore the **Scenario Groups** dialog for advanced options

## Understanding Scenario Design

```
Why Gradual Ramp-Up?
────────────────────

Instant Start (BAD):                    Gradual Ramp-Up (GOOD):
                                        
VUsers                                  VUsers
   ▲                                       ▲
50 │████████████████████                50 │           ████████████
   │████████████████████                   │         ╱╱
   │████████████████████                   │       ╱╱
   │████████████████████                   │     ╱╱
   │████████████████████                   │   ╱╱
 0 └─────────────────────▶              0 └──────────────────────▶
   
Problem:                                Benefit:
- Server overload at start              - Allows caches to warm up
- Doesn't reflect reality               - Simulates realistic user arrival
- Can crash before measuring            - Identifies load-related issues
```

## Common Mistakes to Avoid

1. **Starting all VUsers instantly** - Use gradual ramp-up for realistic simulation
2. **Test too short** - Allow at least 10 minutes steady state for meaningful data
3. **Not saving before running** - Scenario configuration will be lost
4. **Ignoring Load Generator status** - Test won't run if not connected
5. **Exceeding 50 VUsers** - Community Edition will not allow more

## Submission

1. Save your scenario file (.lrs)
2. Take a screenshot of the Design view showing all groups
3. Take a screenshot of the Schedule showing ramp-up configuration

Commit message format:
```
feat(week9): Complete Controller scenario creation exercise
```

