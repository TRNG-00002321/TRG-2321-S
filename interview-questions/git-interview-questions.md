# Git Interview Questions & Answers

## Beginner Level

**Q1: What is Git and why is version control important?**

> **Answer:** Git is a distributed version control system that tracks changes in source code during software development.
>
> **Why it's important:**
> - **Collaboration:** Multiple developers can work simultaneously without conflicts
> - **History tracking:** Every change is recorded with author, timestamp, and message
> - **Branching:** Isolated development environments for features and experiments
> - **Recovery:** Easy rollback to previous working versions
> - **Accountability:** Clear audit trail of who changed what and when
>
> **Example:** "In my project, we used Git to have 5 developers working on different features simultaneously. When a bug was introduced, we used `git bisect` to identify the exact commit that caused the issue."

---

**Q2: Explain the difference between Git and GitHub.**

> **Answer:**
> - **Git:** Version control software that runs locally on your machine. Manages repositories, tracks changes, handles branching/merging.
> - **GitHub:** Cloud-based hosting platform for Git repositories. Adds collaboration features like pull requests, issues, Actions (CI/CD), and project management.
>
> **Analogy:** Git is like Microsoft Word's "Track Changes," while GitHub is like Google Docs—adding cloud storage and collaboration on top.
>
> **Alternatives to GitHub:** GitLab, Bitbucket, Azure DevOps

---

**Q3: What are the three states in Git (working directory, staging area, repository)?**

> **Answer:**
> 1. **Working Directory:** Your actual project files where you make edits
> 2. **Staging Area (Index):** A preparation area for your next commit—you choose which changes to include
> 3. **Repository (.git):** Permanent snapshots of your committed changes
>
> **Workflow:**
> ```bash
> # Edit files (working directory)
> echo "new code" >> app.py
> 
> # Stage changes (staging area)
> git add app.py
> 
> # Commit (repository)
> git commit -m "Add new feature"
> ```
>
> **Why staging exists:** Allows you to commit logically related changes together, even if you modified multiple unrelated things.

---

**Q4: How do you create and switch between branches?**

> **Answer:**
> ```bash
> # Create new branch
> git branch feature-login
> 
> # Switch to branch
> git checkout feature-login
> 
> # Create AND switch (shortcut)
> git checkout -b feature-login
> 
> # Modern alternative (Git 2.23+)
> git switch -c feature-login
> 
> # List branches
> git branch        # Local
> git branch -a     # All (including remote)
> 
> # Delete branch
> git branch -d feature-login   # Safe delete
> git branch -D feature-login   # Force delete
> ```
>
> **Best practice:** Always create branches from an up-to-date main branch.

---

**Q5: What is a merge conflict and how do you resolve it?**

> **Answer:** A merge conflict occurs when Git cannot automatically combine changes because the same lines were modified in different branches.
>
> **Conflict markers:**
> ```python
> <<<<<<< HEAD
> return calculate_total(items)
> =======
> return sum(item.price for item in items)
> >>>>>>> feature-branch
> ```
>
> **Resolution steps:**
> 1. Identify conflicts: `git status`
> 2. Open conflicted files
> 3. Choose correct code, remove markers
> 4. Test the merged code
> 5. Stage and commit:
>    ```bash
>    git add resolved_file.py
>    git commit -m "Resolve merge conflict"
>    ```
>
> **Prevention:** Pull frequently, communicate with teammates, keep changes focused.

---

## Intermediate Level

**Q6: Explain the difference between `git merge` and `git rebase`.**

> **Answer:** Both integrate changes from one branch to another, but differently.
>
> **Merge:**
> - Creates a merge commit combining both branches
> - Preserves complete history and branch structure
> - Non-destructive—existing commits unchanged
> ```bash
> git checkout main
> git merge feature-branch
> ```
>
> **Rebase:**
> - Moves commits to the tip of another branch
> - Creates linear history (no merge commits)
> - Rewrites commit history
> ```bash
> git checkout feature-branch
> git rebase main
> ```
>
> **When to use:**
> - **Merge:** For shared branches, preserving history, team collaboration
> - **Rebase:** For local branches, cleaning up before PR, linear history preference
>
> **Golden rule:** Never rebase commits that have been pushed to shared branches.

---

**Q7: What is `git stash` and when would you use it?**

> **Answer:** Git stash temporarily saves uncommitted changes so you can switch contexts without committing incomplete work.
>
> **Use cases:**
> - Need to switch branches urgently
> - Pull latest changes without committing
> - Test something on clean working directory
>
> **Commands:**
> ```bash
> # Save changes
> git stash
> git stash save "Work in progress on login"
> 
> # List stashes
> git stash list
> 
> # Apply and remove from stash
> git stash pop
> 
> # Apply but keep in stash
> git stash apply
> 
> # Apply specific stash
> git stash apply stash@{2}
> 
> # Drop stash
> git stash drop stash@{0}
> 
> # Clear all stashes
> git stash clear
> ```

---

**Q8: How do you undo changes in Git (reset, revert, checkout)?**

> **Answer:** Different commands for different scenarios:
>
> **Discard working directory changes:**
> ```bash
> git checkout -- filename    # Old way
> git restore filename        # Modern way
> ```
>
> **Unstage files:**
> ```bash
> git reset HEAD filename     # Old way
> git restore --staged filename  # Modern way
> ```
>
> **Undo commits (local only):**
> ```bash
> git reset --soft HEAD~1   # Keep changes staged
> git reset --mixed HEAD~1  # Keep changes unstaged (default)
> git reset --hard HEAD~1   # Discard changes completely
> ```
>
> **Undo commits (already pushed):**
> ```bash
> git revert <commit-hash>  # Creates new commit undoing changes
> git push
> ```
>
> **Key difference:** `reset` rewrites history (dangerous for shared branches), `revert` adds new commits (safe for shared branches).

---

**Q9: What is a pull request and describe its workflow?**

> **Answer:** A pull request (PR) is a request to merge changes from one branch into another, typically accompanied by code review.
>
> **Workflow:**
> 1. Create feature branch from main
> 2. Make changes and commit
> 3. Push branch to remote
> 4. Open PR on GitHub/GitLab
> 5. Team reviews code, leaves comments
> 6. Address feedback, push updates
> 7. Get approvals
> 8. Merge PR
> 9. Delete feature branch
>
> **Good PR practices:**
> - Small, focused changes
> - Clear title and description
> - Link to issue/ticket
> - Include test evidence
> - Request specific reviewers
>
> **Why PRs matter:** Code quality, knowledge sharing, documentation, accountability.

---

**Q10: Explain `git cherry-pick` and its use cases.**

> **Answer:** Cherry-pick applies a specific commit from one branch to another without merging the entire branch.
>
> ```bash
> git cherry-pick <commit-hash>
> git cherry-pick abc123 def456  # Multiple commits
> ```
>
> **Use cases:**
> - Backporting a bug fix to older release branches
> - Picking specific features without merging everything
> - Recovering commits from abandoned branches
>
> **Example:**
> ```bash
> # Bug fixed in develop, need it in release-1.0
> git checkout release-1.0
> git cherry-pick abc123
> git push
> ```
>
> **Caution:** Creates duplicate commits with different hashes; can complicate history if overused.

---

## Advanced Level

**Q11: How would you squash commits before merging?**

> **Answer:** Squashing combines multiple commits into a single, clean commit.
>
> **Method 1: Interactive Rebase**
> ```bash
> git rebase -i HEAD~4  # Last 4 commits
> 
> # In editor, change 'pick' to 'squash' or 's'
> pick abc123 First commit
> squash def456 Second commit
> squash ghi789 Third commit
> squash jkl012 Fourth commit
> 
> # Save, then edit combined commit message
> ```
>
> **Method 2: Merge with squash**
> ```bash
> git checkout main
> git merge --squash feature-branch
> git commit -m "Add complete feature"
> ```
>
> **Benefits:** Clean history, easier to revert, meaningful commit messages.

---

**Q12: Explain Git hooks and give examples.**

> **Answer:** Git hooks are scripts that run automatically at specific points in the Git workflow.
>
> **Common hooks:**
> - `pre-commit`: Run linting, tests before commit
> - `commit-msg`: Enforce commit message format
> - `pre-push`: Run tests before pushing
> - `post-merge`: Install dependencies after merge
>
> **Example pre-commit hook:**
> ```bash
> # .git/hooks/pre-commit
> #!/bin/sh
> 
> # Run linter
> npm run lint
> if [ $? -ne 0 ]; then
>     echo "Linting failed. Fix errors before committing."
>     exit 1
> fi
> 
> # Run tests
> npm test
> if [ $? -ne 0 ]; then
>     echo "Tests failed. Fix tests before committing."
>     exit 1
> fi
> ```
>
> **Tools:** Husky (npm), pre-commit (Python) manage hooks easily.
