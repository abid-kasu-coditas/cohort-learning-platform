package com.example.cohortplatform.mapper;

import com.example.cohortplatform.dto.response.AnnouncementResponse;
import com.example.cohortplatform.entities.Announcement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnnouncementMapper {

    private final UserMapper userMapper;

    public AnnouncementResponse toResponse(Announcement announcement) {
        return AnnouncementResponse.builder()
                .announcementId(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .createdAt(announcement.getCreatedAt())
                .postedBy(userMapper.toSummary(announcement.getPostedBy()))
                .build();
    }
}
