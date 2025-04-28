package com.amor.mcpservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class WeatherConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String url = requestTemplate.feignTarget().url();
        // 检查URL是否包含允许的域名
        if (!url.contains("re.qweatherapi.com")) {
            return;
        }
        requestTemplate.query("key", "b376bdbec5b64f6ca1398a78772ee918");
    }
}
