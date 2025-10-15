CREATE DATABASE IF NOT EXISTS telemetry_data;
USE telemetry_data;

CREATE TABLE IF NOT EXISTS telemetry (
    id INT AUTO_INCREMENT PRIMARY KEY,
    time VARCHAR(9),
    speed FLOAT
);