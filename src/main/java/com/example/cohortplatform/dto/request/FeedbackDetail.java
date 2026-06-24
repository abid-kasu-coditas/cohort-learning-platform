package com.example.cohortplatform.dto.request;

import lombok.Builder;

@Builder
public record FeedbackDetail(
        String marks,
        String feedback,
        String assignmentName,
        String courseName
) {
}
