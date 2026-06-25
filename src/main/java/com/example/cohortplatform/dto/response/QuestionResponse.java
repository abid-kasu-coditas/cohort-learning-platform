package com.example.cohortplatform.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record QuestionResponse(
        Long questionId,
        String text,
        boolean isAnswered,
        LocalDateTime askedAt,
        LocalDateTime answeredAt,
        UserSummaryDto askedBy
) {}
