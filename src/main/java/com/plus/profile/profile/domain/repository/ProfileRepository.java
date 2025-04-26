package com.plus.profile.profile.domain.repository;


import com.plus.profile.profile.domain.MyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<MyProfile, UUID> {
    @Modifying
    @Query("UPDATE MyProfile p SET p.viewCount = p.viewCount + :count WHERE p.id = :id")
    void incrementViewCount(@Param("id") UUID id, @Param("count") long count);

}
