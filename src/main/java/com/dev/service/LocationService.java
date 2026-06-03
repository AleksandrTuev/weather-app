package com.dev.service;

import com.dev.dto.OpenWeatherCityDto;
import com.dev.dto.OpenWeatherGeoDto;
import com.dev.model.Location;
import com.dev.repository.LocationRepository;
import com.dev.util.CookieUtils;
import com.dev.util.OpenWeatherParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Value("${url.geo}")
    private String URL_GEO;
    @Value("${url.city}")
    private String URL_CITY;
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
                .build()
                .toUriString();

        String text = restTemplate.getForEntity(apiUrl, String.class).getBody();
        log.info("Getting searched locations");
        return OpenWeatherParser.parseListInputData(text);
    }

    public List<OpenWeatherCityDto> getSaveLocations(int userId) {
        List<OpenWeatherCityDto> list = new ArrayList<>();

        List<Location> locations = locationRepository.findAll(userId);
        locations.forEach(location -> {
            String apiUrl = UriComponentsBuilder
                    .fromUriString(URL_CITY)
                    .queryParam("lat", location.getLatitude())
                    .queryParam("lon", location.getLongitude())
                    .queryParam("units", "metric")
                    .queryParam("APPID", apiKey)
                    .build()
                    .toUriString();

            String text = restTemplate.getForEntity(apiUrl, String.class).getBody();
            OpenWeatherCityDto cityDto = OpenWeatherParser.parseInputData(text);
            cityDto.setId(location.getId());
            cityDto.setNameLocation(location.getName());
            list.add(cityDto);
        });
        Collections.reverse(list);
        Set<OpenWeatherCityDto> set = new LinkedHashSet<>(list);
        log.info("Getting saved locations");
        return set.stream().toList();
    }

    private UUID getSessionId(HttpServletRequest request) {
        log.info("Getting session id");
        return CookieUtils.getSessionId(request);
    }

    public void deleteLocation(int locationId) {
        locationRepository.delete(locationId);
        log.info("Deleted location with id: {}", locationId);
    }
}