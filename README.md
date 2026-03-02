# Task 1

Easy client-server application to exchange messages over TCP.
It uses a database to store the data sent by the client, and it provides a basic interface to see the data.

## Requirements
- Java
- SQL (MySQL)
- Docker
- Swing
- jfree

## How to run
1. Run in your terminal this command "docker-compose up --build"
2. Open your browser and go to "http://localhost:6080/vnc.html" to view the GUI (or use a VNC client to connect to localhost:5900)
3. (Optional) Start a client in another terminal or in your IDE and send some data

## Choices
- I used MySQL for the database because I am familiar with it
- I first used JavaFX for the GUI, but I had some issues with the Docker image, so I switched to Swing
- The GUI is posted on a noVNC server because from macos there are some issues with the X11 forwarding
