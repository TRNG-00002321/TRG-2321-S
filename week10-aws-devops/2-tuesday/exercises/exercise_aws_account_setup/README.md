# Exercise 1: AWS Account Setup

## Objective

Set up your AWS environment securely following best practices: create an IAM user with appropriate permissions, enable MFA, and configure billing alerts.

---

## Learning Outcomes

By completing this exercise, you will:
- Navigate the AWS Management Console confidently
- Create an IAM user following the principle of least privilege
- Enable Multi-Factor Authentication (MFA) for account security
- Configure billing alerts to avoid unexpected charges

---

## Prerequisites

- AWS Free Tier account (sign up at https://aws.amazon.com/free/)
- Smartphone with authenticator app (Google Authenticator, Authy, or Microsoft Authenticator)
- Web browser

---

## Time Estimate

30 minutes

---

## Tasks

### Task 1: Secure the Root Account (10 minutes)

The root account has full access to everything—it should rarely be used.

1. **Log into AWS Console** as root user
   - Go to https://console.aws.amazon.com
   - Use your email and password

2. **Enable MFA on Root Account**
   - Navigate: `Services → IAM → Dashboard`
   - Look for "Security recommendations"
   - Click "Add MFA"
   - Select "Virtual MFA device"
   - Open your authenticator app
   - Scan the QR code
   - Enter two consecutive MFA codes
   - Click "Assign MFA"

3. **Verify MFA**
   - Sign out of AWS Console
   - Sign back in
   - Confirm you're prompted for MFA code

**Checkpoint:** Root account has MFA enabled ✓

---

### Task 2: Create an IAM Admin User (10 minutes)

You'll use this account for daily work instead of root.

1. **Navigate to IAM Users**
   - `Services → IAM → Users → Add users`

2. **Configure User**
   ```
   User name: admin-trainee
   ☑ Provide user access to the AWS Management Console
   ○ I want to create an IAM user
   
   Console password: 
   ○ Custom password (create a strong one!)
   ☐ User must create new password at next sign-in (uncheck for lab)
   ```

3. **Set Permissions**
   - Click "Next"
   - Select "Attach policies directly"
   - Search and select: `AdministratorAccess`
   - Click "Next"

4. **Review and Create**
   - Review the summary
   - Click "Create user"
   - **Important:** Save the sign-in URL and credentials!

5. **Enable MFA on IAM User**
   - Click on your new user
   - Go to "Security credentials" tab
   - Click "Assign MFA device"
   - Follow the same process as root

**Checkpoint:** IAM admin user created with MFA ✓

---

### Task 3: Configure Billing Alerts (10 minutes)

Protect yourself from unexpected charges.

1. **Access Billing Dashboard**
   - Click your account name (top right)
   - Select "Billing Dashboard"
   - Or navigate: `Services → Billing`

2. **Enable Billing Alerts**
   - Go to "Billing preferences" (left menu)
   - Under "Alert preferences," click "Edit"
   - Enable: `Receive CloudWatch Billing Alerts`
   - Save

3. **Create a Budget**
   - Navigate: `Billing → Budgets → Create a budget`
   - Select "Use a template"
   - Choose "Zero spend budget" (alerts on any charge)
   - Enter your email for notifications
   - Click "Create budget"

4. **Alternative: Create Custom Budget**
   - Select "Customize"
   - Choose "Cost budget"
   - Set monthly budget amount: $10
   - Configure alert at 80% threshold
   - Add your email
   - Create budget

**Checkpoint:** Billing alerts configured ✓

---

## Verification Checklist

Complete these checks before finishing:

- [ ] Root account has MFA enabled
- [ ] IAM admin user `admin-trainee` created
- [ ] IAM admin user has AdministratorAccess policy attached
- [ ] IAM admin user has MFA enabled
- [ ] Billing alert or budget is configured
- [ ] You can log in as `admin-trainee` (test it!)

---

## Deliverables

Take screenshots of:
1. IAM Dashboard showing MFA is active for root
2. IAM Users list showing your admin user
3. Billing Budgets page showing your configured budget

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Can't find IAM | Search "IAM" in the services search bar |
| MFA not working | Ensure phone time is synced; try re-scanning QR |
| No billing access | Billing may be restricted in sandbox environments |
| Forgot IAM password | Root can reset IAM user passwords |

---

## Best Practices Learned

1. **Never use root for daily tasks** - Use IAM users
2. **Always enable MFA** - On all accounts
3. **Least privilege** - Only grant permissions needed
4. **Monitor costs** - Set up billing alerts immediately
5. **Strong passwords** - Use unique, complex passwords

---

## Clean-Up

No clean-up needed—you'll use this IAM user for remaining exercises.

---

## Additional Resources

- [IAM Best Practices](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html)
- [Setting Up MFA](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_mfa.html)
- [AWS Budgets](https://docs.aws.amazon.com/cost-management/latest/userguide/budgets-managing-costs.html)

