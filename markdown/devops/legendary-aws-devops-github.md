<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Connect a GitHub Repo with AWS

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-devops-github)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

## Connect a GitHub Repo with AWS

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-github_dd9d254e)

---

## Introducing Today's Project!

In this project, I will demonstrate how to:

- 🐱 Set up Git and GitHub for version control.
- 🤝 Connect my web app project to a GitHub repository.
- 🪄 Make changes to my web app code and watch those updates automatically reflect in my GitHub repo.
- 💎 Set up a README file to describe my project in the repository.

I'm doing this project to learn how to use Git and GitHub to manage, share, and collaborate on code more efficiently. This experience will help me understand the workflow for tracking changes, working with others, and keeping my projects organized and documented.

### Key tools and concepts

Services I used were:
- **GitHub**: for version control, hosting my code, and collaboration.
- **AWS EC2 (Elastic Compute Cloud)**: for launching and managing a virtual server to host my web application.
- **SSH**: for securely connecting and managing my EC2 instance from my local machine.

Key concepts I learnt include:
- **Git workflow**: initializing a repo, staging, committing, pushing, and pulling changes.
- **Branching in Git**: using branches to manage different lines of development.
- **Remote repositories**: connecting a local repo to GitHub and syncing changes.
- **Authentication**: using personal access tokens instead of passwords for secure access.
- **README and Markdown**: documenting projects using Markdown for clear formatting.
- **Basic deployment**: moving code from local development to a cloud server.
- **CI/CD basics**: understanding how automation can streamline deployment and updates (even if not fully implemented).
- **Collaboration and backup**

### Project reflection

This project took me approximately  2 hours to complete. The most challenging part was setting up authentication with GitHub using a personal access token and making sure my EC2 instance could securely push code to my repository. It was most rewarding to see my web app successfully deployed and managed using Git, GitHub, and AWS tools, as well as to have clear documentation with a README file that makes the project easy to understand and share.

I did this project because I wanted to learn how to deploy a Java web application using modern tools like Git, GitHub, and AWS EC2. It was an opportunity to improve my skills in cloud computing, version control, and continuous integration/deployment. Working on this project helped me understand how to manage code, collaborate effectively, and automate deployment processes—all essential skills for real-world software development.

This project is part two of a series of DevOps projects where I'm building a CI/CD pipeline! I'll be working on the next project the next day..

---

## Git and GitHub

answer:  
Git is a distributed version control system that helps you track changes in your code, collaborate with others, and manage different versions of your project efficiently. It’s widely used by developers to maintain project history and support teamwork.

I installed Git on my EC2 instance using the following commands:

```sh
sudo dnf update -y
sudo dnf install git -y
```

These commands update the package manager and then install Git, making it ready to use for version control.

GitHub is an online platform for hosting, sharing, and collaborating on code using Git. It provides tools for version control, issue tracking, code reviews, and project management, making it easier for teams and individuals to work together on software projects.

I'm using GitHub in this project to store my web app’s code online, track changes, and collaborate more effectively. By pushing my project to GitHub, I can back up my work, share it with others, and take advantage of features like pull requests and README files to keep my project organized and accessible.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-github_efaadbf7)

---

## My local repository

A Git repository is a storage space where your project's code, files, and all changes to them are tracked using Git. It keeps a complete history of edits, additions, and deletions, making it easy to collaborate with others, revert to previous versions, and manage your project's development over time. A repository can be local (on your computer) or hosted remotely (like on GitHub), and is essential for version control and teamwork in software development.

Git init is a command that initializes a new Git repository in your project folder. It sets up the necessary files and structure for Git to start tracking changes in your code. When you run `git init` in your webapp directory on your EC2 instance, it creates a hidden `.git` folder there, allowing you to use Git commands to manage your project’s history from that point forward.

A branch in Git is a separate line of development within a repository. It allows you to work on new features, bug fixes, or experiments independently from the main codebase. Changes made in one branch don’t affect others until you merge them, making collaboration and parallel development much easier.

After running `git init`, the terminal showed a message about the **default branch name**. By default, Git creates a branch called `master` for new repositories, but this can be changed to names like `main`, `trunk`, or `development`. You can configure your preferred default branch name using:

```sh
git config --global init.defaultBranch <name>
```

Or rename the current branch with:

```sh
git branch -m <name>
```

This helps organize your project and makes it clear which branch is the main line of development.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-github_7bf21bae)

---

## To push local changes to GitHub, I ran three commands

### git add

The first command I ran was  
```sh
git remote add origin my-remote-github-repo-url
```
This command tells Git to add a remote repository (usually on GitHub) and name it “origin.” This sets up a link between your local project and your online GitHub repo, so you can push and pull code between them.

A **staging area** in Git is a place where you can group changes you want to commit. When you edit files, they are not automatically included in the next commit. You use commands like `git add` to add your changes to the staging area. Only the changes in the staging area will be saved when you run `git commit`. This gives you control over exactly what goes into each commit.

### git commit

answer:  
Using `-m` with the `git commit` command means you are providing a commit message directly in the command line. The commit message describes the changes you made and helps others (and yourself) understand the purpose of that commit. For example:

```sh
git commit -m "Updated index.jsp with new content"
```

This saves your staged changes to the repository with the message "Updated index.jsp with new content."

### git push

Using `-u` with the `git push` command means you are setting the upstream (tracking) branch for your local branch. When you run:

```sh
git push -u origin master
```

it not only pushes your local `master` branch to the `origin` remote on GitHub, but it also tells Git to remember this remote branch as the default upstream branch for future pushes and pulls. This way, you can simply use `git push` or `git pull` without specifying the branch name every time, making future interactions easier.

---

## Authentication

When I commit changes to GitHub, Git asks for my credentials because it needs to verify my identity before allowing me to push code to the remote repository. This authentication ensures that only authorized users can make changes to the project. If I haven’t set up SSH keys or a credential manager, Git will prompt for my GitHub username and password (or a personal access token) each time I push. This is a security measure to protect the code and repository from unauthorized access.

### Local Git identity

Git needs my name and email because every commit I make is recorded with this information as the author. This helps identify who made each change in the project’s history, making collaboration and tracking contributions easier. It also allows others to contact the author if they have questions or need clarification about a specific change.

Running git log showed me that  history of commits, which also mentions the commit author's name.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-github_9a27ee3b)

---

## GitHub tokens

GitHub authentication failed when I entered my password because GitHub no longer supports password authentication through the terminal. Instead, GitHub requires you to use a personal access token when pushing code from the command line. This change improves security and protects your account from unauthorized access. To authenticate successfully, you need to generate a token from your GitHub account settings and use it in place of your password.

A GitHub token is a secure, randomly generated string (called a Personal Access Token or PAT) that acts as a password for authenticating your identity when using GitHub from the command line or scripts. It provides access to your GitHub account and its repositories in a more secure way than traditional passwords.

I'm using one in this project because GitHub no longer accepts passwords for command-line operations like `git push` or `git pull`. Instead, it requires a token to authenticate actions and protect my account. Using a token allows me to safely push code from my EC2 instance to my GitHub repository.

answer:  
I could set up a GitHub token by going to my GitHub account settings, navigating to "Developer settings" > "Personal access tokens," and clicking "Generate new token." I selected the required scopes (permissions) for my project, gave the token a name, and then clicked "Generate token." I copied the generated token and used it as my password when pushing or pulling code from my local repository to GitHub via the terminal. This allows secure authentication and access to my GitHub repositories.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-github_fa11169d)

---

## Making changes again

I updated the file `src/index.jsp` in my `nextwork-web-project`. I added a new line to test how Git tracks changes. I couldn’t see the changes reflected in my GitHub repo initially because I hadn’t pushed the new change to the remote repository yet—the update was only present in my local repository. To see the changes on GitHub, I need to commit and push my updates.

I finally saw the changes in my GitHub repo after I committed the changes to my local repository and then pushed them to the remote GitHub repository using the `git push` command. This uploaded my updated files from my EC2 instance to GitHub, making the changes visible in my online repository.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-github_6becb2bc)

---

## Setting up a READMe file

answer:  
As a finishing touch to my GitHub repository, I added a README file, which is a special Markdown file (usually named `README.md`) that provides important information about the project. It typically explains what the project does, how to install or use it, and any other relevant details to help users and collaborators understand the repository.

I added a README file by creating a new file called `README.md` in my project folder, writing a brief description and instructions, and then committing and pushing it to my GitHub repository. This makes my project more professional and user-friendly for anyone who visits the repo.

My README is written in Markdown because Markdown is a simple markup language that makes it easy to format text for documentation. It supports special characters that help you organize and style your README, such as:

- `#` for headings  
- `*` or `-` for bullet points  
- `**bold**` or `_italic_` for emphasis  
- `` `code` `` for inline code  
- ``` ``` for code blocks  
- `[link text](URL)` for hyperlinks  
- `> ` for blockquotes

Using Markdown allows my README file to look clean, professional, and easy to read on GitHub.

My README file has 6 sections that outline the structure and purpose of my project:

1. **Welcome** – Introduces the project: Java Web App Deployment with AWS CI/CD.
2. **Table of Contents** – Provides a quick overview of the README sections.
3. **Introduction** – Explains the goal of combining Java web app development with AWS CI/CD tools.
4. **Technologies** – Lists the main technologies used in the project (such as Java, AWS, GitHub, EC2, etc.).
5. **Setup** – Details the steps required to get the project running, including installation and configuration instructions.
6. **Contact** – Shares information on how to reach out for questions or collaboration.
7. **Conclusion** – Summarizes the project and encourages feedback or contributions.

This structure makes it easy for anyone to understand the project, set it up, and get involved.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-github_c94976902)

---

---
