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

echo 'Syncing shared files for Maven build...'
rm -rf "$MAIN_DIR/WebApp/Backend/src/main/java/com/example/common"
cp -r "$MAIN_DIR/shared/src/main/java/com/example/common" "$MAIN_DIR/WebApp/Backend/src/main/java/com/example/"
echo 'Shared files synced for compilation'

echo 'Stopping and removing any existing containers...'
docker ps -a | grep app-backend && docker stop app-backend && docker rm app-backend

echo 'Removing any existing images...'
docker images | grep app-backend && docker rmi app-backend:latest

echo 'Compiling new Jar...'
cd "$MAIN_DIR/WebApp/Backend/"
mvn clean

# mvn package
mvn package -DskipTests -Dmaven.test.skip=true

echo 'Removing synced files before Docker build...'
cd "$MAIN_DIR"
rm -rf "$MAIN_DIR/WebApp/Backend/src/main/java/com/example/common"
echo 'Synced files cleaned up - JAR contains compiled shared classes'

echo 'Building backend container...'
docker pull openjdk:17-jdk-slim
docker build -f WebApp/Backend/Dockerfile -t app-backend:latest .

echo 'Starting backend server inside container...'
docker run -d --name app-backend -p 80:8080 app-backend

echo 'Backend server started on port 80'
echo 'Shared classes are compiled into JAR - no local shared files remain to track in Git' 