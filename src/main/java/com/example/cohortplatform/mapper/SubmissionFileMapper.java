package com.example.cohortplatform.mapper;

import com.example.cohortplatform.dto.response.SubmissionFileDto;
import com.example.cohortplatform.entities.SubmissionFile;
import com.example.cohortplatform.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class SubmissionFileMapper {

    private final FileStorageService fileStorageService;

    public SubmissionFileDto toDto(SubmissionFile file) {
        return SubmissionFileDto.builder()
                .fileId(file.getId())
                .fileName(file.getFileName())
                .contentType(file.getContentType())
                .fileSizeBytes(file.getFileSize())
                .downloadUrl(fileStorageService.generatePresignedUrl(file.getFilePath(), Duration.ofHours(1)))
                .build();
    }
}
