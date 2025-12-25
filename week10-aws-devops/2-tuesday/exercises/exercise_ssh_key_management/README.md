# Exercise 3: SSH Key Management

## Objective

Master SSH key pair management, understand common connection issues, and practice troubleshooting techniques for EC2 access.

---

## Learning Outcomes

By completing this exercise, you will:
- Generate SSH key pairs from the command line
- Understand SSH key permissions and their importance
- Troubleshoot common SSH connection problems
- Use SSH config files for easier connections
- Connect from different operating systems

---

## Prerequisites

- Completed Exercise 2 (EC2 Web Server)
- Running EC2 instance from previous exercise
- Terminal/command line access

---

## Time Estimate

30 minutes

---

## Tasks

### Task 1: Understand Your Current Key (5 minutes)

1. **Examine the Downloaded Key**
   ```bash
   # View key file (DO NOT share this!)
   cat ~/.ssh/web-server-key.pem
   
   # Check permissions
   ls -la ~/.ssh/web-server-key.pem
   ```

2. **Understand the Key Format**
   - `.pem` = Privacy Enhanced Mail format
   - Contains: RSA PRIVATE KEY
   - This is your **private key** - never share it!

3. **Check Permissions**
   ```bash
   # Should show: -r-------- (400) or -rw------- (600)
   stat ~/.ssh/web-server-key.pem
   ```

**Key Concept:** SSH requires private keys to have restricted permissions. If anyone else can read your key, SSH refuses to use it.

---

### Task 2: Generate a New Key Pair Locally (10 minutes)

Practice creating keys outside of AWS.

1. **Generate RSA Key Pair**
   ```bash
   ssh-keygen -t rsa -b 4096 -f ~/.ssh/my-custom-key -C "trainee@week10"
   ```
   
   When prompted:
   - Passphrase: (enter one or leave blank for testing)

2. **Examine Generated Files**
   ```bash
   ls -la ~/.ssh/my-custom-key*
   ```
   
   You'll see two files:
   - `my-custom-key` - Private key (keep secret!)
   - `my-custom-key.pub` - Public key (safe to share)

3. **View the Public Key**
   ```bash
   cat ~/.ssh/my-custom-key.pub
   ```
   
   This is what you would add to `~/.ssh/authorized_keys` on a server.

4. **Import to AWS (Optional)**
   - Go to: `EC2 → Key Pairs → Actions → Import key pair`
   - Paste the contents of `my-custom-key.pub`
   - This allows using your local key with new EC2 instances

---

### Task 3: Create SSH Config File (5 minutes)

Simplify your SSH connections.

1. **Create/Edit SSH Config**
   ```bash
   nano ~/.ssh/config
   ```

2. **Add Configuration**
   ```
   # Week 10 EC2 Web Server
   Host web-server
       HostName <YOUR_EC2_PUBLIC_IP>
       User ec2-user
       IdentityFile ~/.ssh/web-server-key.pem
       StrictHostKeyChecking no
   
   # Template for future servers
   Host my-server
       HostName 0.0.0.0
       User ec2-user
       IdentityFile ~/.ssh/my-custom-key
   ```

3. **Save and Set Permissions**
   ```bash
   chmod 600 ~/.ssh/config
   ```

4. **Connect Using Alias**
   ```bash
   ssh web-server
   ```
   
   Much easier than typing the full command!

---

### Task 4: Troubleshooting Practice (10 minutes)

Practice diagnosing and fixing common SSH issues.

#### Issue 1: Permission Denied (Key too open)

1. **Simulate the Problem**
   ```bash
   chmod 644 ~/.ssh/web-server-key.pem
   ssh web-server
   ```
   
   **Expected Error:**
   ```
   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
   @         WARNING: UNPROTECTED PRIVATE KEY FILE!          @
   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
   Permissions 0644 for '.ssh/web-server-key.pem' are too open.
   ```

2. **Fix It**
   ```bash
   chmod 400 ~/.ssh/web-server-key.pem
   ssh web-server
   ```

#### Issue 2: Wrong Username

1. **Simulate the Problem**
   ```bash
   ssh -i ~/.ssh/web-server-key.pem ubuntu@<YOUR_EC2_IP>
   ```
   
   **Expected Error:**
   ```
   Permission denied (publickey,gssapi-keyex,gssapi-with-mic)
   ```

2. **Fix It**
   - Amazon Linux uses `ec2-user`
   - Ubuntu AMIs use `ubuntu`
   - RHEL uses `ec2-user`
   - Debian uses `admin`

#### Issue 3: Connection Timeout

1. **Simulate Understanding**
   - This happens when:
     - Security group doesn't allow port 22 from your IP
     - Instance is not running
     - Network ACL blocks traffic
     - Your IP changed (if using "My IP" in security group)

2. **Debug Steps**
   ```bash
   # Verbose output
   ssh -v web-server
   
   # Extra verbose
   ssh -vv web-server
   
   # Maximum verbosity
   ssh -vvv web-server
   ```

3. **Check Your Public IP**
   ```bash
   curl ifconfig.me
   ```
   
   Compare this to what's allowed in your security group.

#### Issue 4: Host Key Changed Warning

1. **What It Means**
   - EC2 instance was replaced
   - Someone might be intercepting traffic (man-in-the-middle)
   - For EC2: usually means instance was terminated and recreated

2. **Fix It (Only if you know why)**
   ```bash
   # Remove old host key
   ssh-keygen -R <IP_ADDRESS>
   
   # Or edit known_hosts manually
   nano ~/.ssh/known_hosts
   # Delete the line for that IP
   ```

---

## Verification Checklist

- [ ] Understand difference between public and private keys
- [ ] Generated a new key pair locally
- [ ] Created SSH config file for easier connections
- [ ] Can connect using `ssh web-server` alias
- [ ] Successfully diagnosed permission denied error
- [ ] Understand common SSH troubleshooting steps

---

## Deliverables

1. Your `.ssh/config` file contents (redact actual IPs if needed)
2. Output of `ssh -v web-server` showing successful connection
3. Brief notes on three SSH issues and their solutions

---

## SSH Quick Reference

### Key Management Commands
```bash
# Generate new key pair
ssh-keygen -t rsa -b 4096 -f ~/.ssh/keyname -C "comment"

# Change key passphrase
ssh-keygen -p -f ~/.ssh/keyname

# View public key fingerprint
ssh-keygen -lf ~/.ssh/keyname.pub

# Convert PEM to PPK (for PuTTY)
# Requires puttygen
puttygen keyname.pem -o keyname.ppk
```

### Permission Reference
```bash
chmod 700 ~/.ssh           # Directory
chmod 600 ~/.ssh/config    # Config file
chmod 400 ~/.ssh/*.pem     # Private keys
chmod 644 ~/.ssh/*.pub     # Public keys
```

### Troubleshooting Commands
```bash
# Verbose connection
ssh -v user@host

# Test SSH port connectivity
nc -zv host 22

# Check your IP (for security groups)
curl ifconfig.me

# Remove old host key
ssh-keygen -R hostname
```

---

## Windows-Specific Notes (PuTTY)

If using PuTTY instead of PowerShell SSH:

1. **Convert PEM to PPK**
   - Open PuTTYgen
   - Load your `.pem` file
   - Click "Save private key"
   - Save as `.ppk`

2. **Configure PuTTY Session**
   - Host Name: your EC2 public IP
   - Connection → SSH → Auth → Credentials
   - Browse to your `.ppk` file
   - Session → Save the configuration

3. **Auto-login Username**
   - Connection → Data → Auto-login username: `ec2-user`

---

## Clean-Up

No clean-up needed for this exercise. The key files and config are useful for remaining exercises.

---

## Additional Resources

- [SSH Key Management Best Practices](https://www.ssh.com/academy/ssh/keygen)
- [AWS EC2 Key Pairs](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html)
- [OpenSSH Manual](https://www.openssh.com/manual.html)

