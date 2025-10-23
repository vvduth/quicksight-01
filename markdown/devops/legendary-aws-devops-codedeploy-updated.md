<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Deploy a Web App with CodeDeploy

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-devops-codedeploy-updated)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codedeploy-updated_val-27)

---

## Introducing Today's Project!

In this project, I will demonstrate deployment in web development using AWS CodeDeploy. Deployment often involves many manual steps—like copying files to servers and installing dependencies—which can be time-consuming, error-prone, and hard to reproduce. I’m doing this project to learn how to use a continuous deployment service and specifically how CodeDeploy:

- Automates deployments, eliminating manual steps and reducing human error.
- Enables consistent rollouts so that every deployment happens the same way.
- Minimizes downtime by using deployment strategies that keep the application available.
- Handles failures by automatically rolling back if something goes wrong.

By learning CodeDeploy, I’ll gain hands-on experience in automating, securing, and streamlining the deployment process for web applications.

### Key tools and concepts

Services I used were AWS EC2 (for hosting the web server), AWS CodeDeploy (for automating deployments), AWS S3 (for storing build artifacts), IAM (for managing permissions), and CodeBuild (for building and packaging the application).  

Key concepts I learnt include automated deployment pipelines, writing deployment scripts, configuring appspec.yml and buildspec.yml, using deployment groups and tags for targeting instances, managing IAM roles for security, handling deployment errors and rollbacks, and understanding the flow of CI/CD in real-world cloud environments.

### Project reflection

This project took me approximately 3 hours to complete. The most challenging part was after manually fixing the broken script, rebuilding, and redeploying—sometimes the CodeDeploy Agent would cache the old deployment scripts, including the failed stop_server.sh. I had to reboot my deployment instance and run:

```
sudo rm -rf /opt/codedeploy-agent/deployment-root/deployment-instructions/*
sudo rm -rf /opt/codedeploy-agent/deployment-root/deployment-group-id/*
sudo rm /usr/share/tomcat/webapps/nextwork-web-project.war
sudo service codedeploy-agent restart
```

It was most rewarding to see my web application running smoothly after overcoming these deployment challenges!

This project is part five of a series of DevOps projects where I'm building a CI/CD pipeline! I'll be working on the next project today, after the gym.

---

## Deployment Environment

answer:  
To set up for CodeDeploy, I launched an EC2 instance and VPC because CodeDeploy needs a target server (the EC2 instance) where it can deploy the application, and a network environment (the VPC) to securely manage communication between AWS resources. The EC2 instance acts as the production host for my web app, while the VPC ensures the instance is properly isolated and configured for network access, security, and scalability. Setting these up provides a reliable, cloud-based foundation for automated deployments and smooth application rollouts.

Instead of launching these resources manually, I used AWS CloudFormation to deploy my EC2 instance and VPC. I activated the “Delete all newly created resources during rollback” setting, which means if something goes wrong during creation or I need to delete the stack later, CloudFormation will automatically clean up all resources it created. This makes managing infrastructure much easier and safer, since I don’t have to manually track or delete every resource—CloudFormation handles it for me.

Other resources created in this CloudFormation template include a VPC and all the necessary networking components such as subnets, internet gateway (IGW), security groups, route tables, and IAM roles, along with the EC2 instance. These resources are included in the template to ensure that the EC2 instance is launched in a secure and properly configured network environment. Having all these components defined together in the template allows for automated, repeatable, and consistent infrastructure setup, making it easier to manage and update your deployment stack in AWS.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codedeploy-updated_val-5)

---

## Deployment Scripts

Scripts are like mini-programs that automate tasks by executing a series of commands in order. Instead of typing each command manually in the terminal, you put them in a script file so they run automatically, saving time and reducing errors. In our project, we use scripts to automate deployment tasks—such as installing software, configuring servers, and setting up environments.

To set up CodeDeploy, I wrote a script that installs Tomcat and Apache HTTPD, then creates a custom configuration file for the web server. This ensures that all dependencies and settings are applied consistently on every deployment, making the process reliable and repeatable. Automating with scripts keeps everything efficient and professional, especially when deploying to multiple servers or environments.

The install_dependencies.sh script sets up all the software needed to run our website by installing programs (called Tomcat and Apache) that handle internet traffic and host our application. It then creates special settings that let these programs to work together, making our website accessible to visitors on the internet.

The start_server.sh script automates the process of starting and enabling your application’s server services. Specifically, it starts the Tomcat service (which runs my Java web app) and the Apache HTTPD service (which acts as the web server), and enables both services to automatically start on boot. This ensures my application and web server are running and will stay running even after a system reboot, making your deployment reliable and minimizing downtime.

The stop_server.sh script safely stops your web server services by first checking if they’re currently running. It uses `pgrep` to look for active Apache (httpd) and Tomcat processes, and only issues the stop command if each service is actually active. This prevents unnecessary errors from trying to stop services that aren’t running.

Specifically, the script:

- Checks if Apache (httpd) is running  
- Stops Apache if it is active  
- Checks if Tomcat is running  
- Stops Tomcat if it is active  

This helps ensure a smooth and error-free shutdown process during deployments or maintenance.

---

## appspec.yml

The appspec.yml file is a configuration file used by AWS CodeDeploy to define how your application should be deployed to a server. I wrote an appspec.yml file to specify which files should be copied during deployment and which scripts should be executed at different stages of the deployment process.  

The key sections in appspec.yml are:  
- **version**: Specifies the version of the appspec format.
- **os**: Indicates the operating system (e.g., linux).
- **files**: Lists which files to copy from your build artifact to the destination on the target server (for example, copying your WAR file to the Tomcat webapps directory).
- **hooks**: Defines lifecycle events where custom scripts are run, such as:
  - **BeforeInstall**: Runs a script to install dependencies before the application is installed.
  - **ApplicationStart**: Runs a script to start your server (Tomcat and Apache) after files are installed.
  - **ApplicationStop**: Runs a script to stop your server before a new depl.

I updated buildspec.yml by modifying the `artifacts` section to include `appspec.yml` and the entire `scripts` folder:

```yaml
artifacts:
  files:
    - target/nextwork-web-project.war
    - appspec.yml
    - scripts/**/*
  discard-paths: no
```

This change tells CodeBuild to package not only the compiled WAR file, but also the deployment instructions (`appspec.yml`) and all deployment scripts. Including these files ensures that CodeDeploy has everything it needs to successfully deploy and configure the application. Without them, CodeDeploy wouldn’t know how to handle, start, or stop the app during deployment!

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codedeploy-updated_val-12)

---

## Setting Up CodeDeploy

answer:  
A CodeDeploy deployment group is a collection of deployment settings and target resources (like EC2 instances or Auto Scaling groups) for a specific application. It defines where and how your application will be deployed, including things like deployment configurations, environment tags, and rollback options.

A CodeDeploy application, on the other hand, is a logical entity that represents the overall app you want to deploy. It serves as a container for one or more deployment groups and organizes everything related to deploying that application.

In short:  
- A **CodeDeploy application** is the top-level object representing your app.  
- A **deployment group** is a set of instructions and targets within the application that tells CodeDeploy how and where to deploy your code.

More:  it's where you tell CodeDeploy "let's deploy this app to these specific servers, using this deployment pattern, with these load balancing settings, and handle failures this way."

To set up a deployment group, you also need to create an IAM role for CodeDeploy  to get permissions to access and manage AWS resources on my behalf. These permissions let CodeDeploy do things like:
- Accessing EC2 instances to deploy applications.
- Reading application artifacts from S3 buckets.
- Updating Auto Scaling groups.
- Write CloudWatch logs about what it's doing

Tags are helpful for organizing and identifying resources in AWS. I used the tag with `"role"` as the key and `"webserver"` as the value to make it easy for CodeDeploy to target the correct EC2 instance during deployment. By tagging my EC2 instance this way, CodeDeploy can automatically find and deploy to instances that match this tag, ensuring that only the intended servers receive the new application. Tags also help with resource management, automation, and tracking in larger environments.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codedeploy-updated_val-18)

---

## Deployment configurations

Another key settings is the deployment configuration, which affect how updates are rolled out to my target instances. The main options are:

- **CodeDeployDefault.AllAtOnce**: Deploys the new version to all target instances at the same time. This is fast, but if there’s a problem with the deployment, it can affect all users at once.
- **CodeDeployDefault.HalfAtATime**: Deploys the update to half of the instances first, then to the rest. This provides a balance between speed and safety, allowing you to catch errors before all users are affected.
- **CodeDeployDefault.OneAtATime**: Deploys to one instance at a time, only moving to the next after the previous one succeeds. This is the safest option and minimizes downtime, but is slower.

I used **CodeDeployDefault.AllAtOnce**, so my application was updated on all target instances simultaneously—ideal for quick rollouts, but with the risk that any deployment issue could impact all production servers at once.

A CodeDeploy Agent is a small software package installed on your EC2 instances that allows AWS CodeDeploy to communicate with and manage deployments on those servers. The agent listens for deployment instructions from CodeDeploy, downloads the application package, runs deployment scripts (like install, start, and stop), and reports the status back to AWS. Setting up the CodeDeploy Agent ensures your EC2 instance can receive and execute automated deployment tasks, making the process seamless, reliable, and fully managed.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codedeploy-updated_val-20)

---

## Success!

answer:  
A CodeDeploy deployment is the event that represents a single update or release of your application, with its own unique ID and deployment history. When you create a deployment, you specify:

- **Which version of the application to deploy** (the revision or artifact)
- **Where to deploy it** (the target deployment group)
- **How to deploy it** (using the deployment configuration settings)

CodeDeploy then orchestrates the process by stopping services, copying new files, running your deployment scripts, starting services again, and tracking the deployment status—success or failure. You can monitor this process in real-time and review detailed logs for each step.

**The difference:**  
A deployment group is a persistent set of deployment targets and settings for your application (like a folder of configuration and servers).  
A deployment is a specific instance of deploying a particular version of your app to those targets, with its own progress, status, and history.

I had to configure a revision location, which means in AWS CodeDeploy is the place where your application's deployment package (artifact) is stored and from where CodeDeploy retrieves it during a deployment. I need to configure a revision location so CodeDeploy knows exactly where to find the files to deploy.

In my case, the revision location was your S3 bucket, which stores your app’s artifact file (for example, the compiled WAR file and any deployment scripts). When I start a deployment, CodeDeploy downloads my application package from this S3 bucket and then deploys it to your target EC2 instance(s) according to your configuration.

To check that the deployment was a success, I visited the Public DNS of my target EC2 instance using HTTP. When I saw my web application fully accessible in the browser, I knew CodeDeploy had successfully deployed my app. This confirmed that all files were copied, services started correctly, and the deployment process worked as expected.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codedeploy-updated_val-27)

---

## Disaster Recovery

The intentional error I introduced was changing the command from `sudo systemctl stop httpd.service` to `sudo systemctll stop httpd.service` (note the misspelled `systemctll` instead of `systemctl`). I also added `set -e` at the top of the script, which makes the script exit immediately if any command fails, and included an explicit `exit 1` to ensure the script exits with a non-zero status.

This will cause the deployment to fail because the script will try to run a command that doesn’t exist (`systemctll`), which results in an error. Since `set -e` is enabled, the script will stop right there and return a failure status to CodeDeploy. CodeDeploy detects the error and marks the deployment as failed, helping us test how deployment errors are handled and how to troubleshoot failed deployments.

I also enabled rollbacks with this deployment, which means that if something goes wrong during the deployment—such as a failed script, missing file, or application error—CodeDeploy will automatically revert my environment back to the previous stable version. This helps prevent downtime, protects users from broken releases, and makes it easier to recover quickly from deployment issues. Rollbacks provide an extra layer of safety and reliability for production deployments.

When my deployment failed, the automatic rollback also failed because CodeDeploy’s rollback only reuses the previous deployment’s settings, not the actual build artifact. It still deploys the latest (broken) artifact from S3, so the error persists. True artifact-based rollbacks aren’t supported in basic CodeDeploy setups—this is a common misconception. With AWS CodePipeline, you can automate rollbacks to specific, previously successful artifacts. To actually recover, you must fix the broken script, rebuild, and redeploy. In production, set up your pipeline to retain previous artifacts and reference them for rollbacks to ensure rapid recovery and minimal downtime.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codedeploy-updated_rollback-validation-upload)

---

---
