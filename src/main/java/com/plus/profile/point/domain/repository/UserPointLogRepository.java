package com.plus.profile.point.domain.repository;

import com.plus.profile.point.domain.UserPointLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPointLogRepository extends JpaRepository<UserPointLog, Long> {
}
