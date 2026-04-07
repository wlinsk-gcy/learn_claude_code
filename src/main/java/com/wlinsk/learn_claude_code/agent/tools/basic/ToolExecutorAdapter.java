package com.wlinsk.learn_claude_code.agent.tools.basic;

import com.wlinsk.learn_claude_code.utils.JsonUtil;

import java.lang.reflect.Type;

/**
 * @author wlinsk
 * @date 2026/4/4
 */
public class ToolExecutorAdapter<TArgs, TResult> implements ToolExecutor {

    private final Tool<TArgs, TResult> delegate;

    public ToolExecutorAdapter(Tool<TArgs, TResult> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Type argsType() {
        return delegate.argsType();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object execute(String rawArgumentsJson, ToolContext context) throws Exception {
        TArgs args = (TArgs) JsonUtil.fromJson(rawArgumentsJson, delegate.argsType());
        return delegate.execute(args, context);
    }
}
