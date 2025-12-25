# Amazon Elastic Block Store (EBS)

## Learning Objectives

- Explain what Amazon EBS is and how it differs from instance store
- Identify EBS volume types and select appropriate types for workloads
- Understand IOPS and throughput performance characteristics
- Create, attach, and manage EBS snapshots for backup
- Implement EBS encryption for data protection
- Resize volumes without downtime

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

EBS volumes are the hard drives of EC2 instances. When you launch an EC2 instance, the root volume where the operating system lives is an EBS volume. When you run databases, store application logs, or persist any data on EC2, you're using EBS.

As a quality engineer, understanding EBS helps you investigate performance issues (is the disk slow?), ensure data persistence across deployments, and verify backup procedures. When CI/CD pipelines deploy new application versions, understanding storage helps you ensure data integrity.

## The Concept

### What is Amazon EBS?

**Amazon Elastic Block Store (EBS)** provides persistent block-level storage volumes for EC2 instances. Unlike instance store (temporary storage), EBS volumes persist independently of the instance lifecycle.

```
┌─────────────────────────────────────────────────────────────────────┐
│                    EBS vs Instance Store                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   EBS VOLUMES                        INSTANCE STORE                  │
│   ───────────                        ──────────────                  │
│   ✓ Persistent storage              ✗ Ephemeral (temporary)         │
│   ✓ Survives instance stop/start    ✗ Data lost on stop/terminate   │
│   ✓ Can be detached and reattached  ✗ Fixed to instance             │
│   ✓ Snapshots for backup            ✗ No snapshot capability        │
│   ✓ Encryption available            ✗ Limited encryption options    │
│                                                                      │
│   Use EBS when:                     Use Instance Store when:        │
│   • Data must persist               • Temporary data (cache, buffer)│
│   • Database storage                • Data replicated elsewhere     │
│   • Application files               • Need highest I/O performance  │
│   • Boot volumes                    • Cost optimization             │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### EBS Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                      EBS Architecture                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Availability Zone: us-east-1a                                     │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                                                              │   │
│   │   ┌─────────────────┐                                        │   │
│   │   │  EC2 Instance   │                                        │   │
│   │   │                 │                                        │   │
│   │   │  ┌───────────┐  │     Network     ┌─────────────────┐   │   │
│   │   │  │ /dev/xvda │──┼─────attached────│  EBS Volume     │   │   │
│   │   │  │ (root)    │  │                 │  (Root, 20GB)   │   │   │
│   │   │  └───────────┘  │                 └─────────────────┘   │   │
│   │   │                 │                                        │   │
│   │   │  ┌───────────┐  │                 ┌─────────────────┐   │   │
│   │   │  │ /dev/xvdb │──┼─────attached────│  EBS Volume     │   │   │
│   │   │  │ (data)    │  │                 │  (Data, 100GB)  │   │   │
│   │   │  └───────────┘  │                 └─────────────────┘   │   │
│   │   │                 │                                        │   │
│   │   └─────────────────┘                                        │   │
│   │                                                              │   │
│   │   Key Points:                                                │   │
│   │   • Volumes exist in single AZ (same as instance)           │   │
│   │   • Network-attached (slight latency vs local disk)         │   │
│   │   • Can attach multiple volumes to one instance             │   │
│   │   • Can detach and reattach to different instance           │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### EBS Volume Types

EBS offers different volume types optimized for different workloads:

| Type | Name | Use Case | Max IOPS | Max Throughput |
|------|------|----------|----------|----------------|
| **gp3** | General Purpose SSD | Most workloads | 16,000 | 1,000 MB/s |
| **gp2** | General Purpose SSD | Boot volumes, dev/test | 16,000 | 250 MB/s |
| **io2** | Provisioned IOPS SSD | Critical databases | 256,000 | 4,000 MB/s |
| **io1** | Provisioned IOPS SSD | High-performance databases | 64,000 | 1,000 MB/s |
| **st1** | Throughput Optimized HDD | Big data, log processing | 500 | 500 MB/s |
| **sc1** | Cold HDD | Infrequent access | 250 | 250 MB/s |

```
┌─────────────────────────────────────────────────────────────────────┐
│                   EBS Volume Type Selection                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Need boot volume?                                                  │
│        │                                                             │
│   YES ─┼─ YES ──▶  gp3 (default) or io2 (critical production)      │
│        │                                                             │
│   Need SSD performance?                                              │
│        │                                                             │
│   YES ─┼───▶  Need guaranteed IOPS?                                 │
│        │            │                                                │
│        │       YES ─┼──▶ io2/io1 (Provisioned IOPS)                 │
│        │            │                                                │
│        │        NO ─┼──▶ gp3 (best value SSD)                       │
│        │                                                             │
│    NO ─┼───▶  Need high throughput?                                 │
│        │            │                                                │
│        │       YES ─┼──▶ st1 (Throughput Optimized)                 │
│        │            │                                                │
│        │        NO ─┼──▶ sc1 (Cold HDD, lowest cost)                │
│                                                                      │
│   Cost (per GB/month, us-east-1 approximate):                       │
│   gp3: $0.08 | gp2: $0.10 | io2: $0.125+ | st1: $0.045 | sc1: $0.015│
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Understanding IOPS and Throughput

**IOPS (Input/Output Operations Per Second):** Number of read/write operations per second. Important for databases with many small transactions.

**Throughput (MB/s):** Amount of data transferred per second. Important for streaming large files or sequential access.

```
┌─────────────────────────────────────────────────────────────────────┐
│                    IOPS vs Throughput                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   HIGH IOPS WORKLOAD                 HIGH THROUGHPUT WORKLOAD       │
│   ──────────────────                 ────────────────────────       │
│   │▮│▮│▮│▮│▮│▮│▮│▮│                 │████████████████████│         │
│   │▮│▮│▮│▮│▮│▮│▮│▮│                 │████████████████████│         │
│   │▮│▮│▮│▮│▮│▮│▮│▮│                 │████████████████████│         │
│   │▮│▮│▮│▮│▮│▮│▮│▮│                 │████████████████████│         │
│                                                                      │
│   Many small operations              Few large operations            │
│   (database queries)                 (video processing)              │
│                                                                      │
│   Examples:                          Examples:                       │
│   • MySQL transactions               • Log processing                │
│   • MongoDB operations               • Data warehouse loads          │
│   • Random read/write               • Backup/restore                 │
│                                                                      │
│   Volume choice: gp3, io2           Volume choice: st1, gp3         │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### gp3 Configuration

**gp3** is the recommended general-purpose SSD, with independently configurable IOPS and throughput:

```
┌─────────────────────────────────────────────────────────────────────┐
│                      gp3 Configuration                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   BASELINE (included in price):                                     │
│   • 3,000 IOPS                                                       │
│   • 125 MB/s throughput                                              │
│                                                                      │
│   CUSTOMIZABLE (additional cost):                                    │
│   • Up to 16,000 IOPS                                               │
│   • Up to 1,000 MB/s throughput                                      │
│                                                                      │
│   Size: 1 GB to 16 TB                                               │
│                                                                      │
│   Example configurations:                                            │
│   ┌────────────────────────────────────────────────────────────┐    │
│   │  Web Server       │  Database          │  High Performance │    │
│   │  100 GB           │  500 GB            │  1 TB             │    │
│   │  3,000 IOPS       │  6,000 IOPS        │  16,000 IOPS      │    │
│   │  125 MB/s         │  500 MB/s          │  1,000 MB/s       │    │
│   │  ~$8/month        │  ~$60/month        │  ~$200/month      │    │
│   └────────────────────────────────────────────────────────────┘    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### EBS Snapshots

**Snapshots** are point-in-time backups of EBS volumes stored in S3:

```
┌─────────────────────────────────────────────────────────────────────┐
│                      EBS Snapshots                                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Volume                        Snapshots (incremental)             │
│   ┌──────────┐                                                       │
│   │██████████│  Day 1   ───▶   [Snap-1: Full copy of data]         │
│   │██████████│                         │                             │
│   │          │                         │                             │
│   │          │                         ▼                             │
│   └──────────┘                                                       │
│                                                                       │
│   ┌──────────┐                                                       │
│   │██████████│  Day 2   ───▶   [Snap-2: Only changed blocks]       │
│   │██████████│                         │                             │
│   │░░░░░░░░░░│  (new)                  │                             │
│   │          │                         ▼                             │
│   └──────────┘                                                       │
│                                                                       │
│   ┌──────────┐                                                       │
│   │██████████│  Day 3   ───▶   [Snap-3: Only changed blocks]       │
│   │██████████│                                                       │
│   │░░░░░░░░░░│                                                       │
│   │▓▓▓▓▓▓▓▓▓▓│  (new)                                               │
│   └──────────┘                                                       │
│                                                                      │
│   Benefits:                                                          │
│   • Incremental: Only changed data stored (cost efficient)          │
│   • Can create new volumes from snapshots                           │
│   • Can copy snapshots across regions                               │
│   • Each snapshot is independent (can delete any without issues)    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### EBS Encryption

EBS encryption protects data at rest and in transit:

```
┌─────────────────────────────────────────────────────────────────────┐
│                      EBS Encryption                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  EC2 Instance                                                │   │
│   │                                                              │   │
│   │                      Encrypted                               │   │
│   │                      in transit                              │   │
│   │           ┌──────────────────────────────────┐              │   │
│   │           │                                  │              │   │
│   │           ▼                                  │              │   │
│   │   ┌───────────────┐                  ┌───────────────┐      │   │
│   │   │  EBS Volume   │                  │  Snapshot     │      │   │
│   │   │  (Encrypted   │      Copy        │  (Encrypted   │      │   │
│   │   │   at rest)    │─────────────────▶│   at rest)    │      │   │
│   │   └───────────────┘                  └───────────────┘      │   │
│   │           │                                                  │   │
│   │           │  Uses AWS KMS                                   │   │
│   │           │  (Key Management Service)                       │   │
│   │           ▼                                                  │   │
│   │   ┌───────────────┐                                         │   │
│   │   │  KMS Key      │                                         │   │
│   │   │  (CMK)        │                                         │   │
│   │   └───────────────┘                                         │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   What's encrypted:                                                  │
│   ✓ Data at rest on the volume                                      │
│   ✓ Data moving between volume and instance                         │
│   ✓ All snapshots created from volume                               │
│   ✓ All volumes created from encrypted snapshots                    │
│                                                                      │
│   No performance impact: Encryption handled transparently           │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Volume Resizing

You can resize EBS volumes without downtime:

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Volume Resizing Steps                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Step 1: Modify Volume (AWS Console/CLI)                           │
│   ────────────────────────────────────────                          │
│   • Increase size (cannot decrease)                                 │
│   • Change type (gp2 → gp3)                                        │
│   • Adjust IOPS/throughput                                          │
│                                                                      │
│   ┌──────────┐             ┌──────────────────┐                     │
│   │  100 GB  │   Modify    │     200 GB       │                     │
│   │   gp2    │ ──────────▶ │      gp3         │                     │
│   └──────────┘             └──────────────────┘                     │
│                                                                      │
│   Step 2: Wait for Optimization                                     │
│   ───────────────────────────────                                   │
│   • State changes: modifying → optimizing → completed               │
│   • Volume usable during optimization                               │
│   • Can take hours for large changes                                │
│                                                                      │
│   Step 3: Extend Filesystem (on instance)                           │
│   ─────────────────────────────────────────                         │
│   • AWS resizes the volume                                          │
│   • You must extend the filesystem to use new space                 │
│                                                                      │
│   # Check current size                                              │
│   lsblk                                                              │
│                                                                      │
│   # Extend partition (if needed)                                    │
│   sudo growpart /dev/xvda 1                                         │
│                                                                      │
│   # Extend filesystem                                               │
│   # For ext4:                                                        │
│   sudo resize2fs /dev/xvda1                                         │
│   # For XFS:                                                         │
│   sudo xfs_growfs /                                                 │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Create an EBS Volume

```bash
# Create gp3 volume
aws ec2 create-volume \
    --availability-zone us-east-1a \
    --size 100 \
    --volume-type gp3 \
    --iops 3000 \
    --throughput 125 \
    --encrypted \
    --tag-specifications 'ResourceType=volume,Tags=[{Key=Name,Value=data-volume}]'

# Create io2 volume for database
aws ec2 create-volume \
    --availability-zone us-east-1a \
    --size 500 \
    --volume-type io2 \
    --iops 10000 \
    --encrypted \
    --tag-specifications 'ResourceType=volume,Tags=[{Key=Name,Value=database-volume}]'
```

### Attach Volume to Instance

```bash
# Attach volume
aws ec2 attach-volume \
    --volume-id vol-0123456789abcdef0 \
    --instance-id i-0123456789abcdef0 \
    --device /dev/xvdb

# On the instance, format and mount the volume:
# sudo mkfs -t ext4 /dev/xvdb
# sudo mkdir /data
# sudo mount /dev/xvdb /data
```

### Create and Manage Snapshots

```bash
# Create snapshot
aws ec2 create-snapshot \
    --volume-id vol-0123456789abcdef0 \
    --description "Daily backup $(date +%Y-%m-%d)" \
    --tag-specifications 'ResourceType=snapshot,Tags=[{Key=Name,Value=daily-backup}]'

# List snapshots
aws ec2 describe-snapshots \
    --owner-ids self \
    --query 'Snapshots[].[SnapshotId,VolumeSize,StartTime,State]' \
    --output table

# Create volume from snapshot
aws ec2 create-volume \
    --availability-zone us-east-1a \
    --snapshot-id snap-0123456789abcdef0 \
    --volume-type gp3

# Copy snapshot to another region
aws ec2 copy-snapshot \
    --source-region us-east-1 \
    --source-snapshot-id snap-0123456789abcdef0 \
    --description "DR copy" \
    --region eu-west-1
```

### Modify Volume

```bash
# Resize volume (increase from 100GB to 200GB)
aws ec2 modify-volume \
    --volume-id vol-0123456789abcdef0 \
    --size 200

# Change volume type and performance
aws ec2 modify-volume \
    --volume-id vol-0123456789abcdef0 \
    --volume-type gp3 \
    --iops 6000 \
    --throughput 500

# Check modification progress
aws ec2 describe-volumes-modifications \
    --volume-ids vol-0123456789abcdef0
```

### Python: EBS Management

```python
import boto3
from datetime import datetime, timedelta

def create_snapshot_with_retention(volume_id, retention_days=7):
    """Create snapshot and clean up old ones."""
    
    ec2 = boto3.client('ec2')
    
    # Create new snapshot
    timestamp = datetime.now().strftime('%Y-%m-%d-%H%M')
    response = ec2.create_snapshot(
        VolumeId=volume_id,
        Description=f"Automated backup {timestamp}",
        TagSpecifications=[
            {
                'ResourceType': 'snapshot',
                'Tags': [
                    {'Key': 'Name', 'Value': f'backup-{timestamp}'},
                    {'Key': 'AutoBackup', 'Value': 'true'},
                    {'Key': 'VolumeId', 'Value': volume_id}
                ]
            }
        ]
    )
    
    new_snapshot_id = response['SnapshotId']
    print(f"Created snapshot: {new_snapshot_id}")
    
    # Clean up old snapshots
    cutoff_date = datetime.now() - timedelta(days=retention_days)
    
    old_snapshots = ec2.describe_snapshots(
        Filters=[
            {'Name': 'tag:AutoBackup', 'Values': ['true']},
            {'Name': 'tag:VolumeId', 'Values': [volume_id]}
        ],
        OwnerIds=['self']
    )
    
    for snapshot in old_snapshots['Snapshots']:
        if snapshot['StartTime'].replace(tzinfo=None) < cutoff_date:
            print(f"Deleting old snapshot: {snapshot['SnapshotId']}")
            ec2.delete_snapshot(SnapshotId=snapshot['SnapshotId'])
    
    return new_snapshot_id

def get_volume_metrics(volume_id):
    """Get CloudWatch metrics for an EBS volume."""
    
    cloudwatch = boto3.client('cloudwatch')
    
    end_time = datetime.utcnow()
    start_time = end_time - timedelta(hours=1)
    
    metrics = ['VolumeReadOps', 'VolumeWriteOps', 'VolumeReadBytes', 'VolumeWriteBytes']
    
    results = {}
    for metric_name in metrics:
        response = cloudwatch.get_metric_statistics(
            Namespace='AWS/EBS',
            MetricName=metric_name,
            Dimensions=[{'Name': 'VolumeId', 'Value': volume_id}],
            StartTime=start_time,
            EndTime=end_time,
            Period=300,
            Statistics=['Average', 'Sum']
        )
        
        if response['Datapoints']:
            latest = sorted(response['Datapoints'], key=lambda x: x['Timestamp'])[-1]
            results[metric_name] = latest
    
    return results

# Example usage
# snapshot_id = create_snapshot_with_retention('vol-0123456789abcdef0')
# metrics = get_volume_metrics('vol-0123456789abcdef0')
```

### Extend Filesystem After Resize

```bash
#!/bin/bash
# Run on EC2 instance after modifying volume

# Check current state
echo "Current disk state:"
lsblk
df -h

# Extend the partition (for devices with partition table)
sudo growpart /dev/xvda 1

# Detect filesystem type and extend
FSTYPE=$(df -T / | tail -1 | awk '{print $2}')
DEVICE=$(df / | tail -1 | awk '{print $1}')

if [ "$FSTYPE" == "ext4" ]; then
    echo "Extending ext4 filesystem..."
    sudo resize2fs $DEVICE
elif [ "$FSTYPE" == "xfs" ]; then
    echo "Extending XFS filesystem..."
    sudo xfs_growfs /
fi

# Verify
echo "After resize:"
df -h
```

## Summary

- **EBS** provides persistent block storage that survives instance lifecycle (unlike instance store)
- **Volume types**: gp3 (general purpose), io2 (high IOPS), st1 (throughput), sc1 (cold storage)
- **gp3** is recommended for most workloads with configurable IOPS and throughput
- **IOPS** matters for transaction-heavy workloads; **throughput** matters for streaming large data
- **Snapshots** are incremental backups stored in S3; use for backup and cross-region copies
- **Encryption** is transparent with no performance impact; uses AWS KMS
- **Resizing** is possible without downtime, but filesystem extension required after volume modification

## Additional Resources

- [Amazon EBS Documentation](https://docs.aws.amazon.com/ebs/) - Official comprehensive guide
- [EBS Volume Types](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ebs-volume-types.html) - Detailed specifications
- [EBS Pricing](https://aws.amazon.com/ebs/pricing/) - Cost information for all volume types

