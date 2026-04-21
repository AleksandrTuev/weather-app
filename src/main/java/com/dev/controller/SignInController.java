package com.dev.controller;

import com.dev.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sign-in")
public class SignInController{

    @GetMapping
    public String getLoginPage(Model model) {
        model.addAttribute("user", new User());
        return "sign-in";
    }

    @PostMapping
    public String signIn(@ModelAttribute("user") User user) {
        //todo сохранение пользователя
        //todo в случаи невалидных данных редирект на "redirect:sign-in-with-errors"
        return "redirect:/";
    }
}
