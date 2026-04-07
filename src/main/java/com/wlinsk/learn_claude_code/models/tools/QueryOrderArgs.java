package com.wlinsk.learn_claude_code.models.tools;

import com.wlinsk.learn_claude_code.agent.tools.basic.annotation.ToolParam;

/**
 * @author wlinsk
 * @date 2026/3/31
 */
public class QueryOrderArgs {
    @ToolParam(description = "订单号", required = true)
    public String orderId;
}
