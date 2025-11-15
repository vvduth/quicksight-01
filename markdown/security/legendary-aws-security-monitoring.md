<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Build a Security Monitoring System

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-security-monitoring)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-monitoring_reghtjy" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Introducing Today's Project!

 In this project, I’ll demonstrate how to set up a monitoring system using AWS CloudTrail, CloudWatch, and SNS. My goal is to learn how to protect sensitive data in AWS and track who’s accessing the most confidential resources—like API keys, database credentials, and other critical secrets. This is important because every access to sensitive information poses a potential security risk, so I want to build a system that alerts me to unusual activity and helps maintain strong data security.

### Tools and concepts

Services I used were AWS CloudTrail, AWS CloudWatch, AWS Secrets Manager, and Amazon SNS.
Key concepts I learnt include how CloudTrail records management and data events, how to set up CloudWatch metrics and alarms to monitor specific activity, how SNS topics deliver notifications, and the difference between real-time event alerts and threshold-based monitoring. I also gained practical experience in troubleshooting cloud monitoring setups, understanding the importance of metric statistics, and making architectural decisions for secure and effective alerting.

### Project reflection

This project took me approximately 90 minutes to complete. The most challenging part was troubleshooting the notification issues and making sure the CloudWatch alarm and SNS were configured correctly to send alerts. It was most rewarding to see the whole monitoring system working end-to-end—getting an email notification as soon as my secret was accessed proved that my setup was secure and effective.

---

## Create a Secret

Secrets Manager is an AWS service designed to securely store, manage, and retrieve sensitive information like API keys, database passwords, and other secrets. You can use Secrets Manager to centralize secret management, automatically rotate credentials, control access through fine-grained permissions, and reduce the risk of hardcoding secrets in your code or configuration files.

For this project, I created a secret in AWS Secrets Manager called TopSecretInfo. It includes a single key/value pair: the key is 'The Secret is' and the value is 'I need 3 coffees a day to function.' This gives me a simple, memorable example to monitor and helps me practice tracking access to sensitive information in AWS.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-monitoring_o5p6q7r8" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Set Up CloudTrail

CloudTrail is a monitoring service that acts like an activity recorder for your AWS account, documenting every action—who did what, when it happened, and where it was done from. A trail in CloudTrail specifies exactly what activities to record and where to store those logs. For this project, I set up a trail to track all access to my secret and save those activity records in an S3 bucket for easy review and auditing.

CloudTrail events include several types, each giving you a different perspective on AWS activity. Management events capture admin actions like creating resources, updating configurations, or accessing secrets—these are our main focus since secret access is logged here. Data events track high-volume operations on resources, such as uploading files to S3 or invoking Lambda functions. Insight events highlight unusual or suspicious management activity, like a sudden spike in IAM user creation. Network activity events log changes to network configurations and traffic in your VPC, helping you spot potential security issues.

### Read vs Write Activity

Read API activity happens when someone views resources without making any changes—like listing S3 buckets, describing EC2 instances, or viewing metadata about a secret. Write API activity occurs when changes are made, such as creating, deleting, or modifying resources, and also when retrieving the actual value of a secret. For this project, it's important to monitor both types, so we need to enable tracking for both Read and Write API activities to get a complete picture of who’s accessing or changing sensitive information.

---

## Verifying CloudTrail

I retrieved my secret using two methods. First, I accessed the AWS Console, navigated to Secrets Manager, and clicked on "Retrieve secret value" to view it directly. Second, I used the Cloud Shell and ran the command: aws secretsmanager get-secret-value --secret-id "TopSecretInfo" --region your-region-code. This let me confirm that both the web interface and the CLI can access and expose secret values.

To analyze my CloudTrail events, I checked the Event history section, which shows all management events from the past 90 days. I noticed multiple "GetSecretValue" events, indicating that my secret’s value was retrieved or accessed several times. This confirms that CloudTrail is effectively logging every attempt to use or expose my sensitive information—which is exactly what I want for proper security monitoring.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-monitoring_s8t9u0v1" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## CloudWatch Metrics

Amazon CloudWatch Logs is a service that helps you bring together your logs from different AWS services, including CloudTrail, for visibility, troubleshooting, and analysis. It's important for monitoring because It's especially powerful because once your logs are in CloudWatch, you can create alerts based on specific patterns (such as someone accessing your secret), visualize trends, or trigger automated responses.

The key difference is that CloudTrail’s Event History is great for quickly checking recent account activity, but it only keeps logs for 90 days. CloudWatch Logs, on the other hand, can store logs indefinitely and let us set up alerts and automated responses whenever specific events occur. Plus, CloudWatch offers powerful filtering tools so we can zero in on the exact events we care about—which is essential for proactive monitoring and security.

A CloudWatch metric is a measurement that tracks specific activity or conditions. The metric value represents what gets recorded whenever our filter detects a match in the logs—by setting it to 1, we ensure each secret access increases the counter by one. The default value is used when no matches are found in a given time period; we set it to 0 so that periods without secret access are clearly shown as zero on our charts. This way, we get a complete picture of both when access occurs and when it doesn’t.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-monitoring_a9b0c1d2" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## CloudWatch Alarm

A CloudWatch alarm monitors a metric and triggers an alert when the metric crosses a specified threshold. For my setup, I configured the alarm with a static threshold type: it triggers whenever the SecretIsAccessed metric is greater than or equal to 1. This means any time someone accesses the secret—even once—the alarm will activate and send a notification, helping me respond quickly to potential security events.

 An SNS topic is a messaging resource in AWS Simple Notification Service that lets you send notifications to multiple subscribers at once. I created an SNS topic for my project so that when the CloudWatch alarm goes off, it automatically sends a message to this topic. This way, I get instant alerts—like an email—whenever my secret is accessed or the alarm is triggered.

AWS requires email confirmation for SNS subscriptions to make sure that only the intended recipient receives notifications. This helps prevent unwanted emails, accidental subscriptions, or misuse of your contact information. By confirming, you verify that you actually want to get alerts, which adds an extra layer of security and trust to the notification process.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-monitoring_fsdghstt" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Troubleshooting Notification Errors

 To test my monitoring system, I tried retrieving the secret value multiple times using CloudShell and the AWS Console. However, I didn’t receive any email notifications as expected. This shows that there’s an issue with my alerting setup, so I’ll need to troubleshoot the configuration to figure out why the CloudWatch alarm isn’t sending notifications.

When troubleshooting the notification error, I checked five key areas:

1. Verified if CloudTrail was recording the GetSecretValue event—sometimes, the event might not show up due to configuration or permission issues.
2. Confirmed that CloudTrail was actually sending logs to CloudWatch—misconfigured log destinations can break the monitoring chain.
3. Checked if CloudWatch’s metric filter was correctly identifying secret access events in the logs—incorrect filter patterns can miss important activity.
4. Made sure the CloudWatch Alarm was triggering the intended action when the metric threshold was met—faulty alarm settings can block notifications.
5. Ensured that SNS was properly delivering emails—issues like missing email confirmations or misconfigured topics can prevent alerts from reaching my inbox.

By systematically investigating each step, I was able to narrow down where the problem was happening and fix my monitoring setup.

 I didn’t receive an email notification before because my CloudWatch alarm was set to use the Average statistic instead of Sum. This is crucial because Sum counts the total number of secret access events in the evaluation period—exactly what we want for triggering alerts. If the alarm uses Average, it only looks at the average rate of events, which might never reach the set threshold and therefore won’t trigger the alarm. Changing the alarm’s statistic to Sum solved the problem and ensured I get notified whenever my secret is accessed.

---

## Success!

To validate that my monitoring system works, I triggered a secret access event and then checked both my CloudWatch Alarm status and my email inbox for notifications. I confirmed that the CloudWatch Alarm was activated when the secret was accessed, and I received an email alert through SNS, proving that my system reliably detects and reports secret access events as intended.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-monitoring_ageraergearge" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Comparing CloudWatch with CloudTrail Notifications

After enabling direct CloudTrail SNS notifications, my inbox quickly filled up with several new emails from CloudTrail. These notifications arrived each time the secret was accessed. As for usefulness, I found the alerts helpful for immediate visibility, but they can get overwhelming or noisy if not filtered properly. It’s a good reminder to fine-tune alert settings so you only get notified about truly important events.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-monitoring_d7e8f9g0" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

---
