package com.wlinsk.learn_claude_code.models.tools;

import com.wlinsk.learn_claude_code.agent.tools.basic.ToolExecutor;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Cached tool metadata plus execution adapter.
 *
 * @author wlinsk
 * @date 2026/4/4
 */
public record RegisteredTool(
        String name,
        String description,
        Type argsType,
        Map<String, Object> inputSchema,
        ToolExecutor executor
) {
}
