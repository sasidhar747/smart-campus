package com.smartcampus.service;

import com.smartcampus.model.Event;
import com.smartcampus.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getFilteredEvents(String department, String type, String date) {
        List<Event> events = eventRepository.findAll();
        
        if (department != null && !department.isEmpty()) {
            events = events.stream().filter(e -> e.getDepartment().equals(department)).toList();
        }
        if (type != null && !type.isEmpty()) {
            events = events.stream().filter(e -> e.getType().equals(type)).toList();
        }
        if (date != null && !date.isEmpty()) {
            events = events.stream().filter(e -> e.getEventDate().toLocalDate().toString().equals(date)).toList();
        }
        return events;
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
