# AWS Cheat Sheet

## Core Services Overview

| Service | Type | Purpose |
|---------|------|---------|
| EC2 | Compute | Virtual servers |
| S3 | Storage | Object storage |
| RDS | Database | Managed relational DB |
| Lambda | Compute | Serverless functions |
| VPC | Networking | Virtual network |
| IAM | Security | Access management |
| CloudWatch | Monitoring | Logs and metrics |

---

## EC2 (Elastic Compute Cloud)

### Instance Types
| Type | Use Case |
|------|----------|
| t2/t3 | General purpose, burstable |
| m5/m6 | General purpose, balanced |
| c5/c6 | Compute optimized |
| r5/r6 | Memory optimized |
| i3 | Storage optimized |

### Common Commands (AWS CLI)
```bash
# List instances
aws ec2 describe-instances

# Start instance
aws ec2 start-instances --instance-ids i-1234567890abcdef0

# Stop instance
aws ec2 stop-instances --instance-ids i-1234567890abcdef0

# Terminate instance
aws ec2 terminate-instances --instance-ids i-1234567890abcdef0

# Create instance
aws ec2 run-instances \
    --image-id ami-12345678 \
    --instance-type t2.micro \
    --key-name my-key \
    --security-group-ids sg-12345678

# Get public IP
aws ec2 describe-instances --instance-ids i-12345 \
    --query 'Reservations[0].Instances[0].PublicIpAddress'
```

### Connect to EC2
```bash
# SSH (Linux)
ssh -i "key.pem" ec2-user@<public-ip>

# Common usernames by AMI
# Amazon Linux: ec2-user
# Ubuntu: ubuntu
# RHEL: ec2-user
# Windows: Administrator (use RDP)
```

---

## S3 (Simple Storage Service)

### Key Concepts
- **Bucket**: Container for objects
- **Object**: File + metadata
- **Key**: Unique identifier (path)
- **Region**: Geographic location

### Common Commands
```bash
# List buckets
aws s3 ls

# List bucket contents
aws s3 ls s3://my-bucket

# Copy file to S3
aws s3 cp file.txt s3://my-bucket/

# Copy file from S3
aws s3 cp s3://my-bucket/file.txt ./

# Sync directory
aws s3 sync ./local-folder s3://my-bucket/folder

# Remove file
aws s3 rm s3://my-bucket/file.txt

# Remove bucket (must be empty)
aws s3 rb s3://my-bucket

# Remove bucket and all contents
aws s3 rb s3://my-bucket --force

# Create bucket
aws s3 mb s3://my-new-bucket --region us-east-1
```

### Storage Classes
| Class | Use Case |
|-------|----------|
| Standard | Frequently accessed |
| Intelligent-Tiering | Unknown access patterns |
| Standard-IA | Infrequent access |
| One Zone-IA | Infrequent, non-critical |
| Glacier | Archive (minutes retrieval) |
| Glacier Deep Archive | Long-term archive (hours) |

---

## RDS (Relational Database Service)

### Supported Engines
- MySQL, PostgreSQL, MariaDB
- Oracle, SQL Server
- Amazon Aurora

### Key Features
- Automated backups
- Multi-AZ deployment
- Read replicas
- Automated patching

### Common Commands
```bash
# List DB instances
aws rds describe-db-instances

# Create DB instance
aws rds create-db-instance \
    --db-instance-identifier mydb \
    --db-instance-class db.t2.micro \
    --engine mysql \
    --master-username admin \
    --master-user-password password123 \
    --allocated-storage 20

# Create snapshot
aws rds create-db-snapshot \
    --db-instance-identifier mydb \
    --db-snapshot-identifier my-snapshot

# Delete instance
aws rds delete-db-instance \
    --db-instance-identifier mydb \
    --skip-final-snapshot
```

---

## IAM (Identity and Access Management)

### Key Concepts
- **User**: Individual identity
- **Group**: Collection of users
- **Role**: Assumed identity (for services)
- **Policy**: Permission document (JSON)

### Policy Structure
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:GetObject",
                "s3:PutObject"
            ],
            "Resource": "arn:aws:s3:::my-bucket/*"
        }
    ]
}
```

### Common Commands
```bash
# List users
aws iam list-users

# Create user
aws iam create-user --user-name newuser

# Create access key
aws iam create-access-key --user-name newuser

# Attach policy to user
aws iam attach-user-policy \
    --user-name newuser \
    --policy-arn arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess
```

---

## VPC (Virtual Private Cloud)

### Components
| Component | Description |
|-----------|-------------|
| VPC | Virtual network |
| Subnet | IP range within VPC |
| Route Table | Traffic routing rules |
| Internet Gateway | Internet connection |
| NAT Gateway | Outbound for private subnets |
| Security Group | Instance firewall |
| NACL | Subnet firewall |

### Security Groups vs NACLs
| Feature | Security Group | NACL |
|---------|---------------|------|
| Level | Instance | Subnet |
| Rules | Allow only | Allow and Deny |
| State | Stateful | Stateless |
| Evaluation | All rules | Order matters |

---

## CloudWatch

### Key Features
- Metrics collection
- Log aggregation
- Alarms and notifications
- Dashboards

### Common Commands
```bash
# List metrics
aws cloudwatch list-metrics --namespace AWS/EC2

# Get metric statistics
aws cloudwatch get-metric-statistics \
    --namespace AWS/EC2 \
    --metric-name CPUUtilization \
    --dimensions Name=InstanceId,Value=i-12345 \
    --start-time 2024-01-01T00:00:00Z \
    --end-time 2024-01-02T00:00:00Z \
    --period 3600 \
    --statistics Average

# View log groups
aws logs describe-log-groups

# View log streams
aws logs describe-log-streams --log-group-name my-log-group

# Get log events
aws logs get-log-events \
    --log-group-name my-log-group \
    --log-stream-name my-stream
```

---

## AWS CLI Configuration

```bash
# Configure credentials
aws configure
# Enter: Access Key, Secret Key, Region, Output format

# Use named profile
aws configure --profile myprofile
aws s3 ls --profile myprofile

# Set default region
export AWS_DEFAULT_REGION=us-east-1

# Environment variables
export AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
export AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
```

---

## Common ARN Format

```
arn:aws:service:region:account-id:resource-type/resource-id

Examples:
arn:aws:s3:::my-bucket
arn:aws:ec2:us-east-1:123456789012:instance/i-12345
arn:aws:iam::123456789012:user/johndoe
```

---

## Pricing Tips

| Strategy | Description |
|----------|-------------|
| On-Demand | Pay per hour, no commitment |
| Reserved | 1-3 year commitment, up to 75% off |
| Spot | Bid for unused capacity, up to 90% off |
| Savings Plans | Flexible commitment pricing |
| Free Tier | 12 months free for basic usage |
