package com.smartcampus.controller;

import com.smartcampus.model.User;
import com.smartcampus.service.EventService;
import com.smartcampus.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("departments", eventService.getDepartments());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("user") User user, 
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("departments", eventService.getDepartments());
            return "signup";
        }
        
        try {
            userService.registerUser(user);
            return "redirect:/login?signupSuccess";
        } catch (Exception e) {
            model.addAttribute("departments", eventService.getDepartments());
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }
}
