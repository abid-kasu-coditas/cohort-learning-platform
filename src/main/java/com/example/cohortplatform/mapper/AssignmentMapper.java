package com.example.cohortplatform.mapper;

import com.example.cohortplatform.dto.response.AssignmentResponse;
import com.example.cohortplatform.entities.Assignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignmentMapper {

    private final CourseMapper courseMapper;

    public AssignmentResponse toResponse(Assignment assignment) {
        return AssignmentResponse.builder()
                .assignmentId(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .deadline(assignment.getDeadline())
                .createdAt(assignment.getCreatedAt())
                .course(courseMapper.toSummary(assignment.getCourse()))
                .build();
    }
}
