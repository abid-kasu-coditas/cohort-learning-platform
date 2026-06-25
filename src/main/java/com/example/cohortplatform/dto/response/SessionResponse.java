package com.example.cohortplatform.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record SessionResponse(
        Long sessionId,
        String title,
        LocalDateTime scheduledAt,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        boolean isActive,
        CourseSummaryDto course,
        List<QuestionResponse> questions
) {}
