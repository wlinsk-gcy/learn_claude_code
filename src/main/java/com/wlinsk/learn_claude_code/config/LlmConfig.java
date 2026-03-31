package com.wlinsk.learn_claude_code.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Trump
 * @create 2026/3/31 14:55
 */
@Configuration
@EnableConfigurationProperties({LlmProperties.class})
public class LlmConfig {
}
