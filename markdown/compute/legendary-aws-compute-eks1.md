<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Launch a Kubernetes Cluster

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-compute-eks1)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

## Launch a Kubernetes Cluster

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-compute-eks1_e5f6g7h8" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Introducing Today's Project!

In this project, I will learn about Kubernetes and Amazon EKS by:

- Launching and connecting to an EC2 instance.
- ✨ Creating my very own Kubernetes cluster.
- ☁️ Monitoring cluster creation with CloudFormation.
- 🔑 Accessing my cluster using an IAM access entry.
- 💎 Testing the resilience of my Kubernetes cluster.

...because understanding how to set up, manage, and test a Kubernetes cluster on AWS will give me hands-on experience with modern cloud-native infrastructure, which is essential for deploying scalable, reliable applications in the cloud.

### What is Amazon EKS?

### One thing I didn't expect

### This project took me...

---

## What is Kubernetes?

Kubernetes is a container orchestration platform. It automates the deployment, scaling, and management of containerized applications across clusters of servers. Kubernetes makes sure your containers are running where they should, automatically scales them based on traffic, and restarts them if they crash.

Companies and developers use Kubernetes because it keeps large, container-based applications reliable and easy to scale as demand changes. Without Kubernetes, you’d have to manage every container manually—starting, stopping, monitoring, and updating them one by one. This gets extremely difficult and time-consuming as your app grows.

Kubernetes solves these problems by:

- Automatically distributing containers across servers for best performance
- Scaling the number of containers up or down based on traffic
- Ensuring failed containers are restarted
- Making updates seamless without causing downtime
- Managing storage and networking for containers

I used **eksctl** to create an EKS cluster by running:

```
eksctl create cluster \
--name nextwork-eks-cluster \
--nodegroup-name nextwork-nodegroup \
--node-type t3.micro \
--nodes 3 \
--nodes-min 1 \
--nodes-max 3 \
--version 1.33 \
--region eu-north-1
```

The **create cluster** command I ran defined:

- The name of the cluster (`--name nextwork-eks-cluster`)
- The node group name (`--nodegroup-name nextwork-nodegroup`)
- The type of EC2 instances for the nodes (`--node-type t3.micro`)
- The desired number of nodes in the group (`--nodes 3`)
- The minimum and maximum number of nodes to allow for auto-scaling (`--nodes-min 1` and `--nodes-max 3`)
- The Kubernetes version (`--version 1.33`)
- The AWS region where the cluster will be created (`--region eu-north-1`)

This command tells eksctl to create a new Kubernetes cluster on AWS EKS, set up a node group with the specified instance type and scaling options, and configure everything in the chosen region.

I initially ran into two errors while using eksctl:

1. The first error occurred because I hadn’t installed eksctl yet, so the command wasn’t recognized on my EC2 instance.
2. The second error happened because my EC2 instance didn’t have permission to access other AWS resources. I had to modify the IAM role attached to my EC2 instance to grant it administrator access, which allowed eksctl to create and manage AWS resources needed for the EKS cluster.

After installing eksctl and updating the IAM role, I was able to successfully run eksctl commands and create my Kubernetes cluster.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-compute-eks1_ff9bfc221" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## eksctl and CloudFormation

CloudFormation is involved in creating my EKS cluster because, when I ran the `eksctl create cluster` command, eksctl automatically set up a CloudFormation stack to handle the creation and configuration of all the AWS resources needed for the cluster.

CloudFormation created critical resources, including:
- VPC (Virtual Private Cloud)
- Subnets
- Route tables
- Security groups
- NAT gateways
- Internet gateways

These resources establish a private, secure network for my containers, allowing them to communicate with each other and connect to the internet, while also keeping my app protected. While I could use my AWS account’s default VPC, it might not have all the settings required for a Kubernetes cluster. That’s why eksctl, via CloudFormation, creates a brand new VPC tailored for EKS, so I get a fresh, properly configured environment out of the box.

There was a second CloudFormation stack for the node group (in my case, named `eksctl-nextwork-eks-cluster-nodegroup-nextwork-nodegroup`), alongside the main cluster stack (`eksctl-nextwork-eks-cluster-cluster`). 

The difference between a **cluster** and a **node group** is:

- The **cluster stack** (`eksctl-nextwork-eks-cluster-cluster`) creates the core EKS control plane—this includes the Kubernetes API server, etcd, and basic networking resources.
- The **node group stack** (`eksctl-nextwork-eks-cluster-nodegroup-nextwork-nodegroup`) is responsible for creating the EC2 instances (“nodes”) that actually run your application containers.

CloudFormation separates the core EKS cluster stack from the node group stack to make it easier to manage and troubleshoot each part independently. If you need to update, delete, or fix a problem in the node group, you can do so without affecting the core cluster—and vice versa. This separation is especially helpful if one half fails.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-compute-eks1_w3e4r5t6" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## The EKS console

I had to create an IAM access entry to gain access to resources (like nodes) in my Kubernetes cluster. Even though my IAM user has AdministratorAccess in AWS, those permissions alone don’t automatically carry over to Kubernetes—Kubernetes uses its own system (RBAC) to manage access within the cluster.

An IAM access entry is like a handshake between AWS and Kubernetes. IAM is AWS’s login and permission system; RBAC (Role Based Access Control) is Kubernetes’s. The access entry maps my IAM user or role to a Kubernetes RBAC role, so the cluster lets me interact with resources like nodes.

I set it up by creating a new access entry through the banner in the EKS console (though it can also be done via IAM directly). When setting it up, I made sure:

- The IAM principal ARN is my IAM Admin user.
- The Policy name is AmazonEKSClusterAdminPolicy.

This mapping gives my IAM user administrative access to my cluster in Kubernetes, allowing me to fully manage and interact with cluster resources.

It took 20 minutes to create my cluster. Since I’ll be creating this cluster again in the next project of this series, maybe this process could be sped up if I:

- Use a more powerful EC2 instance for faster resource provisioning
- Pre-install all the required tools like eksctl before starting
- Create reusable CloudFormation templates or eksctl config files
- Automate setup steps with scripts
- Make sure my IAM roles and permissions are configured ahead of time

These steps could help make cluster creation quicker and smoother next time.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-compute-eks1_e5f6g7h8" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## EXTRA: Deleting nodes

Did you know you can find your EKS cluster's nodes in Amazon EC2? This is because, when you create a node group for your EKS cluster, Kubernetes uses EC2 instances as the worker nodes that actually run your containers. These EC2 instances are managed by EKS, but they’re regular virtual machines hosted in your AWS account, so you can view, monitor, and manage them directly from the EC2 console. This gives you visibility and control over your cluster’s compute resources, just like with any other EC2 instance.

Minimum and maximum sizes are helpful for controlling how your node group scales based on traffic and resource needs.

- The **minimum size** ensures your app always has enough resources to stay available, even during low-demand times.
- The **maximum size** puts a limit on how much your node group can grow, preventing unexpected costs or resource over-provisioning during traffic spikes.

By setting these limits, you allow EKS to automatically scale your node group between the minimum and maximum sizes, helping balance availability, performance, and cost.

When I deleted my EC2 instances (which are the worker nodes in my EKS cluster), Kubernetes detected that the nodes had disappeared and automatically started replacing them. This is because Kubernetes is designed to maintain the desired state of your cluster, including the number of nodes specified in your node group. When a node goes missing, the EKS node group controller launches a new EC2 instance to replace it, ensuring that your applications keep running smoothly and your cluster remains healthy and resilient.

<img src="http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-compute-eks1_q7r8s9t0" alt="Image" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

---
