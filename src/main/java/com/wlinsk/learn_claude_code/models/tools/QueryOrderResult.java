package com.wlinsk.learn_claude_code.models.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author wlinsk
 * @date 2026/3/31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryOrderResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -4012819542382952706L;

    public String orderId;
    public String status;
    public Double amount;

}
