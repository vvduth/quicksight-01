<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Set Up a Web App in the Cloud

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-devops-vscode)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

## Set Up a Web App Using AWS and VS Code

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-vscode_7a1de541)

---

## Introducing Today's Project!
  
In this project, I will demonstrate the basics of building a web app using VS Code and AWS. The steps include:

- 💻 Launching an EC2 instance.
- 🔌 Using VS Code to set up a remote SSH connection to the EC2 instance.
- ☕️ Installing Maven and Java, then generating a basic web app.
- 💎 Editing code without VS Code, to experience the difference and appreciate the power of modern IDEs.

I'm doing this project to learn how to set up cloud infrastructure (EC2), connect and work remotely using VS Code, install and use development tools on a server, and understand the workflow of building and editing a web application in a cloud environment. This hands-on experience will help me understand both the benefits of IDEs and the fundamentals of cloud-based development.

### Key tools and concepts

Services I used were:
- **Amazon EC2** (Elastic Compute Cloud) for launching and managing virtual servers in the cloud.
- **AWS Key Pairs** for secure authentication to my EC2 instance.
- **SSH** for secure remote connections to the server.
- **VS Code** with the Remote - SSH extension for cloud-based code editing and management.

Key concepts I learnt include:
- How to generate and use SSH key pairs for secure access.
- Managing file and folder permissions for security (using Icacls on Windows).
- Connecting to a cloud server using SSH and VS Code.
- Installing and configuring development tools (Maven, Amazon Corretto) on a remote instance.
- Generating and organizing a Java web application using Maven.
- Navigating and editing files both in the terminal and using an IDE.
- Understanding the roles of project folders like `src` and `webapp`, and files like `index.jsp`.
- How cloud development workflows differ from local development and why secure access and permissions are im

### Project reflection

One thing I didn’t expect in this project was that the t3.micro instance (which is Free Tier) would frequently disconnect or lock up when I connected to it from VS Code. It would connect at first, but after opening a folder or working for a bit, it kept reconnecting or the server would become unresponsive.

This happened because Free Tier EC2 instances only have 1 GB of RAM and no swap space. When VS Code’s remote server and extensions use up memory, the instance runs out of resources and disconnects.

**The Fix:** I added swap memory, which acts like “backup RAM” using disk space. It doesn’t make the server faster, but it prevents crashes and constant reconnects.

Here are the commands I ran on my EC2 instance to add 1 GB of swap:

```sh
# Create 1GB swap
sudo fallocate -l 1G /swapfile

# Secure it
sudo chmod 600 /swapfile

# Format it as swap
sudo mkswap /swapfile

# Enable swap
sudo swapon /swapfile

# Check if it worked
free -h
```


This project took me approximately a few hours to complete from start to finish. The most challenging part was troubleshooting connectivity issues with my EC2 instance, especially when VS Code kept disconnecting due to limited memory. Setting up swap space and ensuring all tools worked smoothly took extra effort. It was most rewarding to see my Java web application running successfully on the cloud and being able to edit and manage it remotely with VS Code. This gave me hands-on experience with cloud development and remote server management.

This project is part one of a series of DevOps projects where I'm building a CI/CD pipeline! I'll be working on the next project in a few hours after dinner.

---

## Launching an EC2 instance
  
I started this project by launching an EC2 instance because an EC2 instance provides a virtual server in the cloud where I can install software, run applications, and practice configuring a real server environment. By using EC2, I get hands-on experience with cloud infrastructure, remote access, and server management—all essential skills for deploying and maintaining web applications in a cloud environment. It also allows me to simulate a production environment, making my learning more practical and relevant to real-world scenarios.

### I also enabled SSH

SSH (Secure Shell) is a protocol used to securely connect to and manage remote servers. It ensures that only authorized users can access the server by requiring a private key on your computer that matches a public key stored on the server.

I enabled SSH traffic from my IP to my EC2 instance so that only I can connect to it remotely and securely. This protects my instance from unauthorized access and ensures that only trusted connections—specifically from my computer—are allowed to manage or configure the server.

### Key pairs

A key pair is the thing determines the algorithm used for generating the key pair's cryptographic keys.

We use RSA (Rivest-Shamir-Adleman), which is one of the most common cryptographic algorithms used due to its strength and security. RSA is widely supported and trusted for creating digital signatures and encrypting data.

Once I set up my key pair, AWS automatically downloaded a `.pem` file (for example, `my-key-pair.pem`) to my local computer. This file is the private key for securely connecting to my EC2 instance via SSH. It’s important to keep this file safe, as it’s required to access the instance and cannot be downloaded again from AWS.

---

## Set up VS Code

VS Code is a code editor.

I installed VS Code to write code.


![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-vscode_53d05e68)

---

## My first terminal commands

A terminal is A terminal is where you send instructions to your computer using text instead of clicks. For example, instead of right-clicking on your desktop to create a new folder, you can type a simple text command in your terminal instead. It's like sending text messages to your computer's operating system to tell it what to do.

Every computer has a terminal. On Windows, it's often called Command Prompt or PowerShell, while macOS and Linux systems use Terminal.  
The first command I ran in my terminal for this project was:

```sh
cd %USERPROFILE%\git_repos\devops-7days
```

This command changes the current directory to the `devops-7days` folder inside the `git_repos` directory in my user profile. Using `cd` (which stands for "change directory") is a common way to navigate to the project folder where I keep all my code and files for this challenge.

I also updated my private key's permissions using the Icacls tool on Windows, which stands for Integrity Control Access Control Lists. Icacls lets you manage who can read, write, or change the files on your system. The commands I used were:

```sh
icacls "nextwork-keypair.pem" /reset
icacls "nextwork-keypair.pem" /grant:r "Enter your username:R"
icacls "nextwork-keypair.pem" /inheritance:r
```

- The first command (`/reset`) removes all existing permissions and sets them to default.
- The second command grants only my username read permissions (`:R`) on the file.
- The third command removes inherited permissions from the file.

This ensures that only I can read the private key, which is important for security when connecting to my EC2 instance with SSH.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-vscode_9328ada1)

---

## SSH connection to EC2 instance
  
To connect to my EC2 instance, I ran the following command in my terminal:

```sh
ssh -i nextwork-keypair.pem ec2-user@ec2-13-60-46-17.eu-north-1.compute.amazonaws.com
```

This command uses SSH (Secure Shell) with the `-i` flag to specify my private key file `nextwork-keypair.pem`, and connects to the EC2 instance using the default `ec2-user` account and the public DNS of my instance. This gives me secure, remote access to the server so I can install software and manage my application.

### This command required an IPv4 address

A Public IPv4 DNS (which stands for Domain Name System) is the public address for your EC2 server that the internet uses to find and connect to it. The local computer you're using to do this project will find and connect to your EC2 instance through this IPv4 DNS.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-vscode_e3069dca)

---

## Maven & Java

Apache Maven is a tool that helps developers build and organize Java software projects. It's also a package manager, which means it automatically download any external pieces of code your project depends on to work.

Maven is required in this project because it's really useful for kick-starting web projects! It uses something called archetypes, which are like templates, to lay out the foundations for different types of projects e.g. web apps.

I will use Maven later on to help me set up all the necessary web files to create a web app structure, so I  can jump straight into the fun part of developing the web app sooner.

Java is a popular programming language used to build different types of applications, from mobile apps to large enterprise systems.

Maven, which we just downloaded, is a tool that NEEDS Java to operate.

Java is required in this project because Maven, which we just downloaded, is a tool that NEEDS Java to operate. So if we don't install Java, we won't be able to use Maven to generate/build our web app today.

Amazon Corretto 8 is a version of Java that we're using for this project. It's free, reliable and provided by Amazon.

---

## Create the Application

 I generated a Java web app using the following Maven command:

```sh
mvn archetype:generate \
   -DgroupId=com.nextwork.app \
   -DartifactId=nextwork-web-project \
   -DarchetypeArtifactId=maven-archetype-webapp \
   -DinteractiveMode=false
```

This command uses Maven’s archetype feature to quickly create a new Java web application project with a standard structure. The options specify the group ID, artifact ID, and the web app archetype, while `-DinteractiveMode=false` makes the process non-interactive and automatic.

I installed Remote - SSH in VS Code, which is an extension that allows you to securely connect and work on remote servers directly from your editor. I installed it to easily access, edit, and manage files on my EC2 instance as if they were on my local machine. This makes development much smoother, lets me use all of VS Code’s powerful features remotely, and helps streamline workflows when working with cloud-based environments.

Configuration details required to set up a remote connection include:

- The **hostname** or **public DNS** of your EC2 instance (e.g., `ec2-13-60-46-17.eu-north-1.compute.amazonaws.com`)
- The **username** for the connection (`ec2-user` for Amazon Linux, `ubuntu` for Ubuntu instances, etc.)
- The **private key file** path (e.g., `nextwork-keypair.pem`) used for secure authentication
- The **port number** (usually `22` for SSH)
- Any additional settings such as agent forwarding or custom commands

These details are typically stored in a configuration file (like `.ssh/config` or within the settings of your VS Code Remote - SSH extension) to make connecting to your EC2 instance quick and secure, without needing to type the full SSH command each time.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-vscode_2939cf01)

---

## Create the Application

Using VS Code's file explorer, I opened `/home/ec2-user/nextwork-web-project` and I could see the following files and folders (as shown in the picture):

- A `src` directory with:
  - `main`
    - `resources` folder
    - `webapp` folder, which contains:
      - `WEB-INF` directory
        - `web.xml` file
      - `index.jsp` file
- A `pom.xml` file at the root

This structure shows the standard organization of a Java web application created by Maven, with resources, web application files, configuration files, and the build descriptor (`pom.xml`).

  Two of the project folders created by Maven are **src** and **webapp**, which serve distinct purposes:

- **src**: This is the source folder where your main project code resides. Typically, you’ll find subfolders like `main/java` for Java source files and `main/resources` for other resources (like configuration files or assets) that your application needs.

- **webapp**: This folder is for web application resources. It contains files and folders needed for the web part of your app, such as HTML, JSP files, and the `WEB-INF` directory (which holds configuration files like `web.xml` that define how your web application behaves).

Together, these folders organize your code and resources in a standard structure, helping Maven build and manage your Java web application efficiently.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-vscode_45f91fd7)

---

## Using Remote - SSH
  
The `index.jsp` is a Java Server Page file that serves as the starting or main page of your web application. JSP files allow you to write HTML combined with Java code, so you can create dynamic web pages that respond to user input or display data from your server. In most Java web apps, `index.jsp` is the default landing page users see when they visit your site.

I edited `index.jsp` by adding the following HTML code:

```html
<html>
  <body>
    <h2>Hello my name is Dukem!</h2>
    <p>This is my NextWork web application working!</p>
  </body>
</html>
```

This creates a simple web page that displays a greeting and a message, confirming that your NextWork web application is up and running.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-vscode_7a1de541)

---

## Using nano
  
I opened `index.jsp` on my terminal by using a text editor command. On a Linux-based EC2 instance, I could use editors such as:

- `nano index.jsp` — Opens the file in the Nano editor, which is easy for beginners.
- `vi index.jsp` or `vim index.jsp` — Opens the file in the Vi or Vim editor, which is powerful but requires some learning.
- `cat index.jsp` — Displays the contents of the file, but doesn’t allow editing.

For example, running:

```sh
nano index.jsp
```

opened the file in the Nano editor so I could view and make changes to the contents directly from the terminal.
  
Compared to using an IDE, editing `index.jsp` in the terminal felt less intuitive and slower, especially for tasks like navigating files, syntax highlighting, and finding errors. The terminal editors like Nano or Vim are powerful, but they require memorizing keyboard shortcuts and commands, and they don’t offer the same visual feedback or productivity tools as an IDE.

I’d be more likely to use an IDE if I want features like code completion, real-time error checking, integrated version control, and a graphical interface that makes editing, navigating, and debugging code much easier and faster. IDEs also let me manage large projects more comfortably and help reduce mistakes, which is especially helpful for beginners or when working on complex codebases.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-vscode_a3324ad41)

---

---
