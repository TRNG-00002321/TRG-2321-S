# AWS Introduction

## Learning Objectives

- Define cloud computing and explain the three service models (IaaS, PaaS, SaaS)
- Describe what Amazon Web Services (AWS) is and its role in modern IT infrastructure
- Navigate the AWS Console and understand the AWS CLI
- Explain AWS account structure and the basics of Identity and Access Management (IAM)
- Recognize why AWS skills are essential for quality engineers in DevOps environments

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

In Week 10, you're transitioning from application testing to understanding the infrastructure where applications live. Amazon Web Services (AWS) dominates the cloud computing market, powering everything from startups to Fortune 500 enterprises. As a quality engineer, understanding AWS isn't optional—it's fundamental. When tests fail in production, you need to investigate cloud resources. When building CI/CD pipelines (which we'll cover Friday), you'll deploy to AWS. When performance issues arise, you'll examine EC2 instances, database connections, and network configurations.

The applications you've been testing throughout this course—whether through Selenium, API testing, or unit tests—all eventually run on infrastructure like AWS. Understanding this infrastructure makes you a more effective tester and a more valuable team member.

## The Concept

### What is Cloud Computing?

Cloud computing is the delivery of computing services—servers, storage, databases, networking, software, and more—over the internet ("the cloud"). Instead of buying and maintaining physical hardware, organizations rent computing resources from cloud providers like AWS.

**Key characteristics of cloud computing:**

| Characteristic | Description |
|----------------|-------------|
| On-demand self-service | Provision resources automatically without human interaction |
| Broad network access | Access services from anywhere via standard internet protocols |
| Resource pooling | Provider's resources serve multiple customers dynamically |
| Rapid elasticity | Scale resources up or down quickly based on demand |
| Measured service | Pay only for what you use (metered billing) |

### Cloud Service Models

Understanding service models helps you recognize what AWS manages versus what you manage:

```
┌─────────────────────────────────────────────────────────────────┐
│                    Cloud Service Models                         │
├─────────────────┬─────────────────┬─────────────────────────────┤
│      IaaS       │      PaaS       │           SaaS              │
│ Infrastructure  │    Platform     │         Software            │
├─────────────────┼─────────────────┼─────────────────────────────┤
│ You Manage:     │ You Manage:     │ You Manage:                 │
│ - Applications  │ - Applications  │ - Your data                 │
│ - Data          │ - Data          │ - User access               │
│ - Runtime       │                 │                             │
│ - Middleware    │                 │                             │
│ - OS            │                 │                             │
├─────────────────┼─────────────────┼─────────────────────────────┤
│ Provider        │ Provider        │ Provider Manages:           │
│ Manages:        │ Manages:        │ - Everything                │
│ - Virtualization│ - Runtime       │                             │
│ - Servers       │ - Middleware    │                             │
│ - Storage       │ - OS            │                             │
│ - Networking    │ - Virtualization│                             │
│                 │ - Servers       │                             │
│                 │ - Storage       │                             │
│                 │ - Networking    │                             │
├─────────────────┼─────────────────┼─────────────────────────────┤
│ AWS Examples:   │ AWS Examples:   │ Examples:                   │
│ EC2, S3, VPC    │ Elastic         │ Gmail, Salesforce,          │
│                 │ Beanstalk, RDS  │ Microsoft 365               │
└─────────────────┴─────────────────┴─────────────────────────────┘
```

**IaaS (Infrastructure as a Service):** You rent raw computing resources. Think of it as renting a virtual data center. AWS EC2 is a prime example—you get virtual servers but must configure everything from the operating system up.

**PaaS (Platform as a Service):** The platform handles infrastructure, and you focus on your application. AWS Elastic Beanstalk lets you upload code, and it handles servers, load balancing, and scaling automatically.

**SaaS (Software as a Service):** Fully managed applications accessed via web browser. While AWS itself isn't SaaS, it enables companies to build SaaS products.

### What is Amazon Web Services?

AWS is the world's most comprehensive and broadly adopted cloud platform, offering over 200 fully featured services from data centers globally. Launched in 2006, AWS pioneered the cloud computing industry and maintains market leadership.

**AWS by the numbers:**
- 32% global cloud market share (2024)
- Millions of active customers
- 31 geographic regions worldwide
- Hundreds of services across compute, storage, database, machine learning, and more

### AWS Global Infrastructure

AWS infrastructure is organized hierarchically:

```
AWS Global Infrastructure
│
├── Regions (e.g., us-east-1, eu-west-1)
│   │
│   ├── Availability Zones (AZs)
│   │   └── Data Centers
│   │
│   └── Availability Zones (AZs)
│       └── Data Centers
│
└── Edge Locations (for content delivery)
```

- **Regions:** Geographic locations containing multiple data centers (e.g., `us-east-1` is Northern Virginia)
- **Availability Zones (AZs):** Isolated data center clusters within a region, connected by high-bandwidth, low-latency networking
- **Edge Locations:** Endpoints for caching content closer to users

*Note: We'll explore regions and availability zones in depth in the next reading.*

### AWS Console vs CLI

You can interact with AWS through multiple interfaces:

**AWS Management Console (Web UI)**
- Visual, browser-based interface
- Great for learning and exploration
- Point-and-click resource management

**AWS Command Line Interface (CLI)**
- Text-based commands from your terminal
- Essential for automation and scripting
- Consistent across operating systems

```bash
# Example: List all S3 buckets using AWS CLI
aws s3 ls

# Example: Describe EC2 instances
aws ec2 describe-instances --region us-east-1
```

**AWS SDKs (Software Development Kits)**
- Programmatic access in Python (boto3), Java, JavaScript, and more
- Used in applications and automation scripts

```python
# Example: Using Python boto3 SDK
import boto3

# Create an S3 client
s3 = boto3.client('s3')

# List all buckets
response = s3.list_buckets()
for bucket in response['Buckets']:
    print(bucket['Name'])
```

### AWS Account Structure

Understanding AWS account organization is crucial for security and cost management:

```
┌─────────────────────────────────────────────┐
│              AWS Account                     │
│  ┌───────────────────────────────────────┐  │
│  │         Root User (Email)              │  │
│  │  - Full account access                 │  │
│  │  - Should rarely be used               │  │
│  │  - Enable MFA immediately              │  │
│  └───────────────────────────────────────┘  │
│                     │                        │
│                     ▼                        │
│  ┌───────────────────────────────────────┐  │
│  │              IAM                        │  │
│  │  ┌─────────┐  ┌─────────┐  ┌────────┐ │  │
│  │  │  Users  │  │ Groups  │  │ Roles  │ │  │
│  │  └─────────┘  └─────────┘  └────────┘ │  │
│  │         ↓          ↓           ↓       │  │
│  │  ┌─────────────────────────────────┐  │  │
│  │  │          Policies               │  │  │
│  │  │   (Permissions in JSON)         │  │  │
│  │  └─────────────────────────────────┘  │  │
│  └───────────────────────────────────────┘  │
└─────────────────────────────────────────────┘
```

### IAM Basics

**Identity and Access Management (IAM)** controls who can access what in your AWS account.

**Core IAM concepts:**

| Component | Description |
|-----------|-------------|
| **Users** | Individual identities for people or applications |
| **Groups** | Collections of users sharing the same permissions |
| **Roles** | Temporary credentials for services or cross-account access |
| **Policies** | JSON documents defining permissions |

**Example IAM Policy (allows reading S3 buckets):**

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:GetObject",
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::my-bucket",
                "arn:aws:s3:::my-bucket/*"
            ]
        }
    ]
}
```

**IAM Best Practices:**
1. **Never use root account** for daily tasks
2. **Enable MFA** (Multi-Factor Authentication) on all accounts
3. **Grant least privilege**—only permissions needed for the task
4. **Use groups** to assign permissions, not individual users
5. **Rotate credentials** regularly

### Why Quality Engineers Need AWS Knowledge

As a QA professional, AWS knowledge enables you to:

1. **Debug Production Issues:** Investigate EC2 logs, CloudWatch metrics, and database connections
2. **Build Test Infrastructure:** Spin up test environments that mirror production
3. **Understand CI/CD:** Jenkins pipelines deploy to AWS—understanding the target makes you more effective
4. **Performance Testing:** You've learned LoadRunner; now understand the infrastructure being tested
5. **Security Testing:** Verify security groups, IAM policies, and encryption settings

## Code Examples

### Setting Up AWS CLI

```bash
# Install AWS CLI (varies by OS)
# macOS with Homebrew
brew install awscli

# Windows with installer
# Download from https://aws.amazon.com/cli/

# Configure credentials
aws configure
# AWS Access Key ID: AKIAIOSFODNN7EXAMPLE
# AWS Secret Access Key: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
# Default region name: us-east-1
# Default output format: json
```

### Basic AWS CLI Commands

```bash
# Get caller identity (who am I?)
aws sts get-caller-identity

# List EC2 instances
aws ec2 describe-instances --query 'Reservations[].Instances[].[InstanceId,State.Name]'

# List S3 buckets
aws s3 ls

# Get current region
aws configure get region
```

### Python boto3 Example

```python
import boto3

# Create a session with specific credentials
session = boto3.Session(
    aws_access_key_id='YOUR_ACCESS_KEY',
    aws_secret_access_key='YOUR_SECRET_KEY',
    region_name='us-east-1'
)

# Create EC2 client
ec2 = session.client('ec2')

# Describe all running instances
response = ec2.describe_instances(
    Filters=[
        {
            'Name': 'instance-state-name',
            'Values': ['running']
        }
    ]
)

# Print instance IDs
for reservation in response['Reservations']:
    for instance in reservation['Instances']:
        print(f"Instance ID: {instance['InstanceId']}")
        print(f"Instance Type: {instance['InstanceType']}")
        print(f"Public IP: {instance.get('PublicIpAddress', 'N/A')}")
        print("---")
```

## Summary

- **Cloud computing** delivers computing resources over the internet with on-demand provisioning and pay-as-you-go pricing
- **Service models** (IaaS, PaaS, SaaS) define the division of responsibility between you and the cloud provider
- **AWS** is the leading cloud platform with a vast global infrastructure of regions, availability zones, and edge locations
- **AWS Console** provides visual management while **AWS CLI** enables scripting and automation
- **IAM** controls access through users, groups, roles, and policies—always follow least privilege
- **Quality engineers** need AWS knowledge to debug production issues, build test environments, and work effectively with DevOps teams

## Additional Resources

- [AWS Cloud Practitioner Essentials (Free Training)](https://aws.amazon.com/training/digital/aws-cloud-practitioner-essentials/) - Official AWS free course covering fundamentals
- [AWS Documentation - Getting Started](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/EC2_GetStarted.html) - Official step-by-step guides
- [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/) - Complete CLI documentation

