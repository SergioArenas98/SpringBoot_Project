-- Create the database
CREATE DATABASE IF NOT EXISTS nurse_db;

-- Use the database
USE nurse_db;

-- Create the nurses table
CREATE TABLE IF NOT EXISTS nurse (
    nurse_id INT AUTO_INCREMENT PRIMARY KEY, 
    name VARCHAR(100) NOT NULL,             
    user VARCHAR(50) UNIQUE NOT NULL,      
    password VARCHAR(255) NOT NULL          
);

-- Insert sample data into the nurses table (optional)
INSERT INTO nurse (name, user, password)
VALUES
('Fiorella', 'fiorella01', 'password123'),
('David', 'david02', 'securepass'),
('Sergio', 'sergio03', 'passw0rd'),
('Martina', 'martina04', 'm@rt!na123');
