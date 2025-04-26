package com.plus.profile.profile.infra.impl;

import com.plus.profile.profile.domain.ProfileView;
import com.plus.profile.profile.domain.QProfile;
import com.plus.profile.profile.domain.repository.ProfileViewRepository;
import com.plus.profile.profile.infra.ProfileRepositoryCustom;
import com.plus.profile.profile.presentation.dto.ProfileDetailResponse;
import com.plus.profile.profile.presentation.dto.QProfileDetailResponse;
import com.plus.profile.profile.scheduler.ProfileViewBatchService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static com.plus.profile.profile.domain.QProfile.profile;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryCustomImpl implements ProfileRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    @Override
    public Optional<ProfileDetailResponse> findProfileById(UUID id) {
        ProfileDetailResponse profileDetailResponse = queryFactory.select(new QProfileDetailResponse(
                        profile
                ))
                .from(profile)
                .where(profile.id.eq(id))
                .fetchOne();
        if(profileDetailResponse == null) {
            return Optional.empty();
        }
        return Optional.of(profileDetailResponse);
    }
}
