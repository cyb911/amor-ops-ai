package com.amor.mcpservice.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WeatherService {
    @Tool(description = "根据城市名称获取天气预报")
    public String getWeatherByCity(@ToolParam(description = "城市名") String city) {
        Map<String,String> testData = Map.of(
                "上海","暴雨",
                "安吉","阴",
                "杭州","多云",
                "南京","小雨"
        );
        return testData.getOrDefault(city,"未查询到相关信息。");
    }
}
