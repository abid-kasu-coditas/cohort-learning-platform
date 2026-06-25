package com.example.cohortplatform.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AskQuestionRequest(
        @NotBlank(message = "Question text is required") String questionText
) {}
