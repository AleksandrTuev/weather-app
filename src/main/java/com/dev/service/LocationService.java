package com.dev.service;

import com.dev.dto.OpenWeatherCityDto;
import com.dev.dto.OpenWeatherGeoDto;
import com.dev.model.Location;
import com.dev.repository.LocationRepository;
import com.dev.util.OpenWeatherParser;
import com.dev.util.ProjectConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {
    private final RestTemplate restTemplate;
    private final SessionService sessionService;
    private final LocationRepository locationRepository;
    @Value("${api.key}")
    private String apiKey;

    private static final String URL_GEO = "http://api.openweathermap.org/geo/1.0/direct";
    private static final String URL_CITY = "http://api.openweathermap.org/data/2.5/weather";
    private static final int LIMIT = 5;

    public void addLocation(OpenWeatherGeoDto geoDto, HttpServletRequest request) {
        int userId = sessionService.getUserIdBySessionId(getSessionId(request));
        Location location = Location.builder()
                .name(geoDto.getName())
                .userId(userId)
                .latitude(geoDto.getLatitude())
                .longitude(geoDto.getLongitude())
                .build();
        log.info("Adding location: {}", location);
        locationRepository.create(location);
    }

    public List<OpenWeatherGeoDto> getSearchedLocations(String nameLocation) {
        String apiUrl = UriComponentsBuilder
                .fromUriString(URL_GEO)
                .queryParam("q", nameLocation)
                .queryParam("units", "metric")
                .queryParam("limit", LIMIT)
                .queryParam("APPID", apiKey)
                .build().toUriString();

        String text = restTemplate.getForEntity(apiUrl, String.class).getBody();
        log.info("Getting searched locations");
        return OpenWeatherParser.parseListInputData(text);
    }

    public List<OpenWeatherCityDto> getSaveLocations() {
        Integer id = Integer.parseInt(MDC.get("userId"));
        Set<OpenWeatherCityDto> set = new HashSet<>();

        if (id != null) {
            List<Location> locations = locationRepository.findAll(id);
            locations.forEach(location -> {
                String apiUrl = UriComponentsBuilder
                        .fromUriString(URL_CITY)
                        .queryParam("lat", location.getLatitude())
                        .queryParam("lon", location.getLongitude())
                        .queryParam("units", "metric")
                        .queryParam("APPID", apiKey)
                        .build().toUriString();

                String text = restTemplate.getForEntity(apiUrl, String.class).getBody();
                OpenWeatherCityDto cityDto = OpenWeatherParser.parseInputData(text);
                cityDto.setId(location.getId());
                set.add(cityDto);
            });
        }
        log.info("Getting saved locations");
        return set.stream().toList();
    }

    private UUID getSessionId(HttpServletRequest request) {
        Cookie newCookie = Arrays.stream(request
                        .getCookies())
                .filter(cookie -> cookie.getName().equals(ProjectConstants.SESSION_ID))
                .findFirst().orElseThrow();
        return UUID.fromString(newCookie.getValue());
    }

    public void deleteLocation(int locationId) {
        locationRepository.delete(locationId);
        log.info("Deleted location with id: {}", locationId);
    }
}