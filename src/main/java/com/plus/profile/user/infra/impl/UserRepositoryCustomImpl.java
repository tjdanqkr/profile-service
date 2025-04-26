package com.plus.profile.user.infra.impl;

import com.plus.profile.user.domain.User;
import com.plus.profile.user.infra.UserRepositoryCustom;
import com.plus.profile.user.presentation.dto.QUserCouponResponse;
import com.plus.profile.user.presentation.dto.UserCouponResponse;
import com.plus.profile.user.presentation.dto.UserDetailResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.plus.profile.user.domain.QUser.user;
import static com.plus.profile.user.domain.QUserCoupon.userCoupon;
import static com.plus.profile.user.domain.QUserPoint.userPoint;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<UserDetailResponse> findUserDetailById(UUID userId) {
        User findUser = queryFactory
                .selectFrom(user)
                .leftJoin(user.userPoint, userPoint)
                .fetchJoin()
                .where(user.id.eq(userId))
                .fetchOne();
        if (findUser == null) {
            return Optional.empty();
        }
        List<UserCouponResponse> coupons = queryFactory
                .select(new QUserCouponResponse(userCoupon))
                .from(userCoupon)
                .where(userCoupon.user.id.eq(userId), userCoupon.isUsed.isFalse())
                .fetch();

        UserDetailResponse userDetailResponse = new UserDetailResponse(
                findUser,
                findUser.getUserPoint().getPoint(),
                coupons
        );
        return Optional.of(userDetailResponse);
    }
}
