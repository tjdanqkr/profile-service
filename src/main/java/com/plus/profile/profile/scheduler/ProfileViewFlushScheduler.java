package com.plus.profile.profile.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileViewFlushScheduler {
    private final ProfileViewBatchService profileViewBatchService;

    @Scheduled(fixedDelay = 3000)
    public void flush() {
        profileViewBatchService.flushViewsToDatabase();
    }
}
