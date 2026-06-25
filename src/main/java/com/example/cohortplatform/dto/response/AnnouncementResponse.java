package com.example.cohortplatform.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AnnouncementResponse(
        Long announcementId,
        String title,
        String content,
        LocalDateTime createdAt,
        UserSummaryDto postedBy
) {}
