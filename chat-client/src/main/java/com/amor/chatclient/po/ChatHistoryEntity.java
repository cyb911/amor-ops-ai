package com.amor.chatclient.po;

import lombok.Data;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "chat_history")
public class ChatHistoryEntity {
    @Id
    private String chatId;

    private String title;

    private long createTimestamp;

    private long updateTimestamp;

    private String systemPrompt;

    private ChatOptions chatOptions;
}
