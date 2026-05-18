package com.dev.util;

import com.dev.dto.OpenWeatherCityDto;
import com.dev.dto.OpenWeatherGeoDto;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class OpenWeatherParser {

    public static OpenWeatherCityDto parseInputData(String input) {
        JsonNode root = getJsonNode(input);

        return OpenWeatherCityDto.builder()
                .nameLocation(root.get("name").asString())
                .country(root.get("sys").get("country").asString())
                .temperature(root.get("main").get("temp").asInt())
                .temperatureFeelsLike(root.get("main").get("feels_like").asInt())
                .humidity(root.get("main").get("humidity").asInt())
                .latitude(root.get("coord").get("lat").asDecimal())
                .longitude(root.get("coord").get("lon").asDecimal())
                .nameIcon(root.get("weather").get(0).get("icon").asString())
                .description(StringUtils.capitalize(
                        root.get("weather").get(0).get("description").asString())
                )
                .build();
    }

    public static List<OpenWeatherGeoDto> parseListInputData(String input) {
        JsonNode root = getJsonNode(input);
        List<OpenWeatherGeoDto> list = new ArrayList<>();

        for (int i = 0; (i < root.size() && i < 5); i++) {
            list.add(OpenWeatherGeoDto.builder()
                    .name(root.get(i).get("name").asString())
                    .local_names(root.has("local_names") ? root.get("local_names").get("ru").asString() : "-")
                    .country(root.get(i).has("country") ? root.get(i).get("country").asString() : "-")
                    .state(root.get(i).has("state") ? root.get(i).get("state").asString() : "-")
                    .latitude(root.get(i).get("lat").asDecimal())
                    .longitude(root.get(i).get("lon").asDecimal())
                    .build()
            );
        }
        return list;
    }

    private JsonNode getJsonNode(String json) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }
}