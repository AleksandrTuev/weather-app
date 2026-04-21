package com.dev.controller;

import com.dev.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sign-up")
public class SignUpController {

    @GetMapping
    public String getRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "sign-up";
    }

    @PostMapping
    public String signUp(@ModelAttribute("user") User user) {
        //todo в случаи невалидных данных редирект на "redirect:sign-up-with-errors"
        return "redirect:/";
    }
}
