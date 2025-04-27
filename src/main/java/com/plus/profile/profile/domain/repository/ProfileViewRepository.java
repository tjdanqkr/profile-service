package com.plus.profile.profile.domain.repository;

import com.plus.profile.profile.domain.ProfileView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProfileViewRepository extends JpaRepository<ProfileView, Long> {
    List<ProfileView> findByMyProfileId(UUID profileId);
}
