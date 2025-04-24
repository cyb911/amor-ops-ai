package com.amor.mcpservice.config;

import com.amor.mcpservice.service.EmployeeService;
import com.amor.mcpservice.service.TaskflowService;
import com.amor.mcpservice.service.WeatherService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 注册工具类
 */
@Configuration
public class ToolProviderConfig {
    @Bean
    public ToolCallbackProvider weatherTools(WeatherService service){
        return MethodToolCallbackProvider.builder().toolObjects(service).build();
    }

    /**
     * 员工信息查询工具
     */
    @Bean
    public ToolCallbackProvider employeeTools(EmployeeService service){
        return MethodToolCallbackProvider.builder().toolObjects(service).build();
    }

    @Bean
    public ToolCallbackProvider taskflowTools(TaskflowService service){
        return MethodToolCallbackProvider.builder().toolObjects(service).build();
    }
}
