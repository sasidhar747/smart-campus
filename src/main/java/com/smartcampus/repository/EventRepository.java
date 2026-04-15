package com.smartcampus.repository;

import com.smartcampus.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE " +
           "(:department IS NULL OR e.department = :department) AND " +
           "(:type IS NULL OR e.type = :type)")
    List<Event> findByFilters(@Param("department") String department, 
                             @Param("type") String type);
}
