#!/bin/bash

# FeatherJet Server Startup Script for Linux/macOS
# Light as a feather, fast as a jet!

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FEATHERJET_HOME="$(dirname "$SCRIPT_DIR")"

# Set default JVM options
DEFAULT_JVM_OPTS="-Xms256m -Xmx1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Use environment variable if set, otherwise use default
JVM_OPTS="${FEATHERJET_JVM_OPTS:-$DEFAULT_JVM_OPTS}"

# JAR file location
JAR_FILE="$FEATHERJET_HOME/featherjet-server.jar"

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1-2)
if [[ $(echo "$JAVA_VERSION >= 17" | bc -l) -eq 0 ]]; then
    echo "Warning: Java 17 or higher is recommended. Current version: $JAVA_VERSION"
fi

# Check if JAR file exists
if [ ! -f "$JAR_FILE" ]; then
    echo "Error: featherjet-server.jar not found at $JAR_FILE"
    exit 1
fi

# Print banner
echo "🚀 Starting FeatherJet Server..."
echo "   Home: $FEATHERJET_HOME"
echo "   JVM Options: $JVM_OPTS"
echo "   Arguments: $@"
echo ""

# Change to FeatherJet home directory
cd "$FEATHERJET_HOME"

# Start the server
exec java $JVM_OPTS -jar "$JAR_FILE" "$@"
