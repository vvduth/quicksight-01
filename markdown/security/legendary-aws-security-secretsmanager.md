<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Secure Secrets with Secrets Manager

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-security-secretsmanager)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-secretsmanager_r7s8t9u0" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Introducing Today's Project!

Today, I’m on a mission to expose how a web app might be sneakily (and insecurely) storing credentials.

🔎 First, I’ll reveal those secret-stashing mistakes that could lead to big trouble!
🤖 Then, we’ll see GitHub’s awesome secret scanning in action—catching sensitive info before it ever goes public.
🔐 Next, I’ll level up security by migrating the app’s credentials to AWS Secrets Manager (because storing secrets in code is sooo last year).
✅ Finally, I’ll prove that our newly-secured code is safe to share—no secrets leaked!

All this, while learning how to keep credentials super safe and sleep better at night. 

### Tools and concepts

Services I used were:

AWS Secrets Manager 🏦 (for storing credentials securely)
AWS IAM 👥 (to manage access keys and permissions)
GitHub 🐙 (for sharing and versioning code)
Key concepts I learnt include:

Hardcoding vs. secure storage 🔐—why secrets never belong in code
Secret rotation 🔄—keeping credentials fresh and reducing risk
Secret scanning 🕵️‍♂️—how GitHub catches leaked secrets before they go public
Git workflows (fork, clone, rebase, resolve conflicts) 🚦
Integrating AWS services with Python using boto3 🐍
All together? I leveled up my security skills, learned how to keep secrets safe, and became a better cloud developer! 🚀

### Project reflection

This project took me approximately two hours to complete. ⏰ The most challenging part was cleaning up the commit history and safely removing hardcoded secrets—Git rebase is powerful, but it can get tricky! 😅🌀

It was most rewarding to see my app securely fetching credentials from AWS Secrets Manager and knowing my code was safe to share on GitHub. That feeling of leveling up my cloud security skills? Totally worth it! 🚀🔐

I chose to do this project today because I wanted to challenge myself, level up my cloud security skills, and finally stop procrastinating! 😅⏳ Plus, there’s something extra motivating about tackling new tech on a Monday—starting the week with a win! 🏆✨

It felt like the perfect time to blend AWS, Python, and GitHub into one awesome learning adventure. By the end, I knew I’d have a secure, professional app and a bunch of new tricks up my sleeve. Let’s go! 🚀

---

## Hardcoding credentials

In this project, a sample web app is exposing  AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY (they were directly written in config.py) It is unsafe to harcode credentials because  If this were a real, public application, and you shared the code on a GitHub repository, anyone could see these credentials! 😱 
Exposing your AWS credentials publicly is a major security risk and is extremely unsafe for production applications. Once someone else gets access to your credentials, they can use them to access your AWS account, delete resources, steal data, and cause damage.
It's not just AWS credentials; database passwords, API keys for other services, and any kind of secret should never be directly embedded in your code.

In my `config.py`, I put:  

AWS_ACCESS_KEY_ID = "**************"
AWS_SECRET_ACCESS_KEY = "***********************************"
AWS_REGION = "us-east-2"

🎭 These are *fake* AWS credentials—like movie props, not the real deal! I used them to show what *not* to do: hardcoding secrets in your code. If these were real, anyone could party in your AWS account (yikes!).  

Don’t worry, these won’t actually connect to AWS or reveal your S3 buckets. They’re just here for learning and demo purposes. Security first, fun always! 🔐😄

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-secretsmanager_j2k3l4m5" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Using my own AWS credentials

Let’s talk packages! 📦✨  
As an extension for this project,I made sure Python was ready to roll (already installed, so high five! 🙌). Then, I set up a virtual environment to keep things tidy and drama-free.  

Next up, I installed:  
- **boto3** 🐍 — AWS SDK for Python, our magic wand for talking to AWS services.  
- **fastapi** ⚡ — The speedy framework for building web APIs, because slow is not our style!  
- **uvicorn** 🚀 — The super-fast ASGI server to run our FastAPI app.  
- **python-multipart** 📑 — Handles file uploads in FastAPI like a champ.  

Why? So our web app can securely interact with AWS, run fast, and handle uploads—all in a clean, isolated environment. Now let’s code like pros! 💻

Uh oh! 🚨 When I clicked 'View My S3 Buckets', the app threw an error saying the AWS Access Key ID is not valid. Translation: the app tried to peek at AWS—but the credentials are just movie props, not the real deal! 🎬

The app runs fine, but it can’t access your actual AWS account (no bucket party here 🪣🎉). Why? Because our config.py is loaded with placeholder keys, not real secrets.

This error is a friendly reminder: hardcoding fake credentials keeps your account safe, but also means the app can’t connect to AWS for real.

To resolve the 'InvalidAccessKeyId' error, I updated those fake credentials in config.py for my real AWS goodies! 🛠️
I logged into my AWS account, headed to IAM, picked my admin user, and created a shiny new access key (yup, downloaded the .csv ).

Now, config.py contains:

AWS_ACCESS_KEY_ID (the real deal!)
AWS_SECRET_ACCESS_KEY (super secret!)
AWS_REGION (where my AWS stuff lives)
But remember: hardcoding real secrets is risky business! 🔥 This is just to get things working—next step is to secure those credentials like a security ninja! 🥷🔐

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-secretsmanager_wghjteykut" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Pushing Insecure Code to GitHub

Once I updated the web app code with credentials, I forked the repository because I wanted my own copy on GitHub—where I can push changes, open pull requests, and break things without messing up the original! 🍴😎

A fork is different from a clone:

Fork: Makes a new repo under my account on GitHub, perfect for sharing edits, collaborating, and making my mark in the cloud. ☁️🛠️
Clone: Just downloads the repo to my computer—no sharing, no glory, no easy way to contribute back.
Forking lets me experiment, push code, and get feedback, all while keeping the original repo safe and sound. 🚀

To connect my local repository to the forked repository, I run these commands below:

git remote remove origin: Cuts ties with the original repo—so my local isn’t talking to the wrong remote anymore. ✂️
git remote add origin <my-forked-repo-url>: Sets up a new remote pointing to my forked repo—now my changes will go to my own GitHub space! 🚀
git add .: Stages all modified files, getting them ready for a commit. 📦
git commit -m "Message": Saves a snapshot of my work with a handy message, like “hardcoded AWS keys (oops).” 📸
git push origin main: Launches my local commits into the cloud—up to my forked repo! 🌥️

GitHub blocked my push because its secret scanning superpower 🦸‍♂️ automatically hunts for sensitive info—like AWS credentials—in your code. When it spotted the Access Key ID and Secret Access Key chilling in config.py, it said, “Nope!” 🚫

This is a GOOD security feature because it protects you (and everyone) from accidentally leaking secrets to the world. Imagine your AWS account getting hijacked just because of a careless push—no thanks! 🕵️‍♂️🔒

GitHub’s blockade keeps our code public, but our secrets private. Safety first, drama never! 👏

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-secretsmanager_o2p3q4r5" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Secrets Manager

AWS Secrets Manager is like a super-secure digital vault for your secrets—think database passwords, API keys, and more! 🏦✨ Instead of hardcoding sensitive info in your code (a big no-no!), Secrets Manager encrypts and stores it, letting your app fetch secrets safely and on demand. 🔐

It also offers cool features like automatic secret rotation (so your keys stay fresh), access auditing, and fine-grained control over who can see what.

In our project, I’m using Secrets Manager to store my AWS access key and secret key for the web app—keeping them locked up tight and far away from prying eyes. 👀 Other common uses: storing DB credentials, OAuth tokens, or any sensitive config data!

Secret rotation is like giving your secrets a makeover on a regular schedule! 🔄💅 With AWS Secrets Manager, secret rotation means your passwords, API keys, or other sensitive info get automatically changed—so even if a bad actor grabs an old key, it’ll be useless soon.

This is a security best practice because it shrinks the window for potential leaks or hacks. Secrets Manager can rotate database passwords and other service credentials out of the box, plus you can set up custom rotation for anything else.

It’s super useful for apps that need to stay secure without downtime—banks, e-commerce, or anything with sensitive data. Rotate those secrets and keep the hackers guessing! 🕵️‍♀️🔐

Secrets Manager makes developers' lives easier by providing sample code in languages like Python, Java, JavaScript, and more! 🐍☕️💻 These code snippets show exactly how to securely fetch secrets from the vault, so you can integrate Secrets Manager into your app without reinventing the wheel.

This is super helpful because it saves time, reduces mistakes, and means you don’t have to be a security wizard to keep your secrets safe. Just copy, paste, and boom—your app talks to Secrets Manager securely! 💥🔐

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-secretsmanager_h2i3j4k5" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Updating the web app code

I gave config.py a major upgrade! ⚡️
Now, it imports boto3 (our AWS magic wand 🪄) to interact with Secrets Manager. I also added ClientError from botocore to gracefully handle any secret-retrieval hiccups (no panicking allowed!).

The new superstar is the get_secret() function, which securely fetches our AWS credentials straight from Secrets Manager—no more hardcoded secrets lurking in the code! 🔐✨

Why is this awesome? Because now, our app talks to AWS securely, credentials stay locked up, and our code is cleaner and safer. Security with style—mission accomplished! 😎👏

The new code in config.py now uses AWS Secrets Manager to securely grab and extract secrets! 🗝️🚀
After retrieving the secret, it pulls out:

Python
AWS_ACCESS_KEY_ID = credentials['AWS_ACCESS_KEY_ID']
AWS_SECRET_ACCESS_KEY = credentials['AWS_SECRET_ACCESS_KEY']
AWS_REGION = "eu-north-1"
🔓 This means our app fetches AWS credentials directly from a secure vault—no more risky hardcoded values lurking in the code!

This is important because it keeps sensitive credentials out of your codebase, prevents accidental leaks, and makes it super easy to rotate or update secrets without touching your code. Security, flexibility, and peace of mind, all in one vault! 🦸‍♂️🔐

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-secretsmanager_v0w1x2y3" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Rebasing the repository

Git rebasing is like rewriting your repo’s story—changing or rearranging commits to make history cleaner and safer! 🕰️✨ I used it here because simply editing config.py isn’t enough for top-notch security 🤦‍♀️.

The problem? Those hardcoded AWS credentials are still lurking in the commit history, so sneaky folks could dig them up later! 🕵️‍♂️

Rebasing (and history rewriting) lets us totally remove those old commits with secrets, erasing any trace from the timeline. It’s like a time machine for your code—protecting your secrets and making your repo safe to share! 🚀🔐

A merge conflict popped up during rebasing because Git tried to combine changes from different branches (or commits) that both changed the same part of a file—usually config.py in our case. 🤯🔀 It’s like two chefs fighting over the same pot!

I resolved the conflict by reviewing the file, choosing which code to keep (or mixing the best bits), and editing out the conflict markers (<<<<<<<, =======, >>>>>>>). Once I was happy with the final version, I staged the file and continued the rebase. 🛠️

Why did it happen? Because Git can’t read minds! When overlapping changes clash, it needs a human (me!) to make the call. After resolving, everything merged smoothly and securely. 🎉

Once the merge conflict was resolved, I verified my hardcoded credentials were out of sight by checking my repo’s commit history and file contents on GitHub. 👀🔍 No trace of those old secrets in any commit, no AWS keys lurking in config.py—just squeaky clean code!

I also tried searching the repo for the old credentials to make sure they were totally gone—nothing popped up! This double-check gives me peace of mind that my secrets are safe, and the repo is ready for public sharing. 🎉🔐

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-secretsmanager_t5u6v7w8" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

---
