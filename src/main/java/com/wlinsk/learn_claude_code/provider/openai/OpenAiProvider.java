package com.wlinsk.learn_claude_code.provider.openai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonValue;
import com.openai.models.FunctionDefinition;
import com.openai.models.FunctionParameters;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionFunctionTool;
import com.openai.models.chat.completions.ChatCompletionTool;
import com.wlinsk.learn_claude_code.models.tools.RegisteredTool;
import com.wlinsk.learn_claude_code.agent.tools.basic.ToolRegistry;
import com.wlinsk.learn_claude_code.config.LlmProperties;
import com.wlinsk.learn_claude_code.models.LlmMessage;
import com.wlinsk.learn_claude_code.provider.LlmProvider;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Trump
 * @create 2026/3/31 15:15
 */
@Component
public class OpenAiProvider implements LlmProvider {

    private final LlmProperties llmProperties;
    private final OpenAIClient openAIClient;
    private final ToolRegistry toolRegistry;

    public OpenAiProvider(LlmProperties llmProperties, ToolRegistry toolRegistry) {
        this.llmProperties = llmProperties;
        this.toolRegistry = toolRegistry;
        this.openAIClient = OpenAIOkHttpClient.builder()
                .apiKey(llmProperties.getApiKey())
                .baseUrl(llmProperties.getBaseUrl())
                .timeout(Duration.ofMinutes(2))
                .build();
    }

    @Override
    public ChatCompletion generate(List<LlmMessage> messages) {
        ChatCompletionCreateParams params = buildParams(messages);
        ChatCompletion response = this.openAIClient.chat().completions().create(params);
        if (response.choices().isEmpty()) {
            throw new IllegalStateException("LLM returned no choices");
        }
        return response;
//        ChatCompletion.Choice choice = response.choices().get(0);
//        if (ChatCompletion.Choice.FinishReason.TOOL_CALLS.equals(choice.finishReason())) {
//
//        }
//        ChatCompletionMessage message = choice.message();
//
//        return message.content().orElse(null);
    }

    @Override
    public void streamGenerate(List<LlmMessage> messages) {

    }

    private ChatCompletionCreateParams buildParams(List<LlmMessage> messages) {
        ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder()
                .model(llmProperties.getModel());
        messages.forEach(message -> builder.addMessage(toMessageParam(message)));
        toOpenAiTools(toolRegistry.all()).forEach(builder::addTool);
        return builder.build();
    }

    private List<ChatCompletionTool> toOpenAiTools(Collection<RegisteredTool> tools) {
        return tools.stream()
                .map(tool -> ChatCompletionTool.ofFunction(
                        ChatCompletionFunctionTool.builder().function(
                                        FunctionDefinition.builder()
                                                .name(tool.name())
                                                .description(tool.description())
                                                .parameters(toFunctionParameters(tool.inputSchema()))
                                                .build()).
                                build()))
                .toList();
    }

    private FunctionParameters toFunctionParameters(Map<String, Object> inputSchema) {
        Map<String, JsonValue> properties = new LinkedHashMap<>();
        inputSchema.forEach((key, value) -> properties.put(key, JsonValue.from(value)));
        return FunctionParameters.builder()
                .additionalProperties(properties)
                .build();
    }
}
