package com.amor.chatclient.service.chat;


import cn.hutool.core.util.StrUtil;
import com.amor.chatclient.po.ChatHistoryEntity;
import com.amor.chatclient.repository.ChatHistoryRepository;
import lombok.Getter;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

import java.beans.PropertyChangeSupport;
import java.util.*;

@Service
public class ChatHistoryService {

    public static final String CHAT_HISTORY_CHANGE_EVENT = "CHAT_HISTORY_CHANGE_EVENT";
    public static final String CHAT_HISTORY_SELECT_EVENT = "CHAT_HISTORY_SELECT_EVENT";
    public static final String EMPTY_CHAT_HISTORY_EVENT = "EMPTY_CHAT_HISTORY_EVENT";

    private final ChatMemory chatMemory;

    private final ChatHistoryRepository chatHistoryRepository;

    @Getter
    private final PropertyChangeSupport chatHistoryChangeSupport;

    public ChatHistoryService(ChatMemory chatMemory,ChatHistoryRepository chatHistoryRepository) {
        this.chatMemory = chatMemory;
        this.chatHistoryRepository = chatHistoryRepository;
        this.chatHistoryChangeSupport = new PropertyChangeSupport(this);
    }

    public void updateChatHistory(ChatHistory chatHistory) {
        String title = chatHistory.getTitle();
        chatHistory.setUpdateTimestamp(System.currentTimeMillis());
        ChatHistory chatHistoryOld = chatHistoryRepository.getChatHistoryEntitiesByTitle(title);
        if(chatHistoryOld == null){
            ChatHistoryEntity entity = toEntity(chatHistory);
            chatHistoryRepository.save(entity);
        } else {
            ChatHistoryEntity entity = toEntity(chatHistoryOld);
            entity.setTitle(chatHistory.getTitle());
            entity.setChatOptions(chatHistory.getChatOptions());
            chatHistoryRepository.save(entity);
        }
        this.chatHistoryChangeSupport.firePropertyChange(CHAT_HISTORY_CHANGE_EVENT, null, chatHistory);
    }

    public List<ChatHistory> getChatHistoryList() {
        List<ChatHistoryEntity> entities = chatHistoryRepository.findAll();
        return entities.stream()
                .map(this::toDomain)
                .sorted(Comparator.comparingLong(ChatHistory::getUpdateTimestamp).reversed())
                .toList();
    }

    private List<Message> getMessageList(String chatId) {
        return Optional.ofNullable(this.chatMemory.get(chatId, Integer.MAX_VALUE)).orElseGet(List::of);
    }

    public void deleteChatHistory(String chatId) {
        this.chatMemory.clear(chatId);
        chatHistoryRepository.deleteById(chatId);
    }

    public ChatHistory createChatHistory(String systemPrompt, ChatOptions defaultOptions) {
        long createTimestamp = System.currentTimeMillis();
        String chatHistoryId = "ChatHistory-" + UUID.randomUUID();
        ChatHistory chatHistory = new ChatHistory(
                chatHistoryId,"", createTimestamp, createTimestamp, systemPrompt, defaultOptions,
                () -> getMessageList(chatHistoryId)
        );
        updateChatHistory(chatHistory); // 创建完直接保存
        return chatHistory;
    }

    private ChatHistoryEntity toEntity(ChatHistory history) {
        ChatHistoryEntity entity = new ChatHistoryEntity();
        entity.setChatId(history.getChatId());
        entity.setTitle(history.getTitle());
        entity.setCreateTimestamp(history.getCreateTimestamp());
        entity.setUpdateTimestamp(history.getUpdateTimestamp());
        entity.setSystemPrompt(history.getSystemPrompt());
        entity.setChatOptions(history.getChatOptions());
        return entity;
    }

    private ChatHistory toDomain(ChatHistoryEntity entity) {
        return new ChatHistory(
                entity.getChatId(),
                entity.getTitle(),
                entity.getCreateTimestamp(),
                entity.getUpdateTimestamp(),
                entity.getSystemPrompt(),
                entity.getChatOptions(),
                () -> getMessageList(entity.getChatId())
        );
    }

}
