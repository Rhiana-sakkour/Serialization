#!/usr/bin/env bash
# Usage: ./run.sh <MainClass> [args...]      e.g.  ./run.sh JavaSerializationLab
set -euo pipefail
cd "$(dirname "$0")"
mvn -q compile dependency:build-classpath -Dmdep.outputFile=target/cp.txt
CP="target/classes:$(cat target/cp.txt)"
exec java -Dfile.encoding=UTF-8 -cp "$CP" "$@"
