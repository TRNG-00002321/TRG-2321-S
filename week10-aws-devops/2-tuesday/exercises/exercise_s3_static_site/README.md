# Exercise 5: S3 Static Website

## Objective

Create an S3 bucket configured for static website hosting, set up proper permissions, and deploy a simple website accessible from the internet.

---

## Learning Outcomes

By completing this exercise, you will:
- Create and configure S3 buckets
- Understand S3 bucket policies and public access settings
- Enable static website hosting
- Upload files using Console and CLI
- Understand S3 storage classes and versioning

---

## Prerequisites

- Completed Exercise 1 (AWS Account Setup)
- AWS CLI installed and configured
- Basic HTML knowledge

---

## Time Estimate

30 minutes

---

## Architecture

```
                    Internet
                        │
                        ▼
            ┌───────────────────────┐
            │    User's Browser     │
            └───────────┬───────────┘
                        │
                        │ HTTP Request
                        │ GET /index.html
                        ▼
        ┌───────────────────────────────────┐
        │           Amazon S3               │
        │  ┌─────────────────────────────┐  │
        │  │  Bucket: my-static-site     │  │
        │  │  ┌─────────────────────┐    │  │
        │  │  │   index.html        │    │  │
        │  │  │   error.html        │    │  │
        │  │  │   style.css         │    │  │
        │  │  │   /images/          │    │  │
        │  │  └─────────────────────┘    │  │
        │  │                             │  │
        │  │  Static Website Hosting:    │  │
        │  │  ✓ Enabled                  │  │
        │  │  Index: index.html          │  │
        │  │  Error: error.html          │  │
        │  └─────────────────────────────┘  │
        │                                   │
        │  Bucket Policy:                   │
        │  Allow s3:GetObject for everyone │
        └───────────────────────────────────┘
                        │
                        │ HTTP Response
                        │ HTML Content
                        ▼
            ┌───────────────────────┐
            │    Website Displays   │
            └───────────────────────┘
```

---

## Tasks

### Task 1: Create S3 Bucket (10 minutes)

1. **Navigate to S3**
   - `Services → S3 → Create bucket`

2. **Configure Bucket**
   ```
   Bucket name: week10-static-site-<YOUR_UNIQUE_ID>
   
   Note: Bucket names must be globally unique!
   Example: week10-static-site-johndoe-2024
   ```

3. **AWS Region**
   ```
   Region: Same as your other resources (e.g., us-east-1)
   ```

4. **Object Ownership**
   ```
   ○ ACLs disabled (recommended)
   ```

5. **Block Public Access Settings**
   ```
   ☐ Block all public access (UNCHECK this)
   
   ⚠️ Acknowledge warning:
   ☑ I acknowledge that the current settings might result in this bucket 
     and the objects within becoming public.
   ```

6. **Bucket Versioning**
   ```
   ○ Enable
   ```
   
   Versioning helps recover accidentally deleted or overwritten files.

7. **Default Encryption**
   ```
   Server-side encryption: Enable
   Encryption type: SSE-S3
   Bucket Key: Enable
   ```

8. **Create Bucket**
   - Click "Create bucket"

**Checkpoint:** Bucket created ✓

---

### Task 2: Enable Static Website Hosting (5 minutes)

1. **Open Your Bucket**
   - Click on your bucket name

2. **Go to Properties Tab**
   - Click "Properties"
   - Scroll to "Static website hosting"
   - Click "Edit"

3. **Configure Static Hosting**
   ```
   Static website hosting: Enable
   
   Hosting type: Host a static website
   
   Index document: index.html
   Error document: error.html
   ```

4. **Save Changes**

5. **Note the Website Endpoint**
   - After saving, note the bucket website endpoint:
   ```
   http://week10-static-site-<ID>.s3-website-<region>.amazonaws.com
   ```

**Checkpoint:** Static hosting enabled ✓

---

### Task 3: Add Bucket Policy (5 minutes)

Allow public read access to your website files.

1. **Go to Permissions Tab**
   - Click "Permissions"
   - Scroll to "Bucket policy"
   - Click "Edit"

2. **Add Policy**
   ```json
   {
       "Version": "2012-10-17",
       "Statement": [
           {
               "Sid": "PublicReadGetObject",
               "Effect": "Allow",
               "Principal": "*",
               "Action": "s3:GetObject",
               "Resource": "arn:aws:s3:::week10-static-site-<YOUR_UNIQUE_ID>/*"
           }
       ]
   }
   ```
   
   ⚠️ Replace `week10-static-site-<YOUR_UNIQUE_ID>` with your actual bucket name!

3. **Save Changes**

**Checkpoint:** Public read access configured ✓

---

### Task 4: Create and Upload Website Files (10 minutes)

#### Option A: Using AWS Console

1. **Create Files Locally**

   Create `index.html`:
   ```html
   <!DOCTYPE html>
   <html lang="en">
   <head>
       <meta charset="UTF-8">
       <meta name="viewport" content="width=device-width, initial-scale=1.0">
       <title>Week 10 - S3 Static Website</title>
       <style>
           * { margin: 0; padding: 0; box-sizing: border-box; }
           body {
               font-family: 'Segoe UI', system-ui, sans-serif;
               min-height: 100vh;
               background: #0f0f23;
               color: #cccccc;
               display: flex;
               justify-content: center;
               align-items: center;
               padding: 20px;
           }
           .container {
               max-width: 800px;
               text-align: center;
           }
           h1 {
               font-size: 3rem;
               margin-bottom: 1rem;
               background: linear-gradient(135deg, #00d4ff, #7c3aed);
               -webkit-background-clip: text;
               -webkit-text-fill-color: transparent;
               background-clip: text;
           }
           .card {
               background: rgba(255,255,255,0.05);
               border: 1px solid rgba(255,255,255,0.1);
               border-radius: 16px;
               padding: 2rem;
               margin: 2rem 0;
               backdrop-filter: blur(10px);
           }
           .feature {
               display: flex;
               align-items: center;
               gap: 1rem;
               margin: 1rem 0;
               padding: 1rem;
               background: rgba(0,212,255,0.1);
               border-radius: 8px;
           }
           .feature-icon {
               font-size: 2rem;
           }
           .badge {
               display: inline-block;
               background: linear-gradient(135deg, #00d4ff, #7c3aed);
               color: white;
               padding: 0.25rem 0.75rem;
               border-radius: 20px;
               font-size: 0.8rem;
               margin-top: 1rem;
           }
           a { color: #00d4ff; }
       </style>
   </head>
   <body>
       <div class="container">
           <h1>🚀 S3 Static Website</h1>
           <p>Hosted on Amazon S3 - No servers required!</p>
           
           <div class="card">
               <h2>Week 10: From Code to Cloud</h2>
               <p>This website demonstrates S3 static hosting capabilities.</p>
               
               <div class="feature">
                   <span class="feature-icon">📦</span>
                   <div>
                       <strong>Simple Storage Service</strong>
                       <p>Unlimited storage, pay only for what you use</p>
                   </div>
               </div>
               
               <div class="feature">
                   <span class="feature-icon">🌐</span>
                   <div>
                       <strong>Global Distribution</strong>
                       <p>Combine with CloudFront for worldwide delivery</p>
                   </div>
               </div>
               
               <div class="feature">
                   <span class="feature-icon">💰</span>
                   <div>
                       <strong>Cost Effective</strong>
                       <p>Perfect for static content, documentation, SPAs</p>
                   </div>
               </div>
               
               <span class="badge">AWS Free Tier Eligible</span>
           </div>
           
           <p>Test the <a href="nonexistent-page.html">error page</a></p>
       </div>
   </body>
   </html>
   ```

   Create `error.html`:
   ```html
   <!DOCTYPE html>
   <html lang="en">
   <head>
       <meta charset="UTF-8">
       <title>404 - Page Not Found</title>
       <style>
           body {
               font-family: system-ui, sans-serif;
               min-height: 100vh;
               background: #0f0f23;
               color: #cccccc;
               display: flex;
               justify-content: center;
               align-items: center;
               flex-direction: column;
           }
           h1 { 
               font-size: 6rem; 
               color: #ff6b6b;
               margin: 0;
           }
           p { margin: 1rem 0; }
           a {
               color: #00d4ff;
               text-decoration: none;
               padding: 0.5rem 1rem;
               border: 1px solid #00d4ff;
               border-radius: 8px;
           }
           a:hover { background: rgba(0,212,255,0.2); }
       </style>
   </head>
   <body>
       <h1>404</h1>
       <p>Oops! The page you're looking for doesn't exist.</p>
       <a href="index.html">← Back to Home</a>
   </body>
   </html>
   ```

2. **Upload via Console**
   - Click "Upload"
   - Drag and drop both files
   - Click "Upload"
   - Click "Close"

#### Option B: Using AWS CLI

```bash
# Create a directory for your website
mkdir -p ~/s3-website
cd ~/s3-website

# Create the HTML files (copy content from above)
# Or download them if provided

# Upload using AWS CLI
aws s3 cp index.html s3://week10-static-site-<YOUR_ID>/
aws s3 cp error.html s3://week10-static-site-<YOUR_ID>/

# Verify upload
aws s3 ls s3://week10-static-site-<YOUR_ID>/

# Sync entire directory (useful for multiple files)
aws s3 sync . s3://week10-static-site-<YOUR_ID>/ --exclude ".DS_Store"
```

---

### Task 5: Test Your Website (5 minutes)

1. **Access via Website Endpoint**
   - Open the bucket website endpoint in your browser:
   ```
   http://week10-static-site-<ID>.s3-website-<region>.amazonaws.com
   ```

2. **Test Error Page**
   - Navigate to a non-existent page:
   ```
   http://week10-static-site-<ID>.s3-website-<region>.amazonaws.com/fake-page
   ```
   - Should show your custom error page

3. **Test Versioning** (Optional)
   - Modify `index.html` and re-upload
   - Go to bucket → "Show versions"
   - See version history

**Checkpoint:** Website accessible from internet ✓

---

## Verification Checklist

- [ ] S3 bucket created with unique name
- [ ] Block public access disabled (with acknowledgment)
- [ ] Static website hosting enabled
- [ ] Bucket policy allows public read
- [ ] index.html uploaded and displays correctly
- [ ] error.html works for 404 errors
- [ ] Website endpoint documented

---

## Deliverables

1. Screenshot of your website in browser (showing S3 URL)
2. Screenshot of bucket overview showing files
3. Your bucket policy JSON
4. The bucket website endpoint URL

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| 403 Forbidden | Bucket policy | Verify policy allows GetObject |
| 404 Not Found | Index document | Check static hosting settings |
| Access Denied | Public access blocked | Disable block public access |
| Can't access bucket | Wrong endpoint | Use website endpoint, not S3 URL |

### Endpoint Types

```
# S3 Object URL (won't render HTML properly)
https://bucket-name.s3.amazonaws.com/index.html

# Website Endpoint (use this for websites!)
http://bucket-name.s3-website-region.amazonaws.com
```

---

## Clean-Up

**When Done:**

```bash
# Delete all objects in bucket
aws s3 rm s3://week10-static-site-<YOUR_ID> --recursive

# Delete the bucket
aws s3 rb s3://week10-static-site-<YOUR_ID>
```

Or via Console:
1. Select bucket
2. Click "Empty" (must empty first)
3. Type "permanently delete"
4. Then "Delete bucket"

---

## Challenge (Optional)

1. **Add a Custom Domain**
   - Register domain in Route 53
   - Create CNAME record pointing to S3 endpoint
   - Bucket name must match domain name!

2. **Add CloudFront Distribution**
   - Create CloudFront distribution
   - Point to S3 bucket
   - Enable HTTPS
   - Add caching rules

3. **Create a Simple SPA**
   - Add JavaScript for dynamic content
   - Implement client-side routing
   - Configure error document for SPA

---

## AWS CLI Quick Reference

```bash
# List buckets
aws s3 ls

# List bucket contents
aws s3 ls s3://bucket-name/

# Upload file
aws s3 cp file.html s3://bucket-name/

# Upload with content type
aws s3 cp file.html s3://bucket-name/ --content-type "text/html"

# Sync directory
aws s3 sync ./local-dir s3://bucket-name/

# Delete file
aws s3 rm s3://bucket-name/file.html

# Delete all files
aws s3 rm s3://bucket-name/ --recursive

# Download file
aws s3 cp s3://bucket-name/file.html ./

# Copy between buckets
aws s3 cp s3://source-bucket/file s3://dest-bucket/
```

---

## Additional Resources

- [S3 Static Website Hosting](https://docs.aws.amazon.com/AmazonS3/latest/userguide/WebsiteHosting.html)
- [S3 Bucket Policies](https://docs.aws.amazon.com/AmazonS3/latest/userguide/bucket-policies.html)
- [S3 Pricing](https://aws.amazon.com/s3/pricing/)

