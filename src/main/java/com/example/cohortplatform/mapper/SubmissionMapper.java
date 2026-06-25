package com.example.cohortplatform.mapper;

import com.example.cohortplatform.dto.response.GradeSummaryDto;
import com.example.cohortplatform.dto.response.SubmissionDetailResponse;
import com.example.cohortplatform.dto.response.SubmissionFileDto;
import com.example.cohortplatform.dto.response.SubmissionResponse;
import com.example.cohortplatform.entities.Grade;
import com.example.cohortplatform.entities.Submission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubmissionMapper {

    private final UserMapper userMapper;
    private final SubmissionFileMapper submissionFileMapper;
    private final AssignmentMapper assignmentMapper;

    public SubmissionDetailResponse toDetail(Submission submission, Optional<Grade> grade) {
        List<SubmissionFileDto> files = submission.getFiles().stream()
                .map(submissionFileMapper::toDto)
                .collect(Collectors.toList());

        GradeSummaryDto gradeSummary = grade.map(g -> GradeSummaryDto.builder()
                .gradeId(g.getId())
                .score(g.getScore())
                .feedback(g.getFeedback())
                .gradedAt(g.getGradedAt())
                .build()).orElse(null);

        return SubmissionDetailResponse.builder()
                .submissionId(submission.getId())
                .status(submission.getStatus())
                .submittedAt(submission.getSubmittedAt())
                .notes(submission.getNotes())
                .student(userMapper.toSummary(submission.getStudent()))
                .files(files)
                .grade(gradeSummary)
                .build();
    }

    public SubmissionResponse toStudentView(Submission submission, Optional<Grade> grade) {
        List<SubmissionFileDto> files = submission.getFiles().stream()
                .map(submissionFileMapper::toDto)
                .toList();

        GradeSummaryDto gradeSummary = grade.map(g -> GradeSummaryDto.builder()
                .gradeId(g.getId())
                .score(g.getScore())
                .feedback(g.getFeedback())
                .gradedAt(g.getGradedAt())
                .build()).orElse(null);

        return SubmissionResponse.builder()
                .submissionId(submission.getId())
                .status(submission.getStatus())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
