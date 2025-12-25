# Virtual User Configuration

## Learning Objectives
- Configure runtime settings for virtual users
- Understand and configure think time settings
- Master pacing and iteration configuration
- Configure logging options for debugging and analysis
- Implement network virtualization for realistic testing
- Configure browser emulation settings

## Why This Matters

A script that runs successfully with one user may behave differently under load. Runtime settings determine how each virtual user behaves during test execution. Proper configuration ensures your test accurately simulates real user behavior, captures necessary data for analysis, and uses system resources efficiently.

As part of **Mastering Enterprise Performance Testing with LoadRunner**, understanding virtual user configuration allows you to create tests that reveal actual production behavior rather than artificial scenarios that miss real-world issues.

## Runtime Settings Overview

Runtime settings control how virtual users execute during load tests. Access these settings in VuGen via **Replay > Runtime Settings** or in Controller per group.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      Runtime Settings Categories                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐   │
│   │   General   │   │  Run Logic  │   │    Pacing   │   │    Log      │   │
│   │             │   │             │   │             │   │             │   │
│   │ • Think time│   │ • Actions   │   │ • Iteration │   │ • Level     │   │
│   │ • Continue  │   │ • Iterations│   │   delay     │   │ • Options   │   │
│   │   on error  │   │             │   │             │   │             │   │
│   └─────────────┘   └─────────────┘   └─────────────┘   └─────────────┘   │
│                                                                             │
│   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐   │
│   │   Network   │   │   Browser   │   │ Preferences │   │   Content   │   │
│   │             │   │             │   │             │   │   Check     │   │
│   │ • Speed     │   │ • Type      │   │ • Startup   │   │             │   │
│   │ • Bandwidth │   │ • Cache     │   │ • Cleanup   │   │ • Text      │   │
│   │             │   │ • Cookies   │   │             │   │ • Image     │   │
│   └─────────────┘   └─────────────┘   └─────────────┘   └─────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Think Time Configuration

**Think time** simulates the pause between user actions when real users read, think, or interact with content.

### Why Think Time Matters

```
Without Think Time:                    With Think Time:
─────────────────────                  ─────────────────────
VUser → Request → Response             VUser → Request → Response
      → Request → Response                   → (3 sec pause)
      → Request → Response                   → Request → Response
      → Request → Response                   → (5 sec pause)
                                             → Request → Response

Result: Unrealistic load              Result: Realistic simulation
        Machine-gun requests                  of actual user behavior
        Server overwhelmed                    True capacity measured
```

### Think Time Options

| Option | Description | Use Case |
|--------|-------------|----------|
| **As recorded** | Use exact times captured during recording | Most realistic |
| **Multiply by X** | Scale recorded times (0.5 = faster, 2.0 = slower) | Adjust for load level |
| **Limit to X seconds** | Cap maximum think time | Prevent excessive delays |
| **Random percentage** | Add ±X% variance to recorded times | Realistic variation |
| **Ignore** | Remove all think time | Stress testing, max throughput |

### Configuration Example

```
Think Time Settings:
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   (•) Replay think time                                         │
│       [✓] As recorded                                           │
│       [ ] Multiply recorded think time by: [1.0]                │
│       [✓] Limit think time to: [30] seconds                     │
│       [✓] Add random percentage: [25] %                         │
│                                                                 │
│   ( ) Ignore think time                                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

Effect: If recorded think time was 10 seconds:
- With 25% random: actual time will be 7.5-12.5 seconds
- Capped at 30 seconds maximum
```

## Pacing Configuration

**Pacing** controls the timing between iteration starts, ensuring consistent load generation.

### Pacing vs. Think Time

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    Pacing vs. Think Time                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   Think Time: Pauses WITHIN an iteration (between actions)                  │
│   ┌──────────────────────────────────────────────────────────────────────┐ │
│   │ Login ──(3s)──▶ Search ──(5s)──▶ View ──(2s)──▶ Add Cart             │ │
│   └──────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
│   Pacing: Controls time BETWEEN iterations                                  │
│   ┌─────────────────┐         ┌─────────────────┐                          │
│   │   Iteration 1   │──(Xsec)─▶│   Iteration 2   │──(Xsec)─▶ ...          │
│   └─────────────────┘         └─────────────────┘                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Pacing Options

| Option | Behavior | Use Case |
|--------|----------|----------|
| **As soon as previous ends** | Start immediately after iteration completes | Maximum throughput |
| **After iteration** | Wait fixed time after iteration ends | Consistent spacing |
| **At fixed intervals** | Start at regular intervals regardless of duration | Predictable TPS |
| **Random intervals** | Random delay between min and max | Realistic variation |

### Fixed Interval Pacing

This is the most controlled option for predictable load:

```
Pacing: Fixed Interval (60 seconds)
───────────────────────────────────

Timeline:
0s         60s        120s       180s
│──────────│──────────│──────────│
│ Iter 1   │ Iter 2   │ Iter 3   │
│ (45s)    │ (52s)    │ (48s)    │
│    ↓wait │    ↓wait │    ↓wait │
│    15s   │    8s    │    12s   │

If Iteration takes LONGER than interval:
0s         60s        90s        120s
│──────────│──────────│──────────│
│ Iteration 1 (takes 90s)        │ Iter 2 starts immediately
                                   at 90s (missed target)

Warning: Log shows "pacing violation" when iterations exceed interval
```

### Pacing Configuration Dialog

```
Pacing Settings:
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   Start new iteration:                                          │
│                                                                 │
│   ( ) As soon as the previous iteration ends                    │
│                                                                 │
│   ( ) After the previous iteration ends, wait:                  │
│       Fixed: [30] seconds                                       │
│       OR Random: [20] to [40] seconds                           │
│                                                                 │
│   (•) At fixed intervals, every [60] seconds                    │
│       OR Random: [45] to [75] seconds                           │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Iteration Configuration

**Iterations** control how many times each virtual user repeats the Action section.

### Run Logic Settings

```
Run Logic:
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   Number of Iterations: [10]                                    │
│                                                                 │
│   Action Blocks to Run:                                         │
│   ┌─────────────────────────────────────────────────────────┐  │
│   │ [✓] vuser_init     (Run once at start)                  │  │
│   │ [✓] Action         (Run for each iteration)             │  │
│   │ [✓] Action2        (Run for each iteration)             │  │
│   │ [ ] Action3        (Disabled)                           │  │
│   │ [✓] vuser_end      (Run once at end)                    │  │
│   └─────────────────────────────────────────────────────────┘  │
│                                                                 │
│   Action Block Sequence:                                        │
│   vuser_init → (Action → Action2) × 10 → vuser_end             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Iteration Strategies

| Strategy | Configuration | Use Case |
|----------|---------------|----------|
| **Fixed count** | Set specific iteration number | Predictable data consumption |
| **Time-based** | Run until scenario ends | Match test duration |
| **Data-driven** | Run until parameter data exhausted | Unique data per iteration |

## Log Settings

Logging captures execution details for debugging and analysis.

### Log Levels

| Level | Data Captured | Use Case |
|-------|---------------|----------|
| **Disabled** | No logging | Production runs, maximum performance |
| **Standard** | Basic function calls and errors | Normal testing |
| **Extended** | Standard + parameters and data | Debugging issues |
| **Full Trace** | Everything including network data | Deep troubleshooting |

### Log Configuration

```
Log Settings:
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   Enable logging: [✓]                                           │
│                                                                 │
│   Log Level:                                                    │
│   ( ) Standard log                                              │
│   (•) Extended log                                              │
│                                                                 │
│   Extended log options:                                         │
│   [✓] Parameter substitution                                    │
│   [✓] Data returned by server                                   │
│   [ ] Advanced trace (network level)                            │
│                                                                 │
│   Send messages:                                                │
│   (•) Always                                                    │
│   ( ) Only when error occurs                                    │
│                                                                 │
│   Log file size limit: [10] MB per VUser                        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Log Impact on Performance

```
Log Level Impact:
─────────────────

Disabled    ████████████████████████████████  100% throughput
Standard    ████████████████████████████      90% throughput  
Extended    ████████████████████              80% throughput
Full Trace  ████████████                      60% throughput

Recommendation:
- Development/Debug: Extended log
- Load test: Standard or Disabled
- Production verification: Disabled
```

## Network Virtualization

Network virtualization simulates various network conditions that real users experience.

### Why Simulate Network Conditions?

```
Your Test Environment:              Real Users Experience:
────────────────────────            ────────────────────────
LoadRunner ──── LAN ────▶ App      User ──── 4G ────▶ App (high latency)
             (1 Gbps)                      (5-50 Mbps, variable)
             (<1ms latency)        
                                   User ──── DSL ────▶ App (moderate)
Perfect conditions don't                   (10-50 Mbps)
reveal real-world issues
                                   User ──── Satellite ──▶ App (high latency)
                                           (500+ ms latency)
```

### Network Speed Emulation

```
Network Speed Settings:
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   Network Speed:                                                │
│   ( ) Use maximum bandwidth (no limit)                          │
│   (•) Use bandwidth: [Custom ▼]                                 │
│                                                                 │
│   Predefined profiles:                                          │
│   ┌────────────────────────────────────────────────────────┐   │
│   │ Profile          Download      Upload       Latency    │   │
│   │ ──────────────────────────────────────────────────────│   │
│   │ 4G LTE           12 Mbps       5 Mbps       50 ms      │   │
│   │ 3G               1.5 Mbps      384 Kbps     100 ms     │   │
│   │ DSL              8 Mbps        1 Mbps       30 ms      │   │
│   │ Cable            25 Mbps       5 Mbps       20 ms      │   │
│   │ Fiber            100 Mbps      100 Mbps     5 ms       │   │
│   │ Custom...                                              │   │
│   └────────────────────────────────────────────────────────┘   │
│                                                                 │
│   Custom settings:                                              │
│   Bandwidth: [10] Mbps                                          │
│   Latency: [50] ms                                              │
│   Packet loss: [0.5] %                                          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Browser Emulation Settings

Browser emulation controls how VUsers simulate browser behavior.

### Browser Type

```
Browser Emulation:
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   Simulate browser:                                             │
│   ┌─────────────────────────────────────────────────────────┐  │
│   │ Browser Type                User-Agent String           │  │
│   │ ─────────────────────────────────────────────────────── │  │
│   │ Chrome 118 (Windows)        Mozilla/5.0...Chrome/118... │  │
│   │ Firefox 119 (Windows)       Mozilla/5.0...Firefox/119   │  │
│   │ Safari 17 (macOS)           Mozilla/5.0...Safari/605... │  │
│   │ Edge 118 (Windows)          Mozilla/5.0...Edg/118...    │  │
│   │ Custom...                                               │  │
│   └─────────────────────────────────────────────────────────┘  │
│                                                                 │
│   [✓] Download non-HTML resources (images, CSS, JS)             │
│   [✓] Simulate browser cache                                    │
│   [✓] Cache URLs requiring content (HTML)                       │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Cache Simulation

| Setting | Behavior | Effect on Test |
|---------|----------|----------------|
| **Cache enabled** | Resources cached like real browser | Lower load, realistic |
| **Clear each iteration** | Fresh cache per iteration | Higher load, worst-case |
| **Clear each VUser** | Fresh cache per VUser | Moderate load |

### Download Resources

```
Resource Download Options:
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   [✓] Download non-HTML resources                               │
│       [✓] Images                                                │
│       [✓] CSS                                                   │
│       [✓] JavaScript                                            │
│       [✓] Fonts                                                 │
│                                                                 │
│   Resource filtering:                                           │
│   [ ] Only from same domain                                     │
│   [✓] Exclude URLs matching: [*.analytics.*, *.ads.*]           │
│                                                                 │
│   Concurrent connections: [6] (browser default)                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Error Handling Configuration

Control how VUsers respond to errors during execution.

### Continue on Error Settings

```
Error Handling:
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   On error:                                                     │
│   ( ) Stop virtual user immediately                             │
│   (•) Continue to next action                                   │
│   ( ) Continue to next iteration                                │
│                                                                 │
│   Error types to handle:                                        │
│   [✓] HTTP errors (4xx, 5xx)                                    │
│   [✓] Content check failures                                    │
│   [✓] Correlation failures                                      │
│   [ ] Transaction failures                                      │
│                                                                 │
│   Fail transaction on:                                          │
│   [✓] HTTP errors                                               │
│   [✓] Content verification failure                              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Best Practices Summary

| Setting | Development | Load Testing | Stress Testing |
|---------|-------------|--------------|----------------|
| **Think Time** | As recorded | As recorded with variance | Ignore |
| **Pacing** | As soon as ends | Fixed intervals | As soon as ends |
| **Logging** | Extended | Standard | Disabled |
| **Network** | No limit | Simulate real conditions | No limit |
| **Cache** | Clear each iteration | Simulate browser | Clear each iteration |
| **Iterations** | 1-3 | Time-based | Until failure |

## Summary

- **Think time** simulates real user pauses; use realistic values with random variance
- **Pacing** controls iteration timing; fixed intervals provide predictable TPS
- **Iterations** determine how many times Actions repeat per VUser
- **Log settings** balance debugging needs with performance impact
- **Network virtualization** reveals performance under real-world network conditions
- **Browser emulation** simulates actual browser behavior including caching
- Configuration choices significantly impact test realism and results validity

## Additional Resources

- [Runtime Settings Guide - Micro Focus](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/VuGen/c_runtime_settings.htm)
- [Think Time Best Practices](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/VuGen/c_think_time.htm)
- [Network Virtualization Documentation](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/VuGen/c_network_virtualization.htm)

