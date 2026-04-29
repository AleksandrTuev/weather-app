package com.dev.repository;

import com.dev.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private static final String SELECT_USER_BY_LOGIN = "SELECT * FROM users WHERE login = ?";

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate,
                          @Qualifier("userInsert") SimpleJdbcInsert simpleJdbcInsert) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = simpleJdbcInsert;
    }

    public int save(User user) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("login", user.getUsername());
        parameters.put("password", user.getPassword());

        return simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
    }

    public Optional<User> findByName(String username) {
        return jdbcTemplate.query(SELECT_USER_BY_LOGIN,
                        new Object[]{username},
                        new BeanPropertyRowMapper<>(User.class))
                .stream().findFirst();
    }
}
