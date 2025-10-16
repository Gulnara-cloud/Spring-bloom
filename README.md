Spring Bloom — Full-Stack Project

This is a training full-stack application built with Spring Boot (backend) and React (frontend).
It demonstrates the complete development of a modern authentication system with user registration, login, validation, password hashing, testing, and frontend integration.

🚀 Implemented Features

Full project architecture: controller, service, repository, model, dto, exception

Backend (Spring Boot)

REST API for user registration and login (/api/auth/register, /api/auth/login)

Password hashing using PasswordEncoder

Global Exception Handling (GlobalExceptionHandler) for consistent JSON error responses

In-memory H2 database setup for testing

Unit tests and integration tests using JUnit and MockMvc

CORS configuration for React integration

Frontend (React)

Registration and login pages built with React.js

Beautiful beige UI design with centered form layout

fetch API communication with Spring Boot backend

React Router for navigation between Login and Register pages

Validation and success/error messages display

🧰 Technologies Used

Backend:

Java 17

Spring Boot 3.x

Spring Data JPA

Spring Security (PasswordEncoder)

H2 (in-memory database)

JUnit & Mockito

Maven

Frontend:

React 18

HTML / CSS (beige styled UI)

React Router DOM

Fetch API

⚙️ How to Run the Project
 Backend
# Run from the project root
./mvnw spring-boot:run


The backend will start at: http://localhost:8080

 Frontend
cd frontend
npm install
npm start


The React app will run at: http://localhost:3000
