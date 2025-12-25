# Load Generation

## Learning Objectives
- Understand load generation strategies and patterns
- Design effective ramp-up and ramp-down sequences
- Configure steady state for meaningful measurements
- Choose between duration-based and iteration-based execution
- Configure load generators for distributed testing
- Understand cloud burst testing concepts

## Why This Matters

The way you apply load to an application determines what your test reveals. A sudden spike of 1,000 users behaves differently than a gradual ramp to the same number. Understanding load generation strategies helps you design tests that answer specific questions: "Can we handle Black Friday traffic?" or "What happens if our marketing campaign goes viral?"

As you continue **Mastering Enterprise Performance Testing with LoadRunner**, load generation knowledge enables you to create realistic scenarios that predict real-world application behavior and reveal genuine performance characteristics.

## Load Generation Strategies

Different testing goals require different load patterns.

### Strategy Overview

| Strategy | Pattern | Purpose |
|----------|---------|---------|
| **Ramp-up** | Gradual increase | Identify capacity limits, warm-up |
| **Steady State** | Constant load | Measure stable performance |
| **Spike** | Sudden surge | Test system resilience |
| **Step** | Incremental levels | Find precise breaking points |
| **Endurance** | Extended duration | Detect memory leaks, degradation |

### Visual Comparison

```
Ramp-up/Steady/Ramp-down          Spike Test
─────────────────────────         ─────────────────────────
VUsers                            VUsers
  ▲     ┌───────────┐              ▲        ┌────┐
  │    /│           │\             │        │    │
  │   / │   Steady  │ \            │        │    │
  │  /  │   State   │  \           │   ─────┘    └─────
  │ /   │           │   \          │
  │/    │           │    \         │
  └─────┴───────────┴─────▶        └─────────────────────▶
        Time                              Time


Step Load                         Endurance Test
─────────────────────────         ─────────────────────────
VUsers                            VUsers
  ▲           ┌───────             ▲  ┌───────────────────────
  │       ┌───┘                    │  │
  │   ┌───┘                        │  │   Extended Duration
  │ ──┘                            │  │   (hours/days)
  │                                │  │
  └─────────────────────▶          └──┴───────────────────────▶
        Time                              Time
```

## Ramp-Up Patterns

**Ramp-up** gradually increases load, allowing you to observe system behavior at each load level.

### Linear Ramp-Up

The most common pattern: add VUsers at a constant rate.

```
Linear Ramp-Up Configuration:
─────────────────────────────
Start: 0 VUsers
End: 500 VUsers
Duration: 10 minutes

Rate: 500 / 10 = 50 VUsers per minute
      = approximately 1 VUser every 1.2 seconds

Timeline:
  0 min  ──────▶  5 min  ──────▶  10 min
  0 VU            250 VU          500 VU
```

### Step Ramp-Up

Add VUsers in batches, with stabilization periods between steps.

```
Step Ramp-Up:
─────────────
VUsers
  ▲
500│                    ┌───────────
   │               ┌────┘
400│          ┌────┘
   │     ┌────┘
300│┌────┘
   ││
   └┴────┬────┬────┬────┬────┬────▶ Time
    0    5    10   15   20   25 minutes

Add 100 VUsers every 5 minutes
Allows system stabilization between steps
```

### Ramp-Up Best Practices

| Practice | Rationale |
|----------|-----------|
| **Start slowly** | Allow caches to warm, connections to establish |
| **Match production** | Real users don't all arrive simultaneously |
| **Allow stabilization** | Metrics need time to settle after each increase |
| **Monitor continuously** | Watch for early warnings during ramp |

## Ramp-Down Patterns

**Ramp-down** gracefully reduces load, allowing in-flight transactions to complete.

### Gradual Ramp-Down

```
Gradual Ramp-Down:
──────────────────
VUsers
  ▲
500│────────────────────┐
   │                    │\
400│                    │ \
   │                    │  \
300│                    │   \
   │                    │    \
   │                    │     \
   │                    │      \
  0└────────────────────┴───────\──▶ Time
    │← Steady State →│← Ramp →│

Stop 50 VUsers per minute
Allows transactions to complete naturally
```

### Immediate Stop vs. Gradual Stop

| Approach | Behavior | Use Case |
|----------|----------|----------|
| **Immediate** | All VUsers stop instantly | Stress testing, finding limits |
| **Gradual** | VUsers exit over time | Realistic, clean shutdown |
| **Wait for completion** | VUsers finish current iteration | Data integrity |

## Steady State Configuration

**Steady state** is the sustained load period where you collect meaningful performance data.

### Steady State Requirements

```
Test Timeline:
─────────────────────────────────────────────────────────────────────────
                                                                         
 │◀─ Ramp-up ─▶│◀──────────── Steady State ──────────────▶│◀─ Down ─▶│  
 │   (ignored) │    (measurements taken here)              │          │  
                                                                         
VUsers ─────────/‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾\──────────
               │                                            │           
               │  Minimum 30 minutes recommended            │           
               │  Longer for endurance/soak tests           │           
─────────────────────────────────────────────────────────────────────────
```

### Steady State Duration Guidelines

| Test Type | Minimum Duration | Recommended |
|-----------|------------------|-------------|
| **Quick validation** | 15 minutes | 30 minutes |
| **Standard load test** | 30 minutes | 1-2 hours |
| **Capacity test** | 1 hour | 2-4 hours |
| **Endurance test** | 4 hours | 8-24 hours |
| **Soak test** | 24 hours | 48-72 hours |

## Duration-Based vs. Iteration-Based Execution

Two fundamental approaches to controlling test length.

### Duration-Based

VUsers run until a specified time elapses.

```
Duration-Based:
───────────────
Configuration: Run for 60 minutes

VUser behavior:
- Executes iterations continuously
- Stops when 60 minutes elapsed
- Iterations in progress may complete or abort

Timeline:
Start ──────────────────────────────────── 60 min Stop
  │     Iter1 → Iter2 → Iter3 → ... → IterN  │
  │   (as many iterations as time allows)    │
```

### Iteration-Based

VUsers run a fixed number of iterations.

```
Iteration-Based:
────────────────
Configuration: Run 10 iterations per VUser

VUser behavior:
- Completes exactly 10 iterations
- Duration varies based on iteration time
- All VUsers complete the same work

Timeline:
Start ───────────────────────── Variable End
  │   Iter1 → Iter2 → ... → Iter10 │
  │   (exactly 10 iterations)      │
```

### Choosing the Right Approach

| Approach | Best For | Consider When |
|----------|----------|---------------|
| **Duration** | SLA validation, steady state analysis | You need consistent test length |
| **Iteration** | Data-driven tests, exact workload | You have limited test data |
| **Hybrid** | Complex scenarios | Use duration with minimum iterations |

## Load Generator Configuration

Effective load generation requires proper infrastructure configuration.

### Single vs. Multiple Generators

```
Single Generator:                    Multiple Generators:
─────────────────                    ────────────────────
┌────────────────┐                   ┌────────────┐
│ Controller +   │                   │ Controller │
│ Load Generator │                   └─────┬──────┘
│                │                         │
│  ┌──┐ ┌──┐     │                   ┌─────┼─────┬─────────┐
│  │VU│ │VU│ ... │                   │     │     │         │
│  └──┘ └──┘     │                   ▼     ▼     ▼         ▼
└───────┬────────┘               ┌─────┐┌─────┐┌─────┐┌─────┐
        │                        │LG 1 ││LG 2 ││LG 3 ││LG 4 │
        ▼                        │200VU││200VU││200VU││200VU│
    ┌───────┐                    └──┬──┘└──┬──┘└──┬──┘└──┬──┘
    │  App  │                       │      │      │      │
    └───────┘                       └──────┴──────┴──────┘
                                            │
Limit: ~500 VUsers                          ▼
(resource constraints)                  ┌───────┐
                                        │  App  │
                                        └───────┘
                                   
                                   Capacity: 800+ VUsers
                                   (distributed resources)
```

### Generator Capacity Planning

```
Capacity Estimation Formula:
────────────────────────────

Total VUsers Needed: 2,000
VUsers per Generator: 500 (conservative for Web HTTP)
Generators Required: 2,000 / 500 = 4

Add 20% buffer: 4 × 1.2 = 5 generators

Resource Requirements per Generator:
┌────────────────────────────────────────────────┐
│ VUsers     CPU Cores    RAM       Network      │
│ ───────────────────────────────────────────────│
│ 100        2            4 GB      100 Mbps     │
│ 500        4            8 GB      1 Gbps       │
│ 1,000      8            16 GB     1 Gbps       │
│ 2,000      16           32 GB     10 Gbps      │
└────────────────────────────────────────────────┘
```

### Distributed Configuration

```
Distributed Load Generator Setup:
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│   Controller Machine                                                        │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │ LoadRunner Controller                                               │  │
│   │ - Orchestrates all generators                                       │  │
│   │ - Collects results from all sources                                 │  │
│   │ - Provides real-time monitoring                                     │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│   Load Generators:                                                          │
│   ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│   │ LG-01           │  │ LG-02           │  │ LG-03           │            │
│   │ 192.168.1.101   │  │ 192.168.1.102   │  │ 192.168.1.103   │            │
│   │                 │  │                 │  │                 │            │
│   │ Browse: 300 VU  │  │ Search: 200 VU  │  │ Checkout: 100VU │            │
│   │ Search: 100 VU  │  │ Browse: 200 VU  │  │ Browse: 100 VU  │            │
│   └─────────────────┘  └─────────────────┘  └─────────────────┘            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Cloud Burst Testing

Cloud burst testing uses cloud infrastructure for massive scale or geographic distribution.

### Cloud Testing Scenarios

| Scenario | On-Premises | Cloud |
|----------|-------------|-------|
| **Normal testing** | ✓ | Optional |
| **Peak load simulation** | Limited | ✓ Scale on demand |
| **Geographic testing** | Difficult | ✓ Multiple regions |
| **One-time large test** | Expensive | ✓ Pay per use |

### Hybrid Cloud Approach

```
Hybrid Load Generation:
───────────────────────

Normal Operations (On-Premises):
┌─────────────────┐
│ Internal LG     │ ──▶ 500 VUsers (daily testing)
└─────────────────┘

Peak Testing (Cloud Burst):
┌─────────────────┐
│ Internal LG     │ ──▶ 500 VUsers
└─────────────────┘
         +
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│ Cloud LG (US)   │  │ Cloud LG (EU)   │  │ Cloud LG (APAC) │
│ 2,000 VUsers    │  │ 2,000 VUsers    │  │ 2,000 VUsers    │
└─────────────────┘  └─────────────────┘  └─────────────────┘

Total: 6,500 VUsers for Black Friday simulation
```

### Geographic Distribution Benefits

```
Geographic Load Testing:
────────────────────────

US East          US West          Europe           Asia Pacific
┌──────┐         ┌──────┐         ┌──────┐         ┌──────┐
│ LG   │         │ LG   │         │ LG   │         │ LG   │
│ 500VU│         │ 500VU│         │ 500VU│         │ 500VU│
└──┬───┘         └──┬───┘         └──┬───┘         └──┬───┘
   │                │                │                │
   │  (20ms)        │  (80ms)        │  (120ms)       │  (200ms)
   │                │                │                │
   └────────────────┴────────────────┴────────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │ Application │
                    │  (US East)  │
                    └─────────────┘

Benefits:
- Test real network latency from different regions
- Validate CDN and edge caching behavior
- Identify geography-specific issues
```

## Scheduling Advanced Options

### Relative Scheduling

Start groups relative to other groups or scenario start.

```
Relative Scheduling Example:
────────────────────────────

Scenario: E-commerce Load Test

Group Dependencies:
├── Login_Users: Start at scenario start
├── Browse_Users: Start 5 min after Login_Users starts
├── Search_Users: Start 5 min after Browse_Users starts
└── Checkout_Users: Start when Browse_Users reaches 80% capacity

Timeline:
0 min    5 min    10 min   15 min   20 min
│────────│────────│────────│────────│────────▶
│▓▓▓▓▓▓▓▓│▓▓▓▓▓▓▓▓│▓▓▓▓▓▓▓▓│▓▓▓▓▓▓▓▓│ Login
│        │▓▓▓▓▓▓▓▓│▓▓▓▓▓▓▓▓│▓▓▓▓▓▓▓▓│ Browse
│        │        │▓▓▓▓▓▓▓▓│▓▓▓▓▓▓▓▓│ Search
│        │        │   │▓▓▓▓│▓▓▓▓▓▓▓▓│ Checkout
```

### Schedule Actions

| Action | Trigger | Use Case |
|--------|---------|----------|
| **Start VUsers** | Time, dependency | Begin load generation |
| **Stop VUsers** | Time, condition | End load gracefully |
| **Add VUsers** | Manual, threshold | Increase load mid-test |
| **Release VUsers** | Manual | Decrease load mid-test |

## Monitoring During Execution

Real-time monitoring guides load generation decisions.

```
Real-Time Metrics During Load:
──────────────────────────────

┌─────────────────────────────────────────────────────────────────────────┐
│ Running VUsers       ██████████████░░░░░░░░░░░░  450 / 1,000           │
│                                                                         │
│ Transaction Rate     ████████████████████░░░░░░  2,847 trans/sec       │
│                                                                         │
│ Avg Response Time    █████████░░░░░░░░░░░░░░░░░  1.8 seconds           │
│                                                                         │
│ Error Rate           █░░░░░░░░░░░░░░░░░░░░░░░░░  0.02%                 │
│                                                                         │
│ Throughput           ████████████████░░░░░░░░░░  125 MB/sec            │
└─────────────────────────────────────────────────────────────────────────┘

Decision Points:
- Response time trending up? Consider pausing ramp-up
- Error rate increasing? Investigate before adding load
- Throughput plateaued? System may be at capacity
```

## Summary

- **Load generation strategies** vary based on testing goals: ramp-up, steady state, spike, step, endurance
- **Ramp-up patterns** should be gradual to allow system warm-up and realistic simulation
- **Steady state** is where meaningful measurements occur; ensure adequate duration
- **Duration-based execution** provides consistent test length; **iteration-based** ensures consistent workload
- **Distributed load generation** enables massive scale and geographic testing
- **Cloud burst testing** provides elastic capacity for peak simulations
- Proper **load generator configuration** ensures infrastructure can support required VUser counts

## Additional Resources

- [Scenario Scheduling Guide - Micro Focus](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Controller/c_scenario_scheduling.htm)
- [Load Generator Management](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Controller/c_load_generators.htm)
- [LoadRunner Cloud Documentation](https://admhelp.microfocus.com/lrc/)

