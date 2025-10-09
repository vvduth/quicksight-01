<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Connect a Web App with Aurora

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-databases-webapp)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

## Connect a Web App to Amazon Aurora

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-webapp_1709b26b)

---

## Introducing Today's Project!

### What is Amazon Aurora?

Amazon Aurora is a fully managed relational database service from AWS that’s compatible with MySQL and PostgreSQL. It’s useful because it combines the speed and reliability of high-end commercial databases with the simplicity and cost-effectiveness of open-source ones. Aurora automatically handles tasks like backups, patching, and scaling, so developers can focus on building applications instead of managing databases. It’s also highly available, replicating data across multiple Availability Zones to ensure performance and fault tolerance.

### How I used Amazon Aurora in this project

In today’s project, I used Amazon Aurora as the main relational database to store and manage data for my web app. I created an Aurora database cluster, connected it to my EC2 instance, and configured my web app to interact with it using PHP. This allowed my web app to display and update live data stored in the Aurora database, demonstrating how a cloud-based relational database can seamlessly integrate with a web application.

### One thing I didn't expect in this project was...

One thing I didn’t expect in this project was how many small configuration details were needed to make the EC2 instance and Aurora database communicate properly. I initially thought connecting them would be simple, but I had to adjust security groups, permissions, and file ownerships before the connection finally worked. It was a great reminder that networking and access control are just as important as the code itself.

### This project took me...

I project takes me 1 hour.

---

## Creating a Web App

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-webapp_b7999168)

To connect to my EC2 instance, I ran the command:

ssh -i NextWorkAuroraApp.pem ec2-user@16.170.224.243


This command uses my private key file (NextWorkAuroraApp.pem) to securely log in as the ec2-user on the instance with the specified public IP address. Once connected, I can install software, configure the server, and set up my web app to communicate with the Aurora database.

To start my web app, I first ran the command:

sudo dnf install -y httpd php php-mysqli mariadb105


This installs Apache (httpd) as the web server, PHP to handle server-side scripting, the mysqli extension to connect PHP with MySQL/Aurora, and MariaDB client tools for database interaction. After that, I ran:

sudo systemctl start httpd


to start the Apache web server, enabling my EC2 instance to serve the web app.

---

## Connecting my Web App to Aurora

I set up my EC2 instance to connect to the Aurora database by first giving ec2-user ownership of the /var/www folder instead of root, using:

sudo chown ec2-user:ec2-user /var/www


Then, inside /var/www, I created a folder called inc, and inside it, a file dbinfo.inc. I edited dbinfo.inc using nano to add my Aurora connection details:

<?php
define('DB_SERVER', 'nextwork-db-cluster.cluster-cxua822smk3m.eu-north-1.rds.amazonaws.com');
define('DB_USERNAME', 'admin');
define('DB_PASSWORD', 'Getrich123.');
define('DB_DATABASE', 'sample');
?>


Finally, I saved the file with Ctrl + S and exited with Ctrl + X. This file allows my PHP web app to connect securely to the Aurora database.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-webapp_1709b25b)

---

## My Web App Upgrade

Next, I upgraded my web app by going into the /var/www/html folder and creating a new file called SamplePage.php. This file imports the connection details from the dbinfo.inc file I created earlier and uses them to connect to my Aurora database. With this setup, my web app can now display real-time data directly from the database, showing updates or changes instantly on the webpage.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-webapp_2709b25b)

---

## Testing my Web App

To make sure my web app was updating my Aurora database correctly, I installed MySQL on my EC2 instance by adding the MySQL repository and installing the client. Then, I connected to my Aurora cluster using the MySQL CLI and ran commands like SHOW DATABASES;, USE sample;, and SHOW TABLES; to confirm my schema and tables were there. I also ran DESCRIBE employees; to view the table structure and SELECT * FROM employees; to check if new data added through the web app appeared in the database — confirming that the connection between my web app and Aurora worked perfectly.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-databases-webapp_1409z22b)

---

---
