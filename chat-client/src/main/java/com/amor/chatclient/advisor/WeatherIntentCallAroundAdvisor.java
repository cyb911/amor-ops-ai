package com.amor.chatclient.advisor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WeatherIntentCallAroundAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    private final ChatClient intentChatClient;

    public WeatherIntentCallAroundAdvisor(ChatClient intentChatClient) {
        this.intentChatClient = intentChatClient;
    }


    private AdvisedRequest before(AdvisedRequest request) {
        List<Message> messages = request.messages();

        String currentUserInput = request.userText();

        // 构造历史对话（去掉当前用户输入）
        String history = messages.stream()
                .limit(Math.max(0, 5))
                .map(m -> (m.getMessageType() == MessageType.USER ? "用户：" : "助手：") + m.getText())
                .collect(Collectors.joining("\n"));

        String promptText = """
            请根据“当前用户输入”判断是否为查询天气的问题，只回答 true 或 false。
            
            历史对话（供参考）：
            %s
            当前用户输入：
            %s
            """.formatted(history, currentUserInput);
        // 调用意图识别 LLM
        String result = Objects.requireNonNull(intentChatClient.prompt(new Prompt(promptText)).call().chatResponse()).getResult().getOutput().getText();

        boolean isWeatherQuery = result.contains("true");

        // 加入一个系统提示，影响主模型行为
        String systemTip = isWeatherQuery
                ? "当前用户正在查询天气相关信息，可以使用“城市搜索、位置信息搜索（查询天气时使用）”工具。"
                : "当前用户没有提出天气相关问题，请勿调用“城市搜索、位置信息搜索（查询天气时使用）”工具。";

        request.messages().addFirst(new UserMessage(systemTip));
        return request;
    }


    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = before(advisedRequest);
        return chain.nextAroundCall(advisedRequest);
    }

    @Override
    public String getName() {
        return "weatherIntent";
    }

    @Override
    public int getOrder() {
        return 9999;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest = before(advisedRequest);
        return chain.nextAroundStream(advisedRequest);
    }
}
