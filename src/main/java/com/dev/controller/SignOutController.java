package com.dev.controller;

import com.dev.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.dev.util.ProjectConstants.SESSION_ID;

@Controller
@RequestMapping("/sign-out")
@RequiredArgsConstructor
public class SignOutController {

    @PostMapping
    public String signOut(HttpServletResponse resp) {
        //todo удалять сессию из БД сразу. Шедулед оставим
        Cookie cookie = new Cookie(SESSION_ID, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        return "redirect:/";
    }
}