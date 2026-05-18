package com.dev.repository;

import com.dev.model.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class LocationRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_LOCATION = "INSERT INTO locations (name, user_id, latitude, longitude) VALUES (?, ?, ?, ?)";
    public static final String FIND_LOCATION = "SELECT id, name, user_id, latitude, longitude FROM locations WHERE user_id = ?";
    public static final String DELETE_LOCATION = "DELETE FROM locations WHERE id = ?";

    public void create(Location location) {
        log.info("Creating session {}", location);
        jdbcTemplate.update(INSERT_LOCATION, location.getName(),
                location.getUserId(), location.getLatitude(), location.getLongitude());
    }

    public List<Location> findAll(Integer userId) {
        return jdbcTemplate.query(FIND_LOCATION, new BeanPropertyRowMapper<>(Location.class), userId);
    }

    public void delete(int locationId) {
        jdbcTemplate.update(DELETE_LOCATION, locationId);
    }
}