package com.dev.controller;

import com.dev.dto.UserSignInDto;
import com.dev.model.User;
import com.dev.service.UserService;
import com.dev.util.ValidateUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sign-in")
@Slf4j
public class SignInController{
    private UserService userService;

    @Autowired
    public SignInController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getLoginPage(Model model) {
        model.addAttribute("user", new User());
        return "sign-in";
    }

    @PostMapping
    public String signIn(@ModelAttribute("user") UserSignInDto userDto, HttpServletResponse resp) {
        ValidateUtil.validateLoginParameters(userDto.getUsername(), userDto.getPassword());
        Cookie cookie = userService.signIn(userDto);
        resp.addCookie(cookie);
        return "redirect:/";
    }
}