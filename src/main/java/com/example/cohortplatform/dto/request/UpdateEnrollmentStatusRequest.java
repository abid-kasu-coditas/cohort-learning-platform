package com.example.cohortplatform.dto.request;

import com.example.cohortplatform.entities.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateEnrollmentStatusRequest(
        @NotNull(message = "Status is required")
        EnrollmentStatus status
) {}
