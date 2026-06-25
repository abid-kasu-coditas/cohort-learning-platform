package com.example.cohortplatform.dto.response;

import com.example.cohortplatform.entities.enums.SubmissionStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SubmissionResponse(
        Long submissionId,
        SubmissionStatus status,
        LocalDateTime submittedAt
) {
}
