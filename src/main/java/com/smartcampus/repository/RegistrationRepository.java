package com.smartcampus.repository;

import com.smartcampus.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByStudentEmailIgnoreCaseOrderByRegistrationDateDesc(String email);
    List<Registration> findByEventIdOrderByRegistrationDateDesc(Long eventId);
    Optional<Registration> findByIdAndStudentEmailIgnoreCase(Long id, String email);
    boolean existsByEventIdAndStudentEmailIgnoreCase(Long eventId, String studentEmail);
    long countByEventId(Long eventId);
    void deleteByEventId(Long eventId);

    @Query("SELECT COUNT(r) FROM Registration r")
    long getTotalRegistrations();

    @Query("SELECT COUNT(r) FROM Registration r WHERE r.eventId IN :eventIds")
    long countByEventIds(@Param("eventIds") List<Long> eventIds);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Registration r WHERE r.rating > 0")
    double getAverageFeedbackRating();

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Registration r WHERE r.rating > 0 AND r.eventId IN :eventIds")
    double getAverageFeedbackRatingByEventIds(@Param("eventIds") List<Long> eventIds);
}
