package com.dev.service;

import com.dev.exception.SessionNotFoundException;
import com.dev.model.Session;
import com.dev.repository.SessionRepository;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.dev.util.ProjectConstants.*;

@Service
@Slf4j
public class SessionService {
    private final SessionRepository sessionRepository;
    private final long sessionStorageTime;

    @Autowired
    public SessionService(SessionRepository sessionRepository, @Value("${sessionStorageTime}") long sessionStorageTime) {
        this.sessionRepository = sessionRepository;
        this.sessionStorageTime = sessionStorageTime;
    }

    public Cookie createSession(Integer userId) {
        UUID uuid = UUID.randomUUID();
        Session session = Session.builder()
                .uuid(uuid)
                .userId(userId)
                .expiresAt(LocalDateTime.now().plusMinutes(sessionStorageTime))
                .build();
        sessionRepository.create(session);
        return createCookie(uuid);
    }

    public void deleteSession(UUID sessionId) {
        log.info("Deleting session with id {}", sessionId);
        sessionRepository.delete(sessionId);
    }

    public Cookie deleteCookie(String sessionId) {
        Cookie cookie = new Cookie(SESSION_ID, sessionId);
        cookie.setMaxAge(DELETE_COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    public int getUserIdBySessionId(UUID sessionId) {
        log.info("Getting a user id by session id: {}", sessionId);

        List<Session> list = sessionRepository.getById(sessionId);
        if (list.isEmpty()) {
            throw new SessionNotFoundException("Session with id " + sessionId + " not found");
        }
        return list.stream().findFirst().get().getUserId();
    }

    private Cookie createCookie(UUID uuid) {
        Cookie cookie = new Cookie(SESSION_ID, String.valueOf(uuid));
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_EXPIRATION_TIME_IN_SECONDS);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
