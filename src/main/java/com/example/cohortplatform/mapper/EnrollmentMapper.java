package com.example.cohortplatform.mapper;

import com.example.cohortplatform.dto.response.EnrollmentResponse;
import com.example.cohortplatform.entities.CourseEnrollment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollmentMapper {

    private final CourseMapper courseMapper;
    private final UserMapper userMapper;

    public EnrollmentResponse toResponse(CourseEnrollment enrollment) {
        return EnrollmentResponse.builder()
                .enrollmentId(enrollment.getId())
                .status(enrollment.getStatus())
                .enrolledAt(enrollment.getEnrolledAt())
                .course(courseMapper.toDetail(enrollment.getCourse()))
                .student(userMapper.toSummary(enrollment.getUser()))
                .build();
    }
}
