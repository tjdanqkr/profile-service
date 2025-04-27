package com.plus.profile.user.presentation.dto;

import com.plus.profile.user.domain.User;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserResponse {
    private final UUID id;
    private final String username;
    private final String role;
    @QueryProjection
    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole().getValue();
    }
}
