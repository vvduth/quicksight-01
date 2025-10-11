<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Encrypt Data with AWS KMS

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-security-kms)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-kms_w0x1y2z3)

---

## Introducing Today's Project!

In this project, I will demonstrate how encryption works in AWS by using AWS Key Management Service (KMS). I’ll create encryption keys with AWS KMS, encrypt a DynamoDB database using those keys, and then add and retrieve data to test the encryption. I’ll also observe how AWS prevents unauthorized access to encrypted data and give a specific user encryption access. The goal is to show the importance of protecting data from unauthorized access while ensuring that authorized users can securely view and use it.

### Tools and concepts

Services I used include AWS KMS for key management and Amazon DynamoDB for storing encrypted data. Key concepts I learnt include encryption at rest, symmetric keys, KMS key policies, IAM user permissions, and how to control access to sensitive data while allowing authorized users to decrypt and use it safely.

### Project reflection

This project took me approximately 2 hours. The most challenging part was setting up KMS key permissions correctly so that only authorized users could decrypt the data. It was most rewarding to see the test user successfully access the encrypted DynamoDB table after updating the key policy, confirming my encryption setup worked as intended.

I chose to do this project today because I wanted to learn how AWS handles encryption in practice, understand KMS key management, and see how to securely protect sensitive data while still allowing authorized access.

---

## Encryption and KMS

Encryption is the process of converting readable data (plain text) into an unreadable format (cipher text) to protect it from unauthorized access. Companies and developers do this to keep sensitive information—like customer data, passwords, or financial details—secure from hackers or data breaches. Encryption keys are what guide the algorithm on how to transform plain text into cipher text. For example, a key might instruct the algorithm to substitute or rearrange parts of the data to make it appear random and impossible to read without the correct key.

AWS KMS (Key Management Service) is a secure vault for creating, managing, and controlling encryption keys used to protect your data across AWS services. You can use KMS to encrypt data stored in services like DynamoDB, S3, or EBS, without handling the encryption process yourself.

Key management systems are important because they centralize control over who can access or use encryption keys, ensure compliance with security standards, and reduce the risk of data leaks. They make encryption easy to apply consistently while keeping sensitive information protected.

Encryption keys are broadly categorized as symmetric and asymmetric. I chose a symmetric key because it uses a single key for both encryption and decryption. This makes it faster and more efficient, especially for encrypting large volumes of data like in a DynamoDB table. It’s ideal for internal applications where AWS securely manages and controls access to the key.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-kms_a2b3c4d5)

---

## Encrypting Data

My encryption key will safeguard data in DynamoDB, which is  one of AWS's database services. DynamoDB stands out as a fast and flexible way to store your data, which makes it a great choice for applications that need quick access to large volumes of data e.g. games.

The different encryption options in DynamoDB include:

Owned by Amazon DynamoDB: Fully managed by AWS, with no visibility or control over the encryption key. Ideal for basic encryption needs.

AWS managed key: Managed by AWS KMS, offering some visibility into the key’s usage but limited control.

Customer managed key (CMK): Created and managed by you in AWS KMS. You have full control over key policies, rotation, and permissions. This provides the highest level of security and flexibility.

Their differences are based on who controls and manages the encryption key.
I selected “Stored in my account” (customer managed key) because it allows me to fully control access, monitor usage, and enforce my own security policies.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-kms_q8r9s0t1)

---

## Data Visibility

Rather than controlling who can see the data directly, KMS manages user permissions by controlling who can use the key to encrypt or decrypt data. Users must have explicit KMS permissions (like kms:Encrypt or kms:Decrypt) to work with the key. Even if a user has full access to a DynamoDB table, they cannot read or decrypt encrypted items without the proper KMS key permissions. This ensures that only authorized users can access sensitive data.

Despite encrypting my DynamoDB table, I could still see the table's items because DynamoDB uses transparent data encryption, which means the service automatically decrypts the data for authorized users. When I query or view items, DynamoDB securely communicates with KMS to decrypt the data in real time — as long as I have the right permissions. This allows me to work with my data normally while it remains encrypted at rest and protected from unauthorized access.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-kms_c0d1e2f3)

---

## Denying Access

I created a new IAM user named nextwork-kms-user and enabled console access so I can log in with this user's credentials. I attached the AmazonDynamoDBFullAccess policy to allow full DynamoDB operations but did not grant any KMS permissions, so this user can access the table structure and operations but cannot decrypt or view the encrypted data.

After accessing the DynamoDB table as the test user, I encountered a “You don’t have permission to kms:Decrypt” error. This happened because the IAM user (nextwork-kms-user) doesn’t have permission to use the KMS key that encrypts the table. This confirmed that encryption with a customer-managed KMS key effectively blocks unauthorized users from accessing sensitive data, even if they have full DynamoDB permissions.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-kms_w0x1y2z3)

---

## EXTRA: Granting Access

After updating my KMS key policy, I granted my test user (nextwork-kms-user) permissions to use the key. The updated policy allows both my admin account and the test user to perform actions like Encrypt, Decrypt, ReEncrypt*, GenerateDataKey*, and DescribeKey. This ensures the test user can now access and decrypt the DynamoDB table’s encrypted data, while still controlling exactly who has these permissions.

Using the test user, I logged into the AWS console and accessed the encrypted DynamoDB table. I retried viewing the table’s items and observed that the data was now visible and accessible. This confirmed that granting the test user permissions in the KMS key policy successfully allowed them to decrypt and interact with the table while maintaining security control.

Encryption secures data itself, protecting it even if someone bypasses security groups or permission policies. I could combine encryption with IAM policies, security groups, and VPC endpoints to ensure that only authorized users and systems can access and decrypt the data, adding multiple layers of protection.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-security-kms_feffb2fb8)

---

---
