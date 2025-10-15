FROM --platform=linux/amd64 maven:3.9.6-eclipse-temurin-22 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:22-jdk-slim

WORKDIR /app

RUN apt-get update && apt-get install -y \
    xvfb \
    x11vnc \
    fluxbox \
    websockify \
    novnc \
    net-tools \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/Task1.jar ./Task1.jar

ENV DISPLAY=:99

EXPOSE 5900 8080 6080

CMD Xvfb :99 -screen 0 1024x768x16 & \
    sleep 2 && \
    x11vnc -display :99 -nopw -forever -quiet -shared -rfbport 5900 & \
    fluxbox & \
    websockify --web /usr/share/novnc/ 6080 localhost:5900 & \
    java -jar Task1.jar