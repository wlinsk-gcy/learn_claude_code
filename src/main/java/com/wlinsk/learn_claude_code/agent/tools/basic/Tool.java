package com.wlinsk.learn_claude_code.agent.tools.basic;

import java.lang.reflect.Type;

/**
 * @author wlinsk
 * @date 2026/3/31
 */
public interface Tool<TArgs, TResult> {
    Type argsType();

    TResult execute(TArgs args, ToolContext context) throws Exception;
}
