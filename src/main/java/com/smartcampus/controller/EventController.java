package com.smartcampus.controller;

import com.smartcampus.dto.RegistrationForm;
import com.smartcampus.exception.AccessDeniedOperationException;
import com.smartcampus.model.Event;
import com.smartcampus.model.Registration;
import com.smartcampus.model.User;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.service.EventService;
import com.smartcampus.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
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
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       Model model) {
        List<Event> events = eventService.getFilteredEvents(department, type, date);
        populateFilterModel(model, department, type, date);
        model.addAttribute("events", events);
        return "index";
    }

    @GetMapping("/events/{id}")
    public String eventDetails(@PathVariable Long id, Model model, Authentication authentication) {
        Event event = eventService.getRequiredEvent(id);
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setEventId(id);

        User currentUser = getAuthenticatedUser(authentication);
        if (currentUser != null) {
            registrationForm.setStudentName(currentUser.getName());
            registrationForm.setStudentEmail(currentUser.getEmail());
            registrationForm.setStudentDepartment(currentUser.getDepartment());
        }

        model.addAttribute("event", event);
        model.addAttribute("registrationForm", registrationForm);
        model.addAttribute("currentUser", currentUser);
        return "event-details";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
                           BindingResult result,
                           Model model,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        Event event = eventService.getRequiredEvent(registrationForm.getEventId());
        User currentUser = getRequiredAuthenticatedUser(authentication);

        if (result.hasErrors()) {
            model.addAttribute("event", event);
            model.addAttribute("currentUser", currentUser);
            return "event-details";
        }

        registrationService.registerStudent(registrationForm, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Registration confirmed for " + event.getTitle() + ".");
        return "redirect:/my-registrations";
    }

    @GetMapping("/my-registrations")
    public String viewRegistrations(@RequestParam(required = false) String email,
                                    Model model,
                                    Authentication authentication) {
        User currentUser = getRequiredAuthenticatedUser(authentication);
        boolean adminUser = hasAuthority(authentication, "ROLE_ADMIN");

        String targetEmail = currentUser.getEmail();
        if (adminUser && email != null && !email.isBlank()) {
            targetEmail = email;
        }

        List<Registration> registrations = registrationService.getStudentRegistrations(targetEmail);
        model.addAttribute("registrations", registrations);
        model.addAttribute("email", targetEmail);
        model.addAttribute("isAdminView", adminUser);
        model.addAttribute("isOwnRegistrations", currentUser.getEmail().equalsIgnoreCase(targetEmail));
        return "my-registrations";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@RequestParam Long regId,
                                 @RequestParam int rating,
                                 @RequestParam(required = false) String feedback,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        User currentUser = getRequiredAuthenticatedUser(authentication);
        boolean adminUser = hasAuthority(authentication, "ROLE_ADMIN");

        registrationService.saveFeedback(regId, currentUser.getEmail(), adminUser, rating, feedback);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback saved successfully.");
        return "redirect:/my-registrations";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    private void populateFilterModel(Model model, String department, String type, LocalDate date) {
        model.addAttribute("departments", eventService.getDepartments());
        model.addAttribute("eventTypes", eventService.getEventTypes());
        model.addAttribute("selectedDept", department);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedDate", date);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        return userRepository.findByEmail(authentication.getName())
                .orElse(null);
    }

    private User getRequiredAuthenticatedUser(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) {
            throw new AccessDeniedOperationException("Please log in with a student account to continue");
        }
        return user;
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication != null
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }
}
