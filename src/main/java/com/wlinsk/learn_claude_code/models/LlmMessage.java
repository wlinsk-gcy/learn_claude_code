package com.wlinsk.learn_claude_code.models;

import com.openai.models.chat.completions.ChatCompletionMessageToolCall;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * @author wlinsk
 * @date 2026/3/31
 */
@Data
public class LlmMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = -4638240140717374899L;

    private String role;
    private String content;
    private String toolCallId;
    private List<ChatCompletionMessageToolCall> toolCalls;

    public LlmMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public LlmMessage(String role, String content, String toolCallId) {
        this.role = role;
        this.content = content;
        this.toolCallId = toolCallId;
    }

    public static LlmMessage assistantWithToolCalls(List<ChatCompletionMessageToolCall> toolCalls) {
        return assistantWithToolCalls(null, toolCalls);
    }

    public static LlmMessage assistantWithToolCalls(String content, List<ChatCompletionMessageToolCall> toolCalls) {
        LlmMessage message = new LlmMessage("assistant", content);
        message.setToolCalls(List.copyOf(toolCalls));
        return message;
    }
}

