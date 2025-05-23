package com.plus.profile.profile.presentation.dto;

import com.plus.profile.profile.domain.MyProfile;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ProfileResponse {
    private final UUID profileId;
    private final String title;
    private final long viewCount;
    private final LocalDateTime createdAt;
    private final UUID userId;
    private final String username;

    public ProfileResponse(UUID profileId, String title, long viewCount, LocalDateTime createdAt, UUID userId, String username) {
        this.profileId = profileId;
        this.title = title;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.userId = userId;
        this.username = username;
    }

    @QueryProjection
    public ProfileResponse(MyProfile myProfile){
        this.profileId = myProfile.getId();
        this.title = myProfile.getTitle();
        this.viewCount = myProfile.getViewCount();
        this.createdAt = myProfile.getCreatedAt();
        this.userId = myProfile.getUserId();
        this.username = myProfile.getUsername();
    }
}
