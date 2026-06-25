package com.example.cohortplatform.dto.response;

import lombok.Builder;

@Builder
public record DashboardResponse(
        long totalCourses,
        long activeCourses,
        long totalStudents,
        long totalInstructors,
        long totalEnrollments
) {}
