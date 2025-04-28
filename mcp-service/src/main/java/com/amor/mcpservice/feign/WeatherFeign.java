package com.amor.mcpservice.feign;

import com.amor.mcpservice.config.MasFeignConfig;
import com.amor.mcpservice.dto.R;
import com.amor.mcpservice.dto.weather.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "weatherFeign", url = "https://p75cttvkfb.re.qweatherapi.com",
        configuration = {MasFeignConfig.class})
public interface WeatherFeign {
    @GetMapping("/v7/weather/24h")
    Result weather24(@RequestParam(value = "location") String location,
                     @RequestParam(value = "lang",required = false) String lang,
                     @RequestParam(value = "unit",required = false)  String unit);

    @GetMapping("/v7/weather/72h")
    Result weather72(@RequestParam(value = "location") String location,
                                    @RequestParam(value = "lang",required = false) String lang,
                                    @RequestParam(value = "unit",required = false)  String unit);

    @GetMapping("/v7/weather/168h")
    Result weather168(@RequestParam(value = "location") String location,
                                    @RequestParam(value = "lang",required = false) String lang,
                                    @RequestParam(value = "unit",required = false)  String unit);
}
