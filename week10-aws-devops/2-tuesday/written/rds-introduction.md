# RDS Introduction

## Learning Objectives

- Explain what Amazon RDS is and its benefits over self-managed databases
- Identify the database engines supported by RDS
- Configure Multi-AZ deployments for high availability
- Understand read replicas for scaling read-heavy workloads
- Describe RDS backup and recovery options
- Connect applications to RDS instances securely

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Databases are the backbone of most applications you'll test. When tests fail with database errors, understanding RDS helps you investigate connection issues, performance problems, and configuration mistakes. When your CI/CD pipeline deploys application updates, database connectivity must work correctly.

RDS removes the heavy lifting of database administration—patching, backups, and failover—allowing teams to focus on application development. As a quality engineer, you'll interact with RDS when setting up test environments, validating database connections in pipelines, and troubleshooting production issues.

## The Concept

### What is Amazon RDS?

**Amazon Relational Database Service (RDS)** is a managed service that simplifies the setup, operation, and scaling of relational databases in the cloud. RDS handles time-consuming administration tasks while you focus on your applications and users.

```
┌─────────────────────────────────────────────────────────────────┐
│           Self-Managed Database vs Amazon RDS                    │
├─────────────────────────────┬───────────────────────────────────┤
│       Self-Managed          │            Amazon RDS              │
├─────────────────────────────┼───────────────────────────────────┤
│                             │                                    │
│  YOU manage:                │  YOU manage:                       │
│  ✗ Hardware procurement     │  ✓ Application optimization        │
│  ✗ OS installation/patching │  ✓ Schema design                   │
│  ✗ Database installation    │  ✓ Query tuning                    │
│  ✗ Backups                  │                                    │
│  ✗ High availability setup  │  AWS manages:                      │
│  ✗ Scaling                  │  ✓ Hardware                        │
│  ✗ Security patches         │  ✓ OS & DB patches                 │
│  ✓ Application optimization │  ✓ Backups                         │
│  ✓ Schema design            │  ✓ High availability               │
│  ✓ Query tuning             │  ✓ Scaling                         │
│                             │  ✓ Monitoring                      │
└─────────────────────────────┴───────────────────────────────────┘
```

### Supported Database Engines

RDS supports six popular database engines:

| Engine | Description | Use Case |
|--------|-------------|----------|
| **Amazon Aurora** | AWS cloud-native, MySQL/PostgreSQL compatible | High performance, scalability |
| **MySQL** | Popular open-source database | Web applications, CMS |
| **PostgreSQL** | Advanced open-source with rich features | Complex queries, GIS data |
| **MariaDB** | MySQL fork with enhancements | MySQL alternative |
| **Oracle** | Enterprise database | Legacy applications, enterprise |
| **SQL Server** | Microsoft's database | Windows/.NET applications |

```
Database Engine Selection:

                    Need MySQL compatibility?
                            │
                    YES ────┼──── NO
                     │      │      │
                     ▼      │      ▼
         Need high         │    Need PostgreSQL?
         performance?      │          │
              │            │     YES ─┼── NO
         YES ─┼── NO       │      │   │
          │   │    │       │      ▼   ▼
          ▼   │    ▼       │   PostgreSQL  Enterprise?
       Aurora │  MySQL/    │              │
              │  MariaDB   │         YES ─┼── NO
              │            │          │   │
              │            │          ▼   ▼
              │            │    Oracle/SQL  Aurora
              │            │    Server      PostgreSQL
```

### RDS Instance Classes

RDS instances come in different classes optimized for various workloads:

| Class | Description | Example |
|-------|-------------|---------|
| **Standard (m)** | Balanced compute, memory, networking | db.m6g.large |
| **Memory Optimized (r)** | High memory-to-CPU ratio | db.r6g.xlarge |
| **Burstable (t)** | Baseline with burst capability | db.t3.micro |

```bash
# Instance naming convention: db.{class}{generation}.{size}
# Examples:
db.t3.micro      # Burstable, 3rd gen, micro size (Free Tier eligible)
db.m6g.large     # Standard, 6th gen Graviton, large
db.r6g.2xlarge   # Memory optimized, 6th gen Graviton, 2xlarge
```

### Multi-AZ Deployments

Multi-AZ provides high availability by automatically maintaining a synchronous standby replica in a different Availability Zone:

```
┌────────────────────────── Region: us-east-1 ──────────────────────────┐
│                                                                        │
│   ┌─────────────────────────┐     ┌─────────────────────────┐         │
│   │      us-east-1a         │     │      us-east-1b         │         │
│   │                         │     │                         │         │
│   │  ┌───────────────────┐  │     │  ┌───────────────────┐  │         │
│   │  │   RDS Primary     │  │     │  │   RDS Standby     │  │         │
│   │  │                   │──┼─────┼──│                   │  │         │
│   │  │  • Handles all    │  │sync │  │  • Synchronous    │  │         │
│   │  │    read/write     │  │repl.│  │    replication    │  │         │
│   │  │  • Application    │  │     │  │  • No direct      │  │         │
│   │  │    endpoint       │  │     │  │    access         │  │         │
│   │  └───────────────────┘  │     │  └───────────────────┘  │         │
│   │                         │     │                         │         │
│   └─────────────────────────┘     └─────────────────────────┘         │
│                                                                        │
│   On primary failure:                                                  │
│   ┌─────────────────────────────────────────────────────────┐         │
│   │  1. RDS detects failure (within seconds)                 │         │
│   │  2. DNS failover to standby (60-120 seconds)            │         │
│   │  3. Standby promoted to primary                          │         │
│   │  4. New standby created automatically                    │         │
│   │  5. Application reconnects using same endpoint           │         │
│   └─────────────────────────────────────────────────────────┘         │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```

**Multi-AZ Benefits:**
- Automatic failover during AZ outage, hardware failure, or maintenance
- Same endpoint (DNS) works before and after failover
- Synchronous replication ensures zero data loss
- Automatic backups taken from standby (no performance impact on primary)

### Read Replicas

Read replicas scale read-heavy workloads by offloading read traffic from the primary:

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Read Replica Architecture                    │
│                                                                      │
│                    ┌─────────────────┐                               │
│                    │    Primary      │                               │
│                    │   (Read/Write)  │                               │
│                    └────────┬────────┘                               │
│                             │                                        │
│              ┌──────────────┼──────────────┐                        │
│              │              │              │                         │
│         async│         async│         async│                         │
│         repl.│         repl.│         repl.│                         │
│              │              │              │                         │
│              ▼              ▼              ▼                         │
│     ┌─────────────┐ ┌─────────────┐ ┌─────────────┐                 │
│     │  Replica 1  │ │  Replica 2  │ │  Replica 3  │                 │
│     │ (Read Only) │ │ (Read Only) │ │ (Read Only) │                 │
│     │  us-east-1  │ │  us-west-2  │ │  eu-west-1  │                 │
│     └─────────────┘ └─────────────┘ └─────────────┘                 │
│                                                                      │
│   • Up to 5 read replicas (15 for Aurora)                           │
│   • Cross-region replicas for geographic distribution                │
│   • Can be promoted to standalone primary                            │
│   • Asynchronous replication (slight lag possible)                   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

**Multi-AZ vs Read Replicas:**

| Feature | Multi-AZ | Read Replicas |
|---------|----------|---------------|
| Purpose | High availability | Scalability |
| Replication | Synchronous | Asynchronous |
| Can serve reads | No (standby) | Yes |
| Automatic failover | Yes | No (manual promotion) |
| Cross-region | No | Yes |

### Automated Backups

RDS provides two backup mechanisms:

**1. Automated Backups:**
- Daily full backup during configured window
- Transaction logs backed up every 5 minutes
- Point-in-time recovery to any second within retention period (up to 35 days)
- Stored in S3 (managed by AWS)

**2. Manual Snapshots:**
- User-initiated at any time
- Retained until explicitly deleted
- Can be copied across regions
- Can be shared with other AWS accounts

```
┌─────────────────────────────────────────────────────────────────┐
│                     RDS Backup Strategy                          │
│                                                                  │
│   Day 1      Day 2      Day 3      Day 4      Day 5             │
│    │          │          │          │          │                 │
│    ▼          ▼          ▼          ▼          ▼                 │
│   [Full]    [Full]    [Full]    [Full]    [Full]   ← Daily      │
│    │          │          │          │          │      Backups    │
│    └──────────┴──────────┴──────────┴──────────┘                │
│                         │                                        │
│                Transaction Logs (every 5 min)                    │
│    ──────────────────────────────────────────────                │
│                         │                                        │
│                         ▼                                        │
│            Point-in-Time Recovery                                │
│          "Restore to 2:47:32 PM yesterday"                       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### RDS Security

Security is implemented at multiple layers:

```
┌─────────────────────────────────────────────────────────────────┐
│                     RDS Security Layers                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  NETWORK SECURITY                                                │
│  ├── VPC: Isolated network                                       │
│  ├── Security Groups: Control inbound/outbound traffic          │
│  ├── Private Subnets: No public internet access                 │
│  └── No Public IP: Access only from within VPC                  │
│                                                                  │
│  ENCRYPTION                                                      │
│  ├── At Rest: AES-256 encryption (EBS volumes, snapshots)       │
│  ├── In Transit: SSL/TLS connections                            │
│  └── KMS: Customer-managed keys optional                        │
│                                                                  │
│  ACCESS CONTROL                                                  │
│  ├── IAM: Control who can manage RDS resources                  │
│  ├── Database Auth: Traditional username/password               │
│  └── IAM DB Auth: Token-based authentication (MySQL/PostgreSQL) │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Creating an RDS Instance (CLI)

```bash
# Create a MySQL RDS instance
aws rds create-db-instance \
    --db-instance-identifier mydb-instance \
    --db-instance-class db.t3.micro \
    --engine mysql \
    --engine-version 8.0 \
    --master-username admin \
    --master-user-password MySecurePassword123! \
    --allocated-storage 20 \
    --storage-type gp2 \
    --vpc-security-group-ids sg-0123456789abcdef0 \
    --db-subnet-group-name my-db-subnet-group \
    --backup-retention-period 7 \
    --multi-az \
    --no-publicly-accessible
```

### Describing RDS Instances

```bash
# List all RDS instances
aws rds describe-db-instances \
    --query 'DBInstances[].[DBInstanceIdentifier,DBInstanceStatus,Engine,Endpoint.Address]' \
    --output table

# Get specific instance details
aws rds describe-db-instances \
    --db-instance-identifier mydb-instance
```

### Python: Connect to RDS MySQL

```python
import pymysql

# Connection parameters (from AWS Console or describe-db-instances)
connection = pymysql.connect(
    host='mydb-instance.abcdefg12345.us-east-1.rds.amazonaws.com',
    port=3306,
    user='admin',
    password='MySecurePassword123!',
    database='myapp',
    connect_timeout=5
)

try:
    with connection.cursor() as cursor:
        # Execute a query
        cursor.execute("SELECT VERSION()")
        result = cursor.fetchone()
        print(f"MySQL Version: {result[0]}")
        
        # Create a test table
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100),
                email VARCHAR(100)
            )
        """)
        
        # Insert data
        cursor.execute(
            "INSERT INTO users (name, email) VALUES (%s, %s)",
            ('John Doe', 'john@example.com')
        )
        connection.commit()
        
        # Query data
        cursor.execute("SELECT * FROM users")
        for row in cursor.fetchall():
            print(row)
            
finally:
    connection.close()
```

### Python: Connect to RDS PostgreSQL

```python
import psycopg2

# Connection parameters
connection = psycopg2.connect(
    host='mydb-instance.abcdefg12345.us-east-1.rds.amazonaws.com',
    port=5432,
    user='admin',
    password='MySecurePassword123!',
    database='myapp',
    connect_timeout=5
)

try:
    with connection.cursor() as cursor:
        # Execute a query
        cursor.execute("SELECT version()")
        result = cursor.fetchone()
        print(f"PostgreSQL Version: {result[0]}")
        
        # Create a test table
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS products (
                id SERIAL PRIMARY KEY,
                name VARCHAR(100),
                price DECIMAL(10, 2)
            )
        """)
        
        # Insert data
        cursor.execute(
            "INSERT INTO products (name, price) VALUES (%s, %s)",
            ('Widget', 29.99)
        )
        connection.commit()
        
finally:
    connection.close()
```

### Creating a Read Replica

```bash
# Create read replica in same region
aws rds create-db-instance-read-replica \
    --db-instance-identifier mydb-replica \
    --source-db-instance-identifier mydb-instance \
    --db-instance-class db.t3.micro

# Create cross-region read replica
aws rds create-db-instance-read-replica \
    --db-instance-identifier mydb-replica-west \
    --source-db-instance-identifier arn:aws:rds:us-east-1:123456789012:db:mydb-instance \
    --db-instance-class db.t3.micro \
    --region us-west-2
```

### Taking a Manual Snapshot

```bash
# Create snapshot
aws rds create-db-snapshot \
    --db-instance-identifier mydb-instance \
    --db-snapshot-identifier mydb-snapshot-$(date +%Y%m%d)

# List snapshots
aws rds describe-db-snapshots \
    --db-instance-identifier mydb-instance \
    --query 'DBSnapshots[].[DBSnapshotIdentifier,Status,SnapshotCreateTime]' \
    --output table
```

### Point-in-Time Recovery

```bash
# Restore to a specific point in time
aws rds restore-db-instance-to-point-in-time \
    --source-db-instance-identifier mydb-instance \
    --target-db-instance-identifier mydb-restored \
    --restore-time "2024-01-15T14:30:00Z"
```

## Summary

- **Amazon RDS** is a managed database service that handles administration tasks (patching, backups, failover)
- **Six database engines** supported: Aurora, MySQL, PostgreSQL, MariaDB, Oracle, SQL Server
- **Multi-AZ deployments** provide high availability with automatic failover (synchronous replication)
- **Read replicas** scale read-heavy workloads with asynchronous replication (up to 5 per primary)
- **Automated backups** enable point-in-time recovery to any second within the retention period
- **Security** is implemented through VPC, security groups, encryption, and IAM integration
- As a quality engineer, understanding RDS helps you validate database connectivity in test environments and troubleshoot production issues

## Additional Resources

- [Amazon RDS Documentation](https://docs.aws.amazon.com/rds/) - Official comprehensive documentation
- [RDS Best Practices](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_BestPractices.html) - AWS recommended practices
- [RDS Pricing](https://aws.amazon.com/rds/pricing/) - Understand costs for different instance types and engines

