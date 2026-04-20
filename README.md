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

## Kubernetes And Jenkins Files

Files such as `k8s.yaml`, `k8s-dev.yaml`, `k8s-prod.yaml`, and the related shell scripts are historical pipeline templates. They still show the intended deployment shape, but they are not production-ready as-is.

Keep in mind:

- Image names and namespace values are still placeholders or old demo values
- Tokens such as `<BUILD_TAG>` and `<BRANCH_NAME>` are expected to be replaced by a pipeline
- If you want to use these files for a real deployment, adjust image registry, namespaces, rollout strategy, and environment-specific values first

## Suggested Next Steps

- Add a `pom.xml` and convert the project to a standard Maven layout with managed builds
- Add logging, health checks, and stricter static resource handling
- Simplify the Kubernetes manifests into one coherent deployment set
- Add a `Jenkinsfile` for a complete CI/CD example

## Current Use Cases

This repository is currently a good fit for:

- a minimal Java static-site example
- a Docker packaging demo
- a Jenkins or Kubernetes practice repository
