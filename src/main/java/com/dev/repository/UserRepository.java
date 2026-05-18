package com.dev.repository;

import com.dev.model.User;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private static final String SELECT_USER_BY_LOGIN = "SELECT id, login as username, password FROM users WHERE login = ?";

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
        log.debug("Executing SQL request to saving user: {}", user);
        return simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
    }

    public Optional<User> findByName(String username) {
        log.debug("Executing SQL request to find user by name: {}", username);
        return jdbcTemplate.query(SELECT_USER_BY_LOGIN,
                        new BeanPropertyRowMapper<>(User.class),
                        username)
                .stream().findFirst();
    }
}
