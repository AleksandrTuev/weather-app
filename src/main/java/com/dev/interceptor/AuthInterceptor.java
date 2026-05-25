package com.dev.interceptor;

import com.dev.exception.SessionNotFoundException;
import com.dev.service.SessionService;
import com.dev.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {
    private final SessionService sessionService;
    private static final String MDC_USER_ID_KEY = "userId";

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        try {
            UUID sessionId = CookieUtils.getSessionId(req);
            int id = sessionService.getUserIdBySessionId(sessionId);
            MDC.put(MDC_USER_ID_KEY, String.valueOf(id));
            log.info("Checking session: {}", sessionId);

            resolveUserIdentifier(id);
        } catch (SessionNotFoundException e) {
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