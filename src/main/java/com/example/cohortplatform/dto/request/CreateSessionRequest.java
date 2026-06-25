package com.example.cohortplatform.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateSessionRequest(
        @NotBlank(message = "Title is required") String title,
        @NotNull(message = "Scheduled time is required") @Future(message = "Session must be scheduled in the future") LocalDateTime scheduledAt
) {}
