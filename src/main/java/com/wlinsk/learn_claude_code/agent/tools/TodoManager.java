package com.wlinsk.learn_claude_code.agent.tools;

import com.wlinsk.learn_claude_code.agent.tools.basic.Tool;
import com.wlinsk.learn_claude_code.agent.tools.basic.ToolContext;
import com.wlinsk.learn_claude_code.agent.tools.basic.annotation.AgentTool;
import com.wlinsk.learn_claude_code.enums.TodoManagerEnum;
import com.wlinsk.learn_claude_code.models.tools.TodoWriterArgs;
import com.wlinsk.learn_claude_code.models.tools.TodoWriterSubArgs;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wlinsk
 * @date 2026/4/6
 */
@Component
@AgentTool(name = "todo", description = "Update task list. Track progress on multi-step tasks.")
public class TodoManager implements Tool<TodoWriterArgs, String> {

    @Override
    public Type argsType() {
        return TodoWriterArgs.class;
    }

    @Override
    public String execute(TodoWriterArgs args, ToolContext context) {
        List<TodoWriterSubArgs> items = validate(args);
        context.runState().replaceTodoItems(items);
        return render(items);
    }

    private List<TodoWriterSubArgs> validate(TodoWriterArgs args) {
        if (args == null || args.items() == null || args.items().isEmpty()) {
            throw new IllegalArgumentException("todo.items must not be empty");
        }

        int inProgressCount = 0;
        Set<String> ids = new HashSet<>();
        for (TodoWriterSubArgs item : args.items()) {
            if (item == null) {
                throw new IllegalArgumentException("todo.items must not contain null entries");
            }
            if (item.id() == null || item.id().isBlank()) {
                throw new IllegalArgumentException("todo item id is required");
            }
            if (item.text() == null || item.text().isBlank()) {
                throw new IllegalArgumentException("todo item text is required");
            }
            if (item.status() == null) {
                throw new IllegalArgumentException("todo item status is required");
            }
            if (!ids.add(item.id())) {
                throw new IllegalArgumentException("todo item ids must be unique");
            }
            if (TodoManagerEnum.IN_PROGRESS.equals(item.status())) {
                inProgressCount++;
            }
        }

        if (inProgressCount > 1) {
            throw new IllegalArgumentException("todo must have at most one IN_PROGRESS item");
        }

        return List.copyOf(args.items());
    }

    private String render(List<TodoWriterSubArgs> items) {
        long doneCount = items.stream()
                .filter(item -> TodoManagerEnum.DONE.equals(item.status()))
                .count();

        return "Todo list updated (" + doneCount + "/" + items.size() + " done)\n"
                + items.stream()
                .map(item -> "- [" + item.status().name().toLowerCase(Locale.ROOT) + "] "
                        + item.id() + ": " + item.text())
                .collect(Collectors.joining("\n"));
    }
}
