package com.smartcampus.controller;

import com.smartcampus.model.Event;
import com.smartcampus.model.Registration;
import com.smartcampus.model.User;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.service.EventService;
import com.smartcampus.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(@RequestParam(required = false) String department,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) String date,
                       Model model) {
        List<Event> events = eventService.getFilteredEvents(department, type, date);
        model.addAttribute("events", events);
        model.addAttribute("selectedDept", department);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedDate", date);
        return "index";
    }

    @GetMapping("/events/{id}")
    public String eventDetails(@PathVariable Long id, Model model, Authentication auth) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);

        Registration registration = new Registration();
        registration.setEventId(id);

        // Pre-fill form if user is logged in
        if (auth != null && auth.isAuthenticated()) {
            userRepository.findByEmail(auth.getName()).ifPresent(user -> {
                registration.setStudentName(user.getName());
                registration.setStudentEmail(user.getEmail());
                registration.setStudentDepartment(user.getDepartment());
            });
        }
        model.addAttribute("registration", registration);
        return "event-details";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute Registration registration,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            Event event = eventService.getEventById(registration.getEventId())
                    .orElseThrow(() -> new RuntimeException("Event not found"));
            model.addAttribute("event", event);
            return "event-details";
        }

        try {
            registrationService.registerStudent(registration);
            return "redirect:/my-registrations?email=" + registration.getStudentEmail();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            Event event = eventService.getEventById(registration.getEventId())
                    .orElseThrow(() -> new RuntimeException("Event not found"));
            model.addAttribute("event", event);
            return "event-details";
        }
    }

    @GetMapping("/my-registrations")
    public String viewRegistrations(@RequestParam String email, Model model) {
        List<Registration> registrations = registrationService.getStudentRegistrations(email);
        model.addAttribute("registrations", registrations);
        model.addAttribute("email", email);
        return "my-registrations";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@RequestParam Long regId,
                                 @RequestParam int rating,
                                 @RequestParam String feedback,
                                 @RequestParam String email) {
        registrationService.saveFeedback(regId, rating, feedback);
        return "redirect:/my-registrations?email=" + email;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
