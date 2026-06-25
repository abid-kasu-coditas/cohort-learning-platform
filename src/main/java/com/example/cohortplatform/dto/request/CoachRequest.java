package com.example.cohortplatform.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CoachRequest(
        @NotBlank(message = "Message is required") String message
) {}
