#!/bin/bash

UNAME_OUT="$(uname -s)"
case "${UNAME_OUT}" in
    Linux*)     machine=Linux;;
    Darwin*)    machine=Mac;;
    CYGWIN*)    machine=Cygwin;;
    MINGW*)     machine=MinGw;;
    *)          machine="UNKNOWN:${UNAME_OUT}"
esac

if [ "$machine" = "Mac" ]; then
    echo "Running on Mac"
    MAIN_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
elif [ "$machine" = "Linux" ]; then
    echo "Running on Linux"
    MAIN_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
elif [ "$machine" = "Cygwin" ] || [ "$machine" = "MinGw" ]; then
    echo "Running on Windows"
    MAIN_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
else
    echo "Running on Unknown"
    echo "Please run this script on a Mac, Linux, or Windows machine"
    echo "Exiting with error code 1"
    exit 1
fi

echo 'Console-based Fantasy Football Mock Draft Simulator'
echo '===================================================='

echo 'Stopping and removing any existing console containers...'
docker ps -a | grep app-console && docker stop app-console && docker rm app-console

echo 'Removing any existing console images...'
docker images | grep app-console && docker rmi app-console:latest

echo 'Building console container...'
cd "$MAIN_DIR"
docker pull openjdk:17-jdk-slim
docker build -f consolebased/Dockerfile -t app-console:latest .

echo 'Console container built successfully!'
echo ''
echo 'To run the console application:'
echo '  Interactive mode: docker run -it app-console:latest'
echo '  Background mode:  docker run -d --name app-console app-console:latest'
echo ''
echo 'To view logs from a running container:'
echo '  docker exec app-console ls -la /app/logs'
echo '  docker exec app-console cat /app/logs/application_$(date +%Y-%m-%d).log'
echo ''
echo 'Note: Console app uses shared files directly in Docker - no local cleanup needed' 