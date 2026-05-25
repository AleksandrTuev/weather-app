package com.dev.controller;

import com.dev.exception.SessionNotFoundException;
import com.dev.service.SessionService;
import com.dev.service.UserService;
import com.dev.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/sign-out")
@RequiredArgsConstructor
@Slf4j
public class SignOutController {
    private final UserService userService;
    private final SessionService sessionService;

    @PostMapping
    public String signOut(HttpServletRequest req, HttpServletResponse resp) {
        try {
            UUID sessionId = CookieUtils.getSessionId(req);
            userService.signOut(String.valueOf(sessionId));
            resp.addCookie(sessionService.deleteCookie(String.valueOf(sessionId)));
        } catch (SessionNotFoundException e) {
            log.debug("User session already cleared or invalid. message:{}", e.getMessage());
        }

        return "redirect:/";
    }
}