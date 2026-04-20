# Smart Campus Event Management System

Demo-ready Spring Boot web application for colleges and universities to manage campus events, workshops, seminars, and student registrations.

## Core Features

### Student Module
- Browse only upcoming events through Thymeleaf MVC pages.
- Filter events by department, type, and date.
- Register for an event with validation.
- View personal registration history after login.
- Submit event feedback and ratings.

### Admin Module
- Secure admin access with HTTP Basic authentication.
- Create, edit, and delete events using Spring Boot + JPA CRUD.
- Search events with filters.
- View attendee lists for each event.
- Review aggregate event statistics such as total events, registrations, average attendance, and average feedback rating.

### REST API
- `GET /api/events`
- `GET /api/events?department=Computer Science&type=Workshop&date=2026-04-25`
- `GET /api/events/{id}`
- `GET /api/events/stats`

## Technical Highlights
- Spring Core dependency injection with `@Autowired`
- Spring MVC controllers using `@Controller`, `@GetMapping`, and `@PostMapping`
- Spring Boot embedded Tomcat configuration
- Spring Data JPA entity mapping with `@Entity`, `@Id`, and custom repository queries
- Validation using `@NotBlank`, `@NotNull`, `@Email`, `@Size`, `@Min`, and `@Future`
- Spring Security with student form login and admin HTTP Basic authentication
- Global exception handling using `@ControllerAdvice`
- HTML5, CSS3, and Thymeleaf frontend templates
- H2 in-memory database with seeded demo data

## Demo Credentials

### Student Login
- Email: `student@univ.edu`
- Password: `student123`

### Admin Login
- Email: `admin@univ.edu`
- Password: `admin123`

Admin pages are available under `http://localhost:8081/admin/dashboard` and use the browser's HTTP Basic login prompt.

## Run the Project
1. Open a terminal in `project-2-smart-campus`.
2. Start the application:

```powershell
.\mvnw.cmd spring-boot:run
```

3. Open [http://localhost:8081](http://localhost:8081).

## Professor Demo Flow
1. Open the home page and explain event browsing, filtering, and the MVC student view.
2. Login as `student@univ.edu` and show registration history plus feedback submission.
3. Open `http://localhost:8081/api/events` and `http://localhost:8081/api/events/stats` to demonstrate REST APIs.
4. Open `http://localhost:8081/admin/dashboard`, login with the admin account, and show CRUD operations, search filters, attendee management, and statistics.

## Database Console
- H2 console: [http://localhost:8081/h2-console](http://localhost:8081/h2-console)
- JDBC URL: `jdbc:h2:mem:campusdb`
- Username: `sa`
- Password: `password`

## Test Verification

Run:

```powershell
.\mvnw.cmd test
```

The test suite verifies:
- Spring context loading
- Event filtering and statistics
- Home page rendering
- Event details page rendering
- REST API responses
- Admin authentication protection
