# Jenkins Build Jobs and Artifact Management

## Table of Contents
1. [Understanding Build Tools and Dependencies](#understanding-build-tools-and-dependencies)
2. [Configuring Jenkins Tools](#configuring-jenkins-tools)
3. [Creating Your First Job](#creating-your-first-job)
4. [Creating a Build Job](#creating-a-build-job)
5. [Versioning Artifacts](#versioning-artifacts)
6. [Parameterized Builds](#parameterized-builds)

---

## Understanding Build Tools and Dependencies

### What Tools Does Your Project Need?

Before building a project, identify what tools are required. Let's use the [vprofile-project](https://github.com/vvduth/vprofile-project) as an example:

**Required tools:**
- Git (version control)
- Maven (build tool)
- Java JDK (runtime)

### Plugin vs System Installation

Remember this important concept:
- **Jenkins plugins** provide the frontend interface
- **System-level tools** must be installed on the machine itself

When you need a build tool in Jenkins, you need both:
1. A Jenkins plugin (frontend interface)
2. The actual tool installed on the system (backend execution)

---

## Configuring Jenkins Tools

### View Installed Plugins and Tools

1. Navigate to **Manage Jenkins** → **Tools**
2. This shows all configured tools and plugins

### Add Maven Installation
<img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/image-4.png" alt="Jenkins Tools" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />
1. Go to **Manage Jenkins** → **Tools**
2. Find the Maven section
3. Click **Add Maven**
4. Configure Maven version (e.g., 3.9.9)
5. Jenkins can download and install automatically

### Add JDK Installation

For Java, you have two options:

**Option 1: Manual Installation (Recommended)**
1. Install JDK on the system directly:
   ```bash
   sudo apt install openjdk-17-jdk -y
   ```

2. Find the `JAVA_HOME` path:
   ```bash
   ls /usr/lib/jvm
   ```

   Example output:
   ```
   java-1.17.0-openjdk-amd64
   java-1.21.0-openjdk-amd64
   java-17-openjdk-amd64
   java-21-openjdk-amd64
   openjdk-17
   openjdk-21
   ```

   Use path: `/usr/lib/jvm/java-17-openjdk-amd64`

3. Add in Jenkins:
   - Go to **Manage Jenkins** → **Tools** → **JDK Installations**
   - Add the JAVA_HOME path manually

**Option 2: Automatic Installation**
- Jenkins can download and install JDK automatically (more complex setup)

---

## Creating Your First Job

### Step 1: Create a New Freestyle Job

1. Click **New Item** on Jenkins dashboard
2. Enter job name: "FirstJob"
3. Select **Freestyle project**
4. Click **OK**
<img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/image-5.png" alt="Create New Job" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

### Step 2: Configure Basic Settings

1. **Description:** Add a meaningful description
   ```
   Build vprofile project from GitHub repo
   ```

2. **Source Code Management:** Git
   - Repository URL: `https://github.com/your-repo/your-project.git`
   - Branch: `*/main` (or your default branch)
   - If the repo is public, no credentials needed

3. **Build Triggers:** Leave empty for now (manual builds)

4. **Build Step:** Execute Shell
   - Add commands to test:
   ```bash
   whoami
   pwd
   w
   id
   ```

### Step 3: Build and View Output

1. Click **Save**
2. Click **Build Now** multiple times
3. Click on a build to see the console output

<img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/image-6.png" alt="Build Console Output" style="max-width: 100%; height: auto; display: block; margin: 0 auto;" />

**Example Output:**
```
Started by user Duc Thai
Running as SYSTEM
Building in workspace /var/lib/jenkins/workspace/FirstJob
[FirstJob] $ /bin/sh -xe /tmp/jenkins8795589297149698121.sh
+ whoami
jenkins
+ pwd
/var/lib/jenkins/workspace/FirstJob
+ w
 12:48:22 up 47 min,  1 user,  load average: 0.03, 0.02, 0.00
USER     TTY      FROM             LOGIN@   IDLE   JCPU   PCPU  WHAT
ubuntu            80.220.152.39    12:01   44:00   0.00s  0.02s sshd: ubuntu [priv]
+ id
uid=111(jenkins) gid=113(jenkins) groups=113(jenkins)
Finished: SUCCESS
```

### Step 4: Add Another Build Step

1. Go back to **Configure**
2. Add another build step: **Execute Shell**
3. Add command:
   ```bash
   cat /proc/cpuinfo > cpuinfo.txt
   ```

4. Build again
5. Check the workspace - you should see `cpuinfo.txt` in `/var/lib/jenkins/workspace/FirstJob/`

**Note:** Jenkins runs as the `jenkins` user. Some commands may fail without proper permissions. For example:
```bash
sudo apt update  # This will fail - jenkins user cannot use sudo
```

---

## Creating a Build Job

### Project Overview

We'll create a job that:
1. Fetches source code from GitHub
2. Builds the project using Maven
3. Archives the build artifact (WAR file)

### Configuration Steps

**Step 1: Create the Job**
1. New Item → Freestyle project
2. Name: "Vprofile Build"

**Step 2: Configure Source Code Management**
- Repository URL: `https://github.com/vvduth/vprofile-project.git`
- Branch: `*/main` (or your target branch)
- No credentials needed for public repos

**Step 3: Configure Build Step**
1. Add build step: **Invoke top-level Maven targets**
2. Select Maven version: Maven 3.9.9 (or configured version)
3. Goals and options: `install`

**Step 4: Archive Artifacts**
1. Post-build action: **Archive the artifacts**
2. Files to archive: `**/*.war`

### Build and Verify

1. Click **Build Now**
2. View the build output
3. Check the workspace for the generated WAR file

---

## Versioning Artifacts

### Problem: Overwriting Artifacts

When you build multiple times, the artifact gets overwritten. You lose previous builds.

### Solution: Create Versioned Copies

**Step 1: Create New Job**
- Name: "Vprofile Build with Versioning"
- Copy configuration from "Vprofile Build"

**Step 2: Add Versioning Step**
1. After Maven build step, add: **Execute Shell**
2. Add this command:
   ```bash
   mkdir -p versions
   cp target/vprofile-v2.war versions/vpro$BUILD_ID.war
   ```

3. `$BUILD_ID` is a Jenkins variable that increments with each build

**Step 3: Build Multiple Times**
1. Click **Build Now** several times
2. In the workspace, check the `versions/` folder
3. You'll see files named:
   - `vpro1.war`
   - `vpro2.war`
   - `vpro3.war`
   - etc.

Each build creates a new versioned file!

---

## Parameterized Builds

### Problem: Hardcoded Values

In the previous approach, version names were fixed. What if you want to specify the version when building?

### Solution: Parameterized Builds

**Step 1: Create New Job**
- Name: "Vprofile Build with Parameters"
- Copy configuration from "Vprofile Build"

**Step 2: Enable Build Parameters**
1. Check: **This project is parameterized**
2. Click **Add Parameter** → **String Parameter**
3. Configure:
   - **Name:** `VERSION`
   - **Default value:** `v1.0.0`
   - **Description:** `Version number for the build artifact`

**Step 3: Use Parameter in Build**
1. After Maven build step, add: **Execute Shell**
2. Add this command:
   ```bash
   mkdir -p versions
   cp target/vprofile-v2.war versions/vprofile-$VERSION.war
   ```

3. The `$VERSION` variable will be replaced with the parameter value

**Step 4: Build with Parameters**
1. Click **Build with Parameters** (instead of "Build Now")
2. Specify the version: `v2.0.0`
3. Click **Build**
4. The artifact will be named: `vprofile-v2.0.0.war`

**Building Multiple Times:**
- Build 1: `vprofile-v1.0.0.war`
- Build 2: `vprofile-v2.0.0.war`
- Build 3: `vprofile-v3.0.0.war`

---

## Summary

### Key Concepts

1. **Jenkins needs both plugins and system tools** for building
2. **Freestyle jobs** are simple and graphical
3. **Build steps** execute commands in sequence
4. **Artifacts** can be archived for later use
5. **Versioning** helps track multiple builds
6. **Parameterized builds** allow flexibility during build time

### Next Steps

- Explore Pipeline as Code for more complex workflows
- Integrate with version control webhooks for automatic builds
- Add testing and deployment stages
- Connect multiple jobs into a full CI/CD pipeline
