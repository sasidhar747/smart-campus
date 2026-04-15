# Project Walkthrough: Smart Campus Event System

Use this guide to present the folder structure and explain the architecture of your system.

## 📁 Root Directory
- **`pom.xml`**: The heart of the project. Defines all dependencies (Spring Boot, JPA, Security).
- **`README.md`**: Quick-start guide and project overview.
- **`mvnw / mvnw.cmd`**: The Maven Wrapper, ensuring the project runs anywhere without pre-installed Maven.

---

## 🏗️ Backend Architecture (`src/main/java/com/smartcampus/`)

### 1. `model/` (Entities)
*Explain: "This is where we define our data shapes for the database."*
- **`Event.java`**: Fields like title, date, location, and capacity. Handles validation.
- **`Registration.java`**: Stores student data, ratings, and feedback for each event.

### 2. `repository/` (Persistence)
*Explain: "These interfaces handle all database communication automatically using Spring Data JPA."*
- **`EventRepository.java`**: Includes custom filters to search events by department and type.
- **`RegistrationRepository.java`**: Used for counting attendees and retrieving student history.

### 3. `service/` (Business Logic)
*Explain: "This layer handles complex rules, like checking if an event is full before allowing registration."*
- **`EventService.java`**: CRUD operations and filtration logic.
- **`RegistrationService.java`**: Manages the registration process and feedback submission.

### 4. `controller/` (Traffic Control)
*Explain: "This connects the Backend logic to the Frontend UI."*
- **`EventController.java`**: Handles all student-facing pages (Browsing, Registering).
- **`AdminController.java`**: Secured routes for management and statistics.

### 5. `security/` & `exception/`
- **`SecurityConfig.java`**: Configures Basic Auth and route protection.
- **`GlobalExceptionHandler.java`**: Ensures the user never sees a raw error page; they see a styled "Oops!" page instead.

---

## 🎨 Frontend & Config (`src/main/resources/`)

### 1. `templates/` (UI Pages)
- **`layout.html`**: The master template containing the navigation and the Glassmorphism CSS.
- **`index.html`**: The main browsing grid.
- **`admin/dashboard.html`**: The central command center with stats and management table.

### 2. `static/css/`
- **`styles.css`**: Contains the full design system (Glassmorphism, Dark gradients, and modern typography).

### 3. `application.properties`
- Configures the **H2 In-Memory Database** and server port (8080).
