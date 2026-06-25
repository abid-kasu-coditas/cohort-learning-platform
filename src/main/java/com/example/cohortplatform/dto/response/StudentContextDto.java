package com.example.cohortplatform.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record StudentContextDto(
        String studentName,
        List<EnrolledCourseContext> enrolledCourses
) {
    @Builder
    public record EnrolledCourseContext(
            Long courseId,
            String courseTitle,
            String instructorName,
            List<AssignmentContext> assignments
    ) {}

    @Builder
    public record AssignmentContext(
            Long assignmentId,
            String title,
            String deadline,
            boolean submitted,
            Double score,
            String feedback
    ) {}
}
