package com.wlinsk.learn_claude_code.models.tools;

import com.wlinsk.learn_claude_code.agent.tools.basic.annotation.ToolParam;

import java.util.List;

/**
 * @author wlinsk
 * @date 2026/4/6
 */
public class TodoWriterArgs {
    @ToolParam(required = true)
    private List<TodoWriterSubArgs> items;

    public TodoWriterArgs() {
    }

    public TodoWriterArgs(List<TodoWriterSubArgs> items) {
        this.items = items;
    }

    public List<TodoWriterSubArgs> items() {
        return items;
    }
}
