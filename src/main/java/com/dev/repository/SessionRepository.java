package com.dev.repository;

import com.dev.model.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class SessionRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SESSION = "INSERT INTO sessions (id, user_id, expires_at) VALUES (?, ?, ?)";
    private static final String DELETE_SESSION = "DELETE FROM sessions WHERE id = ?";
    private static final String DELETE_OLD_SESSIONS = "DELETE FROM sessions WHERE expires_at < ?";
    public static final String FIND_SESSION = "SELECT id as uuid, user_id, expires_at FROM sessions WHERE id = ?";

    public void create(Session session) {
        log.info("Creating session {}", session);
        jdbcTemplate.update(INSERT_SESSION, session.getUuid(), session.getUserId(), session.getExpiresAt());
    }

    public void delete(UUID id) {
        log.info("Deleting session {}", id);
        jdbcTemplate.update(DELETE_SESSION, id);
    }

    public Session getById(UUID id) {
        log.info("Get session by id: {}", id);
        return jdbcTemplate.query(FIND_SESSION, new BeanPropertyRowMapper<>(Session.class), id)
                .stream().findFirst().orElse(null);
    }

    public void deleteOldSessions() {
        log.info("Deleting old sessions");
        jdbcTemplate.update(DELETE_OLD_SESSIONS, LocalDateTime.now());
    }
}