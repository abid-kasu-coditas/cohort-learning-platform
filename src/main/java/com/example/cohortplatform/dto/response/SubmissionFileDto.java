package com.example.cohortplatform.dto.response;

import lombok.Builder;

@Builder
public record SubmissionFileDto(
        Long fileId,
        String fileName,
        String contentType,
        Long fileSizeBytes,
        String downloadUrl
) {}
