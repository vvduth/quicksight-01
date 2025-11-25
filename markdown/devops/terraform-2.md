# Launching EC2 with Terraform

---

## Table of Contents

1. [Overview](#overview)
2. [Project Structure](#project-structure)
3. [Provider Configuration](#provider-configuration)
4. [Creating SSH Key Pair](#creating-ssh-key-pair)
5. [Security Group Configuration](#security-group-configuration)
6. [EC2 Instance Configuration](#ec2-instance-configuration)
7. [Deploying the Infrastructure](#deploying-the-infrastructure)
8. [Managing Instance State](#managing-instance-state)
9. [Cleaning Up Resources](#cleaning-up-resources)
10. [Summary](#summary)

---

## Overview

Last time we learned how to get instance AMI ID using data sources and output it. We can see in the `terraform.tfstate` file that the AMI ID is stored there:

```json
"hypervisor": "xen",
"id": "ami-0c846debef94e83c2",
```

To launch an EC2 instance, we need to create additional Terraform configuration files. We also need a key pair and security group to launch an instance securely.

---

## Project Structure

For better organization, we'll create separate `.tf` files for different resources:

- `provider.tf` - AWS provider configuration (region, credentials, etc.)
- `keypair.tf` - SSH key pair resource
- `securitygroup.tf` - Security group and rules
- `instance.tf` - EC2 instance configuration

**Note:** You can put all configurations in one file, but separating them improves organization and maintainability.

---

## Provider Configuration

### provider.tf

To browse available providers, go to: https://registry.terraform.io/browse/providers

We'll use the AWS provider:

```hcl
provider "aws" {
  region = "eu-north-1"
}
```

This configures Terraform to use AWS in the `eu-north-1` region (Stockholm).

---

## Creating SSH Key Pair

### Generate SSH Key

Open your terminal and run:

```bash
ssh-keygen
```

You should see output like this:

```
Generating public/private ed25519 key pair.
Enter file in which to save the key (/c/Users/ducth/.ssh/id_ed25519): dove-key
Enter passphrase (empty for no passphrase): 
Enter same passphrase again: 
Your identification has been saved in dove-key
Your public key has been saved in dove-key.pub
The key fingerprint is:
SHA256:"......" ducth@DucPC
The key's randomart image is:
+--[ED25519 256]--+
|       . o+o     |
|      . o.o.     |
|     . + o..     |
|  o . + + =      |
|   = o .S+ .     |
| oo   *.+        |
| .+. + %..+      |
|..o.E B ** .     |
|.o  .+o==+.      |
+----[SHA256]-----+
```

This creates two files:
- `dove-key` (private key)
- `dove-key.pub` (public key)

### View and Copy Public Key

View the public key by running:

```bash
cat dove-key.pub
```

Copy the output and use it in the next step.

### keypair.tf

```hcl
resource "aws_key_pair" "dove-key" {
  key_name   = "dove-key"
  public_key = file("dove-key.pub")
}
```

This will create a key pair called `dove-key` in the AWS console.

---

## Security Group Configuration

### securitygroup.tf

```hcl
resource "aws_security_group" "dove-sg" {
  name        = "dove-sg"
  description = "Security group for dove instances"
  
  tags = {
    Name = "dove-sg"
  }
}

resource "aws_vpc_security_group_ingress_rule" "ssh_from_my_ip" {
  security_group_id = aws_security_group.dove-sg.id
  cidr_ipv4         = "your_ip/32"
  from_port         = 22
  to_port           = 22
  ip_protocol       = "tcp"
}

resource "aws_vpc_security_group_ingress_rule" "allow_http" {
  security_group_id = aws_security_group.dove-sg.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 80
  to_port           = 80
  ip_protocol       = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "allow_all_outbound_ipv4" {
  security_group_id = aws_security_group.dove-sg.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1" # -1 means all protocols
}

resource "aws_vpc_security_group_egress_rule" "allow_all_outbound_ipv6" {
  security_group_id = aws_security_group.dove-sg.id
  cidr_ipv6         = "::/0"
  ip_protocol       = "-1" # -1 means all protocols
}
```

### What This Security Group Does

- **SSH Access:** Allows SSH (port 22) from your IP only
- **HTTP Access:** Allows HTTP (port 80) from anywhere (0.0.0.0/0)
- **Outbound Traffic:** Allows all outbound traffic for both IPv4 and IPv6

**Security Note:** Replace `your_ip/32` with your actual IP address for SSH access.

---

## EC2 Instance Configuration

### instance.tf

```hcl
resource "aws_instance" "web" {
  ami                    = data.aws_ami.amiID.id
  instance_type          = "t3.micro"
  key_name               = aws_key_pair.dove-key.key_name
  vpc_security_group_ids = [aws_security_group.dove-sg.id]
  availability_zone      = "eu-north-1a"
  
  tags = {
    Name    = "dove-web-instance"
    Project = "dove-terraform"
  }
}
```

### Configuration Details

- **AMI:** Uses the Ubuntu AMI we retrieved from the data source
- **Instance Type:** t3.micro (free tier eligible)
- **Key Name:** References the key pair resource we created
  - Alternative: Use hardcoded key name if the key already exists in AWS
  - Make sure the key file is in the folder where Terraform is run
- **Security Group:** References the security group we created
- **Availability Zone:** Specifies eu-north-1a

---

## Deploying the Infrastructure

Run the following commands in order:

```bash
# Initialize Terraform and download providers
terraform init

# Format configuration files
terraform fmt

# Validate configuration syntax
terraform validate

# Preview changes before applying
terraform plan

# Apply the configuration
terraform apply
```

### Important Notes

- **Always read the plan output carefully** before running `terraform apply`
- Terraform will show you what resources will be created, modified, or destroyed
- Type `yes` when prompted to confirm the changes

### Verify Deployment

After running `terraform apply`, check the AWS console. You should see:
- The EC2 instance running
- The security group created
- The key pair registered

---

## Managing Instance State

### Terraform is Great But Also Terrifying

Terraform can create and destroy infrastructure with just a few commands, so always be careful with what you're applying.

### Adding Instance State Management

Add this to your `instance.tf`:

```hcl
resource "aws_ec2_instance_state" "web-state" {
  instance_id = aws_instance.web.id
  state       = "running"
}
```

### What This Does

This resource ensures the instance is always in the running state. If you manually shut down the instance from the AWS console, the next time you run `terraform apply`, it will automatically start the instance again.

This is useful for:
- Ensuring critical instances remain running
- Automatic recovery from manual shutdowns
- Maintaining desired infrastructure state

---

## Cleaning Up Resources

### Destroy Infrastructure

When you're done with the infrastructure, always remember to clean up to avoid unnecessary charges:

```bash
terraform destroy
```

### What Gets Destroyed

Running `terraform destroy` will remove:
- The EC2 instance
- The security group
- The key pair
- All associated resources created by Terraform

**Warning:** This action is irreversible. Make sure you have backups of any important data before destroying resources.

---

## Summary

### What We Learned

1. **Project Organization:** Separating Terraform configurations into multiple files for better maintainability
2. **Provider Configuration:** Setting up AWS provider with region specification
3. **SSH Key Pair:** Generating and managing SSH keys for EC2 access
4. **Security Groups:** Creating security groups with specific ingress and egress rules
5. **EC2 Instance:** Launching an EC2 instance with proper configuration
6. **Instance State Management:** Ensuring instances maintain desired state
7. **Resource Cleanup:** Properly destroying infrastructure to avoid costs

### Key Takeaways

- **Separation of Concerns:** Use multiple `.tf` files to organize resources logically
- **Security First:** Always restrict SSH access to your IP and use proper security groups
- **Plan Before Apply:** Always review `terraform plan` output before applying changes
- **State Management:** Terraform can enforce desired state automatically
- **Clean Up:** Always run `terraform destroy` when done to avoid unnecessary AWS charges

### Best Practices

- Keep sensitive data (like private keys) out of version control
- Use meaningful resource names and tags
- Always validate and format your Terraform code
- Review the execution plan carefully before applying
- Document your infrastructure decisions