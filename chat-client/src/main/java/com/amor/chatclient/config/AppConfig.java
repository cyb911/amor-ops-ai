package com.amor.chatclient.config;


import com.amor.chatclient.repository.MongoDbChatMemoryRepository;
import com.amor.chatclient.service.chat.MonogoDbChatMemory;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final MongoDbChatMemoryRepository chatMemoryRepository;

    @Bean
    @ConditionalOnMissingBean(ChatMemory.class)
    public ChatMemory chatMemory() {
        return new MonogoDbChatMemory(chatMemoryRepository);
    }


}
