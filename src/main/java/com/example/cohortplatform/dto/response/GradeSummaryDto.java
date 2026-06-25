package com.example.cohortplatform.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GradeSummaryDto(
        Long gradeId,
        Double score,
        String feedback,
        LocalDateTime gradedAt
) {}
