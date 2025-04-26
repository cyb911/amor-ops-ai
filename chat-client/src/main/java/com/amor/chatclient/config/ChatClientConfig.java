package com.amor.chatclient.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    private final String prompts = "你是一位某系统助理，你需要遵循一下规则：" +
            "1:当用户需要获取系统验证码时，调用获取系统验证码tool时，你的请求tool参数应该时应该是标准的UUID.";

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools) {
        return chatClientBuilder.defaultSystem(prompts).defaultTools(tools)
                .build();
    }

}
