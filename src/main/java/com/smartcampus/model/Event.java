package com.smartcampus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @NotNull(message = "Date and time are required")
    @Future(message = "Event date must be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime eventDate;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Event type is required")
    private String type; // Workshop, Seminar, Cultural, etc.

    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;
    
    private int registeredCount = 0;

    public Event() {}

    public Event(Long id, String title, String description, LocalDateTime eventDate, String location, String department, String type, int capacity, int registeredCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.department = department;
        this.type = type;
        this.capacity = capacity;
        this.registeredCount = registeredCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getRegisteredCount() { return registeredCount; }
    public void setRegisteredCount(int registeredCount) { this.registeredCount = registeredCount; }

    @Transient
    public int getAvailableSeats() {
        return Math.max(capacity - registeredCount, 0);
    }

    @Transient
    public boolean isFull() {
        return registeredCount >= capacity;
    }

    @Transient
    public int getOccupancyPercentage() {
        if (capacity <= 0) {
            return 0;
        }
        return Math.min((registeredCount * 100) / capacity, 100);
    }
}
