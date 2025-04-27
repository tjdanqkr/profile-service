package com.plus.profile.point.domain.repository;

import com.plus.profile.point.domain.UserPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {
    Optional<UserPoint> findByUserId(UUID userId);
}
