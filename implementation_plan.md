# Implementation Plan - Smart Campus Event Management System

## 1. Project Setup
- [x] Create directory structure.
- [x] Create `pom.xml`.
- [x] Create `application.properties`.

## 2. Model Layer
- [x] `Event.java` (Entity)
- [x] `Registration.java` (Entity)

## 3. Repository Layer
- [x] `EventRepository.java`
- [x] `RegistrationRepository.java`

## 4. Service Layer
- [x] `EventService.java`
- [x] `RegistrationService.java`

## 5. Security & Exception Handling
- [x] `SecurityConfig.java` (Basic Auth)
- [x] `GlobalExceptionHandler.java`

## 6. Controller Layer
- [x] `EventController.java` (MVC)
- [x] `EventRestController.java` (REST)
- [x] `AdminController.java` (CRUD + Stats)

## 7. Frontend (Thymeleaf)
- [x] `layout.html`
- [x] `index.html` (Browsing)
- [x] `event-details.html` (Includes registration-form)
- [x] `admin-dashboard.html` (Includes stats)
- [x] `my-registrations.html`

## 8. Styling
- [x] `styles.css` (Premium Glassmorphism)
