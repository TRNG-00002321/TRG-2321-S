# Git Commands Cheat Sheet

## Basic Commands

| Command | Description |
|---------|-------------|
| `git init` | Initialize new repository |
| `git clone <url>` | Clone remote repository |
| `git status` | Check working directory status |
| `git add <file>` | Stage specific file |
| `git add .` | Stage all changes |
| `git commit -m "message"` | Commit staged changes |
| `git push` | Push commits to remote |
| `git pull` | Fetch and merge from remote |
| `git fetch` | Fetch without merging |

## Branching

| Command | Description |
|---------|-------------|
| `git branch` | List local branches |
| `git branch -a` | List all branches (including remote) |
| `git branch <name>` | Create new branch |
| `git checkout <branch>` | Switch to branch |
| `git checkout -b <name>` | Create and switch to new branch |
| `git merge <branch>` | Merge branch into current |
| `git branch -d <name>` | Delete branch (safe) |
| `git branch -D <name>` | Delete branch (force) |

## History & Differences

| Command | Description |
|---------|-------------|
| `git log` | View commit history |
| `git log --oneline` | Compact history view |
| `git log -n 5` | Last 5 commits |
| `git diff` | Show unstaged changes |
| `git diff --staged` | Show staged changes |
| `git diff <branch1> <branch2>` | Compare branches |
| `git show <commit>` | Show commit details |

## Undoing Changes

| Command | Description |
|---------|-------------|
| `git restore <file>` | Discard working directory changes |
| `git restore --staged <file>` | Unstage file |
| `git reset HEAD~1` | Undo last commit (keep changes) |
| `git reset --hard HEAD~1` | Undo last commit (discard changes) |
| `git revert <commit>` | Create new commit undoing changes |
| `git stash` | Temporarily save changes |
| `git stash pop` | Restore stashed changes |

## Remote Operations

| Command | Description |
|---------|-------------|
| `git remote -v` | List remotes |
| `git remote add origin <url>` | Add remote |
| `git push -u origin <branch>` | Push and set upstream |
| `git push origin --delete <branch>` | Delete remote branch |
| `git pull --rebase` | Pull with rebase |

## Configuration

| Command | Description |
|---------|-------------|
| `git config --global user.name "Name"` | Set username |
| `git config --global user.email "email"` | Set email |
| `git config --list` | Show all config |

## Common Workflows

### Feature Branch Workflow
```bash
git checkout main
git pull
git checkout -b feature/new-feature
# make changes
git add .
git commit -m "Add new feature"
git push -u origin feature/new-feature
# create pull request on GitHub
```

### Resolving Merge Conflicts
```bash
git merge feature-branch
# conflicts appear
# edit files to resolve
git add <resolved-files>
git commit -m "Resolve merge conflicts"
```

### Undoing a Pushed Commit
```bash
git revert <commit-hash>
git push
```
