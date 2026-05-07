package com.dev.interceptor;

import com.dev.exception.SessionNotFoundException;
import com.dev.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static com.dev.util.ProjectConstants.SESSION_ID;

@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {

        if ((req.getCookies() == null)) {
            redirectToLogin(req, resp);
            return false;
        }

        Cookie newCookie = Arrays.stream(req.getCookies())
                .filter(cookie -> SESSION_ID.equals(cookie.getName()))
                .findFirst()
                .orElse(null);

        if ((newCookie == null) || (newCookie.getValue() == null)) {
            redirectToLogin(req, resp);
            return false;
        }

        try {
            log.info("Checking session: {}", newCookie.getValue());
            UUID uuid = UUID.fromString(newCookie.getValue());
            if (!sessionService.hasSession(uuid)) {
                throw new SessionNotFoundException("Session not found");
            }
        } catch (IllegalArgumentException | SessionNotFoundException e) {
            log.info("Has no session: {}", newCookie.getValue());

            newCookie = sessionService.deleteCookie(newCookie.getValue());
            resp.addCookie(newCookie);
            redirectToLogin(req, resp);
            return false;
        }
        return true;
    }

    private void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/sign-in");
    }
}