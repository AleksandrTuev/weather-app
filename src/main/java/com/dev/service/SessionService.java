package com.dev.service;

import com.dev.model.Session;
import com.dev.repository.SessionRepository;
import com.dev.util.CreatorCookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    @Autowired
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void createSession(HttpServletRequest req, HttpServletResponse resp, int userId) {
        UUID uuid = UUID.randomUUID();
        Session session = Session.builder()
                .uuid(uuid)
                .userId(userId)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
        CreatorCookie.create("session_id", String.valueOf(uuid), resp);
        sessionRepository.create(session);
    }
}
