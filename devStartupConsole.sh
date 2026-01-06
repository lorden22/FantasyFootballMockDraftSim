#!/bin/bash

# Fantasy Football Mock Draft Simulator - Console Development Startup
# This script builds and runs the console application with embedded database (like WebApp)

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

echo "🚀 Fantasy Football Mock Draft Simulator - Console Development Startup"
echo "======================================================================"

# Detect operating system
UNAME_OUT="$(uname -s)"
case "${UNAME_OUT}" in
    Linux*)     machine=Linux;;
    Darwin*)    machine=Mac;;
    CYGWIN*)    machine=Cygwin;;
    MINGW*)     machine=MinGw;;
    *)          machine="UNKNOWN:${UNAME_OUT}"
esac

if [ "$machine" = "Mac" ]; then
    print_status "Running on Mac"
    MAIN_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
elif [ "$machine" = "Linux" ]; then
    print_status "Running on Linux"
    MAIN_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
elif [ "$machine" = "Cygwin" ] || [ "$machine" = "MinGw" ]; then
    print_status "Running on Windows"
    MAIN_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
else
    print_error "Running on Unknown OS: ${UNAME_OUT}"
    print_error "Please run this script on a Mac, Linux, or Windows machine"
    exit 1
fi

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

print_status "Console application will run with embedded database (independent from WebApp)..."

print_status "Stopping and removing any existing containers..."
if docker ps -a --format "table {{.Names}}" | grep -q "^app-console$"; then
    docker stop app-console || true
    docker rm app-console || true
fi

print_status "Removing any existing images..."
if docker images --format "table {{.Repository}}:{{.Tag}}" | grep -q "^app-console:latest$"; then
    docker rmi app-console:latest || true
fi

print_status "Building console container with embedded database..."
docker pull openjdk:17-jdk-slim
docker build -f consolebased/Dockerfile -t app-console:latest .

print_status "Starting console application with embedded database..."
echo "====================================================================================="
echo "                          CONSOLE APPLICATION READY"
echo "====================================================================================="
echo
echo "You can now interact with the Fantasy Football Mock Draft Simulator!"
echo
echo "Test credentials:"
echo "  Username: a"
echo "  Password: a"
echo
echo "Available menu options:"
echo "  1. Start New Draft"
echo "  2. Resume Last Saved Draft (if available)"
echo "  3. Delete Current Draft (if available)"
echo "  4. View Draft History (if available)"
echo "  0. Logout"
echo
echo "Note: Console app uses its own embedded database (independent from WebApp)"
echo "====================================================================================="
echo

# Use different port (33307) to avoid conflict with WebApp database
docker run -it --name app-console \
    -p 33307:3306 \
    app-console:latest

echo
echo "======================================================================="
echo "                    SESSION COMPLETED"
echo "======================================================================="
echo

echo "To restart the console application:"
echo "  docker start -i app-console"
echo

echo "To cleanup:"
echo "  docker stop app-console || true"
echo "  docker rm app-console || true"
echo

echo "======================================================================="