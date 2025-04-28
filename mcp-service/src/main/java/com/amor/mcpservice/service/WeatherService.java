package com.amor.mcpservice.service;

import cn.hutool.http.HttpRequest;
import com.amor.mcpservice.dto.weather.GeoResult;
import com.amor.mcpservice.dto.weather.Result;
import com.amor.mcpservice.feign.WeatherFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherService implements ToolService {
    private final String baseUrl = "https://p75cttvkfb.re.qweatherapi.com";

    private final String apiKey = "b376bdbec5b64f6ca1398a78772ee918";

    private final WeatherFeign weatherFeign;

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
    public GeoResult lookUpCity(@ToolParam(description = "查询地区的名称，支持文字、以英文逗号分隔的经度,纬度坐标（十进制，最多支持小数点后两位）、LocationID或Adcode（仅限中国城市）") String location) {
        try {
            return weatherFeign.lookup(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Tool(description = "提供全球城市未来24-168小时逐小时天气预报，包括：温度、天气状况、风力、风速、风向、相对湿度、大气压强、降水概率、露点温度、云量。")
    public Result weather24h72h168h(@ToolParam(description = "查询未来天气类型：24h,72h,168h") String type,
                                    @ToolParam(description = "查询地区的名称，支持文字、以英文逗号分隔的经度,纬度坐标（十进制，最多支持小数点后两位）、LocationID或Adcode（仅限中国城市）") String location) {
        try {
            if("24h".equals(type)){
                Result result = weatherFeign.weather24(location,null,null);
                System.err.println("查询了为了24小时天气");
                return result;
            }

            if("72h".equals(type)){
                Result result = weatherFeign.weather72(location,null,null);
                System.err.println("查询了为了72小时天气");
                return result;
            }


            if("168h".equals(type)){
                Result result = weatherFeign.weather168(location,null,null);
                System.err.println("查询了为了168小时天气");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
