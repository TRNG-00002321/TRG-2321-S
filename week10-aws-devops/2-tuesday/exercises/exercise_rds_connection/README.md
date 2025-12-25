# Exercise 4: RDS Connection

## Objective

Create an Amazon RDS MySQL database instance, configure networking for EC2 access, and establish a connection from your web server.

---

## Learning Outcomes

By completing this exercise, you will:
- Create an RDS database instance within Free Tier limits
- Configure security groups for EC2-to-RDS communication
- Understand RDS endpoints and connection strings
- Connect to MySQL from an EC2 instance
- Execute basic SQL queries on a cloud database

---

## Prerequisites

- Completed Exercise 2 (EC2 Web Server running)
- SSH access to your EC2 instance
- IAM user with appropriate permissions

---

## Time Estimate

45 minutes

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                          AWS VPC                                 │
│                                                                  │
│  ┌──────────────────────┐       ┌──────────────────────┐       │
│  │    EC2 Instance      │       │    RDS Instance      │       │
│  │                      │       │                      │       │
│  │  ┌────────────────┐  │       │  ┌────────────────┐  │       │
│  │  │  MySQL Client  │──┼───────┼─▶│     MySQL      │  │       │
│  │  │                │  │ Port  │  │   Database     │  │       │
│  │  └────────────────┘  │ 3306  │  │                │  │       │
│  │                      │       │  └────────────────┘  │       │
│  │  Security Group:     │       │  Security Group:     │       │
│  │  web-server-sg       │       │  rds-mysql-sg        │       │
│  └──────────────────────┘       └──────────────────────┘       │
│                                                                  │
│  Security Group Rule:                                           │
│  rds-mysql-sg allows inbound port 3306 from web-server-sg      │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Tasks

### Task 1: Create RDS Instance (20 minutes)

1. **Navigate to RDS**
   - `Services → RDS → Create database`

2. **Choose Database Creation Method**
   ```
   ○ Standard create (selected)
   ```

3. **Engine Options**
   ```
   Engine type: MySQL
   Version: MySQL 8.0.x (latest available)
   ```

4. **Templates**
   ```
   ○ Free tier (selected)
   ```
   
   ⚠️ Important: This limits options to free tier eligible settings.

5. **Settings**
   ```
   DB instance identifier: week10-database
   
   Credentials Settings:
   Master username: admin
   
   ○ Self managed
   Master password: Training2024!
   Confirm password: Training2024!
   ```
   
   📝 Note: In production, use AWS Secrets Manager!

6. **Instance Configuration**
   ```
   DB instance class: db.t3.micro (or db.t2.micro)
   ✓ Burstable classes
   ```

7. **Storage**
   ```
   Storage type: gp2
   Allocated storage: 20 GiB
   ☑ Enable storage autoscaling
   Maximum storage threshold: 100 GiB
   ```

8. **Connectivity**
   ```
   Compute resource: Don't connect to an EC2 compute resource
   
   Network type: IPv4
   
   Virtual private cloud (VPC): Default VPC
   
   DB Subnet group: default
   
   Public access: No
   
   VPC security group:
   ○ Create new
   New VPC security group name: rds-mysql-sg
   
   Availability Zone: No preference
   ```

9. **Database Authentication**
   ```
   ○ Password authentication
   ```

10. **Additional Configuration** (expand section)
    ```
    Initial database name: appdb
    
    Backup:
    ☑ Enable automated backups
    Backup retention period: 7 days
    
    Monitoring:
    ☐ Enable Enhanced monitoring (costs extra)
    
    Maintenance:
    ☑ Enable auto minor version upgrade
    ```

11. **Create Database**
    - Review estimated monthly costs (should be $0 for free tier)
    - Click "Create database"
    - Wait 10-15 minutes for creation

12. **Note the Endpoint**
    - Click on your database
    - Under "Connectivity & security"
    - Copy the **Endpoint** (e.g., `week10-database.xxxxxxxxxx.us-east-1.rds.amazonaws.com`)

**Checkpoint:** RDS instance is "Available" ✓

---

### Task 2: Configure Security Groups (10 minutes)

Allow your EC2 instance to connect to RDS.

1. **Find the RDS Security Group**
   - `EC2 → Security Groups`
   - Find `rds-mysql-sg`
   - Click on it

2. **Edit Inbound Rules**
   - Click "Inbound rules" tab
   - Click "Edit inbound rules"
   
3. **Add Rule for EC2 Access**
   ```
   Type: MySQL/Aurora
   Protocol: TCP
   Port: 3306
   Source: Custom → type "sg-" and select "web-server-sg"
   Description: Allow MySQL from EC2 web server
   ```
   
4. **Save Rules**

**Why Security Group Reference?**
Instead of specifying an IP, we reference the EC2's security group. This means:
- Any instance in `web-server-sg` can connect
- Works even when EC2 IPs change
- More maintainable than IP-based rules

**Checkpoint:** Security group allows EC2 to connect ✓

---

### Task 3: Connect from EC2 (15 minutes)

1. **SSH to Your EC2 Instance**
   ```bash
   ssh web-server
   # Or: ssh -i ~/.ssh/web-server-key.pem ec2-user@<EC2_IP>
   ```

2. **Install MySQL Client**
   ```bash
   sudo yum install -y mysql
   ```

3. **Connect to RDS**
   ```bash
   mysql -h <RDS_ENDPOINT> -u admin -p
   ```
   
   Replace `<RDS_ENDPOINT>` with your actual endpoint.
   
   Enter password: `Training2024!`

4. **Verify Connection**
   ```sql
   -- Check connection
   SELECT VERSION();
   
   -- Show databases
   SHOW DATABASES;
   ```
   
   You should see `appdb` in the list.

**Checkpoint:** Connected to RDS from EC2 ✓

---

### Task 4: Create and Populate Database (5 minutes)

1. **Use Your Database**
   ```sql
   USE appdb;
   ```

2. **Create a Table**
   ```sql
   CREATE TABLE users (
       id INT AUTO_INCREMENT PRIMARY KEY,
       username VARCHAR(50) NOT NULL UNIQUE,
       email VARCHAR(100) NOT NULL,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   ```

3. **Insert Data**
   ```sql
   INSERT INTO users (username, email) VALUES 
       ('alice', 'alice@example.com'),
       ('bob', 'bob@example.com'),
       ('charlie', 'charlie@example.com');
   ```

4. **Query Data**
   ```sql
   SELECT * FROM users;
   
   SELECT username, email FROM users WHERE id > 1;
   ```

5. **Check Table Structure**
   ```sql
   DESCRIBE users;
   
   SHOW CREATE TABLE users;
   ```

6. **Exit MySQL**
   ```sql
   EXIT;
   ```

**Checkpoint:** Database operations successful ✓

---

## Verification Checklist

- [ ] RDS instance is in "Available" state
- [ ] Endpoint is noted
- [ ] Security group allows EC2 on port 3306
- [ ] MySQL client installed on EC2
- [ ] Successfully connected from EC2 to RDS
- [ ] Created table and inserted data
- [ ] Queried data successfully

---

## Deliverables

1. Screenshot of RDS Console showing your database instance
2. Screenshot of security group inbound rules
3. Terminal output showing successful MySQL connection and queries

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| Connection timeout | Security group | Verify inbound rule references correct SG |
| Access denied | Wrong credentials | Double-check username and password |
| Unknown host | Wrong endpoint | Copy endpoint exactly from RDS console |
| Can't connect externally | Public access disabled | RDS is private—connect via EC2 |

### Debug Connection Issues

```bash
# From EC2, test port connectivity
nc -zv <RDS_ENDPOINT> 3306

# Check DNS resolution
nslookup <RDS_ENDPOINT>

# Detailed MySQL error
mysql -h <RDS_ENDPOINT> -u admin -p --verbose
```

---

## Clean-Up

⚠️ **Important:** RDS charges apply even when idle!

**When Done:**
1. Go to RDS Console
2. Select your database
3. Actions → Delete
4. Uncheck "Create final snapshot" (for exercises)
5. Check "I acknowledge..."
6. Type "delete me" to confirm
7. Delete

Also clean up:
- Delete the `rds-mysql-sg` security group (EC2 → Security Groups)

---

## Challenge (Optional)

1. **Create a Read Replica**
   - Actions → Create read replica
   - Test read queries against replica

2. **Enable Multi-AZ**
   - Modify instance
   - Enable Multi-AZ deployment
   - Observe failover behavior

3. **Parameter Groups**
   - Create custom parameter group
   - Modify character set settings

---

## Additional Resources

- [RDS User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/)
- [RDS Best Practices](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_BestPractices.html)
- [MySQL 8.0 Reference Manual](https://dev.mysql.com/doc/refman/8.0/en/)

