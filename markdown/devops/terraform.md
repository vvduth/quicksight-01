# Launching EC2 with Terraform

---

## Table of Contents

1. [Finding AMI IDs](#finding-ami-ids)
2. [Terraform Basics](#terraform-basics)
3. [Basic Commands](#basic-commands)
4. [Running Your First Script](#running-your-first-script)
5. [Summary](#summary)

---

## Finding AMI IDs

There are several ways to find Amazon Machine Image (AMI) IDs for your EC2 instances.

### Method 1: AWS CLI

Use the AWS CLI command to find the latest Amazon Linux 2 AMI:

```bash
aws ec2 describe-images --owners amazon --filters "Name=name,Values=amzn2-ami-hvm-*-x86_64-gp2" --query "reverse(sort_by(Images, &CreationDate))[:1].ImageId" --output text
```

### Method 2: AWS Console

1. Go to EC2 Dashboard
2. Navigate to Images → AMIs
3. Search for "amzn2-ami-hvm-*-x86_64-gp2"

### Method 3: Terraform Data Source

Use Terraform data source to get the latest AMI ID dynamically:

```hcl
data "aws_ami" "amiID" {
  most_recent = true
  
  filter {
    name   = "name"
    values = ["ami-0fa91bc90632c73c9"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["amazon"]
}
```

---

## Terraform Basics

### Data Sources

To get information outside Terraform, we use data sources.

**Syntax:**
```hcl
data "TYPE" "NAME" {
  CONFIG
}
```

Data sources allow you to fetch information from your cloud provider or other external sources.

### Output Blocks

Just like print in Python, output blocks are used to display values after `terraform apply`.

**Syntax:**
```hcl
output "output_name" {
  description = "Description of the output"
  value       = resource.type.name.attribute
}
```

---

## Basic Commands

Here are the essential Terraform commands you need to know:

| Command | Description |
|---------|-------------|
| `terraform fmt` | Format configuration files to canonical format |
| `terraform init` | Initialize working directory and download provider plugins |
| `terraform validate` | Validate configuration files for syntax errors |
| `terraform plan` | Create an execution plan showing what actions Terraform will take |
| `terraform apply` | Apply changes to reach the desired state of the configuration |

### Command Details

- **init:** Reads configuration files in the current directory and downloads necessary provider plugins.
- **plan:** Creates an execution plan, showing what actions Terraform will take to achieve the desired state.
- **apply:** Applies the changes required to reach the desired state of the configuration.

---

## Running Your First Script

### Script Example

Create a file named `main.tf` with the following content:

```hcl
data "aws_ami" "amiID" {
  most_recent = true
  
  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["099720109477"] # Canonical
}

output "instance_ami_id" {
  description = "AMI ID of Ubuntu instance"
  value       = data.aws_ami.amiID.id
}
```

### What This Script Does

This script will output the latest Ubuntu 22.04 AMI ID using Terraform data sources.

### Running the Commands

Execute the following commands in order:

```bash
terraform init
terraform plan
terraform apply
```

### Expected Output

```
ducth@DucPC MINGW64 ~/git_repos/quicksight-01/code/terraform/ex1 (main)
$ terraform apply
data.aws_ami.amiID: Reading...
data.aws_ami.amiID: Read complete after 0s [id=ami-0c846debef94e83c2]

Changes to Outputs:
  + instance_ami_id = "ami-0c846debef94e83c2"

You can apply this plan to save these new output values to the Terraform state, 
without changing any real infrastructure.

Do you want to perform these actions?
  Terraform will perform the actions described above.
  Only 'yes' will be accepted to approve.

  Enter a value: yes


Apply complete! Resources: 0 added, 0 changed, 0 destroyed.

Outputs:

instance_ami_id = "ami-0c846debef94e83c2"
```

---

## Summary

### Key Takeaways

- **Terraform** is an infrastructure as code tool that allows you to define and manage cloud resources.
- **Basic Commands:** `init`, `validate`, `plan`, `apply` are the core commands for working with Terraform.
- **Data Sources** are used to fetch information from outside Terraform (like AMI IDs from AWS).
- **Output Blocks** display values after `terraform apply`, similar to print statements in programming.
- **AMI IDs** can be found using AWS CLI, AWS Console, or Terraform data sources for dynamic retrieval.