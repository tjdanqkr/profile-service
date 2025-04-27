package com.plus.profile.user.domain.repository;

import com.plus.profile.user.domain.UserPointLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPointLogRepository extends JpaRepository<UserPointLog, Long> {
}
