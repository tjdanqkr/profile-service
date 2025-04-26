package com.plus.profile.profile.infra.impl;

import com.plus.profile.profile.infra.ProfileRepositoryCustom;
import com.plus.profile.profile.presentation.dto.ProfileDetailResponse;
import com.plus.profile.profile.presentation.dto.ProfileResponse;
import com.plus.profile.profile.presentation.dto.QProfileDetailResponse;
import com.plus.profile.profile.presentation.dto.QProfileResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.plus.profile.profile.domain.QMyProfile.myProfile;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryCustomImpl implements ProfileRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    @Override
    public Optional<ProfileDetailResponse> findProfileById(UUID id) {
        ProfileDetailResponse profileDetailResponse = queryFactory.select(new QProfileDetailResponse(
                        myProfile
                ))
                .from(myProfile)
                .where(myProfile.id.eq(id))
                .fetchOne();
        if(profileDetailResponse == null) {
            return Optional.empty();
        }
        return Optional.of(profileDetailResponse);
    }

    @Override
    public Page<ProfileResponse> findProfiles(Pageable pageable) {
        Sort sort = pageable.getSort();
        List<ProfileResponse> profiles = queryFactory.select(
                        new QProfileResponse(
                                myProfile
                        )
                ).from(myProfile)
                .where(myProfile.deleted.isFalse())
                .orderBy(
                        toOrderSpecifier(sort)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Long total = queryFactory.select(myProfile.count())
                .from(myProfile)
                .fetchOne();
        if(total == null) {
            total = 0L;
        }
        return new PageImpl<>(profiles, pageable, total);
    }

    private static OrderSpecifier<?> toOrderSpecifier(Sort sort) {
        OrderSpecifier<?> result = orderByUsername(sort);
        if(result != null) return result;
        result = orderByViewCount(sort);
        if(result != null) return result;
        return myProfile.createdAt.desc();
    }
    private static OrderSpecifier<String> orderByUsername(Sort sort) {
        Sort.Order username = sort.getOrderFor("username");
        return username != null ? myProfile.username.asc() : null;
    }
    private static OrderSpecifier<Long> orderByViewCount(Sort sort) {
        Sort.Order viewCount = sort.getOrderFor("viewCount");
        return viewCount != null ? myProfile.viewCount.desc() : null;
    }
}
