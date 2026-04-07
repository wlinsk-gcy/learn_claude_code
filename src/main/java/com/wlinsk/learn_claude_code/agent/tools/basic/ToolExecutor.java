package com.wlinsk.learn_claude_code.agent.tools.basic;

import java.lang.reflect.Type;

/**
 * @author wlinsk
 * @date 2026/4/4
 */
public interface ToolExecutor {
    Type argsType();

    Object execute(String rawArgumentsJson, ToolContext context) throws Exception;
}
