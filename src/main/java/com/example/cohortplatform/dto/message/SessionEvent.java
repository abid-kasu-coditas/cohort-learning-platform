package com.example.cohortplatform.dto.message;

import com.example.cohortplatform.dto.response.QuestionResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionEvent {

    private String type; // QUESTION_ASKED | QUESTION_ANSWERED
    private QuestionResponse question;

    public static SessionEvent questionAsked(QuestionResponse question) {
        return SessionEvent.builder().type("QUESTION_ASKED").question(question).build();
    }

    public static SessionEvent questionAnswered(QuestionResponse question) {
        return SessionEvent.builder().type("QUESTION_ANSWERED").question(question).build();
    }
}
