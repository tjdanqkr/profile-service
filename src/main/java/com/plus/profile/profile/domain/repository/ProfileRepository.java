package com.plus.profile.profile.domain.repository;


import com.plus.profile.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

}
