package com.smartcampus.controller;

import com.smartcampus.model.Event;
import com.smartcampus.model.EventStatistics;
import com.smartcampus.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventRestController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public List<Event> getAllEvents(@RequestParam(required = false) String department,
                                    @RequestParam(required = false) String type,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if ((department == null || department.isBlank())
                && (type == null || type.isBlank())
                && date == null) {
            return eventService.getAllEvents();
        }
        return eventService.getFilteredEvents(department, type, date);
    }

    @GetMapping("/stats")
    public EventStatistics getEventStatistics(@RequestParam(required = false) String department,
                                              @RequestParam(required = false) String type,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return eventService.getEventStatistics(eventService.getFilteredEvents(department, type, date));
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getRequiredEvent(id);
    }
}
