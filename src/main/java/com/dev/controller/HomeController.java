package com.dev.controller;

import com.dev.service.LocationService;
import com.dev.service.SessionService;
import com.dev.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final LocationService locationService;
    private final SessionService sessionService;

    @GetMapping
    public String getHomePage(HttpServletRequest request, Model model) {
        UUID sessionId = CookieUtils.getSessionId(request);
        int id = sessionService.getUserIdBySessionId(sessionId);
        model.addAttribute("locations", locationService.getSaveLocations(id));
        return "index";
    }

    @PostMapping()
    public String deleteLocation(@RequestParam(value = "locationId", required = false) int locationId) {
        locationService.deleteLocation(locationId);
        return "redirect:/";
    }
}
