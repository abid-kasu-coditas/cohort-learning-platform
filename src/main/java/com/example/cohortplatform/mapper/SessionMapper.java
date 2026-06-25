package com.example.cohortplatform.mapper;

import com.example.cohortplatform.dto.response.QuestionResponse;
import com.example.cohortplatform.dto.response.SessionResponse;
import com.example.cohortplatform.entities.LiveSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SessionMapper {

    private final CourseMapper courseMapper;
    private final UserMapper userMapper;

    public SessionResponse toResponse(LiveSession session) {
        List<QuestionResponse> questions = session.getQuestions().stream()
                .map(q -> QuestionResponse.builder()
                        .questionId(q.getId())
                        .text(q.getQuestionText())
                        .isAnswered(q.isAnswered())
                        .askedAt(q.getAskedAt())
                        .answeredAt(q.getAnsweredAt())
                        .askedBy(userMapper.toSummary(q.getAskedBy()))
                        .build())
                .collect(Collectors.toList());

        return SessionResponse.builder()
                .sessionId(session.getId())
                .title(session.getTitle())
                .scheduledAt(session.getScheduledAt())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .isActive(session.isActive())
                .course(courseMapper.toSummary(session.getCourse()))
                .questions(questions)
                .build();
    }
}
