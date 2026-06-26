package com.example.cohortplatform.controller;

import com.example.cohortplatform.dto.request.SendMessageRequest;
import com.example.cohortplatform.security.UserPrincipal;
import com.example.cohortplatform.service.LiveSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SessionWebSocketController {

    private final LiveSessionService liveSessionService;

    @MessageMapping("/session/{sessionId}/chat")
    public void sendMessage(
            @DestinationVariable Long sessionId,
            @Payload SendMessageRequest request,
            Principal principal) {
        UserPrincipal user = extractUser(principal);
        liveSessionService.sendMessage(sessionId, user.getId(), request);
    }

    private UserPrincipal extractUser(Principal principal) {
        return (UserPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    }
}
