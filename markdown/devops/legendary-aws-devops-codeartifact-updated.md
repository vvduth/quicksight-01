<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Secure Packages with CodeArtifact

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-devops-codeartifact-updated)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codeartifact-updated_1d79e699)

---

## Introducing Today's Project!

In this project, I will demonstrate how to set up AWS CodeArtifact. I already have my EC2 instance running my web app code, and the code is also available in my GitHub repository. I will start by creating a CodeArtifact repository, then set up my EC2 instance’s permissions so it can access the CodeArtifact repository. After that, I will see how the CodeArtifact repository can store my web app’s packages.

I'm doing this project to learn how to manage package dependencies and distribution using AWS CodeArtifact, as well as to understand how cloud-based package repositories work and how they integrate with my development workflow. This knowledge will help me automate deployments, improve security, and streamline package management for future projects.

### Key tools and concepts

**Services I used were:**
- **AWS CodeArtifact:** For securely storing and managing software packages and dependencies.
- **AWS IAM (Identity and Access Management):** For creating policies and roles to control access to CodeArtifact.
- **AWS EC2:** As the compute environment for running Maven and interacting with CodeArtifact.
- **AWS CLI:** To interact with AWS services and manage artifacts from the terminal.
- **Maven:** As the build tool to manage dependencies and compile Java projects.

**Key concepts I learnt include:**
- **Artifact Repository:** Understanding the role of a repository like CodeArtifact in managing and caching dependencies for secure, reliable, and consistent builds.
- **IAM Roles and Policies:** How to create, attach, and use IAM roles and policies to securely manage access to AWS services.
- **Security Best Practices:** The importance of using IAM roles over static credentials for better security and control.


### Project reflection

This project took me approximately a few hours to complete. The most challenging part was configuring IAM roles and policies correctly to ensure my EC2 instance could securely access CodeArtifact without running into permission or credential errors. It was most rewarding to see my custom package successfully published, stored, and retrieved from CodeArtifact, confirming that the entire workflow—from secure access setup to package lifecycle management—was working seamlessly.

This project is part three of a series of DevOps projects where I'm building a CI/CD pipeline! I'll be working on the next project on monday 20 Octorber 2025.

---

## CodeArtifact Repository

CodeArtifact is a secure, central place to store all your software packages. When building an application, you often use many external packages or libraries—components created by other developers that you don't want to build from scratch. Engineering teams use artifact repositories like CodeArtifact because they provide:

1️⃣ **Security:** Everyone on the team retrieves packages from a secure repository (CodeArtifact), instead of downloading from potentially unsafe sources on the internet. This helps reduce security risks.

2️⃣ **Reliability:** If public package websites go down, you have backups in your CodeArtifact repository, ensuring your builds can continue without interruption.

3️⃣ **Control:** Your team can easily share and use the same versions of packages, instead of everyone working with different versions. This promotes consistency and reduces issues caused by mismatched dependencies.

Using CodeArtifact helps streamline development, improve security, and ensure reliability

A domain in CodeArtifact is a logical grouping that helps you manage multiple repositories under a single organizational unit. It acts like a central hub for your repositories, making it easier to control access, share packages, and manage settings across different teams or projects.

My domain is linked to my current AWS IAM account, and its name is **nextwork**. The domain’s URL is `nextwork-841162690953.d.codeartifact.eu-north-1.amazonaws.com`. By using a domain, I can organize and manage all my repositories in one place, set common policies, and simplify package sharing between projects.

A CodeArtifact repository can have an upstream repository, which means it can pull packages from another repository when you request a dependency that isn’t already stored locally. This setup allows your repository to automatically fetch packages from a central or public source, ensuring you always have access to the latest libraries and updates.

My repository’s upstream repository is **maven-central-store**, so if I request a package that isn’t available in my CodeArtifact repository, it will try to retrieve it from Maven Central through the upstream connection. This helps keep my project up to date and simplifies dependency management.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codeartifact-updated_n4o5p6q7)

---

## CodeArtifact Security

### Issue

To access CodeArtifact, we need an authorization token because CodeArtifact is a secure AWS service, and AWS requires you to prove your identity before allowing access to its resources. The authorization token acts as a temporary credential that lets tools like Maven authenticate and connect to CodeArtifact.

I ran into an error when retrieving a token because I got an "Unable to locate credentials" error. This means my EC2 instance didn’t have the necessary IAM permissions or AWS credentials set up, so it couldn’t request an authorization token from AWS. Setting up the proper IAM role or credentials solves this and allows secure access to CodeArtifact.

### Resolution

  
This resolved the error because attaching the IAM policy and role to my EC2 instance gave it the necessary permissions to request an authorization token and access the CodeArtifact repository securely. With these permissions, my instance could authenticate with AWS and interact with CodeArtifact without running into credential or access errors. This approach follows AWS best practices for secure and managed access to resources.

It's security best practice to use IAM roles because IAM roles allow you to grant temporary and controlled permissions to AWS resources without needing to store sensitive credentials like access keys on your servers. This reduces the risk of accidental exposure or misuse of credentials, makes it easier to rotate permissions, and helps enforce the principle of least privilege—ensuring that each resource only has the access it needs. Using IAM roles improves the overall security of your AWS environment and simplifies permission management.

---

## The JSON policy attached to my role

This IAM policy grants permissions for my EC2 instance (or other AWS resources) to interact with AWS CodeArtifact. Specifically, it allows:

- **codeartifact:GetAuthorizationToken**: Lets the instance request an authorization token, which is required to authenticate and access CodeArtifact repositories.
- **codeartifact:GetRepositoryEndpoint**: Allows the instance to retrieve the endpoint URL for the CodeArtifact repository, which is necessary to connect Maven or other package managers.
- **codeartifact:ReadFromRepository**: Permits the instance to read (download) packages from the CodeArtifact repository.

Additionally, the policy grants:

- **sts:GetServiceBearerToken** (with a condition limiting it to CodeArtifact): This is required for service authentication, allowing the instance to get a bearer token specifically for CodeArtifact operations.

These permissions are necessary because they enable your EC2 instance to securely authenticate, connect to, and read packages.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codeartifact-updated_23rp7q8r9)

---

## Maven and CodeArtifact

### To test the connection between Maven and CodeArtifact, I compiled my web app using settings.xml

  
The settings.xml file configures Maven to behave consistently across all my projects. In my case, I need a settings.xml file to tell Maven where to find dependencies and how to access the correct repositories, such as those hosted in CodeArtifact.

The settings.xml file contains configuration details like the URL of the CodeArtifact repository, authentication settings (such as server credentials or tokens), and any necessary proxy or mirror information. By setting this up, Maven knows exactly which repository to use for downloading dependencies and how to authenticate securely, allowing it to interact seamlessly with CodeArtifact and fetch packages for my projects.

Compiling means converting my source code into a format that the computer can execute, usually turning human-readable code into machine code or bytecode. In my case, when I compile my project with Maven, Maven first looks at my project's dependencies listed in the pom.xml file. Instead of downloading those dependencies directly from public repositories, Maven checks my CodeArtifact repository. If a dependency isn't already in CodeArtifact, Maven will fetch it from the upstream repository (Maven Central in our case), cache it in CodeArtifact, and then deliver it to my project.

This process happens for each required dependency, ensuring that my build process is secure (since dependencies come from a trusted source), controlled (with consistent versions), and faster for subsequent builds because dependencies are cached in CodeArtifact and don’t need to be downloaded again.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codeartifact-updated_c17eace8)

---

## Verify Connection

  
After compiling, I checked my CodeArtifact repository. I noticed a list of Maven packages in my CodeArtifact repository. These packages were the dependencies my web app needed, and they were automatically stored in CodeArtifact as Maven artifacts. This confirmed that the connection between Maven and CodeArtifact was working correctly, and that my repository was now securely storing and managing my project's dependencies.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codeartifact-updated_1d79e699)

---

## Uploading My Own Packages

This secret mission is about creating and publishing your own custom package to your CodeArtifact repository, then experiencing the full package lifecycle by downloading and using your own package. This process is especially useful for situations where you have proprietary, custom, or in-house code that you want to share internally within your organization, but keep private from the outside world.

Companies commonly use this approach to distribute things like shared UI components, internal tools, or specialized data processing libraries without exposing their intellectual property. By using CodeArtifact, you ensure that only authorized team members can access and use these internal packages, maintaining both security and control over your organization's private code.

To create my own package, I opened Cloud Shell and created a file called `secret-mission.txt` containing the text "Hello this is a test package." After that, I bundled the file into a package using the `tar` command to create a `.tar.gz` archive. A `.tar.gz` file bundles multiple files together and compresses them, similar to a zip file, but it also preserves file permissions and ownership information—something that's especially useful on Linux systems like my EC2 instance, where the `tar` command is built-in.

I also generated a security hash because it allows me to verify the integrity of my package. The hash acts like a digital fingerprint for my file; if the hash remains the same after transferring or storing the package, I know the contents haven't been altered or corrupted. This is especially important when sharing packages internally or deploying them to production environments, ensuring that the package hasn't been tampered with or damaged in transit.

After running the following command to publish my custom package:

```
aws codeartifact publish-package-version \
  --domain nextwork \
  --repository nextwork-devops-cicd \
  --format generic \
  --namespace secret-mission \
  --package secret-mission \
  --package-version 1.0.0 \
  --asset-content secret-mission.tar.gz \
  --asset-name secret-mission.tar.gz \
  --asset-sha256 $ASSET_SHA256
```

**When I view the package details in CodeArtifact, I can see:**
- **Publish Timestamp:** When the package was published.
- **Version Information:** The specific version (e.g., 1.0.0).
- **Origin:** The package’s origin is my CodeArtifact repository (not an upstream source).
- **Security Hashes:** CodeArtifact calculates and displays hashes for each asset (such as `secret-mission.tar.gz`), allowing verification of the package’s integrity.

  
To validate my packages, I went back to the CloudShell terminal and downloaded the package from CodeArtifact using this command:

```bash
aws codeartifact get-package-version-asset \
  --domain nextwork \
  --repository nextwork-devops-cicd \
  --format generic \
  --namespace secret-mission \
  --package secret-mission \
  --package-version 1.0.0 \
  --asset secret-mission.tar.gz \
  secret-mission.tar.gz
```

After downloading, I unzipped the `secret-mission.tar.gz` file and used `cat secret-mission.txt` to display its contents. I saw "Hellooooo this is a test package!"—which confirmed that my package was published, stored, and retrieved correctly from CodeArtifact. This process validated both the integrity and accessibility of my custom package.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codeartifact-updated_sm12-upload)

---

---
