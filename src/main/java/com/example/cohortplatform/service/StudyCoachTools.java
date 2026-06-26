package com.example.cohortplatform.service;

import com.example.cohortplatform.dto.response.EnrollmentResponse;

import com.example.cohortplatform.repository.*;
import com.example.cohortplatform.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyCoachTools {


    private final StudentService studentService;

    @Tool(description = "Enroll the student in a course by courseId. Returns enrollment confirmation. Use this when the student asks to join or enroll in a course.")
    public String enrollInCourse(Long courseId) {
        try {
            Long studentId = currentStudentId();
            EnrollmentResponse response = studentService.enroll(courseId, studentId);
            return "Successfully enrolled in course: " + response.course().title()
                    + " (courseId=" + courseId + ")";
        } catch (Exception e) {
            return "Could not enroll: " + e.getMessage();
        }
    }

    private Long currentStudentId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return principal.getId();
    }
}
