package com.example.cohortplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private long totalCourses;
    private long totalActiveCourses;
    private long totalStudents;
    private long totalInstructors;
    private long totalEnrollments;
}
