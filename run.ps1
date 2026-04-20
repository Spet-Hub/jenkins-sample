$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$outDir = Join-Path $projectRoot "out"
$sourceRoot = Join-Path $projectRoot "src\\main\\java"
$resourceRoot = Join-Path $projectRoot "src\\main\\resources"

if (Test-Path $outDir) {
    Remove-Item -LiteralPath $outDir -Recurse -Force
}

New-Item -ItemType Directory -Force -Path $outDir | Out-Null

$javaFiles = Get-ChildItem -Path $sourceRoot -Recurse -Filter *.java | ForEach-Object FullName
if (-not $javaFiles) {
    throw "No Java source files found under $sourceRoot"
}

javac -d $outDir $javaFiles

if ($LASTEXITCODE -ne 0) {
    throw "javac failed"
}

Copy-Item -Path (Join-Path $resourceRoot "*") -Destination $outDir -Recurse -Force
java -cp $outDir com.example.jenkinssample.App
