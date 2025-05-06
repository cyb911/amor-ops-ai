package com.amor.chatclient.service.chat;

import com.amor.chatclient.po.MessageEntity;
import com.amor.chatclient.repository.MongoDbChatMemoryRepository;
import com.amor.chatclient.utils.MessageConverter;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.util.*;
import java.util.stream.Collectors;

public class MonogoDbChatMemory implements ChatMemory {

    private final MongoDbChatMemoryRepository chatMemoryRepository;

    public MonogoDbChatMemory(MongoDbChatMemoryRepository chatMemoryRepository) {
        this.chatMemoryRepository = chatMemoryRepository;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<MessageEntity> entities = messages.stream()
                .map(msg -> MessageConverter.toEntity(conversationId, msg))
                .collect(Collectors.toList());
        chatMemoryRepository.saveAll(entities);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<MessageEntity> entities = chatMemoryRepository.findByConversationId(conversationId);

        return entities.stream()
                .sorted(Comparator.comparing(MessageEntity::getId)) // 保证按顺序
                .skip(Math.max(0, entities.size() - lastN))
                .map(MessageConverter::toMessage)
                .collect(Collectors.toList());
    }

    @Override
    public void clear(String conversationId) {
        chatMemoryRepository.deleteByConversationId(conversationId);
    }
}
