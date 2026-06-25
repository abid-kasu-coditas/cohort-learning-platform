package com.example.cohortplatform.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GradeResponse(
        Long gradeId,
        Double score,
        String feedback,
        LocalDateTime gradedAt,
        UserSummaryDto gradedBy,
        SubmissionResponse submission
) {}
