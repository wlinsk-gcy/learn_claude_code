package com.wlinsk.learn_claude_code.agent.tools.basic;

import com.wlinsk.learn_claude_code.agent.tools.basic.annotation.AgentTool;
import com.wlinsk.learn_claude_code.agent.tools.basic.schema.InputSchemaGenerator;
import com.wlinsk.learn_claude_code.models.tools.RegisteredTool;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wlinsk
 * @date 2026/3/31
 */
@Component
public class ToolRegistry {

    private final Map<String, RegisteredTool> tools = new HashMap<>();

    public ToolRegistry(List<Tool<?, ?>> toolList, InputSchemaGenerator inputSchemaGenerator) {
        for (Tool<?, ?> tool : toolList) {
            ToolExecutorAdapter<?, ?> executor = new ToolExecutorAdapter<>(tool);
            AgentTool metadata = tool.getClass().getAnnotation(AgentTool.class);
            if (metadata == null) {
                throw new IllegalStateException("Missing @AgentTool on " + tool.getClass().getName());
            }

            RegisteredTool registeredTool = new RegisteredTool(
                    metadata.name(),
                    metadata.description(),
                    executor.argsType(),
                    inputSchemaGenerator.generate(executor.argsType()),
                    executor
            );

            RegisteredTool previous = tools.putIfAbsent(registeredTool.name(), registeredTool);
            if (previous != null) {
                throw new IllegalStateException("Duplicate tool name: " + registeredTool.name());
            }
        }
    }

    public RegisteredTool get(String name) {
        RegisteredTool registeredTool = tools.get(name);
        if (registeredTool == null) {
            throw new IllegalArgumentException("Unknown tool: " + name);
        }
        return registeredTool;
    }

    public List<RegisteredTool> all() {
        return tools.values().stream().toList();
    }
}
