package com.smartcampus.repository;

import com.smartcampus.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE " +
           "e.eventDate >= :referenceTime AND " +
           "(:department IS NULL OR :department = '' OR e.department = :department) AND " +
           "(:type IS NULL OR :type = '' OR e.type = :type) AND " +
           "(:startOfDay IS NULL OR e.eventDate >= :startOfDay) AND " +
           "(:endOfDay IS NULL OR e.eventDate < :endOfDay) " +
           "ORDER BY e.eventDate ASC")
    List<Event> findByFilters(@Param("department") String department,
                              @Param("type") String type,
                              @Param("startOfDay") LocalDateTime startOfDay,
                              @Param("endOfDay") LocalDateTime endOfDay,
                              @Param("referenceTime") LocalDateTime referenceTime);

    @Query("SELECT e FROM Event e WHERE e.eventDate >= :referenceTime ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEvents(@Param("referenceTime") LocalDateTime referenceTime);

    @Query("SELECT DISTINCT e.department FROM Event e ORDER BY e.department ASC")
    List<String> findDistinctDepartments();

    @Query("SELECT DISTINCT e.type FROM Event e ORDER BY e.type ASC")
    List<String> findDistinctTypes();
}
