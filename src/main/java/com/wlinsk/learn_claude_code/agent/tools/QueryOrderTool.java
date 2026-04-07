package com.wlinsk.learn_claude_code.agent.tools;

import com.wlinsk.learn_claude_code.agent.tools.basic.Tool;
import com.wlinsk.learn_claude_code.agent.tools.basic.ToolContext;
import com.wlinsk.learn_claude_code.agent.tools.basic.annotation.AgentTool;
import com.wlinsk.learn_claude_code.models.tools.QueryOrderArgs;
import com.wlinsk.learn_claude_code.models.tools.QueryOrderResult;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

/**
 * @author wlinsk
 * @date 2026/3/31
 */
@Component
@AgentTool(name = "query_order", description = "查询订单")
public class QueryOrderTool implements Tool<QueryOrderArgs, QueryOrderResult> {

    @Override
    public Type argsType() {
        return QueryOrderArgs.class;
    }

    @Override
    public QueryOrderResult execute(QueryOrderArgs args, ToolContext context) {
        return new QueryOrderResult(args.orderId, "PAID", 99.0);
    }
}
