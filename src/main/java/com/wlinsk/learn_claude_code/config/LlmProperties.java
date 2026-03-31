package com.wlinsk.learn_claude_code.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Trump
 * @create 2026/3/31 14:50
 */
@Data
@ConfigurationProperties(prefix = "llm")
public class LlmProperties {
    private String apiKey;
    private String baseUrl;
    private String model;
}
