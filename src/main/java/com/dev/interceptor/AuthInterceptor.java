package com.dev.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;

import static com.dev.util.ProjectConstants.*;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
//        String uri = req.getRequestURI();
//
//        if ((uri.contains(PATH_SIGN_IN)) || (uri.contains(PATH_SIGN_UP))){
//            return true;
//        }

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
        return true;
    }

    private void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + PATH_SIGN_IN);
    }
}