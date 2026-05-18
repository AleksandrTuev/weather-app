package com.dev.controller;

import com.dev.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final LocationService locationService;

    @GetMapping
    public String getHomePage(Model model) {
        model.addAttribute("locations", locationService.getSaveLocations());
        return "index";
    }

    @PostMapping()
    public String deleteLocation(@RequestParam(value = "locationId", required = false) int locationId) {
        locationService.deleteLocation(locationId);
        return "redirect:/";
    }
}
