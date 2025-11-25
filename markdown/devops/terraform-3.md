# Variables and Provisioners in Terraform

---

## Table of Contents

1. [Overview](#overview)
2. [Understanding Variables](#understanding-variables)
3. [Setting Up Variables](#setting-up-variables)
4. [Using Variables in Configuration](#using-variables-in-configuration)
5. [Working with Map Variables](#working-with-map-variables)
6. [Introduction to Provisioners](#introduction-to-provisioners)
7. [Implementing Provisioners](#implementing-provisioners)
8. [Output Values](#output-values)
9. [Remote State with Backend](#remote-state-with-backend)
10. [Summary](#summary)

---

## Overview

In this section, we're going to explore variables and provisioners in Terraform. You'll learn the Terraform way of consuming variables and automating instance configuration with provisioners.

### Why Use Variables?

Variables help us:
- Move critical or confidential data out of our scripts
- Handle values that change based on environment or project
- Reuse code across different environments and projects
- Avoid hardcoding values throughout the configuration

### What We'll Build

We'll create:
- `provider.tf` - AWS provider configuration using variables
- `vars.tf` - Variable definitions
- `instance.tf` - EC2 instance configuration accessing variables
- Provisioners to automate web server setup
- Output values to display instance information

We'll start with the project we created in the previous section for launching EC2 with Terraform.

---

## Understanding Variables

Variables in Terraform allow you to parameterize your infrastructure code. Instead of hardcoding values like region names or AMI IDs, you can define them as variables and reference them throughout your configuration.

### Benefits of Variables

1. **Security:** Keep sensitive data separate from code
2. **Flexibility:** Easy to change values for different environments
3. **Reusability:** Use the same code for multiple projects
4. **Maintainability:** Update values in one place rather than throughout your code

---

## Setting Up Variables

### Create vars.tf

Start by creating a file called `vars.tf` with the following variables:

```bash
variable "region" {
  description = "The AWS region to deploy resources in"
  type        = string
  default     = "eu-north-1"
}

variable "zone" {
  description = "The AWS availability zone to deploy the instance in"
  type        = string
  default     = "eu-north-1a"
}
```

### Variable Structure

Each variable consists of:
- **Name:** The identifier used to reference the variable
- **Description:** Explains the variable's purpose
- **Type:** Specifies the data type (string, number, bool, list, map, etc.)
- **Default:** Optional default value if none is provided

---

## Using Variables in Configuration

### Update provider.tf

Replace the hardcoded region with the variable:

**Before:**
```bash
provider "aws" {
  region = "eu-north-1"
}
```

**After:**
```bash
provider "aws" {
  region = var.region
}
```

### Update instance.tf

Replace hardcoded values with variables:

**Before:**
```bash
resource "aws_instance" "web" {
  ami               = data.aws_ami.amiID.id
  instance_type     = "t3.micro"
  availability_zone = "eu-north-1a"
  ...
}
```

**After:**
```bash
resource "aws_instance" "web" {
  ami               = data.aws_ami.amiID.id
  instance_type     = "t3.micro"
  availability_zone = var.zone
  ...
}
```

### Validate Changes

Run the following commands to validate your changes:

```bash
terraform init
terraform fmt
terraform validate
```

If everything is configured correctly, you should see:
```
Success! The configuration is valid.
```

---

## Working with Map Variables

### Understanding Map Type

Maps are key-value pairs that allow you to define different values for different scenarios (like different AMI IDs for different regions).

### Add AMI Map Variable

Our current code is smart enough to get the latest AMI ID dynamically. However, let's make it region-specific using a map variable.

Add this to `vars.tf`:

```bash
variable "amiID" {
  description = "AMI IDs for different regions"
  type        = map(string)
  default     = {
    "eu-north-1" = "ami-0c322300a1dd5dc79"
    "us-east-1"  = "ami-0ff8a91507f77f867"
    "us-west-2"  = "ami-0b2f6494ff0b07a0e"
  }
}
```

**Note:** `type = map(string)` means key-value pairs where values are strings.

### Update Instance Configuration

Modify `instance.tf` to use the map variable:

**Before:**
```bash
resource "aws_instance" "web" {
  ami = data.aws_ami.amiID.id
  ...
}
```

**After:**
```bash
resource "aws_instance" "web" {
  ami = var.amiID[var.region]
  ...
}
```

This syntax `var.amiID[var.region]` looks up the AMI ID based on the current region.

---

## Introduction to Provisioners

### What are Provisioners?

Search for "Terraform Provisioners" in the official documentation, and you'll see:

> **"Provisioners are a last resort."**

### Why "Last Resort"?

Provisioners are operations that Terraform cannot fully manage. While Terraform can execute provisioners, it cannot track their state or guarantee their success.

**Example:** You want to execute a script when an instance comes up:
- Terraform can push the script (File Provisioner)
- Terraform can execute the script (Remote-exec Provisioner)
- But Terraform cannot detect if the script succeeds or fails

### Types of Provisioners

1. **file:** Copies files from local machine to remote resource
2. **remote-exec:** Executes commands on the remote resource
3. **local-exec:** Executes commands on the local machine

---

## Implementing Provisioners

### Create Web Server Script

Create a file called `web.sh` in your Terraform directory:

```bash
#!/bin/bash
apt update
apt install wget unzip apache2 -y
systemctl start apache2
systemctl enable apache2
wget https://www.tooplate.com/zip-templates/2117_infinite_loop.zip
unzip -o 2117_infinite_loop.zip
cp -r 2117_infinite_loop/* /var/www/html/
systemctl restart apache2
```

**What This Script Does:**
- Updates package lists
- Installs Apache web server, wget, and unzip
- Starts and enables Apache
- Downloads a sample website template
- Deploys the website to Apache's document root

### Add Provisioners to instance.tf

Add the following to the `aws_instance` resource in `instance.tf`:

#### 1. File Provisioner - Push the Script

```bash
provisioner "file" {
  source      = "web.sh"
  destination = "/tmp/web.sh"
}
```

This copies `web.sh` from your local machine to `/tmp/web.sh` on the instance.

#### 2. Connection Block - Define How to Connect

```bash
connection {
  type        = "ssh"
  user        = "ubuntu"  # Use ubuntu for Ubuntu AMI
  private_key = file("dove-key")
  host        = self.public_ip  # Use the instance's public IP
}
```

**Note:** `self.public_ip` references the current resource's public IP after creation.

#### 3. Remote-exec Provisioner - Execute the Script

```bash
provisioner "remote-exec" {
  inline = [
    "chmod +x /tmp/web.sh",  # Make script executable
    "sudo /tmp/web.sh"       # Execute script with sudo
  ]
}
```

### Complete instance.tf Example

```bash
resource "aws_instance" "web" {
  ami                    = var.amiID[var.region]
  instance_type          = "t3.micro"
  key_name               = "dove-key"
  vpc_security_group_ids = [aws_security_group.dove-sg.id]
  availability_zone      = var.zone
  
  tags = {
    Name    = "dove-web-instance"
    Project = "dove-terraform"
  }

  provisioner "file" {
    source      = "web.sh"
    destination = "/tmp/web.sh"
  }

  connection {
    type        = "ssh"
    user        = "ubuntu"
    private_key = file("dove-key")
    host        = self.public_ip
  }

  provisioner "remote-exec" {
    inline = [
      "chmod +x /tmp/web.sh",
      "sudo /tmp/web.sh"
    ]
  }
}
```

### Security Group Requirement

Make sure in `securitygroup.tf`, port 22 (SSH) is open for your IP address, otherwise provisioners cannot connect to the instance.

### Deploy the Infrastructure

Run the standard Terraform workflow:

```bash
terraform init
terraform fmt
terraform validate
terraform plan
terraform apply
```

### Verify Deployment

After `terraform apply` completes, you should see the website running on the public IP of your EC2 instance. Access it via:

```
http://<your-instance-public-ip>
```

---

## Output Values

### Add Output Variables

Create or update `instanceId.tf` to output instance information:

```bash
output "WebPublicIp" {
  description = "The public IP address of the web instance"
  value       = aws_instance.web.public_ip
}

output "WebPrivateIp" {
  description = "The private IP address of the web instance"
  value       = aws_instance.web.private_ip
}
```

### View Outputs

Run `terraform apply` again to see the outputs:

```bash
terraform apply
```

You'll see output like:

```
Outputs:

WebPublicIp = "13.48.123.45"
WebPrivateIp = "172.31.20.124"
```

### Save Outputs to File

Add a local-exec provisioner to save output to a file:

```bash
provisioner "local-exec" {
  command = "echo Web Instance Public IP: ${self.public_ip} >> instance_info.txt"
}
```

This creates a local file `instance_info.txt` with the instance's public IP.

### Resource Reference Syntax

To reference resource attributes, use this pattern:

```
resourceType.resourceName.attributeName
```

**Examples:**
- `aws_instance.web.public_ip`
- `aws_instance.web.id`
- `aws_security_group.dove-sg.id`

---

## Remote State with Backend

### Understanding State Management

When we create Terraform infrastructure, it creates a `terraform.tfstate` file that stores state information. This is both the best and worst part of Terraform.

### The Problem

- The state file is stored locally on your machine
- If you're working in a team, team members need access to the same state file
- The state file contains sensitive information
- Version control (GitHub) is not ideal because:
  - You only push after completing your work
  - State files contain critical and sensitive information
  - Multiple people working simultaneously can cause conflicts

### The Solution: Remote Backend

Store the state file in a remote location like an S3 bucket. Terraform has built-in integration for this.

### Benefits of Remote State

1. **Team Collaboration:** All team members access the same state file
2. **Remote State Management:** Centralized state storage
3. **State Locking and Consistency:** Prevents concurrent modifications
4. **Security:** Better access control than local storage

### Setting Up S3 Backend

#### Step 1: Create S3 Bucket

1. Go to your AWS account
2. Navigate to S3 service
3. Click "Create bucket"
4. Give a unique bucket name (e.g., `terraformstate-123456`)
5. Create the bucket

#### Step 2: Create Folder in Bucket

1. Open your bucket
2. Click "Create folder"
3. Name it `terraform`
4. Click "Create folder"

#### Step 3: Configure Backend in Terraform

Create a file called `backend.tf`:

```bash
terraform {
  backend "s3" {
    bucket = "terraformstate-123456"  # Your bucket name
    key    = "terraform/backend"      # Path inside bucket
    region = "eu-north-1"
  }
}
```

### Backend Configuration Explained

- **bucket:** The name of your S3 bucket
- **key:** The path inside the bucket where the state file will be stored
  - In this case: `terraform/backend` means the state file will be at `terraform/backend/terraform.tfstate`
- **region:** The AWS region where the bucket is located

### Initialize Backend

After creating `backend.tf`, run:

```bash
terraform init
```

Terraform will ask if you want to migrate your existing state to the new backend. Type `yes` to confirm.

### Verify Backend

1. Go to your S3 bucket in AWS Console
2. Navigate to the `terraform` folder
3. You should see the `terraform.tfstate` file

Now your state is stored remotely, and your team can collaborate effectively!

---

## Summary

### What We Learned

1. **Variables in Terraform:**
   - Defining variables with types, descriptions, and defaults
   - Using simple string variables
   - Working with map variables for region-specific values
   - Referencing variables with `var.variable_name`

2. **Provisioners:**
   - Understanding when and why to use provisioners
   - File provisioner for copying scripts
   - Connection block for SSH access
   - Remote-exec provisioner for executing commands
   - Local-exec provisioner for local operations

3. **Output Values:**
   - Displaying resource attributes after creation
   - Saving outputs to files for reference
   - Using resource reference syntax

4. **Remote State Management:**
   - Understanding state file challenges
   - Benefits of remote state storage
   - Configuring S3 backend for team collaboration
   - State locking and consistency

### Key Takeaways

- **Variables make your code flexible and reusable** across different environments
- **Map variables are powerful** for region-specific or environment-specific configurations
- **Provisioners are a last resort** - use them only when native Terraform resources aren't available
- **Remote state is essential** for team collaboration and production environments
- **Always validate and format** your code before applying changes

### Best Practices

1. **Variables:**
   - Use descriptive names and include descriptions
   - Set sensible defaults for common scenarios
   - Group related variables together in `vars.tf`

2. **Provisioners:**
   - Test scripts locally before using in provisioners
   - Handle errors gracefully in scripts
   - Use provisioners sparingly - prefer native resources when possible
   - Always ensure SSH access is configured in security groups

3. **State Management:**
   - Never commit state files to version control
   - Use remote backends for team projects
   - Enable state locking for consistency (DynamoDB with S3)
   - Regularly back up state files

4. **Outputs:**
   - Output important information like IPs and endpoints
   - Use descriptive names for outputs
   - Document what each output represents
