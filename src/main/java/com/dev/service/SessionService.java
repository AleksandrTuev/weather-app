package com.dev.service;

import com.dev.model.Session;
import com.dev.repository.SessionRepository;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.dev.util.ProjectConstants.*;

@Service
@RequiredArgsConstructor
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

    public Cookie deleteSession(UUID sessionId) {
        sessionRepository.delete(sessionId);
        return deleteCookie(sessionId);
    }

    private Cookie createCookie(UUID uuid) {
        Cookie cookie = new Cookie(SESSION_ID, String.valueOf(uuid));
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_EXPIRATION_TIME_IN_SECONDS);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private Cookie deleteCookie(UUID uuid) {
        Cookie cookie = new Cookie(SESSION_ID, String.valueOf(uuid));
        cookie.setMaxAge(DELETE_COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
