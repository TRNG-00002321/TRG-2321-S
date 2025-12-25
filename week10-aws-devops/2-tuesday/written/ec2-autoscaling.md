# EC2 Auto Scaling

## Learning Objectives

- Explain what EC2 Auto Scaling is and its benefits for application availability
- Describe EC2 instance types and select appropriate sizes for workloads
- Create and manage launch templates for consistent instance configuration
- Configure Auto Scaling groups with minimum, maximum, and desired capacity
- Implement scaling policies: target tracking, step scaling, and scheduled scaling
- Integrate Auto Scaling with Elastic Load Balancing for traffic distribution

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Applications face varying demand—traffic spikes during sales events, quiet periods overnight, and unexpected viral moments. Manually provisioning servers is slow, error-prone, and expensive (you either over-provision and waste money, or under-provision and lose customers).

Auto Scaling automatically adjusts capacity to maintain performance and minimize costs. As a quality engineer, you'll encounter Auto Scaling when testing application behavior under load (remember LoadRunner from Week 9?), validating CI/CD deployments across multiple instances, and understanding why test environments might behave differently from production.

## The Concept

### What is EC2 Auto Scaling?

**EC2 Auto Scaling** automatically adjusts the number of EC2 instances in your application based on conditions you define. It ensures you have the right number of instances available to handle your application load.

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Auto Scaling Benefits                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   AVAILABILITY           COST OPTIMIZATION      FAULT TOLERANCE     │
│   ────────────           ─────────────────      ───────────────     │
│   • Maintain desired     • Scale in during     • Replace unhealthy  │
│     instance count         low demand            instances          │
│   • Scale out for        • Pay only for        • Distribute across  │
│     increased demand       what you need         multiple AZs       │
│   • Meet SLAs            • No over-provisioning                     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### EC2 Instance Types

Before configuring Auto Scaling, understand the instance types available:

| Family | Optimized For | Use Cases |
|--------|---------------|-----------|
| **T** (Burstable) | Variable CPU workloads | Dev/test, small web servers |
| **M** (General Purpose) | Balanced compute/memory | Web servers, app servers |
| **C** (Compute) | High CPU performance | Batch processing, gaming |
| **R** (Memory) | Memory-intensive | Databases, caching |
| **I** (Storage) | High I/O performance | Data warehousing |
| **G/P** (Accelerated) | GPU workloads | Machine learning, graphics |

**Instance Naming Convention:**
```
Instance Name: m6g.xlarge
               │││  │
               │││  └── Size (nano < micro < small < medium < large < xlarge < 2xlarge...)
               ││└───── Generation (higher = newer, better price/performance)
               │└────── Processor type (g = Graviton/ARM, i = Intel, a = AMD)
               └─────── Family (m = general purpose)
```

**Common Instance Sizes:**

| Size | vCPU | Memory | Example Price (us-east-1) |
|------|------|--------|---------------------------|
| t3.micro | 2 | 1 GB | ~$0.0104/hr |
| t3.small | 2 | 2 GB | ~$0.0208/hr |
| m6i.large | 2 | 8 GB | ~$0.096/hr |
| m6i.xlarge | 4 | 16 GB | ~$0.192/hr |

### Launch Templates

A **Launch Template** defines the configuration for instances that Auto Scaling will launch:

```
┌─────────────────────────────────────────────────────────────────┐
│                      Launch Template                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   AMI ID             │  ami-0c55b159cbfafe1f0 (Amazon Linux 2)  │
│   Instance Type      │  t3.micro                                 │
│   Key Pair           │  my-key-pair                              │
│   Security Groups    │  sg-web-servers                           │
│   IAM Role           │  WebServerRole                            │
│   User Data          │  #!/bin/bash                              │
│                      │  yum update -y                            │
│                      │  yum install -y httpd                     │
│                      │  systemctl start httpd                    │
│   Block Devices      │  20 GB gp3 root volume                    │
│   Network            │  VPC subnet configuration                 │
│   Tags               │  Environment=Production                   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Benefits of Launch Templates:**
- Versioning: Create new versions without replacing the template
- Inheritance: New versions can inherit from previous versions
- Multiple instance types: Support mixed instance policies

### Auto Scaling Groups

An **Auto Scaling Group (ASG)** is a collection of EC2 instances treated as a logical grouping for scaling and management:

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Auto Scaling Group                              │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    Configuration                             │   │
│   │                                                              │   │
│   │   Launch Template: web-server-template (v3)                  │   │
│   │   Minimum Capacity: 2                                        │   │
│   │   Desired Capacity: 4                                        │   │
│   │   Maximum Capacity: 10                                       │   │
│   │   Availability Zones: us-east-1a, us-east-1b, us-east-1c    │   │
│   │   Health Check Type: ELB                                     │   │
│   │   Health Check Grace Period: 300 seconds                     │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    Current State                             │   │
│   │                                                              │   │
│   │   us-east-1a        us-east-1b        us-east-1c            │   │
│   │   ┌─────────┐       ┌─────────┐       ┌─────────┐           │   │
│   │   │  EC2-1  │       │  EC2-2  │       │  EC2-3  │           │   │
│   │   │ Healthy │       │ Healthy │       │ Healthy │           │   │
│   │   └─────────┘       └─────────┘       └─────────┘           │   │
│   │                     ┌─────────┐                              │   │
│   │                     │  EC2-4  │                              │   │
│   │                     │ Healthy │                              │   │
│   │                     └─────────┘                              │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

**Capacity Settings:**
- **Minimum:** Never go below this count (ensures availability)
- **Desired:** The target number of healthy instances
- **Maximum:** Never exceed this count (controls costs)

### Scaling Policies

Auto Scaling supports multiple scaling policy types:

#### 1. Target Tracking Scaling

The simplest and most common approach—you specify a target metric value:

```
┌─────────────────────────────────────────────────────────────────┐
│               Target Tracking Scaling Policy                     │
│                                                                  │
│   Target Metric: Average CPU Utilization                        │
│   Target Value: 50%                                              │
│                                                                  │
│   CPU%                                                           │
│   100│                                                           │
│      │     ╭─────╮                                              │
│    80│    ╱       ╲           Scale Out                         │
│      │   ╱         ╲          (add instances)                   │
│    60│  ╱           ╲                                           │
│      │ ╱             ╲──────────────────────                    │
│    50│───────────────────── Target ─────────                    │
│      │                       ╲                                   │
│    40│                        ╲       Scale In                  │
│      │                         ╲      (remove instances)        │
│    20│                          ╲                               │
│      └───────────────────────────────────────────────────────   │
│                          Time                                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### 2. Step Scaling

More granular control with different actions at different thresholds:

```
┌─────────────────────────────────────────────────────────────────┐
│                    Step Scaling Policy                           │
│                                                                  │
│   CPU%  │  Action                                               │
│   ──────┼─────────────────────────────────────────              │
│   > 80% │  Add 3 instances                                      │
│   > 60% │  Add 1 instance                                       │
│   < 40% │  Remove 1 instance                                    │
│   < 20% │  Remove 2 instances                                   │
│                                                                  │
│   CPU%                                                           │
│   100│         ┌─────┐                                          │
│      │         │+3   │                                          │
│    80│─────────┴─────┴──────────────────────────────           │
│      │    ┌────┐                                                │
│    60│────┤+1  ├─────────────────────────────────              │
│      │    └────┘                                                │
│    40│───────────────────────────────────────────              │
│      │                        ┌───┐                             │
│    20│────────────────────────┤-1 ├─────────────               │
│      │                   ┌────┤-2 │                             │
│     0│───────────────────┴────┴───┴─────────────               │
│      └───────────────────────────────────────────               │
│                          Time                                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### 3. Scheduled Scaling

Scale based on known patterns:

```
┌─────────────────────────────────────────────────────────────────┐
│                   Scheduled Scaling Policy                       │
│                                                                  │
│   Schedule                   │  Action                          │
│   ───────────────────────────┼────────────────────              │
│   Weekdays 8:00 AM           │  Set desired to 10               │
│   Weekdays 6:00 PM           │  Set desired to 2                │
│   Fridays 5:00 PM            │  Set desired to 15 (weekend sale)│
│   Sundays 11:00 PM           │  Set desired to 2                │
│                                                                  │
│   Instances                                                      │
│   15 │                              ┌─────────┐                 │
│      │                              │ Weekend │                 │
│   10 │     ┌──────────┐     ┌──────┤  Sale   │                 │
│      │     │ Business │     │      └────┬────┘                 │
│    5 │     │   Day    │     │           │                       │
│      │     │          │     │           │                       │
│    2 │─────┘          └─────┘           └─────────              │
│      └──────────────────────────────────────────────            │
│        Mon    Tue    Wed    Thu    Fri    Sat    Sun            │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Integration with Load Balancers

Auto Scaling works best with Elastic Load Balancing (ELB):

```
┌─────────────────────────────────────────────────────────────────────┐
│              Auto Scaling + Load Balancer Integration                │
│                                                                      │
│                         Internet                                     │
│                            │                                         │
│                            ▼                                         │
│                   ┌─────────────────┐                               │
│                   │  Application    │                               │
│                   │  Load Balancer  │                               │
│                   └────────┬────────┘                               │
│                            │                                         │
│          ┌─────────────────┼─────────────────┐                      │
│          │                 │                 │                       │
│          ▼                 ▼                 ▼                       │
│   ┌────────────┐   ┌────────────┐   ┌────────────┐                 │
│   │    EC2     │   │    EC2     │   │    EC2     │                 │
│   │ (Healthy)  │   │ (Healthy)  │   │ (Healthy)  │                 │
│   └────────────┘   └────────────┘   └────────────┘                 │
│                                                                      │
│   Auto Scaling Group                                                 │
│   • New instances automatically registered with ALB                  │
│   • Unhealthy instances (failed health checks) terminated            │
│   • Traffic distributed evenly across healthy instances              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

**Health Check Types:**
- **EC2:** Instance is healthy if running (basic check)
- **ELB:** Instance is healthy if passing load balancer health checks (recommended)

### Scaling Best Practices

```
┌─────────────────────────────────────────────────────────────────┐
│                 Auto Scaling Best Practices                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. USE MULTIPLE AZs                                             │
│     Deploy across at least 2 AZs for fault tolerance            │
│                                                                  │
│  2. SET APPROPRIATE COOLDOWN PERIODS                             │
│     Prevent rapid scaling oscillations (default 300 seconds)    │
│                                                                  │
│  3. USE ELB HEALTH CHECKS                                        │
│     More accurate than EC2 health checks alone                  │
│                                                                  │
│  4. CONFIGURE GRACE PERIOD                                       │
│     Allow time for instances to bootstrap before health checks  │
│                                                                  │
│  5. USE LIFECYCLE HOOKS                                          │
│     Run custom actions during launch/termination                │
│                                                                  │
│  6. PREFER TARGET TRACKING                                       │
│     Simpler to configure and self-adjusting                     │
│                                                                  │
│  7. MONITOR SCALING ACTIVITIES                                   │
│     Use CloudWatch to track scaling events and metrics          │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Create a Launch Template

```bash
# Create launch template
aws ec2 create-launch-template \
    --launch-template-name web-server-template \
    --version-description "Initial version" \
    --launch-template-data '{
        "ImageId": "ami-0c55b159cbfafe1f0",
        "InstanceType": "t3.micro",
        "KeyName": "my-key-pair",
        "SecurityGroupIds": ["sg-0123456789abcdef0"],
        "UserData": "IyEvYmluL2Jhc2gKeXVtIHVwZGF0ZSAteQp5dW0gaW5zdGFsbCAteSBodHRwZApzeXN0ZW1jdGwgc3RhcnQgaHR0cGQK",
        "TagSpecifications": [{
            "ResourceType": "instance",
            "Tags": [{"Key": "Environment", "Value": "Production"}]
        }]
    }'

# Note: UserData is base64 encoded. The above decodes to:
# #!/bin/bash
# yum update -y
# yum install -y httpd
# systemctl start httpd
```

### Create an Auto Scaling Group

```bash
# Create Auto Scaling group
aws autoscaling create-auto-scaling-group \
    --auto-scaling-group-name web-server-asg \
    --launch-template "LaunchTemplateName=web-server-template,Version=\$Latest" \
    --min-size 2 \
    --max-size 10 \
    --desired-capacity 4 \
    --vpc-zone-identifier "subnet-0123456789abcdef0,subnet-fedcba9876543210f" \
    --target-group-arns "arn:aws:elasticloadbalancing:us-east-1:123456789012:targetgroup/my-targets/73e2d6bc24d8a067" \
    --health-check-type ELB \
    --health-check-grace-period 300 \
    --tags "Key=Name,Value=WebServer,PropagateAtLaunch=true"
```

### Configure Target Tracking Scaling Policy

```bash
# Create target tracking policy for CPU utilization
aws autoscaling put-scaling-policy \
    --auto-scaling-group-name web-server-asg \
    --policy-name cpu-target-tracking \
    --policy-type TargetTrackingScaling \
    --target-tracking-configuration '{
        "PredefinedMetricSpecification": {
            "PredefinedMetricType": "ASGAverageCPUUtilization"
        },
        "TargetValue": 50.0,
        "ScaleOutCooldown": 300,
        "ScaleInCooldown": 300
    }'
```

### Configure Scheduled Scaling

```bash
# Scale up at 8 AM on weekdays
aws autoscaling put-scheduled-update-group-action \
    --auto-scaling-group-name web-server-asg \
    --scheduled-action-name scale-up-morning \
    --recurrence "0 8 * * MON-FRI" \
    --desired-capacity 10

# Scale down at 6 PM on weekdays
aws autoscaling put-scheduled-update-group-action \
    --auto-scaling-group-name web-server-asg \
    --scheduled-action-name scale-down-evening \
    --recurrence "0 18 * * MON-FRI" \
    --desired-capacity 2
```

### Python: Monitor Auto Scaling Group

```python
import boto3

def get_asg_status(asg_name):
    """Get current status of an Auto Scaling group."""
    
    autoscaling = boto3.client('autoscaling')
    
    response = autoscaling.describe_auto_scaling_groups(
        AutoScalingGroupNames=[asg_name]
    )
    
    if not response['AutoScalingGroups']:
        print(f"ASG '{asg_name}' not found")
        return
    
    asg = response['AutoScalingGroups'][0]
    
    print(f"Auto Scaling Group: {asg['AutoScalingGroupName']}")
    print(f"  Min Size: {asg['MinSize']}")
    print(f"  Max Size: {asg['MaxSize']}")
    print(f"  Desired Capacity: {asg['DesiredCapacity']}")
    print(f"  Current Instances: {len(asg['Instances'])}")
    print(f"\n  Instances:")
    
    for instance in asg['Instances']:
        print(f"    - {instance['InstanceId']}: {instance['HealthStatus']} ({instance['LifecycleState']})")

def get_scaling_activities(asg_name, max_items=5):
    """Get recent scaling activities."""
    
    autoscaling = boto3.client('autoscaling')
    
    response = autoscaling.describe_scaling_activities(
        AutoScalingGroupName=asg_name,
        MaxRecords=max_items
    )
    
    print(f"\nRecent Scaling Activities for {asg_name}:")
    for activity in response['Activities']:
        print(f"  - {activity['StartTime']}: {activity['Description']}")
        print(f"    Status: {activity['StatusCode']}")
        if 'Cause' in activity:
            print(f"    Cause: {activity['Cause'][:100]}...")

# Example usage
get_asg_status('web-server-asg')
get_scaling_activities('web-server-asg')
```

### Describe Scaling Policies

```bash
# List all scaling policies for an ASG
aws autoscaling describe-policies \
    --auto-scaling-group-name web-server-asg \
    --query 'ScalingPolicies[].[PolicyName,PolicyType,TargetTrackingConfiguration.TargetValue]' \
    --output table

# View scaling activities
aws autoscaling describe-scaling-activities \
    --auto-scaling-group-name web-server-asg \
    --max-items 5 \
    --query 'Activities[].[StartTime,StatusCode,Description]' \
    --output table
```

## Summary

- **EC2 Auto Scaling** automatically adjusts instance count based on demand, improving availability and reducing costs
- **Instance types** are categorized by family (T, M, C, R) and sized from nano to metal
- **Launch templates** define instance configuration and support versioning
- **Auto Scaling groups** manage collections of instances with min/max/desired capacity settings
- **Scaling policies** include target tracking (simplest), step scaling (granular), and scheduled scaling (predictable patterns)
- **Load balancer integration** distributes traffic and provides accurate health checks
- As a quality engineer, understanding Auto Scaling helps you test application behavior under load and validate deployments across multiple instances

## Additional Resources

- [EC2 Auto Scaling Documentation](https://docs.aws.amazon.com/autoscaling/ec2/userguide/) - Official comprehensive guide
- [EC2 Instance Types](https://aws.amazon.com/ec2/instance-types/) - Complete list with specifications
- [Auto Scaling Best Practices](https://docs.aws.amazon.com/autoscaling/ec2/userguide/auto-scaling-best-practices.html) - AWS recommendations

