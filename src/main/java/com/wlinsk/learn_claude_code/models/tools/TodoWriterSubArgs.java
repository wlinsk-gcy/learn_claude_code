package com.wlinsk.learn_claude_code.models.tools;

import com.wlinsk.learn_claude_code.agent.tools.basic.annotation.ToolParam;
import com.wlinsk.learn_claude_code.enums.TodoManagerEnum;

/**
 * @author wlinsk
 * @date 2026/4/6
 */
public class TodoWriterSubArgs {
    @ToolParam(required = true)
    private String id;
    @ToolParam(required = true)
    private String text;
    @ToolParam(required = true)
    private TodoManagerEnum status;

    public TodoWriterSubArgs() {
    }

    public TodoWriterSubArgs(String id, String text, TodoManagerEnum status) {
        this.id = id;
        this.text = text;
        this.status = status;
    }

    public String id() {
        return id;
    }

    public String text() {
        return text;
    }

    public TodoManagerEnum status() {
        return status;
    }
}
