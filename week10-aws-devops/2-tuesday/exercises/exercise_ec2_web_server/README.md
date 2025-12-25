# Exercise 2: EC2 Web Server

## Objective

Launch an EC2 instance, configure security groups properly, and run a simple web server accessible from the internet.

---

## Learning Outcomes

By completing this exercise, you will:
- Launch an EC2 instance from the AWS Console
- Create and configure security groups (virtual firewalls)
- Understand AMI selection and instance types
- Install and run a web server on EC2
- Access your server via public IP

---

## Prerequisites

- Completed Exercise 1 (AWS Account Setup)
- Logged in as your IAM admin user
- SSH client available

---

## Time Estimate

45 minutes

---

## Architecture

```
                    Internet
                        │
                        ▼
                ┌───────────────┐
                │  Your Browser │
                └───────┬───────┘
                        │
                        ▼ HTTP (Port 80)
                ┌───────────────────────┐
                │    Security Group     │
                │  ┌─────────────────┐  │
                │  │  Inbound Rules  │  │
                │  │  Port 22: SSH   │  │
                │  │  Port 80: HTTP  │  │
                │  └─────────────────┘  │
                └───────────┬───────────┘
                            │
                            ▼
                ┌───────────────────────┐
                │      EC2 Instance     │
                │    Amazon Linux 2023  │
                │    t2.micro / t3.micro│
                │    ┌─────────────┐    │
                │    │    nginx    │    │
                │    │  Web Server │    │
                │    └─────────────┘    │
                └───────────────────────┘
```

---

## Tasks

### Task 1: Launch EC2 Instance (20 minutes)

1. **Navigate to EC2**
   - Log into AWS Console as your IAM user
   - `Services → EC2 → Instances → Launch instances`

2. **Name Your Instance**
   ```
   Name: web-server-exercise
   ```

3. **Select AMI (Amazon Machine Image)**
   ```
   Amazon Linux 2023 AMI
   Architecture: 64-bit (x86)
   ✓ Free tier eligible
   ```

4. **Choose Instance Type**
   ```
   t2.micro (Free tier eligible)
   - 1 vCPU
   - 1 GiB Memory
   ```

5. **Create Key Pair**
   - Click "Create new key pair"
   ```
   Key pair name: web-server-key
   Key pair type: RSA
   Private key format: .pem (for OpenSSH)
   ```
   - Click "Create key pair"
   - **Save the downloaded file safely!**

6. **Configure Network Settings**
   - Click "Edit" next to Network settings
   ```
   VPC: (default)
   Subnet: No preference
   Auto-assign public IP: Enable
   
   Security Group: Create security group
   Security group name: web-server-sg
   Description: Security group for web server exercise
   
   Inbound Rules:
   Rule 1:
     Type: SSH
     Source: My IP
   
   Rule 2 (Add):
     Type: HTTP
     Source: Anywhere (0.0.0.0/0)
   ```

7. **Configure Storage**
   ```
   8 GiB gp3 (default, free tier eligible)
   ```

8. **Launch Instance**
   - Review settings in Summary panel
   - Click "Launch instance"
   - Click "View all instances"

9. **Wait for Running State**
   - Refresh until Status is "Running"
   - Note the **Public IPv4 address**

**Checkpoint:** EC2 instance is running ✓

---

### Task 2: Connect and Install Web Server (15 minutes)

1. **Set Key Permissions** (in your terminal)
   
   **macOS/Linux:**
   ```bash
   # Move key to .ssh directory
   mv ~/Downloads/web-server-key.pem ~/.ssh/
   
   # Set permissions (required!)
   chmod 400 ~/.ssh/web-server-key.pem
   ```
   
   **Windows PowerShell:**
   ```powershell
   # Move key
   Move-Item .\web-server-key.pem $env:USERPROFILE\.ssh\
   
   # Set permissions
   icacls "$env:USERPROFILE\.ssh\web-server-key.pem" /inheritance:r
   icacls "$env:USERPROFILE\.ssh\web-server-key.pem" /grant:r "$($env:USERNAME):R"
   ```

2. **Connect via SSH**
   ```bash
   ssh -i ~/.ssh/web-server-key.pem ec2-user@<YOUR_PUBLIC_IP>
   ```
   
   Type `yes` when prompted about host authenticity.

3. **Update System**
   ```bash
   sudo yum update -y
   ```

4. **Install nginx**
   ```bash
   sudo yum install -y nginx
   ```

5. **Start nginx**
   ```bash
   sudo systemctl start nginx
   sudo systemctl enable nginx
   ```

6. **Verify nginx is Running**
   ```bash
   sudo systemctl status nginx
   ```

**Checkpoint:** nginx installed and running ✓

---

### Task 3: Customize and Test (10 minutes)

1. **Create Custom HTML Page**
   ```bash
   sudo bash -c 'cat > /usr/share/nginx/html/index.html << EOF
   <!DOCTYPE html>
   <html>
   <head>
       <title>Week 10 - AWS Exercise</title>
       <style>
           body {
               font-family: system-ui, -apple-system, sans-serif;
               max-width: 800px;
               margin: 0 auto;
               padding: 40px;
               background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
               min-height: 100vh;
               color: #eee;
           }
           .card {
               background: rgba(255,255,255,0.1);
               padding: 30px;
               border-radius: 15px;
               backdrop-filter: blur(10px);
               border: 1px solid rgba(255,255,255,0.1);
           }
           h1 { color: #00d9ff; }
           .meta { color: #888; font-size: 0.9em; }
           code { 
               background: rgba(0,217,255,0.2); 
               padding: 2px 8px; 
               border-radius: 4px;
           }
       </style>
   </head>
   <body>
       <div class="card">
           <h1>🚀 Hello from AWS EC2!</h1>
           <p>This web server is running on:</p>
           <ul>
               <li><strong>Service:</strong> Amazon EC2</li>
               <li><strong>OS:</strong> Amazon Linux 2023</li>
               <li><strong>Web Server:</strong> nginx</li>
               <li><strong>Instance Type:</strong> t2.micro</li>
           </ul>
           <p class="meta">
               Instance ID: $(curl -s http://169.254.169.254/latest/meta-data/instance-id)<br>
               Availability Zone: $(curl -s http://169.254.169.254/latest/meta-data/placement/availability-zone)
           </p>
       </div>
   </body>
   </html>
   EOF'
   ```

2. **Test Locally on EC2**
   ```bash
   curl localhost
   ```

3. **Test from Your Browser**
   - Open your browser
   - Navigate to: `http://<YOUR_PUBLIC_IP>`
   - You should see your custom page!

4. **Verify Security Group**
   - Try accessing via HTTPS: `https://<YOUR_PUBLIC_IP>`
   - Should fail (port 443 not open)
   - This confirms security groups are working

**Checkpoint:** Web server accessible from internet ✓

---

## Verification Checklist

- [ ] EC2 instance is running
- [ ] Security group allows SSH (port 22) from your IP
- [ ] Security group allows HTTP (port 80) from anywhere
- [ ] Successfully connected via SSH
- [ ] nginx is installed and running
- [ ] Custom HTML page displays in browser
- [ ] Public IP address documented

---

## Deliverables

1. Screenshot of your web page in browser showing public IP in URL bar
2. Screenshot of EC2 Console showing your running instance
3. Screenshot of security group inbound rules

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| SSH timeout | Security group | Verify port 22 allows your IP |
| Permission denied (SSH) | Key permissions | Run `chmod 400` on key file |
| Page not loading | Security group | Verify port 80 allows 0.0.0.0/0 |
| nginx not starting | Package issues | Run `sudo yum install -y nginx` again |

---

## Clean-Up

⚠️ **Important:** Complete clean-up after finishing ALL exercises to avoid charges.

**If done for the day:**
1. **Stop Instance** (keeps data, no compute charges)
   - Select instance → Instance state → Stop instance

**If completely done:**
1. **Terminate Instance** (deletes everything)
   - Select instance → Instance state → Terminate instance
2. **Delete Key Pair** (optional)
   - EC2 → Key Pairs → Select → Actions → Delete

---

## Challenge (Optional)

Add HTTPS support:
1. Create a self-signed certificate
2. Configure nginx for HTTPS
3. Update security group to allow port 443
4. Test HTTPS access

---

## Additional Resources

- [EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/)
- [Security Groups Documentation](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_SecurityGroups.html)
- [nginx Documentation](https://nginx.org/en/docs/)

