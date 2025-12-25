# Bottleneck Identification

## Learning Objectives
- Understand the process of identifying performance bottlenecks
- Learn to correlate multiple graphs to find root causes
- Use web page diagnostics for detailed analysis
- Perform transaction breakdown analysis
- Correlate application metrics with server resources
- Recognize common bottleneck patterns and their solutions

## Why This Matters

Finding a performance problem is only half the battle. Identifying its root cause enables targeted fixes. Without bottleneck identification, teams waste time optimizing the wrong components or apply generic fixes that don't address the real issue.

In **Mastering Enterprise Performance Testing with LoadRunner**, bottleneck identification transforms you from someone who runs tests to someone who solves performance problems, a critical skill for any quality engineer.

## What is a Bottleneck?

A **bottleneck** is a component that limits overall system performance. Like a narrow section of pipe that restricts water flow, a bottleneck prevents the system from achieving higher throughput or faster response times.

```
System Without Bottleneck:           System With Bottleneck:
─────────────────────────────        ─────────────────────────────

Request ─────▶ Web ─────▶ App        Request ─────▶ Web ─────▶ App
              Server     Server                   Server     Server
                │                                    │
                ▼                                    ▼
             Database                            Database ← BOTTLENECK
                │                                    │
                ▼                                    │ Requests queue
             Response                                ▼
             (1 second)                           Response
                                                  (5 seconds)
```

### Types of Bottlenecks

| Type | Description | Common Causes |
|------|-------------|---------------|
| **CPU** | Processor cannot keep up | Complex calculations, poor algorithms |
| **Memory** | Insufficient RAM | Memory leaks, large data sets |
| **Disk I/O** | Storage too slow | Heavy logging, poor queries |
| **Network** | Bandwidth or latency limits | Large payloads, remote services |
| **Database** | Query performance issues | Missing indexes, locks |
| **Application** | Code inefficiencies | Synchronization, poor design |
| **Connection Pool** | Exhausted connections | Too few connections, leaks |

## Correlating Multiple Graphs

Single graphs show symptoms; correlated graphs reveal causes.

### Correlation Technique

```
Step 1: Identify the Symptom
────────────────────────────

Response Time Graph:
     ▲
  10s│                    ╱╱
   5s│              ╱╱╱╱╱╱
   2s│──────────────╱
     └────────────────────────────────▶ Time
               ↑
         Problem starts at 10:30

Step 2: Find Correlated Events
──────────────────────────────

Running VUsers (same time):
     ▲
 500│            ────────────────────
 300│       ────╱
 100│──────╱
     └────────────────────────────────▶ Time
               ↑
         300 VUsers at 10:30

Database Connections (same time):
     ▲
 100│                ────────────────  ← Pool exhausted
  75│           ────╱
  50│      ────╱
  25│─────╱
     └────────────────────────────────▶ Time
                ↑
          Pool full at 10:30

Conclusion: Connection pool bottleneck at 300 VUsers
```

### Creating Correlation Views

```
Analysis Tool: Overlay Graphs
─────────────────────────────

Graph → Merge Graphs

Select graphs to overlay:
[✓] Transaction Response Time
[✓] Running VUsers
[✓] Database Connections
[ ] Throughput

Alignment: By time
Scale: Normalize to percentage

Result: Combined view showing relationships
```

## Web Page Diagnostics

For web applications, page diagnostics reveal detailed timing breakdown.

### Page Component Analysis

```
Web Page Diagnostics View:
──────────────────────────

Page: Checkout Confirmation
Total Time: 4.2 seconds

Component Breakdown:
┌────────────────────────────────────────────────────────────────┐
│ Component          │ Time    │ Size    │ %     │ Bar           │
├────────────────────┼─────────┼─────────┼───────┼───────────────┤
│ checkout.html      │ 2.1s    │ 45 KB   │ 50%   │ ██████████    │
│ order-confirm.js   │ 0.8s    │ 120 KB  │ 19%   │ ████          │
│ payment-api call   │ 0.6s    │ 2 KB    │ 14%   │ ███           │
│ styles.css         │ 0.3s    │ 85 KB   │ 7%    │ █             │
│ logo.png           │ 0.2s    │ 25 KB   │ 5%    │ █             │
│ Other (12 items)   │ 0.2s    │ 50 KB   │ 5%    │ █             │
└────────────────────┴─────────┴─────────┴───────┴───────────────┘

Insight: checkout.html takes 50% of time → server-side bottleneck
```

### Time to First Byte (TTFB)

```
TTFB Analysis:
──────────────

TTFB = Time from request sent to first byte received

Request Timeline:
─────────────────

0ms        100ms       500ms      2100ms     2500ms
│           │           │           │           │
│←─ DNS ───▶│           │           │           │
│           │← Connect ▶│           │           │
│           │           │← TTFB ───▶│           │ ← Server processing
│           │           │           │← Content ▶│

High TTFB Causes:
├── Database queries slow
├── Application processing slow
├── Server overloaded
└── Backend service delays

Low TTFB but slow page:
├── Large content to download
├── Slow network connection
└── Too many resources
```

## Transaction Breakdown

Drill into individual transactions to find slow components.

### Transaction Time Distribution

```
Transaction: Complete_Purchase
Total Time: 5.2 seconds

Time Breakdown:
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                         │
│   Client Time    │    Network Time    │    Server Time                  │
│      (0.3s)      │       (0.4s)       │       (4.5s)                    │
│        6%        │         8%         │         86%                     │
│                                                                         │
│   ┌──────────────┼────────────────────┼────────────────────────────────┐│
│   │██            │████                │████████████████████████████████││
│   └──────────────┼────────────────────┼────────────────────────────────┘│
│                                                                         │
│   Bottleneck Location: Server                                           │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘

Server Time Breakdown:
├── API processing: 0.5s
├── Database queries: 3.2s  ← Primary bottleneck
├── Payment gateway: 0.6s
└── Response formatting: 0.2s
```

### Step-by-Step Analysis

```
Transaction Steps:
──────────────────

Step                        Time      Status   Notes
────────────────────────────────────────────────────────────
1. Load checkout page       0.3s      ✓
2. Submit order             0.2s      ✓
3. Validate inventory       0.8s      ⚠️       Slightly slow
4. Process payment          0.6s      ✓
5. Update database          2.8s      ❌       BOTTLENECK
6. Send confirmation        0.2s      ✓
7. Generate receipt         0.3s      ✓
────────────────────────────────────────────────────────────
Total                       5.2s

Investigation Focus: Step 5 - Database update
├── Query: INSERT INTO orders + UPDATE inventory
├── Possible issues: Missing index, table locks, connection wait
└── Action: Review query execution plan, check for locks
```

## Server Resource Correlation

Correlate application metrics with infrastructure metrics.

### CPU Correlation

```
Response Time vs. CPU Usage:
────────────────────────────

Response                                 CPU %
Time (s)                                 
    ▲                                      ▲
  5s│            ╱╱                    100│         ────────────
    │          ╱╱                         │        ╱
  3s│        ╱╱                        75│      ╱╱
    │      ╱╱  Response Time              │    ╱╱
  2s│    ╱╱                            50│  ╱╱    CPU Usage
    │  ╱╱                                 │╱╱
  1s│╱╱────────────────                25│────
    └──────────────────────▶              └──────────────────────▶
           Time                                  Time

Pattern: CPU-bound bottleneck
- Response time rises with CPU usage
- CPU reaches 100% and stays there
- System cannot process more requests
```

### Memory Correlation

```
Response Time vs. Memory:
─────────────────────────

Response                                 Memory
Time (s)                                 Usage
    ▲                                      ▲
 20s│                    ╱             100%│                 ╱───
    │                   ╱                  │                ╱
 10s│              ╱╱╱╱╱               75%│            ╱╱╱╱
    │         ╱╱╱╱╱                        │        ╱╱╱╱
  5s│    ╱╱╱╱╱                         50%│    ╱╱╱╱
    │╱╱╱╱                                  │╱╱╱╱
  2s│───────────                       25%│───────
    └────────────────────────▶             └────────────────────────▶
           Time (hours)                          Time (hours)

Pattern: Memory leak
- Memory grows continuously
- Response time degrades over time
- Eventually leads to OutOfMemory errors
```

### Database Wait Time

```
Response Time vs. DB Waits:
───────────────────────────

Response      │    Active     │    Lock
Time          │    Queries    │    Waits
──────────────┼───────────────┼────────────────
    ▲         │       ▲       │       ▲
  8s│     ╱╱  │   100│   ╱╱   │   50%│     ╱╱
  4s│   ╱╱    │    50│ ╱╱     │   25%│   ╱╱
  2s│──╱      │    25│╱       │    5%│──╱
    └─────▶   │      └─────▶  │      └─────▶
     Time     │      Time     │      Time

Correlation: Response time follows DB metrics
- Active queries increasing → more concurrent requests
- Lock waits increasing → contention on tables
- Both indicate database bottleneck
```

## Common Bottleneck Patterns and Solutions

### Pattern 1: Connection Pool Exhaustion

```
Symptoms:
─────────
- Response time suddenly spikes
- Errors: "Cannot acquire connection"
- Database shows few active queries

Graph Pattern:
     ▲
Resp │           ╱────────────────
Time │          ╱
     │─────────╱
     └─────────────────────────────▶

     ▲
Conn │         ──────────────────── (at max)
Pool │        ╱
     │───────╱
     └─────────────────────────────▶

Solution:
✓ Increase connection pool size
✓ Reduce connection hold time
✓ Fix connection leaks in code
✓ Implement connection timeouts
```

### Pattern 2: Database Locks

```
Symptoms:
─────────
- Sporadic slow transactions
- Some users fast, some very slow
- Database shows lock waits

Graph Pattern:
     ▲
Resp │    ╱╲      ╱╲    ╱╲
Time │   ╱  ╲    ╱  ╲  ╱  ╲   Intermittent spikes
     │──╱────╲──╱────╲╱────╲──
     └─────────────────────────────▶

     ▲
Lock │  █   █     █  ██
Wait │  █ █ █ █   █  ██
     │──█─█─█─█───█──██───────────
     └─────────────────────────────▶

Solution:
✓ Optimize queries to reduce lock time
✓ Use appropriate isolation levels
✓ Implement optimistic locking
✓ Redesign to avoid hot spots
```

### Pattern 3: Garbage Collection

```
Symptoms:
─────────
- Periodic response time spikes
- Regular pattern (every few minutes)
- CPU spikes during slow periods

Graph Pattern:
     ▲
Resp │  ╱╲     ╱╲     ╱╲     GC pauses
Time │ ╱  ╲   ╱  ╲   ╱  ╲
     │╱    ╲─╱    ╲─╱    ╲───
     └─────────────────────────────▶
       ↑      ↑      ↑
       GC     GC     GC

     ▲
Heap │╱╲   ╱╲   ╱╲   ╱╲  Sawtooth pattern
Mem  │  ╲ ╱  ╲ ╱  ╲ ╱  ╲
     │   ╲╱   ╲╯   ╲╯   
     └─────────────────────────────▶

Solution:
✓ Tune JVM heap size
✓ Adjust GC algorithm
✓ Reduce object creation
✓ Fix memory leaks
```

### Pattern 4: Network Latency

```
Symptoms:
─────────
- High TTFB for all requests
- Response time consistent but slow
- Server CPU/Memory low

Graph Pattern:
     ▲
TTFB │────────────────────────  Consistently high
     │
     └─────────────────────────────▶

     ▲
CPU  │──────────────────────────  Low usage
     │
     └─────────────────────────────▶

Solution:
✓ Move load generators closer to app
✓ Implement CDN for static content
✓ Optimize payload sizes
✓ Enable compression
```

## Investigation Workflow

```
Bottleneck Investigation Process:
─────────────────────────────────

┌─────────────────────────────────────────────────────────────────┐
│ 1. IDENTIFY SYMPTOM                                             │
│    "Response time increased from 2s to 8s at 300 VUsers"        │
└───────────────────────────────┬─────────────────────────────────┘
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│ 2. DETERMINE SCOPE                                              │
│    - All transactions or specific ones?                         │
│    - All users or percentage?                                   │
│    - Consistent or intermittent?                                │
└───────────────────────────────┬─────────────────────────────────┘
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│ 3. CORRELATE METRICS                                            │
│    - What changed at the same time?                             │
│    - CPU, Memory, Network, DB metrics?                          │
│    - Error rates, connection counts?                            │
└───────────────────────────────┬─────────────────────────────────┘
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│ 4. NARROW DOWN                                                  │
│    - Transaction breakdown                                      │
│    - Page diagnostics                                           │
│    - Server-specific metrics                                    │
└───────────────────────────────┬─────────────────────────────────┘
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│ 5. DOCUMENT & RECOMMEND                                         │
│    - Root cause identified                                      │
│    - Evidence supporting conclusion                             │
│    - Recommended solution                                       │
└─────────────────────────────────────────────────────────────────┘
```

## Summary

- **Bottlenecks** are components limiting overall system performance
- **Correlating multiple graphs** reveals relationships between symptoms and causes
- **Web page diagnostics** show component-level timing for web applications
- **Transaction breakdown** identifies which step in a transaction is slow
- **Server resource correlation** connects application behavior to infrastructure
- Common patterns include **connection pool exhaustion**, **database locks**, **GC pauses**, and **network latency**
- Follow a **systematic investigation workflow** to efficiently identify root causes

## Additional Resources

- [Bottleneck Analysis Guide - Micro Focus](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Analysis/c_bottleneck_analysis.htm)
- [Web Page Diagnostics](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Analysis/c_web_page_diagnostics.htm)
- [Performance Anti-Patterns](https://docs.microsoft.com/en-us/azure/architecture/antipatterns/)

