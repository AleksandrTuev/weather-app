package com.dev.util;

import com.dev.exception.SessionNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static com.dev.util.ProjectConstants.SESSION_ID;

@UtilityClass
@Slf4j
public class CookieUtils {
    public static UUID getSessionId(HttpServletRequest req) {
        return Optional.ofNullable(req.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> SESSION_ID.equals(cookie.getName()))
                        .findFirst())
                .map(Cookie::getValue)
                .flatMap(CookieUtils::parseUUID)
                .orElseThrow(
                        () -> new SessionNotFoundException("Active session not found")
                );
    }

    private Optional<UUID> parseUUID(String uuidString) {
        try {
            return Optional.of(UUID.fromString(uuidString));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format in cookie: {}", uuidString);
            return Optional.empty();
        }
    }
}
