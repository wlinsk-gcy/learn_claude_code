package com.wlinsk.learn_claude_code.provider;

import com.openai.models.chat.completions.*;
import com.wlinsk.learn_claude_code.models.LlmMessage;

import java.util.List;

/**
 * @author Trump
 * @create 2026/3/31 15:09
 */
public interface LlmProvider {

    ChatCompletion generate(List<LlmMessage> messages);

    void streamGenerate(List<LlmMessage> messages);

    default ChatCompletionMessageParam toMessageParam(LlmMessage message) {
        return switch (message.getRole()) {
            case "system" -> ChatCompletionMessageParam.ofSystem(
                    ChatCompletionSystemMessageParam.builder()
                            .content(ChatCompletionSystemMessageParam.Content.ofText(message.getContent()))
                            .build()
            );
            case "user" -> ChatCompletionMessageParam.ofUser(
                    ChatCompletionUserMessageParam.builder()
                            .content(ChatCompletionUserMessageParam.Content.ofText(message.getContent()))
                            .build()
            );
            case "assistant" -> ChatCompletionMessageParam.ofAssistant(
                    toAssistantMessageParam(message)
            );
            case "tool" -> ChatCompletionMessageParam.ofTool(
                    ChatCompletionToolMessageParam.builder()
                            .content(ChatCompletionToolMessageParam.Content.ofText(message.getContent()))
                            .toolCallId(requiredToolCallId(message))
                            .build()
            );
            default -> throw new IllegalArgumentException("Unsupported role: " + message.getRole());
        };
    }

    private ChatCompletionAssistantMessageParam toAssistantMessageParam(LlmMessage message) {
        ChatCompletionAssistantMessageParam.Builder builder = ChatCompletionAssistantMessageParam.builder();
        if (message.getContent() != null) {
            builder.content(message.getContent());
        }
        if (message.getToolCalls() != null && !message.getToolCalls().isEmpty()) {
            builder.toolCalls(message.getToolCalls());
        }
        if (message.getContent() == null
                && (message.getToolCalls() == null || message.getToolCalls().isEmpty())) {
            throw new IllegalArgumentException("Assistant message must include content or toolCalls");
        }
        return builder.build();
    }

    private String requiredToolCallId(LlmMessage message) {
        if (message.getToolCallId() == null || message.getToolCallId().isBlank()) {
            throw new IllegalArgumentException("Tool message must include toolCallId");
        }
        return message.getToolCallId();
    }


}
