# Extending CI to CD: Artifact Management and Deployment

## Overview
In the previous sections, we successfully built the software, ran tests, and analyzed code quality. Now, we will extend our pipeline to:
1.  **Upload Artifacts:** Store the built WAR file in a Nexus repository.
2.  **Containerize:** Build a Docker image and push it to Amazon ECR.
3.  **Deploy:** Automatically deploy the new image to Amazon ECS.
4.  **Notify:** Send build status notifications to Slack.

---

## Part 1: Artifact Management with Nexus

### What is Nexus?
Nexus is a repository manager that allows you to store and retrieve build artifacts. It acts as a central hub for managing your software packages (WAR, JAR, npm, Docker, etc.).

### Setup Prerequisites
Ensure your **Jenkins**, **SonarQube**, and **Nexus** EC2 instances are up and running.

### Step 1: Configure Nexus Repository
1.  Access Nexus in your browser: `http://<nexus-server-ip>:8081`
    <img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/jenkins-cicd-5/image.png" alt="nexus on browser" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />
2.  Log in as admin.
3.  Go to **Server Administration** (gear icon) -> **Repositories**.
4.  Click **Create repository** -> Select **maven2 (hosted)**.
    <img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/jenkins-cicd-5/image-1.png" alt="create repo" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />
5.  Name it `vprofile-repo` and create it.

### Step 2: Configure Jenkins Credentials
1.  Go to **Jenkins Dashboard** -> **Manage Jenkins** -> **Manage Credentials**.
2.  Add a new **Username with password** credential.
    *   **ID:** `nexuslogin`
    *   **Username:** `admin`
    *   **Password:** `<your-nexus-password>`
    <img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/jenkins-cicd-5/image-2.png" alt="jenkisn credential page" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Step 3: Update Jenkinsfile for Nexus Upload
Add the `Upload artifact` stage to your `Jenkinsfile`. This uses the `nexusArtifactUploader` step (note the capital 'A').

```groovy
    stage("Upload artifact") {
        steps {
            nexusArtifactUploader(
                nexusVersion: 'nexus3',
                protocol: 'http',
                nexusUrl: '172.31.8.85:8081', // Use Private IP of Nexus
                groupId: 'QA',
                version: "${env.BUILD_ID}-${env.BUILD_TIMESTAMP}",
                repository: 'vprofile-repo',
                credentialsId: 'nexuslogin',
                artifacts: [
                    [artifactId: 'vproapp', 
                    classifier: '', 
                    file: 'target/vprofile-v2.war', 
                    type: 'war']
                ]
            )
        }
    }
```

### Troubleshooting: Workspace Cleanup
If you encounter issues with disk space or stale files:
1.  Go to **Manage Jenkins** -> **Nodes**.
2.  Click on the node -> **Script Console**.
3.  Run `println "rm -rf *".execute([], new File("/var/lib/jenkins/workspace/")).text` (Use with caution!).
    *   Alternatively, SSH into the server and run `rm -rf *` inside the workspace directory.

### Verification
Run the build. If successful, check your Nexus repository to confirm the artifact was uploaded.

<img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/jenkins-cicd-5/image-5.png" alt="nexus broswer repo" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Part 2: Slack Notifications

We want to be notified automatically when a build passes or fails.

1.  **Install Plugin:** Install the "Slack Notification" plugin in Jenkins.
2.  **Configure Slack:**
    *   Create a Slack Workspace (e.g., `ducthaidevops`).
    *   Add the **Jenkins CI** app to your Slack channel (`#devopscicd`).
    *   Get the **Integration Token**.
3.  **Configure Jenkins:**
    *   Add the token as a **Secret text** credential in Jenkins (ID: `slack-token`).
    *   Go to **Manage Jenkins** -> **System** -> **Slack**.
    *   Enter Workspace: `ducthaidevops` and Credential: `slack-token`.
4.  **Update Jenkinsfile:** Add a `post` block to send notifications.

```groovy
def COLOR_MAP = [
    SUCCESS: 'good',
    UNSTABLE: 'yellow',
    FAILURE: 'danger'
]

pipeline {
    // ... existing pipeline ...
    
    post {
        always {
            echo 'Slack notification would be sent here.'
            slackSend channel: '#devopscicd', 
                      color: COLOR_MAP[currentBuild.currentResult],
                      message: "*${currentBuild.currentResult}:* Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
        }
    }
}
```

<img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/jenkins-cicd-5/image-7.png" alt="slack notification" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Part 3: Docker & ECR Integration

Instead of just storing the WAR file, we will containerize the application and push the image to Amazon Elastic Container Registry (ECR).

### Prerequisites
1.  **AWS IAM User:** Create a user with `AmazonEC2ContainerRegistryFullAccess` and `AmazonECS_FullAccess`. Create Access Keys.
2.  **Jenkins Credentials:** Add these keys to Jenkins as **AWS Credentials** (ID: `awscreds`).
3.  **Install Tools on Jenkins Server:**
    ```bash
    sudo apt-get update
    sudo snap install aws-cli --classic
    sudo apt-get install docker.io -y
    sudo usermod -aG docker jenkins
    sudo systemctl restart jenkins
    ```
4.  **Plugins:** Install **Docker Pipeline**, **Amazon ECR**, and **AWS Steps** plugins in Jenkins.
5.  **Create ECR Repo:** Create a repository named `vprofileappimage` in AWS ECR.
    <img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/jenkins-cicd-5/image-8.png" alt="amazon ecr create" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Update Jenkinsfile for Docker Build & Push
Replace the "Upload artifact" stage with Docker stages.

```groovy
    environment {
        registryCredentials = 'ecr:eu-north-1:awscreds'
        imageName = '841162690953.dkr.ecr.eu-north-1.amazonaws.com/vprofileappimage'
        vprofileRegistry = 'https://841162690953.dkr.ecr.eu-north-1.amazonaws.com'
    }

    stages {
        // ... previous stages ...

        stage("Build Docker Image") {
            steps {
                script {
                    // Build image using the Dockerfile in the repo
                    dockerImage = docker.build(imageName + ":$BUILD_NUMBER", "./Docker-files/app/multistage/")
                }
            }
        }

        stage("Upload app image") {
            steps {
                script {
                    docker.withRegistry(vprofileRegistry, registryCredentials) {
                        dockerImage.push("$BUILD_NUMBER")
                        dockerImage.push("latest")
                    }
                }
            }
        }
    }
```

Run the build. You should see the image in your ECR repository.

<img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/jenkins-cicd-5/image-11.png" alt="docker ecr" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

---

## Part 4: Continuous Deployment to AWS ECS

Now that our image is in ECR, we will deploy it to Amazon ECS (Elastic Container Service).

### ECS Setup
1.  **Create Cluster:** Create a generic ECS cluster (Fargate or EC2).
    <img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/jenkins-cicd-5/image-12.png" alt="ecs" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />
2.  **Task Definition:**
    *   Create a Task Definition.
    *   **Image URI:** Use the ECR URI (e.g., `.../vprofileappimage:latest`).
    *   **Port Mapping:** 8080.
    *   **Role:** Ensure the Task Execution Role has permissions to pull from ECR and write to CloudWatch Logs.
    <img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/jenkins-cicd-5/image-13.png" alt="task definition" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />
3.  **Create Service:**
    *   Create a Service from the Task Definition.
    *   **Load Balancer:** Attach an Application Load Balancer (ALB).
    *   **Security Group:** Allow Port 80 (HTTP) from anywhere.
    <img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/jenkins-cicd-5/image-15.png" alt="ecs service" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Update Jenkinsfile for Deployment
Add the deployment stage. This command forces ECS to update the service with the latest image.

```groovy
    environment {
        // ... existing env vars ...
        cluster = "vprofile"
        service = "vprofileappsvc"
    }

    stages {
        // ... previous stages ...

        stage("Deploy to ECS") {
            steps {
                withAWS(credentials: 'awscreds', region: 'eu-north-1') {
                    sh """
                        aws ecs update-service --cluster ${cluster} --service ${service} --force-new-deployment
                    """
                }
            }
        }
    }
```

### Final Workflow
1.  Developer pushes code to GitHub.
2.  Jenkins fetches code, builds, tests, and analyzes it.
3.  Jenkins builds a Docker image and pushes it to ECR.
4.  Jenkins triggers ECS to update the service.
5.  ECS pulls the new image and replaces the old containers.
6.  Slack notifies the team of success/failure.

<img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/jenkins-cicd-5/image-18.png" alt="build rsuilt" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />
