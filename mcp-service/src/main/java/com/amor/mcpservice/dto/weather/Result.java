package com.amor.mcpservice.dto.weather;

import lombok.Data;

import java.util.List;

@Data
public class Result {

    private String code;

    private String updateTime;

    private String fxLink;

    private List<Hourly> hourly;

}
