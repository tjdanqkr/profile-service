package com.plus.profile.global.dto.point;

import java.util.UUID;

public record PayOffPointRequest(
        UUID userId, long productId, long productPrice, String productName
) {
}
