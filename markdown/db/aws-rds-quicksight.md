<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Visualize a Relational Database

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-databases-rds)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

## Visualise a Relational Database

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-rds_1fddb0b5)

---

## Introducing Today's Project!

### What is Amazon RDS?

Amazon RDS (Relational Database Service) is a managed database service from AWS that makes it easy to set up, operate, and scale relational databases like MySQL, PostgreSQL, or Aurora. It is useful because it handles tasks like backups, patching, and high availability automatically, allowing developers and engineers to focus on building applications instead of managing database infrastructure.

### How I used Amazon RDS in this project

In today’s project, I used Amazon RDS to create a MySQL relational database where I stored sample data in tables. I configured it to be secure and private, connected it to QuickSight for visualization, and practiced managing access using security groups and VPC connections. This allowed me to store, organize, and analyze data safely in the cloud.

### One thing I didn't expect in this project was...

One thing I didn't expect in this project was how important securing the database and managing access properly is. I realized that making an RDS instance public is risky, and setting up VPC connections, dedicated security groups, and IAM roles for QuickSight adds extra layers of security that I hadn’t fully appreciated before. It showed me that cloud database management isn’t just about storing data—it’s also about protecting it.

### This project took me...

This project took me 3 hours, which was longer than I expected. At first, I couldn’t connect my RDS to MySQL Workbench and thought it was a Workbench or password issue. I tried downgrading Workbench, reinstalling MySQL server, and upgrading Workbench, but nothing worked. I even deleted and recreated the RDS instance, but the problem persisted. Eventually, I realized the subnet’s route table had a broken route to a deleted Internet Gateway, creating a blackhole route. Once I updated the route to a valid IGW, the connection finally worked.

---

## In the first part of my project...

### Creating a Relational Database

I created my relational database by logging in as an IAM user, going to the Aurora and RDS service, and selecting “Create Database” with MySQL as the database engine. I then configured settings like DB instance size, storage, and credentials to set up a fully functional relational database in AWS.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-rds_43343546)

---

## Understanding Relational Databases

A relational database is a type of database that organizes data into tables made up of rows and columns. Each table stores information about one type of thing (like customers, orders, or products), and the tables are linked together using relationships — usually through shared keys. This structure makes it easy to manage, search, and combine data efficiently while keeping it organized and consistent.

### MySQL vs SQL

The difference between MySQL and SQL is that SQL (Structured Query Language) is the language used to manage and query data in a database, while MySQL is a database management system (DBMS) that uses SQL to store, organize, and retrieve data. In simple terms, SQL is the language, and MySQL is one of the tools that understands and uses that language.

---

## Populating my RDS instance

The first thing I did was make my RDS instance public because I wanted to connect to it from my local machine using MySQL Workbench. Making it public allows external tools to access the database over the internet while I control who can connect through the security group settings.

I had to update the default security group for my RDS instance to allow access only from my IP even though the instance is public because making it public exposes it to the internet. Restricting access ensures that only my machine can connect, keeping the database secure from unauthorized access.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-rds_91b9fd1g)

---

## Using MySQL Workbench

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-rds_1fddb0b5)

To populate my database, I created a new schema named QuickSightDatabase. After selecting it by double-clicking, I wrote SQL commands to create two tables — new_hire and department — using the CREATE TABLE statement. Then, I used the INSERT command to add sample records into each table. This process helped me understand how to define tables, set data types, and manually input data using SQL, which is a key part of working with relational databases.

---

## Connecting QuickSight and RDS

To connect my RDS instance to QuickSight, I first signed up for a free trial QuickSight account in the same region as my RDS database. After that, I went to the QuickSight console and clicked “New Dataset” to start connecting. I selected RDS as the data source and chose my database instance from the list. Then, I entered my database username and password to establish the connection. Once connected, QuickSight automatically detected my schema and tables, allowing me to import and visualize the data directly.

This solution is risky because my RDS security group allows inbound traffic from anywhere (0.0.0.0/0), meaning the database is publicly accessible over the internet. That’s a major security concern because anyone could try to connect, including hackers or malicious users. It exposes sensitive data to potential attacks like unauthorized access, SQL injection, or brute-force login attempts. In real-world setups, it’s best to restrict access to specific IPs or use private connections such as VPC peering or AWS PrivateLink to keep the database secure.

### A better strategy

This new security group is for managing and securing the connection between QuickSight and my RDS instance. I created it inside the same VPC as my RDS instance so that QuickSight can communicate directly with the database through private network paths. This setup helps control which resources can access the RDS instance, avoids exposing it to the internet, and ensures that only trusted AWS services within the same VPC can send or receive data securely.

To connect my new security group to QuickSight, I went to “Manage QuickSight,” then added a VPC connection by filling in the details of the VPC I wanted QuickSight to access. I assigned the connection the execution role aws-quicksight-service-role-v0. However, this role initially didn’t have enough permissions to create VPC connections, so I updated its IAM policy to include the necessary permissions. After that, QuickSight was successfully able to connect to my VPC securely through the new security group.

---

## Now to secure my RDS instance

To make my RDS instance secure, I set it to Not Publicly Accessible. Then, I created a new security group for the RDS instance that only allows MySQL/Aurora traffic from my QuickSight security group. This ensures that the database cannot be accessed from the public internet and only QuickSight can communicate with it securely within the VPC.

I gave QuickSight access to my RDS instance by removing the default security group from the RDS instance and attaching the new security group I created. This new security group is configured to allow MySQL/Aurora traffic only from the QuickSight security group, ensuring that QuickSight can connect securely while keeping the database private from the public internet.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-rds_1709b26b)

---

## Adding RDS as a data source for QuickSight

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-rds_1709b29b)

This data source is different from my initial data source because it is now connected through a private, secure VPC setup instead of being publicly accessible. QuickSight can access the RDS instance only through the dedicated security groups and VPC connection, ensuring that the database is protected from public internet access while still allowing visualization and analysis.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-rds_1709b30b)

---

---
