package com.dev.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenWeatherGeoDto {
    private String name;
    private String local_names;
    private String country;
    private String state;
    private BigDecimal latitude;
    private BigDecimal longitude;
}