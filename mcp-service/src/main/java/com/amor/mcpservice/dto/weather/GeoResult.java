package com.amor.mcpservice.dto.weather;

import lombok.Data;

import java.util.List;

@Data
public class GeoResult {

    private String code;

    private List<Location> location;

}
