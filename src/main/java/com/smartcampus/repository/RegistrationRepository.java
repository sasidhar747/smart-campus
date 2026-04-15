package com.smartcampus.repository;

import com.smartcampus.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByStudentEmail(String email);
    List<Registration> findByEventId(Long eventId);
    long countByEventId(Long eventId);
}
