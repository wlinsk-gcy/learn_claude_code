package com.wlinsk.learn_claude_code.agent.tools.shell;

import com.wlinsk.learn_claude_code.agent.tools.basic.annotation.AgentTool;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wlinsk
 * @date 2026/4/7
 */
@Component
@Conditional(UnixLikeCondition.class)
@AgentTool(name = "shell", description = "Execute a command in the current platform shell.")
public class BashTool extends AbstractShellTool {
    @Override
    protected List<String> buildCommand(String command) {
        return List.of("bash", "-lc", command);
    }

    @Override
    protected String shellName() {
        return "bash";
    }
}
