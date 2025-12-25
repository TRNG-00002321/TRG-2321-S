# Security Groups

## Learning Objectives

- Define security groups and their role as virtual firewalls
- Distinguish between inbound and outbound rules
- Understand the stateful nature of security groups
- Apply CIDR notation for IP range specification
- Implement security group best practices
- Compare security groups with Network ACLs (NACLs)

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

Security groups are your first line of defense in AWS. Every EC2 instance, RDS database, and load balancer sits behind a security group. Misconfigured security groups are a leading cause of both security breaches and connectivity issues.

As a quality engineer, you'll encounter security groups when tests can't connect to databases ("Connection refused"), when deployments fail to reach target instances, or when investigating why certain ports aren't accessible. Understanding security groups transforms "it doesn't work" into "port 443 isn't allowed from our test subnet."

## The Concept

### What is a Security Group?

A **Security Group** acts as a virtual firewall for your AWS resources, controlling inbound and outbound traffic at the instance level. Think of it as a bouncer at a club—deciding who gets in and who doesn't.

```
┌─────────────────────────────────────────────────────────────────────┐
│                       Security Group                                 │
│                     (Virtual Firewall)                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│                           Internet                                   │
│                              │                                       │
│                              ▼                                       │
│   ┌──────────────────────────────────────────────────────────────┐  │
│   │                    INBOUND RULES                              │  │
│   │   ┌────────────────────────────────────────────────────────┐ │  │
│   │   │  ✓ Port 80 (HTTP) from 0.0.0.0/0                       │ │  │
│   │   │  ✓ Port 443 (HTTPS) from 0.0.0.0/0                     │ │  │
│   │   │  ✓ Port 22 (SSH) from 203.0.113.0/24                   │ │  │
│   │   │  ✗ Everything else: DENIED (implicit)                  │ │  │
│   │   └────────────────────────────────────────────────────────┘ │  │
│   └──────────────────────────────────────────────────────────────┘  │
│                              │                                       │
│                              ▼                                       │
│                    ┌─────────────────┐                              │
│                    │   EC2 Instance  │                              │
│                    │                 │                              │
│                    └─────────────────┘                              │
│                              │                                       │
│                              ▼                                       │
│   ┌──────────────────────────────────────────────────────────────┐  │
│   │                    OUTBOUND RULES                             │  │
│   │   ┌────────────────────────────────────────────────────────┐ │  │
│   │   │  ✓ All traffic to 0.0.0.0/0 (default: allow all)       │ │  │
│   │   └────────────────────────────────────────────────────────┘ │  │
│   └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Key Characteristics

| Characteristic | Description |
|----------------|-------------|
| **Default Deny** | All inbound traffic denied unless explicitly allowed |
| **Stateful** | Return traffic automatically allowed |
| **Instance Level** | Applied to individual instances (not subnets) |
| **Allow Rules Only** | Cannot create deny rules |
| **Multiple SGs** | Instance can have multiple security groups |
| **VPC Scope** | Bound to a specific VPC |

### Inbound vs Outbound Rules

**Inbound Rules:** Control traffic coming INTO the instance

```
┌─────────────────────────────────────────────────────────────────┐
│                        INBOUND RULES                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Type        Protocol    Port      Source            Purpose   │
│   ──────────  ──────────  ────      ──────            ───────   │
│   SSH         TCP         22        203.0.113.50/32   Admin     │
│   HTTP        TCP         80        0.0.0.0/0         Web       │
│   HTTPS       TCP         443       0.0.0.0/0         Web SSL   │
│   MySQL       TCP         3306      sg-webapp123      App→DB    │
│   Custom      TCP         8080      10.0.0.0/16       Internal  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Outbound Rules:** Control traffic going OUT of the instance

```
┌─────────────────────────────────────────────────────────────────┐
│                       OUTBOUND RULES                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Type        Protocol    Port      Destination       Purpose   │
│   ──────────  ──────────  ────      ───────────       ───────   │
│   All         All         All       0.0.0.0/0         Default   │
│                                                                  │
│   OR (more restrictive):                                        │
│   HTTPS       TCP         443       0.0.0.0/0         APIs      │
│   MySQL       TCP         3306      sg-database123    DB access │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Stateful Nature

Security groups are **stateful**—if traffic is allowed in one direction, the response traffic is automatically allowed:

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Stateful Behavior                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   EXAMPLE: Web Request                                              │
│                                                                      │
│   Client                                    EC2 Instance            │
│   ┌─────┐                                  ┌─────┐                  │
│   │     │  ──── Request (port 80) ────▶   │     │                  │
│   │     │                                  │     │   Inbound rule   │
│   │     │       ✓ Allowed by               │     │   allows port 80 │
│   │     │         inbound rule             │     │                  │
│   │     │                                  │     │                  │
│   │     │  ◀─── Response (port 80) ────   │     │                  │
│   │     │                                  │     │   Response auto- │
│   │     │       ✓ Automatically            │     │   allowed (no    │
│   │     │         allowed (stateful)       │     │   outbound rule  │
│   │     │                                  │     │   needed)        │
│   └─────┘                                  └─────┘                  │
│                                                                      │
│   NO explicit outbound rule needed for response traffic!            │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### CIDR Notation

**CIDR (Classless Inter-Domain Routing)** notation specifies IP ranges:

```
┌─────────────────────────────────────────────────────────────────┐
│                      CIDR Notation                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Format: IP_ADDRESS/PREFIX_LENGTH                               │
│                                                                  │
│   ┌───────────────────────────────────────────────────────────┐ │
│   │  CIDR Block      │  IP Range                    │ # IPs   │ │
│   ├───────────────────────────────────────────────────────────┤ │
│   │  0.0.0.0/0       │  All IPs (anywhere)          │ 4.3B    │ │
│   │  10.0.0.0/8      │  10.0.0.0 - 10.255.255.255  │ 16.7M   │ │
│   │  10.0.0.0/16     │  10.0.0.0 - 10.0.255.255    │ 65,536  │ │
│   │  10.0.0.0/24     │  10.0.0.0 - 10.0.0.255      │ 256     │ │
│   │  10.0.0.0/32     │  10.0.0.0 only (single IP)  │ 1       │ │
│   │  192.168.1.0/24  │  192.168.1.0 - 192.168.1.255│ 256     │ │
│   └───────────────────────────────────────────────────────────┘ │
│                                                                  │
│   Rule: /N means first N bits are fixed, rest can vary          │
│   Lower N = more IPs;  Higher N = fewer IPs                     │
│                                                                  │
│   Common patterns:                                               │
│   • /32 = Single IP (your workstation)                          │
│   • /24 = 256 IPs (typical subnet)                              │
│   • /16 = 65,536 IPs (VPC default range)                        │
│   • /0  = All IPs (internet)                                     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Security Group References

You can reference other security groups instead of IP addresses—this is powerful for dynamic environments:

```
┌─────────────────────────────────────────────────────────────────────┐
│              Security Group References                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Instead of:  Allow port 3306 from 10.0.1.50, 10.0.1.51, 10.0.2.50│
│                (Must update when IPs change)                        │
│                                                                      │
│   Use:         Allow port 3306 from sg-webapp                       │
│                (Any instance with sg-webapp can connect)            │
│                                                                      │
│   ┌────────────────────┐              ┌────────────────────┐        │
│   │   Web Tier         │              │   Database Tier    │        │
│   │   sg-webapp        │              │   sg-database      │        │
│   │                    │              │                    │        │
│   │  ┌──────────────┐  │              │  ┌──────────────┐  │        │
│   │  │    EC2       │  │  Port 3306   │  │    RDS       │  │        │
│   │  │  10.0.1.50   │──┼─────────────▶│  │              │  │        │
│   │  └──────────────┘  │              │  └──────────────┘  │        │
│   │  ┌──────────────┐  │              │                    │        │
│   │  │    EC2       │  │              │  Inbound rule:     │        │
│   │  │  10.0.1.51   │──┼──────────────│  Port 3306 from    │        │
│   │  └──────────────┘  │              │  sg-webapp         │        │
│   │  ┌──────────────┐  │              │                    │        │
│   │  │    EC2       │  │              │  (No IPs needed!)  │        │
│   │  │  10.0.2.50   │──┼──────────────│                    │        │
│   │  └──────────────┘  │              │                    │        │
│   └────────────────────┘              └────────────────────┘        │
│                                                                      │
│   Benefits:                                                          │
│   • Auto Scaling adds instances → Automatically have access         │
│   • No rule updates when IPs change                                 │
│   • Clear intent: "web servers can access database"                 │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Security Groups vs Network ACLs

AWS provides two firewall types—understand when to use each:

| Feature | Security Groups | Network ACLs |
|---------|-----------------|--------------|
| **Level** | Instance | Subnet |
| **Statefulness** | Stateful | Stateless |
| **Rules** | Allow only | Allow AND Deny |
| **Rule Processing** | All rules evaluated | Rules processed in order |
| **Default** | Deny all inbound, Allow all outbound | Allow all |
| **Use Case** | Primary instance protection | Subnet-level blocking |

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Defense in Depth                                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   Internet                                                           │
│       │                                                              │
│       ▼                                                              │
│   ┌──────────────────────────────────────────────────┐              │
│   │              Network ACL (NACL)                   │              │
│   │              Subnet-level firewall               │              │
│   │   ┌────────────────────────────────────────────┐│              │
│   │   │            Security Group                   ││              │
│   │   │            Instance-level firewall         ││              │
│   │   │   ┌────────────────────────────────────┐  ││              │
│   │   │   │         EC2 Instance               │  ││              │
│   │   │   │         OS-level firewall          │  ││              │
│   │   │   │         (iptables, Windows FW)     │  ││              │
│   │   │   └────────────────────────────────────┘  ││              │
│   │   └────────────────────────────────────────────┘│              │
│   └──────────────────────────────────────────────────┘              │
│                                                                      │
│   Recommendation:                                                    │
│   • Use Security Groups as primary control (simpler, stateful)      │
│   • Use NACLs for additional subnet-level restrictions if needed    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Security Group Best Practices

```
┌─────────────────────────────────────────────────────────────────┐
│               Security Group Best Practices                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. PRINCIPLE OF LEAST PRIVILEGE                                 │
│     • Only open ports that are needed                           │
│     • Restrict source IPs when possible                         │
│     • Avoid 0.0.0.0/0 except for public-facing services        │
│                                                                  │
│  2. USE DESCRIPTIVE NAMES                                        │
│     • Bad:  sg-1234abcd                                         │
│     • Good: sg-web-servers-prod                                 │
│                                                                  │
│  3. USE SECURITY GROUP REFERENCES                                │
│     • Reference SGs instead of IPs where possible               │
│     • Enables dynamic environments (Auto Scaling)               │
│                                                                  │
│  4. SEPARATE BY FUNCTION                                         │
│     • sg-web: HTTP/HTTPS                                        │
│     • sg-database: MySQL/PostgreSQL                             │
│     • sg-ssh-admin: SSH from admin IPs                          │
│                                                                  │
│  5. DOCUMENT RULES                                               │
│     • Use description field for each rule                       │
│     • Explain why rule exists                                   │
│                                                                  │
│  6. AUDIT REGULARLY                                              │
│     • Review unused security groups                             │
│     • Check for overly permissive rules                         │
│     • Use AWS Config for compliance                             │
│                                                                  │
│  7. NEVER USE 0.0.0.0/0 FOR SSH                                 │
│     • Restrict SSH to known IPs or VPN                          │
│     • Consider Session Manager instead                          │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Create a Security Group

```bash
# Create security group
aws ec2 create-security-group \
    --group-name web-server-sg \
    --description "Security group for web servers" \
    --vpc-id vpc-0123456789abcdef0

# Returns: sg-0123456789abcdef0
```

### Add Inbound Rules

```bash
# Allow HTTP from anywhere
aws ec2 authorize-security-group-ingress \
    --group-id sg-0123456789abcdef0 \
    --protocol tcp \
    --port 80 \
    --cidr 0.0.0.0/0

# Allow HTTPS from anywhere
aws ec2 authorize-security-group-ingress \
    --group-id sg-0123456789abcdef0 \
    --protocol tcp \
    --port 443 \
    --cidr 0.0.0.0/0

# Allow SSH from specific IP
aws ec2 authorize-security-group-ingress \
    --group-id sg-0123456789abcdef0 \
    --protocol tcp \
    --port 22 \
    --cidr 203.0.113.50/32

# Allow MySQL from another security group
aws ec2 authorize-security-group-ingress \
    --group-id sg-database123 \
    --protocol tcp \
    --port 3306 \
    --source-group sg-webapp123
```

### Add Rules with Descriptions

```bash
# Using JSON format for detailed rules
aws ec2 authorize-security-group-ingress \
    --group-id sg-0123456789abcdef0 \
    --ip-permissions '[
        {
            "IpProtocol": "tcp",
            "FromPort": 443,
            "ToPort": 443,
            "IpRanges": [
                {
                    "CidrIp": "0.0.0.0/0",
                    "Description": "HTTPS from internet for web traffic"
                }
            ]
        },
        {
            "IpProtocol": "tcp",
            "FromPort": 22,
            "ToPort": 22,
            "IpRanges": [
                {
                    "CidrIp": "203.0.113.0/24",
                    "Description": "SSH from office network"
                }
            ]
        }
    ]'
```

### View Security Group Rules

```bash
# Describe security group
aws ec2 describe-security-groups \
    --group-ids sg-0123456789abcdef0

# Get just the inbound rules
aws ec2 describe-security-groups \
    --group-ids sg-0123456789abcdef0 \
    --query 'SecurityGroups[0].IpPermissions'

# List all security groups in VPC
aws ec2 describe-security-groups \
    --filters "Name=vpc-id,Values=vpc-0123456789abcdef0" \
    --query 'SecurityGroups[].[GroupId,GroupName,Description]' \
    --output table
```

### Remove Rules

```bash
# Revoke inbound rule
aws ec2 revoke-security-group-ingress \
    --group-id sg-0123456789abcdef0 \
    --protocol tcp \
    --port 22 \
    --cidr 0.0.0.0/0
```

### Python: Manage Security Groups

```python
import boto3

def create_web_server_sg(vpc_id, name):
    """Create a security group for web servers."""
    
    ec2 = boto3.client('ec2')
    
    # Create security group
    response = ec2.create_security_group(
        GroupName=name,
        Description='Security group for web servers',
        VpcId=vpc_id
    )
    sg_id = response['GroupId']
    print(f"Created security group: {sg_id}")
    
    # Add inbound rules
    ec2.authorize_security_group_ingress(
        GroupId=sg_id,
        IpPermissions=[
            {
                'IpProtocol': 'tcp',
                'FromPort': 80,
                'ToPort': 80,
                'IpRanges': [{'CidrIp': '0.0.0.0/0', 'Description': 'HTTP'}]
            },
            {
                'IpProtocol': 'tcp',
                'FromPort': 443,
                'ToPort': 443,
                'IpRanges': [{'CidrIp': '0.0.0.0/0', 'Description': 'HTTPS'}]
            }
        ]
    )
    
    return sg_id

def audit_security_groups(vpc_id):
    """Audit security groups for overly permissive rules."""
    
    ec2 = boto3.client('ec2')
    
    response = ec2.describe_security_groups(
        Filters=[{'Name': 'vpc-id', 'Values': [vpc_id]}]
    )
    
    issues = []
    
    for sg in response['SecurityGroups']:
        sg_id = sg['GroupId']
        sg_name = sg['GroupName']
        
        for rule in sg['IpPermissions']:
            from_port = rule.get('FromPort', 'All')
            to_port = rule.get('ToPort', 'All')
            
            for ip_range in rule.get('IpRanges', []):
                cidr = ip_range.get('CidrIp')
                
                # Check for 0.0.0.0/0 on sensitive ports
                if cidr == '0.0.0.0/0':
                    if from_port == 22:
                        issues.append(f"WARNING: {sg_name} ({sg_id}) - SSH open to world!")
                    elif from_port == 3389:
                        issues.append(f"WARNING: {sg_name} ({sg_id}) - RDP open to world!")
                    elif from_port == 3306:
                        issues.append(f"WARNING: {sg_name} ({sg_id}) - MySQL open to world!")
    
    return issues

# Example usage
issues = audit_security_groups('vpc-0123456789abcdef0')
for issue in issues:
    print(issue)
```

### Check Your Public IP for SSH Rule

```bash
# Get your current public IP
MY_IP=$(curl -s https://checkip.amazonaws.com)

# Add SSH rule for just your IP
aws ec2 authorize-security-group-ingress \
    --group-id sg-0123456789abcdef0 \
    --protocol tcp \
    --port 22 \
    --cidr "${MY_IP}/32"

echo "Added SSH access for $MY_IP"
```

## Summary

- **Security groups** are virtual firewalls controlling inbound and outbound traffic at the instance level
- **Default behavior** denies all inbound traffic; you must explicitly allow required ports
- **Stateful** means response traffic is automatically allowed
- **CIDR notation** specifies IP ranges (e.g., `/32` = single IP, `/0` = all IPs)
- **Security group references** enable dynamic rules that work with Auto Scaling
- **Best practices** include least privilege, descriptive names, and regular audits
- **Security groups vs NACLs:** Use security groups as primary control; NACLs for subnet-level restrictions

## Additional Resources

- [Security Groups Documentation](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_SecurityGroups.html) - Official AWS guide
- [CIDR Calculator](https://www.ipaddressguide.com/cidr) - Calculate IP ranges from CIDR blocks
- [AWS Config Rules for Security Groups](https://docs.aws.amazon.com/config/latest/developerguide/ec2-security-group-attached-to-eni.html) - Automated compliance checking

