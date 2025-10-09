-- Create the database
CREATE DATABASE student;

USE student;

-- Table 1: Login Credentials (Used for Authentication)
CREATE TABLE Login (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE
);

-- Table 2: Student Details (Used to store Name, ID, Course)
CREATE TABLE StudentDetails (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100),
    registration_number VARCHAR(20) UNIQUE,
    course VARCHAR(100),
    FOREIGN KEY (username) REFERENCES Login(username)
);

-- Table 3: Grade (Used to store Subject and Grade)
CREATE TABLE Grade (
    grade_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject_name VARCHAR(100) NOT NULL,
    grade VARCHAR(5),
    UNIQUE KEY (student_id, subject_name),
    FOREIGN KEY (student_id) REFERENCES StudentDetails(student_id)
);

-- --- Sample Data for Testing ---

-- Insert a sample user (username: testuser, password: testpass)
INSERT INTO Login (username, password, email) VALUES ('testuser', 'testpass', 'test@example.com');

-- Insert sample student details for the test user
INSERT INTO StudentDetails (username, name, registration_number, course)
VALUES ('testuser', 'Alice Johnson', 'S98765', 'Physics Major');

-- Insert sample grades for the test user
INSERT INTO Grade (student_id, subject_name, grade)
SELECT sd.student_id, 'Calculus I', 'A' 
FROM StudentDetails sd 
WHERE sd.username = 'testuser';

INSERT INTO Grade (student_id, subject_name, grade)
SELECT sd.student_id, 'Modern Physics', 'B+' 
FROM StudentDetails sd 
WHERE sd.username = 'testuser';

