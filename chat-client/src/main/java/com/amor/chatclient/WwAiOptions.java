package com.amor.chatclient;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "spring.ai.playground")
public record WwAiOptions(Chat chat) {

    public record Chat(String systemPrompt, List<String> models, ChatOptions chatOptions) {

    }
}
