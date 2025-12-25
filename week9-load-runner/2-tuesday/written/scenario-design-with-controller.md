# Scenario Design with Controller

## Learning Objectives
- Understand the LoadRunner Controller's role in test orchestration
- Learn to create and configure test scenarios
- Differentiate between manual and goal-oriented scenarios
- Configure virtual user groups effectively
- Assign scripts to groups and manage load generators
- Design effective scenario schedules

## Why This Matters

A VuGen script simulates a single user's journey. But performance testing isn't about single users; it's about hundreds or thousands of users hitting your application simultaneously. The **Controller** transforms individual scripts into coordinated load tests that reveal how your application performs under real-world conditions.

In your journey toward **Mastering Enterprise Performance Testing with LoadRunner**, the Controller is where strategy meets execution. A well-designed scenario tells a story about your users: how many there are, when they arrive, what they do, and how long they stay. This story must match production reality, or your test results become meaningless.

## LoadRunner Controller Overview

The Controller is the **command center** for LoadRunner test execution. It coordinates multiple scripts, virtual users, and load generators into unified test scenarios.

### Controller Responsibilities

| Responsibility | Description |
|----------------|-------------|
| **Scenario Design** | Combine scripts, configure VUser groups, set schedules |
| **Resource Management** | Connect and manage load generators |
| **Execution Control** | Start, stop, pause tests; adjust load in real-time |
| **Real-Time Monitoring** | Watch key metrics during test execution |
| **Result Collection** | Gather data from all load generators for analysis |

### Controller Interface

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        LoadRunner Controller                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │ File  Edit  View  Scenario  Results  Tools  Help                      │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────┐       │
│  │                     Scenario Groups                              │       │
│  │  ┌─────────────────────────────────────────────────────────────┐│       │
│  │  │ Group Name        Script              VUsers    Generator   ││       │
│  │  │ ─────────────────────────────────────────────────────────── ││       │
│  │  │ Browse_Users      Browse_Script       200       localhost   ││       │
│  │  │ Search_Users      Search_Script       150       LoadGen1    ││       │
│  │  │ Checkout_Users    Checkout_Script     50        LoadGen2    ││       │
│  │  └─────────────────────────────────────────────────────────────┘│       │
│  └─────────────────────────────────────────────────────────────────┘       │
│                                                                             │
│  ┌───────────────────────┐  ┌───────────────────────────────────────────┐  │
│  │     Schedule          │  │           Real-Time Graphs               │  │
│  │  ┌─────────────────┐  │  │                                           │  │
│  │  │     /\          │  │  │   Running VUsers  ████████░░░ 320/400    │  │
│  │  │    /  \         │  │  │   Trans/Sec       ████████████ 1,247     │  │
│  │  │   /    \        │  │  │   Response Time   ██████░░░░░░ 2.3s      │  │
│  │  │  /      \       │  │  │   Errors          █░░░░░░░░░░░ 0.02%    │  │
│  │  │ /        \      │  │  │                                           │  │
│  │  └─────────────────┘  │  └───────────────────────────────────────────┘  │
│  │  Ramp-up | Steady     │                                                  │
│  └───────────────────────┘                                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Creating Scenarios

A **scenario** defines your complete load test: which scripts run, how many virtual users, when they start, and how long they run.

### Scenario Creation Workflow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     Scenario Creation Process                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   1. Create New          2. Add Scripts         3. Configure Groups         │
│   Scenario                                                                  │
│   ┌──────────────┐      ┌──────────────┐       ┌──────────────┐            │
│   │ New Scenario │─────▶│ Select       │──────▶│ Define       │            │
│   │ - Manual     │      │ VuGen Scripts│       │ - VUser count│            │
│   │ - Goal       │      │ from folder  │       │ - Generator  │            │
│   └──────────────┘      └──────────────┘       └──────────────┘            │
│                                                        │                    │
│                                                        ▼                    │
│   6. Execute            5. Verify Ready         4. Set Schedule            │
│   ┌──────────────┐      ┌──────────────┐       ┌──────────────┐            │
│   │ Start        │◀─────│ Check load   │◀──────│ Ramp-up      │            │
│   │ Scenario     │      │ generators   │       │ Duration     │            │
│   │              │      │ connectivity │       │ Ramp-down    │            │
│   └──────────────┘      └──────────────┘       └──────────────┘            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Creating a New Scenario

1. **Launch Controller** and select **New Scenario**
2. **Choose scenario type**: Manual or Goal-Oriented
3. **Select scripts**: Browse to VuGen script folders
4. **Configure groups**: Set VUser counts and generators
5. **Design schedule**: Define ramp-up, duration, ramp-down
6. **Save scenario**: Store configuration for future use

## Manual vs. Goal-Oriented Scenarios

LoadRunner offers two fundamental approaches to scenario design.

### Manual Scenarios

In **manual scenarios**, you explicitly define:
- Number of virtual users per group
- Exact ramp-up schedule
- Test duration
- Ramp-down pattern

```
Manual Scenario Example:
────────────────────────
Group: Browse_Users     → 200 VUsers, ramp 10/minute
Group: Search_Users     → 100 VUsers, ramp 5/minute  
Group: Checkout_Users   → 50 VUsers, ramp 5/minute

Total: 350 VUsers over configured schedule
```

**Best For:**
- Reproducing specific load conditions
- Baseline testing with known user counts
- Comparing test runs with identical configurations

### Goal-Oriented Scenarios

In **goal-oriented scenarios**, you define performance objectives and let the Controller calculate the required VUsers.

```
Goal-Oriented Example:
──────────────────────
Goal: Achieve 500 transactions per second

Controller calculates:
- Browse_Users: needs ~180 VUsers to generate 300 TPS
- Search_Users: needs ~120 VUsers to generate 150 TPS
- Checkout_Users: needs ~50 VUsers to generate 50 TPS

Total: 350 VUsers (calculated automatically)
```

**Goal Types:**

| Goal Type | Description | Use Case |
|-----------|-------------|----------|
| **Virtual Users** | Reach specific VUser count | Capacity testing |
| **Hits per Second** | Achieve target hit rate | Infrastructure sizing |
| **Transactions/Second** | Meet TPS target | SLA validation |
| **Pages per Minute** | Target page throughput | Web application testing |

**Best For:**
- SLA validation ("Can we handle 1,000 TPS?")
- Capacity planning
- Finding breaking points

### Comparison

| Aspect | Manual Scenario | Goal-Oriented Scenario |
|--------|-----------------|------------------------|
| **Control** | Full control over VUser counts | Controller adjusts VUsers |
| **Complexity** | Simpler to set up | Requires goal definition |
| **Repeatability** | Exact same test each run | May vary based on app performance |
| **Use Case** | Baseline, regression | Capacity planning, SLA validation |

## Virtual User Groups Configuration

Virtual User Groups organize your test by user behavior types.

### Why Use Groups?

Real applications have different user types with different behaviors:

```
E-Commerce Application User Mix:
────────────────────────────────
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   Browsers (60%)        Searchers (25%)       Buyers (15%)     │
│   ┌─────────────────┐   ┌─────────────────┐   ┌─────────────┐  │
│   │ • View homepage │   │ • Search items  │   │ • Search    │  │
│   │ • Browse catalog│   │ • Filter results│   │ • Add cart  │  │
│   │ • View products │   │ • Compare items │   │ • Checkout  │  │
│   │ • No purchase   │   │ • Maybe buy     │   │ • Pay       │  │
│   └─────────────────┘   └─────────────────┘   └─────────────┘  │
│                                                                 │
│   Script: Browse.usr    Script: Search.usr   Script: Buy.usr   │
│   VUsers: 600           VUsers: 250          VUsers: 150       │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Group Configuration Options

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    Virtual User Group Settings                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   Group Name:     [Checkout_Users                    ]                      │
│                                                                             │
│   Script:         [Checkout_Script.usr         ] [Browse...]               │
│                                                                             │
│   Quantity:       [50        ] Virtual Users                                │
│                                                                             │
│   Load Generator: [LoadGen_Server_1 ▼]                                      │
│                   ┌─────────────────────┐                                   │
│                   │ localhost           │                                   │
│                   │ LoadGen_Server_1    │                                   │
│                   │ LoadGen_Server_2    │                                   │
│                   │ LoadGen_Cloud_US    │                                   │
│                   └─────────────────────┘                                   │
│                                                                             │
│   Command Line:   [                              ] (Optional arguments)     │
│                                                                             │
│   [ ] Enable IP Spoofing                                                    │
│   [ ] Run as Process (instead of thread)                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Group Configuration Best Practices

1. **Name groups descriptively**: "Checkout_Users" not "Group1"
2. **Match production ratios**: If 60% browse, 60% of VUsers should browse
3. **Consider resource requirements**: Heavy scripts may need dedicated generators
4. **Start conservative**: Begin with fewer VUsers, increase gradually

## Script Assignment

Assigning scripts to groups connects your VuGen work to the Controller.

### Script Selection Process

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       Add Scripts to Scenario                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   Available Scripts:                    Selected Scripts:                   │
│   ┌──────────────────────────────┐     ┌──────────────────────────────┐    │
│   │ 📁 Scripts                   │     │ ✓ Browse_Product.usr         │    │
│   │  ├─ Browse_Product.usr       │ ──▶ │ ✓ Search_Catalog.usr         │    │
│   │  ├─ Search_Catalog.usr       │     │ ✓ Complete_Checkout.usr      │    │
│   │  ├─ Complete_Checkout.usr    │     │                              │    │
│   │  ├─ User_Registration.usr    │     │                              │    │
│   │  └─ Admin_Reports.usr        │     │                              │    │
│   └──────────────────────────────┘     └──────────────────────────────┘    │
│                                                                             │
│   Script Details:                                                           │
│   ─────────────────                                                         │
│   Name: Browse_Product.usr                                                  │
│   Protocol: Web HTTP/HTML                                                   │
│   Last Modified: 2024-01-15 14:30                                           │
│   Path: C:\Scripts\Browse_Product\Browse_Product.usr                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Script Requirements

Before adding to Controller, scripts must:

| Requirement | Why It Matters |
|-------------|----------------|
| **Successful replay** | Script must work with single VUser |
| **Parameterized data** | Each VUser needs unique data |
| **Correlated dynamics** | Sessions must work for all VUsers |
| **Transaction markers** | Need measurements for analysis |
| **Error handling** | Graceful handling of failures |

## Load Generator Management

Load Generators execute the actual virtual users. Managing them effectively is crucial for large-scale tests.

### Load Generator Types

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      Load Generator Options                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   Local (localhost)          Remote On-Premises         Cloud-Based         │
│   ┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐  │
│   │ Controller      │       │                 │       │  ☁️ AWS/Azure   │  │
│   │ Machine         │       │  Data Center    │       │  Load Gen       │  │
│   │                 │       │  Server(s)      │       │                 │  │
│   │ Good for:       │       │                 │       │ Good for:       │  │
│   │ - Small tests   │       │ Good for:       │       │ - Massive scale │  │
│   │ - Development   │       │ - Large tests   │       │ - Geo testing   │  │
│   │ - Community Ed  │       │ - Isolation     │       │ - Burst testing │  │
│   └─────────────────┘       └─────────────────┘       └─────────────────┘  │
│                                                                             │
│   Capacity: 50-200 VU       Capacity: 500-2000 VU     Capacity: Unlimited  │
│                             per machine                                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Adding Load Generators

```
Load Generators Dialog:
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│   Name                    Platform      Status        Details               │
│   ─────────────────────────────────────────────────────────────────────     │
│   localhost               Windows       ● Ready       Local machine         │
│   LoadGen_Server_1        Windows       ● Ready       192.168.1.100        │
│   LoadGen_Server_2        Linux         ● Ready       192.168.1.101        │
│   LoadGen_Cloud           Windows       ○ Connecting  cloud.example.com    │
│                                                                             │
│   [Add...]  [Remove]  [Connect]  [Details...]                               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Generator Connectivity Requirements

| Requirement | Port | Description |
|-------------|------|-------------|
| **Agent Service** | 54345 | LoadRunner Agent communication |
| **Secure Channel** | 443 | HTTPS for encrypted communication |
| **Data Collection** | 50500-50600 | Result data transfer |

### Capacity Planning

```
Estimating Load Generator Needs:
─────────────────────────────────

VUsers per Generator (approximate):
├── Web HTTP/HTML: 500-1,000 VUsers
├── TruClient: 5-15 VUsers (browser overhead)
├── Web Services: 1,000-2,000 VUsers
└── SAP/Citrix: 50-200 VUsers

Example Calculation:
Goal: 2,000 Web HTTP VUsers
Generator Capacity: 500 VUsers each
Generators Needed: 2,000 ÷ 500 = 4 generators

Add buffer for safety: 5 generators
```

## Scenario Scheduling

The schedule determines **when** and **how** virtual users enter and exit the test.

### Schedule Components

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Scenario Schedule                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   VUsers                                                                    │
│   ▲                                                                         │
│   │         ┌───────────────────────────────────────┐                      │
│   │        /│                                       │\                     │
│   │       / │                                       │ \                    │
│   │      /  │          Steady State                 │  \                   │
│   │     /   │         (Duration)                    │   \                  │
│   │    /    │                                       │    \                 │
│   │   /     │                                       │     \                │
│   │  /      │                                       │      \               │
│   │ /       │                                       │       \              │
│   │/        │                                       │        \             │
│   └─────────┴───────────────────────────────────────┴─────────▶ Time      │
│   │         │                                       │         │            │
│   │ Ramp-up │◀─────────── Duration ────────────────▶│Ramp-down│            │
│   │         │                                       │         │            │
│   │ 10 min  │              60 minutes               │  5 min  │            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Schedule Actions

| Action | Description | Options |
|--------|-------------|---------|
| **Initialize** | Prepare VUsers (load script, init connections) | All at once, gradually |
| **Start VUsers** | Begin running Action iterations | Simultaneously, batch, gradual |
| **Duration** | How long VUsers run | Time-based, iteration-based |
| **Stop VUsers** | End VUser execution | Gracefully, immediately |

### Ramp-Up Patterns

```
Pattern 1: Gradual Ramp                 Pattern 2: Stepped Ramp
─────────────────────────               ────────────────────────────
     ▲                                        ▲
     │              /──                       │          ┌───────────
     │            /                           │      ┌───┘
     │          /                             │  ┌───┘
     │        /                               │──┘
     │      /                                 │
     │    /                                   │
     │  /                                     │
     │/                                       │
     └──────────────────▶                     └────────────────────▶
       Time                                     Time

Best for: Realistic user      Best for: Testing specific
arrival simulation            load levels


Pattern 3: Immediate Start              Pattern 4: Goal-Based
─────────────────────────               ────────────────────────────
     ▲                                        ▲
     │┌────────────────────                   │         /────────────
     ││                                       │        /
     ││                                       │       /
     ││                                       │      /  (adjusts based
     ││                                       │     /   on TPS goal)
     ││                                       │    /
     ││                                       │   /
     ││                                       │  /
     └┴────────────────────▶                  └─/──────────────────▶
       Time                                     Time

Best for: Stress testing,     Best for: SLA validation,
finding breaking point        capacity planning
```

### Schedule Configuration

```
Group Schedule Settings:
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│   Start Time:     [ Start at scenario start ▼ ]                            │
│                   ┌───────────────────────────────────┐                     │
│                   │ Start at scenario start           │                     │
│                   │ Start X minutes after scenario    │                     │
│                   │ Start when group X finishes       │                     │
│                   └───────────────────────────────────┘                     │
│                                                                             │
│   Ramp Up:        [ Gradually ▼ ]                                          │
│                   Start [10] VUsers every [30] seconds                     │
│                   Until reaching [200] VUsers                               │
│                                                                             │
│   Duration:       ( ) Run for [60] minutes                                  │
│                   (•) Run until completion                                  │
│                   ( ) Run [5] iterations                                    │
│                                                                             │
│   Ramp Down:      [5] VUsers every [10] seconds                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Schedule Best Practices

1. **Use gradual ramp-up**: Sudden load spikes don't reflect reality
2. **Allow warm-up time**: Let caches populate before measuring
3. **Sufficient steady state**: At least 30-60 minutes for meaningful data
4. **Graceful ramp-down**: Allow transactions to complete naturally
5. **Consider dependencies**: Some groups may need to start after others

## Putting It All Together: Complete Scenario Example

```
E-Commerce Load Test Scenario
═════════════════════════════

Objective: Validate holiday traffic capacity (10x normal load)

Groups:
┌──────────────────┬────────────────┬────────┬─────────────────┐
│ Group            │ Script         │ VUsers │ Generator       │
├──────────────────┼────────────────┼────────┼─────────────────┤
│ Browse_Products  │ Browse.usr     │ 600    │ LoadGen_1,2     │
│ Search_Catalog   │ Search.usr     │ 250    │ LoadGen_2       │
│ Complete_Order   │ Checkout.usr   │ 150    │ LoadGen_3       │
└──────────────────┴────────────────┴────────┴─────────────────┘

Schedule:
─────────
Phase 1: Ramp-up (15 minutes)
  - Browse: Start 40 VUsers/minute
  - Search: Start 17 VUsers/minute  
  - Checkout: Start 10 VUsers/minute

Phase 2: Steady State (60 minutes)
  - All groups at full capacity
  - Monitor response times, errors

Phase 3: Ramp-down (10 minutes)
  - All groups: Stop 100 VUsers/minute
  - Allow in-flight transactions to complete

Success Criteria:
  - Avg response time < 3 seconds
  - Error rate < 1%
  - 95th percentile < 5 seconds
```

## Summary

- The **Controller** orchestrates LoadRunner tests by managing scripts, VUsers, generators, and schedules
- **Manual scenarios** give precise control; **goal-oriented scenarios** automatically adjust to meet targets
- **Virtual user groups** organize tests by user behavior, matching production user mixes
- **Load generators** execute VUsers; capacity planning ensures sufficient infrastructure
- **Scheduling** controls ramp-up, duration, and ramp-down for realistic load patterns
- Well-designed scenarios accurately simulate production conditions for meaningful results

## Additional Resources

- [Controller User Guide - Micro Focus](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Controller/c_controller_intro.htm)
- [Scenario Design Best Practices](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Controller/c_scenario_best_practices.htm)
- [Load Generator Configuration Guide](https://admhelp.microfocus.com/lr/en/latest/help/WebHelp/Content/Controller/c_load_generators.htm)

