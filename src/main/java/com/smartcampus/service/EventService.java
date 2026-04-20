package com.smartcampus.service;

import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.model.Event;
import com.smartcampus.model.EventStatistics;
import com.smartcampus.repository.EventRepository;
import com.smartcampus.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private static final List<String> DEFAULT_DEPARTMENTS = List.of(
            "Computer Science",
            "Information Technology",
            "Electronics",
            "Management",
            "Placement Cell",
            "General"
    );

    private static final List<String> DEFAULT_EVENT_TYPES = List.of(
            "Workshop",
            "Seminar",
            "Hackathon",
            "Career Fair",
            "Cultural"
    );

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now());
    }

    public List<Event> getFilteredEvents(String department, String type, LocalDate date) {
        LocalDateTime startOfDay = null;
        LocalDateTime endOfDay = null;

        if (date != null) {
            startOfDay = date.atStartOfDay();
            endOfDay = date.plusDays(1).atStartOfDay();
        }

        return eventRepository.findByFilters(department, type, startOfDay, endOfDay, LocalDateTime.now());
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event getRequiredEvent(Long id) {
        return getEventById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    public Event saveEvent(Event event) {
        if (event.getRegisteredCount() > event.getCapacity()) {
            throw new IllegalArgumentException("Capacity cannot be lower than the current registration count");
        }
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        Event event = getRequiredEvent(id);
        registrationRepository.deleteByEventId(event.getId());
        eventRepository.delete(event);
    }

    public EventStatistics getEventStatistics(List<Event> events) {
        long totalEvents = events.size();
        if (totalEvents == 0) {
            return new EventStatistics(0, 0, 0, 0);
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();
        long totalRegistrations = registrationRepository.countByEventIds(eventIds);
        double averageAttendance = (double) totalRegistrations / totalEvents;
        double averageFeedbackRating = registrationRepository.getAverageFeedbackRatingByEventIds(eventIds);

        return new EventStatistics(totalEvents, totalRegistrations, averageAttendance, averageFeedbackRating);
    }

    public List<String> getDepartments() {
        List<String> departments = eventRepository.findDistinctDepartments();
        return departments.isEmpty() ? DEFAULT_DEPARTMENTS : departments;
    }

    public List<String> getEventTypes() {
        List<String> eventTypes = eventRepository.findDistinctTypes();
        return eventTypes.isEmpty() ? DEFAULT_EVENT_TYPES : eventTypes;
    }

    public void refreshRegisteredCounts() {
        List<Event> events = eventRepository.findAll();
        boolean updated = false;

        for (Event event : events) {
            int actualCount = Math.toIntExact(registrationRepository.countByEventId(event.getId()));
            if (event.getRegisteredCount() != actualCount) {
                event.setRegisteredCount(actualCount);
                updated = true;
            }
        }

        if (updated) {
            eventRepository.saveAll(events);
        }
    }
}
