package com.amor.chatclient.controller;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class McpController {

    private final ChatClient chatClient;

    private final String prompts = "你是一位某系统助理，你需要遵循一下规则：" +
            "1:当用户需要获取系统验证码时，调用获取系统验证码tool时，你的请求tool参数应该时应该是标准的UUID.";

    @GetMapping("/ai/tool/generate")
    public String toolGenerate(@RequestParam(value = "message") String message) {
        return chatClient.prompt(prompts)
                .user(message)
                .call()
                .content();
    }
}
