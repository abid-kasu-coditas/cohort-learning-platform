package com.example.cohortplatform.dto.response;

import lombok.Builder;

@Builder
public record ActionPerformed(
        String action,
        Long targetId,
        String detail
) {}
