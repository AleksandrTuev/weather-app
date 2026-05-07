package com.dev.controller;

import com.dev.service.SessionService;
import com.dev.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

import static com.dev.util.ProjectConstants.SESSION_ID;

@Controller
@RequestMapping("/sign-out")
@RequiredArgsConstructor
public class SignOutController {
    private final UserService userService;
    private final SessionService sessionService;

    @PostMapping
    public String signOut(HttpServletRequest req, HttpServletResponse resp) {

        Cookie newCookie = Arrays.stream(req.getCookies())
                .filter(cookie -> SESSION_ID.equals(cookie.getName()))
                .findFirst().orElse(null);

        String sessionId = newCookie != null ? newCookie.getValue() : null;

        if ((newCookie != null) && (sessionId != null)) {
            userService.signOut(newCookie.getValue());
            resp.addCookie(sessionService.deleteCookie(sessionId));
        }
        return "redirect:/";
    }
}