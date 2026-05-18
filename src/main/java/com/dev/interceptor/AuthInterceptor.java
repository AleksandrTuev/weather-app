package com.dev.interceptor;

import com.dev.exception.SessionNotFoundException;
import com.dev.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static com.dev.util.ProjectConstants.SESSION_ID;

@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {
    private final SessionService sessionService;
    private static final String MDC_USER_ID_KEY = "userId";

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
            UUID uuid = UUID.fromString(newCookie.getValue());

            Integer id = sessionService.getUserIdBySessionId(uuid);
            MDC.put(MDC_USER_ID_KEY, String.valueOf(id));
            log.info("Checking session: {}", newCookie.getValue());

            if (!sessionService.hasSession(uuid)) {
                throw new SessionNotFoundException("Session not found");
            }

            resolveUserIdentifier(id);

        } catch (IllegalArgumentException | SessionNotFoundException e) {
            log.error("Session with id {} not found", newCookie.getValue());

            newCookie = sessionService.deleteCookie(newCookie.getValue());
            resp.addCookie(newCookie);
            redirectToLogin(req, resp);
            return false;
        }
        return true;
    }

    private void resolveUserIdentifier(Integer userId) {
        if (userId != null) {
            MDC.put(MDC_USER_ID_KEY, String.valueOf(userId));
        } else {
            MDC.put(MDC_USER_ID_KEY, "anonymous");
        }
    }

    private void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/sign-in");
    }



    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        MDC.remove(MDC_USER_ID_KEY);
    }
}