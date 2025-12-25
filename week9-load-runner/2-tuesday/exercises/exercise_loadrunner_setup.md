# Lab: LoadRunner Community Edition Setup and Verification

## Overview
| Attribute | Value |
|-----------|-------|
| **Difficulty** | Beginner |
| **Time Estimate** | 60-90 minutes |
| **Mode** | Individual Lab |
| **Prerequisites** | Windows 10/11 with admin rights, 20GB free disk space, loadrunner-introduction.md |

## Learning Objectives
By completing this exercise, you will:
- Download and install LoadRunner Community Edition
- Verify all four core components are functional
- Explore the VuGen, Controller, and Analysis interfaces
- Confirm the Load Generator agent is running and connectable
- Understand LoadRunner's component architecture hands-on

## The Scenario

Your team has decided to adopt LoadRunner for enterprise performance testing. As the newest member of the QA team, you've been tasked with setting up LoadRunner on your workstation and verifying everything works before the team begins creating test scripts tomorrow.

## Core Tasks

### Task 1: Download LoadRunner Community Edition (15 minutes)

1. Navigate to the Micro Focus website: https://www.microfocus.com/en-us/products/loadrunner-professional/free-trial
2. Register for a free account if you don't have one
3. Download **LoadRunner Community Edition**
4. Note the download size (~2-3 GB) and estimated time

**Document the following:**
- Download file name: ________________
- File size: ________________
- Download date: ________________

### Task 2: Install LoadRunner (30-45 minutes)

Run the installer **as Administrator** and complete the installation:

1. **Accept the license agreement**
2. **Component Selection** - Select all core components:
   - [ ] Virtual User Generator (VuGen)
   - [ ] Controller
   - [ ] Analysis
   - [ ] Load Generator

3. **Protocol Selection** - Ensure these protocols are selected:
   - [ ] Web HTTP/HTML
   - [ ] Web Services
   - [ ] TruClient

4. **Installation Path** - Use default or note your custom path:
   - Installation path: ________________

5. Wait for installation to complete (may take 20-30 minutes)

### Task 3: Activate Community License (10 minutes)

1. Launch any LoadRunner component (VuGen is recommended)
2. When prompted, select **Community Edition**
3. Register with your email address
4. Verify the license shows: **Community - 50 VUsers**

**Document:**
- License type confirmed: ________________
- Maximum VUsers: ________________

### Task 4: Verify Each Component Launches (15 minutes)

Launch and verify each component:

#### VuGen Verification
```
Start Menu → LoadRunner → Virtual User Generator
```
- [ ] VuGen opens successfully
- [ ] "Create New Script" dialog appears
- [ ] Protocol list is populated (Web HTTP/HTML visible)

Screenshot location: ________________

#### Controller Verification
```
Start Menu → LoadRunner → Controller
```
- [ ] Controller opens successfully
- [ ] "New Scenario" option is available
- [ ] Design view is accessible

Screenshot location: ________________

#### Analysis Verification
```
Start Menu → LoadRunner → Analysis
```
- [ ] Analysis opens successfully
- [ ] "Open Results" dialog appears
- [ ] Graph gallery menu is accessible

Screenshot location: ________________

### Task 5: Verify Load Generator Agent (10 minutes)

1. Open PowerShell as Administrator
2. Run the following command:
   ```powershell
   Get-Service -Name "LoadRunner Agent*"
   ```

3. Verify the output shows the service is **Running**

4. In Controller:
   - Go to **Scenario → Load Generators**
   - Click **Add**
   - Enter **localhost**
   - Click **Connect**
   - Verify status shows: **● Connected (green)**

**Document:**
- Agent service status: ________________
- Controller connection status: ________________

### Task 6: Interface Exploration (15 minutes)

Explore each component and document your findings:

#### VuGen Exploration
Navigate through VuGen and identify:
- Where do you select a protocol? ________________
- Where is the script editor? ________________
- How do you run/replay a script? ________________
- Where are Runtime Settings? ________________

#### Controller Exploration
Navigate through Controller and identify:
- How do you add scripts to a scenario? ________________
- Where do you configure VUser groups? ________________
- Where is the real-time monitoring view? ________________
- How do you start a test? ________________

#### Analysis Exploration
Navigate through Analysis and identify:
- How do you open results? ________________
- Where is the graph gallery? ________________
- How do you generate reports? ________________

## Definition of Done

Your setup is complete when:
- [ ] LoadRunner Community Edition is installed with all four components
- [ ] License is activated and shows 50 VUsers maximum
- [ ] VuGen launches and shows protocol list
- [ ] Controller launches and can create new scenarios
- [ ] Analysis launches and can access graph gallery
- [ ] Load Generator agent service is running
- [ ] Controller can connect to localhost Load Generator
- [ ] You have documented all verification steps above

## Verification Checklist

Complete this checklist and save it for your records:

```
LoadRunner Installation Verification
====================================
Date: ________________
Installer: ________________

Components Installed:
[  ] VuGen
[  ] Controller  
[  ] Analysis
[  ] Load Generator

License:
[  ] Community Edition activated
[  ] 50 VUsers confirmed

Component Launch Test:
[  ] VuGen - PASS/FAIL
[  ] Controller - PASS/FAIL
[  ] Analysis - PASS/FAIL

Load Generator:
[  ] Agent service running - PASS/FAIL
[  ] Controller connection - PASS/FAIL

Overall Status: READY / NOT READY
```

## Troubleshooting Guide

| Issue | Possible Cause | Solution |
|-------|---------------|----------|
| Installation hangs | Insufficient permissions | Run as Administrator |
| Missing protocols | Incomplete install | Modify installation, add protocols |
| License error | Network issue | Check internet connection, retry |
| Agent won't start | .NET Framework | Update .NET Framework |
| VuGen crashes | Visual C++ | Install Visual C++ Redistributable |
| Connection refused | Firewall | Allow port 54345 through firewall |

## Stretch Goals (Optional)

If you finish early:
1. Take screenshots of each component and organize them in a folder
2. Explore the LoadRunner documentation (Help menu)
3. Research what protocols your organization might need beyond Web HTTP/HTML
4. Find and bookmark three LoadRunner tutorial resources online

## Common Mistakes to Avoid

1. **Not running installer as Administrator** - Installation will fail or be incomplete
2. **Skipping protocol selection** - You won't be able to test certain application types
3. **Not verifying Load Generator connection** - Tests won't run without this
4. **Ignoring firewall prompts** - May block necessary communication

## Submission

Create a brief summary document (Word or text file) with:
1. Completed verification checklist
2. Screenshots of each component running
3. Any issues encountered and how you resolved them

Save as: `LoadRunner_Setup_Verification_[YourName].docx`

