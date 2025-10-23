<img src="https://cdn.prod.website-files.com/677c400686e724409a5a7409/6790ad949cf622dc8dcd9fe4_nextwork-logo-leather.svg" alt="NextWork" width="300" />

# Infrastructure as Code with CloudFormation

**Project Link:** [View Project](http://learn.nextwork.org/projects/aws-devops-cloudformation-updated)

**Author:** Duc Thai  
**Email:** ducthai060501@gmail.com

---

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-cloudformation-updated_bd8b836b)

---

## Introducing Today's Project!

In this project, I will demonstrate how to use AWS CloudFormation to turn your web app infrastructure into a single, reusable template. The steps I'll cover include:

🏎️ Generating a CloudFormation template to define all the necessary resources for my web application  
👩‍🔧 Fixing common errors in infrastructure as code templates to ensure smooth deployments  
🌟 Watching as the template automatically creates new resources in my AWS account  
💎 Manually adding new resources to the template to customize and expand my infrastructure

I'm doing this project to learn how to automate infrastructure management, reduce manual setup, and gain hands-on experience with infrastructure as code best practices using CloudFormation.

### Key tools and concepts

Services I used were AWS EC2, AWS S3, AWS CodeArtifact, AWS CodeBuild, AWS CodeDeploy, IAM, and AWS CloudFormation.

Key concepts I learnt include:
- Automating infrastructure setup using CloudFormation templates  
- Managing permissions and roles securely with IAM  
- Setting up CI/CD pipelines with CodeBuild and CodeDeploy  
- Using S3 and CodeArtifact to store build artifacts  
- Debugging deployment issues related to caching and resource dependencies  
- Handling infrastructure as code (IaC) best practices  
- Troubleshooting CloudFormation errors such as circular dependencies and resource conflicts  
- Making CloudFormation templates flexible and reusable with Parameters  
- Understanding resource referencing and dependency management in templates

### Project reflection

This project took me approximately a 3 hoursto complete.  challenging part was debugging and resolving CloudFormation dependency and configuration errors. It was most rewarding to see the stack deploy successfully after breaking cycles and correcting resource references.

This project is part six of a series of DevOps projects where I'm building a CI/CD pipeline! I'll be working on the next project on 24th October..

---

## Generating a CloudFormation Template

The IaC Generator is a tool that helps automate the creation of infrastructure-as-code templates by scanning your AWS account for existing resources. It works in a three-step process:

1. **Scan**: It scans all the resources in your AWS account to detect what you have set up.
2. **Template Creation**: You select and bundle the scanned resources into a template, defining what you want to deploy and manage together.
3. **Import**: You import this template into AWS CloudFormation to deploy all the resources in one go, making infrastructure setup, updates, and management much easier and more consistent.

A CloudFormation template is a JSON or YAML file that defines AWS resources and their configurations, allowing you to automate the setup, update, and management of your infrastructure as code. You can use it to create, modify, or delete resources in a consistent and repeatable way.

The resources I could add to my template include:
- **AWS::CodeArtifact::Repository**: Two CodeArtifact repositories for storing and sharing build artifacts.
- **AWS::S3::Bucket**: One S3 bucket for build artifacts.
- **AWS::CodeStarConnections::Connection**: One GitHub connection for source control integration.
- **AWS::CodeDeploy::Application**: One CodeDeploy application for managing deployments.
- **AWS::IAM::Role**: Three IAM roles for the development EC2 instance, CodeBuild, and CodeDeploy.
- **AWS::IAM::ManagedPolicy**: One IAM managed policy to grant CodeArtifact access.


The resources I couldn’t add to my template were my CodeBuild project and CodeDeploy deployment group, because the IaC generator doesn’t currently support exporting these resources into CloudFormation templates. This means I had to manually define them in my template or set them up separately, since not all AWS resources are supported for automatic detection and template generation by the tool.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-cloudformation-updated_0495b046)

---

## Template Testing

Before testing my template, I deleted the CI/CD resources from my AWS account that I set up earlier. I did this because CloudFormation deployment will fail if resources with the same name already exist. Removing the old resources prevents naming conflicts, ensures a smooth deployment, and keeps my account organized without duplicate resources.

The result of my first template test was a failure:  
Resource handler returned message: "The role with name codebuild-nextwork-devops-cicd-service-role cannot be found."  
This happened because AWS couldn’t find the IAM role (codebuild-nextwork-web-build-service-role) that CloudFormation was trying to use, even though I was creating it in the same CloudFormation template. The issue is that CloudFormation sometimes tries to create and attach policies to the IAM role before the role itself exists—like looking for a file before it’s been saved. This dependency timing caused the error during deployment.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-cloudformation-updated_f56730fd)

---

## DependsOn

To resolve the error, I find my  four IAM policies in the template, their name start with "IAMManagedPolicyPolicyserviceroleCodeBuildBasePolicynextworkdevopscicdeunorth1" and then I added this magical line that will tell CloudFormation to ✋ wait ✋ and create the IAM role first, before we try attach it to these polcies:  DependsOn: "IAMRoleCodebuildnextworkdevopscicdservicerole" which is my IAM role name is the cloud formatino template..
The DependsOn attribute means The **DependsOn** attribute in a CloudFormation template explicitly tells CloudFormation to wait and create one resource before creating another that depends on it. In your case, by adding `DependsOn: "IAMRoleCodebuildnextworkdevopscicdservicerole"` to my IAM policies, I instructed CloudFormation to first create the IAM role and only then attach the policies to it. This fixes errors caused by CloudFormation trying to attach policies before the role exists, ensuring the resources are created in the correct order.

The **DependsOn** line was added to four different parts of my CloudFormation template—specifically, to the resources that define the IAM managed policies. For each policy, I added the DependsOn attribute to make sure the IAM role is created before the policy is attached.

**Example locations:**
1. **CodeArtifact Access Policy:**  
   DependsOn added to the resource for the IAM managed policy granting CodeArtifact access.

2. **CodeBuild Base Policy:**  
   DependsOn added to the resource for the IAM managed policy used by CodeBuild.

3. **CodeDeploy Base Policy:**  
   DependsOn added to the resource for the IAM managed policy used by CodeDeploy.

4. **EC2 Instance Base Policy:**  
   DependsOn added to the resource for the IAM managed policy used by your development EC2 instance.

In each case, the DependsOn attribute references the IAM role that each policy is supposed to attach to, ensuring CloudFormation creates the role first before attaching the policy.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-cloudformation-updated_f0df8018)

---

## Circular Dependencies

I gave my CloudFormation template another test! But this time the new error I ran into was a "circular dependency" in my CloudFormation template. This happens when my IAM policies are set to depend on the IAM role being created first (using DependsOn), but the IAM role itself also references those same policies in its definition. CloudFormation gets stuck in a loop, since each resource is waiting for the other to exist before it can be created. It’s like a chicken-and-egg problem—CloudFormation doesn’t know which one to create first, so the deployment fails.

To fix this error, I searched for the IAM role configuration in my CloudFormation template (using Ctrl + F for "IAMRolecodebuild"). I found a section called **ManagedPolicyArns** that was referencing my IAM policies. These references were unnecessary and were causing the circular dependency error. By removing the **ManagedPolicyArns** lines that pointed to my IAM policies, I broke the dependency loop and allowed CloudFormation to create the IAM role and attach the policies in the correct order. This resolved the circular dependency issue and let my template deploy successfully.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-cloudformation-updated_e6fd85ed)

---

## Manual Additions

The two resources I manually added to my CloudFormation template were:

1. **CodeBuild Project**  
2. **CodeDeploy Deployment Group**  

When defining these resources, I referenced supporting resources using their logical IDs (the keys under `Resources:` in my template), not by their actual names. For example:
- `<YOUR_CODEBUILD_SERVICE_ROLE_ID>` → `IAMRoleCodebuildnextworkdevopscicdservicerole`
- `<YOUR_S3_BUCKET_ID>` → `S3BucketNextworkdevopscicddukem`
- `<YOUR_CODEDEPLOY_ROLE_ID>` → `IAMRoleNextWorkCodeDeployRole`
- `<YOUR_CODEDEPLOY_APPLICATION_ID>` → `CodeDeployApplicationNextworkdevopscicd`

Using logical IDs ensures CloudFormation can resolve dependencies and properly link resources during deployment.

I also had to make sure the references were consistent in this template, so I edited my CodeBuild project definition to:

- Use the logical IDs of resources (like IAM roles, S3 buckets, and CodeArtifact repositories) instead of hardcoded resource names.
- Reference the IAM role using its logical ID (e.g., IAMRoleCodebuildnextworkdevopscicdservicerole) for the service role.
- Reference the S3 bucket for build artifacts using its logical ID (e.g., S3BucketNextworkdevopscicddukem).
- Reference other dependencies, such as environment variables or source locations, with parameters or logical IDs to ensure CloudFormation correctly links resources.
- Remove any hardcoded ARNs or names, replacing them with Fn::GetAtt or Ref functions to keep the template reusable and error-free.

These edits ensure that all resource links are managed by CloudFormation and the template is portable and reliable across environments.

I also introduce Parameters in a CloudFormation template are customizable input values that you define under the `Parameters` section. They allow you to make your template reusable and flexible by letting users specify values when they launch the stack, instead of hardcoding everything.

For example, in your template:
```yaml
Parameters:
  GitHubRepoOwner:
    Type: String
    Description: GitHub repository owner
    Default: "your-github-username"
  GitHubRepo:
    Type: String
    Description: GitHub repository name
    Default: "nextwork-web-project"
```

These parameters let you set the GitHub repository owner and name dynamically. When deploying, you can provide different values for these fields, making your template adaptable for various projects or environments.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-cloudformation-updated_1cee0428)

---

## Success!

I could verify all the deployed resources by visiting the **Resources** tab in the CloudFormation console. This tab shows a complete list of all resources that were created by your CloudFormation stack, along with their status and logical IDs.

![Image](http://learn.nextwork.org/relaxed_teal_timid_avocado/uploads/aws-devops-cloudformation-updated_bd8b836b)

---

---
