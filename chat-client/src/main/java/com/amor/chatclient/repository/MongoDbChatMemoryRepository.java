package com.amor.chatclient.repository;

import com.amor.chatclient.po.MessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoDbChatMemoryRepository extends MongoRepository<MessageEntity,String> {
    List<MessageEntity> findByConversationId(String conversationId);

    void deleteByConversationId(String conversationId);
}
