package com.example.cohortplatform.mapper;

import com.example.cohortplatform.dto.response.AdminUserResponse;
import com.example.cohortplatform.dto.response.UserSummaryDto;
import com.example.cohortplatform.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserSummaryDto toSummary(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .firstName(user.getFirstname())
                .lastName(user.getLastname())
                .email(user.getEmail())
                .build();
    }

    public AdminUserResponse toAdminResponse(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstname())
                .lastName(user.getLastname())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
