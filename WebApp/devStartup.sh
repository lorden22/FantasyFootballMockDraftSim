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
    MAIN_DIR='/Users/bryanlorden/Documents/self-workspace/personal/FantasyFootballMockDraftSim/'
elif [ "$machine" = "Linux" ]; then
    echo "Running on Linux"
    MAIN_DIR='/home/bryan/Coding/FantasyFootballMockDraftSim'
elif [ "$machine" = "Cygwin" ] || [ "$machine" = "MinGw" ]; then
    echo "Running on Windows"
    MAIN_DIR='/c/Users/Bryan/Coding/projects/FantasyFootballMockDraftSim'
else
    echo "Running on Unknown"
    echo "Please run this script on a Mac, Linux, or Windows machine"
    echo "Exiting with error code 1"
    exit 1
fi

echo 'Stopping and removing any existing containers...'
docker ps -a | grep app-backend && docker stop app-backend && docker rm app-backend

echo 'Removing any existing images...'
docker images | grep app-backend && docker rmi app-backend:latest

echo 'Compiling new Jar...'
cd $MAIN_DIR/WebApp/Backend/
mvn clean
mvn package

echo 'Building backend container...'
cd $MAIN_DIR
docker pull openjdk:17-jdk-slim
docker build -f WebApp/Backend/Dockerfile -t app-backend:latest .

echo 'Starting backend server inside container...'
docker run -d --name app-backend -p 80:8080 app-backend

echo 'Backend server started on port 80'