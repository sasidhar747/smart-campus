package com.smartcampus;

import com.smartcampus.model.Role;
import com.smartcampus.model.User;
import com.smartcampus.model.Event;
import com.smartcampus.model.Registration;
import com.smartcampus.repository.RoleRepository;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.repository.EventRepository;
import com.smartcampus.repository.RegistrationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Arrays;

@SpringBootApplication
public class SmartCampusApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCampusApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataLoader(EventRepository repository, 
                                       RegistrationRepository regRepo,
                                       UserRepository userRepo,
                                       RoleRepository roleRepo,
                                       PasswordEncoder encoder) {
        return args -> {
            // Seed Roles
            Role adminRole = roleRepo.findByName("ROLE_ADMIN").orElseGet(() -> roleRepo.save(new Role("ROLE_ADMIN")));
            Role studentRole = roleRepo.findByName("ROLE_STUDENT").orElseGet(() -> roleRepo.save(new Role("ROLE_STUDENT")));

            // Seed Admin User
            if (!userRepo.existsByEmail("admin@univ.edu")) {
                User admin = new User("System Admin", "admin@univ.edu", encoder.encode("admin123"), "IT");
                admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
                userRepo.save(admin);
            }

            // Seed Events
            Event e1 = repository.save(new Event(null, "Java Workshop", "Advanced Spring Boot Workshop", 
                           LocalDateTime.now().plusDays(5), "Main Auditorium", "Computer Science", "Workshop", 50, 2));
            repository.save(new Event(null, "Alumni Meetup", "Networking event with alumni", 
                           LocalDateTime.now().plusDays(10), "Hall B", "General", "Seminar", 100, 0));
            repository.save(new Event(null, "Hackathon 2026", "24-hour coding challenge", 
                           LocalDateTime.now().plusDays(15), "Innovation Lab", "Computer Science", "Workshop", 30, 0));
            repository.save(new Event(null, "Career Fair", "Meet top recruiters", 
                           LocalDateTime.now().plusDays(20), "Campus Grounds", "Placement Cell", "Seminar", 200, 0));
            
            // Seed some registrations
            regRepo.save(new Registration(null, e1.getId(), e1.getTitle(), "Alice Smith", "alice@univ.edu", "Computer Science", LocalDateTime.now().minusDays(1), 5, "Amazing workshop!"));
            regRepo.save(new Registration(null, e1.getId(), e1.getTitle(), "Bob Jones", "bob@univ.edu", "Electrical Engineering", LocalDateTime.now().minusDays(1), 4, "Very informative."));
        };
    }
}
