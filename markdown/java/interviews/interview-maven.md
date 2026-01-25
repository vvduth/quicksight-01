## How Maven Works

**Maven** is a build automation and dependency management tool for Java projects. It follows a **convention-over-configuration** approach, meaning it has standard directory structures (like `src/main/java` for source code, `src/test/java` for tests) and predefined build lifecycles.

Maven works through three main phases:
1. **Reading the POM** - Maven reads the pom.xml to understand project configuration
2. **Dependency Resolution** - Downloads required libraries from repositories (Maven Central by default)
3. **Build Lifecycle Execution** - Runs phases like compile, test, package, install, and deploy

## Role of pom.xml

The **pom.xml** (Project Object Model) is the heart of a Maven project. It serves as:

- **Project descriptor** - Contains metadata like groupId, artifactId, version, and packaging type
- **Dependency manager** - Lists all direct dependencies your project needs
- **Build configuration** - Defines plugins, goals, and build settings
- **Repository configuration** - Specifies where to download dependencies from

Basic structure:
```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.telusko</groupId>
    <artifactId>my-service</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <dependencies>
        <!-- Dependencies go here -->
    </dependencies>
</project>
```

## Dependency Resolution

Maven resolves dependencies in this order:

1. **Checks local repository** (`~/.m2/repository`) first
2. **Downloads from remote repositories** (Maven Central) if not found locally
3. **Resolves transitive dependencies** automatically - dependencies of your dependencies
4. **Applies dependency mediation** - if multiple versions exist, Maven picks the nearest one in the dependency tree

## Adding a Third-Party Library

Let's say you want to add **Apache Commons Lang** to your project:

1. **Find the dependency** on Maven Central (search.maven.org)
2. **Add to pom.xml** inside the `<dependencies>` section:

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.12.0</version>
    </dependency>
</dependencies>
```

3. **Run Maven command** or let your IDE auto-import:
```bash
mvn clean install
```

Maven will then download the library and all its transitive dependencies to your local repository and make them available on your project's classpath.