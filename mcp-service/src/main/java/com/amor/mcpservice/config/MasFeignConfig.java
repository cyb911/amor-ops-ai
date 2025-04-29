package com.amor.mcpservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MasFeignConfig implements RequestInterceptor {

    @Value("${taskflow.auth}")
    private String auth;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String url = requestTemplate.feignTarget().url();
        // 检查URL是否包含允许的域名
        if (url.contains("mas.minthgroup.com")) {
            requestTemplate.header("Authorization", auth);
        }
    }
}
