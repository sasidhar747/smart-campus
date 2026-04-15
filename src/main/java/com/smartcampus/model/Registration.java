package com.smartcampus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long eventId;

    private String eventTitle;

    @NotBlank(message = "Name is required")
    private String studentName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String studentEmail;

    @NotBlank(message = "Department is required")
    private String studentDepartment;

    private LocalDateTime registrationDate = LocalDateTime.now();
    
    @Max(5)
    @Min(0)
    private int rating = 0;

    @Size(max = 500)
    private String feedback;

    public Registration() {}

    public Registration(Long id, Long eventId, String eventTitle, String studentName, String studentEmail, String studentDepartment, LocalDateTime registrationDate, int rating, String feedback) {
        this.id = id;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentDepartment = studentDepartment;
        this.registrationDate = registrationDate;
        this.rating = rating;
        this.feedback = feedback;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    public String getStudentDepartment() { return studentDepartment; }
    public void setStudentDepartment(String studentDepartment) { this.studentDepartment = studentDepartment; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}
