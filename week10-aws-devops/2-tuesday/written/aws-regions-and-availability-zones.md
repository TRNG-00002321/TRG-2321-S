# AWS Regions and Availability Zones

## Learning Objectives

- Explain the structure of AWS global infrastructure: regions, availability zones, and edge locations
- Apply criteria for selecting the appropriate AWS region for your workload
- Design for high availability using multiple availability zones
- Understand multi-AZ deployment patterns for resilient applications
- Recognize compliance and data residency requirements that affect region selection

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

When your CI/CD pipeline deploys an application, *where* it deploys matters as much as *how* it deploys. Deploy to a region far from your users, and they'll experience latency. Deploy to only one availability zone, and a data center outage takes down your application. Ignore compliance requirements, and your organization faces legal consequences.

Understanding AWS geography isn't just infrastructure knowledge—it's the foundation for building reliable, performant, and compliant systems. As a quality engineer, you'll encounter availability zone configurations in test environments, investigate region-specific production issues, and validate that deployments meet organizational requirements.

## The Concept

### AWS Global Infrastructure Overview

AWS organizes its infrastructure into three hierarchical levels:

```
┌─────────────────────────────────────────────────────────────────────┐
│                     AWS Global Infrastructure                        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    REGIONS (31+)                             │   │
│   │         Geographic locations around the world                │   │
│   │     Example: us-east-1 (N. Virginia), eu-west-1 (Ireland)   │   │
│   │                                                              │   │
│   │   ┌─────────────────────────────────────────────────────┐   │   │
│   │   │           AVAILABILITY ZONES (99+)                   │   │   │
│   │   │      Isolated data center clusters within region     │   │   │
│   │   │            Example: us-east-1a, us-east-1b          │   │   │
│   │   │                                                      │   │   │
│   │   │   ┌─────────────────────────────────────────────┐   │   │   │
│   │   │   │           DATA CENTERS                       │   │   │   │
│   │   │   │    Physical buildings with servers           │   │   │   │
│   │   │   └─────────────────────────────────────────────┘   │   │   │
│   │   └─────────────────────────────────────────────────────┘   │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │               EDGE LOCATIONS (450+)                          │   │
│   │        Content delivery endpoints near users                 │   │
│   │              (CloudFront CDN, Route 53)                      │   │
│   └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

### Regions

A **Region** is a geographic area containing multiple, isolated data center clusters. Each region is completely independent—designed so that problems in one region don't affect others.

**Key Region Characteristics:**

| Characteristic | Description |
|----------------|-------------|
| **Isolation** | Regions are physically and logically separated |
| **Independence** | Each region has its own resources, services, and billing |
| **Naming** | Code format: `{area}-{geo}-{number}` (e.g., `us-east-1`) |
| **Service Availability** | Not all AWS services available in all regions |

**Common AWS Regions:**

| Region Code | Region Name | Location |
|-------------|-------------|----------|
| `us-east-1` | US East (N. Virginia) | Northern Virginia, USA |
| `us-west-2` | US West (Oregon) | Oregon, USA |
| `eu-west-1` | Europe (Ireland) | Dublin, Ireland |
| `eu-central-1` | Europe (Frankfurt) | Frankfurt, Germany |
| `ap-northeast-1` | Asia Pacific (Tokyo) | Tokyo, Japan |
| `ap-southeast-1` | Asia Pacific (Singapore) | Singapore |

### Availability Zones (AZs)

An **Availability Zone** is one or more discrete data centers with redundant power, networking, and connectivity within a region. AZs are the building blocks of high availability.

```
┌─────────────────── Region: us-east-1 ───────────────────┐
│                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │ us-east-1a   │  │ us-east-1b   │  │ us-east-1c   │   │
│  │              │  │              │  │              │   │
│  │  ┌────────┐  │  │  ┌────────┐  │  │  ┌────────┐  │   │
│  │  │   DC   │  │  │  │   DC   │  │  │  │   DC   │  │   │
│  │  └────────┘  │  │  └────────┘  │  │  └────────┘  │   │
│  │  ┌────────┐  │  │  ┌────────┐  │  │  ┌────────┐  │   │
│  │  │   DC   │  │  │  │   DC   │  │  │  │   DC   │  │   │
│  │  └────────┘  │  │  └────────┘  │  │  └────────┘  │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
│         │                 │                  │           │
│         └─────────────────┼──────────────────┘           │
│               High-speed, private fiber                  │
└──────────────────────────────────────────────────────────┘
```

**Key AZ Characteristics:**

1. **Physical Separation:** AZs are miles apart, isolated from each other's failures (power outages, floods, fires)
2. **Low Latency Connection:** Despite separation, AZs connect via high-bandwidth, low-latency private fiber (single-digit millisecond latency)
3. **Independent Infrastructure:** Separate power, cooling, and networking
4. **Automatic Mapping:** AZ names (like `us-east-1a`) are mapped per-account to balance load—your `us-east-1a` might be different from another account's

### Edge Locations

**Edge Locations** are data centers designed to deliver content to end users with low latency. They're used by:

- **Amazon CloudFront:** Content Delivery Network (CDN) caching static content
- **Amazon Route 53:** DNS service with low-latency responses
- **AWS Global Accelerator:** Optimizing network paths

Edge locations are far more numerous than regions (450+ locations) because they need to be close to users everywhere.

### Selecting the Right Region

Choosing a region involves balancing multiple factors:

```
┌─────────────────────────────────────────────────────────────────┐
│                   Region Selection Criteria                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. COMPLIANCE & DATA RESIDENCY                                  │
│     └── Does data legally need to stay in a specific country?   │
│         Example: GDPR requires EU citizen data in EU             │
│                                                                  │
│  2. LATENCY TO USERS                                             │
│     └── Where are your users located?                           │
│         Deploy closest to majority of users                      │
│                                                                  │
│  3. SERVICE AVAILABILITY                                         │
│     └── Is the AWS service you need available in that region?   │
│         New services launch in us-east-1 first                   │
│                                                                  │
│  4. COST                                                         │
│     └── Pricing varies by region                                │
│         us-east-1 often cheapest, São Paulo often most expensive│
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Decision Framework:**

```
START
  │
  ▼
┌─────────────────────────────┐
│ Are there compliance        │───YES──▶ Use required region(s)
│ requirements?               │
└─────────────────────────────┘
  │ NO
  ▼
┌─────────────────────────────┐
│ Where are most users?       │───▶ Select closest region
└─────────────────────────────┘
  │
  ▼
┌─────────────────────────────┐
│ Are all needed services     │───NO───▶ Choose region with
│ available?                  │          required services
└─────────────────────────────┘
  │ YES
  ▼
┌─────────────────────────────┐
│ Compare costs between       │
│ eligible regions            │
└─────────────────────────────┘
```

### High Availability Concepts

**High Availability (HA)** means your application remains operational even when components fail. AWS enables HA through multiple AZs.

**Availability Metrics:**

| Availability | Downtime per Year | AZ Strategy |
|--------------|-------------------|-------------|
| 99% | 3.65 days | Single AZ |
| 99.9% | 8.76 hours | Multi-AZ |
| 99.99% | 52.56 minutes | Multi-AZ + Multi-Region |
| 99.999% | 5.26 minutes | Active-Active Multi-Region |

**Single AZ vs Multi-AZ:**

```
┌─────────────── Single AZ (Not Recommended) ───────────────┐
│                                                            │
│  ┌──────────────────────────────────────────────────────┐ │
│  │                   us-east-1a                          │ │
│  │                                                       │ │
│  │   ┌─────────┐    ┌─────────┐    ┌─────────┐         │ │
│  │   │   EC2   │    │   EC2   │    │   RDS   │         │ │
│  │   │ (Web)   │    │ (App)   │    │  (DB)   │         │ │
│  │   └─────────┘    └─────────┘    └─────────┘         │ │
│  │                                                       │ │
│  │         ❌ AZ failure = Complete outage              │ │
│  └──────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────┘

┌─────────────── Multi-AZ (Recommended) ────────────────────┐
│                                                            │
│  ┌─────────────────────┐    ┌─────────────────────┐       │
│  │     us-east-1a      │    │     us-east-1b      │       │
│  │                     │    │                     │       │
│  │  ┌─────┐  ┌─────┐  │    │  ┌─────┐  ┌─────┐  │       │
│  │  │ EC2 │  │ EC2 │  │    │  │ EC2 │  │ EC2 │  │       │
│  │  │(Web)│  │(App)│  │    │  │(Web)│  │(App)│  │       │
│  │  └─────┘  └─────┘  │    │  └─────┘  └─────┘  │       │
│  │                     │    │                     │       │
│  │  ┌─────────────┐   │    │  ┌─────────────┐   │       │
│  │  │ RDS Primary │◄──┼────┼──│ RDS Standby │   │       │
│  │  └─────────────┘   │    │  └─────────────┘   │       │
│  └─────────────────────┘    └─────────────────────┘       │
│                                                            │
│         ✅ AZ failure = Automatic failover                │
└────────────────────────────────────────────────────────────┘
```

### Multi-AZ Deployment Patterns

**Pattern 1: Stateless Application Tier**

Deploy identical application instances across AZs behind a load balancer:

```
         ┌─────────────────────┐
         │   Load Balancer     │
         │   (Multi-AZ)        │
         └──────────┬──────────┘
                    │
        ┌───────────┴───────────┐
        │                       │
        ▼                       ▼
┌───────────────┐       ┌───────────────┐
│  us-east-1a   │       │  us-east-1b   │
│  ┌─────────┐  │       │  ┌─────────┐  │
│  │   EC2   │  │       │  │   EC2   │  │
│  └─────────┘  │       │  └─────────┘  │
│  ┌─────────┐  │       │  ┌─────────┐  │
│  │   EC2   │  │       │  │   EC2   │  │
│  └─────────┘  │       │  └─────────┘  │
└───────────────┘       └───────────────┘
```

**Pattern 2: Database Tier with Multi-AZ RDS**

RDS automatically maintains a synchronous standby in another AZ:

```
┌───────────────────────────────────────────────────┐
│                   RDS Multi-AZ                     │
│                                                    │
│  ┌─────────────────┐    ┌─────────────────┐       │
│  │   us-east-1a    │    │   us-east-1b    │       │
│  │                 │    │                 │       │
│  │  ┌───────────┐  │    │  ┌───────────┐  │       │
│  │  │  Primary  │──┼────┼──│  Standby  │  │       │
│  │  │    DB     │  │sync│  │    DB     │  │       │
│  │  └───────────┘  │    │  └───────────┘  │       │
│  └─────────────────┘    └─────────────────┘       │
│                                                    │
│  On failure: Automatic failover in 60-120 seconds │
└───────────────────────────────────────────────────┘
```

**Pattern 3: Multi-Region for Disaster Recovery**

For critical applications, deploy across multiple regions:

```
┌────────────────────────┐         ┌────────────────────────┐
│     us-east-1          │         │     eu-west-1          │
│     (Primary)          │         │     (DR Site)          │
│                        │         │                        │
│  ┌─────┐    ┌─────┐   │         │  ┌─────┐    ┌─────┐   │
│  │ EC2 │    │ RDS │◄──┼─────────┼──│ RDS │    │ EC2 │   │
│  └─────┘    └─────┘   │  async  │  └─────┘    └─────┘   │
│                        │  replic │       (Read Replica)  │
└────────────────────────┘         └────────────────────────┘
```

## Code Examples

### Listing Available Regions

```bash
# List all regions available to your account
aws ec2 describe-regions --output table

# List only region names
aws ec2 describe-regions --query 'Regions[].RegionName' --output text
```

### Listing Availability Zones

```bash
# List AZs in current region
aws ec2 describe-availability-zones --output table

# List AZs in a specific region
aws ec2 describe-availability-zones --region us-west-2 \
    --query 'AvailabilityZones[].ZoneName' --output text
```

### Python: Get Region and AZ Information

```python
import boto3

# Create EC2 client
ec2 = boto3.client('ec2', region_name='us-east-1')

# Get all regions
regions = ec2.describe_regions()
print("Available Regions:")
for region in regions['Regions']:
    print(f"  - {region['RegionName']}: {region['Endpoint']}")

print("\n" + "="*50 + "\n")

# Get availability zones for current region
azs = ec2.describe_availability_zones()
print("Availability Zones in us-east-1:")
for az in azs['AvailabilityZones']:
    print(f"  - {az['ZoneName']}: {az['State']}")
```

### Checking Service Availability by Region

```python
import boto3

def check_service_availability(service_name, regions=None):
    """Check which regions support a given AWS service."""
    
    session = boto3.Session()
    
    if regions is None:
        ec2 = session.client('ec2', region_name='us-east-1')
        regions = [r['RegionName'] for r in ec2.describe_regions()['Regions']]
    
    available_regions = []
    
    for region in regions:
        try:
            # Try to create a client for the service in this region
            client = session.client(service_name, region_name=region)
            available_regions.append(region)
        except Exception as e:
            pass  # Service not available in this region
    
    return available_regions

# Example: Check where SageMaker is available
# sagemaker_regions = check_service_availability('sagemaker')
```

### Deploying Resources to Specific AZs

```bash
# Launch EC2 instance in specific AZ
aws ec2 run-instances \
    --image-id ami-0c55b159cbfafe1f0 \
    --instance-type t2.micro \
    --placement AvailabilityZone=us-east-1a \
    --key-name my-key-pair

# Create subnet in specific AZ (for VPC)
aws ec2 create-subnet \
    --vpc-id vpc-1234567890abcdef0 \
    --cidr-block 10.0.1.0/24 \
    --availability-zone us-east-1a
```

### Testing Latency to Different Regions

```python
import requests
import time

# AWS regional endpoints for latency testing
regional_endpoints = {
    'us-east-1': 'https://ec2.us-east-1.amazonaws.com',
    'us-west-2': 'https://ec2.us-west-2.amazonaws.com',
    'eu-west-1': 'https://ec2.eu-west-1.amazonaws.com',
    'ap-northeast-1': 'https://ec2.ap-northeast-1.amazonaws.com',
}

def measure_latency(url, attempts=3):
    """Measure average latency to an endpoint."""
    latencies = []
    for _ in range(attempts):
        start = time.time()
        try:
            requests.get(url, timeout=5)
            latency = (time.time() - start) * 1000  # Convert to ms
            latencies.append(latency)
        except requests.RequestException:
            latencies.append(float('inf'))
    return sum(latencies) / len(latencies)

# Test latency to each region
print("Latency to AWS Regions:")
for region, endpoint in regional_endpoints.items():
    latency = measure_latency(endpoint)
    print(f"  {region}: {latency:.2f} ms")
```

## Summary

- **Regions** are independent geographic locations (31+) containing isolated infrastructure
- **Availability Zones** are discrete data center clusters within a region, connected by low-latency links
- **Edge Locations** (450+) cache content close to users for fast delivery
- **Region selection** should consider compliance, latency, service availability, and cost
- **High availability** requires deploying across multiple AZs—single AZ is a single point of failure
- **Multi-AZ patterns** include load-balanced application tiers, RDS automatic failover, and multi-region disaster recovery
- As a quality engineer, validate that deployments use appropriate AZ strategies for required availability levels

## Additional Resources

- [AWS Global Infrastructure](https://aws.amazon.com/about-aws/global-infrastructure/) - Interactive map and current region/AZ information
- [AWS Regions and Availability Zones Documentation](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-regions-availability-zones.html) - Official technical documentation
- [AWS Architecture Blog: Multi-AZ Best Practices](https://aws.amazon.com/blogs/architecture/tag/multi-az/) - Real-world architecture patterns

