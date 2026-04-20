package com.smartcampus;

import com.smartcampus.model.Event;
import com.smartcampus.model.Registration;
import com.smartcampus.model.Role;
import com.smartcampus.model.User;
import com.smartcampus.repository.EventRepository;
import com.smartcampus.repository.RegistrationRepository;
import com.smartcampus.repository.RoleRepository;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.service.EventService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@SpringBootApplication
public class SmartCampusApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCampusApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataLoader(EventRepository eventRepository,
                                        RegistrationRepository registrationRepository,
                                        UserRepository userRepository,
                                        RoleRepository roleRepository,
                                        PasswordEncoder passwordEncoder,
                                        EventService eventService) {
        return args -> {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
            Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_STUDENT")));

            createUserIfMissing(userRepository, passwordEncoder, adminRole,
                    "System Admin", "admin@univ.edu", "admin123", "Administration");
            createUserIfMissing(userRepository, passwordEncoder, studentRole,
                    "Demo Student", "student@univ.edu", "student123", "Computer Science");

            Event springBootBootcamp;
            Event researchSeminar;
            Event hackathon;
            Event careerFair;
            Event designSprint;
            Event culturalNight;

            if (eventRepository.count() == 0) {
                springBootBootcamp = eventRepository.save(new Event(null,
                        "Spring Boot Bootcamp",
                        "Hands-on workshop covering MVC, REST APIs, validation, and security for enterprise Java apps.",
                        LocalDateTime.now().plusDays(4).withHour(10).withMinute(0),
                        "Innovation Auditorium",
                        "Computer Science",
                        "Workshop",
                        80,
                        0));

                researchSeminar = eventRepository.save(new Event(null,
                        "AI Research Seminar",
                        "Faculty and student researchers present current work in AI, machine learning, and ethics.",
                        LocalDateTime.now().plusDays(6).withHour(14).withMinute(30),
                        "Research Block - Hall 2",
                        "Computer Science",
                        "Seminar",
                        120,
                        0));

                hackathon = eventRepository.save(new Event(null,
                        "Smart Campus Hackathon",
                        "Build campus automation ideas with your team in a 12-hour coding challenge.",
                        LocalDateTime.now().plusDays(9).withHour(9).withMinute(0),
                        "Startup Lab",
                        "Information Technology",
                        "Hackathon",
                        60,
                        0));

                careerFair = eventRepository.save(new Event(null,
                        "Placement Connect 2026",
                        "Meet recruiters, attend company talks, and explore internship opportunities.",
                        LocalDateTime.now().plusDays(12).withHour(11).withMinute(0),
                        "Central Courtyard",
                        "Placement Cell",
                        "Career Fair",
                        250,
                        0));

                designSprint = eventRepository.save(new Event(null,
                        "Design Thinking Sprint",
                        "Interactive workshop focused on ideation, prototyping, and solving student-centered problems.",
                        LocalDateTime.now().plusDays(15).withHour(13).withMinute(0),
                        "Management Block - Studio 4",
                        "Management",
                        "Workshop",
                        70,
                        0));

                culturalNight = eventRepository.save(new Event(null,
                        "Campus Cultural Night",
                        "Open stage performances, club showcases, and a student celebration evening.",
                        LocalDateTime.now().plusDays(18).withHour(18).withMinute(0),
                        "Open Air Theatre",
                        "General",
                        "Cultural",
                        300,
                        0));
            } else {
                springBootBootcamp = eventRepository.findAll().stream()
                        .filter(event -> "Spring Boot Bootcamp".equals(event.getTitle()))
                        .findFirst()
                        .orElse(null);
                researchSeminar = eventRepository.findAll().stream()
                        .filter(event -> "AI Research Seminar".equals(event.getTitle()))
                        .findFirst()
                        .orElse(null);
                hackathon = eventRepository.findAll().stream()
                        .filter(event -> "Smart Campus Hackathon".equals(event.getTitle()))
                        .findFirst()
                        .orElse(null);
                careerFair = eventRepository.findAll().stream()
                        .filter(event -> "Placement Connect 2026".equals(event.getTitle()))
                        .findFirst()
                        .orElse(null);
                designSprint = eventRepository.findAll().stream()
                        .filter(event -> "Design Thinking Sprint".equals(event.getTitle()))
                        .findFirst()
                        .orElse(null);
                culturalNight = eventRepository.findAll().stream()
                        .filter(event -> "Campus Cultural Night".equals(event.getTitle()))
                        .findFirst()
                        .orElse(null);
            }

            if (registrationRepository.count() == 0 && springBootBootcamp != null && researchSeminar != null && careerFair != null) {
                registrationRepository.save(new Registration(null,
                        springBootBootcamp.getId(),
                        springBootBootcamp.getTitle(),
                        "Demo Student",
                        "student@univ.edu",
                        "Computer Science",
                        LocalDateTime.now().minusDays(1),
                        5,
                        "Very useful workshop with clear practical examples."));

                registrationRepository.save(new Registration(null,
                        researchSeminar.getId(),
                        researchSeminar.getTitle(),
                        "Demo Student",
                        "student@univ.edu",
                        "Computer Science",
                        LocalDateTime.now().minusHours(10),
                        0,
                        null));

                registrationRepository.save(new Registration(null,
                        careerFair.getId(),
                        careerFair.getTitle(),
                        "Aarav Mehta",
                        "aarav@univ.edu",
                        "Management",
                        LocalDateTime.now().minusHours(6),
                        4,
                        "Good industry interaction and practical advice."));
            }

            eventService.refreshRegisteredCounts();
        };
    }

    private void createUserIfMissing(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     Role role,
                                     String name,
                                     String email,
                                     String rawPassword,
                                     String department) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User(name, email, passwordEncoder.encode(rawPassword), department);
            user.setRoles(Set.of(role));
            userRepository.save(user);
        }
    }
}
