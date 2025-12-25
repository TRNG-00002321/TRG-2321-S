# Week 10 Tuesday Exercises: AWS Fundamentals & Core Services

## Overview

**Day:** Tuesday (Week 10)
**Topic:** AWS Fundamentals & Core Services
**Mode:** Implementation (Code Lab)
**Duration:** 3-4 hours total

These exercises reinforce your understanding of AWS core services. Each exercise builds practical skills you'll use throughout your DevOps journey.

---

## Exercise List

| # | Exercise | Focus Area | Duration |
|---|----------|------------|----------|
| 1 | [AWS Account Setup](./exercise_aws_account_setup/) | IAM, MFA, Billing | 30 min |
| 2 | [EC2 Web Server](./exercise_ec2_web_server/) | EC2, Security Groups, SSH | 45 min |
| 3 | [SSH Key Management](./exercise_ssh_key_management/) | SSH, Key Pairs, Troubleshooting | 30 min |
| 4 | [RDS Connection](./exercise_rds_connection/) | RDS, MySQL, Networking | 45 min |
| 5 | [S3 Static Site](./exercise_s3_static_site/) | S3, Static Hosting, Policies | 30 min |

---

## Prerequisites

- AWS Free Tier account (or provided sandbox access)
- AWS CLI installed and configured
- SSH client (terminal on Mac/Linux, PuTTY on Windows)
- Web browser
- Basic command line familiarity

---

## AWS Free Tier Reminders

⚠️ **Important Cost Awareness:**

1. **Always terminate resources** when exercises are complete
2. **Set billing alerts** before starting
3. **Use t2.micro/t3.micro** instances (free tier eligible)
4. **RDS Free Tier:** 750 hours/month of db.t2.micro or db.t3.micro
5. **S3:** 5 GB storage, 20,000 GET requests, 2,000 PUT requests

---

## Skill Mapping

| Exercise | Skills Practiced |
|----------|------------------|
| AWS Account Setup | IAM users, MFA, console navigation, billing |
| EC2 Web Server | Instance launching, security groups, AMIs |
| SSH Key Management | Key generation, permissions, connection troubleshooting |
| RDS Connection | Database creation, security configuration, SQL |
| S3 Static Site | Bucket creation, policies, static hosting |

---

## Success Criteria

Complete all exercises and verify:
- [ ] IAM user created with MFA enabled
- [ ] EC2 instance running and accessible via HTTP
- [ ] Successful SSH connection to EC2
- [ ] RDS database accessible from EC2
- [ ] S3 static website publicly accessible

---

## Getting Help

If stuck:
1. Review the written content in `../written/`
2. Reference the instructor demo guide in `../demos/INSTRUCTOR_GUIDE.md`
3. Check AWS documentation links provided in each exercise
4. Ask your instructor or pair programming partner

