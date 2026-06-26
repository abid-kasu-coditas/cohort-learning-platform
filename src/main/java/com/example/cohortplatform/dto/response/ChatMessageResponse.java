package com.example.cohortplatform.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessageResponse(
        Long messageId,
        String text,
        LocalDateTime sentAt,
        UserSummaryDto sentBy
) {}
