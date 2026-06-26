package com.example.cohortplatform.dto.message;

import com.example.cohortplatform.dto.response.ChatMessageResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionEvent {

    private String type; // CHAT_MESSAGE | SESSION_STARTED | SESSION_ENDED
    private ChatMessageResponse message;

    public static SessionEvent chatMessage(ChatMessageResponse message) {
        return SessionEvent.builder().type("CHAT_MESSAGE").message(message).build();
    }

    public static SessionEvent sessionStarted() {
        return SessionEvent.builder().type("SESSION_STARTED").build();
    }

    public static SessionEvent sessionEnded() {
        return SessionEvent.builder().type("SESSION_ENDED").build();
    }
}
