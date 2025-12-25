# Virtual User Generator (VuGen) Overview

## Learning Objectives
- Understand the purpose and capabilities of Virtual User Generator
- Learn how to record user actions across different protocols
- Master protocol selection for various application types
- Understand VuGen script structure (Actions, vuser_init, vuser_end)
- Learn the fundamentals of parameterization and correlation

## Why This Matters

Creating effective performance test scripts is the foundation of meaningful load testing. A poorly designed script produces meaningless results, while a well-crafted script accurately simulates real user behavior and reveals genuine performance issues.

VuGen is where your performance testing journey begins in the LoadRunner ecosystem. As you work toward **Mastering Enterprise Performance Testing with LoadRunner**, VuGen proficiency determines how realistic your tests are and how valuable your results become. The skills you develop here directly impact your ability to catch performance issues before they affect real users.

## What is VuGen?

**Virtual User Generator (VuGen)** is LoadRunner's integrated development environment for creating, editing, and debugging performance test scripts. Think of it as the "IDE" for performance testers, similar to how developers use Visual Studio or IntelliJ.

### VuGen Capabilities

| Capability | Description |
|------------|-------------|
| **Recording** | Capture user interactions with applications automatically |
| **Script Editing** | Modify recorded scripts with a full-featured editor |
| **Parameterization** | Replace static values with dynamic data |
| **Correlation** | Handle dynamic session data automatically |
| **Debugging** | Step through scripts, set breakpoints, inspect variables |
| **Validation** | Add checkpoints to verify application responses |
| **Replay** | Execute scripts locally to verify correctness |

## Recording User Actions

Recording is the fastest way to create VuGen scripts. Instead of writing code manually, you perform actions in your application while VuGen captures everything.

### The Recording Process

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        VuGen Recording Process                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   Step 1: Configure          Step 2: Record           Step 3: Review        │
│   ┌─────────────────┐       ┌─────────────────┐      ┌─────────────────┐   │
│   │ Select Protocol │──────▶│ Perform Actions │─────▶│ Examine Script  │   │
│   │ Set Options     │       │ in Application  │      │ Enhance Code    │   │
│   │ Choose Browser  │       │                 │      │ Add Validation  │   │
│   └─────────────────┘       └─────────────────┘      └─────────────────┘   │
│                                                                             │
│   Example: Web HTTP/HTML                                                    │
│                                                                             │
│   User Action:              Captured As:                                    │
│   ─────────────             ───────────────────────────────────────         │
│   Navigate to URL    ──▶    web_url("Homepage", "URL=https://...");         │
│   Enter username     ──▶    web_submit_data("Login", ...);                  │
│   Click login        ──▶    web_submit_form("LoginForm", ...);              │
│   Search for item    ──▶    web_submit_data("Search", ...);                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Recording Modes

VuGen offers different recording approaches depending on the protocol:

| Mode | Description | Protocols |
|------|-------------|-----------|
| **Proxy Recording** | Routes traffic through VuGen proxy server | Web HTTP/HTML, Web Services |
| **Browser-based** | Embeds recorder in browser session | TruClient, Mobile Web |
| **Native Hooks** | Intercepts application calls directly | SAP GUI, Citrix, Java |
| **Network Capture** | Captures network traffic at packet level | Various protocols |

### Recording Best Practices

1. **Plan your user journey** before recording
2. **Clear browser cache** before starting
3. **Use realistic data** during recording
4. **Perform actions at normal speed** (not too fast)
5. **Include think time** between actions
6. **Record complete workflows** from login to logout

## Protocol Selection Guide

Choosing the correct protocol is critical. The wrong protocol produces scripts that don't accurately simulate your application.

### Protocol Categories and Selection

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      Protocol Selection Matrix                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   Application Type          Recommended Protocol       Notes                │
│   ─────────────────────────────────────────────────────────────────────     │
│                                                                             │
│   Traditional Web App       Web HTTP/HTML              Server-rendered HTML │
│   (PHP, ASP.NET, JSP)                                  forms, links, etc.   │
│                                                                             │
│   Modern SPA                TruClient (Web)            React, Angular, Vue  │
│   (JavaScript-heavy)                                   Client-side rendering│
│                                                                             │
│   REST API                  Web Services - REST        JSON/XML responses   │
│                                                                             │
│   SOAP Web Services         Web Services - SOAP        WSDL-based services  │
│                                                                             │
│   Mobile App (Native)       Mobile Application         iOS/Android native   │
│                                                                             │
│   Mobile Web                TruClient (Mobile)         Mobile browsers      │
│                                                                             │
│   SAP Application           SAP GUI                    SAP transactions     │
│                                                                             │
│   Citrix Applications       Citrix ICA                 Virtual desktops     │
│                                                                             │
│   Oracle Forms              Oracle NCA                 Oracle applications  │
│                                                                             │
│   Database Operations       ODBC                       Direct DB testing    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Web HTTP/HTML vs. TruClient

This is the most common decision point for web applications:

| Aspect | Web HTTP/HTML | TruClient |
|--------|---------------|-----------|
| **Approach** | Protocol level (HTTP requests) | Browser level (DOM interactions) |
| **Best For** | Server-rendered apps, APIs | JavaScript-heavy SPAs |
| **Resource Usage** | Light (no browser) | Heavy (real browser per VUser) |
| **Correlation** | Manual/automatic | Usually not needed |
| **Scripting** | C-like language | JavaScript |
| **Scalability** | High (thousands of VUsers) | Lower (browser overhead) |

**Decision Rule**: If your application works without JavaScript, use HTTP/HTML. If JavaScript is essential, use TruClient.

## Script Structure

Every VuGen script follows a consistent three-section structure, regardless of protocol.

### The Three Sections

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        VuGen Script Structure                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │                         vuser_init                                  │  │
│   │   • Executes ONCE per virtual user                                  │  │
│   │   • Runs at the START of virtual user lifecycle                     │  │
│   │   • Purpose: Initialize, login, establish connections               │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                    │                                        │
│                                    ▼                                        │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │                           Action                                    │  │
│   │   • Executes for EACH iteration                                     │  │
│   │   • The main business workflow                                      │  │
│   │   • Purpose: Simulate typical user actions                          │  │
│   │   • Can have multiple Action files (Action1, Action2, etc.)         │  │
│   │                                                                     │  │
│   │   ┌──────────┐    ┌──────────┐    ┌──────────┐                     │  │
│   │   │Iteration1│───▶│Iteration2│───▶│Iteration3│───▶ ...             │  │
│   │   └──────────┘    └──────────┘    └──────────┘                     │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                    │                                        │
│                                    ▼                                        │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │                         vuser_end                                   │  │
│   │   • Executes ONCE per virtual user                                  │  │
│   │   • Runs at the END of virtual user lifecycle                       │  │
│   │   • Purpose: Logout, cleanup, close connections                     │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Complete Script Example

```c
// ═══════════════════════════════════════════════════════════════════════════
// vuser_init.c - Initialization (runs once per VUser)
// ═══════════════════════════════════════════════════════════════════════════
vuser_init()
{
    // Set up web defaults
    web_set_sockets_option("SSL_VERSION", "AUTO");
    
    // Navigate to application
    web_url("Navigate_Home",
        "URL=https://shop.example.com/",
        "Resource=0",
        "RecContentType=text/html",
        LAST);
    
    // Log in (happens once per virtual user)
    web_submit_data("Login",
        "Action=https://shop.example.com/api/login",
        "Method=POST",
        "RecContentType=application/json",
        "Mode=HTTP",
        ITEMDATA,
        "Name=username", "Value={Username}", ENDITEM,  // Parameterized
        "Name=password", "Value={Password}", ENDITEM,  // Parameterized
        LAST);
    
    return 0;
}

// ═══════════════════════════════════════════════════════════════════════════
// Action.c - Main workflow (runs for each iteration)
// ═══════════════════════════════════════════════════════════════════════════
Action()
{
    // Start timing the search operation
    lr_start_transaction("Search_Product");
    
    // Perform search
    web_submit_data("Search",
        "Action=https://shop.example.com/api/search",
        "Method=POST",
        "RecContentType=application/json",
        ITEMDATA,
        "Name=query", "Value={SearchTerm}", ENDITEM,  // Different each iteration
        LAST);
    
    // End timing
    lr_end_transaction("Search_Product", LR_AUTO);
    
    // Simulate user reading results (think time)
    lr_think_time(5);
    
    // Start timing add to cart
    lr_start_transaction("Add_To_Cart");
    
    // Add item to cart
    web_submit_data("AddToCart",
        "Action=https://shop.example.com/api/cart/add",
        "Method=POST",
        "RecContentType=application/json",
        ITEMDATA,
        "Name=productId", "Value={ProductID}", ENDITEM,
        "Name=quantity", "Value=1", ENDITEM,
        LAST);
    
    lr_end_transaction("Add_To_Cart", LR_AUTO);
    
    return 0;
}

// ═══════════════════════════════════════════════════════════════════════════
// vuser_end.c - Cleanup (runs once per VUser)
// ═══════════════════════════════════════════════════════════════════════════
vuser_end()
{
    // Log out
    web_url("Logout",
        "URL=https://shop.example.com/api/logout",
        "Resource=0",
        LAST);
    
    return 0;
}
```

### Multiple Actions

You can split complex workflows into multiple Action files:

```
Script Structure:
├── vuser_init.c      → Login
├── Action.c          → Browse products
├── Action2.c         → Add to cart
├── Action3.c         → Checkout
└── vuser_end.c       → Logout

Runtime Settings control which Actions run and how many iterations.
```

## Script Parameters and Parameterization

**Parameterization** replaces static (hardcoded) values with dynamic data, making scripts more realistic.

### Why Parameterize?

```
Without Parameterization:              With Parameterization:
───────────────────────────            ────────────────────────────
All 1000 VUsers search for             Each VUser searches for
"laptop"                               different products
                                       
Result: Unrealistic, cache-            Result: Realistic simulation,
friendly test                          actual database queries

All 1000 VUsers use                    Each VUser logs in with
"testuser1" / "password1"              unique credentials

Result: Single session,                Result: 1000 unique sessions,
not representative                     true load simulation
```

### Creating Parameters

```c
// Before: Hardcoded value
web_submit_data("Search",
    "Action=https://shop.example.com/search",
    ITEMDATA,
    "Name=query", "Value=laptop", ENDITEM,  // Always searches "laptop"
    LAST);

// After: Parameterized value
web_submit_data("Search",
    "Action=https://shop.example.com/search",
    ITEMDATA,
    "Name=query", "Value={SearchTerm}", ENDITEM,  // Uses parameter
    LAST);
```

### Parameter Types

| Type | Description | Example |
|------|-------------|---------|
| **File** | Read values from CSV/DAT file | User credentials, product IDs |
| **Random Number** | Generate random numbers | Order quantities |
| **Unique Number** | Sequential unique numbers | Transaction IDs |
| **Date/Time** | Current or formatted date | Timestamps |
| **User Defined** | Custom values | Environment-specific settings |

### Parameter File Example

**users.csv:**
```csv
Username,Password,Email
john.doe,Pass123!,john@example.com
jane.smith,Pass456!,jane@example.com
bob.wilson,Pass789!,bob@example.com
```

**VuGen configuration:**
```c
// Parameter "Username" reads from column 1
// Parameter "Password" reads from column 2
// Parameter "Email" reads from column 3

// Update method: Sequential, Random, or Unique
// Scope: Each iteration, Each occurrence, Once
```

## Correlation Fundamentals

**Correlation** handles dynamic values that change between sessions, such as session IDs, tokens, and CSRF values.

### The Correlation Problem

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    Why Correlation is Necessary                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   Recording Session:                                                        │
│   ──────────────────                                                        │
│   Server sends:     sessionId = "ABC123"                                    │
│   Script captures:  sessionId = "ABC123" (hardcoded)                        │
│                                                                             │
│   Replay Session:                                                           │
│   ────────────────                                                          │
│   Server sends:     sessionId = "XYZ789" (new session!)                     │
│   Script sends:     sessionId = "ABC123" (old, invalid!)                    │
│   Server responds:  "Invalid session" → TEST FAILS                          │
│                                                                             │
│   With Correlation:                                                         │
│   ─────────────────                                                         │
│   Server sends:     sessionId = "XYZ789"                                    │
│   Script captures:  sessionId = {CorrelatedValue}                           │
│   Script sends:     sessionId = "XYZ789" (captured value!)                  │
│   Server responds:  SUCCESS                                                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Correlation Methods

**1. Automatic Correlation (Recommended)**

VuGen can detect and correlate common dynamic values automatically:

```c
// VuGen inserts this before the request that receives the dynamic value
web_reg_save_param_ex(
    "ParamName=sessionId",
    "LB=sessionId\":\"",           // Left boundary
    "RB=\"",                        // Right boundary
    SEARCH_FILTERS,
    "Scope=BODY",
    LAST);

// The captured value is used in subsequent requests
web_submit_data("NextRequest",
    ...
    "Name=sessionId", "Value={sessionId}", ENDITEM,
    ...);
```

**2. Manual Correlation**

For complex scenarios, manually define correlation rules:

```c
// Save the value from server response
web_reg_save_param_ex(
    "ParamName=csrfToken",
    "LB=<input name=\"_csrf\" value=\"",
    "RB=\"",
    SEARCH_FILTERS,
    "Scope=BODY",
    LAST);

// Use the captured value
web_submit_data("SubmitForm",
    ITEMDATA,
    "Name=_csrf", "Value={csrfToken}", ENDITEM,
    LAST);
```

### Common Values Requiring Correlation

| Value Type | Example | Detection Pattern |
|------------|---------|-------------------|
| Session ID | `JSESSIONID=ABC123` | Cookie or URL parameter |
| CSRF Token | `_csrf=xyz789` | Hidden form field |
| View State | `__VIEWSTATE=...` | ASP.NET state |
| Timestamp | `ts=1699876543` | Numeric, changes each request |
| Transaction ID | `txnId=TXN001` | Generated by server |

## Replay and Debugging

VuGen includes powerful tools for verifying scripts work correctly.

### Single-User Replay

Before running load tests, always verify your script works:

```
VuGen Replay Process:
1. Compile script (check for syntax errors)
2. Execute with single virtual user
3. Watch execution log in real-time
4. Verify all transactions pass
5. Check response content for expected data
```

### Debugging Features

| Feature | Description |
|---------|-------------|
| **Breakpoints** | Pause execution at specific lines |
| **Step Execution** | Execute one statement at a time |
| **Watch Variables** | Monitor parameter values during execution |
| **Execution Log** | Detailed log of all actions and responses |
| **Snapshot Viewer** | View captured HTTP responses |

### Common Replay Issues

```
Issue                          Likely Cause                    Solution
───────────────────────────────────────────────────────────────────────────
HTTP 403 Forbidden             Missing/invalid session         Add correlation
                                                               
"Page not found"               Hardcoded dynamic URL           Parameterize URL

"Invalid token"                CSRF not correlated             Correlate CSRF token

"Session expired"              Think time too long             Adjust think time

SSL/Certificate error          Certificate validation          Configure SSL settings
```

## Summary

- **VuGen** is LoadRunner's IDE for creating and editing performance test scripts
- **Recording** captures user actions and generates script code automatically
- **Protocol selection** is critical: choose HTTP/HTML for server-rendered apps, TruClient for JavaScript-heavy SPAs
- Scripts follow a **three-section structure**: `vuser_init` (once at start), `Action` (each iteration), `vuser_end` (once at end)
- **Parameterization** replaces static values with dynamic data for realistic tests
- **Correlation** captures and reuses dynamic server values like session IDs and tokens
- Always **verify scripts with single-user replay** before running load tests

## Additional Resources

- [VuGen User Guide - Micro Focus](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/VuGen/c_vugen_intro.htm)
- [Web HTTP/HTML Protocol Reference](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/VuGen/c_web_http_html.htm)
- [Correlation Best Practices](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/VuGen/c_correlations.htm)

