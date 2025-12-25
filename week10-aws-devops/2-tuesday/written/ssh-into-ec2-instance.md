# SSH Into EC2 Instance

## Learning Objectives

- Understand SSH key pairs and their role in EC2 authentication
- Generate and manage SSH key pairs for EC2 instances
- Connect to EC2 instances from Windows, macOS, and Linux
- Configure PuTTY for SSH connections on Windows
- Use EC2 Instance Connect as a browser-based alternative
- Troubleshoot common SSH connection issues

## Why This Matters

*From Code to Cloud: Mastering the Modern Deployment Pipeline*

SSH (Secure Shell) is your primary method for accessing EC2 instances. Whether you're deploying applications, debugging issues, viewing logs, or configuring services, you'll need to SSH into instances regularly. When a CI/CD pipeline deploys to EC2, understanding SSH helps you verify deployments and troubleshoot failures.

As a quality engineer, you'll SSH into test servers to examine application behavior, review logs during test failures, and validate configurations. Mastering SSH transforms you from someone who waits for DevOps help to someone who investigates issues directly.

## The Concept

### What is SSH?

**Secure Shell (SSH)** is a cryptographic network protocol for secure remote access. Instead of passwords (which can be intercepted), SSH uses public-key cryptography.

```
┌─────────────────────────────────────────────────────────────────────┐
│                     SSH Key Pair Authentication                      │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   YOUR COMPUTER                           EC2 INSTANCE              │
│   ┌─────────────────┐                    ┌─────────────────┐        │
│   │                 │                    │                 │        │
│   │  Private Key    │ ═══════════════════│  Public Key     │        │
│   │  (my-key.pem)   │   SSH Connection   │  (authorized    │        │
│   │                 │                    │   _keys)        │        │
│   │  🔐 KEEP SECRET │                    │  🔓 Can share   │        │
│   │                 │                    │                 │        │
│   └─────────────────┘                    └─────────────────┘        │
│                                                                      │
│   How it works:                                                      │
│   1. EC2 stores your public key                                     │
│   2. When you connect, you prove you have the private key           │
│   3. EC2 verifies against its stored public key                     │
│   4. Connection established if keys match                           │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Key Pair Basics

| Term | Description |
|------|-------------|
| **Private Key** | Secret file you keep on your computer (`.pem` file) |
| **Public Key** | Stored on EC2 instance in `~/.ssh/authorized_keys` |
| **Key Pair Name** | AWS identifier for the key pair |
| **Fingerprint** | Hash to verify key identity |

**Important Security Rules:**
- **Never share your private key**
- Store private key in a secure location
- Set restrictive permissions (chmod 400)
- Use different key pairs for different environments

### Creating Key Pairs

You can create key pairs through the AWS Console or CLI:

```
┌─────────────────────────────────────────────────────────────────┐
│                    Key Pair Creation Options                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   OPTION 1: AWS Creates Key Pair                                 │
│   ─────────────────────────────                                  │
│   • AWS generates both keys                                      │
│   • You download the private key (.pem) once                     │
│   • AWS stores the public key                                    │
│   • Cannot re-download private key if lost                       │
│                                                                  │
│   OPTION 2: Import Your Own Public Key                           │
│   ────────────────────────────────────                           │
│   • You generate key pair locally                                │
│   • Import public key to AWS                                     │
│   • Private key never leaves your machine                        │
│   • Can use same public key across multiple accounts             │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### SSH Connection Process

```
┌─────────────────────────────────────────────────────────────────────┐
│                     SSH Connection Steps                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   1. LAUNCH INSTANCE                                                 │
│      └── Select key pair during launch                              │
│          └── Public key copied to instance                          │
│                                                                      │
│   2. CONFIGURE SECURITY GROUP                                        │
│      └── Allow inbound SSH (port 22)                                │
│          └── Restrict to your IP if possible                        │
│                                                                      │
│   3. GET INSTANCE PUBLIC IP                                          │
│      └── From EC2 console or describe-instances                     │
│                                                                      │
│   4. SET KEY PERMISSIONS (first time)                                │
│      └── chmod 400 my-key.pem                                       │
│                                                                      │
│   5. CONNECT                                                         │
│      └── ssh -i my-key.pem ec2-user@<public-ip>                     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Default Usernames by AMI

Different AMIs use different default usernames:

| AMI | Default Username |
|-----|------------------|
| Amazon Linux 2/2023 | `ec2-user` |
| Ubuntu | `ubuntu` |
| Debian | `admin` |
| RHEL | `ec2-user` or `root` |
| CentOS | `centos` or `ec2-user` |
| SUSE | `ec2-user` or `root` |
| Windows | `Administrator` (RDP, not SSH) |

### Connecting from Different Operating Systems

#### macOS/Linux

Native SSH client is built-in:

```bash
# Set key permissions (required first time)
chmod 400 ~/keys/my-key.pem

# Connect to instance
ssh -i ~/keys/my-key.pem ec2-user@54.123.45.67

# Connect with specific options
ssh -i ~/keys/my-key.pem \
    -o StrictHostKeyChecking=no \
    -o UserKnownHostsFile=/dev/null \
    ec2-user@54.123.45.67
```

#### Windows (PowerShell/OpenSSH)

Windows 10+ includes OpenSSH:

```powershell
# Check if OpenSSH is installed
Get-WindowsCapability -Online | Where-Object Name -like 'OpenSSH*'

# Set key permissions (Windows equivalent)
icacls .\my-key.pem /inheritance:r
icacls .\my-key.pem /grant:r "%USERNAME%:R"

# Connect
ssh -i .\my-key.pem ec2-user@54.123.45.67
```

#### Windows (PuTTY)

PuTTY is a popular third-party SSH client:

```
┌─────────────────────────────────────────────────────────────────┐
│                    PuTTY Setup Steps                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   1. CONVERT KEY FORMAT                                          │
│      PuTTY uses .ppk format, not .pem                           │
│                                                                  │
│      Open PuTTYgen:                                              │
│      a. Click "Load" → Select .pem file                         │
│      b. Click "Save private key"                                │
│      c. Save as my-key.ppk                                      │
│                                                                  │
│   2. CONFIGURE PUTTY                                             │
│      a. Host Name: ec2-user@54.123.45.67                        │
│      b. Port: 22                                                 │
│      c. Connection → SSH → Auth → Private key: my-key.ppk       │
│      d. (Optional) Save session for reuse                       │
│                                                                  │
│   3. CONNECT                                                     │
│      Click "Open"                                                │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### EC2 Instance Connect

**EC2 Instance Connect** provides browser-based SSH without managing key pairs:

```
┌─────────────────────────────────────────────────────────────────┐
│                   EC2 Instance Connect                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ADVANTAGES:                                                    │
│   • No local key management                                      │
│   • Browser-based access                                         │
│   • IAM-controlled access                                        │
│   • Temporary SSH keys (60-second validity)                      │
│                                                                  │
│   REQUIREMENTS:                                                  │
│   • Amazon Linux 2, Ubuntu 16.04+                               │
│   • Instance in public subnet with public IP                    │
│   • Security group allows SSH from AWS IP ranges                │
│   • IAM permissions: ec2-instance-connect:SendSSHPublicKey      │
│                                                                  │
│   ACCESS:                                                        │
│   1. Go to EC2 Console                                          │
│   2. Select instance                                             │
│   3. Click "Connect"                                             │
│   4. Choose "EC2 Instance Connect"                              │
│   5. Click "Connect"                                             │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Session Manager Alternative

**AWS Systems Manager Session Manager** provides shell access without SSH:

```
┌─────────────────────────────────────────────────────────────────┐
│                    Session Manager                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ADVANTAGES:                                                    │
│   • No open inbound ports (no SSH port 22 needed)               │
│   • No bastion hosts needed                                      │
│   • Works for private instances                                  │
│   • Full audit logging to CloudTrail                            │
│   • IAM-based access control                                     │
│                                                                  │
│   REQUIREMENTS:                                                  │
│   • SSM Agent installed (pre-installed on Amazon Linux 2)       │
│   • IAM role with SSM permissions on instance                   │
│   • VPC endpoint or internet access for SSM                     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Troubleshooting SSH Issues

```
┌─────────────────────────────────────────────────────────────────────┐
│                 Common SSH Connection Problems                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ERROR: "Permission denied (publickey)"                             │
│  ───────────────────────────────────────                            │
│  Causes:                                                             │
│  • Wrong username (ec2-user vs ubuntu)                              │
│  • Wrong key file                                                    │
│  • Key permissions too open                                          │
│                                                                      │
│  Solution:                                                           │
│  chmod 400 my-key.pem                                               │
│  ssh -i my-key.pem ubuntu@<ip>  # Try different usernames           │
│                                                                      │
│  ─────────────────────────────────────────────────────────────────  │
│                                                                      │
│  ERROR: "Connection timed out"                                       │
│  ─────────────────────────────                                       │
│  Causes:                                                             │
│  • Security group doesn't allow SSH (port 22)                       │
│  • Wrong IP address                                                  │
│  • Instance not running                                              │
│  • Network ACL blocking traffic                                      │
│                                                                      │
│  Solution:                                                           │
│  1. Verify security group has inbound rule for port 22              │
│  2. Check instance is in "running" state                            │
│  3. Verify public IP is correct                                      │
│                                                                      │
│  ─────────────────────────────────────────────────────────────────  │
│                                                                      │
│  ERROR: "UNPROTECTED PRIVATE KEY FILE"                              │
│  ──────────────────────────────────────                             │
│  Cause: Key file permissions too permissive                         │
│                                                                      │
│  Solution:                                                           │
│  chmod 400 my-key.pem  # Linux/Mac                                  │
│  # Windows: Remove inheritance, grant only your user read access    │
│                                                                      │
│  ─────────────────────────────────────────────────────────────────  │
│                                                                      │
│  ERROR: "Host key verification failed"                              │
│  ──────────────────────────────────────                             │
│  Cause: Instance was recreated with same IP, different host key     │
│                                                                      │
│  Solution:                                                           │
│  ssh-keygen -R <ip-address>  # Remove old host key                  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## Code Examples

### Create Key Pair with AWS CLI

```bash
# Create key pair and save private key
aws ec2 create-key-pair \
    --key-name my-ec2-key \
    --query 'KeyMaterial' \
    --output text > my-ec2-key.pem

# Set proper permissions
chmod 400 my-ec2-key.pem

# List existing key pairs
aws ec2 describe-key-pairs \
    --query 'KeyPairs[].[KeyName,KeyFingerprint]' \
    --output table
```

### Import Existing Key Pair

```bash
# Generate key pair locally
ssh-keygen -t rsa -b 4096 -f my-imported-key -N ""

# Import public key to AWS
aws ec2 import-key-pair \
    --key-name my-imported-key \
    --public-key-material fileb://my-imported-key.pub
```

### Get Instance Connection Information

```bash
# Get public IP of instance
aws ec2 describe-instances \
    --instance-ids i-0123456789abcdef0 \
    --query 'Reservations[0].Instances[0].PublicIpAddress' \
    --output text

# Get detailed connection info
aws ec2 describe-instances \
    --instance-ids i-0123456789abcdef0 \
    --query 'Reservations[0].Instances[0].[PublicIpAddress,KeyName,State.Name]' \
    --output table
```

### SSH Connection Commands

```bash
# Basic SSH connection
ssh -i my-key.pem ec2-user@54.123.45.67

# SSH with verbose output (debugging)
ssh -v -i my-key.pem ec2-user@54.123.45.67

# Very verbose (more debugging info)
ssh -vvv -i my-key.pem ec2-user@54.123.45.67

# SSH and run a command directly
ssh -i my-key.pem ec2-user@54.123.45.67 "cat /var/log/messages | tail -20"

# SSH with port forwarding (local port 8080 → remote port 80)
ssh -i my-key.pem -L 8080:localhost:80 ec2-user@54.123.45.67

# Copy files to EC2 (SCP)
scp -i my-key.pem localfile.txt ec2-user@54.123.45.67:/home/ec2-user/

# Copy files from EC2
scp -i my-key.pem ec2-user@54.123.45.67:/var/log/app.log ./

# Copy entire directory
scp -r -i my-key.pem ./myapp ec2-user@54.123.45.67:/home/ec2-user/
```

### SSH Config File for Easy Access

Create `~/.ssh/config` to simplify connections:

```
# ~/.ssh/config

# Web server in production
Host web-prod
    HostName 54.123.45.67
    User ec2-user
    IdentityFile ~/.ssh/my-key.pem
    StrictHostKeyChecking no

# Database server (through bastion)
Host db-prod
    HostName 10.0.1.50
    User ec2-user
    IdentityFile ~/.ssh/my-key.pem
    ProxyJump bastion-prod

# Bastion host
Host bastion-prod
    HostName 52.87.123.45
    User ec2-user
    IdentityFile ~/.ssh/bastion-key.pem

# Now you can simply run:
# ssh web-prod
# ssh db-prod
```

### Python: SSH with Paramiko

```python
import paramiko

def ssh_execute_command(host, username, key_path, command):
    """Execute a command on an EC2 instance via SSH."""
    
    # Create SSH client
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    
    # Load private key
    key = paramiko.RSAKey.from_private_key_file(key_path)
    
    try:
        # Connect to instance
        ssh.connect(hostname=host, username=username, pkey=key, timeout=10)
        
        # Execute command
        stdin, stdout, stderr = ssh.exec_command(command)
        
        # Get output
        output = stdout.read().decode()
        errors = stderr.read().decode()
        exit_code = stdout.channel.recv_exit_status()
        
        return {
            'output': output,
            'errors': errors,
            'exit_code': exit_code
        }
    finally:
        ssh.close()

# Example usage
result = ssh_execute_command(
    host='54.123.45.67',
    username='ec2-user',
    key_path='my-key.pem',
    command='uptime && df -h'
)

print(f"Output: {result['output']}")
print(f"Exit Code: {result['exit_code']}")
```

### EC2 Instance Connect via CLI

```bash
# Send SSH public key to instance (valid for 60 seconds)
aws ec2-instance-connect send-ssh-public-key \
    --instance-id i-0123456789abcdef0 \
    --instance-os-user ec2-user \
    --ssh-public-key file://my-key.pub

# Then immediately connect with corresponding private key
ssh -i my-key ec2-user@54.123.45.67
```

### Session Manager Connection

```bash
# Install Session Manager plugin first
# https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager-working-with-install-plugin.html

# Start session (no SSH needed)
aws ssm start-session --target i-0123456789abcdef0

# Run a command remotely
aws ssm send-command \
    --instance-ids i-0123456789abcdef0 \
    --document-name "AWS-RunShellScript" \
    --parameters 'commands=["uptime","df -h"]'
```

## Summary

- **SSH** uses public-key cryptography for secure authentication—private key stays with you, public key on the instance
- **Key pairs** must be specified when launching EC2 instances; private keys cannot be recovered if lost
- **Default usernames** vary by AMI: `ec2-user` (Amazon Linux), `ubuntu` (Ubuntu), `admin` (Debian)
- **PuTTY** on Windows requires converting `.pem` to `.ppk` format using PuTTYgen
- **EC2 Instance Connect** provides browser-based SSH without local key management
- **Session Manager** enables shell access without opening port 22
- Common issues include wrong username, incorrect key permissions, and security group misconfigurations

## Additional Resources

- [Connect to Your Linux Instance](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AccessingInstances.html) - Official AWS documentation
- [EC2 Instance Connect](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/Connect-using-EC2-Instance-Connect.html) - Browser-based access guide
- [PuTTY Download and Documentation](https://www.chiark.greenend.org.uk/~sgtatham/putty/) - Windows SSH client

