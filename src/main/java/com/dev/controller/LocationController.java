package com.dev.controller;

import com.dev.dto.OpenWeatherGeoDto;
import com.dev.service.LocationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/search-results")
@RequiredArgsConstructor
@Slf4j
public class LocationController {
    private final LocationService locationService;

    @GetMapping
    public String findLocation(@RequestParam(value = "location", required = false) String name, Model model) {
        List<OpenWeatherGeoDto>list = locationService.getSearchedLocations(name);

        log.info("Get data to location: {}", name);
        log.info("Found {} results", list.size());

        model.addAttribute("locations_list", list);
        return "search-results";
    }

    @PostMapping
    public String addLocation(@ModelAttribute ("location") OpenWeatherGeoDto geoDto, HttpServletRequest request) {
        locationService.addLocation(geoDto, request);
        return "redirect:/";
    }
}
