package com.amor.mcpservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class MasFeignConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        String url = requestTemplate.feignTarget().url();
        // 检查URL是否包含允许的域名
        if (url.contains("mas.minthgroup.com")) {
            requestTemplate.header("Authorization", "Bearer ktqMjxwWolrJ-Ps7eJrXdAq5MmFqGhj8IsadcGiwsyqyQnhn50TFtiGFG03aKUgo8iZkjK-0QMRJC9CJ4FhWI7WpVkFSGL2pvs1IFXAzG8z8FyyKuG6UJTSGsOiz01xh");
        }
    }
}
