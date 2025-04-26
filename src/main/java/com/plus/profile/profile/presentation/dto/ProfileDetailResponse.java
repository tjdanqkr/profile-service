package com.plus.profile.profile.presentation.dto;

import com.plus.profile.profile.domain.Profile;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ProfileDetailResponse {
    private final UUID id;
    private final String title;
    private final String content;
    private final long viewCount;
    private final LocalDateTime createdAt;
    private final UUID userId;
    private final String username;

    @QueryProjection
    public ProfileDetailResponse(Profile profile) {
        this.id = profile.getId();
        this.title = profile.getTitle();
        this.content = profile.getContent();
        this.viewCount = profile.getViewCount();
        this.createdAt = profile.getCreatedAt();
        this.userId = profile.getUserId();
        this.username = profile.getUsername();
    }
}
