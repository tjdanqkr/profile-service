package com.plus.profile.user.application.impl;

import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.user.application.UserService;
import com.plus.profile.user.exception.UserExceptionCode;
import com.plus.profile.user.infra.UserRepositoryCustom;
import com.plus.profile.user.presentation.dto.UserDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryCustom userRepositoryCustom;
    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(UUID userId) {
        return userRepositoryCustom.findUserDetailById(userId)
                .orElseThrow(() -> new BusinessException(UserExceptionCode.USER_NOT_FOUND));
    }
}
