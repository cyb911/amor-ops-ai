package com.amor.chatclient.repository;

import com.amor.chatclient.po.ChatHistoryEntity;
import com.amor.chatclient.service.chat.ChatHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatHistoryRepository  extends MongoRepository<ChatHistoryEntity, String> {
    ChatHistory getChatHistoryEntitiesByChatId(String chatId);

    ChatHistory getChatHistoryEntitiesByTitle(String title);
}
