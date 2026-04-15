# Smart Campus Event Management System

A premium Spring Boot application for managing university events.

## Features
- **Student Portal**: Browse events, filter by department/type, and register with validation.
- **Admin Dashboard**: Full CRUD (Create, Read, Update, Delete) for events.
- **Statistics**: View registration counts and aggregate stats on the dashboard.
- **Modern UI**: Dark mode with glassmorphism and smooth interactions.
- **Security**: Basic authentication for administration tasks.

## Tech Stack
- **Backend**: Spring Boot 3.2, Spring Data JPA, Spring Security.
- **Frontend**: Thymeleaf, Vanilla CSS3 (Custom Design).
- **Database**: H2 (In-memory for easy demo).

## How to Run
1. Ensure you have **Java 17 or higher** installed.
2. Open a terminal in this folder.
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
   *(Note: If mvnw is not present, use your local Maven installation `mvn spring-boot:run`)*
4. Access the app at: `http://localhost:8080`

## Credentials
- **Admin Login**: `http://localhost:8080/login`
- **Username**: `admin`
- **Password**: `admin123`

## REST API
- `GET /api/events`: List all events.
- `GET /api/events/{id}`: Get event details.
