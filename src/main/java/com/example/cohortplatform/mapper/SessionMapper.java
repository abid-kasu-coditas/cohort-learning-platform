package com.example.cohortplatform.mapper;

import com.example.cohortplatform.dto.response.ChatMessageResponse;
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
        List<ChatMessageResponse> messages = session.getMessages().stream()
                .map(m -> ChatMessageResponse.builder()
                        .messageId(m.getId())
                        .text(m.getMessageText())
                        .sentAt(m.getSentAt())
                        .sentBy(userMapper.toSummary(m.getSentBy()))
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
                .messages(messages)
                .build();
    }
}
