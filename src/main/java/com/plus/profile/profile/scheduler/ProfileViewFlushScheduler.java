package com.plus.profile.profile.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileViewFlushScheduler {
    private final ProfileViewBatchService profileViewBatchService;

    @Scheduled(fixedDelay = 1000)
    public void flush() {
        profileViewBatchService.flushViewsToDatabase();
    }
}
