package com.wlinsk.learn_claude_code.agent.tools.shell;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Locale;

/**
 * @author wlinsk
 * @date 2026/4/7
 */
public class UnixLikeCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        return osName.contains("nix")
                || osName.contains("nux")
                || osName.contains("mac")
                || osName.contains("darwin");
    }
}
