#!/bin/bash

UNAME_OUT="$(uname -s)"
case "${UNAME_OUT}" in
    Linux*)     machine=Linux;;
    Darwin*)    machine=Mac;;
    CYGWIN*)    machine=Cygwin;;
    MINGW*)     machine=MinGw;;
    *)          machine="UNKNOWN:${UNAME_OUT}"
esac

echo "Running on ${machine}"

if [ "$machine" = "Mac" ]; then
    echo "Running on Mac"
    cd /Users/bryanlorden/Documents/self-workspace/personal/FantasyFootballMockDraftSim/
elif [ "$machine" = "Linux" ]; then
    echo "Running on Linux"
    cd ~/Coding/projects/FantasyFootballMockDraftSim/WebApp/
elif [ "$machine" = "Cygwin" ] || [ "$machine" = "MinGw" ]; then
    echo "Running on Windows"
    cd /c/Users/Bryan/Coding/projects/FantasyFootballMockDraftSim
else
    echo "Running on Unknown"
    echo "Please run this script on a Mac, Linux, or Windows machine"
    echo "Exiting with error code 1"
    exit 1
fi

# Assuming you're now in the correct project directory, no need to change directories again
# Let's remove the 'cd' command that was here, as it's not necessary with the above logic

echo 'Building backend container...'
docker build -f WebApp/Backend/Dockerfile -t app-backend:latest .

echo 'Starting backend server inside container...'
docker run -p 80:8080 app-backend

echo 'Backend server started on port 8080'
