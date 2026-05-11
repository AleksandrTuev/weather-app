package com.dev.scheduler;

import com.dev.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionCleanupScheduler {
    private final SessionRepository sessionRepository;

    @Scheduled(fixedDelay = 120000, initialDelay = 60000)
    public void deleteOldSessions() {
        sessionRepository.deleteOldSessions();
    }
}
