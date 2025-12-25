# Introduction to AWS S3

## Learning Objectives

- Explain what Amazon S3 is and its key characteristics
- Understand buckets and objects as S3 fundamentals
- Compare S3 storage classes and select appropriate classes for use cases
- Implement versioning for object protection
- Configure lifecycle policies for automated storage management
- Control access using bucket policies and ACLs
- Set up S3 for static website hosting

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Amazon S3 (Simple Storage Service) is one of the most widely used AWS services. It stores everything from application logs and backup files to static websites and data lakes. In CI/CD pipelines, S3 stores build artifacts, deployment packages, and test reports. When investigating test failures, logs might be in S3. When deploying frontend applications, assets might be served from S3.

As a quality engineer, understanding S3 helps you access test artifacts, verify backup procedures, and validate data storage configurations. S3 is foundational infrastructure that touches almost every AWS deployment.

## The Concept

### What is Amazon S3?

**Amazon Simple Storage Service (S3)** is object storage built to store and retrieve any amount of data from anywhere. Unlike block storage (EBS) or file storage (EFS), S3 stores data as objects—complete files with metadata.

```
┌─────────────────────────────────────────────────────────────────────┐
│                      S3 Key Characteristics                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   DURABILITY             AVAILABILITY           SCALABILITY         │
│   ──────────             ────────────           ───────────         │
│   99.999999999%          99.99%                 Unlimited           │
│   (11 nines)             (per year)             storage             │
│                                                                      │
│   Data stored across     Built for high         No capacity         │
│   multiple facilities    availability           planning needed     │
│                                                                      │
│   ──────────────────────────────────────────────────────────────── │
│                                                                      │
│   SECURITY               COST                   PERFORMANCE         │
│   ────────               ────                   ───────────         │
│   Encryption at rest     Pay only for           High throughput     │
│   and in transit         what you use           and low latency     │
│                                                                      │
│   IAM, bucket policies,  No minimum fees        Parallel requests   │
│   ACLs, access points    or commitments         supported           │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Buckets and Objects

S3 organizes data into **buckets** containing **objects**:

```
┌─────────────────────────────────────────────────────────────────────┐
│                    S3 Structure                                      │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   BUCKET: my-company-data                                           │
│   ├── photos/                                                        │
│   │   ├── 2024/                                                     │
│   │   │   ├── january/                                              │
│   │   │   │   ├── photo1.jpg  ◄── OBJECT                           │
│   │   │   │   └── photo2.jpg  ◄── OBJECT                           │
│   │   │   └── february/                                             │
│   │   │       └── photo3.jpg  ◄── OBJECT                           │
│   ├── documents/                                                     │
│   │   ├── report.pdf          ◄── OBJECT                           │
│   │   └── data.csv            ◄── OBJECT                           │
│   └── logs/                                                          │
│       └── app-2024-01-15.log  ◄── OBJECT                           │
│                                                                      │
│   Note: "Folders" are just prefixes in object keys                  │
│   The actual key for photo1.jpg is:                                 │
│   photos/2024/january/photo1.jpg                                    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

**Bucket characteristics:**
- Globally unique name (across ALL AWS accounts)
- Created in a specific region
- Flat namespace (no true folders, just key prefixes)
- Unlimited objects per bucket
- Object size: 0 bytes to 5 TB

**Object components:**
- **Key:** Unique identifier within bucket (the "filename")
- **Value:** The actual data (up to 5 TB)
- **Metadata:** Key-value pairs describing the object
- **Version ID:** If versioning enabled

### S3 Storage Classes

S3 offers multiple storage classes optimized for different access patterns and costs:

```
┌─────────────────────────────────────────────────────────────────────┐
│                    S3 Storage Classes                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   CLASS                ACCESS PATTERN         COST (approx/GB/mo)   │
│   ─────                ──────────────         ───────────────────   │
│                                                                      │
│   S3 Standard          Frequent access        $0.023                 │
│                        General purpose                               │
│                                                                      │
│   S3 Standard-IA       Infrequent access      $0.0125                │
│                        Min 30 days, 128KB     + retrieval fee        │
│                                                                      │
│   S3 One Zone-IA       Infrequent, single AZ  $0.01                  │
│                        Less durability        + retrieval fee        │
│                                                                      │
│   S3 Glacier Instant   Archive, instant       $0.004                 │
│   Retrieval            access                 + retrieval fee        │
│                                                                      │
│   S3 Glacier Flexible  Archive, minutes       $0.0036                │
│   Retrieval            to hours retrieval     + retrieval fee        │
│                                                                      │
│   S3 Glacier Deep      Long-term archive      $0.00099               │
│   Archive              12+ hour retrieval     + retrieval fee        │
│                                                                      │
│   S3 Intelligent-      Unknown access         $0.023 + monitoring    │
│   Tiering              Auto-moves data        fee                    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

**Storage Class Selection Guide:**

```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                      │
│   How often will data be accessed?                                  │
│                                                                      │
│   Frequently (daily/weekly)                                         │
│       └──▶ S3 Standard                                              │
│                                                                      │
│   Infrequently (monthly)                                            │
│       └──▶ Need multi-AZ durability?                               │
│               YES ──▶ S3 Standard-IA                               │
│               NO  ──▶ S3 One Zone-IA                               │
│                                                                      │
│   Rarely (yearly)                                                    │
│       └──▶ Need immediate access when retrieved?                   │
│               YES ──▶ S3 Glacier Instant Retrieval                 │
│               NO  ──▶ S3 Glacier Flexible Retrieval                │
│                                                                      │
│   Archive (compliance, rarely if ever)                              │
│       └──▶ S3 Glacier Deep Archive                                 │
│                                                                      │
│   Unpredictable access                                               │
│       └──▶ S3 Intelligent-Tiering                                  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### S3 Versioning

**Versioning** keeps multiple variants of an object in the same bucket:

```
┌─────────────────────────────────────────────────────────────────────┐
│                      S3 Versioning                                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Bucket: my-bucket (versioning enabled)                            │
│                                                                      │
│   Object: report.pdf                                                │
│   ┌───────────────────────────────────────────────────────────┐    │
│   │                                                            │    │
│   │   Version ID: abc123 (LATEST)                             │    │
│   │   ├── Uploaded: 2024-01-15 14:30                          │    │
│   │   └── Size: 1.5 MB                                         │    │
│   │                                                            │    │
│   │   Version ID: xyz789                                       │    │
│   │   ├── Uploaded: 2024-01-10 09:15                          │    │
│   │   └── Size: 1.2 MB                                         │    │
│   │                                                            │    │
│   │   Version ID: def456                                       │    │
│   │   ├── Uploaded: 2024-01-05 11:00                          │    │
│   │   └── Size: 1.0 MB                                         │    │
│   │                                                            │    │
│   └───────────────────────────────────────────────────────────┘    │
│                                                                      │
│   Benefits:                                                          │
│   ✓ Recover from accidental deletes                                 │
│   ✓ Recover from accidental overwrites                              │
│   ✓ Audit trail of changes                                          │
│                                                                      │
│   Note: Each version stored = additional cost                       │
│   Use lifecycle policies to expire old versions                     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Lifecycle Policies

**Lifecycle policies** automate moving objects between storage classes or deleting them:

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Lifecycle Policy Example                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Rule: "Archive old logs"                                          │
│   Prefix: logs/                                                      │
│                                                                      │
│   Day 0          Day 30           Day 90           Day 365          │
│   ──────         ──────           ──────           ───────          │
│   S3 Standard → S3 Standard-IA → S3 Glacier   →   Delete            │
│                                   Flexible                           │
│                                                                      │
│   ┌───────┐     ┌───────────┐    ┌─────────┐      ┌────────┐       │
│   │ $0.023│ ──▶ │  $0.0125  │ ──▶│ $0.0036 │ ──▶  │ Deleted│       │
│   │ /GB   │     │  /GB      │    │ /GB     │      │        │       │
│   └───────┘     └───────────┘    └─────────┘      └────────┘       │
│                                                                      │
│   Example lifecycle configuration:                                   │
│                                                                      │
│   {                                                                  │
│     "Rules": [                                                       │
│       {                                                              │
│         "ID": "Archive old logs",                                   │
│         "Filter": { "Prefix": "logs/" },                            │
│         "Status": "Enabled",                                        │
│         "Transitions": [                                            │
│           { "Days": 30, "StorageClass": "STANDARD_IA" },           │
│           { "Days": 90, "StorageClass": "GLACIER" }                │
│         ],                                                           │
│         "Expiration": { "Days": 365 }                               │
│       }                                                              │
│     ]                                                                │
│   }                                                                  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Access Control

S3 provides multiple access control mechanisms:

```
┌─────────────────────────────────────────────────────────────────────┐
│                    S3 Access Control                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   1. BUCKET POLICIES (JSON-based, recommended)                      │
│   ─────────────────────────────────────────────                     │
│   • Attached to bucket                                              │
│   • Can grant access to other AWS accounts                          │
│   • Can make bucket/objects public                                  │
│   • Most flexible option                                             │
│                                                                      │
│   2. IAM POLICIES (User-based)                                      │
│   ─────────────────────────────                                     │
│   • Attached to IAM users, groups, roles                           │
│   • Control what S3 actions user can perform                       │
│   • Cannot grant access to non-AWS users                           │
│                                                                      │
│   3. ACCESS CONTROL LISTS (Legacy)                                  │
│   ────────────────────────────────                                  │
│   • Bucket-level and object-level                                  │
│   • Limited functionality                                           │
│   • AWS recommends disabling ACLs for new buckets                  │
│                                                                      │
│   4. S3 ACCESS POINTS (Simplified access)                           │
│   ───────────────────────────────────────                           │
│   • Named network endpoints                                         │
│   • Simplify managing access at scale                               │
│   • Each access point has its own policy                           │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

**Example Bucket Policy:**

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadForWebsite",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::my-website-bucket/*"
        },
        {
            "Sid": "DenyInsecureConnections",
            "Effect": "Deny",
            "Principal": "*",
            "Action": "s3:*",
            "Resource": [
                "arn:aws:s3:::my-website-bucket",
                "arn:aws:s3:::my-website-bucket/*"
            ],
            "Condition": {
                "Bool": {
                    "aws:SecureTransport": "false"
                }
            }
        }
    ]
}
```

### Static Website Hosting

S3 can host static websites (HTML, CSS, JavaScript):

```
┌─────────────────────────────────────────────────────────────────────┐
│                    S3 Static Website Hosting                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Bucket: my-website.example.com                                    │
│   ├── index.html          (index document)                          │
│   ├── error.html          (error document)                          │
│   ├── css/                                                           │
│   │   └── styles.css                                                │
│   ├── js/                                                            │
│   │   └── app.js                                                    │
│   └── images/                                                        │
│       └── logo.png                                                  │
│                                                                      │
│   Configuration:                                                     │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │  Index Document: index.html                                  │   │
│   │  Error Document: error.html                                  │   │
│   │  Website Endpoint:                                           │   │
│   │    my-website.example.com.s3-website-us-east-1.amazonaws.com│   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
│   For custom domain (e.g., www.example.com):                       │
│   1. Create bucket named exactly like domain                        │
│   2. Configure Route 53 to point to S3 endpoint                    │
│   3. (Optional) Use CloudFront for HTTPS and caching               │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Create and Configure Bucket

```bash
# Create bucket (bucket names must be globally unique)
aws s3 mb s3://my-unique-bucket-name-12345

# Create bucket in specific region
aws s3api create-bucket \
    --bucket my-bucket-eu \
    --region eu-west-1 \
    --create-bucket-configuration LocationConstraint=eu-west-1

# Enable versioning
aws s3api put-bucket-versioning \
    --bucket my-unique-bucket-name-12345 \
    --versioning-configuration Status=Enabled

# Block public access (security best practice)
aws s3api put-public-access-block \
    --bucket my-unique-bucket-name-12345 \
    --public-access-block-configuration \
        "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"
```

### Upload and Download Objects

```bash
# Upload single file
aws s3 cp myfile.txt s3://my-bucket/

# Upload with specific storage class
aws s3 cp largefile.zip s3://my-bucket/ --storage-class STANDARD_IA

# Upload entire directory
aws s3 sync ./local-folder s3://my-bucket/folder/

# Download file
aws s3 cp s3://my-bucket/myfile.txt ./

# Download entire folder
aws s3 sync s3://my-bucket/folder/ ./local-folder/

# List objects
aws s3 ls s3://my-bucket/
aws s3 ls s3://my-bucket/ --recursive
```

### Manage Object Versions

```bash
# List object versions
aws s3api list-object-versions \
    --bucket my-bucket \
    --prefix myfile.txt

# Download specific version
aws s3api get-object \
    --bucket my-bucket \
    --key myfile.txt \
    --version-id abc123xyz \
    downloaded-file.txt

# Delete specific version
aws s3api delete-object \
    --bucket my-bucket \
    --key myfile.txt \
    --version-id abc123xyz
```

### Configure Lifecycle Policy

```bash
# Create lifecycle configuration file
cat > lifecycle.json << 'EOF'
{
    "Rules": [
        {
            "ID": "Move old objects to IA then Glacier",
            "Filter": {
                "Prefix": "logs/"
            },
            "Status": "Enabled",
            "Transitions": [
                {
                    "Days": 30,
                    "StorageClass": "STANDARD_IA"
                },
                {
                    "Days": 90,
                    "StorageClass": "GLACIER"
                }
            ],
            "Expiration": {
                "Days": 365
            },
            "NoncurrentVersionExpiration": {
                "NoncurrentDays": 30
            }
        }
    ]
}
EOF

# Apply lifecycle configuration
aws s3api put-bucket-lifecycle-configuration \
    --bucket my-bucket \
    --lifecycle-configuration file://lifecycle.json
```

### Configure Static Website Hosting

```bash
# Enable static website hosting
aws s3 website s3://my-website-bucket \
    --index-document index.html \
    --error-document error.html

# Create bucket policy for public read access
cat > policy.json << 'EOF'
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::my-website-bucket/*"
        }
    ]
}
EOF

# Apply bucket policy
aws s3api put-bucket-policy \
    --bucket my-website-bucket \
    --policy file://policy.json

# Upload website files
aws s3 sync ./website-files s3://my-website-bucket/
```

### Python: S3 Operations

```python
import boto3
from botocore.exceptions import ClientError

def upload_file(file_path, bucket, object_key=None):
    """Upload a file to S3 bucket."""
    
    s3 = boto3.client('s3')
    
    if object_key is None:
        object_key = file_path
    
    try:
        s3.upload_file(file_path, bucket, object_key)
        print(f"Uploaded {file_path} to s3://{bucket}/{object_key}")
        return True
    except ClientError as e:
        print(f"Error uploading file: {e}")
        return False

def generate_presigned_url(bucket, object_key, expiration=3600):
    """Generate a presigned URL for temporary access."""
    
    s3 = boto3.client('s3')
    
    try:
        url = s3.generate_presigned_url(
            'get_object',
            Params={'Bucket': bucket, 'Key': object_key},
            ExpiresIn=expiration
        )
        return url
    except ClientError as e:
        print(f"Error generating URL: {e}")
        return None

def list_objects_with_size(bucket, prefix=''):
    """List objects and their sizes."""
    
    s3 = boto3.client('s3')
    
    paginator = s3.get_paginator('list_objects_v2')
    total_size = 0
    object_count = 0
    
    for page in paginator.paginate(Bucket=bucket, Prefix=prefix):
        for obj in page.get('Contents', []):
            print(f"{obj['Key']}: {obj['Size']:,} bytes")
            total_size += obj['Size']
            object_count += 1
    
    print(f"\nTotal: {object_count} objects, {total_size:,} bytes")
    return object_count, total_size

def copy_between_buckets(source_bucket, dest_bucket, prefix=''):
    """Copy objects between buckets."""
    
    s3 = boto3.resource('s3')
    source = s3.Bucket(source_bucket)
    
    for obj in source.objects.filter(Prefix=prefix):
        copy_source = {'Bucket': source_bucket, 'Key': obj.key}
        s3.Object(dest_bucket, obj.key).copy_from(CopySource=copy_source)
        print(f"Copied {obj.key}")

# Example usage
# upload_file('report.pdf', 'my-bucket', 'documents/report.pdf')
# url = generate_presigned_url('my-bucket', 'documents/report.pdf')
# list_objects_with_size('my-bucket', 'logs/')
```

## Summary

- **S3** is object storage with 99.999999999% durability and virtually unlimited capacity
- **Buckets** are containers with globally unique names; **objects** are the files stored within
- **Storage classes** range from S3 Standard (frequent access) to Glacier Deep Archive (long-term archive)
- **Versioning** protects against accidental deletion and overwrites
- **Lifecycle policies** automate transitioning objects between storage classes and deletion
- **Access control** via bucket policies, IAM policies, and (legacy) ACLs
- **Static website hosting** enables serving HTML, CSS, and JavaScript directly from S3

## Additional Resources

- [Amazon S3 Documentation](https://docs.aws.amazon.com/s3/) - Official comprehensive guide
- [S3 Pricing](https://aws.amazon.com/s3/pricing/) - Detailed cost information by storage class
- [S3 Security Best Practices](https://docs.aws.amazon.com/AmazonS3/latest/userguide/security-best-practices.html) - AWS security recommendations

