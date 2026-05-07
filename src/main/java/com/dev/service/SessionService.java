package com.dev.service;

import com.dev.model.Session;
import com.dev.repository.SessionRepository;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.dev.util.ProjectConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {
    private final SessionRepository sessionRepository;

    public Cookie createSession(Integer userId) {
        UUID uuid = UUID.randomUUID();
        Session session = Session.builder()
                .uuid(uuid)
                .userId(userId)
                .expiresAt(LocalDateTime.now().plusMinutes(SESSION_EXPIRATION_TIME_IN_MINUTES))
                .build();
        sessionRepository.create(session);
        return createCookie(uuid);
    }

    public boolean hasSession(UUID sessionId) {
        log.info("Checking if session exists with id {}", sessionId);
        return sessionRepository.getById(sessionId) != null;
    }

    public void deleteSession(UUID sessionId) {
        log.info("Deleting session with id {}", sessionId);
        sessionRepository.delete(sessionId);
    }

    @Scheduled(fixedDelay = 600000)
    public void deleteOldSessions() {
        sessionRepository.deleteOldSessions();
    }

    private Cookie createCookie(UUID uuid) {
        Cookie cookie = new Cookie(SESSION_ID, String.valueOf(uuid));
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_EXPIRATION_TIME_IN_SECONDS);
        cookie.setHttpOnly(true);
        return cookie;
    }

    public Cookie deleteCookie(String sessionId) {
        Cookie cookie = new Cookie(SESSION_ID, sessionId);
        cookie.setMaxAge(DELETE_COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
