package com.plus.profile.user.presentation.dto;

import com.plus.profile.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class UserDetailResponse {
    private final UUID id;
    private final String username;
    private final String role;
    private final long point;
    private final List<UserCouponResponse> coupons;


    public UserDetailResponse(User user, long point, List<UserCouponResponse> coupons) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole().getValue();
        this.point = point;
        this.coupons = coupons;
    }
}
