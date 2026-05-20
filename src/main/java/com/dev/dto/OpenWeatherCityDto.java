package com.dev.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenWeatherCityDto {
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private int id;
    private String nameLocation;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private int temperature;
    private int temperatureFeelsLike;
    private String description;
    private int humidity;
    private String nameIcon;
}