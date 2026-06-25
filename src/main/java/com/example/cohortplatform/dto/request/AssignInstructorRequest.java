package com.example.cohortplatform.dto.request;

import jakarta.validation.constraints.NotNull;

public record AssignInstructorRequest(
        @NotNull(message = "Instructor ID is required") Long instructorId
) {}
