package com.example.cohortplatform.dto.response;

import com.example.cohortplatform.entities.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private EnrollmentStatus status;
    private LocalDateTime enrolledAt;
}
