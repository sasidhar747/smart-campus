package com.smartcampus.service;

import com.smartcampus.model.Event;
import com.smartcampus.model.Registration;
import com.smartcampus.repository.EventRepository;
import com.smartcampus.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public Registration registerStudent(Registration registration) {
        Event event = eventRepository.findById(registration.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getRegisteredCount() >= event.getCapacity()) {
            throw new RuntimeException("Event is full");
        }

        // Set event title for easy display
        registration.setEventTitle(event.getTitle());
        
        Registration savedRegistration = registrationRepository.save(registration);
        
        // Update event registered count
        event.setRegisteredCount(event.getRegisteredCount() + 1);
        eventRepository.save(event);
        
        return savedRegistration;
    }

    public List<Registration> getStudentRegistrations(String email) {
        return registrationRepository.findByStudentEmail(email);
    }
    
    public List<Registration> getEventRegistrations(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    @Transactional
    public void saveFeedback(Long registrationId, int rating, String feedback) {
        Registration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        reg.setRating(rating);
        reg.setFeedback(feedback);
        registrationRepository.save(reg);
    }
}
