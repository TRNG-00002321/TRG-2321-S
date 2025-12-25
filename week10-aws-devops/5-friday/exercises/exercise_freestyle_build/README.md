# Exercise 2: Freestyle Build Job

## Objective

Create a Jenkins freestyle job that clones a Git repository, executes build steps, runs tests, and archives artifacts.

---

## Learning Outcomes

By completing this exercise, you will:
- Create and configure freestyle jobs
- Integrate Git repositories with Jenkins
- Execute shell build steps
- Archive build artifacts
- View build history and console output
- Use build parameters

---

## Prerequisites

- Completed Exercise 1 (Jenkins running)
- Jenkins accessible at http://localhost:8080
- Internet connection (to clone public Git repos)

---

## Time Estimate

45 minutes

---

## Tasks

### Task 1: Create Hello World Job (10 minutes)

1. **Create New Item**
   - Click "New Item" on Dashboard
   - Name: `hello-world`
   - Type: `Freestyle project`
   - Click "OK"

2. **General Configuration**
   - Description: `My first Jenkins job - prints hello world`
   - ☑ Discard old builds
     - Max # of builds to keep: `5`

3. **Build Steps**
   - Click "Add build step" → "Execute shell"
   - Enter:
     ```bash
     echo "========================================="
     echo "Hello from Jenkins!"
     echo "========================================="
     echo "Build Number: $BUILD_NUMBER"
     echo "Build ID: $BUILD_ID"
     echo "Job Name: $JOB_NAME"
     echo "Workspace: $WORKSPACE"
     echo "Jenkins URL: $JENKINS_URL"
     echo "========================================="
     date
     hostname
     whoami
     pwd
     ls -la
     echo "========================================="
     echo "Build completed successfully!"
     ```

4. **Save and Build**
   - Click "Save"
   - Click "Build Now" (left sidebar)
   - Watch build appear in "Build History"

5. **View Results**
   - Click on build number (#1)
   - Click "Console Output"
   - Review the output

**Checkpoint:** First job runs successfully ✓

---

### Task 2: Git Integration Job (15 minutes)

1. **Create New Job**
   - New Item → Name: `git-clone-demo`
   - Type: `Freestyle project` → OK

2. **Source Code Management**
   - Select "Git"
   - Repository URL: `https://github.com/jenkins-docs/simple-java-maven-app.git`
   - Branch: `*/master`
   
   (This is an official Jenkins sample repository)

3. **Build Triggers**
   - ☑ Poll SCM
   - Schedule: `H/15 * * * *` (every 15 minutes)
   
   **Note:** The `H` provides distributed load timing

4. **Build Steps**
   - Add build step → Execute shell:
     ```bash
     echo "========================================="
     echo "Git Clone Demo Build"
     echo "========================================="
     echo "Repository cloned successfully!"
     echo ""
     echo "Directory contents:"
     ls -la
     echo ""
     echo "Git information:"
     git log --oneline -5
     echo ""
     echo "Files in project:"
     find . -type f -name "*.java" | head -10
     echo ""
     echo "README content:"
     head -20 README.md 2>/dev/null || echo "No README found"
     echo "========================================="
     ```

5. **Post-build Actions**
   - Add post-build action → "Archive the artifacts"
   - Files to archive: `**/*.java, **/*.xml, README.md`
   
   - Add post-build action → "Publish JUnit test result report" (if available)

6. **Save and Build**
   - Click Save
   - Click "Build Now"
   - View Console Output

7. **Check Artifacts**
   - After build completes, click on build number
   - Look for "Build Artifacts" section
   - Click to download/view archived files

**Checkpoint:** Git integration working ✓

---

### Task 3: Parameterized Build (10 minutes)

1. **Create Parameterized Job**
   - New Item → Name: `parameterized-build`
   - Type: Freestyle project → OK

2. **Enable Parameters**
   - ☑ This project is parameterized

3. **Add String Parameter**
   - Click "Add Parameter" → "String Parameter"
   ```
   Name: GREETING
   Default Value: Hello
   Description: The greeting message to display
   ```

4. **Add Choice Parameter**
   - Click "Add Parameter" → "Choice Parameter"
   ```
   Name: ENVIRONMENT
   Choices:
   development
   staging
   production
   Description: Target environment
   ```

5. **Add Boolean Parameter**
   - Click "Add Parameter" → "Boolean Parameter"
   ```
   Name: VERBOSE
   Default: checked
   Description: Enable verbose output
   ```

6. **Build Steps**
   - Add Execute shell:
     ```bash
     echo "========================================="
     echo "Parameterized Build Demo"
     echo "========================================="
     echo "Greeting: $GREETING"
     echo "Environment: $ENVIRONMENT"
     echo "Verbose: $VERBOSE"
     echo "========================================="
     
     if [ "$VERBOSE" = "true" ]; then
         echo "Verbose mode enabled - showing detailed info"
         echo "Build Number: $BUILD_NUMBER"
         echo "Workspace: $WORKSPACE"
         env | sort
     fi
     
     echo ""
     echo "$GREETING from Jenkins!"
     echo "Deploying to: $ENVIRONMENT"
     
     case $ENVIRONMENT in
         development)
             echo "Using development configuration"
             ;;
         staging)
             echo "Using staging configuration"
             ;;
         production)
             echo "PRODUCTION DEPLOYMENT - Extra validation required!"
             ;;
     esac
     
     echo "========================================="
     echo "Build completed!"
     ```

7. **Save and Build with Parameters**
   - Save
   - Click "Build with Parameters"
   - Modify values and click "Build"
   - Check Console Output

**Checkpoint:** Parameterized builds working ✓

---

### Task 4: Build with Test Results (10 minutes)

1. **Create Test Job**
   - New Item → Name: `test-results-demo`
   - Type: Freestyle project → OK

2. **Build Steps**
   - Add Execute shell:
     ```bash
     echo "Creating mock test results..."
     
     # Create test results directory
     mkdir -p test-results
     
     # Generate JUnit-style XML report
     cat > test-results/results.xml << 'XMLEOF'
     <?xml version="1.0" encoding="UTF-8"?>
     <testsuite name="DemoTests" tests="4" failures="0" errors="0" time="0.123">
         <testcase classname="com.example.DemoTest" name="testAddition" time="0.023"/>
         <testcase classname="com.example.DemoTest" name="testSubtraction" time="0.015"/>
         <testcase classname="com.example.DemoTest" name="testMultiplication" time="0.034"/>
         <testcase classname="com.example.DemoTest" name="testDivision" time="0.051"/>
     </testsuite>
     XMLEOF
     
     echo "Test results generated!"
     cat test-results/results.xml
     ```

3. **Post-build Actions**
   - Add → "Publish JUnit test result report"
   - Test report XMLs: `test-results/*.xml`
   
   - Add → "Archive the artifacts"
   - Files: `test-results/**`

4. **Build and View Results**
   - Build Now
   - Check "Test Result" link after build
   - View test trends on job page

**Checkpoint:** Test results published ✓

---

## Verification Checklist

- [ ] Hello World job runs successfully
- [ ] Git clone job fetches repository
- [ ] Artifacts are archived
- [ ] Parameterized job accepts and uses parameters
- [ ] Test results are published and visible
- [ ] Understand build history and console output

---

## Deliverables

1. Screenshot of Build History showing multiple builds
2. Screenshot of Console Output from git-clone-demo
3. Screenshot of Build with Parameters page
4. Screenshot of Test Results

---

## Jenkins Environment Variables

Useful variables in build steps:

| Variable | Description |
|----------|-------------|
| `$BUILD_NUMBER` | Current build number |
| `$BUILD_ID` | Build identifier |
| `$JOB_NAME` | Name of the job |
| `$WORKSPACE` | Absolute path to workspace |
| `$JENKINS_URL` | Jenkins server URL |
| `$GIT_COMMIT` | Git commit SHA |
| `$GIT_BRANCH` | Git branch name |

---

## Clean-Up

Keep jobs for reference. To delete a job:
1. Click on job name
2. Click "Delete Project" (left sidebar)
3. Confirm deletion

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| Git clone fails | Network/auth | Check URL, try public repo |
| Artifact not found | Wrong path | Check workspace contents |
| Build fails immediately | Script error | Check Console Output |
| Poll SCM not working | Cron syntax | Validate syntax |

---

## Additional Resources

- [Freestyle Projects Guide](https://www.jenkins.io/doc/book/using/building-a-free-style-software-project/)
- [Git Plugin](https://plugins.jenkins.io/git/)
- [Build Environment Variables](https://www.jenkins.io/doc/book/pipeline/jenkinsfile/#using-environment-variables)

