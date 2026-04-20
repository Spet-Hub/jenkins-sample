
# Pure Java Static App

This repository is now organized as a plain Java 17 application with no Maven or Spring dependency.

## Project layout

* `src/main/java/com/example/jenkinssample/`

  Java source code for the HTTP server and static resource handler.

* `src/main/resources/static/`

  Static HTML, CSS, and image assets served by the app.

* `run.ps1`

  PowerShell helper that compiles the project into `out/`, copies resources, and starts the server.

* `run.sh`

  Shell helper for Linux or macOS environments that performs the same compile and run steps.

* `Dockerfile`

  Multi-stage image build for the Java version of the app.

## Run locally

PowerShell:

    .\run.ps1

Shell:

    sh ./run.sh

Manual compile and run:

    javac -d out (Get-ChildItem -Path .\src\main\java -Recurse -Filter *.java | ForEach-Object FullName)
    Copy-Item -Path .\src\main\resources\* -Destination .\out -Recurse -Force
    java -cp out com.example.jenkinssample.App

The application listens on the `PORT` environment variable when present, otherwise it uses `18888`.

