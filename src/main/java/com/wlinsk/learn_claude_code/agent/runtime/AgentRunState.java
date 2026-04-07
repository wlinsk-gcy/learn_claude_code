package com.wlinsk.learn_claude_code.agent.runtime;

import com.wlinsk.learn_claude_code.models.tools.TodoWriterSubArgs;

import java.util.List;

/**
 * Mutable state scoped to a single agent run.
 *
 * @author wlinsk
 * @date 2026/4/6
 */
public class AgentRunState {
    private List<TodoWriterSubArgs> todoItems = List.of();
    private int turnsWithoutTodoUpdate;

    public List<TodoWriterSubArgs> todoItems() {
        return todoItems;
    }

    public void replaceTodoItems(List<TodoWriterSubArgs> todoItems) {
        this.todoItems = List.copyOf(todoItems);
    }

    public boolean hasTodoItems() {
        return !todoItems.isEmpty();
    }

    public void markTodoUpdated() {
        turnsWithoutTodoUpdate = 0;
    }

    public void markTurnWithoutTodoUpdate() {
        if (hasTodoItems()) {
            turnsWithoutTodoUpdate++;
        }
    }

    public boolean shouldRemindTodoUpdate() {
        return hasTodoItems() && turnsWithoutTodoUpdate >= 3;
    }
}
