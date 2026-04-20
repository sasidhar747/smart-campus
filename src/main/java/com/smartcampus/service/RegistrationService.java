package com.smartcampus.service;

import com.smartcampus.dto.RegistrationForm;
import com.smartcampus.exception.AccessDeniedOperationException;
import com.smartcampus.exception.DuplicateRegistrationException;
import com.smartcampus.exception.EventCapacityExceededException;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.model.Event;
import com.smartcampus.model.Registration;
import com.smartcampus.model.User;
import com.smartcampus.repository.EventRepository;
import com.smartcampus.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public Registration registerStudent(RegistrationForm form, User student) {
        Event event = eventRepository.findById(form.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (registrationRepository.existsByEventIdAndStudentEmailIgnoreCase(event.getId(), student.getEmail())) {
            throw new DuplicateRegistrationException("You have already registered for this event");
        }

        if (event.isFull()) {
            throw new EventCapacityExceededException("This event is already full");
        }

        Registration registration = new Registration();
        registration.setEventId(event.getId());
        registration.setEventTitle(event.getTitle());
        registration.setStudentName(student.getName());
        registration.setStudentEmail(student.getEmail());
        registration.setStudentDepartment(student.getDepartment());
        registration.setRegistrationDate(LocalDateTime.now());

        Registration savedRegistration = registrationRepository.save(registration);
        event.setRegisteredCount(event.getRegisteredCount() + 1);
        eventRepository.save(event);

        return savedRegistration;
    }

    public List<Registration> getStudentRegistrations(String email) {
        return registrationRepository.findByStudentEmailIgnoreCaseOrderByRegistrationDateDesc(email);
    }

    public List<Registration> getEventRegistrations(Long eventId) {
        return registrationRepository.findByEventIdOrderByRegistrationDateDesc(eventId);
    }

    @Transactional
    public void saveFeedback(Long registrationId, String studentEmail, boolean adminUser, int rating, String feedback) {
        Registration registration = adminUser
                ? registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"))
                : registrationRepository.findByIdAndStudentEmailIgnoreCase(registrationId, studentEmail)
                .orElseThrow(() -> new AccessDeniedOperationException("You can only submit feedback for your own registrations"));

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        if (feedback != null && feedback.length() > 500) {
            throw new IllegalArgumentException("Feedback must be 500 characters or fewer");
        }

        registration.setRating(rating);
        registration.setFeedback(feedback == null || feedback.isBlank() ? null : feedback.trim());
        registrationRepository.save(registration);
    }
}
