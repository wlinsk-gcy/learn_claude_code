package com.wlinsk.learn_claude_code.agent.tools.shell;

import com.wlinsk.learn_claude_code.agent.tools.basic.Tool;
import com.wlinsk.learn_claude_code.agent.tools.basic.ToolContext;
import com.wlinsk.learn_claude_code.models.tools.ShellCommandArgs;
import com.wlinsk.learn_claude_code.models.tools.ShellCommandResult;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author wlinsk
 * @date 2026/4/7
 */
public abstract class AbstractShellTool implements Tool<ShellCommandArgs, ShellCommandResult> {
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private static final int MAX_TIMEOUT_SECONDS = 120;

    @Override
    public Type argsType() {
        return ShellCommandArgs.class;
    }

    @Override
    public ShellCommandResult execute(ShellCommandArgs args, ToolContext context) throws Exception {
        if (args == null || args.command() == null || args.command().isBlank()) {
            throw new IllegalArgumentException("shell.command must not be blank");
        }

        Path workingDirectory = resolveWorkingDirectory(args.workingDirectory());
        int timeoutSeconds = resolveTimeoutSeconds(args.timeoutSeconds());

        ProcessBuilder processBuilder = new ProcessBuilder(buildCommand(args.command()));
        processBuilder.directory(workingDirectory.toFile());

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Process process = processBuilder.start();
            Future<String> stdoutFuture = executor.submit(() -> read(process.getInputStream()));
            Future<String> stderrFuture = executor.submit(() -> read(process.getErrorStream()));

            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new IllegalStateException("shell command timed out after " + timeoutSeconds + " seconds");
            }

            return new ShellCommandResult(
                    shellName(),
                    args.command(),
                    workingDirectory.toAbsolutePath().normalize().toString(),
                    process.exitValue(),
                    stdoutFuture.get(),
                    stderrFuture.get()
            );
        }
    }

    protected abstract List<String> buildCommand(String command);

    protected abstract String shellName();

    private Path resolveWorkingDirectory(String workingDirectory) {
        Path path = workingDirectory == null || workingDirectory.isBlank()
                ? Path.of("").toAbsolutePath()
                : Path.of(workingDirectory).toAbsolutePath().normalize();

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("working directory does not exist: " + path);
        }
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("working directory is not a directory: " + path);
        }
        return path;
    }

    private int resolveTimeoutSeconds(Integer timeoutSeconds) {
        if (timeoutSeconds == null) {
            return DEFAULT_TIMEOUT_SECONDS;
        }
        if (timeoutSeconds < 1) {
            throw new IllegalArgumentException("timeoutSeconds must be greater than 0");
        }
        return Math.min(timeoutSeconds, MAX_TIMEOUT_SECONDS);
    }


    private String read(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
