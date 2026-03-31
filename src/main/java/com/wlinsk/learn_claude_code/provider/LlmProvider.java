package com.wlinsk.learn_claude_code.provider;

/**
 * @author Trump
 * @create 2026/3/31 15:09
 */
public interface LlmProvider {
    void generate();
    void streamGenerate();
}
