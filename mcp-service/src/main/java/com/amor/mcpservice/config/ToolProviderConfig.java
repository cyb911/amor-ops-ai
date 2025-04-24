package com.amor.mcpservice.config;

import com.amor.mcpservice.service.*;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 注册工具类
 */
@Configuration
public class ToolProviderConfig {
    @Bean
    public ToolCallbackProvider toolCallbackProvider(List<ToolService> services) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(services.toArray())
                .build();
    }
}
