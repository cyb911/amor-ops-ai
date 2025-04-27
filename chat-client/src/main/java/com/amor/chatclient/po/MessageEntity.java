package com.amor.chatclient.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_memory")
@Data
public class MessageEntity {
    @Id
    private String id;

    private String conversationId;

    private String role; // user / assistant / system
    private String text;
}
