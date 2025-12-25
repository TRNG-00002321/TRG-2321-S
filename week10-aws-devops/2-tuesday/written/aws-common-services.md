# AWS Common Services

## Learning Objectives

- Categorize AWS services by their primary function (compute, storage, database, networking)
- Identify the appropriate AWS service for common use cases
- Understand AWS service naming conventions and patterns
- Apply the AWS Well-Architected Framework pillars to service selection
- Recognize how different AWS services work together in typical architectures

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

AWS offers over 200 services—a number that grows every year. For a quality engineer entering the cloud space, this can feel overwhelming. However, you'll interact with perhaps 10-15 services regularly, and understanding the categories helps you quickly identify the right tool for any situation.

When investigating a production issue, you need to know: "Is this a compute problem (EC2), a database issue (RDS), a storage concern (S3), or a networking configuration (VPC)?" This knowledge transforms you from someone who waits for answers to someone who finds them.

## The Concept

### AWS Service Categories

AWS services are organized into categories based on their primary function:

```
┌─────────────────────────────────────────────────────────────────────┐
│                     AWS Service Categories                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  COMPUTE          STORAGE           DATABASE         NETWORKING      │
│  ────────         ───────           ────────         ──────────      │
│  EC2              S3                RDS              VPC             │
│  Lambda           EBS               DynamoDB         Route 53        │
│  ECS              EFS               ElastiCache      CloudFront      │
│  EKS              Glacier           Redshift         API Gateway     │
│  Lightsail        Storage Gateway   DocumentDB       ELB             │
│                                                                      │
│  SECURITY         MONITORING        INTEGRATION      DEV TOOLS       │
│  ────────         ──────────        ───────────      ─────────       │
│  IAM              CloudWatch        SQS              CodeCommit      │
│  KMS              CloudTrail        SNS              CodeBuild       │
│  WAF              X-Ray             Step Functions   CodeDeploy      │
│  Shield           Config            EventBridge      CodePipeline    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Compute Services

Compute services provide processing power for your applications:

| Service | Description | Use Case |
|---------|-------------|----------|
| **EC2** | Virtual servers in the cloud | Web servers, application servers, custom workloads |
| **Lambda** | Serverless compute (run code without servers) | Event-driven processing, APIs, scheduled tasks |
| **ECS** | Container orchestration service | Docker container management |
| **EKS** | Managed Kubernetes | Kubernetes workloads |
| **Lightsail** | Simplified virtual servers | Simple websites, small applications |
| **Elastic Beanstalk** | PaaS for web applications | Quick deployment without infrastructure management |

```
Compute Selection Guide:
                                          
          Need full control?              
                  │                        
         YES ─────┼───── NO               
          │       │       │                
          ▼       │       ▼                
         EC2      │    Need containers?    
                  │       │                
                  │  YES ─┼── NO           
                  │   │   │    │           
                  │   ▼   │    ▼           
                  │  ECS/ │  Lambda        
                  │  EKS  │  (serverless)  
                  │       │                
```

### Storage Services

Storage services provide different options based on access patterns and durability needs:

| Service | Type | Use Case |
|---------|------|----------|
| **S3** | Object storage | Files, backups, static websites, data lakes |
| **EBS** | Block storage | EC2 instance hard drives |
| **EFS** | File storage | Shared file systems across instances |
| **Glacier** | Archive storage | Long-term backup, compliance archives |
| **Storage Gateway** | Hybrid storage | Connect on-premises to cloud storage |

```
Storage Selection:

┌─────────────────────────────────────────────────────────────┐
│                     What type of data?                       │
├──────────────────┬──────────────────┬───────────────────────┤
│     Files/       │   Block device   │    Shared files       │
│    Objects       │   for instance   │  across instances     │
│        │         │        │         │         │             │
│        ▼         │        ▼         │         ▼             │
│       S3         │       EBS        │        EFS            │
│                  │                  │                       │
│  • Unlimited     │  • Attached to   │  • NFS protocol       │
│  • Web accessible│    single EC2    │  • Multiple EC2s      │
│  • Versioning    │  • SSD or HDD    │  • Elastic sizing     │
└──────────────────┴──────────────────┴───────────────────────┘
```

### Database Services

AWS offers managed database services for different data models:

| Service | Type | Use Case |
|---------|------|----------|
| **RDS** | Relational | MySQL, PostgreSQL, SQL Server, Oracle workloads |
| **DynamoDB** | NoSQL (Key-Value) | High-scale, low-latency applications |
| **ElastiCache** | In-memory | Caching, session management (Redis/Memcached) |
| **Redshift** | Data warehouse | Analytics, business intelligence |
| **DocumentDB** | Document DB | MongoDB-compatible workloads |
| **Aurora** | Cloud-native relational | High-performance MySQL/PostgreSQL |

```
Database Selection Guide:

Need relational data model?
         │
    YES ─┼── NO
     │   │    │
     ▼   │    ▼
   RDS/  │  Key-Value    Document    Time-series
  Aurora │  (DynamoDB)   (DocumentDB) (Timestream)
         │
    Need analytics?
         │
    YES ─┼── NO
     │   │    │
     ▼   │    ▼
  Redshift│  Need caching?
         │       │
         │  YES ─┼── NO
         │   │   │
         │   ▼   │
         │  ElastiCache
```

### Networking Services

Networking services connect and secure your AWS resources:

| Service | Description | Use Case |
|---------|-------------|----------|
| **VPC** | Virtual Private Cloud | Isolated network for your resources |
| **Route 53** | DNS service | Domain name management, health checks |
| **CloudFront** | Content Delivery Network | Cache content globally for low latency |
| **API Gateway** | Managed API endpoints | RESTful and WebSocket APIs |
| **ELB** | Load balancing | Distribute traffic across instances |
| **Direct Connect** | Dedicated connection | Private link from data center to AWS |

### AWS Service Naming Conventions

AWS follows patterns in naming that help you understand service purpose:

| Prefix/Pattern | Meaning | Examples |
|----------------|---------|----------|
| **Elastic** | Scalable, flexible | ElastiCache, Elastic Beanstalk, ELB |
| **Amazon** vs **AWS** | Consumer-facing vs Infrastructure | Amazon RDS vs AWS Lambda |
| **Cloud** | Monitoring/Management | CloudWatch, CloudTrail, CloudFront |
| **Simple** | Easy to use | S3 (Simple Storage Service), SQS, SNS |

### Typical Architecture Example

Here's how common services work together in a web application:

```
                                    ┌─────────────────┐
                                    │   Route 53      │
                                    │   (DNS)         │
                                    └────────┬────────┘
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │   CloudFront    │
                                    │   (CDN)         │
                                    └────────┬────────┘
                                             │
                     ┌───────────────────────┼───────────────────────┐
                     │                       │                       │
                     ▼                       ▼                       │
            ┌─────────────────┐    ┌─────────────────┐              │
            │      S3         │    │  Load Balancer  │              │
            │ (Static assets) │    │     (ELB)       │              │
            └─────────────────┘    └────────┬────────┘              │
                                            │                       │
                              ┌─────────────┴─────────────┐        │
                              │                           │        │
                              ▼                           ▼        │
                     ┌─────────────────┐        ┌─────────────────┐│
                     │      EC2        │        │      EC2        ││
                     │   (Web Server)  │        │   (Web Server)  ││
                     │    AZ-1a        │        │    AZ-1b        ││
                     └────────┬────────┘        └────────┬────────┘│
                              │                          │         │
                              └────────────┬─────────────┘         │
                                           │                       │
                              ┌────────────┴─────────────┐        │
                              │                          │        │
                              ▼                          ▼        │
                     ┌─────────────────┐        ┌─────────────────┐
                     │   ElastiCache   │        │      RDS        │
                     │   (Caching)     │        │   (Database)    │
                     └─────────────────┘        └─────────────────┘
```

### AWS Well-Architected Framework

The Well-Architected Framework provides best practices for building reliable, secure, and efficient infrastructure:

```
┌─────────────────────────────────────────────────────────────────┐
│              AWS Well-Architected Framework                      │
│                      (6 Pillars)                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │ OPERATIONAL │  │  SECURITY   │  │      RELIABILITY        │ │
│  │ EXCELLENCE  │  │             │  │                         │ │
│  ├─────────────┤  ├─────────────┤  ├─────────────────────────┤ │
│  │ Automate    │  │ Least       │  │ Recover from failures   │ │
│  │ operations  │  │ privilege   │  │ Scale to meet demand    │ │
│  │ Learn from  │  │ Encryption  │  │ Multi-AZ deployments    │ │
│  │ failures    │  │ Traceability│  │ Test recovery           │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
│                                                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │ PERFORMANCE │  │    COST     │  │    SUSTAINABILITY       │ │
│  │ EFFICIENCY  │  │ OPTIMIZATION│  │                         │ │
│  ├─────────────┤  ├─────────────┤  ├─────────────────────────┤ │
│  │ Right-size  │  │ Pay only for│  │ Minimize environmental  │ │
│  │ resources   │  │ what you use│  │ impact                  │ │
│  │ Monitor     │  │ Reserved    │  │ Efficient resource use  │ │
│  │ performance │  │ capacity    │  │ Managed services        │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Service Selection by Use Case

| Use Case | Primary Services | Why |
|----------|-----------------|-----|
| Web application | EC2, RDS, ELB, S3 | Traditional scalable architecture |
| Serverless API | Lambda, API Gateway, DynamoDB | No servers to manage |
| Big Data processing | EMR, S3, Redshift | Scalable data processing |
| Machine Learning | SageMaker, S3 | ML model training and deployment |
| CI/CD Pipeline | CodePipeline, CodeBuild, ECR | Automated deployments |
| Static website | S3, CloudFront, Route 53 | Low-cost, high-performance |

## Code Examples

### Listing Available Services in a Region

```bash
# List all AWS services (via the pricing API)
aws pricing describe-services --region us-east-1 \
    --query 'Services[].ServiceCode' --output text

# Check if a service is available
aws ssm get-parameters-by-path \
    --path /aws/service/global-infrastructure/services \
    --query 'Parameters[].Name' --output text
```

### Exploring Service Quotas

```python
import boto3

# Service Quotas client
sq = boto3.client('service-quotas', region_name='us-east-1')

# List quotas for EC2
paginator = sq.get_paginator('list_service_quotas')
for page in paginator.paginate(ServiceCode='ec2'):
    for quota in page['Quotas']:
        print(f"{quota['QuotaName']}: {quota['Value']}")
```

### Describing Resources Across Services

```bash
# Describe EC2 instances
aws ec2 describe-instances --query 'Reservations[].Instances[].[InstanceId,State.Name]'

# List S3 buckets
aws s3api list-buckets --query 'Buckets[].Name'

# Describe RDS instances
aws rds describe-db-instances --query 'DBInstances[].[DBInstanceIdentifier,DBInstanceStatus]'

# List Lambda functions
aws lambda list-functions --query 'Functions[].FunctionName'
```

### Resource Tagging (Organization Best Practice)

```bash
# Tag an EC2 instance
aws ec2 create-tags \
    --resources i-1234567890abcdef0 \
    --tags Key=Environment,Value=Production Key=Project,Value=WebApp

# Find all resources with a specific tag
aws resourcegroupstaggingapi get-resources \
    --tag-filters Key=Environment,Values=Production \
    --query 'ResourceTagMappingList[].ResourceARN'
```

### Python: Multi-Service Resource Inventory

```python
import boto3

def get_resource_inventory(region='us-east-1'):
    """Get a basic inventory of common AWS resources."""
    
    inventory = {
        'ec2_instances': [],
        's3_buckets': [],
        'rds_instances': [],
        'lambda_functions': []
    }
    
    # EC2 Instances
    ec2 = boto3.client('ec2', region_name=region)
    response = ec2.describe_instances()
    for reservation in response['Reservations']:
        for instance in reservation['Instances']:
            inventory['ec2_instances'].append({
                'id': instance['InstanceId'],
                'type': instance['InstanceType'],
                'state': instance['State']['Name']
            })
    
    # S3 Buckets (global service)
    s3 = boto3.client('s3')
    response = s3.list_buckets()
    for bucket in response['Buckets']:
        inventory['s3_buckets'].append(bucket['Name'])
    
    # RDS Instances
    rds = boto3.client('rds', region_name=region)
    response = rds.describe_db_instances()
    for db in response['DBInstances']:
        inventory['rds_instances'].append({
            'id': db['DBInstanceIdentifier'],
            'engine': db['Engine'],
            'status': db['DBInstanceStatus']
        })
    
    # Lambda Functions
    lambda_client = boto3.client('lambda', region_name=region)
    response = lambda_client.list_functions()
    for func in response['Functions']:
        inventory['lambda_functions'].append({
            'name': func['FunctionName'],
            'runtime': func['Runtime']
        })
    
    return inventory

# Print inventory summary
if __name__ == '__main__':
    inv = get_resource_inventory()
    print(f"EC2 Instances: {len(inv['ec2_instances'])}")
    print(f"S3 Buckets: {len(inv['s3_buckets'])}")
    print(f"RDS Instances: {len(inv['rds_instances'])}")
    print(f"Lambda Functions: {len(inv['lambda_functions'])}")
```

## Summary

- AWS services are organized into categories: **Compute**, **Storage**, **Database**, **Networking**, **Security**, and more
- **Compute services** range from full-control (EC2) to serverless (Lambda) to container orchestration (ECS/EKS)
- **Storage services** include object storage (S3), block storage (EBS), and file storage (EFS)
- **Database services** cover relational (RDS), NoSQL (DynamoDB), caching (ElastiCache), and analytics (Redshift)
- **Networking services** like VPC, ELB, and Route 53 connect and secure your infrastructure
- The **Well-Architected Framework** provides six pillars for building reliable, secure, efficient systems
- Understanding service naming patterns helps you quickly identify unfamiliar services

## Additional Resources

- [AWS Services Overview](https://aws.amazon.com/products/) - Complete catalog of AWS services with descriptions
- [AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/) - Best practices and design principles
- [AWS Architecture Center](https://aws.amazon.com/architecture/) - Reference architectures and diagrams

