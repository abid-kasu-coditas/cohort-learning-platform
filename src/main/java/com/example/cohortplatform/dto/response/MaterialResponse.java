package com.example.cohortplatform.dto.response;

import lombok.Builder;

@Builder
public record MaterialResponse(
        Long materialId,
        String fileName,
        String contentType,
        String downloadUrl
) {}
