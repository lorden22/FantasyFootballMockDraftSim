#! /bin/bash

# Start the server
#Windows Start up
cd /c/Users/Bryan/Coding/projects/FantasyFootballMockDraftSim

echo 'Building and starting the backend server...'
docker build -f WebApp/Backend/Dockerfile -t app-backend:latest .

echo 'Building and starting the frontend server...'
docker run -p 80:8080 app-backend

echo 'Backend server started on port 8080'


#Mac Start up
" cd /Users/bryan/Documents/self-workself/FantasyFootballMockDraftSim
    echo 'Building and starting the backend server...'
    docker build -f WebApp/Backend/Dockerfile -t app-backend:latest .

    echo 'Building and starting the frontend server...'
    docker run -p 80:8080 app-backend

    echo 'Backend server started on port 8080
"