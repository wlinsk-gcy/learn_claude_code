package com.wlinsk.learn_claude_code.models.tools;

import com.wlinsk.learn_claude_code.agent.tools.basic.annotation.ToolParam;
import lombok.Data;

/**
 * @author wlinsk
 * @date 2026/4/7
 */
@Data
public class ShellCommandArgs {
    @ToolParam(description = "Command to run in the platform shell.", required = true)
    private String command;

    @ToolParam(description = "Optional working directory for the command.")
    private String workingDirectory;

    @ToolParam(description = "Optional timeout in seconds. Defaults to 30 and is capped at 120.")
    private Integer timeoutSeconds;

    public String command() {
        return command;
    }

    public String workingDirectory() {
        return workingDirectory;
    }

    public Integer timeoutSeconds() {
        return timeoutSeconds;
    }
}
