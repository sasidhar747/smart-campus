package com.smartcampus.controller;

import com.smartcampus.model.Event;
import com.smartcampus.model.EventStatistics;
import com.smartcampus.service.EventService;
import com.smartcampus.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                            Model model) {
        var events = eventService.getFilteredEvents(department, type, date);
        EventStatistics statistics = eventService.getEventStatistics(events);

        model.addAttribute("events", events);
        model.addAttribute("statistics", statistics);
        model.addAttribute("departments", eventService.getDepartments());
        model.addAttribute("eventTypes", eventService.getEventTypes());
        model.addAttribute("selectedDept", department);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedDate", date);
        return "admin/dashboard";
    }

    @GetMapping("/events/new")
    public String showCreateForm(Model model) {
        Event event = new Event();
        event.setEventDate(LocalDateTime.now().plusDays(7).withHour(10).withMinute(0));
        model.addAttribute("event", event);
        model.addAttribute("departments", eventService.getDepartments());
        model.addAttribute("eventTypes", eventService.getEventTypes());
        return "admin/event-form";
    }

    @PostMapping("/events/save")
    public String saveEvent(@Valid @ModelAttribute Event event,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        boolean creatingNewEvent = event.getId() == null;

        if (result.hasErrors()) {
            model.addAttribute("departments", eventService.getDepartments());
            model.addAttribute("eventTypes", eventService.getEventTypes());
            return "admin/event-form";
        }

        eventService.saveEvent(event);
        redirectAttributes.addFlashAttribute("successMessage",
                creatingNewEvent ? "Event created successfully." : "Event updated successfully.");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/events/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getRequiredEvent(id));
        model.addAttribute("departments", eventService.getDepartments());
        model.addAttribute("eventTypes", eventService.getEventTypes());
        return "admin/event-form";
    }

    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        eventService.deleteEvent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully.");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/events/registrations/{id}")
    public String viewEventRegistrations(@PathVariable Long id, Model model) {
        Event event = eventService.getRequiredEvent(id);
        model.addAttribute("event", event);
        model.addAttribute("registrations", registrationService.getEventRegistrations(id));
        return "admin/attendees";
    }
}
