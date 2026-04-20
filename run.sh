#!/bin/sh
set -eu

PROJECT_ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
OUT_DIR="$PROJECT_ROOT/out"
SOURCE_ROOT="$PROJECT_ROOT/src/main/java"
RESOURCE_ROOT="$PROJECT_ROOT/src/main/resources"

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

javac -d "$OUT_DIR" $(find "$SOURCE_ROOT" -name "*.java")
cp -R "$RESOURCE_ROOT"/. "$OUT_DIR"/

exec java -cp "$OUT_DIR" com.example.jenkinssample.App
