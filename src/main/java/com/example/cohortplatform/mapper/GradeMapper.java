package com.example.cohortplatform.mapper;

import com.example.cohortplatform.dto.response.GradeResponse;
import com.example.cohortplatform.dto.response.SubmissionFileDto;
import com.example.cohortplatform.dto.response.SubmissionResponse;
import com.example.cohortplatform.entities.Grade;
import com.example.cohortplatform.entities.Submission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GradeMapper {

    private final UserMapper userMapper;
    private final AssignmentMapper assignmentMapper;
    private final SubmissionFileMapper submissionFileMapper;

    public GradeResponse toResponse(Grade grade) {
        Submission sub = grade.getSubmission();

        List<SubmissionFileDto> files = sub.getFiles().stream()
                .map(submissionFileMapper::toDto)
                .toList();

        SubmissionResponse submissionDto = SubmissionResponse.builder()
                .submissionId(sub.getId())
                .status(sub.getStatus())
                .submittedAt(sub.getSubmittedAt())
                .build();

        return GradeResponse.builder()
                .gradeId(grade.getId())
                .score(grade.getScore())
                .feedback(grade.getFeedback())
                .gradedAt(grade.getGradedAt())
                .gradedBy(userMapper.toSummary(grade.getGradedBy()))
                .submission(submissionDto)
                .build();
    }
}
