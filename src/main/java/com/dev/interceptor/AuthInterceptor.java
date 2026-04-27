package com.dev.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor {
    private static final String SESSION_ID = "session_id";
    private static final String USER_ID = "user_id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        if ((uri.contains("/sign-in")) || (uri.contains("/sign-up"))){
            return true;
        }

        Cookie[] cookies = request.getCookies();
        if ((cookies == null) || (Arrays.stream(cookies)
                .filter(cookie -> USER_ID.equals(cookie.getName()))
                .findFirst()
                .isEmpty())) {
            response.sendRedirect(request.getContextPath() + "/sign-in");
            return false;
        }
        return true;
    }
}
