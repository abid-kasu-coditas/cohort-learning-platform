package com.example.cohortplatform.dto.response;

import com.example.cohortplatform.entities.enums.EnrollmentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EnrollmentResponse(
        Long enrollmentId,
        EnrollmentStatus status,
        LocalDateTime enrolledAt,
        CourseDetailDto course,
        UserSummaryDto student
) {}
