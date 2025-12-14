# JMeter GUI Mode

## Learning Objectives
- Navigate and use the JMeter graphical interface effectively
- Create test plans visually using GUI components
- Configure thread groups with appropriate settings
- Set up HTTP samplers for API testing
- Add and configure listeners for viewing results

## Why This Matters

In our **"From API to UI: Mastering Full-Stack Test Automation"** journey, JMeter's GUI mode is your gateway to performance testing. While CLI mode is essential for CI/CD integration, the GUI provides the visual environment where you'll design, debug, and initially validate your test plans.

Understanding the GUI deeply—its components, workflows, and best practices—makes the difference between struggling with JMeter and efficiently creating powerful performance tests. This lesson transforms the GUI from an intimidating interface into your productivity tool.

## Launching JMeter GUI

### Starting JMeter

**Windows:**
```batch
cd apache-jmeter-5.6.2\bin
jmeter.bat
```

**macOS/Linux:**
```bash
cd apache-jmeter-5.6.2/bin
./jmeter.sh
```

### Initial Configuration

```
Recommended JMeter Settings (jmeter.properties):
────────────────────────────────────────────────────────────────
# Increase heap size for large tests
# Edit jmeter.bat or jmeter.sh:
# HEAP="-Xms1g -Xmx4g"

# Enable helpful settings:
view.results.tree.max_size=10485760  # 10MB max for View Results Tree
jmeter.save.saveservice.output_format=xml  # Save format
jmeter.save.saveservice.response_data=true  # Save response data
────────────────────────────────────────────────────────────────
```

## The JMeter Interface

### Main Window Components

```
┌─────────────────────────────────────────────────────────────────────┐
│ Menu Bar: File | Edit | Search | Run | Options | Tools | Help       │
├─────────────────────────────────────────────────────────────────────┤
│ Toolbar: [New][Open][Save] [Start][Stop] [Clear] [Expand/Collapse]  │
├──────────────────┬──────────────────────────────────────────────────┤
│  Test Plan Tree  │                                                   │
│  ┌────────────── │              Configuration Panel                  │
│  │ Test Plan     │                                                   │
│  │ ├─Thread Grp  │   (Settings for selected element appear here)    │
│  │ │ ├─HTTP Req  │                                                   │
│  │ │ ├─Listener  │                                                   │
│  │ │ └─Timer     │                                                   │
│  │ └─Config El   │                                                   │
│  └────────────── │                                                   │
├──────────────────┴──────────────────────────────────────────────────┤
│ Status Bar: Threads: 0/0 | Running: No                              │
└─────────────────────────────────────────────────────────────────────┘
```

### Key Toolbar Buttons

| Button | Shortcut | Function |
|--------|----------|----------|
| New | Ctrl+N | Create new test plan |
| Open | Ctrl+O | Open existing test plan |
| Save | Ctrl+S | Save current test plan |
| Start | Ctrl+R | Run the test |
| Stop | Ctrl+. | Stop the test immediately |
| Shutdown | - | Graceful stop (wait for samples) |
| Clear | Ctrl+Shift+E | Clear all results |
| Clear All | - | Clear all listeners |

### Test Plan Tree Navigation

```
Right-click Context Menu:
├── Add →
│   ├── Threads (Users) → Thread Group
│   ├── Config Element → (HTTP Defaults, CSV, etc.)
│   ├── Timer → (Constant, Random, etc.)
│   ├── Pre Processors → (User Parameters, etc.)
│   ├── Sampler → (HTTP Request, JDBC, etc.)
│   ├── Post Processors → (JSON Extractor, etc.)
│   ├── Assertions → (Response, Duration, etc.)
│   ├── Listener → (View Results, Summary, etc.)
│   └── Logic Controller → (Loop, If, ForEach, etc.)
├── Insert Parent →
├── Remove
├── Copy / Paste
├── Duplicate
├── Enable / Disable
└── Toggle (Enable/Disable)
```

## Creating Test Plans Visually

### Step 1: Configure Test Plan

```
Test Plan Configuration:
┌─────────────────────────────────────────────────────────────┐
│ Name: User API Performance Test                              │
│                                                              │
│ Comments: Load test for /api/users endpoint                  │
│                                                              │
│ User Defined Variables:                                      │
│   ┌────────────┬────────────────────────────────────────┐   │
│   │ Name       │ Value                                   │   │
│   ├────────────┼────────────────────────────────────────┤   │
│   │ BASE_URL   │ https://api.example.com                │   │
│   │ API_TOKEN  │ ${__P(api.token,default-token)}       │   │
│   │ TIMEOUT    │ 30000                                  │   │
│   └────────────┴────────────────────────────────────────┘   │
│                                                              │
│ [✓] Run Thread Groups consecutively                          │
│ [ ] Run tearDown Thread Groups after shutdown                │
│ [✓] Functional Test Mode (saves response data)               │
└─────────────────────────────────────────────────────────────┘
```

### Step 2: Add Thread Group

```
Right-click Test Plan → Add → Threads (Users) → Thread Group

Thread Group Configuration:
┌─────────────────────────────────────────────────────────────┐
│ Name: API Users                                              │
│                                                              │
│ Thread Properties:                                           │
│   Number of Threads (users): [100]                           │
│   Ramp-up period (seconds): [60]                             │
│   Loop Count: [10] OR [✓] Infinite                           │
│                                                              │
│ Scheduler:                                                   │
│   [✓] Scheduler                                              │
│   Duration (seconds): [300]                                  │
│   Startup delay (seconds): [0]                               │
│                                                              │
│ Action to be taken after a Sampler error:                    │
│   (•) Continue  ( ) Start Next Thread Loop                   │
│   ( ) Stop Thread  ( ) Stop Test  ( ) Stop Test Now          │
└─────────────────────────────────────────────────────────────┘
```

### Step 3: Add Configuration Elements

**HTTP Request Defaults:**
```
Right-click Thread Group → Add → Config Element → HTTP Request Defaults

HTTP Request Defaults:
┌─────────────────────────────────────────────────────────────┐
│ Web Server:                                                  │
│   Protocol: [https]                                          │
│   Server Name or IP: [api.example.com]                       │
│   Port Number: [443]                                         │
│                                                              │
│ HTTP Request:                                                │
│   Content encoding: [UTF-8]                                  │
│   Path: [/api/v1]                                            │
│                                                              │
│ Timeouts:                                                    │
│   Connect: [5000]                                            │
│   Response: [30000]                                          │
└─────────────────────────────────────────────────────────────┘
```

**HTTP Header Manager:**
```
Right-click Thread Group → Add → Config Element → HTTP Header Manager

Headers:
┌────────────────────────┬────────────────────────────────────┐
│ Name                   │ Value                               │
├────────────────────────┼────────────────────────────────────┤
│ Content-Type           │ application/json                    │
│ Accept                 │ application/json                    │
│ Authorization          │ Bearer ${API_TOKEN}                 │
│ User-Agent             │ JMeter-PerformanceTest             │
└────────────────────────┴────────────────────────────────────┘
```

## Configuring HTTP Samplers

### GET Request

```
Right-click Thread Group → Add → Sampler → HTTP Request

GET Request Configuration:
┌─────────────────────────────────────────────────────────────┐
│ Name: GET Users List                                         │
│                                                              │
│ Basic:                                                       │
│   Method: [GET ▼]                                            │
│   Path: /users                                               │
│                                                              │
│ Parameters:                                                  │
│   ┌──────────────┬─────────┬──────────┬─────────────┐       │
│   │ Name         │ Value   │ Encode?  │ Include?    │       │
│   ├──────────────┼─────────┼──────────┼─────────────┤       │
│   │ page         │ 1       │ ✓        │ ✓           │       │
│   │ limit        │ 20      │ ✓        │ ✓           │       │
│   │ status       │ active  │ ✓        │ ✓           │       │
│   └──────────────┴─────────┴──────────┴─────────────┘       │
│                                                              │
│ [Advanced] [Embedded Resources] [Source Address]             │
└─────────────────────────────────────────────────────────────┘
```

### POST Request with JSON Body

```
POST Request Configuration:
┌─────────────────────────────────────────────────────────────┐
│ Name: POST Create User                                       │
│                                                              │
│ Basic:                                                       │
│   Method: [POST ▼]                                           │
│   Path: /users                                               │
│                                                              │
│ Body Data: (select "Body Data" tab)                          │
│   ┌─────────────────────────────────────────────────────┐   │
│   │ {                                                    │   │
│   │   "name": "${__RandomString(10,abcdef)}",           │   │
│   │   "email": "user_${__time()}@test.com",             │   │
│   │   "role": "user"                                    │   │
│   │ }                                                    │   │
│   └─────────────────────────────────────────────────────┘   │
│                                                              │
│ Content-Type: Set in Header Manager (application/json)       │
└─────────────────────────────────────────────────────────────┘
```

### PUT/PATCH Requests

```
PUT Request Configuration:
┌─────────────────────────────────────────────────────────────┐
│ Name: PUT Update User                                        │
│                                                              │
│ Method: [PUT ▼]                                              │
│ Path: /users/${userId}   ← Uses extracted variable           │
│                                                              │
│ Body Data:                                                   │
│   {                                                          │
│     "name": "Updated Name",                                  │
│     "email": "updated@test.com"                              │
│   }                                                          │
└─────────────────────────────────────────────────────────────┘
```

### DELETE Request

```
DELETE Request Configuration:
┌─────────────────────────────────────────────────────────────┐
│ Name: DELETE User                                            │
│                                                              │
│ Method: [DELETE ▼]                                           │
│ Path: /users/${userId}                                       │
│                                                              │
│ Body Data: (usually empty for DELETE)                        │
└─────────────────────────────────────────────────────────────┘
```

## Adding Listeners

### View Results Tree

```
Right-click Thread Group → Add → Listener → View Results Tree

View Results Tree:
┌─────────────────────────────────────────────────────────────┐
│ Sample Results:                                              │
│ ┌───────────────────────────────────────────────────────┐   │
│ │ ✓ GET Users List (245ms)                               │   │
│ │ ✓ POST Create User (523ms)                             │   │
│ │ ✗ PUT Update User (error)                              │   │
│ │ ✓ DELETE User (189ms)                                  │   │
│ └───────────────────────────────────────────────────────┘   │
│                                                              │
│ Tabs: [Sampler result] [Request] [Response data]            │
│                                                              │
│ Response Data:                                               │
│ ┌───────────────────────────────────────────────────────┐   │
│ │ {                                                      │   │
│ │   "id": 123,                                          │   │
│ │   "name": "Test User",                                │   │
│ │   "email": "test@example.com"                         │   │
│ │ }                                                      │   │
│ └───────────────────────────────────────────────────────┘   │
│                                                              │
│ ⚠ Warning: Disable during load tests (resource intensive)   │
└─────────────────────────────────────────────────────────────┘
```

### Summary Report

```
Right-click Thread Group → Add → Listener → Summary Report

Summary Report Table:
┌─────────────────────────────────────────────────────────────────────────┐
│ Label          │ #Samples │ Average │ Min  │ Max   │ Std.Dev │ Error% │
├────────────────┼──────────┼─────────┼──────┼───────┼─────────┼────────┤
│ GET Users List │ 1000     │ 245     │ 89   │ 1234  │ 156     │ 0.10%  │
│ POST Create    │ 1000     │ 523     │ 234  │ 2341  │ 312     │ 0.20%  │
│ PUT Update     │ 1000     │ 312     │ 145  │ 1567  │ 198     │ 0.30%  │
│ DELETE User    │ 1000     │ 189     │ 67   │ 987   │ 145     │ 0.00%  │
├────────────────┼──────────┼─────────┼──────┼───────┼─────────┼────────┤
│ TOTAL          │ 4000     │ 317     │ 67   │ 2341  │ 203     │ 0.15%  │
└─────────────────────────────────────────────────────────────────────────┘

Additional Metrics: Throughput, KB/sec, Avg. Bytes
└─────────────────────────────────────────────────────────────────────────┘
```

### Aggregate Report

```
Right-click Thread Group → Add → Listener → Aggregate Report

Aggregate Report (includes percentiles):
┌─────────────────────────────────────────────────────────────────────────────┐
│ Label      │ #Samples │ Avg │ Median │ 90% │ 95% │ 99% │ Min │ Max │ Error% │
├────────────┼──────────┼─────┼────────┼─────┼─────┼─────┼─────┼─────┼────────┤
│ GET Users  │ 1000     │ 245 │ 198    │ 412 │ 567 │ 890 │ 89  │ 1234│ 0.10%  │
│ POST User  │ 1000     │ 523 │ 456    │ 823 │ 1023│ 1567│ 234 │ 2341│ 0.20%  │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Response Times Over Time (Plugin)

```
Graph Features:
├── X-axis: Time (elapsed during test)
├── Y-axis: Response time (ms)
├── Shows trend over test duration
├── Identifies degradation points
└── Multiple samplers as different colors

Visual representation helps identify:
├── When performance degraded
├── Response time patterns
├── Correlation with load increase
└── Stabilization points
```

## Adding Assertions

### Response Assertion

```
Right-click HTTP Request → Add → Assertions → Response Assertion

Response Assertion Configuration:
┌─────────────────────────────────────────────────────────────┐
│ Apply to: (•) Main sample only                               │
│                                                              │
│ Response Field to Test:                                      │
│   (•) Response Code                                          │
│   ( ) Response Message                                       │
│   ( ) Response Headers                                       │
│   ( ) Text Response                                          │
│                                                              │
│ Pattern Matching Rules:                                      │
│   (•) Contains  ( ) Matches  ( ) Equals                      │
│   ( ) Substring ( ) NOT                                      │
│                                                              │
│ Patterns to Test:                                            │
│   ┌──────────────────────────────────────────────────────┐  │
│   │ 200                                                   │  │
│   └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### JSON Assertion

```
Right-click HTTP Request → Add → Assertions → JSON Assertion

JSON Assertion:
┌─────────────────────────────────────────────────────────────┐
│ Assert JSON Path exists: $.id                                │
│                                                              │
│ Additionally assert value:                                   │
│   [✓] Enable                                                 │
│   Expected Value: [not empty]                                │
│   Match as Regular Expression: [ ]                           │
│   Expect Null: [ ]                                           │
│   Invert Assertion: [ ]                                      │
└─────────────────────────────────────────────────────────────┘
```

### Duration Assertion

```
Right-click HTTP Request → Add → Assertions → Duration Assertion

Duration Assertion:
┌─────────────────────────────────────────────────────────────┐
│ Duration in milliseconds: [2000]                             │
│                                                              │
│ Fails if response time exceeds 2000ms                        │
└─────────────────────────────────────────────────────────────┘
```

## Adding Timers

### Constant Timer

```
Right-click HTTP Request → Add → Timer → Constant Timer

Constant Timer:
┌─────────────────────────────────────────────────────────────┐
│ Thread Delay (in milliseconds): [1000]                       │
│                                                              │
│ Adds 1 second delay before the sampler executes              │
└─────────────────────────────────────────────────────────────┘
```

### Uniform Random Timer

```
Random Timer (more realistic):
┌─────────────────────────────────────────────────────────────┐
│ Random Delay Maximum (ms): [5000]                            │
│ Constant Delay Offset (ms): [1000]                           │
│                                                              │
│ Total delay = Constant + Random(0, Maximum)                  │
│ Result: 1000ms to 6000ms delay (simulates think time)        │
└─────────────────────────────────────────────────────────────┘
```

## Running Tests from GUI

### Starting a Test

```
Steps to Run:
1. Save your test plan (Ctrl+S)
2. Click Start button (green play) or Ctrl+R
3. Watch status bar for progress:
   "Threads: 50/100 | Running: Yes"
4. Monitor listeners for results
5. Click Stop when complete (or let duration end)
```

### During Test Execution

```
Status Bar Information:
├── Threads: Active/Total (e.g., "50/100")
├── Running indicator
├── Active thread groups
└── Error count (if any)

Real-time Monitoring:
├── View Results Tree: See individual requests
├── Summary Report: See aggregate metrics
└── Graph listeners: See trends
```

### After Test Completion

```
Post-Test Actions:
1. Review Summary/Aggregate Report
2. Check for errors in View Results Tree
3. Save results if needed:
   - Right-click listener → Save Table Data
   - Or configure filename in listener
4. Clear results before next run (Clear All)
```

## GUI Best Practices

### Development vs Execution

```
During Development:
✓ Enable View Results Tree
✓ Enable Debug Sampler
✓ Use small thread counts (1-5)
✓ Use short durations
✓ Check individual responses

During Load Testing:
✗ Disable View Results Tree
✗ Remove Debug Sampler
✓ Use actual thread counts
✓ Use actual durations
✓ Use Summary/Aggregate only
✓ Better: Use CLI mode
```

### GUI Limitations

```
GUI Mode Limitations:
├── Consumes significant resources
├── Listeners add overhead
├── Can affect test accuracy
├── May crash with large tests
└── Not suitable for CI/CD

When to Use CLI Instead:
├── Actual load tests
├── More than 100-200 threads
├── CI/CD pipelines
├── Distributed testing
└── Accurate metrics needed
```

### Tips for Efficiency

```
Productivity Tips:
1. Use keyboard shortcuts (Ctrl+R to run)
2. Duplicate elements instead of recreating
3. Use Request Defaults to avoid repetition
4. Save templates for common configurations
5. Use descriptive names for all elements
6. Add comments for complex logic
7. Organize with folders (Simple Controller)
8. Disable unused elements instead of deleting
```

## Complete GUI Test Plan Example

```
Test Plan: E-Commerce API Load Test
├── User Defined Variables
│   ├── BASE_URL = https://api.shop.com
│   └── API_TOKEN = ${__P(token)}
├── HTTP Request Defaults
│   ├── Server: api.shop.com
│   ├── Protocol: https
│   └── Headers: Content-Type, Accept, Authorization
├── Thread Group: Browse Flow (60% of users)
│   ├── HTTP Request: GET /products
│   ├── Uniform Random Timer: 2-5 seconds
│   ├── HTTP Request: GET /products/${productId}
│   ├── JSON Extractor: Extract productId
│   └── Response Assertion: Status 200
├── Thread Group: Purchase Flow (40% of users)
│   ├── HTTP Request: POST /cart
│   ├── Constant Timer: 1 second
│   ├── HTTP Request: POST /checkout
│   └── Duration Assertion: < 3000ms
├── Listener: Summary Report
└── Listener: Aggregate Report (save to file)
```

## Summary

- **JMeter GUI** provides visual test plan creation and debugging
- **Test plans** contain thread groups, samplers, config elements, and listeners
- **Thread groups** configure virtual users, ramp-up, and duration
- **HTTP samplers** support all REST methods with headers, parameters, and bodies
- **Listeners** display results but should be limited during actual load tests
- **GUI mode** is best for development; use **CLI mode** for actual load testing

In the next lesson, you'll learn to run JMeter from the command line for production load testing and CI/CD integration.

## Additional Resources

- [JMeter GUI Documentation](https://jmeter.apache.org/usermanual/get-started.html) - Getting started guide
- [JMeter Component Reference](https://jmeter.apache.org/usermanual/component_reference.html) - All components explained
- [JMeter Best Practices](https://jmeter.apache.org/usermanual/best-practices.html) - Official recommendations

