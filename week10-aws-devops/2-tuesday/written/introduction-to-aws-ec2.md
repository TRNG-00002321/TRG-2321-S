# Introduction to AWS EC2

## Learning Objectives
- Define what Amazon EC2 is and its role in cloud computing
- Understand the key components of EC2 instances
- Identify common EC2 instance types and their use cases
- Explain the basic EC2 pricing models
- Recognize how EC2 integrates with data engineering workflows

## Why This Matters

Amazon Elastic Compute Cloud (EC2) is one of the most fundamental services in Amazon Web Services (AWS). As a data professional, you'll encounter EC2 instances constantly—whether hosting databases, running ETL jobs, deploying machine learning models, or processing large datasets. Understanding EC2 gives you the foundation to architect scalable, cost-effective solutions in the cloud.

Think of EC2 as renting a virtual computer. Instead of buying physical hardware, you can spin up a server in minutes, use it for as long as you need, and pay only for what you consume. This on-demand computing power is central to modern data pipelines and analytics platforms.

## The Concept

### What is Amazon EC2?

**Amazon EC2** (Elastic Compute Cloud) is a web service that provides resizable compute capacity in the cloud. It allows you to:

- **Launch virtual servers** (called instances) within minutes
- **Scale capacity up or down** based on your computing requirements
- **Pay only for the capacity you use**
- **Choose from a variety of hardware configurations**

> **Analogy:** If cloud computing is like a utility company, EC2 is the electricity. Just like you pay for the electricity you consume without owning the power plant, EC2 lets you use computing power without owning physical servers.

### Key Components of EC2

| Component | Description |
|-----------|-------------|
| **Instance** | A virtual server running in AWS |
| **AMI (Amazon Machine Image)** | A template containing the OS and software configuration |
| **Instance Type** | The hardware of the host computer (CPU, memory, storage) |
| **Security Group** | Virtual firewall controlling inbound/outbound traffic |
| **Key Pair** | Secure login credentials (SSH keys) |
| **EBS (Elastic Block Store)** | Persistent storage volumes attached to instances |
| **Elastic IP** | Static public IP address for dynamic cloud computing |

### EC2 Instance Types

EC2 offers a wide variety of instance types optimized for different use cases:

| Family | Optimized For | Example Use Cases |
|--------|--------------|-------------------|
| **T-series** (T3, T3a) | General purpose, burstable | Web servers, development environments |
| **M-series** (M5, M6i) | General purpose, balanced | Application servers, small databases |
| **C-series** (C5, C6i) | Compute-intensive | Batch processing, high-performance computing |
| **R-series** (R5, R6i) | Memory-intensive | In-memory databases, real-time analytics |
| **P-series** (P3, P4) | GPU-accelerated | Machine learning training, data science |
| **I-series** (I3, I4i) | Storage-optimized | Data warehousing, distributed file systems |

### Instance Naming Convention

Understanding EC2 instance names helps you quickly identify capabilities:

```
m5.xlarge
│ │  │
│ │  └── Size (nano, micro, small, medium, large, xlarge, 2xlarge, etc.)
│ └───── Generation (5th generation)
└─────── Family (m = general purpose)
```

**Examples:**
- `t3.micro` → General purpose, 3rd gen, micro size (1 vCPU, 1 GB RAM)
- `r5.2xlarge` → Memory-optimized, 5th gen, 2xlarge (8 vCPU, 64 GB RAM)
- `p3.8xlarge` → GPU instance, 3rd gen, 8xlarge (32 vCPU, 4 GPUs)

### EC2 Pricing Models

AWS offers several pricing options to optimize costs:

| Pricing Model | Description | Best For |
|---------------|-------------|----------|
| **On-Demand** | Pay by the hour/second, no commitment | Short-term, unpredictable workloads |
| **Reserved Instances** | 1 or 3-year commitment, up to 72% discount | Steady-state, predictable usage |
| **Spot Instances** | Bid on unused capacity, up to 90% discount | Fault-tolerant, flexible workloads |
| **Savings Plans** | Flexible pricing with commitment to usage | Organizations with predictable spend |
| **Dedicated Hosts** | Physical server dedicated to you | Compliance, licensing requirements |

> **Pro Tip for Data Engineers:** Spot Instances are excellent for batch processing jobs, ETL workloads, and distributed data processing (like Spark clusters) where interruptions can be handled gracefully.

### EC2 in Data Engineering

EC2 instances are foundational to many data engineering scenarios:

```
┌─────────────────────────────────────────────────────────────┐
│                    Data Engineering with EC2                 │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │   Ingest    │───▶│   Process   │───▶│   Store     │      │
│  │   Server    │    │   Cluster   │    │   Results   │      │
│  │  (t3.micro) │    │  (c5.4xlarge)│   │   (S3)      │      │
│  └─────────────┘    └─────────────┘    └─────────────┘      │
│                                                              │
│  Common EC2 Data Workloads:                                 │
│  • Running Apache Spark/Hadoop clusters                     │
│  • Hosting relational databases (MySQL, PostgreSQL)         │
│  • Running ETL orchestration tools (Airflow)                │
│  • Deploying ML models for batch inference                  │
│  • Processing real-time streaming data                      │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### EC2 Lifecycle

Understanding the instance lifecycle helps manage costs and operations:

```
┌──────────┐     ┌─────────┐     ┌──────────┐     ┌───────────┐
│ Pending  │────▶│ Running │────▶│ Stopping │────▶│  Stopped  │
└──────────┘     └─────────┘     └──────────┘     └───────────┘
                      │                                 │
                      ▼                                 ▼
               ┌────────────┐                   ┌─────────────┐
               │  Shutting  │                   │  Terminated │
               │   Down     │                   │  (deleted)  │
               └────────────┘                   └─────────────┘
```

- **Pending:** Instance is being launched
- **Running:** Instance is active and billing is occurring
- **Stopping/Stopped:** Instance is paused; EBS storage persists (no compute billing)
- **Terminated:** Instance is permanently deleted

## Code Example

While EC2 is primarily managed through the AWS Console or CLI, here's an example of launching an instance using Python and Boto3:

```python
import boto3

# Create EC2 client
ec2 = boto3.client('ec2', region_name='us-east-1')

# Launch a new EC2 instance
response = ec2.run_instances(
    ImageId='ami-0abcdef1234567890',  # Amazon Linux 2 AMI
    InstanceType='t3.micro',
    MinCount=1,
    MaxCount=1,
    KeyName='my-key-pair',
    SecurityGroupIds=['sg-0123456789abcdef0'],
    TagSpecifications=[
        {
            'ResourceType': 'instance',
            'Tags': [
                {'Key': 'Name', 'Value': 'DataProcessingServer'},
                {'Key': 'Environment', 'Value': 'Development'}
            ]
        }
    ]
)

# Get the instance ID
instance_id = response['Instances'][0]['InstanceId']
print(f"Launched instance: {instance_id}")
```

## Key Takeaways

- **EC2 provides scalable virtual servers** in the AWS cloud, eliminating the need to invest in physical hardware
- **Instance types** are optimized for different workloads—choose based on your CPU, memory, and GPU requirements
- **Multiple pricing models** allow you to optimize costs based on your usage patterns
- **Data engineers use EC2** for running databases, processing frameworks, and ML workloads
- **Instance lifecycle management** is critical for cost optimization—stop or terminate instances when not needed

## Additional Resources

- [AWS EC2 Official Documentation](https://docs.aws.amazon.com/ec2/)
- [EC2 Instance Types](https://aws.amazon.com/ec2/instance-types/)
- [EC2 Pricing](https://aws.amazon.com/ec2/pricing/)
