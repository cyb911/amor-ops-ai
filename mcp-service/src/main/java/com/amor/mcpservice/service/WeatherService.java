package com.amor.mcpservice.service;

import cn.hutool.http.HttpRequest;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class WeatherService implements ToolService {
    private final String baseUrl = "https://p75cttvkfb.re.qweatherapi.com";

    private final String apiKey = "b376bdbec5b64f6ca1398a78772ee918";

    @Tool(description = "根据地区id(LocationID)获取实时天气预报")
    public String weatherNow(@ToolParam(description = "地区Id(LocationID)") String locationId) {
        String urlString = baseUrl + "/v7/weather/now?location=" + locationId + "&key=" + apiKey;
        try {
            String result= HttpRequest.get(urlString)
                    .timeout(-1)
                    .execute().body();
            System.err.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "无法获取数据！";
    }

    @Tool(description = "城市搜索、位置信息搜索")
    public String lookUpCity(@ToolParam(description = "查询地区的名称，支持文字、以英文逗号分隔的经度,纬度坐标（十进制，最多支持小数点后两位）、LocationID或Adcode（仅限中国城市）") String location) {
        String urlString = baseUrl + "/geo/v2/city/lookup?location=" + location + "&key=" + apiKey;
        try {
            String result= HttpRequest.get(urlString)
                    .timeout(-1)
                    .execute().body();
            System.err.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "无法获取数据！";
    }
}
