package com.smartcampus.model;

public record EventStatistics(
        long totalEvents,
        long totalRegistrations,
        double averageAttendance,
        double averageFeedbackRating
) {
}
