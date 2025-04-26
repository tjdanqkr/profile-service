package com.plus.profile.user.infra.impl;

import com.plus.profile.user.domain.User;
import com.plus.profile.user.infra.UserRepositoryCustom;
import com.plus.profile.user.presentation.dto.UserDetailResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<UserDetailResponse> findUserWithPointById(UUID userId) {

        return Optional.empty();
    }
}
