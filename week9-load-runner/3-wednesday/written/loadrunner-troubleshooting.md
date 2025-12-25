# LoadRunner Troubleshooting

## Learning Objectives
- Identify and resolve common LoadRunner issues
- Troubleshoot script replay errors effectively
- Diagnose and fix load generator connectivity problems
- Understand and resolve licensing issues
- Address result collection problems
- Apply debugging techniques for complex issues

## Why This Matters

Even experienced performance engineers encounter issues. Knowing how to troubleshoot efficiently saves hours of frustration and keeps projects on schedule. The difference between a novice and an expert often lies not in avoiding problems, but in solving them quickly.

As you complete **Mastering Enterprise Performance Testing with LoadRunner**, troubleshooting skills ensure you can handle real-world challenges confidently.

## Common LoadRunner Issues Overview

```
Issue Categories by Frequency:
──────────────────────────────

Script Issues          ████████████████████ 35%
├── Correlation failures
├── Parameterization errors
└── Response validation failures

Connectivity Issues    ████████████████ 25%
├── Load generator offline
├── Firewall blocking
└── Authentication failures

Result Issues          ████████████ 20%
├── Results not saved
├── Data gaps
└── Analysis errors

License Issues         ████████ 12%
├── License expired
├── VUser limits
└── Protocol restrictions

Environment Issues     █████ 8%
├── Resource exhaustion
├── Configuration errors
└── Version mismatches
```

## Script Replay Errors

Script issues are the most common problems. Here's how to diagnose and fix them.

### HTTP Error Responses

```
Common HTTP Errors:
───────────────────

Error 401: Unauthorized
─────────────────────────
Symptoms:
- Login fails during replay
- "Authentication required" message

Causes:
├── Credentials expired/changed
├── Session not established
└── Missing authentication header

Solutions:
✓ Verify credentials in parameters
✓ Check web_set_user() settings
✓ Correlate authentication tokens
✓ Verify script order (login first)


Error 403: Forbidden
─────────────────────────
Symptoms:
- Specific pages fail
- "Access denied" message

Causes:
├── Missing CSRF token
├── Referrer validation
└── IP-based restrictions

Solutions:
✓ Correlate CSRF tokens
✓ Add Referer header
✓ Check IP whitelist
✓ Verify user permissions


Error 404: Not Found
─────────────────────────
Symptoms:
- Resource URLs fail
- Dynamic URLs not found

Causes:
├── Hardcoded dynamic URLs
├── Application changed
└── Missing correlation

Solutions:
✓ Correlate dynamic URL parts
✓ Re-record affected sections
✓ Check application logs


Error 500: Server Error
─────────────────────────
Symptoms:
- Server crashes on request
- Generic error page

Causes:
├── Invalid data submitted
├── Server-side bug
└── Load-induced failure

Solutions:
✓ Verify request payload
✓ Check server logs
✓ Reduce load to isolate
✓ Report to development team
```

### Correlation Failures

```
Correlation Issues:
───────────────────

Issue: "Text not found" Error
─────────────────────────────
Message: "Error -26377: No match found for the 
          requested parameter"

Diagnosis Steps:
1. Check if value exists in response
   → View previous response in log
   
2. Verify boundary strings
   → Left/right boundaries may have changed
   
3. Check search scope
   → Body vs Headers vs All

Solution:
// Update correlation with correct boundaries
web_reg_save_param_ex(
    "ParamName=SessionID",
    "LB=sessionId\":\"",      // Updated left boundary
    "RB=\"",                   // Updated right boundary
    "Ordinal=1",
    SEARCH_FILTERS,
    "Scope=Body",
    "RequestUrl=*/login*",    // Limit search scope
    LAST);


Issue: Wrong Value Captured
───────────────────────────
Symptoms:
- Correlation captures value
- But it's the wrong value

Causes:
├── Multiple matches, wrong ordinal
├── Similar patterns elsewhere
└── Search scope too broad

Solution:
// Use ordinal to get specific match
web_reg_save_param_ex(
    "ParamName=ProductID",
    "LB=productId=\"",
    "RB=\"",
    "Ordinal=3",          // Get third match
    SEARCH_FILTERS,
    "Scope=Body",
    LAST);

// Or use more specific boundaries
web_reg_save_param_ex(
    "ParamName=ProductID",
    "LB=<selected-product id=\"",  // More specific
    "RB=\"",
    LAST);
```

### Parameterization Issues

```
Parameter Problems:
───────────────────

Issue: Parameter File Not Found
───────────────────────────────
Error: "Cannot open file 'users.csv'"

Causes:
├── File path incorrect
├── File not in script folder
└── Permission issues

Solutions:
✓ Use relative paths from script folder
✓ Verify file exists and readable
✓ Check file extension matches config


Issue: Running Out of Data
──────────────────────────
Symptoms:
- Test stops early
- "No more values" message

Causes:
├── Not enough rows for iterations
├── Unique mode with too few values
└── Multiple parameters misaligned

Solutions:
✓ Add more data rows
✓ Use "Wrap around" for Sequential
✓ Match row counts across parameters

Configuration Check:
┌─────────────────────────────────────────────────────┐
│ Parameter: Username                                 │
│                                                     │
│ Select next row: Sequential                         │
│ Update value on: Each iteration                     │
│ When out of values: [Wrap around ▼]  ← Add this    │
│                                                     │
└─────────────────────────────────────────────────────┘
```

## Load Generator Connectivity

Connection issues prevent tests from running.

### Generator Status Issues

```
Load Generator States:
──────────────────────

● Ready     - Connected and ready for test
○ Down      - Not responding
⊘ Offline   - Disconnected
⚠ Error     - Connected but has errors

Diagnosis for "Down" Status:
────────────────────────────

1. VERIFY AGENT SERVICE
   Windows:
   > services.msc
   > Find "LoadRunner Agent Service"
   > Should be "Running"
   
   Linux:
   $ systemctl status lr_agent
   
   Fix: Start the service if stopped

2. CHECK NETWORK CONNECTIVITY
   From Controller machine:
   > ping <generator_hostname>
   > telnet <generator_hostname> 54345
   
   Fix: Verify network route, check firewall

3. VERIFY PORTS OPEN
   Required ports:
   ├── 54345 (Agent communication)
   ├── 443 (Secure channel)
   └── 50500-50600 (Data collection)
   
   Fix: Open ports in firewall

4. CHECK AGENT CONFIGURATION
   On generator machine:
   > Open Agent Configuration
   > Verify settings match Controller
   
   Fix: Reconfigure agent settings
```

### Firewall Configuration

```
Firewall Rules for LoadRunner:
──────────────────────────────

Controller → Load Generator:
┌────────────────────────────────────────────────────────────────┐
│ Port       │ Protocol │ Direction  │ Purpose                  │
├────────────┼──────────┼────────────┼──────────────────────────┤
│ 54345      │ TCP      │ Outbound   │ Agent communication      │
│ 443        │ TCP      │ Outbound   │ Secure channel           │
└────────────────────────────────────────────────────────────────┘

Load Generator → Controller:
┌────────────────────────────────────────────────────────────────┐
│ Port       │ Protocol │ Direction  │ Purpose                  │
├────────────┼──────────┼────────────┼──────────────────────────┤
│ 50500-50600│ TCP      │ Outbound   │ Results data             │
│ 443        │ TCP      │ Outbound   │ Secure channel           │
└────────────────────────────────────────────────────────────────┘

Windows Firewall Command:
netsh advfirewall firewall add rule ^
    name="LoadRunner Agent" ^
    dir=in ^
    action=allow ^
    protocol=TCP ^
    localport=54345
```

### Authentication Failures

```
Agent Authentication Issues:
────────────────────────────

Symptoms:
- "Authentication failed" error
- Generator shows "Down" after connection attempt

Causes:
├── Mismatched credentials
├── Domain/workgroup issues
└── Certificate problems

Solutions:

1. VERIFY CREDENTIALS
   Controller: Scenario → Load Generators → Options
   ├── Username: DOMAIN\username
   ├── Password: ********
   └── [Test connection]

2. USE SECURE CHANNEL
   If domain authentication fails:
   ├── Enable "Use secure channel"
   └── Configure certificates

3. LOCAL USER WORKAROUND
   Create matching local user on generator:
   > net user lruser Password123! /add
   > net localgroup Administrators lruser /add
```

## License Issues

License problems can block testing entirely.

### Common License Errors

```
License Error Types:
────────────────────

"No license available for protocol X"
─────────────────────────────────────
Cause: Protocol not included in license

Solutions:
✓ Check license includes needed protocol
✓ Use Community Edition protocols only
✓ Contact Micro Focus for license upgrade

Community Edition Protocols:
├── Web (HTTP/HTML)
├── TruClient (Web)
├── Web Services
└── Mobile Web


"VUser limit exceeded"
──────────────────────
Cause: Trying to run more VUsers than licensed

Solutions:
✓ Reduce VUser count
✓ Community Edition: 50 VUsers max
✓ Purchase additional capacity


"License expired"
─────────────────
Cause: License term ended

Solutions:
✓ Renew license
✓ Download latest Community Edition
✓ Contact Micro Focus for renewal
```

### Checking License Status

```
View License Information:
─────────────────────────

VuGen:
Help → About → License Information

Controller:
Tools → License Information

Command Line:
> LicenseCheck.exe -list

Output:
┌─────────────────────────────────────────────────────────────┐
│ LoadRunner License Information                              │
├─────────────────────────────────────────────────────────────┤
│ License Type: Community Edition                             │
│ Expiration: Never (Community)                               │
│ VUser Limit: 50                                             │
│                                                             │
│ Licensed Protocols:                                         │
│ ├── Web (HTTP/HTML)          ✓ Available                    │
│ ├── TruClient Web            ✓ Available                    │
│ ├── Web Services             ✓ Available                    │
│ ├── SAP                      ✗ Not licensed                 │
│ └── Citrix                   ✗ Not licensed                 │
└─────────────────────────────────────────────────────────────┘
```

## Result Collection Problems

Incomplete results make analysis impossible.

### Missing Results

```
Results Collection Issues:
──────────────────────────

Symptoms:
- "No results found" in Analysis
- Partial data gaps
- Missing transactions

Diagnosis:

1. CHECK RESULT LOCATION
   Controller → Results → Results Settings
   Verify path is accessible and writable

2. VERIFY COLLECTION ENABLED
   Runtime Settings → Log
   ├── [✓] Enable logging
   └── [✓] Send messages always

3. CHECK DISK SPACE
   Result files can be large:
   - 10-50 MB per hour per generator
   - Ensure adequate free space

4. REVIEW COLLATION STATUS
   After test, check:
   Controller → Results → Collation Log
   
   Common issues:
   ├── Network timeout during collection
   ├── Generator disconnected early
   └── Collation interrupted


Solutions:
─────────
✓ Increase timeout for slow networks
✓ Use local results, manual collection
✓ Clean up old results for space
✓ Verify network stability
```

### Corrupted Results

```
Result Corruption:
──────────────────

Symptoms:
- Analysis won't open results
- "File format error" message
- Missing time periods

Causes:
├── Network interruption during collection
├── Test stopped abnormally
├── Disk full during test
└── Generator crash mid-test

Solutions:

1. TRY RECOVERY
   Analysis → File → Open
   Select folder instead of .lrr file
   Analysis attempts reconstruction

2. USE RAW DATA
   Navigate to results folder
   ├── Find individual .lrr files per generator
   └── Open separately if main file corrupt

3. PARTIAL SALVAGE
   If some data readable:
   ├── Filter to working time ranges
   └── Document missing periods

Prevention:
✓ Monitor disk space during tests
✓ Use stable network connections
✓ Let tests complete naturally when possible
✓ Keep backup of collated results
```

## Debugging Techniques

Systematic debugging resolves complex issues.

### Extended Logging

```
Enable Debug Logging:
─────────────────────

VuGen Runtime Settings → Log:
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│ Log Level:                                                  │
│ ( ) Standard log                                            │
│ (•) Extended log                                            │
│                                                             │
│ Extended options:                                           │
│ [✓] Parameter substitution                                  │
│ [✓] Data returned by server                                 │
│ [✓] Advanced trace                                          │
│                                                             │
│ Log message destination:                                    │
│ (•) Always send messages                                    │
│ ( ) Send only on error                                      │
│                                                             │
└─────────────────────────────────────────────────────────────┘

Reading Extended Logs:
───────────────────────
- Look for "Error" or "Warning" keywords
- Check parameter values at substitution
- Review server responses for clues
- Note timing of failures
```

### Isolation Testing

```
Isolation Strategy:
───────────────────

1. SINGLE USER TEST
   Run script with 1 VUser, 1 iteration
   ├── If fails: Script issue
   └── If passes: Continue to step 2

2. MULTIPLE ITERATIONS
   Run with 1 VUser, 10 iterations
   ├── If fails: Data/parameter issue
   └── If passes: Continue to step 3

3. SMALL LOAD TEST
   Run with 5 VUsers
   ├── If fails: Concurrency issue
   └── If passes: Continue to step 4

4. GRADUAL SCALE
   Increase VUsers incrementally
   ├── Note when problems appear
   └── Correlate with resource metrics

Isolation helps pinpoint:
✓ Script errors (step 1)
✓ Data exhaustion (step 2)
✓ Race conditions (step 3)
✓ Capacity limits (step 4)
```

### Using Breakpoints and Step Execution

```
Debugging in VuGen:
───────────────────

Set Breakpoint:
- Click line margin
- Or: F9 on selected line
- Red circle appears

Step Execution:
- F10: Step over (execute current line)
- F11: Step into (enter function)
- F5: Continue to next breakpoint

Watch Variables:
- Debug → Watch
- Add parameter names
- See values update during execution

Debugging Workflow:
1. Set breakpoint before failing line
2. Run in debug mode (F5)
3. Examine variables when stopped
4. Step through to find exact failure point
5. Check server response content
```

## Quick Reference: Error Messages

```
Common Error Messages and Solutions:
────────────────────────────────────

"Error -27796: Failed to connect to server"
Solution: Check server is up, verify URL, check network

"Error -26612: HTTP Status-Code=500"
Solution: Server error - check server logs, verify request data

"Error -27791: Attempted SSL connection but SSL is not enabled"
Solution: Use web_set_sockets_option("SSL_VERSION", "AUTO")

"Error -26377: No match found for requested parameter"
Solution: Update correlation boundaries, verify response contains value

"Error -27727: Connection attempt timed out"
Solution: Increase timeout, check network latency

"Error -81024: Download timeout (120 seconds) expired"
Solution: Increase web_set_timeout, check server response time
```

## Summary

- **Script errors** are most common; systematic replay testing identifies issues
- **Correlation and parameterization** problems require careful boundary and data verification
- **Load generator connectivity** depends on services, network, and firewall configuration
- **License issues** can be resolved by checking license status and staying within limits
- **Result collection** requires adequate disk space and stable network connections
- **Debugging techniques** like extended logging, isolation testing, and step execution help resolve complex issues

## Additional Resources

- [LoadRunner Troubleshooting Guide - Micro Focus](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Troubleshooting/c_troubleshooting.htm)
- [LoadRunner Error Messages Reference](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Troubleshooting/c_error_messages.htm)
- [LoadRunner Community Forums](https://community.microfocus.com/adtd/loadrunner/)

