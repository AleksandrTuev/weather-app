package com.dev.repository;

import com.dev.model.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@Slf4j
public class SessionRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SESSION = "INSERT INTO sessions (id, user_id, expires_at) VALUES (?, ?, ?)";
    private static final String DELETE_SESSION = "DELETE FROM sessions WHERE id = ?";
    private static final String DELETE_OLD_SESSIONS = "DELETE FROM sessions WHERE expires_at < ?";


    @Autowired
    public SessionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(Session session) {
        jdbcTemplate.update(INSERT_SESSION, session.getUuid(), session.getUserId(), session.getExpiresAt());
    }

    public void delete(UUID uuid) {
        jdbcTemplate.update(DELETE_SESSION, uuid);
    }

    @Scheduled(fixedDelay = 60000)
    public void deleteOldSessions() {
        jdbcTemplate.update(DELETE_OLD_SESSIONS, LocalDateTime.now());
    }
}
