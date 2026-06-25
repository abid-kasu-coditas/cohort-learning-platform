package com.example.cohortplatform.dto.response;

import com.example.cohortplatform.entities.enums.SubmissionStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record SubmissionDetailResponse(
        Long submissionId,
        SubmissionStatus status,
        LocalDateTime submittedAt,
        String notes,
        UserSummaryDto student,
        List<SubmissionFileDto> files,
        GradeSummaryDto grade
) {}
