package com.example.cohortplatform.service;

import com.example.cohortplatform.dto.request.CoachRequest;
import com.example.cohortplatform.dto.response.CoachResponse;
import com.example.cohortplatform.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyCoachService {

    private final ChatClient chatClient;
    private final StudyCoachTools studyCoachTools;

    public CoachResponse chat(CoachRequest request) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        String systemPrompt = """
                You are the Study Coach for student ID %d (%s).
                You help students stay organised, understand their progress, and take actions on their behalf.
                You can only access data belonging to this student — never another student's data.
                When you take an action (enroll, plan), state clearly what you did.
                Be concise, friendly, and practical.
                Today's date and time: %s
                """.formatted(
                principal.getId(),
                principal.getEmail(),
                java.time.LocalDateTime.now()
        );

        String reply = chatClient.prompt()
                .system(systemPrompt)
                .user(request.message())
                .tools(studyCoachTools)
                .call()
                .content();

        return CoachResponse.builder()
                .reply(reply)
                .actionsPerformed(List.of())
                .build();
    }
}
