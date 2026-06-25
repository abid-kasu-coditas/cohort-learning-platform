package com.example.cohortplatform.dto.message;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubmissionEvent {

    private String type; // NEW_SUBMISSION
    private Long submissionId;
    private Long assignmentId;
    private Long studentId;
    private String studentName;
    private LocalDateTime submittedAt;

    public static SubmissionEvent newSubmission(Long submissionId, Long assignmentId,
                                                Long studentId, String studentName,
                                                LocalDateTime submittedAt) {
        return SubmissionEvent.builder()
                .type("NEW_SUBMISSION")
                .submissionId(submissionId)
                .assignmentId(assignmentId)
                .studentId(studentId)
                .studentName(studentName)
                .submittedAt(submittedAt)
                .build();
    }
}
