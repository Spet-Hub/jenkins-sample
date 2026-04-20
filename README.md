# Jenkins Sample

This repository is a pure Java 17 static-site sample application. It uses the JDK built-in `HttpServer` to serve files and does not depend on Maven, Gradle, or Spring Boot.

The repository started as a Go/Bluemix sample and was later used to demonstrate Jenkins, Docker, and Kubernetes delivery flows. The application code is now organized as a plain Java version, while some historical deployment scripts and Kubernetes templates are still kept for CI/CD practice.

## Features

- Pure Java 17 implementation
- Static assets served from `src/main/resources/static/`
- Default port `18888`
- Port override through the `PORT` environment variable
- Startup scripts for both PowerShell and POSIX shell
- Multi-stage Docker build

## Project Layout

```text
.
|-- src
|   `-- main
|       |-- java/com/example/jenkinssample
|       |   |-- App.java
|       |   `-- ClasspathStaticFileHandler.java
|       `-- resources/static
|           |-- index.html
|           |-- images/
|           `-- stylesheets/
|-- run.ps1
|-- run.sh
|-- Dockerfile
|-- Procfile
|-- k8s*.yaml
`-- *.sh
```

## Key Files

- `src/main/java/com/example/jenkinssample/App.java`
  Application entry point. Reads the port and starts the HTTP server.
- `src/main/java/com/example/jenkinssample/ClasspathStaticFileHandler.java`
  Serves static files from the classpath and handles simple request validation.
- `src/main/resources/static/`
  HTML, CSS, and image assets.
- `run.ps1`
  Builds the project into `out/` and starts the app on Windows PowerShell.
- `run.sh`
  Builds the project into `out/` and starts the app on Linux or macOS shells.
- `Dockerfile`
  Builds and runs the Java version of the app inside a container.

## Requirements

- JDK 17 or newer

Check your environment:

```powershell
java -version
javac -version
```

## Run Locally

### PowerShell

```powershell
.\run.ps1
```

### Linux or macOS

```sh
sh ./run.sh
```

Then open:

```text
http://localhost:18888/
```

## Manual Build And Run

If you prefer not to use the helper scripts:

```powershell
$outDir = "out"
if (Test-Path $outDir) { Remove-Item -LiteralPath $outDir -Recurse -Force }
New-Item -ItemType Directory -Force -Path $outDir | Out-Null
$javaFiles = Get-ChildItem -Path .\src\main\java -Recurse -Filter *.java | ForEach-Object FullName
javac -d $outDir $javaFiles
Copy-Item -Path .\src\main\resources\* -Destination .\out -Recurse -Force
java -cp out com.example.jenkinssample.App
```

## Port Configuration

The default port is `18888`. Override it with `PORT`.

### PowerShell

```powershell
$env:PORT = "8080"
.\run.ps1
```

### Shell

```sh
PORT=8080 sh ./run.sh
```

## Docker

Build the image:

```sh
docker build -t jenkins-sample-java .
```

Run the container:

```sh
docker run --rm -p 18888:18888 jenkins-sample-java
```

Run on a custom port:

```sh
docker run --rm -e PORT=8080 -p 8080:8080 jenkins-sample-java
```

## Jenkins Pipeline

The repository now includes a sample `Jenkinsfile` for a straightforward CI/CD flow:

1. Check out the repository
2. Resolve the image name and tag
3. Compile the Java app into `out/`
4. Smoke test the local HTTP server
5. Build a Docker image
6. Optionally push the image
7. Optionally deploy to Kubernetes

Important assumptions for the Jenkins agent:

- JDK 17 is installed
- Docker is available
- `curl` is available for the smoke test
- `kubectl` is available when deployment is enabled

The pipeline exposes these parameters:

- `DEPLOY_ENV`: `none`, `dev`, `qa`, or `prod`
- `IMAGE_REPOSITORY`: Docker repository to tag
- `IMAGE_TAG`: optional explicit image tag
- `BRANCH_NAME_OVERRIDE`: optional deployment metadata override
- `PUSH_IMAGE`: whether to push the image after build

## Kubernetes And Jenkins Files

The main Kubernetes files now match the current Java app, but they are still example deployment templates rather than production-ready manifests.

Keep in mind:

- The primary manifests are now `k8s.yaml`, `k8s-dev.yaml`, `k8s-qa.yaml`, and `k8s-prod.yaml`
- The placeholder values `__IMAGE__` and `__BRANCH__` are replaced by `k8s-deploy.sh` or by the Jenkins pipeline
- The environment namespaces used by the refreshed manifests are `development`, `qatest`, and `production`
- The `*-harbor.yaml` and `*-xuegod.yaml` files are older variants kept for reference
- If you want to use these files for a real deployment, adjust image registry, namespaces, rollout strategy, and environment-specific values first

Deploy manually with the helper script:

```sh
./k8s-deploy.sh dev your-dockerhub-user/jenkins-sample-java:123 main
./k8s-deploy.sh qa your-dockerhub-user/jenkins-sample-java:123 release
./k8s-deploy.sh prod your-dockerhub-user/jenkins-sample-java:123 main
```

Rollback a deployment:

```sh
./k8s-rollback.sh prod
```

## Suggested Next Steps

- Add a `pom.xml` and convert the project to a standard Maven layout with managed builds
- Add logging, health checks, and stricter static resource handling
- Simplify the Kubernetes manifests into one final deployment set and remove the older variants
- Add image registry credentials handling to the Jenkins pipeline

## Current Use Cases

This repository is currently a good fit for:

- a minimal Java static-site example
- a Docker packaging demo
- a Jenkins or Kubernetes practice repository
