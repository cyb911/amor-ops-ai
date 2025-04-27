package com.amor.chatclient.utils;

import com.amor.chatclient.po.MessageEntity;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

public class MessageConverter {
    public static MessageEntity toEntity(String conversationId, Message message) {
        MessageEntity entity = new MessageEntity();
        entity.setConversationId(conversationId);
        entity.setText(message.getText());
        entity.setRole(message.getMessageType().name().toLowerCase());
        return entity;
    }

    public static Message toMessage(MessageEntity entity) {
        switch (entity.getRole()) {
            case "user":
                return new UserMessage(entity.getText());
            case "assistant":
                return new AssistantMessage(entity.getText());
            default:
                throw new IllegalArgumentException("Unsupported role: " + entity.getRole());
        }
    }
}
