#!/usr/bin/env bash

set -euo pipefail

GRADLE_WRAPPER_DIR="$(cd "$(dirname "$0")" && pwd)/gradle/wrapper"
GRADLE_WRAPPER_JAR="$GRADLE_WRAPPER_DIR/gradle-wrapper.jar"

if [ ! -f "$GRADLE_WRAPPER_JAR" ]; then
  echo "Missing gradle-wrapper.jar. Run 'gradle wrapper' to generate it." >&2
  exit 1
fi

JAVA_EXEC="${JAVA_HOME:-}/bin/java"
if [ ! -x "$JAVA_EXEC" ]; then
  JAVA_EXEC="java"
fi

exec "$JAVA_EXEC" -jar "$GRADLE_WRAPPER_JAR" "$@"
