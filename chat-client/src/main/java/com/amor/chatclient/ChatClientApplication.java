package com.amor.chatclient;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import org.springframework.ai.mcp.client.autoconfigure.SseHttpClientTransportAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(exclude = SseHttpClientTransportAutoConfiguration.class)//排除spring mcp客户端不使用默认的自动配置
@ConfigurationPropertiesScan
@Push//必须添加该注解，否则Vaadin 不会刷新界面
public class ChatClientApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(ChatClientApplication.class, args);
    }

}
