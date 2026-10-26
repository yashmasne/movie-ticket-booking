#!/bin/sh
MAVEN_HOME="$HOME/.m2/apache-maven-3.9.6"
if [ ! -d "$MAVEN_HOME" ]; then
    echo "Maven not found at $MAVEN_HOME"
    exit 1
fi
exec "$MAVEN_HOME/bin/mvn" "$@"
