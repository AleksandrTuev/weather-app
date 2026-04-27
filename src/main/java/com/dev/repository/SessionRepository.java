package com.dev.repository;

import com.dev.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SessionRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SessionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(Session session) {
        String sql = "insert into sessions (id, user_id, expires_at) values (?, ?, ?)";
        jdbcTemplate.update(sql, session.getUuid(), session.getUserId(), session.getExpiresAt());
    }

}
