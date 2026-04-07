package com.wlinsk.learn_claude_code.agent.tools.basic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes a single tool argument field for schema generation.
 *
 * @author wlinsk
 * @date 2026/4/4
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToolParam {
    String description() default "";

    boolean required() default false;
}
