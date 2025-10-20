<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Continuous Integration with CodeBuild

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-devops-codebuild-updated)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codebuild-updated_35588a47)

---

## Introducing Today's Project!

Today we’re building an automated CI pipeline for my web app using AWS CodeBuild. I will:

- Create and configure a CodeBuild project from scratch (compute environment, IAM role, environment variables).
- Connect the CodeBuild project to my GitHub repository so builds trigger from commits or PRs.
- Define the build process in a buildspec.yml (install dependencies, run build, run tests, produce artifacts).
- Automate testing in the pipeline so every change is validated before merging.

I’m doing this project to learn how to design and operate reliable CI/CD pipelines, including:
- How to automate builds and tests for faster feedback loops.
- How to securely configure build environments and least‑privilege IAM roles.
- How to integrate source control (GitHub) with AWS developer tooling.
- How to define reproducible build steps with buildspec.yml and produce versioned artifacts.
- How to catch regressions early and improve deployment confidence by automating tests and builds.

### Key tools and concepts

Services I used were AWS CodeBuild, CodePipeline, CodeConnections, S3, CloudWatch Logs, and CodeArtifact. Key concepts I learnt include setting up secure connections between AWS and GitHub, automating builds and deployments with CI/CD, managing build environments and permissions, storing and packaging build artifacts, automated testing in the pipeline, and monitoring build processes for reliability and troubleshooting. These are foundational skills for modern cloud-based software development and DevOps.

### Project reflection

This project took me approximately a few hours to complete, including setup, troubleshooting, and verifying each step. The most challenging part was diagnosing build failures—especially figuring out missing permissions for CodeBuild to access CodeArtifact and making sure the buildspec.yml was configured correctly. It was most rewarding to see the automated build and test pipeline working end-to-end, with artifacts successfully stored in S3 and clear logs in CloudWatch. That gave me confidence in both the process and the reliability of my CI/CD setup!

This project is part four of a series of DevOps projects where I'm building a CI/CD pipeline! I'll be working on the next project on tuesday 21 oct 2025.

---

## Setting up a CodeBuild Project

A CI (continuous integration) service automates building, testing, and packaging your code whenever changes are pushed. AWS CodeBuild is a fully managed CI service — it pulls source from your repo, runs the steps in buildspec.yml in an isolated, scalable build environment, runs tests, produces artifacts, and stores or uploads them (S3, CodeArtifact, etc.).

Engineering teams use CI services because they automate repeatable checks, catch regressions early, provide fast feedback, enforce consistent builds and policies, enable safer merges, and scale on demand — all of which speed delivery and reduce human error.

My CodeBuild project's source configuration tells CodeBuild exactly where to get the project code it should build. By selecting GitHub I pointed CodeBuild to a specific GitHub repository and branch (or PR) as the canonical source. That selection also implies:

- CodeBuild will clone that repo into the build environment (CODEBUILD_SRC_DIR).  
- You can use a buildspec.yml in the repo root (or override it in the project).  
- Authentication is done via a GitHub App or OAuth token (granting CodeBuild permission to access the repo).  
- You can enable webhooks so pushes or PR events automatically start builds (with optional event/branch filters and a webhook secret for security).  

In short: choosing GitHub makes my repo the authoritative input for automated, repeatable CI builds.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codebuild-updated_fewgrhte)

---

## Connecting CodeBuild with GitHub

I chose a GitHub App because it’s the simplest and most secure option for CI integrations. GitHub Apps are installed on repos/orgs (not tied to a user), support fine‑grained permissions, and AWS/CodeConnections handles ephemeral tokens so you don’t manage secrets or rotate personal keys. They also provide better auditability and can be scoped per repo, support webhooks natively, and are easy to revoke/install centrally.

By contrast, a personal access token (PAT) is user‑scoped, long‑lived, and requires manual rotation and secret storage. OAuth apps add complexity and often broader scopes. For automated CI/CD (CodeBuild/CodePipeline), GitHub Apps give least‑privilege, easier ops, and stronger security.

CodeConnections acted as a secure bridge between AWS and my GitHub account. I created a connection in the AWS Console (authorizing the GitHub App or OAuth flow), grabbed the connection ARN, and used that ARN in my CodeBuild project. CodeConnections handled authentication, webhooks, and token management so I didn’t have to store PATs or SSH keys in code. Benefits: least‑privilege repo access, automatic token rotation, centralized auditing and revocation, native webhook triggers for automatic builds, and seamless integration with CodeBuild/CodePipeline—simplifying secure CI integration.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codebuild-updated_a7c98e2d)

---

## CodeBuild Configurations

### Environment

My CodeBuild project's environment configuration sets up the build server for my app. I chose on-demand provisioning for cost and flexibility, Amazon Linux for compatibility, the Standard runtime with the Corretto 8 image for Java support, and created a new service role with just enough permissions for CodeBuild to access resources like S3, CodeArtifact, and CloudWatch. These choices ensure the build runs in a secure, reliable, and consistent environment tailored to my project's needs.

### Artifacts

Build artifacts are the actual outputs of the build process—what you deploy to servers or distribute to users. For my project, the main build artifact is a WAR file (Web Application Archive). It’s basically a zip file containing everything a server needs to run the web app: compiled code, libraries, configs, and static assets. I set up an S3 bucket to store these WAR files and any other build outputs. Using S3 makes artifacts reliably accessible, secure, and easy to use for later deployments within AWS, ensuring my build results are safely stored and ready for production.

### Packaging

When setting up CodeBuild, I chose to package artifacts as a zip file because zip compression reduces the overall size, making uploads to S3 faster and saving on storage costs. It keeps everything organized by bundling all build outputs into one tidy package, which is much easier to manage than dealing with separate files. Plus, deploying or sharing the app becomes much simpler—just download one zip file and you have everything needed in one place, streamlining both collaboration and deployment.

### Monitoring

For monitoring, I enabled CloudWatch Logs, which is AWS’s centralized logging service. It automatically captures and stores all build output, errors, and status updates from my CodeBuild runs. This makes it easy to track build progress in real time, troubleshoot failures, and review build history. Using CloudWatch Logs helps me quickly spot issues, keep an audit trail, and maintain visibility across my CI/CD pipeline without needing to manage log files manually.

---

## buildspec.yml

My first build failed because CodeBuild couldn’t find the buildspec.yml file in my repository, resulting in a YAML_FILE_ERROR. The buildspec.yml file is essential because it tells CodeBuild exactly what steps to run—like installing dependencies, building the app, running tests, and packaging artifacts. Without it, CodeBuild doesn’t know how to process the build, so providing a correctly named and placed buildspec.yml in the repo root is crucial for successful builds.

The first two phases in my buildspec.yml file are install and pre_build. The install phase sets up the environment by specifying the Java version (Corretto 8). The pre_build phase runs commands before the actual build, like logging into AWS CodeArtifact and exporting the authentication token so Maven can pull dependencies securely. The third phase, build, kicks off the actual build process: it logs the start time and runs mvn clean install to compile the code and run tests. The fourth phase, post_build, handles final steps after the build, such as logging completion and packaging the app into a WAR file. Each phase organizes and automates part of the build lifecycle for clarity and reliability.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codebuild-updated_35588a47)

---

## Success!

answer: My second build failed with a COMMAND_EXECUTION_ERROR because the command to get the CodeArtifact auth token couldn’t run successfully. This usually happens if the CodeBuild service role doesn’t have permission to call the CodeArtifact API. To fix this, I need to update the role attached to my CodeBuild project by adding the necessary CodeArtifact permissions (like codeartifact:GetAuthorizationToken and sts:GetServiceBearerToken). This ensures CodeBuild can authenticate and access the repository, allowing the build to proceed without errors.

To resolve the second error, I updated the CodeBuild service role to include the necessary permissions for accessing CodeArtifact, specifically codeartifact:GetAuthorizationToken and related actions. After adjusting the IAM policy, I rebuilt the project and saw the build status change to Success. This confirmed that CodeBuild could now authenticate with CodeArtifact, fetch dependencies, and complete the build process without issues.

To verify the build, I checked the S3 bucket where CodeBuild is configured to store build artifacts. I saw that the artifact file—specifically, the WAR file created by the build process—appeared in the bucket. Seeing this artifact tells me that the build ran successfully, the packaging step worked as expected, and the output is ready for deployment. It’s a clear sign that my CI pipeline is correctly set up to produce and store deployable application packages.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codebuild-updated_d9cc6191)

---

## Automating Testing

In a project extension, I added a test script that check the basic structure and presence of critical files in my Java web application. It verifies that the `src` directory exists, that the essential `index.jsp` file is present in the correct webapp location, and includes a trivial test to demonstrate the structure. 

Running this script as part of the CI/CD pipeline (e.g., in CodeBuild) means every code change is automatically validated for these requirements. This approach helps catch missing files or misconfigurations early, prevents broken builds from making it to production, and gives me confidence that the application’s basic structure is intact before more advanced tests or deployment steps run. Automated testing like this is essential for maintaining code quality, speeding up feedback, and making sure deployments are reliable as the project grows.

answer:  
To add the test script to the build process, I created a file called test.sh with my test commands, added it to my project’s source code, and committed and pushed it to my GitHub repository that’s connected to CodeBuild. Then, I updated my buildspec.yml to include a command that runs test.sh during the build phase (or a dedicated test phase). This way, every time CodeBuild runs, it automatically executes my test script as part of the CI pipeline, ensuring that tests are always performed on new code changes.

answer:  
After pushing my code to GitHub, I ran the Start Build in AWS CodeBuild again and checked the build logs. Around lines 45–50, I saw my custom test phase markers and output from my test.sh script—like "==== RUNNING SIMPLE TESTS ====", "✅ PASS: src directory exists", and "✅ PASS: index.jsp exists". This is clear proof that CodeBuild automatically ran my test commands as part of the build process. Seeing these test results in the logs confirms my automated testing setup is working!

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-codebuild-updated_sm-test-script-upload)

---

---
