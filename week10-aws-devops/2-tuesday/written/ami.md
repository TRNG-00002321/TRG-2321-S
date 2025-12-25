# Amazon Machine Images (AMI)

## Learning Objectives

- Define what an Amazon Machine Image (AMI) is and its components
- Distinguish between public, private, and AWS Marketplace AMIs
- Create custom AMIs from existing EC2 instances
- Understand the AMI lifecycle and management best practices
- Implement the golden image pattern for consistent deployments
- Copy and share AMIs across regions and accounts

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

When you deploy applications through CI/CD pipelines, consistency matters. An AMI captures an exact snapshot of a configured system—operating system, applications, configurations, and more. Instead of manually installing software on each new instance, you launch from an AMI and get an identical copy every time.

As a quality engineer, understanding AMIs helps you ensure test environments match production, investigate "works in dev, fails in prod" issues, and validate that the correct application version is deployed. AMIs are the foundation of repeatable, reliable deployments.

## The Concept

### What is an AMI?

An **Amazon Machine Image (AMI)** is a template that contains the software configuration (operating system, application server, applications) required to launch an EC2 instance. Think of it as a "blueprint" for your server.

```
┌─────────────────────────────────────────────────────────────────────┐
│                      AMI Components                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   AMI = Template for EC2 Instances                                  │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                                                              │   │
│   │   ROOT VOLUME TEMPLATE                                       │   │
│   │   ├── Operating System (e.g., Amazon Linux 2)               │   │
│   │   ├── Installed Software (e.g., Apache, Java)               │   │
│   │   ├── Configuration Files                                    │   │
│   │   └── Application Code                                       │   │
│   │                                                              │   │
│   ├───────────────────────────────────────────────────────────────   │
│   │                                                              │   │
│   │   LAUNCH PERMISSIONS                                         │   │
│   │   └── Who can use this AMI (public, private, shared)        │   │
│   │                                                              │   │
│   ├───────────────────────────────────────────────────────────────   │
│   │                                                              │   │
│   │   BLOCK DEVICE MAPPING                                       │   │
│   │   └── What volumes to attach on launch                      │   │
│   │       (root volume, additional EBS volumes)                  │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### AMI Types

| Type | Description | Use Case |
|------|-------------|----------|
| **Public AMIs** | Available to all AWS users | Quick starts, base images |
| **Private AMIs** | Only available to your account | Custom configurations |
| **Marketplace AMIs** | Sold by vendors (may have fees) | Pre-configured solutions |
| **Community AMIs** | Shared by AWS users | Specialized configurations |

### AMI Sources

```
┌─────────────────────────────────────────────────────────────────────┐
│                       AMI Sources                                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  AWS-PROVIDED AMIs                                           │   │
│   │  ─────────────────                                           │   │
│   │  • Amazon Linux 2/2023                                       │   │
│   │  • Ubuntu Server                                             │   │
│   │  • Red Hat Enterprise Linux                                  │   │
│   │  • Windows Server                                            │   │
│   │  • Deep Learning AMIs                                        │   │
│   │                                                              │   │
│   │  Benefits: Maintained by AWS, security patches, free tier   │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  AWS MARKETPLACE AMIs                                        │   │
│   │  ────────────────────                                        │   │
│   │  • Pre-built solutions (WordPress, Jira, etc.)              │   │
│   │  • Security appliances                                       │   │
│   │  • Enterprise software (SAP, Oracle)                         │   │
│   │                                                              │   │
│   │  Note: May include hourly software licensing fees            │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  CUSTOM AMIs                                                 │   │
│   │  ───────────                                                 │   │
│   │  • Created from your configured instances                    │   │
│   │  • Include your applications and configurations              │   │
│   │  • Foundation for golden image pattern                       │   │
│   │                                                              │   │
│   │  Use when: You need consistent, pre-configured instances    │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Creating Custom AMIs

Creating an AMI captures the current state of an instance:

```
┌─────────────────────────────────────────────────────────────────────┐
│                   Custom AMI Creation Process                        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Step 1: Configure Instance                                        │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  EC2 Instance                                                │   │
│   │  ├── Install OS updates                                      │   │
│   │  ├── Install application software                            │   │
│   │  ├── Configure security settings                             │   │
│   │  ├── Deploy application code                                 │   │
│   │  └── Test everything works                                   │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                          │                                          │
│                          ▼                                          │
│   Step 2: Create Image                                              │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Create Image (Console or CLI)                               │   │
│   │  ├── Specify name and description                            │   │
│   │  ├── Choose reboot behavior                                  │   │
│   │  │   └── Reboot (recommended): Ensures filesystem integrity │   │
│   │  │   └── No reboot: Faster but risk of inconsistent data    │   │
│   │  └── Wait for AMI status: "available"                       │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                          │                                          │
│                          ▼                                          │
│   Step 3: Launch New Instances                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  AMI: my-app-v1.0                                           │   │
│   │       │                                                      │   │
│   │       ├──▶ Instance 1 (Production AZ-1a)                    │   │
│   │       ├──▶ Instance 2 (Production AZ-1b)                    │   │
│   │       └──▶ Instance 3 (Test Environment)                    │   │
│   │                                                              │   │
│   │  All instances are identical copies!                         │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### The Golden Image Pattern

The **Golden Image Pattern** creates standardized, pre-approved AMIs for your organization:

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Golden Image Pattern                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    Build Pipeline                            │   │
│   │                                                              │   │
│   │   Base AMI ──▶ Install ──▶ Configure ──▶ Harden ──▶ Test   │   │
│   │   (AWS)        Software    Settings      Security    AMI    │   │
│   │                                                              │   │
│   │                                          ▼                   │   │
│   │                                    ┌──────────┐              │   │
│   │                                    │  Golden  │              │   │
│   │                                    │   AMI    │              │   │
│   │                                    │  v2.3.1  │              │   │
│   │                                    └──────────┘              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   Benefits:                                                          │
│   ✓ Consistent environments (dev = staging = prod)                  │
│   ✓ Security patches applied uniformly                              │
│   ✓ Fast instance launches (no configuration needed)                │
│   ✓ Immutable infrastructure (replace, don't update)                │
│   ✓ Version tracking and rollback capability                        │
│                                                                      │
│   Pipeline Integration:                                              │
│   • CI/CD builds new AMI on code changes                            │
│   • Automated testing validates AMI                                  │
│   • Auto Scaling uses latest approved AMI                           │
│   • Old AMIs retained for rollback                                   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### AMI Lifecycle Management

```
┌─────────────────────────────────────────────────────────────────────┐
│                    AMI Lifecycle                                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   CREATE                                                             │
│      │                                                               │
│      ▼                                                               │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Status: pending → available                                 │   │
│   │  • EBS snapshots created                                    │   │
│   │  • Metadata stored                                           │   │
│   │  • Takes minutes to hours (depends on size)                  │   │
│   └─────────────────────────────────────────────────────────────┘   │
│      │                                                               │
│      ▼                                                               │
│   USE                                                                │
│      │                                                               │
│      ▼                                                               │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  • Launch instances from AMI                                 │   │
│   │  • Share with other accounts                                │   │
│   │  • Copy to other regions                                    │   │
│   │  • Use in Auto Scaling groups                               │   │
│   │  • Use in CloudFormation templates                          │   │
│   └─────────────────────────────────────────────────────────────┘   │
│      │                                                               │
│      ▼                                                               │
│   DEPRECATE (optional)                                               │
│      │                                                               │
│      ▼                                                               │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  • Mark AMI as deprecated                                    │   │
│   │  • Set deprecation date                                     │   │
│   │  • Warns users but doesn't prevent use                      │   │
│   └─────────────────────────────────────────────────────────────┘   │
│      │                                                               │
│      ▼                                                               │
│   DEREGISTER                                                         │
│      │                                                               │
│      ▼                                                               │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  • AMI ID no longer usable                                  │   │
│   │  • Must manually delete associated snapshots               │   │
│   │  • Running instances unaffected                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Copying AMIs Across Regions

AMIs are region-specific. To use an AMI in another region, you must copy it:

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Cross-Region AMI Copy                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   us-east-1                          eu-west-1                      │
│   ┌─────────────────┐               ┌─────────────────┐             │
│   │   Original AMI  │    Copy       │   Copied AMI    │             │
│   │   ami-abc123    │──────────────▶│   ami-xyz789    │             │
│   │                 │               │   (new AMI ID)  │             │
│   └─────────────────┘               └─────────────────┘             │
│                                                                      │
│   Use Cases:                                                         │
│   • Disaster recovery in another region                             │
│   • Deploying same application globally                             │
│   • Migrating workloads between regions                             │
│                                                                      │
│   Notes:                                                             │
│   • New AMI gets a different AMI ID                                 │
│   • Snapshots are copied (may take time for large volumes)          │
│   • Original AMI unchanged                                          │
│   • Encryption can be changed during copy                           │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Sharing AMIs

You can share AMIs with specific AWS accounts:

```
┌─────────────────────────────────────────────────────────────────────┐
│                    AMI Sharing                                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Account A                         Account B                        │
│   (AMI Owner)                       (Granted Access)                 │
│   ┌─────────────────┐               ┌─────────────────┐             │
│   │   ami-abc123    │    Share      │   Can launch    │             │
│   │   my-webapp     │──────────────▶│   instances     │             │
│   │                 │               │   from AMI      │             │
│   └─────────────────┘               └─────────────────┘             │
│                                                                      │
│   Options:                                                           │
│   • Share with specific account IDs                                 │
│   • Make public (available to all)                                  │
│                                                                      │
│   Security Considerations:                                           │
│   • Shared AMIs can contain sensitive data                          │
│   • Remove credentials before sharing                               │
│   • Use encryption for sensitive AMIs                               │
│   • Audit who has access to your AMIs                               │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Find Available AMIs

```bash
# Find latest Amazon Linux 2 AMI
aws ec2 describe-images \
    --owners amazon \
    --filters "Name=name,Values=amzn2-ami-hvm-*-x86_64-gp2" \
              "Name=state,Values=available" \
    --query 'sort_by(Images, &CreationDate)[-1].[ImageId,Name,CreationDate]' \
    --output table

# Find Ubuntu 22.04 AMIs
aws ec2 describe-images \
    --owners 099720109477 \
    --filters "Name=name,Values=ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*" \
              "Name=state,Values=available" \
    --query 'sort_by(Images, &CreationDate)[-1].ImageId' \
    --output text
```

### Create AMI from Instance

```bash
# Create AMI (with reboot for consistency)
aws ec2 create-image \
    --instance-id i-0123456789abcdef0 \
    --name "my-webapp-v1.0-$(date +%Y%m%d)" \
    --description "Web application v1.0 with Apache and PHP" \
    --no-reboot false

# Create AMI without rebooting (faster but risky)
aws ec2 create-image \
    --instance-id i-0123456789abcdef0 \
    --name "my-webapp-v1.0-$(date +%Y%m%d)" \
    --no-reboot
```

### List Your AMIs

```bash
# List AMIs owned by your account
aws ec2 describe-images \
    --owners self \
    --query 'Images[].[ImageId,Name,State,CreationDate]' \
    --output table

# Get details of specific AMI
aws ec2 describe-images \
    --image-ids ami-0123456789abcdef0
```

### Copy AMI to Another Region

```bash
# Copy AMI to EU region
aws ec2 copy-image \
    --source-region us-east-1 \
    --source-image-id ami-0123456789abcdef0 \
    --name "my-webapp-v1.0-eu" \
    --region eu-west-1

# Copy with encryption
aws ec2 copy-image \
    --source-region us-east-1 \
    --source-image-id ami-0123456789abcdef0 \
    --name "my-webapp-v1.0-encrypted" \
    --encrypted \
    --kms-key-id alias/my-key \
    --region eu-west-1
```

### Share AMI with Another Account

```bash
# Share AMI with specific account
aws ec2 modify-image-attribute \
    --image-id ami-0123456789abcdef0 \
    --launch-permission "Add=[{UserId=123456789012}]"

# Remove sharing
aws ec2 modify-image-attribute \
    --image-id ami-0123456789abcdef0 \
    --launch-permission "Remove=[{UserId=123456789012}]"

# Make AMI public (caution!)
aws ec2 modify-image-attribute \
    --image-id ami-0123456789abcdef0 \
    --launch-permission "Add=[{Group=all}]"
```

### Deregister AMI and Delete Snapshots

```bash
# First, get the snapshot IDs associated with the AMI
aws ec2 describe-images \
    --image-ids ami-0123456789abcdef0 \
    --query 'Images[0].BlockDeviceMappings[*].Ebs.SnapshotId' \
    --output text

# Deregister the AMI
aws ec2 deregister-image \
    --image-id ami-0123456789abcdef0

# Delete associated snapshots (must do separately)
aws ec2 delete-snapshot --snapshot-id snap-0123456789abcdef0
```

### Python: AMI Management

```python
import boto3
from datetime import datetime, timedelta

def create_ami(instance_id, name_prefix):
    """Create an AMI from an EC2 instance."""
    
    ec2 = boto3.client('ec2')
    
    timestamp = datetime.now().strftime('%Y%m%d-%H%M%S')
    ami_name = f"{name_prefix}-{timestamp}"
    
    response = ec2.create_image(
        InstanceId=instance_id,
        Name=ami_name,
        Description=f"AMI created from {instance_id}",
        NoReboot=False,  # Reboot for consistency
        TagSpecifications=[
            {
                'ResourceType': 'image',
                'Tags': [
                    {'Key': 'CreatedBy', 'Value': 'automation'},
                    {'Key': 'SourceInstance', 'Value': instance_id}
                ]
            }
        ]
    )
    
    ami_id = response['ImageId']
    print(f"Creating AMI: {ami_id} ({ami_name})")
    
    # Wait for AMI to be available
    waiter = ec2.get_waiter('image_available')
    print("Waiting for AMI to be available...")
    waiter.wait(ImageIds=[ami_id])
    print("AMI is now available!")
    
    return ami_id

def cleanup_old_amis(name_prefix, days_old=30):
    """Deregister AMIs older than specified days."""
    
    ec2 = boto3.client('ec2')
    
    # Find AMIs matching prefix
    response = ec2.describe_images(
        Owners=['self'],
        Filters=[{'Name': 'name', 'Values': [f"{name_prefix}*"]}]
    )
    
    cutoff_date = datetime.now() - timedelta(days=days_old)
    deleted_count = 0
    
    for ami in response['Images']:
        creation_date = datetime.strptime(
            ami['CreationDate'], '%Y-%m-%dT%H:%M:%S.%fZ'
        )
        
        if creation_date < cutoff_date:
            ami_id = ami['ImageId']
            
            # Get associated snapshots
            snapshot_ids = [
                mapping['Ebs']['SnapshotId']
                for mapping in ami['BlockDeviceMappings']
                if 'Ebs' in mapping
            ]
            
            # Deregister AMI
            print(f"Deregistering old AMI: {ami_id}")
            ec2.deregister_image(ImageId=ami_id)
            
            # Delete snapshots
            for snap_id in snapshot_ids:
                print(f"  Deleting snapshot: {snap_id}")
                ec2.delete_snapshot(SnapshotId=snap_id)
            
            deleted_count += 1
    
    print(f"Cleaned up {deleted_count} old AMIs")

# Example usage
# ami_id = create_ami('i-0123456789abcdef0', 'my-webapp')
# cleanup_old_amis('my-webapp', days_old=30)
```

## Summary

- **AMIs** are templates containing OS, software, and configuration for launching EC2 instances
- **AMI sources** include AWS-provided, Marketplace, and custom (self-created) images
- **Creating custom AMIs** captures your configured instance for repeatable deployments
- **The golden image pattern** ensures consistent environments across dev, staging, and production
- **AMI lifecycle** includes creation, use, deprecation, and deregistration (with manual snapshot cleanup)
- **Cross-region copies** enable multi-region deployments and disaster recovery
- **Sharing** allows other AWS accounts to launch instances from your AMI

## Additional Resources

- [Amazon Machine Images (AMI) Documentation](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AMIs.html) - Official AWS guide
- [EC2 Image Builder](https://aws.amazon.com/image-builder/) - Automate AMI creation pipelines
- [AMI Design Best Practices](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/building-shared-amis.html) - Security and sharing guidelines

