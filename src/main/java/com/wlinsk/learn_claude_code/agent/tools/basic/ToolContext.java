package com.wlinsk.learn_claude_code.agent.tools.basic;

import com.wlinsk.learn_claude_code.agent.runtime.AgentRunState;

/**
 * Runtime state shared across tool calls in a single agent loop.
 *
 * @author wlinsk
 * @date 2026/4/6
 */
public record ToolContext(AgentRunState runState) {
}
