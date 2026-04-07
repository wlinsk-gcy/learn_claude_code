package com.wlinsk.learn_claude_code.models.tools;

/**
 * @author wlinsk
 * @date 2026/4/7
 */
public record ShellCommandResult(
        String shell,
        String command,
        String workingDirectory,
        int exitCode,
        String stdout,
        String stderr
) {
}
