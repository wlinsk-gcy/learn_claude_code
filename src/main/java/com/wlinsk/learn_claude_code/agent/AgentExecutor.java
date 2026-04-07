package com.wlinsk.learn_claude_code.agent;

import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionMessageFunctionToolCall;
import com.openai.models.chat.completions.ChatCompletionMessageToolCall;
import com.wlinsk.learn_claude_code.agent.runtime.AgentRunState;
import com.wlinsk.learn_claude_code.agent.tools.basic.ToolContext;
import com.wlinsk.learn_claude_code.agent.tools.basic.ToolRegistry;
import com.wlinsk.learn_claude_code.models.LlmMessage;
import com.wlinsk.learn_claude_code.models.tools.RegisteredTool;
import com.wlinsk.learn_claude_code.provider.LlmProvider;
import com.wlinsk.learn_claude_code.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wlinsk
 * @date 2026/3/31
 */
@Slf4j
@Service
public class AgentExecutor {
    private static final String DEFAULT_SYSTEM_PROMPT = """
            You are a tool-using assistant.
            Use the todo tool for multi-step or non-trivial tasks.
            Keep at most one todo item marked IN_PROGRESS at a time.
            Update the todo list when you start work, complete work, or change plan.
            Skip todo only for simple one-step requests.
            """;
    private static final String TODO_REMINDER_PROMPT = """
            Reminder: update your todo list before continuing.
            Keep at most one item IN_PROGRESS.
            """;

    @Autowired
    private LlmProvider llmProvider;
    @Autowired
    private ToolRegistry toolRegistry;

    public void agentLoop(String query){
        AgentRunState runState = new AgentRunState();
        ToolContext toolContext = new ToolContext(runState);
        List<LlmMessage> messages = new ArrayList<>();
        messages.add(new LlmMessage("system", DEFAULT_SYSTEM_PROMPT));
        messages.add(new LlmMessage("user", query));
        while (true) {
            ChatCompletion response = llmProvider.generate(buildRequestMessages(messages, runState));
            ChatCompletion.Choice choice = response.choices().get(0);
            if (!ChatCompletion.Choice.FinishReason.TOOL_CALLS.equals(choice.finishReason())) {
                String assistantContent = choice.message().content()
                        .orElseThrow(() -> new IllegalStateException("Assistant response missing content"));
                messages.add(new LlmMessage("assistant", assistantContent));
                log.info("llm response (full text): {}", assistantContent);
                return;
            }
            List<ChatCompletionMessageToolCall> toolCalls = choice.message().toolCalls()
                    .orElseThrow(() -> new IllegalStateException("Tool call finish reason without tool calls"));
            messages.add(LlmMessage.assistantWithToolCalls(choice.message().content().orElse(null), toolCalls));
            boolean todoUpdatedThisTurn = false;
            for (ChatCompletionMessageToolCall toolCall : toolCalls) {
                ChatCompletionMessageFunctionToolCall call = toolCall.function()
                        .orElseThrow(() -> new IllegalStateException("Unsupported non-function tool call"));
                try {
                    RegisteredTool registeredTool = toolRegistry.get(call.function().name());
                    Object toolResult = registeredTool.executor().execute(call.function().arguments(), toolContext);
                    messages.add(new LlmMessage("tool", JsonUtil.toJson(toolResult), call.id()));
                    if ("todo".equals(call.function().name())) {
                        todoUpdatedThisTurn = true;
                    }
                } catch (Exception e) {
                    log.error("call tool error: ", e);
                    throw new RuntimeException(e);
                }
            }
            if (todoUpdatedThisTurn) {
                runState.markTodoUpdated();
            } else {
                runState.markTurnWithoutTodoUpdate();
            }
        }
    }

    private List<LlmMessage> buildRequestMessages(List<LlmMessage> messages, AgentRunState runState) {
        List<LlmMessage> requestMessages = new ArrayList<>(messages);
        if (runState.shouldRemindTodoUpdate()) {
            requestMessages.add(new LlmMessage("system", TODO_REMINDER_PROMPT));
        }
        return requestMessages;
    }
}
