package com.example.cohortplatform.mapper;

import com.example.cohortplatform.dto.response.MaterialResponse;
import com.example.cohortplatform.entities.CourseMaterial;
import com.example.cohortplatform.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class MaterialMapper {

    private final FileStorageService fileStorageService;

    public MaterialResponse toResponse(CourseMaterial material) {
        return MaterialResponse.builder()
                .materialId(material.getId())
                .fileName(material.getFilename())
                .contentType(material.getContentType().name())
                .downloadUrl(fileStorageService.generatePresignedUrl(material.getFilePath(), Duration.ofHours(1)))
                .build();
    }
}
