package com.smartcampus.controller;

import com.smartcampus.model.Event;
import com.smartcampus.service.EventService;
import com.smartcampus.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String department,
                            @RequestParam(required = false) String type,
                            @RequestParam(required = false) String date,
                            Model model) {
        model.addAttribute("events", eventService.getFilteredEvents(department, type, date));
        model.addAttribute("selectedDept", department);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedDate", date);
        return "admin/dashboard";
    }

    @GetMapping("/events/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        return "admin/event-form";
    }

    @PostMapping("/events/save")
    public String saveEvent(@Valid @ModelAttribute Event event, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/event-form";
        }
        eventService.saveEvent(event);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/events/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);
        return "admin/event-form";
    }

    @GetMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/events/registrations/{id}")
    public String viewEventRegistrations(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);
        model.addAttribute("registrations", registrationService.getEventRegistrations(id));
        return "admin/attendees";
    }
}
