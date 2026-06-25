package com.example.cohortplatform.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AssignmentResponse(
        Long assignmentId,
        String title,
        String description,
        LocalDateTime deadline,
        LocalDateTime createdAt,
        CourseSummaryDto course
) {}
