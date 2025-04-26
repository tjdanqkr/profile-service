package com.plus.profile.profile.scheduler;


import com.plus.profile.profile.domain.ProfileView;
import com.plus.profile.profile.domain.repository.ProfileRepository;
import com.plus.profile.profile.domain.repository.ProfileViewRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileViewBatchService {
    private final ProfileRepository profileRepository;
    private final ProfileViewRepository profileViewRepository;
    @Getter
    private final ConcurrentHashMap<UUID, Long> viewMap = new ConcurrentHashMap<>();

    @Async
    public synchronized void addView(UUID profileId) {
        viewMap.merge(profileId, 1L, Long::sum);
        profileViewRepository.save(ProfileView.of(profileId));
    }


    public void flushViewsToDatabase() {
        viewMap.forEach(profileRepository::incrementViewCount);
        viewMap.clear();
    }

}
