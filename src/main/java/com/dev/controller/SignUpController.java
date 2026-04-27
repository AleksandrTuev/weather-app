package com.dev.controller;

import com.dev.model.User;
import com.dev.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sign-up")
public class SignUpController {

    private final UserService userService;

    @Autowired
    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "sign-up";
    }

    @PostMapping
    public String signUp(@ModelAttribute("user") User user, HttpServletRequest req, HttpServletResponse resp) {
        //todo в случаи невалидных данных редирект на "redirect:sign-up-with-errors"
        userService.signUp(user, req, resp);
        return "redirect:/";
    }
}
