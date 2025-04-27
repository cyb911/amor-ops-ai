package com.amor.chatclient;

import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class ChatClientApplicationTests {

    @Autowired
    private OpenAiEmbeddingModel openAiEmbeddingModel;

    @Test
    void contextLoads() {
        float[] floats = openAiEmbeddingModel.embed("项目");
        System.err.println(Arrays.toString(floats));
    }

}
