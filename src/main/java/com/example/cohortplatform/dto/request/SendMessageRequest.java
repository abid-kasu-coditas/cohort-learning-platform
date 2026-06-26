package com.example.cohortplatform.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
        @NotBlank(message = "Message text is required")
        String messageText
) {}
