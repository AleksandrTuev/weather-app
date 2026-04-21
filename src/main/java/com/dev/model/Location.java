package com.dev.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Location {
    private int id;
    private String name;
    private int userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
