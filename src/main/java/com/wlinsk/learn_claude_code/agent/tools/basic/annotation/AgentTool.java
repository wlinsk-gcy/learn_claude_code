package com.wlinsk.learn_claude_code.agent.tools.basic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares tool metadata exposed to the LLM provider.
 *
 * @author wlinsk
 * @date 2026/4/4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AgentTool {
    String name();

    String description();
}
